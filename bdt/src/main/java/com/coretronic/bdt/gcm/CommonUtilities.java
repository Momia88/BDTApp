package com.coretronic.bdt.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public final class CommonUtilities {

     /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "827492188278";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "info";

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.google.android.gcm.GCMBroadcastReceiver";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     */
    static void displayMessage(Context context, Bundle bundle) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }
}
