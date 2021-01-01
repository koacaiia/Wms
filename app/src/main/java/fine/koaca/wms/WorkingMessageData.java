package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkingMessageData extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ArrayList<WorkingMessageList> dataList = new ArrayList<WorkingMessageList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_message_data);
        recyclerView = findViewById(R.id.recyclerView_workingMessageData);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void workingMessageList(String workingMessage) {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("WorkingMessage");
        String timeStamp1 = new SimpleDateFormat("yyyy년MM월dd일E요일").format(new Date());
        String timeStamp2 = new SimpleDateFormat("a_HH시mm분ss초").format(new Date());
        databaseReference.setValue(timeStamp1 + timeStamp2 + workingMessage);
    }

    private void getArrayList(boolean add) {
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if (add) {

//                Fine2IncargoList list=new Fine2IncargoList(msg,nickname);
//
//                postValues=list.toMap();
//                childUpdates.put(bl+"_"+description+"_"+count+"/",postValues);}
//            childUpdates.put(bl+"_"+description+"_"+count+"/",postValues);
//            databaseReference.updateChildren(childUpdates);}

        }
    }
}