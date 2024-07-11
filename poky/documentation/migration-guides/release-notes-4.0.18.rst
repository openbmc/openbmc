.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.18 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.18
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  curl: Fix :cve:`2024-2398`
-  expat: fix :cve:`2023-52426` and :cve:`2024-28757`
-  libssh2: fix :cve:`2023-48795`
-  ncurses: Fix :cve:`2023-50495`
-  nghttp2: Fix :cve:`2024-28182` and :cve:`2023-44487`
-  openssh: Ignore :cve:`2023-51767`
-  openssl: Fix :cve:`2024-2511`
-  perl: Ignore :cve:`2023-47100`
-  python3-cryptography: Fix :cve:`2024-26130`
-  python3-urllib3: Fix :cve:`2023-45803`
-  qemu: Fix :cve:`2023-6683`
-  ruby: fix :cve_mitre:`2024-27281`
-  rust: Ignore :cve:`2024-24576`
-  tiff: Fix :cve:`2023-52356` and :cve:`2023-6277`
-  xserver-xorg: Fix :cve:`2024-31080` and :cve:`2024-31081`
-  xwayland: Fix :cve:`2023-6816`, :cve:`2024-0408` and :cve:`2024-0409`


Fixes in Yocto-4.0.18
~~~~~~~~~~~~~~~~~~~~~

-  build-appliance-image: Update to kirkstone head revision
-  common-licenses: Backport missing license
-  contributor-guide: add notes for tests
-  contributor-guide: be more specific about meta-* trees
-  cups: fix typo in :cve:`2023-32360` backport patch
-  cve-update-nvd2-native: Add an age threshold for incremental update
-  cve-update-nvd2-native: Fix CVE configuration update
-  cve-update-nvd2-native: Fix typo in comment
-  cve-update-nvd2-native: Remove duplicated CVE_CHECK_DB_FILE definition
-  cve-update-nvd2-native: Remove rejected CVE from database
-  cve-update-nvd2-native: nvd_request_next: Improve comment
-  dev-manual: improve descriptions of 'bitbake -S printdiff'
-  dev-manual: packages: fix capitalization
-  docs: conf.py: properly escape backslashes for latex_elements
-  gcc: Backport sanitizer fix for 32-bit ALSR
-  glibc: Fix subscript typos for get_nscd_addresses
-  kernel-dev: join mkdir commands with -p
-  linux-firmware: Upgrade to 20240220
-  manuals: add initial sphinx-lint support
-  manuals: add initial stylechecks with Vale
-  manuals: document VIRTUAL-RUNTIME variables
-  manuals: fix duplicate "stylecheck" target
-  manuals: fix incorrect double backticks
-  manuals: fix trailing spaces
-  manuals: refer to new yocto-patches mailing list wherever appropriate
-  manuals: remove tab characters
-  manuals: replace hyphens with em dashes
-  manuals: use "manual page(s)"
-  migration-guides: add release notes for 4.0.17
-  poky.conf: bump version for 4.0.18
-  profile-manual: usage.rst: fix reference to bug report
-  profile-manual: usage.rst: formatting fixes
-  profile-manual: usage.rst: further style improvements
-  python3-urllib3: Upgrade to v1.26.18
-  ref-manual: add documentation of the variable :term:`SPDX_NAMESPACE_PREFIX`
-  ref-manual: tasks: do_cleanall: recommend using '-f' instead
-  ref-manual: tasks: do_cleansstate: recommend using '-f' instead for a shared sstate
-  ref-manual: variables: adding multiple groups in :term:`GROUPADD_PARAM`
-  ref-manual: variables: correct sdk installation default path
-  stress-ng: avoid calling sync during do_compile
-  systemd: Fix vlan qos mapping
-  tcl: Add a way to skip ptests
-  tcl: skip async and event tests in run-ptest
-  tcl: skip timing-dependent tests in run-ptest
-  valgrind: skip intermittently failing ptest
-  wireless-regdb: Upgrade to 2024.01.23
-  yocto-uninative: Update to 4.4 for glibc 2.39


Known Issues in Yocto-4.0.18
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.18
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alex Kiernan
-  Alex Stewart
-  Alexander Kanavin
-  BELOUARGA Mohamed
-  Claus Stovgaard
-  Colin McAllister
-  Geoff Parker
-  Haitao Liu
-  Harish Sadineni
-  Johan Bezem
-  Jonathan GUILLOT
-  Jörg Sommer
-  Khem Raj
-  Lee Chee Yang
-  Luca Ceresoli
-  Martin Jansa
-  Meenali Gupta
-  Michael Halstead
-  Michael Opdenacker
-  Peter Marko
-  Quentin Schulz
-  Ross Burton
-  Sana Kazi
-  Simone Weiß
-  Soumya Sambu
-  Steve Sakoman
-  Tan Wen Yan
-  Vijay Anusuri
-  Wang Mingyu
-  Yoann Congal
-  Yogita Urade
-  Zahir Hussain


Repositories / Downloads for Yocto-4.0.18
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.18 </poky/log/?h=yocto-4.0.18>`
-  Git Revision: :yocto_git:`31751bba1c789f15f574773a659b8017d7bcf440 </poky/commit/?id=31751bba1c789f15f574773a659b8017d7bcf440>`
-  Release Artefact: poky-31751bba1c789f15f574773a659b8017d7bcf440
-  sha: 72d5aa65c3c37766ebc24b212740272c1d52342468548f9c070241d3522ad2ca
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.18/poky-31751bba1c789f15f574773a659b8017d7bcf440.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.18/poky-31751bba1c789f15f574773a659b8017d7bcf440.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.18 </openembedded-core/log/?h=yocto-4.0.18>`
-  Git Revision: :oe_git:`b7182571242dc4e23e5250a449d90348e62a6abc </openembedded-core/commit/?id=b7182571242dc4e23e5250a449d90348e62a6abc>`
-  Release Artefact: oecore-b7182571242dc4e23e5250a449d90348e62a6abc
-  sha: 6f257e50c10ebae673dcf61a833b3270db6d22781f02f6794a370aac839f1020
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.18/oecore-b7182571242dc4e23e5250a449d90348e62a6abc.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.18/oecore-b7182571242dc4e23e5250a449d90348e62a6abc.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.18 </meta-mingw/log/?h=yocto-4.0.18>`
-  Git Revision: :yocto_git:`f6b38ce3c90e1600d41c2ebb41e152936a0357d7 </meta-mingw/commit/?id=f6b38ce3c90e1600d41c2ebb41e152936a0357d7>`
-  Release Artefact: meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7
-  sha: 7d57167c19077f4ab95623d55a24c2267a3a3fb5ed83688659b4c03586373b25
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.18/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.18/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.18 </meta-gplv2/log/?h=yocto-4.0.18>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.18/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.18/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.18 </bitbake/log/?h=yocto-4.0.18>`
-  Git Revision: :oe_git:`40fd5f4eef7460ca67f32cfce8e229e67e1ff607 </bitbake/commit/?id=40fd5f4eef7460ca67f32cfce8e229e67e1ff607>`
-  Release Artefact: bitbake-40fd5f4eef7460ca67f32cfce8e229e67e1ff607
-  sha: 5d20a0e4c5d0fce44bd84778168714a261a30a4b83f67c88df3b8a7e7115e444
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.18/bitbake-40fd5f4eef7460ca67f32cfce8e229e67e1ff607.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.18/bitbake-40fd5f4eef7460ca67f32cfce8e229e67e1ff607.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.18 </yocto-docs/log/?h=yocto-4.0.18>`
-  Git Revision: :yocto_git:`fd1423141e7458ba557db465c171b0b4e9063987 </yocto-docs/commit/?id=fd1423141e7458ba557db465c171b0b4e9063987>`

