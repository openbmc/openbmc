.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.23 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.23
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  ``curl``: Fix :cve_nist:`2024-9681`
-  ``expat``: Fix :cve_nist:`2024-50602`
-  ``gcc``: Ignore :cve_nist:`2023-4039`
-  ``ghostscript``: Fix :cve_nist:`2023-46361` and :cve_nist:`2024-29508`
-  ``gstreamer1.0``: Ignore :cve_nist:`2024-0444`
-  ``libarchive``: Fix :cve_nist:`2024-48957` and :cve_nist:`2024-48958`
-  ``openssl``: Fix :cve_nist:`2024-9143`
-  ``orc``: Fix :cve_nist:`2024-40897`
-  ``python3``: Ignore :cve_nist:`2023-27043`, :cve_nist:`2024-6232` and :cve_nist:`2024-7592`
-  ``qemu``: Fix :cve_nist:`2023-3019`
-  ``vim``: Fix :cve_nist:`2024-43790`, :cve_nist:`2024-43802`, :cve_nist:`2024-45306` and :cve_nist:`2024-47814`
-  ``zstd``: Fix :cve_nist:`2022-4899`


Fixes in Yocto-4.0.23
~~~~~~~~~~~~~~~~~~~~~

-  at-spi2-core: backport a patch to fix build with gcc-14 on host
-  bitbake: bitbake: doc/user-manual: Update the BB_HASHSERVE_UPSTREAM
-  bitbake: codeparser: Fix handling of string AST nodes with older Python versions
-  bitbake: fetch2/git: Use quote from shlex, not pipes
-  bitbake: gitsm: Add call_process_submodules() to remove duplicated code
-  bitbake: gitsm: Remove downloads/tmpdir when failed
-  bitbake: tests/fetch: Use our own mirror of mobile-broadband-provider to decouple from gnome gitlab
-  bitbake: tests/fetch: Use our own mirror of sysprof to decouple from gnome gitlab
-  bmap-tools: update :term:`HOMEPAGE` and :term:`SRC_URI`
-  build-appliance-image: Update to kirkstone head revision
-  cmake: Fix sporadic issues when determining compiler internals
-  cracklib: Modify patch to compile with GCC 14
-  cve-check: add CVSS vector string to CVE database and reports
-  cve-check: add support for cvss v4.0
-  cve_check: Use a local copy of the database during builds
-  dev-manual: document how to provide confs from layer.conf
-  documentation: Makefile: add SPHINXLINTDOCS to specify subset to sphinx-lint
-  documentation: Makefile: fix epub and latexpdf targets
-  documentation: README: add instruction to run Vale on a subset
-  documentation: brief-yoctoprojectqs: update BB_HASHSERVE_UPSTREAM for new infrastructure
-  documentation: conf.py: add a bitbake_git extlink
-  documentation: rename :cve: role to :cve_nist:
-  documentation: styles: vocabularies: Yocto: add sstate
-  documnetation: contributor-guide: Remove duplicated words
-  gcc: restore a patch for Neoverse N2 core
-  glib-2.0: patch regression of :cve_nist:`2023-32665`
-  kmscube: create_framebuffer: backport modifier fix
-  libffi: backport a fix to build libffi-native with gcc-14
-  linux-firmware: Upgrade to 20240909
-  local.conf.sample: update BB_HASHSERVE_UPSTREAM for new infrastructure
-  migration-guide: add release notes for 4.0.22
-  migration-guide: release-notes-4.0: update BB_HASHSERVE_UPSTREAM for new infrastructure
-  nativesdk-intercept: Fix bad intercept chgrp/chown logic
-  orc: Upgrade to  0.4.40
-  overlayfs-etc: add option to skip creation of mount dirs
-  overview-manual: concepts: add details on package splitting
-  package: Switch debug source handling to use prefix map
-  patch.py: Use shlex instead of deprecated pipe
-  poky.conf: bump version for 4.0.23
-  pseudo: Disable LFS on 32bit arches
-  pseudo: Fix envp bug and add posix_spawn wrapper
-  pseudo: Fix to work with glibc 2.40
-  pseudo: Switch back to the master branch
-  pseudo: Update to include logic fix
-  pseudo: Update to include open symlink handling bugfix
-  pseudo: Update to pull in fchmodat fix
-  pseudo: Update to pull in fd leak fix
-  pseudo: Update to pull in gcc14 fix and missing statvfs64 intercept
-  pseudo: Update to pull in linux-libc-headers race fix
-  pseudo: Update to pull in python 3.12+ fix
-  pseudo: Update to pull in syncfs probe fix
-  ref-manual: add description for the "sysroot" term
-  ref-manual: add missing CVE_CHECK manifest variables
-  ref-manual: add missing :term:`EXTERNAL_KERNEL_DEVICETREE` variable
-  ref-manual: add missing :term:`OPKGBUILDCMD` variable
-  ref-manual: devtool-reference: document missing commands
-  ref-manual: devtool-reference: refresh example outputs
-  ref-manual: introduce :term:`CVE_CHECK_REPORT_PATCHED` variable
-  ref-manual: release-process: add a reference to the doc's release
-  ref-manual: release-process: refresh the current LTS releases
-  ref-manual: release-process: update releases.svg
-  ref-manual: release-process: update releases.svg with month after "Current"
-  ref-manual: structure.rst: document missing tmp/ dirs
-  ref-manual: variables: add SIGGEN_LOCKEDSIGS* variables
-  syslinux: Disable error on implicit-function-declaration
-  util-linux: Define pidfd_* function signatures
-  vala: add -Wno-error=incompatible-pointer-types work around
-  vim: Upgrade to 9.1.0764
-  xmlto: backport a patch to fix build with gcc-14 on host
-  zip: Fix build with gcc-14
-  zip: Make configure checks to be more robust


Known Issues in Yocto-4.0.23
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-N/A


Contributors to Yocto-4.0.23
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Alexandre Belloni
-  Antoine Lubineau
-  Antonin Godard
-  Archana Polampalli
-  Ashish Sharma
-  Baruch Siach
-  Eilís 'pidge' Ní Fhlannagáin
-  Jose Quaresma
-  Julien Stephan
-  Khem Raj
-  Lee Chee Yang
-  Macpaul Lin
-  Martin Jansa
-  Michael Opdenacker
-  Ola x Nilsson
-  Peter Marko
-  Philip Lorenz
-  Randolph Sapp
-  Richard Purdie
-  Robert Yang
-  Rohini Sangam
-  Ruiqiang Hao
-  Siddharth Doshi
-  Steve Sakoman
-  Talel BELHAJSALEM
-  Wang Mingyu
-  Yogita Urade
-  Zoltan Boszormenyi


Repositories / Downloads for Yocto-4.0.23
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.23 </poky/log/?h=yocto-4.0.23>`
-  Git Revision: :yocto_git:`8e092852b63e998d990b8f8e1aa91297dec4430f </poky/commit/?id=8e092852b63e998d990b8f8e1aa91297dec4430f>`
-  Release Artefact: poky-8e092852b63e998d990b8f8e1aa91297dec4430f
-  sha: 339d34d8432070dac948449e732ebf06a888eeb27ff548958b2395c9446b029d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.23/poky-8e092852b63e998d990b8f8e1aa91297dec4430f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.23/poky-8e092852b63e998d990b8f8e1aa91297dec4430f.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.23 </openembedded-core/log/?h=yocto-4.0.23>`
-  Git Revision: :oe_git:`fb45c5cf8c2b663af293acb069d446610f77ff1a </openembedded-core/commit/?id=fb45c5cf8c2b663af293acb069d446610f77ff1a>`
-  Release Artefact: oecore-fb45c5cf8c2b663af293acb069d446610f77ff1a
-  sha: 1d394370ea7d43fb885ab8a952d6d1e43f1a850745a5152d5ead5565a283a0f5
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.23/oecore-fb45c5cf8c2b663af293acb069d446610f77ff1a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.23/oecore-fb45c5cf8c2b663af293acb069d446610f77ff1a.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.23 </meta-mingw/log/?h=yocto-4.0.23>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.23/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.23/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.23 </meta-gplv2/log/?h=yocto-4.0.23>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.23/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.23/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.23 </bitbake/log/?h=yocto-4.0.23>`
-  Git Revision: :oe_git:`fb73c495c45d1d4107cfd60b67a5b4f11a99647b </bitbake/commit/?id=fb73c495c45d1d4107cfd60b67a5b4f11a99647b>`
-  Release Artefact: bitbake-fb73c495c45d1d4107cfd60b67a5b4f11a99647b
-  sha: 5cd271299951f25912a2e8d4de6d8769a4c0bb3bbcfc90815be41f23fd299a0b
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.23/bitbake-fb73c495c45d1d4107cfd60b67a5b4f11a99647b.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.23/bitbake-fb73c495c45d1d4107cfd60b67a5b4f11a99647b.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.23 </yocto-docs/log/?h=yocto-4.0.23>`
-  Git Revision: :yocto_git:`TBD </yocto-docs/commit/?id=TBD>`


