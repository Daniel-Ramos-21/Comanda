package com.example.comanda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comanda.R;
import com.example.comanda.models.Categoria;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria, int position);
    }

    private final List<Categoria> categorias;
    private final OnCategoriaClickListener listener;
    private int posicionSeleccionada = 0;

    public CategoriaAdapter(List<Categoria> categorias, OnCategoriaClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    public int getPosicionSeleccionada() {
        return posicionSeleccionada;
    }

    public void setPosicionSeleccionada(int posicion) {
        int anterior = this.posicionSeleccionada;
        this.posicionSeleccionada = posicion;
        notifyItemChanged(anterior);
        notifyItemChanged(posicion);
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria_chip, parent, false);
        return new CategoriaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria cat = categorias.get(position);
        holder.tvNombre.setText(cat.getNombre());

        boolean isSelected = (position == posicionSeleccionada);
        if (isSelected) {
            holder.container.setBackgroundResource(R.drawable.bg_chip_selected);
            holder.tvNombre.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_chip_unselected);
            holder.tvNombre.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.munchly_text_secondary));
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                setPosicionSeleccionada(currentPos);
                if (listener != null) {
                    listener.onCategoriaClick(cat, currentPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias != null ? categorias.size() : 0;
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout container;
        final TextView tvNombre;

        CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.chipContainer);
            tvNombre = itemView.findViewById(R.id.tvChipNombre);
        }
    }
}
