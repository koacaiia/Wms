package fine.koaca.wms;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmProcessService extends FirebaseMessagingService {
    public FcmProcessService() {
    }

    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

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

    }
}