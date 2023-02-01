package fine.koaca.wms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentInit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInit extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btnConIn,btnDevCom,btnInsCom,btnIncargoCom,btnRegPallet,btnPicCount;

    public FragmentInit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentInit.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentInit newInstance(String param1, String param2) {
        FragmentInit fragment = new FragmentInit();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= getLayoutInflater().inflate(R.layout.fragment_init,container,false);
        PublicMethod publicMethod = new PublicMethod(getActivity());
        String deptName=publicMethod.getUserInformation().get("deptName");
        String nickName = publicMethod.getUserInformation().get("nickName");
        String listImageViewCount = publicMethod.getUserInformation().get("imageViewListCount");
        String keyValue = null;
        if(getActivity()!=null){
            keyValue= ((Incargo)getActivity()).keyValue;
        }
        String consigneeNameValue=((Incargo)getActivity()).consigneeNameF;
        String blValue= ((Incargo)getActivity()).blF;
        String containerValue= ((Incargo)getActivity()).containerF;
        String dateValue=((Incargo)getActivity()).dateF;
        Log.i("FragmentInit Value",
                "keyValue:"+keyValue+"\n"+"consignee:"+consigneeNameValue+"\n" +"BlValue:"+blValue+"\n"+"containerValue:"+containerValue+"\n"+"dateValue:"+dateValue);
        String itemClickTitle=
                consigneeNameValue + "_비엘:" + blValue + "_컨테이너:" + containerValue;
        ((Incargo)getActivity()).selectedItemGetDatabase(dateValue, keyValue);

//        pickedUpItemClickDialog(keyValue, updateTitleValue, consigneeName);


        FirebaseDatabase itemClickDatabase=FirebaseDatabase.getInstance();
        String refPath=
                "DeptName/" + deptName + "/" + "InCargo" + "/" + keyValue.substring(5, 7) + "월/" + keyValue.substring(0,10) +
                        "/" + keyValue;
        DatabaseReference itemClickReference=itemClickDatabase.getReference(refPath);
        Map<String,Object> itemClickMap=new HashMap<>();


//

        btnPicCount=view.findViewById(R.id.fragmentInit_regRemark);
        btnConIn= view.findViewById(R.id.fragmentInit_btnConIn);
        btnIncargoCom = view.findViewById(R.id.fragmentInit_btnIncargoCom);
        btnInsCom = view.findViewById(R.id.fragmentInit_btnInsCom);
        btnDevCom = view.findViewById(R.id.fragmentInit_btnDevCom);
        btnRegPallet= view.findViewById(R.id.fragmentInit_btnRegPallet);
        btnConIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickMap.put("working","컨테이너 진입");
                itemClickReference.updateChildren(itemClickMap);
                Toast.makeText(getActivity(),itemClickTitle+"\n"+"화물 컨테이너 진입으로 서버 등록 되었습니다.",Toast.LENGTH_LONG).show();
                ((Incargo)getActivity()).initIntent();
            }
        });

        btnDevCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("입고작업 완료+컨테이너 회수요청 선택창")
                        .setMessage(itemClickTitle+" 화물정보"+"\n" +" 에 대한 컨테이너 회수가 필요하면 하단 컨테이너 회수 요청 버튼으로 알림등록 바랍니다.")
                        .setPositiveButton("입고작업 완료+컨테이너 회수요청 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickMap.put("working","입고작업 완료");
                                itemClickReference.updateChildren(itemClickMap);
                                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + " 입고작업 완료 + 컨테이너 회수 요청",
                                        consigneeNameValue,
                                        "InCargo", deptName);

                                ((Incargo)getActivity()).initIntent();
                            }
                        })
                        .setNegativeButton("입고작업 완료 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickMap.put("working","입고작업 완료");
                                itemClickReference.updateChildren(itemClickMap);

                                Toast.makeText(getActivity(),itemClickTitle+"\n"+" 입고작업 완료  알림 등록 되었습니다.",
                                        Toast.LENGTH_LONG).show();
                                ((Incargo)getActivity()).initIntent();
                            }
                        })
                        .setNeutralButton("컨테이너 회수요청 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + " 컨테이너 회수요청",
                                        consigneeNameValue,
                                        "InCargo", deptName);

                                ((Incargo)getActivity()).initIntent();
                            }
                        })
                        .show();
            }
        });
        btnInsCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("검수완료+반입요청 확인 창")
                        .setMessage(itemClickTitle+" 화물정보"+"\n" +"검수내용과 일치하면 하단 반입 요청 버튼 클릭하여 알림 등록 바랍니다.")
                        .setPositiveButton("검수 완료+반입요청 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickMap.put("working","검수완료");
                                itemClickReference.updateChildren(itemClickMap);
                                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + "검수 완료+반입요청 등록", consigneeNameValue,
                                        "InCargo", deptName);

                                ((Incargo)getActivity()).initIntent();
                            }
                        })
                        .setNegativeButton("검수완료 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickMap.put("working","검수완료");
                                itemClickReference.updateChildren(itemClickMap);

                                Toast.makeText(getActivity(),itemClickTitle+"\n"+"검수완료 등록 되었습니다.",
                                        Toast.LENGTH_SHORT).show();
                                ((Incargo)getActivity()).initIntent();
                            }
                        })
                        .setNeutralButton("반입요청 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                publicMethod.putNewDataUpdateAlarm(nickName, itemClickTitle + "재고반입 등록 요청", consigneeNameValue,
                                        "InCargo", deptName);

                                ((Incargo)getActivity()).initIntent();
                            }
                        })
                        .show();
            }
        });
        ArrayList<Fine2IncargoList> listItems=((Incargo)getActivity()).listItems;
        btnIncargoCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String consigneeName=listItems.get(0).getConsignee();
                String des=listItems.get(0).getDescription();
                String bl=listItems.get(0).getBl();
                itemClickMap.put("working","창고반입");
                itemClickReference.updateChildren(itemClickMap);
                Toast.makeText(getActivity(),itemClickTitle+"\n"+"창고반입 으로 서버 등록 되었습니다.",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("로케이션 등록 확인창")
                        .setMessage("화주명:"+consigneeName+"\n"+"품명:"+des+"\n"+"비엘:"+bl+"\n"+"화물에 대한 Location 등록을 진행할려면 하단 " +
                                "Location 등록 버튼을 클릭 바랍니다.")
                        .setPositiveButton("Location 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String keyValue=listItems.get(0).getKeyValue();
                                String date=listItems.get(0).getDate();
                                String month=date.substring(5,7)+"월";
                                String refPath="DeptName/"+deptName+"/InCargo/"+month+"/"+date+"/"+keyValue;

                                Intent intent=new Intent(getActivity(),Location.class);
                                Fine2IncargoList setList=listItems.get(0);
                                intent.putExtra("list",setList);
                                intent.putExtra("refPath",refPath);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((Incargo)getActivity()).initIntent();
                            }
                        })
                        .show();

            }
        });

        btnIncargoCom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        });
        btnRegPallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String sharedValue="Pallet";
                String bl=listItems.get(0).getBl();
                String des=listItems.get(0).getDescription();
                String consigneeName=listItems.get(0).getConsignee();
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("팔렛트 등록 확인창")
                        .setMessage("사용 등록:" + "\n" + consigneeName+"_"+bl+"_"+des+ "\n"+"화물에 대한 사용등록"+"\n" + "입고 등록:" + "\n" + "화주별,팔렛트별 팔렛트 신규 입고등록")
                        .setPositiveButton("사용 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                publicMethod.pltReg(consigneeNameValue,nickName,0,bl,des);
                            }
                        })
                        .setNeutralButton("입고 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((Incargo)getActivity()).manualPltReg();
                            }
                        })
                        .show();

            }
        });
        btnPicCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String keyValue=getIntent().getStringExtra("refPath");
//                String consigneeNameValue=getIntent().getStringExtra("consigneeName");
//                String blValue=getIntent().getStringExtra("bl");
//                String containerValue=getIntent().getStringExtra("container");
//                String dateValue= getIntent().getStringExtra("date");
//                if(keyValue==null){
//                    keyValue=listItems.get(0).getKeyValue();
//                    dateValue=listItems.get(0).getDate();
//                }
//                publicMethod.putRemarkValue(listItems.get(0).getBl(),listItems.get(0).getDescription());
//
            }
        });

        btnPicCount.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                String keyValue=listItems.get(0).getKeyValue();
//                String date=listItems.get(0).getDate();
//                String month=date.substring(5,7)+"월";
//                String refPath="DeptName/"+deptName+"/InCargo/"+month+"/"+date+"/"+keyValue;
//
//                Intent intent=new Intent(Incargo.this,Location.class);
//                Fine2IncargoList setList=listItems.get(0);
//                intent.putExtra("list",setList);
//                intent.putExtra("refPath",refPath);
//                startActivity(intent);
                return true;
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}