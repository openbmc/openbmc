.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.17 (Kirkstone)
------------------------------------------

Security Fixes in Yocto-4.0.17
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve:`2023-4408`, :cve:`2023-5517`, :cve:`2023-5679`, :cve:`2023-50868` and :cve:`2023-50387`
-  binutils: Fix :cve:`2023-39129` and :cve:`2023-39130`
-  curl: Fix :cve:`2023-46219`
-  curl: Ignore :cve:`2023-42915`
-  gcc: Ignore :cve:`2023-4039`
-  gdb: Fix :cve:`2023-39129` and :cve:`2023-39130`
-  glibc: Ignore :cve:`2023-0687`
-  go: Fix :cve:`2023-29406`, :cve:`2023-45285`, :cve:`2023-45287`, :cve:`2023-45289`, :cve:`2023-45290`, :cve:`2024-24784` and :cve:`2024-24785`
-  less: Fix :cve:`2022-48624`
-  libgit2: Fix :cve:`2024-24575` and :cve:`2024-24577`
-  libuv: fix :cve:`2024-24806`
-  libxml2: Fix for :cve:`2024-25062`
-  linux-yocto/5.15: Fix :cve:`2022-36402`, :cve:`2022-40982`, :cve:`2022-47940`, :cve:`2023-1193`, :cve:`2023-1194`, :cve:`2023-3772`, :cve_mitre:`2023-3867`, :cve:`2023-4128`, :cve:`2023-4206`, :cve:`2023-4207`, :cve:`2023-4208`, :cve:`2023-4244`, :cve:`2023-4273`, :cve:`2023-4563`, :cve:`2023-4569`, :cve:`2023-4623`, :cve:`2023-4881`, :cve:`2023-4921`, :cve:`2023-5158`, :cve:`2023-5717`, :cve:`2023-6040`, :cve:`2023-6121`, :cve:`2023-6176`, :cve:`2023-6546`, :cve:`2023-6606`, :cve:`2023-6622`, :cve:`2023-6817`, :cve:`2023-6915`, :cve:`2023-6931`, :cve:`2023-6932`, :cve:`2023-20569`, :cve:`2023-20588`, :cve:`2023-25775`, :cve:`2023-31085`, :cve:`2023-32247`, :cve:`2023-32250`, :cve:`2023-32252`, :cve:`2023-32254`, :cve:`2023-32257`, :cve:`2023-32258`, :cve:`2023-34324`, :cve:`2023-35827`, :cve:`2023-38427`, :cve:`2023-38430`, :cve:`2023-38431`, :cve:`2023-39189`, :cve:`2023-39192`, :cve:`2023-39193`, :cve:`2023-39194`, :cve:`2023-39198`, :cve:`2023-40283`, :cve:`2023-42752`, :cve:`2023-42753`, :cve:`2023-42754`, :cve:`2023-42755`, :cve:`2023-45871`, :cve:`2023-46343`, :cve:`2023-46813`, :cve:`2023-46838`, :cve:`2023-46862`, :cve:`2023-51042`, :cve:`2023-51779`, :cve_mitre:`2023-52340`, :cve:`2023-52429`, :cve:`2023-52435`, :cve:`2023-52436`, :cve:`2023-52438`, :cve:`2023-52439`, :cve:`2023-52441`, :cve:`2023-52442`, :cve:`2023-52443`, :cve:`2023-52444`, :cve:`2023-52445`, :cve:`2023-52448`, :cve:`2023-52449`, :cve:`2023-52451`, :cve:`2023-52454`, :cve:`2023-52456`, :cve:`2023-52457`, :cve:`2023-52458`, :cve:`2023-52463`, :cve:`2023-52464`, :cve:`2024-0340`, :cve:`2024-0584`, :cve:`2024-0607`, :cve:`2024-0641`, :cve:`2024-0646`, :cve:`2024-1085`, :cve:`2024-1086`, :cve:`2024-1151`, :cve:`2024-22705`, :cve:`2024-23849`, :cve:`2024-23850`, :cve:`2024-23851`, :cve:`2024-24860`, :cve:`2024-26586`, :cve:`2024-26589`, :cve:`2024-26591`, :cve:`2024-26592`, :cve:`2024-26593`, :cve:`2024-26594`, :cve:`2024-26597` and :cve:`2024-26598`
-  linux-yocto/5.15: Ignore :cve:`2020-27418`, :cve:`2020-36766`, :cve:`2021-33630`, :cve:`2021-33631`, :cve:`2022-48619`, :cve:`2023-2430`, :cve:`2023-4610`, :cve:`2023-4732`, :cve:`2023-5090`, :cve:`2023-5178`, :cve:`2023-5197`, :cve:`2023-5345`, :cve:`2023-5633`, :cve:`2023-5972`, :cve:`2023-6111`, :cve:`2023-6200`, :cve:`2023-6531`, :cve:`2023-6679`, :cve:`2023-7192`, :cve:`2023-40791`, :cve:`2023-42756`, :cve:`2023-44466`, :cve:`2023-45862`, :cve:`2023-45863`, :cve:`2023-45898`, :cve:`2023-51043`, :cve:`2023-51780`, :cve:`2023-51781`, :cve:`2023-51782`, :cve:`2023-52433`, :cve:`2023-52440`, :cve:`2023-52446`, :cve:`2023-52450`, :cve:`2023-52453`, :cve:`2023-52455`, :cve:`2023-52459`, :cve:`2023-52460`, :cve:`2023-52461`, :cve:`2023-52462`, :cve:`2024-0193`, :cve:`2024-0443`, :cve:`2024-0562`, :cve:`2024-0582`, :cve:`2024-0639`, :cve:`2024-0775`, :cve:`2024-26581`, :cve:`2024-26582`, :cve:`2024-26590`, :cve:`2024-26596` and :cve:`2024-26599`
-  linux-yocto/5.10: Fix :cve:`2023-6040`, :cve:`2023-6121`, :cve:`2023-6606`, :cve:`2023-6817`, :cve:`2023-6915`, :cve:`2023-6931`, :cve:`2023-6932`, :cve:`2023-39198`, :cve:`2023-46838`, :cve:`2023-51779`, :cve:`2023-51780`, :cve:`2023-51781`, :cve:`2023-51782`, :cve_mitre:`2023-52340`, :cve:`2024-0584` and :cve:`2024-0646`
-  linux-yocto/5.10: Ignore :cve:`2021-33630`, :cve:`2021-33631`, :cve:`2022-1508`, :cve:`2022-36402`, :cve:`2022-48619`, :cve:`2023-2430`, :cve:`2023-4610`, :cve:`2023-5972`, :cve:`2023-6039`, :cve:`2023-6200`, :cve:`2023-6531`, :cve:`2023-6546`, :cve:`2023-6622`, :cve:`2023-6679`, :cve:`2023-7192`, :cve:`2023-46343`, :cve:`2023-51042`, :cve:`2023-51043`, :cve:`2024-0193`, :cve:`2024-0443`, :cve:`2024-0562`, :cve:`2024-0582`, :cve:`2024-0639`, :cve:`2024-0641`, :cve:`2024-0775`, :cve:`2024-1085` and :cve:`2024-22705`
-  openssl: Fix :cve:`2024-0727`
-  python3-pycryptodome: Fix :cve:`2023-52323`
-  qemu: Fix :cve:`2023-6693`, :cve:`2023-42467` and :cve:`2024-24474`
-  vim: Fix :cve:`2024-22667`
-  xwayland: Fix :cve:`2023-6377` and :cve:`2023-6478`


Fixes in Yocto-4.0.17
~~~~~~~~~~~~~~~~~~~~~

-  bind: Upgrade to 9.18.24
-  bitbake: bitbake/codeparser.py: address ast module deprecations in py 3.12
-  bitbake: bitbake/lib/bs4/tests/test_tree.py: python 3.12 regex
-  bitbake: codeparser: replace deprecated ast.Str and 's'
-  bitbake: fetch2: Ensure that git LFS objects are available
-  bitbake: tests/fetch: Add real git lfs tests and decorator
-  bitbake: tests/fetch: git-lfs restore _find_git_lfs
-  bitbake: toaster/toastergui: Bug-fix verify given layer path only if import/add local layer
-  build-appliance-image: Update to kirkstone head revision
-  cmake: Unset CMAKE_CXX_IMPLICIT_INCLUDE_DIRECTORIES
-  contributor-guide: fix lore URL
-  curl: don't enable debug builds
-  cve_check: cleanup logging
-  dbus: Add missing :term:`CVE_PRODUCT`
-  dev-manual: sbom: Rephrase spdx creation
-  dev-manual: runtime-testing: gen-tapdevs need iptables installed
-  dev-manual: packages: clarify shared :term:`PR` service constraint
-  dev-manual: packages: need enough free space
-  dev-manual: start: remove idle line
-  feature-microblaze-versions.inc: python 3.12 regex
-  ghostscript: correct :term:`LICENSE` with AGPLv3
-  image-live.bbclass: LIVE_ROOTFS_TYPE support compression
-  kernel.bbclass: Set pkg-config variables for building modules
-  kernel.bbclass: introduce KERNEL_LOCALVERSION
-  kernel: fix localversion in v6.3+
-  kernel: make LOCALVERSION consistent between recipes
-  ldconfig-native: Fix to point correctly on the DT_NEEDED entries in an ELF file
-  librsvg: Fix do_package_qa error for librsvg
-  linux-firmware: upgrade to 20231211
-  linux-yocto/5.10: update to v5.10.210
-  linux-yocto/5.15: update to v5.15.150
-  manuals: add minimum RAM requirements
-  manuals: suppress excess use of "following" word
-  manuals: update disk space requirements
-  manuals: update references to buildtools
-  manuals: updates for building on Windows (WSL 2)
-  meta/lib/oeqa: python 3.12 regex
-  meta/recipes: python 3.12 regex
-  migration-guide: add release notes for 4.0.16
-  oeqa/selftest/oelib/buildhistory: git default branch
-  oeqa/selftest/recipetool: downgrade meson version to not use pyproject.toml
-  oeqa/selftest/recipetool: expect meson.bb
-  oeqa/selftest/recipetool: fix for python 3.12
-  oeqa/selftest/runtime_test: only run the virgl tests on qemux86-64
-  oeqa: replace deprecated assertEquals
-  openssl: Upgrade to 3.0.13
-  poky.conf: bump version for 4.0.17
-  populate_sdk_ext: use ConfigParser instead of SafeConfigParser
-  python3-jinja2: upgrade to 3.1.3
-  recipetool/create_buildsys_python: use importlib instead of imp
-  ref-manual: system-requirements: recommend buildtools for not supported distros
-  ref-manual: system-requirements: add info on buildtools-make-tarball
-  ref-manual: release-process: grammar fix
-  ref-manual: system-requirements: fix AlmaLinux variable name
-  ref-manual: system-requirements: modify anchor
-  ref-manual: system-requirements: remove outdated note
-  ref-manual: system-requirements: simplify supported distro requirements
-  ref-manual: system-requirements: update packages to build docs
-  scripts/runqemu: add qmp socket support
-  scripts/runqemu: direct mesa to use its own drivers, rather than ones provided by host distro
-  scripts/runqemu: fix regex escape sequences
-  scripts: python 3.12 regex
-  selftest: skip virgl gtk/sdl test on ubuntu 18.04
-  systemd: Only add myhostname to nsswitch.conf if in :term:`PACKAGECONFIG`
-  tzdata : Upgrade to 2024a
-  u-boot: Move UBOOT_INITIAL_ENV back to u-boot.inc
-  useradd-example: do not use unsupported clear text password
-  vim: upgrade to v9.0.2190
-  yocto-bsp: update to v5.15.150


Known Issues in Yocto-4.0.17
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.17
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Adrian Freihofer
-  Alassane Yattara
-  Alexander Kanavin
-  Alexander Sverdlin
-  Archana Polampalli
-  Baruch Siach
-  Bruce Ashfield
-  Chen Qi
-  Chris Laplante
-  Deepthi Hemraj
-  Dhairya Nagodra
-  Fabien Mahot
-  Fabio Estevam
-  Hitendra Prajapati
-  Hugo SIMELIERE
-  Jermain Horsman
-  Kai Kang
-  Lee Chee Yang
-  Ludovic Jozeau
-  Michael Opdenacker
-  Ming Liu
-  Munehisa Kamata
-  Narpat Mali
-  Nikhil R
-  Paul Eggleton
-  Paulo Neves
-  Peter Marko
-  Philip Lorenz
-  Poonam Jadhav
-  Priyal Doshi
-  Ross Burton
-  Simone Weiß
-  Soumya Sambu
-  Steve Sakoman
-  Tim Orling
-  Trevor Gamblin
-  Vijay Anusuri
-  Vivek Kumbhar
-  Wang Mingyu
-  Zahir Hussain


Repositories / Downloads for Yocto-4.0.17
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.17 </poky/log/?h=yocto-4.0.17>`
-  Git Revision: :yocto_git:`6d1a878bbf24c66f7186b270f823fcdf82e35383 </poky/commit/?id=6d1a878bbf24c66f7186b270f823fcdf82e35383>`
-  Release Artefact: poky-6d1a878bbf24c66f7186b270f823fcdf82e35383
-  sha: 3bc3010340b674f7b0dd0a7997f0167b2240b794fbd4aa28c0c4217bddd15e30
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.17/poky-6d1a878bbf24c66f7186b270f823fcdf82e35383.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.17/poky-6d1a878bbf24c66f7186b270f823fcdf82e35383.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.17 </openembedded-core/log/?h=yocto-4.0.17>`
-  Git Revision: :oe_git:`2501534c9581c6c3439f525d630be11554a57d24 </openembedded-core/commit/?id=2501534c9581c6c3439f525d630be11554a57d24>`
-  Release Artefact: oecore-2501534c9581c6c3439f525d630be11554a57d24
-  sha: 52cc6cce9e920bdce078584b89136e81cc01e0c55616fab5fca6c3e04264c88e
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.17/oecore-2501534c9581c6c3439f525d630be11554a57d24.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.17/oecore-2501534c9581c6c3439f525d630be11554a57d24.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.17 </meta-mingw/log/?h=yocto-4.0.17>`
-  Git Revision: :yocto_git:`f6b38ce3c90e1600d41c2ebb41e152936a0357d7 </meta-mingw/commit/?id=f6b38ce3c90e1600d41c2ebb41e152936a0357d7>`
-  Release Artefact: meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7
-  sha: 7d57167c19077f4ab95623d55a24c2267a3a3fb5ed83688659b4c03586373b25
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.17/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.17/meta-mingw-f6b38ce3c90e1600d41c2ebb41e152936a0357d7.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.17 </meta-gplv2/log/?h=yocto-4.0.17>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.17/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.17/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

meta-clang

-  Repository Location: :yocto_git:`/meta-clang`
-  Branch: :yocto_git:`kirkstone </meta-clang/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.17 </meta-clang/log/?h=yocto-4.0.17>`
-  Git Revision: :yocto_git:`eebe4ff2e539f3ffb01c5060cc4ca8b226ea8b52 </meta-clang/commit/?id=eebe4ff2e539f3ffb01c5060cc4ca8b226ea8b52>`
-  Release Artefact: meta-clang-eebe4ff2e539f3ffb01c5060cc4ca8b226ea8b52
-  sha: 3299e96e069a22c0971e903fbc191f2427efffc83d910ac51bf0237caad01d17
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.17/meta-clang-eebe4ff2e539f3ffb01c5060cc4ca8b226ea8b52.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.17/meta-clang-eebe4ff2e539f3ffb01c5060cc4ca8b226ea8b52.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.17 </bitbake/log/?h=yocto-4.0.17>`
-  Git Revision: :oe_git:`40fd5f4eef7460ca67f32cfce8e229e67e1ff607 </bitbake/commit/?id=40fd5f4eef7460ca67f32cfce8e229e67e1ff607>`
-  Release Artefact: bitbake-40fd5f4eef7460ca67f32cfce8e229e67e1ff607
-  sha: 5d20a0e4c5d0fce44bd84778168714a261a30a4b83f67c88df3b8a7e7115e444
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.17/bitbake-40fd5f4eef7460ca67f32cfce8e229e67e1ff607.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.17/bitbake-40fd5f4eef7460ca67f32cfce8e229e67e1ff607.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.17 </yocto-docs/log/?h=yocto-4.0.17>`
-  Git Revision: :yocto_git:`08ce7db2aa3a38deb8f5aa59bafc78542986babb </yocto-docs/commit/?id=08ce7db2aa3a38deb8f5aa59bafc78542986babb>`

