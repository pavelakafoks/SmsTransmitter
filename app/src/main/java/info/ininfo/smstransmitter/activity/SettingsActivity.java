package info.ininfo.smstransmitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import info.ininfo.smstransmitter.helpers.DbHelper;
import info.ininfo.smstransmitter.models.EnumLogType;
import info.ininfo.smstransmitter.R;
import info.ininfo.smstransmitter.service.ServiceSmsTransmitter;
import info.ininfo.smstransmitter.models.Settings;

public class SettingsActivity extends AppCompatActivity {

    private Settings _settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner spinner = (Spinner) findViewById(R.id.frequency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        _settings = new Settings(this);
        initFromSettings();

        // switch change
        Switch switchSendAutomatically = (Switch)  findViewById(R.id.switchSendAutomatically);
        onSwitchSendAutomaticallyChange(switchSendAutomatically.isChecked());
        switchSendAutomatically.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitchSendAutomaticallyChange(isChecked);
            }
        });
    }

    public void onButtonClickCancel(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onSwitchSendAutomaticallyChange(boolean isChecked){
        TextView frequencyLabel = (TextView) findViewById(R.id.frequencyLabel);
        int visible = frequencyLabel.VISIBLE;
        if (!isChecked){
            visible = frequencyLabel.INVISIBLE;
        }
        frequencyLabel.setVisibility(visible);

        Spinner frequency = (Spinner) findViewById(R.id.frequency);
        frequency.setVisibility(visible);

        Switch switchBatterySaveMode = (Switch) findViewById(R.id.switchBatterySaveMode);
        switchBatterySaveMode.setVisibility(visible);
    }

    public void initFromSettings(){
        TextView key = (TextView) findViewById(R.id.key);
        key.setText(_settings.GetKey());

        TextView urlGateway = (TextView) findViewById(R.id.urlGateway);
        urlGateway.setText(_settings.GetUrlGateway());

        Switch switchSendAutomatically = (Switch) findViewById(R.id.switchSendAutomatically);
        switchSendAutomatically.setChecked(_settings.GetSwitchSendAutomatically());

        Spinner frequency = (Spinner) findViewById(R.id.frequency);
        int frequencyValue = _settings.GetFrequency();
        if (frequencyValue == 15){
            frequency.setSelection(0);
        }else if (frequencyValue == 20){
            frequency.setSelection(1);
        }else if (frequencyValue == 30){
            frequency.setSelection(2);
        }else{
            frequency.setSelection(3);
        }

        Switch switchBatterySaveMode = (Switch) findViewById(R.id.switchBatterySaveMode);
        switchBatterySaveMode.setChecked(_settings.GetSwitchBatterySaveMode());
    }

    public void onButtonClickSave(View view) {
        TextView key = (TextView) findViewById(R.id.key);
        String keyText = key.getText().toString();
        boolean keyIsEmpty = false;

        if (keyText == null || keyText.isEmpty()) {
            Toast.makeText(this, this.getString(R.string.settings_error_empty_key), Toast.LENGTH_LONG).show();
            new DbHelper(this).LogInsert(R.string.settings_error_empty_key, EnumLogType.Error);
            keyIsEmpty = true;
        }else{
            _settings.SetKey(keyText);
        }


        TextView urlGateway = (TextView) findViewById(R.id.urlGateway);
        String urlGatewayText = urlGateway.getText().toString();
        boolean urlGatewayIsEmpty = false;
        if (urlGatewayText == null || urlGatewayText.isEmpty()) {
            Toast.makeText(this, this.getString(R.string.settings_error_empty_gateway), Toast.LENGTH_LONG).show();
            new DbHelper(this).LogInsert(R.string.settings_error_empty_gateway, EnumLogType.Error);
            urlGatewayIsEmpty = true;
        }else{
            _settings.SetUrlGateway(urlGatewayText);
        }


        Switch switchSendAutomatically = (Switch) findViewById(R.id.switchSendAutomatically);
        _settings.SetSwitchSendAutomatically(switchSendAutomatically.isChecked());

        Spinner frequency = (Spinner) findViewById(R.id.frequency);
        int frequencyMinutes;
        int frequencyIndex = frequency.getSelectedItemPosition();
        if (frequencyIndex == 0){
            frequencyMinutes = 15;
        }else if (frequencyIndex == 1){
            frequencyMinutes = 20;
        }else if (frequencyIndex == 2){
            frequencyMinutes = 30;
        }else{
            frequencyMinutes = 60;
        }
        _settings.SetFrequency(frequencyMinutes);

        Switch switchBatterySaveMode = (Switch) findViewById(R.id.switchBatterySaveMode);
        _settings.SetSwitchBatterySaveMode(switchBatterySaveMode.isChecked());


        if (!keyIsEmpty && !urlGatewayIsEmpty) {
            ServiceSmsTransmitter.stopService(this);
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        findViewById( R.id.keyWrapper ).requestFocus();
    }


}
