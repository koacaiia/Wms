package fine.koaca.wms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublicMethod {
    Activity activity;
    public PublicMethod(Activity activity){
        this.activity=activity;

    }
    public void putContent(String pathValue){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference(pathValue);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);

        ArrayList<String> incargoContent=new ArrayList<>();
        incargoContent.add("컨테이너 진입");
        incargoContent.add("입고작업");
        incargoContent.add("검수완료");
        incargoContent.add("창고반입");

        String[] incargoContentList=incargoContent.toArray(new String[incargoContent.size()]);

        builder.setTitle("입고현황 변경사항")
                .setSingleChoiceItems(incargoContentList,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String contentValue=incargoContentList[which];
                        Map<String,Object> putValue=new HashMap<>();
                        putValue.put("working",contentValue);
                        databaseReference.updateChildren(putValue);

                        Toast.makeText(activity,contentValue+"로 작업 현황 등록 합니다.",Toast.LENGTH_SHORT).show();
                        dialog.cancel();

                    }
                })
                .show();


    }
    public ArrayList<String> getPictureLists(){
        ArrayList<String> imageViewLists=new ArrayList<>();
        Uri uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=activity.getContentResolver().query(uri,projection,null,null,MediaStore.MediaColumns.DATE_ADDED+" desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext()){
            String uriI=cursor.getString(columnsDataIndex);
            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Fine/입,출고/Resize");
            String strFile=String.valueOf(file);
            if(uriI.startsWith(strFile)){
                imageViewLists.add(uriI);
                Log.i("TestValue","ImageViewLists:::::"+strFile);
            }
        }
        cursor.close();
        return imageViewLists;
    }
}