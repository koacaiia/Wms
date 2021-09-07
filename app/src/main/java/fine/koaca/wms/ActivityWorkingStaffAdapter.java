package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityWorkingStaffAdapter extends RecyclerView.Adapter<ActivityWorkingStaffAdapter.ListViewHolder>{
    ArrayList<ActivityWorkingStaffList> list;
    public ActivityWorkingStaffAdapter(ArrayList<ActivityWorkingStaffList> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_workingstaff_outsourcing,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.date.setText(list.get(position).getDate());
        holder.name.setText(list.get(position).getOutsourcingValue());
        holder.male.setText(list.get(position).getOutsourcingMale());
        holder.feMale.setText(list.get(position).getOutsourcingFemale());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView name;
        TextView male;
        TextView feMale;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.date=itemView.findViewById(R.id.list_workingstaff_date);
            this.name=itemView.findViewById(R.id.list_workingstaff_name);
            this.male=itemView.findViewById(R.id.list_workingstaff_oursourcingMale);
            this.feMale=itemView.findViewById(R.id.list_workingstaff_outsourcingFemale);
        }
    }
}
