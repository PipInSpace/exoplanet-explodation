import java.awt.Point;

public class Resource {
    /**
     * Enum containing all types of resource and None
     */
    public enum ResourceType {
        GOLD,
        COPPER,
        SILVER,
        URANIUM,
        ZINC,
        NONE
    }

    private ResourceType resourceType;

    private int x, y;

    public Resource(ResourceType resourceType, int x, int y) {
        this.x = x;
        this.y = y;
        this.resourceType = resourceType;
    }

    public Resource(char resourceCh, int x, int y) {
        this(charToResourceType(resourceCh), x, y);
    }

    public static ResourceType charToResourceType(char c) {
        return switch (c) {
          case 'g' -> ResourceType.GOLD;
          case 'k' -> ResourceType.COPPER;
          case 's' -> ResourceType.SILVER;
          case 'u' -> ResourceType.URANIUM;
          case 'z' -> ResourceType.ZINC;
          default -> ResourceType.NONE;
        };
    }

    public static char resourceTypeToChar(ResourceType rType) {
        return switch (rType) {
            case ResourceType.GOLD -> 'g';
            case ResourceType.COPPER -> 'k';
            case ResourceType.SILVER -> 's';
            case ResourceType.URANIUM -> 'u';
            case ResourceType.ZINC -> 'z';
            default -> 'x';
        };
    }

    public static String resourceTypeToName(ResourceType type) {
        return switch (type) {
            case ResourceType.GOLD -> "Gold";
            case ResourceType.COPPER -> "Kupfer";
            case ResourceType.SILVER -> "Silber";
            case ResourceType.URANIUM -> "Uran";
            case ResourceType.ZINC -> "Zink";
            default -> "";
        };
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Point getPosition() {
        return new Point(x, y);
    }
}
