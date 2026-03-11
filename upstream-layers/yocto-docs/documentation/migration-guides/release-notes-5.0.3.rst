.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.3 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve_nist:`2024-0760`, :cve_nist:`2024-1737`, :cve_nist:`2024-1975` and :cve_nist:`2024-4076`
-  busybox: Fix :cve_nist:`2023-42366`, :cve_nist:`2023-42364`, :cve_nist:`2023-42365`, :cve_nist:`2021-42380` and :cve_nist:`2023-42363`
-  cpio: Ignore :cve_nist:`2023-7216`
-  curl: Fix :cve_nist:`2024-6197`
-  ffmpeg: Fix :cve_nist:`2023-49502`, :cve_nist:`2024-31578` and :cve_nist:`2024-31582`
-  ghostscript: Fix :cve_nist:`2023-52722`
-  go: Fix :cve_nist:`2024-24790`
-  gstreamer1.0-plugins-base: Fix :cve_nist:`2024-4453`
-  less: Fix :cve_nist:`2024-32487`
-  libxml2: Fix :cve_nist:`2024-34459`
-  libyaml: Ignore :cve_nist:`2024-35328`
-  linux-yocto/6.6: Fix :cve_nist:`2024-23307`, :cve_nist:`2024-24861`, :cve_nist:`2024-26642`, :cve_nist:`2024-26643`, :cve_nist:`2024-26654`, :cve_nist:`2024-26656` and :cve_nist:`2023-47233`
-  linux-yocto/6.6: Ignore :cve_nist:`2019-25160`, :cve_nist:`2019-25162`, :cve_nist:`2020-36775`, :cve_nist:`2020-36776`, :cve_nist:`2020-36777`, :cve_nist:`2020-36778`, :cve_nist:`2020-36779`, :cve_nist:`2020-36780`, :cve_nist:`2020-36781`, :cve_nist:`2020-36782`, :cve_nist:`2020-36783`, :cve_nist:`2020-36784`, :cve_nist:`2020-36785`, :cve_nist:`2020-36786`, :cve_nist:`2020-36787`, :cve_nist:`2021-46904`, :cve_nist:`2021-46905`, :cve_nist:`2021-46906`, :cve_nist:`2021-46908`, :cve_nist:`2021-46909`, :cve_nist:`2021-46910`, :cve_nist:`2021-46911`, :cve_nist:`2021-46912`, :cve_nist:`2021-46913`, :cve_nist:`2021-46914`, :cve_nist:`2021-46915`, :cve_nist:`2021-46916`, :cve_nist:`2021-46917`, :cve_nist:`2021-46918`, :cve_nist:`2021-46919`, :cve_nist:`2021-46920`, :cve_nist:`2021-46921`, :cve_nist:`2021-46922`, :cve_nist:`2021-46923`, :cve_nist:`2021-46924`, :cve_nist:`2021-46925`, :cve_nist:`2021-46926`, :cve_nist:`2021-46927`, :cve_nist:`2021-46928`, :cve_nist:`2021-46929`, :cve_nist:`2021-46930`, :cve_nist:`2021-46931`, :cve_nist:`2021-46932`, :cve_nist:`2021-46933`, :cve_nist:`2021-46934`, :cve_nist:`2021-46935`, :cve_nist:`2021-46936`, :cve_nist:`2021-46937`, :cve_nist:`2021-46938`, :cve_nist:`2021-46939`, :cve_nist:`2021-46940`, :cve_nist:`2021-46941`, :cve_nist:`2021-46942`, :cve_nist:`2021-46943`, :cve_nist:`2021-46944`, :cve_nist:`2021-46945`, :cve_nist:`2021-46947`, :cve_nist:`2021-46948`, :cve_nist:`2021-46949`, :cve_nist:`2021-46950`, :cve_nist:`2021-46951`, :cve_nist:`2021-46952`, :cve_nist:`2021-46953`, :cve_nist:`2021-46954`, :cve_nist:`2021-46955`, :cve_nist:`2021-46956`, :cve_nist:`2021-46957`, :cve_nist:`2021-46958`, :cve_nist:`2021-46959`, :cve_nist:`2021-46960`, :cve_nist:`2021-46961`, :cve_nist:`2021-46962`, :cve_nist:`2021-46963`, :cve_nist:`2021-46964`, :cve_nist:`2021-46965`, :cve_nist:`2021-46966`, :cve_nist:`2021-46967`, :cve_nist:`2021-46968`, :cve_nist:`2021-46969`, :cve_nist:`2021-46970`, :cve_nist:`2021-46971`, :cve_nist:`2021-46972`, :cve_nist:`2021-46973`, :cve_nist:`2021-46974`, :cve_nist:`2021-46976`, :cve_nist:`2021-46977`, :cve_nist:`2021-46978`, :cve_nist:`2021-46979`, :cve_nist:`2021-46980`, :cve_nist:`2021-46981`, :cve_nist:`2021-46982`, :cve_nist:`2021-46983`, :cve_nist:`2021-46984`, :cve_nist:`2021-46985`, :cve_nist:`2021-46986`, :cve_nist:`2021-46987`, :cve_nist:`2021-46988`, :cve_nist:`2021-46989`, :cve_nist:`2021-46990`, :cve_nist:`2021-46991`, :cve_nist:`2021-46992`, :cve_nist:`2021-46993`, :cve_nist:`2021-46994`, :cve_nist:`2021-46995`, :cve_nist:`2021-46996`, :cve_nist:`2021-46997`, :cve_nist:`2021-46998`, :cve_nist:`2021-46999`, :cve_nist:`2021-47000`, :cve_nist:`2021-47001`, :cve_nist:`2021-47002`, :cve_nist:`2021-47003`, :cve_nist:`2021-47004`, :cve_nist:`2021-47005`, :cve_nist:`2021-47006`, :cve_nist:`2021-47007`, :cve_nist:`2021-47008`, :cve_nist:`2021-47009`, :cve_nist:`2021-47010`, :cve_nist:`2021-47011`, :cve_nist:`2021-47012`, :cve_nist:`2021-47013`, :cve_nist:`2021-47014`, :cve_nist:`2021-47015`, :cve_nist:`2021-47016`, :cve_nist:`2021-47017`, :cve_nist:`2021-47018`, :cve_nist:`2021-47019`, :cve_nist:`2021-47020`, :cve_nist:`2021-47021`, :cve_nist:`2021-47022`, :cve_nist:`2021-47023`, :cve_nist:`2021-47024`, :cve_nist:`2021-47025`, :cve_nist:`2021-47026`, :cve_nist:`2021-47027`, :cve_nist:`2021-47028`, :cve_nist:`2021-47029`, :cve_nist:`2021-47030`, :cve_nist:`2021-47031`, :cve_nist:`2021-47032`, :cve_nist:`2021-47033`, :cve_nist:`2021-47034`, :cve_nist:`2021-47035`, :cve_nist:`2021-47036`, :cve_nist:`2021-47037`, :cve_nist:`2021-47038`, :cve_nist:`2021-47039`, :cve_nist:`2021-47040`, :cve_nist:`2021-47041`, :cve_nist:`2021-47042`, :cve_nist:`2021-47043`, :cve_nist:`2021-47044`, :cve_nist:`2021-47045`, :cve_nist:`2021-47046`, :cve_nist:`2021-47047`, :cve_nist:`2021-47048`, :cve_nist:`2021-47049`, :cve_nist:`2021-47050`, :cve_nist:`2021-47051`, :cve_nist:`2021-47052`, :cve_nist:`2021-47053`, :cve_nist:`2021-47054`, :cve_nist:`2021-47055`, :cve_nist:`2021-47056`, :cve_nist:`2021-47057`, :cve_nist:`2021-47058`, :cve_nist:`2021-47059`, :cve_nist:`2021-47060`, :cve_nist:`2021-47061`, :cve_nist:`2021-47062`, :cve_nist:`2021-47063`, :cve_nist:`2021-47064`, :cve_nist:`2021-47065`, :cve_nist:`2021-47066`, :cve_nist:`2021-47067`, :cve_nist:`2021-47068`, :cve_nist:`2021-47069`, :cve_nist:`2021-47070`, :cve_nist:`2021-47071`, :cve_nist:`2021-47072`, :cve_nist:`2021-47073`, :cve_nist:`2021-47074`, :cve_nist:`2021-47075`, :cve_nist:`2021-47076`, :cve_nist:`2021-47077`, :cve_nist:`2021-47078`, :cve_nist:`2021-47079`, :cve_nist:`2021-47080`, :cve_nist:`2021-47081`, :cve_nist:`2021-47082`, :cve_nist:`2021-47083`, :cve_nist:`2021-47086`, :cve_nist:`2021-47087`, :cve_nist:`2021-47088`, :cve_nist:`2021-47089`, :cve_nist:`2021-47090`, :cve_nist:`2021-47091`, :cve_nist:`2021-47092`, :cve_nist:`2021-47093`, :cve_nist:`2021-47094`, :cve_nist:`2021-47095`, :cve_nist:`2021-47096`, :cve_nist:`2021-47097`, :cve_nist:`2021-47098`, :cve_nist:`2021-47099`, :cve_nist:`2021-47100`, :cve_nist:`2021-47101`, :cve_nist:`2021-47102`, :cve_nist:`2021-47103`, :cve_nist:`2021-47104`, :cve_nist:`2021-47105`, :cve_nist:`2021-47106`, :cve_nist:`2021-47107`, :cve_nist:`2021-47108`, :cve_nist:`2021-47109`, :cve_nist:`2021-47110`, :cve_nist:`2021-47111`, :cve_nist:`2021-47112`, :cve_nist:`2021-47113`, :cve_nist:`2021-47114`, :cve_nist:`2021-47116`, :cve_nist:`2021-47117`, :cve_nist:`2021-47118`, :cve_nist:`2021-47119`, :cve_nist:`2021-47120`, :cve_nist:`2021-47121`, :cve_nist:`2021-47122`, :cve_nist:`2021-47123`, :cve_nist:`2021-47124`, :cve_nist:`2021-47125`, :cve_nist:`2021-47126`, :cve_nist:`2021-47127`, :cve_nist:`2021-47128`, :cve_nist:`2021-47129`, :cve_nist:`2021-47130`, :cve_nist:`2021-47131`, :cve_nist:`2021-47132`, :cve_nist:`2021-47133`, :cve_nist:`2021-47134`, :cve_nist:`2021-47135`, :cve_nist:`2021-47136`, :cve_nist:`2021-47137`, :cve_nist:`2021-47138`, :cve_nist:`2021-47139`, :cve_nist:`2021-47140`, :cve_nist:`2021-47141`, :cve_nist:`2021-47142`, :cve_nist:`2021-47143`, :cve_nist:`2021-47144`, :cve_nist:`2021-47145`, :cve_nist:`2021-47146`, :cve_nist:`2021-47147`, :cve_nist:`2021-47148`, :cve_nist:`2021-47149`, :cve_nist:`2021-47150`, :cve_nist:`2021-47151`, :cve_nist:`2021-47152`, :cve_nist:`2021-47153`, :cve_nist:`2021-47158`, :cve_nist:`2021-47159`, :cve_nist:`2021-47160`, :cve_nist:`2021-47161`, :cve_nist:`2021-47162`, :cve_nist:`2021-47163`, :cve_nist:`2021-47164`, :cve_nist:`2021-47165`, :cve_nist:`2021-47166`, :cve_nist:`2021-47167`, :cve_nist:`2021-47168`, :cve_nist:`2021-47169`, :cve_nist:`2021-47170`, :cve_nist:`2021-47171`, :cve_nist:`2021-47172`, :cve_nist:`2021-47173`, :cve_nist:`2021-47174`, :cve_nist:`2021-47175`, :cve_nist:`2021-47176`, :cve_nist:`2021-47177`, :cve_nist:`2021-47178`, :cve_nist:`2021-47179`, :cve_nist:`2021-47180`, :cve_nist:`2022-48626`, :cve_nist:`2022-48627`, :cve_nist:`2022-48628`, :cve_nist:`2022-48629` and :cve_nist:`2022-48630`
-  linux-yocto/6.6 (cont.): Ignore :cve_nist:`2023-6270`, :cve_nist:`2023-6356`, :cve_nist:`2023-6536`, :cve_nist:`2023-7042`, :cve_nist:`2023-28746`, :cve_nist:`2023-52465`, :cve_nist:`2023-52467`, :cve_nist:`2023-52468`, :cve_nist:`2023-52469`, :cve_nist:`2023-52470`, :cve_nist:`2023-52471`, :cve_nist:`2023-52472`, :cve_nist:`2023-52473`, :cve_nist:`2023-52474`, :cve_nist:`2023-52475`, :cve_nist:`2023-52476`, :cve_nist:`2023-52477`, :cve_nist:`2023-52478`, :cve_nist:`2023-52479`, :cve_nist:`2023-52480`, :cve_nist:`2023-52481`, :cve_nist:`2023-52482`, :cve_nist:`2023-52483`, :cve_nist:`2023-52484`, :cve_nist:`2023-52486`, :cve_nist:`2023-52487`, :cve_nist:`2023-52488`, :cve_nist:`2023-52489`, :cve_nist:`2023-52490`, :cve_nist:`2023-52491`, :cve_nist:`2023-52492`, :cve_nist:`2023-52493`, :cve_nist:`2023-52494`, :cve_nist:`2023-52495`, :cve_nist:`2023-52497`, :cve_nist:`2023-52498`, :cve_nist:`2023-52499`, :cve_nist:`2023-52500`, :cve_nist:`2023-52501`, :cve_nist:`2023-52502`, :cve_nist:`2023-52503`, :cve_nist:`2023-52504`, :cve_nist:`2023-52505`, :cve_nist:`2023-52506`, :cve_nist:`2023-52507`, :cve_nist:`2023-52508`, :cve_nist:`2023-52509`, :cve_nist:`2023-52510`, :cve_nist:`2023-52511`, :cve_nist:`2023-52512`, :cve_nist:`2023-52513`, :cve_nist:`2023-52515`, :cve_nist:`2023-52516`, :cve_nist:`2023-52517`, :cve_nist:`2023-52518`, :cve_nist:`2023-52519`, :cve_nist:`2023-52520`, :cve_nist:`2023-52522`, :cve_nist:`2023-52523`, :cve_nist:`2023-52524`, :cve_nist:`2023-52525`, :cve_nist:`2023-52526`, :cve_nist:`2023-52527`, :cve_nist:`2023-52528`, :cve_nist:`2023-52529`, :cve_nist:`2023-52530`, :cve_nist:`2023-52531`, :cve_nist:`2023-52532`, :cve_nist:`2023-52559`, :cve_nist:`2023-52560`, :cve_nist:`2023-52561`, :cve_nist:`2023-52562`, :cve_nist:`2023-52563`, :cve_nist:`2023-52564`, :cve_nist:`2023-52565`, :cve_nist:`2023-52566`, :cve_nist:`2023-52567`, :cve_nist:`2023-52568`, :cve_nist:`2023-52569`, :cve_nist:`2023-52570`, :cve_nist:`2023-52571`, :cve_nist:`2023-52572`, :cve_nist:`2023-52573`, :cve_nist:`2023-52574`, :cve_nist:`2023-52575`, :cve_nist:`2023-52576`, :cve_nist:`2023-52577`, :cve_nist:`2023-52578`, :cve_nist:`2023-52580`, :cve_nist:`2023-52581`, :cve_nist:`2023-52582`, :cve_nist:`2023-52583`, :cve_nist:`2023-52584`, :cve_nist:`2023-52587`, :cve_nist:`2023-52588`, :cve_nist:`2023-52589`, :cve_nist:`2023-52591`, :cve_nist:`2023-52593`, :cve_nist:`2023-52594`, :cve_nist:`2023-52595`, :cve_nist:`2023-52596`, :cve_nist:`2023-52597`, :cve_nist:`2023-52598`, :cve_nist:`2023-52599`, :cve_nist:`2023-52600`, :cve_nist:`2023-52601`, :cve_nist:`2023-52602`, :cve_nist:`2023-52603`, :cve_nist:`2023-52604`, :cve_nist:`2023-52606`, :cve_nist:`2023-52607`, :cve_nist:`2023-52608`, :cve_nist:`2023-52609`, :cve_nist:`2023-52610`, :cve_nist:`2023-52611`, :cve_nist:`2023-52612`, :cve_nist:`2023-52613`, :cve_nist:`2023-52614`, :cve_nist:`2023-52615`, :cve_nist:`2023-52616`, :cve_nist:`2023-52617`, :cve_nist:`2023-52618`, :cve_nist:`2023-52619`, :cve_nist:`2023-52620`, :cve_nist:`2023-52621`, :cve_nist:`2023-52622`, :cve_nist:`2023-52623`, :cve_nist:`2023-52626`, :cve_nist:`2023-52627`, :cve_nist:`2023-52628`, :cve_nist:`2023-52629`, :cve_nist:`2023-52630`, :cve_nist:`2023-52631`, :cve_nist:`2023-52632`, :cve_nist:`2023-52633`, :cve_nist:`2023-52635`, :cve_nist:`2023-52636`, :cve_nist:`2023-52637`, :cve_nist:`2023-52638`, :cve_nist:`2023-52639`, :cve_nist:`2023-52640`, :cve_nist:`2023-52641`, :cve_nist:`2024-0841`, :cve_nist:`2024-22099`, :cve_nist:`2024-23196`, :cve_nist:`2024-26600`, :cve_nist:`2024-26601`, :cve_nist:`2024-26602`, :cve_nist:`2024-26603`, :cve_nist:`2024-26604`, :cve_nist:`2024-26605`, :cve_nist:`2024-26606`, :cve_nist:`2024-26607`, :cve_nist:`2024-26608`, :cve_nist:`2024-26610`, :cve_nist:`2024-26611`, :cve_nist:`2024-26612`, :cve_nist:`2024-26614`, :cve_nist:`2024-26615`, :cve_nist:`2024-26616`, :cve_nist:`2024-26617`, :cve_nist:`2024-26618`, :cve_nist:`2024-26619`, :cve_nist:`2024-26620`, :cve_nist:`2024-26621`, :cve_nist:`2024-26622`, :cve_nist:`2024-26623`, :cve_nist:`2024-26625`, :cve_nist:`2024-26626`, :cve_nist:`2024-26627`, :cve_nist:`2024-26629`, :cve_nist:`2024-26630`, :cve_nist:`2024-26631`, :cve_nist:`2024-26632`, :cve_nist:`2024-26633`, :cve_nist:`2024-26634`, :cve_nist:`2024-26635`, :cve_nist:`2024-26636`, :cve_nist:`2024-26637`, :cve_nist:`2024-26638`, :cve_nist:`2024-26639`, :cve_nist:`2024-26640`, :cve_nist:`2024-26641`, :cve_nist:`2024-26644`, :cve_nist:`2024-26645`, :cve_nist:`2024-26646`, :cve_nist:`2024-26647`, :cve_nist:`2024-26648`, :cve_nist:`2024-26649`, :cve_nist:`2024-26650`, :cve_nist:`2024-26651`, :cve_nist:`2024-26652`, :cve_nist:`2024-26653`, :cve_nist:`2024-26657`, :cve_nist:`2024-26659`, :cve_nist:`2024-26660`, :cve_nist:`2024-26661`, :cve_nist:`2024-26662`, :cve_nist:`2024-26663`, :cve_nist:`2024-26664`, :cve_nist:`2024-26665`, :cve_nist:`2024-26666`, :cve_nist:`2024-26667`, :cve_nist:`2024-26668`, :cve_nist:`2024-26669`, :cve_nist:`2024-26670`, :cve_nist:`2024-26671`, :cve_nist:`2024-26673`, :cve_nist:`2024-26674`, :cve_nist:`2024-26675`, :cve_nist:`2024-26676`, :cve_nist:`2024-26677`, :cve_nist:`2024-26678`, :cve_nist:`2024-26679`, :cve_nist:`2024-26680`, :cve_nist:`2024-26681`, :cve_nist:`2024-26682`, :cve_nist:`2024-26683`, :cve_nist:`2024-26684`, :cve_nist:`2024-26685`, :cve_nist:`2024-26687`, :cve_nist:`2024-26688`, :cve_nist:`2024-26689`, :cve_nist:`2024-26690`, :cve_nist:`2024-26691`, :cve_nist:`2024-26692`, :cve_nist:`2024-26693`, :cve_nist:`2024-26694`, :cve_nist:`2024-26695`, :cve_nist:`2024-26696`, :cve_nist:`2024-26697`, :cve_nist:`2024-26698`, :cve_nist:`2024-26700`, :cve_nist:`2024-26702`, :cve_nist:`2024-26703`, :cve_nist:`2024-26704`, :cve_nist:`2024-26705`, :cve_nist:`2024-26706`, :cve_nist:`2024-26707`, :cve_nist:`2024-26708`, :cve_nist:`2024-26709`, :cve_nist:`2024-26710`, :cve_nist:`2024-26711`, :cve_nist:`2024-26712`, :cve_nist:`2024-26713`, :cve_nist:`2024-26714`, :cve_nist:`2024-26715`, :cve_nist:`2024-26716`, :cve_nist:`2024-26717`, :cve_nist:`2024-26718`, :cve_nist:`2024-26719`, :cve_nist:`2024-26720`, :cve_nist:`2024-26721`, :cve_nist:`2024-26722`, :cve_nist:`2024-26723`, :cve_nist:`2024-26724`, :cve_nist:`2024-26725`, :cve_nist:`2024-26726`, :cve_nist:`2024-26727`, :cve_nist:`2024-26728`, :cve_nist:`2024-26729`, :cve_nist:`2024-26730`, :cve_nist:`2024-26731`, :cve_nist:`2024-26732`, :cve_nist:`2024-26733`, :cve_nist:`2024-26734`, :cve_nist:`2024-26735`, :cve_nist:`2024-26736`, :cve_nist:`2024-26737`, :cve_nist:`2024-26738`, :cve_nist:`2024-26739`, :cve_nist:`2024-26740`, :cve_nist:`2024-26741`, :cve_nist:`2024-26742`, :cve_nist:`2024-26743`, :cve_nist:`2024-26744`, :cve_nist:`2024-26745`, :cve_nist:`2024-26746`, :cve_nist:`2024-26747`, :cve_nist:`2024-26748`, :cve_nist:`2024-26749`, :cve_nist:`2024-26750`, :cve_nist:`2024-26751`, :cve_nist:`2024-26752`, :cve_nist:`2024-26753`, :cve_nist:`2024-26754`, :cve_nist:`2024-26755`, :cve_nist:`2024-26759`, :cve_nist:`2024-26760`, :cve_nist:`2024-26761`, :cve_nist:`2024-26762`, :cve_nist:`2024-26763`, :cve_nist:`2024-26764`, :cve_nist:`2024-26765`, :cve_nist:`2024-26766`, :cve_nist:`2024-26767`, :cve_nist:`2024-26768`, :cve_nist:`2024-26769`, :cve_nist:`2024-26770`, :cve_nist:`2024-26771`, :cve_nist:`2024-26772`, :cve_nist:`2024-26773`, :cve_nist:`2024-26774`, :cve_nist:`2024-26775`, :cve_nist:`2024-26776`, :cve_nist:`2024-26777`, :cve_nist:`2024-26778`, :cve_nist:`2024-26779`, :cve_nist:`2024-26780`, :cve_nist:`2024-26781`, :cve_nist:`2024-26782`, :cve_nist:`2024-26783`, :cve_nist:`2024-26786`, :cve_nist:`2024-26787`, :cve_nist:`2024-26788`, :cve_nist:`2024-26789`, :cve_nist:`2024-26790`, :cve_nist:`2024-26791`, :cve_nist:`2024-26792`, :cve_nist:`2024-26793`, :cve_nist:`2024-26794`, :cve_nist:`2024-26795`, :cve_nist:`2024-26796`, :cve_nist:`2024-26798`, :cve_nist:`2024-26799`, :cve_nist:`2024-26800`, :cve_nist:`2024-26801`, :cve_nist:`2024-26802`, :cve_nist:`2024-26803`, :cve_nist:`2024-26804`, :cve_nist:`2024-26805`, :cve_nist:`2024-26807`, :cve_nist:`2024-26808` and :cve_nist:`2024-26809`
-  llvm: Fix :cve_nist:`2024-0151`
-  ofono: Fix :cve_nist:`2023-2794`
-  openssh: Fix :cve_nist:`2024-6387` and :cve_nist:`2024-39894`
-  openssl: Fix :cve_nist:`2024-5535`
-  pam: Fix :cve_nist:`2024-22365`
-  python3-idna: Fix :cve_nist:`2024-3651`
-  qemu: Fix :cve_nist:`2023-6683`, :cve_nist:`2024-3446`, :cve_mitre:`2024-3447`, :cve_nist:`2024-3567`, :cve_nist:`2024-26327` and :cve_nist:`2024-26328`
-  ruby: Fix :cve_nist:`2023-36617` and :cve_nist:`2024-27281`
-  vte: Fix :cve_nist:`2024-37535`
-  wget: Fix for :cve_nist:`2024-38428`


Fixes in Yocto-5.0.3
~~~~~~~~~~~~~~~~~~~~

-  apt-native: don't let dpkg overwrite files by default
-  archiver.bbclass: Fix work-shared checking for kernel recipes
-  automake: mark new_rt_path_for_test-driver.patch as Inappropriate
-  bash: fix configure checks that fail with GCC 14.1
-  bind: upgrade to 9.18.28
-  binutils: stable 2.42 branch updates
-  bitbake: codeparser/data: Ensure module function contents changing is accounted for
-  bitbake: codeparser: Skip non-local functions for module dependencies
-  build-appliance-image: Update to scarthgap head revision
-  cargo: remove True option to getVar calls
-  classes/create-spdx-2.2: Fix :term:`SPDX` Namespace Prefix
-  classes/kernel: No symlink in postinst without KERNEL_IMAGETYPE_SYMLINK
-  cmake-qemu.bbclass: fix if criterion
-  create-spdx-3.0/populate_sdk_base: Add SDK_CLASSES inherit mechanism to fix tarball :term:`SPDX` manifests
-  create-spdx-'*': Support multilibs via SPDX_MULTILIB_SSTATE_ARCHS
-  curl: correct the :term:`PACKAGECONFIG` for native/nativesdk
-  curl: locale-base-en-us isn't glibc-specific
-  curl: skip FTP tests in run-ptest
-  cve-check: Introduce CVE_CHECK_MANIFEST_JSON_SUFFIX
-  cve-exclusion: Drop the version comparision/warning
-  devtool: ide-sdk: correct help typo
-  dnf: Fix missing leading whitespace with ':append'
-  dpkg: mark patches adding custom non-debian architectures as inappropriate for upstream
-  ed: upgrade to 1.20.2
-  expect: fix configure with GCC 14
-  ffmpeg: backport patch to fix errors with GCC 14
-  ffmpeg: backport patches to use new Vulkan AV1 codec API
-  flac: fix buildpaths warnings
-  fribidi: upgrade to 1.0.14
-  gawk: Remove References to /usr/local/bin/gawk
-  gawk: update patch status
-  gettext: fix a parallel build issue
-  ghostscript: upgrade to 10.03.1
-  glib-networking: submit eagain.patch upstream
-  glibc: cleanup old cve status
-  glibc: stable 2.39 branch updates
-  glslang: mark 0001-generate-glslang-pkg-config.patch as Inappropriate
-  go: drop the old 1.4 bootstrap C version
-  go: upgrade to 1.22.5
-  gpgme: move gpgme-tool to own sub-package
-  grub,grub-efi: Remove -mfpmath=sse on x86
-  grub: mark grub-module-explicitly-keeps-symbole-.module_license.patch as a workaround
-  gstreamer1.0: skip another known flaky test
-  gstreamer: upgrade to 1.22.12
-  insane.bbclass: fix `HOST_` variable names
-  insane.bbclass: remove leftover variables and comment
-  insane.bbclass: remove skipping of cross-compiled packages
-  insane: handle dangling symlinks in the libdir QA check
-  iptables: fix memory corruption when parsing nft rules
-  iptables: fix save/restore symlinks with libnftnl :term:`PACKAGECONFIG` enabled
-  iptables: submit 0001-configure-Add-option-to-enable-disable-libnfnetlink.patch upstream
-  kexec-tools: submit 0003-kexec-ARM-Fix-add_buffer_phys_virt-align-issue.patch upstream
-  layer.conf: Add os-release to :term:`SIGGEN_EXCLUDERECIPES_ABISAFE`
-  libacpi: mark patches as inactive-upstream
-  libadwaita: upgrade to 1.5.1
-  libcap-ng-python: upgrade to 0.8.5
-  libcap-ng: upgrade to 0.8.5
-  libmnl: explicitly disable doxygen
-  libnl: change :term:`HOMEPAGE`
-  libpam: fix runtime error in pam_pwhistory moudle
-  libpng: update :term:`SRC_URI`
-  libportal: fix rare build race
-  libstd-rs: set :term:`CVE_PRODUCT` to rust
-  libxcrypt: correct the check for a working libucontext.h
-  libxml2: upgrade to 2.12.8
-  linux-yocto-custom: Fix comment override syntax
-  linux-yocto/6.6: cfg: drop obselete options
-  linux-yocto/6.6: cfg: introduce Intel NPU fragment
-  linux-yocto/6.6: fix AMD boot trace
-  linux-yocto/6.6: fix kselftest failures
-  linux-yocto/6.6: intel configuration changes
-  linux-yocto/6.6: nft: enable veth
-  linux-yocto/6.6: update to v6.6.35
-  linux-yocto: Enable team net driver
-  linuxloader: add -armhf on arm only for :term:`TARGET_FPU` 'hard'
-  llvm: upgrade to 18.1.6
-  maintainers.inc: update self e-mail address
-  maintainers: Drop go-native as recipe removed
-  mesa: Fix missing leading whitespace with ':append'
-  mesa: remove obsolete 0001-meson.build-check-for-all-linux-host_os-combinations.patch
-  mesa: upgrade to 24.0.7
-  meson: don't use deprecated pkgconfig variable
-  migration-guides: add release notes for 4.0.19
-  migration-guides: add release notes for 5.0.2
-  migration-notes: add release notes for 5.0.1
-  mmc-utils: fix URL
-  mobile-broadband-provider-info: upgrade to 20240407
-  multilib.bbclass: replace deprecated e.data with d
-  multilib.conf: remove appending to :term:`PKG_CONFIG_PATH`
-  nasm: upgrade to 2.16.03
-  ncurses: switch to new mirror
-  oeqa/runtime/scp: requires openssh-sftp-server
-  oeqa/runtime: fix race-condition in minidebuginfo test
-  oeqa/runtime: fix regression in minidebuginfo test
-  oeqa/runtime: make minidebuginfo test work with coreutils
-  oeqa/sdk/case: Ensure :term:`DL_DIR` is populated with artefacts if used
-  oeqa/sdk/case: Skip SDK test cases when :term:`TCLIBC` is newlib
-  oeqa/selftest/devtool: Fix for usrmerge in :term:`DISTRO_FEATURES`
-  oeqa/selftest/recipetool: Fix for usrmerge in :term:`DISTRO_FEATURES`
-  openssh: drop rejected patch fixed in 8.6p1 release
-  openssh: systemd notification was implemented upstream
-  openssh: systemd sd-notify patch was rejected upstream
-  orc: upgrade to 0.4.39
-  package.py: Fix static debuginfo split
-  package.py: Fix static library processing
-  pcmanfm: Disable incompatible-pointer-types warning as error
-  perl: submit the rest of determinism.patch upstream
-  pixman: fixing inline failure with -Og
-  poky.conf: bump version for 5.0.3
-  populate_sdk_ext.bbclass: Fix undefined variable error
-  pseudo: Fix to work with glibc 2.40
-  pseudo: Update to include open symlink handling bugfix
-  pseudo: Update to pull in python 3.12+ fix
-  python3-attrs: drop python3-ctypes from :term:`RDEPENDS`
-  python3-bcrypt: drop python3-six from :term:`RDEPENDS`
-  python3-idna: upgrade to 3.7
-  python3-jinja2: upgrade to 3.1.4
-  python3-pyopenssl: drop python3-six from :term:`RDEPENDS`
-  python3-requests: cleanup :term:`RDEPENDS`
-  python3-setuptools: drop python3-2to3 from :term:`RDEPENDS`
-  python3: Treat UID/GID overflow as failure
-  python3: skip test_concurrent_futures/test_deadlock
-  python3: skip test_multiprocessing/test_active_children test
-  python3: submit deterministic_imports.patch upstream as a ticket
-  python3: upgrade to 3.12.4
-  qemu: upgrade to 8.2.3
-  rng-tools: ignore incompatible-pointer-types errors for now
-  rt-tests: rt_bmark.py: fix TypeError
-  rust-cross-canadian: set :term:`CVE_PRODUCT` to rust
-  rust: Add new varaible RUST_ENABLE_EXTRA_TOOLS
-  sanity: Check if tar is gnutar
-  sdk: Fix path length limit to match reserved size
-  selftest-hardlink: Add additional test cases
-  selftest/cases/runtime_test: Exclude centos-9 from virgl tests
-  selftest: add Upstream-Status to .patch files
-  settings-daemon: submit addsoundkeys.patch upstream and update to a revision that has it
-  systemd.bbclass: Clarify error message
-  tcp-wrappers: mark all patches as inactive-upstream
-  tzdata: Add tzdata.zi to tzdata-core package
-  vorbis: mark patch as Inactive-Upstream
-  vulkan-samples: fix do_compile error when -Og enabled
-  watchdog: Set watchdog_module in default config
-  webkitgtk: fix do_compile errors on beaglebone-yocto
-  webkitgtk: fix do_configure error on beaglebone-yocto
-  weston: upgrade to 13.0.1
-  wic/partition.py: Set hash_seed for empty ext partition
-  wic: bootimg-efi: fix error handling
-  wic: engine.py: use raw string for escape sequence
-  wireless-regdb: upgrade to 2024.05.08
-  xserver-xorg: upgrade to 21.1.13
-  xz: Update :term:`LICENSE` variable for xz packages


Known Issues in Yocto-5.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  N/A


Contributors to Yocto-5.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Adithya Balakumar
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Antonin Godard
-  Archana Polampalli
-  Ashish Sharma
-  Benjamin Szőke
-  Bruce Ashfield
-  Changqing Li
-  Chen Qi
-  Christian Taedcke
-  Deepthi Hemraj
-  Denys Dmytriyenko
-  Dmitry Baryshkov
-  Emil Kronborg
-  Enrico Jörns
-  Etienne Cordonnier
-  Guðni Már Gilbert
-  Hitendra Prajapati
-  Jonas Gorski
-  Jookia
-  Jose Quaresma
-  Joshua Watt
-  Jörg Sommer
-  Kai Kang
-  Khem Raj
-  Kirill Yatsenko
-  Lee Chee Yang
-  Mark Hatle
-  Markus Volk
-  Martin Jansa
-  Michael Opdenacker
-  Mingli Yu
-  Niko Mauno
-  Patrick Wicki
-  Peter Marko
-  Quentin Schulz
-  Ranjitsinh Rathod
-  Richard Purdie
-  Robert Kovacsics
-  Ross Burton
-  Siddharth Doshi
-  Simone Weiß
-  Soumya Sambu
-  Steve Sakoman
-  Sundeep KOKKONDA
-  Trevor Gamblin
-  Vijay Anusuri
-  Wadim Egorov
-  Wang Mingyu
-  Xiangyu Chen
-  Yi Zhao
-  Yogita Urade
-  Zahir Hussain


Repositories / Downloads for Yocto-5.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.3 </poky/log/?h=yocto-5.0.3>`
-  Git Revision: :yocto_git:`0b37512fb4b231cc106768e2a7328431009b3b70 </poky/commit/?id=0b37512fb4b231cc106768e2a7328431009b3b70>`
-  Release Artefact: poky-0b37512fb4b231cc106768e2a7328431009b3b70
-  sha: b37fe0b2f6a685ee94b4af55f896cbf52ba69023e10eb21d3e54798ca21ace79
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.3/poky-0b37512fb4b231cc106768e2a7328431009b3b70.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.3/poky-0b37512fb4b231cc106768e2a7328431009b3b70.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.3 </openembedded-core/log/?h=yocto-5.0.3>`
-  Git Revision: :oe_git:`236ac1b43308df722a78d3aa20aef065dfae5b2b </openembedded-core/commit/?id=236ac1b43308df722a78d3aa20aef065dfae5b2b>`
-  Release Artefact: oecore-236ac1b43308df722a78d3aa20aef065dfae5b2b
-  sha: 44b89feba9563c2281c8c2f45037dd7c312fb20e8b7d9289b25f0ea0fe1fc2c4
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.3/oecore-236ac1b43308df722a78d3aa20aef065dfae5b2b.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.3/oecore-236ac1b43308df722a78d3aa20aef065dfae5b2b.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.3 </meta-mingw/log/?h=yocto-5.0.3>`
-  Git Revision: :yocto_git:`acbba477893ef87388effc4679b7f40ee49fc852 </meta-mingw/commit/?id=acbba477893ef87388effc4679b7f40ee49fc852>`
-  Release Artefact: meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852
-  sha: 3b7c2f475dad5130bace652b150367f587d44b391218b1364a8bbc430b48c54c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.3/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.3/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.3 </bitbake/log/?h=yocto-5.0.3>`
-  Git Revision: :oe_git:`11d83170922a2c6b9db1f6e8c23e533526984b2c </bitbake/commit/?id=11d83170922a2c6b9db1f6e8c23e533526984b2c>`
-  Release Artefact: bitbake-11d83170922a2c6b9db1f6e8c23e533526984b2c
-  sha: 9643433748d7ed80d6334124390271929566b3bc076dad0f6e6be1ec6d753b8d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.3/bitbake-11d83170922a2c6b9db1f6e8c23e533526984b2c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.3/bitbake-11d83170922a2c6b9db1f6e8c23e533526984b2c.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.3 </yocto-docs/log/?h=yocto-5.0.3>`
-  Git Revision: :yocto_git:`TBD </yocto-docs/commit/?id=TBD>`


