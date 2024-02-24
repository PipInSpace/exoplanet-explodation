import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceMap {
    // Parsed Map information, access over .get methods.
    private String planetName;
    private Integer quadrant;
    private Double valueIndex;

    // Raw Data
    private final String path;
    private final List<String> mapLines;

    public ResourceMap(String path) {
        this.path = path.replace('\\', '/'); // Clean input

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Path.of(this.path));
        } catch (IOException e) {
            System.out.println("Error reading " + this.path + " : " + e);
        }

        this.mapLines = Objects.requireNonNullElseGet(lines, ArrayList::new);
    }

    public double getValueIndex() {
        if (this.valueIndex == null) {
            // Do calculation here.
            // Is that your no-work-until-needed approach?
            double rawIndex = 0.0;
            int positions = 0;
            for (String line : this.mapLines) {
                for (char ch : line.toCharArray()) {
                    positions += 1;
                    switch (ch){
                        case 'g':
                            rawIndex += 10.0;
                            break;
                        case 'k':
                            rawIndex += 4.0;
                            break;
                        case 's':
                            rawIndex += 6.0;
                            break;
                        case 'u':
                            rawIndex += 20.0;
                            break;
                        case 'z':
                            rawIndex += 2;
                            break;
                    }
                }
            }
            this.valueIndex = rawIndex / (double) positions;
        }
        return this.valueIndex;
    }

    public String getPlanetName() {
        if (this.planetName == null) {
            String[] fArr = this.path.split("/");
            this.planetName = fArr[fArr.length - 1].split("-")[0];
        }
        return this.planetName;
    }

    public int getQuadrant() {
        if (this.quadrant == null) {
            String[] fArr = this.path.split("/");
            this.quadrant = Integer.valueOf(fArr[fArr.length - 1].split("-Q")[1].split("_")[0]);
        }
        return this.quadrant;
    }
}
