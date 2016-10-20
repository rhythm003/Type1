package com.rhythm003.help;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Rhythm003 on 10/20/2016.
 */

public class Util {
    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("fitbit.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }
}
