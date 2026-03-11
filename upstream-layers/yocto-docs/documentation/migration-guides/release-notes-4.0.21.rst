.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.21 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.21
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve_nist:`2024-4076`, :cve_nist:`2024-1737`, :cve_nist:`2024-0760` and :cve_nist:`2024-1975`
-  apr: Fix :cve_nist:`2023-49582`
-  busybox: Fix :cve_nist:`2023-42363`, :cve_nist:`2023-42364`, :cve_nist:`2023-42365`, :cve_nist:`2023-42366` and :cve_nist:`2021-42380`
-  curl: Ignore :cve_nist:`2024-32928`
-  curl: Fix :cve_nist:`2024-7264`
-  ghostscript: Fix :cve_nist:`2024-29506`, :cve_nist:`2024-29509` and :cve_nist:`2024-29511`
-  go: Fix :cve_nist:`2024-24789` and :cve_nist:`2024-24791`
-  gtk+3: Fix :cve_nist:`2024-6655`
-  libarchive: Ignore :cve_nist:`2024-37407`
-  libyaml: Ignore :cve_nist:`2024-35325`, :cve_nist:`2024-35326` and :cve_nist:`2024-35328`
-  linux-yocto/5.15: Fix :cve_nist:`2022-48772`, :cve_nist:`2024-35972`, :cve_nist:`2024-35984`, :cve_nist:`2024-35990`, :cve_nist:`2024-35997`, :cve_nist:`2024-36008`, :cve_nist:`2024-36270`, :cve_nist:`2024-36489`, :cve_nist:`2024-36897`, :cve_nist:`2024-36938`, :cve_nist:`2024-36965`, :cve_nist:`2024-36967`, :cve_nist:`2024-36969`, :cve_nist:`2024-36971`, :cve_nist:`2024-36978`, :cve_nist:`2024-38546`, :cve_nist:`2024-38547`, :cve_nist:`2024-38549`, :cve_nist:`2024-38552`, :cve_nist:`2024-38555`, :cve_nist:`2024-38571`, :cve_nist:`2024-38583`, :cve_nist:`2024-38591`, :cve_nist:`2024-38597`, :cve_nist:`2024-38598`, :cve_nist:`2024-38600`, :cve_nist:`2024-38627`, :cve_nist:`2024-38633`, :cve_nist:`2024-38661`, :cve_nist:`2024-38662`, :cve_nist:`2024-38780`, :cve_nist:`2024-39277`, :cve_nist:`2024-39292`, :cve_nist:`2024-39301`, :cve_nist:`2024-39466`, :cve_nist:`2024-39468`, :cve_nist:`2024-39471`, :cve_nist:`2024-39475`, :cve_nist:`2024-39476`, :cve_nist:`2024-39480`, :cve_nist:`2024-39482`, :cve_nist:`2024-39484`, :cve_nist:`2024-39487`, :cve_nist:`2024-39489`, :cve_nist:`2024-39493`, :cve_nist:`2024-39495`, :cve_nist:`2024-39506`, :cve_nist:`2024-40902`, :cve_nist:`2024-40911`, :cve_nist:`2024-40912`, :cve_nist:`2024-40932`, :cve_nist:`2024-40934`, :cve_nist:`2024-40954`, :cve_nist:`2024-40956`, :cve_nist:`2024-40957`, :cve_nist:`2024-40958`, :cve_nist:`2024-40959`, :cve_nist:`2024-40960`, :cve_nist:`2024-40961`, :cve_nist:`2024-40967`, :cve_nist:`2024-40970`, :cve_nist:`2024-40980`, :cve_nist:`2024-40981`, :cve_nist:`2024-40994`, :cve_nist:`2024-40995`, :cve_nist:`2024-41000`, :cve_nist:`2024-41002`, :cve_nist:`2024-41006`, :cve_nist:`2024-41007`, :cve_nist:`2024-41046`, :cve_nist:`2024-41049`, :cve_nist:`2024-41055`, :cve_nist:`2024-41064`, :cve_nist:`2024-41070`, :cve_nist:`2024-41073`, :cve_nist:`2024-41087`, :cve_nist:`2024-41089`, :cve_nist:`2024-41092`, :cve_nist:`2024-41093`, :cve_nist:`2024-41095`, :cve_nist:`2024-41097`, :cve_nist:`2024-42068`, :cve_nist:`2024-42070`, :cve_nist:`2024-42076`, :cve_nist:`2024-42077`, :cve_nist:`2024-42080`, :cve_nist:`2024-42082`, :cve_nist:`2024-42085`, :cve_nist:`2024-42090`, :cve_nist:`2024-42093`, :cve_nist:`2024-42094`, :cve_nist:`2024-42101`, :cve_nist:`2024-42102`, :cve_nist:`2024-42104`, :cve_nist:`2024-42109`, :cve_nist:`2024-42140`, :cve_nist:`2024-42148`, :cve_nist:`2024-42152`, :cve_nist:`2024-42153`, :cve_nist:`2024-42154`, :cve_nist:`2024-42157`, :cve_nist:`2024-42161`, :cve_nist:`2024-42223`, :cve_nist:`2024-42224`, :cve_nist:`2024-42225`, :cve_nist:`2024-42229`, :cve_nist:`2024-42232`, :cve_nist:`2024-42236`, :cve_nist:`2024-42244` and :cve_nist:`2024-42247`
-  llvm: Fix :cve_nist:`2023-46049` and :cve_nist:`2024-31852`
-  ofono: fix :cve_nist:`2023-2794`
-  orc: Fix :cve_nist:`2024-40897`
-  python3-certifi: Fix :cve_nist:`2024-39689`
-  python3-jinja2: Fix :cve_nist:`2024-34064`
-  python3: Fix :cve_nist:`2024-8088`
-  qemu: Fix :cve_nist:`2024-7409`
-  ruby: Fix for :cve_nist:`2024-27282`
-  tiff: Fix :cve_nist:`2024-7006`
-  vim: Fix :cve_nist:`2024-22667`, :cve_nist:`2024-41957`, :cve_nist:`2024-41965` and :cve_nist:`2024-43374`
-  wpa-supplicant: Fix :cve_nist:`2023-52160`


Fixes in Yocto-4.0.21
~~~~~~~~~~~~~~~~~~~~~

-  apr: upgrade to 1.7.5
-  bind: Upgrade to 9.18.28
-  bitbake: data_smart: Improve performance for VariableHistory
-  build-appliance-image: Update to kirkstone head revision
-  cryptodev-module: Fix build for linux 5.10.220
-  gcc-runtime: remove bashism
-  grub: fs/fat: Don't error when mtime is 0
-  image_types.bbclass: Use --force also with lz4,lzop
-  libsoup: fix compile error on centos7
-  linux-yocto/5.15: upgrade to v5.15.164
-  lttng-modules: Upgrade to 2.13.14
-  migration-guide: add release notes for 4.0.20
-  orc: upgrade to 0.4.39
-  poky.conf: bump version for 4.0.21
-  python3-jinja2: upgrade to 3.1.4
-  python3-pycryptodome(x): use python_setuptools_build_meta build class
-  python3: add PACKAGECONFIG[editline]
-  ref-manual: fix typo and move :term:`SYSROOT_DIRS` example
-  sqlite3: CVE_ID correction for :cve_nist:`2023-7104` as patched
-  sqlite3: Rename patch for :cve_nist:`2022-35737`
-  uboot-sign: Fix index error in concat_dtb_helper() with multiple configs
-  vim: upgrade to 9.1.0682
-  wireless-regdb: upgrade to 2024.07.04


Known Issues in Yocto-4.0.21
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.21
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Archana Polampalli
-  Ashish Sharma
-  Bruce Ashfield
-  Deepthi Hemraj
-  Divya Chellam
-  Florian Amstutz
-  Guocai He
-  Hitendra Prajapati
-  Hugo SIMELIERE
-  Lee Chee Yang
-  Leon Anavi
-  Matthias Pritschet
-  Ming Liu
-  Niko Mauno
-  Peter Marko
-  Robert Yang
-  Rohini Sangam
-  Ross Burton
-  Siddharth Doshi
-  Soumya Sambu
-  Steve Sakoman
-  Vijay Anusuri
-  Vrushti Dabhi
-  Wang Mingyu
-  Yogita Urade


Repositories / Downloads for Yocto-4.0.21
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.21 </poky/log/?h=yocto-4.0.21>`
-  Git Revision: :yocto_git:`4cdc553814640851cce85f84ee9c0b58646cd33b </poky/commit/?id=4cdc553814640851cce85f84ee9c0b58646cd33b>`
-  Release Artefact: poky-4cdc553814640851cce85f84ee9c0b58646cd33b
-  sha: 460e3a4ede491a9b66c5d262cd9498d5bcca1f2d880885342b08dc32b967f33d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.21/poky-4cdc553814640851cce85f84ee9c0b58646cd33b.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.21/poky-4cdc553814640851cce85f84ee9c0b58646cd33b.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.21 </openembedded-core/log/?h=yocto-4.0.21>`
-  Git Revision: :oe_git:`c40a3fec49942ac6d25ba33e57e801a550e252c9 </openembedded-core/commit/?id=c40a3fec49942ac6d25ba33e57e801a550e252c9>`
-  Release Artefact: oecore-c40a3fec49942ac6d25ba33e57e801a550e252c9
-  sha: afc2aaf312f9fb2590ae006615557ec605c98eff42bc380a1b2d6e39cfdf8930
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.21/oecore-c40a3fec49942ac6d25ba33e57e801a550e252c9.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.21/oecore-c40a3fec49942ac6d25ba33e57e801a550e252c9.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.21 </meta-mingw/log/?h=yocto-4.0.21>`
-  Git Revision: :yocto_git:`f6b38ce3c90e1600d41c2ebb41e152936a0357d7 </meta-mingw/commit/?id=f6b38ce3c90e1600d41c2ebb41e152936a0357d7>`
-  Release Artefact: meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7
-  sha: 7d57167c19077f4ab95623d55a24c2267a3a3fb5ed83688659b4c03586373b25
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.21/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.21/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.21 </meta-gplv2/log/?h=yocto-4.0.21>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.21/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.21/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.21 </bitbake/log/?h=yocto-4.0.21>`
-  Git Revision: :oe_git:`ec2a99a077da9aa0e99e8b05e0c65dcbd45864b1 </bitbake/commit/?id=ec2a99a077da9aa0e99e8b05e0c65dcbd45864b1>`
-  Release Artefact: bitbake-ec2a99a077da9aa0e99e8b05e0c65dcbd45864b1
-  sha: 1cb102f4c8dbd067f0262072e4e629ec7cb423103111ccdde75a09fcb8f55e5f
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.21/bitbake-ec2a99a077da9aa0e99e8b05e0c65dcbd45864b1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.21/bitbake-ec2a99a077da9aa0e99e8b05e0c65dcbd45864b1.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.21 </yocto-docs/log/?h=yocto-4.0.21>`
-  Git Revision: :yocto_git:`512025edd9b3b6b8d0938b35bb6188c9f3b7f17d </yocto-docs/commit/?id=512025edd9b3b6b8d0938b35bb6188c9f3b7f17d>`

