package fine.koaca.wms;

import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class WorkingMessageAdapter extends RecyclerView.Adapter<WorkingMessageAdapter.ListViewHolder>
implements OnListImageClickListener{
    ArrayList<WorkingMessageList> messageLists;
    OnListImageClickListener listener;
    String myNickname;
    Context context;
    SharedPreferences sharedPreferences;

    public WorkingMessageAdapter(ArrayList<WorkingMessageList> messageLists, Context context, String myNickname) {
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
        String time=messageLists.get(position).getTime();
        holder.time.setText(time.substring(time.length()-9));
        Glide.with(holder.itemView)
                .load(messageLists.get(position).getUri0())
                .into(holder.image0);
        Glide.with(holder.itemView)
                .load(messageLists.get(position).getUri1())
                .into(holder.image1);
        Glide.with(holder.itemView)
                .load(messageLists.get(position).getUri2())
                .into(holder.image2);
        Glide.with(holder.itemView)
                .load(messageLists.get(position).getUri3())
                .into(holder.image3);
        Glide.with(holder.itemView)
                .load(messageLists.get(position).getUri4())
                .into(holder.image4);
        sharedPreferences=context.getSharedPreferences("SHARE_DEPOT",MODE_PRIVATE);
        String nickName=sharedPreferences.getString("nickName",null);

        if(messageLists.get(position).getNickName().equals(nickName)){
            holder.linearLayout.setGravity(Gravity.END);

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
    public void onItemClickImage(WorkingMessageAdapter.ListViewHolder holder, View view, int position) {
       if(listener!=null){
           listener.onItemClickImage(holder,view,position);
       }

    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        TextView nickName;
        TextView time;
        ImageView image0;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        ImageView image4;
        CardView cardView;
        LinearLayout linearLayout;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.msg=itemView.findViewById(R.id.txt_work_msg);
            this.nickName=itemView.findViewById(R.id.txt_work_nickName);
            this.time=itemView.findViewById(R.id.txt_work_time);
            this.image0=itemView.findViewById(R.id.work_image0);
            this.image1=itemView.findViewById(R.id.work_image1);
            this.image2=itemView.findViewById(R.id.work_image2);
            this.image3=itemView.findViewById(R.id.work_image3);
            this.image4=itemView.findViewById(R.id.work_image4);
            this.cardView=itemView.findViewById(R.id.workinglistcardview);
            this.linearLayout=itemView.findViewById(R.id.workinglayout);


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
