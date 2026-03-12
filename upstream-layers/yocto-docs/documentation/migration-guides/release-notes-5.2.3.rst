Release notes for Yocto-5.2.3 (Walnascar)
-----------------------------------------

Security Fixes in Yocto-5.2.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2024-52615` and :cve_nist:`2024-52616`
-  bind: Fix:cve_nist:`2025-40777`
-  binutils: Fix :cve_nist:`2025-5245`, :cve_nist:`2025-7545` and :cve_nist:`2025-7546`
-  busybox: Fix :cve_nist:`2023-39810`
-  coreutils: Fix :cve_nist:`2025-5278`
-  curl: Ignore :cve_nist:`2025-4947` and :cve_nist:`2025-5025`
-  dpkg: Fix :cve_nist:`2025-6297`
-  dropbear: Fix :cve_nist:`2025-47203`
-  gdk-pixbuf: Fix :cve_nist:`2025-7345`
-  git: Fix :cve_nist:`2025-27613`, :cve_nist:`2025-27614`, :cve_nist:`2025-46334`,
   :cve_nist:`2025-46835`, :cve_nist:`2025-48384`, :cve_nist:`2025-48385` and :cve_nist:`2025-48386`
-  gnutls: Fix :cve_nist:`2025-6395`, :cve_nist:`2025-32988`, :cve_nist:`2025-32989` and
   :cve_nist:`2025-32990`
-  go: Fix :cve_nist:`2025-4674`
-  icu: Fix :cve_nist:`2025-5222`
-  iputils: Fix :cve_nist:`2025-48964`
-  libarchive: Fix :cve_nist:`2025-5915`
-  libpam: Fix :cve_nist:`2024-10963`
-  libsoup-2.4: Fix :cve_nist:`2025-4945`
-  libsoup: Fix :cve_nist:`2025-4945`, :cve_nist:`2025-6021`, :cve_nist:`2025-6170`,
   :cve_nist:`2025-49794`, :cve_nist:`2025-49795` and :cve_nist:`2025-49796`
-  linux-yocto/6.12: Ignore :cve_nist:`2021-47342`, :cve_nist:`2022-49934`, :cve_nist:`2022-49935`,
   :cve_nist:`2022-49936`, :cve_nist:`2022-49937`, :cve_nist:`2022-49938`, :cve_nist:`2022-49939`,
   :cve_nist:`2022-49940`, :cve_nist:`2022-49942`, :cve_nist:`2022-49943`, :cve_nist:`2022-49944`,
   :cve_nist:`2022-49945`, :cve_nist:`2022-49946`, :cve_nist:`2022-49947`, :cve_nist:`2022-49948`,
   :cve_nist:`2022-49949`, :cve_nist:`2022-49950`, :cve_nist:`2022-49951`, :cve_nist:`2022-49952`,
   :cve_nist:`2022-49953`, :cve_nist:`2022-49954`, :cve_nist:`2022-49955`, :cve_nist:`2022-49956`,
   :cve_nist:`2022-49957`, :cve_nist:`2022-49958`, :cve_nist:`2022-49959`, :cve_nist:`2022-49960`,
   :cve_nist:`2022-49961`, :cve_nist:`2022-49962`, :cve_nist:`2022-49963`, :cve_nist:`2022-49964`,
   :cve_nist:`2022-49965`, :cve_nist:`2022-49966`, :cve_nist:`2022-49967`, :cve_nist:`2022-49968`,
   :cve_nist:`2022-49969`, :cve_nist:`2022-49970`, :cve_nist:`2022-49971`, :cve_nist:`2022-49972`,
   :cve_nist:`2022-49973`, :cve_nist:`2022-49974`, :cve_nist:`2022-49975`, :cve_nist:`2022-49976`,
   :cve_nist:`2022-49977`, :cve_nist:`2022-49978`, :cve_nist:`2022-49979`, :cve_nist:`2022-49980`,
   :cve_nist:`2022-49981`, :cve_nist:`2022-49982`, :cve_nist:`2022-49983`, :cve_nist:`2022-49984`,
   :cve_nist:`2022-49985`, :cve_nist:`2022-49986`, :cve_nist:`2022-49987`, :cve_nist:`2022-49989`,
   :cve_nist:`2022-49990`, :cve_nist:`2022-49991`, :cve_nist:`2022-49992`, :cve_nist:`2022-49993`,
   :cve_nist:`2022-49994`, :cve_nist:`2022-49995`, :cve_nist:`2022-49996`, :cve_nist:`2022-49997`,
   :cve_nist:`2022-49998`, :cve_nist:`2022-49999`, :cve_nist:`2022-50000`, :cve_nist:`2022-50001`,
   :cve_nist:`2022-50002`, :cve_nist:`2022-50003`, :cve_nist:`2022-50004`, :cve_nist:`2022-50005`,
   :cve_nist:`2022-50006`, :cve_nist:`2022-50007`, :cve_nist:`2022-50008`, :cve_nist:`2022-50009`,
   :cve_nist:`2022-50010`, :cve_nist:`2022-50011`, :cve_nist:`2022-50012`, :cve_nist:`2022-50013`,
   :cve_nist:`2022-50014`, :cve_nist:`2022-50015`, :cve_nist:`2022-50016`, :cve_nist:`2022-50017`,
   :cve_nist:`2022-50019`, :cve_nist:`2022-50020`, :cve_nist:`2022-50021`, :cve_nist:`2022-50022`,
   :cve_nist:`2022-50023`, :cve_nist:`2022-50024`, :cve_nist:`2022-50025`, :cve_nist:`2022-50026`,
   :cve_nist:`2022-50027`, :cve_nist:`2022-50028`, :cve_nist:`2022-50029`, :cve_nist:`2022-50030`,
   :cve_nist:`2022-50031`, :cve_nist:`2022-50032`, :cve_nist:`2022-50033`, :cve_nist:`2022-50034`,
   :cve_nist:`2022-50035`, :cve_nist:`2022-50036`, :cve_nist:`2022-50037`, :cve_nist:`2022-50038`,
   :cve_nist:`2022-50039`, :cve_nist:`2022-50040`, :cve_nist:`2022-50041`, :cve_nist:`2022-50042`,
   :cve_nist:`2022-50043`, :cve_nist:`2022-50044`, :cve_nist:`2022-50045`, :cve_nist:`2022-50046`,
   :cve_nist:`2022-50047`, :cve_nist:`2022-50048`, :cve_nist:`2022-50049`, :cve_nist:`2022-50050`,
   :cve_nist:`2022-50051`, :cve_nist:`2022-50052`, :cve_nist:`2022-50053`, :cve_nist:`2022-50054`,
   :cve_nist:`2022-50055`, :cve_nist:`2022-50056`, :cve_nist:`2022-50057`, :cve_nist:`2022-50058`,
   :cve_nist:`2022-50059`, :cve_nist:`2022-50060`, :cve_nist:`2022-50061`, :cve_nist:`2022-50062`,
   :cve_nist:`2022-50063`, :cve_nist:`2022-50064`, :cve_nist:`2022-50065`, :cve_nist:`2022-50066`,
   :cve_nist:`2022-50067`, :cve_nist:`2022-50068`, :cve_nist:`2022-50069`, :cve_nist:`2022-50070`,
   :cve_nist:`2022-50071`, :cve_nist:`2022-50072`, :cve_nist:`2022-50073`, :cve_nist:`2022-50074`,
   :cve_nist:`2022-50075`, :cve_nist:`2022-50076`, :cve_nist:`2022-50077`, :cve_nist:`2022-50078`,
   :cve_nist:`2022-50079`, :cve_nist:`2022-50080`, :cve_nist:`2022-50082`, :cve_nist:`2022-50083`,
   :cve_nist:`2022-50084`, :cve_nist:`2022-50085`, :cve_nist:`2022-50086`, :cve_nist:`2022-50087`,
   :cve_nist:`2022-50088`, :cve_nist:`2022-50089`, :cve_nist:`2022-50090`, :cve_nist:`2022-50091`,
   :cve_nist:`2022-50092`, :cve_nist:`2022-50093`, :cve_nist:`2022-50094`, :cve_nist:`2022-50095`,
   :cve_nist:`2022-50096`, :cve_nist:`2022-50097`, :cve_nist:`2022-50098`, :cve_nist:`2022-50099`,
   :cve_nist:`2022-50100`, :cve_nist:`2022-50101`, :cve_nist:`2022-50102`, :cve_nist:`2022-50103`,
   :cve_nist:`2022-50104`, :cve_nist:`2022-50105`, :cve_nist:`2022-50106`, :cve_nist:`2022-50107`,
   :cve_nist:`2022-50108`, :cve_nist:`2022-50109`, :cve_nist:`2022-50110`, :cve_nist:`2022-50111`,
   :cve_nist:`2022-50112`, :cve_nist:`2022-50113`, :cve_nist:`2022-50114`, :cve_nist:`2022-50115`,
   :cve_nist:`2022-50116`, :cve_nist:`2022-50117`, :cve_nist:`2022-50118`, :cve_nist:`2022-50119`,
   :cve_nist:`2022-50120`, :cve_nist:`2022-50121`, :cve_nist:`2022-50122`, :cve_nist:`2022-50123`,
   :cve_nist:`2022-50124`, :cve_nist:`2022-50125`, :cve_nist:`2022-50126`, :cve_nist:`2022-50127`,
   :cve_nist:`2022-50129`, :cve_nist:`2022-50130`, :cve_nist:`2022-50131`, :cve_nist:`2022-50132`,
   :cve_nist:`2022-50133`, :cve_nist:`2022-50134`, :cve_nist:`2022-50135`, :cve_nist:`2022-50136`,
   :cve_nist:`2022-50137`, :cve_nist:`2022-50138`, :cve_nist:`2022-50139`, :cve_nist:`2022-50140`,
   :cve_nist:`2022-50141`, :cve_nist:`2022-50142`, :cve_nist:`2022-50143`, :cve_nist:`2022-50144`,
   :cve_nist:`2022-50145`, :cve_nist:`2022-50146`, :cve_nist:`2022-50147`, :cve_nist:`2022-50148`,
   :cve_nist:`2022-50149`, :cve_nist:`2022-50151`, :cve_nist:`2022-50152`, :cve_nist:`2022-50153`,
   :cve_nist:`2022-50154`, :cve_nist:`2022-50155`, :cve_nist:`2022-50156`, :cve_nist:`2022-50157`,
   :cve_nist:`2022-50158`, :cve_nist:`2022-50159`, :cve_nist:`2022-50160`, :cve_nist:`2022-50161`,
   :cve_nist:`2022-50162`, :cve_nist:`2022-50163`, :cve_nist:`2022-50164`, :cve_nist:`2022-50165`,
   :cve_nist:`2022-50166`, :cve_nist:`2022-50167`, :cve_nist:`2022-50168`, :cve_nist:`2022-50169`,
   :cve_nist:`2022-50170`, :cve_nist:`2022-50171`, :cve_nist:`2022-50172`, :cve_nist:`2022-50173`,
   :cve_nist:`2022-50174`, :cve_nist:`2022-50175`, :cve_nist:`2022-50176`, :cve_nist:`2022-50177`,
   :cve_nist:`2022-50178`, :cve_nist:`2022-50179`, :cve_nist:`2022-50181`, :cve_nist:`2022-50182`,
   :cve_nist:`2022-50183`, :cve_nist:`2022-50184`, :cve_nist:`2022-50185`, :cve_nist:`2022-50186`,
   :cve_nist:`2022-50187`, :cve_nist:`2022-50188`, :cve_nist:`2022-50189`, :cve_nist:`2022-50190`,
   :cve_nist:`2022-50191`, :cve_nist:`2022-50192`, :cve_nist:`2022-50193`, :cve_nist:`2022-50194`,
   :cve_nist:`2022-50195`, :cve_nist:`2022-50196`, :cve_nist:`2022-50197`, :cve_nist:`2022-50198`,
   :cve_nist:`2022-50199`, :cve_nist:`2022-50200`, :cve_nist:`2022-50201`, :cve_nist:`2022-50202`,
   :cve_nist:`2022-50203`, :cve_nist:`2022-50204`, :cve_nist:`2022-50205`, :cve_nist:`2022-50206`,
   :cve_nist:`2022-50207`, :cve_nist:`2022-50208`, :cve_nist:`2022-50209`, :cve_nist:`2022-50210`,
   :cve_nist:`2022-50211`, :cve_nist:`2022-50212`, :cve_nist:`2022-50213`, :cve_nist:`2022-50214`,
   :cve_nist:`2022-50215`, :cve_nist:`2022-50217`, :cve_nist:`2022-50218`, :cve_nist:`2022-50219`,
   :cve_nist:`2022-50220`, :cve_nist:`2022-50221`, :cve_nist:`2022-50222`, :cve_nist:`2022-50223`,
   :cve_nist:`2022-50224`, :cve_nist:`2022-50225`, :cve_nist:`2022-50226`, :cve_nist:`2022-50227`,
   :cve_nist:`2022-50228`, :cve_nist:`2022-50229`, :cve_nist:`2022-50231`, :cve_nist:`2024-26710`,
   :cve_nist:`2024-57976` and :cve_nist:`2024-58091`
-  linux-yocto/6.12: (cont.) Ignore :cve_nist:`2025-21817`, :cve_nist:`2025-22101`, :cve_nist:`2025-22112`,
   :cve_nist:`2025-22119`, :cve_nist:`2025-22122`, :cve_nist:`2025-22123`, :cve_nist:`2025-22128`,
   :cve_nist:`2025-23137`, :cve_nist:`2025-23155`, :cve_nist:`2025-37842`, :cve_nist:`2025-37855`,
   :cve_nist:`2025-38000`, :cve_nist:`2025-38001`, :cve_nist:`2025-38002`, :cve_nist:`2025-38003`,
   :cve_nist:`2025-38004`, :cve_nist:`2025-38005`, :cve_nist:`2025-38006`, :cve_nist:`2025-38007`,
   :cve_nist:`2025-38008`, :cve_nist:`2025-38009`, :cve_nist:`2025-38010`, :cve_nist:`2025-38011`,
   :cve_nist:`2025-38012`, :cve_nist:`2025-38013`, :cve_nist:`2025-38014`, :cve_nist:`2025-38015`,
   :cve_nist:`2025-38016`, :cve_nist:`2025-38017`, :cve_nist:`2025-38018`, :cve_nist:`2025-38019`,
   :cve_nist:`2025-38020`, :cve_nist:`2025-38021`, :cve_nist:`2025-38022`, :cve_nist:`2025-38023`,
   :cve_nist:`2025-38024`, :cve_nist:`2025-38025`, :cve_nist:`2025-38027`, :cve_nist:`2025-38028`,
   :cve_nist:`2025-38031`, :cve_nist:`2025-38032`, :cve_nist:`2025-38033`, :cve_nist:`2025-38034`,
   :cve_nist:`2025-38035`, :cve_nist:`2025-38037`, :cve_nist:`2025-38038`, :cve_nist:`2025-38039`,
   :cve_nist:`2025-38040`, :cve_nist:`2025-38043`, :cve_nist:`2025-38044`, :cve_nist:`2025-38045`,
   :cve_nist:`2025-38047`, :cve_nist:`2025-38048`, :cve_nist:`2025-38050`, :cve_nist:`2025-38051`,
   :cve_nist:`2025-38052`, :cve_nist:`2025-38053`, :cve_nist:`2025-38054`, :cve_nist:`2025-38055`,
   :cve_nist:`2025-38056`, :cve_nist:`2025-38057`, :cve_nist:`2025-38058`, :cve_nist:`2025-38059`,
   :cve_nist:`2025-38060`, :cve_nist:`2025-38061`, :cve_nist:`2025-38062`, :cve_nist:`2025-38063`,
   :cve_nist:`2025-38065`, :cve_nist:`2025-38066`, :cve_nist:`2025-38068`, :cve_nist:`2025-38069`,
   :cve_nist:`2025-38070`, :cve_nist:`2025-38071`, :cve_nist:`2025-38072`, :cve_nist:`2025-38073`,
   :cve_nist:`2025-38074`, :cve_nist:`2025-38075`, :cve_nist:`2025-38076`, :cve_nist:`2025-38077`,
   :cve_nist:`2025-38078`, :cve_nist:`2025-38079`, :cve_nist:`2025-38080`, :cve_nist:`2025-38081`,
   :cve_nist:`2025-38082`, :cve_nist:`2025-38083`, :cve_nist:`2025-38084`, :cve_nist:`2025-38085`,
   :cve_nist:`2025-38086`, :cve_nist:`2025-38087`, :cve_nist:`2025-38088`, :cve_nist:`2025-38089`,
   :cve_nist:`2025-38090`, :cve_nist:`2025-38091`, :cve_nist:`2025-38092`, :cve_nist:`2025-38093`,
   :cve_nist:`2025-38094`, :cve_nist:`2025-38095`, :cve_nist:`2025-38096`, :cve_nist:`2025-38097`,
   :cve_nist:`2025-38098`, :cve_nist:`2025-38099`, :cve_nist:`2025-38100`, :cve_nist:`2025-38101`,
   :cve_nist:`2025-38102`, :cve_nist:`2025-38103`, :cve_nist:`2025-38106`, :cve_nist:`2025-38107`,
   :cve_nist:`2025-38108`, :cve_nist:`2025-38109`, :cve_nist:`2025-38110`, :cve_nist:`2025-38111`,
   :cve_nist:`2025-38112`, :cve_nist:`2025-38113`, :cve_nist:`2025-38114`, :cve_nist:`2025-38115`,
   :cve_nist:`2025-38116`, :cve_nist:`2025-38117`, :cve_nist:`2025-38118`, :cve_nist:`2025-38119`,
   :cve_nist:`2025-38120`, :cve_nist:`2025-38121`, :cve_nist:`2025-38122`, :cve_nist:`2025-38123`,
   :cve_nist:`2025-38124`, :cve_nist:`2025-38125`, :cve_nist:`2025-38126`, :cve_nist:`2025-38127`,
   :cve_nist:`2025-38128`, :cve_nist:`2025-38129`, :cve_nist:`2025-38130`, :cve_nist:`2025-38131`,
   :cve_nist:`2025-38133`, :cve_nist:`2025-38134`, :cve_nist:`2025-38135`, :cve_nist:`2025-38136`,
   :cve_nist:`2025-38138`, :cve_nist:`2025-38139`, :cve_nist:`2025-38141`, :cve_nist:`2025-38142`,
   :cve_nist:`2025-38143`, :cve_nist:`2025-38144`, :cve_nist:`2025-38145`, :cve_nist:`2025-38146`,
   :cve_nist:`2025-38147`, :cve_nist:`2025-38148`, :cve_nist:`2025-38149`, :cve_nist:`2025-38150`,
   :cve_nist:`2025-38151`, :cve_nist:`2025-38153`, :cve_nist:`2025-38154`, :cve_nist:`2025-38155`,
   :cve_nist:`2025-38156`, :cve_nist:`2025-38157`, :cve_nist:`2025-38158`, :cve_nist:`2025-38159`,
   :cve_nist:`2025-38160`, :cve_nist:`2025-38161`, :cve_nist:`2025-38162`, :cve_nist:`2025-38163`,
   :cve_nist:`2025-38164`, :cve_nist:`2025-38165`, :cve_nist:`2025-38166`, :cve_nist:`2025-38167`,
   :cve_nist:`2025-38168`, :cve_nist:`2025-38169`, :cve_nist:`2025-38170`, :cve_nist:`2025-38171`,
   :cve_nist:`2025-38172`, :cve_nist:`2025-38173`, :cve_nist:`2025-38174`, :cve_nist:`2025-38175`,
   :cve_nist:`2025-38176`, :cve_nist:`2025-38177`, :cve_nist:`2025-38178`, :cve_nist:`2025-38179`,
   :cve_nist:`2025-38180`, :cve_nist:`2025-38181`, :cve_nist:`2025-38182`, :cve_nist:`2025-38183`,
   :cve_nist:`2025-38184`, :cve_nist:`2025-38185`, :cve_nist:`2025-38186`, :cve_nist:`2025-38188`,
   :cve_nist:`2025-38189`, :cve_nist:`2025-38190`, :cve_nist:`2025-38191`, :cve_nist:`2025-38192`,
   :cve_nist:`2025-38193`, :cve_nist:`2025-38194`, :cve_nist:`2025-38195`, :cve_nist:`2025-38196`,
   :cve_nist:`2025-38197`, :cve_nist:`2025-38198`, :cve_nist:`2025-38200`, :cve_nist:`2025-38201`,
   :cve_nist:`2025-38202`, :cve_nist:`2025-38208`, :cve_nist:`2025-38209`, :cve_nist:`2025-38210`,
   :cve_nist:`2025-38211`, :cve_nist:`2025-38212`, :cve_nist:`2025-38213`, :cve_nist:`2025-38214`,
   :cve_nist:`2025-38215`, :cve_nist:`2025-38216`, :cve_nist:`2025-38217`, :cve_nist:`2025-38218`,
   :cve_nist:`2025-38219`, :cve_nist:`2025-38220`, :cve_nist:`2025-38221`, :cve_nist:`2025-38222`,
   :cve_nist:`2025-38223`, :cve_nist:`2025-38224`, :cve_nist:`2025-38225`, :cve_nist:`2025-38226`,
   :cve_nist:`2025-38227`, :cve_nist:`2025-38228`, :cve_nist:`2025-38229`, :cve_nist:`2025-38230`,
   :cve_nist:`2025-38231`, :cve_nist:`2025-38232`, :cve_nist:`2025-38233`, :cve_nist:`2025-38235`,
   :cve_nist:`2025-38236`, :cve_nist:`2025-38238`, :cve_nist:`2025-38239`, :cve_nist:`2025-38241`,
   :cve_nist:`2025-38242`, :cve_nist:`2025-38243`, :cve_nist:`2025-38244`, :cve_nist:`2025-38245`,
   :cve_nist:`2025-38246`, :cve_nist:`2025-38247`, :cve_nist:`2025-38249`, :cve_nist:`2025-38250`,
   :cve_nist:`2025-38251`, :cve_nist:`2025-38252`, :cve_nist:`2025-38253`, :cve_nist:`2025-38254`,
   :cve_nist:`2025-38255`, :cve_nist:`2025-38256`, :cve_nist:`2025-38257`, :cve_nist:`2025-38258`,
   :cve_nist:`2025-38259`, :cve_nist:`2025-38260`, :cve_nist:`2025-38262`, :cve_nist:`2025-38263`,
   :cve_nist:`2025-38264`, :cve_nist:`2025-38265`, :cve_nist:`2025-38266`, :cve_nist:`2025-38267`,
   :cve_nist:`2025-38268`, :cve_nist:`2025-38269`, :cve_nist:`2025-38270`, :cve_nist:`2025-38271`,
   :cve_nist:`2025-38273`, :cve_nist:`2025-38274`, :cve_nist:`2025-38275`, :cve_nist:`2025-38276`,
   :cve_nist:`2025-38277`, :cve_nist:`2025-38278`, :cve_nist:`2025-38279`, :cve_nist:`2025-38280`,
   :cve_nist:`2025-38281`, :cve_nist:`2025-38282`, :cve_nist:`2025-38283`, :cve_nist:`2025-38285`,
   :cve_nist:`2025-38286`, :cve_nist:`2025-38287`, :cve_nist:`2025-38288`, :cve_nist:`2025-38289`,
   :cve_nist:`2025-38290`, :cve_nist:`2025-38291`, :cve_nist:`2025-38292`, :cve_nist:`2025-38293`,
   :cve_nist:`2025-38294`, :cve_nist:`2025-38295`, :cve_nist:`2025-38296`, :cve_nist:`2025-38297`,
   :cve_nist:`2025-38298`, :cve_nist:`2025-38299`, :cve_nist:`2025-38300`, :cve_nist:`2025-38301`,
   :cve_nist:`2025-38302`, :cve_nist:`2025-38303`, :cve_nist:`2025-38304`, :cve_nist:`2025-38305`,
   :cve_nist:`2025-38307`, :cve_nist:`2025-38308`, :cve_nist:`2025-38309`, :cve_nist:`2025-38310`,
   :cve_nist:`2025-38312`, :cve_nist:`2025-38313`, :cve_nist:`2025-38314`, :cve_nist:`2025-38315`,
   :cve_nist:`2025-38316`, :cve_nist:`2025-38317`, :cve_nist:`2025-38318`, :cve_nist:`2025-38319`,
   :cve_nist:`2025-38320`, :cve_nist:`2025-38321`, :cve_nist:`2025-38322`, :cve_nist:`2025-38323`,
   :cve_nist:`2025-38324`, :cve_nist:`2025-38325`, :cve_nist:`2025-38326`, :cve_nist:`2025-38327`,
   :cve_nist:`2025-38328`, :cve_nist:`2025-38329`, :cve_nist:`2025-38330`, :cve_nist:`2025-38331`,
   :cve_nist:`2025-38332`, :cve_nist:`2025-38333`, :cve_nist:`2025-38334`, :cve_nist:`2025-38336`,
   :cve_nist:`2025-38337`, :cve_nist:`2025-38338`, :cve_nist:`2025-38339`, :cve_nist:`2025-38340`,
   :cve_nist:`2025-38341`, :cve_nist:`2025-38342`, :cve_nist:`2025-38343`, :cve_nist:`2025-38344`,
   :cve_nist:`2025-38345`, :cve_nist:`2025-38346`, :cve_nist:`2025-38347` and :cve_nist:`2025-38348`
-  ncurses: Fix :cve_nist:`2025-6141`
-  python3: Fix :cve_nist:`2025-8194`
-  sqlite3: Fix :cve_nist:`2025-6965`
-  sudo: Fix :cve_nist:`2025-32462` and :cve_nist:`2025-32463`
-  webkitgtk: Fix :cve_nist:`2025-24223`, :cve_nist:`2025-31204`, :cve_nist:`2025-31205`,
   :cve_nist:`2025-31206`, :cve_nist:`2025-31215` and :cve_nist:`2025-31257`
-  xserver-xorg: Fix :cve_nist:`2025-49175`, :cve_nist:`2025-49176`, :cve_nist:`2025-49177`,
   :cve_nist:`2025-49178`, :cve_nist:`2025-49179` and :cve_nist:`2025-49180`


Fixes in Yocto-5.2.3
~~~~~~~~~~~~~~~~~~~~

-  bind: upgrade to 9.20.11
-  binutils: stable 2.44 branch updates
-  bitbake: test/fetch: Switch u-boot based test to use our own mirror
-  bitbake: utils: Optimise signal/sigmask performance
-  build-appliance-image: Update to walnascar head revision
-  ca-certificates: correct the :term:`SRC_URI`
-  conf.py: improve SearchEnglish to handle terms with dots
-  dev-manual/start.rst: added missing command in Optimize your VHDX file using DiskPart
-  dev-manual/start.rst: mention that :term:`PERSISTENT_DIR` should be shared too
-  dev-manual/start.rst: remove basic setup for hash equivalence
-  dev-manual/start.rst: remove shared :term:`PERSISTENT_DIR` mentions
-  docs/variables.rst: remove references to obsolete tar packaging
-  git: upgrade to 2.49.1
-  glibc: stable 2.41 branch updates
-  gnutls: upgrade to 3.8.10
-  go: upgrade to 1.24.5
-  kea: set correct permissions for /var/run/kea
-  libpam: upgrade to 1.7.1
-  linux-yocto/6.12: riscv tune fragments
-  linux-yocto/6.12: riscv: Enable :term:`TUNE_FEATURES` based :term:`KERNEL_FEATURES`
-  linux-yocto/6.12: update to v6.12.38
-  linux-yocto/6.12: yaffs2: silence warnings
-  ltp: Skip semctl08 when __USE_TIME64_REDIRECTS is defined
-  ltp: backport patch to fix compilation error for Skylake -march=x86-64-v3
-  migration-guides: add release notes for 4.0.28, 5.0.11, 5.2.2
-  mingetty: fix do_package warning
-  mtools: upgrade to 4.0.49
-  openssl: upgrade to 3.4.2
-  orc: set :term:`CVE_PRODUCT`
-  overview-manual/concepts.rst: fix sayhello hardcoded bindir
-  overview-manual/concepts.rst: mention :term:`PERSISTENT_DIR` for user configuration
-  overview-manual/yp-intro.rst: fix broken link to article
-  poky.conf: bump version for 5.2.3
-  poky.yaml.in: increase minimum RAM from 8 to 32
-  python3: update CVE product
-  ref-manual/classes.rst: document the testexport class
-  ref-manual/classes.rst: drop obsolete QA errors
-  ref-manual/classes.rst: insane: drop cve_status_not_in_db
-  ref-manual/structure.rst: remove shared :term:`PERSISTENT_DIR` mentions
-  ref-manual/structure.rst: update with info on :term:`PERSISTENT_DIR`
-  ref-manual/system-requirements.rst: update supported distributions
-  ref-manual/variables.rst: document :term:`SPL_DTB_BINARY`
-  ref-manual/variables.rst: document the :term:`FIT_CONF_PREFIX` variable
-  ruby-ptest: some ptest fixes
-  ruby: upgrade to 3.4.4
-  rust: Fix malformed hunk header in rustix patch
-  scripts/install-buildtools: Update to 5.2.2
-  sudo: upgrade to 1.9.17p1
-  test-manual/understand-autobuilder.rst: mention hashequiv server
-  webkitgtk: Fix build break on non-arm/non-x86 systems
-  webkitgtk: Use gcc to compile for arm target
-  webkitgtk: upgrade to 2.48.2
-  xserver-xorg: upgrade to 21.1.18


Known Issues in Yocto-5.2.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A 

Contributors to Yocto-5.2.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Antonin Godard
-  Archana Polampalli
-  Bruce Ashfield
-  Changqing Li
-  Chen Qi
-  Deepesh Varatharajan
-  Divya Chellam
-  Enrico Jörns
-  Erik Lindsten
-  Gyorgy Sarvari
-  Hongxu Jia
-  Jiaying Song
-  Jinfeng Wang
-  Khem Raj
-  Lee Chee Yang
-  Marco Cavallini
-  Mark Hatle
-  Peter Marko
-  Praveen Kumar
-  Richard Purdie
-  Robert P. J. Day
-  Steve Sakoman
-  Vijay Anusuri
-  Wang Mingyu
-  Yash Shinde
-  Yi Zhao
-  Yogesh Tyagi
-  Yogita Urade
-  Zhang Peng

Repositories / Downloads for Yocto-5.2.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`walnascar </poky/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2.3 </poky/log/?h=yocto-5.2.3>`
-  Git Revision: :yocto_git:`db04028d9070f05c3b5dee728473fb234bd24f05 </poky/commit/?id=db04028d9070f05c3b5dee728473fb234bd24f05>`
-  Release Artefact: poky-db04028d9070f05c3b5dee728473fb234bd24f05
-  sha: 32e1d457d5de0041ee423727b5690fbde58c485a42b8ed81ecebb7bb2d8c58cc
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.3/poky-db04028d9070f05c3b5dee728473fb234bd24f05.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.3/poky-db04028d9070f05c3b5dee728473fb234bd24f05.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`walnascar </openembedded-core/log/?h=walnascar>`
-  Tag:  :oe_git:`yocto-5.2.3 </openembedded-core/log/?h=yocto-5.2.3>`
-  Git Revision: :oe_git:`347cb0861dde58613541ce692778f907943a60ea </openembedded-core/commit/?id=347cb0861dde58613541ce692778f907943a60ea>`
-  Release Artefact: oecore-347cb0861dde58613541ce692778f907943a60ea
-  sha: 88cbb79f7bc2de9d931cfa1092463005189972d4584cdae1562621df79f09fbd
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.3/oecore-347cb0861dde58613541ce692778f907943a60ea.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.3/oecore-347cb0861dde58613541ce692778f907943a60ea.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`walnascar </meta-mingw/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2.3 </meta-mingw/log/?h=yocto-5.2.3>`
-  Git Revision: :yocto_git:`edce693e1b8fabd84651aa6c0888aafbcf238577 </meta-mingw/commit/?id=edce693e1b8fabd84651aa6c0888aafbcf238577>`
-  Release Artefact: meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577
-  sha: 6cfed41b54f83da91a6cf201ec1c2cd4ac284f642b1268c8fa89d2335ea2bce1
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.3/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.3/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.12 </bitbake/log/?h=2.12>`
-  Tag:  :oe_git:`yocto-5.2.3 </bitbake/log/?h=yocto-5.2.3>`
-  Git Revision: :oe_git:`710f98844ae30416bdf6a01b655df398b49574ec </bitbake/commit/?id=710f98844ae30416bdf6a01b655df398b49574ec>`
-  Release Artefact: bitbake-710f98844ae30416bdf6a01b655df398b49574ec
-  sha: e30aa4739e3104634184b1dd7d5502f0994a725daec15929c4adf1164aa1296d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.3/bitbake-710f98844ae30416bdf6a01b655df398b49574ec.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.3/bitbake-710f98844ae30416bdf6a01b655df398b49574ec.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`walnascar </meta-yocto/log/?h=walnascar>`
-  Tag: :yocto_git:`yocto-5.2.3 </meta-yocto/log/?h=yocto-5.2.3>`
-  Git Revision: :yocto_git:`ce011415ab4e583a4545cd91aceff4190225f31d </meta-yocto/commit/?id=ce011415ab4e583a4545cd91aceff4190225f31d>`

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`walnascar </yocto-docs/log/?h=walnascar>`
-  Tag: :yocto_git:`yocto-5.2.3 </yocto-docs/log/?h=yocto-5.2.3>`
-  Git Revision: :yocto_git:`e664a70adb5bc19041b3b5f553fb90dcddff99d0 </yocto-docs/commit/?id=e664a70adb5bc19041b3b5f553fb90dcddff99d0>`

