.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.9 (Kirkstone)
-----------------------------------------

Security Fixes in Yocto-4.0.9
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2023-22608`
-  curl: Fix :cve_nist:`2023-23914`, :cve_nist:`2023-23915` and :cve_nist:`2023-23916`
-  epiphany: Fix :cve_nist:`2023-26081`
-  git: Ignore :cve_nist:`2023-22743`
-  glibc: Fix  :cve_nist:`2023-0687`
-  gnutls: Fix :cve_nist:`2023-0361`
-  go: Fix :cve_nist:`2022-2879`, :cve_nist:`2022-41720` and :cve_nist:`2022-41723`
-  harfbuzz: Fix :cve_nist:`2023-25193`
-  less: Fix :cve_nist:`2022-46663`
-  libmicrohttpd: Fix :cve_nist:`2023-27371`
-  libsdl2: Fix :cve_nist:`2022-4743`
-  openssl: Fix :cve_nist:`2022-3996`, :cve_nist:`2023-0464`, :cve_nist:`2023-0465` and :cve_nist:`2023-0466`
-  pkgconf: Fix :cve_nist:`2023-24056`
-  python3: Fix :cve_nist:`2023-24329`
-  shadow: Ignore :cve_nist:`2016-15024`
-  systemd: Fix :cve_nist:`2022-4415`
-  tiff: Fix :cve_nist:`2023-0800`, :cve_nist:`2023-0801`, :cve_nist:`2023-0802`, :cve_nist:`2023-0803` and :cve_nist:`2023-0804`
-  vim: Fix :cve_nist:`2023-0433`, :cve_nist:`2023-0512`, :cve_nist:`2023-1127`, :cve_nist:`2023-1170`, :cve_nist:`2023-1175`, :cve_nist:`2023-1264` and :cve_nist:`2023-1355`
-  xserver-xorg: Fix :cve_nist:`2023-0494`
-  xwayland: Fix :cve_nist:`2023-0494`


Fixes in Yocto-4.0.9
~~~~~~~~~~~~~~~~~~~~

-  base-files: Drop localhost.localdomain from hosts file
-  binutils: Fix nativesdk ld.so search
-  bitbake: cookerdata: Drop dubious exception handling code
-  bitbake: cookerdata: Improve early exception handling
-  bitbake: cookerdata: Remove incorrect SystemExit usage
-  bitbake: fetch/git: Fix local clone url to make it work with repo
-  bitbake: utils: Allow to_boolean to support int values
-  bmap-tools: switch to main branch
-  buildtools-tarball: Handle spaces within user $PATH
-  busybox: Fix depmod patch
-  cracklib: update github branch to 'main'
-  cups: add/fix web interface packaging
-  cups: check PACKAGECONFIG for pam feature
-  cups: use BUILDROOT instead of DESTDIR
-  curl: fix dependencies when building with ldap/ldaps
-  cve-check: Fix false negative version issue
-  dbus: upgrade to 1.14.6
-  devtool/upgrade: do not delete the workspace/recipes directory
-  dhcpcd: Fix install conflict when enable multilib.
-  dhcpcd: fix dhcpcd start failure on qemuppc64
-  gcc-shared-source: do not use ${S}/.. in deploy_source_date_epoch
-  glibc: Add missing binutils dependency
-  image_types: fix multiubi var init
-  iso-codes: upgrade to 4.13.0
-  json-c: Add ptest for json-c
-  kernel-yocto: fix kernel-meta data detection
-  lib/buildstats: handle tasks that never finished
-  lib/resulttool: fix typo breaking resulttool log --ptest
-  libjpeg-turbo: upgrade to 2.1.5.1
-  libmicrohttpd: upgrade to 0.9.76
-  libseccomp: fix for the ptest result format
-  libssh2: Clean up ptest patch/coverage
-  linux-firmware: add yamato fw files to qcom-adreno-a2xx package
-  linux-firmware: properly set license for all Qualcomm firmware
-  linux-firmware: upgrade to 20230210
-  linux-yocto-rt/5.15: update to -rt59
-  linux-yocto/5.10: upgrade to v5.10.175
-  linux-yocto/5.15: upgrade to v5.15.103
-  linux: inherit pkgconfig in kernel.bbclass
-  lttng-modules: fix for kernel 6.2+
-  lttng-modules: upgrade to v2.13.9
-  lua: Fix install conflict when enable multilib.
-  mdadm: Fix raid0, 06wrmostly and 02lineargrow tests
-  meson: Fix wrapper handling of implicit setup command
-  migration-guides: add 4.0.8 release notes
-  nghttp2: never build python bindings
-  oeqa rtc.py: skip if read-only-rootfs
-  oeqa ssh.py: fix hangs in run()
-  oeqa/sdk: Improve Meson test
-  oeqa/selftest/prservice: Improve debug output for failure
-  oeqa/selftest/resulttooltests: fix minor typo
-  openssl: upgrade to 3.0.8
-  package.bbclase: Add check for /build in copydebugsources()
-  patchelf: replace a rejected patch with an equivalent uninative.bbclass tweak
-  poky.conf: bump version for 4.0.9
-  populate_sdk_ext: Handle spaces within user $PATH
-  pybootchartui: Fix python syntax issue
-  python3-git: fix indent error
-  python3-setuptools-rust-native: Add direct dependency of native python3 modules
-  qemu: Revert "fix :cve_nist:`2021-3507`" as not applicable for qemu 6.2
-  rsync: Add missing prototypes to function declarations
-  rsync: Turn on -pedantic-errors at the end of 'configure'
-  runqemu: kill qemu if it hangs
-  scripts/lib/buildstats: handle top-level build_stats not being complete
-  selftest/recipetool: Stop test corrupting tinfoil class
-  selftest/runtime_test/virgl: Disable for all Rocky Linux
-  selftest: devtool: set BB_HASHSERVE_UPSTREAM when setting SSTATE_MIRROR
-  sstatesig: Improve output hash calculation
-  staging/multilib: Fix manifest corruption
-  staging: Separate out different multiconfig manifests
-  sudo: update 1.9.12p2 -> 1.9.13p3
-  systemd.bbclass: Add /usr/lib/systemd to searchpaths as well
-  systemd: add group sgx to udev package
-  systemd: fix wrong nobody-group assignment
-  timezone: use 'tz' subdir instead of ${WORKDIR} directly
-  toolchain-scripts: Handle spaces within user $PATH
-  tzcode-native: fix build with gcc-13 on host
-  tzdata: use separate B instead of WORKDIR for zic output
-  uninative: upgrade to 3.9 to include libgcc and glibc 2.37
-  vala: Fix install conflict when enable multilib.
-  vim: add missing pkgconfig inherit
-  vim: set modified-by to the recipe MAINTAINER
-  vim: upgrade to 9.0.1429
-  wic: Fix usage of fstype=none in wic
-  wireless-regdb: upgrade to 2023.02.13
-  xserver-xorg: upgrade to 21.1.7
-  xwayland: upgrade to 22.1.8


Known Issues in Yocto-4.0.9
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.9
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alexander Kanavin
-  Alexis Lothoré
-  Bruce Ashfield
-  Changqing Li
-  Chee Yang Lee
-  Dmitry Baryshkov
-  Federico Pellegrin
-  Geoffrey GIRY
-  Hitendra Prajapati
-  Hongxu Jia
-  Joe Slater
-  Kai Kang
-  Kenfe-Mickael Laventure
-  Khem Raj
-  Martin Jansa
-  Mateusz Marciniec
-  Michael Halstead
-  Michael Opdenacker
-  Mikko Rapeli
-  Ming Liu
-  Mingli Yu
-  Narpat Mali
-  Pavel Zhukov
-  Pawan Badganchi
-  Peter Marko
-  Piotr Łobacz
-  Poonam Jadhav
-  Randy MacLeod
-  Richard Purdie
-  Robert Yang
-  Romuald Jeanne
-  Ross Burton
-  Sakib Sajal
-  Saul Wold
-  Shubham Kulkarni
-  Siddharth Doshi
-  Simone Weiss
-  Steve Sakoman
-  Tim Orling
-  Tom Hochstein
-  Trevor Woerner
-  Ulrich Ölmann
-  Vivek Kumbhar
-  Wang Mingyu
-  Xiangyu Chen
-  Yash Shinde


Repositories / Downloads for Yocto-4.0.9
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.9 </poky/log/?h=yocto-4.0.9>`
-  Git Revision: :yocto_git:`09def309f91929f47c6cce386016ccb777bd2cfc </poky/commit/?id=09def309f91929f47c6cce386016ccb777bd2cfc>`
-  Release Artefact: poky-09def309f91929f47c6cce386016ccb777bd2cfc
-  sha: 5c7ce209c8a6b37ec2898e5ca21858234d91999c11fa862880ba98e8bde62f63
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.9/poky-09def309f91929f47c6cce386016ccb777bd2cfc.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.9/poky-09def309f91929f47c6cce386016ccb777bd2cfc.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.9 </openembedded-core/log/?h=yocto-4.0.9>`
-  Git Revision: :oe_git:`ff4b57ffff903a93b710284c7c7f916ddd74712f </openembedded-core/commit/?id=ff4b57ffff903a93b710284c7c7f916ddd74712f>`
-  Release Artefact: oecore-ff4b57ffff903a93b710284c7c7f916ddd74712f
-  sha: 726778ffc291136db1704316b196de979f68df9f96476b785e1791957fbb66b3
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.9/oecore-ff4b57ffff903a93b710284c7c7f916ddd74712f.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.9/oecore-ff4b57ffff903a93b710284c7c7f916ddd74712f.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.9 </meta-mingw/log/?h=yocto-4.0.9>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.9/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.9/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.9 </meta-gplv2/log/?h=yocto-4.0.9>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.9/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.9/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.9 </bitbake/log/?h=yocto-4.0.9>`
-  Git Revision: :oe_git:`2802adb572eb73a3eb2725a74a9bbdaafc543fa7 </bitbake/commit/?id=2802adb572eb73a3eb2725a74a9bbdaafc543fa7>`
-  Release Artefact: bitbake-2802adb572eb73a3eb2725a74a9bbdaafc543fa7
-  sha: 5c6e713b5e26b3835c0773095c7a1bc1f8affa28316b33597220ed86f1f1b643
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.9/bitbake-2802adb572eb73a3eb2725a74a9bbdaafc543fa7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.9/bitbake-2802adb572eb73a3eb2725a74a9bbdaafc543fa7.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.9 </yocto-docs/log/?h=yocto-4.0.9>`
-  Git Revision: :yocto_git:`86d0b38a97941ad52b1af220c7b801a399d50e93 </yocto-docs/commit/?id=86d0b38a97941ad52b1af220c7b801a399d50e93>`

