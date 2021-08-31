package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityPallet extends AppCompatActivity implements ActivityPalletRecyclerAdapter.PltClicked,ActivityPalletRecyclerAdapter.PltLongClicked {
RecyclerView recyclerview;
RecyclerView recyclerviewResult;
FirebaseDatabase database;
ArrayList<ActivityPalletList> list;
ArrayList<ActivityPalletResultList> resultLists;
ActivityPalletRecyclerAdapter adapter;
ActivityPalletResultAdapter adapterResult;
String deptName;
String consigneeName;
String pltS,month,year,yearMonth;
Button btnDate,btnSearch;
Spinner spConsignee,spPltS;
String[] spPltSList={"KPP","AJ","ETC"};

String refPath;
ArrayList<String> consigneeList;
ActivityPalletResultList listResult;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet);
        PublicMethod publicMethod=new PublicMethod(this);
        deptName=publicMethod.getUserInformation().get("deptName");
        btnDate=findViewById(R.id.plt_Date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMonth();
            }
        });
        btnSearch=findViewById(R.id.plt_btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatabase(yearMonth,consigneeName,pltS);
            }
        });
        yearMonth="ALL";


        resultLists=new ArrayList<>();
        listResult=new ActivityPalletResultList();
        spConsignee=findViewById(R.id.plt_spinnerConsignee);
        consigneeList=new ArrayList<>();
        database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1=database.getReference("DeptName/"+deptName+"/PltManagement");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    consigneeList.add(data.getKey());
                }
                ArrayAdapter<String> consigneeAdapter=new ArrayAdapter<String>(ActivityPallet.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        consigneeList);
                consigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spConsignee.setAdapter(consigneeAdapter);
                spConsignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        consigneeName=consigneeList.get(position);
                        btnSearch.setText("기간:"+yearMonth+"\n"+"화주명:"+consigneeName+"\n"+"팔렛트 규격:"+pltS);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {


                    }
                });


                getResultData(consigneeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerviewResult=findViewById(R.id.pltrecyclereviewResult);
        LinearLayoutManager managerResult=new LinearLayoutManager(this);
        recyclerviewResult.setLayoutManager(managerResult);
        adapterResult=new ActivityPalletResultAdapter(resultLists);
        recyclerviewResult.setAdapter(adapterResult);
        adapterResult.notifyDataSetChanged();

        spPltS=findViewById(R.id.plt_spinnerPltS);
        ArrayAdapter<String> pltSAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<String>(Arrays.asList(spPltSList)));
        pltSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPltS.setAdapter(pltSAdapter);
        spPltS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pltS=new ArrayList<String>(Arrays.asList(spPltSList)).get(position);
                btnSearch.setText("기간:"+yearMonth+"\n"+"화주명:"+consigneeName+"\n"+"팔렛트 규격:"+pltS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerview=findViewById(R.id.activity_pallet_recyclerview);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager(manager);
        list=new ArrayList<>();
        adapter=new ActivityPalletRecyclerAdapter(list,this,this);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @SuppressLint("NotifyDataSetChanged")
    private void getResultData(ArrayList<String> consigneeList) {
        for(int i=0;i<consigneeList.size();i++){
            final int[] kppQty = {0};
            final int[] ajQty = {0};
            final int[] etcQty={0};
            for(int j=0;j<spPltSList.length;j++){
                String consigneeName=consigneeList.get(i);
                String plts=spPltSList[j];

                DatabaseReference databaseReference=
                        database.getReference("DeptName/"+deptName+"/PltManagement/"+consigneeList.get(i)+"/"+plts);
                int finalJ = j;

                int finalI = i;
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data:snapshot.getChildren()) {

                            ActivityPalletList mList = data.getValue(ActivityPalletList.class);
                            switch (finalJ) {
                                case 0:
                                    kppQty[0] = kppQty[0] + mList.getStockQty();
                                    break;
                                case 1:
                                    ajQty[0] = ajQty[0] + mList.getStockQty();
                                    break;
                                case 2:
                                    etcQty[0] = etcQty[0] + mList.getStockQty();
                                    break;
                            }

                            listResult = new ActivityPalletResultList(consigneeName, kppQty[0], ajQty[0], etcQty[0]);
                            resultLists.add(listResult);
                            int count=resultLists.size();
                            if(count!=1){
                                if(resultLists.get(count-1).getConsigneeName().equals(resultLists.get(count-2).getConsigneeName())){
                                    resultLists.remove(count-2);
                                }
                            }
                        }
                        adapterResult.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }


        }

    }

    private void searchMonth() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.year_month_picker,null);
        TextView txtYear=view.findViewById(R.id.picker_txtYear);
        TextView txtMonth=view.findViewById(R.id.picker_txtMonth);

        NumberPicker nbYear=view.findViewById(R.id.picker_year);
        nbYear.setMaxValue(2023);
        nbYear.setMinValue(2021);
        nbYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                year=String.valueOf(newVal);
                txtYear.setText(year);
            }
        });
        NumberPicker nbMonth=view.findViewById(R.id.picker_month);
        nbMonth.setMaxValue(12);
        nbMonth.setMinValue(1);
        nbMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal<10){
                    month="0"+newVal;
                }else{
                    month=String.valueOf(newVal);
                }
                txtMonth.setText(month);
            }

        });
        builder.setTitle("검색월 설정창")
                .setView(view)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yearMonth=year+"-"+month;
                        btnSearch.setText("기간:"+yearMonth+"\n"+"화주명:"+consigneeName+"\n"+"팔렛트 규격:"+pltS);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    @Override
    public void onBackPressed() {
        PublicMethod publicMethod=new PublicMethod(this);
        publicMethod.intentSelect();
    }

    private void getDatabase(String yearMonth, String consigneeName, String pltS) {
        final int[] inQty = {0};
        final int[] outQty = {0};
        list.clear();

        refPath="DeptName/"+deptName+"/PltManagement/"+ consigneeName +"/"+ pltS+"/";

        DatabaseReference databaseReference=database.getReference(refPath);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){

                    String keyValue=data.getKey();
                    ActivityPalletList mList=data.getValue(ActivityPalletList.class);
                    inQty[0] =mList.getInQty()+ inQty[0];
                    outQty[0] =mList.getOutQty()+ outQty[0];

                    if(yearMonth.equals("ALL")){
                        list.add(mList);
                    }else{
                        String tYearMonth=mList.getDate().substring(7);
                        if(yearMonth.equals(tYearMonth)){
                            list.add(mList);
                        }
                    }
                    DatabaseReference databaseKeyRef=
                            database.getReference(refPath+keyValue);
                    Map<String,Object> valueKey=new HashMap<>();
                    valueKey.put("stockQty", inQty[0] - outQty[0]);
                    valueKey.put("keyValue",keyValue);
                    valueKey.putIfAbsent("tDate", "");
                    valueKey.putIfAbsent("refPath",refPath);
                    databaseKeyRef.updateChildren(valueKey);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void clicked(ActivityPalletRecyclerAdapter.ListViewHolder listViewHolder, View v, int position) {
      inputValueDialog(position);
    }

    @Override
    public void longClicked(ActivityPalletRecyclerAdapter.ListViewHolder listViewHolder, View v, int position) {
        String bl=list.get(position).getBl();
        String des=list.get(position).getDes();
        String date=list.get(position).getDate();
        int in=list.get(position).getInQty();
        int out=list.get(position).getOutQty();
        int stock=list.get(position).getStockQty();



        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityPallet.this);
        builder.setTitle("비엘:"+bl+"\n"+"품명:"+des+"  항목 삭제 확인창")
                .setMessage("날짜"+date+"_재고:"+stock+"\n"+"입고:"+in+",출고:"+out)
                .setPositiveButton("삭제 확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String keyValue=list.get(position).getKeyValue();
                                String refPath=list.get(position).getRefPath();
                                DatabaseReference ref=database.getReference(refPath);
                                Map<String,Object> value=new HashMap<>();
                                value.put(keyValue,null);
                                ref.updateChildren(value);
                                Toast.makeText(ActivityPallet.this,"선택항목에 대한 자료 삭제 완료 되었습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ActivityPallet.this,ActivityPallet.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ActivityPallet.this.startActivity(intent);
                            }
                        }
                )
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();



        DatabaseReference databaseReference=database.getReference(list.get(position).getKeyValue());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void inputValueDialog(int position){
        String date=list.get(position).getDate();
        String keyValue=list.get(position).getKeyValue();
        String refPath=list.get(position).getRefPath();
        final String[] month = new String[1];
        final String[] day = new String[1];
        final String[] dateResult = new String[1];
        int inQty=list.get(position).getInQty();
        int outQty=list.get(position).getOutQty();
        final int[] qty = new int[1];
        Map<String,Object> value=new HashMap<>();
        ArrayList<String> contentValues=new ArrayList<String>();
        contentValues.add("날짜:"+date);
        contentValues.add("입고수량:"+inQty);
        contentValues.add("출고수량:"+outQty);
        contentValues.add("재고이관");
        contentValues.add("추가사용");

        DatabaseReference databaseReference=database.getReference(keyValue);
        DatePicker datePicker=new DatePicker(ActivityPallet.this);


        String[] contentValuesList=contentValues.toArray(new String[contentValues.size()]);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("팔렛트 등록창 수정")

                .setSingleChoiceItems(contentValuesList,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which1) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityPallet.this);
                        switch(which1){

                            case 0: case 3:
                                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                    @Override
                                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                        if(monthOfYear+1<10){
                                            month[0] ="0"+(monthOfYear+1);
                                        }else{
                                            month[0] =String.valueOf(monthOfYear+1);
                                        }

                                        if(dayOfMonth<10){
                                            day[0] ="0"+dayOfMonth;
                                        }else{
                                            day[0] =String.valueOf(dayOfMonth);
                                        }
                                        dateResult[0] =year+"-"+ month[0] +"-"+ day[0];
                                        Toast.makeText(ActivityPallet.this, dateResult[0],
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                               builder.setTitle("날짜 선택창")
                                       .setView(datePicker)
                                       .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                           @SuppressLint("NotifyDataSetChanged")
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {

                                            switch(which1){
                                                case 0:
                                                    value.put("date",dateResult[0]);
                                                    break;
                                                case 3:
                                                    value.put("tDate",dateResult[0]);
                                                    value.put("bl","재고");
                                                    value.put("des","이관");
                                            }
                                           databaseReference.updateChildren(value);
                                               Intent intent=new Intent(ActivityPallet.this,ActivityPallet.class);
                                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                               ActivityPallet.this.startActivity(intent);
                                           }
                                       })
                                       .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {

                                           }
                                       })
                                       .show();
                                break;
                            case 1: case 2:
                                EditText editText=new EditText(ActivityPallet.this);
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                builder.setTitle("수량 변경창")
                                        .setView(editText)
                                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                qty[0] = Integer.parseInt(editText.getText().toString());
                                               switch(which1){
                                                   case 1:
                                                       value.put("inQty",qty[0]);
                                                       break;
                                                   case 2:
                                                       value.put("outQty",qty[0]);

                                                }
                                                databaseReference.updateChildren(value);

                                                Intent intent=new Intent(ActivityPallet.this,ActivityPallet.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                ActivityPallet.this.startActivity(intent);
                                            }
                                        })
                                        .show();
                                break;
                            case 4:
                                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                    @Override
                                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                        if(monthOfYear+1<10){
                                            month[0] ="0"+(monthOfYear+1);
                                        }else{
                                            month[0] =String.valueOf(monthOfYear+1);
                                        }

                                        if(dayOfMonth<10){
                                            day[0] ="0"+dayOfMonth;
                                        }else{
                                            day[0] =String.valueOf(dayOfMonth);
                                        }
                                        dateResult[0] =year+"-"+ month[0] +"-"+ day[0];
                                        Toast.makeText(ActivityPallet.this, dateResult[0],
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                             builder.setTitle("추가사용 날자 선택창")
                                     .setView(datePicker)
                                     .setPositiveButton("날짜확인", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                             EditText editText=new EditText(ActivityPallet.this);
                                             editText.setInputType(InputType.TYPE_CLASS_NUMBER);


                                             builder.setTitle("추가사용 수량 등록창")
                                                    .setView(editText)
                                                    .setPositiveButton("추가수량 등록", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            qty[0] = Integer.parseInt(editText.getText().toString());
                                                            value.put("date",dateResult[0]);
                                                            value.put("outQty",qty[0]);
                                                            value.put("bl",list.get(position).getBl());
                                                            value.put("des",list.get(position).getDes());
                                                            value.put("inQty",0);
                                                            value.put("nickName",list.get(position).getNickName());
                                                            value.put("keyValue",list.get(position).getKeyValue());

                                                            DatabaseReference changedRef=database.getReference(keyValue+
                                                                    "_수정"+dateResult[0]);
                                                            changedRef.updateChildren(value);
                                                            Intent intent=new Intent(ActivityPallet.this,ActivityPallet.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            ActivityPallet.this.startActivity(intent);
                                                        }
                                                    })
                                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .show();
                                         }
                                     })
                                     .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {

                                         }
                                     })
                                     .show();

                                break;
                        }
                        dialog.cancel();
                    }
                })

                .show();
    }



}