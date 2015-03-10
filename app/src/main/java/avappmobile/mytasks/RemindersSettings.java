package avappmobile.mytasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import avappmobile.mytasks.Adapters.ReminderListAdapter;
import avappmobile.mytasks.DBHandlers.DatabaseReminderHandler;
import avappmobile.mytasks.Objects.Reminder;

/**
 * Created by Alexandre on 01/03/2015.
 */
public class RemindersSettings extends ActionBarActivity implements TimePFragment.OnTimePickedListener {

    private static final int EDIT = 0;

    private DatabaseReminderHandler dbRemHandler;

    private int reminderhour;
    private int reminderminute;
    private int reminderduration;

    private Spinner reminderSpinner;
    private ListView remindersListView;
    private List<Reminder> RemindersList = new ArrayList<Reminder>();

    private ReminderListAdapter reminderAdapter;
    private int longClickedItemIndex;
    private int longClickedViewId;
    private Calendar cal;

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_settings);

        cal = Calendar.getInstance();
        dbRemHandler = new DatabaseReminderHandler(getApplicationContext());
        try {
            dbRemHandler.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        remindersListView = (ListView) findViewById(R.id.listViewReminders);

        registerForContextMenu(remindersListView);

        remindersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                longClickedViewId = view.getId();
                return false;
            }
        });

        reminderSpinner = (Spinner) findViewById(R.id.spinReminder);
        reminderSpinner.setSelection(dbRemHandler.getActiveReminders()-1);

        reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRemindersList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(dbRemHandler.getActiveReminders() != 0) {
            RemindersList.addAll(dbRemHandler.getActiveListReminders());
        }

        populateRemindersList();
    }

    // To define which reminders are displayed depending on the number selected with the spinner.
    private void updateRemindersList(int number){
        if(number == 0) {
            // Inactivate the reminders 2 and 3
            dbRemHandler.switchActivation("REMINDER_2", 0);
            dbRemHandler.switchActivation("REMINDER_3", 0);

            RemindersList.clear();

            // Display the first reminder into the list
            RemindersList.add(dbRemHandler.getReminder("REMINDER_1"));
            reminderAdapter.notifyDataSetChanged();
        } else if(number == 1) {
            // Inactivate reminder 3 and activate reminder 2
            dbRemHandler.switchActivation("REMINDER_3", 0);
            dbRemHandler.switchActivation("REMINDER_2", 1);

            RemindersList.clear();

            // Display the 2 first reminders into the list
            RemindersList.add(dbRemHandler.getReminder("REMINDER_1"));
            RemindersList.add(dbRemHandler.getReminder("REMINDER_2"));
            reminderAdapter.notifyDataSetChanged();
        } else if (number == 2) {
            // Activate all reminders (remember that reminder 1 is always active, can't be inactivated)
            dbRemHandler.switchActivation("REMINDER_3", 1);
            dbRemHandler.switchActivation("REMINDER_2", 1);

            RemindersList.clear();

            // Display all the 3 reminders into the list
            RemindersList.addAll(dbRemHandler.getAllReminders());
            reminderAdapter.notifyDataSetChanged();
        }
    }

    private void populateRemindersList() {
        //reminderAdapter = new ReminderListAdapter();
        reminderAdapter = new ReminderListAdapter(this, R.layout.reminderlist_item, RemindersList);
        remindersListView.setAdapter(reminderAdapter);
    }

    public void gotToHomePage(View view) {
        Intent intent = new Intent(this,HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.edit_pencil_icon);
        menu.setHeaderTitle(R.string.contextMenu_title);
        menu.add(menu.NONE, EDIT, menu.NONE, R.string.contextMenu_edit);
    }

    public boolean onContextItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case EDIT:
                if(longClickedItemIndex == 0) {
                    Toast.makeText(getApplicationContext(), R.string.blocked_reminder, Toast.LENGTH_SHORT).show();
                    break;
                }
                else
                    showTimePickerDialog(longClickedViewId);

                break;
        }

        return super.onContextItemSelected(item);
    }

    public void showTimePickerDialog(int layoutId) {
        Integer hour = cal.get(Calendar.HOUR);
        Integer minute = cal.get(Calendar.MINUTE);

        Bundle bundle = createTimePickerBundle(layoutId, hour, minute);
        DialogFragment newFragment = new TimePFragment();
        newFragment.setArguments(bundle);
        newFragment.show(fm, Integer.toString(layoutId));
    }

    public Bundle createTimePickerBundle(int layoutId, int hour, int minute) {
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        bundle.putInt("hour", hour);
        bundle.putInt("minute", minute);
        return bundle;
    }

    // This is used when a time is selected within the TimePicker widget
    @Override
    public void onTimePicked(int LayoutId, int hour, int minute){

        // Gathering the value to then be used within the task object.
        reminderhour = hour;
        reminderminute = minute;
        reminderduration = reminderminute + reminderhour*60;

        Reminder reminderToEdit = (Reminder) remindersListView.getAdapter().getItem(longClickedItemIndex);
        reminderToEdit.setDuration(reminderduration);
        reminderToEdit.setHour(reminderhour);
        reminderToEdit.setMinute(reminderminute);
        reminderToEdit.setActive(1);

        dbRemHandler.updateReminder(reminderToEdit);

        RemindersList.clear();
        RemindersList.addAll(dbRemHandler.getActiveListReminders());

        populateRemindersList();
    }
}
