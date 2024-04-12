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

-  Architecture-specific enhancements:

   -  ``genericarm64``: a new :term:`MACHINE` to represent a 64-bit General Arm
      SystemReady platform.

   -  Add Power8 tune to PowerPC architecture.

   -  ``arch-armv9``: remove CRC and SVE tunes, since FEAT_CRC32 is now mandatory
      and SVE/SVE2 are enabled by default in GCC's ``-march=armv9-a``.

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

-  Testing:

   -  Add an optional ``unimplemented-ptest`` QA warning to detect upstream
      packages with tests, that do not use ptest.

   -  ``testimage``: retrieve the ptests directory, especially for the logs,
      upon ptest failure.

   -  ``oeqa``, ``oe-selftest``: add test cases for Maturin (SDK and runtime).

-  Utility script changes:

   -  New ``recipetool/create_go.py`` script added to support Go recipe creation

   -  ``oe-init-build-env`` can generate a initial configuration (``.vscode``)
      for VSCode and its "Yocto Project BitBake" extension.

-  BitBake improvements:

   -  Add support for :term:`BB_LOADFACTOR_MAX`, so Bitbake can stop running
      extra tasks if the system load is too high, especially in distributions
      where ``/proc/pressure`` is disabled.

   -  Add garbage collection to remove unused unihashes from the database.

   -  ``taskexp_ncurses``: add ncurses version of ``taskexp``, the dependency
      explorer originally implemented with GTK.

   -  Improve ``runqueue`` performance by adding a cache mechanism in
      ``build_taskdepdata``.

   -  ``bitbake.conf``: add ``runtimedir`` to represent the path to the runtime
      state directory (i.e., ``/run``).

-  Packaging changes:

   -  ``package_rpm``: the RPM package compressor's mode can now be overriden.

-  Security improvements:

   -  Improve incremental CVE database download from NVD. Rejected CVEs are
      removed, configuration is kept up-to-date. The age threshold for
      incremental update can be configured with :term:`CVE_DB_INCR_UPDATE_AGE_THRES`
      variable.

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

   -  ``systemd-boot`` can, from now on, be compiled as ``native``, thus
      providing ``ukify`` tool to build UKI images.

   -  systemd: split bash completion for ``udevadm`` in a new
      ``udev-bash-completion`` package.

   -  The :ref:`ref-classes-go-vendor` class was added to support offline builds
      (i.e., vendoring). It can also handle modules from the same repository,
      taking into account their versions.

   -  Disable strace support of bluetooth by default.

   -  ``openssh`` now has a Systemd service: ``sshd.service``.

Known Issues in 5.0
~~~~~~~~~~~~~~~~~~~

-  N/A

Recipe License changes in 5.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following corrections have been made to the :term:`LICENSE` values set by recipes:

-  ``systemd``: make the scope of ``LGPL`` more accurate (``LGPL-2.1`` -> ``LGPL-2.1-or-later``)
-  ``libsystemd``: set its own :term:`LICENSE` value (``LGPL-2.1-or-later``) to add more granularity

Security Fixes in 5.0
~~~~~~~~~~~~~~~~~~~~~

Recipe Upgrades in 5.0
~~~~~~~~~~~~~~~~~~~~~~

-  go: update 1.20.10 -> 1.22.1

Contributors to 5.0
~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

Repositories / Downloads for Yocto-5.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

