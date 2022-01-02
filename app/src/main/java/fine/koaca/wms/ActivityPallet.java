package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityPallet extends AppCompatActivity implements ActivityPalletRecyclerAdapter.PltClicked,
        ActivityPalletRecyclerAdapter.PltLongClicked ,ActivityPalletResultAdapter.ConsigneeClicked,
        ActivityPalletResultAdapter.KppClicked,ActivityPalletResultAdapter.AjClicked,ActivityPalletResultAdapter.EtcClicked{
RecyclerView recyclerview;
RecyclerView recyclerviewResult;
FirebaseDatabase database;
ArrayList<ActivityPalletList> list;
ArrayList<ActivityPalletResultList> resultLists;
ActivityPalletRecyclerAdapter adapter;
ActivityPalletResultAdapter adapterResult;
String deptName;
String consigneeName;
String pltS,yearMonth;
TextView txtSearch;
String[] spPltSList={"KPP","AJ","ETC"};

ArrayList<String> consigneeList=new ArrayList<>();
ActivityPalletResultList listResult;

String bl,date,des,keyValue,nickName,refPath,tDate;
int inQty,outQty,stockQty;

Button btnDateSearch;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet);
        PublicMethod publicMethod=new PublicMethod(this);
        deptName=publicMethod.getUserInformation().get("deptName");

        txtSearch=findViewById(R.id.plt_txtSearch);
        btnDateSearch=findViewById(R.id.textView11);
        btnDateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMonth();
            }
        });
        yearMonth="ALL";
        resultLists=new ArrayList<>();
        listResult=new ActivityPalletResultList();
        database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1=database.getReference("DeptName/"+deptName+"/PltManagement");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    consigneeList.add(data.getKey());
                }
                getResultData(consigneeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerviewResult=findViewById(R.id.pltrecyclereviewResult);
        LinearLayoutManager managerResult=new LinearLayoutManager(this);
        recyclerviewResult.setLayoutManager(managerResult);
        adapterResult=new ActivityPalletResultAdapter(resultLists,this,this,this,this);
        recyclerviewResult.setAdapter(adapterResult);
        adapterResult.notifyDataSetChanged();



        recyclerview=findViewById(R.id.activity_pallet_recyclerview);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager(manager);
        list=new ArrayList<>();
        adapter=new ActivityPalletRecyclerAdapter(list,this,this);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getResultData(ArrayList<String> consigneeList) {
        for(int i=0;i<consigneeList.size();i++){
            final int[] kppQty = {0};
            final int[] ajQty = {0};
            final int[] etcQty={0};
            for(int j=0;j<spPltSList.length;j++){
                String consigneeName=consigneeList.get(i);
                String plts=spPltSList[j];

                DatabaseReference databaseReference=
                        database.getReference("DeptName/"+deptName+"/PltManagement/"+consigneeList.get(i)+"/"+plts);
                int finalJ = j;
                ValueEventListener listener=new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data:snapshot.getChildren()) {

                            ActivityPalletList mList = data.getValue(ActivityPalletList.class);
                            switch (finalJ) {
                                case 0:
                                    kppQty[0] = mList.getStockQty();
                                    break;
                                case 1:
                                    ajQty[0] = mList.getStockQty();
                                    break;
                                case 2:
                                    etcQty[0] = mList.getStockQty();
                                    break;
                            }

                            listResult = new ActivityPalletResultList(consigneeName, kppQty[0], ajQty[0], etcQty[0]);
                            resultLists.add(listResult);
                            int count=resultLists.size();
                            if(count!=1){
                                if(resultLists.get(count-1).getConsigneeName().equals(resultLists.get(count-2).getConsigneeName())){
                                    resultLists.remove(count-2);
                                }
                            }
                        }
                        adapterResult.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                Query sortByDate=databaseReference.orderByChild("date");
                sortByDate.addListenerForSingleValueEvent(listener);
            }


        }

    }

    private void searchMonth() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.year_month_picker,null);
        TextView txtYear=view.findViewById(R.id.picker_txtYear);
        String yearThis=new SimpleDateFormat("yyyy").format(new Date());
        String monthThis=new SimpleDateFormat("MM").format(new Date());
        TextView txtMonth=view.findViewById(R.id.picker_txtMonth);
        txtYear.setText(yearThis);
        txtMonth.setText(monthThis);
        NumberPicker nbYear=view.findViewById(R.id.picker_year);
        nbYear.setMaxValue(2023);
        nbYear.setMinValue(2021);
        nbYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                 String year=String.valueOf(newVal);
                txtYear.setText(year);
            }
        });
        NumberPicker nbMonth=view.findViewById(R.id.picker_month);
        nbMonth.setMaxValue(12);
        nbMonth.setMinValue(1);
        nbMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String month;
                if(newVal<10){
                    month="0"+newVal;
                }else{
                    month=String.valueOf(newVal);
                }
                txtMonth.setText(month);
            }

        });
        builder.setTitle("검색월 설정창")
                .setView(view)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yearMonth=txtYear.getText().toString()+"-"+txtMonth.getText().toString();
                        String searchMsg="기간:"+yearMonth+"\n"+"기간 설정 합니다.검색하고자 하는 항목을 클릭하여 추가 진행 바랍니다.";
                        Toast.makeText(getApplication(),searchMsg,Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    @Override
    public void onBackPressed() {
        PublicMethod publicMethod=new PublicMethod(this);
        publicMethod.intentSelect();
    }

    private void getDatabase(String yearMonth, String consigneeName, String pltS) {
        final int[] inQty = {0};
        final int[] outQty = {0};
        list.clear();

        refPath="DeptName/"+deptName+"/PltManagement/"+ consigneeName +"/"+ pltS+"/";
        txtSearch.setText("기간: "+yearMonth+"\n"+"화주명: "+consigneeName+"\n"+"팔렛트 규격: "+pltS+"     입,출고 세부내역");

        DatabaseReference databaseReference=database.getReference(refPath);
        ValueEventListener listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){

                    String keyValue=data.getKey();
                    ActivityPalletList mList=data.getValue(ActivityPalletList.class);
                    inQty[0] =mList.getInQty()+ inQty[0];
                    outQty[0] =mList.getOutQty()+ outQty[0];

                    if(yearMonth.equals("ALL")){
                        list.add(mList);
                    }else{
                        String tYearMonth=mList.getDate().substring(0,7);
                        if(yearMonth.equals(tYearMonth)){
                            list.add(mList);
                        }
                    }
                    DatabaseReference databaseKeyRef=
                            database.getReference(refPath+keyValue);
                    Map<String,Object> valueKey=new HashMap<>();
                    valueKey.put("stockQty", inQty[0] - outQty[0]);
                    valueKey.put("keyValue",keyValue);
                    valueKey.putIfAbsent("refPath",refPath);
                    databaseKeyRef.updateChildren(valueKey);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        Query sortbyDate=databaseReference.orderByChild("date");
        sortbyDate.addListenerForSingleValueEvent(listener);

    }

    @Override
    public void clicked(ActivityPalletRecyclerAdapter.ListViewHolder listViewHolder, View v, int position) {
        bl=list.get(position).getBl();
        date=list.get(position).getDate();
        des=list.get(position).getDes();
        keyValue=list.get(position).getKeyValue();
        refPath=list.get(position).getRefPath();
        tDate=list.get(position).gettDate();
        nickName=list.get(position).getNickName();

        inQty=list.get(position).getInQty();
        outQty=list.get(position).getOutQty();
        stockQty=list.get(position).getStockQty();

        inputValueDialog(position);

    }

    @Override
    public void longClicked(ActivityPalletRecyclerAdapter.ListViewHolder listViewHolder, View v, int position) {
        String bl=list.get(position).getBl();
        String des=list.get(position).getDes();
        String date=list.get(position).getDate();
        int in=list.get(position).getInQty();
        int out=list.get(position).getOutQty();
        int stock=list.get(position).getStockQty();



        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityPallet.this);
        builder.setTitle("비엘:"+bl+"\n"+"품명:"+des+"  항목 삭제 확인창")
                .setMessage("날짜"+date+"_재고:"+stock+"\n"+"입고:"+in+",출고:"+out)
                .setPositiveButton("삭제 확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String keyValue=list.get(position).getKeyValue();
                                String refPath=list.get(position).getRefPath();
                                DatabaseReference ref=database.getReference(refPath);
                                Map<String,Object> value=new HashMap<>();
                                value.put(keyValue,null);
                                ref.updateChildren(value);
                                Toast.makeText(ActivityPallet.this,"선택항목에 대한 자료 삭제 완료 되었습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ActivityPallet.this,ActivityPallet.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ActivityPallet.this.startActivity(intent);
                            }
                        }
                )
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();



        DatabaseReference databaseReference=database.getReference(list.get(position).getKeyValue());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void inputValueDialog(int position){

        final String[] month = new String[1];
        final String[] day = new String[1];
        final String[] dateResult = new String[1];
        final int[] qty = new int[1];
        Map<String,Object> value=new HashMap<>();
        ArrayList<String> contentValues=new ArrayList<String>();
        contentValues.add("날짜:"+date);
        contentValues.add("입고수량:"+inQty);
        contentValues.add("사용수량:"+outQty);
        contentValues.add("재고이관");
        contentValues.add("추가사용");

        DatabaseReference databaseReference=database.getReference(refPath+keyValue);
        DatabaseReference putStockReference=database.getReference(refPath);
        RecyclerView recyclerView=new RecyclerView(ActivityPallet.this);
        LinearLayoutManager manager=new LinearLayoutManager(ActivityPallet.this);
        recyclerView.setLayoutManager(manager);
        manager.setOrientation(RecyclerView.HORIZONTAL);

        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");

        StorageReference storageReference=firebaseStorage.getReference(deptName+"/Pallet/"+consigneeName+"/"+keyValue);
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ListResult listResult) {
                ArrayList<ImageViewList> imageViewLists=new ArrayList<>();
                for(StorageReference items:listResult.getItems()){
                    items.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageViewList imageViewList=new ImageViewList(uri.toString());
                            imageViewLists.add(imageViewList);

                            if(imageViewLists.size()==listResult.getItems().size()){
                                ImageViewListAdapter adapter=new ImageViewListAdapter(imageViewLists);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });
                }

            }
        });
        dateResult[0]=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String[] contentValuesList=contentValues.toArray(new String[contentValues.size()]);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("팔렛트 등록창 수정")
                .setView(recyclerView)
                .setSingleChoiceItems(contentValuesList,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which1) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityPallet.this);
                        DatePicker datePicker=new DatePicker(ActivityPallet.this);

                        switch(which1){
                            case 0:
                                final String[] dateIncargo = new String[1];
                                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                    @Override
                                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                        if(monthOfYear+1<10){
                                            month[0] ="0"+(monthOfYear+1);
                                        }else{
                                            month[0] =String.valueOf(monthOfYear+1);
                                        }

                                        if(dayOfMonth<10){
                                            day[0] ="0"+dayOfMonth;
                                        }else{
                                            day[0] =String.valueOf(dayOfMonth);
                                        }
                                        dateResult[0] =year+"-"+ month[0] +"-"+ day[0];
                                        Toast.makeText(ActivityPallet.this, dateResult[0],
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                               builder.setTitle("날짜 선택창")
                                       .setView(datePicker)
                                       .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                           @SuppressLint("NotifyDataSetChanged")
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               value.put("date",dateResult[0]);
                                               databaseReference.updateChildren(value);
                                               Toast.makeText(ActivityPallet.this, dateResult[0]+" 로 팔렛트 입고일 변경",
                                                       Toast.LENGTH_SHORT).show();
                                               Intent intent=new Intent(ActivityPallet.this,ActivityPallet.class);
                                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                               getDatabase(yearMonth,consigneeName,pltS);
                                           }
                                       })
                                       .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {

                                           }
                                       })
                                       .show();
                                break;
                            case 1: case 2:
                                EditText editText=new EditText(ActivityPallet.this);
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                builder.setTitle("수량 변경창")
                                        .setView(editText)
                                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                            @SuppressLint("NotifyDataSetChanged")
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                qty[0] = Integer.parseInt(editText.getText().toString());
                                               switch(which1){
                                                   case 1:
                                                       value.put("inQty",qty[0]);
                                                       break;
                                                   case 2:
                                                       value.put("outQty",qty[0]);

                                                }
                                                databaseReference.updateChildren(value);

                                                Intent intent=new Intent(ActivityPallet.this,ActivityPallet.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                putStockReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        int inQty=0,outQty=0;
                                                        for(DataSnapshot data:snapshot.getChildren()){
                                                            ActivityPalletList mList=data.getValue(ActivityPalletList.class);
                                                            inQty=inQty+mList.getInQty();
                                                            outQty=outQty+mList.getOutQty();
                                                        }
                                                        Map<String,Object> stockMap=new HashMap<>();
                                                        stockMap.put("stockQty",inQty-outQty);
                                                        databaseReference.updateChildren(stockMap);
                                                        getDatabase(yearMonth,consigneeName,pltS);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                                adapter.notifyDataSetChanged();
//                                                ActivityPallet.this.startActivity(intent);
                                            }
                                        })
                                        .show();
                                break;
                            case 3:
                                dialogPalletTrans("","");
                                break;
                            case 4:
                                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                    @Override
                                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                        if(monthOfYear+1<10){
                                            month[0] ="0"+(monthOfYear+1);
                                        }else{
                                            month[0] =String.valueOf(monthOfYear+1);
                                        }

                                        if(dayOfMonth<10){
                                            day[0] ="0"+dayOfMonth;
                                        }else{
                                            day[0] =String.valueOf(dayOfMonth);
                                        }
                                        dateResult[0] =year+"-"+ month[0] +"-"+ day[0];
                                        Toast.makeText(ActivityPallet.this, dateResult[0],
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                             builder.setTitle("추가사용 날자 선택창")
                                     .setView(datePicker)
                                     .setPositiveButton("날짜확인", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                             EditText editText=new EditText(ActivityPallet.this);
                                             editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                             builder.setTitle("추가사용 수량 등록창")
                                                    .setView(editText)
                                                    .setPositiveButton("추가수량 등록", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            qty[0] = Integer.parseInt(editText.getText().toString());
                                                            value.put("date",dateResult[0]);
                                                            value.put("outQty",qty[0]);
                                                            value.put("bl",list.get(position).getBl());
                                                            value.put("des",list.get(position).getDes());
                                                            value.put("inQty",0);
                                                            value.put("nickName",list.get(position).getNickName());
                                                            value.put("keyValue",list.get(position).getKeyValue());


                                                            DatabaseReference changedRef=database.getReference(refPath+
                                                                    "_추가사용"+dateResult[0]);
                                                            changedRef.updateChildren(value);
                                                            Intent intent=new Intent(ActivityPallet.this,ActivityPallet.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            ActivityPallet.this.startActivity(intent);
                                                        }
                                                    })
                                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .show();
                                         }
                                     })
                                     .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {

                                         }
                                     })
                                     .show();

                                break;
                        }
                        dialog.cancel();
                    }
                })

                .show();
    }

    private void dialogPalletTrans(String monthOfIncargo,String putReference) {
        final String[] strIncargoMonth = new String[1];
        final String[] strIncargoYear=new String[1];
        String strConsigneeName;
        String strIncargoCount;
        final String[] strTransDate = new String[1];
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.dialog_pallet_datatransferview,null);
        Button btnIncargoDate=view.findViewById(R.id.dialog_pallet_datatransferview_btnIncargoDate);
        Spinner spConsignee=view.findViewById(R.id.dialog_pallet_datatransferview_spinnerConsignee);
        EditText editIncargoCount=view.findViewById(R.id.dialog_pallet_datatransferview_editIncarogCount);
        Button btnIncargoTransDate=view.findViewById(R.id.dialog_pallet_datatransferview_btnTransDate);
        TextView txtIncargoYear=view.findViewById(R.id.dialog_pallet_datatransferview_txtIncargoYear);
        TextView txtIncargoMonth=view.findViewById(R.id.dialog_pallet_datatransferview_txtIncargoDate);
        TextView txtIncargoCount=view.findViewById(R.id.dialog_pallet_datatransferview_txtIncargoCount);
        TextView txtTransDate=view.findViewById(R.id.dialog_pallet_datatransferview_txtTransDate);
        TextView txtDes=view.findViewById(R.id.dialog_pallet_datatransferview_txtDes);
        Button btnIncargoCount=view.findViewById(R.id.dialog_pallet_datatransferview_btnRegIncargoCount);

        btnIncargoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityPallet.this);
                View yearMonthPicker=getLayoutInflater().inflate(R.layout.year_month_picker,null);
                String yearThis=new SimpleDateFormat("yyyy").format(new Date());
                String monthThis=new SimpleDateFormat("MM").format(new Date());
                TextView txtMonth=yearMonthPicker.findViewById(R.id.picker_txtMonth);
                TextView txtYear=yearMonthPicker.findViewById(R.id.picker_txtYear);
                txtMonth.setText(monthThis);
                txtYear.setText(yearThis);
                NumberPicker nbYear=yearMonthPicker.findViewById(R.id.picker_year);
                nbYear.setMaxValue(2023);
                nbYear.setMinValue(2021);
                nbYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                      strIncargoYear[0]=String.valueOf(i1);
                        txtYear.setText(strIncargoYear[0]);
                    }
                });
                NumberPicker nbMonth=yearMonthPicker.findViewById(R.id.picker_month);
                nbMonth.setMaxValue(12);
                nbMonth.setMinValue(1);
                nbMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                       strIncargoMonth[0] =String.valueOf(i1);
                        txtMonth.setText(strIncargoMonth[0]);
                    }
                });
                builder.setTitle("화물 반입월 등록 창")
                        .setView(yearMonthPicker)
                        .setPositiveButton("반입 년,월 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                txtIncargoYear.setText(txtYear.getText().toString());
                                txtIncargoMonth.setText(txtMonth.getText().toString());


        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference dataRef;
        String year=strIncargoYear[0];
        String refPath="DeptName/"+deptName+"/InCargo/"+strIncargoMonth[0]+"월/";

        Calendar calendar=Calendar.getInstance();
        ArrayList<Fine2IncargoList> incargoList=new ArrayList<>();
        ArrayList<String> blValueList=new ArrayList<>();

            calendar.set(Integer.parseInt(year),(Integer.parseInt(strIncargoMonth[0]))-1,1);
            int monthOfLastDay=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for(int j=1;j<=monthOfLastDay;j++){
                String dateInDatabase=null;
                if(j<10){
                    dateInDatabase="0"+j;
                }else{
                    dateInDatabase=String.valueOf(j);
                }
                dataRef=database.getReference(refPath+year+"-"+strIncargoMonth[0]+"-"+dateInDatabase);
                ValueEventListener listener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data:snapshot.getChildren()){
                            Fine2IncargoList mList=data.getValue(Fine2IncargoList.class);
                            if(!blValueList.contains(mList.getBl())){
                                blValueList.add(mList.getBl());
                                incargoList.add(mList);
                            }

                        }

                        ArrayAdapter<String> spinnerList=new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_dropdown_item,blValueList);
                        spinnerList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spConsignee.setAdapter(spinnerList);
                        spConsignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                txtDes.setText(incargoList.get(i).getDescription());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                Query sortByKey=dataRef.orderByChild("consignee").equalTo("SPCGFS(시노관세사)");
                sortByKey.addListenerForSingleValueEvent(listener);
            }
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

       btnIncargoCount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
          txtIncargoCount.setText(editIncargoCount.getText().toString());
           }
       });
       btnIncargoTransDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityPallet.this);
                DatePicker datePicker=new DatePicker(ActivityPallet.this);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                       String month,day;
                        if(i1+1<10){
                            month ="0"+(i1+1);
                        }else{
                            month=String.valueOf(i1+1);
                        }

                        if(i2<10){
                            day="0"+i2;
                        }else{
                            day=String.valueOf(i2);
                        }
                        strTransDate[0] =i+"-"+month +"-"+day;
                    }
                });
                builder.setTitle("재고이관일 등록창")
                        .setView(datePicker)
                        .setPositiveButton("이관일등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                txtTransDate.setText(strTransDate[0]);
                                Toast.makeText(ActivityPallet.this,strTransDate[0]+" 으로 재고 이관일 등록",Toast.LENGTH_SHORT).show();
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
        builder.setTitle("재고이관 설정창")
                .setView(view)
                .setPositiveButton("재고이관등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
//        TextView txtDate=view.findViewById(R.id.dialog_pallet_datatrans_txtTdate);
//        txtDate.setText(date);
//        TextView txtBl=view.findViewById(R.id.dialog_pallet_datatrans_txtBl);
//        TextView txtQty=view.findViewById(R.id.dialog_pallet_datatrans_txtQty);
//        TextView txtDes=view.findViewById(R.id.dialog_pallet_datatrans_txtDes);
//        Spinner spinner=view.findViewById(R.id.dialog_pallet_datatrans_spinner);
//        EditText editQty=view.findViewById(R.id.dialog_pallet_datatrans_editQty);
//
//
//
//
//        Button btnBl=view.findViewById(R.id.dialog_pallet_datatrans_btnSearchBl);
//        btnBl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityPallet.this);
//                EditText editText=new EditText(ActivityPallet.this);
//                builder.setTitle("비엘번호 조회")
//                        .setMessage("비엘번호 마지막 4자리 입력후 하단 비엘조회 버튼 클릭 바랍니다")
//                        .setView(editText)
//                        .setPositiveButton("비엘조회", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Toast.makeText(getApplicationContext(),editText.getText().toString()+" 비엘 번호 조회 합니다.",
//                                        Toast.LENGTH_SHORT).show();
//                                String searchBl=editText.getText().toString();
//                                String baseBl;
//                                for(int j=0;j<TitleActivity.list.size();j++){
//                                    baseBl=TitleActivity.list.get(j).getBl();
//                                }
//                            }
//
//            })
//                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        })
//                        .show();
//        }});
//        Button btnQty=view.findViewById(R.id.dialog_pallet_datatrans_btnPutQty);
//        btnQty.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                txtQty.setText(editQty.getText().toString());
//            }
//        });
//        builder.setTitle("팔렛트 이관 등록 창")
//                .setView(view)
//                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        }
//                )
//                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
//                .show();

    }


    @Override
    public void clickConsignee(ActivityPalletResultAdapter.ListViewHolder holder, View v, int position) {
        searchMonth();
       AlertDialog.Builder builder=new AlertDialog.Builder(ActivityPallet.this);
       builder.setTitle(resultLists.get(position).getConsigneeName()+"팔렛트 변경 등록")
               .setMessage("사용등록:")
               .setPositiveButton("사용등록", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               })
               .setNegativeButton("재고 이관", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               })
               .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               })
               .show();
    }

    @Override
    public void clickKpp(ActivityPalletResultAdapter.ListViewHolder holder, View v, int position) {
        getDatabase(yearMonth,resultLists.get(position).getConsigneeName(),"KPP");
    }

    @Override
    public void clickAj(ActivityPalletResultAdapter.ListViewHolder holder, View v, int position) {
        getDatabase(yearMonth,resultLists.get(position).getConsigneeName(),"AJ");
    }

    @Override
    public void clickEtc(ActivityPalletResultAdapter.ListViewHolder holder, View v, int position) {
        getDatabase(yearMonth,resultLists.get(position).getConsigneeName(),"ETC");
    }


}