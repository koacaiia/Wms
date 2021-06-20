package fine.koaca.wms;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushFcmProgress {

    RequestQueue requestQueue;

    public PushFcmProgress(RequestQueue requestQueue) {
        this.requestQueue=requestQueue;
    }

    public void sendAlertMessage(String depotName,String nickName,String message,String contents){
        JSONObject requestData=new JSONObject();
        try {
            requestData.put("priority","high");
            JSONObject dataObj=new JSONObject();
            dataObj.put("contents",contents);
            dataObj.put("nickName",nickName);
            dataObj.put("message",message);

            requestData.put("data",dataObj);
            requestData.put("to","/topics/"+depotName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendData(requestData, new SendResponsedListener() {
            @Override
            public void onRequestStarted() {
               Log.i("duatjsrb","send Push Message succeeded");
            }

            @Override
            public void onRequestCompleted() {

            }

            @Override
            public void onRequestWithError(VolleyError error) {

            }
        });



    }

    private void sendData(JSONObject requestData, SendResponsedListener sendResponsedListener) {
        JsonObjectRequest request=new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        sendResponsedListener.onRequestCompleted();
                    }},
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendResponsedListener.onRequestWithError(error);
                    }
                })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
                return params;
            }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> headers=new HashMap<String,String>();
                headers.put("Authorization","key=AAAAYLjTacM:APA91bEfxvEgfzLykmd3YAu-WAI6VW64Ol8TdmGC0GIKao0EB9c3OMAsJNpPCDEUVsMgUkQjbWCpP_Dw2CNpF2u-4u3xuUF30COZslRIqqbryAAhQu0tGLdtFsTXU5EqsMGaMnGK8jpQ");
                return headers;
            }
            @Override
            public String getBodyContentType(){
                return "application/json";
            }

        };

        request.setShouldCache(false);
        sendResponsedListener.onRequestStarted();
        requestQueue.add(request);
    }



    public interface SendResponsedListener {
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }
}
