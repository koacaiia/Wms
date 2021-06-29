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
    DatabaseReference databaseReference;
    ArrayList<OutCargoList> listOut;
    OutCargoListAdapter adapterOut;
    String refPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        recyclerViewOut=findViewById(R.id.titleRecyclerOut);
        LinearLayoutManager outManager=new LinearLayoutManager(this);
        recyclerViewOut.setLayoutManager(outManager);
        database=FirebaseDatabase.getInstance();

        getFirebaseData();
        adapterOut=new OutCargoListAdapter(listOut);
        recyclerViewOut.setAdapter(adapterOut);
        adapterOut.notifyDataSetChanged();
    }

    private void getFirebaseData() {
        listOut=new ArrayList<>();
        refPath="Outcargo2";
        databaseReference=database.getReference(refPath);
        ValueEventListener inListener=new ValueEventListener() {
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
        databaseReference.addListenerForSingleValueEvent(inListener);

    }
}