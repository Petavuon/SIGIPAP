package presentacion;

import dominio.*;
import auxiliares.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MenuVentas {
    
    public static List<Venta> mostrar(Inventario inventario, List<Venta> historialVentas) {
        int opcion;
        
        do {
            AuxConsola.limpiarPantalla();
            AuxConsola.mostrarCabecera("Menu Principal > Registro de Ventas");
            AuxConsola.mostrarMenu(AuxConstantes.MENU_VENTAS);
            AuxConsola.mostrarSeparador();
            
            opcion = AuxConsola.leerEntero("Seleccione una opcion", 1, AuxConstantes.MENU_VENTAS.length);
            if (opcion == -1) continue;
            
            switch (opcion) {
                case 1: 
                    historialVentas = nuevaVenta(inventario, historialVentas);
                    break;
                case 2: 
                    consultarVentasDia(historialVentas);
                    break;
                case 3: 
                    consultarVentasPorFecha(historialVentas);
                    break;
                case 4: 
                    historialVentas = cancelarUltimaVenta(historialVentas);
                    break;
                case 5: 
                    mostrarTotalVentas(historialVentas);
                    break;
                case 6:
                    AuxConsola.mostrarInfo("Regresando al menu principal...");
                    AuxConsola.pausa();
                    return historialVentas;
            }
        } while (true);
    }
    
    private static List<Venta> nuevaVenta(Inventario inventario, List<Venta> historialVentas) {
        Venta ventaActual = new Venta();
        
        do {
            AuxConsola.limpiarPantalla();
            AuxConsola.mostrarCabecera("Nueva Venta");
            
            if (!ventaActual.getDetalles().isEmpty()) {
                AuxConsola.mostrarTituloSeccion("Productos agregados");
                for (DetalleVenta detalle : ventaActual.getDetalles()) {
                    System.out.printf("- %s - Cantidad: %d - Subtotal: $%.2f%n",
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getSubtotal());
                }
                AuxConsola.mostrarSeparador();
                System.out.printf("TOTAL ACTUAL: $%.2f%n", ventaActual.getTotal());
                AuxConsola.mostrarSeparador();
            }
            
            System.out.println("\n1. Agregar producto");
            System.out.println("2. Confirmar venta");
            System.out.println("3. Cancelar venta");
            
            int opcion = AuxConsola.leerEntero("Seleccione", 1, 3);
            if (opcion == -1) continue;
            
            switch (opcion) {
                case 1:
                    agregarProductoAVenta(inventario, ventaActual);
                    break;
                case 2:
                    if (confirmarVenta(ventaActual)) {
                        historialVentas.add(ventaActual);
                        AuxConsola.mostrarExito("Venta registrada exitosamente");
                        AuxConsola.pausa();
                        return historialVentas;
                    }
                    break;
                case 3:
                    if (AuxConsola.leerConfirmacion(AuxConstantes.CONFIRM_CANCELAR)) {
                        AuxConsola.mostrarInfo("Venta cancelada");
                        AuxConsola.pausa();
                        return historialVentas;
                    }
                    break;
            }
        } while (true);
    }
    
    private static void agregarProductoAVenta(Inventario inventario, Venta ventaActual) {
        if (inventario.getProductos().isEmpty()) {
            AuxConsola.mostrarError("No hay productos disponibles para vender");
            AuxConsola.pausa();
            return;
        }
        
        String nombre = AuxConsola.leerTexto("Nombre del producto", 2, 50);
        if (nombre == null) return;
        
        Producto producto = inventario.buscarPorNombre(nombre);
        if (producto == null) {
            AuxConsola.mostrarError(AuxConstantes.ERR_PRODUCTO_NO_ENCONTRADO);
            AuxConsola.pausa();
            return;
        }
        
        System.out.printf("Producto: %s - Precio: $%.2f - Stock disponible: %d%n",
            producto.getNombre(), producto.getPrecio(), producto.getCantidadDisponible());
        
        if (producto.tieneBajoInventario()) {
            AuxConsola.mostrarAdvertencia("Este producto tiene inventario bajo!");
        }
        if (producto.inventarioCritico()) {
            AuxConsola.mostrarError("CRITICO: Este producto tiene stock muy bajo!");
        }
        
        Integer cantidad = AuxConsola.leerEntero("Cantidad a vender", 1, producto.getCantidadDisponible());
        if (cantidad == null) return;
        
        try {
            DetalleVenta detalle = new DetalleVenta(producto, cantidad);
            ventaActual.agregarDetalle(detalle);
            AuxConsola.mostrarExito("Producto agregado a la venta");
        } catch (IllegalArgumentException | IllegalStateException e) {
            AuxConsola.mostrarError("Error: " + e.getMessage());
        }
        
        AuxConsola.pausa();
    }
    
    private static boolean confirmarVenta(Venta ventaActual) {
        if (ventaActual.getDetalles().isEmpty()) {
            AuxConsola.mostrarError("No se puede confirmar una venta sin productos");
            AuxConsola.pausa();
            return false;
        }
        
        AuxConsola.limpiarPantalla();
        AuxConsola.mostrarCabecera("Resumen de la venta");
        
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            Producto p = detalle.getProducto();
            System.out.printf("- %s x%d = $%.2f (Stock actual: %d)%n",
                p.getNombre(),
                detalle.getCantidad(),
                detalle.getSubtotal(),
                p.getCantidadDisponible());
        }
        AuxConsola.mostrarSeparador();
        System.out.printf("TOTAL: $%.2f%n", ventaActual.getTotal());
        AuxConsola.mostrarSeparador();
        
    
        AuxConsola.mostrarSeparador();
        
        if (AuxConsola.leerConfirmacion("Confirmar venta? (S/N): ")) {
            try {
                ventaActual.confirmar();
                AuxConsola.mostrarExito("Venta registrada exitosamente");
                AuxConsola.pausa();
                return true;
            } catch (IllegalStateException e) {
                AuxConsola.mostrarError("Error al confirmar venta: " + e.getMessage());
                AuxConsola.pausa();
            }
        }
        return false;
    }
    
    private static void consultarVentasDia(List<Venta> historialVentas) {
        AuxConsola.limpiarPantalla();
        AuxConsola.mostrarCabecera("Ventas del dia");
        
        LocalDate hoy = LocalDate.now();
        List<Venta> ventasHoy = new ArrayList<>();
        for (Venta v : historialVentas) {
            if (v.getFecha().equals(hoy) && v.getEstado().equals("confirmada")) {
                ventasHoy.add(v);
            }
        }
        
        if (ventasHoy.isEmpty()) {
            AuxConsola.mostrarInfo("No hay ventas registradas hoy");
        } else {
            double totalDia = 0;
            for (int i = 0; i < ventasHoy.size(); i++) {
                Venta v = ventasHoy.get(i);
                System.out.printf("%d. Venta del %s - Total: $%.2f%n", 
                    i + 1, v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), v.getTotal());
                totalDia += v.getTotal();
            }
            AuxConsola.mostrarSeparador();
            System.out.printf("TOTAL DEL DIA: $%.2f%n", totalDia);
        }
        
        AuxConsola.pausa();
    }
    
    private static void consultarVentasPorFecha(List<Venta> historialVentas) {
        AuxConsola.limpiarPantalla();
        AuxConsola.mostrarCabecera("Consultar ventas por fecha");
        
        String fechaStr = AuxConsola.leerFecha("Ingrese la fecha");
        if (fechaStr == null) return;
        
        LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        List<Venta> ventasFecha = new ArrayList<>();
        for (Venta v : historialVentas) {
            if (v.getFecha().equals(fecha) && v.getEstado().equals("confirmada")) {
                ventasFecha.add(v);
            }
        }
        
        if (ventasFecha.isEmpty()) {
            AuxConsola.mostrarInfo("No hay ventas registradas en esta fecha");
        } else {
            double totalFecha = 0;
            for (int i = 0; i < ventasFecha.size(); i++) {
                Venta v = ventasFecha.get(i);
                System.out.printf("%d. Venta - Total: $%.2f%n", i + 1, v.getTotal());
                totalFecha += v.getTotal();
            }
            AuxConsola.mostrarSeparador();
            System.out.printf("TOTAL: $%.2f%n", totalFecha);
        }
        
        AuxConsola.pausa();
    }
    
    private static List<Venta> cancelarUltimaVenta(List<Venta> historialVentas) {
        if (historialVentas.isEmpty()) {
            AuxConsola.mostrarAdvertencia("No hay ventas registradas para cancelar");
            AuxConsola.pausa();
            return historialVentas;
        }
        
        Venta ultimaVenta = historialVentas.get(historialVentas.size() - 1);
        if (!ultimaVenta.getEstado().equals("confirmada")) {
            AuxConsola.mostrarError("La ultima venta ya esta cancelada o pendiente");
            AuxConsola.pausa();
            return historialVentas;
        }
        
        AuxConsola.mostrarTituloSeccion("Ultima venta");
        System.out.printf("Fecha: %s%n", ultimaVenta.getFecha());
        System.out.printf("Total: $%.2f%n", ultimaVenta.getTotal());
        AuxConsola.mostrarSeparador();
        
        if (AuxConsola.leerConfirmacion("Cancelar esta venta? (S/N): ")) {
            ultimaVenta.cancelar();
            AuxConsola.mostrarExito("Venta cancelada exitosamente");
        }
        AuxConsola.pausa();
        return historialVentas;
    }
    
    private static void mostrarTotalVentas(List<Venta> historialVentas) {
        AuxConsola.limpiarPantalla();
        AuxConsola.mostrarCabecera("Total de ventas");
        
        double totalGeneral = 0;
        long cantidadVentas = 0;
        for (Venta v : historialVentas) {
            if (v.getEstado().equals("confirmada")) {
                totalGeneral += v.getTotal();
                cantidadVentas++;
            }
        }
        
        System.out.printf("TOTAL ACUMULADO DE VENTAS: $%.2f%n", totalGeneral);
        System.out.println("NUMERO DE VENTAS REALIZADAS: " + cantidadVentas);
        
        AuxConsola.pausa();
    }
}