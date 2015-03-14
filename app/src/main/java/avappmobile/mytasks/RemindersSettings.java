package avappmobile.mytasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

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

    private DatabaseReminderHandler dbRemHandler;

    private int reminderhour;
    private int reminderminute;
    private int reminderduration;

    private ListView remindersListView;
    private List<Reminder> RemindersList = new ArrayList<Reminder>();
    private Toolbar toolbarRemindersSettings;
    private ImageButton editBtn;
    private CheckBox disableChkBx;

    private ReminderListAdapter reminderAdapter;
    private int longClickedItemIndex;
    private int longClickedViewId;
    private int reminderId;
    private Calendar cal;

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_settings);
        initVariables();

        remindersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                longClickedViewId = view.getId();
                return false;
            }
        });

        if(dbRemHandler.getActiveReminders() != 0) {
            //RemindersList.addAll(dbRemHandler.getActiveListReminders());
            RemindersList.addAll(dbRemHandler.getAllReminders());
        }

        populateRemindersList();
    }

    private void initVariables(){

        // DatabaseHandlers initialization
        dbRemHandler = new DatabaseReminderHandler(getApplicationContext());
        try {
            dbRemHandler.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Association of displayed items
        remindersListView = (ListView) findViewById(R.id.listViewReminders);
        toolbarRemindersSettings = (Toolbar) findViewById(R.id.toolbar_reminders_settings);
        toolbarRemindersSettings.setNavigationIcon(R.drawable.mytaskstheme_ic_navigation_drawer);
        if(toolbarRemindersSettings != null) {
            setSupportActionBar(toolbarRemindersSettings);
        }

        // Initialization of variables
        registerForContextMenu(remindersListView);
        cal = Calendar.getInstance();
        //reminderSpinner.setSelection(dbRemHandler.getActiveReminders()-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_reminders_settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, RemindersSettings.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void disablingReminder(View view) {
        disableChkBx = (CheckBox) view.findViewById(R.id.chkBxRem);
        reminderId = (int) disableChkBx.getTag();
        Reminder reminderSelected = dbRemHandler.getReminder(reminderId);

        if (disableChkBx.isChecked()) {
            reminderSelected.setActive(1);
            dbRemHandler.updateReminder(reminderSelected);
            // verify if the user enabled the third reminder while the second one is still inactive. Then activating it also.
            if (reminderSelected.getDescription().equals("REMINDER_3")) {
                Reminder reminderSelected2 = dbRemHandler.getReminder("REMINDER_2");
                if(reminderSelected2.getActive() == 0) {
                    reminderSelected2.setActive(1);
                    dbRemHandler.updateReminder(reminderSelected2);
                }
            }
        } else {
            reminderSelected.setActive(0);
            dbRemHandler.updateReminder(reminderSelected);
            // verify if the user disabled the second reminder while the third one is still active. Then unactivating it also.
            if(reminderSelected.getDescription().equals("REMINDER_2")) {
                Reminder reminderSelected2 = dbRemHandler.getReminder("REMINDER_3");
                if(reminderSelected2.getActive() == 1) {
                    reminderSelected2.setActive(0);
                    dbRemHandler.updateReminder(reminderSelected2);
                }
            }
        }

        RemindersList.clear();
        RemindersList.addAll(dbRemHandler.getAllReminders());

        populateRemindersList();


    }

    private void populateRemindersList() {
        reminderAdapter = new ReminderListAdapter(this, R.layout.reminderlist_item, RemindersList);
        remindersListView.setAdapter(reminderAdapter);
    }

    //public void showTimePickerDialog(int layoutId) {
    public void reminderShowTimePickerDialog(View view) {
        editBtn = (ImageButton) view.findViewById(R.id.imBtnEdit);
        reminderId = (int)editBtn.getTag();
        int layoutId = view.getId();
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

        //Reminder reminderToEdit = (Reminder) remindersListView.getAdapter().getItem(reminderId);
        Reminder reminderToEdit = dbRemHandler.getReminder(reminderId);
        reminderToEdit.setDuration(reminderduration);
        reminderToEdit.setHour(reminderhour);
        reminderToEdit.setMinute(reminderminute);
        reminderToEdit.setActive(1);

        dbRemHandler.updateReminder(reminderToEdit);

        RemindersList.clear();
        RemindersList.addAll(dbRemHandler.getAllReminders());

        populateRemindersList();
    }
}
