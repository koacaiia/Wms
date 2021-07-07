package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TitleActivity extends AppCompatActivity implements OutCargoListAdapter.OutCargoListAdapterClickListener , Serializable {
    RecyclerView recyclerViewIn;
    RecyclerView recyclerViewOut;
    FirebaseDatabase database;
    DatabaseReference databaseReferenceOut;
    DatabaseReference databaseReferenceIn;
    ArrayList<OutCargoList> listOut=new ArrayList<>();
    ArrayList<OutCargoList> listOutSort;
    ArrayList<Fine2IncargoList> listIn=new ArrayList<>();
    OutCargoListAdapter adapterOut;
    IncargoListAdapter adapterIn;
    String dateToday;

    TextView txtTitle;
    static private final String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    String departmentName,nickName,wareHouseDepot,alertDepot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

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
                wareHouseDepot="Incargo2";
                alertDepot="Depot2";
                break;
            case "1물류(02010810)":
                databaseReferenceIn=database.getReference("Incargo1");
                wareHouseDepot="Incargo1";
                alertDepot="Depot1";

                break;
            case "(주)화인통상 창고사업부":
                databaseReferenceIn=database.getReference("Incargo");
                wareHouseDepot="Incargo";
                alertDepot="Depot";
                break;
        }

        dateToday=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        txtTitle=findViewById(R.id.activity_title_txttile);

        txtTitle.setText(dateToday+"일  입,출고 현황 ");

        recyclerViewOut=findViewById(R.id.titleRecyclerOut);
        recyclerViewIn=findViewById(R.id.titleRecyclerOutIn);
        LinearLayoutManager outManager=new LinearLayoutManager(this);
        LinearLayoutManager inManager=new LinearLayoutManager(this);
        recyclerViewOut.setLayoutManager(outManager);
        recyclerViewIn.setLayoutManager(inManager);

        titleDialog();
//        getFirebaseDataIn();
//        getFirebaseDataOut();
        adapterOut=new OutCargoListAdapter(listOut,this,this);
        adapterIn=new IncargoListAdapter(listIn,this);

        adapterIn.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
                Intent intent=new Intent(getApplicationContext(),Incargo.class);
                startActivity(intent);
            }
        });
        recyclerViewOut.setAdapter(adapterOut);
        recyclerViewIn.setAdapter(adapterIn);



    }

    public void titleDialog() {
        AlertDialog.Builder titleBuilder=new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.title_dialog,null);
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

                TextView totalPlt=view.findViewById(R.id.dialog_titleTotalCargoPlt);
                totalPlt.setText(intTotalPlt+"PLT");
                TextView proPlt=view.findViewById(R.id.dialog_titleProgressOutcargoPlt);
                proPlt.setText(pPlt+"PLT");
                TextView ratePlt=view.findViewById(R.id.dialog_titleProgressOutcargoPltRate);
                ratePlt.setText(rPlt+"%");

                TextView totalEa=view.findViewById(R.id.dialog_titleTotalCargoEa);
                totalEa.setText(intTotalEa+"EA");
                TextView proEa=view.findViewById(R.id.dialog_titleProgressOutcargoEA);
                proEa.setText(pEa+"EA");
                TextView rateEa=view.findViewById(R.id.dialog_titleProgressOutcargoEaRate);
                rateEa.setText(rEa+"%");
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
            int con40,con20,cargo,qty,totalTeu,proIncargo,proIncargoW,proIncargoI,proIncargoC,eCon40,eCon20;


            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataIn:snapshot.getChildren()){
                    Fine2IncargoList mListIn=dataIn.getValue(Fine2IncargoList.class);
                    listIn.add(mListIn);
                    eCon40=Integer.parseInt(mListIn.getContainer40());
                    eCon20=Integer.parseInt(mListIn.getContainer20());
                    con40=con40+eCon40;
                    con20=con20+eCon20;
                    cargo=cargo+Integer.parseInt(mListIn.getLclcargo());
                    qty=qty+Integer.parseInt(mListIn.getIncargo());
                    String workP=mListIn.getWorking();
                    switch(workP){
                        case "컨테이너 진입":
                            proIncargoC=proIncargoC+(eCon40*2)+eCon20;
                            break;
                        case "입고작업":
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
                rateIncargo.setText(proIncargo+"%");
                rateIncargoI.setText(proIncargoI+"%");
                rateIncargoW.setText(proIncargoW+"%");
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
        databaseReferenceOut=database.getReference("Outcargo2");
        Query sortByDateOutcargoData=databaseReferenceOut.orderByChild("date").equalTo(dateToday);
        sortByDateOutcargoData.addListenerForSingleValueEvent(outListener);

        Query sortByDateIncargoData=databaseReferenceIn.orderByChild("date").equalTo(dateToday);
        sortByDateIncargoData.addListenerForSingleValueEvent(inListener);


        titleBuilder.setView(view)
                .setPositiveButton("세부입고 현황", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getApplicationContext(),Incargo.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("세부출고 현황", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getApplicationContext(),OutCargoActivity.class);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("현황 확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }



    @Override
    public void itemClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        Intent intent=new Intent(this,OutCargoActivity.class);
        intent.putExtra("listOut",listOut);
        startActivity(intent);
    }
}