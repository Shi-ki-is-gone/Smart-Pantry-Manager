package com.example.smartpantry;
import java.text.SimpleDateFormat;
import java.util.*;

/** Strict matching plus "rescue score" ranking: recipes that use soon-to-expire items rise to the top. */
public class Matcher {
    public static final int SOON_DAYS = 3;

    /** Result of testing one recipe against the pantry. */
    public static class Match {
        public Recipe recipe;
        public int rescue;                                   // ingredients that are expiring soon in the pantry
        public List<Recipe.Ing> missing = new ArrayList<>(); // absent OR insufficient ingredients
    }

    /** Lowercase, trim, simple singularisation: tomatoes->tomato, berries->berry, eggs->egg. */
    public static String norm(String s) {
        if (s == null) return "";
        s = s.toLowerCase(Locale.US).trim().replaceAll("[\\s_]+", " ");
        s = s.replaceAll("[-/]", " ").replaceAll("[.,]", "");
        if (s.endsWith("ies") && s.length() > 4) return s.substring(0, s.length() - 3) + "y";
        if (s.matches(".*(oes|ches|shes|xes)$")) return s.substring(0, s.length() - 2);
        if (s.endsWith("s") && !s.endsWith("ss") && !s.endsWith("us") && s.length() > 3) return s.substring(0, s.length() - 1);
        return s.trim();
    }

    /** Normalises common unit spellings and missing values to a consistent internal form. */
    static String normUnit(String u) {
        if (u == null) return "pcs";
        String v = u.toLowerCase(Locale.US).trim().replaceAll("\\s+", "");
        if (v.isEmpty()) return "pcs";
        switch (v) {
            case "g": case "gram": case "grams": case "gr": return "g";
            case "kg": case "kilogram": case "kilograms": case "kilo": case "kilos": return "kg";
            case "ml": case "milliliter": case "millilitre": case "milliliters": case "millilitres": return "ml";
            case "l": case "liter": case "litre": case "liters": case "litres": return "l";
            case "tsp": case "teaspoon": case "teaspoons": return "tsp";
            case "tbsp": case "tablespoon": case "tablespoons": return "tbsp";
            case "cup": case "cups": return "cup";
            case "oz": case "ounce": case "ounces": return "oz";
            case "lb": case "lbs": case "pound": case "pounds": return "lb";
            case "pc": case "pcs": case "piece": case "pieces": case "item": case "items": return "pcs";
            default: return v;
        }
    }
    /** w = weight, v = volume, c = count. Dimensions are never mixed. */
    static String dim(String u) {
        switch (normUnit(u)) { case "g": case "kg": case "oz": case "lb": return "w";
            case "ml": case "l": case "tsp": case "tbsp": case "cup": return "v";
            default: return "c"; }
    }
    /** Converts to base unit: grams, millilitres or count. */
    static double base(double q, String u) {
        switch (normUnit(u)) {
            case "kg": return q * 1000;
            case "g": return q;
            case "l": return q * 1000;
            case "ml": return q;
            case "tsp": return q * 5;
            case "tbsp": return q * 15;
            case "cup": return q * 240;
            case "oz": return q * 28.35;
            case "lb": return q * 453.592;
            default: return q;
        }
    }
    static String key(String name, String unit) { return norm(name) + "|" + dim(unit); }

    /** Days until expiry; Long.MAX_VALUE if no/invalid date. Negative = already expired. */
    public static long daysLeft(String expiry) {
        if (expiry == null || expiry.isEmpty()) return Long.MAX_VALUE;
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expiry);
            return (long) Math.floor((d.getTime() - System.currentTimeMillis()) / 86400000.0);
        } catch (Exception e) { return Long.MAX_VALUE; }
    }

    public static List<Match> evaluateAll(List<PantryItem> pantry, List<Recipe> recipes) {
        Map<String, Double> have = new HashMap<>();
        Set<String> soon = new HashSet<>();
        for (PantryItem p : pantry) {
            String k = key(p.name, p.unit);
            Double cur = have.get(k);
            have.put(k, (cur == null ? 0 : cur) + base(p.qty, p.unit)); // duplicates are summed
            if (daysLeft(p.expiry) <= SOON_DAYS) soon.add(k);
        }
        List<Match> out = new ArrayList<>();
        for (Recipe r : recipes) {
            Match m = new Match(); m.recipe = r;
            for (Recipe.Ing i : r.ings) {
                String k = key(i.name, i.unit);
                Double h = have.get(k);
                if (h == null || h + 1e-9 < base(i.qty, i.unit)) m.missing.add(i);
                else if (soon.contains(k)) m.rescue++;
            }
            out.add(m);
        }
        return out;
    }

    /** STRICT list: zero missing ingredients, ranked by rescue score (most waste prevented first). */
    public static List<Match> suggest(List<PantryItem> pantry, List<Recipe> recipes) {
        List<Match> out = new ArrayList<>();
        for (Match m : evaluateAll(pantry, recipes)) if (m.missing.isEmpty() && !m.recipe.ings.isEmpty()) out.add(m);
        Collections.sort(out, (a, b) -> b.rescue != a.rescue ? b.rescue - a.rescue : a.recipe.name.compareTo(b.recipe.name));
        return out;
    }
    /** Bonus list: recipes missing exactly ONE ingredient. Never mixed into the strict list. */
    public static List<Match> almostThere(List<PantryItem> pantry, List<Recipe> recipes) {
        List<Match> out = new ArrayList<>();
        for (Match m : evaluateAll(pantry, recipes)) if (m.missing.size() == 1) out.add(m);
        return out;
    }
}
