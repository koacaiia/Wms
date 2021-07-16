package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TitleActivity extends AppCompatActivity implements OutCargoListAdapter.OutCargoListAdapterClickListener ,
        OutCargoListAdapter.OutCargoListAdapterLongClickListener,
        Serializable, SensorEventListener,IncargoListAdapter.AdapterClickListener,IncargoListAdapter.AdapterLongClickListener ,
        Comparator<Fine2IncargoList> {
    RecyclerView recyclerViewIn;
    RecyclerView recyclerViewOut;
    FirebaseDatabase database;
    DatabaseReference databaseReferenceOut;
    DatabaseReference databaseReferenceIn;
    ArrayList<OutCargoList> listOut=new ArrayList<>();
    ArrayList<Fine2IncargoList> listIn=new ArrayList<>();
    OutCargoListAdapter adapterOut;
    IncargoListAdapter adapterIn;
    String dateToday;

    Button btnTitle;
    static private final String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    String departmentName,nickName,wareHouseDepotIn,wareHouseDepotOut,alertDepot;

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    private long mShakeTime;
    private static final int SHAKE_SKIP_TIME=500;
    private static final float SHAKE_THERESHOLD_GRAVITY=2.7F;

    String [] permission_list={
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

    Button btnAnnual;
    Button btnWorkmessage;
    Button btnCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        display=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        requestPermissions(permission_list,0);
        sharedPref=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        if(sharedPref.getString("depotName",null)==null){
            NickCheckProcess nickcheckProcess=new NickCheckProcess(this);
            nickcheckProcess.putUserInformation();
            return;
        }

        departmentName=sharedPref.getString("depotName",null);
        nickName=sharedPref.getString("nickName",null);
        database=FirebaseDatabase.getInstance();
        switch(departmentName){
            case "2물류(02010027)":
                databaseReferenceIn=database.getReference("Incargo2");
                databaseReferenceOut=database.getReference("Outcargo2");
                wareHouseDepotIn="Incargo2";
                wareHouseDepotOut="Outcargo2";
                alertDepot="Depot2";
                break;
            case "1물류(02010810)":
                databaseReferenceIn=database.getReference("Incargo1");
                databaseReferenceOut=database.getReference("Outcargo2");
                wareHouseDepotIn="Incargo1";
                wareHouseDepotOut="Outcargo2";
                alertDepot="Depot1";

                break;
            case "(주)화인통상 창고사업부":
                databaseReferenceIn=database.getReference("Incargo");
                databaseReferenceOut=database.getReference("Outcargo2");
                wareHouseDepotIn="Incargo";
                wareHouseDepotOut="Outcargo2";
                alertDepot="Depot";
                break;
        }

        if (sharedPref.getString("depotName", null) == null) {

            putUserInformation();
            return;
        }

        dateToday=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        btnTitle=findViewById(R.id.activity_title_btn);

        btnTitle.setText(dateToday+"일  입,출고 현황 (화면초기화)");
        btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initIntent();
            }
        });

        recyclerViewOut=findViewById(R.id.titleRecyclerOut);
        recyclerViewIn=findViewById(R.id.titleRecyclerOutIn);
        LinearLayoutManager outManager=new LinearLayoutManager(this);
        LinearLayoutManager inManager=new LinearLayoutManager(this);
        recyclerViewOut.setLayoutManager(outManager);
        recyclerViewIn.setLayoutManager(inManager);

        titleDialog();

        adapterOut=new OutCargoListAdapter(listOut,this,this,this);
        adapterIn=new IncargoListAdapter(listIn,this,this);
        recyclerViewOut.setAdapter(adapterOut);
        recyclerViewIn.setAdapter(adapterIn);


        getVersion();

        FirebaseMessaging.getInstance().subscribeToTopic(alertDepot);
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }

        btnAnnual=findViewById(R.id.titleAnnual);
        btnAnnual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAnnual=new Intent(TitleActivity.this,AnnualLeave.class);
                intentAnnual.putExtra("depotName",departmentName);
                intentAnnual.putExtra("nickName",nickName);
                intentAnnual.putExtra("alertDepot",alertDepot);
                startActivity(intentAnnual);
            }
        });
        btnWorkmessage=findViewById(R.id.titleWorkmessage);
        btnWorkmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TitleActivity.this,WorkingMessageData.class);
                intent.putExtra("nickName",nickName);
                intent.putExtra("alertDepot",alertDepot);
                startActivity(intent);

            }
        });
        btnCamera=findViewById(R.id.titleCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TitleActivity.this,CameraCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("depotName",departmentName);
                intent.putExtra("nickName",nickName);
                intent.putExtra("alertDepot",alertDepot);
                startActivity(intent);
            }
        });

    }

    private void initIntent() {
        Intent intent=new Intent(this,TitleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void inCargoIntent(){
        Intent intent=new Intent(this,Incargo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("depotName",departmentName);
        intent.putExtra("nickName",nickName);
        intent.putExtra("alertDepot",alertDepot);
        startActivity(intent);
    }
    private void intentOutcargoActivity() {
        Intent intent=new Intent(this,OutCargoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("depotName",departmentName);
        intent.putExtra("nickName",nickName);
        intent.putExtra("alertDepot",alertDepot);
        intent.putExtra("listOut",listOut);
        startActivity(intent);
    }


    public void titleDialog() {
        AlertDialog.Builder titleBuilder=new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.title_dialog,null);
        Button btnConfirm=view.findViewById(R.id.button2);
        Button btnIncargo=view.findViewById(R.id.button4);
        Button btnOutcargo=view.findViewById(R.id.button3);

        TextView textTitle=view.findViewById(R.id.dialog_title_txttile);
        textTitle.setText(dateToday+" 입,출고 현황");
        ArrayList<OutCargoList> listOutP=new ArrayList<>();
        ArrayList<OutCargoList> listOutTotal=new ArrayList<>();
        listOut.clear();
        listIn.clear();
        ValueEventListener outListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    OutCargoList mList=data.getValue(OutCargoList.class);
                    listOutTotal.add(mList);
                }

                int intTotalPlt = 0,intTotalEa=0,intProgressPlt=0,intProgressEa=0;

                int listOutSize= listOutTotal.size();
                for(int i=0;i<listOutSize;i++)
                {
                    String totalQty= listOutTotal.get(i).getTotalQty();
                    String workProcess= listOutTotal.get(i).getWorkprocess();

                    if(workProcess.equals("완")){
                        listOutP.add(listOutTotal.get(i));
                        if(totalQty.contains("PLT")){
                            intProgressPlt=intProgressPlt+Integer.parseInt(totalQty.substring(0, totalQty.length() - 3));
                        }else{
                            intProgressEa=intProgressEa+ Integer.parseInt(totalQty.substring(0, totalQty.length() - 2));
                        }
                    }else{
                        listOut.add(listOutTotal.get(i));
                    }
                    if(totalQty.contains("PLT")){
                        intTotalPlt=intTotalPlt+ Integer.parseInt(totalQty.substring(0, totalQty.length() - 3));
                    }else{
                        intTotalEa=intTotalEa+ Integer.parseInt(totalQty.substring(0, totalQty.length() - 2));
                    }

                   }
                int pCargo,pPlt,pEa,rCargo,rPlt,rEa;

                pCargo=listOutTotal.size()-listOutP.size();
                pPlt=intTotalPlt-intProgressPlt;
                pEa=intTotalEa-intProgressEa;

                rCargo= (int)(((double) listOutP.size() /(double) listOutTotal.size())*100);
                rPlt=(int)(((double) intProgressPlt /(double) intTotalPlt)*100);
                rEa=(int)(((double) intProgressEa /(double) intTotalEa)*100);
                TextView totalCargo=view.findViewById(R.id.dialog_titleTotalCargo);
                totalCargo.setText(listOutTotal.size()+"건");
                TextView proCargo=view.findViewById(R.id.dialog_titleProgressOutcargo);
                proCargo.setText(pCargo+"건");
                TextView rateCargo=view.findViewById(R.id.dialog_titleProgressOutcargoRate);
                rateCargo.setText(rCargo+"%");
                if(rCargo==100){
                    rateCargo.setTextColor(Color.RED);
                }

                TextView totalPlt=view.findViewById(R.id.dialog_titleTotalCargoPlt);
                totalPlt.setText(intTotalPlt+"PLT");
                TextView proPlt=view.findViewById(R.id.dialog_titleProgressOutcargoPlt);
                proPlt.setText(pPlt+"PLT");
                TextView ratePlt=view.findViewById(R.id.dialog_titleProgressOutcargoPltRate);
                ratePlt.setText(rPlt+"%");
                if(rPlt==100){
                    ratePlt.setTextColor(Color.RED);
                }

                TextView totalEa=view.findViewById(R.id.dialog_titleTotalCargoEa);
                totalEa.setText(intTotalEa+"EA");
                TextView proEa=view.findViewById(R.id.dialog_titleProgressOutcargoEA);
                proEa.setText(pEa+"EA");
                TextView rateEa=view.findViewById(R.id.dialog_titleProgressOutcargoEaRate);
                rateEa.setText(rEa+"%");
                if(rEa==100){
                    rateEa.setTextColor(Color.RED);
                }
                adapterOut.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        TextView inContainer40=view.findViewById(R.id.dialog_titleContainer40);
        TextView inContainer20=view.findViewById(R.id.dialog_titleContainer20);
        TextView inCargo=view.findViewById(R.id.dialog_titleCargo);
        TextView inPlt=view.findViewById(R.id.dialog_titleQty);
        TextView rateIncargoC=view.findViewById(R.id.dialog_titleProgressContainer);
        TextView rateIncargo=view.findViewById(R.id.dialog_titleProgressInCargo);
        TextView rateIncargoI=view.findViewById(R.id.dialog_titleProgressInspection);
        TextView rateIncargoW=view.findViewById(R.id.dialog_titleProgressInWarehouse);




        ValueEventListener inListener= new ValueEventListener() {
            int con40,con20,cargo,qty,totalTeu,proIncargo,proIncargoW,proIncargoI,proIncargoC,eCon40,eCon20,eCargo;


            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataIn:snapshot.getChildren()){
                    Fine2IncargoList mListIn=dataIn.getValue(Fine2IncargoList.class);

                    String str40,str20,strCargo;
                    str40= mListIn.getContainer40();
                    str20=mListIn.getContainer20();
                    strCargo=mListIn.getLclcargo();
                    if(!str40.equals("0")||!str20.equals("0")||!strCargo.equals("0")){
                        listIn.add(mListIn);
                    }
                    listIn.sort(new Comparator<Fine2IncargoList>() {
                        @Override
                        public int compare(Fine2IncargoList a, Fine2IncargoList b) {
                            int compare=0;
                            compare=a.working.compareTo(b.working);

                            return compare;
                        }
                    });

                    eCon40=Integer.parseInt(str40);
                    eCon20=Integer.parseInt(str20);
                    eCargo=Integer.parseInt(strCargo);
                    con40=con40+eCon40;
                    con20=con20+eCon20;
                    cargo=cargo+eCargo;


                    qty=qty+Integer.parseInt(mListIn.getIncargo());
                    String workP=mListIn.getWorking();
                    switch(workP){
                        case "컨테이너 진입":
                            proIncargoC=proIncargoC+(eCon40*2)+eCon20;
                            break;
                        case "입고작업 완료":
                            proIncargoC=proIncargoC+(eCon40*2)+eCon20;
                            proIncargo=proIncargo+(eCon40*2)+eCon20;
                            break;
                        case "검수완료":
                            proIncargoC=proIncargoC+(eCon40*2)+eCon20;
                            proIncargo=proIncargo+(eCon40*2)+eCon20;
                            proIncargoI=proIncargoI+(eCon40*2)+eCon20;
                            break;
                        case "창고반입":
                            proIncargoC=proIncargoC+(eCon40*2)+eCon20;
                            proIncargo=proIncargo+(eCon40*2)+eCon20;

                            proIncargoW=proIncargoW+(eCon40*2)+eCon20;
                            break;
                    }


                }
                totalTeu=(con40*2)+con20;
                proIncargoC=(int)(((double)proIncargoC/(double)totalTeu)*100);
                proIncargo=(int)(((double)proIncargo/(double)totalTeu)*100);
                proIncargoI=(int)(((double)proIncargoI/(double)totalTeu)*100);
                proIncargoW=(int)(((double)proIncargoW/(double)totalTeu)*100);
                rateIncargoC.setText(proIncargoC+"%");
                if(proIncargoC==100){
                    rateIncargoC.setTextColor(Color.RED);
                }
                rateIncargo.setText(proIncargo+"%");
                if(proIncargo==100){
                    rateIncargo.setTextColor(Color.RED);
                }
                rateIncargoI.setText(proIncargoI+"%");
                if(proIncargoI==100){
                    rateIncargoI.setTextColor(Color.RED);
                }
                rateIncargoW.setText(proIncargoW+"%");
                if(proIncargoW==100){
                    rateIncargoW.setTextColor(Color.RED);
                }
                inContainer40.setText(con40+"대");
                inContainer20.setText(con20+"대");
                inCargo.setText(cargo+"EA");
                inPlt.setText(qty+"PLT");

                adapterIn.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        Query sortByDateOutcargoData=databaseReferenceOut.orderByChild("date").equalTo(dateToday);
        sortByDateOutcargoData.addListenerForSingleValueEvent(outListener);

        Query sortByDateIncargoData=databaseReferenceIn.orderByChild("date").equalTo(dateToday);
        sortByDateIncargoData.addListenerForSingleValueEvent(inListener);


        titleBuilder.setView(view);

        AlertDialog dialog=titleBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
        params.width=WindowManager.LayoutParams.MATCH_PARENT;
        params.height= WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnIncargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(getApplicationContext(),Incargo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        btnOutcargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listOut=null;
                intentOutcargoActivity();
            }
        });
    }



    @Override
    public void itemClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        String refPath=listOut.get(position).getKeypath();
        String consigneeName=listOut.get(position).getConsigneeName();
        String dialogTitle=
                consigneeName+"_"+listOut.get(position).getDescription()+listOut.get(position).getTotalQty();
        dialogOutCargoRecyclerItemClicked(refPath,dialogTitle,consigneeName);
    }

    private void putNewDataUpdateAlarm(String dialogTitle, String consigneeName, String out) {
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String timeDate=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());

        WorkingMessageList messageList=new WorkingMessageList();


        messageList.setNickName(nickName);
        messageList.setTime(timeStamp);
        messageList.setMsg(dialogTitle);
        messageList.setDate(timeDate);
        messageList.setConsignee(consigneeName);
        messageList.setInOutCargo(out);


        DatabaseReference databaseReference = database.getReference("WorkingMessage"+"/"+nickName+"_"+timeStamp);
        databaseReference.setValue(messageList);

        PushFcmProgress push=new PushFcmProgress(requestQueue);
        push.sendAlertMessage(alertDepot,nickName,dialogTitle,"WorkingMessage");

        Intent intent=new Intent(this,WorkingMessageData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            float axisX=event.values[0];
            float axisY=event.values[1];
            float axisZ=event.values[2];

            float gravityX=axisX/SensorManager.GRAVITY_EARTH;
            float gravityY=axisY/SensorManager.GRAVITY_EARTH;
            float gravityZ=axisZ/SensorManager.GRAVITY_EARTH;

            Float f=gravityX*gravityX+gravityY*gravityY+gravityZ*gravityZ;
            double squaredD=Math.sqrt(f.doubleValue());
            float gForce=(float) squaredD;
            if(gForce>SHAKE_THERESHOLD_GRAVITY){
                long currentTime=System.currentTimeMillis();
                if(mShakeTime+SHAKE_SKIP_TIME>currentTime){
                    return;
                }
                mShakeTime=currentTime;

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
        for(int result:grantResults){
            if(result== PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "permission denied"+permissions[requestCode], Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }
    public void getVersion(){
        database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference;
        databaseReference=database.getReference("Version");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot version:snapshot.getChildren()){
                    VersionCheck data=version.getValue(VersionCheck.class);
                    int versionCheck=data.getVersionChecked();
                    int versioncode = 0;
                    try {
                        PackageInfo pi=getPackageManager().getPackageInfo(getPackageName(),0);
                        versioncode=pi.versionCode;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(versionCheck!=versioncode){
                        String alertVersion="현재 버전:"+versioncode+"으로 "+""+"최신버전:"+versionCheck+" 로 업데이트 바랍니다.!";

                        Intent intent=new Intent(getApplicationContext(),WebList.class);
                        intent.putExtra("version",alertVersion);
                       startActivity(intent);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dialogOutCargoRecyclerItemClicked(String refPath, String dialogTitle, String consigneeName) {
        ArrayList<String> clickValue=new ArrayList<>();
        clickValue.add("사진제외 출고완료 등록");
        clickValue.add("사진포함 출고완료 등록");
        clickValue.add("신규출고 항목으로 공유");
        String[] clickValueList=clickValue.toArray(new String[clickValue.size()]);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(dialogTitle+" 출고")
                .setSingleChoiceItems(clickValueList,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                DatabaseReference dataRef=database.getReference(wareHouseDepotOut+"/"+refPath);
                                Map<String,Object> value=new HashMap<>();
                                value.put("workprocess","완");
                                dataRef.updateChildren(value);
                                adapterOut.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(),refPath+"건 출고 완료등록",Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                int listOutSize=listOut.size();
                                for(int i=(listOutSize-1);0 <= i;i--){
                                    if(!refPath.equals(listOut.get(i).getKeypath())){
                                        listOut.remove(i);

                                    }
                                }

                                intentOutcargoActivity();
                                break;

                            case 2:
                                putNewDataUpdateAlarm(dialogTitle+" 신규 등록",consigneeName,"OutCargo");

                                break;
                        }
                        dialog.cancel();
                    }
                })
                .show();

    }


    @Override
    public void onItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {

        String keyValue=listIn.get(pos).getDate()+"_"+listIn.get(pos).getBl()+"_"+listIn.get(pos).getDescription()+
                        "_"+listIn.get(pos).getCount()+"_"+listIn.get(pos).getContainer();
        String updateTitleValue=
                listIn.get(pos).getConsignee()+"_비엘:"+listIn.get(pos).getBl()+"_컨테이너:"+listIn.get(pos).getContainer();

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference(wareHouseDepotIn+"/"+keyValue);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        ArrayList<String> incargoContent=new ArrayList<>();
        incargoContent.add("컨테이너 진입");
        incargoContent.add("입고작업 완료");
        incargoContent.add("검수완료");
        incargoContent.add("창고반입");
        incargoContent.add("입고관련 사진등록");
        incargoContent.add("신규입고 항목으로 공유");


        String[] incargoContentList=incargoContent.toArray(new String[incargoContent.size()]);

        builder.setTitle(updateTitleValue+" 입고")
                .setSingleChoiceItems(incargoContentList,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent=new Intent(getApplicationContext(),Incargo.class);
                        switch(which){
                            case 0: case 1: case 2:case 3:
                                String contentValue=incargoContentList[which];
                                Map<String,Object> putValue=new HashMap<>();
                                putValue.put("working",contentValue);
                                databaseReference.updateChildren(putValue);
                                initIntent();
                                break;
                            case 4:
                                intent.putExtra("refPath",keyValue);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;
                            case 5:
                                putNewDataUpdateAlarm(updateTitleValue+" 신규 등록", listIn.get(pos).getConsignee(),"OutCargo");

                                break;

                        }


                    }
                })
                .show();


    }

    @Override
    public void onLongItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
        Intent intent=new Intent(this,Incargo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public  void pushMessage(String depotName, String nickName, String message, String contents) {
        PushFcmProgress push=new PushFcmProgress(requestQueue);
        push.sendAlertMessage(depotName,nickName,message,contents);
    }


    @Override
    public void itemLongClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        Intent intent=new Intent(this,OutCargoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        putUserInformation();
        return true;
    }
    public void putUserInformation() {
        editor = sharedPref.edit();
        final String[] depotName = {departmentName};
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

}