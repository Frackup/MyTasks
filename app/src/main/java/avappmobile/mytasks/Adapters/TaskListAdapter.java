package avappmobile.mytasks.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import avappmobile.mytasks.Objects.Task;
import avappmobile.mytasks.R;

/**
 * Created by Alexandre on 02/03/2015.
 */
public class TaskListAdapter extends ArrayAdapter<Task> {

    private LayoutInflater layoutInflater;
    private List<Task> Tasks = new ArrayList<Task>();
    private Context mCtx;

    public TaskListAdapter(Context context, int resource, List<Task> tasks){
        super(context, resource, tasks);
        Tasks = tasks;
        mCtx = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null) {
            layoutInflater = LayoutInflater.from(mCtx);

            view = layoutInflater.inflate(R.layout.tasklist_item, parent, false);
        }

        Task currentTask = Tasks.get(position);

        TextView taskListName = (TextView) view.findViewById(R.id.txtTaskListName);
        taskListName.setText(currentTask.getTitle());
        TextView taskListDate = (TextView) view.findViewById(R.id.txtTaskListDate);
        taskListDate.setText(currentTask.getDate());
        TextView taskListTime = (TextView) view.findViewById(R.id.txtTaskListTime);
        taskListTime.setText(currentTask.getTime());

        return view;
    }
}
