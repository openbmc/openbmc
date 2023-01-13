.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 3.4.4 (honister)
----------------------------------

Security Fixes in 3.4.4
~~~~~~~~~~~~~~~~~~~~~~~

-  tiff: fix :cve:`2022-0865`, :cve:`2022-0891`, :cve:`2022-0907`, :cve:`2022-0908`, :cve:`2022-0909` and :cve:`2022-0924`
-  xz: fix `CVE-2022-1271 <https://security-tracker.debian.org/tracker/CVE-2022-1271>`__
-  unzip: fix `CVE-2021-4217 <https://security-tracker.debian.org/tracker/CVE-2021-4217>`__
-  zlib: fix :cve:`2018-25032`
-  grub: ignore :cve:`2021-46705`

Fixes in 3.4.4
~~~~~~~~~~~~~~

-  alsa-tools: Ensure we install correctly
-  bitbake.conf: mark all directories as safe for git to read
-  bitbake: knotty: display active tasks when printing keepAlive() message
-  bitbake: knotty: reduce keep-alive timeout from 5000s (83 minutes) to 10 minutes
-  bitbake: server/process: Disable gc around critical section
-  bitbake: server/xmlrpcserver: Add missing xmlrpcclient import
-  bitbake: toaster: Fix :term:`IMAGE_INSTALL` issues with _append vs :append
-  bitbake: toaster: fixtures replace gatesgarth
-  build-appliance-image: Update to honister head revision
-  conf.py/poky.yaml: Move version information to poky.yaml and read in conf.py
-  conf/machine: fix QEMU x86 sound options
-  devupstream: fix handling of :term:`SRC_URI`
-  documentation: update for 3.4.4 release
-  externalsrc/devtool: Fix to work with fixed export funcition flags handling
-  gmp: add missing COPYINGv3
-  gnu-config: update :term:`SRC_URI`
-  libxml2: fix CVE-2022-23308 regression
-  libxml2: move to gitlab.gnome.org
-  libxml2: update to 2.9.13
-  libxshmfence: Correct :term:`LICENSE` to HPND
-  license_image.bbclass: close package.manifest file
-  linux-firmware: correct license for ar3k firmware
-  linux-firmware: upgrade 20220310 -> 20220411
-  linux-yocto-rt/5.10: update to -rt61
-  linux-yocto/5.10: cfg/debug: add configs for kcsan
-  linux-yocto/5.10: split vtpm for more granular inclusion
-  linux-yocto/5.10: update to v5.10.109
-  linux-yocto: nohz_full boot arg fix
-  oe-pkgdata-util: Adapt to the new variable override syntax
-  oeqa/selftest/devtool: ensure Git username is set before upgrade tests
-  poky.conf: bump version for 3.4.4 release
-  pseudo: Add patch to workaround paths with crazy lengths
-  pseudo: Fix handling of absolute links
-  sanity: Add warning for local hasheqiv server with remote sstate mirrors
-  scripts/runqemu: Fix memory limits for qemux86-64
-  shadow-native: Simplify and fix syslog disable patch
-  tiff: Add marker for CVE-2022-1056 being fixed
-  toaster: Fix broken overrides usage
-  u-boot: Inherit pkgconfig
-  uninative: Upgrade to 3.6 with gcc 12 support
-  vim: Upgrade 8.2.4524 -> 8.2.4681
-  virglrenderer: update :term:`SRC_URI`
-  webkitgtk: update to 2.32.4
-  wireless-regdb: upgrade 2022.02.18 -> 2022.04.08

Known Issues
~~~~~~~~~~~~

There were a couple of known autobuilder intermittent bugs that occurred during release testing but these are not regressions in the release.

Contributors to 3.4.4
~~~~~~~~~~~~~~~~~~~~~

-  Alexandre Belloni
-  Anuj Mittal
-  Bruce Ashfield
-  Chee Yang Lee
-  Dmitry Baryshkov
-  Joe Slater
-  Konrad Weihmann
-  Martin Jansa
-  Michael Opdenacker
-  Minjae Kim
-  Peter Kjellerstedt
-  Ralph Siemsen
-  Richard Purdie
-  Ross Burton
-  Tim Orling
-  Wang Mingyu
-  Zheng Ruoqin

Repositories / Downloads for 3.4.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`honister </poky/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.4 </poky/tag/?h=yocto-3.4.4>`
-  Git Revision: :yocto_git:`780eeec8851950ee6ac07a2a398ba937206bd2e4 </poky/commit/?id=780eeec8851950ee6ac07a2a398ba937206bd2e4>`
-  Release Artefact: poky-780eeec8851950ee6ac07a2a398ba937206bd2e4
-  sha: 09558927064454ec2492da376156b716d9fd14aae57196435d742db7bfdb4b95
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.4/poky-780eeec8851950ee6ac07a2a398ba937206bd2e4.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.4/poky-780eeec8851950ee6ac07a2a398ba937206bd2e4.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`honister </openembedded-core/log/?h=honister>`
-  Tag: :oe_git:`yocto-3.4.4 </openembedded-core/tag/?h=yocto-3.4.4>`
-  Git Revision: :oe_git:`1a6f5e27249afb6fb4d47c523b62b5dd2482a69d </openembedded-core/commit/?id=1a6f5e27249afb6fb4d47c523b62b5dd2482a69d>`
-  Release Artefact: oecore-1a6f5e27249afb6fb4d47c523b62b5dd2482a69d
-  sha: b8354ca457756384139a579b9e51f1ba854013c99add90c0c4c6ef68421fede5
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.4/oecore-1a6f5e27249afb6fb4d47c523b62b5dd2482a69d.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.4/oecore-1a6f5e27249afb6fb4d47c523b62b5dd2482a69d.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`honister </meta-mingw/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.4 </meta-mingw/tag/?h=yocto-3.4.4>`
-  Git Revision: :yocto_git:`f5d761cbd5c957e4405c5d40b0c236d263c916a8 </meta-mingw/commit/?id=f5d761cbd5c957e4405c5d40b0c236d263c916a8>`
-  Release Artefact: meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8
-  sha: d4305d638ef80948584526c8ca386a8cf77933dffb8a3b8da98d26a5c40fcc11
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.4/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.4/meta-mingw-f5d761cbd5c957e4405c5d40b0c236d263c916a8.tar.bz2

meta-gplv2

-  Repository Location: :yocto_git:`/meta-gplv2`
-  Branch: :yocto_git:`honister </meta-gplv2/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.4 </meta-gplv2/tag/?h=yocto-3.4.4>`
-  Git Revision: :yocto_git:`f04e4369bf9dd3385165281b9fa2ed1043b0e400 </meta-gplv2/commit/?id=f04e4369bf9dd3385165281b9fa2ed1043b0e400>`
-  Release Artefact: meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400
-  sha: ef8e2b1ec1fb43dbee4ff6990ac736315c7bc2d8c8e79249e1d337558657d3fe
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.4/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.4/meta-gplv2-f04e4369bf9dd3385165281b9fa2ed1043b0e400.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`1.52 </bitbake/log/?h=1.52>`
-  Tag: :oe_git:`yocto-3.4.4 </bitbake/tag/?h=yocto-3.4.3>`
-  Git Revision: :oe_git:`c2d8f9b2137bd4a98eb0f51519493131773e7517 </bitbake/commit/?id=c2d8f9b2137bd4a98eb0f51519493131773e7517>`
-  Release Artefact: bitbake-c2d8f9b2137bd4a98eb0f51519493131773e7517
-  sha: a8b6217f2d63975bbf49f430e11046608023ee2827faa893b15d9a0d702cf833
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-3.4.4/bitbake-c2d8f9b2137bd4a98eb0f51519493131773e7517.tar.bz2,
   http://mirrors.kernel.org/yocto/yocto/yocto-3.4.4/bitbake-c2d8f9b2137bd4a98eb0f51519493131773e7517.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`honister </yocto-docs/log/?h=honister>`
-  Tag: :yocto_git:`yocto-3.4.4 </yocto-docs/tag/?h=yocto-3.4.4>`
-  Git Revision: :yocto_git:`5ead7d39aaf9044078dff27f462e29a8e31d89e4 </yocto-docs/commit/?5ead7d39aaf9044078dff27f462e29a8e31d89e4>`
