package com.freshconnect.capacitor.webchat.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;

import com.freshconnect.capacitor.webchat.FreshconnectWebChat;


/**
 * 公共工具类
 */
public class Util {

    public static final String EXTERNAL_STORAGE_IMAGE_PREFIX = "external://";

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            Log.e(FreshconnectWebChat.LOG_TAG, e.getMessage());
        }

        return result;
    }

    public static byte[] getHtmlByteArray(final String url) {
        URL htmlUrl = null;
        InputStream inStream = null;
        try {
            htmlUrl = new URL(url);
            URLConnection connection = htmlUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
            }
        } catch (MalformedURLException e) {
            Log.e(FreshconnectWebChat.LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(FreshconnectWebChat.LOG_TAG, e.getMessage());
        }
        byte[] data = inputStreamToByte(inStream);

        return data;
    }

    public static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            Log.e(FreshconnectWebChat.LOG_TAG, e.getMessage());
        }

        return null;
    }

    public static byte[] readFromFile(String fileName, int offset, int len) {
        if (fileName == null) {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(FreshconnectWebChat.LOG_TAG, "readFromFile: file not found");
            return null;
        }

        if (len == -1) {
            len = (int) file.length();
        }

        Log.d(FreshconnectWebChat.LOG_TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

        if (offset < 0) {
            Log.e(FreshconnectWebChat.LOG_TAG, "readFromFile invalid offset:" + offset);
            return null;
        }
        if (len <= 0) {
            Log.e(FreshconnectWebChat.LOG_TAG, "readFromFile invalid len:" + len);
            return null;
        }
        if (offset + len > (int) file.length()) {
            Log.e(FreshconnectWebChat.LOG_TAG, "readFromFile invalid file len:" + file.length());
            return null;
        }

        byte[] b = null;
        try {
            RandomAccessFile in = new RandomAccessFile(fileName, "r");
            b = new byte[len]; // 创建合适文件大小的数组
            in.seek(offset);
            in.readFully(b);
            in.close();

        } catch (Exception e) {
            Log.e(FreshconnectWebChat.LOG_TAG, "readFromFile : errMsg = " + e.getMessage());
        }
        return b;
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            Log.d(FreshconnectWebChat.LOG_TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            Log.d(FreshconnectWebChat.LOG_TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            Log.i(FreshconnectWebChat.LOG_TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                Log.e(FreshconnectWebChat.LOG_TAG, "bitmap decode failed");
                return null;
            }

            Log.i(FreshconnectWebChat.LOG_TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                Log.i(FreshconnectWebChat.LOG_TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            Log.e(FreshconnectWebChat.LOG_TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    public static int parseInt(final String string, final int def) {
        try {
            return (string == null || string.length() <= 0) ? def : Integer.parseInt(string);

        } catch (Exception e) {
        }
        return def;
    }

    public static File getCacheFolder(Context context) {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "cache");
            if (!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }

        if (!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir(); //get system cache folder
        }

        return cacheDir;
    }

    /**
     * 下载并缓存文件
     *
     * @param context
     * @param url
     * @return
     */
    public static File downloadAndCacheFile(Context context, String url) {
        URL fileURL = null;
        try {
            fileURL = new URL(url);

            Log.d(FreshconnectWebChat.LOG_TAG, String.format("Start downloading file at %s.", url));

            HttpURLConnection connection = (HttpURLConnection) fileURL.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(FreshconnectWebChat.LOG_TAG, String.format("Failed to download file from %s, response code: %d.", url, connection.getResponseCode()));
                return null;
            }

            InputStream inputStream = connection.getInputStream();

            File cacheDir = getCacheFolder(context);
            File cacheFile = new File(cacheDir, url.substring(url.lastIndexOf("/") + 1));
            FileOutputStream outputStream = new FileOutputStream(cacheFile);

            byte buffer[] = new byte[4096];
            int dataSize;
            while ((dataSize = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, dataSize);
            }
            outputStream.close();
            Log.d(FreshconnectWebChat.LOG_TAG, String.format("File was downloaded and saved at %s.", cacheFile.getAbsolutePath()));

            return cacheFile;
        } catch (Exception e) {
            Log.e(FreshconnectWebChat.LOG_TAG, "downloadAndCacheFile failed: " + e.getMessage());
        }

        return null;
    }

    public static InputStream getFileInputStream(String url, Context context) {
        InputStream inputStream = null;
        try {
            if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                File file = Util.downloadAndCacheFile(context, url);
                if (file == null) {
                    Log.d(FreshconnectWebChat.LOG_TAG, String.format("File could not be downloaded from %s.", url));
                    return null;
                }
                inputStream = new FileInputStream(file);
                Log.d(FreshconnectWebChat.LOG_TAG, String.format("File was downloaded and cached to %s.", file.getAbsolutePath()));
            } else if (url.startsWith("data:image")) {  // base64 image
                String imageDataBytes = url.substring(url.indexOf(",") + 1);
                byte imageBytes[] = Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT);
                inputStream = new ByteArrayInputStream(imageBytes);
                Log.d(FreshconnectWebChat.LOG_TAG, "Image is in base64 format.");
            } else if (url.startsWith(EXTERNAL_STORAGE_IMAGE_PREFIX)) { // external path
                url = Environment.getExternalStorageDirectory().getAbsolutePath() + url.substring(EXTERNAL_STORAGE_IMAGE_PREFIX.length());
                inputStream = new FileInputStream(url);
                Log.d(FreshconnectWebChat.LOG_TAG, String.format("File is located on external storage at %s.", url));
            } else if (!url.startsWith("/")) {
                inputStream = context.getAssets().open(url);
                Log.d(FreshconnectWebChat.LOG_TAG, String.format("File is located in assets folder at %s.", url));
            } else {
                inputStream = new FileInputStream(url);
                Log.d(FreshconnectWebChat.LOG_TAG, String.format("File is located at %s.", url));
            }
        } catch (Exception e) {
            Log.e(FreshconnectWebChat.LOG_TAG, "downloadAndCacheFile failed: " + e.getMessage());
        }
        return inputStream;
    }

}
