package domainlayer.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

import propulsar.pgb.R;
import propulsar.pgb.presentationlayer.activities.Splash;
import propulsar.pgb.presentationlayer.activities.TabActivity;

/**
 * Created by maubocanegra on 04/11/17.
 */

public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        sendNotification(nhMessage);
        if (TabActivity.isVisible) {
            TabActivity.tabActivity.ToastNotify(nhMessage);
        }
    }

    private void sendNotification(String msg) {

        Intent intent = new Intent(ctx, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                CharSequence name = ctx.getString(R.string.default_notification_channel_id);
                String description = ctx.getString(R.string.default_notification_channel_id);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(ctx.getString(R.string.default_notification_channel_id), name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }catch(Exception e){Log.e("CreateNotifChannel","Null Notif Channel");}
        }


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_notif_yonayarit)
                        .setContentTitle(ctx.getString(R.string.app_name))
                        .setChannelId(ctx.getString(R.string.default_notification_channel_id))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
