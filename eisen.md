# APP Compiler opdracht
Christiaan Smits 1645750
31-10-2025, Versie 2
Dennis Breuker
APP


Het product voldoet aan de eisen van de opdracht zoals beschreven in assignment.md:

## Algemeen

- De code behoudt de packagestructuur van de aangeleverde startcode. Toegevoegde code bevindt zich in de relevante packages.
- Alle code compileert en is te bouwen met Maven 3.6 of hoger, onder OpenJDK 13. Tip: controleer dit door **eerst** `mvn clean` uit te voeren alvorens te compileren en in te leveren, hierop een onvoldoende halen is echt zonde. **Gebruik van Oracle versies van Java is uitdrukkelijk niet toegestaan**.
- De code is goed geformatteerd, zo nodig voorzien van commentaar, correcte variabelenamen gebruikt, bevat geen onnodig ingewikkelde constructies en is zo onderhoudbaar mogelijk opgesteld. (naar oordeel van docent)
- De docent heeft vastgesteld (tijdens les, assessment of op een andere manier) dat de compiler eigen werk is en dat je voldoet aan de beoordelingscriteria van APP-6, te weten: - Kent de standaardarchitectuur van compilers; - Kent de basisbegrippen over programmeertalen (zoals syntaxis, semantiek).

## Parser

- De parser dient zinvol gebruik te maken van **jouw** eigen implementatie van een stack generic voor `ASTNode` (VT: zie huiswerk `IHANStack<ASTNode>`)
- Implementeer een grammatica plus listener die AST’s kan maken voor ICSS documenten die “eenvoudige opmaak” kan parseren, zoals beschreven in de taalbeschrijving. In `level0.icss` vind je een voorbeeld van ICSS code die je moet kunnen parseren. `testParseLevel0()` slaagt.
- Breid je grammatica en listener uit zodat nu ook assignments van variabelen en het gebruik ervan geparseerd kunnen worden. In `level1.icss` vind je voorbeeldcode die je nu zou moeten kunnen parseren. `testParseLevel1()` slaagt.
- Breid je grammatica en listener uit zodat je nu ook optellen en aftrekken en vermenigvuldigen kunt parseren. In `level2.icss` vind je voorbeeld- code die je nu ook zou moeten kunnen parseren. Houd hierbij rekening met de rekenregels (vermenigvuldigen gaat voor optellen en aftrekken, optellen en aftrekken gaan van links naar rechts; zie ook [deze site](https://www.beterrekenen.nl/website/index.php?pag=217).”`testParseLevel2()` slaagt.
- Breid je grammatica en listener uit zodat je if/else-statements aankunt. In `level3.icss` vind je voorbeeldcode die je nu ook zou moeten kunnen parseren. `testParseLevel3()` slaagt.
- PA01 t/m PA04 leveren minimaal 30 punten op

## Checker

- Minimaal vier van onderstaande checks **moeten** zijn geïmplementeerd
    - Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn.
    - Controleer of de operanden van de operaties plus en min van gelijk type zijn. Je mag geen pixels bij percentages optellen bijvoorbeeld. Controleer dat bij vermenigvuldigen minimaal een operand een scalaire waarde is. Zo mag `20% * 3` en `4 * 5` wel, maar mag `2px * 3px` niet.
    - Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer).
    - Controleer of bij declaraties het type van de value klopt met de property. Declaraties zoals `width: #ff0000` of `color: 12px` zijn natuurlijk onzin.
    - Controleer of de conditie bij een if-statement van het type boolean is (zowel bij een variabele-referentie als een boolean literal)
- Controleer of variabelen enkel binnen hun scope gebruikt worden

## Transformeren

- Evalueer expressies. Schrijf een transformatie in `Evaluator` die alle `Expression` knopen in de AST door een `Literal` knoop met de berekende waarde vervangt.
- Evalueer if/else expressies. Schrijf een transformatie in `Evaluator` die alle `IfClause`s uit de AST verwijdert. Wanneer de conditie van de `IfClause` `TRUE` is wordt deze vervangen door de body van het if-statement. Als de conditie `FALSE` is dan vervang je de `IfClause` door de body van de `ElseClause`. Als er geen `ElseClause` is bij een negatieve conditie dan verwijder je de `IfClause` volledig uit de AST.

## Genereren

- Implementeer de generator in `nl.han.ica.icss.generator.Generator` die de AST naar een CSS2-compliant string omzet.
- Zorg dat de CSS met twee spaties inspringing per scopeniveau gegenereerd wordt


### Eigen uitbreiding

Niks overeengekomen en geen eigen uitbreidingen geïmplementeerd.

## Beperkingen

Bepekeringen waar rekening mee is gehouden:

- Selectoren zijn allemaal lowercase.
- Selectoren selecteren maar op één ding tegelijk. Combinaties zoals `a.active` zijn niet toegestaan.
- Selectoren voor het selecteren van kinderen uit CSS zoals `div > a` zijn niet toegestaan.
- Alleen de properties `color`, `background-color`, `width` en `height` zijn toegestaan.
- Voor kleuren (`color` en `background-color`) moet de waarde als een hexadecimale waarde van zes tekens opgegeven worden (bijv. `#00ff00`)
- Voor groottes mag of een waarde in pixels (bijv. `100px`) of een percentage (bijv. `50%`) gespecifieerd worden.

Bron: Koolwaaij, M. (z.d.). Assignment. GitHub. Geraadpleegd op 31 oktober 2025, van https://github.com/ChrisCzsSCHOOL/icss2022-sep/blob/main/ASSIGNMENT.md