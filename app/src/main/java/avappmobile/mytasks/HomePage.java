package avappmobile.mytasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import avappmobile.mytasks.Adapters.TaskListAdapter;
import avappmobile.mytasks.DBHandlers.DatabaseAdapter;
import avappmobile.mytasks.DBHandlers.DatabaseReminderHandler;
import avappmobile.mytasks.DBHandlers.DatabaseTaskHandler;
import avappmobile.mytasks.Objects.Task;


public class HomePage extends ActionBarActivity {

    final Context context = this;
    private static final int EDIT = 0, DELETE = 1;
    private static final String TASKID = "taskId";

    private DatabaseAdapter dbAdapter;
    private DatabaseTaskHandler dbHandler;
    private DatabaseReminderHandler dbRemHandler;
    ListView listViewDayTasks;
    ListView listViewNextTasks;
    ListView tempListView;
    ArrayAdapter<Task> taskAdapter;
    private Toolbar toolbarHomePage;

    private boolean loaded = false;

    int longClickedItemIndex;

    List<Task> DayTasks = new ArrayList<Task>();
    List<Task> NextTasks = new ArrayList<Task>();
    List<Task> TempList = new ArrayList<Task>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initVariables();

        // The following piece of code allows to empty database before testing when reinstalling the app for test.
        //getApplicationContext().deleteDatabase("myTasks");

        listViewDayTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                tempListView = listViewDayTasks;
                return false;
            }
        });

        listViewNextTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                tempListView = listViewNextTasks;
                return false;
            }
        });

        if(dbHandler.getDayTasks() != null)
            DayTasks.addAll(dbHandler.getDayTasks());

        populateTasksList(listViewDayTasks, DayTasks);

        if(dbHandler.getNextTasks() != null)
            NextTasks.addAll(dbHandler.getNextTasks());

        populateTasksList(listViewNextTasks, NextTasks);

        loaded = true;
    }

    public void initVariables() {

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();

        dbHandler = new DatabaseTaskHandler(this);
        try {
            dbHandler.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbRemHandler = new DatabaseReminderHandler(this);
        try {
            dbRemHandler.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbRemHandler.initReminders();

        toolbarHomePage = (Toolbar) findViewById(R.id.toolbar_home_page);
        toolbarHomePage.setTitle("My Tasks");
        if (toolbarHomePage != null) {
            setSupportActionBar(toolbarHomePage);
        }
        listViewDayTasks = (ListView) findViewById(R.id.listViewTasksDay);
        listViewNextTasks = (ListView) findViewById(R.id.listViewTasksNext);
        registerForContextMenu(listViewDayTasks);
        registerForContextMenu(listViewNextTasks);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
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
            case R.id.action_add:
                View view = getLayoutInflater().inflate(R.layout.activity_home_page,null);
                goToAddTask(view);
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void populateTasksList(ListView lv, List<Task> listTasks) {
        //taskAdapter = new TaskListAdapter();
        taskAdapter = new TaskListAdapter(this, R.layout.tasklist_item, listTasks);
        lv.setAdapter(taskAdapter);
    }

    public void goToAddTask(View view){
        Intent intent = new Intent(this, AddTask.class);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();

        if (!loaded) {
/* Old part (working)
            if (dbHandler.getTasksCount() != 0) {
                DayTasks.clear();
                DayTasks.addAll(dbHandler.getAllTasks());
            }

            populateTasksList();
*/
            if(dbHandler.getDayTasks() != null) {
                DayTasks.clear();
                DayTasks.addAll(dbHandler.getDayTasks());
            }
            populateTasksList(listViewDayTasks, DayTasks);

            if(dbHandler.getNextTasks() != null) {
                NextTasks.clear();
                NextTasks.addAll(dbHandler.getNextTasks());
            }
            populateTasksList(listViewNextTasks, NextTasks);
        }

        loaded = false;
    }

    public void editTask() {
        Task taskToEdit = (Task) tempListView.getAdapter().getItem(longClickedItemIndex);
        int taskId = taskToEdit.getId();

        Intent intent = new Intent(this, EditTask.class);
        intent.putExtra(TASKID, taskId);
        startActivity(intent);
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.edit_pencil_icon);
        menu.setHeaderTitle(R.string.contextMenu_title);
        menu.add(menu.NONE, EDIT, menu.NONE, R.string.contextMenu_edit);
        menu.add(menu.NONE, DELETE, menu.NONE, R.string.contextMenu_delete);
    }

    public boolean onContextItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case EDIT:
                editTask();
                break;
            case DELETE:
                deleteTask();
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void deleteTask(){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                // Set Dialog Icon
                .setIcon(R.drawable.ic_bullet_key_permission)
                        // Set Dialog Title
                .setTitle(R.string.removeAlert)
                        // Set Dialog Message
                .setMessage(R.string.deleteMessage)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Task taskToDelete = (Task) tempListView.getAdapter().getItem(longClickedItemIndex);

                        // Removing the task from the database and from the calendar
                        dbHandler.deleteTask(taskToDelete, context);

                        // Removing the task from the listView

                        TaskListAdapter tAdapter = (TaskListAdapter) tempListView.getAdapter();
                        tAdapter.remove(tAdapter.getItem(longClickedItemIndex));
                        tAdapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), R.string.removedTask, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        alertDialog.show();

    }
}
