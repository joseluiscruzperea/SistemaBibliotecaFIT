package fit.biblioteca.modelo;

/**
 * Enumeración que define los tipos de usuarios del sistema
 * según las reglas de negocio (RN-01).
 */
public enum TipoUsuario {
    ALUMNO("Alumno", 3, 7),
    DOCENTE("Docente", 5, 15),
    ADMINISTRATIVO("Personal Administrativo", 3, 7);

    private final String descripcion;
    private final int limiteLibros;
    private final int diasPrestamo;

    TipoUsuario(String descripcion, int limiteLibros, int diasPrestamo) {
        this.descripcion = descripcion;
        this.limiteLibros = limiteLibros;
        this.diasPrestamo = diasPrestamo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getLimiteLibros() {
        return limiteLibros;
    }

    public int getDiasPrestamo() {
        return diasPrestamo;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
