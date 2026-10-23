package com.example.smartpantry;
import android.os.Bundle;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class EditItemActivity extends BaseActivity {
    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"};
    private Db db; private PantryItem item = new PantryItem();
    private EditText etName, etQty, etExpiry; private Spinner sp;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_edit);
        db = new Db(this);
        etName = findViewById(R.id.etName); etQty = findViewById(R.id.etQty); etExpiry = findViewById(R.id.etExpiry);
        sp = findViewById(R.id.spUnit);
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));

        long id = getIntent().getLongExtra("id", 0);
        setTitle(id == 0 ? "Add Ingredient" : "Edit Ingredient");
        item = db.pantryItem(id);
        if (item == null) {
            item = new PantryItem();
            sp.setSelection(0);
        } else {
            etName.setText(item.name); etQty.setText("" + item.qty); etExpiry.setText(item.expiry);
            int unitIndex = Arrays.asList(UNITS).indexOf(item.unit);
            sp.setSelection(unitIndex >= 0 ? unitIndex : 0);
            Button del = findViewById(R.id.btnDelete);
            del.setVisibility(android.view.View.VISIBLE);
            del.setOnClickListener(v -> { db.delete(item.id); finish(); });
        }
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String qs = etQty.getText().toString().trim();
        String exp = etExpiry.getText().toString().trim();
        if (name.isEmpty()) { etName.setError("Name is required"); return; }
        if (qs.isEmpty()) { etQty.setError("Quantity is required"); return; }
        double q;
        try { q = Double.parseDouble(qs); } catch (Exception e) { etQty.setError("Enter a valid number"); return; }
        if (q <= 0) { etQty.setError("Quantity must be greater than 0"); return; }
        if (!exp.isEmpty()) {
            try { SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US); f.setLenient(false); f.parse(exp); }
            catch (Exception e) { etExpiry.setError("Use format yyyy-MM-dd"); return; }
        }
        int selected = Math.max(0, sp.getSelectedItemPosition());
        item.name = name; item.qty = q; item.unit = UNITS[selected]; item.expiry = exp;
        db.save(item);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
