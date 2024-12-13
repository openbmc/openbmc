.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.1.4 (Langdale)
----------------------------------------

Security Fixes in Yocto-4.1.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  cve-extra-exclusions/linux-yocto: Ignore :cve_nist:`2020-27784`, :cve_nist:`2021-3669`, :cve_nist:`2021-3759`, :cve_nist:`2021-4218`, :cve_nist:`2022-0480`, :cve_nist:`2022-1184`, :cve_nist:`2022-1462`, :cve_nist:`2022-2308`, :cve_nist:`2022-2327`, :cve_nist:`2022-26365`, :cve_nist:`2022-2663`, :cve_nist:`2022-2785`, :cve_nist:`2022-3176`, :cve_nist:`2022-33740`, :cve_nist:`2022-33741`, :cve_nist:`2022-33742`, :cve_nist:`2022-3526`, :cve_nist:`2022-3563`, :cve_nist:`2022-3621`, :cve_nist:`2022-3623`, :cve_nist:`2022-3624`, :cve_nist:`2022-3625`, :cve_nist:`2022-3629`, :cve_nist:`2022-3630`, :cve_nist:`2022-3633`, :cve_nist:`2022-3635`, :cve_nist:`2022-3636`, :cve_nist:`2022-3637`, :cve_nist:`2022-3646` and :cve_nist:`2022-3649`
-  cve-extra-exclusions/linux-yocto 5.15: Ignore :cve_nist:`2022-3435`, :cve_nist:`2022-3534`, :cve_nist:`2022-3564`, :cve_nist:`2022-3564`, :cve_nist:`2022-3619`, :cve_nist:`2022-3640`, :cve_nist:`2022-42895`, :cve_nist:`2022-42896`, :cve_nist:`2022-4382`, :cve_nist:`2023-0266` and :cve_nist:`2023-0394`
-  epiphany: Fix :cve_nist:`2023-26081`
-  git: Ignore :cve_nist:`2023-22743`
-  go: Fix :cve_nist:`2022-41722`, :cve_nist:`2022-41723`, :cve_nist:`2022-41724`, :cve_nist:`2022-41725` and :cve_nist:`2023-24532`
-  harfbuzz: Fix :cve_nist:`2023-25193`
-  libmicrohttpd: Fix :cve_nist:`2023-27371`
-  libxml2: Fix :cve_nist:`2022-40303` and :cve_nist:`2022-40304`
-  openssl: Fix :cve_nist:`2023-0464`, :cve_nist:`2023-0465` and :cve_nist:`2023-0466`
-  python3-setuptools: Fix :cve_nist:`2022-40897`
-  qemu: Fix :cve_nist:`2022-4144`
-  screen: Fix :cve_nist:`2023-24626`
-  shadow: Ignore :cve_nist:`2016-15024`
-  tiff: Fix :cve_nist:`2022-48281`, :cve_nist:`2023-0795`, :cve_nist:`2023-0796`, :cve_nist:`2023-0797`, :cve_nist:`2023-0798`, :cve_nist:`2023-0799`, :cve_nist:`2023-0800`, :cve_nist:`2023-0801`, :cve_nist:`2023-0802`, :cve_nist:`2023-0803` and :cve_nist:`2023-0804`
-  vim: Fix :cve_nist:`2023-1127`, :cve_nist:`2023-1170`, :cve_nist:`2023-1175`, :cve_nist:`2023-1264` and :cve_nist:`2023-1355`
-  xdg-utils: Fix :cve_nist:`2022-4055`
-  xserver-xorg: Fix for :cve_nist:`2023-1393`


Fixes in Yocto-4.1.4
~~~~~~~~~~~~~~~~~~~~

-  apt: re-enable version check
-  base-files: Drop localhost.localdomain from hosts file
-  binutils: Fix nativesdk ld.so search
-  bitbake: bin/utils: Ensure locale en_US.UTF-8 is available on the system
-  bitbake: cookerdata: Drop dubious exception handling code
-  bitbake: cookerdata: Improve early exception handling
-  bitbake: cookerdata: Remove incorrect SystemExit usage
-  bitbake: fetch/git: Fix local clone url to make it work with repo
-  bitbake: toaster: Add refreshed oe-core and poky fixtures
-  bitbake: toaster: fixtures/README: django 1.8 -> 3.2
-  bitbake: toaster: fixtures/gen_fixtures.py: update branches
-  bitbake: utils: Allow to_boolean to support int values
-  bmap-tools: switch to main branch
-  build-appliance-image: Update to langdale head revision
-  buildtools-tarball: Handle spaces within user $PATH
-  busybox: move hwclock init earlier in startup
-  cargo.bbclass: use offline mode for building
-  cpio: Fix wrong CRC with ASCII CRC for large files
-  cracklib: update github branch to 'main'
-  cups: add/fix web interface packaging
-  cups: check :term:`PACKAGECONFIG` for pam feature
-  cups: use BUILDROOT instead of DESTDIR
-  cve-check: Fix false negative version issue
-  devtool/upgrade: do not delete the workspace/recipes directory
-  dhcpcd: Fix install conflict when enable multilib.
-  ffmpeg: fix build failure when vulkan is enabled
-  filemap.py: enforce maximum of 4kb block size
-  gcc-shared-source: do not use ${S}/.. in deploy_source_date_epoch
-  glibc: Add missing binutils dependency
-  go: upgrade to 1.19.7
-  image_types: fix multiubi var init
-  image_types: fix vname var init in multiubi_mkfs() function
-  iso-codes: upgrade to 4.13.0
-  kernel-devsrc: fix mismatched compiler warning
-  lib/oe/gpg_sign.py: Avoid race when creating .sig files in detach_sign
-  lib/resulttool: fix typo breaking resulttool log --ptest
-  libcomps: Fix callback function prototype for PyCOMPS_hash
-  libdnf: upgrade to 0.70.0
-  libgit2: update license information
-  libmicrohttpd: upgrade to 0.9.76
-  linux-yocto-rt/5.15: upgrade to -rt59
-  linux-yocto/5.15: upgrade to v5.15.108
-  linux: inherit pkgconfig in kernel.bbclass
-  lttng-modules: upgrade to v2.13.9
-  lua: Fix install conflict when enable multilib.
-  mdadm: Fix raid0, 06wrmostly and 02lineargrow tests
-  mesa-demos: packageconfig weston should have a dependency on wayland-protocols
-  meson: Fix wrapper handling of implicit setup command
-  meson: remove obsolete RPATH stripping patch
-  migration-guides: update release notes
-  oeqa ping.py: avoid busylooping failing ping command
-  oeqa ping.py: fail test if target IP address has not been set
-  oeqa rtc.py: skip if read-only-rootfs
-  oeqa/runtime: clean up deprecated backslash expansion
-  oeqa/sdk: Improve Meson test
-  oeqa/selftest/cases/package.py: adding unittest for package rename conflicts
-  oeqa/selftest/cases/runqemu: update imports
-  oeqa/selftest/prservice: Improve debug output for failure
-  oeqa/selftest/reproducible: Split different packages from missing packages output
-  oeqa/selftest: OESelftestTestContext: convert relative to full path when newbuilddir is provided
-  oeqa/targetcontrol: do not set dump_host_cmds redundantly
-  oeqa/targetcontrol: fix misspelled RuntimeError
-  oeqa/targetcontrol: remove unused imports
-  oeqa/utils/commands: fix usage of undefined EPIPE
-  oeqa/utils/commands: remove unused imports
-  oeqa/utils/qemurunner: replace hard-coded user 'root' in debug output
-  oeqs/selftest: OESelftestTestContext: replace the os.environ after subprocess.check_output
-  package.bbclass: check packages name conflict in do_package
-  pango: upgrade to 1.50.13
-  piglit: Fix build time dependency
-  poky.conf: bump version for 4.1.4
-  populate_sdk_base: add zip options
-  populate_sdk_ext: Handle spaces within user $PATH
-  pybootchart: Fix extents handling to account for cpu/io/mem pressure changes
-  pybootchartui: Fix python syntax issue
-  report-error: catch Nothing :term:`PROVIDES` error
-  rpm: Fix hdr_hash function prototype
-  run-postinsts: Set dependency for ldconfig to avoid boot issues
-  runqemu: respect :term:`IMAGE_LINK_NAME`
-  runqemu: Revert "workaround for APIC hang on pre 4.15 kernels on qemux86q"
-  scripts/lib/buildstats: handle top-level build_stats not being complete
-  selftest/recipetool: Stop test corrupting tinfoil class
-  selftest/runtime_test/virgl: Disable for all Rocky Linux
-  selftest: devtool: set :term:`BB_HASHSERVE_UPSTREAM` when setting :term:`SSTATE_MIRRORS`
-  selftest: runqemu: better check for ROOTFS: in the log
-  selftest: runqemu: use better error message when asserts fail
-  shadow: Fix can not print full login timeout message
-  staging/multilib: Fix manifest corruption
-  staging: Separate out different multiconfig manifests
-  sudo: upgrade to 1.9.13p3
-  systemd.bbclass: Add /usr/lib/systemd to searchpaths as well
-  systemd: add group sgx to udev package
-  systemd: fix wrong nobody-group assignment
-  timezone: use 'tz' subdir instead of ${WORKDIR} directly
-  toolchain-scripts: Handle spaces within user $PATH
-  tzcode-native: fix build with gcc-13 on host
-  tzdata: upgrade to 2023c
-  tzdata: use separate :term:`B` instead of :term:`WORKDIR` for zic output
-  u-boot: Map arm64 into map for u-boot dts installation
-  uninative: Upgrade to 3.9 to include glibc 2.37
-  vala: Fix install conflict when enable multilib.
-  vim: add missing pkgconfig inherit
-  vim: set modified-by to the recipe :term:`MAINTAINER`
-  vim: upgrade to 9.0.1429
-  xcb-proto: Fix install conflict when enable multilib.


Known Issues in Yocto-4.1.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.1.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alexander Kanavin
-  Andrew Geissler
-  Arturo Buzarra
-  Bhabu Bindu
-  Bruce Ashfield
-  Carlos Alberto Lopez Perez
-  Chee Yang Lee
-  Chris Elledge
-  Christoph Lauer
-  Dmitry Baryshkov
-  Enrico Jörns
-  Fawzi KHABER
-  Frank de Brabander
-  Frederic Martinsons
-  Geoffrey GIRY
-  Hitendra Prajapati
-  Jose Quaresma
-  Kenfe-Mickael Laventure
-  Khem Raj
-  Marek Vasut
-  Martin Jansa
-  Michael Halstead
-  Michael Opdenacker
-  Mikko Rapeli
-  Ming Liu
-  Mingli Yu
-  Narpat Mali
-  Pavel Zhukov
-  Peter Marko
-  Piotr Łobacz
-  Randy MacLeod
-  Richard Purdie
-  Robert Yang
-  Romuald JEANNE
-  Romuald Jeanne
-  Ross Burton
-  Siddharth
-  Siddharth Doshi
-  Soumya
-  Steve Sakoman
-  Sudip Mukherjee
-  Tim Orling
-  Tobias Hagelborn
-  Tom Hochstein
-  Trevor Woerner
-  Wang Mingyu
-  Xiangyu Chen
-  Zoltan Boszormenyi


Repositories / Downloads for Yocto-4.1.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`langdale </poky/log/?h=langdale>`
-  Tag:  :yocto_git:`yocto-4.1.4 </poky/log/?h=yocto-4.1.4>`
-  Git Revision: :yocto_git:`3e95f268ce04b49ba6731fd4bbc53b1693c21963 </poky/commit/?id=3e95f268ce04b49ba6731fd4bbc53b1693c21963>`
-  Release Artefact: poky-3e95f268ce04b49ba6731fd4bbc53b1693c21963
-  sha: 54798c4b519f5e11f409e1fd074bea1bc0a1b80672aa60dddbac772c8e4d838b
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.1.4/poky-3e95f268ce04b49ba6731fd4bbc53b1693c21963.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.1.4/poky-3e95f268ce04b49ba6731fd4bbc53b1693c21963.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`langdale </openembedded-core/log/?h=langdale>`
-  Tag:  :oe_git:`yocto-4.1.4 </openembedded-core/log/?h=yocto-4.1.4>`
-  Git Revision: :oe_git:`78211cda40eb018a3aa535c75b61e87337236628 </openembedded-core/commit/?id=78211cda40eb018a3aa535c75b61e87337236628>`
-  Release Artefact: oecore-78211cda40eb018a3aa535c75b61e87337236628
-  sha: 1303d836bae54c438c64d6b9f068eb91c32be4cc1779e89d0f2d915a55d59b15
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.1.4/oecore-78211cda40eb018a3aa535c75b61e87337236628.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.1.4/oecore-78211cda40eb018a3aa535c75b61e87337236628.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`langdale </meta-mingw/log/?h=langdale>`
-  Tag:  :yocto_git:`yocto-4.1.4 </meta-mingw/log/?h=yocto-4.1.4>`
-  Git Revision: :yocto_git:`b0067202db8573df3d23d199f82987cebe1bee2c </meta-mingw/commit/?id=b0067202db8573df3d23d199f82987cebe1bee2c>`
-  Release Artefact: meta-mingw-b0067202db8573df3d23d199f82987cebe1bee2c
-  sha: 704f2940322b81ce774e9cbd27c3cfa843111d497dc7b1eeaa39cd694d9a2366
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.1.4/meta-mingw-b0067202db8573df3d23d199f82987cebe1bee2c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.1.4/meta-mingw-b0067202db8573df3d23d199f82987cebe1bee2c.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.2 </bitbake/log/?h=2.2>`
-  Tag:  :oe_git:`yocto-4.1.4 </bitbake/log/?h=yocto-4.1.4>`
-  Git Revision: :oe_git:`5b105e76dd7de3b9a25b17b397f2c12c80048894 </bitbake/commit/?id=5b105e76dd7de3b9a25b17b397f2c12c80048894>`
-  Release Artefact: bitbake-5b105e76dd7de3b9a25b17b397f2c12c80048894
-  sha: 2cd6448138816f5a906f9927c6b6fdc5cf24981ef32b6402312f52ca490edb4f
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.1.4/bitbake-5b105e76dd7de3b9a25b17b397f2c12c80048894.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.1.4/bitbake-5b105e76dd7de3b9a25b17b397f2c12c80048894.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`langdale </yocto-docs/log/?h=langdale>`
-  Tag: :yocto_git:`yocto-4.1.4 </yocto-docs/log/?h=yocto-4.1.4>`
-  Git Revision: :yocto_git:`da685fc5e69d49728e3ffd6c4d623e7e1745059d </yocto-docs/commit/?id=da685fc5e69d49728e3ffd6c4d623e7e1745059d>`

