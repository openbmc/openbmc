.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.0.7 (Kirkstone)
-----------------------------------------

Security Fixes in Yocto-4.0.7
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve:`2022-4285`
-  curl: Fix :cve:`2022-43551` and :cve_mitre:`2022-43552`
-  ffmpeg: Fix :cve:`2022-3109` and :cve:`2022-3341`
-  go: Fix :cve:`2022-41715` and :cve:`2022-41717`
-  libX11: Fix :cve:`2022-3554` and :cve:`2022-3555`
-  libarchive: Fix :cve:`2022-36227`
-  libksba: Fix :cve:`2022-47629`
-  libpng: Fix :cve:`2019-6129`
-  libxml2: Fix :cve:`2022-40303` and :cve:`2022-40304`
-  openssl: Fix :cve:`2022-3996`
-  python3: Fix :cve:`2022-45061`
-  python3-git: Fix :cve:`2022-24439`
-  python3-setuptools: Fix :cve:`2022-40897`
-  python3-wheel: Fix :cve:`2022-40898`
-  qemu: Fix :cve:`2022-4144`
-  sqlite: Fix :cve:`2022-46908`
-  systemd: Fix :cve:`2022-45873`
-  vim: Fix :cve:`2023-0049`, :cve:`2023-0051`, :cve:`2023-0054` and :cve:`2023-0088`
-  webkitgtk: Fix :cve:`2022-32886`, :cve_mitre:`2022-32891`


Fixes in Yocto-4.0.7
~~~~~~~~~~~~~~~~~~~~

-  Revert "gstreamer1.0: disable flaky gstbin:test_watch_for_state_change test"
-  at: Change when files are copied
-  baremetal-image: Avoid overriding qemu variables from IMAGE_CLASSES
-  base.bbclass: Fix way to check ccache path
-  bc: extend to nativesdk
-  bind: upgrade to 9.18.10
-  busybox: always start do_compile with orig config files
-  busybox: rm temporary files if do_compile was interrupted
-  cairo: fix CVE patches assigned wrong CVE number
-  cairo: update patch for :cve:`2019-6461` with upstream solution
-  classes/create-spdx: Add SPDX_PRETTY option
-  classes: image: Set empty weak default IMAGE_LINGUAS
-  combo-layer: add sync-revs command
-  combo-layer: dont use bb.utils.rename
-  combo-layer: remove unused import
-  curl: Correct LICENSE from MIT-open-group to curl
-  cve-check: write the cve manifest to IMGDEPLOYDIR
-  cve-update-db-native: avoid incomplete updates
-  cve-update-db-native: show IP on failure
-  dbus: Add missing CVE product name
-  devtool/upgrade: correctly handle recipes where S is a subdir of upstream tree
-  devtool: process local files only for the main branch
-  dhcpcd: backport two patches to fix runtime error
-  docs: kernel-dev: faq: update tip on how to not include kernel in image
-  docs: migration-4.0: specify variable name change for kernel inclusion in image recipe
-  efibootmgr: update compilation with musl
-  externalsrc: fix lookup for .gitmodules
-  ffmpeg: refresh patches to apply cleanly
-  freetype:update mirror site.
-  gcc: Refactor linker patches and fix linker on arm with usrmerge
-  glibc: stable 2.35 branch updates.
-  go-crosssdk: avoid host contamination by GOCACHE
-  gstreamer1.0: Fix race conditions in gstbin tests
-  gstreamer1.0: upgrade to 1.20.5
-  gtk-icon-cache: Fix GTKIC_CMD if-else condition
-  harfbuzz: remove bindir only if it exists
-  kernel-fitimage: Adjust order of dtb/dtbo files
-  kernel-fitimage: Allow user to select dtb when multiple dtb exists
-  kernel.bbclass: remove empty module directories to prevent QA issues
-  lib/buildstats: fix parsing of trees with reduced_proc_pressure directories
-  lib/oe/reproducible: Use git log without gpg signature
-  libepoxy: remove upstreamed patch
-  libnewt: update 0.52.21 -> 0.52.23
-  libseccomp: fix typo in DESCRIPTION
-  libxcrypt-compat: upgrade 4.4.30 -> 4.4.33
-  libxml2: fix test data checksums
-  linux-firmware: upgrade 20221109 -> 20221214
-  linux-yocto/5.10: update to v5.10.152
-  linux-yocto/5.10: update to v5.10.154
-  linux-yocto/5.10: update to v5.10.160
-  linux-yocto/5.15: fix perf build with clang
-  linux-yocto/5.15: libbpf: Fix build warning on ref_ctr_off
-  linux-yocto/5.15: ltp and squashfs fixes
-  linux-yocto/5.15: powerpc: Fix reschedule bug in KUAP-unlocked user copy
-  linux-yocto/5.15: update to v5.15.84
-  lsof: add update-alternatives logic
-  lttng-modules: update 2.13.7 -> 2.13.8
-  manuals: add 4.0.5 and 4.0.6 release notes
-  manuals: document SPDX_PRETTY variable
-  mpfr: upgrade 4.1.0 -> 4.1.1
-  oeqa/concurrencytest: Add number of failures to summary output
-  oeqa/rpm.py: Increase timeout and add debug output
-  oeqa/selftest/externalsrc: add test for srctree_hash_files
-  openssh: remove RRECOMMENDS to rng-tools for sshd package
-  poky.conf: bump version for 4.0.7
-  qemuboot.bbclass: make sure runqemu boots bundled initramfs kernel image
-  rm_work.bbclass: use HOSTTOOLS 'rm' binary exclusively
-  rm_work: adjust dependency to make do_rm_work_all depend on do_rm_work
-  ruby: merge .inc into .bb
-  ruby: update 3.1.2 -> 3.1.3
-  selftest/virgl: use pkg-config from the host
-  tiff: Add packageconfig knob for webp
-  toolchain-scripts: compatibility with unbound variable protection
-  tzdata: update 2022d -> 2022g
-  valgrind: skip the boost_thread test on arm
-  xserver-xorg: upgrade 21.1.4 -> 21.1.6
-  xwayland: libxshmfence is needed when dri3 is enabled
-  xwayland: upgrade 22.1.5 -> 22.1.7
-  yocto-check-layer: Allow OE-Core to be tested


Known Issues in Yocto-4.0.7
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.0.7
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alejandro Hernandez Samaniego
-  Alex Kiernan
-  Alex Stewart
-  Alexander Kanavin
-  Antonin Godard
-  Benoît Mauduit
-  Bhabu Bindu
-  Bruce Ashfield
-  Carlos Alberto Lopez Perez
-  Changqing Li
-  Chen Qi
-  Daniel Gomez
-  Florin Diaconescu
-  He Zhe
-  Hitendra Prajapati
-  Jagadeesh Krishnanjanappa
-  Jan Kircher
-  Jermain Horsman
-  Jose Quaresma
-  Joshua Watt
-  KARN JYE LAU
-  Kai Kang
-  Khem Raj
-  Luis
-  Marta Rybczynska
-  Martin Jansa
-  Mathieu Dubois-Briand
-  Michael Opdenacker
-  Narpat Mali
-  Ovidiu Panait
-  Pavel Zhukov
-  Peter Marko
-  Petr Kubizňák
-  Quentin Schulz
-  Randy MacLeod
-  Ranjitsinh Rathod
-  Richard Purdie
-  Robert Andersson
-  Ross Burton
-  Sandeep Gundlupet Raju
-  Saul Wold
-  Steve Sakoman
-  Vivek Kumbhar
-  Wang Mingyu
-  Xiangyu Chen
-  Yash Shinde
-  Yogita Urade


Repositories / Downloads for Yocto-4.0.7
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`kirkstone </poky/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.7 </poky/log/?h=yocto-4.0.7>`
-  Git Revision: :yocto_git:`65dafea22018052fe7b2e17e6e4d7eb754224d38 </poky/commit/?id=65dafea22018052fe7b2e17e6e4d7eb754224d38>`
-  Release Artefact: poky-65dafea22018052fe7b2e17e6e4d7eb754224d38
-  sha: 6b1b67600b84503e2d5d29bcd6038547339f4f9413b830cd2408df825eda642d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.7/poky-65dafea22018052fe7b2e17e6e4d7eb754224d38.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.7/poky-65dafea22018052fe7b2e17e6e4d7eb754224d38.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`kirkstone </openembedded-core/log/?h=kirkstone>`
-  Tag:  :oe_git:`yocto-4.0.7 </openembedded-core/log/?h=yocto-4.0.7>`
-  Git Revision: :oe_git:`a8c82902384f7430519a31732a4bb631f21693ac </openembedded-core/commit/?id=a8c82902384f7430519a31732a4bb631f21693ac>`
-  Release Artefact: oecore-a8c82902384f7430519a31732a4bb631f21693ac
-  sha: 6f2dbc4ea1e388620ef77ac3a7bbb2b5956bb8bf9349b0c16cd7610e9996f5ea
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.7/oecore-a8c82902384f7430519a31732a4bb631f21693ac.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.7/oecore-a8c82902384f7430519a31732a4bb631f21693ac.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`kirkstone </meta-mingw/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.7 </meta-mingw/log/?h=yocto-4.0.7>`
-  Git Revision: :yocto_git:`a90614a6498c3345704e9611f2842eb933dc51c1 </meta-mingw/commit/?id=a90614a6498c3345704e9611f2842eb933dc51c1>`
-  Release Artefact: meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1
-  sha: 49f9900bfbbc1c68136f8115b314e95d0b7f6be75edf36a75d9bcd1cca7c6302
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.7/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.7/meta-mingw-a90614a6498c3345704e9611f2842eb933dc51c1.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`kirkstone </meta-gplv2/log/?h=kirkstone>`
-  Tag:  :yocto_git:`yocto-4.0.7 </meta-gplv2/log/?h=yocto-4.0.7>`
-  Git Revision: :yocto_git:`d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a </meta-gplv2/commit/?id=d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a>`
-  Release Artefact: meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a
-  sha: c386f59f8a672747dc3d0be1d4234b6039273d0e57933eb87caa20f56b9cca6d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.7/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.7/meta-gplv2-d2f8b5cdb285b72a4ed93450f6703ca27aa42e8a.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.0 </bitbake/log/?h=2.0>`
-  Tag:  :oe_git:`yocto-4.0.7 </bitbake/log/?h=yocto-4.0.7>`
-  Git Revision: :oe_git:`7e268c107bb0240d583d2c34e24a71e373382509 </bitbake/commit/?id=7e268c107bb0240d583d2c34e24a71e373382509>`
-  Release Artefact: bitbake-7e268c107bb0240d583d2c34e24a71e373382509
-  sha: c3e2899012358c95962c7a5c85cf98dc30c58eae0861c374124e96d9556bb901
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.0.7/bitbake-7e268c107bb0240d583d2c34e24a71e373382509.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.0.7/bitbake-7e268c107bb0240d583d2c34e24a71e373382509.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`kirkstone </yocto-docs/log/?h=kirkstone>`
-  Tag: :yocto_git:`yocto-4.0.7 </yocto-docs/log/?h=yocto-4.0.7>`
-  Git Revision: :yocto_git:`5883e897c34f25401b358a597fb6e18d80f7f90b </yocto-docs/commit/?id=5883e897c34f25401b358a597fb6e18d80f7f90b>`


