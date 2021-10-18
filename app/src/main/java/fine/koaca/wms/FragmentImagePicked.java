package fine.koaca.wms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FragmentImagePicked extends Fragment implements ImageViewActivityAdapter.ImageViewClicked{
    ArrayList<String> imageList;
    SparseBooleanArray selectedSortItems=new SparseBooleanArray(0);
    ArrayList<String> list=new ArrayList<>();
    String activityValue;

    public FragmentImagePicked(){
    }

    public FragmentImagePicked(String activityValue) {
        this.activityValue=activityValue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String deptName;

        PublicMethod publicMethod=new PublicMethod(getActivity());
        deptName=publicMethod.getUserInformation().get("deptName");
        View view=getLayoutInflater().inflate(R.layout.fragment_image_picked,null);
        RecyclerView recyclerView=view.findViewById(R.id.fragmentImageRecycler_Picked);
        GridLayoutManager manager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(manager);
        list=new ArrayList<>();
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
        StorageReference storageReference = null;
        String keyValue;
        switch(activityValue){
            case "Incargo":
                keyValue=Incargo.keyValue;
                storageReference=firebaseStorage.getReference("images/"+deptName+"/"+keyValue.substring(0,10)+
                        "/InCargo/"+keyValue);
                break;
            case "OutCargoActivity":
                keyValue=OutCargoActivity.keyValue;
                storageReference=firebaseStorage.getReference("images/"+deptName+"/"+keyValue.substring(0,10)+
                        "/OutCargo/"+keyValue);
                break;

        }

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item:listResult.getItems()){
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            list.add(uri.toString());
                            if(list.size()==listResult.getItems().size()){
                                ImageViewActivityAdapter adapter=new ImageViewActivityAdapter(list,FragmentImagePicked.this);
                                recyclerView.setAdapter(adapter);
                            }

                        }
                    });
                }

            }
        });
        return view;
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        PublicMethod publicMethod=new PublicMethod(getActivity());
        switch(activityValue){
            case "Incargo":
                imageList=Incargo.imageViewListsSelected;
                String clickedPictureCount="("+imageList.size()+") 장 선택";
                Incargo.incargo_contents_date.setText(clickedPictureCount);
                break;
            case "OutCargoActivity":
                imageList=OutCargoActivity.imageViewLists;

                break;
        }
            if(selectedSortItems.get(position,true)){
                selectedSortItems.put(position,false);
                imageList.add(list.get(position));
            }else{
                selectedSortItems.put(position,true);
                imageList.remove(list.get(position));
            }
            if (imageList.size() > 7) {

              publicMethod.upLoadPictureOverCount_alertDialog();
            }
        switch(activityValue){
            case "Incargo":
                publicMethod.adapterPictureSavedMethod(Incargo.keyValue);
                break;
            case "OutCargoActivity":
                publicMethod.adapterPictureSavedMethod(imageList.get(position));

                break;
        }




    }
}