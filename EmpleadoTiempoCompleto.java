package dominio;

public class EmpleadoTiempoCompleto extends Empleado {
    private double salarioMensualBase;
    private double bonoAnual;

    public EmpleadoTiempoCompleto(String id, String nombre, String puesto, 
                                  double salarioMensualBase, double bonoAnual) {
        super(id, nombre, puesto);
        this.salarioMensualBase = salarioMensualBase;
        this.bonoAnual = bonoAnual;
    }

    public double getSalarioMensualBase() {
        return salarioMensualBase;
    }

    public double getBonoAnual() {
        return bonoAnual;
    }

    @Override
    public double calcularSalario() {
        double bonoMensual = this.bonoAnual / 12;
        double salarioTotal = this.salarioMensualBase + bonoMensual;
        System.out.print("[TIEMPO COMPLETO] Salario mensual calculado: ");
        return salarioTotal;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Base: $%.2f | Bono Anual: $%.2f", 
               salarioMensualBase, bonoAnual);
    }
}