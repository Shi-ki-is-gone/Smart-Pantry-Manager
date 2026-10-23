package com.example.smartpantry;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.*;
import java.util.List;

public class MainActivity extends BaseActivity {
    private Db db; private RecyclerView rv; private TextView empty;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_list);
        setTitle("My Pantry");
        db = new Db(this);
        rv = findViewById(R.id.rv); empty = findViewById(R.id.empty);
        rv.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.btnAdd).setOnClickListener(v -> startActivity(new Intent(this, EditItemActivity.class)));
    }
    @Override protected void onResume() { super.onResume(); load(); }

    private void load() {
        List<PantryItem> items = db.pantry();
        android.content.SharedPreferences sp = getSharedPreferences("prefs", MODE_PRIVATE);
        ((TextView) findViewById(R.id.stats)).setText("\uD83C\uDF7D Meals cooked: " + sp.getInt("cooked", 0)
            + "   \u267B Items rescued from expiry: " + sp.getInt("rescued", 0));
        empty.setText("Your pantry is empty - add some ingredients.");
        empty.setVisibility(items.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
        boolean hl = getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("alerts", true);
        rv.setAdapter(new PantryAdapter(items, hl, new PantryAdapter.Listener() {
            public void onClick(PantryItem p) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("id", p.id); startActivity(i);
            }
            public void onLongClick(PantryItem p) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Delete " + p.name + "?")
                    .setPositiveButton("Delete", (d, w) -> { db.delete(p.id); load(); })
                    .setNegativeButton("Cancel", null).show();
            }
        }));
    }
}
