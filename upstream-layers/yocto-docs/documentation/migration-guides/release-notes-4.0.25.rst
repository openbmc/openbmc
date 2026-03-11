Release notes for Yocto-4.0.25 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.25
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  avahi: Fix :cve_nist:`2024-52616`
-  binutils: Fix :cve_nist:`2024-53589`
-  gdb: Fix :cve_nist:`2024-53589`
-  go: Fix :cve_nist:`2024-34155`, :cve_nist:`2024-34156`, :cve_nist:`2024-34158` and
   :cve_nist:`2024-45336`
-  gstreamer1.0: Ignore :cve_nist:`2024-47537`, :cve_nist:`2024-47539`, :cve_nist:`2024-47540`,
   :cve_nist:`2024-47543`, :cve_nist:`2024-47544`, :cve_nist:`2024-47545`, :cve_nist:`2024-47538`,
   :cve_nist:`2024-47541`, :cve_nist:`2024-47542`, :cve_nist:`2024-47600`, :cve_nist:`2024-47607`,
   :cve_nist:`2024-47615`, :cve_nist:`2024-47835`, :cve_nist:`2024-47546`, :cve_nist:`2024-47596`,
   :cve_nist:`2024-47597`, :cve_nist:`2024-47598`, :cve_nist:`2024-47599`, :cve_nist:`2024-47601`,
   :cve_nist:`2024-47777`, :cve_nist:`2024-47778`, :cve_nist:`2024-47834`, :cve_nist:`2024-47602`,
   :cve_nist:`2024-47603`, :cve_nist:`2024-47613`, :cve_nist:`2024-47774`, :cve_nist:`2024-47775`
   and :cve_nist:`2024-47776`
-  linux-yocto/5.15: Fix :cve_nist:`2024-36476`, :cve_nist:`2024-55916`, :cve_nist:`2024-56369`,
   :cve_nist:`2024-56626`, :cve_nist:`2024-56627`, :cve_nist:`2024-56715`, :cve_nist:`2024-56716`,
   :cve_nist:`2024-57802`, :cve_nist:`2024-57807`, :cve_nist:`2024-57841`, :cve_nist:`2024-57890`,
   :cve_nist:`2024-57896`, :cve_nist:`2024-57900`, :cve_nist:`2024-57910`, :cve_nist:`2024-57911`,
   :cve_nist:`2024-57938`, :cve_nist:`2024-57951`, :cve_nist:`2025-21631`, :cve_nist:`2025-21665`,
   :cve_nist:`2025-21666`, :cve_nist:`2025-21669`, :cve_nist:`2025-21680`, :cve_nist:`2025-21683`,
   :cve_nist:`2025-21694`, :cve_nist:`2025-21697` and :cve_nist:`2025-21699`
-  ofono: Fix :cve_nist:`2024-7539`, :cve_nist:`2024-7540`, :cve_nist:`2024-7541`,
   :cve_nist:`2024-7542`, :cve_nist:`2024-7543`, :cve_nist:`2024-7544`, :cve_nist:`2024-7545`,
   :cve_nist:`2024-7546` and :cve_nist:`2024-7547`
-  openssl: Fix :cve_nist:`2024-13176`
-  rsync: Fix :cve_nist:`2024-12084`, :cve_nist:`2024-12085`, :cve_nist:`2024-12086`,
   :cve_nist:`2024-12087`, :cve_nist:`2024-12088` and :cve_nist:`2024-12747`
-  ruby: Fix :cve_nist:`2024-49761`
-  socat: Fix :cve_nist:`2024-54661`
-  vte: Fix :cve_nist:`2024-37535`
-  wget: Fix :cve_nist:`2024-10524`


Fixes in Yocto-4.0.25
~~~~~~~~~~~~~~~~~~~~~

-  bitbake: tests/fetch: Fix git shallow test failure with git >= 2.48
-  build-appliance-image: Update to kirkstone head revision
-  classes-global/insane: Look up all runtime providers for file-rdeps
-  classes/nativesdk: also override :term:`TUNE_PKGARCH`
-  classes/qemu: use tune to select QEMU_EXTRAOPTIONS, not package architecture
-  cmake: apply parallel build settings to ptest tasks
-  dev-manual/building: document the initramfs-framework recipe
-  docs: Update autobuilder URLs to valkyrie
-  documentation: Fix typo in standards.md
-  glibc: Suppress GCC -Os warning on user2netname for sunrpc
-  glibc: stable 2.35 branch updates
-  lib/packagedata.py: Add API to iterate over rprovides
-  linux-yocto/5.15: upgrade to v5.15.178
-  migration-guides: add release notes for 4.0.24
-  openssl: upgrade to 3.0.16
-  poky.conf: bump version for 4.0.25
-  python3: Treat UID/GID overflow as failure
-  rsync: Delete pedantic errors re-ordering patch
-  rsync: upgrade to 3.2.7
-  rust-common.bbclass: soft assignment for RUSTLIB path
-  scripts/install-buildtools: Update to 4.0.23
-  test-manual/reproducible-builds: fix reproducible links


Known Issues in Yocto-4.0.25
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.25
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aleksandar Nikolic
-  Alexander Kanavin
-  Antonin Godard
-  Archana Polampalli
-  Bruce Ashfield
-  Deepesh Varatharajan
-  Divya Chellam
-  Joshua Watt
-  Khem Raj
-  Lee Chee Yang
-  Nikhil R
-  Pedro Ferreira
-  Peter Marko
-  Praveen Kumar
-  Richard Purdie
-  Ross Burton
-  Simon A. Eugster
-  Steve Sakoman
-  Yash Shinde
-  Yogita Urade
-  Zhang Peng


Repositories / Downloads for Yocto-4.0.25
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.25 </poky/log/?h=yocto-4.0.25>`
-  Git Revision: :yocto_git:`b5aa03f336c121269551f9e7baed4c677c76bb39 </poky/commit/?id=b5aa03f336c121269551f9e7baed4c677c76bb39>`
-  Release Artefact: poky-b5aa03f336c121269551f9e7baed4c677c76bb39
-  sha: 7afbcb25f0dd89a4fb6dd4c5945061705ef9ce79a6863806278603273c2b3b4a
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.25/poky-b5aa03f336c121269551f9e7baed4c677c76bb39.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.25/poky-b5aa03f336c121269551f9e7baed4c677c76bb39.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.25 </openembedded-core/log/?h=yocto-4.0.25>`
-  Git Revision: :oe_git:`5a794fd244f7fdeb426bd5e3def6b4effc0e8c62 </openembedded-core/commit/?id=5a794fd244f7fdeb426bd5e3def6b4effc0e8c62>`
-  Release Artefact: oecore-5a794fd244f7fdeb426bd5e3def6b4effc0e8c62
-  sha: 8fc93109693e5f4702b3fe0633b6be833605291b3d595dc8bdeb6379f40cd2de
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.25/oecore-5a794fd244f7fdeb426bd5e3def6b4effc0e8c62.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.25/oecore-5a794fd244f7fdeb426bd5e3def6b4effc0e8c62.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.25 </meta-mingw/log/?h=yocto-4.0.25>`
-  Git Revision: :yocto_git:`87c22abb1f11be430caf4372e6b833dc7d77564e </meta-mingw/commit/?id=87c22abb1f11be430caf4372e6b833dc7d77564e>`
-  Release Artefact: meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e
-  sha: f0bc4873e2e0319fb9d6d6ab9b98eb3f89664d4339a167d2db6a787dd12bc1a8
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.25/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.25/meta-mingw-87c22abb1f11be430caf4372e6b833dc7d77564e.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.25 </meta-gplv2/log/?h=yocto-4.0.25>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.25/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.25/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.25 </bitbake/log/?h=yocto-4.0.25>`
-  Git Revision: :oe_git:`e71f1ce53cf3b8320caa481ae62d1ce2900c4670 </bitbake/commit/?id=e71f1ce53cf3b8320caa481ae62d1ce2900c4670>`
-  Release Artefact: bitbake-e71f1ce53cf3b8320caa481ae62d1ce2900c4670
-  sha: 007eef35174586c85b233f4ec91578956fe21e0236f7ca2c3f90f9d034f94b5b
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-4.0.25/bitbake-e71f1ce53cf3b8320caa481ae62d1ce2900c4670.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-4.0.25/bitbake-e71f1ce53cf3b8320caa481ae62d1ce2900c4670.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.25 </yocto-docs/log/?h=yocto-4.0.25>`
-  Git Revision: :yocto_git:`c6dce0c77481dee7b0a0fcdc803f755ceccef234 </yocto-docs/commit/?id=c6dce0c77481dee7b0a0fcdc803f755ceccef234>`

