package fine.koaca.wms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.StringPrepParseException;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.security.auth.Destroyable;

public class PublicMethod {
    Activity activity;
    ArrayList<String> list=new ArrayList<>();
    String deptName;
    String nickName;

    public PublicMethod(Activity activity){
        this.activity=activity;
    }
    public PublicMethod(ArrayList<String> list){
        this.list=list;
    }

    public PublicMethod(){
    }

    public PublicMethod(Activity activity, ArrayList<String> list) {
        this.activity=activity;
        this.list=list;
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
    public ArrayList<String> getPictureLists(String sort){
        ArrayList<String> imageViewLists=new ArrayList<>();
        Uri uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=activity.getContentResolver().query(uri,projection,null,null,MediaStore.MediaColumns.DATE_ADDED+" desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while(cursor.moveToNext()){
            String uriI=cursor.getString(columnsDataIndex);
            File file=null;
            switch(sort){
                case "Re":
                    file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Fine/입," +
                        "출고/Resize");
                    break;
                case "All":
                    file=new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
                    break;
                case "Ori":
                    file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Fine/입," +
                            "출고/Ori");
                    break;
            }
            String strFile=String.valueOf(file);
            if(uriI.startsWith(strFile)){
                imageViewLists.add(uriI);
            }
        }
        cursor.close();
        return imageViewLists;
    }

    public void putNewDataUpdateAlarm(String nickName,String message, String consigneeName, String inOut,String deptName
                                     ) {
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String timeDate=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());

        WorkingMessageList messageList=new WorkingMessageList();

        messageList.setNickName(nickName);
        messageList.setTime(timeStamp);
        messageList.setMsg(message);
        messageList.setDate(timeDate);
        messageList.setConsignee(consigneeName);
        messageList.setInOutCargo(inOut);


        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference =
                database.getReference("DeptName/"+deptName+"/WorkingMessage/"+nickName+"_"+timeStamp);
        databaseReference.setValue(messageList);
        if(message.contains("요청")){
            sendPushMessage(deptName,nickName,message,"AskedWorkingMessage");
        }else if(consigneeName.equals("근태")){
            sendPushMessage(deptName,nickName,message,"Annual");
        }else{
            sendPushMessage(deptName,nickName,message,"WorkingMessage");
        }


        Intent intent=new Intent(activity,WorkingMessageData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public void upLoadPictures(String nickName,String consigneeName,String inoutCargo,String keyValue,String deptName){

        ArrayList<String> uriList=new ArrayList<>();
        int listSize=list.size();
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateNtime=new SimpleDateFormat("yyyy년MM월dd일HH시mm분ss초").format(new Date());
        String refPath;
        FirebaseStorage storage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");

        for(int i=0;i<listSize;i++){
            Uri uriValue=Uri.fromFile(new File(list.get(i)));
            refPath=deptName+"/"+date+"/"+inoutCargo+"/"+keyValue+"/"+nickName+System.currentTimeMillis()+".jpg";
            StorageReference storageReference=storage.getReference().child("images/"+refPath);
            StorageReference consigneeReference;


            if(inoutCargo.equals("장비_시설물")){
                consigneeReference=
                        storage.getReference().child(deptName+"/"+
                                inoutCargo+"/"+consigneeName+"/"+keyValue+"/"+nickName+System.currentTimeMillis()+".jpg");

            }else if(inoutCargo.equals("Pallet")){
                consigneeReference=
                        storage.getReference().child(deptName+"/"+
                                inoutCargo+"/"+consigneeName+"/"+keyValue+"/"+nickName+System.currentTimeMillis()+".jpg");
            }else{
                consigneeReference=
                        storage.getReference().child("ConsigneeValue/"+consigneeName+"/"+keyValue+"/"+nickName+System.currentTimeMillis()+".jpg" );
            }


            int finalI = i;

            consigneeReference.putFile(uriValue).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
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
                                       messageList.setConsignee(consigneeName);
                                       messageList.setNickName(nickName);
                                       messageList.setTime(dateNtime);
                                       messageList.setDate(date);
                                       messageList.setMsg(consigneeName+"_"+inoutCargo+"_ 사진 업로드");
                                       messageList.setInOutCargo(inoutCargo);
                                       messageList.setKeyValue(keyValue);

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
                                               case 5:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   messageList.setUri2(uriList.get(2));
                                                   messageList.setUri3(uriList.get(3));
                                                   messageList.setUri4(uriList.get(4));
                                                   messageList.setUri5(uriList.get(5));
                                               case 6:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   messageList.setUri2(uriList.get(2));
                                                   messageList.setUri3(uriList.get(3));
                                                   messageList.setUri4(uriList.get(4));
                                                   messageList.setUri5(uriList.get(5));
                                                   messageList.setUri6(uriList.get(6));
                                                   break;
                                           }
                                       }catch(IndexOutOfBoundsException e){
                                           messageList.setMsg(e.toString());
                                       }

                                       FirebaseDatabase database=FirebaseDatabase.getInstance();
                                       DatabaseReference databaseReference=
                                               database.getReference("DeptName/"+deptName +"/WorkingMessage/"+nickName+"_"+dateNtime   );
                                       databaseReference.setValue(messageList);

                                       sendPushMessage(deptName,nickName,consigneeName+"_"+inoutCargo+"_ 사진 업로드","CameraUpLoad");

                                   }

                               }
                           });
                        }
                    });

        }
    }

    public Map<String,String> getUserInformation(){
    Map<String,String> userInformation=new HashMap<>();
    SharedPreferences sharedPreferences=activity.getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);
    String nickName=sharedPreferences.getString("nickName",null);
    String deptName=sharedPreferences.getString("deptName",null);

    userInformation.put("nickName",nickName);
    userInformation.put("deptName",deptName);

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

        Button btnWorkingStaff=view.findViewById(R.id.dialog_select_intent_btnEquipFacility);
        btnWorkingStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,ActivityEquipFacility.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
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

        Button btnAnnual=view.findViewById(R.id.dialog_select_intent_btnAnnual);
        btnAnnual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,ActivityWorkingStaff.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Toast.makeText(activity.getApplicationContext(),"길게누르면 연,월차,휴가 등록 창 으로 전황 됩니다",Toast.LENGTH_SHORT).show();
                activity.startActivity(intent);
            }
        });

        btnAnnual.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent=new Intent(activity,AnnualLeave.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                return true;
            }
        });

        Button btnMessage=view.findViewById(R.id.dialog_select_intent_btnMessage);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,WorkingMessageData.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);

            }
        });

        Button btnPallet=view.findViewById(R.id.dialog_select_intent_btnPallet);
        btnPallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,ActivityPallet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            }
        });

        Button btnLocation=view.findViewById(R.id.dialog_select_intent_btnLocation);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(activity,ActivityLocationSearch.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);

//

              }
        });

        builder.setView(view);

        AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
        float density=activity.getResources().getDisplayMetrics().density;
        int pxValue=Math.round((float)300*density);
        int widthPx=activity.getResources().getDisplayMetrics().widthPixels;

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
                requestData.put("to","/topics/Test1");
            }else{
                requestData.put("to","/topics/"+deptName);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        sendData(requestData, new SendResponsedListener() {
            @Override
            public void onRequestStarted() {

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

    public void updateBasicDataRef() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference basicDataRef=database.getReference("DeptName/"+getUserInformation().get("deptName")+"/BaseRef");
        basicDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    if(data.getKey().equals("consigneeRef")){
                        String strConRef=data.getValue().toString();
                        SharedPreferences sharedPreferences=activity.getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("consigneeList",strConRef);
                        editor.apply();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface SendResponsedListener{
        void onRequestStarted();
        void onRequestCompleted();
        void onRequestWithError(VolleyError error);
    }

    public void checkUserInfo(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

           ArrayList<String> deptSort=new ArrayList<>();

            deptSort.add("1물류(02010810)");
            deptSort.add("2물류(02010027)");
            deptSort.add("(주)화인통상 창고사업부");

            String[] deptList=deptSort.toArray(new String[deptSort.size()]);
            AlertDialog.Builder builder=new AlertDialog.Builder(activity);
            View view=activity.getLayoutInflater().inflate(R.layout.user_reg,null);
            EditText reg_edit=view.findViewById(R.id.user_reg_Edit);
            Button reg_button=view.findViewById(R.id.user_reg_button);
            TextView reg_txt=view.findViewById(R.id.user_reg_depot);
            reg_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nickName=reg_edit.getText().toString();
                    reg_txt.setText(deptName+"_"+nickName+"으로 사용자 등록을"+"\n"+" 진행할려면 하단 확인 버튼 클릭 바랍니다.");
                }
            });


            builder.setView(view)
                    .setSingleChoiceItems(deptList,0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which){
                                case 0:
                                deptName="WareHouseDept1";
                                    break;
                                case 1:
                                    deptName="WareHouseDept2";
                                    break;
                                case 2:
                                    deptName="WareHouseDivision";
                            }
                            reg_txt.setText("부서명"+deptName+" 로 확인");
                        }
                    })
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(deptName==null||nickName.equals("")){
                                Toast.makeText(activity.getApplicationContext(), "사용자 정보 누락 확인됩니다.다시한번 확인 후 등록 바랍니다.",
                                        Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                checkUserInfo();
                            }else{
                               editor.putString("deptName",deptName);
                                editor.putString("nickName",nickName);
                                editor.apply();
                                Toast.makeText(activity.getApplicationContext(), deptName+"__"+nickName+"로 사용자 등록 성공 하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(activity.getApplicationContext(),TitleActivity.class);
                                activity.startActivity(intent);
                            }

                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

    }

    public void adapterPictureSavedMethod(String uriValue){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        View view=activity.getLayoutInflater().inflate(R.layout.imageview_list,null);
        ImageView imageView=view.findViewById(R.id.captureimageview);

        Glide.with(view).asBitmap()
                .load(uriValue)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                     imageView.setImageBitmap(resource);
                    }
                });

        builder.setTitle("사진저장 확인")
                .setView(view)
                .setMessage("저장된 사진은 PICTURES/Fine/DownLoad 경로에 저장 됩니다."+"\n"+"업무에 참고 하시기 바랍니다."+"\n")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Glide.with(activity).asBitmap()
                                .load(uriValue)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                     glideImageToSave(resource);
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public void glideImageToSave(Bitmap resource){
        ContentResolver resolver=activity.getContentResolver();
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME,System.currentTimeMillis()+".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/*");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/Fine/DownLoad");
        Uri imageUri=resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        OutputStream fos=null;
        try{
            fos=resolver.openOutputStream(imageUri);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        resource.compress(Bitmap.CompressFormat.JPEG,100,fos);
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(activity,"Saved InPath PICTURES/Fine/DownLoad",Toast.LENGTH_SHORT).show();

    }

    public void pltReg(String consigneeName,String nickName, int totalQty,String bl,String des){
        final String[] pltDate = {new SimpleDateFormat("yyyy-MM-dd").format(new Date())};
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        final String[] pltS = new String[1];
        final String[] inOut = new String[1];
        final int[] pltQty = new int[1];
        View view=activity.getLayoutInflater().inflate(R.layout.dialog_plt_reg,null);
        TextView pltTxt=view.findViewById(R.id.plt_txtTitle);
        Button btnDate=view.findViewById(R.id.plt_date);
        btnDate.setText("팔렛트 수량 변경일:"+pltDate[0]);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                DatePicker datePicker=new DatePicker(activity);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                        String month,day;
                        if(i1+1<10){
                            month="0"+(i1+1);
                        }else{
                            month=String.valueOf(i1+1);
                        }
                        if(i2<10){
                            day="0"+i2;
                        }else{
                            day=String.valueOf(i2);
                        }
                        pltDate[0] =i+"-"+month+"-"+day;

                    }
                });
                builder.setTitle("수량변경일 수정창")
                        .setView(datePicker)
                        .setPositiveButton("변경일 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                btnDate.setText("팔렛트 수량 변경일:"+pltDate[0]);
                            }
                        })
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
        RecyclerView imageRecyclerView=view.findViewById(R.id.plt_imagerecyclerview);
        LinearLayoutManager manager=new LinearLayoutManager(activity);
        imageRecyclerView.setLayoutManager(manager);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        ArrayList<ImageViewList> adapterListArr=new ArrayList<>();
        ImageViewList adapterList;
        for(int i=0;i<list.size();i++){
            adapterList=new ImageViewList(list.get(i));
            adapterListArr.add(adapterList);
        }
        ImageViewListAdapter adapter=new ImageViewListAdapter(adapterListArr);
        imageRecyclerView.setAdapter(adapter);
        TextView pltTxtBl=view.findViewById(R.id.plt_txtBl);
        pltTxtBl.setText("비엘:"+bl);
        TextView pltTxtDes=view.findViewById(R.id.plt_txtDes);
        pltTxtDes.setText("품명:"+des);
        pltTxt.setText("화주명:"+consigneeName+" 팔렛트 재고등록");
        Button pltBtnKpp=view.findViewById(R.id.plt_btnKPP);
        Button pltBtnAj=view.findViewById(R.id.plt_btnAJ);
        Button pltBtnEtc=view.findViewById(R.id.plt_btnETC);
        pltBtnKpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pltS[0] ="KPP";
                pltBtnKpp.setTextColor(Color.RED);
                pltBtnAj.setTextColor(Color.WHITE);
                pltBtnEtc.setTextColor(Color.WHITE);
                pltTxt.setText(consigneeName+" "+pltS[0]);
            }
        });

        pltBtnAj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pltS[0]="AJ";
                pltBtnKpp.setTextColor(Color.WHITE);
                pltBtnAj.setTextColor(Color.RED);
                pltBtnEtc.setTextColor(Color.WHITE);
                pltTxt.setText(consigneeName+" "+pltS[0]);
            }
        });

        pltBtnEtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pltS[0]="ETC";
                pltBtnKpp.setTextColor(Color.WHITE);
                pltBtnAj.setTextColor(Color.WHITE);
                pltBtnEtc.setTextColor(Color.RED);
                pltTxt.setText(consigneeName+" "+pltS[0]);
            }
        });
        EditText pltEdit=view.findViewById(R.id.plt_editQty);
        pltEdit.setText(String.valueOf(totalQty));
        pltEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        pltEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pltEdit.setText("");
            }
        });
        Button pltIn=view.findViewById(R.id.plt_btnIn);
        Button pltOut=view.findViewById(R.id.plt_btnOut);
        pltIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pltIn.setTextColor(Color.RED);
                pltOut.setTextColor(Color.WHITE);
                inOut[0] ="In";
                pltQty[0] =Integer.parseInt(pltEdit.getText().toString());
                pltTxt.setText(consigneeName+" 입고 "+pltS[0]+" 팔렛트 "+pltQty[0]+" 장 등록");

            }
        });

        pltOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pltIn.setTextColor(Color.WHITE);
                pltOut.setTextColor(Color.RED);
                inOut[0]="Out";
                pltQty[0] =Integer.parseInt(pltEdit.getText().toString());
                pltTxt.setText(consigneeName+" 출고 "+pltS[0]+" 팔렛트 "+pltQty[0]+" 장 등록");
            }
        });
        builder.setTitle("팔렛트 재고 등록 확인 창")
                .setView(view)
                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(pltEdit.getText().toString().equals("0")){
                            Toast.makeText(activity,"팔렛트 수량 확인후 진행 바랍니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            pltQty[0] =Integer.parseInt(pltEdit.getText().toString());
                        }

                        SharedPreferences sharedPreferences=activity.getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);
                        String deptName=sharedPreferences.getString("deptName",null);
                        String upLoadKeyValue=nickName+"_"+ pltDate[0] +"_"+pltS[0]+pltQty[0]+"장_"+inOut[0];
                        DatabaseReference pltRef = null;
                        Map<String,Object> value=new HashMap<>();
                        value.put("nickName",nickName);
                        value.put("date", pltDate[0]);
                        value.put("keyValue",bl+"_"+des+"_"+upLoadKeyValue);
                        value.put("bl",bl);
                        value.put("des",des);

                        if(pltS[0]==null){
                            Toast.makeText(activity,"팔렛트 규격 확인후 진행 바랍니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(inOut[0]==null){
                            Toast.makeText(activity,"입고,사용 처 가시 확인후 진행 바랍니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            switch(inOut[0]){
                                case "In":
                                    pltRef=FirebaseDatabase.getInstance().getReference("DeptName/"+deptName+
                                            "/PltManagement/"+consigneeName+"/"+pltS[0]+"/"+upLoadKeyValue);
                                    value.put("inQty",pltQty[0]);
                                    value.put("outQty",0);
                                    break;
                                case "Out":
                                    pltRef=FirebaseDatabase.getInstance().getReference("DeptName/"+deptName+
                                            "/PltManagement/"+consigneeName+"/"+pltS[0]+"/"+bl+"_"+des+"_"+upLoadKeyValue);
                                    value.put("inQty",0);
                                    value.put("outQty",pltQty[0]);
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + inOut[0]);
                            }
                        }

                        pltRef.updateChildren(value);

                        upLoadPictures(nickName,consigneeName,"Pallet",upLoadKeyValue,deptName);
                        Toast.makeText(activity,consigneeName+"_"+pltS[0]+"_"+inOut[0]+"_"+pltQty[0]+"장으로"+list.size() +" 장의 " +
                                        "사진이 서버" +
                                        " 등록 " +
                                        "되었습니다.",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    public ArrayList<String> getConsigneeList(){
        SharedPreferences preferences=activity.getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);
        String consigneeStr=preferences.getString("consigneeList",null);
        ArrayList<String> consigneeList=new ArrayList<String>();
        if(consigneeStr !=null){
            try {
                JSONArray jsonArray=new JSONArray(consigneeStr);
                for(int i=0;i<jsonArray.length();i++){
                    String consigneeName=jsonArray.optString(i);
                    consigneeList.add(consigneeName);
                    Log.i("TestValue","consignee Value="+consigneeName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return consigneeList;
    }

    public void imageViewListCount(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        ArrayList<String> imageViewListCountArr=new ArrayList<>();
        imageViewListCountArr.add("1장씩");
        imageViewListCountArr.add("2장씩");
        imageViewListCountArr.add("3장씩");
        imageViewListCountArr.add("4장씩");

        String[] imageViewListCountList=imageViewListCountArr.toArray(new String[imageViewListCountArr.size()]);
        builder.setTitle("사진리스트 수 조정창")
                .setSingleChoiceItems(imageViewListCountList,2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    putSharedPreference("imageViewListCount",String.valueOf(i+1));
                    Toast.makeText(activity.getApplicationContext(),"다음 어플 구동부터 사진 목록"+(i+1)+" 장으로 표시 됩니다.",Toast.LENGTH_SHORT).show();
                    }
                }).show();

    }

    public void putSharedPreference(String key,String value){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public void putRemarkValue(String bl,String des){
        deptName=getUserInformation().get("deptName");
        nickName=getUserInformation().get("nickName");
        String activityName=activity.getLocalClassName();
        ArrayList<String> arrBlDes=new ArrayList<>();
        ArrayList<String> arrBl=new ArrayList<>();
        ArrayList<String> arrDes=new ArrayList<>();
        switch(activityName){
            case "Incargo":
                arrBl.add(bl);
                arrDes.add(des);
                arrBlDes.add(bl+"_"+des);
                break;
            case "OutCargoActivity":
               arrBl=extractChar(bl,',');
               arrDes=extractChar(des,',');
                for(int i=0; i<arrBl.size();i++){
                    arrBlDes.add(arrBl.get(i)+"_"+arrDes.get(i));
                }
                break;
        }

        String[] blDesList=arrBlDes.toArray(new String[arrBlDes.size()]);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        View view=activity.getLayoutInflater().inflate(R.layout.dialog_putremark,null);
        Spinner spinner=view.findViewById(R.id.dialog_remark_spinner);
        TextView txtContent=view.findViewById(R.id.dialog_remark_txtRemark);
        TextView txtBl=view.findViewById(R.id.dialog_remark_txtBl);
        TextView txtDes=view.findViewById(R.id.dialog_remark_txtDes);
        EditText editPutRemark=view.findViewById(R.id.dialog_remark_edit);
        Button btnPutRemark=view.findViewById(R.id.dialog_reamrk_btnPutRemark);
        ArrayAdapter<String> remarkAdapter=new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_dropdown_item,
                blDesList);
        remarkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(remarkAdapter);
        ArrayList<String> finalArrBl = arrBl;
        ArrayList<String> finalArrDes = arrDes;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              txtBl.setText(finalArrBl.get(i)+"::");
              txtDes.setText(finalArrDes.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnPutRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remarkValue=editPutRemark.getText().toString();

                        remarkValue=remarkValue.replace(',','_');
                        txtContent.setText(remarkValue);
                }

        });

        builder.setView(view)
                .setPositiveButton("이어쓰기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String blValue=txtBl.getText().toString().replace("::","");
                        String desValue=txtDes.getText().toString();
                        String remarkValue=txtContent.getText().toString();

                        if(txtContent.getText().toString().equals("")){
                            Toast.makeText(activity.getApplicationContext(),"비고특이사항 공란 입니다.다시 확인후 등록 바랍니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference ref=database.getReference( "DeptName/" + deptName + "/" +"OutCargo/RemarkReference/");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               for(DataSnapshot data:snapshot.getChildren()){
                                   if(data.getKey().equals(blValue)){
                                       ListRemarkReference mList=data.getValue(ListRemarkReference.class);
                                       String oldDes ="",oldNick="",oldRemark="";
                                       String activityName=activity.getLocalClassName();
                                       switch(activityName){
                                           case "Incargo":
                                               oldDes=
                                                       mList.getInCargoDesValue();
                                               oldNick=
                                                       mList.getInCargoNickName();
                                               oldRemark=
                                                       mList.getInCargoRemarkValue();
                                               break;
                                           case "OutCargoActivity":
                                               oldDes=
                                                       mList.getOutCargoDesValue();
                                               oldNick=
                                                       mList.getOutCargoNickName();
                                               oldRemark=
                                                       mList.getOutCargoRemarkValue();
                                               break;
                                       }
                                      updateRemarkValue(blValue,oldDes+desValue+",",oldRemark+remarkValue+",",
                                              oldNick+nickName+",");

                                   }
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });

                    }
                })
                .setNegativeButton("덮어쓰기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String blValue=txtBl.getText().toString().replace("::","");
                        String desValue=txtDes.getText().toString()+",";
                        String remarkValue=txtContent.getText().toString()+",";
                        updateRemarkValue(blValue,desValue,remarkValue,nickName+",");
                    }
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();


    }

    public void updateRemarkValue(String bl,String des,String remark,String nickName){

        String putRefPath="DeptName/" + deptName + "/" +"OutCargo/RemarkReference/"+bl;
        String activityName=activity.getLocalClassName();
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference remarkRef=database.getReference(putRefPath);
        Map<String,Object> remarkMap=new HashMap<>();

        switch(activityName){
            case "Incargo":
                remarkMap.put("inCargoRemarkValue",remark);
                remarkMap.put("inCargoNickName",nickName);
                remarkMap.put("inCargoDesValue",des);
                break;
            case "OutCargoActivity":
                remarkMap.put("outCargoRemarkValue",remark);
                remarkMap.put("outCargoNickName",nickName);
                remarkMap.put("outCargoDesValue",des);
        }

        remarkRef.updateChildren(remarkMap);

    }
    public void getRemarkValue(String bl){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference getRemarkRef=database.getReference("DeptName/"+getUserInformation().get("deptName")+"/OutCargo" +
                "/RemarkReference/");

        getRemarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                View view=activity.getLayoutInflater().inflate(R.layout.dialog_getremark,null);
                RecyclerView recyclerViewIn=view.findViewById(R.id.dialog_getremark_recyclerViewInCargo);
                RecyclerView recyclerViewOut=view.findViewById(R.id.dialog_getremark_recyclerViewOutCargo);
                LinearLayoutManager inManager=new LinearLayoutManager(activity);
                LinearLayoutManager outManager=new LinearLayoutManager(activity);
                recyclerViewOut.setLayoutManager(outManager);
                recyclerViewIn.setLayoutManager(inManager);
                ArrayList<ListRemarkRecyclerView> listIn=new ArrayList<>();
                ArrayList<ListRemarkRecyclerView> listOut=new ArrayList<>();
                for(DataSnapshot data:snapshot.getChildren()){
                    String inNickName="",outNickName="",inRemark="",outRemark="",inDes="",outDes="";
                    if(bl.contains(data.getKey())){
                        ListRemarkReference mList=data.getValue(ListRemarkReference.class);
                        assert mList != null;

                        if(mList.getInCargoNickName()!=null){
                            inNickName=mList.getInCargoNickName();
                            inRemark=mList.getInCargoRemarkValue();
                            inDes=mList.getInCargoDesValue();
                            ArrayList<String> arrInNickName=extractChar(inNickName,',');
                            ArrayList<String> arrInDes=extractChar(inDes,',');
                            ArrayList<String> arrInRemark=extractChar(inRemark,',');

                            for(int i=0;i<arrInNickName.size();i++){
                                ListRemarkRecyclerView mListIn=new ListRemarkRecyclerView(arrInNickName.get(i),arrInDes.get(i),
                                        arrInRemark.get(i));
                                listIn.add(mListIn);
                            }

                        }
                        if(mList.getOutCargoNickName()!=null){
                            outNickName=mList.getOutCargoNickName();
                            outRemark=mList.getOutCargoRemarkValue();
                            outDes=mList.getOutCargoDesValue();
                            ArrayList<String> arrOutNickName=extractChar(outNickName,',');
                            ArrayList<String> arrOutDes=extractChar(outDes,',');
                            ArrayList<String> arrOutRemark=extractChar(outRemark,',');

                            for(int i=0;i<arrOutNickName.size();i++){
                                ListRemarkRecyclerView mListOut=new ListRemarkRecyclerView(arrOutNickName.get(i),arrOutDes.get(i),
                                        arrOutRemark.get(i));
                                listOut.add(mListOut);
                            }
                        }

                    }
                }
                if(listIn.size()>0||listOut.size()>0){
                    RemarkRecyclerViewAdapter inAdapter=new RemarkRecyclerViewAdapter(listIn);
                    RemarkRecyclerViewAdapter outAdapter=new RemarkRecyclerViewAdapter(listOut);
                    recyclerViewOut.setAdapter(outAdapter);
                    recyclerViewIn.setAdapter(inAdapter);
                    AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                    builder.setTitle(bl+":화물에 대한 특이사항")
                            .setView(view)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
public void extractData(String keyPath,String keyValue){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference dataRef=database.getReference(keyPath);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String manageNo = null,desNo = null;
                for(DataSnapshot data:snapshot.getChildren()){
                    if(data.getKey().equals(keyValue)){
                        OutCargoList mList=data.getValue(OutCargoList.class);
                        manageNo= mList.getManagementNo();
                        desNo=mList.getDescription();
//                        int charCheck=0;
//                        ArrayList<String> manageList=new ArrayList<>();
//                        for(int i=0;i<mList.getManagementNo().length();i++){
//                            if(manageNo.charAt(i)==','){
//                                String manageNoEx=manageNo.substring(charCheck,i).replace(",","");
//                                charCheck=i;
//                               manageList.add(manageNoEx);
//                            }
//                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}
public ArrayList<String> extractChar(String data,Character ch){
        int charCheck=0;
        ArrayList<String> extractList=new ArrayList<>();
        for(int i =0;i<data.length();i++){
            if(data.charAt(i)==ch){
                String dataEx=data.substring(charCheck,i).replace(",","");
                charCheck=i;
                extractList.add(dataEx);
            }
        }
        return extractList;
}
public int extractCharCount(String data,Character ch){
        int intCount=0;
        for(int i=0;i<data.length();i++){
            if(data.charAt(i)==ch){
                intCount=intCount+1;
            }
        }
        return intCount;
}

public void searchIncargoData(String key,String value){
    ArrayList<Fine2IncargoList> list=new ArrayList<>();
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    TitleActivity.list=new ArrayList<>();
    DatabaseReference dataRef;
    String refPath="DeptName/"+getUserInformation().get("deptName")+"/InCargo/";

    for(int i=1;i<13;i++){
        String month;
        if(i<10){
            month="0"+i;
        }else{
            month=String.valueOf(i);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, i - 1, 1);
        int monthOfLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int j = 1; j <= monthOfLastDay; j++) {
            String date = null;

            if (j < 10) {
                date = "0" + j;
            } else {
                date = String.valueOf(j);
            }
            dataRef=database.getReference(refPath+month+"월/2021-"+month+"-"+date);
            ValueEventListener listener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data:snapshot.getChildren()){
                        if(!data.getKey().contains("json")){
                            Fine2IncargoList mList=data.getValue(Fine2IncargoList.class);
                            TitleActivity.list.add(mList);
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            Query SortByKey=dataRef.orderByChild(key).equalTo(value);
            SortByKey.addListenerForSingleValueEvent(listener);
//            dataRef.addListenerForSingleValueEvent(listener);
        }


    }

}
  public void getConsigneeListFromWorkingMessage(){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        String getRef="DeptName/"+getUserInformation().get("deptName")+"/WorkingMessage/";
        DatabaseReference databaseReference=database.getReference(getRef);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String consigneeList="";
                String filterList;
                for(DataSnapshot data:snapshot.getChildren()){
                   WorkingMessageList mList=data.getValue(WorkingMessageList.class);
                   if(mList.getConsignee()==null){
                       Map<String,Object> map=new HashMap<>();
                       map.put("consignee","Null");
                       map.put("msg","Npe Checked");
                       DatabaseReference dataRef=database.getReference(getRef+mList.getNickName()+"_"+mList.getTime());
                       dataRef.updateChildren(map);
                   }else{
                       filterList=mList.getConsignee();
                       if(!consigneeList.contains(mList.getConsignee())&&!filterList.equals("근태")&&!filterList.equals("Null")&&filterList.length()<15){
                           consigneeList=consigneeList+mList.getConsignee()+",";
                       }
                       }
                }
                Log.i("TestValue","ConsigneeList sharedPref Checked="+consigneeList);
                SharedPreferences preferences=activity.getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("consigneeList",consigneeList);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


}

public void LocationReg(String refPath){
//        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
//       ArrayList<String> locationArr=new ArrayList<>();
//       locationArr.add("A동");
//       locationArr.add("B동");
//       locationArr.add("C동");
//       locationArr.add("D동");
//
//       String[] locationList=locationArr.toArray(new String[locationArr.size()]);
//       builder.setTitle("LOCATION 등록 창")
//               .setSingleChoiceItems(locationList, 0, new DialogInterface.OnClickListener() {
//                   @Override
//                   public void onClick(DialogInterface dialogInterface, int i) {
//                       String location = null;
//                       switch(i){
//                           case 0: case 2: case 5:
//                               location=locationList[i];
//                                Intent intent=new Intent(activity,LocationA.class);
//                                activity.startActivity(intent);
//
//                               Toast.makeText(activity,locationList[i]+" 로케이션 등록",Toast.LENGTH_SHORT).show();
//                               break;
//                           case 1: case 3: case 4:
//                               dialogInterface.dismiss();
//                               Toast.makeText(activity,locationList[i]+" 로케이션 등록",Toast.LENGTH_SHORT).show();
//                               break;
////                           case 3:
////                               Toast.makeText(activity,locationList[i]+" 로케이션 등록",Toast.LENGTH_SHORT).show();
////                               break;
////
////                           case 4:
////                               Toast.makeText(activity,locationList[i]+" 로케이션 등록",Toast.LENGTH_SHORT).show();
////                               break;
//
//                       }
//                       FirebaseDatabase database=FirebaseDatabase.getInstance();
//                       DatabaseReference remarkRef=database.getReference(refPath);
//                       Map<String,Object> locationValue=new HashMap<>();
//                       locationValue.put("location",location);
//                       remarkRef.updateChildren(locationValue);
//                   }
//               })
//               .show();
                Intent intent=new Intent(activity,Location.class);
                activity.startActivity(intent);


}
}
