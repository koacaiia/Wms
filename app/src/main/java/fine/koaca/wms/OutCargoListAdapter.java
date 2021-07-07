package fine.koaca.wms;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OutCargoListAdapter extends RecyclerView.Adapter<OutCargoListAdapter.ListView>{
    ArrayList<OutCargoList> list;
    Context context;
    OutCargoListAdapterClickListener listener;

    public interface OutCargoListAdapterClickListener{
        void itemClicked(OutCargoListAdapter.ListView listView,View v,int position);
    }
    public OutCargoListAdapter(ArrayList<OutCargoList> list,Context context,OutCargoListAdapterClickListener listener) {
        this.list=list;
        this.context=context;
        this.listener=listener;
    }



    @NonNull
    @NotNull
    @Override
    public ListView onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_outcargo_recyclerview,parent,false);
        return new ListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListView holder, int position) {
        String no,des,pQ,eQ;
        no=list.get(position).getManagementNo().replace(",","\n");
        des=list.get(position).getDescription().replace(",","\n");
        pQ=list.get(position).getPltQty().replace(",","\n");
        eQ=list.get(position).getEaQty().replace(",","\n");

        holder.consigneeName.setText(list.get(position).getConsigneeName());
        holder.date.setText(list.get(position).getDate());
        holder.outwarehouse.setText(list.get(position).getOutwarehouse());
        holder.totalQty.setText(list.get(position).getTotalQty());
        holder.managementNo.setText(no);
        holder.description.setText(des);
        holder.pQty.setText(pQ);
        holder.eQty.setText(eQ);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListView extends RecyclerView.ViewHolder {
        TextView consigneeName;
        TextView date;
        TextView outwarehouse;
        TextView totalQty;
        TextView managementNo;
        TextView description;
        TextView pQty;
        TextView eQty;
        public ListView(@NonNull @NotNull View itemView) {
            super(itemView);
            this.consigneeName=itemView.findViewById(R.id.list_outcargo_consignee);
            this.date=itemView.findViewById(R.id.list_outcargo_date);
            this.outwarehouse=itemView.findViewById(R.id.list_outcargo_outwarehouse);
            this.totalQty=itemView.findViewById(R.id.list_outcargo_totalqty);
            this.managementNo=itemView.findViewById(R.id.list_outcargo_managementNo);
            this.description=itemView.findViewById(R.id.list_outcargo_description);
            this.pQty=itemView.findViewById(R.id.list_outcargo_pltQty);
            this.eQty=itemView.findViewById(R.id.list_outcargo_eaQty);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClicked(ListView.this,v,getAdapterPosition());
                }
            });


        }
    }
}
