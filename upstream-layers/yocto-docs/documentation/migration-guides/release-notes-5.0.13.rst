.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.13 (Scarthgap)
------------------------------------------

Security Fixes in Yocto-5.0.13
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  busybox: Fix :cve_nist:`2025-46394`
-  cups: Fix :cve_nist:`2025-58060` and :cve_nist:`2025-58364`
-  curl: Fix :cve_nist:`2025-9086`
-  dpkg: Fix :cve_nist:`2025-6297`
-  expat: follow-up Fix :cve_nist:`2024-8176`
-  ffmpeg: Fix :cve_nist:`2025-1594`
-  ffmpeg: Ignore :cve_nist:`2023-49502`, :cve_nist:`2023-50007`, :cve_nist:`2023-50008`,
   :cve_nist:`2023-50009`, :cve_nist:`2023-50010`, :cve_nist:`2024-31578`, :cve_nist:`2024-31582`
   and :cve_nist:`2024-31585`
-  ghostscript: Fix :cve_nist:`2025-59798`, :cve_nist:`2025-59799` and :cve_nist:`2025-59800`
-  glib-2.0: Fix :cve_nist:`2025-6052` and :cve_nist:`2025-7039`
-  go-binary-native: Ignore :cve_nist:`2025-0913`
-  go: Fix :cve_nist:`2025-4674`, :cve_nist:`2025-47906` and :cve_nist:`2025-47907`
-  grub2: Fix :cve_nist:`2024-56738`
-  grub2: Ignore :cve_nist:`2024-2312`
-  gstreamer1.0-plugins-bad: Fix :cve_nist:`2025-3887`
-  gstreamer1.0-plugins-base: Fix :cve_nist:`2025-47807`
-  gstreamer1.0-plugins-base: Ignore :cve_nist:`2025-47806` and :cve_nist:`2025-47808`
-  gstreamer1.0-plugins-good: Ignore :cve_nist:`2025-47183` and :cve_nist:`2025-47219`
-  gstreamer1.0: Ignore :cve_nist:`2025-2759`
-  libpam: Fix :cve_nist:`2024-10963`
-  libxslt: Fix :cve_nist:`2025-7424`
-  openssl: Fix :cve_nist:`2025-9230`, :cve_nist:`2025-9231` and :cve_nist:`2025-9232`
-  pulseaudio: Ignore :cve_nist:`2024-11586`
-  qemu: Ignore :cve_nist:`2024-7730`
-  tiff: Fix :cve_nist:`2025-9900`
-  tiff: Ignore :cve_nist:`2024-13978`, :cve_nist:`2025-8176`, :cve_nist:`2025-8177`,
   :cve_nist:`2025-8534` and :cve_nist:`2025-8851`
-  vim: Fix :cve_nist:`2025-9389`
-  wpa-supplicant: Fix :cve_nist:`2022-37660`


Fixes in Yocto-5.0.13
~~~~~~~~~~~~~~~~~~~~~

-  binutils: fix build with gcc-15
-  bitbake: Use a "fork" multiprocessing context
-  bitbake: bitbake: Bump version to 2.8.1
-  build-appliance-image: Update to scarthgap head revision
-  buildtools-tarball: fix unbound variable issues under 'set -u'
-  cmake: fix build with gcc-15 on host
-  conf/bitbake.conf: use gnu mirror instead of main server
-  contributor-guide: submit-changes: align :term:`CC` tag description
-  contributor-guide: submit-changes: clarify example with Yocto bug ID
-  contributor-guide: submit-changes: fix improper bold string
-  contributor-guide: submit-changes: make "Crediting contributors" part of "Commit your changes"
-  contributor-guide: submit-changes: make the Cc tag follow kernel guidelines
-  contributor-guide: submit-changes: number instruction list in commit your changes
-  contributor-guide: submit-changes: reword commit message instructions
-  cpio: Pin to use C17 std
-  cups: upgrade to 2.4.11
-  curl: update :term:`CVE_STATUS` for :cve_nist:`2025-5025`
-  dbus-glib: fix build with gcc-15
-  default-distrovars.inc: Fix CONNECTIVITY_CHECK_URIS redirect issue
-  dev-manual/building.rst: add note about externalsrc variables absolute paths
-  dev-manual/security-subjects.rst: update mailing lists
-  elfutils: fix build with gcc-15
-  examples: genl: fix wrong attribute size
-  expect: Fix build with GCC 15
-  expect: Revert "expect-native: fix do_compile failure with gcc-14"
-  expect: cleanup do_install
-  expect: don't run aclocal in do_configure
-  expect: fix native build with GCC 15
-  expect: update code for Tcl channel implementation
-  ffmpeg: upgrade to 6.1.3
-  gdbm: Use C11 standard
-  git: fix build with gcc-15 on host
-  gmp: Fix build with GCC15/C23
-  gmp: Fix build with older gcc versions
-  kernel-dev/common.rst: fix the in-tree defconfig description
-  lib/oe/utils: use multiprocessing from bb
-  libarchive: patch regression of patch for :cve_nist:`2025-5918`
-  libgpg-error: fix build with gcc-15
-  libtirpc: Fix build with gcc-15/C23
-  license.py: avoid deprecated ast.Str
-  llvm: fix build with gcc-15
-  llvm: update to 18.1.8
-  m4: Stick to C17 standard
-  migration-guides: add release notes for 4.0.29 5.0.12
-  ncurses: Pin to C17 standard
-  oeqa/sdk/cases/buildcpio.py: use gnu mirror instead of main server
-  openssl: upgrade to 3.2.6
-  p11-kit: backport fix for handle :term:`USE_NLS` from master
-  pkgconfig: fix build with gcc-15
-  poky.conf: bump version for 5.0.13
-  pulseaudio: Add audio group explicitly
-  ref-manual/structure: document the auto.conf file
-  ref-manual/variables.rst: expand :term:`IMAGE_OVERHEAD_FACTOR` glossary entry
-  ref-manual/variables.rst: fix the description of :term:`KBUILD_DEFCONFIG` :term:`STAGING_DIR`
-  rpm: keep leading "/" from sed operation
-  ruby-ptest: some ptest fixes
-  runqemu: fix special characters bug
-  rust-llvm: fix build with gcc-15
-  sanity.conf: Update minimum bitbake version to 2.8.1
-  scripts/install-buildtools: Update to 5.0.12
-  sdk: The main in the C example should return an int
-  selftest/cases/meta_ide.py: use use gnu mirror instead of main server
-  shared-mime-info: Handle :term:`USE_NLS`
-  sudo: remove devtool FIXME comment
-  systemd: backport fix for handle :term:`USE_NLS` from master
-  systemtap: Fix task_work_cancel build
-  test-manual/yocto-project-compatible.rst: fix a typo
-  test-manual: update runtime-testing Exporting Tests section
-  unifdef: Don't use C23 constexpr keyword
-  unzip: Fix build with GCC-15
-  util-linux: use ${B} instead of ${WORKDIR}/build, to fix building under devtool
-  vim: upgrade to 9.1.1683
-  yocto-uninative: Update to 4.9 for glibc 2.42 GCC 15.1


Known Issues in Yocto-5.0.13
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.13
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:
-  Adam Blank
-  Adrian Freihofer
-  Aleksandar Nikolic
-  Antonin Godard
-  Archana Polampalli
-  AshishKumar Mishra
-  Barne Carstensen
-  Chris Laplante
-  Deepak Rathore
-  Divya Chellam
-  Gyorgy Sarvari
-  Haixiao Yan
-  Hitendra Prajapati
-  Hongxu Jia
-  Jan Vermaete
-  Jiaying Song
-  Jinfeng Wang
-  Joao Marcos Costa
-  Joshua Watt
-  Khem Raj
-  Kyungjik Min
-  Lee Chee Yang
-  Libo Chen
-  Martin Jansa
-  Michael Halstead
-  Nitin Wankhade
-  Peter Marko
-  Philip Lorenz
-  Praveen Kumar
-  Quentin Schulz
-  Ross Burton
-  Stanislav Vovk
-  Steve Sakoman
-  Talel BELHAJ SALEM
-  Vijay Anusuri
-  Vrushti Dabhi
-  Yogita Urade


Repositories / Downloads for Yocto-5.0.13
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.13 </yocto-docs/log/?h=yocto-5.0.13>`
-  Git Revision: :yocto_git:`6f086fd3d9dbbb0c80f6c3e89b8df4fed422e79a </yocto-docs/commit/?id=6f086fd3d9dbbb0c80f6c3e89b8df4fed422e79a>`
-  Release Artefact: yocto-docs-6f086fd3d9dbbb0c80f6c3e89b8df4fed422e79a
-  sha: 454601d8b6034268212f74ca689ed360b08f7a4c7de5df726aa3706586ca4351
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.13/yocto-docs-6f086fd3d9dbbb0c80f6c3e89b8df4fed422e79a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.13/yocto-docs-6f086fd3d9dbbb0c80f6c3e89b8df4fed422e79a.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.13 </poky/log/?h=yocto-5.0.13>`
-  Git Revision: :yocto_git:`f16cffd030d21d12dd57bb95cfc310bda41f8a1f </poky/commit/?id=f16cffd030d21d12dd57bb95cfc310bda41f8a1f>`
-  Release Artefact: poky-f16cffd030d21d12dd57bb95cfc310bda41f8a1f
-  sha: 1367e43907f5ffa725f3afb019cd7ca07de21f13e5e73a1f5d1808989ae6ed2a
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.13/poky-f16cffd030d21d12dd57bb95cfc310bda41f8a1f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.13/poky-f16cffd030d21d12dd57bb95cfc310bda41f8a1f.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.13 </openembedded-core/log/?h=yocto-5.0.13>`
-  Git Revision: :oe_git:`7af6b75221d5703ba5bf43c7cd9f1e7a2e0ed20b </openembedded-core/commit/?id=7af6b75221d5703ba5bf43c7cd9f1e7a2e0ed20b>`
-  Release Artefact: oecore-7af6b75221d5703ba5bf43c7cd9f1e7a2e0ed20b
-  sha: 4dcf636ec4a7b38b47a24e9cb3345b385bc126bb19620bf6af773bf292fef6b2
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.13/oecore-7af6b75221d5703ba5bf43c7cd9f1e7a2e0ed20b.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.13/oecore-7af6b75221d5703ba5bf43c7cd9f1e7a2e0ed20b.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`scarthgap </meta-yocto/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.13 </meta-yocto/log/?h=yocto-5.0.13>`
-  Git Revision: :yocto_git:`3ff7ca786732390cd56ae92ff4a43aba46a1bf2e </meta-yocto/commit/?id=3ff7ca786732390cd56ae92ff4a43aba46a1bf2e>`
-  Release Artefact: meta-yocto-3ff7ca786732390cd56ae92ff4a43aba46a1bf2e
-  sha: 8efbaeab49dc3e1c4b67ff8d5801df1b05204c2255d18cff9a6857769ae33b23
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.13/meta-yocto-3ff7ca786732390cd56ae92ff4a43aba46a1bf2e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.13/meta-yocto-3ff7ca786732390cd56ae92ff4a43aba46a1bf2e.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.13 </meta-mingw/log/?h=yocto-5.0.13>`
-  Git Revision: :yocto_git:`bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f </meta-mingw/commit/?id=bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f>`
-  Release Artefact: meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f
-  sha: ab073def6487f237ac125d239b3739bf02415270959546b6b287778664f0ae65
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.13/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.13/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.13 </bitbake/log/?h=yocto-5.0.13>`
-  Git Revision: :oe_git:`1c9ec1ffde75809de34c10d3ec2b40d84d258cb4 </bitbake/commit/?id=1c9ec1ffde75809de34c10d3ec2b40d84d258cb4>`
-  Release Artefact: bitbake-1c9ec1ffde75809de34c10d3ec2b40d84d258cb4
-  sha: 98bf54fa3abe237b73a93b1e33842a429209371fca6e409c258a441987879d16
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.13/bitbake-1c9ec1ffde75809de34c10d3ec2b40d84d258cb4.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.13/bitbake-1c9ec1ffde75809de34c10d3ec2b40d84d258cb4.tar.bz2

