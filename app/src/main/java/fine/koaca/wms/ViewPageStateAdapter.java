package fine.koaca.wms;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageStateAdapter extends FragmentStateAdapter {
    int mCount;
    FragmentActivity fragmentActivity;

    public ViewPageStateAdapter(FragmentActivity fa, int mCount){
        super(fa);
        this.fragmentActivity=fa;
                this.mCount=mCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String activityValue=fragmentActivity.getLocalClassName();
        Fragment fragment = null;
        int index=getRealPosition(position);
        switch(index){
            case 0:
                fragment=new FragmentImagePicked(activityValue);
                break;
            case 1:
                fragment=new FragmentImageOri(activityValue);
                break;
            case 2:
                fragment=new FragmentImageAll(activityValue);
                break;
            case 3:
                fragment=new FragmentImageRe(activityValue);
                break;
        }
//        if(index==0) return new FragmentImagePicked();
//        else if(index==1) return new FragmentImageOri();
//        else if(index==2) return new FragmentImageAll();
        assert fragment != null;
        return fragment;
    }

    private int getRealPosition(int position) {
        return position % mCount;
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
