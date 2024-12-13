.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.4 (Kirkstone)
-----------------------------------------

Security Fixes in Yocto-4.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils : fix :cve_nist:`2022-38533`
-  curl: fix :cve_nist:`2022-35252`
-  sqlite: fix :cve_nist:`2022-35737`
-  grub2: fix :cve_nist:`2021-3695`, :cve_nist:`2021-3696`, :cve_nist:`2021-3697`, :cve_nist:`2022-28733`, :cve_nist:`2022-28734` and :cve_nist:`2022-28735`
-  u-boot: fix :cve_nist:`2022-30552` and :cve_nist:`2022-33967`
-  libxml2: Ignore :cve_nist:`2016-3709`
-  libtiff: fix :cve_nist:`2022-34526`
-  zlib: fix :cve_nist:`2022-37434`
-  gnutls: fix :cve_nist:`2022-2509`
-  u-boot: fix :cve_nist:`2022-33103`
-  qemu: fix :cve_nist:`2021-3507`, :cve_nist:`2021-3929`, :cve_nist:`2021-4158`, :cve_nist:`2022-0216` and :cve_nist:`2022-0358`


Fixes in Yocto-4.0.4
~~~~~~~~~~~~~~~~~~~~

-  apr: Cache configure tests which use AC_TRY_RUN
-  apr: Use correct strerror_r implementation based on libc type
-  apt: fix nativesdk-apt build failure during the second time build
-  archiver.bbclass: remove unsed do_deploy_archives[dirs]
-  archiver.bbclass: some recipes that uses the kernelsrc bbclass uses the shared source
-  autoconf: Fix strict prototype errors in generated tests
-  autoconf: Update K & R stype functions
-  bind: upgrade to 9.18.5
-  bitbake.conf: set :term:`BB_DEFAULT_UMASK` using ??=
-  bitbake: ConfHandler/BBHandler: Improve comment error messages and add tests
-  bitbake: ConfHandler: Remove lingering close
-  bitbake: bb/utils: movefile: use the logger for printing
-  bitbake: bb/utils: remove: check the path again the expand python glob
-  bitbake: bitbake-user-manual: Correct description of the ??= operator
-  bitbake: bitbake-user-manual: npm fetcher: improve description of :term:`SRC_URI` format
-  bitbake: bitbake: bitbake-user-manual: hashserv can be accessed on a dedicated domain
-  bitbake: bitbake: runqueue: add cpu/io pressure regulation
-  bitbake: bitbake: runqueue: add memory pressure regulation
-  bitbake: cooker: Drop sre_constants usage
-  bitbake: doc: bitbake-user-manual: add explicit target for crates fetcher
-  bitbake: doc: bitbake-user-manual: document npm and npmsw fetchers
-  bitbake: event.py: ignore exceptions from stdout and sterr operations in atexit
-  bitbake: fetch2: Ensure directory exists before creating symlink
-  bitbake: fetch2: gitsm: fix incorrect handling of git submodule relative urls
-  bitbake: runqueue: Change pressure file warning to a note
-  bitbake: runqueue: Fix unihash cache mismatch issues
-  bitbake: toaster: fix kirkstone version
-  bitbake: utils: Pass lock argument in fileslocked
-  bluez5: upgrade to 5.65
-  boost: fix install of fiber shared libraries
-  cairo: Adapt the license information based on what is being built
-  classes: cve-check: Get shared database lock
-  cmake: remove CMAKE_ASM_FLAGS variable in toolchain file
-  connman: Backports for security fixes
-  core-image.bbclass: Exclude openssh complementary packages
-  cracklib: Drop using register keyword
-  cracklib: upgrade to 2.9.8
-  create-spdx: Fix supplier field
-  create-spdx: handle links to inaccessible locations
-  create-spdx: ignore packing control files from ipk and deb
-  cve-check: Don't use f-strings
-  cve-check: close cursors as soon as possible
-  devtool/upgrade: catch bb.fetch2.decodeurl errors
-  devtool/upgrade: correctly clean up when recipe filename isn't yet known
-  devtool: error out when workspace is using old override syntax
-  ell: upgrade to 0.50
-  epiphany: upgrade to 42.4
-  externalsrc: Don't wipe out src dir when EXPORT_FUNCTIONS is used.
-  gcc-multilib-config: Fix i686 toolchain relocation issues
-  gcr: Define _GNU_SOURCE
-  gdk-pixbuf: upgrade to 2.42.9
-  glib-networking: upgrade to 2.72.2
-  go: upgrade to v1.17.13
-  insane.bbclass: Skip patches not in oe-core by full path
-  iso-codes: upgrade to 4.11.0
-  kernel-fitimage.bbclass: add padding algorithm property in config nodes
-  kernel-fitimage.bbclass: only package unique DTBs
-  kernel: Always set :term:`CC` and :term:`LD` for the kernel build
-  kernel: Use consistent make flags for menuconfig
-  lib:npm_registry: initial checkin
-  libatomic-ops: upgrade to 7.6.14
-  libcap: upgrade to 2.65
-  libjpeg-turbo: upgrade to 2.1.4
-  libpam: use /run instead of /var/run in systemd tmpfiles
-  libtasn1: upgrade to 4.19.0
-  liburcu: upgrade to 0.13.2
-  libwebp: upgrade to 1.2.4
-  libwpe: upgrade to 1.12.3
-  libxml2: Port gentest.py to Python-3
-  lighttpd: upgrade to 1.4.66
-  linux-yocto/5.10: update genericx86* machines to v5.10.135
-  linux-yocto/5.10: update to v5.10.137
-  linux-yocto/5.15: update genericx86* machines to v5.15.59
-  linux-yocto/5.15: update to v5.15.62
-  linux-yocto: Fix :term:`COMPATIBLE_MACHINE` regex match
-  linux-yocto: prepend the value with a space when append to :term:`KERNEL_EXTRA_ARGS`
-  lttng-modules: fix 5.19+ build
-  lttng-modules: fix build against mips and v5.19 kernel
-  lttng-modules: fix build for kernel 5.10.137
-  lttng-modules: replace mips compaction fix with upstream change
-  lz4: upgrade to 1.9.4
-  maintainers: update opkg maintainer
-  meta: introduce :term:`UBOOT_MKIMAGE_KERNEL_TYPE`
-  migration guides: add missing release notes
-  mobile-broadband-provider-info: upgrade to 20220725
-  nativesdk: Clear :term:`TUNE_FEATURES`
-  npm: replace 'npm pack' call by 'tar czf'
-  npm: return content of 'package.json' in 'npm_pack'
-  npm: take 'version' directly from 'package.json'
-  npm: use npm_registry to cache package
-  oeqa/gotoolchain: put writable files in the Go module cache
-  oeqa/gotoolchain: set CGO_ENABLED=1
-  oeqa/parselogs: add qemuarmv5 arm-charlcd masking
-  oeqa/qemurunner: add run_serial() comment
-  oeqa/selftest: rename git.py to intercept.py
-  oeqa: qemurunner: Report UNIX Epoch timestamp on login
-  package_rpm: Do not replace square brackets in %files
-  packagegroup-self-hosted: update for strace
-  parselogs: Ignore xf86OpenConsole error
-  perf: Fix reproducibility issues with 5.19 onwards
-  pinentry: enable _XOPEN_SOURCE on musl for wchar usage in curses
-  poky.conf: add ubuntu-22.04 to tested distros
-  poky.conf: bump version for 4.0.4
-  pseudo: Update to include recent upstream minor fixes
-  python3-pip: Fix :term:`RDEPENDS` after the update
-  ref-manual: add numa to machine features
-  relocate_sdk.py: ensure interpreter size error causes relocation to fail
-  rootfs-postcommands.bbclass: avoid moving ssh host keys if etc is writable
-  rootfs.py: dont try to list installed packages for baremetal images
-  rootfspostcommands.py: Cleanup subid backup files generated by shadow-utils
-  ruby: drop capstone support
-  runqemu: Add missing space on default display option
-  runqemu: display host uptime when starting
-  sanity: add a comment to ensure CONNECTIVITY_CHECK_URIS is correct
-  scripts/oe-setup-builddir: make it known where configurations come from
-  scripts/runqemu.README: fix typos and trailing whitespaces
-  selftest/wic: Tweak test case to not depend on kernel size
-  shadow: Avoid nss warning/error with musl
-  shadow: Enable subid support
-  system-requirements.rst: Add Ubuntu 22.04 to list of supported distros
-  systemd: Add 'no-dns-fallback' :term:`PACKAGECONFIG` option
-  systemd: Fix unwritable /var/lock when no sysvinit handling
-  sysvinit-inittab/start_getty: Fix respawn too fast
-  tcp-wrappers: Fix implicit-function-declaration warnings
-  tzdata: upgrade to 2022b
-  util-linux: Remove --enable-raw from :term:`EXTRA_OECONF`
-  vala: upgrade to 0.56.3
-  vim: Upgrade to 9.0.0453
-  watchdog: Include needed system header for function decls
-  webkitgtk: upgrade to 2.36.5
-  weston: upgrade to 10.0.2
-  wic/bootimg-efi: use cross objcopy when building unified kernel image
-  wic: add target tools to PATH when executing native commands
-  wic: depend on cross-binutils
-  wireless-regdb: upgrade to 2022.08.12
-  wpebackend-fdo: upgrade to 1.12.1
-  xinetd: Pass missing -D_GNU_SOURCE
-  xz: update to 5.2.6


Known Issues in Yocto-4.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alejandro Hernandez Samaniego
-  Alex Stewart
-  Alexander Kanavin
-  Alexandre Belloni
-  Andrei Gherzan
-  Anuj Mittal
-  Aryaman Gupta
-  Awais Belal
-  Beniamin Sandu
-  Bertrand Marquis
-  Bruce Ashfield
-  Changqing Li
-  Chee Yang Lee
-  Daiane Angolini
-  Enrico Scholz
-  Ernst Sjöstrand
-  Gennaro Iorio
-  Hitendra Prajapati
-  Jacob Kroon
-  Jon Mason
-  Jose Quaresma
-  Joshua Watt
-  Kai Kang
-  Khem Raj
-  Kristian Amlie
-  LUIS ENRIQUEZ
-  Mark Hatle
-  Martin Beeger
-  Martin Jansa
-  Mateusz Marciniec
-  Michael Opdenacker
-  Mihai Lindner
-  Mikko Rapeli
-  Ming Liu
-  Niko Mauno
-  Ola x Nilsson
-  Otavio Salvador
-  Paul Eggleton
-  Pavel Zhukov
-  Peter Bergin
-  Peter Kjellerstedt
-  Peter Marko
-  Rajesh Dangi
-  Randy MacLeod
-  Rasmus Villemoes
-  Richard Purdie
-  Robert Joslyn
-  Roland Hieber
-  Ross Burton
-  Sakib Sajal
-  Shubham Kulkarni
-  Steve Sakoman
-  Ulrich Ölmann
-  Yang Xu
-  Yongxin Liu
-  ghassaneben
-  pgowda
-  Wang Mingyu

Repositories / Downloads for Yocto-4.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.4 </poky/log/?h=yocto-4.0.4>`
-  Git Revision: :yocto_git:`d64bef1c7d713b92a51228e5ade945835e5a94a4 </poky/commit/?id=d64bef1c7d713b92a51228e5ade945835e5a94a4>`
-  Release Artefact: poky-d64bef1c7d713b92a51228e5ade945835e5a94a4
-  sha: b5e92506b31f88445755bad2f45978b747ad1a5bea66ca897370542df5f1e7db
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.4/poky-d64bef1c7d713b92a51228e5ade945835e5a94a4.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.4/poky-d64bef1c7d713b92a51228e5ade945835e5a94a4.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.4 </openembedded-core/log/?h=yocto-4.0.4>`
-  Git Revision: :oe_git:`f7766da462905ec67bf549d46b8017be36cd5b2a </openembedded-core/commit/?id=f7766da462905ec67bf549d46b8017be36cd5b2a>`
-  Release Artefact: oecore-f7766da462905ec67bf549d46b8017be36cd5b2a
-  sha: ce0ac011474db5e5f0bb1be3fb97f890a02e46252a719dbcac5813268e48ff16
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.4/oecore-f7766da462905ec67bf549d46b8017be36cd5b2a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.4/oecore-f7766da462905ec67bf549d46b8017be36cd5b2a.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.4 </meta-mingw/log/?h=yocto-4.0.4>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.4/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.4/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.4 </meta-gplv2/log/?h=yocto-4.0.4>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.4/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.4/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.4 </bitbake/log/?h=yocto-4.0.4>`
-  Git Revision: :oe_git:`ac576d6fad6bba0cfea931883f25264ea83747ca </bitbake/commit/?id=ac576d6fad6bba0cfea931883f25264ea83747ca>`
-  Release Artefact: bitbake-ac576d6fad6bba0cfea931883f25264ea83747ca
-  sha: 526c2768874eeda61ade8c9ddb3113c90d36ef44a026d6690f02de6f3dd0ea12
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.4/bitbake-ac576d6fad6bba0cfea931883f25264ea83747ca.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.4/bitbake-ac576d6fad6bba0cfea931883f25264ea83747ca.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.4 </yocto-docs/log/?h=yocto-4.0.4>`
-  Git Revision: :yocto_git:`f632dad24c39778f948014029e74db3c871d9d21 </yocto-docs/commit/?id=f632dad24c39778f948014029e74db3c871d9d21>`
