public class Main {
    public static void main(String[] args) {
        //EinlesenDemo.einlesen();
        //DBVerbindung db = new DBVerbindung();
        //db.dbVerwenden();

        // "data/PlanetA-Q1_6x13.txt"
        // "data/PlanetB-Q3_16x50.txt"
        // "data/PlanetB-Q5_50x100.txt"

        // setup quadrant
        Quadrant q = Utils.quadrantFromFile("data/PlanetA-Q1_6x13.txt");
        System.out.println("Name: " + q.getPlanetName());
        System.out.println("Quadrant: Q" + q.getQuadrant());
        System.out.println("Value Index: " + q.getValueIndex());
        System.out.println("Resource Count Gold: " + q.getResourceCount('g'));
        System.out.println("Resource Density Gold: " + q.getResourceDensity('g'));
    }
}
