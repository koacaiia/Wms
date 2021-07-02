package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fine.koaca.MyApplication;

import static android.content.Context.MODE_PRIVATE;

public class CaptureProcess implements SurfaceHolder.Callback {
    Camera camera;
    CameraCapture mainActivity=new CameraCapture();
    WindowDegree windowDegree;
    ContentResolver contentResolver;
    ContentValues contentValues;
    CalendarPick calendarPick;
    ArrayList<ImageViewList> captureImageList=new ArrayList<ImageViewList>();
    ImageViewListAdapter adapter;
    ArrayList<String> uriString=new ArrayList<String>();
    Context context;

    public CaptureProcess(CameraCapture mainActivity,ImageViewListAdapter adapter) {
        this.mainActivity = mainActivity;
        this.adapter=adapter;
    }

    public CaptureProcess() {

    }


    public void preViewProcess() {
        mainActivity.surfaceHolder.addCallback(this);
        mainActivity.surfaceHolder.setType(mainActivity.surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        camera = Camera.open();
        windowDegree = new WindowDegree(mainActivity);
        int degree = windowDegree.getDegree();
        camera.setDisplayOrientation(degree);

        try {
            camera.setPreviewDisplay(mainActivity.surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    public void captureProcess(String date_today) {
        Camera.PictureCallback callback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                OutputStream fos = null;
                OutputStream fosRe=null;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                windowDegree = new WindowDegree(mainActivity);
                int degree = windowDegree.getDegree();
                bitmap = rotate(bitmap, degree);
                contentResolver = mainActivity.getContentResolver();
                contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Fine/입,출고");
                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Fine/입,출고/Resize");
                Uri imageUriRe = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                try {
                    fos = contentResolver.openOutputStream(imageUri);
                    fosRe=contentResolver.openOutputStream(imageUriRe);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                bitmap.compress(Bitmap.CompressFormat.JPEG,30,fosRe);


                try {
                    assert fos != null;
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    assert fosRe != null;
                    fosRe.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                camera.startPreview();

//                captureImageList=queryAllPictures();
                mainActivity.list=queryAllPictures();
//                mainActivity.queryAllList(captureImageList);
                mainActivity.adapter.notifyDataSetChanged();


//
//                adapter=new ImageViewListAdapter(queryAllPictures());
//                adapter.notifyDataSetChanged();
//               mainActivity.recyclerView.setAdapter(adapter);
            }
        };
        camera.takePicture(null, null, callback);

        setmAutoFocus();

    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        preViewProcess();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        preViewProcess();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if(camera!=null){
            camera.stopPreview();
            camera.release();
            camera=null;
        }
    }

    public synchronized void firebaseCameraUpLoad(Uri imageUri, String captureItem, String uploadItem, String nick,
                                                  String message, String strRef, int i, int arSize) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
        StorageReference storageReference = storage.getReference();
        StorageReference recvRef = storageReference.child("images/" + strRef);

        String timeStamp = new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        String timeStamp_date = new SimpleDateFormat("yyyy년MM월dd일").format(new Date());


        recvRef.putFile(imageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   receivedUri(recvRef,nick,timeStamp,message,timeStamp_date,captureItem,uploadItem,i,arSize);


                }

            })
                .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mainActivity, "공용서버에" + captureItem + "사진이 UpLoad에 실패했습니다..", Toast.LENGTH_SHORT).show();
//                    String msg = captureItem + "_사진 서버에 업로드 실패후 재전송시도";
//                    putMessage(msg, "", captureItem, uploadItem);
//                    receivedUri(recvRef,nick,timeStamp,message,timeStamp_date,captureItem,uploadItem, i,arSize);
                }
            });

    }
    public void bitmapReturn(File fileName, String itemName) {

        OutputStream fos = null;

        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(fileName));
        contentResolver = MyApplication.getAppContext().getContentResolver();
        contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,itemName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Fine/입,출고");
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            fos = contentResolver.openOutputStream(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void downLoadingUri(String dataMessage,String downLoadingItems){
        FirebaseStorage storage=FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        StorageReference listRef = storage.getReference().child("/images/" + dataMessage + "/" + downLoadingItems+"/");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for(StorageReference item:listResult.getItems()) {
                            String itemName=item.getName();
                            StorageReference itemRef = storage.getReference().child("/images/" + dataMessage + "/" + downLoadingItems + "/" + itemName);

                            String dirPath = "/storage/emulated/0/" + Environment.DIRECTORY_PICTURES + "/Fine/입,출고";
                            File fileName=new File(dirPath,itemName);

                            if(fileName.exists()){

                            }else{
                            File tempFile= null;
                            try {
                                tempFile = File.createTempFile("koaca",".jpg");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            File finalTempFile = tempFile;
                                File finalTempFile1 = tempFile;
                                itemRef.getFile(tempFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                            OutputStream fos = null;

                                            bitmapReturn(finalTempFile1,itemName);
                                            Toast.makeText(MyApplication.getAppContext(), "서버에서"+dataMessage+"_"+downLoadingItems+"사진 목록 " +
                                                            "DownLoad에 " +
                                                            "성공하였습니다.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MyApplication.getAppContext(), "서버에서"+dataMessage+"_"+downLoadingItems+"사진 목록 " +
                                                            "DownLoad에 " +
                                                            "실패하였습니다.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            if(tempFile.exists()){
                            tempFile.delete();
                            }
                        }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("koacaiia","DownLoad List Transfer Failed");
                    }
                });
    }



    public void putMessage(String msg, String imageUri, String captureItem, String uploadItem){
        @SuppressLint("SimpleDateFormat")
        String timeStamp=new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분ss초").format(new Date());
        SharedPreferences sharedPreferences;
        sharedPreferences=mainActivity.getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nick;
        calendarPick=new CalendarPick();
        calendarPick.CalendarCall();
        String date=calendarPick.date_today;
        nick=sharedPreferences.getString("nickName","koaca");
        WorkingMessageList messageList=new WorkingMessageList();
        messageList.setNickName(nick);
        messageList.setTime(timeStamp);
        messageList.setMsg(msg);
//        messageList.setUri(imageUri);
        messageList.setDate(date);
        messageList.setConsignee(captureItem);
        messageList.setInOutCargo(uploadItem);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference =
                database.getReference("WorkingMessage"+"/"+nick+"_"+date+"_"+System.currentTimeMillis()+msg);
        databaseReference.setValue(messageList);

    }

    public void setmAutoFocus(){
        camera.autoFocus(mAutoFocus);
    }

    Camera.AutoFocusCallback mAutoFocus=new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };


    public ArrayList<ImageViewList> queryAllPictures(){
//        captureImageList=new ArrayList<ImageViewList>();
        captureImageList.clear();
        Uri uri =MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=mainActivity.getContentResolver().query(uri,projection,null,null,MediaStore.MediaColumns.DATE_ADDED +
                 " desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext()){
            String uriI=cursor.getString(columnsDataIndex);

            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Fine/입,출고/Resize");
            String strFile=String.valueOf(file);

            if(uriI.startsWith(strFile)){
                ImageViewList lists=new ImageViewList(uriI);
                captureImageList.add(lists);

            }

        }

        cursor.close();

        return captureImageList;
    }

    public void receivedUri(StorageReference recvRef, String nick, String timeStamp, String msg, String timeStamp_date,
                            String consigneeName, String inoutCargo, int i,int arSize){


        recvRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>(){

                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUri=String.valueOf(uri);

//                        uriString.add(imageUri);
                        if(uriString.size()==0){
                            AlertDialog.Builder builder=new AlertDialog.Builder(mainActivity);
                            builder.setTitle("전송실패")
                                    .setMessage("전송중 오류 발생하였습니다.다시 진행 바랍니다.")
                                    .setPositiveButton("재전송", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mainActivity.upCapturePictures(consigneeName,inoutCargo);
                                        }
                                    })
                                    .setNegativeButton("카톡으로 전송", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String message=nick+":"+consigneeName+"_"+inoutCargo+"사진 전달";

                                            mainActivity.sendMessage(message+"(카톡)");
                                            Intent intent=new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, message );
                                            intent.setPackage("com.kakao.talk");
                                            mainActivity.startActivity(intent);
                                        }
                                    })
                                    .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(mainActivity.getApplicationContext(), "사진 전송을 취소 합니다.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();
                            return;
                        }
//
                        WorkingMessageList messageList= new WorkingMessageList();
                        messageList.setNickName(nick);
                        messageList.setTime(timeStamp);
                        messageList.setMsg(msg);



                        if(uriString.size()-1==i){

                            String strUri0 = null;
                            String strUri1=null;
                            String strUri2=null;
                            String strUri3=null;
                            String strUri4=null;
                            switch(i){
                                case 0:
                                   strUri0=uriString.get(0);
                                   break;
                                case 1:
                                    strUri0=uriString.get(0);
                                    strUri1=uriString.get(1);
                                    break;
                                case 2:
                                    strUri0=uriString.get(0);
                                    strUri1=uriString.get(1);
                                    strUri2=uriString.get(2);
                                    break;
                                case 3:
                                    strUri0=uriString.get(0);
                                    strUri1=uriString.get(1);
                                    strUri2=uriString.get(2);
                                    strUri3=uriString.get(3);
                                    break;
                                case 4:
                                    strUri0=uriString.get(0);
                                    strUri1=uriString.get(1);
                                    strUri2=uriString.get(2);
                                    strUri3=uriString.get(3);
                                    strUri4=uriString.get(4);
                                    break;
                            }

                            if(strUri0==null){
                                strUri0="";
                            }
                            if(strUri1==null){
                                strUri1="";
                            }
                            if(strUri2==null){
                                strUri2="";
                            }
                            if(strUri3==null){
                                strUri3="";
                            }
                            if(strUri4==null){
                                strUri4="";
                            }
                            messageList.setUri0(strUri0);
                            messageList.setUri1(strUri1);
                            messageList.setUri2(strUri2);
                            messageList.setUri3(strUri3);
                            messageList.setUri4(strUri4);

                            Toast.makeText(mainActivity,msg+"("+arSize+")"+"개의 사진을 전송 했습니다.",Toast.LENGTH_SHORT).show();
                        }


                        messageList.setDate(timeStamp_date);
                        messageList.setConsignee(consigneeName);
                        messageList.setInOutCargo(inoutCargo);
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=database.getReference("WorkingMessage"+
                                "/"+nick+"_"+timeStamp);
                        databaseReference.setValue(messageList);
                        if(arSize-1==i){
                            mainActivity.sendMessage(nick+":"+consigneeName+"_"+inoutCargo+"사진 전송");
                           mainActivity.initIntent();
                        }

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        putMessage("Failed Uri Received","",consigneeName,inoutCargo);


                    }
                });


    }





}








