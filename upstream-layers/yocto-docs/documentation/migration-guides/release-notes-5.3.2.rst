.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.3.2 (Whinlatter)
------------------------------------------

Security Fixes in Yocto-5.3.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2025-68276`, :cve_nist:`2025-68468`, :cve_nist:`2025-68471` and
   :cve_nist:`2026-24401`
-  curl: Fix :cve_nist:`2025-13034`, :cve_nist:`2025-14017`, :cve_nist:`2025-14524`,
   :cve_nist:`2025-14819`, :cve_nist:`2025-15079` and :cve_nist:`2025-15224`
-  dropbear: Fix :cve_nist:`2025-14282`
-  expat: Fix :cve_nist:`2026-24515` and :cve_nist:`2026-25210`
-  ffmpeg: Ignore :cve_nist:`2023-51791`, :cve_nist:`2023-51793`, :cve_nist:`2023-51794`,
   :cve_nist:`2023-51795`, :cve_nist:`2023-51796`, :cve_nist:`2023-51797`, :cve_nist:`2023-51798`,
   :cve_nist:`2025-22921`, :cve_nist:`2025-25468` and :cve_nist:`2025-25469`
-  glib-2.0: Fix :cve_nist:`2026-0988`
-  glibc: Fix :cve_nist:`2025-15281`, :cve_nist:`2026-0861` and :cve_nist:`2026-0915`
-  gnupg: Fix :cve_nist:`2025-68973`
-  go: Fix :cve_nist:`2025-61730`, :cve_nist:`2025-61731`, :cve_nist:`2025-61728`,
   :cve_nist:`2025-61726`, :cve_nist:`2025-61732`, :cve_nist:`2025-68119` and :cve_nist:`2025-68121`
-  grub: Fix :cve_nist:`2025-54770`, :cve_nist:`2025-61661`, :cve_nist:`2025-61662`,
   :cve_nist:`2025-61663` and :cve_nist:`2025-61664`
-  inetutils: Fix :cve_nist:`2026-24061`
-  libarchive: Fix :cve_nist:`2025-60753`
-  libpng: Fix :cve_nist:`2026-22695` and :cve_nist:`2026-22801`
-  libsndfile1: Fix :cve_nist:`2025-56226`
-  libtasn1: Fix :cve_nist:`2025-13151`
-  libxml2: Fix :cve_nist:`2026-0989`, :cve_nist:`2026-0990` and :cve_nist:`2026-0992`
-  linux-yocto/6.12: Ignore :cve_nist:`2021-47620`, :cve_nist:`2022-48853`, :cve_nist:`2022-48950`,
   :cve_nist:`2022-50583`, :cve_nist:`2022-50614`, :cve_nist:`2022-50615`, :cve_nist:`2022-50616`,
   :cve_nist:`2022-50617`, :cve_nist:`2022-50618`, :cve_nist:`2022-50619`, :cve_nist:`2022-50620`,
   :cve_nist:`2022-50621`, :cve_nist:`2022-50622`, :cve_nist:`2022-50623`, :cve_nist:`2022-50624`,
   :cve_nist:`2022-50625`, :cve_nist:`2022-50626`, :cve_nist:`2022-50627`, :cve_nist:`2022-50628`,
   :cve_nist:`2022-50629`, :cve_nist:`2022-50630`, :cve_nist:`2022-50631`, :cve_nist:`2022-50632`,
   :cve_nist:`2022-50633`, :cve_nist:`2022-50634`, :cve_nist:`2022-50635`, :cve_nist:`2022-50636`,
   :cve_nist:`2022-50637`, :cve_nist:`2022-50638`, :cve_nist:`2022-50639`, :cve_nist:`2022-50640`,
   :cve_nist:`2022-50641`, :cve_nist:`2022-50642`, :cve_nist:`2022-50643`, :cve_nist:`2022-50644`,
   :cve_nist:`2022-50645`, :cve_nist:`2022-50646`, :cve_nist:`2022-50647`, :cve_nist:`2022-50648`,
   :cve_nist:`2022-50649`, :cve_nist:`2022-50650`, :cve_nist:`2022-50651`, :cve_nist:`2022-50652`,
   :cve_nist:`2022-50653`, :cve_nist:`2022-50654`, :cve_nist:`2022-50655`, :cve_nist:`2022-50656`,
   :cve_nist:`2022-50657`, :cve_nist:`2022-50658`, :cve_nist:`2022-50659`, :cve_nist:`2022-50660`,
   :cve_nist:`2022-50661`, :cve_nist:`2022-50662`, :cve_nist:`2022-50663`, :cve_nist:`2022-50664`,
   :cve_nist:`2022-50665`, :cve_nist:`2022-50666`, :cve_nist:`2022-50667`, :cve_nist:`2022-50668`,
   :cve_nist:`2022-50669`, :cve_nist:`2022-50670`, :cve_nist:`2022-50671`, :cve_nist:`2022-50672`,
   :cve_nist:`2022-50673`, :cve_nist:`2022-50674`, :cve_nist:`2022-50675`, :cve_nist:`2022-50676`,
   :cve_nist:`2022-50677`, :cve_nist:`2022-50678`, :cve_nist:`2022-50679`, :cve_nist:`2022-50697`,
   :cve_nist:`2022-50698`, :cve_nist:`2022-50699`, :cve_nist:`2022-50700`, :cve_nist:`2022-50701`,
   :cve_nist:`2022-50702`, :cve_nist:`2022-50703`, :cve_nist:`2022-50704`, :cve_nist:`2022-50705`,
   :cve_nist:`2022-50706`, :cve_nist:`2022-50707`, :cve_nist:`2022-50708`, :cve_nist:`2022-50709`,
   :cve_nist:`2022-50710`, :cve_nist:`2022-50711`, :cve_nist:`2022-50712`, :cve_nist:`2022-50713`,
   :cve_nist:`2022-50714`, :cve_nist:`2022-50715`, :cve_nist:`2022-50716`, :cve_nist:`2022-50717`,
   :cve_nist:`2022-50718`, :cve_nist:`2022-50719`, :cve_nist:`2022-50720`, :cve_nist:`2022-50721`,
   :cve_nist:`2022-50722`, :cve_nist:`2022-50723`, :cve_nist:`2022-50724`, :cve_nist:`2022-50725`,
   :cve_nist:`2022-50726`, :cve_nist:`2022-50727`, :cve_nist:`2022-50728`, :cve_nist:`2022-50729`,
   :cve_nist:`2022-50730`, :cve_nist:`2022-50731`, :cve_nist:`2022-50732`, :cve_nist:`2022-50733`,
   :cve_nist:`2022-50734`, :cve_nist:`2022-50735`, :cve_nist:`2022-50736`, :cve_nist:`2022-50737`,
   :cve_nist:`2022-50738`, :cve_nist:`2022-50739`, :cve_nist:`2022-50740`, :cve_nist:`2022-50741`,
   :cve_nist:`2022-50742`, :cve_nist:`2022-50743`, :cve_nist:`2022-50744`, :cve_nist:`2022-50745`,
   :cve_nist:`2022-50746`, :cve_nist:`2022-50747`, :cve_nist:`2022-50748`, :cve_nist:`2022-50749`,
   :cve_nist:`2022-50750`, :cve_nist:`2022-50751`, :cve_nist:`2022-50752`, :cve_nist:`2022-50753`,
   :cve_nist:`2022-50754`, :cve_nist:`2022-50755`, :cve_nist:`2022-50756`, :cve_nist:`2022-50757`,
   :cve_nist:`2022-50758`, :cve_nist:`2022-50759`, :cve_nist:`2022-50760`, :cve_nist:`2022-50761`,
   :cve_nist:`2022-50762`, :cve_nist:`2022-50763`, :cve_nist:`2022-50764`, :cve_nist:`2022-50765`,
   :cve_nist:`2022-50766`, :cve_nist:`2022-50767`, :cve_nist:`2022-50768`, :cve_nist:`2022-50769`,
   :cve_nist:`2022-50770`, :cve_nist:`2022-50771`, :cve_nist:`2022-50772`, :cve_nist:`2022-50773`,
   :cve_nist:`2022-50774`, :cve_nist:`2022-50775`, :cve_nist:`2022-50776`, :cve_nist:`2022-50777`,
   :cve_nist:`2022-50778`, :cve_nist:`2022-50779`, :cve_nist:`2022-50780`, :cve_nist:`2022-50781`,
   :cve_nist:`2022-50782`, :cve_nist:`2022-50783`, :cve_nist:`2022-50784`, :cve_nist:`2022-50785`,
   :cve_nist:`2022-50786`, :cve_nist:`2022-50809`, :cve_nist:`2022-50810`, :cve_nist:`2022-50811`,
   :cve_nist:`2022-50812`, :cve_nist:`2022-50813`, :cve_nist:`2022-50814`, :cve_nist:`2022-50815`,
   :cve_nist:`2022-50816`, :cve_nist:`2022-50817`, :cve_nist:`2022-50818`, :cve_nist:`2022-50819`,
   :cve_nist:`2022-50820`, :cve_nist:`2022-50821`, :cve_nist:`2022-50822`, :cve_nist:`2022-50823`,
   :cve_nist:`2022-50824`, :cve_nist:`2022-50825`, :cve_nist:`2022-50826`, :cve_nist:`2022-50827`,
   :cve_nist:`2022-50828`, :cve_nist:`2022-50829`, :cve_nist:`2022-50830`, :cve_nist:`2022-50832`,
   :cve_nist:`2022-50833`, :cve_nist:`2022-50834`, :cve_nist:`2022-50835`, :cve_nist:`2022-50836`,
   :cve_nist:`2022-50837`, :cve_nist:`2022-50838`, :cve_nist:`2022-50839`, :cve_nist:`2022-50840`,
   :cve_nist:`2022-50841`, :cve_nist:`2022-50842`, :cve_nist:`2022-50843`, :cve_nist:`2022-50844`,
   :cve_nist:`2022-50845`, :cve_nist:`2022-50846`, :cve_nist:`2022-50847`, :cve_nist:`2022-50848`,
   :cve_nist:`2022-50849`, :cve_nist:`2022-50850`, :cve_nist:`2022-50851`, :cve_nist:`2022-50852`,
   :cve_nist:`2022-50853`, :cve_nist:`2022-50854`, :cve_nist:`2022-50855`, :cve_nist:`2022-50856`,
   :cve_nist:`2022-50857`, :cve_nist:`2022-50858`, :cve_nist:`2022-50859`, :cve_nist:`2022-50860`,
   :cve_nist:`2022-50861`, :cve_nist:`2022-50862`, :cve_nist:`2022-50863`, :cve_nist:`2022-50864`,
   :cve_nist:`2022-50865`, :cve_nist:`2022-50866`, :cve_nist:`2022-50867`, :cve_nist:`2022-50868`,
   :cve_nist:`2022-50869`, :cve_nist:`2022-50870`, :cve_nist:`2022-50871`, :cve_nist:`2022-50872`,
   :cve_nist:`2022-50873`, :cve_nist:`2022-50874`, :cve_nist:`2022-50875`, :cve_nist:`2022-50876`,
   :cve_nist:`2022-50877`, :cve_nist:`2022-50878`, :cve_nist:`2022-50879`, :cve_nist:`2022-50880`,
   :cve_nist:`2022-50881`, :cve_nist:`2022-50882`, :cve_nist:`2022-50883`, :cve_nist:`2022-50884`,
   :cve_nist:`2022-50885`, :cve_nist:`2022-50886`, :cve_nist:`2022-50887`, :cve_nist:`2022-50888`,
   :cve_nist:`2022-50889`, :cve_nist:`2023-53339`, :cve_nist:`2023-53742`, :cve_nist:`2023-53743`,
   :cve_nist:`2023-53744`, :cve_nist:`2023-53745`, :cve_nist:`2023-53746`, :cve_nist:`2023-53747`,
   :cve_nist:`2023-53748`, :cve_nist:`2023-53750`, :cve_nist:`2023-53751`, :cve_nist:`2023-53752`,
   :cve_nist:`2023-53753`, :cve_nist:`2023-53754`, :cve_nist:`2023-53755`, :cve_nist:`2023-53756`,
   :cve_nist:`2023-53757`, :cve_nist:`2023-53758`, :cve_nist:`2023-53759`, :cve_nist:`2023-53760`,
   :cve_nist:`2023-53761`, :cve_nist:`2023-53762`, :cve_nist:`2023-53763`, :cve_nist:`2023-53764`,
   :cve_nist:`2023-53765`, :cve_nist:`2023-53766`, :cve_nist:`2023-53767`, :cve_nist:`2023-53768`,
   :cve_nist:`2023-53769`, :cve_nist:`2023-53777`, :cve_nist:`2023-53778`, :cve_nist:`2023-53780`,
   :cve_nist:`2023-53781`, :cve_nist:`2023-53782`, :cve_nist:`2023-53783`, :cve_nist:`2023-53784`,
   :cve_nist:`2023-53785`, :cve_nist:`2023-53786`, :cve_nist:`2023-53787`, :cve_nist:`2023-53788`,
   :cve_nist:`2023-53789`, :cve_nist:`2023-53790`, :cve_nist:`2023-53791`, :cve_nist:`2023-53792`,
   :cve_nist:`2023-53793`, :cve_nist:`2023-53794`, :cve_nist:`2023-53795`, :cve_nist:`2023-53796`,
   :cve_nist:`2023-53797`, :cve_nist:`2023-53798`, :cve_nist:`2023-53799`, :cve_nist:`2023-53800`,
   :cve_nist:`2023-53801`, :cve_nist:`2023-53802`, :cve_nist:`2023-53803`, :cve_nist:`2023-53804`,
   :cve_nist:`2023-53806`, :cve_nist:`2023-53807`, :cve_nist:`2023-53808`, :cve_nist:`2023-53809`,
   :cve_nist:`2023-53810`, :cve_nist:`2023-53811`, :cve_nist:`2023-53812`, :cve_nist:`2023-53813`,
   :cve_nist:`2023-53814`, :cve_nist:`2023-53815`, :cve_nist:`2023-53816`, :cve_nist:`2023-53817`,
   :cve_nist:`2023-53818`, :cve_nist:`2023-53819`, :cve_nist:`2023-53820`, :cve_nist:`2023-53821`,
   :cve_nist:`2023-53822`, :cve_nist:`2023-53823`, :cve_nist:`2023-53824`, :cve_nist:`2023-53825`,
   :cve_nist:`2023-53826`, :cve_nist:`2023-53827`, :cve_nist:`2023-53828`, :cve_nist:`2023-53829`,
   :cve_nist:`2023-53830`, :cve_nist:`2023-53831`, :cve_nist:`2023-53832`, :cve_nist:`2023-53833`,
   :cve_nist:`2023-53834`, :cve_nist:`2023-53836`, :cve_nist:`2023-53837`, :cve_nist:`2023-53838`,
   :cve_nist:`2023-53839`, :cve_nist:`2023-53840`, :cve_nist:`2023-53841`, :cve_nist:`2023-53842`,
   :cve_nist:`2023-53843`, :cve_nist:`2023-53844`, :cve_nist:`2023-53845`, :cve_nist:`2023-53846`,
   :cve_nist:`2023-53847`, :cve_nist:`2023-53848`, :cve_nist:`2023-53849`, :cve_nist:`2023-53850`,
   :cve_nist:`2023-53851`, :cve_nist:`2023-53852`, :cve_nist:`2023-53853`, :cve_nist:`2023-53854`,
   :cve_nist:`2023-53855`, :cve_nist:`2023-53856`, :cve_nist:`2023-53857`, :cve_nist:`2023-53858`,
   :cve_nist:`2023-53859`, :cve_nist:`2023-53860`, :cve_nist:`2023-53861`, :cve_nist:`2023-53862`,
   :cve_nist:`2023-53863`, :cve_nist:`2023-53864`, :cve_nist:`2023-53865`, :cve_nist:`2023-53866`,
   :cve_nist:`2023-53867`, :cve_nist:`2023-53986`, :cve_nist:`2023-53987`, :cve_nist:`2023-53988`,
   :cve_nist:`2023-53989`, :cve_nist:`2023-53990`, :cve_nist:`2023-53991`, :cve_nist:`2023-53992`,
   :cve_nist:`2023-53993`, :cve_nist:`2023-53994`, :cve_nist:`2023-53995`, :cve_nist:`2023-53996`,
   :cve_nist:`2023-53997`, :cve_nist:`2023-53998`, :cve_nist:`2023-53999`, :cve_nist:`2023-54000`,
   :cve_nist:`2023-54001`, :cve_nist:`2023-54002`, :cve_nist:`2023-54003`, :cve_nist:`2023-54004`,
   :cve_nist:`2023-54005`, :cve_nist:`2023-54006`, :cve_nist:`2023-54007`, :cve_nist:`2023-54008`,
   :cve_nist:`2023-54009`, :cve_nist:`2023-54010`, :cve_nist:`2023-54011`, :cve_nist:`2023-54012`,
   :cve_nist:`2023-54013`, :cve_nist:`2023-54014`, :cve_nist:`2023-54015`, :cve_nist:`2023-54016`,
   :cve_nist:`2023-54017`, :cve_nist:`2023-54018`, :cve_nist:`2023-54019`, :cve_nist:`2023-54020`,
   :cve_nist:`2023-54021`, :cve_nist:`2023-54022`, :cve_nist:`2023-54023`, :cve_nist:`2023-54024`,
   :cve_nist:`2023-54025`, :cve_nist:`2023-54026`, :cve_nist:`2023-54027`, :cve_nist:`2023-54028`,
   :cve_nist:`2023-54030`, :cve_nist:`2023-54031`, :cve_nist:`2023-54032`, :cve_nist:`2023-54033`,
   :cve_nist:`2023-54034`, :cve_nist:`2023-54035`, :cve_nist:`2023-54036`, :cve_nist:`2023-54037`,
   :cve_nist:`2023-54038`, :cve_nist:`2023-54039`, :cve_nist:`2023-54040`, :cve_nist:`2023-54041`,
   :cve_nist:`2023-54042`, :cve_nist:`2023-54043`, :cve_nist:`2023-54044`, :cve_nist:`2023-54045`,
   :cve_nist:`2023-54046`, :cve_nist:`2023-54047`, :cve_nist:`2023-54048`, :cve_nist:`2023-54049`,
   :cve_nist:`2023-54050`, :cve_nist:`2023-54051`, :cve_nist:`2023-54052`, :cve_nist:`2023-54053`,
   :cve_nist:`2023-54055`, :cve_nist:`2023-54056`, :cve_nist:`2023-54057`, :cve_nist:`2023-54058`,
   :cve_nist:`2023-54059`, :cve_nist:`2023-54060`, :cve_nist:`2023-54062`, :cve_nist:`2023-54063`,
   :cve_nist:`2023-54064`, :cve_nist:`2023-54065`, :cve_nist:`2023-54066`, :cve_nist:`2023-54067`,
   :cve_nist:`2023-54068`, :cve_nist:`2023-54069`, :cve_nist:`2023-54070`, :cve_nist:`2023-54071`,
   :cve_nist:`2023-54072`, :cve_nist:`2023-54073`, :cve_nist:`2023-54074`, :cve_nist:`2023-54075`,
   :cve_nist:`2023-54076`, :cve_nist:`2023-54077`, :cve_nist:`2023-54078`, :cve_nist:`2023-54079`,
   :cve_nist:`2023-54080`, :cve_nist:`2023-54081`, :cve_nist:`2023-54083`, :cve_nist:`2023-54084`,
   :cve_nist:`2023-54085`, :cve_nist:`2023-54086`, :cve_nist:`2023-54087`, :cve_nist:`2023-54088`,
   :cve_nist:`2023-54089`, :cve_nist:`2023-54090`, :cve_nist:`2023-54091`, :cve_nist:`2023-54092`,
   :cve_nist:`2023-54093`, :cve_nist:`2023-54094`, :cve_nist:`2023-54095`, :cve_nist:`2023-54096`,
   :cve_nist:`2023-54097`, :cve_nist:`2023-54098`, :cve_nist:`2023-54099`, :cve_nist:`2023-54100`,
   :cve_nist:`2023-54101`, :cve_nist:`2023-54102`, :cve_nist:`2023-54104`, :cve_nist:`2023-54105`,
   :cve_nist:`2023-54106`, :cve_nist:`2023-54107`, :cve_nist:`2023-54108`, :cve_nist:`2023-54109`,
   :cve_nist:`2023-54110`, :cve_nist:`2023-54111`, :cve_nist:`2023-54112`, :cve_nist:`2023-54113`,
   :cve_nist:`2023-54114`, :cve_nist:`2023-54115`, :cve_nist:`2023-54116`, :cve_nist:`2023-54117`,
   :cve_nist:`2023-54118`, :cve_nist:`2023-54119`, :cve_nist:`2023-54120`, :cve_nist:`2023-54121`,
   :cve_nist:`2023-54122`, :cve_nist:`2023-54123`, :cve_nist:`2023-54124`, :cve_nist:`2023-54125`,
   :cve_nist:`2023-54126`, :cve_nist:`2023-54127`, :cve_nist:`2023-54128`, :cve_nist:`2023-54129`,
   :cve_nist:`2023-54130`, :cve_nist:`2023-54131`, :cve_nist:`2023-54132`, :cve_nist:`2023-54133`,
   :cve_nist:`2023-54134`, :cve_nist:`2023-54135`, :cve_nist:`2023-54136`, :cve_nist:`2023-54137`,
   :cve_nist:`2023-54138`, :cve_nist:`2023-54139`, :cve_nist:`2023-54140`, :cve_nist:`2023-54141`,
   :cve_nist:`2023-54142`, :cve_nist:`2023-54143`, :cve_nist:`2023-54144`, :cve_nist:`2023-54145`,
   :cve_nist:`2023-54146`, :cve_nist:`2023-54147`, :cve_nist:`2023-54148`, :cve_nist:`2023-54149`,
   :cve_nist:`2023-54150`, :cve_nist:`2023-54151`, :cve_nist:`2023-54152`, :cve_nist:`2023-54153`,
   :cve_nist:`2023-54154`, :cve_nist:`2023-54155`, :cve_nist:`2023-54156`, :cve_nist:`2023-54157`,
   :cve_nist:`2023-54158`, :cve_nist:`2023-54159`, :cve_nist:`2023-54160`, :cve_nist:`2023-54162`,
   :cve_nist:`2023-54164`, :cve_nist:`2023-54165`, :cve_nist:`2023-54166`, :cve_nist:`2023-54167`,
   :cve_nist:`2023-54168`, :cve_nist:`2023-54169`, :cve_nist:`2023-54170`, :cve_nist:`2023-54171`,
   :cve_nist:`2023-54172`, :cve_nist:`2023-54173`, :cve_nist:`2023-54174`, :cve_nist:`2023-54175`,
   :cve_nist:`2023-54176`, :cve_nist:`2023-54177`, :cve_nist:`2023-54178`, :cve_nist:`2023-54179`,
   :cve_nist:`2023-54180`, :cve_nist:`2023-54181`, :cve_nist:`2023-54182`, :cve_nist:`2023-54183`,
   :cve_nist:`2023-54184`, :cve_nist:`2023-54185`, :cve_nist:`2023-54186`, :cve_nist:`2023-54187`,
   :cve_nist:`2023-54188`, :cve_nist:`2023-54189`, :cve_nist:`2023-54190`, :cve_nist:`2023-54191`,
   :cve_nist:`2023-54192`, :cve_nist:`2023-54193`, :cve_nist:`2023-54194`, :cve_nist:`2023-54195`,
   :cve_nist:`2023-54196`, :cve_nist:`2023-54197`, :cve_nist:`2023-54198`, :cve_nist:`2023-54199`,
   :cve_nist:`2023-54200`, :cve_nist:`2023-54201`, :cve_nist:`2023-54202`, :cve_nist:`2023-54203`,
   :cve_nist:`2023-54204`, :cve_nist:`2023-54205`, :cve_nist:`2023-54206`, :cve_nist:`2023-54207`,
   :cve_nist:`2023-54208`, :cve_nist:`2023-54209`, :cve_nist:`2023-54210`, :cve_nist:`2023-54211`,
   :cve_nist:`2023-54213`, :cve_nist:`2023-54214`, :cve_nist:`2023-54215`, :cve_nist:`2023-54216`,
   :cve_nist:`2023-54217`, :cve_nist:`2023-54218`, :cve_nist:`2023-54219`, :cve_nist:`2023-54220`,
   :cve_nist:`2023-54221`, :cve_nist:`2023-54222`, :cve_nist:`2023-54223`, :cve_nist:`2023-54224`,
   :cve_nist:`2023-54225`, :cve_nist:`2023-54226`, :cve_nist:`2023-54227`, :cve_nist:`2023-54228`,
   :cve_nist:`2023-54229`, :cve_nist:`2023-54230`, :cve_nist:`2023-54231`, :cve_nist:`2023-54232`,
   :cve_nist:`2023-54233`, :cve_nist:`2023-54234`, :cve_nist:`2023-54235`, :cve_nist:`2023-54236`,
   :cve_nist:`2023-54237`, :cve_nist:`2023-54238`, :cve_nist:`2023-54239`, :cve_nist:`2023-54240`,
   :cve_nist:`2023-54241`, :cve_nist:`2023-54242`, :cve_nist:`2023-54243`, :cve_nist:`2023-54244`,
   :cve_nist:`2023-54245`, :cve_nist:`2023-54246`, :cve_nist:`2023-54247`, :cve_nist:`2023-54248`,
   :cve_nist:`2023-54249`, :cve_nist:`2023-54250`, :cve_nist:`2023-54251`, :cve_nist:`2023-54252`,
   :cve_nist:`2023-54253`, :cve_nist:`2023-54254`, :cve_nist:`2023-54255`, :cve_nist:`2023-54257`,
   :cve_nist:`2023-54258`, :cve_nist:`2023-54259`, :cve_nist:`2023-54260`, :cve_nist:`2023-54261`,
   :cve_nist:`2023-54262`, :cve_nist:`2023-54263`, :cve_nist:`2023-54264`, :cve_nist:`2023-54265`,
   :cve_nist:`2023-54266`, :cve_nist:`2023-54267`, :cve_nist:`2023-54268`, :cve_nist:`2023-54269`,
   :cve_nist:`2023-54270`, :cve_nist:`2023-54271`, :cve_nist:`2023-54272`, :cve_nist:`2023-54273`,
   :cve_nist:`2023-54274`, :cve_nist:`2023-54275`, :cve_nist:`2023-54276`, :cve_nist:`2023-54277`,
   :cve_nist:`2023-54278`, :cve_nist:`2023-54279`, :cve_nist:`2023-54280`, :cve_nist:`2023-54281`,
   :cve_nist:`2023-54282`, :cve_nist:`2023-54283`, :cve_nist:`2023-54284`, :cve_nist:`2023-54285`,
   :cve_nist:`2023-54286`, :cve_nist:`2023-54287`, :cve_nist:`2023-54288`, :cve_nist:`2023-54289`,
   :cve_nist:`2023-54291`, :cve_nist:`2023-54292`, :cve_nist:`2023-54293`, :cve_nist:`2023-54294`,
   :cve_nist:`2023-54295`, :cve_nist:`2023-54296`, :cve_nist:`2023-54297`, :cve_nist:`2023-54298`,
   :cve_nist:`2023-54299`, :cve_nist:`2023-54300`, :cve_nist:`2023-54301`, :cve_nist:`2023-54302`,
   :cve_nist:`2023-54303`, :cve_nist:`2023-54304`, :cve_nist:`2023-54305`, :cve_nist:`2023-54306`,
   :cve_nist:`2023-54307`, :cve_nist:`2023-54308`, :cve_nist:`2023-54309`, :cve_nist:`2023-54310`,
   :cve_nist:`2023-54311`, :cve_nist:`2023-54312`, :cve_nist:`2023-54313`, :cve_nist:`2023-54314`,
   :cve_nist:`2023-54315`, :cve_nist:`2023-54316`, :cve_nist:`2023-54317`, :cve_nist:`2023-54318`,
   :cve_nist:`2023-54319`, :cve_nist:`2023-54320`, :cve_nist:`2023-54321`, :cve_nist:`2023-54322`,
   :cve_nist:`2023-54323`, :cve_nist:`2023-54324`, :cve_nist:`2023-54325`, :cve_nist:`2023-54326`,
   :cve_nist:`2024-47683`, :cve_nist:`2025-37884`, :cve_nist:`2025-39745`, :cve_nist:`2025-39822`,
   :cve_nist:`2025-39905`, :cve_nist:`2025-40210`, :cve_nist:`2025-40214`, :cve_nist:`2025-40216`,
   :cve_nist:`2025-40218`, :cve_nist:`2025-40219`, :cve_nist:`2025-40220`, :cve_nist:`2025-40221`,
   :cve_nist:`2025-40222`, :cve_nist:`2025-40223`, :cve_nist:`2025-40224`, :cve_nist:`2025-40225`,
   :cve_nist:`2025-40226`, :cve_nist:`2025-40227`, :cve_nist:`2025-40228`, :cve_nist:`2025-40229`,
   :cve_nist:`2025-40230`, :cve_nist:`2025-40231`, :cve_nist:`2025-40232`, :cve_nist:`2025-40233`,
   :cve_nist:`2025-40234`, :cve_nist:`2025-40235`, :cve_nist:`2025-40236`, :cve_nist:`2025-40237`,
   :cve_nist:`2025-40238`, :cve_nist:`2025-40239`, :cve_nist:`2025-40240`, :cve_nist:`2025-40241`,
   :cve_nist:`2025-40242`, :cve_nist:`2025-40243`, :cve_nist:`2025-40244`, :cve_nist:`2025-40245`,
   :cve_nist:`2025-40246`, :cve_nist:`2025-40248`, :cve_nist:`2025-40249`, :cve_nist:`2025-40250`,
   :cve_nist:`2025-40251`, :cve_nist:`2025-40252`, :cve_nist:`2025-40253`, :cve_nist:`2025-40254`,
   :cve_nist:`2025-40255`, :cve_nist:`2025-40256`, :cve_nist:`2025-40257`, :cve_nist:`2025-40258`,
   :cve_nist:`2025-40259`, :cve_nist:`2025-40260`, :cve_nist:`2025-40261`, :cve_nist:`2025-40262`,
   :cve_nist:`2025-40263`, :cve_nist:`2025-40264`, :cve_nist:`2025-40265`, :cve_nist:`2025-40266`,
   :cve_nist:`2025-40267`, :cve_nist:`2025-40268`, :cve_nist:`2025-40269`, :cve_nist:`2025-40270`,
   :cve_nist:`2025-40271`, :cve_nist:`2025-40272`, :cve_nist:`2025-40273`, :cve_nist:`2025-40274`,
   :cve_nist:`2025-40275`, :cve_nist:`2025-40277`, :cve_nist:`2025-40278`, :cve_nist:`2025-40279`,
   :cve_nist:`2025-40280`, :cve_nist:`2025-40281`, :cve_nist:`2025-40282`, :cve_nist:`2025-40283`,
   :cve_nist:`2025-40284`, :cve_nist:`2025-40285`, :cve_nist:`2025-40286`, :cve_nist:`2025-40287`,
   :cve_nist:`2025-40288`, :cve_nist:`2025-40289`, :cve_nist:`2025-40290`, :cve_nist:`2025-40291`,
   :cve_nist:`2025-40292`, :cve_nist:`2025-40293`, :cve_nist:`2025-40294`, :cve_nist:`2025-40295`,
   :cve_nist:`2025-40296`, :cve_nist:`2025-40297`, :cve_nist:`2025-40298`, :cve_nist:`2025-40299`,
   :cve_nist:`2025-40301`, :cve_nist:`2025-40302`, :cve_nist:`2025-40303`, :cve_nist:`2025-40304`,
   :cve_nist:`2025-40305`, :cve_nist:`2025-40306`, :cve_nist:`2025-40307`, :cve_nist:`2025-40308`,
   :cve_nist:`2025-40309`, :cve_nist:`2025-40310`, :cve_nist:`2025-40311`, :cve_nist:`2025-40312`,
   :cve_nist:`2025-40313`, :cve_nist:`2025-40314`, :cve_nist:`2025-40315`, :cve_nist:`2025-40316`,
   :cve_nist:`2025-40317`, :cve_nist:`2025-40318`, :cve_nist:`2025-40319`, :cve_nist:`2025-40320`,
   :cve_nist:`2025-40321`, :cve_nist:`2025-40322`, :cve_nist:`2025-40323`, :cve_nist:`2025-40324`,
   :cve_nist:`2025-40326`, :cve_nist:`2025-40327`, :cve_nist:`2025-40328`, :cve_nist:`2025-40329`,
   :cve_nist:`2025-40330`, :cve_nist:`2025-40331`, :cve_nist:`2025-40332`, :cve_nist:`2025-40333`,
   :cve_nist:`2025-40334`, :cve_nist:`2025-40335`, :cve_nist:`2025-40336`, :cve_nist:`2025-40337`,
   :cve_nist:`2025-40339`, :cve_nist:`2025-40340`, :cve_nist:`2025-40341`, :cve_nist:`2025-40342`,
   :cve_nist:`2025-40343`, :cve_nist:`2025-40344`, :cve_nist:`2025-40346`, :cve_nist:`2025-40347`,
   :cve_nist:`2025-40348`, :cve_nist:`2025-40349`, :cve_nist:`2025-40350`, :cve_nist:`2025-40351`,
   :cve_nist:`2025-40352`, :cve_nist:`2025-40353`, :cve_nist:`2025-40354`, :cve_nist:`2025-40356`,
   :cve_nist:`2025-40357`, :cve_nist:`2025-40358`, :cve_nist:`2025-40359`, :cve_nist:`2025-40360`,
   :cve_nist:`2025-40362`, :cve_nist:`2025-40363`, :cve_nist:`2025-68167`, :cve_nist:`2025-68168`,
   :cve_nist:`2025-68169`, :cve_nist:`2025-68170`, :cve_nist:`2025-68171`, :cve_nist:`2025-68172`,
   :cve_nist:`2025-68173`, :cve_nist:`2025-68176`, :cve_nist:`2025-68177`, :cve_nist:`2025-68178`,
   :cve_nist:`2025-68179`, :cve_nist:`2025-68180`, :cve_nist:`2025-68181`, :cve_nist:`2025-68182`,
   :cve_nist:`2025-68183`, :cve_nist:`2025-68184`, :cve_nist:`2025-68185`, :cve_nist:`2025-68186`,
   :cve_nist:`2025-68187`, :cve_nist:`2025-68188`, :cve_nist:`2025-68189`, :cve_nist:`2025-68190`,
   :cve_nist:`2025-68191`, :cve_nist:`2025-68192`, :cve_nist:`2025-68194`, :cve_nist:`2025-68196`,
   :cve_nist:`2025-68197`, :cve_nist:`2025-68198`, :cve_nist:`2025-68199`, :cve_nist:`2025-68200`,
   :cve_nist:`2025-68201`, :cve_nist:`2025-68202`, :cve_nist:`2025-68204`, :cve_nist:`2025-68205`,
   :cve_nist:`2025-68207`, :cve_nist:`2025-68208`, :cve_nist:`2025-68210`, :cve_nist:`2025-68211`,
   :cve_nist:`2025-68212`, :cve_nist:`2025-68213`, :cve_nist:`2025-68214`, :cve_nist:`2025-68215`,
   :cve_nist:`2025-68216`, :cve_nist:`2025-68217`, :cve_nist:`2025-68218`, :cve_nist:`2025-68219`,
   :cve_nist:`2025-68220`, :cve_nist:`2025-68221`, :cve_nist:`2025-68222`, :cve_nist:`2025-68223`,
   :cve_nist:`2025-68225`, :cve_nist:`2025-68226`, :cve_nist:`2025-68227`, :cve_nist:`2025-68228`,
   :cve_nist:`2025-68229`, :cve_nist:`2025-68230`, :cve_nist:`2025-68231`, :cve_nist:`2025-68232`,
   :cve_nist:`2025-68233`, :cve_nist:`2025-68234`, :cve_nist:`2025-68235`, :cve_nist:`2025-68237`,
   :cve_nist:`2025-68238`, :cve_nist:`2025-68240`, :cve_nist:`2025-68241`, :cve_nist:`2025-68242`,
   :cve_nist:`2025-68243`, :cve_nist:`2025-68244`, :cve_nist:`2025-68245`, :cve_nist:`2025-68246`,
   :cve_nist:`2025-68247`, :cve_nist:`2025-68248`, :cve_nist:`2025-68249`, :cve_nist:`2025-68250`,
   :cve_nist:`2025-68252`, :cve_nist:`2025-68253`, :cve_nist:`2025-68260`, :cve_nist:`2025-68262`,
   :cve_nist:`2025-68281`, :cve_nist:`2025-68294`, :cve_nist:`2025-68299`, :cve_nist:`2025-68309`,
   :cve_nist:`2025-68310`, :cve_nist:`2025-68311`, :cve_nist:`2025-68312`, :cve_nist:`2025-68313`,
   :cve_nist:`2025-68314`, :cve_nist:`2025-68315`, :cve_nist:`2025-68316`, :cve_nist:`2025-68317`,
   :cve_nist:`2025-68320`, :cve_nist:`2025-68321`, :cve_nist:`2025-68322`, :cve_nist:`2025-68323`,
   :cve_nist:`2025-68326`, :cve_nist:`2025-68350`, :cve_nist:`2025-68355`, :cve_nist:`2025-68370`,
   :cve_nist:`2025-68373`, :cve_nist:`2025-68375`, :cve_nist:`2025-68377`, :cve_nist:`2025-68726`,
   :cve_nist:`2025-68731`, :cve_nist:`2025-68734`, :cve_nist:`2025-68737`, :cve_nist:`2025-68738`,
   :cve_nist:`2025-68739`, :cve_nist:`2025-68743`, :cve_nist:`2025-68750`, :cve_nist:`2025-68752`,
   :cve_nist:`2025-68754`, :cve_nist:`2025-68760`, :cve_nist:`2025-68761`, :cve_nist:`2025-68762`,
   :cve_nist:`2025-68779`, :cve_nist:`2025-68790`, :cve_nist:`2025-68791`, :cve_nist:`2025-68793`,
   :cve_nist:`2025-68805`, :cve_nist:`2025-68807`, :cve_nist:`2025-68812`, :cve_nist:`2025-71070`,
   :cve_nist:`2025-71090`, :cve_nist:`2025-71092`, :cve_nist:`2025-71103`, :cve_nist:`2025-71106`,
   :cve_nist:`2025-71110`, :cve_nist:`2025-71115`, :cve_nist:`2025-71124`, :cve_nist:`2025-71128`,
   :cve_nist:`2025-71139`, :cve_nist:`2025-71142`, :cve_nist:`2025-71155`, :cve_nist:`2025-71158`,
   :cve_nist:`2025-71159`, :cve_nist:`2025-71181`, :cve_nist:`2025-71187`, :cve_nist:`2026-22983`,
   :cve_nist:`2026-22987`, :cve_nist:`2026-22995`, :cve_nist:`2026-23008`, :cve_nist:`2026-23009`,
   :cve_nist:`2026-23012`, :cve_nist:`2026-23014`, :cve_nist:`2026-23015`, :cve_nist:`2026-23016`,
   :cve_nist:`2026-23018`, :cve_nist:`2026-23022`, :cve_nist:`2026-23024`, :cve_nist:`2026-23027`,
   :cve_nist:`2026-23028`, :cve_nist:`2026-23029`, :cve_nist:`2026-23034`, :cve_nist:`2026-23036`,
   :cve_nist:`2026-23039`, :cve_nist:`2026-23040`, :cve_nist:`2026-23041`, :cve_nist:`2026-23042`,
   :cve_nist:`2026-23043`, :cve_nist:`2026-23044`, :cve_nist:`2026-23045`, :cve_nist:`2026-23046`,
   :cve_nist:`2026-23048`, :cve_nist:`2026-23051`, :cve_nist:`2026-23052`, :cve_nist:`2026-23067`,
   :cve_nist:`2026-23077`, :cve_nist:`2026-23079`, :cve_nist:`2026-23081`, :cve_nist:`2026-23092`,
   :cve_nist:`2026-23100`, :cve_nist:`2026-23106` and :cve_nist:`2026-23109`
-  linux-yocto/6.12: Fix :cve_nist:`2024-58096`, :cve_nist:`2024-58097`, :cve_nist:`2025-22111`,
   :cve_nist:`2025-38234`, :cve_nist:`2025-38248`, :cve_nist:`2025-38591`, :cve_nist:`2025-39872`,
   :cve_nist:`2025-40075`, :cve_nist:`2025-40149`, :cve_nist:`2025-40164`, :cve_nist:`2025-40170`,
   :cve_nist:`2025-40215`, :cve_nist:`2025-40276`, :cve_nist:`2025-40325`, :cve_nist:`2025-40345`,
   :cve_nist:`2025-68206`, :cve_nist:`2025-68254`, :cve_nist:`2025-68255`, :cve_nist:`2025-68256`,
   :cve_nist:`2025-68257`, :cve_nist:`2025-68258`, :cve_nist:`2025-68259`, :cve_nist:`2025-68261`,
   :cve_nist:`2025-68263`, :cve_nist:`2025-68264`, :cve_nist:`2025-68265`, :cve_nist:`2025-68266`,
   :cve_nist:`2025-68282`, :cve_nist:`2025-68283`, :cve_nist:`2025-68284`, :cve_nist:`2025-68285`,
   :cve_nist:`2025-68286`, :cve_nist:`2025-68287`, :cve_nist:`2025-68288`, :cve_nist:`2025-68289`,
   :cve_nist:`2025-68290`, :cve_nist:`2025-68291`, :cve_nist:`2025-68292`, :cve_nist:`2025-68293`,
   :cve_nist:`2025-68295`, :cve_nist:`2025-68296`, :cve_nist:`2025-68297`, :cve_nist:`2025-68298`,
   :cve_nist:`2025-68300`, :cve_nist:`2025-68301`, :cve_nist:`2025-68302`, :cve_nist:`2025-68303`,
   :cve_nist:`2025-68305`, :cve_nist:`2025-68306`, :cve_nist:`2025-68307`, :cve_nist:`2025-68308`,
   :cve_nist:`2025-68324`, :cve_nist:`2025-68325`, :cve_nist:`2025-68327`, :cve_nist:`2025-68328`,
   :cve_nist:`2025-68329`, :cve_nist:`2025-68330`, :cve_nist:`2025-68331`, :cve_nist:`2025-68332`,
   :cve_nist:`2025-68333`, :cve_nist:`2025-68335`, :cve_nist:`2025-68336`, :cve_nist:`2025-68337`,
   :cve_nist:`2025-68338`, :cve_nist:`2025-68339`, :cve_nist:`2025-68340`, :cve_nist:`2025-68341`,
   :cve_nist:`2025-68342`, :cve_nist:`2025-68343`, :cve_nist:`2025-68344`, :cve_nist:`2025-68345`,
   :cve_nist:`2025-68346`, :cve_nist:`2025-68347`, :cve_nist:`2025-68348`, :cve_nist:`2025-68349`,
   :cve_nist:`2025-68351`, :cve_nist:`2025-68352`, :cve_nist:`2025-68354`, :cve_nist:`2025-68356`,
   :cve_nist:`2025-68357`, :cve_nist:`2025-68358`, :cve_nist:`2025-68361`, :cve_nist:`2025-68362`,
   :cve_nist:`2025-68363`, :cve_nist:`2025-68364`, :cve_nist:`2025-68365`, :cve_nist:`2025-68366`,
   :cve_nist:`2025-68367`, :cve_nist:`2025-68369`, :cve_nist:`2025-68371`, :cve_nist:`2025-68372`,
   :cve_nist:`2025-68374`, :cve_nist:`2025-68378`, :cve_nist:`2025-68379`, :cve_nist:`2025-68380`,
   :cve_nist:`2025-68724`, :cve_nist:`2025-68725`, :cve_nist:`2025-68727`, :cve_nist:`2025-68728`,
   :cve_nist:`2025-68732`, :cve_nist:`2025-68733`, :cve_nist:`2025-68740`, :cve_nist:`2025-68741`,
   :cve_nist:`2025-68742`, :cve_nist:`2025-68744`, :cve_nist:`2025-68746`, :cve_nist:`2025-68747`,
   :cve_nist:`2025-68748`, :cve_nist:`2025-68749`, :cve_nist:`2025-68753`, :cve_nist:`2025-68756`,
   :cve_nist:`2025-68757`, :cve_nist:`2025-68758`, :cve_nist:`2025-68759`, :cve_nist:`2025-68763`,
   :cve_nist:`2025-68764`, :cve_nist:`2025-68765`, :cve_nist:`2025-68766`, :cve_nist:`2025-68767`,
   :cve_nist:`2025-68769`, :cve_nist:`2025-68770`, :cve_nist:`2025-68771`, :cve_nist:`2025-68772`,
   :cve_nist:`2025-68773`, :cve_nist:`2025-68774`, :cve_nist:`2025-68775`, :cve_nist:`2025-68776`,
   :cve_nist:`2025-68777`, :cve_nist:`2025-68778`, :cve_nist:`2025-68780`, :cve_nist:`2025-68781`,
   :cve_nist:`2025-68782`, :cve_nist:`2025-68783`, :cve_nist:`2025-68784`, :cve_nist:`2025-68785`,
   :cve_nist:`2025-68786`, :cve_nist:`2025-68787`, :cve_nist:`2025-68788`, :cve_nist:`2025-68789`,
   :cve_nist:`2025-68792`, :cve_nist:`2025-68794`, :cve_nist:`2025-68795`, :cve_nist:`2025-68796`,
   :cve_nist:`2025-68797`, :cve_nist:`2025-68798`, :cve_nist:`2025-68799`, :cve_nist:`2025-68800`,
   :cve_nist:`2025-68801`, :cve_nist:`2025-68802`, :cve_nist:`2025-68803`, :cve_nist:`2025-68804`,
   :cve_nist:`2025-68806`, :cve_nist:`2025-68808`, :cve_nist:`2025-68809`, :cve_nist:`2025-68810`,
   :cve_nist:`2025-68811`, :cve_nist:`2025-68813`, :cve_nist:`2025-68814`, :cve_nist:`2025-68815`,
   :cve_nist:`2025-68816`, :cve_nist:`2025-68817`, :cve_nist:`2025-68818`, :cve_nist:`2025-68819`,
   :cve_nist:`2025-68820`, :cve_nist:`2025-68821`, :cve_nist:`2025-68822`, :cve_nist:`2025-71064`,
   :cve_nist:`2025-71065`, :cve_nist:`2025-71066`, :cve_nist:`2025-71067`, :cve_nist:`2025-71068`,
   :cve_nist:`2025-71069`, :cve_nist:`2025-71071`, :cve_nist:`2025-71072`, :cve_nist:`2025-71073`,
   :cve_nist:`2025-71075`, :cve_nist:`2025-71076`, :cve_nist:`2025-71077`, :cve_nist:`2025-71078`,
   :cve_nist:`2025-71079`, :cve_nist:`2025-71080`, :cve_nist:`2025-71081`, :cve_nist:`2025-71082`,
   :cve_nist:`2025-71083`, :cve_nist:`2025-71084`, :cve_nist:`2025-71085`, :cve_nist:`2025-71086`,
   :cve_nist:`2025-71087`, :cve_nist:`2025-71088`, :cve_nist:`2025-71089`, :cve_nist:`2025-71091`,
   :cve_nist:`2025-71093`, :cve_nist:`2025-71094`, :cve_nist:`2025-71095`, :cve_nist:`2025-71096`,
   :cve_nist:`2025-71097`, :cve_nist:`2025-71098`, :cve_nist:`2025-71099`, :cve_nist:`2025-71100`,
   :cve_nist:`2025-71101`, :cve_nist:`2025-71102`, :cve_nist:`2025-71104`, :cve_nist:`2025-71105`,
   :cve_nist:`2025-71107`, :cve_nist:`2025-71108`, :cve_nist:`2025-71109`, :cve_nist:`2025-71111`,
   :cve_nist:`2025-71112`, :cve_nist:`2025-71113`, :cve_nist:`2025-71114`, :cve_nist:`2025-71116`,
   :cve_nist:`2025-71118`, :cve_nist:`2025-71119`, :cve_nist:`2025-71120`, :cve_nist:`2025-71121`,
   :cve_nist:`2025-71122`, :cve_nist:`2025-71123`, :cve_nist:`2025-71125`, :cve_nist:`2025-71126`,
   :cve_nist:`2025-71127`, :cve_nist:`2025-71129`, :cve_nist:`2025-71130`, :cve_nist:`2025-71131`,
   :cve_nist:`2025-71132`, :cve_nist:`2025-71133`, :cve_nist:`2025-71134`, :cve_nist:`2025-71135`,
   :cve_nist:`2025-71136`, :cve_nist:`2025-71137`, :cve_nist:`2025-71138`, :cve_nist:`2025-71140`,
   :cve_nist:`2025-71143`, :cve_nist:`2025-71144`, :cve_nist:`2025-71146`, :cve_nist:`2025-71147`,
   :cve_nist:`2025-71148`, :cve_nist:`2025-71149`, :cve_nist:`2025-71150`, :cve_nist:`2025-71151`,
   :cve_nist:`2025-71153`, :cve_nist:`2025-71154`, :cve_nist:`2025-71156`, :cve_nist:`2025-71157`,
   :cve_nist:`2025-71160`, :cve_nist:`2025-71162`, :cve_nist:`2025-71163`, :cve_nist:`2025-71180`,
   :cve_nist:`2025-71182`, :cve_nist:`2025-71183`, :cve_nist:`2025-71184`, :cve_nist:`2025-71185`,
   :cve_nist:`2025-71186`, :cve_nist:`2025-71188`, :cve_nist:`2025-71189`, :cve_nist:`2025-71190`,
   :cve_nist:`2025-71191`, :cve_nist:`2025-71192`, :cve_nist:`2025-71193`, :cve_nist:`2025-71194`,
   :cve_nist:`2025-71195`, :cve_nist:`2025-71196`, :cve_nist:`2025-71197`, :cve_nist:`2025-71198`,
   :cve_nist:`2025-71199`, :cve_nist:`2026-22976`, :cve_nist:`2026-22977`, :cve_nist:`2026-22978`,
   :cve_nist:`2026-22979`, :cve_nist:`2026-22980`, :cve_nist:`2026-22982`, :cve_nist:`2026-22984`,
   :cve_nist:`2026-22988`, :cve_nist:`2026-22989`, :cve_nist:`2026-22990`, :cve_nist:`2026-22991`,
   :cve_nist:`2026-22992`, :cve_nist:`2026-22994`, :cve_nist:`2026-22996`, :cve_nist:`2026-22997`,
   :cve_nist:`2026-22998`, :cve_nist:`2026-22999`, :cve_nist:`2026-23000`, :cve_nist:`2026-23001`,
   :cve_nist:`2026-23002`, :cve_nist:`2026-23003`, :cve_nist:`2026-23005`, :cve_nist:`2026-23006`,
   :cve_nist:`2026-23010`, :cve_nist:`2026-23011`, :cve_nist:`2026-23013`, :cve_nist:`2026-23019`,
   :cve_nist:`2026-23020`, :cve_nist:`2026-23021`, :cve_nist:`2026-23023`, :cve_nist:`2026-23025`,
   :cve_nist:`2026-23026`, :cve_nist:`2026-23030`, :cve_nist:`2026-23031`, :cve_nist:`2026-23032`,
   :cve_nist:`2026-23033`, :cve_nist:`2026-23035`, :cve_nist:`2026-23037`, :cve_nist:`2026-23038`,
   :cve_nist:`2026-23047`, :cve_nist:`2026-23049`, :cve_nist:`2026-23050`, :cve_nist:`2026-23053`,
   :cve_nist:`2026-23054`, :cve_nist:`2026-23055`, :cve_nist:`2026-23056`, :cve_nist:`2026-23057`,
   :cve_nist:`2026-23058`, :cve_nist:`2026-23059`, :cve_nist:`2026-23060`, :cve_nist:`2026-23061`,
   :cve_nist:`2026-23062`, :cve_nist:`2026-23063`, :cve_nist:`2026-23064`, :cve_nist:`2026-23065`,
   :cve_nist:`2026-23068`, :cve_nist:`2026-23069`, :cve_nist:`2026-23071`, :cve_nist:`2026-23072`,
   :cve_nist:`2026-23073`, :cve_nist:`2026-23074`, :cve_nist:`2026-23075`, :cve_nist:`2026-23076`,
   :cve_nist:`2026-23078`, :cve_nist:`2026-23080`, :cve_nist:`2026-23082`, :cve_nist:`2026-23083`,
   :cve_nist:`2026-23084`, :cve_nist:`2026-23085`, :cve_nist:`2026-23086`, :cve_nist:`2026-23087`,
   :cve_nist:`2026-23088`, :cve_nist:`2026-23089`, :cve_nist:`2026-23090`, :cve_nist:`2026-23091`,
   :cve_nist:`2026-23093`, :cve_nist:`2026-23094`, :cve_nist:`2026-23095`, :cve_nist:`2026-23096`,
   :cve_nist:`2026-23097`, :cve_nist:`2026-23098`, :cve_nist:`2026-23099`, :cve_nist:`2026-23101`,
   :cve_nist:`2026-23103`, :cve_nist:`2026-23105`, :cve_nist:`2026-23107`, :cve_nist:`2026-23108`
   and :cve_nist:`2026-23110`
-  openssl: Fix :cve_nist:`2025-11187`, :cve_nist:`2025-15467`, :cve_nist:`2025-15468`,
   :cve_nist:`2025-15469`, :cve_nist:`2025-66199`, :cve_nist:`2025-68160`, :cve_nist:`2025-69418`,
   :cve_nist:`2025-69419`, :cve_nist:`2025-69420`, :cve_nist:`2025-69421`, :cve_nist:`2026-22795`
   and :cve_nist:`2026-22796`
-  python3-urllib3: Fix :cve_nist:`2026-21441`
-  util-linux: Fix :cve_nist:`2025-14104`
-  vim: ignore :cve_nist:`2025-66476`
-  webkitgtk: Fix :cve_nist:`2025-43343`
-  zlib: ignore :cve_nist:`2026-22184`


Fixes in Yocto-5.3.2
~~~~~~~~~~~~~~~~~~~~

bitbake
^^^^^^^
-  bitbake-setup: ensure paths with timestamps in them are unique

meta-yocto
^^^^^^^^^^
-  poky.conf: Bump version for 5.3.2 release

openembedded-core
^^^^^^^^^^^^^^^^^
-  build-appliance-image: Update to whinlatter head revisions
-  devtool: deploy: Reset PATH after strip_execs
-  devtool: deploy: Run pseudo with correct PATH
-  docbook-xml-dtd4: fix the fetching failure
-  dpkg: Fix ADMINDIR
-  expat: upgrade to 2.7.4
-  ffmpeg: add a (possible) build race fix
-  ffmpeg: fix a build race, hopefully for real this time
-  glibc: stable 2.42 branch updates (912d89a766...)
-  go: upgrade to 1.25.7
-  libarchive: upgrade to 3.8.5
-  libpng: upgrade to 1.6.54
-  libtheora: set :term:`CVE_PRODUCT`
-  linux-yocto/6.12: update to v6.12.69
-  mobile-broadband-provider-info: upgrade to 20251101
-  oeqa/gitarchive: Fix git push URL parameter
-  oeqa/gitarchive: Push tag before copying log files
-  oeqa/selftest/wic: test recursive dir copy on ext partitions
-  openssl: upgrade to 3.5.5
-  pseudo: Update to 1.9.3+git9ab513512d... (include an openat2 fix)
-  scripts/install-buildtools: Update to 5.3.1
-  scripts/oe-git-archive: Ensure new push parameter is specified
-  selftest: devtool: Set PATH when running pseudo
-  webkitgtk: upgrade to 2.50.4
-  webkitgtk: workaround compile failure for large debug symbols
-  wic/engine: fix copying directories into wic image with ext* partition

yocto-docs
^^^^^^^^^^
-  Add a new "Security" section
-  Add a security manual
-  Move security related manuals to the security manual
-  README: remove obsolete poky repo references
-  README: replace obsolete substitution variables
-  Remove diagrams directory
-  brief-yoctoprojectqs/index.rst: remove extra word from text
-  bsp-guide/bsp.rst: remove obsolete poky repo references
-  contributor-guide/identify-component.rst: remove obsolete poky repo references
-  contributor-guide/recipe-style-guide.rst: explain difference between layer and recipe license(s)
-  contributor-guide/submit-changes.rst: remove obsolete poky repo references
-  dev-manual/build-quality.rst: remove obsolete poky repo references
-  dev-manual/building.rst: remove obsolete poky repo references
-  dev-manual/custom-distribution.rst: remove obsolete poky repo references
-  dev-manual/custom-template-configuration-directory.rst: remove obsolete poky repo references
-  dev-manual/debugging.rst: remove obsolete poky repo references
-  dev-manual/error-reporting-tool.rst: remove obsolete poky repo references
-  dev-manual/external-toolchain.rst: remove obsolete poky repo references
-  dev-manual/init-manager.rst: remove obsolete poky repo references
-  dev-manual/layers.rst: re-organize the document
-  dev-manual/layers.rst: remove obsolete poky repo references
-  dev-manual/libraries.rst: remove obsolete poky repo references
-  dev-manual/licenses.rst: instruct to use git-archive instead of removing .git
-  dev-manual/licenses.rst: remove obsolete poky repo references
-  dev-manual/new-recipe.rst: remove obsolete poky repo references
-  dev-manual/packages.rst: fix example recipe version
-  dev-manual/packages.rst: pr server: fix and explain why r0.X increments on :term:`SRCREV` change
-  dev-manual/packages.rst: remove obsolete poky repo references
-  dev-manual/packages.rst: rename r0.0 to r0 when :term:`PR` server is not enabled
-  dev-manual/poky-manual-setup.rst: mention the Source Directory
-  dev-manual/poky-manual-setup.rst: remove obsolete poky repo references
-  dev-manual/python-development-shell.rst: remove obsolete poky repo references
-  dev-manual/qemu.rst: remove obsolete poky repo references
-  dev-manual/qemu: add an example for the snapshot+nographic mode
-  dev-manual/securing-images.rst: remove obsolete poky repo references
-  dev-manual/start.rst: remove obsolete poky repo references
-  dev-manual/temporary-source-code.rst: remove obsolete poky repo references
-  dev-manual/upgrading-recipes.rst: move meta-openembedded to bitbake-builds/layers
-  dev-manual/upgrading-recipes.rst: remove obsolete poky repo references
-  dev-manual/vulnerabilities.rst: remove obsolete poky repo references
-  dev-manual/wayland.rst: remove obsolete poky repo references
-  dev-manual/wic.rst: remove obsolete poky repo references
-  kernel-dev/advanced.rst: remove obsolete poky repo references
-  kernel-dev/common.rst: remove obsolete poky repo references
-  migration-guides/migration-1.4.rst: remove obsolete poky repo references
-  migration-guides/migration-5.3.rst: add note on \*FLAGS behavior change
-  migration-guides: add release notes for 4.0.32, 5.0.14, 5.0.15 and 5.3.1
-  overview-manual/concepts.rst: remove obsolete poky repo references
-  overview-manual/development-environment.rst: remove obsolete poky repo references
-  overview-manual/yp-intro.rst: fix SDK type in bullet list
-  overview-manual/yp-intro.rst: remove obsolete poky repo references
-  overview-manual: convert YP-flow-diagram.png to SVG
-  ref-manual/classes.rst: remove obsolete poky repo references
-  ref-manual/devtool-reference.rst: remove obsolete poky repo references
-  ref-manual/faq.rst: remove obsolete poky repo references
-  ref-manual/features.rst: remove obsolete poky repo references
-  ref-manual/images.rst: remove obsolete poky repo references
-  ref-manual/release-process.rst: add a "Development Cycle" section
-  ref-manual/release-process.rst: remove obsolete poky repo references
-  ref-manual/release-process.rst: remove repeated "in the"
-  ref-manual/structure.rst: remove obsolete poky repo references
-  ref-manual/system-requirements.rst: fix wrong path to install-buildtools
-  ref-manual/system-requirements.rst: remove obsolete poky repo references
-  ref-manual/tasks.rst: remove obsolete poky repo references
-  ref-manual/terms.rst: refresh the Build Directory definition
-  ref-manual/terms.rst: refresh the OpenEmbedded build system definition
-  ref-manual/terms.rst: refresh the OpenEmbedded-Core definition
-  ref-manual/terms.rst: simplify the Source Directory definition
-  ref-manual/variables.rst: document the :term:`CCACHE_TOP_DIR` variable
-  ref-manual/variables.rst: remove obsolete poky repo references
-  ref-manual/yocto-project-supported-features.rst: remove obsolete poky repo reference
-  sdk-manual/appendix-obtain.rst: remove obsolete poky repo references
-  sdk-manual/intro.rst: remove obsolete poky repo references
-  sdk-manual: appending-customizing: use none lexer for BitBake code blocks
-  sdk-manual: appendix-obtain: fix default path for eSDK installer script
-  sdk-manual: appendix-obtain: replace directory structure PNG with a parsed-literal block
-  sdk-manual: appendix-obtain: replace eSDK directory structure PNG with a parsed-literal block
-  sdk-manual: appendix-obtain: use parsed-literal block for naming convention of the installer scripts
-  sdk-manual: delete sdk-title PNG
-  sdk-manual: fix improper indent of general form of tarball installer scripts
-  sdk-manual: fix incorrect highlight language for console code-blocks
-  sdk-manual: fix incorrect highlight language for text code-blocks
-  sdk-manual: replace sdk-environment PNG with SVG
-  sdk-manual: using: fix SDK filename example
-  sdk-manual: working-projects: properly highlight code code-blocks
-  security-manual/securing-images.rst: remove old links
-  test-manual/ptest.rst: detail the exit code and output requirements
-  test-manual/reproducible-builds.rst: remove obsolete poky repo references
-  test-manual/runtime-testing.rst: remove obsolete poky repo references
-  test-manual/understand-autobuilder.rst: remove obsolete poky repo references
-  transitioning-to-a-custom-environment.rst: remove obsolete poky repo references
-  tree-wide: figures: remove title PNGs
-  what-i-wish-id-known.rst: remove obsolete poky repo references


Known Issues in Yocto-5.3.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.3.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adarsh Jagadish Kamini
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Amaury Couderc
-  Ankur Tyagi
-  Antonin Godard
-  Bruce Ashfield
-  Dragomir, Daniel
-  Hongxu Jia
-  Hugo SIMELIERE
-  Jiaying Song
-  Ken Kurematsu
-  Khai Dang
-  Lee Chee Yang
-  Mark Hatle
-  Mathieu Dubois-Briand
-  Mohammad Rahimi
-  Paul Barker
-  Peter Marko
-  Quentin Schulz
-  Richard Purdie

Repositories / Downloads for Yocto-5.3.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`whinlatter </yocto-docs/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.2 </yocto-docs/log/?h=yocto-5.3.2>`
-  Git Revision: :yocto_git:`9c363d270ab45f3443391fb7275817db69d8a5d4 </yocto-docs/commit/?id=9c363d270ab45f3443391fb7275817db69d8a5d4>`
-  Release Artefact: yocto-docs-9c363d270ab45f3443391fb7275817db69d8a5d4
-  sha: ca6638b5ab0cfc60c2f6e43db765fe0e87242c733e07ed6da55d01228067cf1f
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.2/yocto-docs-9c363d270ab45f3443391fb7275817db69d8a5d4.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.2/yocto-docs-9c363d270ab45f3443391fb7275817db69d8a5d4.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`whinlatter </openembedded-core/log/?h=whinlatter>`
-  Tag:  :oe_git:`yocto-5.3.2 </openembedded-core/log/?h=yocto-5.3.2>`
-  Git Revision: :oe_git:`21314665e198c1abe458d0bad5c4d14d4c3ad856 </openembedded-core/commit/?id=21314665e198c1abe458d0bad5c4d14d4c3ad856>`
-  Release Artefact: oecore-21314665e198c1abe458d0bad5c4d14d4c3ad856
-  sha: 9ccfb0e279ccd1c683de2ed04db2f2b3acb81d9d50b7cbc0484fd2c1bc379a1e
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.2/oecore-21314665e198c1abe458d0bad5c4d14d4c3ad856.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.2/oecore-21314665e198c1abe458d0bad5c4d14d4c3ad856.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`whinlatter </meta-yocto/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.2 </meta-yocto/log/?h=yocto-5.3.2>`
-  Git Revision: :yocto_git:`691ff0fbeab54eceb26d90140d7a8f672532235b </meta-yocto/commit/?id=691ff0fbeab54eceb26d90140d7a8f672532235b>`
-  Release Artefact: meta-yocto-691ff0fbeab54eceb26d90140d7a8f672532235b
-  sha: 3ef0e1c99820e15eeda97c16579173f7818ea84964bc73a1859827d836be6405
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.2/meta-yocto-691ff0fbeab54eceb26d90140d7a8f672532235b.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.2/meta-yocto-691ff0fbeab54eceb26d90140d7a8f672532235b.tar.bz2

meta-qcom

-  Repository Location: :yocto_git:`/meta-qcom`
-  Branch: :yocto_git:`whinlatter </meta-qcom/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.2 </meta-qcom/log/?h=yocto-5.3.2>`
-  Git Revision: :yocto_git:`49bc2113adb0b86d463c311d32d4fb1007fe4845 </meta-qcom/commit/?id=49bc2113adb0b86d463c311d32d4fb1007fe4845>`
-  Release Artefact: meta-qcom-49bc2113adb0b86d463c311d32d4fb1007fe4845
-  sha: ccd82e54bfe30786cd0792175cb9b6b74c98d613ff42832036bcaa6e96582683
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.2/meta-qcom-49bc2113adb0b86d463c311d32d4fb1007fe4845.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.2/meta-qcom-49bc2113adb0b86d463c311d32d4fb1007fe4845.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`whinlatter </meta-mingw/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.2 </meta-mingw/log/?h=yocto-5.3.2>`
-  Git Revision: :yocto_git:`00323de97e397d4f6734ef2191806616989f5e10 </meta-mingw/commit/?id=00323de97e397d4f6734ef2191806616989f5e10>`
-  Release Artefact: meta-mingw-00323de97e397d4f6734ef2191806616989f5e10
-  sha: c9a70539b12c0642596fde6a2766d4a6a8fec8b2a366453fb6473363127a1c77
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.2/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.2/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.16 </bitbake/log/?h=2.16>`
-  Tag:  :oe_git:`yocto-5.3.2 </bitbake/log/?h=yocto-5.3.2>`
-  Git Revision: :oe_git:`7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0 </bitbake/commit/?id=7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0>`
-  Release Artefact: bitbake-7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0
-  sha: 06dd68908a5ffafae46ff7616e93a80f8b73c016f541e9517cf63b05f52f15c7
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.2/bitbake-7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.2/bitbake-7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0.tar.bz2

