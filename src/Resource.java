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
        ZINC,
        NONE
    }

    // constants
    private static final String GOLD_STR = "GOLD";
    private static final String COPPER_STR = "COPPER";
    private static final String SILVER_STR = "SILVER";
    private static final String URANIUM_STR = "URANIUM";
    private static final String ZINC_STR = "ZINC";
    private static final char GOLD_CHAR = 'g';
    private static final char COPPER_CHAR = 'k';
    private static final char SILVER_CHAR = 's';
    private static final char URANIUM_CHAR = 'u';
    private static final char ZINC_CHAR = 'z';

    // Values
    private final Type type;
    private final int coordX;
    private final int coordY;

    public Resource(char typeCh, int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.type = switch (typeCh) {
            case GOLD_CHAR -> Type.GOLD;
            case COPPER_CHAR -> Type.COPPER;
            case SILVER_CHAR -> Type.SILVER;
            case URANIUM_CHAR -> Type.URANIUM;
            case ZINC_CHAR -> Type.ZINC;
            default -> Type.NONE;
        };
    }

    public Resource(String typeStr, int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.type = switch (typeStr) {
            case GOLD_STR -> Type.GOLD;
            case COPPER_STR -> Type.COPPER;
            case SILVER_STR -> Type.SILVER;
            case URANIUM_STR -> Type.URANIUM;
            case ZINC_STR -> Type.ZINC;
            default -> Type.NONE;
        };
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
        return switch (this.type) {
            case GOLD -> GOLD_STR;
            case COPPER -> COPPER_STR;
            case SILVER -> SILVER_STR;
            case URANIUM -> URANIUM_STR;
            case ZINC -> ZINC_STR;
            default -> "";
        };
    }

    public char getTypeChar() {
        return switch (this.type) {
            case GOLD -> GOLD_CHAR;
            case COPPER -> COPPER_CHAR;
            case SILVER -> SILVER_CHAR;
            case URANIUM -> URANIUM_CHAR;
            case ZINC -> ZINC_CHAR;
            default -> 'x';
        };
    }

    public static Type charToType(char c) {
        return switch (c) {
            case GOLD_CHAR -> Type.GOLD;
            case COPPER_CHAR -> Type.COPPER;
            case SILVER_CHAR -> Type.SILVER;
            case URANIUM_CHAR -> Type.URANIUM;
            case ZINC_CHAR -> Type.ZINC;
            default -> Type.ZINC;
        };
    }
}
