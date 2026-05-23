// Venta.java
package dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private LocalDate fecha;
    private List<DetalleVenta> detalles;
    private double total;
    private String estado;
    private static int contadorVentas = 0;
    private int numeroVenta;

    public Venta() {
        this.fecha = LocalDate.now();
        this.detalles = new ArrayList<>();
        this.total = 0.0;
        this.estado = "pendiente";
        this.numeroVenta = ++contadorVentas;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public double getTotal() {
        return total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (!estado.equals("pendiente") && !estado.equals("confirmada") && !estado.equals("cancelada")) {
            throw new IllegalArgumentException("Estado no valido");
        }
        this.estado = estado;
    }

    public int getNumeroVenta() {
        return numeroVenta;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo");
        }
        if (!estado.equals("pendiente")) {
            throw new IllegalStateException("Solo se pueden agregar detalles a una venta pendiente");
        }
        detalles.add(detalle);
        calcularTotal();
    }

    private void calcularTotal() {
        total = detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
    }

    public void confirmar() {
        if (detalles.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar una venta sin detalles");
        }
        
        System.out.println("Venta #" + numeroVenta + " - Fecha: " + fecha);
        System.out.println("────────────────────────────────────────────────────────────────");
        
        for (DetalleVenta detalle : detalles) {
            Producto p = detalle.getProducto();
            int cantidad = detalle.getCantidad();
            int stockAntes = p.getCantidadDisponible() + cantidad;
            
            System.out.println("\n▶ Producto: " + p.getNombre());
            System.out.println("   Stock antes: " + stockAntes + " unidades");
            System.out.println("   Cantidad vendida: " + cantidad);

            if (p.inventarioCritico()) {

                System.out.println("   [USANDO VERSION 2] reducirStock(cantidad, razon)");
                p.reducirStock(cantidad, "Venta #" + numeroVenta + " - Producto con stock critico");
            } else {

                System.out.println("   [USANDO VERSION 1] reducirStock(cantidad)");
                p.reducirStock(cantidad);
            }
            
            System.out.println("   Stock despues: " + p.getCantidadDisponible() + " unidades");
            System.out.println("   ✓ Reduccion completada");
        }
        
        this.estado = "confirmada";
        System.out.println("\n────────────────────────────────────────────────────────────────");
        System.out.println("✅ VENTA #" + numeroVenta + " CONFIRMADA - Total: $" + String.format("%.2f", total));
        System.out.println("══════════════════════════════════════════════════════════════════\n");
    }

    public void cancelar() {
        this.estado = "cancelada";
        System.out.println("[INFO] Venta #" + numeroVenta + " ha sido cancelada");
    }
}