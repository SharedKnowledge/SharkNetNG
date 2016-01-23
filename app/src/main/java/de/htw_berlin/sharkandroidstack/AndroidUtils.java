package de.htw_berlin.sharkandroidstack;

import android.content.Context;
import android.content.Intent;

import java.util.Random;

/**
 * Created by mn-io on 22.01.16.
 */
public final class AndroidUtils {

    public static String deviceId;

    public static void startActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    public static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random randomGenerator = new Random();

        StringBuffer randStr = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = randomGenerator.nextInt(chars.length());
            char ch = chars.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }
}
