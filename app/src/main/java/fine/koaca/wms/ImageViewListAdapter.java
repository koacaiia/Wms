package fine.koaca.wms;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageViewListAdapter extends RecyclerView.Adapter<ImageViewListAdapter.ListViewHolder> implements OnListItemSelectedInterface,OnListItemLongSelectedInterface{
    ArrayList<ImageViewList> list;
    Context context;
    OnListItemSelectedInterface listener;
    OnListItemLongSelectedInterface longlistener;
    SparseBooleanArray imageListSelected=new SparseBooleanArray(0);
    ArrayList<String> arrUriString=new ArrayList<String>();
    CameraCapture cameraCapture;


    public ImageViewListAdapter(ArrayList<ImageViewList> captureImageList) {
        this.list=captureImageList;
    }
    public void onListItemSelected(OnListItemSelectedInterface listener){
        this.listener=listener;
    }

    public void onListItemLongSelected(OnListItemLongSelectedInterface longlistener){
        this.longlistener=longlistener;
    }

    @NonNull
    @Override
    public ImageViewListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_list,parent,false);
        return new ListViewHolder(view,this,this);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewListAdapter.ListViewHolder holder, int position) {
        String str=list.get(position).getUriName();

        Glide.with(holder.itemView)
                .load(str)
                .into(holder.imageView);
        Log.i("koacaiia",str+"____onbindViewholder getUriName");
        holder.itemView.setSelected(isItemSelected(position));


    }
    private void toggleItemSelected(int position){
        String uriString=list.get(position).getUriName();

        if(imageListSelected.get(position,false)==true){
            imageListSelected.delete(position);
//            arrUriString.remove(uriString);
        }else{
            imageListSelected.put(position,true);
            arrUriString.add(uriString);
        }
        notifyItemChanged(position);
        Log.i("koacaiia",arrUriString+"____arrList");
    }

    private boolean isItemSelected(int position) {

        return imageListSelected.get(position,false);
    }

    public void clearSelectedItem(){
        int position;
        for(int i=0;i<imageListSelected.size();i++){
            position=imageListSelected.keyAt(i);
            imageListSelected.put(position,false);
            notifyItemChanged(position);
        }
        imageListSelected.clear();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onLongClick(ListViewHolder holder, View view, int position) {
        if(longlistener !=null){
            longlistener.onLongClick(holder,view,position);
        }

    }

    @Override
    public void onItemClick(ListViewHolder holder, View view, int position) {
        if(listener !=null){
            listener.onItemClick(holder,view,position);
        }
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ListViewHolder(@NonNull View itemView,OnListItemSelectedInterface listener,
                              OnListItemLongSelectedInterface longlistener) {
            super(itemView);
            this.imageView=itemView.findViewById(R.id.captureimageview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    listener.onItemClick(ListViewHolder.this,v,position);
                    toggleItemSelected(position);

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position=getAdapterPosition();
//                    longlistener.onItemLongSelected(v,position);
                    Log.i("koacaiia","longclickPostion"+position);
                    return true;
                }
            });

        }
    }
}
