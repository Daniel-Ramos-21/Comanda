package com.example.comanda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comanda.R;
import com.example.comanda.models.Producto;

import java.util.List;
import java.util.Locale;

public class ProductoHorizontalAdapter extends RecyclerView.Adapter<ProductoHorizontalAdapter.ProductoViewHolder> {

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    private List<Producto> productos;
    private final OnProductoClickListener listener;

    public ProductoHorizontalAdapter(List<Producto> productos, OnProductoClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_horizontal, parent, false);
        return new ProductoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto p = productos.get(position);
        holder.tvNombre.setText(p.getNombre());
        holder.tvDescripcion.setText(p.getDescripcion());

        double precioDolares = p.getPrecioCentavos() / 100.0;
        holder.tvPrecio.setText(String.format(Locale.US, "$%.2f", precioDolares));

        // Asignación de recurso visual según categoría o identificador
        asignarImagen(holder.ivProducto, p);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductoClick(p);
            }
        });
    }

    private void asignarImagen(ImageView iv, Producto p) {
        String nombre = p.getNombre() != null ? p.getNombre().toLowerCase() : "";
        if (nombre.contains("pupusa")) {
            iv.setImageResource(R.drawable.ic_food_pupusas);
        } else if (nombre.contains("horchata") || nombre.contains("bebida") || nombre.contains("jugo")) {
            iv.setImageResource(R.drawable.ic_food_horchata);
        } else if (nombre.contains("taco")) {
            iv.setImageResource(R.drawable.ic_food_tacos);
        } else {
            iv.setImageResource(R.drawable.ic_food_horchata);
        }
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
            ivProducto = itemView.findViewById(R.id.ivProductoHorizontal);
            tvNombre = itemView.findViewById(R.id.tvNombreHorizontal);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionHorizontal);
            tvPrecio = itemView.findViewById(R.id.tvPrecioHorizontal);
        }
    }
}
