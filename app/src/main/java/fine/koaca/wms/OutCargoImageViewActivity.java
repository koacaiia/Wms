package fine.koaca.wms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class OutCargoImageViewActivity extends AppCompatActivity implements ImageViewActivityAdapter.ImageViewClicked {
    RecyclerView recyclerView;
    ArrayList<String> imageViewLists=new ArrayList<>();
    ImageViewActivityAdapter adapter;
    CaptureProcess captureProcess;
    TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_cargo_image_view);

        recyclerView=findViewById(R.id.outcargoimageviewrecyclerview);
        GridLayoutManager manager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(manager);
        imageViewLists=new ArrayList<>();
        queryAllPictures();
        adapter=new ImageViewActivityAdapter(imageViewLists,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public ArrayList<String> queryAllPictures(){
//        captureImageList=new ArrayList<ImageViewList>();
        imageViewLists.clear();
        Uri uri =MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.MediaColumns.DATA};
        Cursor cursor=this.getContentResolver().query(uri,projection,null,null, MediaStore.MediaColumns.DATE_ADDED +
                " desc");
        int columnsDataIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext()){
            String uriI=cursor.getString(columnsDataIndex);

            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Fine/입,출고/Resize");
            String strFile=String.valueOf(file);

            if(uriI.startsWith(strFile)){
//                ImageViewList lists=new ImageViewList(uriI);
                imageViewLists.add(uriI);
            }

        }

        cursor.close();

        return imageViewLists;
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {

    }
}