package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityEquipFacility extends AppCompatActivity implements ImageViewActivityAdapter.ImageViewClicked{
Spinner spName,spContents;
String deptName,efName,process,date,manageContents,nickName;
FirebaseDatabase database;
DatabaseReference databaseReference;
PublicMethod publicMethod;
Button btnDate,btnReg,btnSearch;
TextView txtName,txtDate,txtContent;
ArrayList<String> imageViewLists;
RecyclerView imageRecyclerView,recyclerViewHistory;
ImageViewActivityAdapter iAdapter;
ArrayList<ActivityEquipFacilityList> list;
ActivityEquipFacilityAdapter historyAdapter;

String refPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equip_facility);
        database=FirebaseDatabase.getInstance();
        publicMethod=new PublicMethod(this);
        deptName=publicMethod.getUserInformation().get("deptName");
        nickName=publicMethod.getUserInformation().get("nickName");
        date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        efName="FF02(인천04마1068)";
        manageContents="타이어수리";
        refPath="DeptName/"+deptName+"/EquipNFacility/";

        recyclerViewHistory=findViewById(R.id.activity_equip_facility_recyclerhistory);
        LinearLayoutManager historyManager=new LinearLayoutManager(this);
        recyclerViewHistory.setLayoutManager(historyManager);
        list=new ArrayList<>();
        getHistoryDatabase();
        historyAdapter=new ActivityEquipFacilityAdapter(list);
        recyclerViewHistory.setAdapter(historyAdapter);


        imageRecyclerView = findViewById(R.id.activity_equip_facility_recyclerimageview);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        imageRecyclerView.setLayoutManager(manager);
        PublicMethod pictures = new PublicMethod(this);
        imageViewLists = pictures.getPictureLists();
        iAdapter = new ImageViewActivityAdapter(imageViewLists, this);
        imageRecyclerView.setAdapter(iAdapter);

        txtName=findViewById(R.id.activity_equip_facility_txtName);
        txtDate=findViewById(R.id.activity_equip_facility_txtDate);
        txtContent=findViewById(R.id.activity_equip_facility_txtContent);

        spName=findViewById(R.id.activity_equip_facility_spname);
        spName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityEquipFacility.this);
                EditText editText=new EditText(ActivityEquipFacility.this);
                builder.setTitle("장비,시설물 종류 직접 입력")
                        .setMessage("장비,시설물 종류를 직접 입력후 하단 등록 버튼을 눌러 진행 바랍니다.")
                        .setView(editText)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               efName=editText.getText().toString();
                               txtName.setText(efName);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return true;
            }
        });
        spContents=findViewById(R.id.activity_equip_facility_spcontents);
        spContents.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityEquipFacility.this);
                EditText editText=new EditText(ActivityEquipFacility.this);
                builder.setTitle("장비,시설물 점검사항 항목 직접입력")
                        .setMessage("장비,시설물에 대한 점검사항을 입력후 하단 등록 버튼을 눌러 진행 바랍니다.")
                        .setView(editText)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manageContents=editText.getText().toString();
                                txtContent.setText(manageContents);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return true;
            }
        });

        btnDate=findViewById(R.id.activity_equip_facility_btndate);
//        btnDate.setText(date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityEquipFacility.this);
                DatePicker datePicker=new DatePicker(ActivityEquipFacility.this);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = String.valueOf(monthOfYear+1),day = String.valueOf(dayOfMonth);
                        if(monthOfYear+1<10){
                            month="0"+(monthOfYear+1);
                        }
                        if(dayOfMonth<10){
                            day="0"+dayOfMonth;
                        }
                        date=year+"-"+month+"-"+day;
                    }
                });
                builder.setTitle("장비,시설물 관리항목에 대한 날짜 지정")
                        .setMessage("장비,시설물에 대한 점검사항을 입력후 하단 등록 버튼을 눌러 진행 바랍니다.")
                        .setView(datePicker)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtDate.setText(date);

                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });
    }

    private void getHistoryDatabase() {
        databaseReference=database.getReference(refPath);
        ValueEventListener listener=new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    ActivityEquipFacilityList mList=data.getValue(ActivityEquipFacilityList.class);
                    list.add(mList);
                }
                historyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {

    }
}