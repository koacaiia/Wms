package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityEquipFacility extends AppCompatActivity implements ImageViewActivityAdapter.ImageViewClicked, ActivityEquipFacilityAdapter.ActivityEquipFacilityAdapterClicked{
FirebaseDatabase database;
DatabaseReference databaseReference;
PublicMethod publicMethod;
ArrayList<String> imageViewLists=new ArrayList<>();
RecyclerView imageRecyclerView,recyclerViewHistory;
ImageViewActivityAdapter iAdapter;
ArrayList<ActivityEquipFacilityList> list;
ActivityEquipFacilityAdapter historyAdapter;

String refPath;

ArrayList<String> nameList;

String deptName,nickName,date;
Button btnSearch,btnRegAndSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equip_facility);
        database=FirebaseDatabase.getInstance();
        publicMethod=new PublicMethod(this);
        deptName=publicMethod.getUserInformation().get("deptName");
        nickName=publicMethod.getUserInformation().get("nickName");
        date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        refPath="DeptName/"+deptName+"/EquipNFacility/";

        recyclerViewHistory=findViewById(R.id.activity_equip_facility_recyclerhistory);
        LinearLayoutManager historyManager=new LinearLayoutManager(this);
        recyclerViewHistory.setLayoutManager(historyManager);
        list=new ArrayList<>();
        getHistoryDatabase("ALL");

        historyAdapter=new ActivityEquipFacilityAdapter(list,this);
        recyclerViewHistory.setAdapter(historyAdapter);


        btnRegAndSearch=findViewById(R.id.activity_equip_facility_btnActivityReg);
        btnRegAndSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ActivityEquipFacility.this,ActivityEquipNFacilityPutData.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityEquipFacility.this.startActivity(intent);

            }
        });
        btnRegAndSearch.setOnLongClickListener(new View.OnLongClickListener() {
                                                   @Override
                                                   public boolean onLongClick(View v) {
                                                       searchData();
                                                       return true;
                                                   }
                                               }
        );




    }

    private void searchData() {
        nameList=new ArrayList<>();
        databaseReference=database.getReference(refPath);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    ActivityEquipFacilityList mList=data.getValue(ActivityEquipFacilityList.class);
                            String efName=mList.getName();
                    if(!nameList.contains(efName)){
                        nameList.add(efName);
                    }
                }
                dialogSearchData(nameList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dialogSearchData(ArrayList<String> nameList) {
        String[] list=nameList.toArray(new String[nameList.size()]);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("항목선택창")
                .setSingleChoiceItems(list,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String efName=list[which];
                        getHistoryDatabase(efName);
                        dialog.cancel();
                    }
                })
                .show();

    }


    private void getHistoryDatabase(String efName) {
        list.clear();
        databaseReference=database.getReference(refPath);
        ValueEventListener listener=new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    ActivityEquipFacilityList mList=data.getValue(ActivityEquipFacilityList.class);
                    if(mList.getAskDate()!=null){
                        list.add(mList);
                    }

//                    String strDateSub=mList.getAskDate().replaceAll("-","");
//                    int intDateSub=Integer.parseInt(strDateSub);
//                    Log.i("TestValue","dateSub Value:::"+intDateSub);

                }
                historyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        if(!efName.equals("ALL")){
            Query sortByefName=databaseReference.orderByChild("name").equalTo(efName);
            sortByefName.addListenerForSingleValueEvent(listener);
        }else{
            databaseReference.addListenerForSingleValueEvent(listener);
        }

    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {

    }


    @Override
    public void itemClick(ActivityEquipFacilityAdapter.ListViewHolder listViewHolder, View v, int position) {

        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityEquipFacility.this);
        ArrayList<String> contentValue=new ArrayList<>();
        contentValue.add("견적결제 등록");
        contentValue.add("점검진행 승인 등록");
        contentValue.add("작업완료 등록");
        contentValue.add("계산서 지출결의서 결재 등록");
        contentValue.add("관련 사진 검색");
        contentValue.add("점검요청일 변경");
        contentValue.add("비고사항 등록");

        String name=list.get(position).getName();
        String date=list.get(position).getAskDate();
        String content=list.get(position).getContent();
        String keyValue=list.get(position).getKeyValue();


        DatabaseReference databaseReference=database.getReference("DeptName/"+deptName+"/EquipNFacility");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                   String dataKeyPath=data.getKey();
                   if(dataKeyPath.equals(keyValue)){
                       ActivityEquipFacilityList mList=data.getValue(ActivityEquipFacilityList.class);
                       list.add(mList);
                   }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String[] contentValueList=contentValue.toArray(new String[contentValue.size()]);

        builder.setTitle(name+"_"+content+" Update")

                .setSingleChoiceItems(contentValueList,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        DatabaseReference dataRef=database.getReference("DeptName/"+deptName+"/EquipNFacility/"+keyValue);
                        Map<String,Object> value=new HashMap<>();
                        switch(which){
                            case 0:case 3:

                                View view=getLayoutInflater().inflate(R.layout.dialog_datepicker_equipfacility,null);
                                EditText editText=view.findViewById(R.id.dialog_datepicker_equipfacility_editText);
                                TextView textViewDate=view.findViewById(R.id.dialog_datepicker_equipfacility_date);
                                Button btnViewAmount=view.findViewById(R.id.dialog_datepicker_equipfacility_amount);
                                btnViewAmount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    btnViewAmount.setText(editText.getText().toString()+" 원");
                                    }
                                });
                                DatePicker datePicker=view.findViewById(R.id.dialog_datepicker_equipfacility_datePicker);
                                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                    @Override
                                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        String month,day;
                                        if(monthOfYear<9){
                                            month="0"+(monthOfYear+1);
                                        }else{
                                            month=String.valueOf(monthOfYear+1);
                                        }

                                        if(dayOfMonth<10){
                                            day="0"+dayOfMonth;
                                        }else{
                                            day=String.valueOf(dayOfMonth);
                                        }
                                        textViewDate.setText(year+"-"+month+"-"+day);
                                    }
                                });
                                switch(which){
                                    case 0:
                                        builder.setTitle("견적진행 금액 입력 창")
                                                .setMessage("견적진행 금액을 입력 바랍니다.")
                                                .setView(view)
                                                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                                    @SuppressLint("NotifyDataSetChanged")
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(textViewDate.getText().toString().equals("")){
                                                            Toast.makeText(ActivityEquipFacility.this,"날짜 지정 확인후 다시 진행 바랍니다.",
                                                                    Toast.LENGTH_SHORT).show();

                                                        }else{
                                                            value.put("estAmountDate",textViewDate.getText().toString());
                                                            value.put("estAmount",
                                                                    Integer.parseInt(editText.getText().toString()));
                                                            dataRef.updateChildren(value);
                                                           getChangedData(keyValue);
                                                        }
                                                    }
                                                }).
                                                setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).
                                                show();
                                        break;
                                    case 3:
                                        builder.setTitle("계산서 발행 금액 입력 창")
                                                .setMessage("계산서 발행 금액 입력 바랍니다.")
                                                .setView(view)
                                                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                                    @SuppressLint("NotifyDataSetChanged")
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(textViewDate.getText().toString().equals("")){
                                                            Toast.makeText(ActivityEquipFacility.this,"날짜 지정 확인후 다시 진행 바랍니다.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            value.put("conAmountDate",textViewDate.getText().toString());
                                                            value.put("conAmount",
                                                                    Integer.parseInt(editText.getText().toString()));
                                                            dataRef.updateChildren(value);
                                                          getChangedData(keyValue);
                                                        }
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

                                break;
                            case 1:
                                DatePicker datePickerConfirm=new DatePicker(ActivityEquipFacility.this);
                                final String[] confirmDate=new String[1];
                                datePickerConfirm.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                    @Override
                                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            String month,day;
                                            if(monthOfYear<9){
                                                month="0"+(monthOfYear+1);
                                            }else{
                                                month=String.valueOf(monthOfYear+1);
                                            }
                                            if(dayOfMonth<10){
                                                day="0"+dayOfMonth;
                                            }else{
                                                day=String.valueOf(dayOfMonth);
                                            }
                                            confirmDate[0]=year+"-"+month+"-"+day;
                                        Toast.makeText(ActivityEquipFacility.this,confirmDate[0]+" 등록일 선택",Toast.LENGTH_SHORT).show();

                                    }
                                }
                                );
                                builder.setTitle("점검진행 승인일 등록")
                                        .setMessage("점검진행 승인일 확인후 날짜 등록 바랍니다.")
                                        .setView(datePickerConfirm)
                                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                AlertDialog.Builder builderRepair=new AlertDialog.Builder(ActivityEquipFacility.this);
                                                builderRepair.setTitle("점검승인일 확인창")
                                                        .setMessage("점검승인일: "+confirmDate[0]+"\n"+"점검승인일 확인후 하단 등록버튼 눌러 서버등록 " +
                                                                "바랍니다.")
                                                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                value.put("confirmDate",confirmDate[0]);
                                                                dataRef.updateChildren(value);
                                                                Toast.makeText(ActivityEquipFacility.this,
                                                                        "점검 승인일 : "+confirmDate[0]+
                                                                        " 로 서버등록 되었습니다.",Toast.LENGTH_SHORT).show();
                                                                getChangedData(keyValue);
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
                            case 2:
                           DatePicker datePickerRepair=new DatePicker(ActivityEquipFacility.this);
                                final String[] repairDate = new String[1];
                           datePickerRepair.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                               @Override
                               public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                   String month,day;
                                   if(monthOfYear<9){
                                       month="0"+(monthOfYear+1);
                                   }else{
                                       month=String.valueOf(monthOfYear+1);
                                   }

                                   if(dayOfMonth<10){
                                       day="0"+dayOfMonth;
                                   }else{
                                       day=String.valueOf(dayOfMonth);
                                   }
                                 repairDate[0] =year+"-"+month+"-"+day;
                                   Toast.makeText(ActivityEquipFacility.this,repairDate[0]+" 등록일 선택",Toast.LENGTH_SHORT).show();

                               }
                           });
                           builder.setTitle("점검진행일 등록 창")
                                   .setMessage("작업완료일 확인후 날짜 등록 바랍니다.")
                                   .setView(datePickerRepair)
                                   .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           AlertDialog.Builder builderRepair=new AlertDialog.Builder(ActivityEquipFacility.this);
                                           builderRepair.setTitle("작업완료일 확인창")
                                                   .setMessage("작업완료일: "+repairDate[0]+"\n"+"작업완료일 확인후 하단 등록버튼 눌러 서버등록 바랍니다.")
                                                   .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           value.put("repairDate",repairDate[0]);
                                                           dataRef.updateChildren(value);
                                                           Toast.makeText(ActivityEquipFacility.this,"작업 완료일 : "+repairDate[0]+
                                                                   " 로 서버등록 되었습니다.",Toast.LENGTH_SHORT).show();
                                                           getChangedData(keyValue);
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
                            case 4:
                                itemPictureList(name,keyValue);
                                break;
                            case 5:
                                DatePicker datePickerAskDate=new DatePicker(ActivityEquipFacility.this);
                                String[] dateAsk=new String[1];
                                datePickerAskDate.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                    @Override
                                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        String month,day;
                                        if(monthOfYear<9){
                                            month="0"+(monthOfYear+1);
                                        }else{
                                            month=String.valueOf(monthOfYear);
                                        }
                                        if(dayOfMonth<10){
                                            day="0"+dayOfMonth;
                                        }else{
                                            day=String.valueOf(dayOfMonth);
                                        }
                                        dateAsk[0]=year+"-"+month+"-"+day;
                                        Toast.makeText(ActivityEquipFacility.this,dateAsk[0]+" 등록일 선택",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.setTitle("점검요청일 변경창")
                                        .setMessage("변경된 요청일 확인후 하단 등록 버튼으로 서버등록 진행 바랍니다.")
                                        .setView(datePickerAskDate)
                                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                AlertDialog.Builder builderAsk=
                                                        new AlertDialog.Builder(ActivityEquipFacility.this);
                                                builderAsk.setTitle("점검요청일 변경 확인창")
                                                        .setMessage("점검요청일: "+dateAsk[0]+"\n"+"변경된 점검 요청일 확인후 하단 등록버튼 눌러 서버등록 " +
                                                                "바랍니다.")
                                                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                value.put("askDate",dateAsk[0]);
                                                                dataRef.updateChildren(value);
                                                                Toast.makeText(ActivityEquipFacility.this,"작업 완료일 : "+dateAsk[0]+
                                                                        " 로 서버등록 되었습니다.",Toast.LENGTH_SHORT).show();
                                                                getChangedData(keyValue);
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
                    }
                })
                .show();
    }

    private void getChangedData(String keyValue) {
        list.clear();
        DatabaseReference dataRef=database.getReference("DeptName/"+deptName+"/EquipNFacility/"+keyValue);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ActivityEquipFacilityList mList=snapshot.getValue(ActivityEquipFacilityList.class);
                list.add(mList);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void itemPictureList(String name,String keyValue) {
        imageViewLists.clear();
        RecyclerView imageRecycler=findViewById(R.id.activity_equip_facility_recyclePickedItem);
        GridLayoutManager manager=new GridLayoutManager(this,3);
        imageRecycler.setLayoutManager(manager);

        FirebaseStorage storage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
        StorageReference storageReference=storage.getReference(deptName+"/장비_시설물/"+name+"/"+keyValue);
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item:listResult.getItems()){
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                       imageViewLists.add(uri.toString());
                       iAdapter=new ImageViewActivityAdapter(imageViewLists);

                       if(imageViewLists.size()==listResult.getItems().size()){
                           imageRecycler.setAdapter(iAdapter);
                           iAdapter.notifyDataSetChanged();
                       }
                            iAdapter.clickListener= new ImageViewActivityAdapter.ImageViewClicked() {
                                @Override
                                public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
                                    publicMethod=new PublicMethod(ActivityEquipFacility.this);
                                    publicMethod.adapterPictureSavedMethod(imageViewLists.get(position));
                                }
                            };
                        }
                    });
                }

            }
        });

    }
    @Override
    public void onBackPressed() {

        PublicMethod publicMethod=new PublicMethod(ActivityEquipFacility.this);
        publicMethod.intentSelect();
    }
}