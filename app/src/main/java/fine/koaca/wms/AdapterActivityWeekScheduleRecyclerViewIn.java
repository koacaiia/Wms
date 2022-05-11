package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterActivityWeekScheduleRecyclerViewIn extends RecyclerView.Adapter<AdapterActivityWeekScheduleRecyclerViewIn.ListViewHolder>{
ArrayList<ListWeekSchedule> list;

    public AdapterActivityWeekScheduleRecyclerViewIn(ArrayList<ListWeekSchedule> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_weekschedule,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        String consigneeName;
        if(list.get(position).getConsignee().length()>4){
            consigneeName=list.get(position).getConsignee().substring(0,3);
        }else{
            consigneeName=list.get(position).getConsignee();
        }
        holder.consigName.setText(consigneeName);
        holder.container20.setText(list.get(position).getContainer20());
        holder.container40.setText(list.get(position).getContainer40());
        holder.cargo.setText(list.get(position).getLclcargo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView consigName;
        TextView container40;
        TextView container20;
        TextView cargo;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            consigName=itemView.findViewById(R.id.list_weekschedule_consigneeName);
            container40=itemView.findViewById(R.id.list_weekschedule_40ft);
            container20=itemView.findViewById(R.id.list_weekschedule_20ft);
            cargo=itemView.findViewById(R.id.list_weekschedule_cargo);
        }
    }
}
