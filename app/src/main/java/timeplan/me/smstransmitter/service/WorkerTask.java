package timeplan.me.smstransmitter.service;

import android.content.Context;
import android.os.AsyncTask;

import timeplan.me.smstransmitter.engine.Worker;
import timeplan.me.smstransmitter.activity.MainActivity;


public class WorkerTask extends AsyncTask<String, Void, String> {

    private MainActivity _mainActivity;
    private Worker _worker;

    public WorkerTask(Context context, MainActivity mainActivity, boolean isAlarm, boolean batterySaveMode, String key) {
        _worker = new Worker(context, isAlarm, batterySaveMode, key);
        _mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... parameters) {
        _worker.Process();
        return "";
    }

    protected void onPostExecute(String feed) {
        if(_mainActivity != null){
            _mainActivity.Refresh();
        }
    }
}
