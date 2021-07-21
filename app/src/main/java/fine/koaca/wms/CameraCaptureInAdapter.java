package fine.koaca.wms;

import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.RED;

public class CameraCaptureInAdapter extends RecyclerView.Adapter<CameraCaptureInAdapter.ListViewHolder>{
    ArrayList<Fine2IncargoList> list;
    CameraCaptureInAdapterClick listener;
    private final SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);

    public interface CameraCaptureInAdapterClick{
        void inAdapterClick(CameraCaptureInAdapter.ListViewHolder listViewHolder,View v,int position);
    }


    public CameraCaptureInAdapter(ArrayList<Fine2IncargoList> list,CameraCaptureInAdapterClick listener) {
        this.list=list;
        this.listener=listener;
    }

    @NotNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_upload_picture_list,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListViewHolder holder, int position) {
        holder.consigneeName.setText(list.get(position).getConsignee());
        holder.itemNo.setText(list.get(position).getBl());
        holder.manageNo.setText(list.get(position).getContainer());
        if(mSelectedItems.get(position,false)){

           holder.layout.setBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView consigneeName;
        TextView itemNo;
        TextView manageNo;
        LinearLayout layout;
        public ListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.consigneeName=itemView.findViewById(R.id.cameralist_consigneeName);
            this.itemNo=itemView.findViewById(R.id.cameralist_itemNo);
            this.manageNo=itemView.findViewById(R.id.cameralist_manageNo);
            this.layout=itemView.findViewById(R.id.cameralist_back);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    listener.inAdapterClick(CameraCaptureInAdapter.ListViewHolder.this,v,pos);

                    if(mSelectedItems.get(pos,false)){
                        mSelectedItems.put(pos,false);

                    }else{
                        mSelectedItems.put(pos,true);

                   }
                    notifyItemChanged(pos);
                    }
            });
        }
    }
}
