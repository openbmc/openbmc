Release notes for Yocto-4.0.31 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.31
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-8225`, :cve_nist:`2025-11081`, :cve_nist:`2025-11082` and
   :cve_nist:`2025-11083`
-  busybox: Fix :cve_nist:`2025-46394`
-  cmake: Fix :cve_nist:`2025-9301`
-  curl: Fix :cve_nist:`2025-9086`
-  ffmpeg: Ignore :cve_nist:`2023-6603`
-  ffmpeg: mark :cve_nist:`2023-6601` as Fixed
-  ghostscript: Fix :cve_nist:`2025-59798`, :cve_nist:`2025-59799` and :cve_nist:`2025-59800`
-  git: Fix :cve_nist:`2025-48386`
-  glib-networking: Fix :cve_nist:`2025-60018` and :cve_nist:`2025-60019`
-  go: Fix :cve_nist:`2025-47906` and :cve_nist:`2025-47907`
-  grub2: Fix :cve_nist:`2024-56738`
-  grub: Ignore :cve_nist:`2024-2312`
-  gstreamer1.0-plugins-bad: Fix :cve_nist:`2025-3887`
-  gstreamer1.0: Ignore :cve_nist:`2025-2759`, :cve_nist:`2025-3887`, :cve_nist:`2025-47183`,
   :cve_nist:`2025-47219`, :cve_nist:`2025-47806`, :cve_nist:`2025-47807` and :cve_nist:`2025-47808`
-  python3-jinja2: Fix :cve_nist:`2024-56201`, :cve_nist:`2024-56326` and :cve_nist:`2025-27516`
-  libxml2: Fix :cve_nist:`2025-9714`
-  libxslt: Fix :cve_nist:`2025-7424`
-  lz4: Fix :cve_nist:`2025-62813`
-  openssl: Fix :cve_nist:`2025-9230` and :cve_nist:`2025-9232`
-  pulseaudio: Ignore :cve_nist:`2024-11586`
-  python3: Fix :cve_nist:`2024-6345`, :cve_nist:`2025-47273` and :cve_nist:`2025-59375`
-  qemu: Fix :cve_nist:`2024-8354`
-  tiff: Fix :cve_nist:`2025-8961`, :cve_nist:`2025-9165` and :cve_nist:`2025-9900`
-  vim: Fix :cve_nist:`2025-9389`


Fixes in Yocto-4.0.31
~~~~~~~~~~~~~~~~~~~~~

-  build-appliance-image: Update to kirkstone head revision
-  poky.conf: bump version for 4.0.31
-  ref-manual/classes.rst: document the relative_symlinks class
-  ref-manual/classes.rst: gettext: extend the documentation of the class
-  ref-manual/variables.rst: document the CCACHE_DISABLE, UNINATIVE_CHECKSUM, UNINATIVE_URL, USE_NLS,
   REQUIRED_COMBINED_FEATURES, REQUIRED_IMAGE_FEATURES, :term:`REQUIRED_MACHINE_FEATURES` variable
-  ref-manual/variables.rst: fix :term:`LAYERDEPENDS` description
-  dev-manual, test-manual: Update autobuilder output links
-  ref-manual/classes.rst: extend the uninative class documentation
-  python3: upgrade to 3.10.19
-  linux-yocto/5.15: update to v5.15.194
-  glibc: : PTHREAD_COND_INITIALIZER compatibility with pre-2.41 versions (bug 32786)
-  glibc: nptl Use all of g1_start and g_signals
-  glibc: nptl rename __condvar_quiesce_and_switch_g1
-  glibc: nptl Fix indentation
-  glibc: nptl Use a single loop in pthread_cond_wait instaed of a nested loop
-  glibc: Remove g_refs from condition variables
-  glibc: nptl Remove unnecessary quadruple check in pthread_cond_wait
-  glibc: nptl Remove unnecessary catch-all-wake in condvar group switch
-  glibc: nptl Update comments and indentation for new condvar implementation
-  glibc: pthreads NPTL lost wakeup fix 2
-  glibc: Remove partial BZ#25847 backport patches
-  vulnerabilities: update nvdcve file name
-  migration-guides: add release notes for 4.0.30
-  oeqa/sdk/cases/buildcpio.py: use gnu mirror instead of main server
-  selftest/cases/meta_ide.py: use use gnu mirror instead of main server
-  conf/bitbake.conf: use gnu mirror instead of main server
-  p11-kit: backport fix for handle :term:`USE_NLS` from master
-  systemd: backport fix for handle :term:`USE_NLS` from master
-  glibc: stable 2.35 branch updates
-  openssl: upgrade to 3.0.18
-  scripts/install-buildtools: Update to 4.0.30
-  ref-manual/variables.rst: fix the description of :term:`STAGING_DIR`
-  ref-manual/structure: document the auto.conf file
-  dev-manual/building.rst: add note about externalsrc variables absolute paths
-  ref-manual/variables.rst: fix the description of :term:`KBUILD_DEFCONFIG`
-  kernel-dev/common.rst: fix the in-tree defconfig description
-  test-manual/yocto-project-compatible.rst: fix a typo
-  contributor-guide: submit-changes: make "Crediting contributors" part of "Commit your changes"
-  contributor-guide: submit-changes: number instruction list in commit your changes
-  contributor-guide: submit-changes: reword commit message instructions
-  contributor-guide: submit-changes: make the Cc tag follow kernel guidelines
-  contributor-guide: submit-changes: align :term:`CC` tag description
-  contributor-guide: submit-changes: clarify example with Yocto bug ID
-  contributor-guide: submit-changes: fix improper bold string
-  libhandy: update git branch name
-  python3-jinja2: upgrade to 3.1.6
-  vim: upgrade to 9.1.1683


Known Issues in Yocto-4.0.31
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.31
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Adam Blank
-  Aleksandar Nikolic
-  Antonin Godard
-  Archana Polampalli
-  AshishKumar Mishra
-  Bruce Ashfield
-  Deepesh Varatharajan
-  Divya Chellam
-  Gyorgy Sarvari
-  Hitendra Prajapati
-  João Marcos Costa
-  Lee Chee Yang
-  Paul Barker
-  Peter Marko
-  Praveen Kumar
-  Quentin Schulz
-  Rajeshkumar Ramasamy
-  Saravanan
-  Soumya Sambu
-  Steve Sakoman
-  Sunil Dora
-  Talel BELHAJ SALEM
-  Theo GAIGE
-  Vijay Anusuri
-  Yash Shinde
-  Yogita Urade

Repositories / Downloads for Yocto-4.0.31
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.31 </yocto-docs/log/?h=yocto-4.0.31>`
-  Git Revision: :yocto_git:`073f3bca4c374b03398317e7f445d2440a287741 </yocto-docs/commit/?id=073f3bca4c374b03398317e7f445d2440a287741>`
-  Release Artefact: yocto-docs-073f3bca4c374b03398317e7f445d2440a287741
-  sha: 3bfde9b6ad310dd42817509b67f61cd69552f74b2bc5011bd20788fe96d6823b
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.31/yocto-docs-073f3bca4c374b03398317e7f445d2440a287741.tar.bz2
   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.31/yocto-docs-073f3bca4c374b03398317e7f445d2440a287741.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.31 </poky/log/?h=yocto-4.0.31>`
-  Git Revision: :yocto_git:`04b39e5b7eb19498215d85c88a5fffb460fea1eb </poky/commit/?id=04b39e5b7eb19498215d85c88a5fffb460fea1eb>`
-  Release Artefact: poky-04b39e5b7eb19498215d85c88a5fffb460fea1eb
-  sha: 0ca18ab1ed25c0d77412ba30dbb03d74811756c7c2fe2401940f848a5e734930
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.31/poky-04b39e5b7eb19498215d85c88a5fffb460fea1eb.tar.bz2
   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.31/poky-04b39e5b7eb19498215d85c88a5fffb460fea1eb.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.31 </openembedded-core/log/?h=yocto-4.0.31>`
-  Git Revision: :oe_git:`99204008786f659ab03538cd2ae2fd23ed4164c5 </openembedded-core/commit/?id=99204008786f659ab03538cd2ae2fd23ed4164c5>`
-  Release Artefact: oecore-99204008786f659ab03538cd2ae2fd23ed4164c5
-  sha: aa97bf826ad217b3a5278b4ad60bef4d194f0f1ff617677cf2323d3cc4897687
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.31/oecore-99204008786f659ab03538cd2ae2fd23ed4164c5.tar.bz2
   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.31/oecore-99204008786f659ab03538cd2ae2fd23ed4164c5.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`kirkstone </meta-yocto/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.31 </meta-yocto/log/?h=yocto-4.0.31>`
-  Git Revision: :yocto_git:`3b2df00345b46479237fe0218675a818249f891c </meta-yocto/commit/?id=3b2df00345b46479237fe0218675a818249f891c>`
-  Release Artefact: meta-yocto-3b2df00345b46479237fe0218675a818249f891c
-  sha: 630e99e0f515bab8a316b2e32aff1352b4404f15aa087e8821b84093596a08ce
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.31/meta-yocto-3b2df00345b46479237fe0218675a818249f891c.tar.bz2
   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.31/meta-yocto-3b2df00345b46479237fe0218675a818249f891c.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.31 </meta-mingw/log/?h=yocto-4.0.31>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.31/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2
   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.31/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.31 </meta-gplv2/log/?h=yocto-4.0.31>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.31/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.31/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.31 </bitbake/log/?h=yocto-4.0.31>`
-  Git Revision: :oe_git:`8e2d1f8de055549b2101614d85454fcd1d0f94b2 </bitbake/commit/?id=8e2d1f8de055549b2101614d85454fcd1d0f94b2>`
-  Release Artefact: bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2
-  sha: fad4e7699bae62082118e89785324b031b0af0743064caee87c91ba28549afb0
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.31/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2
   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.31/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

