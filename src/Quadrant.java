import java.util.List;

public class Quadrant {
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
        double rawIndex = 0.0;
        int positions = 0;
        for (char[] line : this.map) {
            for (char ch : line) {
                switch (ch){
                    case 'g':
                        rawIndex += 10.0;
                        positions += 1;
                        break;
                    case 'k':
                        rawIndex += 4.0;
                        positions += 1;
                        break;
                    case 's':
                        rawIndex += 6.0;
                        positions += 1;
                        break;
                    case 'u':
                        rawIndex += 20.0;
                        positions += 1;
                        break;
                    case 'z':
                        rawIndex += 2;
                        positions += 1;
                        break;
                    case 'x':
                        positions += 1;
                        break;
                }
            }
        }
        return rawIndex / (double) positions;
    }

    public double getResourceDensity(char resourceID) {
        return ((double)getResourceCount(resourceID)) / ((double) (getWidth() * getHeight()));
    }

    public int getResourceCount(char resourceID) {
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
        return this.planetName;
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
