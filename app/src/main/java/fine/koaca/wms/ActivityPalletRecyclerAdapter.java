package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityPalletRecyclerAdapter extends RecyclerView.Adapter<ActivityPalletRecyclerAdapter.ListViewHolder> {
    ArrayList<ActivityPalletList> list;
    PltClicked pltClicked;
    PltLongClicked pltLongClicked;
    ActivityPalletRecyclerAdapter(ArrayList<ActivityPalletList> list,PltClicked pltClicked,PltLongClicked pltLongClicked){
        this.list=list;
        this.pltClicked=pltClicked;
        this.pltLongClicked=pltLongClicked;
    }
    public interface PltClicked{
        void clicked(ActivityPalletRecyclerAdapter.ListViewHolder listViewHolder,View v,int position);
    }
    public interface PltLongClicked{
        void longClicked(ActivityPalletRecyclerAdapter.ListViewHolder listViewHolder,View v,int position);
    }
    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_plt,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.date.setText(list.get(position).getDate());
        holder.in.setText(String.valueOf(list.get(position).getInQty()));
        holder.out.setText(String.valueOf(list.get(position).getOutQty()));
        holder.stock.setText(String.valueOf(list.get(position).getStockQty()));
        holder.bl.setText(list.get(position).getBl());
        holder.des.setText(list.get(position).getDes());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView in;
        TextView out;
        TextView stock;
        TextView bl;
        TextView des;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.list_plt_date);
            in=itemView.findViewById(R.id.list_plt_in);
            out=itemView.findViewById(R.id.list_plt_out);
            stock=itemView.findViewById(R.id.list_plt_stock);
            bl=itemView.findViewById(R.id.list_plt_bl);
            des=itemView.findViewById(R.id.list_plt_des);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pltClicked.clicked(ListViewHolder.this,v,getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    pltLongClicked.longClicked(ListViewHolder.this,v,getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
