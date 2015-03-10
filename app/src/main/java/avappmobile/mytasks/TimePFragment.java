package avappmobile.mytasks;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Alexandre on 18/02/2015.
 */
public class TimePFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    TimePicker tp;
    OnTimePickedListener mCallback;
    Integer mLayoutId = null;

    // Interface definition
    public interface OnTimePickedListener {
        public void onTimePicked(int textId, int hour, int minute);
    }

    // make sure the Activity implement the OnDatePickedListener
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnTimePickedListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimePickedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        mCallback = (OnTimePickedListener)getActivity();

        Calendar cal = Calendar.getInstance();
        Bundle bundle = this.getArguments();
        mLayoutId = bundle.getInt("layoutId");
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        View view = getActivity().getLayoutInflater().inflate(R.layout.timepfragment, null);

        tp = (TimePicker) view.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, true);

        // Create a new instance of DatePickerDialog and return it
        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        if (mCallback != null) {
            // Use the date chosen by the user.

            mCallback.onTimePicked(mLayoutId, hour, minute);
        }
    }
}
