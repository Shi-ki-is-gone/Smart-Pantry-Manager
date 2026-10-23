package com.example.smartpantry;
import android.content.Intent;
import android.view.*;
import androidx.appcompat.app.AppCompatActivity;

/** Provides the toolbar navigation menu to every screen. */
public abstract class BaseActivity extends AppCompatActivity {
    @Override public boolean onCreateOptionsMenu(Menu m) { getMenuInflater().inflate(R.menu.nav, m); return true; }
    @Override public boolean onOptionsItemSelected(MenuItem i) {
        int id = i.getItemId();
        if (id == R.id.nav_pantry) go(MainActivity.class);
        else if (id == R.id.nav_suggested) go(SuggestedActivity.class);
        else if (id == R.id.nav_settings) go(SettingsActivity.class);
        else return super.onOptionsItemSelected(i);
        return true;
    }
    private void go(Class<?> c) {
        if (getClass() == c) return;
        Intent in = new Intent(this, c);
        if (c == MainActivity.class) in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }
}
