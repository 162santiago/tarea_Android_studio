package com.example.crudproductos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudproductos.Modelo.Producto;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdapter adapter;
    private DatabaseHelper databaseHelper;
    private Button btnAgregarProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        Log.d("DB_DEBUG", "Cantidad de productos: " + databaseHelper.contarRegistros());
        actualizarLista();

        btnAgregarProducto.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AgregarProductoActivity.class));
        });
    }

    private void actualizarLista() {
        List<Producto> listaProductos = databaseHelper.obtenerProductos();
        adapter = new ProductoAdapter(this,listaProductos);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarLista();
    }
}