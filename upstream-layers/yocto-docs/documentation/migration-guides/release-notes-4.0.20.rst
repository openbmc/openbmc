.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.20 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  acpica: Fix :cve_nist:`2024-24856`
-  glib-2.0: Fix :cve_nist:`2024-34397`
-  gstreamer1.0-plugins-base: Fix :cve_nist:`2024-4453`
-  libxml2: Fix :cve_nist:`2024-34459`
-  openssh: fix :cve_nist:`2024-6387`
-  openssl: Fix :cve_mitre:`2024-4741` and :cve_nist:`2024-5535`
-  ruby: fix :cve_nist:`2024-27280`
-  wget: Fix for :cve_nist:`2024-38428`


Fixes in Yocto-4.0.20
~~~~~~~~~~~~~~~~~~~~~

-  bitbake: tests/fetch: Tweak test to match upstream repo url change Upstream changed their urls, update our test to match.
-  build-appliance-image: Update to kirkstone head revision
-  glibc-tests: Add missing bash ptest dependency
-  glibc-tests: correctly pull in the actual tests when installing -ptest package
-  glibc: stable 2.35 branch updates
-  gobject-introspection: Do not hardcode objdump name
-  linuxloader: add -armhf on arm only for :term:`TARGET_FPU` 'hard'
-  man-pages: add an alternative link name for crypt_r.3
-  man-pages: remove conflict pages
-  migration-guides: add release notes for 4.0.19
-  openssl: Upgrade 3.0.13 -> 3.0.14
-  poky.conf: bump version for 4.0.20


Known Issues in Yocto-4.0.20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Archana Polampalli
-  Changqing Li
-  Deepthi Hemraj
-  Jonas Gorski
-  Jose Quaresma
-  Khem Raj
-  Lee Chee Yang
-  Peter Marko
-  Poonam Jadhav
-  Siddharth Doshi
-  Steve Sakoman
-  Thomas Perrot
-  Vijay Anusuri
-  Yogita Urade


Repositories / Downloads for Yocto-4.0.20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.20 </poky/log/?h=yocto-4.0.20>`
-  Git Revision: :yocto_git:`6bd3969d32730538608e680653e032e66958fe84 </poky/commit/?id=6bd3969d32730538608e680653e032e66958fe84>`
-  Release Artefact: poky-6bd3969d32730538608e680653e032e66958fe84
-  sha: b7ef1bd5ba1af257c4eb07a59b51d69e147723aea010eb2da99ea30dcbbbe2d9
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.20/poky-6bd3969d32730538608e680653e032e66958fe84.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.20/poky-6bd3969d32730538608e680653e032e66958fe84.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.20 </openembedded-core/log/?h=yocto-4.0.20>`
-  Git Revision: :oe_git:`5d97b0576e98a2cf402abab1a1edcab223545d87 </openembedded-core/commit/?id=5d97b0576e98a2cf402abab1a1edcab223545d87>`
-  Release Artefact: oecore-5d97b0576e98a2cf402abab1a1edcab223545d87
-  sha: 4064a32b8ff1ad8a98aa15e75b27585d2b27236c8cdfa4a28af6d6fef99b93c0
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.20/oecore-5d97b0576e98a2cf402abab1a1edcab223545d87.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.20/oecore-5d97b0576e98a2cf402abab1a1edcab223545d87.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.20 </meta-mingw/log/?h=yocto-4.0.20>`
-  Git Revision: :yocto_git:`f6b38ce3c90e1600d41c2ebb41e152936a0357d7 </meta-mingw/commit/?id=f6b38ce3c90e1600d41c2ebb41e152936a0357d7>`
-  Release Artefact: meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7
-  sha: 7d57167c19077f4ab95623d55a24c2267a3a3fb5ed83688659b4c03586373b25
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.20/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.20/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.20 </meta-gplv2/log/?h=yocto-4.0.20>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.20/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.20/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.20 </bitbake/log/?h=yocto-4.0.20>`
-  Git Revision: :oe_git:`734b0ea3dfe45eb16ee60f0c2c388e22af4040e0 </bitbake/commit/?id=734b0ea3dfe45eb16ee60f0c2c388e22af4040e0>`
-  Release Artefact: bitbake-734b0ea3dfe45eb16ee60f0c2c388e22af4040e0
-  sha: 99f4c6786fec790fd6c4577b5dea3c97c580cc4815bd409ce554a68ee99b0180
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.20/bitbake-734b0ea3dfe45eb16ee60f0c2c388e22af4040e0.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.20/bitbake-734b0ea3dfe45eb16ee60f0c2c388e22af4040e0.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.20 </yocto-docs/log/?h=yocto-4.0.20>`
-  Git Revision: :yocto_git:`b15b1d369edf33cd91232fefa0278e7e89653a01 </yocto-docs/commit/?id=b15b1d369edf33cd91232fefa0278e7e89653a01>`

