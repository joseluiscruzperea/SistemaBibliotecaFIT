package fit.biblioteca.controlador;

import fit.biblioteca.datos.DatosIniciales;
import fit.biblioteca.modelo.*;
import fit.biblioteca.servicio.BibliotecaService;
import fit.biblioteca.servicio.ResultadoValidacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador principal del patrón MVC en JavaFX.
 * Gestiona el menú lateral (sidebar), navegación entre vistas y lógica operativa:
 * - Menú lateral moderno (Dashboard, Catálogo, Préstamos, Prórrogas, Devoluciones, Cómputo, Usuarios, Reportes).
 * - Catálogo idéntico a la interfaz del laptop (Dewey, Lector actual, Disponibilidad y filtros).
 * - Plazo de 2 días hábiles (viernes a martes), prórrogas solo con matrícula y multas a $20/día hábil.
 */
public class MainController implements Initializable {

    private BibliotecaService service;

    // Estado en memoria de la ventanilla actual
    private Usuario usuarioActualVentanilla;
    private Ejemplar ejemplarActualVentanilla;

    // ==========================================
    // INYECCIÓN DE COMPONENTES DEL MENÚ LATERAL Y TOP BAR
    // ==========================================
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavCatalogo;
    @FXML private Button btnNavPrestamos;
    @FXML private Button btnNavProrrogas;
    @FXML private Button btnNavDevoluciones;
    @FXML private Button btnNavComputo;
    @FXML private Button btnNavUsuarios;
    @FXML private Button btnNavReportes;

    @FXML private Label lblTopBarTitle;
    @FXML private Label lblFechaHeader;
    @FXML private Label lblOperadorHeader;
    @FXML private Label lblOperadorFooter;

    // ==========================================
    // VISTAS DEL STACKPANE CENTRAL
    // ==========================================
    @FXML private VBox viewDashboard;
    @FXML private VBox viewCatalogo;
    @FXML private VBox viewPrestamos;
    @FXML private VBox viewProrrogas;
    @FXML private VBox viewDevoluciones;
    @FXML private VBox viewComputo;
    @FXML private VBox viewUsuarios;
    @FXML private VBox viewReportes;

    // DASHBOARD KPI LABELS
    @FXML private Label lblKpiTotalLibros;
    @FXML private Label lblKpiPrestamosActivos;
    @FXML private Label lblKpiMultasPendientes;
    @FXML private Label lblKpiComputadorasUso;

    // VISTA 2: CATÁLOGO DE LIBROS (FOTO LAPTOP)
    @FXML private TextField txtFiltroCatalogo;
    @FXML private ComboBox<String> cbTemasRecomendados;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private CheckBox chkSoloDisponibles;
    @FXML private Label lblConteoResultados;
    @FXML private TableView<Libro> tblCatalogo;
    @FXML private TableColumn<Libro, String> colTit;
    @FXML private TableColumn<Libro, String> colCar;
    @FXML private TableColumn<Libro, String> colDewey;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colEst;
    @FXML private TableColumn<Libro, String> colDisp;
    @FXML private TableColumn<Libro, String> colLectorActual;

    // VISTA 3: VENTANILLA EXPRESS
    @FXML private TextField txtMatricula;
    @FXML private Label lblUsuarioInfo;
    @FXML private TextField txtCodigoBarras;
    @FXML private Label lblLibroInfo;
    @FXML private Button btnRegistrarPrestamo;
    @FXML private Button btnDeshacerUltimo;
    @FXML private VBox pnlSemaforo;
    @FXML private Label lblSemaforoTitulo;
    @FXML private Label lblSemaforoDetalle;
    @FXML private TextArea txtTicketVentanilla;

    // VISTA 4: PRÓRROGAS (SOLO MATRÍCULA)
    @FXML private TextField txtMatriculaProrroga;
    @FXML private TableView<Prestamo> tblPrestamosProrroga;
    @FXML private TableColumn<Prestamo, String> colProFolio;
    @FXML private TableColumn<Prestamo, String> colProMat;
    @FXML private TableColumn<Prestamo, String> colProLector;
    @FXML private TableColumn<Prestamo, String> colProLibro;
    @FXML private TableColumn<Prestamo, String> colProTarjeta;
    @FXML private TableColumn<Prestamo, String> colProFechaLim;
    @FXML private TableColumn<Prestamo, String> colProProrrogas;

    // VISTA 5: DEVOLUCIONES Y MULTAS
    @FXML private TextField txtDevCodigoBarras;
    @FXML private ComboBox<EstadoLibro> cbDevCondicion;
    @FXML private DatePicker dpDevFechaRetorno;
    @FXML private TableView<Prestamo> tblPrestamosActivos;
    @FXML private TableColumn<Prestamo, String> colFolio;
    @FXML private TableColumn<Prestamo, String> colMat;
    @FXML private TableColumn<Prestamo, String> colLector;
    @FXML private TableColumn<Prestamo, String> colLibro;
    @FXML private TableColumn<Prestamo, String> colCodigo;
    @FXML private TableColumn<Prestamo, String> colFechaPrest;
    @FXML private TableColumn<Prestamo, String> colFechaLimite;
    @FXML private TableColumn<Prestamo, String> colEstado;

    @FXML private TableView<Multa> tblMultas;
    @FXML private TableColumn<Multa, String> colMulId;
    @FXML private TableColumn<Multa, String> colMulMat;
    @FXML private TableColumn<Multa, String> colMulLector;
    @FXML private TableColumn<Multa, String> colMulMonto;
    @FXML private TableColumn<Multa, String> colMulMotivo;
    @FXML private TableColumn<Multa, String> colMulEstado;

    // VISTA 6: RECEPCIÓN Y CÓMPUTO
    @FXML private TextField txtMatriculaAcceso;
    @FXML private ComboBox<TipoServicio> cbTipoServicio;
    @FXML private Label lblPcTitulo;
    @FXML private ComboBox<String> cbComputadoras;
    @FXML private Label lblHorasTitulo;
    @FXML private Spinner<Integer> spnHorasPc;
    @FXML private TableView<RegistroAcceso> tblAccesos;
    @FXML private TableColumn<RegistroAcceso, String> colAccFolio;
    @FXML private TableColumn<RegistroAcceso, String> colAccMat;
    @FXML private TableColumn<RegistroAcceso, String> colAccNombre;
    @FXML private TableColumn<RegistroAcceso, String> colAccCarrera;
    @FXML private TableColumn<RegistroAcceso, String> colAccServicio;
    @FXML private TableColumn<RegistroAcceso, String> colAccPc;
    @FXML private TableColumn<RegistroAcceso, String> colAccEntrada;
    @FXML private TableColumn<RegistroAcceso, String> colAccLimite;
    @FXML private TableColumn<RegistroAcceso, String> colAccEstado;

    // VISTA 7: LECTORES Y NO ADEUDO
    @FXML private TableView<Usuario> tblLectores;
    @FXML private TableColumn<Usuario, String> colLecMat;
    @FXML private TableColumn<Usuario, String> colLecNom;
    @FXML private TableColumn<Usuario, String> colLecTipo;
    @FXML private TableColumn<Usuario, String> colLecCar;
    @FXML private TableColumn<Usuario, String> colLecEmail;
    @FXML private TableColumn<Usuario, String> colLecLibros;
    @FXML private TableColumn<Usuario, String> colLecAdeudo;
    @FXML private TableColumn<Usuario, String> colLecSem;
    @FXML private TextArea txtCartaNoAdeudo;

    // VISTA 8: REPORTES Y CORTE
    @FXML private TextArea txtReporteSalida;

    // Listas observables
    private ObservableList<Prestamo> olPrestamos;
    private ObservableList<Prestamo> olPrestamosProrroga;
    private ObservableList<Multa> olMultas;
    private ObservableList<Libro> olLibros;
    private ObservableList<Usuario> olUsuarios;
    private ObservableList<RegistroAcceso> olAccesos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        service = new BibliotecaService();
        DatosIniciales.cargarDatos(service);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblFechaHeader.setText("📅 " + LocalDate.now().format(dtf));

        olPrestamos = FXCollections.observableArrayList(service.getPrestamos());
        olPrestamosProrroga = FXCollections.observableArrayList();
        olMultas = FXCollections.observableArrayList(service.getMultasPendientes());
        olLibros = FXCollections.observableArrayList(service.getLibros());
        olUsuarios = FXCollections.observableArrayList(service.getUsuarios());
        olAccesos = FXCollections.observableArrayList(service.getAccesosBiblioteca());

        configurarTablaPrestamos();
        configurarTablaProrrogas();
        configurarTablaMultas();
        configurarTablaCatalogo();
        configurarTablaLectores();
        configurarTablaAccesos();

        // Configuración de Devolución
        cbDevCondicion.setItems(FXCollections.observableArrayList(EstadoLibro.DISPONIBLE, EstadoLibro.EN_REPARACION));
        cbDevCondicion.setValue(EstadoLibro.DISPONIBLE);
        dpDevFechaRetorno.setValue(LocalDate.now());

        // Configuración de Filtros de Catálogo
        cbTemasRecomendados.setItems(FXCollections.observableArrayList(service.getTemasRecomendados()));
        cbTemasRecomendados.setValue("TODOS LOS TEMAS");

        cbFiltroEstado.setItems(FXCollections.observableArrayList("Todos los estados", "Disponibles", "Prestados", "Solo Sala"));
        cbFiltroEstado.setValue("Todos los estados");

        txtFiltroCatalogo.textProperty().addListener((obs, oldV, newV) -> aplicarFiltrosCatalogo());

        // Configuración de Recepción y Cómputo
        cbTipoServicio.setItems(FXCollections.observableArrayList(TipoServicio.CONSULTA_SALA, TipoServicio.USO_COMPUTADORA));
        cbTipoServicio.setValue(TipoServicio.CONSULTA_SALA);

        ObservableList<String> pcs = FXCollections.observableArrayList();
        for (int i = 1; i <= 15; i++) {
            pcs.add("PC-" + String.format("%02d", i));
        }
        cbComputadoras.setItems(pcs);
        cbComputadoras.setValue("PC-01");

        spnHorasPc.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 2));
        actualizarVisibilidadCamposPc();

        // Textos iniciales
        txtTicketVentanilla.setText("--- Ninguna transacción procesada en este momento ---");
        txtCartaNoAdeudo.setText("--- Seleccione un alumno o docente y presione 'Emitir Constancia Oficial de No Adeudo' ---");
        txtReporteSalida.setText(service.getCorteCajaActual().generarTicketTexto());

        // Actualizar KPIs y seleccionar por defecto la vista Catálogo de Libros (como en la foto)
        actualizarKpis();
        mostrarVistaCatalogo();
    }

    // ==========================================
    // NAVEGACIÓN DEL MENÚ LATERAL (SIDEBAR)
    // ==========================================

    private void switchView(VBox activeView, Button activeButton, String title) {
        VBox[] allViews = {viewDashboard, viewCatalogo, viewPrestamos, viewProrrogas, viewDevoluciones, viewComputo, viewUsuarios, viewReportes};
        Button[] allButtons = {btnNavDashboard, btnNavCatalogo, btnNavPrestamos, btnNavProrrogas, btnNavDevoluciones, btnNavComputo, btnNavUsuarios, btnNavReportes};

        for (VBox v : allViews) {
            if (v != null) {
                boolean isTarget = (v == activeView);
                v.setVisible(isTarget);
                v.setManaged(isTarget);
            }
        }

        for (Button b : allButtons) {
            if (b != null) {
                b.getStyleClass().remove("sidebar-btn-active");
                if (b == activeButton) {
                    b.getStyleClass().add("sidebar-btn-active");
                }
            }
        }

        if (lblTopBarTitle != null) {
            lblTopBarTitle.setText("BiblioGestión · " + title);
        }
    }

    @FXML public void mostrarVistaDashboard() { switchView(viewDashboard, btnNavDashboard, "Dashboard de Indicadores"); actualizarKpis(); }
    @FXML public void mostrarVistaCatalogo() { switchView(viewCatalogo, btnNavCatalogo, "Catálogo de Libros"); aplicarFiltrosCatalogo(); }
    @FXML public void mostrarVistaPrestamos() { switchView(viewPrestamos, btnNavPrestamos, "Ventanilla Express de Préstamos"); }
    @FXML public void mostrarVistaProrrogas() { switchView(viewProrrogas, btnNavProrrogas, "Prórrogas y Renovaciones"); }
    @FXML public void mostrarVistaDevoluciones() { switchView(viewDevoluciones, btnNavDevoluciones, "Devoluciones y Control de Sanciones"); }
    @FXML public void mostrarVistaComputo() { switchView(viewComputo, btnNavComputo, "Recepción y Centro de Cómputo"); }
    @FXML public void mostrarVistaUsuarios() { switchView(viewUsuarios, btnNavUsuarios, "Padrón de Lectores y No Adeudo"); }
    @FXML public void mostrarVistaReportes() { switchView(viewReportes, btnNavReportes, "Reportes Gerenciales y Corte de Caja"); }

    private void actualizarKpis() {
        if (lblKpiTotalLibros != null) {
            lblKpiTotalLibros.setText(service.getLibros().size() + " Obras");
        }
        if (lblKpiPrestamosActivos != null) {
            long activos = service.getPrestamos().stream().filter(p -> p.getEstado() == EstadoPrestamo.ACTIVO).count();
            lblKpiPrestamosActivos.setText(activos + " Préstamos");
        }
        if (lblKpiMultasPendientes != null) {
            lblKpiMultasPendientes.setText(service.getMultasPendientes().size() + " Deudores");
        }
        if (lblKpiComputadorasUso != null) {
            long pcsUso = service.getAccesosBiblioteca().stream()
                    .filter(a -> a.isActivo() && a.getTipoServicio() == TipoServicio.USO_COMPUTADORA).count();
            lblKpiComputadorasUso.setText(pcsUso + " Equipos");
        }
    }

    // ==========================================
    // CONFIGURACIÓN DE TABLAS
    // ==========================================

    private void configurarTablaPrestamos() {
        colFolio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdPrestamo()));
        colMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getMatricula()));
        colLector.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getNombreCompleto()));
        colLibro.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEjemplar().getLibro().getTitulo()));
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getEjemplar().getFolioTarjeta() + " (" + c.getValue().getEjemplar().getCodigoBarras() + ")"
        ));
        colFechaPrest.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaPrestamo().toString()));
        colFechaLimite.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaDevolucionEsperada().toString()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
        tblPrestamosActivos.setItems(olPrestamos);
    }

    private void configurarTablaProrrogas() {
        colProFolio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdPrestamo()));
        colProMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getMatricula()));
        colProLector.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getNombreCompleto()));
        colProLibro.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEjemplar().getLibro().getTitulo()));
        colProTarjeta.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEjemplar().getFolioTarjeta()));
        colProFechaLim.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaDevolucionEsperada().toString()));
        colProProrrogas.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getNumeroProrrogas() + " de " + Prestamo.MAX_PRORROGAS + 
            (c.getValue().getNumeroProrrogas() >= Prestamo.MAX_PRORROGAS ? " (AGOTADAS)" : " (Disponible)")
        ));
        tblPrestamosProrroga.setItems(olPrestamosProrroga);
    }

    private void configurarTablaMultas() {
        colMulId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdMulta()));
        colMulMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getMatricula()));
        colMulLector.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getNombreCompleto()));
        colMulMonto.setCellValueFactory(c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().getMonto())));
        colMulMotivo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotivo()));
        colMulEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
        tblMultas.setItems(olMultas);
    }

    private void configurarTablaCatalogo() {
        colTit.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo() + "\n" + c.getValue().getAutor()));
        colCar.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCarrera().getNombreCompleto()));
        colDewey.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClasificacionDewey()));
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        colEst.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUbicacionEstante()));
        colDisp.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCopiasDisponibles() + " de " + c.getValue().getCopiasTotales() + " disponibles"
        ));
        colLectorActual.setCellValueFactory(c -> {
            StringBuilder sb = new StringBuilder();
            for (Ejemplar ej : c.getValue().getEjemplares()) {
                if (ej.getEstado() == EstadoLibro.PRESTADO && ej.getPrestamoActivo() != null) {
                    sb.append("[").append(ej.getFolioTarjeta()).append("] ").append(ej.getLectorActualInfo()).append(" | ");
                }
            }
            if (c.getValue().isEsConsultaInterna()) {
                return new SimpleStringProperty("🔴 Solo Consulta en Sala (RN-03)");
            }
            return new SimpleStringProperty(sb.length() > 0 ? sb.toString() : "🟢 Disponible en Estantería");
        });
        tblCatalogo.setItems(olLibros);
    }

    private void configurarTablaLectores() {
        colLecMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricula()));
        colLecNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colLecTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().getDescripcion()));
        colLecCar.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCarrera().getNombreCompleto()));
        colLecEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreoInstitucional()));
        colLecLibros.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCantidadPrestamosActivos() + " / " + c.getValue().getTipo().getLimiteLibros()
        ));
        colLecAdeudo.setCellValueFactory(c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().getSaldoPendiente())));
        colLecSem.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadoSemaforo()));
        tblLectores.setItems(olUsuarios);
    }

    private void configurarTablaAccesos() {
        colAccFolio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdAcceso()));
        colAccMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getMatricula()));
        colAccNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getNombreCompleto()));
        colAccCarrera.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario().getCarrera().getAbreviatura()));
        colAccServicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoServicio().getDescripcion()));
        colAccPc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumeroComputadora()));
        colAccEntrada.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraEntradaFormateada()));
        colAccLimite.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraLimiteFormateada()));
        colAccEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActivo() ? "🟢 En Sesión" : "⚪ Finalizada"));
        tblAccesos.setItems(olAccesos);
    }

    // ==========================================
    // VISTA 2: CATÁLOGO Y FILTROS
    // ==========================================

    @FXML public void onChkSoloDisponiblesCambio(ActionEvent e) { aplicarFiltrosCatalogo(); }
    @FXML public void onFiltroEstadoCambio(ActionEvent e) { aplicarFiltrosCatalogo(); }

    @FXML
    public void onTemaRecomendadoSeleccionado(ActionEvent e) {
        String tema = cbTemasRecomendados.getValue();
        if (tema == null || tema.equals("TODOS LOS TEMAS")) {
            txtFiltroCatalogo.clear();
        } else {
            String clave = tema.split(" ")[0].toLowerCase();
            txtFiltroCatalogo.setText(clave);
        }
        aplicarFiltrosCatalogo();
    }

    private void aplicarFiltrosCatalogo() {
        String texto = txtFiltroCatalogo != null ? txtFiltroCatalogo.getText() : "";
        var lista = service.buscarLibrosPorPalabraClave(texto);

        // Filtro: Solo disponibles
        if (chkSoloDisponibles != null && chkSoloDisponibles.isSelected()) {
            lista.removeIf(l -> l.getCopiasDisponibles() == 0);
        }

        // Filtro: Estado general
        if (cbFiltroEstado != null && cbFiltroEstado.getValue() != null) {
            String est = cbFiltroEstado.getValue();
            if (est.equals("Disponibles")) {
                lista.removeIf(l -> l.getCopiasDisponibles() == 0);
            } else if (est.equals("Prestados")) {
                lista.removeIf(l -> l.getCopiasDisponibles() == l.getCopiasTotales());
            } else if (est.equals("Solo Sala")) {
                lista.removeIf(l -> !l.isEsConsultaInterna());
            }
        }

        olLibros.setAll(lista);
        if (lblConteoResultados != null) {
            lblConteoResultados.setText("Resultados: " + lista.size() + " libros encontrados");
        }
    }

    @FXML public void onSugerenciaSuelos() { txtFiltroCatalogo.setText("suelos"); }
    @FXML public void onSugerenciaConcreto() { txtFiltroCatalogo.setText("concreto"); }
    @FXML public void onSugerenciaOperaciones() { txtFiltroCatalogo.setText("operaciones"); }
    @FXML public void onSugerenciaCalidad() { txtFiltroCatalogo.setText("calidad"); }
    @FXML public void onSugerenciaIncoterms() { txtFiltroCatalogo.setText("incoterms"); }
    @FXML public void onSugerenciaCalculo() { txtFiltroCatalogo.setText("calculo"); }

    // ==========================================
    // VISTA 3: VENTANILLA EXPRESS
    // ==========================================

    @FXML
    public void onMatriculaKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            buscarYValidarUsuario();
            txtCodigoBarras.requestFocus();
        } else if (e.getCode() == KeyCode.ESCAPE) {
            onLimpiarVentanilla();
        }
    }

    @FXML
    public void onValidarMatriculaClick(ActionEvent e) {
        buscarYValidarUsuario();
        txtCodigoBarras.requestFocus();
    }

    @FXML
    public void onCodigoBarrasKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            onRegistrarPrestamo();
        } else if (e.getCode() == KeyCode.ESCAPE) {
            onLimpiarVentanilla();
        }
    }

    @FXML
    public void onBuscarEjemplarClick(ActionEvent e) {
        buscarEjemplar();
    }

    private void buscarYValidarUsuario() {
        String m = txtMatricula.getText().trim();
        Optional<Usuario> opt = service.buscarUsuario(m);
        if (opt.isPresent()) {
            usuarioActualVentanilla = opt.get();
            lblUsuarioInfo.setText(String.format("Lector: %s | %s (%s)",
                usuarioActualVentanilla.getNombreCompleto(),
                usuarioActualVentanilla.getTipo().getDescripcion(),
                usuarioActualVentanilla.getCarrera().getAbreviatura()));
            lblUsuarioInfo.setStyle("-fx-text-fill: #4F46E5; -fx-font-weight: bold;");

            actualizarSemaforo(usuarioActualVentanilla);
        } else {
            usuarioActualVentanilla = null;
            lblUsuarioInfo.setText("❌ Matrícula no encontrada en Servicios Escolares.");
            lblUsuarioInfo.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            setSemaforo("semaforo-rojo", "❌ MATRÍCULA NO REGISTRADA", "El alumno o docente no aparece en la base de la FIT.");
        }
    }

    private void buscarEjemplar() {
        String cod = txtCodigoBarras.getText().trim();
        Optional<Ejemplar> optEj = service.buscarEjemplar(cod);
        if (optEj.isPresent()) {
            ejemplarActualVentanilla = optEj.get();
            lblLibroInfo.setText("Obra: " + ejemplarActualVentanilla.getLibro().getTitulo() + 
                " | Dewey: " + ejemplarActualVentanilla.getLibro().getClasificacionDewey() + 
                " | Tarjeta: " + ejemplarActualVentanilla.getFolioTarjeta() + 
                " (" + ejemplarActualVentanilla.getLibro().getUbicacionEstante() + ")");
            lblLibroInfo.setStyle("-fx-text-fill: #4F46E5;");
        } else {
            ejemplarActualVentanilla = null;
            lblLibroInfo.setText("❌ Ejemplar o Tarjeta no encontrada: " + cod);
            lblLibroInfo.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    private void actualizarSemaforo(Usuario u) {
        String est = u.getEstadoSemaforo();
        switch (est) {
            case "VERDE_OK":
                setSemaforo("semaforo-verde", "🟢 USUARIO AL CORRIENTE (APTO)", 
                    String.format("Préstamos activos: %d de %d permitidos. Sin adeudos ni sanciones.", 
                    u.getCantidadPrestamosActivos(), u.getTipo().getLimiteLibros()));
                break;
            case "ROJO_ADEUDO":
                setSemaforo("semaforo-rojo", "🔴 USUARIO MOROSO (BLOQUEADO)", 
                    String.format("¡ALERTA! El usuario tiene MULTAS PENDIENTES por $%.2f ($20/día). Préstamos bloqueados por RN-04.", 
                    u.getSaldoPendiente()));
                break;
            case "AMARILLO_LIMITE":
                setSemaforo("semaforo-amarillo", "🟡 LÍMITE DE LIBROS ALCANZADO", 
                    String.format("El usuario ya tiene %d de %d libros permitidos en su poder. Debe devolver material antes de solicitar más (RN-01).", 
                    u.getCantidadPrestamosActivos(), u.getTipo().getLimiteLibros()));
                break;
            default:
                setSemaforo("semaforo-rojo", "🔴 USUARIO INACTIVO", "El usuario está suspendido.");
                break;
        }
    }

    private void setSemaforo(String estiloCss, String titulo, String detalle) {
        pnlSemaforo.getStyleClass().removeAll("semaforo-espera", "semaforo-verde", "semaforo-rojo", "semaforo-amarillo");
        pnlSemaforo.getStyleClass().add(estiloCss);
        lblSemaforoTitulo.setText(titulo);
        lblSemaforoDetalle.setText(detalle);
    }

    @FXML
    public void onRegistrarPrestamo() {
        if (usuarioActualVentanilla == null) {
            buscarYValidarUsuario();
            if (usuarioActualVentanilla == null) return;
        }

        if (ejemplarActualVentanilla == null) {
            buscarEjemplar();
            if (ejemplarActualVentanilla == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Código Inválido", "No se encontró el ejemplar especificado.");
                return;
            }
        }

        ResultadoValidacion val = service.validarPrestamo(usuarioActualVentanilla, ejemplarActualVentanilla);
        if (!val.isAprobado()) {
            setSemaforo("semaforo-rojo", "❌ PRÉSTAMO DENEGADO (" + val.getCodigoRegla() + ")", val.getMensaje());
            mostrarAlerta(Alert.AlertType.WARNING, "Operación Denegada", val.getMensaje());
            return;
        }

        try {
            Prestamo p = service.registrarPrestamo(usuarioActualVentanilla, ejemplarActualVentanilla, "Mariana Rojas", LocalDate.now());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy (EEEE)", new java.util.Locale("es", "MX"));
            
            String ticket = "========================================================\n" +
                            "            UAT · FACULTAD DE INGENIERÍA (FIT)         \n" +
                            "              TICKET DE PRÉSTAMO BIBLIOGRÁFICO         \n" +
                            "========================================================\n" +
                            "Folio Préstamo    : " + p.getIdPrestamo() + "\n" +
                            "Fecha Salida      : " + LocalDate.now() + "\n" +
                            "FECHA DE ENTREGA  : " + p.getFechaDevolucionEsperada().format(fmt).toUpperCase() + "\n" +
                            "                    (Plazo: 2 Días Hábiles sin fines de semana)\n" +
                            "Matrícula Lector  : " + usuarioActualVentanilla.getMatricula() + "\n" +
                            "Nombre            : " + usuarioActualVentanilla.getNombreCompleto() + "\n" +
                            "Carrera           : " + usuarioActualVentanilla.getCarrera().getNombreCompleto() + "\n" +
                            "--------------------------------------------------------\n" +
                            "Clasif. Dewey     : " + ejemplarActualVentanilla.getLibro().getClasificacionDewey() + "\n" +
                            "Folio Tarjeta     : " + ejemplarActualVentanilla.getFolioTarjeta() + " [ÚNICO CONSECUTIVO]\n" +
                            "Código Ejemplar   : " + ejemplarActualVentanilla.getCodigoBarras() + "\n" +
                            "Título del Libro  : " + ejemplarActualVentanilla.getLibro().getTitulo() + "\n" +
                            "Estante           : " + ejemplarActualVentanilla.getLibro().getUbicacionEstante() + "\n" +
                            "--------------------------------------------------------\n" +
                            "Prórrogas permit. : Hasta 2 prórrogas (solicitadas solo con matrícula)\n" +
                            "Multa por retraso : $20.00 pesos por cada día hábil de mora\n" +
                            "========================================================\n";
            txtTicketVentanilla.setText(ticket);

            setSemaforo("semaforo-verde", "✅ ¡PRÉSTAMO REGISTRADO CON ÉXITO!", 
                "Libro entregado. Fecha estipulada de retorno: " + p.getFechaDevolucionEsperada().format(fmt));

            refrescarTodasLasTablas();
            actualizarKpis();

            txtCodigoBarras.clear();
            ejemplarActualVentanilla = null;
            lblLibroInfo.setText("Listo para siguiente escaneo de tarjeta/libro...");
            txtCodigoBarras.requestFocus();

        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    @FXML
    public void onDeshacerUltimo() {
        boolean ok = service.deshacerUltimoPrestamo();
        if (ok) {
            txtTicketVentanilla.setText("↩ ÚLTIMO PRÉSTAMO REVERTIDO EXITOSAMENTE.\nEl ejemplar volvió a estar Disponible y se liberó la cuota del lector.");
            setSemaforo("semaforo-amarillo", "↩ PRÉSTAMO CANCELADO", "Se revirtió la asignación errónea previa.");
            refrescarTodasLasTablas();
            actualizarKpis();
            onLimpiarVentanilla();
        } else {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Deshacer", "No hay préstamos activos recientes para revertir.");
        }
    }

    @FXML
    public void onLimpiarVentanilla() {
        txtMatricula.clear();
        txtCodigoBarras.clear();
        usuarioActualVentanilla = null;
        ejemplarActualVentanilla = null;
        lblUsuarioInfo.setText("Esperando escaneo de credencial...");
        lblUsuarioInfo.setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
        lblLibroInfo.setText("Esperando tarjeta o código de libro...");
        lblLibroInfo.setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
        setSemaforo("semaforo-espera", "⚪ SEMÁFORO: EN ESPERA DE LECTURA", 
            "Escanee una credencial para validar en tiempo real solvencia, adeudos ($20/día) y límite de libros.");
        txtMatricula.requestFocus();
    }

    // ==========================================
    // VISTA 4: PRÓRROGAS
    // ==========================================

    @FXML
    public void onConsultarProrrogaMatricula() {
        String mat = txtMatriculaProrroga.getText().trim();
        Optional<Usuario> uOpt = service.buscarUsuario(mat);
        if (!uOpt.isPresent()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Usuario No Encontrado", "No existe ningún usuario con la matrícula: " + mat);
            olPrestamosProrroga.clear();
            return;
        }
        Usuario u = uOpt.get();
        olPrestamosProrroga.setAll(u.getPrestamosActivos());
        if (olPrestamosProrroga.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin Préstamos", "El usuario " + u.getNombreCompleto() + " no tiene libros prestados actualmente.");
        }
    }

    @FXML
    public void onAplicarProrroga() {
        Prestamo sel = tblPrestamosProrroga.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un préstamo de la lista para otorgar prórroga.");
            return;
        }

        try {
            Prestamo p = service.aplicarProrroga(sel);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy (EEEE)", new java.util.Locale("es", "MX"));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Prórroga Autorizada", 
                String.format("Se otorgaron 2 días hábiles adicionales con éxito.\nNueva fecha límite de entrega: %s\nPrórrogas utilizadas: %d de %d permitidas.", 
                p.getFechaDevolucionEsperada().format(fmt), p.getNumeroProrrogas(), Prestamo.MAX_PRORROGAS));
            refrescarTodasLasTablas();
            tblPrestamosProrroga.refresh();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Prórroga Rechazada", e.getMessage());
        }
    }

    // ==========================================
    // VISTA 5: DEVOLUCIONES Y MULTAS
    // ==========================================

    @FXML
    public void onProcesarDevolucion() {
        String cod = txtDevCodigoBarras.getText().trim();
        Optional<Prestamo> pOpt = service.buscarPrestamoActivoPorEjemplar(cod);
        if (!pOpt.isPresent()) {
            mostrarAlerta(Alert.AlertType.ERROR, "No Encontrado", "No existe un préstamo activo para la tarjeta/código: " + cod);
            return;
        }

        Prestamo p = pOpt.get();
        LocalDate fRetorno = dpDevFechaRetorno.getValue();
        EstadoLibro estadoFisico = cbDevCondicion.getValue();

        Prestamo dev = service.registrarDevolucion(p, estadoFisico, "Mariana Rojas", fRetorno);
        if (dev.getDiasHabilesRetraso() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Devolución con Retraso", 
                String.format("El libro fue devuelto con %d día(s) hábil(es) de mora.\n" +
                              "Se generó una multa automática de $%.2f ($20.00 pesos diarios).\n" +
                              "El lector queda en estatus BLOQUEADO hasta liquidar en caja.\n" +
                              "Debe transcurrir 1 día hábil para volver a pedir este título (Cooldown).", 
                              dev.getDiasHabilesRetraso(), dev.getMultaAsociada().getMonto()));
        } else {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Devolución Exitosa", 
                "Libro recibido en tiempo y forma. Ejemplar devuelto a estantería como: " + estadoFisico + 
                "\nAplica regla de enfriamiento de 1 día hábil para que el mismo usuario vuelva a solicitarlo.");
        }

        txtDevCodigoBarras.clear();
        refrescarTodasLasTablas();
        actualizarKpis();
    }

    @FXML
    public void onCobrarMulta() {
        Multa sel = tblMultas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione una multa de la tabla para registrar el pago.");
            return;
        }

        service.cobrarMulta(sel.getIdMulta(), "Mariana Rojas");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Pago Registrado", 
            String.format("Cobro de $%.2f registrado en efectivo en caja. El usuario %s ha sido desbloqueado.", 
            sel.getMonto(), sel.getUsuario().getMatricula()));
        refrescarTodasLasTablas();
        actualizarKpis();
    }

    @FXML
    public void onCondonarMulta() {
        Multa sel = tblMultas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione una multa de la tabla para condonar.");
            return;
        }

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Autorización Exclusiva de Jefatura");
        dlg.setHeaderText("Condonar Multa: " + sel.getIdMulta() + " ($" + sel.getMonto() + ")");
        dlg.setContentText("Ingrese justificación o motivo institucional:");
        Optional<String> res = dlg.showAndWait();
        res.ifPresent(mot -> {
            service.condonarMulta(sel.getIdMulta(), mot, "Getsel Giadans (Jefa FIT)");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Multa Condonada", "La sanción económica ha sido condonada.");
            refrescarTodasLasTablas();
            actualizarKpis();
        });
    }

    // ==========================================
    // VISTA 6: RECEPCIÓN Y CÓMPUTO
    // ==========================================

    @FXML
    public void onTipoServicioCambio(ActionEvent e) {
        actualizarVisibilidadCamposPc();
    }

    private void actualizarVisibilidadCamposPc() {
        boolean esPc = cbTipoServicio.getValue() == TipoServicio.USO_COMPUTADORA;
        lblPcTitulo.setVisible(esPc);
        cbComputadoras.setVisible(esPc);
        lblHorasTitulo.setVisible(esPc);
        spnHorasPc.setVisible(esPc);
    }

    @FXML
    public void onRegistrarAcceso() {
        String mat = txtMatriculaAcceso.getText().trim();
        Optional<Usuario> uOpt = service.buscarUsuario(mat);
        if (!uOpt.isPresent()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Usuario No Encontrado", "La matrícula no existe en el sistema.");
            return;
        }

        Usuario u = uOpt.get();
        TipoServicio tipo = cbTipoServicio.getValue();
        String pc = tipo == TipoServicio.USO_COMPUTADORA ? cbComputadoras.getValue() : null;
        int horas = tipo == TipoServicio.USO_COMPUTADORA ? spnHorasPc.getValue() : 0;

        RegistroAcceso acc = service.registrarAccesoBiblioteca(u, tipo, pc, horas);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Acceso Registrado", 
            String.format("Bienvenido %s.\nServicio: %s%s", 
            u.getNombreCompleto(), tipo.getDescripcion(),
            tipo == TipoServicio.USO_COMPUTADORA ? "\nEquipo: " + pc + " asignado por " + horas + " hora(s). Hora límite: " + acc.getHoraLimiteFormateada() : ""));

        txtMatriculaAcceso.clear();
        olAccesos.setAll(service.getAccesosBiblioteca());
        actualizarKpis();
    }

    @FXML
    public void onFinalizarSesionPc() {
        RegistroAcceso sel = tblAccesos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione una sesión de acceso de la tabla.");
            return;
        }
        if (!sel.isActivo()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sesión Inactiva", "Esta sesión ya se encontraba concluida.");
            return;
        }

        service.finalizarSesionComputadora(sel.getIdAcceso());
        mostrarAlerta(Alert.AlertType.INFORMATION, "Sesión Concluida", "El equipo " + sel.getNumeroComputadora() + " ha sido liberado.");
        olAccesos.setAll(service.getAccesosBiblioteca());
        actualizarKpis();
    }

    // ==========================================
    // VISTA 7: LECTORES Y NO ADEUDO
    // ==========================================

    @FXML
    public void onGenerarCartaNoAdeudo() {
        Usuario sel = tblLectores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione a un alumno o docente de la tabla.");
            return;
        }

        String constancia = service.generarCartaNoAdeudo(sel);
        txtCartaNoAdeudo.setText(constancia);

        if (constancia.startsWith("DENEGADO")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Constancia Denegada (RN-05 / RN-10)", constancia);
        } else {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Constancia Generada", 
                "Constancia oficial de No Adeudo para " + sel.getNombreCompleto() + " emitida correctamente.");
        }
    }

    // ==========================================
    // VISTA 8: REPORTES Y CORTE DE CAJA
    // ==========================================

    @FXML
    public void onMostrarCorteCaja() {
        txtReporteSalida.setText(service.getCorteCajaActual().generarTicketTexto());
    }

    @FXML
    public void onMostrarReporteMorosos() {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================================\n");
        sb.append("         REPORTE OFICIAL DE LECTORES MOROSOS Y ADEUDOS FIT             \n");
        sb.append("                 Tarifa Oficial: $20.00 Pesos por Día                  \n");
        sb.append("=======================================================================\n");
        sb.append(String.format("%-12s %-25s %-20s %12s\n", "Matrícula", "Nombre", "Carrera", "Adeudo ($)"));
        sb.append("-----------------------------------------------------------------------\n");
        for (Usuario u : service.getUsuarios()) {
            if (u.tieneAdeudo()) {
                sb.append(String.format("%-12s %-25s %-20s $%11.2f\n", 
                    u.getMatricula(), u.getNombreCompleto(), u.getCarrera().getAbreviatura(), u.getSaldoPendiente()));
            }
        }
        sb.append("=======================================================================\n");
        txtReporteSalida.setText(sb.toString());
    }

    @FXML
    public void onMostrarStatsCarrera() {
        Map<Carrera, Long> stats = service.obtenerPrestamosPorCarrera();
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================================\n");
        sb.append("    MÉTRICAS DE DEMANDA BIBLIOGRÁFICA POR COORDINACIÓN ACADÉMICA       \n");
        sb.append("             (SOPORTE PARA ACREDITACIONES CACEI FIT)                   \n");
        sb.append("=======================================================================\n");
        for (Map.Entry<Carrera, Long> entry : stats.entrySet()) {
            sb.append(String.format("  * %-35s : %d préstamos registrados\n", 
                entry.getKey().getNombreCompleto(), entry.getValue()));
        }
        sb.append("-----------------------------------------------------------------------\n");
        sb.append("Total de Obras en Catálogo : ").append(service.getLibros().size()).append("\n");
        sb.append("Total de Copias Físicas    : ").append(service.getEjemplares().size()).append("\n");
        sb.append("=======================================================================\n");
        txtReporteSalida.setText(sb.toString());
    }

    @FXML
    public void onExportarReporteTXT() {
        try {
            File f = service.exportarArchivoTexto("Reporte_Biblioteca_FIT", txtReporteSalida.getText());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación Exitosa", 
                "El reporte ha sido generado y guardado en:\n" + f.getAbsolutePath());
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Exportación", ex.getMessage());
        }
    }

    private void refrescarTodasLasTablas() {
        olPrestamos.setAll(service.getPrestamos());
        olMultas.setAll(service.getMultasPendientes());
        olLibros.setAll(service.getLibros());
        olUsuarios.setAll(service.getUsuarios());
        olAccesos.setAll(service.getAccesosBiblioteca());
        if (txtMatriculaProrroga != null && !txtMatriculaProrroga.getText().trim().isEmpty()) {
            onConsultarProrrogaMatricula();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
