package com.surajvanshsv.quoteapps.utils;

import android.opengl.GLES20;
import android.util.Log;

public class SimpleShader {

    private static final String TAG = "SimpleShader";

    public static final String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTexCoord;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = aPosition;\n" +
                    "  vTexCoord = aTexCoord;\n" +
                    "}";

    public static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(uTexture, vTexCoord);\n" +
                    "}";

    public static int compileShader(int type, String code) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            throw new RuntimeException("Could not create shader of type " + type);
        }

        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            String log = GLES20.glGetShaderInfoLog(shader);
            Log.e(TAG, "Shader compile error: " + log);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Shader compile failed");
        }

        return shader;
    }

    public static int buildProgram() {
        int vs = compileShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fs = compileShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        int program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Could not create program");
        }

        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);
        GLES20.glLinkProgram(program);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == 0) {
            String log = GLES20.glGetProgramInfoLog(program);
            Log.e(TAG, "Program link error: " + log);
            GLES20.glDeleteProgram(program);
            throw new RuntimeException("Program link failed");
        }

        // shaders can be detached and deleted after linking
        GLES20.glDetachShader(program, vs);
        GLES20.glDetachShader(program, fs);
        GLES20.glDeleteShader(vs);
        GLES20.glDeleteShader(fs);

        return program;
    }
}
