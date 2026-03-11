Release notes for Yocto-5.0.5 (Scarthgap)
-----------------------------------------

Security Fixes in Yocto-5.0.5
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  ``cups``: Fix :cve_nist:`2024-47175`
-  ``curl``: Fix :cve_nist:`2024-8096`
-  ``gnupg``: Ignore :cve_nist:`2022-3219` (wont-fix)
-  ``libarchive``: Fix :cve_nist:`2024-48957` and :cve_nist:`2024-48958`
-  ``openssh``: Ignore :cve_nist:`2023-51767` (wont-fix)
-  ``openssl``: Fix :cve_nist:`2024-9143`
-  ``ruby``: Fix :cve_nist:`2024-41123` and :cve_mitre:`2024-41496`
-  ``rust-llvm``: Fix :cve_nist:`2024-0151`
-  ``rust``, ``libstd-rs``: Ignore :cve_nist:`2024-43402`
-  ``wpa-supplicant``: Patch SAE H2E and incomplete downgrade protection for group negotiation
-  ``wpa-supplicant``: Fix :cve_nist:`2024-3596`
-  ``wpa-supplicant``: Ignore :cve_nist:`2024-5290`


Fixes in Yocto-5.0.5
~~~~~~~~~~~~~~~~~~~~

-  binutils: stable 2.42 branch updates
-  bitbake.conf: Add truncate to :term:`HOSTTOOLS`
-  bitbake: asyncrpc: Use client timeout for websocket open timeout
-  bitbake: bitbake: doc/user-manual: Update the :term:`BB_HASHSERVE_UPSTREAM`
-  bitbake: gitsm: Add call_process_submodules() to remove duplicated code
-  bitbake: gitsm: Remove downloads/tmpdir when failed
-  bitbake: tests/fetch: Use our own mirror of mobile-broadband-provider to decouple from gnome gitlab
-  bitbake: tests/fetch: Use our own mirror of sysprof to decouple from gnome gitlab
-  build-appliance-image: Update to scarthgap head revision
-  cryptodev: upgrade to 1.14
-  cve-check: add support for cvss v4.0
-  cve_check: Use a local copy of the database during builds
-  dev-manual: add bblock documentation
-  documentation: conf.py: rename :cve: role to :cve_nist:
-  documentation: README: add instruction to run Vale on a subset
-  documentation: Makefile: add SPHINXLINTDOCS to specify subset to sphinx-lint
-  e2fsprogs: removed 'sed -u' option
-  ffmpeg: Add "libswresample libavcodec" to :term:`CVE_PRODUCT`
-  glibc: stable 2.39 branch updates.
-  go: upgrade to 1.22.8
-  icu: update patch Upstream-Status
-  image.bbclass: Drop support for ImageQAFailed exceptions in image_qa
-  image_qa: fix error handling
-  install-buildtools: fix "test installation" step
-  install-buildtools: remove md5 checksum validation
-  install-buildtools: update base-url, release and installer version
-  kernel-devsrc: remove 64 bit vdso cmd files
-  kernel-fitimage: fix external dtb check
-  kernel-fitimage: fix intentation
-  lib/oe/package-manager: skip processing installed-pkgs with empty globs
-  liba52: fix do_fetch error
-  libpcre2: Update base uri PhilipHazel -> PCRE2Project
-  libsdl2: Fix non-deterministic configure option for libsamplerate
-  license: Fix directory layout issues
-  linux-firmware: upgrade to 20240909
-  linux-yocto/6.6: fix genericarm64 config warning
-  linux-yocto/6.6: upgrade to v6.6.54
-  lsb-release: fix Distro Codename shell escaping
-  makedevs: Fix issue when rootdir of / is given
-  makedevs: Fix matching uid/gid
-  meta-ide-support: Mark recipe as MACHINE-specific
-  meta-world-pkgdata: Inherit nopackages
-  migration-guide: add release notes for 4.0.21, 4.0.22 and 5.0.4
-  migration-guide: release-notes-4.0: update :term:`BB_HASHSERVE_UPSTREAM` for new infrastructure
-  migration-guide: release-notes-5.0.rst: update NO_OUTPUT -> NO_COLOR
-  orc: upgrade to 0.4.40
-  overview-manual: concepts: add details on package splitting
-  poky.conf: bump version for 5.0.5
-  populate_sdk_base: inherit nopackages
-  ptest-runner: upgrade to 2.4.5
-  pulseaudio: correct freedesktop.org -> www.freedesktop.org :term:`SRC_URI`
-  desktop-file-utils: correct freedesktop.org -> www.freedesktop.org :term:`SRC_URI`
-  python3-lxml: upgrade to v5.0.2
-  python3-setuptools: Add "python:setuptools" to :term:`CVE_PRODUCT`
-  recipes-bsp: usbutils: Fix usb-devices command using busybox
-  ref-manual: add missing CVE_CHECK manifest variables
-  ref-manual: add missing :term:`EXTERNAL_KERNEL_DEVICETREE` variable
-  ref-manual: add missing :term:`OPKGBUILDCMD` variable
-  ref-manual: add missing :term:`TESTIMAGE_FAILED_QA_ARTIFACTS`
-  ref-manual: devtool-reference: document missing commands
-  ref-manual: devtool-reference: refresh example outputs
-  ref-manual: faq: add q&a on class appends
-  ref-manual: introduce :term:`CVE_CHECK_REPORT_PATCHED` variable
-  ref-manual: merge patch-status-* to patch-status
-  ref-manual: release-process: add a reference to the doc's release
-  ref-manual: release-process: refresh the current LTS releases
-  ref-manual: release-process: update releases.svg
-  ref-manual: release-process: update releases.svg with month after "Current"
-  ref-manual: structure.rst: document missing tmp/ dirs
-  ref-manual: variables: add SIGGEN_LOCKEDSIGS* variables
-  rootfs-postcommands.bbclass: make opkg status reproducible
-  rpm: fix expansion of %_libdir in macros
-  ruby: upgrade to 3.3.5
-  runqemu: Fix detection of -serial parameter
-  runqemu: keep generating tap devices
-  scripts/install-buildtools: Update to 5.0.3
-  sqlite3: upgrade to 3.45.3
-  styles: vocabularies: Yocto: add sstate
-  systemtap: fix systemtap-native build error on Fedora 40
-  sysvinit: take release tarballs from github
-  testexport: fallback for empty :term:`IMAGE_LINK_NAME`
-  testimage: fallback for empty :term:`IMAGE_LINK_NAME`
-  uboot-sign: fix counters in do_uboot_assemble_fitimage
-  vim: upgrade to 9.1.0764
-  virglrenderer: Add patch to fix -int-conversion build issue
-  webkitgtk: upgrade to 2.44.3
-  weston: backport patch to allow neatvnc < v0.9.0
-  wpa-supplicant: Patch security advisory 2024-2
-  xserver-xorg: upgrade to 21.1.14


Known Issues in Yocto-5.0.5
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  ``oeqa/runtime``: the ``beaglebone-yocto`` target fails the parselogs runtime test due to unexpected kernel error messages in the log (see :yocto_bugs:`bug 15624 </show_bug.cgi?id=15624>` on Bugzilla).


Contributors to Yocto-5.0.5
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Aditya Tayade
-  Adrian Freihofer
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Antonin Godard
-  Anuj Mittal
-  Bruce Ashfield
-  Claus Stovgaard
-  Deepesh Varatharajan
-  Deepthi Hemraj
-  Hiago De Franco
-  Hitendra Prajapati
-  Jaeyoon Jung
-  Jiaying Song
-  Jonas Gorski
-  Jose Quaresma
-  Joshua Watt
-  Julien Stephan
-  Jörg Sommer
-  Khem Raj
-  Konrad Weihmann
-  Lee Chee Yang
-  Louis Rannou
-  Macpaul Lin
-  Martin Jansa
-  Paul Barker
-  Paul Gerber
-  Peter Kjellerstedt
-  Peter Marko
-  Purushottam Choudhary
-  Richard Purdie
-  Robert Yang
-  Rohini Sangam
-  Ross Burton
-  Sergei Zhmylev
-  Shunsuke Tokumoto
-  Steve Sakoman
-  Teresa Remmet
-  Victor Kamensky
-  Vijay Anusuri
-  Wang Mingyu
-  Yi Zhao
-  Yogita Urade
-  Zahir Hussain


Repositories / Downloads for Yocto-5.0.5
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.5 </poky/log/?h=yocto-5.0.5>`
-  Git Revision: :yocto_git:`dce4163d42f7036ea216b52b9135968d51bec4c1 </poky/commit/?id=dce4163d42f7036ea216b52b9135968d51bec4c1>`
-  Release Artefact: poky-dce4163d42f7036ea216b52b9135968d51bec4c1
-  sha: ad35a965a284490a962f6854ace536b8795f96514e14bf5c79f91f6d76ac25d3
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.5/poky-dce4163d42f7036ea216b52b9135968d51bec4c1.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.5/poky-dce4163d42f7036ea216b52b9135968d51bec4c1.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.5 </openembedded-core/log/?h=yocto-5.0.5>`
-  Git Revision: :oe_git:`a051a066da2874b95680d0353dfa18c1d56b2670 </openembedded-core/commit/?id=a051a066da2874b95680d0353dfa18c1d56b2670>`
-  Release Artefact: oecore-a051a066da2874b95680d0353dfa18c1d56b2670
-  sha: 16d252aade00161ade2692f41b2da3effeb1f41816a66db843bb1c5495125e93
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.5/oecore-a051a066da2874b95680d0353dfa18c1d56b2670.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.5/oecore-a051a066da2874b95680d0353dfa18c1d56b2670.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.5 </meta-mingw/log/?h=yocto-5.0.5>`
-  Git Revision: :yocto_git:`acbba477893ef87388effc4679b7f40ee49fc852 </meta-mingw/commit/?id=acbba477893ef87388effc4679b7f40ee49fc852>`
-  Release Artefact: meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852
-  sha: 3b7c2f475dad5130bace652b150367f587d44b391218b1364a8bbc430b48c54c
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.5/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.5/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2

bitbake

-  Repository Location: :bitbake_git:`/`
-  Branch: :bitbake_git:`2.8 </log/?h=2.8>`
-  Tag:  :bitbake_git:`yocto-5.0.5 </log/?h=yocto-5.0.5>`
-  Git Revision: :bitbake_git:`377eba2361850adfb8ce7e761ef9c76be287f88c </commit/?id=377eba2361850adfb8ce7e761ef9c76be287f88c>`
-  Release Artefact: bitbake-377eba2361850adfb8ce7e761ef9c76be287f88c
-  sha: 4a5a35098eec719bbb879706d50e552a2b709295db4055c8050ae7dda1eb2994
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.5/bitbake-377eba2361850adfb8ce7e761ef9c76be287f88c.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.5/bitbake-377eba2361850adfb8ce7e761ef9c76be287f88c.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.5 </yocto-docs/log/?h=yocto-5.0.5>`
-  Git Revision: :yocto_git:`e882cb3e5816d081eb05cb83488f286cca70e0c6 </yocto-docs/commit/?id=e882cb3e5816d081eb05cb83488f286cca70e0c6>`

