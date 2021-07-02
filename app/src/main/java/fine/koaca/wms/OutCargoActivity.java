package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.StringPrepParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OutCargoActivity extends AppCompatActivity implements OutCargoListAdapter.OutCargoListAdapterClickListener , Serializable {
    FirebaseDatabase database;
    RecyclerView recyclerView;
    RecyclerView recyclerViewIn;
    ArrayList<OutCargoList> list;
    OutCargoListAdapter adapter;
    String departmentName;

    TextView txtTitle;
    String dateToDay;
    String refPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_cargo);

        dateToDay=new SimpleDateFormat("yyyy-MM-dd").format(new Date());


        recyclerView=findViewById(R.id.activity_list_outcargo_recyclerview);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        departmentName="Outcargo2";
        database= FirebaseDatabase.getInstance();

        if((ArrayList<OutCargoList>)getIntent().getSerializableExtra("listOut")==null){
            getOutcargoData();
        }else{
            list=(ArrayList<OutCargoList>)getIntent().getSerializableExtra("listOut");
        }

        adapter=new OutCargoListAdapter(list,this,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        txtTitle=findViewById(R.id.activity_list_outcargo_title);
        txtTitle.setText(dateToDay+" 출고 목록");
    }

    private void getOutcargoData() {
        list=new ArrayList<>();
        DatabaseReference databaseReference=database.getReference(departmentName);
        ValueEventListener listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    OutCargoList mList=data.getValue(OutCargoList.class);
                    list.add(mList);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);

    }

    @Override
    public void itemClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        refPath=list.get(position).getKeypath();
        itemClickedDialog();
    }



    public void itemClickedDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        AlertDialog dialog=builder.create();
        builder.setTitle("작업현황 변경사항");
        ArrayList<String> clickValue=new ArrayList<>();
        clickValue.add("사진제외 출고완료 등록");
        clickValue.add("사진포함 출고완료 등록");
        clickValue.add("미출고 등록");

        String[] clickValueList=clickValue.toArray(new String[clickValue.size()]);

        builder.setSingleChoiceItems(clickValueList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        updateValue("완");
                        break;
                    case 1:
                        updateValue("완");
                        intentImageViewActivity();
                        break;
                    case 2:
                        updateValue("미");
                        break;
                }
                dialog.dismiss();
            }
        })
                .show();
    }

    private void intentImageViewActivity() {
        Intent intent=new Intent(this,OutCargoImageViewActivity.class);
        startActivity(intent);
    }

    public void updateValue(String updateValue){
        DatabaseReference dataRef=database.getReference(departmentName+"/"+refPath);

        Map<String,Object> value=new HashMap<>();
        value.put("workprocess",updateValue);
        dataRef.updateChildren(value);

        adapter.notifyDataSetChanged();
    }
}