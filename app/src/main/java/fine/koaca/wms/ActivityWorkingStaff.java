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
    ArrayList<ActivityWorkingStaffList> list;
    ArrayList<ActivityWorkingStaffList> listResult;
    ActivityWorkingStaffAdapter adapter;
    ActivityWorkingStaffAdapter adapterResult;
    PublicMethod publicMethod;
    String nickName,deptName,toDay;

    Button btnRegStaff;
    String outsourcingValue;
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
        Button btnWeek=view.findViewById(R.id.dialog_date_select_btnweek);
        Button btnTomorrow=view.findViewById(R.id.dialog_date_select_btntomorrow);
        TextView txtName=view.findViewById(R.id.dialog_date_select_txtName);
        TextView txtStartDay=view.findViewById(R.id.dialog_date_select_txtstartday);
        TextView txtEndDay=view.findViewById(R.id.dialog_date_select_txtendday);
        Spinner spConsigneeName=view.findViewById(R.id.dialog_date_select_spinnername);

        ArrayList<String> consigneeListArr=new ArrayList<>();
        databaseReference=database.getReference("DeptName/"+deptName+"/WorkingStaff");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    ActivityWorkingStaffList mList=data.getValue(ActivityWorkingStaffList.class);
                    assert mList != null;
                    if(!mList.getOutsourcingValue().equals("") && !consigneeListArr.contains(mList.getOutsourcingValue())){
                        consigneeListArr.add(mList.getOutsourcingValue());
                    }
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
                calendar.add(Calendar.DATE,1);
                startDay[0]=date.format(calendar.getTime());
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
        String trStartDay,trEndDay,trDate;
        trStartDay=startDay.replace("-","");
        trEndDay=endDay.replace("-","");
        list.clear();
        listResult.clear();
        databaseReference=database.getReference("DeptName/"+deptName+"/WorkingStaff");
        ValueEventListener listener=new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int fineStaff=0,fineWomenStaff=0,outsourcingMale=0,outsourcingFemale=0;
                ArrayList<String> consigneeListArr=new ArrayList<>();
                for(DataSnapshot data:snapshot.getChildren()){
                    ActivityWorkingStaffList mList=data.getValue(ActivityWorkingStaffList.class);
                    fineStaff=fineStaff+Integer.parseInt(mList.getFineStaff());
                    fineWomenStaff=fineWomenStaff+Integer.parseInt(mList.getFineWomenStaff());
                    outsourcingMale=outsourcingMale+Integer.parseInt(mList.getOutsourcingMale());
                    outsourcingFemale=outsourcingFemale+Integer.parseInt(mList.getOutsourcingFemale());

                    if(!mList.getOutsourcingValue().equals("")){
                        ActivityWorkingStaffList mList1=new ActivityWorkingStaffList(mList.getDate(),"","",
                                String.valueOf(mList.getOutsourcingMale()),String.valueOf(mList.getOutsourcingFemale()),
                                mList.getOutsourcingValue());
                        list.add(mList1);
                        if(!consigneeListArr.contains(mList.getOutsourcingValue())){
                            consigneeListArr.add(mList.getOutsourcingValue());
                            Log.i("TestValue","consigneeListArr size::::"+consigneeListArr.size());
                        }
                    }

                }
                int listSize=list.size();
                int consigneeListSize= consigneeListArr.size();
                for(int i=0;i<consigneeListSize;i++) {
                    int outMale=0,outFemale=0;
                    for (int j = 0; j < listSize; j++) {
                        if (consigneeListArr.get(i).equals(list.get(j).getOutsourcingValue())) {
                            outMale=outMale+Integer.parseInt(list.get(j).getOutsourcingMale());
                            outFemale=outFemale+Integer.parseInt(list.get(j).getOutsourcingFemale());
                        }
                    }
                    ActivityWorkingStaffList mList2=new ActivityWorkingStaffList(startDay+"/"+endDay,"","",
                            String.valueOf(outMale),String.valueOf(outFemale),
                            consigneeListArr.get(i));
                    listResult.add(mList2);
                }

                adapter.notifyDataSetChanged();
                adapterResult.notifyDataSetChanged();
                TextView txtFineStaff=findViewById(R.id.activityworkingstaff_finestaff);
                txtFineStaff.setText("총:"+fineStaff+" 명");
                TextView txtFineWomenStaff=findViewById(R.id.activityworkingstaff_finewomen);
                txtFineWomenStaff.setText("총:"+fineWomenStaff+" 명");
                TextView txtOutsourcingMale=findViewById(R.id.activityworkingstaff_outsourcingmen);
                txtOutsourcingMale.setText("총:"+outsourcingMale+" 명");
                TextView txtOutsourcingFemale=findViewById(R.id.activityworkingstaff_outsourcingwmen);
                txtOutsourcingFemale.setText("총:"+outsourcingFemale+" 명");

//                ActivityWorkingStaffList mList=new ActivityWorkingStaffList(startDay+"/"+endDay,String.valueOf(fineStaff),
//                        String.valueOf(fineWomenStaff),String.valueOf(outsourcingMale),String.valueOf(outsourcingFemale),
//                        consigneeName);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Query sortByDate=databaseReference.orderByChild("date").startAt(startDay).endAt(endDay);
        sortByDate.addListenerForSingleValueEvent(listener);
    }


    private void dialogRegStaff() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        View view=getLayoutInflater().inflate(R.layout.dialog_putworkingstaff,null);

        Button btnDate=view.findViewById(R.id.dialog_putworkingstaff_btnDate);
        btnDate.setText(date);
        EditText editStaffCount=view.findViewById(R.id.dialog_putworkingstaff_editstaffcount);
        TextView textView=view.findViewById(R.id.dialog_putworkingstaff_txtscroll);

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
                                                                   btnDate.setText(date[0]);
                                                                   textView.append(date[0]+" 로 날짜 등록");
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





        Spinner spOutsourcingValue=view.findViewById(R.id.dialog_putworkingstaff_spinneroutsourcing);
        databaseReference=database.getReference("DeptName/"+deptName+"/OutSourcingValue");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> outsourcingValueList=new ArrayList<>();
                for(DataSnapshot data:snapshot.getChildren()){

                    outsourcingValueList.add(data.getKey());
                }
                outsourcingValueList.add(0,"");
                ArrayAdapter<String> spOutsourcingAdapter=new ArrayAdapter<String>(ActivityWorkingStaff.this,
                        android.R.layout.simple_spinner_dropdown_item,outsourcingValueList);
                spOutsourcingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spOutsourcingValue.setAdapter(spOutsourcingAdapter);
                spOutsourcingValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        outsourcingValue=outsourcingValueList.get(position);
                        if(!outsourcingValue.equals("")){
                            Toast.makeText(ActivityWorkingStaff.this,outsourcingValue+" 선텍",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Button btnFineStaff=view.findViewById(R.id.dialog_putworkingstaff_btnfinestaff);
        btnFineStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editStaffCount.getText().toString().equals("")){
                    Toast.makeText(ActivityWorkingStaff.this,"출근인원 다시 확인후 진행 바랍니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                int count= Integer.parseInt(editStaffCount.getText().toString());
                editStaffCount.setText("");
                if(textView.getText().toString().equals("")){
                    textView.setText(deptName+" 출근인원:"+count+"명으로 서버 등록");
                }else{
                    textView.append("\n"+deptName+" 출근인원:"+count+"명으로 서버 등록");
                }
//               putDialogRegStaff("fineStaff", btnDate.getText().toString(),count);


            }
        });
        Button btnFineWomenStaff=view.findViewById(R.id.dialog_putworkingstaff_btnfinewomenstaff);
        btnFineWomenStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editStaffCount.getText().toString().equals("")){
                    Toast.makeText(ActivityWorkingStaff.this,"출근인원 다시 확인후 진행 바랍니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                int count= Integer.parseInt(editStaffCount.getText().toString());
                putDialogRegStaff("fineWomenStaff", btnDate.getText().toString(),count);
                if(textView.getText().toString().equals("")){
                    textView.setText("화인주부사원 출근인원:"+count+"명으로 서버 등록");
                }else{
                    textView.append("\n"+"화인주부사원 출근인원:"+count+"명으로 서버 등록");
                }
                editStaffCount.setText("");
            }
        });
        Button btnOutsourcingMale=view.findViewById(R.id.dialog_putworkingstaff_btnoutsourcingmale);
        btnOutsourcingMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editStaffCount.getText().toString().equals("")){
                    Toast.makeText(ActivityWorkingStaff.this,"출근인원 다시 확인후 진행 바랍니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                int count= Integer.parseInt(editStaffCount.getText().toString());
                if(outsourcingValue==null||outsourcingValue.equals("")){
                    Toast.makeText(ActivityWorkingStaff.this,"아웃소싱업체 공란 입니다.확인후 진행 바랍니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                putDialogRegStaff("M_"+outsourcingValue, btnDate.getText().toString(),count);
                if(textView.getText().toString().equals("")){
                    textView.setText(outsourcingValue+"(남):"+count+"명으로 서버 등록");
                }else{
                    textView.append("\n"+outsourcingValue+"(남):"+count+"명으로 서버 등록");
                }
                editStaffCount.setText("");
            }
        });
        Button btnOutsourcingFemale=view.findViewById(R.id.dialog_putworkingstaff_btnoutsourcingfemale);
        btnOutsourcingFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editStaffCount.getText().toString().equals("")){
                    Toast.makeText(ActivityWorkingStaff.this,"출근인원 다시 확인후 진행 바랍니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                int count= Integer.parseInt(editStaffCount.getText().toString());
                putDialogRegStaff("FE_"+outsourcingValue, btnDate.getText().toString(),count);
                if(textView.getText().toString().equals("")){
                    textView.setText(outsourcingValue+"(여):"+count+"명으로 서버 등록");
                }else{
                    textView.append("\n"+outsourcingValue+"(여):"+count+"명으로 서버 등록");
                }
                editStaffCount.setText("");
            }
        });
        builder.setTitle("출근 인원 등록창")
                .setView(view)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .show();

    }

    private void putDialogRegStaff(String contents, String date, int count) {

        databaseReference=database.getReference("DeptName/"+deptName+"/WorkingStaff/"+date+"_"+contents);
        ActivityWorkingStaffList list=new ActivityWorkingStaffList(date,"0","0","0","0","");
        databaseReference.setValue(list);
        String strcount=String.valueOf(count);
        Map<String,Object> value=new HashMap<>();
        if(contents.equals("fineStaff")||contents.equals("fineWomenStaff")){
        value.put(contents,strcount);
        }

        if(contents.contains("M_")){
            contents=contents.replace("M_","");
            value.put("outsourcingMale",strcount);
            value.put("outsourcingValue",contents);
        }
       if(contents.equals("FE_")){
           contents=contents.replace("FE_","");
           value.put("outsourcingFemale",strcount);
           value.put("outsourcingValue",contents);
       }
       databaseReference.updateChildren(value);

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
        databaseReference=database.getReference("DeptName/"+deptName+"/WorkingStaff/");
        ValueEventListener listener=new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int intF=0;
                int intWf=0;
                int intoF=0;
                int intoM=0;
                for(DataSnapshot data:snapshot.getChildren()){
                    ActivityWorkingStaffList mList=data.getValue(ActivityWorkingStaffList.class);
                    assert mList != null;
                    if(mList.getDate().equals(dateToDay)){
                       intF=intF+Integer.parseInt(mList.getFineStaff());
                       intWf=intWf+Integer.parseInt(mList.getFineWomenStaff());
                       intoF=intoF+Integer.parseInt(mList.getOutsourcingFemale());
                       intoM=intoM+Integer.parseInt(mList.getOutsourcingMale());
                        if(!mList.getOutsourcingValue().equals("")){
                            list.add(mList);
                        }
                   }
                   }

                TextView txtFineStaff=findViewById(R.id.activityworkingstaff_finestaff);
                txtFineStaff.setText(intF+"명");
                TextView txtFineStaffWomen=findViewById(R.id.activityworkingstaff_finewomen);
                txtFineStaffWomen.setText(intWf+"명");
                TextView txtOutsourcingMale=findViewById(R.id.activityworkingstaff_outsourcingwmen);
                txtOutsourcingMale.setText(intoF+"명");
                TextView txtOutsourcingFemale=findViewById(R.id.activityworkingstaff_outsourcingmen);
                txtOutsourcingFemale.setText(intoM+"명");
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