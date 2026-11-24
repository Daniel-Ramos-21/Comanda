
package com.example.comanda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comanda.R;
import com.example.comanda.models.ItemCarrito;

import java.util.List;
import java.util.Locale;

public class CarritoAdapter
        extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    public interface OnCarritoChangeListener {
        void onCantidadCambiada(int posicion, int nuevaCantidad);

        void onEliminar(int posicion);
    }

    private final List<ItemCarrito> items;
    private final OnCarritoChangeListener listener;

    public CarritoAdapter(
            List<ItemCarrito> items,
            OnCarritoChangeListener listener
    ) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carrito, parent, false);

        return new CarritoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CarritoViewHolder holder,
            int position
    ) {
        ItemCarrito item = items.get(position);

        holder.tvNombre.setText(item.getNombre());

        holder.tvPrecio.setText(
                String.format(
                        Locale.US,
                        "$%.2f",
                        item.getPrecioUnitarioCentavos() / 100.0
                )
        );

        holder.tvCantidad.setText(
                String.valueOf(item.getCantidad())
        );

        holder.tvSubtotal.setText(
                String.format(
                        Locale.US,
                        "$%.2f",
                        item.getSubtotalCentavos() / 100.0
                )
        );

        if (item.getNotas() != null &&
                !item.getNotas().trim().isEmpty()) {

            holder.tvNotas.setVisibility(View.VISIBLE);
            holder.tvNotas.setText("Nota: " + item.getNotas());

        } else {
            holder.tvNotas.setVisibility(View.GONE);
        }

        cargarImagen(holder.ivProducto, item.getImagenUrl());

        // Disminuir cantidad
        holder.btnMenos.setOnClickListener(v -> {
            int posicionActual = holder.getAdapterPosition();

            if (posicionActual == RecyclerView.NO_POSITION) {
                return;
            }

            ItemCarrito itemActual = items.get(posicionActual);

            if (itemActual.getCantidad() > 1 && listener != null) {
                listener.onCantidadCambiada(
                        posicionActual,
                        itemActual.getCantidad() - 1
                );
            }
        });

        // Aumentar cantidad
        holder.btnMas.setOnClickListener(v -> {
            int posicionActual = holder.getAdapterPosition();

            if (posicionActual == RecyclerView.NO_POSITION) {
                return;
            }

            ItemCarrito itemActual = items.get(posicionActual);

            if (listener != null) {
                listener.onCantidadCambiada(
                        posicionActual,
                        itemActual.getCantidad() + 1
                );
            }
        });

        // Eliminar producto
        holder.btnEliminar.setOnClickListener(v -> {
            int posicionActual = holder.getAdapterPosition();

            if (posicionActual == RecyclerView.NO_POSITION) {
                return;
            }

            if (listener != null) {
                listener.onEliminar(posicionActual);
            }
        });
    }

    private void cargarImagen(ImageView imageView, String imagenUrl) {
        if (imagenUrl == null ||
                imagenUrl.trim().isEmpty() ||
                !imagenUrl.startsWith("https://")) {

            imageView.setImageResource(R.drawable.ic_food_pupusas);
            return;
        }

        Glide.with(imageView.getContext())
                .load(imagenUrl)
                .placeholder(R.drawable.ic_food_pupusas)
                .error(R.drawable.ic_food_pupusas)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class CarritoViewHolder
            extends RecyclerView.ViewHolder {

        final ImageView ivProducto;
        final TextView tvNombre;
        final TextView tvPrecio;
        final TextView tvNotas;
        final TextView tvCantidad;
        final TextView tvSubtotal;
        final TextView btnMenos;
        final TextView btnMas;
        final TextView btnEliminar;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProducto = itemView.findViewById(R.id.ivCarritoProducto);
            tvNombre = itemView.findViewById(R.id.tvCarritoNombre);
            tvPrecio = itemView.findViewById(R.id.tvCarritoPrecio);
            tvNotas = itemView.findViewById(R.id.tvCarritoNotas);
            tvCantidad = itemView.findViewById(R.id.tvCarritoCantidad);
            tvSubtotal = itemView.findViewById(R.id.tvCarritoSubtotal);

            btnMenos = itemView.findViewById(R.id.btnCarritoMenos);
            btnMas = itemView.findViewById(R.id.btnCarritoMas);
            btnEliminar = itemView.findViewById(R.id.btnCarritoEliminar);
        }
    }
}
