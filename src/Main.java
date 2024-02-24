public class Main {
    public static void main(String[] args) {
        EinlesenDemo.einlesen();
        DBVerbindung db = new DBVerbindung();
        db.dbVerwenden();

        // "data/PlanetA-Q1_6x13.txt"
        // "data/PlanetB-Q3_16x50.txt"
        // "data/PlanetB-Q5_50x100.txt"
        ResourceMap rMap = new ResourceMap("data/PlanetB-Q5_50x100.txt");
        System.out.println("Name: " + rMap.getPlanetName());
        System.out.println("Quadrant: Q" + rMap.getQuadrant());
        System.out.println("Value Index: " + rMap.getValueIndex());
    }
}
