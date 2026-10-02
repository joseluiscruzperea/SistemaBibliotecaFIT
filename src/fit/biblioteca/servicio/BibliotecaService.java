package fit.biblioteca.servicio;

import fit.biblioteca.modelo.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Servicio central del Sistema Integral de Biblioteca (FIT - UAT).
 * Incorpora:
 * - Clasificación Dewey y Tarjetas con folios únicos consecutivos.
 * - Plazo base de 2 días hábiles (si sale viernes regresa martes; fines de semana no cuentan).
 * - Renovaciones/Prórrogas (máximo 2 prórrogas de 2 días hábiles c/u, solicitadas solo con matrícula).
 * - Multa por retraso de $20.00 pesos diarios por día hábil.
 * - Regla de Cooldown: debe transcurrir al menos 1 día hábil para volver a pedir el mismo título.
 * - Registro de Entrada a Biblioteca (Recepción solo con matrícula: Consulta en Sala o Cómputo con horas).
 * - Recomendador de temas y búsqueda por palabras clave.
 */
public class BibliotecaService {
    private List<Usuario> usuarios;
    private List<Libro> libros;
    private List<Ejemplar> ejemplares;
    private List<Prestamo> prestamos;
    private List<Multa> multas;
    private List<CorteCaja> cortesCaja;
    private List<RegistroAcceso> accesosBiblioteca;

    // Control de Cooldown (Matrícula + "_" + ISBN -> Fecha de devolución)
    private Map<String, LocalDate> registroCooldownDevoluciones;

    // Pila para "Deshacer último" (CA-03)
    private Prestamo ultimoPrestamoRegistrado;

    // Corte de caja del turno actual
    private CorteCaja corteCajaActual;

    private int consecutivoPrestamo = 1;
    private int consecutivoMulta = 1;
    private int consecutivoCorte = 1;
    private int consecutivoAcceso = 1;
    private int consecutivoTarjeta = 1;

    public BibliotecaService() {
        this.usuarios = new ArrayList<>();
        this.libros = new ArrayList<>();
        this.ejemplares = new ArrayList<>();
        this.prestamos = new ArrayList<>();
        this.multas = new ArrayList<>();
        this.cortesCaja = new ArrayList<>();
        this.accesosBiblioteca = new ArrayList<>();
        this.registroCooldownDevoluciones = new HashMap<>();
        this.corteCajaActual = new CorteCaja("CORTE-" + String.format("%03d", consecutivoCorte++), "Operador Activo");
    }

    public String generarSiguienteFolioTarjeta() {
        return "TARJ-" + String.format("%04d", consecutivoTarjeta++);
    }

    // ==========================================
    // BÚSQUEDAS Y RECOMENDADOR DE TEMAS
    // ==========================================

    public Optional<Usuario> buscarUsuario(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) return Optional.empty();
        String m = matricula.trim().toUpperCase();
        return usuarios.stream()
                .filter(u -> u.getMatricula().equalsIgnoreCase(m))
                .findFirst();
    }

    public Optional<Ejemplar> buscarEjemplar(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) return Optional.empty();
        String c = criterio.trim().toUpperCase();
        return ejemplares.stream()
                .filter(e -> e.getCodigoBarras().equalsIgnoreCase(c) || 
                             e.getFolioTarjeta().equalsIgnoreCase(c))
                .findFirst();
    }

    public List<Libro> buscarLibrosPorPalabraClave(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return new ArrayList<>(libros);
        }
        List<Libro> res = new ArrayList<>();
        for (Libro lib : libros) {
            if (lib.coincideConPalabraClave(palabra)) {
                res.add(lib);
            }
        }
        return res;
    }

    public List<String> getTemasRecomendados() {
        return Arrays.asList(
            "TODOS LOS TEMAS",
            "Geotecnia y Mecánica de Suelos",
            "Estructuras y Concreto Reforzado",
            "Topografía y Geomática",
            "Investigación de Operaciones",
            "Control Estadístico de la Calidad",
            "Manufactura y Ergonomía",
            "Comercio Internacional e Incoterms",
            "Administración Estratégica y Finanzas",
            "Cálculo Diferencial e Integral",
            "Física Universitaria"
        );
    }

    // ==========================================
    // VALIDACIÓN DE REGLAS DE NEGOCIO (PRESTAMO)
    // ==========================================

    public ResultadoValidacion validarPrestamo(Usuario usuario, Ejemplar ejemplar) {
        if (usuario == null) {
            return ResultadoValidacion.fallo("RN-00", "El usuario no se encuentra registrado en el padrón de la FIT.");
        }

        if (!usuario.isActivo()) {
            return ResultadoValidacion.fallo("RN-00", "El usuario está inactivo o suspendido en Servicios Escolares.");
        }

        // RN-04 / Bloqueo automático por multas pendientes
        if (usuario.tieneAdeudo()) {
            return ResultadoValidacion.fallo("RN-04", 
                String.format("BLOQUEADO: El usuario cuenta con adeudo de $%.2f en multas pendientes. No se autoriza préstamo.", 
                usuario.getSaldoPendiente()));
        }

        // RN-01: Límite estricto de ejemplares
        if (usuario.alcanzaLimiteLibros()) {
            return ResultadoValidacion.fallo("RN-01", 
                String.format("LÍMITE ALCANZADO: El %s ya tiene %d libros en préstamo (Límite: %d).", 
                usuario.getTipo().getDescripcion(), 
                usuario.getCantidadPrestamosActivos(), 
                usuario.getTipo().getLimiteLibros()));
        }

        if (ejemplar == null) {
            return ResultadoValidacion.fallo("RN-00", "El código o tarjeta del ejemplar no existe en el catálogo.");
        }

        // RN-03: Consulta Interna / Reserva no prestable a domicilio
        if (ejemplar.getLibro().isEsConsultaInterna()) {
            return ResultadoValidacion.fallo("RN-03", 
                "RESTRICCIÓN: Los ejemplares de 'Consulta Interna' o 'Reserva' (Dewey Sala) NO pueden ser prestados a domicilio.");
        }

        // Disponibilidad física
        if (ejemplar.getEstado() != EstadoLibro.DISPONIBLE) {
            return ResultadoValidacion.fallo("RN-00", 
                "EJEMPLAR NO DISPONIBLE: El ejemplar está actualmente con estatus [" + ejemplar.getEstado().getEtiqueta() + "].");
        }

        // Regla de Cooldown: Debe pasar al menos 1 día hábil para volver a pedir el mismo libro
        String claveCooldown = usuario.getMatricula() + "_" + ejemplar.getLibro().getIsbn();
        LocalDate ultimaDev = registroCooldownDevoluciones.get(claveCooldown);
        if (ultimaDev != null && !LocalDate.now().isAfter(ultimaDev)) {
            return ResultadoValidacion.fallo("RN-COOLDOWN", 
                "REGLA DE ENFRIAMIENTO (COOLDOWN): Debe transcurrir al menos 1 día hábil desde la devolución previa para volver a solicitar el mismo libro.");
        }

        return ResultadoValidacion.ok("Aprobado: El préstamo cumple con todas las políticas y directrices.");
    }

    // ==========================================
    // EJECUCIÓN DE PRÉSTAMO (2 DÍAS HÁBILES)
    // ==========================================

    public Prestamo registrarPrestamo(Usuario usuario, Ejemplar ejemplar, String operador, LocalDate fechaPrestamo) {
        ResultadoValidacion val = validarPrestamo(usuario, ejemplar);
        if (!val.isAprobado()) {
            throw new IllegalStateException(val.getMensaje());
        }

        if (fechaPrestamo == null) {
            fechaPrestamo = LocalDate.now();
        }

        // Regla: 2 DÍAS HÁBILES. Fines de semana no cuentan.
        // Si sale viernes (o en fin de semana), se regresa el martes.
        LocalDate fechaLimite = calcularFechaVencimientoHabiles(fechaPrestamo, 2);

        String id = "PR-" + LocalDate.now().getYear() + "-" + String.format("%04d", consecutivoPrestamo++);
        Prestamo p = new Prestamo(id, usuario, ejemplar, fechaPrestamo, fechaLimite, operador);

        // Actualizar estados
        ejemplar.setEstado(EstadoLibro.PRESTADO);
        ejemplar.setPrestamoActivo(p);
        usuario.getPrestamosActivos().add(p);
        prestamos.add(p);

        // Guardar referencia para Deshacer
        this.ultimoPrestamoRegistrado = p;

        return p;
    }

    /**
     * Calcula la fecha de vencimiento sumando N días hábiles (brincando sábados y domingos).
     * Si fechaInicio es viernes:
     *   sábado (no), domingo (no) -> lunes (día 1) -> martes (día 2). Devuelve Martes.
     * Si fechaInicio es sábado o domingo:
     *   lunes (día 1) -> martes (día 2). Devuelve Martes.
     */
    public LocalDate calcularFechaVencimientoHabiles(LocalDate fechaInicio, int diasHabiles) {
        LocalDate cur = fechaInicio;
        int cont = 0;
        while (cont < diasHabiles) {
            cur = cur.plusDays(1);
            DayOfWeek dow = cur.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                cont++;
            }
        }
        return cur;
    }

    public boolean deshacerUltimoPrestamo() {
        if (ultimoPrestamoRegistrado == null) return false;
        if (ultimoPrestamoRegistrado.getEstado() != EstadoPrestamo.ACTIVO) return false;

        ultimoPrestamoRegistrado.setEstado(EstadoPrestamo.CANCELADO);
        ultimoPrestamoRegistrado.getEjemplar().setEstado(EstadoLibro.DISPONIBLE);
        ultimoPrestamoRegistrado.getEjemplar().setPrestamoActivo(null);
        ultimoPrestamoRegistrado.getUsuario().getPrestamosActivos().remove(ultimoPrestamoRegistrado);
        
        ultimoPrestamoRegistrado = null;
        return true;
    }

    // ==========================================
    // PRÓRROGAS O RENOVACIONES (MÁXIMO 2)
    // "Cuando viene a pedir prórroga no tiene que venir con el libro, solo con la matrícula"
    // ==========================================

    public Prestamo aplicarProrroga(Prestamo prestamo) {
        if (prestamo == null || prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new IllegalArgumentException("El préstamo no está activo.");
        }
        if (!prestamo.puedeSolicitarProrroga()) {
            throw new IllegalStateException("Límite de prórrogas alcanzado (Máximo 2 permitidas). Debe devolver el ejemplar.");
        }
        if (prestamo.estaVencido(LocalDate.now())) {
            throw new IllegalStateException("El préstamo se encuentra vencido. No se puede prorrogar un libro con mora; debe acudir a liquidar.");
        }

        // Cada prórroga otorga 2 días hábiles adicionales
        LocalDate nuevaFecha = calcularFechaVencimientoHabiles(prestamo.getFechaDevolucionEsperada(), 2);
        prestamo.aplicarProrroga(nuevaFecha);
        return prestamo;
    }

    // ==========================================
    // DEVOLUCIÓN Y SANCIONES ($20/DÍA HÁBIL)
    // ==========================================

    public Optional<Prestamo> buscarPrestamoActivoPorEjemplar(String codigoOFolio) {
        String c = codigoOFolio.trim();
        return prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.ACTIVO && 
                            (p.getEjemplar().getCodigoBarras().equalsIgnoreCase(c) ||
                             p.getEjemplar().getFolioTarjeta().equalsIgnoreCase(c)))
                .findFirst();
    }

    public Prestamo registrarDevolucion(Prestamo prestamo, EstadoLibro estadoRetorno, String operador, LocalDate fechaDevolucion) {
        if (prestamo == null || prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new IllegalArgumentException("El préstamo no es válido o ya fue cerrado.");
        }

        if (fechaDevolucion == null) {
            fechaDevolucion = LocalDate.now();
        }

        prestamo.setFechaDevolucionReal(fechaDevolucion);

        // Calcular días hábiles de retraso
        int diasRetraso = prestamo.calcularDiasHabilesRetraso(fechaDevolucion);
        prestamo.setDiasHabilesRetraso(diasRetraso);

        if (diasRetraso > 0) {
            prestamo.setEstado(EstadoPrestamo.DEVUELTO_CON_RETRASO);
            double montoMulta = diasRetraso * Prestamo.TARIFA_MULTA_DIARIA; // $20 pesos diarios por día hábil
            String idMulta = "MUL-" + String.format("%04d", consecutivoMulta++);
            String motivo = "Retraso de " + diasRetraso + " día(s) hábil(es) en devolución del libro: " + 
                            prestamo.getEjemplar().getLibro().getTitulo();
            
            Multa m = new Multa(idMulta, prestamo, prestamo.getUsuario(), montoMulta, fechaDevolucion, motivo);
            prestamo.setMultaAsociada(m);
            multas.add(m);
            prestamo.getUsuario().getHistorialMultas().add(m);
        } else {
            prestamo.setEstado(EstadoPrestamo.DEVUELTO_A_TIEMPO);
        }

        // Registrar cooldown para que deba pasar 1 día hábil para volver a pedir el mismo título
        String claveCooldown = prestamo.getUsuario().getMatricula() + "_" + prestamo.getEjemplar().getLibro().getIsbn();
        registroCooldownDevoluciones.put(claveCooldown, fechaDevolucion);

        // Actualizar ejemplar y usuario
        prestamo.getEjemplar().setEstado(estadoRetorno);
        prestamo.getEjemplar().setPrestamoActivo(null);
        prestamo.getUsuario().getPrestamosActivos().remove(prestamo);

        return prestamo;
    }

    // ==========================================
    // COBRO Y CONDONACIÓN DE MULTAS
    // ==========================================

    public boolean cobrarMulta(String idMulta, String cajero) {
        for (Multa m : multas) {
            if (m.getIdMulta().equalsIgnoreCase(idMulta) && m.getEstado() == EstadoMulta.PENDIENTE) {
                m.registrarPago(cajero, LocalDate.now());
                corteCajaActual.agregarCobro(m);
                return true;
            }
        }
        return false;
    }

    public boolean condonarMulta(String idMulta, String motivo, String autorizadorJefatura) {
        for (Multa m : multas) {
            if (m.getIdMulta().equalsIgnoreCase(idMulta) && m.getEstado() == EstadoMulta.PENDIENTE) {
                m.condonar(motivo, autorizadorJefatura);
                return true;
            }
        }
        return false;
    }

    // ==========================================
    // REGISTRO DE ENTRADA / ACCESO A BIBLIOTECA
    // "Cuando los usuarios llegan se registran con su matrícula nada más"
    // "captura la matrícula y te da consulta o computadora con horas asignadas"
    // ==========================================

    public RegistroAcceso registrarAccesoBiblioteca(Usuario usuario, TipoServicio tipo, String pcId, int horas) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no válido.");
        }
        String idAcceso = "ACC-" + String.format("%04d", consecutivoAcceso++);
        RegistroAcceso acc = new RegistroAcceso(idAcceso, usuario, tipo, pcId, horas);
        accesosBiblioteca.add(0, acc); // Agregar al inicio para ver los más recientes
        return acc;
    }

    public void finalizarSesionComputadora(String idAcceso) {
        for (RegistroAcceso a : accesosBiblioteca) {
            if (a.getIdAcceso().equalsIgnoreCase(idAcceso) && a.isActivo()) {
                a.finalizarSesion();
                return;
            }
        }
    }

    // ==========================================
    // CORTE DE CAJA POR TURNO
    // ==========================================

    public CorteCaja cerrarTurnoCorteCaja(String responsable) {
        CorteCaja corteCerrado = this.corteCajaActual;
        cortesCaja.add(corteCerrado);
        this.corteCajaActual = new CorteCaja("CORTE-" + String.format("%03d", consecutivoCorte++), responsable);
        return corteCerrado;
    }

    public CorteCaja getCorteCajaActual() {
        return corteCajaActual;
    }

    // ==========================================
    // CONSTANCIA DE NO ADEUDO
    // ==========================================

    public String generarCartaNoAdeudo(Usuario u) {
        if (u == null) return "ERROR: Usuario no válido.";
        if (u.tieneAdeudo()) {
            return "DENEGADO: El usuario presenta adeudos pendientes de $" + 
                   String.format("%.2f", u.getSaldoPendiente()) + ". No es posible expedir la carta.";
        }
        if (u.getCantidadPrestamosActivos() > 0) {
            return "DENEGADO: El usuario aún tiene " + u.getCantidadPrestamosActivos() + 
                   " libro(s) en su poder sin devolver. No es posible expedir la carta.";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"));
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================================\n");
        sb.append("           UNIVERSIDAD AUTÓNOMA DE TAMAULIPAS (UAT)                    \n");
        sb.append("                  FACULTAD DE INGENIERÍA (FIT)                         \n");
        sb.append("               COORDINACIÓN DEL SISTEMA DE BIBLIOTECA                  \n");
        sb.append("=======================================================================\n\n");
        sb.append("ASUNTO: CONSTANCIA OFICIAL DE NO ADEUDO BIBLIOGRÁFICO\n\n");
        sb.append("A QUIEN CORRESPONDA:\n\n");
        sb.append("Por medio de la presente, la Jefatura de Biblioteca de la Facultad de\n");
        sb.append("Ingeniería hace constar que el (la) C.:\n\n");
        sb.append(String.format("  NOMBRE   : %s\n", u.getNombreCompleto().toUpperCase()));
        sb.append(String.format("  MATRÍCULA: %s\n", u.getMatricula()));
        sb.append(String.format("  CARRERA  : %s\n", u.getCarrera().getNombreCompleto()));
        sb.append(String.format("  TIPO     : %s\n\n", u.getTipo().getDescripcion()));
        sb.append("A la fecha de emisión del presente documento NO REGISTRA ADEUDOS DE\n");
        sb.append("MATERIAL BIBLIOGRÁFICO NI SANCIONES ECONÓMICAS PENDIENTES en este acervo.\n\n");
        sb.append("Se expide la presente para trámites académico-administrativos y de Titulación.\n\n");
        sb.append(String.format("Tampico, Tamaulipas, a %s.\n\n\n", LocalDate.now().format(dtf)));
        sb.append("             _____________________________________________\n");
        sb.append("                          Getsel Giadans                  \n");
        sb.append("                        Jefa de Biblioteca                \n");
        sb.append("                   Facultad de Ingeniería - UAT           \n");
        sb.append("=======================================================================\n");
        return sb.toString();
    }

    public File exportarArchivoTexto(String nombreBase, String contenido) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = nombreBase + "_" + timestamp + ".txt";
        File dir = new File("reportes_generados");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File(dir, fileName);
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(contenido);
        }
        return f;
    }

    public Map<Carrera, Long> obtenerPrestamosPorCarrera() {
        Map<Carrera, Long> mapa = new EnumMap<>(Carrera.class);
        for (Carrera c : Carrera.values()) {
            mapa.put(c, 0L);
        }
        for (Prestamo p : prestamos) {
            Carrera c = p.getUsuario().getCarrera();
            mapa.put(c, mapa.get(c) + 1);
        }
        return mapa;
    }

    public List<Multa> getMultasPendientes() {
        List<Multa> res = new ArrayList<>();
        for (Multa m : multas) {
            if (m.getEstado() == EstadoMulta.PENDIENTE) {
                res.add(m);
            }
        }
        return res;
    }

    // Getters de colecciones
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Libro> getLibros() { return libros; }
    public List<Ejemplar> getEjemplares() { return ejemplares; }
    public List<Prestamo> getPrestamos() { return prestamos; }
    public List<Multa> getMultas() { return multas; }
    public List<CorteCaja> getCortesCaja() { return cortesCaja; }
    public List<RegistroAcceso> getAccesosBiblioteca() { return accesosBiblioteca; }
    public Prestamo getUltimoPrestamoRegistrado() { return ultimoPrestamoRegistrado; }
}
