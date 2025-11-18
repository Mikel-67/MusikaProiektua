<h2>Musika proiektua</h2>
Aplikazio hau Android Studio erabiliz garatutako sare sozial estiloko plataforma bat da, Letterboxd ereduan oinarrituta baina musikaren mundura egokituta. Erabiltzaileek kantuak baloratu ditzakete (nota batekin), eta balorazio horiek post moduan argitaratuko dira. Argitaratutako post-ak beste erabiltzaileen pantailan scroll infinitu bidez erakusten dira, 10 post-eko multzoetan kargatuz.

<h3>- Hasierako saioa eta Spotify konexioa</h3>

  Aplikazioa irekitzean, erabiltzaileak saioa hasi edo erregistratzeko orrira eramaten dira. Orrialde hauek Firebase datu-basearekin konektatzen dira erabiltzailea sortu edo bilatzeko.
  Saioa ondo hasi ondoren, aplikazioak erabiltzailearen Spotify kontuarekin konektatzen saiatuko da.

  OHARRA: Android gailuan Spotify instalatuta egotea derrigorrezkoa da konexioa behar bezala funtziona dadin.

<h3>- Menu nagusia</h3>

  Saioa hasi ondoren, erabiltzailea aplikazioaren orri nagusira iristen da, behealdeko menu batekin. Menuan honako atalak agertzen dira:

  - Home

  - Bilatu

  - Add

  - Compartidos

  - Profil

  Hala ere, gaur-gaurkoz garatuta dauden atalak hauek dira: Home, Add eta Profil.

<h3>- Home — Scroll infinitua</h3>

  Home orrian erabiltzaileak post zerrenda mugagabea ikus dezake. Post-ak Firebase-etik eskuratzen dira eta aplikazioak 10eko multzoetan kargatzen ditu; hamar post ikusi eta gero, hurrengoak automatikoki gehitzen dira.

<h3>- Profil — Erabiltzailearen informazioa</h3>

  Profil orrian erabiltzaileak bere informazioa ikus dezake:

  - Erregistratutako datuak

  - Baloratu dituen abestien zerrenda

  - Abesti bakoitzari jarritako nota

<h3>- Add — Gomendioak eta balorazio berriak</h3>

  Add atalean erabiltzaileari gomendatutako abestien zerrenda agertzen zaio. Gainera, bilatzaile bat ere agertzen da (baina oraindik ez dago funtzionala).

  Erabiltzaileak zerrendako abesti bat hautatzen duenean, aplikazioak aukera ematen dio:

  - Nota bat jartzeko

  - Iruzkin bat gehitzeko (aukerakoa)

  Datu hauek bidaltzen direnean, balorazioa Firebase datu-basean** gordetzen da eta erabiltzaile guztiek ikus dezakete Home orrian.
