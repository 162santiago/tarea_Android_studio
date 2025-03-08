package com.example.crudproductos.Modelo;

public class Producto {
    //Atributos de la tabla Productos
    private int id;
    private String nombre;
    private double precio;
    private int stock;
    private String descripcion;
    private String url;

    //Métodos


    public Producto() {

    }

    public Producto(int id, String nombre, double precio,String descripcion, int stock, String url) {
        this.id = id;
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.url = url;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }


    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nom='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\''+
                ", precio=" + precio +
                ", stock=" + stock +
                ", url=" + url +
                '}';
    }


}
