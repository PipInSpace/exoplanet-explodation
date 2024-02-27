import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Quadrant fromFile(String path) {
        // get planet name from path
        final String planet = path.replace('\\', '/').split("/")[1].split("-")[0];

        // get quadrant id from path
        final int quadrant = Integer.parseInt(path.split("/")[1].split("-Q")[1].split("_")[0]);

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

    public double getValueIndex() {
        // get average value per point in the whole quadrant
        return getRawValueIndex() / ((double) (getWidth() * getHeight()));
    }

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

    public double getResourceDensity(char resourceID) {
        // get density of a selected resource
        return ((double)getResourceCount(resourceID)) / ((double) (getWidth() * getHeight()));
    }

    public int getResourceCount(char resourceID) {
        // get density of a selected resource
        int total = 0;
        for (char[] r : this.map) {
            for (char c : r) {
                if(c == resourceID) total++;
            }
        }

        return total;
    }

    public Point[] getResourcePositions(char resourceID) {
        // go over map and save coordinates where you find the resource
        List<Point> points = new ArrayList<>();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if(map[y][x] == resourceID) points.add(new Point(x, y));
            }
        }

        return points.toArray(new Point[0]);
    }

    public Map<Point, Character> getAllResourcePositions() {
        // get a map of all points with a resource and the present resource
        Map<Point, Character> resorcePositions = new HashMap<>();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if(map[y][x] != 'x') resorcePositions.put(new Point(x, y), map[y][x]);
            }
        }

        return resorcePositions;
    }

    public String getPlanetName() {
        return this.planetName == null ? "" : this.planetName;
    }

    public int getQuadrant() {
        return this.quadrantIndex;
    }

    public int getWidth() {
        return map[0].length;
    }

    public int getHeight() {
        return map.length;
    }
}
