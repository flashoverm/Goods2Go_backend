# goods2go.backend
Repository des Backend für das Studienprojekt "Goods2Go" an der HAW Landshut. Glückauf!

###Lombok installieren
Damit die IDE auch alle Annotationen kennt, muss noch das Lombok-Plugin installiert werden:
https://projectlombok.org/setup/eclipse


### Login/Session starten
Um eine Session zu starten muss das JSON-Objekt 
{
	"email": "test",
	"password": "test"
}
an den Endpoint "[hostaddress]:[port]/login" mittels POST übertragen werden.
Bei Android kann dies ein serialisiertes User-Objekt mit den entsprechend gesetzten Feldern sein.

Anschließend bekommt man ein JSON-Objekt (bzw. String mit Name-Wert-Paaren) zurück. 
Ein Feld nennt sich hierbei "item". Dieses Feld hat wiederum ein Objekt als Wert.

(In Android entspricht dies der Klasse SessionItem, welches in der SessionResponse-Klasse von Login zurückgegeben wird)
 
Dieses "item-Objekt" besitzt nun das Feld "token". Wie der Name schon sagt, 
hier liegt der Session-Key. Zusätzlich sind die Felder "email" und "role" zu
finden.

(Den Token findet man zusätzlich aber auch im Response-Header-Feld
"Authorization" -> kann also auch daraus gelesen werden)

Alle anderen Endpoints verlangen nun, dass dieser Session-Key im Header des Requests
mitgesendet wird. Genauer gesagt muss man das Header-Feld "Authorization: [token]" hinzufügen.
Beispiel:
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' --header 'Authorization: xxx.xxx.xxx' -d '{ "username": "test", "password": "test" }' 'http://localhost:8080/path/irgendeinendpoint'


Weiter Infos:
- Ein Token ist immer für eine bestimmte Zeit valide, d.h. wenn die Zeit vorbei ist wird
aktuell ein neuer Login erforderlich. Der "Session-Timer" wird nicht resettet durch
nachfolgende Requests (Das ist technisch bedingt und könnte gelöst werden, indem mit
jedem Request praktisch ein neuer Token ausgestellt wird, so ist es auch in Lösungen
aus dem Internet zu finden - hier jetzt allerdings nicht implementiert)
 

### Security abschalten
Um die REST-Schnittstellen ohne vorherigen Login ansprechen zu können
sind folgende Schritte notwendig:
	1. In Datei "application.properties" folgendes Feld einfügen "security.ignored=/**" 
	(Is bereits drinn, also nur das Raute# davor entfernen)
	2. In Klasse "WebSecurityConfig" die Annotation "@EnableWebSecurity" auskommentieren//
 
Das Abschalten von "Security" kann für Entwicklungstests sicher sehr nützlich sein.
Um den Swagger vollständig nutzen zu können muss ebenfalls Security abgeschaltet sein. 
