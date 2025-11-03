.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.3.4 (Nanbield)
----------------------------------------

Security Fixes in Yocto-4.3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve_nist:`2023-4408`, :cve_nist:`2023-5517`, :cve_nist:`2023-5679` and :cve_nist:`2023-50387`
-  gcc: Update :term:`CVE_STATUS` for :cve_nist:`2023-4039` as fixed
-  glibc: Fix :cve_nist:`2023-6246`, :cve_nist:`2023-6779` and :cve_nist:`2023-6780`
-  gnutls: Fix :cve_nist:`2024-0553` and :cve_nist:`2024-0567`
-  gstreamer: Fix :cve_mitre:`2024-0444`
-  libssh2: fix :cve_nist:`2023-48795`
-  libxml2: Fix :cve_nist:`2024-25062`
-  linux-yocto/6.1: Fix :cve_nist:`2023-6610`, :cve_nist:`2023-6915`, :cve_nist:`2023-46838`, :cve_nist:`2023-50431`, :cve_nist:`2024-1085`, :cve_nist:`2024-1086` and :cve_nist:`2024-23849`
-  linux-yocto/6.1: Ignore :cve_nist:`2021-33630`, :cve_nist:`2021-33631`, :cve_nist:`2022-36402`, :cve_nist:`2023-5717`, :cve_nist:`2023-6200`, :cve_nist:`2023-35827`, :cve_nist:`2023-40791`, :cve_nist:`2023-46343`, :cve_nist:`2023-46813`, :cve_nist:`2023-46862`, :cve_nist:`2023-51042`, :cve_nist:`2023-51043`, :cve_mitre:`2023-52340`, :cve_nist:`2024-0562`, :cve_nist:`2024-0565`, :cve_nist:`2024-0582`, :cve_nist:`2024-0584`, :cve_nist:`2024-0607`, :cve_nist:`2024-0639`, :cve_nist:`2024-0641`, :cve_nist:`2024-0646`, :cve_nist:`2024-0775` and :cve_nist:`2024-22705`
-  openssl: fix :cve_nist:`2024-0727`
-  python3-jinja2: Fix :cve_nist:`2024-22195`
-  tiff: Fix :cve_nist:`2023-6228`, :cve_nist:`2023-52355` and :cve_nist:`2023-52356`
-  vim: Fix :cve_nist:`2024-22667`
-  wpa-supplicant: Fix :cve_nist:`2023-52160`
-  xserver-xorg: Fix :cve_nist:`2023-6377`, :cve_nist:`2023-6478`, :cve_nist:`2023-6816`, :cve_nist:`2024-0229`, :cve_nist:`2024-0408`, :cve_nist:`2024-0409`, :cve_nist:`2024-21885` and :cve_nist:`2024-21886`
-  xwayland: Fix :cve_nist:`2023-6816`, :cve_nist:`2024-0408` and :cve_nist:`2024-0409`
-  zlib: Ignore :cve_nist:`2023-6992`


Fixes in Yocto-4.3.4
~~~~~~~~~~~~~~~~~~~~

-  allarch: Fix allarch corner case
-  at-spi2-core: Upgrade to 2.50.1
-  bind: Upgrade to 9.18.24
-  build-appliance-image: Update to nanbield head revision
-  contributor-guide: add notes for tests
-  contributor-guide: be more specific about meta-* trees
-  core-image-ptest: Increase disk size to 1.5G for strace ptest image
-  cpio: Upgrade to 2.15
-  curl: improve run-ptest
-  curl: increase test timeouts
-  cve-check: Log if :term:`CVE_STATUS` set but not reported for component
-  cve-update-nvd2-native: Add an age threshold for incremental update
-  cve-update-nvd2-native: Fix CVE configuration update
-  cve-update-nvd2-native: Fix typo in comment
-  cve-update-nvd2-native: Remove duplicated CVE_CHECK_DB_FILE definition
-  cve-update-nvd2-native: Remove rejected CVE from database
-  cve-update-nvd2-native: nvd_request_next: Improve comment
-  cve_check: cleanup logging
-  cve_check: handle :term:`CVE_STATUS` being set to the empty string
-  dev-manual: Rephrase spdx creation
-  dev-manual: improve descriptions of 'bitbake -S printdiff'
-  dev-manual: packages: clarify shared :term:`PR` service constraint
-  dev-manual: packages: fix capitalization
-  dev-manual: packages: need enough free space
-  docs: add initial stylechecks with Vale
-  docs: correct sdk installation default path
-  docs: document VIRTUAL-RUNTIME variables
-  docs: suppress excess use of "following" word
-  docs: use "manual page(s)"
-  docs: Makefile: remove releases.rst in "make clean"
-  externalsrc: fix task dependency for do_populate_lic
-  glibc: Remove duplicate :term:`CVE_STATUS` for :cve_nist:`2023-4527`
-  glibc: stable 2.38 branch updates (2.38+gitd37c2b20a4)
-  gnutls: Upgrade to 3.8.3
-  gstreamer1.0: skip a test that is known to be flaky
-  gstreamer: Upgrade to 1.22.9
-  gtk: Set :term:`CVE_PRODUCT`
-  kernel.bbclass: Set pkg-config variables for building modules
-  libxml2: Upgrade to 2.11.7
-  linux-firmware: Upgrade to 20240220
-  linux-yocto/6.1: update to v6.1.78
-  mdadm: Disable ptests
-  migration-guides: add release notes for 4.3.3
-  migration-guides: add release notes for 4.0.17
-  migration-guides: fix release notes for 4.3.3 linux-yocto/6.1 CVE entries
-  multilib_global.bbclass: fix parsing error with no kernel module split
-  openssl: fix crash on aarch64 if BTI is enabled but no Crypto instructions
-  openssl: Upgrade to 3.1.5
-  overlayfs: add missing closing parenthesis in selftest
-  poky.conf: bump version for 4.3.4 release
-  profile-manual: usage.rst: fix reference to bug report
-  profile-manual: usage.rst: formatting fixes
-  profile-manual: usage.rst: further style improvements
-  pseudo: Update to pull in gcc14 fix and missing statvfs64 intercept
-  python3-jinja2: Upgrade to 3.1.3
-  ref-manual: release-process: grammar fix
-  ref-manual: system-requirements: update packages to build docs
-  ref-manual: tasks: do_cleanall: recommend using '-f' instead
-  ref-manual: tasks: do_cleansstate: recommend using '-f' instead for a shared sstate
-  ref-manual: variables: adding multiple groups in :term:`GROUPADD_PARAM`
-  ref-manual: variables: add documentation of the variable :term:`SPDX_NAMESPACE_PREFIX`
-  reproducible: Fix race with externalsrc/devtool over lockfile
-  sdk-manual: extensible: correctly describe separate build-sysroots tasks in direct sdk workflows
-  tzdata : Upgrade to 2024a
-  udev-extraconf: fix unmount directories containing octal-escaped chars
-  vim: Upgrade to v9.0.2190
-  wireless-regdb: Upgrade to 2024.01.23
-  xserver-xorg: Upgrade to 21.1.11
-  xwayland: Upgrade to 23.2.4
-  yocto-uninative: Update to 4.4 for glibc 2.39


Known Issues in Yocto-4.3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alex Kiernan
-  Alexander Kanavin
-  Alexander Sverdlin
-  Baruch Siach
-  BELOUARGA Mohamed
-  Benjamin Bara
-  Bruce Ashfield
-  Chen Qi
-  Claus Stovgaard
-  Dhairya Nagodra
-  Geoff Parker
-  Johan Bezem
-  Jonathan GUILLOT
-  Julien Stephan
-  Kai Kang
-  Khem Raj
-  Lee Chee Yang
-  Luca Ceresoli
-  Martin Jansa
-  Michael Halstead
-  Michael Opdenacker
-  Munehisa Kamata
-  Pavel Zhukov
-  Peter Marko
-  Priyal Doshi
-  Richard Purdie
-  Robert Joslyn
-  Ross Burton
-  Simone Weiß
-  Soumya Sambu
-  Steve Sakoman
-  Tim Orling
-  Wang Mingyu
-  Yoann Congal
-  Yogita Urade


Repositories / Downloads for Yocto-4.3.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`nanbield </poky/log/?h=nanbield>`
-  Tag:  :yocto_git:`yocto-4.3.4 </poky/log/?h=yocto-4.3.4>`
-  Git Revision: :yocto_git:`7b8aa378d069ee31373f22caba3bd7fc7863f447 </poky/commit/?id=7b8aa378d069ee31373f22caba3bd7fc7863f447>`
-  Release Artefact: poky-7b8aa378d069ee31373f22caba3bd7fc7863f447
-  sha: 0cb14125f215cc9691cff43982e2c540a5b6018df4ed25c10933135b5bf21d0f
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.4/poky-7b8aa378d069ee31373f22caba3bd7fc7863f447.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.4/poky-7b8aa378d069ee31373f22caba3bd7fc7863f447.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`nanbield </openembedded-core/log/?h=nanbield>`
-  Tag:  :oe_git:`yocto-4.3.4 </openembedded-core/log/?h=yocto-4.3.4>`
-  Git Revision: :oe_git:`d0e68072d138ccc1fb5957fdc46a91871eb6a3e1 </openembedded-core/commit/?id=d0e68072d138ccc1fb5957fdc46a91871eb6a3e1>`
-  Release Artefact: oecore-d0e68072d138ccc1fb5957fdc46a91871eb6a3e1
-  sha: d311fe22ff296c466f9bea1cd26343baee5630bc37f3dda42f2d9d8cc99e3add
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.4/oecore-d0e68072d138ccc1fb5957fdc46a91871eb6a3e1.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.4/oecore-d0e68072d138ccc1fb5957fdc46a91871eb6a3e1.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`nanbield </meta-mingw/log/?h=nanbield>`
-  Tag:  :yocto_git:`yocto-4.3.4 </meta-mingw/log/?h=yocto-4.3.4>`
-  Git Revision: :yocto_git:`49617a253e09baabbf0355bc736122e9549c8ab2 </meta-mingw/commit/?id=49617a253e09baabbf0355bc736122e9549c8ab2>`
-  Release Artefact: meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2
-  sha: 2225115b73589cdbf1e491115221035c6a61679a92a93b2a3cf761ff87bf4ecc
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.4/meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.4/meta-mingw-49617a253e09baabbf0355bc736122e9549c8ab2.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.6 </bitbake/log/?h=2.6>`
-  Tag:  :oe_git:`yocto-4.3.4 </bitbake/log/?h=yocto-4.3.4>`
-  Git Revision: :oe_git:`380a9ac97de5774378ded5e37d40b79b96761a0c </bitbake/commit/?id=380a9ac97de5774378ded5e37d40b79b96761a0c>`
-  Release Artefact: bitbake-380a9ac97de5774378ded5e37d40b79b96761a0c
-  sha: 78f579b9d29e72d09b6fb10ac62aa925104335e92d2afb3155bc9ab1994e36c1
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.3.4/bitbake-380a9ac97de5774378ded5e37d40b79b96761a0c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.3.4/bitbake-380a9ac97de5774378ded5e37d40b79b96761a0c.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`nanbield </yocto-docs/log/?h=nanbield>`
-  Tag: :yocto_git:`yocto-4.3.4 </yocto-docs/log/?h=yocto-4.3.4>`
-  Git Revision: :yocto_git:`05d08b0bbaef760157c8d35a78d7405bc5ffce55 </yocto-docs/commit/?id=05d08b0bbaef760157c8d35a78d7405bc5ffce55>`

