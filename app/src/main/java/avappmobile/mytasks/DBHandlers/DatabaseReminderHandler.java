package avappmobile.mytasks.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import avappmobile.mytasks.Objects.Reminder;

/**
 * Created by Alexandre on 01/03/2015.
 */
public class DatabaseReminderHandler  {

    private static final String ID = "id";
    private static final String DESC = "description";
    private static final String ACTIVE = "active";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String DURATION = "duration";

    private static final String DATABASE_TABLE = "reminders";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DatabaseAdapter.DATABASE_NAME, null, DatabaseAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public DatabaseReminderHandler(Context context) {
        mCtx = context;
    }

    /**
     * open the db
     * @return this
     * @throws android.database.SQLException
     * return type: DatabaseReminderHandler
     */
    public DatabaseReminderHandler open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDbHelper.close();
    }

    public void createReminder(Reminder reminder) {

        ContentValues values = new ContentValues();

        values.put(DESC, reminder.getDescription());
        values.put(ACTIVE, reminder.getActive());
        values.put(HOUR, reminder.getHour());
        values.put(MINUTE, reminder.getMinute());
        values.put(DURATION, reminder.getDuration());

        mDb.insert(DATABASE_TABLE, null, values);
    }

    public Reminder getReminder(int id) {
        Cursor cursor = mDb.query(DATABASE_TABLE, new String[] { ID, DESC, ACTIVE, HOUR, MINUTE, DURATION }, ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));

        return reminder;
    }

    public Reminder getReminder(String description) {
        Cursor cursor = mDb.query(DATABASE_TABLE, new String[] { ID, DESC, ACTIVE, HOUR, MINUTE, DURATION }, DESC + "=?", new String[] { description }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));

        return reminder;
    }

    public int getRemindersCount() {
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
        int count = cursor.getCount();

        return count;
    }

    public int getActiveReminders() {
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE active = 1", null);
        int count = cursor.getCount();

        return count;
    }

    public List<Reminder> getActiveListReminders() {
        List<Reminder> reminders = new ArrayList<Reminder>();

        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DATABASE_TABLE  + " WHERE active = 1", null);

        if (cursor.moveToFirst()) {
            do {
                int active;
                if(cursor.getInt(2) > 0) { active = 1; }
                else { active = 0; }

                reminders.add(new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1), active, Integer.parseInt(cursor.getString(3)),
                          Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5))));
            }
            while (cursor.moveToNext());
        }

        return reminders;
    }

    public int updateReminder(Reminder reminder) {
        ContentValues values = new ContentValues();

        values.put(DESC, reminder.getDescription());
        values.put(ACTIVE, reminder.getActive());
        values.put(HOUR, reminder.getHour());
        values.put(MINUTE, reminder.getMinute());
        values.put(DURATION, reminder.getDuration());

        int rowsAffected = mDb.update(DATABASE_TABLE, values, ID + "=" + reminder.getId(), null);

        return rowsAffected;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<Reminder>();

        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                reminders.add(new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5))));
            }
            while (cursor.moveToNext());
        }

        return reminders;
    }

    public void initReminders(){
        int count = this.getRemindersCount();

        if(count == 0){

            // Create the 3 reminders
            Reminder rem1 = new Reminder(count, "REMINDER_1", 1, 0, 5, 5);
            Reminder rem2 = new Reminder(count + 1, "REMINDER_2", 1, 1, 0, 60);
            Reminder rem3 = new Reminder(count + 2, "REMINDER_3", 1, 24, 0, 1440);

            this.createReminder(rem1);
            this.createReminder(rem2);
            this.createReminder(rem3);
        }
    }

    public void switchActivation(String reminderName, int active) {
        ContentValues values = new ContentValues();

        values.put(ACTIVE, active);
        mDb.update(DATABASE_TABLE, values, DESC + "='" + reminderName + "'", null);
    }
}
