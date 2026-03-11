.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-5.0.10 (Scarthgap)
------------------------------------------

Security Fixes in Yocto-5.0.10
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  binutils: Fix :cve_nist:`2025-1153`, :cve_nist:`2025-1179`, :cve_nist:`2025-1180` and
   :cve_nist:`2025-1182`
-  connman: Fix :cve_nist:`2025-32366` and :cve_nist:`2025-32743`
-  curl: Fix :cve_nist:`2024-11053` and :cve_nist:`2025-0167`
-  elfutils: Fix :cve_nist:`2025-1371`
-  ffmpeg: Fix :cve_nist:`2024-7055`, :cve_nist:`2024-32230`, :cve_nist:`2024-35366`,
   :cve_nist:`2024-36613`, :cve_nist:`2024-36616`, :cve_nist:`2024-36617` and :cve_nist:`2024-36619`
-  git: Fix :cve_nist:`2024-50349` and :cve_nist:`2024-52006`
-  glib-2.0: fix :cve_nist:`2025-3360` and :cve_nist:`2025-4373`
-  iputils: Fix :cve_nist:`2025-47268`
-  libpam: Fix :cve_nist:`2024-10041`
-  libsoup-2.4: Fix :cve_nist:`2024-52530`, :cve_nist:`2024-52531`, :cve_nist:`2024-52532`,
   :cve_nist:`2025-32906`, :cve_nist:`2025-32909`, :cve_nist:`2025-32910`, :cve_nist:`2025-32911`,
   :cve_nist:`2025-32912`, :cve_nist:`2025-32913`, :cve_nist:`2025-32914` and :cve_nist:`2025-46420`
-  libsoup: Fix :cve_nist:`2025-4476`, :cve_nist:`2025-32906`, :cve_nist:`2025-32909`,
   :cve_nist:`2025-32910`, :cve_nist:`2025-32911`, :cve_nist:`2025-32912`, :cve_nist:`2025-32913`,
   :cve_nist:`2025-32914` and :cve_nist:`2025-46420`
-  libxml2: Fix :cve_nist:`2025-32414` and :cve_nist:`2025-32415`
-  openssh: Fix :cve_nist:`2025-32728`
-  perl: Fix :cve_nist:`2024-56406`
-  ppp: Fix :cve_nist:`2024-58250`
-  python3-jinja2: Fix :cve_nist:`2024-56201`, :cve_nist:`2024-56326` and :cve_nist:`2025-27516`
-  ruby: Fix :cve_nist:`2025-27221`
-  sqlite3: Fix :cve_nist:`2025-3277`, :cve_nist:`2025-29087` and :cve_nist:`2025-29088`


Fixes in Yocto-5.0.10
~~~~~~~~~~~~~~~~~~~~~

-  binutils: stable 2.42 branch updates
-  bluez5: add missing tools to noinst-tools package
-  bluez5: backport a patch to fix btmgmt -i
-  bluez5: make media control a :term:`PACKAGECONFIG` option
-  build-appliance-image: Update to scarthgap head revision
-  buildtools-tarball: Make buildtools respects host CA certificates
-  buildtools-tarball: add envvars into :term:`BB_ENV_PASSTHROUGH_ADDITIONS`
-  buildtools-tarball: move setting of envvars to respective envfile
-  contributor-guide/submit-changes: encourage patch version changelogs
-  cve-check.bbclass: Fix symlink handling also for text files
-  cve-update-nvd2-native: Revert "cve-update-nvd2-native: Tweak to work better with NFS DL_DIR"
-  dev-manual/sbom.rst: fix wrong build outputs
-  docs: Fix dead links that use the :term:`DISTRO` macro
-  docs: conf.py: tweak SearchEnglish to be hyphen-friendly
-  docs:conf.py: define a manpage url
-  ffmpeg: upgrade to 6.1.2
-  git: upgrade to 2.44.3
-  glibc-y2038-tests: remove glibc-y2038-tests_2.39.bb recipe
-  glibc: Add single-threaded fast path to rand()
-  glibc: stable 2.39 branch updates
-  initscripts: add function log_success_msg/log_failure_msg/log_warning_msg
-  libatomic-ops: Update :term:`GITHUB_BASE_URI`
-  manuals: remove repeated word
-  migration-guides: add release notes for 4.0.26, 5.0.8, 5.0.9
-  module.bbclass: add KBUILD_EXTRA_SYMBOLS to install
-  perl: upgrade to 5.38.4
-  perlcross: upgrade to 1.6.2
-  poky.conf: bump version for 5.0.10
-  poky.yaml: introduce DISTRO_LATEST_TAG
-  python3-jinja2: upgrade to 3.1.6
-  ref-manual/release-process: update releases.svg
-  ref-manual/variables.rst: HOST_CC_ARCH: fix wrong SDK reference
-  ref-manual/variables.rst: WATCHDOG_TIMEOUT: fix recipe name
-  ref-manual/variables.rst: add manpage links for toolchain variables
-  ref-manual/variables.rst: add missing documentation for BUILD_* variables
-  ref-manual/variables.rst: document HOST_*_ARCH variables
-  ref-manual/variables.rst: document :term:`INHIBIT_DEFAULT_RUST_DEPS`
-  ref-manual/variables.rst: document :term:`INHIBIT_UPDATERCD_BBCLASS`
-  ref-manual/variables.rst: document :term:`SSTATE_SKIP_CREATION`
-  ref-manual/variables.rst: document :term:`WIC_CREATE_EXTRA_ARGS`
-  ref-manual/variables.rst: document autotools class related variables
-  ref-manual/variables.rst: document missing SDK_*_ARCH variables
-  ref-manual/variables.rst: document the :term:`IMAGE_ROOTFS_MAXSIZE` variable
-  ref-manual/variables.rst: document the :term:`INITRAMFS_MAXSIZE` variable
-  ref-manual/variables.rst: improve the :term:`PKGV` documentation
-  ref-manual/variables.rst: update :term:`ROOT_HOME` documentation
-  ref-manual: kernel-fitimage.bbclass does not use :term:`SPL_SIGN_KEYNAME`
-  scripts/install-buildtools: Update to 5.0.9
-  sphinx-lint: missing space after literal
-  sphinx-lint: trailing whitespace
-  sphinx-lint: unbalanced inline literal markup
-  systemd: Password agents shouldn't be optional
-  systemd: upgrade to 255.18
-  test-manual/intro: remove Buildbot version used
-  tzdata/tzcode-native: upgrade 2025a -> 2025b
-  u-boot: ensure keys are generated before assembling U-Boot FIT image
-  util-linux: Add fix to isolate test fstab entries using CUSTOM_FSTAB
-  wic: bootimg-efi: Support + symbol in filenames


Known Issues in Yocto-5.0.10
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  There is an issue where the target libsoup-2.4 build may fail if apachectl is present on the build
   host. The issue only affects test binaries which aren't actually used. The issue can be fixed by
   disabling the tests or updating to more recent changes on the scarthgap branch which fix this.


Contributors to Yocto-5.0.10
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adrian Freihofer
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Alon Bar-Lev
-  Alper Ak
-  Andrew Kreimer
-  Antonin Godard
-  Archana Polampalli
-  Ashish Sharma
-  Changqing Li
-  Christos Gavros
-  Deepesh Varatharajan
-  Divya Chellam
-  Divyanshu Rathore
-  Enrico Jörns
-  Etienne Cordonnier
-  Guðni Már Gilbert
-  Haixiao Yan
-  Harish Sadineni
-  Igor Opaniuk
-  Jeroen Hofstee
-  Lee Chee Yang
-  Nguyen Dat Tho
-  Niko Mauno
-  Peter Marko
-  Praveen Kumar
-  Priyal Doshi
-  Rogerio Guerra Borin
-  Shubham Kulkarni
-  Soumya Sambu
-  Steve Sakoman
-  Sunil Dora
-  Trevor Woerner
-  Vijay Anusuri
-  Virendra Thakur
-  Vyacheslav Yurkov
-  Yi Zhao
-  Yogita Urade
-  rajmohan r

Repositories / Downloads for Yocto-5.0.10
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.10 </poky/log/?h=yocto-5.0.10>`
-  Git Revision: :yocto_git:`ac257900c33754957b2696529682029d997a8f28 </poky/commit/?id=ac257900c33754957b2696529682029d997a8f28>`
-  Release Artefact: poky-ac257900c33754957b2696529682029d997a8f28
-  sha: ddca7e54b331e78214bea65b346320d4fbcddf4b51103bfbbd9fc3960f32cdc7
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.10/poky-ac257900c33754957b2696529682029d997a8f28.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.10/poky-ac257900c33754957b2696529682029d997a8f28.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0.10 </openembedded-core/log/?h=yocto-5.0.10>`
-  Git Revision: :oe_git:`d5342ffc570d47a723b18297d75bd2f63c2088db </openembedded-core/commit/?id=d5342ffc570d47a723b18297d75bd2f63c2088db>`
-  Release Artefact: oecore-d5342ffc570d47a723b18297d75bd2f63c2088db
-  sha: daa62094f2327f4b3fbcc485e8964d1b86a4722f58fb37e0d8e8e9885094a262
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.10/oecore-d5342ffc570d47a723b18297d75bd2f63c2088db.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.10/oecore-d5342ffc570d47a723b18297d75bd2f63c2088db.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0.10 </meta-mingw/log/?h=yocto-5.0.10>`
-  Git Revision: :yocto_git:`bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f </meta-mingw/commit/?id=bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f>`
-  Release Artefact: meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f
-  sha: ab073def6487f237ac125d239b3739bf02415270959546b6b287778664f0ae65
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.10/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.10/meta-mingw-bd9fef71ec005be3c3a6d7f8b99d8116daf70c4f.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0.10 </bitbake/log/?h=yocto-5.0.10>`
-  Git Revision: :oe_git:`696c2c1ef095f8b11c7d2eff36fae50f58c62e5e </bitbake/commit/?id=696c2c1ef095f8b11c7d2eff36fae50f58c62e5e>`
-  Release Artefact: bitbake-696c2c1ef095f8b11c7d2eff36fae50f58c62e5e
-  sha: fc83f879cd6dd14b9b7eba0161fec23ecc191fed0fb00556ba729dceef6c145f
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.0.10/bitbake-696c2c1ef095f8b11c7d2eff36fae50f58c62e5e.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.0.10/bitbake-696c2c1ef095f8b11c7d2eff36fae50f58c62e5e.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0.10 </yocto-docs/log/?h=yocto-5.0.10>`
-  Git Revision: :yocto_git:`3996388e337377bedc113d072a51fe9d68dd40c6 </yocto-docs/commit/?id=3996388e337377bedc113d072a51fe9d68dd40c6>`

