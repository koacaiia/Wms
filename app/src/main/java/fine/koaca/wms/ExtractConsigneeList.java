package fine.koaca.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ExtractConsigneeList {
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    static private final String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;
    String[] consigneeList;
    String consigneeName;



    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);

    }
    public void extractConsigneeList(Context context){

        database=FirebaseDatabase.getInstance();
        String depotName;
        sharedPref=getSharedPreferences(context);
        editor=sharedPref.edit();
        depotName=sharedPref.getString("depotName","2물류(02010027)");

        Log.i("TestValue","depotName++++:"+depotName);
        Log.i("TestValue","database Value::::"+database.toString());
        switch(depotName){
            case "2물류(02010027)":
                databaseReference=database.getReference("Incargo2");
                break;
            case "1물류(02010810)":
                databaseReference=database.getReference("Incargo1");
                break;
                case "(주)화인통상 창고사업부":
                    databaseReference=database.getReference("Incargo");
                    break;
        }
        Log.i("TestValue","databaseReference Value+++:"+databaseReference.getKey());

       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               ArrayList<String> arrayListConsigneeList;
               arrayListConsigneeList=new ArrayList<>();
               for(DataSnapshot data:snapshot.getChildren()){
                   Fine2IncargoList list=data.getValue(Fine2IncargoList.class);
                   assert list != null;
                   String consigneeName=list.getConsignee();

                   if(!consigneeName.equals("")){
                       if(!arrayListConsigneeList.contains(consigneeName)){
                           arrayListConsigneeList.add(consigneeName);


                       }

                   }
               }
               arrayListConsigneeList.add("Etc");
               consigneeList=arrayListConsigneeList.toArray(new String[arrayListConsigneeList.size()]);

               ConsigneeList mList=new ConsigneeList();
               mList.setConsigneeList_list(consigneeList);

               WorkingMessageData data=new WorkingMessageData();
               data.consigneeList=consigneeList;

               Log.i("Test Value","ConsigneeList constructor Checked::::"+consigneeList.length);
           }



           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }
       });


        }




}
