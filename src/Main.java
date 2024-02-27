import java.util.List;

public class Main {
    public static void main(String[] args) {
        // "data/PlanetA-Q1_6x13.txt"
        // "data/PlanetB-Q3_16x50.txt"
        // "data/PlanetB-Q5_50x100.txt"

        // load paths to all quadrants
        List<String> paths = Utils.openFolder("data/quadrants");

        // setup quadrant
        for(String path : paths) {
            Quadrant q = Quadrant.fromFile(path);
            assert q != null;
            System.out.println("Loading " + q.getPlanetName() + ", Quadrant " + q.getQuadrant() + ":");
            System.out.println("Width: " + q.getWidth() + ", Height: " + q.getHeight());
            System.out.println("Total Value Index: " + q.getValueIndex());
            System.out.println("Resorces:");
            // iterate over all resources
            for (char c : new char[]{'g', 'k', 's', 'u', 'z'}) {
                System.out.println("    " + c +": Total: " + q.getResourceCount(c) + ", Density: " + q.getResourceDensity(c));
            }
            System.out.println();
        }
        /*
        Utils.logTS("Name: " + q.getPlanetName());
        Utils.logTS("Quadrant: Q" + q.getQuadrant());
        Utils.logTS("Value Index: " + q.getValueIndex());
        Utils.logTS("Resource Count Gold: " + q.getResourceCount('g'));
        Utils.logTS("Resource Density Gold: " + q.getResourceDensity('g'));

         */
    }
}
