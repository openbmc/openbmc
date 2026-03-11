.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.7 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.7
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2024-52616`
-  binutils: Fix :cve_nist:`2024-53589`
-  ffmpeg: Fix :cve_nist:`2024-35366`, :cve_nist:`2024-35367` and :cve_nist:`2024-35368`
-  gstreamer1.0-plugins-base: Fix :cve_nist:`2024-47538`, :cve_nist:`2024-47541`,
   :cve_nist:`2024-47542`, :cve_nist:`2024-47600`, :cve_nist:`2024-47607`, :cve_nist:`2024-47615`
   and :cve_nist:`2024-47835`
-  gstreamer1.0-plugins-good: Fix :cve_nist:`2024-47537`, :cve_nist:`2024-47539`,
   :cve_nist:`2024-47540`, :cve_nist:`2024-47543`, :cve_nist:`2024-47544`, :cve_nist:`2024-47545`,
   :cve_nist:`2024-47546`, :cve_nist:`2024-47596`, :cve_nist:`2024-47597`, :cve_nist:`2024-47598`,
   :cve_nist:`2024-47599`, :cve_nist:`2024-47601`, :cve_nist:`2024-47602`, :cve_nist:`2024-47603`,
   :cve_nist:`2024-47606`, :cve_nist:`2024-47613`, :cve_nist:`2024-47774`, :cve_nist:`2024-47775`,
   :cve_nist:`2024-47776`, :cve_nist:`2024-47777`, :cve_nist:`2024-47778` and :cve_nist:`2024-47834`
-  gstreamer1.0: Ignore :cve_nist:`2024-47537`, :cve_nist:`2024-47539`, :cve_nist:`2024-47540`,
   :cve_nist:`2024-47543`, :cve_nist:`2024-47544`, :cve_nist:`2024-47545`, :cve_nist:`2024-47538`,
   :cve_nist:`2024-47541`, :cve_nist:`2024-47542`, :cve_nist:`2024-47600`, :cve_nist:`2024-47607`,
   :cve_nist:`2024-47615`, :cve_nist:`2024-47835`, :cve_nist:`2024-47546`, :cve_nist:`2024-47596`,
   :cve_nist:`2024-47597`, :cve_nist:`2024-47598`, :cve_nist:`2024-47599`, :cve_nist:`2024-47601`,
   :cve_nist:`2024-47602`, :cve_nist:`2024-47603`, :cve_nist:`2024-47613`, :cve_nist:`2024-47774`,
   :cve_nist:`2024-47775`, :cve_nist:`2024-47776`, :cve_nist:`2024-47777`, :cve_nist:`2024-47778`
   and :cve_nist:`2024-47834`
-  libarchive: Fix :cve_nist:`2024-20696`
-  libxml2: Fix :cve_nist:`2024-40896`
-  linux-yocto/6.6: Fix :cve_nist:`2024-27059`, :cve_nist:`2024-43098`, :cve_nist:`2024-45828`,
   :cve_nist:`2024-47141`, :cve_nist:`2024-47143`, :cve_nist:`2024-47704`, :cve_nist:`2024-47809`,
   :cve_nist:`2024-48873`, :cve_nist:`2024-48875`, :cve_nist:`2024-48881`, :cve_nist:`2024-49863`,
   :cve_nist:`2024-49864`, :cve_nist:`2024-49866`, :cve_nist:`2024-49867`, :cve_nist:`2024-49868`,
   :cve_nist:`2024-49870`, :cve_nist:`2024-49871`, :cve_nist:`2024-49874`, :cve_nist:`2024-49875`,
   :cve_nist:`2024-49877`, :cve_nist:`2024-49878`, :cve_nist:`2024-49879`, :cve_nist:`2024-49881`,
   :cve_nist:`2024-49882`, :cve_nist:`2024-49883`, :cve_nist:`2024-49884`, :cve_nist:`2024-49886`,
   :cve_nist:`2024-49889`, :cve_nist:`2024-49890`, :cve_nist:`2024-49892`, :cve_nist:`2024-49894`,
   :cve_nist:`2024-49895`, :cve_nist:`2024-49896`, :cve_nist:`2024-49900`, :cve_nist:`2024-49901`,
   :cve_nist:`2024-49902`, :cve_nist:`2024-49903`, :cve_nist:`2024-49905`, :cve_nist:`2024-49907`,
   :cve_nist:`2024-49912`, :cve_nist:`2024-49913`, :cve_nist:`2024-49924`, :cve_nist:`2024-49925`,
   :cve_nist:`2024-49927`, :cve_nist:`2024-49929`, :cve_nist:`2024-49930`, :cve_nist:`2024-49931`,
   :cve_nist:`2024-49933`, :cve_nist:`2024-49935`, :cve_nist:`2024-49936`, :cve_nist:`2024-49937`,
   :cve_nist:`2024-49938`, :cve_nist:`2024-49939`, :cve_nist:`2024-49944`, :cve_nist:`2024-49946`,
   :cve_nist:`2024-49947`, :cve_nist:`2024-49948`, :cve_nist:`2024-49949`, :cve_nist:`2024-49950`,
   :cve_nist:`2024-49951`, :cve_nist:`2024-49952`, :cve_nist:`2024-49953`, :cve_nist:`2024-49954`,
   :cve_nist:`2024-49955`, :cve_nist:`2024-49957`, :cve_nist:`2024-49958`, :cve_nist:`2024-49959`,
   :cve_nist:`2024-49960`, :cve_nist:`2024-49961`, :cve_nist:`2024-49962`, :cve_nist:`2024-49963`,
   :cve_nist:`2024-49965`, :cve_nist:`2024-49966`, :cve_nist:`2024-49969`, :cve_nist:`2024-49973`,
   :cve_nist:`2024-49975`, :cve_nist:`2024-49976`, :cve_nist:`2024-49977`, :cve_nist:`2024-49978`,
   :cve_nist:`2024-49980`, :cve_nist:`2024-49981`, :cve_nist:`2024-49982`, :cve_nist:`2024-49983`,
   :cve_nist:`2024-49985`, :cve_nist:`2024-49986`, :cve_nist:`2024-49987`, :cve_nist:`2024-49988`,
   :cve_nist:`2024-49989`, :cve_nist:`2024-49991`, :cve_nist:`2024-49992`, :cve_nist:`2024-49995`,
   :cve_nist:`2024-49996`, :cve_nist:`2024-49997`, :cve_nist:`2024-50000`, :cve_nist:`2024-50001`,
   :cve_nist:`2024-50002`, :cve_nist:`2024-50003`, :cve_nist:`2024-50005`, :cve_nist:`2024-50006`,
   :cve_nist:`2024-50007`, :cve_nist:`2024-50008`, :cve_nist:`2024-50012`, :cve_nist:`2024-50013`,
   :cve_nist:`2024-50015`, :cve_nist:`2024-50016`, :cve_nist:`2024-50019`, :cve_nist:`2024-50022`,
   :cve_nist:`2024-50023`, :cve_nist:`2024-50024`, :cve_nist:`2024-50026`, :cve_nist:`2024-50029`,
   :cve_nist:`2024-50031`, :cve_nist:`2024-50032`, :cve_nist:`2024-50033`, :cve_nist:`2024-50035`,
   :cve_nist:`2024-50036`, :cve_nist:`2024-50038`, :cve_nist:`2024-50039`, :cve_nist:`2024-50040`,
   :cve_nist:`2024-50041`, :cve_nist:`2024-50044`, :cve_nist:`2024-50045`, :cve_nist:`2024-50046`,
   :cve_nist:`2024-50047`, :cve_nist:`2024-50048`, :cve_nist:`2024-50049`, :cve_nist:`2024-50051`,
   :cve_nist:`2024-50055`, :cve_nist:`2024-50057`, :cve_nist:`2024-50058`, :cve_nist:`2024-50059`,
   :cve_nist:`2024-50060`, :cve_nist:`2024-50061`, :cve_nist:`2024-50062`, :cve_nist:`2024-50063`,
   :cve_nist:`2024-50064`, :cve_nist:`2024-50065`, :cve_nist:`2024-50066`, :cve_nist:`2024-50069`,
   :cve_nist:`2024-50070`, :cve_nist:`2024-50072`, :cve_nist:`2024-50073`, :cve_nist:`2024-50074`,
   :cve_nist:`2024-50075`, :cve_nist:`2024-50076`, :cve_nist:`2024-50077`, :cve_nist:`2024-50078`,
   :cve_nist:`2024-50080`, :cve_nist:`2024-50082`, :cve_nist:`2024-50083`, :cve_nist:`2024-50084`,
   :cve_nist:`2024-50085`, :cve_nist:`2024-50086`, :cve_nist:`2024-50087`, :cve_nist:`2024-50088`,
   :cve_nist:`2024-50093`, :cve_nist:`2024-50095`, :cve_nist:`2024-50096`, :cve_nist:`2024-50098`,
   :cve_nist:`2024-50099`, :cve_nist:`2024-50101`, :cve_nist:`2024-50103`, :cve_nist:`2024-50108`,
   :cve_nist:`2024-50110`, :cve_nist:`2024-50111`, :cve_nist:`2024-50112`, :cve_nist:`2024-50115`,
   :cve_nist:`2024-50116`, :cve_nist:`2024-50117`, :cve_nist:`2024-50120`, :cve_nist:`2024-50121`,
   :cve_nist:`2024-50124`, :cve_nist:`2024-50125`, :cve_nist:`2024-50126`, :cve_nist:`2024-50127`,
   :cve_nist:`2024-50128`, :cve_nist:`2024-50130`, :cve_nist:`2024-50131`, :cve_nist:`2024-50133`,
   :cve_nist:`2024-50134`, :cve_nist:`2024-50135`, :cve_nist:`2024-50136`, :cve_nist:`2024-50139`,
   :cve_nist:`2024-50140`, :cve_nist:`2024-50141`, :cve_nist:`2024-50142`, :cve_nist:`2024-50143`,
   :cve_nist:`2024-50145`, :cve_nist:`2024-50147`, :cve_nist:`2024-50148`, :cve_nist:`2024-50150`,
   :cve_nist:`2024-50151`, :cve_nist:`2024-50152`, :cve_nist:`2024-50153`, :cve_nist:`2024-50154`,
   :cve_nist:`2024-50155`, :cve_nist:`2024-50156`, :cve_nist:`2024-50158`, :cve_nist:`2024-50159`,
   :cve_nist:`2024-50160`, :cve_nist:`2024-50162`, :cve_nist:`2024-50163`, :cve_nist:`2024-50164`,
   :cve_nist:`2024-50166`, :cve_nist:`2024-50167`, :cve_nist:`2024-50168`, :cve_nist:`2024-50169`,
   :cve_nist:`2024-50170`, :cve_nist:`2024-50171`, :cve_nist:`2024-50172`, :cve_nist:`2024-50175`,
   :cve_nist:`2024-50176`, :cve_nist:`2024-50179`, :cve_nist:`2024-50180`, :cve_nist:`2024-50181`,
   :cve_nist:`2024-50182`, :cve_nist:`2024-50183`, :cve_nist:`2024-50184`, :cve_nist:`2024-50185`,
   :cve_nist:`2024-50186`, :cve_nist:`2024-50187`, :cve_nist:`2024-50188`, :cve_nist:`2024-50189`,
   :cve_nist:`2024-50191`, :cve_nist:`2024-50192`, :cve_nist:`2024-50193`, :cve_nist:`2024-50194`,
   :cve_nist:`2024-50195`, :cve_nist:`2024-50196`, :cve_nist:`2024-50198`, :cve_nist:`2024-50201`,
   :cve_nist:`2024-50202`, :cve_nist:`2024-50205`, :cve_nist:`2024-50208`, :cve_nist:`2024-50209`,
   :cve_nist:`2024-50211`, :cve_nist:`2024-50215`, :cve_nist:`2024-50222`, :cve_nist:`2024-50223`,
   :cve_nist:`2024-50224`, :cve_nist:`2024-50226`, :cve_nist:`2024-50229`, :cve_nist:`2024-50230`,
   :cve_nist:`2024-50231`, :cve_nist:`2024-50232`, :cve_nist:`2024-50233`, :cve_nist:`2024-50234`,
   :cve_nist:`2024-50235`, :cve_nist:`2024-50236`, :cve_nist:`2024-50237`, :cve_nist:`2024-50239`,
   :cve_nist:`2024-50240`, :cve_nist:`2024-50242`, :cve_nist:`2024-50243`, :cve_nist:`2024-50244`,
   :cve_nist:`2024-50245`, :cve_nist:`2024-50246`, :cve_nist:`2024-50247`, :cve_nist:`2024-50248`,
   :cve_nist:`2024-50249`, :cve_nist:`2024-50250`, :cve_nist:`2024-50251`, :cve_nist:`2024-50252`,
   :cve_nist:`2024-50255`, :cve_nist:`2024-50256`, :cve_nist:`2024-50257`, :cve_nist:`2024-50258`,
   :cve_nist:`2024-50259`, :cve_nist:`2024-50261`, :cve_nist:`2024-50262`, :cve_nist:`2024-50264`,
   :cve_nist:`2024-50265`, :cve_nist:`2024-50267`, :cve_nist:`2024-50268`, :cve_nist:`2024-50269`,
   :cve_nist:`2024-50271`, :cve_nist:`2024-50272`, :cve_nist:`2024-50273`, :cve_nist:`2024-50275`,
   :cve_nist:`2024-50276`, :cve_nist:`2024-50278`, :cve_nist:`2024-50279`, :cve_nist:`2024-50282`,
   :cve_nist:`2024-50283`, :cve_nist:`2024-50284`, :cve_nist:`2024-50285`, :cve_nist:`2024-50286`,
   :cve_nist:`2024-50287`, :cve_nist:`2024-50292`, :cve_nist:`2024-50296`, :cve_nist:`2024-50298`,
   :cve_nist:`2024-50299`, :cve_nist:`2024-50300`, :cve_nist:`2024-50301`, :cve_nist:`2024-50302`,
   :cve_nist:`2024-53042`, :cve_nist:`2024-53043`, :cve_nist:`2024-53046`, :cve_nist:`2024-53047`,
   :cve_nist:`2024-53052`, :cve_nist:`2024-53055`, :cve_nist:`2024-53057`, :cve_nist:`2024-53058`,
   :cve_nist:`2024-53059`, :cve_nist:`2024-53060`, :cve_nist:`2024-53061`, :cve_nist:`2024-53063`,
   :cve_nist:`2024-53066`, :cve_nist:`2024-53068`, :cve_nist:`2024-53072`, :cve_nist:`2024-53076`,
   :cve_nist:`2024-53079`, :cve_nist:`2024-53081`, :cve_nist:`2024-53082`, :cve_nist:`2024-53083`,
   :cve_nist:`2024-53088`, :cve_nist:`2024-53091`, :cve_nist:`2024-53093`, :cve_nist:`2024-53094`,
   :cve_nist:`2024-53096`, :cve_nist:`2024-53099`, :cve_nist:`2024-53100`, :cve_nist:`2024-53101`,
   :cve_nist:`2024-53103`, :cve_nist:`2024-53108`, :cve_nist:`2024-53109`, :cve_nist:`2024-53110`,
   :cve_nist:`2024-53112`, :cve_nist:`2024-53113`, :cve_nist:`2024-53119`, :cve_nist:`2024-53120`,
   :cve_nist:`2024-53121`, :cve_nist:`2024-53122`, :cve_nist:`2024-53123`, :cve_nist:`2024-53126`,
   :cve_nist:`2024-53127`, :cve_nist:`2024-53129`, :cve_nist:`2024-53130`, :cve_nist:`2024-53131`,
   :cve_nist:`2024-53134`, :cve_nist:`2024-53135`, :cve_nist:`2024-53138`, :cve_nist:`2024-53139`,
   :cve_nist:`2024-53140`, :cve_nist:`2024-53141`, :cve_nist:`2024-53142`, :cve_nist:`2024-53145`,
   :cve_nist:`2024-53146`, :cve_nist:`2024-53150`, :cve_nist:`2024-53151`, :cve_nist:`2024-53154`,
   :cve_nist:`2024-53155`, :cve_nist:`2024-53156`, :cve_nist:`2024-53157`, :cve_nist:`2024-53161`,
   :cve_nist:`2024-53165`, :cve_nist:`2024-53166`, :cve_nist:`2024-53168`, :cve_nist:`2024-53171`,
   :cve_nist:`2024-53173`, :cve_nist:`2024-53175`, :cve_nist:`2024-53180`, :cve_nist:`2024-53188`,
   :cve_nist:`2024-53191`, :cve_nist:`2024-53200`, :cve_nist:`2024-53202`, :cve_nist:`2024-53208`,
   :cve_nist:`2024-53210`, :cve_nist:`2024-53213`, :cve_nist:`2024-53215`, :cve_nist:`2024-53217`,
   :cve_nist:`2024-53224`, :cve_nist:`2024-53226`, :cve_nist:`2024-53227`, :cve_nist:`2024-53230`,
   :cve_nist:`2024-53231`, :cve_nist:`2024-53237`, :cve_nist:`2024-53239`, :cve_nist:`2024-54683`,
   :cve_nist:`2024-55916`, :cve_nist:`2024-56369`, :cve_nist:`2024-56538`, :cve_nist:`2024-56551`,
   :cve_nist:`2024-56567`, :cve_nist:`2024-56568`, :cve_nist:`2024-56569`, :cve_nist:`2024-56572`,
   :cve_nist:`2024-56574`, :cve_nist:`2024-56575`, :cve_nist:`2024-56577`, :cve_nist:`2024-56578`,
   :cve_nist:`2024-56579`, :cve_nist:`2024-56581`, :cve_nist:`2024-56587`, :cve_nist:`2024-56593`,
   :cve_nist:`2024-56595`, :cve_nist:`2024-56596`, :cve_nist:`2024-56598`, :cve_nist:`2024-56600`,
   :cve_nist:`2024-56601`, :cve_nist:`2024-56602`, :cve_nist:`2024-56603`, :cve_nist:`2024-56604`,
   :cve_nist:`2024-56605`, :cve_nist:`2024-56606`, :cve_nist:`2024-56611`, :cve_nist:`2024-56613`,
   :cve_nist:`2024-56614`, :cve_nist:`2024-56615`, :cve_nist:`2024-56617`, :cve_nist:`2024-56622`,
   :cve_nist:`2024-56623`, :cve_nist:`2024-56626`, :cve_nist:`2024-56627`, :cve_nist:`2024-56629`,
   :cve_nist:`2024-56631`, :cve_nist:`2024-56634`, :cve_nist:`2024-56635`, :cve_nist:`2024-56640`,
   :cve_nist:`2024-56642`, :cve_nist:`2024-56643`, :cve_nist:`2024-56648`, :cve_nist:`2024-56649`,
   :cve_nist:`2024-56650`, :cve_nist:`2024-56651`, :cve_nist:`2024-56653`, :cve_nist:`2024-56654`,
   :cve_nist:`2024-56657`, :cve_nist:`2024-56658`, :cve_nist:`2024-56659`, :cve_nist:`2024-56660`,
   :cve_nist:`2024-56662`, :cve_nist:`2024-56663`, :cve_nist:`2024-56664`, :cve_nist:`2024-56667`,
   :cve_nist:`2024-56670`, :cve_nist:`2024-56672`, :cve_nist:`2024-56675`, :cve_nist:`2024-56687`,
   :cve_nist:`2024-56688`, :cve_nist:`2024-56689`, :cve_nist:`2024-56692`, :cve_nist:`2024-56694`,
   :cve_nist:`2024-56698`, :cve_nist:`2024-56704`, :cve_nist:`2024-56708`, :cve_nist:`2024-56710`,
   :cve_nist:`2024-56715`, :cve_nist:`2024-56716`, :cve_nist:`2024-56717`, :cve_nist:`2024-56718`,
   :cve_nist:`2024-56720`, :cve_nist:`2024-56722`, :cve_nist:`2024-56723`, :cve_nist:`2024-56724`,
   :cve_nist:`2024-56725`, :cve_nist:`2024-56726`, :cve_nist:`2024-56727`, :cve_nist:`2024-56728`,
   :cve_nist:`2024-56729`, :cve_nist:`2024-56739`, :cve_nist:`2024-56741`, :cve_nist:`2024-56744`,
   :cve_nist:`2024-56745`, :cve_nist:`2024-56746`, :cve_nist:`2024-56747`, :cve_nist:`2024-56748`,
   :cve_nist:`2024-56751`, :cve_nist:`2024-56752`, :cve_nist:`2024-56754`, :cve_nist:`2024-56755`,
   :cve_nist:`2024-56756`, :cve_nist:`2024-56760`, :cve_nist:`2024-56763`, :cve_nist:`2024-56765`,
   :cve_nist:`2024-56767`, :cve_nist:`2024-56769`, :cve_nist:`2024-56770`, :cve_nist:`2024-56774`,
   :cve_nist:`2024-56776`, :cve_nist:`2024-56777`, :cve_nist:`2024-56778`, :cve_nist:`2024-56779`,
   :cve_nist:`2024-56780`, :cve_nist:`2024-56781`, :cve_nist:`2024-56783`, :cve_nist:`2024-56785`,
   :cve_nist:`2024-56786`, :cve_nist:`2024-56787`, :cve_nist:`2024-57798`, :cve_nist:`2024-57807`
   and :cve_nist:`2024-57874`
-  ofono: Fix :cve_nist:`2023-4232`, :cve_nist:`2023-4235`, :cve_nist:`2024-7539`,
   :cve_nist:`2024-7540`, :cve_nist:`2024-7541`, :cve_nist:`2024-7542`, :cve_nist:`2024-7543`,
   :cve_nist:`2024-7544`, :cve_nist:`2024-7545`, :cve_nist:`2024-7546` and :cve_nist:`2024-7547`
-  rsync: Fix :cve_nist:`2024-12084`, :cve_nist:`2024-12085`, :cve_nist:`2024-12086`,
   :cve_nist:`2024-12087`, :cve_nist:`2024-12088` and :cve_nist:`2024-12747`
-  socat: Fix :cve_nist:`2024-54661`
-  subversion: Fix :cve_nist:`2024-46901`
-  wget: Fix :cve_nist:`2024-10524`


Fixes in Yocto-5.0.7
~~~~~~~~~~~~~~~~~~~~

-  bitbake: cooker: Make cooker 'skiplist' per-multiconfig/mc
-  bitbake: tests/fetch: Fix git shallow test failure with git >= 2.48
-  bitbake: ui/knotty: print log paths for failed tasks in summary
-  bitbake: ui/knotty: respect NO_COLOR & check for tty; rename print_hyperlink => format_hyperlink
-  bluez5: Revert "bluez5: remove configuration files from install task"
-  bluez5: backport patch to fix address type when loading keys
-  boost: fix do_fetch error
-  build-appliance-image: Update to scarthgap head revision
-  classes/nativesdk: also override :term:`TUNE_PKGARCH`
-  classes/qemu: use tune to select QEMU_EXTRAOPTIONS, not package architecture
-  contributor-guide/submit-changes.rst: suggest to remove the git signature
-  cve-update-nvd2-native: Handle :term:`BB_NO_NETWORK` and missing db
-  cve-update-nvd2-native: Tweak to work better with NFS :term:`DL_DIR`
-  dev-manual/bmaptool.rst: correct command for bmaptool-native
-  dev-manual/bmaptool.rst: simplify and fix instructions
-  dev-manual: fix styling of references to bmaptool
-  docs: Gather dependencies in poky.yaml.in
-  docs: Update autobuilder URLs to valkyrie
-  docs: Update the documentation for :term:`SRCPV`
-  gcc: Fix c++: tweak for Wrange-loop-construct
-  groff: Fix race issues for parallel build
-  libgfortran: fix buildpath QA issue
-  libxml2: Upgrade to 2.12.9
-  linux-yocto/6.6: bsp/genericarm64: disable ARM64_SME
-  linux-yocto/6.6: genericarm64.cfg: enable CONFIG_DMA_CMA
-  linux-yocto/6.6: update to v6.6.69
-  lttng-modules: fix sched_stat_runtime changed in Linux 6.6.66
-  migration-guides: add release notes for 5.0.6
-  oeqa/ssh: allow to retrieve raw, unformatted ouput
-  ovmf-native: remove .pyc files from install
-  poky.conf: add new tested distros
-  poky.conf: bump version for 5.0.7
-  poky.yaml.in: add missing locales dependency
-  poky.yaml.in: replace inkscape dependency by librsvg2-bin
-  populate_sdk_ext: write_local_conf add shutil import
-  pulseaudio: fix webrtc audio depdency
-  python3-requests: upgrade to 2.32.3
-  python3: Drop empty patch
-  python3: add dependency on -compression to -core
-  python3: upgrade to 3.12.7
-  ref-manual: move runtime-testing section to the test-manual
-  ref-manual: use standardized method accross both ubuntu and debian for locale install
-  ref-manual: SSTATE_MIRRORS/SOURCE_MIRROR_URL: add instructions for mirror authentication
-  reproducible-builds.rst: show how to build a single package
-  rust-target-config: Fix TARGET_C_INT_WIDTH with correct size
-  rust: Revert "rust: Add new varaible RUST_ENABLE_EXTRA_TOOLS"
-  rust: add reproducibility patch to eliminate host leakage
-  rust: build the default set of tools
-  rust: correctly link rust-snapshot into build/stage0
-  rust: use rust-snapshot binaries only in rust-native
-  sanity.bbclass: skip check_userns for non-local uid
-  scripts/install-buildtools: Update to 5.0.6
-  system-requirements.rst: add dependencies for pdf builds
-  system-requirements: add fedora 39 to supported distros
-  system-requirements: update list of supported distros
-  systemd: enable create-log-dirs
-  test-manual/reproducible-builds: fix reproducible links


Known Issues in Yocto-5.0.7
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.7
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Aleksandar Nikolic
-  Alexander Kanavin
-  Alexis Lothoré
-  Antonin Godard
-  Archana Polampalli
-  Bruce Ashfield
-  Catalin Popescu
-  Changqing Li
-  Chen Qi
-  Chris Laplante
-  Divya Chellam
-  Esben Haabendal
-  Guénaël Muller
-  Guðni Már Gilbert
-  Harish Sadineni
-  Hiago De Franco
-  Hitendra Prajapati
-  Jiaying Song
-  Khem Raj
-  Lee Chee Yang
-  Mark Hatle
-  Michael Opdenacker
-  Mikko Rapeli
-  Peter Marko
-  Richard Purdie
-  Robert Yang
-  Ross Burton
-  Soumya Sambu
-  Steve Sakoman
-  Sunil Dora
-  Trevor Gamblin
-  Xiangyu Chen
-  Yash Shinde
-  Zhang Peng
-  Zahir Hussain


Repositories / Downloads for Yocto-5.0.7
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.7 </poky/log/?h=yocto-5.0.7>`
-  Git Revision: :yocto_git:`7dad83c7e5e9637c0ff5d5712409611fd4a14946 </poky/commit/?id=7dad83c7e5e9637c0ff5d5712409611fd4a14946>`
-  Release Artefact: poky-7dad83c7e5e9637c0ff5d5712409611fd4a14946
-  sha: ae688031b19b88582bb4a76d0525e3704b981ad1d21eb38a0873cd01dd9a4652
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.7/poky-7dad83c7e5e9637c0ff5d5712409611fd4a14946.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.7/poky-7dad83c7e5e9637c0ff5d5712409611fd4a14946.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.7 </openembedded-core/log/?h=yocto-5.0.7>`
-  Git Revision: :oe_git:`62cb12967391db709315820d48853ffa4c6b4740 </openembedded-core/commit/?id=62cb12967391db709315820d48853ffa4c6b4740>`
-  Release Artefact: oecore-62cb12967391db709315820d48853ffa4c6b4740
-  sha: bc45429df1805445b678f1b0ed6ce017edfac38c7226dce92ce393b3ef311f95
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.7/oecore-62cb12967391db709315820d48853ffa4c6b4740.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.7/oecore-62cb12967391db709315820d48853ffa4c6b4740.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.7 </meta-mingw/log/?h=yocto-5.0.7>`
-  Git Revision: :yocto_git:`acbba477893ef87388effc4679b7f40ee49fc852 </meta-mingw/commit/?id=acbba477893ef87388effc4679b7f40ee49fc852>`
-  Release Artefact: meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852
-  sha: 3b7c2f475dad5130bace652b150367f587d44b391218b1364a8bbc430b48c54c
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.7/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.7/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.7 </bitbake/log/?h=yocto-5.0.7>`
-  Git Revision: :oe_git:`aa0e540fc31a1c26839efd2c7785a751ce24ebfb </bitbake/commit/?id=aa0e540fc31a1c26839efd2c7785a751ce24ebfb>`
-  Release Artefact: bitbake-aa0e540fc31a1c26839efd2c7785a751ce24ebfb
-  sha: 169b68ed7d5e55015b1c35a82d35efaa25c87cba4722c85e66514a15d31e1d28
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.7/bitbake-aa0e540fc31a1c26839efd2c7785a751ce24ebfb.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.7/bitbake-aa0e540fc31a1c26839efd2c7785a751ce24ebfb.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.7 </yocto-docs/log/?h=yocto-5.0.7>`
-  Git Revision: :yocto_git:`bb9e018adcc10c642f87d0b95432783b5eb8057b </yocto-docs/commit/?id=bb9e018adcc10c642f87d0b95432783b5eb8057b>`

