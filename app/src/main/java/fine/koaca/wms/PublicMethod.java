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
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

                ArrayList<String> intentValue=new ArrayList<>();
                intentValue.add("연차,반차,휴가자 등록,조회");
                intentValue.add("출근 인원 등록,조회");
                String[] intentValueList=intentValue.toArray(new String[intentValue.size()]);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("근태 관련 화면 선택창")
                        .setSingleChoiceItems(intentValueList,1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                switch(which){
                                    case 0:
                                        dialog.cancel();
                                        intent=new Intent(activity,AnnualLeave.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        activity.startActivity(intent);
                                        break;
                                    case 1:
                                        dialog.cancel();
                                        intent=new Intent(activity,ActivityWorkingStaff.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity.startActivity(intent);
                                        break;
                                }
                            }
                        })
                        .show();


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
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        final String[] pltS = new String[1];
        final String[] inOut = new String[1];
        final int[] pltQty = new int[1];
        View view=activity.getLayoutInflater().inflate(R.layout.dialog_plt_reg,null);
        TextView pltTxt=view.findViewById(R.id.plt_txtTitle);
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
                        pltQty[0] =Integer.parseInt(pltEdit.getText().toString());
                        String pltDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        SharedPreferences sharedPreferences=activity.getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);
                        String deptName=sharedPreferences.getString("deptName",null);
                        String upLoadKeyValue=nickName+"_"+pltDate+"_"+pltS[0]+pltQty[0]+"장_"+inOut[0];

                        DatabaseReference pltRef=FirebaseDatabase.getInstance().getReference("DeptName/"+deptName+
                                "/PltManagement/"+consigneeName+"/"+pltS[0]+"/"+upLoadKeyValue);
                        Map<String,Object> value=new HashMap<>();
                        value.put("nickName",nickName);
                        value.put("date",pltDate);
                        value.put("keyValue",upLoadKeyValue);
                        value.put("bl",bl);
                        value.put("des",des);
                        switch(inOut[0]){
                            case "In":
                                value.put("inQty",pltQty[0]);
                                value.put("outQty",0);
                                break;
                            case "Out":
                                value.put("inQty",0);
                                value.put("outQty",pltQty[0]);
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
                    Log.i("TestValue","consigneeName:::"+consigneeName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return consigneeList;
    }

    public ArrayList<String> getPictureListsApplyThread(String sort){
        ArrayList<String> imageViewLists=new ArrayList<>();
        Uri uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=activity.getContentResolver().query(uri,projection,null,null,MediaStore.MediaColumns.DATE_ADDED+" desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while(cursor.moveToNext()){
            String uriI=cursor.getString(columnsDataIndex);
            File file=new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
            String strFile=String.valueOf(file);
            if (uriI.startsWith(strFile)) {

                imageViewLists.add(uriI);
            }
        }
        cursor.close();
        return imageViewLists;
    }
}
