package info.ininfo.smstransmitter.service;

import android.os.AsyncTask;

import info.ininfo.smstransmitter.activity.MainActivity;
import info.ininfo.smstransmitter.engine.SmsWorker;


public class WorkerTask extends AsyncTask<String, Void, String> {

    private MainActivity _mainActivity;
    private SmsWorker _worker;

    public WorkerTask(MainActivity mainActivity, SmsWorker worker) {
        _worker = worker;
        _mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... parameters) {
        _worker.Process();
        return "";
    }

    protected void onPostExecute(String feed) {
        if (_mainActivity != null) {
            _mainActivity.loadMessages();
        }
    }
}
