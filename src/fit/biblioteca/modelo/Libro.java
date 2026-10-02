package fit.biblioteca.modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Entidad que representa la ficha bibliográfica dentro del catálogo general.
 * Incorpora Clasificación Decimal Dewey, palabras clave temáticas y temas asociados.
 */
public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private String editorial;
    private int anioPublicacion;
    private String categoria;
    private Carrera carrera;
    private String clasificacionDewey; // Sistema de clasificación Dewey (ej. 624.15, 658.5)
    private String palabrasClave;       // Tags temáticos separados por comas
    private boolean esConsultaInterna;  // RN-03: Solo consulta en sala / Reserva
    private String ubicacionEstante;
    private List<Ejemplar> ejemplares;

    public Libro(String isbn, String titulo, String autor, String editorial, int anioPublicacion, 
                 String categoria, Carrera carrera, String clasificacionDewey, 
                 String palabrasClave, boolean esConsultaInterna, String ubicacionEstante) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.categoria = categoria;
        this.carrera = carrera;
        this.clasificacionDewey = clasificacionDewey;
        this.palabrasClave = palabrasClave;
        this.esConsultaInterna = esConsultaInterna;
        this.ubicacionEstante = ubicacionEstante;
        this.ejemplares = new ArrayList<>();
    }

    public int getCopiasTotales() {
        return ejemplares.size();
    }

    public int getCopiasDisponibles() {
        int count = 0;
        for (Ejemplar ej : ejemplares) {
            if (ej.getEstado() == EstadoLibro.DISPONIBLE) {
                count++;
            }
        }
        return count;
    }

    public void agregarEjemplar(Ejemplar ej) {
        this.ejemplares.add(ej);
    }

    public boolean coincideConPalabraClave(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) return true;
        String p = palabra.toLowerCase().trim();
        return (titulo != null && titulo.toLowerCase().contains(p)) ||
               (autor != null && autor.toLowerCase().contains(p)) ||
               (isbn != null && isbn.toLowerCase().contains(p)) ||
               (clasificacionDewey != null && clasificacionDewey.toLowerCase().contains(p)) ||
               (categoria != null && categoria.toLowerCase().contains(p)) ||
               (palabrasClave != null && palabrasClave.toLowerCase().contains(p));
    }

    // Getters y Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public String getClasificacionDewey() { return clasificacionDewey; }
    public void setClasificacionDewey(String clasificacionDewey) { this.clasificacionDewey = clasificacionDewey; }

    public String getPalabrasClave() { return palabrasClave; }
    public void setPalabrasClave(String palabrasClave) { this.palabrasClave = palabrasClave; }

    public boolean isEsConsultaInterna() { return esConsultaInterna; }
    public void setEsConsultaInterna(boolean esConsultaInterna) { this.esConsultaInterna = esConsultaInterna; }

    public String getUbicacionEstante() { return ubicacionEstante; }
    public void setUbicacionEstante(String ubicacionEstante) { this.ubicacionEstante = ubicacionEstante; }

    public List<Ejemplar> getEjemplares() { return ejemplares; }

    @Override
    public String toString() {
        return "[" + clasificacionDewey + "] " + titulo + " - " + autor + " (ISBN: " + isbn + ")";
    }
}
