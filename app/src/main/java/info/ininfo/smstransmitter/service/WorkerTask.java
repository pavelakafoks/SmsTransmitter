package info.ininfo.smstransmitter.service;

import android.content.Context;
import android.os.AsyncTask;

import info.ininfo.smstransmitter.engine.SmsWorker;
import info.ininfo.smstransmitter.activity.MainActivity;


public class WorkerTask extends AsyncTask<String, Void, String> {

    private MainActivity _mainActivity;
    private SmsWorker _worker;

    public WorkerTask(Context context, MainActivity mainActivity, boolean isAlarm, boolean batterySaveMode, String key) {
        _worker = new SmsWorker(context, isAlarm, batterySaveMode, key);
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
