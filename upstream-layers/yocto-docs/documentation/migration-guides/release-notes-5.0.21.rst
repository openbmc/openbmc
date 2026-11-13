Release notes for Yocto-5.0.21 (Scarthgap)
------------------------------------------

Expat has been updated to v2.8.3 to fix CVEs for which the patches were too complex to backport.

Security Fixes in Yocto-5.0.21
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  alsa-lib: Fix :cve_nist:`2026-56109`
-  apt: Ignore :cve_nist:`2011-3374`
-  binutils: Fix :cve_nist:`2025-1147`, :cve_nist:`2025-8224`, :cve_nist:`2026-3441`,
   :cve_nist:`2026-3442`, :cve_nist:`2026-4647`, :cve_nist:`2026-15003` and :cve_nist:`2026-18220`
-  bison: Fix :cve_nist:`2026-56389`
-  busybox: Fix :cve_nist:`2024-58251`
-  cpio: Fix :cve_nist:`2026-66484` and :cve_nist:`2026-66485`
-  curl: Fix :cve_nist:`2026-13608`
-  curl: Ignore :cve_nist:`2026-8458` and :cve_nist:`2026-82209`
-  expat: Ignore :cve_nist:`2026-72522`
-  glib-2.0: Fix :cve_nist:`2026-58015`
-  glibc: Fix :cve_nist:`2026-5435` and :cve_nist:`2026-6238`
-  gnutls: Fix :cve_nist:`2026-5419`, :cve_nist:`2026-33845`, :cve_nist:`2026-33846`,
   :cve_nist:`2026-42010` and :cve_nist:`2026-42011`
-  gnutls: Ignore :cve_nist:`2026-1584`
-  go: Fix :cve_nist:`2026-39823`
-  kbd: Fix :cve_nist:`2026-72693`
-  libarchive: Ignore :cve_nist:`2026-14164`
-  libarchive: Fix :cve_nist:`2026-5121` and :cve_nist:`2026-5745`
-  libevent: Ignore :cve_nist:`2026-63380`
-  libslirp: Fix :cve_nist:`2026-9539`
-  libssh2: Fix :cve_nist:`2026-58050` and :cve_nist:`2026-58051`
-  libxfont2: Fix :cve_nist:`2026-56001`, :cve_nist:`2026-56002` and :cve_nist:`2026-56003`
-  libxfont: Fix :cve_nist:`2026-56001`, :cve_nist:`2026-56002` and :cve_nist:`2026-56003`
-  linux-yocto/6.6: Fix :cve_nist:`2026-23385`, :cve_nist:`2026-53090`, :cve_nist:`2026-64192`,
   :cve_nist:`2026-64205`, :cve_nist:`2026-64280`, :cve_nist:`2025-38525`, :cve_nist:`2025-39901`,
   :cve_nist:`2025-40307`, :cve_nist:`2026-64561`, :cve_nist:`2026-64562`, :cve_nist:`2026-64563`,
   :cve_nist:`2026-64564`, :cve_nist:`2026-64565`, :cve_nist:`2026-64569`, :cve_nist:`2026-64567`,
   :cve_nist:`2026-64571`, :cve_nist:`2026-64572`, :cve_nist:`2026-64573`, :cve_nist:`2026-64574`,
   :cve_nist:`2026-64576`, :cve_nist:`2026-64577`, :cve_nist:`2026-64578`, :cve_nist:`2026-64579`,
   :cve_nist:`2026-64580`, :cve_nist:`2026-64583`, :cve_nist:`2026-64584`, :cve_nist:`2026-64590`,
   :cve_nist:`2026-64586`, :cve_nist:`2026-64542`, :cve_nist:`2026-68093`, :cve_nist:`2026-68096`,
   :cve_nist:`2026-68097`, :cve_nist:`2026-68098`, :cve_nist:`2026-68099`, :cve_nist:`2026-68100`,
   :cve_nist:`2026-68102`, :cve_nist:`2026-68104`, :cve_nist:`2026-68106`, :cve_nist:`2026-68107`,
   :cve_nist:`2026-68108`, :cve_nist:`2026-68110`, :cve_nist:`2026-68111`, :cve_nist:`2026-68112`,
   :cve_nist:`2026-68115`, :cve_nist:`2026-68116`, :cve_nist:`2026-68117`, :cve_nist:`2026-68121`,
   :cve_nist:`2026-68123`, :cve_nist:`2026-68124`, :cve_nist:`2026-68125`, :cve_nist:`2026-68126`,
   :cve_nist:`2026-68127`, :cve_nist:`2026-68129`, :cve_nist:`2026-68130`, :cve_nist:`2026-68131`,
   :cve_nist:`2026-68135`, :cve_nist:`2026-68137`, :cve_nist:`2026-68140`, :cve_nist:`2026-68141`,
   :cve_nist:`2026-68142`, :cve_nist:`2026-68143`, :cve_nist:`2026-68144`, :cve_nist:`2026-68146`,
   :cve_nist:`2026-68147`, :cve_nist:`2026-68148`, :cve_nist:`2026-68149`, :cve_nist:`2026-68151`,
   :cve_nist:`2026-68153`, :cve_nist:`2026-68154`, :cve_nist:`2026-68155`, :cve_nist:`2026-68156`,
   :cve_nist:`2026-68157`, :cve_nist:`2026-68158`, :cve_nist:`2026-68160`, :cve_nist:`2026-68161`,
   :cve_nist:`2026-68162`, :cve_nist:`2026-68164`, :cve_nist:`2026-68165`, :cve_nist:`2026-68169`,
   :cve_nist:`2026-68175`, :cve_nist:`2026-68176`, :cve_nist:`2026-68180`, :cve_nist:`2026-68181`,
   :cve_nist:`2026-68182`, :cve_nist:`2026-68183`, :cve_nist:`2026-68184`, :cve_nist:`2026-68186`,
   :cve_nist:`2026-68187`, :cve_nist:`2026-68188`, :cve_nist:`2026-68189`, :cve_nist:`2026-68190`,
   :cve_nist:`2026-68192`, :cve_nist:`2026-68194`, :cve_nist:`2026-68195`, :cve_nist:`2026-68196`,
   :cve_nist:`2026-68197`, :cve_nist:`2026-68198`, :cve_nist:`2026-68199`, :cve_nist:`2026-68202`,
   :cve_nist:`2026-68204`, :cve_nist:`2026-68205`, :cve_nist:`2026-68206`, :cve_nist:`2026-68207`,
   :cve_nist:`2026-68209`, :cve_nist:`2026-68210`, :cve_nist:`2026-68212`, :cve_nist:`2026-68213`,
   :cve_nist:`2026-68214`, :cve_nist:`2026-68215`, :cve_nist:`2026-68216`, :cve_nist:`2026-68217`,
   :cve_nist:`2026-68218`, :cve_nist:`2026-68219`, :cve_nist:`2026-68220`, :cve_nist:`2026-68222`,
   :cve_nist:`2026-68223`, :cve_nist:`2026-68226`, :cve_nist:`2026-68227`, :cve_nist:`2026-68229`,
   :cve_nist:`2026-68231`, :cve_nist:`2026-68234`, :cve_nist:`2026-68236`, :cve_nist:`2026-68243`,
   :cve_nist:`2026-68244`, :cve_nist:`2026-68246`, :cve_nist:`2026-68248`, :cve_nist:`2026-68249`,
   :cve_nist:`2026-68250`, :cve_nist:`2026-68251`, :cve_nist:`2026-68253`, :cve_nist:`2026-68254`,
   :cve_nist:`2026-68255`, :cve_nist:`2026-68256`, :cve_nist:`2026-68259`, :cve_nist:`2026-68269`,
   :cve_nist:`2026-68271`, :cve_nist:`2026-68272`, :cve_nist:`2026-68277`, :cve_nist:`2026-68278`,
   :cve_nist:`2026-68279`, :cve_nist:`2026-68280`, :cve_nist:`2026-68284`, :cve_nist:`2026-68294`,
   :cve_nist:`2026-68297`, :cve_nist:`2026-68299`, :cve_nist:`2026-68300`, :cve_nist:`2026-68301`,
   :cve_nist:`2026-68302`, :cve_nist:`2026-68304`, :cve_nist:`2026-68306`, :cve_nist:`2026-68308`,
   :cve_nist:`2026-68309`, :cve_nist:`2026-68310`, :cve_nist:`2026-68313`, :cve_nist:`2026-68315`,
   :cve_nist:`2026-68317`, :cve_nist:`2026-68320`, :cve_nist:`2026-68322`, :cve_nist:`2026-68324`,
   :cve_nist:`2026-68325`, :cve_nist:`2026-68326`, :cve_nist:`2026-68327`, :cve_nist:`2026-68328`,
   :cve_nist:`2026-68329`, :cve_nist:`2026-68331`, :cve_nist:`2026-68333`, :cve_nist:`2026-68335`,
   :cve_nist:`2026-68336`, :cve_nist:`2026-68338`, :cve_nist:`2026-68339`, :cve_nist:`2026-68340`,
   :cve_nist:`2026-68343`, :cve_nist:`2026-68344`, :cve_nist:`2026-68348`, :cve_nist:`2026-68349`,
   :cve_nist:`2026-68350`, :cve_nist:`2026-68351`, :cve_nist:`2026-68352`, :cve_nist:`2026-68353`,
   :cve_nist:`2026-68354`, :cve_nist:`2026-68355`, :cve_nist:`2026-68357`, :cve_nist:`2026-68359`,
   :cve_nist:`2026-68360`, :cve_nist:`2026-68361`, :cve_nist:`2026-68362`, :cve_nist:`2026-68363`,
   :cve_nist:`2026-68365`, :cve_nist:`2026-68366`, :cve_nist:`2026-68367`, :cve_nist:`2026-68368`,
   :cve_nist:`2026-68369`, :cve_nist:`2026-68370`, :cve_nist:`2026-68371`, :cve_nist:`2026-68373`,
   :cve_nist:`2026-68376`, :cve_nist:`2026-68377`, :cve_nist:`2026-68381`, :cve_nist:`2026-68386`,
   :cve_nist:`2026-68388`, :cve_nist:`2026-68389`, :cve_nist:`2026-68391`, :cve_nist:`2026-68392`,
   :cve_nist:`2026-68395`, :cve_nist:`2026-68397`, :cve_nist:`2026-68398`, :cve_nist:`2026-68402`,
   :cve_nist:`2026-68403`, :cve_nist:`2026-68405`, :cve_nist:`2026-68406`, :cve_nist:`2026-68407`,
   :cve_nist:`2026-68410`, :cve_nist:`2026-68411`, :cve_nist:`2026-68413`, :cve_nist:`2026-68414`,
   :cve_nist:`2026-68416`, :cve_nist:`2026-68417`, :cve_nist:`2026-68419`, :cve_nist:`2026-68422`,
   :cve_nist:`2026-68425`, :cve_nist:`2026-68427`, :cve_nist:`2026-68428`, :cve_nist:`2026-68429`,
   :cve_nist:`2026-68430`, :cve_nist:`2026-68432`, :cve_nist:`2026-68433`, :cve_nist:`2026-68434`,
   :cve_nist:`2026-68444`, :cve_nist:`2026-68445`, :cve_nist:`2026-68446`, :cve_nist:`2026-68449`,
   :cve_nist:`2026-68450`, :cve_nist:`2026-68451`, :cve_nist:`2026-68452`, :cve_nist:`2026-68480`,
   :cve_nist:`2026-72015`, :cve_nist:`2026-72017`, :cve_nist:`2026-72023`, :cve_nist:`2026-72030`,
   :cve_nist:`2026-72040`, :cve_nist:`2026-72045`, :cve_nist:`2026-72051`, :cve_nist:`2026-72062`,
   :cve_nist:`2026-72063`, :cve_nist:`2026-72065`, :cve_nist:`2026-72069`, :cve_nist:`2026-72070`,
   :cve_nist:`2026-72071`, :cve_nist:`2026-72077`, :cve_nist:`2026-72089`, :cve_nist:`2026-72096`,
   :cve_nist:`2026-72113`, :cve_nist:`2026-72114`, :cve_nist:`2026-72115`, :cve_nist:`2026-72116`,
   :cve_nist:`2026-72117`, :cve_nist:`2026-72118`, :cve_nist:`2026-72119`, :cve_nist:`2026-72121`,
   :cve_nist:`2026-72124`, :cve_nist:`2026-72125`, :cve_nist:`2026-72132`, :cve_nist:`2026-72142`,
   :cve_nist:`2026-72144`, :cve_nist:`2026-72146`, :cve_nist:`2026-72147`, :cve_nist:`2026-72242`,
   :cve_nist:`2026-72252`, :cve_nist:`2026-72253`, :cve_nist:`2026-72254`, :cve_nist:`2026-72260`,
   :cve_nist:`2026-72273`, :cve_nist:`2026-72299`, :cve_nist:`2026-72305`, :cve_nist:`2026-72392`,
   :cve_nist:`2026-74436`, :cve_nist:`2026-74441`, :cve_nist:`2026-74443`, :cve_nist:`2026-74444`,
   :cve_nist:`2026-74445`, :cve_nist:`2026-74446`, :cve_nist:`2026-74448`, :cve_nist:`2026-74453`,
   :cve_nist:`2026-74454`, :cve_nist:`2026-74455`, :cve_nist:`2026-74456`, :cve_nist:`2026-74457`,
   :cve_nist:`2026-74458`, :cve_nist:`2026-74459`, :cve_nist:`2026-74460`, :cve_nist:`2026-74461`,
   :cve_nist:`2026-74463`, :cve_nist:`2026-74464`, :cve_nist:`2026-74465`, :cve_nist:`2026-74467`,
   :cve_nist:`2026-74468`, :cve_nist:`2026-74469`, :cve_nist:`2026-74470`, :cve_nist:`2026-74471`,
   :cve_nist:`2026-74472`, :cve_nist:`2026-74473`, :cve_nist:`2026-74475`, :cve_nist:`2026-74478`,
   :cve_nist:`2026-74480`, :cve_nist:`2026-74481`, :cve_nist:`2026-74482`, :cve_nist:`2026-74485`,
   :cve_nist:`2026-74488`, :cve_nist:`2026-74490`, :cve_nist:`2026-74492`, :cve_nist:`2026-74493`,
   :cve_nist:`2026-74495`, :cve_nist:`2026-74497`, :cve_nist:`2026-74498`, :cve_nist:`2026-74499`,
   :cve_nist:`2026-74501`, :cve_nist:`2026-74502`, :cve_nist:`2026-74505`, :cve_nist:`2026-74507`,
   :cve_nist:`2026-74508`, :cve_nist:`2026-74510`, :cve_nist:`2026-74512`, :cve_nist:`2026-74515`,
   :cve_nist:`2026-74516`, :cve_nist:`2026-74518`, :cve_nist:`2026-74519`, :cve_nist:`2026-74522`,
   :cve_nist:`2026-74523`, :cve_nist:`2026-74525`, :cve_nist:`2026-74531`, :cve_nist:`2026-74532`,
   :cve_nist:`2026-74540`, :cve_nist:`2026-74541`, :cve_nist:`2026-74546`, :cve_nist:`2026-74547`,
   :cve_nist:`2026-74548`, :cve_nist:`2026-74549`, :cve_nist:`2026-74550`, :cve_nist:`2026-74551`,
   :cve_nist:`2026-74552`, :cve_nist:`2026-74553`, :cve_nist:`2026-74555`, :cve_nist:`2026-74556`,
   :cve_nist:`2026-74557`, :cve_nist:`2026-74563`, :cve_nist:`2026-74564`, :cve_nist:`2026-74566`,
   :cve_nist:`2026-74567`, :cve_nist:`2026-74569`, :cve_nist:`2026-74572`, :cve_nist:`2026-74574`,
   :cve_nist:`2026-74575`, :cve_nist:`2026-74577`, :cve_nist:`2026-74579`, :cve_nist:`2026-74581`,
   :cve_nist:`2026-80678`, :cve_nist:`2026-80679`, :cve_nist:`2026-80680`, :cve_nist:`2026-80681`,
   :cve_nist:`2026-80684`, :cve_nist:`2026-80691`, :cve_nist:`2026-80700`, :cve_nist:`2026-80702`,
   :cve_nist:`2026-80703`, :cve_nist:`2026-80706`, :cve_nist:`2026-80707`, :cve_nist:`2026-80709`,
   :cve_nist:`2026-80710`, :cve_nist:`2026-80714`, :cve_nist:`2026-80716`, :cve_nist:`2026-80717`,
   :cve_nist:`2026-80718`, :cve_nist:`2026-80722`, :cve_nist:`2026-80887`, :cve_nist:`2026-80888`,
   :cve_nist:`2026-80889`, :cve_nist:`2026-80890`, :cve_nist:`2026-80891`, :cve_nist:`2026-80892`,
   :cve_nist:`2026-80893`, :cve_nist:`2026-80901` and :cve_nist:`2026-80902`
-  linux-yocto/6.6: Ignore :cve_nist:`2022-4994`, :cve_nist:`2023-52485`, :cve_nist:`2023-52585`,
   :cve_nist:`2025-68313`, :cve_nist:`2025-71289`, :cve_nist:`2026-17523`, :cve_nist:`2026-23459`,
   :cve_nist:`2026-31502`, :cve_nist:`2026-43119`, :cve_nist:`2026-43216`, :cve_nist:`2026-43456`,
   :cve_nist:`2026-53027`, :cve_nist:`2026-53332`, :cve_nist:`2026-53392`, :cve_nist:`2026-53393`,
   :cve_nist:`2026-53399`, :cve_nist:`2026-53400`, :cve_nist:`2026-53402`, :cve_nist:`2026-63806`,
   :cve_nist:`2026-63810`, :cve_nist:`2026-63815`, :cve_nist:`2026-63816`, :cve_nist:`2026-63818`,
   :cve_nist:`2026-63829`, :cve_nist:`2026-64187`, :cve_nist:`2026-64189`, :cve_nist:`2026-64206`,
   :cve_nist:`2026-64208`, :cve_nist:`2026-64209`, :cve_nist:`2026-64211`, :cve_nist:`2026-64214`,
   :cve_nist:`2026-64215`, :cve_nist:`2026-64216`, :cve_nist:`2026-64217`, :cve_nist:`2026-64218`,
   :cve_nist:`2026-64219`, :cve_nist:`2026-64220`, :cve_nist:`2026-64221`, :cve_nist:`2026-64222`,
   :cve_nist:`2026-64223`, :cve_nist:`2026-64224`, :cve_nist:`2026-64225`, :cve_nist:`2026-64226`,
   :cve_nist:`2026-64227`, :cve_nist:`2026-64228`, :cve_nist:`2026-64229`, :cve_nist:`2026-64230`,
   :cve_nist:`2026-64231`, :cve_nist:`2026-64232`, :cve_nist:`2026-64233`, :cve_nist:`2026-64234`,
   :cve_nist:`2026-64235`, :cve_nist:`2026-64236`, :cve_nist:`2026-64237`, :cve_nist:`2026-64238`,
   :cve_nist:`2026-64239`, :cve_nist:`2026-64240`, :cve_nist:`2026-64242`, :cve_nist:`2026-64243`,
   :cve_nist:`2026-64244`, :cve_nist:`2026-64245`, :cve_nist:`2026-64246`, :cve_nist:`2026-64247`,
   :cve_nist:`2026-64248`, :cve_nist:`2026-64249`, :cve_nist:`2026-64250`, :cve_nist:`2026-64251`,
   :cve_nist:`2026-64252`, :cve_nist:`2026-64253`, :cve_nist:`2026-64254`, :cve_nist:`2026-64256`,
   :cve_nist:`2026-64257`, :cve_nist:`2026-64258`, :cve_nist:`2026-64259`, :cve_nist:`2026-64260`,
   :cve_nist:`2026-64261`, :cve_nist:`2026-64262`, :cve_nist:`2026-64263`, :cve_nist:`2026-64264`,
   :cve_nist:`2026-64265`, :cve_nist:`2026-64266`, :cve_nist:`2026-64267`, :cve_nist:`2026-64268`,
   :cve_nist:`2026-64269`, :cve_nist:`2026-64270`, :cve_nist:`2026-64271`, :cve_nist:`2026-64272`,
   :cve_nist:`2026-64273`, :cve_nist:`2026-64274`, :cve_nist:`2026-64275`, :cve_nist:`2026-64276`,
   :cve_nist:`2026-64277`, :cve_nist:`2026-64278`, :cve_nist:`2026-64279`, :cve_nist:`2026-64281`,
   :cve_nist:`2026-64282`, :cve_nist:`2026-64283`, :cve_nist:`2026-64284`, :cve_nist:`2026-64285`,
   :cve_nist:`2026-64286`, :cve_nist:`2026-64287`, :cve_nist:`2026-64288`, :cve_nist:`2026-64289`,
   :cve_nist:`2026-64290`, :cve_nist:`2026-64291`, :cve_nist:`2026-64292`, :cve_nist:`2026-64293`,
   :cve_nist:`2026-64294`, :cve_nist:`2026-64295`, :cve_nist:`2026-64296`, :cve_nist:`2026-64297`,
   :cve_nist:`2026-64298`, :cve_nist:`2026-64299`, :cve_nist:`2026-64300`, :cve_nist:`2026-64301`,
   :cve_nist:`2026-64302`, :cve_nist:`2026-64303`, :cve_nist:`2026-64304`, :cve_nist:`2026-64306`,
   :cve_nist:`2026-64307`, :cve_nist:`2026-64308`, :cve_nist:`2026-64309`, :cve_nist:`2026-64310`,
   :cve_nist:`2026-64311`, :cve_nist:`2026-64312`, :cve_nist:`2026-64313`, :cve_nist:`2026-64314`,
   :cve_nist:`2026-64315`, :cve_nist:`2026-64316`, :cve_nist:`2026-64317`, :cve_nist:`2026-64318`,
   :cve_nist:`2026-64319`, :cve_nist:`2026-64321`, :cve_nist:`2026-64322`, :cve_nist:`2026-64323`,
   :cve_nist:`2026-64324`, :cve_nist:`2026-64325`, :cve_nist:`2026-64326`, :cve_nist:`2026-64327`,
   :cve_nist:`2026-64328`, :cve_nist:`2026-64329`, :cve_nist:`2026-64330`, :cve_nist:`2026-64331`,
   :cve_nist:`2026-64332`, :cve_nist:`2026-64333`, :cve_nist:`2026-64334`, :cve_nist:`2026-64335`,
   :cve_nist:`2026-64336`, :cve_nist:`2026-64337`, :cve_nist:`2026-64338`, :cve_nist:`2026-64339`,
   :cve_nist:`2026-64340`, :cve_nist:`2026-64342`, :cve_nist:`2026-64343`, :cve_nist:`2026-64344`,
   :cve_nist:`2026-64345`, :cve_nist:`2026-64346`, :cve_nist:`2026-64347`, :cve_nist:`2026-64348`,
   :cve_nist:`2026-64349`, :cve_nist:`2026-64350`, :cve_nist:`2026-64351`, :cve_nist:`2026-64352`,
   :cve_nist:`2026-64353`, :cve_nist:`2026-64354`, :cve_nist:`2026-64355`, :cve_nist:`2026-64356`,
   :cve_nist:`2026-64357`, :cve_nist:`2026-64358`, :cve_nist:`2026-64359`, :cve_nist:`2026-64360`,
   :cve_nist:`2026-64361`, :cve_nist:`2026-64362`, :cve_nist:`2026-64363`, :cve_nist:`2026-64364`,
   :cve_nist:`2026-64365`, :cve_nist:`2026-64366`, :cve_nist:`2026-64367`, :cve_nist:`2026-64368`,
   :cve_nist:`2026-64369`, :cve_nist:`2026-64370`, :cve_nist:`2026-64371`, :cve_nist:`2026-64372`,
   :cve_nist:`2026-64373`, :cve_nist:`2026-64374`, :cve_nist:`2026-64375`, :cve_nist:`2026-64376`,
   :cve_nist:`2026-64378`, :cve_nist:`2026-64379`, :cve_nist:`2026-64380`, :cve_nist:`2026-64381`,
   :cve_nist:`2026-64382`, :cve_nist:`2026-64383`, :cve_nist:`2026-64384`, :cve_nist:`2026-64385`,
   :cve_nist:`2026-64386`, :cve_nist:`2026-64387`, :cve_nist:`2026-64390`, :cve_nist:`2026-64392`,
   :cve_nist:`2026-64393`, :cve_nist:`2026-64394`, :cve_nist:`2026-64395`, :cve_nist:`2026-64396`,
   :cve_nist:`2026-64397`, :cve_nist:`2026-64398`, :cve_nist:`2026-64399`, :cve_nist:`2026-64401`,
   :cve_nist:`2026-64402`, :cve_nist:`2026-64403`, :cve_nist:`2026-64404`, :cve_nist:`2026-64405`,
   :cve_nist:`2026-64406`, :cve_nist:`2026-64407`, :cve_nist:`2026-64408`, :cve_nist:`2026-64409`,
   :cve_nist:`2026-64410`, :cve_nist:`2026-64411`, :cve_nist:`2026-64412`, :cve_nist:`2026-64413`,
   :cve_nist:`2026-64414`, :cve_nist:`2026-64415`, :cve_nist:`2026-64416`, :cve_nist:`2026-64417`,
   :cve_nist:`2026-64418`, :cve_nist:`2026-64419`, :cve_nist:`2026-64420`, :cve_nist:`2026-64421`,
   :cve_nist:`2026-64422`, :cve_nist:`2026-64423`, :cve_nist:`2026-64425`, :cve_nist:`2026-64426`,
   :cve_nist:`2026-64427`, :cve_nist:`2026-64428`, :cve_nist:`2026-64429`, :cve_nist:`2026-64430`,
   :cve_nist:`2026-64431`, :cve_nist:`2026-64432`, :cve_nist:`2026-64433`, :cve_nist:`2026-64434`,
   :cve_nist:`2026-64435`, :cve_nist:`2026-64436`, :cve_nist:`2026-64437`, :cve_nist:`2026-64438`,
   :cve_nist:`2026-64439`, :cve_nist:`2026-64440`, :cve_nist:`2026-64441`, :cve_nist:`2026-64442`,
   :cve_nist:`2026-64443`, :cve_nist:`2026-64444`, :cve_nist:`2026-64445`, :cve_nist:`2026-64446`,
   :cve_nist:`2026-64447`, :cve_nist:`2026-64448`, :cve_nist:`2026-64449`, :cve_nist:`2026-64450`,
   :cve_nist:`2026-64451`, :cve_nist:`2026-64452`, :cve_nist:`2026-64453`, :cve_nist:`2026-64454`,
   :cve_nist:`2026-64455`, :cve_nist:`2026-64456`, :cve_nist:`2026-64457`, :cve_nist:`2026-64458`,
   :cve_nist:`2026-64459`, :cve_nist:`2026-64460`, :cve_nist:`2026-64461`, :cve_nist:`2026-64462`,
   :cve_nist:`2026-64463`, :cve_nist:`2026-64464`, :cve_nist:`2026-64465`, :cve_nist:`2026-64466`,
   :cve_nist:`2026-64467`, :cve_nist:`2026-64468`, :cve_nist:`2026-64469`, :cve_nist:`2026-64470`,
   :cve_nist:`2026-64471`, :cve_nist:`2026-64472`, :cve_nist:`2026-64473`, :cve_nist:`2026-64474`,
   :cve_nist:`2026-64475`, :cve_nist:`2026-64476`, :cve_nist:`2026-64477`, :cve_nist:`2026-64478`,
   :cve_nist:`2026-64479`, :cve_nist:`2026-64480`, :cve_nist:`2026-64482`, :cve_nist:`2026-64483`,
   :cve_nist:`2026-64484`, :cve_nist:`2026-64485`, :cve_nist:`2026-64486`, :cve_nist:`2026-64487`,
   :cve_nist:`2026-64488`, :cve_nist:`2026-64489`, :cve_nist:`2026-64490`, :cve_nist:`2026-64491`,
   :cve_nist:`2026-64492`, :cve_nist:`2026-64493`, :cve_nist:`2026-64494`, :cve_nist:`2026-64495`,
   :cve_nist:`2026-64496`, :cve_nist:`2026-64497`, :cve_nist:`2026-64498`, :cve_nist:`2026-64499`,
   :cve_nist:`2026-64500`, :cve_nist:`2026-64501`, :cve_nist:`2026-64502`, :cve_nist:`2026-64503`,
   :cve_nist:`2026-64504`, :cve_nist:`2026-64505`, :cve_nist:`2026-64506`, :cve_nist:`2026-64507`,
   :cve_nist:`2026-64508`, :cve_nist:`2026-64509`, :cve_nist:`2026-64510`, :cve_nist:`2026-64511`,
   :cve_nist:`2026-64512`, :cve_nist:`2026-64514`, :cve_nist:`2026-64515`, :cve_nist:`2026-64516`,
   :cve_nist:`2026-64517`, :cve_nist:`2026-64518`, :cve_nist:`2026-64519`, :cve_nist:`2026-64520`,
   :cve_nist:`2026-64521`, :cve_nist:`2026-64522`, :cve_nist:`2026-64524`, :cve_nist:`2026-64525`,
   :cve_nist:`2026-64526`, :cve_nist:`2026-64527`, :cve_nist:`2026-64528`, :cve_nist:`2026-64529`,
   :cve_nist:`2026-64530`, :cve_nist:`2026-64531`, :cve_nist:`2026-64532`, :cve_nist:`2026-64533`,
   :cve_nist:`2026-64534`, :cve_nist:`2026-64535`, :cve_nist:`2026-64536`, :cve_nist:`2026-64537`,
   :cve_nist:`2026-64538`, :cve_nist:`2026-64540`, :cve_nist:`2026-64541`, :cve_nist:`2026-64543`,
   :cve_nist:`2026-64544`, :cve_nist:`2026-64545`, :cve_nist:`2026-64546`, :cve_nist:`2026-64547`,
   :cve_nist:`2026-64548`, :cve_nist:`2026-64549`, :cve_nist:`2026-64550`, :cve_nist:`2026-64551`,
   :cve_nist:`2026-64552`, :cve_nist:`2026-64553`, :cve_nist:`2026-64554`, :cve_nist:`2026-64555`,
   :cve_nist:`2026-64556`, :cve_nist:`2026-64557`, :cve_nist:`2026-64558`, :cve_nist:`2026-64559`,
   :cve_nist:`2026-64560`, :cve_nist:`2026-64566`, :cve_nist:`2026-64568`, :cve_nist:`2026-64570`,
   :cve_nist:`2026-64575`, :cve_nist:`2026-64582`, :cve_nist:`2026-64585`, :cve_nist:`2026-64587`,
   :cve_nist:`2026-64588`, :cve_nist:`2026-64589`, :cve_nist:`2026-64591`, :cve_nist:`2026-64592`,
   :cve_nist:`2026-64593`, :cve_nist:`2026-64594`, :cve_nist:`2026-64595`, :cve_nist:`2026-64596`,
   :cve_nist:`2026-64597`, :cve_nist:`2026-64598`, :cve_nist:`2026-64599`, :cve_nist:`2026-64600`,
   :cve_nist:`2026-64601`, :cve_nist:`2026-64602`, :cve_nist:`2026-64603`, :cve_nist:`2026-64604`,
   :cve_nist:`2026-68084`, :cve_nist:`2026-68085`, :cve_nist:`2026-68087`, :cve_nist:`2026-68088`,
   :cve_nist:`2026-68089`, :cve_nist:`2026-68090`, :cve_nist:`2026-68091`, :cve_nist:`2026-68092`,
   :cve_nist:`2026-68094`, :cve_nist:`2026-68095`, :cve_nist:`2026-68103`, :cve_nist:`2026-68105`,
   :cve_nist:`2026-68109`, :cve_nist:`2026-68113`, :cve_nist:`2026-68114`, :cve_nist:`2026-68119`,
   :cve_nist:`2026-68120`, :cve_nist:`2026-68122`, :cve_nist:`2026-68128`, :cve_nist:`2026-68133`,
   :cve_nist:`2026-68134`, :cve_nist:`2026-68139`, :cve_nist:`2026-68150`, :cve_nist:`2026-68163`,
   :cve_nist:`2026-68167`, :cve_nist:`2026-68168`, :cve_nist:`2026-68170`, :cve_nist:`2026-68172`,
   :cve_nist:`2026-68173`, :cve_nist:`2026-68174`, :cve_nist:`2026-68177`, :cve_nist:`2026-68178`,
   :cve_nist:`2026-68179`, :cve_nist:`2026-68191`, :cve_nist:`2026-68193`, :cve_nist:`2026-68200`,
   :cve_nist:`2026-68201`, :cve_nist:`2026-68208`, :cve_nist:`2026-68211`, :cve_nist:`2026-68221`,
   :cve_nist:`2026-68224`, :cve_nist:`2026-68225`, :cve_nist:`2026-68228`, :cve_nist:`2026-68230`,
   :cve_nist:`2026-68232`, :cve_nist:`2026-68235`, :cve_nist:`2026-68237`, :cve_nist:`2026-68239`,
   :cve_nist:`2026-68240`, :cve_nist:`2026-68245`, :cve_nist:`2026-68252`, :cve_nist:`2026-68257`,
   :cve_nist:`2026-68260`, :cve_nist:`2026-68261`, :cve_nist:`2026-68262`, :cve_nist:`2026-68263`,
   :cve_nist:`2026-68264`, :cve_nist:`2026-68265`, :cve_nist:`2026-68266`, :cve_nist:`2026-68267`,
   :cve_nist:`2026-68268`, :cve_nist:`2026-68270`, :cve_nist:`2026-68274`, :cve_nist:`2026-68275`,
   :cve_nist:`2026-68276`, :cve_nist:`2026-68281`, :cve_nist:`2026-68282`, :cve_nist:`2026-68283`,
   :cve_nist:`2026-68285`, :cve_nist:`2026-68290`, :cve_nist:`2026-68291`, :cve_nist:`2026-68292`,
   :cve_nist:`2026-68295`, :cve_nist:`2026-68296`, :cve_nist:`2026-68298`, :cve_nist:`2026-68305`,
   :cve_nist:`2026-68307`, :cve_nist:`2026-68311`, :cve_nist:`2026-68314`, :cve_nist:`2026-68316`,
   :cve_nist:`2026-68319`, :cve_nist:`2026-68321`, :cve_nist:`2026-68330`, :cve_nist:`2026-68332`,
   :cve_nist:`2026-68334`, :cve_nist:`2026-68341`, :cve_nist:`2026-68342`, :cve_nist:`2026-68345`,
   :cve_nist:`2026-68346`, :cve_nist:`2026-68347`, :cve_nist:`2026-68356`, :cve_nist:`2026-68358`,
   :cve_nist:`2026-68364`, :cve_nist:`2026-68372`, :cve_nist:`2026-68374`, :cve_nist:`2026-68375`,
   :cve_nist:`2026-68378`, :cve_nist:`2026-68379`, :cve_nist:`2026-68380`, :cve_nist:`2026-68382`,
   :cve_nist:`2026-68383`, :cve_nist:`2026-68384`, :cve_nist:`2026-68385`, :cve_nist:`2026-68387`,
   :cve_nist:`2026-68390`, :cve_nist:`2026-68393`, :cve_nist:`2026-68394`, :cve_nist:`2026-68400`,
   :cve_nist:`2026-68408`, :cve_nist:`2026-68415`, :cve_nist:`2026-68420`, :cve_nist:`2026-68421`,
   :cve_nist:`2026-68423`, :cve_nist:`2026-68424`, :cve_nist:`2026-68436`, :cve_nist:`2026-68437`,
   :cve_nist:`2026-68438`, :cve_nist:`2026-68439`, :cve_nist:`2026-68440`, :cve_nist:`2026-68442`,
   :cve_nist:`2026-68443`, :cve_nist:`2026-68454`, :cve_nist:`2026-68455`, :cve_nist:`2026-68456`,
   :cve_nist:`2026-68457`, :cve_nist:`2026-68458`, :cve_nist:`2026-68459`, :cve_nist:`2026-68460`,
   :cve_nist:`2026-68461`, :cve_nist:`2026-68462`, :cve_nist:`2026-68463`, :cve_nist:`2026-68464`,
   :cve_nist:`2026-68465`, :cve_nist:`2026-68466`, :cve_nist:`2026-68467`, :cve_nist:`2026-68468`,
   :cve_nist:`2026-68469`, :cve_nist:`2026-68472`, :cve_nist:`2026-68473`, :cve_nist:`2026-68474`,
   :cve_nist:`2026-68475`, :cve_nist:`2026-68476`, :cve_nist:`2026-68477`, :cve_nist:`2026-68478`,
   :cve_nist:`2026-68479`, :cve_nist:`2026-72003`, :cve_nist:`2026-72004`, :cve_nist:`2026-72005`,
   :cve_nist:`2026-72006`, :cve_nist:`2026-72007`, :cve_nist:`2026-72008`, :cve_nist:`2026-72010`,
   :cve_nist:`2026-72011`, :cve_nist:`2026-72012`, :cve_nist:`2026-72013`, :cve_nist:`2026-72014`,
   :cve_nist:`2026-72016`, :cve_nist:`2026-72018`, :cve_nist:`2026-72019`, :cve_nist:`2026-72020`,
   :cve_nist:`2026-72021`, :cve_nist:`2026-72022`, :cve_nist:`2026-72024`, :cve_nist:`2026-72025`,
   :cve_nist:`2026-72026`, :cve_nist:`2026-72027`, :cve_nist:`2026-72028`, :cve_nist:`2026-72029`,
   :cve_nist:`2026-72031`, :cve_nist:`2026-72032`, :cve_nist:`2026-72033`, :cve_nist:`2026-72034`,
   :cve_nist:`2026-72035`, :cve_nist:`2026-72036`, :cve_nist:`2026-72037`, :cve_nist:`2026-72038`,
   :cve_nist:`2026-72039`, :cve_nist:`2026-72041`, :cve_nist:`2026-72043`, :cve_nist:`2026-72046`,
   :cve_nist:`2026-72047`, :cve_nist:`2026-72048`, :cve_nist:`2026-72049`, :cve_nist:`2026-72050`,
   :cve_nist:`2026-72052`, :cve_nist:`2026-72053`, :cve_nist:`2026-72054`, :cve_nist:`2026-72055`,
   :cve_nist:`2026-72056`, :cve_nist:`2026-72057`, :cve_nist:`2026-72058`, :cve_nist:`2026-72059`,
   :cve_nist:`2026-72060`, :cve_nist:`2026-72061`, :cve_nist:`2026-72064`, :cve_nist:`2026-72066`,
   :cve_nist:`2026-72067`, :cve_nist:`2026-72068`, :cve_nist:`2026-72072`, :cve_nist:`2026-72074`,
   :cve_nist:`2026-72075`, :cve_nist:`2026-72076`, :cve_nist:`2026-72078`, :cve_nist:`2026-72079`,
   :cve_nist:`2026-72080`, :cve_nist:`2026-72081`, :cve_nist:`2026-72082`, :cve_nist:`2026-72083`,
   :cve_nist:`2026-72084`, :cve_nist:`2026-72085`, :cve_nist:`2026-72086`, :cve_nist:`2026-72087`,
   :cve_nist:`2026-72088`, :cve_nist:`2026-72090`, :cve_nist:`2026-72091`, :cve_nist:`2026-72092`,
   :cve_nist:`2026-72093`, :cve_nist:`2026-72094`, :cve_nist:`2026-72095`, :cve_nist:`2026-72097`,
   :cve_nist:`2026-72099`, :cve_nist:`2026-72100`, :cve_nist:`2026-72101`, :cve_nist:`2026-72102`,
   :cve_nist:`2026-72103`, :cve_nist:`2026-72104`, :cve_nist:`2026-72105`, :cve_nist:`2026-72106`,
   :cve_nist:`2026-72107`, :cve_nist:`2026-72108`, :cve_nist:`2026-72109`, :cve_nist:`2026-72110`,
   :cve_nist:`2026-72111`, :cve_nist:`2026-72112`, :cve_nist:`2026-72120`, :cve_nist:`2026-72122`,
   :cve_nist:`2026-72123`, :cve_nist:`2026-72126`, :cve_nist:`2026-72127`, :cve_nist:`2026-72128`,
   :cve_nist:`2026-72129`, :cve_nist:`2026-72131`, :cve_nist:`2026-72133`, :cve_nist:`2026-72134`,
   :cve_nist:`2026-72135`, :cve_nist:`2026-72136`, :cve_nist:`2026-72137`, :cve_nist:`2026-72138`,
   :cve_nist:`2026-72139`, :cve_nist:`2026-72140`, :cve_nist:`2026-72141`, :cve_nist:`2026-72143`,
   :cve_nist:`2026-72145`, :cve_nist:`2026-72148`, :cve_nist:`2026-72149`, :cve_nist:`2026-72150`,
   :cve_nist:`2026-72151`, :cve_nist:`2026-72152`, :cve_nist:`2026-72153`, :cve_nist:`2026-72154`,
   :cve_nist:`2026-72155`, :cve_nist:`2026-72156`, :cve_nist:`2026-72157`, :cve_nist:`2026-72158`,
   :cve_nist:`2026-72159`, :cve_nist:`2026-72160`, :cve_nist:`2026-72161`, :cve_nist:`2026-72163`,
   :cve_nist:`2026-72164`, :cve_nist:`2026-72165`, :cve_nist:`2026-72166`, :cve_nist:`2026-72167`,
   :cve_nist:`2026-72169`, :cve_nist:`2026-72170`, :cve_nist:`2026-72171`, :cve_nist:`2026-72172`,
   :cve_nist:`2026-72173`, :cve_nist:`2026-72174`, :cve_nist:`2026-72175`, :cve_nist:`2026-72176`,
   :cve_nist:`2026-72177`, :cve_nist:`2026-72178`, :cve_nist:`2026-72179`, :cve_nist:`2026-72180`,
   :cve_nist:`2026-72181`, :cve_nist:`2026-72182`, :cve_nist:`2026-72183`, :cve_nist:`2026-72184`,
   :cve_nist:`2026-72185`, :cve_nist:`2026-72186`, :cve_nist:`2026-72187`, :cve_nist:`2026-72188`,
   :cve_nist:`2026-72189`, :cve_nist:`2026-72190`, :cve_nist:`2026-72191`, :cve_nist:`2026-72192`,
   :cve_nist:`2026-72193`, :cve_nist:`2026-72194`, :cve_nist:`2026-72195`, :cve_nist:`2026-72196`,
   :cve_nist:`2026-72197`, :cve_nist:`2026-72199`, :cve_nist:`2026-72201`, :cve_nist:`2026-72202`,
   :cve_nist:`2026-72204`, :cve_nist:`2026-72205`, :cve_nist:`2026-72206`, :cve_nist:`2026-72207`,
   :cve_nist:`2026-72208`, :cve_nist:`2026-72209`, :cve_nist:`2026-72210`, :cve_nist:`2026-72211`,
   :cve_nist:`2026-72212`, :cve_nist:`2026-72214`, :cve_nist:`2026-72215`, :cve_nist:`2026-72216`,
   :cve_nist:`2026-72217`, :cve_nist:`2026-72218`, :cve_nist:`2026-72219`, :cve_nist:`2026-72220`,
   :cve_nist:`2026-72221`, :cve_nist:`2026-72222`, :cve_nist:`2026-72223`, :cve_nist:`2026-72224`,
   :cve_nist:`2026-72225`, :cve_nist:`2026-72226`, :cve_nist:`2026-72227`, :cve_nist:`2026-72228`,
   :cve_nist:`2026-72229`, :cve_nist:`2026-72230`, :cve_nist:`2026-72231`, :cve_nist:`2026-72232`,
   :cve_nist:`2026-72233`, :cve_nist:`2026-72234`, :cve_nist:`2026-72235`, :cve_nist:`2026-72236`,
   :cve_nist:`2026-72237`, :cve_nist:`2026-72238`, :cve_nist:`2026-72239`, :cve_nist:`2026-72240`,
   :cve_nist:`2026-72241`, :cve_nist:`2026-72243`, :cve_nist:`2026-72244`, :cve_nist:`2026-72245`,
   :cve_nist:`2026-72247`, :cve_nist:`2026-72248`, :cve_nist:`2026-72249`, :cve_nist:`2026-72250`,
   :cve_nist:`2026-72251`, :cve_nist:`2026-72255`, :cve_nist:`2026-72256`, :cve_nist:`2026-72257`,
   :cve_nist:`2026-72258`, :cve_nist:`2026-72259`, :cve_nist:`2026-72261`, :cve_nist:`2026-72262`,
   :cve_nist:`2026-72263`, :cve_nist:`2026-72264`, :cve_nist:`2026-72265`, :cve_nist:`2026-72266`,
   :cve_nist:`2026-72267`, :cve_nist:`2026-72268`, :cve_nist:`2026-72269`, :cve_nist:`2026-72270`,
   :cve_nist:`2026-72271`, :cve_nist:`2026-72272`, :cve_nist:`2026-72274`, :cve_nist:`2026-72275`,
   :cve_nist:`2026-72276`, :cve_nist:`2026-72277`, :cve_nist:`2026-72278`, :cve_nist:`2026-72279`,
   :cve_nist:`2026-72280`, :cve_nist:`2026-72281`, :cve_nist:`2026-72282`, :cve_nist:`2026-72283`,
   :cve_nist:`2026-72284`, :cve_nist:`2026-72285`, :cve_nist:`2026-72286`, :cve_nist:`2026-72287`,
   :cve_nist:`2026-72289`, :cve_nist:`2026-72290`, :cve_nist:`2026-72291`, :cve_nist:`2026-72292`,
   :cve_nist:`2026-72293`, :cve_nist:`2026-72294`, :cve_nist:`2026-72295`, :cve_nist:`2026-72296`,
   :cve_nist:`2026-72297`, :cve_nist:`2026-72298`, :cve_nist:`2026-72300`, :cve_nist:`2026-72301`,
   :cve_nist:`2026-72302`, :cve_nist:`2026-72303`, :cve_nist:`2026-72304`, :cve_nist:`2026-72306`,
   :cve_nist:`2026-72307`, :cve_nist:`2026-72308`, :cve_nist:`2026-72309`, :cve_nist:`2026-72310`,
   :cve_nist:`2026-72311`, :cve_nist:`2026-72312`, :cve_nist:`2026-72313`, :cve_nist:`2026-72314`,
   :cve_nist:`2026-72316`, :cve_nist:`2026-72317`, :cve_nist:`2026-72318`, :cve_nist:`2026-72319`,
   :cve_nist:`2026-72322`, :cve_nist:`2026-72323`, :cve_nist:`2026-72326`, :cve_nist:`2026-72327`,
   :cve_nist:`2026-72328`, :cve_nist:`2026-72330`, :cve_nist:`2026-72331`, :cve_nist:`2026-72332`,
   :cve_nist:`2026-72333`, :cve_nist:`2026-72335`, :cve_nist:`2026-72336`, :cve_nist:`2026-72338`,
   :cve_nist:`2026-72339`, :cve_nist:`2026-72340`, :cve_nist:`2026-72342`, :cve_nist:`2026-72343`,
   :cve_nist:`2026-72344`, :cve_nist:`2026-72345`, :cve_nist:`2026-72346`, :cve_nist:`2026-72347`,
   :cve_nist:`2026-72348`, :cve_nist:`2026-72349`, :cve_nist:`2026-72350`, :cve_nist:`2026-72351`,
   :cve_nist:`2026-72353`, :cve_nist:`2026-72354`, :cve_nist:`2026-72355`, :cve_nist:`2026-72356`,
   :cve_nist:`2026-72357`, :cve_nist:`2026-72358`, :cve_nist:`2026-72359`, :cve_nist:`2026-72360`,
   :cve_nist:`2026-72361`, :cve_nist:`2026-72362`, :cve_nist:`2026-72363`, :cve_nist:`2026-72364`,
   :cve_nist:`2026-72365`, :cve_nist:`2026-72366`, :cve_nist:`2026-72367`, :cve_nist:`2026-72368`,
   :cve_nist:`2026-72369`, :cve_nist:`2026-72370`, :cve_nist:`2026-72371`, :cve_nist:`2026-72372`,
   :cve_nist:`2026-72374`, :cve_nist:`2026-72377`, :cve_nist:`2026-72378`, :cve_nist:`2026-72379`,
   :cve_nist:`2026-72381`, :cve_nist:`2026-72382`, :cve_nist:`2026-72384`, :cve_nist:`2026-72385`,
   :cve_nist:`2026-72386`, :cve_nist:`2026-72387`, :cve_nist:`2026-72388`, :cve_nist:`2026-72389`,
   :cve_nist:`2026-72390`, :cve_nist:`2026-72391`, :cve_nist:`2026-72393`, :cve_nist:`2026-72394`,
   :cve_nist:`2026-72395`, :cve_nist:`2026-72396`, :cve_nist:`2026-72399`, :cve_nist:`2026-72400`,
   :cve_nist:`2026-72401`, :cve_nist:`2026-72403`, :cve_nist:`2026-72406`, :cve_nist:`2026-72407`,
   :cve_nist:`2026-72408`, :cve_nist:`2026-72409`, :cve_nist:`2026-72410`, :cve_nist:`2026-72411`,
   :cve_nist:`2026-72412`, :cve_nist:`2026-72414`, :cve_nist:`2026-72415`, :cve_nist:`2026-72416`,
   :cve_nist:`2026-72417`, :cve_nist:`2026-72418`, :cve_nist:`2026-72419`, :cve_nist:`2026-72421`,
   :cve_nist:`2026-72422`, :cve_nist:`2026-72424`, :cve_nist:`2026-72425`, :cve_nist:`2026-72426`,
   :cve_nist:`2026-72427`, :cve_nist:`2026-72428`, :cve_nist:`2026-72429`, :cve_nist:`2026-72430`,
   :cve_nist:`2026-72431`, :cve_nist:`2026-72432`, :cve_nist:`2026-72433`, :cve_nist:`2026-72435`,
   :cve_nist:`2026-72436`, :cve_nist:`2026-72437`, :cve_nist:`2026-72441`, :cve_nist:`2026-72442`,
   :cve_nist:`2026-72443`, :cve_nist:`2026-72444`, :cve_nist:`2026-72445`, :cve_nist:`2026-72446`,
   :cve_nist:`2026-72447`, :cve_nist:`2026-72448`, :cve_nist:`2026-72449`, :cve_nist:`2026-72450`,
   :cve_nist:`2026-72451`, :cve_nist:`2026-72452`, :cve_nist:`2026-72453`, :cve_nist:`2026-72455`,
   :cve_nist:`2026-72456`, :cve_nist:`2026-72457`, :cve_nist:`2026-72458`, :cve_nist:`2026-72459`,
   :cve_nist:`2026-72460`, :cve_nist:`2026-72461`, :cve_nist:`2026-72462`, :cve_nist:`2026-72463`,
   :cve_nist:`2026-72464`, :cve_nist:`2026-72465`, :cve_nist:`2026-72466`, :cve_nist:`2026-72467`,
   :cve_nist:`2026-72468`, :cve_nist:`2026-72470`, :cve_nist:`2026-72471`, :cve_nist:`2026-72473`,
   :cve_nist:`2026-72474`, :cve_nist:`2026-72475`, :cve_nist:`2026-72476`, :cve_nist:`2026-72477`,
   :cve_nist:`2026-72478`, :cve_nist:`2026-72479`, :cve_nist:`2026-72480`, :cve_nist:`2026-72481`,
   :cve_nist:`2026-72482`, :cve_nist:`2026-72483`, :cve_nist:`2026-72484`, :cve_nist:`2026-72486`,
   :cve_nist:`2026-72487`, :cve_nist:`2026-72489`, :cve_nist:`2026-72490`, :cve_nist:`2026-72491`,
   :cve_nist:`2026-72492`, :cve_nist:`2026-72497`, :cve_nist:`2026-72498`, :cve_nist:`2026-72499`,
   :cve_nist:`2026-72500`, :cve_nist:`2026-72502`, :cve_nist:`2026-74255`, :cve_nist:`2026-74256`,
   :cve_nist:`2026-74259`, :cve_nist:`2026-74260`, :cve_nist:`2026-74262`, :cve_nist:`2026-74263`,
   :cve_nist:`2026-74265`, :cve_nist:`2026-74266`, :cve_nist:`2026-74267`, :cve_nist:`2026-74270`,
   :cve_nist:`2026-74271`, :cve_nist:`2026-74274`, :cve_nist:`2026-74275`, :cve_nist:`2026-74276`,
   :cve_nist:`2026-74277`, :cve_nist:`2026-74279`, :cve_nist:`2026-74280`, :cve_nist:`2026-74281`,
   :cve_nist:`2026-74282`, :cve_nist:`2026-74283`, :cve_nist:`2026-74284`, :cve_nist:`2026-74285`,
   :cve_nist:`2026-74286`, :cve_nist:`2026-74287`, :cve_nist:`2026-74288`, :cve_nist:`2026-74290`,
   :cve_nist:`2026-74292`, :cve_nist:`2026-74293`, :cve_nist:`2026-74295`, :cve_nist:`2026-74296`,
   :cve_nist:`2026-74297`, :cve_nist:`2026-74298`, :cve_nist:`2026-74299`, :cve_nist:`2026-74300`,
   :cve_nist:`2026-74301`, :cve_nist:`2026-74303`, :cve_nist:`2026-74304`, :cve_nist:`2026-74305`,
   :cve_nist:`2026-74306`, :cve_nist:`2026-74308`, :cve_nist:`2026-74309`, :cve_nist:`2026-74310`,
   :cve_nist:`2026-74311`, :cve_nist:`2026-74312`, :cve_nist:`2026-74313`, :cve_nist:`2026-74315`,
   :cve_nist:`2026-74316`, :cve_nist:`2026-74319`, :cve_nist:`2026-74320`, :cve_nist:`2026-74321`,
   :cve_nist:`2026-74322`, :cve_nist:`2026-74323`, :cve_nist:`2026-74324`, :cve_nist:`2026-74325`,
   :cve_nist:`2026-74326`, :cve_nist:`2026-74327`, :cve_nist:`2026-74328`, :cve_nist:`2026-74329`,
   :cve_nist:`2026-74330`, :cve_nist:`2026-74331`, :cve_nist:`2026-74332`, :cve_nist:`2026-74333`,
   :cve_nist:`2026-74335`, :cve_nist:`2026-74336`, :cve_nist:`2026-74339`, :cve_nist:`2026-74340`,
   :cve_nist:`2026-74341`, :cve_nist:`2026-74343`, :cve_nist:`2026-74346`, :cve_nist:`2026-74348`,
   :cve_nist:`2026-74349`, :cve_nist:`2026-74351`, :cve_nist:`2026-74352`, :cve_nist:`2026-74353`,
   :cve_nist:`2026-74354`, :cve_nist:`2026-74355`, :cve_nist:`2026-74357`, :cve_nist:`2026-74358`,
   :cve_nist:`2026-74359`, :cve_nist:`2026-74360`, :cve_nist:`2026-74361`, :cve_nist:`2026-74362`,
   :cve_nist:`2026-74363`, :cve_nist:`2026-74364`, :cve_nist:`2026-74365`, :cve_nist:`2026-74366`,
   :cve_nist:`2026-74367`, :cve_nist:`2026-74369`, :cve_nist:`2026-74370`, :cve_nist:`2026-74371`,
   :cve_nist:`2026-74372`, :cve_nist:`2026-74376`, :cve_nist:`2026-74377`, :cve_nist:`2026-74378`,
   :cve_nist:`2026-74379`, :cve_nist:`2026-74380`, :cve_nist:`2026-74381`, :cve_nist:`2026-74382`,
   :cve_nist:`2026-74383`, :cve_nist:`2026-74384`, :cve_nist:`2026-74385`, :cve_nist:`2026-74387`,
   :cve_nist:`2026-74390`, :cve_nist:`2026-74391`, :cve_nist:`2026-74392`, :cve_nist:`2026-74393`,
   :cve_nist:`2026-74394`, :cve_nist:`2026-74395`, :cve_nist:`2026-74396`, :cve_nist:`2026-74397`,
   :cve_nist:`2026-74398`, :cve_nist:`2026-74399`, :cve_nist:`2026-74400`, :cve_nist:`2026-74401`,
   :cve_nist:`2026-74402`, :cve_nist:`2026-74403`, :cve_nist:`2026-74404`, :cve_nist:`2026-74406`,
   :cve_nist:`2026-74408`, :cve_nist:`2026-74409`, :cve_nist:`2026-74410`, :cve_nist:`2026-74411`,
   :cve_nist:`2026-74412`, :cve_nist:`2026-74413`, :cve_nist:`2026-74414`, :cve_nist:`2026-74415`,
   :cve_nist:`2026-74416`, :cve_nist:`2026-74417`, :cve_nist:`2026-74418`, :cve_nist:`2026-74419`,
   :cve_nist:`2026-74420`, :cve_nist:`2026-74421`, :cve_nist:`2026-74422`, :cve_nist:`2026-74423`,
   :cve_nist:`2026-74424`, :cve_nist:`2026-74425`, :cve_nist:`2026-74426`, :cve_nist:`2026-74427`,
   :cve_nist:`2026-74428`, :cve_nist:`2026-74429`, :cve_nist:`2026-74430`, :cve_nist:`2026-74431`,
   :cve_nist:`2026-74432`, :cve_nist:`2026-74433`, :cve_nist:`2026-74434`, :cve_nist:`2026-74435`,
   :cve_nist:`2026-74437`, :cve_nist:`2026-74438`, :cve_nist:`2026-74439`, :cve_nist:`2026-74440`,
   :cve_nist:`2026-74442`, :cve_nist:`2026-74447`, :cve_nist:`2026-74449`, :cve_nist:`2026-74451`,
   :cve_nist:`2026-74452`, :cve_nist:`2026-74462`, :cve_nist:`2026-74477`, :cve_nist:`2026-74483`,
   :cve_nist:`2026-74484`, :cve_nist:`2026-74486`, :cve_nist:`2026-74489`, :cve_nist:`2026-74491`,
   :cve_nist:`2026-74500`, :cve_nist:`2026-74503`, :cve_nist:`2026-74504`, :cve_nist:`2026-74513`,
   :cve_nist:`2026-74517`, :cve_nist:`2026-74520`, :cve_nist:`2026-74524`, :cve_nist:`2026-74526`,
   :cve_nist:`2026-74528`, :cve_nist:`2026-74529`, :cve_nist:`2026-74530`, :cve_nist:`2026-74533`,
   :cve_nist:`2026-74534`, :cve_nist:`2026-74535`, :cve_nist:`2026-74538`, :cve_nist:`2026-74539`,
   :cve_nist:`2026-74542`, :cve_nist:`2026-74545`, :cve_nist:`2026-74554`, :cve_nist:`2026-74561`,
   :cve_nist:`2026-74562`, :cve_nist:`2026-74568`, :cve_nist:`2026-74570`, :cve_nist:`2026-74573`,
   :cve_nist:`2026-74576`, :cve_nist:`2026-74578`, :cve_nist:`2026-74584`, :cve_nist:`2026-74590`,
   :cve_nist:`2026-74591`, :cve_nist:`2026-74593`, :cve_nist:`2026-74596`, :cve_nist:`2026-74602`,
   :cve_nist:`2026-74617`, :cve_nist:`2026-74618`, :cve_nist:`2026-74627`, :cve_nist:`2026-74629`,
   :cve_nist:`2026-74633`, :cve_nist:`2026-74634`, :cve_nist:`2026-74639`, :cve_nist:`2026-74640`,
   :cve_nist:`2026-74643`, :cve_nist:`2026-74644`, :cve_nist:`2026-74645`, :cve_nist:`2026-74652`,
   :cve_nist:`2026-74665`, :cve_nist:`2026-74674`, :cve_nist:`2026-74681`, :cve_nist:`2026-74685`,
   :cve_nist:`2026-74686`, :cve_nist:`2026-74698`, :cve_nist:`2026-74699`, :cve_nist:`2026-74702`,
   :cve_nist:`2026-74703`, :cve_nist:`2026-74706`, :cve_nist:`2026-74707`, :cve_nist:`2026-74708`,
   :cve_nist:`2026-74709`, :cve_nist:`2026-74710`, :cve_nist:`2026-74712`, :cve_nist:`2026-74716`,
   :cve_nist:`2026-74721`, :cve_nist:`2026-74727`, :cve_nist:`2026-74728`, :cve_nist:`2026-74731`,
   :cve_nist:`2026-74738`, :cve_nist:`2026-74741`, :cve_nist:`2026-74742`, :cve_nist:`2026-74745`,
   :cve_nist:`2026-74749`, :cve_nist:`2026-74750`, :cve_nist:`2026-74751`, :cve_nist:`2026-80519`,
   :cve_nist:`2026-80520`, :cve_nist:`2026-80522`, :cve_nist:`2026-80523`, :cve_nist:`2026-80524`,
   :cve_nist:`2026-80525`, :cve_nist:`2026-80530`, :cve_nist:`2026-80531`, :cve_nist:`2026-80532`,
   :cve_nist:`2026-80533`, :cve_nist:`2026-80535`, :cve_nist:`2026-80537`, :cve_nist:`2026-80538`,
   :cve_nist:`2026-80542`, :cve_nist:`2026-80543`, :cve_nist:`2026-80544`, :cve_nist:`2026-80545`,
   :cve_nist:`2026-80546`, :cve_nist:`2026-80563`, :cve_nist:`2026-80564`, :cve_nist:`2026-80571`,
   :cve_nist:`2026-80575`, :cve_nist:`2026-80577`, :cve_nist:`2026-80582`, :cve_nist:`2026-80588`,
   :cve_nist:`2026-80591`, :cve_nist:`2026-80592`, :cve_nist:`2026-80593`, :cve_nist:`2026-80594`,
   :cve_nist:`2026-80595`, :cve_nist:`2026-80596`, :cve_nist:`2026-80597`, :cve_nist:`2026-80598`,
   :cve_nist:`2026-80599`, :cve_nist:`2026-80600`, :cve_nist:`2026-80601`, :cve_nist:`2026-80602`,
   :cve_nist:`2026-80603`, :cve_nist:`2026-80604`, :cve_nist:`2026-80605`, :cve_nist:`2026-80606`,
   :cve_nist:`2026-80608`, :cve_nist:`2026-80609`, :cve_nist:`2026-80610`, :cve_nist:`2026-80612`,
   :cve_nist:`2026-80613`, :cve_nist:`2026-80614`, :cve_nist:`2026-80615`, :cve_nist:`2026-80617`,
   :cve_nist:`2026-80618`, :cve_nist:`2026-80619`, :cve_nist:`2026-80620`, :cve_nist:`2026-80621`,
   :cve_nist:`2026-80622`, :cve_nist:`2026-80624`, :cve_nist:`2026-80625`, :cve_nist:`2026-80626`,
   :cve_nist:`2026-80627`, :cve_nist:`2026-80630`, :cve_nist:`2026-80632`, :cve_nist:`2026-80633`,
   :cve_nist:`2026-80635`, :cve_nist:`2026-80636`, :cve_nist:`2026-80638`, :cve_nist:`2026-80639`,
   :cve_nist:`2026-80640`, :cve_nist:`2026-80641`, :cve_nist:`2026-80642`, :cve_nist:`2026-80644`,
   :cve_nist:`2026-80645`, :cve_nist:`2026-80646`, :cve_nist:`2026-80647`, :cve_nist:`2026-80648`,
   :cve_nist:`2026-80649`, :cve_nist:`2026-80651`, :cve_nist:`2026-80652`, :cve_nist:`2026-80655`,
   :cve_nist:`2026-80656`, :cve_nist:`2026-80657`, :cve_nist:`2026-80658`, :cve_nist:`2026-80659`,
   :cve_nist:`2026-80660`, :cve_nist:`2026-80661`, :cve_nist:`2026-80663`, :cve_nist:`2026-80664`,
   :cve_nist:`2026-80665`, :cve_nist:`2026-80666`, :cve_nist:`2026-80667`, :cve_nist:`2026-80669`,
   :cve_nist:`2026-80672`, :cve_nist:`2026-80673`, :cve_nist:`2026-80674`, :cve_nist:`2026-80675`,
   :cve_nist:`2026-80677`, :cve_nist:`2026-80682`, :cve_nist:`2026-80683`, :cve_nist:`2026-80685`,
   :cve_nist:`2026-80687`, :cve_nist:`2026-80688`, :cve_nist:`2026-80690`, :cve_nist:`2026-80693`,
   :cve_nist:`2026-80696`, :cve_nist:`2026-80697`, :cve_nist:`2026-80699`, :cve_nist:`2026-80701`,
   :cve_nist:`2026-80705`, :cve_nist:`2026-80711`, :cve_nist:`2026-80712`, :cve_nist:`2026-80713`,
   :cve_nist:`2026-80719`, :cve_nist:`2026-80720`, :cve_nist:`2026-80721`, :cve_nist:`2026-80723`,
   :cve_nist:`2026-80724`, :cve_nist:`2026-80727`, :cve_nist:`2026-80729`, :cve_nist:`2026-80734`,
   :cve_nist:`2026-80735`, :cve_nist:`2026-80736`, :cve_nist:`2026-80740`, :cve_nist:`2026-80741`,
   :cve_nist:`2026-80745`, :cve_nist:`2026-80746`, :cve_nist:`2026-80748`, :cve_nist:`2026-80749`,
   :cve_nist:`2026-80750`, :cve_nist:`2026-80751`, :cve_nist:`2026-80753`, :cve_nist:`2026-80758`,
   :cve_nist:`2026-80759`, :cve_nist:`2026-80760`, :cve_nist:`2026-80769`, :cve_nist:`2026-80773`,
   :cve_nist:`2026-80774`, :cve_nist:`2026-80775`, :cve_nist:`2026-80776`, :cve_nist:`2026-80777`,
   :cve_nist:`2026-80778`, :cve_nist:`2026-80779`, :cve_nist:`2026-80787`, :cve_nist:`2026-80804`,
   :cve_nist:`2026-80810`, :cve_nist:`2026-80811`, :cve_nist:`2026-80813`, :cve_nist:`2026-80815`,
   :cve_nist:`2026-80816`, :cve_nist:`2026-80817`, :cve_nist:`2026-80818`, :cve_nist:`2026-80821`,
   :cve_nist:`2026-80822`, :cve_nist:`2026-80825`, :cve_nist:`2026-80835`, :cve_nist:`2026-80838`,
   :cve_nist:`2026-80839`, :cve_nist:`2026-80845`, :cve_nist:`2026-80849`, :cve_nist:`2026-80850`,
   :cve_nist:`2026-80853`, :cve_nist:`2026-80857`, :cve_nist:`2026-80858`, :cve_nist:`2026-80859`,
   :cve_nist:`2026-80860`, :cve_nist:`2026-80862`, :cve_nist:`2026-80865`, :cve_nist:`2026-80867`,
   :cve_nist:`2026-80868`, :cve_nist:`2026-80869`, :cve_nist:`2026-80870`, :cve_nist:`2026-80871`,
   :cve_nist:`2026-80873`, :cve_nist:`2026-80874`, :cve_nist:`2026-80875`, :cve_nist:`2026-80876`,
   :cve_nist:`2026-80877`, :cve_nist:`2026-80878`, :cve_nist:`2026-80879`, :cve_nist:`2026-80880`,
   :cve_nist:`2026-80881`, :cve_nist:`2026-80882`, :cve_nist:`2026-80883`, :cve_nist:`2026-80885`,
   :cve_nist:`2026-80886`, :cve_nist:`2026-80894`, :cve_nist:`2026-80895`, :cve_nist:`2026-80896`,
   :cve_nist:`2026-80897`, :cve_nist:`2026-80898`, :cve_nist:`2026-80899`, :cve_nist:`2026-80900`,
   :cve_nist:`2026-80903`, :cve_nist:`2026-80915`, :cve_nist:`2026-80919`, :cve_nist:`2026-80922`
   and :cve_nist:`2026-80924`
-  nghttp2: Ignore :cve_nist:`2026-58055`
-  p11-kit: Fix :cve_nist:`2026-13757` and :cve_nist:`2026-18938`
-  patch: Fix :cve_nist:`2026-56288` and :cve_nist:`2026-56289`
-  perl: Fix :cve_nist:`2025-40909`, :cve_nist:`2026-13221`, :cve_nist:`2026-19487` and
   :cve_nist:`2026-57432`
-  python3-cryptography: Fix :cve_nist:`2026-34073`, :cve_nist:`2026-69248` and :cve_nist:`2026-69249`
-  python3-git: Fix :cve_nist:`2026-42215`, :cve_nist:`2026-42284`, :cve_nist:`2026-44243` and
   :cve_nist:`2026-44244`
-  python3-idna: Fix :cve_nist:`2026-45409`
-  python3-lxml: Fix :cve_nist:`2026-41066`
-  python3-mako: Fix :cve_nist:`2026-41205`
-  python3-pip: Fix :cve_nist:`2026-8643` and :cve_nist:`2026-13346`
-  python3-pip: Ignore :cve_nist:`2018-20225`
-  python3-ply: Ignore :cve_nist:`2025-56005`
-  python3-pyasn1: Fix :cve_nist:`2026-30922`, :cve_nist:`2026-59884`, :cve_nist:`2026-59885` and
   :cve_nist:`2026-59886`
-  rpcbind: Fix :cve_nist:`2026-16277`
-  systemd: Fix :cve_nist:`2026-29111`
-  u-boot, u-boot-tools: Fix :cve_nist:`2026-46728`
-  util-linux: Fix :cve_nist:`2026-3184`
-  vim: Fix :cve_nist:`2026-28417` and :cve_nist:`2026-52858`
-  vim: Fix :cve_nist:`2026-55693`, :cve_nist:`2026-55892`, :cve_nist:`2026-55895`,
   :cve_nist:`2026-57452`, :cve_nist:`2026-57455`, :cve_nist:`2026-57456`, :cve_nist:`2026-59856`,
   :cve_nist:`2026-59857`, :cve_nist:`2026-59858`, :cve_nist:`2026-73072`, :cve_nist:`2026-73073`,
   :cve_nist:`2026-73074`, :cve_nist:`2026-73077` and :cve_nist:`2026-73078`
-  wget: Fix :cve_nist:`2026-58469`, :cve_nist:`2026-58470`, :cve_nist:`2026-58471` and
   :cve_nist:`2026-58472`


Fixes in Yocto-5.0.21
~~~~~~~~~~~~~~~~~~~~~

-  at-spi2-core: :term:`RDEPENDS` on gsettings-desktop-schemas
-  bitbake.conf: add :term:`TMPDIR` to GIT_CEILING_DIRECTORIES
-  bitbake: fetch/gitsm: Store the original url data to fix relative gitsm paths
-  bitbake: fetch2/gcp: treat GatewayTimeout as fetch failure
-  bitbake: tests/fetch: Drop ftp checkstatus test
-  bitbake: tests/fetch: Drop github url from testing
-  bitbake: tests/fetch: Swap bitbake github mirror for YP one
-  bitbake: tests/fetch: Swap github grpc repo for our own small test repo
-  bitbake: tests/fetch: Switch to YP mirror repos instead of github
-  bitbake: toastergui/views: Replace os.system call with improved parameter handling
-  bsp-manual/bsp.rst: fix a "local.conf" -> "layer.conf" typo
-  build-appliance-image: Update to scarthgap head revision
-  ca-certificates: upgrade to 20260816
-  curl: Fix CVE-2025-10148.patch for websockets on 8.7.1
-  dev-manual: correct :term:`OVERRIDES` to :term:`FILESOVERRIDES` for :term:`FILESPATH`
-  dev-manual: drop link to out-of-date wiki page
-  dev-manual: quote the values in two recipe examples
-  documentation: use "https" protocol for git.yoctoproject.org
-  expat: upgrade 2.6.4 -> 2.8.3 (exception security update)
-  gcc: Upgrade to GCC 13.5
-  golang: fix homepage
-  improve_kernel_cve_report: fix backported-patch check
-  improve_kernel_cve_report: validate that cve details field exists
-  libevent: merge inherit statements
-  libevent: upgrade to 2.1.13
-  libevent: use libtool to install test binaries
-  linux-yocto/6.6: fix tiny build
-  linux-yocto/6.6: update to v6.6.151
-  migration-guide: add release notes for 5.0.20
-  openssl: upgrade to 3.5.8
-  ovmf: fix tpm :term:`PACKAGECONFIG` to use TPM2_ENABLE
-  poky.conf: Bump version for 5.0.21 release
-  pseudo: upgrade to 1.9.11
-  pseudo: Add in openat2, exec and linkat fixes
-  ptest-runner: Upgrade to 2.5.1
-  python3: skip ptest incompatible with ptest-runner >= 2.5.0
-  python3: upgrade to 3.12.14
-  python3-attrs: fix :term:`CVE_PRODUCT`
-  python3-babel: fix :term:`CVE_PRODUCT`
-  python3-certifi: fix homepage
-  python3-click: fix :term:`CVE_PRODUCT`
-  python3-dbusmock: fix :term:`CVE_PRODUCT`
-  python3-git: fix :term:`CVE_PRODUCT`
-  python3-mako: correct :term:`CVE_PRODUCT` mapping
-  python3-numpy: fix :term:`CVE_PRODUCT`
-  python3-py: set :term:`CVE_PRODUCT`
-  python3-pycryptodome: fix :term:`CVE_PRODUCT`
-  python3-pycryptodomex: fix :term:`CVE_PRODUCT`
-  python3-wheel: fix :term:`CVE_PRODUCT`
-  ref-manual/features.rst: add tools-profile
-  ref-manual/structure.rst: fix a "local.conf" -> "layer.conf" typo
-  ref-manual/variables.rst: BBMULTICONFIG: fix a typo
-  ref-manual/varlocality.rst: remove :term:`BBFILES` from local configuration assignments
-  ref-manual: add new vex class
-  ref-manual: drop the full stop from the :term:`QB_SMP` example
-  ref-manual: fix a typo in the :term:`QB_SMP` description
-  rootfs.py: fix run-postinsts removal on multilib images
-  scripts/install-buildtools: Update to 5.0.20
-  socat: fix native build on host with newer glibc
-  spdx30_tasks: Fix :term:`SPDX_CUSTOM_ANNOTATION_VARS` implementation
-  sstate: Update mtime/atime for sig/siginfo files in sstate_checkhashes()
-  sstate: Update unpack touch code to be consistent
-  systemd: upgrade to 255.22
-  testimage: avoid symlinking missing qemu boot log
-  time64: enable 64-bit time/file-offset flags for 32-bit nativesdk
-  toaster-manual: spell the json code-block language in lower case
-  u-boot: share :term:`CVE_PRODUCT` with u-boot-tools
-  wireless-regdb: upgrade to 2026.09.03


Known Issues in Yocto-5.0.21
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.21
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adarsh Jagadish Kamini
-  Anil Dongare
-  Antonin Godard
-  Bruce Ashfield
-  Daniel Turull
-  Darsh Kelaiya
-  Deepak Rathore
-  Deepesh Varatharajan
-  Devansh Patel
-  Eilís 'pidge' Ní Fhlannagáin
-  Emily Vekariya
-  Etienne Cordonnier
-  Gyorgy Sarvari
-  Hemanth Kumar M D
-  Hetvi Thakar
-  Hiago De Franco
-  Hitendra Prajapati
-  Jaipaul Cheernam
-  Jakub Szczudlo (Nokia)
-  Kyungjik Min
-  Lee Chee Yang
-  Martin Jansa
-  Paul Barker
-  Peter Marko
-  Peter Tatrai
-  Pratik Farkase
-  Richard Purdie
-  Robert P. J. Day
-  Roland Kovacs
-  Ross Burton
-  Sebastian Muxel
-  Siddharth Doshi
-  Stefano Tondo
-  Tim Orling
-  Trevor Woerner
-  Vijay Anusuri
-  Wang Mingyu
-  Yoann Congal
-  Yogita Urade

Repositories / Downloads for Yocto-5.0.21
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.21 </yocto-docs/log/?h=yocto-5.0.21>`
-  Git Revision: :yocto_git:`9d2cb790abf5ad37b2b1f35823d238c57f4e21a5 </yocto-docs/commit/?id=9d2cb790abf5ad37b2b1f35823d238c57f4e21a5>`
-  Release Artefact: yocto-docs-9d2cb790abf5ad37b2b1f35823d238c57f4e21a5
-  sha: fe9c4042581b09395422593ccd1251398ecf65daccabadc35ccf52ce4a59face
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.21/yocto-docs-9d2cb790abf5ad37b2b1f35823d238c57f4e21a5.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.21/yocto-docs-9d2cb790abf5ad37b2b1f35823d238c57f4e21a5.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.21 </poky/log/?h=yocto-5.0.21>`
-  Git Revision: :yocto_git:`4fb7016a07507683359ebd620f1951fd5ea44bfa </poky/commit/?id=4fb7016a07507683359ebd620f1951fd5ea44bfa>`
-  Release Artefact: poky-4fb7016a07507683359ebd620f1951fd5ea44bfa
-  sha: 48e797d41bfbe52eafde62b4fe5231656e5233df811f33abb3b83b6413aa148b
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.21/poky-4fb7016a07507683359ebd620f1951fd5ea44bfa.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.21/poky-4fb7016a07507683359ebd620f1951fd5ea44bfa.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.21 </openembedded-core/log/?h=yocto-5.0.21>`
-  Git Revision: :oe_git:`b4f06303e55c7270a8482a610927a1c41d5dbf9c </openembedded-core/commit/?id=b4f06303e55c7270a8482a610927a1c41d5dbf9c>`
-  Release Artefact: oecore-b4f06303e55c7270a8482a610927a1c41d5dbf9c
-  sha: cf9c6e7f23ad718472dce4f5a4da9ebc8aeb8b322b8be84d9b83ec8ee9bdc38d
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.21/oecore-b4f06303e55c7270a8482a610927a1c41d5dbf9c.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.21/oecore-b4f06303e55c7270a8482a610927a1c41d5dbf9c.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`scarthgap </meta-yocto/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.21 </meta-yocto/log/?h=yocto-5.0.21>`
-  Git Revision: :yocto_git:`8183b55783d45dd45691983bc0b407752a3449f4 </meta-yocto/commit/?id=8183b55783d45dd45691983bc0b407752a3449f4>`
-  Release Artefact: meta-yocto-8183b55783d45dd45691983bc0b407752a3449f4
-  sha: bf5df906352a128d281791d2ab9936cd5b26789fb3340b7e86c2fe195fcdaad0
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.21/meta-yocto-8183b55783d45dd45691983bc0b407752a3449f4.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.21/meta-yocto-8183b55783d45dd45691983bc0b407752a3449f4.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.21 </meta-mingw/log/?h=yocto-5.0.21>`
-  Git Revision: :yocto_git:`a2065f321a3dbca1e69f070502917c76653d9105 </meta-mingw/commit/?id=a2065f321a3dbca1e69f070502917c76653d9105>`
-  Release Artefact: meta-mingw-a2065f321a3dbca1e69f070502917c76653d9105
-  sha: 02042144a265ca5a25a8345688bb626636639a96f33d2b7b8c581edcf2d8df4e
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.21/meta-mingw-a2065f321a3dbca1e69f070502917c76653d9105.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.21/meta-mingw-a2065f321a3dbca1e69f070502917c76653d9105.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.21 </bitbake/log/?h=yocto-5.0.21>`
-  Git Revision: :oe_git:`d3b32587c2314febf96897f97e0172790b4fc1cf </bitbake/commit/?id=d3b32587c2314febf96897f97e0172790b4fc1cf>`
-  Release Artefact: bitbake-d3b32587c2314febf96897f97e0172790b4fc1cf
-  sha: d437ed0d9e7f8aa9365971e84b48004f38c0bf6a30cd800397b16fcc9921a14d
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.21/bitbake-d3b32587c2314febf96897f97e0172790b4fc1cf.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.21/bitbake-d3b32587c2314febf96897f97e0172790b4fc1cf.tar.bz2

