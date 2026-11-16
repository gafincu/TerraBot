# Sistem de Gestionare a Tichetelor

## Descriere
Acest proiect implementează un sistem backend pentru gestionarea fluxului de lucru într-o echipă de dezvoltare software. Aplicația simulează interacțiunile dintre Manageri, Developeri și Reporteri, facilitând raportarea problemelor (Bugs), solicitarea de funcționalități (Feature Requests) și feedback-ul UI.

Sistemul este construit pentru a procesa un flux de comenzi dintr-un fișier JSON, a manipula datele în memorie și a genera rapoarte analitice complexe (risc, eficiență, stabilitate) într-un fișier de ieșire.

## Arhitectură și Implementare

Soluția este modulară, separând logica de business de structura de date prin utilizarea intensivă a **Design Pattern-urilor**. Am ales această abordare pentru a asigura extensibilitatea codului și pentru a evita clasele "God Object" care fac prea multe lucruri.

### Gestionarea Datelor (JSON & Polimorfism)
Pentru serializare și deserializare am folosit biblioteca **Jackson**.
Un detaliu important de implementare este gestionarea polimorfismului direct din JSON. Am adnotat clasele abstracte `User` și `Ticket` cu `@JsonTypeInfo` și `@JsonSubTypes`.
* **Avantaj:** Jackson instanțiază automat subclasa corectă (`Bug`, `Developer`, etc.) pe baza câmpurilor `"type"` sau `"role"`, eliminând necesitatea unor verificări manuale sau cast-uri riscante în timpul parsării.

---

## Design Patterns Utilizate

Am integrat 5 șabloane de proiectare pentru a rezolva probleme specifice de structură și decuplare:

### 1. Singleton Pattern
* **Utilizare:** Clasele `Database` și `CommandFactory`.
* **Motivație:**
    * `Database`: Toate comenzile trebuie să opereze pe aceeași instanță a datelor (liste de useri, tichete, milestone-uri). Singleton asigură consistența stării globale a aplicației.
    * `CommandFactory`: Nu este eficient să recreez mapa de asocieri comanda-constructor la fiecare iterație.

### 2. Command Pattern
* **Utilizare:** Interfața `Command` și implementările din pachetul `command`.
* **Motivație:** Decuplează clasa principală `App` de logica specifică fiecărei acțiuni. `App` doar parcurge lista de input și apelează `command.execute()`, fără să știe dacă se execută un raport sau o asignare. Aceasta respectă principiul *Open/Closed*: pot adăuga comenzi noi fără a modifica bucla principală.

### 3. Factory Pattern (cu Registry)
* **Utilizare:** `CommandFactory`.
* **Motivație:** În loc de un `switch` sau `if-else` masiv pentru a decide ce comandă să instanțiez pe baza string-ului din JSON, am folosit un `Map<String, Function<JsonNode, Command>>`. Această abordare (Registry) transformă căutarea comenzii într-o operație O(1) și face codul mult mai curat.

### 4. Visitor Pattern (Double Dispatch)
* **Utilizare:** Interfețele `TicketVisitor` și `UserVisitor`.
* **Motivație:** Este pattern-ul central pentru logica de business. Am vrut să separ operațiunile (calcule, validări) de structura datelor.
    * **TicketRiskVisitor / ResolutionEfficiencyVisitor:** Calculele matematice pentru risc și eficiență diferă în funcție de tipul tichetului (Bug vs Feature). Visitor-ul gestionează aceste formule separat, păstrând clasele de model curate (doar date).
    * **AssignValidatorVisitor:** Validează permisiunile de asignare. Regulile sunt diferite pentru un Manager (nu poate primi tichete) față de un Developer (verificare skill-uri, senioritate). Prin Double Dispatch, se execută automat metoda corectă pentru tipul de user.

### 5. Builder Pattern
* **Utilizare:** `TicketBuilder`.
* **Motivație:** Construcția unui obiect `Ticket` din JSON este complexă din cauza ierarhiei de clase. Builder-ul încapsulează configurarea `ObjectMapper`-ului și gestionarea excepțiilor, oferind o interfață fluidă pentru crearea instanțelor.

---

## Detalii Tehnice Relevante

### Calculul Eficienței (Resolution Efficiency)
Am implementat calculul duratei de rezolvare folosind `java.time.temporal.ChronoUnit.DAYS` între datele `assignedAt` și `solvedAt`.
* Coeficienții de complexitate sunt stocați ca constante în `ResolutionEfficiencyVisitor` pentru a evita "magic numbers".
* Logica tratează cazurile edge (ex: tichet rezolvat în aceeași zi) prin asigurarea unei durate minime de 1 zi pentru a evita împărțirea la zero.

### Sistemul de Milestone-uri și Dependențe
O provocare a fost gestionarea interdependențelor.
* Un milestone nu poate fi marcat "COMPLETED" dacă blochează un alt milestone activ.
* La închiderea unui tichet (`ChangeStatus`), sistemul verifică recursiv dacă milestone-ul curent se finalizează și dacă acest eveniment deblochează alte milestone-uri, trimițând notificări asincrone developerilor afectați.

### Validări și Constrângeri
Sistemul aplică reguli stricte prin intermediul visitorilor:
* **Senioritate:** Un Junior nu poate prelua tichete "CRITICAL".
* **Expertiză:** Un developer de Backend nu poate prelua tichete de Frontend.
* **Faza de testare:** Raportarea bug-urilor este permisă doar dacă nu există milestone-uri active (faza de testare pornită).
