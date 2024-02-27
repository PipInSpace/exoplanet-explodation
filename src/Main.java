import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        //EinlesenDemo.einlesen();
        //DBVerbindung db = new DBVerbindung();
        //db.dbVerwenden();

        List<String> files = Utils.openFolder("data/quadrants");

        try (Connection conn = Utils.dbConnectTo("data/cartography.sqlite")) {
            assert conn != null;
            conn.createStatement().execute("PRAGMA foreign_keys = ON; PRAGMA foreign_keys;"); // Needed for deletion cascade
            Statement stmt = conn.createStatement();
            String anfragestring = "SELECT * FROM Planets;";
            ResultSet rset = stmt.executeQuery(anfragestring);

            while (rset.next()){
                Utils.logTS("ID: " + rset.getString("planets_id") + " | Name: " + (rset.getString("name")));
            }
            rset.close();
            stmt.close();
            conn.close();
            Utils.logTS("Abgemeldet.\n");
        } catch (SQLException e) {
            Utils.logTS("SQL Error at cartography database: " + e);
            e.printStackTrace();
        }

        // "data/PlanetA-Q1_6x13.txt"
        // "data/PlanetB-Q3_16x50.txt"
        // "data/PlanetB-Q5_50x100.txt"

        // setup quadrant
        Quadrant q = Quadrant.fromFile(files.get(0));
        assert q != null;
        Utils.logTS("Name: " + q.getPlanetName());
        Utils.logTS("Quadrant: Q" + q.getQuadrant());
        Utils.logTS("Value Index: " + q.getValueIndex());
        Utils.logTS("Resource Count Gold: " + q.getResourceCount('g'));
        Utils.logTS("Resource Density Gold: " + q.getResourceDensity('g'));
    }
}
