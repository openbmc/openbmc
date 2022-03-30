Release notes for 3.4.1 (honister)
----------------------------------

Known Issues in 3.4.1
~~~~~~~~~~~~~~~~~~~~~

- :yocto_bugs:`bsps-hw.bsps-hw.Test_Seek_bar_and_volume_control manual test case failure </show_bug.cgi?id=14622>`

Security Fixes in 3.4.1
~~~~~~~~~~~~~~~~~~~~~~~

-  glibc: Backport fix for :cve:`2021-43396`
-  vim: add patch number to :cve:`2021-3778` patch
-  vim: fix :cve:`2021-3796`, :cve:`2021-3872`, and :cve:`2021-3875`
-  squashfs-tools: follow-up fix for :cve:`2021-41072`
-  avahi: update CVE id fixed by local-ping.patch
-  squashfs-tools: fix :cve:`2021-41072`
-  ffmpeg: fix :cve:`2021-38114`
-  curl: fix :cve:`2021-22945`, :cve:`2021-22946` and :cve:`2021-22947`

Fixes in 3.4.1
~~~~~~~~~~~~~~

-  bitbake.conf: Fix corruption of GNOME mirror url
-  bitbake.conf: Use wayland distro feature for native builds
-  bitbake: Revert "parse/ast: Show errors for append/prepend/remove operators combined with +=/.="
-  bitbake: bitbake-worker: Add debug when unpickle fails
-  bitbake: cooker: Fix task-depends.dot for multiconfig targets
-  bitbake: cooker: Handle parse threads disappearing to avoid hangs
-  bitbake: cooker: Handle parsing results queue race
-  bitbake: cooker: Remove debug code, oops :(
-  bitbake: cooker: check if upstream hash equivalence server is available
-  bitbake: fetch/git: Handle github dropping git:// support
-  bitbake: fetch/wget: Add timeout for checkstatus calls (30s)
-  bitbake: fetch2/perforce: Fix typo
-  bitbake: fetch2: Fix url remap issue and add testcase
-  bitbake: fetch2: fix downloadfilename issue with premirror
-  bitbake: fetch: Handle mirror user/password replacements correctly
-  bitbake: parse/ast: Show errors for append/prepend/remove operators combined with +=/.=
-  bitbake: runqueue: Fix runall option handling
-  bitbake: runqueue: Fix runall option task deletion ordering issue
-  bitbake: test/fetch: Update urls to match upstream branch name changes
-  bitbake: tests/fetch.py: add test case to ensure downloadfilename is used for premirror
-  bitbake: tests/fetch.py: fix premirror test cases
-  bitbake: tests/fetch: Update github urls
-  bitbake: tests/fetch: Update pcre.org address after github changes
-  bitbake: tests/runqueue: Ensure hashserv exits before deleting files
-  bitbake: utils: Handle lockfile filenames that are too long for filesystems
-  bootchart2: Don't compile python modules
-  build-appliance-image: Update to honister head revision
-  buildhistory: Fix package output files for SDKs
-  busybox: 1.34.0 -> 1.34.1
-  ca-certificates: update 20210119 -> 20211016
-  classes/populate_sdk_base: Add setscene tasks
-  conf: update for release 3.4
-  convert-srcuri.py: use regex to check space in SRC_URI
-  create-spdx: Fix key errors in do_create_runtime_spdx
-  create-spdx: Protect against None from LICENSE_PATH
-  create-spdx: Set the Organization field via a variable
-  create-spdx: add create_annotation function
-  create-spdx: cross recipes are native also
-  create_spdx: ensure is_work_shared() is unique
-  cups: Fix missing installation of cups sysv init scripts
-  docs: poky.yaml: updates for 3.4
-  dpkg: Install dkpg-perl scripts to versioned perl directory
-  glibc-version.inc: remove branch= from GLIBC_GIT_URI
-  go-helloworld/glide: Fix urls
-  go.bbclass: Allow adding parameters to go ldflags
-  go: upgrade 1.16.7 -> 1.16.8
-  gst-devtools: 1.18.4 -> 1.18.5
-  gst-examples: 1.18.4 -> 1.18.5
-  gstreamer1.0-libav: 1.18.4 -> 1.18.5
-  gstreamer1.0-omx: 1.18.4 -> 1.18.5
-  gstreamer1.0-plugins-bad: 1.18.4 -> 1.18.5
-  gstreamer1.0-plugins-base: 1.18.4 -> 1.18.5
-  gstreamer1.0-plugins-good: 1.18.4 -> 1.18.5
-  gstreamer1.0-plugins-ugly: 1.18.4 -> 1.18.5
-  gstreamer1.0-python: 1.18.4 -> 1.18.5
-  gstreamer1.0-rtsp-server: 1.18.4 -> 1.18.5
-  gstreamer1.0-vaapi: 1.18.4 -> 1.18.5
-  gstreamer1.0: 1.18.4 -> 1.18.5
-  insane.bbclass: Add a check for directories that are expected to be empty
-  kernel-devsrc: Add vdso.lds and other build files for riscv64 as well
-  libnewt: Use python3targetconfig to fix reproducibility issue
-  libpcre/libpcre2: correct SRC_URI
-  libx11-compose-data: Update LICENSE to better reflect reality
-  libx11: Update LICENSE to better reflect reality
-  libxml2: Use python3targetconfig to fix reproducibility issue
-  linunistring: Add missing gperf-native dependency
-  linux-firmware: upgrade to 20211027
-  linux-yocto-dev: Ensure DEPENDS matches recent 5.14 kernel changes
-  linux-yocto-rt/5.10: update to -rt54
-  linux-yocto/5.10: update to v5.10.78
-  linux-yocto/5.14: common-pc: enable CONFIG_ATA_PIIX as built-in
-  linux-yocto/5.14: update to v5.14.17
-  linux-yocto: add libmpc-native to DEPENDS
-  lttng-tools: replace ad hoc ptest fixup with upstream fixes
-  manuals: releases.rst: move gatesgarth to outdated releases section
-  mesa: Enable svga for x86 only
-  mesa: upgrade 21.2.1 -> 21.2.4
-  meson.bblcass: Remove empty egg-info directories before running meson
-  meson: install native file in sdk
-  meson: move lang args to the right section
-  meson: set objcopy in the cross and native toolchain files
-  meta/scripts: Manual git url branch additions
-  meta: Add explict branch to git SRC_URIs
-  migration-3.4: add additional migration info
-  migration-3.4: add some extra packaging notes
-  migration-3.4: tweak overrides change section
-  migration: tweak introduction section
-  mirrors: Add kernel.org sources mirror for downloads.yoctoproject.org
-  mirrors: Add uninative mirror on kernel.org
-  nativesdk-packagegroup-sdk-host.bb: Update host tools for wayland
-  oeqa/runtime/parselogs: modified drm error in common errors list
-  oeqa/selftest/sstatetests: fix typo ware -> were
-  oeqa: Update cleanup code to wait for hashserv exit
-  opkg: Fix poor operator combination choice
-  ovmf: update 202105 -> 202108
-  patch.bbclass: when the patch fails show more info on the fatal error
-  poky.conf: bump version for 3.4.1 honister release
-  poky.yaml: add lz4 and zstd to essential host packages
-  poky.yaml: fix lz4 package name for older Ubuntu versions
-  pseudo: Add fcntl64 wrapper
-  python3-setuptools: _distutils/sysconfig fix
-  python3: update to 3.9.7
-  qemu.inc: Remove empty egg-info directories before running meson
-  recipes: Update github.com urls to use https
-  ref-manual: Update how to set a useradd password
-  ref-manual: document "reproducible_build" class and SOURCE_DATE_EPOCH
-  ref-manual: document BUILD_REPRODUCIBLE_BINARIES
-  ref-manual: document TOOLCHAIN_HOST_TASK_ESDK
-  ref-manual: remove meta class
-  ref-manual: update system requirements
-  releases.rst: fix release number for 3.3.3
-  scripts/convert-srcuri: Update SRC_URI conversion script to handle github url changes
-  scripts/lib/wic/help.py: Update Fedora Kickstart URLs
-  scripts/oe-package-browser: Fix after overrides change
-  scripts/oe-package-browser: Handle no packages being built
-  spdx.py: Add annotation to relationship
-  sstate: Account for reserved characters when shortening sstate filenames
-  sstate: another fix for touching files inside pseudo
-  sstate: fix touching files inside pseudo
-  staging: Fix autoconf-native rebuild failure
-  strace: fix build against 5.15 kernel/kernel-headers
-  strace: show test suite log on failure
-  stress-ng: convert to git, website is down
-  systemd: add missing include for musl
-  tar: filter CVEs using vendor name
-  test-manual: how to enable reproducible builds
-  testimage: fix unclosed testdata file
-  tzdata: update 2021d to 2021d
-  uninative: Add version to uninative tarball name
-  waffle: convert to git, website is down
-  wayland: Fix wayland-tools packaging
-  wireless-regdb: upgrade 2021.07.14 -> 2021.08.28
-  wpa-supplicant: Match package override to PACKAGES for pkg_postinst

Contributors to 3.4.1
~~~~~~~~~~~~~~~~~~~~~

-  Ahmed Hossam
-  Alexander Kanavin
-  Alexandre Belloni
-  Andrej Valek
-  Andres Beltran
-  Anuj Mittal
-  Bruce Ashfield
-  Chen Qi
-  Claus Stovgaard
-  Daiane Angolini
-  Hsia-Jun(Randy) Li
-  Jon Mason
-  Jose Quaresma
-  Joshua Watt
-  Kai Kang
-  Khem Raj
-  Kiran Surendran
-  Manuel Leonhardt
-  Michael Opdenacker
-  Oleksandr Kravchuk
-  Pablo Saavedra
-  Paul Eggleton
-  Peter Kjellerstedt
-  Quentin Schulz
-  Ralph Siemsen
-  Randy Li
-  Richard Purdie
-  Ross Burton
-  Sakib Sajal
-  Saul Wold
-  Teoh Jay Shen
-  Tim Orling
-  Tom Hochstein
-  Yureka

Repositories / Downloads for 3.4.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: https://git.yoctoproject.org/poky/
-  Branch: :yocto_git:`honister </poky/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.1 </poky/tag/?h=yocto-3.4.1>`
-  Git Revision: :yocto_git:`b53230c08d9f02ecaf35b4f0b70512abbf10ae11 </poky/commit/?id=b53230c08d9f02ecaf35b4f0b70512abbf10ae11>`
-  Release Artefact: poky-b53230c08d9f02ecaf35b4f0b70512abbf10ae11
-  sha: 57d49e2afafb555baf65643acf752464f0eb7842b964713a5de7530c392de159
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.1/poky-b53230c08d9f02ecaf35b4f0b70512abbf10ae11.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.1/poky-b53230c08d9f02ecaf35b4f0b70512abbf10ae11.tar.bz2

meta-mingw

-  Repository Location: https://git.yoctoproject.org/meta-mingw
-  Branch: :yocto_git:`honister </meta-mingw/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.1 </meta-mingw/tag/?h=yocto-3.4.1>`
-  Git Revision: :yocto_git:`f5d761cbd5c957e4405c5d40b0c236d263c916a8 </meta-mingw/commit/?id=f5d761cbd5c957e4405c5d40b0c236d263c916a8>`
-  Release Artefact: meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8
-  sha: d4305d638ef80948584526c8ca386a8cf77933dffb8a3b8da98d26a5c40fcc11
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.1/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.1/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2

meta-gplv2

-  Repository Location: https://git.yoctoproject.org/meta-gplv2
-  Branch: :yocto_git:`honister </meta-gplv2/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.1 </meta-gplv2/tag/?h=yocto-3.4.1>`
-  Git Revision: :yocto_git:`f04e4369bf9dd3385165281b9fa2ed1043b0e400 </meta-gplv2/commit/?id=f04e4369bf9dd3385165281b9fa2ed1043b0e400>`
-  Release Artefact: meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400
-  sha: ef8e2b1ec1fb43dbee4ff6990ac736315c7bc2d8c8e79249e1d337558657d3fe
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`1.52 </bitbake/log/?h=1.52>`
-  Tag: :oe_git:`yocto-3.4.1 </bitbake/tag/?h=yocto-3.4.1>`
-  Git Revision: :oe_git:`44a83b373e1fc34c93cd4a6c6cf8b73b230c1520 </bitbake/commit/?id=44a83b373e1fc34c93cd4a6c6cf8b73b230c1520>`
-  Release Artefact: bitbake-44a83b373e1fc34c93cd4a6c6cf8b73b230c1520
-  sha: 03d50c1318d88d62eb01d359412ea5a8014ef506266629a2bd43ab3a2ef19430
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.1/bitbake-44a83b373e1fc34c93cd4a6c6cf8b73b230c1520.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.1/bitbake-44a83b373e1fc34c93cd4a6c6cf8b73b230c1520.tar.bz2

yocto-docs

-  Repository Location: https://git.yoctoproject.org/yocto-docs
-  Branch: :yocto_git:`honister </yocto-docs/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.1 </yocto-docs/tag/?h=yocto-3.4.1>`
-  Git Revision: :yocto_git:`b250eda5a0beba8acc9641c55a5b0e30594b5178 </yocto-docs/commit/?b250eda5a0beba8acc9641c55a5b0e30594b5178>`
