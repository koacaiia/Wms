package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import org.w3c.dom.CDATASection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        getData();
        adapter=new AnnualListAdapter(list,this,this,this);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        txtTitle=findViewById(R.id.annualtxt_title);
        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sortData("김연자", dateS[0], dateE[0]);
//                putBasicData();

            }
        });

        strMonth=new SimpleDateFormat("yyyy-MM-dd").format(new Date()).substring(5,7);



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


            }
        });


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view)
                .show();

    }

    private void getData() {

//
//        DatabaseReference databaseReference=database.getReference("AnnualData");
//        ValueEventListener listener=new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                list.clear();
//                for(DataSnapshot data:snapshot.getChildren()){
//                    AnnualList mList=data.getValue(AnnualList.class);
//                   list.add(mList);
//                    }
//                for(int i=0;i<list.size();i++){
//                    double annual = 0;
//                    double half1 = 0;
//                    double half2 = 0;
//                    double totalDate;
//                    Log.i("duatjsrb","list Size:"+list.size());
//
//                    if(!list.get(i).getAnnual().equals("")){
//                        annual=1.0;
//                    }
//                    if(!list.get(i).getHalf1().equals("")){
//                        half1=0.5;
//                    }
//                    if(!list.get(i).getHalf2().equals("")){
//                        half2=0.5;
//                    }
//                    totalDate=annual+half1+half2;
//                    Map<String,Object> value=new HashMap<>();
//                    value.put("totaldate",totalDate);
//                    Log.i("duatjsrb","Name:"+list.get(i).getName()+"/totalDate:"+totalDate);
//                    DatabaseReference dataRef=database.getReference("AnnualData/2021_"+strMonth+"_"+list.get(i).getName());
////            dataRef.updateChildren(value);
//                    AnnualList mList=new AnnualList(list.get(i).getName(),list.get(i).getAnnual(),
//                            list.get(i).getAnnual2(),list.get(i).getHalf1(),
//                            list.get(i).getHalf2(),totalDate);
//                    dataRef.setValue(mList);
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        };

//        databaseReference.addValueEventListener(listener);

        DatabaseReference databaseRef=database.getReference("AnnualData/");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    AnnualList mList=data.getValue(AnnualList.class);
                    list.add(mList);
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
                                getData();
                            }
                        }
                )
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

        Log.i("duatjsrb","Dates:"+dateS+"/DateE::"+dateE);
        list.clear();
//        AnnualListAdapter adapter=new AnnualListAdapter(list,this,this,this);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("AnnualData");
        ValueEventListener listener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    AnnualList mList=data.getValue(AnnualList.class);
                    if( ! mList.getAnnual().equals("") &&  !mList.getAnnual2().equals("")){
                        int intSmonth=Integer.parseInt(mList.getAnnual().substring(5,7));
                        int intSday=Integer.parseInt(mList.getAnnual().substring(8,10));
                        int intEmonth=Integer.parseInt(mList.getAnnual2().substring(5,7));
                        int intEday=Integer.parseInt(mList.getAnnual2().substring(8,10));
                        int intdateSmonth=Integer.parseInt(dateS.substring(5,7));
                        int intdateSday=Integer.parseInt(dateS.substring(8,10));
                        int intdateEmonth=Integer.parseInt(dateE.substring(5,7));
                        int intdateEday=Integer.parseInt(dateE.substring(8,10));

                        Log.i("duatjsrb","Annual Value::::"+intSmonth+"parameter Value:::"+intdateSmonth);
                        if(intSmonth >= intdateSmonth && intSday >= intdateSday && intEmonth <=intdateEmonth && intEday <= intdateEday){

                            Log.i("duatjsrb","int Smonth:"+intSmonth+"int Date:"+intdateSmonth+"/int Sday:"+intSday+
                                    "intdateSday"+intdateSday);
                            Log.i("duatjsrb","intEmonth:"+intEmonth+"intDateEmonth:"+intdateEmonth+"/intEday:"+intEday+
                                    "intdateEday:"+intdateEday);
                            list.add(mList);
                            }

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

        btnDate.setText(name+" 직원 에 대한 기간별 근태상황 조회");
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.show();

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateS[0] =btnStart.getText().toString();
                dateE[0] =btnEnd.getText().toString();
                sortData(name,dateS[0],dateE[0]);
                dialog.dismiss();
            }
        });
    }
}