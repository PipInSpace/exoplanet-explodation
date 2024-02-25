import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Utils {
    public static Quadrant quadrantFromFile(String path) {
        // get planet name from path
        final String planet = path.replace('\\', '/').split("/")[1].split("-")[0];

        // get quadrant id from path
        final int quadrant = Integer.parseInt(path.split("/")[1].split("-Q")[1].split("_")[0]);

        // read file
        final List<String> map;
        try {
            map = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            logTS("Error reading file: " + path + " Stacktrace: " + e);
            return null;
        }

        return new Quadrant(planet, quadrant, map);
    }

    /**
     * Log a message "msg" with the current time stamp.
     * Time stamp format: "yyyy/MM/dd HH:mm:ss".
     *
     * @param msg The log message string
     */
    public static void logTS(String msg) {
        System.out.println(
                "[" +
                (new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss"))
                        .format(new java.util.Date())
                + "] " + msg
        );
    }
}
