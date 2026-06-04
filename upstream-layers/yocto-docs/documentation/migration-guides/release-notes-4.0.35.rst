.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.35 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.35
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  alsa-lib: Fix :cve_nist:`2026-25068`
-  busybox: Fix :cve_nist:`2025-60876`
-  curl: Fix :cve_nist:`2025-14524`, :cve_nist:`2026-1965`, :cve_nist:`2026-3783` and
   :cve_nist:`2026-3784`
-  ffmpeg: Fix :cve_nist:`2025-10256`
-  gdk-pixbuf: Fix :cve_nist:`2025-6199`
-  inetutils: Fix :cve_nist:`2026-28372`
-  libarchive: Fix :cve_nist:`2026-4111`
-  libpam: Fix :cve_nist:`2024-10963`
-  linux-yocto/5.15: Fix :cve_nist:`2025-40082`, :cve_nist:`2025-68358`, :cve_nist:`2025-71089`,
   :cve_nist:`2025-71220`, :cve_nist:`2025-71222`, :cve_nist:`2025-71232`, :cve_nist:`2025-71233`,
   :cve_nist:`2025-71235`, :cve_nist:`2025-71236`, :cve_nist:`2025-71237`, :cve_nist:`2025-71238`,
   :cve_nist:`2026-23111`, :cve_nist:`2026-23112`, :cve_nist:`2026-23169`, :cve_nist:`2026-23190`,
   :cve_nist:`2026-23193`, :cve_nist:`2026-23198`, :cve_nist:`2026-23202`, :cve_nist:`2026-23206`,
   :cve_nist:`2026-23209`, :cve_nist:`2026-23216`, :cve_nist:`2026-23221`, :cve_nist:`2026-23222`,
   :cve_nist:`2026-23228`, :cve_nist:`2026-23229`, :cve_nist:`2026-23231`, :cve_nist:`2026-23234`,
   :cve_nist:`2026-23235`, :cve_nist:`2026-23236`, :cve_nist:`2026-23237` and :cve_nist:`2026-23238`
-  ncurses: Fix :cve_nist:`2025-69720`
-  python3: Fix :cve_nist:`2024-6923`, :cve_nist:`2025-15282`, :cve_nist:`2025-59375`,
   :cve_nist:`2026-0865`, :cve_nist:`2026-24515` and :cve_nist:`2026-25210`
-  python3-pip: Fix :cve_nist:`2026-1703`
-  python3-pyopenssl: Fix :cve_nist:`2026-27448` and :cve_nist:`2026-27459`
-  sqlite3: Fix :cve_nist:`2025-70873`
-  tiff: Fix :cve_nist:`2025-61143` and :cve_nist:`2025-61144`
-  vim: Fix :cve_nist:`2026-25749`, :cve_nist:`2026-26269`, :cve_nist:`2026-28418`,
   :cve_nist:`2026-28419` and :cve_nist:`2026-33412`


Fixes in Yocto-4.0.35
~~~~~~~~~~~~~~~~~~~~~

-  bitbake: tests/fetch: Avoid using git protocol in tests
-  build-appliance-image: Update to kirkstone head revision
-  contributor-guide/submit-changes.rst: Added missing word
-  create-pull-request: Keep commit hash to be pulled in cover email
-  createrepo-c: Fix createrepo-c-native build on GCC14 hosts (e.g. Fedora 41)
-  gtk+3: fix incompatible-pointer-types errors for native build on Fedora 41
-  libcomps: Fix libcomps-native build on GCC14 hosts (e.g. Fedora 41)
-  libpam: re-add missing libgen include
-  libtheora: set :term:`CVE_PRODUCT`
-  linux-yocto/5.15: update to v5.15.201
-  lsb.py: strip ' from os-release file
-  migration-guide: add release notes for 4.0.33 4.0.34
-  oeqa/manual: Default to https git protocol for YP/OE repos
-  oeqa/sdk: Default to https git protocol for YP/OE repos
-  oeqa/selftest/git-submodule-test: Default to https git protocol for YP/OE repos
-  overview-manual: escape wildcard in inline markup
-  poky.conf: Bump version for 4.0.35 release
-  python3: upgrade to 3.10.20
-  README.OE-Core: update contributor links and add kirkstone prefix
-  recipes: Default to https git protocol for YP/OE repos
-  recipetool: Recognise https://git. as git urls
-  ref-manual/system-requirements.rst: update end-of-life distros
-  scripts/install-buildtools: Update to 4.0.34
-  scripts: Default to https git protocol for YP/OE repos
-  selftest/scripts: Update old git protocol references
-  tcl: skip http11 tests
-  tiff: set status of CVE-2025-61145 as fixed by patch for :cve_nist:`2025-8961`
-  tzdata,tzcode-native: Upgrade to 2026a


Known Issues in Yocto-4.0.35
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.35
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Antonin Godard
-  Bruce Ashfield
-  Fabien Thomas
-  Hitendra Prajapati
-  Jinfeng Wang
-  Ken Kurematsu
-  Kristiyan Chakarov
-  Lee Chee Yang
-  Martin Jansa
-  Paul Barker
-  Peter Marko
-  Richard Purdie
-  Ross Burton
-  Shaik Moin
-  Vijay Anusuri
-  Yanis BINARD
-  Yoann Congal

Repositories / Downloads for Yocto-4.0.35
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.35 </yocto-docs/log/?h=yocto-4.0.35>`
-  Git Revision: :yocto_git:`ce6734c68649739c635675a133fa77edb9865028 </yocto-docs/commit/?id=ce6734c68649739c635675a133fa77edb9865028>`
-  Release Artefact: yocto-docs-ce6734c68649739c635675a133fa77edb9865028
-  sha: ddb6fac4d257f4f76836055cafad529729e99c293d3b8d3dabef926fad5e725f
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.35/yocto-docs-ce6734c68649739c635675a133fa77edb9865028.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.35/yocto-docs-ce6734c68649739c635675a133fa77edb9865028.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.35 </poky/log/?h=yocto-4.0.35>`
-  Git Revision: :yocto_git:`93431249a6260da7bd29ee3ca32145d89e5b8259 </poky/commit/?id=93431249a6260da7bd29ee3ca32145d89e5b8259>`
-  Release Artefact: poky-93431249a6260da7bd29ee3ca32145d89e5b8259
-  sha: a8e95213248c5400276611754f2c98b8d8972e166bdf41433c45fcdd2bf668cb
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.35/poky-93431249a6260da7bd29ee3ca32145d89e5b8259.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.35/poky-93431249a6260da7bd29ee3ca32145d89e5b8259.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.35 </openembedded-core/log/?h=yocto-4.0.35>`
-  Git Revision: :oe_git:`51259c7e933a2ac8ebc01604d6e65607b76b7b56 </openembedded-core/commit/?id=51259c7e933a2ac8ebc01604d6e65607b76b7b56>`
-  Release Artefact: oecore-51259c7e933a2ac8ebc01604d6e65607b76b7b56
-  sha: 2cd531e2a107849e7a452e71e41f22b42160979066e10d0661e97acfab125b1f
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.35/oecore-51259c7e933a2ac8ebc01604d6e65607b76b7b56.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.35/oecore-51259c7e933a2ac8ebc01604d6e65607b76b7b56.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`kirkstone </meta-yocto/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.35 </meta-yocto/log/?h=yocto-4.0.35>`
-  Git Revision: :yocto_git:`34e3c9a19b8b955116109a2e9528966db3fced37 </meta-yocto/commit/?id=34e3c9a19b8b955116109a2e9528966db3fced37>`
-  Release Artefact: meta-yocto-34e3c9a19b8b955116109a2e9528966db3fced37
-  sha: 18da6dbb745d5e4e42a93527c36751778155e3762728b0b1020b890480402dde
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.35/meta-yocto-34e3c9a19b8b955116109a2e9528966db3fced37.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.35/meta-yocto-34e3c9a19b8b955116109a2e9528966db3fced37.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.35 </meta-mingw/log/?h=yocto-4.0.35>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.35/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.35/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.35 </meta-gplv2/log/?h=yocto-4.0.35>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.35/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.35/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.35 </bitbake/log/?h=yocto-4.0.35>`
-  Git Revision: :oe_git:`7fd0197fd5fedd23cc885b5e7e816d86a392fdf9 </bitbake/commit/?id=7fd0197fd5fedd23cc885b5e7e816d86a392fdf9>`
-  Release Artefact: bitbake-7fd0197fd5fedd23cc885b5e7e816d86a392fdf9
-  sha: 6c01ff2b4b0060ef3d6d3f1fc11690094b22865af4989946544d08d74b473ec9
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.35/bitbake-7fd0197fd5fedd23cc885b5e7e816d86a392fdf9.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.35/bitbake-7fd0197fd5fedd23cc885b5e7e816d86a392fdf9.tar.bz2

