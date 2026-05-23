package dominio;

public abstract class Empleado {
    protected String id;
    protected String nombre;
    protected String puesto;

    public Empleado(String id, String nombre, String puesto) {
        this.id = id;
        this.nombre = nombre;
        this.puesto = puesto;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPuesto() {
        return puesto;
    }
    public double calcularSalario() {
        System.out.print("[BASE] Calculo generico de salario: ");
        return 500.00;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Nombre: %s | Puesto: %s", id, nombre, puesto);
    }
}