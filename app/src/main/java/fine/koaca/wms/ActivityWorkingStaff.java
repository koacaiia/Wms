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
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityWorkingStaff extends AppCompatActivity {
    TextView textViewTitle;
    RecyclerView recyclerView;
    RecyclerView recyclerViewResult;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<ListOutSourcingValue> list;
    ArrayList<ListOutSourcingValue> listResult;
    ActivityWorkingStaffAdapter adapter;
    ActivityWorkingStaffAdapter adapterResult;
    PublicMethod publicMethod;
    String nickName,deptName,toDay;

    Button btnRegStaff;
    String dateToDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_staff);
        dateToDay=new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        publicMethod=new PublicMethod(this);
        nickName=publicMethod.getUserInformation().get("nickName");
        deptName=publicMethod.getUserInformation().get("deptName");
        toDay=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        textViewTitle=findViewById(R.id.activityworkingstaff_textview);
        textViewTitle.setText(dateToDay+" 출근 현황");
        recyclerView=findViewById(R.id.activityworkingstaff_recyclerview);
        recyclerViewResult=findViewById(R.id.activityworkingstaff_recyclerview_result);
        btnRegStaff=findViewById(R.id.activityworkingstaff_btnputstaff);
        btnRegStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialogRegStaff();
             }
        });
        btnRegStaff.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialogSearch();
                return true;
            }
        });

//        GridLayoutManager manager=new GridLayoutManager(this,2);
        list=new ArrayList<>();
        listResult=new ArrayList<>();
        LinearLayoutManager manager=new LinearLayoutManager(this);
        LinearLayoutManager resultManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerViewResult.setLayoutManager(resultManager);
        database=FirebaseDatabase.getInstance();
        getDatabaseData(dateToDay);

        adapter=new ActivityWorkingStaffAdapter(list);
        adapterResult=new ActivityWorkingStaffAdapter(listResult);
        recyclerView.setAdapter(adapter);
        recyclerViewResult.setAdapter(adapterResult);

    }

    private void dialogSearch() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.dialog_date_select,null);
        final String[] startDay = new String[1];
        final String[] endDay = new String[1];
        Button btnStartDay=view.findViewById(R.id.dialog_date_select_btnstartday);
        Button btnEndDay=view.findViewById(R.id.dialog_date_select_btnendday);
        Button btnYear=view.findViewById(R.id.dialog_date_select_btnyear);
        Button btnMonth=view.findViewById(R.id.dialog_date_select_btnmonth);
        Button btnBeforeWeek=view.findViewById(R.id.dialog_date_select_btnbeforeweek);
        Button btnWeek=view.findViewById(R.id.dialog_date_select_btnweek);
        Button btnTomorrow=view.findViewById(R.id.dialog_date_select_btntomorrow);
        TextView txtName=view.findViewById(R.id.dialog_date_select_txtName);
        TextView txtStartDay=view.findViewById(R.id.dialog_date_select_txtstartday);
        TextView txtEndDay=view.findViewById(R.id.dialog_date_select_txtendday);
        Spinner spConsigneeName=view.findViewById(R.id.dialog_date_select_spinnername);

        ArrayList<String> consigneeListArr=new ArrayList<>();
        databaseReference=database.getReference("DeptName/"+deptName+"/OutSourcingValue/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                   consigneeListArr.add(data.getKey());

                }
                ArrayAdapter<String> consigneeAdapter=
                        new ArrayAdapter<String>(ActivityWorkingStaff.this,android.R.layout.simple_spinner_dropdown_item,
                                consigneeListArr);
                consigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spConsigneeName.setAdapter(consigneeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Calendar calendar= Calendar.getInstance();
        String strMonth;
        String strDay;
        final String[] consigneeName = new String[1];
        String format="yyyy-MM-dd";
        SimpleDateFormat date=new SimpleDateFormat(format);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        consigneeName[0] ="전체";
        if((month+1)<10){
            strMonth="0"+(month+1);
        }else{
            strMonth=String.valueOf(month);
        }
        if(day<10){
            strDay="0"+day;
        }else{
            strDay=String.valueOf(day);
        }

        spConsigneeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                consigneeName[0] =consigneeListArr.get(position);
                txtName.setText(consigneeName[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDay[0]=year+"-01-01";
                endDay[0]=year+"-12-31";
                txtStartDay.setText(startDay[0]);
                txtEndDay.setText(endDay[0]);
                txtStartDay.setTextColor(Color.RED);
                txtEndDay.setTextColor(Color.RED);
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startDay[0]=year+"-"+strMonth+"-01";
            String maxDay=String.valueOf(calendar.getMaximum(Calendar.DAY_OF_MONTH));
            endDay[0]=year+"-"+strMonth+"-"+maxDay;
                txtStartDay.setText(startDay[0]);
                txtEndDay.setText(endDay[0]);
                txtStartDay.setTextColor(Color.RED);
                txtEndDay.setTextColor(Color.RED);

            }
        });
        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                startDay[0]=date.format(calendar.getTime());
                calendar.add(Calendar.DATE,5);
                endDay[0]=date.format(calendar.getTime());
                txtStartDay.setText(startDay[0]);
                txtEndDay.setText(endDay[0]);
                txtStartDay.setTextColor(Color.RED);
                txtEndDay.setTextColor(Color.RED);

            }
        });
        btnTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                calendar.add(Calendar.DATE,1);
                startDay[0]=date.format(calendar.getTime());
                endDay[0]=date.format(calendar.getTime());
                txtStartDay.setText(startDay[0]);
                txtEndDay.setText(endDay[0]);
                txtStartDay.setTextColor(Color.RED);
                txtEndDay.setTextColor(Color.RED);
            }
        });
        btnBeforeWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                calendar.add(Calendar.DATE,-7);
                calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                startDay[0]=date.format(calendar.getTime());
                calendar.add(Calendar.DATE,5);
                endDay[0]=date.format(calendar.getTime());
                txtStartDay.setText(startDay[0]);
                txtEndDay.setText(endDay[0]);
                txtStartDay.setTextColor(Color.RED);
                txtEndDay.setTextColor(Color.RED);

            }
        });
        btnStartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityWorkingStaff.this);
                DatePicker datePicker=new DatePicker(ActivityWorkingStaff.this);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = String.valueOf(monthOfYear+1),day = String.valueOf(dayOfMonth);
                        if(monthOfYear+1<10){
                            month="0"+(monthOfYear+1);
                        }
                        if(dayOfMonth<10){
                            day="0"+dayOfMonth;
                        }
                        startDay[0]=year+"-"+month+"-"+day;
                    }
                }
                );
                builder.setTitle("시작일 선택창")
                        .setView(datePicker)
                        .setPositiveButton("시작일 선택", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(startDay[0]==null){
                                    Toast.makeText(ActivityWorkingStaff.this,"날짜가 지정 안되었습니다.확인 바랍니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                txtStartDay.setText(startDay[0]);
                                txtStartDay.setTextColor(Color.RED);
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

        btnEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityWorkingStaff.this);
                DatePicker datePicker=new DatePicker(ActivityWorkingStaff.this);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = String.valueOf(monthOfYear+1),day = String.valueOf(dayOfMonth);
                        if(monthOfYear+1<10){
                            month="0"+(monthOfYear+1);
                        }
                        if(dayOfMonth<10){
                            day="0"+dayOfMonth;
                        }
                        endDay[0]=year+"-"+month+"-"+day;
                    }
                }
                );
                builder.setTitle("종료일 선택창")
                        .setView(datePicker)
                        .setPositiveButton("종료일 선택", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(endDay[0]==null){
                                    Toast.makeText(ActivityWorkingStaff.this,"날짜가 지정 안되었습니다.확인 바랍니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                txtEndDay.setText(endDay[0]);
                                txtEndDay.setTextColor(Color.RED);
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
        builder.setTitle("날짜 선택창")
                .setView(view)
                .setPositiveButton("검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(startDay[0]==null){
                            startDay[0]=endDay[0];
                        }
                        if(endDay[0]==null){
                            endDay[0]=startDay[0];
                        }
                        if(startDay[0]==null||endDay[0]==null){
                            Toast.makeText(ActivityWorkingStaff.this,"날짜 지정 확인 바랍니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getDataSortByDate(txtName.getText().toString(),startDay[0],endDay[0]);

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void getDataSortByDate(String consigneeName, String startDay, String endDay) {
        String trStartDay, trEndDay, trDate;
        trStartDay = startDay.replace("-", "");
        trEndDay = endDay.replace("-", "");
        list.clear();
        listResult.clear();
        databaseReference = database.getReference("DeptName/" + deptName + "/WorkingStaffCheck");
        ValueEventListener listener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double fineStaff = 0, fineWomenStaff = 0,outMale=0,outFemale=0,outEquip=0;
                for (DataSnapshot data : snapshot.getChildren()) {

                    ListOutSourcingValue mList = data.getValue(ListOutSourcingValue.class);
                    assert mList != null;
                    String outValue=mList.getGender();
                    String outName=mList.getName();
                    if(outName.equals(consigneeName)){
                        switch(outValue){
                            case "남자":
                                outMale=outMale+mList.getCount();
                                break;
                            case "여자":
                                outFemale=outFemale+mList.getCount();
                                break;
                            case "장비운행":
                                outEquip=outEquip+mList.getCount();
                                break;
                            }
                        list.add(mList);
                    }
                    switch(outValue){


                        case "Staff":
                            fineStaff=fineStaff+mList.getCount();
                            break;
                        case "WomenStaff":
                            fineWomenStaff=fineWomenStaff+mList.getCount();
                            break;

                    }


                }
                textViewTitle.setText(startDay+"~"+endDay+" 출근현황("+consigneeName+")");
                TextView txtOutsourcingMale = findViewById(R.id.activityworkingstaff_outsourcingmen);
                txtOutsourcingMale.setText("총:" + outMale + " 명");
                TextView txtOutsourcingFemale = findViewById(R.id.activityworkingstaff_outsourcingwmen);
                txtOutsourcingFemale.setText("총:" + outFemale + " 명");
                TextView txtOutsourcingEquip = findViewById(R.id.activityworkingstaff_outsourcingEquip);
                txtOutsourcingEquip.setText("총:" + outEquip + " 명");
                TextView txtFineStaff=findViewById(R.id.activityworkingstaff_finestaff);
                txtFineStaff.setText("총:"+fineStaff+" 명");
                TextView txtFineWStaff=findViewById(R.id.activityworkingstaff_finewomen);
                txtFineWStaff.setText("총:"+fineWomenStaff+"명 ");

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Query sortByDate = databaseReference.orderByChild("date").startAt(startDay).endAt(endDay);
        sortByDate.addListenerForSingleValueEvent(listener);


        ValueEventListener outsourcingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    String outKey = data.getKey();

                    if (outKey.contains("OutsourcingValue")) {
                        DatabaseReference outRef=database.getReference("DeptName/" + deptName + "/WorkingStaff/" + outKey);
                        outRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double outsourcingMale = 0, outsourcingFemale = 0, outsourcingEquip = 0;
                                for(DataSnapshot data:snapshot.getChildren()){
                                    ListOutSourcingValue outList = data.getValue(ListOutSourcingValue.class);
                                    String gender=outList.getGender();
                                    if (gender.contains("남자")) {
                                        outsourcingMale = outsourcingMale + outList.getCount();
                                    }
                                    if (gender.contains("여자")) {
                                        outsourcingFemale = outsourcingFemale + outList.getCount();
                                    }
                                    if (gender.contains("장비")) {
                                        outsourcingEquip = outsourcingEquip + outList.getCount();
                                    }
                                    listResult.add(outList);
                                }

                                adapterResult.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        databaseReference.addListenerForSingleValueEvent(outsourcingListener);
    }


    private void dialogRegStaff() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        View view=getLayoutInflater().inflate(R.layout.dialog_putworkingstaff,null);

        Button btnDate=view.findViewById(R.id.dialog_putworkingstaff_btnDate);
        btnDate.setText(date);
        TextView txtDate=view.findViewById(R.id.dialog_putworkingstaff_txtDate);
        txtDate.setText(dateToDay);
        Button btnFineStaff=view.findViewById(R.id.dialog_putworkingstaff_btnfinestaff);
        TextView txtFineStaff=view.findViewById(R.id.dialog_putworkingstaff_txtFinestaff);
        txtFineStaff.setText(String.valueOf(5.0));
        Button btnWfineStaff=view.findViewById(R.id.dialog_putworkingstaff_btnfinewomenstaff);
        TextView txtWfineStaff=view.findViewById(R.id.dialog_putworkingstaff_txtFineWomenStaff);
        txtWfineStaff.setText(String.valueOf(1.0));
        Button btnOutSourcing=view.findViewById(R.id.dialog_putworkingstaff_btnOutsourcing);
        TextView scrOutSourcingTitle=view.findViewById(R.id.dialog_putworkingstaff_txtscroll);

        btnDate.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           final String[] date = new String[1];
                                           AlertDialog.Builder builder=new AlertDialog.Builder(ActivityWorkingStaff.this);
                                           DatePicker datePicker=new DatePicker(ActivityWorkingStaff.this);
                                           datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                               @Override
                                               public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                   String month = String.valueOf(monthOfYear+1),day = String.valueOf(dayOfMonth);
                                                   if(monthOfYear+1<10){
                                                       month="0"+(monthOfYear+1);
                                                   }
                                                   if(dayOfMonth<10){
                                                       day="0"+dayOfMonth;
                                                   }
                                                   date[0] =year+"-"+month+"-"+day;
                                               }
                                           });
                                           builder.setTitle("날짜 선택창")
                                                   .setMessage("날짜 선택후 하단 등록 버튼으로 등록 바랍니다.")
                                                   .setView(datePicker)
                                                   .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {
                                                                   if(date[0]==null){
                                                                      date[0]= btnDate.getText().toString();
                                                                   }
                                                                   btnDate.setText(date[0]);
                                                                   txtDate.setText(date[0]);
                                                               }
                                                           }
                                                   )
                                                   .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {

                                                       }
                                                   })
                                                   .show();
                                       }
                                   }
        );

        btnFineStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           AlertDialog.Builder fineStaffDialog=new AlertDialog.Builder(ActivityWorkingStaff.this);
           EditText editText=new EditText(ActivityWorkingStaff.this);
           editText.setInputType(InputType.TYPE_CLASS_NUMBER);
           fineStaffDialog.setTitle("부서원출근 등록")
                   .setView(editText)
                   .setPositiveButton("출근등록", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           double dFineStaff=Double.parseDouble(editText.getText().toString());
                           txtFineStaff.setText(String.valueOf(dFineStaff));
                       }
                   })
                   .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {

                       }
                   })
                   .show();
            }
        });

        btnWfineStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder wFineStaff=new AlertDialog.Builder(ActivityWorkingStaff.this);
                EditText editText=new EditText(ActivityWorkingStaff.this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                wFineStaff.setTitle("화인 소속 주부사원 출근등록")
                        .setView(editText)
                        .setPositiveButton("출근등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                double dWfineStaff=Double.parseDouble(editText.getText().toString());
                                txtWfineStaff.setText(String.valueOf(dWfineStaff));
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });

        btnOutSourcing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] outsourcingName = new String[1];
                final String[] outsourcingGender = new String[1];
                list.clear();
           AlertDialog.Builder outSourcingBuilder=new AlertDialog.Builder(ActivityWorkingStaff.this);
           View outsourcingView=getLayoutInflater().inflate(R.layout.dialog_putworkingstaff_outsourcing,null);
           Spinner spOutsourcing=outsourcingView.findViewById(R.id.dialog_putworkingstaff_outsourcing_spinner);
           Spinner spOutsourcingName=outsourcingView.findViewById(R.id.dialog_putworkingstaff_outsourcing_spoutsourcingName);
           EditText editOutsourcingCount=outsourcingView.findViewById(R.id.dialog_putworkingstaff_outsourcing_editCount);
           ArrayList<String> outsourcingGenderList=new ArrayList<>();
           outsourcingGenderList.add("남자");
           outsourcingGenderList.add("장비운행");
           outsourcingGenderList.add("여자");
           ArrayAdapter<String> spOutsourcingNameAdapter=new ArrayAdapter<>(ActivityWorkingStaff.this,
                   android.R.layout.simple_spinner_dropdown_item,outsourcingGenderList);
           spOutsourcingNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
           spOutsourcingName.setAdapter(spOutsourcingNameAdapter);
           spOutsourcingName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   outsourcingGender[0] =outsourcingGenderList.get(i);
                   if(i==1){
                       editOutsourcingCount.setText(String.valueOf(2));
                   }
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {

               }
           });

           TextView txtScroll=outsourcingView.findViewById(R.id.dialog_putworkingstaff_outsourcing_txtscroll);
           Button btnReg=outsourcingView.findViewById(R.id.dialog_putworkingstaff_outsourcing_btnReg);
           btnReg.setOnClickListener(new View.OnClickListener() {
               @SuppressLint("SetTextI18n")
               @Override
               public void onClick(View view) {
                   if(editOutsourcingCount.getText().toString().equals("")){
                       Toast.makeText(ActivityWorkingStaff.this,"인원 등록 안되었습니다.다시 확인 바랍니다.",Toast.LENGTH_SHORT).show();
                       return;
                   }
                   ListOutSourcingValue value=new ListOutSourcingValue(txtDate.getText().toString(),outsourcingName[0],
                           outsourcingGender[0],
                           Double.parseDouble(editOutsourcingCount.getText().toString()));
                   list.add(value);
                   txtScroll.setText(txtScroll.getText().toString()+outsourcingName[0]+"("+outsourcingGender[0]+")="+editOutsourcingCount.getText().toString()+
                      "\n");
               }
           });


           databaseReference=database.getReference("DeptName/"+deptName+"/OutSourcingValue");
           databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   ArrayList<String> outsourcingValueList=new ArrayList<>();
                   for(DataSnapshot data:snapshot.getChildren()){
                       outsourcingValueList.add(data.getKey());
                       }
                   ArrayAdapter<String> spOutsourcingAdapter=new ArrayAdapter<String>(ActivityWorkingStaff.this,
                           android.R.layout.simple_spinner_dropdown_item,outsourcingValueList);
                   spOutsourcingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                   spOutsourcing.setAdapter(spOutsourcingAdapter);
                   spOutsourcing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                       @Override
                       public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            outsourcingName[0] =outsourcingValueList.get(i);

                       }

                       @Override
                       public void onNothingSelected(AdapterView<?> adapterView) {

                       }
                   });
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
           outSourcingBuilder.setView(outsourcingView)
                   .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           scrOutSourcingTitle.setText(txtScroll.getText().toString());
                       }
                   })
                   .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {

                       }
                   })
                   .show();

            }
        });



        builder.setTitle("출근 인원 등록창")
                .setView(view)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            getDatabaseData(dateToDay);

                        double countFineStaff= Double.parseDouble(txtFineStaff.getText().toString());
                        double countWfineStaff= Double.parseDouble(txtWfineStaff.getText().toString());
                        ListOutSourcingValue value=new ListOutSourcingValue(txtDate.getText().toString(),"Fine",
                                "Staff",countFineStaff);
                        list.add(value);
                        ListOutSourcingValue valueW=new ListOutSourcingValue(txtDate.getText().toString(),"Fine",
                                "WomenStaff",countWfineStaff);
                        list.add(valueW);

                        putDialogRegStaff(txtDate.getText().toString());
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })

                .show();

    }

    private void putDialogRegStaff(String date) {
        String outsourcingBasicPath="DeptName/"+deptName+"/WorkingStaffCheck/"+date;

        for(int i=0;i<list.size();i++){
            String outsourcingName=list.get(i).getName();
            String outsourcingGender=list.get(i).getGender();
            double outsourcingCount=list.get(i).getCount();
            DatabaseReference outsourcingRef=
                    database.getReference(outsourcingBasicPath+"_"+outsourcingName+"_"+outsourcingGender);
            ListOutSourcingValue listOutSourcingValue=new ListOutSourcingValue(date,outsourcingName,outsourcingGender,
                    outsourcingCount);
            outsourcingRef.setValue(listOutSourcingValue);
        }
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.workingstaff_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            putOutsourcingName();
        }
        return true;
    }

    private void putOutsourcingName() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        EditText editText=new EditText(this);
                builder.setTitle("외부인력 업체명 등록 창")
                .setMessage("외부인력 신규업체 등록 진행")
                .setView(editText)
                .setPositiveButton("신규업체 등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String outsourcingValue=editText.getText().toString();
                        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityWorkingStaff.this);
                        builder.setTitle("신규화주 확인창")
                                .setMessage(outsourcingValue+" 로 신규 외부인력"+"\n"+"아웃소싱 업체 등록 진행 합니다.")
                                .setPositiveButton(outsourcingValue+" 등록", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        databaseReference=database.getReference("DeptName/"+deptName+
                                                "/OutSourcingValue/"+outsourcingValue);
                                        Map<String,Object> value=new HashMap<>();
                                        value.put("outsourcingValue",outsourcingValue);
                                        databaseReference.updateChildren(value);
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

    }

    private void getDatabaseData(String dateToDay) {
        databaseReference=database.getReference("DeptName/"+deptName+"/WorkingStaffCheck/");
        ValueEventListener listener=new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double intF=0.0;
                double intWf=0.0;
                double intoF=0;
                double intoM=0;
                double intoE=0;

                for(DataSnapshot data:snapshot.getChildren()){
                  if(data.getKey().contains(dateToDay)){
                      ListOutSourcingValue mList=data.getValue(ListOutSourcingValue.class);
                      assert mList != null;
                      String keyValue=data.getKey();

                      if(keyValue.contains("장비")){
                          intoE=intoE+mList.getCount();
                      }
                      if(keyValue.contains("남자")){
                          intoM=intoM+mList.getCount();
                      }
                      if(keyValue.contains("여자")){
                          intoF=intoF+mList.getCount();
                      }
                      if(keyValue.contains("Fine_Staff")){
                          intF=intF+mList.getCount();
                      }
                      if(keyValue.contains("Fine_WomenStaff")){
                          intWf=intWf+mList.getCount();
                      }
                      list.add(mList);

                  }


                   }
                TextView txtOutsourcingMale=findViewById(R.id.activityworkingstaff_outsourcingwmen);
                txtOutsourcingMale.setText(intoF+"명");
                TextView txtOutsourcingFemale=findViewById(R.id.activityworkingstaff_outsourcingmen);
                txtOutsourcingFemale.setText(intoM+"명");
                TextView txtOutsourcingEquip=findViewById(R.id.activityworkingstaff_outsourcingEquip);
                txtOutsourcingEquip.setText(intoE+"명");
                TextView txtFineStaff=findViewById(R.id.activityworkingstaff_finestaff);
                txtFineStaff.setText(intF+"명");
                TextView txtFineStaffWomen=findViewById(R.id.activityworkingstaff_finewomen);
                txtFineStaffWomen.setText(intWf+"명");
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);

    }
    @Override
    public void onBackPressed() {

        PublicMethod publicMethod=new PublicMethod(ActivityWorkingStaff.this);
        publicMethod.intentSelect();
    }
}