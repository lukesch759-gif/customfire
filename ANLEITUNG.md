# Custom Fire – Fabric-Mod für Minecraft 1.21.11

Wenn du brennst, kannst du mit **Rechts-Shift** ein Menü öffnen und einstellen:

- **Feuer-Anzeige AN/AUS** – komplett ausschalten
- **Höhe** (0–100 %) – 100 % = normales Minecraft-Feuer, 50 % = etwa wie „Low Fire“, 0 % = weg
- **Deckkraft** – Feuer durchsichtiger machen
- **Textur** – Normal / Seelenfeuer (blau) / Bunt (eigene Flammen, die sich in jede Farbe färben lassen)
- **Rot / Grün / Blau** + Vorlagen (Rot, Orange, Gelb, Grün, Blau, Lila, Pink, Weiß, Seelenfeuer, Original)

Die Einstellungen werden in `.minecraft/config/customfire.json` gespeichert.
Die Taste kannst du unter *Optionen → Steuerung → Custom Fire* ändern.

> Tipp: Bei Textur „Normal“ wird die Farbe mit dem Orange gemischt (Blau sieht dann dunkel aus).
> Für knallige Farben die Vorlagen benutzen – die stellen automatisch „Bunt“ ein.

## Die .jar bekommen (ohne etwas zu installieren)

1. Auf https://github.com kostenlos anmelden.
2. Oben rechts **+ → New repository**, Name z. B. `customfire`, **Create repository**.
3. Auf der leeren Seite **„uploading an existing file“** klicken und **alles aus diesem Ordner**
   (auch den Ordner `.github`!) hineinziehen → **Commit changes**.
   *Falls der Ordner `.github` nicht mit hochgeht:* „Add file → Create new file“, als Name
   `.github/workflows/build.yml` eintippen und den Inhalt dieser Datei hineinkopieren.
4. Reiter **Actions** öffnen → der Lauf „Mod bauen“ startet von allein (ca. 3–5 Minuten).
5. Wenn er grün ist: draufklicken → unten bei **Artifacts** `customfire-mod` herunterladen,
   ZIP entpacken → darin liegt `customfire-1.0.0.jar`.

## In Modrinth einbauen

1. Modrinth App → dein Profil (Fabric, **1.21.11**) → **Content** → **Add content** / Datei hineinziehen:
   `customfire-1.0.0.jar`.
2. **Fabric API** muss auch im Profil sein (in Modrinth einfach suchen und installieren).
3. Spiel starten, irgendwo reinlaufen wo's brennt, **Rechts-Shift** drücken.

## Selbst bauen (wenn du Java 21 + Gradle hast)

```
gradle build
```
Die Mod liegt dann in `build/libs/`.
