package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CameraCaptureOutAdapter extends RecyclerView.Adapter<CameraCaptureOutAdapter.ListViewHolder>{
    ArrayList<OutCargoList> outList;
    CameraCaptureOutAdapterClick listener;
    public interface CameraCaptureOutAdapterClick{
        void outAdapterClick(CameraCaptureOutAdapter.ListViewHolder listViewHolder,View v,int pos);
    }

    public CameraCaptureOutAdapter(ArrayList<OutCargoList> list,CameraCaptureOutAdapterClick listener) {
        this.outList=list;
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
        holder.consigneeName.setText(outList.get(position).getConsigneeName());
        holder.itemNo.setText(outList.get(position).getOutwarehouse());
        holder.manageNo.setText(outList.get(position).getTotalQty());


    }

    @Override
    public int getItemCount() {
        return outList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView consigneeName;
        TextView itemNo;
        TextView manageNo;
        public ListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.consigneeName=itemView.findViewById(R.id.cameralist_consigneeName);
            this.itemNo=itemView.findViewById(R.id.cameralist_itemNo);
            this.manageNo=itemView.findViewById(R.id.cameralist_manageNo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 listener.outAdapterClick(CameraCaptureOutAdapter.ListViewHolder.this,v,getAdapterPosition());
                }
            });
        }
    }
}
