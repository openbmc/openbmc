.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 3.4.3 (honister)
----------------------------------

Security Fixes in 3.4.3
~~~~~~~~~~~~~~~~~~~~~~~

-  ghostscript: fix :cve:`2021-3781`
-  ghostscript: fix :cve:`2021-45949`
-  tiff: Add backports for two CVEs from upstream (:cve:`2022-0561` & :cve:`2022-0562`)
-  gcc : Fix :cve:`2021-46195`
-  virglrenderer: fix `CVE-2022-0135 <https://security-tracker.debian.org/tracker/CVE-2022-0135>`__ and `CVE-2022-0175 <https://security-tracker.debian.org/tracker/CVE-2022-0175>`__
-  binutils: Add fix for :cve:`2021-45078`


Fixes in 3.4.3
~~~~~~~~~~~~~~

-  Revert "cve-check: add lockfile to task"
-  asciidoc: update git repository
-  bitbake: build: Tweak exception handling for setscene tasks
-  bitbake: contrib: Fix hash server Dockerfile dependencies
-  bitbake: cooker: Improve parsing failure from handled exception usability
-  bitbake: data_smart: Fix overrides file/line message additions
-  bitbake: fetch2: ssh: username and password are optional
-  bitbake: tests/fetch: Handle upstream master -> main branch change
-  bitbake: utils: Ensure shell function failure in python logging is correct
-  build-appliance-image: Update to honister head revision
-  build-appliance-image: Update to honister head revision
-  coreutils: remove obsolete ignored CVE list
-  crate-fetch: fix setscene failures
-  cups: Add --with-dbusdir to :term:`EXTRA_OECONF` for deterministic build
-  cve-check: create directory of CVE_CHECK_MANIFEST before copy
-  cve-check: get_cve_info should open the database read-only
-  default-distrovars.inc: Switch connectivity check to a yoctoproject.org page
-  depmodwrapper-cross: add config directory option
-  devtool: deploy-target: Remove stripped binaries in pseudo context
-  devtool: explicitly set main or master branches in upgrades when available
-  docs: fix hardcoded link warning messages
-  documentation: conf.py: update for 3.4.2
-  documentation: prepare for 3.4.3 release
-  expat: Upgrade to 2.4.7
-  gcc-target: fix glob to remove gcc-<version> binary
-  gcsections: add nativesdk-cairo to exclude list
-  go: update to 1.16.15
-  gst-devtools: 1.18.5 -> 1.18.6
-  gst-examples: 1.18.5 -> 1.18.6
-  gstreamer1.0-libav: 1.18.5 -> 1.18.6
-  gstreamer1.0-omx: 1.18.5 -> 1.18.6
-  gstreamer1.0-plugins-bad: 1.18.5 -> 1.18.6
-  gstreamer1.0-plugins-base: 1.18.5 -> 1.18.6
-  gstreamer1.0-plugins-good: 1.18.5 -> 1.18.6
-  gstreamer1.0-plugins-ugly: 1.18.5 -> 1.18.6
-  gstreamer1.0-python: 1.18.5 -> 1.18.6
-  gstreamer1.0-rtsp-server: 1.18.5 -> 1.18.6
-  gstreamer1.0-vaapi: 1.18.5 -> 1.18.6
-  gstreamer1.0: 1.18.5 -> 1.18.6
-  harfbuzz: upgrade 2.9.0 -> 2.9.1
-  initramfs-framework: unmount automounts before switch_root
-  kernel-devsrc: do not copy Module.symvers file during install
-  libarchive : update to 3.5.3
-  libpcap: Disable DPDK explicitly
-  libxml-parser-perl: Add missing :term:`RDEPENDS`
-  linux-firmware: upgrade 20211216 -> 20220209
-  linux-yocto/5.10: Fix ramoops/ftrace
-  linux-yocto/5.10: features/zram: remove CONFIG_ZRAM_DEF_COMP
-  linux-yocto/5.10: fix dssall build error with binutils 2.3.8
-  linux-yocto/5.10: ppc/riscv: fix build with binutils 2.3.8
-  linux-yocto/5.10: update genericx86* machines to v5.10.99
-  linux-yocto/5.10: update to v5.10.103
-  mc: fix build if ncurses have been configured without wide characters
-  oeqa/buildtools: Switch to our webserver instead of example.com
-  patch.py: Prevent git repo reinitialization
-  perl: Improve and update module RPDEPENDS
-  poky.conf: bump version for 3.4.3 honister release
-  qemuboot: Fix build error if UNINATIVE_LOADER is unset
-  quilt: Disable external sendmail for deterministic build
-  recipetool: Fix circular reference in :term:`SRC_URI`
-  releases: update to include 3.3.5
-  releases: update to include 3.4.2
-  rootfs-postcommands: amend systemd_create_users add user to group check
-  ruby: update 3.0.2 -> 3.0.3
-  scripts/runqemu-ifdown: Don't treat the last iptables command as special
-  sdk: fix search for dynamic loader
-  selftest: recipetool: Correct the URI for socat
-  sstate: inside the threadedpool don't write to the shared localdata
-  uninative: Upgrade to 3.5
-  util-linux: upgrade to 2.37.4
-  vim: Update to 8.2.4524 for further CVE fixes
-  wic: Use custom kernel path if provided
-  wireless-regdb: upgrade 2021.08.28 -> 2022.02.18
-  zip: modify when match.S is built

Contributors to 3.4.3
~~~~~~~~~~~~~~~~~~~~~

-  Alexander Kanavin
-  Anuj Mittal
-  Bill Pittman
-  Bruce Ashfield
-  Chee Yang Lee
-  Christian Eggers
-  Daniel Gomez
-  Daniel Müller
-  Daniel Wagenknecht
-  Florian Amstutz
-  Joe Slater
-  Jose Quaresma
-  Justin Bronder
-  Lee Chee Yang
-  Michael Halstead
-  Michael Opdenacker
-  Oleksandr Ocheretnyi
-  Oleksandr Suvorov
-  Pavel Zhukov
-  Peter Kjellerstedt
-  Richard Purdie
-  Robert Yang
-  Ross Burton
-  Sakib Sajal
-  Saul Wold
-  Sean Anderson
-  Stefan Herbrechtsmeier
-  Tamizharasan Kumar
-  Tean Cunningham
-  Zoltán Böszörményi
-  pgowda
-  Wang Mingyu

Repositories / Downloads for 3.4.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`honister </poky/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.3 </poky/tag/?h=yocto-3.4.3>`
-  Git Revision: :yocto_git:`ee68ae307fd951b9de6b31dc6713ea29186b7749 </poky/commit/?id=ee68ae307fd951b9de6b31dc6713ea29186b7749>`
-  Release Artefact: poky-ee68ae307fd951b9de6b31dc6713ea29186b7749
-  sha: 92c3d73c3e74f0e1d5c2ab2836ce3a3accbe47772cea70df3755845e0db1379b
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.3/poky-ee68ae307fd951b9de6b31dc6713ea29186b7749.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.3/poky-ee68ae307fd951b9de6b31dc6713ea29186b7749.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`honister </openembedded-core/log/?h=honister>`
-  Tag: :oe_git:`yocto-3.4.3 </openembedded-core/tag/?h=yocto-3.4.3>`
-  Git Revision: :oe_git:`ebca8f3ac9372b7ebb3d39e8f7f930b63b481448 </openembedded-core/commit/?id=ebca8f3ac9372b7ebb3d39e8f7f930b63b481448>`
-  Release Artefact: oecore-ebca8f3ac9372b7ebb3d39e8f7f930b63b481448
-  sha: f28e503f6f6c0bcd9192dbd528f8e3c7bcea504c089117e0094d9a4f315f4b9f
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.3/oecore-ebca8f3ac9372b7ebb3d39e8f7f930b63b481448.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.3/oecore-ebca8f3ac9372b7ebb3d39e8f7f930b63b481448.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`honister </meta-mingw/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.3 </meta-mingw/tag/?h=yocto-3.4.3>`
-  Git Revision: :yocto_git:`f5d761cbd5c957e4405c5d40b0c236d263c916a8 </meta-mingw/commit/?id=f5d761cbd5c957e4405c5d40b0c236d263c916a8>`
-  Release Artefact: meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8
-  sha: d4305d638ef80948584526c8ca386a8cf77933dffb8a3b8da98d26a5c40fcc11
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.3/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.3/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`honister </meta-gplv2/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.3 </meta-gplv2/tag/?h=yocto-3.4.3>`
-  Git Revision: :yocto_git:`f04e4369bf9dd3385165281b9fa2ed1043b0e400 </meta-gplv2/commit/?id=f04e4369bf9dd3385165281b9fa2ed1043b0e400>`
-  Release Artefact: meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400
-  sha: ef8e2b1ec1fb43dbee4ff6990ac736315c7bc2d8c8e79249e1d337558657d3fe
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.3/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.3/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`1.52 </bitbake/log/?h=1.52>`
-  Tag: :oe_git:`yocto-3.4.3 </bitbake/tag/?h=yocto-3.4.3>`
-  Git Revision: :oe_git:`43dcb2b2a2b95a5c959be57bca94fb7190ea6257 </bitbake/commit/?id=43dcb2b2a2b95a5c959be57bca94fb7190ea6257>`
-  Release Artefact: bitbake-43dcb2b2a2b95a5c959be57bca94fb7190ea6257
-  sha: 92497ff97fed81dcc6d3e202969fb63ca983a8f5d9d91cafc6aee88312f79cf9
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.3/bitbake-43dcb2b2a2b95a5c959be57bca94fb7190ea6257.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.3/bitbake-43dcb2b2a2b95a5c959be57bca94fb7190ea6257.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`honister </yocto-docs/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.3 </yocto-docs/tag/?h=yocto-3.4.3>`
-  Git Revision: :yocto_git:`15f46f97d9cad558c19fc1dc19cfbe3720271d04 </yocto-docs/commit/?15f46f97d9cad558c19fc1dc19cfbe3720271d04>`
