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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
 * Created by Alexandre on 28/02/2015.
 */
public class EditTask extends ActionBarActivity implements DatePFragment.OnDatePickedListener, TimePFragment.OnTimePickedListener {

    private EditText txtEditName;
    private EditText txtEditDate;
    private EditText txtEditTime;
    private Button btnEditTask;
    private SeekBar seekBarEditFreq;
    private TextView txtEditFreqDisplay;

    private String taskName;
    private int taskyear;
    private int taskmonth;
    private int taskday;
    private int taskhour;
    private int taskminute;
    private String taskdate;
    private String tasktime;

    private Task taskToEdit;
    private Calendar cal;

    private static final String FR_LANG_CONTEXT = "fr";
    private static final String US_LANG_CONTEXT = "en";

    FragmentManager fm = getSupportFragmentManager();

    private DatabaseTaskHandler dbHandler;
    private int frequency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        initVariables();

        txtEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Show Dialog Fragment
                showDatePickerDialog(v.getId());
            }
        });

        txtEditTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Show Dialog Fragment
                showTimePickerDialog(v.getId());
            }
        });

        seekBarEditFreq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    // TODO: to be completed
    public void initVariables() {

        // The context of the dialog Fragment is getActivity()
        dbHandler = new DatabaseTaskHandler(getApplicationContext());
        try {
            dbHandler.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        int taskId = intent.getExtras().getInt("taskId");
        taskToEdit = dbHandler.getTask(taskId);

        txtEditName = (EditText) findViewById(R.id.txtEditName);
        txtEditName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtEditDate = (EditText) findViewById(R.id.txtEditDate);
        txtEditTime = (EditText) findViewById(R.id.txtEditTime);
        btnEditTask = (Button) findViewById(R.id.btnUpdateTask);
        seekBarEditFreq = (SeekBar) findViewById(R.id.seekBarEditFreq);
        txtEditFreqDisplay = (TextView) findViewById(R.id.txtEditFreqDisp);

        // This prevent from having to tap twice to get the related onClick activity
        txtEditDate.setFocusable(false);
        txtEditTime.setFocusable(false);

        cal = Calendar.getInstance();

        // Init the Task data to be edited.
        txtEditName.setText(taskToEdit.getTitle());
        txtEditDate.setText(taskToEdit.getDate());
        txtEditTime.setText(taskToEdit.getTime());
        frequency = taskToEdit.getFrequency();
        seekBarEditFreq.setProgress(frequency);
        changeFreqDisplay(frequency);

    }

    private void changeFreqDisplay(int freq) {
        switch (freq) {
            case 0:
                txtEditFreqDisplay.setText(R.string.freqNone);
                break;
            case 1:
                txtEditFreqDisplay.setText(R.string.freqDaily);
                break;
            case 2:
                txtEditFreqDisplay.setText(R.string.freqWeekly);
                break;
            case 3:
                txtEditFreqDisplay.setText(R.string.freqMonthly);
                break;
            case 4:
                txtEditFreqDisplay.setText(R.string.freqYearly);
                break;
        }
    }

    public void showDatePickerDialog(int layoutId) {
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        Integer day = cal.get(Calendar.DAY_OF_MONTH);

        // Due to an issue with DatePicker and fr language (locale), if locale language is french, this is changed to us to make the DatePicker working
        if(Locale.getDefault().getLanguage().toString().equals(FR_LANG_CONTEXT)){
            Configuration config = new Configuration();
            config.locale = new Locale(US_LANG_CONTEXT);
            getResources().updateConfiguration(config, null);
        }

        Bundle bundle = createDatePickerBundle(layoutId, year, month, day);
        DialogFragment newFragment = new DatePFragment();
        newFragment.setArguments(bundle);
        newFragment.show(fm, Integer.toString(layoutId));
    }

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

    public void updateTask(View view) {
        // Check if all the necessary data have been filled, return an alert instead.
        if (txtEditName.getText().toString().matches("") || txtEditDate.getText().toString().matches("") || txtEditTime.getText().toString().matches("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext())
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
            // Once the UpdateTask button is clicked, update of the Task object to fulfill the given items and modify the task into the calendar.
            taskName = txtEditName.getText().toString();
            taskToEdit.setTitle(taskName);
            taskToEdit.setFrequency(frequency);

            // Update the task event and reminder into the calendar.
            taskToEdit.updateEvent(getApplicationContext());

            // Updating object in database
            dbHandler.updateTask(taskToEdit);
            Toast.makeText(getApplicationContext(), R.string.updatedTask, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDatePicked(int textId, int year, int month, int day) {
        // Once out from the DatePicker, if we were working on a fr language, we update back the configuration with fr language.
        if(Locale.getDefault().getLanguage().toString().equals(FR_LANG_CONTEXT)){
            Configuration config = new Configuration();
            config.locale = new Locale(FR_LANG_CONTEXT);
            getApplicationContext().getResources().updateConfiguration(config, null);
        }

        // Gathering the value to then be used within the task object.
        taskyear = year;
        taskToEdit.setYear(taskyear);
        taskmonth = month;
        taskToEdit.setMonth(taskmonth);
        taskday = day;
        taskToEdit.setDay(taskday);

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
        txtEditDate.setText(taskdate);
        taskToEdit.setDate(taskdate);
    }

    @Override
    public void onTimePicked(int textId, int hour, int minute) {
        // Gathering the value to then be used within the task object.
        taskhour = hour;
        taskToEdit.setHour(taskhour);
        taskminute = minute;
        taskToEdit.setMinute(taskminute);

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
        txtEditTime.setText(tasktime);
        taskToEdit.setTime(tasktime);
    }
}
