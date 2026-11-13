.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-6.0.4 (Wrynose)
---------------------------------------

sbom-cve-check has been updated to v1.3.5 and will now generate a "not affected" assessment if a vulnerability is disputed.

Expat has been updated to v2.8.3 to fix CVEs for which the patches were too complex to backport.

The fix for CVE-2026-72693 will change behavior of "openvt -u" when root is involved. See:

https://lore.kernel.org/all/CANQUz1807YxnBxVQMeJn8N1mhC698-NEk_tE8XQqRxwa8ke9Vw@mail.gmail.com/

We merged patches to support stable AUH upgrades on a selected few recipes:

  - babeltrace2
  - binutils
  - dbus
  - git
  - glib-2.0
  - libgcrypt
  - libxml2
  - lttng-modules
  - lttng-tools
  - lttng-ust
  - openssl
  - perl
  - python3
  - sqlite3
  - systemd
  - util-linux
  - xz


Security Fixes in Yocto-6.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  alsa-lib: Fix :cve_nist:`2026-56109`
-  apr-util: Fix :cve_nist:`2025-49506`, :cve_nist:`2026-32327`, :cve_nist:`2026-34191`,
   :cve_nist:`2026-34501` and :cve_nist:`2026-34502`
-  apt: Ignore :cve_nist:`2011-3374`
-  binutils: Fix :cve_nist:`2026-3441`, :cve_nist:`2026-3442`, :cve_nist:`2026-15003` and
   :cve_nist:`2026-18220`
-  bison: Fix :cve_nist:`2026-56389`
-  busybox: Fix :cve_nist:`2026-38754`
-  coreutils: Fix :cve_nist:`2026-56391`
-  cpio: Fix :cve_nist:`2026-66484`, :cve_nist:`2026-66485` and :cve_nist:`2026-66486`
-  curl: Fix :cve_nist:`2026-4873`, :cve_nist:`2026-7009`, :cve_nist:`2026-8286`,
   :cve_nist:`2026-8925`, :cve_nist:`2026-8927`, :cve_nist:`2026-8932`, :cve_nist:`2026-9079`,
   :cve_nist:`2026-9080`, :cve_nist:`2026-9545`, :cve_nist:`2026-9546`, :cve_nist:`2026-11352`,
   :cve_nist:`2026-11586`, :cve_nist:`2026-13608` and :cve_nist:`2026-80229`
-  curl: Ignore :cve_nist:`2026-8458`, :cve_nist:`2026-8924`, :cve_nist:`2026-9547`,
   :cve_nist:`2026-12064` and :cve_nist:`2026-82209`
-  diffutils: Fix :cve_nist:`2026-53910`
-  expat: Ignore :cve_nist:`2026-72522`
-  expat: Fix :cve_nist:`2026-50219`, :cve_nist:`2026-56131`, :cve_nist:`2026-56412` and
   :cve_nist:`2026-72522`
-  ffmpeg: Fix :cve_nist:`2025-69693`, :cve_nist:`2026-8461`, :cve_nist:`2026-30997`,
   :cve_nist:`2026-30998`, :cve_nist:`2026-30999`, :cve_nist:`2026-40962`, :cve_nist:`2026-52295`,
   :cve_nist:`2026-52296`, :cve_nist:`2026-52297`, :cve_nist:`2026-64830`, :cve_nist:`2026-64831`,
   :cve_nist:`2026-64832`, :cve_nist:`2026-64833`, :cve_nist:`2026-64834`, :cve_nist:`2026-64835`,
   :cve_nist:`2026-65703`, :cve_nist:`2026-65704`, :cve_nist:`2026-65705`, :cve_nist:`2026-65706`,
   :cve_nist:`2026-66037`, :cve_nist:`2026-66038` and :cve_nist:`2026-66039`
-  glibc: Fix :cve_nist:`2026-5435`, :cve_nist:`2026-5450`, :cve_nist:`2026-5928`,
   :cve_nist:`2026-6238` and :cve_nist:`2026-6791`
-  gnutls: Fix :cve_nist:`2026-5419`, :cve_nist:`2026-33845`, :cve_nist:`2026-42010` and
   :cve_nist:`2026-42011`
-  go: Fix :cve_nist:`2026-33818`, :cve_nist:`2026-39821`, :cve_nist:`2026-46600`,
   :cve_nist:`2026-56864`, :cve_nist:`2026-56865`, :cve_nist:`2026-56853`, :cve_nist:`2026-56858`,
   :cve_nist:`2026-56859`, :cve_nist:`2026-56860` and :cve_nist:`2026-56862`
-  gstreamer1.0*: Fix :cve_nist:`2026-12891` and :cve_nist:`2026-12892`
-  kbd: Fix :cve_nist:`2026-72693`
-  libarchive: Ignore :cve_nist:`2026-14164`
-  libevent: Ignore :cve_nist:`2026-63380`
-  libpcap: Fix :cve_nist:`2026-0799`, :cve_nist:`2026-6244`, :cve_nist:`2026-6554`,
   :cve_nist:`2026-18238`, :cve_nist:`2026-18313`, :cve_nist:`2026-31911` and :cve_nist:`2026-31912`
-  libsndfile1: Fix :cve_nist:`2026-37555`
-  libsoup: Fix :cve_nist:`2026-4271`
-  libssh2: Fix :cve_nist:`2026-58051`, :cve_nist:`2026-58050`, :cve_nist:`2026-66032`,
   :cve_nist:`2026-66033`, :cve_nist:`2026-66034` and :cve_nist:`2026-66035`
-  libxfont2: Fix :cve_nist:`2026-44950`, :cve_nist:`2026-56001`, :cve_nist:`2026-56002`,
   :cve_nist:`2026-56003` and :cve_nist:`2026-59679`
-  libxfont: Fix :cve_nist:`2026-56001`, :cve_nist:`2026-56002` and :cve_nist:`2026-56003`
-  libxml2: Ignore :cve_nist:`2026-6732`
-  libxml2: Fix :cve_nist:`2026-11979`, :cve_nist:`2026-86137`, :cve_nist:`2026-86138`,
   :cve_nist:`2026-86139`, :cve_nist:`2026-86140`, :cve_nist:`2026-86141`, :cve_nist:`2026-86142`,
   :cve_nist:`2026-86143` and :cve_nist:`2026-86144`
-  nghttp2: Ignore :cve_nist:`2026-58055`
-  openssh: Ignore :cve_nist:`2026-55653`
-  openssl: Fix :cve_nist:`2026-14456`, :cve_nist:`2026-14457`, :cve_nist:`2026-18798`,
   :cve_nist:`2026-54874`, :cve_nist:`2026-63072`, :cve_nist:`2026-63073`, :cve_nist:`2026-63074`,
   :cve_nist:`2026-63075`, :cve_nist:`2026-63076` and :cve_nist:`2026-75803`
-  p11-kit: Fix :cve_nist:`2026-13757` and :cve_nist:`2026-18938`
-  patch: Fix :cve_nist:`2026-56288` and :cve_nist:`2026-56289`
-  perl: Fix :cve_nist:`2026-13221`, :cve_nist:`2026-42496`, :cve_nist:`2026-42497`,
   :cve_nist:`2026-57432` and :cve_nist:`2026-57433`
-  python3-click: Fix :cve_nist:`2026-7246`
-  python3-cryptography(-vectors): Fix :cve_nist:`2026-34073` and :cve_nist:`2026-39892`
-  python3-cryptography: Fix :cve_nist:`2026-69248` and :cve_nist:`2026-69249`
-  python3-git: Fix :cve_nist:`2026-42215`, :cve_nist:`2026-42284`, :cve_nist:`2026-44243` and
   :cve_nist:`2026-44244`
-  python3-idna: Fix :cve_nist:`2026-45409`
-  python3-lxml: Fix :cve_nist:`2026-41066`
-  python3-pip: Fix :cve_nist:`2026-8643` and :cve_nist:`2026-13346`
-  python3-pyasn1: Fix :cve_nist:`2026-30922`, :cve_nist:`2026-59884`, :cve_nist:`2026-59885` and
   :cve_nist:`2026-59886`
-  qemu: Fix :cve_nist:`2026-48914`
-  rpcbind: Fix :cve_nist:`2026-16277`
-  ruby: Fix :cve_nist:`2026-41316` and :cve_nist:`2026-46727`
-  u-boot: Ignore :cve_nist:`2026-33243`
-  u-boot, u-boot-tools: Fix :cve_nist:`2026-46728`
-  u-boot-tools: Ignore :cve_nist:`2026-29007`, :cve_nist:`2026-29008`, :cve_nist:`2026-29009` and
   :cve_nist:`2026-33243`
-  util-linux: Fix :cve_nist:`2026-3184`
-  util-linux: Ignore :cve_nist:`2026-13595`
-  vim: Fix :cve_nist:`2026-28417`, :cve_nist:`2026-52858`, :cve_nist:`2026-55693`,
   :cve_nist:`2026-55895`, :cve_nist:`2026-57451`, :cve_nist:`2026-57453`, :cve_nist:`2026-57454`,
   :cve_nist:`2026-57455`, :cve_nist:`2026-57456`, :cve_nist:`2026-59856`, :cve_nist:`2026-59857`,
   :cve_nist:`2026-59858`, :cve_nist:`2026-73072`, :cve_nist:`2026-73073`, :cve_nist:`2026-73074`,
   :cve_nist:`2026-73076`, :cve_nist:`2026-73077` and :cve_nist:`2026-73078`
-  wget: Fix :cve_nist:`2026-16599`, :cve_nist:`2026-58469`, :cve_nist:`2026-58470`,
   :cve_nist:`2026-58471` and :cve_nist:`2026-58472`
-  wpa-supplicant: Fix :cve_nist:`2026-58374`


Fixes in Yocto-6.0.4
~~~~~~~~~~~~~~~~~~~~

bitbake
-----------
-  asyncrpc: Close the client event loop when the client is collected
-  bb/tests/fetch.py: add case for filter_regex parameter in latest_versionstring
-  data: Fix expandKeys recursion issue
-  data: Return a list from exported_vars()
-  doc: bitbake-user-manual-environment-setup: use pip to install bitbake-setup
-  fetch/gitsm: Store the original url data to fix relative gitsm paths
-  fetch/wget: Handle long filenames correctly
-  fetch2/__init__.py: add filter_regex to default latest_versionstring
-  fetch2/crate.py: add filter_regex parameter to latest_versionstring
-  fetch2/git.py: add filter_regex parameter to latest_versionstring
-  fetch2/wget.py: add filter_regex parameter to latest_versionstring
-  runqueue: Fix pending_hash_index not including taskname
-  runqueue: fix bad check in process_possible_migrations
-  runqueue: wait for covered tasks before setscene
-  tests/fetch: Drop ftp checkstatus test
-  tests/fetch: Swap bitbake github mirror for YP one
-  tests/fetch: Swap github grpc repo for our own small test repo
-  tests/fetch: Switch to YP mirror repos instead of github
-  toastergui/views: Replace os.system call with improved parameter handling

oecore
-----------
-  apr-util: upgrade to 1.6.5
-  at-spi2-core: :term:`RDEPENDS` on gsettings-desktop-schemas
-  babeltrace2: inherit upstream-stable-release-point
-  binutils: inherit upstream-stable-release-point
-  binutils: stable 2.46 branch updates
-  bitbake.conf: pseudo add rootfs-dbg to :term:`PSEUDO_INCLUDE_PATHS`
-  bluez5: restrict delta=0 RSSI to proximity filters
-  build-appliance-image: Update to wrynose head revisions
-  ca-certificates: upgrade 20260601 -> 20260816
-  cmake-native: use bundled nghttp2 with bundled curl
-  connman: remove connection_manager from CVE_PRODUCTS
-  dbus: inherit upstream-stable-release-point
-  devtool/upgrade.py: add --stable option
-  dhcpcd: upgrade to 10.3.2
-  expat: upgrade to 2.8.3
-  ffmpeg: upgrade to 8.0.3
-  fmt: update branch name to main
-  gawk: skip randtest in ptest suite
-  git: inherit upstream-stable-release-point
-  glib-2.0: inherit upstream-stable-release-point
-  glibc: stable 2.43 branch updates
-  go: upgrade to 1.26.8
-  golang: fix homepage
-  grub: disable grub-protect for native builds
-  gst-devtools: upgrade to 1.28.5
-  gstreamer1.0*: upgrade to 1.28.5
-  gstreamer1.0: disable an intermittently failing test
-  improve_kernel_cve_report: Fix backported-patch check
-  kernel-fit-image: Don't add hash node when signing is enabled
-  kernel-yocto-rust: Add clang toolchain check for riscv64
-  kern-tools: bump :term:`SRCREV` for conditional 'depends on X if Y' support
-  lib/oe/recipeutils: make stable_upgrade argument optional in get_recipe_upstream_version()
-  libevent: upgrade to 2.1.13
-  libgcrypt: inherit upstream-stable-release-point
-  libslirp: add tag in :term:`SRC_URI`
-  libslirp: fix upstream version check
-  libslirp: upgrade to 4.9.3
-  libxml2: inherit upstream-stable-release-point
-  libxml2: upgrade to 2.15.4
-  linux-yocto/6.18: fix kernel reproducibility issues
-  linux-yocto/6.18: update to v6.18.52
-  lttng-modules: inherit upstream-stable-release-point
-  lttng-tools: inherit upstream-stable-release-point
-  lttng-ust: inherit upstream-stable-release-point
-  mesa: align x86 mesa config with LLVM graphics
-  mirrors: Disable YP mirrors on autobuilder
-  oeqa/maturin: Update dependency version
-  openssl: inherit upstream-stable-release-point
-  openssl: upgrade to 3.5.8
-  perl: inherit upstream-stable-release-point
-  procps: ptest: skip flaky pgrep full process name match test
-  pseudo: upgrade to 1.9.11
-  pseudo: Add in openat2, exec and linkat fixes
-  pseudo: Update to 1.9.10
-  python3: add missing pyc files to core
-  python3: fix stringold cache files
-  python3: inherit upstream-stable-release-point
-  python3: upgrade to 3.14.7
-  python3-attrs: Fix :term:`CVE_PRODUCT`
-  python3-babel: Fix :term:`CVE_PRODUCT`
-  python3-certifi: fix homepage
-  python3-click: Fix :term:`CVE_PRODUCT`
-  python3-click: upgrade to 8.3.3
-  python3-cryptography(-vectors): upgrade to 46.0.7
-  python3-cryptography: backport stray file install fix
-  python3-dbusmock: Fix :term:`CVE_PRODUCT`
-  python3-git: Fix :term:`CVE_PRODUCT`
-  python3-mako: correct :term:`CVE_PRODUCT` mapping
-  python3-mako: upgrade to 1.3.12
-  python3-numpy: Fix :term:`CVE_PRODUCT`
-  python3-pyasn1: upgrade to 0.6.4
-  python3-pycryptodome: Fix :term:`CVE_PRODUCT`
-  python3-pycryptodomex: Fix :term:`CVE_PRODUCT`
-  python3-sbom-cve-check: update to version 1.3.5
-  python3-shacl2code: upgrade to 1.1.0
-  python3-spdx-python-model: upgrade to 0.0.6
-  python3-wheel: Fix :term:`CVE_PRODUCT`
-  qemu: guard RESOLVE_CACHED strace flag
-  recipeutils: add optional stable_upgrade parameter to get_recipe_upgrade_status
-  rootfs.py: fix run-postinsts removal on multilib images
-  rpcbind: Drop dependency on quota
-  rpcbind: upgrade to 1.2.9
-  ruby: upgrade to 4.0.5
-  sbom-cve-check-update-{nvd-native,cvelist-native}: upgrade to 2026.09.12
-  sbom-cve-check-update-db: Exclude recipes from rm_work
-  scripts/install-buildtools: Update to 6.0.3
-  sqlite3: inherit upstream-stable-release-point
-  sstate: Update mtime/atime for sig/siginfo files in sstate_checkhashes()
-  sstate: Update unpack touch code to be consistent
-  systemd: inherit upstream-stable-release-point
-  testimage: handle bootlog variants on failed qemu tests
-  time64: enable 64-bit time/file-offset flags for 32-bit nativesdk
-  toolchain-scripts: fix kernel host tool builds broken in the SDK
-  u-boot: share :term:`CVE_PRODUCT` with u-boot-tools
-  upstream-stable-release-point.bbclass: add bbclass for stable point upgrade
-  util-linux: inherit upstream-stable-release-point
-  volatile-binds: order systemd-timesyncd after /var/lib
-  wireless-regdb: upgrade to 2026.09.03
-  xserver-xorg: upgrade to 21.1.24
-  xwayland: upgrade to 24.1.13
-  xz: inherit upstream-stable-release-point


meta-yocto
-----------
-  poky.conf: Bump version for 6.0.4 release
-  yocto-bsps: update to v6.18.52

yocto-docs
-----------
-  README: add references to recent security documents
-  README: update instructions for installing vale and sphinx-lint
-  bitbake.rst: rename section to "BitBake User Manual"
-  bsp-manual/bsp.rst: fix a "local.conf" -> "layer.conf" typo
-  contributor-guide/identify-component.rst: use the same tense as other section
-  dev-manual/poky-manual-setup.rst: suggest use of fragment instead of assignments
-  dev-manual/wic.rst: remove local.conf mentions
-  dev-manual: correct :term:`OVERRIDES` to :term:`FILESOVERRIDES` for :term:`FILESPATH`
-  dev-manual: drop link to out-of-date wiki page
-  dev-manual: quote the values in two recipe examples
-  docs-wide: fix manual names in cross references
-  documentation: use "https" protocol for git.yoctoproject.org
-  index.rst: add introduction paragraphs
-  index.rst: move external links to the bottom of the intro section
-  index.rst: move release notes in their own section
-  index.rst: move the "Overview and Concepts" manual in the "Introduction and Overview" section
-  index.rst: move the contributor guide out of the Manuals section
-  index.rst: show the intro paragraph only for html-based docs
-  migration-guide: add release notes for 5.0.20 6.0.3
-  overview-manual: move the intro content in the index
-  ref-manual/classes.rst: buildstats: remove local.conf default value
-  ref-manual/features.rst: add tools-profile
-  ref-manual/features.rst: fix some cross references
-  ref-manual/structure.rst: fix a "local.conf" -> "layer.conf" typo
-  ref-manual/system-requirements.rst: drop ccache in distros
-  ref-manual/variables.rst: BBMULTICONFIG: fix a typo
-  ref-manual/variables.rst: Document :term:`SPDX_AUTHORS`
-  ref-manual/variables.rst: Fix the documentation for the SPDX agent variables
-  ref-manual/variables.rst: document the :term:`PACKAGEFUNCS` :term:`PACKAGE_NO_LOCALE` variable
-  ref-manual/variables.rst: fix local.conf.extended obsolete path
-  ref-manual/variables.rst: mention built-in fragments for MACHINE/DISTRO
-  ref-manual/variables.rst: GCCVERSION: replace local.conf and mention recipe existence
-  ref-manual/varlocality.rst: mention built-in fragments for distro/machine
-  ref-manual/varlocality.rst: remove :term:`BBFILES` from local configuration assignments
-  ref-manual: drop the full stop from the :term:`QB_SMP` example
-  ref-manual: fix a typo in the :term:`QB_SMP` description
-  ref-manual: update DEBUG_OPTIMIZATION/FULL_OPTIMIZATION
-  security-manual/securing-images.rst: remove "your" in "your OE-Core"
-  toaster-manual: spell the json code-block language in lower case


Known Issues in Yocto-6.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-6.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~


Thanks to the following people who contributed to this release:

-  Abhishek Bachiphale
-  Adarsh Jagadish Kamini
-  Adrian Freihofer
-  Alejandro Mery
-  Alexander Kanavin
-  Andrei Lalaev
-  Ankur Tyagi
-  Antonin Godard
-  AshishKumar Mishra
-  Benjamin Robin (Schneider Electric)
-  Bhavesh R Maheshwari
-  Bruce Ashfield
-  Chen Qi
-  Chris Laplante
-  Daiane Angolini
-  Daniel Dragomir
-  Daniel Turull
-  Darsh Kelaiya
-  Deepak Rathore
-  Devansh Patel
-  Esa Jaaskela
-  Francisco Pedraza
-  Ghanshyam Banait
-  Harish Sadineni
-  Hetvi Thakar
-  Hiago De Franco
-  Himani Ramesh Barde
-  Himanshu Jadon
-  Hitendra Prajapati
-  Jaipaul Cheernam
-  Jakub Szczudlo (Nokia)
-  Jonas Juffinger
-  Jose Quaresma
-  Joshua Watt
-  Kyungjik Min
-  Lee Chee Yang
-  Leonid Iziumtsev
-  Michal Sieron
-  Paul Barker
-  Peter Marko
-  Peter Tatrai
-  Pratik Farkase
-  Quentin Schulz
-  Richard Purdie
-  Robert P. J. Day
-  Roopa Kalmath
-  Ross Burton
-  Siddharth Doshi
-  Sowmya Sathram
-  Sudhir Dumbhare
-  Tafil Avdyli
-  Tim Orling
-  Trevor Woerner
-  Vijay Anusuri
-  Wang Mingyu
-  Xiuzhuo Shang
-  Yoann Congal

Repositories / Downloads for Yocto-6.0.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`wrynose </yocto-docs/log/?h=wrynose>`
-  Tag:  :yocto_git:`yocto-6.0.4 </yocto-docs/log/?h=yocto-6.0.4>`
-  Git Revision: :yocto_git:`a2cc1f5b86f3cdc9ad3257a2545322c5169da3ca </yocto-docs/commit/?id=a2cc1f5b86f3cdc9ad3257a2545322c5169da3ca>`
-  Release Artefact: yocto-docs-a2cc1f5b86f3cdc9ad3257a2545322c5169da3ca
-  sha: 1addfdd9f0f9a98a325102aabd24e221173f42dd1e62b8aa2d0b5f224e25d21a
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.4/yocto-docs-a2cc1f5b86f3cdc9ad3257a2545322c5169da3ca.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.4/yocto-docs-a2cc1f5b86f3cdc9ad3257a2545322c5169da3ca.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`wrynose </openembedded-core/log/?h=wrynose>`
-  Tag:  :oe_git:`yocto-6.0.4 </openembedded-core/log/?h=yocto-6.0.4>`
-  Git Revision: :oe_git:`0f03c732a0a2d0506b41b1abe60b3f368b2300dc </openembedded-core/commit/?id=0f03c732a0a2d0506b41b1abe60b3f368b2300dc>`
-  Release Artefact: oecore-0f03c732a0a2d0506b41b1abe60b3f368b2300dc
-  sha: a35af9fd6d814ee2fbe21bbcb8de19cdf63fb2c5c78e8f5badf91dfc723a7748
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.4/oecore-0f03c732a0a2d0506b41b1abe60b3f368b2300dc.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.4/oecore-0f03c732a0a2d0506b41b1abe60b3f368b2300dc.tar.bz2

mixin

-  Repository Location: :yocto_git:`/mixin`
-  Branch: :yocto_git:`wrynose </mixin/log/?h=wrynose>`
-  Tag:  :yocto_git:`yocto-6.0.4 </mixin/log/?h=yocto-6.0.4>`
-  Git Revision: :yocto_git:`71dfb81bd98f59ca2127d39b100ecbb8ce1e0ba3 </mixin/commit/?id=71dfb81bd98f59ca2127d39b100ecbb8ce1e0ba3>`
-  Release Artefact: mixin-wrynose-linux-firmware-71dfb81bd98f59ca2127d39b100ecbb8ce1e0ba3
-  sha: 98ff4a72ec37e826f2b9cb01586bd1d40c20237a8ebf0ab2d3e68a4a475880e2
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.4/mixin-wrynose-linux-firmware-71dfb81bd98f59ca2127d39b100ecbb8ce1e0ba3.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.4/mixin-wrynose-linux-firmware-71dfb81bd98f59ca2127d39b100ecbb8ce1e0ba3.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`wrynose </meta-yocto/log/?h=wrynose>`
-  Tag:  :yocto_git:`yocto-6.0.4 </meta-yocto/log/?h=yocto-6.0.4>`
-  Git Revision: :yocto_git:`7c25b11156553145fa6b241a2d4f65d94438f804 </meta-yocto/commit/?id=7c25b11156553145fa6b241a2d4f65d94438f804>`
-  Release Artefact: meta-yocto-7c25b11156553145fa6b241a2d4f65d94438f804
-  sha: 411c783757cd2931e407dff408248b6b15f4508e4bf7a9ceedec13c0e7874b84
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.4/meta-yocto-7c25b11156553145fa6b241a2d4f65d94438f804.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.4/meta-yocto-7c25b11156553145fa6b241a2d4f65d94438f804.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.18 </bitbake/log/?h=2.18>`
-  Tag:  :oe_git:`yocto-6.0.4 </bitbake/log/?h=yocto-6.0.4>`
-  Git Revision: :oe_git:`51fd94a67fa17650a9ff623492005972bbc7d26f </bitbake/commit/?id=51fd94a67fa17650a9ff623492005972bbc7d26f>`
-  Release Artefact: bitbake-51fd94a67fa17650a9ff623492005972bbc7d26f
-  sha: a541a9535cca6e342151e8e7b0c11f322dab879317b32e71bd47490c28af8b7d
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.0.4/bitbake-51fd94a67fa17650a9ff623492005972bbc7d26f.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.0.4/bitbake-51fd94a67fa17650a9ff623492005972bbc7d26f.tar.bz2

