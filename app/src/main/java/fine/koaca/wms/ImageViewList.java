package fine.koaca.wms;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageViewList {
    Bitmap imageview;

    public ImageViewList(Bitmap imageview) {
        this.imageview = imageview;
    }

    public Bitmap getImageview() {
        return imageview;
    }

    public void setImageview(Bitmap imageview) {
        this.imageview = imageview;
    }
}
