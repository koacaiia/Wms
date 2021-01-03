package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

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
    private String nick;
    EditText messageEdit;
    Button btn_send;
    String message;
    SharedPreferences sharedPreferences;



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
                message=messageEdit.getText().toString();
                putWorkingMessageList(message);

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

   public void putWorkingMessageList(String msg){
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        nick=sharedPreferences.getString("nickName","koaca");
        WorkingMessageList messageList=new WorkingMessageList();
        messageList.setNickName(nick);
        Log.i("koacaiia",nick+"___Log nickName");
        messageList.setTime(timeStamp);
        messageList.setMsg(msg);
        databaseReference.push().setValue(messageList);


   }

}