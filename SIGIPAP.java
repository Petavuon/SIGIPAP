package presentacion;

import dominio.*;
import auxiliares.*;
import java.util.ArrayList;
import java.util.List;

public class SIGIPAP {
    private static Inventario inventario = new Inventario();
    private static List<Venta> historialVentas = new ArrayList<>();
    private static List<Empleado> nomina = new ArrayList<>();

    public static void main(String[] args) {
        cargarDatosEjemplo();
        cargarNominaEjemplo();

        int opcion;
        String[] MENU_PRINCIPAL_MODIFICADO = {
            "Gestion de productos",
            "Control de inventario",
            "Registro de ventas",
            "Consulta de ventas y reportes",
            "Alertas de inventario",
            "REPORTE DE NOMINA",
            "Salir del sistema"
        };

        do {
            AuxConsola.limpiarPantalla();
            AuxConsola.mostrarCabecera("Menu Principal");
            AuxConsola.mostrarMenu(MENU_PRINCIPAL_MODIFICADO);
            AuxConsola.mostrarSeparador();

            opcion = AuxConsola.leerEntero("Seleccione una opcion", 1, MENU_PRINCIPAL_MODIFICADO.length);
            if (opcion == -1) continue;

            switch (opcion) {
                case 1:
                    MenuProductos.mostrar(inventario);
                    break;
                case 2:
                    MenuInventario.mostrar(inventario);
                    break;
                case 3:
                    historialVentas = MenuVentas.mostrar(inventario, historialVentas);
                    break;
                case 4:
                    MenuReportes.mostrar(inventario, historialVentas);
                    break;
                case 5:
                    mostrarAlertas();
                    break;
                case 6:
                    generarReporteNominaPolimorfico();
                    break;
                case 7:
                    AuxConsola.mostrarExito(AuxConstantes.MSG_DESPEDIDA);
                    AuxConsola.pausa();
                    return;
            }
        } while (true);
    }

    private static void cargarDatosEjemplo() {
        try {
            inventario.agregarProducto(new Producto("Boligrafos", 8.50, 25, 10));
            inventario.agregarProducto(new Producto("Cuadernos", 35.00, 15, 8));
            inventario.agregarProducto(new Producto("Gomas", 3.50, 5, 10));
            inventario.agregarProducto(new Producto("Lapices", 5.00, 30, 12));
            inventario.agregarProducto(new Producto("Marcadores", 12.00, 8, 10));
            inventario.agregarProducto(new Producto("Hojas bond", 25.00, 50, 20));
        } catch (IllegalArgumentException e) {
            System.out.println("Error al cargar datos de ejemplo: " + e.getMessage());
        }
    }

    private static void cargarNominaEjemplo() {
        nomina.add(new EmpleadoTiempoCompleto("E001", "Ana Martinez", "Gerente Ventas", 25000.00, 30000.00));
        nomina.add(new EmpleadoTiempoCompleto("E002", "Carlos Lopez", "Desarrollador Sr.", 22000.00, 15000.00));
        nomina.add(new EmpleadoPorHoras("E003", "Laura Gomez", "Asesor Ventas", 150.00, 48));
        nomina.add(new EmpleadoPorHoras("E004", "Jose Rodriguez", "Soporte Tecnico", 120.00, 40));
        nomina.add(new EmpleadoTiempoCompleto("E005", "Marta Fernandez", "Contadora", 21000.00, 10000.00));
    }

    private static void mostrarAlertas() {
        AuxConsola.limpiarPantalla();
        AuxConsola.mostrarCabecera("Alertas de Inventario");

        List<Producto> productosBajo = inventario.productosConBajoInventario();
        List<Producto> productosCriticos = inventario.productosSinExistencia();

        if (productosBajo.isEmpty() && productosCriticos.isEmpty()) {
            AuxConsola.mostrarExito("No hay alertas activas. Inventario saludable.");
        } else {
            if (!productosBajo.isEmpty()) {
                AuxConsola.mostrarTituloSeccion("Productos con inventario bajo");
                for (Producto p : productosBajo) {
                    System.out.printf("- %s - Stock: %d (Minimo: %d)%n", 
                        p.getNombre(), p.getCantidadDisponible(), p.getLimiteMinimo());
                }
            }

            if (!productosCriticos.isEmpty()) {
                AuxConsola.mostrarTituloSeccion("Productos criticos (sin existencia)");
                for (Producto p : productosCriticos) {
                    System.out.printf("- %s - SIN EXISTENCIA%n", p.getNombre());
                }
            }
        }

        AuxConsola.pausa();
    }

    private static void generarReporteNominaPolimorfico() {
        AuxConsola.limpiarPantalla();
        AuxConsola.mostrarCabecera(">>> REPORTE DE NOMINA (Ejemplo de Polimorfismo Dinamico) <<<");

        Empleado[] empleados = nomina.toArray(new Empleado[0]);
        double totalNomina = 0;

        System.out.println("+------+----------------------+----------------------+----------------------+------------------+");
        System.out.printf("| %-4s | %-20s | %-20s | %-20s | %-16s |\n", "ID", "Nombre", "Puesto", "Detalle Especifico", "Salario Calculado");
        System.out.println("+------+----------------------+----------------------+----------------------+------------------+");

        for (Empleado emp : empleados) {
            double salario = emp.calcularSalario();
            totalNomina += salario;

            String detalle = "";
            if (emp instanceof EmpleadoTiempoCompleto) {
                EmpleadoTiempoCompleto etc = (EmpleadoTiempoCompleto) emp;
                detalle = String.format("Base: $%.0f", etc.getSalarioMensualBase());
            } else if (emp instanceof EmpleadoPorHoras) {
                EmpleadoPorHoras eph = (EmpleadoPorHoras) emp;
                detalle = String.format("Tarifa: $%.0f x %d hrs", eph.getTarifaPorHora(), eph.getHorasTrabajadas());
            }

            System.out.printf("| %-4s | %-20s | %-20s | %-20s | $%-15.2f |\n",
                    emp.getId(), emp.getNombre(), emp.getPuesto(), detalle, salario);
        }
        System.out.println("+------+----------------------+----------------------+----------------------+------------------+");
        System.out.printf("| %-94s | $%-15.2f |\n", "TOTAL ACUMULADO DE LA NOMINA", totalNomina);
        System.out.println("+--------------------------------------------------------------------------------------------+");


        AuxConsola.pausa();
    }
}