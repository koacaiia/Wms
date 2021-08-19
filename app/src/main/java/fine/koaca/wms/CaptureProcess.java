package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fine.koaca.MyApplication;

import static android.content.Context.MODE_PRIVATE;

public class CaptureProcess implements SurfaceHolder.Callback {
    Camera camera;
    CameraCapture mainActivity=new CameraCapture();
    OutCargoActivity outCargoActivity=new OutCargoActivity();
    WindowDegree windowDegree;
    ContentResolver contentResolver;
    ContentValues contentValues;
    CalendarPick calendarPick;
    ArrayList<ImageViewList> captureImageList=new ArrayList<ImageViewList>();
    ImageViewListAdapter adapter;
    ArrayList<String> uriString=new ArrayList<String>();
    Activity activity;
    Incargo inCargoActivity=new Incargo();


    public CaptureProcess(CameraCapture mainActivity,ImageViewListAdapter adapter) {
        this.mainActivity = mainActivity;
        this.adapter=adapter;
    }


    public CaptureProcess(OutCargoActivity outCargoActivity) {
      this.outCargoActivity=outCargoActivity;
    }
    public CaptureProcess(Incargo inCargoActivity) {
        this.inCargoActivity=inCargoActivity;
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
                OutputStream fosRe=null;
                Bitmap src = BitmapFactory.decodeByteArray(data, 0, data.length);
                windowDegree = new WindowDegree(mainActivity);
                int degree = windowDegree.getDegree();
                src = rotate(src, degree);
                Bitmap bitmap=Bitmap.createBitmap(src.getWidth(),src.getHeight(),Bitmap.Config.ARGB_8888);
                Canvas canvas=new Canvas(bitmap);
                Paint paint=new Paint();
                paint.setTextSize(15);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawBitmap(src,0f,0f,null);
                String timeStamp=new SimpleDateFormat("yyyy년 MM월 dd일  E요일 HH시mm분ss초").format(new Date());
                canvas.drawText(timeStamp,20f,src.getHeight()-30f,paint);
//                windowDegree = new WindowDegree(mainActivity);
//                int degree = windowDegree.getDegree();
//                bitmap = rotate(bitmap, degree);
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


                mainActivity.list=queryAllPictures();
                mainActivity.adapter.notifyDataSetChanged();
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

    public void firebaseCameraUpLoad(Uri imageUri, String captureItem, String uploadItem, String nick,
                                                  String message, String strRef, int i, int arSize, String context) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
        StorageReference storageReference = storage.getReference();
        StorageReference recvRef = storageReference.child("images/" + strRef);

        recvRef.putFile(imageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   recvRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           String strUri=String.valueOf(uri);
                           uriString.add(strUri);



//                           if(uriString.size()==arSize){
//                               upLoadUriToDatabase(nick,message,captureItem,uploadItem,i,context);
//                           }
                       }
                   })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull @NotNull Exception e) {
                                   putMessage("Failed Uri Received","",captureItem,uploadItem);
                               }
                           });

                }

            })
                .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mainActivity, "공용서버에" + captureItem + "사진이 UpLoad에 실패했습니다..", Toast.LENGTH_SHORT).show();
                    String msg = captureItem + "_사진 서버에 업로드 실패후 재전송시도";
                    putMessage(msg, "", captureItem, uploadItem);

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


    public void failedUpLoad(String nick, String consigneeName, String inoutCargo, int arSize, int size, String context){


        AlertDialog.Builder builder;

        switch(context){

            case "OutCargoActivity":
                builder=new AlertDialog.Builder(outCargoActivity);
                break;
            case "CameraCapture":

                builder=new AlertDialog.Builder(mainActivity);
                break;
            case "Incargo":
                builder=new AlertDialog.Builder(inCargoActivity);
                break;


            default:
                throw new IllegalStateException("Unexpected value: " + context);
        }

        AlertDialog dialog=builder.create();

        builder.setTitle("전송실패")
                .setMessage("전송중 오류 발생하였습니다.다시 진행 바랍니다."+"\n"+"전송요청 사진:"+arSize+"장"+"\n"+"실재 서버 전송사진 숫자:"+size+"장")
                .setPositiveButton("재전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(context){

                            case "OutCargoActivity":

                                outCargoActivity.initIntent();
                                break;
                            case "CameraCapture":

                                mainActivity.initIntent();
                            case "Incargo":
                               inCargoActivity.initIntent();
                                break;
                        }

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
                        switch(context){

                            case "OutCargoActivity":

                                outCargoActivity.initIntent();
                                break;
                            case "CameraCapture":

                                mainActivity.initIntent();
                            case "Incargo":
                                inCargoActivity.initIntent();
                                break;
                        }
                    }
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();

    }
}








