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

    public Quadrant(String planetName, int quadrantIndex, List<String> mapList) {
        /*
        this.path = path.replace('\\', '/'); // Clean input

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Path.of(this.path));
        } catch (IOException e) {
            System.out.println("Error reading " + this.path + " : " + e);
        }

        this.mapLines = Objects.requireNonNullElseGet(lines, ArrayList::new);

         */
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

    public String getPlanetName() {
        /*
        if (this.planetName == null) {
            String[] fArr = this.path.split("/");
            this.planetName = fArr[fArr.length - 1].split("-")[0];
        }
        */
        return this.planetName == null ? "" : this.planetName;
    }

    public int getQuadrant() {
        /*
        if (this.quadrant == null) {

            String[] fArr = this.path.split("/");
            this.quadrant = Integer.valueOf(fArr[fArr.length - 1].split("-Q")[1].split("_")[0]);
        }
         */
        return this.quadrantIndex;
    }

    public int getWidth() {
        return map[0].length;
    }

    public int getHeight() {
        return map.length;
    }
}
