public class Main {
    public static void main(String[] args) {
        EinlesenDemo.einlesen();
        DBVerbindung db = new DBVerbindung();
        db.dbVerwenden();

        ResourceMap rMap = new ResourceMap("data/PlanetA-Q1_6x13.txt");
        System.out.println("Name: " + rMap.getPlanetName());
        System.out.println("Quadrant: Q" + rMap.getQuadrant());
        System.out.println("Value Index: " + rMap.getValueIndex());
    }
}
