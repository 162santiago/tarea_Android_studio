package com.example.crudproductos;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crudproductos.Modelo.Producto;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {
    private Context context;
    private List<Producto> listaProductos;

    //Construtor
    public ProductoAdapter(Context context,List<Producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        holder.txtNombre.setText(producto.getNombre());
        holder.txtPrecio.setText("Precio: S/. " + producto.getPrecio());
        holder.txtDescripcion.setText("descripcion:" + producto.getDescripcion());
        holder.txtStock.setText("Stock  : " + producto.getStock());

        Glide.with(holder.itemView.getContext())
                .load(producto.getUrl())
                .into(holder.imgPro);

        //Click en el item
        holder.itemView.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(context, EditarProductoActivity.class);
                intent.putExtra("id", producto.getId());
                intent.putExtra("nombre", producto.getNombre());
                intent.putExtra("precio", producto.getPrecio());
                intent.putExtra("descripcion", producto.getDescripcion());
                intent.putExtra("stock", producto.getStock());
                intent.putExtra("url", producto.getUrl());
                Toast.makeText(context, "Producto", Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }catch (Exception e){
                Log.e("ERROR_INTENT", "Error al abrir EditarProductoActivity", e);
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        //Click largo en el item
        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Eliminar Producto")
                    .setMessage("¿Deseas eliminar este producto?")
                    .setPositiveButton("Sí", (dialog, which) -> eliminarProducto(position))
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPrecio,txtDescripcion, txtStock;
        ImageView imgPro;
        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtStock = itemView.findViewById(R.id.txtStock);
            imgPro = itemView.findViewById(R.id.imageView);
        }
    }

    public void eliminarProducto(int position) {
        Producto producto = listaProductos.get(position);
        DatabaseHelper db = new DatabaseHelper(context);
        if (db.eliminarProducto(producto.getId()) > 0) {
            listaProductos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listaProductos.size());
            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}
