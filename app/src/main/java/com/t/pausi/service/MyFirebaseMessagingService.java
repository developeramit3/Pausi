package com.t.pausi.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.t.pausi.Activity.ChatActivity;
import com.t.pausi.Activity.HomeActivity;
import com.t.pausi.Activity.LoginActivity;
import com.t.pausi.Activity.NotificationActivity;
import com.t.pausi.Activity.WelcomeSliderActivity;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Constant.Constants;
import com.t.pausi.utils.NotificationUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nitin on 14/12/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    MySharedPref sp;
    private NotificationUtils notificationUtils;
    String notification_status, format;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //handleNotification(remoteMessage.getNotification().getBody());
            handleNotification("");
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format = simpleDateFormat.format(new Date());


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
            showNotificationMessage(getApplicationContext(), "Pausi", "You have new chat message.", format, resultIntent);
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        System.out.println("----------------------json----------------" + json);

        try {

            JSONObject data = json.getJSONObject("message");
            System.out.println("----------------------json----------------" + data);
            String result = data.getString("result");
            String key = data.getString("key");
            Intent resultIntent = new Intent(getApplicationContext(), NotificationActivity.class);
            Intent resultIntent1 = new Intent(getApplicationContext(), HomeActivity.class);
            Intent resultIntent2 = new Intent(getApplicationContext(), WelcomeSliderActivity.class);

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format = simpleDateFormat.format(new Date());
                Log.e(TAG, "push json: " + json.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", data.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);


                if (key.equalsIgnoreCase("You have a new tips")) {
                    showNotificationMessage(getApplicationContext(), "Pausi", key, format, resultIntent);

                }

                if (key.equalsIgnoreCase("Property Updated Successfully")) {
                    showNotificationMessage(getApplicationContext(), "Pausi", key, format, resultIntent);
                }

                if (key.equalsIgnoreCase("New Property has been added")) {
                    showNotificationMessage(getApplicationContext(), "Pausi", key, format, resultIntent);
                }

                if (key.equalsIgnoreCase("Delete User successfully")) {
                    showNotificationMessage(getApplicationContext(), "Pausi", "you have been deleted by Admin", format, resultIntent2);
                    startActivity(resultIntent2);
                }

            } else {

                // app is in background, show the notification in notification tray
                Log.e("hello like", "else");

                if (key.equalsIgnoreCase("You have a new tips")) {
                    showNotificationMessage(getApplicationContext(), "Pausi", key, format, resultIntent);


                }

                if (key.equalsIgnoreCase("Property Updated Successfully")) {
                    showNotificationMessage(getApplicationContext(), "Pausi", key, format, resultIntent);

                }

                if (key.equalsIgnoreCase("New Property has been added")) {
                    showNotificationMessage(getApplicationContext(), "Pausi", key, format, resultIntent);
                }

                if (key.equalsIgnoreCase("Delete User successfully")) {
                    showNotificationMessage(getApplicationContext(), "Pausi", "you have been deleted by Admin", format, resultIntent2);

                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}