.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.14 (Scarthgap)
------------------------------------------

Security Fixes in Yocto-5.0.14
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve_nist:`2025-8677`, :cve_nist:`2025-40778` and :cve_nist:`2025-40780`
-  binutils: Fix :cve_nist:`2025-8225`, :cve_nist:`2025-11081`, :cve_nist:`2025-11082`,
   :cve_nist:`2025-11083`, :cve_nist:`2025-11412`, :cve_nist:`2025-11413` and :cve_nist:`2025-11414`
-  cmake: fix :cve_nist:`2025-9301`
-  curl: Ignore :cve_nist:`2025-10966`
-  elfutils: Fix :cve_nist:`2025-1376` and :cve_nist:`2025-1377`
-  expat: Fix :cve_nist:`2025-59375`
-  glib-networking: Fix :cve_nist:`2025-60018` and :cve_nist:`2025-60019`
-  gnupg: Ignore :cve_nist:`2025-30258`
-  go: Fix :cve_nist:`2025-47912`, :cve_nist:`2025-58185`, :cve_nist:`2025-58187`,
   :cve_nist:`2025-58188`, :cve_nist:`2025-58189`, :cve_nist:`2025-61723` and :cve_nist:`2025-61724`
-  libpam: Ignore :cve_nist:`2025-6018`
-  lz4: Fix :cve_nist:`2025-62813`
-  openssh: Fix :cve_nist:`2025-61984` and :cve_nist:`2025-61985`
-  python3: Fix :cve_nist:`2025-59375`
-  python3-xmltodict: Fix :cve_nist:`2025-9375`
-  qemu: Fix :cve_nist:`2024-8354`
-  tiff: Ignore :cve_nist:`2025-8961`
-  u-boot: Fix :cve_nist:`2024-42040`
-  wpa-supplicant: Fix :cve_nist:`2025-24912`


Fixes in Yocto-5.0.14
~~~~~~~~~~~~~~~~~~~~~

-  bind: upgrade to 9.18.41
-  bitbake: bb/fetch2/__init__.py: remove a DeprecationWarning in uri_replace()
-  bitbake: fetch2/wget: Keep query parameters in URL during checkstatus
-  build-appliance-image: Update to scarthgap head revision
-  ca-certificates: Add comment for provenance of :term:`SRCREV`
-  ca-certificates: fix on-target postinstall script
-  ca-certificates: get sources from debian tarballs
-  ca-certificates: submit sysroot patch upstream, drop default-sysroot.patch
-  ca-certificates: upgrade to 20250419
-  classes/create-spdx-2.2: align DEPLOY_DIR_SPDX with SPDX_VERSION layout
-  classes/create-spdx-2.2: Handle empty packages
-  classes-global/license: Move functions to library code
-  classes-global/staging: Exclude do_create_spdx from automatic sysroot extension
-  classes-recipe/baremetal-image: Add image file manifest
-  classes-recipe/image: Add image file manifest
-  curl: only set CA bundle in target build
-  dev-manual, test-manual: Update autobuilder output links
-  flex: fix build with gcc-15 on host
-  glibc: stable 2.39 branch updates
-  gstreamer1.0-plugins-bad: fix buffer allocation fail for v4l2codecs
-  icu: Backport patch to fix build issues with long paths (>512 chars)
-  iptables: remove /etc/ethertypes
-  lib/license: Move package license skip to library
-  lib: oe: license: Add missing import
-  lib: oeqa: spdx: Add tests for extra options
-  linux-yocto/6.6: update to v6.6.111
-  meta: backport :term:`SPDX` 3.0 fixes and tasks from upstream version Walnascar
-  migration-guides: add release notes for 4.0.30
-  oe-build-perf-report: relax metadata matching rules
-  oe-core: Remove empty file
-  oeqa/runtime/ping: don't bother trying to ping localhost
-  oeqa/selftest: Add :term:`SPDX` 3.0 include source case for work-share
-  oeqa/selftest/devtool: Update after upstream repo changes
-  oeqa: spdx: Add tar test for :term:`SPDX` 2.2
-  overview-manual/yp-intro.rst: update on-target packaging info
-  perf: add arm64 source files for unistd_64.h
-  poky.conf: bump version for 5.0.14
-  python3: upgrade to 3.12.12
-  ref-manual/classes.rst: document the relative_symlinks class
-  ref-manual/classes.rst: extend the uninative class documentation
-  ref-manual/classes.rst: gettext: extend the documentation of the class
-  ref-manual/variables.rst: document :term:`CCACHE_DISABLE` CHECKSUM :term:`UNINATIVE_URL`
   :term:`REQUIRED_COMBINED_FEATURES` :term:`REQUIRED_IMAGE_FEATURES`
   :term:`REQUIRED_MACHINE_FEATURES` :term:`USE_NLS` variable
-  ref-manual/variables.rst: fix :term:`LAYERDEPENDS` description
-  selftest: spdx: Add :term:`SPDX` 3.0 test cases
-  selftest/spdx: Fix for SPDX_VERSION addition
-  spdx 3.0: Rework how :term:`SPDX` aliases are linked
-  spdx30_tasks: adapt CVE handling to new cve-check API
-  spdx30_tasks: fix FetchData attribute in add_download_files
-  util-linux: fix pointer usage in hwclock param handling
-  vulnerabilities: update nvdcve file name
-  webkitgtk: upgrade to 2.44.4
-  wireless-regdb: upgrade to 2025.10.07
-  xf86-video-intel: correct :term:`SRC_URI` as freedesktop anongit is down


Known Issues in Yocto-5.0.14
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A

Contributors to Yocto-5.0.14
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:
-  Alexander Kanavin
-  Anders Heimer
-  Ankur Tyagi
-  Antonin Godard
-  Archana Polampalli
-  Bastian Krause
-  Bin Lan
-  Bruce Ashfield
-  Carlos Alberto Lopez Perez
-  Daniel Semkowicz
-  David Nyström
-  Deepesh Varatharajan
-  Gyorgy Sarvari
-  Hongxu Jia
-  Joshua Watt
-  João Marcos Costa
-  Kamel Bouhara (Schneider Electric)
-  Lee Chee Yang
-  Martin Jansa
-  Matthias Schiffer
-  Michael Haener
-  Paul Barker
-  Peter Marko
-  Philippe-Alexandre Mathieu
-  Praveen Kumar
-  Rajeshkumar Ramasamy
-  Rasmus Villemoes
-  Richard Purdie
-  Robert P. J. Day
-  Saravanan
-  Soumya Sambu
-  Steve Sakoman
-  Theodore A. Roth
-  Wang Mingyu
-  Yannic Moog
-  Yash Shinde
-  Yogita Urade

Repositories / Downloads for Yocto-5.0.14
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.14 </yocto-docs/log/?h=yocto-5.0.14>`
-  Git Revision: :yocto_git:`a8687e4bb2e822670b6ad110613a12fa02943d3d </yocto-docs/commit/?id=a8687e4bb2e822670b6ad110613a12fa02943d3d>`
-  Release Artefact: yocto-docs-a8687e4bb2e822670b6ad110613a12fa02943d3d
-  sha: 72a51b6049a59f773720d9b0aa94f090222a41aeb22d65c5f4211c78418fb6fa
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.14/yocto-docs-a8687e4bb2e822670b6ad110613a12fa02943d3d.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.14/yocto-docs-a8687e4bb2e822670b6ad110613a12fa02943d3d.tar.bz2

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.14 </poky/log/?h=yocto-5.0.14>`
-  Git Revision: :yocto_git:`7e8674996b0164b07e56bc066d0fba790e627061 </poky/commit/?id=7e8674996b0164b07e56bc066d0fba790e627061>`
-  Release Artefact: poky-7e8674996b0164b07e56bc066d0fba790e627061
-  sha: 071e189ebccfad99d4d79ea9021475296fa642611828249f0963b019f842a021
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.14/poky-7e8674996b0164b07e56bc066d0fba790e627061.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.14/poky-7e8674996b0164b07e56bc066d0fba790e627061.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.14 </openembedded-core/log/?h=yocto-5.0.14>`
-  Git Revision: :oe_git:`471adaa5f77fa3b974eab60a2ded48e360042828 </openembedded-core/commit/?id=471adaa5f77fa3b974eab60a2ded48e360042828>`
-  Release Artefact: oecore-471adaa5f77fa3b974eab60a2ded48e360042828
-  sha: 4dfad047a68aea2293845cdb4a86911bb3b1b0444a63f51b4e5a2448018d6a5e
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.14/oecore-471adaa5f77fa3b974eab60a2ded48e360042828.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.14/oecore-471adaa5f77fa3b974eab60a2ded48e360042828.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`scarthgap </meta-yocto/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.14 </meta-yocto/log/?h=yocto-5.0.14>`
-  Git Revision: :yocto_git:`bf6aea52c4009e08f26565c33ce432eec7cfb090 </meta-yocto/commit/?id=bf6aea52c4009e08f26565c33ce432eec7cfb090>`
-  Release Artefact: meta-yocto-bf6aea52c4009e08f26565c33ce432eec7cfb090
-  sha: 92c9da1027efaf945d80bcd44984d5f8e7606c7ded485b57c0c8f47c9fa1302d
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.14/meta-yocto-bf6aea52c4009e08f26565c33ce432eec7cfb090.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.14/meta-yocto-bf6aea52c4009e08f26565c33ce432eec7cfb090.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.14 </meta-mingw/log/?h=yocto-5.0.14>`
-  Git Revision: :yocto_git:`bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f </meta-mingw/commit/?id=bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f>`
-  Release Artefact: meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f
-  sha: ab073def6487f237ac125d239b3739bf02415270959546b6b287778664f0ae65
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.14/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.14/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.14 </bitbake/log/?h=yocto-5.0.14>`
-  Git Revision: :oe_git:`8dcf084522b9c66a6639b5f117f554fde9b6b45a </bitbake/commit/?id=8dcf084522b9c66a6639b5f117f554fde9b6b45a>`
-  Release Artefact: bitbake-8dcf084522b9c66a6639b5f117f554fde9b6b45a
-  sha: 766eda21f2a914276d2723b1d8248be11507f954aef8fc5bb1767f3cb65688dd
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.14/bitbake-8dcf084522b9c66a6639b5f117f554fde9b6b45a.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.14/bitbake-8dcf084522b9c66a6639b5f117f554fde9b6b45a.tar.bz2
