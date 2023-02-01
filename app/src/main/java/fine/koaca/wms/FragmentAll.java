package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAll#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAll extends Fragment implements ImageViewActivityAdapter.ImageViewClicked {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SparseBooleanArray selectedList = new SparseBooleanArray(0);
    ArrayList<String> selectImage;
    ArrayList<String> list;
    TextView textView;
//    Button btnSend;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentAll() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAll.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAll newInstance(String param1, String param2) {
        FragmentAll fragment = new FragmentAll();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= getLayoutInflater().inflate(R.layout.fragment_all,container,false);
        textView = view.findViewById(R.id.fragmentAll_txtView);
        RecyclerView recycler=view.findViewById(R.id.fragmentAll_recyclerView);
        GridLayoutManager manager=new GridLayoutManager(getActivity(),3);
        recycler.setLayoutManager(manager);
        PublicMethod pictures= new PublicMethod(getActivity());
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        list = pictures.getPictureLists("All",date);
        ImageViewActivityAdapter adapter= new ImageViewActivityAdapter(list,this);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        btnSend = view.findViewById(R.id.btnFragmentAll);
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PublicMethod publicMethod= new PublicMethod(getActivity(),selectImage);
////                publicMethod.upLoadPictures("Ysk",)
//            }
//
//        });
        return view;
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        String activityName=getActivity().getLocalClassName();
        switch(activityName){
            case "Incargo":
                selectImage= ((Incargo)getActivity()).imageViewListsSelected;
                break;
            case "ActivityEquipNFacilityPutData":
                selectImage= ((ActivityEquipNFacilityPutData)getActivity()).selectedImageViewLists;
                break;
        }
        if(selectedList.get(position,true)){
            selectedList.put(position,false);
            selectImage.add(list.get(position));
        }else{
            selectedList.put(position,true);
            selectImage.remove(list.get(position) );
        }
        textView.setText(selectImage.size()+"장의 사진이 선택 되었습니다.");
        textView.setTextColor(Color.RED);
        switch(activityName){
            case "Incargo":selectImage= ((Incargo)getActivity()).imageViewListsSelected;
                ((Incargo)getActivity()).imageViewListsSelected=selectImage;
                break;
            case "ActivityEquipNFacilityPutData":

                ((ActivityEquipNFacilityPutData)getActivity()).selectedImageViewLists=selectImage;
                break;
        }

         }
}