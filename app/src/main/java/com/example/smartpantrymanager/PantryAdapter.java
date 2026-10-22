package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnIngredientActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnIngredientActionListener listener;

    public PantryAdapter(
            List<PantryItem> items,
            OnIngredientActionListener listener) {

        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item = items.get(position);

        holder.name.setText(item.getName());

        holder.quantity.setText(String.format(
                Locale.getDefault(),
                "Quantity: %s %s",
                String.format(
                        Locale.getDefault(),
                        "%s",
                        item.getQuantity()),
                item.getUnit()));

        String expiry = item.getExpiryDate();

        if (expiry == null || expiry.trim().isEmpty()) {
            holder.expiry.setText("No expiry date set");
        } else {
            holder.expiry.setText("Expires: " + expiry);
        }

        holder.editButton.setOnClickListener(
                v -> listener.onEdit(item));

        holder.deleteButton.setOnClickListener(
                v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView quantity;
        TextView expiry;
        Button editButton;
        Button deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtIngredientName);
            quantity = itemView.findViewById(R.id.txtIngredientQuantity);
            expiry = itemView.findViewById(R.id.txtIngredientExpiry);

            editButton = itemView.findViewById(R.id.btnEditIngredient);
            deleteButton = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}
