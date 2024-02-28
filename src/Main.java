import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        List<String> quadrantPaths = Utils.openFolder("data/quadrants");
        List<Quadrant> quadrants = new ArrayList<>();
        for (String path : quadrantPaths) {
            quadrants.add(Quadrant.fromFile(path));
        }
        Utils.logTS("Quadrant objects created.\n");

        try (Connection conn = Utils.dbConnectTo("data/cartography.sqlite")) {
            assert conn != null;
            Utils.logTS("Activating PRAGMA settings.\n");
            conn.createStatement().execute("PRAGMA foreign_keys = ON; PRAGMA foreign_keys;"); // Needed for deletion cascade

            Utils.logTS("Starting database update from data files...");
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

                if (!planetExists) {
                    Utils.logTS("Creating Planet " + pName + " entry");
                    stmtStr = String.format("INSERT INTO Planets (name) VALUES ('%s');", pName);
                    conn.createStatement().execute(stmtStr);
                }

                stmtStr = String.format("SELECT planets_id AS 'ID' FROM Planets WHERE name = '%s';", pName);
                int planetID = conn.createStatement().executeQuery(stmtStr).getInt("ID");

                stmtStr = String.format("SELECT COUNT(*) AS 'COUNT' FROM Planets, Quadrants WHERE Quadrants.number = '%d' AND Planets.name = '%s';", quadrantNum, pName);
                boolean quadrantExists = conn.createStatement().executeQuery(stmtStr).getInt("COUNT") > 0;

                int quadrantID;
                if (!quadrantExists) {
                    // Create quadrant
                    Utils.logTS("Creating Quadrant " + pName + " Q" + quadrantNum + " entry");
                    stmtStr = String.format(Locale.US, "INSERT INTO Quadrants (planet_fid, number, value_index, width, height) \nVALUES (%d, %d, %.15g, %d, %d);", planetID, quadrantNum, quadrantValueIndex, quadrantWidth, quadrantHeight);
                    conn.createStatement().execute(stmtStr);
                    // Optain ID
                    stmtStr = String.format("SELECT quadrants_id AS 'ID' FROM Quadrants WHERE planet_fid = %d AND number = %d;", planetID, quadrantNum);
                    quadrantID = conn.createStatement().executeQuery(stmtStr).getInt("ID");
                } else {
                    // Update quadrant
                    stmtStr = String.format(Locale.US, "UPDATE Quadrants\nSET value_index = %.15g,\nwidth = %d,\nheight = %d\nWHERE planet_fid = %d AND number = %d;", quadrantValueIndex, quadrantWidth, quadrantHeight, planetID, quadrantNum);
                    conn.createStatement().execute(stmtStr);
                    // Optain ID
                    stmtStr = String.format("SELECT quadrants_id AS 'ID' FROM Quadrants WHERE planet_fid = %d AND number = %d;", planetID, quadrantNum);
                    quadrantID = conn.createStatement().executeQuery(stmtStr).getInt("ID");
                    // Delete resources
                    stmtStr = String.format("DELETE FROM Resources WHERE quadrant_fid = %d;", quadrantID);
                    conn.createStatement().execute(stmtStr);
                }

                // Write resources to database
                for (Resource res : resources) {
                    stmtStr = String.format("INSERT INTO Resources (quadrant_fid, type, coord_x, coord_y) VALUES (%d, '%s', %d, %d);", quadrantID, res.getTypeStr(), res.getCoordX(), res.getCoordY());
                    conn.createStatement().execute(stmtStr);
                }
            }
            Utils.logTS("Completed database update from data files.\n");

            // Test Database
            Utils.logTS("Validating database, listing content of \"Planets\" table:");
            Statement stmt = conn.createStatement();
            String anfragestring = "SELECT * FROM Planets;";
            ResultSet rset = stmt.executeQuery(anfragestring);

            while (rset.next()){
                Utils.logTS("ID: " + rset.getString("planets_id") + " | Name: " + (rset.getString("name")));
            }
            rset.close();
            stmt.close();
            conn.close();
            Utils.logTS("Closed database connection.\n");
        } catch (SQLException e) {
            Utils.logTS("SQL Error at cartography database: " + e);
            e.printStackTrace();
        }

        // "data/PlanetA-Q1_6x13.txt"
        // "data/PlanetB-Q3_16x50.txt"
        // "data/PlanetB-Q5_50x100.txt"

        // setup quadrant
        Quadrant q = Quadrant.fromFile(quadrantPaths.getFirst());
        assert q != null;
        Utils.logTS("Name: " + q.getPlanetName());
        Utils.logTS("Quadrant: Q" + q.getQuadrantNumber());
        Utils.logTS("Value Index: " + q.getValueIndex());
        Utils.logTS("Resource Count Gold: " + q.getResourceCount('g'));
        Utils.logTS("Resource Density Gold: " + q.getResourceDensity('g'));
    }
}
