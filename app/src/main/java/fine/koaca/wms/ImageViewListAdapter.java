package fine.koaca.wms;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

    public void onListItemLongSelectedInterface(OnListItemLongSelectedInterface longlistener){
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
        holder.itemView.setSelected(isItemSelected(position));


    }
    private void toggleItemSelected(int position){

        if(imageListSelected.get(position, false)){
            imageListSelected.delete(position);
            imageListSelected.put(position,false);
        }else{
            imageListSelected.put(position,true);
        }
        notifyItemChanged(position);
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
        CardView cardView;
        public ListViewHolder(@NonNull View itemView,OnListItemSelectedInterface listener,
                              OnListItemLongSelectedInterface longlistener) {
            super(itemView);
            this.imageView=itemView.findViewById(R.id.captureimageview);
            this.cardView=itemView.findViewById(R.id.capturecardview);

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
                    longlistener.onLongClick(ImageViewListAdapter.ListViewHolder.this,v,position);
                    return true;
                }
            });

        }
    }
}
