.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.1.1 (Styhead)
---------------------------------------

Security Fixes in Yocto-5.1.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  go: Fix :cve_nist:`2024-34155`, :cve_nist:`2024-34156` and :cve_nist:`2024-34158`
-  linux-yocto/6.6: Fix :cve_nist:`2023-52917`, :cve_nist:`2024-46735`, :cve_nist:`2024-46737`, :cve_nist:`2024-46738`, :cve_nist:`2024-46739`, :cve_nist:`2024-46740`, :cve_nist:`2024-46741`, :cve_nist:`2024-46742`, :cve_nist:`2024-46743`, :cve_nist:`2024-46744`, :cve_nist:`2024-46746`, :cve_nist:`2024-46747`, :cve_nist:`2024-46749`, :cve_nist:`2024-46750`, :cve_nist:`2024-46755`, :cve_nist:`2024-46756`, :cve_nist:`2024-46757`, :cve_nist:`2024-46758`, :cve_nist:`2024-46759`, :cve_nist:`2024-46760`, :cve_nist:`2024-46761`, :cve_nist:`2024-46762`, :cve_nist:`2024-46763`, :cve_nist:`2024-46765`, :cve_nist:`2024-46768`, :cve_nist:`2024-46770`, :cve_nist:`2024-46771`, :cve_nist:`2024-46773`, :cve_nist:`2024-46776`, :cve_nist:`2024-46777`, :cve_nist:`2024-46780`, :cve_nist:`2024-46781`, :cve_nist:`2024-46782`, :cve_nist:`2024-46783`, :cve_nist:`2024-46784`, :cve_nist:`2024-46785`, :cve_nist:`2024-46786`, :cve_nist:`2024-46787`, :cve_nist:`2024-46788`, :cve_nist:`2024-46791`, :cve_nist:`2024-46794`, :cve_nist:`2024-46795`, :cve_nist:`2024-46796`, :cve_nist:`2024-46797`, :cve_nist:`2024-46798`, :cve_nist:`2024-46800`, :cve_nist:`2024-46822`, :cve_nist:`2024-46825`, :cve_nist:`2024-46826`, :cve_nist:`2024-46827`, :cve_nist:`2024-46828`, :cve_nist:`2024-46829`, :cve_nist:`2024-46830`, :cve_nist:`2024-46831`, :cve_nist:`2024-46832`, :cve_nist:`2024-46835`, :cve_nist:`2024-46836`, :cve_nist:`2024-46838`, :cve_nist:`2024-46840`, :cve_nist:`2024-46843`, :cve_nist:`2024-46844`, :cve_nist:`2024-46845`, :cve_nist:`2024-46846`, :cve_nist:`2024-46847`, :cve_nist:`2024-46848`, :cve_nist:`2024-46849`, :cve_nist:`2024-46852`, :cve_nist:`2024-46853`, :cve_nist:`2024-46854`, :cve_nist:`2024-46855`, :cve_nist:`2024-46857`, :cve_nist:`2024-46858`, :cve_nist:`2024-46859`, :cve_nist:`2024-46860`, :cve_nist:`2024-46861`, :cve_nist:`2024-46863`, :cve_nist:`2024-46864`, :cve_nist:`2024-47663`, :cve_nist:`2024-47664`, :cve_nist:`2024-47665`, :cve_nist:`2024-47666`, :cve_nist:`2024-47667`, :cve_nist:`2024-47668`, :cve_nist:`2024-47669`, :cve_nist:`2024-47670`, :cve_nist:`2024-47671`, :cve_nist:`2024-47672`, :cve_nist:`2024-47673`, :cve_nist:`2024-47674`, :cve_nist:`2024-47675`, :cve_nist:`2024-47678`, :cve_nist:`2024-47679`, :cve_nist:`2024-47681`, :cve_nist:`2024-47682`, :cve_nist:`2024-47683`, :cve_nist:`2024-47684`, :cve_nist:`2024-47685`, :cve_nist:`2024-47686`, :cve_nist:`2024-47688`, :cve_nist:`2024-47689`, :cve_nist:`2024-47690`, :cve_nist:`2024-47691`, :cve_nist:`2024-47692`, :cve_nist:`2024-47693`, :cve_nist:`2024-47695`, :cve_nist:`2024-47696`, :cve_nist:`2024-47697`, :cve_nist:`2024-47698`, :cve_nist:`2024-47699`, :cve_nist:`2024-47700`, :cve_nist:`2024-47701`, :cve_nist:`2024-47705`, :cve_nist:`2024-47706`, :cve_nist:`2024-47707`, :cve_nist:`2024-47710`, :cve_nist:`2024-47712`, :cve_nist:`2024-47713`, :cve_nist:`2024-47714`, :cve_nist:`2024-47715`, :cve_nist:`2024-47716`, :cve_nist:`2024-47718`, :cve_nist:`2024-47719`, :cve_nist:`2024-47720`, :cve_nist:`2024-47723`, :cve_nist:`2024-47727`, :cve_nist:`2024-47728`, :cve_nist:`2024-47730`, :cve_nist:`2024-47731`, :cve_nist:`2024-47734`, :cve_nist:`2024-47735`, :cve_nist:`2024-47737`, :cve_nist:`2024-47738`, :cve_nist:`2024-47739`, :cve_nist:`2024-47741`, :cve_nist:`2024-47742`, :cve_nist:`2024-47743`, :cve_nist:`2024-47744`, :cve_nist:`2024-47745`, :cve_nist:`2024-47747`, :cve_nist:`2024-47748`, :cve_nist:`2024-47749`, :cve_nist:`2024-47750`, :cve_nist:`2024-47751`, :cve_nist:`2024-47752`, :cve_nist:`2024-47753`, :cve_nist:`2024-47754`, :cve_nist:`2024-47757`, :cve_nist:`2024-49850`, :cve_nist:`2024-49851`, :cve_nist:`2024-49852`, :cve_nist:`2024-49853`, :cve_nist:`2024-49854`, :cve_nist:`2024-49855`, :cve_nist:`2024-49856`, :cve_nist:`2024-49858`, :cve_nist:`2024-49859`, :cve_nist:`2024-49860`, :cve_nist:`2024-49861` and :cve_nist:`2024-49862`
-  linux-yocto/6.10: Fix :cve_nist:`2023-52917`, :cve_nist:`2024-46714`, :cve_nist:`2024-46719`, :cve_nist:`2024-46720`, :cve_nist:`2024-46721`, :cve_nist:`2024-46722`, :cve_nist:`2024-46723`, :cve_nist:`2024-46724`, :cve_nist:`2024-46725`, :cve_nist:`2024-46726`, :cve_nist:`2024-46727`, :cve_nist:`2024-46728`, :cve_nist:`2024-46730`, :cve_nist:`2024-46731`, :cve_nist:`2024-46732`, :cve_nist:`2024-46735`, :cve_nist:`2024-46737`, :cve_nist:`2024-46738`, :cve_nist:`2024-46739`, :cve_nist:`2024-46740`, :cve_nist:`2024-46741`, :cve_nist:`2024-46742`, :cve_nist:`2024-46743`, :cve_nist:`2024-46744`, :cve_nist:`2024-46746`, :cve_nist:`2024-46747`, :cve_nist:`2024-46749`, :cve_nist:`2024-46750`, :cve_nist:`2024-46751`, :cve_nist:`2024-46755`, :cve_nist:`2024-46756`, :cve_nist:`2024-46757`, :cve_nist:`2024-46758`, :cve_nist:`2024-46759`, :cve_nist:`2024-46760`, :cve_nist:`2024-46761`, :cve_nist:`2024-46762`, :cve_nist:`2024-46763`, :cve_nist:`2024-46765`, :cve_nist:`2024-46766`, :cve_nist:`2024-46768`, :cve_nist:`2024-46769`, :cve_nist:`2024-46770`, :cve_nist:`2024-46771`, :cve_nist:`2024-46772`, :cve_nist:`2024-46773`, :cve_nist:`2024-46774`, :cve_nist:`2024-46775`, :cve_nist:`2024-46776`, :cve_nist:`2024-46777`, :cve_nist:`2024-46778`, :cve_nist:`2024-46779`, :cve_nist:`2024-46780`, :cve_nist:`2024-46781`, :cve_nist:`2024-46782`, :cve_nist:`2024-46783`, :cve_nist:`2024-46784`, :cve_nist:`2024-46785`, :cve_nist:`2024-46786`, :cve_nist:`2024-46787`, :cve_nist:`2024-46788`, :cve_nist:`2024-46789`, :cve_nist:`2024-46790`, :cve_nist:`2024-46791`, :cve_nist:`2024-46792`, :cve_nist:`2024-46793`, :cve_nist:`2024-46794`, :cve_nist:`2024-46795`, :cve_nist:`2024-46796`, :cve_nist:`2024-46797`, :cve_nist:`2024-46798`, :cve_nist:`2024-46799`, :cve_nist:`2024-46800`, :cve_nist:`2024-46801`, :cve_nist:`2024-46802`, :cve_nist:`2024-46803`, :cve_nist:`2024-46804`, :cve_nist:`2024-46805`, :cve_nist:`2024-46806`, :cve_nist:`2024-46807`, :cve_nist:`2024-46808`, :cve_nist:`2024-46809`, :cve_nist:`2024-46810`, :cve_nist:`2024-46811`, :cve_nist:`2024-46812`, :cve_nist:`2024-46813`, :cve_nist:`2024-46814`, :cve_nist:`2024-46815`, :cve_nist:`2024-46816`, :cve_nist:`2024-46817`, :cve_nist:`2024-46818`, :cve_nist:`2024-46819`, :cve_nist:`2024-46820`, :cve_nist:`2024-46821`, :cve_nist:`2024-46822`, :cve_nist:`2024-46823`, :cve_nist:`2024-46824`, :cve_nist:`2024-46825`, :cve_nist:`2024-46826`, :cve_nist:`2024-46827`, :cve_nist:`2024-46828`, :cve_nist:`2024-46829`, :cve_nist:`2024-46830`, :cve_nist:`2024-46831`, :cve_nist:`2024-46832`, :cve_nist:`2024-46833`, :cve_nist:`2024-46834`, :cve_nist:`2024-46835`, :cve_nist:`2024-46836`, :cve_nist:`2024-46837`, :cve_nist:`2024-46838`, :cve_nist:`2024-46840`, :cve_nist:`2024-46841`, :cve_nist:`2024-46842`, :cve_nist:`2024-46843`, :cve_nist:`2024-46844`, :cve_nist:`2024-46845`, :cve_nist:`2024-46846`, :cve_nist:`2024-46847`, :cve_nist:`2024-46848`, :cve_nist:`2024-46849`, :cve_nist:`2024-46850`, :cve_nist:`2024-46851`, :cve_nist:`2024-46852`, :cve_nist:`2024-46853`, :cve_nist:`2024-46854`, :cve_nist:`2024-46855`, :cve_nist:`2024-46856`, :cve_nist:`2024-46857`, :cve_nist:`2024-46858`, :cve_nist:`2024-46859`, :cve_nist:`2024-46860`, :cve_nist:`2024-46861`, :cve_nist:`2024-46862`, :cve_nist:`2024-46863`, :cve_nist:`2024-46864`, :cve_nist:`2024-46866`, :cve_nist:`2024-46867`, :cve_nist:`2024-46868`, :cve_nist:`2024-46869`, :cve_nist:`2024-46870`, :cve_nist:`2024-46871`, :cve_nist:`2024-47658`, :cve_nist:`2024-47659`, :cve_nist:`2024-47660`, :cve_nist:`2024-47661`, :cve_nist:`2024-47662`, :cve_nist:`2024-47663`, :cve_nist:`2024-47664`, :cve_nist:`2024-47665`, :cve_nist:`2024-47666`, :cve_nist:`2024-47667`, :cve_nist:`2024-47668`, :cve_nist:`2024-47669`, :cve_nist:`2024-47670`, :cve_nist:`2024-47671`, :cve_nist:`2024-47672`, :cve_nist:`2024-47673`, :cve_nist:`2024-47674`, :cve_nist:`2024-47675`, :cve_nist:`2024-47676`, :cve_nist:`2024-47677`, :cve_nist:`2024-47678`, :cve_nist:`2024-47679`, :cve_nist:`2024-47680`, :cve_nist:`2024-47681`, :cve_nist:`2024-47682`, :cve_nist:`2024-47683`, :cve_nist:`2024-47684`, :cve_nist:`2024-47685`, :cve_nist:`2024-47686`, :cve_nist:`2024-47687`, :cve_nist:`2024-47688`, :cve_nist:`2024-47689`, :cve_nist:`2024-47690`, :cve_nist:`2024-47691`, :cve_nist:`2024-47692`, :cve_nist:`2024-47693`, :cve_nist:`2024-47695`, :cve_nist:`2024-47696`, :cve_nist:`2024-47697`, :cve_nist:`2024-47698`, :cve_nist:`2024-47699`, :cve_nist:`2024-47700`, :cve_nist:`2024-47701`, :cve_nist:`2024-47702`, :cve_nist:`2024-47703`, :cve_nist:`2024-47704`, :cve_nist:`2024-47705`, :cve_nist:`2024-47706`, :cve_nist:`2024-47707`, :cve_nist:`2024-47710`, :cve_nist:`2024-47712`, :cve_nist:`2024-47713`, :cve_nist:`2024-47714`, :cve_nist:`2024-47715`, :cve_nist:`2024-47716`, :cve_nist:`2024-47717`, :cve_nist:`2024-47718`, :cve_nist:`2024-47719`, :cve_nist:`2024-47720`, :cve_nist:`2024-47721`, :cve_nist:`2024-47723`, :cve_nist:`2024-47724`, :cve_nist:`2024-47727`, :cve_nist:`2024-47728`, :cve_nist:`2024-47730`, :cve_nist:`2024-47731`, :cve_nist:`2024-47732`, :cve_nist:`2024-47733`, :cve_nist:`2024-47734`, :cve_nist:`2024-47735`, :cve_nist:`2024-47736`, :cve_nist:`2024-47737`, :cve_nist:`2024-47738`, :cve_nist:`2024-47739`, :cve_nist:`2024-47741`, :cve_nist:`2024-47742`, :cve_nist:`2024-47743`, :cve_nist:`2024-47744`, :cve_nist:`2024-47745`, :cve_nist:`2024-47746`, :cve_nist:`2024-47747`, :cve_nist:`2024-47748`, :cve_nist:`2024-47749`, :cve_nist:`2024-47750`, :cve_nist:`2024-47751`, :cve_nist:`2024-47752`, :cve_nist:`2024-47753`, :cve_nist:`2024-47754`, :cve_nist:`2024-47757`, :cve_nist:`2024-49850`, :cve_nist:`2024-49851`, :cve_nist:`2024-49852`, :cve_nist:`2024-49853`, :cve_nist:`2024-49854`, :cve_nist:`2024-49855`, :cve_nist:`2024-49856`, :cve_nist:`2024-49858`, :cve_nist:`2024-49859`, :cve_nist:`2024-49860`, :cve_nist:`2024-49861`, :cve_nist:`2024-49862`, :cve_nist:`2024-49863`, :cve_nist:`2024-49864`, :cve_nist:`2024-49866`, :cve_nist:`2024-49867`, :cve_nist:`2024-49868`, :cve_nist:`2024-49870`, :cve_nist:`2024-49871`, :cve_nist:`2024-49874` and :cve_nist:`2024-49875`
-  orc: Fix :cve_nist:`2024-40897` (follow-up fix)
-  vim: Fix :cve_nist:`2024-45306` and :cve_nist:`2024-47814`
-  wpa-supplicant: Ignore :cve_nist:`2024-5290`
-  xserver-xorg: Fix :cve_nist:`2024-9632`
-  xwayland: Fix :cve_nist:`2024-9632`


Fixes in Yocto-5.1.1
~~~~~~~~~~~~~~~~~~~~

-  binutils: Add missing perl modules to :term:`RDEPENDS` for nativsdk variant
-  binutils: Fix binutils mingw packaging
-  bitbake.conf: Mark VOLATILE_LOG_DIR & VOLATILE_TMP_DIR as obsolete
-  bitbake: Remove custom exception backtrace formatting
-  bitbake: bitbake: doc/user-manual: Update the :term:`BB_HASHSERVE_UPSTREAM`
-  bitbake: fetch2/git: Use quote from shlex, not pipes
-  bitbake: fetch2: don't try to preserve all attributes when unpacking files
-  bitbake: fetch2: use persist_data context managers
-  bitbake: fetch/wget: Increase timeout to 100s from 30s
-  bitbake: gitsm: Add call_process_submodules() to remove duplicated code
-  bitbake: gitsm: Remove downloads/tmpdir when failed
-  bitbake: persist_data: close connection in SQLTable __exit__
-  bitbake: tests/fetch: Update GoModTest and GoModGitTest
-  bitbake: tests/fetch: Use our own mirror of mobile-broadband-provider to decouple from gnome gitlab
-  bitbake: tests/fetch: Use our own mirror of sysprof to decouple from gnome gitlab
-  bluez: Fix mesh builds on musl
-  build-appliance-image: Update to styhead head revision
-  cml1.bbclass: do_diffconfig: Don't override .config with .config.orig
-  contributor-guide: Remove duplicated words
-  cve-check: add field "modified" to JSON report
-  cve-check: add support for cvss v4.0
-  cve-check: do not skip cve status description after :
-  cve-check: fix malformed cve status description with : characters
-  dev-manual: add bblock documentation
-  dev-manual: bblock: use warning block instead of attention
-  dev-manual: document how to provide confs from layer.conf
-  documentation: features: describe distribution feature pni-name
-  documentation; features: remove duplicate word in distribution feature ext2
-  documentation: Makefile: add SPHINXLINTDOCS to specify subset to sphinx-lint
-  documentation: Makefile: add support for xelatex
-  documentation: Makefile: publish pdf and epub versions too
-  documentation: Makefile: remove inkscape, replace by rsvg-convert
-  documentation: README: add instruction to run Vale on a subset
-  documentation: Replace VOLATILE_LOG_DIR with :term:`FILESYSTEM_PERMS_TABLES`
-  documentation: Replace VOLATILE_TMP_DIR with :term:`FILESYSTEM_PERMS_TABLES`
-  documentation: add a download page for epub and pdf
-  documentation: conf.py: add a bitbake_git extlink
-  documentation: conf.py: rename :cve: role to :cve_nist:
-  documentation: sphinx-static/switchers.js.in: do not refer to URL_ROOT anymore
-  documentation: styles: vocabularies: Yocto: add sstate
-  e2fsprogs: removed 'sed -u' option
-  efi-bootdisk.wks: Increase overhead-factor to avoid test failures
-  ffmpeg: Add "libswresample libavcodec" to :term:`CVE_PRODUCT`
-  ffmpeg: Disable asm optimizations on x86
-  ffmpeg: fix packaging examples
-  ffmpeg: nasm is x86 only, so only DEPEND if x86
-  ffmpeg: no need for textrel :term:`INSANE_SKIP`
-  gcc-source: Fix racing on building gcc-source-14.2.0 and lib32-gcc-source-14.2.0
-  gcc: add a backport patch to fix an issue with tzdata 2024b
-  git: upgrade to 2.46.1
-  glib-2.0: fix glib-2.0 ptest failure when upgrading tzdata2024b
-  glibc: Fix missing randomness in __gen_tempname
-  glibc: stable 2.40 branch updates
-  go: upgrade to 1.22.8
-  groff: fix rare build race in hdtbl
-  icu: update patch Upstream-Status
-  image.bbclass: Drop support for ImageQAFailed exceptions in image_qa
-  json-c: avoid ptest failure caused by valgrind
-  kexec-tools: update :term:`COMPATIBLE_HOST` because of makedumpfile
-  kmscube: Upgrade to latest revision (b2f97f53e0..)
-  lib/oe/package-manager: skip processing installed-pkgs with empty globs
-  libevdev: upgrade to 1.13.3
-  libgfortran: fix buildpath QA issue
-  libpam: use libdir in conditional
-  linux-yocto/6.6: update to v6.6.54
-  linux-yocto/6.10: bsp/genericarm64: disable ARM64_SME
-  linux-yocto/6.10: cfg: gpio: allow to re-enable the deprecated GPIO sysfs interface
-  linux-yocto/6.10: genericarm64.cfg: enable CONFIG_DMA_CMA
-  linux-yocto/6.10: update to v6.10.14
-  linux-yocto: Enable l2tp drivers when ptest featuee is on
-  lsb-release: fix Distro Codename shell escaping
-  migration-guides/release-notes-4.0: update :term:`BB_HASHSERVE_UPSTREAM` for new infrastructure
-  migration-guides/release-notes-5.0: NO_OUTPUT -> NO_COLOR
-  migration-guides/release-notes-5.1: add beaglebone-yocto parselogs test oeqa failure
-  migration-guides/release-notes-5.1: document added python3-libarchive-c ptest
-  migration-guides/release-notes-5.1: document fixed _test_devtool_add_git_url test
-  migration-guides/release-notes-5.1: document oeqa/selftest envvars change
-  migration-guides/release-notes-5.1: document spirv-tools reproducibility
-  migration-guides/release-notes-5.1: fix spdx bullet point
-  migration-guides/release-notes-5.1: update for several section
-  migration-guides/release-notes-5.1: update release note for styhead
-  migration-guides: 5.1: fix titles
-  migration-guides: add release notes for 4.0.21 and 4.0.22
-  oeqa/postactions: Fix archive retrieval from target
-  oeqa/runtime/ssh: Fix incorrect timeout fix
-  oeqa/runtime/ssh: Rework ssh timeout
-  oeqa/selftest/gcc: Fix kex exchange identification error
-  oeqa/selftest: Update the :term:`BB_HASHSERVE_UPSTREAM`
-  openssl: Fix SDK environment script to avoid unbound variable
-  orc: upgrade to 0.4.40
-  overview-manual: concepts: add details on package splitting
-  ovmf-native: remove .pyc files from install
-  package_rpm: Check if file exists before open()
-  package_rpm: restrict rpm to 4 threads
-  package_rpm: use zstd's default compression level
-  poky.conf: bump version for 5.1.1
-  pseudo: Fix envp bug and add posix_spawn wrapper
-  python3-maturin: sort external libs in wheel files
-  python3-setuptools: Add "python:setuptools" to :term:`CVE_PRODUCT`
-  qemu: Fix build on musl/riscv64
-  qemurunner: Clean up serial_lock handling
-  ref-manual: add missing CVE_CHECK manifest variables
-  ref-manual: add missing :term:`EXTERNAL_KERNEL_DEVICETREE` variable
-  ref-manual: add missing :term:`OPKGBUILDCMD` variable
-  ref-manual: add missing :term:`TESTIMAGE_FAILED_QA_ARTIFACTS`
-  ref-manual: add missing image manifest variables
-  ref-manual: add missing nospdx class
-  ref-manual: add missing variable :term:`PRSERV_UPSTREAM`
-  ref-manual: add mission pep517-backend sanity check
-  ref-manual: add new :term:`RECIPE_UPGRADE_EXTRA_TASKS` variable
-  ref-manual: add new retain class and variables
-  ref-manual: add new vex class
-  ref-manual: devtool-reference: document missing commands
-  ref-manual: devtool-reference: refresh example outputs
-  ref-manual: drop TCLIBCAPPEND variable
-  ref-manual: drop siteconfig class
-  ref-manual: faq: add q&a on class appends
-  ref-manual: fix ordering of insane checks list
-  ref-manual: merge patch-status-* to patch-status
-  ref-manual: release-process: add a reference to the doc's release
-  ref-manual: release-process: refresh the current LTS releases
-  ref-manual: release-process: update releases.svg
-  ref-manual: release-process: update releases.svg with month after "Current"
-  ref-manual: structure.rst: document missing tmp/ dirs
-  ref-manual: variables: add SIGGEN_LOCKEDSIGS* variables
-  rootfs-postcommands.bbclass: make opkg status reproducible
-  scripts/install-buildtools: Update to 5.1
-  selftest/sstatetests: run CDN mirror check only once
-  shadow: use update-alternatives to handle groups.1
-  strace: download release tarballs from GitHub
-  systemd: fix broken links for sysvinit-compatible commands
-  tcl: skip io-13.6 test case
-  toolchain-shar-extract.sh: exit when post-relocate-setup.sh fails
-  tune-cortexa32: set tune feature as armv8a
-  tzdata/tzcode-native: upgrade to 2024b
-  uboot-sign: fix concat_dtb arguments
-  udev-extraconf: fix network.sh script did not configure hotplugged interfaces
-  util-linux: Add `findmnt` to the bash completion :term:`RDEPENDS`
-  vim: Upgrade to 9.1.0764
-  virglrenderer: Add patch to fix -int-conversion build issue
-  weston: Add missing runtime dependency on freerdp
-  weston: backport patch to allow neatvnc < v0.9.0
-  wireless-regdb: upgrade to 2024.10.07
-  xserver-xorg: upgrade to 21.1.14
-  xwayland: upgrade to 24.1.4


Known Issues in Yocto-5.1.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.1.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aditya Tayade
-  Alban Bedel
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Antonin Godard
-  Bruce Ashfield
-  Chen Qi
-  Chris Laplante
-  Christian Lindeberg
-  Claus Stovgaard
-  Clayton Casciato
-  Daniel McGregor
-  Deepthi Hemraj
-  Harish Sadineni
-  Hiago De Franco
-  Hongxu Jia
-  Jagadeesh Krishnanjanappa
-  Jinfeng Wang
-  Jonas Gorski
-  Jose Quaresma
-  Joshua Watt
-  Julien Stephan
-  Jörg Sommer
-  Kai Kang
-  Katawann
-  Khem Raj
-  Lee Chee Yang
-  Markus Volk
-  Martin Jansa
-  Mathieu Dubois-Briand
-  Michael Opdenacker
-  Mikko Rapeli
-  Niko Mauno
-  Ola x Nilsson
-  Pavel Zhukov
-  Peter Kjellerstedt
-  Peter Marko
-  Purushottam Choudhary
-  Regis Dargent
-  Richard Purdie
-  Robert Yang
-  Rohini Sangam
-  Ross Burton
-  Sergei Zhmylev
-  Shunsuke Tokumoto
-  Steve Sakoman
-  Talel BELHAJSALEM
-  Tom Hochstein
-  Vijay Anusuri
-  Wang Mingyu
-  Yi Zhao
-  Yoann Congal
-  Zahir Hussain


Repositories / Downloads for Yocto-5.1.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`styhead </poky/log/?h=styhead>`
-  Tag:  :yocto_git:`yocto-5.1.1 </poky/log/?h=yocto-5.1.1>`
-  Git Revision: :yocto_git:`7e081bd98fdc5435e850d1df79a5e0f1e30293d0 </poky/commit/?id=7e081bd98fdc5435e850d1df79a5e0f1e30293d0>`
-  Release Artefact: poky-7e081bd98fdc5435e850d1df79a5e0f1e30293d0
-  sha: 1ae688856bcd4aa2d1a14c2659217143cc2050151a8c194b99e6b472b0a99710
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.1/poky-7e081bd98fdc5435e850d1df79a5e0f1e30293d0.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.1/poky-7e081bd98fdc5435e850d1df79a5e0f1e30293d0.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`styhead </openembedded-core/log/?h=styhead>`
-  Tag:  :oe_git:`yocto-5.1.1 </openembedded-core/log/?h=yocto-5.1.1>`
-  Git Revision: :oe_git:`b511d0146a2e8f316f4aecc90c853215674013ea </openembedded-core/commit/?id=b511d0146a2e8f316f4aecc90c853215674013ea>`
-  Release Artefact: oecore-b511d0146a2e8f316f4aecc90c853215674013ea
-  sha: 71eb36bf898b3eb5a7a79c2f1c057755405740e82b21a57ac540cebc1337e151
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.1/oecore-b511d0146a2e8f316f4aecc90c853215674013ea.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.1/oecore-b511d0146a2e8f316f4aecc90c853215674013ea.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`styhead </meta-mingw/log/?h=styhead>`
-  Tag:  :yocto_git:`yocto-5.1.1 </meta-mingw/log/?h=yocto-5.1.1>`
-  Git Revision: :yocto_git:`77fe18d4f8ec34501045c5d92ce7e13b1bd129e9 </meta-mingw/commit/?id=77fe18d4f8ec34501045c5d92ce7e13b1bd129e9>`
-  Release Artefact: meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9
-  sha: 4c7f8100a3675d9863e51825def3df5b263ffc81cd57bae26eedbc156d771534
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.1/meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.1/meta-mingw-77fe18d4f8ec34501045c5d92ce7e13b1bd129e9.tar.bz2

bitbake

-  Repository Location: :bitbake_git:`/`
-  Branch: :bitbake_git:`2.10 </log/?h=2.10>`
-  Tag:  :bitbake_git:`yocto-5.1.1 </log/?h=yocto-5.1.1>`
-  Git Revision: :bitbake_git:`9602a684568910fd333ffce907fa020ad3661c26 </commit/?id=9602a684568910fd333ffce907fa020ad3661c26>`
-  Release Artefact: bitbake-9602a684568910fd333ffce907fa020ad3661c26
-  sha: 8f5304b7a71cf7ad5dc8e5ee8bbfc041780bd402712f314d2c3c8be79c89a526
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.1.1/bitbake-9602a684568910fd333ffce907fa020ad3661c26.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.1.1/bitbake-9602a684568910fd333ffce907fa020ad3661c26.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`styhead </yocto-docs/log/?h=styhead>`
-  Tag: :yocto_git:`yocto-5.1.1 </yocto-docs/log/?h=yocto-5.1.1>`
-  Git Revision: :yocto_git:`8288c8cae7fe7303e89d8ed286de91fc26ce6cc3 </yocto-docs/commit/?id=8288c8cae7fe7303e89d8ed286de91fc26ce6cc3>`

