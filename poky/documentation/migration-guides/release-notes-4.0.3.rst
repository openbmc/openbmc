.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.3 (Kirkstone)
-----------------------------------------

Security Fixes in Yocto-4.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: fix :cve:`2019-1010204`
-  busybox: fix :cve:`2022-30065`
-  cups: ignore :cve:`2022-26691`
-  curl: Fix :cve:`2022-32205`, :cve:`2022-32206`, :cve:`2022-32207` and :cve:`2022-32208`
-  dpkg: fix :cve:`2022-1664`
-  ghostscript: fix :cve:`2022-2085`
-  harfbuzz: fix :cve:`2022-33068`
-  libtirpc: fix :cve:`2021-46828`
-  lua: fix :cve:`2022-33099`
-  nasm: ignore :cve:`2020-18974`
-  qemu: fix :cve:`2022-35414`
-  qemu: ignore :cve:`2021-20255` and :cve:`2019-12067`
-  tiff: fix :cve:`2022-1354`, :cve:`2022-1355`, :cve:`2022-2056`, :cve:`2022-2057` and :cve:`2022-2058`
-  u-boot: fix :cve:`2022-34835`
-  unzip: fix :cve:`2022-0529` and :cve:`2022-0530`


Fixes in Yocto-4.0.3
~~~~~~~~~~~~~~~~~~~~

-  alsa-state: correct license
-  at: take tarballs from debian
-  base.bbclass: Correct the test for obsolete license exceptions
-  base/reproducible: Change Source Date Epoch generation methods
-  bin_package: install into base_prefix
-  bind: Remove legacy python3 :term:`PACKAGECONFIG` code
-  bind: upgrade to 9.18.4
-  binutils: stable 2.38 branch updates
-  build-appliance-image: Update to kirkstone head revision
-  cargo_common.bbclass: enable bitbake vendoring for externalsrc
-  coreutils: Tweak packaging variable names for coreutils-dev
-  curl: backport openssl fix CN check error code
-  cve-check: hook cleanup to the BuildCompleted event, not CookerExit
-  cve-extra-exclusions: Clean up and ignore three CVEs (2xqemu and nasm)
-  devtool: finish: handle patching when :term:`S` points to subdir of a git repo
-  devtool: ignore pn- overrides when determining :term:`SRC_URI` overrides
-  docs: BB_HASHSERVE_UPSTREAM: update to new host
-  dropbear: break dependency on base package for -dev package
-  efivar: fix import functionality
-  encodings: update to 1.0.6
-  epiphany: upgrade to 42.3
-  externalsrc.bbclass: support crate fetcher on externalsrc
-  font-util: update 1.3.2 -> 1.3.3
-  gcc-runtime: Fix build when using gold
-  gcc-runtime: Fix missing :term:`MLPREFIX` in debug mappings
-  gcc-runtime: Pass -nostartfiles when building dummy libstdc++.so
-  gcc: Backport a fix for gcc bug 105039
-  git: upgrade to v2.35.4
-  glib-2.0: upgrade to 2.72.3
-  glib-networking: upgrade to 2.72.1
-  glibc : stable 2.35 branch updates
-  glibc-tests: Avoid reproducibility issues
-  glibc-tests: not clear :term:`BBCLASSEXTEND`
-  glibc: revert one upstream change to work around broken :term:`DEBUG_BUILD` build
-  glibc: stable 2.35 branch updates
-  gnupg: upgrade to 2.3.7
-  go: upgrade to v1.17.12
-  gobject-introspection-data: Disable cache for g-ir-scanner
-  gperf: Add a patch to work around reproducibility issues
-  gperf: Switch to upstream patch
-  gst-devtools: upgrade to 1.20.3
-  gstreamer1.0-libav: upgrade to 1.20.3
-  gstreamer1.0-omx: upgrade to 1.20.3
-  gstreamer1.0-plugins-bad: upgrade to 1.20.3
-  gstreamer1.0-plugins-base: upgrade to 1.20.3
-  gstreamer1.0-plugins-good: upgrade to 1.20.3
-  gstreamer1.0-plugins-ugly: upgrade to 1.20.3
-  gstreamer1.0-python: upgrade to 1.20.3
-  gstreamer1.0-rtsp-server: upgrade to 1.20.3
-  gstreamer1.0-vaapi: upgrade to 1.20.3
-  gstreamer1.0: upgrade to 1.20.3
-  gtk-doc: Remove hardcoded buildpath
-  harfbuzz: Fix compilation with clang
-  initramfs-framework: move storage mounts to actual rootfs
-  initscripts: run umountnfs as a KILL script
-  insane.bbclass: host-user-contaminated: Correct per package home path
-  insane: Fix buildpaths test to work with special devices
-  kernel-arch: Fix buildpaths leaking into external module compiles
-  kernel-devsrc: fix reproducibility and buildpaths QA warning
-  kernel-devsrc: ppc32: fix reproducibility
-  kernel-uboot.bbclass: Use vmlinux.initramfs when :term:`INITRAMFS_IMAGE_BUNDLE` set
-  kernel.bbclass: pass :term:`LD` also in savedefconfig
-  libffi: fix native build being not portable
-  libgcc: Fix standalone target builds with usrmerge distro feature
-  libmodule-build-perl: Use env utility to find perl interpreter
-  libsoup: upgrade to 3.0.7
-  libuv: upgrade to 1.44.2
-  linux-firmware: upgrade to 20220708
-  linux-firwmare: restore WHENCE_CHKSUM variable
-  linux-yocto-rt/5.15: update to -rt48 (and fix -stable merge)
-  linux-yocto/5.10: fix build_OID_registry/conmakehash buildpaths warning
-  linux-yocto/5.10: fix buildpaths issue with gen-mach-types
-  linux-yocto/5.10: fix buildpaths issue with pnmtologo
-  linux-yocto/5.10: update to v5.10.135
-  linux-yocto/5.15: drop obselete GPIO sysfs ABI
-  linux-yocto/5.15: fix build_OID_registry buildpaths warning
-  linux-yocto/5.15: fix buildpaths issue with gen-mach-types
-  linux-yocto/5.15: fix buildpaths issue with pnmtologo
-  linux-yocto/5.15: fix qemuppc buildpaths warning
-  linux-yocto/5.15: fix reproducibility issues
-  linux-yocto/5.15: update to v5.15.59
-  log4cplus: upgrade to 2.0.8
-  lttng-modules: Fix build failure for kernel v5.15.58
-  lttng-modules: upgrade to 2.13.4
-  lua: Fix multilib buildpath reproducibility issues
-  mkfontscale: upgrade to 1.2.2
-  oe-selftest-image: Ensure the image has sftp as well as dropbear
-  oe-selftest: devtool: test modify git recipe building from a subdir
-  oeqa/runtime/scp: Disable scp test for dropbear
-  oeqa/runtime: add test that the kernel has CONFIG_PREEMPT_RT enabled
-  oeqa/sdk: drop the nativesdk-python 2.x test
-  openssh: Add openssh-sftp-server to openssh :term:`RDEPENDS`
-  openssh: break dependency on base package for -dev package
-  openssl: update to 3.0.5
-  package.bbclass: Avoid stripping signed kernel modules in splitdebuginfo
-  package.bbclass: Fix base directory for debugsource files when using externalsrc
-  package.bbclass: Fix kernel source handling when not using externalsrc
-  package_manager/ipk: do not pipe stderr to stdout
-  packagegroup-core-ssh-dropbear: Add openssh-sftp-server recommendation
-  patch: handle if :term:`S` points to a subdirectory of a git repo
-  perf: fix reproducibility in 5.19+
-  perf: fix reproduciblity in older releases of Linux
-  perf: sort-pmuevents: really keep array terminators
-  perl: don't install Makefile.old into perl-ptest
-  poky.conf: bump version for 4.0.3
-  pulseaudio: add m4-native to :term:`DEPENDS`
-  python3: Backport patch to fix an issue in subinterpreters
-  qemu: Add :term:`PACKAGECONFIG` for brlapi
-  qemu: Avoid accidental librdmacm linkage
-  qemu: Avoid accidental libvdeplug linkage
-  qemu: Fix slirp determinism issue
-  qemu: add :term:`PACKAGECONFIG` for capstone
-  recipetool/devtool: Fix python egg whitespace issues in :term:`PACKAGECONFIG`
-  ref-manual: variables: remove sphinx directive from literal block
-  rootfs-postcommands.bbclass: move host-user-contaminated.txt to ${S}
-  ruby: add :term:`PACKAGECONFIG` for capstone
-  rust: fix issue building cross-canadian tools for aarch64 on x86_64
-  sanity.bbclass: Add ftps to accepted URI protocols for mirrors sanity
-  selftest/runtime_test/virgl: Disable for all almalinux
-  sstatesig: Include all dependencies in SPDX task signatures
-  strace: set :term:`COMPATIBLE_HOST` for riscv32
-  systemd: Added base_bindir into pkg_postinst:udev-hwdb.
-  udev-extraconf/initrdscripts/parted: Rename mount.blacklist -> mount.ignorelist
-  udev-extraconf/mount.sh: add LABELs to mountpoints
-  udev-extraconf/mount.sh: ignore lvm in automount
-  udev-extraconf/mount.sh: only mount devices on hotplug
-  udev-extraconf/mount.sh: save mount name in our tmp filecache
-  udev-extraconf: fix some systemd automount issues
-  udev-extraconf: force systemd-udevd to use shared MountFlags
-  udev-extraconf: let automount base directory configurable
-  udev-extraconf:mount.sh: fix a umount issue
-  udev-extraconf:mount.sh: fix path mismatching issues
-  vala: Fix on target wrapper buildpaths issue
-  vala: upgrade to 0.56.2
-  vim: upgrade to 9.0.0063
-  waffle: correctly request wayland-scanner executable
-  webkitgtk: upgrade to 2.36.4
-  weston: upgrade to 10.0.1
-  wic/plugins/rootfs: Fix NameError for 'orig_path'
-  wic: fix WicError message
-  wireless-regdb: upgrade to 2022.06.06
-  xdpyinfo: upgrade to 1.3.3
-  xev: upgrade to 1.2.5
-  xf86-input-synaptics: upgrade to 1.9.2
-  xmodmap: upgrade to 1.0.11
-  xorg-app: Tweak handling of compression changes in :term:`SRC_URI`
-  xserver-xorg: upgrade to 21.1.4
-  xwayland: upgrade to 22.1.3
-  yocto-bsps/5.10: fix buildpaths issue with gen-mach-types
-  yocto-bsps/5.10: fix buildpaths issue with pnmtologo
-  yocto-bsps/5.15: fix buildpaths issue with gen-mach-types
-  yocto-bsps/5.15: fix buildpaths issue with pnmtologo
-  yocto-bsps: buildpaths fixes
-  yocto-bsps: update to v5.10.130
-  yocto-bsps: buildpaths fixes
-  yocto-bsps: update to v5.15.54


Known Issues in Yocto-4.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Ahmed Hossam
-  Alejandro Hernandez Samaniego
-  Alex Kiernan
-  Alexander Kanavin
-  Bruce Ashfield
-  Chanho Park
-  Christoph Lauer
-  David Bagonyi
-  Dmitry Baryshkov
-  He Zhe
-  Hitendra Prajapati
-  Jose Quaresma
-  Joshua Watt
-  Kai Kang
-  Khem Raj
-  Lee Chee Yang
-  Lucas Stach
-  Markus Volk
-  Martin Jansa
-  Maxime Roussin-Bélanger
-  Michael Opdenacker
-  Mihai Lindner
-  Ming Liu
-  Mingli Yu
-  Muhammad Hamza
-  Naveen
-  Pascal Bach
-  Paul Eggleton
-  Pavel Zhukov
-  Peter Bergin
-  Peter Kjellerstedt
-  Peter Marko
-  Pgowda
-  Raju Kumar Pothuraju
-  Richard Purdie
-  Robert Joslyn
-  Ross Burton
-  Sakib Sajal
-  Shruthi Ravichandran
-  Steve Sakoman
-  Sundeep Kokkonda
-  Thomas Roos
-  Tom Hochstein
-  Wentao Zhang
-  Yi Zhao
-  Yue Tao
-  gr embeter
-  leimaohui
-  Wang Mingyu


Repositories / Downloads for Yocto-4.0.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.3 </poky/log/?h=yocto-4.0.3>`
-  Git Revision: :yocto_git:`387ab5f18b17c3af3e9e30dc58584641a70f359f </poky/commit/?id=387ab5f18b17c3af3e9e30dc58584641a70f359f>`
-  Release Artefact: poky-387ab5f18b17c3af3e9e30dc58584641a70f359f
-  sha: fe674186bdb0684313746caa9472134fc19e6f1443c274fe02c06cb1e675b404
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.3/poky-387ab5f18b17c3af3e9e30dc58584641a70f359f.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.3/poky-387ab5f18b17c3af3e9e30dc58584641a70f359f.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.3 </openembedded-core/log/?h=yocto-4.0.3>`
-  Git Revision: :oe_git:`2cafa6ed5f0aa9df5a120b6353755d56c7c7800d </openembedded-core/commit/?id=2cafa6ed5f0aa9df5a120b6353755d56c7c7800d>`
-  Release Artefact: oecore-2cafa6ed5f0aa9df5a120b6353755d56c7c7800d
-  sha: 5181d3e8118c6112936637f01a07308b715e0e3d12c7eba338556747dfcabe92
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.3/oecore-2cafa6ed5f0aa9df5a120b6353755d56c7c7800d.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.3/oecore-2cafa6ed5f0aa9df5a120b6353755d56c7c7800d.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.3 </meta-mingw/log/?h=yocto-4.0.3>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.3/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.3/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.3 </meta-gplv2/log/?h=yocto-4.0.3>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.3/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.3/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.3 </bitbake/log/?h=yocto-4.0.3>`
-  Git Revision: :oe_git:`b8fd6f5d9959d27176ea016c249cf6d35ac8ba03 </bitbake/commit/?id=b8fd6f5d9959d27176ea016c249cf6d35ac8ba03>`
-  Release Artefact: bitbake-b8fd6f5d9959d27176ea016c249cf6d35ac8ba03
-  sha: 373818b1dee2c502264edf654d6d8f857b558865437f080e02d5ba6bb9e72cc3
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.3/bitbake-b8fd6f5d9959d27176ea016c249cf6d35ac8ba03.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.3/bitbake-b8fd6f5d9959d27176ea016c249cf6d35ac8ba03.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.3 </yocto-docs/log/?h=yocto-4.0.3>`
-  Git Revision: :yocto_git:`d9b3dcf65ef25c06f552482aba460dd16862bf96 </yocto-docs/commit/?id=d9b3dcf65ef25c06f552482aba460dd16862bf96>`

