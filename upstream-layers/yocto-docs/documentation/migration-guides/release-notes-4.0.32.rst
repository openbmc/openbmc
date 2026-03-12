Release notes for Yocto-4.0.32 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.32
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve_nist:`2025-8677`, :cve_nist:`2025-40778` and :cve_nist:`2025-40780`
-  binutils: Fix :cve_nist:`2025-11412` and :cve_nist:`2025-11413`
-  curl: Ignore :cve_nist:`2025-10966`
-  elfutils: Fix :cve_nist:`2025-1376` and :cve_nist:`2025-1377`
-  gnutls: Fix :cve_nist:`2025-9820`
-  go: Fix :cve_nist:`2024-24783`, :cve_nist:`2025-58187`, :cve_nist:`2025-58189`,
   :cve_nist:`2025-61723` and :cve_nist:`2025-61724`
-  libarchive: Fix :cve_nist:`2025-60753`
-  libarchive: Fix 2 security issue (https://github.com/libarchive/libarchive/pull/2753 and
   https://github.com/libarchive/libarchive/pull/2768)
-  libpng: Fix :cve_nist:`2025-64505`, :cve_nist:`2025-64506`, :cve_nist:`2025-64720`,
   :cve_nist:`2025-65018` and :cve_nist:`2025-66293`
-  libxml2: Fix :cve_nist:`2025-7425`
-  musl: Fix :cve_nist:`2025-26519`
-  openssh: Fix :cve_nist:`2025-61984` and :cve_nist:`2025-61985`
-  python3-idna: Fix :cve_nist:`2024-3651`
-  python3-urllib3: Fix :cve_nist:`2024-37891`
-  python3: fix :cve_nist:`2025-6075`
-  ruby: Fix :cve_nist:`2024-35176`, :cve_nist:`2024-39908` and :cve_nist:`2024-41123`
-  rust-cross-canadian: Ignore :cve_nist:`2024-43402`
-  u-boot: Fix :cve_nist:`2024-42040`
-  wpa-supplicant: Fix :cve_nist:`2025-24912`
-  xserver-xorg: Fix :cve_nist:`2025-62229`, :cve_nist:`2025-62230` and :cve_nist:`2025-62231`
-  xwayland: Fix :cve_nist:`2025-62229`, :cve_nist:`2025-62230` and :cve_nist:`2025-62231`


Fixes in Yocto-4.0.32
~~~~~~~~~~~~~~~~~~~~~

-  babeltrace2: fetch with https protocol
-  bind: upgrade to 9.18.41
-  build-appliance-image: Update to kirkstone head revision
-  dev-manual/layers.rst: document "bitbake-layers show-machines"
-  dev-manual/new-recipe.rst: replace 'bitbake -e' with 'bitbake-getvar'
-  dev-manual/new-recipe.rst: typo, "whith" -> "which"
-  dev-manual/new-recipe.rst: update "recipetool -h" output
-  dev-manual: debugging: use bitbake-getvar in Viewing Variable Values section
-  documentation: link to the Releases page on yoctoproject.org instead of wiki
-  efibootmgr: update :term:`SRC_URI` branch
-  flac: patch seeking bug
-  goarch.bbclass: do not leak :term:`TUNE_FEATURES` into crosssdk task signatures
-  kernel-dev: add disable config example
-  kernel-dev: common: migrate bitbake -e to bitbake-getvar
-  libmicrohttpd: disable experimental code by default
-  migration-guides: add release notes for 4.0.31
-  oe-build-perf-report: relax metadata matching rules
-  overview-manual: migrate to SVG + fix typo
-  poky.conf: bump version for 4.0.32
-  python3-urllib3: upgrade to 1.26.20
-  recipes: Don't use ftp.gnome.org
-  ref-manual: variables: migrate the :term:`OVERRIDES` note to bitbake-getvar
-  systemd-bootchart: update :term:`SRC_URI` branch
-  xf86-video-intel: correct :term:`SRC_URI` as freedesktop anongit is down


Known Issues in Yocto-4.0.32
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.32
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Alexander Kanavin
-  Archana Polampalli
-  Divya Chellam
-  Gyorgy Sarvari
-  Hitendra Prajapati
-  Hongxu Jia
-  Jason Schonberg
-  Lee Chee Yang
-  Peter Marko
-  Praveen Kumar
-  Quentin Schulz
-  Richard Purdie
-  Robert P. J. Day
-  Ross Burton
-  Saquib Iltaf
-  Soumya Sambu
-  Steve Sakoman
-  Vijay Anusuri
-  Walter Werner SCHNEIDER


Repositories / Downloads for Yocto-4.0.32
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.32 </yocto-docs/log/?h=yocto-4.0.32>`
-  Git Revision: :yocto_git:`4b9df539fa06fb19ed8b51ef2d46e5c56779de81 </yocto-docs/commit/?id=4b9df539fa06fb19ed8b51ef2d46e5c56779de81>`
-  Release Artefact: yocto-docs-4b9df539fa06fb19ed8b51ef2d46e5c56779de81
-  sha: 70ee2caf576683c5f31ac5a592cde1c0650ece25cfcd5ff3cc7eedf531575611
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.32/yocto-docs-4b9df539fa06fb19ed8b51ef2d46e5c56779de81.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.32/yocto-docs-4b9df539fa06fb19ed8b51ef2d46e5c56779de81.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.32 </poky/log/?h=yocto-4.0.32>`
-  Git Revision: :yocto_git:`2c05660b21c7cc1082aeac8b75d8a2d82e249f63 </poky/commit/?id=2c05660b21c7cc1082aeac8b75d8a2d82e249f63>`
-  Release Artefact: poky-2c05660b21c7cc1082aeac8b75d8a2d82e249f63
-  sha: d7a55a18a597a7b140a81586b7ca6379c208ebbb3285de36c48fde10882947d8
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.32/poky-2c05660b21c7cc1082aeac8b75d8a2d82e249f63.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.32/poky-2c05660b21c7cc1082aeac8b75d8a2d82e249f63.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.32 </openembedded-core/log/?h=yocto-4.0.32>`
-  Git Revision: :oe_git:`2ed3f8b938579dbbb804e04c45a968cc57761db7 </openembedded-core/commit/?id=2ed3f8b938579dbbb804e04c45a968cc57761db7>`
-  Release Artefact: oecore-2ed3f8b938579dbbb804e04c45a968cc57761db7
-  sha: 11b9632586dfbf3f0ef69eca2014a8002f25ca8d53cfe9424e27361ba3a20831
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.32/oecore-2ed3f8b938579dbbb804e04c45a968cc57761db7.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.32/oecore-2ed3f8b938579dbbb804e04c45a968cc57761db7.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`kirkstone </meta-yocto/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.32 </meta-yocto/log/?h=yocto-4.0.32>`
-  Git Revision: :yocto_git:`77b40877c179ea3ce5c37c7ba1831e9c0e289266 </meta-yocto/commit/?id=77b40877c179ea3ce5c37c7ba1831e9c0e289266>`
-  Release Artefact: meta-yocto-77b40877c179ea3ce5c37c7ba1831e9c0e289266
-  sha: e908d42690881cd6e07b9ca18a21eb8761a0ec72d940b12905622e75ba913974
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.32/meta-yocto-77b40877c179ea3ce5c37c7ba1831e9c0e289266.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.32/meta-yocto-77b40877c179ea3ce5c37c7ba1831e9c0e289266.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.32 </meta-mingw/log/?h=yocto-4.0.32>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.32/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.32/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.32 </meta-gplv2/log/?h=yocto-4.0.32>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.32/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.32/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.32 </bitbake/log/?h=yocto-4.0.32>`
-  Git Revision: :oe_git:`8e2d1f8de055549b2101614d85454fcd1d0f94b2 </bitbake/commit/?id=8e2d1f8de055549b2101614d85454fcd1d0f94b2>`
-  Release Artefact: bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2
-  sha: fad4e7699bae62082118e89785324b031b0af0743064caee87c91ba28549afb0
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.32/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.32/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

