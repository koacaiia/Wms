package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityEquipFacilityAdapter extends RecyclerView.Adapter<ActivityEquipFacilityAdapter.ListViewHolder>{
    ArrayList<ActivityEquipFacilityList> list;

    public ActivityEquipFacilityAdapter(ArrayList<ActivityEquipFacilityList> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_equipnfacility,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.txtDate.setText(list.get(position).getDate());
        holder.txtName.setText(list.get(position).geteFName());
        holder.txtContents.setText(list.get(position).getManageContent());
        holder.txtProcess.setText(list.get(position).getProcess());
        holder.txtAmount.setText(String.valueOf(list.get(position).getConfirmAmount()));
        holder.txtRemark.setText(list.get(position).getRemark());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtName;
        TextView txtContents;
        TextView txtProcess;
        TextView txtAmount;
        TextView txtRemark;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtDate=itemView.findViewById(R.id.list_equipnfacility_txtDate);
            this.txtName=itemView.findViewById(R.id.list_equipnfacility_txtName);
            this.txtContents=itemView.findViewById(R.id.list_equipnfacility_contents);
            this.txtProcess=itemView.findViewById(R.id.list_equipnfacility_process);
            this.txtAmount=itemView.findViewById(R.id.list_equipnfacility_amount);
            this.txtRemark=itemView.findViewById(R.id.list_equipnfacility_remark);
        }
    }
}
