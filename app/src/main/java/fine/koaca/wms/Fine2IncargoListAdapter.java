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

public class Fine2IncargoListAdapter extends RecyclerView.Adapter<Fine2IncargoListAdapter.ListViewHolder>  {

    ArrayList<Fine2IncargoList> fine2IncargoLists;
    OnInCargoListItemClickListener listener;
    OnInCargoListItemLongClickListener listenerLong;

    private SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);

    public Fine2IncargoListAdapter(ArrayList<Fine2IncargoList> fine2IncargoLists,OnInCargoListItemClickListener listener, OnInCargoListItemLongClickListener listenerLong) {
        this.fine2IncargoLists = fine2IncargoLists;
        this.listener=listener;
        this.listenerLong=listenerLong;
    }

    public Fine2IncargoListAdapter(ArrayList<Fine2IncargoList> list) {
        this.fine2IncargoLists=list;
    }

    @NonNull
    @Override
    public Fine2IncargoListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list,parent,false);

        return new ListViewHolder(view);
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



    @Override
    public int getItemCount() {

        return (fine2IncargoLists !=null ? fine2IncargoLists.size():0);
    }




    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView working,bl,date,container,container40,container20,lclCargo,des,location,remark,count_seal,incargo,consignee;
        public ListViewHolder(@NonNull View itemView) {
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
                    if(listenerLong !=null){
                        listenerLong.onItemClickLong(ListViewHolder.this,v,pos);

                    }
                    return true;
                }
            });
        }
    }

    public interface OnInCargoListItemClickListener {
        void onItemClick(ListViewHolder listViewHolder, View v, int pos);

    }

    public interface OnInCargoListItemLongClickListener {
        void onItemClickLong(ListViewHolder holder, View view, int position);

    }
}
