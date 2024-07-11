.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 5.0 (scarthgap)
---------------------------------

New Features / Enhancements in 5.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Linux kernel 6.6, gcc 13.2, glibc 2.39, LLVM 18.1, and over 300 other recipe upgrades

-  New variables:

   -  :term:`CVE_DB_INCR_UPDATE_AGE_THRES`: Configure the maximum age of the
      internal CVE database for incremental update (instead of a full
      redownload).

   -  :term:`RPMBUILD_EXTRA_PARAMS`: support extra user-defined fields without
      crashing the RPM package creation.

   -  :term:`OPKG_MAKE_INDEX_EXTRA_PARAMS`: support extra parameters for
      ``opkg-make-index``.

   -  :term:`EFI_UKI_PATH`, :term:`EFI_UKI_DIR`: define the location of UKI
      image in the EFI System partition.

   -  :term:`TARGET_DBGSRC_DIR`: specifies the target path to debug source files

   -  :term:`USERADD_DEPENDS`: provides a way to declare dependencies on the users
      and/or groups created by other recipes, resolving a long-standing build
      ordering issue

-  Architecture-specific enhancements:

   -  ``genericarm64``: a new :term:`MACHINE` to represent a 64-bit General Arm
      SystemReady platform.

   -  Add Power8 tune to PowerPC architecture.

   -  ``arch-armv9``: remove CRC and SVE tunes, since FEAT_CRC32 is now mandatory
      and SVE/SVE2 are enabled by default in GCC's ``-march=armv9-a``.

   -  ``arm/armv*``: add all of the additional Arm tunes in GCC 13.2.0

-  Kernel-related enhancements:

   -  The default kernel is the current LTS (6.6).

   -  Add support for ``genericarm64``.

-  New core recipes:

   -  `bmaptool <https://github.com/yoctoproject/bmaptool>`__: a tool for
      creating block maps for files and flashing images, being now under the
      Yocto Project umbrella.

   -  ``core-image-initramfs-boot``: a minimal initramfs image, containing just
      ``udev`` and ``init``, designed to find the main root filesystem and
      pivot to it.

   -  `lzlib <https://www.nongnu.org/lzip/lzlib.html>`__: a data compression
      library that provides LZMA compression and decompression functions.

   -  `lzop <https://www.lzop.org/>`__: a compression utility based on the LZO
      library, that was brought back after a (now reverted) removal.

   -  `python3-jsonschema-specifications <https://pypi.org/project/jsonschema-specifications/>`__:
      support files for JSON Schema Specifications (meta-schemas and
      vocabularies), added as a new dependency of ``python3-jsonschema``.

   -  `python3-maturin <https://github.com/pyo3/maturin>`__: a project that
      allows building and publishing Rust crates as Python packages.

   -  `python3-meson-python <https://github.com/mesonbuild/meson-python>`__: a
      Python build backend that enables the Meson build-system for Python packages.

   -  `python3-pyproject-metadata <https://pypi.org/project/pyproject-metadata/>`__:
      a class to handle PEP 621 metadata, and a dependency for
      ``python3-meson-python``.

   -  `python3-referencing <https://github.com/python-jsonschema/referencing>`__:
      another dependency of ``python3-jsonschema``, it provides an
      implementation of JSON reference resolution.

   -  `python3-rpds-py <https://pypi.org/project/rpds-py/>`__: Python bindings
      to the Rust rpds crate, and a runtime dependency for ``python3-referencing``.

   -  `python3-sphinxcontrib-jquery <https://pypi.org/project/sphinxcontrib-jquery/>`__:
      a Sphinx extension to include jQuery on newer Sphinx releases. Recent
      versions of ``python3-sphinx-rtd-theme`` depend on it.

   -  `python3-websockets <https://pypi.org/project/websockets/>`__: a
      library for building WebSocket servers and clients in Python.

   -  `python3-yamllint <https://github.com/adrienverge/yamllint>`__: a linter
      for YAML files. In U-Boot, the ``binman`` tool uses this linter to verify the
      configurations at compile time.

   -  ``systemd-boot-native``: a UEFI boot manager, this time built as native to
      provide the ``ukify`` tool.

   -  `utfcpp <https://github.com/nemtrif/utfcpp>`__: a C++ library to handle
      UTF-8 encoded strings. It was added as a dependency for ``taglib`` after
      its upgrade to v2.0.

   -  `vulkan-utility-libraries <https://github.com/KhronosGroup/Vulkan-Utility-Libraries>`__:
      a set of libraries to share code across various Vulkan repositories.

   -  `vulkan-volk <https://github.com/zeux/volk>`__: a meta-loader for Vulkan,
      needed to support building the latest ``vulkan-tools``.

-  QEMU / ``runqemu`` enhancements:

   -  QEMU has been upgraded to version 8.2.1

   -  ``qemuboot``: support predictable network interface names.

   -  ``runqemu``: match ".rootfs." in addition to "-image-" for the root
      filesystem.

   -  :ref:`ref-classes-cmake-qemu`: a new class allowing to execute cross-compiled
      binaries using QEMU user-mode emulation.

-  Rust improvements:

   -  Rust has been upgraded to version 1.75

   -  The Rust profiler (i.e., PGO - Profile-Guided Optimization) options were
      enabled back.

   -  The Rust ``oe-selftest`` were enabled, except for ``mips32`` whose tests
      are skipped.

   -  ``rust-cross-canadian``: added ``riscv64`` to cross-canadian hosts.

-  wic Image Creator enhancements:

   -  Allow the imager's output file extension to match the imager's name,
      instead of hardcoding it to ``direct`` (i.e., the default imager)

   -  For GPT-based disks, add reproducible Disk GUID generation

   -  Allow generating reproducible ext4 images

   -  Add feature to fill a specific range of a partition with zeros

   -  ``bootimg-efi``: add ``install-kernel-into-boot-dir`` parameter to
      configure kernel installation point(s) (i.e., rootfs and/or boot partition)

   -  ``rawcopy``: add support for zstd decompression

-  SDK-related improvements:

   -  ``nativesdk``: let :term:`MACHINE_FEATURES` be set by ``machine-sdk``
      configuration files.

   -  ``nativesdk``: prevent :term:`MACHINE_FEATURES` and :term:`DISTRO_FEATURES`
      from being backfilled.

   -  Support for ``riscv64`` as an SDK host architecture

   -  Extend recipes to ``nativesdk``: ``acpica``, ``libpcap``, ``python3-setuptools-rust``

-  Testing:

   -  Move `patchtest` to the core (as ``scripts/patchtest``, test cases under
      ``meta/lib/patchtest/tests``) and make a number of improvements to enable
      it to validate patches submitted on the mailing list again. Additionally,
      make it work with the original upstream version of
      `Patchwork <http://jk.ozlabs.org/projects/patchwork/>`__.

   -  Add an optional ``unimplemented-ptest`` QA warning to detect upstream
      packages with tests, that do not use ptest.

   -  ``testimage``: retrieve the ptests directory, especially for the logs,
      upon ptest failure.

   -  ``oeqa``, ``oe-selftest``: add test cases for Maturin (SDK and runtime).

   -  Proof-of-concept of screenshot-based runtime UI test
      (``meta/lib/oeqa/runtime/cases/login.py``)

   -  Enable ptests for ``python3-attrs``, ``python3-pyyaml``, ``xz``

-  Utility script changes:

   -  ``oe-init-build-env`` can generate a initial configuration (``.vscode``)
      for VSCode and its "Yocto Project BitBake" extension.

   -  The ``sstate-cache-management`` script has been rewritten in python for better performance and maintainability

   -  ``bitbake-layers``: added an option to update the reference of repositories in layer setup

-  BitBake improvements:

   -  New ``inherit_defer`` statement which works as
      :ref:`inherit <bitbake:bitbake-user-manual/bitbake-user-manual-metadata:\`\`inherit\`\` directive>`
      does, except that it is only evaluated at the end of parsing
      --- recommended where a conditional expression is used, e.g.::

         inherit_defer ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3targetconfig', '', d)}

      This allows conditional expressions to be evaluated 'late' meaning changes
      to the variable after the line is parsed will take effect - with inherit this
      is not the case.

   -  Add support for :term:`BB_LOADFACTOR_MAX`, so Bitbake can stop running
      extra tasks if the system load is too high, especially in distributions
      where ``/proc/pressure`` is disabled.

   -  ``taskexp_ncurses``: add ncurses version of ``taskexp``, the dependency
      explorer originally implemented with GTK.

   -  Improve ``runqueue`` performance by adding a cache mechanism in
      ``build_taskdepdata``.

   -  ``bitbake.conf``: add ``runtimedir`` to represent the path to the runtime
      state directory (i.e., ``/run``).

   -  Allow to disable colored text output through the
      `NO_OUTPUT <https://no-color.org/>`__ environment variable.

   -  ``git-make-shallow`` script: add support for Git's ``safe.bareRepository=explicit``
      configuration setting.

   -  Hash equivalence gained a number of scalability improvements including:

      -  Support for a wide range of database backends through `SQLAlchemy`

      -  Support for hash equivalence server and client to communicate over websockets

      -  Support for per-user permissions in the hashserver, and on the client side
         specifying credentials via the environment or ``.netrc``

      -  Add garbage collection to remove unused unihashes from the database.

-  devtool improvements:

   -  Introduce a new ``ide-sdk`` plugin to generate a configuration to use
      the eSDK through an IDE.

   -  Add ``--no-pypi`` option for Python projects that are not hosted on PyPI.

   -  Add support for Git submodules.

   -  ``ide``: ``vscode``: generate files from recipe sysroots and debug the
      root filesystem in read-only mode to avoid confusion.

   -  ``modify``: add support for multiple sources in :term:`SRC_URI`.

   -  Support plugins within plugins.

-  recipetool improvements:

   - ``appendsrcfile(s)``: add a mode to update the recipe itself.

   - ``appendsrcfile(s)``: add ``--dry-run`` mode.

   - ``create``: add handler to create Go recipes.

   - ``create``: improve identification of licenses.

   - ``create``: add support for modern Python PEP-517 build systems including
     hatchling, maturin, meson-python.

   - ``create``: add PyPi support.

   - ``create``: prefix created Python recipes with ``python3-``.

-  Packaging changes:

   -  ``package_rpm``: the RPM package compressor's mode can now be overriden.

   -  ipk packaging (using ``opkg``) now uses ``zstd`` compression instead of
      ``xz`` for better compression and performance.

-  Security improvements:

   -  Improve incremental CVE database download from NVD. Rejected CVEs are
      removed, configuration is kept up-to-date. The age threshold for
      incremental update can be configured with :term:`CVE_DB_INCR_UPDATE_AGE_THRES`
      variable.

-  Toaster Web UI improvements:

   - Numerous bugfixes, and additional input validation

   - Add `pytest` support and add/update test cases

-  Prominent documentation updates:

   -  Documentation for using the new ``devtool ide-sdk`` command and features.
      See :ref:`using_devtool` for details.

   -  New ":doc:`bitbake:bitbake-user-manual/bitbake-user-manual-ref-variables-context`"
      section in the BitBake User Manual.

   -  New ``make stylecheck`` command to run `Vale <https://vale.sh>`__,
      to perform text style checks and comply with text writing standards in
      the industry.

   -  New ``make sphinx-lint`` command to run `sphinx-lint
      <https://github.com/sphinx-contrib/sphinx-lint>`__. After customization,
      this will allow us to enforce Sphinx syntax style choices.

-  Miscellaneous changes:

   -  Systemd's following :term:`PACKAGECONFIG` options were added:
      ``cryptsetup-plugins``, ``no-ntp-fallback``, and ``p11kit``.

   -  New PACKAGECONFIG options added to ``libarchive``, ``libinput``,
      ``libunwind``, ``mesa``, ``mesa-gl``, ``openssh``, ``perf``,
      ``python3-pyyaml``, ``qemu``, ``rpm``, ``shadow``, ``strace``,
      ``syslinux``, ``systemd``, ``vte``, ``webkitgtk``, ``xserver-xorg``.

   -  ``systemd-boot`` can, from now on, be compiled as ``native``, thus
      providing ``ukify`` tool to build UKI images.

   -  systemd: split bash completion for ``udevadm`` in a new
      ``udev-bash-completion`` package.

   -  The :ref:`ref-classes-go-vendor` class was added to support offline builds
      (i.e., vendoring). It can also handle modules from the same repository,
      taking into account their versions.

   -  Disable strace support of bluetooth by default.

   -  ``openssh`` now has a systemd service: ``sshd.service``.

   -  The :ref:`ref-classes-python_mesonpy` class was added (moved in from
      ``meta-python``) to support Python package builds using the meson-python
      PEP-517 build backend.

   -  Support for unpacking ``.7z`` archives in :term:`SRC_URI` using ``p7zip``.

   -  Add minimal VS Code configuration to avoid VS Code's indexer from choking
      on build directories.


Known Issues in 5.0
~~~~~~~~~~~~~~~~~~~

-  ``gpgme`` has had Python binding support disabled since upstream does not yet support Python 3.12.


Recipe License changes in 5.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following corrections have been made to the :term:`LICENSE` values set by recipes:

-  ``elfutils``: split license for libraries & backend and utilities.
-  ``ghostscript``: correct :term:`LICENSE` to ``AGPL-3.0-or-later``.
-  ``kbd``: update license for consolefont and keymaps.
-  ``libsystemd``: set its own :term:`LICENSE` value (``LGPL-2.1-or-later``) to add more granularity.
-  ``libtest-warnings-perl``: update :term:`LICENSE` ``Artistic-1.0`` to ``Artistic-1.0-Perl``.
-  ``linux-firmware``: set package :term:`LICENSE` appropriately for ``carl9170``, ``rockchip`` and ``powerpr``.
-  ``newlib``: add license ``Apache-2.0-with-LLVM-exception``.
-  ``python3-poetry-core``: add license ``BSD-3-Clause`` for ``fastjsonschema``.
-  ``systemd``: make the scope of ``LGPL`` more accurate (``LGPL-2.1`` -> ``LGPL-2.1-or-later``).
-  ``util-linux``: add ``GPL-1.0-or-later`` license for fdisk and ``MIT`` license for ``flock``.
-  ``zstd``: set to dual-licensed ``BSD-3-Clause`` or ``GPL-2.0-only``.

Security Fixes in 5.0
~~~~~~~~~~~~~~~~~~~~~

-  avahi: :cve:`2023-1981`, :cve:`2023-38469`, :cve:`2023-38470`, :cve:`2023-38471`, :cve:`2023-38469`, :cve:`2023-38470`, :cve:`2023-38471`, :cve:`2023-38472`, :cve:`2023-38473`
-  bind: :cve:`2023-4408`, :cve:`2023-5517`, :cve:`2023-5679`, :cve:`2023-50387`
-  bluez5: :cve:`2023-45866`
-  coreutils: :cve:`2024-0684`
-  cups: :cve:`2023-4504`
-  curl: :cve:`2023-46218`
-  expat: :cve:`2024-28757`
-  gcc: :cve:`2023-4039`
-  glibc: :cve:`2023-5156`, :cve:`2023-0687`
-  gnutls: :cve:`2024-0553`, :cve:`2024-0567`, :cve:`2024-28834`, :cve:`2024-28835`
-  go: :cve:`2023-45288`
-  grub: :cve:`2023-4692`, :cve:`2023-4693`
-  grub2: :cve:`2023-4001` (ignored), :cve:`2024-1048` (ignored)
-  libgit2: :cve:`2024-24575`, :cve:`2024-24577`
-  libsndfile1: :cve:`2022-33065`
-  libssh2: :cve:`2023-48795`
-  libuv: :cve:`2024-24806`
-  libxml2: :cve:`2023-45322` (ignored)
-  linux-yocto/6.6: :cve:`2020-16119`
-  openssh: :cve:`2023-48795`, :cve:`2023-51384`, :cve:`2023-51385`
-  openssl: :cve:`2023-5363`, :cve:`2023-5678`, :cve:`2023-6129`, :cve_mitre:`2023-6237`, :cve:`2024-0727`, :cve:`2024-2511`
-  perl: :cve:`2023-47100`
-  pixman: :cve:`2023-37769` (ignored)
-  python3-cryptography{-vectors}: :cve:`2023-49083`, :cve:`2024-26130`
-  python3-urllib3: :cve:`2023-45803`
-  shadow: :cve:`2023-4641`
-  sudo: :cve:`2023-42456`
-  tiff: :cve:`2023-6228`, :cve:`2023-6277`, :cve:`2023-52355`, :cve:`2023-52356`
-  vim: :cve:`2023-46246`, :cve:`2023-48231`, :cve:`2023-48232`, :cve:`2023-48233`, :cve:`2023-48234`, :cve:`2023-48235`, :cve:`2023-48236`, :cve:`2023-48237`, :cve:`2024-22667`
-  wpa-supplicant: :cve:`2023-52160`
-  xserver-xorg: :cve:`2023-5574`, :cve:`2023-6816`, :cve:`2024-0229`, :cve:`2024-0408`, :cve:`2024-0409`, :cve:`2024-21885`, :cve:`2024-21886`
-  xwayland: :cve:`2023-5367`, :cve:`2024-0408`, :cve:`2024-0409`, :cve:`2023-6816`, :cve:`2024-0229`, :cve:`2024-21885`, :cve:`2024-21886`
-  zlib: :cve:`2023-45853` (ignored), :cve:`2023-6992` (ignored)


Recipe Upgrades in 5.0
~~~~~~~~~~~~~~~~~~~~~~

-  acl 2.3.1 -> 2.3.2
-  acpica 20230628 -> 20240322
-  alsa-lib 1.2.10 -> 1.2.11
-  alsa-tools 1.2.5 -> 1.2.11
-  alsa-ucm-conf 1.2.10 -> 1.2.11
-  alsa-utils 1.2.10 -> 1.2.11
-  appstream 0.16.3 -> 1.0.2
-  autoconf 2.72c -> 2.72e
-  bash 5.2.15 -> 5.2.21
-  bash-completion 2.11 -> 2.12.0
-  binutils 2.41 -> 2.42
-  bluez5 5.69 -> 5.72
-  boost 1.83.0 -> 1.84.0
-  boost-build-native 1.83.0 -> 1.84.0
-  btrfs-tools 6.5.1 -> 6.7.1
-  cairo 1.16.0 -> 1.18.0
-  cargo 1.70.0 -> 1.75.0
-  cargo-c-native 0.9.18 -> 0.9.30+cargo-0.77.0
-  ccache 4.8.3 -> 4.9.1
-  cmake 3.27.7 -> 3.28.3
-  cmake-native 3.27.7 -> 3.28.3
-  createrepo-c 1.0.0 -> 1.0.4
-  cronie 1.6.1 -> 1.7.1
-  cross-localedef-native 2.38+git -> 2.39+git
-  cups 2.4.6 -> 2.4.7
-  curl 8.4.0 -> 8.7.1
-  dbus-wait 0.1+git (6cc6077a36fe…) -> 0.1+git (64bc7c8fae61…)
-  debianutils 5.13 -> 5.16
-  desktop-file-utils 0.26 -> 0.27
-  dhcpcd 10.0.2 -> 10.0.6
-  diffoscope 249 -> 259
-  diffstat 1.65 -> 1.66
-  dnf 4.17.0 -> 4.19.0
-  dos2unix 7.5.1 -> 7.5.2
-  ed 1.19 -> 1.20.1
-  efivar 38+39+git -> 39+39+git
-  elfutils 0.189 -> 0.191
-  ell 0.60 -> 0.63
-  enchant2 2.6.2 -> 2.6.7
-  epiphany 44.6 -> 46.0
-  erofs-utils 1.6 -> 1.7.1
-  ethtool 6.5 -> 6.7
-  eudev 3.2.12 -> 3.2.14
-  expat 2.5.0 -> 2.6.2
-  ffmpeg 6.0 -> 6.1.1
-  fontconfig 2.14.2 -> 2.15.0
-  gawk 5.2.2 -> 5.3.0
-  gcr 4.1.0 -> 4.2.0
-  gdb 13.2 -> 14.2
-  gettext 0.22 -> 0.22.5
-  gettext-minimal-native 0.22 -> 0.22.5
-  gi-docgen 2023.1 -> 2023.3
-  git 2.42.0 -> 2.44.0
-  glib-2.0 2.78.3 -> 2.78.4
-  glib-networking 2.76.1 -> 2.78.1
-  glibc 2.38+git -> 2.39+git
-  glibc-locale 2.38 -> 2.39+git
-  glibc-mtrace 2.38 -> 2.39+git
-  glibc-scripts 2.38 -> 2.39+git
-  glibc-testsuite 2.38+git -> 2.39+git
-  glibc-y2038-tests 2.38+git -> 2.39+git
-  glslang 1.3.261.1 -> 1.3.275.0
-  gnu-config 20230216+git -> 20240101+git
-  gnupg 2.4.3 -> 2.4.4
-  gnutls 3.8.3 -> 3.8.4
-  go 1.20.12 -> 1.22.2
-  go-binary-native 1.20.12 -> 1.22.2
-  go-native 1.20.12 -> 1.22.2
-  go-runtime 1.20.12 -> 1.22.2
-  gpgme 1.22.0 -> 1.23.2
-  grub 2.06 -> 2.12
-  grub-efi 2.06 -> 2.12
-  gsettings-desktop-schemas 44.0 -> 46.0
-  gst-devtools 1.22.9 -> 1.22.11
-  gstreamer1.0 1.22.9 -> 1.22.11
-  gstreamer1.0-libav 1.22.9 -> 1.22.11
-  gstreamer1.0-omx 1.22.9 -> 1.22.11
-  gstreamer1.0-plugins-bad 1.22.9 -> 1.22.11
-  gstreamer1.0-plugins-base 1.22.9 -> 1.22.11
-  gstreamer1.0-plugins-good 1.22.9 -> 1.22.11
-  gstreamer1.0-plugins-ugly 1.22.9 -> 1.22.11
-  gstreamer1.0-python 1.22.9 -> 1.22.11
-  gstreamer1.0-rtsp-server 1.22.9 -> 1.22.11
-  gstreamer1.0-vaapi 1.22.9 -> 1.22.11
-  gtk+3 3.24.38 -> 3.24.41
-  gtk4 4.12.3 -> 4.14.1
-  harfbuzz 8.2.2 -> 8.3.0
-  hwlatdetect 2.5 -> 2.6
-  icu 73-2 -> 74-1
-  inetutils 2.4 -> 2.5
-  init-system-helpers 1.65.2 -> 1.66
-  iproute2 6.5.0 -> 6.7.0
-  iptables 1.8.9 -> 1.8.10
-  iputils 20221126 -> 20240117
-  iso-codes 4.15.0 -> 4.16.0
-  iw 5.19 -> 6.7
-  json-glib 1.6.6 -> 1.8.0
-  kbd 2.6.3 -> 2.6.4
-  kexec-tools 2.0.27 -> 2.0.28
-  kmod 30 -> 31
-  kmscube git -> 0.0.1+git
-  libadwaita 1.4.2 -> 1.5.0
-  libbsd 0.11.7 -> 0.12.1
-  libcap-ng 0.8.3 -> 0.8.4
-  libcap-ng-python 0.8.3 -> 0.8.4
-  libcomps 0.1.19 -> 0.1.20
-  libdnf 0.71.0 -> 0.73.0
-  libdrm 2.4.116 -> 2.4.120
-  libffi 3.4.4 -> 3.4.6
-  libgit2 1.7.1 -> 1.7.2
-  libgloss 4.3.0+git -> 4.4.0+git
-  libgpg-error 1.47 -> 1.48
-  libhandy 1.8.2 -> 1.8.3
-  libical 3.0.16 -> 3.0.17
-  libidn2 2.3.4 -> 2.3.7
-  libinput 1.24.0 -> 1.25.0
-  libksba 1.6.4 -> 1.6.6
-  libmicrohttpd 0.9.77 -> 1.0.1
-  libnl 3.8.0 -> 3.9.0
-  libnotify 0.8.2 -> 0.8.3
-  libpciaccess 0.17 -> 0.18
-  libpcre2 10.42 -> 10.43
-  libpng 1.6.40 -> 1.6.42
-  libproxy 0.5.3 -> 0.5.4
-  libpsl 0.21.2 -> 0.21.5
-  librepo 1.16.0 -> 1.17.0
-  librsvg 2.56.3 -> 2.57.1
-  libsdl2 2.28.4 -> 2.30.0
-  libseccomp 2.5.4 -> 2.5.5
-  libsecret 0.21.1 -> 0.21.4
-  libsolv 0.7.26 -> 0.7.28
-  libsoup 3.4.2 -> 3.4.4
-  libstd-rs 1.70.0 -> 1.75.0
-  libtest-warnings-perl 0.031 -> 0.033
-  libtirpc 1.3.3 -> 1.3.4
-  libubootenv 0.3.4 -> 0.3.5
-  libunistring 1.1 -> 1.2
-  liburi-perl 5.21 -> 5.27
-  libusb1 1.0.26 -> 1.0.27
-  libuv 1.46.0 -> 1.48.0
-  libva 2.19.0 -> 2.20.0
-  libva-initial 2.19.0 -> 2.20.0
-  libwpe 1.14.1 -> 1.14.2
-  libxext 1.3.5 -> 1.3.6
-  libxkbcommon 1.5.0 -> 1.6.0
-  libxkbfile 1.1.2 -> 1.1.3
-  libxml-parser-perl 2.46 -> 2.47
-  libxml2 2.11.7 -> 2.12.5
-  libxmlb 0.3.14 -> 0.3.15
-  libxrandr 1.5.3 -> 1.5.4
-  libxvmc 1.0.13 -> 1.0.14
-  lighttpd 1.4.71 -> 1.4.74
-  linux-firmware 20240220 -> 20240312
-  linux-libc-headers 6.5 -> 6.6
-  linux-yocto 6.1.78+git, 6.5.13+git -> 6.6.23+git
-  linux-yocto-dev 6.6+git -> 6.9+git
-  linux-yocto-rt 6.1.78+git, 6.5.13+git -> 6.6.23+git
-  linux-yocto-tiny 6.1.78+git, 6.5.13+git -> 6.6.23+git
-  llvm 17.0.3 -> 18.1.3
-  lsof 4.98.0 -> 4.99.3
-  ltp 20230516 -> 20240129
-  lttng-modules 2.13.10 -> 2.13.12
-  lttng-ust 2.13.6 -> 2.13.7
-  lzip 1.23 -> 1.24
-  makedepend 1.0.8 -> 1.0.9
-  man-db 2.11.2 -> 2.12.0
-  man-pages 6.05.01 -> 6.06
-  mc 4.8.30 -> 4.8.31
-  mesa 23.2.1 -> 24.0.2
-  mesa-gl 23.2.1 -> 24.0.2
-  meson 1.2.2 -> 1.3.1
-  minicom 2.8 -> 2.9
-  mmc-utils 0.1+git (613495ecaca9…) -> 0.1+git (b5ca140312d2…)
-  mpg123 1.31.3 -> 1.32.5
-  newlib 4.3.0+git -> 4.4.0+git
-  nghttp2 1.57.0 -> 1.61.0
-  numactl 2.0.16 -> 2.0.18
-  ofono 2.1 -> 2.4
-  opensbi 1.2 -> 1.4
-  openssh 9.5p1 -> 9.6p1
-  openssl 3.1.5 -> 3.2.1
-  opkg 0.6.2 -> 0.6.3
-  opkg-utils 0.6.2 -> 0.6.3
-  orc 0.4.34 -> 0.4.38
-  ovmf edk2-stable202308 -> edk2-stable202402
-  p11-kit 0.25.0 -> 0.25.3
-  pango 1.51.0 -> 1.52.0
-  pciutils 3.10.0 -> 3.11.1
-  piglit 1.0+gitr (71c21b1157c4…) -> 1.0+gitr (22eaf6a91cfd…)
-  pkgconf 2.0.3 -> 2.1.1
-  psplash 0.1+git (44afb7506d43…) -> 0.1+git (ecc191375669…)
-  ptest-runner 2.4.2+git -> 2.4.3+git
-  pulseaudio 16.1 -> 17.0
-  puzzles 0.0+git (2d9e414ee316…) -> 0.0+git (80aac3104096…)
-  python3 3.11.5 -> 3.12.3
-  python3-alabaster 0.7.13 -> 0.7.16
-  python3-attrs 23.1.0 -> 23.2.0
-  python3-babel 2.12.1 -> 2.14.0
-  python3-bcrypt 4.0.1 -> 4.1.2
-  python3-beartype 0.15.0 -> 0.17.2
-  python3-build 1.0.3 -> 1.1.1
-  python3-certifi 2023.7.22 -> 2024.2.2
-  python3-cffi 1.15.1 -> 1.16.0
-  python3-cryptography 41.0.4 -> 42.0.5
-  python3-cryptography-vectors 41.0.4 -> 42.0.5
-  python3-cython 0.29.36 -> 3.0.8
-  python3-dbusmock 0.29.1 -> 0.31.1
-  python3-dtschema 2023.7 -> 2024.2
-  python3-git 3.1.36 -> 3.1.42
-  python3-gitdb 4.0.10 -> 4.0.11
-  python3-hatch-fancy-pypi-readme 23.1.0 -> 24.1.0
-  python3-hatch-vcs 0.3.0 -> 0.4.0
-  python3-hatchling 1.18.0 -> 1.21.1
-  python3-hypothesis 6.86.2 -> 6.98.15
-  python3-idna 3.4 -> 3.6
-  python3-importlib-metadata 6.8.0 -> 7.0.1
-  python3-iso8601 2.0.0 -> 2.1.0
-  python3-jsonschema 4.17.3 -> 4.21.1
-  python3-license-expression 30.1.1 -> 30.2.0
-  python3-lxml 4.9.3 -> 5.0.0
-  python3-mako 1.2.4 -> 1.3.2
-  python3-markdown 3.4.4 -> 3.5.2
-  python3-markupsafe 2.1.3 -> 2.1.5
-  python3-more-itertools 10.1.0 -> 10.2.0
-  python3-numpy 1.26.0 -> 1.26.4
-  python3-packaging 23.1 -> 23.2
-  python3-pathspec 0.11.2 -> 0.12.1
-  python3-pbr 5.11.1 -> 6.0.0
-  python3-pip 23.2.1 -> 24.0
-  python3-pluggy 1.3.0 -> 1.4.0
-  python3-poetry-core 1.7.0 -> 1.9.0
-  python3-psutil 5.9.5 -> 5.9.8
-  python3-pyasn1 0.5.0 -> 0.5.1
-  python3-pycairo 1.24.0 -> 1.26.0
-  python3-pycryptodome 3.19.0 -> 3.20.0
-  python3-pycryptodomex 3.19.0 -> 3.20.0
-  python3-pygments 2.16.1 -> 2.17.2
-  python3-pyopenssl 23.2.0 -> 24.0.0
-  python3-pyrsistent 0.19.3 -> 0.20.0
-  python3-pytest 7.4.2 -> 8.0.2
-  python3-pytest-runner 6.0.0 -> 6.0.1
-  python3-pytz 2023.3 -> 2024.1
-  python3-ruamel-yaml 0.17.32 -> 0.18.6
-  python3-scons 4.5.2 -> 4.6.0
-  python3-setuptools 68.2.2 -> 69.1.1
-  python3-setuptools-rust 1.7.0 -> 1.9.0
-  python3-setuptools-scm 7.1.0 -> 8.0.4
-  python3-spdx-tools 0.8.1 -> 0.8.2
-  python3-sphinx-rtd-theme 1.3.0 -> 2.0.0
-  python3-sphinxcontrib-applehelp 1.0.4 -> 1.0.8
-  python3-sphinxcontrib-devhelp 1.0.2 -> 1.0.6
-  python3-sphinxcontrib-htmlhelp 2.0.1 -> 2.0.5
-  python3-sphinxcontrib-qthelp 1.0.3 -> 1.0.7
-  python3-sphinxcontrib-serializinghtml 1.1.5 -> 1.1.10
-  python3-subunit 1.4.2 -> 1.4.4
-  python3-testtools 2.6.0 -> 2.7.1
-  python3-trove-classifiers 2023.9.19 -> 2024.2.23
-  python3-typing-extensions 4.8.0 -> 4.10.0
-  python3-unittest-automake-output 0.1 -> 0.2
-  python3-urllib3 2.0.7 -> 2.2.1
-  python3-wcwidth 0.2.6 -> 0.2.13
-  python3-wheel 0.41.2 -> 0.42.0
-  qemu 8.1.4 -> 8.2.1
-  qemu-native 8.1.4 -> 8.2.1
-  qemu-system-native 8.1.4 -> 8.2.1
-  repo 2.36.1 -> 2.42
-  resolvconf 1.91 -> 1.92
-  rpm 4.18.1 -> 4.19.1
-  rt-tests 2.5 -> 2.6
-  rust 1.70.0 -> 1.75.0
-  rust-cross-canadian 1.70.0 -> 1.75.0
-  rust-llvm 1.70.0 -> 1.75.0
-  shaderc 2023.6 -> 2023.8
-  shadow 4.13 -> 4.14.2
-  shared-mime-info 2.2 -> 2.4
-  socat 1.7.4.4 -> 1.8.0.0
-  spirv-headers 1.3.261.1 -> 1.3.275.0
-  spirv-tools 1.3.261.1 -> 1.3.275.0
-  sqlite3 3.43.2 -> 3.45.1
-  strace 6.5 -> 6.7
-  stress-ng 0.16.05 -> 0.17.05
-  subversion 1.14.2 -> 1.14.3
-  swig 4.1.1 -> 4.2.1
-  sysstat 12.7.4 -> 12.7.5
-  systemd 254.4 -> 255.4
-  systemd-boot 254.4 -> 255.4
-  systemd-bootchart 234 -> 235
-  systemtap 4.9 -> 5.0
-  systemtap-native 4.9 -> 5.0
-  taglib 1.13.1 -> 2.0
-  ttyrun 2.29.0 -> 2.31.0
-  u-boot 2023.10 -> 2024.01
-  u-boot-tools 2023.10 -> 2024.01
-  update-rc.d 0.8 (8636cf478d42…) -> 0.8 (b8f950105010…)
-  usbutils 015 -> 017
-  util-linux 2.39.2 -> 2.39.3
-  util-linux-libuuid 2.39.2 -> 2.39.3
-  vala 0.56.13 -> 0.56.15
-  valgrind 3.21.0 -> 3.22.0
-  vim 9.0.2190 -> 9.1.0114
-  vim-tiny 9.0.2190 -> 9.1.0114
-  virglrenderer 0.10.4 -> 1.0.1
-  vte 0.72.2 -> 0.74.2
-  vulkan-headers 1.3.261.1 -> 1.3.275.0
-  vulkan-loader 1.3.261.1 -> 1.3.275.0
-  vulkan-tools 1.3.261.1 -> 1.3.275.0
-  vulkan-validation-layers 1.3.261.1 -> 1.3.275.0
-  wayland-protocols 1.32 -> 1.33
-  webkitgtk 2.40.5 -> 2.44.0
-  weston 12.0.2 -> 13.0.0
-  xkbcomp 1.4.6 -> 1.4.7
-  xkeyboard-config 2.39 -> 2.41
-  xprop 1.2.6 -> 1.2.7
-  xwayland 23.2.4 -> 23.2.5
-  xz 5.4.4 -> 5.4.6
-  zlib 1.3 -> 1.3.1


Contributors to 5.0
~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Adam Johnston
-  Adithya Balakumar
-  Adrian Freihofer
-  Alassane Yattara
-  Alejandro Hernandez Samaniego
-  Aleksey Smirnov
-  Alexander Kanavin
-  Alexander Lussier-Cullen
-  Alexander Sverdlin
-  Alexandre Belloni
-  Alexandre Truong
-  Alex Bennée
-  Alexis Lothoré
-  Alex Kiernan
-  Alex Stewart
-  André Draszik
-  Anibal Limon
-  Anuj Mittal
-  Archana Polampalli
-  Arne Schwerdt
-  Bartosz Golaszewski
-  Baruch Siach
-  Bastian Krause
-  BELHADJ SALEM Talel
-  BELOUARGA Mohamed
-  Bruce Ashfield
-  Changhyeok Bae
-  Changqing Li
-  Charlie Johnston
-  Chen Qi
-  Chi Xu
-  Chris Laplante
-  Christian Taedcke
-  Christoph Vogtländer
-  Claus Stovgaard
-  Clay Chang
-  Clément Péron
-  Colin McAllister
-  Corentin Guillevic
-  Daniel Ammann
-  david d zuhn
-  David Reyna
-  Deepthi Hemraj
-  Denys Dmytriyenko
-  Derek Erdmann
-  Desone Burns
-  Dhairya Nagodra
-  Dmitry Baryshkov
-  Eero Aaltonen
-  Eilís 'pidge' Ní Fhlannagáin
-  Emil Kronborg
-  Enguerrand de Ribaucourt
-  Enrico Jörns
-  Enrico Scholz
-  Etienne Cordonnier
-  Fabien Mahot
-  Fabio Estevam
-  Fahad Arslan
-  Felix Moessbauer
-  Florian Wickert
-  Geoff Parker
-  Glenn Strauss
-  Harish Sadineni
-  Hongxu Jia
-  Ilya A. Kriveshko
-  Jamin Lin
-  Jan Vermaete
-  Jason Andryuk
-  Javier Tia
-  Jeremy A. Puhlman
-  Jérémy Rosen
-  Jermain Horsman
-  Jiang Kai
-  Joakim Tjernlund
-  Joao Marcos Costa
-  Joe Slater
-  Johan Bezem
-  Johannes Schneider
-  Jonathan GUILLOT
-  Jon Mason
-  Jörg Sommer
-  Jose Quaresma
-  Joshua Watt
-  Julien Stephan
-  Justin Bronder
-  Kai Kang
-  Kareem Zarka
-  Kevin Hao
-  Khem Raj
-  Konrad Weihmann
-  Lee Chee Yang
-  Lei Maohui
-  lixiaoyong
-  Logan Gunthorpe
-  Luca Ceresoli
-  luca fancellu
-  Lucas Stach
-  Ludovic Jozeau
-  Lukas Funke
-  Maanya Goenka
-  Malte Schmidt
-  Marcel Ziswiler
-  Marco Felsch
-  Marcus Folkesson
-  Marek Vasut
-  Mark Asselstine
-  Mark Hatle
-  Markus Fuchs
-  Markus Volk
-  Marlon Rodriguez Garcia
-  Marta Rybczynska
-  Martin Hundebøll
-  Martin Jansa
-  Massimiliano Minella
-  Maxin B. John
-  Max Krummenacher
-  Meenali Gupta
-  Michael Halstead
-  Michael Opdenacker
-  Michal Sieron
-  Mikko Rapeli
-  Ming Liu
-  Mingli Yu
-  Munehisa Kamata
-  Nick Owens
-  Niko Mauno
-  Ola x Nilsson
-  Oleh Matiusha
-  Patrick Williams
-  Paul Barker
-  Paul Eggleton
-  Paul Gortmaker
-  Pavel Zhukov
-  Peter A. Bigot
-  Peter Kjellerstedt
-  Peter Marko
-  Petr Vorel
-  Philip Balister
-  Philip Lorenz
-  Philippe Rivest
-  Piotr Łobacz
-  Priyal Doshi
-  Quentin Schulz
-  Ragesh Nair
-  Randolph Sapp
-  Randy MacLeod
-  Rasmus Villemoes
-  Renat Khalikov
-  Richard Haar
-  Richard Purdie
-  Robert Berger
-  Robert Joslyn
-  Robert P. J. Day
-  Robert Yang
-  Rodrigo M. Duarte
-  Ross Burton
-  Rouven Czerwinski
-  Ryan Eatmon
-  Sam Van Den Berge
-  Saul Wold
-  Sava Jakovljev
-  Sean Nyekjaer
-  Sergei Zhmylev
-  Shinji Matsunaga
-  Shubham Kulkarni
-  Simone Weiß
-  Siong W.LIM
-  Soumya Sambu
-  Sourav Kumar Pramanik
-  Stefan Herbrechtsmeier
-  Stéphane Veyret
-  Steve Sakoman
-  Sundeep KOKKONDA
-  Thomas Perrot
-  Thomas Wolber
-  Timon Bergelt
-  Tim Orling
-  Timotheus Giuliani
-  Tobias Hagelborn
-  Tom Hochstein
-  Tom Rini
-  Toni Lammi
-  Trevor Gamblin
-  Trevor Woerner
-  Ulrich Ölmann
-  Valek Andrej
-  venkata pyla
-  Victor Kamensky
-  Vijay Anusuri
-  Vikas Katariya
-  Vincent Davis Jr
-  Viswanath Kraleti
-  Vyacheslav Yurkov
-  Wang Mingyu
-  William A. Kennington III
-  William Hauser
-  William Lyu
-  Xiangyu Chen
-  Xiaotian Wu
-  Yang Xu
-  Yannick Rodriguez
-  Yash Shinde
-  Yi Zhao
-  Yoann Congal
-  Yogesh Tyagi
-  Yogita Urade
-  Zahir Hussain
-  Zang Ruochen
-  Zoltan Boszormenyi

Repositories / Downloads for Yocto-5.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`scarthgap </poky/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0 </poky/log/?h=yocto-5.0>`
-  Git Revision: :yocto_git:`fb91a49387cfb0c8d48303bb3354325ba2a05587 </poky/commit/?id=fb91a49387cfb0c8d48303bb3354325ba2a05587>`
-  Release Artefact: poky-fb91a49387cfb0c8d48303bb3354325ba2a05587
-  sha: 8a0dff4b677b9414ab814ed35d1880196123732ea16ab2fafa388bcc509b32ab
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0/poky-fb91a49387cfb0c8d48303bb3354325ba2a05587.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0/poky-fb91a49387cfb0c8d48303bb3354325ba2a05587.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`scarthgap </openembedded-core/log/?h=scarthgap>`
-  Tag:  :oe_git:`yocto-5.0 </openembedded-core/log/?h=yocto-5.0>`
-  Git Revision: :oe_git:`b65b4e5a8e4473d8ca43835ba17bc8bd4bdca277 </openembedded-core/commit/?id=b65b4e5a8e4473d8ca43835ba17bc8bd4bdca277>`
-  Release Artefact: oecore-b65b4e5a8e4473d8ca43835ba17bc8bd4bdca277
-  sha: c7fd05d1a00c70acba2540e60dce01a1bdc4701ebff9a808784960240c69261d
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0/oecore-b65b4e5a8e4473d8ca43835ba17bc8bd4bdca277.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0/oecore-b65b4e5a8e4473d8ca43835ba17bc8bd4bdca277.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`scarthgap </meta-mingw/log/?h=scarthgap>`
-  Tag:  :yocto_git:`yocto-5.0 </meta-mingw/log/?h=yocto-5.0>`
-  Git Revision: :yocto_git:`acbba477893ef87388effc4679b7f40ee49fc852 </meta-mingw/commit/?id=acbba477893ef87388effc4679b7f40ee49fc852>`
-  Release Artefact: meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852
-  sha: 3b7c2f475dad5130bace652b150367f587d44b391218b1364a8bbc430b48c54c
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0/meta-mingw-acbba477893ef87388effc4679b7f40ee49fc852.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.8 </bitbake/log/?h=2.8>`
-  Tag:  :oe_git:`yocto-5.0 </bitbake/log/?h=yocto-5.0>`
-  Git Revision: :oe_git:`c86466d51e8ff14e57a734c1eec5bb651fdc73ef </bitbake/commit/?id=c86466d51e8ff14e57a734c1eec5bb651fdc73ef>`
-  Release Artefact: bitbake-c86466d51e8ff14e57a734c1eec5bb651fdc73ef
-  sha: 45c91294c1fa5a0044f1bb72a9bb69456bb458747114115af85c7664bf672d48
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-5.0/bitbake-c86466d51e8ff14e57a734c1eec5bb651fdc73ef.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-5.0/bitbake-c86466d51e8ff14e57a734c1eec5bb651fdc73ef.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`scarthgap </yocto-docs/log/?h=scarthgap>`
-  Tag: :yocto_git:`yocto-5.0 </yocto-docs/log/?h=yocto-5.0>`
-  Git Revision: :yocto_git:`0cdc0afd3332459d30cfc8f4c2e62bdcc23f5ed5 </yocto-docs/commit/?id=0cdc0afd3332459d30cfc8f4c2e62bdcc23f5ed5>`

