Release notes for Yocto-4.0.5 (Kirkstone)
-----------------------------------------

Security Fixes in Yocto-4.0.5
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  qemu: fix :cve:`2021-3750`, :cve:`2021-3611` and :cve:`2022-2962`
-  binutils : fix :cve:`2022-38126`, :cve:`2022-38127` and :cve:`2022-38128`
-  tff: fix :cve:`2022-2867`, :cve:`2022-2868` and :cve:`2022-2869`
-  inetutils: fix :cve:`2022-39028`
-  go: fix :cve:`2022-27664`

Fixes in Yocto-4.0.5
~~~~~~~~~~~~~~~~~~~~

-  Revert "gcc-cross-canadian: Add symlink to real-ld alongside other symlinks"
-  bind: upgrade to 9.18.7
-  binutils: stable 2.38 branch updates (dc2474e7)
-  bitbake: Fix npm to use https rather than http
-  bitbake: asyncrpc/client: Fix unix domain socket chdir race issues
-  bitbake: bitbake: Add copyright headers where missing
-  bitbake: gitsm: Error out if submodule refers to parent repo
-  bitbake: runqueue: Drop deadlock breaking force fail
-  bitbake: runqueue: Ensure deferred tasks are sorted by multiconfig
-  bitbake: runqueue: Improve deadlock warning messages
-  bitbake: siggen: Fix insufficent entropy in sigtask file names
-  bitbake: tests/fetch: Allow handling of a file:// url within a submodule
-  build-appliance-image: Update to kirkstone head revision (4a88ada)
-  busybox: add devmem 128-bit support
-  classes: files: Extend overlayfs-etc class
-  coreutils: add openssl PACKAGECONFIG
-  create-pull-request: don't switch the git remote protocol to git://
-  dev-manual: fix reference to BitBake user manual
-  expat: upgrade 2.4.8 -> 2.4.9
-  files: overlayfs-etc: refactor preinit template
-  gcc-cross-canadian: add default plugin linker
-  gcc: add arm-v9 support
-  git: upgrade 2.35.4 -> 2.35.5
-  glibc-locale: explicitly remove empty dirs in ${libdir}
-  glibc-tests: use += instead of :append
-  glibc: stable 2.35 branch updates.(8d125a1f)
-  go-native: switch from SRC_URI:append to SRC_URI +=
-  image_types_wic.bbclass: fix cross binutils dependency
-  kern-tools: allow 'y' or 'm' to avoid config audit warnings
-  kern-tools: fix queue processing in relative TOPDIR configurations
-  kernel-yocto: allow patch author date to be commit date
-  libpng: upgrade to 1.6.38
-  linux-firmware: package new Qualcomm firmware
-  linux-firmware: upgrade 20220708 -> 20220913
-  linux-libc-headers: switch from SRC_URI:append to SRC_URI +=
-  linux-yocto-dev: add qemuarm64
-  linux-yocto/5.10: update to v5.10.149
-  linux-yocto/5.15: cfg: fix ACPI warnings for -tiny
-  linux-yocto/5.15: update to v5.15.68
-  local.conf.sample: correct the location of public hashserv
-  ltp: Fix pread02 case trigger the glibc overflow detection
-  lttng-modules: Fix crash on powerpc64
-  lttng-tools: Disable on qemuriscv32
-  lttng-tools: Disable on riscv32
-  migration-guides: add 4.0.4 release notes
-  oeqa/runtime/dnf: fix typo
-  own-mirrors: add crate
-  perf: Fix for recent kernel upgrades
-  poky.conf: bump version for 4.0.5
-  poky.yaml.in: update version requirements
-  python3-rfc3986-validator: switch from SRC_URI:append to SRC_URI +=
-  python3: upgrade 3.10.4 -> 3.10.7
-  qemu: Backport patches from upstream to support float128 on qemu-ppc64
-  rpm: Remove -Wimplicit-function-declaration warnings
-  rpm: update to 4.17.1
-  rsync: update to 3.2.5
-  stress-cpu: disable float128 math on powerpc64 to avoid SIGILL
-  tune-neoversen2: support tune-neoversen2 base on armv9a
-  tzdata: update to 2022d
-  u-boot: switch from append to += in SRC_URI
-  uninative: Upgrade to 3.7 to work with glibc 2.36
-  vim: Upgrade to 9.0.0598
-  webkitgtk: Update to 2.36.7


Known Issues in Yocto-4.0.5
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- There are recent CVEs in key components such as openssl. They are not included in this release as it was built before the issues were known and fixes were available but these are now available on the kirkstone branch. 


Contributors to Yocto-4.0.5
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Adrian Freihofer
-  Alexander Kanavin
-  Alexandre Belloni
-  Bhabu Bindu
-  Bruce Ashfield
-  Chen Qi
-  Daniel McGregor
-  Denys Dmytriyenko
-  Dmitry Baryshkov
-  Florin Diaconescu
-  He Zhe
-  Joshua Watt
-  Khem Raj
-  Martin Jansa
-  Michael Halstead
-  Michael Opdenacker
-  Mikko Rapeli
-  Mingli Yu
-  Neil Horman
-  Pavel Zhukov
-  Richard Purdie
-  Robert Joslyn
-  Ross Burton
-  Ruiqiang Hao
-  Samuli Piippo
-  Steve Sakoman
-  Sundeep KOKKONDA
-  Teoh Jay Shen
-  Tim Orling
-  Virendra Thakur
-  Vyacheslav Yurkov
-  Xiangyu Chen
-  Yash Shinde
-  pgowda
-  Wang Mingyu


Repositories / Downloads for Yocto-4.0.5
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.5 </poky/log/?h=yocto-4.0.5>`
-  Git Revision: :yocto_git:`2e79b199114b25d81bfaa029ccfb17676946d20d </poky/commit/?id=2e79b199114b25d81bfaa029ccfb17676946d20d>`
-  Release Artefact: poky-2e79b199114b25d81bfaa029ccfb17676946d20d
-  sha: 7bcf3f901d4c5677fc95944ab096e9e306f4c758a658dde5befd16861ad2b8ea
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.5/poky-2e79b199114b25d81bfaa029ccfb17676946d20d.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.5/poky-2e79b199114b25d81bfaa029ccfb17676946d20d.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.5 </openembedded-core/log/?h=yocto-4.0.5>`
-  Git Revision: :oe_git:`fbdf93f43ff4b876487e1f26752598ec8abcb46e </openembedded-core/commit/?id=fbdf93f43ff4b876487e1f26752598ec8abcb46e>`
-  Release Artefact: oecore-fbdf93f43ff4b876487e1f26752598ec8abcb46e
-  sha: 2d9b5a8e9355b633bb57633cc8c2d319ba13fe4721f79204e61116b3faa6cbf1
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.5/oecore-fbdf93f43ff4b876487e1f26752598ec8abcb46e.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.5/oecore-fbdf93f43ff4b876487e1f26752598ec8abcb46e.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.5 </meta-mingw/log/?h=yocto-4.0.5>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.5/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.5/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.5 </meta-gplv2/log/?h=yocto-4.0.5>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.5/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.5/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.5 </bitbake/log/?h=yocto-4.0.5>`
-  Git Revision: :oe_git:`c90d57497b9bcd237c3ae810ee8edb5b0d2d575a </bitbake/commit/?id=c90d57497b9bcd237c3ae810ee8edb5b0d2d575a>`
-  Release Artefact: bitbake-c90d57497b9bcd237c3ae810ee8edb5b0d2d575a
-  sha: 5698d548ce179036e46a24f80b213124c8825a4f443fa1d6be7ab0f70b01a9ff
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.5/bitbake-c90d57497b9bcd237c3ae810ee8edb5b0d2d575a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.5/bitbake-c90d57497b9bcd237c3ae810ee8edb5b0d2d575a.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.5 </yocto-docs/log/?h=yocto-4.0.5>`
-  Git Revision: :yocto_git:`8c2f9f54e29781f4ee72e81eeaa12ceaa82dc2d3 </yocto-docs/commit/?id=8c2f9f54e29781f4ee72e81eeaa12ceaa82dc2d3>`

