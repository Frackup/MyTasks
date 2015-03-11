package avappmobile.mytasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;

import avappmobile.mytasks.DBHandlers.DatabaseTaskHandler;
import avappmobile.mytasks.Objects.Task;

/**
 * Created by Alexandre on 18/02/2015.
 * This class is build to allow users to add a task into the database and the calendar.
 */
public class AddTask extends ActionBarActivity implements DatePFragment.OnDatePickedListener, TimePFragment.OnTimePickedListener {

    // Variables to link the xml object used
    private EditText txtTaskName;
    private EditText txtTaskDate;
    private EditText txtTaskTime;
    private SeekBar seekBarFrequency;
    private TextView txtFreqDisplay;

    // Variables dealing with a Task object to be edited and used within the AddTask Activity
    private String taskName;
    private int taskyear;
    private int taskmonth;
    private int taskday;
    private int taskhour;
    private int taskminute;
    private String taskdate;
    private String tasktime;

    // Other needed variables
    private Task mTask;
    private Calendar cal;
    private int frequency;

    private DatabaseTaskHandler dbHandler;

    final Context context = this;

    private static final String FR_LANG_CONTEXT = "fr";
    private static final String US_LANG_CONTEXT = "en";

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        initVariables();

        // Implementing the detection of a click on the date selection line (to display the DatePicker)
        txtTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Show Dialog Fragment
                showDatePickerDialog(v.getId());
            }
        });

        // Implementing the detection of a click on the time selection line (to display the TimePicker)
        txtTaskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Show Dialog Fragment
                showTimePickerDialog(v.getId());
            }
        });

        // Implementing the detection of an action on the seekbar (to grab the number picked)
        seekBarFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequency = progress;
                changeFreqDisplay(frequency);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // Initialization of all the variables of the java class.
    private void initVariables() {

        // DatabaseHandler initialization
        dbHandler = new DatabaseTaskHandler(this);
        try {
            dbHandler.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Locate the textView of the xml display and associate them to the private variables.
        txtTaskName = (EditText) findViewById(R.id.txtTaskName);
        txtTaskDate = (EditText) findViewById(R.id.txtTaskDate);
        txtTaskTime = (EditText) findViewById(R.id.txtTaskTime);
        seekBarFrequency = (SeekBar) findViewById(R.id.seekBarFreq);
        txtFreqDisplay = (TextView) findViewById(R.id.txtFreqDisp);

        // This prevent from having to tap twice when selecting the chose date or chose time line.
        txtTaskDate.setFocusable(false);
        txtTaskTime.setFocusable(false);

        // Other initialization
        txtTaskName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        cal = Calendar.getInstance();
        frequency = 0;

        changeFreqDisplay(frequency);
    }

    // Switch to translate the (int) frequency into a text to be displayed on screen.
    private void changeFreqDisplay(int freq) {
        switch (freq) {
            case 0:
                txtFreqDisplay.setText(R.string.freqNone);
                break;
            case 1:
                txtFreqDisplay.setText(R.string.freqDaily);
                break;
            case 2:
                txtFreqDisplay.setText(R.string.freqWeekly);
                break;
            case 3:
                txtFreqDisplay.setText(R.string.freqMonthly);
                break;
            case 4:
                txtFreqDisplay.setText(R.string.freqYearly);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_add_task, menu);

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
            case R.id.action_save:
                View view = getLayoutInflater().inflate(R.layout.activity_add_task, null);
                try {
                    createNewTask(view);
                } catch (SQLException e) {
                    e.printStackTrace();
                }            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // Methods to display the calendar to pick a date.
    public void showDatePickerDialog(int layoutId) {
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        Integer day = cal.get(Calendar.DAY_OF_MONTH);

        // Due to an issue with DatePicker and fr language (locale), if locale language is "fr", this is changed to "us" to make the DatePicker working
        if(Locale.getDefault().getLanguage().toString().equals(FR_LANG_CONTEXT)){
            Configuration config = new Configuration();
            config.locale = new Locale(US_LANG_CONTEXT);
            getApplicationContext().getResources().updateConfiguration(config, null);
        }

        Bundle bundle = createDatePickerBundle(layoutId, year, month, day);
        DialogFragment newFragment = new DatePFragment();
        newFragment.setArguments(bundle);
        newFragment.show(fm, Integer.toString(layoutId));
    }

    // Methods to display the timer to pick a time.
    public void showTimePickerDialog(int layoutId) {
        Integer hour = cal.get(Calendar.HOUR);
        Integer minute = cal.get(Calendar.MINUTE);

        Bundle bundle = createTimePickerBundle(layoutId, hour, minute);
        DialogFragment newFragment = new TimePFragment();
        newFragment.setArguments(bundle);
        newFragment.show(fm, Integer.toString(layoutId));
    }

    public Bundle createDatePickerBundle(int layoutId, int year, int month, int day) {
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        bundle.putInt("year", year);
        bundle.putInt("month", month);
        bundle.putInt("day", day);
        return bundle;
    }

    public Bundle createTimePickerBundle(int layoutId, int hour, int minute) {
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        bundle.putInt("hour", hour);
        bundle.putInt("minute", minute);
        return bundle;
    }

    // Method to create a new task when saving the selected items.
    /**
     * @return this
     * @throws SQLException
     */
    public void createNewTask(View view) throws SQLException {
        // Check if all the necessary data have been filled, return an alert instead.
        if (txtTaskName.getText().toString().matches("") || txtTaskDate.getText().toString().matches("") || txtTaskTime.getText().toString().matches("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    // Set Dialog Icon
                    .setIcon(R.drawable.ic_bullet_key_permission)
                            // Set Dialog Title
                    .setTitle(R.string.incompleteData)
                            // Set Dialog Message
                    .setMessage(R.string.errorMessage)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something else
                        }
                    }).create();

            alertDialog.show();
        } else {
            // Once the CreateTask button is clicked, creation of the Task object to fulfill the given items and add the task to the calendar.
            // date, time, year, month, day, hour and minute have been initialized within the onDatePicked and onTimePicked methods
            taskName = txtTaskName.getText().toString();

            // Create a new Task object
            mTask = new Task(dbHandler.getTasksCount(), taskName, taskyear, taskmonth, taskday, taskhour, taskminute, taskdate, tasktime, 0, frequency);

            // Add task event and reminder to the calendar.
            mTask.addEvent(context);

            // Adding Task object to the database
            dbHandler.createTask(mTask);
            Toast.makeText(getApplicationContext(), R.string.addedTask, Toast.LENGTH_SHORT).show();

            //removing the data populated into the different EditText boxes.
            txtTaskName.setText("");
            txtTaskDate.setText("");
            txtTaskTime.setText("");
            frequency = 0;
            seekBarFrequency.setProgress(frequency);
            changeFreqDisplay(frequency);

        }
    }

    // To get back to the HomePage and clearing all the activities history (from the back button)
    public void goToHomePage(View view) {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // This is used when a date is selected into the DatePicker widget
    @Override
    public void onDatePicked(int LayoutId, int year, int month, int day){
        // Once out from the DatePicker, if we were working on a fr language, we update back the configuration with fr language.
        if(Locale.getDefault().getLanguage().toString().equals(FR_LANG_CONTEXT)){
            Configuration config = new Configuration();
            config.locale = new Locale(FR_LANG_CONTEXT);
            getApplicationContext().getResources().updateConfiguration(config, null);
        }

        // Gathering the value to then be used within the task object.
        taskyear = year;
        taskmonth = month;
        taskday = day;

        // Building the date to be displayed (as int are used, check if they're on 1 or 2 digit(s) to add a 0 before).
        // Also check the language
        if(Locale.getDefault().getLanguage().toString().equals(FR_LANG_CONTEXT)) {
            if (String.valueOf(day).length() == 1) {
                taskdate = "0" + day + "/";
            } else {
                taskdate = day + "/";
            }

            if (String.valueOf(month).length() == 1) {
                taskdate += "0" + (month+1) + "/" + year;
            } else {
                taskdate += (month+1) + "/" + year;
            }
        } else {
            if (String.valueOf(month).length() == 1) {
                taskdate = "0" + (month+1) + "/";
            } else {
                taskdate = (month+1) + "/";
            }

            if (String.valueOf(day).length() == 1) {
                taskdate += "0" + day + "/" + year;
            } else {
                taskdate += day + "/" + year;
            }
        }

        // Displaying the date in the EditText box.
        txtTaskDate.setText(taskdate);
    }

    // This is used when a time is selected within the TimePicker widget
    @Override
    public void onTimePicked(int LayoutId, int hour, int minute){

        // Gathering the value to then be used within the task object.
        taskhour = hour;
        taskminute = minute;

        // Building the time to be displayed (as int are used, check if they're on 1 or 2 digit to add a 0 before).
        if (String.valueOf(hour).length() == 1){
            tasktime ="0" + hour + ":";
        } else {
            tasktime = hour + ":";
        }

        if (String.valueOf(minute).length() == 1){
            tasktime += "0" + minute;
        } else {
            tasktime += minute;
        }

        // Displaying the time in the EditText box.
        txtTaskTime.setText(tasktime);
    }
}
