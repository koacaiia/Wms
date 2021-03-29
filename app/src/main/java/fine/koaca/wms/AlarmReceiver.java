package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("koacaiia","kocaiia___AlaramReceived");
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

               vibrator.vibrate(new long[]{1000, 1000}, -1);

//            intent = new Intent(context, Incargo.class);
//            PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
//                    PendingIntent.FLAG_ONE_SHOT);
//
//            try {
//                pi.send();
//            } catch (PendingIntent.CanceledException e) {
//                e.printStackTrace();
//            }


    }
    }