package fine.koaca.wms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    Button btnWork,btnDate,btnConsignee,btnDes,btnCont,btnType,btnQty,btnBl,btnRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_data_reg);

        editWork=findViewById(R.id.editWorking);
        editBl=findViewById(R.id.editBl);
        editDate=findViewById(R.id.editDate);
        editConsignee=findViewById(R.id.editConsignee);
        editDes=findViewById(R.id.editDes);
        editCont=findViewById(R.id.editCont);
        editType=findViewById(R.id.editType);
        editQty=findViewById(R.id.editQty);
        editRemark=findViewById(R.id.editRemark);

        btnWork=findViewById(R.id.btnWorking);
        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editWork=findViewById(R.id.editWorking);
                strWork=editWork.getText().toString();
                TextView btnWork=findViewById(R.id.regWorking);
                btnWork.setText(strWork);
            }
        });
        btnDate=findViewById(R.id.btnDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           EditText editDate=findViewById(R.id.editDate);
           strDate=editDate.getText().toString();
           TextView btnDate=findViewById(R.id.regDate);
           btnDate.setText(strDate);
            }
        });
        btnConsignee=findViewById(R.id.btnConsignee);
        btnConsignee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editConsignee=findViewById(R.id.editConsignee);
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
            EditText editType=findViewById(R.id.editType);
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


}

public void putEditDate(){
        AlertDialog.Builder editBuilder=new AlertDialog.Builder(this);

}
}