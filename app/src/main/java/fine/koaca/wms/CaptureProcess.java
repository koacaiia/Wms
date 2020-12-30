package fine.koaca.wms;

import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class CaptureProcess implements SurfaceHolder.Callback{
    Camera camera;
    CameraCapture mainActivity;
    WindowDegree windowDegree;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference recvRef;
    ContentResolver contentResolver;
    ContentValues contentValues;
    CalendarPick calendarPick;
    String captureItem="";


    public CaptureProcess(CameraCapture mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void preViewProcess(){
        mainActivity.surfaceHolder.addCallback(this);
        mainActivity.surfaceHolder.setType(mainActivity.surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        camera=Camera.open();
        windowDegree=new fine.koaca.wms.WindowDegree(mainActivity);
        int degree=windowDegree.getDegree();
        camera.setDisplayOrientation(degree);
        try {
            camera.setPreviewDisplay(mainActivity.surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    public void captureProcess(String date_today){
        Camera.PictureCallback callback=new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                OutputStream fos=null;
                Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
                windowDegree=new fine.koaca.wms.WindowDegree(mainActivity);
                int degree=windowDegree.getDegree();
                bitmap=rotate(bitmap,degree);
//                bitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
                contentResolver=mainActivity.getContentResolver();
                contentValues=new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,System.currentTimeMillis()+".jpg");
                contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/*");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/Fine/2");
                Uri imageUri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

                try {
                    fos=contentResolver.openOutputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                AlertDialog.Builder builder=new AlertDialog.Builder(mainActivity);
                builder.setMessage("선택된 사진 유형은 버튼클릭시 서버에 저장 됩니다.");
                builder.setTitle("사진 유형 선택");


                builder.setPositiveButton("입고 사진 전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureItem="InCargo";
                        firebaseCameraUpLoad(imageUri,date_today,captureItem);
                        Toast.makeText(mainActivity, "서버에 입고 사진이 UpLoad 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("출고 사진 전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureItem="OutCargo";
                        firebaseCameraUpLoad(imageUri,date_today,captureItem);
                        Toast.makeText(mainActivity, "서버에 출고 사진이 UpLoad 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNeutralButton("기타사진 전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureItem="Etc";
                        firebaseCameraUpLoad(imageUri,date_today,captureItem);
                        Toast.makeText(mainActivity, "서버에 기타 사진이 UpLoad 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

                camera.startPreview();

            }
        };
        camera.takePicture(null,null,callback);

    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        Matrix matrix=new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        preViewProcess();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    public void firebaseCameraUpLoad(Uri imageUri, String date_today, String captureItem){

        storage=FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        storageReference=storage.getReference();
        recvRef=storageReference.child("images/"+date_today+"/"+captureItem+"/"+System.currentTimeMillis()+".jpg");

        recvRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("koacaiia",imageUri+"uploading successed");

                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mainActivity, "failure Server Uploading", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    public void listAllFiles() {
        calendarPick=new fine.koaca.wms.CalendarPick();
        calendarPick.CalendarCall();
        String date_today=calendarPick.date_today;
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        StorageReference listRef = storage.getReference().child("/images/"+date_today+"/");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            String itemName=item.getName();
                            StorageReference itemRef=storage.getReference().child("/images/"+date_today+"/"+itemName);
                            String dirPath="/storage/emulated/0/"+Environment.DIRECTORY_PICTURES+"/Fine/입,출고";
                            File dirFile=new File(dirPath);
                            if(!dirFile.exists()){
                                dirFile.mkdirs();}
                            File fileName=new File(dirPath,itemName);
                            if(fileName.exists()){
                                Log.i("koacaiia",fileName+"__exist");
                                }else{
                                try {
                                    fileName.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();}
                                itemRef.getFile(fileName)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("koacaiia",fileName+"__downloading failed");
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    public void bitmapReturn(File tempFile, String itemName){

        OutputStream fos=null;
        Bitmap bitmap=BitmapFactory.decodeFile(String.valueOf(tempFile));
        contentResolver=mainActivity.getContentResolver();
        contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,itemName+".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/*");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+"/Fine/DownLoad");
        Uri imageUri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Log.i("fileName3?",itemName);


        try {
            fos=contentResolver.openOutputStream(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
//        imageUri= Uri.parse(String.valueOf(imageUri));
//        Cursor cursor = mainActivity.getContentResolver().query(imageUri, null, null, null, null );
//        assert cursor != null;
//        cursor.moveToNext();
//        String imageFilePath = cursor.getString( cursor.getColumnIndex( "_data" ) );
//        cursor.close();
        mainActivity.sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
//        String filePath="/document/primary:Pictures/Fine/DownLoad/";
//
//        File file=new File(filePath,itemName+".jpg");
//        String filePath2=file.getPath();


//      if(file.exists()){
//          Log.i("fileName",file+"exists"+filePath2);
//
//
//      }else{
//          Log.i("fileName",file+"noexists"+filePath+"_imagePath_"+imageFilePath);
//      }
    }



}





