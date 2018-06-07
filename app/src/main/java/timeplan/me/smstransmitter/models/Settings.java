package timeplan.me.smstransmitter.models;

import android.content.Context;
import android.content.SharedPreferences;

import timeplan.me.smstransmitter.R;

public class Settings {

    public static boolean IsDemo = false;

    private SharedPreferences _preferences;
    private Context _context;

    public  Settings(Context context){
        _preferences = context.getSharedPreferences("settingsOfSmsTransmitter", Context.MODE_PRIVATE);
        _context = context;
    }

    public String GetKey(){
        if (IsDemo) return _context.getString(R.string.demo_settings_key);

        return _preferences.getString("key", "");
    }

    public void SetKey(String value){
        SharedPreferences.Editor editor = _preferences.edit();
        editor.putString("key", value.toUpperCase());
        editor.commit();
    }

    public String GetUrlGateway(){
        if (IsDemo) return _context.getString(R.string.settings_url_gateway_default);

        return _preferences.getString("urlGateway", _context.getString(R.string.settings_url_gateway_default));
    }

    public void SetUrlGateway(String value){
        SharedPreferences.Editor editor = _preferences.edit();
        editor.putString("urlGateway", value.toLowerCase());
        editor.commit();
    }

    public boolean GetSwitchSendAutomatically(){
        if (GetKey() == null || GetKey().isEmpty()) return false;
        return _preferences.getBoolean("switchSendAutomatically", true);
    }

    public void SetSwitchSendAutomatically(boolean value){
        SharedPreferences.Editor editor = _preferences.edit();
        editor.putBoolean("switchSendAutomatically", value);
        editor.commit();
    }

    public int GetFrequency(){
        return _preferences.getInt("frequency", 30);
    }

    public void SetFrequency(int value){
        SharedPreferences.Editor editor = _preferences.edit();
        editor.putInt("frequency", value);
        editor.commit();
    }

    public boolean GetSwitchBatterySaveMode(){
        return _preferences.getBoolean("switchBatterySaveMode", true);
    }

    public void SetSwitchBatterySaveMode(boolean value){
        SharedPreferences.Editor editor = _preferences.edit();
        editor.putBoolean("switchBatterySaveMode", value);
        editor.commit();
    }

}
