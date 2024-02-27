import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class Utils {
    public static Quadrant quadrantFromFile(String path) {
        // get planet name from path
        final String planet = path.replace('\\', '/').split("/")[1].split("-")[0];

        // get quadrant id from path
        final int quadrant = Integer.parseInt(path.split("/")[1].split("-Q")[1].split("_")[0]);

        // read file
        final List<String> map;
        try {
            map = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            logTS("Error reading file: " + path + " Stacktrace: " + e);
            return null;
        }

        return new Quadrant(planet, quadrant, map);
    }

    /**
     * Stellt eine Verbindung zu einer Datenbank her.
     * Dazu muss die Datei sqlite-jdbc-3.7.2.jar
     * im gleichen Ordner wie die Projektdateien gespeichert werden.
     * Die Operation gibt ein Objekt vom Typ Connection zurück und wird in der nächsten Operation verwendet
     * NUR NACH RÜCKSPRACHE VERÄNDERN
     *
     * @param dbPath der Pfad zur Datenbank; kann absolut sein oder relativ zum aktiven Java user.dir -> in BlueJ ist dies das Projektverzeichnis
     * @return          die aufgebaute Verbindung zur Datenbank
     */
    public static Connection dbConnectTo(String dbPath) {
        Utils.logTS("Ich suche nach der Datenbank in: \n    "+dbPath);
        Utils.logTS("Verbindung zu SQLite Datenbank wird versucht.");

        String treiber = "org.sqlite.JDBC"; // z.B. aus: sqlite-jdbc-3.7.2.jar
        String praefix = "jdbc:sqlite:";

        try {
            //1. Passenden Treiber laden
            Class.forName(treiber);
            //2. Verbindung zur DB erstellen
            //user und kennwort spielen bei der SQLite Datenbank keine Rolle, sind daher null
            Connection c = DriverManager.getConnection(praefix + dbPath, null, null);
            Utils.logTS("Verbindung zu SQLite Datenbank steht.");
            // Die Verbindung wird zurückgegeben
            return c;
        } catch (Exception e) {
            Utils.logTS("Fehler beim Erstellen der Verbindung: " + e);
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Log a message "msg" with the current time stamp.
     * Time stamp format: "yyyy/MM/dd HH:mm:ss".
     *
     * @param msg The log message string
     */
    public static void logTS(String msg) {
        System.out.println(
                "[" +
                (new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss"))
                        .format(new java.util.Date())
                + "] " + msg
        );
    }
}
