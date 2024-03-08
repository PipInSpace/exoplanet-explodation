import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Update Database
        Utils.updateDatabase("data/cartography.sqlite", "data/quadrants");

        // Fetch from database
        List<Quadrant> quadrants = Utils.quadrantsListFromDB("data/cartography.sqlite");

        // Print quadrant
        Quadrant q = quadrants.getFirst();
        assert q != null;
        Utils.printQuadrantInfo(q);

        for(ResourceCluster c : q.getClusters('z')) {
            System.out.println(c.getSize());
        }
    }
}
