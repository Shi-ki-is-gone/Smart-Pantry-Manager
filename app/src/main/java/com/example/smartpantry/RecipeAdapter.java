package com.example.smartpantry;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {
    public interface Listener { void onClick(Recipe r); }
    private final List<Matcher.Match> items; private final boolean almost; private final Listener l;
    public RecipeAdapter(List<Matcher.Match> items, boolean almost, Listener l) { this.items = items; this.almost = almost; this.l = l; }

    static class VH extends RecyclerView.ViewHolder {
        TextView t, s;
        VH(View v) { super(v); t = v.findViewById(R.id.title); s = v.findViewById(R.id.sub); }
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int type) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_row, p, false));
    }
    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Matcher.Match m = items.get(pos);
        h.t.setText(m.recipe.name);
        if (almost) {
            Recipe.Ing i = m.missing.get(0);
            h.s.setText("Missing: " + (i.qty == Math.floor(i.qty) ? "" + (long) i.qty : "" + i.qty) + " " + i.unit + " " + i.name);
            h.s.setTextColor(0xFF9A6B00);
        } else {
            h.s.setText(m.rescue > 0 ? "♻ Rescues " + m.rescue + " expiring ingredient" + (m.rescue > 1 ? "s" : "") + "!"
                                     : m.recipe.ings.size() + " ingredients - ready to cook");
            h.s.setTextColor(m.rescue > 0 ? 0xFFD35400 : 0xFF2E6B4F);
        }
        h.itemView.setOnClickListener(v -> l.onClick(m.recipe));
    }
    @Override public int getItemCount() { return items.size(); }
}
