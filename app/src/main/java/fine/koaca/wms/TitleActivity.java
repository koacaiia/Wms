package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TitleActivity extends AppCompatActivity implements OutCargoListAdapter.OutCargoListAdapterClickListener ,
        OutCargoListAdapter.OutCargoListAdapterLongClickListener,
        Serializable, SensorEventListener,IncargoListAdapter.AdapterClickListener,IncargoListAdapter.AdapterLongClickListener ,
        Comparator<Fine2IncargoList>,IncargoListAdapter.ItemConsigneeClickListener {

    public static ArrayList<Fine2IncargoList> list;
    RecyclerView recyclerViewIn;
    RecyclerView recyclerViewOut;
    FirebaseDatabase database;

    ArrayList<OutCargoList> listOut = new ArrayList<>();
    ArrayList<Fine2IncargoList> listIn = new ArrayList<>();
    OutCargoListAdapter adapterOut;
    IncargoListAdapter adapterIn;
    String dateToday,refMonth;

    Button btnTitle;

    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    String deptName, nickName,imageViewListCount;

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    private long mShakeTime;
    private static final int SHAKE_SKIP_TIME = 500;
    private static final float SHAKE_THERESHOLD_GRAVITY = 2.7F;

    String[] permission_list = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.USE_FULL_SCREEN_INTENT,
            Manifest.permission.ANSWER_PHONE_CALLS,
    };

    static RequestQueue requestQueue;
    Display display;

    String alertVersion;
    TextView txtTitle;
    ArrayList<String> arrAnnualLeaveStaff = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);



//        Intent intent=new Intent(this,WorkingMessageData.class);
//        startActivity(intent);

        /*LOG Test
        Display display=getWindowManager().getDefaultDisplay();
        int width=display.getWidth();
        Log.i("TestValue","Display width:::"+width);*/
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        dateToday = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//        dateToday="2021-10-27";
        requestPermissions(permission_list, 0);
        sharedPref = getSharedPreferences("Dept_Name", MODE_PRIVATE);
        if (sharedPref.getString("deptName", null) == null) {
            PublicMethod publicMethod = new PublicMethod(this);
            publicMethod.checkUserInfo();
            return;
        }


        database = FirebaseDatabase.getInstance();


        deptName = sharedPref.getString("deptName", null);
        nickName = sharedPref.getString("nickName", null);
        imageViewListCount=sharedPref.getString("imageViewListCount","3");
        refMonth=dateToday.substring(5,7);

        if(sharedPref.getString("consigneeList",null)==null){
            DatabaseReference conRef=database.getReference("DeptName/"+deptName+"/BaseRef");
            conRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data:snapshot.getChildren()){
                        if(data.getKey().equals("consigneeRef")){
                            String consigneeValue= (String) data.getValue();
                            editor = sharedPref.edit();
                            editor.putString("consigneeList",consigneeValue);
                            editor.apply();
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        btnTitle = findViewById(R.id.activity_title_btn);

        btnTitle.setText(dateToday + "일  입,출고 현황 (화면초기화)");
        btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initIntent();
            }
        });

        recyclerViewOut = findViewById(R.id.titleRecyclerOut);
        recyclerViewIn = findViewById(R.id.titleRecyclerOutIn);
        LinearLayoutManager outManager = new LinearLayoutManager(this);
        LinearLayoutManager inManager = new LinearLayoutManager(this);
        recyclerViewOut.setLayoutManager(outManager);
        recyclerViewIn.setLayoutManager(inManager);

        titleDialog();

        adapterOut = new OutCargoListAdapter(listOut, this, this, this);
        adapterIn = new IncargoListAdapter(listIn, this, this,this);
        recyclerViewOut.setAdapter(adapterOut);
        recyclerViewIn.setAdapter(adapterIn);


        getVersion();
        if (nickName.equals("Test")) {
            FirebaseMessaging.getInstance().subscribeToTopic("Test1");
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(deptName);
        }


        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }



    }

    private void annualLeaveStaffCheck() {

    }

    private void initIntent() {
        Intent intent = new Intent(this, TitleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void inCargoIntent() {
        Intent intent = new Intent(this, Incargo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("depotName", deptName);
        intent.putExtra("nickName", nickName);
        startActivity(intent);
    }

    private void intentOutcargoActivity() {
        Intent intent = new Intent(this, OutCargoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("deptName", deptName);
        intent.putExtra("nickName", nickName);
        intent.putExtra("listOut", listOut);

        startActivity(intent);
    }  private void workingStaffActivity() {
        Intent intent = new Intent(this, ActivityWorkingStaff.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("deptName", deptName);
        intent.putExtra("nickName", nickName);
        intent.putExtra("listOut", listOut);

        startActivity(intent);
    }


    public void titleDialog() {

        AlertDialog.Builder titleBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_title, null);
        CardView cardViewIncargo=view.findViewById(R.id.title_cardview_incargo);
        CardView cardViewOutcargo=view.findViewById(R.id.title_cardview_outcargo);
        CardView cardViewWorkStaff=view.findViewById(R.id.title_cardview_workstaff);
        txtTitle = view.findViewById(R.id.dialog_title_txttile);

        txtTitle.setText(dateToday + " 입,출고 현황");
        String yearPath=new SimpleDateFormat("yyyy년").format(new Date());
        DatabaseReference databaseReferenceAnnual= database.getReference("AnnualData/"+yearPath);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String date = new SimpleDateFormat("MM월dd일").format(new Date());
                for (DataSnapshot data : snapshot.getChildren()) {
                    AnnualList list = data.getValue(AnnualList.class);
                    if (list.getHalf().contains(date)) {
                            arrAnnualLeaveStaff.add("반차자:" + list.getName());}
                    if (list.getAnnual().contains(date) ) {
                        arrAnnualLeaveStaff.add("휴가자:" + list.getName());
                    }

                }
                txtTitle.append("\n" + arrAnnualLeaveStaff);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

       databaseReferenceAnnual.addListenerForSingleValueEvent(listener);

        ArrayList<OutCargoList> listOutP = new ArrayList<>();
        ArrayList<OutCargoList> listOutTotal = new ArrayList<>();
        listOut.clear();
        listIn.clear();
        DatabaseReference databaseReferenceOut=
                database.getReference("DeptName/" + deptName + "/" +"OutCargo" + "/" + refMonth + "월/" + dateToday);
        ValueEventListener outListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    OutCargoList mList = data.getValue(OutCargoList.class);
                    listOutTotal.add(mList);
                }

                int intTotalPlt = 0, intTotalEa = 0, intProgressPlt = 0, intProgressEa = 0;

                int listOutSize = listOutTotal.size();
                for (int i = 0; i < listOutSize; i++) {
                    String totalQty = listOutTotal.get(i).getTotalQty();
                    String workProcess = listOutTotal.get(i).getWorkprocess();

                    if (workProcess.equals("완")) {
                        listOutP.add(listOutTotal.get(i));
                        if (totalQty.contains("PLT")) {
                            intProgressPlt = intProgressPlt + Integer.parseInt(totalQty.substring(0, totalQty.length() - 3));
                        } else {
                            intProgressEa = intProgressEa + Integer.parseInt(totalQty.substring(0, totalQty.length() - 2));
                        }
                    } else {
                        listOut.add(listOutTotal.get(i));
                    }
                    if (totalQty.contains("PLT")) {
                        intTotalPlt = intTotalPlt + Integer.parseInt(totalQty.substring(0, totalQty.length() - 3));
                    } else {
                        intTotalEa = intTotalEa + Integer.parseInt(totalQty.substring(0, totalQty.length() - 2));
                    }

                }
                int pCargo, pPlt, pEa, rCargo, rPlt, rEa;

                pCargo = listOutTotal.size() - listOutP.size();
                pPlt = intTotalPlt - intProgressPlt;
                pEa = intTotalEa - intProgressEa;

                rCargo = (int) (((double) listOutP.size() / (double) listOutTotal.size()) * 100);
                rPlt = (int) (((double) intProgressPlt / (double) intTotalPlt) * 100);
                rEa = (int) (((double) intProgressEa / (double) intTotalEa) * 100);
                TextView totalCargo = view.findViewById(R.id.dialog_titleTotalCargo);
                totalCargo.setText(listOutTotal.size() + "건");
                TextView proCargo = view.findViewById(R.id.dialog_titleProgressOutcargo);
                proCargo.setText(pCargo + "건");
                TextView rateCargo = view.findViewById(R.id.dialog_titleProgressOutcargoRate);
                rateCargo.setText(rCargo + "%");
                if (rCargo == 100) {
                    rateCargo.setTextColor(Color.RED);
                }

                TextView totalPlt = view.findViewById(R.id.dialog_titleTotalCargoPlt);
                totalPlt.setText(intTotalPlt + "PLT");
                TextView proPlt = view.findViewById(R.id.dialog_titleProgressOutcargoPlt);
                proPlt.setText(pPlt + "PLT");
                TextView ratePlt = view.findViewById(R.id.dialog_titleProgressOutcargoPltRate);
                ratePlt.setText(rPlt + "%");
                if (rPlt == 100) {
                    ratePlt.setTextColor(Color.RED);
                }

                TextView totalEa = view.findViewById(R.id.dialog_titleTotalCargoEa);
                totalEa.setText(intTotalEa + "EA");
                TextView proEa = view.findViewById(R.id.dialog_titleProgressOutcargoEA);
                proEa.setText(pEa + "EA");
                TextView rateEa = view.findViewById(R.id.dialog_titleProgressOutcargoEaRate);
                rateEa.setText(rEa + "%");
                if (rEa == 100) {
                    rateEa.setTextColor(Color.RED);
                }

                adapterOut.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        TextView inContainer40 = view.findViewById(R.id.dialog_titleContainer40);
        TextView inContainer20 = view.findViewById(R.id.dialog_titleContainer20);
        TextView inCargo = view.findViewById(R.id.dialog_titleCargo);
        TextView inPlt = view.findViewById(R.id.dialog_titleQty);
        TextView rateIncargoC = view.findViewById(R.id.dialog_titleProgressContainer);
        TextView rateIncargo = view.findViewById(R.id.dialog_titleProgressInCargo);
        TextView rateIncargoI = view.findViewById(R.id.dialog_titleProgressInspection);
        TextView rateIncargoW = view.findViewById(R.id.dialog_titleProgressInWarehouse);

        DatabaseReference databaseReferenceIn=
                database.getReference("DeptName/" + deptName + "/" +"InCargo" + "/" + refMonth + "월/" + dateToday);
        ValueEventListener inListener = new ValueEventListener() {
            int con40, con20, cargo, qty, totalTeu, proIncargo, proIncargoW, proIncargoI, proIncargoC, eCon40, eCon20, eCargo;
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataIn : snapshot.getChildren()) {
                    Fine2IncargoList mListIn = dataIn.getValue(Fine2IncargoList.class);

                    String str40, str20, strCargo;
                    str40 = mListIn.getContainer40();
                    str20 = mListIn.getContainer20();
                    strCargo = mListIn.getLclcargo();
                    if (!str40.equals("0") || !str20.equals("0") || !strCargo.equals("0")) {
                        listIn.add(mListIn);
                    }
                    listIn.sort(new Comparator<Fine2IncargoList>() {
                        @Override
                        public int compare(Fine2IncargoList a, Fine2IncargoList b) {
                            int compare = 0;
                            compare = a.working.compareTo(b.working);

                            return compare;
                        }
                    });

                    eCon40 = Integer.parseInt(str40);
                    eCon20 = Integer.parseInt(str20);
                    eCargo = Integer.parseInt(strCargo);
                    con40 = con40 + eCon40;
                    con20 = con20 + eCon20;
                    cargo = cargo + eCargo;


                    qty = qty + Integer.parseInt(mListIn.getIncargo());
                    String workP = mListIn.getWorking();
                    switch (workP) {
                        case "컨테이너 진입":
                            proIncargoC = proIncargoC + (eCon40 * 2) + eCon20;
                            break;
                        case "입고작업 완료":
                            proIncargoC = proIncargoC + (eCon40 * 2) + eCon20;
                            proIncargo = proIncargo + (eCon40 * 2) + eCon20;
                            break;
                        case "검수완료":
                            proIncargoC = proIncargoC + (eCon40 * 2) + eCon20;
                            proIncargo = proIncargo + (eCon40 * 2) + eCon20;
                            proIncargoI = proIncargoI + (eCon40 * 2) + eCon20;
                            break;
                        case "창고반입":
                            proIncargoC = proIncargoC + (eCon40 * 2) + eCon20;
                            proIncargo = proIncargo + (eCon40 * 2) + eCon20;

                            proIncargoW = proIncargoW + (eCon40 * 2) + eCon20;
                            break;
                    }


                }
                totalTeu = (con40 * 2) + con20;
                proIncargoC = (int) (((double) proIncargoC / (double) totalTeu) * 100);
                proIncargo = (int) (((double) proIncargo / (double) totalTeu) * 100);
                proIncargoI = (int) (((double) proIncargoI / (double) totalTeu) * 100);
                proIncargoW = (int) (((double) proIncargoW / (double) totalTeu) * 100);
                rateIncargoC.setText(proIncargoC + "%");
                if (proIncargoC == 100) {
                    rateIncargoC.setTextColor(Color.RED);
                }
                rateIncargo.setText(proIncargo + "%");
                if (proIncargo == 100) {
                    rateIncargo.setTextColor(Color.RED);
                }
                rateIncargoI.setText(proIncargoI + "%");
                if (proIncargoI == 100) {
                    rateIncargoI.setTextColor(Color.RED);
                }
                rateIncargoW.setText(proIncargoW + "%");
                if (proIncargoW == 100) {
                    rateIncargoW.setTextColor(Color.RED);
                }
                inContainer40.setText(con40 + "대");
                inContainer20.setText(con20 + "대");
                inCargo.setText(cargo + "EA");
                inPlt.setText(qty + "PLT");

                adapterIn.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        Query sortByDateOutcargoData = databaseReferenceOut.orderByChild("date").equalTo(dateToday);
        sortByDateOutcargoData.addListenerForSingleValueEvent(outListener);

        Query sortByDateIncargoData = databaseReferenceIn.orderByChild("date").equalTo(dateToday);
        sortByDateIncargoData.addListenerForSingleValueEvent(inListener);

        TextView txtFineStaff=view.findViewById(R.id.dialog_title_finestaff);
        TextView txtFineStaffWomen=view.findViewById(R.id.dialog_title_finewomen);
        TextView txtOutsourcingMale=view.findViewById(R.id.dialog_title_outsourcingmen);
        TextView txtOutsourcingFemale=view.findViewById(R.id.dialog_title_outsourcingwomen);
        TextView txtOutsourcingEquip=view.findViewById(R.id.dialog_title_outsourcingEquip);

        DatabaseReference databaseReferenceStaff=database.getReference("DeptName/"+deptName+"/WorkingStaffCheck/");
        ValueEventListener listenerStaff=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double fineStaff = 0,fineStaffWomen = 0,outsourcingMale = 0,outsourcingFemale = 0,outsourcingEquip=0;
                for(DataSnapshot data:snapshot.getChildren()){
                    if(data.getKey().contains(dateToday)){
                        ListOutSourcingValue mList=data.getValue(ListOutSourcingValue.class);

                            switch(mList.getGender()){
                                case "Staff":
                                    fineStaff=fineStaff+mList.getCount();
                                    break;
                                case "WomenStaff":
                                    fineStaffWomen=fineStaffWomen+mList.getCount();
                                    break;
                                case "남자":
                                    outsourcingMale=outsourcingMale+mList.getCount();
                                    break;
                                case "여자":
                                    outsourcingFemale=outsourcingFemale+mList.getCount();
                                    break;
                                case "장비운행":
                                    outsourcingEquip=outsourcingEquip+mList.getCount();
                            }
                        }
                    }
                txtFineStaff.setText(fineStaff+" 명");
                txtFineStaffWomen.setText(fineStaffWomen+" 명");
                txtOutsourcingMale.setText(outsourcingMale+" 명");
                txtOutsourcingFemale.setText(outsourcingFemale+" 명");
                txtOutsourcingEquip.setText(outsourcingEquip+" 명");
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Query sortByDateStaff=databaseReferenceStaff.orderByChild("date").equalTo(dateToday);
        sortByDateStaff.addListenerForSingleValueEvent(listenerStaff);



        titleBuilder.setView(view);

        AlertDialog dialog = titleBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);


        cardViewOutcargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listOut = null;
                intentOutcargoActivity();
            }
        });
        cardViewIncargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent=new Intent(getApplicationContext(),Incargo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        cardViewWorkStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           dialog.dismiss();
           Intent intent=new Intent(getApplicationContext(),ActivityWorkingStaff.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Button btnCamera=view.findViewById(R.id.title_button_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent=new Intent(getApplicationContext(),CameraCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    @Override
    public void itemClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        String refPath = listOut.get(position).getKeypath();
        String consigneeName = listOut.get(position).getConsigneeName();
        String dialogTitle =
                consigneeName + "_" + listOut.get(position).getDescription() + listOut.get(position).getTotalQty();
        dialogOutCargoRecyclerItemClicked(refPath, dialogTitle, consigneeName);

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

                initIntent();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "permission denied" + permissions[requestCode], Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    public void getVersion() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference;
        databaseReference = database.getReference("Version");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot version : snapshot.getChildren()) {
                    VersionCheck data = version.getValue(VersionCheck.class);
                    int versionCheck = data.getVersionChecked();
                    int versioncode = 0;
                    try {
                        PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                        versioncode = pi.versionCode;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (versionCheck != versioncode) {
                        alertVersion = "현재 버전:" + versioncode + "으로 " + "" + "최신버전:" + versionCheck + " 로 업데이트 바랍니다.!";
                        txtTitle.setText(dateToday + " 입,출고 현황" + "\n" + alertVersion);
                        txtTitle.setTextColor(Color.RED);
                        txtTitle.setTextSize(10);
                        Animation ani=new AlphaAnimation(0.0f,1.0f);
                        ani.setRepeatMode(Animation.REVERSE);
                        ani.setDuration(1000);
                        ani.setRepeatCount(Animation.INFINITE);
                        txtTitle.startAnimation(ani);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dialogOutCargoRecyclerItemClicked(String refPath, String dialogTitle, String consigneeName) {
        int listOutSize = listOut.size();
                                for (int i = (listOutSize - 1); 0 <= i; i--) {
                                    if (!refPath.equals(listOut.get(i).getKeypath())) {
                                        listOut.remove(i);

                                    }
                                }
        Intent intent = new Intent(TitleActivity.this, OutCargoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("deptName", deptName);
        intent.putExtra("nickName", nickName);
        intent.putExtra("listOut", listOut);
        intent.putExtra("refPath",refPath);
        intent.putExtra("consigneeName",consigneeName);

        startActivity(intent);

    }


    @Override
    public void onItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
        Intent intent=new Intent(getApplicationContext(),Incargo.class);
        intent.putExtra("refPath",listIn.get(pos).getKeyValue());
        intent.putExtra("date",listIn.get(pos).getDate());
        intent.putExtra("bl",listIn.get(pos).getBl());
        intent.putExtra("consigneeName",listIn.get(pos).getConsignee());
        intent.putExtra("container",listIn.get(pos).getContainer());
        startActivity(intent);


    }

    @Override
    public void onLongItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
        Intent intent = new Intent(this, Incargo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }


    public void pushMessage(String depotName, String nickName, String message, String contents) {
        PushFcmProgress push = new PushFcmProgress(requestQueue);
        push.sendAlertMessage(depotName, nickName, message, contents);
    }


    @Override
    public void itemLongClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        Intent intent = new Intent(this, OutCargoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("depotName", deptName);
        intent.putExtra("nickName", nickName);

        startActivity(intent);
    }

    @Override
    public int compare(Fine2IncargoList o1, Fine2IncargoList o2) {
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.title_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_userInfo:
               PublicMethod publicMethod=new PublicMethod(this);
               publicMethod.checkUserInfo();
                break;
            case R.id.action_settings:
//                putBasicData();
                publicMethod=new PublicMethod(TitleActivity.this);
                publicMethod.getConsigneeListFromWorkingMessage();
                alertBasicData();

        }
        return true;
    }

    private void putBasicData(String refPath) {
        for (int i = 1; i < 13; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2022, i - 1, 1);
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int j = 1; j <= lastDay; j++) {

                String date = null;
                String month = null;
                if (i < 10) {
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
                        database.getReference("DeptName/" + deptName + "/"+refPath+"/" + month +"월/"+ "2022-" + month + "-" + date);
                Map<String, String> value = new HashMap<>();
                value.put("json 등록시 덥어쓰기 바랍니다", "json 최초등록시 ` { `기호 다음  `,`기호 있으면 `,` 기호삭제후 최초 등록 바랍니다. ");

                databaseReference.setValue(value);
            }


        }
    }

    public void putUserInformation() {
        editor = sharedPref.edit();
        final String[] depotName = {deptName};
        ArrayList<String> depotSort = new ArrayList<String>();
        depotSort.add("1물류(02010810)");
        depotSort.add("2물류(02010027)");
        depotSort.add("(주)화인통상 창고사업부");

        ArrayList selectedItems = new ArrayList();
        int defaultItem = 0;
        selectedItems.add(defaultItem);

        String[] depotSortList = depotSort.toArray(new String[depotSort.size()]);
        AlertDialog.Builder sortBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.user_reg, null);
        EditText reg_edit = view.findViewById(R.id.user_reg_Edit);

        Button reg_button = view.findViewById(R.id.user_reg_button);
        TextView reg_depot = view.findViewById(R.id.user_reg_depot);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickName = reg_edit.getText().toString();
                reg_depot.setText(depotName[0] + "_" + nickName + "으로 사용자 등록을" + "\n" + " 진행할려면 하단 confirm 버튼 클릭 바랍니다.");

            }
        });

        sortBuilder.setView(view);
        sortBuilder.setSingleChoiceItems(depotSortList, defaultItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                depotName[0] = depotSortList[which];
                reg_depot.setText("부서명_" + depotName[0] + "로 확인");

            }
        });
        sortBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putString("depotName", depotName[0]);
                editor.putString("nickName", nickName);
                editor.apply();
                Toast.makeText(TitleActivity.this, depotName[0] + "__" + nickName + "로 사용자 등록 성공 하였습니다.", Toast.LENGTH_SHORT).show();
                initIntent();
            }
        });
        sortBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        sortBuilder.show();
    }

    public void alertBasicData() {
        int versioncode = 0;
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();}
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Button btnPutVersion=new Button(this);
            btnPutVersion.setText("버전등록");
            btnPutVersion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(TitleActivity.this);
                    EditText editText=new EditText(TitleActivity.this);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setTitle("버전등록창")
                            .setView(editText)
                            .setMessage("업데이트 되거나 된 버전 입력 바랍니다.")
                            .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Map<String,Object> versionMap=new HashMap<>();
                                    int versionCheck=Integer.parseInt(editText.getText().toString());
                                    DatabaseReference versionRef=database.getReference("Version/Version");
                                    versionMap.put("versionChecked",versionCheck);
                                    versionRef.updateChildren(versionMap);
                                    Toast.makeText(TitleActivity.this,"입력 버전:"+versionCheck+" 으로 서버 등록 진행 되었습니다.",
                                            Toast.LENGTH_SHORT).show();
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

            builder.setTitle("기초자료 등록 창")
                    .setView(btnPutVersion)
                    .setMessage("물류센터명:" + deptName + "\n" + "사용자명:" + nickName + "\n" + "어플버전:" + versioncode + "\n" +"사진목록 " +
                            "리스트:"+imageViewListCount+" 장"+
                            "\n"+
                            "하단 버튼의 목록자료 초기화 버튼 클릭시" +
                            " 목록의 데이터베이스 초기화 됩니다.주의 바랍니다.")
                    .setPositiveButton("입고자료 초기화", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder1=new AlertDialog.Builder(TitleActivity.this);
                            builder1.setTitle("확인창")
                                    .setMessage("입고자료 초기화 진행 시키겠습니다.초기화 전 입고자료 목록에 대한 백업 다시한번 확인 바랍니다.!!")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            putBasicData("InCargo");
                                        }
                                    })
                                    .setNegativeButton("Data Transfer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dataTrans("Incargo2","InCargo");
                                        }
                                    })
                                    .show();

                        }
                    })
                    .setNegativeButton("출고자료 초기화", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder1=new AlertDialog.Builder(TitleActivity.this);
                            builder1.setTitle("확인창")
                                    .setMessage("출고자료 초기화 진행 시키겠습니다.초기화 전 출고자료 목록에 대한 백업 다시한번 확인 바랍니다.!!")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            putBasicData("OutCargo");
                                        }

                                    })
                                    .setNegativeButton("Data Transfer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dataTrans("Outcargo2","OutCargo");
                                        }
                                    })

                                    .show();
                            dialog.cancel();

                        }
                    })
                    .setNeutralButton("메세지창 수정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builderMessage=new AlertDialog.Builder(TitleActivity.this);
                            builderMessage.setTitle("확인창")
                                    .setMessage("메세지창 내역을 초기화 진행 시키겠습니다.초기화 전 메세지창 자료 목록에 대한 백업 다시 한번 확인 바랍니다.!!")
                                    .setPositiveButton("Data Transfer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dataTrans("WorkingMessage","WorkingMessage");
                                        }
                                    })
                                    .show();

                        }
                    })
                    .show();
        }

    private void dataTrans(String getRefPath,String refPath) {

            DatabaseReference databaseInCargo=database.getReference(getRefPath);
            databaseInCargo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot data:snapshot.getChildren()){
                        switch(refPath){
                            case "InCargo":
                                Fine2IncargoList mList=data.getValue(Fine2IncargoList.class);
                                assert mList != null;
                                String keyValue =
                                        mList.getDate() + "_" + mList.getBl() + "_" + mList.getDescription() +
                                                "_" + mList.getCount() + "_" + mList.getContainer();

                                String date="";
                                if(mList.getDate()==null){
                                    date="null";
                                }else{
                                    date=mList.getDate();
                                }
                                String month="";
                                if(mList.getDate()==null){
                                    month="null";
                                }else{
                                    if(mList.getDate().length()<5){
                                        month="null";
                                    }else{
                                        month=mList.getDate().substring(5,7);
                                        DatabaseReference databaseReference =
                                                database.getReference("DeptName/"+deptName+"/"+refPath+"/"+
                                                                month + "월/" + date+
                                                        "/"+keyValue);
                                        databaseReference.setValue(mList);
                                    }
                                }
                                break;
                            case "OutCargo":
                                OutCargoList outList=data.getValue(OutCargoList.class);
                                String keyPath=outList.getKeypath();
                                DatabaseReference refOut=
                                        database.getReference("DeptName/"+deptName+"/"+refPath+"/"+keyPath.substring(5,7)+"월/"+outList.getDate()+"/"+keyPath);
                                refOut.setValue(outList);
                                break;
                            case "WorkingMessage":
                                WorkingMessageList messageList=data.getValue(WorkingMessageList.class);
                                String keyMessage=messageList.getNickName()+"_"+messageList.getTime();
                                DatabaseReference refMessage=
                                        database.getReference("DeptName/"+deptName+"/WorkingMessage/"+keyMessage);
                                refMessage.setValue(messageList);
                        }
                        }


                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


    }

    @Override
    public void onBackPressed() {

        PublicMethod publicMethod=new PublicMethod(TitleActivity.this);
        publicMethod.intentSelect();
    }

    @Override
    public void onItemConsigneeClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {

    }
}
