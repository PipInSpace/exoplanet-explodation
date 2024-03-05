import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ResourceCluster {
    private Resource.Type resourceType;
    private List<Point> positions;

    public ResourceCluster(Resource.Type type) {
        this.resourceType = type;
        this.positions = new ArrayList<>();
    }

    public ResourceCluster(char typeChar) {
        this(Resource.charToType(typeChar));
    }
}
