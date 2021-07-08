package fine.koaca.wms;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivitySub extends AppCompatActivity implements Serializable {
    FloatingActionButton fBtnSearch;
    RecyclerView recyclerView;
    ArrayList<Fine2IncargoList> list;
    Fine2IncargoListAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference dataRef;
    Query dataReference;
    InputMethodManager imm;
    SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);
    ArrayList<Fine2IncargoList> mSelectedList;
    String intentBl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sub);
        recyclerView=findViewById(R.id.recyclerView_list);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        list=new ArrayList<>();
        mSelectedList=new ArrayList<>();

        database=FirebaseDatabase.getInstance();
        dataRef=database.getReference("Incargo");
        dataReference=dataRef.orderByChild("consignee").equalTo("코만");
        dataReference.addListenerForSingleValueEvent(listener);
        adapter=new Fine2IncargoListAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        fBtnSearch=findViewById(R.id.fbtnSearch );
        fBtnSearch.setOnClickListener(v->{
            mSelectedList.clear();
            sortSearchDialog();
        });
        fBtnSearch.setOnLongClickListener(v->{
            dataReference.addListenerForSingleValueEvent(listener);
            adapter=new Fine2IncargoListAdapter(list);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return true;
        });

        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        intentBl=getIntent().getStringExtra("intentBl");
        if(intentBl!=null){
            String sortIntentBl=intentBl.substring(intentBl.length()-4);
            searchFirebaseData(sortIntentBl);


        }
    }

    public void locationDialog(ArrayList<Fine2IncargoList> mSelectedList, int position) {
        AlertDialog.Builder locationBuilder=new AlertDialog.Builder(this);
        EditText editText=new EditText(this);

        String bl=list.get(position).getBl();
        String des=list.get(position).getDescription();
        String count=list.get(position).getCount();
        String location=list.get(position).getLocation();
        editText.setText(location);
        Fine2IncargoList setList=list.get(position);


        locationBuilder.setTitle("Location Configuration")
                .setMessage("B/L:"+bl+"\n"+des+"("+count+")"+"\n"+"로케이션:"+location)
                .setView(editText)
                .setPositiveButton("간편등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mSelectedList.clear();
                        setList.setLocation(editText.getText().toString());
                        DatabaseReference databaseReference=
                                database.getReference("Incargo"+"/"+bl+"_"+des+
                                        "_"+count);

                       databaseReference.setValue(setList);
                       String sortBl=bl.substring(bl.length()-4);
                       searchFirebaseData(sortBl);
                        String msg=count+"_"+des+"_"+"["+location+"]"+"등록 합니다..";

                        putWorkingMessage(msg,"M&F");
                    }
                })
                .setNeutralButton("세부등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedList.clear();

                        intentLocation(setList);
                    }
                })
                .setNegativeButton("선택초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedList.clear();
                    }
                })
                .show();


    }

    private void sortSearchDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        EditText editText=new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setTitle("Search Information")
                .setMessage("Put Bl Number")
                .setView(editText);

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
                        String blName = editText.getText().toString();
                        searchFirebaseData(blName);
                    }
                });
               builder.show();

    }

    private void searchFirebaseData(String blName) {
        ValueEventListener listener=new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    Fine2IncargoList mList=data.getValue(Fine2IncargoList.class);
                    int blLength=mList.getBl().length();

                    if(blLength>4){
                        String sortBlName=mList.getBl().substring(mList.getBl().length()-4,mList.getBl().length());
                        if(sortBlName.equals(blName)){
                            list.add(mList);
                        }else{
                        }
                    }else{
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dataReference.addListenerForSingleValueEvent(listener);
    }

    ValueEventListener listener=new ValueEventListener(){

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot data:snapshot.getChildren()){
                Fine2IncargoList mList=data.getValue(Fine2IncargoList.class);
                list.add(mList);
            }
            list.sort(new IncargoListComparator("date").reversed());
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }


    };

    private void intentLocation(Fine2IncargoList setList){
        Intent intent=new Intent(MainActivitySub.this,Location.class);
        intent.putExtra("list",setList);
        startActivity(intent);

    }

    public void putWorkingMessage(String msg,String etc){
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String timeStampDate=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());
        SharedPreferences sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nick=sharedPreferences.getString("nickName","FineWareHouseDepot");
        WorkingMessageList messageList=new WorkingMessageList();
        messageList.setNickName(nick);
        messageList.setTime(timeStamp);
        messageList.setMsg(msg);
        messageList.setDate(timeStampDate);
        messageList.setConsignee(etc);
        messageList.setInOutCargo("Etc");
//        messageList.setUri("");
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference dataRef=database.getReference("WorkingMessage"+"/"+nick+"_"+timeStamp);
        dataRef.setValue(messageList);
    }
}
