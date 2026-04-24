Release notes for Yocto-5.0.17 (Scarthgap)
------------------------------------------

Openssl 3.2 has reached EOL. Some projects would like to use LTS version due to criticality and exposure of this component, so upgrade to 3.5 branch.

Security Fixes in Yocto-5.0.17
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  alsa-lib: Fix :cve_nist:`2026-25068`
-  avahi: Fix :cve_nist:`2025-68276`, :cve_nist:`2025-68468`, :cve_nist:`2025-68471` and
   :cve_nist:`2026-24401`
-  bind: Fix :cve_nist:`2025-13878`
-  busybox: Fix :cve_nist:`2025-60876`
-  ffmpeg: ignore :cve_nist:`2025-1594`, :cve_nist:`2025-10256`, :cve_nist:`2025-12343` and
   :cve_nist:`2025-25468`
-  freetype: Fix :cve_nist:`2026-23865`
-  gdk-pixbuf: Fix :cve_nist:`2025-6199`
-  glib-2.0: Fix :cve_nist:`2026-1484`, :cve_nist:`2026-1485` and :cve_nist:`2026-1489`
-  gnupg: Fix :cve_nist:`2025-68973`
-  gnutls: Fix :cve_nist:`2025-14831`
-  go 1.22.12: Fix :cve_nist:`2025-61726`, :cve_nist:`2025-61728`, :cve_nist:`2025-61730`,
   :cve_nist:`2025-61731`, :cve_nist:`2025-61732`, :cve_nist:`2025-68119` and :cve_nist:`2025-68121`
-  harfbuzz: Fix :cve_nist:`2026-22693`
-  inetutils: Fix :cve_nist:`2026-28372` and :cve_nist:`2026-32746`
-  libpng: Fix :cve_nist:`2026-25646`
-  libsndfile1: Fix :cve_nist:`2025-56226`
-  libtheora: Ignore :cve_nist:`2024-56431`
-  linux-yocto/6.6: Fix :cve_nist:`2025-38593`, :cve_nist:`2025-38643`, :cve_nist:`2025-38678`,
   :cve_nist:`2025-40039`, :cve_nist:`2025-40040`, :cve_nist:`2025-40149`, :cve_nist:`2025-40164`,
   :cve_nist:`2025-40251`, :cve_nist:`2025-68211`, :cve_nist:`2025-68214`, :cve_nist:`2025-68223`,
   :cve_nist:`2025-68340`, :cve_nist:`2025-68365`, :cve_nist:`2025-68725`, :cve_nist:`2025-68817`,
   :cve_nist:`2025-71068`, :cve_nist:`2025-71071`, :cve_nist:`2025-71075`, :cve_nist:`2025-71077`,
   :cve_nist:`2025-71078`, :cve_nist:`2025-71079`, :cve_nist:`2025-71081`, :cve_nist:`2025-71082`,
   :cve_nist:`2025-71083`, :cve_nist:`2025-71084`, :cve_nist:`2025-71085`, :cve_nist:`2025-71086`,
   :cve_nist:`2025-71087`, :cve_nist:`2025-71088`, :cve_nist:`2025-71089`, :cve_nist:`2025-71091`,
   :cve_nist:`2025-71093`, :cve_nist:`2025-71094`, :cve_nist:`2025-71095`, :cve_nist:`2025-71096`,
   :cve_nist:`2025-71097`, :cve_nist:`2025-71098`, :cve_nist:`2025-71101`, :cve_nist:`2025-71102`,
   :cve_nist:`2025-71104`, :cve_nist:`2025-71105`, :cve_nist:`2025-71107`, :cve_nist:`2025-71108`,
   :cve_nist:`2025-71111`, :cve_nist:`2025-71112`, :cve_nist:`2025-71113`, :cve_nist:`2025-71114`,
   :cve_nist:`2025-71116`, :cve_nist:`2025-71118`, :cve_nist:`2025-71119`, :cve_nist:`2025-71120`,
   :cve_nist:`2025-71121`, :cve_nist:`2025-71122`, :cve_nist:`2025-71125`, :cve_nist:`2025-71126`,
   :cve_nist:`2025-71127`, :cve_nist:`2025-71129`, :cve_nist:`2025-71130`, :cve_nist:`2025-71131`,
   :cve_nist:`2025-71132`, :cve_nist:`2025-71133`, :cve_nist:`2025-71136`, :cve_nist:`2025-71137`,
   :cve_nist:`2025-71138`, :cve_nist:`2025-71141`, :cve_nist:`2025-71143`, :cve_nist:`2025-71147`,
   :cve_nist:`2025-71148`, :cve_nist:`2025-71149`, :cve_nist:`2025-71150`, :cve_nist:`2025-71151`,
   :cve_nist:`2025-71153`, :cve_nist:`2025-71154`, :cve_nist:`2025-71160`, :cve_nist:`2025-71162`,
   :cve_nist:`2025-71163`, :cve_nist:`2025-71180`, :cve_nist:`2025-71182`, :cve_nist:`2025-71183`,
   :cve_nist:`2025-71185`, :cve_nist:`2025-71186`, :cve_nist:`2025-71188`, :cve_nist:`2025-71189`,
   :cve_nist:`2025-71190`, :cve_nist:`2025-71191`, :cve_nist:`2025-71200`, :cve_nist:`2026-22976`,
   :cve_nist:`2026-22977`, :cve_nist:`2026-22978`, :cve_nist:`2026-22979`, :cve_nist:`2026-22980`,
   :cve_nist:`2026-22982`, :cve_nist:`2026-22984`, :cve_nist:`2026-22990`, :cve_nist:`2026-22991`,
   :cve_nist:`2026-22992`, :cve_nist:`2026-22994`, :cve_nist:`2026-22997`, :cve_nist:`2026-22998`,
   :cve_nist:`2026-22999`, :cve_nist:`2026-23001`, :cve_nist:`2026-23003`, :cve_nist:`2026-23005`,
   :cve_nist:`2026-23006`, :cve_nist:`2026-23010`, :cve_nist:`2026-23011`, :cve_nist:`2026-23019`,
   :cve_nist:`2026-23020`, :cve_nist:`2026-23021`, :cve_nist:`2026-23025`, :cve_nist:`2026-23026`,
   :cve_nist:`2026-23060`, :cve_nist:`2026-23061`, :cve_nist:`2026-23062`, :cve_nist:`2026-23063`,
   :cve_nist:`2026-23064`, :cve_nist:`2026-23068`, :cve_nist:`2026-23069`, :cve_nist:`2026-23071`,
   :cve_nist:`2026-23073`, :cve_nist:`2026-23074`, :cve_nist:`2026-23075`, :cve_nist:`2026-23076`,
   :cve_nist:`2026-23078`, :cve_nist:`2026-23080`, :cve_nist:`2026-23083`, :cve_nist:`2026-23084`,
   :cve_nist:`2026-23085`, :cve_nist:`2026-23086`, :cve_nist:`2026-23087`, :cve_nist:`2026-23088`,
   :cve_nist:`2026-23089`, :cve_nist:`2026-23090`, :cve_nist:`2026-23091`, :cve_nist:`2026-23093`,
   :cve_nist:`2026-23094`, :cve_nist:`2026-23095`, :cve_nist:`2026-23096`, :cve_nist:`2026-23097`,
   :cve_nist:`2026-23098`, :cve_nist:`2026-23099`, :cve_nist:`2026-23101`, :cve_nist:`2026-23102`,
   :cve_nist:`2026-23103`, :cve_nist:`2026-23105`, :cve_nist:`2026-23107`, :cve_nist:`2026-23108`,
   :cve_nist:`2026-23110`, :cve_nist:`2026-23113`, :cve_nist:`2026-23116`, :cve_nist:`2026-23119`,
   :cve_nist:`2026-23120`, :cve_nist:`2026-23121`, :cve_nist:`2026-23123`, :cve_nist:`2026-23124`,
   :cve_nist:`2026-23125`, :cve_nist:`2026-23126`, :cve_nist:`2026-23128`, :cve_nist:`2026-23131`,
   :cve_nist:`2026-23133`, :cve_nist:`2026-23135`, :cve_nist:`2026-23136`, :cve_nist:`2026-23139`,
   :cve_nist:`2026-23140`, :cve_nist:`2026-23141`, :cve_nist:`2026-23142`, :cve_nist:`2026-23144`,
   :cve_nist:`2026-23146`, :cve_nist:`2026-23150`, :cve_nist:`2026-23156`, :cve_nist:`2026-23160`,
   :cve_nist:`2026-23163`, :cve_nist:`2026-23164`, :cve_nist:`2026-23167`, :cve_nist:`2026-23168`,
   :cve_nist:`2026-23170`, :cve_nist:`2026-23172`, :cve_nist:`2026-23173` and :cve_nist:`2026-23212`
-  openssl: fix :cve_nist:`2025-15468` and :cve_nist:`2025-69419`
-  python3-cryptography: Fix :cve_nist:`2026-26007`
-  python3-pip: Fix :cve_nist:`2026-1703`
-  python3-pyopenssl: Fix :cve_nist:`2026-27448` and :cve_nist:`2026-27459`
-  tiff: ignore :cve_nist:`2025-61144` and :cve_nist:`2025-61145`
-  vim: ignore :cve_nist:`2025-66476`
-  zlib: Fix :cve_nist:`2026-27171`


Fixes in Yocto-5.0.17
~~~~~~~~~~~~~~~~~~~~~

-  README: Add scarthgap subject-prefix to git-send-email suggestion
-  bind: upgrade to 9.18.44
-  bitbake: COW: Fix hardcoded magic numbers and work with python 3.13
-  bitbake: fetch2: Fix LFS object checkout in submodules
-  bitbake: fetch2: Fix incorrect lfs parametrization for submodules
-  bitbake: fetch2: don't try to preserve all attributes when unpacking files
-  bitbake: gitsm: Add clean function
-  build-appliance-image: Update to scarthgap head revision
-  classes/buildhistory: Do not sign buildhistory commits
-  create-pull-request: Keep commit hash to be pulled in cover email
-  dev-manual: delete references to "tar" package format
-  docs: Makefile: pass -silent to latexmk
-  go-vendor: Fix absolute paths issue
-  improve_kernel_cve_report: add option to read debugsources.zstd
-  improve_kernel_cve_report: do not override backported-patch
-  improve_kernel_cve_report: do not use custom version
-  linux-yocto/6.6: upgrade to v6.6.123
-  lsb.py: strip ' from os-release file
-  migration-guides: add release notes for 5.0.16
-  mobile-broadband-provider-info: upgrade to 20251101
-  oe-setup-build: Fix typo
-  oeqa/selftest/wic: test recursive dir copy on ext partitions
-  openssl: upgrade to 3.5.5
-  overview-manual/concepts: list other possible class directories
-  overview-manual: escape wildcard in inline markup
-  poky.conf: Bump version for 5.0.17 release
-  poky.conf: add Centos Stream 9, fedora-41, rocky-8 to :term:`SANITY_TESTED_DISTROS`
-  pseudo: Update to include a fix for systems with kernel <5.6
-  python3-pip: drop unused Windows distlib launcher templates
-  python3-setuptools: drop Windows launcher executables on non-mingw builds
-  ref-manual/classes.rst: fix broken links to U-Boot documentation
-  ref-manual/system-requirements.rst: update supported, end-of-life and untested distros
-  scripts/install-buildtools: Update to 5.0.15
-  spdx30_tasks: Exclude 'doc' when exporting :term:`PACKAGECONFIG` to :term:`SPDX`
-  spdx: add option to include only compiled sources
-  systemd-systemctl: Fix instance name parsing with escapes or periods
-  tzdata,tzcode-native: upgrade to 2025c
-  u-boot: move CVE Fixes out of the common .inc file
-  uboot-config: Fix devtool modify
-  weston: fix a touch-calibrator issue
-  what-i-wish-id-known.rst: replace figure by the new SVG
-  wic/engine: error on old host debugfs for standalone directory copy
-  wic/engine: fix copying directories into wic image with ext* partition
-  wireless-regdb: upgrade to 2026.02.04


Known Issues in Yocto-5.0.17
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.17
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Amaury Couderc
-  Ankur Tyagi
-  Antonin Godard
-  Benjamin Robin (Schneider Electric)
-  Bruce Ashfield
-  Daniel Dragomir
-  Daniel Turull
-  Deepak Rathore
-  Dragomir, Daniel
-  Eduardo Ferreira
-  Fabio Berton
-  Hitendra Prajapati
-  Hugo SIMELIERE
-  João Marcos Costa (Schneider Electric)
-  Kristiyan Chakarov
-  Krupal Ka Patel
-  Lee Chee Yang
-  Livin Sunny
-  Martin Jansa
-  Michael Opdenacker
-  Ming Liu
-  Nguyen Dat Tho
-  Paul Barker
-  Peter Marko
-  Philip Lorenz
-  Quentin Schulz
-  Richard Purdie
-  Robert P. J. Day
-  Robert Yang
-  Ross Burton
-  Ryan Eatmon
-  Shaik Moin
-  Tom Hochstein
-  Trent Piepho
-  Vijay Anusuri
-  Yoann Congal

Repositories / Downloads for Yocto-5.0.17
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.17 </yocto-docs/log/?h=yocto-5.0.17>`
-  Git Revision: :yocto_git:`aa7226705451e6c1ef964d49963bbed29b267c27 </yocto-docs/commit/?id=aa7226705451e6c1ef964d49963bbed29b267c27>`
-  Release Artefact: yocto-docs-aa7226705451e6c1ef964d49963bbed29b267c27
-  sha: d429833609637657f213611317dfadbd70293fff2f9e22753d1f71ef8515a6c0
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.17/yocto-docs-aa7226705451e6c1ef964d49963bbed29b267c27.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.17/yocto-docs-aa7226705451e6c1ef964d49963bbed29b267c27.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.17 </poky/log/?h=yocto-5.0.17>`
-  Git Revision: :yocto_git:`1e8099846661571ede077f533eb1b6c86818ddce </poky/commit/?id=1e8099846661571ede077f533eb1b6c86818ddce>`
-  Release Artefact: poky-1e8099846661571ede077f533eb1b6c86818ddce
-  sha: b56890576f593cc881ea8e467562d842cfca248099ce653d28ca14d250f6219e
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.17/poky-1e8099846661571ede077f533eb1b6c86818ddce.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.17/poky-1e8099846661571ede077f533eb1b6c86818ddce.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.17 </openembedded-core/log/?h=yocto-5.0.17>`
-  Git Revision: :oe_git:`52380df998b3a8fe6a091f8547434a3231320a8e </openembedded-core/commit/?id=52380df998b3a8fe6a091f8547434a3231320a8e>`
-  Release Artefact: oecore-52380df998b3a8fe6a091f8547434a3231320a8e
-  sha: a948d75acf76a392d170129ce6eb6f5fe45082d95b4fd28045aac58b8373cb26
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.17/oecore-52380df998b3a8fe6a091f8547434a3231320a8e.tar.bz

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.17/oecore-52380df998b3a8fe6a091f8547434a3231320a8e.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`scarthgap </meta-yocto/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.17 </meta-yocto/log/?h=yocto-5.0.17>`
-  Git Revision: :yocto_git:`c7c38663a1cafb1fa8593c0b246811e51d3bbe20 </meta-yocto/commit/?id=c7c38663a1cafb1fa8593c0b246811e51d3bbe20>`
-  Release Artefact: meta-yocto-c7c38663a1cafb1fa8593c0b246811e51d3bbe20
-  sha: 5a2a9360249e639694cc2a75985e3907085512b3eb236e8491cb07f1e0cb0f19
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.17/meta-yocto-c7c38663a1cafb1fa8593c0b246811e51d3bbe20.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.17/meta-yocto-c7c38663a1cafb1fa8593c0b246811e51d3bbe20.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.17 </meta-mingw/log/?h=yocto-5.0.17>`
-  Git Revision: :yocto_git:`bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f </meta-mingw/commit/?id=bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f>`
-  Release Artefact: meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f
-  sha: ab073def6487f237ac125d239b3739bf02415270959546b6b287778664f0ae65
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.17/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.17/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.17 </bitbake/log/?h=yocto-5.0.17>`
-  Git Revision: :oe_git:`d3b4c352dd33fca90cd31649eda054b884478739 </bitbake/commit/?id=d3b4c352dd33fca90cd31649eda054b884478739>`
-  Release Artefact: bitbake-d3b4c352dd33fca90cd31649eda054b884478739
-  sha: 1021fc412780e21b25ccb045b66368ebe3fc4e785a65066ac0cafb9bdd5492fa
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.17/bitbake-d3b4c352dd33fca90cd31649eda054b884478739.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.0.17/bitbake-d3b4c352dd33fca90cd31649eda054b884478739.tar.bz2

