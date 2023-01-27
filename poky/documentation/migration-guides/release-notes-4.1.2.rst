.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.1.2 (Langdale)
----------------------------------------

Security Fixes in Yocto-4.1.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  sudo: Fix :cve:`2022-43995`
-  binutils: Fix :cve:`2022-4285`
-  cairo: update patch for :cve:`2019-6461` with upstream solution
-  expat: Fix :cve:`2022-43680`
-  ffmpeg: Fix :cve:`2022-3964` and :cve:`2022-3965`
-  grub: Fix :cve:`2022-28736`
-  libarchive: Fix :cve:`2022-36227`
-  libpam: Fix :cve:`2022-28321`
-  libpng: Fix :cve:`2019-6129`
-  ruby: Fix :cve:`2022-28738` and :cve:`2022-28739`
-  tiff: Fix :cve:`2022-3970`
-  vim: Fix :cve:`2022-4141`


Fixes in Yocto-4.1.2
~~~~~~~~~~~~~~~~~~~~

-  Expand create-spdx class documentation
-  Expand cve-check class documentation
-  archiver: avoid using machine variable as it breaks multiconfig
-  babeltrace: Upgrade to 1.5.11
-  backport SPDX documentation and vulnerability improvements
-  baremetal-image: Avoid overriding qemu variables from IMAGE_CLASSES
-  bc: extend to nativesdk
-  bind: Upgrade to 9.18.9
-  bitbake.conf: Drop export of SOURCE_DATE_EPOCH_FALLBACK
-  bitbake: gitsm: Fix regression in gitsm submodule path parsing
-  bitbake: runqueue: Fix race issues around hash equivalence and sstate reuse
-  bluez5: Point hciattach bcm43xx firmware search path to /lib/firmware
-  build-appliance-image: Update to langdale head revision
-  cargo_common.bbclass: Fix typos
-  classes: make TOOLCHAIN more permissive for kernel
-  cmake: Upgrade to 3.24.2
-  combo-layer: add sync-revs command
-  combo-layer: dont use bb.utils.rename
-  combo-layer: remove unused import
-  common-tasks.rst: fix oeqa runtime test path
-  create-spdx: default share_src for shared sources
-  curl: Correct LICENSE from MIT-open-group to curl
-  dbus: Add missing CVE product name
-  devtool/upgrade: correctly handle recipes where S is a subdir of upstream tree
-  dhcpcd: fix to work with systemd
-  docs: kernel-dev: faq: update tip on how to not include kernel in image
-  docs: migration-4.0: specify variable name change for kernel inclusion in image recipe
-  expat: upgrade to 2.5.0
-  externalsrc: fix lookup for .gitmodules
-  ffmpeg: Upgrade to 5.1.2
-  gcc-shared-source: Fix source date epoch handling
-  gcc-source: Drop gengtype manipulation
-  gcc-source: Ensure deploy_source_date_epoch sstate hash doesn't change
-  gcc-source: Fix gengtypes race
-  gdk-pixbuf: Upgrade to 2.42.10
-  get_module_deps3.py: Check attribute '__file__'
-  glibc-tests: correctly pull in the actual tests when installing -ptest package
-  gnomebase.bbclass: return the whole version for tarball directory if it is a number
-  go-crosssdk: avoid host contamination by GOCACHE
-  go: Update reproducibility patch to fix panic errors
-  go: submit patch upstream
-  go: Upgrade to 1.19.3
-  gptfdisk: remove warning message from target system
-  groff: submit patches upstream
-  gstreamer1.0: Upgrade to 1.20.5
-  help2man: Upgrade to 1.49.3
-  insane: add codeload.github.com to src-uri-bad checkz
-  inetutils: Upgrade to 2.4
-  iso-codes: Upgrade to 4.12.0
-  kbd: Don't build tests
-  kea: submit patch upstream
-  kern-tools: integrate ZFS speedup patch
-  kernel.bbclass: Include randstruct seed assets in STAGING_KERNEL_BUILDDIR
-  kernel.bbclass: make KERNEL_DEBUG_TIMESTAMPS work at rebuild
-  kernel.bbclass: remove empty module directories to prevent QA issues
-  lib/buildstats: fix parsing of trees with reduced_proc_pressure directories
-  libdrm: Remove libdrm-kms package
-  libepoxy: convert to git
-  libepoxy: remove upstreamed patch
-  libepoxy: Upgrade to 1.5.10
-  libffi: submit patch upstream
-  libffi: Upgrade to 3.4.4
-  libical: Upgrade to 3.0.16
-  libnewt: Upgrade to 0.52.23
-  libsdl2: Upgrade to 2.24.2
-  libpng: Upgrade to 1.6.39
-  libuv: fixup SRC_URI
-  libxcrypt-compat: Upgrade to 4.4.33
-  libxcrypt: Upgrade to 4.4.30
-  libxml2: fix test data checksums
-  linux-firmware: add new fw file to ${PN}-qcom-adreno-a530
-  linux-firmware: don't put the firmware into the sysroot
-  linux-firmware: Upgrade to 20221109
-  linux-yocto/5.15: fix CONFIG_CRYPTO_CCM mismatch warnings
-  linux-yocto/5.15: update genericx86* machines to v5.15.72
-  linux-yocto/5.15: Upgrade to v5.15.78
-  linux-yocto/5.19: cfg: intel and vesa updates
-  linux-yocto/5.19: fix CONFIG_CRYPTO_CCM mismatch warnings
-  linux-yocto/5.19: fix elfutils run-backtrace-native-core ptest failure
-  linux-yocto/5.19: security.cfg: remove configs which have been dropped
-  linux-yocto/5.19: update genericx86* machines to v5.19.14
-  linux-yocto/5.19: Upgrade to v5.19.17
-  lsof: add update-alternatives logic
-  lttng-modules: Upgrade to 2.13.7
-  lttng-tools: submit determinism.patch upstream
-  manuals: add 4.0.5 and 4.0.6 release notes
-  mesa: do not rely on native llvm-config in target sysroot
-  mesa: Upgrade to 22.2.3
-  meta-selftest/staticids: add render group for systemd
-  mirrors.bbclass: update CPAN_MIRROR
-  mobile-broadband-provider-info: Upgrade to 20221107
-  mpfr: Upgrade to 4.1.1
-  mtd-utils: Upgrade to 2.1.5
-  oeqa/concurrencytest: Add number of failures to summary output
-  oeqa/runtime/dnf: rewrite test_dnf_installroot_usrmerge
-  oeqa/selftest/externalsrc: add test for srctree_hash_files
-  oeqa/selftest/lic_checksum: Cleanup changes to emptytest include
-  openssh: remove RRECOMMENDS to rng-tools for sshd package
-  opkg: Set correct info_dir and status_file in opkg.conf
-  opkg: Upgrade to 0.6.1
-  ovmf: correct patches status
-  package: Fix handling of minidebuginfo with newer binutils
-  pango: Make it build with ptest disabled
-  pango: replace a recipe fix with an upstream submitted patch
-  pango: Upgrade to 1.50.11
-  poky.conf: bump version for 4.1.2
-  psplash: consider the situation of psplash not exist for systemd
-  python3-mako: Upgrade to 1.2.3
-  qemu-helper-native: Correctly pass program name as argv[0]
-  qemu-helper-native: Re-write bridge helper as C program
-  qemu: Ensure libpng dependency is deterministic
-  qemuboot.bbclass: make sure runqemu boots bundled initramfs kernel image
-  resolvconf: make it work
-  rm_work: adjust dependency to make do_rm_work_all depend on do_rm_work
-  rm_work: exclude the SSTATETASKS from the rm_work tasks sinature
-  ruby: merge .inc into .bb
-  ruby: Upgrade to 3.1.3
-  rust: submit a rewritten version of crossbeam_atomic.patch upstream
-  sanity: Drop data finalize call
-  scripts: convert-overrides: Allow command-line customizations
-  selftest: add a copy of previous mtd-utils version to meta-selftest
-  socat: Upgrade to 1.7.4.4
-  sstate: Allow optimisation of do_deploy_archives task dependencies
-  sstatesig: emit more helpful error message when not finding sstate manifest
-  sstatesig: skip the rm_work task signature
-  sudo: Upgrade to 1.9.12p1
-  sysstat: Upgrade to 12.6.1
-  systemd: Consider PACKAGECONFIG in RRECOMMENDS
-  systemd: Make importd depend on glib-2.0 again
-  systemd: add group render to udev package
-  systemd: Upgrade to 251.8
-  tcl: correct patch status
-  tzdata: Upgrade to 2022g
-  vala: install vapigen-wrapper into /usr/bin/crosscripts and stage only that
-  valgrind: skip the boost_thread test on arm
-  vim: Upgrade to 9.0.0947
-  wic: make ext2/3/4 images reproducible
-  xwayland: libxshmfence is needed when dri3 is enabled
-  xwayland: Upgrade to 22.1.5
-  yocto-check-layer: Allow OE-Core to be tested


Known Issues in Yocto-4.1.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.1.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alejandro Hernandez Samaniego
-  Alex Kiernan
-  Alex Stewart
-  Alexander Kanavin
-  Alexey Smirnov
-  Bruce Ashfield
-  Carlos Alberto Lopez Perez
-  Chen Qi
-  Diego Sueiro
-  Dmitry Baryshkov
-  Enrico Jörns
-  Harald Seiler
-  Hitendra Prajapati
-  Jagadeesh Krishnanjanappa
-  Jose Quaresma
-  Joshua Watt
-  Kai Kang
-  Konrad Weihmann
-  Leon Anavi
-  Marek Vasut
-  Martin Jansa
-  Mathieu Dubois-Briand
-  Michael Opdenacker
-  Mikko Rapeli
-  Narpat Mali
-  Nathan Rossi
-  Niko Mauno
-  Ola x Nilsson
-  Ovidiu Panait
-  Pavel Zhukov
-  Peter Bergin
-  Peter Kjellerstedt
-  Peter Marko
-  Polampalli, Archana
-  Qiu, Zheng
-  Quentin Schulz
-  Randy MacLeod
-  Ranjitsinh Rathod
-  Ravula Adhitya Siddartha
-  Richard Purdie
-  Robert Andersson
-  Ross Burton
-  Ryan Eatmon
-  Sakib Sajal
-  Sandeep Gundlupet Raju
-  Sergei Zhmylev
-  Steve Sakoman
-  Tim Orling
-  Wang Mingyu
-  Xiangyu Chen
-  pgowda

Repositories / Downloads for Yocto-4.1.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`langdale </poky/log/?h=langdale>`
-  Tag:  :yocto_git:`yocto-4.1.2 </poky/log/?h=yocto-4.1.2>`
-  Git Revision: :yocto_git:`74c92e38c701e268406bb656b45ccd68471c217e </poky/commit/?id=74c92e38c701e268406bb656b45ccd68471c217e>`
-  Release Artefact: poky-74c92e38c701e268406bb656b45ccd68471c217e
-  sha: 06a2b304d0e928b62d81087797ae86115efe925c506bcb40c7d4747e14790bb0
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.1.2/poky-74c92e38c701e268406bb656b45ccd68471c217e.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.1.2/poky-74c92e38c701e268406bb656b45ccd68471c217e.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`langdale </openembedded-core/log/?h=langdale>`
-  Tag:  :oe_git:`yocto-4.1.2 </openembedded-core/log/?h=yocto-4.1.2>`
-  Git Revision: :oe_git:`670f4f103b25897524d115c1f290ecae441fe4bd </openembedded-core/commit/?id=670f4f103b25897524d115c1f290ecae441fe4bd>`
-  Release Artefact: oecore-670f4f103b25897524d115c1f290ecae441fe4bd
-  sha: 09d77700e84efc738aef5713c5e86f19fa092f876d44b870789155cc1625ef04
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.1.2/oecore-670f4f103b25897524d115c1f290ecae441fe4bd.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.1.2/oecore-670f4f103b25897524d115c1f290ecae441fe4bd.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`langdale </meta-mingw/log/?h=langdale>`
-  Tag:  :yocto_git:`yocto-4.1.2 </meta-mingw/log/?h=yocto-4.1.2>`
-  Git Revision: :yocto_git:`b0067202db8573df3d23d199f82987cebe1bee2c </meta-mingw/commit/?id=b0067202db8573df3d23d199f82987cebe1bee2c>`
-  Release Artefact: meta-mingw-b0067202db8573df3d23d199f82987cebe1bee2c
-  sha: 704f2940322b81ce774e9cbd27c3cfa843111d497dc7b1eeaa39cd694d9a2366
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.1.2/meta-mingw-b0067202db8573df3d23d199f82987cebe1bee2c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.1.2/meta-mingw-b0067202db8573df3d23d199f82987cebe1bee2c.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.2 </bitbake/log/?h=2.2>`
-  Tag:  :oe_git:`yocto-4.1.2 </bitbake/log/?h=yocto-4.1.2>`
-  Git Revision: :oe_git:`f0f166aee766b4bb1f8cf8b35dfc7d406c75e6a4 </bitbake/commit/?id=f0f166aee766b4bb1f8cf8b35dfc7d406c75e6a4>`
-  Release Artefact: bitbake-f0f166aee766b4bb1f8cf8b35dfc7d406c75e6a4
-  sha: 7faf97eca78afd3994e4e126e5f5908617408c340c6eff8cd7047e0b961e2d10
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.1.2/bitbake-f0f166aee766b4bb1f8cf8b35dfc7d406c75e6a4.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.1.2/bitbake-f0f166aee766b4bb1f8cf8b35dfc7d406c75e6a4.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`langdale </yocto-docs/log/?h=langdale>`
-  Tag: :yocto_git:`yocto-4.1.2 </yocto-docs/log/?h=yocto-4.1.2>`
-  Git Revision: :yocto_git:`30f5f9ece260fd600f0c0fa32fc2f1fc61cf7d1b </yocto-docs/commit/?id=30f5f9ece260fd600f0c0fa32fc2f1fc61cf7d1b>`

