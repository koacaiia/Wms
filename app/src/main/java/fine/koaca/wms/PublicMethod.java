package fine.koaca.wms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PublicMethod {
    Activity activity;
    ArrayList<String> list=new ArrayList<>();
    public PublicMethod(Activity activity){
        this.activity=activity;
    }
    public PublicMethod(ArrayList<String> list){
        this.list=list;
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

    public void putNewDataUpdateAlarm(String nickName,String alertDepot,String dialogTitle, String consigneeName, String out,
                                      RequestQueue requestQueue) {
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String timeDate=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());

        WorkingMessageList messageList=new WorkingMessageList();


        messageList.setNickName(nickName);
        messageList.setTime(timeStamp);
        messageList.setMsg(dialogTitle);
        messageList.setDate(timeDate);
        messageList.setConsignee(consigneeName);
        messageList.setInOutCargo(out);

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("WorkingMessage"+"/"+nickName+"_"+timeStamp);
        databaseReference.setValue(messageList);

        PushFcmProgress push=new PushFcmProgress(requestQueue);
        push.sendAlertMessage(alertDepot,nickName,dialogTitle,"WorkingMessage");

        Intent intent=new Intent(activity,WorkingMessageData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public void upLoadPictures(String nickName,String consigneeName,String inoutCargo){
        ArrayList<String> uriList=new ArrayList<>();
        int listSize=list.size();
        String date=new SimpleDateFormat("yyyy년MM월dd일").format(new Date());
        String dateNtime=new SimpleDateFormat("yyyy년MM월dd일HH시mm분ss초").format(new Date());
        String refPath;
        FirebaseStorage storage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");

        for(int i=0;i<listSize;i++){
            Uri uriValue=Uri.fromFile(new File(list.get(i)));
            refPath=date+"/"+consigneeName+"/"+inoutCargo+"/"+nickName+System.currentTimeMillis()+".jpg";
            StorageReference storageReference=storage.getReference().child("image/"+refPath);
            int finalI = i;
            storageReference.putFile(uriValue)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   uriList.add(String.valueOf(uri));
                                   WorkingMessageList messageList=new WorkingMessageList();

                                   if((finalI +1)==listSize){
                                       messageList.setNickName(nickName);
                                       messageList.setTime(dateNtime);
                                       messageList.setDate(date);
                                       messageList.setMsg(consigneeName+"_"+inoutCargo+"_ 사진 업로드");
                                       messageList.setInOutCargo(inoutCargo);

                                       try {
                                           switch (finalI) {
                                               case 0:
                                                   messageList.setUri0(uriList.get(0));
                                                   break;
                                               case 1:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   break;
                                               case 2:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   messageList.setUri2(uriList.get(2));
                                                   break;
                                               case 3:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   messageList.setUri2(uriList.get(2));
                                                   messageList.setUri3(uriList.get(3));
                                                   break;
                                               case 4:
                                                   messageList.setUri0(uriList.get(0));
                                                   messageList.setUri1(uriList.get(1));
                                                   messageList.setUri2(uriList.get(2));
                                                   messageList.setUri3(uriList.get(3));
                                                   messageList.setUri4(uriList.get(4));
                                                   break;
                                           }
                                       }catch(IndexOutOfBoundsException e){
                                           messageList.setMsg(e.toString());
                                       }

                                       FirebaseDatabase database=FirebaseDatabase.getInstance();
                                       DatabaseReference databaseReference=
                                               database.getReference("WorkingMessage" +"/"+nickName+"_"+dateNtime   );
                                       databaseReference.setValue(messageList);
                                   }

                               }
                           });
                        }
                    });

        }

    }
}
