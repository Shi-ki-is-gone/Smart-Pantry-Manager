package com.example.smartpantry;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import java.util.*;

public class RecipeDetailActivity extends BaseActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_detail);
        Db db = new Db(this);
        Recipe r = db.recipe(getIntent().getLongExtra("id", -1));
        if (r == null) { finish(); return; }
        setTitle("Recipe");
        ((TextView) findViewById(R.id.dTitle)).setText(r.name);

        Matcher.Match m = Matcher.evaluateAll(db.pantry(), Collections.singletonList(r)).get(0);
        StringBuilder sb = new StringBuilder("INGREDIENTS  (\u2713 in pantry, \u2717 missing)\n");
        for (Recipe.Ing i : r.ings) {
            boolean miss = m.missing.contains(i);
            String q = (i.qty == Math.floor(i.qty)) ? "" + (long) i.qty : "" + i.qty;
            sb.append(miss ? "\u2717 " : "\u2713 ").append(q).append(" ").append(i.unit).append(" ").append(i.name).append("\n");
        }
        sb.append("\nMETHOD\n");
        String[] steps = r.steps.split("\\|");
        for (int n = 0; n < steps.length; n++) sb.append(n + 1).append(". ").append(steps[n]).append("\n");
        ((TextView) findViewById(R.id.dBody)).setText(sb.toString());

        Button cook = findViewById(R.id.btnCook);
        cook.setOnClickListener(v -> {
            Matcher.Match now = Matcher.evaluateAll(db.pantry(), Collections.singletonList(r)).get(0);
            if (!now.missing.isEmpty()) { Toast.makeText(this, "You are missing ingredients for this recipe", Toast.LENGTH_LONG).show(); return; }
            int rescued = db.cook(r);
            SharedPreferences sp = getSharedPreferences("prefs", MODE_PRIVATE);
            sp.edit().putInt("cooked", sp.getInt("cooked", 0) + 1).putInt("rescued", sp.getInt("rescued", 0) + rescued).apply();
            Toast.makeText(this, "Enjoy! Pantry updated" + (rescued > 0 ? " - you rescued " + rescued + " expiring item(s)" : ""), Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
