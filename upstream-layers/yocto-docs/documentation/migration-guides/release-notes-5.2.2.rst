Release notes for Yocto-5.2.2 (Walnascar)
-----------------------------------------

Security Fixes in Yocto-5.2.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve_nist:`2025-40775`
-  binutils: Fix :cve_nist:`2025-1153`, :cve_nist:`2025-1178`, :cve_nist:`2025-1180`,
   :cve_nist:`2025-1181`, :cve_nist:`2025-1182`, :cve_nist:`2025-3198` and :cve_nist:`2025-5244`
-  binutils: Ignore :cve_nist:`2025-1153` (fixed in current version)
-  epiphany: Fix CVE-2025-3839
-  go: Fix :cve_nist:`2025-0913`, :cve_nist:`2025-4673` and :cve_nist:`2025-22874`
-  go: Ignore :cve_nist:`2024-3566`
-  kea: Fix :cve_nist:`2025-32801`, :cve_nist:`2025-32802` and :cve_nist:`2025-32803`
-  libarchive: Fix :cve_nist:`2025-5914`
-  libsoup-2.4: Fix :cve_nist:`2024-52530`, :cve_nist:`2024-52531`, :cve_nist:`2025-2784`,
   :cve_nist:`2025-4476`, :cve_nist:`2025-4948`, :cve_nist:`2025-4969`, :cve_nist:`2025-32050`,
   :cve_nist:`2025-32052`, :cve_nist:`2025-32053`, :cve_nist:`2025-32906`, :cve_nist:`2025-32907`,
   :cve_nist:`2025-32909`, :cve_nist:`2025-32910`, :cve_nist:`2025-32912`, :cve_nist:`2025-32913`,
   :cve_nist:`2025-32914`, :cve_nist:`2025-46420` and :cve_nist:`2025-46421`
-  libsoup: Fix :cve_nist:`2025-4476`, :cve_nist:`2025-4948`, :cve_nist:`2025-4969`,
   :cve_nist:`2025-32907`, :cve_nist:`2025-32908` and :cve_nist:`2025-32914`
-  linux-yocto: Fix :cve_nist:`2023-3079`, :cve_nist:`2023-52904`, :cve_nist:`2023-52979`,
   :cve_nist:`2025-22102`, :cve_nist:`2025-37800`, :cve_nist:`2025-37801`, :cve_nist:`2025-37802`,
   :cve_nist:`2025-37805`, :cve_nist:`2025-37821`, :cve_nist:`2025-37838`, :cve_nist:`2025-37890`,
   :cve_nist:`2025-37891`, :cve_nist:`2025-37894`, :cve_nist:`2025-37895`, :cve_nist:`2025-37897`,
   :cve_nist:`2025-37899`, :cve_nist:`2025-37900`, :cve_nist:`2025-37901`, :cve_nist:`2025-37903`,
   :cve_nist:`2025-37905`, :cve_nist:`2025-37907`, :cve_nist:`2025-37908`, :cve_nist:`2025-37909`,
   :cve_nist:`2025-37910`, :cve_nist:`2025-37911`, :cve_nist:`2025-37912`, :cve_nist:`2025-37913`,
   :cve_nist:`2025-37914`, :cve_nist:`2025-37915`, :cve_nist:`2025-37916`, :cve_nist:`2025-37917`,
   :cve_nist:`2025-37918`, :cve_nist:`2025-37919`, :cve_nist:`2025-37920`, :cve_nist:`2025-37921`,
   :cve_nist:`2025-37922`, :cve_nist:`2025-37923`, :cve_nist:`2025-37924`, :cve_nist:`2025-37926`,
   :cve_nist:`2025-37927`, :cve_nist:`2025-37928`, :cve_nist:`2025-37929`, :cve_nist:`2025-37930`,
   :cve_nist:`2025-37931`, :cve_nist:`2025-37932`, :cve_nist:`2025-37933`, :cve_nist:`2025-37934`,
   :cve_nist:`2025-37935`, :cve_nist:`2025-37936`, :cve_nist:`2025-37946`, :cve_nist:`2025-37947`,
   :cve_nist:`2025-37948`, :cve_nist:`2025-37949`, :cve_nist:`2025-37951`, :cve_nist:`2025-37952`,
   :cve_nist:`2025-37953`, :cve_nist:`2025-37954`, :cve_nist:`2025-37955`, :cve_nist:`2025-37956`,
   :cve_nist:`2025-37957`, :cve_nist:`2025-37958`, :cve_nist:`2025-37959`, :cve_nist:`2025-37960`,
   :cve_nist:`2025-37961`, :cve_nist:`2025-37962`, :cve_nist:`2025-37963`, :cve_nist:`2025-37964`,
   :cve_nist:`2025-37965`, :cve_nist:`2025-37967`, :cve_nist:`2025-37968`, :cve_nist:`2025-37969`,
   :cve_nist:`2025-37970`, :cve_nist:`2025-37971`, :cve_nist:`2025-37972`, :cve_nist:`2025-37973`,
   :cve_nist:`2025-37974`, :cve_nist:`2025-37990`, :cve_nist:`2025-37991`, :cve_nist:`2025-37992`,
   :cve_nist:`2025-37993`, :cve_nist:`2025-37994`, :cve_nist:`2025-37995`, :cve_nist:`2025-37997`,
   :cve_nist:`2025-37998` and :cve_nist:`2025-37999`
-  linux-yocto: Ignore :cve_nist:`2023-3079` and :cve_nist:`2025-37996`
-  net-tools: Fix :cve_nist:`2025-46836`
-  ofono: Fix :cve_nist:`2024-7537`
-  python3-setuptools: Fix :cve_nist:`2025-47273`
-  python3-urllib3: Fix :cve_nist:`2025-50181` and :cve_nist:`2025-50182`
-  sqlite3: Fix :cve_nist:`2025-3277` and :cve_nist:`2025-29088`
-  sqlite3: mark :cve_nist:`2025-29087` as patched
-  systemd: Fix :cve_nist:`2025-4598`
-  xz: Fix :cve_nist:`2025-31115`


Fixes in Yocto-5.2.2
~~~~~~~~~~~~~~~~~~~~

-  bind: upgrade to 9.20.9
-  bitbake: toaster/tests/buildtest: Switch to new CDN
-  brief-yoctoprojectqs/index.rst: replace removed macro
-  brief-yoctoprojectqs/ref-manual: Switch to new CDN
-  bsp guide: update kernel version example to 6.12
-  bsp-guide: update all of section 1.8.2 to reflect current beaglebone conf file
-  bsp-guide: update lonely "4.12" kernel reference to "6.12"
-  build-appliance-image: Update to walnascar head revision
-  cmake: Correctly handle cost data of tests with arbitrary chars in name
-  conf.py: tweak SearchEnglish to be hyphen-friendly
-  cve-exclusion_6.12.inc: Update using current cvelistV5
-  cve-exclusions: correct cve status for 5 entries
-  docs: Clean up explanation of minimum required version numbers
-  docs: README: specify how to contribute instead of pointing at another file
-  docs: conf.py: silence SyntaxWarning on js_splitter_code
-  docs: sphinx-lint: superfluous backtick in front of role
-  docs: sphinx-lint: unbalanced inline literal markup
-  epiphany: upgrade to 48.3
-  gcc: Upgrade to GCC 14.3
-  gcc: fix incorrect preprocessor line numbers in large files
-  genericarm64.conf: increase :term:`INITRAMFS_MAXSIZE`
-  ghostscript: upgrade to 10.05.1
-  glibc: stable 2.41 branch updates
-  go: upgrade to 1.24.4
-  kea: upgrade to 2.6.3
-  libarchive: upgrade to 3.7.9
-  libmatchbox: upgrade to 1.14
-  libsoup: upgrade to 3.6.5
-  linux-yocto/6.12: bsp/genericarm64: modular configuration updates
-  linux-yocto/6.12: libbpf: silence maybe-uninitialized warning from clang
-  linux-yocto/6.12: update to v6.12.31
-  linux-yoto/6.12: bsp/arm: fix CONFIG_CRYPTO_LIB_CHACHA
-  linux/cve-exclusion: Execute the script after changing to the new data source
-  linux/cve-exclusion: correct fixed-version calculation
-  linux/cve-exclusion: do not shift first_affected
-  linux/cve-exclusion: update exclusions after script fixes
-  linux/cve-exclusion: update with latest cvelistV5
-  linux/generate-cve-exclusions: show the name and version of the data source
-  linux/generate-cve-exclusions: use data from CVEProject
-  linux: cve-exclusions: Amend terminology
-  linux: cve-exclusions: Fix false negatives
-  local.conf.sample: Switch to new CDN
-  migration-guides: add release notes for 4.0.27, 5.0.10, 5.2.1
-  nfs-utils: don't use signals to shut down nfs server.
-  oeqa/sstatetests: Fix :term:`NATIVELSBSTRING` handling
-  oeqa/sstatetests: Improve/fix sstate creation tests
-  overview-manual: small number of pedantic cleanups
-  package_rpm.bbclass: Remove empty build directory
-  poky.conf: bump version for 5.2.2
-  python3-pygobject: :term:`RDEPENDS` on gobject-introspection
-  python3-requests: upgrade to 2.32.4
-  python3: backport the full fix for importlib scanning invalid distributions
-  python3: drop old nis module dependencies
-  python3: remove obsolete deletion of non-deterministic .pyc files
-  python3: upgrade to 3.13.4
-  ref-manual/variables.rst: document :term:`IMAGE_ROOTFS_MAXSIZE` :term:`INHIBIT_DEFAULT_RUST_DEPS`
   :term:`INHIBIT_UPDATERCD_BBCLASS` :term:`INITRAMFS_MAXSIZE` :term:`KERNEL_SPLIT_MODULES`
   :term:`SSTATE_SKIP_CREATION`
-  ref-manual: clarify :term:`KCONFIG_MODE` default behaviour
-  ref-manual: classes: nativesdk: move note to appropriate section
-  ref-manual: classes: reword to clarify that native/nativesdk options are exclusive
-  scripts/install-buildtools: Update to 5.2.1
-  sstate: apply proper umask when fetching from SSTATE_MIRROR
-  sstatetests: Switch to new CDN
-  systemd.bbclass: generate preset for templates
-  systemd: upgrade to 257.6
-  tcf-agent: correct the :term:`SRC_URI`
-  testimage: get real os-release file
-  tune-cortexr52: Remove aarch64 for ARM Cortex-R52
-  util-linux: fix agetty segfault issue
-  xwayland: Add missing libtirpc dependency


Known Issues in Yocto-5.2.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.2.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Alper Ak
-  Antonin Godard
-  Archana Polampalli
-  Bruce Ashfield
-  Carlos Sánchez de La Lama
-  Changqing Li
-  Christos Gavros
-  Colin Pinnell McAllister
-  Daniel Turull
-  Deepesh Varatharajan
-  Dixit Parmar
-  Enrico Jörns
-  Etienne Cordonnier
-  Guocai He
-  Guðni Már Gilbert
-  Gyorgy Sarvari
-  Harish Sadineni
-  Jiaying Song
-  Lee Chee Yang
-  Mathieu Dubois-Briand
-  Mikko Rapeli
-  Moritz Haase
-  NeilBrown
-  Niko Mauno
-  Patrick Williams
-  Peter Marko
-  Praveen Kumar
-  Quentin Schulz
-  Randy MacLeod
-  Rasmus Villemoes
-  Richard Purdie
-  Robert P. J. Day
-  Robert Yang
-  Ross Burton
-  Sandeep Gundlupet Raju
-  Steve Sakoman
-  Trevor Gamblin
-  Trevor Woerner
-  Wang Mingyu
-  Yash Shinde
-  Yi Zhao
-  Yogita Urade
-  Yongxin Liu

Repositories / Downloads for Yocto-5.2.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`walnascar </poky/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2.2 </poky/log/?h=yocto-5.2.2>`
-  Git Revision: :yocto_git:`41038342a471b4a8884548568ad147a1704253a3 </poky/commit/?id=41038342a471b4a8884548568ad147a1704253a3>`
-  Release Artefact: poky-41038342a471b4a8884548568ad147a1704253a3
-  sha: 4b1e9c80949e5c5ab5ffeb4fa3dadb43b74b813fc9d132caabf1fc8c38bd8f5e
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.2/poky-41038342a471b4a8884548568ad147a1704253a3.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.2/poky-41038342a471b4a8884548568ad147a1704253a3.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`walnascar </openembedded-core/log/?h=walnascar>`
-  Tag:  :oe_git:`yocto-5.2.2 </openembedded-core/log/?h=yocto-5.2.2>`
-  Git Revision: :oe_git:`c855be07828c9cff3aa7ddfa04eb0c4df28658e4 </openembedded-core/commit/?id=c855be07828c9cff3aa7ddfa04eb0c4df28658e4>`
-  Release Artefact: oecore-c855be07828c9cff3aa7ddfa04eb0c4df28658e4
-  sha: c510b69b984be7ad8045236a3dde9bc4f5833bc9f3045dc04d6442a9453165f4
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.2/oecore-c855be07828c9cff3aa7ddfa04eb0c4df28658e4.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.2/oecore-c855be07828c9cff3aa7ddfa04eb0c4df28658e4.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`walnascar </meta-mingw/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2.2 </meta-mingw/log/?h=yocto-5.2.2>`
-  Git Revision: :yocto_git:`edce693e1b8fabd84651aa6c0888aafbcf238577 </meta-mingw/commit/?id=edce693e1b8fabd84651aa6c0888aafbcf238577>`
-  Release Artefact: meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577
-  sha: 6cfed41b54f83da91a6cf201ec1c2cd4ac284f642b1268c8fa89d2335ea2bce1
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.2/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.2/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.12 </bitbake/log/?h=2.12>`
-  Tag:  :oe_git:`yocto-5.2.2 </bitbake/log/?h=yocto-5.2.2>`
-  Git Revision: :oe_git:`74c28e14a9b5e2ff908a03f93c189efa6f56b0ca </bitbake/commit/?id=74c28e14a9b5e2ff908a03f93c189efa6f56b0ca>`
-  Release Artefact: bitbake-74c28e14a9b5e2ff908a03f93c189efa6f56b0ca
-  sha: 1d417990d922289152af6274d461d7809d06c290d57e5373fd46bb0112e6b812
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2.2/bitbake-74c28e14a9b5e2ff908a03f93c189efa6f56b0ca.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2.2/bitbake-74c28e14a9b5e2ff908a03f93c189efa6f56b0ca.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`walnascar </meta-yocto/log/?h=walnascar>`
-  Tag: :yocto_git:`yocto-5.2.2 </meta-yocto/log/?h=yocto-5.2.2>`
-  Git Revision: :yocto_git:`5754fb5efb54cf06f96012a88619baba0995b0fc </meta-yocto/commit/?id=5754fb5efb54cf06f96012a88619baba0995b0fc>`

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`walnascar </yocto-docs/log/?h=walnascar>`
-  Tag: :yocto_git:`yocto-5.2.2 </yocto-docs/log/?h=yocto-5.2.2>`
-  Git Revision: :yocto_git:`85f8e5c799ef38c6dcca615d7cc6baff325df259 </yocto-docs/commit/?id=85f8e5c799ef38c6dcca615d7cc6baff325df259>`

