package avappmobile.mytasks.Objects;

/**
 * Created by Alexandre on 01/03/2015.
 * Reminder object
 */

public class Reminder {

    private int _id;
    private String _description;
    private int _active;
    private int _hour;
    private int _minute;
    private int _duration;

    public Reminder (int id, String description, int active, int hour, int minute, int duration) {
        this._id = id;
        this._description = description;
        this._active = active;
        this._hour = hour;
        this._minute = minute;
        this._duration = duration;
    }

    public int getId() { return _id; }

    public String getDescription() { return _description; }
    public void setDescription(String description) { this._description = description; }

    public int getActive() { return _active; }
    public void setActive(int active) { this._active = active; }

    public int getHour() { return _hour; }
    public void setHour(int hour) { this._hour = hour; }

    public int getMinute() { return _minute; }
    public void setMinute(int minute) { _minute = minute; }

    public int getDuration() { return _duration; }
    public void setDuration(int duration) { this._duration = duration; }
}
