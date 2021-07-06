package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Arrays;
import java.util.Date;

public class TitleActivity extends AppCompatActivity implements OutCargoListAdapter.OutCargoListAdapterClickListener , Serializable {
    RecyclerView recyclerViewIn;
    RecyclerView recyclerViewOut;
    FirebaseDatabase database;
    DatabaseReference databaseReferenceOut;
    DatabaseReference databaseReferenceIn;
    ArrayList<OutCargoList> listOut;
    ArrayList<OutCargoList> listOutSort;
    ArrayList<Fine2IncargoList> listIn;
    OutCargoListAdapter adapterOut;
    IncargoListAdapter adapterIn;
    String dateToday;

    TextView txtTitle;
    static private final String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    String departmentName,nickName;

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

        dateToday=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        txtTitle=findViewById(R.id.activity_title_txttile);

        txtTitle.setText(dateToday+"일  입,출고 현황 ");

        recyclerViewOut=findViewById(R.id.titleRecyclerOut);
        recyclerViewIn=findViewById(R.id.titleRecyclerOutIn);
        LinearLayoutManager outManager=new LinearLayoutManager(this);
        LinearLayoutManager inManager=new LinearLayoutManager(this);
        recyclerViewOut.setLayoutManager(outManager);
        recyclerViewIn.setLayoutManager(inManager);
        database=FirebaseDatabase.getInstance();

        getFirebaseDataIn();
        getFirebaseDataOut();
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
        adapterOut.notifyDataSetChanged();
        adapterIn.notifyDataSetChanged();
    }

    private void getFirebaseDataOut() {
        listOut=new ArrayList<>();

        ValueEventListener listener=new ValueEventListener() {
            ArrayList<OutCargoList> listOutP=new ArrayList<>();
            ArrayList<OutCargoList> listOutTotal=new ArrayList<>();
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int intTotalPlt = 0,intTotalEa=0,intProgressPlt=0,intProgressEa=0;
                  for(DataSnapshot data:snapshot.getChildren()){
                      OutCargoList mList=data.getValue(OutCargoList.class);
                      assert mList != null;
                      String totalQty=mList.getTotalQty();
                      String workProcess=mList.getWorkprocess();

                      if(workProcess.equals("완")){
                          listOutP.add(mList);
                          if(totalQty.contains("PLT")){
                              intProgressPlt=intProgressPlt+Integer.parseInt(totalQty.substring(0, totalQty.length() - 3));
                          }else{
                              intProgressEa=intProgressEa+ Integer.parseInt(totalQty.substring(0, totalQty.length() - 2));
                          }
                      }else{
                          listOut.add(mList);
                      }
                      if(totalQty.contains("PLT")){
                          intTotalPlt=intTotalPlt+ Integer.parseInt(totalQty.substring(0, totalQty.length() - 3));
                      }else{
                          intTotalEa=intTotalEa+ Integer.parseInt(totalQty.substring(0, totalQty.length() - 2));
                      }
                      listOutTotal.add(mList);

                  }

                  int pCargo,pPlt,pEa,rCargo,rPlt,rEa;

                  pCargo=listOutTotal.size()-listOutP.size();
                  pPlt=intTotalPlt-intProgressPlt;
                  pEa=intTotalEa-intProgressEa;

                  rCargo= (int)(((double) listOutP.size() /(double) listOutTotal.size())*100);
                  rPlt=(int)(((double) intProgressPlt /(double) intTotalPlt)*100);
                  rEa=(int)(((double) intProgressEa /(double) intTotalEa)*100);
                  TextView totalCargo=findViewById(R.id.titleTotalCargo);
                  totalCargo.setText(listOutTotal.size()+"건");
                  TextView proCargo=findViewById(R.id.titleProgressOutcargo);
                  proCargo.setText(pCargo+"건");
                  TextView rateCargo=findViewById(R.id.titleProgressOutcargoRate);
                  rateCargo.setText(rCargo+"%");

                  TextView totalPlt=findViewById(R.id.titleTotalCargoPlt);
                  totalPlt.setText(intTotalPlt+"PLT");
                  TextView proPlt=findViewById(R.id.titleProgressOutcargoPlt);
                  proPlt.setText(pPlt+"PLT");
                  TextView ratePlt=findViewById(R.id.titleProgressOutcargoPltRate);
                  ratePlt.setText(rPlt+"%");

                  TextView totalEa=findViewById(R.id.titleTotalCargoEa);
                  totalEa.setText(intTotalEa+"EA");
                  TextView proEa=findViewById(R.id.titleProgressOutcargoEA);
                  proEa.setText(pEa+"EA");
                  TextView rateEa=findViewById(R.id.titleProgressOutcargoEaRate);
                  rateEa.setText(rEa+"%");


                  adapterOut.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        databaseReferenceOut=database.getReference("Outcargo2");
        Query sortByDateOutcargoData=databaseReferenceOut.orderByChild("date").equalTo(dateToday);
        sortByDateOutcargoData.addListenerForSingleValueEvent(listener);
    }

    private void getFirebaseDataIn() {

        listIn=new ArrayList<>();
        databaseReferenceIn=database.getReference("Incargo2");
        ValueEventListener inListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataIn:snapshot.getChildren()){
                    Fine2IncargoList mListIn=dataIn.getValue(Fine2IncargoList.class);
                    listIn.add(mListIn);
                }
                adapterIn.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        databaseReferenceIn.addListenerForSingleValueEvent(inListener);


    }

    @Override
    public void itemClicked(OutCargoListAdapter.ListView listView, View v, int position) {
        Intent intent=new Intent(this,OutCargoActivity.class);
        intent.putExtra("listOut",listOut);

        startActivity(intent);
    }
}