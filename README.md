# Detektion von Nummernschildern mit Hilfe von Korrelationsverfahren
## Bildverarbeitung Abschlussprojekt

Verantwortlich: [Torsten Rauch](https://github.com/ToRauch), [Julian Knaup](https://github.com/julianknaup), [Alissa Müller](https://github.com/chaosbambi) & [Jan-Philipp Töberg](https://github.com/Janfiderheld)

Projektbetreuer: Dipl.-Inform. Jan Leif Hoffmann & Prof. Dr.-Ing. Volker Lohweg 


Startdatum: 23.01.2019

Enddatum & Demonstration: 08.04.2019

## Aufgabenstellung

Es sollen deutsche PKW-Standardnummernschildern detektiert und inhaltlich erkannt werden. Dazu muss zunächst eine alpha-numerische Bildreferenz erzeugt werden.
Hierzu kann z. B. [die folgende Sammlung](http://www.dna.nl/germany.htm) dienen. Es können auch andere Datenbanken verwendet werden. 
Verwenden Sie auch die Ihnen zur Verfügung gestellte Literatur, um sich in das Thema einzuarbeiten. 
Es sollen Nummernschilder und deren Inhalte detektiert werden. Es ist nicht notwendig, Fahrzeuge zu erkennen, um daraus die Position der Nummernschilder zu extrahieren.  

1. Untersuchen Sie, welche Daten- bzw. Datenbank für die Aufgabenstellung nützlich sind.  
2. Begründen Sie die Verwendung einer spezifischen Datenbank für alpha-numerische Zeichen. 
3. Generieren Sie eine Zeichendatenbank, die Sie für die weiteren Untersuchungen verwenden. 
4. Entwickeln Sie ein Konzept zur Detektion von Nummernschildern und deren alphanumerischen Inhalten. Speichern Sie die Inhalte der Nummernlesung in eine Excel-Tabelle. Wie hoch ist die Erkennungsrate? 
5. Wie lässt sich eine Klassifikation durchführen? Implementieren Sie einen geeigneten Klassifikator.  

Entwickeln Sie für die oben genannte Aufgabenstellung ein Software-Bildverarbeitungssystem in Java unter der Zuhilfenahme des Programms ImageJ.
  
Beantworten Sie insbesondere auch folgende Fragen: 

1. Inwiefern ist der Einsatz einer Shading-Korrektur sinnvoll oder notwendig? 
2. Wie kann eine Beleuchtungsadaption vorgenommen werden? 
3. Was für eine Segmentierung kommt zum Einsatz? Welche Alternativen gibt es? Wie wird die Segmentierung ausgeführt? 
4. Inwieweit ist es sinnvoll oder notwendig, morphologische Operationen in der Vorverarbeitung einzusetzen? 
5. Lassen sich alle Zeichen voneinander unterscheiden? Wie beurteilen Sie die Klassifikationsrate?  
6. Wie bewerten Sie den Rechenaufwand? 

## Anleitung
### Laden des Projekts in Eclipse

Um das Projekt in Eclipse zu öffnen, gehen Sie auf File -> Import... -> Maven -> Existing Maven Projects und dann auf Next.
Nun muss das Verzeichnis aufgerufen werden, in welchem sich das Projekt befindet. Nach dieser Auswahl sollte die benötigte pom-Datei bereits ausgewählt sein.
Mit dem Finish-Button kann der Import abgeschlossen werden.

### Öffnen des PlugIns mit ImageJ

Um das PlugIn in ImageJ zu öffnen, muss das Projekt gebaut werden. Sobald das abgeschlossen ist, kann die entstandene .jar-Datei in den Plugins-Ordner in ImageJ geladen werden.
Danach kann in ImageJ über den Reiter "Gruppe A" das "Abschlussprojekt"-PlugIn geladen werden.

### Hinweise zur GUI

Nach der Ausführung des PlugIns öffnet sich eine GUI, welche mit einer Auswahl über zwei RadioButtons beginnt.
Hier kann ausgewählt werden, ob ein einzelnes Bild oder ein Ordner von Bildern im nächsten Schritt ausgewählt werden soll.
Im nächsten Schritt muss eine Datei oder ein Ordner ausgewählt werden. Zur Kontrolle wird der gewählte Speicherort angezeigt.
Als nächstes muss der Speicherort für die CSV-Datei mit den Ergebnissen angegeben werden. Dabei kann eine bereits existierende Datei ausgewählt werden, oder eine neue Datei erstellt werden.
Zum Abschluss kann eingestellt werden, ob die Klassifikationsrate angezeigt werden soll und ob die Datei vorm Beschreiben erst geleert werden soll.
Bei der Klassifikationsrate gilt zu beachten, dass die Angabe nur repräsentativ ist, wenn die Namen der Bilder folgender Konvention entsprechen:
Kennzeichen HX SR 206 --> HX_SR_206.jpg

## Coding Guidelines (basierend auf denen aus Mobile Systeme)

Die Einhaltung der folgenden Konventionen wird von der Java-Sprachsyntax nicht zwingend vorgeschrieben. Ihre Einhaltung erhöht die Lesbarkeit der Programmquellen allerdings ungemein. Nur durch die Beachtung von einheitlichen Programmierkonventionen kann umfangreicherer Quellcode mit vertretbaren Aufwand gereviewed, gepflegt und ergänzt werden. Die Einhaltung folgender Konventionen wird empfohlen (vgl. auch mit SUNs Empfehlungen Code Conventions for the Java Programming Language):
- Schreibweisen (s.a. Code Conventions for the Java Programming Language, Kap. 9):
  - Klassen- und Interfacenamen beginnen mit einem Großbuchstaben und sollten ein Substantiv sein
  - Methodennamen beginnen mit einem Kleinbuchstaben und sollten ein Verb sein
  - Variablen beginnen mit einem Kleinbuchstaben
  - Konstanten (final deklarierte Variablen) werden nur aus Großbuchstaben und dem Underscore-Zeichen '_' zusammengesetzt
  - Paketnamen (Packages) beginnen mit einem Kleinbuchstaben
  - CamelCase & englische Sprache
- Der Zweck von Klassen, Methoden und Variablen, die nicht nur lokal in einer Methode genutzt werden, ist durch eine entsprechend sinnvolle Namensgebung deutlich zu machen. Sind ausführlichere Erklärungen zu Zweck und Nutzung nötig, so sind diese in einer (Javadoc-)Kommentierung anzugeben
- Anweisungen (Statements):
  - Nur eine Anweisung je Zeile
  - Keine Zeile länger als 100 Zeichen (auch Kommentare!)
  - Zeilen innerhalb eines Anweisungsblocks (zwischen den geschweiften Klammern {...}) einrücken (4 Leerzeichen)
  - Für Verzweigungsbedingungen (if(..), else if(..), else) und Schleifenanfänge (for(..;..;..), while(..), do) ist jeweils eine eigene Zeile zu nutzen. (Auch eine einzelne return-Anweisung nach einer if-Abfrage sollte (eingerückt) auf eine eigene Zeile geschrieben werden.)
- Exceptions sollten in aller Regel nicht mit einer leeren catch-Anweisungen weggefangen werden (Empfehlung: printStackTrace() verwenden)