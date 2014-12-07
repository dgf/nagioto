package de.g2d.nagioto;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class Utils {

    private static final String TAG = Utils.TAG;

    public static void writeToFile(Context ctx, String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }


    public static String readFromFile(Context ctx, String filename) {
        String ret = "";
        try {
            AssetManager assets = ctx.getAssets();
            InputStream inputStream = assets.open(filename);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
        return ret;
    }

    public static String slurp(final InputStream is, final int bufferSize) {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader in = new InputStreamReader(is, "UTF-8");
            try {
                for (; ; ) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
            } finally {
                in.close();
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return out.toString();
    }
}
