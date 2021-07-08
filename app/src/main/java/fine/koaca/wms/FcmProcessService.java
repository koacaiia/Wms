package fine.koaca.wms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.datatransport.runtime.scheduling.jobscheduling.AlarmManagerSchedulerBroadcastReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FcmProcessService extends FirebaseMessagingService implements Serializable{

    public Vibrator vibrator;
    public Incargo incargo;
    public Ringtone ringtone;
    int id;



    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

       Log.i("koacaiia","onMessageReceived");

        Map<String, String> data = remoteMessage.getData();
        String contents = data.get("contents");
        String nickName=data.get("nickName");
        String message=data.get("message");



        String alertTimeStamp = new SimpleDateFormat("HH시mm분").format(new Date());
        Intent intent; 
        switch(contents){
            case "Annual":
            intent=new Intent(this, AnnualLeave.class);
            id=0;
            break;
            case "CameraUpLoad":
                intent=new Intent(this,WorkingMessageData.class);
                id=1;
                break;
            case "WorkingMessage":
                id=2;
                intent=new Intent(this,WorkingMessageData.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + contents);
        } 

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = getNotificationBuilder("Ask", "alert")
                .setSmallIcon(R.drawable.logo3)
                .setContentTitle(nickName)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{10000, 10000,})
                .setAutoCancel(true)
                ;

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,builder.build());

    }
    private NotificationCompat.Builder getNotificationBuilder(String ask,String name){
        NotificationCompat.Builder builder=null;
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel=new NotificationChannel(ask,name,NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);

        manager.createNotificationChannel(channel);

        builder=new NotificationCompat.Builder(this,ask);
        return builder;
    }
    }