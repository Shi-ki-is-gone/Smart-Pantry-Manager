package com.example.smartpantry;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.*;
import java.util.List;

public class SuggestedActivity extends BaseActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_suggested);
        setTitle("Suggested Recipes");
        Db db = new Db(this);
        List<PantryItem> pantry = db.pantry(); List<Recipe> all = db.recipes();
        List<Matcher.Match> strict = Matcher.suggest(pantry, all), almost = Matcher.almostThere(pantry, all);

        android.widget.TextView empty = findViewById(R.id.empty);
        empty.setText("No recipes match your pantry yet - add more ingredients");
        empty.setVisibility(strict.isEmpty() ? View.VISIBLE : View.GONE);
        findViewById(R.id.emptyAlmost).setVisibility(almost.isEmpty() ? View.VISIBLE : View.GONE);
        setup((RecyclerView) findViewById(R.id.rv), strict, false);
        setup((RecyclerView) findViewById(R.id.rv2), almost, true);
    }
    private void setup(RecyclerView rv, List<Matcher.Match> list, boolean almost) {
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RecipeAdapter(list, almost, r -> {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra("id", r.id); startActivity(i);
        }));
    }
}
