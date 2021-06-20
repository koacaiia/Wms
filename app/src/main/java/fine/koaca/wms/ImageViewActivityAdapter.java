package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageViewActivityAdapter extends RecyclerView.Adapter<ImageViewActivityAdapter.ListView>{
    ArrayList<String> list;
    public ImageViewActivityAdapter(ArrayList<String> list) {
        this.list=list;
    }

    @NonNull
    @NotNull
    @Override
    public ListView onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.imageactivityview_list,parent,false);
        return new ListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListView holder, int position) {
        String str=list.get(position);

        Glide.with(holder.itemView)
                .load(str)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListView extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ListView(@NonNull @NotNull View itemView) {
            super(itemView);
            this.imageView=itemView.findViewById(R.id.captureimageview_activity);
        }
    }
}
