/**
 * Classe principal de entrada (Launcher) para a aplicação JavaFX.
 * Evita o erro "JavaFX runtime components are missing" ao iniciar sem argumentos de módulo no Java 11+.
 */
public class Main {
    public static void main(String[] args) {
        IMCApp.main(args);
    }
}
