.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.22 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.22
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  cups: Fix :cve_nist:`2024-35235` and :cve_nist:`2024-47175`
-  curl: Fix :cve_nist:`2024-8096`
-  expat: Fix :cve_nist:`2024-45490`, :cve_nist:`2024-45491` and :cve_nist:`2024-45492`
-  gnupg: Ignore :cve_nist:`2022-3219`
-  libpcap: Fix :cve_nist:`2023-7256` and :cve_nist:`2024-8006`
-  linux-yocto/5.10: Fix :cve_nist:`2022-48772`, :cve_nist:`2023-52434`, :cve_nist:`2023-52447`, :cve_nist:`2023-52458`, :cve_nist:`2024-0841`, :cve_nist:`2024-26601`, :cve_nist:`2024-26882`, :cve_nist:`2024-26883`, :cve_nist:`2024-26884`, :cve_nist:`2024-26885`, :cve_nist:`2024-26898`, :cve_nist:`2024-26901`, :cve_nist:`2024-26903`, :cve_nist:`2024-26907`, :cve_nist:`2024-26934`, :cve_nist:`2024-26978`, :cve_nist:`2024-27013`, :cve_nist:`2024-27020`, :cve_nist:`2024-35972`, :cve_nist:`2024-35978`, :cve_nist:`2024-35982`, :cve_nist:`2024-35984`, :cve_nist:`2024-35990`, :cve_nist:`2024-35997`, :cve_nist:`2024-36008`, :cve_nist:`2024-36270`, :cve_nist:`2024-36489`, :cve_nist:`2024-36902`, :cve_nist:`2024-36971`, :cve_nist:`2024-36978`, :cve_nist:`2024-38546`, :cve_nist:`2024-38547`, :cve_nist:`2024-38549`, :cve_nist:`2024-38552`, :cve_nist:`2024-38555`, :cve_nist:`2024-38583`, :cve_nist:`2024-38590`, :cve_nist:`2024-38597`, :cve_nist:`2024-38598`, :cve_nist:`2024-38627`, :cve_nist:`2024-38633`, :cve_nist:`2024-38661`, :cve_nist:`2024-38662`, :cve_nist:`2024-38780`, :cve_nist:`2024-39292`, :cve_nist:`2024-39301`, :cve_nist:`2024-39468`, :cve_nist:`2024-39471`, :cve_nist:`2024-39475`, :cve_nist:`2024-39476`, :cve_nist:`2024-39480`, :cve_nist:`2024-39482`, :cve_nist:`2024-39484`, :cve_nist:`2024-39487`, :cve_nist:`2024-39489`, :cve_nist:`2024-39495`, :cve_nist:`2024-39506`, :cve_nist:`2024-40902`, :cve_nist:`2024-40904`, :cve_nist:`2024-40905`, :cve_nist:`2024-40912`, :cve_nist:`2024-40932`, :cve_nist:`2024-40934`, :cve_nist:`2024-40958`, :cve_nist:`2024-40959`, :cve_nist:`2024-40960`, :cve_nist:`2024-40961`, :cve_nist:`2024-40980`, :cve_nist:`2024-40981`, :cve_nist:`2024-40995`, :cve_nist:`2024-41000`, :cve_nist:`2024-41006`, :cve_nist:`2024-41007`, :cve_nist:`2024-41012`, :cve_nist:`2024-41040`, :cve_nist:`2024-41046`, :cve_nist:`2024-41049`, :cve_nist:`2024-41059`, :cve_nist:`2024-41063`, :cve_nist:`2024-41064`, :cve_nist:`2024-41070`, :cve_nist:`2024-41087`, :cve_nist:`2024-41089`, :cve_nist:`2024-41092`, :cve_nist:`2024-41095`, :cve_nist:`2024-41097`, :cve_nist:`2024-42070`, :cve_nist:`2024-42076`, :cve_nist:`2024-42077`, :cve_nist:`2024-42082`, :cve_nist:`2024-42090`, :cve_nist:`2024-42093`, :cve_nist:`2024-42094`, :cve_nist:`2024-42101`, :cve_nist:`2024-42102`, :cve_nist:`2024-42104`, :cve_nist:`2024-42131`, :cve_nist:`2024-42137`, :cve_nist:`2024-42148`, :cve_nist:`2024-42152`, :cve_nist:`2024-42153`, :cve_nist:`2024-42154`, :cve_nist:`2024-42157`, :cve_nist:`2024-42161`, :cve_nist:`2024-42223`, :cve_nist:`2024-42224`, :cve_nist:`2024-42229`, :cve_nist:`2024-42232`, :cve_nist:`2024-42236`, :cve_nist:`2024-42244` and :cve_nist:`2024-42247`
-  linux-yocto/5.15: Fix :cve_nist:`2023-52889`, :cve_nist:`2024-41011`, :cve_nist:`2024-42114`, :cve_nist:`2024-42259`, :cve_nist:`2024-42271`, :cve_nist:`2024-42272`, :cve_nist:`2024-42277`, :cve_nist:`2024-42280`, :cve_nist:`2024-42283`, :cve_nist:`2024-42284`, :cve_nist:`2024-42285`, :cve_nist:`2024-42286`, :cve_nist:`2024-42287`, :cve_nist:`2024-42288`, :cve_nist:`2024-42289`, :cve_nist:`2024-42301`, :cve_nist:`2024-42302`, :cve_nist:`2024-42309`, :cve_nist:`2024-42310`, :cve_nist:`2024-42311`, :cve_nist:`2024-42313`, :cve_nist:`2024-43817`, :cve_nist:`2024-43828`, :cve_nist:`2024-43854`, :cve_nist:`2024-43856`, :cve_nist:`2024-43858`, :cve_nist:`2024-43860`, :cve_nist:`2024-43861`, :cve_nist:`2024-43863`, :cve_nist:`2024-43871`, :cve_nist:`2024-43873`, :cve_nist:`2024-43882`, :cve_nist:`2024-43889`, :cve_nist:`2024-43890`, :cve_nist:`2024-43893`, :cve_nist:`2024-43894`, :cve_nist:`2024-43902`, :cve_nist:`2024-43907`, :cve_nist:`2024-43908`, :cve_nist:`2024-43909`, :cve_nist:`2024-43914`, :cve_nist:`2024-44934`, :cve_nist:`2024-44935`, :cve_nist:`2024-44944`, :cve_nist:`2024-44947`, :cve_nist:`2024-44952`, :cve_nist:`2024-44954`, :cve_nist:`2024-44958`, :cve_nist:`2024-44960`, :cve_nist:`2024-44965`, :cve_nist:`2024-44966`, :cve_nist:`2024-44969`, :cve_nist:`2024-44971`, :cve_nist:`2024-44982`, :cve_nist:`2024-44983`, :cve_nist:`2024-44985`, :cve_nist:`2024-44986`, :cve_nist:`2024-44987`, :cve_nist:`2024-44988`, :cve_nist:`2024-44989`, :cve_nist:`2024-44990`, :cve_nist:`2024-44995`, :cve_nist:`2024-44998`, :cve_nist:`2024-44999`, :cve_nist:`2024-45003`, :cve_nist:`2024-45006`, :cve_nist:`2024-45011`, :cve_nist:`2024-45016`, :cve_nist:`2024-45018`, :cve_nist:`2024-45021`, :cve_nist:`2024-45025`, :cve_nist:`2024-45026`, :cve_nist:`2024-45028`, :cve_nist:`2024-46673`, :cve_nist:`2024-46674`, :cve_nist:`2024-46675`, :cve_nist:`2024-46676`, :cve_nist:`2024-46677`, :cve_nist:`2024-46679`, :cve_nist:`2024-46685`, :cve_nist:`2024-46689`, :cve_nist:`2024-46702` and :cve_nist:`2024-46707`
-  openssl: Fix :cve_nist:`2024-6119`
-  procps: Fix :cve_nist:`2023-4016`
-  python3: Fix :cve_nist:`2023-27043`, :cve_nist:`2024-4030`, :cve_nist:`2024-4032`, :cve_nist:`2024-6923`, :cve_nist:`2024-6232`, :cve_nist:`2024-7592` and :cve_nist:`2024-8088`
-  qemu: Fix :cve_nist:`2024-4467`
-  rust: Ignore :cve_nist:`2024-43402`
-  webkitgtk: Fix :cve_nist:`2024-40779`
-  wpa-supplicant: Ignore :cve_nist:`2024-5290`
-  wpa-supplicant: Fix :cve_nist:`2024-3596`


Fixes in Yocto-4.0.22
~~~~~~~~~~~~~~~~~~~~~

-  bintuils: stable 2.38 branch update
-  bitbake: fetch2/wget: Canonicalize :term:`DL_DIR` paths for wget2 compatibility
-  bitbake: fetch/wget: Move files into place atomically
-  bitbake: hashserv: tests: Omit client in slow server start test
-  bitbake: tests/fetch: Tweak to work on Fedora40
-  bitbake: wget: Make wget --passive-ftp option conditional on ftp/ftps
-  build-appliance-image: Update to kirkstone head revision
-  buildhistory: Fix intermittent package file list creation
-  buildhistory: Restoring files from preserve list
-  buildhistory: Simplify intercept call sites and drop SSTATEPOSTINSTFUNC usage
-  busybox: Fix cut with "-s" flag
-  cdrtools-native: fix build with gcc-14
-  curl: free old conn better on reuse
-  cve-exclusion: Drop the version comparision/warning
-  dejagnu: Fix :term:`LICENSE` (change to GPL-3.0-only)
-  doc/features: remove duplicate word in distribution feature ext2
-  gcc: upgrade to v11.5
-  gcr: Fix :term:`LICENSE` (change to LGPL-2.0-only)
-  glibc: stable 2.35 branch updates
-  install-buildtools: fix "test installation" step
-  install-buildtools: remove md5 checksum validation
-  install-buildtools: support buildtools-make-tarball and update to 4.1
-  iw: Fix :term:`LICENSE` (change to ISC)
-  kmscube: Add patch to fix -int-conversion build error
-  lib/oeqa: rename assertRaisesRegexp to assertRaisesRegex
-  libedit: Make docs generation deterministic
-  linux-yocto/5.10: fix NFSV3 config warning
-  linux-yocto/5.10: remove obsolete options
-  linux-yocto/5.10: update to v5.10.223
-  linux-yocto/5.15: update to v5.15.166
-  meta-world-pkgdata: Inherit nopackages
-  migration-guide: add release notes for 4.0.21
-  openssl: Upgrade to 3.0.15
-  poky.conf: bump version for 4.0.22
-  populate_sdk_base: inherit nopackages
-  python3: Upgrade to 3.10.15
-  ruby: Make docs generation deterministic
-  runqemu: keep generating tap devices
-  scripts/install-buildtools: Update to 4.0.21
-  selftest/runtime_test/virgl: Disable for all fedora
-  testexport: fallback for empty :term:`IMAGE_LINK_NAME`
-  testimage: fallback for empty :term:`IMAGE_LINK_NAME`
-  tiff: Fix :term:`LICENSE` (change to libtiff)
-  udev-extraconf: Add collect flag to mount
-  unzip: Fix :term:`LICENSE` (change to Info-ZIP)
-  valgrind: disable avx_estimate_insn.vgtest
-  wpa-supplicant: Patch security advisory 2024-2
-  yocto-uninative: Update to 4.5 for gcc 14
-  yocto-uninative: Update to 4.6 for glibc 2.40
-  zip: Fix :term:`LICENSE` (change to Info-ZIP)
-  zstd: fix :term:`LICENSE` statement (change to "BSD-3-Clause | GPL-2.0-only")


Known Issues in Yocto-4.0.22
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  ``oeqa/runtime``: the ``beaglebone-yocto`` target fails the ``parselogs``
   runtime test due to unexpected kernel error messages in the log (see
   :yocto_bugs:`bug 15624 </show_bug.cgi?id=15624>` on Bugzilla).


Contributors to Yocto-4.0.22
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Alexandre Belloni
-  Archana Polampalli
-  Bruce Ashfield
-  Colin McAllister
-  Deepthi Hemraj
-  Divya Chellam
-  Hitendra Prajapati
-  Hugo SIMELIERE
-  Jinfeng Wang
-  Joshua Watt
-  Jörg Sommer
-  Konrad Weihmann
-  Lee Chee Yang
-  Martin Jansa
-  Massimiliano Minella
-  Michael Halstead
-  Mingli Yu
-  Niko Mauno
-  Paul Eggleton
-  Pedro Ferreira
-  Peter Marko
-  Purushottam Choudhary
-  Richard Purdie
-  Rob Woolley
-  Rohini Sangam
-  Ross Burton
-  Rudolf J Streif
-  Siddharth Doshi
-  Steve Sakoman
-  Vijay Anusuri
-  Vivek Kumbhar


Repositories / Downloads for Yocto-4.0.22
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.22 </poky/log/?h=yocto-4.0.22>`
-  Git Revision: :yocto_git:`7e87dc422d972e0dc98372318fcdc63a76347d16 </poky/commit/?id=7e87dc422d972e0dc98372318fcdc63a76347d16>`
-  Release Artefact: poky-7e87dc422d972e0dc98372318fcdc63a76347d16
-  sha: 5058e7b2474f8cb73c19e776ef58d9784321ef42109d5982747c8c432531239f
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.22/poky-7e87dc422d972e0dc98372318fcdc63a76347d16.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.22/poky-7e87dc422d972e0dc98372318fcdc63a76347d16.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.22 </openembedded-core/log/?h=yocto-4.0.22>`
-  Git Revision: :oe_git:`f09fca692f96c9c428e89c5ef53fbcb92ac0c9bf </openembedded-core/commit/?id=f09fca692f96c9c428e89c5ef53fbcb92ac0c9bf>`
-  Release Artefact: oecore-f09fca692f96c9c428e89c5ef53fbcb92ac0c9bf
-  sha: 378bcc840ba9fbf06a15fea1b5dacdd446f3ad4d85115d708e7bbb20629cdeb4
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.22/oecore-f09fca692f96c9c428e89c5ef53fbcb92ac0c9bf.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.22/oecore-f09fca692f96c9c428e89c5ef53fbcb92ac0c9bf.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.22 </meta-mingw/log/?h=yocto-4.0.22>`
-  Git Revision: :yocto_git:`f6b38ce3c90e1600d41c2ebb41e152936a0357d7 </meta-mingw/commit/?id=f6b38ce3c90e1600d41c2ebb41e152936a0357d7>`
-  Release Artefact: meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7
-  sha: 7d57167c19077f4ab95623d55a24c2267a3a3fb5ed83688659b4c03586373b25
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.22/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.22/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.22 </meta-gplv2/log/?h=yocto-4.0.22>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.22/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.22/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.22 </bitbake/log/?h=yocto-4.0.22>`
-  Git Revision: :oe_git:`eb5c1ce6b1b8f33535ff7b9263ec7648044163ea </bitbake/commit/?id=eb5c1ce6b1b8f33535ff7b9263ec7648044163ea>`
-  Release Artefact: bitbake-eb5c1ce6b1b8f33535ff7b9263ec7648044163ea
-  sha: 473d3e9539160633f3de9d88cce69123f6c623e4c8ab35beb7875868564593cf
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.22/bitbake-eb5c1ce6b1b8f33535ff7b9263ec7648044163ea.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.22/bitbake-eb5c1ce6b1b8f33535ff7b9263ec7648044163ea.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.22 </yocto-docs/log/?h=yocto-4.0.22>`
-  Git Revision: :yocto_git:`2169a52a24ebd1906039c42632bae6c4285a3aca </yocto-docs/commit/?id=2169a52a24ebd1906039c42632bae6c4285a3aca>`

