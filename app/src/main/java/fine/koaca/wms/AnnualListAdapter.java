package fine.koaca.wms;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AnnualListAdapter extends RecyclerView.Adapter<AnnualListAdapter.ListViewHolder>{
    ArrayList<AnnualList> list;
    public interface AnnualOnClickListener{
        void onItemClick(AnnualListAdapter.ListViewHolder holder,View view,int position);
    }
    public interface AnnualLongClickListener{
        void longClick(AnnualListAdapter.ListViewHolder holder,View view,int position);
    }
    AnnualOnClickListener onClickListener;
    AnnualLongClickListener longClickListener;

    public AnnualListAdapter(ArrayList<AnnualList> list,AnnualOnClickListener listener,
                             AnnualLongClickListener longClickListener) {
        this.list=list;
        this.onClickListener=listener;
        this.longClickListener=longClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.annuallist,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListViewHolder holder, int position) {
        holder.txtName.setText(list.get(position).getName());
        holder.txtAnnual.setText(list.get(position).getAnnual());
        holder.half1.setText(list.get(position).getHalf1());
        holder.half2.setText(list.get(position).getHalf2());
        holder.totalDate.setText(String.valueOf(list.get(position).getTotaldate()));

        String annual = null;
        String half1;
        String half2;
        String toDay=new SimpleDateFormat("yyyy-MM-dd").format(new Date()).substring(8,10);


        if(!list.get(position).getAnnual().equals("")){
            annual=list.get(position).getAnnual().substring(8,10);
            if(Integer.parseInt(annual)<=Integer.parseInt(toDay)){
                holder.txtAnnual.setBackgroundColor(Color.GRAY);
            }
           }
        if(!list.get(position).getHalf1().equals("")){
            half1=list.get(position).getHalf1().substring(8,10);
            if(Integer.parseInt(half1)<=Integer.parseInt(toDay)){
                holder.half1.setBackgroundColor(Color.LTGRAY);
            }
        }
        if(!list.get(position).getHalf2().equals("")){
            half2=list.get(position).getHalf2().substring(8,10);
            if(Integer.parseInt(half2)<=Integer.parseInt(toDay)){
                holder.half2.setBackgroundColor(Color.LTGRAY);
            }
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtAnnual;
        TextView txtAnnual2;
        TextView half1;
        TextView half2;
        TextView totalDate;
        public ListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.txtName=itemView.findViewById(R.id.annualtxt_name);
            this.txtAnnual=itemView.findViewById(R.id.annualtxt_annual);
            this.txtAnnual2=itemView.findViewById(R.id.annualtxt_annual2);
            this.half1=itemView.findViewById(R.id.annualtxt_half1);
            this.half2=itemView.findViewById(R.id.annualtxt_half2);
            this.totalDate=itemView.findViewById(R.id.annualtxt_totaldate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(ListViewHolder.this,v,getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.longClick(ListViewHolder.this,v,getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
