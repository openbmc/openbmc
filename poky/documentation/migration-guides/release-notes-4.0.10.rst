.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.10 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.10
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2023-1579`, :cve_nist:`2023-1972`, :cve_mitre:`2023-25584`, :cve_mitre:`2023-25585` and :cve_mitre:`2023-25588`
-  cargo : Ignore :cve_nist:`2022-46176`
-  connman: Fix :cve_nist:`2023-28488`
-  curl: Fix :cve_nist:`2023-27533`, :cve_nist:`2023-27534`, :cve_nist:`2023-27535`, :cve_nist:`2023-27536` and :cve_nist:`2023-27538`
-  ffmpeg: Fix :cve_nist:`2022-48434`
-  freetype: Fix :cve_nist:`2023-2004`
-  ghostscript: Fix :cve_mitre:`2023-29979`
-  git: Fix :cve_nist:`2023-25652` and :cve_nist:`2023-29007`
-  go: Fix :cve_nist:`2022-41722`, :cve_nist:`2022-41724`, :cve_nist:`2022-41725`, :cve_nist:`2023-24534`, :cve_nist:`2023-24537` and :cve_nist:`2023-24538`
-  go: Ignore :cve_nist:`2022-41716`
-  libxml2: Fix :cve_nist:`2023-28484` and :cve_nist:`2023-29469`
-  libxpm: Fix :cve_nist:`2022-44617`, :cve_nist:`2022-46285` and :cve_nist:`2022-4883`
-  linux-yocto: Ignore :cve_nist:`2021-3759`, :cve_nist:`2021-4135`, :cve_nist:`2021-4155`, :cve_nist:`2022-0168`, :cve_nist:`2022-0171`, :cve_nist:`2022-1016`, :cve_nist:`2022-1184`, :cve_nist:`2022-1198`, :cve_nist:`2022-1199`, :cve_nist:`2022-1462`, :cve_nist:`2022-1734`, :cve_nist:`2022-1852`, :cve_nist:`2022-1882`, :cve_nist:`2022-1998`, :cve_nist:`2022-2078`, :cve_nist:`2022-2196`, :cve_nist:`2022-2318`, :cve_nist:`2022-2380`, :cve_nist:`2022-2503`, :cve_nist:`2022-26365`, :cve_nist:`2022-2663`, :cve_nist:`2022-2873`, :cve_nist:`2022-2905`, :cve_nist:`2022-2959`, :cve_nist:`2022-3028`, :cve_nist:`2022-3078`, :cve_nist:`2022-3104`, :cve_nist:`2022-3105`, :cve_nist:`2022-3106`, :cve_nist:`2022-3107`, :cve_nist:`2022-3111`, :cve_nist:`2022-3112`, :cve_nist:`2022-3113`, :cve_nist:`2022-3115`, :cve_nist:`2022-3202`, :cve_nist:`2022-32250`, :cve_nist:`2022-32296`, :cve_nist:`2022-32981`, :cve_nist:`2022-3303`, :cve_nist:`2022-33740`, :cve_nist:`2022-33741`, :cve_nist:`2022-33742`, :cve_nist:`2022-33743`, :cve_nist:`2022-33744`, :cve_nist:`2022-33981`, :cve_nist:`2022-3424`, :cve_nist:`2022-3435`, :cve_nist:`2022-34918`, :cve_nist:`2022-3521`, :cve_nist:`2022-3545`, :cve_nist:`2022-3564`, :cve_nist:`2022-3586`, :cve_nist:`2022-3594`, :cve_nist:`2022-36123`, :cve_nist:`2022-3621`, :cve_nist:`2022-3623`, :cve_nist:`2022-3629`, :cve_nist:`2022-3633`, :cve_nist:`2022-3635`, :cve_nist:`2022-3646`, :cve_nist:`2022-3649`, :cve_nist:`2022-36879`, :cve_nist:`2022-36946`, :cve_nist:`2022-3707`, :cve_nist:`2022-39188`, :cve_nist:`2022-39190`, :cve_nist:`2022-39842`, :cve_nist:`2022-40307`, :cve_nist:`2022-40768`, :cve_nist:`2022-4095`, :cve_nist:`2022-41218`, :cve_nist:`2022-4139`, :cve_nist:`2022-41849`, :cve_nist:`2022-41850`, :cve_nist:`2022-41858`, :cve_nist:`2022-42328`, :cve_nist:`2022-42329`, :cve_nist:`2022-42703`, :cve_nist:`2022-42721`, :cve_nist:`2022-42722`, :cve_nist:`2022-42895`, :cve_nist:`2022-4382`, :cve_nist:`2022-4662`, :cve_nist:`2022-47518`, :cve_nist:`2022-47519`, :cve_nist:`2022-47520`, :cve_nist:`2022-47929`, :cve_nist:`2023-0179`, :cve_nist:`2023-0394`, :cve_nist:`2023-0461`, :cve_nist:`2023-0590`, :cve_nist:`2023-1073`, :cve_nist:`2023-1074`, :cve_nist:`2023-1077`, :cve_nist:`2023-1078`, :cve_nist:`2023-1079`, :cve_nist:`2023-1095`, :cve_nist:`2023-1118`, :cve_nist:`2023-1249`, :cve_nist:`2023-1252`, :cve_nist:`2023-1281`, :cve_nist:`2023-1382`, :cve_nist:`2023-1513`, :cve_nist:`2023-1829`, :cve_nist:`2023-1838`, :cve_nist:`2023-1998`, :cve_nist:`2023-2006`, :cve_nist:`2023-2008`, :cve_nist:`2023-2162`, :cve_nist:`2023-2166`, :cve_nist:`2023-2177`, :cve_nist:`2023-22999`, :cve_nist:`2023-23002`, :cve_nist:`2023-23004`, :cve_nist:`2023-23454`, :cve_nist:`2023-23455`, :cve_nist:`2023-23559`, :cve_nist:`2023-25012`, :cve_nist:`2023-26545`, :cve_nist:`2023-28327` and :cve_nist:`2023-28328`
-  nasm: Fix :cve_nist:`2022-44370`
-  python3-cryptography: Fix :cve_nist:`2023-23931`
-  qemu: Ignore :cve_nist:`2023-0664`
-  ruby: Fix :cve_nist:`2023-28755` and :cve_nist:`2023-28756`
-  screen: Fix :cve_nist:`2023-24626`
-  shadow: Fix :cve_nist:`2023-29383`
-  tiff: Fix :cve_nist:`2022-4645`
-  webkitgtk: Fix :cve_nist:`2022-32888` and :cve_nist:`2022-32923`
-  xserver-xorg: Fix :cve_nist:`2023-1393`


Fixes in Yocto-4.0.10
~~~~~~~~~~~~~~~~~~~~~

-  bitbake: bin/utils: Ensure locale en_US.UTF-8 is available on the system
-  build-appliance-image: Update to kirkstone head revision
-  cmake: add CMAKE_SYSROOT to generated toolchain file
-  glibc: stable 2.35 branch updates.
-  kernel-devsrc: depend on python3-core instead of python3
-  kernel: improve initramfs bundle processing time
-  libarchive: Enable acls, xattr for native as well as target
-  libbsd: Add correct license for all packages
-  libpam: Fix the xtests/tst-pam_motd[1|3] failures
-  libxpm: upgrade to 3.5.15
-  linux-firmware: upgrade to 20230404
-  linux-yocto/5.15: upgrade to v5.15.108
-  migration-guides: add release-notes for 4.0.9
-  oeqa/utils/metadata.py: Fix running oe-selftest running with no distro set
-  openssl: Move microblaze to linux-latomic config
-  package.bbclass: correct check for /build in copydebugsources()
-  poky.conf: bump version for 4.0.10
-  populate_sdk_base: add zip options
-  populate_sdk_ext.bbclass: set :term:`METADATA_REVISION` with an :term:`DISTRO` override
-  run-postinsts: Set dependency for ldconfig to avoid boot issues
-  update-alternatives.bbclass: fix old override syntax
-  wic/bootimg-efi: if fixed-size is set then use that for mkdosfs
-  wpebackend-fdo: upgrade to 1.14.2
-  xorg-lib-common: Add variable to set tarball type
-  xserver-xorg: upgrade to 21.1.8


Known Issues in Yocto-4.0.10
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.10
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Archana Polampalli
-  Arturo Buzarra
-  Bruce Ashfield
-  Christoph Lauer
-  Deepthi Hemraj
-  Dmitry Baryshkov
-  Frank de Brabander
-  Hitendra Prajapati
-  Joe Slater
-  Kai Kang
-  Kyle Russell
-  Lee Chee Yang
-  Mark Hatle
-  Martin Jansa
-  Mingli Yu
-  Narpat Mali
-  Pascal Bach
-  Pawan Badganchi
-  Peter Bergin
-  Peter Marko
-  Piotr Łobacz
-  Randolph Sapp
-  Ranjitsinh Rathod
-  Ross Burton
-  Shubham Kulkarni
-  Siddharth Doshi
-  Steve Sakoman
-  Sundeep KOKKONDA
-  Thomas Roos
-  Virendra Thakur
-  Vivek Kumbhar
-  Wang Mingyu
-  Xiangyu Chen
-  Yash Shinde
-  Yoann Congal
-  Yogita Urade
-  Zhixiong Chi


Repositories / Downloads for Yocto-4.0.10
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.10 </poky/log/?h=yocto-4.0.10>`
-  Git Revision: :yocto_git:`f53ab3a2ff206a130cdc843839dd0ea5ec4ad02f </poky/commit/?id=f53ab3a2ff206a130cdc843839dd0ea5ec4ad02f>`
-  Release Artefact: poky-f53ab3a2ff206a130cdc843839dd0ea5ec4ad02f
-  sha: 8820aeac857ce6bbd1c7ef26cadbb86eca02be93deded253b4a5f07ddd69255d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.10/poky-f53ab3a2ff206a130cdc843839dd0ea5ec4ad02f.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.10/poky-f53ab3a2ff206a130cdc843839dd0ea5ec4ad02f.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.10 </openembedded-core/log/?h=yocto-4.0.10>`
-  Git Revision: :oe_git:`d2713785f9cd2d58731df877bc8b7bcc71b6c8e6 </openembedded-core/commit/?id=d2713785f9cd2d58731df877bc8b7bcc71b6c8e6>`
-  Release Artefact: oecore-d2713785f9cd2d58731df877bc8b7bcc71b6c8e6
-  sha: 78e084a1aceaaa6ec022702f29f80eaffade3159e9c42b6b8985c1b7ddd2fbab
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.10/oecore-d2713785f9cd2d58731df877bc8b7bcc71b6c8e6.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.10/oecore-d2713785f9cd2d58731df877bc8b7bcc71b6c8e6.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.10 </meta-mingw/log/?h=yocto-4.0.10>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.10/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.10/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.10 </meta-gplv2/log/?h=yocto-4.0.10>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.10/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.10/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.10 </bitbake/log/?h=yocto-4.0.10>`
-  Git Revision: :oe_git:`0c6f86b60cfba67c20733516957c0a654eb2b44c </bitbake/commit/?id=0c6f86b60cfba67c20733516957c0a654eb2b44c>`
-  Release Artefact: bitbake-0c6f86b60cfba67c20733516957c0a654eb2b44c
-  sha: 4caa94ee4d644017b0cc51b702e330191677f7d179018cbcec8b1793949ebc74
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.10/bitbake-0c6f86b60cfba67c20733516957c0a654eb2b44c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.10/bitbake-0c6f86b60cfba67c20733516957c0a654eb2b44c.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.10 </yocto-docs/log/?h=yocto-4.0.10>`
-  Git Revision: :yocto_git:`8388be749806bd0bf4fccf1005dae8f643aa4ef4 </yocto-docs/commit/?id=8388be749806bd0bf4fccf1005dae8f643aa4ef4>`

