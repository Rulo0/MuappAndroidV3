package me.muapp.android.Classes.Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rulo on 5/04/17.
 */

public class FileCache {
    private static final String TAG = "FileCache";
    private static final String CACHE_DIR = "Muapp";
    private File diskCacheDir;

    public FileCache(Context context, String dir) {
        diskCacheDir = getDiskCacheDir(context, dir);
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
    }

    public FileDescriptor getFileFromCache(String key) {
        File f = getFile(key);
        if (f.exists()) {
            try {
                Log.wtf("getFileFromCache", f.getAbsolutePath());
                FileInputStream fis = new FileInputStream(f.getAbsolutePath());
                return fis.getFD();
                //return new FileInputStream(f).getFD();
            } catch (IOException e) {
                Log.wtf("getFileFromCache", "Not exist " + f.getAbsolutePath());
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean downloadUrlToCache(String key, String urlString) {
        Log.wtf("downloadUrlToCache", urlString);
        HttpURLConnection urlConnection = null;
        BufferedInputStream in;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            return addFileToCache(key, in);

        } catch (final IOException e) {
            Log.wtf("downloadUrlToCache", e.getMessage(), e);
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return false;
    }

    public boolean addFileToCache(String key, File value) {
        try {
            value.createNewFile();
            Log.wtf("addFileToCache", value.getAbsolutePath() + " - " + key);
            FileInputStream fis = new FileInputStream(value);
            return addFileToCache(key, fis);
        } catch (Exception e) {
            Log.wtf("addFileToCache", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean addFileToCache(String key, InputStream value) {
        if (key == null || value == null) {
            Log.wtf("downloadFileToCache", "Validation " + (value == null));
            return false;
        }
        File file = getFile(key);
        if (file.exists()) {
            return true;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.wtf("downloadFileToCache", e.getMessage());
                e.printStackTrace();
                return false;
            }

            try {
                FileOutputStream out = new FileOutputStream(file);
                copy(value, out);
            } catch (IOException e) {
                e.printStackTrace();
                Log.wtf("downloadFileToCache", e.getMessage());
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }//addFileToCache


    private File getFile(String key) {
        return new File(diskCacheDir, key);
    }


    public boolean deleteFile(String key) {
        File file = getFile(key);

        return file.exists() && file.delete();
    }


    private static long copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[4096];
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }

    public File getDiskCacheDir() {
        return diskCacheDir;
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !isExternalStorageRemovable()) {
            return new File(getExternalCacheDir(context).getPath() + File.separator + uniqueName);
        } else {
            return new File(getInternalCacheDir(context).getPath() + File.separator + "Muapp" + File.separator + uniqueName);
        }
    }

    public static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }


    public static File getExternalCacheDir(Context context) {
//        return context.getExternalCacheDir();
        return Environment
                .getExternalStoragePublicDirectory(CACHE_DIR);

    }

    public static File getInternalCacheDir(Context context) {
//        return context.getCacheDir();
        return context.getFilesDir();
    }
}
