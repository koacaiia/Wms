package fine.koaca.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityPalletResultAdapter extends RecyclerView.Adapter<ActivityPalletResultAdapter.ListViewHolder> {
    ArrayList<ActivityPalletResultList> list;

    public ActivityPalletResultAdapter(ArrayList<ActivityPalletResultList> list){
        this.list=list;

    }
    @NonNull
    @Override
    public ActivityPalletResultAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_plt_result,parent,false);
        return new ActivityPalletResultAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityPalletResultAdapter.ListViewHolder holder, int position) {
        holder.consigneeName.setText(list.get(position).getConsigneeName());
        holder.kpp.setText(String.valueOf(list.get(position).getKppQty()));
        holder.aj.setText(String.valueOf(list.get(position).getAjQty()));
        holder.etc.setText(String.valueOf(list.get(position).getEtcQty()));



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView consigneeName;
        TextView kpp;
        TextView aj;
        TextView etc;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            consigneeName=itemView.findViewById(R.id.list_pltresult_consigneeName);
            kpp=itemView.findViewById(R.id.list_pltresult_kpp);
            aj=itemView.findViewById(R.id.list_pltresult_aj);
            etc=itemView.findViewById(R.id.list_pltresult_etc);



        }
    }
}
