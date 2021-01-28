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
    int contCountSize=1;
    String nickName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_data_reg);
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

                strWork=spWorkList[position];
                txtWork.setText("Work:"+"\n"+strWork);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        editBl=findViewById(R.id.editBl);
        txtBl=findViewById(R.id.regBl);
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
                strConsignee=consigneeList[position];
                txtConsignee.setText("화주명:"+"\n"+strConsignee);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editDes=findViewById(R.id.editDes);
        txtDes=findViewById(R.id.regDes);
        btnDes=findViewById(R.id.btnDes);
        btnDes.setOnClickListener(v->{
            strDes=editDes.getText().toString();
            txtDes.setText("품명:"+"\n"+strDes);
            imm.hideSoftInputFromWindow(editDes.getWindowToken(),0);
        });

        editCont=findViewById(R.id.editCont);
        txtCont=findViewById(R.id.regContainer);
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
              strType=typeList[position];
              txtType.setText("Type:"+"\n"+strType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editQty=findViewById(R.id.editQty);
        editQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtQty=findViewById(R.id.regQty);
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
        contCount.setInputType(InputType.TYPE_CLASS_NUMBER);
        regUpload=findViewById(R.id.regUpload);
        regUpload.setOnClickListener(v->{

            if(contCountSize==1){
                contCountSize=1;

            }else{
                Integer.parseInt(contCount.getText().toString());
            }

            postData(contCountSize);
            Incargo incargo=new Incargo();
            String msg=strConsignee+"화물 신규 정보 등록";
            incargo.putMessage(msg,"Etc",nickName);

        });
        regUpload.setOnLongClickListener(v->{
            Intent intent=new Intent(PutDataReg.this,PutDataReg.class);
            startActivity(intent);
            return true;
        });


}

public void postData(int contCountSize){
        for(int i=0;i<contCountSize;i++){
        Fine2IncargoList list=new Fine2IncargoList();
        list.setBl(strBl);
        list.setConsignee(strConsignee);
        Log.i("koacaiia","consigneeName"+strConsignee);
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
        DatabaseReference databaseReference=database.getReference("Incargo"+"/"+strBl+"_"+strDes+"_"+i);
        databaseReference.setValue(list);}
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