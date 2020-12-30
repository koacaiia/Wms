package fine.koaca.wms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Exercise extends AppCompatActivity {
    RecyclerView exr_recyclerView;
    fine.koaca.wms.IncargoListAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Fine2IncargoList> listItems=new ArrayList<Fine2IncargoList>();

    ArrayList<String> list_consignee=null;

    static private String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPref=null;
    static SharedPreferences.Editor editor=null;
    TextView exr_text;
    Incargo inCargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__exercise);

        exr_text=findViewById(R.id.exr_text);
        inCargo=new Incargo(listItems);
        listItems=inCargo.listItems;


        exr_recyclerView=findViewById(R.id.exr_recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        exr_recyclerView.setLayoutManager(layoutManager);
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Incargo");
        getFirebaseDatabase();

        adapter=new fine.koaca.wms.IncargoListAdapter(listItems,this);
        exr_recyclerView.setAdapter(adapter);
//        Toast.makeText(this, String.valueOf(listItems1.size()), Toast.LENGTH_SHORT).show();
//        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
//        dialog.setTitle("koaca");
//        dialog.show();

        sharedPref=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        editor= sharedPref.edit();
    }

    public void btnClick(View view){
        int btnName=view.getId();
        switch(btnName){
            case R.id.exr_save:

                saveData();
                break;
            case R.id.exr_update:
                updateData();
                break;
            case R.id.exr_delete:
                deleteData();
                break;
            case R.id.exr_clear:
                editor.clear();
                editor.commit();
                break;
            default:
        }
        listData();
    }

    private void listData() {
        String dataList="";
        Map<String,?> totalvalue=sharedPref.getAll();
        for(Map.Entry<String,?>entry: totalvalue.entrySet()){
            dataList +=entry.getKey().toString()+":"+entry.getValue().toString()+"\r\n";
            Log.d("share:",entry.getKey()+":"+entry.getValue());
        }
//        exr_text.setText(dataList);
    }

    private void deleteData() {
        editor.remove("nValue");
        editor.commit();
    }

    public void updateData() {

        editor.putBoolean("isShare",false);
        editor.putFloat("fRate",3.33f);
        editor.putInt("nValue",5000);
        editor.putString("name","copycoding.tistory");
        editor.apply();
    }

    public void saveData() {
        editor.putBoolean("isShare",true);
        editor.putFloat("fRate",1.33f);
        editor.putInt("nValue",100);
        editor.putString("name","copycoding");

        editor.apply();

    }

//    public void saveDepotData(String depotName){
//        editor.putString("depotSortname",depotName);
//        editor.apply();
//    }

    public void getFirebaseDatabase(){

        list_consignee=new ArrayList<String>();
        ArrayList<String> list_container=new ArrayList<String>();

        ValueEventListener sortListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                ArrayList<String > arr_containerName=new ArrayList<String>();
                ArrayList<Activity_Exercise_list> filter_list=new ArrayList<Activity_Exercise_list>();

                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Fine2IncargoList data=snapshot1.getValue(Fine2IncargoList.class);
                    Activity_Exercise_list filterdata=snapshot1.getValue(Activity_Exercise_list.class);
                    String containerName=data.getContainer();
                    arr_containerName.add(containerName);
                    filter_list.add(filterdata);
                    listItems.add(data);
                }
                String[] container_list=arr_containerName.toArray(new String[arr_containerName.size()]);
                arr_containerName.clear();
                for(String item:container_list){
                    if(! arr_containerName.contains(item))
                    arr_containerName.add(item);
                               }
//                listItems.clear();
//                String array_container_list= Arrays.toString(container_list);
//                Log.i("listsize1",String.valueOf(listItems.size()));
//                for(DataSnapshot snapshot2:snapshot.getChildren()){
//                    Fine2IncargoList data=snapshot2.getValue(Fine2IncargoList.class);
////                        if(! data.getContainer().contains(array_container_list))
//                    if(!listItems.contains(array_container_list))
//                    listItems.add(data);
//                }

                int listItemsSize=listItems.size();
                for(int i=0;i<listItems.size()-2;i++){
                    String container20=listItems.get(i+1).getContainer20();
                    String container40=listItems.get(i+1).getContainer40();
                    String lclcargo=listItems.get(i+1).getLclcargo();
                    String containerName3=listItems.get(i).getContainer();
                    String containerName1=listItems.get(i+1).getContainer();
                    String containerName2=listItems.get(i+2).getContainer();

                    if(container20.equals("0")&&container40.equals("0")&&lclcargo.equals("0")){
                        listItems.remove(i+1);
                    }
                    if(containerName3.equals(containerName1)||containerName2.equals(containerName1)){
                        listItems.remove(i+1);
                    }



                }

                adapter.notifyDataSetChanged();
                Log.i("listSize2",String.valueOf(listItems.size()));
                Log.i("listSize3",String.valueOf(arr_containerName.size()));
//                Log.i("listArr",array_container_list);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        databaseReference.addListenerForSingleValueEvent(sortListener);
        String[] arr_consignee=list_consignee.toArray(new String[list_consignee.size()]);
//        exr_text.setText(arr_consignee);
        int list_consignee_init=0;
//       for(String str:list_consignee){
//           exr_text.append(str+",");
//
//        }



//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listItems.clear();
//                for(DataSnapshot snapshot1:snapshot.getChildren()){
//                    Fine2IncargoList data=snapshot1.getValue(Fine2IncargoList.class);
//                    listItems.add(data);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }
    public static class Activity_Exercise_list{
        String list_containerName;
        String list_container40;
        String list_container20;
        String list_consignee;
        public Activity_Exercise_list(){

        }

        public Activity_Exercise_list(String list_containerName, String list_container40, String list_container20,String list_consignee) {
            this.list_containerName = list_containerName;
            this.list_container40 = list_container40;
            this.list_container20 = list_container20;
            this.list_consignee = list_consignee;
        }


        public String getList_containerName() {
            return list_containerName;
        }

        public void setList_containerName(String list_containerName) {
            this.list_containerName = list_containerName;
        }

        public String getList_container40() {
            return list_container40;
        }

        public void setList_container40(String list_container40) {
            this.list_container40 = list_container40;
        }

        public String getList_container20() {
            return list_container20;
        }

        public void setList_container20(String list_container20) {
            this.list_container20 = list_container20;
        }

        public String getList_consignee() {
            return list_consignee;
        }

        public void setList_consignee(String list_consignee) {
            this.list_consignee = list_consignee;
        }
        @Exclude
        public Map<String,Object> toMap(){
            HashMap<String,Object> result=new HashMap<>();

            result.put("container",list_containerName);
            result.put("container40",list_container40);
            result.put("container20",list_container20);
            result.put("consignee",list_consignee);
            return result;
        }

    }
}
