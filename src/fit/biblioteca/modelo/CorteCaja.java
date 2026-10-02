package fit.biblioteca.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro de corte de caja por turno para control de ingresos por multas.
 */
public class CorteCaja {
    private String idCorte;
    private LocalDateTime fechaHora;
    private String responsableTurno;
    private double totalCobrado;
    private List<Multa> multasCobradas;

    public CorteCaja(String idCorte, String responsableTurno) {
        this.idCorte = idCorte;
        this.fechaHora = LocalDateTime.now();
        this.responsableTurno = responsableTurno;
        this.totalCobrado = 0.0;
        this.multasCobradas = new ArrayList<>();
    }

    public void agregarCobro(Multa m) {
        multasCobradas.add(m);
        totalCobrado += m.getMonto();
    }

    public String generarTicketTexto() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("        FACULTAD DE INGENIERÍA (FIT) - UAT          \n");
        sb.append("         SISTEMA INTEGRAL DE BIBLIOTECA             \n");
        sb.append("             CORTE DE CAJA POR TURNO                \n");
        sb.append("====================================================\n");
        sb.append("Folio de Corte: ").append(idCorte).append("\n");
        sb.append("Fecha y Hora  : ").append(fechaHora.format(dtf)).append("\n");
        sb.append("Responsable   : ").append(responsableTurno).append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("%-10s %-12s %-15s %10s\n", "Folio", "Matrícula", "Concepto", "Monto"));
        sb.append("----------------------------------------------------\n");
        for (Multa m : multasCobradas) {
            sb.append(String.format("%-10s %-12s %-15s $%9.2f\n", 
                m.getIdMulta(), 
                m.getUsuario().getMatricula(), 
                "Multa Retraso", 
                m.getMonto()));
        }
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("TOTAL MULTAS COBRADAS : %d\n", multasCobradas.size()));
        sb.append(String.format("TOTAL EN EFECTIVO     : $%.2f\n", totalCobrado));
        sb.append("====================================================\n");
        sb.append("Firma Responsable: ________________________________\n");
        sb.append("Firma Jefatura   : ________________________________\n");
        return sb.toString();
    }

    // Getters
    public String getIdCorte() { return idCorte; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getResponsableTurno() { return responsableTurno; }
    public double getTotalCobrado() { return totalCobrado; }
    public List<Multa> getMultasCobradas() { return multasCobradas; }
}
