package fine.koaca.wms;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
String a;

    public DatePickerFragment(String a) {
        this.a=a;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c= Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
       if(a.equals("a")){
        fine.koaca.wms.MainActivity activity=(fine.koaca.wms.MainActivity)getActivity();
        activity.processDatePickerResult(year,month,dayOfMonth);}
       else{
           fine.koaca.wms.Incargo incargo=(fine.koaca.wms.Incargo)getActivity();
           incargo.processDatePickerResult(year,month,dayOfMonth);
       }


    }
}