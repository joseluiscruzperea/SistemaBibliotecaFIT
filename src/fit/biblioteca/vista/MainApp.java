package fit.biblioteca.vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Punto de entrada de la aplicación JavaFX implementada bajo el patrón
 * Modelo - Vista - Controlador (MVC) mediante FXML.
 * 
 * Incorpora un cargador robusto multi-ruta para garantizar compatibilidad total
 * con Eclipse IDE y evitar el error "Location is not set".
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlLocation = obtenerUrlFxml();

            if (fxmlLocation == null) {
                throw new IllegalStateException(
                    "No se pudo encontrar el archivo 'MainView.fxml'.\n" +
                    "Por favor, en Eclipse haz clic derecho en el proyecto -> 'Refresh' (F5)."
                );
            }

            System.out.println("Cargando vista FXML desde: " + fxmlLocation);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1140, 740);

            primaryStage.setTitle("Sistema Integral de Biblioteca - Facultad de Ingeniería (FIT · UAT)");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(640);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error crítico al inicializar la vista FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Búsqueda en cascada multi-origen de MainView.fxml para evitar 'Location is not set' en Eclipse.
     */
    private URL obtenerUrlFxml() {
        // 1. Vía ClassLoader relativo a la clase
        URL url = getClass().getResource("MainView.fxml");
        if (url != null) return url;

        // 2. Vía ClassLoader absoluto del paquete
        url = getClass().getResource("/fit/biblioteca/vista/MainView.fxml");
        if (url != null) return url;

        // 3. Vía Thread Context ClassLoader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            url = cl.getResource("fit/biblioteca/vista/MainView.fxml");
            if (url != null) return url;
        }

        // 4. Búsqueda directa en el sistema de archivos de Eclipse (src/ y bin/)
        String[] posiblesRutas = {
            "src/fit/biblioteca/vista/MainView.fxml",
            "bin/fit/biblioteca/vista/MainView.fxml",
            "SistemaBibliotecaFIT/src/fit/biblioteca/vista/MainView.fxml",
            "SistemaBibliotecaFIT/bin/fit/biblioteca/vista/MainView.fxml",
            "MainView.fxml"
        };

        for (String ruta : posiblesRutas) {
            File f = new File(ruta);
            if (f.exists()) {
                try {
                    return f.toURI().toURL();
                } catch (Exception ignored) {
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
