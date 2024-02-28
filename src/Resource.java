import java.util.ArrayList;
import java.util.List;

/**
 * The Resource Class contains a resource type enum and coordinates.
 * It provides methods to convert the type enum to a String for database use;
 */
public class Resource {
    // Type enum
    public enum Type {
        GOLD,
        COPPER,
        SILVER,
        URANIUM,
        ZINK
    }

    // constants
    private static final String GOLD_STR = "GOLD";
    private static final String COPPER_STR = "COPPER";
    private static final String SILVER_STR = "SILVER";
    private static final String URANIUM_STR = "URANIUM";
    private static final String ZINC_STR = "ZINC";

    // Values
    private final Type type;
    private final int coordX;
    private final int coordY;

    public Resource(char type, int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
        switch (type) {
            case 'g':
                this.type = Type.GOLD;
                break;
            case 'k':
                this.type = Type.COPPER;
                break;
            case 's':
                this.type = Type.SILVER;
                break;
            case 'u':
                this.type = Type.URANIUM;
                break;
            case 'z':
            default:
                this.type = Type.ZINK;
                break;
        }

    }

    public Type getType() {
        return this.type;
    }

    public int getCoordX() {
        return this.coordX;
    }

    public int getCoordY() {
        return this.coordY;
    }

    public String getTypeStr() {
        switch (this.type) {
            case GOLD -> {
                return GOLD_STR;
            }
            case COPPER -> {
                return COPPER_STR;
            }
            case SILVER -> {
                return SILVER_STR;
            }
            case URANIUM -> {
                return URANIUM_STR;
            }
            case ZINK -> {
                return ZINC_STR;
            }
        }
        return "";
    }
}
