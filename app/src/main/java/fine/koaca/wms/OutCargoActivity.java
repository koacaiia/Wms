package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OutCargoActivity extends AppCompatActivity {
    FirebaseDatabase database;
    RecyclerView recyclerView;
    RecyclerView recyclerViewIn;
    ArrayList<OutCargoList> list;
    OutCargoListAdapter adapter;
    String departmentName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_cargo);

        recyclerView=findViewById(R.id.activity_list_outcargo_recyclerview);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        departmentName="Outcargo2";
        database= FirebaseDatabase.getInstance();
        getOutcargoData();
        adapter=new OutCargoListAdapter(list,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
}