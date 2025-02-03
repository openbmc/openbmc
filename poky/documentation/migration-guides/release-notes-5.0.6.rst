Release notes for Yocto-5.0.6 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.6
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  acpica: Fix :cve_nist:`2024-24856`
-  curl: Fix :cve_nist:`2024-9681`
-  dropbear: Fix :cve_nist:`2023-48795`
-  expat: Fix :cve_nist:`2024-50602`
-  ffmpeg: Fix :cve_nist:`2023-49501`, :cve_nist:`2023-49528`, :cve_nist:`2023-50007`,
   :cve_nist:`2024-7055` and :cve_mitre:`2024-28661`
-  glib-2.0: Fix :cve_nist:`2024-52533`
-  ghostscript: Fix :cve_nist:`2024-46951`, :cve_nist:`2024-46952`, :cve_nist:`2024-46953`,
   :cve_nist:`2024-46954`, :cve_nist:`2024-46955` and :cve_nist:`2024-46956`
-  gstreamer1.0: Ignore :cve_nist:`2024-0444`
-  libpam: Fix :cve_nist:`2024-10041`
-  libsndfile: Fix :cve_nist:`2024-50612`
-  libsoup: Fix :cve_nist:`2024-52530`, :cve_nist:`2024-52531` and :cve_nist:`2024-52532`
-  ovmf: Fix :cve_nist:`2024-1298` and :cve_nist:`2024-38796`
-  python3-zipp: Fix :cve_nist:`2024-5569`
-  qemu: Fix :cve_nist:`2024-4693`, :cve_nist:`2024-6505` and :cve_nist:`2024-7730`
-  qemu: Ignore :cve_nist:`2024-6505`


Fixes in Yocto-5.0.6
~~~~~~~~~~~~~~~~~~~~

-  binutils: Add missing perl modules to :term:`RDEPENDS` for nativesdk variant
-  binutils: stable 2.42 branch update
-  bitbake: Remove custom exception backtrace formatting
-  bitbake: fetch2/git: Use quote from shlex, not pipes
-  bitbake: fetch2: use persist_data context managers
-  bitbake: fetch/wget: Increase timeout to 100s from 30s
-  bitbake: persist_data: close connection in SQLTable __exit__
-  bitbake: runqueue: Fix performance of multiconfigs with large overlap
-  bitbake: runqueue: Fix scenetask processing performance issue
-  bitbake: runqueue: Optimise setscene loop processing
-  build-appliance-image: Update to scarthgap head revision
-  builder: set :term:`CVE_PRODUCT`
-  cmake: Fix sporadic issues when determining compiler internals
-  cml1: do_diffconfig: Don't override .config with .config.orig
-  contributor-guide: Remove duplicated words
-  dev-manual: bblock: use warning block instead of attention
-  dev-manual: document how to provide confs from layer.conf
-  dnf: drop python3-iniparse from :term:`DEPENDS` and :term:`RDEPENDS`
-  do_package/sstate/sstatesig: Change timestamp clamping to hash output only
-  doc: Makefile: add support for xelatex
-  doc: Makefile: publish pdf and epub versions too
-  doc: Makefile: remove inkscape, replace by rsvg-convert
-  doc: add a download page for epub and pdf
-  doc: conf.py: add a bitbake_git extlink
-  doc: standards.md: add a section on admonitions
-  doc: sphinx-static: switchers.js.in: do not refer to URL_ROOT anymore
-  dropbear: backport fix for concurrent channel open/close
-  enchant2: fix do_fetch error
-  expat: upgrade to 2.6.4
-  gcc: backport patch to fix an issue with tzdata 2024b
-  ghostscript: upgrade to 10.04.0
-  glibc: stable 2.39 branch updates
-  groff: fix rare build race in hdtbl
-  libgcrypt: Fix building error with '-O2' in sysroot path
-  libpam: drop cracklib from :term:`DEPENDS`
-  libxml-parser-perl: fix do_fetch error
-  llvm: reduce size of -dbg package
-  lttng-ust: backport patch to fix cmake-multiple-shared-libraries build error
-  migration-guides: add release notes for 4.0.23 and 5.0.5
-  ninja: fix build with python 3.13
-  oeqa/runtime/ssh: Fix incorrect timeout fix
-  oeqa/runtime/ssh: Rework ssh timeout
-  oeqa/utils/gitarchive: Return tag name and improve exclude handling
-  package_rpm: Check if file exists before open()
-  package_rpm: restrict rpm to 4 threads
-  package_rpm: use zstd's default compression level
-  poky.conf: bump version for 5.0.6
-  pseudo: Fix envp bug and add posix_spawn wrapper
-  python3-poetry-core: drop python3-six from :term:`RDEPENDS`
-  python3-requests: upgrade to 2.32.2
-  python3-urllib3: upgrade to 2.2.2
-  qemu: upgrade to 8.2.7
-  qemurunner: Clean up serial_lock handling
-  ref-manual: classes: fix bin_package description
-  resulttool: Add --logfile-archive option to store mode
-  resulttool: Allow store to filter to specific revisions
-  resulttool: Clean up repoducible build logs
-  resulttool: Fix passthrough of --all files in store mode
-  resulttool: Handle ltp rawlogs as well as ptest
-  resulttool: Improve repo layout for oeselftest results
-  resulttool: Trim the precision of duration information
-  resulttool: Use single space indentation in json output
-  rootfs: Ensure run-postinsts is not uninstalled for read-only-rootfs-delayed-postinsts
-  rxvt-unicode.inc: disable the terminfo installation by setting TIC to :
-  sanity: check for working user namespaces
-  scripts/install-buildtools: Update to 5.0.5
-  selftest/reproducible: Clean up pathnames
-  selftest/reproducible: Drop rawlogs
-  shared-mime-info: drop itstool-native from :term:`DEPENDS`
-  strace: download release tarballs from GitHub
-  systemd-boot: drop intltool-native from :term:`DEPENDS`
-  systemd: drop intltool-native from :term:`DEPENDS`
-  systemd: upgrade to 255.13
-  sysvinit: backport patch for fixing one issue of pidof
-  tcl: skip io-13.6 test case
-  toolchain-shar-extract.sh: exit when post-relocate-setup.sh fails
-  tune-cortexa32: set tune feature as armv8a
-  tzcode-native: upgrade to 2024b
-  tzdata: upgrade to 2024b
-  uboot-sign: fix concat_dtb arguments
-  udev-extraconf: fix network.sh script did not configure hotplugged interfaces
-  webkitgtk: fix erroneous use of unsuported DEBUG_LEVELFLAG variable
-  wireless-regdb: upgrade to 2024.10.07


Known Issues in Yocto-5.0.6
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.0.6
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Aleksandar Nikolic
-  Alexander Kanavin
-  Antonin Godard
-  Archana Polampalli
-  Bin Lan
-  Changqing Li
-  Chen Qi
-  Chris Laplante
-  Clayton Casciato
-  Deepthi Hemraj
-  Divya Chellam
-  Florian Kreutzer
-  Gassner, Tobias.ext
-  Guðni Már Gilbert
-  Harish Sadineni
-  Hitendra Prajapati
-  Hongxu Jia
-  Jagadeesh Krishnanjanappa
-  Jiaying Song
-  Jinfeng Wang
-  Joshua Watt
-  Lee Chee Yang
-  Markus Volk
-  Michael Opdenacker
-  Pavel Zhukov
-  Peter Marko
-  Philip Lorenz
-  Randy MacLeod
-  Regis Dargent
-  Richard Purdie
-  Robert Yang
-  Ross Burton
-  Soumya Sambu
-  Steve Sakoman
-  Talel BELHAJSALEM
-  Trevor Gamblin
-  Vijay Anusuri
-  Wang Mingyu
-  Yogita Urade


Repositories / Downloads for Yocto-5.0.6
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.6 </poky/log/?h=yocto-5.0.6>`
-  Git Revision: :yocto_git:`2541a8171f91812a4b16e7dc4da0d77d2318a256 </poky/commit/?id=2541a8171f91812a4b16e7dc4da0d77d2318a256>`
-  Release Artefact: poky-2541a8171f91812a4b16e7dc4da0d77d2318a256
-  sha: b77157596ae75d163387a08a317397a57ab8fa6cf4725f28e344fae3f69cca4d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.6/poky-2541a8171f91812a4b16e7dc4da0d77d2318a256.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.6/poky-2541a8171f91812a4b16e7dc4da0d77d2318a256.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.6 </openembedded-core/log/?h=yocto-5.0.6>`
-  Git Revision: :oe_git:`336eec6808710f260a5336ca8ca98139a80ccb14 </openembedded-core/commit/?id=336eec6808710f260a5336ca8ca98139a80ccb14>`
-  Release Artefact: oecore-336eec6808710f260a5336ca8ca98139a80ccb14
-  sha: 38c4fa7e7e88c28361c012dd5baabe373e2ec3c8aba6194146768b146192cceb
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.6/oecore-336eec6808710f260a5336ca8ca98139a80ccb14.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.6/oecore-336eec6808710f260a5336ca8ca98139a80ccb14.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.6 </meta-mingw/log/?h=yocto-5.0.6>`
-  Git Revision: :yocto_git:`acbba477893ef87388effc4679b7f40ee49fc852 </meta-mingw/commit/?id=acbba477893ef87388effc4679b7f40ee49fc852>`
-  Release Artefact: meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852
-  sha: 3b7c2f475dad5130bace652b150367f587d44b391218b1364a8bbc430b48c54c
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.6/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.6/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.6 </bitbake/log/?h=yocto-5.0.6>`
-  Git Revision: :oe_git:`f40a3a477d5241b697bf2fb030dd804c1ff5839f </bitbake/commit/?id=f40a3a477d5241b697bf2fb030dd804c1ff5839f>`
-  Release Artefact: bitbake-f40a3a477d5241b697bf2fb030dd804c1ff5839f
-  sha: dbfc056c7408a5547f624799621ab1261a05685112e0922a88007723b1edbc87
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.6/bitbake-f40a3a477d5241b697bf2fb030dd804c1ff5839f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.6/bitbake-f40a3a477d5241b697bf2fb030dd804c1ff5839f.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.6 </yocto-docs/log/?h=yocto-5.0.6>`
-  Git Revision: :yocto_git:`TBD </yocto-docs/commit/?id=TBD>`

