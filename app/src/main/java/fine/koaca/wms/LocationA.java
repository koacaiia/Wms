package fine.koaca.wms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LocationA extends AppCompatActivity {
CheckBox chk_a1,chk_a2,chk_a3,chk_a4,chk_ab1,chk_ab2,chk_ab3,chk_ab4,chk_b1,chk_b2,chk_b3,chk_b4,chk_c1,chk_c2,chk_c3,chk_c4,
        chk_cd1,chk_cd2,chk_cd3,chk_cd4,chk_d1,chk_d2,chk_d3,chk_d4,chk_e1,chk_e2,chk_e3,chk_e4,chk_ef1,chk_ef2,chk_ef3,chk_ef4,chk_f1,chk_f2,chk_f3,chk_f4;

    Integer[] rackCount={0,1,2,3,4,5};
    Spinner spinner_rack;

TextView tex_chkValue;
String str_location;
String str_rackCount;
String intent_bl;
String intent_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_backup);

        Intent intent=getIntent();
        intent_bl=intent.getStringExtra("bl");
        intent_des=intent.getStringExtra("des");



        Button button=findViewById(R.id.btn_locationReg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterChkBox();
            }
        });

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String intent_location=tex_chkValue.getText().toString();
                Intent intent=new Intent(fine.koaca.wms.LocationA.this,MainActivity.class);
                intent.putExtra("location",intent_location);
                intent.putExtra("bl",intent_bl);
                intent.putExtra("des",intent_des);
                startActivity(intent);
                return true;
            }
        });

        spinner_rack=findViewById(R.id.spinner_rack);
        ArrayAdapter<Integer> rackAdapter=new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,rackCount);
        rackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_rack.setAdapter(rackAdapter);
        spinner_rack.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_rack.setTag(rackCount[position]);

                str_rackCount=Integer.toString(rackCount[position]);
//                filterChkBox();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        tex_chkValue=findViewById(R.id.txt_location);
        tex_chkValue.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tex_chkValue.setText("");

                return true;
            }
        });
        chk_a1=findViewById(R.id.a1);
        chk_a2=findViewById(R.id.a2); chk_a3=findViewById(R.id.a3); chk_a4=findViewById(R.id.a4);
        chk_ab1=findViewById(R.id.ab1); chk_ab2=findViewById(R.id.ab2); chk_ab3=findViewById(R.id.ab3); chk_ab4=
                findViewById(R.id.ab4);
        chk_b1=findViewById(R.id.b1); chk_b2=findViewById(R.id.b2); chk_b3=findViewById(R.id.b3); chk_b4=findViewById(R.id.b4);
        chk_c1=findViewById(R.id.c1); chk_c2=findViewById(R.id.c2); chk_c3=findViewById(R.id.c3); chk_c4=findViewById(R.id.c4);
        chk_d1=findViewById(R.id.d1); chk_d2=findViewById(R.id.d2); chk_d3=findViewById(R.id.d3); chk_d4=findViewById(R.id.d4);
        chk_cd1=findViewById(R.id.cd1); chk_cd2=findViewById(R.id.cd2); chk_cd3=findViewById(R.id.cd3); chk_cd4=
                findViewById(R.id.cd4);
        chk_e1=findViewById(R.id.e1); chk_e2=findViewById(R.id.e2); chk_e3=findViewById(R.id.e3); chk_e4=findViewById(R.id.e4);
        chk_ef1=findViewById(R.id.ef1); chk_ef2=findViewById(R.id.ef2); chk_ef3=findViewById(R.id.ef3); chk_ef4=
                findViewById(R.id.ef4);
        chk_f1=findViewById(R.id.f1); chk_f2=findViewById(R.id.f2); chk_f3=findViewById(R.id.f3); chk_f4=findViewById(R.id.f4);




    }
    public void filterChkBox(){
        CheckBox [] chkBox_list=new CheckBox[]{chk_a1,chk_a2,chk_a3,chk_a4,chk_ab1,chk_ab2,chk_ab3,chk_ab4,chk_b1,chk_b2,chk_b3,chk_b4,
                chk_c1,chk_c2,
                chk_c3,chk_c4,
                chk_cd1,chk_cd2,chk_cd3,chk_cd4,chk_d1,chk_d2,chk_d3,chk_d4,chk_e1,chk_e2,chk_e3,chk_e4,chk_ef1,chk_ef2,chk_ef3,chk_ef4
                ,chk_f1,chk_f2,chk_f3,chk_f4};
        int chkCount=chkBox_list.length;

        for(int i=0;i<chkCount;i++){
            if(chkBox_list[i].isChecked()){
                String chkValue=chkBox_list[i].getText().toString();

                    tex_chkValue.append("  "+chkValue+" ");

                }
            }
        str_location=tex_chkValue.getText().toString();

        tex_chkValue.setText(str_location+"_"+str_rackCount);

        }

    }
