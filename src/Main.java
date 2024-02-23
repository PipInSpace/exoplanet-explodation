public class Main {
    public static void main(String[] args) {
        EinlesenDemo.einlesen();
        DBVerbindung db = new DBVerbindung();
        db.dbVerwenden();
    }
}
