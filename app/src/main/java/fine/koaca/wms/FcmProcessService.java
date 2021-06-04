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


//        msg=remoteMessage.getNotification().getBody();
//        SharedPreferences sharedPreferences = getSharedPreferences("SHARE_DEPOT", MODE_PRIVATE);
//        if (sharedPreferences == null) {
//            nickName = "Guest";
//        } else {
//            nickName = sharedPreferences.getString("nickName", "Fine");
//        }
//            Log.i("koacaiia","vibrator+++Called"+vibrator.toString());
//            Log.i("koacaiia","Ringtone++++Called"+rt.toString());

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{3000, 1000}, 0);

        Uri ringtoneUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone=RingtoneManager.getRingtone(getBaseContext(),ringtoneUri);
        ringtone.play();

//        Log.i("koacaiia","ringtoneService Called"+ringtone.toString());
        String alertTimeStamp = new SimpleDateFormat("HH시mm분").format(new Date());
        Intent intent = new Intent(this, AnnualLeave.class);
//        intent.putExtra("vibrator", getApplicationContext().toString());
//        intent.putExtra("vib", (Serializable) vibrator);
//        intent.putExtra("rt", (Serializable) ringtone);
        Intent fullscreenIntent = new Intent(this, IncargoEx.class);

//        intent.putExtra("vibrate", "cancel");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullscreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = getNotificationBuilder("Ask", "alert")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(nickName)
                .setContentText(contents)
                .setContentIntent(contentIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{10000, 10000,})
                .setAutoCancel(true)

//                .setOngoing(true)

//                .setFullScreenIntent(fullScreenPendingIntent,true)
                ;

//Log.i("kocaiia",vibrator.toString()+"_____"+ringtone.toString());
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,builder.build());
//        try {
//            Thread.sleep(10000);
//            ringtone.stop();
////            Thread.sleep(30000);
////            vibrator.cancel();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try {
//            Thread.sleep(30000);
//            vibrator.cancel();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        intent=new Intent(this,IncargoEx.class);
//        PendingIntent pi=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
//        try {
//            pi.send();
//        } catch (PendingIntent.CanceledException e) {
//            e.printStackTrace();
//        }

//      vibratorStart("start");


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

    public void vibratorStart(String start){
//        incargo=new Incargo();
//        incargo.vibratorStart(start);

        vibrator=(Vibrator)getSystemService(getApplicationContext().VIBRATOR_SERVICE);
//
        if(start.equals("start")){
            vibrator.vibrate(new long[]{3000,1000},0);
            Log.i("koacaiia","koacaiiaVibrate___name"+vibrator.toString());
        }else{
            vibrator.cancel();
        }

    }
    public void notificationStop(){
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(0);

    }
    @SuppressLint("ShortAlarm")
    public void alarmManager(){
        PackageManager pm=this.getPackageManager();
        ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        Date t = new Date();
        t.setTime(java.lang.System.currentTimeMillis() + 300);
        Intent intent=new Intent(FcmProcessService.this, AlarmReceiver.class);
        PendingIntent alarmIntent=PendingIntent.getBroadcast(this,0,intent,0  );
        am.setRepeating(AlarmManager.RTC_WAKEUP,t.getTime(),60,alarmIntent);
        Log.i("koacaiia","koacaiia___alarmManager Run Completed");

    }
}