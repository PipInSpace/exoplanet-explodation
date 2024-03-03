import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        // Update Database
        updateDatabase("data/cartography.sqlite", "data/quadrants");

        // Fetch from database
        List<Quadrant> quadrants = quadrantsListFromDB("data/cartography.sqlite");

        // Print quadrant
        Quadrant q = quadrants.getFirst();
        assert q != null;
        Utils.printQuadrantInfo(q);
    }

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

                Utils.logTS("Working on " + quadrant.getPlanetName() + " Q" + quadrantNum);

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

    public static List<Quadrant> quadrantsListFromDB(String dbPath) {
        Utils.logTS("Fetching all quadrants from database...");
        List<Quadrant> quadrants = new ArrayList<>();

        try (Connection conn = Utils.dbConnectTo(dbPath)) {
            assert conn != null;

            String stmtString = "SELECT quadrants_id FROM Quadrants";
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

    public static Quadrant quadrantFromDB(Connection conn, int quadId) {
        Utils.logTS("Fetching quadrant id " + quadId);
        try {
            String stmtString = String.format("SELECT planet_fid, number, value_index, width, height FROM Quadrants WHERE quadrants_id = %d", quadId);
            ResultSet rset = conn.createStatement().executeQuery(stmtString);

            if (rset.next()) {
                int planetFID = rset.getInt("planet_fid");
                int quadNum = rset.getInt("number");
                int quadWidth = rset.getInt("width");
                int quadHeight = rset.getInt("height");

                stmtString = String.format("SELECT name FROM Planets WHERE planets_id = %d", planetFID);
                String planetName = conn.createStatement().executeQuery(stmtString).getString("name");

                stmtString = String.format("SELECT type, coord_x, coord_y FROM Resources WHERE quadrant_fid = %d", quadId);
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
}
