package avappmobile.mytasks.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import avappmobile.mytasks.Objects.Task;

/**
 * Created by Alexandre on 23/02/2015.
 * All database methods related to the Tasks
 */
public class DatabaseTaskHandler {

    private static final String DATABASE_TABLE = "tasks";

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String EVENT_ID = "eventid";
    private static final String FREQUENCY = "frequency";

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

    /**
     * Constructor
     * @param context
     */
    public DatabaseTaskHandler(Context context) {
        mCtx = context;
    }

    /**
     * open the db
     * @return this
     * @throws SQLException
     * return type: DatabaseTaskHandler
     */
    public DatabaseTaskHandler open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * close the db
     * return type: void
     */
    public void close(){
        mDbHelper.close();
    }

    public void createTask(Task task) {
        ContentValues values = new ContentValues();

        values.put(TITLE, task.getTitle());
        values.put(YEAR, task.getYear());
        values.put(MONTH, task.getMonth());
        values.put(DAY, task.getDay());
        values.put(HOUR, task.getHour());
        values.put(MINUTE, task.getMinute());
        values.put(DATE, task.getDate());
        values.put(TIME, task.getTime());
        values.put(EVENT_ID, task.getEventId());
        values.put(FREQUENCY, task.getFrequency());

        mDb.insert(DATABASE_TABLE, null, values);
    }

    public Task getTask(int id) {
        Cursor cursor = mDb.query(DATABASE_TABLE, new String[] { ID, TITLE, YEAR, MONTH, DAY, HOUR, MINUTE, DATE, TIME, EVENT_ID, FREQUENCY }, ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), cursor.getString(7), cursor.getString(8),
                Long.parseLong(cursor.getString(9)), Integer.parseInt(cursor.getString(10)));

        return task;
    }

    public void deleteTask(Task task, Context context) {
        // Remove the events and reminders from the calendar app
        task.removeEvent(context);

        mDb.delete(DATABASE_TABLE, ID + "=?", new String[]{String.valueOf(task.getId())});
    }

    public int getTasksCount() {
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
        int count = cursor.getCount();

        return count;
    }

    public int updateTask(Task task) {
        ContentValues values = new ContentValues();

        values.put(TITLE, task.getTitle());
        values.put(YEAR, task.getYear());
        values.put(MONTH, task.getMonth());
        values.put(DAY, task.getDay());
        values.put(HOUR, task.getHour());
        values.put(MINUTE, task.getMinute());
        values.put(DATE, task.getDate());
        values.put(TIME, task.getTime());
        values.put(EVENT_ID, task.getEventId());
        values.put(FREQUENCY, task.getFrequency());

        int rowsAffected = mDb.update(DATABASE_TABLE, values, ID + "=" + task.getId(), null);

        return rowsAffected;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<Task>();

        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                tasks.add(new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)),
                        Integer.parseInt(cursor.getString(6)), cursor.getString(7), cursor.getString(8), Long.parseLong(cursor.getString(9)),
                        Integer.parseInt(cursor.getString(10))));
            }
            while (cursor.moveToNext());
        }

        return tasks;
    }

    public List<Task> getDayTasks() {
        List<Task> tasks = new ArrayList<Task>();
        Calendar calDate = Calendar.getInstance();
        int day = calDate.get(Calendar.DAY_OF_MONTH);
        int month = calDate.get(Calendar.MONTH);
        int year = calDate.get(Calendar.YEAR);

        Cursor cursor = mDb.query(DATABASE_TABLE, new String[] { ID, TITLE, YEAR, MONTH, DAY, HOUR, MINUTE, DATE, TIME, EVENT_ID, FREQUENCY },
                DAY + "=?" + " AND " + MONTH + "=?" + " AND " + YEAR + "=?",
                new String[] { String.valueOf(day), String.valueOf(month), String.valueOf(year) },
                null, null, null, null );

        if (cursor.moveToFirst()) {
            do {
                tasks.add(new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)),
                        Integer.parseInt(cursor.getString(6)), cursor.getString(7), cursor.getString(8), Long.parseLong(cursor.getString(9)),
                        Integer.parseInt(cursor.getString(10))));
            }
            while (cursor.moveToNext());
        }

        return tasks;
    }

    public List<Task> getNextTasks() {
        List<Task> tasks = new ArrayList<Task>();
        Calendar calDate = Calendar.getInstance();
        int day = calDate.get(Calendar.DAY_OF_MONTH);
        int month = calDate.get(Calendar.MONTH);
        int year = calDate.get(Calendar.YEAR);

        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE MONTH = " + month + " AND YEAR = " + year + " AND DAY > " + day +
                " UNION ALL SELECT * FROM " + DATABASE_TABLE + " WHERE MONTH > " + month + " AND YEAR = " + year +
                " UNION ALL SELECT * FROM " + DATABASE_TABLE + " WHERE YEAR > " + year
                , null);

        if (cursor.moveToFirst()) {
            do {
                tasks.add(new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)),
                        Integer.parseInt(cursor.getString(6)), cursor.getString(7), cursor.getString(8), Long.parseLong(cursor.getString(9)),
                        Integer.parseInt(cursor.getString(10))));
            }
            while (cursor.moveToNext());
        }

        return tasks;
    }
}
