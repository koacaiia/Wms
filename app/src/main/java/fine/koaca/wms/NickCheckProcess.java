package fine.koaca.wms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class NickCheckProcess {

    static private final String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;
    Activity activity;
    String nickName;
    String depotName;

    public NickCheckProcess(Activity activity) {
        this.activity=activity;
    }

    public void checkNickName(){
        sharedPref=activity.getSharedPreferences(SHARE_NAME,MODE_PRIVATE);

        if (sharedPref.getString("depotName", null) == null) {

            putUserInformation();
            return;
        }
    }

    public void putUserInformation() {
        sharedPref=activity.getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        editor=sharedPref.edit();
        ArrayList<String> depotSort=new ArrayList<String>();
        depotSort.add("1물류(02010810)");
        depotSort.add("2물류(02010027)");
        depotSort.add("(주)화인통상 창고사업부");

        ArrayList selectedItems=new ArrayList();
        int defaultItem=0;
        selectedItems.add(defaultItem);

        String[] depotSortList=depotSort.toArray(new String[depotSort.size()]);
        AlertDialog.Builder sortBuilder=new AlertDialog.Builder(activity);

        View view=activity.getLayoutInflater().inflate(R.layout.user_reg,null);
        EditText reg_edit=view.findViewById(R.id.user_reg_Edit);

        Button reg_button=view.findViewById(R.id.user_reg_button);
        TextView reg_depot=view.findViewById(R.id.user_reg_depot);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickName=reg_edit.getText().toString();
                reg_depot.setText(depotName+"_"+nickName+"으로 사용자 등록을"+"\n"+" 진행할려면 하단 confirm 버튼 클릭 바랍니다.");

            }
        });

        sortBuilder.setView(view);
        sortBuilder.setSingleChoiceItems(depotSortList,defaultItem,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                depotName=depotSortList[which];
                reg_depot.setText("부서명_"+depotName+"로 확인");

            }
        });
        sortBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putString("depotName",depotName);
                editor.putString("nickName",nickName);
                editor.apply();
                Toast.makeText(activity.getApplicationContext(), depotName+"__"+nickName+"로 사용자 등록 성공 하였습니다.", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(activity.getApplicationContext(),TitleActivity.class);
                activity.startActivity(intent);
            }
        });
        sortBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        sortBuilder.show();
    }
    }

