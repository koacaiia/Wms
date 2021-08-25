package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

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
FirebaseDatabase database;
ArrayList<ActivityPalletList> list;
ActivityPalletRecyclerAdapter adapter;
String deptName;
String consigneeName;
String pltS,month,year,yearMonth;
Button btnDate,btnSearch;
Spinner spConsignee,spPltS;
String[] spPltSList={"KPP","AJ","ETC"};
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
        spConsignee=findViewById(R.id.plt_spinnerConsignee);
        ArrayAdapter<String> consigneeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                publicMethod.getConsigneeList());
        consigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConsignee.setAdapter(consigneeAdapter);
        spConsignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                consigneeName=publicMethod.getConsigneeList().get(position);
                btnSearch.setText("기간:"+yearMonth+"\n"+"화주명:"+consigneeName+"\n"+"팔렛트 규격:"+pltS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        database=FirebaseDatabase.getInstance();
        list=new ArrayList<>();
        adapter=new ActivityPalletRecyclerAdapter(list,this,this);
        recyclerview.setAdapter(adapter);

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
        final int[] outQty = { 0 };
        list.clear();
        DatabaseReference databaseReference=database.getReference("DeptName/"+deptName+"/PltManagement/"+ consigneeName +"/"+ pltS);
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
                            database.getReference("DeptName/"+deptName+"/PltManagement/"+consigneeName+"/"+pltS+"/"+keyValue);
                    Map<String,Object> valueKey=new HashMap<>();
                    valueKey.put("stockQty", inQty[0] - outQty[0]);
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

    }

    @Override
    public void longClicked(ActivityPalletRecyclerAdapter.ListViewHolder listViewHolder, View v, int position) {

    }
}