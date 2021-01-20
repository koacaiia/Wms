package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExtractIncargoDataAdapter extends RecyclerView.Adapter<ExtractIncargoDataAdapter.ListViewHolder>{
    ArrayList<ExtractIncargoDataList> list=new ArrayList<ExtractIncargoDataList>();

    public ExtractIncargoDataAdapter(ArrayList<ExtractIncargoDataList> arrList) {
        list=arrList;
    }

    @NonNull
    @Override
    public ExtractIncargoDataAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.arr_re_list,parent,false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtractIncargoDataAdapter.ListViewHolder holder, int position) {
holder.cargo.setText(list.get(position).getLcLCargo());
holder.consignee.setText(list.get(position).getConsignee());
holder.container40.setText(list.get(position).getContainer40());
holder.container20.setText(list.get(position).getContainer20());
holder.qty.setText(list.get(position).getQty());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView consignee;
        TextView container40;
        TextView container20;
        TextView cargo;
        TextView qty;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.consignee=itemView.findViewById(R.id.exRecyclerConsignee);
            this.container40=itemView.findViewById(R.id.exRecyclerContainer40);
            this.container20=itemView.findViewById(R.id.exRecyclerContainer20);
            this.cargo=itemView.findViewById(R.id.exRecyclerCargo);
            this.qty=itemView.findViewById(R.id.exRecyclerQty);
        }
    }
}
