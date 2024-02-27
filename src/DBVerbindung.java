//Importieren einer Klasse mit Methoden zur SQL-Bearbeitung
import java.sql.*;

class DBVerbindung {

    /**
     * Eine Testmethode, die eine einfache Anfrage an die Datenbank stellt und das Ergebnis auf die Konsole ausgibt.
     */
    public void dbVerwenden() {

        // 1. Treiber laden und 2. Herstellen einer Verbindung mit der Datenbank
        Connection conn;
        conn = Utils.dbConnectTo("data/musikgruppen.sqlite");

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
}