package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityWeekSchedule extends AppCompatActivity {
    RecyclerView recyclerViewWeekSchedule;
    FirebaseDatabase firebaseDatabase;
    ArrayList<ListWeekSchedule> listWeekSchedule;
    String refPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_schedule);
        recyclerViewWeekSchedule=findViewById(R.id.activity_week_schedule_RecyclerView);
        PublicMethod publicMethod=new PublicMethod(this);

        String deptName=publicMethod.getUserInformation().get("deptName");
        String nickName=publicMethod.getUserInformation().get("nickName");
        String dateMonth=new SimpleDateFormat("MM").format(new Date());
        Log.i("TestValue","Date Value="+dateMonth+"deptName///"+deptName+"/"+nickName);
        refPath="DeptName/"+deptName+"/InCargo/"+dateMonth+"월";
        listWeekSchedule=new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        for(int i=1;i<32;i++){
            String day;
            if(i<10){
                day="0"+i;
            }else{
                day=String.valueOf(i);
            }
            DatabaseReference databaseReference=firebaseDatabase.getReference(refPath+"/2022-"+dateMonth+"-"+day);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data:snapshot.getChildren()){
                        if(!data.getKey().contains("json")){
                            ListWeekSchedule mData=data.getValue(ListWeekSchedule.class);
                            listWeekSchedule.add(mData);
                            Log.i("TestValue","List Value="+mData.getConsignee()+listWeekSchedule.size());
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }
}