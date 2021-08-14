package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import java.util.HashMap;
import java.util.Map;

public class WorkingMessageData extends AppCompatActivity implements Serializable {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    RecyclerView recyclerView;
    WorkingMessageAdapter adapter;
    ArrayList<WorkingMessageList> dataList;

    EditText messageEdit;
    Button btn_send;
    String message;
    CalendarPick calendarPick;
    String sortItemName="date";
    FloatingActionButton fab_search;
    String dialog_date;
    String dialog_consignee="ALL";

    TextView searchTextView;
    String upLoadItemsName;
    String date;
    String[] consigneeList;

    String pickedDate;

    String nickName;
    String deptName;

    PublicMethod publicMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_message_data);

        recyclerView = findViewById(R.id.recyclerView_workingMessageData);
        messageEdit=findViewById(R.id.edit_workingMessageData);
        calendarPick=new CalendarPick();
        calendarPick.CalendarCall();

        publicMethod=new PublicMethod(this);
        nickName=publicMethod.getUserInformation().get("nickName");
        deptName=publicMethod.getUserInformation().get("deptName");

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
        databaseReference = database.getReference("DeptName/"+deptName+"/WorkingMessage");

        getWorkingMessageLists(date);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataList=new ArrayList<WorkingMessageList>();
        adapter=new WorkingMessageAdapter(dataList,WorkingMessageData.this,nickName);
        recyclerView.setAdapter(adapter);


        

        adapter.setOnListImageClickListener(new OnListImageClickListener() {
          @Override
            public void onItemClickImage(WorkingMessageAdapter.ListViewHolder holder, View view, int position) {
              String strUri0=dataList.get(position).getUri0();
              String strUri1=dataList.get(position).getUri1();
              String strUri2=dataList.get(position).getUri2();
              String strUri3=dataList.get(position).getUri3();
              String strUri4=dataList.get(position).getUri4();
              String strUri5=dataList.get(position).getUri5();
              String strUri6=dataList.get(position).getUri6();
              ArrayList<String> uriArrayList=new ArrayList<>();
              uriArrayList.add(strUri0);
              uriArrayList.add(strUri1);
              uriArrayList.add(strUri2);
              uriArrayList.add(strUri3);
              uriArrayList.add(strUri4);
              uriArrayList.add(strUri5);
              uriArrayList.add(strUri6);

              String message=
                      dataList.get(position).getNickName()+":"+dataList.get(position).getTime()+"\n"+dataList.get(position).getMsg();


               intentImageView(uriArrayList,message);
                   }
                });



                fab_search=findViewById(R.id.btn_workMessageSearch);
                fab_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchCondition();
                   }
                });

                fab_search.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        workingMessageListSortByDate();
                        return true;
                    }
                });



    }


    private void workingMessageListSortByDate() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        DatePicker pickerDialog=new DatePicker(this);

        builder.setTitle("항목 삭제")
                .setMessage("지정일 이전 메세지 항목 삭제진행"+"\n"+"기존 서버자료 필히 백업후 진행 바랍니다")
                .setView(pickerDialog)
                .setPositiveButton("목록삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataList.clear();                      ArrayList<String> pathKey=new ArrayList<>();

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                for(DataSnapshot data:snapshot.getChildren()){
                                    WorkingMessageList mList=data.getValue(WorkingMessageList.class);
                                    String key=data.getKey();
                                    dataList.add(mList);
                                    pathKey.add(key);

                                }

                                int dataListSize=dataList.size();
                                for(int i=0;i<dataListSize;i++){
                                    String transDate;
                                    String pickedDateRe;

                                    pickedDateRe=pickedDate.replaceAll("-","");
                                    int intPickedDateRe=Integer.parseInt(pickedDateRe);
                                    if(dataList.get(i).getDate()!=null){
                                        transDate=dataList.get(i).getDate().replaceAll("-","");
                                    }else{
                                        transDate=("20210101");
                                    }

                                    int intTransDate=Integer.parseInt(transDate);

                                    if(intTransDate<intPickedDateRe){

                                        Map<String,Object> value=new HashMap<>();
                                        value.put(pathKey.get(i)+"/",null);


                                        databaseReference.updateChildren(value);
                                    }

                                }
                                adapter.notifyDataSetChanged();
                            }


                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                        }
                })
                .setNegativeButton("Test항목 삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataList.clear();
                        ArrayList<String> pathKey=new ArrayList<>();

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                for(DataSnapshot data:snapshot.getChildren()){
                                    WorkingMessageList mList=data.getValue(WorkingMessageList.class);
                                    String key=data.getKey();
                                    dataList.add(mList);
                                    pathKey.add(key);
                                }
                                int dataListSize=dataList.size();
                                for(int i=0;i<dataListSize;i++){
                                    String nickName=dataList.get(i).nickName;
                                    if(nickName.equals("Test")){
                                        Map<String,Object> value=new HashMap<>();
                                        value.put(pathKey.get(i)+"/",null);
                                        Log.i("TestValue","Time getValue::::"+dataList.get(i).getTime());
                                        databaseReference.updateChildren(value);
                                    }

                                }
                                adapter.notifyDataSetChanged();
                            }


                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });

                    }
                })
                .show();
        pickerDialog.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String month;
                String date;
                if((monthOfYear+1)<10){
                    month="-0"+(monthOfYear+1)+"-";
                }else{
                    month="-"+(monthOfYear+1)+"-";
                }
                if(dayOfMonth<10){
                    date="0"+dayOfMonth;
                    }else{
                    date=String.valueOf(dayOfMonth);
                }
                pickedDate=year+month+date;
                Toast.makeText(getApplicationContext(),pickedDate+"선택 되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getWorkingMessageList(String dialog_date, String dialog_consignee, String upLoadItemsName) {
        ValueEventListener postListener=new ValueEventListener(){
            @SuppressLint("NotifyDataSetChanged")
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

                    assert mList != null;
                    String dateRe=mList.getDate().replace("년","-").replace("월","-").replace("일","");
                    DatabaseReference dataRefRe=
                            database.getReference("DeptName/"+deptName+"/WorkingMessage/"+mList.getNickName()+"_"+mList.getTime());
                    Map<String,Object> value=new HashMap<>();
                    value.put("date",dateRe);

                    dataRefRe.updateChildren(value);
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


        databaseReference = database.getReference("DeptName"+"/"+deptName+"/WorkingMessage/"+nickName+"_"+timeStamp);
        databaseReference.setValue(messageList);


       publicMethod.sendPushMessage(deptName,nickName,message,"WorkingMessage");

       intentWorkMessageActivity();



   }
   public void intentImageView(ArrayList<String> uri, String message){
        Intent intent=new Intent(WorkingMessageData.this,ImageViewActivity.class);
        String[] uriList=uri.toArray(new String[uri.size()]);
        intent.putExtra("uri",uriList);
        intent.putExtra("message",message);

        startActivity(intent);
   }
   public void searchCondition(){
        ArrayList<String> consigneeArrayList=new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    WorkingMessageList mList=data.getValue(WorkingMessageList.class);

//                    assert mList !=null:data.getKey();
                    if(mList.getConsignee()==null){

                        String nickName=mList.getNickName();
                        String timeStamp=mList.getTime();
                        DatabaseReference databaseReference=database.getReference("DeptName"+"/"+deptName+"/WorkingMessage/"+nickName+"_"+timeStamp);
                        Map<String,Object> value=new HashMap<>();
                        value.put("msg","ConsigneeName Null Checked:::::"+mList.getTime());
                        value.put("consignee","Null");
                        databaseReference.updateChildren(value);
                    }
                    String consigneeName=mList.getConsignee();

                    if(!consigneeArrayList.contains(consigneeName)){
                        consigneeArrayList.add(consigneeName);

                    }

                }

                consigneeArrayList.add(0,"ALL");
                consigneeList=consigneeArrayList.toArray(new String[consigneeArrayList.size()]);
                dialog_date="All Time";

                AlertDialog.Builder searchBuilder=new AlertDialog.Builder(WorkingMessageData.this);
                searchBuilder.setTitle("검색 조건 설정창");
                View view=getLayoutInflater().inflate(R.layout.spinnerlist_searchitem,null);
                Button searchButton=view.findViewById(R.id.workmessage_inputdate);
                Spinner searchSpinner=view.findViewById(R.id.workmessage_spinner);
                searchTextView=view.findViewById(R.id.workmessage_text);
                searchTextView.setText("All Time");
                ArrayAdapter<String> searchAdapter=new ArrayAdapter<String>(WorkingMessageData.this,
                        android.R.layout.simple_spinner_dropdown_item,
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

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



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

          dialog_date=(year_string+"-"+month_string+"-"+day_string);
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
    @Override
    public void onBackPressed() {

       publicMethod.intentSelect();
    }
}