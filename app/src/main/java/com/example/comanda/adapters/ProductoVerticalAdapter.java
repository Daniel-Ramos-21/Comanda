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
import com.example.comanda.models.Producto;

import java.util.List;
import java.util.Locale;

public class ProductoVerticalAdapter extends RecyclerView.Adapter<ProductoVerticalAdapter.ProductoViewHolder> {

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    private List<Producto> productos;
    private final OnProductoClickListener listener;

    public ProductoVerticalAdapter(
            List<Producto> productos,
            OnProductoClickListener listener
    ) {
        this.productos = productos;
        this.listener = listener;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_vertical, parent, false);

        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProductoViewHolder holder,
            int position
    ) {
        Producto producto = productos.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion());

        double precio = producto.getPrecioCentavos() / 100.0;

        holder.tvPrecio.setText(
                String.format(Locale.US, "$%.2f", precio)
        );

        asignarImagen(holder.ivProducto, producto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductoClick(producto);
            }
        });
    }

    private void asignarImagen(ImageView imagen, Producto producto) {

        int imagenRespaldo = obtenerImagenRespaldo(producto);

        String imagenUrl = producto.getImagenUrl();

        if (imagenUrl == null ||
                imagenUrl.trim().isEmpty() ||
                !imagenUrl.startsWith("https://")) {

            imagen.setImageResource(imagenRespaldo);
            return;
        }

        Glide.with(imagen.getContext())
                .load(imagenUrl)
                .placeholder(imagenRespaldo)
                .error(imagenRespaldo)
                .centerCrop()
                .into(imagen);
    }

    private int obtenerImagenRespaldo(Producto producto) {

        String nombre = producto.getNombre() != null
                ? producto.getNombre().toLowerCase(Locale.ROOT)
                : "";

        if (nombre.contains("pupusa")) {
            return R.drawable.ic_food_pupusas;
        }

        if (nombre.contains("horchata") ||
                nombre.contains("bebida") ||
                nombre.contains("jugo")) {
            return R.drawable.ic_food_horchata;
        }

        if (nombre.contains("taco")) {
            return R.drawable.ic_food_tacos;
        }

        return R.drawable.ic_food_pupusas;
    }

    @Override
    public int getItemCount() {
        return productos != null ? productos.size() : 0;
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        final ImageView ivProducto;
        final TextView tvNombre;
        final TextView tvDescripcion;
        final TextView tvPrecio;

        ProductoViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProducto = itemView.findViewById(R.id.ivProductoVertical);
            tvNombre = itemView.findViewById(R.id.tvNombreVertical);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionVertical);
            tvPrecio = itemView.findViewById(R.id.tvPrecioVertical);
        }
    }
}