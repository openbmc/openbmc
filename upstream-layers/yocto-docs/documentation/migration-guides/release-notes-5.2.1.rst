Release notes for Yocto-5.2.1 (Walnascar)
-----------------------------------------

Security Fixes in Yocto-5.2.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  connman :Fix :cve_nist:`2025-32366` and :cve_nist:`2025-32743`
-  ffmpeg: Fix :cve_nist:`2025-22921`
-  go: Fix :cve_nist:`2025-22871` and CVE-2025-22873
-  iputils: Fix :cve_nist:`2025-47268`
-  libsoup-2.4: Fix :cve_nist:`2024-52532` and :cve_nist:`2025-32911`
-  libxml2: Fix :cve_nist:`2025-32414` and :cve_nist:`2025-32415`
-  openssh: Fix :cve_nist:`2025-32728`
-  perl: Fix :cve_nist:`2024-56406`
-  qemu: Ignore :cve_nist:`2023-1386`
-  ruby: Fix :cve_nist:`2025-27219`, :cve_nist:`2025-27220` and :cve_nist:`2025-27221`
-  webkitgtk: Fix :cve_nist:`2024-54551`, :cve_nist:`2025-24208`, :cve_nist:`2025-24209`,
   :cve_nist:`2025-24213`, :cve_nist:`2025-24216`, :cve_nist:`2025-24264` and :cve_nist:`2025-30427`


Fixes in Yocto-5.2.1
~~~~~~~~~~~~~~~~~~~~

-  binutils: stable 2.44 branch updates
-  bluez5: add missing tools to noinst-tools package
-  build-appliance-image: Update to walnascar head revision
-  buildtools-tarball: Make buildtools respects host CA certificates
-  buildtools-tarball: add envvars into :term:`BB_ENV_PASSTHROUGH_ADDITIONS`
-  buildtools-tarball: move setting of envvars to respective envfile
-  cdrtools-native: fix booting EFI ISO live failed
-  contributor-guide/submit-changes: encourage patch version changelogs
-  gcc: Fix LDRD register overlap in register-indexed mode
-  glibc-y2038-tests: remove glibc-y2038-tests_2.41.bb recipe
-  glibc: Add single-threaded fast path to rand()
-  glibc: stable 2.41 branch update
-  go: upgrade to 1.24.3
-  gobject-introspection: Fix wrong :term:`PN` used in MULTILIB_SCRIPTS
-  icu: set ac_cv_path_install to ensure install tool reproducibility
-  initscripts: add function log_success_msg/log_failure_msg/log_warning_msg
-  insane.bbclass: Move test for invalid PACKAGECONFIGs to do_recipe_qa
-  insane.bbclass: Report all invalid PACKAGECONFIGs for a recipe at once
-  libxml2: upgrade to 2.13.8
-  makedumpfile: upgrade to 1.7.7
-  migration-guides: add release notes for 4.0.26 and 5.0.9
-  module.bbclass: add KBUILD_EXTRA_SYMBOLS to install
-  patch.py: set commituser and commitemail for addNote
-  perl: upgrade to 5.40.2
-  perlcross: upgrade to 1.6.2
-  poky.conf: bump version for 5.2.1
-  ref-manual/release-process: update releases.svg
-  ref-manual/variables.rst: document :term:`WIC_CREATE_EXTRA_ARGS`
-  ref-manual/variables.rst: update :term:`ROOT_HOME` documentation
-  ref-manual: classes: uki: Fix git links
-  ref-manual: kernel-fitimage.bbclass does not use :term:`SPL_SIGN_KEYNAME`
-  ruby: upgrade to 3.4.3
-  sbom.rst: how to disable :term:`SPDX` generation
-  scripts/install-buildtools: Update to 5.2
-  sphinx-lint: various fixes
-  syslinux: improve isohybrid to process extra sector count for ISO 9660 image
-  test-manual/intro: remove Buildbot version used
-  tzdata/tzcode-native: upgrade to 2025b
-  webkitgtk: Use WTF_CPU_UNKNOWN when building for riscv64
-  webkitgtk: upgrade to 2.48.1


Known Issues in Yocto-5.2.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.2.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Adrian Freihofer
-  Aleksandar Nikolic
-  Alon Bar-Lev
-  Antonin Godard
-  Archana Polampalli
-  Changqing Li
-  Deepesh Varatharajan
-  Divya Chellam
-  Enrico Jörns
-  Guðni Már Gilbert
-  Haixiao Yan
-  Hongxu Jia
-  Jiaying Song
-  Khem Raj
-  Lee Chee Yang
-  Leonard Anderweit
-  Madhu Marri
-  Mikko Rapeli
-  Peter Kjellerstedt
-  Peter Marko
-  Praveen Kumar
-  Priyal Doshi
-  Steve Sakoman
-  Trevor Woerner
-  Yi Zhao
-  Yogita Urade
-  rajmohan r


Repositories / Downloads for Yocto-5.2.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`walnascar </poky/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2.1 </poky/log/?h=yocto-5.2.1>`
-  Git Revision: :yocto_git:`fd9b605507a20d850a9991316cd190c1d20dc4a6 </poky/commit/?id=fd9b605507a20d850a9991316cd190c1d20dc4a6>`
-  Release Artefact: poky-fd9b605507a20d850a9991316cd190c1d20dc4a6
-  sha: 0234a96fc28e60e0acaae2a03118ecde76810c47e8bb259f4950492bd75fc050
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.1/poky-fd9b605507a20d850a9991316cd190c1d20dc4a6.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.1/poky-fd9b605507a20d850a9991316cd190c1d20dc4a6.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`walnascar </openembedded-core/log/?h=walnascar>`
-  Tag:  :oe_git:`yocto-5.2.1 </openembedded-core/log/?h=yocto-5.2.1>`
-  Git Revision: :oe_git:`17affdaa600896282e07fb4d64cb23195673baa1 </openembedded-core/commit/?id=17affdaa600896282e07fb4d64cb23195673baa1>`
-  Release Artefact: oecore-17affdaa600896282e07fb4d64cb23195673baa1
-  sha: b6c3c15004fcd1efbaa26c9695202806402730dde8e41552a70140cff67c97c9
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.1/oecore-17affdaa600896282e07fb4d64cb23195673baa1.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.1/oecore-17affdaa600896282e07fb4d64cb23195673baa1.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`walnascar </meta-mingw/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2.1 </meta-mingw/log/?h=yocto-5.2.1>`
-  Git Revision: :yocto_git:`edce693e1b8fabd84651aa6c0888aafbcf238577 </meta-mingw/commit/?id=edce693e1b8fabd84651aa6c0888aafbcf238577>`
-  Release Artefact: meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577
-  sha: 6cfed41b54f83da91a6cf201ec1c2cd4ac284f642b1268c8fa89d2335ea2bce1
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.1/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.1/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.12 </bitbake/log/?h=2.12>`
-  Tag:  :oe_git:`yocto-5.2.1 </bitbake/log/?h=yocto-5.2.1>`
-  Git Revision: :oe_git:`5b4e20377eea8d428edf1aeb2187c18f82ca6757 </bitbake/commit/?id=5b4e20377eea8d428edf1aeb2187c18f82ca6757>`
-  Release Artefact: bitbake-5b4e20377eea8d428edf1aeb2187c18f82ca6757
-  sha: 48cff22c1e61f47adce474b636ca865e7e0b62293fc5c8829d09e7f1ac5252af
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.1/bitbake-5b4e20377eea8d428edf1aeb2187c18f82ca6757.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.1/bitbake-5b4e20377eea8d428edf1aeb2187c18f82ca6757.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`walnascar </yocto-docs/log/?h=walnascar>`
-  Tag: :yocto_git:`yocto-5.2.1 </yocto-docs/log/?h=yocto-5.2.1>`
-  Git Revision: :yocto_git:`6b7019c13054bf11fb16657a3fac85831352cea9 </yocto-docs/commit/?id=6b7019c13054bf11fb16657a3fac85831352cea9>`

