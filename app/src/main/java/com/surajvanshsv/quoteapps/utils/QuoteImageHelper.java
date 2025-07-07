package com.surajvanshsv.quoteapps.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.surajvanshsv.quoteapps.model.Quote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuoteImageHelper {

    public static Bitmap createQuoteShareImage(Context context, Quote quote) {
        int width = 1080;
        int height = 1920;


        Bitmap background = null;
        boolean isDark = true; // fallback

        try {
            // pick random background
            String[] backgrounds = context.getAssets().list("backgrounds");
            if (backgrounds != null && backgrounds.length > 0) {
                int randomIndex = (int) (Math.random() * backgrounds.length);
                String randomBackground = backgrounds[randomIndex];

                background = BitmapFactory.decodeStream(
                        context.getAssets().open("backgrounds/" + randomBackground)
                );

                // determine if it is a "light_" background
                if (randomBackground.startsWith("light_")) {
                    isDark = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (background == null) {
            background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(background);
            tempCanvas.drawColor(Color.parseColor("#001F54"));
        }

        Bitmap scaledBackground = Bitmap.createScaledBitmap(background, width, height, true);
        Bitmap bitmap = scaledBackground.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        // colors depending on theme
        int quoteColor = isDark ? Color.WHITE : Color.BLACK;
        int authorColor = isDark ? Color.LTGRAY : Color.DKGRAY;
        int vanshColor = isDark ? Color.LTGRAY : Color.DKGRAY;
        int titleColor = isDark ? Color.LTGRAY : Color.DKGRAY;
        int handleColor = isDark ? Color.LTGRAY : Color.DKGRAY;

        // main quote
        Paint quotePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        quotePaint.setColor(quoteColor);
        quotePaint.setTextSize(80);
        quotePaint.setTextAlign(Paint.Align.CENTER);
        quotePaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/PlayfairDisplay-Italic.ttf"));
        quotePaint.setShadowLayer(6, 2, 2, Color.BLACK);

        // author
        Paint authorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        authorPaint.setColor(authorColor);
        authorPaint.setTextSize(55);
        authorPaint.setTextAlign(Paint.Align.CENTER);
        authorPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));
        authorPaint.setShadowLayer(4, 2, 2, Color.BLACK);

        // Vansh + date
        Paint vanshPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vanshPaint.setColor(vanshColor);
        vanshPaint.setTextSize(40);
        vanshPaint.setTextAlign(Paint.Align.CENTER);
        vanshPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        vanshPaint.setShadowLayer(4, 2, 2, Color.BLACK);

        // title
        Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(titleColor);
        titlePaint.setTextSize(60);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        titlePaint.setShadowLayer(4, 2, 2, Color.BLACK);

        // handle below title
        Paint handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(handleColor); // synced with background
        handlePaint.setTextSize(45);
        handlePaint.setTextAlign(Paint.Align.CENTER);
        handlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        handlePaint.setShadowLayer(4, 2, 2, Color.BLACK);

        int xPos = width / 2;
        int padding = 80;

        // heading
        canvas.drawText("| Quote of the Day |", xPos, 200, titlePaint);
        // handle below heading
        canvas.drawText("@surajvansh12", xPos, 260, handlePaint);

        // draw quote
        String quotedText = "“" + quote.getBody() + "”";
        String[] quoteLines = splitTextIntoLines(quotedText, quotePaint, width - 2 * padding);

        float totalTextHeight = quoteLines.length * (quotePaint.getTextSize() + 20);
        int yPos = (int) ((height / 2) - (totalTextHeight / 2)) + 80;

        for (String line : quoteLines) {
            canvas.drawText(line, xPos, yPos, quotePaint);
            yPos += quotePaint.getTextSize() + 20;
        }

        yPos += 50;
        canvas.drawText("- " + quote.getAuthor(), xPos, yPos, authorPaint);

        yPos += 60;
        String vanshDateText = "Vansh | " + getCurrentDateString();
        canvas.drawText(vanshDateText, xPos, yPos, vanshPaint);

        return bitmap;
    }

    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String filename) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Quotes");

                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri == null) return false;

                fos = context.getContentResolver().openOutputStream(uri);
            } else {
                File imagesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Quotes");
                if (!imagesDir.exists()) imagesDir.mkdirs();
                File imageFile = new File(imagesDir, filename);
                fos = new FileOutputStream(imageFile);
                MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, null);
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void shareImage(Context context, Bitmap bitmap) {
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }
            File file = new File(cachePath, "quote_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider",
                    file);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

                PackageManager pm = context.getPackageManager();
                if (shareIntent.resolveActivity(pm) != null) {
                    context.startActivity(Intent.createChooser(shareIntent, "Share Quote"));
                } else {
                    Toast.makeText(context, "No apps available to share image", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to share image", Toast.LENGTH_SHORT).show();
        }
    }

    private static String[] splitTextIntoLines(String text, Paint paint, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (paint.measureText(testLine) < maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines.toArray(new String[0]);
    }

    private static String getCurrentDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
