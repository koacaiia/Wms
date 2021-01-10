package fine.koaca.wms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    CalendarPick calendarPick=new CalendarPick();
    String date_today;

    RecyclerView recyclerView;
    ImageViewListAdapter adapter;
    ArrayList<ImageViewList> list=new ArrayList<ImageViewList>();
    ArrayList<String> upLoadUriString=new ArrayList<String>();
    SparseBooleanArray imageListSelected=new SparseBooleanArray(0);
    private String[] consignee_list;
    String uploadItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        requestPermissions(permission_list,0);
        intentGetItems();
        calendarPick.CalendarCall();
        date_today=calendarPick.date_today;


        btn_capture=findViewById(R.id.btn_textureView_Capture);
        captureProcess=new CaptureProcess(this);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             captureProcess.captureProcess(date_today);
             list=captureProcess.captureImageList;


            }
        });

        btn_capture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                captureProcess.downLoadingOnlyImage();
                    list=captureProcess.queryAllPictures();
                adapter=new ImageViewListAdapter(list);
                adapter.notifyDataSetChanged();
                 recyclerView.setAdapter(adapter);

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
                Log.i("koacaiia","itemArrayList");
                adapter.notifyItemChanged(position);


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
        String[] items_consignee={"M&F", "SPC", "공차", "케이비켐", "BNI","기타","스위치코리아","서강비철", "스위치코리아","한큐한신","하랄코"};
        AlertDialog.Builder dialogConsignee=new AlertDialog.Builder(this);
        dialogConsignee.setTitle("항목 선택");
        View view=getLayoutInflater().inflate(R.layout.spinnerlist_consignee,null);
        ArrayAdapter<String> consigneeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                items_consignee);
        Spinner spinner_consignee=view.findViewById(R.id.workmessage_spinner);
        EditText spinner_edit=view.findViewById(R.id.spinner_search_inpurDate);
        TextView spinner_text=view.findViewById(R.id.workmessage_text);

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
        spinner_consignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               spinner_text.setText(items_consignee[position]);
               spinner_edit.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_consignee.setAdapter(consigneeAdapter);
        dialogConsignee.setView(view);
        dialogConsignee.setMessage("하단의 업체명 선택후 전송 하기랍니다."+"\n"+"화주명 등록 여부 다시 한번 확인 바랍니다.");

        dialogConsignee.setPositiveButton("출고사진 UpLoad", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int arrsize=upLoadUriString.size();
                uploadItem="OutCargo";
                for(int i=0;i<arrsize;i++){
                    Uri uri=Uri.fromFile(new File(upLoadUriString.get(i)));
                    captureProcess.firebaseCameraUpLoad(uri,date_today,spinner_text.getText().toString(),uploadItem);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        dialogConsignee.setNegativeButton("입고사진 UpLoad", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int arrsize=upLoadUriString.size();
                uploadItem="InCargo";
                for(int i=0;i<arrsize;i++){
                    Uri uri=Uri.fromFile(new File(upLoadUriString.get(i)));
                    captureProcess.firebaseCameraUpLoad(uri,date_today,spinner_text.getText().toString(), uploadItem);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        dialogConsignee.setNeutralButton("기타사진 UpLoad", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int arrsize=upLoadUriString.size();
                uploadItem="Etc";
                for(int i=0;i<arrsize;i++){
                    Uri uri=Uri.fromFile(new File(upLoadUriString.get(i)));
                    captureProcess.firebaseCameraUpLoad(uri,date_today,spinner_text.getText().toString(), uploadItem);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        dialogConsignee.show();

    }


}