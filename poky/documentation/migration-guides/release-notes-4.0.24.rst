Release notes for Yocto-4.0.24 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.24
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  coreutils: Fix :cve_nist:`2024-0684`
-  cpio: Ignore :cve_nist:`2023-7216`
-  diffoscope: Fix :cve_nist:`2024-25711`
-  ffmpeg: fix :cve_mitre:`2023-47342`, :cve_nist:`2023-50007`, :cve_nist:`2023-50008`,
   :cve_nist:`2023-51793`, :cve_nist:`2023-51794`, :cve_nist:`2023-51796`, :cve_nist:`2023-51798`,
   :cve_nist:`2024-7055`, :cve_nist:`2024-31578`, :cve_nist:`2024-31582`, :cve_nist:`2024-32230`,
   :cve_nist:`2024-35366`, :cve_nist:`2024-35367` and :cve_nist:`2024-35368`
-  ghostscript: Fix :cve_nist:`2024-46951`, :cve_nist:`2024-46952`, :cve_nist:`2024-46953`,
   :cve_nist:`2024-46955` and :cve_nist:`2024-46956`
-  ghostscript: Ignore :cve_nist:`2024-46954`
-  glib-2.0: Fix :cve_nist:`2024-52533`
-  gnupg: Ignore :cve_nist:`2022-3515`
-  grub: Ignore :cve_nist:`2024-1048` and :cve_nist:`2023-4001`
-  gstreame1.0: Ignore :cve_nist:`2023-40474`, :cve_nist:`2023-40475`, :cve_nist:`2023-40476`,
   :cve_nist:`2023-44429`, :cve_nist:`2023-44446`, :cve_nist:`2023-50186` and :cve_nist:`2024-0444`
-  gstreamer1.0-plugins-base: Fix :cve_nist:`2024-47538`, :cve_nist:`2024-47541`,
   :cve_nist:`2024-47542`, :cve_nist:`2024-47600`, :cve_nist:`2024-47607`, :cve_nist:`2024-47615`
   and :cve_nist:`2024-47835`
-  gstreamer1.0-plugins-good: Fix :cve_nist:`2024-47537`, :cve_nist:`2024-47539`,
   :cve_nist:`2024-47540`, :cve_nist:`2024-47543`, :cve_nist:`2024-47544`, :cve_nist:`2024-47545`,
   :cve_nist:`2024-47546`, :cve_nist:`2024-47596`, :cve_nist:`2024-47597`, :cve_nist:`2024-47598`,
   :cve_nist:`2024-47599`, :cve_nist:`2024-47601`, :cve_nist:`2024-47602`, :cve_nist:`2024-47603`,
   :cve_nist:`2024-47606`, :cve_nist:`2024-47613`, :cve_nist:`2024-47774`, :cve_nist:`2024-47775`,
   :cve_nist:`2024-47776`, :cve_nist:`2024-47777`, :cve_nist:`2024-47778` and :cve_nist:`2024-47834`
-  gstreamer1.0: Fix :cve_nist:`2024-47606`
-  libarchive: Fix :cve_nist:`2024-20696`
-  libpam: Fix :cve_nist:`2024-10041`
-  libsdl2: Ignore :cve_nist:`2020-14409` and :cve_nist:`2020-14410`
-  libsndfile1: Fix :cve_nist:`2022-33065` and :cve_nist:`2024-50612`
-  libsoup-2.4: Fix :cve_nist:`2024-52530`, :cve_nist:`2024-52531` and :cve_nist:`2024-52532`
-  libsoup: Fix :cve_nist:`2024-52530`, :cve_nist:`2024-52531` and :cve_nist:`2024-52532`
-  linux-yocto/5.10: Fix :cve_nist:`2023-52889`, :cve_nist:`2023-52917`, :cve_nist:`2023-52918`,
   :cve_nist:`2024-41011`, :cve_nist:`2024-42259`, :cve_nist:`2024-42271`, :cve_nist:`2024-42272`,
   :cve_nist:`2024-42280`, :cve_nist:`2024-42283`, :cve_nist:`2024-42284`, :cve_nist:`2024-42285`,
   :cve_nist:`2024-42286`, :cve_nist:`2024-42287`, :cve_nist:`2024-42288`, :cve_nist:`2024-42289`,
   :cve_nist:`2024-42301`, :cve_nist:`2024-42302`, :cve_nist:`2024-42309`, :cve_nist:`2024-42310`,
   :cve_nist:`2024-42311`, :cve_nist:`2024-42313`, :cve_nist:`2024-43828`, :cve_nist:`2024-43856`,
   :cve_nist:`2024-43858`, :cve_nist:`2024-43860`, :cve_nist:`2024-43861`, :cve_nist:`2024-43871`,
   :cve_nist:`2024-43882`, :cve_nist:`2024-43889`, :cve_nist:`2024-43890`, :cve_nist:`2024-43893`,
   :cve_nist:`2024-43894`, :cve_nist:`2024-43907`, :cve_nist:`2024-43908`, :cve_nist:`2024-43914`,
   :cve_nist:`2024-44935`, :cve_nist:`2024-44944`, :cve_nist:`2024-44947`, :cve_nist:`2024-44954`,
   :cve_nist:`2024-44960`, :cve_nist:`2024-44965`, :cve_nist:`2024-44969`, :cve_nist:`2024-44971`,
   :cve_nist:`2024-44987`, :cve_nist:`2024-44988`, :cve_nist:`2024-44989`, :cve_nist:`2024-44990`,
   :cve_nist:`2024-44995`, :cve_nist:`2024-44998`, :cve_nist:`2024-44999`, :cve_nist:`2024-45003`,
   :cve_nist:`2024-45006`, :cve_nist:`2024-45016`, :cve_nist:`2024-45018`, :cve_nist:`2024-45021`,
   :cve_nist:`2024-45025`, :cve_nist:`2024-45026`, :cve_nist:`2024-45028`, :cve_nist:`2024-46673`,
   :cve_nist:`2024-46674`, :cve_nist:`2024-46675`, :cve_nist:`2024-46676`, :cve_nist:`2024-46677`,
   :cve_nist:`2024-46679`, :cve_nist:`2024-46685`, :cve_nist:`2024-46689`, :cve_nist:`2024-46702`,
   :cve_nist:`2024-46707`, :cve_nist:`2024-46714`, :cve_nist:`2024-46719`, :cve_nist:`2024-46721`,
   :cve_nist:`2024-46722`, :cve_nist:`2024-46723`, :cve_nist:`2024-46724`, :cve_nist:`2024-46725`,
   :cve_nist:`2024-46731`, :cve_nist:`2024-46737`, :cve_nist:`2024-46738`, :cve_nist:`2024-46739`,
   :cve_nist:`2024-46740`, :cve_nist:`2024-46743`, :cve_nist:`2024-46744`, :cve_nist:`2024-46747`,
   :cve_nist:`2024-46750`, :cve_nist:`2024-46755`, :cve_nist:`2024-46759`, :cve_nist:`2024-46761`,
   :cve_nist:`2024-46763`, :cve_nist:`2024-46771`, :cve_nist:`2024-46777`, :cve_nist:`2024-46780`,
   :cve_nist:`2024-46781`, :cve_nist:`2024-46782`, :cve_nist:`2024-46783`, :cve_nist:`2024-46791`,
   :cve_nist:`2024-46798`, :cve_nist:`2024-46800`, :cve_nist:`2024-46804`, :cve_nist:`2024-46814`,
   :cve_nist:`2024-46815`, :cve_nist:`2024-46817`, :cve_nist:`2024-46818`, :cve_nist:`2024-46819`,
   :cve_nist:`2024-46822`, :cve_nist:`2024-46828`, :cve_nist:`2024-46829`, :cve_nist:`2024-46832`,
   :cve_nist:`2024-46840`, :cve_nist:`2024-46844`, :cve_nist:`2024-47659`, :cve_nist:`2024-47660`,
   :cve_nist:`2024-47663`, :cve_nist:`2024-47667`, :cve_nist:`2024-47668`, :cve_nist:`2024-47669`,
   :cve_nist:`2024-47679`, :cve_nist:`2024-47684`, :cve_nist:`2024-47685`, :cve_nist:`2024-47692`,
   :cve_nist:`2024-47697`, :cve_nist:`2024-47698`, :cve_nist:`2024-47699`, :cve_nist:`2024-47701`,
   :cve_nist:`2024-47705`, :cve_nist:`2024-47706`, :cve_nist:`2024-47710`, :cve_nist:`2024-47712`,
   :cve_nist:`2024-47713`, :cve_nist:`2024-47718`, :cve_nist:`2024-47723`, :cve_nist:`2024-47735`,
   :cve_nist:`2024-47737`, :cve_nist:`2024-47739`, :cve_nist:`2024-47742`, :cve_nist:`2024-47747`,
   :cve_nist:`2024-47748`, :cve_nist:`2024-47749`, :cve_nist:`2024-47757`, :cve_nist:`2024-49851`,
   :cve_nist:`2024-49858`, :cve_nist:`2024-49860`, :cve_nist:`2024-49863`, :cve_nist:`2024-49867`,
   :cve_nist:`2024-49868`, :cve_nist:`2024-49875`, :cve_nist:`2024-49877`, :cve_nist:`2024-49878`,
   :cve_nist:`2024-49879`, :cve_nist:`2024-49881`, :cve_nist:`2024-49882`, :cve_nist:`2024-49883`,
   :cve_nist:`2024-49884`, :cve_nist:`2024-49889`, :cve_nist:`2024-49890`, :cve_nist:`2024-49892`,
   :cve_nist:`2024-49894`, :cve_nist:`2024-49895`, :cve_nist:`2024-49896`, :cve_nist:`2024-49900`,
   :cve_nist:`2024-49902`, :cve_nist:`2024-49903`, :cve_nist:`2024-49907`, :cve_nist:`2024-49913`,
   :cve_nist:`2024-49924`, :cve_nist:`2024-49930`, :cve_nist:`2024-49933`, :cve_nist:`2024-49936`,
   :cve_nist:`2024-49938`, :cve_nist:`2024-49944`, :cve_nist:`2024-49948`, :cve_nist:`2024-49949`,
   :cve_nist:`2024-49952`, :cve_nist:`2024-49955`, :cve_nist:`2024-49957`, :cve_nist:`2024-49958`,
   :cve_nist:`2024-49959`, :cve_nist:`2024-49962`, :cve_nist:`2024-49963`, :cve_nist:`2024-49965`,
   :cve_nist:`2024-49966`, :cve_nist:`2024-49969`, :cve_nist:`2024-49973`, :cve_nist:`2024-49975`,
   :cve_nist:`2024-49977`, :cve_nist:`2024-49981`, :cve_nist:`2024-49982`, :cve_nist:`2024-49983`,
   :cve_nist:`2024-49985`, :cve_nist:`2024-49995`, :cve_nist:`2024-49997`, :cve_nist:`2024-50001`,
   :cve_nist:`2024-50006`, :cve_nist:`2024-50007`, :cve_nist:`2024-50008`, :cve_nist:`2024-50013`,
   :cve_nist:`2024-50015`, :cve_nist:`2024-50024`, :cve_nist:`2024-50033`, :cve_nist:`2024-50035`,
   :cve_nist:`2024-50039`, :cve_nist:`2024-50040`, :cve_nist:`2024-50044`, :cve_nist:`2024-50045`,
   :cve_nist:`2024-50046`, :cve_nist:`2024-50049`, :cve_nist:`2024-50059`, :cve_nist:`2024-50095`,
   :cve_nist:`2024-50096`, :cve_nist:`2024-50179`, :cve_nist:`2024-50180`, :cve_nist:`2024-50181`,
   :cve_nist:`2024-50184` and :cve_nist:`2024-50188`
-  linux-yocto/5.15: Fix :cve_nist:`2022-48695`, :cve_nist:`2023-52530`, :cve_nist:`2023-52917`,
   :cve_nist:`2024-45009`, :cve_nist:`2024-46714`, :cve_nist:`2024-46719`, :cve_nist:`2024-46721`,
   :cve_nist:`2024-46722`, :cve_nist:`2024-46723`, :cve_nist:`2024-46724`, :cve_nist:`2024-46725`,
   :cve_nist:`2024-46731`, :cve_nist:`2024-46732`, :cve_nist:`2024-46737`, :cve_nist:`2024-46738`,
   :cve_nist:`2024-46739`, :cve_nist:`2024-46740`, :cve_nist:`2024-46743`, :cve_nist:`2024-46744`,
   :cve_nist:`2024-46746`, :cve_nist:`2024-46747`, :cve_nist:`2024-46750`, :cve_nist:`2024-46755`,
   :cve_nist:`2024-46759`, :cve_nist:`2024-46761`, :cve_nist:`2024-46763`, :cve_nist:`2024-46771`,
   :cve_nist:`2024-46777`, :cve_nist:`2024-46780`, :cve_nist:`2024-46781`, :cve_nist:`2024-46782`,
   :cve_nist:`2024-46783`, :cve_nist:`2024-46791`, :cve_nist:`2024-46795`, :cve_nist:`2024-46798`,
   :cve_nist:`2024-46800`, :cve_nist:`2024-46804`, :cve_nist:`2024-46805`, :cve_nist:`2024-46807`,
   :cve_nist:`2024-46810`, :cve_nist:`2024-46814`, :cve_nist:`2024-46815`, :cve_nist:`2024-46817`,
   :cve_nist:`2024-46818`, :cve_nist:`2024-46819`, :cve_nist:`2024-46822`, :cve_nist:`2024-46828`,
   :cve_nist:`2024-46829`, :cve_nist:`2024-46832`, :cve_nist:`2024-46840`, :cve_nist:`2024-46844`,
   :cve_nist:`2024-47659`, :cve_nist:`2024-47660`, :cve_nist:`2024-47663`, :cve_nist:`2024-47665`,
   :cve_nist:`2024-47667`, :cve_nist:`2024-47668`, :cve_nist:`2024-47669`, :cve_nist:`2024-47674`,
   :cve_nist:`2024-47679`, :cve_nist:`2024-47684`, :cve_nist:`2024-47685`, :cve_nist:`2024-47690`,
   :cve_nist:`2024-47692`, :cve_nist:`2024-47693`, :cve_nist:`2024-47695`, :cve_nist:`2024-47696`,
   :cve_nist:`2024-47697`, :cve_nist:`2024-47698`, :cve_nist:`2024-47699`, :cve_nist:`2024-47701`,
   :cve_nist:`2024-47705`, :cve_nist:`2024-47706`, :cve_nist:`2024-47710`, :cve_nist:`2024-47712`,
   :cve_nist:`2024-47713`, :cve_nist:`2024-47718`, :cve_nist:`2024-47720`, :cve_nist:`2024-47723`,
   :cve_nist:`2024-47734`, :cve_nist:`2024-47735`, :cve_nist:`2024-47737`, :cve_nist:`2024-47739`,
   :cve_nist:`2024-47742`, :cve_nist:`2024-47747`, :cve_nist:`2024-47748`, :cve_nist:`2024-47749`,
   :cve_nist:`2024-47757`, :cve_nist:`2024-49851`, :cve_nist:`2024-49852`, :cve_nist:`2024-49854`,
   :cve_nist:`2024-49856`, :cve_nist:`2024-49858`, :cve_nist:`2024-49860`, :cve_nist:`2024-49863`,
   :cve_nist:`2024-49866`, :cve_nist:`2024-49867`, :cve_nist:`2024-49868`, :cve_nist:`2024-49871`,
   :cve_nist:`2024-49875`, :cve_nist:`2024-49877`, :cve_nist:`2024-49878`, :cve_nist:`2024-49879`,
   :cve_nist:`2024-49881`, :cve_nist:`2024-49882`, :cve_nist:`2024-49883`, :cve_nist:`2024-49884`,
   :cve_nist:`2024-49886`, :cve_nist:`2024-49889`, :cve_nist:`2024-49890`, :cve_nist:`2024-49892`,
   :cve_nist:`2024-49894`, :cve_nist:`2024-49895`, :cve_nist:`2024-49896`, :cve_nist:`2024-49900`,
   :cve_nist:`2024-49902`, :cve_nist:`2024-49903`, :cve_nist:`2024-49907`, :cve_nist:`2024-49913`,
   :cve_nist:`2024-49924`, :cve_nist:`2024-49927`, :cve_nist:`2024-49930`, :cve_nist:`2024-49933`,
   :cve_nist:`2024-49935`, :cve_nist:`2024-49936`, :cve_nist:`2024-49938`, :cve_nist:`2024-49944`,
   :cve_nist:`2024-49946`, :cve_nist:`2024-49948`, :cve_nist:`2024-49949`, :cve_nist:`2024-49952`,
   :cve_nist:`2024-49954`, :cve_nist:`2024-49955`, :cve_nist:`2024-49957`, :cve_nist:`2024-49958`,
   :cve_nist:`2024-49959`, :cve_nist:`2024-49962`, :cve_nist:`2024-49963`, :cve_nist:`2024-49965`,
   :cve_nist:`2024-49966`, :cve_nist:`2024-49969`, :cve_nist:`2024-49973`, :cve_nist:`2024-49975`,
   :cve_nist:`2024-49977`, :cve_nist:`2024-49981`, :cve_nist:`2024-49982`, :cve_nist:`2024-49983`,
   :cve_nist:`2024-49985`, :cve_nist:`2024-49995`, :cve_nist:`2024-49997`, :cve_nist:`2024-50000`,
   :cve_nist:`2024-50001`, :cve_nist:`2024-50002`, :cve_nist:`2024-50003`, :cve_nist:`2024-50006`,
   :cve_nist:`2024-50007`, :cve_nist:`2024-50008`, :cve_nist:`2024-50013`, :cve_nist:`2024-50015`,
   :cve_nist:`2024-50019`, :cve_nist:`2024-50024`, :cve_nist:`2024-50031`, :cve_nist:`2024-50033`,
   :cve_nist:`2024-50035`, :cve_nist:`2024-50038`, :cve_nist:`2024-50039`, :cve_nist:`2024-50040`,
   :cve_nist:`2024-50041`, :cve_nist:`2024-50044`, :cve_nist:`2024-50045`, :cve_nist:`2024-50046`,
   :cve_nist:`2024-50049`, :cve_nist:`2024-50059`, :cve_nist:`2024-50062`, :cve_nist:`2024-50074`,
   :cve_nist:`2024-50082`, :cve_nist:`2024-50083`, :cve_nist:`2024-50093`, :cve_nist:`2024-50095`,
   :cve_nist:`2024-50096`, :cve_nist:`2024-50099`, :cve_nist:`2024-50101`, :cve_nist:`2024-50103`,
   :cve_nist:`2024-50110`, :cve_nist:`2024-50115`, :cve_nist:`2024-50116`, :cve_nist:`2024-50117`,
   :cve_nist:`2024-50127`, :cve_nist:`2024-50128`, :cve_nist:`2024-50131`, :cve_nist:`2024-50134`,
   :cve_nist:`2024-50141`, :cve_nist:`2024-50142`, :cve_nist:`2024-50143`, :cve_nist:`2024-50148`,
   :cve_nist:`2024-50150`, :cve_nist:`2024-50151`, :cve_nist:`2024-50153`, :cve_nist:`2024-50154`,
   :cve_nist:`2024-50156`, :cve_nist:`2024-50160`, :cve_nist:`2024-50162`, :cve_nist:`2024-50163`,
   :cve_nist:`2024-50167`, :cve_nist:`2024-50168`, :cve_nist:`2024-50171`, :cve_nist:`2024-50179`,
   :cve_nist:`2024-50180`, :cve_nist:`2024-50181`, :cve_nist:`2024-50182`, :cve_nist:`2024-50184`,
   :cve_nist:`2024-50185`, :cve_nist:`2024-50186`, :cve_nist:`2024-50188`, :cve_nist:`2024-50189`,
   :cve_nist:`2024-50191`, :cve_nist:`2024-50192`, :cve_nist:`2024-50193`, :cve_nist:`2024-50194`,
   :cve_nist:`2024-50195`, :cve_nist:`2024-50196`, :cve_nist:`2024-50198`, :cve_nist:`2024-50201`,
   :cve_nist:`2024-50202`, :cve_nist:`2024-50205`, :cve_nist:`2024-50208`, :cve_nist:`2024-50209`,
   :cve_nist:`2024-50229`, :cve_nist:`2024-50230`, :cve_nist:`2024-50232`, :cve_nist:`2024-50233`,
   :cve_nist:`2024-50234`, :cve_nist:`2024-50236`, :cve_nist:`2024-50237`, :cve_nist:`2024-50244`,
   :cve_nist:`2024-50245`, :cve_nist:`2024-50247`, :cve_nist:`2024-50251`, :cve_nist:`2024-50257`,
   :cve_nist:`2024-50259`, :cve_nist:`2024-50262`, :cve_nist:`2024-50264`, :cve_nist:`2024-50265`,
   :cve_nist:`2024-50267`, :cve_nist:`2024-50268`, :cve_nist:`2024-50269`, :cve_nist:`2024-50273`,
   :cve_nist:`2024-50278`, :cve_nist:`2024-50279`, :cve_nist:`2024-50282`, :cve_nist:`2024-50287`,
   :cve_nist:`2024-50292`, :cve_nist:`2024-50296`, :cve_nist:`2024-50299`, :cve_nist:`2024-50301`,
   :cve_nist:`2024-50302`, :cve_nist:`2024-53052`, :cve_nist:`2024-53055`, :cve_nist:`2024-53057`,
   :cve_nist:`2024-53058`, :cve_nist:`2024-53059`, :cve_nist:`2024-53060`, :cve_nist:`2024-53061`,
   :cve_nist:`2024-53063`, :cve_nist:`2024-53066`, :cve_nist:`2024-53088`, :cve_nist:`2024-53096`,
   :cve_nist:`2024-53101`, :cve_nist:`2024-53103`, :cve_nist:`2024-53145`, :cve_nist:`2024-53146`,
   :cve_nist:`2024-53150`, :cve_nist:`2024-53151`, :cve_nist:`2024-53155`, :cve_nist:`2024-53156`,
   :cve_nist:`2024-53157`, :cve_nist:`2024-53165`, :cve_nist:`2024-53171`, :cve_nist:`2024-53173`,
   :cve_nist:`2024-53226`, :cve_nist:`2024-53227`, :cve_nist:`2024-53237`, :cve_nist:`2024-56567`,
   :cve_nist:`2024-56572`, :cve_nist:`2024-56574`, :cve_nist:`2024-56578`, :cve_nist:`2024-56581`,
   :cve_nist:`2024-56593`, :cve_nist:`2024-56600`, :cve_nist:`2024-56601`, :cve_nist:`2024-56602`,
   :cve_nist:`2024-56603`, :cve_nist:`2024-56605`, :cve_nist:`2024-56606`, :cve_nist:`2024-56614`,
   :cve_nist:`2024-56622`, :cve_nist:`2024-56623`, :cve_nist:`2024-56629`, :cve_nist:`2024-56634`,
   :cve_nist:`2024-56640`, :cve_nist:`2024-56642`, :cve_nist:`2024-56643`, :cve_nist:`2024-56648`,
   :cve_nist:`2024-56650`, :cve_nist:`2024-56659`, :cve_nist:`2024-56662`, :cve_nist:`2024-56670`,
   :cve_nist:`2024-56688`, :cve_nist:`2024-56694`, :cve_nist:`2024-56704`, :cve_nist:`2024-56708`,
   :cve_nist:`2024-56720`, :cve_nist:`2024-56723`, :cve_nist:`2024-56724`, :cve_nist:`2024-56726`,
   :cve_nist:`2024-56728`, :cve_nist:`2024-56739`, :cve_nist:`2024-56741`, :cve_nist:`2024-56745`,
   :cve_nist:`2024-56746`, :cve_nist:`2024-56747`, :cve_nist:`2024-56748`, :cve_nist:`2024-56754`,
   :cve_nist:`2024-56756`, :cve_nist:`2024-56770`, :cve_nist:`2024-56774`, :cve_nist:`2024-56776`,
   :cve_nist:`2024-56777`, :cve_nist:`2024-56778`, :cve_nist:`2024-56779`, :cve_nist:`2024-56780`,
   :cve_nist:`2024-56781`, :cve_nist:`2024-56785` and :cve_nist:`2024-56787`
-  ovmf: Fix :cve_nist:`2022-36763`, :cve_nist:`2022-36764`, :cve_nist:`2022-36765`,
   :cve_nist:`2023-45229`, :cve_nist:`2023-45230`, :cve_nist:`2023-45231`, :cve_nist:`2023-45232`,
   :cve_nist:`2023-45233`, :cve_nist:`2023-45234`, :cve_nist:`2023-45235`, :cve_nist:`2023-45236`,
   :cve_nist:`2023-45237`, :cve_nist:`2024-1298` and :cve_nist:`2024-38796`
-  pixman: Ignore :cve_nist:`2023-37769`
-  python3: Fix :cve_nist:`2024-9287`, :cve_nist:`2024-11168` and :cve_nist:`2024-50602`
-  python3-pip: Fix :cve_nist:`2023-5752`
-  python3-requests: Fix :cve_nist:`2024-35195`
-  python3-zipp: Fix :cve_nist:`2024-5569`
-  qemu: Fix :cve_nist:`2024-3446`, :cve_nist:`2024-3447` and :cve_nist:`2024-6505`
-  qemu: Ignore :cve_nist:`2022-36648`
-  subversion: Fix :cve_nist:`2024-46901`
-  tiff: Fix :cve_nist:`2023-3164`
-  tiff: Ignore :cve_nist:`2023-2731`
-  webkitgtk: Fix :cve_nist:`2024-40776` and :cve_nist:`2024-40780`
-  xserver-xorg: Fix :cve_nist:`2024-9632`
-  xwayland: Fix :cve_nist:`2023-5380` and :cve_nist:`2024-0229`


Fixes in Yocto-4.0.24
~~~~~~~~~~~~~~~~~~~~~

-  base-passwd: Add the sgx group
-  base-passwd: Regenerate the patches
-  base-passwd: Update the status for two patches
-  base-passwd: Update to 3.5.52
-  base-passwd: add the wheel group
-  base-passwd: fix patchreview warning
-  bitbake: fetch2: use persist_data context managers
-  bitbake: fetch/wget: Increase timeout to 100s from 30s
-  bitbake: persist_data: close connection in SQLTable __exit__
-  build-appliance-image: Update to kirkstone head revision
-  builder: set :term:`CVE_PRODUCT`
-  contributor-guide: submit-changes.rst: suggest to remove the git signature
-  cve-update-nvd2-native: Tweak to work better with NFS :term:`DL_DIR`
-  dbus: disable assertions and enable only modular tests
-  do_package/sstate/sstatesig: Change timestamp clamping to hash output only
-  docs: Gather dependencies in poky.yaml.in
-  docs: standards.md: add a section on admonitions
-  gstreamer1.0: improve test reliability
-  linux-yocto/5.10: update to v5.10.227
-  linux-yocto/5.15: update to v5.15.175
-  llvm: reduce size of -dbg package
-  lttng-modules: fix build error after kernel update to 5.15.171
-  migration-guides: add release notes for 4.0.23
-  ninja: fix build with python 3.13
-  oeqa/utils/gitarchive: Return tag name and improve exclude handling
-  ovmf-native: remove .pyc files from install
-  package.bbclass: Use shlex instead of deprecated pipes
-  package_rpm: restrict rpm to 4 threads
-  package_rpm: use zstd's default compression level
-  poky.conf: add new tested distros
-  poky.conf: bump version for 4.0.24
-  poky.yaml.in: add missing locales dependency
-  python3: upgrade to 3.10.16
-  ref-manual: SSTATE_MIRRORS/SOURCE_MIRROR_URL: add instructions for mirror authentication
-  ref-manual: classes: fix bin_package description
-  ref-manual: devtool-reference: add warning note on deploy-target and shared objects
-  ref-manual: move runtime-testing section to the test-manual
-  ref-manual: packages: move ptest section to the test-manual
-  ref-manual: system-requirements: update list of supported distros
-  ref-manual: use standardized method accross both ubuntu and debian for locale install
-  resulttool: Add --logfile-archive option to store mode
-  resulttool: Allow store to filter to specific revisions
-  resulttool: Clean up repoducible build logs
-  resulttool: Fix passthrough of --all files in store mode
-  resulttool: Handle ltp rawlogs as well as ptest
-  resulttool: Improve repo layout for oeselftest results
-  resulttool: Trim the precision of duration information
-  resulttool: Use single space indentation in json output
-  rootfs-postcommands.bbclass: make opkg status reproducible
-  rxvt-unicode.inc: disable the terminfo installation by setting TIC to :
-  sanity: check for working user namespaces
-  scripts/install-buildtools: Update to 4.0.22
-  selftest/reproducible: Clean up pathnames
-  selftest/reproducible: Drop rawlogs
-  test-manual: reproducible-builds.rst: document :term:`OEQA_REPRODUCIBLE_TEST_TARGET` and
   :term:`OEQA_REPRODUCIBLE_TEST_SSTATE_TARGETS`
-  test-manual: reproducible-builds.rst: show how to build a single package
-  toolchain-shar-extract.sh: exit when post-relocate-setup.sh fails
-  tzdata & tzcode-native: upgrade 2024b
-  udev-extraconf: fix network.sh script did not configure hotplugged interfaces
-  unzip: Fix configure tests to use modern C
-  webkitgtk: Fix build on 32bit arm
-  webkitgtk: fix perl-native dependency
-  webkitgtk: reduce size of -dbg package
-  wireless-regdb: upgrade to 2024.10.07


Known Issues in Yocto-4.0.24
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-4.0.24
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Aleksandar Nikolic
-  Alex Kiernan
-  Alexander Kanavin
-  Alexandre Belloni
-  Antonin Godard
-  Archana Polampalli
-  Bruce Ashfield
-  Changqing Li
-  Chen Qi
-  Chris Laplante
-  Divya Chellam
-  Ernst Persson
-  Guénaël Muller
-  Hitendra Prajapati
-  Hongxu Jia
-  Jiaying Song
-  Jinfeng Wang
-  Khem Raj
-  Lee Chee Yang
-  Liyin Zhang
-  Louis Rannou
-  Markus Volk
-  Mikko Rapeli
-  Ovidiu Panait
-  Peter Kjellerstedt
-  Peter Marko
-  Regis Dargent
-  Richard Purdie
-  Rohini Sangam
-  Ross Burton
-  Soumya Sambu
-  Steve Sakoman
-  Trevor Gamblin
-  Vijay Anusuri
-  Wang Mingyu
-  Yogita Urade
-  Zahir Hussain


Repositories / Downloads for Yocto-4.0.24
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.24 </poky/log/?h=yocto-4.0.24>`
-  Git Revision: :yocto_git:`f50532593651dff82bc952288d786c55038c2c86 </poky/commit/?id=f50532593651dff82bc952288d786c55038c2c86>`
-  Release Artefact: poky-f50532593651dff82bc952288d786c55038c2c86
-  sha: 0aa062d19510394748db9a2d6ded2d764f435383296d9c94fb6b25755280556e
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.24/poky-f50532593651dff82bc952288d786c55038c2c86.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.24/poky-f50532593651dff82bc952288d786c55038c2c86.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.24 </openembedded-core/log/?h=yocto-4.0.24>`
-  Git Revision: :oe_git:`a270d4c957259761bcc7382fcc54642a02f9fc7d </openembedded-core/commit/?id=a270d4c957259761bcc7382fcc54642a02f9fc7d>`
-  Release Artefact: oecore-a270d4c957259761bcc7382fcc54642a02f9fc7d
-  sha: b08b9b16c8ffa587d521ad28e24e38c79d757a6f0839d18165ebac3081a34b68
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.24/oecore-a270d4c957259761bcc7382fcc54642a02f9fc7d.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.24/oecore-a270d4c957259761bcc7382fcc54642a02f9fc7d.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.24 </meta-mingw/log/?h=yocto-4.0.24>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.24/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.24/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.24 </meta-gplv2/log/?h=yocto-4.0.24>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.24/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.24/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.24 </bitbake/log/?h=yocto-4.0.24>`
-  Git Revision: :oe_git:`3f88b005244a0afb5d5c7260e54a94a453ec9b3e </bitbake/commit/?id=3f88b005244a0afb5d5c7260e54a94a453ec9b3e>`
-  Release Artefact: bitbake-3f88b005244a0afb5d5c7260e54a94a453ec9b3e
-  sha: 31f442b72ec7d81ca75509b1a7179c3fe3942528b1e31c823b21a413244bd15b
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.24/bitbake-3f88b005244a0afb5d5c7260e54a94a453ec9b3e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.24/bitbake-3f88b005244a0afb5d5c7260e54a94a453ec9b3e.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.24 </yocto-docs/log/?h=yocto-4.0.24>`
-  Git Revision: :yocto_git:`3128bf149f40928e6c2a3e264590a0c6c9778c6a </yocto-docs/commit/?id=3128bf149f40928e6c2a3e264590a0c6c9778c6a>`

