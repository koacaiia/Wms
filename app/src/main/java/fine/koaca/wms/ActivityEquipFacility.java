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
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
                    list.add(mList);
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
        contentValue.add("작업완료 등록");
        contentValue.add("계산서 지출결의서 결재 등록");
        contentValue.add("관련 사진 검색");

        String name=list.get(position).getName();
        String date=list.get(position).getAskDate();
        String content=list.get(position).getContent();

        String keyPath=date+"_"+name+"_"+content;
        DatabaseReference databaseReference=database.getReference("DeptName/"+deptName+"/EquipNFacility");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                   String dataKeyPath=data.getKey().substring(0,data.getKey().length()-5);
                   if(dataKeyPath.equals(keyPath)){
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
                        switch(which){
                            case 0:
                                break;
                            case 3:
                                itemPictureList(name,keyPath);
                                break;
                        }
                    }
                })
                .show();
    }

    private void itemPictureList(String name,String keyValue) {
        imageViewLists.clear();
        RecyclerView imageRecycler=findViewById(R.id.activity_equip_facility_recyclePickedItem);
        GridLayoutManager manager=new GridLayoutManager(this,3);
        imageRecycler.setLayoutManager(manager);

        FirebaseStorage storage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
//        StorageReference storageReference=storage.getReference(deptName+"/"+keyValue+"/장비_시설물/");
        StorageReference storageReference=storage.getReference("장비_시설물/"+name+"/"+keyValue);
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item:listResult.getItems()){
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                       imageViewLists.add(uri.toString());
                       Log.i("TestValue","list Value:::"+uri.toString());


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