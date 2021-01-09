package fine.koaca.wms;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogCus {
    Context context;
    public AlertDialogCus(Context context){
        this.context=context;
    }

    public void alertConsignee(){
        AlertDialog.Builder dialogConsignee=new AlertDialog.Builder(context);
        dialogConsignee.setTitle("1");
        dialogConsignee.show();
    }
}
