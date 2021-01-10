package fine.koaca.wms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class WorkMessageAdapter extends RecyclerView.Adapter<WorkMessageAdapter.ListViewHolder>
implements OnListImageClickListener{
    ArrayList<WorkingMessageList> messageLists;
    OnListImageClickListener listener;
    String myNickname;
    Context context;
    SharedPreferences sharedPreferences;

    public WorkMessageAdapter(ArrayList<WorkingMessageList> messageLists, Context context, String myNickname) {
        this.messageLists = messageLists;
        this.myNickname=myNickname;
        this.context=context;
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
        holder.time.setText(messageLists.get(position).getTime());
        Glide.with(holder.itemView)
                .load(messageLists.get(position).getUri())
                .into(holder.imageview);
        sharedPreferences=context.getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nickName=sharedPreferences.getString("nickName",null);
        Log.i("koacaiia",nickName+"__onBindViewHolder Log");


        if(messageLists.get(position).getNickName().equals(nickName)){
            holder.msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.nickName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.time.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.imageview.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        }else{
            holder.msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.nickName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.time.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.imageview.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }

    @Override
    public int getItemCount() {
        return messageLists.size();
    }

    public void setOnListImageClickListener(OnListImageClickListener listener){
        this.listener=listener;
    }



    @Override
    public void onItemClickImage(WorkMessageAdapter.ListViewHolder holder, View view, int position) {
       if(listener!=null){
           listener.onItemClickImage(holder,view,position);
       }

    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        TextView nickName;
        TextView time;
        ImageView imageview;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.msg=itemView.findViewById(R.id.txt_work_msg);
            this.nickName=itemView.findViewById(R.id.txt_work_nickName);
            this.time=itemView.findViewById(R.id.txt_work_time);
            this.imageview=itemView.findViewById(R.id.image_work_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos=getAdapterPosition();
                    if(listener !=null){
                        listener.onItemClickImage(ListViewHolder.this,v,pos);
                    }

                }
            });


        }
    }

    public void addWorkingMessage(WorkingMessageList messageData){
        messageLists.add(messageData);
        Collections.reverse(messageLists);
        notifyItemInserted(messageLists.size()-1);
    }
}
