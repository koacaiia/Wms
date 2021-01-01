package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkMessageAdapter extends RecyclerView.Adapter<WorkMessageAdapter.ListViewHolder>{
    ArrayList<WorkingMessageList> messageLists;
    String myNickname;

    public WorkMessageAdapter(ArrayList<WorkingMessageList> messageLists,String myNickname) {
        this.messageLists = messageLists;
        this.myNickname=myNickname;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.workinglist,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.msg.setText(messageLists.get(position).getMsg());
        holder.nickName.setText(messageLists.get(position).getNickName());
        if(messageLists.get(position).getMsg().equals(this.myNickname)){
            holder.msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }else{
            holder.msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        if(messageLists.get(position).getNickName().equals(this.myNickname)){
            holder.nickName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }else{
            holder.nickName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

    }

    @Override
    public int getItemCount() {
        return messageLists.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        TextView nickName;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.msg=itemView.findViewById(R.id.txt_work_msg);
            this.nickName=itemView.findViewById(R.id.txt_work_nickName);

        }
    }
}
