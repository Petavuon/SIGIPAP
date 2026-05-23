package dominio;

public class Producto {
    private String nombre;
    private double precio;
    private int cantidadDisponible;
    private int limiteMinimo;

    public Producto(String nombre, double precio, int cantidadDisponible, int limiteMinimo) {
        setNombre(nombre);
        setPrecio(precio);
        setCantidadDisponible(cantidadDisponible);
        setLimiteMinimo(limiteMinimo);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (nombre.length() < 2 || nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 50 caracteres");
        }
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (precio > 999999.99) {
            throw new IllegalArgumentException("El precio excede el limite permitido");
        }
        this.precio = precio;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        if (cantidadDisponible < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        this.cantidadDisponible = cantidadDisponible;
    }

    public int getLimiteMinimo() {
        return limiteMinimo;
    }

    public void setLimiteMinimo(int limiteMinimo) {
        if (limiteMinimo < 0) {
            throw new IllegalArgumentException("El limite minimo no puede ser negativo");
        }
        this.limiteMinimo = limiteMinimo;
    }

    public boolean tieneBajoInventario() {
        return cantidadDisponible < limiteMinimo;
    }

    public boolean sinExistencia() {
        return cantidadDisponible == 0;
    }

    public boolean inventarioCritico() {
        return cantidadDisponible <= (limiteMinimo / 2);
    }

    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser positiva");
        }
        if (cantidad > cantidadDisponible) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        this.cantidadDisponible -= cantidad;
    }

    public void reducirStock(int cantidad, String razon) {
        reducirStock(cantidad);
        System.out.println("[INFO] Reduccion de stock - Razon: " + razon);
    }
}