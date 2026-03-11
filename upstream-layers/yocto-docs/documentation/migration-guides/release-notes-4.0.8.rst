.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.8 (Kirkstone)
-----------------------------------------

Security Fixes in Yocto-4.0.8
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  apr-util: Fix :cve_nist:`2022-25147`
-  apr: Fix :cve_nist:`2022-24963`, :cve_nist:`2022-28331` and :cve_nist:`2021-35940`
-  bind: Fix :cve_nist:`2022-3094`, :cve_nist:`2022-3736` and :cve_nist:`2022-3924`
-  git: Ignore :cve_nist:`2022-41953`
-  git: Fix :cve_nist:`2022-23521` and :cve_nist:`2022-41903`
-  libgit2: Fix :cve_nist:`2023-22742`
-  ppp: Fix :cve_nist:`2022-4603`
-  python3-certifi: Fix :cve_nist:`2022-23491`
-  sudo: Fix :cve_nist:`2023-22809`
-  tar: Fix :cve_nist:`2022-48303`


Fixes in Yocto-4.0.8
~~~~~~~~~~~~~~~~~~~~

-  core-image.bbclass: Fix missing leading whitespace with ':append'
-  populate_sdk_ext.bbclass: Fix missing leading whitespace with ':append'
-  ptest-packagelists.inc: Fix missing leading whitespace with ':append'
-  apr-util: upgrade to 1.6.3
-  apr: upgrade to 1.7.2
-  apt: fix do_package_qa failure
-  bind: upgrade to 9.18.11
-  bitbake: bb/utils: include SSL certificate paths in export_proxies
-  bitbake: bitbake-diffsigs: Make PEP8 compliant
-  bitbake: bitbake-diffsigs: break on first dependent task difference
-  bitbake: fetch2/git: Clarify the meaning of namespace
-  bitbake: fetch2/git: Prevent git fetcher from fetching gitlab repository metadata
-  bitbake: fetch2/git: show SRCREV and git repo in error message about fixed SRCREV
-  bitbake: siggen: Fix inefficient string concatenation
-  bitbake: utils/ply: Update md5 to better report errors with hashlib
-  bootchart2: Fix usrmerge support
-  bsp-guide: fix broken git URLs and missing word
-  build-appliance-image: Update to kirkstone head revision
-  buildtools-tarball: set pkg-config search path
-  classes/fs-uuid: Fix command output decoding issue
-  dev-manual: common-tasks.rst: add link to FOSDEM 2023 video
-  dev-manual: fix old override syntax
-  devshell: Do not add scripts/git-intercept to PATH
-  devtool: fix devtool finish when gitmodules file is empty
-  diffutils: upgrade to 3.9
-  gdk-pixbuf: do not use tools from gdk-pixbuf-native when building tests
-  git: upgrade to 2.35.7
-  glslang: branch rename master -> main
-  httpserver: add error handler that write to the logger
-  image.bbclass: print all QA functions exceptions
-  kernel/linux-kernel-base: Fix kernel build artefact determinism issues
-  libc-locale: Fix on target locale generation
-  libgit2: upgrade to 1.4.5
-  libjpeg-turbo: upgrade to 2.1.5
-  libtirpc: Check if file exists before operating on it
-  libusb1: Link with latomic only if compiler has no atomic builtins
-  libusb1: Strip trailing whitespaces
-  linux-firmware: upgrade to 20230117
-  linux-yocto/5.15: update to v5.15.91
-  lsof: fix old override syntax
-  lttng-modules: Fix for 5.10.163 kernel version
-  lttng-tools: upgrade to 2.13.9
-  make-mod-scripts: Ensure kernel build output is deterministic
-  manuals: update patchwork instance URL
-  meta: remove True option to getVar and getVarFlag calls (again)
-  migration-guides: add release-notes for 4.0.7
-  native: Drop special variable handling
-  numactl: skip test case when target platform doesn't have 2 CPU node
-  oeqa context.py: fix --target-ip comment to include ssh port number
-  oeqa dump.py: add error counter and stop after 5 failures
-  oeqa qemurunner.py: add timeout to QMP calls
-  oeqa qemurunner.py: try to avoid reading one character at a time
-  oeqa qemurunner: read more data at a time from serial
-  oeqa ssh.py: add connection keep alive options to ssh client
-  oeqa ssh.py: move output prints to new line
-  oeqa/qemurunner: do not use Popen.poll() when terminating runqemu with a signal
-  oeqa/selftest/bbtests: Update message lookup for test_git_unpack_nonetwork_fail
-  oeqa/selftest/locales: Add selftest for locale generation/presence
-  poky.conf: Update SANITY_TESTED_DISTROS to match autobuilder
-  poky.conf: bump version for 4.0.8
-  profile-manual: update WireShark hyperlinks
-  python3-pytest: depend on python3-tomli instead of python3-toml
-  qemu: fix compile error
-  quilt: fix intermittent failure in faildiff.test
-  quilt: use upstreamed faildiff.test fix
-  recipe_sanity: fix old override syntax
-  ref-manual: document SSTATE_EXCLUDEDEPS_SYSROOT
-  scons.bbclass: Make MAXLINELENGTH overridable
-  scons: Pass MAXLINELENGTH to scons invocation
-  sdkext/cases/devtool: pass a logger to HTTPService
-  spirv-headers: set correct branch name
-  sudo: upgrade to 1.9.12p2
-  system-requirements.rst: add Fedora 36 and AlmaLinux 8.7 to list of supported distros
-  testimage: Fix error message to reflect new syntax
-  update-alternatives: fix typos
-  vulkan-samples: branch rename master -> main


Known Issues in Yocto-4.0.8
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.8
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alejandro Hernandez Samaniego
-  Alexander Kanavin
-  Alexandre Belloni
-  Armin Kuster
-  Arnout Vandecappelle
-  Bruce Ashfield
-  Changqing Li
-  Chee Yang Lee
-  Etienne Cordonnier
-  Harald Seiler
-  Kai Kang
-  Khem Raj
-  Lee Chee Yang
-  Louis Rannou
-  Marek Vasut
-  Marius Kriegerowski
-  Mark Hatle
-  Martin Jansa
-  Mauro Queiros
-  Michael Opdenacker
-  Mikko Rapeli
-  Mingli Yu
-  Narpat Mali
-  Niko Mauno
-  Pawel Zalewski
-  Peter Kjellerstedt
-  Richard Purdie
-  Rodolfo Quesada Zumbado
-  Ross Burton
-  Sakib Sajal
-  Schmidt, Adriaan
-  Steve Sakoman
-  Thomas Roos
-  Ulrich Ölmann
-  Xiangyu Chen


Repositories / Downloads for Yocto-4.0.8
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.8 </poky/log/?h=yocto-4.0.8>`
-  Git Revision: :yocto_git:`a361fb3df9c87cf12963a9d785a9f99faa839222 </poky/commit/?id=a361fb3df9c87cf12963a9d785a9f99faa839222>`
-  Release Artefact: poky-a361fb3df9c87cf12963a9d785a9f99faa839222
-  sha: af4e8d64be27d3a408357c49b7952ce04c6d8bb0b9d7b50c48848d9355de7fc2
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.8/poky-a361fb3df9c87cf12963a9d785a9f99faa839222.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.8/poky-a361fb3df9c87cf12963a9d785a9f99faa839222.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.8 </openembedded-core/log/?h=yocto-4.0.8>`
-  Git Revision: :oe_git:`b20e2134daec33fbb8ce358d984751d887752bd5 </openembedded-core/commit/?id=b20e2134daec33fbb8ce358d984751d887752bd5>`
-  Release Artefact: oecore-b20e2134daec33fbb8ce358d984751d887752bd5
-  sha: 63cce6f1caf8428eefc1471351ab024affc8a41d8d7777f525e3aa9ea454d2cd
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.8/oecore-b20e2134daec33fbb8ce358d984751d887752bd5.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.8/oecore-b20e2134daec33fbb8ce358d984751d887752bd5.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.8 </meta-mingw/log/?h=yocto-4.0.8>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.8/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.8/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.8 </meta-gplv2/log/?h=yocto-4.0.8>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.8/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.8/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.8 </bitbake/log/?h=yocto-4.0.8>`
-  Git Revision: :oe_git:`9bbdedc0ba7ca819b898e2a29a151d6a2014ca11 </bitbake/commit/?id=9bbdedc0ba7ca819b898e2a29a151d6a2014ca11>`
-  Release Artefact: bitbake-9bbdedc0ba7ca819b898e2a29a151d6a2014ca11
-  sha: 8e724411f4df00737e81b33eb568f1f97d2a00d5364342c0a212c46abb7b005b
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.8/bitbake-9bbdedc0ba7ca819b898e2a29a151d6a2014ca11.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.8/bitbake-9bbdedc0ba7ca819b898e2a29a151d6a2014ca11.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.8 </yocto-docs/log/?h=yocto-4.0.8>`
-  Git Revision: :yocto_git:`16ecbe028f2b9cc021267817a5413054e070b563 </yocto-docs/commit/?id=16ecbe028f2b9cc021267817a5413054e070b563>`

