import java.awt.*;
import java.sql.*;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

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
        String treiber = "org.sqlite.JDBC"; // z.B. aus: sqlite-jdbc-3.7.2.jar
        String praefix = "jdbc:sqlite:";

        try {
            //1. Passenden Treiber laden
            Class.forName(treiber);
            //2. Verbindung zur DB erstellen
            //user und kennwort spielen bei der SQLite Datenbank keine Rolle, sind daher null
            Connection c = DriverManager.getConnection(praefix + dbPath, null, null);
            Utils.logTS("Connected to SQLite database at " + dbPath);
            // Die Verbindung wird zurückgegeben
            return c;
        } catch (Exception e) {
            Utils.logTS("Error creating a connection: " + e);
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Gibt eine Liste an Dateipfaden zurück die im angefragten Ordner vorhanden sind.
     * 
     * @param path der Pfad des zu öffnenden Ordners
     * @return Liste an Pfaden zu allen Dateien des Ordners
     */
    public static List<String> openFolder(String path) {
        List<String> filePaths = new ArrayList<>();
        final File folder = new File(path);

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                logTS("Opening file " + fileEntry.getPath());
                filePaths.add(fileEntry.getPath());
            }
        }

        return filePaths;
    }

    /**
     * Gibt Informationen über einen Quadranten in der Konsole aus.
     * 
     * @param q Quadrant, dessen Informationen ausgegeben werden
     */
    public static void printQuadrantInfo(Quadrant q) {
        System.out.println("Lade " + q.getPlanetName() + ", Quadrant " + q.getQuadrantNumber() + ":");
        System.out.println("Breite: " + q.getWidth() + ", Höhe: " + q.getHeight());
        System.out.println("Gesamtwertindex: " + q.getValueIndex());
        System.out.println("Resourcen:");
        // iterate over all resources
        for (char c : new char[]{'g', 'k', 's', 'u', 'z'}) {
            // getting recource name from char is very scuffed
            System.out.println("    " + new Resource(c, 0, 0).getTypeStr() +": Anzahl: " + q.getResourceCount(c) + ", Dichte: " + q.getResourceDensity(c));
            if(q.getResourceCount(c) > 0) {
                System.out.println("    Positionen:");
                for(Point p : q.getResourcePositions(c)) {
                    System.out.println("      " + Utils.pointAsString(p));
                }
            }
        }
        System.out.println();
    }

    /**
     * Aktualisiert die Kartographie-Datenbank an dbPath mit den Quadranten die aus dem dataPath ausgelesen werden.
     * 
     * @param dbPath Der Dateipfad zur Datenbank
     * @param dataPath Der Dateipfad zum Daten-Ordner. Dieser enthält "Quadranten"-Textdateien.
     */
    public static void updateDatabase(String dbPath, String dataPath) {
        Utils.logTS("Updating database from raw data: " + dataPath);
        List<String> quadrantPaths = Utils.openFolder(dataPath);
        List<Quadrant> quadrants = new ArrayList<>();
        for (String path : quadrantPaths) {
            quadrants.add(Quadrant.fromFile(path));
        }
        Utils.logTS("Quadrant objects created.\n");

        try (Connection conn = Utils.dbConnectTo(dbPath)) {
            assert conn != null;
            Utils.logTS("Activating PRAGMA settings.\n");
            conn.createStatement().execute("PRAGMA foreign_keys = ON; PRAGMA foreign_keys;"); // Needed for deletion cascade

            Utils.logTS("Starting database update from data files...");
            // Loop over every quadrant and update database
            for (Quadrant quadrant : quadrants) {
                String pName = quadrant.getPlanetName();
                int quadrantNum = quadrant.getQuadrantNumber();
                int quadrantWidth = quadrant.getWidth();
                int quadrantHeight = quadrant.getHeight();
                double quadrantValueIndex = quadrant.getValueIndex();
                List<Resource> resources = quadrant.getResourceList();
                String stmtStr;

                Utils.logTS("Working on " + pName + " Q" + quadrantNum);

                stmtStr = String.format("SELECT COUNT(*) AS 'COUNT' FROM Planets WHERE name = '%s';", pName);
                boolean planetExists = conn.createStatement().executeQuery(stmtStr).getInt("COUNT") > 0;

                // Create planet entry if it doesn't exist already
                if (!planetExists) {
                    Utils.logTS("Creating Planet " + pName + " entry");
                    stmtStr = String.format("INSERT INTO Planets (name) VALUES ('%s');", pName);
                    conn.createStatement().execute(stmtStr);
                }

                stmtStr = String.format("SELECT planets_id AS 'ID' FROM Planets WHERE name = '%s';", pName);
                int planetID = conn.createStatement().executeQuery(stmtStr).getInt("ID");

                stmtStr = String.format("SELECT COUNT(*) AS 'COUNT' FROM Quadrants WHERE Quadrants.number = %d AND Quadrants.planet_fid = %d;", quadrantNum, planetID);
                boolean quadrantExists = conn.createStatement().executeQuery(stmtStr).getInt("COUNT") > 0;

                int quadrantID;
                if (!quadrantExists) {
                    // Create quadrant
                    Utils.logTS("Creating Quadrant " + pName + " Q" + quadrantNum + " entry");
                    stmtStr = String.format(Locale.US, "INSERT INTO Quadrants (planet_fid, number, value_index, width, height) VALUES (%d, %d, %f, %d, %d);", planetID, quadrantNum, quadrantValueIndex, quadrantWidth, quadrantHeight);
                    conn.createStatement().execute(stmtStr);
                    // Optain ID
                    stmtStr = String.format("SELECT quadrants_id AS 'ID' FROM Quadrants WHERE planet_fid = %d AND number = %d;", planetID, quadrantNum);
                    quadrantID = conn.createStatement().executeQuery(stmtStr).getInt("ID");
                } else {
                    // Update quadrant
                    stmtStr = String.format(Locale.US, "UPDATE Quadrants SET value_index = %f, width = %d, height = %d WHERE planet_fid = %d AND number = %d;", quadrantValueIndex, quadrantWidth, quadrantHeight, planetID, quadrantNum);
                    conn.createStatement().execute(stmtStr);
                    // Optain ID
                    stmtStr = String.format("SELECT quadrants_id AS 'ID' FROM Quadrants WHERE planet_fid = %d AND number = %d;", planetID, quadrantNum);
                    quadrantID = conn.createStatement().executeQuery(stmtStr).getInt("ID");
                    // Delete resources, updating would be very slow and pointless
                    stmtStr = String.format("DELETE FROM Resources WHERE quadrant_fid = %d;", quadrantID);
                    conn.createStatement().execute(stmtStr);
                }

                // (Re)Write resources to database
                // This batch approach might be more efficient.
                Statement resInsertQuery = conn.createStatement();
                resInsertQuery.addBatch("BEGIN;");
                for (Resource res : resources) {
                    resInsertQuery.addBatch(String.format("INSERT INTO Resources (quadrant_fid, type, coord_x, coord_y) VALUES (%d, '%s', %d, %d);\n", quadrantID, res.getTypeStr(), res.getCoordX(), res.getCoordY()));
                }
                resInsertQuery.addBatch("COMMIT;");
                int[] updates = resInsertQuery.executeBatch();
                Utils.logTS("Added " + updates.length + " resource entries.");
                resInsertQuery.close();
            }
            Utils.logTS("Completed database update from data files.");
            conn.close();
            Utils.logTS("Closed database connection.\n");
        } catch (SQLException e) {
            Utils.logTS("SQL Error at cartography database while updating quadrants: " + e);
        }
    }

    /**
     * Gibt eine Liste mit allen Quadranten aus der Kartographie-Datenbank zurück.
     * 
     * @param dbPath Der Dateipfad zur Datenbank
     * @return Liste aller Quadranten
     */
    public static List<Quadrant> quadrantsListFromDB(String dbPath) {
        Utils.logTS("Fetching all quadrants from database...");
        List<Quadrant> quadrants = new ArrayList<>();

        try (Connection conn = Utils.dbConnectTo(dbPath)) {
            assert conn != null;

            String stmtString = "SELECT quadrants_id FROM Quadrants;";
            ResultSet rset = conn.createStatement().executeQuery(stmtString);
            while (rset.next()) {
                Quadrant quadrant = quadrantFromDB(conn, rset.getInt("quadrants_id"));
                if (quadrant != null) quadrants.add(quadrant);
            }
            Utils.logTS("Closed database connection.\n");
        } catch (SQLException e) {
            Utils.logTS("SQL Error at cartography database while fetching quadrants: " + e);
        }

        return quadrants;
    }

    /**
     * Gibt einen voll ausgelesenen Quadranten aus der Datenbank zurück.
     * 
     * @param conn Eine Datenbank-Verbindung zur Kartographie-Datenbank
     * @param quadId Die Primärschlüssel-ID des Quadranten (Tabelle Quadrants Spalte quadrants_id)
     * @return Ausgelesener Quadrant
     */
    public static Quadrant quadrantFromDB(Connection conn, int quadId) {
        Utils.logTS("Fetching quadrant id " + quadId);
        try {
            String stmtString = String.format("SELECT planet_fid, number, value_index, width, height FROM Quadrants WHERE quadrants_id = %d;", quadId);
            ResultSet rset = conn.createStatement().executeQuery(stmtString);

            if (rset.next()) {
                int planetFID = rset.getInt("planet_fid");
                int quadNum = rset.getInt("number");
                int quadWidth = rset.getInt("width");
                int quadHeight = rset.getInt("height");

                stmtString = String.format("SELECT name FROM Planets WHERE planets_id = %d;", planetFID);
                String planetName = conn.createStatement().executeQuery(stmtString).getString("name");

                stmtString = String.format("SELECT type, coord_x, coord_y FROM Resources WHERE quadrant_fid = %d;", quadId);
                rset = conn.createStatement().executeQuery(stmtString);
                List<Resource> resources = new ArrayList<>();
                while (rset.next()) {
                    resources.add(new Resource(rset.getString("type"), rset.getInt("coord_x"), rset.getInt("coord_y")));
                }

                rset.close();
                return new Quadrant(planetName, quadNum, quadWidth, quadHeight, resources);
            }
        } catch (SQLException e) {
            Utils.logTS("SQL Error at cartography database while fetching quadrant: " + e);
        }
        return null;
    }

    /**
     * Formatiert einen Point als String
     * 
     * @param p Punkt
     * @return Punkt formatiert als String
     */
    public static String pointAsString(Point p) {
        return String.format("(%d, %d)", p.x, p.y);
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
