package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class WorkingMessageData extends AppCompatActivity implements Serializable {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    RecyclerView recyclerView;
    WorkingMessageAdapter adapter;
    ArrayList<WorkingMessageList> dataList;
    private String nickName;
    String depotName;

    EditText messageEdit;
    Button btn_send;
    String message;
    SharedPreferences sharedPreferences;
    CalendarPick calendarPick;
    String sortItemName="date";
    FloatingActionButton fab_search;
    String dialog_date;
    String dialog_consignee="ALL";

    TextView searchTextView;
    String upLoadItemsName;
    String date;
    String[] consigneeList;

    RequestQueue requestQueue;
    String alertDepot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_message_data);
        sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        nickName=sharedPreferences.getString("nickName","Fine");
        depotName=sharedPreferences.getString("depotName",null);

        if(depotName !=null){
            switch(depotName){
                case "2물류(02010027)":
                    alertDepot="Depot2";
                    break;
                case "1물류(02010810)":
                    alertDepot="Depot1";
                    break;
                case "(주)화인통상 창고사업부":
                    alertDepot="Depot";
                    break;
            }}else{
            Toast.makeText(this, "사용자등록 바랍니다.", Toast.LENGTH_SHORT).show();

            return;
        }

        recyclerView = findViewById(R.id.recyclerView_workingMessageData);
        messageEdit=findViewById(R.id.edit_workingMessageData);
        calendarPick=new CalendarPick();
        calendarPick.CalendarCall();

        date=calendarPick.date_today;
        InputMethodManager imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        database = FirebaseDatabase.getInstance();
        btn_send=findViewById(R.id.button_workignMessageData);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message=String.valueOf(messageEdit.getText().toString());
                putWorkingMessageList(message,date,nickName);
                messageEdit.setText("");
                imm.hideSoftInputFromWindow(messageEdit.getWindowToken(),0);
            }
        });
        btn_send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messageEdit.setText("");
                return true;
            }
        });
        databaseReference = database.getReference("WorkingMessage");

        getWorkingMessageLists(date);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataList=new ArrayList<WorkingMessageList>();
        adapter=new WorkingMessageAdapter(dataList,WorkingMessageData.this,nickName);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        

        adapter.setOnListImageClickListener(new OnListImageClickListener() {
          @Override
            public void onItemClickImage(WorkingMessageAdapter.ListViewHolder holder, View view, int position) {
              String strUri0=dataList.get(position).getUri0();
              String strUri1=dataList.get(position).getUri1();
              String strUri2=dataList.get(position).getUri2();
              String strUri3=dataList.get(position).getUri3();
              String strUri4=dataList.get(position).getUri4();
              ArrayList<String> uriArrayList=new ArrayList<>();
              uriArrayList.add(strUri0);
              uriArrayList.add(strUri1);
              uriArrayList.add(strUri2);
              uriArrayList.add(strUri3);
              uriArrayList.add(strUri4);

              String message=
                      dataList.get(position).getNickName()+":"+dataList.get(position).getTime()+"\n"+dataList.get(position).getMsg();


               intentImageView(uriArrayList,message);
                   }
                });



                fab_search=findViewById(R.id.btn_workMessageSearch);
                fab_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchCondition(Incargo.shared_consigneeList);
                   }
                });

        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }
    }



    public void getWorkingMessageList(String dialog_date, String dialog_consignee, String upLoadItemsName) {
        ValueEventListener postListener=new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    WorkingMessageList data=dataSnapshot.getValue(WorkingMessageList.class);
                    if(dialog_consignee.equals("ALL")){
                        assert data != null;
                        if(data.getInOutCargo().equals(upLoadItemsName)){
                            dataList.add(data);}

                    }else{
                        if(data.getConsignee().equals(dialog_consignee) && data.getInOutCargo().equals(upLoadItemsName)){
                            dataList.add(data);
                        }
                    }
                    dataList.sort(new WorkingMessageListComparator("time"));
                }

                adapter.notifyDataSetChanged();
                messageEdit.setText(dialog_date+"_"+dialog_consignee+"_"+upLoadItemsName+"조회결과");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WorkingMessageData.this, "Database Error", Toast.LENGTH_SHORT).show();

            }
        };
        Query sortItem ;
        if(dialog_date.equals("All Time")){
            sortItem=databaseReference.orderByChild(sortItemName);
        }else{
            sortItem=databaseReference.orderByChild(sortItemName).equalTo(dialog_date);
        }

        sortItem.addListenerForSingleValueEvent(postListener);

    }


    public void getWorkingMessageLists(String date){
        ValueEventListener listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    WorkingMessageList mList=data.getValue(WorkingMessageList.class);

                    if(mList.getDate()!=null&&mList.getDate().equals(date)){
                        dataList.add(mList);
                       }
                    dataList.sort(new WorkingMessageListComparator("time"));
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);






        }



   public void putWorkingMessageList(String msg,String date,String nick){
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());

        WorkingMessageList messageList=new WorkingMessageList();

        String consignee="Etc";
        String inOutCargo="Etc";
        messageList.setNickName(nick);
        messageList.setTime(timeStamp);
        messageList.setMsg(msg);
        messageList.setDate(date);
        messageList.setConsignee(consignee);
        messageList.setInOutCargo(inOutCargo);
//        messageList.setUri("");

        databaseReference = database.getReference("WorkingMessage"+"/"+nick+"_"+timeStamp);
        databaseReference.setValue(messageList);

       PushFcmProgress push=new PushFcmProgress(requestQueue);
       push.sendAlertMessage(alertDepot,nickName,message,"WorkingMessage");
//        adapter.notifyDataSetChanged();
       intentWorkMessageActivity();



   }
   public void intentImageView(ArrayList<String> uri, String message){
        Intent intent=new Intent(WorkingMessageData.this,ImageViewActivity.class);
        String[] uriList=uri.toArray(new String[uri.size()]);
        intent.putExtra("uri",uriList);
        intent.putExtra("message",message);

        startActivity(intent);
   }
   public void searchCondition(String[] consigneeList){

        dialog_date="All Time";
        getWorkingMessageList(dialog_date, dialog_consignee, upLoadItemsName);

//       consigneeList = new String[]{"ALL", "M&F", "SPC", "공차", "케이비켐", "BNI", "기타", "스위치코리아", "서강비철", "한큐한신", "하랄코", "Etc"};



        AlertDialog.Builder searchBuilder=new AlertDialog.Builder(this);
       searchBuilder.setTitle("검색 조건 설정창");
       View view=getLayoutInflater().inflate(R.layout.spinnerlist_searchitem,null);
       Button searchButton=view.findViewById(R.id.workmessage_inputdate);
       Spinner searchSpinner=view.findViewById(R.id.workmessage_spinner);
       searchTextView=view.findViewById(R.id.workmessage_text);
       searchTextView.setText("All Time");
       ArrayAdapter<String> searchAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
               consigneeList);
       searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       searchSpinner.setAdapter(searchAdapter);

       searchButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String a="c";
               DatePickerFragment datePickerFragment=new DatePickerFragment(a);
               datePickerFragment.show(getSupportFragmentManager(),"datePicker");
               searchTextView.setText(dialog_date);
           }
       });

       searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               dialog_consignee=consigneeList[position];
               searchTextView.append("_"+dialog_consignee);

           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

       searchBuilder.setView(view);
       searchBuilder.setPositiveButton("출고 검색", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               upLoadItemsName="OutCargo";
               getWorkingMessageList(dialog_date,dialog_consignee,upLoadItemsName);


           }
       });
       searchBuilder.setNegativeButton("입고 검색", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               upLoadItemsName="InCargo";
               Log.i("TestValue",
                       "dataValue+++:"+dialog_date+"ConsigneeNameValue+++:"+dialog_consignee+"UpLoadValue+++:"+upLoadItemsName);
               getWorkingMessageList(dialog_date,dialog_consignee,upLoadItemsName);

           }
       });
       searchBuilder.setNeutralButton("기타 검색", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               upLoadItemsName="Etc";
               getWorkingMessageList(dialog_date,dialog_consignee,upLoadItemsName);

           }
       });
       searchBuilder.show();


   }
    public void processDatePickerResult(int year, int month, int dayOfMonth) {
        String month_string;
        if(month<10){
            month_string="0"+Integer.toString(month+1);
        }else{
            month_string=Integer.toString(month+1);
        }
        String day_string;
        if(dayOfMonth<10){
            day_string="0"+Integer.toString(dayOfMonth);
        }else{
            day_string=Integer.toString(dayOfMonth);
        }
        String year_string=Integer.toString(year);

          dialog_date=(year_string+"년"+month_string+"월"+day_string+"일");
          searchTextView.setText(dialog_date);


    }


    private class WorkingMessageListComparator implements Comparator<WorkingMessageList> {
        String time;
        public WorkingMessageListComparator(String time) {
            this.time=time;
        }

        @Override
        public int compare(WorkingMessageList a, WorkingMessageList b) {
            int compare=0;
            compare=b.time.replaceAll("[^0-9]", "").compareTo(a.time.replaceAll("[^0-9]", ""));
            return compare;
        }
    }

    public void intentWorkMessageActivity(){
        Intent intent=new Intent(WorkingMessageData.this,WorkingMessageData.class);
        startActivity(intent);

    }
}