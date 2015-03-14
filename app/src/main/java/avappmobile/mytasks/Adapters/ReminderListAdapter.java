package avappmobile.mytasks.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

        RelativeLayout lay = (RelativeLayout) view.findViewById(R.id.relativeLayoutReminder);
        Reminder currentReminder = Reminders.get(position);

        TextView remDescription = (TextView) view.findViewById(R.id.txtReminderDesc);
        TextView remHours = (TextView) view.findViewById(R.id.txtRemHr);
        TextView remHrsTxt = (TextView) view.findViewById(R.id.txtHour);
        TextView remMinutes = (TextView) view.findViewById(R.id.txtRemMin);
        TextView remMnsText = (TextView) view.findViewById(R.id.txtMinute);

        switch (currentReminder.getDescription()) {
            case "REMINDER_1":
                remDescription.setText(mCtx.getResources().getString(R.string.firstRem));
                break;
            case "REMINDER_2":
                remDescription.setText(mCtx.getResources().getString(R.string.secondRem));
                break;
            case "REMINDER_3":
                remDescription.setText(mCtx.getResources().getString(R.string.thirdRem));
                break;
            default:
                remDescription.setText("Reminder Error");
                break;
        }

        if(currentReminder.getHour() == 0) {
            remHours.setVisibility(TextView.GONE);
            remHrsTxt.setVisibility(TextView.GONE);
        }
        else {
            remHours.setVisibility(TextView.GONE);
            remHrsTxt.setVisibility(TextView.GONE);
            remHours.setText(String.valueOf(currentReminder.getHour()));
        }

        if(currentReminder.getMinute() == 0){
            remMinutes.setVisibility(View.INVISIBLE);
            remMnsText.setVisibility(View.INVISIBLE);
        }
        else {
            remMinutes.setVisibility(View.VISIBLE);
            remMnsText.setVisibility(View.VISIBLE);
            remMinutes.setText(String.valueOf(currentReminder.getMinute()));
        }

        // Initializing the button
        ImageButton editButton = (ImageButton) view.findViewById(R.id.imBtnEdit);
        editButton.setTag(currentReminder.getId());

        // Initializing the check box
        CheckBox chckBxSlctRem = (CheckBox) view.findViewById(R.id.chkBxRem);
        chckBxSlctRem.setTag(currentReminder.getId());
        if(currentReminder.getActive() == 1) {
            chckBxSlctRem.setChecked(true);
            view.setBackgroundColor(mCtx.getResources().getColor(R.color.mt_reminderBckgrnd_transparent));
        } else {
            chckBxSlctRem.setChecked(false);
            view.setBackgroundColor(mCtx.getResources().getColor(R.color.mt_reminderBckgrnd_color));
        }

        // Display specific for the first reminder item which can't be modified nor disabled.
        if(currentReminder.getDescription().equals("REMINDER_1")){
            chckBxSlctRem.setVisibility(view.INVISIBLE);
            editButton.setVisibility(view.INVISIBLE);
        }

        return view;
    }
}
