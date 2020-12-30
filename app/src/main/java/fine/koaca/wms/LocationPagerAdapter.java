package fine.koaca.wms;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class LocationPagerAdapter extends PagerAdapter {
    private Location location;

    public LocationPagerAdapter(Location location) {
        this.location = location;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        location.viewPager.addView(location.views.get(position));
        return location.views.get(position);
    }

    @Override
    public int getCount() {
        return location.views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        location.viewPager.removeView((View)object);
    }
}
