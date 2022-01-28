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
    ActivityEquipFacilityAdapterClicked itemClicked;
    ActivityEquipFacilityAdapterLongClicked longClicked;

    public interface ActivityEquipFacilityAdapterClicked{
        void itemClick(ActivityEquipFacilityAdapter.ListViewHolder listViewHolder,View v,int position);
    }
    public interface ActivityEquipFacilityAdapterLongClicked{
        void longClick(ActivityEquipFacilityAdapter.ListViewHolder listViewHolder,View v,int position);
    }

    public ActivityEquipFacilityAdapter(ArrayList<ActivityEquipFacilityList> list,
                                        ActivityEquipFacilityAdapterClicked itemClicked,
                                        ActivityEquipFacilityAdapterLongClicked longClicked) {
        this.list = list;
        this.itemClicked=itemClicked;
        this.longClicked=longClicked;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_equipnfacility,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.content.setText(list.get(position).getContent());
        holder.askDate.setText(list.get(position).getAskDate());
        holder.estAmountDate.setText(list.get(position).getEstAmountDate());
        holder.estAmount.setText("견적액:"+list.get(position).getEstAmount()+"원");
        holder.confirmDate.setText(list.get(position).getConfirmDate());
        holder.repairDate.setText(list.get(position).getRepairDate());
        holder.conAmountDate.setText(list.get(position).getConAmountDate());
        holder.conAmount.setText("결재액:"+list.get(position).getConAmount()+"원");
        holder.remark.setText(list.get(position).getRemark());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
       TextView name;
       TextView content;
       TextView askDate;
       TextView ask;
       TextView estAmountDate;
       TextView estAmount;
       TextView confirmDate;
       TextView confirm;
       TextView repairDate;
       TextView repair;
       TextView conAmountDate;
       TextView conAmount;
       TextView remark;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.list_equipnfacility_txtName);
            this.content=itemView.findViewById(R.id.list_equipnfacility_contents);
            this.askDate=itemView.findViewById(R.id.list_equipnfacility_txtAskDate);
            this.ask=itemView.findViewById(R.id.list_equipnfacility_txtAsk);
            this.estAmountDate=itemView.findViewById(R.id.list_equipnfacility_txtEstDate);
            this.estAmount=itemView.findViewById(R.id.list_equipnfacility_txtEstAmount);
            this.confirmDate=itemView.findViewById(R.id.list_equipnfacility_txtConfirmDate);
            this.confirm=itemView.findViewById(R.id.list_equipnfacility_txtConfirm);
            this.repairDate=itemView.findViewById(R.id.list_equipnfacility_txtRepairedDate);
            this.repair=itemView.findViewById(R.id.list_equipnfacility_txtRepaied);
            this.conAmountDate=itemView.findViewById(R.id.list_equipnfacility_txtConfirmAmountDate);
            this.conAmount=itemView.findViewById(R.id.list_equipnfacility_txtConfirmAmount);
            this.remark=itemView.findViewById(R.id.list_equipnfacility_txtRemark);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked.itemClick(ListViewHolder.this,v,getAdapterPosition());
                }
            }
            );
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClicked.longClick(ListViewHolder.this,view,getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
