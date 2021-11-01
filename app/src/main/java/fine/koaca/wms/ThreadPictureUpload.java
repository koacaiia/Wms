package fine.koaca.wms;

import android.net.Uri;
import android.util.Log;

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

public class ThreadPictureUpload extends Thread{
    String nickName;
    String consigneeName;
    String inoutCargo;
    String keyValue;
    String deptName;
    ArrayList<String> list;
    public ThreadPictureUpload(ArrayList<String> list,String nickName,String consigneeName,String inoutCargo,String keyValue,
                               String deptName){
        this.list=list;
        this.nickName=nickName;
        this.consigneeName=consigneeName;
        this.inoutCargo=inoutCargo;
        this.keyValue=keyValue;
        this.deptName=deptName;
    }
    @Override
    public void run() {


        ArrayList<String> uriList=new ArrayList<>();
        int listSize=list.size();
        Log.i("TestValue","listContent1:::"+list.get(0));
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dateNtime=new SimpleDateFormat("yyyy년MM월dd일HH시mm분ss초").format(new Date());
        String refPath;
        FirebaseStorage storage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");

        for(int i=0;i<listSize;i++){
            Log.i("TestValue","listContent2:::"+list.get(0));
            Uri uriValue=Uri.fromFile(new File(list.get(i)));
            refPath=deptName+"/"+date+"/"+inoutCargo+"/"+keyValue+"/"+nickName+System.currentTimeMillis()+".jpg";
            StorageReference storageReference=storage.getReference().child("images/"+refPath);
            StorageReference consigneeReference;
            if(inoutCargo.equals("장비_시설물")){
                consigneeReference=
                        storage.getReference().child(deptName+"/"+
                                inoutCargo+"/"+consigneeName+"/"+keyValue+"/"+nickName+System.currentTimeMillis()+".jpg");

            }else if(inoutCargo.equals("Pallet")){
                consigneeReference=
                        storage.getReference().child(deptName+"/"+
                                inoutCargo+"/"+consigneeName+"/"+keyValue+"/"+nickName+System.currentTimeMillis()+".jpg");
            }else{
                consigneeReference=
                        storage.getReference().child("ConsigneeValue/"+consigneeName+"/"+keyValue+"/"+nickName+System.currentTimeMillis()+".jpg" );
            }


            int finalI = i;

            consigneeReference.putFile(uriValue).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
            storageReference.putFile(uriValue)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uriList.add(String.valueOf(uri));
                                    WorkingMessageList messageList=new WorkingMessageList();

                                    if((uriList.size())==listSize){
                                        messageList.setConsignee(consigneeName);
                                        messageList.setNickName(nickName);
                                        messageList.setTime(dateNtime);
                                        messageList.setDate(date);
                                        messageList.setMsg(consigneeName+"_"+inoutCargo+"_ 사진 업로드");
                                        messageList.setInOutCargo(inoutCargo);
                                        messageList.setKeyValue(keyValue);

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
                                                case 5:
                                                    messageList.setUri0(uriList.get(0));
                                                    messageList.setUri1(uriList.get(1));
                                                    messageList.setUri2(uriList.get(2));
                                                    messageList.setUri3(uriList.get(3));
                                                    messageList.setUri4(uriList.get(4));
                                                    messageList.setUri5(uriList.get(5));
                                                case 6:
                                                    messageList.setUri0(uriList.get(0));
                                                    messageList.setUri1(uriList.get(1));
                                                    messageList.setUri2(uriList.get(2));
                                                    messageList.setUri3(uriList.get(3));
                                                    messageList.setUri4(uriList.get(4));
                                                    messageList.setUri5(uriList.get(5));
                                                    messageList.setUri6(uriList.get(6));
                                                    break;
                                            }
                                        }catch(IndexOutOfBoundsException e){
                                            messageList.setMsg(e.toString());
                                        }

                                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference=
                                                database.getReference("DeptName/"+deptName +"/WorkingMessage/"+nickName+"_"+dateNtime   );
                                        databaseReference.setValue(messageList);

//                                        sendPushMessage(deptName,nickName,consigneeName+"_"+inoutCargo+"_ 사진 업로드","CameraUpLoad");

                                    }

                                }
                            });
                        }
                    });

        }
    }
}
