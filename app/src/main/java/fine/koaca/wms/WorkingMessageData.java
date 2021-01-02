package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
    RecyclerView.Adapter adapter;
    ArrayList<WorkingMessageList> dataList;
    private String nick="koaca";
    EditText messageEdit;
    Button btn_send;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_message_data);
        recyclerView = findViewById(R.id.recyclerView_workingMessageData);
        messageEdit=findViewById(R.id.edit_workingMessageData);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("WorkingMessage");

        btn_send=findViewById(R.id.button_workignMessageData);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkingMessageList messageList=new WorkingMessageList();
                messageList.setNickName("koaca");
                messageList.setMsg(messageEdit.getText().toString());
                databaseReference.push().setValue(messageList);

            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataList=new ArrayList<WorkingMessageList>();
        adapter=new WorkMessageAdapter(dataList,WorkingMessageData.this,nick);
        recyclerView.setAdapter(adapter);

        getWorkingMessageList();


    }

    public void getWorkingMessageList() {

        String timeStamp1 = new SimpleDateFormat("yyyy년MM월dd일E요일").format(new Date());
        String timeStamp2 = new SimpleDateFormat("a_HH시mm분ss초").format(new Date());
//        databaseReference.setValue(timeStamp1 + timeStamp2 + workingMessage);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                WorkingMessageList data=snapshot.getValue(WorkingMessageList.class);
                ((WorkMessageAdapter) adapter).addWorkingMessage(data);

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