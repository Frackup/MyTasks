package avappmobile.mytasks.Objects;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import avappmobile.mytasks.DBHandlers.DatabaseAdapter;
import avappmobile.mytasks.DBHandlers.DatabaseReminderHandler;

/**
 * Created by Alexandre on 19/02/2015.
 */
public class Task {

    private static final String DEBUG_TAG = "CalendarActivity";

    private DatabaseReminderHandler dbRemHandler;
    private DatabaseAdapter dbAdapter;

    private String _title;
    private String _date;
    private String _time;
    private int _id;
    private int _year;
    private int _month;
    private int _day;
    private int _hour;
    private int _minute;
    private long _eventId;
    private int _frequency;

    private static final String CALENDAR_URI_BASE = "content://com.android.calendar/";

    public Task (int id, String title, int year, int month, int day, int hour, int minute, String date, String time, long eventId, int frequency){
        _id = id;
        _title = title;
        _year = year;
        _month = month;
        _day = day;
        _hour = hour;
        _minute = minute;
        _date = date;
        _time = time;
        _eventId = eventId;
        _frequency = frequency;
    }

    public int getId() { return _id; }

    public String getTitle(){
        return _title;
    }
    public void setTitle(String title) { this._title = title; }

    public int getYear() {return _year; }
    public void setYear(int year) { this._year = year; }

    public int getMonth() { return _month; }
    public void setMonth(int month) { this._month = month; }

    public int getDay() { return _day; }
    public void setDay(int day) { this._day = day; }

    public int getHour() { return _hour; }
    public void setHour(int hour) { this._hour = hour; }

    public int getMinute() { return _minute; }
    public void setMinute(int minute) { this._minute = minute; }

    public String getDate() { return _date; }
    public void setDate(String date) { this._date = date; }

    public String getTime() { return _time; }
    public void setTime(String time) { this._time = time; }

    public long getEventId() { return _eventId; }

    public int getFrequency() { return _frequency; }
    public void setFrequency(int frequency) { this._frequency = frequency; }

    // Add an event to the calendar of the user.
    public void addEvent(Context context) {

        try {
            GregorianCalendar calDate = new GregorianCalendar(this._year, this._month, this._day, this._hour, this._minute);

            dbAdapter = new DatabaseAdapter(context);
            dbAdapter.open();
            dbRemHandler = new DatabaseReminderHandler(context);
            dbRemHandler.open();

            String freq = defineFrequency();

            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, calDate.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, this._title);
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());


            // TODO: Implement the RFC2445 format when using duration (it's a text format)
            // If a frequency has been selected, add it to the calendar
            if (freq != "") {
                values.put(CalendarContract.Events.RRULE, freq);
                long durationSeconds = 60*30;
                values.put(CalendarContract.Events.DURATION, "P" + durationSeconds + "S"); // RFC2445 format for the duration (using seconds).
            } else {
                values.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis()+60*60*500);
            }

            System.out.println(Calendar.getInstance().getTimeZone().getID());
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // Save the eventId into the Task object for possible future delete.
            this._eventId = Long.parseLong(uri.getLastPathSegment());

            // Check on base how many reminders are set, and what is their duration time in minutes
            // The first reminder will always be set
            setReminder(cr, this._eventId, dbRemHandler.getReminder("REMINDER_1").getDuration());

            if(dbRemHandler.getReminder("REMINDER_2").getActive() == 1)
                setReminder(cr, this._eventId, dbRemHandler.getReminder("REMINDER_2").getDuration());

            if(dbRemHandler.getReminder("REMINDER_3").getActive() == 1)
                setReminder(cr, this._eventId, dbRemHandler.getReminder("REMINDER_3").getDuration());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // routine to add reminders with the event
    public void setReminder(ContentResolver cr, long eventID, int timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            Cursor c = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});
            if (c.moveToFirst()) {
                System.out.println("calendar"
                        + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // function to remove an event from the calendar using the eventId stored within the Task object.
    public void removeEvent(Context context) {
        ContentResolver cr = context.getContentResolver();

        int iNumRowsDeleted = 0;

        Uri eventsUri = Uri.parse(CALENDAR_URI_BASE+"events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, this._eventId);
        iNumRowsDeleted = cr.delete(eventUri, null, null);

        Log.i(DEBUG_TAG, "Deleted " + iNumRowsDeleted + " calendar entry.");
    }

    public int updateEvent(Context context) {
        int iNumRowsUpdated = 0;
        GregorianCalendar calDate = new GregorianCalendar(this._year, this._month, this._day, this._hour, this._minute);

        String freq = defineFrequency();

        ContentValues event = new ContentValues();

        event.put(CalendarContract.Events.TITLE, this._title);
        event.put("hasAlarm", 1); // 0 for false, 1 for true
        event.put(CalendarContract.Events.DTSTART, calDate.getTimeInMillis());
        //event.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis()+60*60*500);

        // TODO: Implement the RFC2445 format when using duration (it's a text format)
        if (freq != "") {
            event.put(CalendarContract.Events.RRULE, freq);
            long durationSeconds = 60*30;
            event.put(CalendarContract.Events.DURATION, "P" + durationSeconds + "S");

        } else {
            event.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis()+60*60*500);
        }

        Uri eventsUri = Uri.parse(CALENDAR_URI_BASE+"events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, this._eventId);

        iNumRowsUpdated = context.getContentResolver().update(eventUri, event, null,
                null);

        Log.i(DEBUG_TAG, "Updated " + iNumRowsUpdated + " calendar entry.");

        return iNumRowsUpdated;
    }

    public String defineFrequency(){

        switch (_frequency){
            case 0:
                return "";
            case 1:
                return "FREQ=DAILY";
            case 2:
                return "FREQ=WEEKLY";
            case 3:
                return "FREQ=MONTHLY";
            case 4:
                return "FREQ=YEARLY";
            default:
                return "";
        }
    }
}
