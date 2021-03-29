//package fine.koaca.wms;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.Response.ErrorListener;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
//import com.google.firebase.messaging.FirebaseMessaging;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class FcmProcess extends AppCompatActivity {
//    TextView textView;
//    TextView textView2;
//    EditText editText;
//    TextView textView3;
//
//    static RequestQueue requestQueue;
//    static String regId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fcm_process);
//        FirebaseMessaging.getInstance().subscribeToTopic("Alert");
//        textView=findViewById(R.id.textView6);
//        textView2=findViewById(R.id.textView7);
//        textView3=findViewById(R.id.txt_send);
//        editText=findViewById(R.id.edit_send);
//
////        getToken();
//
//        Button button=findViewById(R.id.button);
//        button.setOnClickListener(v->{
//            String instanceId=FirebaseInstanceId.getInstance().getId();
//            println("Knowed Id:"+instanceId);
//        });
//
//        Button buttonSend=findViewById(R.id.btn_send);
//        buttonSend.setOnClickListener(v->{
//            String input=editText.getText().toString();
//            send(input);
//        });
//
//        if(requestQueue==null){
//            requestQueue= Volley.newRequestQueue(getApplicationContext());
//        }
//
//    }
//
//    public void send(String input) {
//        JSONObject requestData=new JSONObject();
//        try{
//            requestData.put("priority","high");
//            JSONObject dataObj=new JSONObject();
//            dataObj.put("contents",input);
//            requestData.put("data",dataObj);
//            requestData.put("to","/topics/Alert");
//
////            JSONArray idArray=new JSONArray();
////            idArray.put(0,regId);
////            requestData.put("registration_ids",idArray);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        sendData(requestData,new SendResponseListener(){
//
//            @Override
//            public void onRequestStarted() {
//                printll("onRequestStarted Called");
//            }
//            @Override
//            public void onRequestCompleted() {
//                printll("onRequestCompleted Called");
//            }
//            @Override
//            public void onRequestWithError(VolleyError error) {
//                printll("onRequestWithError Called");
//            }
//        });
//    }
//
//    private void sendData(JSONObject requestData, SendResponseListener sendResponseListener) {
//        JsonObjectRequest request=new JsonObjectRequest(
//                Request.Method.POST,
//                "https://fcm.googleapis.com/fcm/send",
//                requestData,
//                new Response.Listener<JSONObject>(){
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        sendResponseListener.onRequestCompleted();
//                    }},
//                            new Response.ErrorListener(){
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    sendResponseListener.onRequestWithError(error);
//                                }
//                            })
//        {
//            @Override
//            protected Map<String,String> getParams() throws AuthFailureError{
//                Map<String,String> params=new HashMap<String,String>();
//                return params;
//        }
//        @Override
//            public Map<String,String> getHeaders() throws AuthFailureError{
//                Map<String,String> headers=new HashMap<String,String>();
//                headers.put("Authorization","key=AAAAKv8kPlM:APA91bF8Hq-XBpxF9a0z7pDBVRBabqUZt3uela3d6m5r9iWXzIzCJJcCplCcWRksa47jYXGGL5LMSBTMXVWzVhU4JzThvsExOQ2VKRt1H7rzoOg6yL2CKH4KNlIbV1oCC8zzJ1DHxW10");
//                return headers;
//        }
//        @Override
//            public String getBodyContentType(){
//                return "application/json";
//        }
//
//        };
//
//        request.setShouldCache(false);
//        sendResponseListener.onRequestStarted();
//        requestQueue.add(request);
//    }
//
//    public interface SendResponseListener{
//        public void onRequestStarted();
//        public void onRequestCompleted();
//        public void onRequestWithError(VolleyError error);
//
//    }
//
//    public void printll(String data){
//        textView3.append(data+"\n");
//    }
//
//    private void println(String s) {
//        textView2.append(s+"\n");
//    }
//
//    public void getToken(){
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
//                    @Override
//                    public void onSuccess(InstanceIdResult instanceIdResult) {
//                        String newToken=instanceIdResult.getToken();
//                        println("RegId:"+newToken);
//
//                    }
//                });
//
//                 /* inflearn Version
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if(!task.isSuccessful()){
//                            Log.i("koacaiia","getInatanceId Failed",task.getException());
//                            return;
//                        }
//                        String token=task.getResult().getToken();
//                        Toast.makeText(FcmProcess.this, token, Toast.LENGTH_SHORT).show();
//
//                    }
//
//                });
//                inflearn Version*/
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        println("onNewIntent Called");
//        if(intent !=null){
//            processIntent(intent);
//        }
//        super.onNewIntent(intent);
//    }
//
//    private void processIntent(Intent intent) {
//        String from=intent.getStringExtra("from");
//        if(from==null){
//            println("from is null.....");
//            return;
//        }
//        String contents=intent.getStringExtra("contents");
//        println("DATA:"+from+","+contents);
//        textView.setText("["+from+"] DATA from "+contents);
//    }
//}
