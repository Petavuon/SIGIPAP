// EmpleadoPorHoras.java
package dominio;

public class EmpleadoPorHoras extends Empleado {
    private double tarifaPorHora;
    private int horasTrabajadas;

    public EmpleadoPorHoras(String id, String nombre, String puesto, 
                           double tarifaPorHora, int horasTrabajadas) {
        super(id, nombre, puesto);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
    }

    public double getTarifaPorHora() {
        return tarifaPorHora;
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }
     
    @Override
    public double calcularSalario() {
        double salarioTotal = this.tarifaPorHora * this.horasTrabajadas;
        System.out.print("[POR HORAS] Salario semanal calculado: ");
        return salarioTotal;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Tarifa/h: $%.2f | Horas: %d", 
               tarifaPorHora, horasTrabajadas);
    }
}