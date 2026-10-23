# Larder Rescue (Java, SQLite)
Open in Android Studio, sync Gradle, run on API 24+.

Unique features beyond the brief:
- Rescue Score: suggestions ranked by how many soon-to-expire (<=3 days) ingredients they use.
- "Almost There" list (bonus): recipes missing exactly one ingredient, shown in a separate section naming what is missing.
- Cook-it: deducts ingredients from the pantry (soonest-expiring stock first, with unit conversion) and updates the dashboard.
- Waste dashboard: meals cooked and items rescued from expiry.
- Per-recipe pantry check: each ingredient is marked in pantry / missing.
- Custom green/orange theme and app identity.

Core: Db.java (SQLiteOpenHelper, 18 seeded recipes), Matcher.java (strict matching, singular/plural, unit conversion).
No maps, location or GPS.
