package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.StringPrepParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OutCargoActivity extends AppCompatActivity implements OutCargoListAdapter.OutCargoListAdapterClickListener,
        OutCargoListAdapter.OutCargoListAdapterLongClickListener, ImageViewActivityAdapter.ImageViewClicked {
    FirebaseDatabase database;
    RecyclerView recyclerView;
    ArrayList<OutCargoList> list;
    OutCargoListAdapter adapter;
    String departmentName,nickName,alertDepot;

    TextView txtTitle;
    String dateToDay;
    String refPath;

    ArrayList<String> imageViewLists=new ArrayList<>();
    ArrayList<String> clickedImageViewLists=new ArrayList<>();
    ImageViewActivityAdapter iAdapter;
    SparseBooleanArray clickedArray=new SparseBooleanArray(0);
    FloatingActionButton fltBtn;

    static RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_cargo);

        dateToDay=new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        nickName=getIntent().getStringExtra("nickName");
        alertDepot=getIntent().getStringExtra("alertDepot");

        fltBtn=findViewById(R.id.activity_list_outcargo_flb);
        fltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                upCapturePictures("OutCargo",list.get(0).consigneeName);

            }
        });
        recyclerView=findViewById(R.id.activity_list_outcargo_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        departmentName="Outcargo2";
        database= FirebaseDatabase.getInstance();
        list=new ArrayList<>();

        if((ArrayList<OutCargoList>)getIntent().getSerializableExtra("listOut")==null){
            getOutcargoData();
        }else{
            list=(ArrayList<OutCargoList>)getIntent().getSerializableExtra("listOut");
            pictureUpdate();

        }

        adapter=new OutCargoListAdapter(list,this,this,this);
        recyclerView.setAdapter(adapter);


        txtTitle=findViewById(R.id.activity_list_outcargo_title);
        txtTitle.setText(dateToDay+" 출고 목록");

        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }

    }

    private void getOutcargoData() {

        list.clear();
        DatabaseReference databaseReference=database.getReference(departmentName);
        ValueEventListener listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    OutCargoList mList=data.getValue(OutCargoList.class);
                    list.add(mList);
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        };
        Query sortByDateDatabase=databaseReference.orderByChild("date").equalTo(dateToDay);
        sortByDateDatabase.addListenerForSingleValueEvent(listener);

    }

    @Override
    public void itemClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        refPath=list.get(position).getKeypath();
        String consigneeName=list.get(position).getConsigneeName();
        String dialogTitle=
                consigneeName+"_"+list.get(position).getDescription()+list.get(position).getTotalQty();

        getOutcargoData(refPath);
        itemClickedDialog(consigneeName,dialogTitle);
    }

    private void getOutcargoData(String refPath) {
        list.clear();
        DatabaseReference databaseReference=database.getReference(departmentName);
        ValueEventListener listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    OutCargoList mList=data.getValue(OutCargoList.class);
                    if(mList.getKeypath().equals(refPath)){
                        list.add(mList);
                    }

                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        };
        Query sortByDateDatabase=databaseReference.orderByChild("date").equalTo(dateToDay);
        sortByDateDatabase.addListenerForSingleValueEvent(listener);
    }

    public void itemClickedDialog(String consigneeName, String dialogTitle){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        AlertDialog dialog=builder.create();
        builder.setTitle("출고현황 변경사항");
        ArrayList<String> clickValue=new ArrayList<>();
        clickValue.add("사진제외 출고완료 등록");
        clickValue.add("사진포함 출고완료 등록");
        clickValue.add("미출고 등록");
        clickValue.add("신규출고 항목으로 공유");

        String[] clickValueList=clickValue.toArray(new String[clickValue.size()]);
        builder.setTitle(dialogTitle+" 출고");
        builder.setSingleChoiceItems(clickValueList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        updateValue("완");
                        intentTitleActivity();
                        break;
                    case 1:
                        updateValue("완");
//                        intentImageViewActivity();
                        pictureUpdate();
                        break;
                    case 2:
                        updateValue("미");
                        intentTitleActivity();
                        break;
                    case 3:
                        PublicMethod publicMethod=new PublicMethod(OutCargoActivity.this);
                        publicMethod.putNewDataUpdateAlarm(nickName,alertDepot,dialogTitle+" 신규 등록",consigneeName,"OutCargo",requestQueue);

                        break;
                }
                dialog.dismiss();
            }
        })
                .show();
    }

    private void pictureUpdate() {
        RecyclerView imageRecyclerView=findViewById(R.id.activity_list_outcargo_imageviewRe);
        GridLayoutManager manager=new GridLayoutManager(this,2);
        imageRecyclerView.setLayoutManager(manager);

        PublicMethod getImageLists=new PublicMethod(this);
        imageViewLists=getImageLists.getPictureLists();

        iAdapter=new ImageViewActivityAdapter(imageViewLists,this);
        imageRecyclerView.setAdapter(iAdapter);

    }

    public void intentTitleActivity() {
        Intent intent=new Intent(OutCargoActivity.this,TitleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void initIntent() {
        Intent intent=new Intent(OutCargoActivity.this,OutCargoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void updateValue(String updateValue){
        DatabaseReference dataRef=database.getReference(departmentName+"/"+refPath);

        Map<String,Object> value=new HashMap<>();
        value.put("workprocess",updateValue);
        dataRef.updateChildren(value);

        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.outcargoactivity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.outcargoActivity_date:
                dateAlertDialog();

                break;

        }
        return true;
    }

    private void dateAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        DatePicker datePicker=new DatePicker(this);
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month,day;
                if((monthOfYear+1)<10){
                    month="0"+(monthOfYear+1);
                }else{
                    month=String.valueOf(monthOfYear+1);
                }
                if(dayOfMonth<10){
                    day="0"+dayOfMonth;
                }else{
                    day=String.valueOf(dayOfMonth);
                }
                dateToDay=year+"-"+month+"-"+day;
                Toast.makeText(getApplicationContext(),dateToDay+"을 지정",Toast.LENGTH_SHORT).show();
            }

        });
        builder.setTitle("검색일 설정")
                .setView(datePicker)
                .setPositiveButton("지정일 검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtTitle.setText(dateToDay+" 모든 출고 목록");
                        getOutcargoData();
                    }
                })
                .setNegativeButton("당일 검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dateToDay=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        txtTitle.setText(dateToDay+" 모든 출고 목록");
                        getOutcargoData();
                    }
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }



    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        String uriString=imageViewLists.get(position);
       if(clickedArray.get(position,false)){
           clickedArray.delete(position);
           clickedImageViewLists.remove(uriString);
       }else{
           clickedArray.put(position,true);
           clickedImageViewLists.add(uriString);
       }
       if(clickedImageViewLists.size()>5){
           AlertDialog.Builder builder=new AlertDialog.Builder(this);
           builder.setTitle("!사진전송 주의사항")
                   .setMessage("한번에 전송할수 있는 사진은 최대 5장 입니다.")
                   .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Toast.makeText(getApplicationContext(),"사진을 다신 선택 하기 바랍니다.",Toast.LENGTH_SHORT).show();

                       }
                   }).show();
       }
       String clickedPictureCount="("+clickedImageViewLists.size()+"장 선택)";
        txtTitle.setText(dateToDay+" 출고 목록"+clickedPictureCount);
    }

    public void upCapturePictures(String inoutItems,String consigneeName){
        int arrsize=clickedImageViewLists.size();

        SharedPreferences sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nick=sharedPreferences.getString("nickName","Fine");

        String message=consigneeName+"_"+inoutItems+"_사진 업로드";
        CaptureProcess captureProcess=new CaptureProcess(this);
        String activityName=this.getClass().getSimpleName();
        for(int i=0;i<arrsize;i++){
            Uri uri = Uri.fromFile(new File(clickedImageViewLists.get(i)));
            String strRef = dateToDay + "/" + consigneeName+"/"+inoutItems+"/" + nick+System.currentTimeMillis() + ".jpg";
            captureProcess.firebaseCameraUpLoad(uri, consigneeName, inoutItems, nick, message,strRef,i,arrsize,activityName);
        }



    }

    public void sendMessage(String message){

        PushFcmProgress push=new PushFcmProgress(requestQueue);

        push.sendAlertMessage(alertDepot,nickName,message,"CameraUpLoad");
    }

    public void messageIntent() {
        Intent intent=new Intent(this,WorkingMessageData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(OutCargoActivity.this);
        builder.setTitle("화면 선택")
                .setPositiveButton("초기화면", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(OutCargoActivity.this,TitleActivity.class);
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
    public void itemLongClicked(OutCargoListAdapter.ListView listView, View v, int position) {

    }
}