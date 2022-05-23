package fine.koaca.wms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CameraCapture extends AppCompatActivity implements CameraCaptureInAdapter.CameraCaptureInAdapterClick, CameraCaptureOutAdapter.CameraCaptureOutAdapterClick {
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    String [] permission_list={Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    TextView camera_date;
    TextView camera_bl;
    TextView camera_count;
    TextView camera_des;

    String intent_camera_date;
    String intent_camera_bl;
    String intent_camera_count;
    String intent_camera_des;

    CaptureProcess captureProcess;

    String dateToday;

    RecyclerView recyclerView;
    ImageViewListAdapter adapter;
    CameraCaptureOutAdapter adapterOut;
    CameraCaptureInAdapter adapterIn;
    ArrayList<ImageViewList> list=new ArrayList<ImageViewList>();
    ArrayList<String> upLoadUriString=new ArrayList<String>();
    SparseBooleanArray imageListSelected=new SparseBooleanArray(0);
    String uploadItem;
    String deptName;
    String nickName;


    static RequestQueue requestQueue;

    ArrayList<OutCargoList> listOut=new ArrayList<>();
    ArrayList<Fine2IncargoList> listIn=new ArrayList<>();

    Button btnPicText;

    AlertDialog dialog;

    RecyclerView recyclerViewIn;
    RecyclerView recyclerViewOut;

    FirebaseDatabase database;
    Activity activity;
    @SuppressLint({"SimpleDateFormat", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        requestPermissions(permission_list,0);
        intentGetItems();

        database=FirebaseDatabase.getInstance();

        activity=this;

        PublicMethod publicMethod=new PublicMethod(this);
        nickName=publicMethod.getUserInformation().get("nickName");
        deptName=publicMethod.getUserInformation().get("deptName");


        dateToday=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        btnPicText=findViewById(R.id.camera_textView_piccount);
        captureProcess=new CaptureProcess(this,adapter);

        camera_date=findViewById(R.id.camera_textView_date);
        camera_date.setText(intent_camera_date);
        camera_bl=findViewById(R.id.camera_textView_bl);
        camera_bl.setText(intent_camera_bl);
        camera_count=findViewById(R.id.camera_textView_count);
        camera_count.setText(intent_camera_count);
        camera_des=findViewById(R.id.camera_textView_des);
        camera_des.setText(intent_camera_des);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView=findViewById(R.id.surfaceView);
        surfaceHolder=surfaceView.getHolder();
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                captureProcess.captureProcess(dateToday);
            }
        });

        surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Intent intent=new Intent(CameraCapture.this,CameraCapture.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);

                captureProcess.setmAutoFocus();
                return true;
            }
        });


        recyclerView=findViewById(R.id.captureImageList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        list=captureProcess.queryAllPictures(dateToday);
        adapter=new ImageViewListAdapter(list);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        adapter.onListItemSelected(new OnListItemSelectedInterface() {
            @Override
            public void onItemClick(ImageViewListAdapter.ListViewHolder holder, View view, int position) {

                String uriString=list.get(position).getUriName();


                if(imageListSelected.get(position, false)){
                    imageListSelected.delete(position);
                    upLoadUriString.remove(uriString);

                }else{
                    imageListSelected.put(position,true);
                    upLoadUriString.add(uriString);

                }

                if(upLoadUriString.size()>7){
                    AlertDialog.Builder builder=new AlertDialog.Builder(CameraCapture.this);
                    builder.setTitle("!사진전송 주의사항")
                            .setMessage("한번에 전송할수 있는 사진은 최대 7장 입니다.")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   Toast.makeText(getApplicationContext(),"사진을 다신 선택 하기 바랍니다.",Toast.LENGTH_SHORT).show();
                                    adapter.clearSelectedItem();
                                }
                            }).show();
                }

                btnPicText.setText(upLoadUriString.size()+" 개의 사진이 선택 되었습니다."+"\n"+"해당 버튼길게 누르면 메세지창으로 넘어 갑니다."+"\n"+
                        "사진 리스트 길게 누르면 공유선택으로 전환 됩니다.");
               btnPicText.setTextSize(12);
            }
        });
        adapter.onListItemLongSelectedInterface(new OnListItemLongSelectedInterface() {
            @Override
            public void onLongClick(ImageViewListAdapter.ListViewHolder holder, View view, int position) {
                dialogOutCamera();
            }
        });

        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        btnPicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                captureProcess.captureProcess(dateToday);

            }
        });

        btnPicText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Intent intent=new Intent(CameraCapture.this, WorkingMessageData.class);
//                startActivity(intent);
                getStorageUri();
                return true;
            }
        });

    }


    private void dialogOutCamera() {

        DatabaseReference databaseReferenceOut=
                database.getReference("DeptName/" + deptName + "/" +"OutCargo" + "/" +dateToday.substring(5,7) + "월/" +dateToday);
        DatabaseReference databaseReferenceIn=
                database.getReference("DeptName/" + deptName + "/" +"InCargo" + "/" +dateToday.substring(5,7) + "월/" +dateToday);
        View view=getLayoutInflater().inflate(R.layout.camera_upload_picture_adapter,null);

        recyclerViewIn=view.findViewById(R.id.capture_adapter_in);
        recyclerViewOut=view.findViewById(R.id.capture_adapter_out);
        LinearLayoutManager managerIn=new LinearLayoutManager(this);
        LinearLayoutManager managerOut=new LinearLayoutManager(this);
        recyclerViewIn.setLayoutManager(managerIn);
        recyclerViewOut.setLayoutManager(managerOut);
        adapterOut=new CameraCaptureOutAdapter(listOut,this);
        adapterIn=new CameraCaptureInAdapter(listIn,this);
        recyclerViewIn.setAdapter(adapterIn);
        recyclerViewOut.setAdapter(adapterOut);


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("사진전송 선택 창")
                .setView(view)
                .show();

        dialog=builder.create();
        Button btnInit=view.findViewById(R.id.capture_adapter_btnInit);

        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(CameraCapture.this,TitleActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        Button btnIn=view.findViewById(R.id.capture_adapter_btnIn);
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(CameraCapture.this,Incargo.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        Button btnOut=view.findViewById(R.id.capture_adapter_btnOut);
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(CameraCapture.this,OutCargoActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



        ValueEventListener listenerOut=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    String keyValue=data.getKey();
                    if(!keyValue.equals("json 등록시 덥어쓰기 바랍니다")) {
                        OutCargoList mList = data.getValue(OutCargoList.class);
                        if (!mList.getWorkprocess().equals("완")) {
                            listOut.add(mList);
                        }
                    }
                }
               adapterOut.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        ValueEventListener listenerIn= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    String keyValue=data.getKey();
                    if(!keyValue.equals("json 등록시 덥어쓰기 바랍니다")) {
                        Fine2IncargoList mList = data.getValue(Fine2IncargoList.class);
                        if (!mList.getContainer40().equals("0") || !mList.getContainer20().equals("0") || !mList.getLclcargo().equals("0")) {
                            listIn.add(mList);
                        }
                    }
                }
                adapterIn.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
       databaseReferenceIn.addListenerForSingleValueEvent(listenerIn);
       databaseReferenceOut.addListenerForSingleValueEvent(listenerOut);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int result:grantResults){
            if(result== PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        captureProcess.preViewProcess();
    }

    public void intentGetItems(){
        Intent intent=getIntent();
        intent_camera_date=intent.getStringExtra("date");
        intent_camera_bl=intent.getStringExtra("bl");
        intent_camera_count=intent.getStringExtra("count");
        intent_camera_des=intent.getStringExtra("des");
    }



    public void upCapturePictures(String inoutItems,String consigneeName){
        int arrsize=upLoadUriString.size();
        uploadItem=inoutItems;
        SharedPreferences sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nick=sharedPreferences.getString("nickName","Fine");

        String message=consigneeName+"_"+uploadItem+"_사진 업로드";
        String activityName=this.getClass().getSimpleName();
        for(int i=0;i<arrsize;i++){
            Uri uri = Uri.fromFile(new File(upLoadUriString.get(i)));
            String strRef = dateToday + "/" + consigneeName+"/"+uploadItem+"/" + nick+System.currentTimeMillis() + ".jpg";
            captureProcess.firebaseCameraUpLoad(uri, consigneeName, uploadItem, nick, message,strRef,i,arrsize, activityName);
        }



    }
    public void messageIntent(){
        Intent intent=new Intent(CameraCapture.this,WorkingMessageData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void initIntent(){
        Intent intent=new Intent(CameraCapture.this,CameraCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void sendMessage(String message){

        PushFcmProgress push=new PushFcmProgress(requestQueue);

        push.sendAlertMessage(deptName,nickName,message,"CameraUpLoad");

    }
    @Override
    public void onBackPressed() {

        PublicMethod publicMethod=new PublicMethod(this);
        publicMethod.intentSelect();
    }


    @Override
    public void inAdapterClick(CameraCaptureInAdapter.ListViewHolder listViewHolder, View v, int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(upLoadUriString.size()+" 장의 사진을 "+listIn.get(position).getConsignee()+"_"+listIn.get(position).getContainer()+
                "컨테이너 " +
                "진입으로 등록 " +
                "진행 합니다."+
                "\n"+"내용이 맞으면 하단 확인버튼으로 공유 바랍니다.")

                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String refPath=listIn.get(position).getKeyValue();
                        DatabaseReference databaseReference=
                                database.getReference("DeptName/"+deptName+"/"+"InCargo/"+refPath.substring(5,7)+"월/"+refPath.substring(0,10)+"/"+refPath);
                        Map<String,Object> value=new HashMap<>();
                        value.put("working","컨테이너 진입");
                        databaseReference.updateChildren(value);
                        PublicMethod publicMethod=new PublicMethod(CameraCapture.this,upLoadUriString);
                        publicMethod.upLoadPictures(nickName,listIn.get(position).getConsignee(),"InCargo",
                                listIn.get(position).getKeyValue(),deptName);
                        dialog.cancel();
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

    @Override
    public void outAdapterClick(CameraCaptureOutAdapter.ListViewHolder listViewHolder, View v, int position) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(upLoadUriString.size()+" 장의 사진을 "+listOut.get(position).getConsigneeName()+"_"+listOut.get(position).getTotalQty()+
                "건 출고완료로 등록 진행 합니다." +"\n"+"내용이 맞으면 하단 확인버튼으로 공유 바랍니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String refPath=listOut.get(position).getKeypath();
                        DatabaseReference databaseReference=
                                database.getReference("DeptName/"+deptName+"/"+"OutCargo/"+refPath.substring(5,7)+"월/"+refPath.substring(0,10)+"/"+refPath);
                        Map<String,Object> value=new HashMap<>();
                        value.put("workprocess","완");
                        databaseReference.updateChildren(value);
                        PublicMethod publicMethod=new PublicMethod(CameraCapture.this,upLoadUriString);
                        publicMethod.upLoadPictures(nickName,listOut.get(position).getConsigneeName(),"OutCargo",refPath,
                                deptName);
                        dialog.cancel();
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
    public void getStorageUri(){
        FirebaseStorage storage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
        String refPath=
                deptName+"/"+"2021-08-05"+"/"+"InCargo"+"/"+"2021-08-05__몬 월남쌈(원형16cm) 200g_세계 83차_";
        StorageReference storageReference=storage.getReference().child("image/"+refPath);
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
            }
        });
    }
}