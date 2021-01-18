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

public class PutDataListAdapter extends RecyclerView.Adapter<PutDataListAdapter.ListViewHolder> {
    public interface AdapterClickListener{
        void onItemClick(View v,int pos);

    }

    public interface AdapterLongClickListener{
        void onLongItemClick(View v,int pos);
    }
    private AdapterClickListener mListener=null;
    private AdapterLongClickListener mLongListener=null;
    public void setAdapterClickListener(AdapterClickListener listener){
        this.mListener=listener;    }
        public void setAdaptLongClickListener(AdapterLongClickListener listener){
        this.mLongListener=listener;
        }

    ArrayList<Fine2IncargoList> list;
    private final SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);
    Context context;

    public PutDataListAdapter(ArrayList<Fine2IncargoList> list) {
        this.list = list;
    }

    public PutDataListAdapter(ArrayList<Fine2IncargoList> list, Context context) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public PutDataListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.incargolist,parent,false);
                return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PutDataListAdapter.ListViewHolder holder, int position) {
        String str_container20=String.valueOf(list.get(position).getContainer20());
        Log.i("kocaca",str_container20);
        String str_container40=String.valueOf(list.get(position).getContainer40());
        String str_lclcargo=String.valueOf(list.get(position).getLclcargo());
        String cargotype;
        if(str_container20.equals("1")){
            cargotype="20FT";}
        else if(str_container40 .equals("1")){
            cargotype="40FT";
        }else if(str_lclcargo .equals("")){
            cargotype="LcLCargo";
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
//        if (position<list.size()-1){
////            String location_chk3=list.get(position-1).getContainer();
//            String location_chk1=list.get(position).getContainer();
//            String location_chk2=list.get(position+1).getContainer();
//            if (location_chk1.equals(location_chk2)) {
//                holder.itemView.setBackgroundColor(Color.BLUE);
//            } else {
//                holder.itemView.setBackgroundColor(Color.WHITE);
//
//        }}
        holder.itemView.setSelected(isItemSelected(position));
    }

    private boolean isItemSelected(int position) {

        return mSelectedItems.get(position,false);
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
        CardView cardView;
        LinearLayout lL;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.working=itemView.findViewById(R.id.incargo_working);
            this.date=itemView.findViewById(R.id.incargo_date);
            this.consignee=itemView.findViewById(R.id.incargo_consignee);
            this.container=itemView.findViewById(R.id.incargo_container);
            this.cargotype=itemView.findViewById(R.id.incargo_cargotype);
            this.remark=itemView.findViewById(R.id.incargo_remark);
            this.bl=itemView.findViewById(R.id.incargo_bl);
            this.des=itemView.findViewById(R.id.incargo_des);
            this.incargo=itemView.findViewById(R.id.incargo_incargo);
            this.cardView=itemView.findViewById(R.id.background);
            this.lL=itemView.findViewById(R.id.re_back);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    if(mListener !=null){
                        mListener.onItemClick(v,pos);
                        toggleItemSelected(pos);

                }}
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if (mLongListener != null){
                        mLongListener.onLongItemClick(v,pos);
                }

                    return true;
                }
            });
            }

            private void toggleItemSelected(int pos) {
                if(mSelectedItems.get(pos, true)){

                    lL.setBackgroundColor(Color.WHITE);
                    mSelectedItems.delete(pos);
                    mSelectedItems.put(pos,false);

                }else{
                    lL.setBackgroundColor(Color.BLUE);
                    mSelectedItems.put(pos,true);
                }
                notifyItemChanged(pos);

            }

        }


}