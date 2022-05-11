package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdapterActivityWeekScheduleRecyclerViewEx extends RecyclerView.Adapter<AdapterActivityWeekScheduleRecyclerViewEx.ListViewHolder>{
ArrayList<ListWeekSchedule> listWeekSchedule;
ArrayList<String> dateList;
Context context;
ArrayList<String> consigneeList;
Activity activity;
PublicMethod publicMethod;
FirebaseDatabase database;
    public AdapterActivityWeekScheduleRecyclerViewEx(ArrayList<ListWeekSchedule> listWeekSchedule,
                                                     ArrayList<String> dateList,ArrayList<String> consigneeList,Context context
            ,Activity activity) {
        this.listWeekSchedule = listWeekSchedule;
        this.dateList=dateList;
        this.context=context;
        this.consigneeList=consigneeList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_weekschedule_ex,parent,false);
        return new ListViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        String strDate=dateList.get(position);
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date date=null;
        try {
            date=dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);

        String dayOfTheWeek=calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.SHORT, Locale.KOREAN);

        holder.date.setText(strDate+"("+dayOfTheWeek+")");
        ArrayList<ListWeekSchedule> listWeekScheduleRe=new ArrayList<>();
        AdapterActivityWeekScheduleRecyclerViewIn adapter;
        int consigneeListSize=consigneeList.size();
        for(int i=0;i<consigneeListSize;i++){
            int cont20=0;
            int cont40=0;
            int cargo=0;
            for(int j=0;j<listWeekSchedule.size();j++){
                String consigneeName=listWeekSchedule.get(j).getConsignee();

                if(listWeekSchedule.get(j).getDate().equals(strDate)&&consigneeList.get(i).contains(consigneeName)){
                  cont20=cont20+Integer.parseInt(listWeekSchedule.get(j).getContainer20());
                  cont40=cont40+Integer.parseInt(listWeekSchedule.get(j).getContainer40());
                  cargo=cargo+Integer.parseInt(listWeekSchedule.get(j).getLclcargo());
                }
            }
            if(cont20+cont40+cargo!=0){
                ListWeekSchedule list=new ListWeekSchedule(consigneeList.get(i),String.valueOf(cont40),String.valueOf(cont20),
                        String.valueOf(cargo),
                        dateList.get(position),"","");
                listWeekScheduleRe.add(list);
            }
        }
        adapter=new AdapterActivityWeekScheduleRecyclerViewIn(listWeekScheduleRe);
        LinearLayoutManager manager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        holder.recyclerViewSchedule.setLayoutManager(manager);
        holder.recyclerViewSchedule.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        int total40=0;
        int total20=0;
        int totalCargo=0;
        for(int listWeekScheduleSize=0;listWeekScheduleSize<listWeekScheduleRe.size();listWeekScheduleSize++){
            total40=total40+Integer.parseInt(listWeekScheduleRe.get(listWeekScheduleSize).getContainer40());
            total20=total20+Integer.parseInt(listWeekScheduleRe.get(listWeekScheduleSize).getContainer20());
            totalCargo=totalCargo+Integer.parseInt(listWeekScheduleRe.get(listWeekScheduleSize).getLclcargo());
        }
        holder.txt40Ft.setText("40FT: "+total40);
        holder.txt20Ft.setText("20FT: "+total20);
        holder.txtCargo.setText("Cargo :"+totalCargo);
        publicMethod=new PublicMethod(activity);
        String depotName=publicMethod.getUserInformation().get("deptName");
        String strRef="DeptName/"+depotName+"/WorkingStaffSchedule";
        database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference(strRef+"/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    if(data.getKey().equals(strDate)){
                        OutSourcingSchedule outsourcingSchedule=data.getValue(OutSourcingSchedule.class);
                        if(outsourcingSchedule.getMale()!=null) {
                            String male=outsourcingSchedule.getMale();
                            holder.outSourcingMale.setText(male);
                        }
                        if(outsourcingSchedule.getFemale()!=null){
                            String female=outsourcingSchedule.getFemale();
                            holder.outSourcingFemale.setText(female);
                        }

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        Button outSourcingMale;
        Button outSourcingFemale;
        RecyclerView recyclerViewSchedule;
        TextView txt40Ft,txt20Ft,txtCargo;
        String putDate;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.list_weekschedule_ex_date);
            outSourcingMale=itemView.findViewById(R.id.list_weekschedule_ex_outsourcingMale);
            outSourcingFemale=itemView.findViewById(R.id.list_weekschedule_ex_outsourcingFemale);
            recyclerViewSchedule=itemView.findViewById(R.id.list_weekschedule_ex_scheduleRecyclerView);
            txt40Ft=itemView.findViewById(R.id.list_weekschedule_ex_40Ft);
            txt20Ft=itemView.findViewById(R.id.list_weekschedule_ex_20Ft);
            txtCargo=itemView.findViewById(R.id.list_weekschedule_ex_Cargo);
//            View.OnClickListener listener=new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent=new Intent(context,ActivityWorkingStaff.class);
//                    context.startActivity(intent);
//                }
//            };

            outSourcingMale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    putDate=dateList.get(getAdapterPosition());
                    putOutSourcingData("Male",putDate);

                }
            });
            outSourcingFemale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    putDate=dateList.get(getAdapterPosition());
                    putOutSourcingData("Female",putDate);
                }
            });

        }
        public void putOutSourcingData(String gender,String date){

            publicMethod=new PublicMethod(activity);
            String depotName=publicMethod.getUserInformation().get("deptName");
            database= FirebaseDatabase.getInstance();
            String strRef="DeptName/"+depotName+"/WorkingStaffSchedule";
            Toast.makeText(context,"인원등록은 우선은 The_Job 로 업체 등록 됩니다."+"\n"+"별도의 업체로 등록시에는 근태,출근 창에서 등록 바랍니다.",Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            EditText editText=new EditText(context);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setTitle(gender+"  인원 등록 창")
                    .setView(editText)
                    .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           DatabaseReference databaseReference=database.getReference(strRef+"/"+date);
                           Map<String,Object> map=new HashMap<>();
                           String count=String.valueOf(editText.getText());
                           map.put(gender,count);
                           databaseReference.updateChildren(map);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();

        }
    }

    private static class OutSourcingSchedule {
        String Male;
        String Female;
        public OutSourcingSchedule(){

        }

        public OutSourcingSchedule(String male, String female) {
           this.Male = male;
            this.Female = female;
        }

        public String getMale() {
            return Male;
        }

        public void setMale(String male) {
            Male = male;
        }

        public String getFemale() {
            return Female;
        }

        public void setFemale(String female) {
            Female = female;
        }
    }
}
