import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
     * Konstruktor aus Planetenname, Nummer und rohen Text-Daten.
     * @param planetName Planetenname, aus Dateiname ausgelesen
     * @param quadrantIndex Quadrantennummer, aus Dateiname ausgelesen
     * @param mapList Rohe Text-Daten, aus Datei ausgelesen
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
     * Konstruktor aus Planetenname, Nummer und Liste an Resourcen.
     * @param planetName Planetenname, aus Datenbank ausgelesen
     * @param quadrantIndex Quadrantennummer, aus Datenbank ausgelesen
     * @param resources Liste an Resourcen, aus Datenbank ausgelesen
     */
    public Quadrant(String planetName, int quadrantIndex, int width, int height, List<Resource> resources) {
        this.planetName = planetName;
        this.quadrantIndex = quadrantIndex;
        char[][] map = new char[width][height];

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = 'x';
            }
        }

        for (Resource res : resources) {
            map[res.getCoordX()][res.getCoordY()] = res.getTypeChar();
        }

        this.map = map;
    }

    /**
     * Erstellt einen Quadranten aus einer Text-Datei mit bestimmtem Namen.
     * @param path Dateipfad zur Quadranten-Datei
     * @return Ausgelesener Quadrant
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
     * Gibt einen Wert-Index aus Resourcendichte und -wert zurück.
     * @return Wertung des Quadranten
     */
    public double getValueIndex() {
        // get average value per point in the whole quadrant
        return getRawValueIndex() / ((double) (getWidth() * getHeight()));
    }

    /**
     * Gibt einen Wert-Index aus dem gesamten Resourcenwert zurück.
     * @return Rohe Wertung des Quadranten
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
     * Gibt die Dichte einer bestimmten Resource in einem Quadranten zurück.
     * @param resourceID Gesuchte Resource als char
     * @return Dichte der Resource
     */
    public double getResourceDensity(char resourceID) {
        // get density of a selected resource
        return ((double)getResourceCount(resourceID)) / ((double) (getWidth() * getHeight()));
    }

    /**
     * Gibt die Gesamtanzahl einer bestimmten Resource in einem Quadranten zurück.
     * @param resourceID Gesuchte Resource als char
     * @return Gesamtanzahl der Resource
     */
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

    /**
     * Gibt eine Liste aller Resourcen in einem Quadranten zurück.
     * @return Liste an Resourcen
     */
    public List<Resource> getResourceList() {
        List<Resource> resources = new ArrayList<>();
        for (int y = 0; y < this.map[0].length; y++) {
            for (int x = 0; x < this.map.length; x++) {
                char ch = map[x][y];
                if (ch == 'g' || ch == 'k' || ch == 's' || ch == 'u' || ch == 'z') {
                    resources.add(new Resource(ch, x, y));
                }
            }
        }

        return resources;
    }

    /**
     * Gibt ein Array an Koordinaten zurück, an denen sich eine bestimmte Resource befindet.
     * @param resourceID Gesuchte Resource als char
     * @return Array an Punkten, an denen die Resource vorhanden ist.
     */
    public Point[] getResourcePositions(char resourceID) {
        // go over map and save coordinates where you find the resource
        List<Point> points = new ArrayList<>();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if(map[x][y] == resourceID) points.add(new Point(x, y));
            }
        }

        return points.toArray(new Point[0]);
    }

    /**
     * Findet alle benachbarten Resourcen einer Art und gruppiert sie in mehrere ResourceCluster.
     * @param resourceID Gesuchte Resource als char
     * @return Array aus ResourceCluster mit benachbarten Resourcen
     */
    public ResourceCluster[] getClusters(char resourceID) {
        // Liste der Cluster, die später augegeben wird
        List<ResourceCluster> clusters = new ArrayList<>();

        // Liste der Positionen aller Resourcen einer Art, die noch keinem Cluster zugeordnet sind
        List<Point> positions = new LinkedList<>(Arrays.asList(getResourcePositions(resourceID)));
        if(positions.isEmpty()) return new ResourceCluster[0];
        while (!positions.isEmpty()) {
            // Neuen Cluster erstellen und das erste Element der Positionsliste hinzufügen
            ResourceCluster cluster = new ResourceCluster(resourceID);
            cluster.addPosition(positions.getFirst());

            // Liste mit Positionen, bei denen noch geprüft werden muss, ob sie einen Nachbar gleicher Art haben, der auch zum Cluster gehört
            List<Point> toScan = new LinkedList<>();
            toScan.add(positions.getFirst());

            // Da das erste Element einem Cluster zugeordnet ist, kann es in keinem anderen sein und wir aus der Positionsliste entfernt.
            positions.removeFirst();

            // Wiederholung, bis alle Positionen im Cluster auf ihre Nachbarn überprüft wurden
            while(!toScan.isEmpty()) {
                Point current = toScan.getFirst();
                toScan.removeFirst();
                // Liste aller Positionen, die in diesem Durchgang einem Cluster zugeordnet werden können
                // Können beim Iterieren über positions noch nicht entfernt werden
                List<Point> markForRemove = new ArrayList<>();
                for (Point p : positions) {
                    // falls p ein Nachbar von current ist
                    if((p.x == current.x + 1 && p.y == current.y) || (p.x == current.x - 1 && p.y == current.y) || (p.y == current.y + 1 && p.x == current.x) || (p.y == current.y - 1 && p.x == current.x)) {
                        // p muss im nächsten Durchgang auf Nachbarn geprüft werden
                        toScan.add(p);
                        // p gehört zum aktuellen Cluster
                        cluster.addPosition(p);
                        // p muss aus positions entfernt werden, da er zugeordnet wurde
                        markForRemove.add(p);
                    }
                }
                // alle Positionen entfernen, die zugeordnet wurden
                for (Point p : markForRemove) {
                    positions.remove(p);
                }
            }

            // vollständigen Cluster zur Liste hinzufügen
            clusters.add(cluster);
        }
        // Rückgabe aller ResourceCluster als Array
        return clusters.toArray(new ResourceCluster[0]);
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
