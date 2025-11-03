Release notes for Yocto-4.0.29 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.29
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2024-52615`
-  binutils: Fix :cve_nist:`2025-7545` and :cve_nist:`2025-7546`
-  coreutils: Fix :cve_nist:`2025-5278`
-  curl: Fix :cve_nist:`2024-11053` and :cve_nist:`2025-0167`
-  dropbear: Fix :cve_nist:`2025-47203`
-  ffmpeg: Ignore :cve_nist:`2022-3109` and :cve_nist:`2022-3341`
-  gdk-pixbuf: Fix :cve_nist:`2025-7345`
-  ghostscript: Ignore :cve_nist:`2025-46646`
-  gnupg: Fix :cve_nist:`2025-30258`
-  gnutls: Fix :cve_nist:`2025-6395`, :cve_nist:`2025-32988`, :cve_nist:`2025-32989` and
   :cve_nist:`2025-32990`
-  iputils: Fix :cve_nist:`2025-48964`
-  libarchive: Fix :cve_nist:`2025-5914`, :cve_nist:`2025-5915`, :cve_nist:`2025-5916` and
   :cve_nist:`2025-5917`
-  libpam: Fix :cve_nist:`2025-6020`
-  libsoup-2.4: Fix :cve_nist:`2025-4945`
-  libsoup-2.4: Fix :cve_nist:`2025-4969` (update patch)
-  libsoup: Fix :cve_nist:`2025-4945`, :cve_nist:`2025-6021`, :cve_nist:`2025-6170`,
   :cve_nist:`2025-49794` and :cve_nist:`2025-49796`
-  ncurses: Fix :cve_nist:`2025-6141`
-  ofono: Fix :cve_nist:`2023-4232` and :cve_nist:`2023-4235`
-  openssl: Fix :cve_nist:`2024-41996`
-  python3-urllib3: Fix :cve_nist:`2025-50181`
-  ruby: Fix :cve_nist:`2024-43398` (update patches)
-  sqlite3: Fix :cve_nist:`2025-6965` and :cve_nist:`2025-7458`
-  sqlite3: Ignore :cve_nist:`2025-3277`
-  systemd: Fix :cve_nist:`2025-4598`
-  xwayland: Fix :cve_nist:`2025-49175`, :cve_nist:`2025-49176`, :cve_nist:`2025-49177`,
   :cve_nist:`2025-49178`, :cve_nist:`2025-49179` and :cve_nist:`2025-49180`


Fixes in Yocto-4.0.29
~~~~~~~~~~~~~~~~~~~~~

-  bintuils: stable 2.38 branch update
-  bitbake: test/fetch: Switch u-boot based test to use our own mirror
-  build-appliance-image: Update to kirkstone head revision
-  conf.py: improve SearchEnglish to handle terms with dots
-  db: ignore implicit-int and implicit-function-declaration issues fatal with gcc-14
-  dev-manual/start.rst: added missing command in Optimize your VHDX file using DiskPart
-  glibc: stable 2.35 branch updates
-  gnutls: patch read buffer overrun in the "pre_shared_key" extension
-  gnutls: patch reject zero-length version in certificate request
-  linux-yocto/5.15: update to v5.15.186
-  migration-guides: add release notes for 4.0.28
-  oeqa/core/decorator: add decorators to skip based on :term:`HOST_ARCH`
-  openssl: upgrade to 3.0.17
-  orc: set :term:`CVE_PRODUCT`
-  overview-manual/concepts.rst: fix sayhello hardcoded bindir
-  poky.conf: bump version for 4.0.29
-  python3: update CVE product
-  ref-manual: document :term:`KERNEL_SPLIT_MODULES` variable
-  scripts/install-buildtools: Update to 4.0.28
-  sudo: upgrade to 1.9.17p1
-  tcf-agent: correct the :term:`SRC_URI`


Known Issues in Yocto-4.0.29
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A 


Contributors to Yocto-4.0.29
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Antonin Godard
-  Archana Polampalli
-  Bruce Ashfield
-  Changqing Li
-  Chen Qi
-  Colin Pinnell McAllister
-  Daniel Díaz
-  Deepesh Varatharajan
-  Divya Chellam
-  Dixit Parmar
-  Enrico Jörns
-  Guocai He
-  Hitendra Prajapati
-  Lee Chee Yang
-  Marco Cavallini
-  Martin Jansa
-  Peter Marko
-  Praveen Kumar
-  Richard Purdie
-  Rob Woolley
-  Ross Burton
-  Steve Sakoman
-  Vijay Anusuri
-  Yash Shinde
-  Yogita Urade
-  Zhang Peng


Repositories / Downloads for Yocto-4.0.29
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.29 </poky/log/?h=yocto-4.0.29>`
-  Git Revision: :yocto_git:`81ab000fa437ca04f584a3327b076f7a512dc6d0 </poky/commit/?id=81ab000fa437ca04f584a3327b076f7a512dc6d0>`
-  Release Artefact: poky-81ab000fa437ca04f584a3327b076f7a512dc6d0
-  sha: 2fecf3cac5c2361c201b5ae826960af92289862ec9be13837a8431138e534fd2
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.29/poky-81ab000fa437ca04f584a3327b076f7a512dc6d0.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.29/poky-81ab000fa437ca04f584a3327b076f7a512dc6d0.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.29 </openembedded-core/log/?h=yocto-4.0.29>`
-  Git Revision: :oe_git:`bd620eb14660075fd0f7476bbbb65d5da6293874 </openembedded-core/commit/?id=bd620eb14660075fd0f7476bbbb65d5da6293874>`
-  Release Artefact: oecore-bd620eb14660075fd0f7476bbbb65d5da6293874
-  sha: f32ab195c7090268e6e87ccf8db2813cf705c517030654326d14b25d926de88e
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.29/oecore-bd620eb14660075fd0f7476bbbb65d5da6293874.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.29/oecore-bd620eb14660075fd0f7476bbbb65d5da6293874.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.29 </meta-mingw/log/?h=yocto-4.0.29>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.29/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.29/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.29 </meta-gplv2/log/?h=yocto-4.0.29>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.29/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.29/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.29 </bitbake/log/?h=yocto-4.0.29>`
-  Git Revision: :oe_git:`8e2d1f8de055549b2101614d85454fcd1d0f94b2 </bitbake/commit/?id=8e2d1f8de055549b2101614d85454fcd1d0f94b2>`
-  Release Artefact: bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2
-  sha: fad4e7699bae62082118e89785324b031b0af0743064caee87c91ba28549afb0
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.29/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.29/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`kirkstone </meta-yocto/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.29 </meta-yocto/log/?h=yocto-4.0.29>`
-  Git Revision: :yocto_git:`e916d3bad58f955b73e2c67aba975e63cd191394 </meta-yocto/commit/?id=e916d3bad58f955b73e2c67aba975e63cd191394>`

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.29 </yocto-docs/log/?h=yocto-4.0.29>`
-  Git Revision: :yocto_git:`bf855ecaf4bec4cef9bbfea2e50caa65a8339828 </yocto-docs/commit/?id=bf855ecaf4bec4cef9bbfea2e50caa65a8339828>`

