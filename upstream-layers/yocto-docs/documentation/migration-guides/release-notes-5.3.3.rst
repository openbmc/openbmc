.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.3.3 (Whinlatter)
------------------------------------------

Security Fixes in Yocto-5.3.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  alsa-lib: patch :cve_nist:`2026-25068`
-  busybox: Fix :cve_nist:`2025-60876`
-  ffmpeg: Ignore :cve_nist:`2025-12343`
-  freetype: Fix :cve_nist:`2026-23865`
-  gdk-pixbuf: Fix :cve_nist:`2025-6199`
-  glib-2.0: Fix :cve_nist:`2026-1484`, :cve_nist:`2026-1485` and :cve_nist:`2026-1489`
-  gnutls: Fix :cve_nist:`2025-14831`
-  go: Fix :cve_nist:`2026-25679`, :cve_nist:`2026-27137`, :cve_nist:`2026-27138`,
   :cve_nist:`2026-27139` and :cve_nist:`2026-27142`
-  harfbuzz: Fix :cve_nist:`2026-22693`
-  inetutils: Fix :cve_nist:`2026-28372`
-  libpam: Ignore :cve_nist:`2024-10041`
-  libpng: Fix :cve_nist:`2026-25646`
-  linux-yocto: Ignore :cve_nist:`2022-38096`, :cve_nist:`2023-6535`, :cve_nist:`2023-39176`,
   :cve_nist:`2023-39179` and :cve_nist:`2023-39180`
-  python3-pip: Fix :cve_nist:`2026-1703`
-  python3-urllib3: Fix :cve_nist:`2025-66471`
-  zlib: Fix :cve_nist:`2026-27171`


Fixes in Yocto-5.3.3
~~~~~~~~~~~~~~~~~~~~

meta-yocto
^^^^^^^^^^
-  poky.conf: Bump version for 5.3.3 release

openembedded-core
^^^^^^^^^^^^^^^^^
-  README: Add whinlatter subject-prefix to git-send-email suggestion
-  avahi: Remove a reference to the rejected :cve_nist:`2021-36217`
-  b4-config: add send-prefixes for whinlatter
-  build-appliance-image: Update to whinlatter head revisions
-  classes/buildhistory: Do not sign buildhistory commits
-  create-pull-request: Keep commit hash to be pulled in cover email
-  glib-2.0: upgrade to 2.86.4
-  gnutls: fix postinst script for ${PN}-fips for multilibs
-  gnutls: use libtool to install test binaries
-  go: upgrade to 1.25.8
-  libpng: upgrade to 1.6.55
-  linux-yocto: apply cve-exclusions also to rt and tiny recipe variants
-  lsb.py: strip ' from os-release file
-  lz4: Remove a reference to the rejected :cve_nist:`2025-62813`
-  pseudo: Update to include a fix for systems with kernel <5.6
-  python3: skip flaky test_default_timeout test
-  scripts/install-buildtools: Update to 5.3.2
-  wic/engine: error on old host debugfs for standalone directory copy
-  wireless-regdb: upgrade to 2026.02.04

yocto-docs
^^^^^^^^^^
-  Makefile: pass -silent to latexmk
-  dev-manual: delete references to "tar" package format
-  overview-manual/concepts: list other possible class directories
-  overview-manual: escape wildcard in inline markup
-  qemu.rst: parsing of the code-block was not done
-  ref-manual/classes.rst: fix broken links to U-Boot documentation
-  tools/host_packages_scripts/tlmgr_docs_pdf.sh: install perl
-  what-i-wish-id-known.rst: replace figure by the new SVG


Known Issues in Yocto-5.3.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.3.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adarsh Jagadish Kamini
-  Ankur Tyagi
-  Antonin Godard
-  Benjamin Robin (Schneider Electric)
-  Daniel Dragomir
-  Fabio Berton
-  Hugo SIMELIERE
-  Jan Vermaete
-  Kristiyan Chakarov
-  Liu Yiding
-  Livin Sunny
-  Martin Jansa
-  Michael Opdenacker
-  Paul Barker
-  Peter Marko
-  Quentin Schulz
-  Richard Purdie
-  Robert P. J. Day
-  Ross Burton
-  Shaik Moin
-  Vijay Anusuri
-  Yoann Congal

Repositories / Downloads for Yocto-5.3.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`whinlatter </yocto-docs/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.3 </yocto-docs/log/?h=yocto-5.3.3>`
-  Git Revision: :yocto_git:`82bd1a71e6e6a707d0009725969976cf2256af36 </yocto-docs/commit/?id=82bd1a71e6e6a707d0009725969976cf2256af36>`
-  Release Artefact: yocto-docs-82bd1a71e6e6a707d0009725969976cf2256af36
-  sha: fcf7020e34198f3b89077f9649cec579f4d23228f7f0a3a28a21c6b5b3649857
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.3/yocto-docs-82bd1a71e6e6a707d0009725969976cf2256af36.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.3/yocto-docs-82bd1a71e6e6a707d0009725969976cf2256af36.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`whinlatter </openembedded-core/log/?h=whinlatter>`
-  Tag:  :oe_git:`yocto-5.3.3 </openembedded-core/log/?h=yocto-5.3.3>`
-  Git Revision: :oe_git:`ab57471acad7ce2a037480dc7b301104620f1ebf </openembedded-core/commit/?id=ab57471acad7ce2a037480dc7b301104620f1ebf>`
-  Release Artefact: oecore-ab57471acad7ce2a037480dc7b301104620f1ebf
-  sha: 63ed486722e186d517e333c204def0a7bf578ca78bdeb2ad982625c49e7ee833
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.3/oecore-ab57471acad7ce2a037480dc7b301104620f1ebf.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.3/oecore-ab57471acad7ce2a037480dc7b301104620f1ebf.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`whinlatter </meta-yocto/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.3 </meta-yocto/log/?h=yocto-5.3.3>`
-  Git Revision: :yocto_git:`9b98ec5e6cfb6c9dc74d3c2e6304eeb274f7c487 </meta-yocto/commit/?id=9b98ec5e6cfb6c9dc74d3c2e6304eeb274f7c487>`
-  Release Artefact: meta-yocto-9b98ec5e6cfb6c9dc74d3c2e6304eeb274f7c487
-  sha: fcb81c6835036c4157b16dfc9f6d8c5a7dd0ed7ac72f59be238e99b486b1f8fa
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.3/meta-yocto-9b98ec5e6cfb6c9dc74d3c2e6304eeb274f7c487.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.3/meta-yocto-9b98ec5e6cfb6c9dc74d3c2e6304eeb274f7c487.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`whinlatter </meta-mingw/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3.3 </meta-mingw/log/?h=yocto-5.3.3>`
-  Git Revision: :yocto_git:`00323de97e397d4f6734ef2191806616989f5e10 </meta-mingw/commit/?id=00323de97e397d4f6734ef2191806616989f5e10>`
-  Release Artefact: meta-mingw-00323de97e397d4f6734ef2191806616989f5e10
-  sha: c9a70539b12c0642596fde6a2766d4a6a8fec8b2a366453fb6473363127a1c77
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.3/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.3/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.16 </bitbake/log/?h=2.16>`
-  Tag:  :oe_git:`yocto-5.3.3 </bitbake/log/?h=yocto-5.3.3>`
-  Git Revision: :oe_git:`7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0 </bitbake/commit/?id=7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0>`
-  Release Artefact: bitbake-7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0
-  sha: 06dd68908a5ffafae46ff7616e93a80f8b73c016f541e9517cf63b05f52f15c7
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3.3/bitbake-7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-5.3.3/bitbake-7af7e04f3adc8ba144d9e2e6b37ed4ddb3b59df0.tar.bz2

