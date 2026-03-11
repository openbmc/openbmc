.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.2 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  cups: Fix :cve_nist:`2024-35235`
-  gcc: Fix :cve_nist:`2024-0151`
-  gdk-pixbuf: Fix :cve_nist:`2022-48622`
-  ghostscript: fix :cve_mitre:`2024-29510`, :cve_mitre:`2024-33869`, :cve_mitre:`2024-33870` and :cve_mitre:`2024-33871`
-  git: Fix :cve_nist:`2024-32002`, :cve_nist:`2024-32004`, :cve_nist:`2024-32020`, :cve_nist:`2024-32021` and :cve_nist:`2024-32465`
-  glib-2.0: Fix :cve_nist:`2024-34397`
-  glibc: Fix :cve_nist:`2024-2961`, :cve_nist:`2024-33599`, :cve_nist:`2024-33600`, :cve_nist:`2024-33601` and :cve_nist:`2024-33602`
-  ncurses: Fix :cve_nist:`2023-45918` and :cve_nist:`2023-50495`
-  openssl: Fix :cve_nist:`2024-4603` and :cve_mitre:`2024-4741`
-  util-linux: Fix :cve_nist:`2024-28085`
-  xserver-xorg: Fix :cve_nist:`2024-31080`, :cve_nist:`2024-31081`, :cve_nist:`2024-31082` and :cve_nist:`2024-31083`


Fixes in Yocto-5.0.2
~~~~~~~~~~~~~~~~~~~~

-  appstream: Upgrade to 1.0.3
-  apr: submit 0001-Add-option-to-disable-timed-dependant-tests.patch upstream
-  base-files: profile: fix error sh: 1: unknown operand
-  bash: Fix file-substitution error-handling bug
-  bash: mark build-tests.patch as Inappropriate
-  binutils: Fix aarch64 disassembly abort
-  bitbake: bb: Use namedtuple for Task data
-  bitbake: cooker: Handle ImportError for websockets
-  bitbake: fetch2/gcp: Add missing runfetchcmd import
-  bitbake: fetch2/wget: Canonicalize :term:`DL_DIR` paths for wget2 compatibility
-  bitbake: fetch2/wget: Fix failure path for files that are empty or don't exist
-  bitbake: hashserv: client: Add batch stream API
-  bitbake: parse: Improve/fix cache invalidation via mtime
-  bitbake: runqueue: Add timing warnings around slow loops
-  bitbake: runqueue: Allow rehash loop to exit in case of interrupts
-  bitbake: runqueue: Improve rehash get_unihash parallelism
-  bitbake: runqueue: Process unihashes in parallel at init
-  bitbake: siggen/runqueue: Report which dependencies affect the taskhash
-  bitbake: siggen: Enable batching of unihash queries
-  bitbake: tests/fetch: Tweak test to match upstream repo url change
-  bitbake: tests/fetch: Tweak to work on Fedora40
-  build-appliance-image: Update to scarthgap head revision
-  busybox: update :cve_nist:`2022-28391` patches upstream status
-  cdrtools-native: Fix build with GCC 14
-  classes: image_types: apply EXTRA_IMAGECMD:squashfs* in oe_mksquashfs()
-  classes: image_types: quote variable assignment needed by dash
-  consolekit: Disable incompatible-pointer-types warning as error
-  cracklib: Modify patch to compile with GCC 14
-  cronie: Upgrade to 1.7.2
-  cups: Upgrade to 2.4.9
-  db: ignore implicit-int and implicit-function-declaration issues fatal with gcc-14
-  devtool: modify: Catch git submodule error for go code
-  devtool: standard: update-recipe/finish: fix update localfile in another layer
-  devtool: sync: Fix Execution error
-  expect: ignore various issues now fatal with gcc-14
-  expect: mark patches as Inactive-Upstream
-  gawk: fix readline detection
-  gcc : Upgrade to v13.3
-  gcc-runtime: libgomp fix for gcc 14 warnings with mandb selftest
-  gdk-pixbuf: Upgrade to 2.42.12
-  git: set --with-gitconfig=/etc/gitconfig for -native builds
-  git: Upgrade to 2.44.1
-  glib-2.0: Upgrade to 2.78.6
-  glibc: Update to latest on stable 2.39 branch (273a835fe7...)
-  glibc: correct :term:`LICENSE` to "GPL-2.0-only & LGPL-2.1-or-later"
-  go: Drop the linkmode completely
-  goarch: Revert "disable dynamic linking globally"
-  gstreamer1.0-plugins-good: Include qttools-native during the build with qt5 :term:`PACKAGECONFIG`
-  gtk4: Disable int-conversion warning as error
-  icu: add upstream submission links for fix-install-manx.patch
-  ipk: Fix clean up of extracted IPK payload
-  iproute2: Fix build with GCC-14
-  iproute2: drop obsolete patch
-  iputils: splitting the ping6 as a package
-  kea: Remove -fvisibility-inlines-hidden from C++ flags
-  kea: remove unnecessary reproducibility patch
-  kernel.bbclass: check, if directory exists before removing empty module directory
-  kexec-tools: Fix build with GCC-14 on musl
-  lib/oe/package-manager: allow including self in create_packages_dir
-  lib/package_manager/ipk: Do not hardcode payload compression algorithm
-  libarchive: Upgrade to 3.7.4
-  libcgroup: fix build on non-systemd systems
-  libgloss: Do not apply non-existent patch
-  libinput: fix building with debug-gui option
-  libtraceevent: submit meson.patch upstream
-  libunwind: ignore various issues now fatal with gcc-14
-  libusb1: Set :term:`CVE_PRODUCT`
-  llvm: Switch to using release tarballs
-  llvm: Upgrade to 18.1.5
-  lrzsz connman-gnome libfm: ignore various issues fatal with gcc-14
-  ltp: Fix build with GCC-14
-  ltp: add iputils-ping6 to :term:`RDEPENDS`
-  lttng-ust: Upgrade to 2.13.8
-  mesa: Upgrade to 24.0.5
-  oeqa/postactions: Do not use -l option with df
-  oeqa/sdk/assimp: Upgrade and fix for gcc 14
-  oeqa/sdkext/devtool: replace use of librdfa
-  oeqa/selftest/debuginfod: use localpkgfeed to speed server startup
-  oeqa/selftest/devtool: Revert  fix test_devtool_add_git_style2"
-  oeqa/selftest/devtool: add test for modifying recipes using go.bbclass
-  oeqa/selftest/devtool: add test for updating local files into another layer
-  oeqa/selftest/devtool: fix _test_devtool_add_git_url
-  oeqa: selftest: context: run tests serially if testtools/subunit modules are not found
-  openssl: Upgrade to 3.2.2
-  p11-kit: ignore various issues fatal with gcc-14 (for 32bit MACHINEs)
-  patchtest: test_metadata: fix invalid escape sequences
-  poky.conf: bump version for 5.0.2
-  ppp: Add RSA-MD in :term:`LICENSE`
-  procps: fix build with new glibc but old kernel headers
-  ptest-runner: Bump to 2.4.4 (95f528c)
-  recipetool: Handle several go-import tags in go resolver
-  recipetool: Handle unclean response in go resolver
-  run-postinsts.service: Removed --no-reload to fix reload warning when users execute systemctl in the first boot.
-  selftest/classes: add localpkgfeed class
-  serf: mark patch as inappropriate for upstream submission
-  taglib: Upgrade to 2.0.1
-  ttyrun: define :term:`CVE_PRODUCT`
-  uboot-sign: fix loop in do_uboot_assemble_fitimage
-  update-rc.d: add +git to :term:`PV`
-  webkitgtk: Upgrade to 2.44.1
-  xinput-calibrator: mark upstream as inactive in a patch
-  xserver-xorg: Upgrade to 21.1.12
-  yocto-uninative: Update to 4.5 for gcc 14
-  zip: Fix build with gcc-14


Known Issues in Yocto-5.0.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.0.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Adriaan Schmidt
-  Alexander Kanavin
-  Alexandre Truong
-  Anton Almqvist
-  Archana Polampalli
-  Changqing Li
-  Deepthi Hemraj
-  Felix Nilsson
-  Heiko Thole
-  Jose Quaresma
-  Joshua Watt
-  Julien Stephan
-  Kai Kang
-  Khem Raj
-  Lei Maohui
-  Marc Ferland
-  Marek Vasut
-  Mark Hatle
-  Martin Hundebøll
-  Martin Jansa
-  Maxin B. John
-  Michael Halstead
-  Mingli Yu
-  Ola x Nilsson
-  Peter Marko
-  Philip Lorenz
-  Poonam Jadhav
-  Ralph Siemsen
-  Rasmus Villemoes
-  Ricardo Simoes
-  Richard Purdie
-  Robert Joslyn
-  Ross Burton
-  Rudolf J Streif
-  Siddharth Doshi
-  Soumya Sambu
-  Steve Sakoman
-  Sven Schwermer
-  Trevor Gamblin
-  Vincent Kriek
-  Wang Mingyu
-  Xiangyu Chen
-  Yogita Urade
-  Zev Weiss
-  Zoltan Boszormenyi


Repositories / Downloads for Yocto-5.0.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.2 </poky/log/?h=yocto-5.0.2>`
-  Git Revision: :yocto_git:`f7def85be9f99dcb4ba488bead201f670304379b </poky/commit/?id=f7def85be9f99dcb4ba488bead201f670304379b>`
-  Release Artefact: poky-f7def85be9f99dcb4ba488bead201f670304379b
-  sha: 0610a3175846d87f8a853020e8d517c94fe5e8b3fd4e40cd2d0ddbc22e75db4c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.2/poky-f7def85be9f99dcb4ba488bead201f670304379b.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.2/poky-f7def85be9f99dcb4ba488bead201f670304379b.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.2 </openembedded-core/log/?h=yocto-5.0.2>`
-  Git Revision: :oe_git:`803cc32e72b4fc2fc28d92090e61f5dd288a10cb </openembedded-core/commit/?id=803cc32e72b4fc2fc28d92090e61f5dd288a10cb>`
-  Release Artefact: oecore-803cc32e72b4fc2fc28d92090e61f5dd288a10cb
-  sha: b63f1214438e540ec15f1ec7f49615f31584c93e9cff10833273eefc710a7862
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.2/oecore-803cc32e72b4fc2fc28d92090e61f5dd288a10cb.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.2/oecore-803cc32e72b4fc2fc28d92090e61f5dd288a10cb.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.2 </meta-mingw/log/?h=yocto-5.0.2>`
-  Git Revision: :yocto_git:`acbba477893ef87388effc4679b7f40ee49fc852 </meta-mingw/commit/?id=acbba477893ef87388effc4679b7f40ee49fc852>`
-  Release Artefact: meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852
-  sha: 3b7c2f475dad5130bace652b150367f587d44b391218b1364a8bbc430b48c54c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.2/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.2/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.2 </bitbake/log/?h=yocto-5.0.2>`
-  Git Revision: :oe_git:`8714a02e13477a9d97858b3642e05f28247454b5 </bitbake/commit/?id=8714a02e13477a9d97858b3642e05f28247454b5>`
-  Release Artefact: bitbake-8714a02e13477a9d97858b3642e05f28247454b5
-  sha: f22b56447e321c308353196da1d6dd76af5e9957e7e654c75dfd707f58091fd1
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.2/bitbake-8714a02e13477a9d97858b3642e05f28247454b5.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.2/bitbake-8714a02e13477a9d97858b3642e05f28247454b5.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.2 </yocto-docs/log/?h=yocto-5.0.2>`
-  Git Revision: :yocto_git:`875dfe69e93bf8fee3b8c07818a6ac059f228a13 </yocto-docs/commit/?id=875dfe69e93bf8fee3b8c07818a6ac059f228a13>`


