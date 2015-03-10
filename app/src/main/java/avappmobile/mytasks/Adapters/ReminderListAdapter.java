package avappmobile.mytasks.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import avappmobile.mytasks.Objects.Reminder;
import avappmobile.mytasks.R;

/**
 * Created by Alexandre on 02/03/2015.
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

        TextView remHours = (TextView) view.findViewById(R.id.txtRemHr);
        if(currentReminder.getHour() < 10)
            remHours.setText("0" + String.valueOf(currentReminder.getHour()));
        else
            remHours.setText(String.valueOf(currentReminder.getHour()));

        TextView remMinutes = (TextView) view.findViewById(R.id.txtRemMin);
        if(currentReminder.getMinute() < 10)
            remMinutes.setText("0" + String.valueOf(currentReminder.getMinute()));
        else
            remMinutes.setText(String.valueOf(currentReminder.getMinute()));

        return view;
    }
}
