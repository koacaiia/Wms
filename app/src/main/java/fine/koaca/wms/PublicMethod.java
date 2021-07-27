package fine.koaca.wms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PublicMethod {
    Activity activity;
    ArrayList<String> list=new ArrayList<>();
    SharedPreferences sharedPreferences;

    public PublicMethod(Activity activity){
        this.activity=activity;
    }
    public PublicMethod(ArrayList<String> list){
        this.list=list;
    }


    public PublicMethod(){

    }

    public void putContent(String pathValue){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference(pathValue);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);

        ArrayList<String> incargoContent=new ArrayList<>();
        incargoContent.add("컨테이너 진입");
        incargoContent.add("입고작업");
        incargoContent.add("검수완료");
        incargoContent.add("창고반입");

        String[] incargoContentList=incargoContent.toArray(new String[incargoContent.size()]);

        builder.setTitle("입고현황 변경사항")
                .setSingleChoiceItems(incargoContentList,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String contentValue=incargoContentList[which];
                        Map<String,Object> putValue=new HashMap<>();
                        putValue.put("working",contentValue);
                        databaseReference.updateChildren(putValue);

                        Toast.makeText(activity,contentValue+"로 작업 현황 등록 합니다.",Toast.LENGTH_SHORT).show();
                        dialog.cancel();

                    }
                })
                .show();


    }
    public ArrayList<String> getPictureLists(){
        ArrayList<String> imageViewLists=new ArrayList<>();
        Uri uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=activity.getContentResolver().query(uri,projection,null,null,MediaStore.MediaColumns.DATE_ADDED+" desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext()){
            String uriI=cursor.getString(columnsDataIndex);
            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Fine/입,출고/Resize");
            String strFile=String.valueOf(file);
            if(uriI.startsWith(strFile)){
                imageViewLists.add(uriI);
                Log.i("TestValue","ImageViewLists:::::"+strFile);
            }
        }
        cursor.close();
        return imageViewLists;
    }

    public void putNewDataUpdateAlarm(String nickName,String alertDepot,String dialogTitle, String consigneeName, String out,
                                      RequestQueue requestQueue) {
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String timeDate=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());

        WorkingMessageList messageList=new WorkingMessageList();


        messageList.setNickName(nickName);
        messageList.setTime(timeStamp);
        messageList.setMsg(dialogTitle);
        messageList.setDate(timeDate);
        messageList.setConsignee(consigneeName);
        messageList.setInOutCargo(out);

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("WorkingMessage"+"/"+nickName+"_"+timeStamp);
        databaseReference.setValue(messageList);

        PushFcmProgress push=new PushFcmProgress(requestQueue);
        push.sendAlertMessage(alertDepot,nickName,dialogTitle,"WorkingMessage");

        Intent intent=new Intent(activity,WorkingMessageData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public void upLoadPictures(String nickName,String consigneeName,String inoutCargo){
        ArrayList<String> uriList=new ArrayList<>();
        int listSize=list.size();
        String date=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());
        String dateNtime=new SimpleDateFormat("yyyy년MM월dd일HH시mm분ss초").format(new Date());
        String refPath;
        FirebaseStorage storage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");

        for(int i=0;i<listSize;i++){
            Uri uriValue=Uri.fromFile(new File(list.get(i)));
            refPath=date+"/"+consigneeName+"/"+inoutCargo+"/"+nickName+System.currentTimeMillis()+".jpg";
            StorageReference storageReference=storage.getReference().child("image/"+refPath);
            int finalI = i;
            storageReference.putFile(uriValue)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   uriList.add(String.valueOf(uri));
                                   WorkingMessageList messageList=new WorkingMessageList();


                                   if((uriList.size())==listSize){
                                       messageList.setNickName(nickName);
                                       messageList.setTime(dateNtime);
                                       messageList.setDate(date);
                                       messageList.setMsg(consigneeName+"_"+inoutCargo+"_ 사진 업로드");
                                       messageList.setInOutCargo(inoutCargo);

                                       try {
                                           switch (finalI) {
                                               case 0:
                                                   messageList.setUri0(uriList.get(0));
                                                   break;
                                               case 1:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   break;
                                               case 2:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   messageList.setUri2(uriList.get(2));
                                                   break;
                                               case 3:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   messageList.setUri2(uriList.get(2));
                                                   messageList.setUri3(uriList.get(3));
                                                   break;
                                               case 4:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   messageList.setUri2(uriList.get(2));
                                                   messageList.setUri3(uriList.get(3));
                                                   messageList.setUri4(uriList.get(4));
                                                   break;
                                           }
                                       }catch(IndexOutOfBoundsException e){
                                           messageList.setMsg(e.toString());
                                       }

                                       FirebaseDatabase database=FirebaseDatabase.getInstance();
                                       DatabaseReference databaseReference=
                                               database.getReference("WorkingMessage" +"/"+nickName+"_"+dateNtime   );
                                       databaseReference.setValue(messageList);
                                   }

                               }
                           });
                        }
                    });

        }
    }

    public Map<String,String> getUserInformation(){
    Map<String,String> userInformation=new HashMap<>();

    SharedPreferences sharedPreferences=TitleActivity.sharedPref;
    String nickName=sharedPreferences.getString("nickName",null);
    String depotName=sharedPreferences.getString("depotName",null);
    String deptName=null;
    String alertDepot=null;
    switch(depotName){
        case "2물류(02010027)":
            deptName="WareHouseDept2";
            alertDepot="Depot2";
            break;
        case "1물류(02010810)":
            deptName="WareHouseDept1";
            alertDepot="Depot1";
            break;

    }
    userInformation.put("nickName",nickName);
    userInformation.put("depotName",depotName);
    userInformation.put("deptName",deptName);
    userInformation.put("alertDepot",alertDepot);

    return userInformation;
    }

    public void intentSelect(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        View view=activity.getLayoutInflater().inflate(R.layout.dialog_select_intent,null);
        Button btnIn=view.findViewById(R.id.dialog_select_intent_btnIn);

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent=new Intent(activity,Incargo.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
             activity.startActivity(intent);
            }
        });
        Button btnOut=view.findViewById(R.id.dialog_select_intent_btnOut);
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           Intent intent=new Intent(activity,OutCargoActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
           activity.startActivity(intent);
            }
        });
        Button btnCamera=view.findViewById(R.id.dialog_select_intent_btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,CameraCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            }
        });

        Button btnFinish=view.findViewById(R.id.dialog_select_intent_btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            activity.finish();
            }
        });

        Button btnInit=view.findViewById(R.id.dialog_select_intent_init);
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,TitleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            }
        });

        builder.setView(view);

        AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
        Log.i("TestValue","Layout Params width::::"+params.width);
        float density=activity.getResources().getDisplayMetrics().density;
        int pxValue=Math.round((float)300*density);
        int widthPx=activity.getResources().getDisplayMetrics().widthPixels;
        Log.i("TestValue","Put 300Dp convert Pixel to:::::"+pxValue +"Device widthPx::::"+widthPx);

        params.width=(widthPx*2)/3;
        dialog.getWindow().setAttributes(params);
//        dialog.show();
//        dialog.show();가 끝에 위치하면 Attributes 반영이 안됨

    }

    public void sendPushMessage(String deptName,String nickName,String message,String contents){
        JSONObject requestData=new JSONObject();
        try{
            requestData.put("priority","high");
            JSONObject dataObj=new JSONObject();
            dataObj.put("contents",contents);
            dataObj.put("nickName",nickName);
            dataObj.put("message",message);
            requestData.put("data",dataObj);

            if(nickName.equals("Test")){
                requestData.put("to","/topics/Test");

            }else{
                requestData.put("to","/topics/"+deptName);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        sendData(requestData, new SendResponsedListener() {
            @Override
            public void onRequestStarted() {
            Log.i("TestValue","Request Started");
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
        RequestQueue requestQueue= Volley.newRequestQueue(activity.getApplicationContext());
        JsonObjectRequest request=new JsonObjectRequest(
                Request.Method.POST,"https://fcm.googleapis.com/fcm/send",requestData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sendResponsedListener.onRequestCompleted();
                Log.i("TestValue","Data Responsed Successed");
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendResponsedListener.onRequestWithError(error);
                        Log.i("TestValue","Data Responsed Failed:::"+error);
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
                return params;
            }

            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> headers=new HashMap<String,String>();
                headers.put("Authorization","key=AAAAYLjTacM:APA91bEfxvEgfzLykmd3YAu-WAI6VW64Ol8TdmGC0GIKao0EB9c3OMAsJNpPCDEUVsMgUkQjbWCpP_Dw2CNpF2u-4u3xuUF30COZslRIqqbryAAhQu0tGLdtFsTXU5EqsMGaMnGK8jpQ");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };
        request.setShouldCache(false);
        sendResponsedListener.onRequestStarted();
        requestQueue.add(request);

    }

    private interface SendResponsedListener{
        void onRequestStarted();
        void onRequestCompleted();
        void onRequestWithError(VolleyError error);
    }
}
