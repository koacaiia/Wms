package fine.koaca.wms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

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



    static RequestQueue requestQueue;
    String [] permission_list={
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.USE_FULL_SCREEN_INTENT,
            Manifest.permission.ANSWER_PHONE_CALLS,
    };

    Vibrator vibrator;
    String[] upDataRegList;

    String wareHouseDepot="Incargo";
    String alertDepot="Depot";

    String dateSelectCondition="";

    SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incargo);
        requestPermissions(permission_list,0);


        Intent intent=getIntent();

        vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

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
                databaseReference=database.getReference("Incargo2");
                wareHouseDepot="Incargo2";
                alertDepot="Depot2";
                break;
            case "1물류(02010810)":
                databaseReference=database.getReference("Incargo1");
                wareHouseDepot="Incargo1";
                alertDepot="Depot1";

                break;
            case "(주)화인통상 창고사업부":
                databaseReference=database.getReference("Incargo");
                wareHouseDepot="Incargo";
                alertDepot="Depot";
                break;
        }}else{
            Toast.makeText(this, "사용자등록 바랍니다.", Toast.LENGTH_SHORT).show();
            databaseReference=database.getReference("Incargo");
            alertDepot="Depot";
        }

        FirebaseMessaging.getInstance().subscribeToTopic(alertDepot);

        getVersion();
        getFirebaseDataInit();
        String alertTimeStamp=new SimpleDateFormat("HH시mm분").format(new Date());
        adapter=new IncargoListAdapter(listItems,this);
        recyclerView.setAdapter(adapter);
        adapter.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {

                bl=listItems.get(pos).getBl();
                String incargoWorking=listItems.get(pos).getWorking();
                String incargoDate=listItems.get(pos).getDate();
                String incargoConsigneeName=listItems.get(pos).getConsignee();
                String incargoDescription=listItems.get(pos).getDescription();
                String incargoContainerNumber=listItems.get(pos).getContainer();
                String incargoQty=listItems.get(pos).getIncargo();
                String incargoBl=listItems.get(pos).getBl();
                String incargoRemark=listItems.get(pos).getRemark();
                String incargoContainer20=String.valueOf(listItems.get(pos).getContainer20());
                String incargoContainer40=String.valueOf(listItems.get(pos).getContainer40());
                String incargoCargo=String.valueOf(listItems.get(pos).getLclcargo());


                upDataRegList= new String[]{incargoWorking,incargoDate,incargoConsigneeName,incargoDescription,
                        incargoContainer20,incargoContainer40,incargoCargo,
                        incargoContainerNumber,incargoQty,incargoBl,incargoRemark};

                if(selectedSortItems.get(pos, false)){
                    selectedSortItems.put(pos,false);
//                    selectedSortItems.delete(pos);
                    listSortItems.remove(listItems.get(pos));
                    Toast.makeText(Incargo.this,incargoDate+"_"+incargoConsigneeName+"_"+bl+"_"+incargoContainerNumber+"항목 해제",
                            Toast.LENGTH_SHORT).show();
                }else{
                    selectedSortItems.put(pos,true);
//                    selectedSortItems.delete(pos);
                    listSortItems.add(listItems.get(pos));
                    Toast.makeText(Incargo.this,incargoDate+"_"+incargoConsigneeName+"_"+bl+"_"+incargoContainerNumber+"항목 선택",
                            Toast.LENGTH_SHORT).show();
                }

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
                        childUpdates.put(deDate+"_"+deBl+"_"+deDes+"_"+deCount+"_"+deCont+"/", null);
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
                Intent intent=new Intent(Incargo.this,MainActivitySub.class);
                startActivity(intent);
            }
        });

        incargo_mnf.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Intent intent=new Intent(Incargo.this,FcmProcess.class);
//               sortConsigneeListEx();
//                detailConditionSearch();

                return true;
            }
        });
        dia_dateInit=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());


        incargo_contents_date.setText(dia_dateInit);
        incargo_contents_consignee.setText(depotName+" 전 화물 입고현황");


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
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }

      Button btnAlert=findViewById(R.id.incargo_alert);
      btnAlert.setOnClickListener(v->{

          String emergencyMessage=alertTimeStamp+" 에 업무지원 요청 합니다.!!!";
          sendAlertMessage(emergencyMessage);

      });
      btnAlert.setOnLongClickListener(v->{

          return false;

      });
    }

    private void sendAlertMessage(String message) {
        JSONObject requestData=new JSONObject();
        try{
            requestData.put("priority","high");
            JSONObject dataObj=new JSONObject();
            dataObj.put("contents",message);
            dataObj.put("nickName",nickName);

            requestData.put("data",dataObj);
            requestData.put("to","/topics/"+alertDepot);

        }catch (Exception e){
            e.printStackTrace();
        }
        sendData(requestData,new SendResponseListener(){
            @Override
            public void onRequestStarted() {
                Toast.makeText(getApplicationContext(),"지원 알림 요청 성공 하였습니다.",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRequestCompleted() {
            }

            @Override
            public void onRequestWithError(VolleyError error) {

            }
        });
    }


    private void sendData(JSONObject requestData, SendResponseListener sendResponseListener) {
        JsonObjectRequest request=new JsonObjectRequest(
        Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        sendResponseListener.onRequestCompleted();
                    }},
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendResponseListener.onRequestWithError(error);
                    }
                })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
            Map<String,String> params=new HashMap<String,String>();
            return params;
        }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
            Map<String,String> headers=new HashMap<String,String>();
                headers.put("Authorization","key=AAAAKv8kPlM:APA91bF8Hq-XBpxF9a0z7pDBVRBabqUZt3uela3d6m5r9iWXzIzCJJcCplCcWRksa47jYXGGL5LMSBTMXVWzVhU4JzThvsExOQ2VKRt1H7rzoOg6yL2CKH4KNlIbV1oCC8zzJ1DHxW10");
            return headers;
        }
            @Override
            public String getBodyContentType(){
            return "application/json";
        }

        };

        request.setShouldCache(false);
        sendResponseListener.onRequestStarted();
        requestQueue.add(request);
    }

    public interface SendResponseListener{
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);

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

        }else if(downLoadingMark.equals("EndDate")) {
            dataMessage=(year_string+"-"+month_string+"-"+day_string);
            day_end=dataMessage;
           }
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
                if(listSortItems.size()==0){
                    Intent intent=new Intent(Incargo.this,PutDataReg.class);
                    intent.putExtra("dataRef",wareHouseDepot);
                    intent.putExtra("list",upDataRegList);
                    intent.putExtra("consigneeList",consignee_list2);
                    startActivity(intent);
                }
                reg_Button_date.setText(listSortItems.get(0).getDate());
                reg_edit_bl=regView.findViewById(R.id.reg_edit_bl);
                reg_edit_bl.setText(listSortItems.get(0).getBl());
                reg_Button_bl=regView.findViewById(R.id.reg_Button_bl);
                reg_edit_container=regView.findViewById(R.id.reg_edit_container);
                reg_edit_container.setText(listSortItems.get(0).getContainer());
                reg_Button_container=regView.findViewById(R.id.reg_Button_container);
                reg_edit_remark=regView.findViewById(R.id.reg_edit_remark);
                reg_edit_remark.setText(listSortItems.get(0).getRemark());
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
                dataReg.setPositiveButton("요약 자료등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        regData();

                    }
                });
                dataReg.setNegativeButton("세부자료 등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Incargo.this,PutDataReg.class);
                        intent.putExtra("dataRef",wareHouseDepot);
                        intent.putExtra("list",upDataRegList);
                        intent.putExtra("consigneeList",consignee_list2);
                        startActivity(intent);


                    }
                });

                dataReg.setNeutralButton("신규등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Incargo.this,PutDataReg.class);
                        intent.putExtra("dataRef",wareHouseDepot);
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

        String dateO,blO,desO,countO,contNO;
        for(int i=0;i<listSortItems.size();i++){
            dateO=listSortItems.get(i).getDate();
            blO=listSortItems.get(i).getBl();
            desO=listSortItems.get(i).getDescription();
            countO=listSortItems.get(i).getCount();
            contNO=listSortItems.get(i).getContainer();
            Map<String,Object> childUpdates=new HashMap<>();
            childUpdates.put(dateO+"_"+blO+"_"+desO+"_"+countO+"_"+contNO+"/", null);
            databaseReference.updateChildren(childUpdates);
            String chBl;
        if(!regBl.equals("")){
            chBl="(비엘:"+regBl+")";
        list.setBl(regBl);}
        else{
            chBl="";
            list.setBl(listSortItems.get(i).getBl());
            regBl=listSortItems.get(i).getBl();
        }

        list.setConsignee(listSortItems.get(i).getConsignee());
        String chContainer;
        if(!regContainer.equals("")){
            chContainer="(컨테이너 번호:"+regContainer+")";
        list.setContainer(regContainer);}
        else{
            chContainer="";
            list.setContainer(listSortItems.get(i).getContainer());
            regContainer=listSortItems.get(i).getContainer();
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
            list.setDate(listSortItems.get(i).getDate());
            regDate=listSortItems.get(i).getDate();
        }
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
                    database.getReference(wareHouseDepot+"/"+regDate+"_"+regBl+"_"+listSortItems.get(i).getDescription()+
                            "_"+listSortItems.get(i).getCount()+"_"+regContainer);
            DatabaseReference databaseRef=
                    database.getReference("Incargo"+"/"+listSortItems.get(i).getDate()+"_"+listSortItems.get(i).getBl()+"_"+listSortItems.get(i).getDescription()+
                            "_"+listSortItems.get(i).getCount()+"_"+listSortItems.get(i).getContainer());


        databaseReference.setValue(list);
        databaseRef.setValue(list);
        String msg=
                "("+listSortItems.get(i).getDate()+")_"+listSortItems.get(i).getConsignee()+"_"+"비엘: "+listSortItems.get(i).getBl()+
            "를";
        String msg1=chDate+chBl+chContainer+chRemark+"로 변경 진행 합니다.";
        putMessage(msg+"\n"+msg1,"Etc",nickName);
//        putMessage(msg1,"Etc");
        }
        sort_dialog="dialogsort";
        getFirebaseData(day_start,day_end,"sort", sortConsignee);

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

        arrConsignee.clear();
        int listSize=listSortList.size();
        String consigneeName;
        for(int i=0;i<listSize;i++){
            consigneeName=listSortList.get(i).getConsignee();
            if(!arrConsignee.contains(consigneeName)){
                arrConsignee.add(consigneeName);
            }
        }
        String consigneeName1,getConsigneeName;
        int cont40=0;
        int cont20=0;
        int cargo=0;
        int qty=0;
        for(int i=0;i<arrConsignee.size();i++){
            consigneeName1=arrConsignee.get(i);
            for(int j=0;j<listSortList.size();j++){
                getConsigneeName=listSortList.get(j).getConsignee();
                if(consigneeName1.equals(getConsigneeName)){
                    cont40=cont40+Integer.parseInt(listSortList.get(j).getContainer40());
                    cont20=cont20+Integer.parseInt(listSortList.get(j).getContainer20());
                    cargo=cargo+Integer.parseInt(listSortList.get(j).getLclcargo());
                    qty=qty+Integer.parseInt(listSortList.get(j).getIncargo());
                }
            }
            ExtractIncargoDataList list=new ExtractIncargoDataList(consigneeName1,String.valueOf(cont40),String.valueOf(cont20),
                    String.valueOf(cargo),String.valueOf(qty));
            arrList.add(list);
            cont40=0;
            cont20=0;
            cargo=0;
            qty=0;
            }
//        adapter.notifyDataSetChanged();
        AlertDialog.Builder arrReBuilder=new AlertDialog.Builder(this);

        String cargoDate;
        if(startDay.equals(endDay)){
            cargoDate=startDay;

        }else{
            cargoDate=startDay+"~"+endDay;

        }

        arrReBuilder.setTitle(cargoDate+" 입고화물 정보");
        View view=getLayoutInflater().inflate(R.layout.date_select_dialog,null);
        for(int i=0;i<arrList.size();i++){
            cont40=cont40+Integer.parseInt(arrList.get(i).getContainer40());
            cont20=cont20+Integer.parseInt(arrList.get(i).getContainer20());
            cargo=cargo+Integer.parseInt(arrList.get(i).getLcLCargo());
            qty=qty+Integer.parseInt(arrList.get(i).getQty());
        }

        TextView textViewContainer40=view.findViewById(R.id.exContainer40);
        textViewContainer40.setText(cont40+" 대");
        TextView textViewContainer20=view.findViewById(R.id.exContainer20);
        textViewContainer20.setText(cont20+" 대");
        TextView textViewCargo=view.findViewById(R.id.exCargo);
        textViewCargo.setText(cargo+" 건");
        TextView textViewQty=view.findViewById(R.id.exQty);
        textViewQty.setText(qty+" PLT");



        consigneeArrayList.add(0,"ALL");
        consignee_list=consigneeArrayList.toArray(new String[consigneeArrayList.size()]);
        consigneeArrayList.clear();
        for(String item:consignee_list){
            if(!consigneeArrayList.contains(item)){
                consigneeArrayList.add(item);
            }
        }
        consignee_list2=consigneeArrayList.toArray(new String[consigneeArrayList.size()]);


        Button searchDetail=view.findViewById(R.id.btnDetailSearch);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        RecyclerView recyclerEx=view.findViewById(R.id.resortEx);
        recyclerEx.setLayoutManager(layoutManager);
        ExtractIncargoDataAdapter adapter=new ExtractIncargoDataAdapter(arrList);
        recyclerEx.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        arrReBuilder.setView(view);
//        arrReBuilder.show();
        AlertDialog dialog=arrReBuilder.create();
        dialog.show();
        WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
        params.width=WindowManager.LayoutParams.MATCH_PARENT;
        params.height=WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        searchDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortCargoDataDialog();
                dialog.dismiss();
            }
        });
        searchDetail.setOnLongClickListener(v->{

          return true;
        });
        Button btnThisMonth=view.findViewById(R.id.btnThisMonth);
        btnThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirebaseDataSortDate("ThisMonth");
                dialog.dismiss();
            }
        });
        Button btnNextWeek=view.findViewById(R.id.btnNextWeek);
        btnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirebaseDataSortDate("NextWeek");
                dialog.dismiss();
            }
        });
        Button btnThisWeek=view.findViewById(R.id.btnThisWeek);
        btnThisWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirebaseDataSortDate("ThisWeek");
                dialog.dismiss();
            }
        });

        Button btnTomorrow=view.findViewById(R.id.btnTomorrow);
        btnTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirebaseDataSortDate("Tomorrow");
                dialog.dismiss();
            }
        });

    }

    private void sortCargoDataDialog() {
        View view=getLayoutInflater().inflate(R.layout.update_datepicker_spinner,null);
        DatePicker datePickerDefault=view.findViewById(R.id.udatepicker_default);
        Button btnStart=view.findViewById(R.id.uBtnSearchDate_start);
        Button btnEnd=view.findViewById(R.id.uBtnSearchDate_end);
        Calendar calendar=Calendar.getInstance();
        Button btnSearch=view.findViewById(R.id.ubtnSearchDate);
        TextView txtUntil=view.findViewById(R.id.txtUntil);


        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        String sbMonth,sbDay;

        if((month+1)<10){
            sbMonth="0"+(month+1);
        }else{
            sbMonth=String.valueOf(month+1);
        }

        if(day<10){
            sbDay="0"+day;
        }else{
            sbDay=String.valueOf(day);
        }
        String dateBasic=year+"-"+sbMonth+"-"+sbDay;
        btnStart.setText(dateBasic);
        btnEnd.setText(dateBasic);

        final String[] pickDate=new String[1];
        datePickerDefault.init(year,month,day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String bmonth,bday;

                if(monthOfYear<9){
                    bmonth="0"+(monthOfYear+1);
                }else{
                    bmonth=String.valueOf(monthOfYear+1);
                }
                if(dayOfMonth<10){
                    bday="0"+dayOfMonth;
                }else{
                    bday=String.valueOf(dayOfMonth);
                }
                pickDate[0]=year+"-"+bmonth+"-"+bday;
                if(!dateSelectCondition.equals("until")){
                btnStart.setText(pickDate[0]);
                btnEnd.setText(pickDate[0]);}
            }
        });

        datePickerDefault.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mSelectedItems.get(0, false)){
                    dateSelectCondition="date";
                    mSelectedItems.put(0,false);
                    Toast.makeText(getApplicationContext(),"기간설정",Toast.LENGTH_SHORT).show();
                }else{
                    dateSelectCondition="until";
                    mSelectedItems.put(0,true);
                    Toast.makeText(getApplicationContext(),"시작일~ 종료일 설정",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        txtUntil.setOnClickListener(v->{
            if(mSelectedItems.get(0,false)){
                dateSelectCondition="date";
                mSelectedItems.put(0,false);
                txtUntil.setText("=");
            }else{
                dateSelectCondition="until";
                mSelectedItems.put(0,true);
                txtUntil.setText("~");
            }

        });

        btnStart.setOnClickListener(v->{

            btnStart.setText(pickDate[0]);
            Toast.makeText(this,"검색 시작일 을 "+pickDate[0]+" 일로 설정",Toast.LENGTH_SHORT).show();

        });
        btnStart.setOnLongClickListener(v->{
            btnStart.setText(dateBasic);
            Toast.makeText(this,"검색 시작일 을 당일("+dateBasic+" )일로 설정",Toast.LENGTH_SHORT).show();
            return true;
        });
        btnEnd.setOnClickListener(v->{
            btnEnd.setText(pickDate[0]);
            Toast.makeText(this,"검색 종료일 을 "+pickDate[0]+" 일로 설정",Toast.LENGTH_SHORT).show();
        });
        btnEnd.setOnLongClickListener(v->{
           btnEnd.setText(dateBasic);
            Toast.makeText(this,"검색 종료일 을 당일("+dateBasic+" )일로 설정",Toast.LENGTH_SHORT).show();
            return true;
        });


        final String[] spinnerconsignee = {"All"};
        Spinner consigneeSpinner=view.findViewById(R.id.spinner_consigneelist);
        ArrayAdapter<String> consigneeListAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                consignee_list2);
        consigneeSpinner.setAdapter(consigneeListAdapter);
        consigneeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerconsignee[0] =consignee_list2[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder sortDialog=new AlertDialog.Builder(this);
        sortDialog.setView(view);
//                .show();
        AlertDialog dialog=sortDialog.create();
        dialog.show();;
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day_start=btnStart.getText().toString();
                day_end=btnEnd.getText().toString();
                getFirebaseData(day_start,day_end,"sort", spinnerconsignee[0]);
                dialog.dismiss();
            }
        });

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
//                sortDialogOld(startDay, endDay, listSortList);
                String date;
                if(startDay.equals(endDay)){
                    date=startDay;
                }else{
                    date=startDay+"~"+endDay;
                }
                incargo_contents_date.setText(date);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        sortByData=databaseReference.orderByChild("date").startAt(startDay).endAt(endDay);
        sortByData.addListenerForSingleValueEvent(dataListener);

    }
        public void getFirebaseDataInit(){
        databaseReference=database.getReference(wareHouseDepot);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Fine2IncargoList data = dataSnapshot.getValue(Fine2IncargoList.class);
                    consigneeArrayList.add(data.getConsignee());
                    arrConsignee.add(data.getConsignee());
                }

                getFirebaseData(dataMessage,dataMessage,"sort", sortConsignee);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int result:grantResults){
            if(result== PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "permission denied"+permissions[requestCode], Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    public void getFirebaseDataSortDate(String sortDateItem){
        Calendar calendar=Calendar.getInstance();
        String format= "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        String sbMonth,sbDay;
        if((month+1)<10){
            sbMonth="0"+(month+1);
        }else{
            sbMonth=String.valueOf(month+1);
        }
        if(day<10){
            sbDay="0"+day;
        }else{
            sbDay=String.valueOf(day);
        }
        String dateBasic=year+"-"+sbMonth+"-"+sbDay;

        switch(sortDateItem){
            case "ThisMonth":
                day_start=year+"-"+sbMonth+"-"+"01";
                String maxDay=String.valueOf(calendar.getMaximum(Calendar.DAY_OF_MONTH));
                day_end=year+"-"+sbMonth+"-"+maxDay;
                break;
            case "NextWeek":
                calendar.add(Calendar.DATE,7);
                calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                day_start=simpleDateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE,5);
                day_end=simpleDateFormat.format(calendar.getTime());
                    break;
            case "ThisWeek":
                calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                day_start=simpleDateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE,5);
                day_end=simpleDateFormat.format(calendar.getTime());
                break;
            case "Tomorrow":
                calendar.add(Calendar.DATE,1);
                day_start=simpleDateFormat.format(calendar.getTime());
                day_end=simpleDateFormat.format(calendar.getTime());
                break;
        }

        getFirebaseData(day_start,day_end,"sort","ALL");

        Toast.makeText(this,day_start+"일부터"+day_end+"까지 검색을 시작 합니다.",Toast.LENGTH_SHORT).show();


    }

    private void dateSelectedDialog(String date){
        View view=getLayoutInflater().inflate(R.layout.date_select_click_event,null);
        AlertDialog.Builder clickBuilder=new AlertDialog.Builder(this);
        clickBuilder.setView(view)
                .show();
    }



}




