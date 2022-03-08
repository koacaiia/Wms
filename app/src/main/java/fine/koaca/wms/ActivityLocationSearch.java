package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class ActivityLocationSearch extends AppCompatActivity implements IncargoListAdapter.AdapterClickListener, IncargoListAdapter.ItemConsigneeClickListener, IncargoListAdapter.AdapterLongClickListener {

    ArrayList<Fine2IncargoList> list=new ArrayList<>();
    IncargoListAdapter adapter;
    TextView txtCon,txtBl;
    String deptName,consigneeValue,blValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        PublicMethod publicMethod=new PublicMethod(this);
        deptName=publicMethod.getUserInformation().get("deptName");

                SharedPreferences sharedPreferences=getSharedPreferences("Dept_Name", Context.MODE_PRIVATE);
                String consigneeList=sharedPreferences.getString("consigneeList",null);
                ArrayList<String> consigneeArr=publicMethod.extractChar(consigneeList,',');

                Spinner spinner=findViewById(R.id.dialog_location_search_spinner);
                EditText editText=findViewById(R.id.dialog_location_search_txtSearch);
                Button btnSearch=findViewById(R.id.dialog_location_search_btnSearch);
                txtCon=findViewById(R.id.dialog_location_search_txtConsignee);
                txtBl=findViewById(R.id.dialog_location_search_txtBl);

                ArrayAdapter<String> locationAdapter=new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        consigneeArr);
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(locationAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        txtCon.setText(consigneeArr.get(i));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String bl=editText.getText().toString();
                        if(bl.length()!=4){
                            AlertDialog.Builder builder=new AlertDialog.Builder(ActivityLocationSearch.this);
                            builder.setTitle("검색 경고창")
                                    .setMessage("검색 비엘 조건 :"+bl.length()+" 자리 입니다.4자리 로 입력 바랍니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();

                        }else{
                            txtBl.setText(bl);
                            getLocationData( txtCon.getText().toString(),
                           txtBl.getText().toString());
                        }

                    }
                });
                btnSearch.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        editText.setText("");
                        txtBl.setText("");
                        return true;
                    }
                });

        RecyclerView locationRecyclerView=findViewById(R.id.dialog_location_search_re);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        locationRecyclerView.setLayoutManager(manager);
        adapter=new IncargoListAdapter(list,this,this,this);
        locationRecyclerView.setAdapter(adapter);
        Intent intent=getIntent();
        if(intent.getStringExtra("consigneeValue")!=null){
            consigneeValue=intent.getStringExtra("consigneeValue");
            blValue=intent.getStringExtra("blValue");
            txtCon.setText(consigneeValue);
            txtBl.setText(blValue);
            getLocationData(consigneeValue,blValue);
            Toast.makeText(ActivityLocationSearch.this,consigneeValue+"_"+blValue+"에 대한 Location 확인 바랍니다",Toast.LENGTH_SHORT).show();
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private void getLocationData(String consignee,String bl) {
        list.clear();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        String year=new SimpleDateFormat("yyyy").format(new Date());
        Calendar calendar= Calendar.getInstance();
        for(int y=2021;y<=Integer.parseInt(year);y++){
            for(int i=1;i<13;i++){
                String month;
                if(i<10){
                    month="0"+i;
                }else{
                    month=String.valueOf(i);
                }
                calendar.set(y,i-1,1);
                int lastDayOfMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for(int d=1;d<=lastDayOfMonth;d++){
                    String day;
                    if(d<10){
                        day="0"+d;
                    }else{
                        day=String.valueOf(d);
                    }
                    String strReferencePath="DeptName/"+deptName+"/InCargo/"+month+"월/"+y+"-"+month+"-"+day;
                    DatabaseReference ref=database.getReference(strReferencePath);
                    ValueEventListener listener=new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String checkPath=dataSnapshot.getKey();
                                if(!checkPath.contains("json")){
                                    Fine2IncargoList data=dataSnapshot.getValue(Fine2IncargoList.class);
                                    String blValue=data.getBl();
                                    if(blValue.contains(bl)){
                                        list.add(data);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    Query locationQuery=ref.orderByChild("consignee").equalTo(consignee);
                    locationQuery.addListenerForSingleValueEvent(listener);
                }
                }

        }


    }

    @Override
    public void onItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {

    }

    @Override
    public void onItemConsigneeClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {

    }

    @Override
    public void onLongItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {

    }
}