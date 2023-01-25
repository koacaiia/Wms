package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSaved#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSaved extends Fragment implements ImageViewActivityAdapter.ImageViewClicked{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<String> imageSelects=new ArrayList<>();

    public FragmentSaved() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSaved.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSaved newInstance(String param1, String param2) {
        FragmentSaved fragment = new FragmentSaved();
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
        PublicMethod publicMethod = new PublicMethod(getActivity());
        String deptName=publicMethod.getUserInformation().get("deptName");
        String nickName = publicMethod.getUserInformation().get("nickName");
        String listImageViewCount = publicMethod.getUserInformation().get("imageViewListCount");
        String keyValue= this.getArguments().getString("keyValue");
        View view= getLayoutInflater().inflate(R.layout.fragment_saved,null);
        RecyclerView recyclerView= view.findViewById(R.id.fragmentSaved_recyclerView);
        GridLayoutManager manager= new GridLayoutManager(getActivity(),Integer.parseInt(listImageViewCount));
        recyclerView.setLayoutManager(manager);
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://fine-bondedwarehouse.appspot.com");
        StorageReference storageReference =
                storage.getReference("images/" + deptName + "/" + keyValue.substring(0, 10) + "/InCargo/" + keyValue);
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            imageSelects.add(uri.toString());
                            ImageViewActivityAdapter iAdapter = new ImageViewActivityAdapter(imageSelects, FragmentSaved.this);
                            if (imageSelects.size() == listResult.getItems().size()) {
                                recyclerView.setAdapter(iAdapter);
                                iAdapter.notifyDataSetChanged();
                            }
//                            iAdapter.clickListener = new ImageViewActivityAdapter.ImageViewClicked() {
//                                @Override
//                                public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
//                                    PublicMethod publicMethod = new PublicMethod(getActivity());
//                                    publicMethod.adapterPictureSavedMethod(imageSelects.get(position));
//                                }
//                            };

                        }

                    });

                }

            }
        });
        return view;
    }

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        PublicMethod publicMethod = new PublicMethod(getActivity());
                                    publicMethod.adapterPictureSavedMethod(imageSelects.get(position));
    }
}