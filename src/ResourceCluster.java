import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ResourceCluster {
    private final Resource.Type resourceType;
    private final List<Point> positions;

    public ResourceCluster(Resource.Type type) {
        this.resourceType = type;
        this.positions = new ArrayList<>();
    }

    public ResourceCluster(char typeChar) {
        this(Resource.charToType(typeChar));
    }

    public int getSize() {
        return this.positions.size();
    }

    public Point[] getPositions() {
        return this.positions.toArray(new Point[0]);
    }

    public void addPosition(Point p) {
        this.positions.add(p);
    }

    public boolean addResourcePosition(Resource r) {
        // safety in case of wrong type, should never happen
        if(r.getType() != this.resourceType) return false;
        this.addPosition(new Point(r.getCoordX(), r.getCoordY()));
        return true;
    }
}
