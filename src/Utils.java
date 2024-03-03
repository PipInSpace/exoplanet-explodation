import java.awt.*;
import java.util.List;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class Utils {
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

    public static List<String> openFolder(String path) {
        List<String> filePaths = new ArrayList<>();
        final File folder = new File(path);

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                logTS("Opening file " + fileEntry.getPath());
                filePaths.add(fileEntry.getPath());
            }
        }

        return filePaths;
    }

    public static String pointAsString(Point p) {
        return String.format("(%d, %d)", p.x, p.y);
    }

    public static void printQuadrantInfo(Quadrant q) {
        System.out.println("Lade " + q.getPlanetName() + ", Quadrant " + q.getQuadrant() + ":");
        System.out.println("Breite: " + q.getWidth() + ", Höhe: " + q.getHeight());
        System.out.println("Gesamtwertindex: " + q.getValueIndex());
        System.out.println("Resourcen:");
        // iterate over all resources
        for (char c : new char[]{'g', 'k', 's', 'u', 'z'}) {
            System.out.println("    " + resourceName(c) +": Anzahl: " + q.getResourceCount(c) + ", Dichte: " + q.getResourceDensity(c));
            if(q.getResourceCount(c) > 0) {
                System.out.println("    Positionen:");
                for(Point p : q.getResourcePositions(c)) {
                    System.out.println("      " + Utils.pointAsString(p));
                }
            }
        }
        System.out.println();
    }

    private static String resourceName(char resourceID) {
        return switch (resourceID) {
            case 'g' -> "Gold";
            case 'k' -> "Kupfer";
            case 's' -> "Silber";
            case 'u' -> "Uran";
            case 'z' -> "Zink";
            default -> "";
        };
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
