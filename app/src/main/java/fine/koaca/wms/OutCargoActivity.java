package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.StringPrepParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

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

public class OutCargoActivity extends AppCompatActivity implements OutCargoListAdapter.OutCargoListAdapterClickListener, ImageViewActivityAdapter.ImageViewClicked {
    FirebaseDatabase database;
    RecyclerView recyclerView;
    ArrayList<OutCargoList> list;
    OutCargoListAdapter adapter;
    String departmentName;

    TextView txtTitle;
    String dateToDay;
    String refPath;

    ArrayList<String> imageViewLists=new ArrayList<>();
    ImageViewActivityAdapter iAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_cargo);

        dateToDay=new SimpleDateFormat("yyyy-MM-dd").format(new Date());


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

        }

        adapter=new OutCargoListAdapter(list,this,this);
        recyclerView.setAdapter(adapter);


        txtTitle=findViewById(R.id.activity_list_outcargo_title);
        txtTitle.setText(dateToDay+" 출고 목록");
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
//                adapter=new OutCargoListAdapter(list,getApplicationContext(),OutCargoActivity.this::itemClicked);
//                recyclerView.setAdapter(adapter);

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
        getOutcargoData(refPath);
        itemClickedDialog();
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
//                adapter=new OutCargoListAdapter(list,getApplicationContext(),OutCargoActivity.this::itemClicked);
//                recyclerView.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        };
        Query sortByDateDatabase=databaseReference.orderByChild("date").equalTo(dateToDay);
        sortByDateDatabase.addListenerForSingleValueEvent(listener);


    }


    public void itemClickedDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        AlertDialog dialog=builder.create();
        builder.setTitle("작업현황 변경사항");
        ArrayList<String> clickValue=new ArrayList<>();
        clickValue.add("사진제외 출고완료 등록");
        clickValue.add("사진포함 출고완료 등록");
        clickValue.add("미출고 등록");

        String[] clickValueList=clickValue.toArray(new String[clickValue.size()]);

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
        imageViewLists=new ArrayList<>();
        queryAllPictures();
        iAdapter=new ImageViewActivityAdapter(imageViewLists,this);
        imageRecyclerView.setAdapter(iAdapter);


    }

    private void intentTitleActivity() {
        Intent intent=new Intent(OutCargoActivity.this,TitleActivity.class);
        startActivity(intent);
    }

    private void intentImageViewActivity() {
        Intent intent=new Intent(this,OutCargoImageViewActivity.class);
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
//            case R.id.outcargoActivity_Allsearch:
//
//                getOutcargoData();
//
//                break;
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
                if(monthOfYear<10){
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

    public ArrayList<String> queryAllPictures(){
//        captureImageList=new ArrayList<ImageViewList>();
        imageViewLists.clear();
        Uri uri =MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=this.getContentResolver().query(uri,projection,null,null, MediaStore.MediaColumns.DATE_ADDED +
                " desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext()){
            String uriI=cursor.getString(columnsDataIndex);

            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Fine/입,출고/Resize");
            String strFile=String.valueOf(file);

            if(uriI.startsWith(strFile)){
//                ImageViewList lists=new ImageViewList(uriI);
                imageViewLists.add(uriI);
            }

        }

        cursor.close();

        return imageViewLists;
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {

    }
}