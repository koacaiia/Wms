package fine.koaca.wms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imageView;
    FloatingActionButton fab;
    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Intent intent=getIntent();
        uri=intent.getStringExtra("uri");

        imageView=findViewById(R.id.img);
        fab=findViewById(R.id.floatingActionButton);

        Glide.with(this).asBitmap()
                .load(uri)
                .into(new SimpleTarget<Bitmap>() {
                          @Override
                          public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                              imageView.setImageBitmap(resource);
                              fab.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      glideImageToSave(resource);
                                  }
                              });
                          }
                      });



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

    }
}