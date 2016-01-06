package de.htw_berlin.sharkandroidstack;

import android.content.Context;
import android.content.Intent;

public final class Utils {

    public static String deviceId;

    public static void startActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }
}
