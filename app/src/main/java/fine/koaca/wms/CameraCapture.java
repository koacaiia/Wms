package fine.koaca.wms;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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

    String date_today;

    RecyclerView recyclerView;
    ImageViewListAdapter adapter;
    CameraCaptureOutAdapter adapterOut;
    CameraCaptureInAdapter adapterIn;
    ArrayList<ImageViewList> list=new ArrayList<ImageViewList>();
    ArrayList<String> upLoadUriString=new ArrayList<String>();
    SparseBooleanArray imageListSelected=new SparseBooleanArray(0);
    String uploadItem;
    String depotName;
    String nickName;
    String alertDepot;

    static RequestQueue requestQueue;

    ArrayList<OutCargoList> listOut=new ArrayList<>();
    ArrayList<Fine2IncargoList> listIn=new ArrayList<>();

    Button btnPicText;

    AlertDialog dialog;

    RecyclerView recyclerViewIn;
    RecyclerView recyclerViewOut;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        requestPermissions(permission_list,0);
        intentGetItems();

        FirebaseMessaging.getInstance().subscribeToTopic("testUp");


        depotName=getIntent().getStringExtra("depotName");
        nickName=getIntent().getStringExtra("nickName");
        alertDepot=getIntent().getStringExtra("alertDepot");



        date_today=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

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
                captureProcess.setmAutoFocus();

            }
        });

        surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(CameraCapture.this,CameraCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
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

                if(upLoadUriString.size()>5){
                    AlertDialog.Builder builder=new AlertDialog.Builder(CameraCapture.this);
                    builder.setTitle("!사진전송 주의사항")
                            .setMessage("한번에 전송할수 있는 사진은 최대 5장 입니다.")
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
                captureProcess.captureProcess(date_today);

            }
        });

        btnPicText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(CameraCapture.this, WorkingMessageData.class);
                startActivity(intent);
                return true;
            }
        });

    }

    private void sharedInOutCaro() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("입,출고 사진 공유 선택창")
                .setMessage("하단 입,출고 항목을 선택하여 공유할 사진의 항목을 등록 바랍니다.")
                .setPositiveButton("출고", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent=new Intent(CameraCapture.this,OutCargoActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        dialogOutCamera();
                    }
                })
                .setNegativeButton("입고", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(CameraCapture.this,Incargo.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void dialogOutCamera() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceOut=database.getReference("Outcargo2");
        DatabaseReference databaseReferenceIn=database.getReference("Incargo2");



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


        ValueEventListener listenerOut=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    OutCargoList mList=data.getValue(OutCargoList.class);
                    listOut.add(mList);
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
                    Fine2IncargoList mList=data.getValue(Fine2IncargoList.class);
                    listIn.add(mList);
                }
                adapterIn.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };


        Query sortDatabaseByDateOut=databaseReferenceOut.orderByChild("date").equalTo(date_today);
        sortDatabaseByDateOut.addValueEventListener(listenerOut);

        Query sortDatabaseByDateIn=databaseReferenceIn.orderByChild("date").equalTo(date_today);
        sortDatabaseByDateIn.addValueEventListener(listenerIn);



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
        ImageView imageView0=view.findViewById(R.id.dialog_imageView0);
        ImageView imageView1=view.findViewById(R.id.dialog_imageView1);
        ImageView imageView2=view.findViewById(R.id.dialog_imageView2);
        ImageView imageView3=view.findViewById(R.id.dialog_imageView3);
        ImageView imageView4=view.findViewById(R.id.dialog_imageView4);
        switch(upLoadUriString.size()){
            case 1:
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(0))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imageView0.setImageBitmap(resource);
                            }});
                break;
            case 2:
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(0))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView0.setImageBitmap(resource);
                            }
                        });

                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(1))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView1.setImageBitmap(resource);
                            }
                        });
                break;
            case 3:
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(0))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView0.setImageBitmap(resource);
                            }
                        });
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(1))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView1.setImageBitmap(resource);
                            }
                        });
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(2))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView2.setImageBitmap(resource);
                            }
                        });
                break;
            case 4:
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(0))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView0.setImageBitmap(resource);
                            }
                        });
                Glide.with(view).asBitmap()
                    .load(upLoadUriString.get(1))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            imageView1.setImageBitmap(resource);
                        }
                    });
                Glide.with(view).asBitmap()
                    .load(upLoadUriString.get(2))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            imageView2.setImageBitmap(resource);
                        }
                    });
                Glide.with(view).asBitmap()
                    .load(upLoadUriString.get(3))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            imageView3.setImageBitmap(resource);
                        }
                    });
                break;
            case 5:
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(0))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView0.setImageBitmap(resource);
                            }
                        });
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(1))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView1.setImageBitmap(resource);
                            }
                        });
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(2))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView2.setImageBitmap(resource);
                            }
                        });
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(3))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView3.setImageBitmap(resource);
                            }
                        });
                Glide.with(view).asBitmap()
                        .load(upLoadUriString.get(4))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                imageView4.setImageBitmap(resource);
                            }
                        });
                break;
        }

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

        dialogConsignee.setPositiveButton("출고사진 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadItem="OutCargo";
                upCapturePictures("OutCargo",spinner_text.getText().toString());
            }
        });

        dialogConsignee.setNegativeButton("입고사진 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadItem="InCargo";
                upCapturePictures(uploadItem,spinner_text.getText().toString());
            }
        });
        dialogConsignee.setNeutralButton("기타사진 ", new DialogInterface.OnClickListener() {
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
        adapter.notifyDataSetChanged();


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
            String strRef = date_today + "/" + consigneeName+"/"+uploadItem+"/" + nick+System.currentTimeMillis() + ".jpg";
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

        push.sendAlertMessage(alertDepot,nickName,message,"CameraUpLoad");
    }
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(CameraCapture.this);
        builder.setTitle("화면 선택")
                .setPositiveButton("초기화면", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(CameraCapture.this,TitleActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("어플 종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }


    @Override
    public void inAdapterClick(CameraCaptureInAdapter.ListViewHolder listViewHolder, View v, int position) {
        upCapturePictures("InCargo",listIn.get(position).getConsignee());




    }

    @Override
    public void outAdapterClick(CameraCaptureOutAdapter.ListViewHolder listViewHolder, View v, int position) {
        upCapturePictures("OutCargo",listOut.get(position).getConsigneeName());


    }
}