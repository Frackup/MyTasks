package avappmobile.mytasks.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import avappmobile.mytasks.Objects.Task;

/**
 * Created by Alexandre on 23/02/2015.
 */
public class DatabaseTaskHandler {
/*
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TASKS = "tasks",
            KEY_ID = "id",
            KEY_TITLE = "title",
            KEY_YEAR = "year",
            KEY_MONTH = "month",
            KEY_DAY = "day",
            KEY_HOUR = "hour",
            KEY_MINUTE = "minute",
            KEY_DATE = "date",
            KEY_TIME = "time",
            KEY_EVENT_ID = "eventid";
*/
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

    public DatabaseTaskHandler(Context context) {
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCtx = context;
    }

    /**
     * open the db
     * @return this
     * @throws android.database.SQLException
     * return type: DatabaseTaskHandler
     */
    public DatabaseTaskHandler open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDbHelper.close();
    }
/*
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITLE + " TEXT," +
                YEAR + " TEXT," + MONTH + " TEXT," + DAY + " TEXT," + HOUR + " TEXT," + MINUTE + " TEXT," +
                DATE + " TEXT," + TIME + " TEXT," + EVENT_ID + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        onCreate(db);
    }
*/
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

        mDb.insert(DATABASE_TABLE, null, values);
    }

    public Task getTask(int id) {
        Cursor cursor = mDb.query(DATABASE_TABLE, new String[] { ID, TITLE, YEAR, MONTH, DAY, HOUR, MINUTE, DATE, TIME, EVENT_ID }, ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), cursor.getString(7), cursor.getString(8),
                Long.parseLong(cursor.getString(9)));

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
                        Integer.parseInt(cursor.getString(6)), cursor.getString(7), cursor.getString(8), Long.parseLong(cursor.getString(9))));
            }
            while (cursor.moveToNext());
        }

        return tasks;
    }
/*
    public DatabaseTaskHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TASKS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " TEXT," +
                KEY_YEAR + " TEXT," + KEY_MONTH + " TEXT," + KEY_DAY + " TEXT," + KEY_HOUR + " TEXT," + KEY_MINUTE + " TEXT," +
                KEY_DATE + " TEXT," + KEY_TIME + " TEXT," + KEY_EVENT_ID + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_REMINDERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DESC + " TEXT," +
                KEY_ACTIVE + " TEXT," + KEY_REM_HOUR + " TEXT," + KEY_REM_MINUTE + " TEXT," + KEY_DURATION + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

        onCreate(db);
    }

    public void createTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_YEAR, task.getYear());
        values.put(KEY_MONTH, task.getMonth());
        values.put(KEY_DAY, task.getDay());
        values.put(KEY_HOUR, task.getHour());
        values.put(KEY_MINUTE, task.getMinute());
        values.put(KEY_DATE, task.getDate());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_EVENT_ID, task.getEventId());

        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    public Task getTask(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[] { KEY_ID, KEY_TITLE, KEY_YEAR, KEY_MONTH, KEY_DAY, KEY_HOUR, KEY_MINUTE, KEY_DATE, KEY_TIME, KEY_EVENT_ID }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), cursor.getString(7), cursor.getString(8),
                Long.parseLong(cursor.getString(9)));
        db.close();
        cursor.close();
        return task;
    }

    public void deleteTask(Task task, Context context) {
        // Remove the events and reminders from the calendar app
        task.removeEvent(context);

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + "=?", new String[] { String.valueOf(task.getId()) });
        db.close();
    }

    public int getTasksCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_YEAR, task.getYear());
        values.put(KEY_MONTH, task.getMonth());
        values.put(KEY_DAY, task.getDay());
        values.put(KEY_HOUR, task.getHour());
        values.put(KEY_MINUTE, task.getMinute());
        values.put(KEY_DATE, task.getDate());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_EVENT_ID, task.getEventId());

        int rowsAffected = db.update(TABLE_TASKS, values, KEY_ID + "=" + task.getId(), null);
        db.close();

        return rowsAffected;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<Task>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

        if (cursor.moveToFirst()) {
            do {
                tasks.add(new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)),
                        Integer.parseInt(cursor.getString(6)), cursor.getString(7), cursor.getString(8), Long.parseLong(cursor.getString(9))));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

*/
}
