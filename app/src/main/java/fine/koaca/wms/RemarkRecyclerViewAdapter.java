package fine.koaca.wms;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RemarkRecyclerViewAdapter extends RecyclerView.Adapter<RemarkRecyclerViewAdapter.ListViewHolder>{
    ArrayList<ListRemarkRecyclerView> list;

    public RemarkRecyclerViewAdapter(ArrayList<ListRemarkRecyclerView> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recyclerview_getremark,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.nickName.setText(list.get(position).getNickName());
        holder.des.setText(list.get(position).getDes());
        holder.remark.setText(list.get(position).getRemark());
        Log.i("TestValue","ListAdapter nickName Value="+list.get(position).getNickName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView nickName;
        TextView des;
        TextView remark;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            nickName=itemView.findViewById(R.id.list_recyclerview_getremark_nickname);
            des=itemView.findViewById(R.id.list_recyclerview_getremark_des);
            remark=itemView.findViewById(R.id.list_recyclerview_getremark_remark);
        }
    }
}
