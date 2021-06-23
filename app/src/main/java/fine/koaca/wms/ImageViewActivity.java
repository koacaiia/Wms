package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageViewActivity extends AppCompatActivity implements ImageViewActivityAdapter.ImageViewClicked {
    RecyclerView imageViewRecycler;
    FloatingActionButton fab;
    String[] uri;
    ArrayList<String> list;
    ImageViewActivityAdapter adapter;
    Context context;
    String message;
    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        context=this.getApplicationContext();
        Intent intent=getIntent();
        uri=intent.getStringArrayExtra("uri");
        message=intent.getStringExtra("message");

        list=new ArrayList<String>(Arrays.asList(uri));
        txtMessage=findViewById(R.id.imageViewActivityTitle);

        txtMessage.setText(message);
        txtMessage.setGravity(Gravity.CENTER);


        imageViewRecycler=findViewById(R.id.imageViewActivity_recyclerView);
        GridLayoutManager manager=new GridLayoutManager(this,2);
        imageViewRecycler.setLayoutManager(manager);
        adapter=new ImageViewActivityAdapter(list,this);
        imageViewRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }
    public void glideImageToSave(Bitmap resource){
        ContentResolver resolver=getContentResolver();
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME,System.currentTimeMillis()+".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/*");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/Fine/DownLoad");
        Uri imageUri=resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        OutputStream fos= null;
        try {
            fos=resolver.openOutputStream(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        resource.compress(Bitmap.CompressFormat.JPEG,100,fos);
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this,"Saved InPath PICTURES/Fine/DownLoad",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(ImageViewActivity.this);
        View view=getLayoutInflater().inflate(R.layout.imageview_list,null);

        ImageView imageView=view.findViewById(R.id.captureimageview);

        Glide.with(view).asBitmap()
                .load(uri[position])
                .into(new SimpleTarget<Bitmap>() {
                          @Override
                          public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                             imageView.setImageBitmap(resource);

                          }});
        builder.setTitle("사진저장 확인 ")
                .setView(view)
                .setMessage("저장된 사진은 PICTURES/Fine/DownLoad 경로에 저장 됩니다."+"\n"+"업무에 참고 하시기 바랍니다."+"\n")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Glide.with(ImageViewActivity.this).asBitmap()
                                .load(uri[position])
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        glideImageToSave(resource);
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();



    }
}