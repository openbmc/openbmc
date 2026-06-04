.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.3.4 (Whinlatter)
------------------------------------------

Security Fixes in Yocto-5.3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-69644`, :cve_nist:`2025-69647`, :cve_nist:`2025-69648`,
   :cve_nist:`2025-69649` and :cve_nist:`2025-69652`
-  binutils: Ignore :cve_nist:`2025-69650` and :cve_nist:`2025-69651`
-  curl: Fix :cve_nist:`2026-1965`, :cve_nist:`2026-3783`, :cve_nist:`2026-3784` and
   :cve_nist:`2026-3805`
-  expat: Fix :cve_nist:`2026-32776`, :cve_nist:`2026-32777` and :cve_nist:`2026-32778`
-  glibc: Fix :cve_nist:`2026-4437` and :cve_nist:`2026-4438`
-  go: Fix :cve_nist:`2026-27140`, :cve_nist:`2026-27143`, :cve_nist:`2026-27144`,
   :cve_nist:`2026-32280`, :cve_nist:`2026-32281`, :cve_nist:`2026-32282`, :cve_nist:`2026-32283`,
   :cve_nist:`2026-32288` and :cve_nist:`2026-32289`
-  inetutils: Fix :cve_nist:`2026-32746`
-  libpng: Fix :cve_nist:`2026-33416` and :cve_nist:`2026-33636`
-  libsoup: fix :cve_nist:`2025-32049` and :cve_nist:`2026-1539`
-  nfs-utils: Fix :cve_nist:`2025-12801`
-  openssl: Fix :cve_nist:`2026-2673`, :cve_nist:`2026-28387`, :cve_nist:`2026-28388`,
   :cve_nist:`2026-28389`, :cve_nist:`2026-28390`, :cve_nist:`2026-31789` and :cve_nist:`2026-31790`
-  python3-pyopenssl: Fix :cve_nist:`2026-27448` and :cve_nist:`2026-27459`
-  python3: Fix :cve_nist:`2025-15282`
-  sqlite3: Fix :cve_nist:`2025-70873`
-  vim: Fix :cve_nist:`2026-25749`, :cve_nist:`2026-26269`, :cve_nist:`2026-28418`,
   :cve_nist:`2026-28419` and :cve_nist:`2026-33412`


Fixes in Yocto-5.3.4
~~~~~~~~~~~~~~~~~~~~

bitbake
^^^^^^^
-  runqueue.py: make sure we use bb multiprocessing
-  tests/fetch: Avoid using git protocol in tests

meta-yocto
^^^^^^^^^^
-  poky.conf: Bump version for 5.3.4 release
-  poky: Fix CentOS Stream 9 distro name

openembedded-core
^^^^^^^^^^^^^^^^^
-  archiver: Don't try to preserve all attributes when copying files
-  barebox/barebox-tools: upgrade to 2025.09.3
-  binutils: Fix build with GLIBC 2.43 on the host
-  binutils: Upgrade to 2.45.1 release
-  build-appliance-image: switch :term:`SRC_URI` to https protocol
-  build-appliance-image: Update to whinlatter head revisions
-  ca-certificates: upgrade to 20260223
-  ccache: upgrade to 4.12.3
-  dtc: backport fix for build with glibc-2.43
-  gcc: backport a fix for building with gcc-16
-  gettext: backport gnulib changes to fix build with glibc-2.43 on host
-  glibc: stable 2.42 branch updates (a56a2943d2c...)
-  go: upgrade to 1.25.9
-  libarchive: upgrade to 3.8.6
-  libpng: upgrade to 1.6.56
-  libsoup: upgrade to 3.6.6
-  libxcrypt: avoid discarded-qualifiers build failure with glibc 2.43
-  libxcrypt: Fix build wrt C23 support
-  libxcrypt: Use configure knob to disable warnings as errors
-  libxmlb: upgrade to 0.3.25
-  license.py: Drop visit_Str from SeenVisitor in selftest
-  m4: backport 3 gnulib changes to fix build with glibc-2.43 on host
-  meta/files/layers.example.json: switch to https clone URIs
-  oe-setup-build: :term:`TEMPLATECONF` were not applied correctly
-  oeqa/sdk: Default to https git protocol for YP/OE repos
-  oeqa/selftest/devtool: add vulkan feature check for test needing it
-  oeqa/selftest/git-submodule-test: Default to https git protocol for YP/OE repos
-  oeqa/selftest: add wayland feature check for tests needing it
-  openssl: upgrade to 3.5.6
-  ovmf: backport a fix for build with gcc-16
-  pseudo: Add fix for glibc 2.43
-  ptest-runner: Upgrade to 2.5.1
-  ptest-runner: Use git tag in :term:`SRC_URI`
-  python3-pip: drop unused Windows distlib launcher templates
-  python3-setuptools: drop Windows launcher executables on non-mingw builds
-  python3: Fix test under ptest-runner 2.5.1
-  python3: upgrade to 3.13.12
-  recipetool: Recognise https://git. as git urls
-  report-error.bbclass: replace 'codecs.open()' with 'open()'
-  scripts/install-buildtools: Update to 5.3.3
-  scripts: Default to https git protocol for YP/OE repos
-  selftest/gdbserver: replace shutil.unpack_archive with tarfile extract
-  selftest/minidebuginfo: extract files from tar archive using tarfile module
-  selftest/scripts: Update old git protocol references
-  spirv-tools: backport a fix for building with gcc-16
-  systemd: backport fix to build with glibc-2.43 on host
-  tzdata/tzcode-native: upgrade to 2026a
-  util-linux: backport fix to build with glibc-2.43 on host
-  virglrenderer: Fix build with glibc 2.43+
-  yocto-uninative: Update to 5.1 for glibc 2.43

yocto-docs
^^^^^^^^^^
-  contributor-guide/submit-changes.rst: Added missing word
-  dev-manual/new-recipe.rst: rework Unpacking Code section
-  migration-guide: add release notes for 4.0.33 4.0.34 5.0.16 5.0.17 5.3.2 5.3.3
-  sphinx-static/theme_overrides.css: switch to a fixed width documentation


Known Issues in Yocto-5.3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-5.3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adarsh Jagadish Kamini
-  Alexander Kanavin
-  Andrej Kozemcak
-  Anil Dongare
-  Ankur Tyagi
-  Antonin Godard
-  Changqing Li
-  Deepak Rathore
-  Hemanth Kumar M D
-  Hitendra Prajapati
-  Jinfeng Wang
-  Khem Raj
-  Krupal Ka Patel
-  Lee Chee Yang
-  Logan Gallois
-  Martin Jansa
-  Michael Halstead
-  Paul Barker
-  Peter Marko
-  Richard Purdie
-  Sunil Dora
-  Trevor Gamblin
-  Vijay Anusuri
-  Wang Mingyu
-  Yanis BINARD
-  Yoann Congal
-  Zoltán Böszörményi


Repositories / Downloads for Yocto-5.3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`whinlatter </yocto-docs/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.4 </yocto-docs/log/?h=yocto-5.3.4>`
-  Git Revision: :yocto_git:`10818445a22c524de0934901547cf676256c2b9d </yocto-docs/commit/?id=10818445a22c524de0934901547cf676256c2b9d>`
-  Release Artefact: yocto-docs-10818445a22c524de0934901547cf676256c2b9d
-  sha: f9e0b155730ba23b83febd40b6e5d6e22c624d083cccb569cfff4a83ef3fde43
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.4/yocto-docs-10818445a22c524de0934901547cf676256c2b9d.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.4/yocto-docs-10818445a22c524de0934901547cf676256c2b9d.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`whinlatter </openembedded-core/log/?h=whinlatter>`
-  Tag:  :oe_git:`yocto-5.3.4 </openembedded-core/log/?h=yocto-5.3.4>`
-  Git Revision: :oe_git:`8751ec83421192fc0f8495fb95798f9eb7be77a0 </openembedded-core/commit/?id=8751ec83421192fc0f8495fb95798f9eb7be77a0>`
-  Release Artefact: oecore-8751ec83421192fc0f8495fb95798f9eb7be77a0
-  sha: d587dc39a046a1a48f4cd33f5e9dd79b7d0948d71a9151fcf5542641893be229
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.4/oecore-8751ec83421192fc0f8495fb95798f9eb7be77a0.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.4/oecore-8751ec83421192fc0f8495fb95798f9eb7be77a0.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`whinlatter </meta-yocto/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.4 </meta-yocto/log/?h=yocto-5.3.4>`
-  Git Revision: :yocto_git:`3f2e6b6fadfa542f0fe3738375a2f9fcbd4c1f6e </meta-yocto/commit/?id=3f2e6b6fadfa542f0fe3738375a2f9fcbd4c1f6e>`
-  Release Artefact: meta-yocto-3f2e6b6fadfa542f0fe3738375a2f9fcbd4c1f6e
-  sha: b942dc726a631b52284d4fe62dda4b97d880fea758a7090a8590cca964865e30
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.4/meta-yocto-3f2e6b6fadfa542f0fe3738375a2f9fcbd4c1f6e.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.4/meta-yocto-3f2e6b6fadfa542f0fe3738375a2f9fcbd4c1f6e.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`whinlatter </meta-mingw/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.4 </meta-mingw/log/?h=yocto-5.3.4>`
-  Git Revision: :yocto_git:`00323de97e397d4f6734ef2191806616989f5e10 </meta-mingw/commit/?id=00323de97e397d4f6734ef2191806616989f5e10>`
-  Release Artefact: meta-mingw-00323de97e397d4f6734ef2191806616989f5e10
-  sha: c9a70539b12c0642596fde6a2766d4a6a8fec8b2a366453fb6473363127a1c77
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.4/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.4/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.16 </bitbake/log/?h=2.16>`
-  Tag:  :oe_git:`yocto-5.3.4 </bitbake/log/?h=yocto-5.3.4>`
-  Git Revision: :oe_git:`713fbbdb9ecc195ceb2216a2345e0d79dbee2135 </bitbake/commit/?id=713fbbdb9ecc195ceb2216a2345e0d79dbee2135>`
-  Release Artefact: bitbake-713fbbdb9ecc195ceb2216a2345e0d79dbee2135
-  sha: 1d0f14e8bc71a39de4207aeeb329cb1d2541089c07e954131bf37a3d1b66b823
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.4/bitbake-713fbbdb9ecc195ceb2216a2345e0d79dbee2135.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.4/bitbake-713fbbdb9ecc195ceb2216a2345e0d79dbee2135.tar.bz2

