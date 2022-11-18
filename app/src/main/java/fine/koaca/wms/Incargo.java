package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Incargo extends AppCompatActivity implements Serializable , SensorEventListener,
        IncargoListAdapter.AdapterClickListener,IncargoListAdapter.AdapterLongClickListener,
        ImageViewActivityAdapter.ImageViewClicked, Comparator<Fine2IncargoList>,IncargoListAdapter.ItemConsigneeClickListener {
    ArrayList<Fine2IncargoList> listItems = new ArrayList<Fine2IncargoList>();
    ArrayList<Fine2IncargoList> listSortItems = new ArrayList<Fine2IncargoList>();
    ArrayList<Fine2IncargoList> listIn = new ArrayList<>();
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

    String keyValue;

    PublicMethod publicMethod;
    String sharedValue;

    LinearLayout itemLayout;
    Button btnConIn,btnDevCom,btnInsCom,btnIncargoCom,btnNewIncargo,btnRegPallet,btnPicList,btnPicCount;
    String itemClickTitle;

    RecyclerView imageRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incargo);
        /* ItemClick Listener
        onItemClick reference
        imageViewClicked imageViewClicked reference
         */

        sharedValue="";
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        publicMethod = new PublicMethod(this);
        deptName = publicMethod.getUserInformation().get("deptName");
        nickName = publicMethod.getUserInformation().get("nickName");

        dateToday = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
//        dateToday="2022-02-17";
        day_start = dateToday;
        day_end = dateToday;

        imageRecyclerView=findViewById(R.id.incargo_recyclerView_image);
        imageRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TestValue","imageRecyclerView Clicked Checked=");
            }
        });
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
        imageRecyclerView.setLayoutManager(gridLayoutManager);
        imageViewLists=publicMethod.getPictureLists("Re",dateToday);
        iAdapter=new ImageViewActivityAdapter(imageViewLists,this);
        imageRecyclerView.setAdapter(iAdapter);

        fltBtn_share = findViewById(R.id.incargo_floatBtn_share);
        fltBtn_share.setVisibility(View.INVISIBLE);

        itemLayout=findViewById(R.id.incargo_itemLayout);
        itemLayout.setVisibility(View.INVISIBLE);
        btnPicCount=findViewById(R.id.incargo_regReamrk);
        btnPicCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyValue=getIntent().getStringExtra("refPath");
                String consigneeNameValue=getIntent().getStringExtra("consigneeName");
                String blValue=getIntent().getStringExtra("bl");
                String containerValue=getIntent().getStringExtra("container");
                String dateValue= getIntent().getStringExtra("date");
                if(keyValue==null){
                    keyValue=listItems.get(0).getKeyValue();
                    dateValue=listItems.get(0).getDate();
                }
                publicMethod.putRemarkValue(listItems.get(0).getBl(),listItems.get(0).getDescription());
//
            }
        });

        btnPicCount.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String keyValue=listItems.get(0).getKeyValue();
                String date=listItems.get(0).getDate();
                String month=date.substring(5,7)+"월";
                String refPath="DeptName/"+deptName+"/InCargo/"+month+"/"+date+"/"+keyValue;

                Intent intent=new Intent(Incargo.this,Location.class);
                Fine2IncargoList setList=listItems.get(0);
                intent.putExtra("list",setList);
                intent.putExtra("refPath",refPath);
                startActivity(intent);
                return true;
            }
        });
        fltBtn_share.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
                ArrayList<String> picItemList=new ArrayList<>();
                picItemList.add("업무 원본사진");
                picItemList.add("갤러리 전체 사진");
                picItemList.add("서버 전송용 조정사진");
                picItemList.add("서버 저장된 사진 검색");

                String[] picItemListArr=picItemList.toArray(new String[picItemList.size()]);
                builder.setTitle("사진 저장소 선택창")
                        .setSingleChoiceItems(picItemListArr,0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Animation ani=new AlphaAnimation(0.0f,1.0f);
                                ani.setRepeatMode(Animation.REVERSE);
                                ani.setDuration(1000);
                                ani.setRepeatCount(Animation.INFINITE);
                                switch(i){
                                    case 0:
                                        pickedUpItemClick("Ori",dateToday);
                                        btnPicCount.setText("전송사진을 선택 하세요");
                                        btnPicCount.startAnimation(ani);
                                        break;
                                    case 1:
                                        pickedUpItemClick("All",dateToday);
                                        btnPicCount.setText("전송사진을 선택 하세요");
                                        btnPicCount.startAnimation(ani);
                                        break;
                                    case 2:
                                        pickedUpItemClick("Re",dateToday);
                                        btnPicCount.setText("전송사진을 선택 하세요");
                                        btnPicCount.startAnimation(ani);

                                        break;
                                    case 3:
                                        if(keyValue==null){
                                            keyValue= getIntent().getStringExtra("refPath");
                                        }
                                        itemPictureList(keyValue);
                                        btnPicList.setText("서버 저장된 사진 검색");
                                        btnPicCount.setText("디바이스 저장 원하면 사진 클릭");
                                        btnPicCount.setTextColor(Color.RED);
                                        btnPicCount.startAnimation(ani);
                                        break;
                                }
                            }
                        })
                        .show();
                return true;
            }
        });

        btnPicList=findViewById(R.id.incargo_picCount);

        btnPicList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              AlertDialog.Builder dateDialog=new AlertDialog.Builder(Incargo.this);
              DatePicker datePicker=new DatePicker(Incargo.this);
                final String[] datePicturePic = {new SimpleDateFormat("yyyy-MM-dd").format(new Date())};
              datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                  @Override
                  public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                   String month,day;
                   if((i1+1)<10){
                       month="0"+(i1+1);
                   }else{
                       month=String.valueOf(i1+1);
                      }
                   if(i2<10){
                       day="0"+i2;
                   }else{
                       day=String.valueOf(i2);
                      }
                   datePicturePic[0] =i+"-"+month+"-"+day;
                   Toast.makeText(Incargo.this,datePicturePic[0]+" 검색일 지정",Toast.LENGTH_SHORT).show();
                  }
              });
              dateDialog.setTitle("사진 검색일 설정창")
                      .setView(datePicker)
                      .setPositiveButton("검색", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                             pickedUpItemClick("Re",datePicturePic[0]);
                          }
                      })
                      .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {

                          }
                      })
                      .show();
            }
        });

       btnPicList.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               publicMethod.imageViewListCount();;
               return true;
           }
       });



        btnConIn=findViewById(R.id.incargo_btnConIn);
        btnDevCom=findViewById(R.id.incargo_btnDevCom);
        btnInsCom=findViewById(R.id.incargo_btnInsCom);
        btnIncargoCom=findViewById(R.id.incargo_btnIncargoCom);
        btnRegPallet=findViewById(R.id.incargo_btnRegPallet);

        btnNewIncargo=findViewById(R.id.incargo_btnNewIncargo);
        checkWeekend();
        incargo_incargo = findViewById(R.id.incargo_incargo);
        incargo_contents_date = findViewById(R.id.incargo_contents_date);
        incargo_contents_consignee = findViewById(R.id.incargo_contents_consignee);
        incargo_mnf = findViewById(R.id.incargo_mnf);
        recyclerView = findViewById(R.id.incargo_recyclerViewList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        listItems = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        listIn =(ArrayList<Fine2IncargoList>)getIntent().getSerializableExtra("IncargoData");
        adapter = new IncargoListAdapter(listItems, this, this,this);
        recyclerView.setAdapter(adapter);
        if(getIntent().getStringExtra("refPath")==null){
//            if (listIn != null){
//                listIn =(ArrayList<Fine2IncargoList>)getIntent().getSerializableExtra("IncargoData");
//                adapter = new IncargoListAdapter(listIn, this, this,this);
//                recyclerView.setAdapter(adapter);
//                sortDialog(day_start, day_end, listIn);
//                String date;
//                if (day_start.equals(day_end)) {
//                    date = day_start;
//                } else {
//                    date = day_start + "~" + day_end;
//            }
//                incargo_contents_date.setText(date);
//            }else{
//                adapter = new IncargoListAdapter(listItems, this, this,this);
                getFirebaseData(day_start, day_end, "sort", "ALL");
//                recyclerView.setAdapter(adapter);

//            }






        }else{
            String date=getIntent().getStringExtra("date");
            String keyValue=getIntent().getStringExtra("refPath");
            String consigneeNameI=getIntent().getStringExtra("consigneeName");
            String bl=getIntent().getStringExtra("bl");
            String container=getIntent().getStringExtra("container");
            initLayout(consigneeNameI, keyValue, container, bl, date );

        }
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

                getFirebaseData(day_start, day_end, "all", "ALL");
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

        fltBtn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String consigneeName=listItems.get(0).getConsignee();
                publicMethod = new PublicMethod(Incargo.this, imageViewListsSelected);
                if(sharedValue.equals("Pallet")){
                                String bl = listItems.get(0).getBl();
                                String des = listItems.get(0).getDescription();
                                String consigneeNameP=listItems.get(0).getConsignee();
                                AlertDialog.Builder builder = new AlertDialog.Builder(Incargo.this);
                                builder.setTitle("팔렛트 등록 확인창")
                                        .setMessage("사용등록:" +"\n" + consigneeNameP+"_"+bl+"_"+"_"+des+"\n"+"화물의 팔렛트 사용등록" + "\n" +"신규 입고등록: 화주,재고 확인후 팔렛트 신규입고 등록")

                                        .setPositiveButton("사용 등록", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                                publicMethod.pltReg(consigneeName, keyValue, nickName, 0, bl, des);
                                                    getPalletStock(consigneeName,bl,des);
                                            }
                                        })

                                        .setNeutralButton("신규 입고등록", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                manualPltReg();

                                            }
                                        })
                                        .show();

                }else{

                    publicMethod.upLoadPictures(nickName, consigneeName, "InCargo", listItems.get(0).getKeyValue(),
                            deptName);
                    String date = listItems.get(0).getDate();
                    String workingProcess;
                    if(listItems.get(0).getWorking().equals("") ){
                        workingProcess="컨테이너 진입";
                    }else{
                        workingProcess=listItems.get(0).getWorking();
                    }
                    Map<String, Object> putValue = new HashMap<>();
                    putValue.put("working", workingProcess);
                    databaseReference =
                            database.getReference("DeptName/" + deptName + "/" + "InCargo" + "/" + date.substring(5, 7) + "월/" + date +
                                    "/" + keyValue);
                    databaseReference.updateChildren(putValue);
                    imageViewListsSelected.clear();

                    Toast.makeText(getApplicationContext(), "컨테이너 진입으로 작업현황 등록 됩니다.변경사항 있으면 추후 수정 바랍니다.", Toast.LENGTH_SHORT).show();
                    if(keyValue==null){
                        keyValue=getIntent().getStringExtra("refPath");
                    }

                    itemPictureList(keyValue);
                }


            }
        });



    }

    private void checkWeekend() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        String satDay = simpleDateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String sunDay = simpleDateFormat.format(calendar.getTime());
        if (dateToday.equals(sunDay)) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            day_start = simpleDateFormat.format(calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            day_end = simpleDateFormat.format(calendar.getTime());
        }

        if (dateToday.equals(satDay)) {
            calendar.add(Calendar.DATE, 7);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            day_start = simpleDateFormat.format(calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            day_end = simpleDateFormat.format(calendar.getTime());
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

                Intent intent = new Intent(getApplicationContext(), CameraCapture.class);
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
        searchBuilder.setNeutralButton("ALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sortContents = editText.getText().toString();
                searchContents = "ALL";
                getFirebaseData("2021-01-01", "2021-12-31", "all", sortContents);
                searchFirebaseDatabaseToArray(sortContents);

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

        for (int i = (listItems.size() - 1); 0 <= i; i--) {
            int containerNameLength = listItems.get(i).getContainer().length();
            int blNameLength = listItems.get(i).getBl().length();

            switch (searchContents) {
                case "container":
                    if (containerNameLength == 11) {
                        String sort_contentsName = listItems.get(i).getContainer().substring(listItems.get(i).getContainer().length() - 4);
                        if (!sortContents.equals(sort_contentsName)) {
                            listItems.remove(i);
                        }
                    }
                    break;
                case "bl":
                    if (blNameLength > 4) {
                        String sort_contentsName = listItems.get(i).getBl().substring(listItems.get(i).getBl().length() - 4);
                        if (!sortContents.equals(sort_contentsName)) {
                            listItems.remove(i);
                        }
                    }
                    break;
                case "ALL":
                    if (containerNameLength == 11 && blNameLength > 4) {
                        String sort_containerName =
                                listItems.get(i).getContainer().substring(listItems.get(i).getContainer().length() - 4);
                        String sort_blName =
                                listItems.get(i).getBl().substring(listItems.get(i).getBl().length() - 4);
                        if (!sortContents.equals(sort_containerName) || sortContents.equals(sort_blName)) {
                            listItems.remove(i);
                        }

                    }

            }

        }
        adapter.notifyDataSetChanged();


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
            String keyValue = listSortItems.get(i).getKeyValue();
            DatabaseReference databaseReference =
                    database.getReference("DeptName/" + deptName + "/" + "InCargo" + "/" + keyValue.substring(5, 7) + "월/" + keyValue.substring(0, 10));
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
            databaseReference =
                    database.getReference("DeptName/" + deptName + "/" + "InCargo" + "/" + regDate.substring(5, 7) + "월/" + regDate +
                            "/" + regDate + "_" + regBl + "_" + listSortItems.get(i).getDescription() +
                            "_" + listSortItems.get(i).getCount() + "_" + regContainer);

            databaseReference.setValue(list);

            String msg =
                    "(" + listSortItems.get(i).getDate() + ")_" + listSortItems.get(i).getConsignee() + "_" + "비엘: " + listSortItems.get(i).getBl() +
                            "를";
            String msg1 = chDate + chBl + chContainer + chRemark + "로 변경 진행 합니다.";
            publicMethod = new PublicMethod(this);
            publicMethod.putNewDataUpdateAlarm(nickName, msg + "\n" + msg1, listSortItems.get(i).getConsignee(), "InCargo", deptName);


        }
        sort_dialog = "dialogsort";
        getFirebaseData(day_start, day_end, "sort", sortConsignee);
    }


    public void putMessage(String msg, String etc, String nick) {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String timeStamp1 = new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        WorkingMessageList messageList = new WorkingMessageList();

        messageList.setNickName(nick);
        messageList.setTime(timeStamp1);
        messageList.setMsg(msg);
        messageList.setDate(date);
        messageList.setConsignee(etc);
        messageList.setInOutCargo("Etc");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("WorkingMessage" + "/" + nick + "_" + date + "_" + timeStamp);
        databaseReference.setValue(messageList);
        PublicMethod publicMethod = new PublicMethod(this);
        publicMethod.sendPushMessage(deptName, nickName, msg, "WorkingMessage");


    }

    @SuppressLint("NotifyDataSetChanged")
    public void sortDialog(String startDay, String endDay, ArrayList<Fine2IncargoList> listItems) {
        arrList.clear();
        arrConsignee.clear();
        int listSize = listItems.size();
        String consigneeName;
        for (int i = 0; i < listSize; i++) {
            consigneeName = listItems.get(i).getConsignee();
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
            for (int j = 0; j < listItems.size(); j++) {
                getConsigneeName = listItems.get(j).getConsignee();
                if (consigneeName1.equals(getConsigneeName)) {
                    try{
                        cont40 = cont40 + Integer.parseInt(listItems.get(j).getContainer40());
                        cont20 = cont20 + Integer.parseInt(listItems.get(j).getContainer20());
                        cargo = cargo + Integer.parseInt(listItems.get(j).getLclcargo());
                        qty = qty + Integer.parseInt(listItems.get(j).getIncargo());
                    }catch(Exception e){
                        Log.i("Exception Incargo KeyValue:::",listItems.get(j).getKeyValue());
                    }


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

        consignee_list = consigneeArrayList.toArray(new String[consigneeArrayList.size()]);

        consigneeArrayList.add(0, "ALL");
        shared_consigneeList = consigneeArrayList.toArray(new String[consigneeArrayList.size()]);

        Button searchDetail = view.findViewById(R.id.btnUntilSearch);
        searchDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortCargoDateDialog();
            }
        });

        Button searchDate=view.findViewById(R.id.btnDateSearch);

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
        searchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
                DatePicker datePicker=new DatePicker(Incargo.this);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                        String month,day;
                        if(i1+1<10){
                            month="-0"+(i1+1)+"-";
                        }else{
                            month="-"+(i1+1)+"-";
                        }
                        if(i2<10){
                            day="0"+i2;
                        }else{
                            day=String.valueOf(i2);
                        }
                        String date=i+month+day;
                        day_start=date;
                        day_end=day_start;
                        Toast.makeText(Incargo.this,date+" 지정 검색",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setTitle("특정일 검색창")
                        .setView(datePicker)
                        .setPositiveButton("검색", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getFirebaseData(day_start, day_end, "sort","ALL");
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        searchDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sortCargoDateDialog();

            }
        });
        searchDetail.setOnLongClickListener(v -> {

            return true;
        });
        Button btnThisMonth = view.findViewById(R.id.btnThisMonth);
        btnThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFirebaseDataSortDate("ThisMonth");

            }
        });
        Button btnNextWeek = view.findViewById(R.id.btnNextWeek);
        btnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFirebaseDataSortDate("NextWeek");

            }
        });
        Button btnThisWeek = view.findViewById(R.id.btnThisWeek);
        btnThisWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFirebaseDataSortDate("ThisWeek");

            }
        });

        Button btnTomorrow = view.findViewById(R.id.btnTomorrow);
        btnTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFirebaseDataSortDate("Tomorrow");

            }
        });

    }


    private void sortCargoDateDialog() {
        View view = getLayoutInflater().inflate(R.layout.update_datepicker_spinner, null);
        DatePicker datePickerDefault = view.findViewById(R.id.udatepicker_default);
        Button btnStart = view.findViewById(R.id.uBtnSearchDate_start);
        Button btnEnd = view.findViewById(R.id.uBtnSearchDate_end);
        Calendar calendar = Calendar.getInstance();
        TextView txtUntil = view.findViewById(R.id.txtUntil);
        Button btnSearch=view.findViewById(R.id.ubtnSearchDate);


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
//                if (!dateSelectCondition.equals("until")) {
//                    btnStart.setText(pickDate[0]);
//                    btnEnd.setText(pickDate[0]);
//                }
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
//        txtUntil.setOnClickListener(v -> {
//            if (mSelectedItems.get(0, false)) {
//                dateSelectCondition = "date";
//                mSelectedItems.put(0, false);
//                txtUntil.setText("=");
//            } else {
//                dateSelectCondition = "until";
//                mSelectedItems.put(0, true);
//                txtUntil.setText("~");
//            }
//
//        });

        btnStart.setOnClickListener(v -> {
            if(pickDate[0]==null){
                pickDate[0]=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            }
            btnStart.setText(pickDate[0]);
            Toast.makeText(this, "검색 시작일 을 " + pickDate[0] + " 일로 설정", Toast.LENGTH_SHORT).show();

        });
        btnStart.setOnLongClickListener(v -> {
            btnStart.setText(dateBasic);
            Toast.makeText(this, "검색 시작일 을 당일(" + dateBasic + " )일로 설정", Toast.LENGTH_SHORT).show();
            return true;
        });
        btnEnd.setOnClickListener(v -> {
            if(pickDate[0]==null){
                pickDate[0]=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            }
            btnEnd.setText(pickDate[0]);
            Toast.makeText(this, "검색 종료일 을 " + pickDate[0] + " 일로 설정", Toast.LENGTH_SHORT).show();
        });
        btnEnd.setOnLongClickListener(v -> {
            btnEnd.setText(dateBasic);
            Toast.makeText(this, "검색 종료일 을 당일(" + dateBasic + " )일로 설정", Toast.LENGTH_SHORT).show();
            return true;
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
                getFirebaseData(day_start, day_end, "sort","ALL");
                dialog.dismiss();
            }
        });

    }


    private void getFirebaseData(String startDay, String endDay, String sortKey, String consigneeName) {

        consigneeArrayList.clear();
        listItems.clear();
        String year1=new SimpleDateFormat("yyyy-").format(new Date());
        SimpleDateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dt_start=null,dt_end=null;
        try {
            dt_start=dateFormat.parse(startDay);
            dt_end = dateFormat.parse(endDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffSec =(dt_end.getTime()-dt_start.getTime())/1000;
        long diffDay = diffSec/(24*60*60);
        Log.i("TestValue","Different Day Value:::"+diffDay);

        int startDayRe = Integer.parseInt(startDay.replace("-", ""));
        int endDayRe = Integer.parseInt(endDay.replace("-", ""));
        String year_s = startDay.substring(0,4);
        String month_s = startDay.substring(5,7);
        String day_s = startDay.substring(8,10);
        DatabaseReference databaseReference;
        Calendar calS= Calendar.getInstance();
        Calendar calE = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        for(int i=0;i<=diffDay;i++){
            cal.setTime(dt_start);
            cal.add(Calendar.DATE,i);
            String date = dateFormat.format(cal.getTime());
            Log.i("TestValue","Test Date Value::;"+date);
            String year = date.substring(0,4);
            String month = date.substring(5,7);
            String day = date.substring(8,10);
            databaseReference = database.getReference("DeptName/"+deptName+"/InCargo/"+month+"월/"+date+"/");
            int finalI = i;
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data: snapshot.getChildren()){
                        if(!data.getKey().equals("json 등록시 덥어쓰기 바랍니다")){
                            try {
                                Fine2IncargoList mList = data.getValue(Fine2IncargoList.class);
                                switch(sortKey){
                                    case("all"):
                                        if(consigneeName.equals("ALL")){
                                            listItems.add(mList);
                                        }else{
                                            if(consigneeName.equals(mList.getConsignee())){
                                                listItems.add(mList);
                                            }
                                        }
                                        break;
                                    case("sort"):
                                        if(!mList.getContainer20().equals("0")||!mList.getContainer40().equals("0")||!mList.getLclcargo().equals("0")){
                                            if(consigneeName.equals("ALL")){
                                                listItems.add(mList);
                                            }else{
                                                if(consigneeName.equals(mList.getConsignee())){
                                                    listItems.add(mList);
                                                }
                                            }
                                        }

                                }


                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Server Data Occured Error .Please Check Server Data", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (finalI ==diffDay){
                            adapter.notifyDataSetChanged();
                            sortDialog(startDay, endDay, listItems);
                            String date=null;
                            if (startDay.equals(endDay)) {
                                date = startDay;
                            } else {
                                date = startDay + "~" + endDay;
                            }
                            incargo_contents_date.setText(date);
                        }
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
//        if (startDay.equals(endDay)){
//            Log.i("TestValue","Trans Method Successed");
////            databaseReference =
////                    database.getReference("DeptName/" + deptName + "/" + "InCargo" + "/" + month_s + "월/" +startDay + "/");
////            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////                @SuppressLint("NotifyDataSetChanged")
////                @Override
////                public void onDataChange(@NonNull DataSnapshot snapshot) {
////                    for (DataSnapshot data : snapshot.getChildren()) {
////                        if (!data.getKey().equals("json 등록시 덥어쓰기 바랍니다")) {
////                            try {
////                            Fine2IncargoList mList = data.getValue(Fine2IncargoList.class);
////                            switch(sortKey){
////                                case("all"):
////                                    if(consigneeName.equals("ALL")){
////                                        listItems.add(mList);
////                                    }else{
////                                        if(consigneeName.equals(mList.getConsignee())){
////                                            listItems.add(mList);
////                                        }
////                                    }
////                                    break;
////                                case("sort"):
////                                    if(!mList.getContainer20().equals("0")||!mList.getContainer40().equals("0")||!mList.getLclcargo().equals("0")){
////                                        if(consigneeName.equals("ALL")){
////                                            listItems.add(mList);
////                                        }else{
////                                            if(consigneeName.equals(mList.getConsignee())){
////                                                listItems.add(mList);
////                                            }
////                                        }
////                                    }
////
////                            }
////
////
////                            } catch (Exception e) {
////                                Toast.makeText(getApplicationContext(), "Server Data Occured Error .Please Check Server Data", Toast.LENGTH_SHORT).show();
////                            }
////
////                        }
////                    }
////
////                    adapter.notifyDataSetChanged();
////                    sortDialog(startDay, endDay, listItems);
////                    String date=null;
////                    if (startDay.equals(endDay)) {
////                        date = startDay;
////                    } else {
////                        date = startDay + "~" + endDay;
////                    }
////                    incargo_contents_date.setText(date);
////                }
////                @Override
////                public void onCancelled(@NonNull DatabaseError error) {
////
////                }
////            });
//
//        }else {
//            for (int i = 1; i <= 12; i++) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(2022, i - 1, 1);
//                int monthOfLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//
//                for (int j = 1; j <= monthOfLastDay; j++) {
//                    String date = null;
//                    String month = null;
//                    if (i < 10) {
//                        month = "0" + i;
//                    } else {
//                        month = String.valueOf(i);
//                    }
//                    if (j < 10) {
//                        date = "0" + j;
//                    } else {
//                        date = String.valueOf(j);
//                    }
//
//                    databaseReference =
//                            database.getReference("DeptName/" + deptName + "/" + "InCargo" + "/" + month + "월/" + year1 + month +
//                                    "-" + date + "/");
//
//                    String finalMonth = month;
//                    String finalDate = date;
//
//                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @SuppressLint("NotifyDataSetChanged")
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Map<String, Object> value = new HashMap<>();
//
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                String keyValue = data.getKey();
//                                if (keyValue.equals("null")) {
//                                    Map<String, Object> nullValue = new HashMap<>();
//                                    nullValue.put("null", null);
//                                    DatabaseReference databaseReference = database.getReference("DeptName/" + deptName + "/" +
//                                            "InCargo" + "/" + finalMonth + "월/" + year1 + finalMonth +
//                                            "-" + finalDate);
//                                    databaseReference.updateChildren(nullValue);
//                                }
//                                if (!keyValue.equals("json 등록시 덥어쓰기 바랍니다")) {
//                                    try {
//                                        Fine2IncargoList mList = data.getValue(Fine2IncargoList.class);
//                                        Log.i("TestValue", "Key Value:::" + mList.getKeyValue());
//                                        if (mList.getKeyValue() == null) {
//                                            value.put("keyValue", keyValue);
//                                            DatabaseReference databaseReference = database.getReference("DeptName/" + deptName + "/" +
//                                                    "InCargo" + "/" + finalMonth + "월/" + year1 + finalMonth +
//                                                    "-" + finalDate + "/" + keyValue);
//                                            databaseReference.updateChildren(value);
//                                        }
//
//                                        if (mList.getDate() == null) {
//                                            Log.i("TestValue", "Get date Value npe=" + mList.getKeyValue());
//                                        }
//                                        int dayReList = Integer.parseInt(mList.getDate().replace("-", ""));
//
//                                        if (!consigneeArrayList.contains(mList.getConsignee())) {
//                                            consigneeArrayList.add(mList.getConsignee());
//                                        }
//                                        if (dayReList >= startDayRe && dayReList <= endDayRe) {
//                                            switch (sortKey) {
//                                                case "all":
//                                                    if (consigneeName.equals("ALL")) {
//                                                        listItems.add(mList);
//                                                    } else {
//                                                        if (consigneeName.equals(mList.getConsignee())) {
//                                                            listItems.add(mList);
//                                                        }
//                                                    }
//                                                    break;
//                                                case "sort":
//                                                    if (!mList.getContainer20().equals("0") || !mList.getContainer40().equals("0") || !mList.getLclcargo().equals("0")) {
//                                                        if (consigneeName.equals("ALL")) {
//
//                                                            listItems.add(mList);
//                                                        } else {
//                                                            if (consigneeName.equals(mList.getConsignee())) {
//                                                                listItems.add(mList);
//                                                            }
//                                                        }
//                                                    }
//                                            }
//
//                                        }
//
//                                    } catch (Exception e) {
//                                        Log.i("TestValue", "Exception Value Occured");
//                                    }
//                                }
//
//
//                            }
//                            if (finalMonth.equals("12") && finalDate.equals("31")) {
//
//                                adapter.notifyDataSetChanged();
//                                sortDialog(startDay, endDay, listItems);
//                                String date;
//                                if (startDay.equals(endDay)) {
//                                    date = startDay;
//                                } else {
//                                    date = startDay + "~" + endDay;
//                                }
//                                incargo_contents_date.setText(date);
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//
//
//            }
//        }

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
            if(listIn!=null){
                listItems =listIn;
            }
            String consigneeNameValue=listItems.get(pos).getConsignee();
            keyValue=listItems.get(pos).getKeyValue();
            String containerValue=listItems.get(pos).getContainer();
            String blValue=listItems.get(pos).getBl();
            String dateValue=listItems.get(pos).getDate();
            initLayout(consigneeNameValue,keyValue,containerValue,blValue,dateValue);
            publicMethod.getRemarkValue(blValue);






    }

    private void initLayout(String consigneeNameValue, String keyValue, String containerValue, String blValue, String dateValue) {

        itemClickTitle=
                consigneeNameValue + "_비엘:" + blValue + "_컨테이너:" + containerValue;
        selectedItemGetDatabase(dateValue, keyValue);

//        pickedUpItemClickDialog(keyValue, updateTitleValue, consigneeName);
        itemLayout.setVisibility(View.VISIBLE);
        if(fltBtn_share.getVisibility()==View.INVISIBLE){
            fltBtn_share.setVisibility(View.VISIBLE);
        }

        FirebaseDatabase itemClickDatabase=FirebaseDatabase.getInstance();
        String refPath=
                "DeptName/" + deptName + "/" + "InCargo" + "/" + keyValue.substring(5, 7) + "월/" + keyValue.substring(0,10) +
                        "/" + keyValue;
        DatabaseReference itemClickReference=itemClickDatabase.getReference(refPath);
        Map<String,Object> itemClickMap=new HashMap<>();
        btnConIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickMap.put("working","컨테이너 진입");
                itemClickReference.updateChildren(itemClickMap);
                Toast.makeText(getApplicationContext(),itemClickTitle+"\n"+"화물 컨테이너 진입으로 서버 등록 되었습니다.",Toast.LENGTH_LONG).show();
                initIntent();
            }
        });

        btnDevCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
                builder.setTitle("입고작업 완료+컨테이너 회수요청 선택창")
                        .setMessage(itemClickTitle+" 화물정보"+"\n" +" 에 대한 컨테이너 회수가 필요하면 하단 컨테이너 회수 요청 버튼으로 알림등록 바랍니다.")
                        .setPositiveButton("입고작업 완료+컨테이너 회수요청 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickMap.put("working","입고작업 완료");
                                itemClickReference.updateChildren(itemClickMap);
                                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + " 입고작업 완료 + 컨테이너 회수 요청",
                                        consigneeNameValue,
                                        "InCargo", deptName);

                                initIntent();
                            }
                        })
                        .setNegativeButton("입고작업 완료 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickMap.put("working","입고작업 완료");
                                itemClickReference.updateChildren(itemClickMap);

                                Toast.makeText(getApplicationContext(),itemClickTitle+"\n"+" 입고작업 완료  알림 등록 되었습니다.",
                                        Toast.LENGTH_LONG).show();
                                initIntent();
                            }
                        })
                        .setNeutralButton("컨테이너 회수요청 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + " 컨테이너 회수요청",
                                        consigneeNameValue,
                                        "InCargo", deptName);

                                initIntent();
                            }
                        })
                        .show();
            }
        });
        btnInsCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
                builder.setTitle("검수완료+반입요청 확인 창")
                        .setMessage(itemClickTitle+" 화물정보"+"\n" +"검수내용과 일치하면 하단 반입 요청 버튼 클릭하여 알림 등록 바랍니다.")
                        .setPositiveButton("검수 완료+반입요청 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickMap.put("working","검수완료");
                                itemClickReference.updateChildren(itemClickMap);
                                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + "검수 완료+반입요청 등록", consigneeNameValue,
                                        "InCargo", deptName);

                                initIntent();
                            }
                        })
                        .setNegativeButton("검수완료 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickMap.put("working","검수완료");
                                itemClickReference.updateChildren(itemClickMap);

                                Toast.makeText(getApplicationContext(),itemClickTitle+"\n"+"검수완료 등록 되었습니다.",
                                        Toast.LENGTH_SHORT).show();
                                initIntent();
                            }
                        })
                        .setNeutralButton("반입요청 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + "재고반입 등록 요청", consigneeNameValue,
                                        "InCargo", deptName);

                                initIntent();
                            }
                        })
                        .show();
            }
        });

        btnIncargoCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String consigneeName=listItems.get(0).getConsignee();
                String des=listItems.get(0).getDescription();
                String bl=listItems.get(0).getBl();
                itemClickMap.put("working","창고반입");
                itemClickReference.updateChildren(itemClickMap);
                Toast.makeText(getApplicationContext(),itemClickTitle+"\n"+"창고반입 으로 서버 등록 되었습니다.",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
                builder.setTitle("로케이션 등록 확인창")
                        .setMessage("화주명:"+consigneeName+"\n"+"품명:"+des+"\n"+"비엘:"+bl+"\n"+"화물에 대한 Location 등록을 진행할려면 하단 " +
                                "Location 등록 버튼을 클릭 바랍니다.")
                        .setPositiveButton("Location 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String keyValue=listItems.get(0).getKeyValue();
                                String date=listItems.get(0).getDate();
                                String month=date.substring(5,7)+"월";
                                String refPath="DeptName/"+deptName+"/InCargo/"+month+"/"+date+"/"+keyValue;

                                Intent intent=new Intent(Incargo.this,Location.class);
                                Fine2IncargoList setList=listItems.get(0);
                                intent.putExtra("list",setList);
                                intent.putExtra("refPath",refPath);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                initIntent();
                            }
                        })
                        .show();

            }
        });

        btnIncargoCom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        });
        btnRegPallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedValue="Pallet";
                String bl=listItems.get(0).getBl();
                String des=listItems.get(0).getDescription();
                String consigneeName=listItems.get(0).getConsignee();
                AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
                builder.setTitle("팔렛트 등록 확인창")
                        .setMessage("사용 등록:" + "\n" + consigneeName+"_"+bl+"_"+des+ "\n"+"화물에 대한 사용등록"+"\n" + "입고 등록:" + "\n" + "화주별,팔렛트별 팔렛트 신규 입고등록")
                        .setPositiveButton("사용 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                publicMethod.pltReg(consigneeNameValue,nickName,0,bl,des);
                            }
                        })
                        .setNeutralButton("입고 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                manualPltReg();
                            }
                        })
                        .show();

            }
        });

        btnNewIncargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + " 신규 등록", consigneeNameValue,
                        "InCargo", deptName);
                initIntent();
            }
        });

        pickedUpItemClick("Re",dateToday);
    }

    private void selectedItemGetDatabase(String date, String keyValue) {
        listItems.clear();
        String dateValue=date.substring(5,7);
        databaseReference =
                database.getReference("DeptName/" + deptName + "/" + "InCargo" + "/" + dateValue + "월/" + date);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (!data.getKey().equals("json 등록시 덥어쓰기 바랍니다")) {
                        Fine2IncargoList mList = data.getValue(Fine2IncargoList.class);
                        if (Objects.equals(data.getKey(), keyValue)) {
                            listItems.add(mList);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



    @Override
    public void onLongItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
        String dialogTitle = listItems.get(pos).getConsignee() + "_" + listItems.get(pos).getDescription();
        selectLongClickDialog(dialogTitle, pos);

    }

    private void selectLongClickDialog(String dialogTitle, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("입고화물 정보 수정")
                .setMessage(dialogTitle + " 화물정보에 대한 수정,삭제 진행을 합니다." + "\n" + "화물정보 삭제시에는 하단 삭제 버튼 클릭" + "\n" + "화물정보 수정,또는 " +
                        "화물조회시에는" +
                        " " +
                        "하단 " +
                        "정보수정 버튼 " +
                        "클릭후 메뉴 중간 버튼 " +
                        "클릭후 내용 수정 진행 바랍니다.")
                .setPositiveButton("정보 수정,화물조회", new DialogInterface.OnClickListener() {
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
                        String incargoCount = listItems.get(pos).getCount();


                        upDataRegList = new String[]{incargoWorking, incargoDate, incargoConsigneeName, incargoDescription,
                                incargoContainer20, incargoContainer40, incargoCargo,
                                incargoContainerNumber, incargoQty, incargoBl, incargoRemark, incargoCount};

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
                                String keyValue = listItems.get(pos).getKeyValue();
                                DatabaseReference databaseReference = database.getReference("DeptName/" + deptName + "/" +
                                        "InCargo" + "/" + keyValue.substring(5, 7) + "월/" + keyValue.substring(0, 10));
                                childUpdates.put(listItems.get(pos).getKeyValue() + "/", null);
                                databaseReference.updateChildren(childUpdates);

                                publicMethod = new PublicMethod(Incargo.this);
                                publicMethod.putNewDataUpdateAlarm(nickName, msgWorking, deConsignee,
                                        "InCargo", deptName);
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
       btnPicCount.setText(clickedPictureCount);
       btnPicCount.setTextColor(Color.RED);
        Animation ani=new AlphaAnimation(0.0f,1.0f);
        ani.setRepeatMode(Animation.REVERSE);
        ani.setDuration(1000);
        ani.setRepeatCount(Animation.INFINITE);
        btnPicCount.startAnimation(ani);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void pickedUpItemClick(String sort, String date) {

        switch(sort){
            case "Re":
                btnPicList.setText("서버전송용 조정 사진");
                break;
            case "All":
                btnPicList.setText("갤러리 전체사진");
                break;
            case "Ori":
                btnPicList.setText("업무용 원본사진");
                break;

        }

        int imageViewListCount;
        SharedPreferences sharedPreferences=getSharedPreferences("Dept_Name", Context.MODE_PRIVATE);
        imageViewListCount=Integer.parseInt(sharedPreferences.getString("imageViewListCount","3"));
        GridLayoutManager manager = new GridLayoutManager(this, imageViewListCount);

        imageRecyclerView.setLayoutManager(manager);
        PublicMethod pictures = new PublicMethod(this);
        if(sort.equals("Re")||sort.equals("All")||sort.equals("Ori")){
            imageViewLists=pictures.getPictureLists(sort,date);
        }else{
            itemPictureList(sort);
        }


        iAdapter = new ImageViewActivityAdapter(imageViewLists, this);
        imageRecyclerView.setAdapter(iAdapter);
        iAdapter.notifyDataSetChanged();
    }

    public void initIntent() {
        Intent intent = new Intent(Incargo.this, Incargo.class);
        intent.putExtra("refPath",keyValue);
        intent.putExtra("date",listItems.get(0).getDate());
        intent.putExtra("consigneeName",listItems.get(0).getConsignee());
        intent.putExtra("bl",listItems.get(0).getBl());
        intent.putExtra("container",listItems.get(0).getContainer());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        publicMethod = new PublicMethod(this);
        publicMethod.intentSelect();
    }

    @Override
    public int compare(Fine2IncargoList a, Fine2IncargoList b) {
        int compare = 0;
        compare = a.working.compareTo(b.working);
        return 0;
    }

    public void itemPictureList(String keyValue) {
        if(btnPicList.getVisibility()==View.INVISIBLE){
            btnPicList.setVisibility(View.VISIBLE);
            btnPicCount.setVisibility(View.VISIBLE);
        }
        btnPicList.setText("서버 저장된 사진 검색");
        btnPicCount.setText("디바이스 저장 원하면 사진 클릭");
        btnPicCount.setTextColor(Color.RED);
        Animation ani=new AlphaAnimation(0.0f,1.0f);
        ani.setRepeatCount(Animation.INFINITE);
        ani.setRepeatMode(Animation.REVERSE);
        ani.setDuration(1000);
        btnPicCount.startAnimation(ani);
        imageViewLists.clear();
        RecyclerView imageRecyclerView = findViewById(R.id.incargo_recyclerView_image);
        int imageViewListCount;

        SharedPreferences sharedPreferences=getSharedPreferences("Dept_Name", Context.MODE_PRIVATE);
        imageViewListCount=Integer.parseInt(sharedPreferences.getString("imageViewListCount","3"));
        GridLayoutManager manager = new GridLayoutManager(this, imageViewListCount);
        imageRecyclerView.setLayoutManager(manager);
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
        StorageReference storageReference =
                storage.getReference("images/" + deptName + "/" + keyValue.substring(0, 10) + "/InCargo/" + keyValue);
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            imageViewLists.add(uri.toString());
                            ImageViewActivityAdapter iAdapter = new ImageViewActivityAdapter(imageViewLists, Incargo.this);
                            if (imageViewLists.size() == listResult.getItems().size()) {
                                imageRecyclerView.setAdapter(iAdapter);
                                iAdapter.notifyDataSetChanged();
                            }
                            iAdapter.clickListener = new ImageViewActivityAdapter.ImageViewClicked() {
                                @Override
                                public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
                                    PublicMethod publicMethod = new PublicMethod(Incargo.this);
                                    publicMethod.adapterPictureSavedMethod(imageViewLists.get(position));
                                }
                            };

                        }

                    });

                }

            }
        });

    }

    public void manualPltReg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Spinner spinner = new Spinner(this);
        PublicMethod publicMethod = new PublicMethod(this);
        final String[] consigneeName = new String[1];
        SharedPreferences sharedPreferences=getSharedPreferences("Dept_Name",Context.MODE_PRIVATE);

        String strConsignee= sharedPreferences.getString("consigneeList",null);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                publicMethod.extractChar(strConsignee,','));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                consigneeName[0] = publicMethod.extractChar(strConsignee,',').get(position);
                Toast.makeText(Incargo.this, consigneeName[0] + " 으로 업체 선택 하였습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setTitle("팔렛트 입고 화주선택 창")
                .setView(spinner)
                .setMessage("입고 팔렛트 업체명 확인후 " + "\n" + "하단 등록버튼 클릭하면 세부등록창으로 전환 됩니다.")
                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPalletStock(consigneeName[0],nickName,"");

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();


    }

    private void getPalletStock(String consigneeName, String bl, String des) {
        String[] pltSlist = {"KPP", "AJ", "ETC"};
        final int[] kppQty = new int[1];
        final int[] ajQty = new int[1];
        final int[] etcQty = new int[1];
        for (String s : pltSlist) {
            DatabaseReference pltRef = FirebaseDatabase.getInstance().getReference("DeptName/" + deptName +
                    "/PltManagement/" + consigneeName + "/" + s);
            pltRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        ActivityPalletList mList = data.getValue(ActivityPalletList.class);
                        switch (s) {
                            case "KPP":
                                kppQty[0] = mList.getStockQty();
                                break;
                            case "AJ":
                                ajQty[0] = mList.getStockQty();
                                break;
                            case "ETC":
                                assert mList != null;
                                etcQty[0] = mList.getStockQty();
                        }
                    }
                    if(s.equals("ETC")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(Incargo.this);
                        builder.setTitle(consigneeName+"_입고 등록전 재고 확인")
                                .setMessage("KPP재고:" + kppQty[0] + "\n" + "AJ재고:" + ajQty[0] + "\n" + "ETC재고:" + etcQty[0])
                                .setPositiveButton("재고 확인후 등록", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PublicMethod publicMethod=new PublicMethod(Incargo.this,imageViewListsSelected);
                                        publicMethod.pltReg(consigneeName, nickName,0, bl,des);


                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }


    }
    public synchronized void synUpLoadPictures(){
//        publicMethod.upLoadPictures(nickName, listItems.get(0).getConsignee(), "InCargo", listItems.get(0).getKeyValue(),
//                deptName);
        String consigneeName=listItems.get(0).getConsignee();
        String inoutCargo="InCargo";
        ArrayList<String> uriList=new ArrayList<>();
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateNtime=new SimpleDateFormat("yyyy년MM월dd일HH시mm분ss초").format(new Date());
        String refPath;
        FirebaseStorage storage=FirebaseStorage.getInstance(("gs://fine-bondedwarehouse.appspot.com"));
        for(int i =0;i< imageViewListsSelected.size();i++){
            Uri uriValue=Uri.fromFile(new File(imageViewLists.get(i)));
                refPath=deptName+"/"+date+"/InCargo/"+listItems.get(0).getKeyValue()+"/"+nickName+System.currentTimeMillis()+".jpg";
                StorageReference storageReference=storage.getReference("images/"+refPath);
            int finalI = i;
            storageReference.putFile(uriValue)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                       uriList.add(String.valueOf(uri));
                                       WorkingMessageList messageList=new WorkingMessageList();

                                        if((uriList.size())==imageViewListsSelected.size()){
                                            messageList.setConsignee(consigneeName);
                                            messageList.setNickName(nickName);
                                            messageList.setTime(dateNtime);
                                            messageList.setDate(date);
                                            messageList.setMsg(consigneeName+"_"+inoutCargo+"_ 사진 업로드");
                                            messageList.setInOutCargo(inoutCargo);
                                            messageList.setKeyValue(keyValue);

                                            try {
                                                switch (finalI) {
                                                    case 0:
                                                        messageList.setUri0(uriList.get(0));
                                                        break;
                                                    case 1:
                                                        messageList.setUri0(uriList.get(0));
                                                        messageList.setUri1(uriList.get(1));
                                                        break;
                                                    case 2:
                                                        messageList.setUri0(uriList.get(0));
                                                        messageList.setUri1(uriList.get(1));
                                                        messageList.setUri2(uriList.get(2));
                                                        break;
                                                    case 3:
                                                        messageList.setUri0(uriList.get(0));
                                                        messageList.setUri1(uriList.get(1));
                                                        messageList.setUri2(uriList.get(2));
                                                        messageList.setUri3(uriList.get(3));
                                                        break;
                                                    case 4:
                                                        messageList.setUri0(uriList.get(0));
                                                        messageList.setUri1(uriList.get(1));
                                                        messageList.setUri2(uriList.get(2));
                                                        messageList.setUri3(uriList.get(3));
                                                        messageList.setUri4(uriList.get(4));
                                                        break;
                                                    case 5:
                                                        messageList.setUri0(uriList.get(0));
                                                        messageList.setUri1(uriList.get(1));
                                                        messageList.setUri2(uriList.get(2));
                                                        messageList.setUri3(uriList.get(3));
                                                        messageList.setUri4(uriList.get(4));
                                                        messageList.setUri5(uriList.get(5));
                                                    case 6:
                                                        messageList.setUri0(uriList.get(0));
                                                        messageList.setUri1(uriList.get(1));
                                                        messageList.setUri2(uriList.get(2));
                                                        messageList.setUri3(uriList.get(3));
                                                        messageList.setUri4(uriList.get(4));
                                                        messageList.setUri5(uriList.get(5));
                                                        messageList.setUri6(uriList.get(6));
                                                        break;
                                                }
                                            }catch(IndexOutOfBoundsException e){
                                                messageList.setMsg(e.toString());
                                            }

                                            FirebaseDatabase database=FirebaseDatabase.getInstance();
                                            DatabaseReference databaseReference=
                                                    database.getReference("DeptName/"+deptName +"/WorkingMessage/"+nickName+"_"+dateNtime   );
                                            databaseReference.setValue(messageList);
                                            PublicMethod publicMethod=new PublicMethod(Incargo.this);
                                            publicMethod.sendPushMessage(deptName,nickName,listItems.get(0).getConsignee()+"_InCargo_ 사진 업로드","CameraUpLoad");

                                    }}
                                });
                            }
                        });

        }
    }

    @Override
    public void onItemConsigneeClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
        AlertDialog.Builder builder=new AlertDialog.Builder(Incargo.this);
        String consigneeName=listItems.get(pos).getConsignee();
        builder.setTitle("조건 검색창")
                .setMessage("검색 시작일: " + day_start + "\n" + "검색 종료일: " + day_end + "\n" + "검색 화주명: " + consigneeName)
                .setPositiveButton("전화물 검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            getFirebaseData(day_start,day_end,"all",consigneeName);
                    }
                })
                .setNeutralButton("컨테이너 단위검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getFirebaseData(day_start,day_end,"sort",consigneeName);
                    }
                })
                .setNegativeButton("검색 취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

    }





}




