.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 4.2 (mickledore)
----------------------------------

New Features / Enhancements in 4.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Linux kernel 6.1, glibc 2.37 and ~350 other recipe upgrades

-  Python 3.8+ and GCC 8.0+ are now the minimum required versions on the build host.
   For host distributions that do not provide it, this is included as part of the
   :term:`buildtools` tarball.

-  BitBake in this release now supports a new ``addpylib`` directive to enable
   Python libraries within layers. For more information,
   see :ref:`bitbake-user-manual/bitbake-user-manual-metadata:extending python library code`.

   This directive should be added to your layer configuration
   as in the below example from ``meta/conf/layer.conf``::

      addpylib ${LAYERDIR}/lib oe

-  BitBake has seen multiple internal changes that may improve
   memory and disk usage as well as parsing time, in particular:

   -  BitBake's Cooker server is now multithreaded.

   -  Ctrl+C can now be used to interrupt some long-running operations
      that previously ignored it.

   -  BitBake's cache has been extended to include more hash
      debugging data, but has also been optimized to :yocto_git:`compress
      cache data <https://git.yoctoproject.org/poky/commit/?h=mickledore&id=7d010055e2af3294e17db862f42664ca689a9356>`.

   -  BitBake's UI will now ping the server regularly to ensure
      it is still alive.

-  New variables:

   -  :term:`VOLATILE_TMP_DIR` allows to specify
      whether ``/tmp`` should be on persistent storage
      or in RAM.

   -  :term:`SPDX_CUSTOM_ANNOTATION_VARS` allows to add
      specific comments to the :term:`SPDX` description of a recipe.

-  Rust improvements:

   -  This release adds Cargo support on the target, and includes
      automated QA tests for this functionality.

   -  It also supports checksums for Rust crates and makes
      them mandatory for each crate in a recipe.

   -  New :ref:`ref-classes-cargo-update-recipe-crates` class to
      enable updating :term:`SRC_URI` crate lists from ``Cargo.lock``

   -  Enabled building Rust for baremetal targets

   -  You can now also easily select to build beta or nightly
      versions of Rust with a new :term:`RUST_CHANNEL` variable
      (use at own risk)

   -  Support for local GitHub repos in :term:`SRC_URI` as
      replacements for Cargo dependencies

   -  Use built-in Rust targets for ``-native`` builds to save several
      minutes building the Rust toolchain

-  Architecture-specific enhancements:

   -  This release adds initial support for the
      :wikipedia:`LoongArch <Loongson#LoongArch>`
      (``loongarch64``) architecture, though there is no testing for it yet.

   -  New ``x86-64-v3`` tunes (AVX, AVX2, BMI1, BMI2, F16C, FMA, LZCNT, MOVBE, XSAVE)

   -  go: add support to build on ppc64le
   -  rust: rustfmt now working and installed for riscv32
   -  libpng: enable NEON for aarch64 to ensure consistency with arm32.
   -  baremetal-helloworld: Enable x86 and x86-64 ports

-  Kernel-related enhancements:

   -  Added some support for building 6.2/6.3-rc kernels
   -  linux-yocto-dev: mark as compatible with qemuarm64 and qemuarmv5
   -  Add kernel specific OBJCOPY to help switching toolchains cleanly for kernel build between gcc and clang

-  New core recipes:

   - ``debugedit``
   - ``gtk4`` (import from meta-gnome)
   - ``gcr``: add recipe for gcr-4
   - ``graphene`` (import from meta-oe)
   - ``libc-test``
   - ``libportal`` (import from meta-gnome)
   - ``libslirp``
   - ``libtest-fatal-perl``
   - ``libtest-warnings-perl`` (import from meta-perl)
   - ``libtry-tiny-perl``
   - ``python3-build``
   - ``python3-pyproject-hooks``
   - ``python3-hatch-fancy-pypi-readme``
   - ``python3-unittest-automake``

-  QEMU/runqemu enhancements:

   -  Set ``QB_SMP`` with ?= to make it easier to modify
   -  Set ``QB_CPU`` with ?= to make it easier to modify (x86 configuration only)
   -  New ``QB_NFSROOTFS_EXTRA_OPT`` to allow extra options to be appended to the NFS rootfs options in kernel boot args, e.g. ``"wsize=4096,rsize=4096"``
   -  New ``QB_SETUP_CMD`` and ``QB_CLEANUP_CMD`` to enable running custom shell setup and cleanup commands before and after QEMU.
   -  ``QB_DEFAULT_KERNEL`` now defaults to pick the bundled initramfs kernel image if the Linux kernel image is generated with :term:`INITRAMFS_IMAGE_BUNDLE` set to "1"
   -  Split out the QEMU guest agent to its own ``qemu-guest-agent`` package
   -  runqemu: new ``guestagent`` option to enable communication with the guest agent
   -  runqemu: respect :term:`IMAGE_LINK_NAME` when searching for image

-  Image-related enhancements:

   -  Add 7-Zip support in image conversion types (``7zip``)
   -  New :term:`IMAGE_MACHINE_SUFFIX` variable to allow easily removing machine name suffix from image file names

-  wic Image Creator enhancements:

   -  ``bootimg-efi.py``: add support for directly loading Linux kernel UEFI stub
   -  ``bootimg-efi.py``: implement ``--include-path``
   -  Allow usage of ``fstype=none`` to specify an unformatted partition
   -  Implement repeatable disk identifiers based on :term:`SOURCE_DATE_EPOCH`

-  FIT image related improvements:

   -  FIT image signing support has been reworked to remove interdependencies and make it more easily extensible
   -  Skip FDT section creation for applicable symlinks to avoid the same dtb being duplicated
   -  New :term:`FIT_CONF_DEFAULT_DTB` variable to enable selecting default dtb when multiple dtbs exist

-  SDK-related improvements:

   -  Extended the following recipes to nativesdk:

      - ``bc``
      - ``gi-docgen``
      - ``gperf``
      - ``python3-iniconfig``
      - ``python3-atomicwrites``
      - ``python3-markdown``
      - ``python3-smartypants``
      - ``python3-typogrify``
      - ``ruby``
      - ``unifdef``

   -  New :term:`SDK_ZIP_OPTIONS` variable to enable passing additional options to the zip command when preparing the SDK zip archive
   -  New Rust SDK target packagegroup (``packagegroup-rust-sdk-target``)

-  Testing:

   -  The ptest images have changed structure in this release. The
      underlying ``core-image-ptest`` recipe now uses :term:`BBCLASSEXTEND` to
      create a variant for each ptest enabled recipe in OE-Core.

      For example, this means that ``core-image-ptest-bzip2``,
      ``core-image-ptest-lttng-tools`` and many more image targets now exist
      and can be built/tested individually.

      The ``core-image-ptest-all`` and ``core-image-ptest-fast`` targets are now
      wrappers that target groups of individual images and means that the tests
      can be executed in parallel during our automated testing. This also means
      the dependencies are more accurately tested.

   -  It is now possible to track regression changes between releases using
      :oe_git:`yocto_testresults_query.py </openembedded-core/tree/scripts/yocto_testresults_query.py>`,
      which is a thin wrapper over :oe_git:`resulttool
      </openembedded-core/tree/scripts/resulttool>`. Here is an example
      command, which allowed to spot and fix a regression in the
      ``quilt`` ptest::

         yocto_testresults_query.py regression-report 4.2_M1 4.2_M2

      See this `blog post about regression detection
      <https://bootlin.com/blog/continuous-integration-in-yocto-improving-the-regressions-detection/>`__.

   -  This release adds support for parallel ptest execution with a ptest per image.
      This takes ptest execution time from 3.5 hours to around 45 minutes on the autobuilder.

   -  Basic Rust compile/run and cargo tests

   -  New ``python3-unittest-automake`` recipe which provides modules for pytest
      and unittest to adjust their output to automake-style for easier integration
      with the ptest system.

   -  ptest support added to ``bc``, ``cpio`` and ``gnutls``, and fixes made to
      ptests in numerous other recipes.

   -  ``ptest-runner`` now adds a non-root "ptest" user to run tests.

   -  ``resulttool``: add a ``--list-ptest`` option to the log subcommand to list ptest names
      in a results file

   -  ``resulttool``: regression: add metadata filtering for oeselftest

-  New :term:`PACKAGECONFIG` options in the following recipes:

   - ``at-spi2-core``
   - ``base-passwd``
   - ``cronie``
   - ``cups``
   - ``curl``
   - ``file``
   - ``gstreamer1.0-plugins-good``
   - ``gtk+3``
   - ``iproute2``
   - ``libsdl2``
   - ``libtiff``
   - ``llvm``
   - ``mesa``
   - ``psmisc``
   - ``qemu``
   - ``sudo``
   - ``systemd``
   - ``tiff``
   - ``util-linux``

-  Extended the following recipes to native:

   - ``iso-codes``
   - ``libxkbcommon``
   - ``p11-kit``
   - ``python3-atomicwrites``
   - ``python3-dbusmock``
   - ``python3-iniconfig``
   - ``xkeyboard-config``

-  Utility script changes:

   -  ``devtool``: ignore patch-fuzz errors when extracting source in order to enable fixing fuzz issues
   -  ``oe-setup-layers``: make efficiently idempotent
   -  ``oe-setup-layers``: print a note about submodules if present
   -  New ``buildstats-summary`` script to show a summary of the buildstats data
   -  :ref:`ref-classes-report-error` class: catch ``Nothing PROVIDES`` error
   -  ``combo-layer``: add ``sync-revs`` command
   -  ``convert-overrides``: allow command-line customizations

-  bitbake-layers improvements:

   -  ``layerindex-fetch``: checkout layer(s) branch when clone exists
   -  ``create``: add ``-a``/``--add-layer option`` to add layer to ``bblayers.conf`` after creating layer
   -  ``show-layers``: improve output layout

-  Other BitBake improvements:

   -  Inline Python snippets can now include dictionary expressions
   -  Evaluate the value of export/unexport/network flags so that they can be reset to "0"
   -  Make :term:`EXCLUDE_FROM_WORLD` boolean so that it can be reset to "0"
   -  Support int values in ``bb.utils.to_boolean()`` in addition to strings
   -  ``bitbake-getvar``: Add a ``quiet`` command line argument
   -  Allow the ``@`` character in variable flag names
   -  Python library code will now be included when calculating task hashes
   -  ``fetch2/npmsw``: add more short forms for git operations
   -  Display a warning when ``SRCREV = "${AUTOREV}"`` is set too late to be effective
   -  Display all missing :term:`SRC_URI` checksums at once
   -  Improve error message for a missing multiconfig
   -  Switch to a new :term:`BB_CACHEDIR` variable for codeparser cache location
   -  Mechanism introduced to reduce the codeparser cache unnecessarily growing in size

-  Packaging changes:

   -  ``rng-tools`` is no longer recommended by ``openssh``, and the ``rng-tools``
      service files have been split out to their own package
   -  ``linux-firmware``: split ``rtl8761`` and ``amdgpu`` firmware
   -  ``linux-firmware``: add new firmware file to ``${PN}-qcom-adreno-a530``
   -  ``iproute2``: separate ``routel`` and add Python dependency
   -  ``xinetd``: move ``xconv.pl`` script to separate package
   -  ``perf``: enable debug/source packaging

-  Prominent documentation updates:

   -  Substantially expanded the ":doc:`/dev-manual/vulnerabilities`" section.
   -  Added a new ":doc:`/dev-manual/sbom`" section about SPDX SBoM generation.
   -  Expanded ":ref:`init-manager`" documentation.
   -  New section about :ref:`ref-long-term-support-releases`.
   -  System Requirements: details about :ref:`system-requirements-minimum-ram`.
   -  Details about :ref:`ref-building-meson-package` and the
      :ref:`ref-classes-meson` class.
   -  Documentation about how to write recipes for Rust programs. See the
      :ref:`ref-classes-cargo` class.
   -  Documentation about how to write recipes for Go programs. See the
      :ref:`ref-classes-go` class.
   -  Variable index: added references to variables only documented in the
      BitBake manual. All variables should be easy to access through the Yocto
      Manual variable index.
   -  Expanded the description of the :term:`BB_NUMBER_THREADS` variable.

-  Miscellaneous changes:

   -  Supporting 64 bit dates on 32 bit platforms: several packages have been
      updated to pass year 2038 tests, and a QA check for 32 bit time and file
      offset functions has been added (default off)

   -  Patch fuzz/Upstream-Status checking has been reworked:

      -  Upstream-Status checking is now configurable from :term:`WARN_QA`/:term:`ERROR_QA` (``patch-status-core``)
      -  Can now be enabled for non-core layers (``patch-status-noncore``)
      -  ``patch-fuzz`` is now in :term:`ERROR_QA` by default, and actually stops the build

   -  Many packages were updated to add large file support.

   -  ``vulkan-loader``: allow headless targets to build the loader
   -  ``dhcpcd``: fix to work with systemd
   -  ``u-boot``: add /boot to :term:`SYSROOT_DIRS` to allow boot files to be used by other recipes
   -  ``linux-firmware``: don't put the firmware into the sysroot
   -  ``cups``: add :term:`PACKAGECONFIG` to control web interface and default to off
   -  ``buildtools-tarball``: export certificates to python and curl
   -  ``yocto-check-layer``: allow OE-Core to be tested
   -  ``yocto-check-layer``: check for patch file upstream status
   -  ``boost``: enable building ``Boost.URL`` library
   -  ``native``: drop special variable handling
   -  Poky: make it easier to set :term:`INIT_MANAGER` from local.conf
   -  :ref:`ref-classes-create-spdx`: add support for custom annotations (:term:`SPDX_CUSTOM_ANNOTATION_VARS`)
   -  :ref:`ref-classes-create-spdx`: report downloads as separate packages
   -  :ref:`ref-classes-create-spdx`: remove the top-level image SPDX file and the JSON index file from :term:`DEPLOYDIR` to avoid confusion
   -  ``os-release``: replace ``DISTRO_CODENAME`` with ``VERSION_CODENAME`` (still set from :term:`DISTRO_CODENAME`)
   -  ``weston``: add kiosk shell
   -  :ref:`ref-classes-overlayfs`: Allow unused mount points
   -  ``sstatesig``: emit more helpful error message when not finding sstate manifest
   -  :ref:`ref-classes-pypi`.bbclass: Set :term:`SRC_URI` downloadfilename with an optional prefix
   -  ``poky-bleeding`` distro: update and rework
   -  :ref:`package.bbclass <ref-classes-package>`: check if package names conflict via ``PKG:${PN}`` override in :ref:`do_package <ref-tasks-package>`
   -  ``cve-update-nvd2-native``: new NVD CVE database fetcher using the 2.0 API
   -  :ref:`ref-classes-mirrors` class: use shallow tarball for ``binutils-native``/``nativesdk-binutils``
   -  ``meta/conf``: move default configuration templates into ``meta/conf/templates/default``
   -  ``binutils``: enable ``--enable-new-dtags`` as per many Linux distributions
   -  ``base-files``: drop ``localhost.localdomain`` from hosts file as per many Linux distributions
   -  ``packagegroup-core-boot``: make ``init-ifupdown`` package a recommendation

Known Issues in 4.2
~~~~~~~~~~~~~~~~~~~

- N/A

Recipe License changes in 4.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following corrections have been made to the :term:`LICENSE` values set by recipes:

- ``curl``: set :term:`LICENSE` appropriately to ``curl`` as it is a special derivative of the MIT/X license, not exactly that license.
- ``libgit2``: added ``Zlib``, ``ISC``, ``LGPL-2.1-or-later`` and ``CC0-1.0`` to :term:`LICENSE` covering portions of the included code.
- ``linux-firmware``: set package :term:`LICENSE` appropriately for all qcom packages

Security Fixes in 4.2
~~~~~~~~~~~~~~~~~~~~~

- binutils: :cve_nist:`2022-4285`, :cve_mitre:`2023-25586`
- curl: :cve_nist:`2022-32221`, :cve_nist:`2022-35260`, :cve_nist:`2022-42915`, :cve_nist:`2022-42916`
- epiphany: :cve_nist:`2023-26081`
- expat: :cve_nist:`2022-43680`
- ffmpeg: :cve_nist:`2022-3964`, :cve_nist:`2022-3965`
- git: :cve_nist:`2022-39260`, :cve_nist:`2022-41903`, :cve_nist:`2022-23521`, :cve_nist:`2022-41953` (ignored)
- glibc: :cve_nist:`2023-25139` (ignored)
- go: :cve_nist:`2023-24532`, :cve_nist:`2023-24537`
- grub2: :cve_nist:`2022-2601`, :cve_nist:`2022-3775`, :cve_mitre:`2022-28736`
- inetutils: :cve_nist:`2019-0053`
- less: :cve_nist:`2022-46663`
- libarchive: :cve_nist:`2022-36227`
- libinput: :cve_nist:`2022-1215`
- libpam: :cve_nist:`2022-28321`
- libpng: :cve_nist:`2019-6129`
- libx11: :cve_nist:`2022-3554`
- openssh: :cve_nist:`2023-28531`
- openssl: :cve_nist:`2022-3358`, :cve_nist:`2022-3786`, :cve_nist:`2022-3602`, :cve_nist:`2022-3996`, :cve_nist:`2023-0286`, :cve_nist:`2022-4304`, :cve_nist:`2022-4203`, :cve_nist:`2023-0215`, :cve_nist:`2022-4450`, :cve_nist:`2023-0216`, :cve_nist:`2023-0217`, :cve_nist:`2023-0401`, :cve_nist:`2023-0464`
- ppp: :cve_nist:`2022-4603`
- python3-cryptography{-vectors}: :cve_nist:`2022-3602`, :cve_nist:`2022-3786`, :cve_nist:`2023-23931`
- python3: :cve_mitre:`2022-37460`
- qemu: :cve_nist:`2022-3165`
- rust: :cve_nist:`2022-46176`
- rxvt-unicode: :cve_nist:`2022-4170`
- screen: :cve_nist:`2023-24626`
- shadow: :cve_nist:`2023-29383`, :cve_nist:`2016-15024` (ignored)
- sudo: :cve_nist:`2022-43995`
- systemd: :cve_nist:`2022-4415` (ignored)
- tar: :cve_nist:`2022-48303`
- tiff: :cve_nist:`2022-3599`, :cve_nist:`2022-3597`, :cve_nist:`2022-3626`, :cve_nist:`2022-3627`, :cve_nist:`2022-3570`, :cve_nist:`2022-3598`, :cve_nist:`2022-3970`, :cve_nist:`2022-48281`
- vim: :cve_nist:`2022-3352`, :cve_nist:`2022-4141`, :cve_nist:`2023-0049`, :cve_nist:`2023-0051`, :cve_nist:`2023-0054`, :cve_nist:`2023-0288`, :cve_nist:`2023-1127`, :cve_nist:`2023-1170`, :cve_nist:`2023-1175`, :cve_nist:`2023-1127`, :cve_nist:`2023-1170`, :cve_nist:`2023-1175`, :cve_nist:`2023-1264`, :cve_nist:`2023-1355`, :cve_nist:`2023-0433`, :cve_nist:`2022-47024`, :cve_nist:`2022-3705`
- xdg-utils: :cve_nist:`2022-4055`
- xserver-xorg: :cve_nist:`2022-3550`, :cve_nist:`2022-3551`, :cve_nist:`2023-1393`, :cve_nist:`2023-0494`, :cve_nist:`2022-3553` (ignored)

Recipe Upgrades in 4.2
~~~~~~~~~~~~~~~~~~~~~~

- acpid: upgrade 2.0.33 -> 2.0.34
- adwaita-icon-theme: update 42.0 -> 43
- alsa-lib: upgrade 1.2.7.2 -> 1.2.8
- alsa-ucm-conf: upgrade 1.2.7.2 -> 1.2.8
- alsa-utils: upgrade 1.2.7 -> 1.2.8
- apr: update 1.7.0 -> 1.7.2
- apr-util: update 1.6.1 -> 1.6.3
- argp-standalone: replace with a maintained fork
- at-spi2-core: upgrade 2.44.1 -> 2.46.0
- autoconf-archive: upgrade 2022.09.03 -> 2023.02.20
- babeltrace: upgrade 1.5.8 -> 1.5.11
- base-passwd: Update to 3.6.1
- bash: update 5.1.16 -> 5.2.15
- bind: upgrade 9.18.7 -> 9.18.12
- binutils: Upgrade to 2.40 release
- bluez: update 5.65 -> 5.66
- boost-build-native: update 1.80.0 -> 1.81.0
- boost: upgrade 1.80.0 -> 1.81.0
- btrfs-tools: upgrade 5.19.1 -> 6.1.3
- busybox: 1.35.0 -> 1.36.0
- ccache: upgrade 4.6.3 -> 4.7.4
- cmake: update 3.24.0 -> 3.25.2
- cracklib: upgrade to v2.9.10
- curl: upgrade 7.86.0 -> 8.0.1
- dbus: upgrade 1.14.0 -> 1.14.6
- diffoscope: upgrade 221 -> 236
- diffstat: upgrade 1.64 -> 1.65
- diffutils: update 3.8 -> 3.9
- dos2unix: upgrade 7.4.3 -> 7.4.4
- dpkg: update 1.21.9 -> 1.21.21
- dropbear: upgrade 2022.82 -> 2022.83
- dtc: upgrade 1.6.1 -> 1.7.0
- e2fsprogs: upgrade 1.46.5 -> 1.47.0
- ed: upgrade 1.18 -> 1.19
- elfutils: update 0.187 -> 0.188
- ell: upgrade 0.53 -> 0.56
- enchant2: upgrade 2.3.3 -> 2.3.4
- encodings: update 1.0.6 -> 1.0.7
- epiphany: update 42.4 -> 43.1
- ethtool: upgrade 5.19 -> 6.2
- expat: upgrade to 2.5.0
- ffmpeg: upgrade 5.1.1 -> 5.1.2
- file: upgrade 5.43 -> 5.44
- flac: update 1.4.0 -> 1.4.2
- font-alias: update 1.0.4 -> 1.0.5
- fontconfig: upgrade 2.14.0 -> 2.14.2
- font-util: upgrade 1.3.3 -> 1.4.0
- freetype: update 2.12.1 -> 2.13.0
- gawk: update 5.1.1 -> 5.2.1
- gcr3: update 3.40.0 -> 3.41.1
- gcr: rename gcr -> gcr3
- gdb: Upgrade to 13.1
- gdk-pixbuf: upgrade 2.42.9 -> 2.42.10
- gettext: update 0.21 -> 0.21.1
- ghostscript: update 9.56.1 -> 10.0.0
- gi-docgen: upgrade 2022.1 -> 2023.1
- git: upgrade 2.37.3 -> 2.39.2
- glib-2.0: update 2.72.3 -> 2.74.6
- glibc: upgrade to 2.37 release + stable updates
- glib-networking: update 2.72.2 -> 2.74.0
- glslang: upgrade 1.3.236.0 -> 1.3.239.0
- gnu-config: upgrade to latest revision
- gnupg: upgrade 2.3.7 -> 2.4.0
- gnutls: upgrade 3.7.7 -> 3.8.0
- gobject-introspection: upgrade 1.72.0 -> 1.74.0
- go: update 1.19 -> 1.20.1
- grep: update 3.7 -> 3.10
- gsettings-desktop-schemas: upgrade 42.0 -> 43.0
- gstreamer1.0: upgrade 1.20.3 -> 1.22.0
- gtk+3: upgrade 3.24.34 -> 3.24.36
- gtk4: update 4.8.2 -> 4.10.0
- harfbuzz: upgrade 5.1.0 -> 7.1.0
- hdparm: update 9.64 -> 9.65
- help2man: upgrade 1.49.2 -> 1.49.3
- icu: update 71.1 -> 72-1
- ifupdown: upgrade 0.8.37 -> 0.8.41
- igt-gpu-tools: upgrade 1.26 -> 1.27.1
- inetutils: upgrade 2.3 -> 2.4
- init-system-helpers: upgrade 1.64 -> 1.65.2
- iproute2: upgrade 5.19.0 -> 6.2.0
- iptables: update 1.8.8 -> 1.8.9
- iputils: update to 20221126
- iso-codes: upgrade 4.11.0 -> 4.13.0
- jquery: upgrade 3.6.0 -> 3.6.3
- kexec-tools: upgrade 2.0.25 -> 2.0.26
- kmscube: upgrade to latest revision
- libarchive: upgrade 3.6.1 -> 3.6.2
- libbsd: upgrade 0.11.6 -> 0.11.7
- libcap: upgrade 2.65 -> 2.67
- libdnf: update 0.69.0 -> 0.70.0
- libdrm: upgrade 2.4.113 -> 2.4.115
- libedit: upgrade 20210910-3.1 -> 20221030-3.1
- libepoxy: update 1.5.9 -> 1.5.10
- libffi: upgrade 3.4.2 -> 3.4.4
- libfontenc: upgrade 1.1.6 -> 1.1.7
- libgit2: upgrade 1.5.0 -> 1.6.3
- libgpg-error: update 1.45 -> 1.46
- libhandy: update 1.6.3 -> 1.8.1
- libical: upgrade 3.0.14 -> 3.0.16
- libice: update 1.0.10 -> 1.1.1
- libidn2: upgrade 2.3.3 -> 2.3.4
- libinput: upgrade 1.19.4 -> 1.22.1
- libjpeg-turbo: upgrade 2.1.4 -> 2.1.5.1
- libksba: upgrade 1.6.0 -> 1.6.3
- libmicrohttpd: upgrade 0.9.75 -> 0.9.76
- libmodule-build-perl: update 0.4231 -> 0.4232
- libmpc: upgrade 1.2.1 -> 1.3.1
- libnewt: update 0.52.21 -> 0.52.23
- libnotify: upgrade 0.8.1 -> 0.8.2
- libpcap: upgrade 1.10.1 -> 1.10.3
- libpciaccess: update 0.16 -> 0.17
- libpcre2: upgrade 10.40 -> 10.42
- libpipeline: upgrade 1.5.6 -> 1.5.7
- libpng: upgrade 1.6.38 -> 1.6.39
- libpsl: upgrade 0.21.1 -> 0.21.2
- librepo: upgrade 1.14.5 -> 1.15.1
- libsdl2: upgrade 2.24.1 -> 2.26.3
- libsm: 1.2.3 > 1.2.4
- libsndfile1: upgrade 1.1.0 -> 1.2.0
- libsolv: upgrade 0.7.22 -> 0.7.23
- libsoup-2.4: upgrade 2.74.2 -> 2.74.3
- libsoup: upgrade 3.0.7 -> 3.2.2
- libtest-fatal-perl: upgrade 0.016 -> 0.017
- libtest-needs-perl: upgrade 0.002009 -> 0.002010
- libunistring: upgrade 1.0 -> 1.1
- liburcu: upgrade 0.13.2 -> 0.14.0
- liburi-perl: upgrade 5.08 -> 5.17
- libva: upgrade 2.15.0 -> 2.16.0
- libva-utils: upgrade 2.15.0 -> 2.17.1
- libwebp: upgrade 1.2.4 -> 1.3.0
- libwpe: upgrade 1.12.3 -> 1.14.1
- libx11: 1.8.1 -> 1.8.4
- libx11-compose-data: 1.6.8 -> 1.8.4
- libxau: upgrade 1.0.10 -> 1.0.11
- libxcomposite: update 0.4.5 -> 0.4.6
- libxcrypt-compat: upgrade 4.4.30 -> 4.4.33
- libxcrypt: upgrade 4.4.28 -> 4.4.30
- libxdamage: update 1.1.5 -> 1.1.6
- libxdmcp: update 1.1.3 -> 1.1.4
- libxext: update 1.3.4 -> 1.3.5
- libxft: update 2.3.4 -> 2.3.6
- libxft: upgrade 2.3.6 -> 2.3.7
- libxinerama: update 1.1.4 -> 1.1.5
- libxkbcommon: upgrade 1.4.1 -> 1.5.0
- libxkbfile: update 1.1.0 -> 1.1.1
- libxkbfile: upgrade 1.1.1 -> 1.1.2
- libxml2: upgrade 2.9.14 -> 2.10.3
- libxmu: update 1.1.3 -> 1.1.4
- libxpm: update 3.5.13 -> 3.5.15
- libxrandr: update 1.5.2 -> 1.5.3
- libxrender: update 0.9.10 -> 0.9.11
- libxres: update 1.2.1 -> 1.2.2
- libxscrnsaver: update 1.2.3 -> 1.2.4
- libxshmfence: update 1.3 -> 1.3.2
- libxslt: upgrade 1.1.35 -> 1.1.37
- libxtst: update 1.2.3 -> 1.2.4
- libxv: update 1.0.11 -> 1.0.12
- libxxf86vm: update 1.1.4 -> 1.1.5
- lighttpd: upgrade 1.4.66 -> 1.4.69
- linux-firmware: upgrade 20220913 -> 20230210
- linux-libc-headers: bump to 6.1
- linux-yocto/5.15: update genericx86* machines to v5.15.103
- linux-yocto/5.15: update to v5.15.108
- linux-yocto/6.1: update to v6.1.25
- linux-yocto-dev: bump to v6.3
- linux-yocto-rt/5.15: update to -rt59
- linux-yocto-rt/6.1: update to -rt7
- llvm: update 14.0.6 -> 15.0.7
- log4cplus: upgrade 2.0.8 -> 2.1.0
- logrotate: upgrade 3.20.1 -> 3.21.0
- lsof: upgrade 4.95.0 -> 4.98.0
- ltp: upgrade 20220527 -> 20230127
- lttng-modules: upgrade 2.13.4 -> 2.13.9
- lttng-tools: update 2.13.8 -> 2.13.9
- lttng-ust: upgrade 2.13.4 -> 2.13.5
- makedepend: upgrade 1.0.6 -> 1.0.8
- make: update 4.3 -> 4.4.1
- man-db: update 2.10.2 -> 2.11.2
- man-pages: upgrade 5.13 -> 6.03
- matchbox-config-gtk: Update to latest SRCREV
- matchbox-desktop-2: Update 2.2 -> 2.3
- matchbox-panel-2: Update 2.11 -> 2.12
- matchbox-terminal: Update to latest SRCREV
- matchbox-wm: Update 1.2.2 -> 1.2.3
- mc: update 4.8.28 -> 4.8.29
- mesa: update 22.2.0 -> 23.0.0
- meson: upgrade 0.63.2 -> 1.0.1
- mmc-utils: upgrade to latest revision
- mobile-broadband-provider-info: upgrade 20220725 -> 20221107
- mpfr: upgrade 4.1.0 -> 4.2.0
- mpg123: upgrade 1.30.2 -> 1.31.2
- msmtp: upgrade 1.8.22 -> 1.8.23
- mtd-utils: upgrade 2.1.4 -> 2.1.5
- mtools: upgrade 4.0.40 -> 4.0.42
- musl-obstack: Update to 1.2.3
- musl: Upgrade to latest master
- nasm: update 2.15.05 -> 2.16.01
- ncurses: upgrade 6.3+20220423 -> 6.4
- netbase: upgrade 6.3 -> 6.4
- newlib: Upgrade 4.2.0 -> 4.3.0
- nghttp2: upgrade 1.49.0 -> 1.52.0
- numactl: upgrade 2.0.15 -> 2.0.16
- opensbi: Upgrade to 1.2 release
- openssh: upgrade 9.0p1 -> 9.3p1
- openssl: Upgrade 3.0.5 -> 3.1.0
- opkg: upgrade to version 0.6.1
- orc: upgrade 0.4.32 -> 0.4.33
- ovmf: upgrade edk2-stable202205 -> edk2-stable202211
- pango: upgrade 1.50.9 -> 1.50.13
- patchelf: upgrade 0.15.0 -> 0.17.2
- pciutils: upgrade 3.8.0 -> 3.9.0
- piglit: upgrade to latest revision
- pinentry: update 1.2.0 -> 1.2.1
- pixman: upgrade 0.40.0 -> 0.42.2
- pkgconf: upgrade 1.9.3 -> 1.9.4
- popt: update 1.18 -> 1.19
- powertop: upgrade 2.14 -> 2.15
- procps: update 3.3.17 -> 4.0.3
- psmisc: upgrade 23.5 -> 23.6
- puzzles: upgrade to latest revision
- python3-alabaster: upgrade 0.7.12 -> 0.7.13
- python3-attrs: upgrade 22.1.0 -> 22.2.0
- python3-babel: upgrade 2.10.3 -> 2.12.1
- python3-bcrypt: upgrade 3.2.2 -> 4.0.1
- python3-certifi: upgrade 2022.9.14 -> 2022.12.7
- python3-chardet: upgrade 5.0.0 -> 5.1.0
- python3-cryptography: upgrade 38.0.3 -> 39.0.4
- python3-cryptography-vectors: upgrade 37.0.4 -> 39.0.2
- python3-cython: upgrade 0.29.32 -> 0.29.33
- python3-dbusmock: update 0.28.4 -> 0.28.7
- python3-dbus: upgrade 1.2.18 -> 1.3.2
- python3-dtschema: upgrade 2022.8.3 -> 2023.1
- python3-flit-core: upgrade 3.7.1 -> 3.8.0
- python3-gitdb: upgrade 4.0.9 -> 4.0.10
- python3-git: upgrade 3.1.27 -> 3.1.31
- python3-hatch-fancy-pypi-readme: upgrade 22.7.0 -> 22.8.0
- python3-hatchling: upgrade 1.9.0 -> 1.13.0
- python3-hatch-vcs: upgrade 0.2.0 -> 0.3.0
- python3-hypothesis: upgrade 6.54.5 -> 6.68.2
- python3-importlib-metadata: upgrade 4.12.0 -> 6.0.0
- python3-iniconfig: upgrade 1.1.1 -> 2.0.0
- python3-installer: update 0.5.1 -> 0.6.0
- python3-iso8601: upgrade 1.0.2 -> 1.1.0
- python3-jsonschema: upgrade 4.9.1 -> 4.17.3
- python3-lxml: upgrade 4.9.1 -> 4.9.2
- python3-mako: upgrade 1.2.2 -> 1.2.4
- python3-markupsafe: upgrade 2.1.1 -> 2.1.2
- python3-more-itertools: upgrade 8.14.0 -> 9.1.0
- python3-numpy: upgrade 1.23.3 -> 1.24.2
- python3-packaging: upgrade to 23.0
- python3-pathspec: upgrade 0.10.1 -> 0.11.0
- python3-pbr: upgrade 5.10.0 -> 5.11.1
- python3-pip: upgrade 22.2.2 -> 23.0.1
- python3-poetry-core: upgrade 1.0.8 -> 1.5.2
- python3-psutil: upgrade 5.9.2 -> 5.9.4
- python3-pycairo: upgrade 1.21.0 -> 1.23.0
- python3-pycryptodome: upgrade 3.15.0 -> 3.17
- python3-pycryptodomex: upgrade 3.15.0 -> 3.17
- python3-pygments: upgrade 2.13.0 -> 2.14.0
- python3-pyopenssl: upgrade 22.0.0 -> 23.0.0
- python3-pyrsistent: upgrade 0.18.1 -> 0.19.3
- python3-pytest-subtests: upgrade 0.8.0 -> 0.10.0
- python3-pytest: upgrade 7.1.3 -> 7.2.2
- python3-pytz: upgrade 2022.2.1 -> 2022.7.1
- python3-requests: upgrade 2.28.1 -> 2.28.2
- python3-scons: upgrade 4.4.0 -> 4.5.2
- python3-setuptools-rust: upgrade 1.5.1 -> 1.5.2
- python3-setuptools-scm: upgrade 7.0.5 -> 7.1.0
- python3-setuptools: upgrade 65.0.2 -> 67.6.0
- python3-sphinxcontrib-applehelp: update 1.0.2 -> 1.0.4
- python3-sphinxcontrib-htmlhelp: 2.0.0 -> 2.0.1
- python3-sphinx-rtd-theme: upgrade 1.0.0 -> 1.2.0
- python3-sphinx: upgrade 5.1.1 -> 6.1.3
- python3-subunit: upgrade 1.4.0 -> 1.4.2
- python3-testtools: upgrade 2.5.0 -> 2.6.0
- python3-typing-extensions: upgrade 4.3.0 -> 4.5.0
- python3: update 3.10.6 -> 3.11.2
- python3-urllib3: upgrade 1.26.12 -> 1.26.15
- python3-wcwidth: upgrade 0.2.5 -> 0.2.6
- python3-wheel: upgrade 0.37.1 -> 0.40.0
- python3-zipp: upgrade 3.8.1 -> 3.15.0
- qemu: update 7.1.0 -> 7.2.0
- quota: update 4.06 -> 4.09
- readline: update 8.1.2 -> 8.2
- repo: upgrade 2.29.2 -> 2.32
- rgb: update 1.0.6 -> 1.1.0
- rng-tools: upgrade 6.15 -> 6.16
- rsync: update 3.2.5 -> 3.2.7
- rt-tests: update 2.4 -> 2.5
- ruby: update 3.1.2 -> 3.2.1
- rust: update 1.63.0 -> 1.68.1
- rxvt-unicode: upgrade 9.30 -> 9.31
- sed: update 4.8 -> 4.9
- shaderc: upgrade 2022.2 -> 2023.2
- shadow: update 4.12.1 -> 4.13
- socat: upgrade 1.7.4.3 -> 1.7.4.4
- spirv-headers: upgrade 1.3.236.0 -> 1.3.239.0
- spirv-tools: upgrade 1.3.236.0 -> 1.3.239.0
- sqlite3: upgrade 3.39.3 -> 3.41.0
- strace: upgrade 5.19 -> 6.2
- stress-ng: update 0.14.03 -> 0.15.06
- sudo: upgrade 1.9.11p3 -> 1.9.13p3
- swig: update 4.0.2 -> 4.1.1
- sysstat: upgrade 12.6.0 -> 12.6.2
- systemd: update 251.4 -> 253.1
- systemtap: upgrade 4.7 -> 4.8
- taglib: upgrade 1.12 -> 1.13
- tcf-agent: Update to current version
- tcl: update 8.6.11 -> 8.6.13
- texinfo: update 6.8 -> 7.0.2
- tiff: update 4.4.0 -> 4.5.0
- tzdata: update 2022d -> 2023c
- u-boot: upgrade 2022.07 -> 2023.01
- unfs: update 0.9.22 -> 0.10.0
- usbutils: upgrade 014 -> 015
- util-macros: upgrade 1.19.3 -> 1.20.0
- vala: upgrade 0.56.3 -> 0.56.4
- valgrind: update to 3.20.0
- vim: Upgrade 9.0.0598 -> 9.0.1429
- virglrenderer: upgrade 0.10.3 -> 0.10.4
- vte: update 0.68.0 -> 0.72.0
- vulkan-headers: upgrade 1.3.236.0 -> 1.3.239.0
- vulkan-loader: upgrade 1.3.236.0 -> 1.3.239.0
- vulkan-samples: update to latest revision
- vulkan-tools: upgrade 1.3.236.0 -> 1.3.239.0
- vulkan: update 1.3.216.0 -> 1.3.236.0
- wayland-protocols: upgrade 1.26 -> 1.31
- wayland-utils: update 1.0.0 -> 1.1.0
- webkitgtk: update 2.36.7 -> 2.38.5
- weston: update 10.0.2 -> 11.0.1
- wireless-regdb: upgrade 2022.08.12 -> 2023.02.13
- wpebackend-fdo: upgrade 1.12.1 -> 1.14.0
- xcb-util: update 0.4.0 -> 0.4.1
- xcb-util-keysyms: 0.4.0 -> 0.4.1
- xcb-util-renderutil: 0.3.9 -> 0.3.10
- xcb-util-wm: 0.4.1 -> 0.4.2
- xcb-util-image: 0.4.0 -> 0.4.1
- xf86-input-mouse: update 1.9.3 -> 1.9.4
- xf86-input-vmmouse: update 13.1.0 -> 13.2.0
- xf86-video-vesa: update 2.5.0 -> 2.6.0
- xf86-video-vmware: update 13.3.0 -> 13.4.0
- xhost: update 1.0.8 -> 1.0.9
- xinit: update 1.4.1 -> 1.4.2
- xkbcomp: update 1.4.5 -> 1.4.6
- xkeyboard-config: upgrade 2.36 -> 2.38
- xprop: update 1.2.5 -> 1.2.6
- xrandr: upgrade 1.5.1 -> 1.5.2
- xserver-xorg: upgrade 21.1.4 -> 21.1.7
- xset: update 1.2.4 -> 1.2.5
- xvinfo: update 1.1.4 -> 1.1.5
- xwayland: upgrade 22.1.3 -> 22.1.8
- xz: upgrade 5.2.6 -> 5.4.2
- zlib: upgrade 1.2.12 -> 1.2.13
- zstd: upgrade 1.5.2 -> 1.5.4

Contributors to 4.2
~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

- Adrian Freihofer
- Ahmad Fatoum
- Alejandro Hernandez Samaniego
- Alexander Kanavin
- Alexandre Belloni
- Alexey Smirnov
- Alexis Lothoré
- Alex Kiernan
- Alex Stewart
- Andrej Valek
- Andrew Geissler
- Anton Antonov
- Antonin Godard
- Archana Polampalli
- Armin Kuster
- Arnout Vandecappelle
- Arturo Buzarra
- Atanas Bunchev
- Benjamin Szőke
- Benoît Mauduit
- Bernhard Rosenkränzer
- Bruce Ashfield
- Caner Altinbasak
- Carlos Alberto Lopez Perez
- Changhyeok Bae
- Changqing Li
- Charlie Johnston
- Chase Qi
- Chee Yang Lee
- Chen Qi
- Chris Elledge
- Christian Eggers
- Christoph Lauer
- Chuck Wolber
- Ciaran Courtney
- Claus Stovgaard
- Clément Péron
- Daniel Ammann
- David Bagonyi
- Denys Dmytriyenko
- Denys Zagorui
- Diego Sueiro
- Dmitry Baryshkov
- Ed Tanous
- Enguerrand de Ribaucourt
- Enrico Jörns
- Enrico Scholz
- Etienne Cordonnier
- Fabio Estevam
- Fabre Sébastien
- Fawzi KHABER
- Federico Pellegrin
- Frank de Brabander
- Frederic Martinsons
- Geoffrey GIRY
- George Kelly
- Harald Seiler
- He Zhe
- Hitendra Prajapati
- Jagadeesh Krishnanjanappa
- James Raphael Tiovalen
- Jan Kircher
- Jan Luebbe
- Jan-Simon Moeller
- Javier Tia
- Jeremy Puhlman
- Jermain Horsman
- Jialing Zhang
- Joel Stanley
- Joe Slater
- Johan Korsnes
- Jon Mason
- Jordan Crouse
- Jose Quaresma
- Joshua Watt
- Justin Bronder
- Kai Kang
- Kasper Revsbech
- Keiya Nobuta
- Kenfe-Mickael Laventure
- Kevin Hao
- Khem Raj
- Konrad Weihmann
- Lei Maohui
- Leon Anavi
- Liam Beguin
- Louis Rannou
- Luca Boccassi
- Luca Ceresoli
- Luis Martins
- Maanya Goenka
- Marek Vasut
- Mark Asselstine
- Mark Hatle
- Markus Volk
- Marta Rybczynska
- Martin Jansa
- Martin Larsson
- Mateusz Marciniec
- Mathieu Dubois-Briand
- Mauro Queiros
- Maxim Uvarov
- Michael Halstead
- Michael Opdenacker
- Mike Crowe
- Mikko Rapeli
- Ming Liu
- Mingli Yu
- Narpat Mali
- Nathan Rossi
- Niko Mauno
- Ola x Nilsson
- Oliver Lang
- Ovidiu Panait
- Pablo Saavedra
- Patrick Williams
- Paul Eggleton
- Paulo Neves
- Pavel Zhukov
- Pawel Zalewski
- Pedro Baptista
- Peter Bergin
- Peter Kjellerstedt
- Peter Marko
- Petr Kubizňák
- Petr Vorel
- pgowda
- Piotr Łobacz
- Quentin Schulz
- Randy MacLeod
- Ranjitsinh Rathod
- Ravineet Singh
- Ravula Adhitya Siddartha
- Richard Elberger
- Richard Leitner
- Richard Purdie
- Robert Andersson
- Robert Joslyn
- Robert Yang
- Romuald JEANNE
- Ross Burton
- Ryan Eatmon
- Sakib Sajal
- Sandeep Gundlupet Raju
- Saul Wold
- Sean Anderson
- Sergei Zhmylev
- Siddharth Doshi
- Soumya
- Sudip Mukherjee
- Sundeep KOKKONDA
- Teoh Jay Shen
- Thomas De Schampheleire
- Thomas Perrot
- Thomas Roos
- Tim Orling
- Tobias Hagelborn
- Tom Hochstein
- Trevor Woerner
- Ulrich Ölmann
- Vincent Davis Jr
- Vivek Kumbhar
- Vyacheslav Yurkov
- Wang Mingyu
- Wentao Zhang
- Xiangyu Chen
- Xiaotian Wu
- Yan Xinkuan
- Yash Shinde
- Yi Zhao
- Yoann Congal
- Yureka Lilian
- Zang Ruochen
- Zheng Qiu
- Zheng Ruoqin
- Zoltan Boszormenyi
- 张忠山


Repositories / Downloads for Yocto-4.2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`mickledore </poky/log/?h=mickledore>`
-  Tag:  :yocto_git:`yocto-4.2 </poky/log/?h=yocto-4.2>`
-  Git Revision: :yocto_git:`21790e71d55f417f27cd51fae9dd47549758d4a0 </poky/commit/?id=21790e71d55f417f27cd51fae9dd47549758d4a0>`
-  Release Artefact: poky-21790e71d55f417f27cd51fae9dd47549758d4a0
-  sha: 38606076765d912deec84e523403709ef1249122197e61454ae08818e60f83c2
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2/poky-21790e71d55f417f27cd51fae9dd47549758d4a0.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2/poky-21790e71d55f417f27cd51fae9dd47549758d4a0.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`mickledore </openembedded-core/log/?h=mickledore>`
-  Tag:  :oe_git:`yocto-4.2 </openembedded-core/log/?h=yocto-4.2>`
-  Git Revision: :oe_git:`c57d1a561db563ed2f521bbac5fc12d4ac8e11a7 </openembedded-core/commit/?id=c57d1a561db563ed2f521bbac5fc12d4ac8e11a7>`
-  Release Artefact: oecore-c57d1a561db563ed2f521bbac5fc12d4ac8e11a7
-  sha: e8cdd870492017be7e7b74b8c2fb73ae6771b2d2125b2aa1f0e65d0689f96af8
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2/oecore-c57d1a561db563ed2f521bbac5fc12d4ac8e11a7.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2/oecore-c57d1a561db563ed2f521bbac5fc12d4ac8e11a7.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`mickledore </meta-mingw/log/?h=mickledore>`
-  Tag:  :yocto_git:`yocto-4.2 </meta-mingw/log/?h=yocto-4.2>`
-  Git Revision: :yocto_git:`250617ffa524c082b848487359b9d045703d59c2 </meta-mingw/commit/?id=250617ffa524c082b848487359b9d045703d59c2>`
-  Release Artefact: meta-mingw-250617ffa524c082b848487359b9d045703d59c2
-  sha: 873a97dfd5ed6fb26e1f6a2ddc2c0c9d7a7b3c7f5018588e912294618775c323
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2/meta-mingw-250617ffa524c082b848487359b9d045703d59c2.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2/meta-mingw-250617ffa524c082b848487359b9d045703d59c2.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.4 </bitbake/log/?h=2.4>`
-  Tag:  :oe_git:`yocto-4.2 </bitbake/log/?h=yocto-4.2>`
-  Git Revision: :oe_git:`d97d62e2cbe4bae17f0886f3b4759e8f9ba6d38c </bitbake/commit/?id=d97d62e2cbe4bae17f0886f3b4759e8f9ba6d38c>`
-  Release Artefact: bitbake-d97d62e2cbe4bae17f0886f3b4759e8f9ba6d38c
-  sha: 5edcb97cb545011226b778355bb840ebcc790552d4a885a0d83178153697ba7a
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2/bitbake-d97d62e2cbe4bae17f0886f3b4759e8f9ba6d38c.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2/bitbake-d97d62e2cbe4bae17f0886f3b4759e8f9ba6d38c.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`mickledore </yocto-docs/log/?h=mickledore>`
-  Tag: :yocto_git:`yocto-4.2 </yocto-docs/log/?h=yocto-4.2>`
-  Git Revision: :yocto_git:`4d6807e34adf5d92d9b6e5852736443a867c78fa </yocto-docs/commit/?id=4d6807e34adf5d92d9b6e5852736443a867c78fa>`

