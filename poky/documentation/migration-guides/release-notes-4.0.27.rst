Release notes for Yocto-4.0.27 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.27
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-1178`
-  busybox: fix :cve_nist:`2023-39810`
-  connman :fix :cve_nist:`2025-32743`
-  curl: Ignore :cve_nist:`2025-0725`
-  ghostscript: Fix :cve_nist:`2025-27830`, :cve_nist:`2025-27831`, :cve_nist:`2025-27832`,
   :cve_nist:`2025-27834`, :cve_nist:`2025-27835` and :cve_nist:`2025-27836`
-  ghostscript: Ignore :cve_nist:`2024-29507`, :cve_nist:`2025-27833` and :cve_nist:`2025-27837`
-  glib-2.0: Fix :cve_nist:`2025-3360`
-  go: Fix :cve_nist:`2025-22871`
-  libarchive: Ignore :cve_nist:`2024-48615`
-  libpam: Fix :cve_nist:`2024-10041`
-  libsoup-2.4: Fix :cve_nist:`2024-52532`, :cve_nist:`2025-32906` and :cve_nist:`2025-32909`
-  libsoup: Fix :cve_nist:`2024-52532`, :cve_nist:`2025-32906`, :cve_nist:`2025-32909`,
   :cve_nist:`2025-32910`, :cve_nist:`2025-32911`, :cve_nist:`2025-32912`, :cve_nist:`2025-32913`
   and :cve_nist:`2025-32914`
-  libxml2: Fix :cve_nist:`2025-32414` and :cve_nist:`2025-32415`
-  ofono: Fix :cve_nist:`2024-7537`
-  perl: Fix :cve_nist:`2024-56406`
-  ppp: Fix :cve_nist:`2024-58250`
-  python3-setuptools: Fix :cve_nist:`2024-6345`
-  qemu: Ignore :cve_nist:`2023-1386`
-  ruby: Fix :cve_nist:`2024-43398`
-  sqlite3: Fix :cve_nist:`2025-29088`
-  systemd: Ignore :cve_nist:`2022-3821`, :cve_nist:`2022-4415` and :cve_nist:`2022-45873`


Fixes in Yocto-4.0.27
~~~~~~~~~~~~~~~~~~~~~

-  Revert "cve-update-nvd2-native: Tweak to work better with NFS DL_DIR"
-  build-appliance-image: Update to kirkstone head revision
-  cve-update-nvd2-native: add workaround for json5 style list
-  docs: Fix dead links that use the :term:`DISTRO` macro
-  docs: manuals: remove repeated word
-  docs: poky.yaml: introduce DISTRO_LATEST_TAG
-  glibc: Add single-threaded fast path to rand()
-  glibc: stable 2.35 branch updates
-  module.bbclass: add KBUILD_EXTRA_SYMBOLS to install
-  perl: enable _GNU_SOURCE define via d_gnulibc
-  poky.conf: bump version for 4.0.27
-  ref-manual/variables.rst: document autotools class related variables
-  scripts/install-buildtools: Update to 4.0.26
-  systemd: backport patch to fix journal issue
-  systemd: systemd-journald fails to setup LogNamespace
-  tzdata/tzcode-native: upgrade to 2025b


Known Issues in Yocto-4.0.27
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.27
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Alexander Kanavin
-  Alon Bar-Lev
-  Andrew Kreimer
-  Antonin Godard
-  Chen Qi
-  Deepesh Varatharajan
-  Divya Chellam
-  Haitao Liu
-  Haixiao Yan
-  Hitendra Prajapati
-  Peter Marko
-  Praveen Kumar
-  Priyal Doshi
-  Shubham Kulkarni
-  Soumya Sambu
-  Steve Sakoman
-  Vijay Anusuri
-  Yogita Urade


Repositories / Downloads for Yocto-4.0.27
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.27 </poky/log/?h=yocto-4.0.27>`
-  Git Revision: :yocto_git:`ab9a994a8cd8e06b519a693db444030999d273b7 </poky/commit/?id=ab9a994a8cd8e06b519a693db444030999d273b7>`
-  Release Artefact: poky-ab9a994a8cd8e06b519a693db444030999d273b7
-  sha: 77a366c17cf29eef15c6ff3f44e73f81c07288c723fd4a6dbd8c7ee9b79933f3
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.27/poky-ab9a994a8cd8e06b519a693db444030999d273b7.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.27/poky-ab9a994a8cd8e06b519a693db444030999d273b7.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.27 </openembedded-core/log/?h=yocto-4.0.27>`
-  Git Revision: :oe_git:`e8be08a624b2d024715a5c8b0c37f2345a02336b </openembedded-core/commit/?id=e8be08a624b2d024715a5c8b0c37f2345a02336b>`
-  Release Artefact: oecore-e8be08a624b2d024715a5c8b0c37f2345a02336b
-  sha: cc5b0fadab021c6dc61f37fc4ff01a1cf657e7c219488ce264bede42f7f6212f
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.27/oecore-e8be08a624b2d024715a5c8b0c37f2345a02336b.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.27/oecore-e8be08a624b2d024715a5c8b0c37f2345a02336b.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.27 </meta-mingw/log/?h=yocto-4.0.27>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.27/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.27/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.27 </meta-gplv2/log/?h=yocto-4.0.27>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.27/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.27/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.27 </bitbake/log/?h=yocto-4.0.27>`
-  Git Revision: :oe_git:`046871d9fd76efdca7b72718b328d8f545523f7e </bitbake/commit/?id=046871d9fd76efdca7b72718b328d8f545523f7e>`
-  Release Artefact: bitbake-046871d9fd76efdca7b72718b328d8f545523f7e
-  sha: e9df0a9f5921b583b539188d66b23f120e1751000e7822e76c3391d5c76ee21a
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.27/bitbake-046871d9fd76efdca7b72718b328d8f545523f7e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.27/bitbake-046871d9fd76efdca7b72718b328d8f545523f7e.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.27 </yocto-docs/log/?h=yocto-4.0.27>`
-  Git Revision: :yocto_git:`0d51e553d5f83eea6634e03ddc9c7740bf72fcea </yocto-docs/commit/?id=0d51e553d5f83eea6634e03ddc9c7740bf72fcea>`

