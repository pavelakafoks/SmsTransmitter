package info.ininfo.smstransmitter.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.List;

import info.ininfo.smstransmitter.App;
import info.ininfo.smstransmitter.R;
import info.ininfo.smstransmitter.adapters.MessageRecyclerViewAdapter;
import info.ininfo.smstransmitter.engine.SmsWorker;
import info.ininfo.smstransmitter.helpers.DbHelper;
import info.ininfo.smstransmitter.models.EnumLogType;
import info.ininfo.smstransmitter.models.Message;
import info.ininfo.smstransmitter.models.Settings;
import info.ininfo.smstransmitter.service.ServiceSmsTransmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private DbHelper db;

    private FloatingActionButton refreshButton;
    private Snackbar snackBar;
    private RecyclerView messagesRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isAlarmSenderRunning;
    private boolean isManualSenderRunning;

    private Disposable workingStatusSubscription;
    private Disposable manualSenderStatusSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHelper(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshButton = (FloatingActionButton) findViewById(R.id.fab);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runManually();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMessages();
            }
        });

        messagesRecyclerView = (RecyclerView) this.findViewById(R.id.listMessages);
        messagesRecyclerView.setNestedScrollingEnabled(false);    // turn on inertia scroll
        loadMessages();

        if (Build.VERSION.SDK_INT >= 23) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }


        workingStatusSubscription = getAlarmSmsWorker().workingStatusPublisher
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        isAlarmSenderRunning = aBoolean;
                        invalidateRunningAnimation();
                    }
                });

        ServiceSmsTransmitter.startService(this);
        ((App) getApplication()).checkWorker(false);


        // request permissions  (https://developer.android.com/training/permissions/requesting.html)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    0);
        }


        //} catch (Exception e) {
        //    db.LogInsert(e);
        //    db.LogInsert(R.string.log_error, EnumLogType.Error);
        //}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_log) {
            startActivity(new Intent(this, LogActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void invalidateRunningAnimation() {
        boolean isRunning = isAlarmSenderRunning || isManualSenderRunning;
        if (isRunning) {
            Animation rotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.android_rotate_animation);
            refreshButton.startAnimation(rotateAnimation);
            snackBar = Snackbar.make(refreshButton, R.string.title_getting_data, Snackbar.LENGTH_INDEFINITE);
            snackBar.show();
        } else {
            refreshButton.clearAnimation();
            if (snackBar != null) {
                snackBar.dismiss();
                snackBar = null;
            }
        }
    }

    public void runManually() {
        Settings settings = new Settings(this);
        if (settings.GetKey().isEmpty()) {
            Toast.makeText(this, this.getString(R.string.settings_error_empty_key), Toast.LENGTH_LONG).show();
            new DbHelper(this).LogInsert(R.string.settings_error_empty_key, EnumLogType.Error);
        } else {
            if (!isAlarmSenderRunning && !isManualSenderRunning) {
                SmsWorker worker = new SmsWorker(this, false);
                if (manualSenderStatusSubscription != null) {
                    manualSenderStatusSubscription.dispose();
                    manualSenderStatusSubscription = null;
                }
                manualSenderStatusSubscription = worker.workingStatusPublisher
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) {
                                isManualSenderRunning = aBoolean;
                                invalidateRunningAnimation();
                                if (!isManualSenderRunning) {
                                    loadMessages();
                                }
                            }
                        });
                new ManuallySenderTask().execute(worker);
            }
        }
    }

    public void loadMessages() {
        List<Message> messages = db.MessageGetAll();
        MessageRecyclerViewAdapter messageAdapter = new MessageRecyclerViewAdapter(messages);
        messagesRecyclerView.setAdapter(messageAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private App getApp() {
        return (App) getApplication();
    }

    private SmsWorker getAlarmSmsWorker() {
        return getApp().getAlarmSmsWorker();
    }

    @Override
    protected void onDestroy() {
        if (workingStatusSubscription != null) {
            workingStatusSubscription.dispose();
            workingStatusSubscription = null;
        }

        if (manualSenderStatusSubscription != null) {
            manualSenderStatusSubscription.dispose();
            manualSenderStatusSubscription = null;
        }
        super.onDestroy();
    }

    private static class ManuallySenderTask extends AsyncTask<SmsWorker, Void, String> {
        @Override
        protected String doInBackground(SmsWorker... workers) {
            workers[0].Process();
            return "";
        }
    }
}
