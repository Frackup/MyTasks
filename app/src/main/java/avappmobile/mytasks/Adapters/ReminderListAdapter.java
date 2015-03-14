package avappmobile.mytasks.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import avappmobile.mytasks.Objects.Reminder;
import avappmobile.mytasks.R;

/**
 * Created by Alexandre on 02/03/2015.
 * This module allows to create the list displaying the reminders according to the reminderlist_item.xml format
 */
public class ReminderListAdapter extends ArrayAdapter<Reminder> {

    private LayoutInflater layoutInflater;
    private List<Reminder> Reminders = new ArrayList<Reminder>();
    private Context mCtx;

    public ReminderListAdapter(Context context, int resource, List<Reminder> reminders) {
        super(context, resource, reminders);
        Reminders = reminders;
        mCtx = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null) {
            layoutInflater = LayoutInflater.from(mCtx);

            view = layoutInflater.inflate(R.layout.reminderlist_item, parent, false);
        }

        Reminder currentReminder = Reminders.get(position);

        TextView remDescription = (TextView) view.findViewById(R.id.txtReminderDesc);
        remDescription.setText(currentReminder.getDescription());

        // Checking if the hour (int) is on 1 or 2 digits, to complete if needed.
        TextView remHours = (TextView) view.findViewById(R.id.txtRemHr);
        if(currentReminder.getHour() < 10)
            remHours.setText("0" + String.valueOf(currentReminder.getHour()));
        else
            remHours.setText(String.valueOf(currentReminder.getHour()));

        // Checking if the hour (int) is on 1 or 2 digits, to complete if needed.
        TextView remMinutes = (TextView) view.findViewById(R.id.txtRemMin);
        if(currentReminder.getMinute() < 10)
            remMinutes.setText("0" + String.valueOf(currentReminder.getMinute()));
        else
            remMinutes.setText(String.valueOf(currentReminder.getMinute()));

        // Initializing the button
        ImageButton editButton = (ImageButton) view.findViewById(R.id.imBtnEdit);
        editButton.setTag(currentReminder.getId());

        // Initializing the check box
        CheckBox chckBxSlctRem = (CheckBox) view.findViewById(R.id.chkBxRem);
        chckBxSlctRem.setTag(currentReminder.getId());
        if(currentReminder.getActive() == 1) {
            chckBxSlctRem.setChecked(true);
        } else {
            chckBxSlctRem.setChecked(false);
        }

        // Display specific for the firts reminder item which can't be modified nor disabled.
        if(currentReminder.getDescription() == "REMINDER_1"){
            chckBxSlctRem.setVisibility(view.INVISIBLE);
            editButton.setVisibility(view.INVISIBLE);
        }

        return view;
    }
}
