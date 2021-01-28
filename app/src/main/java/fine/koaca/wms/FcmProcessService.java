package fine.koaca.wms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FcmProcessService extends FirebaseMessagingService {
    private String msg,title;
    public FcmProcessService() {
    }

    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        /* hongdroid Version
       Log.i("koacaiia","onMessageReceived");
       title=remoteMessage.getNotification().getTitle();
        msg=remoteMessage.getNotification().getBody();
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
        .setContentText(msg)
        .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1,1000});
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,mBuilder.build());
        mBuilder.setContentIntent(contentIntent);
            hongdroid Version */

        /*inflearn Version
        if(remoteMessage.getNotification() !=null){
            final String message=remoteMessage.getNotification().getBody();

            Handler handler=new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FcmProcessService.this, message, Toast.LENGTH_SHORT).show();
                }
            });

        }
        inflearn Version*/

        Log.i("koacaiia","onMessagedReceived Called");
        String from=remoteMessage.getFrom();
        Map<String,String> data=remoteMessage.getData();
        String contents=data.get("contents");

        Log.i("koacaiia","from:"+from+".contents:"+contents);
        sendToActivity(getApplicationContext(),from,contents);

    }

    private void sendToActivity(Context applicationContext, String from, String contents) {
        Intent intent=new Intent(applicationContext,FcmProcess.class);
        intent.putExtra("from",from);
        intent.putExtra("contents",contents);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);
    }
}