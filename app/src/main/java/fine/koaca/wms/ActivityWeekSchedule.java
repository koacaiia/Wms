package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityWeekSchedule extends AppCompatActivity {
    RecyclerView recyclerViewWeekSchedule;
    FirebaseDatabase firebaseDatabase;
    ArrayList<ListWeekSchedule> listWeekSchedule=new ArrayList<>();
    ArrayList<String> dateList=new ArrayList<>();
    String deptName,nickName,dateMonth;
    AdapterActivityWeekScheduleRecyclerViewEx adapterActivityWeekScheduleRecyclerViewEx;
    int startDay,endDay;
    Calendar calendar;
    String[] dateTerms={"ThisWeek","Tomorrow","ThisMonth","LastWeek","NextWeek","LastMonth","NextMonth","ThisYear"};
    Spinner spinner;
    Button btnDateTerms;
    String strDateTerms;
    ArrayList<String> consigneeList=new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_schedule);
        recyclerViewWeekSchedule=findViewById(R.id.activity_week_schedule_RecyclerViewEx);
        PublicMethod publicMethod=new PublicMethod(this);

        spinner=findViewById(R.id.activity_week_schedule_spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,dateTerms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              strDateTerms=dateTerms[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnDateTerms=findViewById(R.id.activity_week_schedule_btnSelectDate);
        btnDateTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startDay=publicMethod.getStartDayEndDay(strDateTerms).get("startDay");
                String endDay=publicMethod.getStartDayEndDay(strDateTerms).get("endDay");
                assert startDay != null;
                assert endDay != null;
                getWeekScheduleData(startDay,endDay);
            }
        });
        deptName=publicMethod.getUserInformation().get("deptName");
        nickName=publicMethod.getUserInformation().get("nickName");
        dateMonth=new SimpleDateFormat("MM").format(new Date());
        firebaseDatabase=FirebaseDatabase.getInstance();
        String startDay=publicMethod.getStartDayEndDay(dateTerms[0]).get("startDay");
        String endDay=publicMethod.getStartDayEndDay(dateTerms[0]).get("endDay");

        getWeekScheduleData(startDay,endDay);
        adapterActivityWeekScheduleRecyclerViewEx=new AdapterActivityWeekScheduleRecyclerViewEx(listWeekSchedule,dateList,
                consigneeList,this,this);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerViewWeekSchedule.setLayoutManager(manager);
        recyclerViewWeekSchedule.setAdapter(adapterActivityWeekScheduleRecyclerViewEx);
            }

    private void getWeekScheduleData(String startDay, String endDay) {
        listWeekSchedule.clear();
        dateList.clear();
        consigneeList.clear();
        int intMonthS=Integer.parseInt(startDay.substring(5,7));
        int intMonthE=Integer.parseInt(endDay.substring(5,7));
        for(int i=intMonthS;i<=intMonthE;i++){
            String month;
            if(i<10){
                month="0"+i;
            }else{
                month=String.valueOf(i);
            }
            String refPath="DeptName/"+deptName+"/InCargo/"+month+"월/";
            DatabaseReference databaseRef=firebaseDatabase.getReference(refPath);
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   for(DataSnapshot data:snapshot.getChildren()){
                        DatabaseReference ref=firebaseDatabase.getReference(refPath+data.getKey());
                        ValueEventListener listener=new ValueEventListener() {
                           @SuppressLint("NotifyDataSetChanged")
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               
                               for(DataSnapshot inData:snapshot.getChildren()){
                                   int con40=0;
                                   int con20=0;
                                   int cargo=0;
                                   ListWeekSchedule mData=inData.getValue(ListWeekSchedule.class);
                                   String consigneeName=mData.getConsignee();
                                   String dateValue=mData.getDate();
                                   if(!inData.getKey().contains("json")){
                                       assert mData != null;
                                       if(!dateList.contains(mData.getDate())){
                                           dateList.add(mData.getDate());
                                           }
                                       if(!consigneeList.contains(mData.getConsignee())){
                                           consigneeList.add(mData.getConsignee());
                                       }
                                       mData=new ListWeekSchedule(consigneeName,mData.getContainer40(),
                                               mData.getContainer20(),mData.getLclcargo(),
                                               mData.getDate(),"","");
                                   }
                                   listWeekSchedule.add(mData);
                               }

                               adapterActivityWeekScheduleRecyclerViewEx.notifyDataSetChanged();
                           }
                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       };
                       Query refQ=ref.orderByChild("date").startAt(startDay).endAt(endDay);

                       refQ.addListenerForSingleValueEvent(listener);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getWeekScheduleData(int startDay,int endDay) {
        listWeekSchedule=new ArrayList<>();
        String strMonth;

        for(int iMonth=startDay;iMonth<=endDay;iMonth++){
            if(iMonth<10){
                strMonth="0"+iMonth;
            }else{
                strMonth=String.valueOf(iMonth);
            }
            String refPath="DeptName/"+deptName+"/InCargo/"+strMonth+"월";

            for(int i=1;i<32;i++){
                String day;
                if(i<10){
                    day="0"+i;
                }else{
                    day=String.valueOf(i);
                }
                DatabaseReference databaseReference=firebaseDatabase.getReference(refPath+"/2022-"+strMonth+"-"+day);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data:snapshot.getChildren()){
                            if(!data.getKey().contains("json")){
                                ListWeekSchedule mData=data.getValue(ListWeekSchedule.class);
                                listWeekSchedule.add(mData);
                            }

                        }
                        adapterActivityWeekScheduleRecyclerViewEx.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
}