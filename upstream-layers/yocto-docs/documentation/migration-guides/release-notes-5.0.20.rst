Release notes for Yocto-5.0.20 (Scarthgap)
------------------------------------------

Security Fixes in Yocto-5.0.20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-69645`, :cve_nist:`2025-69649`, :cve_nist:`2025-69652` and
   :cve_nist:`2026-6846`
-  busybox: Fix :cve_nist:`2026-38754`
-  bzip2: Fix :cve_nist:`2026-42250`
-  cargo: Fix :cve_nist:`2026-5222` and :cve_nist:`2026-5223`
-  cups: Fix :cve_nist:`2026-27447`, :cve_nist:`2026-34978`, :cve_nist:`2026-34979`,
   :cve_nist:`2026-34980`, :cve_nist:`2026-34990`, :cve_nist:`2026-39314`, :cve_nist:`2026-39316`
   and :cve_nist:`2026-41079`
-  curl: Ignore :cve_nist:`2026-10536` if nghttp2 not enabled
-  curl: Fix :cve_nist:`2026-4873`, :cve_nist:`2026-5545`, :cve_nist:`2026-5773`, :cve_nist:`2026-6253`
   and :cve_nist:`2026-6276`
-  dhcpcd: Fix :cve_nist:`2026-56113`, :cve_nist:`2026-56114` and :cve_nist:`2026-56117`
-  expat: Fix :cve_nist:`2026-41080`, :cve_nist:`2026-45186`, :cve_nist:`2026-56132`,
   :cve_nist:`2026-56403`, :cve_nist:`2026-56404`, :cve_nist:`2026-56405`, :cve_nist:`2026-56406`,
   :cve_nist:`2026-56407`, :cve_nist:`2026-56408`, :cve_nist:`2026-56409`, :cve_nist:`2026-56410`
   and :cve_nist:`2026-56411`
-  gawk: Fix :cve_nist:`2026-40467`, :cve_nist:`2026-40468`, :cve_nist:`2026-40469` and
   :cve_nist:`2026-40553`
-  glib-2.0: Fix :cve_nist:`2026-58010`, :cve_nist:`2026-58011`, :cve_nist:`2026-58012`,
   :cve_nist:`2026-58013`, :cve_nist:`2026-58014` and :cve_nist:`2026-58016`
-  gnutls: Fix :cve_nist:`2026-3833` and :cve_nist:`2026-42009`
-  gnutls: Ignore :cve_nist:`2026-3832`
-  gzip: Fix :cve_nist:`2026-41991` and :cve_nist:`2026-41992`
-  libcap: Fix :cve_nist:`2026-4878`
-  libpng: Fix :cve_nist:`2026-34757`
-  libsndfile1: Fix :cve_nist:`2026-37555`
-  libsolv: Fix :cve_nist:`2026-9149`
-  libssh2: Fix :cve_nist:`2025-15661`, :cve_nist:`2026-55199`, :cve_nist:`2026-55200`,
   :cve_nist:`2026-66032`, :cve_nist:`2026-66033`, :cve_nist:`2026-66034` and :cve_nist:`2026-66035`
-  libxml2: Fix :cve_nist:`2026-11979`
-  libxpm: Fix :cve_nist:`2026-4367`
-  linux-yocto/6.6: Fix :cve_nist:`2025-23131`, :cve_nist:`2025-68296`, :cve_nist:`2025-68736`,
   :cve_nist:`2025-68768`, :cve_nist:`2026-23278`, :cve_nist:`2026-23346`, :cve_nist:`2026-31419`,
   :cve_nist:`2026-31432`, :cve_nist:`2026-31486`, :cve_nist:`2026-43010`, :cve_nist:`2026-43019`,
   :cve_nist:`2026-43088`, :cve_nist:`2026-43116`, :cve_nist:`2026-43129`, :cve_nist:`2026-43219`,
   :cve_nist:`2026-43240`, :cve_nist:`2026-43303`, :cve_nist:`2026-43311`, :cve_nist:`2026-43331`,
   :cve_nist:`2026-43421`, :cve_nist:`2026-45850`, :cve_nist:`2026-45930`, :cve_nist:`2026-46054`,
   :cve_nist:`2026-46135`, :cve_nist:`2026-46140`, :cve_nist:`2026-46242`, :cve_nist:`2026-46252`,
   :cve_nist:`2026-46320`, :cve_nist:`2026-46321`, :cve_nist:`2026-46322`, :cve_nist:`2026-46331`,
   :cve_nist:`2026-52908`, :cve_nist:`2026-52909`, :cve_nist:`2026-52910`, :cve_nist:`2026-52913`,
   :cve_nist:`2026-52917`, :cve_nist:`2026-52923`, :cve_nist:`2026-52924`, :cve_nist:`2026-52927`,
   :cve_nist:`2026-52928`, :cve_nist:`2026-52929`, :cve_nist:`2026-52930`, :cve_nist:`2026-52934`,
   :cve_nist:`2026-52935`, :cve_nist:`2026-52939`, :cve_nist:`2026-52942`, :cve_nist:`2026-52943`,
   :cve_nist:`2026-52944`, :cve_nist:`2026-52946`, :cve_nist:`2026-52947`, :cve_nist:`2026-52948`,
   :cve_nist:`2026-53080`, :cve_nist:`2026-53131`, :cve_nist:`2026-53133`, :cve_nist:`2026-53134`,
   :cve_nist:`2026-53135`, :cve_nist:`2026-53136`, :cve_nist:`2026-53137`, :cve_nist:`2026-53138`,
   :cve_nist:`2026-53139`, :cve_nist:`2026-53143`, :cve_nist:`2026-53144`, :cve_nist:`2026-53146`,
   :cve_nist:`2026-53147`, :cve_nist:`2026-53148`, :cve_nist:`2026-53149`, :cve_nist:`2026-53150`,
   :cve_nist:`2026-53151`, :cve_nist:`2026-53154`, :cve_nist:`2026-53157`, :cve_nist:`2026-53158`,
   :cve_nist:`2026-53159`, :cve_nist:`2026-53160`, :cve_nist:`2026-53161`, :cve_nist:`2026-53163`,
   :cve_nist:`2026-53167`, :cve_nist:`2026-53168`, :cve_nist:`2026-53176`, :cve_nist:`2026-53177`,
   :cve_nist:`2026-53181`, :cve_nist:`2026-53182`, :cve_nist:`2026-53183`, :cve_nist:`2026-53184`,
   :cve_nist:`2026-53185`, :cve_nist:`2026-53186`, :cve_nist:`2026-53189`, :cve_nist:`2026-53190`,
   :cve_nist:`2026-53194`, :cve_nist:`2026-53195`, :cve_nist:`2026-53196`, :cve_nist:`2026-53198`,
   :cve_nist:`2026-53199`, :cve_nist:`2026-53207`, :cve_nist:`2026-53208`, :cve_nist:`2026-53209`,
   :cve_nist:`2026-53212`, :cve_nist:`2026-53213`, :cve_nist:`2026-53214`, :cve_nist:`2026-53215`,
   :cve_nist:`2026-53216`, :cve_nist:`2026-53217`, :cve_nist:`2026-53218`, :cve_nist:`2026-53219`,
   :cve_nist:`2026-53221`, :cve_nist:`2026-53223`, :cve_nist:`2026-53225`, :cve_nist:`2026-53227`,
   :cve_nist:`2026-53228`, :cve_nist:`2026-53230`, :cve_nist:`2026-53232`, :cve_nist:`2026-53236`,
   :cve_nist:`2026-53237`, :cve_nist:`2026-53238`, :cve_nist:`2026-53239`, :cve_nist:`2026-53242`,
   :cve_nist:`2026-53245`, :cve_nist:`2026-53247`, :cve_nist:`2026-53249`, :cve_nist:`2026-53252`,
   :cve_nist:`2026-53253`, :cve_nist:`2026-53254`, :cve_nist:`2026-53255`, :cve_nist:`2026-53256`,
   :cve_nist:`2026-53263`, :cve_nist:`2026-53264`, :cve_nist:`2026-53265`, :cve_nist:`2026-53266`,
   :cve_nist:`2026-53267`, :cve_nist:`2026-53268`, :cve_nist:`2026-53269`, :cve_nist:`2026-53270`,
   :cve_nist:`2026-53271`, :cve_nist:`2026-53273`, :cve_nist:`2026-53274`, :cve_nist:`2026-53275`,
   :cve_nist:`2026-53325`, :cve_nist:`2026-53327`, :cve_nist:`2026-53329`, :cve_nist:`2026-53331`,
   :cve_nist:`2026-53336`, :cve_nist:`2026-53337`, :cve_nist:`2026-53339`, :cve_nist:`2026-53343`,
   :cve_nist:`2026-53345`, :cve_nist:`2026-53347`, :cve_nist:`2026-53349`, :cve_nist:`2026-53350`,
   :cve_nist:`2026-53352`, :cve_nist:`2026-53353`, :cve_nist:`2026-53354`, :cve_nist:`2026-53355`,
   :cve_nist:`2026-53356`, :cve_nist:`2026-53358`, :cve_nist:`2026-53359`, :cve_nist:`2026-53361`,
   :cve_nist:`2026-53362`, :cve_nist:`2026-53366`, :cve_nist:`2026-53381`, :cve_nist:`2026-53382`,
   :cve_nist:`2026-53383`, :cve_nist:`2026-53384`, :cve_nist:`2026-53385`, :cve_nist:`2026-53388`,
   :cve_nist:`2026-53390`, :cve_nist:`2026-53391`, :cve_nist:`2026-53397`, :cve_nist:`2026-53398`,
   :cve_nist:`2026-53403`, :cve_nist:`2026-63794`, :cve_nist:`2026-63795`, :cve_nist:`2026-63796`,
   :cve_nist:`2026-63797`, :cve_nist:`2026-63798`, :cve_nist:`2026-63800`, :cve_nist:`2026-63801`,
   :cve_nist:`2026-63802`, :cve_nist:`2026-63803`, :cve_nist:`2026-63804`, :cve_nist:`2026-63807`,
   :cve_nist:`2026-63808`, :cve_nist:`2026-63809`, :cve_nist:`2026-63812`, :cve_nist:`2026-63814`,
   :cve_nist:`2026-63817`, :cve_nist:`2026-63821`, :cve_nist:`2026-63822`, :cve_nist:`2026-63823`,
   :cve_nist:`2026-63824`, :cve_nist:`2026-63826`, :cve_nist:`2026-63827`, :cve_nist:`2026-63828`,
   :cve_nist:`2026-63830`, :cve_nist:`2026-63831`, :cve_nist:`2026-63833`, :cve_nist:`2026-63834`,
   :cve_nist:`2026-63835`, :cve_nist:`2026-63836`, :cve_nist:`2026-63867`, :cve_nist:`2026-63868`,
   :cve_nist:`2026-63870`, :cve_nist:`2026-63875`, :cve_nist:`2026-63876`, :cve_nist:`2026-63877`,
   :cve_nist:`2026-63881`, :cve_nist:`2026-63882`, :cve_nist:`2026-63883`, :cve_nist:`2026-63884`,
   :cve_nist:`2026-63886`, :cve_nist:`2026-63887`, :cve_nist:`2026-63888`, :cve_nist:`2026-63889`,
   :cve_nist:`2026-63890`, :cve_nist:`2026-63891`, :cve_nist:`2026-63892`, :cve_nist:`2026-63893`,
   :cve_nist:`2026-63895`, :cve_nist:`2026-63896`, :cve_nist:`2026-63897`, :cve_nist:`2026-63898`,
   :cve_nist:`2026-63899`, :cve_nist:`2026-63900`, :cve_nist:`2026-63901`, :cve_nist:`2026-63902`,
   :cve_nist:`2026-63903`, :cve_nist:`2026-63904`, :cve_nist:`2026-63905`, :cve_nist:`2026-63906`,
   :cve_nist:`2026-63908`, :cve_nist:`2026-63909`, :cve_nist:`2026-63912`, :cve_nist:`2026-63913`,
   :cve_nist:`2026-63914`, :cve_nist:`2026-63915`, :cve_nist:`2026-63916`, :cve_nist:`2026-63917`,
   :cve_nist:`2026-63919`, :cve_nist:`2026-63920`, :cve_nist:`2026-63921`, :cve_nist:`2026-63922`,
   :cve_nist:`2026-63924`, :cve_nist:`2026-63925`, :cve_nist:`2026-63926`, :cve_nist:`2026-63927`,
   :cve_nist:`2026-63928`, :cve_nist:`2026-63930`, :cve_nist:`2026-63931`, :cve_nist:`2026-63933`,
   :cve_nist:`2026-63934`, :cve_nist:`2026-63942`, :cve_nist:`2026-63943`, :cve_nist:`2026-63944`,
   :cve_nist:`2026-63945`, :cve_nist:`2026-63946`, :cve_nist:`2026-63947`, :cve_nist:`2026-63948`,
   :cve_nist:`2026-63949`, :cve_nist:`2026-63952`, :cve_nist:`2026-63954`, :cve_nist:`2026-63956`,
   :cve_nist:`2026-63957`, :cve_nist:`2026-63958`, :cve_nist:`2026-63959`, :cve_nist:`2026-63960`,
   :cve_nist:`2026-63961`, :cve_nist:`2026-63964`, :cve_nist:`2026-63967`, :cve_nist:`2026-63968`,
   :cve_nist:`2026-63969`, :cve_nist:`2026-63971`, :cve_nist:`2026-63973`, :cve_nist:`2026-63975`,
   :cve_nist:`2026-63976`, :cve_nist:`2026-63984`, :cve_nist:`2026-63985`, :cve_nist:`2026-63990`,
   :cve_nist:`2026-63991`, :cve_nist:`2026-63992`, :cve_nist:`2026-63993`, :cve_nist:`2026-63994`,
   :cve_nist:`2026-64000`, :cve_nist:`2026-64002`, :cve_nist:`2026-64003`, :cve_nist:`2026-64004`,
   :cve_nist:`2026-64005`, :cve_nist:`2026-64006`, :cve_nist:`2026-64007`, :cve_nist:`2026-64009`,
   :cve_nist:`2026-64010`, :cve_nist:`2026-64011`, :cve_nist:`2026-64012`, :cve_nist:`2026-64014`,
   :cve_nist:`2026-64026`, :cve_nist:`2026-64090`, :cve_nist:`2026-64091`, :cve_nist:`2026-64093`,
   :cve_nist:`2026-64094`, :cve_nist:`2026-64095`, :cve_nist:`2026-64109`, :cve_nist:`2026-64116`,
   :cve_nist:`2026-64123`, :cve_nist:`2026-64131`, :cve_nist:`2026-64137`, :cve_nist:`2026-64188`
   and :cve_nist:`2026-64191`
-  linux-yocto/6.6: Ignore :cve_nist:`2023-52920`, :cve_nist:`2024-14027`, :cve_nist:`2024-27022`,
   :cve_nist:`2024-43826`, :cve_nist:`2024-46691`, :cve_nist:`2024-56647`, :cve_nist:`2024-58093`,
   :cve_nist:`2025-21682`, :cve_nist:`2025-21739`, :cve_nist:`2025-21817`, :cve_nist:`2025-22125`,
   :cve_nist:`2025-38164`, :cve_nist:`2025-38426`, :cve_nist:`2025-38531`, :cve_nist:`2025-38584`,
   :cve_nist:`2025-38704`, :cve_nist:`2025-38710`, :cve_nist:`2025-39748`, :cve_nist:`2025-39764`,
   :cve_nist:`2025-39930`, :cve_nist:`2025-39981`, :cve_nist:`2025-40135`, :cve_nist:`2025-40150`,
   :cve_nist:`2025-40219`, :cve_nist:`2025-40242`, :cve_nist:`2025-68206`, :cve_nist:`2025-68239`,
   :cve_nist:`2025-68315`, :cve_nist:`2025-68334`, :cve_nist:`2025-71152`, :cve_nist:`2025-71161`,
   :cve_nist:`2025-71184`, :cve_nist:`2025-71203`, :cve_nist:`2025-71221`, :cve_nist:`2025-71239`,
   :cve_nist:`2025-71265`, :cve_nist:`2025-71266`, :cve_nist:`2025-71267`, :cve_nist:`2025-71269`,
   :cve_nist:`2025-71274`, :cve_nist:`2025-71286`, :cve_nist:`2025-71287`, :cve_nist:`2025-71288`,
   :cve_nist:`2025-71291`, :cve_nist:`2025-71292`, :cve_nist:`2025-71295`, :cve_nist:`2025-71297`,
   :cve_nist:`2025-71303`, :cve_nist:`2025-71304`, :cve_nist:`2025-71305`, :cve_nist:`2025-71306`,
   :cve_nist:`2025-71307`, :cve_nist:`2025-71308`, :cve_nist:`2025-71309`, :cve_nist:`2025-71311`,
   :cve_nist:`2025-71312`, :cve_nist:`2025-71314`, :cve_nist:`2026-23004`, :cve_nist:`2026-23050`,
   :cve_nist:`2026-23053`, :cve_nist:`2026-23066`, :cve_nist:`2026-23118`, :cve_nist:`2026-23138`,
   :cve_nist:`2026-23154`, :cve_nist:`2026-23157`, :cve_nist:`2026-23171`, :cve_nist:`2026-23227`,
   :cve_nist:`2026-23231`, :cve_nist:`2026-23242`, :cve_nist:`2026-23243`, :cve_nist:`2026-23244`,
   :cve_nist:`2026-23245`, :cve_nist:`2026-23246`, :cve_nist:`2026-23253`, :cve_nist:`2026-23255`,
   :cve_nist:`2026-23268`, :cve_nist:`2026-23269`, :cve_nist:`2026-23270`, :cve_nist:`2026-23271`,
   :cve_nist:`2026-23272`, :cve_nist:`2026-23273`, :cve_nist:`2026-23274`, :cve_nist:`2026-23277`,
   :cve_nist:`2026-23279`, :cve_nist:`2026-23281`, :cve_nist:`2026-23284`, :cve_nist:`2026-23285`,
   :cve_nist:`2026-23286`, :cve_nist:`2026-23287`, :cve_nist:`2026-23289`, :cve_nist:`2026-23290`,
   :cve_nist:`2026-23291`, :cve_nist:`2026-23292`, :cve_nist:`2026-23293`, :cve_nist:`2026-23296`,
   :cve_nist:`2026-23298`, :cve_nist:`2026-23300`, :cve_nist:`2026-23302`, :cve_nist:`2026-23303`,
   :cve_nist:`2026-23304`, :cve_nist:`2026-23306`, :cve_nist:`2026-23307`, :cve_nist:`2026-23308`,
   :cve_nist:`2026-23309`, :cve_nist:`2026-23310`, :cve_nist:`2026-23312`, :cve_nist:`2026-23313`,
   :cve_nist:`2026-23315`, :cve_nist:`2026-23317`, :cve_nist:`2026-23318`, :cve_nist:`2026-23319`,
   :cve_nist:`2026-23321`, :cve_nist:`2026-23324`, :cve_nist:`2026-23325`, :cve_nist:`2026-23330`,
   :cve_nist:`2026-23334`, :cve_nist:`2026-23335`, :cve_nist:`2026-23336`, :cve_nist:`2026-23339`,
   :cve_nist:`2026-23340`, :cve_nist:`2026-23343`, :cve_nist:`2026-23347`, :cve_nist:`2026-23351`,
   :cve_nist:`2026-23352`, :cve_nist:`2026-23356`, :cve_nist:`2026-23357`, :cve_nist:`2026-23359`,
   :cve_nist:`2026-23360`, :cve_nist:`2026-23362`, :cve_nist:`2026-23364`, :cve_nist:`2026-23365`,
   :cve_nist:`2026-23367`, :cve_nist:`2026-23368`, :cve_nist:`2026-23370`, :cve_nist:`2026-23372`,
   :cve_nist:`2026-23374`, :cve_nist:`2026-23378`, :cve_nist:`2026-23379`, :cve_nist:`2026-23381`,
   :cve_nist:`2026-23382`, :cve_nist:`2026-23386`, :cve_nist:`2026-23387`, :cve_nist:`2026-23388`,
   :cve_nist:`2026-23389`, :cve_nist:`2026-23391`, :cve_nist:`2026-23392`, :cve_nist:`2026-23394`,
   :cve_nist:`2026-23395`, :cve_nist:`2026-23396`, :cve_nist:`2026-23397`, :cve_nist:`2026-23398`,
   :cve_nist:`2026-23399`, :cve_nist:`2026-23401`, :cve_nist:`2026-23403`, :cve_nist:`2026-23404`,
   :cve_nist:`2026-23405`, :cve_nist:`2026-23406`, :cve_nist:`2026-23407`, :cve_nist:`2026-23408`,
   :cve_nist:`2026-23409`, :cve_nist:`2026-23410`, :cve_nist:`2026-23411`, :cve_nist:`2026-23412`,
   :cve_nist:`2026-23413`, :cve_nist:`2026-23414`, :cve_nist:`2026-23419`, :cve_nist:`2026-23420`,
   :cve_nist:`2026-23422`, :cve_nist:`2026-23426`, :cve_nist:`2026-23427`, :cve_nist:`2026-23428`,
   :cve_nist:`2026-23434`, :cve_nist:`2026-23438`, :cve_nist:`2026-23439`, :cve_nist:`2026-23440`,
   :cve_nist:`2026-23441`, :cve_nist:`2026-23442`, :cve_nist:`2026-23443`, :cve_nist:`2026-23444`,
   :cve_nist:`2026-23446`, :cve_nist:`2026-23447`, :cve_nist:`2026-23448`, :cve_nist:`2026-23449`,
   :cve_nist:`2026-23450`, :cve_nist:`2026-23452`, :cve_nist:`2026-23454`, :cve_nist:`2026-23455`,
   :cve_nist:`2026-23456`, :cve_nist:`2026-23457`, :cve_nist:`2026-23458`, :cve_nist:`2026-23460`,
   :cve_nist:`2026-23461`, :cve_nist:`2026-23462`, :cve_nist:`2026-23463`, :cve_nist:`2026-23465`,
   :cve_nist:`2026-23468`, :cve_nist:`2026-23474`, :cve_nist:`2026-23475`, :cve_nist:`2026-31389`,
   :cve_nist:`2026-31391`, :cve_nist:`2026-31392`, :cve_nist:`2026-31393`, :cve_nist:`2026-31396`,
   :cve_nist:`2026-31399`, :cve_nist:`2026-31400`, :cve_nist:`2026-31402`, :cve_nist:`2026-31403`,
   :cve_nist:`2026-31405`, :cve_nist:`2026-31407`, :cve_nist:`2026-31408`, :cve_nist:`2026-31409`,
   :cve_nist:`2026-31411`, :cve_nist:`2026-31412`, :cve_nist:`2026-31414`, :cve_nist:`2026-31415`,
   :cve_nist:`2026-31416`, :cve_nist:`2026-31417`, :cve_nist:`2026-31418`, :cve_nist:`2026-31421`,
   :cve_nist:`2026-31422`, :cve_nist:`2026-31423`, :cve_nist:`2026-31424`, :cve_nist:`2026-31425`,
   :cve_nist:`2026-31426`, :cve_nist:`2026-31427`, :cve_nist:`2026-31428`, :cve_nist:`2026-31429`,
   :cve_nist:`2026-31430`, :cve_nist:`2026-31431`, :cve_nist:`2026-31433`, :cve_nist:`2026-31434`,
   :cve_nist:`2026-31439`, :cve_nist:`2026-31440`, :cve_nist:`2026-31441`, :cve_nist:`2026-31444`,
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
   :cve_nist:`2026-31525`, :cve_nist:`2026-31527`, :cve_nist:`2026-31528`, :cve_nist:`2026-31532`,
   :cve_nist:`2026-31533`, :cve_nist:`2026-31540`, :cve_nist:`2026-31542`, :cve_nist:`2026-31545`,
   :cve_nist:`2026-31546`, :cve_nist:`2026-31548`, :cve_nist:`2026-31549`, :cve_nist:`2026-31550`,
   :cve_nist:`2026-31551`, :cve_nist:`2026-31552`, :cve_nist:`2026-31555`, :cve_nist:`2026-31563`,
   :cve_nist:`2026-31565`, :cve_nist:`2026-31566`, :cve_nist:`2026-31570`, :cve_nist:`2026-31576`,
   :cve_nist:`2026-31577`, :cve_nist:`2026-31578`, :cve_nist:`2026-31580`, :cve_nist:`2026-31581`,
   :cve_nist:`2026-31583`, :cve_nist:`2026-31584`, :cve_nist:`2026-31585`, :cve_nist:`2026-31586`,
   :cve_nist:`2026-31587`, :cve_nist:`2026-31588`, :cve_nist:`2026-31590`, :cve_nist:`2026-31594`,
   :cve_nist:`2026-31595`, :cve_nist:`2026-31596`, :cve_nist:`2026-31597`, :cve_nist:`2026-31598`,
   :cve_nist:`2026-31599`, :cve_nist:`2026-31602`, :cve_nist:`2026-31603`, :cve_nist:`2026-31604`,
   :cve_nist:`2026-31605`, :cve_nist:`2026-31607`, :cve_nist:`2026-31610`, :cve_nist:`2026-31611`,
   :cve_nist:`2026-31612`, :cve_nist:`2026-31613`, :cve_nist:`2026-31614`, :cve_nist:`2026-31615`,
   :cve_nist:`2026-31616`, :cve_nist:`2026-31617`, :cve_nist:`2026-31618`, :cve_nist:`2026-31619`,
   :cve_nist:`2026-31622`, :cve_nist:`2026-31623`, :cve_nist:`2026-31624`, :cve_nist:`2026-31625`,
   :cve_nist:`2026-31626`, :cve_nist:`2026-31627`, :cve_nist:`2026-31628`, :cve_nist:`2026-31629`,
   :cve_nist:`2026-31634`, :cve_nist:`2026-31637`, :cve_nist:`2026-31638`, :cve_nist:`2026-31639`,
   :cve_nist:`2026-31642`, :cve_nist:`2026-31646`, :cve_nist:`2026-31648`, :cve_nist:`2026-31649`,
   :cve_nist:`2026-31651`, :cve_nist:`2026-31655`, :cve_nist:`2026-31656`, :cve_nist:`2026-31657`,
   :cve_nist:`2026-31658`, :cve_nist:`2026-31659`, :cve_nist:`2026-31660`, :cve_nist:`2026-31661`,
   :cve_nist:`2026-31662`, :cve_nist:`2026-31664`, :cve_nist:`2026-31665`, :cve_nist:`2026-31667`,
   :cve_nist:`2026-31668`, :cve_nist:`2026-31669`, :cve_nist:`2026-31670`, :cve_nist:`2026-31671`,
   :cve_nist:`2026-31672`, :cve_nist:`2026-31673`, :cve_nist:`2026-31674`, :cve_nist:`2026-31675`,
   :cve_nist:`2026-31676`, :cve_nist:`2026-31678`, :cve_nist:`2026-31679`, :cve_nist:`2026-31680`,
   :cve_nist:`2026-31681`, :cve_nist:`2026-31682`, :cve_nist:`2026-31683`, :cve_nist:`2026-31684`,
   :cve_nist:`2026-31685`, :cve_nist:`2026-31686`, :cve_nist:`2026-31689`, :cve_nist:`2026-31693`,
   :cve_nist:`2026-31694`, :cve_nist:`2026-31695`, :cve_nist:`2026-31696`, :cve_nist:`2026-31697`,
   :cve_nist:`2026-31698`, :cve_nist:`2026-31699`, :cve_nist:`2026-31700`, :cve_nist:`2026-31701`,
   :cve_nist:`2026-31702`, :cve_nist:`2026-31704`, :cve_nist:`2026-31705`, :cve_nist:`2026-31707`,
   :cve_nist:`2026-31708`, :cve_nist:`2026-31709`, :cve_nist:`2026-31711`, :cve_nist:`2026-31712`,
   :cve_nist:`2026-31714`, :cve_nist:`2026-31715`, :cve_nist:`2026-31716`, :cve_nist:`2026-31717`,
   :cve_nist:`2026-31718`, :cve_nist:`2026-31720`, :cve_nist:`2026-31721`, :cve_nist:`2026-31726`,
   :cve_nist:`2026-31728`, :cve_nist:`2026-31730`, :cve_nist:`2026-31737`, :cve_nist:`2026-31738`,
   :cve_nist:`2026-31740`, :cve_nist:`2026-31741`, :cve_nist:`2026-31747`, :cve_nist:`2026-31748`,
   :cve_nist:`2026-31749`, :cve_nist:`2026-31751`, :cve_nist:`2026-31752`, :cve_nist:`2026-31754`,
   :cve_nist:`2026-31755`, :cve_nist:`2026-31756`, :cve_nist:`2026-31758`, :cve_nist:`2026-31759`,
   :cve_nist:`2026-31761`, :cve_nist:`2026-31762`, :cve_nist:`2026-31763`, :cve_nist:`2026-31768`,
   :cve_nist:`2026-31770`, :cve_nist:`2026-31773`, :cve_nist:`2026-31778`, :cve_nist:`2026-31779`,
   :cve_nist:`2026-31780`, :cve_nist:`2026-31781`, :cve_nist:`2026-31786`, :cve_nist:`2026-31787`,
   :cve_nist:`2026-31788`, :cve_nist:`2026-43007`, :cve_nist:`2026-43011`, :cve_nist:`2026-43013`,
   :cve_nist:`2026-43014`, :cve_nist:`2026-43015`, :cve_nist:`2026-43016`, :cve_nist:`2026-43017`,
   :cve_nist:`2026-43018`, :cve_nist:`2026-43020`, :cve_nist:`2026-43023`, :cve_nist:`2026-43024`,
   :cve_nist:`2026-43025`, :cve_nist:`2026-43026`, :cve_nist:`2026-43027`, :cve_nist:`2026-43028`,
   :cve_nist:`2026-43030`, :cve_nist:`2026-43032`, :cve_nist:`2026-43033`, :cve_nist:`2026-43035`,
   :cve_nist:`2026-43037`, :cve_nist:`2026-43038`, :cve_nist:`2026-43040`, :cve_nist:`2026-43041`,
   :cve_nist:`2026-43043`, :cve_nist:`2026-43044`, :cve_nist:`2026-43046`, :cve_nist:`2026-43047`,
   :cve_nist:`2026-43050`, :cve_nist:`2026-43051`, :cve_nist:`2026-43052`, :cve_nist:`2026-43054`,
   :cve_nist:`2026-43056`, :cve_nist:`2026-43057`, :cve_nist:`2026-43058`, :cve_nist:`2026-43060`,
   :cve_nist:`2026-43061`, :cve_nist:`2026-43062`, :cve_nist:`2026-43064`, :cve_nist:`2026-43065`,
   :cve_nist:`2026-43066`, :cve_nist:`2026-43067`, :cve_nist:`2026-43068`, :cve_nist:`2026-43069`,
   :cve_nist:`2026-43071`, :cve_nist:`2026-43072`, :cve_nist:`2026-43074`, :cve_nist:`2026-43075`,
   :cve_nist:`2026-43076`, :cve_nist:`2026-43077`, :cve_nist:`2026-43078`, :cve_nist:`2026-43079`,
   :cve_nist:`2026-43080`, :cve_nist:`2026-43081`, :cve_nist:`2026-43082`, :cve_nist:`2026-43085`,
   :cve_nist:`2026-43086`, :cve_nist:`2026-43089`, :cve_nist:`2026-43091`, :cve_nist:`2026-43092`,
   :cve_nist:`2026-43093`, :cve_nist:`2026-43094`, :cve_nist:`2026-43098`, :cve_nist:`2026-43099`,
   :cve_nist:`2026-43103`, :cve_nist:`2026-43104`, :cve_nist:`2026-43105`, :cve_nist:`2026-43109`,
   :cve_nist:`2026-43110`, :cve_nist:`2026-43111`, :cve_nist:`2026-43112`, :cve_nist:`2026-43113`,
   :cve_nist:`2026-43114`, :cve_nist:`2026-43117`, :cve_nist:`2026-43120`, :cve_nist:`2026-43123`,
   :cve_nist:`2026-43124`, :cve_nist:`2026-43128`, :cve_nist:`2026-43130`, :cve_nist:`2026-43132`,
   :cve_nist:`2026-43133`, :cve_nist:`2026-43134`, :cve_nist:`2026-43135`, :cve_nist:`2026-43136`,
   :cve_nist:`2026-43137`, :cve_nist:`2026-43139`, :cve_nist:`2026-43140`, :cve_nist:`2026-43141`,
   :cve_nist:`2026-43143`, :cve_nist:`2026-43145`, :cve_nist:`2026-43147`, :cve_nist:`2026-43148`,
   :cve_nist:`2026-43149`, :cve_nist:`2026-43150`, :cve_nist:`2026-43152`, :cve_nist:`2026-43156`,
   :cve_nist:`2026-43157`, :cve_nist:`2026-43158`, :cve_nist:`2026-43159`, :cve_nist:`2026-43162`,
   :cve_nist:`2026-43163`, :cve_nist:`2026-43167`, :cve_nist:`2026-43168`, :cve_nist:`2026-43170`,
   :cve_nist:`2026-43171`, :cve_nist:`2026-43173`, :cve_nist:`2026-43180`, :cve_nist:`2026-43182`,
   :cve_nist:`2026-43183`, :cve_nist:`2026-43184`, :cve_nist:`2026-43186`, :cve_nist:`2026-43187`,
   :cve_nist:`2026-43189`, :cve_nist:`2026-43190`, :cve_nist:`2026-43194` and :cve_nist:`2026-43196`
-  linux-yocto/6.6: (cont.) Ignore :cve_nist:`2026-43200`, :cve_nist:`2026-43202`, :cve_nist:`2026-43203`,
   :cve_nist:`2026-43205`, :cve_nist:`2026-43206`, :cve_nist:`2026-43207`, :cve_nist:`2026-43209`,
   :cve_nist:`2026-43211`, :cve_nist:`2026-43212`, :cve_nist:`2026-43214`, :cve_nist:`2026-43215`,
   :cve_nist:`2026-43218`, :cve_nist:`2026-43220`, :cve_nist:`2026-43221`, :cve_nist:`2026-43222`,
   :cve_nist:`2026-43223`, :cve_nist:`2026-43225`, :cve_nist:`2026-43226`, :cve_nist:`2026-43227`,
   :cve_nist:`2026-43230`, :cve_nist:`2026-43231`, :cve_nist:`2026-43232`, :cve_nist:`2026-43233`,
   :cve_nist:`2026-43236`, :cve_nist:`2026-43238`, :cve_nist:`2026-43239`, :cve_nist:`2026-43241`,
   :cve_nist:`2026-43242`, :cve_nist:`2026-43245`, :cve_nist:`2026-43246`, :cve_nist:`2026-43251`,
   :cve_nist:`2026-43252`, :cve_nist:`2026-43253`, :cve_nist:`2026-43255`, :cve_nist:`2026-43256`,
   :cve_nist:`2026-43257`, :cve_nist:`2026-43261`, :cve_nist:`2026-43262`, :cve_nist:`2026-43264`,
   :cve_nist:`2026-43265`, :cve_nist:`2026-43266`, :cve_nist:`2026-43268`, :cve_nist:`2026-43269`,
   :cve_nist:`2026-43270`, :cve_nist:`2026-43271`, :cve_nist:`2026-43273`, :cve_nist:`2026-43275`,
   :cve_nist:`2026-43277`, :cve_nist:`2026-43278`, :cve_nist:`2026-43279`, :cve_nist:`2026-43281`,
   :cve_nist:`2026-43283`, :cve_nist:`2026-43284`, :cve_nist:`2026-43287`, :cve_nist:`2026-43288`,
   :cve_nist:`2026-43289`, :cve_nist:`2026-43291`, :cve_nist:`2026-43295`, :cve_nist:`2026-43296`,
   :cve_nist:`2026-43302`, :cve_nist:`2026-43304`, :cve_nist:`2026-43312`, :cve_nist:`2026-43313`,
   :cve_nist:`2026-43314`, :cve_nist:`2026-43315`, :cve_nist:`2026-43316`, :cve_nist:`2026-43319`,
   :cve_nist:`2026-43324`, :cve_nist:`2026-43327`, :cve_nist:`2026-43328`, :cve_nist:`2026-43329`,
   :cve_nist:`2026-43330`, :cve_nist:`2026-43332`, :cve_nist:`2026-43333`, :cve_nist:`2026-43334`,
   :cve_nist:`2026-43336`, :cve_nist:`2026-43339`, :cve_nist:`2026-43340`, :cve_nist:`2026-43341`,
   :cve_nist:`2026-43342`, :cve_nist:`2026-43343`, :cve_nist:`2026-43345`, :cve_nist:`2026-43350`,
   :cve_nist:`2026-43355`, :cve_nist:`2026-43357`, :cve_nist:`2026-43359`, :cve_nist:`2026-43360`,
   :cve_nist:`2026-43361`, :cve_nist:`2026-43362`, :cve_nist:`2026-43363`, :cve_nist:`2026-43365`,
   :cve_nist:`2026-43366`, :cve_nist:`2026-43368`, :cve_nist:`2026-43370`, :cve_nist:`2026-43371`,
   :cve_nist:`2026-43372`, :cve_nist:`2026-43373`, :cve_nist:`2026-43376`, :cve_nist:`2026-43377`,
   :cve_nist:`2026-43378`, :cve_nist:`2026-43379`, :cve_nist:`2026-43380`, :cve_nist:`2026-43381`,
   :cve_nist:`2026-43382`, :cve_nist:`2026-43383`, :cve_nist:`2026-43386`, :cve_nist:`2026-43387`,
   :cve_nist:`2026-43397`, :cve_nist:`2026-43405`, :cve_nist:`2026-43406`, :cve_nist:`2026-43407`,
   :cve_nist:`2026-43409`, :cve_nist:`2026-43411`, :cve_nist:`2026-43412`, :cve_nist:`2026-43413`,
   :cve_nist:`2026-43415`, :cve_nist:`2026-43419`, :cve_nist:`2026-43420`, :cve_nist:`2026-43424`,
   :cve_nist:`2026-43425`, :cve_nist:`2026-43426`, :cve_nist:`2026-43427`, :cve_nist:`2026-43428`,
   :cve_nist:`2026-43429`, :cve_nist:`2026-43430`, :cve_nist:`2026-43432`, :cve_nist:`2026-43436`,
   :cve_nist:`2026-43437`, :cve_nist:`2026-43439`, :cve_nist:`2026-43441`, :cve_nist:`2026-43445`,
   :cve_nist:`2026-43448`, :cve_nist:`2026-43449`, :cve_nist:`2026-43450`, :cve_nist:`2026-43451`,
   :cve_nist:`2026-43452`, :cve_nist:`2026-43453`, :cve_nist:`2026-43455`, :cve_nist:`2026-43457`,
   :cve_nist:`2026-43458`, :cve_nist:`2026-43459`, :cve_nist:`2026-43466`, :cve_nist:`2026-43468`,
   :cve_nist:`2026-43469`, :cve_nist:`2026-43471`, :cve_nist:`2026-43472`, :cve_nist:`2026-43473`,
   :cve_nist:`2026-43475`, :cve_nist:`2026-43476`, :cve_nist:`2026-43480`, :cve_nist:`2026-43483`,
   :cve_nist:`2026-43484`, :cve_nist:`2026-43488`, :cve_nist:`2026-43490`, :cve_nist:`2026-43491`,
   :cve_nist:`2026-43492`, :cve_nist:`2026-43493`, :cve_nist:`2026-43494`, :cve_nist:`2026-43495`,
   :cve_nist:`2026-43496`, :cve_nist:`2026-43497`, :cve_nist:`2026-43499`, :cve_nist:`2026-43500`,
   :cve_nist:`2026-43501`, :cve_nist:`2026-43502`, :cve_nist:`2026-43503`, :cve_nist:`2026-45834`,
   :cve_nist:`2026-45835`, :cve_nist:`2026-45836`, :cve_nist:`2026-45837`, :cve_nist:`2026-45838`,
   :cve_nist:`2026-45839`, :cve_nist:`2026-45840`, :cve_nist:`2026-45841`, :cve_nist:`2026-45842`,
   :cve_nist:`2026-45843`, :cve_nist:`2026-45844`, :cve_nist:`2026-45845`, :cve_nist:`2026-45846`,
   :cve_nist:`2026-45847`, :cve_nist:`2026-45848`, :cve_nist:`2026-45849`, :cve_nist:`2026-45851`,
   :cve_nist:`2026-45852`, :cve_nist:`2026-45853`, :cve_nist:`2026-45854`, :cve_nist:`2026-45856`,
   :cve_nist:`2026-45857`, :cve_nist:`2026-45858`, :cve_nist:`2026-45860`, :cve_nist:`2026-45862`,
   :cve_nist:`2026-45863`, :cve_nist:`2026-45864`, :cve_nist:`2026-45865`, :cve_nist:`2026-45866`,
   :cve_nist:`2026-45867`, :cve_nist:`2026-45868`, :cve_nist:`2026-45869`, :cve_nist:`2026-45870`,
   :cve_nist:`2026-45871`, :cve_nist:`2026-45872`, :cve_nist:`2026-45873`, :cve_nist:`2026-45874`,
   :cve_nist:`2026-45875`, :cve_nist:`2026-45876`, :cve_nist:`2026-45878`, :cve_nist:`2026-45879`,
   :cve_nist:`2026-45880`, :cve_nist:`2026-45881`, :cve_nist:`2026-45882`, :cve_nist:`2026-45883`,
   :cve_nist:`2026-45884`, :cve_nist:`2026-45885`, :cve_nist:`2026-45886`, :cve_nist:`2026-45887`,
   :cve_nist:`2026-45888`, :cve_nist:`2026-45889`, :cve_nist:`2026-45890`, :cve_nist:`2026-45891`,
   :cve_nist:`2026-45892`, :cve_nist:`2026-45895`, :cve_nist:`2026-45896`, :cve_nist:`2026-45898`,
   :cve_nist:`2026-45899`, :cve_nist:`2026-45900`, :cve_nist:`2026-45902`, :cve_nist:`2026-45903`,
   :cve_nist:`2026-45904`, :cve_nist:`2026-45905`, :cve_nist:`2026-45906`, :cve_nist:`2026-45907`,
   :cve_nist:`2026-45908`, :cve_nist:`2026-45909`, :cve_nist:`2026-45910`, :cve_nist:`2026-45911`,
   :cve_nist:`2026-45912`, :cve_nist:`2026-45913`, :cve_nist:`2026-45914`, :cve_nist:`2026-45915`,
   :cve_nist:`2026-45916`, :cve_nist:`2026-45918`, :cve_nist:`2026-45919`, :cve_nist:`2026-45920`,
   :cve_nist:`2026-45921`, :cve_nist:`2026-45922`, :cve_nist:`2026-45923`, :cve_nist:`2026-45924`,
   :cve_nist:`2026-45925`, :cve_nist:`2026-45926`, :cve_nist:`2026-45927`, :cve_nist:`2026-45928`,
   :cve_nist:`2026-45929`, :cve_nist:`2026-45931`, :cve_nist:`2026-45933`, :cve_nist:`2026-45935`,
   :cve_nist:`2026-45936`, :cve_nist:`2026-45937`, :cve_nist:`2026-45938`, :cve_nist:`2026-45939`,
   :cve_nist:`2026-45941`, :cve_nist:`2026-45942`, :cve_nist:`2026-45945`, :cve_nist:`2026-45946`,
   :cve_nist:`2026-45947`, :cve_nist:`2026-45948`, :cve_nist:`2026-45950`, :cve_nist:`2026-45951`,
   :cve_nist:`2026-45952`, :cve_nist:`2026-45953`, :cve_nist:`2026-45954`, :cve_nist:`2026-45955`,
   :cve_nist:`2026-45956`, :cve_nist:`2026-45957`, :cve_nist:`2026-45958`, :cve_nist:`2026-45959`,
   :cve_nist:`2026-45960`, :cve_nist:`2026-45962`, :cve_nist:`2026-45964`, :cve_nist:`2026-45965`,
   :cve_nist:`2026-45966`, :cve_nist:`2026-45967`, :cve_nist:`2026-45968`, :cve_nist:`2026-45969`,
   :cve_nist:`2026-45970`, :cve_nist:`2026-45971`, :cve_nist:`2026-45972`, :cve_nist:`2026-45973`,
   :cve_nist:`2026-45974`, :cve_nist:`2026-45975`, :cve_nist:`2026-45976`, :cve_nist:`2026-45977`,
   :cve_nist:`2026-45978`, :cve_nist:`2026-45979`, :cve_nist:`2026-45980`, :cve_nist:`2026-45981`,
   :cve_nist:`2026-45982`, :cve_nist:`2026-45983`, :cve_nist:`2026-45984`, :cve_nist:`2026-45985`,
   :cve_nist:`2026-45986`, :cve_nist:`2026-45987`, :cve_nist:`2026-45988`, :cve_nist:`2026-45989`,
   :cve_nist:`2026-45990`, :cve_nist:`2026-45991`, :cve_nist:`2026-45993`, :cve_nist:`2026-45994`,
   :cve_nist:`2026-45995`, :cve_nist:`2026-45996`, :cve_nist:`2026-45997`, :cve_nist:`2026-45998`,
   :cve_nist:`2026-45999`, :cve_nist:`2026-46000`, :cve_nist:`2026-46001`, :cve_nist:`2026-46002`,
   :cve_nist:`2026-46003`, :cve_nist:`2026-46004`, :cve_nist:`2026-46005`, :cve_nist:`2026-46006`,
   :cve_nist:`2026-46007`, :cve_nist:`2026-46008`, :cve_nist:`2026-46009`, :cve_nist:`2026-46010`,
   :cve_nist:`2026-46011`, :cve_nist:`2026-46012`, :cve_nist:`2026-46013`, :cve_nist:`2026-46015`,
   :cve_nist:`2026-46016`, :cve_nist:`2026-46018`, :cve_nist:`2026-46019`, :cve_nist:`2026-46020`,
   :cve_nist:`2026-46021`, :cve_nist:`2026-46022`, :cve_nist:`2026-46023`, :cve_nist:`2026-46024`,
   :cve_nist:`2026-46025`, :cve_nist:`2026-46026`, :cve_nist:`2026-46027`, :cve_nist:`2026-46028`,
   :cve_nist:`2026-46029`, :cve_nist:`2026-46030`, :cve_nist:`2026-46031`, :cve_nist:`2026-46033`,
   :cve_nist:`2026-46034`, :cve_nist:`2026-46035`, :cve_nist:`2026-46036`, :cve_nist:`2026-46037`,
   :cve_nist:`2026-46038`, :cve_nist:`2026-46039`, :cve_nist:`2026-46040`, :cve_nist:`2026-46041`,
   :cve_nist:`2026-46042`, :cve_nist:`2026-46043`, :cve_nist:`2026-46045`, :cve_nist:`2026-46046`,
   :cve_nist:`2026-46047`, :cve_nist:`2026-46048`, :cve_nist:`2026-46049`, :cve_nist:`2026-46050`,
   :cve_nist:`2026-46051`, :cve_nist:`2026-46052`, :cve_nist:`2026-46053`, :cve_nist:`2026-46055`,
   :cve_nist:`2026-46056`, :cve_nist:`2026-46057`, :cve_nist:`2026-46058`, :cve_nist:`2026-46060`,
   :cve_nist:`2026-46061`, :cve_nist:`2026-46062`, :cve_nist:`2026-46063`, :cve_nist:`2026-46064`,
   :cve_nist:`2026-46065`, :cve_nist:`2026-46067`, :cve_nist:`2026-46068`, :cve_nist:`2026-46069`,
   :cve_nist:`2026-46070`, :cve_nist:`2026-46072`, :cve_nist:`2026-46073`, :cve_nist:`2026-46074`,
   :cve_nist:`2026-46075`, :cve_nist:`2026-46077`, :cve_nist:`2026-46078`, :cve_nist:`2026-46079`,
   :cve_nist:`2026-46080`, :cve_nist:`2026-46081`, :cve_nist:`2026-46082`, :cve_nist:`2026-46083`,
   :cve_nist:`2026-46084`, :cve_nist:`2026-46085`, :cve_nist:`2026-46086`, :cve_nist:`2026-46087`,
   :cve_nist:`2026-46088`, :cve_nist:`2026-46089`, :cve_nist:`2026-46091`, :cve_nist:`2026-46092`,
   :cve_nist:`2026-46093`, :cve_nist:`2026-46094`, :cve_nist:`2026-46095`, :cve_nist:`2026-46096`,
   :cve_nist:`2026-46097`, :cve_nist:`2026-46098`, :cve_nist:`2026-46099`, :cve_nist:`2026-46100`,
   :cve_nist:`2026-46101`, :cve_nist:`2026-46102`, :cve_nist:`2026-46103`, :cve_nist:`2026-46104`,
   :cve_nist:`2026-46105`, :cve_nist:`2026-46106`, :cve_nist:`2026-46107`, :cve_nist:`2026-46108`,
   :cve_nist:`2026-46109`, :cve_nist:`2026-46110`, :cve_nist:`2026-46111`, :cve_nist:`2026-46112`,
   :cve_nist:`2026-46113`, :cve_nist:`2026-46114`, :cve_nist:`2026-46115`, :cve_nist:`2026-46116`,
   :cve_nist:`2026-46117`, :cve_nist:`2026-46118`, :cve_nist:`2026-46119`, :cve_nist:`2026-46120`,
   :cve_nist:`2026-46121`, :cve_nist:`2026-46122`, :cve_nist:`2026-46123`, :cve_nist:`2026-46124`,
   :cve_nist:`2026-46125`, :cve_nist:`2026-46126`, :cve_nist:`2026-46127`, :cve_nist:`2026-46128`,
   :cve_nist:`2026-46129`, :cve_nist:`2026-46131`, :cve_nist:`2026-46132`, :cve_nist:`2026-46133`,
   :cve_nist:`2026-46134`, :cve_nist:`2026-46136`, :cve_nist:`2026-46137`, :cve_nist:`2026-46138`,
   :cve_nist:`2026-46139`, :cve_nist:`2026-46141`, :cve_nist:`2026-46142`, :cve_nist:`2026-46143`,
   :cve_nist:`2026-46144`, :cve_nist:`2026-46145`, :cve_nist:`2026-46146`, :cve_nist:`2026-46149`,
   :cve_nist:`2026-46150`, :cve_nist:`2026-46151`, :cve_nist:`2026-46152`, :cve_nist:`2026-46154`,
   :cve_nist:`2026-46155`, :cve_nist:`2026-46156`, :cve_nist:`2026-46158`, :cve_nist:`2026-46159`,
   :cve_nist:`2026-46160`, :cve_nist:`2026-46161`, :cve_nist:`2026-46162`, :cve_nist:`2026-46163`,
   :cve_nist:`2026-46164`, :cve_nist:`2026-46165`, :cve_nist:`2026-46166`, :cve_nist:`2026-46167`,
   :cve_nist:`2026-46168`, :cve_nist:`2026-46169`, :cve_nist:`2026-46170`, :cve_nist:`2026-46172`,
   :cve_nist:`2026-46173`, :cve_nist:`2026-46174`, :cve_nist:`2026-46176`, :cve_nist:`2026-46177`,
   :cve_nist:`2026-46178`, :cve_nist:`2026-46179`, :cve_nist:`2026-46180`, :cve_nist:`2026-46182`,
   :cve_nist:`2026-46183`, :cve_nist:`2026-46184`, :cve_nist:`2026-46185`, :cve_nist:`2026-46186`,
   :cve_nist:`2026-46187`, :cve_nist:`2026-46188`, :cve_nist:`2026-46189`, :cve_nist:`2026-46190`,
   :cve_nist:`2026-46191`, :cve_nist:`2026-46192`, :cve_nist:`2026-46193`, :cve_nist:`2026-46194`,
   :cve_nist:`2026-46195`, :cve_nist:`2026-46196`, :cve_nist:`2026-46197`, :cve_nist:`2026-46198`,
   :cve_nist:`2026-46199`, :cve_nist:`2026-46201`, :cve_nist:`2026-46202`, :cve_nist:`2026-46203`,
   :cve_nist:`2026-46204`, :cve_nist:`2026-46205`, :cve_nist:`2026-46206`, :cve_nist:`2026-46207`,
   :cve_nist:`2026-46208`, :cve_nist:`2026-46209`, :cve_nist:`2026-46210`, :cve_nist:`2026-46211`,
   :cve_nist:`2026-46212`, :cve_nist:`2026-46213`, :cve_nist:`2026-46214`, :cve_nist:`2026-46215`,
   :cve_nist:`2026-46216`, :cve_nist:`2026-46218`, :cve_nist:`2026-46219`, :cve_nist:`2026-46220`,
   :cve_nist:`2026-46221`, :cve_nist:`2026-46222`, :cve_nist:`2026-46223`, :cve_nist:`2026-46224`,
   :cve_nist:`2026-46225`, :cve_nist:`2026-46226`, :cve_nist:`2026-46227`, :cve_nist:`2026-46228`,
   :cve_nist:`2026-46229`, :cve_nist:`2026-46230`, :cve_nist:`2026-46231`, :cve_nist:`2026-46232`,
   :cve_nist:`2026-46233`, :cve_nist:`2026-46234`, :cve_nist:`2026-46235`, :cve_nist:`2026-46236`,
   :cve_nist:`2026-46238`, :cve_nist:`2026-46239`, :cve_nist:`2026-46240`, :cve_nist:`2026-46243`,
   :cve_nist:`2026-46244`, :cve_nist:`2026-46246`, :cve_nist:`2026-46247`, :cve_nist:`2026-46248`,
   :cve_nist:`2026-46249`, :cve_nist:`2026-46250`, :cve_nist:`2026-46251`, :cve_nist:`2026-46253`,
   :cve_nist:`2026-46255`, :cve_nist:`2026-46256`, :cve_nist:`2026-46257`, :cve_nist:`2026-46258`,
   :cve_nist:`2026-46259`, :cve_nist:`2026-46260`, :cve_nist:`2026-46261`, :cve_nist:`2026-46262`,
   :cve_nist:`2026-46263`, :cve_nist:`2026-46264`, :cve_nist:`2026-46265`, :cve_nist:`2026-46266`,
   :cve_nist:`2026-46267`, :cve_nist:`2026-46268`, :cve_nist:`2026-46269`, :cve_nist:`2026-46270`,
   :cve_nist:`2026-46271`, :cve_nist:`2026-46273`, :cve_nist:`2026-46274`, :cve_nist:`2026-46275`,
   :cve_nist:`2026-46276`, :cve_nist:`2026-46277`, :cve_nist:`2026-46278`, :cve_nist:`2026-46279`,
   :cve_nist:`2026-46280`, :cve_nist:`2026-46281`, :cve_nist:`2026-46283`, :cve_nist:`2026-46284`,
   :cve_nist:`2026-46285`, :cve_nist:`2026-46286`, :cve_nist:`2026-46287`, :cve_nist:`2026-46288`,
   :cve_nist:`2026-46289`, :cve_nist:`2026-46290`, :cve_nist:`2026-46291`, :cve_nist:`2026-46292`,
   :cve_nist:`2026-46293`, :cve_nist:`2026-46294`, :cve_nist:`2026-46295`, :cve_nist:`2026-46296`,
   :cve_nist:`2026-46297`, :cve_nist:`2026-46298` and :cve_nist:`2026-46299`
-  linux-yocto/6.6: (cont.) Ignore :cve_nist:`2026-46300`, :cve_nist:`2026-46301`, :cve_nist:`2026-46303`,
   :cve_nist:`2026-46304`, :cve_nist:`2026-46305`, :cve_nist:`2026-46306`, :cve_nist:`2026-46307`,
   :cve_nist:`2026-46308`, :cve_nist:`2026-46309`, :cve_nist:`2026-46310`, :cve_nist:`2026-46311`,
   :cve_nist:`2026-46312`, :cve_nist:`2026-46313`, :cve_nist:`2026-46315`, :cve_nist:`2026-46316`,
   :cve_nist:`2026-46317`, :cve_nist:`2026-46318`, :cve_nist:`2026-46319`, :cve_nist:`2026-46323`,
   :cve_nist:`2026-46326`, :cve_nist:`2026-46327`, :cve_nist:`2026-46328`, :cve_nist:`2026-46329`,
   :cve_nist:`2026-46332`, :cve_nist:`2026-46333`, :cve_nist:`2026-52904`, :cve_nist:`2026-52905`,
   :cve_nist:`2026-52906`, :cve_nist:`2026-52907`, :cve_nist:`2026-52911`, :cve_nist:`2026-52912`,
   :cve_nist:`2026-52914`, :cve_nist:`2026-52915`, :cve_nist:`2026-52916`, :cve_nist:`2026-52918`,
   :cve_nist:`2026-52919`, :cve_nist:`2026-52920`, :cve_nist:`2026-52921`, :cve_nist:`2026-52922`,
   :cve_nist:`2026-52925`, :cve_nist:`2026-52926`, :cve_nist:`2026-52931`, :cve_nist:`2026-52932`,
   :cve_nist:`2026-52933`, :cve_nist:`2026-52936`, :cve_nist:`2026-52938`, :cve_nist:`2026-52940`,
   :cve_nist:`2026-52941`, :cve_nist:`2026-52945`, :cve_nist:`2026-52949`, :cve_nist:`2026-52950`,
   :cve_nist:`2026-52951`, :cve_nist:`2026-52952`, :cve_nist:`2026-52954`, :cve_nist:`2026-52955`,
   :cve_nist:`2026-52957`, :cve_nist:`2026-52958`, :cve_nist:`2026-52959`, :cve_nist:`2026-52960`,
   :cve_nist:`2026-52962`, :cve_nist:`2026-52963`, :cve_nist:`2026-52964`, :cve_nist:`2026-52965`,
   :cve_nist:`2026-52966`, :cve_nist:`2026-52967`, :cve_nist:`2026-52968`, :cve_nist:`2026-52969`,
   :cve_nist:`2026-52970`, :cve_nist:`2026-52971`, :cve_nist:`2026-52972`, :cve_nist:`2026-52973`,
   :cve_nist:`2026-52974`, :cve_nist:`2026-52975`, :cve_nist:`2026-52976`, :cve_nist:`2026-52977`,
   :cve_nist:`2026-52978`, :cve_nist:`2026-52979`, :cve_nist:`2026-52980`, :cve_nist:`2026-52981`,
   :cve_nist:`2026-52982`, :cve_nist:`2026-52983`, :cve_nist:`2026-52984`, :cve_nist:`2026-52985`,
   :cve_nist:`2026-52986`, :cve_nist:`2026-52987`, :cve_nist:`2026-52989`, :cve_nist:`2026-52992`,
   :cve_nist:`2026-52993`, :cve_nist:`2026-52994`, :cve_nist:`2026-52995`, :cve_nist:`2026-52996`,
   :cve_nist:`2026-52997`, :cve_nist:`2026-52998`, :cve_nist:`2026-52999`, :cve_nist:`2026-53001`,
   :cve_nist:`2026-53002`, :cve_nist:`2026-53003`, :cve_nist:`2026-53004`, :cve_nist:`2026-53006`,
   :cve_nist:`2026-53007`, :cve_nist:`2026-53008`, :cve_nist:`2026-53011`, :cve_nist:`2026-53012`,
   :cve_nist:`2026-53013`, :cve_nist:`2026-53014`, :cve_nist:`2026-53016`, :cve_nist:`2026-53019`,
   :cve_nist:`2026-53020`, :cve_nist:`2026-53021`, :cve_nist:`2026-53022`, :cve_nist:`2026-53023`,
   :cve_nist:`2026-53026`, :cve_nist:`2026-53028`, :cve_nist:`2026-53029`, :cve_nist:`2026-53030`,
   :cve_nist:`2026-53031`, :cve_nist:`2026-53032`, :cve_nist:`2026-53033`, :cve_nist:`2026-53034`,
   :cve_nist:`2026-53035`, :cve_nist:`2026-53036`, :cve_nist:`2026-53037`, :cve_nist:`2026-53038`,
   :cve_nist:`2026-53039`, :cve_nist:`2026-53040`, :cve_nist:`2026-53041`, :cve_nist:`2026-53042`,
   :cve_nist:`2026-53043`, :cve_nist:`2026-53044`, :cve_nist:`2026-53045`, :cve_nist:`2026-53046`,
   :cve_nist:`2026-53047`, :cve_nist:`2026-53048`, :cve_nist:`2026-53049`, :cve_nist:`2026-53050`,
   :cve_nist:`2026-53051`, :cve_nist:`2026-53052`, :cve_nist:`2026-53054`, :cve_nist:`2026-53055`,
   :cve_nist:`2026-53056`, :cve_nist:`2026-53057`, :cve_nist:`2026-53058`, :cve_nist:`2026-53059`,
   :cve_nist:`2026-53060`, :cve_nist:`2026-53061`, :cve_nist:`2026-53062`, :cve_nist:`2026-53063`,
   :cve_nist:`2026-53064`, :cve_nist:`2026-53065`, :cve_nist:`2026-53066`, :cve_nist:`2026-53067`,
   :cve_nist:`2026-53068`, :cve_nist:`2026-53069`, :cve_nist:`2026-53071`, :cve_nist:`2026-53072`,
   :cve_nist:`2026-53073`, :cve_nist:`2026-53074`, :cve_nist:`2026-53075`, :cve_nist:`2026-53076`,
   :cve_nist:`2026-53077`, :cve_nist:`2026-53079`, :cve_nist:`2026-53081`, :cve_nist:`2026-53082`,
   :cve_nist:`2026-53083`, :cve_nist:`2026-53084`, :cve_nist:`2026-53085`, :cve_nist:`2026-53086`,
   :cve_nist:`2026-53087`, :cve_nist:`2026-53088`, :cve_nist:`2026-53092`, :cve_nist:`2026-53093`,
   :cve_nist:`2026-53094`, :cve_nist:`2026-53095`, :cve_nist:`2026-53096`, :cve_nist:`2026-53098`,
   :cve_nist:`2026-53099`, :cve_nist:`2026-53100`, :cve_nist:`2026-53101`, :cve_nist:`2026-53103`,
   :cve_nist:`2026-53104`, :cve_nist:`2026-53105`, :cve_nist:`2026-53107`, :cve_nist:`2026-53110`,
   :cve_nist:`2026-53111`, :cve_nist:`2026-53112`, :cve_nist:`2026-53114`, :cve_nist:`2026-53116`,
   :cve_nist:`2026-53117`, :cve_nist:`2026-53119`, :cve_nist:`2026-53121`, :cve_nist:`2026-53123`,
   :cve_nist:`2026-53124`, :cve_nist:`2026-53125`, :cve_nist:`2026-53126`, :cve_nist:`2026-53127`,
   :cve_nist:`2026-53128`, :cve_nist:`2026-53130`, :cve_nist:`2026-53140`, :cve_nist:`2026-53141`,
   :cve_nist:`2026-53142`, :cve_nist:`2026-53145`, :cve_nist:`2026-53152`, :cve_nist:`2026-53153`,
   :cve_nist:`2026-53155`, :cve_nist:`2026-53162`, :cve_nist:`2026-53164`, :cve_nist:`2026-53165`,
   :cve_nist:`2026-53169`, :cve_nist:`2026-53170`, :cve_nist:`2026-53171`, :cve_nist:`2026-53172`,
   :cve_nist:`2026-53173`, :cve_nist:`2026-53174`, :cve_nist:`2026-53175`, :cve_nist:`2026-53180`,
   :cve_nist:`2026-53187`, :cve_nist:`2026-53188`, :cve_nist:`2026-53191`, :cve_nist:`2026-53192`,
   :cve_nist:`2026-53193`, :cve_nist:`2026-53197`, :cve_nist:`2026-53200`, :cve_nist:`2026-53201`,
   :cve_nist:`2026-53202`, :cve_nist:`2026-53203`, :cve_nist:`2026-53204`, :cve_nist:`2026-53205`,
   :cve_nist:`2026-53206`, :cve_nist:`2026-53210`, :cve_nist:`2026-53211`, :cve_nist:`2026-53222`,
   :cve_nist:`2026-53231`, :cve_nist:`2026-53233`, :cve_nist:`2026-53234`, :cve_nist:`2026-53235`,
   :cve_nist:`2026-53240`, :cve_nist:`2026-53241`, :cve_nist:`2026-53243`, :cve_nist:`2026-53244`,
   :cve_nist:`2026-53248`, :cve_nist:`2026-53250`, :cve_nist:`2026-53251`, :cve_nist:`2026-53257`,
   :cve_nist:`2026-53259`, :cve_nist:`2026-53260`, :cve_nist:`2026-53261`, :cve_nist:`2026-53276`,
   :cve_nist:`2026-53277`, :cve_nist:`2026-53278`, :cve_nist:`2026-53279`, :cve_nist:`2026-53280`,
   :cve_nist:`2026-53281`, :cve_nist:`2026-53282`, :cve_nist:`2026-53283`, :cve_nist:`2026-53286`,
   :cve_nist:`2026-53287`, :cve_nist:`2026-53288`, :cve_nist:`2026-53289`, :cve_nist:`2026-53290`,
   :cve_nist:`2026-53291`, :cve_nist:`2026-53293`, :cve_nist:`2026-53294`, :cve_nist:`2026-53295`,
   :cve_nist:`2026-53296`, :cve_nist:`2026-53298`, :cve_nist:`2026-53299`, :cve_nist:`2026-53300`,
   :cve_nist:`2026-53301`, :cve_nist:`2026-53302`, :cve_nist:`2026-53303`, :cve_nist:`2026-53304`,
   :cve_nist:`2026-53305`, :cve_nist:`2026-53306`, :cve_nist:`2026-53307`, :cve_nist:`2026-53308`,
   :cve_nist:`2026-53309`, :cve_nist:`2026-53310`, :cve_nist:`2026-53311`, :cve_nist:`2026-53312`,
   :cve_nist:`2026-53314`, :cve_nist:`2026-53315`, :cve_nist:`2026-53316`, :cve_nist:`2026-53318`,
   :cve_nist:`2026-53319`, :cve_nist:`2026-53320`, :cve_nist:`2026-53321`, :cve_nist:`2026-53322`,
   :cve_nist:`2026-53323`, :cve_nist:`2026-53324`, :cve_nist:`2026-53326`, :cve_nist:`2026-53328`,
   :cve_nist:`2026-53333`, :cve_nist:`2026-53334`, :cve_nist:`2026-53335`, :cve_nist:`2026-53338`,
   :cve_nist:`2026-53340`, :cve_nist:`2026-53341`, :cve_nist:`2026-53342`, :cve_nist:`2026-53344`,
   :cve_nist:`2026-53346`, :cve_nist:`2026-53348`, :cve_nist:`2026-53351`, :cve_nist:`2026-53357`,
   :cve_nist:`2026-53360`, :cve_nist:`2026-53363`, :cve_nist:`2026-53364`, :cve_nist:`2026-53365`,
   :cve_nist:`2026-53367`, :cve_nist:`2026-53369`, :cve_nist:`2026-53370`, :cve_nist:`2026-53371`,
   :cve_nist:`2026-53372`, :cve_nist:`2026-53373`, :cve_nist:`2026-53374`, :cve_nist:`2026-53375`,
   :cve_nist:`2026-53376`, :cve_nist:`2026-53378`, :cve_nist:`2026-53379`, :cve_nist:`2026-53380`,
   :cve_nist:`2026-53386`, :cve_nist:`2026-53387`, :cve_nist:`2026-53389`, :cve_nist:`2026-53394`,
   :cve_nist:`2026-53395`, :cve_nist:`2026-53396`, :cve_nist:`2026-63793`, :cve_nist:`2026-63799`,
   :cve_nist:`2026-63813`, :cve_nist:`2026-63820`, :cve_nist:`2026-63832`, :cve_nist:`2026-63837`,
   :cve_nist:`2026-63838`, :cve_nist:`2026-63839`, :cve_nist:`2026-63840`, :cve_nist:`2026-63841`,
   :cve_nist:`2026-63842`, :cve_nist:`2026-63843`, :cve_nist:`2026-63844`, :cve_nist:`2026-63845`,
   :cve_nist:`2026-63846`, :cve_nist:`2026-63847`, :cve_nist:`2026-63848`, :cve_nist:`2026-63849`,
   :cve_nist:`2026-63850`, :cve_nist:`2026-63851`, :cve_nist:`2026-63852`, :cve_nist:`2026-63854`,
   :cve_nist:`2026-63855`, :cve_nist:`2026-63856`, :cve_nist:`2026-63857`, :cve_nist:`2026-63859`,
   :cve_nist:`2026-63860`, :cve_nist:`2026-63861`, :cve_nist:`2026-63862`, :cve_nist:`2026-63863`,
   :cve_nist:`2026-63864`, :cve_nist:`2026-63865`, :cve_nist:`2026-63866`, :cve_nist:`2026-63869`,
   :cve_nist:`2026-63873`, :cve_nist:`2026-63874`, :cve_nist:`2026-63878`, :cve_nist:`2026-63880`,
   :cve_nist:`2026-63885`, :cve_nist:`2026-63894`, :cve_nist:`2026-63907`, :cve_nist:`2026-63910`,
   :cve_nist:`2026-63911`, :cve_nist:`2026-63918`, :cve_nist:`2026-63923`, :cve_nist:`2026-63929`,
   :cve_nist:`2026-63932`, :cve_nist:`2026-63935`, :cve_nist:`2026-63936`, :cve_nist:`2026-63937`,
   :cve_nist:`2026-63938`, :cve_nist:`2026-63939`, :cve_nist:`2026-63941`, :cve_nist:`2026-63950`,
   :cve_nist:`2026-63951`, :cve_nist:`2026-63953`, :cve_nist:`2026-63955`, :cve_nist:`2026-63965`,
   :cve_nist:`2026-63966`, :cve_nist:`2026-63970`, :cve_nist:`2026-63972`, :cve_nist:`2026-63977`,
   :cve_nist:`2026-63980`, :cve_nist:`2026-63981`, :cve_nist:`2026-63982`, :cve_nist:`2026-63986`,
   :cve_nist:`2026-63987`, :cve_nist:`2026-63988`, :cve_nist:`2026-63989`, :cve_nist:`2026-63995`,
   :cve_nist:`2026-63996`, :cve_nist:`2026-63997`, :cve_nist:`2026-63998`, :cve_nist:`2026-64008`,
   :cve_nist:`2026-64013`, :cve_nist:`2026-64015`, :cve_nist:`2026-64016`, :cve_nist:`2026-64018`,
   :cve_nist:`2026-64019`, :cve_nist:`2026-64020`, :cve_nist:`2026-64021`, :cve_nist:`2026-64022`,
   :cve_nist:`2026-64023`, :cve_nist:`2026-64024`, :cve_nist:`2026-64025`, :cve_nist:`2026-64027`,
   :cve_nist:`2026-64028`, :cve_nist:`2026-64029`, :cve_nist:`2026-64030`, :cve_nist:`2026-64031`,
   :cve_nist:`2026-64032`, :cve_nist:`2026-64033`, :cve_nist:`2026-64034`, :cve_nist:`2026-64035`,
   :cve_nist:`2026-64037`, :cve_nist:`2026-64039`, :cve_nist:`2026-64040`, :cve_nist:`2026-64041`,
   :cve_nist:`2026-64042`, :cve_nist:`2026-64043`, :cve_nist:`2026-64044`, :cve_nist:`2026-64045`,
   :cve_nist:`2026-64046`, :cve_nist:`2026-64047`, :cve_nist:`2026-64048`, :cve_nist:`2026-64049`,
   :cve_nist:`2026-64050`, :cve_nist:`2026-64051`, :cve_nist:`2026-64052`, :cve_nist:`2026-64053`,
   :cve_nist:`2026-64054`, :cve_nist:`2026-64055`, :cve_nist:`2026-64056`, :cve_nist:`2026-64057`,
   :cve_nist:`2026-64058`, :cve_nist:`2026-64059`, :cve_nist:`2026-64061`, :cve_nist:`2026-64062`,
   :cve_nist:`2026-64063`, :cve_nist:`2026-64064`, :cve_nist:`2026-64065`, :cve_nist:`2026-64066`,
   :cve_nist:`2026-64067`, :cve_nist:`2026-64068`, :cve_nist:`2026-64069`, :cve_nist:`2026-64071`,
   :cve_nist:`2026-64072`, :cve_nist:`2026-64073`, :cve_nist:`2026-64074`, :cve_nist:`2026-64075`,
   :cve_nist:`2026-64080`, :cve_nist:`2026-64081`, :cve_nist:`2026-64083`, :cve_nist:`2026-64084`,
   :cve_nist:`2026-64085`, :cve_nist:`2026-64086`, :cve_nist:`2026-64087`, :cve_nist:`2026-64088`,
   :cve_nist:`2026-64089`, :cve_nist:`2026-64092`, :cve_nist:`2026-64096`, :cve_nist:`2026-64097`,
   :cve_nist:`2026-64098`, :cve_nist:`2026-64099`, :cve_nist:`2026-64100`, :cve_nist:`2026-64101`,
   :cve_nist:`2026-64102`, :cve_nist:`2026-64103`, :cve_nist:`2026-64104`, :cve_nist:`2026-64105`,
   :cve_nist:`2026-64106`, :cve_nist:`2026-64107`, :cve_nist:`2026-64108`, :cve_nist:`2026-64110`,
   :cve_nist:`2026-64111`, :cve_nist:`2026-64113`, :cve_nist:`2026-64114`, :cve_nist:`2026-64115`,
   :cve_nist:`2026-64118`, :cve_nist:`2026-64119`, :cve_nist:`2026-64120`, :cve_nist:`2026-64121`,
   :cve_nist:`2026-64122`, :cve_nist:`2026-64124`, :cve_nist:`2026-64125`, :cve_nist:`2026-64126`,
   :cve_nist:`2026-64127`, :cve_nist:`2026-64128`, :cve_nist:`2026-64129`, :cve_nist:`2026-64130`,
   :cve_nist:`2026-64132`, :cve_nist:`2026-64133`, :cve_nist:`2026-64134`, :cve_nist:`2026-64135`,
   :cve_nist:`2026-64136`, :cve_nist:`2026-64139`, :cve_nist:`2026-64140`, :cve_nist:`2026-64141`,
   :cve_nist:`2026-64142`, :cve_nist:`2026-64143`, :cve_nist:`2026-64144`, :cve_nist:`2026-64145`,
   :cve_nist:`2026-64147`, :cve_nist:`2026-64148`, :cve_nist:`2026-64149`, :cve_nist:`2026-64150`,
   :cve_nist:`2026-64151`, :cve_nist:`2026-64152`, :cve_nist:`2026-64153`, :cve_nist:`2026-64155`,
   :cve_nist:`2026-64156`, :cve_nist:`2026-64157`, :cve_nist:`2026-64158`, :cve_nist:`2026-64159`,
   :cve_nist:`2026-64161`, :cve_nist:`2026-64162`, :cve_nist:`2026-64163`, :cve_nist:`2026-64164`,
   :cve_nist:`2026-64165`, :cve_nist:`2026-64166`, :cve_nist:`2026-64167`, :cve_nist:`2026-64168`,
   :cve_nist:`2026-64169`, :cve_nist:`2026-64170`, :cve_nist:`2026-64171`, :cve_nist:`2026-64172`,
   :cve_nist:`2026-64173`, :cve_nist:`2026-64174`, :cve_nist:`2026-64175`, :cve_nist:`2026-64176`,
   :cve_nist:`2026-64177`, :cve_nist:`2026-64178`, :cve_nist:`2026-64179`, :cve_nist:`2026-64180`,
   :cve_nist:`2026-64181`, :cve_nist:`2026-64182`, :cve_nist:`2026-64183`, :cve_nist:`2026-64184`,
   :cve_nist:`2026-64185`, :cve_nist:`2026-64186` and :cve_nist:`2026-64207`
-  openssh: Fix :cve_nist:`2026-59995`, :cve_nist:`2026-59996`, :cve_nist:`2026-59997`,
   :cve_nist:`2026-59999`, :cve_nist:`2026-60000`, :cve_nist:`2026-60001` and :cve_nist:`2026-60002`
-  openssh: Ignore :cve_nist:`2026-59998` if kerberos not enabled
-  openssh: Ignore :cve_nist:`2026-3497`
-  perl: Fix :cve_nist:`2026-8376`
-  python3: Fix :cve_nist:`2026-7210`, :cve_nist:`2026-9669`, :cve_nist:`2026-11940` and
   :cve_nist:`2026-11972`
-  python3-setuptools: Fix :cve_nist:`2026-59890`
-  python3-urllib3: Fix :cve_nist:`2026-44431`
-  qemu: Fix :cve_nist:`2025-14876`, :cve_nist:`2026-0665` and :cve_nist:`2026-2243`
-  socat: Fix :cve_nist:`2026-56123`
-  sqlite3: Fix :cve_nist:`2026-11822` and :cve_nist:`2026-11824`
-  tar: Fix :cve_nist:`2026-5704`
-  util-linux: Fix :cve_nist:`2026-13595`
-  vim: Fix :cve_nist:`2026-28417`, :cve_nist:`2026-28420`, :cve_nist:`2026-28421`,
   :cve_nist:`2026-28422`, :cve_nist:`2026-32249`, :cve_nist:`2026-34714`, :cve_nist:`2026-34982`,
   :cve_nist:`2026-35177`, :cve_nist:`2026-41411`, :cve_nist:`2026-42307`, :cve_nist:`2026-43961`,
   :cve_nist:`2026-44656`, :cve_nist:`2026-45130`, :cve_nist:`2026-46483`, :cve_nist:`2026-47162`,
   :cve_nist:`2026-47167`, :cve_nist:`2026-52858`, :cve_nist:`2026-52859` and :cve_nist:`2026-52860`

Fixes in Yocto-5.0.20
~~~~~~~~~~~~~~~~~~~~~

-  bind: Upgrade to 9.18.49
-  binutils: CVE-2025-69648.patch also fixed :cve_nist:`2025-69646`
-  bitbake.rst: rename section to "BitBake User Manual"
-  bitbake: README: Add "2.8" subject-prefix to git-send-email suggestion
-  bitbake: utils: Add NFS EEXISTS/isdir failure workaround
-  build-appliance-image: Update to scarthgap head revision
-  bzip2: fix 'bzip2 --version > /tmp/aaa 2>&1' hang
-  ca-certificates: upgrade to 20260601
-  cargo-update-recipe-crates: Don't fail for partially empty Cargo.lock
-  contributor-guide/identify-component.rst: use the same tense as other section
-  contributor-guide: Note patch complexity requirements for stable branches
-  create-spdx-image-3.0: correct :term:`SSTATE_SKIP_CREATION` key for do_create_image_sbom_spdx
-  cve-update: Avoid NFS caching issues
-  dev-manual/sbom.rst: refresh for SPDX3
-  docs: fix broken path links
-  docs: fix manual names in cross references
-  docs: fix various broken links
-  docs: index.rst: add introduction paragraphs
-  docs: index.rst: move external links to the bottom of the intro section
-  docs: index.rst: move release notes in their own section
-  docs: index.rst: move the "Overview and Concepts" manual in the "Introduction and Overview" section
-  docs: index.rst: move the contributor guide out of the Manuals section
-  docs: index.rst: show the intro paragraph only for html-based docs
-  docs: kernel-dev/common.rst: remove taskhash mismatch note
-  docs: README: update instructions for installing vale and sphinx-lint
-  docs: recipe-style-guide: Clarify when License-Update tag is needed
-  docs: remove CROPS references
-  flex: update :term:`CVE_PRODUCT`
-  glibc-testsuite: Do not generate :term:`SPDX`
-  glibc: stable 2.39 branch updates
-  libgcrypt: upgrade to 1.10.4
-  linux-yocto/6.6: update to v6.6.147
-  migration-guide: add release notes for 5.0.19
-  migration-guides/release-notes-3.4.2.rst: fix a broken link
-  migration-guides/release-notes-5.0.rst: remove broken link
-  migration-guides: replace broken link with archive links
-  openssh: CVE-2026-35387.patch also fixed :cve_nist:`2026-35414`
-  overview-manual: move the intro content in the index
-  package.bbclass: hardcode emit_pkgdata to run last
-  perf: drop newt from tui build requirements
-  poky.conf: Bump version for 5.0.20 release
-  python3-certifi: set :term:`CVE_PRODUCT`
-  python3-cryptography: set :term:`CVE_PRODUCT`
-  python3-idna: set :term:`CVE_PRODUCT`
-  python3-pip: set :term:`CVE_PRODUCT`
-  python3-ply: set :term:`CVE_PRODUCT`
-  python3-pyasn1: set :term:`CVE_PRODUCT`
-  python3-pyopenssl: set :term:`CVE_PRODUCT`
-  python3-pyyaml: set :term:`CVE_PRODUCT`
-  python3-xmltodict: set :term:`CVE_PRODUCT`
-  python3: Simplify ptest exclusion list
-  python3: skiptest tracemalloc_track_race
-  ref-manual/classes.rst: replace obsolete mailing list thread
-  ref-manual/features.rst: fix some cross references
-  ref-manual/images.rst: update obsolete VMWare links
-  ref-manual/release-process.rst: update LTS supported versions
-  ref-manual: Fix occurrences of omitted space with :prepend
-  ref-manual: remove all traces of "kernel_menuconfig" task
-  rootfs: move tasks using image_list_installed_packages to postuninstall
-  scripts/install-buildtools: Update to 5.0.19
-  shadow: set :term:`CVE_PRODUCT`
-  sudo: fix pam-wheel sed for sudo 1.9.17p2 sudoers
-  sudo: set :term:`CVE_PRODUCT`
-  tzdata/tzcode-native: upgrade 2026b -> 2026c
-  u-boot: Set :term:`CVE_PRODUCT`
-  vex: remove obsolete semicolon
-  wic: Fix updating fstab for nvme devices
-  wireless-regdb: upgrade to 2026.05.30
-  xmlto: update :term:`SRC_URI`
-  xserver-org: update :term:`CVE_PRODUCT`

Known Issues in Yocto-5.0.20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adarsh Jagadish Kamini
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Amaury Couderc
-  Anil Dongare
-  Ankur Tyagi
-  Antonin Godard
-  AshishKumar Mishra
-  Ashishkumar Parmar
-  Benjamin Robin (Schneider Electric)
-  Bruce Ashfield
-  Daiane Angolini
-  Daniel Turull
-  Darsh Kelaiya
-  David Nyström
-  Deepak Rathore
-  Devansh Patel
-  Enoch Ng
-  Eric Meyers
-  Esa Jaaskela
-  Harish Sadineni
-  Himanshu Jadon
-  Hitendra Prajapati
-  Hongxu Jia
-  Hugo SIMELIERE (Schneider Electric)
-  Jaipaul Cheernam
-  Jakub Szczudlo
-  Joshua Watt
-  João Marcos Costa
-  Kris Gavvala
-  Lee Chee Yang
-  Maik Otto
-  Marta Rybczynska
-  Martin Schwan
-  Mathieu Dubois-Briand
-  Nate Kent
-  Niko Mauno
-  Paul Barker
-  Peter Marko
-  Quentin Schulz
-  Richard Purdie
-  Robert P. J. Day
-  Roland Kovacs
-  Ross Burton
-  Shubham Pushpkar
-  Siddharth Doshi
-  Sudhir Dumbhare
-  Theo Gaige
-  Theo Gaige (Schneider Electric)
-  Vijay Anusuri
-  Yoann Congal
-  mark.yang

Repositories / Downloads for Yocto-5.0.20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.20 </yocto-docs/log/?h=yocto-5.0.20>`
-  Git Revision: :yocto_git:`fe46d5de41930a2762a9ad2543d50e0f4e10463b </yocto-docs/commit/?id=fe46d5de41930a2762a9ad2543d50e0f4e10463b>`
-  Release Artefact: yocto-docs-fe46d5de41930a2762a9ad2543d50e0f4e10463b
-  sha: aae57cc7fce4c3d06a24206c939b614d55fe7dc41e0525e88282ab7ae9c429d5
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.20/yocto-docs-fe46d5de41930a2762a9ad2543d50e0f4e10463b.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.20/yocto-docs-fe46d5de41930a2762a9ad2543d50e0f4e10463b.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.20 </poky/log/?h=yocto-5.0.20>`
-  Git Revision: :yocto_git:`64e69ed23703f6358ec431d3f5f1f8483f974cae </poky/commit/?id=64e69ed23703f6358ec431d3f5f1f8483f974cae>`
-  Release Artefact: poky-64e69ed23703f6358ec431d3f5f1f8483f974cae
-  sha: 533b0bbe168d5e8cfd0b58b8bff75d6110a85d3dae8e6828871868254257f762
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.20/poky-64e69ed23703f6358ec431d3f5f1f8483f974cae.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.20/poky-64e69ed23703f6358ec431d3f5f1f8483f974cae.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.20 </openembedded-core/log/?h=yocto-5.0.20>`
-  Git Revision: :oe_git:`70dc15941dd33270a92d1001174efb3093e79bdf </openembedded-core/commit/?id=70dc15941dd33270a92d1001174efb3093e79bdf>`
-  Release Artefact: oecore-70dc15941dd33270a92d1001174efb3093e79bdf
-  sha: 3025900891f488349cc2e5327b0c57287a28eca398970c691000075b54c6556f
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.20/oecore-70dc15941dd33270a92d1001174efb3093e79bdf.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.20/oecore-70dc15941dd33270a92d1001174efb3093e79bdf.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`scarthgap </meta-yocto/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.20 </meta-yocto/log/?h=yocto-5.0.20>`
-  Git Revision: :yocto_git:`961c2399d06fbdb04ee27d06079e186d2c4a14ad </meta-yocto/commit/?id=961c2399d06fbdb04ee27d06079e186d2c4a14ad>`
-  Release Artefact: meta-yocto-961c2399d06fbdb04ee27d06079e186d2c4a14ad
-  sha: a51ccee6d11696703faf8f154a44a5ceb71c3be08b6565158ad8cae0a71c9939
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.20/meta-yocto-961c2399d06fbdb04ee27d06079e186d2c4a14ad.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.20/meta-yocto-961c2399d06fbdb04ee27d06079e186d2c4a14ad.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.20 </meta-mingw/log/?h=yocto-5.0.20>`
-  Git Revision: :yocto_git:`a2065f321a3dbca1e69f070502917c76653d9105 </meta-mingw/commit/?id=a2065f321a3dbca1e69f070502917c76653d9105>`
-  Release Artefact: meta-mingw-a2065f321a3dbca1e69f070502917c76653d9105
-  sha: 02042144a265ca5a25a8345688bb626636639a96f33d2b7b8c581edcf2d8df4e
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.20/meta-mingw-a2065f321a3dbca1e69f070502917c76653d9105.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.20/meta-mingw-a2065f321a3dbca1e69f070502917c76653d9105.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.20 </bitbake/log/?h=yocto-5.0.20>`
-  Git Revision: :oe_git:`cff3be6f664f8f07a40727ff63eeeb24d6f5e00b </bitbake/commit/?id=cff3be6f664f8f07a40727ff63eeeb24d6f5e00b>`
-  Release Artefact: bitbake-cff3be6f664f8f07a40727ff63eeeb24d6f5e00b
-  sha: 42622a37bc52f7fe9921e943026d73316cc2e8d62535e41bc4d308318c4a706e
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.20/bitbake-cff3be6f664f8f07a40727ff63eeeb24d6f5e00b.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.20/bitbake-cff3be6f664f8f07a40727ff63eeeb24d6f5e00b.tar.bz2

