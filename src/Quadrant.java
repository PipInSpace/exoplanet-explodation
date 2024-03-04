import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Quadrant {
    // fixed values
    private static final double GOLD_VALUE = 10.0;
    private static final double COPPER_VALUE = 4.0;
    private static final double SILVER_VALUE = 6.0;
    private static final double URANIUM_VALUE = 20.0;
    private static final double ZINC_VALUE = 2.0;


    // Parsed Quadrant information, access over .get methods.
    private final String planetName;
    private final int quadrantIndex;

    // Raw Data
    //private final String path;
    private final char[][] map;

    /**
     * Creates a new Quadrant based on List of String representing the map of the quadrant
     * @param planetName Name of planet
     * @param quadrantIndex Number of quadrant on the planet
     * @param mapList List of String containing map, must be rectangular to avoid unexpected behaviour
     */
    public Quadrant(String planetName, int quadrantIndex, List<String> mapList) {

        this.planetName = planetName;
        this.quadrantIndex = quadrantIndex;

        // Initialize map
        String[] lines = mapList.toArray(new String[0]);
        this.map = new char[lines[0].length()][lines.length];
        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[0].length(); x++) {
                map[x][y] = lines[y].charAt(x);
            }
        }
    }

    /**
     * Creates a new Quadrant base on a file
     * @param path Path to the file
     * @return Quadrant containing info of file
     */
    public static Quadrant fromFile(String path) {
        final String[] pathArr = path.replace('\\', '/').split("/");
        final String fileName = pathArr[pathArr.length - 1];
        // get planet name from path
        final String planet = fileName.split("-")[0];

        // get quadrant id from path
        final int quadrant = Integer.parseInt(fileName.split("-Q")[1].split("_")[0]);

        // read file
        final List<String> map;
        try {
            map = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            Utils.logTS("Error reading file: " + path + " Stacktrace: " + e);
            return null;
        }

        return new Quadrant(planet, quadrant, map);
    }

    /**
     * Calculates value index based on density and type of all resources in the quadrant
     * @return value index
     */
    public double getValueIndex() {
        // get average value per point in the whole quadrant
        return getRawValueIndex() / ((double) (getWidth() * getHeight()));
    }

    /**
     * Calculates value index based on total count and type  of all resources in the quadrant
     * @return raw value index (not accounting for quadrant size)
     */
    public double getRawValueIndex() {
        // go over each point and add the found recource's value
        double rawIndex = 0.0;
        for (char[] line : this.map) {
            for (char ch : line) {
                switch (ch){
                    case 'g':
                        rawIndex += GOLD_VALUE;
                        break;
                    case 'k':
                        rawIndex += COPPER_VALUE;
                        break;
                    case 's':
                        rawIndex += SILVER_VALUE;
                        break;
                    case 'u':
                        rawIndex += URANIUM_VALUE;
                        break;
                    case 'z':
                        rawIndex += ZINC_VALUE;
                        break;
                    default:
                        break;
                }
            }
        }
        return rawIndex;
    }

    /**
     * Calculates density of one resource in a quadrant
     * @param resourceType type to calculate density of
     * @return density (resources/point)
     */
    public double getResourceDensity(Resource.ResourceType resourceType) {
        // get density of a selected resource
        return ((double)getResourceCount(resourceType)) / ((double) (getWidth() * getHeight()));
    }

    /**
     * Calculates total count of one resource in the quadrant
     * @param resourceType type to count
     * @return count
     */
    public int getResourceCount(Resource.ResourceType resourceType) {
        // get density of a selected resource
        int total = 0;
        for (char[] r : this.map) {
            for (char c : r) {
                if(Resource.charToResourceType(c) == resourceType) total++;
            }
        }

        return total;
    }

    /**
     * Gets an array of all resources in the quadrant of one type
     * @param resourceType type to search for
     * @return Resource array of type
     */
    public Resource[] getResourcesOfType(Resource.ResourceType resourceType) {
        // go over map and save coordinates where you find the resource
        List<Resource> resources = new ArrayList<>();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if(Resource.charToResourceType(map[x][y]) == resourceType) resources.add(new Resource(resourceType, x, y));
            }
        }

        return resources.toArray(new Resource[0]);
    }

    /**
     * Gets an array of all resources in the quadrant
     * @return Resource array of all resources excluding none
     */
    public Resource[] getAllResources() {
        // get a map of all points with a resource and the present resource
        List<Resource> resorces = new ArrayList<>();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if(map[x][y] != 'x') resorces.add(new Resource(map[x][y], x, y));
            }
        }

        return resorces.toArray(new Resource[0]);
    }

    public String getPlanetName() {
        return this.planetName == null ? "" : this.planetName;
    }

    public int getQuadrantNumber() {
        return this.quadrantIndex;
    }

    public int getWidth() {
        return map.length;
    }

    public int getHeight() {
        return map[0].length;
    }
}
