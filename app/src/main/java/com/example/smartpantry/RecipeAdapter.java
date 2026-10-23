package com.example.smartpantry;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {
    private static final int COLOR_MISSING = 0xFFFFD54F;
    private static final int COLOR_RESCUE = 0xFFFF8A65;
    private static final int COLOR_READY = 0xFF81C784;

    public interface Listener {
        void onClick(Recipe recipe);
    }

    private final List<Matcher.Match> matches;
    private final boolean showMissingIngredient;
    private final Listener listener;

    public RecipeAdapter(List<Matcher.Match> matches, boolean showMissingIngredient, Listener listener) {
        this.matches = matches;
        this.showMissingIngredient = showMissingIngredient;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;

        VH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.sub);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Matcher.Match match = matches.get(position);
        holder.title.setText(match.recipe.name);

        if (showMissingIngredient) {
            bindMissingIngredient(holder, match.missing.get(0));
        } else {
            bindReadyRecipe(holder, match);
        }

        holder.itemView.setOnClickListener(view -> listener.onClick(match.recipe));
    }

    private void bindMissingIngredient(VH holder, Recipe.Ing ingredient) {
        holder.subtitle.setText("Missing: " + formatQuantity(ingredient.qty)
                + " " + ingredient.unit + " " + ingredient.name);
        holder.subtitle.setTextColor(COLOR_MISSING);
    }

    private void bindReadyRecipe(VH holder, Matcher.Match match) {
        if (match.rescue > 0) {
            String ingredientLabel = match.rescue == 1 ? "ingredient" : "ingredients";
            holder.subtitle.setText("♻ Rescues " + match.rescue + " expiring "
                    + ingredientLabel + "!");
            holder.subtitle.setTextColor(COLOR_RESCUE);
            return;
        }

        holder.subtitle.setText(match.recipe.ings.size() + " ingredients - ready to cook");
        holder.subtitle.setTextColor(COLOR_READY);
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }
}
