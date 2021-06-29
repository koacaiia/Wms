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

public class TitleActivity extends AppCompatActivity {
    RecyclerView recyclerViewIn;
    RecyclerView recyclerViewOut;
    FirebaseDatabase database;
    DatabaseReference databaseReferenceOut;
    DatabaseReference databaseReferenceIn;
    ArrayList<OutCargoList> listOut;
    ArrayList<Fine2IncargoList> listIn;
    OutCargoListAdapter adapterOut;
    IncargoListAdapter adapterIn;
    String refPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        recyclerViewOut=findViewById(R.id.titleRecyclerOut);
        recyclerViewIn=findViewById(R.id.titleRecyclerOutIn);
        LinearLayoutManager outManager=new LinearLayoutManager(this);
        LinearLayoutManager inManager=new LinearLayoutManager(this);
        recyclerViewOut.setLayoutManager(outManager);
        recyclerViewIn.setLayoutManager(inManager);
        database=FirebaseDatabase.getInstance();

        getFirebaseData();
        adapterOut=new OutCargoListAdapter(listOut);
        adapterIn=new IncargoListAdapter(listIn,this);
        recyclerViewOut.setAdapter(adapterOut);
        recyclerViewIn.setAdapter(adapterIn);
        adapterOut.notifyDataSetChanged();
        adapterIn.notifyDataSetChanged();
    }

    private void getFirebaseData() {
        listOut=new ArrayList<>();
        listIn=new ArrayList<>();
//        refPath="Outcargo2";
        databaseReferenceOut=database.getReference("Outcargo2");
        ValueEventListener outListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataOut:snapshot.getChildren()){
                    OutCargoList mListOut=dataOut.getValue(OutCargoList.class);
                    listOut.add(mListOut);
                }
                adapterOut.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        databaseReferenceIn=database.getReference("Incargo2");
        ValueEventListener inListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataIn:snapshot.getChildren()){
                    Fine2IncargoList mListIn=dataIn.getValue(Fine2IncargoList.class);
                    listIn.add(mListIn);
                }
                adapterIn.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        databaseReferenceIn.addListenerForSingleValueEvent(inListener);
        databaseReferenceOut.addListenerForSingleValueEvent(outListener);

    }
}