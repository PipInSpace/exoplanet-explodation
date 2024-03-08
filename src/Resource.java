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
    public static final String GOLD_STR = "GOLD";
    public static final String COPPER_STR = "COPPER";
    public static final String SILVER_STR = "SILVER";
    public static final String URANIUM_STR = "URANIUM";
    public static final String ZINC_STR = "ZINC";
    public static final char GOLD_CHAR = 'g';
    public static final char COPPER_CHAR = 'k';
    public static final char SILVER_CHAR = 's';
    public static final char URANIUM_CHAR = 'u';
    public static final char ZINC_CHAR = 'z';

    // Values
    private final Type type;
    private final int coordX;
    private final int coordY;

    /**
     * Konstruktor aus Resourcen-Typ als char, Koordinaten
     * 
     * @param typeCh Resourcen-Typ als char
     * @param coordX x-Koordinate
     * @param coordY y-Koordinate
     */
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

    /**
     * Konstruktor aus Resourcen-Typ als String, Koordinaten
     * 
     * @param typeStr Resourcen-Typ als String
     * @param coordX x-Koordinate
     * @param coordY y-Koordinate
     */
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

    /**
     * Gibt den Typ der Resource als String zurück
     * 
     * @return Resourcentyp als String
     */
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

    /**
     * Gibt den Typ der Resource als char zurück
     * 
     * @return Resourcentyp als char
     */
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
