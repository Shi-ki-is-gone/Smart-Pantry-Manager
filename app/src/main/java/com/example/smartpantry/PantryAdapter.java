package com.example.smartpantry;
import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.*;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {
    public interface Listener { void onClick(PantryItem p); void onLongClick(PantryItem p); }
    private final List<PantryItem> items; private final Listener l; private final boolean highlight;

    public PantryAdapter(List<PantryItem> items, boolean highlight, Listener l) { this.items = items; this.highlight = highlight; this.l = l; }

    static class VH extends RecyclerView.ViewHolder {
        TextView t, s;
        VH(View v) { super(v); t = v.findViewById(R.id.title); s = v.findViewById(R.id.sub); }
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int type) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_row, p, false));
    }
    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        PantryItem p = items.get(pos);
        String q = (p.qty == Math.floor(p.qty)) ? "" + (long) p.qty : "" + p.qty;
        h.t.setText(p.name);
        String sub = q + " " + p.unit;
        int color = Color.DKGRAY;
        if (p.expiry != null && !p.expiry.isEmpty()) {
            sub += "  |  expires " + p.expiry;
            if (highlight) try {
                Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(p.expiry);
                long days = (d.getTime() - System.currentTimeMillis()) / 86400000L;
                if (days <= 3) { color = Color.RED; sub += " (soon!)"; }
            } catch (Exception ignored) {}
        }
        h.s.setText(sub); h.s.setTextColor(color);
        h.itemView.setOnClickListener(v -> l.onClick(p));
        h.itemView.setOnLongClickListener(v -> { l.onLongClick(p); return true; });
    }
    @Override public int getItemCount() { return items.size(); }
}
