package fine.koaca.wms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

public class GalleryPictureList extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageViewActivityAdapter adapter;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        recyclerView=findViewById(R.id.imageViewActivity_recyclerView);
        GridLayoutManager manager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(manager);
        PublicMethod publicMethod=new PublicMethod(this);
        list=new ArrayList<>();
        Uri uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=getContentResolver().query(uri,projection,null,null,MediaStore.MediaColumns.DATE_ADDED+" desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while(cursor.moveToNext()){
            String uri1=cursor.getString(columnsDataIndex);
            File file=new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
            String strFile=String.valueOf(file);
            if(uri1.startsWith(strFile)){
                list.add(uri1);
            }
        }
        cursor.close();
        adapter=new ImageViewActivityAdapter(list);
        recyclerView.setAdapter(adapter);


            }
}