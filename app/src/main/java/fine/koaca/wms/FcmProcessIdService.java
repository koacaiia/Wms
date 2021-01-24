package fine.koaca.wms;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FcmProcessIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String token= FirebaseInstanceId.getInstance().getToken();
        Log.i("koacaiia",token);
    }
}
