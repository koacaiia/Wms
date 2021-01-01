package fine.koaca.wms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraCapture extends AppCompatActivity {
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    String [] permission_list={Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
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

    CaptureProcess captureProcess;

    CalendarPick calendarPick=new CalendarPick();
    String date_today;



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

            }
        });

        btn_capture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                 captureProcess.getImageDown_temp();
//                captureProcess.listAllFiles();

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

        surfaceView=findViewById(R.id.surfaceView);
        surfaceHolder=surfaceView.getHolder();
        surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                outCargoSelect();
                return true;
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

    public void outCargoSelect(){
        String[] items_cargo = {"코만푸드", "M&F", "SPC", "공차", "케이비켐", "BNI","기타","스위치코리아","서강비철", "스위치코리아","한큐한신","하랄코"};
        int items_length=items_cargo.length-5;
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        EditText editText_etc=new EditText(this);
        builder.setTitle("출고사진 항목선택");


        ArrayAdapter<String> cargoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items_cargo);
        cargoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner_outcargo=new Spinner(this);
        spinner_outcargo.setAdapter(cargoAdapter);
        builder.setView(spinner_outcargo);

//        builder.setView(editText_etc);
//        builder.setSingleChoiceItems(items_cargo, items_length, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                selectedItem=items_cargo[which].toString();
//                editText_etc.setText(selectedItem);
//
//            }
//        });

        spinner_outcargo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem=items_cargo[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setPositiveButton("출고사진", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String timeStamp1 = new SimpleDateFormat("yyyy년MM월dd일E요일").format(new Date());
                String timeStamp2 = new SimpleDateFormat("a_HH시mm분ss초").format(new Date());
                camera_date.setText(timeStamp1);
                camera_bl.setText(timeStamp2);
                camera_count.setText(selectedItem);
                camera_des.setText("출고");

            }
        });
        builder.create();
        builder.show();
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();

    }


}