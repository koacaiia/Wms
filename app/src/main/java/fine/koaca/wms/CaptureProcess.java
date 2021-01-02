package fine.koaca.wms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.Date;

import fine.koaca.MyApplication;

public class CaptureProcess implements SurfaceHolder.Callback {
    Camera camera;
    CameraCapture mainActivity;
    WindowDegree windowDegree;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference recvRef;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    ContentResolver contentResolver;
    ContentValues contentValues;
    CalendarPick calendarPick;
    String captureItem = "";
    WorkingMessageData messageData=new WorkingMessageData();

    public CaptureProcess(CameraCapture mainActivity) {
        this.mainActivity = mainActivity;
    }
    public CaptureProcess(){
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
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                windowDegree = new WindowDegree(mainActivity);
                int degree = windowDegree.getDegree();
                bitmap = rotate(bitmap, degree);
//                bitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
                contentResolver = mainActivity.getContentResolver();
                Log.i("koacaiia",contentResolver+"__information");
                contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Fine/2");
                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                try {
                    fos = contentResolver.openOutputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("선택된 사진 유형은 버튼클릭시 서버에 업로드 됩니다.");
                builder.setTitle("사진 유형 선택");
                String workingMessage="입고 사진 촬영";
//                messageData.workingMessageList(workingMessage);


                builder.setPositiveButton("입고사진 업로드", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureItem = "InCargo";
                        firebaseCameraUpLoad(imageUri, date_today, captureItem);

                    }
                });

                builder.setNegativeButton("출고사진 업로드", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureItem = "OutCargo";
                        firebaseCameraUpLoad(imageUri, date_today, captureItem);

                    }
                });

                builder.setNeutralButton("기타사진 업로드", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureItem = "Etc";
                        firebaseCameraUpLoad(imageUri, date_today, captureItem);

                    }
                });
                builder.show();
                camera.startPreview();
            }
        };
        camera.takePicture(null, null, callback);

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

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    public void firebaseCameraUpLoad(Uri imageUri, String date_today, String captureItem) {

        storage = FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        storageReference = storage.getReference();
        recvRef = storageReference.child("images/" + date_today + "/" + captureItem + "/" + System.currentTimeMillis() + ".jpg");

        recvRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(mainActivity, "공용서버에" + captureItem + "사진이 성공적으로 UpLoad 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mainActivity, "failure Server Uploading", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void listAllFiles(String dataMessage, String downLoadingItems) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        StorageReference listRef = storage.getReference().child("/images/" + dataMessage + "/" + downLoadingItems+"/");

        Log.i("koacaiia", listRef + "__lnit_Executed");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            String itemName = item.getName();
                            Log.i("koacaiia", itemName + "listAllExecuted");
                            StorageReference itemRef = storage.getReference().child("/images/" + dataMessage + "/" + downLoadingItems +
                                    "/" + itemName);
                            String dirPath = "/storage/emulated/0/" + Environment.DIRECTORY_PICTURES + "/Fine/입,출고";
                            File dirFile = new File(dirPath);
                            if (!dirFile.exists()) {
                                dirFile.mkdirs();
                                Log.i("koaca", dirFile + "생성");
                            }
                            File fileName = new File(dirPath, itemName);
                            if (fileName.exists()) {
                                Log.i("koacaiia", fileName + "__exist");
                            } else {
                                try {
                                    fileName.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                itemRef.getFile(fileName)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                Log.i("koacaiia", fileName + "__downLoad");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("koacaiia", fileName + "__downloading failed");

                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("koacaiia", listRef + "__exist");
                    }
                });

    }

    public void listAllFiles() {
        calendarPick = new CalendarPick();
        calendarPick.CalendarCall();
        String date_today = calendarPick.date_today;
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        StorageReference listRef = storage.getReference().child("/images/" + date_today + "/");
        Log.i("koacaiia", listRef + "__lnit_Executed" + date_today);
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            String itemName = item.getName();
                            StorageReference itemRef = storage.getReference().child("/images/" + date_today + "/" + itemName);
                            String dirPath = "/storage/emulated/0/" + Environment.DIRECTORY_PICTURES + "/Fine/입,출고";
                            File dirFile = new File(dirPath);
                            if (!dirFile.exists()) {
                                dirFile.mkdirs();
                            }
                            File fileName = new File(dirPath, itemName);
                            if (fileName.exists()) {
                                Log.i("koacaiia", fileName + "__exist");
                            } else {
                                try {
                                    fileName.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                itemRef.getFile(fileName)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Log.i("koacaiia",fileName+"__downloading successed");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("koacaiia", fileName + "__downloading failed");
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


    public void bitmapReturn(File fileName, String itemName) {

        OutputStream fos = null;
        File tempFile=null;

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
//        imageUri= Uri.parse(String.valueOf(imageUri));
//        Cursor cursor = mainActivity.getContentResolver().query(imageUri, null, null, null, null );
//        assert cursor != null;
//        cursor.moveToNext();
//        String imageFilePath = cursor.getString( cursor.getColumnIndex( "_data" ) );
//        cursor.close();
//        mainActivity.sendBroadcast(new Intent(
//                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
//        String filePath="/document/primary:Pictures/Fine/DownLoad/";
//
//        File file=new File(filePath,itemName+".jpg");
//        String filePath2=file.getPath();

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
                            Log.i("koacaiia",fileName+"___?");
                            if(fileName.exists()){
                                Log.i("koacaiia",fileName+"___exists");
                            }else{
                            File tempFile= null;
                            try {
                                tempFile = File.createTempFile("koaca",".jpg");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.i("koacaiia",tempFile+"__cratedFile");

                            File finalTempFile = tempFile;
                                File finalTempFile1 = tempFile;
                                itemRef.getFile(tempFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                            OutputStream fos = null;

                                            bitmapReturn(finalTempFile1,itemName);
//                                            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(finalTempFile));
//                                            contentResolver= MyApplication.getAppContext().getContentResolver();
//                                            ContentValues contentValues = new ContentValues();
//                                            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, itemName );
//                                            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
//                                            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Fine/입,출고");
//                                            Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                                                    , contentValues);
//                                            Cursor cursor = MyApplication.getAppContext().getContentResolver().query(imageUri, null, null,
//                                                    null, null );
//                                            assert cursor != null;
//                                            cursor.moveToNext();
//                                            String imageFilePath = cursor.getString( cursor.getColumnIndex( "_data" ) );
//                                            Log.i("koacaiia",imageFilePath+"__uriTofile");
//                                            cursor.close();
//                                            try {
//                                                fos = contentResolver.openOutputStream(imageUri);
//                                            } catch (FileNotFoundException e) {
//                                                e.printStackTrace();
//                                            }
//                                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                                            Log.i("koacaiia",itemName+"__downloading successed");
                                            Toast.makeText(MyApplication.getAppContext(), "서버에서"+dataMessage+"_"+downLoadingItems+"사진 목록 " +
                                                            "DownLoad에 " +
                                                            "성공하였습니다.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("koacaiia","__downloading failed");
                                            Toast.makeText(MyApplication.getAppContext(), "서버에서"+dataMessage+"_"+downLoadingItems+"사진 목록 " +
                                                            "DownLoad에 " +
                                                            "실패하였습니다.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            if(tempFile.exists()){
                            tempFile.delete();
                            Log.i("koacaiia",tempFile+"_deleted");
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

    public void downLoadingUriBaseFile(String dataMessage,String downLoadingItems){
        FirebaseStorage storage=FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        StorageReference listRef=storage.getReference().child("/images/"+dataMessage+"/"+downLoadingItems+"/");
            Log.i("koacaiia1",listRef+"__executed");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.i("koacaiia2",listRef+"__before For 문 ListAll succeessed");
                        for(StorageReference item:listResult.getItems()){
                            String itemName=item.getName();
                            String dirPath="/storage/emulated/0/"+Environment.DIRECTORY_PICTURES+"/Fine/koaca";
                            File fileName=new File(dirPath,itemName);
                            if(fileName.exists()){
                                Log.i("koacaiia3",fileName+"__existed,Don't DownLoading");
                            }else{
                                fileDownLoading(dataMessage,downLoadingItems,itemName,fileName);
                                Log.i("koacaiia4",fileName+"__is Not Exists DownLoading Init");
                            }
                        }

                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("koacaiia5",listRef+"__ListAll 문 Failed");                  }
                });

    }

    private void fileDownLoading(String dataMessage, String downLoadingItems, String itemName, File fileName) {
        FirebaseStorage storage=FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        StorageReference itemRef=storage.getReference().child("/images/"+dataMessage+"/"+downLoadingItems+"/"+itemName);
Log.i("koacaiia6",itemRef+"__fileDownLoading Init");
        itemRef.getFile(fileName)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.i("koacaiia7",fileName+"+__after fileDownLoading Init downloading successed ");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("koacaiia8",fileName+"__after fileDownLoading Init downloading Failed");

            }
        });
    }




    }








