package avappmobile.mytasks.DBHandlers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Alexandre on 02/03/2015.
 * Global Database Adapter where all the tables and the database are created.
 */
public class DatabaseAdapter {

    public static final String DATABASE_NAME = "myTasks";

    public static final int DATABASE_VERSION = 1;

    private static final String TABLE_TASKS = "tasks",
            KEY_TASK_ID = "id",
            KEY_TITLE = "title",
            KEY_YEAR = "year",
            KEY_MONTH = "month",
            KEY_DAY = "day",
            KEY_HOUR = "hour",
            KEY_MINUTE = "minute",
            KEY_DATE = "date",
            KEY_TIME = "time",
            KEY_EVENT_ID = "eventid",
            KEY_FREQUENCY = "frequency";

    private static final String TABLE_REMINDERS = "reminders",
            KEY_REM_ID = "id",
            KEY_DESC = "description",
            KEY_ACTIVE = "active",
            KEY_REM_HOUR = "hour",
            KEY_REM_MINUTE = "minute",
            KEY_DURATION = "duration";


    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private static final String TAG = "NotesDatabaseAdapter";

    /**
     * Constructor
     * @param ctx
     */
    public DatabaseAdapter(Context ctx)
    {
        context = ctx;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE " + TABLE_TASKS + "(" + KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " TEXT," +
                    KEY_YEAR + " TEXT," + KEY_MONTH + " TEXT," + KEY_DAY + " TEXT," + KEY_HOUR + " TEXT," + KEY_MINUTE + " TEXT," +
                    KEY_DATE + " TEXT," + KEY_TIME + " TEXT," + KEY_EVENT_ID + " TEXT," + KEY_FREQUENCY + " TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_REMINDERS + "(" + KEY_REM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DESC + " TEXT," +
                    KEY_ACTIVE + " TEXT," + KEY_REM_HOUR + " TEXT," + KEY_REM_MINUTE + " TEXT," + KEY_DURATION + " TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * open the db
     * @return this
     * @throws SQLException
     * return type: DatabaseAdapter
     */
    public DatabaseAdapter open() throws SQLException
    {
        DBHelper = new DatabaseHelper(context);
        db = DBHelper.getWritableDatabase();
        return this;
    }

    /**
     * close the db
     * return type: void
     */
    public void close()
    {
        DBHelper.close();
    }
}