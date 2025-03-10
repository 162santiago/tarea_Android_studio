package com.example.crudproductos;
import android.Manifest;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crudproductos.Modelo.Producto;
import java.io.File;
import com.itextpdf.text.Document;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

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
    public void generarPDF() {
        // Verificar permisos (Solo necesario para Android 9 o menor)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                return;
            }
        }

        // Obtener la lista de productos
        List<Producto> productos = databaseHelper.obtenerProductos();
        if (productos.isEmpty()) {
            Toast.makeText(this, "No hay productos en la base de datos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el directorio para guardar el PDF
        File pdfDir = new File(getExternalFilesDir(null), "mis_pdfs");
        if (!pdfDir.exists() && !pdfDir.mkdirs()) {
            Log.e("PDF_ERROR", "No se pudo crear el directorio.");
            return;
        }

        File pdfFile = new File(pdfDir, "productos_listado.pdf");
        Log.d("PDF", "Ruta del archivo: " + pdfFile.getAbsolutePath());

        Document document = new Document(PageSize.A4);
        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, fos);
            document.open();

            // Título del PDF
            document.add(new Paragraph("Listado de Productos\n\n"));

            // Crear la tabla con 6 columnas
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 2.5f, 1.5f, 3.5f, 1.5f, 2.5f}); // Ajusta los tamaños de columna

            // Color de fondo para la cabecera (azul claro #a5d8ff)
            BaseColor headerColor = new BaseColor(165, 216, 255);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

            String[] headers = {"ID", "Nombre", "Precio", "Descripción", "Stock", "Imagen"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(headerColor);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }

            // Descargar imágenes en un hilo separado
            new Thread(() -> {
                for (Producto producto : productos) {
                    table.addCell(String.valueOf(producto.getId()));  // ID
                    table.addCell(producto.getNombre());  // Nombre
                    table.addCell(String.valueOf(producto.getPrecio()));  // Precio
                    table.addCell(producto.getDescripcion());  // Descripción
                    table.addCell(String.valueOf(producto.getStock()));  // Stock

                    PdfPCell imgCell;
                    if (producto.getUrl() != null && !producto.getUrl().isEmpty()) {
                        Bitmap bitmap = descargarImagen(producto.getUrl());
                        if (bitmap != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            try {
                                Image img = Image.getInstance(stream.toByteArray());
                                img.scaleToFit(60, 60); // Asegura un tamaño consistente
                                imgCell = new PdfPCell(img, true);
                            } catch (Exception e) {
                                Log.e("IMG_ERROR", "Error al insertar imagen en PDF: " + e.getMessage());
                                imgCell = new PdfPCell(new Phrase("No disponible"));
                            }
                        } else {
                            imgCell = new PdfPCell(new Phrase("No disponible"));
                        }
                    } else {
                        imgCell = new PdfPCell(new Phrase("No disponible"));
                    }
                    imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    imgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    imgCell.setPadding(5);
                    table.addCell(imgCell);
                }

                // Agregar la tabla al PDF en el hilo principal
                runOnUiThread(() -> {
                    try {
                        document.add(table);
                        document.close();
                        Log.d("PDF", "PDF generado correctamente.");
                        Toast.makeText(this, "PDF generado exitosamente: " + pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    } catch (DocumentException e) {
                        Log.e("PDF_ERROR", "Error al agregar la tabla: " + e.getMessage());
                    }
                });

            }).start();

        } catch (DocumentException | IOException e) {
            Log.e("PDF_ERROR", "Error al generar el PDF: " + e.getMessage());
            Toast.makeText(this, "Error al generar el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Método para descargar imágenes desde una URL
    private Bitmap descargarImagen(String urlImagen) {
        try {
            URL url = new URL(urlImagen);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();

            if (bitmap == null) {
                Log.e("IMG_ERROR", "Imagen vacía: " + urlImagen);
            } else {
                Log.d("IMG_SUCCESS", "Imagen descargada: " + urlImagen);
            }

            return bitmap;
        } catch (Exception e) {
            Log.e("IMG_ERROR", "No se pudo descargar la imagen: " + e.getMessage());
            return null;
        }
    }


}
