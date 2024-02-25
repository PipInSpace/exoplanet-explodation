public class Main {
    public static void main(String[] args) {
        //EinlesenDemo.einlesen();
        DBVerbindung db = new DBVerbindung();
        db.dbVerwenden();

        // "data/PlanetA-Q1_6x13.txt"
        // "data/PlanetB-Q3_16x50.txt"
        // "data/PlanetB-Q5_50x100.txt"

        // setup quadrant
        Quadrant q = Utils.quadrantFromFile("data/PlanetA-Q1_6x13.txt");
        assert q != null;
        Utils.logTS("Name: " + q.getPlanetName());
        Utils.logTS("Quadrant: Q" + q.getQuadrant());
        Utils.logTS("Value Index: " + q.getValueIndex());
        Utils.logTS("Resource Count Gold: " + q.getResourceCount('g'));
        Utils.logTS("Resource Density Gold: " + q.getResourceDensity('g'));
    }
}
