package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Incargo extends AppCompatActivity implements Serializable , SensorEventListener,
        IncargoListAdapter.AdapterClickListener,IncargoListAdapter.AdapterLongClickListener,
        ImageViewActivityAdapter.ImageViewClicked, Comparator<Fine2IncargoList> {
    ArrayList<Fine2IncargoList> listItems = new ArrayList<Fine2IncargoList>();
    ArrayList<Fine2IncargoList> listSortItems = new ArrayList<Fine2IncargoList>();
    ArrayList<Fine2IncargoList> listSortList = new ArrayList<Fine2IncargoList>();
    ArrayList<String> consigneeArrayList = new ArrayList<String>();
    SparseBooleanArray selectedSortItems = new SparseBooleanArray(0);
    ArrayList<ExtractIncargoDataList> arrList = new ArrayList<ExtractIncargoDataList>();
    IncargoListAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    String sort_dialog = "dialogsort";
    String sortConsignee = "ALL";

    ArrayList<String> arrConsignee = new ArrayList<>();
    String[] consignee_list;
    static String[] shared_consigneeList;


    Button incargo_reset;
    Button incargo_mnf;


    TextView incargo_incargo;
    TextView incargo_contents_date;
    TextView incargo_contents_consignee;


    String day_start;
    String day_end;
    String dateToday;

    String deptName;
    String nickName;
    String bl = "";

    Button fltBtn_Capture;
    FloatingActionButton fltBtn_share;
    String searchContents;

    String downLoadingMark = "";
    Button reg_Button_date;
    EditText reg_edit_bl;
    Button reg_Button_bl;
    EditText reg_edit_container;
    Button reg_Button_container;


    EditText reg_edit_remark;
    Button reg_Button_remark;

    String regDate = "";
    String regBl = "";
    String regContainer = "";
    String regRemark = "";

    Query sortByData;

    String[] upDataRegList;


    String dateSelectCondition = "";

    SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    private long mShakeTime;
    private static final int SHAKE_SKIP_TIME = 500;
    private static final float SHAKE_THERESHOLD_GRAVITY = 2.7F;

    ArrayList<String> imageViewLists = new ArrayList<>();
    ArrayList<String> imageViewListsSelected = new ArrayList<>();
    ImageViewActivityAdapter iAdapter;

    static RequestQueue requestQueue;
    String keyValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incargo);


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        PublicMethod publicMethod=new PublicMethod(this);
        deptName=publicMethod.getUserInformation().get("deptName");
        nickName=publicMethod.getUserInformation().get("nickName");

//        dateToday= new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        dateToday="2021-07-29";

        incargo_incargo = findViewById(R.id.incargo_incargo);
        incargo_contents_date = findViewById(R.id.incargo_contents_date);
        incargo_contents_consignee = findViewById(R.id.incargo_contents_consignee);
        incargo_mnf = findViewById(R.id.incargo_mnf);
        recyclerView = findViewById(R.id.incargo_recyclerViewList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        listItems = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        databaseReference=
                database.getReference("DeptName/" + deptName + "/" +"InCargo" + "/" +dateToday.substring(5,7) + "월/" + dateToday);
//        sharedPref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
//        depotName = sharedPref.getString("depotName", null);
//        if (depotName != null) {
//            switch (depotName) {
//                case "2물류(02010027)":
//                    databaseReference = database.getReference("Incargo2");
//                    wareHouseDepot = "Incargo2";
//                    alertDepot = "Depot2";
//                    break;
//                case "1물류(02010810)":
//                    databaseReference = database.getReference("Incargo1");
//                    wareHouseDepot = "Incargo1";
//                    alertDepot = "Depot1";
//
//                    break;
//                case "(주)화인통상 창고사업부":
//                    databaseReference = database.getReference("Incargo");
//                    wareHouseDepot = "Incargo";
//                    alertDepot = "Depot";
//                    break;
//            }
//            nickName = sharedPref.getString("nickName", "Guest");
//        } else {
//            Toast.makeText(this, "사용자등록 바랍니다.", Toast.LENGTH_SHORT).show();
//            databaseReference = database.getReference("Incargo2");
//            alertDepot = "Depot2";
//            nickName = "Guest";
//        }

        getFirebaseDataInit();
        adapter = new IncargoListAdapter(listItems, this, this);
        recyclerView.setAdapter(adapter);

        incargo_reset = findViewById(R.id.incargo_reset);
        incargo_reset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Incargo.this, Incargo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        incargo_reset.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                getFirebaseData(day_start, day_end, "all", sortConsignee);
                return true;
            }
        });
        incargo_mnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSort();
            }
        });

        incargo_mnf.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Incargo.this, MainActivitySub.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                return true;
            }
        });


        incargo_contents_date.setText(dateToday);
        incargo_contents_consignee.setText(deptName + " 전 화물 입고현황");


        fltBtn_Capture = findViewById(R.id.incargo_camera);
        fltBtn_Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentCameraActivity();
            }
        });

        fltBtn_Capture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Incargo.this, TitleActivity.class);

                startActivity(intent);
                return true;
            }
        });
        fltBtn_share = findViewById(R.id.incargo_floatBtn_share);
        fltBtn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upCapturePictures("InCargo",listItems.get(0).getConsignee());
                Map<String,Object> putValue=new HashMap<>();
                putValue.put("working","컨테이너 진입");
                databaseReference=
                        database.getReference("DeptName/" + deptName + "/" +"InCargo" + "/" +dateToday.substring(5,7) + "월/" + dateToday+"/"+keyValue);
                databaseReference.updateChildren(putValue);
                Toast.makeText(getApplicationContext(),"컨테이너 진입으로 작업현황 등록 됩니다.변경사항 있으면 추후 수정 바랍니다.",Toast.LENGTH_SHORT).show();
//                initIntent();

            }
        });
        fltBtn_share.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Incargo.this, WorkingMessageData.class);
                intent.putExtra("nickName", nickName);
                intent.putExtra("deptName", deptName);
                startActivity(intent);
                return true;
            }
        });

        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }


    }

    private void intentCameraActivity() {
        Intent intent = new Intent(Incargo.this, CameraCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("depotName", deptName);
        intent.putExtra("nickName", nickName);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];
            float gravityX = axisX / SensorManager.GRAVITY_EARTH;
            float gravityY = axisY / SensorManager.GRAVITY_EARTH;
            float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

            Float f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;
            double squaredD = Math.sqrt(f.doubleValue());
            float gForce = (float) squaredD;
            if (gForce > SHAKE_THERESHOLD_GRAVITY) {
                long currentTime = System.currentTimeMillis();
                if (mShakeTime + SHAKE_SKIP_TIME > currentTime) {
                    return;
                }
                mShakeTime = currentTime;
                Intent intent = new Intent(Incargo.this, Incargo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void searchSort() {
        AlertDialog.Builder searchBuilder = new AlertDialog.Builder(Incargo.this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        searchBuilder.setTitle("B/L 또는 컨테이너 번호 조회");
        searchBuilder.setMessage("마지막 4자리 번호 입력 바랍니다.");
        searchBuilder.setView(editText);

        searchBuilder.setPositiveButton("Bl", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sortContents = editText.getText().toString();
                searchContents = "bl";
                searchFirebaseDatabaseToArray(sortContents);

            }
        });
        searchBuilder.setNegativeButton("container", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sortContents = editText.getText().toString();
                searchContents = "container";
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
        if (month < 10) {
            month_string = "0" + Integer.toString(month + 1);
        } else {
            month_string = Integer.toString(month + 1);
        }
        String day_string;
        if (dayOfMonth < 10) {
            day_string = "0" + Integer.toString(dayOfMonth);
        } else {
            day_string = Integer.toString(dayOfMonth);
        }
        String year_string = Integer.toString(year);

        regDate = (year_string + "-" + month_string + "-" + day_string);
        reg_Button_date.setText("Date:" + regDate + "등록");
        reg_Button_date.setTextColor(Color.RED);
        dateSelectCondition = "Clicked";
        if (!regDate.equals("") && dateSelectCondition.equals("Clicked")) {
            Toast.makeText(getApplicationContext(), "Date Button Clicked", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_account_search:
                if (bl.equals("")) {
                    Toast.makeText(this, "화물조회 항목비엘 다시한번 확인 바랍니다.", Toast.LENGTH_SHORT).show();
                }

                webView(bl);
                break;

            case R.id.action_account_down:

                AlertDialog.Builder dataReg = new AlertDialog.Builder(this);
                dataReg.setTitle("화물정보 업데이트");
                View regView = getLayoutInflater().inflate(R.layout.reg_putdata, null);
                dataReg.setView(regView);

                reg_Button_date = regView.findViewById(R.id.reg_Button_date);
                if (listSortItems.size() == 0) {
                    Intent intent = new Intent(Incargo.this, PutDataReg.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("deptName", deptName);
                    intent.putExtra("list", upDataRegList);
                    intent.putExtra("consigneeList", shared_consigneeList);


                    startActivity(intent);
                }
                if (listSortItems.size() > 0) {
                    reg_Button_date.setText(listSortItems.get(0).getDate());
                    reg_edit_bl = regView.findViewById(R.id.reg_edit_bl);
                    reg_edit_bl.setText(listSortItems.get(0).getBl());
                    reg_Button_bl = regView.findViewById(R.id.reg_Button_bl);
                    reg_edit_container = regView.findViewById(R.id.reg_edit_container);
                    reg_edit_container.setText(listSortItems.get(0).getContainer());
                    reg_Button_container = regView.findViewById(R.id.reg_Button_container);
                    reg_edit_remark = regView.findViewById(R.id.reg_edit_remark);
                    reg_edit_remark.setText(listSortItems.get(0).getRemark());
                    reg_Button_remark = regView.findViewById(R.id.reg_Button_remark);
                    reg_Button_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downLoadingMark = "RegData";
                            String a = "b";
                            DatePickerFragment datePickerFragment = new DatePickerFragment(a);
                            datePickerFragment.show(getSupportFragmentManager(), "datePicker");

                        }
                    });
                    reg_Button_bl.setOnClickListener(v -> {

                        reg_Button_bl.setText("BL:" + regBl + "등록");
                        reg_Button_bl.setTextColor(Color.RED);
                        regBl = reg_edit_bl.getText().toString();
                        reg_edit_bl.setTextColor(Color.RED);
                    });

                    reg_Button_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            reg_Button_container.setText("Con`t:" + regContainer + "등록");
                            reg_Button_container.setTextColor(Color.RED);
                            regContainer = reg_edit_container.getText().toString();
                            reg_edit_container.setTextColor(Color.RED);
                        }
                    });

                    reg_Button_remark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            reg_Button_remark.setText("Remark:" + regRemark + "등록");
                            reg_Button_remark.setTextColor(Color.RED);
                            regRemark = reg_edit_remark.getText().toString();
                            reg_edit_remark.setTextColor(Color.RED);
                        }
                    });
                    dataReg.setPositiveButton("요약 자료등록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String consignee = listSortItems.get(0).consignee;
                            regData(consignee);

                        }
                    });
                    dataReg.setNegativeButton("세부자료 등록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Incargo.this, PutDataReg.class);
                            intent.putExtra("deptName", deptName);
                            intent.putExtra("list", upDataRegList);
                            intent.putExtra("consigneeList", shared_consigneeList);
                            startActivity(intent);


                        }
                    });

                    dataReg.setNeutralButton("신규등록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Incargo.this, PutDataReg.class);
                            intent.putExtra("deptName", deptName);
                            intent.putExtra("consigneeList", shared_consigneeList);
                            startActivity(intent);

                        }
                    });

                    dataReg.setMessage("총(" + listSortItems.size() + ")건의 화물정보 업데이트를" + "\n" + "하기 내용으로 UpDate 진행 합니다.");
                    dataReg.show();

                }

                break;
        }
        return true;

    }

    public void searchFirebaseDatabaseToArray(String sortContents) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                for (DataSnapshot searchsnapshot : snapshot.getChildren()) {
                    Fine2IncargoList data = searchsnapshot.getValue(Fine2IncargoList.class);
                    int containerNameLength = data.getContainer().length();
                    int blNameLength = data.getBl().length();

                    switch (searchContents) {
                        case "container":
                            if (containerNameLength == 11) {
                                String sort_contentsName = data.getContainer().substring(data.getContainer().length() - 4,
                                        data.getContainer().length());
                                if (sortContents.equals(sort_contentsName)) {
                                    listItems.add(data);

                                } else {
                                    ;
                                }
                            } else {
                            }
                            break;
                        case "bl":
                            if (blNameLength > 4) {
                                String sort_contentsName = data.getBl().substring(data.getBl().length() - 4,
                                        data.getBl().length());
                                if (sortContents.equals(sort_contentsName)) {
                                    listItems.add(data);
                                } else {
                                }
                            } else {
                            }
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

    public void webView(String bl) {
        Intent intent = new Intent(Incargo.this, WebList.class);
        intent.putExtra("bl", bl);
        startActivity(intent);
    }

    public void regData(String consignee) {
        Fine2IncargoList list = new Fine2IncargoList();

        String dateO, blO, desO, countO, contNO;
        for (int i = 0; i < listSortItems.size(); i++) {
            dateO = listSortItems.get(i).getDate();
            blO = listSortItems.get(i).getBl();
            desO = listSortItems.get(i).getDescription();
            countO = listSortItems.get(i).getCount();
            contNO = listSortItems.get(i).getContainer();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(dateO + "_" + blO + "_" + desO + "_" + countO + "_" + contNO + "/", null);
            databaseReference.updateChildren(childUpdates);
            String chBl;

            if (!regBl.equals("") && !reg_Button_bl.getText().toString().equals("BL")) {
                chBl = "(비엘:" + regBl + ")";
                list.setBl(regBl);
            } else {
                chBl = "";
                list.setBl(listSortItems.get(i).getBl());
                regBl = listSortItems.get(i).getBl();
            }

            list.setConsignee(listSortItems.get(i).getConsignee());
            String chContainer;
            if (!regContainer.equals("") && !reg_Button_container.equals("Container")) {
                chContainer = "(컨테이너 번호:" + regContainer + ")";
                list.setContainer(regContainer);
            } else {
                chContainer = "";
                list.setContainer(listSortItems.get(i).getContainer());
                regContainer = listSortItems.get(i).getContainer();
            }
            list.setContainer20(listSortItems.get(i).getContainer20());
            list.setContainer40(listSortItems.get(i).getContainer40());
            list.setCount(listSortItems.get(i).getCount());
            String chDate;
            if (!regDate.equals("") && dateSelectCondition.equals("Clicked")) {
                chDate = "(입고일:" + regDate + ")";
                list.setDate(regDate);

            } else {
                chDate = "";
                list.setDate(listSortItems.get(i).getDate());
                regDate = listSortItems.get(i).getDate();
            }
            list.setDescription(listSortItems.get(i).getDescription());
            list.setIncargo(listSortItems.get(i).getIncargo());
            list.setLclcargo(listSortItems.get(i).getLclcargo());
            list.setLocation(listSortItems.get(i).getLocation());
            String chRemark;
            if (!regRemark.equals("") && !reg_Button_remark.equals("Remark")) {
                chRemark = "(비고:" + regRemark + ")";
                list.setRemark(regRemark);
            } else {
                chRemark = "";
                list.setRemark(listSortItems.get(i).getRemark());
            }
            list.setWorking(listSortItems.get(i).getWorking());

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference =
                    database.getReference("DeptName/" + deptName + "/" +"InCargo" + "/" +day_start.substring(5,7) + "월/" + day_start+"/" + "/" + regDate + "_" + regBl + "_" + listSortItems.get(i).getDescription() +
                            "_" + listSortItems.get(i).getCount() + "_" + regContainer);
            DatabaseReference databaseRef =
                    database.getReference("Incargo" + "/" + listSortItems.get(i).getDate() + "_" + listSortItems.get(i).getBl() + "_" + listSortItems.get(i).getDescription() +
                            "_" + listSortItems.get(i).getCount() + "_" + listSortItems.get(i).getContainer());


            databaseReference.setValue(list);
            databaseRef.setValue(list);
            String msg =
                    "(" + listSortItems.get(i).getDate() + ")_" + listSortItems.get(i).getConsignee() + "_" + "비엘: " + listSortItems.get(i).getBl() +
                            "를";
            String msg1 = chDate + chBl + chContainer + chRemark + "로 변경 진행 합니다.";
            putMessage(msg + "\n" + msg1, "Etc", nickName);
            Log.i("TestValue", "beforeNickName Value" + nickName);

        }
        sort_dialog = "dialogsort";
        getFirebaseData(day_start, day_end, "sort", sortConsignee);
    }

    public void putDataReg() {
        Intent intent = new Intent(Incargo.this, PutDataReg.class);
        intent.putExtra("list", listItems);
        startActivity(intent);
    }

    public void putMessage(String msg, String etc, String nick) {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String timeStamp1 = new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String date = new SimpleDateFormat("yyyy년MM월dd일").format(new Date());

        WorkingMessageList messageList = new WorkingMessageList();
        messageList.setNickName(nick);
        messageList.setTime(timeStamp1);
        messageList.setMsg(msg);
        messageList.setDate(date);
        messageList.setConsignee(etc);
        messageList.setInOutCargo("Etc");
//        messageList.setUri("");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("WorkingMessage" + "/" + nick + "_" + date + "_" + timeStamp);
        databaseReference.setValue(messageList);
        TitleActivity titleActivity = new TitleActivity();
        titleActivity.pushMessage(deptName, nickName, msg, "WorkingMessage");

    }

    public void sortDialog(String startDay, String endDay, ArrayList<Fine2IncargoList> listSortList) {
        arrList.clear();
        arrConsignee.clear();
        int listSize = listSortList.size();
        String consigneeName;
        for (int i = 0; i < listSize; i++) {
            consigneeName = listSortList.get(i).getConsignee();
            if (!arrConsignee.contains(consigneeName)) {
                arrConsignee.add(consigneeName);
            }
        }


        String consigneeName1, getConsigneeName;
        int cont40 = 0;
        int cont20 = 0;
        int cargo = 0;
        int qty = 0;
        for (int i = 0; i < arrConsignee.size(); i++) {
            consigneeName1 = arrConsignee.get(i);
            for (int j = 0; j < listSortList.size(); j++) {
                getConsigneeName = listSortList.get(j).getConsignee();
                if (consigneeName1.equals(getConsigneeName)) {
                    cont40 = cont40 + Integer.parseInt(listSortList.get(j).getContainer40());
                    cont20 = cont20 + Integer.parseInt(listSortList.get(j).getContainer20());
                    cargo = cargo + Integer.parseInt(listSortList.get(j).getLclcargo());
                    qty = qty + Integer.parseInt(listSortList.get(j).getIncargo());
                }
            }
            ExtractIncargoDataList list = new ExtractIncargoDataList(consigneeName1, String.valueOf(cont40), String.valueOf(cont20),
                    String.valueOf(cargo), String.valueOf(qty));
            arrList.add(list);
            cont40 = 0;
            cont20 = 0;
            cargo = 0;
            qty = 0;
        }
//        adapter.notifyDataSetChanged();
        AlertDialog.Builder arrReBuilder = new AlertDialog.Builder(this);

        String cargoDate;
        if (startDay.equals(endDay)) {
            cargoDate = startDay;

        } else {
            cargoDate = startDay + "~" + endDay;

        }

        arrReBuilder.setTitle(cargoDate + " 입고화물 정보");
        View view = getLayoutInflater().inflate(R.layout.date_select_dialog, null);
        for (int i = 0; i < arrList.size(); i++) {
            cont40 = cont40 + Integer.parseInt(arrList.get(i).getContainer40());
            cont20 = cont20 + Integer.parseInt(arrList.get(i).getContainer20());
            cargo = cargo + Integer.parseInt(arrList.get(i).getLcLCargo());
            qty = qty + Integer.parseInt(arrList.get(i).getQty());
        }

        TextView textViewContainer40 = view.findViewById(R.id.exContainer40);
        textViewContainer40.setText(cont40 + " 대");
        TextView textViewContainer20 = view.findViewById(R.id.exContainer20);
        textViewContainer20.setText(cont20 + " 대");
        TextView textViewCargo = view.findViewById(R.id.exCargo);
        textViewCargo.setText(cargo + " 건");
        TextView textViewQty = view.findViewById(R.id.exQty);
        textViewQty.setText(qty + " PLT");

        consignee_list=consigneeArrayList.toArray(new String[consigneeArrayList.size()]);

        consigneeArrayList.add(0,"ALL");
        shared_consigneeList=consigneeArrayList.toArray(new String[consigneeArrayList.size()]);

        Button searchDetail = view.findViewById(R.id.btnDetailSearch);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerEx = view.findViewById(R.id.resortEx);
        recyclerEx.setLayoutManager(layoutManager);
        ExtractIncargoDataAdapter adapter = new ExtractIncargoDataAdapter(arrList);
        recyclerEx.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        arrReBuilder.setView(view);
//        arrReBuilder.show();
        AlertDialog dialog = arrReBuilder.create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        searchDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortCargoDataDialog();
                dialog.dismiss();
            }
        });
        searchDetail.setOnLongClickListener(v -> {

            return true;
        });
        Button btnThisMonth = view.findViewById(R.id.btnThisMonth);
        btnThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirebaseDataSortDate("ThisMonth");
                dialog.dismiss();
            }
        });
        Button btnNextWeek = view.findViewById(R.id.btnNextWeek);
        btnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirebaseDataSortDate("NextWeek");
                dialog.dismiss();
            }
        });
        Button btnThisWeek = view.findViewById(R.id.btnThisWeek);
        btnThisWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirebaseDataSortDate("ThisWeek");
                dialog.dismiss();
            }
        });

        Button btnTomorrow = view.findViewById(R.id.btnTomorrow);
        btnTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirebaseDataSortDate("Tomorrow");
                dialog.dismiss();
            }
        });
        if (getIntent().getStringExtra("refPath") != null) {
            selectedItemGetDatabase(getIntent().getStringExtra("refPath"));
            pickedUpItemClick(getIntent().getStringExtra("refPath"));
            dialog.dismiss();
        }

    }

    private void sortCargoDataDialog() {
        View view = getLayoutInflater().inflate(R.layout.update_datepicker_spinner, null);
        DatePicker datePickerDefault = view.findViewById(R.id.udatepicker_default);
        Button btnStart = view.findViewById(R.id.uBtnSearchDate_start);
        Button btnEnd = view.findViewById(R.id.uBtnSearchDate_end);
        Calendar calendar = Calendar.getInstance();
        Button btnSearch = view.findViewById(R.id.ubtnSearchDate);
        TextView txtUntil = view.findViewById(R.id.txtUntil);


        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String sbMonth, sbDay;

        if ((month + 1) < 10) {
            sbMonth = "0" + (month + 1);
        } else {
            sbMonth = String.valueOf(month + 1);
        }

        if (day < 10) {
            sbDay = "0" + day;
        } else {
            sbDay = String.valueOf(day);
        }
        String dateBasic = year + "-" + sbMonth + "-" + sbDay;
        btnStart.setText(dateBasic);
        btnEnd.setText(dateBasic);

        final String[] pickDate = new String[1];
        datePickerDefault.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String bmonth, bday;

                if (monthOfYear < 9) {
                    bmonth = "0" + (monthOfYear + 1);
                } else {
                    bmonth = String.valueOf(monthOfYear + 1);
                }
                if (dayOfMonth < 10) {
                    bday = "0" + dayOfMonth;
                } else {
                    bday = String.valueOf(dayOfMonth);
                }
                pickDate[0] = year + "-" + bmonth + "-" + bday;
                if (!dateSelectCondition.equals("until")) {
                    btnStart.setText(pickDate[0]);
                    btnEnd.setText(pickDate[0]);
                }
            }
        });

        datePickerDefault.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mSelectedItems.get(0, false)) {
                    dateSelectCondition = "date";
                    mSelectedItems.put(0, false);
                    Toast.makeText(getApplicationContext(), "기간설정", Toast.LENGTH_SHORT).show();
                } else {
                    dateSelectCondition = "until";
                    mSelectedItems.put(0, true);
                    Toast.makeText(getApplicationContext(), "시작일~ 종료일 설정", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        txtUntil.setOnClickListener(v -> {
            if (mSelectedItems.get(0, false)) {
                dateSelectCondition = "date";
                mSelectedItems.put(0, false);
                txtUntil.setText("=");
            } else {
                dateSelectCondition = "until";
                mSelectedItems.put(0, true);
                txtUntil.setText("~");
            }

        });

        btnStart.setOnClickListener(v -> {

            btnStart.setText(pickDate[0]);
            Toast.makeText(this, "검색 시작일 을 " + pickDate[0] + " 일로 설정", Toast.LENGTH_SHORT).show();

        });
        btnStart.setOnLongClickListener(v -> {
            btnStart.setText(dateBasic);
            Toast.makeText(this, "검색 시작일 을 당일(" + dateBasic + " )일로 설정", Toast.LENGTH_SHORT).show();
            return true;
        });
        btnEnd.setOnClickListener(v -> {
            btnEnd.setText(pickDate[0]);
            Toast.makeText(this, "검색 종료일 을 " + pickDate[0] + " 일로 설정", Toast.LENGTH_SHORT).show();
        });
        btnEnd.setOnLongClickListener(v -> {
            btnEnd.setText(dateBasic);
            Toast.makeText(this, "검색 종료일 을 당일(" + dateBasic + " )일로 설정", Toast.LENGTH_SHORT).show();
            return true;
        });


        final String[] spinnerconsignee = {"All"};
        Spinner consigneeSpinner = view.findViewById(R.id.spinner_consigneelist);
        ArrayAdapter<String> consigneeListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                shared_consigneeList);
        consigneeSpinner.setAdapter(consigneeListAdapter);
        consigneeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerconsignee[0] = shared_consigneeList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder sortDialog = new AlertDialog.Builder(this);
        sortDialog.setView(view);
//                .show();
        AlertDialog dialog = sortDialog.create();
        dialog.show();
        ;
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day_start = btnStart.getText().toString();
                day_end = btnEnd.getText().toString();
                getFirebaseData(day_start, day_end, "sort", spinnerconsignee[0]);
                dialog.dismiss();
            }
        });

    }

    public void getFirebaseData(String startDay, String endDay, String sortItems, String sortConsignee) {
        int startDayRe=Integer.parseInt(startDay.replace("-",""));
        int endDayRe=Integer.parseInt(endDay.replace("-",""));

//        for (int i = (listOutSize - 1); 0 <= i; i--) {
//            if (!refPath.equals(listOut.get(i).getKeypath())) {
//                listOut.remove(i);
//
//            }
        Log.i("TestValue","listitemsSize:"+listItems.size());
        for(int i=(listItems.size()-1);0<=i;i--){
            int startDayList=Integer.parseInt(listItems.get(i).getDate().replace("-",""));
            int endDayList=Integer.parseInt(listItems.get(i).getDate().replace("-",""));
            if(startDayList<=startDayRe||endDayList>=endDayRe){
                listItems.remove(i);
            }
            Log.i("TestValue","listitemsSize:"+listItems.size());
        }
//        ValueEventListener dataListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listItems.clear();
//                listSortList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Fine2IncargoList data = dataSnapshot.getValue(Fine2IncargoList.class);
//
//                    assert data != null;
//                    String forty = data.getContainer40();
//                    String twenty = data.getContainer20();
//                    String cargo = data.getLclcargo();
//                    Log.i("TestValue","sortItems:"+sortItems+"/sortConsignee:"+sortConsignee);
//                    if (sortItems.equals("all")) {
//                        if (sortConsignee.equals("ALL")) {
//                            listItems.add(data);
//                            listSortList.add(data);
//
//                        } else {
//                            if (sortConsignee.equals(data.getConsignee())) {
//                                listItems.add(data);
//                                listSortList.add(data);
//                            }
//                        }
//
//                    } else if (sortItems.equals("sort")) {
//                        final boolean b = !forty.equals("0") || !twenty.equals("0") || !cargo.equals("0");
//                        if (sortConsignee.equals("ALL")) {
//                            if (b) {
//                                listItems.add(data);
//                                listSortList.add(data);
//                            }
//                        } else {
//                            if (b) {
//                                if (sortConsignee.equals(data.getConsignee())) {
//                                    listItems.add(data);
//                                    listSortList.add(data);
//                                }
//                            }
//                        }
//                    }
//                    listItems.sort(new Comparator<Fine2IncargoList>() {
//                        @Override
//                        public int compare(Fine2IncargoList o1, Fine2IncargoList o2) {
//                            int compare=o1.working.compareTo(o2.working);
//                            return compare;
//                        }
//                    });
//                    Collections.reverse(listItems);
//                }
//                Collections.reverse(listItems);
//                adapter.notifyDataSetChanged();

                sortDialog(startDay, endDay, listItems);
//                sortDialogOld(startDay, endDay, listSortList);
                String date;
                if (startDay.equals(endDay)) {
                    date = startDay;
                } else {
                    date = startDay + "~" + endDay;
                }
                incargo_contents_date.setText(date);
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.i("TestValue","FirebaseDataReference Occured :"+error);
//            }
//        };
//
//        sortByData = databaseReference.orderByChild("date").startAt(startDay).endAt(endDay);
//        sortByData.addListenerForSingleValueEvent(dataListener);


    }

    public void getFirebaseDataInit() {
        for(int i=1;i<=12;i++){
            Calendar calendar=Calendar.getInstance();
            calendar.set(2021,i-1,1);
            int monthOfLastDay=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for(int j=1;j<=monthOfLastDay;j++){
                String date = null;
                String month = null;
                if (i + 1 < 10) {
                    month = "0" + i;
                } else {
                    month = String.valueOf(i);
                }
                if (j < 10) {
                    date = "0" + j;
                } else {
                    date = String.valueOf(j);
                }
                DatabaseReference databaseReference =
                        database.getReference("DeptName/" + deptName + "/" +"InCargo" + "/" +month+ "월/" + "2021-" + month+
                                        "-" + date+"/"
                                );
                String finalMonth = month;
                String finalDate = date;
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String keyValue=dataSnapshot.getKey();
                            assert keyValue != null;
                            if(!keyValue.equals("json 등록시 덥어쓰기 바랍니다")){

                                Fine2IncargoList data = dataSnapshot.getValue(Fine2IncargoList.class);
                                assert data != null;
                                arrConsignee.add(data.getConsignee());
                                listItems.add(data);
                                if(!consigneeArrayList.contains(data.getConsignee())){
                                    consigneeArrayList.add(data.getConsignee());
                                }
                            }

                            if(finalMonth.equals("12")&& finalDate.equals("31")){
                                getFirebaseData(dateToday, dateToday, "all", sortConsignee,listItems);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }


        }
//        getFirebaseData(dateToday, dateToday, "all", sortConsignee);


    }

    private void getFirebaseData(String startDay, String endDay, String all, String sortConsignee,
                                 ArrayList<Fine2IncargoList> listItems) {
        int startDayRe=Integer.parseInt(startDay.replace("-",""));
        int endDayRe=Integer.parseInt(endDay.replace("-",""));

//        for (int i = (listOutSize - 1); 0 <= i; i--) {
//            if (!refPath.equals(listOut.get(i).getKeypath())) {
//                listOut.remove(i);
//
//            }

        for(int i=(listItems.size()-1);0<=i;i--){
            int startDayList=Integer.parseInt(listItems.get(i).getDate().replace("-",""));
            int endDayList=Integer.parseInt(listItems.get(i).getDate().replace("-",""));
            if(startDayList<startDayRe||endDayList>endDayRe){
                Log.i("TestValue","List Date:::"+listItems.get(i).getDate());
                listItems.remove(i);
            }
        }

    }


    public void getFirebaseDataSortDate(String sortDateItem) {
        Calendar calendar = Calendar.getInstance();
        String format = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String sbMonth, sbDay;
        if ((month + 1) < 10) {
            sbMonth = "0" + (month + 1);
        } else {
            sbMonth = String.valueOf(month + 1);
        }
        if (day < 10) {
            sbDay = "0" + day;
        } else {
            sbDay = String.valueOf(day);
        }
        String dateBasic = year + "-" + sbMonth + "-" + sbDay;

        switch (sortDateItem) {
            case "ThisMonth":
                day_start = year + "-" + sbMonth + "-" + "01";
                String maxDay = String.valueOf(calendar.getMaximum(Calendar.DAY_OF_MONTH));
                day_end = year + "-" + sbMonth + "-" + maxDay;
                break;
            case "NextWeek":
                calendar.add(Calendar.DATE, 7);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                day_start = simpleDateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 5);
                day_end = simpleDateFormat.format(calendar.getTime());
                break;
            case "ThisWeek":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                day_start = simpleDateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 5);
                day_end = simpleDateFormat.format(calendar.getTime());
                break;
            case "Tomorrow":
                calendar.add(Calendar.DATE, 1);
                day_start = simpleDateFormat.format(calendar.getTime());
                day_end = simpleDateFormat.format(calendar.getTime());
                break;
        }

        getFirebaseData(day_start, day_end, "sort", "ALL");

        Toast.makeText(this, day_start + "일부터" + day_end + "까지 검색을 시작 합니다.", Toast.LENGTH_SHORT).show();


    }

    private void dateSelectedDialog(String date) {
        View view = getLayoutInflater().inflate(R.layout.date_select_click_event, null);
        AlertDialog.Builder clickBuilder = new AlertDialog.Builder(this);
        clickBuilder.setView(view)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



    @Override
    public void onItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
        String consigneeName=listItems.get(pos).getConsignee();
        keyValue = listItems.get(pos).getDate() + "_" + listItems.get(pos).getBl() + "_" + listItems.get(pos).getDescription() +
                "_" + listItems.get(pos).getCount() + "_" + listItems.get(pos).getContainer();
        String updateTitleValue=
                consigneeName+"_비엘:"+listItems.get(pos).getBl()+"_컨테이너:"+listItems.get(pos).getContainer();
        selectedItemGetDatabase(keyValue);

        pickedUpItemClickDialog(keyValue,updateTitleValue,consigneeName);


    }

    private void selectedItemGetDatabase(String keyValue) {
        listItems.clear();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    Fine2IncargoList mList=data.getValue(Fine2IncargoList.class);
                    if(Objects.equals(data.getKey(), keyValue)){
                        listItems.add(mList);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void pickedUpItemClickDialog(String keyValue, String updateTitleValue, String consigneeName) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("DeptName/" + deptName + "/" +"InCargo" + "/" +day_start.substring(5,7) + "월/" + day_start+"/" + keyValue);
        AlertDialog.Builder builder = new AlertDialog.Builder(Incargo.this);

        ArrayList<String> incargoContent = new ArrayList<>();
        incargoContent.add("컨테이너 진입");
        incargoContent.add("입고작업 완료");
        incargoContent.add("검수완료");
        incargoContent.add("창고반입");
        incargoContent.add("입고관련 사진등록");
        incargoContent.add("신규입고 항목으로 공유");


        String[] incargoContentList = incargoContent.toArray(new String[incargoContent.size()]);

        builder.setTitle(updateTitleValue+" 입고")
                .setSingleChoiceItems(incargoContentList, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (which == 4) {

                                            pickedUpItemClick(keyValue);

                        } else if(which==5){
                            PublicMethod publicMethod=new PublicMethod(Incargo.this);
                            publicMethod.putNewDataUpdateAlarm(nickName,deptName,updateTitleValue+" 신규 등록",consigneeName,
                                    "InCargo",requestQueue);

                        }else{
                            String contentValue = incargoContentList[which];
                            Map<String, Object> putValue = new HashMap<>();
                            putValue.put("working", contentValue);
                            databaseReference.updateChildren(putValue);
                            initIntent();
                            }


                    }
                })
                .show();


    }

    @Override
    public void onLongItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
        String dialogTitle=listItems.get(pos).getConsignee()+"_"+listItems.get(pos).getDescription();
        selectLongClickDialog(dialogTitle,pos);

    }

    private void selectLongClickDialog(String dialogTitle, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("입고화물 정보 수정")
                .setMessage(dialogTitle+" 화물정보에 대한 수정,삭제 진행을 합니다." + "\n" + "화물정보 삭제시에는 하단 삭제 버튼 클릭" + "\n" + "화물정보 수정시에는 하단 " +
                        "정보수정 버큰 " +
                        "클릭후 메뉴 중간 버튼 " +
                        "클릭후 내용 수정 진행 바랍니다.")
                .setPositiveButton("정보 수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        bl = listItems.get(pos).getBl();
                        String incargoWorking = listItems.get(pos).getWorking();
                        String incargoDate = listItems.get(pos).getDate();
                        String incargoConsigneeName = listItems.get(pos).getConsignee();
                        String incargoDescription = listItems.get(pos).getDescription();
                        String incargoContainerNumber = listItems.get(pos).getContainer();
                        String incargoQty = listItems.get(pos).getIncargo();
                        String incargoBl = listItems.get(pos).getBl();
                        String incargoRemark = listItems.get(pos).getRemark();
                        String incargoContainer20 = String.valueOf(listItems.get(pos).getContainer20());
                        String incargoContainer40 = String.valueOf(listItems.get(pos).getContainer40());
                        String incargoCargo = String.valueOf(listItems.get(pos).getLclcargo());


                        upDataRegList = new String[]{incargoWorking, incargoDate, incargoConsigneeName, incargoDescription,
                                incargoContainer20, incargoContainer40, incargoCargo,
                                incargoContainerNumber, incargoQty, incargoBl, incargoRemark};

                        if (selectedSortItems.get(pos, false)) {
                            selectedSortItems.put(pos, false);

                            listSortItems.remove(listItems.get(pos));
                            Toast.makeText(Incargo.this, incargoDate + "_" + incargoConsigneeName + "_" + bl + "_" + incargoContainerNumber + "항목 해제",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            selectedSortItems.put(pos, true);

                            listSortItems.add(listItems.get(pos));
                            Toast.makeText(Incargo.this, incargoDate + "_" + incargoConsigneeName + "_" + bl + "_" + incargoContainerNumber + "항목 선택",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .setNegativeButton("정보 삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AlertDialog.Builder deleteItem = new AlertDialog.Builder(Incargo.this);
                        deleteItem.setTitle("항목 제거 진행");
                        String deDate = listItems.get(pos).getDate();
                        String deConsignee = listItems.get(pos).getConsignee();
                        String deBl = listItems.get(pos).getBl();
                        String deCont = listItems.get(pos).getContainer();
                        String deCount = listItems.get(pos).getCount();
                        String deDes = listItems.get(pos).getDescription();
                        String msg = "반입일: " + deDate + "\n" + "화주명: " + deConsignee + "\n" + "Bl: " + deBl +
                                "\n" + "컨테이너번호: " + deCont + "\n" + "화물 정보 삭제를 진행 합니다.";
                        String msgWorking = "반입일: " + deDate + "_" + "화주명: " + deConsignee + "_" + "\n" + "Bl: " + deBl +
                                "_컨테이너번호 : " + deCont + "에 대한" + "화물 정보 삭제를 진행.";
                        deleteItem.setMessage(msg);
                        deleteItem.setPositiveButton("삭제 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(deDate + "_" + deBl + "_" + deDes + "_" + deCount + "_" + deCont + "/", null);
                                databaseReference.updateChildren(childUpdates);

                                putMessage(msgWorking, "Etc", nickName);
                                getFirebaseData(dateToday, dateToday, "sort", sortConsignee);
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
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {

        if (selectedSortItems.get(position, true)) {
            selectedSortItems.put(position, false);
            imageViewListsSelected.add(imageViewLists.get(position));
        } else {
            selectedSortItems.put(position, true);
            imageViewListsSelected.remove(imageViewLists.get(position));
        }
        if (imageViewListsSelected.size() > 7) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("!사진전송 주의사항")
                    .setMessage("한번에 전송할수 있는 사진은 최대 7장 입니다.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "사진을 다신 선택 하기 바랍니다.", Toast.LENGTH_SHORT).show();

                        }
                    }).show();
        }
        String clickedPictureCount = "(" + imageViewListsSelected.size() + "장 선택)";
        incargo_contents_date.setText(dateToday + " 입고 목록" + clickedPictureCount);

    }

    public void pickedUpItemClick(String value) {
        RecyclerView imageRecyclerView = findViewById(R.id.incargo_recyclerView_image);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        imageRecyclerView.setLayoutManager(manager);
        PublicMethod pictures = new PublicMethod(this);
        imageViewLists = pictures.getPictureLists();
        iAdapter = new ImageViewActivityAdapter(imageViewLists, this);
        imageRecyclerView.setAdapter(iAdapter);


    }
        public void initIntent() {
        Intent intent = new Intent(Incargo.this, Incargo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void upCapturePictures(String inoutItems, String consigneeName) {
        int arrsize = imageViewListsSelected.size();

        SharedPreferences sharedPreferences = getSharedPreferences("SHARE_DEPOT", MODE_PRIVATE);
        String nick = sharedPreferences.getString("nickName", "Fine");

        String message = consigneeName + "_" + inoutItems + "_사진 업로드";
        CaptureProcess captureProcess = new CaptureProcess(this);
        String activityName = this.getClass().getSimpleName();
        for (int i = 0; i < arrsize; i++) {
            Uri uri = Uri.fromFile(new File(imageViewListsSelected.get(i)));
            String strRef = dateToday + "/" + consigneeName + "/" + inoutItems + "/" + nick + System.currentTimeMillis() + ".jpg";
            captureProcess.firebaseCameraUpLoad(uri, consigneeName, inoutItems, nick, message, strRef, i, arrsize, activityName);
        }
    }
    public void sendMessage(String message){

        PushFcmProgress push=new PushFcmProgress(requestQueue);

        push.sendAlertMessage(deptName,nickName,message,"CameraUpLoad");
    }

    public void messageIntent() {
        Intent intent=new Intent(this,WorkingMessageData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {

       PublicMethod publicMethod=new PublicMethod(this);
       publicMethod.intentSelect();
    }

    @Override
    public int compare(Fine2IncargoList a, Fine2IncargoList b) {
        int compare=0;
        compare=a.working.compareTo(b.working);
        return 0;
    }
}




