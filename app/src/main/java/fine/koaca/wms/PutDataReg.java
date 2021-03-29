package fine.koaca.wms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PutDataReg extends AppCompatActivity {
    Incargo incargo;
    String strWork="";
    String strDate="";
    String strConsignee;
    String strDes="";
    String strCont="미정";
    String strType="";
    String strQty="0";
    String strBl="";
    String strRemark="";
    String strCount="";
    EditText editDes,editCont,editQty,editBl,editRemark;
    Button btnDes,btnCont,btnQty,btnBl,btnRemark,regUpload;
    TextView txtWork,txtDate,txtConsignee,txtDes,txtCont,txtType,txtQty,txtBl,txtRemark;
    TextView textViewDate;
    String[] consigneeList;
    String[] spWorkList={"Bulk","Pallet"};
    String[] typeList={"40FT","20FT","Cargo"};
    int contCountSize;
    String nickName;
    String[] intentList;

    String wareHouseDepotName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_data_reg);
        Intent getIntent=getIntent();

        intentList=getIntent.getStringArrayExtra("list");
//        String listLength=intentList[2];
//        Log.i("koacaiia", "ListRef++++" +listLength );
        wareHouseDepotName=getIntent.getStringExtra("dataRef");

        SharedPreferences sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        nickName=sharedPreferences.getString("nickName","Fine");
        InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        Spinner spWork=findViewById(R.id.spinner_Working);
        txtWork=findViewById(R.id.regWorking);

        ArrayAdapter<String> spWorkAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,PutDataReg.this.spWorkList);
        spWorkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWork.setAdapter(spWorkAdapter);
        spWork.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(intentList!=null){
                    strWork=intentList[0];
                }else{
                    strWork=spWorkList[position];
                }
                txtWork.setText("Work:"+"\n"+strWork);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        editBl=findViewById(R.id.editBl);
        txtBl=findViewById(R.id.regBl);
        if(intentList!=null){
            strBl=intentList[9];
            editBl.setText(strBl);
            txtBl.setText(strBl);
        }
        btnBl=findViewById(R.id.btnBl);
        btnBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strBl=editBl.getText().toString();
                txtBl.setText("Bl:"+"\n"+strBl);
                imm.hideSoftInputFromWindow(editBl.getWindowToken(),0);
            }
        });
        textViewDate=findViewById(R.id.textViewDate);
        if(intentList!=null){
            strDate=intentList[1];
            textViewDate.setText(strDate);
            TextView regDate=findViewById(R.id.regDate);
            regDate.setText(strDate);
        }
        textViewDate.setOnClickListener(v->{
            DatePickerFragment putDataDate=new DatePickerFragment("d");
            putDataDate.show(getSupportFragmentManager(),"datePicker");
        });
        Intent intent2=getIntent();
        consigneeList=intent2.getStringArrayExtra("consigneeList");

        Spinner spConsignee=findViewById(R.id.spinner_Consignee);
        txtConsignee=findViewById(R.id.regConsignee);
        ArrayAdapter<String> consigneeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,PutDataReg.this.consigneeList);
        consigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConsignee.setAdapter(consigneeAdapter);
        spConsignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(intentList!=null){
                    strConsignee=intentList[2];
                }else{
                    strConsignee=consigneeList[position];
                }
                txtConsignee.setText("화주명:"+"\n"+strConsignee);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editDes=findViewById(R.id.editDes);
        txtDes=findViewById(R.id.regDes);
        if(intentList!=null){
            strDes=intentList[3];
            editDes.setText(strDes);
            txtDes.setText(strDes);
        }
        btnDes=findViewById(R.id.btnDes);
        btnDes.setOnClickListener(v->{
            strDes=editDes.getText().toString();
            txtDes.setText("품명:"+"\n"+strDes);
            imm.hideSoftInputFromWindow(editDes.getWindowToken(),0);
        });

        editCont=findViewById(R.id.editCont);
        txtCont=findViewById(R.id.regContainer);
        if(intentList!=null){
            strCont=intentList[7];
            editCont.setText(strCont);
            txtCont.setText(strCont);
        }
        btnCont=findViewById(R.id.btnCont);
        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strCont=editCont.getText().toString();
                txtCont.setText("컨테이너번호:"+"\n"+strCont);
                imm.hideSoftInputFromWindow(editCont.getWindowToken(),0);
            }
        });
        Spinner spType=findViewById(R.id.spinner_Type);
        txtType=findViewById(R.id.regType);
        ArrayAdapter<String> typeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                PutDataReg.this.typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(intentList!=null){
                if(intentList[4].equals("1")){
                    strType="20FT";}
                else if(intentList[5] .equals("1")){
                    strType="40FT";
                }else if(intentList[6].equals("1")){
                    strType="Cargo";
                }else{strType="미정";}
                }else{
                    strType=typeList[position];
                }
              txtType.setText("Type:"+"\n"+strType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editQty=findViewById(R.id.editQty);
        editQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtQty=findViewById(R.id.regQty);
        if(intentList!=null){
            strQty=intentList[8];
            editQty.setText(strQty);
            txtQty.setText(strQty);
        }
        btnQty=findViewById(R.id.btnQty);
        btnQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strQty=editQty.getText().toString();
                txtQty.setText("Qty:"+"\n"+strQty);
                imm.hideSoftInputFromWindow(editQty.getWindowToken(),0);
            }
        });
        editRemark=findViewById(R.id.editRemark);
        txtRemark=findViewById(R.id.regRemark);
        if(intentList!=null){
            strRemark=intentList[10];
            editRemark.setText(strRemark);
            txtRemark.setText(strRemark);
        }
        btnRemark=findViewById(R.id.btnRemark);
        btnRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 strRemark=editRemark.getText().toString();
                 txtRemark.setText("비고:"+"\n"+strRemark);
                 imm.hideSoftInputFromWindow(editRemark.getWindowToken(),0);
            }
        });

        EditText contCount=findViewById(R.id.reg_contCount);
        contCount.setOnClickListener(v->{
            contCount.setText("");
        });
        contCount.setInputType(InputType.TYPE_CLASS_NUMBER);
        regUpload=findViewById(R.id.regUpload);
        regUpload.setOnClickListener(v->{


                contCountSize=Integer.parseInt(contCount.getText().toString());

            Log.i("koacaiia","arraysize++++"+contCountSize);

            postData(contCountSize);
            Incargo incargo=new Incargo();
            String msg=strConsignee+"화물 신규 정보 등록";
            incargo.putMessage(msg,"Etc",nickName);

        });
        regUpload.setOnLongClickListener(v->{
            Intent intent=new Intent(PutDataReg.this,Incargo.class);
            startActivity(intent);
            return true;
        });


}

public void postData(int contCountSize){
        for(int i=0;i<contCountSize;i++){
        Fine2IncargoList list=new Fine2IncargoList();
        list.setBl(strBl);
        list.setConsignee(strConsignee);
        list.setContainer(strCont);
        list.setDate(strDate);
        list.setDescription(strDes);
        list.setIncargo(strQty);
        list.setLocation("");
        list.setRemark(strRemark);
        list.setWorking(strWork);
        list.setCount(String.valueOf(i));
        switch(strType){
            case "40FT":
                list.setContainer40("1");
                list.setContainer20("0");
                list.setLclcargo("0");
                break;
            case "20FT":
                list.setContainer40("0");
                list.setContainer20("1");
                list.setLclcargo("0");
                break;
            case "Cargo":
                list.setContainer40("0");
                list.setContainer20("0");
                list.setLclcargo("1");
                break;
        }
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference("Incargo"+"/"+strBl+"_"+strDes+"_"+i+"_"+strCont);
        databaseReference.setValue(list);
        DatabaseReference databaseReference1=database.getReference(wareHouseDepotName+"/"+strBl+"_"+strDes+"_"+i+"_"+strCont);
        databaseReference1.setValue(list);
        }
        Intent intent=new Intent(PutDataReg.this,Incargo.class);
        startActivity(intent);

}

    public void processDatePickerResult(int year, int month, int dayOfMonth) {
        String month_string;
        if(month<10){
            month_string="0"+Integer.toString(month+1);
        }else{
            month_string=Integer.toString(month+1);
        }
        String day_string;
        if(dayOfMonth<10){
            day_string="0"+Integer.toString(dayOfMonth);
        }else{
            day_string=Integer.toString(dayOfMonth);
        }
        String year_string=Integer.toString(year);

        strDate=(year_string+"-"+month_string+"-"+day_string);
        textViewDate.setText(strDate);
        TextView regDate=findViewById(R.id.regDate);
        regDate.setText(strDate);

    }
}