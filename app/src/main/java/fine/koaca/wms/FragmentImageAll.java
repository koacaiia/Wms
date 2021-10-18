package fine.koaca.wms;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentImageAll extends Fragment implements ImageViewActivityAdapter.ImageViewClicked {
    ArrayList<String> list;
    SparseBooleanArray selectedSortItems=new SparseBooleanArray(0);
    String activityValue;
    TextView txt;
    ArrayList<String> imageList;
    public FragmentImageAll() {
        // Required empty public constructor
    }

    public FragmentImageAll(String activityValue) {
        this.activityValue=activityValue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=getLayoutInflater().inflate(R.layout.fragment_image_all,null);
        RecyclerView recyclerView=view.findViewById(R.id.fragmentImageRecycler_All);
        GridLayoutManager manager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(manager);
        list=new PublicMethod(this.getActivity()).getPictureListsAll();
        ImageViewActivityAdapter adapter=new ImageViewActivityAdapter(list,this);
        recyclerView.setAdapter(adapter);
        txt=view.findViewById(R.id.fragmentImageText_all);
        switch(activityValue){
            case "Incargo":
                imageList=Incargo.imageViewListsSelected;
                break;
            case "OutCargoActivity":
                imageList=OutCargoActivity.imageViewLists;
                break;
            default :
                imageList=null;
        }
        imageList.clear();

        return view;
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {


        Animation anim=new TranslateAnimation(-v.getWidth(),v.getWidth(),0,0);


        if(selectedSortItems.get(position,true)){
            selectedSortItems.put(position,false);
            imageList.add(list.get(position));
        }else{
            selectedSortItems.put(position,true);
            imageList.remove(list.get(position));
        }
        if(imageList.size()>7){
            PublicMethod publicMethod=new PublicMethod(getActivity());
            publicMethod.upLoadPictureOverCount_alertDialog();
        }
        txt.setText("선택사진 수량:"+imageList.size()+" 장");
        txt.setTextColor(Color.RED);
        anim.setDuration(3000);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txt.startAnimation(anim);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.clearAnimation();
            }
        });

    }
}