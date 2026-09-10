Release notes for Yocto-5.0.19 (Scarthgap)
------------------------------------------

gawk-native is now built from source instead of used from the host when
building grub2 & glibc to avoid reproducibility issues on newer host
distros.

Security Fixes in Yocto-5.0.19
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Remove a reference to the rejected :cve_nist:`2021-36217`
-  binutils: Fix :cve_nist:`2025-69644`
-  busybox: Fix :cve_nist:`2026-29004`
-  dpkg: Fix :cve_nist:`2026-2219`
-  go: Fix :cve_nist:`2025-58183`, :cve_mitre:`2026-25679`, :cve_nist:`2026-27140`,
   :cve_nist:`2026-27142`, :cve_nist:`2026-27143`, :cve_nist:`2026-27144`, :cve_nist:`2026-27145`,
   :cve_nist:`2026-32280`, :cve_nist:`2026-32283`, :cve_nist:`2026-32288`, :cve_nist:`2026-32289`,
   :cve_nist:`2026-33811`, :cve_nist:`2026-39817`, :cve_nist:`2026-39819`, :cve_nist:`2026-39820`,
   :cve_nist:`2026-39825`, :cve_nist:`2026-39826`, :cve_nist:`2026-42499`, :cve_nist:`2026-42501`,
   :cve_nist:`2026-42504` and :cve_nist:`2026-42507`
-  go: Ignore :cve_nist:`2026-39836`
-  libarchive: Fix :cve_nist:`2026-4424`
-  libexif: Fix :cve_nist:`2026-32775`, :cve_nist:`2026-40385` and :cve_nist:`2026-40386`
-  libinput: Fix :cve_nist:`2026-50292`
-  libpng: Fix :cve_nist:`2026-33416`
-  libsolv: Fix :cve_nist:`2026-9150`
-  libsoup: Fix :cve_nist:`2025-11021` and :cve_nist:`2026-2369`
-  libssh2: Fix :cve_nist:`2026-7598`
-  libusb1: Fix :cve_nist:`2026-23679` and :cve_nist:`2026-47104`
-  libxml-parser-perl: Fix :cve_nist:`2006-10003`
-  linux-yocto/6.6: Fix :cve_nist:`2023-52920`, :cve_nist:`2024-14027`, :cve_nist:`2024-27022`,
   :cve_nist:`2024-56647`, :cve_nist:`2025-21739`, :cve_nist:`2025-22125`, :cve_nist:`2025-38531`,
   :cve_nist:`2025-38584`, :cve_nist:`2025-38710`, :cve_nist:`2025-39981`, :cve_nist:`2025-40219`,
   :cve_nist:`2025-68315`, :cve_nist:`2025-71184`, :cve_nist:`2025-71239`, :cve_nist:`2025-71265`,
   :cve_nist:`2025-71266`, :cve_nist:`2025-71267`, :cve_nist:`2025-71269`, :cve_nist:`2025-71274`,
   :cve_nist:`2025-71286`, :cve_nist:`2025-71287`, :cve_nist:`2025-71288`, :cve_nist:`2025-71291`,
   :cve_nist:`2025-71292`, :cve_nist:`2025-71295`, :cve_nist:`2025-71297`, :cve_nist:`2025-71304`,
   :cve_nist:`2025-71305`, :cve_nist:`2026-23004`, :cve_nist:`2026-23171`, :cve_nist:`2026-23231`,
   :cve_nist:`2026-23242`, :cve_nist:`2026-23243`, :cve_nist:`2026-23244`, :cve_nist:`2026-23245`,
   :cve_nist:`2026-23246`, :cve_nist:`2026-23253`, :cve_nist:`2026-23255`, :cve_nist:`2026-23268`,
   :cve_nist:`2026-23269`, :cve_nist:`2026-23270`, :cve_nist:`2026-23271`, :cve_nist:`2026-23272`,
   :cve_nist:`2026-23273`, :cve_nist:`2026-23274`, :cve_nist:`2026-23277`, :cve_nist:`2026-23279`,
   :cve_nist:`2026-23281`, :cve_nist:`2026-23284`, :cve_nist:`2026-23285`, :cve_nist:`2026-23286`,
   :cve_nist:`2026-23287`, :cve_nist:`2026-23289`, :cve_nist:`2026-23290`, :cve_nist:`2026-23291`,
   :cve_nist:`2026-23292`, :cve_nist:`2026-23293`, :cve_nist:`2026-23296`, :cve_nist:`2026-23298`,
   :cve_nist:`2026-23300`, :cve_nist:`2026-23302`, :cve_nist:`2026-23303`, :cve_nist:`2026-23304`,
   :cve_nist:`2026-23306`, :cve_nist:`2026-23307`, :cve_nist:`2026-23308`, :cve_nist:`2026-23310`,
   :cve_nist:`2026-23312`, :cve_nist:`2026-23313`, :cve_nist:`2026-23315`, :cve_nist:`2026-23317`,
   :cve_nist:`2026-23318`, :cve_nist:`2026-23319`, :cve_nist:`2026-23321`, :cve_nist:`2026-23324`,
   :cve_nist:`2026-23325`, :cve_nist:`2026-23330`, :cve_nist:`2026-23334`, :cve_nist:`2026-23335`,
   :cve_nist:`2026-23336`, :cve_nist:`2026-23339`, :cve_nist:`2026-23340`, :cve_nist:`2026-23343`,
   :cve_nist:`2026-23347`, :cve_nist:`2026-23351`, :cve_nist:`2026-23352`, :cve_nist:`2026-23356`,
   :cve_nist:`2026-23357`, :cve_nist:`2026-23359`, :cve_nist:`2026-23360`, :cve_nist:`2026-23362`,
   :cve_nist:`2026-23364`, :cve_nist:`2026-23365`, :cve_nist:`2026-23367`, :cve_nist:`2026-23368`,
   :cve_nist:`2026-23370`, :cve_nist:`2026-23372`, :cve_nist:`2026-23374`, :cve_nist:`2026-23378`,
   :cve_nist:`2026-23379`, :cve_nist:`2026-23381`, :cve_nist:`2026-23382`, :cve_nist:`2026-23386`,
   :cve_nist:`2026-23387`, :cve_nist:`2026-23388`, :cve_nist:`2026-23389`, :cve_nist:`2026-23391`,
   :cve_nist:`2026-23392`, :cve_nist:`2026-23395`, :cve_nist:`2026-23396`, :cve_nist:`2026-23397`,
   :cve_nist:`2026-23398`, :cve_nist:`2026-23399`, :cve_nist:`2026-23401`, :cve_nist:`2026-23403`,
   :cve_nist:`2026-23404`, :cve_nist:`2026-23405`, :cve_nist:`2026-23406`, :cve_nist:`2026-23407`,
   :cve_nist:`2026-23408`, :cve_nist:`2026-23409`, :cve_nist:`2026-23410`, :cve_nist:`2026-23411`,
   :cve_nist:`2026-23412`, :cve_nist:`2026-23413`, :cve_nist:`2026-23414`, :cve_nist:`2026-23419`,
   :cve_nist:`2026-23420`, :cve_nist:`2026-23422`, :cve_nist:`2026-23426`, :cve_nist:`2026-23427`,
   :cve_nist:`2026-23428`, :cve_nist:`2026-23434`, :cve_nist:`2026-23438`, :cve_nist:`2026-23439`,
   :cve_nist:`2026-23440`, :cve_nist:`2026-23441`, :cve_nist:`2026-23442`, :cve_nist:`2026-23443`,
   :cve_nist:`2026-23444`, :cve_nist:`2026-23446`, :cve_nist:`2026-23447`, :cve_nist:`2026-23448`,
   :cve_nist:`2026-23449`, :cve_nist:`2026-23450`, :cve_nist:`2026-23452`, :cve_nist:`2026-23454`,
   :cve_nist:`2026-23455`, :cve_nist:`2026-23456`, :cve_nist:`2026-23457`, :cve_nist:`2026-23458`,
   :cve_nist:`2026-23460`, :cve_nist:`2026-23461`, :cve_nist:`2026-23462`, :cve_nist:`2026-23463`,
   :cve_nist:`2026-23465`, :cve_nist:`2026-23468`, :cve_nist:`2026-23474`, :cve_nist:`2026-23475`,
   :cve_nist:`2026-31389`, :cve_nist:`2026-31391`, :cve_nist:`2026-31392`, :cve_nist:`2026-31393`,
   :cve_nist:`2026-31396`, :cve_nist:`2026-31399`, :cve_nist:`2026-31400`, :cve_nist:`2026-31402`,
   :cve_nist:`2026-31403`, :cve_nist:`2026-31405`, :cve_nist:`2026-31407`, :cve_nist:`2026-31408`,
   :cve_nist:`2026-31409`, :cve_nist:`2026-31411`, :cve_nist:`2026-31412`, :cve_nist:`2026-31414`,
   :cve_nist:`2026-31415`, :cve_nist:`2026-31416`, :cve_nist:`2026-31417`, :cve_nist:`2026-31418`,
   :cve_nist:`2026-31421`, :cve_nist:`2026-31422`, :cve_nist:`2026-31423`, :cve_nist:`2026-31424`,
   :cve_nist:`2026-31425`, :cve_nist:`2026-31426`, :cve_nist:`2026-31427`, :cve_nist:`2026-31428`,
   :cve_nist:`2026-31429`, :cve_nist:`2026-31430`, :cve_nist:`2026-31431`, :cve_nist:`2026-31433`,
   :cve_nist:`2026-31434`, :cve_nist:`2026-31439`, :cve_nist:`2026-31440`, :cve_nist:`2026-31441`,
   :cve_nist:`2026-31446`, :cve_nist:`2026-31447`, :cve_nist:`2026-31448`, :cve_nist:`2026-31449`,
   :cve_nist:`2026-31450`, :cve_nist:`2026-31451`, :cve_nist:`2026-31452`, :cve_nist:`2026-31453`,
   :cve_nist:`2026-31454`, :cve_nist:`2026-31455`, :cve_nist:`2026-31458`, :cve_nist:`2026-31464`,
   :cve_nist:`2026-31466`, :cve_nist:`2026-31467`, :cve_nist:`2026-31469`, :cve_nist:`2026-31473`,
   :cve_nist:`2026-31474`, :cve_nist:`2026-31476`, :cve_nist:`2026-31477`, :cve_nist:`2026-31478`,
   :cve_nist:`2026-31480`, :cve_nist:`2026-31482`, :cve_nist:`2026-31483`, :cve_nist:`2026-31485`,
   :cve_nist:`2026-31488`, :cve_nist:`2026-31489`, :cve_nist:`2026-31492`, :cve_nist:`2026-31494`,
   :cve_nist:`2026-31495`, :cve_nist:`2026-31496`, :cve_nist:`2026-31497`, :cve_nist:`2026-31498`,
   :cve_nist:`2026-31500`, :cve_nist:`2026-31503`, :cve_nist:`2026-31504`, :cve_nist:`2026-31507`,
   :cve_nist:`2026-31508`, :cve_nist:`2026-31509`, :cve_nist:`2026-31510`, :cve_nist:`2026-31512`,
   :cve_nist:`2026-31515`, :cve_nist:`2026-31518`, :cve_nist:`2026-31519`, :cve_nist:`2026-31520`,
   :cve_nist:`2026-31521`, :cve_nist:`2026-31522`, :cve_nist:`2026-31523`, :cve_nist:`2026-31524`,
   :cve_nist:`2026-31525`, :cve_nist:`2026-31528`, :cve_nist:`2026-31532`, :cve_nist:`2026-31533`,
   :cve_nist:`2026-31540`, :cve_nist:`2026-31542`, :cve_nist:`2026-31545`, :cve_nist:`2026-31546`,
   :cve_nist:`2026-31548`, :cve_nist:`2026-31549`, :cve_nist:`2026-31550`, :cve_nist:`2026-31551`,
   :cve_nist:`2026-31552`, :cve_nist:`2026-31555`, :cve_nist:`2026-31563`, :cve_nist:`2026-31565`,
   :cve_nist:`2026-31566`, :cve_nist:`2026-31570`, :cve_nist:`2026-31576`, :cve_nist:`2026-31577`,
   :cve_nist:`2026-31578`, :cve_nist:`2026-31580`, :cve_nist:`2026-31581`, :cve_nist:`2026-31583`,
   :cve_nist:`2026-31584`, :cve_nist:`2026-31585`, :cve_nist:`2026-31586`, :cve_nist:`2026-31587`,
   :cve_nist:`2026-31588`, :cve_nist:`2026-31590`, :cve_nist:`2026-31594`, :cve_nist:`2026-31595`,
   :cve_nist:`2026-31596`, :cve_nist:`2026-31597`, :cve_nist:`2026-31598`, :cve_nist:`2026-31599`,
   :cve_nist:`2026-31602`, :cve_nist:`2026-31603`, :cve_nist:`2026-31604`, :cve_nist:`2026-31605`,
   :cve_nist:`2026-31607`, :cve_nist:`2026-31610`, :cve_nist:`2026-31611`, :cve_nist:`2026-31612`,
   :cve_nist:`2026-31613`, :cve_nist:`2026-31614`, :cve_nist:`2026-31615`, :cve_nist:`2026-31616`,
   :cve_nist:`2026-31617`, :cve_nist:`2026-31618`, :cve_nist:`2026-31619`, :cve_nist:`2026-31622`,
   :cve_nist:`2026-31623`, :cve_nist:`2026-31624`, :cve_nist:`2026-31625`, :cve_nist:`2026-31626`,
   :cve_nist:`2026-31627`, :cve_nist:`2026-31628`, :cve_nist:`2026-31629`, :cve_nist:`2026-31634`,
   :cve_nist:`2026-31637`, :cve_nist:`2026-31638`, :cve_nist:`2026-31639`, :cve_nist:`2026-31642`,
   :cve_nist:`2026-31646`, :cve_nist:`2026-31648`, :cve_nist:`2026-31649`, :cve_nist:`2026-31651`,
   :cve_nist:`2026-31655`, :cve_nist:`2026-31656`, :cve_nist:`2026-31657`, :cve_nist:`2026-31658`,
   :cve_nist:`2026-31659`, :cve_nist:`2026-31660`, :cve_nist:`2026-31661`, :cve_nist:`2026-31662`,
   :cve_nist:`2026-31664`, :cve_nist:`2026-31665`, :cve_nist:`2026-31667`, :cve_nist:`2026-31668`,
   :cve_nist:`2026-31669`, :cve_nist:`2026-31670`, :cve_nist:`2026-31671`, :cve_nist:`2026-31672`,
   :cve_nist:`2026-31673`, :cve_nist:`2026-31674`, :cve_nist:`2026-31675`, :cve_nist:`2026-31676`,
   :cve_nist:`2026-31678`, :cve_nist:`2026-31679`, :cve_nist:`2026-31680`, :cve_nist:`2026-31681`,
   :cve_nist:`2026-31682`, :cve_nist:`2026-31683`, :cve_nist:`2026-31684`, :cve_nist:`2026-31685`,
   :cve_nist:`2026-31686`, :cve_nist:`2026-31689`, :cve_nist:`2026-31693`, :cve_nist:`2026-31694`,
   :cve_nist:`2026-31695`, :cve_nist:`2026-31696`, :cve_nist:`2026-31697`, :cve_nist:`2026-31698`,
   :cve_nist:`2026-31699`, :cve_nist:`2026-31700`, :cve_nist:`2026-31701`, :cve_nist:`2026-31702`,
   :cve_nist:`2026-31704`, :cve_nist:`2026-31705`, :cve_nist:`2026-31707`, :cve_nist:`2026-31708`,
   :cve_nist:`2026-31709`, :cve_nist:`2026-31711`, :cve_nist:`2026-31712`, :cve_nist:`2026-31714`,
   :cve_nist:`2026-31715`, :cve_nist:`2026-31716`, :cve_nist:`2026-31718`, :cve_nist:`2026-31720`,
   :cve_nist:`2026-31721`, :cve_nist:`2026-31726`, :cve_nist:`2026-31728`, :cve_nist:`2026-31730`,
   :cve_nist:`2026-31737`, :cve_nist:`2026-31738`, :cve_nist:`2026-31740`, :cve_nist:`2026-31741`,
   :cve_nist:`2026-31747`, :cve_nist:`2026-31748`, :cve_nist:`2026-31749`, :cve_nist:`2026-31751`,
   :cve_nist:`2026-31752`, :cve_nist:`2026-31754`, :cve_nist:`2026-31755`, :cve_nist:`2026-31756`,
   :cve_nist:`2026-31758`, :cve_nist:`2026-31759`, :cve_nist:`2026-31761`, :cve_nist:`2026-31762`,
   :cve_nist:`2026-31763`, :cve_nist:`2026-31768`, :cve_nist:`2026-31770`, :cve_nist:`2026-31773`,
   :cve_nist:`2026-31778`, :cve_nist:`2026-31779`, :cve_nist:`2026-31780`, :cve_nist:`2026-31781`,
   :cve_nist:`2026-31786`, :cve_nist:`2026-31787`, :cve_nist:`2026-31788`, :cve_nist:`2026-43007`,
   :cve_nist:`2026-43011`, :cve_nist:`2026-43013`, :cve_nist:`2026-43014`, :cve_nist:`2026-43015`,
   :cve_nist:`2026-43016`, :cve_nist:`2026-43017`, :cve_nist:`2026-43018`, :cve_nist:`2026-43020`,
   :cve_nist:`2026-43023`, :cve_nist:`2026-43024`, :cve_nist:`2026-43025`, :cve_nist:`2026-43026`,
   :cve_nist:`2026-43027`, :cve_nist:`2026-43028`, :cve_nist:`2026-43030`, :cve_nist:`2026-43032`,
   :cve_nist:`2026-43033`, :cve_nist:`2026-43035`, :cve_nist:`2026-43037`, :cve_nist:`2026-43038`,
   :cve_nist:`2026-43040`, :cve_nist:`2026-43041`, :cve_nist:`2026-43043`, :cve_nist:`2026-43044`,
   :cve_nist:`2026-43046`, :cve_nist:`2026-43047`, :cve_nist:`2026-43050`, :cve_nist:`2026-43051`,
   :cve_nist:`2026-43054`, :cve_nist:`2026-43056`, :cve_nist:`2026-43057`, :cve_nist:`2026-43058`,
   :cve_nist:`2026-43060`, :cve_nist:`2026-43061`, :cve_nist:`2026-43062`, :cve_nist:`2026-43064`,
   :cve_nist:`2026-43065`, :cve_nist:`2026-43066`, :cve_nist:`2026-43067`, :cve_nist:`2026-43068`,
   :cve_nist:`2026-43069`, :cve_nist:`2026-43071`, :cve_nist:`2026-43072`, :cve_nist:`2026-43074`,
   :cve_nist:`2026-43075`, :cve_nist:`2026-43076`, :cve_nist:`2026-43077`, :cve_nist:`2026-43078`,
   :cve_nist:`2026-43079`, :cve_nist:`2026-43080`, :cve_nist:`2026-43081`, :cve_nist:`2026-43082`,
   :cve_nist:`2026-43085`, :cve_nist:`2026-43086`, :cve_nist:`2026-43089`, :cve_nist:`2026-43091`,
   :cve_nist:`2026-43092`, :cve_nist:`2026-43093`, :cve_nist:`2026-43094`, :cve_nist:`2026-43098`,
   :cve_nist:`2026-43099`, :cve_nist:`2026-43103`, :cve_nist:`2026-43104`, :cve_nist:`2026-43105`,
   :cve_nist:`2026-43109`, :cve_nist:`2026-43110`, :cve_nist:`2026-43111`, :cve_nist:`2026-43112`,
   :cve_nist:`2026-43113`, :cve_nist:`2026-43114`, :cve_nist:`2026-43117`, :cve_nist:`2026-43120`,
   :cve_nist:`2026-43123`, :cve_nist:`2026-43124`, :cve_nist:`2026-43128`, :cve_nist:`2026-43130`,
   :cve_nist:`2026-43132`, :cve_nist:`2026-43133`, :cve_nist:`2026-43134`, :cve_nist:`2026-43135`,
   :cve_nist:`2026-43136`, :cve_nist:`2026-43137`, :cve_nist:`2026-43139`, :cve_nist:`2026-43140`,
   :cve_nist:`2026-43141`, :cve_nist:`2026-43143`, :cve_nist:`2026-43145`, :cve_nist:`2026-43147`,
   :cve_nist:`2026-43148`, :cve_nist:`2026-43149`, :cve_nist:`2026-43150`, :cve_nist:`2026-43152`,
   :cve_nist:`2026-43156`, :cve_nist:`2026-43157`, :cve_nist:`2026-43158`, :cve_nist:`2026-43159`,
   :cve_nist:`2026-43162`, :cve_nist:`2026-43163`, :cve_nist:`2026-43167`, :cve_nist:`2026-43168`,
   :cve_nist:`2026-43170`, :cve_nist:`2026-43171`, :cve_nist:`2026-43173`, :cve_nist:`2026-43180`,
   :cve_nist:`2026-43182`, :cve_nist:`2026-43183`, :cve_nist:`2026-43184`, :cve_nist:`2026-43186`,
   :cve_nist:`2026-43187`, :cve_nist:`2026-43189`, :cve_nist:`2026-43190`, :cve_nist:`2026-43194`,
   :cve_nist:`2026-43196`, :cve_nist:`2026-43200`, :cve_nist:`2026-43202`, :cve_nist:`2026-43203`,
   :cve_nist:`2026-43205`, :cve_nist:`2026-43206`, :cve_nist:`2026-43207`, :cve_nist:`2026-43209`,
   :cve_nist:`2026-43211`, :cve_nist:`2026-43212`, :cve_nist:`2026-43214`, :cve_nist:`2026-43215`,
   :cve_nist:`2026-43218`, :cve_nist:`2026-43221`, :cve_nist:`2026-43222`, :cve_nist:`2026-43223`,
   :cve_nist:`2026-43225`, :cve_nist:`2026-43226`, :cve_nist:`2026-43227`, :cve_nist:`2026-43230`,
   :cve_nist:`2026-43231`, :cve_nist:`2026-43232`, :cve_nist:`2026-43233`, :cve_nist:`2026-43236`,
   :cve_nist:`2026-43238`, :cve_nist:`2026-43239`, :cve_nist:`2026-43240`, :cve_nist:`2026-43241`,
   :cve_nist:`2026-43242`, :cve_nist:`2026-43245`, :cve_nist:`2026-43246`, :cve_nist:`2026-43251`,
   :cve_nist:`2026-43252`, :cve_nist:`2026-43253`, :cve_nist:`2026-43255`, :cve_nist:`2026-43256`,
   :cve_nist:`2026-43257`, :cve_nist:`2026-43261`, :cve_nist:`2026-43262`, :cve_nist:`2026-43264`,
   :cve_nist:`2026-43265`, :cve_nist:`2026-43266`, :cve_nist:`2026-43268`, :cve_nist:`2026-43269`,
   :cve_nist:`2026-43270`, :cve_nist:`2026-43271`, :cve_nist:`2026-43273`, :cve_nist:`2026-43275`,
   :cve_nist:`2026-43277`, :cve_nist:`2026-43278`, :cve_nist:`2026-43279`, :cve_nist:`2026-43281`,
   :cve_nist:`2026-43283`, :cve_nist:`2026-43284`, :cve_nist:`2026-43287`, :cve_nist:`2026-43288`,
   :cve_nist:`2026-43289`, :cve_nist:`2026-43291`, :cve_nist:`2026-43295`, :cve_nist:`2026-43296`,
   :cve_nist:`2026-43302`, :cve_nist:`2026-43304`, :cve_nist:`2026-43312`, :cve_nist:`2026-43313`,
   :cve_nist:`2026-43314`, :cve_nist:`2026-43315`, :cve_nist:`2026-43316`, :cve_nist:`2026-43324`,
   :cve_nist:`2026-43327`, :cve_nist:`2026-43328`, :cve_nist:`2026-43329`, :cve_nist:`2026-43330`,
   :cve_nist:`2026-43332`, :cve_nist:`2026-43333`, :cve_nist:`2026-43334`, :cve_nist:`2026-43336`,
   :cve_nist:`2026-43339`, :cve_nist:`2026-43340`, :cve_nist:`2026-43341`, :cve_nist:`2026-43342`,
   :cve_nist:`2026-43343`, :cve_nist:`2026-43345`, :cve_nist:`2026-43350`, :cve_nist:`2026-43355`,
   :cve_nist:`2026-43357`, :cve_nist:`2026-43359`, :cve_nist:`2026-43360`, :cve_nist:`2026-43361`,
   :cve_nist:`2026-43362`, :cve_nist:`2026-43363`, :cve_nist:`2026-43365`, :cve_nist:`2026-43366`,
   :cve_nist:`2026-43368`, :cve_nist:`2026-43370`, :cve_nist:`2026-43371`, :cve_nist:`2026-43372`,
   :cve_nist:`2026-43373`, :cve_nist:`2026-43376`, :cve_nist:`2026-43377`, :cve_nist:`2026-43378`,
   :cve_nist:`2026-43379`, :cve_nist:`2026-43380`, :cve_nist:`2026-43381`, :cve_nist:`2026-43382`,
   :cve_nist:`2026-43383`, :cve_nist:`2026-43386`, :cve_nist:`2026-43387`, :cve_nist:`2026-43397`,
   :cve_nist:`2026-43405`, :cve_nist:`2026-43406`, :cve_nist:`2026-43407`, :cve_nist:`2026-43409`,
   :cve_nist:`2026-43411`, :cve_nist:`2026-43412`, :cve_nist:`2026-43413`, :cve_nist:`2026-43415`,
   :cve_nist:`2026-43419`, :cve_nist:`2026-43420`, :cve_nist:`2026-43424`, :cve_nist:`2026-43425`,
   :cve_nist:`2026-43426`, :cve_nist:`2026-43427`, :cve_nist:`2026-43428`, :cve_nist:`2026-43429`,
   :cve_nist:`2026-43430`, :cve_nist:`2026-43432`, :cve_nist:`2026-43436`, :cve_nist:`2026-43437`,
   :cve_nist:`2026-43439`, :cve_nist:`2026-43441`, :cve_nist:`2026-43445`, :cve_nist:`2026-43448`,
   :cve_nist:`2026-43449`, :cve_nist:`2026-43450`, :cve_nist:`2026-43451`, :cve_nist:`2026-43452`,
   :cve_nist:`2026-43453`, :cve_nist:`2026-43455`, :cve_nist:`2026-43457`, :cve_nist:`2026-43458`,
   :cve_nist:`2026-43459`, :cve_nist:`2026-43466`, :cve_nist:`2026-43468`, :cve_nist:`2026-43469`,
   :cve_nist:`2026-43471`, :cve_nist:`2026-43472`, :cve_nist:`2026-43473`, :cve_nist:`2026-43475`,
   :cve_nist:`2026-43476`, :cve_nist:`2026-43480`, :cve_nist:`2026-43483`, :cve_nist:`2026-43484`,
   :cve_nist:`2026-43488`, :cve_nist:`2026-43490`, :cve_nist:`2026-43491`, :cve_nist:`2026-43492`,
   :cve_nist:`2026-43493`, :cve_nist:`2026-43494`, :cve_nist:`2026-43495`, :cve_nist:`2026-43496`,
   :cve_nist:`2026-43497`, :cve_nist:`2026-43499`, :cve_nist:`2026-43500`, :cve_nist:`2026-43501`,
   :cve_nist:`2026-43502`, :cve_nist:`2026-43503`, :cve_nist:`2026-45834`, :cve_nist:`2026-45835`,
   :cve_nist:`2026-45836`, :cve_nist:`2026-45838`, :cve_nist:`2026-45839`, :cve_nist:`2026-45840`,
   :cve_nist:`2026-45841`, :cve_nist:`2026-45842`, :cve_nist:`2026-45843`, :cve_nist:`2026-45844`,
   :cve_nist:`2026-45845`, :cve_nist:`2026-45846`, :cve_nist:`2026-45847`, :cve_nist:`2026-45848`,
   :cve_nist:`2026-45849`, :cve_nist:`2026-45851`, :cve_nist:`2026-45852`, :cve_nist:`2026-45856`,
   :cve_nist:`2026-45857`, :cve_nist:`2026-45858`, :cve_nist:`2026-45860`, :cve_nist:`2026-45862`,
   :cve_nist:`2026-45864`, :cve_nist:`2026-45865`, :cve_nist:`2026-45866`, :cve_nist:`2026-45867`,
   :cve_nist:`2026-45868`, :cve_nist:`2026-45869`, :cve_nist:`2026-45870`, :cve_nist:`2026-45871`,
   :cve_nist:`2026-45872`, :cve_nist:`2026-45873`, :cve_nist:`2026-45875`, :cve_nist:`2026-45878`,
   :cve_nist:`2026-45879`, :cve_nist:`2026-45880`, :cve_nist:`2026-45881`, :cve_nist:`2026-45883`,
   :cve_nist:`2026-45885`, :cve_nist:`2026-45886`, :cve_nist:`2026-45890`, :cve_nist:`2026-45891`,
   :cve_nist:`2026-45895`, :cve_nist:`2026-45899`, :cve_nist:`2026-45902`, :cve_nist:`2026-45904`,
   :cve_nist:`2026-45905`, :cve_nist:`2026-45910`, :cve_nist:`2026-45911`, :cve_nist:`2026-45912`,
   :cve_nist:`2026-45913`, :cve_nist:`2026-45914`, :cve_nist:`2026-45915`, :cve_nist:`2026-45916`,
   :cve_nist:`2026-45919`, :cve_nist:`2026-45920`, :cve_nist:`2026-45921`, :cve_nist:`2026-45923`,
   :cve_nist:`2026-45924`, :cve_nist:`2026-45935`, :cve_nist:`2026-45936`, :cve_nist:`2026-45941`,
   :cve_nist:`2026-45942`, :cve_nist:`2026-45946`, :cve_nist:`2026-45947`, :cve_nist:`2026-45948`,
   :cve_nist:`2026-45954`, :cve_nist:`2026-45956`, :cve_nist:`2026-45957`, :cve_nist:`2026-45958`,
   :cve_nist:`2026-45960`, :cve_nist:`2026-45962`, :cve_nist:`2026-45964`, :cve_nist:`2026-45965`,
   :cve_nist:`2026-45968`, :cve_nist:`2026-45969`, :cve_nist:`2026-45970`, :cve_nist:`2026-45972`,
   :cve_nist:`2026-45974`, :cve_nist:`2026-45976`, :cve_nist:`2026-45978`, :cve_nist:`2026-45981`,
   :cve_nist:`2026-45982`, :cve_nist:`2026-45983`, :cve_nist:`2026-45984`, :cve_nist:`2026-45985`,
   :cve_nist:`2026-45986`, :cve_nist:`2026-45987`, :cve_nist:`2026-45988`, :cve_nist:`2026-45989`,
   :cve_nist:`2026-45991`, :cve_nist:`2026-45993`, :cve_nist:`2026-45994`, :cve_nist:`2026-45996`,
   :cve_nist:`2026-45997`, :cve_nist:`2026-45998`, :cve_nist:`2026-45999`, :cve_nist:`2026-46000`,
   :cve_nist:`2026-46002`, :cve_nist:`2026-46003`, :cve_nist:`2026-46004`, :cve_nist:`2026-46005`,
   :cve_nist:`2026-46006`, :cve_nist:`2026-46009`, :cve_nist:`2026-46011`, :cve_nist:`2026-46012`,
   :cve_nist:`2026-46015`, :cve_nist:`2026-46016`, :cve_nist:`2026-46018`, :cve_nist:`2026-46019`,
   :cve_nist:`2026-46021`, :cve_nist:`2026-46022`, :cve_nist:`2026-46023`, :cve_nist:`2026-46024`,
   :cve_nist:`2026-46026`, :cve_nist:`2026-46027`, :cve_nist:`2026-46028`, :cve_nist:`2026-46031`,
   :cve_nist:`2026-46033`, :cve_nist:`2026-46037`, :cve_nist:`2026-46038`, :cve_nist:`2026-46040`,
   :cve_nist:`2026-46043`, :cve_nist:`2026-46046`, :cve_nist:`2026-46047`, :cve_nist:`2026-46049`,
   :cve_nist:`2026-46050`, :cve_nist:`2026-46051`, :cve_nist:`2026-46052`, :cve_nist:`2026-46053`,
   :cve_nist:`2026-46056`, :cve_nist:`2026-46058`, :cve_nist:`2026-46062`, :cve_nist:`2026-46063`,
   :cve_nist:`2026-46064`, :cve_nist:`2026-46065`, :cve_nist:`2026-46068`, :cve_nist:`2026-46069`,
   :cve_nist:`2026-46070`, :cve_nist:`2026-46072`, :cve_nist:`2026-46075`, :cve_nist:`2026-46077`,
   :cve_nist:`2026-46078`, :cve_nist:`2026-46079`, :cve_nist:`2026-46080`, :cve_nist:`2026-46082`,
   :cve_nist:`2026-46083`, :cve_nist:`2026-46084`, :cve_nist:`2026-46086`, :cve_nist:`2026-46088`,
   :cve_nist:`2026-46089`, :cve_nist:`2026-46091`, :cve_nist:`2026-46094`, :cve_nist:`2026-46098`,
   :cve_nist:`2026-46099`, :cve_nist:`2026-46101`, :cve_nist:`2026-46102`, :cve_nist:`2026-46103`,
   :cve_nist:`2026-46106`, :cve_nist:`2026-46107`, :cve_nist:`2026-46108`, :cve_nist:`2026-46110`,
   :cve_nist:`2026-46111`, :cve_nist:`2026-46112`, :cve_nist:`2026-46113`, :cve_nist:`2026-46114`,
   :cve_nist:`2026-46115`, :cve_nist:`2026-46116`, :cve_nist:`2026-46119`, :cve_nist:`2026-46120`,
   :cve_nist:`2026-46121`, :cve_nist:`2026-46122`, :cve_nist:`2026-46123`, :cve_nist:`2026-46124`,
   :cve_nist:`2026-46125`, :cve_nist:`2026-46127`, :cve_nist:`2026-46128`, :cve_nist:`2026-46129`,
   :cve_nist:`2026-46131`, :cve_nist:`2026-46132`, :cve_nist:`2026-46133`, :cve_nist:`2026-46136`,
   :cve_nist:`2026-46137`, :cve_nist:`2026-46138`, :cve_nist:`2026-46142`, :cve_nist:`2026-46143`,
   :cve_nist:`2026-46144`, :cve_nist:`2026-46145`, :cve_nist:`2026-46146`, :cve_nist:`2026-46149`,
   :cve_nist:`2026-46150`, :cve_nist:`2026-46151`, :cve_nist:`2026-46152`, :cve_nist:`2026-46155`,
   :cve_nist:`2026-46158`, :cve_nist:`2026-46159`, :cve_nist:`2026-46160`, :cve_nist:`2026-46161`,
   :cve_nist:`2026-46163`, :cve_nist:`2026-46164`, :cve_nist:`2026-46167`, :cve_nist:`2026-46168`,
   :cve_nist:`2026-46169`, :cve_nist:`2026-46170`, :cve_nist:`2026-46172`, :cve_nist:`2026-46173`,
   :cve_nist:`2026-46174`, :cve_nist:`2026-46176`, :cve_nist:`2026-46177`, :cve_nist:`2026-46178`,
   :cve_nist:`2026-46179`, :cve_nist:`2026-46180`, :cve_nist:`2026-46184`, :cve_nist:`2026-46185`,
   :cve_nist:`2026-46186`, :cve_nist:`2026-46187`, :cve_nist:`2026-46189`, :cve_nist:`2026-46190`,
   :cve_nist:`2026-46191`, :cve_nist:`2026-46193`, :cve_nist:`2026-46194`, :cve_nist:`2026-46195`,
   :cve_nist:`2026-46196`, :cve_nist:`2026-46197`, :cve_nist:`2026-46198`, :cve_nist:`2026-46199`,
   :cve_nist:`2026-46204`, :cve_nist:`2026-46205`, :cve_nist:`2026-46206`, :cve_nist:`2026-46208`,
   :cve_nist:`2026-46209`, :cve_nist:`2026-46212`, :cve_nist:`2026-46214`, :cve_nist:`2026-46218`,
   :cve_nist:`2026-46219`, :cve_nist:`2026-46220`, :cve_nist:`2026-46225`, :cve_nist:`2026-46226`,
   :cve_nist:`2026-46227`, :cve_nist:`2026-46229`, :cve_nist:`2026-46230`, :cve_nist:`2026-46231`,
   :cve_nist:`2026-46232`, :cve_nist:`2026-46233`, :cve_nist:`2026-46234`, :cve_nist:`2026-46235`,
   :cve_nist:`2026-46236`, :cve_nist:`2026-46238`, :cve_nist:`2026-46243`, :cve_nist:`2026-46244`,
   :cve_nist:`2026-46247`, :cve_nist:`2026-46249`, :cve_nist:`2026-46250`, :cve_nist:`2026-46251`,
   :cve_nist:`2026-46253`, :cve_nist:`2026-46255`, :cve_nist:`2026-46259`, :cve_nist:`2026-46260`,
   :cve_nist:`2026-46261`, :cve_nist:`2026-46262`, :cve_nist:`2026-46265`, :cve_nist:`2026-46266`,
   :cve_nist:`2026-46267`, :cve_nist:`2026-46270`, :cve_nist:`2026-46273`, :cve_nist:`2026-46274`,
   :cve_nist:`2026-46275`, :cve_nist:`2026-46276`, :cve_nist:`2026-46280`, :cve_nist:`2026-46285`,
   :cve_nist:`2026-46286`, :cve_nist:`2026-46287`, :cve_nist:`2026-46289`, :cve_nist:`2026-46291`,
   :cve_nist:`2026-46292`, :cve_nist:`2026-46293`, :cve_nist:`2026-46294`, :cve_nist:`2026-46296`,
   :cve_nist:`2026-46299`, :cve_nist:`2026-46300`, :cve_nist:`2026-46301`, :cve_nist:`2026-46303`,
   :cve_nist:`2026-46304`, :cve_nist:`2026-46306`, :cve_nist:`2026-46307`, :cve_nist:`2026-46312`,
   :cve_nist:`2026-46319`, :cve_nist:`2026-46323`, :cve_nist:`2026-46328`, :cve_nist:`2026-46333`,
   :cve_nist:`2026-52911`, :cve_nist:`2026-52912`, :cve_nist:`2026-52914`, :cve_nist:`2026-52915`,
   :cve_nist:`2026-52916`, :cve_nist:`2026-52918`, :cve_nist:`2026-52919`, :cve_nist:`2026-52920`,
   :cve_nist:`2026-52921`, :cve_nist:`2026-52922`, :cve_nist:`2026-52923`, :cve_nist:`2026-52925`,
   :cve_nist:`2026-52926`, :cve_nist:`2026-52931`, :cve_nist:`2026-52933`, :cve_nist:`2026-52936`,
   :cve_nist:`2026-52941`, :cve_nist:`2026-53128`, :cve_nist:`2026-53130`, :cve_nist:`2026-53279`,
   :cve_nist:`2026-53287`, :cve_nist:`2026-53289`, :cve_nist:`2026-53291`, :cve_nist:`2026-53293`,
   :cve_nist:`2026-53294`, :cve_nist:`2026-53295`, :cve_nist:`2026-53296`, :cve_nist:`2026-53303`,
   :cve_nist:`2026-53304`, :cve_nist:`2026-53306`, :cve_nist:`2026-53309`, :cve_nist:`2026-53314`
   and :cve_nist:`2026-53320`
-  linux-yocto/6.6: Ignore :cve_nist:`2023-53012`, :cve_nist:`2023-53187`, :cve_nist:`2024-26949`,
   :cve_nist:`2024-26997`, :cve_nist:`2024-35983`, :cve_nist:`2024-36002`, :cve_nist:`2024-36288`,
   :cve_nist:`2024-38542`, :cve_nist:`2024-38595`, :cve_nist:`2024-40920`, :cve_nist:`2024-40921`,
   :cve_nist:`2024-40986`, :cve_nist:`2024-42111`, :cve_nist:`2024-42112`, :cve_nist:`2024-42150`,
   :cve_nist:`2024-43820`, :cve_nist:`2024-43838`, :cve_nist:`2024-47711`, :cve_nist:`2024-49854`,
   :cve_nist:`2024-50042`, :cve_nist:`2024-50097`, :cve_nist:`2024-50203`, :cve_nist:`2024-50206`,
   :cve_nist:`2024-50238`, :cve_nist:`2024-50241`, :cve_nist:`2024-50249`, :cve_nist:`2024-50281`,
   :cve_nist:`2024-53073`, :cve_nist:`2024-53097`, :cve_nist:`2024-53115`, :cve_nist:`2024-53133`,
   :cve_nist:`2024-53169`, :cve_nist:`2024-53201`, :cve_nist:`2024-54191`, :cve_nist:`2024-54460`,
   :cve_nist:`2024-56542`, :cve_nist:`2024-56550`, :cve_nist:`2024-56561`, :cve_nist:`2024-56782`,
   :cve_nist:`2024-57805`, :cve_nist:`2024-57852`, :cve_nist:`2024-57879`, :cve_nist:`2024-57935`,
   :cve_nist:`2024-57952`, :cve_nist:`2024-57990`, :cve_nist:`2024-58008`, :cve_nist:`2024-58081`,
   :cve_nist:`2025-21752`, :cve_nist:`2025-21809`, :cve_nist:`2025-21813`, :cve_nist:`2025-21902`,
   :cve_nist:`2025-22026`, :cve_nist:`2025-22030`, :cve_nist:`2025-22032`, :cve_nist:`2025-22069`,
   :cve_nist:`2025-22112`, :cve_nist:`2025-37760`, :cve_nist:`2025-37814`, :cve_nist:`2025-37827`,
   :cve_nist:`2025-37845`, :cve_nist:`2025-37868`, :cve_nist:`2025-37871`, :cve_nist:`2025-37878`,
   :cve_nist:`2025-37895`, :cve_nist:`2025-37929`, :cve_nist:`2025-37962`, :cve_nist:`2025-37974`,
   :cve_nist:`2025-38055`, :cve_nist:`2025-38098`, :cve_nist:`2025-38099`, :cve_nist:`2025-38162`,
   :cve_nist:`2025-38169`, :cve_nist:`2025-38201`, :cve_nist:`2025-38224`, :cve_nist:`2025-38234`,
   :cve_nist:`2025-38289`, :cve_nist:`2025-38325`, :cve_nist:`2025-38372`, :cve_nist:`2025-38373`,
   :cve_nist:`2025-38405`, :cve_nist:`2025-38421`, :cve_nist:`2025-38484`, :cve_nist:`2025-38551`,
   :cve_nist:`2025-38567`, :cve_nist:`2025-38675`, :cve_nist:`2025-38733`, :cve_nist:`2025-38736`,
   :cve_nist:`2025-39688`, :cve_nist:`2025-39725`, :cve_nist:`2025-39775`, :cve_nist:`2025-39807`,
   :cve_nist:`2025-39872`, :cve_nist:`2025-39930`, :cve_nist:`2025-39948`, :cve_nist:`2025-39965`,
   :cve_nist:`2025-39999`, :cve_nist:`2025-40005`, :cve_nist:`2025-40007`, :cve_nist:`2025-40082`,
   :cve_nist:`2025-40101`, :cve_nist:`2025-40131`, :cve_nist:`2025-40199`, :cve_nist:`2025-40213`,
   :cve_nist:`2025-40290`, :cve_nist:`2025-40327`, :cve_nist:`2025-40332`, :cve_nist:`2025-40344`,
   :cve_nist:`2025-68195`, :cve_nist:`2025-68207`, :cve_nist:`2025-68213`, :cve_nist:`2025-68215`,
   :cve_nist:`2025-68298`, :cve_nist:`2025-68351`, :cve_nist:`2025-68357`, :cve_nist:`2025-68358`,
   :cve_nist:`2025-68823`, :cve_nist:`2025-71070`, :cve_nist:`2025-71076`, :cve_nist:`2025-71135`,
   :cve_nist:`2025-71145`, :cve_nist:`2025-71146`, :cve_nist:`2025-71155`, :cve_nist:`2025-71204`,
   :cve_nist:`2025-71220`, :cve_nist:`2025-71222`, :cve_nist:`2025-71223`, :cve_nist:`2025-71224`,
   :cve_nist:`2025-71229`, :cve_nist:`2025-71232`, :cve_nist:`2025-71233`, :cve_nist:`2025-71235`,
   :cve_nist:`2025-71236`, :cve_nist:`2025-71237`, :cve_nist:`2025-71238`, :cve_nist:`2025-71268`,
   :cve_nist:`2025-71270`, :cve_nist:`2025-71271`, :cve_nist:`2025-71290`, :cve_nist:`2025-71293`,
   :cve_nist:`2025-71294`, :cve_nist:`2025-71296`, :cve_nist:`2025-71298`, :cve_nist:`2025-71299`,
   :cve_nist:`2025-71300`, :cve_nist:`2025-71301`, :cve_nist:`2025-71302`, :cve_nist:`2026-23014`,
   :cve_nist:`2026-23018`, :cve_nist:`2026-23079`, :cve_nist:`2026-23100`, :cve_nist:`2026-23111`,
   :cve_nist:`2026-23112`, :cve_nist:`2026-23143`, :cve_nist:`2026-23148`, :cve_nist:`2026-23151`,
   :cve_nist:`2026-23166`, :cve_nist:`2026-23169`, :cve_nist:`2026-23176`, :cve_nist:`2026-23178`,
   :cve_nist:`2026-23180`, :cve_nist:`2026-23182`, :cve_nist:`2026-23187`, :cve_nist:`2026-23189`,
   :cve_nist:`2026-23190`, :cve_nist:`2026-23193`, :cve_nist:`2026-23198`, :cve_nist:`2026-23200`,
   :cve_nist:`2026-23201`, :cve_nist:`2026-23202`, :cve_nist:`2026-23204`, :cve_nist:`2026-23205`,
   :cve_nist:`2026-23206`, :cve_nist:`2026-23209`, :cve_nist:`2026-23216`, :cve_nist:`2026-23217`,
   :cve_nist:`2026-23220`, :cve_nist:`2026-23221`, :cve_nist:`2026-23222`, :cve_nist:`2026-23228`,
   :cve_nist:`2026-23229`, :cve_nist:`2026-23230`, :cve_nist:`2026-23233`, :cve_nist:`2026-23234`,
   :cve_nist:`2026-23235`, :cve_nist:`2026-23236`, :cve_nist:`2026-23237`, :cve_nist:`2026-23238`,
   :cve_nist:`2026-23254`, :cve_nist:`2026-23256`, :cve_nist:`2026-23257`, :cve_nist:`2026-23258`,
   :cve_nist:`2026-23260`, :cve_nist:`2026-23261`, :cve_nist:`2026-23262`, :cve_nist:`2026-23264`,
   :cve_nist:`2026-23266`, :cve_nist:`2026-23267`, :cve_nist:`2026-23288`, :cve_nist:`2026-23341`,
   :cve_nist:`2026-23355`, :cve_nist:`2026-23376`, :cve_nist:`2026-23418`, :cve_nist:`2026-23421`,
   :cve_nist:`2026-23423`, :cve_nist:`2026-23424`, :cve_nist:`2026-23425`, :cve_nist:`2026-23429`,
   :cve_nist:`2026-23430`, :cve_nist:`2026-23431`, :cve_nist:`2026-23432`, :cve_nist:`2026-23433`,
   :cve_nist:`2026-23435`, :cve_nist:`2026-23436`, :cve_nist:`2026-23437`, :cve_nist:`2026-23445`,
   :cve_nist:`2026-23451`, :cve_nist:`2026-23453`, :cve_nist:`2026-23459`, :cve_nist:`2026-23464`,
   :cve_nist:`2026-23466`, :cve_nist:`2026-23467`, :cve_nist:`2026-23469`, :cve_nist:`2026-23470`,
   :cve_nist:`2026-31390`, :cve_nist:`2026-31394`, :cve_nist:`2026-31395`, :cve_nist:`2026-31397`,
   :cve_nist:`2026-31398`, :cve_nist:`2026-31401`, :cve_nist:`2026-31404`, :cve_nist:`2026-31406`,
   :cve_nist:`2026-31413`, :cve_nist:`2026-31435`, :cve_nist:`2026-31436`, :cve_nist:`2026-31437`,
   :cve_nist:`2026-31438`, :cve_nist:`2026-31442`, :cve_nist:`2026-31443`, :cve_nist:`2026-31444`,
   :cve_nist:`2026-31445`, :cve_nist:`2026-31456`, :cve_nist:`2026-31457`, :cve_nist:`2026-31459`,
   :cve_nist:`2026-31460`, :cve_nist:`2026-31461`, :cve_nist:`2026-31463`, :cve_nist:`2026-31465`,
   :cve_nist:`2026-31468`, :cve_nist:`2026-31470`, :cve_nist:`2026-31471`, :cve_nist:`2026-31472`,
   :cve_nist:`2026-31475`, :cve_nist:`2026-31479`, :cve_nist:`2026-31481`, :cve_nist:`2026-31484`,
   :cve_nist:`2026-31490`, :cve_nist:`2026-31491`, :cve_nist:`2026-31499`, :cve_nist:`2026-31501`,
   :cve_nist:`2026-31511`, :cve_nist:`2026-31513`, :cve_nist:`2026-31514`, :cve_nist:`2026-31517`,
   :cve_nist:`2026-31526`, :cve_nist:`2026-31529`, :cve_nist:`2026-31535`, :cve_nist:`2026-31538`,
   :cve_nist:`2026-31539`, :cve_nist:`2026-31541`, :cve_nist:`2026-31543`, :cve_nist:`2026-31544`,
   :cve_nist:`2026-31547`, :cve_nist:`2026-31553`, :cve_nist:`2026-31554`, :cve_nist:`2026-31556`,
   :cve_nist:`2026-31558`, :cve_nist:`2026-31559`, :cve_nist:`2026-31561`, :cve_nist:`2026-31562`,
   :cve_nist:`2026-31564`, :cve_nist:`2026-31567`, :cve_nist:`2026-31569`, :cve_nist:`2026-31571`,
   :cve_nist:`2026-31572`, :cve_nist:`2026-31573`, :cve_nist:`2026-31574`, :cve_nist:`2026-31575`,
   :cve_nist:`2026-31582`, :cve_nist:`2026-31589`, :cve_nist:`2026-31591`, :cve_nist:`2026-31593`,
   :cve_nist:`2026-31600`, :cve_nist:`2026-31601`, :cve_nist:`2026-31608`, :cve_nist:`2026-31609`,
   :cve_nist:`2026-31620`, :cve_nist:`2026-31621`, :cve_nist:`2026-31631`, :cve_nist:`2026-31632`,
   :cve_nist:`2026-31633`, :cve_nist:`2026-31635`, :cve_nist:`2026-31636`, :cve_nist:`2026-31640`,
   :cve_nist:`2026-31641`, :cve_nist:`2026-31643`, :cve_nist:`2026-31644`, :cve_nist:`2026-31647`,
   :cve_nist:`2026-31650`, :cve_nist:`2026-31652`, :cve_nist:`2026-31653`, :cve_nist:`2026-31654`,
   :cve_nist:`2026-31666`, :cve_nist:`2026-31687`, :cve_nist:`2026-31690`, :cve_nist:`2026-31691`,
   :cve_nist:`2026-31703`, :cve_nist:`2026-31710`, :cve_nist:`2026-31713`, :cve_nist:`2026-31719`,
   :cve_nist:`2026-31727`, :cve_nist:`2026-31731`, :cve_nist:`2026-31732`, :cve_nist:`2026-31733`,
   :cve_nist:`2026-31734`, :cve_nist:`2026-31735`, :cve_nist:`2026-31736`, :cve_nist:`2026-31739`,
   :cve_nist:`2026-31742`, :cve_nist:`2026-31743`, :cve_nist:`2026-31744`, :cve_nist:`2026-31745`,
   :cve_nist:`2026-31746`, :cve_nist:`2026-31750`, :cve_nist:`2026-31753`, :cve_nist:`2026-31757`,
   :cve_nist:`2026-31760`, :cve_nist:`2026-31764`, :cve_nist:`2026-31765`, :cve_nist:`2026-31766`,
   :cve_nist:`2026-31769`, :cve_nist:`2026-31772`, :cve_nist:`2026-31774`, :cve_nist:`2026-31775`,
   :cve_nist:`2026-31776`, :cve_nist:`2026-31782`, :cve_nist:`2026-31783`, :cve_nist:`2026-31784`,
   :cve_nist:`2026-31785`, :cve_nist:`2026-43004`, :cve_nist:`2026-43005`, :cve_nist:`2026-43006`,
   :cve_nist:`2026-43008`, :cve_nist:`2026-43012`, :cve_nist:`2026-43021`, :cve_nist:`2026-43031`,
   :cve_nist:`2026-43034`, :cve_nist:`2026-43039`, :cve_nist:`2026-43045`, :cve_nist:`2026-43055`,
   :cve_nist:`2026-43059`, :cve_nist:`2026-43063`, :cve_nist:`2026-43070`, :cve_nist:`2026-43084`,
   :cve_nist:`2026-43087`, :cve_nist:`2026-43090`, :cve_nist:`2026-43095`, :cve_nist:`2026-43096`,
   :cve_nist:`2026-43097`, :cve_nist:`2026-43100`, :cve_nist:`2026-43102`, :cve_nist:`2026-43106`,
   :cve_nist:`2026-43108`, :cve_nist:`2026-43121`, :cve_nist:`2026-43122`, :cve_nist:`2026-43131`,
   :cve_nist:`2026-43138`, :cve_nist:`2026-43142`, :cve_nist:`2026-43144`, :cve_nist:`2026-43146`,
   :cve_nist:`2026-43151`, :cve_nist:`2026-43154`, :cve_nist:`2026-43155`, :cve_nist:`2026-43160`,
   :cve_nist:`2026-43164`, :cve_nist:`2026-43165`, :cve_nist:`2026-43166`, :cve_nist:`2026-43169`,
   :cve_nist:`2026-43174`, :cve_nist:`2026-43175`, :cve_nist:`2026-43176`, :cve_nist:`2026-43177`,
   :cve_nist:`2026-43178`, :cve_nist:`2026-43179`, :cve_nist:`2026-43181`, :cve_nist:`2026-43188`,
   :cve_nist:`2026-43191`, :cve_nist:`2026-43192`, :cve_nist:`2026-43193`, :cve_nist:`2026-43195`,
   :cve_nist:`2026-43201`, :cve_nist:`2026-43208`, :cve_nist:`2026-43210`, :cve_nist:`2026-43217`,
   :cve_nist:`2026-43220`, :cve_nist:`2026-43224`, :cve_nist:`2026-43228`, :cve_nist:`2026-43229`,
   :cve_nist:`2026-43235`, :cve_nist:`2026-43237`, :cve_nist:`2026-43243`, :cve_nist:`2026-43247`,
   :cve_nist:`2026-43254`, :cve_nist:`2026-43259`, :cve_nist:`2026-43260`, :cve_nist:`2026-43263`,
   :cve_nist:`2026-43267`, :cve_nist:`2026-43272`, :cve_nist:`2026-43274`, :cve_nist:`2026-43276`,
   :cve_nist:`2026-43280`, :cve_nist:`2026-43282`, :cve_nist:`2026-43285`, :cve_nist:`2026-43286`,
   :cve_nist:`2026-43290`, :cve_nist:`2026-43292`, :cve_nist:`2026-43293`, :cve_nist:`2026-43297`,
   :cve_nist:`2026-43300`, :cve_nist:`2026-43301`, :cve_nist:`2026-43305`, :cve_nist:`2026-43306`,
   :cve_nist:`2026-43307`, :cve_nist:`2026-43320`, :cve_nist:`2026-43321`, :cve_nist:`2026-43322`,
   :cve_nist:`2026-43323`, :cve_nist:`2026-43325`, :cve_nist:`2026-43326`, :cve_nist:`2026-43335`,
   :cve_nist:`2026-43337`, :cve_nist:`2026-43346`, :cve_nist:`2026-43347`, :cve_nist:`2026-43348`,
   :cve_nist:`2026-43349`, :cve_nist:`2026-43351`, :cve_nist:`2026-43354`, :cve_nist:`2026-43356`,
   :cve_nist:`2026-43358`, :cve_nist:`2026-43364`, :cve_nist:`2026-43367`, :cve_nist:`2026-43369`,
   :cve_nist:`2026-43374`, :cve_nist:`2026-43375`, :cve_nist:`2026-43384`, :cve_nist:`2026-43385`,
   :cve_nist:`2026-43388`, :cve_nist:`2026-43389`, :cve_nist:`2026-43390`, :cve_nist:`2026-43391`,
   :cve_nist:`2026-43392`, :cve_nist:`2026-43393`, :cve_nist:`2026-43394`, :cve_nist:`2026-43395`,
   :cve_nist:`2026-43396`, :cve_nist:`2026-43398`, :cve_nist:`2026-43399`, :cve_nist:`2026-43400`,
   :cve_nist:`2026-43401`, :cve_nist:`2026-43402`, :cve_nist:`2026-43403`, :cve_nist:`2026-43404`,
   :cve_nist:`2026-43408`, :cve_nist:`2026-43410`, :cve_nist:`2026-43417`, :cve_nist:`2026-43418`,
   :cve_nist:`2026-43422`, :cve_nist:`2026-43423`, :cve_nist:`2026-43431`, :cve_nist:`2026-43433`,
   :cve_nist:`2026-43434`, :cve_nist:`2026-43435`, :cve_nist:`2026-43438`, :cve_nist:`2026-43440`,
   :cve_nist:`2026-43442`, :cve_nist:`2026-43444`, :cve_nist:`2026-43446`, :cve_nist:`2026-43447`,
   :cve_nist:`2026-43454`, :cve_nist:`2026-43460`, :cve_nist:`2026-43461`, :cve_nist:`2026-43462`,
   :cve_nist:`2026-43463`, :cve_nist:`2026-43467`, :cve_nist:`2026-43470`, :cve_nist:`2026-43474`,
   :cve_nist:`2026-43477`, :cve_nist:`2026-43478`, :cve_nist:`2026-43479`, :cve_nist:`2026-43481`,
   :cve_nist:`2026-43482`, :cve_nist:`2026-43485`, :cve_nist:`2026-43486`, :cve_nist:`2026-43487`,
   :cve_nist:`2026-43489` and :cve_nist:`2026-43498`
-  lz4: Remove a reference to the rejected :cve_nist:`2025-62813`
-  nfs-utils: Fix :cve_nist:`2025-12801`
-  openssh: Fix :cve_nist:`2026-35386`, :cve_nist:`2026-35385`, :cve_nist:`2026-35387` and
   :cve_nist:`2026-35388`
-  python3-requests: Fix :cve_nist:`2026-25645`
-  python3: Fix :cve_nist:`2025-13462`, :cve_nist:`2026-0672`, :cve_nist:`2026-1502`,
   :cve_nist:`2026-3644`, :cve_nist:`2026-4224`, :cve_nist:`2026-4519`, :cve_nist:`2026-4786`,
   :cve_nist:`2026-6019` and :cve_nist:`2026-6100`
-  python3: Ignore :cve_nist:`2026-3087`
-  qemu: Fix :cve_nist:`2024-6519`
-  qemu: fix :cve_nist:`2025-11234`
-  rust,libstd-rs: Ignore :cve_nist:`2024-3566`
-  tiff: fix :cve_nist:`2026-4775`
-  util-linux: Fix :cve_nist:`2026-27456`
-  xserver-xorg: Fix :cve_nist:`2026-33999`, :cve_nist:`2026-34000`, :cve_nist:`2026-34001`,
   :cve_nist:`2026-34002` and :cve_nist:`2026-34003`
-  xwayland: Fix :cve_nist:`2026-33999`, :cve_nist:`2026-34000`, :cve_nist:`2026-34001`,
   :cve_nist:`2026-34002` and :cve_nist:`2026-34003`
-  xz: Fix :cve_nist:`2026-34743`

Fixes in Yocto-5.0.19
~~~~~~~~~~~~~~~~~~~~~

-  apr-util: Add :term:`CVE_PRODUCT` to support product name
-  apr: Add :term:`CVE_PRODUCT` to support product name
-  bitbake: data: fix issue with varflag exclusion
-  bitbake: fetch2/git: quote shallow extra ref arguments
-  bitbake: fetch2/wget: handle HTTP 308 Permanent Redirect
-  bitbake: fetch2/wget: limit auth on checkstatus redirects
-  bitbake: fetch2: Unpack RPMs with --no-absolute-filenames
-  bitbake: fetch2: validate deb/ipk data member names
-  bitbake: fetch2: validate striplevel parameter
-  bitbake: hashserv/tests: use valid 64-character unihashes
-  bitbake: hashserv: validate unihash values
-  bitbake: tests/fetch: cover checkstatus redirect auth handling
-  bsp-guide: mention bootloader and device tree in BSP intro
-  bsp-guide: update guide to reflect newer beaglebone
-  build-appliance-image: Update to scarthgap head revisions
-  build-manual: update :term:`ROOTFS_POSTPROCESS_COMMAND` example
-  bzip2: set :term:`CVE_PRODUCT`
-  cargo: set :term:`CVE_PRODUCT`
-  classes/base: prefer gnu-prefixed :term:`HOSTTOOLS`
-  classes/gtk-icon-cache: fix libdir passed to the postrm intercept
-  conf/machine: fix typos in ARM and x86 README files
-  contributor-guide: couple minor typo/grammar fixes
-  contributor-guide: fix type "maintainance" to "maintenance"
-  dev-manual: drop "PREFERRED_VERSION" from x86-base.inc snippet
-  dev-manual: fix broken grammar in "Libraries" section
-  dev-manual: fix grammatical error, missing word "with"
-  dev-manual: fully define SOLIBS-related variables in bitbake.conf
-  dev-manual: remove semicolons for rootfs commands
-  dev-manual: SysVinit is the default init manager for Poky
-  dev-manual: update :term:`AUTOREV` explanation to match current file
-  devtool: prevent 'devtool modify -n' from corrupting kernel Git repos
-  docs: conf.py: add a :yocto_bug: role
-  docs: conf.py: define new \*_path roles
-  docs: conf.py: define new {oecore,bitbake,meta_yocto}_rev roles
-  docs: index.rst: update "Software Overview" to "Technical Overview"
-  docs: "Transitioning ..." Various pedantic cleanups
-  docs: What I Wish I'd Known: Various pedantic cleanups
-  docs: YP Quick Build: delete extraneous periods in list
-  gawk-native: fix gcc-15/C23 compilation issues
-  gawk: trim native build configuration
-  gawk: use native gawk when building glibc and grub
-  gdb: backport a patch to fix static_assert in recent GCC
-  gnupg: upgrade to 2.4.9
-  go.bbclass: change GOTMPDIR to improve reproducibility
-  go.bbclass: disable workspaces
-  grub/glibc: Bump versions to resolve hashequiv/reproducibility issues
-  kernel-dev: remove references to defunct LTSI project
-  linux-yocto/6.6: address ltp hang
-  linux-yocto/6.6: genericarm64 fix configuration audit warning
-  linux-yocto/6.6: update to v6.6.142
-  lttng-modules: Fix trace_hrtimer_start build failure
-  meta/lib/oe/package.py: fix path to kernel sources in save_debugsources_info
-  meta: fix generation of kernel CONFIGi\_ in SPDX3
-  migration-guide: add release notes for 4.0.35 5.0.18
-  oeqa/core/runner: stub addDuration in OETestResult
-  oeqa/runtime/parselogs: update pci BAR ignore for kernel 6.10
-  oeqa: Drop /git/ from our urls
-  oeqa: runtime: go: Increase test_go_compile/test_go_module timeout
-  openssl: upgrade to 3.5.7
-  overview-manual: add ":term:" for OE Build System
-  overview-manual: fix "checkout" versus "check out"
-  overview-manual: fix typo, "semi-colon" -> "colon"
-  overview-manual: hyphens not allowed in file version
-  overview-manual: inform the reader early of "bitbake-getvar"
-  overview-manual: mention that patch files can be compressed
-  overview-manual: provide a more expansive definition of "layer"
-  overview-manual: remind reader that meta-poky is a distro layer
-  overview-manual: update deploy.bbclass snippet
-  overview-manual: use correct spelling "counterpart"
-  overview-manual: yp-intro.rst: add link to "buildbot"
-  overview-manual: yp-intro.rst: delete really old references
-  perl: link to the system zlib instead of a vendored copy
-  poky.conf: Bump version for 5.0.19 release
-  pseudo: Update to 1.9.8
-  python_setuptools_build_meta: clean the build directory in configure
-  recipe-style-guide.rst: two minor grammatical tweaks
-  recipetool: Recognise https://git. as git urls
-  ref-manual/variables.rst: link \*MIRRORS definitions to the BitBake manual
-  ref-manual: add more explanation to glossary variable :term:`LICENSE`
-  ref-manual: clarify that :term:`PACKAGE_EXCLUDE` supports DEB packaging
-  ref-manual: clarify use of "PACKAGE_ARCH" in a packagegroup
-  ref-manual: document :term:`RM_WORK_EXCLUDE_ITEMS` variable
-  scripts/install-buildtools: Update to 5.0.18
-  security-team.rst: update my email address and key
-  security-team: Add section on multi-project embargoes
-  security-team: Tidy and update section on security team operations
-  security-team: Update membership list
-  setuptools3: clean the build directory in configure
-  setuptools3_legacy: ensure ${B} is clean
-  systemd: update musl specific patch to apply
-  tzdata/tzcode-native: upgrade to 2026b
-  wic: filemap: use separate fd for SEEK_HOLE probes


Known Issues in Yocto-5.0.19
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.19
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adarsh Jagadish Kamini
-  Alexander Kanavin
-  Amaury Couderc
-  Anders Heimer
-  Anil Dongare
-  Ankur Tyagi
-  Antonin Godard
-  Benjamin Robin (Schneider Electric)
-  Bruce Ashfield
-  Changqing Li
-  Deepak Rathore
-  Enrico Jörns
-  Guðni Már Gilbert
-  He Zhe
-  Himanshu Jadon
-  Hitendra Prajapati
-  Hugo SIMELIERE (Schneider Electric)
-  Jonas Munsin
-  João Marcos Costa (Schneider Electric)
-  Lee Chee Yang
-  Marcio Henriques
-  Mark Hatle
-  Marta Rybczynska
-  Martin Jansa
-  Mathieu Dubois-Briand
-  Naman Jain
-  Paul Barker
-  Peter Bergin
-  Peter Marko
-  Prabhudasu Vatala
-  Richard Purdie
-  Robert P. J. Day
-  Ross Burton
-  Shubham Pushpkar
-  Sudhir Dumbhare
-  Theo Gaige (Schneider Electric)
-  Trevor Woerner
-  Vijay Anusuri
-  Yoann Congal
-  Zahir Hussain


Repositories / Downloads for Yocto-5.0.19
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.19 </yocto-docs/log/?h=yocto-5.0.19>`
-  Git Revision: :yocto_git:`1b819d324780a699d9307a2d4e68c69b576ab748 </yocto-docs/commit/?id=1b819d324780a699d9307a2d4e68c69b576ab748>`
-  Release Artefact: yocto-docs-1b819d324780a699d9307a2d4e68c69b576ab748
-  sha: b2c484d8d13b05b133f6f23f44e7757e4b53d93c27c9ea76d1a2e5f759f51af0
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.19/yocto-docs-1b819d324780a699d9307a2d4e68c69b576ab748.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.19/yocto-docs-1b819d324780a699d9307a2d4e68c69b576ab748.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.19 </poky/log/?h=yocto-5.0.19>`
-  Git Revision: :yocto_git:`bb98354685781296e3b3737e7762412100f359c2 </poky/commit/?id=bb98354685781296e3b3737e7762412100f359c2>`
-  Release Artefact: poky-bb98354685781296e3b3737e7762412100f359c2
-  sha: 37fd9eeff8043e6d7d595421b8ac40d38357d2d4ce3a6d0f86a0f7d884c995c3
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.19/poky-bb98354685781296e3b3737e7762412100f359c2.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.19/poky-bb98354685781296e3b3737e7762412100f359c2.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.19 </openembedded-core/log/?h=yocto-5.0.19>`
-  Git Revision: :oe_git:`2814f0962f56c8d1afa4de76d2895ba9b5cb767d </openembedded-core/commit/?id=2814f0962f56c8d1afa4de76d2895ba9b5cb767d>`
-  Release Artefact: oecore-2814f0962f56c8d1afa4de76d2895ba9b5cb767d
-  sha: 8556e454499fcda4026dc58fb8fc627861720f5c006bd72bf3b455dabd22a0e8
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.19/oecore-2814f0962f56c8d1afa4de76d2895ba9b5cb767d.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.19/oecore-2814f0962f56c8d1afa4de76d2895ba9b5cb767d.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`scarthgap </meta-yocto/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.19 </meta-yocto/log/?h=yocto-5.0.19>`
-  Git Revision: :yocto_git:`2f749ae477c3b94dce71038f025180d7f612dab0 </meta-yocto/commit/?id=2f749ae477c3b94dce71038f025180d7f612dab0>`
-  Release Artefact: meta-yocto-2f749ae477c3b94dce71038f025180d7f612dab0
-  sha: 754237ee59a67d31da1f242555c77b75a96d9cdabdf0a4c9cfd98e378643d031
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.19/meta-yocto-2f749ae477c3b94dce71038f025180d7f612dab0.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.19/meta-yocto-2f749ae477c3b94dce71038f025180d7f612dab0.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.19 </meta-mingw/log/?h=yocto-5.0.19>`
-  Git Revision: :yocto_git:`bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f </meta-mingw/commit/?id=bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f>`
-  Release Artefact: meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f
-  sha: ab073def6487f237ac125d239b3739bf02415270959546b6b287778664f0ae65
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.19/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.19/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.19 </bitbake/log/?h=yocto-5.0.19>`
-  Git Revision: :oe_git:`0880963fea4d91a034e4a6e007d23f98658ab986 </bitbake/commit/?id=0880963fea4d91a034e4a6e007d23f98658ab986>`
-  Release Artefact: bitbake-0880963fea4d91a034e4a6e007d23f98658ab986
-  sha: 2bbf83d622fd4ad7ef0110378a9c88b683b2d82a911aba681b561446ec2f486b
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.19/bitbake-0880963fea4d91a034e4a6e007d23f98658ab986.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.19/bitbake-0880963fea4d91a034e4a6e007d23f98658ab986.tar.bz2

