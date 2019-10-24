package com.example.triptourguide;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TripUtils {

    public static String ReadFileFromAsset(Context context, String fileName) {

        String str = "";

        try {
            InputStream is = context.getAssets().open(fileName);
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            str = new String(buffer, StandardCharsets.UTF_8);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return str;
    }



}
