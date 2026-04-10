Release notes for Yocto-4.0.33 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.33
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-1181`, :cve_nist:`2025-11494`, :cve_nist:`2025-11839` and
   :cve_nist:`2025-11840`
-  cups: Fix :cve_nist:`2025-58436` and :cve_nist:`2025-61915`
-  curl: Fix :cve_nist:`2025-14017`, :cve_nist:`2025-15079` and :cve_nist:`2025-15224`
-  dropbear: Fix :cve_nist:`2019-6111`
-  glib-2.0: Fix :cve_nist:`2025-13601`, :cve_nist:`2025-14087` and :cve_nist:`2025-14512`
-  gnupg: Fix :cve_nist:`2025-68973`
-  go: Fix :cve_nist:`2023-39323`, :cve_nist:`2025-61727` and :cve_nist:`2025-61729`
-  go: Fix :cve_nist:`2025-58187` (update patch)
-  grub: Fix :cve_nist:`2025-61661`, :cve_nist:`2025-61662`, :cve_nist:`2025-61663` and
   :cve_nist:`2025-61664`
-  libarchive: Fix :cve_nist:`2025-60753` (update patch)
-  libpcap: Fix :cve_nist:`2025-11961` and :cve_nist:`2025-11964`
-  libsoup: fix :cve_nist:`2025-12105`
-  libxslt: Fix :cve_nist:`2025-11731`
-  python3: Fix :cve_nist:`2025-13836`
-  python3-urllib3: Fix :cve_nist:`2025-66418`
-  qemu: Fix :cve_nist:`2025-12464`
-  qemu: Ignore :cve_nist:`2025-54566` and :cve_nist:`2025-54567`
-  rsync: Fix :cve_nist:`2025-10158`
-  util-linux: Fix :cve_nist:`2025-14104`


Fixes in Yocto-4.0.33
~~~~~~~~~~~~~~~~~~~~~

-  build-appliance-image: Update to kirkstone head revision
-  contributor-guide/recipe-style-guide.rst: explain difference between layer and recipe license(s)
-  cross.bbclass: Propagate dependencies to outhash
-  cups: allow unknown directives in conf files
-  docs: Add a new "Security" section
-  oeqa: Use 2.14 release of cpio instead of 2.13
-  overview-manual/yp-intro.rst: change removed ECOSYSTEM to ABOUT
-  overview-manual/yp-intro.rst: fix SDK type in bullet list
-  overview-manual/yp-intro.rst: link to YP members and participants
-  overview-manual: convert YP-flow-diagram.png to SVG
-  poky.conf: Bump version for 4.0.33 release
-  pseudo: Upgrade to 1.9.2+git125b020dd2
-  ref-manual/classes.rst: document the image-container class
-  ref-manual/release-process.rst: add a "Development Cycle" section
-  ref-manual/svg/releases.svg: mark styhead and walnascar EOL
-  ref-manual/svg/releases.svg: mark whinlatter as current release
-  ref-manual/variables.rst: document the :term:`CCACHE_TOP_DIR` variable
-  scripts/install-buildtools: Update to 4.0.31
-  test-manual/ptest.rst: detail the exit code and output requirements


Known Issues in Yocto-4.0.33
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.33
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Antonin Godard
-  Changqing Li
-  Deepesh Varatharajan
-  Hitendra Prajapati
-  Jiaying Song
-  Kai Kang
-  Khem Raj
-  Libo Chen
-  Liyin Zhang
-  Martin Jansa
-  Mingli Yu
-  Paul Barker
-  Peter Marko
-  Richard Purdie
-  Robert Yang
-  Vijay Anusuri
-  Yash Shinde

Repositories / Downloads for Yocto-4.0.33
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.33 </yocto-docs/log/?h=yocto-4.0.33>`
-  Git Revision: :yocto_git:`6799b1be5d48f4bf5dcd0b16c2dbc2e297d4ecd9 </yocto-docs/commit/?id=6799b1be5d48f4bf5dcd0b16c2dbc2e297d4ecd9>`
-  Release Artefact: yocto-docs-6799b1be5d48f4bf5dcd0b16c2dbc2e297d4ecd9
-  sha: 42a0eb89c8f87a9a966aecb8265f463486d4383cb67d1e67382ddf9d4d7f88b5
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.33/yocto-docs-6799b1be5d48f4bf5dcd0b16c2dbc2e297d4ecd9.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.33/yocto-docs-6799b1be5d48f4bf5dcd0b16c2dbc2e297d4ecd9.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.33 </poky/log/?h=yocto-4.0.33>`
-  Git Revision: :yocto_git:`ff118ede826a9ae45eb35025a5f7f612880fba01 </poky/commit/?id=ff118ede826a9ae45eb35025a5f7f612880fba01>`
-  Release Artefact: poky-ff118ede826a9ae45eb35025a5f7f612880fba01
-  sha: 2a8c24406fa96fc52728a96f25136a3fd7ee652eea6e12319a6b7c0457ccfdfd
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.33/poky-ff118ede826a9ae45eb35025a5f7f612880fba01.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.33/poky-ff118ede826a9ae45eb35025a5f7f612880fba01.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.33 </openembedded-core/log/?h=yocto-4.0.33>`
-  Git Revision: :oe_git:`036f76ea35c49a78d612093dcd8eb1fac7ded8d7 </openembedded-core/commit/?id=036f76ea35c49a78d612093dcd8eb1fac7ded8d7>`
-  Release Artefact: oecore-036f76ea35c49a78d612093dcd8eb1fac7ded8d7
-  sha: fc180ff224529fd73a7aec4a4cf5beb40fba17646ee694715cf603baba26610c
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.33/oecore-036f76ea35c49a78d612093dcd8eb1fac7ded8d7.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.33/oecore-036f76ea35c49a78d612093dcd8eb1fac7ded8d7.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`kirkstone </meta-yocto/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.33 </meta-yocto/log/?h=yocto-4.0.33>`
-  Git Revision: :yocto_git:`677379f21941363d50f9d946963542b4ccb7e27c </meta-yocto/commit/?id=677379f21941363d50f9d946963542b4ccb7e27c>`
-  Release Artefact: meta-yocto-677379f21941363d50f9d946963542b4ccb7e27c
-  sha: 90f52c406f4e69748b8d73eee07b8a1247d19cc29f4893174f110a034b10415f
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.33/meta-yocto-677379f21941363d50f9d946963542b4ccb7e27c.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.33/meta-yocto-677379f21941363d50f9d946963542b4ccb7e27c.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.33 </meta-mingw/log/?h=yocto-4.0.33>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.33/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.33/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.33 </meta-gplv2/log/?h=yocto-4.0.33>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.33/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.33/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.33 </bitbake/log/?h=yocto-4.0.33>`
-  Git Revision: :oe_git:`8e2d1f8de055549b2101614d85454fcd1d0f94b2 </bitbake/commit/?id=8e2d1f8de055549b2101614d85454fcd1d0f94b2>`
-  Release Artefact: bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2
-  sha: fad4e7699bae62082118e89785324b031b0af0743064caee87c91ba28549afb0
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.33/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.33/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

