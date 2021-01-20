package fine.koaca.wms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PutDataReg extends AppCompatActivity {
    String strWork="";
    String strDate="";
    String strConsignee="";
    String strDes="";
    String strCont="";
    String strType="";
    String strQty="";
    String strBl="";
    String strRemark="";
    EditText editWork,editDate,editConsignee,editDes,editCont,editType,editQty,editBl,editRemark;
    Button btnWork,btnDate,btnConsignee,btnDes,btnCont,btnType,btnQty,btnBl,btnRemark,regUpload;
    TextView textViewDate;
    String[] consigneeList = {"M&F", "SPC", "공차", "케이비켐", "BNI","기타","스위치코리아","서강비철","한큐한신","하랄코"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_data_reg);

        Spinner spWork=findViewById(R.id.spinner_Working);
        editBl=findViewById(R.id.editBl);
        textViewDate=findViewById(R.id.textViewDate);
        textViewDate.setOnClickListener(v->{
            DatePickerFragment putDataDate=new DatePickerFragment("d");
            putDataDate.show(getSupportFragmentManager(),"datePicker");


        });
        Spinner spConsignee=findViewById(R.id.spinner_Consignee);
        ArrayAdapter<String> consigneeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,PutDataReg.this.consigneeList);
        consigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConsignee.setAdapter(consigneeAdapter);
        spConsignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editDes=findViewById(R.id.editDes);
        editCont=findViewById(R.id.editCont);
        Spinner spType=findViewById(R.id.spinner_Type);
        editQty=findViewById(R.id.editQty);
        editRemark=findViewById(R.id.editRemark);

        btnWork=findViewById(R.id.btnWorking);
        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strWork=editWork.getText().toString();
                TextView btnWork=findViewById(R.id.regWorking);
                btnWork.setText(strWork);
            }
        });
        btnDate=findViewById(R.id.btnDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnConsignee=findViewById(R.id.btnConsignee);
        btnConsignee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strConsignee=editConsignee.getText().toString();
                TextView btnConsignee=findViewById(R.id.regConsignee);
                btnConsignee.setText(strConsignee);
            }
        });
        btnDes=findViewById(R.id.btnDes);
        btnDes.setOnClickListener(v -> {
            EditText editDes=findViewById(R.id.editDes);
            strDes=editDes.getText().toString();
            TextView btnDes=findViewById(R.id.regDes);
            btnDes.setText(strDes);
        });
        btnCont=findViewById(R.id.btnCont);
        btnCont.setOnClickListener(v->{
            EditText editCont=findViewById(R.id.editCont);
            strCont=editCont.getText().toString();
            TextView btnCont=findViewById(R.id.regContainer);
            btnCont.setText(strCont);
        });
        btnType=findViewById(R.id.btnType);
        btnType.setOnClickListener(v->{
            strType=editType.getText().toString();
            TextView btnType=findViewById(R.id.regType);
            btnType.setText(strType);

        });
        btnQty=findViewById(R.id.btnQty);
        btnQty.setOnClickListener(v -> {
            EditText editQty=findViewById(R.id.editQty);
            strQty=editQty.getText().toString();
            TextView btnQty=findViewById(R.id.regQty);
            btnQty.setText(strQty);
        });
        btnBl=findViewById(R.id.btnBl);
        btnBl.setOnClickListener(v->{
            EditText editBl=findViewById(R.id.editBl);
            strBl=editBl.getText().toString();
            TextView btnBl=findViewById(R.id.regBl);
            btnBl.setText(strBl);
        });
        btnRemark=findViewById(R.id.btnRemark);
        btnRemark.setOnClickListener(v->{
            EditText editRemark=findViewById(R.id.editRemark);
            strRemark=editRemark.getText().toString();
            TextView btnRemark=findViewById(R.id.regRemark);
            btnRemark.setText(strRemark);
        });

        Button regUpload=findViewById(R.id.regUpload);
        regUpload.setOnClickListener(v->{

        });
        regUpload.setOnLongClickListener(v->{
            Intent intent=new Intent(PutDataReg.this,PutDataReg.class);
            startActivity(intent);
            return true;
        });


}

public void putEditDate(){
        AlertDialog.Builder editBuilder=new AlertDialog.Builder(this);

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

        strDate=(year_string+"년"+month_string+"월"+day_string+"일");
        textViewDate.setText(strDate);
        TextView regDate=findViewById(R.id.regDate);
        regDate.setText(strDate);

    }
}