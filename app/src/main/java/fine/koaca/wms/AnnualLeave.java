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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    String strYear;
    String strMonth;
    String vDate;
    int intYear;
    int intMonth;

    String deptName;
    String nickName;

    PublicMethod publicMethod;
    String strSearchStartDate,strSearchEndDate;
    String getAnnualData;
    public AnnualLeave(ArrayList<AnnualList> list) {
        this.list=list;
    }
    public AnnualLeave(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_leave);

        publicMethod=new PublicMethod(this);
        nickName=publicMethod.getUserInformation().get("nickName");
        deptName=publicMethod.getUserInformation().get("deptName");


        recyclerview=findViewById(R.id.recyclerViewAnnual);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager(manager);
        list=new ArrayList<>();
        database=FirebaseDatabase.getInstance();
        strYear=new SimpleDateFormat("yyyy년MM월dd일").format(new Date()).substring(0,5);
        strMonth=new SimpleDateFormat("yyyy년MM월dd일").format(new Date()).substring(5,8);
        getData(strYear,strMonth);
        adapter=new AnnualListAdapter(list,this,this,this);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        txtTitle=findViewById(R.id.annualtxt_title);
        txtTitle.setText(strYear+strMonth+" 근태상황");
        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerDate("All");
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

    private void numberPickerDate(String name) {

        View view=getLayoutInflater().inflate(R.layout.datepicker_spinner,null);
        Button btnSearch=view.findViewById(R.id.btn_search);
        NumberPicker yearPicker=(NumberPicker)view.findViewById(R.id.picker_year);
        NumberPicker monthPicker=(NumberPicker)view.findViewById(R.id.picker_month);
        Calendar cal=Calendar.getInstance();
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH)+1);
        intMonth=cal.get(Calendar.MONTH)+1;
        String strMonth;
        Calendar cal2=Calendar.getInstance();
        int year=cal2.get(Calendar.YEAR);
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(2022);
        yearPicker.setValue(year);
        intYear=cal2.get(Calendar.YEAR);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog=builder.create();
                dialog.show();

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                intYear =newVal;
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                intMonth=newVal;
            }
        });

        if(intMonth<10){
            strMonth="0"+intMonth;
        }else{
            strMonth=String.valueOf(intMonth);
        }
        Button btnS=view.findViewById(R.id.btn_searchS);
        btnS.setText("시작월: "+intYear+"년"+strMonth+"월");
        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMonth;
                if(intMonth<10){
                    strMonth="0"+intMonth;
                }else{
                    strMonth=String.valueOf(intMonth);
                }
                strSearchStartDate=intYear+"년"+strMonth+"월";
                btnS.setText("시작월: "+strSearchStartDate);
            }
        });

        Button btnE=view.findViewById(R.id.btn_searchE);
        btnE.setText("종료월: "+intYear+"년"+strMonth+"월");
        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMonth;
                if(intMonth<10){
                    strMonth="0"+intMonth;
                }else{
                    strMonth=String.valueOf(intMonth);
                }
                strSearchEndDate=intYear+"년"+strMonth+"월";
                btnE.setText("종료월: "+strSearchEndDate);
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=btnS.getText().toString().substring(10,12);
                String e=btnE.getText().toString().substring(10,12);
                sortData(name,s,e);
                txtTitle.setText(s+"~"+e+" 근태상황");
                dialog.dismiss();
            }
        });
    }

    private void putDateData(String staffName) {
        View view=getLayoutInflater().inflate(R.layout.datepicker_calendar,null);
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
                staffDate=year+"년"+strMonth+"월"+strDay+"일";
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
            dialog.dismiss();
            return true;
        });
        Button btnhalf1=view.findViewById(R.id.btnhalf1);
        btnhalf1.setOnClickListener(v->{
            if(staffDate==null){
                Toast.makeText(getApplicationContext(),"일자를 다시한번 선택 바랍니다.",Toast.LENGTH_SHORT).show();
            }else{
                condition[0] ="반차";
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
                vDate=year+"년"+month+"월"+day+"일";
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

                String annual=btnDateStart.getText().toString();
                String annual2=btnDateEnd.getText().toString();
                DateFormat dateFormat=new SimpleDateFormat("yyyy년MM월dd일");
                Date dateBasic=null,dateTarget=null;
                try{
                    dateBasic=dateFormat.parse(annual);
                    dateTarget=dateFormat.parse(annual2);
                }catch(ParseException e){
                    e.printStackTrace();
                }
                Calendar calBasic=Calendar.getInstance();
                Calendar calTarget=Calendar.getInstance();
                calBasic.setTime(dateBasic);
                calTarget.setTime(dateTarget);

                String vacationDate1="";
                    DatabaseReference databaseReference=
                            database.getReference("AnnualData/"+annual.substring(0,5)+"/"+staffName );
                    while(!calBasic.after(calTarget)){
                        vacationDate1=vacationDate1+dateFormat.format(calBasic.getTime()).substring(5)+",";
                        calBasic.add(Calendar.DATE,1);
                    }
                    Map<String,Object> value=new HashMap<>();
                    value.put("annual",getAnnualData+vacationDate1);
                    databaseReference.updateChildren(value);
                dialog.dismiss();
            }
        });


    }

    private void getData(String strYear,String strMonth) {
        DatabaseReference databaseRef=database.getReference("AnnualData/"+strYear);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();

                PublicMethod publicMethod=new PublicMethod();

                for(DataSnapshot data:snapshot.getChildren()){
                    ArrayList<String> annualArr;
                    ArrayList<String> halfArr;
                    AnnualList getList=data.getValue(AnnualList.class);
                    String path=data.getKey();
                    String strAnnual="",strHalf="";
                    int intAnnual=0;
                    double doubleHalf=0.0;
                    if(!getList.getAnnual().equals("")){
                        annualArr=publicMethod.extractChar(getList.getAnnual(),',');
                        for(int i=0;i<annualArr.size();i++){
                            if(annualArr.get(i).contains(strMonth)){
                              strAnnual=strAnnual+annualArr.get(i)+",";
                              intAnnual=publicMethod.extractCharCount(strAnnual,',');

                            }
                        }
                    }
                    if(!getList.getHalf().equals("")){
                        halfArr=publicMethod.extractChar(getList.getHalf(),',');
                        for(int i=0;i<halfArr.size();i++){
                            if(halfArr.get(i).contains(strMonth)){
                                strHalf=strHalf+halfArr.get(i)+',';
                                doubleHalf=publicMethod.extractCharCount(strHalf,',')*0.5;
                            }
                        }
                    }

                    AnnualList putList=new AnnualList(getList.getName(),strAnnual,strHalf,intAnnual+doubleHalf,deptName);
                    list.add(putList);


//                    String sortPath=data.getKey().substring(0,7);
//                    if(strMonth.equals(sortPath)) {
//                        int annual = 0;
//                        int annual2 = 0;
//                        double half = 0.0;
//                        double half2 = 0.0;
//                        double totalDate = 0;
//
//                        String strAnnual, strAnnual2, strHalf, strHalf2;
//                        assert mList != null;
//                        strAnnual = mList.getAnnual();
//                        strHalf = mList.getHalf();
//                            if (!strAnnual.equals("")) {
//                                annual = 1;}
////                            } else {
////                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
////                            Date date = null;
////
////                            try {
////                                date = dateFormat.parse(strAnnual);
////
////                            } catch (ParseException e) {
////                                e.printStackTrace();
////                            }
////                            Calendar cal = Calendar.getInstance();
////                            cal.setTime(date);
//////                            Calendar cal2 = Calendar.getInstance();
//////                            cal2.setTime(date2);
////
////                            while (!cal.after(cal)) {
////                                annual++;
////                                cal.add(Calendar.DATE, 1);
////                            }
////
////                        }
//                        if (!strHalf.equals("")) {
//                            half = 0.5;
//                        }
//
//
//                        totalDate = annual + half ;
//                        Map<String, Object> value = new HashMap<>();
//                        value.put("totaldate", totalDate);
//
//                        DatabaseReference dataRef = database.getReference("AnnualData/" + path);
//                        dataRef.updateChildren(value);
//                        AnnualList dList = new AnnualList(mList.getName(), strAnnual,  strHalf,  totalDate,
//                                deptName);
//                        list.add(dList);
//
//                    }
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onItemClick(AnnualListAdapter.ListViewHolder holder, View view, int position) {
        String staffName=list.get(position).getName();
        getAnnualData=list.get(position).getAnnual();
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
                        String putYear=editText.getText().toString()+"년";
                        String path;
                        for(int i=0;i<staffList.length;i++){

                                path=putYear+"/"+staffList[i];
                                DatabaseReference databaseReference=
                                        database.getReference("AnnualData/"+path);
                                AnnualList list=new AnnualList(staffList[i],"","",0.0,"WareHouseDepot2");
                                databaseReference.setValue(list);
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

                                String strYear=staffDate.substring(0,5);

                                DatabaseReference databaseReference=database.getReference("AnnualData/"+strYear);
                                DatabaseReference putDataRef=database.getReference("AnnualData/"+strYear+"/"+staffName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot data:snapshot.getChildren()){

                                            if(data.getKey().equals(staffName)){
                                                AnnualList mList=data.getValue(AnnualList.class);
                                                String annual=mList.getAnnual();
                                                String half=mList.getHalf();
                                                Map<String,Object> map=new HashMap<>();
                                                switch(condition){
                                                    case "월차":
                                                        map.put("annual",annual+staffDate.substring(5)+",");

                                                        publicMethod.putNewDataUpdateAlarm(nickName,staffName+" 직원이 " +staffDate+" 로 월차 등록 됩니다",
                                                                "근태","Etc",
                                                                deptName);

                                                        break;
                                                    case "반차":
                                                        map.put("half",half+staffDate.substring(5)+",");

                                                        publicMethod.putNewDataUpdateAlarm(nickName,staffName+" 직원이 " +staffDate+" 로 반차 등록 합니다" +
                                                                        ".","근태","Etc",
                                                                deptName);
                                                        break;

                                                }
                                                map.put("deptName",deptName);
                                                putDataRef.updateChildren(map);
                                                getData(strYear,strMonth);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


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
        String condition2="반차:"+list.get(position).getHalf();


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("항목 초기화")
                .setMessage(name+" 에 대한"+"\n"+condition1+"\n"+condition2+"\n"+"항목에 대한 내용을 초기화 합니다"+"\n"+"!!!해당월 자료만 초기화 가능 합니다." +
                        "(이전자료 초기화는 관리자에게 문의 바랍니다.!")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference data=
                                        database.getReference("AnnualData/"+strYear+"/"+name);
                                AnnualList mList=new AnnualList(name,"","",0.0,deptName);
                                data.setValue(mList);
                                publicMethod.sendPushMessage(deptName,nickName,"직원 근태상황을 초기화 진행 합니다.","Annual");
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
        String strYear=strSearchStartDate.substring(0,5);
        PublicMethod publicMethod=new PublicMethod();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("AnnualData/"+strYear);
        ValueEventListener listener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    AnnualList mList=data.getValue(AnnualList.class);
                   int intAnnualMonthCount=0;
                   double doubleHalfMonthCount=0.0;
                   String strAnnualMonth="",strHalfMonth="";
                    if(!mList.getAnnual().equals("")){
                        ArrayList<String> annualMonth=publicMethod.extractChar(mList.getAnnual(),',');
                        for(int i=0;i<annualMonth.size();i++){
                           int intAnnualMonth=Integer.parseInt(annualMonth.get(i).substring(0,2));
                          if(intAnnualMonth>=Integer.parseInt(dateS)){
                              strAnnualMonth=strAnnualMonth+annualMonth.get(i)+",";
                              intAnnualMonthCount=publicMethod.extractCharCount(strAnnualMonth,',');
                            }
                        }
                    }
                    if(!mList.getHalf().equals("")){
                        ArrayList<String> halfMonth=publicMethod.extractChar(mList.getHalf(),',');
                        for(int i=0;i<halfMonth.size();i++){
                            int intHalfMonth=Integer.parseInt(halfMonth.get(i).substring(0,2));
                            if(intHalfMonth<=Integer.parseInt(dateE)){
                                strHalfMonth=strHalfMonth+halfMonth.get(i)+",";
                                doubleHalfMonthCount=publicMethod.extractCharCount(strHalfMonth,',')*0.5;
                            }
                        }
                    }

                    AnnualList putList=new AnnualList(mList.getName(),strAnnualMonth,strHalfMonth,
                            intAnnualMonthCount+doubleHalfMonthCount,
                            mList.getDeptName());
                    list.add(putList);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        if(name.equals("All")){
            databaseReference.addListenerForSingleValueEvent(listener);
        }else{
            Query sortingData=databaseReference.orderByChild("name").equalTo(name);
            sortingData.addListenerForSingleValueEvent(listener);
        }

    }

    @Override
    public void onTxtItemClick(AnnualListAdapter.ListViewHolder holder, View view, int position) {
        String name=list.get(position).getName();
        numberPickerDate(name);
//        staffDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//        final String[] dateS = new String[1];
//        final String[] dateE = new String[1];
//        view= getLayoutInflater().inflate(R.layout.datepicker_spinner,null);
//        NumberPicker yearPicker=view.findViewById(R.id.picker_year);
//        NumberPicker monthPicker=view.findViewById(R.id.picker_month);
//
//        Calendar cal=Calendar.getInstance();
//        intYear=cal.get(Calendar.YEAR);
//        yearPicker.setMinValue(2020);
//        yearPicker.setMaxValue(2022);
//        yearPicker.setValue(intMonth);
//
//        intMonth=cal.get(Calendar.MONTH)+1;
//        monthPicker.setMinValue(1);
//        monthPicker.setMaxValue(12);
//        monthPicker.setValue(intYear);
//        String strMonth;
//        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                intYear=newVal;
//
//            }
//        });
//        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//               intMonth=newVal;
//            }
//        });
//
//
//        if(intMonth<10){
//            strMonth="0"+intMonth;
//            }else{
//            strMonth=String.valueOf(intMonth);
//        }
//        staffDate=intYear+"-"+strMonth;
//        Button btnStart=view.findViewById(R.id.btn_searchS);
//        btnStart.setText("시작월: "+staffDate);
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(staffDate==null){
//                    Toast.makeText(getApplicationContext(),"일자를 다시한번 선택 바랍니다.",Toast.LENGTH_SHORT).show();
//                }else{
//
//                }
//                String strMonth;
//                if(intMonth<10){
//                    strMonth="0"+intMonth;
//                }else{
//                    strMonth=String.valueOf(intMonth);
//                }
//                staffDate=intYear+"-"+strMonth;
//                btnStart.setText("시작월: "+staffDate);
//
//            }
//        });
//        Button btnEnd=view.findViewById(R.id.btn_searchE);
//        btnEnd.setText("종료월: "+staffDate);
//        btnEnd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(staffDate==null){
//                    Toast.makeText(getApplicationContext(),"일자를 다시한번 선택 바랍니다.",Toast.LENGTH_SHORT).show();
//                }else{
//                    String strMonth;
//                    if(intMonth<10){
//                        strMonth="0"+intMonth;
//                    }else{
//                        strMonth=String.valueOf(intMonth);
//                    }
//                    staffDate=intYear+"-"+strMonth;
//                    btnEnd.setText("종료월: "+staffDate);
//                }
//
//            }
//        });
//        Button btnDate=view.findViewById(R.id.btn_search);
//
//        btnDate.setText(name+" 직원 에 대한 기간별 근태상황 조회");
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//
//        builder.setView(view);
//        AlertDialog dialog=builder.create();
//        dialog.show();
//        btnDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dateS[0] =btnStart.getText().toString().substring(10,12);
//                dateE[0] =btnEnd.getText().toString().substring(10,12);
//                sortData(name,dateS[0],dateE[0]);
//                dialog.dismiss();
//            }
//        });
    }


    @Override
    public void onBackPressed() {

       PublicMethod publicMethod=new PublicMethod(this);
       publicMethod.intentSelect();
    }
}