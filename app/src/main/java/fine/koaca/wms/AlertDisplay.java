package fine.koaca.wms;

import android.content.Context;
import android.media.Ringtone;
import android.os.Vibrator;



public class AlertDisplay {
    Ringtone ringtone;
    Vibrator vibrator;
    public void displayAlert(){
//        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{3000, 1000}, 0);

    }

}
