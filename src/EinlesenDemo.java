//Importieren von  Klassen mit Methoden zur Datei-Bearbeitung
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EinlesenDemo {

    /**
     * Die Operation demonstriert das zeilenweise Einlesen einer Datei Testdatei.txt.
     * Die eingelesenen Zeilen werden in der Demo direkt auf dem Bildschirm ausgegeben.
     * Es ist bekannt, dass die Testdatei 6 Zeilen enthält. 
     */
    public static void einlesen() {

        // Da bei der Arbeit mit Dateien vielfältige Fehler auftreten können, muss
        // eine sogenannte "Fehlerbehandlung" erfolgen.
        // Dies wird in Java mit einer try-catch-Struktur umgesetzt.
        // Wenn möglich wird der try-Block ausgeführt.
        // Sollte ein Fehler auftreten (z. B. Datei existiert nicht, fehlende
        // Leseberechtigung, etc.) wird der Fehler im catch-Block aufgefangen.

        try {
            // Auslesen aller Zeilen der Datei. 
            // Es wird eine Liste mit den einzelnen Zeilen (eingelesen als Strings) erstellt.
            String fileName = "data/Testdatei.txt" ;
            List<String> lines = Files.readAllLines(Path.of(fileName));

            // Hier wird über die einzelnen Zeilen iteriert. Jede Zeile wird auf der Konsole ausgegeben.
            // lines.size() liefert die Länge der Liste (hier also die Anzahl der Zeilen)
            // lines.get(i) liefert die i-te Zeile (als String)
            //        
            for (int i = 0; i < lines.size(); i++) {
                // Die Zeilen können auf der Konsole ausgegeben werden ...
                System.out.println(lines.get(i));

            }

        } catch (IOException ioex) {
            System.out.println("Lesefehler: " + ioex);
        }
    }    

}