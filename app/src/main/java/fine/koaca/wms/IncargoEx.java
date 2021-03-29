package fine.koaca.wms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class IncargoEx extends AppCompatActivity {
    IncargoListAdapter adpater;
    RecyclerView recyclerView;
    FloatingActionButton flt;
    Ringtone rt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incargo_ex);
        flt=findViewById(R.id.floatingActionButton2);
        FcmProcessService fs=new FcmProcessService();
        Vibrator vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
//        Vibrator vibrator=fs.vibrator;
        vibrator.vibrate(new long[]{1,500,3000,1000},0);
        Uri notification= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        rt=RingtoneManager.getRingtone(getBaseContext(),notification);
        rt.play();
        Log.i("kocaiia",vibrator.toString()+"_____"+rt.toString());
        flt.setOnClickListener(v->{
            vibrator.cancel();
            rt.stop();
            Log.i("kocaiia",vibrator.toString()+"_____"+rt.toString());
        });
    }
}