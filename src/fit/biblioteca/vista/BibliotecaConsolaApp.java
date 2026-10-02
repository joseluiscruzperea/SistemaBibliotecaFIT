package fit.biblioteca.vista;

import fit.biblioteca.datos.DatosIniciales;
import fit.biblioteca.modelo.*;
import fit.biblioteca.servicio.BibliotecaService;
import fit.biblioteca.servicio.ResultadoValidacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Modo Consola del Sistema Integral de Biblioteca (FIT - UAT),
 * actualizado con todas las observaciones del comité evaluador:
 * - Clasificación Dewey y tarjetas únicas.
 * - Préstamos a 2 días hábiles (fines de semana excluidos).
 * - Prórrogas solicitadas solo con matrícula (máximo 2 prórrogas).
 * - Multa actualizada a $20.00 pesos por día hábil.
 * - Registro de entrada / Cómputo con horas.
 */
public class BibliotecaConsolaApp {

    private final BibliotecaService service;
    private final Scanner scanner;

    public BibliotecaConsolaApp() {
        this.service = new BibliotecaService();
        this.scanner = new Scanner(System.in);
        DatosIniciales.cargarDatos(service);
    }

    public void iniciar() {
        int opcion = -1;
        do {
            mostrarMenuPrincipal();
            System.out.print("Seleccione una opción: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1: menuVentanillaPrestamos(); break;
                case 2: menuProrrogasSoloMatricula(); break;
                case 3: menuDevolucionesYMultas(); break;
                case 4: menuCatalogoDeweyYTemas(); break;
                case 5: menuRegistroAccesoYComputo(); break;
                case 6: menuPadronLectores(); break;
                case 7: menuConstanciaNoAdeudo(); break;
                case 8: menuCorteCaja(); break;
                case 9: menuReportesGerenciales(); break;
                case 10:
                    System.out.println("\nSaliendo del Sistema de Biblioteca FIT... ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.\n");
            }
        } while (opcion != 10);
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n==========================================================================");
        System.out.println("   SISTEMA INTEGRAL DE BIBLIOTECA - FACULTAD DE INGENIERÍA (FIT · UAT)   ");
        System.out.println("     Dewey · Tarjetas Consecutivas · 2 Días Hábiles · Multa $20/día       ");
        System.out.println("==========================================================================");
        System.out.println("1. Ventanilla Express de Préstamos (2 Días Hábiles · Tarjeta Dewey)");
        System.out.println("2. Prórrogas / Renovaciones (Solo Matrícula · Máximo 2)");
        System.out.println("3. Devoluciones y Cobro de Multas ($20.00 Pesos por Día Hábil)");
        System.out.println("4. Catálogo Dewey y Búsqueda por Palabras Clave / Temas");
        System.out.println("5. Registro de Entrada (Solo Matrícula · Consulta o Cómputo con Horas)");
        System.out.println("6. Padrón Institucional de Lectores (Alumnos / Docentes)");
        System.out.println("7. Emisión de Constancia de No Adeudo (Titulación)");
        System.out.println("8. Corte de Caja de Turno de Mostrador");
        System.out.println("9. Módulo de Reportes Estadísticos y Exportación (.txt)");
        System.out.println("10. Salir");
        System.out.println("==========================================================================");
    }

    private void menuVentanillaPrestamos() {
        System.out.println("\n--- 1. VENTANILLA EXPRESS DE PRÉSTAMOS (2 DÍAS HÁBILES) ---");
        System.out.print("Ingrese o escanee la MATRÍCULA del usuario: ");
        String mat = scanner.nextLine().trim();

        Optional<Usuario> uOpt = service.buscarUsuario(mat);
        if (!uOpt.isPresent()) {
            System.out.println("❌ ERROR: La matrícula '" + mat + "' no existe en el padrón de la FIT.");
            return;
        }

        Usuario u = uOpt.get();
        System.out.println("\nLector identificado: " + u.getNombreCompleto() + " (" + u.getTipo() + " - " + u.getCarrera().getNombreCompleto() + ")");
        System.out.println("Semáforo del lector: [" + u.getEstadoSemaforo() + "]");
        System.out.println("Libros prestados actualmente: " + u.getCantidadPrestamosActivos() + " de " + u.getTipo().getLimiteLibros() + " permitidos.");

        if (u.tieneAdeudo()) {
            System.out.println("🔴 BLOQUEO POR REGLA RN-04: El usuario tiene adeudos pendientes por $" + 
                               String.format("%.2f", u.getSaldoPendiente()) + ". No se permite prestar.");
            return;
        }

        if (u.alcanzaLimiteLibros()) {
            System.out.println("🟡 BLOQUEO POR REGLA RN-01: Límite de libros alcanzado. Debe devolver material.");
            return;
        }

        System.out.print("\nIngrese Folio de TARJETA DEWEY o CÓDIGO del libro (ej. TARJ-0001, CIV-101): ");
        String cod = scanner.nextLine().trim();
        Optional<Ejemplar> ejOpt = service.buscarEjemplar(cod);
        if (!ejOpt.isPresent()) {
            System.out.println("❌ ERROR: La tarjeta o código '" + cod + "' no existe en la biblioteca.");
            return;
        }

        Ejemplar ej = ejOpt.get();
        ResultadoValidacion val = service.validarPrestamo(u, ej);
        if (!val.isAprobado()) {
            System.out.println("❌ OPERACIÓN RECHAZADA: " + val.getMensaje());
            return;
        }

        try {
            Prestamo p = service.registrarPrestamo(u, ej, "Mariana Rojas", LocalDate.now());
            System.out.println("\n✅ ¡PRÉSTAMO REGISTRADO CON ÉXITO!");
            System.out.println("Folio de Préstamo : " + p.getIdPrestamo());
            System.out.println("Folio Tarjeta     : " + ej.getFolioTarjeta() + " [ÚNICO CONSECUTIVO]");
            System.out.println("Clasif. Dewey     : " + ej.getLibro().getClasificacionDewey());
            System.out.println("FECHA DE ENTREGA  : " + p.getFechaDevolucionEsperada() + " (2 Días Hábiles sin fines de semana)");
            System.out.println("Libro Asignado    : " + ej.getLibro().getTitulo());
            System.out.println("Ubicación Física  : " + ej.getLibro().getUbicacionEstante());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void menuProrrogasSoloMatricula() {
        System.out.println("\n--- 2. PRÓRROGAS / RENOVACIONES (SOLO CON MATRÍCULA) ---");
        System.out.println("ℹ️ El alumno no requiere traer el libro físicamente.");
        System.out.print("Ingrese la matrícula del alumno: ");
        String mat = scanner.nextLine().trim();

        Optional<Usuario> uOpt = service.buscarUsuario(mat);
        if (!uOpt.isPresent()) {
            System.out.println("❌ Matrícula no encontrada.");
            return;
        }

        Usuario u = uOpt.get();
        List<Prestamo> activos = u.getPrestamosActivos();
        if (activos.isEmpty()) {
            System.out.println("El alumno " + u.getNombreCompleto() + " no tiene préstamos activos.");
            return;
        }

        System.out.println("\nPréstamos de " + u.getNombreCompleto() + ":");
        for (int i = 0; i < activos.size(); i++) {
            Prestamo p = activos.get(i);
            System.out.printf("[%d] %s | %s | Vence: %s | Prórrogas: %d/2\n",
                (i + 1), p.getIdPrestamo(), p.getEjemplar().getLibro().getTitulo(),
                p.getFechaDevolucionEsperada(), p.getNumeroProrrogas());
        }

        System.out.print("\nSeleccione el número del préstamo a prorrogar: ");
        try {
            int sel = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (sel >= 0 && sel < activos.size()) {
                Prestamo p = activos.get(sel);
                service.aplicarProrroga(p);
                System.out.println("✅ Prórroga aplicada con éxito (+2 días hábiles adicionales).");
                System.out.println("Nueva fecha límite de entrega: " + p.getFechaDevolucionEsperada());
                System.out.println("Prórrogas utilizadas: " + p.getNumeroProrrogas() + " de 2 permitidas.");
            } else {
                System.out.println("Selección inválida.");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        }
    }

    private void menuDevolucionesYMultas() {
        System.out.println("\n--- 3. DEVOLUCIONES Y COBRO DE MULTAS ($20/DÍA HÁBIL) ---");
        System.out.print("Ingrese Folio de Tarjeta o Código del libro devuelto: ");
        String cod = scanner.nextLine().trim();

        Optional<Prestamo> pOpt = service.buscarPrestamoActivoPorEjemplar(cod);
        if (!pOpt.isPresent()) {
            System.out.println("❌ No se encontró ningún préstamo activo para: " + cod);
            return;
        }

        Prestamo p = pOpt.get();
        System.out.println("Préstamo: " + p.getIdPrestamo() + " | Lector: " + p.getUsuario().getNombreCompleto());
        System.out.println("Fecha Límite: " + p.getFechaDevolucionEsperada());

        Prestamo dev = service.registrarDevolucion(p, EstadoLibro.DISPONIBLE, "Mariana Rojas", LocalDate.now());
        if (dev.getDiasHabilesRetraso() > 0) {
            System.out.println("⚠️ ATENCIÓN: El libro fue devuelto con " + dev.getDiasHabilesRetraso() + " días hábiles de mora.");
            System.out.println("🔴 Multa calculada a $20/día: $" + String.format("%.2f", dev.getMultaAsociada().getMonto()));
            System.out.println("Folio de Multa: " + dev.getMultaAsociada().getIdMulta());
            System.out.print("¿Desea registrar el pago en caja ahora? [S/N]: ");
            String pagar = scanner.nextLine().trim();
            if (pagar.equalsIgnoreCase("S")) {
                service.cobrarMulta(dev.getMultaAsociada().getIdMulta(), "Mariana Rojas");
                System.out.println("💵 Multa liquidada en caja. Alumno desbloqueado.");
            }
        } else {
            System.out.println("✅ Devolución registrada a tiempo. Sin sanciones.");
        }
        System.out.println("ℹ️ Aplica regla de enfriamiento: El usuario debe esperar al menos 1 día hábil para volver a pedir este título.");
    }

    private void menuCatalogoDeweyYTemas() {
        System.out.println("\n--- 4. CATÁLOGO DEWEY Y BÚSQUEDA POR PALABRAS CLAVE ---");
        System.out.print("Ingrese término o palabra clave de búsqueda (o presione ENTER para ver todos): ");
        String busqueda = scanner.nextLine().trim();

        List<Libro> lista = service.buscarLibrosPorPalabraClave(busqueda);
        System.out.printf("%-12s %-16s %-32s %-15s %-10s %-10s\n", "Dewey", "ISBN", "Título", "Carrera", "Estante", "Disp/Total");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Libro l : lista) {
            System.out.printf("%-12s %-16s %-32s %-15s %-10s %d/%d\n",
                l.getClasificacionDewey(),
                l.getIsbn(),
                truncar(l.getTitulo(), 30),
                l.getCarrera().getAbreviatura(),
                l.getUbicacionEstante(),
                l.getCopiasDisponibles(),
                l.getCopiasTotales());
        }
    }

    private void menuRegistroAccesoYComputo() {
        System.out.println("\n--- 5. REGISTRO DE ENTRADA (SALA Y CENTRO DE CÓMPUTO) ---");
        System.out.print("Ingrese la matrícula del visitante: ");
        String mat = scanner.nextLine().trim();

        Optional<Usuario> uOpt = service.buscarUsuario(mat);
        if (!uOpt.isPresent()) {
            System.out.println("❌ Matrícula no encontrada.");
            return;
        }
        Usuario u = uOpt.get();

        System.out.println("Seleccione tipo de servicio:");
        System.out.println("1. Consulta en Sala / Cubículo de Lectura");
        System.out.println("2. Uso de Computadora en Centro de Cómputo");
        System.out.print("Opción: ");
        String opc = scanner.nextLine().trim();

        if (opc.equals("2")) {
            System.out.print("Ingrese número de PC asignada (ej. PC-01 a PC-15): ");
            String pc = scanner.nextLine().trim().toUpperCase();
            System.out.print("Ingrese cantidad de horas autorizadas (ej. 1, 2 o 3): ");
            int horas = Integer.parseInt(scanner.nextLine().trim());
            RegistroAcceso acc = service.registrarAccesoBiblioteca(u, TipoServicio.USO_COMPUTADORA, pc, horas);
            System.out.println("✅ Asignación exitosa: " + pc + " a " + u.getNombreCompleto() + " por " + horas + " horas.");
            System.out.println("Hora límite de entrega de equipo: " + acc.getHoraLimiteFormateada());
        } else {
            service.registrarAccesoBiblioteca(u, TipoServicio.CONSULTA_SALA, null, 0);
            System.out.println("✅ Acceso a sala registrado para: " + u.getNombreCompleto());
        }
    }

    private void menuPadronLectores() {
        System.out.println("\n--- 6. PADRÓN INSTITUCIONAL DE LECTORES ---");
        System.out.printf("%-10s %-25s %-10s %-12s %-10s %-10s\n", "Matrícula", "Nombre", "Tipo", "Carrera", "Préstamos", "Multas ($)");
        System.out.println("----------------------------------------------------------------------------------");
        for (Usuario u : service.getUsuarios()) {
            System.out.printf("%-10s %-25s %-10s %-12s %d/%d       $%-9.2f\n",
                u.getMatricula(),
                truncar(u.getNombreCompleto(), 23),
                u.getTipo().getDescripcion(),
                u.getCarrera().getAbreviatura(),
                u.getCantidadPrestamosActivos(),
                u.getTipo().getLimiteLibros(),
                u.getSaldoPendiente());
        }
    }

    private void menuConstanciaNoAdeudo() {
        System.out.println("\n--- 7. CONSTANCIA DE NO ADEUDO BIBLIOGRÁFICO ---");
        System.out.print("Ingrese la matrícula del solicitante: ");
        String mat = scanner.nextLine().trim();
        Optional<Usuario> uOpt = service.buscarUsuario(mat);
        if (!uOpt.isPresent()) {
            System.out.println("❌ Usuario no encontrado.");
            return;
        }
        Usuario u = uOpt.get();
        System.out.println("\n" + service.generarCartaNoAdeudo(u));
    }

    private void menuCorteCaja() {
        System.out.println("\n--- 8. CORTE DE CAJA POR TURNO ---");
        CorteCaja corte = service.getCorteCajaActual();
        System.out.println(corte.generarTicketTexto());
        System.out.print("¿Desea cerrar el turno y abrir una nueva sesión de mostrador? [S/N]: ");
        String r = scanner.nextLine().trim();
        if (r.equalsIgnoreCase("S")) {
            service.cerrarTurnoCorteCaja("Mariana Rojas");
            System.out.println("✅ Turno cerrado exitosamente.");
        }
    }

    private void menuReportesGerenciales() {
        System.out.println("\n--- 9. REPORTES GERENCIALES Y EXPORTACIÓN ---");
        System.out.println("A. Métricas de Demanda por Carrera (Civil, Industrial, Negocios)");
        System.out.println("B. Listado Oficial de Lectores Morosos");
        System.out.print("Seleccione: ");
        String op = scanner.nextLine().trim().toUpperCase();

        if (op.equals("A")) {
            Map<Carrera, Long> stats = service.obtenerPrestamosPorCarrera();
            System.out.println("\nDISTRIBUCIÓN DE PRÉSTAMOS POR CARRERA:");
            for (Map.Entry<Carrera, Long> entry : stats.entrySet()) {
                System.out.println(" * " + entry.getKey().getNombreCompleto() + ": " + entry.getValue() + " préstamos.");
            }
        } else if (op.equals("B")) {
            System.out.println("\nLECTORES CON MOROSIDAD ACTIVA ($20.00/día):");
            for (Usuario u : service.getUsuarios()) {
                if (u.tieneAdeudo()) {
                    System.out.printf(" - %s (%s, %s): Adeudo de $%.2f\n",
                        u.getNombreCompleto(), u.getMatricula(), u.getCarrera().getAbreviatura(), u.getSaldoPendiente());
                }
            }
        }
    }

    private String truncar(String str, int max) {
        if (str == null) return "";
        return str.length() <= max ? str : str.substring(0, max - 3) + "...";
    }

    public static void main(String[] args) {
        new BibliotecaConsolaApp().iniciar();
    }
}
