.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.2 (Kirkstone)
-----------------------------------------

Security Fixes in Yocto-4.0.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  libxslt: Mark :cve:`2022-29824` as not applying
-  tiff: Add jbig :term:`PACKAGECONFIG` and clarify IGNORE :cve:`2022-1210`
-  tiff: mark :cve:`2022-1622` and :cve:`2022-1623` as invalid
-  pcre2:fix :cve:`2022-1586` Out-of-bounds read
-  curl: fix :cve:`2022-22576`, :cve:`2022-27775`, :cve:`2022-27776`, :cve:`2022-27774`, :cve:`2022-30115`, :cve:`2022-27780`, :cve:`2022-27781`, :cve:`2022-27779` and :cve:`2022-27782`
-  qemu: fix :cve:`2021-4206` and :cve:`2021-4207`
-  freetype: fix :cve:`2022-27404`, :cve:`2022-27405` and :cve:`2022-27406`

Fixes in Yocto-4.0.2
~~~~~~~~~~~~~~~~~~~~

-  alsa-plugins: fix libavtp vs. avtp packageconfig
-  archiver: don't use machine variables in shared recipes
-  archiver: use bb.note instead of echo
-  baremetal-image: fix broken symlink in do_rootfs
-  base-passwd: Disable shell for default users
-  bash: submit patch upstream
-  bind: upgrade 9.18.1 -> 9.18.2
-  binutils: Bump to latest 2.38 release branch
-  bitbake.conf: Make :term:`TCLIBC` and :term:`TCMODE` lazy assigned
-  bitbake: build: Add clean_stamp API function to allow removal of task stamps
-  bitbake: data: Do not depend on vardepvalueexclude flag
-  bitbake: fetch2/osc: Small fixes for osc fetcher
-  bitbake: server/process: Fix logging issues where only the first message was displayed
-  build-appliance-image: Update to kirkstone head revision
-  buildhistory.bbclass: fix shell syntax when using dash
-  cairo: Add missing GPLv3 license checksum entry
-  classes: rootfs-postcommands: add skip option to overlayfs_qa_check
-  cronie: upgrade 1.6.0 -> 1.6.1
-  cups: upgrade 2.4.1 -> 2.4.2
-  cve-check.bbclass: Added do_populate_sdk[recrdeptask].
-  cve-check: Add helper for symlink handling
-  cve-check: Allow warnings to be disabled
-  cve-check: Fix report generation
-  cve-check: Only include installed packages for rootfs manifest
-  cve-check: add support for Ignored CVEs
-  cve-check: fix return type in check_cves
-  cve-check: move update_symlinks to a library
-  cve-check: write empty fragment files in the text mode
-  cve-extra-exclusions: Add kernel CVEs
-  cve-update-db-native: make it possible to disable database updates
-  devtool: Fix _copy_file() TypeError
-  e2fsprogs: add alternatives handling of lsattr as well
-  e2fsprogs: update upstream status
-  efivar: add musl libc compatibility
-  epiphany: upgrade 42.0 -> 42.2
-  ffmpeg: upgrade 5.0 -> 5.0.1
-  fribidi: upgrade 1.0.11 -> 1.0.12
-  gcc-cross-canadian: Add nativesdk-zstd dependency
-  gcc-source: Fix incorrect task dependencies from ${B}
-  gcc: Upgrade to 11.3 release
-  gcc: depend on zstd-native
-  git: fix override syntax in :term:`RDEPENDS`
-  glib-2.0: upgrade 2.72.1 -> 2.72.2
-  glibc: Drop make-native dependency
-  go: upgrade 1.17.8 -> 1.17.10
-  gst-devtools: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-libav: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-omx: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-plugins-bad: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-plugins-base: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-plugins-good: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-plugins-ugly: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-python: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-rtsp-server: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0-vaapi: upgrade 1.20.1 -> 1.20.2
-  gstreamer1.0: upgrade 1.20.1 -> 1.20.2
-  gtk+3: upgrade 3.24.33 -> 3.24.34
-  gtk-doc: Fix potential shebang overflow on gtkdoc-mkhtml2
-  image.bbclass: allow overriding dependency on virtual/kernel:do_deploy
-  insane.bbclass: make sure to close .patch files
-  iso-codes: upgrade 4.9.0 -> 4.10.0
-  kernel-yocto.bbclass: Reset to exiting on non-zero return code at end of task
-  libcgroup: upgrade 2.0.1 -> 2.0.2
-  liberror-perl: Update sstate/equiv versions to clean cache
-  libinput: upgrade 1.19.3 -> 1.19.4
-  libpcre2: upgrade 10.39 -> 10.40
-  librepo: upgrade 1.14.2 -> 1.14.3
-  libseccomp: Add missing files for ptests
-  libseccomp: Correct :term:`LIC_FILES_CHKSUM`
-  libxkbcommon: upgrade 1.4.0 -> 1.4.1
-  libxml2: Upgrade 2.9.13 -> 2.9.14
-  license.bbclass: Bound beginline and endline in copy_license_files()
-  license_image.bbclass: Make QA errors fail the build
-  linux-firmware: add support for building snapshots
-  linux-firmware: package new Qualcomm firmware
-  linux-firmware: replace mkdir by install
-  linux-firmware: split ath3k firmware
-  linux-firmware: upgrade to 20220610
-  linux-yocto/5.10: update to v5.10.119
-  linux-yocto/5.15: Enable MDIO bus config
-  linux-yocto/5.15: bpf: explicitly disable unpriv eBPF by default
-  linux-yocto/5.15: cfg/xen: Move x86 configs to separate file
-  linux-yocto/5.15: update to v5.15.44
-  local.conf.sample: Update sstate url to new 'all' path
-  logrotate: upgrade 3.19.0 -> 3.20.1
-  lttng-modules: Fix build failure for 5.10.119+ and 5.15.44+ kernel
-  lttng-modules: fix build against 5.18-rc7+
-  lttng-modules: fix shell syntax
-  lttng-ust: upgrade 2.13.2 -> 2.13.3
-  lzo: Add further info to a patch and mark as Inactive-Upstream
-  makedevs: Don't use COPYING.patch just to add license file into ${S}
-  manuals: switch to the sstate mirror shared between all versions
-  mesa.inc: package 00-radv-defaults.conf
-  mesa: backport a patch to support compositors without zwp_linux_dmabuf_v1 again
-  mesa: upgrade to 22.0.3
-  meson.bbclass: add cython binary to cross/native toolchain config
-  mmc-utils: upgrade to latest revision
-  mobile-broadband-provider-info: upgrade 20220315 -> 20220511
-  ncurses: update to patchlevel 20220423
-  oeqa/selftest/cve_check: add tests for Ignored and partial reports
-  oeqa/selftest/cve_check: add tests for recipe and image reports
-  oescripts: change compare logic in OEListPackageconfigTests
-  openssl: Backport fix for ptest cert expiry
-  overlayfs: add docs about skipping QA check & service dependencies
-  ovmf: Fix native build with gcc-12
-  patch.py: make sure that patches/series file exists before quilt pop
-  pciutils: avoid lspci conflict with busybox
-  perl: Add dependency on make-native to avoid race issues
-  perl: Fix build with gcc-12
-  poky.conf: bump version for 4.0.2
-  popt: fix override syntax in :term:`RDEPENDS`
-  pypi.bbclass: Set :term:`CVE_PRODUCT` to :term:`PYPI_PACKAGE`
-  python3: Ensure stale empty python module directories don't break the build
-  python3: Remove problematic paths from sysroot files
-  python3: fix reproducibility issue with python3-core
-  python3: use built-in distutils for ptest, rather than setuptools' 'fork'
-  python: Avoid shebang overflow on python-config.py
-  rootfs-postcommands.bbclass: correct comments
-  rootfs.py: close kernel_abi_ver_file
-  rootfs.py: find .ko.zst kernel modules
-  rust-common: Drop LLVM_TARGET and simplify
-  rust-common: Ensure sstate signatures have correct dependencues for do_rust_gen_targets
-  rust-common: Fix for target definitions returning 'NoneType' for arm
-  rust-common: Fix native signature dependency issues
-  rust-common: Fix sstate signatures between arm hf and non-hf
-  sanity: Don't warn about make 4.2.1 for mint
-  sanity: Switch to make 4.0 as a minimum version
-  sed: Specify shell for "nobody" user in run-ptest
-  selftest/imagefeatures/overlayfs: Always append to :term:`DISTRO_FEATURES`
-  selftest/multiconfig: Test that multiconfigs in separate layers works
-  sqlite3: upgrade to 3.38.5
-  staging.bbclass: process direct dependencies in deterministic order
-  staging: Fix rare sysroot corruption issue
-  strace: Don't run ptest as "nobody"
-  systemd: Correct 0001-pass-correct-parameters-to-getdents64.patch
-  systemd: Correct path returned in sd_path_lookup()
-  systemd: Document future actions needed for set of musl patches
-  systemd: Drop 0001-test-parse-argument-Include-signal.h.patch
-  systemd: Drop 0002-don-t-use-glibc-specific-qsort_r.patch
-  systemd: Drop 0016-Hide-__start_BUS_ERROR_MAP-and-__stop_BUS_ERROR_MAP.patch
-  systemd: Drop redundant musl patches
-  systemd: Fix build regression with latest update
-  systemd: Remove __compare_fn_t type in musl-specific patch
-  systemd: Update patch status
-  systemd: systemd-systemctl: Support instance conf files during enable
-  systemd: update ``0008-add-missing-FTW_-macros-for-musl.patch``
-  systemd: upgrade 250.4 -> 250.5
-  uboot-sign: Fix potential index error issues
-  valgrind: submit arm patches upstream
-  vim: Upgrade to 8.2.5083
-  webkitgtk: upgrade to 2.36.3
-  wic/plugins/rootfs: Fix permissions when splitting rootfs folders across partitions
-  xwayland: upgrade 22.1.0 -> 22.1.1
-  xxhash: fix build with gcc 12
-  zip/unzip: mark all submittable patches as Inactive-Upstream

Known Issues in Yocto-4.0.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- There were build failures at the autobuilder due to a known scp issue on Fedora-36 hosts.

Contributors to Yocto-4.0.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alex Kiernan
-  Alexander Kanavin
-  Aryaman Gupta
-  Bruce Ashfield
-  Claudius Heine
-  Davide Gardenal
-  Dmitry Baryshkov
-  Ernst Sjöstrand
-  Felix Moessbauer
-  Gunjan Gupta
-  He Zhe
-  Hitendra Prajapati
-  Jack Mitchell
-  Jeremy Puhlman
-  Jiaqing Zhao
-  Joerg Vehlow
-  Jose Quaresma
-  Kai Kang
-  Khem Raj
-  Konrad Weihmann
-  Marcel Ziswiler
-  Markus Volk
-  Marta Rybczynska
-  Martin Jansa
-  Michael Opdenacker
-  Mingli Yu
-  Naveen Saini
-  Nick Potenski
-  Paulo Neves
-  Pavel Zhukov
-  Peter Kjellerstedt
-  Rasmus Villemoes
-  Richard Purdie
-  Robert Joslyn
-  Ross Burton
-  Samuli Piippo
-  Sean Anderson
-  Stefan Wiehler
-  Steve Sakoman
-  Sundeep Kokkonda
-  Tomasz Dziendzielski
-  Xiaobing Luo
-  Yi Zhao
-  leimaohui
-  Wang Mingyu

Repositories / Downloads for Yocto-4.0.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.2 </poky/log/?h=yocto-4.0.2>`
-  Git Revision: :yocto_git:`a5ea426b1da472fc8549459fff3c1b8c6e02f4b5 </poky/commit/?id=a5ea426b1da472fc8549459fff3c1b8c6e02f4b5>`
-  Release Artefact: poky-a5ea426b1da472fc8549459fff3c1b8c6e02f4b5
-  sha: 474ddfacfed6661be054c161597a1a5273188dfe021b31d6156955d93c6b7359
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.2/poky-a5ea426b1da472fc8549459fff3c1b8c6e02f4b5.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.2/poky-a5ea426b1da472fc8549459fff3c1b8c6e02f4b5.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.2 </openembedded-core/log/?h=yocto-4.0.2>`
-  Git Revision: :oe_git:`eea52e0c3d24c79464f4afdbc3c397e1cb982231 </openembedded-core/commit/?id=eea52e0c3d24c79464f4afdbc3c397e1cb982231>`
-  Release Artefact: oecore-eea52e0c3d24c79464f4afdbc3c397e1cb982231
-  sha: 252d5c2c2db7e14e7365fcc69d32075720b37d629894bae36305eba047a39907
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.2/oecore-eea52e0c3d24c79464f4afdbc3c397e1cb982231.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.2/oecore-eea52e0c3d24c79464f4afdbc3c397e1cb982231.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.2 </meta-mingw/log/?h=yocto-4.0.2>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.2/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.2/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.2 </meta-gplv2/log/?h=yocto-4.0.2>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.2/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.2/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.2 </bitbake/log/?h=yocto-4.0.2>`
-  Git Revision: :oe_git:`b8fd6f5d9959d27176ea016c249cf6d35ac8ba03 </bitbake/commit/?id=b8fd6f5d9959d27176ea016c249cf6d35ac8ba03>`
-  Release Artefact: bitbake-b8fd6f5d9959d27176ea016c249cf6d35ac8ba03
-  sha: 373818b1dee2c502264edf654d6d8f857b558865437f080e02d5ba6bb9e72cc3
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.2/bitbake-b8fd6f5d9959d27176ea016c249cf6d35ac8ba03.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.2/bitbake-b8fd6f5d9959d27176ea016c249cf6d35ac8ba03.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.2 </yocto-docs/log/?h=yocto-4.0.2>`
-  Git Revision: :yocto_git:`662294dccd028828d5c7e9fd8f5c8e14df53df4b </yocto-docs/commit/?id=662294dccd028828d5c7e9fd8f5c8e14df53df4b>`
