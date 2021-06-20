package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Location extends AppCompatActivity  {
    ViewPager viewPager;
    ArrayList<View> views=new ArrayList<>();
    TextView textView_location;

    String str_location;
    String str_rackCount;

    Spinner spinner_rack;
    Integer[] rackCount={0,1,2,3,4,5};

    CheckBox chk_a_a1,chk_a_a2,chk_a_a3,chk_a_a4,chk_a_ab1,chk_a_ab2,chk_a_ab3,chk_a_ab4,chk_a_b1,chk_a_b2,chk_a_b3,chk_a_b4,chk_a_c1,chk_a_c2,chk_a_c3,
            chk_a_c4,chk_a_cd1,chk_a_cd2,chk_a_cd3,chk_a_cd4,chk_a_d1,chk_a_d2,chk_a_d3,chk_a_d4,chk_a_e1,chk_a_e2,chk_a_e3,chk_a_e4,chk_a_ef1,chk_a_ef2,chk_a_ef3,chk_a_ef4,chk_a_f1,chk_a_f2,chk_a_f3,chk_a_f4;
    CheckBox chk_b_a1,chk_b_a2,chk_b_a3,chk_b_a4,chk_b_ab1,chk_b_ab3,chk_b_ab4,chk_b_b1,chk_b_b3,chk_b_b4,
            chk_b_c1,chk_b_c3,chk_b_c4, chk_b_cd1,chk_b_cd3,chk_b_cd4,chk_b_d1,chk_b_d3,chk_b_d4,chk_b_e1,chk_b_e3,chk_b_e4,chk_b_ef1,chk_b_ef3,chk_b_ef4,chk_b_f1,chk_b_f3,chk_b_f4;
    CheckBox chk_c_a1,chk_c_a2,chk_c_a3,chk_c_b1,chk_c_b2,chk_c_b3;
    CheckBox chk_d_a1,chk_d_a2,chk_d_a3,chk_d_a4,chk_d_b1,chk_d_b2,chk_d_b3,chk_d_b4,
            chk_d_c1,chk_d_c2,chk_d_c3,chk_d_c4,chk_d_d1,chk_d_d2,chk_d_d3,chk_d_d4;
//
//    String intent_bl;
//    String intent_des;
//    String intent_date;
//    String intent_count;
//    String intent_remark;
//    String intent_container;
//    String intent_incargo;
//    String intent_container40;
//    String intent_container20;
//    String intent_consignee;
//    String intent_working;
//    String intent_lclCargo;
//    String intent_multi;
    String intent_location;

    TextView location_bl;
    TextView location_count;
    TextView location_description;
    TextView location_itemcount;
    Fine2IncargoList setList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        viewPager=findViewById(R.id.pager_location);
        textView_location=findViewById(R.id.txt_location);


        Intent intent=getIntent();
        setList= (Fine2IncargoList) intent.getSerializableExtra("list");

        location_bl=findViewById(R.id.location_bl);
        location_bl.setText(setList.getBl());
        location_count=findViewById(R.id.location_count);
        location_count.setText(setList.getCount());
        location_description=findViewById(R.id.location_description);
        location_description.setText(setList.getDescription());
        location_itemcount=findViewById(R.id.location_itemcount);
        location_itemcount.setText(setList.getIncargo());

        LayoutInflater inflater=getLayoutInflater();
        View v_locationA=inflater.inflate(R.layout.location_a,null);
        View v_locationB=inflater.inflate(R.layout.location_b,null);
        View v_locationC=inflater.inflate(R.layout.location_c,null);
        View v_locationD=inflater.inflate(R.layout.location_d,null);

        views.add(v_locationA);views.add(v_locationB);views.add(v_locationC);views.add(v_locationD);
        fine.koaca.wms.LocationPagerAdapter pagerAdapter=new fine.koaca.wms.LocationPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        chk_a_a1=v_locationA.findViewById(R.id.a1);
        chk_a_a2=v_locationA.findViewById(R.id.a2);
        chk_a_a3=v_locationA.findViewById(R.id.a3);
        chk_a_a4=v_locationA.findViewById(R.id.a4);
        chk_a_ab1=v_locationA.findViewById(R.id.ab1);
        chk_a_ab2=v_locationA.findViewById(R.id.ab2);
        chk_a_ab3=v_locationA.findViewById(R.id.ab3);
        chk_a_ab4=v_locationA.findViewById(R.id.ab4);
        chk_a_b1=v_locationA.findViewById(R.id.b1);
        chk_a_b2=v_locationA.findViewById(R.id.b2);
        chk_a_b3=v_locationA.findViewById(R.id.b3);
        chk_a_b4=v_locationA.findViewById(R.id.b4);
        chk_a_c1=v_locationA.findViewById(R.id.c1);
        chk_a_c2=v_locationA.findViewById(R.id.c2);
        chk_a_c3=v_locationA.findViewById(R.id.c3);
        chk_a_c4=v_locationA.findViewById(R.id.c4);
        chk_a_d1=v_locationA.findViewById(R.id.d1);
        chk_a_d2=v_locationA.findViewById(R.id.d2);
        chk_a_d3=v_locationA.findViewById(R.id.d3);
        chk_a_d4=v_locationA.findViewById(R.id.d4);
        chk_a_cd1=v_locationA.findViewById(R.id.cd1);
        chk_a_cd2=v_locationA.findViewById(R.id.cd2);
        chk_a_cd3=v_locationA.findViewById(R.id.cd3);
        chk_a_cd4=v_locationA.findViewById(R.id.cd4);
        chk_a_e1=v_locationA.findViewById(R.id.e1);
        chk_a_e2=v_locationA.findViewById(R.id.e2);
        chk_a_e3=v_locationA.findViewById(R.id.e3);
        chk_a_e4=v_locationA.findViewById(R.id.e4);
        chk_a_ef1=v_locationA.findViewById(R.id.ef1);
        chk_a_ef2=v_locationA.findViewById(R.id.ef2);
        chk_a_ef3=v_locationA.findViewById(R.id.ef3);
        chk_a_ef4=v_locationA.findViewById(R.id.ef4);
        chk_a_f1=v_locationA.findViewById(R.id.f1);
        chk_a_f2=v_locationA.findViewById(R.id.f2);
        chk_a_f3=v_locationA.findViewById(R.id.f3);
        chk_a_f4=v_locationA.findViewById(R.id.f4);


        chk_b_a1=v_locationB.findViewById(R.id.b_a1);
        chk_b_a2=v_locationB.findViewById(R.id.b_a2);
        chk_b_a3=v_locationB.findViewById(R.id.b_a3);
        chk_b_a4=v_locationB.findViewById(R.id.b_a4);
        chk_b_ab1=v_locationB.findViewById(R.id.b_ab1);
        chk_b_ab3=v_locationB.findViewById(R.id.b_ab3);
        chk_b_ab4=v_locationB.findViewById(R.id.b_ab4);
        chk_b_b1=v_locationB.findViewById(R.id.b_b1);
        chk_b_b3=v_locationB.findViewById(R.id.b_b3);
        chk_b_b4=v_locationB.findViewById(R.id.b_b4);
        chk_b_c1=v_locationB.findViewById(R.id.b_c1);
        chk_b_c3=v_locationB.findViewById(R.id.b_c3);
        chk_b_c4=v_locationB.findViewById(R.id.b_c4);
        chk_b_d1=v_locationB.findViewById(R.id.b_d1);
        chk_b_d3=v_locationB.findViewById(R.id.b_d3);
        chk_b_d4=v_locationB.findViewById(R.id.b_d4);
        chk_b_cd1=v_locationB.findViewById(R.id.b_cd1);
        chk_b_cd3=v_locationB.findViewById(R.id.b_cd3);
        chk_b_cd4=v_locationB.findViewById(R.id.b_cd4);
        chk_b_e1=v_locationB.findViewById(R.id.b_e1);
        chk_b_e3=v_locationB.findViewById(R.id.b_e3);
        chk_b_e4=v_locationB.findViewById(R.id.b_e4);
        chk_b_ef1=v_locationB.findViewById(R.id.b_ef1);
        chk_b_ef3=v_locationB.findViewById(R.id.b_ef3);
        chk_b_ef4=v_locationB.findViewById(R.id.b_ef4);
        chk_b_f1=v_locationB.findViewById(R.id.b_f1);
        chk_b_f3=v_locationB.findViewById(R.id.b_f3);
        chk_b_f4=v_locationB.findViewById(R.id.b_f4);


        chk_c_a1=v_locationC.findViewById(R.id.c_a1);
        chk_c_a2=v_locationC.findViewById(R.id.c_a2);
        chk_c_a3=v_locationC.findViewById(R.id.c_a3);
        chk_c_b1=v_locationC.findViewById(R.id.c_b1);
        chk_c_b2=v_locationC.findViewById(R.id.c_b2);
        chk_c_b3=v_locationC.findViewById(R.id.c_b3);


        chk_d_a1=v_locationD.findViewById(R.id.d_a1);
        chk_d_a2=v_locationD.findViewById(R.id.d_a2);
        chk_d_a3=v_locationD.findViewById(R.id.d_a3);
        chk_d_a4=v_locationD.findViewById(R.id.d_a4);
        chk_d_b1=v_locationD.findViewById(R.id.d_b1);
        chk_d_b2=v_locationD.findViewById(R.id.d_b2);
        chk_d_b3=v_locationD.findViewById(R.id.d_b3);
        chk_d_b4=v_locationD.findViewById(R.id.d_b4);
        chk_d_c1=v_locationD.findViewById(R.id.d_c1);
        chk_d_c2=v_locationD.findViewById(R.id.d_c2);
        chk_d_c3=v_locationD.findViewById(R.id.d_c3);
        chk_d_c4=v_locationD.findViewById(R.id.d_c4);
        chk_d_d1=v_locationD.findViewById(R.id.d_d1);
        chk_d_d2=v_locationD.findViewById(R.id.d_d2);
        chk_d_d3=v_locationD.findViewById(R.id.d_d3);
        chk_d_d4=v_locationD.findViewById(R.id.d_d4);




//        Button btn_locationReg=findViewById(R.id.btn_locationReg);
//        btn_locationReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               filterChkBox();
//
//            }
//        });
//
//        btn_locationReg.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
////                intent_multi="multi";
////                String intent_location=textView_location.getText().toString();
////                Intent intent=new Intent(fine.koaca.wms.Location.this, fine.koaca.wms.MainActivity.class);
////                intent.putExtra("location",intent_location);
////                intent.putExtra("bl",intent_bl);
////                intent.putExtra("des",intent_des);
////                intent.putExtra("date",intent_date);
////                intent.putExtra("count",intent_count);
////                intent.putExtra("remark",intent_remark);
////                intent.putExtra("container",intent_container);
////                intent.putExtra("incargo",intent_incargo);
////                intent.putExtra("container40",intent_container40);
////                intent.putExtra("container20",intent_container20);
////                intent.putExtra("consignee", intent_consignee);
////                intent.putExtra("working", intent_working);
////                intent.putExtra("lclCargo",intent_lclCargo);
////                intent.putExtra("multi",intent_multi);
////                startActivity(intent);
//
//
//                return true;
//            }
//        });

        textView_location.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                textView_location.setText("");
                str_location=null;
                return true;
            }
        });

        spinner_rack=findViewById(R.id.spinner_rack);
        ArrayAdapter<Integer> rackAdapter=new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,
                Location.this.rackCount);
        rackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_rack.setAdapter(rackAdapter);
        spinner_rack.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_rackCount=Integer.toString(rackCount[position]);
                spinner_rack.setTag(str_rackCount);
                String toastMessage;
                if(str_rackCount.equals("0")){
                    toastMessage="평치 적재";
                }else{
                    toastMessage=str_rackCount+"_단 랙 적재";
                }

                Toast.makeText(Location.this, toastMessage, Toast.LENGTH_SHORT).show();
                filterChkBox();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    @SuppressLint("SetTextI18n")
    public void filterChkBox(){
        CheckBox [] chkBox_list=new CheckBox[]{chk_a_a1,chk_a_a2,chk_a_a3,chk_a_a4,chk_a_ab1,chk_a_ab2,chk_a_ab3,chk_a_ab4,chk_a_b1,chk_a_b2,chk_a_b3,chk_a_b4,chk_a_c1,chk_a_c2,chk_a_c3,
                chk_a_c4,chk_a_cd1,chk_a_cd2,chk_a_cd3,chk_a_cd4,chk_a_d1,chk_a_d2,chk_a_d3,chk_a_d4,chk_a_e1,chk_a_e2,chk_a_e3
                ,chk_a_e4,chk_a_ef1,chk_a_ef2,chk_a_ef3,chk_a_ef4,chk_a_f1,chk_a_f2,chk_a_f3,chk_a_f4,chk_b_a1,chk_b_a2,chk_b_a3,chk_b_a4,chk_b_ab1,chk_b_ab3,chk_b_ab4,chk_b_b1,chk_b_b3,chk_b_b4,
                chk_b_c1,chk_b_c3,chk_b_c4, chk_b_cd1,chk_b_cd3,chk_b_cd4,chk_b_d1,chk_b_d3,chk_b_d4,chk_b_e1,chk_b_e3,chk_b_e4
                ,chk_b_ef1,chk_b_ef3,chk_b_ef4,chk_b_f1,chk_b_f3,chk_b_f4,chk_c_a1,chk_c_a2,chk_c_a3,chk_c_b1,chk_c_b2,chk_c_b3,chk_d_a1,chk_d_a2,chk_d_a3,chk_d_a4,chk_d_b1,chk_d_b2,chk_d_b3,chk_d_b4,
                chk_d_c1,chk_d_c2,chk_d_c3,chk_d_c4,chk_d_d1,chk_d_d2,chk_d_d3,chk_d_d4};

        int chkCount=chkBox_list.length;
        for(int i=0;i<chkCount;i++){
            if(chkBox_list[i].isChecked()){
                String chkValue=chkBox_list[i].getText().toString();
                textView_location.append(chkValue);
                str_location=textView_location.getText().toString();
                CheckBox chkBox=chkBox_list[i];
                chkBox.setChecked(false);
            }
        }
        if(str_location!=null){

            textView_location.setText(str_location+"("+str_rackCount+")");
            locationDialog(textView_location.getText().toString());
        }else{
            Toast.makeText(this,"로케이션 자료 누락! 재 확인 바랍니다",Toast.LENGTH_SHORT).show();
        }


    }

    private void locationDialog(String str_location) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        EditText editText=new EditText(this);
        editText.setText(str_location);
        builder.setTitle("Location Configuration")
                .setMessage("B/L:"+setList.getBl()+"\n"+setList.getDescription()+"("+setList.getCount()+")"+"\n"+"로케이션:"+str_location)
                .setView(editText)
                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setList.setLocation(editText.getText().toString());
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=
                                database.getReference("Incargo"+"/"+location_bl.getText().toString()+"_"+location_description.getText().toString()+
                                        "_"+location_count.getText().toString());

                        databaseReference.setValue(setList);
                        String msg=setList.getCount()+"_"+setList.getDescription()+"_"+"["+editText.getText().toString()+"]"+"등록 " +
                                "합니다..";
                        putWorkingMessage(msg,"M&F");
                        Intent intent=new Intent(Location.this,MainActivitySub.class);
                        intent.putExtra("intentBl",location_bl.getText().toString());
                        startActivity(intent);


                    }
                })
                .setNegativeButton("초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationDialog(setList.getLocation());

                    }
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
    private void putWorkingMessage(String msg,String consignee){
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String timeStampDate=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());
        SharedPreferences sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nick=sharedPreferences.getString("nickName","FineWareHouseDepot");
        WorkingMessageList messageList=new WorkingMessageList();
        messageList.setNickName(nick);
        messageList.setTime(timeStamp);
        messageList.setDate(timeStampDate);
        messageList.setMsg(msg);
        messageList.setConsignee(consignee);
        messageList.setInOutCargo("Etc");
//        messageList.setUri("");
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference dataRef=database.getReference("WorkingMessage"+"/"+nick+"_"+timeStamp);
        dataRef.setValue(messageList);


    }


}
