.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.8 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.8
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-0840`
-  curl: Ignore :cve_nist:`2025-0725`
-  elfutils: Fix :cve_nist:`2025-1352`, :cve_nist:`2025-1365` and :cve_nist:`2025-1372`
-  ffmpeg: Fix :cve_nist:`2024-35365`, :cve_nist:`2024-35369`, :cve_nist:`2024-36613`,
   :cve_nist:`2024-36616`, :cve_nist:`2024-36617`, :cve_nist:`2024-36618`, :cve_nist:`2024-36619`,
   :cve_nist:`2025-0518`, :cve_nist:`2025-22919`, :cve_nist:`2025-22921` and :cve_nist:`2025-25473`
-  glibc: Fix :cve_nist:`2025-0395`
-  gnutls: Fix :cve_nist:`2024-12243`
-  go: Fix :cve_nist:`2024-45336`, :cve_nist:`2024-45341` and :cve_nist:`2025-22866`
-  gstreamer1.0-rtsp-server: Fix :cve_nist:`2024-44331`
-  libcap: Fix :cve_nist:`2025-1390`
-  libtasn1: Fix :cve_nist:`2024-12133`
-  libxml2: Fix :cve_nist:`2024-56171` and :cve_nist:`2025-24928`
-  linux-yocto/6.6: Fix :cve_nist:`2024-36476`, :cve_nist:`2024-53179`, :cve_nist:`2024-56582`,
   :cve_nist:`2024-56703`, :cve_nist:`2024-57801`, :cve_nist:`2024-57802`, :cve_nist:`2024-57841`,
   :cve_nist:`2024-57882`, :cve_nist:`2024-57887`, :cve_nist:`2024-57890`, :cve_nist:`2024-57892`,
   :cve_nist:`2024-57895`, :cve_nist:`2024-57896`, :cve_nist:`2024-57900`, :cve_nist:`2024-57901`,
   :cve_nist:`2024-57902`, :cve_nist:`2024-57906`, :cve_nist:`2024-57907`, :cve_nist:`2024-57908`,
   :cve_nist:`2024-57910`, :cve_nist:`2024-57911`, :cve_nist:`2024-57912`, :cve_nist:`2024-57913`,
   :cve_nist:`2024-57916`, :cve_nist:`2024-57922`, :cve_nist:`2024-57925`, :cve_nist:`2024-57926`,
   :cve_nist:`2024-57933`, :cve_nist:`2024-57938`, :cve_nist:`2024-57939`, :cve_nist:`2024-57940`,
   :cve_nist:`2024-57949`, :cve_nist:`2024-57951`, :cve_nist:`2025-21631`, :cve_nist:`2025-21636`,
   :cve_nist:`2025-21637`, :cve_nist:`2025-21638`, :cve_nist:`2025-21639`, :cve_nist:`2025-21640`,
   :cve_nist:`2025-21642`, :cve_nist:`2025-21652`, :cve_nist:`2025-21658`, :cve_nist:`2025-21665`,
   :cve_nist:`2025-21666`, :cve_nist:`2025-21667`, :cve_nist:`2025-21669`, :cve_nist:`2025-21670`,
   :cve_nist:`2025-21671`, :cve_nist:`2025-21673`, :cve_nist:`2025-21674`, :cve_nist:`2025-21675`,
   :cve_nist:`2025-21676`, :cve_nist:`2025-21680`, :cve_nist:`2025-21681`, :cve_nist:`2025-21683`,
   :cve_nist:`2025-21684`, :cve_nist:`2025-21687`, :cve_nist:`2025-21689`, :cve_nist:`2025-21690`,
   :cve_nist:`2025-21692`, :cve_nist:`2025-21694`, :cve_nist:`2025-21697` and :cve_nist:`2025-21699`
-  openssh: Fix :cve_nist:`2025-26466`
-  openssl: Fix :cve_nist:`2024-9143`, :cve_nist:`2024-12797` and :cve_nist:`2024-13176`
-  pyhton3: Fix :cve_nist:`2024-12254` and :cve_nist:`2025-0938`
-  subversion: Ignore :cve_nist:`2024-45720`
-  u-boot: Fix :cve_nist:`2024-57254`, :cve_nist:`2024-57255`, :cve_nist:`2024-57256`,
   :cve_nist:`2024-57257`, :cve_nist:`2024-57258` and :cve_nist:`2024-57259`
-  vim: Fix :cve_nist:`2025-22134` and :cve_nist:`2025-24014`
-  xwayland: Fix :cve_nist:`2024-9632`, :cve_nist:`2025-26594`, :cve_nist:`2025-26595`,
   :cve_nist:`2025-26596`, :cve_nist:`2025-26597`, :cve_nist:`2025-26598`, :cve_nist:`2025-26599`,
   :cve_nist:`2025-26600` and :cve_nist:`2025-26601`


Fixes in Yocto-5.0.8
~~~~~~~~~~~~~~~~~~~~

-  base-files: Drop /bin/sh dependency
-  bind: upgrade to 9.18.33
-  binutils: File name too long causing failure to open temporary head file in dlltool
-  binutils: stable 2.42 branch update
-  bitbake: bblayers/query: Fix using "removeprefix" string method
-  bitbake: bitbake-diffsigs: fix handling when finding only a single sigfile
-  bitbake: data_smart.py: clear expand_cache in _setvar_update_overridevars
-  bitbake: data_smart.py: remove unnecessary ? from __expand_var_regexp__
-  bitbake: data_smart.py: simple clean up
-  build-appliance-image: Update to scarthgap head revision
-  ccache.conf: Add include_file_ctime to sloppiness
-  cmake: apply parallel build settings to ptest tasks
-  contributor-guide/submit-changes: add policy on AI generated code
-  dev-manual/building: document the initramfs-framework recipe
-  devtool: ide-sdk recommend :term:`DEBUG_BUILD`
-  devtool: ide-sdk remove the plugin from eSDK installer
-  devtool: ide-sdk sort cmake preset
-  devtool: modify support debug-builds
-  docs: Add favicon for the documentation html
-  docs: Fix typo in standards.md
-  docs: Remove all mention of core-image-lsb
-  docs: vulnerabilities/classes: remove references to cve-check text format
-  files: Amend overlayfs unit descriptions with path information
-  files: overlayfs-create-dirs: Improve mount unit dependency
-  glibc: stable 2.39 branch updates
-  gnupg: upgrade to 2.4.5
-  go: upgrade 1.22.12
-  icu: remove host references in nativesdk to fix reproducibility
-  libtasn1: upgrade to 4.20.0
-  libxml2: upgrade to 2.12.10
-  linux-yocto/6.6: upgrade to v6.6.75
-  meta: Enable '-o pipefail' for the SDK installer
-  migration-guides: add release notes for 4.0.24, 4.0.25 and 5.0.7
-  oe-selftest: devtool ide-sdk use modify debug-build
-  oeqa/sdk/context: fix for gtk3 test failure during do_testsdk
-  oeqa/selftest/rust: skip on all MIPS platforms
-  openssl: upgrade to 3.2.4
-  pkg-config-native: pick additional search paths from $EXTRA_NATIVE_PKGCONFIG_PATH
-  poky.conf: add ubuntu2404 to :term:`SANITY_TESTED_DISTROS`
-  poky.conf: bump version for 5.0.8
-  ppp: Revert lock path to /var/lock
-  python3-setuptools-scm: respect GIT_CEILING_DIRECTORIES
-  python3: upgrade to 3.12.9
-  qemu: Do not define sched_attr with glibc >= 2.41
-  ref-manual/faq: add q&a on systemd as default
-  ref-manual: Add missing variable :term:`IMAGE_ROOTFS_MAXSIZE`
-  ref-manual: don't refer to poky-lsb
-  ref-manual: remove OE_IMPORTS
-  rust-common.bbclass: soft assignment for RUSTLIB path
-  rust: fix for rust multilib sdk configuration
-  rust: remove redundant cargo config file
-  scripts/install-buildtools: Update to 5.0.7
-  sdk-manual: extensible.rst: devtool ide-sdk improve
-  sdk-manual: extensible.rst: update devtool ide-sdk
-  selftest/rust: correctly form the PATH environment variable
-  systemd: add libpcre2 as :term:`RRECOMMENDS` if pcre2 is enabled
-  systemd: upgrade to 255.17
-  test-manual/ptest: link to common framework ptest classes
-  tzcode-native: Fix compiler setting from 2023d version
-  tzdata/tzcode-native: upgrade to 2025a
-  u-boot: kernel-fitimage: Fix dependency loop if :term:`UBOOT_SIGN_ENABLE` and UBOOT_ENV enabled
-  u-boot: kernel-fitimage: Restore FIT_SIGN_INDIVIDUAL="1" behavior
-  uboot-config: fix devtool modify with kernel-fitimage
-  vim: upgrade to 9.1.1043


Known Issues in Yocto-5.0.8
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  N/A

Contributors to Yocto-5.0.8
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adrian Freihofer
-  Aleksandar Nikolic
-  Alessio Cascone
-  Alexander Kanavin
-  Alexis Cellier
-  Antonin Godard
-  Archana Polampalli
-  Bruce Ashfield
-  Chen Qi
-  Deepesh Varatharajan
-  Divya Chellam
-  Enrico Jörns
-  Esben Haabendal
-  Etienne Cordonnier
-  Fabio Berton
-  Guðni Már Gilbert
-  Harish Sadineni
-  Hitendra Prajapati
-  Hongxu Jia
-  Jiaying Song
-  Joerg Schmidt
-  Johannes Schneider
-  Khem Raj
-  Lee Chee Yang
-  Marek Vasut
-  Marta Rybczynska
-  Moritz Haase
-  Oleksandr Hnatiuk
-  Pedro Ferreira
-  Peter Marko
-  Poonam Jadhav
-  Priyal Doshi
-  Ross Burton
-  Simon A. Eugster
-  Steve Sakoman
-  Vijay Anusuri
-  Wang Mingyu
-  Weisser, Pascal


Repositories / Downloads for Yocto-5.0.8
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.8 </poky/log/?h=yocto-5.0.8>`
-  Git Revision: :yocto_git:`dc4827b3660bc1a03a2bc3b0672615b50e9137ff </poky/commit/?id=dc4827b3660bc1a03a2bc3b0672615b50e9137ff>`
-  Release Artefact: poky-dc4827b3660bc1a03a2bc3b0672615b50e9137ff
-  sha: ace7264e16e18ed02ef0ad2935fa10b5fad2c4de38b2356f4192b38ef2184504
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.8/poky-dc4827b3660bc1a03a2bc3b0672615b50e9137ff.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.8/poky-dc4827b3660bc1a03a2bc3b0672615b50e9137ff.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.8 </openembedded-core/log/?h=yocto-5.0.8>`
-  Git Revision: :oe_git:`cd2b6080a4c0f2ed2c9939ec0b87763aef595048 </openembedded-core/commit/?id=cd2b6080a4c0f2ed2c9939ec0b87763aef595048>`
-  Release Artefact: oecore-cd2b6080a4c0f2ed2c9939ec0b87763aef595048
-  sha: 14c7cd5c62a96ceb9c2141164ea0f087fdbaed99ca3e9a722977a3f12d6381f6
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.8/oecore-cd2b6080a4c0f2ed2c9939ec0b87763aef595048.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.8/oecore-cd2b6080a4c0f2ed2c9939ec0b87763aef595048.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.8 </meta-mingw/log/?h=yocto-5.0.8>`
-  Git Revision: :yocto_git:`bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f </meta-mingw/commit/?id=bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f>`
-  Release Artefact: meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f
-  sha: ab073def6487f237ac125d239b3739bf02415270959546b6b287778664f0ae65
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.8/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.8/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.8 </bitbake/log/?h=yocto-5.0.8>`
-  Git Revision: :oe_git:`7375d32e8c1af20c51abec4eb3b072b4ca58b239 </bitbake/commit/?id=7375d32e8c1af20c51abec4eb3b072b4ca58b239>`
-  Release Artefact: bitbake-7375d32e8c1af20c51abec4eb3b072b4ca58b239
-  sha: 13dffbc162c5b6e2c95fa72936a430b9a542d52d81d502a5d0afc592fbf4a16b
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.8/bitbake-7375d32e8c1af20c51abec4eb3b072b4ca58b239.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.8/bitbake-7375d32e8c1af20c51abec4eb3b072b4ca58b239.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.8 </yocto-docs/log/?h=yocto-5.0.8>`
-  Git Revision: :yocto_git:`7d3cce5b962ca9f73b29affceb7ebc6710627739 </yocto-docs/commit/?id=7d3cce5b962ca9f73b29affceb7ebc6710627739>`

