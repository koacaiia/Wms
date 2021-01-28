package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Incargo extends AppCompatActivity implements Serializable {
    ArrayList<Fine2IncargoList> listItems=new ArrayList<Fine2IncargoList>();
    ArrayList<Fine2IncargoList> listSortItems=new ArrayList<Fine2IncargoList>();
    ArrayList<Fine2IncargoList> listSortList=new ArrayList<Fine2IncargoList>();
    ArrayList<String> consigneeArrayList=new ArrayList<String>();
    SparseBooleanArray selectedSortItems=new SparseBooleanArray(0);
    ArrayList<ExtractIncargoDataList> arrList=new ArrayList<ExtractIncargoDataList>();
    IncargoListAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    String sort_dialog="dialogsort";
    String sortConsignee="ALL";

    ArrayList<String> arrConsignee = new ArrayList<>();
    String [] consignee_list;
    String [] consignee_list2;

    String dataMessage;
    Button incargo_location;
    Button incargo_mnf;


    TextView incargo_incargo;
    TextView incargo_contents_date;
    TextView incargo_contents_consignee;


    TextView dia_date;

    String day_start;
    String day_end;
    String dia_dateInit="";

    String depotName;
    String nickName;
    String bl="";

    static private final String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    FloatingActionButton fltBtn_Capture;
    FloatingActionButton fltBtn_Search;
    String searchContents;

    String downLoadingMark="";
    Button reg_Button_date;
    EditText reg_edit_bl;
    Button reg_Button_bl;
    EditText reg_edit_container;
    Button reg_Button_container;


    EditText reg_edit_remark;
    Button reg_Button_remark;

    String regDate="";
    String regBl="";
    String regContainer="";
    String regRemark="";


    int versioncode;
    String alertVersion;
    Query sortByData;

    TextView exSortConsignee;
    TextView exSortDate;

    public Incargo(){ }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incargo);
        getVersion();
        getFirebaseDataInit();

        sharedPref=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        if(sharedPref==null){
            depotName="2물류(02010027)";
            nickName="Guest";
        }else{
            depotName=sharedPref.getString("depotName",null);
            nickName=sharedPref.getString("nickName","Fine");
        }
        dataMessage = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        day_start=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                day_end=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
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
        adapter=new IncargoListAdapter(listItems,this);
        recyclerView.setAdapter(adapter);
        adapter.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {

                String toDate=listItems.get(pos).getDate();
                String toConsignee=listItems.get(pos).getConsignee();
                bl=listItems.get(pos).getBl();
                String cont=listItems.get(pos).getContainer();

                if(selectedSortItems.get(pos, false)){
                    selectedSortItems.put(pos,false);
//                    selectedSortItems.delete(pos);
                    listSortItems.remove(listItems.get(pos));
                    Toast.makeText(Incargo.this,toDate+"_"+toConsignee+"_"+bl+"_"+cont+"항목 해제", Toast.LENGTH_SHORT).show();
                }else{
                    selectedSortItems.put(pos,true);
//                    selectedSortItems.delete(pos);
                    listSortItems.add(listItems.get(pos));
                    Toast.makeText(Incargo.this,toDate+"_"+toConsignee+"_"+bl+"_"+cont+"항목 선택", Toast.LENGTH_SHORT).show();
                }
                Log.i("koacaiia","listSortItems array__"+listSortItems.size());
            }
        });
        adapter.setAdaptLongClickListener(new AdapterLongClickListener() {
            @Override
            public void onLongItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {

                AlertDialog.Builder deleteItem=new AlertDialog.Builder(Incargo.this);
                deleteItem.setTitle("항목 제거 진행");
                String deDate=listItems.get(pos).getDate();
                String deConsignee=listItems.get(pos).getConsignee();
                String deBl=listItems.get(pos).getBl();
                String deCont=listItems.get(pos).getContainer();
                String deCount=listItems.get(pos).getCount();
                String deDes=listItems.get(pos).getDescription();
                String msg="반입일: "+deDate+"\n"+"화주명: "+deConsignee+"\n"+"Bl: "+deBl+
                        "\n"+"컨테이너번호: "+deCont+"\n"+"화물 정보 삭제를 진행 합니다.";
                String msgWorking="반입일: "+deDate+"_"+"화주명: "+deConsignee+"_"+"\n"+"Bl: "+deBl+
                        "_컨테이너번호 : "+deCont+"에 대한"+"화물 정보 삭제를 진행.";
                deleteItem.setMessage(msg);
                deleteItem.setPositiveButton("삭제 등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String,Object> childUpdates=new HashMap<>();
                        childUpdates.put(deBl+"_"+deDes+"_"+deCount+"/", null);
                        databaseReference.updateChildren(childUpdates);
                        putMessage(msgWorking,"Etc",nickName);
                        getFirebaseData(dataMessage,dataMessage,"sort", sortConsignee);
                    }
                });
                deleteItem.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                deleteItem.create();
                deleteItem.show();
            }
        });

        incargo_location=findViewById(R.id.incargo_reset);
        incargo_location.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {

//                dataMessage = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
//
//                getFirebaseData(dataMessage,dataMessage,"sort", "ALL");
//                incargo_contents_consignee.setText("전 화물 입고조회");
//                listSortItems.clear();
//                adapter.clearSelectedItem();
                Intent intent=new Intent(Incargo.this,Incargo.class);
                startActivity(intent);



            }
        });

        incargo_location.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dataMessage = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                getFirebaseData(day_start,day_end,"all", sortConsignee);
return true;
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
//                Intent intent=new Intent(Incargo.this,FcmProcess.class);
                Intent intent=new Intent(Incargo.this,FcmProcess.class);
                startActivity(intent);
                return true;
            }
        });
        dia_dateInit=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());


        incargo_contents_date.setText(dia_dateInit);
        incargo_contents_consignee.setText("전 화물 입고현황");


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

              return true;
          }
      });
      fltBtn_Search=findViewById(R.id.incargo_floatBtn_search);
      fltBtn_Search.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            searchSort();
//              putDataReg();

          }
      });
      fltBtn_Search.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
              Intent intent=new Intent(Incargo.this,WorkingMessageData.class);
              intent.putExtra("nickName",nickName);
              startActivity(intent);
              return true;
          }
      });
    }

    private void searchSort() {
        AlertDialog.Builder searchBuilder=new AlertDialog.Builder(Incargo.this);
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


        if(downLoadingMark.equals("DownLoadingOk")) {
            dataMessage = (year_string + "년" + month_string + "월" + day_string + "일");
            downLoadDialogMessage(dataMessage);
        }else if (downLoadingMark.equals("RegData")){

              regDate= (year_string + "-" + month_string + "-" + day_string);
              reg_Button_date.setText("Date:"+regDate+"등록");
              reg_Button_date.setTextColor(Color.RED);
//              reg_Result_date.setText(putdata_date);


        }else if(downLoadingMark.equals("StartDate")) {
            dataMessage=(year_string+"-"+month_string+"-"+day_string);
            day_start=dataMessage;
            day_end=dataMessage;
            exSortDate.setText(dataMessage);
            exSortDate.setTextColor(Color.RED);
        }else if(downLoadingMark.equals("EndDate")) {
            dataMessage=(year_string+"-"+month_string+"-"+day_string);
            day_end=dataMessage;
            exSortDate.setTextColor(Color.RED);
            exSortDate.append("~"+dataMessage);}
        else{
            dataMessage=(year_string+"-"+month_string+"-"+day_string);
        dia_date.setText(dataMessage);
        dia_dateInit=dataMessage;}
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
   @SuppressLint("NonConstantResourceId")
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
                AlertDialog.Builder sortBuilder=new AlertDialog.Builder(Incargo.this);
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
                if(bl.equals("")){
                    Toast.makeText(this, "화물조회 항목비엘 다시한번 확인 바랍니다.", Toast.LENGTH_SHORT).show();
                     }

                webView(bl);
                break;

            case R.id.action_account_down:

                AlertDialog.Builder dataReg=new AlertDialog.Builder(this);
                dataReg.setTitle("화물정보 업데이트");
                View regView=getLayoutInflater().inflate(R.layout.reg_putdata,null);
                dataReg.setView(regView);

                reg_Button_date=regView.findViewById(R.id.reg_Button_date);
                reg_edit_bl=regView.findViewById(R.id.reg_edit_bl);
//                reg_edit_bl.setText(listSortItems.get(0).getBl());
                reg_Button_bl=regView.findViewById(R.id.reg_Button_bl);
                reg_edit_container=regView.findViewById(R.id.reg_edit_container);
//                reg_edit_container.setText(listSortItems.get(0).getContainer());
                reg_Button_container=regView.findViewById(R.id.reg_Button_container);
                reg_edit_remark=regView.findViewById(R.id.reg_edit_remark);
//                reg_edit_remark.setText(listSortItems.get(0).getRemark());
                reg_Button_remark=regView.findViewById(R.id.reg_Button_remark);
                reg_Button_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downLoadingMark="RegData";
                        String a="b";
                DatePickerFragment datePickerFragment=new DatePickerFragment(a);
                datePickerFragment.show(getSupportFragmentManager(),"datePicker");


                    }
                });
                reg_Button_bl.setOnClickListener(v -> {

                    reg_Button_bl.setText("BL:"+regBl+"등록");
                    reg_Button_bl.setTextColor(Color.RED);
                    regBl=reg_edit_bl.getText().toString();
                    reg_edit_bl.setTextColor(Color.RED);
                });

                reg_Button_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reg_Button_container.setText("Con`t:"+regContainer+"등록");
                        reg_Button_container.setTextColor(Color.RED);
                        regContainer=reg_edit_container.getText().toString();
                        reg_edit_container.setTextColor(Color.RED);
                                          }
                });

                reg_Button_remark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reg_Button_remark.setText("Remark:"+regRemark+"등록");
                        reg_Button_remark.setTextColor(Color.RED);
                        regRemark=reg_edit_remark.getText().toString();
                        reg_edit_remark.setTextColor(Color.RED);
                    }
                });
                dataReg.setPositiveButton("자료등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        regData();

                    }
                });
                dataReg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dataReg.setNeutralButton("신규등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Incargo.this,PutDataReg.class);
                        intent.putExtra("consigneeList",consignee_list2);
                        startActivity(intent);

                    }
                });

                dataReg.setMessage("총("+listSortItems.size()+")건의 화물정보 업데이트를"+"\n"+ "하기 내용으로 UpDate 진행 합니다.");
                dataReg.show();

                break;
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



    public void webView(String bl){
        Intent intent=new Intent(Incargo.this,WebList.class);
        intent.putExtra("bl",bl);
        intent.putExtra("version",alertVersion);
        startActivity(intent);
    }

    public void regData(){
        Fine2IncargoList list=new Fine2IncargoList();
        for(int i=0;i<listSortItems.size();i++){
            String chBl;
        if(!regBl.equals("")){
            chBl="(비엘:"+regBl+")";
        list.setBl(regBl);}
        else{
            chBl="";
            list.setBl(listSortItems.get(i).getBl());
        }

        list.setConsignee(listSortItems.get(i).getConsignee());
        String chContainer;
        if(!regContainer.equals("")){
            chContainer="(컨테이너 번호:"+regContainer+")";
        list.setContainer(regContainer);}
        else{
            chContainer="";

            list.setContainer(listSortItems.get(i).getContainer());
            }
        list.setContainer20(listSortItems.get(i).getContainer20());
        list.setContainer40(listSortItems.get(i).getContainer40());
        list.setCount(listSortItems.get(i).getCount());
        String chDate;
        if(!regDate.equals("")){
            chDate="(입고일:"+regDate+")";
        list.setDate(regDate);}
        else{
            chDate="";
            list.setDate(listSortItems.get(i).getDate());}
        list.setDescription(listSortItems.get(i).getDescription());
        list.setIncargo(listSortItems.get(i).getIncargo());
        list.setLclcargo(listSortItems.get(i).getLclcargo());
        list.setLocation(listSortItems.get(i).getLocation());
        String chRemark;
        if(!regRemark.equals("")){
            chRemark="(비고:"+regRemark+")";
        list.setRemark(regRemark);}
        else{
            chRemark="";
            list.setRemark(listSortItems.get(i).getRemark());}
        list.setWorking(listSortItems.get(i).getWorking());


            FirebaseDatabase database=FirebaseDatabase.getInstance();
            DatabaseReference databaseReference=
                    database.getReference("Incargo"+"/"+listSortItems.get(i).getBl()+"_"+listSortItems.get(i).getDescription()+
                            "_"+listSortItems.get(i).getCount());

        databaseReference.setValue(list);
        String msg=
                "("+listSortItems.get(i).getDate()+")_"+listSortItems.get(i).getConsignee()+"_"+"비엘: "+listSortItems.get(i).getBl()+
            "를";
        String msg1=chDate+chBl+chContainer+chRemark+"로 변경 진행 합니다.";
        putMessage(msg+"\n"+msg1,"Etc",nickName);
//        putMessage(msg1,"Etc");
        }
        sort_dialog="dialogsort";
        getFirebaseData(dataMessage,dataMessage,"sort", sortConsignee);

    }

    public void getVersion(){
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Version");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot version:snapshot.getChildren()){
                    VersionCheck data=version.getValue(VersionCheck.class);
                    int versionCheck=data.getVersionChecked();
                    try {
                        PackageInfo pi=getPackageManager().getPackageInfo(getPackageName(),0);
                    versioncode=pi.versionCode;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(versionCheck!=versioncode){
                        alertVersion="현재 버전:"+versioncode+"으로 "+""+"최신버전:"+versionCheck+" 로 업데이트 바랍니다.!";
                        webView("version");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void putDataReg(){
        Intent intent=new Intent(Incargo.this,PutDataReg.class);
        intent.putExtra("list", listItems);
        startActivity(intent);
    }
    public void putMessage(String msg, String etc,String nick) {

        String timeStamp=String.valueOf(System.currentTimeMillis());
        String timeStamp1=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String date=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());

        WorkingMessageList messageList=new WorkingMessageList();
        messageList.setNickName(nick);
        messageList.setTime(timeStamp1);
        messageList.setMsg(msg);
        messageList.setDate(date);
        messageList.setConsignee(etc);
        messageList.setInOutCargo("Etc");
        messageList.setUri("");
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference("WorkingMessage"+"/"+nick+"_"+date+"_"+timeStamp);
        databaseReference.setValue(messageList);

    }
    public void sortDialog(String startDay, String endDay, ArrayList<Fine2IncargoList> listSortList){
        arrList.clear();
        int listItemsSize;
        listItemsSize=listSortList.size();

        int sum40=0;
        int sum20=0;
        int sumCargo=0;
        int sumQty=0;

        int to40 = 0;
        int to20=0;
        int toCargo=0;
        int toQty=0;

//
        for(int i=0;i<listItemsSize;i++){
            String consignee=listSortList.get(i).getConsignee();
            int int40=Integer.parseInt(listSortList.get(i).getContainer40());
            int int20=Integer.parseInt(listSortList.get(i).getContainer20());
            int intCargo=Integer.parseInt(listSortList.get(i).getLclcargo());
            int intQty=Integer.parseInt(listSortList.get(i).getIncargo());

            sum40=sum40+int40;
            sum20=sum20+int20;
            sumCargo=sumCargo+intCargo;
            sumQty=sumQty+intQty;

            to40=to40+int40;
            to20=to20+int20;
            toCargo=toCargo+intCargo;
            toQty=toQty+intQty;
            if(i==listItemsSize-1){
                ExtractIncargoDataList list;
                if(consignee.equals(listSortList.get(i-1).getConsignee())){
                    list = new ExtractIncargoDataList(consignee, String.valueOf(sum40), String.valueOf(sum20),
                            String.valueOf(sumCargo), String.valueOf(sumQty));

                }else{
                    list = new ExtractIncargoDataList(consignee, String.valueOf(int40), String.valueOf(int20),
                            String.valueOf(intCargo), String.valueOf(intQty));

                }
                arrList.add(list);
                sum40=0;
                sum20=0;
                sumCargo=0;
                sumQty=0;
            }else if(!consignee.equals(listSortList.get(i+1).getConsignee())){
                ExtractIncargoDataList list=new ExtractIncargoDataList(consignee,String.valueOf(sum40),String.valueOf(sum20),
                        String.valueOf(sumCargo),String.valueOf(sumQty));

                arrList.add(list);
                sum40=0;
                sum20=0;
                sumCargo=0;
                sumQty=0;
            }
        }
        AlertDialog.Builder arrReBuilder=new AlertDialog.Builder(this);

        arrReBuilder.setTitle("입고화물 정보");
        View view=getLayoutInflater().inflate(R.layout.arr_re,null);
        TextView textViewDate=view.findViewById(R.id.exDate);
        if(startDay.equals(endDay)){
            textViewDate.setText(startDay);
            textViewDate.setTextColor(Color.RED);
            incargo_contents_date.setText(startDay);
        }else{
            textViewDate.setText(startDay+"\n"+"~"+endDay);
            incargo_contents_date.setText(startDay+"\n"+"~"+endDay);
            incargo_contents_date.setTextSize(14);
            textViewDate.setTextSize(9);
            textViewDate.setTextColor(Color.RED);
        }

        TextView textViewContainer40=view.findViewById(R.id.exContainer40);
        textViewContainer40.setText("40FT:"+"\n"+to40+" 대");
        TextView textViewContainer20=view.findViewById(R.id.exContainer20);
        textViewContainer20.setText("20FT:"+"\n"+to20+" 대");
        TextView textViewCargo=view.findViewById(R.id.exCargo);
        textViewCargo.setText("Cargo:"+"\n"+toCargo+" 건");
        TextView textViewQty=view.findViewById(R.id.exQty);
        textViewQty.setText("팔렛트 수량 :"+"\n"+toQty+" PLT");

        TextView textViewConsignee=view.findViewById(R.id.exSortConsignee);

        arrConsignee.add(0,"ALL");
        consignee_list=arrConsignee.toArray(new String[arrConsignee.size()]);
        arrConsignee.clear();
        for(String item:consignee_list){
            if(!arrConsignee.contains(item)){
                arrConsignee.add(item);
            }
        }
        consignee_list2=arrConsignee.toArray(new String[arrConsignee.size()]);

        Spinner spinnerConsignee=view.findViewById(R.id.exSpinnerConsignee);
        ArrayAdapter<String> consigneeListAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                consignee_list2);
        spinnerConsignee.setAdapter(consigneeListAdapter);
        spinnerConsignee.setSelection(0,false);
        spinnerConsignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sortConsignee=consignee_list2[position];
                textViewConsignee.setText(sortConsignee+":화주검색");
                textViewConsignee.setTextColor(Color.RED);
                incargo_contents_consignee.setText(sortConsignee);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortConsignee=consignee_list2[0];

            }
        });

        Button btnStartDay=view.findViewById(R.id.exBtnStartDay);
        btnStartDay.setOnClickListener(v->{
            downLoadingMark="StartDate";
            DatePickerFragment datePickerFragment=new DatePickerFragment("b");
            datePickerFragment.show(getSupportFragmentManager(),"datePicker");
        });

        Button btnEndDay=view.findViewById(R.id.exBtnEndDay);
        btnEndDay.setOnClickListener(v->{
            downLoadingMark="EndDate";
            DatePickerFragment datePickerFragment=new DatePickerFragment("b");
            datePickerFragment.show(getSupportFragmentManager(),"datePicker");
        });
        exSortDate=view.findViewById(R.id.exSortDate);

        exSortConsignee=view.findViewById(R.id.exSortConsignee);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        RecyclerView recyclerEx=view.findViewById(R.id.reEx);
        recyclerEx.setLayoutManager(layoutManager);
        ExtractIncargoDataAdapter adapter=new ExtractIncargoDataAdapter(arrList);
        recyclerEx.setAdapter(adapter);
        arrReBuilder.setView(view);
        arrReBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        arrReBuilder.setNegativeButton("조회진행", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFirebaseData(day_start,day_end, "sort",sortConsignee);

            }
        });
//        arrReBuilder.show();
        AlertDialog dialog=arrReBuilder.create();
        dialog.show();
        WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
        params.width=WindowManager.LayoutParams.MATCH_PARENT;
                params.height=1600;
                        dialog.getWindow().setAttributes(params);
    }

    public void getFirebaseData(String startDay, String endDay, String sortItems, String sortConsignee){


        ValueEventListener dataListener=new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                listSortList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Fine2IncargoList data = dataSnapshot.getValue(Fine2IncargoList.class);
                    String forty = data.getContainer40();
                    String twenty = data.getContainer20();
                    String cargo = data.getLclcargo();
                    if (sortItems.equals("all")) {
                        if (sortConsignee.equals("ALL")) {
                            listItems.add(data);
                            listSortList.add(data);
                        } else {
                            if (sortConsignee.equals(data.getConsignee())) {
                                listItems.add(data);
                                listSortList.add(data);
                            }
                        }

                    } else if (sortItems.equals("sort")) {
                        final boolean b = !forty.equals("0") || !twenty.equals("0") || !cargo.equals("0");
                        if (sortConsignee.equals("ALL")) {
                            if (b) {
                                listItems.add(data);
                                listSortList.add(data);
                            }
                        } else {
                                if (b) {
                                    if (sortConsignee.equals(data.getConsignee())) {
                                    listItems.add(data);
                                        listSortList.add(data);
                                }
                            }
                        }
                    }

                }
                Collections.reverse(listItems);
                adapter.notifyDataSetChanged();
                sortDialog(startDay, endDay, listSortList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        sortByData=databaseReference.orderByChild("date").startAt(startDay).endAt(endDay);
        sortByData.addListenerForSingleValueEvent(dataListener);

    }

    public void getFirebaseDataInit(){
        databaseReference=database.getReference("Incargo");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Fine2IncargoList data = dataSnapshot.getValue(Fine2IncargoList.class);
                    consigneeArrayList.add(data.getConsignee());

                }
               arrConsignee=consigneeArrayList;
                getFirebaseData(dataMessage,dataMessage,"sort", sortConsignee);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}




