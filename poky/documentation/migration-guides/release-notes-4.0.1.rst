Release notes for 4.0.1 (kirkstone)
-----------------------------------

Security Fixes in 4.0.1
~~~~~~~~~~~~~~~~~~~~~~~

-  linux-yocto/5.15: fix :cve:`2022-28796`
-  python3: ignore :cve:`2015-20107`
-  e2fsprogs: fix :cve:`2022-1304`
-  lua: fix :cve:`2022-28805`
-  busybox: fix :cve:`2022-28391`

Fixes in 4.0.1
~~~~~~~~~~~~~~

-  abi_version/sstate: Bump hashequiv and sstate versions due to git changes
-  apt: add apt selftest to test signed package feeds
-  apt: upgrade 2.4.4 -> 2.4.5
-  arch-armv8-2a.inc: fix a typo in TUNEVALID variable
-  babeltrace: Disable warnings as errors
-  base: Avoid circular references to our own scripts
-  base: Drop git intercept
-  build-appliance-image: Update to kirkstone head revision
-  build-appliance: Switch to kirkstone branch
-  buildtools-tarball: Only add cert envvars if certs are included
-  busybox: Use base_bindir instead of hardcoding /bin path
-  cases/buildepoxy.py: fix typo
-  create-spdx: delete virtual/kernel dependency to fix FreeRTOS build
-  create-spdx: fix error when symlink cannot be created
-  cve-check: add JSON format to summary output
-  cve-check: fix symlinks where link and output path are equal
-  cve-check: no need to depend on the fetch task
-  cve-update-db-native: let the user to drive the update interval
-  cve-update-db-native: update the CVE database once a day only
-  cve_check: skip remote patches that haven't been fetched when searching for CVE tags
-  dev-manual: add command used to add the signed-off-by line.
-  devshell.bbclass: Allow devshell & pydevshell to use the network
-  docs: conf.py: fix cve extlinks caption for sphinx <4.0
-  docs: migration-guides: migration-3.4: mention that hardcoded password are supported if hashed
-  docs: migration-guides: release-notes-4.0: fix risc-v typo
-  docs: migration-guides: release-notes-4.0: replace kernel placeholder with correct recipe name
-  docs: ref-manual: variables: add hashed password example in EXTRA_USERS_PARAMS
-  docs: set_versions.py: add information about obsolescence of a release
-  docs: set_versions.py: fix latest release of a branch being shown twice in switchers.js
-  docs: set_versions.py: fix latest version of an active release shown as obsolete
-  docs: set_versions.py: mark as obsolete only branches and old tags from obsolete releases
-  docs: sphinx-static: switchers.js.in: do not mark branches as outdated
-  docs: sphinx-static: switchers.js.in: fix broken switcher for branches
-  docs: sphinx-static: switchers.js.in: improve obsolete version detection
-  docs: sphinx-static: switchers.js.in: remove duplicate for outdated versions
-  docs: sphinx-static: switchers.js.in: rename all_versions to switcher_versions
-  docs: update Bitbake objects.inv location for master branch
-  documentation/brief-yoctoprojectqs: add directory for local.conf
-  gcompat: Fix build when usrmerge distro feature is enabled
-  git: correct license
-  git: upgrade 2.35.2 -> 2.35.3
-  glib: upgrade 2.72.0 -> 2.72.1
-  glibc: ptest: Fix glibc-tests package issue
-  gnupg: Disable FORTIFY_SOURCES on mips
-  go.bbclass: disable the use of the default configuration file
-  gstreamer1.0-plugins-bad: drop patch
-  gstreamer1.0-plugins-good: Fix libsoup dependency
-  gstreamer1.0: Minor documentation addition
-  install/devshell: Introduce git intercept script due to fakeroot issues
-  kernel-yocto.bbclass: Fixup do_kernel_configcheck usage of KMETA
-  libc-glibc: Use libxcrypt to provide virtual/crypt
-  libgit2: upgrade 1.4.2 -> 1.4.3
-  libsoup: upgrade 3.0.5 -> 3.0.6
-  libusb1: upgrade 1.0.25 -> 1.0.26
-  linux-firmware: correct license for ar3k firmware
-  linux-firmware: upgrade 20220310 -> 20220411
-  linux-yocto/5.10: base: enable kernel crypto userspace API
-  linux-yocto/5.10: update to v5.10.112
-  linux-yocto/5.15: arm: poky-tiny cleanup and fixes
-  linux-yocto/5.15: base: enable kernel crypto userspace API
-  linux-yocto/5.15: fix -standard kernel build issue
-  linux-yocto/5.15: fix ppc boot
-  linux-yocto/5.15: fix qemuarm graphical boot
-  linux-yocto/5.15: kasan: fix BUG: sleeping function called from invalid context
-  linux-yocto/5.15: netfilter: conntrack: avoid useless indirection during conntrack destruction
-  linux-yocto/5.15: update to v5.15.36
-  linux-yocto: enable powerpc-debug fragment
-  mdadm: Drop clang specific cflags
-  migration-3.4: add missing entry on EXTRA_USERS_PARAMS
-  migration-guides: add release notes for 4.0
-  migration-guides: complete migration guide for 4.0
-  migration-guides: release-notes-4.0: mention LTS release
-  migration-guides: release-notes-4.0: update 'Repositories / Downloads' section
-  migration-guides: stop including documents with ".. include"
-  musl: Fix build when usrmerge distro feature is enabled
-  ncurses: use COPYING file
-  neard: Switch SRC_URI to git repo
-  oeqa/selftest: add test for git working correctly inside pseudo
-  openssl: minor security upgrade 3.0.2 -> 3.0.3
-  package.bbclass: Prevent perform_packagecopy from removing /sysroot-only
-  package: Ensure we track whether PRSERV was active or not
-  package_manager: fix missing dependency on gnupg when signing deb package feeds
-  poky-tiny: enable qemuarmv5/qemuarm64 and cleanups
-  poky.conf: bump version for 4.0.1 release
-  qemu.bbclass: Extend ppc/ppc64 extra options
-  qemuarm64: use virtio pci interfaces
-  qemuarmv5: use arm-versatile-926ejs KMACHINE
-  ref-manual: Add XZ_THREADS and XZ_MEMLIMIT
-  ref-manual: add KERNEL_DEBUG_TIMESTAMPS
-  ref-manual: add ZSTD_THREADS
-  ref-manual: add a note about hard-coded passwords
-  ref-manual: add empty-dirs QA check and QA_EMPTY_DIRS*
-  ref-manual: add mention of vendor filtering to CVE_PRODUCT
-  ref-manual: mention wildcarding support in INCOMPATIBLE_LICENSE
-  releases: update for yocto 4.0
-  rootfs-postcommands: fix symlinks where link and output path are equal
-  ruby: upgrade 3.1.1 -> 3.1.2
-  sanity: skip make 4.2.1 warning for debian
-  scripts/git: Ensure we don't have circular references
-  scripts: Make git intercept global
-  seatd: Disable overflow warning as error on ppc64/musl
-  selftest/lic_checksum: Add test for filename containing space
-  set_versions: update for 4.0 release
-  staging: Ensure we filter out ourselves
-  strace: fix ptest failure in landlock
-  subversion: upgrade to 1.14.2
-  systemd-boot: remove outdated EFI_LD comment
-  systemtap: Fix build with gcc-12
-  terminal.py: Restore error output from Terminal
-  u-boot: Correct the SRC_URI
-  u-boot: Inherit pkgconfig
-  update_udev_hwdb: fix multilib issue with systemd
-  util-linux: Create u-a symlink for findfs utility
-  virgl: skip headless test on alma 8.6
-  webkitgtk: adjust patch status
-  wic: do not use PARTLABEL for msdos partition tables
-  wireless-regdb: upgrade 2022.02.18 -> 2022.04.08
-  xserver-xorg: Fix build with gcc12
-  yocto-bsps: update to v5.15.36

Contributors to 4.0.1
~~~~~~~~~~~~~~~~~~~~~

-  Abongwa Amahnui Bonalais
-  Alexander Kanavin
-  Bruce Ashfield
-  Carlos Rafael Giani
-  Chen Qi
-  Davide Gardenal
-  Dmitry Baryshkov
-  Ferry Toth
-  Henning Schild
-  Jon Mason
-  Justin Bronder
-  Kai Kang
-  Khem Raj
-  Konrad Weihmann
-  Lee Chee Yang
-  Marta Rybczynska
-  Martin Jansa
-  Matt Madison
-  Michael Halstead
-  Michael Opdenacker
-  Naveen Saini
-  Nicolas Dechesne
-  Paul Eggleton
-  Paul Gortmaker
-  Paulo Neves
-  Peter Kjellerstedt
-  Peter Marko
-  Pgowda
-  Portia
-  Quentin Schulz
-  Rahul Kumar
-  Richard Purdie
-  Robert Joslyn
-  Robert Yang
-  Roland Hieber
-  Ross Burton
-  Russ Dill
-  Steve Sakoman
-  wangmy
-  zhengruoqin

Repositories / Downloads for 4.0.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: https://git.yoctoproject.org/git/poky
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.1 </poky/tag/?h=yocto-4.0.1>`
-  Git Revision: :yocto_git:`8c489602f218bcf21de0d3c9f8cf620ea5f06430 </poky/commit/?id=8c489602f218bcf21de0d3c9f8cf620ea5f06430>`
-  Release Artefact: poky-8c489602f218bcf21de0d3c9f8cf620ea5f06430
-  sha: 65c545a316bd8efb13ae1358eeccc8953543be908008103b51f7f90aed960d00
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.1/poky-8c489602f218bcf21de0d3c9f8cf620ea5f06430.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.1/poky-8c489602f218bcf21de0d3c9f8cf620ea5f06430.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag: :oe_git:`yocto-4.0.1 </openembedded-core/tag/?h=yocto-4.0>`
-  Git Revision: :oe_git:`cb8647c08959abb1d6b7c2b3a34b4b415f66d7ee </openembedded-core/commit/?id=cb8647c08959abb1d6b7c2b3a34b4b415f66d7ee>`
-  Release Artefact: oecore-cb8647c08959abb1d6b7c2b3a34b4b415f66d7ee
-  sha: 43981b8fad82f601618a133dffbec839524f0d0a055efc3d8f808cbfd811ab17
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.1/oecore-cb8647c08959abb1d6b7c2b3a34b4b415f66d7ee.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.1/oecore-cb8647c08959abb1d6b7c2b3a34b4b415f66d7ee.tar.bz2

meta-mingw

-  Repository Location: https://git.yoctoproject.org/git/meta-mingw
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.1 </meta-mingw/tag/?h=yocto-4.0.1>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.1/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.1/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: https://git.yoctoproject.org/git/meta-gplv2
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.1 </meta-gplv2/tag/?h=yocto-4.0.1>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-mingw/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.1/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.1/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag: :oe_git:`yocto-4.0 </bitbake/tag/?h=yocto-4.0>`
-  Git Revision: :oe_git:`59c16ae6c55c607c56efd2287537a1b97ba2bf52 </bitbake/commit/?id=59c16ae6c55c607c56efd2287537a1b97ba2bf52>`
-  Release Artefact: bitbake-59c16ae6c55c607c56efd2287537a1b97ba2bf52
-  sha: 3ae466c31f738fc45c3d7c6f665952d59f01697f2667ea42f0544d4298dd6ef0
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.1/bitbake-59c16ae6c55c607c56efd2287537a1b97ba2bf52.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.1/bitbake-59c16ae6c55c607c56efd2287537a1b97ba2bf52.tar.bz2

yocto-docs

-  Repository Location: https://git.yoctoproject.org/git/yocto-docs
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.1 </yocto-docs/tag/?h=yocto-4.0>`
-  Git Revision: :yocto_git:`4ec9df3336a425719a9a35532504731ce56984ca </yocto-docs/commit/?id=4ec9df3336a425719a9a35532504731ce56984ca>`
