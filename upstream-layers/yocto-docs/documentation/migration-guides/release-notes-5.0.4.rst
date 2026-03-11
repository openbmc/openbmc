.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.4 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  apr: Fix :cve_nist:`2023-49582`
-  curl: Ignore :cve_nist:`2024-32928`
-  curl: Fix :cve_nist:`2024-7264`
-  expat: Fix :cve_nist:`2024-45490`, :cve_nist:`2024-45491` and :cve_nist:`2024-45492`
-  ffmpeg: Fix :cve_nist:`2023-50008` and :cve_nist:`2024-32230`
-  libpcap: Fix :cve_nist:`2023-7256` and :cve_nist:`2024-8006`
-  libyaml: Ignore :cve_nist:`2024-35325` and :cve_nist:`2024-35326`
-  openssl: Fix :cve_nist:`2024-5535` and :cve_nist:`2024-6119`
-  python3-certifi: Fix :cve_nist:`2024-39689`
-  python3-setuptools: Fix :cve_nist:`2024-6345`
-  python3: Fix :cve_nist:`2024-6232`, :cve_nist:`2024-7592`, :cve_nist:`2024-8088` and :cve_nist:`2024-27034`
-  qemu: Fix :cve_nist:`2024-4467` and :cve_nist:`2024-7409`
-  ruby: Fix :cve_nist:`2024-27282`
-  tiff: Fix :cve_nist:`2024-7006`
-  vim: Fix :cve_nist:`2024-41957`, :cve_nist:`2024-41965`, :cve_nist:`2024-43374`, :cve_nist:`2024-43790` and :cve_nist:`2024-43802`


Fixes in Yocto-5.0.4
~~~~~~~~~~~~~~~~~~~~

-  apr: drop 0007-explicitly-link-libapr-against-phtread-to-make-gold-.patch
-  apr: upgrade to 1.7.5
-  bind: Fix build with the `httpstats` package config enabled
-  bitbake: data_smart: Improve performance for VariableHistory
-  bluez5: remove redundant patch for MAX_INPUT
-  build-appliance-image: Update to scarthgap head revision
-  buildhistory: Fix intermittent package file list creation
-  buildhistory: Restoring files from preserve list
-  buildhistory: Simplify intercept call sites and drop SSTATEPOSTINSTFUNC usage
-  busybox: Fix cut with "-s" flag
-  create-sdpx-2.2.bbclass: Switch from exists to isfile checking debugsrc
-  cups: upgrade to 2.4.10
-  dejagnu: Fix :term:`LICENSE` (change to GPL-3.0-only)
-  doc: features: describe distribution feature pni-name
-  doc: features: remove duplicate word in distribution feature ext2
-  expat: upgrade to 2.6.3
-  expect-native: fix do_compile failure with gcc-14
-  gcc: Fix spurious '/' in GLIBC_DYNAMIC_LINKER on microblaze
-  gcr: Fix :term:`LICENSE` (change to LGPL-2.0-only)
-  glibc: fix fortran header file conflict for arm
-  go: upgrade to 1.22.6
-  gstreamer1.0: disable flaky baseparser tests
-  image_types.bbclass: Use --force also with lz4,lzop
-  initramfs-framework: fix typos
-  iw: Fix :term:`LICENSE` (change to ISC)
-  libadwaita: upgrade to 1.5.2
-  libcap-ng: update :term:`SRC_URI`
-  libdnf: upgrade to 0.73.2
-  libedit: Make docs generation deterministic
-  libgfortran.inc: fix nativesdk-libgfortran dependencies
-  librsvg: don't try to run target code at build time
-  linux-firmware: add a package for ath12k firmware
-  llvm: Enable libllvm for native build
-  maintainers.inc: add maintainer for python(-setuptools, -smmap, -subunit, -testtools)
-  mc: fix source URL
-  migration-guide: add release notes for 4.0.20 and 5.0.3
-  oeqa/postactions: fix exception handling
-  oeqa/runtime/ssh: In case of failure, show exit code and handle -15 (SIGTERM)
-  oeqa/runtime/ssh: add retry logic and sleeps to allow for slower systems
-  oeqa/runtime/ssh: check for all errors at the end
-  oeqa/runtime/ssh: increase the number of attempts
-  oeqa/selftest/reproducibile: Explicitly list virtual targets
-  oeqa/utils/postactions: transfer whole archive over ssh instead of doing individual copies
-  openssh: add backported header file include
-  openssl: upgrade to 3.2.3
-  os-release: Fix VERSION_CODENAME in case it is empty
-  poky.conf: bump version for 5.0.4
-  populate_sdk_ext.bclass: make sure OECORE_NATIVE_SYSROOT is exported.
-  python3-maturin: Fix cross compilation issue for armv7l, mips64, ppc
-  python3-pycryptodome(x): use python_setuptools_build_meta build class
-  python3: upgrade to 3.12.6
-  python3: skip readline limited history tests
-  qemu: backport patches to fix riscv64 build failure
-  qemuboot: Trigger write_qemuboot_conf task on changes of kernel image realpath
-  ref-manual: fix typo and move :term:`SYSROOT_DIRS` example
-  ruby: Make docs generation deterministic
-  systemd: Mitigate /var/log type mismatch issue
-  systemd: Mitigate /var/tmp type mismatch issue
-  tiff: Fix :term:`LICENSE` (change to libtiff)
-  u-boot.inc: Refactor do_* steps into functions that can be overridden
-  udev-extraconf: Add collect flag to mount
-  unzip: Fix :term:`LICENSE` (change to Info-ZIP)
-  util-linux: Add :term:`PACKAGECONFIG` option (libmount-mountfd-support) to mitigate rootfs remount error
-  vim: upgrade to 9.1.0698
-  weston-init: fix weston not starting when xwayland is enabled
-  wireless-regdb: upgrade to 2024.07.04
-  wpa-supplicant: upgrade to 2.11
-  xserver-xorg: mark :cve_nist:`2023-5574` as unpatched when xvfb enabled
-  yocto-uninative: Update to 4.6 for glibc 2.40
-  zip: Fix :term:`LICENSE` (change to Info-ZIP)


Known Issues in Yocto-5.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alban Bedel
-  Alexander Kanavin
-  Alexis Lothoré
-  Archana Polampalli
-  Ashish Sharma
-  Bartosz Golaszewski
-  Benjamin Szőke
-  Changqing Li
-  Chen Qi
-  Colin McAllister
-  Daniel Semkowicz
-  Dmitry Baryshkov
-  Gauthier HADERER
-  Guðni Már Gilbert
-  Jon Mason
-  Jose Quaresma
-  Jörg Sommer
-  Kai Kang
-  Khem Raj
-  Lee Chee Yang
-  Mark Hatle
-  Martin Jansa
-  Matthias Pritschet
-  Michael Halstead
-  Mingli Yu
-  Niko Mauno
-  Pedro Ferreira
-  Peter Marko
-  Quentin Schulz
-  Richard Purdie
-  Robert Yang
-  Ross Burton
-  Ryan Eatmon
-  Siddharth Doshi
-  Simone Weiß
-  Soumya Sambu
-  Steve Sakoman
-  Trevor Gamblin
-  Ulrich Ölmann
-  Vijay Anusuri
-  Wang Mingyu
-  Weisser, Pascal.ext
-  Yogita Urade


Repositories / Downloads for Yocto-5.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.4 </poky/log/?h=yocto-5.0.4>`
-  Git Revision: :yocto_git:`2034fc38eb4e63984d9bd6b260aa1bf95ce562e4 </poky/commit/?id=2034fc38eb4e63984d9bd6b260aa1bf95ce562e4>`
-  Release Artefact: poky-2034fc38eb4e63984d9bd6b260aa1bf95ce562e4
-  sha: 697ed099793d6c86d5ffe590e96f99689bd28dcb2d4451dc4585496fa4a20400
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.4/poky-2034fc38eb4e63984d9bd6b260aa1bf95ce562e4.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.4/poky-2034fc38eb4e63984d9bd6b260aa1bf95ce562e4.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.4 </openembedded-core/log/?h=yocto-5.0.4>`
-  Git Revision: :oe_git:`f888dd911529a828820799a7a1b75dfd3a44847c </openembedded-core/commit/?id=f888dd911529a828820799a7a1b75dfd3a44847c>`
-  Release Artefact: oecore-f888dd911529a828820799a7a1b75dfd3a44847c
-  sha: 93cb4c3c8e0f77edab20814d155847dc3452c6b083e3dd9c7a801e80a7e4d228
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.4/oecore-f888dd911529a828820799a7a1b75dfd3a44847c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.4/oecore-f888dd911529a828820799a7a1b75dfd3a44847c.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.4 </meta-mingw/log/?h=yocto-5.0.4>`
-  Git Revision: :yocto_git:`acbba477893ef87388effc4679b7f40ee49fc852 </meta-mingw/commit/?id=acbba477893ef87388effc4679b7f40ee49fc852>`
-  Release Artefact: meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852
-  sha: 3b7c2f475dad5130bace652b150367f587d44b391218b1364a8bbc430b48c54c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.4/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.4/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.4 </bitbake/log/?h=yocto-5.0.4>`
-  Git Revision: :oe_git:`d251668d9a7a8dd25bd8767efb30d6d9ff8b1ad3 </bitbake/commit/?id=d251668d9a7a8dd25bd8767efb30d6d9ff8b1ad3>`
-  Release Artefact: bitbake-d251668d9a7a8dd25bd8767efb30d6d9ff8b1ad3
-  sha: d873f4d3a471d26680dc39200d8f3851a6863f15daa9bed978ba31b930f9a1c1
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0.4/bitbake-d251668d9a7a8dd25bd8767efb30d6d9ff8b1ad3.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0.4/bitbake-d251668d9a7a8dd25bd8767efb30d6d9ff8b1ad3.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.4 </yocto-docs/log/?h=yocto-5.0.4>`
-  Git Revision: :yocto_git:`d71081dd14a9d75ace4d1c62472374f37b4a888d </yocto-docs/commit/?id=d71081dd14a9d75ace4d1c62472374f37b4a888d>`

