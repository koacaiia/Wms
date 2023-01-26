package fine.koaca.wms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOri#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOri extends Fragment implements ImageViewActivityAdapter.ImageViewClicked {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<String> list;
    ArrayList<String> selectImage;
    SparseBooleanArray selectedList= new SparseBooleanArray(0);

    public FragmentOri() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOri.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOri newInstance(String param1, String param2) {
        FragmentOri fragment = new FragmentOri();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =getLayoutInflater().inflate(R.layout.fragment_ori,container,false);
        RecyclerView recyclerView=view.findViewById(R.id.fragmentOri_recyclerView);
        GridLayoutManager manager=new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(manager);
        PublicMethod picture=new PublicMethod(getActivity());
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        list= picture.getPictureLists("Ori",date);
        ImageViewActivityAdapter iAdapter= new ImageViewActivityAdapter(list,this);
        recyclerView.setAdapter(iAdapter);
        iAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        if(getActivity()!=null){
            selectImage= ((Incargo)getActivity()).imageViewListsSelected;}
        if(selectedList.get(position,true)){
            selectedList.put(position,false);
            selectImage.add(list.get(position));
        }else{
            selectedList.put(position,true);
            selectImage.remove(list.get(position));
        }
        Toast.makeText(getActivity(),"Selected Images Count::"+selectImage.size(),Toast.LENGTH_SHORT).show();
        if(getActivity()!=null){
            ((Incargo)getActivity()).imageViewListsSelected=selectImage;
        }
    }
}