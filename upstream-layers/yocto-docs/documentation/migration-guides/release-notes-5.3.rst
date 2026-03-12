.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: whinlatter
.. |yocto-ver| replace:: 5.3
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Release notes for |yocto-ver| (|yocto-codename|)
------------------------------------------------

This release does have significant changes which people need to be aware of. See
:doc:`the migration guide </migration-guides/migration-5.3>` for more information.

New Features / Enhancements in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Linux kernel 6.16, gcc 15, glibc 2.42, LLVM 21.1.1, and over 300 other
   recipe upgrades.

-  Minimum Python version required on the host: 3.9.

-  Host requirements changes:

   -  The minimum disk space requirement is now 140Gbytes (previously 90Gbytes).

   -  The minimum RAM requirement is now 32Gbytes (previously 8Gbytes).

   -  These changes are mainly due to recent additions of the LLVM and other
      resource heavy recipes. For guidance on how to limit the resources used by
      the :term:`OpenEmbedded Build System`, see the
      :doc:`/dev-manual/limiting-resources` guide.

-  BitBake changes:

   -  A new ``bitbake-setup`` tool can be used to clone the initial layers and
      setup the environment. See the :doc:`/brief-yoctoprojectqs/index` section
      for instructions on how to build an image that uses the :term:`Poky`
      distribution with this new tool.

      See the reference documentation of ``bitbake-setup`` at
      :doc:`bitbake:bitbake-user-manual/bitbake-user-manual-environment-setup`.

   -  ``codeparser``: Add function decorators for ``vardeps``

      Adds ``bb.parse.vardeps`` and ``bb.parse.excludevardeps`` function
      decorators that can be used to explicitly add or exclude variables from a
      Python function parsed by :term:`BitBake`.

      Move ``vardepexclude`` flag entries alongside functions for
      maintainability.

   -  Fetcher:

      -  Check for ``git-lfs`` existence before using it.

      -  Add support for ``.debs`` files containing uncompressed data tarballs.

      -  ``az``: Add sanity check to check that :term:`AZ_SAS` starts with ``?``
         to mark the start of the query parameters.

      -  ``git``:

         -  Add the tag to shallow clone tarball name.
         -  Verify if local clones contains a tag, when the ``tag=`` parameter
            is used in :term:`SRC_URI`.

   -  ``knotty``:

      -  Pass failed task logs through the log infrastructure (use
         ``bb.plain()`` instead of ``print()``).

      -  Improve refresh rate of the footer progress bar.

   -  Add support for automatically promoting class inherits to deferred
      inherits by listing them in the :term:`BB_DEFER_BBCLASSES` variable.

   -  "Built-in" fragments support is now added to the :ref:`addfragments
      <bitbake-user-manual/bitbake-user-manual-metadata:\`\`addfragments\`\`
      directive>` directive. This is the fourth parameter to this directive, and
      should be the name of the variable that contains definitions of built-in
      fragments. Refer to the documentation of :ref:`addfragments
      <bitbake-user-manual/bitbake-user-manual-metadata:\`\`addfragments\`\`
      directive>` to learn how to define new built-in fragments.

      Listing these built-in fragments can be done with
      :oe_git:`bitbake-config-build
      list-fragments</bitbake/tree/bin/bitbake-config-build>`, which could
      list::

         Available built-in fragments:
         machine/...     Sets MACHINE = ...
         distro/...      Sets DISTRO = ...

      In the above example, this means that the :term:`MACHINE` of
      :term:`DISTRO` can be overridden with::

         OE_FRAGMENTS += "machine/qemuarm64 distro/poky-bleeding"

      This would set :term:`MACHINE` to ``qemuarm64`` and the :term:`DISTRO` to
      ``poky-bleeding``.

   -  The ``tag-`` parameter in URLs can now be specified alongside the ``rev=``
      parameter and :term:`SRCREV` variable, and will ensure that the
      specified tag matches the specified revision.

      It is **strongly encouraged** to include the ``tag=`` parameter to the
      :term:`SRC_URI` definition when possible.

   -  ``tinfoil``: add a ``wait_for`` decorator to wrap a function that makes an
      asynchronous tinfoil call wait for event to say that the call has been
      successful, or an error has occurred.

   -  New ``bb.utils.to_filemode()`` helper function which is a helper to take a
      variable's content containing a filemode and convert it to the proper
      Python representation of the number.

   -  ``cooker``: Use a shared counter for processing parser jobs. This allows
      the parser processes to run independently of needing to be feed by the
      parent process, and load balances them much better.

   -  ``cooker/process/utils``: Add a ``-P`` (``--profile``) option to
      :term:`BitBake` to specify what to profile. Can be "main", "idle" or
      "parsing". Split the reports in separate files.

   -  A "filtering" functionality was added and allows modifying the value of a
      variable before its value is returned by :term:`BitBake`. The
      ``setVarFilter`` API can be used for applying the filters, but it is
      **not** recommended for general use. It was added for internal use in
      the :term:`OpenEmbedded Build System` in the :ref:`ref-classes-native`
      class. The list of filters that are allowed are derived from a select
      list of functions that must be added using a ``filter_proc`` decorator.

   -  ``tests/parse``: Add tests for ``include``, ``require`` and
      ``include_all``.

-  Toolchain changes:

   -  The Clang/LLVM toolchain can now be used as part of the build.

      The :term:`PREFERRED_TOOLCHAIN_TARGET`, :term:`PREFERRED_TOOLCHAIN_NATIVE`
      and :term:`PREFERRED_TOOLCHAIN_SDK` variables can be used to customize the
      selected toolchain globally.

      There are two supported toolchains: "gcc" and "clang". See the
      documentation of :term:`PREFERRED_TOOLCHAIN_TARGET` for more details.

      The toolchain is also customizable on a per-recipe basis, using the
      :term:`TOOLCHAIN` and :term:`TOOLCHAIN_NATIVE` variables.

      .. warning::

         The :term:`TOOLCHAIN` should **not** be set globally. For overriding
         the toolchain globally, use :term:`PREFERRED_TOOLCHAIN_TARGET`,
         :term:`PREFERRED_TOOLCHAIN_NATIVE` and :term:`PREFERRED_TOOLCHAIN_SDK`.

   -  Multiple recipes were pinned to use the GCC/Binutils toolchain as they do
      not support being built with Clang/LLVM yet. In these recipes the
      :term:`TOOLCHAIN` variable is set to "gcc".

-  Global configuration changes:

   -  ``bitbake.conf/pseudo``: Switch from exclusion list to inclusion list by
      swapping :term:`PSEUDO_IGNORE_PATHS` for :term:`PSEUDO_INCLUDE_PATHS`
      which should be easier and more explicit to maintain.

   -  ``bitbake.conf``: Drop ``lz4`` from :term:`HOSTTOOLS`, as it is not
      required anymore, and the ``lz4-native`` package is used instead.

   -  :term:`Configuration Fragments <Configuration Fragment>`:

      -  Add a fragment for the `CDN` :ref:`sstate-cache
         <overview-manual/concepts:shared state cache>` mirror.

      -  Add a ``show-fragments`` sub-command to the
         :oe_git:`bitbake-config-build </bitbake/tree/bin/bitbake-config-build>`
         utility, to show the content of fragments from command-line.

   -  ``default-distrovars``: set an empty default for :term:`LICENSE_PATH`.

   -  The default definition of :term:`UNPACKDIR` is no longer
      ``sources-unpack`` but ``sources``.

   -  The default value for :term:`IMAGE_FSTYPES` (defined in
      :oe_git:`bitbake.conf </openembedded-core/tree/meta/conf/bitbake.conf>`)
      is now ``tar.zst`` (previously ``tar.gz``).

   -  Remove the ``meta/conf/distro/include/distro_alias.inc`` include file,
      which associated a recipe name to one or more Distribution package name.
      This file is not used and maintained anymore.

   -  A new configuration file :ref:`structure-build-conf-toolcfg.conf` is now
      used by :oe_git:`bitbake-config-build </bitbake/tree/bin/bitbake-config-build>`
      to manage :term:`Configuration Fragments <Configuration Fragment>`.

   -  ``bitbake.conf``: add :term:`TMPDIR` to the ``GIT_CEILING_DIRECTORIES``
      Git variable. This avoids Git trying to find a repository higher than
      :term:`TMPDIR` in recipes that use the :ref:`structure-build-work-shared`
      directory for storing their sources. This fixes reproducibility issues.

   -  Changes to the ``genericarm64`` machine configuration:

      -  Increase the :term:`Initramfs` maximum size.
      -  Install extra Linux firmware packages to fix Linux kernel warnings.

-  New variables:

   -  The ``VIRTUAL-RUNTIME_dbus`` variable, to allow changing the runtime
      implementation of D-Bus. See :term:`VIRTUAL-RUNTIME`.

   -  The ``VIRTUAL-RUNTIME_libsdl2`` variable, to allow changing the runtime
      implementation of `libsdl2 <https://www.libsdl.org/>`__. See
      :term:`VIRTUAL-RUNTIME`.

   -  The :term:`SPDX_PACKAGE_URL` variable can be used in recipes to set the
      output ``software_packageUrl`` field in their associated SPDX 3.0 output
      (default value: empty string).

   -  The :term:`KMETA_CONFIG_FEATURES` variable can be used to control
      :ref:`ref-classes-kernel-yocto` configuration features. For now only
      ``prefer-modules`` is supported for this variable.

   -  The :term:`TESTSDK_SUITES` variable can be used to control the list of
      tests run for the :ref:`ref-classes-testsdk` class.

   -  The :term:`UBOOT_FIT_CONF_FIRMWARE` can be used to specify a ``firmware``
      entry in the configuration node of a FIT image.

   -  The :term:`SPDX_INCLUDE_COMPILED_SOURCES` option allows the same as
      :term:`SPDX_INCLUDE_SOURCES` but including only the sources used to
      compile the host tools and the target packages.

   -  The :term:`UBOOT_VERSION` variable holds the package version
      (:term:`PV`) and revision (:term:`PR`) which are part of the installed and
      deployed filenames. Users can now override :term:`UBOOT_VERSION` to
      changes the output filenames.

   -  The :term:`UBOOT_MAKE_OPTS` variable specifies extra options passed to
      ``make`` when building U-boot. Extra options can also be passed as the
      fourth argument of the :term:`UBOOT_CONFIG` variable. See the
      documentation of :ref:`ref-classes-uboot-config` class for more details.

   -  The :term:`WESTON_USER` and :term:`WESTON_USER_HOME` variables can be
      used to define the username and home directory for the `Weston` user.

-  New core tasks:

   -  The :ref:`ref-tasks-list_image_features` can be used to list the available
      :term:`IMAGE_FEATURES` for an image recipe.

-  Kernel-related changes:

   -  Support for the 6.17 kernel exists but 6.16 is the default selected one in
      the :term:`Poky` distro.

   -  ``linux/generate-cve-exclusions``: use data from CVEProject instead of
      the archived https://linuxkernelcves.com.

   -  ``kernel-yocto``: allow annotated options to be modified. For example if
      the following kernel configuration is set::

         CONFIG_INET_TUNNEL=y # OVERRIDE:$MODULE_OR_Y

      And if the :term:`KMETA_CONFIG_FEATURES` variable contains
      ``prefer-modules``, ``CONFIG_INET_TUNNEL`` will be set to ``m`` instead of
      ``y``.

   -  ``kernel-devsrc``: Replace the extra ``System.map`` file with symbolic
      link.

   -  ``kernel-module-split``: Allow for external configuration files being
      assigned to the correct kernel module package.

   -  When built for the RISC-V architecture, ensure that the minimum required
      features set by :term:`TUNE_FEATURES` are set using the
      :ref:`ref-classes-features_check` class.

   -  ``linux-yocto``: when built for RISC-V, enable features in
      :term:`KERNEL_FEATURES` based on features listed in :term:`TUNE_FEATURES`.

   -  ``perf``: Enable ``coresight`` if enabled in :term:`MACHINE_FEATURES`.

-  New core recipes:

   -  ``python3-pdm``, ``python3-pdm-backend`` and ``python3-pdm-build-locked``,
      which are dependencies of ``python3-webcolors``. ``python3-pdm`` itself
      depends on ``python3-pdm-build-locked``

   -  ``bindgen-cli``: a tool to generate Rust bindings.

   -  ``python3-colorama``: Cross-platform colored terminal text, needed by
      ``pytest`` as a dependency.

   -  ``libglvnd``: imported from :oe_git:`meta-oe
      </meta-openembedded/tree/meta-oe>` which provides a vendor neutral
      approach to handling OpenGL / OpenGL ES / EGL / GLX libraries.

   -  ``python3-sphinx-argparse``: A sphinx extension that automatically
      documents ``argparse`` commands and options. It is part of
      ``buildtools-docs-tarball`` for later use in the Yocto Project
      documentation.

   -  ``python3-sphinx-copybutton``: A sphinx extension that adds a copy button
      to code blocks in Sphinx. It is part of ``buildtools-docs-tarball`` for later
      use in the Yocto Project documentation.

   -  ``python3-coherent-licensed``: License management tooling for `Coherent
      System` and skeleton projects. It became a new dependency of
      ``python3-zipp``.

   -  ``gn``: a commonly used build tool to generate `ninja
      <https://ninja-build.org/>`__ files.

   -  LLVM/Clang related recipes:

      -  ``clang``: LLVM based C/C++ compiler.

      -  ``compiler-rt``: LLVM based C/C++ compiler Runtime.

      -  ``libclc``: Implementation of the library requirements of the OpenCL C
         programming language.

      -  ``libcxx``: new implementation of the C++ standard library, targeting
         C++11 and above

      -  ``llvm-tblgen-native``: LLVM TableGen binaries for the build host,
         often used to build LLVM projects.

      -  ``lld``: the LLVM Linker.

      -  ``lldb``: LLDB debugger for LLVM projects.

      -  ``llvm-project-source``: canonical git mirror of the LLVM subversion
         repository.

      -  ``llvm``: The LLVM Compiler Infrastructure.

      -  ``openmp``: LLVM OpenMP compiler Runtime.

  -  ``kernel-signing-keys-native``: this recipe is used in the
     :ref:`ref-classes-kernel-fit-image` class to generate a pair of RSA
     public/private key. It replaces the ``do_generate_rsa_keys`` of the
     :ref:`ref-classes-kernel-fit-image` class.

-  New :term:`DISTRO_FEATURES`:

   -  ``glvnd``, which enables OpenGL Vendor Neutral Dispatch Library
      support when using recipes such as ``mesa``.

   -  ``opencl``: support for the :wikipedia:`OpenCL (Open Computing Language)
      <OpenCL>` framework.

-  New core classes:

   -  The new :ref:`ref-classes-kernel-fit-image` class replaces the previous
      ``kernel-fitimage`` class. It has been rewritten and improved to fix
      :yocto_bugs:`bug 12912</show_bug.cgi?id=12912>`. See the :ref:`Removed
      Classes <migration-guides/migration-5.3:Removed Classes>` section of the
      Migration notes for |yocto-ver| (|yocto-codename|) for more details on how
      to switch to this new class.

   -  The new :ref:`ref-classes-go-mod-update-modules` class can be used to
      maintain Go recipes that use a ``BPN-go-mods.inc`` and
      ``BPN-licenses.inc`` and update these files automatically.

   -  The new :ref:`ref-classes-python_pdm` class supports building Python
      recipes with the `PDM <https://pdm-project.org/>`__ package and dependency
      manager.

-  Architecture-specific changes:

   -  Rework the RISC-V :term:`TUNE_FEATURES` to make them based of the RISC-V
      ISA (Instruction Set Architecture) implementation.

      This implements the following base ISAs:

      -  ``rv32i``, ``rv64i``
      -  ``rv32e``, ``rv64i``

      The following ABIs:

      -  ``ilp32``, ``ilp32e``, ``ilp32f``, ``ilp32d``
      -  ``lp64``, ``lp64e``, ``lp64f``, ``lp64d``

      The following ISA extension are also implemented:

      -  M: Integer Multiplication and Division Extension
      -  A: Atomic Memory Extension
      -  F: Single-Precision Floating-Point Extension
      -  D: Double-Precision Floating-Point Extension
      -  C: Compressed Extension
      -  B: Bit Manipulation Extension (implies Zba, Zbb, Zbs)
      -  V: Vector Operations Extension
      -  Zicsr: Control and Status Register Access Extension
      -  Zifencei: Instruction-Fetch Fence Extension
      -  Zba: Address bit manipulation extension
      -  Zbb: Basic bit manipulation extension
      -  Zbc: Carry-less multiplication extension
      -  Zbs: Single-bit manipulation extension
      -  Zicbom: Cache-block management extension

      The existing processors tunes are preserved:

      -  ``riscv64`` (``rv64gc``)
      -  ``riscv32`` (``rv32gc``)
      -  ``riscv64nf`` (``rv64imac_zicsr_zifencei``)
      -  ``riscv32nf`` (``rv32imac_zicsr_zifencei``)
      -  ``riscv64nc`` (``rv64imafd_zicsr_zifencei``)

      See :oe_git:`meta/conf/machine/include/riscv/README
      </openembedded-core/tree/meta/conf/machine/include/riscv/README>` for more
      information.

   -  Add support for new Arm64 instruction sets, which are represented as files
      to be included in :term:`MACHINE` configuration in :term:`OpenEmbedded-Core
      (OE-Core)`. The new configuration files are:

      -  :oe_git:`conf/machine/include/arm/arch-armv8-7a.inc </openembedded-core/tree/meta/conf/machine/include/arm/arch-armv8-7a.inc>`
      -  :oe_git:`conf/machine/include/arm/arch-armv8-8a.inc </openembedded-core/tree/meta/conf/machine/include/arm/arch-armv8-8a.inc>`
      -  :oe_git:`conf/machine/include/arm/arch-armv9-1a.inc </openembedded-core/tree/meta/conf/machine/include/arm/arch-armv9-1a.inc>`
      -  :oe_git:`conf/machine/include/arm/arch-armv9-2a.inc </openembedded-core/tree/meta/conf/machine/include/arm/arch-armv9-2a.inc>`
      -  :oe_git:`conf/machine/include/arm/arch-armv9-3a.inc </openembedded-core/tree/meta/conf/machine/include/arm/arch-armv9-3a.inc>`

   -  ``arch-mips.inc``: Use ``-EB``/``-EL`` for denoting Endianness.

   -  Enable ``riscv32`` as supported arch for ``musl`` systems.

   -  Powerpc: Use ``-maltivec`` in compiler flags if ``altivec`` is in
      :term:`TUNE_FEATURES`.

   -  ``arm``: add a ``nocrypto`` feature to :term:`TUNE_FEATURES` to complement
      the ``crypto`` feature to explicitly disable cryptographic extensions via
      `GCC` flags.

      This lead to the creation of two new tunes:

      -  ``tune-cortexa72-nocrypto``
      -  ``tune-cortexa53-nocrypto``

-  QEMU / ``runqemu`` changes:

   -  Refactor :ref:`ref-classes-qemu` functions into library functions (in
      :oe_git:`lib/oe/qemu.py </openembedded-core/tree/meta/lib/oe/qemu.py>`).

   -  The ``qemux86-64`` :term:`MACHINE` now defaults to the ``x86-64-v3``
      micro-architecture level.

      The previous default was Core 2 era processors. This change means that the
      toolchain is configured to build for that level, and QEMU is configured to
      emulate it.

      The v3 level adds support for AVX/AVX2/BMI/BMI2/F16C and other newer
      instructions which are seeing increasing usage in modern software and add
      performance benefits. Please see :wikipedia:`X86-64 Microarchitecture
      levels <X86-64#Microarchitecture_levels>` for definition of the levels and
      lists of Intel/AMD CPUs where support for the instructions was first
      added.

      Note that if QEMU system emulation is used on an x86 build machine with
      :wikipedia:`KVM <Kernel-based_Virtual_Machine>` enabled, then the build
      machine's CPU must also be recent enough to support these instructions
      natively.

   -  ``runqemu``:

      -  The script can now run compressed images with snapshot mode. For
         example, with :term:`IMAGE_FSTYPES` containing ``ext4.zst``, you can run::

            runqemu snapshot ext4.zst <image-recipe>

      -  Add support for the ``erofs`` filesystem.

      -  The :term:`BitBake` environment is now a requirement, and the script
         cannot run without a successful call to ``bitbake -e``.

         The script will also raise an error with the ``bitbake`` command is not
         found.

-  Documentation changes:

   -  Add documentation on :term:`Configuration Fragments <Configuration
      Fragment>`:

      -  :doc:`/ref-manual/fragments`
      -  :doc:`/dev-manual/creating-fragments`

   -  Part of :term:`BitBake` internals are now documented at
      :yocto_docs:`/bitbake/bitbake-user-manual/bitbake-user-manual-library-functions.html`.

   -  A new :doc:`/dev-manual/limiting-resources` guide was created to help
      users limit the resources used by the :term:`OpenEmbedded Build System`.

   -  A new :doc:`/dev-manual/hashequivserver` guide was created to help users
      setting up a :ref:`overview-manual/concepts:Hash Equivalence` server.

   -  The QA checks defined in the :term:`OpenEmbedded Build System` were
      gathered in :doc:`/ref-manual/qa-checks`.

   -  A new :doc:`/dev-manual/poky-manual-setup` document was added to instruct
      how to setup the :term:`Poky` reference distribution manually, after the
      :ref:`master branch of the Poky repository has stopped being updated
      <migration-guides/migration-5.3:The Poky repository master branch is no
      longer updated>`.

-  Core library changes:

   -  Add :oe_git:`license_finder.py </openembedded-core/tree/meta/lib/oe/license_finder.py>`,
      which was extracted from ``recipetool`` to be shared for multiple users.
      Improve its functionalities.

-  Go changes:

   -  :ref:`ref-classes-go-mod-update-modules`: Update license finding to use
      the new ``find_licenses_up`` library function.

-  Rust changes:

   - The rust-llvm recipe has been removed, and the rust recipe now uses the
     same llvm recipe as clang.

   -  Add the ``has-thread-local`` option to the
      :ref:`ref-classes-rust-target-config` class.

-  Wic Image Creator changes:

   -  After a Python upgrade, WIC plugins containing dashes (``-``) for their
      filenames are **no longer supported**. One must convert the dashed to
      underscores (``_``) and update users of the plugins accordingly.

      See the :ref:`migration-guides/migration-5.3:Wic plugins containing dashes
      should be renamed` section of the Yocto Project 5.3 Migration Guide for
      more information.

   -  ``wic``: do not ignore :term:`IMAGE_ROOTFS_SIZE` if the Rootfs is
      modified.

   -  Several improvements in WIC selftests.

   -  ``bootimg_efi.py``: fail build if no binaries are installed.

   -  Add new options to the ``wic`` ``ls``, ``cp``, ``rm``, and ``write``
      commands:

      -  ``--image-name``: name of the image to use the artifacts from.
      -  ``--vars``: directory with ``<image>.env`` files that store
         :term:`BitBake` variables. This directory is usually found in
         :term:`STAGING_DIR`.

   -  Add the Wic-specific option ``--extra-partition-space`` to add extra empty
      space after the space filled by the filesystem in the partition.

   -  The Wic-specific option ``--extra-space`` has a new alias
      ``--extra-filesystem-space``.

   -  ``bootimg_pcbios``: move Syslinux install into separate functions, to make
      it easier to add new bootloaders.

      The Grub bootloader can now be installed with this Wic plugin.

   -  Add the Wic plugin ``extra_partition`` to install files from the
      :term:`DEPLOY_DIR_IMAGE` directory into an extra non-rootfs partition. See the
      :term:`IMAGE_EXTRA_PARTITION_FILES` variable for more information.

   -  The ``--diskid`` option was added to allow passing a :wikipedia:`MS-DOS
      </MS-DOS>` or :wikipedia:`GPT <GUID_Partition_Table>`-formatted
      disk IDs for a partition (for example: ``deadbeef-cafe-babe-f00d-cec2ea4eface``).

-  SDK-related changes:

   -  Include additional information about Meson setting in the SDK environment
      setup script (host system, CPU family, etc.).

   -  Add Go to :term:`SDK_TOOLCHAIN_LANGS`, except for the following
      architecture on which this is not supported:

      -  RISC-V 32 bits (``rv32``)
      -  PowerPC

   -  Image-based SDKs can now include `Zsh` completions by adding the
      ``zsh-completion-pkgs`` feature to the :term:`IMAGE_FEATURES` variable in
      the image recipe.

   -  The SDK build is now part of the :ref:`ref-classes-buildhistory`
      difference analysis (``buildhistory-diff`` command).

-  Testing-related changes:

   -  ``bitbake/tests/fetch``: Add tests for ``gitsm`` with git-lfs.

   -  ``bitbake/lib/bb/tests/fetch``: add a test case to ensure Git shallow
      fetch works for tag containing slashes.

   -  :ref:`ref-classes-testexport`: capture all tests and data from all layers
      (instead of the :term:`OpenEmbedded-Core (OE-Core)` layer only).

   -  OEQA:

      -  SDK:

         -  Add a test to sanity check that the generated SDK manifest was
            parsed correctly and isn't empty.

         -  Add a test to verify the manifests are generated correctly.

         -  Add helpers to check for and install packages.

         -  Add check that meson has detected the target correctly.

      -  Simplify test specification and discovery:

         -  Introduce the ``TESTSDK_CASE_DIRS`` variable to specify test
            directory types, replacing the need to modify the ``default_cases``
            class member.

         -  Discover tests from configured layers using a common discovery
            pattern (``<LAYER_DIR>/lib/oeqa/<dirname>/cases``) where
            ``<dirname>`` is specified in ``TESTSDK_CASE_DIRS``.

         -  The "buildtools" directories were renamed to follow the common
            discovery pattern (``<LAYER_DIR>/lib/oeqa/<dirname>/cases``) for
            consistency across all SDK configurations.

      -  ``selftest/reproducible``: Limit memory used by ``diffoscope`` to avoid
         triggering OOM kills.

      -  Add tests for the :ref:`ref-classes-devicetree` class.

      -  Tests for the :ref:`ref-classes-kernel-fit-image` class have been
         reworked and improved.

      -  ``data.py``: add ``skipIfNotBuildArch`` decorator, to skip tests if
         :term:`BUILD_ARCH` is not in present in the specified tuple.

      -  ``selftest``: add new test for toolchain switching.

      -  ``utils/command``: add a fast-path ``get_bb_var()`` that uses
         ``bitbake-getvar`` instead of ``bitbake -e`` when there is not
         ``postconfig`` argument passed.

      -  ``core/case``: add file exists assertion test case.

      -  ``context.py``: use :term:`TEST_SUITES` if set.

      - ``runqemu``: add new test for booting compressed images.

      -  General improvements of the parallelization of tests, namely fixing
         some tests that could spawn an unlimited number of threads leading to
         OOM kills.

      -  A new SDK test is now running for Go after ``go`` was added to
         :term:`SDK_TOOLCHAIN_LANGS`.

      -  Commands sent over SSH (using the ``OESSHTarget`` class) will now error
         when an SSH failure occurs. It is possible to ignore these errors by
         passing ``ignore_ssh_fails`` when executing a command.

-  Utility script changes:

   -  ``sstate-cache-management``: add a ``--dry-run`` argument

   -  ``yocto-check-layer``:

      -  Expect success for ``test_patches_upstream_status``. This means that
         patch files *must* include an ``Upstream-Status`` to pass with this
         script.

      -  Show the :term:`DISTRO` used when running the script.

      -  :ref:`ref-classes-yocto-check-layer` class:

         -  Refactor to be extended easily.

         -  Add a ``check_network_flag`` test that checks that no tasks other
            than :ref:`ref-tasks-fetch` can access the network.

   -  ``send-error-report``:

      -  Respect URL scheme in server name if it exists.

      -  Drop ``--no-ssl`` as the server URL specifies it with ``http://`` or
         ``https://``.

   -  ``buildstats.py``:

      -  Extend disk stats support for NVMe and flexible token count.

      -  Add tracking of network I/O per interface.

   -  ``buildstats-diff``: find last two Buildstats files if none are specified.

   -  ``pybootchartgui``:

      -  visualize ``/proc/net/dev`` network stats in graphs.

      -  account for network statistics when calculating extents.

-  Packaging changes:

   -  Export ``debugsources`` in :term:`PKGDESTWORK` as JSON. The source
      information used during packaging can be use from other tasks to have more
      detailed information on the files used during the compilation and improve
      SPDX accuracy.

   -  When using the ``ipk`` and ``rpm`` package managers, give out more possible
      reasons about unmatched packages.

      For example::

         E: Package 'catch2' has no installation candidate
         catch2 is a recipe. Its generated packages are: ['catch2-src', 'catch2-dbg', 'catch2-staticdev', 'catch2-dev', 'catch2-doc']
         Either specify a generated package or set ALLOW_EMPTY:${PN} = "1" in catch2 recipe

   -  ``package.py``: replace all files unconditionally when copying debug
      sources (passing ``-u`` to the ``cpio`` command in
      ``copydebugsources()``). This improves reproducibility.

-  LLVM related changes:

   -  Like ``gcc-source``, the LLVM project sources are part of ``work-shared``
      under :term:`TMPDIR`. The project codebase is large and sharing it offers
      performance improvements.

-  SPDX-related changes:

   -  ``spdx30``: Provide ``software_packageUrl`` field

   -  ``spdx30_tasks``: Change recipe license to "declared" (instead of
      "concluded")

   -  ``create-spdx-2.2``: support to override the version of a package in SPDX
      2 through :term:`SPDX_PACKAGE_VERSION`.

-  ``devtool`` and ``recipetool`` changes:

   -  Use ``lib/oe/license_finder`` to extract the license from source code.

   -  Calculate source paths relative to :term:`UNPACKDIR`.

   -  Allow ``recipe create`` handlers to specify bitbake tasks to run.

   -  ``create_go``: Use :ref:`ref-classes-go-mod` class instead of
      :ref:`ref-classes-go-vendor`.

   -  Go recipes are now generated with help of the new
      :ref:`ref-classes-go-mod-update-modules` class.

   -  Add a new :oe_git:`improve_kernel_cve_report.py
      </openembedded-core/tree/meta/scripts/contrib/improve_kernel_cve_report.py>`
      script in ``scripts/contrib`` for post-processing of kernel CVE data.

   -  Handle workspaces for multiconfig.

   -  Fix upgrade for recipes with Git submodules.

-  Patchtest-related changes:

   -  Multiple improvements to the tool's :oe_git:`README
      </openembedded-core/tree/scripts/patchtest.README>`.

   -  Don't match :term:`BitBake` Python expansions as GitHub usernames
      (``${@...}`` syntax).

-  Security changes:

   -  ``openssl``: add FIPS support. This can be enabled through the ``fips``
      :term:`PACKAGECONFIG`.

   -  The default :term:`Poky` template configuration
      (:yocto_git:`local.conf.sample </meta-yocto/commit/meta-poky/conf/templates/default/local.conf.sample?id=b3b659263566c4d2f2813190e72d93f8598a4c47>`)
      does not define the ``allow-empty-password``, ``empty-root-password``, and
      ``allow-root-login`` in :term:`EXTRA_IMAGE_FEATURES` anymore.

-  :term:`Toaster` changes:

   -  Adapt it for :doc:`bitbake-setup
      <bitbake:bitbake-user-manual/bitbake-user-manual-environment-setup>`.

   -  Remove hard dependency on the :term:`Poky` repository, as its :ref:`master
      branch is no longer updated <migration-guides/migration-5.3:The Poky
      repository master branch is no longer updated>`.

-  :ref:`ref-classes-cve-check` class changes:

   -  ``cve-update-db-native``: FKIE: use Secondary metric if there is no
      Primary metric.

-  New :term:`PACKAGECONFIG` options for individual recipes:

   -  ``ppp``: ``l2tp``, ``pptp``
   -  ``dropbear``: ``x11`` (renamed from ``enable-x11-forwarding``)
   -  ``gdb``: ``source-highlight``
   -  ``gstreamer1.0-plugins-bad``: ``analytics``
   -  ``mtd-utils``: ``ubihealthd-service``
   -  ``openssl``: ``fips``
   -  ``qemu``: ``sdl-image``, ``pixman``
   -  ``wget``: ``pcre2``
   -  ``mesa``: ``asahi``, ``amd``, ``svga``, ``teflon``, ``nouveau``,
      ``xmlconfig``
   -  ``dbus``: ``traditional-activation``, ``message-bus``
   -  ``cmake``: ``debugger``
   -  ``libcxx``: ``unwind-cross``
   -  ``tiff``: ``lerc``
   -  ``freetype``: ``brotli``
   -  ``gawk``: ``pma-if-64bit``
   -  ``x264``: ``ffmpeg``, ``opencl``

-  Systemd related changes:

   -  Enable getty generator by default by adding ``serial-getty-generator`` to
      :term:`PACKAGECONFIG`.

   -  Now uses the :term:`USE_NLS` variable to enable or disable building
      translations.

   -  Fix deduplicated templates and instance lines in preset files when listing
      both template and instances in :term:`SYSTEMD_SERVICE`.

   -  Stop enabling non-standard MAC policy when using the 'pni-names' feature
      (part of :term:`DISTRO_FEATURES`). Instead, follow what is provided by
      upstream systemd.

   -  Install ``systemd-sysv-install`` when using the
      ``systemd-systemctl-native`` recipe.

-  :ref:`ref-classes-sanity` class changes:

   -  :ref:`ref-classes-insane`: Move test for invalid :term:`PACKAGECONFIG` to
      :ref:`ref-tasks-recipe-qa`.

   -  Add ``unimplemented-ptest`` detection for cargo-based tests, allowing to
      detect when a cargo package has available tests that could be enable with
      :doc:`Ptest </test-manual/ptest>`.

   -  Add a test for recipe naming/class mismatches.

   -  Add a sanity test for "bad" gcc installs on Ubuntu 24.04. The host should
      install ``libstdc++-14-dev`` instead of ``libgcc-14-dev`` to avoid build
      issues when building :ref:`ref-classes-native` with Clang.

   -  Drop the ``var-undefined`` QA check as it was not relevant for the
      variables it was checking, as those are mandatory by default.

-  U-boot related changes:

   -  :ref:`ref-classes-uboot-sign`: Add support for setting firmware property
      in FIT configuration with :term:`UBOOT_FIT_CONF_FIRMWARE`.

   -  :ref:`ref-classes-uboot-sign`: Add support for signing U-Boot FIT image
      without an SPL. The :term:`SPL_DTB_BINARY` variable can be set to an empty
      string to indicate that no SPL is present.

   -  When built for the RISC-V architecture, read the :term:`TUNE_FEATURES`
      variable to automatically set U-boot configuration options (for example
      ``CONFIG_RISCV_ISA_F``).

   -  Improve the way build directories are split when having multiple
      configurations listed in :term:`UBOOT_CONFIG`. This fixes an issue where
      two or more of these configurations were using the same directory for
      building (because these were using the same defconfig file).

-  Miscellaneous changes:

   -  ``dropbear``: The ``dropbearkey.service`` can now take extra arguments for
      key generation, through ``/etc/default/dropbear``.

   -  ``initscripts``: add ``log_success_msg``/``log_failure_msg``/``log_warning_msg``
      functions for logging in initscripts.

   -  ``connman``:

      -  Mark ``iptables`` and ``nftables`` feature of :term:`PACKAGECONFIG`
         mutually incompatible.

      -  Set ``dns-backend`` automatically to ``systemd-resolved``
         when ``systemd-resolved`` is part of :term:`DISTRO_FEATURES`.

   -  ``uninative``: show errors if installing fails.

   -  ``meson``: Allow user to override setup command options by exporting
      ``MESON_SETUP_OPTS`` in a recipe.

   -  :ref:`ref-classes-cmake`: Enhance to emit a native toolchain CMake file.
      This is part of improvements allowing to use ``clang`` in an SDK.

   -  Fix the runtime version of several recipes (they now return the effective
      version instead of a default string like "Unknown").

   -  :ref:`ref-classes-module`: add ``KBUILD_EXTRA_SYMBOLS`` to the install
      command.

   -  ``rpm-sequoia``: add :doc:`Ptest </test-manual/ptest>` support.

   -  ``libunwind``: disable installation of tests directory with
      ``--disable-tests``, which can be installed with the ``libunwind-ptest``
      package instead.

   -  ``boost``: add ``process`` library to the list of built libraries.

   -  ``base-files``: add ``nsswitch-resolved.conf``, only installed if
      ``systemd`` and ``systemd-resolved`` is part of :term:`DISTRO_FEATURES`.

   -  ``nfs-utils``: don't use signals to shut down the NFS server in the
      associated initscript, instead use ``rpc.nfsd 0``.

   -  ``readline``: enable HOME, END, INSERT, and DELETE key bindings in
      ``inputrc``.

   -  Switch to a new :ref:`sstate-cache <overview-manual/concepts:shared state
      cache>` CDN (http://sstate.yoctoproject.org).

   -  :ref:`ref-classes-sstate`: Apply a proper :manpage:`umask` when fetching
      from :term:`SSTATE_MIRRORS`.

   -  ``kernel-devsrc``: make package version consistent with kernel source (by
      inheriting :ref:`ref-classes-kernelsrc`).

   -  :ref:`ref-classes-externalsrc`: Always ask Git for location of ``.git``
      directory (may be different from the default ``${S}/.git``).

   -  :ref:`ref-classes-features_check`: Add support for :term:`REQUIRED_TUNE_FEATURES`.

   -  ``openssh``: limit read access to ``sshd_config`` file (set its filemode
      to ``0600``).

   -  ``barebox-tools`` now installs the ``rk-usb-loader`` utility.

   -  The :ref:`ref-classes-setuptools3_legacy` class now supports the
      :ref:`qa-check-pep517-backend` QA check.

   -  The :ref:`ref-classes-ccache` class now supports using `Ccache` for native
      recipes when the local build configuration contains::

         ASSUME_PROVIDED += "ccache-native"
         HOSTTOOLS += "ccache"

   -  :ref:`ref-classes-python_pep517`: use ``pyproject-build`` instead of
      calling the module with ``nativepython3``.

   -  ``dbus-glib``: include the binding tools separately into the
      ``${PN}-tools`` package.

   -  ``dbus``: use the :ref:`ref-classes-systemd` class to handle the unit
      files of D-Bus.

   -  ``dpkg``: add :ref:`ptest <test-manual/ptest:testing packages with ptest>`
      support.

   -  ``shared-mime-info``: Now uses the :term:`USE_NLS` variable to enable
      building translations.

   -  ``p11-kit``: Now uses the :term:`USE_NLS` variable to enable building
      translations.

   -  ``babeltrace2``: Enable Python plugins

   -  ``initramfs-framework``: mount a temporary filesystem on ``/run`` and move
      it to the root filesystem directory before calling ``switch_root``.

   -  ``python3``: Pass ``PLATFORM_TRIPLET`` explicitly when cross compiling to
      make the build deterministic instead of letting Python detect the platform
      triplet (``${HOST_ARCH}-${HOST_OS}``).

   -  ``pulseaudio``: Add the ``audio`` group explicitly if
      ``pulseaudio-server`` is used.

   -  ``oe/license_finder``: Add ``find_licenses_up`` function to find licenses
      upwards until reaching a predefined top directory (as an argument).

   -  ``mesa``:

      -  Build Mesa's Asahi tools when ``asahi`` is part of the recipe's
         :term:`PACKAGECONFIG` variable.

      -  The ``mesa`` recipe now declares two new :term:`PROVIDES` for Vulkan
         and OpenCL ICD. These virtual provider are respectively named
         ``virtual-opencl-icd`` and ``virtual-vulkan-icd``.

   -  ``mesa-demos``: split info tools to a separate package ``mesa-demos-info``.

   -  ``vte``: skip :ref:`ref-classes-gobject-introspection` with Clang on Arm,
      as it caused build failures.

   -  ``shadow``: Increase the maximum group name length from 24 to 32 (default
      value provided by upstream recipe, was previously hardcoded to 24).

   -  ``udev-extraconf``: Speed up the ``mount.sh`` script by passing the block
      device of interest to ``blkid`` when getting partition label names.

   -  ``piglit``: enable OpenCL support if ``opencl`` is part of the
      :term:`DISTRO` features.

Known Issues in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~

N/A

Recipe License changes in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

..
   Going through commits on OE-Core filtered by License-Update:
   git log -U0 --patch --grep "License-Update:" yocto-5.2..origin/master

The following changes have been made to the :term:`LICENSE` values set by recipes:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous value
     - New value
   * - ``flac``
     - ``GFDL-1.2 & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-3-Clause``
     - ``GFDL-1.3 & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-3-Clause``
   * - ``rust``
     - ``(MIT | Apache-2.0) & Unicode-TOU``
     - ``(MIT | Apache-2.0) & Unicode-3.0``
   * - ``vulkan-validation-layers``
     - ``Apache-2.0 & MIT``
     - ``Apache-2.0 & MIT & BSL-1.0``
   * - ``util-linux``
     - ``GPL-1.0-or-later & GPL-2.0-only & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-2-Clause & B SD-3-Clause & BSD-4-Clause-UC & MIT``
     - ``GPL-1.0-or-later & GPL-2.0-only & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-2-Clause & B SD-3-Clause & BSD-4-Clause-UC & MIT & EUPL-1.2``
   * - ``python3-docutils``
     - ``CC0-1.0 & ZPL-2.1 & BSD-2-Clause & GPL-3.0-only``
     - ``CC0-1.0 & BSD-2-Clause & GPL-3.0-only``
   * - ``tiff``
     - ``libtiff``
     - ``libtiff & BSD-4.3TAHOE``
   * - ``gawk``
     - ``GPL-3.0-only``
     - ``GPL-3.0-or-later & AGPL-3.0-or-later``
   * - ``go-helloworld``
     - ``MIT``
     - ``BSD-3-Clause``

Security Fixes in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

..
   Generated with documentation/tools/gen-cve-release-notes

The following CVEs have been fixed:

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Recipe
     - CVE IDs
   * - ``busybox``
     - :cve_nist:`2025-46394`
   * - ``ghostscript``
     - :cve_nist:`2025-59798`, :cve_nist:`2025-59799`, :cve_nist:`2025-59800`
   * - ``libmicrohttpd``
     - :cve_nist:`2025-59777`, :cve_nist:`2025-62689`
   * - ``libpng``
     - :cve_nist:`2025-64505`, :cve_nist:`2025-64506`, :cve_nist:`2025-64720`, :cve_nist:`2025-65018`
   * - ``libsndfile1``
     - :cve_nist:`2024-50613`, :cve_nist:`2025-52194`
   * - ``linux-yocto``
     - :cve_nist:`2019-14899`, :cve_nist:`2021-3714`, :cve_nist:`2021-3864`, :cve_nist:`2022-0400`, :cve_nist:`2022-1247`, :cve_nist:`2022-4543`, :cve_nist:`2022-38096`, :cve_nist:`2023-3397`, :cve_nist:`2023-3640`, :cve_nist:`2023-4010`, :cve_nist:`2023-6238`, :cve_nist:`2023-6240`, :cve_nist:`2023-6535`, :cve_nist:`2023-39176`, :cve_nist:`2023-39179`, :cve_nist:`2023-39180`, :cve_nist:`2024-52560`, :cve_nist:`2024-57995`, :cve_nist:`2024-58015`, :cve_nist:`2024-58074`, :cve_nist:`2024-58093`, :cve_nist:`2024-58094`, :cve_nist:`2024-58095`, :cve_nist:`2024-58096`, :cve_nist:`2024-58097`, :cve_nist:`2025-4598`, :cve_nist:`2025-21709`, :cve_nist:`2025-21751`, :cve_nist:`2025-21752`, :cve_nist:`2025-21807`, :cve_nist:`2025-21833`, :cve_nist:`2025-21949`, :cve_nist:`2025-22104`, :cve_nist:`2025-22105`, :cve_nist:`2025-22106`, :cve_nist:`2025-22107`, :cve_nist:`2025-22108`, :cve_nist:`2025-22109`, :cve_nist:`2025-22111`, :cve_nist:`2025-22116`, :cve_nist:`2025-22117`, :cve_nist:`2025-22121`, :cve_nist:`2025-22127`, :cve_nist:`2025-23129`, :cve_nist:`2025-23130`, :cve_nist:`2025-23131`, :cve_nist:`2025-23132`, :cve_nist:`2025-23135`, :cve_nist:`2025-37743`, :cve_nist:`2025-37746`, :cve_nist:`2025-37803`, :cve_nist:`2025-37860`, :cve_nist:`2025-37880`, :cve_nist:`2025-37906`, :cve_nist:`2025-38029`, :cve_nist:`2025-38036`, :cve_nist:`2025-38041`, :cve_nist:`2025-38042`, :cve_nist:`2025-38064`, :cve_nist:`2025-38105`, :cve_nist:`2025-38132`, :cve_nist:`2025-38137`, :cve_nist:`2025-38140`, :cve_nist:`2025-38187`, :cve_nist:`2025-38199`, :cve_nist:`2025-38205`, :cve_nist:`2025-38207`, :cve_nist:`2025-38234`, :cve_nist:`2025-38248`, :cve_nist:`2025-38261`, :cve_nist:`2025-38284`, :cve_nist:`2025-38311`, :cve_nist:`2025-38359`, :cve_nist:`2025-38426`, :cve_nist:`2025-38584`, :cve_nist:`2025-38591`, :cve_nist:`2025-38597`, :cve_nist:`2025-38605`, :cve_nist:`2025-38621`, :cve_nist:`2025-38627`, :cve_nist:`2025-38636`, :cve_nist:`2025-38656`, :cve_nist:`2025-38678`, :cve_nist:`2025-39677`, :cve_nist:`2025-39678`, :cve_nist:`2025-39745`, :cve_nist:`2025-39762`, :cve_nist:`2025-39764`, :cve_nist:`2025-39789`, :cve_nist:`2025-40325`
   * - ``qemu``
     - :cve_nist:`2024-6519`, :cve_nist:`2024-8354`
   * - ``webkitgtk``
     - :cve_nist:`2025-43342`, :cve_nist:`2025-43343`

Recipe Upgrades in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

..
   Generated with https://layers.openembedded.org/layerindex/branch_comparison
   With "rST" output selected

The following recipes have been upgraded:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous version(s)
     - New version(s)
   * - ``acpica``
     - 20240827
     - 20250807
   * - ``alsa-lib``
     - 1.2.13
     - 1.2.14
   * - ``alsa-tools``
     - 1.2.11
     - 1.2.14
   * - ``alsa-ucm-conf``
     - 1.2.13
     - 1.2.14
   * - ``alsa-utils``
     - 1.2.13
     - 1.2.14
   * - ``appstream``
     - 1.0.4
     - 1.0.6
   * - ``apr``
     - 1.7.5
     - 1.7.6
   * - ``apt``
     - 2.6.1
     - 3.0.3
   * - ``at-spi2-core``
     - 2.56.0
     - 2.56.4
   * - ``autoconf``
     - 2.72e
     - 2.72
   * - ``automake``
     - 1.17
     - 1.18.1
   * - ``babeltrace2``
     - 2.1.0
     - 2.1.2
   * - ``barebox``
     - 2025.02.0
     - 2025.11.0
   * - ``barebox-tools``
     - 2025.02.0
     - 2025.11.0
   * - ``base-passwd``
     - 3.6.6
     - 3.6.7
   * - ``bash``
     - 5.2.37
     - 5.3
   * - ``bc``
     - 1.08.1
     - 1.08.2
   * - ``bind``
     - 9.20.11
     - 9.20.15
   * - ``binutils``
     - 2.44
     - 2.45
   * - ``binutils-cross``
     - 2.44
     - 2.45
   * - ``binutils-cross-canadian``
     - 2.44
     - 2.45
   * - ``binutils-crosssdk``
     - 2.44
     - 2.45
   * - ``binutils-testsuite``
     - 2.44
     - 2.45
   * - ``blktrace``
     - 1.3.0+git
     - 1.3.0
   * - ``bluez5``
     - 5.79
     - 5.85
   * - ``bmaptool``
     - 3.8.0+git
     - 3.9.0+git
   * - ``boost``
     - 1.87.0
     - 1.89.0
   * - ``boost-build-native``
     - 1.87.0
     - 1.89.0
   * - ``btrfs-tools``
     - 6.13
     - 6.17
   * - ``build-appliance-image``
     - 15.0.0 (316baad50b45…)
     - 15.0.0
   * - ``ca-certificates``
     - 20241223
     - 20250419
   * - ``cargo``
     - 1.84.1
     - 1.90.0
   * - ``cargo-c``
     - 0.10.5+cargo-0.83.0
     - 0.10.16+cargo-0.91.0
   * - ``ccache``
     - 4.11
     - 4.12.1
   * - ``cmake``
     - 3.31.6
     - 4.2.0
   * - ``cmake-native``
     - 3.31.6
     - 4.2.0
   * - ``connman``
     - 1.43
     - 1.45
   * - ``coreutils``
     - 9.6
     - 9.7
   * - ``createrepo-c``
     - 1.2.0
     - 1.2.1
   * - ``cross-localedef-native``
     - 2.41+git
     - 2.42+git
   * - ``cups``
     - 2.4.11
     - 2.4.14
   * - ``curl``
     - 8.12.1
     - 8.17.0
   * - ``debianutils``
     - 5.21
     - 5.23.2
   * - ``debugedit``
     - 5.1
     - 5.2
   * - ``dhcpcd``
     - 10.2.2
     - 10.2.4
   * - ``diffoscope``
     - 289
     - 306
   * - ``diffstat``
     - 1.67
     - 1.68
   * - ``diffutils``
     - 3.11
     - 3.12
   * - ``dnf``
     - 4.22.0
     - 4.24.0
   * - ``dos2unix``
     - 7.5.2
     - 7.5.3
   * - ``dpkg``
     - 1.22.11
     - 1.22.21
   * - ``dropbear``
     - 2024.86
     - 2025.88
   * - ``e2fsprogs``
     - 1.47.1
     - 1.47.3
   * - ``ed``
     - 1.21
     - 1.22.2
   * - ``elfutils``
     - 0.192
     - 0.194
   * - ``ell``
     - 0.74
     - 0.80
   * - ``enchant2``
     - 2.8.2
     - 2.8.12
   * - ``epiphany``
     - 48.3
     - 48.5
   * - ``erofs-utils``
     - 1.8.5
     - 1.8.10
   * - ``ethtool``
     - 6.11
     - 6.15
   * - ``expat``
     - 2.7.2
     - 2.7.3
   * - ``fastfloat``
     - 8.0.2
     - 8.1.0
   * - ``ffmpeg``
     - 7.1.2
     - 8.0
   * - ``flac``
     - 1.4.3
     - 1.5.0
   * - ``fmt``
     - 11.1.4
     - 12.1.0
   * - ``fontconfig``
     - 2.15.0
     - 2.17.1
   * - ``gawk``
     - 5.3.1
     - 5.3.2
   * - ``gcc``
     - 14.3.0
     - 15.2.0
   * - ``gcc-cross``
     - 14.3.0
     - 15.2.0
   * - ``gcc-cross-canadian``
     - 14.3.0
     - 15.2.0
   * - ``gcc-crosssdk``
     - 14.3.0
     - 15.2.0
   * - ``gcc-runtime``
     - 14.3.0
     - 15.2.0
   * - ``gcc-sanitizers``
     - 14.3.0
     - 15.2.0
   * - ``gcc-source``
     - 14.3.0
     - 15.2.0
   * - ``gcr``
     - 4.3.1
     - 4.4.0.1
   * - ``gdb``
     - 16.2
     - 16.3
   * - ``gdb-cross``
     - 16.2
     - 16.3
   * - ``gdb-cross-canadian``
     - 16.2
     - 16.3
   * - ``gdbm``
     - 1.24
     - 1.26
   * - ``gettext``
     - 0.23.1
     - 0.26
   * - ``gettext-minimal-native``
     - 0.23.1
     - 0.26
   * - ``ghostscript``
     - 10.05.1
     - 10.06.0
   * - ``gi-docgen``
     - 2025.3
     - 2025.5
   * - ``git``
     - 2.49.1
     - 2.51.2
   * - ``glib-2.0``
     - 2.84.4
     - 2.86.1
   * - ``glib-2.0-initial``
     - 2.84.4
     - 2.86.1
   * - ``glib-networking``
     - 2.80.0
     - 2.80.1
   * - ``glibc``
     - 2.41+git
     - 2.42+git
   * - ``glibc-locale``
     - 2.41+git
     - 2.42+git
   * - ``glibc-mtrace``
     - 2.41+git
     - 2.42+git
   * - ``glibc-scripts``
     - 2.41+git
     - 2.42+git
   * - ``glibc-testsuite``
     - 2.41+git
     - 2.42+git
   * - ``glslang``
     - 1.4.309.0
     - 1.4.328.1
   * - ``gnu-config``
     - 20240823+git
     - 20250709+git
   * - ``gnu-efi``
     - 4.0.0
     - 4.0.2
   * - ``gnupg``
     - 2.5.5
     - 2.5.11
   * - ``go``
     - 1.24.6
     - 1.25.4
   * - ``go-binary-native``
     - 1.24.6
     - 1.25.4
   * - ``go-cross-canadian``
     - 1.24.6
     - 1.25.4
   * - ``go-cross-core2-32``
     - 1.24.6
     - 1.25.4
   * - ``go-crosssdk``
     - 1.24.6
     - 1.25.4
   * - ``go-helloworld``
     - 0.1 (d7b0ac127859…)
     - 0.1 (8b405629c4a5…)
   * - ``go-runtime``
     - 1.24.6
     - 1.25.4
   * - ``gperf``
     - 3.1
     - 3.3
   * - ``gpgme``
     - 1.24.2
     - 2.0.1
   * - ``grep``
     - 3.11
     - 3.12
   * - ``gsettings-desktop-schemas``
     - 48.0
     - 49.1
   * - ``gst-devtools``
     - 1.24.13
     - 1.26.7
   * - ``gst-examples``
     - 1.18.6
     - 1.26.7
   * - ``gstreamer1.0``
     - 1.24.13
     - 1.26.7
   * - ``gstreamer1.0-libav``
     - 1.24.13
     - 1.26.7
   * - ``gstreamer1.0-plugins-bad``
     - 1.24.13
     - 1.26.7
   * - ``gstreamer1.0-plugins-base``
     - 1.24.13
     - 1.26.7
   * - ``gstreamer1.0-plugins-good``
     - 1.24.13
     - 1.26.7
   * - ``gstreamer1.0-plugins-ugly``
     - 1.24.13
     - 1.26.7
   * - ``gstreamer1.0-python``
     - 1.24.13
     - 1.26.7
   * - ``gstreamer1.0-rtsp-server``
     - 1.24.13
     - 1.26.7
   * - ``gstreamer1.0-vaapi``
     - 1.24.13
     - 1.26.7
   * - ``gtk+3``
     - 3.24.43
     - 3.24.51
   * - ``gtk4``
     - 4.18.1
     - 4.18.6
   * - ``gzip``
     - 1.13
     - 1.14
   * - ``harfbuzz``
     - 10.4.0
     - 11.5.1
   * - ``hwdata``
     - 0.393
     - 0.399
   * - ``hwlatdetect``
     - 2.8
     - 2.9
   * - ``icu``
     - 76-1
     - 77-1
   * - ``igt-gpu-tools``
     - 1.30
     - 2.1
   * - ``init-system-helpers``
     - 1.68
     - 1.69
   * - ``iproute2``
     - 6.13.0
     - 6.17.0
   * - ``iputils``
     - 20240905
     - 20250605
   * - ``iso-codes``
     - 4.17.0
     - 4.18.0
   * - ``iw``
     - 6.9
     - 6.17
   * - ``json-glib``
     - 1.10.6
     - 1.10.8
   * - ``kbd``
     - 2.7.1
     - 2.8.0
   * - ``kea``
     - 2.6.3
     - 3.0.1
   * - ``kern-tools-native``
     - 0.3+git (c8c1f17867d0…)
     - 0.3+git (f589e1df2325…)
   * - ``kexec-tools``
     - 2.0.30
     - 2.0.31
   * - ``kmod``
     - 34.1
     - 34.2
   * - ``kmscube``
     - 0.0.1+git (311eaaaa473d…)
     - 0.0.1+git (f60e50e887d3…)
   * - ``less``
     - 668
     - 685
   * - ``libadwaita``
     - 1.7.0
     - 1.7.6
   * - ``libarchive``
     - 3.7.9
     - 3.8.3
   * - ``libatomic-ops``
     - 7.8.2
     - 7.8.4
   * - ``libc-test``
     - 0+git (18e28496adee…)
     - 0+git (f2bac7711bec…)
   * - ``libcap``
     - 2.75
     - 2.77
   * - ``libcgroup``
     - 3.1.0
     - 3.2.0
   * - ``libcomps``
     - 0.1.21
     - 0.1.23
   * - ``libdnf``
     - 0.73.4
     - 0.75.0
   * - ``libdrm``
     - 2.4.124
     - 2.4.128
   * - ``libedit``
     - 20250104-3.1
     - 20251016-3.1
   * - ``libevdev``
     - 1.13.3
     - 1.13.5
   * - ``libffi``
     - 3.4.7
     - 3.5.2
   * - ``libgcc``
     - 14.3.0
     - 15.2.0
   * - ``libgcc-initial``
     - 14.3.0
     - 15.2.0
   * - ``libgcrypt``
     - 1.11.0
     - 1.11.2
   * - ``libgfortran``
     - 14.3.0
     - 15.2.0
   * - ``libgit2``
     - 1.9.0
     - 1.9.1
   * - ``libgloss``
     - 4.4.0+git
     - 4.5.0+git
   * - ``libgpg-error``
     - 1.51
     - 1.56
   * - ``libinput``
     - 1.27.1
     - 1.29.1
   * - ``libjitterentropy``
     - 3.6.2
     - 3.6.3
   * - ``libjpeg-turbo``
     - 3.0.1
     - 3.1.2
   * - ``libmicrohttpd``
     - 1.0.1
     - 1.0.2
   * - ``libmodulemd``
     - 2.15.0
     - 2.15.2
   * - ``libnotify``
     - 0.8.4
     - 0.8.6
   * - ``libnss-nis``
     - 3.2
     - 3.4
   * - ``libogg``
     - 1.3.5
     - 1.3.6
   * - ``libpng``
     - 1.6.47
     - 1.6.50
   * - ``libproxy``
     - 0.5.9
     - 0.5.10
   * - ``librepo``
     - 1.19.0
     - 1.20.0
   * - ``librsvg``
     - 2.59.2
     - 2.61.0
   * - ``libsdl2``
     - 2.32.2
     - 2.32.10
   * - ``libsecret``
     - 0.21.6
     - 0.21.7
   * - ``libslirp``
     - 4.9.0
     - 4.9.1
   * - ``libsolv``
     - 0.7.31
     - 0.7.35
   * - ``libstd-rs``
     - 1.84.1
     - 1.90.0
   * - ``libtheora``
     - 1.1.1
     - 1.2.0
   * - ``libucontext``
     - 1.2
     - 1.3.2
   * - ``libunistring``
     - 1.3
     - 1.4.1
   * - ``libunwind``
     - 1.6.2
     - 1.8.3
   * - ``liburcu``
     - 0.15.1
     - 0.15.3
   * - ``libusb1``
     - 1.0.27
     - 1.0.29
   * - ``libuv``
     - 1.50.0
     - 1.51.0
   * - ``libwebp``
     - 1.5.0
     - 1.6.0
   * - ``libwpe``
     - 1.16.2
     - 1.16.3
   * - ``libxfixes``
     - 6.0.1
     - 6.0.2
   * - ``libxft``
     - 2.3.8
     - 2.3.9
   * - ``libxkbcommon``
     - 1.8.1
     - 1.11.0
   * - ``libxml2``
     - 2.13.8
     - 2.14.6
   * - ``libxmlb``
     - 0.3.22
     - 0.3.23
   * - ``libxres``
     - 1.2.2
     - 1.2.3
   * - ``libxscrnsaver``
     - 1.2.4
     - 1.2.5
   * - ``lighttpd``
     - 1.4.77
     - 1.4.81
   * - ``linux-firmware``
     - 20250311
     - 20251111
   * - ``linux-libc-headers``
     - 6.12
     - 6.17
   * - ``linux-yocto``
     - 6.12.47+git
     - 6.12.55+git, 6.16.11+git, 6.17.6+git
   * - ``linux-yocto-dev``
     - 6.14+git
     - 6.18+git
   * - ``linux-yocto-rt``
     - 6.12.47+git
     - 6.12.55+git, 6.16.11+git, 6.17.6+git
   * - ``linux-yocto-tiny``
     - 6.12.47+git
     - 6.12.55+git, 6.16.11+git, 6.17.6+git
   * - ``llvm``
     - 20.1.0
     - 21.1.6
   * - ``lsof``
     - 4.99.4
     - 4.99.5
   * - ``ltp``
     - 20250130
     - 20250930
   * - ``lttng-modules``
     - 2.13.17
     - 2.14.3
   * - ``lttng-tools``
     - 2.13.14
     - 2.14.0
   * - ``lttng-ust``
     - 2.13.8
     - 2.14.0
   * - ``lua``
     - 5.4.7
     - 5.4.8
   * - ``m4``
     - 1.4.19
     - 1.4.20
   * - ``m4-native``
     - 1.4.19
     - 1.4.20
   * - ``man-db``
     - 2.13.0
     - 2.13.1
   * - ``man-pages``
     - 6.13
     - 6.15
   * - ``mdadm``
     - 4.3
     - 4.4
   * - ``mesa``
     - 24.0.7
     - 25.2.5
   * - ``mesa-gl``
     - 24.0.7
     - 25.2.5
   * - ``meson``
     - 1.7.0
     - 1.9.1
   * - ``mmc-utils``
     - 0.1+git
     - 1.0
   * - ``mobile-broadband-provider-info``
     - 20240407
     - 20250613
   * - ``mpfr``
     - 4.2.1
     - 4.2.2
   * - ``mpg123``
     - 1.32.10
     - 1.33.2
   * - ``msmtp``
     - 1.8.28
     - 1.8.31
   * - ``musl``
     - 1.2.5+git (c47ad25ea3b4…)
     - 1.2.5+git (0ccaf0572e9c…)
   * - ``nettle``
     - 3.10.1
     - 3.10.2
   * - ``newlib``
     - 4.4.0+git
     - 4.5.0+git
   * - ``nfs-utils``
     - 2.8.2
     - 2.8.4
   * - ``nghttp2``
     - 1.65.0
     - 1.68.0
   * - ``ninja``
     - 1.12.1
     - 1.13.1
   * - ``ofono``
     - 2.15
     - 2.18
   * - ``opensbi``
     - 1.6
     - 1.7
   * - ``openssh``
     - 9.9p2
     - 10.2p1
   * - ``openssl``
     - 3.4.2
     - 3.5.4
   * - ``opkg``
     - 0.7.0
     - 0.9.0
   * - ``ovmf``
     - edk2-stable202411
     - edk2-stable202508
   * - ``pango``
     - 1.56.2
     - 1.57.0
   * - ``patch``
     - 2.7.6
     - 2.8
   * - ``patchelf``
     - 0.18.0
     - 0.18.0+git
   * - ``pciutils``
     - 3.13.0
     - 3.14.0
   * - ``piglit``
     - 1.0+gitr (fc8179d31904…)
     - 1.0+gitr (a0a27e528f64…)
   * - ``pinentry``
     - 1.3.1
     - 1.3.2
   * - ``pixman``
     - 0.44.2
     - 0.46.4
   * - ``pkgconf``
     - 2.4.3
     - 2.5.1
   * - ``pseudo``
     - 1.9.0+git
     - 1.9.2+git
   * - ``psplash``
     - 0.1+git (1f64c654129f…)
     - 0.1+git (53ae74a36bf1…)
   * - ``puzzles``
     - 0.0+git (7da464122232…)
     - 0.0+git (a7c7826bce5c…)
   * - ``python3``
     - 3.13.4
     - 3.13.9
   * - ``python3-beartype``
     - 0.20.0
     - 0.21.0
   * - ``python3-booleanpy``
     - 4.0
     - 5.0
   * - ``python3-build``
     - 1.2.2
     - 1.3.0
   * - ``python3-calver``
     - 2022.6.26
     - 2025.04.17
   * - ``python3-certifi``
     - 2025.1.31
     - 2025.8.3
   * - ``python3-click``
     - 8.1.8
     - 8.2.2
   * - ``python3-cryptography``
     - 44.0.2
     - 45.0.7
   * - ``python3-cryptography-vectors``
     - 44.0.2
     - 45.0.7
   * - ``python3-cython``
     - 3.0.12
     - 3.1.3
   * - ``python3-dbusmock``
     - 0.33.0
     - 0.37.0
   * - ``python3-docutils``
     - 0.21.2
     - 0.22
   * - ``python3-dtschema``
     - 2025.2
     - 2025.8
   * - ``python3-flit-core``
     - 3.11.0
     - 3.12.0
   * - ``python3-hatch-fancy-pypi-readme``
     - 24.1.0
     - 25.1.0
   * - ``python3-hatch-vcs``
     - 0.4.0
     - 0.5.0
   * - ``python3-hypothesis``
     - 6.129.2
     - 6.142.2
   * - ``python3-idna``
     - 3.10
     - 3.11
   * - ``python3-iniconfig``
     - 2.0.0
     - 2.1.0
   * - ``python3-jsonschema``
     - 4.23.0
     - 4.25.1
   * - ``python3-jsonschema-specifications``
     - 2024.10.1
     - 2025.9.1
   * - ``python3-license-expression``
     - 30.4.1
     - 30.4.4
   * - ``python3-lxml``
     - 5.3.1
     - 6.0.2
   * - ``python3-mako``
     - 1.3.9
     - 1.3.10
   * - ``python3-markdown``
     - 3.7
     - 3.9
   * - ``python3-maturin``
     - 1.8.3
     - 1.9.4
   * - ``python3-meson-python``
     - 0.17.1
     - 0.18.0
   * - ``python3-numpy``
     - 2.2.3
     - 2.3.4
   * - ``python3-packaging``
     - 24.2
     - 25.0
   * - ``python3-pbr``
     - 6.1.0
     - 7.0.1
   * - ``python3-pip``
     - 25.0.1
     - 25.2
   * - ``python3-pluggy``
     - 1.5.0
     - 1.6.0
   * - ``python3-poetry-core``
     - 2.1.1
     - 2.1.3
   * - ``python3-pycairo``
     - 1.27.0
     - 1.28.0
   * - ``python3-pycryptodome``
     - 3.22.0
     - 3.23.0
   * - ``python3-pycryptodomex``
     - 3.22.0
     - 3.23.0
   * - ``python3-pygments``
     - 2.19.1
     - 2.19.2
   * - ``python3-pygobject``
     - 3.52.2
     - 3.54.5
   * - ``python3-pyopenssl``
     - 25.0.0
     - 25.1.0
   * - ``python3-pyparsing``
     - 3.2.1
     - 3.2.4
   * - ``python3-pytest``
     - 8.3.5
     - 8.4.2
   * - ``python3-pytest-subtests``
     - 0.14.1
     - 0.14.2
   * - ``python3-pytz``
     - 2025.1
     - 2025.2
   * - ``python3-rdflib``
     - 7.1.3
     - 7.1.4
   * - ``python3-referencing``
     - 0.36.2
     - 0.37.0
   * - ``python3-requests``
     - 2.32.4
     - 2.32.5
   * - ``python3-rpds-py``
     - 0.22.3
     - 0.27.1
   * - ``python3-ruamel-yaml``
     - 0.18.10
     - 0.18.15
   * - ``python3-scons``
     - 4.9.0
     - 4.9.1
   * - ``python3-setuptools``
     - 76.0.0
     - 80.9.0
   * - ``python3-setuptools-rust``
     - 1.11.0
     - 1.12.0
   * - ``python3-setuptools-scm``
     - 8.2.0
     - 8.3.1
   * - ``python3-smartypants``
     - 2.0.0
     - 2.0.2
   * - ``python3-snowballstemmer``
     - 2.2.0
     - 3.0.1
   * - ``python3-trove-classifiers``
     - 2025.3.13.13
     - 2025.9.11.17
   * - ``python3-typing-extensions``
     - 4.12.2
     - 4.15.0
   * - ``python3-unittest-automake-output``
     - 0.2
     - 0.3
   * - ``python3-uritools``
     - 4.0.3
     - 5.0.0
   * - ``python3-urllib3``
     - 2.3.0
     - 2.5.0
   * - ``python3-webcolors``
     - 24.8.0
     - 24.11.1
   * - ``python3-wheel``
     - 0.45.1
     - 0.46.1
   * - ``python3-xmltodict``
     - 0.14.2
     - 0.15.1
   * - ``python3-yamllint``
     - 1.36.0
     - 1.37.1
   * - ``python3-zipp``
     - 3.21.0
     - 3.23.0
   * - ``qemu``
     - 9.2.0
     - 10.0.6
   * - ``qemu-native``
     - 9.2.0
     - 10.0.6
   * - ``qemu-system-native``
     - 9.2.0
     - 10.0.6
   * - ``quilt``
     - 0.68
     - 0.69
   * - ``quilt-native``
     - 0.68
     - 0.69
   * - ``quota``
     - 4.09
     - 4.10
   * - ``re2c``
     - 4.1
     - 4.3
   * - ``readline``
     - 8.2.13
     - 8.3
   * - ``repo``
     - 2.52
     - 2.58
   * - ``resolvconf``
     - 1.92
     - 1.94
   * - ``rpcbind``
     - 1.2.7
     - 1.2.8
   * - ``rpm``
     - 4.20.0
     - 4.20.1
   * - ``rpm-sequoia``
     - 1.7.0
     - 1.9.0
   * - ``rpm-sequoia-crypto-policy``
     - git (032b418a6db8…)
     - git (ae1df75b1155…)
   * - ``rt-tests``
     - 2.8
     - 2.9
   * - ``ruby``
     - 3.4.4
     - 3.4.5
   * - ``rust``
     - 1.84.1
     - 1.90.0
   * - ``rust-cross-canadian``
     - 1.84.1
     - 1.90.0
   * - ``sbc``
     - 2.0
     - 2.1
   * - ``scdoc``
     - 1.11.3
     - 1.11.4
   * - ``screen``
     - 5.0.0
     - 5.0.1
   * - ``shaderc``
     - 2024.3
     - 2025.3
   * - ``shadow``
     - 4.17.3
     - 4.18.0
   * - ``spirv-headers``
     - 1.4.309.0
     - 1.4.328.1
   * - ``spirv-tools``
     - 1.4.309.0
     - 1.4.328.1
   * - ``squashfs-tools``
     - 4.6.1
     - 4.7.4
   * - ``strace``
     - 6.12
     - 6.17
   * - ``stress-ng``
     - 0.18.11
     - 0.19.04
   * - ``sudo``
     - 1.9.17p1
     - 1.9.17p2
   * - ``swig``
     - 4.3.0
     - 4.3.1
   * - ``sysklogd``
     - 2.7.1
     - 2.7.2
   * - ``sysstat``
     - 12.7.7
     - 12.7.8
   * - ``systemd``
     - 257.6
     - 257.8
   * - ``systemd-boot``
     - 257.6
     - 257.8
   * - ``systemd-boot-native``
     - 257.6
     - 257.8
   * - ``systemd-systemctl-native``
     - 257.6
     - 257.8
   * - ``systemtap``
     - 5.2
     - 5.3
   * - ``systemtap-native``
     - 5.2
     - 5.3
   * - ``taglib``
     - 2.0.2
     - 2.1.1
   * - ``tcf-agent``
     - 1.8.0+git
     - 1.9.0
   * - ``tcl``
     - 9.0.1
     - 9.0.2
   * - ``tcl8``
     - 8.6.16
     - 8.6.17
   * - ``tiff``
     - 4.7.0
     - 4.7.1
   * - ``ttyrun``
     - 2.37.0
     - 2.38.0
   * - ``u-boot``
     - 2025.01
     - 2025.10
   * - ``u-boot-tools``
     - 2025.01
     - 2025.10
   * - ``unfs3``
     - 0.10.0
     - 0.11.0
   * - ``usbutils``
     - 018
     - 019
   * - ``util-linux``
     - 2.40.4
     - 2.41.1
   * - ``util-linux-libuuid``
     - 2.40.4
     - 2.41.1
   * - ``valgrind``
     - 3.24.0
     - 3.25.1
   * - ``vim``
     - 9.1.1652
     - 9.1.1683
   * - ``vim-tiny``
     - 9.1.1652
     - 9.1.1683
   * - ``virglrenderer``
     - 1.1.0
     - 1.1.1
   * - ``vte``
     - 0.78.2
     - 0.82.1
   * - ``vulkan-headers``
     - 1.4.309.0
     - 1.4.328.1
   * - ``vulkan-loader``
     - 1.4.309.0
     - 1.4.328.1
   * - ``vulkan-samples``
     - git (8547ce1022a1…)
     - git (d27205d14d01…)
   * - ``vulkan-tools``
     - 1.4.309.0
     - 1.4.328.1
   * - ``vulkan-utility-libraries``
     - 1.4.309.0
     - 1.4.328.1
   * - ``vulkan-validation-layers``
     - 1.4.309.0
     - 1.4.328.1
   * - ``vulkan-volk``
     - 1.4.309.0
     - 1.4.328.1
   * - ``wayland``
     - 1.23.1
     - 1.24.0
   * - ``wayland-protocols``
     - 1.41
     - 1.45
   * - ``webkitgtk``
     - 2.48.2
     - 2.50.0
   * - ``weston``
     - 14.0.1
     - 14.0.2
   * - ``which``
     - 2.21
     - 2.23
   * - ``wireless-regdb``
     - 2025.02.20
     - 2025.10.07
   * - ``xdpyinfo``
     - 1.3.4
     - 1.4.0
   * - ``xinput-calibrator``
     - 0.7.5+git
     - 0.8.0
   * - ``xkeyboard-config``
     - 2.44
     - 2.45
   * - ``xwayland``
     - 24.1.6
     - 24.1.8
   * - ``xz``
     - 5.6.4
     - 5.8.1

Contributors to |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~

..
   List obtained with the following shell snippet:

      authors=""
      for repo in openembedded-core yocto-docs bitbake meta-yocto; do
         authors="${authors}\n$(git --no-pager -C $repo log --format="-  %an" yocto-5.2..origin/master)"
      done
      echo $authors | sort | uniq

   Email addresses removed.

Thanks to the following people who contributed to this release:

-  Adam Blank
-  Adam Nilsson
-  Adrian Freihofer
-  Ahmad Fatoum
-  Alejandro Hernandez Samaniego
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Alex Kiernan
-  Alon Bar-Lev
-  Alper Ak
-  Andreas Stergiopoulos
-  Andrej Valek
-  Anibal Limon
-  Ankur Tyagi
-  Anna-Lena Marx
-  Antonin Godard
-  Anuj Mittal
-  Archana Polampalli
-  Barne Carstensen
-  Benjamin Missey
-  Benjamin Szőke
-  Bo Sun
-  Bruce Ashfield
-  Carlos Sánchez de La Lama
-  Changqing Li
-  Chen Qi
-  Chris Laplante
-  Christian Lindeberg
-  Christos Gavros
-  Daisuke Yamane
-  Daniel McGregor
-  Daniel Turull
-  Daniel Wagenknecht
-  Dan McGregor
-  Dario Binacchi
-  David Reyna
-  Deepak Rathore
-  Deepesh Varatharajan
-  denisova-ok
-  Denys Dmytriyenko
-  Diego de los Santos
-  Diego Sueiro
-  Divya Chellam
-  Dixit Parmar
-  Dmitry Baryshkov
-  Enrico Jörns
-  Enrico Scholz
-  Erick Shepherd
-  Erik Lindsten
-  Etienne Cordonnier
-  Fabio Estevam
-  Falk Bauer
-  Gregor Herburger
-  Guðni Már Gilbert
-  Gyorgy Sarvari
-  Haixiao Yan
-  Harish Sadineni
-  Hiago De Franco
-  hongxu
-  Hongxu Jia
-  Ines KCHELFI
-  Isaac True
-  Jaeyoon Jung
-  Jamin Lin
-  Jan Vermaete
-  Jason M. Bills
-  Jason Schonberg
-  Jayasurya Maganuru
-  Jeroen Hofstee
-  Jiaying Song
-  Jimmy Ho
-  Jinfeng Wang
-  João Henrique Ferreira de Freitas
-  Joao Marcos Costa
-  João Marcos Costa
-  Johannes Schneider
-  Jonathan Schnitzler
-  Jon Mason
-  Jörg Sommer
-  Jose Quaresma
-  Joshua Watt
-  J. S.
-  Julian Haller
-  Kai Kang
-  Kavinaya S
-  Keerthivasan Raghavan
-  Khang D Nguyen
-  Khem Raj
-  Koch, Stefan
-  Koen Kooi
-  Kyungjik Min
-  Lamine REHAHLIA
-  Lee Chee Yang
-  leimaohui
-  Lei Maohui
-  Leon Anavi
-  Leonard Anderweit
-  Libo Chen
-  Liu Yiding
-  Li Wang
-  Louis Rannou
-  Luca Fancellu
-  Madhu Marri
-  Manuel Leonhardt
-  Marco Cavallini
-  Mark Asselstine
-  Mark Hatle
-  Markus Kurz
-  Markus Volk
-  Martin Jansa
-  Martin Siegumfeldt
-  Mathieu Dubois-Briand
-  Michael Halstead
-  Michael Jeanson
-  Michael Opdenacker
-  Michael Tretter
-  Michal Sieron
-  Mike Crowe
-  Mike Looijmans
-  Mikko Rapeli
-  Ming Liu
-  Mingli Yu
-  Moritz Haase
-  NeilBrown
-  Nguyen Dat Tho
-  Nikhil R
-  Niko Mauno
-  Ola x Nilsson
-  Olga Denisova
-  Osama Abdelkader
-  Osose Itua
-  Ovidiu Panait
-  Patrick Vogelaar
-  Patrick Williams
-  Patryk Seregiet
-  Paul Barker
-  Paul Gortmaker
-  Pedro Ferreira
-  Per x Johansson
-  Peter Kjellerstedt
-  Peter Marko
-  Peter Tatrai
-  Petr Vorel
-  Philip Lorenz
-  Philippe-Alexandre Mathieu
-  Pierre-Loup GOSSE
-  Poonam Jadhav
-  Praveen Kumar
-  Priyal Doshi
-  Quentin Schulz
-  rajmohan r
-  Randolph Sapp
-  Randy MacLeod
-  Raphael Schlarb
-  Rasmus Villemoes
-  Ricardo Salveti
-  Ricardo Simoes
-  Richard Grünert
-  Richard Purdie
-  Robbin Van Damme
-  Robert P. J. Day
-  Robert Tiemann
-  Robert Yang
-  Rogerio Guerra Borin
-  Ross Burton
-  Ryan Eatmon
-  Samuli Piippo
-  Sandeep Gundlupet Raju
-  Saravanan
-  Siddharth Doshi
-  Simone Weiß
-  Soumya Sambu
-  Stefan Eichenberger
-  Stefan Koch
-  Steffen Greber
-  Steve Sakoman
-  Talel BELHAJ SALEM
-  Thomas Perrot
-  Thune Tran
-  Tim Orling
-  Tobias Pistora
-  Tom Hochstein
-  Trevor Gamblin
-  Trevor Woerner
-  Tudor Ambarus
-  Uwe Kleine-König
-  Veeresh Kadasani
-  Victor Kamensky
-  Vijay Anusuri
-  Vincent Davis Jr
-  Vishwas Udupa
-  Vivek Puar
-  Vyacheslav Yurkov
-  Walter Werner SCHNEIDER
-  Wang Mingyu
-  Weisser, Pascal.ext
-  Yannic Moog
-  Yash Shinde
-  Yi Zhao
-  Yoann Congal
-  Yogesh Tyagi
-  Yogita Urade
-  Zhang Peng
-  Zoltan Boszormenyi
-  Zoltán Böszörményi

Repositories / Downloads for Yocto-|yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`whinlatter </yocto-docs/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3 </yocto-docs/log/?h=yocto-5.3>`
-  Git Revision: :yocto_git:`79cd33b06e87c04e4f873a5afd9d53714bc5047f </yocto-docs/commit/?id=79cd33b06e87c04e4f873a5afd9d53714bc5047f>`
-  Release Artefact: yocto-docs-79cd33b06e87c04e4f873a5afd9d53714bc5047f
-  sha: f2a353847243370d6924ea83542b055ee8098c51d4875b6a9b2cee79b98c0f29
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3/yocto-docs-79cd33b06e87c04e4f873a5afd9d53714bc5047f.tar.bz2 

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3/yocto-docs-79cd33b06e87c04e4f873a5afd9d53714bc5047f.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`whinlatter </openembedded-core/log/?h=whinlatter>`
-  Tag:  :oe_git:`yocto-5.3 </openembedded-core/log/?h=yocto-5.3>`
-  Git Revision: :oe_git:`8519978592483bb096ed5192fff7af6c887b799e </openembedded-core/commit/?id=8519978592483bb096ed5192fff7af6c887b799e>`
-  Release Artefact: oecore-8519978592483bb096ed5192fff7af6c887b799e
-  sha: 3b23d8a56a2f6e3872ec01272a76e3551ac1eab001f81c9abc680ce415aa888d
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3/oecore-8519978592483bb096ed5192fff7af6c887b799e.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3/oecore-8519978592483bb096ed5192fff7af6c887b799e.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`whinlatter </meta-yocto/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3 </meta-yocto/log/?h=yocto-5.3>`
-  Git Revision: :yocto_git:`d02d3faaf4d6075ea03e9eb47654ec7639f929a0 </meta-yocto/commit/?id=d02d3faaf4d6075ea03e9eb47654ec7639f929a0>`
-  Release Artefact: meta-yocto-d02d3faaf4d6075ea03e9eb47654ec7639f929a0
-  sha: e2a69bf93466cd010fd14e4f1b2bb5f45da165b00eb1fbf7987541d7a9378d47
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3/meta-yocto-d02d3faaf4d6075ea03e9eb47654ec7639f929a0.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3/meta-yocto-d02d3faaf4d6075ea03e9eb47654ec7639f929a0.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`whinlatter </meta-mingw/log/?h=whinlatter>`
-  Tag:  :yocto_git:`yocto-5.3 </meta-mingw/log/?h=yocto-5.3>`
-  Git Revision: :yocto_git:`	 </meta-mingw/commit/?id=00323de97e397d4f6734ef2191806616989f5e10>`
-  Release Artefact: meta-mingw-00323de97e397d4f6734ef2191806616989f5e10
-  sha: c9a70539b12c0642596fde6a2766d4a6a8fec8b2a366453fb6473363127a1c77
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3/meta-mingw-00323de97e397d4f6734ef2191806616989f5e10.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.16 </bitbake/log/?h=2.16>`
-  Tag:  :oe_git:`yocto-5.3 </bitbake/log/?h=yocto-5.3>`
-  Git Revision: :oe_git:`720df1a53452983c1c832f624490e255cf389204 </bitbake/commit/?id=720df1a53452983c1c832f624490e255cf389204>`
-  Release Artefact: bitbake-720df1a53452983c1c832f624490e255cf389204
-  sha: 08a5d8914b59e904a805fbf0e76058dfeddd49f4a022298a3c1485b98233db24
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-5.3/bitbake-720df1a53452983c1c832f624490e255cf389204.tar.bz2

   https://mirrors.kernel.org/yocto/yocto/yocto-5.3/bitbake-720df1a53452983c1c832f624490e255cf389204.tar.bz2


