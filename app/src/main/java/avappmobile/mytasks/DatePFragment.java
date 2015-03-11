package avappmobile.mytasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Alexandre on 18/02/2015.
 * This class is built to put all the details dealing with the creation of a Datepicker into a DialogFragment
 */
public class DatePFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    DatePicker dp;
    OnDatePickedListener mCallback;
    Integer mLayoutId;

    private int cYear;
    private int cMonth;
    private int cDay;
    private long curDate;

    // Interface definition
    public interface OnDatePickedListener {
        public void onDatePicked(int textId, int year, int month, int day);
    }

    // make sure the Activity implement the OnDatePickedListener
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnDatePickedListener)activity;
        }

        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + R.string.ErrorDPMessage);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        initVariables();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, cYear, cMonth, cDay);
        // This to grey all dates before today's date.
        datePickerDialog.getDatePicker().setMinDate(curDate);

        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog;
    }

    public void initVariables() {

        View view = getActivity().getLayoutInflater().inflate(R.layout.datepfragment, null);

        final Calendar cal = Calendar.getInstance();
        mCallback = (OnDatePickedListener)getActivity();
        Bundle bundle = this.getArguments();
        mLayoutId = bundle.getInt("layoutId");
        cYear = cal.get(Calendar.YEAR);
        cMonth = cal.get(Calendar.MONTH);
        cDay = cal.get(Calendar.DAY_OF_MONTH);
        curDate = cal.getTimeInMillis();

        dp = (DatePicker) view.findViewById(R.id.datePicker);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (mCallback != null) {
            // Use the date chosen by the user.
            mCallback.onDatePicked(mLayoutId, year, monthOfYear, dayOfMonth);
        }
    }


}
