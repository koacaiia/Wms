package fine.koaca.wms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Calendar;
import java.util.Collections;

public class Incargo extends AppCompatActivity implements Serializable {
    ArrayList<Fine2IncargoList> listItems;
    fine.koaca.wms.IncargoListAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    String sort_dialog="dialogsort";
    String sortConsignee;

    ArrayList<String> arrConsignee = new ArrayList<>();
    String [] consignee_list;
    String [] consignee_list2;

    String dataMessage;
    Button incargo_location;
    Button incargo_mnf;
    String str_sort="long";
    String str_sort_date="today_init";

    TextView incargo_incargo;
    TextView incargo_contents_date;
    TextView incargo_contents_consignee;
    String incargo_consignee;
    String container40;
    String container20;
    String lclcargo;
    String inCargo;

    TextView dia_date;
    TextView dia_consignee;

    String day_start;
    String day_end;
    String dia_dateInit="";

    String depotName;
    String nickName;

    static private String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    FloatingActionButton fltBtn_Capture;
    FloatingActionButton fltBtn_Search;
    String searchContents;

    String downLoadingMark="";

    public Incargo(ArrayList<Fine2IncargoList> listItems) {
        this.listItems=listItems;
    }

    public Incargo(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incargo);

        sharedPref=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        if(sharedPref==null){
            depotName="2물류(02010027)";
        }else{
            depotName=sharedPref.getString("depotName","koaca");
        }
        nickName=sharedPref.getString("nickName","koaca");
        dataMessage = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        incargo_incargo=findViewById(R.id.incargo_incargo);
        incargo_contents_date=findViewById(R.id.incargo_contents_date);
        incargo_contents_consignee=findViewById(R.id.incargo_contents_consignee);
        incargo_mnf=findViewById(R.id.incargo_mnf);
        recyclerView=findViewById(R.id.incargo_recyclerViewList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        listItems=new ArrayList<>();

        database=FirebaseDatabase.getInstance();
        if(depotName !=null){
        switch(depotName){
            case "2물류(02010027)":
                databaseReference=database.getReference("Incargo");
                break;
            case "1물류(02010810)":
                databaseReference=database.getReference("Incargo");
                Log.i("depotSort1","1물류 화물조회는 아직 미구현 입니다.");
                break;
            case "(주)화인통상 창고사업부":
                databaseReference=database.getReference("Incargo");
                Log.i("depotSort2","사업부 화물조회는 아직 미구현 입니다.");
                break;
        }}else{
            Toast.makeText(this, "사용자등록 바랍니다.", Toast.LENGTH_SHORT).show();
            databaseReference=database.getReference("Incargo");
        }
        adapter=new fine.koaca.wms.IncargoListAdapter(listItems,this);
        recyclerView.setAdapter(adapter);
        adapter.setAdapterClickListener(new fine.koaca.wms.IncargoListAdapter.AdapterClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String sortItemName=listItems.get(pos).getContainer();
                String filterItemName="container";
                sortGetFirebaseIncargoDatabase(filterItemName,sortItemName);
            }
        });
        adapter.setAdaptLongClickListener(new fine.koaca.wms.IncargoListAdapter.AdapterLongClickListener() {
            @Override
            public void onLongItemClick(View v, int pos) {
                String sortItemName=listItems.get(pos).getConsignee();
                String filterItemName="consignee";
                sortGetFirebaseIncargoDatabase(filterItemName,sortItemName);
            }
        });

        incargo_location=findViewById(R.id.incargo_reset);
        incargo_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_dialog="dialogsort";
                str_sort_date="today_init";
                str_sort="long";
                dia_dateInit=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                getFirebaseIncargoDatabase();
            }
        });
        incargo_mnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Incargo.this,MainActivity.class);
                startActivity(intent);
            }
        });

        incargo_mnf.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(Incargo.this,Activity_Exercise.class);
                startActivity(intent);
                return true;
            }
        });
        dia_dateInit=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        getFirebaseIncargoDatabase();
        incargo_consignee="전 화물";
        incargo_contents_date.setText(dia_dateInit);
        incargo_contents_consignee.setText(incargo_consignee+"_"+"입고현황");

      fltBtn_Capture=findViewById(R.id.incargo_floatBtn_Capture);
      fltBtn_Capture.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(Incargo.this,CameraCapture.class);
              startActivity(intent);
          }
      });

      fltBtn_Capture.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
              Intent intent=new Intent(Incargo.this,CameraCaptureTextureView.class);
              startActivity(intent);
              return true;
          }
      });
      fltBtn_Search=findViewById(R.id.incargo_floatBtn_search);
      fltBtn_Search.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
//              fcmSelected();
//            searchSort();
            Intent intent=new Intent(Incargo.this,WorkingMessageData.class);
            intent.putExtra("nickName",nickName);
            startActivity(intent);
          }
      });
      fltBtn_Search.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {

              return true;
          }
      });

    }

    private void searchSort() {
        AlertDialog.Builder searchBuilder=new AlertDialog.Builder(fine.koaca.wms.Incargo.this);
        final EditText editText=new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        searchBuilder.setTitle("B/L 또는 컨테이너 번호 조회");
        searchBuilder.setMessage("마지막 4자리 번호 입력 바랍니다.");
        searchBuilder.setView(editText);

        searchBuilder.setPositiveButton("Bl", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sortContents=editText.getText().toString();
                searchContents="bl";
                searchFirebaseDatabaseToArray(sortContents);

            }
        });
        searchBuilder.setNegativeButton("container", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sortContents=editText.getText().toString();
                searchContents="container";
                searchFirebaseDatabaseToArray(sortContents);
            }
        });
        searchBuilder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        searchBuilder.show();
    }
    private void sortGetFirebaseIncargoDatabase(String filterItemName,String sortItemName) {
        ValueEventListener sortItemsListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Fine2IncargoList data=dataSnapshot.getValue(Fine2IncargoList.class);

                    if(!dia_dateInit.equals(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()))){
                        listItems.add(data);
                    }else{

                    if(dia_dateInit.equals(data.getDate())){
                    listItems.add(data);}}
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Query sortContainer=databaseReference.orderByChild(filterItemName).equalTo(sortItemName);
        sortContainer.addListenerForSingleValueEvent(sortItemsListener);
    }

    public void getFirebaseIncargoDatabase(){
        ValueEventListener incargoListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                arrConsignee.clear();
                container40="";
                container20="";
                lclcargo="";
                inCargo="";

                ArrayList<Integer> list_40 = new ArrayList<Integer>();
                ArrayList<Integer> list_20=new ArrayList<Integer>();
                ArrayList<Integer> list_lclcargo=new ArrayList<Integer>();
                ArrayList<Integer> list_incargo=new ArrayList<Integer>();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Fine2IncargoList data=dataSnapshot.getValue(Fine2IncargoList.class);
                    if(str_sort.equals("long")){
                        listItems.add(data);}
                    else if(str_sort.equals("sort")){
                        if(dia_consignee.getText().toString().equals("All")){
                            listItems.add(data);
                        }
                        if(data.getConsignee().equals(dia_consignee.getText().toString())){
                            listItems.add(data);
                        }else{}
                        dia_dateInit=dia_date.getText().toString();
                    }
                }

                Collections.reverse(listItems);
                int listItems_count=listItems.size();
                int sum40=0;
                int sum20=0;
                int lcl=0;
                int sumIncargo=0;
                for(int i=0;i<listItems_count;i++){
                    String str_consignee=listItems.get(i).getConsignee();
                    int int_40=Integer.parseInt(listItems.get(i).getContainer40());
                    int int_20=Integer.parseInt(listItems.get(i).getContainer20());
                    int int_lclcargo=Integer.parseInt(listItems.get(i).getLclcargo());
                    int int_incargo=Integer.parseInt(listItems.get(i).getIncargo());
                    list_40.add(int_40);
                    list_20.add(int_20);
                    list_lclcargo.add(int_lclcargo);
                    list_incargo.add(int_incargo);
                    sum40=sum40+list_40.get(i);
                    sum20=sum20+list_20.get(i);
                    lcl=lcl+list_lclcargo.get(i);
                    sumIncargo=sumIncargo+list_incargo.get(i);

                    container40="40FT*"+sum40;
                    container20="20FT*"+sum20;
                    lclcargo="LcL화물:"+lcl+"PLT";
                    inCargo="총 입고 팔렛트 수량:"+sumIncargo+"(PLT)";
                    arrConsignee.add("All");
                    arrConsignee.add(str_consignee);

                }
                consignee_list=arrConsignee.toArray(new String[arrConsignee.size()]);
                arrConsignee.clear();
                for(String item : consignee_list){
                    if(!arrConsignee.contains(item))
                        arrConsignee.add(item);}

                consignee_list2=arrConsignee.toArray(new String[arrConsignee.size()]);
                adapter.notifyDataSetChanged();
                if(sort_dialog.equals("dialogsort")){
                dialogMessage(container40,container20,lclcargo,inCargo,consignee_list2);

                }else if(sort_dialog.equals("init")){
                    dialogMessage(consignee_list2);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Data Server connection Error", Toast.LENGTH_SHORT).show();
            }
        };
        Query sortbyDate = null;

      switch(str_sort_date){
          case "total":
          sortbyDate = databaseReference.orderByChild("date");
              Log.i("dateInit","total");
          break;
          case "fixed1":
              sortbyDate=databaseReference.orderByChild("date").equalTo(dia_dateInit);

              break;
          case "fixed2":
              sortbyDate=databaseReference.orderByChild("date").startAt(day_start).endAt(day_end);
              break;
          case "today_init":
              sortbyDate=
                      databaseReference.orderByChild("date").equalTo(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
      }
        sortbyDate.addListenerForSingleValueEvent(incargoListener);

    }

    private void dialogMessage(String[] consignee_list2) {
        final String fixedDate = "날짜지정";
        ArrayList<String> dateSelected=new ArrayList<String>();
        dateSelected.add("내일 전체화물 입고 일정");
        dateSelected.add(fixedDate);
        dateSelected.add("이번 주");
        dateSelected.add("다음 주");
        dateSelected.add("이번 달");
        dateSelected.add("전체");
        ArrayList selectedItems=new ArrayList();
        int defaultItem=0;
        selectedItems.add(defaultItem);
        this.consignee_list2=consignee_list2;

        String[] dateList=dateSelected.toArray(new String[dateSelected.size()]);
        AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
        View view=getLayoutInflater().inflate(R.layout.spinnerlist,null);
        Spinner sp=view.findViewById(R.id.incargo_spinner_listconsignee1);
        dia_date=view.findViewById(R.id.dia_date);
        dia_date.setText(dia_dateInit);
        dia_consignee=view.findViewById(R.id.dia_consignee);
        ArrayAdapter<String> consigneelistAdapter=new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, fine.koaca.wms.Incargo.this.consignee_list2);
        consigneelistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(consigneelistAdapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortConsignee=consignee_list2[position];
                Log.i("koaca_consignee",sortConsignee);
                dia_consignee.setText(sortConsignee);
                incargo_contents_consignee.setText("_"+sortConsignee+"_입고현황");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(view);
//        builder.create();
//        AlertDialog ad=builder.create();
        builder.setSingleChoiceItems(dateList,defaultItem,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fine.koaca.wms.CalendarPick calendarPick=new fine.koaca.wms.CalendarPick();
                calendarPick.CalendarCall();
                dia_consignee.setText("All");
                switch(which){
                    case 0:
                        String tomorrow=calendarPick.date_tomorrow;
                        Log.i("datetomorrow",tomorrow);
                         dia_dateInit=tomorrow;
                         str_sort_date="fixed1";
                         str_sort="sort";
                         sort_dialog="dialogsort";
                        getFirebaseIncargoDatabase();
                        break;
                    case 1:
                        String a="b";
                        str_sort_date="fixed1";
                        str_sort="sort";
                        DatePickerFragment datePickerFragment=new DatePickerFragment(a);
                        datePickerFragment.show(getSupportFragmentManager(),"datePicker");

                    break;
                    case 2:
                        str_sort_date="fixed2";
                        str_sort="sort";
                       day_start=calendarPick.date_mon;
                       day_end=calendarPick.date_sat;
                       dia_dateInit=day_start+"~"+day_end;
                        break;
                    case 3:
                        str_sort_date="fixed2";
                        str_sort="sort";
                        day_start=calendarPick.date_Nmon;
                        day_end=calendarPick.date_Nsat;
                        dia_dateInit=day_start+"~"+day_end;
                                             break;
                    case 4:
                        str_sort="sort";
                        str_sort_date="fixed2";
                        day_start=calendarPick.year+"-"+calendarPick.month+"-"+"01";
                        day_end=calendarPick.year+"-"+calendarPick.month+"-"+calendarPick.date_lastMonth;
                        dia_dateInit=day_start+"~"+day_end;
                        break;
                    case 5:
                       day_start="전체";
                       day_end="조회";
                       str_sort="long";
                       str_sort_date="total";
                       dia_dateInit="전화물 ";
                        break;
                }
                dia_date.setText(dia_dateInit);
                incargo_contents_date.setText(dia_dateInit);
            }
        });

        builder.setPositiveButton("조회", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sort_dialog="dialogsort";
                Toast.makeText(fine.koaca.wms.Incargo.this, "화주명:"+dia_consignee.getText().toString()+"\n"+"검색기간"+dia_date.getText().toString()+
                                "\n"+
                                "화물을 " +
                                "조회 합니다.",
                        Toast.LENGTH_LONG).show();
                getFirebaseIncargoDatabase();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setTitle("조건별 화물 조회");
        builder.show();
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


        if(downLoadingMark.equals("DownLoadingOk")){
            dataMessage=(year_string+"년"+month_string+"월"+day_string+"일");
            downLoadDialogMessage(dataMessage);
        }else{
            dataMessage=(year_string+"-"+month_string+"-"+day_string);
        dia_date.setText(dataMessage);
        dia_dateInit=dataMessage;}
    }


    public void dialogMessage(String container40, String container20, String lclcargo, String inCargo, String[] consignee_list2){
        this.consignee_list2=consignee_list2;

        AlertDialog.Builder dialog=new AlertDialog.Builder(fine.koaca.wms.Incargo.this);
        dialog.setTitle(dia_dateInit+"__"+"입고 화물 현황");
        incargo_contents_date.setText(dia_dateInit);
        dialog.setMessage(container40 +"\n"+container20 +"\n"+ lclcargo+"\n"+inCargo);
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setNegativeButton("전체 화물조회", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sort_dialog="dialogsort";
                str_sort_date="total";
                str_sort="long";
                dia_dateInit="전체";
                getFirebaseIncargoDatabase();
            }
        });
        dialog.setNeutralButton("조건별 화물 조회", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               str_sort_date="total";
                sort_dialog="init";
                str_sort="long";
                getFirebaseIncargoDatabase();
            }
        });
        dialog.show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
   @Override
    public boolean onOptionsItemSelected(MenuItem item){
       sharedPref=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
       editor= sharedPref.edit();
        switch(item.getItemId()){
            case R.id.action_account:
                ArrayList<String> depotSort=new ArrayList<String>();
                depotSort.add("1물류(02010810)");
                depotSort.add("2물류(02010027)");
                depotSort.add("(주)화인통상 창고사업부");

                ArrayList selectedItems=new ArrayList();
                int defaultItem=0;
                selectedItems.add(defaultItem);

                String[] depotSortList=depotSort.toArray(new String[depotSort.size()]);
                AlertDialog.Builder sortBuilder=new AlertDialog.Builder(fine.koaca.wms.Incargo.this);
                View view=getLayoutInflater().inflate(R.layout.user_reg,null);
                EditText reg_edit=view.findViewById(R.id.user_reg_Edit);

                Button reg_button=view.findViewById(R.id.user_reg_button);
                TextView reg_depot=view.findViewById(R.id.user_reg_depot);

                reg_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       nickName=reg_edit.getText().toString();
                       reg_depot.setText(depotName+"_"+nickName+"으로 사용자 등록을"+"\n"+" 진행할려면 하단 confirm 버튼 클릭 바랍니다.");

                    }
                });

                sortBuilder.setView(view);
                sortBuilder.setSingleChoiceItems(depotSortList,defaultItem,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        depotName=depotSortList[which];
                        Log.i("depo1",depotName);
                        reg_depot.setText("부서명_"+depotName+"로 확인");

                    }
                });
                sortBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("depotName",depotName);
                        editor.putString("nickName",nickName);
                        Log.i("depo2",depotName);
                        editor.apply();
                        Toast.makeText(Incargo.this, depotName+"__"+nickName+"로 사용자 등록 성공 하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Incargo.this,Incargo.class);
                        startActivity(intent);
                    }
                });
                sortBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                sortBuilder.show();
                break;

            case R.id.action_account_search:
                searchSort();
            case R.id.action_account_down:
                downLoadingMark="DownLoadingOk";
                String a="b";
                DatePickerFragment datePickerFragment=new DatePickerFragment(a);
                datePickerFragment.show(getSupportFragmentManager(),"datePicker");
        }
        return true;

    }

    public void searchFirebaseDatabaseToArray(String sortContents){
        ValueEventListener listener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                for(DataSnapshot searchsnapshot:snapshot.getChildren()){
                    Fine2IncargoList data=searchsnapshot.getValue(Fine2IncargoList.class);
                    int containerNameLength=data.getContainer().length();
                    int blNameLength=data.getBl().length();

                  switch(searchContents){
                      case "container":
                          if(containerNameLength==11){
                              String sort_contentsName=data.getContainer().substring(data.getContainer().length()-4,
                                      data.getContainer().length());
                              if(sortContents.equals(sort_contentsName)){
                                  listItems.add(data);

                              }else{;
                              }
                          }else{}
                          break;
                      case "bl":
                         if(blNameLength>4){
                          String sort_contentsName=data.getBl().substring(data.getBl().length()-4,
                                      data.getBl().length());
                          if(sortContents.equals(sort_contentsName)){
                                  listItems.add(data);
                          }else{
                          }}else{}
                          break;
                  }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);
    }

    public void downLoadDialogMessage(String dataMessage){
        AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
        builder.setTitle("서버 저장사진 다운로드");
        builder.setMessage(dataMessage +"_목록을 지정하였습니다."+"\n"+"하단 세부항목 버튼을 클릭하여 다운로드 진행 합니다.");
        CaptureProcess captureProcess=new CaptureProcess();

        builder.setPositiveButton("입고사진 다운로드", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String downLoadingItems="InCargo";
                captureProcess.downLoadingUri(dataMessage,downLoadingItems);
            }
        });
        builder.setNegativeButton("출고사진 다운로드", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String downLoadingItems="OutCargo";
                captureProcess.downLoadingUri(dataMessage,downLoadingItems);
            }
        });
        builder.setNeutralButton("기타사진 다운로드", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String downLoadingItems="Etc";
                captureProcess.downLoadingUri(dataMessage,downLoadingItems);
            }
        });
        builder.show();
    }

    public void fcmSelected(){
        AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
        builder.setTitle("FCM 선택");
        builder.setPositiveButton("보내기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Incargo.this,FcmSendMessage.class);
                Log.i("koacaiia",intent+"__changed");
                startActivity(intent);

            }
        });
        builder.setNegativeButton("받기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Incargo.this,FcmProject.class);

                startActivity(intent);
            }
        });
        builder.show();
    }



}