import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Utils {
    public static Quadrant quadrantFromFile(String path) {
        final String pathClean = path.replace('\\', '/'); // Clean input

        // get planet name from path
        final String planet = pathClean.split("/")[1].split("-")[0];

        // get quadrant id from path
        final int quadrant = Integer.parseInt(pathClean.split("/")[1].split("-Q")[1].split("_")[0]);

        // read file
        final List<String> map;
        try {
            map = Files.readAllLines(Path.of(pathClean));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new Quadrant(planet, quadrant, map);
    }
}
