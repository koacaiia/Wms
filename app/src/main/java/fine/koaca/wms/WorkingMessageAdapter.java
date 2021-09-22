package fine.koaca.wms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class WorkingMessageAdapter extends RecyclerView.Adapter<WorkingMessageAdapter.ListViewHolder>
implements OnListImageClickListener,ImageViewActivityAdapter.ImageViewClicked{
    ArrayList<WorkingMessageList> messageLists;
    OnListImageClickListener listener;
    String myNickname;
    Context context;
    SharedPreferences sharedPreferences;

    private SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);


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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, @SuppressLint("RecyclerView") int position) {

        sharedPreferences=context.getSharedPreferences("Dept_Name",MODE_PRIVATE);
        String nickName=sharedPreferences.getString("nickName",null);

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
        Glide.with(holder.itemView)
                .load(messageLists.get(position).getUri5())
                .into(holder.image5);
        Glide.with(holder.itemView)
                .load(messageLists.get(position).getUri6())
                .into(holder.image6);


        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) holder.linearLayout.getLayoutParams();
        if(messageLists.get(position).getNickName().equals(nickName)){

            mSelectedItems.put(position,true);}
        else {
            mSelectedItems.put(position,false);
        }
       if(mSelectedItems.get(position,true)){
           params.gravity=Gravity.END;
       }else{
           params.gravity=Gravity.START;
       }



        holder.linearLayout.setLayoutParams(params);

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

    @Override
    public void imageViewClicked(ImageViewActivityAdapter.ListView listView, View v, int position) {
        Toast.makeText(context,"ImageView Clicked",Toast.LENGTH_SHORT).show();
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
        ImageView image5;
        ImageView image6;
        CardView cardView;
        LinearLayout linearLayout;
        RecyclerView recyclerView;


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
            this.image5=itemView.findViewById(R.id.work_image5);
            this.image6=itemView.findViewById(R.id.work_image6);
            this.cardView=itemView.findViewById(R.id.workinglistcardview);
            this.linearLayout=itemView.findViewById(R.id.workinglayout);
//
//
//            this.recyclerView=itemView.findViewById(R.id.recycler_work_recycler);

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
