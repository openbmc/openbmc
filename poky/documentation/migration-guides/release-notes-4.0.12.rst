.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.12 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.12
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve:`2023-2828` and :cve:`2023-2911`
-  cups: Fix :cve:`2023-34241`
-  curl: Added :cve:`2023-28320` Follow-up patch
-  dbus: Fix :cve:`2023-34969`
-  dmidecode: fix :cve:`2023-30630`
-  ghostscript: fix :cve:`2023-36664`
-  go: fix :cve_mitre:`2023-24531`, :cve:`2023-24536`, :cve:`2023-29400`, :cve:`2023-29402`, :cve:`2023-29404`, :cve:`2023-29405` and :cve:`2023-29406`
-  libarchive: Ignore :cve:`2023-30571`
-  libcap: Fix :cve:`2023-2602` and :cve:`2023-2603`
-  libjpeg-turbo: Fix :cve:`2023-2804`
-  libpcre2: Fix :cve:`2022-41409`
-  libtiff: fix :cve:`2023-26965`
-  libwebp: Fix :cve:`2023-1999`
-  libx11: Fix :cve:`2023-3138`
-  libxpm: Fix :cve:`2022-44617`
-  ninja: Ignore :cve:`2021-4336`
-  openssh: Fix :cve:`2023-38408`
-  openssl: Fix :cve:`2023-2975`, :cve:`2023-3446` and :cve:`2023-3817`
-  perl: Fix :cve:`2023-31486`
-  python3: Ignore :cve:`2023-36632`
-  qemu: Fix :cve:`2023-0330`, :cve_mitre:`2023-2861`, :cve_mitre:`2023-3255` and :cve_mitre:`2023-3301`
-  sqlite3: Fix :cve:`2023-36191`
-  tiff: Fix :cve:`2023-0795`, :cve:`2023-0796`, :cve:`2023-0797`, :cve:`2023-0798`, :cve:`2023-0799`, :cve:`2023-25433`, :cve:`2023-25434` and :cve:`2023-25435`
-  vim: :cve:`2023-2609` and :cve:`2023-2610`


Fixes in Yocto-4.0.12
~~~~~~~~~~~~~~~~~~~~~

-  babeltrace2: Always use BFD linker when building tests with ld-is-lld distro feature
-  babeltrace2: upgrade to 2.0.5
-  bitbake.conf: add unzstd in :term:`HOSTTOOLS`
-  bitbake: bitbake-layers: initialize tinfoil before registering command line arguments
-  bitbake: runqueue: Fix deferred task/multiconfig race issue
-  blktrace: ask for python3 specifically
-  build-appliance-image: Update to kirkstone head revision
-  cmake: Fix CMAKE_SYSTEM_PROCESSOR setting for SDK
-  connman: fix warning by specifying runstatedir at configure time
-  cpio: Replace fix wrong CRC with ASCII CRC for large files with upstream backport
-  cve-update-nvd2-native: actually use API keys
-  cve-update-nvd2-native: always pass str for json.loads()
-  cve-update-nvd2-native: fix cvssV3 metrics
-  cve-update-nvd2-native: handle all configuration nodes, not just first
-  cve-update-nvd2-native: increase retry count
-  cve-update-nvd2-native: log a little more
-  cve-update-nvd2-native: retry all errors and sleep between retries
-  cve-update-nvd2-native: use exact times, don't truncate
-  dbus: upgrade to 1.14.8
-  devtool: Fix the wrong variable in srcuri_entry
-  diffutils: upgrade to 3.10
-  docs: ref-manual: terms: fix typos in :term:`SPDX` term
-  fribidi: upgrade to 1.0.13
-  gcc: upgrade to v11.4
-  gcc-testsuite: Fix ppc cpu specification
-  gcc: don't pass --enable-standard-branch-protection
-  gcc: fix runpath errors in cc1 binary
-  grub: submit determinism.patch upstream
-  image_types: Fix reproducible builds for initramfs and UKI img
-  kernel: add missing path to search for debug files
-  kmod: remove unused ptest.patch
-  layer.conf: Add missing dependency exclusion
-  libassuan: upgrade to 2.5.6
-  libksba: upgrade to 1.6.4
-  libpng: Add ptest for libpng
-  libxcrypt: fix build with perl-5.38 and use master branch
-  libxcrypt: fix hard-coded ".so" extension
-  libxpm: upgrade to 3.5.16
-  linux-firmware: upgrade to 20230515
-  linux-yocto/5.10: cfg: fix DECNET configuration warning
-  linux-yocto/5.10: update to v5.10.185
-  linux-yocto/5.15: cfg: fix DECNET configuration warning
-  linux-yocto/5.15: update to v5.15.120
-  logrotate: Do not create logrotate.status file
-  lttng-ust: upgrade to 2.13.6
-  machine/arch-arm64: add -mbranch-protection=standard
-  maintainers.inc: correct Carlos Rafael Giani's email address
-  maintainers.inc: correct unassigned entries
-  maintainers.inc: unassign Adrian Bunk from wireless-regdb
-  maintainers.inc: unassign Alistair Francis from opensbi
-  maintainers.inc: unassign Andreas Müller from itstool entry
-  maintainers.inc: unassign Pascal Bach from cmake entry
-  maintainers.inc: unassign Ricardo Neri from ovmf
-  maintainers.inc: unassign Richard Weinberger from erofs-utils entry
-  mdadm: fix 07revert-inplace ptest
-  mdadm: fix segfaults when running ptests
-  mdadm: fix util-linux ptest dependency
-  mdadm: skip running known broken ptests
-  meson.bbclass: Point to llvm-config from native sysroot
-  meta: lib: oe: npm_registry: Add more safe caracters
-  migration-guides: add release notes for 4.0.11
-  minicom: remove unused patch files
-  mobile-broadband-provider-info: upgrade to 20230416
-  oe-depends-dot: Handle new format for task-depends.dot
-  oeqa/runtime/cases/rpm: fix wait_for_no_process_for_user failure case
-  oeqa/selftest/bbtests: add non-existent prefile/postfile tests
-  oeqa/selftest/devtool: add unit test for "devtool add -b"
-  openssl: Upgrade to 3.0.10
-  openssl: add PERLEXTERNAL path to test its existence
-  openssl: use a glob on the PERLEXTERNAL to track updates on the path
-  package.bbclass: moving field data process before variable process in process_pkgconfig
-  pm-utils: fix multilib conflictions
-  poky.conf: bump version for 4.0.12
-  psmisc: Set :term:`ALTERNATIVE` for pstree to resolve conflict with busybox
-  pybootchartgui: show elapsed time for each task
-  python3: fix missing comma in get_module_deps3.py
-  python3: upgrade to 3.10.12
-  recipetool: Fix inherit in created -native* recipes
-  ref-manual: add LTS and Mixin terms
-  ref-manual: document image-specific variant of :term:`INCOMPATIBLE_LICENSE`
-  ref-manual: release-process: update for LTS releases
-  rust-llvm: backport a fix for build with gcc-13
-  scripts/runqemu: allocate unfsd ports in a way that doesn't race or clash with unrelated processes
-  scripts/runqemu: split lock dir creation into a reusable function
-  sdk.py: error out when moving file fails
-  sdk.py: fix moving dnf contents
-  selftest reproducible.py: support different build targets
-  selftest/license: Exclude from world
-  selftest/reproducible: Allow chose the package manager
-  serf: upgrade to 1.3.10
-  strace: Disable failing test
-  strace: Merge two similar patches
-  strace: Update patches/tests with upstream fixes
-  sysfsutils: fetch a supported fork from github
-  systemd-systemctl: fix errors in instance name expansion
-  systemd: Backport nspawn: make sure host root can write to the uidmapped mounts we prepare for the container payload
-  tzdata: upgrade to 2023c
-  uboot-extlinux-config.bbclass: fix old override syntax in comment
-  unzip: fix configure check for cross compilation
-  useradd-staticids.bbclass: improve error message
-  util-linux: add alternative links for ipcs,ipcrm
-  v86d: Improve kernel dependency
-  vim: upgrade to 9.0.1592
-  wget: upgrade to 1.21.4
-  wic: Add dependencies for erofs-utils
-  wireless-regdb: upgrade to 2023.05.03
-  xdpyinfo: upgrade to 1.3.4
-  zip: fix configure check by using _Static_assert


Known Issues in Yocto-4.0.12
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.12
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alberto Planas
-  Alexander Kanavin
-  Alexander Sverdlin
-  Andrej Valek
-  Archana Polampalli
-  BELOUARGA Mohamed
-  Benjamin Bouvier
-  Bruce Ashfield
-  Charlie Wu
-  Chen Qi
-  Etienne Cordonnier
-  Fabien Mahot
-  Frieder Paape
-  Frieder Schrempf
-  Heiko Thole
-  Hitendra Prajapati
-  Jermain Horsman
-  Jose Quaresma
-  Kai Kang
-  Khem Raj
-  Lee Chee Yang
-  Marc Ferland
-  Marek Vasut
-  Martin Jansa
-  Mauro Queiros
-  Michael Opdenacker
-  Mikko Rapeli
-  Nikhil R
-  Ovidiu Panait
-  Peter Marko
-  Poonam Jadhav
-  Quentin Schulz
-  Richard Purdie
-  Ross Burton
-  Rusty Howell
-  Sakib Sajal
-  Soumya Sambu
-  Steve Sakoman
-  Sundeep KOKKONDA
-  Tim Orling
-  Tom Hochstein
-  Trevor Gamblin
-  Vijay Anusuri
-  Vivek Kumbhar
-  Wang Mingyu
-  Xiangyu Chen
-  Yoann Congal
-  Yogita Urade
-  Yuta Hayama


Repositories / Downloads for Yocto-4.0.12
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.12 </poky/log/?h=yocto-4.0.12>`
-  Git Revision: :yocto_git:`d6b8790370500b99ca11f0d8a05c39b661ab2ba6 </poky/commit/?id=d6b8790370500b99ca11f0d8a05c39b661ab2ba6>`
-  Release Artefact: poky-d6b8790370500b99ca11f0d8a05c39b661ab2ba6
-  sha: 35f0390e0c5a12f403ed471c0b1254c13cbb9d7c7b46e5a3538e63e36c1ac280
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.12/poky-d6b8790370500b99ca11f0d8a05c39b661ab2ba6.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.12/poky-d6b8790370500b99ca11f0d8a05c39b661ab2ba6.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.12 </openembedded-core/log/?h=yocto-4.0.12>`
-  Git Revision: :oe_git:`e1a604db8d2cf8782038b4016cc2e2052467333b </openembedded-core/commit/?id=e1a604db8d2cf8782038b4016cc2e2052467333b>`
-  Release Artefact: oecore-e1a604db8d2cf8782038b4016cc2e2052467333b
-  sha: 8b302eb3f3ffe5643f88bc6e4ae8f9a5cda63544d67e04637ecc4197e9750a1d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.12/oecore-e1a604db8d2cf8782038b4016cc2e2052467333b.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.12/oecore-e1a604db8d2cf8782038b4016cc2e2052467333b.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.12 </meta-mingw/log/?h=yocto-4.0.12>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.12/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.12/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.12 </meta-gplv2/log/?h=yocto-4.0.12>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.12/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.12/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.12 </bitbake/log/?h=yocto-4.0.12>`
-  Git Revision: :oe_git:`41b6684489d0261753344956042be2cc4adb0159 </bitbake/commit/?id=41b6684489d0261753344956042be2cc4adb0159>`
-  Release Artefact: bitbake-41b6684489d0261753344956042be2cc4adb0159
-  sha: efa2b1c4d0be115ed3960750d1e4ed958771b2db6d7baee2d13ad386589376e8
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.12/bitbake-41b6684489d0261753344956042be2cc4adb0159.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.12/bitbake-41b6684489d0261753344956042be2cc4adb0159.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.12 </yocto-docs/log/?h=yocto-4.0.12>`
-  Git Revision: :yocto_git:`4dfef81ac6164764c6541e39a9fef81d49227096 </yocto-docs/commit/?id=4dfef81ac6164764c6541e39a9fef81d49227096>`

