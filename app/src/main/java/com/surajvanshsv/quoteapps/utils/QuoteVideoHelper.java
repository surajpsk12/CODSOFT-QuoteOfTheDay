package com.surajvanshsv.quoteapps.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class QuoteVideoHelper {

    private static final String TAG = "QuoteVideoHelper";

    public static void createQuoteReel(Context context, com.surajvanshsv.quoteapps.model.Quote quote) {
        int width = 1080;
        int height = 1920;
        int frameRate = 30;
        int bitRate = 2 * 1024 * 1024;
        int durationSec = 6;  // 6 seconds

        File outputFile = new File(context.getExternalFilesDir(null), "quote_reel.mp4");

        try {
            MediaMuxer muxer = new MediaMuxer(outputFile.getAbsolutePath(),
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            MediaFormat format = MediaFormat.createVideoFormat("video/avc", width, height);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            MediaCodec codec = MediaCodec.createEncoderByType("video/avc");
            codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            Surface inputSurface = codec.createInputSurface();
            codec.start();

            // EGL
            EGLDisplay eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            EGL14.eglInitialize(eglDisplay, null, 0, null, 0);

            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            int[] configAttribs = {
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_NONE
            };
            EGL14.eglChooseConfig(eglDisplay, configAttribs, 0, configs, 0, 1, numConfigs, 0);

            int[] contextAttribs = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            EGLContext eglContext = EGL14.eglCreateContext(eglDisplay, configs[0],
                    EGL14.EGL_NO_CONTEXT, contextAttribs, 0);
            EGLSurface eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, configs[0], inputSurface, null, 0);
            EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);

            // Bitmap
            Bitmap quoteBitmap = QuoteImageHelper.createQuoteShareImage(context, quote);

            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, quoteBitmap, 0);

            float[] quadVertices = {
                    -1f, -1f, 0f, 1f,
                    1f, -1f, 1f, 1f,
                    -1f,  1f, 0f, 0f,
                    1f,  1f, 1f, 0f
            };
            FloatBuffer quadBuffer = ByteBuffer
                    .allocateDirect(quadVertices.length * 4)
                    .order(java.nio.ByteOrder.nativeOrder())
                    .asFloatBuffer();
            quadBuffer.put(quadVertices).position(0);

            int program = SimpleShader.buildProgram();
            GLES20.glUseProgram(program);

            int aPosition = GLES20.glGetAttribLocation(program, "aPosition");
            int aTexCoord = GLES20.glGetAttribLocation(program, "aTexCoord");
            int uTexture  = GLES20.glGetUniformLocation(program, "uTexture");

            GLES20.glEnableVertexAttribArray(aPosition);
            GLES20.glEnableVertexAttribArray(aTexCoord);

            GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 4 * 4, quadBuffer);
            quadBuffer.position(2);
            GLES20.glVertexAttribPointer(aTexCoord, 2, GLES20.GL_FLOAT, false, 4 * 4, quadBuffer);
            quadBuffer.position(0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glUniform1i(uTexture, 0);

            int totalFrames = frameRate * durationSec;
            boolean muxerStarted = false;
            int trackIndex = -1;
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            for (int frame = 0; frame < totalFrames; frame++) {
                GLES20.glClearColor(0f, 0f, 0f, 1f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

                long presentationTimeUs = frame * 1_000_000L / frameRate;
                EGLExt.eglPresentationTimeANDROID(eglDisplay, eglSurface, presentationTimeUs);

                EGL14.eglSwapBuffers(eglDisplay, eglSurface);

                // drain encoder in parallel while drawing
                while (true) {
                    int outputIndex = codec.dequeueOutputBuffer(bufferInfo, 0);
                    if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        break;
                    } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newFormat = codec.getOutputFormat();
                        trackIndex = muxer.addTrack(newFormat);
                        muxer.start();
                        muxerStarted = true;
                    } else if (outputIndex >= 0) {
                        if (muxerStarted) {
                            ByteBuffer encodedData = codec.getOutputBuffer(outputIndex);
                            if (encodedData != null && bufferInfo.size > 0) {
                                encodedData.position(bufferInfo.offset);
                                encodedData.limit(bufferInfo.offset + bufferInfo.size);
                                muxer.writeSampleData(trackIndex, encodedData, bufferInfo);
                            }
                        }
                        codec.releaseOutputBuffer(outputIndex, false);

                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            break;
                        }
                    }
                }
            }

            // finalize
            codec.signalEndOfInputStream();

            // drain remaining
            while (true) {
                int outputIndex = codec.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    break;
                } else if (outputIndex >= 0) {
                    if (muxerStarted) {
                        ByteBuffer encodedData = codec.getOutputBuffer(outputIndex);
                        if (encodedData != null && bufferInfo.size > 0) {
                            encodedData.position(bufferInfo.offset);
                            encodedData.limit(bufferInfo.offset + bufferInfo.size);
                            muxer.writeSampleData(trackIndex, encodedData, bufferInfo);
                        }
                    }
                    codec.releaseOutputBuffer(outputIndex, false);

                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        break;
                    }
                }
            }

            codec.stop();
            codec.release();
            muxer.stop();
            muxer.release();

            GLES20.glDeleteTextures(1, textures, 0);
            quoteBitmap.recycle();
            EGL14.eglDestroySurface(eglDisplay, eglSurface);
            EGL14.eglDestroyContext(eglDisplay, eglContext);
            EGL14.eglTerminate(eglDisplay);

            Log.d(TAG, "Quote reel created successfully");

        } catch (IOException e) {
            Log.e(TAG, "Error creating video: " + e.getMessage(), e);
        }
    }
}
