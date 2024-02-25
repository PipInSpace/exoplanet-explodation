//Importieren einer Klasse mit Methoden zur SQL-Bearbeitung
import java.sql.*;

class DBVerbindung {

    /**
     * Eine Testmethode, die eine einfache Anfrage an die Datenbank stellt und das Ergebnis auf die Konsole ausgibt.
     */
    public void dbVerwenden() {

        // 1. Treiber laden und 2. Herstellen einer Verbindung mit der Datenbank
        Connection conn;
        conn = verbindungHerstellen("data/musikgruppen.sqlite");

        //Die Verwendung von Datenbanken kann vielfältige Fehler erzeugen, vgl. Arbeit mit Dateien 
        try{
            // 3. Erzeugen eines Statements durch das Verbindungs-Objekt
            assert conn != null;
            Statement stmt = conn.createStatement();

            // 4a. Erstellen einer SQL Anfrage: INSERT INTO.
            // Die id wird hier nicht eingefügt, da sie beim Einfügen eines neuen Datensatzes automatisch erzeugt wird (auto-increment).
            // Da dass Anführungszeichen von Java als String Anfang/Ende interpretiert wird, muss es mithilfe des Backslash \ eingegeben werden.
            // String anfragestring = "INSERT INTO Person (Vorname, Nachname) VALUES (\"Tina\", \"Mustermann\");";
            // 5a Ausführen der SQL Anfrage (OHNE Ergebnis)
            //stmt.execute(anfragestring);
            
            // 4b. Erstellen einer SQL Anfrage: INSERT INTO
            String anfragestring = "SELECT * FROM Person;";
            // 5b Ausführen der SQL Anfrage (MIT Ergebnis), das Ergebnis wird als ResultSet gespeichert
            ResultSet rset = stmt.executeQuery(anfragestring);
            
            // 6. Durchgehen der Ergebnismenge, solange es jeweils ein nächstes Ergebnis gibt
            while (rset.next()){
                Utils.logTS(rset.getString("Vorname") + ", " + (rset.getString("Nachname")));     //mit getString- bzw. getInt-Methoden des Ergebnismengen-Objekts jeweils die Daten herausholen
            }
            
            // 7. Ergebnismenge schliessen
            rset.close();
            // 8. Statement schliessen
            stmt.close();
            // 9. Verbindung schliessen     
            conn.close();
            Utils.logTS("Abgemeldet.\n");
        } catch (Exception e) {
            // Fehlerbehebung
            Utils.logTS(e.toString());
        }

    }  

    /**
     * Stellt eine Verbindung zu einer Datenbank her. 
     * Dazu muss die Datei sqlite-jdbc-3.7.2.jar
     * im gleichen Ordner wie die Projektdateien gespeichert werden.
     * Die Operation gibt ein Objekt vom Typ Connection zurück und wird in der nächsten Operation verwendet 
     * NUR NACH RÜCKSPRACHE VERÄNDERN
     * 
     * @param datenbank der Pfad zur Datenbank; kann absolut sein oder relativ zum aktiven Java user.dir -> in BlueJ ist dies das Projektverzeichnis
     * @return          die aufgebaute Verbindung zur Datenbank
     */
    private Connection verbindungHerstellen(String datenbank) {
        Utils.logTS("Ich suche nach der Datenbank in: \n    "+datenbank);
        Utils.logTS("Verbindung zu SQLite Datenbank wird versucht.");

        String treiber = "org.sqlite.JDBC"; // z.B. aus: sqlite-jdbc-3.7.2.jar
        String praefix = "jdbc:sqlite:";  

        try {
            //1. Passenden Treiber laden
            Class.forName(treiber);
            //2. Verbindung zur DB erstellen
            //user und kennwort spielen bei der SQLite Datenbank keine Rolle, sind daher null
            Connection c = DriverManager.getConnection(praefix + datenbank, null, null);
            Utils.logTS("Verbindung zu SQLite Datenbank steht.");
            // Die Verbindung wird zurückgegeben
            return c;
        } catch (Exception e) {
            Utils.logTS("Fehler beim Erstellen der Verbindung: " + e);
            //e.printStackTrace();
            return null;
        }
    }
}