Release notes for Yocto-4.0.34 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.34
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2026-24401`, :cve_nist:`2025-68276`, :cve_nist:`2025-68468` and
   :cve_nist:`2025-68471`
-  bind: Fix :cve_nist:`2025-13878`
-  expat: Fix :cve_nist:`2026-24515` and :cve_nist:`2026-25210`
-  ffmpeg: Ignore :cve_nist:`2025-25468` and :cve_nist:`2025-25469`
-  glib-2.0: Fix :cve_nist:`2026-0988`, :cve_nist:`2026-1484`, :cve_nist:`2026-1485` and
   :cve_nist:`2026-1489`
-  glibc: Fix :cve_nist:`2025-15281`, :cve_nist:`2026-0861` and :cve_nist:`2026-0915`
-  harfbuzz: Ignore :cve_nist:`2026-22693`
-  inetutils: Fix :cve_nist:`2026-24061`
-  libpng: Fix :cve_nist:`2026-22695`, :cve_nist:`2026-22801` and :cve_nist:`2026-25646`
-  libtasn1: Fix :cve_nist:`2025-13151`
-  libxml2: Fix :cve_nist:`2026-0990` and :cve_nist:`2026-0992`
-  linux-yocto/5.15: Fix :cve_nist:`2022-49465`, :cve_nist:`2023-54207`, :cve_nist:`2025-22058`,
   :cve_nist:`2025-40040`, :cve_nist:`2025-40149`, :cve_nist:`2025-40164`, :cve_nist:`2025-68211`,
   :cve_nist:`2025-68340`, :cve_nist:`2025-68365`, :cve_nist:`2025-68725`, :cve_nist:`2025-68817`,
   :cve_nist:`2025-71147`, :cve_nist:`2025-71154`, :cve_nist:`2025-71162`, :cve_nist:`2025-71163`,
   :cve_nist:`2026-22976`, :cve_nist:`2026-22977`, :cve_nist:`2026-22978`, :cve_nist:`2026-22980`,
   :cve_nist:`2026-22982`, :cve_nist:`2026-22984`, :cve_nist:`2026-22990`, :cve_nist:`2026-22991`,
   :cve_nist:`2026-22992`, :cve_nist:`2026-22997`, :cve_nist:`2026-22998`, :cve_nist:`2026-22999`,
   :cve_nist:`2026-23060`, :cve_nist:`2026-23061`, :cve_nist:`2026-23063`, :cve_nist:`2026-23064`,
   :cve_nist:`2026-23076`, :cve_nist:`2026-23078`, :cve_nist:`2026-23080`, :cve_nist:`2026-23083`,
   :cve_nist:`2026-23084`, :cve_nist:`2026-23085`, :cve_nist:`2026-23087`, :cve_nist:`2026-23089`,
   :cve_nist:`2026-23090`, :cve_nist:`2026-23091`, :cve_nist:`2026-23093`, :cve_nist:`2026-23095`,
   :cve_nist:`2026-23096`, :cve_nist:`2026-23097`, :cve_nist:`2026-23119`, :cve_nist:`2026-23120`,
   :cve_nist:`2026-23121`, :cve_nist:`2026-23124`, :cve_nist:`2026-23125`, :cve_nist:`2026-23133`,
   :cve_nist:`2026-23146`, :cve_nist:`2026-23150`, :cve_nist:`2026-23164`, :cve_nist:`2026-23167`
   and :cve_nist:`2026-23170`
-  openssl: Fix :cve_nist:`2025-15467`, :cve_nist:`2026-22795`, :cve_nist:`2026-22796`,
   :cve_nist:`2025-68160`, :cve_nist:`2025-69418`, :cve_nist:`2025-69419`, :cve_nist:`2025-69420`
   and :cve_nist:`2025-69421`
-  python3: Fix :cve_nist:`2025-12084` and :cve_nist:`2025-13837`
-  vim: Ignore :cve_nist:`2025-66476`
-  zlib: Ignore :cve_nist:`2026-22184`


Fixes in Yocto-4.0.34
~~~~~~~~~~~~~~~~~~~~~

-  bind: Upgrade to 9.18.44
-  build-appliance-image: Update to kirkstone head revision
-  classes/buildhistory: Do not sign buildhistory commits
-  dev-manual/packages.rst: fix example recipe version
-  dev-manual/packages.rst: pr server: fix and explain why r0.X increments on :term:`SRCREV` change
-  dev-manual/packages.rst: rename r0.0 to r0 when :term:`PR` server is not enabled
-  glibc: stable 2.35 branch updates
-  linux-yocto/5.15: update to v5.15.199
-  migration-guides: add release notes for 4.0.32
-  openssl: upgrade to 3.0.19
-  poky.conf: Bump version for 4.0.34 release
-  poky.conf: add fedora-41, debian-12, rocky-8&9 to :term:`SANITY_TESTED_DISTROS`
-  pseudo: Update to 1.9.3+git43cbd8fb49
-  ref-manual/classes.rst: fix broken links to U-Boot documentation
-  ref-manual/system-requirements.rst: update untested distros
-  scripts/install-buildtools: Update to 4.0.32
-  u-boot: move CVE patch out of u-boot-common.inc
-  what-i-wish-id-known.rst: replace figure by the new SVG


Known Issues in Yocto-4.0.34
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.34
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Amaury Couderc
-  Ankur Tyagi
-  Antonin Godard
-  Bruce Ashfield
-  Fabio Berton
-  Hugo SIMELIERE
-  Lee Chee Yang
-  Michael Opdenacker
-  Paul Barker
-  Peter Marko
-  Richard Purdie
-  Scott Murray
-  Vijay Anusuri
-  Yoann Congal

Repositories / Downloads for Yocto-4.0.34
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.34 </yocto-docs/log/?h=yocto-4.0.34>`
-  Git Revision: :yocto_git:`7c348dd67cfd169b1a56bf969606b03dccb76c56 </yocto-docs/commit/?id=7c348dd67cfd169b1a56bf969606b03dccb76c56>`
-  Release Artefact: yocto-docs-7c348dd67cfd169b1a56bf969606b03dccb76c56
-  sha: 0677fc3aee3c936599f3bcffbe16792494058bd3506ca3ab1697ceac1822829b
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.34/yocto-docs-7c348dd67cfd169b1a56bf969606b03dccb76c56.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.34/yocto-docs-7c348dd67cfd169b1a56bf969606b03dccb76c56.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.34 </poky/log/?h=yocto-4.0.34>`
-  Git Revision: :yocto_git:`8334e82e1d85e50557bd3da64054fc9e3eafc495 </poky/commit/?id=8334e82e1d85e50557bd3da64054fc9e3eafc495>`
-  Release Artefact: poky-8334e82e1d85e50557bd3da64054fc9e3eafc495
-  sha: 74fcc57d1dd3bb0c6ef77bfaaeca7504f393e705a55149cf52d4b61981c9c387
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.34/poky-8334e82e1d85e50557bd3da64054fc9e3eafc495.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.34/poky-8334e82e1d85e50557bd3da64054fc9e3eafc495.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.34 </openembedded-core/log/?h=yocto-4.0.34>`
-  Git Revision: :oe_git:`7b6c9faa301a6d058ca34e230586f6a81ffa3ffb </openembedded-core/commit/?id=7b6c9faa301a6d058ca34e230586f6a81ffa3ffb>`
-  Release Artefact: oecore-7b6c9faa301a6d058ca34e230586f6a81ffa3ffb
-  sha: 375a22e3e229064749e78c80c44cde95adcedd26df76045fccefa3a9d3fa14ad
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.34/oecore-7b6c9faa301a6d058ca34e230586f6a81ffa3ffb.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.34/oecore-7b6c9faa301a6d058ca34e230586f6a81ffa3ffb.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`kirkstone </meta-yocto/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.34 </meta-yocto/log/?h=yocto-4.0.34>`
-  Git Revision: :yocto_git:`1d3874a383023a5e2433e0fcfd87ac5d1e6d341d </meta-yocto/commit/?id=1d3874a383023a5e2433e0fcfd87ac5d1e6d341d>`
-  Release Artefact: meta-yocto-1d3874a383023a5e2433e0fcfd87ac5d1e6d341d
-  sha: baf48bbe1f29686d502c0c6f311c7723b0a18f08e7efbf89c150589102285dbe
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.34/meta-yocto-1d3874a383023a5e2433e0fcfd87ac5d1e6d341d.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.34/meta-yocto-1d3874a383023a5e2433e0fcfd87ac5d1e6d341d.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.34 </meta-mingw/log/?h=yocto-4.0.34>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.34/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.34/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.34 </meta-gplv2/log/?h=yocto-4.0.34>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.34/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.34/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.34 </bitbake/log/?h=yocto-4.0.34>`
-  Git Revision: :oe_git:`8e2d1f8de055549b2101614d85454fcd1d0f94b2 </bitbake/commit/?id=8e2d1f8de055549b2101614d85454fcd1d0f94b2>`
-  Release Artefact: bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2
-  sha: fad4e7699bae62082118e89785324b031b0af0743064caee87c91ba28549afb0
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.34/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-4.0.34/bitbake-8e2d1f8de055549b2101614d85454fcd1d0f94b2.tar.bz2

