.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.12 (Scarthgap)
------------------------------------------

Security Fixes in Yocto-5.0.12
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2024-52615`
-  binutils: Fix :cve_nist:`2025-7545` and :cve_nist:`2025-7546`
-  busybox: Fix :cve_nist:`2023-39810`
-  dropbear: Fix :cve_nist:`2025-47203`
-  gdk-pixbuf: Fix :cve_nist:`2025-7345`
-  git: Fix :cve_nist:`2025-27613`, :cve_nist:`2025-27614`, :cve_nist:`2025-46334`,
   :cve_nist:`2025-46835`, :cve_nist:`2025-48384`, :cve_nist:`2025-48385` and :cve_nist:`2025-48386`
-  glib-2.0: Ignore :cve_nist:`2025-4056`
-  glibc: Fix :cve_nist:`2025-8058`
-  gnutls: Fix :cve_nist:`2025-6395`, :cve_nist:`2025-32988`, :cve_nist:`2025-32989` and
   :cve_nist:`2025-32990`
-  go: Ignore :cve_nist:`2025-0913`
-  gstreamer1.0-plugins-base: Fix :cve_nist:`2025-47806` and :cve_nist:`2025-47808`
-  gstreamer1.0-plugins-good: Fix :cve_nist:`2025-47183` and :cve_nist:`2025-47219`
-  iputils: Fix :cve_nist:`2025-48964`
-  libpam: Fix :cve_nist:`2025-6020`
-  libxml2: Fix :cve_nist:`2025-6170`, :cve_nist:`2025-49794`, :cve_nist:`2025-49795` and
   :cve_nist:`2025-49796`
-  libxml2: Ignore :cve_nist:`2025-8732`
-  ncurses: Fix :cve_nist:`2025-6141`
-  openssl: Fix :cve_nist:`2024-41996` and :cve_nist:`2025-27587`
-  python3: Fix :cve_nist:`2025-8194`
-  sqlite3: Fix :cve_nist:`2025-6965`
-  sudo: Fix :cve_nist:`2025-32463`
-  xserver-xorg: Fix :cve_nist:`2022-49737`, :cve_nist:`2025-49175`, :cve_nist:`2025-49176`,
   :cve_nist:`2025-49177`, :cve_nist:`2025-49178`, :cve_nist:`2025-49179`, :cve_nist:`2025-49180`
   and :cve_nist:`2025-49176`
-  xz: Ignore :cve_nist:`2024-47611`


Fixes in Yocto-5.0.12
~~~~~~~~~~~~~~~~~~~~~

-  bash: Stick to C17 std
-  bash: use -std=gnu17 also for native :term:`CFLAGS`
-  binutils: stable 2.42 branch updates
-  bitbake: bitbake: runqueue: Verify mcdepends are valid
-  bitbake: test/fetch: Switch u-boot based test to use our own mirror
-  bitbake: utils: Optimise signal/sigmask performance
-  build-appliance-image: Update to scarthgap head revision
-  cairo: fix build with gcc-15 on host
-  cmake: Add :term:`PACKAGECONFIG` option for debugger support
-  cve-check: Add missing call to exit_if_errors
-  dev-manual/start.rst: added missing command in Optimize your VHDX file using DiskPart
-  e2fsprogs: Fix build failure with gcc 15
-  git: Upgrade to 2.44.4
-  glibc: stable 2.39 branch updates
-  gnutls: patch read buffer overrun in the "pre_shared_key" extension
-  gnutls: patch reject zero-length version in certificate request
-  go-helloworld: fix license
-  kea: set correct permissions for /var/run/kea
-  linux-libc-headers: Fix invalid conversion in cn_proc.h
-  migration-guides: add release notes for 5.0.11
-  mtools: upgrade to 4.0.49
-  oe-debuginfod: add option for data storage
-  orc: set :term:`CVE_PRODUCT`
-  overview-manual/yp-intro.rst: fix broken link to article
-  parted: Fix build with GCC 15
-  poky.conf: bump version for 5.0.12
-  python3: update CVE product
-  ref-manual/classes.rst: document the testexport class
-  ref-manual/system-requirements.rst: update supported distributions
-  ref-manual/variables.rst: document :term:`SPL_DTB_BINARY` :term:`FIT_CONF_PREFIX` variable
-  scripts/install-buildtools: Update to 5.0.11
-  sudo: upgrade to 1.9.17p1
-  timedated: wait for jobs before SetNTP response
-  variables.rst: remove references to obsolete tar packaging
-  xserver-xorg: upgrade to 21.1.18


Known Issues in Yocto-5.0.12
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.12
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Antonin Godard
-  Archana Polampalli
-  Daniel Turull
-  Deepesh Varatharajan
-  Erik Lindsten
-  Fabio Berton
-  Hitendra Prajapati
-  Jinfeng Wang
-  Joe Slater
-  Khem Raj
-  Lee Chee Yang
-  Marco Cavallini
-  Mark Hatle
-  Martin Jansa
-  Michal Seben
-  Nikhil R
-  Peter Marko
-  Philip Lorenz
-  Praveen Kumar
-  Quentin Schulz
-  Richard Purdie
-  Robert P. J. Day
-  Roland Kovacs
-  Steve Sakoman
-  Vijay Anusuri
-  Wang Mingyu
-  Yash Shinde
-  Yi Zhao
-  Zhang Peng

Repositories / Downloads for Yocto-5.0.12
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.12 </poky/log/?h=yocto-5.0.12>`
-  Git Revision: :yocto_git:`ec220ae083dba35c279192b2249ad03fe238446e </poky/commit/?id=ec220ae083dba35c279192b2249ad03fe238446e>`
-  Release Artefact: poky-ec220ae083dba35c279192b2249ad03fe238446e
-  sha: a5f8c2ad491c59d0bdfb85f46a136b0ee66cfdd4359ab1ab9dac2430d0a52c17
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.12/poky-ec220ae083dba35c279192b2249ad03fe238446e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.12/poky-ec220ae083dba35c279192b2249ad03fe238446e.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.12 </openembedded-core/log/?h=yocto-5.0.12>`
-  Git Revision: :oe_git:`93c7489d843a0e46fe4fc685b356d0ae885300d7 </openembedded-core/commit/?id=93c7489d843a0e46fe4fc685b356d0ae885300d7>`
-  Release Artefact: oecore-93c7489d843a0e46fe4fc685b356d0ae885300d7
-  sha: 49695592179cd777eee337d922aae354dad4ab503628f0344b1b53329900c4d9
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.12/oecore-93c7489d843a0e46fe4fc685b356d0ae885300d7.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.12/oecore-93c7489d843a0e46fe4fc685b356d0ae885300d7.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.12 </meta-mingw/log/?h=yocto-5.0.12>`
-  Git Revision: :yocto_git:`bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f </meta-mingw/commit/?id=bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f>`
-  Release Artefact: meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f
-  sha: ab073def6487f237ac125d239b3739bf02415270959546b6b287778664f0ae65
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.12/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.12/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.12 </bitbake/log/?h=yocto-5.0.12>`
-  Git Revision: :oe_git:`982645110a19ebb94d519926a4e14c8a2a205cfd </bitbake/commit/?id=982645110a19ebb94d519926a4e14c8a2a205cfd>`
-  Release Artefact: bitbake-982645110a19ebb94d519926a4e14c8a2a205cfd
-  sha: f8d777d322b8f05372d7ce75c67df2db2b7de3f64d5b7769b8051c507161245d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.12/bitbake-982645110a19ebb94d519926a4e14c8a2a205cfd.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.12/bitbake-982645110a19ebb94d519926a4e14c8a2a205cfd.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`scarthgap </meta-yocto/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.12 </meta-yocto/log/?h=yocto-5.0.12>`
-  Git Revision: :yocto_git:`82602cda1a89644d1acbe230a81c93e3fb5031c8 </meta-yocto/commit/?id=82602cda1a89644d1acbe230a81c93e3fb5031c8>`

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.12 </yocto-docs/log/?h=yocto-5.0.12>`
-  Git Revision: :yocto_git:`dd665216fa578a1f2f268790d708c6a5d2912ecf </yocto-docs/commit/?id=dd665216fa578a1f2f268790d708c6a5d2912ecf>`

