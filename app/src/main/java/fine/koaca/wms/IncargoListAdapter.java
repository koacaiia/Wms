package fine.koaca.wms;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IncargoListAdapter extends RecyclerView.Adapter<IncargoListAdapter.ListViewHolder>
{

    AdapterClickListener mListener=null;
    AdapterLongClickListener mLongListener=null;
    ArrayList<Fine2IncargoList> list;
    SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);
    Context context;

    public interface AdapterClickListener {
        void onItemClick(IncargoListAdapter.ListViewHolder listViewHolder,View v, int pos);

    }
    public interface AdapterLongClickListener {
        void onLongItemClick(IncargoListAdapter.ListViewHolder listViewHolder,View v, int pos);
    }
    public  IncargoListAdapter(ArrayList<Fine2IncargoList> list, Context context) {
        this.context=context;
        this.list=list;
    }

    public IncargoListAdapter(ArrayList<Fine2IncargoList> listItems,  AdapterClickListener mListener, AdapterLongClickListener mLongListener) {
        this.list=listItems;
        this.mListener=mListener;
        this.mLongListener=mLongListener;
    }

    @NonNull
    @Override
    public IncargoListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.incargolist,parent,false);
                return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull fine.koaca.wms.IncargoListAdapter.ListViewHolder holder, int position) {
        String str_container20=String.valueOf(list.get(position).getContainer20());
        String str_container40=String.valueOf(list.get(position).getContainer40());
        String str_lclcargo=String.valueOf(list.get(position).getLclcargo());
        String cargotype;
        if(str_container20.equals("1")){
            cargotype="20FT";}
        else if(str_container40 .equals("1")){
            cargotype="40FT";
        }else if(str_lclcargo .equals("1")){
            cargotype="Cargo";
        }else{cargotype="미정";}

        holder.working.setText(list.get(position).getWorking());
        holder.date.setText(list.get(position).getDate());
        holder.consignee.setText(list.get(position).getConsignee());
        holder.container.setText(list.get(position).getContainer());
        holder.cargotype.setText(cargotype);
        holder.remark.setText(list.get(position).getRemark());
        holder.bl.setText(list.get(position).getBl());
        holder.des.setText(list.get(position).getDescription());
        holder.incargo.setText(list.get(position).getIncargo()+"(PLT)");

//        if(mSelectedItems.get(position,false)){
//            holder.cardView.setBackgroundColor(Color.LTGRAY);
//        }else{
//            holder.cardView.setBackgroundColor(Color.WHITE);
//        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView working;
        TextView date;
        TextView container;
        TextView consignee;
        TextView cargotype;
        TextView remark;
        TextView bl;
        TextView des;
        TextView incargo;
        LinearLayout cardView;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.working=itemView.findViewById(R.id.incargo_working);
            this.cardView=itemView.findViewById(R.id.re_back);
            this.date=itemView.findViewById(R.id.incargo_date);
            this.consignee=itemView.findViewById(R.id.incargo_consignee);
            this.container=itemView.findViewById(R.id.incargo_container);
            this.cargotype=itemView.findViewById(R.id.incargo_cargotype);
            this.remark=itemView.findViewById(R.id.incargo_remark);
            this.bl=itemView.findViewById(R.id.incargo_bl);
            this.des=itemView.findViewById(R.id.incargo_des);
            this.incargo=itemView.findViewById(R.id.incargo_incargo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    if(mListener !=null){
                        mListener.onItemClick(ListViewHolder.this,v,pos);
//                            if(mSelectedItems.get(pos, false)){
//                                mSelectedItems.put(pos,false);
//                                cardView.setBackgroundColor(Color.WHITE);
//                            }else{
//                                mSelectedItems.put(pos,true);
//                                cardView.setBackgroundColor(Color.LTGRAY);
//                            }
//                            notifyItemChanged(pos);
                        }
                      }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if (mLongListener != null){
                        mLongListener.onLongItemClick(ListViewHolder.this,v,pos);
                }
                    return true;
                }
            });
            }
        }
        public void clearSelectedItem(){
        int position;
        for(int i=0;i<mSelectedItems.size();i++){
            position=mSelectedItems.keyAt(i);
            mSelectedItems.put(position,false);
            notifyItemChanged(position);

        }
        mSelectedItems.clear();
        }
}