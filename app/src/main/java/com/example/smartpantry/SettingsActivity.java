package com.example.smartpantry;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        SharedPreferences sp = getSharedPreferences("prefs", MODE_PRIVATE);
        SwitchCompat sw = findViewById(R.id.swAlerts);
        sw.setChecked(sp.getBoolean("alerts", true));
        sw.setOnCheckedChangeListener((v, on) -> sp.edit().putBoolean("alerts", on).apply());
    }
}
