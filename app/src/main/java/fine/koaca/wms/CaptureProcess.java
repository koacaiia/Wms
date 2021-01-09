package fine.koaca.wms;

import android.content.ContentResolver;
import android.content.ContentValues;
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

import fine.koaca.MyApplication;

import static android.content.Context.MODE_PRIVATE;

public class CaptureProcess implements SurfaceHolder.Callback {
    Camera camera;
    CameraCapture mainActivity;
    WindowDegree windowDegree;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference recvRef;
    ContentResolver contentResolver;
    ContentValues contentValues;
    CalendarPick calendarPick;
    String captureItem = "";
    ArrayList<ImageViewList> captureImageList=new ArrayList<ImageViewList>();
    ImageViewListAdapter adapter;
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
                contentResolver = mainActivity.getContentResolver();
                Log.i("koacaiia",contentResolver+"__information");
                contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
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

                camera.startPreview();
                captureImageList=queryAllPictures();
                adapter=new ImageViewListAdapter(captureImageList);
                adapter.notifyDataSetChanged();
                mainActivity.recyclerView.setAdapter(adapter);
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

    public void firebaseCameraUpLoad(Uri imageUri, String date_today, String captureItem, String uploadItem) {

        storage = FirebaseStorage.getInstance("gs://wmsysk.appspot.com");
        storageReference = storage.getReference();
        String strRef=date_today+"/"+captureItem+"/"+System.currentTimeMillis()+".jpg";
        recvRef = storageReference.child("images/" +strRef);

        recvRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(mainActivity, "공용서버에" +captureItem +"("+uploadItem+")"+ "사진이 성공적으로 UpLoad 되었습니다.",
                                Toast.LENGTH_SHORT).show();
                        String msg=captureItem +"("+uploadItem+")"+"_사진 서버에 업로드 성공";

                        recvRef.getDownloadUrl().
                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUri=String.valueOf(uri);
                                        putMessage(msg,imageUri,captureItem,uploadItem);
                                    }
                                });
                        }                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mainActivity, "공용서버에" + captureItem + "사진이 UpLoad에 실패했습니다..", Toast.LENGTH_SHORT).show();
                        String msg=captureItem+"_사진 서버에 업로드 실패";
                        putMessage(msg, "", captureItem, uploadItem);
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



    public void putMessage(String msg, String imageUri, String captureItem, String uploadItem){
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
        messageList.setUri(imageUri);
        messageList.setDate(date);
        messageList.setConsignee(captureItem);
        messageList.setInOutCargo(uploadItem);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("WorkingMessage"+"/"+nick+"_"+System.currentTimeMillis()+msg);
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
        captureImageList=new ArrayList<>();
        Uri uri =MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=mainActivity.getContentResolver().query(uri,projection,null,null,MediaStore.MediaColumns.DATE_ADDED +
                 " desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext()){
            String uriI=cursor.getString(columnsDataIndex);

            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Fine/입,출고");
            String strFile=String.valueOf(file);
            Log.i("koacaiia","publicPictureFolder"+strFile);

            if(uriI.startsWith(strFile)){
                ImageViewList lists=new ImageViewList(uriI);
                captureImageList.add(lists);
            }

        }
            cursor.close();
        return captureImageList;
    }
}








