import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //EinlesenDemo.einlesen();
        //DBVerbindung db = new DBVerbindung();
        //db.dbVerwenden();

        // "data/PlanetA-Q1_6x13.txt"
        // "data/PlanetB-Q3_16x50.txt"
        // "data/PlanetB-Q5_50x100.txt"

        // Read File
        final String path = "data/PlanetB-Q5_50x100.txt";
        final String planet = path.split("/")[1].split("-")[0];
        final int quadrant = Integer.parseInt(path.split("/")[1].split("-Q")[1].split("_")[0]);
        final List<String> map;
        try {
            map = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // setup quadrant
        Quadrant q = new Quadrant(planet, quadrant, map);
        System.out.println("Name: " + q.getPlanetName());
        System.out.println("Quadrant: Q" + q.getQuadrant());
        System.out.println("Value Index: " + q.getValueIndex());
    }
}
