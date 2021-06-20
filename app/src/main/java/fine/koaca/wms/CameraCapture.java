package fine.koaca.wms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CameraCapture extends AppCompatActivity
{
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
    String selectedItem;

    FloatingActionButton btn_capture;
    FloatingActionButton btn_share;

    CaptureProcess captureProcess;

    String date_today;

    RecyclerView recyclerView;
    ImageViewListAdapter adapter;
    ArrayList<ImageViewList> list=new ArrayList<ImageViewList>();
    ArrayList<String> upLoadUriString=new ArrayList<String>();
    SparseBooleanArray imageListSelected=new SparseBooleanArray(0);
    String uploadItem;
    String depotName;
    String nickName;
    String alertDepot;

    static RequestQueue requestQueue;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        requestPermissions(permission_list,0);
        intentGetItems();

        FirebaseMessaging.getInstance().subscribeToTopic("testUp");

        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }

        depotName=getIntent().getStringExtra("depotName");
        nickName=getIntent().getStringExtra("nickName");
        alertDepot=getIntent().getStringExtra("alertDepot");



        date_today=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());


        btn_capture=findViewById(R.id.btn_textureView_Capture);
        captureProcess=new CaptureProcess(this,adapter);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureProcess.captureProcess(date_today);
//                list=captureProcess.queryAllPictures();
//                Log.i("koacaiia","adapter Capture image size"+list.size());
//                adapter.notifyDataSetChanged();
//                queryAllList(captureImageList);

            }
        });

        btn_capture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                captureProcess.downLoadingOnlyImage();
               adapter.clearSelectedItem();

                return true;
            }
        });
        btn_share=findViewById(R.id.fabshare);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                consigneeSelected();
            }
        });
        btn_share.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(CameraCapture.this,WorkingMessageData.class);
                startActivity(intent);
                return true;
            }
        });


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
                captureProcess.setmAutoFocus();

            }
        });


        recyclerView=findViewById(R.id.captureImageList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        list=captureProcess.queryAllPictures();
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

            }
        });


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


    public void consigneeSelected(){
        AlertDialog.Builder dialogConsignee=new AlertDialog.Builder(this);
        dialogConsignee.setTitle("항목 선택");
        View view=getLayoutInflater().inflate(R.layout.spinnerlist_consignee,null);
        ArrayAdapter<String> consigneeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                Incargo.shared_consigneeList);
        Spinner spinner_spinner=view.findViewById(R.id.capture_spinner_consignee);
        EditText spinner_edit=view.findViewById(R.id.capture_edit_consignee);
        TextView spinner_text=view.findViewById(R.id.capture_text_consignee);


        spinner_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner_text.setText(spinner_edit.getText().toString());
            }
        });
        spinner_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(spinner_edit.getWindowToken(),0);
                    spinner_text.setText(spinner_edit.getText().toString());
                    return true;
                }
                return false;
            }
        });
        spinner_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_text.setText(Incargo.shared_consigneeList[position]);
                spinner_edit.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner_text.setText("Etc");
            }
        });


        spinner_spinner.setAdapter(consigneeAdapter);
        dialogConsignee.setView(view);
        dialogConsignee.setMessage("하단의 업체명 선택후 전송 하기랍니다."+"\n"+"화주명 등록 여부 다시 한번 확인 바랍니다.");

        dialogConsignee.setPositiveButton("출고사진 UpLoad", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadItem="OutCargo";
                upCapturePictures("OutCargo",spinner_text.getText().toString());
            }
        });

        dialogConsignee.setNegativeButton("입고사진 UpLoad", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadItem="InCargo";
                upCapturePictures(uploadItem,spinner_text.getText().toString());
            }
        });
        dialogConsignee.setNeutralButton("기타사진 UpLoad", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadItem="Etc";
                upCapturePictures(uploadItem,spinner_text.getText().toString());
            }
        });

        dialogConsignee.show();

    }
    public void queryAllList(ArrayList<ImageViewList> captureImageList){

        list=captureImageList;
        Log.i("koacaiia","adapter Capture image size"+list.size());
        adapter.notifyDataSetChanged();


    }
    public void upCapturePictures(String inoutItems,String consigneeName){
        int arrsize=upLoadUriString.size();
        uploadItem=inoutItems;
        SharedPreferences sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nick=sharedPreferences.getString("nickName","Fine");

        String message=consigneeName+"_"+uploadItem+"_사진 업로드";
        for(int i=0;i<arrsize;i++){
            Uri uri = Uri.fromFile(new File(upLoadUriString.get(i)));
            String strRef = date_today + "/" + consigneeName+"/"+uploadItem+"/" + System.currentTimeMillis() + ".jpg";
            captureProcess.firebaseCameraUpLoad(uri, consigneeName, uploadItem, nick, message,strRef,i,arrsize);
//            if(i==arrsize-1){
//                Intent intent=new Intent(CameraCapture.this,WorkingMessageData.class);
//                startActivity(intent);
//
//            }
        }
        PushFcmProgress push=new PushFcmProgress(requestQueue);
        push.sendAlertMessage(alertDepot,nickName,message,"CameraUpLoad");

    }
    public void initIntent(){
        Intent intent=new Intent(CameraCapture.this,WorkingMessageData.class);
        startActivity(intent);
    }



}