package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageViewListAdapter extends RecyclerView.Adapter<ImageViewListAdapter.ListViewHolder>{
    ArrayList<ImageViewList> list=new ArrayList<ImageViewList>();

    @NonNull
    @Override
    public ImageViewListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_list,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewListAdapter.ListViewHolder holder, int position) {
        holder.imageView.setImageBitmap(list.get(position).getImageview());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView=itemView.findViewById(R.id.captureimageview);

        }
    }
}
