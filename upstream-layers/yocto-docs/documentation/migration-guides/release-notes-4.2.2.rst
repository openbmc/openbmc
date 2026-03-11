.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.2.2 (Mickledore)
------------------------------------------

Security Fixes in Yocto-4.2.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2023-1972`
-  cups: Fix :cve_nist:`2023-32324`
-  curl: Fix :cve_nist:`2023-28319`, :cve_nist:`2023-28320`, :cve_nist:`2023-28321` and :cve_nist:`2023-28322`
-  dbus: Fix :cve_nist:`2023-34969`
-  git: Fix :cve_nist:`2023-25652` and :cve_nist:`2023-29007`
-  git: Ignore :cve_nist:`2023-25815`
-  libwebp: Fix :cve_nist:`2023-1999`
-  libxml2: Fix :cve_nist:`2023-28484` and :cve_nist:`2023-29469`
-  libxpm: Fix :cve_nist:`2022-44617`
-  ninja: Ignore :cve_nist:`2021-4336`
-  openssl: Fix :cve_nist:`2023-0464`, :cve_nist:`2023-0465`, :cve_nist:`2023-0466`, :cve_nist:`2023-1255` and :cve_nist:`2023-2650`
-  perl: Fix :cve_nist:`2023-31484` and :cve_nist:`2023-31486`
-  sysstat: Fix :cve_nist:`2023-33204`
-  tiff: Fix :cve_mitre:`2023-25434`, :cve_nist:`2023-26965` and :cve_nist:`2023-2731`
-  vim: Fix :cve_nist:`2023-2426`


Fixes in Yocto-4.2.2
~~~~~~~~~~~~~~~~~~~~

-  apr: Upgrade to 1.7.4
-  avahi: fix D-Bus introspection
-  babeltrace2: Always use BFD linker when building tests with ld-is-lld distro feature
-  babeltrace2: Upgrade to 2.0.5
-  baremetal-helloworld: Update :term:`SRCREV` to fix entry addresses for ARM architectures
-  bind: Upgrade to 9.18.15
-  binutils: move packaging of gprofng static lib into common .inc
-  binutils: package static libs from gprofng
-  binutils: stable 2.40 branch updates (7343182dd1)
-  bitbake.conf: add unzstd in :term:`HOSTTOOLS`
-  bitbake: runqueue: Fix deferred task/multiconfig race issue
-  bno_plot.py, btt_plot.py: Ask for python3 specifically
-  build-appliance-image: Update to mickledore head revision
-  busybox: Upgrade to 1.36.1
-  cmake.bbclass: do not search host paths for find_program()
-  conf: add nice level to the hash config ignred variables
-  connman: fix warning by specifying runstatedir at configure time
-  cpio: Run ptests under ptest user
-  dbus: Upgrade to 1.14.8
-  devtool: Fix the wrong variable in srcuri_entry
-  dnf: only write the log lock to root for native dnf
-  docs: bsp-guide: bsp: fix typo
-  dpkg: Upgrade to v1.21.22
-  e2fsprogs: Fix error SRCDIR when using usrmerge :term:`DISTRO_FEATURES`
-  e2fsprogs: fix ptest bug for second running
-  ell: Upgrade to 0.57
-  expect: Add ptest support
-  fribidi: Upgrade to 1.0.13
-  gawk: Upgrade to 5.2.2
-  gcc : upgrade to v12.3
-  gdb: fix crashes when debugging threads with Arm Pointer Authentication enabled
-  gdb: Upgrade to 13.2
-  git: Upgrade to 2.39.3
-  glib-networking: use correct error code in ptest
-  glibc: Pass linker choice via compiler flags
-  glibc: stable 2.37 branch updates.
-  gnupg: Upgrade to 2.4.2
-  go.bbclass: don't use test to check output from ls
-  go: Upgrade to 1.20.5
-  go: Use -no-pie to build target cgo
-  gobject-introspection: remove obsolete :term:`DEPENDS`
-  grub: submit determinism.patch upstream
-  gstreamer1.0: Upgrade to 1.22.3
-  gtk4: Upgrade to 4.10.4
-  image-live.bbclass: respect :term:`IMAGE_MACHINE_SUFFIX`
-  image_types: Fix reproducible builds for initramfs and UKI img
-  inetutils: remove unused patch files
-  ipk: Revert Decode byte data to string in manifest handling
-  iso-codes: Upgrade to 4.15.0
-  kernel: don't force PAHOLE=false
-  kmod: remove unused ptest.patch
-  kmscube: Correct :term:`DEPENDS` to avoid overwrite
-  layer.conf: Add missing dependency exclusion
-  lib/terminal.py: Add urxvt terminal
-  libbsd: Add correct license for all packages
-  libdnf: Upgrade to 0.70.1
-  libgcrypt: Upgrade to 1.10.2
-  libgloss: remove unused patch file
-  libmicrohttpd: Upgrade to 0.9.77
-  libmodule-build-perl: Upgrade to 0.4234
-  libx11: remove unused patch and :term:`FILESEXTRAPATHS`
-  libx11: Upgrade to 1.8.5
-  libxfixes: Upgrade to v6.0.1
-  libxft: Upgrade to 2.3.8
-  libxi: Upgrade to v1.8.1
-  libxml2: Do not use lld linker when building with tests on rv64
-  libxml2: Upgrade to 2.10.4
-  libxpm: Upgrade to 3.5.16
-  linux-firmware: Upgrade to 20230515
-  linux-yocto/5.15: cfg: fix DECNET configuration warning
-  linux-yocto/5.15: Upgrade to v5.15.118
-  linux-yocto/6.1: fix intermittent x86 boot hangs
-  linux-yocto/6.1: Upgrade to v6.1.35
-  linux-yocto: move build / debug dependencies to .inc
-  logrotate: Do not create logrotate.status file
-  maintainers.inc: correct Carlos Rafael Giani's email address
-  maintainers.inc: correct unassigned entries
-  maintainers.inc: unassign Adrian Bunk from wireless-regdb
-  maintainers.inc: unassign Alistair Francis from opensbi
-  maintainers.inc: unassign Andreas Müller from itstool entry
-  maintainers.inc: unassign Chase Qi from libc-test
-  maintainers.inc: unassign Oleksandr Kravchuk from python3 and all other items
-  maintainers.inc: unassign Pascal Bach from cmake entry
-  maintainers.inc: unassign Ricardo Neri from ovmf
-  maintainers.inc: update version for gcc-source
-  maintainers.inc: unassign Richard Weinberger from erofs-utils entry
-  meta: depend on autoconf-archive-native, not autoconf-archive
-  meta: lib: oe: npm_registry: Add more safe caracters
-  migration-guides: add release notes for 4.2.1
-  minicom: remove unused patch files
-  mobile-broadband-provider-info: Upgrade to 20230416
-  musl: Correct :term:`SRC_URI`
-  oeqa/selftest/bbtests: add non-existent prefile/postfile tests
-  oeqa/selftest/cases/devtool.py: skip all tests require folder a git repo
-  oeqa: adding selftest-hello and use it to speed up tests
-  openssh: Remove BSD-4-clause contents completely from codebase
-  openssl: fix building on riscv32
-  openssl: Upgrade to 3.1.1
-  overview-manual: concepts.rst: Fix a typo
-  parted: Add missing libuuid to linker cmdline for libparted-fs-resize.so
-  perf: Make built-in libtraceevent plugins cohabit with external libtraceevent
-  piglit: Add missing glslang dependencies
-  piglit: Fix c++11-narrowing warnings in tests
-  pkgconf: Upgrade to 1.9.5
-  pm-utils: fix multilib conflictions
-  poky.conf: bump version for 4.2.2 release
-  populate_sdk_base.bbclass: respect :term:`MLPREFIX` for ptest-pkgs's ptest-runner
-  profile-manual: fix blktrace remote usage instructions
-  psmisc: Set :term:`ALTERNATIVE` for pstree to resolve conflict with busybox
-  ptest-runner: Ensure data writes don't race
-  ptest-runner: Pull in "runner: Remove threads and mutexes" fix
-  ptest-runner: Pull in sync fix to improve log warnings
-  python3-bcrypt: Use BFD linker when building tests
-  python3-numpy: remove NPY_INLINE, use inline instead
-  qemu: a pending patch was submitted and accepted upstream
-  qemu: remove unused qemu-7.0.0-glibc-2.36.patch
-  qemurunner.py: fix error message about qmp
-  qemurunner: avoid leaking server_socket
-  ref-manual: add clarification for :term:`SRCREV`
-  ref-manual: classes.rst: fix typo
-  rootfs-postcommands.bbclass: add post func remove_unused_dnf_log_lock
-  rpcsvc-proto: Upgrade to 1.4.4
-  rpm: drop unused 0001-Rip-out-partial-support-for-unused-MD2-and-RIPEMD160.patch
-  rpm: Upgrade to 4.18.1
-  rpm: write macros under libdir
-  runqemu-gen-tapdevs: Refactoring
-  runqemu-ifupdown/get-tapdevs: Add support for ip tuntap
-  scripts/runqemu: allocate unfsd ports in a way that doesn't race or clash with unrelated processes
-  scripts/runqemu: split lock dir creation into a reusable function
-  scripts: fix buildstats diff/summary hard bound to host python3
-  sdk.py: error out when moving file fails
-  sdk.py: fix moving dnf contents
-  selftest/license: Exclude from world
-  selftest/reproducible: Allow native/cross reuse in test
-  serf: Upgrade to 1.3.10
-  staging.bbclass: do not add extend_recipe_sysroot to prefuncs of prepare_recipe_sysroot
-  strace: Disable failing test
-  strace: Merge two similar patches
-  strace: Update patches/tests with upstream fixes
-  sysfsutils: fetch a supported fork from github
-  systemd-systemctl: support instance expansion in WantedBy
-  systemd: Drop a backport
-  tiff: Remove unused patch from tiff
-  uninative: Upgrade to 3.10 to support gcc 13
-  uninative: Upgrade to 4.0 to include latest gcc 13.1.1
-  unzip: fix configure check for cross compilation
-  unzip: remove hardcoded LARGE_FILE_SUPPORT
-  useradd-example: package typo correction
-  useradd-staticids.bbclass: improve error message
-  v86d: Improve kernel dependency
-  vim: Upgrade to 9.0.1527
-  weston-init: add profile to point users to global socket
-  weston-init: add the weston user to the wayland group
-  weston-init: add weston user to the render group
-  weston-init: fix the mixed indentation
-  weston-init: guard against systemd configs
-  weston-init: make sure the render group exists
-  wget: Upgrade to 1.21.4
-  wireless-regdb: Upgrade to 2023.05.03
-  xdpyinfo: Upgrade to 1.3.4
-  xf86-video-intel: Use the HTTPS protocol to fetch the Git repositories
-  xinput: upgrade to v1.6.4
-  xwininfo: upgrade to v1.1.6
-  xz: Upgrade to 5.4.3
-  yocto-bsps: update to v5.15.106
-  zip: fix configure check by using _Static_assert
-  zip: remove unnecessary LARGE_FILE_SUPPORT CLFAGS


Known Issues in Yocto-4.2.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.2.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alberto Planas
-  Alejandro Hernandez Samaniego
-  Alexander Kanavin
-  Andrej Valek
-  Andrew Jeffery
-  Anuj Mittal
-  Archana Polampalli
-  BELOUARGA Mohamed
-  Bruce Ashfield
-  Changqing Li
-  Charlie Wu
-  Chen Qi
-  Chi Xu
-  Daniel Ammann
-  Deepthi Hemraj
-  Denys Dmytriyenko
-  Dmitry Baryshkov
-  Ed Beroset
-  Eero Aaltonen
-  Fabien Mahot
-  Frieder Paape
-  Frieder Schrempf
-  Hannu Lounento
-  Ian Ray
-  Jermain Horsman
-  Jörg Sommer
-  Kai Kang
-  Khem Raj
-  Lee Chee Yang
-  Lorenzo Arena
-  Marc Ferland
-  Markus Volk
-  Martin Jansa
-  Michael Halstead
-  Mikko Rapeli
-  Mingli Yu
-  Natasha Bailey
-  Nikhil R
-  Pablo Saavedra
-  Paul Gortmaker
-  Pavel Zhukov
-  Peter Kjellerstedt
-  Qiu Tingting
-  Quentin Schulz
-  Randolph Sapp
-  Randy MacLeod
-  Ranjitsinh Rathod
-  Richard Purdie
-  Riyaz Khan
-  Ross Burton
-  Sakib Sajal
-  Sanjay Chitroda
-  Siddharth Doshi
-  Soumya Sambu
-  Steve Sakoman
-  Sudip Mukherjee
-  Sundeep KOKKONDA
-  Thomas Roos
-  Tim Orling
-  Tom Hochstein
-  Trevor Gamblin
-  Ulrich Ölmann
-  Wang Mingyu
-  Xiangyu Chen


Repositories / Downloads for Yocto-4.2.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`mickledore </poky/log/?h=mickledore>`
-  Tag:  :yocto_git:`yocto-4.2.2 </poky/log/?h=yocto-4.2.2>`
-  Git Revision: :yocto_git:`6e17b3e644ca15b8b4afd071ccaa6f172a0e681a </poky/commit/?id=6e17b3e644ca15b8b4afd071ccaa6f172a0e681a>`
-  Release Artefact: poky-6e17b3e644ca15b8b4afd071ccaa6f172a0e681a
-  sha: c0b4dadcf00b97d866dd4cc2f162474da2c3e3289badaa42a978bff1d479af99
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2.2/poky-6e17b3e644ca15b8b4afd071ccaa6f172a0e681a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2.2/poky-6e17b3e644ca15b8b4afd071ccaa6f172a0e681a.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`mickledore </openembedded-core/log/?h=mickledore>`
-  Tag:  :oe_git:`yocto-4.2.2 </openembedded-core/log/?h=yocto-4.2.2>`
-  Git Revision: :oe_git:`3ef283e02b0b91daf64c3a589e1f6bb68d4f5aa1 </openembedded-core/commit/?id=3ef283e02b0b91daf64c3a589e1f6bb68d4f5aa1>`
-  Release Artefact: oecore-3ef283e02b0b91daf64c3a589e1f6bb68d4f5aa1
-  sha: d2fd127f46e626fa4456c193af3dbd25d4b2565db59bc23be69a3b2dd4febed5
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2.2/oecore-3ef283e02b0b91daf64c3a589e1f6bb68d4f5aa1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2.2/oecore-3ef283e02b0b91daf64c3a589e1f6bb68d4f5aa1.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`mickledore </meta-mingw/log/?h=mickledore>`
-  Tag:  :yocto_git:`yocto-4.2.2 </meta-mingw/log/?h=yocto-4.2.2>`
-  Git Revision: :yocto_git:`4608d0bb7e47c52b8f6e9be259bfb1716fda9fd6 </meta-mingw/commit/?id=4608d0bb7e47c52b8f6e9be259bfb1716fda9fd6>`
-  Release Artefact: meta-mingw-4608d0bb7e47c52b8f6e9be259bfb1716fda9fd6
-  sha: fcbae0dedb363477492b86b8f997e06f995793285535b24dc66038845483eeef
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2.2/meta-mingw-4608d0bb7e47c52b8f6e9be259bfb1716fda9fd6.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2.2/meta-mingw-4608d0bb7e47c52b8f6e9be259bfb1716fda9fd6.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.4 </bitbake/log/?h=2.4>`
-  Tag:  :oe_git:`yocto-4.2.2 </bitbake/log/?h=yocto-4.2.2>`
-  Git Revision: :oe_git:`08033b63ae442c774bd3fce62844eac23e6882d7 </bitbake/commit/?id=08033b63ae442c774bd3fce62844eac23e6882d7>`
-  Release Artefact: bitbake-08033b63ae442c774bd3fce62844eac23e6882d7
-  sha: 1d070c133bfb6502ac04befbf082cbfda7582c8b1c48296a788384352e5061fd
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2.2/bitbake-08033b63ae442c774bd3fce62844eac23e6882d7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2.2/bitbake-08033b63ae442c774bd3fce62844eac23e6882d7.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`mickledore </yocto-docs/log/?h=mickledore>`
-  Tag: :yocto_git:`yocto-4.2.2 </yocto-docs/log/?h=yocto-4.2.2>`
-  Git Revision: :yocto_git:`54d849d259a332389beea159d789f8fa92871475 </yocto-docs/commit/?id=54d849d259a332389beea159d789f8fa92871475>`

