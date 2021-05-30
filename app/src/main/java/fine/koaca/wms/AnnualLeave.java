package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class
AnnualLeave extends AppCompatActivity implements AnnualListAdapter.AnnualOnClickListener, AnnualListAdapter.AnnualLongClickListener, AnnualListAdapter.AnnualTxtClickListener {

    RecyclerView recyclerview;
    ArrayList<AnnualList> list;
    AnnualListAdapter adapter;
    FirebaseDatabase database;
    String[] staffList={"염선규","정재철","김태근","배경직","방석동","주현숙","한기룡","장우원","박천행","신정식","조아름","김연자"};
    TextView txtTitle;
    String staffDate;
    String strMonth;
    String vDate;

    public AnnualLeave(ArrayList<AnnualList> list) {
        this.list=list;
    }
    public AnnualLeave(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_leave);

        recyclerview=findViewById(R.id.recyclerViewAnnual);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager(manager);
        list=new ArrayList<>();
        database=FirebaseDatabase.getInstance();
        strMonth=new SimpleDateFormat("yyyy-MM-dd").format(new Date()).substring(5,7);
        getData(strMonth);
        adapter=new AnnualListAdapter(list,this,this,this);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        txtTitle=findViewById(R.id.annualtxt_title);
        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        txtTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                putBasicData();
                return true;
            }
        });



    }

    private void putDateData(String staffName) {
        View view=getLayoutInflater().inflate(R.layout.datepicker_spinner,null);
        DatePicker datePicker=view.findViewById(R.id.datePicker_start);
        Button btnAnnual=view.findViewById(R.id.btnannual);
        final String[] condition = new String[1];

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String strMonth;
                String strDay;
                if((monthOfYear+1)<10){
                    strMonth="0"+(monthOfYear+1);
                }else{
                    strMonth=String.valueOf(monthOfYear+1);
                }
                if(dayOfMonth<10){
                    strDay="0"+dayOfMonth;
                }else{
                    strDay=String.valueOf(dayOfMonth);
                }
                staffDate=year+"-"+strMonth+"-"+strDay;
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog=builder.create();
                dialog.show();

        btnAnnual.setOnClickListener(v->{
            if(staffDate==null){
                Toast.makeText(getApplicationContext(),"일자를 다시한번 선택 바랍니다.",Toast.LENGTH_SHORT).show();
            }else{
                condition[0] ="월차";
                checkCondition(condition[0],staffName,staffDate);
                dialog.dismiss();
            }

        });
        btnAnnual.setOnLongClickListener(v->{
            alertDialogVacation(staffName);
            return true;
        });
        Button btnhalf1=view.findViewById(R.id.btnhalf1);
        btnhalf1.setOnClickListener(v->{
            if(staffDate==null){
                Toast.makeText(getApplicationContext(),"일자를 다시한번 선택 바랍니다.",Toast.LENGTH_SHORT).show();
            }else{
                condition[0] ="반차1";
                checkCondition(condition[0], staffName, staffDate);
                dialog.dismiss();
            }

        });
        Button btnhalf2=view.findViewById(R.id.btnhalf2);
        btnhalf2.setOnClickListener(v->{
            if(staffDate==null){
                Toast.makeText(getApplicationContext(),"일자를 다시한번 선택 바랍니다.",Toast.LENGTH_SHORT).show();
            }else{
                condition[0] ="반차2";
                checkCondition(condition[0], staffName, staffDate);
                dialog.dismiss();
            }

        });
        Button btnVacation=view.findViewById(R.id.btnvacation);
        btnVacation.setOnClickListener(v->{
            alertDialogVacation(staffName);
            dialog.dismiss();
        });

    }

    private void alertDialogVacation(String staffName) {
        View view=getLayoutInflater().inflate(R.layout.annual_datepicker,null);
        DatePicker vDatePicker=view.findViewById(R.id.adatepicker_default);
        vDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month;
                if(monthOfYear+1<10){
                    month="0"+(monthOfYear+1);
                }else{
                    month=String.valueOf(monthOfYear+1);
                }
                String day;
                if(dayOfMonth<10){
                    day="0"+dayOfMonth;
                }else{
                    day=String.valueOf(dayOfMonth);
                }
                vDate=year+"-"+month+"-"+day;
            }
        });
        Button btnDateStart=view.findViewById(R.id.aBtnSearchDate_start);
        btnDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vDate!=null){
                    btnDateStart.setText(vDate);
                }else{
                    Toast.makeText(getApplicationContext(),"!!날짜를 지정 바랍니다!!.",Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        Button btnDateEnd=view.findViewById(R.id.aBtnSearchDate_end);
        btnDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vDate!=null){
                    btnDateEnd.setText(vDate);
                }else{
                    Toast.makeText(getApplicationContext(),"!!날짜를 지정 바랍니다!!.",Toast.LENGTH_SHORT).show();
                }

            }
        });
        Button btnDate=view.findViewById(R.id.abtnSearchDate);


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog=builder.create();
                 dialog.show();
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path;
                String annual=btnDateStart.getText().toString();
                String annual2=btnDateEnd.getText().toString();
                String annualmonth=annual.substring(5,7);
                String annual2month=annual2.substring(5,7);
                if(annualmonth.equals(annual2month)){
                    DatabaseReference databaseReference=database.getReference("AnnualData/2021_"+annualmonth+"_"+staffName );
                    Map<String,Object> value=new HashMap<>();
                    value.put("annual",annual);
                    value.put("annual2",annual2);
                    databaseReference.updateChildren(value);
                }else{
                    DatabaseReference databaseAnnual=database.getReference("AnnualData/2021_"+annualmonth+"_"+staffName);
                    Map<String,Object> valueAnnual=new HashMap<>();
                    valueAnnual.put("annual",annual);
                    databaseAnnual.updateChildren(valueAnnual);
                    DatabaseReference databaseAnnual2=database.getReference("AnnualData/2021_"+annual2month+"_"+staffName);
                    Map<String,Object> valueAnnual2=new HashMap<>();
                    valueAnnual2.put("annual2",annual2);
                    databaseAnnual2.updateChildren(valueAnnual2);
                }

                dialog.dismiss();
            }
        });


    }

    private void getData(String strMonth) {


        DatabaseReference databaseRef=database.getReference("AnnualData/");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    AnnualList mList=data.getValue(AnnualList.class);
                    String path=data.getKey();
                    String sortPath=data.getKey().substring(5,7);
                    if(strMonth.equals(sortPath)) {


                        int annual = 0;
                        int annual2 = 0;
                        double half = 0.0;
                        double half2 = 0.0;
                        double totalDate = 0;

                        String strAnnual, strAnnual2, strHalf, strHalf2;
                        strAnnual = mList.getAnnual();
                        strAnnual2 = mList.getAnnual2();
                        strHalf = mList.getHalf1();
                        strHalf2 = mList.getHalf2();

                        if (strAnnual2.equals("")) {
                            if (!strAnnual.equals("")) {
                                annual = 1;
                            }
                        } else {

                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");


                            Date date = null;
                            Date date2 = null;

                            try {
                                date = dateFormat.parse(strAnnual);
                                date2 = dateFormat2.parse(strAnnual2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            Calendar cal2 = Calendar.getInstance();
                            cal2.setTime(date2);

                            while (!cal.after(cal2)) {
                                annual2++;
                                cal.add(Calendar.DATE, 1);
                            }

                        }
                        if (!strHalf.equals("")) {
                            half = 0.5;
                        }
                        if (!strHalf2.equals("")) {
                            half2 = 0.5;
                        }

                        totalDate = annual + annual2 + half + half2;
                        Map<String, Object> value = new HashMap<>();
                        value.put("totaldate", totalDate);
                        DatabaseReference dataRef = database.getReference("AnnualData/" + path);
                        dataRef.updateChildren(value);
                        AnnualList dList = new AnnualList(mList.getName(), strAnnual, strAnnual2, strHalf, strHalf2, totalDate);
                        Log.i("duatjsrb", "get Key Value" + path + "////List Size" + list.size());
                        list.add(dList);
                    }
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void putTotalDate() {

    }

    @Override
    public void onItemClick(AnnualListAdapter.ListViewHolder holder, View view, int position) {
        String staffName=list.get(position).getName();
            putDateData(staffName);
    }

    public void putBasicData(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        EditText editText=new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setTitle("기초자료 등록 창")
                .setMessage("관리자 권한 기초자료 등록 창 입니다.신중히 조작 바랍니다"+"\n"+"하단 입력창에 기초자료 등록 년도를 입력 바랍니다.")
                .setView(editText)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String putYear=editText.getText().toString();
                        String path;
                        for(int j=1;j<13;j++){
                            for(int i=0;i<staffList.length;i++){
                                String monthP;
                                if(j<10){
                                    monthP="0"+j;
                                }else{
                                    monthP=String.valueOf(j);
                                }
                                path=putYear+"_"+monthP+"_"+staffList[i];
                                DatabaseReference databaseReference=database.getReference("AnnualData/"+path);
                                AnnualList list=new AnnualList(staffList[i],"","","","",0.0);
                                databaseReference.setValue(list);
                            }
                        }
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();



    }
    public void checkCondition(String condition, String staffName, String staffDate){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("근태등록 확인 사항")
                .setMessage("근태 변경 등록 직원 :"+staffName+"\n"+"근태 등록사항 :"+condition+"\n"+"근태등록일 :"+staffDate+"\n"+"로 등록 확인 합니다.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String path=staffDate.substring(5,7)+"_"+staffName;
                                DatabaseReference databaseReference=database.getReference("AnnualData/2021_"+path);

                                Map<String,Object> map=new HashMap<>();
                                switch(condition){
                                    case "월차":
                                        map.put("annual",staffDate);

                                        break;
                                    case "반차1":
                                        map.put("half1",staffDate);
                                        break;
                                    case "반차2":
                                        map.put("half2",staffDate);
                                        break;
                                }
                                databaseReference.updateChildren(map);
                                getData(strMonth);
                            }
                        }
                )
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    @Override
    public void longClick(AnnualListAdapter.ListViewHolder holder, View view, int position) {
        String name=list.get(position).getName();
        String condition1="월차:"+list.get(position).getAnnual();
        String condition2="반차:"+list.get(position).getHalf1()+","+list.get(position).getHalf2();


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("항목 초기화")
                .setMessage(name+" 에 대한"+"\n"+condition1+"\n"+condition2+"\n"+"항목에 대한 내용을 초기화 합니다"+"\n"+"!!!해당월 자료만 초기화 가능 합니다." +
                        "(이전자료 초기화는 관리자에게 문의 바랍니다.!")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference data=database.getReference("AnnualData/2021_"+strMonth+"_"+name);
                                AnnualList mList=new AnnualList(name,"","","","",0.0);
                                data.setValue(mList);

                            }
                        }
                )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    public void activityInit(){
        Intent intent=new Intent(AnnualLeave.this,AnnualLeave.class);
        startActivity(intent);
    }

    public void getDataData(){

    }

    public void sortData(String name, String dateS, String dateE) {


        list.clear();
//        AnnualListAdapter adapter=new AnnualListAdapter(list,this,this,this);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("AnnualData");
        ValueEventListener listener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    AnnualList mList=data.getValue(AnnualList.class);
                    int intS=Integer.parseInt(mList.getAnnual());
                    int intE=Integer.parseInt(mList.getAnnual());
                    int intdateS=Integer.parseInt(dateS);
                    int intdateE=Integer.parseInt(dateE);
                    if(intS>=intdateS && intE<=intdateE){
                        list.add(mList);
                    }

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        Query sortingData=databaseReference.orderByChild("name").equalTo(name);
        sortingData.addListenerForSingleValueEvent(listener);
    }

    @Override
    public void onTxtItemClick(AnnualListAdapter.ListViewHolder holder, View view, int position) {
        String name=list.get(position).getName();
        staffDate="";
        final String[] dateS = new String[1];
        final String[] dateE = new String[1];
        view= getLayoutInflater().inflate(R.layout.annual_datepicker,null);
        DatePicker datePicker=view.findViewById(R.id.adatepicker_default);
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String strMonth;
                String strDay;
                if((monthOfYear+1)<10){
                    strMonth="0"+(monthOfYear+1);
                }else{
                    strMonth=String.valueOf(monthOfYear+1);
                }
                if(dayOfMonth<10){
                    strDay="0"+dayOfMonth;
                }else{
                    strDay=String.valueOf(dayOfMonth);
                }
                staffDate=year+"-"+strMonth+"-"+strDay;
            }
        });
        Button btnStart=view.findViewById(R.id.aBtnSearchDate_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(staffDate==null){
                    Toast.makeText(getApplicationContext(),"일자를 다시한번 선택 바랍니다.",Toast.LENGTH_SHORT).show();
                }else{

                }btnStart.setText(staffDate);

            }
        });
        Button btnEnd=view.findViewById(R.id.aBtnSearchDate_end);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(staffDate==null){
                    Toast.makeText(getApplicationContext(),"일자를 다시한번 선택 바랍니다.",Toast.LENGTH_SHORT).show();
                }else{
                    btnEnd.setText(staffDate);
                }

            }
        });
        Button btnDate=view.findViewById(R.id.abtnSearchDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateS[0] =btnStart.getText().toString();
                dateE[0] =btnEnd.getText().toString();
                sortData(name,dateS[0],dateE[0]);
            }
        });
        btnDate.setText(name+" 직원 에 대한 기간별 근태상황 조회");
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setView(view);
        builder.show();
    }
}