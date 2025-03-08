package com.example.crudproductos;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class AgregarProductoActivity extends AppCompatActivity {

    private EditText edtNombre, edtPrecio, edtDescripcion , edtStock, edtUrl;
    private Button btnGuardar;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        edtNombre = findViewById(R.id.edtNombre);
        edtPrecio = findViewById(R.id.edtPrecio);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtStock = findViewById(R.id.edtStock);
        edtUrl = findViewById(R.id.edtUrl);
        btnGuardar = findViewById(R.id.btnGuardar);
        databaseHelper = new DatabaseHelper(this);

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {
        String nombre = edtNombre.getText().toString().trim();
        String precioStr = edtPrecio.getText().toString().trim();
        String description = edtDescripcion.getText().toString().trim();
        String stockStr = edtStock.getText().toString().trim();
        String url = edtUrl.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || description.isEmpty() || stockStr.isEmpty() || url.isEmpty() ) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int stock = Integer.parseInt(stockStr);

        databaseHelper.insertarProducto(nombre, precio ,description, stock, url);
        Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}
