package com.example.smartpantry;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class Db extends SQLiteOpenHelper {
    public Db(Context c) { super(c, "pantry.db", null, 1); }

    @Override public void onCreate(SQLiteDatabase d) {
        d.execSQL("CREATE TABLE pantry(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, qty REAL, unit TEXT, expiry TEXT)");
        d.execSQL("CREATE TABLE recipe(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, steps TEXT)");
        d.execSQL("CREATE TABLE ingredient(id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER, name TEXT, qty REAL, unit TEXT)");
        seed(d);
    }
    @Override public void onUpgrade(SQLiteDatabase d, int o, int n) {}

    private void add(SQLiteDatabase d, String name, String steps, String ings) {
        ContentValues v = new ContentValues(); v.put("name", name); v.put("steps", steps);
        long id = d.insert("recipe", null, v);
        for (String s : ings.split(";")) {
            String[] p = s.split(":");
            ContentValues i = new ContentValues(); i.put("recipe_id", id); i.put("name", p[0]);
            i.put("qty", Double.parseDouble(p[1])); i.put("unit", p[2]);
            d.insert("ingredient", null, i);
        }
    }
    private void seed(SQLiteDatabase d) {
        add(d, "Scrambled Eggs", "Whisk eggs with salt.|Melt butter in a pan.|Stir eggs gently until just set.", "egg:2:pcs;butter:10:g;salt:1:tsp");
        add(d, "Cheese Omelette", "Beat eggs.|Cook in buttered pan.|Add cheese, fold and serve.", "egg:3:pcs;cheese:50:g;butter:10:g");
        add(d, "Tomato Pasta", "Boil pasta.|Fry garlic in oil, add chopped tomatoes and salt.|Toss pasta in the sauce.", "pasta:200:g;tomato:4:pcs;garlic:2:pcs;olive oil:2:tbsp;salt:1:tsp");
        add(d, "Fried Rice", "Fry onion in oil.|Scramble in the eggs.|Add cooked rice and soy sauce, stir-fry.", "rice:200:g;egg:2:pcs;onion:1:pcs;soy sauce:2:tbsp;oil:1:tbsp");
        add(d, "Pancakes", "Mix flour, sugar, milk and egg into a batter.|Melt butter in a pan.|Fry ladles of batter until golden both sides.", "flour:200:g;milk:300:ml;egg:1:pcs;sugar:2:tbsp;butter:20:g");
        add(d, "Garlic Bread", "Mash butter with crushed garlic.|Spread on bread.|Bake at 200C for 8 minutes.", "bread:4:pcs;butter:50:g;garlic:2:pcs");
        add(d, "Cheese Toastie", "Butter the bread.|Fill with cheese.|Toast in a pan until golden.", "bread:2:pcs;cheese:60:g;butter:10:g");
        add(d, "Mashed Potato", "Boil peeled potatoes until soft.|Drain and mash.|Mix in butter, milk and salt.", "potato:500:g;butter:30:g;milk:100:ml;salt:1:tsp");
        add(d, "Vegetable Soup", "Chop vegetables.|Simmer in stock for 25 minutes.|Season with salt and serve.", "carrot:2:pcs;potato:2:pcs;onion:1:pcs;stock:500:ml;salt:1:tsp");
        add(d, "Chicken and Rice", "Brown chicken with onion.|Add rice, salt and water.|Cover and cook for 20 minutes.", "chicken:300:g;rice:200:g;onion:1:pcs;salt:1:tsp");
        add(d, "Guacamole", "Mash avocados.|Stir in chopped tomato and onion.|Add lemon juice and salt.", "avocado:2:pcs;tomato:1:pcs;onion:1:pcs;lemon:1:pcs;salt:1:tsp");
        add(d, "Banana Pancakes", "Mash bananas.|Mix in eggs and flour.|Fry small pancakes.", "banana:2:pcs;egg:2:pcs;flour:50:g");
        add(d, "Bean Chili", "Fry onion and garlic.|Add tomatoes, beans and chili powder.|Simmer for 20 minutes.", "beans:400:g;tomato:3:pcs;onion:1:pcs;chili powder:1:tsp;garlic:2:pcs");
        add(d, "Mac and Cheese", "Boil pasta.|Make a sauce with butter, flour and milk.|Stir in cheese and pasta.", "pasta:250:g;cheese:150:g;milk:250:ml;butter:30:g;flour:2:tbsp");
        add(d, "Tuna Salad", "Flake tuna.|Mix with mayonnaise and chopped onion.|Serve on lettuce.", "tuna:1:pcs;mayonnaise:2:tbsp;onion:1:pcs;lettuce:1:pcs");
        add(d, "Banana Oatmeal", "Heat oats with milk until thick.|Slice banana on top.|Sprinkle sugar.", "oats:80:g;milk:250:ml;sugar:1:tsp;banana:1:pcs");
        add(d, "French Toast", "Whisk eggs, milk and sugar.|Soak bread slices.|Fry in butter until golden.", "bread:4:pcs;egg:2:pcs;milk:100:ml;sugar:1:tbsp;butter:10:g");
        add(d, "Egg Fried Noodles", "Boil noodles and drain.|Scramble eggs in oil.|Toss noodles with soy sauce.", "noodle:200:g;egg:2:pcs;soy sauce:2:tbsp;oil:1:tbsp");
    }

    // ---- Pantry CRUD ----
    public void save(PantryItem p) {
        ContentValues v = new ContentValues();
        v.put("name", p.name); v.put("qty", p.qty); v.put("unit", p.unit); v.put("expiry", p.expiry);
        if (p.id == 0) getWritableDatabase().insert("pantry", null, v);
        else getWritableDatabase().update("pantry", v, "id=?", new String[]{"" + p.id});
    }
    public void delete(long id) { getWritableDatabase().delete("pantry", "id=?", new String[]{"" + id}); }
    private PantryItem row(Cursor c) {
        PantryItem p = new PantryItem();
        p.id = c.getLong(0); p.name = c.getString(1); p.qty = c.getDouble(2); p.unit = c.getString(3); p.expiry = c.getString(4);
        return p;
    }
    public List<PantryItem> pantry() {
        List<PantryItem> l = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT id,name,qty,unit,expiry FROM pantry ORDER BY name COLLATE NOCASE", null);
        while (c.moveToNext()) l.add(row(c));
        c.close(); return l;
    }
    public PantryItem pantryItem(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT id,name,qty,unit,expiry FROM pantry WHERE id=?", new String[]{"" + id});
        PantryItem p = c.moveToFirst() ? row(c) : null; c.close(); return p;
    }

    // ---- Recipes ----
    public List<Recipe> recipes() {
        List<Recipe> l = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT id FROM recipe ORDER BY name", null);
        while (c.moveToNext()) l.add(recipe(c.getLong(0)));
        c.close(); return l;
    }
    public Recipe recipe(long id) {
        SQLiteDatabase d = getReadableDatabase();
        Cursor c = d.rawQuery("SELECT id,name,steps FROM recipe WHERE id=?", new String[]{"" + id});
        if (!c.moveToFirst()) { c.close(); return null; }
        Recipe r = new Recipe(); r.id = c.getLong(0); r.name = c.getString(1); r.steps = c.getString(2); c.close();
        c = d.rawQuery("SELECT name,qty,unit FROM ingredient WHERE recipe_id=?", new String[]{"" + id});
        while (c.moveToNext()) {
            Recipe.Ing i = new Recipe.Ing(); i.name = c.getString(0); i.qty = c.getDouble(1); i.unit = c.getString(2); r.ings.add(i);
        }
        c.close(); return r;
    }

    /** "Cook it": deducts ingredients from the pantry, soonest-expiring stock first. Returns how many were soon-to-expire (rescued). */
    public int cook(Recipe r) {
        List<PantryItem> items = pantry();
        int rescued = 0;
        for (Recipe.Ing ing : r.ings) {
            String k = Matcher.key(ing.name, ing.unit);
            double need = Matcher.base(ing.qty, ing.unit);
            List<PantryItem> m = new ArrayList<>();
            for (PantryItem p : items) if (Matcher.key(p.name, p.unit).equals(k)) m.add(p);
            Collections.sort(m, (a, b) -> Long.compare(Matcher.daysLeft(a.expiry), Matcher.daysLeft(b.expiry)));
            boolean counted = false;
            for (PantryItem p : m) {
                if (need <= 1e-9) break;
                double avail = Matcher.base(p.qty, p.unit), take = Math.min(avail, need), left = avail - take;
                if (!counted && Matcher.daysLeft(p.expiry) <= Matcher.SOON_DAYS) { rescued++; counted = true; }
                need -= take;
                if (left <= 1e-9) delete(p.id); else { p.qty = left / Matcher.base(1, p.unit); save(p); }
            }
        }
        return rescued;
    }
}
