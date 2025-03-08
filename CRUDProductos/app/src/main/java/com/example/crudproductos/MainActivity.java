package com.example.crudproductos;
import android.Manifest;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;




import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudproductos.Modelo.Producto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdapter adapter;
    private DatabaseHelper databaseHelper;
    private Button btnAgregarProducto, btnGenerarPDF;
    private static final int PERMISSION_REQUEST_CODE = 1; // Declaración de la constante


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
        btnGenerarPDF = findViewById(R.id.btnGenerarPDF);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        Log.d("DB_DEBUG", "Cantidad de productos: " + databaseHelper.contarRegistros());
        actualizarLista();

        btnAgregarProducto.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AgregarProductoActivity.class));
        });
        btnGenerarPDF.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Solicitar el permiso si no ha sido otorgado
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                // Permiso ya otorgado, puedes proceder con la acción
                generarPDF();
            }

        });
    }

    private void actualizarLista() {
        List<Producto> listaProductos = databaseHelper.obtenerProductos();
        adapter = new ProductoAdapter(this, listaProductos);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarLista();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el permiso es concedido, puedes proceder
                Log.d("Permission", "Permiso concedido.");
                generarPDF();
            } else {
                // El permiso fue denegado
                Log.d("Permission", "Permiso denegado.");
                Toast.makeText(this, "Permiso denegado, no se puede generar el PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }




    // Método para generar el PDF
    private void generarPDF() {
        // Verificar permisos si es necesario
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return; // No continuar si los permisos no están concedidos
        }

        // Obtén la lista de productos de la base de datos
        List<Producto> productos = databaseHelper.obtenerProductos();
        Log.d("PDF", "Cantidad de productos: " + productos.size()); // Verificar la cantidad de productos

        if (productos.isEmpty()) {
            Toast.makeText(this, "No hay productos en la base de datos.", Toast.LENGTH_SHORT).show();
            return; // No generar el PDF si no hay productos
        }

        // Crea un documento PDF
        Document document = new Document();
        try {
            // Usar un directorio público para la prueba
            File pdfDir = new File(Environment.getExternalStorageDirectory(), "mis_pdfs");
            if (!pdfDir.exists()) {
                pdfDir.mkdirs(); // Crear el directorio si no existe
            }

            File pdfFile = new File(pdfDir, "productos_listado.pdf");

            // Verificar ruta del archivo
            Log.d("PDF", "Ruta del archivo: " + pdfFile.getAbsolutePath());

            // Crear el archivo PDF y escribir en él
            FileOutputStream fos = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, fos);
            document.open();

            // Agregar título al PDF
            document.add(new Paragraph("Listado de Productos"));
            document.add(new Paragraph("\n"));

            // Agregar los detalles de los productos al PDF
            for (Producto producto : productos) {
                if (producto.getNombre() != null && !producto.getNombre().isEmpty()) {
                    String productDetails = "ID: " + producto.getId() + "\n" +
                            "Nombre: " + producto.getNombre() + "\n" +
                            "Precio: " + producto.getPrecio() + "\n" +
                            "Descripción: " + producto.getDescripcion() + "\n" +
                            "Stock: " + producto.getStock() + "\n" +
                            "URL: " + producto.getUrl() + "\n";
                    document.add(new Paragraph(productDetails));
                    document.add(new Paragraph("\n"));
                } else {
                    Log.d("PDF", "Producto con nombre vacío o nulo: " + producto.getId());
                }
            }

            document.close();
            Log.d("PDF", "Documento PDF cerrado y generado correctamente.");

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "PDF generado exitosamente: " + pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            // Mostrar un mensaje de error
            Log.e("PDF_ERROR", "Error al generar el PDF: " + e.getMessage());
            Toast.makeText(this, "Error al generar el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



}
