package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.StringPrepParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OutCargoActivity extends AppCompatActivity implements OutCargoListAdapter.OutCargoListAdapterClickListener,
        OutCargoListAdapter.OutCargoListAdapterLongClickListener, ImageViewActivityAdapter.ImageViewClicked ,
        Comparator<OutCargoList> {
    FirebaseDatabase database;
    RecyclerView recyclerView;
    ArrayList<OutCargoList> list;
    OutCargoListAdapter adapter;
    String deptName,nickName;

    TextView txtTitle;
    String dateToday;
    String refPath;

    public static ArrayList<String> imageViewLists=new ArrayList<>();
    ArrayList<String> clickedImageViewLists=new ArrayList<>();
    ImageViewActivityAdapter iAdapter;
    SparseBooleanArray clickedArray=new SparseBooleanArray(0);
    FloatingActionButton fltBtn;

    PublicMethod publicMethod;
    int listPosition;
    public static String keyValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_cargo);

        dateToday=new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        PublicMethod publicMethod=new PublicMethod(this);
        nickName=publicMethod.getUserInformation().get("nickName");
        deptName=publicMethod.getUserInformation().get("deptName");



        fltBtn=findViewById(R.id.activity_list_outcargo_flb);
        fltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateValue("완");
               PublicMethod publicMethod=new PublicMethod(OutCargoActivity.this,clickedImageViewLists);
               publicMethod.upLoadPictures(nickName,list.get(0).getConsigneeName(),"OutCargo",list.get(0).getKeypath(),deptName);
                Toast.makeText(getApplicationContext(),list.get(0).getKeypath()+"서버에 출고완료 등록으로(사진"+clickedImageViewLists.size()+" 장을 " +
                        "서버에 등록 진행 하였습니다.",Toast.LENGTH_LONG).show();
            }
        });
        recyclerView=findViewById(R.id.activity_list_outcargo_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        database= FirebaseDatabase.getInstance();
        list=new ArrayList<>();

        if((ArrayList<OutCargoList>)getIntent().getSerializableExtra("listOut")==null){
            getOutcargoData();
        }else{
            list=(ArrayList<OutCargoList>)getIntent().getSerializableExtra("listOut");
            pictureUpdate();

        }

        adapter=new OutCargoListAdapter(list,this,this,this);
        recyclerView.setAdapter(adapter);


        txtTitle=findViewById(R.id.activity_list_outcargo_title);
        txtTitle.setText(dateToday+" 출고 목록");
        refPath=getIntent().getStringExtra("refPath");


    }

    private void getOutcargoData() {

        list.clear();
        DatabaseReference databaseReference=
                database.getReference("DeptName/" + deptName + "/" +"OutCargo" + "/" +dateToday.substring(5,7) + "월/" +dateToday);
        ValueEventListener listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    String keyValue=data.getKey();
                    if(!keyValue.equals("json 등록시 덥어쓰기 바랍니다")) {
                        OutCargoList mList = data.getValue(OutCargoList.class);
                        Log.i("TestValue","KeyValue::::"+keyValue);
                        list.add(mList);
                        list.sort(new Comparator<OutCargoList>() {
                            @Override
                            public int compare(OutCargoList a, OutCargoList b) {
                                int compare = 0;
                                compare = a.workprocess.compareTo(b.workprocess);
                                return compare;
                            }
                        });
                    }
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        };
       databaseReference.addListenerForSingleValueEvent(listener);

    }

    @Override
    public void itemClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        refPath=list.get(position).getKeypath();
        String consigneeName=list.get(position).getConsigneeName();
        String dialogTitle=
                consigneeName+"_"+list.get(position).getDescription()+list.get(position).getTotalQty();
        listPosition=position;
        getOutcargoData(refPath);
        itemClickedDialog(consigneeName,dialogTitle);
    }

    private void getOutcargoData(String refPath) {
        list.clear();
        DatabaseReference databaseReference=
                database.getReference("DeptName/" + deptName + "/" +"OutCargo" + "/" +refPath.substring(5,7) + "월/" +refPath.substring(0,10));
        ValueEventListener listener=new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){

                    if(!data.getKey().equals("json 등록시 덥어쓰기 바랍니다")) {
                        OutCargoList mList=data.getValue(OutCargoList.class);
                        assert mList != null;
                        if (Objects.equals(mList.getKeypath(), refPath)) {
                            list.add(mList);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        };
       databaseReference.addListenerForSingleValueEvent(listener);
    }

    public void itemClickedDialog(String consigneeName, String dialogTitle){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        AlertDialog dialog=builder.create();
        View view=getLayoutInflater().inflate(R.layout.dialog_fragment_outcargo,null);
        Button btnRegExPic=view.findViewById(R.id.dialog_fragment_outcargo_RegExPic);
        btnRegExPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                updateValue("완");
                Toast.makeText(getApplicationContext(),dialogTitle+" 화물 출고 등록으로 진행 하였습니다",Toast.LENGTH_LONG).show();
                        intentTitleActivity();
            }
        });
        Button btnConPic=view.findViewById(R.id.dialog_fragment_outcargo_RegComPic);
        btnConPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            dialog.dismiss();
                updateValue("완");
                        keyValue=list.get(0).getKeypath();
                pictureUpdate();
            }
        });
        Button btnNotOutcargo=view.findViewById(R.id.dialog_fragment_outcargo_NotOutcargo);
        btnNotOutcargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                updateValue("미");
                Toast.makeText(getApplicationContext(),dialogTitle+" 화물 -미-출고 등록으로 진행 하였습니다.",Toast.LENGTH_LONG).show();
                        intentTitleActivity();

            }
        });
        Button btnNewOutcargo=view.findViewById(R.id.dialog_fragment_outcargo_NewOutcargo);
        btnNewOutcargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),dialogTitle+" 화물 신규등록 진행 하였습니다.",Toast.LENGTH_LONG).show();
                publicMethod.putNewDataUpdateAlarm(nickName,dialogTitle+" 신규 등록",consigneeName,"OutCargo",deptName);
                intentTitleActivity();
                                   }
        });
        Button btnRegPallet=view.findViewById(R.id.dialog_fragment_outcargo_RegPallet);
        btnRegPallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//

//        builder.setTitle("출고현황 변경사항");
//        ArrayList<String> clickValue=new ArrayList<>();
//        clickValue.add("사진제외 출고완료 등록");
//        clickValue.add("사진포함 출고완료 등록,항목 사진검색");
//        clickValue.add("미출고 등록");
//        clickValue.add("신규출고 항목으로 공유");
//        clickValue.add("Pallet 등록");
//        clickValue.add("항목 출고 사진 검색");
//
//        String[] clickValueList=clickValue.toArray(new String[clickValue.size()]);
        dialog.setTitle(dialogTitle+" 출고");
        dialog.setView(view);
//        builder.setSingleChoiceItems(clickValueList, 0, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                PublicMethod publicMethod=new PublicMethod(OutCargoActivity.this);
//                switch(which){
//
//                    case 0:
//                        updateValue("완");
//                        intentTitleActivity();
//                        break;
//                    case 1:
//                        updateValue("완");
//                        keyValue=list.get(0).getKeypath();
//                        pictureUpdate();
//                        break;
//                    case 2:
//                        updateValue("미");
//                        intentTitleActivity();
//                        break;
//                    case 3:
//                        publicMethod.putNewDataUpdateAlarm(nickName,dialogTitle+" 신규 등록",consigneeName,"OutCargo",deptName);
//                        break;
//                    case 4:
////                        putPalletReg(consigneeName);
//                        int totalQty=Integer.parseInt(list.get(0).getTotalQty().replace("PLT",""));
//
//                        AlertDialog.Builder builder=new AlertDialog.Builder(OutCargoActivity.this);
//                        builder.setTitle("팔렛트 등록 확인창")
//                                .setMessage("출고 팔렛트가 재고관리 되는 팔렛트 인 경우"+"\n"+"하단의 등록 버튼을 눌러 재고관리 진행 바랍니다.!")
//                                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
////                                        publicMethod.pltReg(consigneeName,list.get(0).getKeypath(),nickName,totalQty);
//                                    }
//                                })
//                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                })
//                                .show();
//
//                        break;
//
//                    case 5:
////                        itemPictureList(list.get(0).getKeypath());
//                         break;
//                }
//                dialog.dismiss();
//            }
//        })
                dialog.show();
    }

    private void putPalletReg(String consigneeName) {
        ArrayList<String> palletArrayList=new ArrayList<>();
        palletArrayList.add("KPP");
        palletArrayList.add("AJ");
        palletArrayList.add("ETC");

        String[] palletList=palletArrayList.toArray(new String[palletArrayList.size()]);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("등록팔렛트 관리")

                .setSingleChoiceItems(palletList,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        switch(which){
                            case 0:
                                AlertDialog.Builder builder=new AlertDialog.Builder(OutCargoActivity.this);
                                EditText editText=new EditText(OutCargoActivity.this);
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                editText.setText(list.get(0).getTotalQty());

                                builder.setTitle(palletArrayList.get(which)+"Pallet"+list.get(0).getTotalQty()+" 장을 팔렛트 등록합니다.")
                                .setMessage("수정사항 있으면 하단 입력창에 수정사항 입력후 등록 바랍니다")
                                .setView(editText);

                                builder.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int pltQty=Integer.parseInt(editText.getText().toString());
                                        String pltDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                        String keyValue=list.get(0).getKeypath();
                                        DatabaseReference pltRef=
                                                database.getReference("DeptName/"+deptName+"/PltManagement/"+consigneeName+"/"+
                                                                "KPP"+"/"+nickName+"_"+keyValue);
                                        Map<String,Object> value=new HashMap<>();
                                        value.put("nickName",nickName);
                                        value.put("date",pltDate);
                                        value.put("outQty",pltQty);
                                        value.put("keyValue",keyValue);

                                        pltRef.updateChildren(value);


                                    }
                                })
                                .show();

                                break;
                        }
                    }
                }).show();
    }

//    public void itemPictureList(String keyValue) {
//
//        imageViewLists.clear();
//        RecyclerView imageRecyclerView = findViewById(R.id.activity_list_outcargo_imageviewRe);
//        GridLayoutManager manager = new GridLayoutManager(this, 2);
//        imageRecyclerView.setLayoutManager(manager);
//        FirebaseStorage storage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
//        StorageReference storageReference=
//                storage.getReference("images/"+deptName+"/"+keyValue.substring(0,10)+"/OutCargo/"+keyValue);
//        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onSuccess(ListResult listResult) {
//
//                for(StorageReference item:listResult.getItems()){
//
//                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//
//                            imageViewLists.add(uri.toString());
//
//                            iAdapter = new ImageViewActivityAdapter(imageViewLists);
//                            if(imageViewLists.size()==listResult.getItems().size()){
//                                imageRecyclerView.setAdapter(iAdapter);
//                                iAdapter.notifyDataSetChanged();
//                            }
//                            iAdapter.clickListener=new ImageViewActivityAdapter.ImageViewClicked() {
//                                @Override
//                                public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
//                                    PublicMethod publicMethod=new PublicMethod(OutCargoActivity.this);
//                                    publicMethod.adapterPictureSavedMethod(imageViewLists.get(position));
//                                }
//                            };
//
//
//                        }
//
//                    });
//
//                }
//
//            }
//        });
//
//    }
//

    private void pictureUpdate() {
        ViewPager2 viewPager2=findViewById(R.id.activity_list_outcargo_viewpager2);
        ViewPageStateAdapter adapter=new ViewPageStateAdapter(this,4);
        viewPager2.setAdapter(adapter);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setCurrentItem(1000);
        viewPager2.setOffscreenPageLimit(3);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(positionOffset==0){
                    viewPager2.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });








    }

    public void intentTitleActivity() {
        Intent intent=new Intent(OutCargoActivity.this,TitleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void initIntent() {
        Intent intent=new Intent(OutCargoActivity.this,OutCargoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateValue(String updateValue){

        DatabaseReference dataRef=database.getReference("DeptName/" + deptName + "/" +"OutCargo" + "/" +refPath.substring(5,7) +
                "월/" +refPath.substring(0,10)+"/"+refPath);

        Map<String,Object> value=new HashMap<>();
        value.put("workprocess",updateValue);
        dataRef.updateChildren(value);

        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.outcargoactivity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.outcargoActivity_date:
                dateAlertDialog();

                break;

        }
        return true;
    }

    private void dateAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        DatePicker datePicker=new DatePicker(this);
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month,day;
                if((monthOfYear+1)<10){
                    month="0"+(monthOfYear+1);
                }else{
                    month=String.valueOf(monthOfYear+1);
                }
                if(dayOfMonth<10){
                    day="0"+dayOfMonth;
                }else{
                    day=String.valueOf(dayOfMonth);
                }
                dateToday=year+"-"+month+"-"+day;
                Toast.makeText(getApplicationContext(),dateToday+"을 지정",Toast.LENGTH_SHORT).show();
            }

        });
        builder.setTitle("검색일 설정")
                .setView(datePicker)
                .setPositiveButton("지정일 검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtTitle.setText(dateToday+" 모든 출고 목록");
                        getOutcargoData();
                    }
                })
                .setNegativeButton("당일 검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dateToday=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        txtTitle.setText(dateToday+" 모든 출고 목록");
                        getOutcargoData();
                    }
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }



    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        String uriString=imageViewLists.get(position);
       if(clickedArray.get(position,false)){
           clickedArray.delete(position);
           clickedImageViewLists.remove(uriString);
       }else{
           clickedArray.put(position,true);
           clickedImageViewLists.add(uriString);
       }
       if(clickedImageViewLists.size()>7){
           AlertDialog.Builder builder=new AlertDialog.Builder(this);
           builder.setTitle("!사진전송 주의사항")
                   .setMessage("한번에 전송할수 있는 사진은 최대 7장 입니다.")
                   .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Toast.makeText(getApplicationContext(),"사진을 다신 선택 하기 바랍니다.",Toast.LENGTH_SHORT).show();

                       }
                   }).show();
       }
       String clickedPictureCount="("+clickedImageViewLists.size()+"장 선택)";
        txtTitle.setText(dateToday+" 출고 목록"+clickedPictureCount);
    }

    public void upCapturePictures(String inoutItems,String consigneeName){
        int arrsize=clickedImageViewLists.size();

        SharedPreferences sharedPreferences=getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nick=sharedPreferences.getString("nickName","Fine");

        if(consigneeName==null){
            AlertDialog.Builder builder=new AlertDialog.Builder(OutCargoActivity.this);
            builder.setMessage("!출고 목록 선택이 되질 않았습니다."+"\n"+"상단의 출고목록 틀릭후 전송 바랍니다.!")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            return;
        }
        String message=consigneeName+"_"+inoutItems+"_사진 업로드";
        CaptureProcess captureProcess=new CaptureProcess(this);
        String activityName=this.getClass().getSimpleName();
        for(int i=0;i<arrsize;i++){
            Uri uri = Uri.fromFile(new File(clickedImageViewLists.get(i)));
            String strRef = dateToday + "/" + consigneeName+"/"+inoutItems+"/" + nick+System.currentTimeMillis() + ".jpg";
            captureProcess.firebaseCameraUpLoad(uri, consigneeName, inoutItems, nick, message,strRef,i,arrsize,activityName);
        }



    }

    public void sendMessage(String message){


        PublicMethod publicMethod=new PublicMethod(this);
        publicMethod.sendPushMessage(deptName,nickName,message,"CameraUpLoad");
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
    public void itemLongClicked(OutCargoListAdapter.ListView listView, View v, int position) {

    }

    @Override
    public int compare(OutCargoList o1, OutCargoList o2) {
        return 0;
    }
}