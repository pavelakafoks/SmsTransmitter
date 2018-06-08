package info.ininfo.smstransmitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import info.ininfo.smstransmitter.helpers.DbHelper;
import info.ininfo.smstransmitter.adapters.LogRecyclerViewAdapter;
import info.ininfo.smstransmitter.models.MessagesAmount;
import info.ininfo.smstransmitter.R;
import info.ininfo.smstransmitter.models.Log;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DbHelper db = new DbHelper(this);
        List<Log> logs = db.LogGetAll();
        LogRecyclerViewAdapter logAdapter = new LogRecyclerViewAdapter(logs);
        RecyclerView mRecycler = (RecyclerView) this.findViewById(R.id.listLogs);
        mRecycler.setAdapter(logAdapter);

        // turn on inertia scroll
        mRecycler.setNestedScrollingEnabled(false);

        // show statistics
        MessagesAmount messagesAmount = db.MessageGetAmount();
        TextView log_amount7Day = (TextView) findViewById(R.id.log_amount7Day);
        log_amount7Day.setText(this.getString(R.string.log_amount7Day) + " " +  messagesAmount.Amount7Day);
        TextView log_amount30Day = (TextView) findViewById(R.id.log_amount30Day);
        log_amount30Day.setText(this.getString(R.string.log_amount30Day) + " " + messagesAmount.Amount30Day);

        // refresh on pull down
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });
        //swipeRefreshLayout.setRefreshing(false);  // for Disable ?
    }

    public void Refresh(){
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_main) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}