.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.3.2 (Nanbield)
----------------------------------------

Security Fixes in Yocto-4.3.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2023-1981`, :cve_nist:`2023-38469`, :cve_nist:`2023-38470`, :cve_nist:`2023-38471`, :cve_nist:`2023-38472` and :cve_nist:`2023-38473`
-  curl: Fix :cve_nist:`2023-46218`
-  ghostscript: Fix :cve_nist:`2023-46751`
-  grub: fix :cve_nist:`2023-4692` and :cve_nist:`2023-4693`
-  gstreamer1.0: Fix :cve_mitre:`2023-44446`
-  linux-yocto/6.1: Ignore :cve_mitre:`2023-39197`, :cve_nist:`2023-39198`, :cve_nist:`2023-5090`, :cve_nist:`2023-5633`, :cve_nist:`2023-6111`, :cve_nist:`2023-6121` and :cve_nist:`2023-6176`
-  linux-yocto/6.5: Ignore :cve_nist:`2022-44034`, :cve_mitre:`2023-39197`, :cve_nist:`2023-39198`, :cve_nist:`2023-5972`, :cve_nist:`2023-6039`, :cve_nist:`2023-6111` and :cve_nist:`2023-6176`
-  perl: fix :cve_nist:`2023-47100`
-  python3-urllib3: Fix :cve_nist:`2023-45803`
-  rust: Fix :cve_nist:`2023-40030`
-  vim: Fix :cve_nist:`2023-48231`, :cve_nist:`2023-48232`, :cve_nist:`2023-48233`, :cve_nist:`2023-48234`, :cve_nist:`2023-48235`, :cve_nist:`2023-48236` and :cve_nist:`2023-48237`
-  xserver-xorg: Fix :cve_nist:`2023-5367` and :cve_nist:`2023-5380`
-  xwayland: Fix :cve_nist:`2023-5367`


Fixes in Yocto-4.3.2
~~~~~~~~~~~~~~~~~~~~

-  base-passwd: Upgrade to 3.6.2
-  bind: Upgrade to 9.18.20
-  binutils: stable 2.41 branch updates
-  bitbake: command: Make parseRecipeFile() handle virtual recipes correctly
-  bitbake: lib/bb: Add workaround for libgcc issues with python 3.8 and 3.9
-  bitbake: toastergui: verify that an existing layer path is given
-  bluez5: fix connection for ps5/dualshock controllers
-  build-appliance-image: Update to nanbield head revision
-  cmake: Upgrade to 3.27.7
-  contributor-guide: add License-Update tag
-  contributor-guide: fix command option
-  cups: Add root,sys,wheel to system groups
-  cve-update-nvd2-native: faster requests with API keys
-  cve-update-nvd2-native: increase the delay between subsequent request failures
-  cve-update-nvd2-native: make number of fetch attemtps configurable
-  cve-update-nvd2-native: remove unused variable CVE_SOCKET_TIMEOUT
-  dev-manual: Discourage the use of SRC_URI[md5sum]
-  dev-manual: layers: update link to YP Compatible form
-  dev-manual: runtime-testing: fix test module name
-  devtool: finish/update-recipe: restrict mode srcrev to recipes fetched from SCM
-  devtool: fix update-recipe dry-run mode
-  ell: Upgrade to 0.60
-  enchant2: Upgrade to 2.6.2
-  ghostscript: Upgrade to 10.02.1
-  glib-2.0: Upgrade to 2.78.1
-  glibc: stable 2.38 branch updates
-  gstreamer1.0: Upgrade to 1.22.7
-  gtk: Add rdepend on printbackend for cups
-  harfbuzz: Upgrade to 8.2.2
-  json-c: fix icecc compilation
-  kern-tools: bump :term:`SRCREV` for queue processing changes
-  kern-tools: make lower context patches reproducible
-  kern-tools: update :term:`SRCREV` to include SECURITY.md file
-  kernel-arch: use ccache only for compiler
-  kernel-yocto: improve metadata patching
-  lib/oe/buildcfg.py: Include missing import
-  lib/oe/buildcfg.py: Remove unused parameter
-  lib/oe/patch: ensure os.chdir restoring always happens
-  lib/oe/path: Deploy files can start only with a dot
-  libgcrypt: Upgrade to 1.10.3
-  libjpeg-turbo: Upgrade to 3.0.1
-  libnewt: Upgrade to 0.52.24
-  libnsl2: Upgrade to 2.0.1
-  libsolv: Upgrade to 0.7.26
-  libxslt: Upgrade to 1.1.39
-  linux-firmware: add audio topology symlink to the X13's audio package
-  linux-firmware: add missing depenencies on license packages
-  linux-firmware: add new fw file to ${PN}-rtl8821
-  linux-firmware: add notice file to sdm845 modem firmware
-  linux-firmware: create separate packages
-  linux-firmware: package Qualcomm Venus 6.0 firmware
-  linux-firmware: package Robotics RB5 sensors DSP firmware
-  linux-firmware: package firmware for Qualcomm Adreno a702
-  linux-firmware: package firmware for Qualcomm QCM2290 / QRB4210
-  linux-firmware: Upgrade to 20231030
-  linux-yocto-rt/6.1: update to -rt18
-  linux-yocto/6.1: cfg: restore CONFIG_DEVMEM
-  linux-yocto/6.1: drop removed IMA option
-  linux-yocto/6.1: Upgrade to v6.1.68
-  linux-yocto/6.5: cfg: restore CONFIG_DEVMEM
-  linux-yocto/6.5: cfg: split runtime and symbol debug
-  linux-yocto/6.5: drop removed IMA option
-  linux-yocto/6.5: fix AB-INT: QEMU kernel panic: No irq handler for vector
-  linux-yocto/6.5: Upgrade to v6.5.13
-  linux/cve-exclusion6.1: Update to latest kernel point release
-  log4cplus: Upgrade to 2.1.1
-  lsb-release: use https for :term:`UPSTREAM_CHECK_URI`
-  manuals: brief-yoctoprojectqs: align variable order with default local.conf
-  manuals: fix URL
-  meson: use correct targets for rust binaries
-  migration-guide: add release notes for 4.0.14, 4.0.15, 4.2.4, 4.3.1
-  migration-guides: release 3.5 is actually 4.0
-  migration-guides: reword fix in release-notes-4.3.1
-  msmtp: Upgrade to 1.8.25
-  oeqa/selftest/tinfoil: Add tests that parse virtual recipes
-  openssl: improve handshake test error reporting
-  package_ipk: Fix Source: field variable dependency
-  patchtest: shorten patch signed-off-by test output
-  perf: lift :term:`TARGET_CC_ARCH` modification out of security_flags.inc
-  perl: Upgrade to 5.38.2
-  perlcross: Upgrade to 1.5.2
-  poky.conf: bump version for 4.3.2 release
-  python3-ptest: skip test_storlines
-  python3-urllib3: Upgrade to 2.0.7
-  qemu: Upgrade to 8.1.2
-  ref-manual: Fix reference to MIRRORS/PREMIRRORS defaults
-  ref-manual: releases.svg: update nanbield release status
-  useradd_base: sed -i destroys symlinks
-  rootfs-postcommands: sed -i destroys symlinks
-  sstate: Ensure sstate searches update file mtime
-  strace: backport fix for so_peerpidfd-test
-  systemd-boot: Fix build issues on armv7a-linux
-  systemd-compat-units.bb: fix postinstall script
-  systemd: fix DynamicUser issue
-  systemd: update :term:`LICENSE` statement
-  tcl: skip async and event tests in run-ptest
-  tcl: skip timing-dependent tests in run-ptest
-  test-manual: add links to python unittest
-  test-manual: add or improve hyperlinks
-  test-manual: explicit or fix file paths
-  test-manual: resource updates
-  test-manual: text and formatting fixes
-  test-manual: use working example
-  testimage: Drop target_dumper and most of monitor_dumper
-  testimage: Exclude wtmp from target-dumper commands
-  tzdata: Upgrade to 2023d
-  update_gtk_icon_cache: Fix for GTK4-only builds
-  useradd_base: Fix sed command line for passwd-expire
-  vim: Upgrade to 9.0.2130
-  xserver-xorg: Upgrade to 21.1.9
-  xwayland: Upgrade to 23.2.2


Known Issues in Yocto-4.3.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-4.3.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Adam Johnston
-  Alexander Kanavin
-  Anuj Mittal
-  Bastian Krause
-  Bruce Ashfield
-  Chen Qi
-  Deepthi Hemraj
-  Dhairya Nagodra
-  Dmitry Baryshkov
-  Fahad Arslan
-  Javier Tia
-  Jermain Horsman
-  Joakim Tjernlund
-  Julien Stephan
-  Justin Bronder
-  Khem Raj
-  Lee Chee Yang
-  Marco Felsch
-  Markus Volk
-  Marta Rybczynska
-  Massimiliano Minella
-  Michael Opdenacker
-  Paul Barker
-  Peter Kjellerstedt
-  Peter Marko
-  Randy MacLeod
-  Rasmus Villemoes
-  Richard Purdie
-  Ross Burton
-  Shubham Kulkarni
-  Simone Weiß
-  Steve Sakoman
-  Sundeep KOKKONDA
-  Tim Orling
-  Trevor Gamblin
-  Vijay Anusuri
-  Viswanath Kraleti
-  Vyacheslav Yurkov
-  Wang Mingyu
-  William Lyu
-  Zoltán Böszörményi

Repositories / Downloads for Yocto-4.3.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`nanbield </poky/log/?h=nanbield>`
-  Tag:  :yocto_git:`yocto-4.3.2 </poky/log/?h=yocto-4.3.2>`
-  Git Revision: :yocto_git:`f768ffb8916feb6542fcbe3e946cbf30e247b151 </poky/commit/?id=f768ffb8916feb6542fcbe3e946cbf30e247b151>`
-  Release Artefact: poky-f768ffb8916feb6542fcbe3e946cbf30e247b151
-  sha: 21ca1695d70aba9b4bd8626d160111feab76206883cd14fe41eb024692bdfd7b
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.2/poky-f768ffb8916feb6542fcbe3e946cbf30e247b151.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.2/poky-f768ffb8916feb6542fcbe3e946cbf30e247b151.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`nanbield </openembedded-core/log/?h=nanbield>`
-  Tag:  :oe_git:`yocto-4.3.2 </openembedded-core/log/?h=yocto-4.3.2>`
-  Git Revision: :oe_git:`ff595b937d37d2315386aebf315cea719e2362ea </openembedded-core/commit/?id=ff595b937d37d2315386aebf315cea719e2362ea>`
-  Release Artefact: oecore-ff595b937d37d2315386aebf315cea719e2362ea
-  sha: a7c6332dc0e09ecc08221e78b11151e8e2a3fd9fa3eaad96a4c03b67012bfb97
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.2/oecore-ff595b937d37d2315386aebf315cea719e2362ea.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.2/oecore-ff595b937d37d2315386aebf315cea719e2362ea.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`nanbield </meta-mingw/log/?h=nanbield>`
-  Tag:  :yocto_git:`yocto-4.3.2 </meta-mingw/log/?h=yocto-4.3.2>`
-  Git Revision: :yocto_git:`49617a253e09baabbf0355bc736122e9549c8ab2 </meta-mingw/commit/?id=49617a253e09baabbf0355bc736122e9549c8ab2>`
-  Release Artefact: meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2
-  sha: 2225115b73589cdbf1e491115221035c6a61679a92a93b2a3cf761ff87bf4ecc
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.2/meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.2/meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.6 </bitbake/log/?h=2.6>`
-  Tag:  :oe_git:`yocto-4.3.2 </bitbake/log/?h=yocto-4.3.2>`
-  Git Revision: :oe_git:`72bf75f0b2e7f36930185e18a1de8277ce7045d8 </bitbake/commit/?id=72bf75f0b2e7f36930185e18a1de8277ce7045d8>`
-  Release Artefact: bitbake-72bf75f0b2e7f36930185e18a1de8277ce7045d8
-  sha: 0b6ccd4796ccd211605090348a3d4378358c839ae1bb4c35964d0f36f2663187
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.2/bitbake-72bf75f0b2e7f36930185e18a1de8277ce7045d8.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.2/bitbake-72bf75f0b2e7f36930185e18a1de8277ce7045d8.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`nanbield </yocto-docs/log/?h=nanbield>`
-  Tag: :yocto_git:`yocto-4.3.2 </yocto-docs/log/?h=yocto-4.3.2>`
-  Git Revision: :yocto_git:`fac88b9e80646a68b31975c915a718a9b6b2b439 </yocto-docs/commit/?id=fac88b9e80646a68b31975c915a718a9b6b2b439>`

