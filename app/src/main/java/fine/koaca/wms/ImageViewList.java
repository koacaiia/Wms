package fine.koaca.wms;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageViewList {

    String uriName;

    public ImageViewList(String uriName) {
        this.uriName = uriName;
    }

    public ImageViewList() {

    }

    public String getUriName() {
        return uriName;
    }

    public void setUriName(String uriName) {
        this.uriName = uriName;
    }
}
