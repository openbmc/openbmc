.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 3.4.2 (honister)
----------------------------------

Security Fixes in 3.4.2
~~~~~~~~~~~~~~~~~~~~~~~

-  tiff: backport fix for :cve:`2022-22844`
-  glibc : Fix :cve:`2021-3999`
-  glibc : Fix :cve:`2021-3998`
-  glibc : Fix :cve:`2022-23219`
-  glibc : Fix :cve:`2022-23218`
-  lighttpd: backport a fix for :cve:`2022-22707`
-  speex: fix :cve:`2020-23903`
-  linux-yocto/5.10: amdgpu: updates for :cve:`2021-42327`
-  libsndfile1: fix :cve:`2021-4156`
-  xserver-xorg: whitelist two CVEs
-  grub2: fix :cve:`2021-3981`
-  xserver-xorg: update :term:`CVE_PRODUCT`
-  binutils: :cve:`2021-42574`
-  gcc: Fix :cve:`2021-42574`
-  gcc: Fix :cve:`2021-35465`
-  cve-extra-exclusions: add db CVEs to exclusion list
-  gcc: Add :cve:`2021-37322` to the list of CVEs to ignore
-  bind: fix :cve:`2021-25219`
-  openssh: fix :cve:`2021-41617`
-  ncurses: fix :cve:`2021-39537`
-  vim: fix :cve:`2021-3968` and :cve:`2021-3973`
-  vim: fix :cve:`2021-3927` and :cve:`2021-3928`
-  gmp: fix :cve:`2021-43618`

Fixes in 3.4.2
~~~~~~~~~~~~~~

-  build-appliance-image: Update to honister head revision
-  poky.conf: bump version for 3.4.2 release
-  libxml2: Backport python3-lxml workaround patch
-  core-image-sato-sdk: allocate more memory when in qemu
-  vim: upgrade to patch 4269
-  vim: update to include latest CVE fixes
-  expat: upgrade to 2.4.4
-  libusb1: correct :term:`SRC_URI`
-  yocto-check-layer: add debug output for the layers that were found
-  linux-firmware: Add CLM blob to linux-firmware-bcm4373 package
-  linux-yocto/5.10: update to v5.10.93
-  icu: fix make_icudata dependencies
-  sstate: Improve failure to obtain archive message/handling
-  insane.bbclass: Correct package_qa_check_empty_dirs()
-  sstate: A third fix for for touching files inside pseudo
-  kernel: introduce python3-dtschema-wrapper
-  vim: upgrade to 8.2 patch 3752
-  bootchart2: Add missing python3-math dependency
-  socat: update :term:`SRC_URI`
-  pigz: fix one failure of command "unpigz -l"
-  linux-yocto/5.14: update genericx86* machines to v5.14.21
-  linux-yocto/5.10: update genericx86* machines to v5.10.87
-  go: upgrade 1.16.10 -> 1.16.13
-  linux-yocto/5.10/cfg: add kcov feature fragment
-  linux-yocto/5.14: fix arm 32bit -rt warnings
-  oeqa/sstate: Fix allarch samesigs test
-  rootfs-postcommands.bbclass: Make two comments use the new variable syntax
-  cve-check: add lockfile to task
-  lib/oe/reproducible: correctly set .git location when recursively looking for git repos
-  epiphany: Update 40.3 -> 40.6
-  scripts/buildhistory-diff: drop use of distutils
-  scripts: Update to use exec_module() instead of load_module()
-  vulkan-loader: inherit pkgconfig
-  webkitgtk: Add reproducibility fix
-  openssl: Add reproducibility fix
-  rpm: remove tmp folder created during install
-  package_manager: ipk: Fix host manifest generation
-  bitbake: utils: Update to use exec_module() instead of load_module()
-  linux-yocto: add libmpc-native to :term:`DEPENDS`
-  ref-manual: fix patch documentation
-  bitbake: tests/fetch: Drop gnu urls from wget connectivity test
-  bitbake: fetch: npm: Use temporary file for empty user config
-  bitbake: fetch: npm: Quote destdir in run chmod command
-  bitbake: process: Do not mix stderr with stdout
-  xserver-xorg: upgrade 1.20.13 -> 1.20.14
-  python3-pyelftools: Depend on debugger, pprint
-  linux-firmware: upgrade 20211027 -> 20211216
-  oeqa/selftest/bbtests: Use YP sources mirror instead of GNU
-  systemd: Fix systemd-journal-gateway user/groups
-  license.bbclass: implement ast.NodeVisitor.visit_Constant
-  oe/license: implement ast.NodeVisitor.visit_Constant
-  packagedata.py: silence a DeprecationWarning
-  uboot-sign: fix the concatenation when multiple U-BOOT configurations are specified
-  runqemu: check the qemu PID has been set before kill()ing it
-  selftest/devtool: Check branch in git fetch
-  recipetool: Set master branch only as fallback
-  kern-tools: bug fixes and kgit-gconfig
-  linux-yocto-rt/5.10: update to -rt56
-  linux-yocto/5.14: update to v5.14.21
-  python3: upgrade 3.9.7 -> 3.9.9
-  bitbake: lib/pyinotify.py: Remove deprecated module asyncore
-  updates for recent releases
-  libdrm: upgrade 2.4.108 -> 2.4.109
-  patch.py: Initialize git repo before patching
-  boost: Fix build on arches with no atomics
-  boost: allow searching for python310
-  recipetool: extend curl detection when creating recipes
-  recipetool: handle GitLab URLs like we do GitHub
-  README.OE-Core.md: update URLs
-  libtool: change the default AR_FLAGS from "cru" to "cr"
-  libtool: Update patchset to match those submitted upstream
-  scripts/checklayer/common.py: Fixed a minor grammatical error
-  oeqa/parselogs: Fix quoting
-  oeqa/utils/dump: Fix typo
-  systemd: update 249.6 -> 249.7
-  glibc: Fix i586/c3 support
-  wic: support rootdev identified by partition label
-  buildhistory: Fix srcrevs output
-  classes/crate-fetch: Ensure crate fetcher is available
-  rootfs-postcommands: update systemd_create_users
-  classes/meson: Add optional rust definitions
-  rust-cross: Replace :term:`TARGET_ARCH` with :term:`TUNE_PKGARCH`
-  maintainers.inc: fix up rust-cross entry
-  rust-cross: Fix directory not deleted for race glibc vs. musl
-  wic: use shutil.which
-  bitbake: data_smart.py: Skip old override syntax checking for anonymous functions
-  documentation: conf.py: fix version of bitbake objects.inv
-  updates for release 3.3.4

Contributors to 3.4.2
~~~~~~~~~~~~~~~~~~~~~

-  Alexander Kanavin
-  Alexandre Belloni
-  Anton Mikanovich
-  Anuj Mittal
-  Bruce Ashfield
-  Carlos Rafael Giani
-  Chaitanya Vadrevu
-  Changqing Li
-  Dhruva Gole
-  Florian Amstutz
-  Joshua Watt
-  Kai Kang
-  Khairul Rohaizzat Jamaluddin
-  Khem Raj
-  Konrad Weihmann
-  Kory Maincent
-  Li Wang
-  Marek Vasut
-  Markus Volk
-  Martin Jansa
-  Max Krummenacher
-  Michael Opdenacker
-  Mingli Yu
-  Oleksiy Obitotskyy
-  Pavel Zhukov
-  Peter Kjellerstedt
-  Pgowda
-  Quentin Schulz
-  Richard Purdie
-  Robert Yang
-  Ross Burton
-  Rudolf J Streif
-  Sakib Sajal
-  Samuli Piippo
-  Schmidt, Adriaan
-  Stefan Herbrechtsmeier
-  Steve Sakoman
-  Sundeep KOKKONDA
-  Teoh Jay Shen
-  Thomas Perrot
-  Tim Orling
-  Vyacheslav Yurkov
-  Yongxin Liu
-  pgowda
-  Wang Mingyu

Repositories / Downloads for 3.4.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`honister </poky/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.2 </poky/tag/?h=yocto-3.4.2>`
-  Git Revision: :yocto_git:`e0ab08bb6a32916b457d221021e7f402ffa36b1a </poky/commit/?id=e0ab08bb6a32916b457d221021e7f402ffa36b1a>`
-  Release Artefact: poky-e0ab08bb6a32916b457d221021e7f402ffa36b1a
-  sha: 8580dc5067ee426fe347a0d0f7a74c29ba539120bbe8438332339a9c8bce00fd
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.2/poky-e0ab08bb6a32916b457d221021e7f402ffa36b1a.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.2/poky-e0ab08bb6a32916b457d221021e7f402ffa36b1a.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`honister </openembedded-core/log/?h=honister>`
-  Tag: :oe_git:`yocto-3.4.2 </openembedded-core/tag/?h=yocto-3.4.2>`
-  Git Revision: :oe_git:`418a9c4c31615a9e3e011fc2b21fb7154bc6c93a </openembedded-core/commit/?id=418a9c4c31615a9e3e011fc2b21fb7154bc6c93a>`
-  Release Artefact: oecore-418a9c4c31615a9e3e011fc2b21fb7154bc6c93a
-  sha: f2ca94a5a7ec669d4c208d1729930dfc1b917846dbb2393d01d6d5856fcbc6de
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.2/oecore-418a9c4c31615a9e3e011fc2b21fb7154bc6c93a.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.2/oecore-418a9c4c31615a9e3e011fc2b21fb7154bc6c93a.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`meta-mingw`
-  Branch: :yocto_git:`honister </meta-mingw/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.2 </meta-mingw/tag/?h=yocto-3.4.2>`
-  Git Revision: :yocto_git:`f5d761cbd5c957e4405c5d40b0c236d263c916a8 </meta-mingw/commit/?id=f5d761cbd5c957e4405c5d40b0c236d263c916a8>`
-  Release Artefact: meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8
-  sha: d4305d638ef80948584526c8ca386a8cf77933dffb8a3b8da98d26a5c40fcc11
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.2/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.2/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`honister </meta-gplv2/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.2 </meta-gplv2/tag/?h=yocto-3.4.2>`
-  Git Revision: :yocto_git:`f04e4369bf9dd3385165281b9fa2ed1043b0e400 </meta-gplv2/commit/?id=f04e4369bf9dd3385165281b9fa2ed1043b0e400>`
-  Release Artefact: meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400
-  sha: ef8e2b1ec1fb43dbee4ff6990ac736315c7bc2d8c8e79249e1d337558657d3fe
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.2/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.2/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`1.52 </bitbake/log/?h=1.52>`
-  Tag: :oe_git:`yocto-3.4.2 </bitbake/tag/?h=yocto-3.4.2>`
-  Git Revision: :oe_git:`c039182c79e2ccc54fff5d7f4f266340014ca6e0 </bitbake/commit/?id=c039182c79e2ccc54fff5d7f4f266340014ca6e0>`
-  Release Artefact: bitbake-c039182c79e2ccc54fff5d7f4f266340014ca6e0
-  sha: bd80297f8d8aa40cbcc8a3d4e23a5223454b305350adf34cd29b5fb65c1b4c52
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.2/bitbake-c039182c79e2ccc54fff5d7f4f266340014ca6e0.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.2/bitbake-c039182c79e2ccc54fff5d7f4f266340014ca6e0.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`honister </yocto-docs/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.2 </yocto-docs/tag/?h=yocto-3.4.2>`
-  Git Revision: :yocto_git:`3061d3d62054a5c3b9e16bfce4bcd186fa7a23d2` </yocto-docs/commit/?3061d3d62054a5c3b9e16bfce4bcd186fa7a23d2>`
