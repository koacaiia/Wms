package fine.koaca.wms;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.RED;

public class Fine2IncargoListAdapter extends RecyclerView.Adapter<Fine2IncargoListAdapter.ListViewHolder> implements    OnListItemClickListener,OnItemLongClickListener {

    ArrayList<Fine2IncargoList> fine2IncargoLists;
    OnListItemClickListener listener;
    OnItemLongClickListener longClickListener;

    private SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);

    public Fine2IncargoListAdapter(ArrayList<Fine2IncargoList> fine2IncargoLists, MainActivity mainActivity) {
        this.fine2IncargoLists = fine2IncargoLists;
    }

    public Fine2IncargoListAdapter(ArrayList<Fine2IncargoList> list) {
        this.fine2IncargoLists=list;
    }

    @NonNull
    @Override
    public Fine2IncargoListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list,parent,false);

        return new ListViewHolder(view,this,this);
    }

    @Override
    public void onBindViewHolder(@NonNull Fine2IncargoListAdapter.ListViewHolder holder, int position) {
          String str_incargo=String.valueOf(fine2IncargoLists.get(position).getIncargo());
          String location_chk=fine2IncargoLists.get(position).getLocation();

            holder.date.setText(fine2IncargoLists.get(position).getDate());
            holder.bl.setText(fine2IncargoLists.get(position).getBl());
            holder.incargo.setText(str_incargo);
            holder.remark.setText(fine2IncargoLists.get(position).getRemark());
            holder.des.setText(fine2IncargoLists.get(position).getDescription());
            holder.count_seal.setText(fine2IncargoLists.get(position).getCount());
            holder.location.setText(fine2IncargoLists.get(position).getLocation());
            holder.container.setText(fine2IncargoLists.get(position).getContainer());

        if(location_chk.equals("")){
            holder.itemView.setBackgroundColor(Color.BLUE);
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE);
        }


    }

    private void addList(fine.koaca.wms.Fine2IncargoListAdapter.ListViewHolder holder, int position) {
        holder.working.setText(fine2IncargoLists.get(position).getWorking());
        holder.consignee.setText(fine2IncargoLists.get(position).getConsignee());
        holder.container40.setText(fine2IncargoLists.get(position).getContainer40());
        holder.container20.setText(fine2IncargoLists.get(position).getContainer20());
        holder.lclCargo.setText(fine2IncargoLists.get(position).getLclcargo());
    }

    public void setOnItemClicklistener(OnListItemClickListener listener){

        this.listener=listener;
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener){
        this.longClickListener=longClickListener;
    }
    @Override
    public int getItemCount() {
//        return fine2IncargoLists.size();
        return (fine2IncargoLists !=null ? fine2IncargoLists.size():0);
    }

    @Override
    public void onLongItemClick(Fine2IncargoListAdapter.ListViewHolder listViewHolder, View v, int pos) {
        if(longClickListener !=null){
            longClickListener.onLongItemClick(listViewHolder,v,pos);
        }

    }

    @Override
    public void onItemClick(fine.koaca.wms.Fine2IncargoListAdapter.ListViewHolder holder, View view, int position) {
        if(listener !=null){
            listener.onItemClick(holder,view,position);
        }


    }


    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView working,bl,date,container,container40,container20,lclCargo,des,location,remark,count_seal,incargo,consignee;
        public ListViewHolder(@NonNull View itemView,final OnListItemClickListener listener,
                              final fine.koaca.wms.OnItemLongClickListener longClickListener) {
            super(itemView);
            this.bl=itemView.findViewById(R.id.textView2);
            this.des=itemView.findViewById(R.id.textView);
            this.location=itemView.findViewById(R.id.text_location);
            this.date=itemView.findViewById(R.id.text_Date);
            this.count_seal=itemView.findViewById(R.id.textView_Rotate);
            this.remark=itemView.findViewById(R.id.textView_list_mark);
            this.incargo=itemView.findViewById(R.id.textView_incargo);
            this.working=itemView.findViewById(R.id.incargo_working);
            this.container=itemView.findViewById(R.id.textView_container);
//            this.container40=itemView.findViewById(R.id.incargo_container40);
//            this.container20=itemView.findViewById(R.id.incargo_container20);
            this.lclCargo=itemView.findViewById(R.id.incargo_cargotype);
            this.consignee=itemView.findViewById(R.id.incargo_consignee);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    if(listener !=null){
                        listener.onItemClick(ListViewHolder.this,v,pos);
                    }
                    if(mSelectedItems.get(pos,true)){
                        mSelectedItems.put(pos,false);
                        bl.setTextColor(RED);
                        des.setTextColor(RED);
                        count_seal.setTextColor(RED);
                        container.setTextColor(RED);
                    }else{
                        mSelectedItems.put(pos,true);
                        bl.setTextColor(BLACK);
                        des.setTextColor(BLACK);
                        count_seal.setTextColor(BLACK);
                        container.setTextColor(BLACK);
                    }

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=getAdapterPosition();
                    if(longClickListener !=null){
                        longClickListener.onLongItemClick(ListViewHolder.this,v,pos);

                    }
                    return true;
                }
            });



        }
    }

}
