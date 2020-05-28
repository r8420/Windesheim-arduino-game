#####################
#      README       #
#####################

Om dit project te kunnen runnen zijn er een aantal stappen die gedaan moeten worden



# 1. Arduino
#####################
Ga naar bladzijde 5 van het "TO Game" en gebruik het elektrisch schema dat daat staat om je arduino/breadboard goed in te stellen.
Sluit de arduino aan op je computer
Navigeer naar de 'arduino' map en open het .ino bestand dat daar in staat.
Upload dit bestand naar de Arduino.
Sluit de Arduino IDE of zorg er in ieder geval voor dat de Serial comm niet open staat.

	
	
# 2. IntelliJ
#####################
Open intelliJ en selecteer Open...
Navigeer naar de projectfolder en open de folder "javafx" als een project.
Het kan zijn dat je een ander java JDK hebt dan wij, dan moet je dat even aanpassen in de projectsettings.

als je geen javafx op je computer hebt staan ga dan naar deze site en download de recentse versie: https://gluonhq.com/products/javafx/ 
Stel javafx in als een library van je project Door te gaan naar "File > Project Structure"
Een scherm opent, klik aan de linkerkant op "Libraries"
Druk nu bovenaan dit scherm op het + teken en selecteer "Java" in de drop-down
navigeer naar waar je de javafx library hebt opgeslagen en selecteer de sub-folder "lib" ervan
Druk bij alles op Ok en sluit het scherm

Ga naar "Run > Edit configurations"
Ga naar het textvak "VM options" en kopieer het volgende stuk text erin
Verander wel eerst de string naar de goede bestandslocatie/naam van jouw javafx lib map

	--module-path "\Program Files\Java\javafx-sdk-14.0.1\lib" --add-modules javafx.controls,javafx.fxml,javafx.media

Druk op Ok, je moet nu StartupScreen kunnen runnen om het spel te spelen
Je kunt de knoppen op de arduino gebruiken om door menu's te navigeren, en de potmeter gebruiken om de magneet te besturen.
