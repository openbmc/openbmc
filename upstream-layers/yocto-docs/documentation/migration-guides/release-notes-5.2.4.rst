Release notes for Yocto-5.2.4 (Walnascar)
-----------------------------------------

Security Fixes in Yocto-5.2.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-8225`
-  binutils: Ignore :cve_nist:`2025-8224`
-  cups: Fix :cve_nist:`2025-58060` and :cve_nist:`2025-58364`
-  curl: Fix :cve_nist:`2025-9086` and :cve_nist:`2025-10148`
-  elfutils: Fix :cve_nist:`2025-1352`, :cve_nist:`2025-1365`, :cve_nist:`2025-1371`,
   :cve_nist:`2025-1372`, :cve_nist:`2025-1376` and :cve_nist:`2025-1377`
-  expat: Fix :cve_nist:`2025-59375`
-  ffmpeg: Fix CVE-2025-7700
-  glib-2.0: Fix :cve_nist:`2025-4056`, :cve_nist:`2025-6052` and :cve_nist:`2025-7039`
-  gnutls: Ignore :cve_nist:`2025-32989` and :cve_nist:`2025-32990`
-  grub2: Fix :cve_nist:`2024-56738`
-  grub2: Ignore :cve_nist:`2024-2312`
-  gstreamer1.0: Ignore :cve_nist:`2025-2759`, :cve_nist:`2025-3887`, :cve_nist:`2025-47183`,
   :cve_nist:`2025-47219`, :cve_nist:`2025-47806`, :cve_nist:`2025-47807` and :cve_nist:`2025-47808`
-  libarchive: Fix :cve_nist:`2025-5916`, :cve_nist:`2025-5917` and :cve_nist:`2025-5918`
-  libpre2: Fix :cve_nist:`2025-58050`
-  linux-yocto/6.12: Ignore :cve_nist:`2022-50233`, :cve_nist:`2023-3865`, :cve_nist:`2023-3866`,
   :cve_nist:`2023-3867`, :cve_nist:`2023-4130`, :cve_nist:`2023-4515`, :cve_nist:`2023-32246`,
   :cve_nist:`2023-32249`, :cve_nist:`2024-58238`, :cve_nist:`2024-58239`, :cve_nist:`2024-58240`,
   :cve_nist:`2025-21884`, :cve_nist:`2025-22103`, :cve_nist:`2025-22113`, :cve_nist:`2025-22115`,
   :cve_nist:`2025-22124`, :cve_nist:`2025-22125`, :cve_nist:`2025-23133`, :cve_nist:`2025-37925`,
   :cve_nist:`2025-37984`, :cve_nist:`2025-38067`, :cve_nist:`2025-38104`, :cve_nist:`2025-38272`,
   :cve_nist:`2025-38306`, :cve_nist:`2025-38335`, :cve_nist:`2025-38349`, :cve_nist:`2025-38350`,
   :cve_nist:`2025-38351`, :cve_nist:`2025-38352`, :cve_nist:`2025-38353`, :cve_nist:`2025-38354`,
   :cve_nist:`2025-38355`, :cve_nist:`2025-38356`, :cve_nist:`2025-38357`, :cve_nist:`2025-38358`,
   :cve_nist:`2025-38360`, :cve_nist:`2025-38361`, :cve_nist:`2025-38362`, :cve_nist:`2025-38363`,
   :cve_nist:`2025-38364`, :cve_nist:`2025-38365`, :cve_nist:`2025-38366`, :cve_nist:`2025-38367`,
   :cve_nist:`2025-38368`, :cve_nist:`2025-38369`, :cve_nist:`2025-38370`, :cve_nist:`2025-38371`,
   :cve_nist:`2025-38372`, :cve_nist:`2025-38373`, :cve_nist:`2025-38374`, :cve_nist:`2025-38375`,
   :cve_nist:`2025-38376`, :cve_nist:`2025-38377`, :cve_nist:`2025-38378`, :cve_nist:`2025-38379`,
   :cve_nist:`2025-38380`, :cve_nist:`2025-38381`, :cve_nist:`2025-38382`, :cve_nist:`2025-38383`,
   :cve_nist:`2025-38384`, :cve_nist:`2025-38385`, :cve_nist:`2025-38386`, :cve_nist:`2025-38387`,
   :cve_nist:`2025-38388`, :cve_nist:`2025-38389`, :cve_nist:`2025-38390`, :cve_nist:`2025-38391`,
   :cve_nist:`2025-38392`, :cve_nist:`2025-38393`, :cve_nist:`2025-38394`, :cve_nist:`2025-38395`,
   :cve_nist:`2025-38396`, :cve_nist:`2025-38397`, :cve_nist:`2025-38398`, :cve_nist:`2025-38399`,
   :cve_nist:`2025-38400`, :cve_nist:`2025-38401`, :cve_nist:`2025-38402`, :cve_nist:`2025-38403`,
   :cve_nist:`2025-38404`, :cve_nist:`2025-38405`, :cve_nist:`2025-38406`, :cve_nist:`2025-38407`,
   :cve_nist:`2025-38408`, :cve_nist:`2025-38409`, :cve_nist:`2025-38410`, :cve_nist:`2025-38411`,
   :cve_nist:`2025-38412`, :cve_nist:`2025-38413`, :cve_nist:`2025-38414`, :cve_nist:`2025-38415`,
   :cve_nist:`2025-38416`, :cve_nist:`2025-38417`, :cve_nist:`2025-38418`, :cve_nist:`2025-38419`,
   :cve_nist:`2025-38420`, :cve_nist:`2025-38421`, :cve_nist:`2025-38422`, :cve_nist:`2025-38423`,
   :cve_nist:`2025-38424`, :cve_nist:`2025-38425`, :cve_nist:`2025-38427`, :cve_nist:`2025-38428`,
   :cve_nist:`2025-38429`, :cve_nist:`2025-38430`, :cve_nist:`2025-38431`, :cve_nist:`2025-38432`,
   :cve_nist:`2025-38433`, :cve_nist:`2025-38434`, :cve_nist:`2025-38435`, :cve_nist:`2025-38436`,
   :cve_nist:`2025-38437`, :cve_nist:`2025-38438`, :cve_nist:`2025-38439`, :cve_nist:`2025-38440`,
   :cve_nist:`2025-38441`, :cve_nist:`2025-38442`, :cve_nist:`2025-38443`, :cve_nist:`2025-38444`,
   :cve_nist:`2025-38445`, :cve_nist:`2025-38446`, :cve_nist:`2025-38447`, :cve_nist:`2025-38448`,
   :cve_nist:`2025-38449`, :cve_nist:`2025-38450`, :cve_nist:`2025-38451`, :cve_nist:`2025-38452`,
   :cve_nist:`2025-38453`, :cve_nist:`2025-38454`, :cve_nist:`2025-38455`, :cve_nist:`2025-38456`,
   :cve_nist:`2025-38457`, :cve_nist:`2025-38458`, :cve_nist:`2025-38459`, :cve_nist:`2025-38460`,
   :cve_nist:`2025-38461`, :cve_nist:`2025-38462`, :cve_nist:`2025-38463`, :cve_nist:`2025-38464`,
   :cve_nist:`2025-38465`, :cve_nist:`2025-38466`, :cve_nist:`2025-38467`, :cve_nist:`2025-38468`,
   :cve_nist:`2025-38469`, :cve_nist:`2025-38470`, :cve_nist:`2025-38471`, :cve_nist:`2025-38472`,
   :cve_nist:`2025-38473`, :cve_nist:`2025-38474`, :cve_nist:`2025-38475`, :cve_nist:`2025-38476`,
   :cve_nist:`2025-38477`, :cve_nist:`2025-38478`, :cve_nist:`2025-38480`, :cve_nist:`2025-38481`,
   :cve_nist:`2025-38482`, :cve_nist:`2025-38483`, :cve_nist:`2025-38484`, :cve_nist:`2025-38485`,
   :cve_nist:`2025-38486`, :cve_nist:`2025-38487`, :cve_nist:`2025-38488`, :cve_nist:`2025-38489`,
   :cve_nist:`2025-38490`, :cve_nist:`2025-38491`, :cve_nist:`2025-38492`, :cve_nist:`2025-38493`,
   :cve_nist:`2025-38494`, :cve_nist:`2025-38495`, :cve_nist:`2025-38496`, :cve_nist:`2025-38497`,
   :cve_nist:`2025-38498`, :cve_nist:`2025-38499`, :cve_nist:`2025-38500`, :cve_nist:`2025-38501`,
   :cve_nist:`2025-38502`, :cve_nist:`2025-38503`, :cve_nist:`2025-38504`, :cve_nist:`2025-38505`,
   :cve_nist:`2025-38506`, :cve_nist:`2025-38507`, :cve_nist:`2025-38508`, :cve_nist:`2025-38509`,
   :cve_nist:`2025-38510`, :cve_nist:`2025-38511`, :cve_nist:`2025-38512`, :cve_nist:`2025-38513`,
   :cve_nist:`2025-38514`, :cve_nist:`2025-38515`, :cve_nist:`2025-38516`, :cve_nist:`2025-38517`,
   :cve_nist:`2025-38518`, :cve_nist:`2025-38519`, :cve_nist:`2025-38520`, :cve_nist:`2025-38521`,
   :cve_nist:`2025-38522`, :cve_nist:`2025-38523`, :cve_nist:`2025-38524`, :cve_nist:`2025-38525`,
   :cve_nist:`2025-38526`, :cve_nist:`2025-38527`, :cve_nist:`2025-38528`, :cve_nist:`2025-38529`,
   :cve_nist:`2025-38530`, :cve_nist:`2025-38531`, :cve_nist:`2025-38532`, :cve_nist:`2025-38533`,
   :cve_nist:`2025-38534`, :cve_nist:`2025-38535`, :cve_nist:`2025-38536`, :cve_nist:`2025-38537`,
   :cve_nist:`2025-38538`, :cve_nist:`2025-38539`, :cve_nist:`2025-38540`, :cve_nist:`2025-38541`,
   :cve_nist:`2025-38542`, :cve_nist:`2025-38543`, :cve_nist:`2025-38544`, :cve_nist:`2025-38545`,
   :cve_nist:`2025-38546`, :cve_nist:`2025-38547`, :cve_nist:`2025-38548`, :cve_nist:`2025-38549`,
   :cve_nist:`2025-38550`, :cve_nist:`2025-38551`, :cve_nist:`2025-38552`, :cve_nist:`2025-38553`,
   :cve_nist:`2025-38554`, :cve_nist:`2025-38555`, :cve_nist:`2025-38556`, :cve_nist:`2025-38557`,
   :cve_nist:`2025-38558`, :cve_nist:`2025-38559`, :cve_nist:`2025-38560`, :cve_nist:`2025-38561`,
   :cve_nist:`2025-38562`, :cve_nist:`2025-38563`, :cve_nist:`2025-38564`, :cve_nist:`2025-38565`,
   :cve_nist:`2025-38566`, :cve_nist:`2025-38567`, :cve_nist:`2025-38568`, :cve_nist:`2025-38569`,
   :cve_nist:`2025-38570`, :cve_nist:`2025-38571`, :cve_nist:`2025-38572`, :cve_nist:`2025-38573`,
   :cve_nist:`2025-38574`, :cve_nist:`2025-38576`, :cve_nist:`2025-38577`, :cve_nist:`2025-38578`,
   :cve_nist:`2025-38579`, :cve_nist:`2025-38580`, :cve_nist:`2025-38581`, :cve_nist:`2025-38582`,
   :cve_nist:`2025-38583`, :cve_nist:`2025-38585`, :cve_nist:`2025-38586`, :cve_nist:`2025-38587`,
   :cve_nist:`2025-38588`, :cve_nist:`2025-38589`, :cve_nist:`2025-38590`, :cve_nist:`2025-38592`,
   :cve_nist:`2025-38593`, :cve_nist:`2025-38594`, :cve_nist:`2025-38595`, :cve_nist:`2025-38596`,
   :cve_nist:`2025-38598` and :cve_nist:`2025-38599`
-  linux-yocto/6.12: Ignore :cve_nist:`2025-38600`, :cve_nist:`2025-38601`, :cve_nist:`2025-38602`,
   :cve_nist:`2025-38603`, :cve_nist:`2025-38604`, :cve_nist:`2025-38606`, :cve_nist:`2025-38607`,
   :cve_nist:`2025-38608`, :cve_nist:`2025-38609`, :cve_nist:`2025-38610`, :cve_nist:`2025-38611`,
   :cve_nist:`2025-38612`, :cve_nist:`2025-38613`, :cve_nist:`2025-38614`, :cve_nist:`2025-38615`,
   :cve_nist:`2025-38616`, :cve_nist:`2025-38617`, :cve_nist:`2025-38618`, :cve_nist:`2025-38619`,
   :cve_nist:`2025-38620`, :cve_nist:`2025-38622`, :cve_nist:`2025-38623`, :cve_nist:`2025-38624`,
   :cve_nist:`2025-38625`, :cve_nist:`2025-38626`, :cve_nist:`2025-38628`, :cve_nist:`2025-38629`,
   :cve_nist:`2025-38630`, :cve_nist:`2025-38631`, :cve_nist:`2025-38632`, :cve_nist:`2025-38633`,
   :cve_nist:`2025-38634`, :cve_nist:`2025-38635`, :cve_nist:`2025-38638`, :cve_nist:`2025-38639`,
   :cve_nist:`2025-38640`, :cve_nist:`2025-38641`, :cve_nist:`2025-38642`, :cve_nist:`2025-38644`,
   :cve_nist:`2025-38645`, :cve_nist:`2025-38646`, :cve_nist:`2025-38647`, :cve_nist:`2025-38648`,
   :cve_nist:`2025-38649`, :cve_nist:`2025-38650`, :cve_nist:`2025-38651`, :cve_nist:`2025-38652`,
   :cve_nist:`2025-38653`, :cve_nist:`2025-38654`, :cve_nist:`2025-38655`, :cve_nist:`2025-38657`,
   :cve_nist:`2025-38658`, :cve_nist:`2025-38659`, :cve_nist:`2025-38660`, :cve_nist:`2025-38661`,
   :cve_nist:`2025-38662`, :cve_nist:`2025-38663`, :cve_nist:`2025-38664`, :cve_nist:`2025-38665`,
   :cve_nist:`2025-38666`, :cve_nist:`2025-38667`, :cve_nist:`2025-38668`, :cve_nist:`2025-38669`,
   :cve_nist:`2025-38670`, :cve_nist:`2025-38671`, :cve_nist:`2025-38672`, :cve_nist:`2025-38673`,
   :cve_nist:`2025-38674`, :cve_nist:`2025-38675`, :cve_nist:`2025-38676`, :cve_nist:`2025-38677`,
   :cve_nist:`2025-38679`, :cve_nist:`2025-38680`, :cve_nist:`2025-38681`, :cve_nist:`2025-38682`,
   :cve_nist:`2025-38683`, :cve_nist:`2025-38684`, :cve_nist:`2025-38685`, :cve_nist:`2025-38686`,
   :cve_nist:`2025-38687`, :cve_nist:`2025-38688`, :cve_nist:`2025-38689`, :cve_nist:`2025-38690`,
   :cve_nist:`2025-38691`, :cve_nist:`2025-38692`, :cve_nist:`2025-38693`, :cve_nist:`2025-38694`,
   :cve_nist:`2025-38695`, :cve_nist:`2025-38696`, :cve_nist:`2025-38697`, :cve_nist:`2025-38698`,
   :cve_nist:`2025-38699`, :cve_nist:`2025-38700`, :cve_nist:`2025-38701`, :cve_nist:`2025-38702`,
   :cve_nist:`2025-38703`, :cve_nist:`2025-38704`, :cve_nist:`2025-38705`, :cve_nist:`2025-38706`,
   :cve_nist:`2025-38707`, :cve_nist:`2025-38708`, :cve_nist:`2025-38709`, :cve_nist:`2025-38710`,
   :cve_nist:`2025-38711`, :cve_nist:`2025-38712`, :cve_nist:`2025-38713`, :cve_nist:`2025-38714`,
   :cve_nist:`2025-38715`, :cve_nist:`2025-38716`, :cve_nist:`2025-38717`, :cve_nist:`2025-38718`,
   :cve_nist:`2025-38719`, :cve_nist:`2025-38720`, :cve_nist:`2025-38721`, :cve_nist:`2025-38722`,
   :cve_nist:`2025-38723`, :cve_nist:`2025-38724`, :cve_nist:`2025-38725`, :cve_nist:`2025-38726`,
   :cve_nist:`2025-38727`, :cve_nist:`2025-38728`, :cve_nist:`2025-38729`, :cve_nist:`2025-38730`,
   :cve_nist:`2025-38731`, :cve_nist:`2025-38732`, :cve_nist:`2025-38733`, :cve_nist:`2025-38734`,
   :cve_nist:`2025-38735`, :cve_nist:`2025-38736`, :cve_nist:`2025-38737`, :cve_nist:`2025-39673`,
   :cve_nist:`2025-39674`, :cve_nist:`2025-39675`, :cve_nist:`2025-39676`, :cve_nist:`2025-39679`,
   :cve_nist:`2025-39680`, :cve_nist:`2025-39681`, :cve_nist:`2025-39682`, :cve_nist:`2025-39683`,
   :cve_nist:`2025-39684`, :cve_nist:`2025-39685`, :cve_nist:`2025-39686`, :cve_nist:`2025-39687`,
   :cve_nist:`2025-39689`, :cve_nist:`2025-39690`, :cve_nist:`2025-39691`, :cve_nist:`2025-39692`,
   :cve_nist:`2025-39693`, :cve_nist:`2025-39694`, :cve_nist:`2025-39695`, :cve_nist:`2025-39696`,
   :cve_nist:`2025-39697`, :cve_nist:`2025-39698`, :cve_nist:`2025-39699`, :cve_nist:`2025-39700`,
   :cve_nist:`2025-39701`, :cve_nist:`2025-39702`, :cve_nist:`2025-39703`, :cve_nist:`2025-39704`,
   :cve_nist:`2025-39705`, :cve_nist:`2025-39706`, :cve_nist:`2025-39707`, :cve_nist:`2025-39708`,
   :cve_nist:`2025-39709`, :cve_nist:`2025-39710`, :cve_nist:`2025-39711`, :cve_nist:`2025-39712`,
   :cve_nist:`2025-39713`, :cve_nist:`2025-39714`, :cve_nist:`2025-39715`, :cve_nist:`2025-39716`,
   :cve_nist:`2025-39717`, :cve_nist:`2025-39718`, :cve_nist:`2025-39719`, :cve_nist:`2025-39720`,
   :cve_nist:`2025-39721`, :cve_nist:`2025-39722`, :cve_nist:`2025-39723`, :cve_nist:`2025-39724`,
   :cve_nist:`2025-39725`, :cve_nist:`2025-39726`, :cve_nist:`2025-39727`, :cve_nist:`2025-39729`,
   :cve_nist:`2025-39730`, :cve_nist:`2025-39731`, :cve_nist:`2025-39732`, :cve_nist:`2025-39733`,
   :cve_nist:`2025-39734`, :cve_nist:`2025-39736`, :cve_nist:`2025-39737`, :cve_nist:`2025-39738`,
   :cve_nist:`2025-39739`, :cve_nist:`2025-39740`, :cve_nist:`2025-39741`, :cve_nist:`2025-39742`,
   :cve_nist:`2025-39743`, :cve_nist:`2025-39744`, :cve_nist:`2025-39746`, :cve_nist:`2025-39747`,
   :cve_nist:`2025-39748`, :cve_nist:`2025-39749`, :cve_nist:`2025-39750`, :cve_nist:`2025-39751`,
   :cve_nist:`2025-39752`, :cve_nist:`2025-39753`, :cve_nist:`2025-39754`, :cve_nist:`2025-39756`,
   :cve_nist:`2025-39757`, :cve_nist:`2025-39758`, :cve_nist:`2025-39759`, :cve_nist:`2025-39760`,
   :cve_nist:`2025-39761`, :cve_nist:`2025-39763`, :cve_nist:`2025-39765`, :cve_nist:`2025-39766`,
   :cve_nist:`2025-39767`, :cve_nist:`2025-39768`, :cve_nist:`2025-39769`, :cve_nist:`2025-39770`,
   :cve_nist:`2025-39771`, :cve_nist:`2025-39772`, :cve_nist:`2025-39773`, :cve_nist:`2025-39774`,
   :cve_nist:`2025-39775`, :cve_nist:`2025-39776`, :cve_nist:`2025-39777`, :cve_nist:`2025-39779`,
   :cve_nist:`2025-39780`, :cve_nist:`2025-39781`, :cve_nist:`2025-39782`, :cve_nist:`2025-39783`,
   :cve_nist:`2025-39784`, :cve_nist:`2025-39785`, :cve_nist:`2025-39786`, :cve_nist:`2025-39787`,
   :cve_nist:`2025-39788`, :cve_nist:`2025-39790`, :cve_nist:`2025-39791`, :cve_nist:`2025-39792`,
   :cve_nist:`2025-39793`, :cve_nist:`2025-39794`, :cve_nist:`2025-39795`, :cve_nist:`2025-39796`,
   :cve_nist:`2025-39797`, :cve_nist:`2025-39798` and :cve_nist:`2025-39799`
-  libxslt: Fix :cve_nist:`2025-7424`
-  pulseaudio: Ignore :cve_nist:`2024-11586`
-  tiff: Fix :cve_nist:`2024-13978`, :cve_nist:`2025-8176`, :cve_nist:`2025-8177`,
   :cve_nist:`2025-8534`, :cve_nist:`2025-8961` and :cve_nist:`2025-9165`
-  tiff: Ignore :cve_nist:`2025-8851`
-  vim: Fix :cve_nist:`2025-53905`, :cve_nist:`2025-53906`, :cve_nist:`2025-55157` and
   :cve_nist:`2025-55158`


Fixes in Yocto-5.2.4
~~~~~~~~~~~~~~~~~~~~

-  bash: use -std=gnu17 also for native :term:`CFLAGS`
-  binutils: Fix gprofng broken symbolic link with gp-*
-  bitbake: Use a "fork" multiprocessing context
-  bitbake: bitbake: Bump version to 2.12.1
-  bitbake: tests/fetch: Update tests after bitbake tag removal
-  build-appliance-image: Update to walnascar head revision
-  buildtools-tarball: fix unbound variable issues under 'set -u'
-  cve-update-db-native: Fix fetcher for CVEs missing nodes
-  default-distrovars.inc: Fix CONNECTIVITY_CHECK_URIS redirect issue
-  dev-manual/security-subjects.rst: update mailing lists
-  expat: upgrade to 2.7.2
-  ffmpeg: upgrade to 7.1.2
-  glib-2.0: update to 2.84.4
-  go: upgrade 1.24.6
-  gst-devtools: upgrade 1.24.13
-  gstreamer1.0-libav: upgrade 1.24.13
-  gstreamer1.0-plugins-bad: upgrade 1.24.13
-  gstreamer1.0-plugins-base: upgrade 1.24.13
-  gstreamer1.0-plugins-good: upgrade 1.24.13
-  gstreamer1.0-plugins-ugly: upgrade 1.24.13
-  gstreamer1.0-python: upgrade 1.24.13
-  gstreamer1.0-rtsp-server: upgrade 1.24.13
-  gstreamer1.0-vaapi: upgrade 1.24.13
-  gstreamer1.0: upgrade 1.24.13
-  lib/oe/utils: use multiprocessing from bb
-  libpcre2: upgrade 10.46
-  license.py: avoid deprecated ast.Str
-  linux-firmware: fix :term:`FILES` to drop :term:`RDEPENDS` on full package
-  linux-yocto/6.12: Revert "linux-yocto/6.12: riscv: Enable :term:`TUNE_FEATURES` based
   KERNEL_FEATURES"
-  linux-yocto/6.12: update to 6.12.47
-  migration-guides: add release notes for 4.0.29, 5.0.12 and 5.2.3
-  pkgconfig: fix build with gcc-15
-  poky.conf: bump version for 5.2.4
-  pulseaudio: Add audio group explicitly
-  python3-setuptools: restore build_scripts.executable support
-  ref-manual/variables.rst: expand :term:`IMAGE_OVERHEAD_FACTOR` glossary entry
-  rpm: correct tool path in macros for no usrmerge
-  rpm: keep leading '/' from sed operation
-  runqemu: fix special characters bug
-  rust-target-config: Add has-thread-local option
-  sanity.conf: Update minimum bitbake version to 2.12.1
-  sdk: The main in the C example should return an int
-  systemd-systemctl-native: Install systemd-sysv-install
-  systemd-systemctl-native: Use += instead of :append
-  systemd.bbclass: Make systemd_postinst run as intended
-  vim: upgrade 9.1.1652
-  vulnerabilities: update nvdcve file name
-  yocto-uninative: Update to 4.9 for glibc 2.42 and GCC 15.1


Known Issues in Yocto-5.2.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.2.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Antonin Godard
-  Archana Polampalli
-  Bruce Ashfield
-  Deepak Rathore
-  Haixiao Yan
-  Harish Sadineni
-  Hongxu Jia
-  Jan Vermaete
-  Joao Marcos Costa
-  Joshua Watt
-  Kyungjik Min
-  Lee Chee Yang
-  Libo Chen
-  Markus Kurz
-  Markus Volk
-  Martin Jansa
-  Mathieu Dubois-Briand
-  Michael Halstead
-  Patryk Seregiet
-  Per x Johansson
-  Peter Kjellerstedt
-  Peter Marko
-  Praveen Kumar
-  Ross Burton
-  Siddharth Doshi
-  Soumya Sambu
-  Steve Sakoman
-  Yi Zhao
-  Yogita Urade


Repositories / Downloads for Yocto-5.2.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`walnascar </poky/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2.4 </poky/log/?h=yocto-5.2.4>`
-  Git Revision: :yocto_git:`d0b46a6624ec9c61c47270745dd0b2d5abbe6ac1 </poky/commit/?id=d0b46a6624ec9c61c47270745dd0b2d5abbe6ac1>`
-  Release Artefact: poky-d0b46a6624ec9c61c47270745dd0b2d5abbe6ac1
-  sha: b5fa58650f7069c1c001f48aa8eeb12ab78b5f50a414de46df1a196fdc3be8cb
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.4/poky-d0b46a6624ec9c61c47270745dd0b2d5abbe6ac1.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.4/poky-d0b46a6624ec9c61c47270745dd0b2d5abbe6ac1.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`walnascar </openembedded-core/log/?h=walnascar>`
-  Tag:  :oe_git:`yocto-5.2.4 </openembedded-core/log/?h=yocto-5.2.4>`
-  Git Revision: :oe_git:`ff1c54df4e7b15df2e2c9fced59d9ad3e92ed565 </openembedded-core/commit/?id=ff1c54df4e7b15df2e2c9fced59d9ad3e92ed565>`
-  Release Artefact: oecore-ff1c54df4e7b15df2e2c9fced59d9ad3e92ed565
-  sha: a348eb7c759cc02c1415d2b506903ac1d5530708c9b47550a4ae8c8d86c75728
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.4/oecore-ff1c54df4e7b15df2e2c9fced59d9ad3e92ed565.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.4/oecore-ff1c54df4e7b15df2e2c9fced59d9ad3e92ed565.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`walnascar </meta-mingw/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2.4 </meta-mingw/log/?h=yocto-5.2.4>`
-  Git Revision: :yocto_git:`edce693e1b8fabd84651aa6c0888aafbcf238577 </meta-mingw/commit/?id=edce693e1b8fabd84651aa6c0888aafbcf238577>`
-  Release Artefact: meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577
-  sha: 6cfed41b54f83da91a6cf201ec1c2cd4ac284f642b1268c8fa89d2335ea2bce1
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.4/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.4/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.12 </bitbake/log/?h=2.12>`
-  Tag:  :oe_git:`yocto-5.2.4 </bitbake/log/?h=yocto-5.2.4>`
-  Git Revision: :oe_git:`ac097300921590ed6a814f2c3fa08a59f4ded92d </bitbake/commit/?id=ac097300921590ed6a814f2c3fa08a59f4ded92d>`
-  Release Artefact: bitbake-ac097300921590ed6a814f2c3fa08a59f4ded92d
-  sha: ad8afd956f5378691172317c95bdd1c098d6144a7269975d7d3230707305e88a
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.4/bitbake-ac097300921590ed6a814f2c3fa08a59f4ded92d.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.4/bitbake-ac097300921590ed6a814f2c3fa08a59f4ded92d.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`walnascar </meta-yocto/log/?h=walnascar>`
-  Tag: :yocto_git:`yocto-5.2.4 </meta-yocto/log/?h=yocto-5.2.4>`
-  Git Revision: :yocto_git:`0993c45a1f78f302fd40c78a2a1f709daa7a0ae0 </meta-yocto/commit/?id=0993c45a1f78f302fd40c78a2a1f709daa7a0ae0>`

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`walnascar </yocto-docs/log/?h=walnascar>`
-  Tag: :yocto_git:`yocto-5.2.4 </yocto-docs/log/?h=yocto-5.2.4>`
-  Git Revision: :yocto_git:`29330751c8a2b82b4bd80659d2a0a8bac51afca5 </yocto-docs/commit/?id=29330751c8a2b82b4bd80659d2a0a8bac51afca5>`

