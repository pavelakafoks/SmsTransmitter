package info.ininfo.smstransmitter.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import info.ininfo.smstransmitter.service.WorkerTask;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton refreshButton;
    //private WorkerTask receiverTask;
    //private AlarmManager alarmMgr;
    //private PendingIntent alarmIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DbHelper db = new DbHelper(this);
        //try{
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshButton = (FloatingActionButton) findViewById(R.id.fab);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshClick(view);
            }
        });


        // start bind Messages List
        List<Message> messages = db.MessageGetAll();
        MessageRecyclerViewAdapter messageAdapter = new MessageRecyclerViewAdapter(messages);
        RecyclerView mRecycler = (RecyclerView) this.findViewById(R.id.listMessages);
        mRecycler.setAdapter(messageAdapter);
        // end Messages bind

        mRecycler.setNestedScrollingEnabled(false);    // turn on inertia scroll

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


        //noinspection AccessStaticViaInstance
        if (SmsWorker.isWorking()) {
            InProcess(refreshButton);
        }

        // refresh on pull down
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });
        //swipeRefreshLayout.setRefreshing(false);  // for Disable ?


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

    public Snackbar InProcess(View view) {
        Snackbar snackBar;
        Animation rotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.android_rotate_animation);
        refreshButton.startAnimation(rotateAnimation);
        snackBar = Snackbar.make(view, R.string.title_getting_data, Snackbar.LENGTH_INDEFINITE);
        snackBar.show();
        return snackBar;
    }

    public void onRefreshClick(View view) {
        Settings settings = new Settings(this);
        if (settings.GetKey().isEmpty()) {
            Toast.makeText(this, this.getString(R.string.settings_error_empty_key), Toast.LENGTH_LONG).show();
            new DbHelper(this).LogInsert(R.string.settings_error_empty_key, EnumLogType.Error);
        } else {
            if (!SmsWorker.isWorking()) {
                InProcess(view);
                WorkerTask workerTask = new WorkerTask(this, this, false, true, settings.GetKey());
                workerTask.execute();
            }
        }
    }

    public void Refresh() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
