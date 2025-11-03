Release notes for Yocto-4.0.30 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.30
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  cups: Fix :cve_nist:`2025-58060` and :cve_nist:`2025-58364`
-  dpkg: Fix :cve_nist:`2025-6297`
-  ffmpeg: Fix :cve_nist:`2023-6602`, :cve_nist:`2023-6604`, :cve_nist:`2023-6605`,
   :cve_nist:`2025-1594` and CVE-2025-7700
-  git: Fix :cve_nist:`2025-27613`, :cve_nist:`2025-27614`, :cve_nist:`2025-46334`,
   :cve_nist:`2025-46835` and :cve_nist:`2025-48384`
-  glib-2.0: Fix :cve_nist:`2025-7039`
-  glib-2.0: Ignore :cve_nist:`2025-4056`
-  go: Ignore :cve_nist:`2024-24790` and :cve_nist:`2025-0913`
-  gstreamer1.0-plugins-base: Fix :cve_nist:`2025-47806`, :cve_nist:`2025-47807` and
   :cve_nist:`2025-47808`
-  gstreamer1.0-plugins-good: Fix :cve_nist:`2025-47183` and :cve_nist:`2025-47219`
-  libarchive: Fix :cve_nist:`2025-5918`
-  libxslt: Fix :cve_nist:`2023-40403`
-  openssl: Fix :cve_nist:`2023-50781`
-  python3: Fix :cve_nist:`2025-8194`
-  qemu: Ignore :cve_nist:`2024-7730`
-  sqlite3: Revert "sqlite3: patch CVE-2025-7458"
-  tiff: Fix :cve_nist:`2024-13978`, :cve_nist:`2025-8176`, :cve_nist:`2025-8177`,
   :cve_nist:`2025-8534` and :cve_nist:`2025-8851`
-  vim: Fix :cve_nist:`2025-53905` and :cve_nist:`2025-53906`
-  wpa-supplicant: Fix :cve_nist:`2022-37660`
-  xserver-xorg: Fix :cve_nist:`2025-49175`, :cve_nist:`2025-49176`, :cve_nist:`2025-49177`,
   :cve_nist:`2025-49178`, :cve_nist:`2025-49179` and :cve_nist:`2025-49180`


Fixes in Yocto-4.0.30
~~~~~~~~~~~~~~~~~~~~~

-  build-appliance-image: Update to kirkstone head revision
-  default-distrovars.inc: Fix CONNECTIVITY_CHECK_URIS redirect issue
-  dev-manual/security-subjects.rst: update mailing lists
-  gnupg: disable tests to avoid running target binaries at build time
-  go-helloworld: fix license
-  insane: Ensure that `src-uri-bad` fails correctly
-  insane: Improve patch warning/error handling
-  libubootenv: backport patch to fix unknown type name 'size_t'
-  llvm: fix typo in CVE-2024-0151.patch
-  migration-guides: add release notes for 4.0.29
-  overview-manual/yp-intro.rst: fix broken link to article
-  poky.conf: bump version for 4.0.30
-  pulseaudio: Add audio group explicitly
-  ref-manual/classes.rst: document the testexport class
-  ref-manual/system-requirements.rst: update supported distributions
-  ref-manual/variables.rst: document :term:`FIT_CONF_PREFIX` :term:`SPL_DTB_BINARY` variable
-  ref-manual/variables.rst: expand :term:`IMAGE_OVERHEAD_FACTOR` glossary entry
-  sdk: The main in the C example should return an int
-  sudo: remove devtool FIXME comment
-  systemd: Fix manpage build after :cve_nist:`2025-4598`
-  vim: not adjust script pathnames for native scripts either
-  vim: upgrade to 9.1.1652


Known Issues in Yocto-4.0.30
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-4.0.30
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Antonin Godard
-  Archana Polampalli
-  Dan McGregor
-  Deepak Rathore
-  Divya Chellam
-  Erik Lindsten
-  Guocai He
-  Gyorgy Sarvari
-  Hitendra Prajapati
-  Jan Vermaete
-  Jiaying Song
-  Joao Marcos Costa
-  Kyungjik Min
-  Lee Chee Yang
-  Mingli Yu
-  Peter Marko
-  Philip Lorenz
-  Praveen Kumar
-  Quentin Schulz
-  Richard Purdie
-  Steve Sakoman
-  Vijay Anusuri
-  Yogita Urade
-  Youngseok Jeong


Repositories / Downloads for Yocto-4.0.30
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.30 </poky/log/?h=yocto-4.0.30>`
-  Git Revision: :yocto_git:`51dc9c464de0703bfbc6f1ee71ac9bea20933a45 </poky/commit/?id=51dc9c464de0703bfbc6f1ee71ac9bea20933a45>`
-  Release Artefact: poky-51dc9c464de0703bfbc6f1ee71ac9bea20933a45
-  sha: 2b5db0a07598df7684975c0839e6f31515a8e78d366503feb9917ef1ca56c0b2
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.30/poky-51dc9c464de0703bfbc6f1ee71ac9bea20933a45.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.30/poky-51dc9c464de0703bfbc6f1ee71ac9bea20933a45.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.30 </openembedded-core/log/?h=yocto-4.0.30>`
-  Git Revision: :oe_git:`d381eeb5e70bd0ce9e78032c909e4a23564f4dd7 </openembedded-core/commit/?id=d381eeb5e70bd0ce9e78032c909e4a23564f4dd7>`
-  Release Artefact: oecore-d381eeb5e70bd0ce9e78032c909e4a23564f4dd7
-  sha: 022ab4ef5ac59ac3f01a9dacd8b1d6310cc117c6bed2e86e195ced88e0689c85
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.30/oecore-d381eeb5e70bd0ce9e78032c909e4a23564f4dd7.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.30/oecore-d381eeb5e70bd0ce9e78032c909e4a23564f4dd7.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.30 </meta-mingw/log/?h=yocto-4.0.30>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.30/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.30/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.30 </meta-gplv2/log/?h=yocto-4.0.30>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.30/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.30/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.30 </bitbake/log/?h=yocto-4.0.30>`
-  Git Revision: :oe_git:`8e2d1f8de055549b2101614d85454fcd1d0f94b2 </bitbake/commit/?id=8e2d1f8de055549b2101614d85454fcd1d0f94b2>`
-  Release Artefact: bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2
-  sha: fad4e7699bae62082118e89785324b031b0af0743064caee87c91ba28549afb0
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.30/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.30/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`kirkstone </meta-yocto/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.30 </meta-yocto/log/?h=yocto-4.0.30>`
-  Git Revision: :yocto_git:`edf7950e4d81dd31f29a58acdd8022dabd2be494 </meta-yocto/commit/?id=edf7950e4d81dd31f29a58acdd8022dabd2be494>`

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.30 </yocto-docs/log/?h=yocto-4.0.30>`
-  Git Revision: :yocto_git:`71a3933c609ce73ff07e5be48d9e7b03f22ef8d7 </yocto-docs/commit/?id=71a3933c609ce73ff07e5be48d9e7b03f22ef8d7>`

