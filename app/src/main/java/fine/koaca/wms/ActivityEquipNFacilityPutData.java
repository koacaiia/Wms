package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityEquipNFacilityPutData extends AppCompatActivity implements ImageViewActivityAdapter.ImageViewClicked {

    Spinner spName, spContents;
    String deptName, efName, process, date, manageContents, nickName;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    PublicMethod publicMethod;
    Button btnDate, btnReg, btnSearch;
    TextView txtName, txtDate, txtContent;
    ArrayList<String> imageViewLists;
    RecyclerView imageRecyclerView;
    ImageViewActivityAdapter iAdapter;


    String refPath;

    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> contentsList = new ArrayList<>();
    ArrayList<String> selectedImageViewLists=new ArrayList<>();
    SparseBooleanArray clickedList=new SparseBooleanArray(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equip_nfacility_put_data);
        database = FirebaseDatabase.getInstance();
        publicMethod = new PublicMethod(this);
        deptName = publicMethod.getUserInformation().get("deptName");
        nickName = publicMethod.getUserInformation().get("nickName");
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        refPath = "DeptName/" + deptName + "/EquipNFacility/";


        imageRecyclerView = findViewById(R.id.activity_equip_facility_recyclerimageview);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        imageRecyclerView.setLayoutManager(manager);
        PublicMethod pictures = new PublicMethod(this);
        imageViewLists = pictures.getPictureLists("Re");
        iAdapter = new ImageViewActivityAdapter(imageViewLists, this);
        imageRecyclerView.setAdapter(iAdapter);

        txtName = findViewById(R.id.activity_equip_facility_txtName);
        txtDate = findViewById(R.id.activity_equip_facility_txtDate);
        txtContent = findViewById(R.id.activity_equip_facility_txtContent);

        spName = findViewById(R.id.activity_equip_facility_spname);
        spContents = findViewById(R.id.activity_equip_facility_spcontents);
        getSpinnerAdapter();
        spName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEquipNFacilityPutData.this);
                EditText editText = new EditText(ActivityEquipNFacilityPutData.this);
                builder.setTitle("장비,시설물 종류 직접 입력")
                        .setMessage("장비,시설물 종류를 직접 입력후 하단 등록 버튼을 눌러 진행 바랍니다.")
                        .setView(editText)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                efName = editText.getText().toString();
                                txtName.setText(efName);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return true;
            }
        });
        spName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    txtName.setText(nameList.get(position));
                } else {
                    txtName.setText("장비,시설물 종류");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spContents.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEquipNFacilityPutData.this);
                EditText editText = new EditText(ActivityEquipNFacilityPutData.this);
                builder.setTitle("장비,시설물 점검사항 항목 직접입력")
                        .setMessage("장비,시설물에 대한 점검사항을 입력후 하단 등록 버튼을 눌러 진행 바랍니다.")
                        .setView(editText)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manageContents = editText.getText().toString();
                                txtContent.setText(manageContents);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return true;
            }
        });
        spContents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    txtContent.setText(contentsList.get(position));
                } else {
                    txtContent.setText("점검항목");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btnDate = findViewById(R.id.activity_equip_facility_btndate);
        btnDate.setText(date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEquipNFacilityPutData.this);
                DatePicker datePicker = new DatePicker(ActivityEquipNFacilityPutData.this);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = String.valueOf(monthOfYear + 1), day = String.valueOf(dayOfMonth);
                        if (monthOfYear + 1 < 10) {
                            month = "0" + (monthOfYear + 1);
                        }
                        if (dayOfMonth < 10) {
                            day = "0" + dayOfMonth;
                        }
                        date = year + "-" + month + "-" + day;
                        btnDate.setText(date);
                    }
                });
                builder.setTitle("장비,시설물 관리항목에 대한 날짜 지정")
                        .setMessage("장비,시설물에 대한 점검사항을 입력후 하단 등록 버튼을 눌러 진행 바랍니다.")
                        .setView(datePicker)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtDate.setText(date);

                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });
        btnReg = findViewById(R.id.activity_equip_facility_btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                String date = btnDate.getText().toString();
                String name = txtName.getText().toString();
                String content = txtContent.getText().toString();
                String process = "점검요청";


                int estimateAmount = 0;
                int confirmAmount = 0;
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityEquipNFacilityPutData.this);
                builder.setTitle(date+"_"+name+"_"+content+" 점검요청 사항")
                        .setMessage(selectedImageViewLists.size()+" 개의 사진을 상기내용으로 서버에 등록 합니다.내용이 맞으면 하단 등록 버튼으로 전송 바랍니다.")
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String keyValue = date + "_" + name + "_" + content;
                                publicMethod=new PublicMethod(ActivityEquipNFacilityPutData.this,selectedImageViewLists);
                                publicMethod.upLoadPictures(nickName,name,"장비_시설물",keyValue,deptName);


                                databaseReference = database.getReference(refPath + keyValue);
                                ActivityEquipFacilityList mList = new ActivityEquipFacilityList(name,content,date,"",
                                        estimateAmount,"","","",confirmAmount,keyValue,"");
                                databaseReference.setValue(mList);


                                Toast.makeText(ActivityEquipNFacilityPutData.this, keyValue + " " + process + " 로 서버에 등록 되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
//

//
            }
        });
    }

    private void getSpinnerAdapter() {
        databaseReference = database.getReference(refPath);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    ActivityEquipFacilityList mList = data.getValue(ActivityEquipFacilityList.class);
                    String name = mList.getName();
                    String contents = mList.getContent();
                    if (!nameList.contains(name)) {
                        nameList.add(name);
                    }
                    if (!contentsList.contains(contents)) {
                        contentsList.add(contents);
                    }
                }
                nameList.add(0, "");
                nameList.add(nameList.size(), "시설물 점검");
                contentsList.add(0, "");
                ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(ActivityEquipNFacilityPutData.this,
                        android.R.layout.simple_spinner_dropdown_item, nameList);
                nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spName.setAdapter(nameAdapter);

                ArrayAdapter<String> contentsAdapter = new ArrayAdapter<String>(ActivityEquipNFacilityPutData.this,
                        android.R.layout.simple_spinner_dropdown_item, contentsList);
                contentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spContents.setAdapter(contentsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        if(clickedList.get(position, false)){
            clickedList.delete(position);
            selectedImageViewLists.remove(imageViewLists.get(position));

        }else{
            clickedList.put(position,true);
            selectedImageViewLists.add(imageViewLists.get(position));
        }

        Toast.makeText(ActivityEquipNFacilityPutData.this,selectedImageViewLists.size()+" 개의 사진이 선택 되었습니다.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        PublicMethod publicMethod=new PublicMethod(ActivityEquipNFacilityPutData.this);
        publicMethod.intentSelect();
    }
}
