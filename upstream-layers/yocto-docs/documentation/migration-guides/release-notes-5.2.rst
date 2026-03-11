.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: walnascar
.. |yocto-ver| replace:: 5.2

Release notes for |yocto-ver| (|yocto-codename|)
------------------------------------------------

New Features / Enhancements in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Linux kernel 6.12, gcc 14.2, glibc 2.41, LLVM 19.1.7, and over 300 other
   recipe upgrades.

-  Minimum Python version required on the host: 3.9.

-  New variables:

   -  ``linux-firmware``: Add the :term:`FIRMWARE_COMPRESSION` variable which
      allows compression the firmwares provided by the ``linux-firmware`` recipe.
      Possible values are ``xz`` and ``zst``.

   -  Reproducibility: Add the :term:`OEQA_REPRODUCIBLE_TEST_LEAF_TARGETS`
      variable which enables a reproducibility test on recipes using
      :ref:`Shared State <overview-manual/concepts:Shared State>` for the
      dependencies. See :doc:`/test-manual/reproducible-builds`.

   -  ``systemd``: Add :term:`WATCHDOG_RUNTIME_SEC`: for controlling the
      ``RuntimeWatchdogSec`` option in ``/etc/systemd/system.conf``.

   -  :term:`FIT_UBOOT_ENV` to allow including a u-boot script as a text in a
      fit image. See the ``kernel-fitimage`` for more information.

   -  :ref:`ref-classes-meson`: :term:`MESON_INSTALL_TAGS` to allow passing
      install tags (``--tags``) to the ``meson install`` command during the
      :ref:`ref-tasks-install` task.

   -  :ref:`ref-classes-cve-check`: :term:`NVD_DB_VERSION` to allow choosing the
      CVE feed when using the :ref:`ref-classes-cve-check` class.

   -  The :term:`BB_USE_HOME_NPMRC` controls whether or not BitBake uses the
      user's ``.npmrc`` file within their home directory within the npm fetcher.
      This can be used for authentication of private NPM registries, among other
      uses.

   -  The :term:`GRUB_MKIMAGE_OPTS` can be used to control the flags to the
      ``grub-mkimage`` command in the context of the GRUB recipe (``grub-efi``).

   -  The :term:`SPDX_PACKAGE_VERSION` variable controls the package version as
      seen in the SPDX 3.0 JSON output (``software_packageVersion``).

-  Kernel-related changes:

   -  :ref:`ref-classes-cml1`: in :ref:`ref-tasks-diffconfig`, do not override
      ``.config`` with ``.config.orig``. This applies to other recipes using the
      class :ref:`ref-classes-cml1`.

   -  ``linux-firmware``: add following new firmware packages:

       -  ``qcom-qcm6490-audio``
       -  ``qcom-qcm6490-compute``
       -  ``qcom-adreno-a663``
       -  ``qcom-qcm6490-adreno``
       -  ``qcom-sa8775p-adreno``
       -  ``qcom-qcm6490-ipa``
       -  ``qcom-x1e80100-audio``
       -  ``qcom-qcs615-adreno``
       -  ``qcom-aic100``
       -  ``qcom-qdu100``
       -  ``qca-qca2066``
       -  ``qca-qca61x4-serial``
       -  ``qca-qca61x4-usb``
       -  ``qca-qca6390``
       -  ``qca-qca6698``
       -  ``qca-wcn3950``
       -  ``qca-wcn3988``
       -  ``qca-wcn399x``
       -  ``qca-wcn6750``
       -  ``qca-wcn7850``
       -  ``qcom-2-license``
       -  ``qcom-aic100``
       -  ``qcom-qcm6490-wifi``
       -  ``qcom-qdu100``
       -  ``qcom-sa8775p-audio``
       -  ``qcom-sa8775p-compute``
       -  ``qcom-sa8775p-generalpurpose``
       -  ``qcom-x1e80100-lenovo-t14s-g6-adreno``
       -  ``qcom-x1e80100-lenovo-t14s-g6-audio``
       -  ``qcom-x1e80100-lenovo-t14s-g6-compute``
       -  ``qcom-adreno-a623``
       -  ``qcom-qcs8300-adreno``
       -  ``qca-qca2066``
       -  ``qcom-adreno-a2xx``

   -  ``linux-firmware``: split ``amgpu``, ``ath10k``, ``ath11k`` and ``ath12k``
      in separate packages.

   -  The :ref:`ref-classes-kernel-yocto` classes now supports in-tree
      configuration fragments. These can be added with the
      :term:`KERNEL_FEATURES` variable.

   -  Kernel configuration audit can now be disabled by setting
      :term:`KMETA_AUDIT` to 1.

   -  The ``kern-tools`` recipe is now able to recognize files ending with
      ``.config`` for :ref:`ref-classes-kernel-yocto`-based Kernel recipes.

   -  Support the LZMA compression algorithm in the
      :ref:`ref-classes-kernel-uboot` class. This can be done by setting the
      variable :term:`FIT_KERNEL_COMP_ALG` to ``lzma``.

   -  :ref:`ref-classes-kernel-yocto`: Reproducibility for commits created by
      the :ref:`ref-classes-kernel-yocto` class was improved.

   -  ``kernel-arch``: add ``-fmacro-prefix-map`` in ``KERNEL_CC`` to fix a
      reproducibility issue.

-  New core recipes:

   -  ``python3-pefile``: required for the :ref:`ref-classes-uki` class.

   -  Add initial support for the `Barebox <https://www.barebox.org>`__
      bootloader, along with associated OEQA test cases. This adds the
      ``barebox`` and the ``barebox-tools`` recipes.

   -  Import ``makedumpfile`` from meta-openembedded, as the ``kexec-tools``
      recipe :term:`RDEPENDS` on it.

   -  The ``tcl-8`` recipe was added back to support the build of ``expect``.

   -  Add the ``libdisplay-info`` recipe, an EDID and DisplayID library,
      required for Weston 14.0.1 and newer.

   -  The ``hwdata`` recipe was imported from :oe_git:`meta-openembedded
      </meta-openembedded>`, a recipe for hardware identification and
      configuration data, needed by ``libdisplay-info``.

   -  The ``cve-update-db-native`` was restored from kirkstone and can be used
      to update the CVE National Vulnerability Database (NVD). Add support for
      the FKIE-CAD (https://github.com/fkie-cad/nvd-json-data-feeds) CVE source
      for it.

   -  The ``rpm-sequoia-crypto-policy`` to ship a crypto policy file for the
      ``rpm-sequoia`` recipe.

   -  The ``libsass`` and ``sassc`` for the C/C++ port of the Sass CSS
      pre-compiler, required by the ``libadwaita`` recipe.

   -  ``python3-roman-numerals-py``: module providing utilities for working with
      well-formed Roman numerals. ``python3-sphinx`` relies on this recipe.

   -  The ``fastfloat`` recipe, a header-only library for fast number parsing.
      This will be a dependency for the ``vte`` recipe in later versions.

   -  The ``avahi-libnss-mdns`` was renamed from ``libnss-mdns``.

   -  The ``cargo-c`` was renamed from ``cargo-c-native``.

   -  The ``tcl8`` recipe was added to support the failing build of ``expect``.
      The ``tcl`` recipe (version 9) remains the main recipe for this component.

   -  The ``scdoc`` recipe is imported from
      :oe_layerindex:`/layerindex/branch/master/layer/meta-wayland` to support
      the generation of the man-pages of ``kdoc``.

-  New core classes:

   -  New :ref:`ref-classes-uki` class for building Unified Kernel Images (UKI).
      Associated OEQA tests were also added for this class.

   -  New :ref:`ref-classes-cython` class for python recipes that require Cython
      for their compilation. Existing recipes depending on Cython now inherit
      this class. This class also strips potential build paths in the compilation
      output for reproducibility.

   -  New :ref:`ref-classes-ptest-python-pytest` class to automatically
      configure :ref:`ref-classes-ptest` for Python packages using the `pytest
      <https://docs.pytest.org>`__ unit test framework.

-  Architecture-specific changes:

   -  ``tune-cortexa32``: set tune feature to ``armv8a``.

   -  Add the ``loongarch64`` architecture for the ``grub2`` and ``llvm``
      recipes. It was also added to build with ``musl`` as the toolchain.

-  QEMU / ``runqemu`` changes:

   -  ``qemu/machine``: change the  ``QEMU_EXTRAOPTIONS_${TUNE_PKGARCH}`` syntax
      in QEMU machine definitions to ``QEMU_EXTRAOPTIONS:tune-${TUNE_PKGARCH}``
      to follow the same patterns as other QEMU-related variables.

-  Documentation changes:

   -  Use ``rsvg`` as a replacement of ``inkscape`` to convert svg files in the
      documentation.

   -  The ``cve`` role was replaced by ``cve_nist`` to avoid a conflict with
      more recent version of Sphinx.

   -  New documentation on the multiconfig feature: :doc:`/dev-manual/multiconfig`.

   -  New documentation on ``bblock``: :doc:`/dev-manual/bblock`.

-  Go changes:

   -  The :ref:`ref-classes-go-mod` class now sets an internal variable
      ``GO_MOD_CACHE_DIR`` to enable the use of the Go module fetchers for
      downloading and unpacking module dependencies to the module cache.

   -  Make the :ref:`ref-tasks-compile` task run before
      :ref:`ref-tasks-populate_lic` in the :ref:`ref-classes-go-mod` class so
      license files are found by :ref:`ref-tasks-populate_lic` after the ``go
      install`` command is run in :ref:`ref-tasks-compile`.

-  Rust changes:

   -  ``rust-target-config``: Update the data layout for the *x86-64* target, as
      it was different in Rust from LLVM, which produced a data layout error.

   -  The :term:`PACKAGECONFIG_CONFARGS` value if now passed to the ``cargo
      build`` command, which means that Rust recipes can now properly define
      their :term:`PACKAGECONFIG` configuration.

-  Wic Image Creator changes:

   -  Allow the ``--exclude-path`` option to exclude symlinks.

   -  Add the variable :term:`WIC_SECTOR_SIZE` to control the sector size of Wic
      images.

   -  ``bootimg-efi``: Support "+" symbol in filenames passed in
      :term:`IMAGE_EFI_BOOT_FILES`.

-  SDK-related changes:

   -  Add support for ZST-compression through :term:`SDK_ARCHIVE_TYPE`, by
      setting its value to ``tar.zst``.

   -  The ``debug-tweaks`` features were removed from ``-sdk`` images
      (``core-image-*-sdk.bb``).

   -  Enable ``ipv6``, ``acl``, and ``xattr`` in :term:`DISTRO_FEATURES_NATIVESDK`.

   -  Toolchain SDKs (``meta-toolchain``) now properly supports the ``usrmerge``
      feature (part of :term:`DISTRO_FEATURES`).

   -  The ``pipefail`` shell option is now added to the SDK installer script.

-  Testing-related changes:

   -  ``oeqa/postactions``: Fix archive retrieval from target.

   -  ``oeqa/selftest/gcc``: Fix kex exchange identification error.

   -  ``oeqa/utils/qemurunner``: support ignoring vt100 escape sequences.

   -  ``oeqa``: support passing custom boot patterns to runqemu.

   -  ``oeqa/selftest/cases``: add basic U-boot and Barebox tests.

   -  ``oeqa/selftest/rust``: skip on all MIPS platforms.

   -  Lots of changes and improvements to the :term:`Toaster` OEQA tests.

   -  ``oeqa/selftest``: add a test for bitbake "-e" and "-getvar" difference.

   -  ``oeqa/selftest``: Fix failure when configuration contains ``BBLAYERS:append``

   -  ``oeqa/ssh``: improve performance and log sizes when handling large files.

   -  ``oeqa/poisoning``: fix and improve gcc include poisoning tests.

-  Utility script changes:

   -  The ``patchreview.py`` script now uses the ``check_upstream_status`` from
      ``oe.qa`` to get patch statuses.

   -  ``resulttool``:

      -  Allow store to filter to specific revisions (``--revision`` flag).

      -  Use single space indentation in JSON output, to save disk
         space.

      -  Add ``--logfile-archive`` option to store and archive log files
         separately.

      -  Handle LTP raw logs as well as Ptest.

   -  ``yocto-check-layer``:

      -  Check for the presence of a ``SECURITY.md`` file in layers and make it
         mandatory.

      -  The :ref:`ref-classes-yocto-check-layer` class now uses
         :term:`CHECKLAYER_REQUIRED_TESTS` to get the list of QA checks to verify
         when running the ``yocto-check-layer`` script.

   -  New ``oe-image-files-spdx`` script utility directory under
      ``scripts/contrib`` to that processes the SPDX 3.0.1 output from a build
      and lists all the files on the root file system with their checksums.

   -  ``install-buildtools``:

      -  Add the ``--downloads-directory`` argument to the script to allow
         specifying the location of the artifact download directory.

      -  The download URL are now stored next to the download artifacts for
         traceability.

   -  New ``clean-hashserver-database`` under ``scripts/`` that can be used to
      clean the hashserver database based on the files available in the sstate
      directory (see :ref:`overview-manual/concepts:Hash Equivalence` for more
      information).

-  BitBake changes:

   -  Add a new concept of configuration fragment, which allows providing
      configuration snippets contained in layers in a structured and controlled
      way. For more information, see the
      :ref:`bitbake:bitbake-user-manual/bitbake-user-manual-metadata:\`\`addfragments\`\`
      Directive` section of the BitBake User Manual.

   -  Add a new ``include_all`` directive, which can be used to include multiple
      files present in the same location in different layers.

   -  Fetcher related changes (``fetch2``):

      -  Do not preserve ownership when unpacking.

      -  switch from Sqlite ``persist_data`` to a standard cache file
         for checksums, and drop ``persist_data``.

      -  add support for GitHub codespaces by adding the
         ``GITHUB_TOKEN`` to the list of variables exported during ``git``
         invocations.

      -  set User-Agent to 'bitbake/version' instead of a "fake
         mozilla" user agent.

      -  ``wget``: handle HTTP 308 Permanent Redirect.

      -  ``wget``: increase timeout to 100s from 30s to match CDN worst
         response time.

      -  Add support for fast initial shallow fetch. The fetcher will prefer an
         initial shallow clone, but will re-utilize an existing bare clone if
         there is one. If the remote server does not allow shallow fetches, the
         fetcher falls back to a bare clone. This improves the data transfer
         size on the initial fetch of a repository, eliminates the need to use
         an HTTPS tarball :term:`SRC_URI` to reduce data transfer, and allows
         SSH-based authentication when using non-public repos, so additional
         HTTPS tokens may not be required.

   -  ``compress``: use ``lz4`` instead of ``lz4c``, as ``lz4c`` as been
      considered deprecated since 2018.

   -  ``server/process``: decrease idle/main loop frequency, as it is idle and
      main loops have socket select calls to know when to execute.

   -  ``bitbake-worker``:

      -  improve bytearray truncation performance when large
         amounts of data are being transferred from the cooker to the worker.

      -  ``cooker``: increase the default pipe size from 64KB to
         512KB for better efficiency when transferring large amounts of data.

   -  ``bitbake-getvar``: catch ``NoProvider`` exception to improve error
      readability when a recipe is not found with ``--recipe``.

   -  ``bb/build``: add a function ``bb.build.listtasks()`` to list the tasks in
      a datastore.

   -  Remove custom exception backtrace formatting, and replace occurrences of
      ``bb.exception.format_exception()`` by ``traceback.format_exception()``.

   -  ``runqueue``: various performance optimizations including:

      -  Fix performance of multiconfigs with large overlap.
      -  Optimise ``setscene`` loop processing by starting where it
         was left off in the previous execution.

   -  ``knotty`` now hints the user if :term:`MACHINE` was not set in
      the ``local.conf`` file.

   -  ``utils``: add Go mod h1 checksum support, specific to Go modules. Use
      with ``goh1``.

   -  The parser now catches empty variable name assignments such as::

         += "value"

      The previous code would have assigned ``value`` to the variable named ``+``.

   -  ``hashserv``: Add the ``gc-mark-stream`` command for batch hash marking.


-  Packaging changes:

   -  ``systemd``: extract dependencies from ``.note.dlopen`` ELF segments, to
      better detect dynamically linked libraries at runtime.

   -  ``package_rpm``: use ZSTD's default compression level from the variable
      :term:`ZSTD_COMPRESSION_LEVEL`.

   -  ``package_rpm``: restrict RPM packaging to 4 threads to improve
      the compression speed.

   -  ``sign_rpm``: ``rpm`` needs the ``sequoia`` :term:`PACKAGECONFIG`
      config set to be able to generate signed packages.

-  LLVM related changes:

   -  Set ``LLVM_HOST_TRIPLE`` for cross-compilation, which is recommended when
      cross-compiling Llvm.

-  SPDX-related changes:

   -  SPDX 3.0:

      -  Find local sources when searching for debug sources.

      -  Map ``gitsm`` URIs to ``git``.

      -  Link license and build by alias instead of SPDX ID.

   -  Fix SPDX tasks not running when code changes (use of ``file-checksums``).

-  ``devtool`` changes:

   -  Remove the "S = WORKDIR" workaround as now :term:`S` cannot be equal to
      :term:`WORKDIR`.

   -  The already broken ``--debug-build-config`` option of
      ``devtool ide-sdk`` has been replaced by a new ``--debug-build`` option
      of ``devtool modify``. The new ``devtool ide-sdk`` workflow is:
      ``devtool modify my-recipe --debug-build`` followed by
      ``devtool ide-sdk my-recipe my-image``.

   -  ``create-spdx``: support line numbers for :term:`NO_GENERIC_LICENSE`
      license types.

   -  ``spdx30``: Adds a "contains" relationship that relates the root file
      system package to the files contained in it. If a package provides a file
      with a matching hash and path, it will be linked, otherwise a new File
      element will be created.

   -  The output of :ref:`devtool upgrade-status
      <ref-manual/devtool-reference:Checking on the Upgrade Status of a Recipe>`
      is now sorted by recipe name.

-  Patchtest-related changes:

   -  Refactor pattern definitions in a ``patterns`` module.

   -  Refactor and improve the ``mbox`` module.

   -  Split out result messages.

   -  Add a check for user name tags in patches (for example "fix added by
      @username").

-  :ref:`ref-classes-insane` class related changes:

   -  Only parse ELF if they are files and not symlinks.

   -  Check for ``RUNPATH`` in addition to ``RPATH`` in binaries.

   -  Ensure :ref:`ref-classes-insane` tasks of dependencies run in builds when
      expected.

-  Security changes:

   -  The ``PIE`` gcc flag is now passed for the *powerpc* architecture after a
      bugfix in gcc (https://gcc.gnu.org/bugzilla/show_bug.cgi?id=81170).

   -  ``openssh``: be more restrictive on private key file permissions by
      setting them from the :ref:`ref-tasks-install` task.

-  :ref:`ref-classes-cve-check` changes:

   -  Update the :term:`DL_DIR` database location name
      (``${DL_DIR}/CVE_CHECK2``).

   -  Add the field "modified" to the JSON report (from "NVD-modified").

   -  Add support for CVSS v4.0.

   -  Fix malformed cve status description with ``:`` characters.

   -  Restore the :term:`CVE_CHECK_SHOW_WARNINGS` variable and functionality. It
      currently prints warning message for every unpatched CVE the
      :ref:`ref-classes-cve-check` class finds.

   -  Users can control the NVD database source using the :term:`NVD_DB_VERSION`
      variable with possible values ``NVD1``, ``NVD2``, or ``FKIE``.

   -  The default feed for CVEs is now ``FKIE`` instead of ``NVD2`` (see
      :term:`NVD_DB_VERSION` for more information).

-  New :term:`PACKAGECONFIG` options for individual recipes:

   -  ``perf``: ``zstd``
   -  ``ppp``: ``pam``, ``openssl``
   -  ``libpciaccess``: ``zlib``
   -  ``gdk-pixbuf``: ``gif``, ``others``
   -  ``libpam``: ``selinux``
   -  ``libsecret``: ``pam``
   -  ``rpm``: ``sequoia``
   -  ``systemd``: ``apparmor``, ``fido``, ``mountfsd``, ``nsresourced``
   -  ``ovmf``: ``debug``
   -  ``webkitgtk``: ``assertions``
   -  ``iproute2``: ``iptables``
   -  ``man-db``: ``col``

-  Systemd related changes:

   -  ``systemd``:

      -  set better sane time at startup by creating the ``clock-epoch`` file in
         ``${libdir}`` if the ``set-time-epoch`` :term:`PACKAGECONFIG` config is
         set.

      -  really disable `Predictable Network Interface names
         <https://www.freedesktop.org/wiki/Software/systemd/PredictableNetworkInterfaceNames/>`__
         if the ``pni-names`` feature is not part of :term:`DISTRO_FEATURES`.
         Previously it was only really disabled for QEMU machines.

      -  split ``networkd`` into its own package named ``systemd-networkd``.

   -  ``systemd-bootchart``: now supports the 32-bit *riscv* architecture.

   -  ``systemd-boot``: now supports the *riscv* architecture.

   -  ``systemd-serialgetty``:

      -  the recipe no longer sets a default value for
         :term:`SERIAL_CONSOLES`, and uses the one set in ``bitbake.conf``.

      -  the recipe no longer ships a copy of the ``serial-getty@.service`` as
         it is provided by systemd directly.

      -  Don't set a default :term:`SERIAL_CONSOLES` value in the
         ``systemd-serialgetty`` recipe and take the global value that should
         already be set.

      -  Replace custom unit files by existing unit files provided in the
         systemd source code.

   -  User unit supports was improved. All the user units are now enabled by
      default.

   -  The custom implementation of ``systemctl`` in :term:`OpenEmbedded-Core
      (OE-Core)` was removed to use the upstream one. This ``systemctl`` binary
      is now compiled and used for systemd-related operations.

-  :ref:`ref-classes-sanity` class changes:

   -  Add a sanity check to validate that the C++ toolchain is functional on the
      host.

   -  Add a sanity check to check that the C++ compiler on the host supports
      C++20.

   -  Add a sanity check to verify that :term:`TOPDIR` does not contain
      non-ASCII characters, as it may lead to unexpected build errors.

-  Miscellaneous changes:

   -  ``bluez``: fix mesh build when building with musl.

   -  ``python3-pip``: the ``pip`` executable is now left and not deleted, and
      can be used instead of ``pip3`` and ``pip2``.

   -  ``tar`` image types are now more reproducible as the :term:`IMAGE_CMD` for
      ``tar`` now strips ``atime`` and ``ctime`` from the archive content.

   -  :term:`SOLIBSDEV` and :term:`SOLIBS` are now defined for the *mingw32*
      architecture (``.dll``).

   -  :ref:`rootfs-postcommands <ref-classes-rootfs*>`: make ``opkg`` status
      reproducible.

   -  The default :term:`KERNEL_CONSOLE` value is no longer ``ttyS0`` but the
      first entry from the :term:`SERIAL_CONSOLES` variable.

   -  ``virglrenderer``: add a patch to fix ``-int-conversion`` build issue.

   -  ``ffmpeg``: disable asm optimizations for the *x86* architecture as PIC is
      required and *x86* ASM code is not PIC.

   -  ``udev-extraconf``: fix the ``network.sh`` script that did not configure
      hotplugged interfaces.

   -  ``classes-global/license``: move several functions and logic to library
      code in :oe_git:`meta/lib/oe/license.py </openembedded-core/tree/meta/lib/oe/license.py>`.

   -  The recipe ``cairo`` now disables the features ``symbol-lookup``,
      ``spectre``, and ``tests`` by default.

   -  The recipe ``glib-2.0`` now disables the feature ``sysprof`` by default.

   -  The recipe ``gstreamer1.0-libav`` now disables the feature ``doc`` by default.

   -  ``rxvt-unicode``: change ``virtual/x-terminal-emulator`` from
      :term:`PROVIDES` to :term:`RPROVIDES` as ``virtual-x-terminal-emulator``.
      Also make this recipe depend on the ``x11`` distro features with
      :term:`REQUIRED_DISTRO_FEATURES`.

   -  ``rxvt-unicode.inc``: disable the ``terminfo`` installation by setting
      ``TIC`` to ``:`` in :term:`EXTRA_OECONF`, to avoid host contamination.

   -  ``matchbox-terminal``: add ``x-terminal-emulator`` as :term:`RPROVIDES`
      and set :term:`ALTERNATIVE` for the recipe.

   -  ``default-providers.conf``: set ``rxvt-unicode`` as the default
      ``virtual-x-terminal-emulator`` runtime provider with
      :term:`PREFERRED_RPROVIDER`.

   -  ``cve-update-nvd2-native``: updating the database will now result in an
      error if :term:`BB_NO_NETWORK` is enabled and
      :term:`CVE_DB_UPDATE_INTERVAL` is not set to ``-1``. Users can control the
      NVD database source using the :term:`NVD_DB_VERSION` variable with
      possible values ``NVD1``, ``NVD2``, or ``FKIE``.

   -  ``systemtap``: add ``--with-extra-version="oe"`` configure option to
      improve the reproducibility of the recipe.

   -  ``python3``: package ``tkinter``'s shared objects separately in the
      ``python3-tkinter`` package.

   -  ``init-manager``: set the variable ``VIRTUAL-RUNTIME_dev_manager`` to
      ``udev`` by default in
      :oe_git:`meta/conf/distro/include/init-manager-none.inc
      </openembedded-core/tree/meta/conf/distro/include/init-manager-none.inc>`
      and :oe_git:`meta/conf/distro/include/init-manager-sysvinit.inc
      </openembedded-core/tree/meta/conf/distro/include/init-manager-sysvinit.inc>`,
      instead of :oe_git:`meta/recipes-core/packagegroups/packagegroup-core-boot.bb
      </openembedded-core/tree/meta/recipes-core/packagegroups/packagegroup-core-boot.bb>`
      only.

      Likewise, the same is done for ``VIRTUAL-RUNTIME_keymaps`` with
      ``keymaps`` as its default value.

   -  ``seatd``: Create a ``seat`` group and package the systemd service
      ``seatd.service`` with correct permissions.

      That way, the ``weston`` user in ``weston-init.bb`` was added to the
      ``seat`` group to be able to properly establish connection between the
      Weston and the ``seatd`` socket.

   -  ``webkitgtk``:

      -  Fix build on 32bit arches with 64bit ``time_t`` only.

      -  Disable JIT on RISCV64.

   -  :ref:`ref-classes-report-error`: Add :term:`PN` to error report files.

   -  ``initrdscripts``: add UBI support for mounting a live ``ubifs`` rootfs.

   -  ``uboot-extlinux-config.bbclass``: add support for device tree overlays.

   -  ``glibc``: add ``ld.so.conf`` to :term:`CONFFILES`.

   -  ``udev-extraconf``: Allow FAT mount group to be specified with
      :term:`MOUNT_GROUP`.

   -  New ``bbverbnote`` log utility which can be used to print on the console
      (equivalent to the ``bb.verbnote`` Python implementation).

   -  :ref:`ref-classes-grub-efi`: Add :term:`GRUB_TITLE` variable to set
      custom GRUB titles.

   -  ``gawk``: Enable high precision arithmetic support by default (``mpfr``
      enabled by default in :term:`PACKAGECONFIG`).

   -  ``licenses``: Map the license ``SGIv1`` to ``SGI-OpenGL``, as ``SGIv1`` is
      not an SPDX license identifier.

   -  Configuration files for the `b4 <https://b4.docs.kernel.org>`__
      command-line tool was added to the different Yocto Project and OpenEmbedded
      repositories.

   -  ``kernel-fitimage``: handle :doc:`multiconfig
      </dev-manual/multiconfig>` dependency when
      :term:`INITRAMFS_MULTICONFIG` is set.

   -  ``psplash``: when using the ``systemd`` feature from
      :term:`DISTRO_FEATURES`, start the ``psplash`` service when the
      ``/dev/fb0`` framebuffer is detected with Udev.

   -  ``gdb``: is now compiled with xz support by default (``--with-lzma``).

   -  ``busybox``: drop net-tools from the default ``defconfig``, since these tools
      (``ifconfig``, etc.) have been deprecated since `2009
      <https://lists.debian.org/debian-devel/2009/03/msg00780.html>`__.

   -  ``perf`` is built with ``zstd`` in :term:`PACKAGECONFIG` by default.

   -  ``boost``: add ``charconv`` to built libraries by default.

   -  ``mirrors``: rationalise Debian mirrors to point at the canonical server
      (deb.debian.org) instead of country specific ones. This server is backed
      by a :wikipedia:`CDN <Content_delivery_network>` to properly balance the
      server load.

   -  ``lib: sbom30``: Add action statement for affected VEX statements with
      "Mitigation action unknown", as these are not tracked by the existing
      code.

Known Issues in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  The :ref:`ref-classes-cve-check` class is based on the `National
   Vulnerability Database <https://nvd.nist.gov/>`__ (NVD). Since the beginning
   of 2024, the maintainers of this database have stopped annotating CVEs with
   the affected CPEs. This prevents the :ref:`ref-classes-cve-check` class to
   properly report CVEs as CPEs are used to match Yocto recipes with CVEs
   affecting them. As a result, the current CVE reports may look good but the
   reality is that some vulnerabilities are just not reported.

   During that time, users may look up the 'CVE database
   <https://www.cve.org/>'__ for entries concerning software they use, or follow
   release notes of such projects closely.

   Please note, that the :ref:`ref-classes-cve-check` tool has always been a
   helper tool, and users are advised to always review the final result. Results
   of an automatic scan may not take into account configuration options,
   compiler options and other factors.

Recipe License changes in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following changes have been made to the :term:`LICENSE` values set by recipes:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous value
     - New value
   * - ``babeltrace2``
     - ``MIT & GPL-2.0-only & LGPL-2.1-only & BSD-2-Clause``
     - ``MIT & GPL-2.0-only & LGPL-2.1-only & BSD-2-Clause & BSD-4-Clause & GPL-3.0-or-later & CC-BY-SA-4.0 & PSF-2.0``
   * - ``busybox``
     - ``GPL-2.0-only & bzip2-1.0.4``
     - ``GPL-2.0-only & bzip2-1.0.6``
   * - ``dbus-glib``
     - ``AFL-2.1 | GPL-2.0-or-later``
     - ``(AFL-2.1 & LGPL-2.0-or-later & MIT) | (GPL-2.0-or-later & LGPL-2.0-or-later & MIT)``
   * - ``diffstat``
     - ``MIT``
     - ``X11``
   * - ``docbook-xsl-stylesheets``
     - ``XSL``
     - ``DocBook-XML``
   * - ``font-util``
     - ``Unicode-TOU & BSD-4-Clause & BSD-2-Clause``
     - ``Unicode-TOU & MIT & X11 & BSD-2-Clause``
   * - ``json-glib``
     - ``LGPL-2.1-only``
     - ``LGPL-2.1-or-later``
   * - ``libbsd``
     - ``BSD-3-Clause & BSD-4-Clause & ISC & PD``
     - ``BSD-3-Clause & ISC & PD``
   * - ``libxfont2``
     - ``MIT & MIT & BSD-4-Clause & BSD-2-Clause``
     - ``MIT & MIT & BSD-4-Clause-UC & BSD-2-Clause``
   * - ``libxkbcommon``
     - ``MIT & MIT``
     - ``MIT & MIT-open-group & HPND & HPND-sell-variant & X11``
   * - ``man-pages``
     - ``GPL-2.0-or-later & GPL-2.0-only & GPL-1.0-or-later & BSD-2-Clause & BSD-3-Clause & BSD-4-Clause & MIT``
     - ``GPL-2.0-or-later & GPL-2.0-only & GPL-1.0-or-later & BSD-2-Clause & BSD-3-Clause & BSD-4-Clause-UC & MIT``
   * - ``ppp``
     - ``BSD-3-Clause & BSD-3-Clause-Attribution & GPL-2.0-or-later & LGPL-2.0-or-later & PD & RSA-MD & MIT``
     - ``BSD-2-Clause & GPL-2.0-or-later & LGPL-2.0-or-later & PD & RSA-MD & MIT``
   * - ``tcf-agent``
     - ``EPL-1.0 | EDL-1.0``
     - ``EPL-1.0 | BSD-3-Clause``
   * - ``unfs3``
     - ``unfs3``
     - ``BSD-3-Clause``
   * - ``usbutils``
     - ``GPL-2.0-or-later & (GPL-2.0-only | GPL-3.0-only)``
     - ``GPL-2.0-or-later & (GPL-2.0-only | GPL-3.0-only) & CC0-1.0 & LGPL-2.1-or-later & MIT``
   * - ``util-linux``
     - ``GPL-1.0-or-later & GPL-2.0-only & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-2-Clause & BSD-3-Clause & BSD-4-Clause & MIT``
     - ``GPL-1.0-or-later & GPL-2.0-only & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-2-Clause & BSD-3-Clause & BSD-4-Clause-UC & MIT``

Security Fixes in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following CVEs have been fixed:

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Recipe
     - CVE IDs
   * - ``barebox``
     - :cve_nist:`2025-26721`, :cve_nist:`2025-26722`, :cve_nist:`2025-26723`, :cve_nist:`2025-26724`, :cve_nist:`2025-26725`
   * - ``binutils``
     - :cve_nist:`2024-53589`, :cve_nist:`2025-1153`
   * - ``curl``
     - :cve_nist:`2024-8096`, :cve_nist:`2024-9681`, :cve_nist:`2024-11053`, :cve_nist:`2025-0167`, :cve_nist:`2025-0665`, :cve_nist:`2025-0725`
   * - ``expat``
     - :cve_nist:`2024-8176`, :cve_nist:`2024-50602`
   * - ``ghostscript``
     - :cve_nist:`2024-46951`, :cve_nist:`2024-46952`, :cve_nist:`2024-46953`, :cve_nist:`2024-46954`, :cve_nist:`2024-46955`, :cve_nist:`2024-46956`
   * - ``gnutls``
     - :cve_nist:`2024-12243`
   * - ``go``
     - :cve_nist:`2024-34155`, :cve_nist:`2024-34156`, :cve_nist:`2024-34158`, :cve_nist:`2024-45336`, :cve_nist:`2024-45341`, :cve_nist:`2025-22866`, :cve_nist:`2025-22870`
   * - ``grub``
     - :cve_nist:`2024-45774`, :cve_nist:`2024-45775`, :cve_nist:`2024-45776`, :cve_nist:`2024-45777`, :cve_nist:`2024-45778`, :cve_nist:`2024-45779`, :cve_nist:`2024-45780`, :cve_nist:`2024-45781`, :cve_nist:`2024-45782`, :cve_nist:`2024-45783`, :cve_nist:`2024-56737`, :cve_nist:`2025-0622`, :cve_nist:`2025-0624`, :cve_nist:`2025-0677`, :cve_nist:`2025-0678`, :cve_nist:`2025-0684`, :cve_nist:`2025-0685`, :cve_nist:`2025-0686`, :cve_nist:`2025-0689`, :cve_nist:`2025-0690`, :cve_nist:`2025-1118`, :cve_nist:`2025-1125`
   * -  ``gstreamer1.0``
     - :cve_nist:`2024-47606`
   * -  ``gstreamer1.0-plugins-base``
     - :cve_nist:`2024-47538`, :cve_nist:`2024-47541`, :cve_nist:`2024-47542`,  :cve_nist:`2024-47600`, :cve_nist:`2024-47607`, :cve_nist:`2024-47615`, :cve_nist:`2024-47835`
   * -  ``gstreamer1.0-plugins-good``
     - :cve_nist:`2024-47537`, :cve_nist:`2024-47539`, :cve_nist:`2024-47540`, :cve_nist:`2024-47543`, :cve_nist:`2024-47544`, :cve_nist:`2024-47545`, :cve_nist:`2024-47546`, :cve_nist:`2024-47596`, :cve_nist:`2024-47597`, :cve_nist:`2024-47598`, :cve_nist:`2024-47599`, :cve_nist:`2024-47601`, :cve_nist:`2024-47602`, :cve_nist:`2024-47603`, :cve_nist:`2024-47606`, :cve_nist:`2024-47613`, :cve_nist:`2024-47774`, :cve_nist:`2024-47775`, :cve_nist:`2024-47776`, :cve_nist:`2024-47777`, :cve_nist:`2024-47778`, :cve_nist:`2024-47834`
   * - ``libarchive``
     - :cve_nist:`2024-57970`, :cve_nist:`2025-1632`, :cve_nist:`2025-25724`
   * - ``libcap``
     - :cve_nist:`2025-1390`
   * - ``libsndfile1``
     - :cve_nist:`2024-50612`
   * - ``libtasn1``
     - :cve_nist:`2024-12133`
   * - ``libxml2``
     - :cve_nist:`2024-56171`, :cve_nist:`2025-24928`
   * - ``ofono``
     - :cve_nist:`2024-7539`, :cve_nist:`2024-7540`, :cve_nist:`2024-7541`, :cve_nist:`2024-7542`
   * - ``omvf``
     - :cve_nist:`2023-45236`, :cve_nist:`2023-45237`, :cve_nist:`2024-25742`
   * - ``openssh``
     - :cve_nist:`2025-26465`, :cve_nist:`2025-26466`
   * - ``openssl``
     - :cve_nist:`2024-9143`, :cve_nist:`2024-12797`, :cve_nist:`2024-13176`
   * - ``orc``
     - :cve_nist:`2024-40897`
   * - ``python3``
     - :cve_nist:`2025-0938`, :cve_nist:`2024-12254`
   * - ``qemu``
     - :cve_nist:`2024-6505`
   * - ``rsync``
     - :cve_nist:`2024-12084`, :cve_nist:`2024-12085`, :cve_nist:`2024-12086`, :cve_nist:`2024-12087`, :cve_nist:`2024-12088`, :cve_nist:`2024-12747`
   * - ``ruby``
     - :cve_nist:`2024-41123`, :cve_nist:`2024-41946`
   * - ``rust``
     - :cve_nist:`2024-43402`
   * - ``socat``
     - :cve_nist:`2024-54661`
   * - ``tiff``
     - :cve_nist:`2023-6277`, :cve_nist:`2023-6228`, :cve_nist:`2023-52356`
   * - ``vim``
     - :cve_nist:`2024-45306`, :cve_nist:`2024-47814`, :cve_nist:`2025-1215`, :cve_nist:`2025-22134`, :cve_nist:`2025-24014`, :cve_nist:`2025-26603`, :cve_nist:`2025-27423`, :cve_nist:`2025-29768`
   * - ``webkitgtk``
     - :cve_nist:`2025-24143`, :cve_nist:`2025-24150`, :cve_nist:`2025-24158`, :cve_nist:`2025-24162`
   * - ``wpa-supplicant``
     - :cve_nist:`2024-5290`
   * - ``xserver-xorg``
     - :cve_nist:`2024-9632`, :cve_nist:`2025-26594`, :cve_nist:`2025-26595`, :cve_nist:`2025-26596`, :cve_nist:`2025-26597`, :cve_nist:`2025-26598`, :cve_nist:`2025-26599`, :cve_nist:`2025-26600`, :cve_nist:`2025-26601`
   * - ``xwayland``
     - :cve_nist:`2024-9632`, :cve_nist:`2025-26594`, :cve_nist:`2025-26595`, :cve_nist:`2025-26596`, :cve_nist:`2025-26597`, :cve_nist:`2025-26598`, :cve_nist:`2025-26599`, :cve_nist:`2025-26600`, :cve_nist:`2025-26601`

Recipe Upgrades in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous version
     - New version
   * - ``adwaita-icon-theme``
     - 46.2
     - 48.0
   * - ``alsa-lib``
     - 1.2.12
     - 1.2.13
   * - ``alsa-ucm-conf``
     - 1.2.12
     - 1.2.13
   * - ``alsa-utils``
     - 1.2.12
     - 1.2.13
   * - ``appstream``
     - 1.0.3
     - 1.0.4
   * - ``at-spi2-core``
     - 2.52.0
     - 2.56.0
   * - ``autoconf-archive``
     - 2023.02.20
     - 2024.10.16
   * - ``babeltrace2``
     - 2.0.6
     - 2.1.0
   * - ``base-passwd``
     - 3.6.4
     - 3.6.6
   * - ``bash``
     - 5.2.32
     - 5.2.37
   * - ``bash-completion``
     - 2.14.0
     - 2.16.0
   * - ``bc``
     - 1.07.1
     - 1.08.1
   * - ``bind``
     - 9.20.1
     - 9.20.6
   * - ``binutils``
     - 2.43.1
     - 2.44
   * - ``binutils-cross``
     - 2.43.1
     - 2.44
   * - ``binutils-cross-canadian``
     - 2.43.1
     - 2.44
   * - ``binutils-crosssdk``
     - 2.43.1
     - 2.44
   * - ``binutils-testsuite``
     - 2.43.1
     - 2.44
   * - ``bluez5``
     - 5.78
     - 5.79
   * - ``boost``
     - 1.86.0
     - 1.87.0
   * - ``boost-build-native``
     - 1.86.0
     - 1.87.0
   * - ``btrfs-tools``
     - 6.10.1
     - 6.13
   * - ``build-appliance-image``
     - 15.0.0 (6a5ba188b79e…)
     - 15.0.0 (2fe7f46e1779…)
   * - ``busybox``
     - 1.36.1
     - 1.37.0
   * - ``busybox-inittab``
     - 1.36.1
     - 1.37.0
   * - ``ca-certificates``
     - 20240203
     - 20241223
   * - ``cairo``
     - 1.18.2
     - 1.18.4
   * - ``cargo``
     - 1.79.0
     - 1.84.1
   * - ``ccache``
     - 4.10.2
     - 4.11
   * - ``chrpath``
     - 0.16
     - 0.18
   * - ``cmake``
     - 3.30.2
     - 3.31.6
   * - ``cmake-native``
     - 3.30.2
     - 3.31.6
   * - ``connman``
     - 1.42
     - 1.43
   * - ``coreutils``
     - 9.5
     - 9.6
   * - ``cracklib``
     - 2.10.2
     - 2.10.3
   * - ``createrepo-c``
     - 1.1.4
     - 1.2.0
   * - ``cross-localedef-native``
     - 2.40+git
     - 2.41+git
   * - ``cups``
     - 2.4.10
     - 2.4.11
   * - ``curl``
     - 8.9.1
     - 8.12.1
   * - ``dbus``
     - 1.14.10
     - 1.16.2
   * - ``dbus-glib``
     - 0.112
     - 0.114
   * - ``debianutils``
     - 5.20
     - 5.21
   * - ``debugedit``
     - 5.0
     - 5.1
   * - ``desktop-file-utils``
     - 0.27
     - 0.28
   * - ``dhcpcd``
     - 10.0.10
     - 10.2.2
   * - ``diffoscope``
     - 277
     - 289
   * - ``diffstat``
     - 1.66
     - 1.67
   * - ``diffutils``
     - 3.10
     - 3.11
   * - ``dnf``
     - 4.21.1
     - 4.22.0
   * - ``dropbear``
     - 2024.85
     - 2024.86
   * - ``dtc``
     - 1.7.0
     - 1.7.2
   * - ``ed``
     - 1.20.2
     - 1.21
   * - ``efivar``
     - 39+39+git
     - 39
   * - ``elfutils``
     - 0.191
     - 0.192
   * - ``ell``
     - 0.68
     - 0.74
   * - ``epiphany``
     - 46.3
     - 48.0
   * - ``erofs-utils``
     - 1.8.1
     - 1.8.5
   * - ``ethtool``
     - 6.10
     - 6.11
   * - ``expat``
     - 2.6.4
     - 2.7.0
   * - ``ffmpeg``
     - 7.0.2
     - 7.1.1
   * - ``file``
     - 5.45
     - 5.46
   * - ``fmt``
     - 11.0.2
     - 11.1.4
   * - ``fribidi``
     - 1.0.15
     - 1.0.16
   * - ``gawk``
     - 5.3.0
     - 5.3.1
   * - ``gcr``
     - 4.3.0
     - 4.3.1
   * - ``gdb``
     - 15.1
     - 16.2
   * - ``gdb-cross``
     - 15.1
     - 16.2
   * - ``gdb-cross-canadian``
     - 15.1
     - 16.2
   * - ``gettext``
     - 0.22.5
     - 0.23.1
   * - ``gettext-minimal-native``
     - 0.22.5
     - 0.23.1
   * - ``ghostscript``
     - 10.04.0
     - 10.05.0
   * - ``gi-docgen``
     - 2024.1
     - 2025.3
   * - ``git``
     - 2.46.1
     - 2.49.0
   * - ``glib-2``
     - .0 2.80.4
     - 2.84.0
   * - ``glib-2``
     - .0-initial 2.80.4
     - 2.84.0
   * - ``glibc``
     - 2.40+git
     - 2.41+git
   * - ``glibc-locale``
     - 2.40+git
     - 2.41+git
   * - ``glibc-mtrace``
     - 2.40+git
     - 2.41+git
   * - ``glibc-scripts``
     - 2.40+git
     - 2.41+git
   * - ``glibc-testsuite``
     - 2.40+git
     - 2.41+git
   * - ``glibc-y2038-tests``
     - 2.40+git
     - 2.41+git
   * - ``glslang``
     - 1.3.290.0
     - 1.4.309.0
   * - ``gnu-efi``
     - 3.0.18
     - 4.0.0
   * - ``gnupg``
     - 2.5.0
     - 2.5.5
   * - ``gnutls``
     - 3.8.6
     - 3.8.9
   * - ``go``
     - 1.22.12
     - 1.24.1
   * - ``go-binary-native``
     - 1.22.12
     - 1.24.1
   * - ``go-cross-canadian``
     - 1.22.12
     - 1.24.1
   * - ``go-cross-core2-32``
     - 1.22.12
     - 1.24.1
   * - ``go-crosssdk``
     - 1.22.12
     - 1.24.1
   * - ``go-helloworld``
     - 0.1 (39e772fc2670…)
     - 0.1 (d7b0ac127859…)
   * - ``go-runtime``
     - 1.22.12
     - 1.24.1
   * - ``gobject-introspection``
     - 1.80.1
     - 1.84.0
   * - ``gpgme``
     - 1.23.2
     - 1.24.2
   * - ``gsettings-desktop-schemas``
     - 46.1
     - 48.0
   * - ``gst-devtools``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0-libav``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0-plugins-bad``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0-plugins-base``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0-plugins-good``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0-plugins-ugly``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0-python``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0-rtsp-server``
     - 1.24.10
     - 1.24.12
   * - ``gstreamer1.0-vaapi``
     - 1.24.10
     - 1.24.12
   * - ``gtk4``
     - 4.14.5
     - 4.18.1
   * - ``harfbuzz``
     - 9.0.0
     - 10.4.0
   * - ``hwlatdetect``
     - 2.7
     - 2.8
   * - ``i2c-tools``
     - 4.3
     - 4.4
   * - ``icu``
     - 75-1
     - 76-1
   * - ``ifupdown``
     - 0.8.43
     - 0.8.44
   * - ``igt-gpu-tools``
     - 1.28
     - 1.30
   * - ``inetutils``
     - 2.5
     - 2.6
   * - ``init-system-helpers``
     - 1.66
     - 1.68
   * - ``iproute2``
     - 6.10.0
     - 6.13.0
   * - ``iptables``
     - 1.8.10
     - 1.8.11
   * - ``iputils``
     - 20240117
     - 20240905
   * - ``iso-codes``
     - 4.16.0
     - 4.17.0
   * - ``json-c``
     - 0.17
     - 0.18
   * - ``json-glib``
     - 1.8.0
     - 1.10.6
   * - ``kbd``
     - 2.6.4
     - 2.7.1
   * - ``kern-tools-native``
     - 0.3+git (7160ebe8b865…)
     - 0.3+git (bfca22a52ec5…)
   * - ``kexec-tools``
     - 2.0.29
     - 2.0.30
   * - ``kmod``
     - 33
     - 34.1
   * - ``kmscube``
     - 0.0.1+git (b2f97f53e01e…)
     - 0.0.1+git (311eaaaa473d…)
   * - ``less``
     - 661
     - 668
   * - ``libadwaita``
     - 1.5.3
     - 1.7.0
   * - ``libarchive``
     - 3.7.4
     - 3.7.8
   * - ``libassuan``
     - 3.0.1
     - 3.0.2
   * - ``libcap``
     - 2.70
     - 2.75
   * - ``libdnf``
     - 0.73.3
     - 0.73.4
   * - ``libdrm``
     - 2.4.123
     - 2.4.124
   * - ``libedit``
     - 20240808-3.1
     - 20250104-3.1
   * - ``libexif``
     - 0.6.24
     - 0.6.25
   * - ``libffi``
     - 3.4.6
     - 3.4.7
   * - ``libgit2``
     - 1.8.1
     - 1.9.0
   * - ``libgpg-error``
     - 1.50
     - 1.51
   * - ``libical``
     - 3.0.18
     - 3.0.20
   * - ``libice``
     - 1.1.1
     - 1.1.2
   * - ``libidn2``
     - 2.3.7
     - 2.3.8
   * - ``libinput``
     - 1.26.1
     - 1.27.1
   * - ``libjitterentropy``
     - 3.5.0
     - 3.6.2
   * - ``libmatchbox``
     - 1.12
     - 1.13
   * - ``libnl``
     - 3.10.0
     - 3.11.0
   * - ``libnotify``
     - 0.8.3
     - 0.8.4
   * - ``libpam``
     - 1.6.1
     - 1.7.0
   * - ``libpcre2``
     - 10.44
     - 10.45
   * - ``libpipeline``
     - 1.5.7
     - 1.5.8
   * - ``libpng``
     - 1.6.43
     - 1.6.47
   * - ``libportal``
     - 0.7.1
     - 0.9.1
   * - ``libproxy``
     - 0.5.8
     - 0.5.9
   * - ``librepo``
     - 1.18.1
     - 1.19.0
   * - ``librsvg``
     - 2.58.2
     - 2.59.2
   * - ``libsdl2``
     - 2.30.7
     - 2.32.2
   * - ``libseccomp``
     - 2.5.5
     - 2.6.0
   * - ``libsecret``
     - 0.21.4
     - 0.21.6
   * - ``libslirp``
     - 4.8.0
     - 4.9.0
   * - ``libsm``
     - 1.2.4
     - 1.2.6
   * - ``libsolv``
     - 0.7.30
     - 0.7.31
   * - ``libsoup``
     - 3.6.0
     - 3.6.4
   * - ``libssh2``
     - 1.11.0
     - 1.11.1
   * - ``libstd-rs``
     - 1.79.0
     - 1.84.1
   * - ``libtest-warnings-perl``
     - 0.033
     - 0.038
   * - ``libtirpc``
     - 1.3.5
     - 1.3.6
   * - ``libtool``
     - 2.5.2
     - 2.5.4
   * - ``libtool-cross``
     - 2.5.2
     - 2.5.4
   * - ``libtool-native``
     - 2.5.2
     - 2.5.4
   * - ``libtraceevent``
     - 1.8.3
     - 1.8.4
   * - ``libtry-tiny-perl``
     - 0.31
     - 0.32
   * - ``libubootenv``
     - 0.3.5
     - 0.3.6
   * - ``libunistring``
     - 1.2
     - 1.3
   * - ``liburcu``
     - 0.14.1
     - 0.15.1
   * - ``libuv``
     - 1.48.0
     - 1.50.0
   * - ``libwebp``
     - 1.4.0
     - 1.5.0
   * - ``libwpe``
     - 1.16.0
     - 1.16.2
   * - ``libx11``
     - 1.8.10
     - 1.8.12
   * - ``libxau``
     - 1.0.11
     - 1.0.12
   * - ``libxcrypt``
     - 4.4.36
     - 4.4.38
   * - ``libxcrypt-compat``
     - 4.4.36
     - 4.4.38
   * - ``libxcursor``
     - 1.2.2
     - 1.2.3
   * - ``libxcvt``
     - 0.1.2
     - 0.1.3
   * - ``libxi``
     - 1.8.1
     - 1.8.2
   * - ``libxkbcommon``
     - 1.7.0
     - 1.8.1
   * - ``libxmlb``
     - 0.3.19
     - 0.3.22
   * - ``libxrender``
     - 0.9.11
     - 0.9.12
   * - ``libxshmfence``
     - 1.3.2
     - 1.3.3
   * - ``libxslt``
     - 1.1.42
     - 1.1.43
   * - ``libxt``
     - 1.3.0
     - 1.3.1
   * - ``libxv``
     - 1.0.12
     - 1.0.13
   * - ``libxxf86vm``
     - 1.1.5
     - 1.1.6
   * - ``lighttpd``
     - 1.4.76
     - 1.4.77
   * - ``linux-firmware``
     - 20240909
     - 20250311
   * - ``linux-libc-headers``
     - 6.10
     - 6.12
   * - ``linux-yocto``
     - 6.6.75+git, 6.10.14+git
     - 6.12.19+git
   * - ``linux-yocto-dev``
     - 6.11+git
     - 6.14+git
   * - ``linux-yocto-rt``
     - 6.6.75+git, 6.10.14+git
     - 6.12.19+git
   * - ``linux-yocto-tiny``
     - 6.6.75+git, 6.10.14+git
     - 6.12.19+git
   * - ``llvm``
     - 18.1.8
     - 20.1.0
   * - ``log4cplus``
     - 2.1.1
     - 2.1.2
   * - ``lsof``
     - 4.99.3
     - 4.99.4
   * - ``ltp``
     - 20240524
     - 20250130
   * - ``lttng-modules``
     - 2.13.14
     - 2.13.17
   * - ``lzip``
     - 1.24.1
     - 1.25
   * - ``lzlib``
     - 1.14
     - 1.15
   * - ``man-db``
     - 2.12.1
     - 2.13.0
   * - ``man-pages``
     - 6.9.1
     - 6.13
   * - ``mc``
     - 4.8.32
     - 4.8.33
   * - ``mesa-demos``
     - 8.5.0
     - 9.0.0
   * - ``meson``
     - 1.5.1
     - 1.7.0
   * - ``minicom``
     - 2.9
     - 2.10
   * - ``mmc-utils``
     - 0.1+git (123fd8b2ac39…)
     - 0.1+git (2aef4cd9a84d…)
   * - ``mpg123``
     - 1.32.7
     - 1.32.10
   * - ``msmtp``
     - 1.8.26
     - 1.8.28
   * - ``mtd-utils``
     - 2.2.0
     - 2.3.0
   * - ``mtools``
     - 4.0.44
     - 4.0.48
   * - ``musl``
     - 1.2.5+git (dd1e63c3638d…)
     - 1.2.5+git (c47ad25ea3b4…)
   * - ``nativesdk-libtool``
     - 2.5.2
     - 2.5.4
   * - ``netbase``
     - 6.4
     - 6.5
   * - ``nettle``
     - 3.10
     - 3.10.1
   * - ``nfs-utils``
     - 2.6.4
     - 2.8.2
   * - ``nghttp2``
     - 1.63.0
     - 1.65.0
   * - ``npth``
     - 1.7
     - 1.8
   * - ``numactl``
     - 2.0.18
     - 2.0.19
   * - ``ofono``
     - 2.10
     - 2.15
   * - ``opensbi``
     - 1.5.1
     - 1.6
   * - ``openssh``
     - 9.8p1
     - 9.9p2
   * - ``openssl``
     - 3.3.1
     - 3.4.1
   * - ``orc``
     - 0.4.40
     - 0.4.41
   * - ``ovmf``
     - edk2-stable202402
     - edk2-stable202411
   * - ``pango``
     - 1.54.0
     - 1.56.2
   * - ``piglit``
     - 1.0+gitr (c11c9374c144…)
     - 1.0+gitr (fc8179d31904…)
   * - ``pixman``
     - 0.42.2
     - 0.44.2
   * - ``pkgconf``
     - 2.3.0
     - 2.4.3
   * - ``ppp``
     - 2.5.0
     - 2.5.2
   * - ``procps``
     - 4.0.4
     - 4.0.5
   * - ``psplash``
     - 0.1+git (ecc191375669…)
     - 0.1+git (1f64c654129f…)
   * - ``ptest-runner``
     - 2.4.5+git
     - 2.4.5.1
   * - ``puzzles``
     - 0.0+git (1c1899ee1c4e…)
     - 0.0+git (7da464122232…)
   * - ``python3``
     - 3.12.9
     - 3.13.2
   * - ``python3-attrs``
     - 24.2.0
     - 25.3.0
   * - ``python3-babel``
     - 2.16.0
     - 2.17.0
   * - ``python3-bcrypt``
     - 4.2.0
     - 4.3.0
   * - ``python3-beartype``
     - 0.18.5
     - 0.20.0
   * - ``python3-build``
     - 1.2.1
     - 1.2.2
   * - ``python3-certifi``
     - 2024.8.30
     - 2025.1.31
   * - ``python3-cffi``
     - 1.17.0
     - 1.17.1
   * - ``python3-click``
     - 8.1.7
     - 8.1.8
   * - ``python3-cryptography``
     - 42.0.8
     - 44.0.2
   * - ``python3-cryptography-vectors``
     - 42.0.8
     - 44.0.2
   * - ``python3-cython``
     - 3.0.11
     - 3.0.12
   * - ``python3-dbus``
     - 1.3.2
     - 1.4.0
   * - ``python3-dbusmock``
     - 0.32.1
     - 0.33.0
   * - ``python3-dtc``
     - 1.7.0
     - 1.7.2
   * - ``python3-dtschema``
     - 2024.5
     - 2025.2
   * - ``python3-flit-core``
     - 3.9.0
     - 3.11.0
   * - ``python3-gitdb``
     - 4.0.11
     - 4.0.12
   * - ``python3-hatchling``
     - 1.25.0
     - 1.27.0
   * - ``python3-hypothesis``
     - 6.111.2
     - 6.129.2
   * - ``python3-idna``
     - 3.8
     - 3.10
   * - ``python3-jinja2``
     - 3.1.4
     - 3.1.6
   * - ``python3-jsonschema-specifications``
     - 2023.12.1
     - 2024.10.1
   * - ``python3-license-expression``
     - 30.3.1
     - 30.4.1
   * - ``python3-lxml``
     - 5.3.0
     - 5.3.1
   * - ``python3-mako``
     - 1.3.5
     - 1.3.9
   * - ``python3-markdown``
     - 3.6
     - 3.7
   * - ``python3-markupsafe``
     - 2.1.5
     - 3.0.2
   * - ``python3-maturin``
     - 1.7.1
     - 1.8.3
   * - ``python3-meson-python``
     - 0.16.0
     - 0.17.1
   * - ``python3-more-itertools``
     - 10.4.0
     - 10.6.0
   * - ``python3-numpy``
     - 1.26.4
     - 2.2.3
   * - ``python3-packaging``
     - 24.1
     - 24.2
   * - ``python3-pip``
     - 24.2
     - 25.0.1
   * - ``python3-poetry-core``
     - 1.9.0
     - 2.1.1
   * - ``python3-psutil``
     - 6.0.0
     - 7.0.0
   * - ``python3-pyasn1``
     - 0.6.0
     - 0.6.1
   * - ``python3-pycairo``
     - 1.26.1
     - 1.27.0
   * - ``python3-pycryptodome``
     - 3.20.0
     - 3.22.0
   * - ``python3-pycryptodomex``
     - 3.20.0
     - 3.22.0
   * - ``python3-pyelftools``
     - 0.31
     - 0.32
   * - ``python3-pygments``
     - 2.18.0
     - 2.19.1
   * - ``python3-pygobject``
     - 3.48.2
     - 3.52.2
   * - ``python3-pyopenssl``
     - 24.2.1
     - 25.0.0
   * - ``python3-pyparsing``
     - 3.1.4
     - 3.2.1
   * - ``python3-pyproject-hooks``
     - 1.0.0
     - 1.2.0
   * - ``python3-pyproject-metadata``
     - 0.8.0
     - 0.9.1
   * - ``python3-pytest``
     - 8.3.2
     - 8.3.5
   * - ``python3-pytest-subtests``
     - 0.13.1
     - 0.14.1
   * - ``python3-pytz``
     - 2024.1
     - 2025.1
   * - ``python3-rdflib``
     - 7.0.0
     - 7.1.3
   * - ``python3-referencing``
     - 0.35.1
     - 0.36.2
   * - ``python3-rpds-py``
     - 0.20.0
     - 0.22.3
   * - ``python3-ruamel-yaml``
     - 0.18.6
     - 0.18.10
   * - ``python3-scons``
     - 4.8.0
     - 4.9.0
   * - ``python3-setuptools``
     - 72.1.0
     - 76.0.0
   * - ``python3-setuptools-rust``
     - 1.10.1
     - 1.11.0
   * - ``python3-setuptools-scm``
     - 8.1.0
     - 8.2.0
   * - ``python3-six``
     - 1.16.0
     - 1.17.0
   * - ``python3-spdx-tools``
     - 0.8.2
     - 0.8.3
   * - ``python3-sphinx``
     - 8.0.2
     - 8.2.1
   * - ``python3-sphinx-rtd-theme``
     - 2.0.0
     - 3.0.2
   * - ``python3-trove-classifiers``
     - 2024.7.2
     - 2025.3.13.13
   * - ``python3-typogrify``
     - 2.0.7
     - 2.1.0
   * - ``python3-urllib3``
     - 2.2.2
     - 2.3.0
   * - ``python3-websockets``
     - 13.0.1
     - 15.0.1
   * - ``python3-wheel``
     - 0.44.0
     - 0.45.1
   * - ``python3-xmltodict``
     - 0.13.0
     - 0.14.2
   * - ``python3-yamllint``
     - 1.35.1
     - 1.36.0
   * - ``python3-zipp``
     - 3.20.1
     - 3.21.0
   * - ``qemu``
     - 9.0.2
     - 9.2.0
   * - ``qemu-native``
     - 9.0.2
     - 9.2.0
   * - ``qemu-system-native``
     - 9.0.2
     - 9.2.0
   * - ``re2c``
     - 3.1
     - 4.1
   * - ``repo``
     - 2.46
     - 2.52
   * - ``rpm``
     - 4.19.1.1
     - 4.20.0
   * - ``rsync``
     - 3.3.0
     - 3.4.1
   * - ``rt-tests``
     - 2.7
     - 2.8
   * - ``ruby``
     - 3.3.4
     - 3.4.2
   * - ``rust``
     - 1.79.0
     - 1.84.1
   * - ``rust-cross-canadian``
     - 1.79.0
     - 1.84.1
   * - ``rust-llvm``
     - 1.79.0
     - 1.84.1
   * - ``screen``
     - 4.9.1
     - 5.0.0
   * - ``seatd``
     - 0.8.0
     - 0.9.1
   * - ``shaderc``
     - 2024.1
     - 2024.3
   * - ``shadow``
     - 4.16.0
     - 4.17.3
   * - ``socat``
     - 1.8.0.0
     - 1.8.0.3
   * - ``spirv-headers``
     - 1.3.290.0
     - 1.4.309.0
   * - ``spirv-tools``
     - 1.3.290.0
     - 1.4.309.0
   * - ``sqlite3``
     - 3.46.1
     - 3.48.0
   * - ``strace``
     - 6.10
     - 6.12
   * - ``stress-ng``
     - 0.18.02
     - 0.18.11
   * - ``subversion``
     - 1.14.3
     - 1.14.5
   * - ``sudo``
     - 1.9.15p5
     - 1.9.16p2
   * - ``swig``
     - 4.2.1
     - 4.3.0
   * - ``sysklogd``
     - 2.6.1
     - 2.7.1
   * - ``sysstat``
     - 12.7.6
     - 12.7.7
   * - ``systemd``
     - 256.5
     - 257.4
   * - ``systemd-boot``
     - 256.5
     - 257.4
   * - ``systemd-boot-native``
     - 256.5
     - 257.4
   * - ``systemd-systemctl-native``
     - 1.0
     - 257.4
   * - ``systemtap``
     - 5.1
     - 5.2
   * - ``systemtap-native``
     - 5.1
     - 5.2
   * - ``sysvinit``
     - 3.04
     - 3.14
   * - ``tcl``
     - 8.6.14
     - 9.0.1
   * - ``texinfo``
     - 7.1
     - 7.2
   * - ``tiff``
     - 4.6.0
     - 4.7.0
   * - ``ttyrun``
     - 2.34.0
     - 2.37.0
   * - ``u-boot``
     - 2024.07
     - 2025.01
   * - ``u-boot-tools``
     - 2024.07
     - 2025.01
   * - ``usbutils``
     - 017
     - 018
   * - ``utfcpp``
     - 4.0.5
     - 4.0.6
   * - ``util-linux``
     - 2.40.2
     - 2.40.4
   * - ``util-linux-libuuid``
     - 2.40.2
     - 2.40.4
   * - ``util-macros``
     - 1.20.1
     - 1.20.2
   * - ``vala``
     - 0.56.17
     - 0.56.18
   * - ``valgrind``
     - 3.23.0
     - 3.24.0
   * - ``vim``
     - 9.1.1043
     - 9.1.1198
   * - ``vim-tiny``
     - 9.1.1043
     - 9.1.1198
   * - ``virglrenderer``
     - 1.0.1
     - 1.1.0
   * - ``vte``
     - 0.76.3
     - 0.78.2
   * - ``vulkan-headers``
     - 1.3.290.0
     - 1.4.309.0
   * - ``vulkan-loader``
     - 1.3.290.0
     - 1.4.309.0
   * - ``vulkan-samples``
     - git (fdce530c0295…)
     - git (8547ce1022a1…)
   * - ``vulkan-tools``
     - 1.3.290.0
     - 1.4.309.0
   * - ``vulkan-utility-libraries``
     - 1.3.290.0
     - 1.4.309.0
   * - ``vulkan-validation-layers``
     - 1.3.290.0
     - 1.4.309.0
   * - ``vulkan-volk``
     - 1.3.290.0
     - 1.4.309.0
   * - ``wayland-protocols``
     - 1.37
     - 1.41
   * - ``webkitgtk``
     - 2.44.3
     - 2.48.0
   * - ``weston``
     - 13.0.3
     - 14.0.1
   * - ``wget``
     - 1.24.5
     - 1.25.0
   * - ``wireless-regdb``
     - 2024.10.07
     - 2025.02.20
   * - ``wpebackend-fdo``
     - 1.14.2
     - 1.16.0
   * - ``xauth``
     - 1.1.3
     - 1.1.4
   * - ``xcb-util-cursor``
     - 0.1.4
     - 0.1.5
   * - ``xf86-input-evdev``
     - 2.10.6
     - 2.11.0
   * - ``xf86-input-libinput``
     - 1.4.0
     - 1.5.0
   * - ``xf86-input-synaptics``
     - 1.9.2
     - 1.10.0
   * - ``xf86-video-fbdev``
     - 0.5.0
     - 0.5.1
   * - ``xhost``
     - 1.0.9
     - 1.0.10
   * - ``xinit``
     - 1.4.2
     - 1.4.4
   * - ``xkeyboard-config``
     - 2.42
     - 2.44
   * - ``xprop``
     - 1.2.7
     - 1.2.8
   * - ``xrandr``
     - 1.5.2
     - 1.5.3
   * - ``xtrans``
     - 1.5.0
     - 1.6.0
   * - ``xxhash``
     - 0.8.2
     - 0.8.3
   * - ``xz``
     - 5.6.2
     - 5.6.4
   * - ``zstd``
     - 1.5.6
     - 1.5.
       7

Contributors to |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Thanks to the following people who contributed to this release:

-  Aditya Tayade
-  Adrian Freihofer
-  Alban Bedel
-  Aleksandar Nikolic
-  Alessio Cascone
-  Alexander Hirsch
-  Alexander Kanavin
-  Alexander Sverdlin
-  Alexander van Gessel
-  Alexander Yurkov
-  Alexandre Marques
-  Alexis Cellier
-  Alex Kiernan
-  Andrej Valek
-  Angelo Ribeiro
-  Antonin Godard
-  Archana Polampalli
-  Artur Kowalski
-  Awais Belal
-  Balaji Pothunoori
-  Bartosz Golaszewski
-  Bastian Germann
-  Bastian Krause
-  Bastien JAUNY
-  BELHADJ SALEM Talel
-  Benjamin Bara
-  Benjamin Grossschartner
-  Benjamin Szőke
-  Bin Lan
-  Bruce Ashfield
-  Changhyeok Bae
-  Changqing Li
-  Chen Qi
-  Chris Laplante
-  Christian Lindeberg
-  Christian Taedcke
-  Christos Gavros
-  Claus Stovgaard
-  Clayton Casciato
-  Colin McAllister
-  Daniel Ammann
-  Daniel McGregor
-  Dan McGregor
-  Deepesh Varatharajan
-  Deepthi Hemraj
-  Denis OSTERLAND-HEIM
-  Denys Dmytriyenko
-  Derek Straka
-  Divya Chellam
-  Dmitry Baryshkov
-  Enrico Jörns
-  Enrico Scholz
-  Eric Meyers
-  Esben Haabendal
-  Etienne Cordonnier
-  Fabio Berton
-  Fabio Estevam
-  Gaël PORTAY
-  Georgi, Tom
-  Guðni Már Gilbert
-  Guénaël Muller
-  Harish Sadineni
-  Haseeb Ashraf
-  Hiago De Franco
-  Hongxu Jia
-  Igor Opaniuk
-  Jagadeesh Krishnanjanappa
-  Jamin Lin
-  Jason Schonberg
-  Jean-Pierre Geslin
-  Jermain Horsman
-  Jesse Riemens
-  Jiaying Song
-  Jinfeng Wang
-  João Henrique Ferreira de Freitas
-  Joerg Schmidt
-  Jonas Gorski
-  Jon Mason
-  Jörg Sommer
-  Jose Quaresma
-  Joshua Watt
-  Julien Stephan
-  Justin Bronder
-  Kai Kang
-  Katariina Lounento
-  Katawann
-  Kevin Hao
-  Khem Raj
-  Koen Kooi
-  Lee Chee Yang
-  Lei Maohui
-  Lei YU
-  Leon Anavi
-  Louis Rannou
-  Maik Otto
-  Makarios Christakis
-  Marc Ferland
-  Marco Felsch
-  Marek Vasut
-  Mark Hatle
-  Markus Volk
-  Marta Rybczynska
-  Martin Jansa
-  Mathieu Dubois-Briand
-  Matthias Schiffer
-  Maxin John
-  Michael Estner
-  Michael Halstead
-  Michael Nazzareno Trimarchi
-  Michael Opdenacker
-  Michelle Lin
-  Mikko Rapeli
-  Ming Liu
-  Moritz Haase
-  Nick Owens
-  Nicolas Dechesne
-  Nikolai Merinov
-  Niko Mauno
-  Ninette Adhikari
-  Ola x Nilsson
-  Oleksandr Hnatiuk
-  Oliver Kästner
-  Omri Sarig
-  Pascal Eberhard
-  Patrik Nordvall
-  Paul Barker
-  Pavel Zhukov
-  Pedro Ferreira
-  Peter Bergin
-  Peter Delevoryas
-  Peter Kjellerstedt
-  Peter Marko
-  Peter Tatrai
-  Philip Lorenz
-  Priyal Doshi
-  Purushottam Choudhary
-  Quentin Schulz
-  Ralph Siemsen
-  Randy MacLeod
-  Ranjitsinh Rathod
-  Rasmus Villemoes
-  Regis Dargent
-  Ricardo Salveti
-  Richard Purdie
-  Robert Yang
-  Rohini Sangam
-  Roland Hieber
-  Ross Burton
-  Ryan Eatmon
-  Savvas Etairidis
-  Sean Nyekjaer
-  Sebastian Zenker
-  Sergei Zhmylev
-  Shunsuke Tokumoto
-  Sid-Ali
-  Simon A. Eugster
-  Simone Weiß
-  Slawomir Stepien
-  Sofiane HAMAM
-  Stefan Gloor
-  Stefan Herbrechtsmeier
-  Stefan Koch
-  Stefan Mueller-Klieser
-  Steve Sakoman
-  Sunil Dora
-  Sven Kalmbach
-  Talel BELHAJSALEM
-  Thomas Perrot
-  Thomas Roos
-  Tim Orling
-  Tom Hochstein
-  Trevor Gamblin
-  Ulrich Ölmann
-  Valeria Petrov
-  Victor J. Hansen
-  Victor Kamensky
-  Vijay Anusuri
-  Vince Chang
-  Vivek Puar
-  Vyacheslav Yurkov
-  Walter Schweizer
-  Wang Mingyu
-  Weisser, Pascal
-  Xiangyu Chen
-  Xiaotian Wu
-  Yash Shinde
-  Yi Zhao
-  Yoann Congal
-  Yogita Urade
-  Zoltán Böszörményi

Repositories / Downloads for Yocto-|yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`walnascar </poky/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2 </poky/log/?h=yocto-5.2>`
-  Git Revision: :yocto_git:`9b96fdbb0cab02f4a6180e812b02bc9d4c41b1a5 </poky/commit/?id=9b96fdbb0cab02f4a6180e812b02bc9d4c41b1a5>`
-  Release Artefact: poky-9b96fdbb0cab02f4a6180e812b02bc9d4c41b1a5
-  sha: 2d3c0e216c7fa71a364986be6754549e2059d37581aad0a53f0f95c33fb1eefe
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2/poky-9b96fdbb0cab02f4a6180e812b02bc9d4c41b1a5.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2/poky-9b96fdbb0cab02f4a6180e812b02bc9d4c41b1a5.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`walnascar </openembedded-core/log/?h=walnascar>`
-  Tag:  :oe_git:`yocto-5.2 </openembedded-core/log/?h=yocto-5.2>`
-  Git Revision: :oe_git:`6ec2c52b938302b894f119f701ffcf0a847eee85 </openembedded-core/commit/?id=6ec2c52b938302b894f119f701ffcf0a847eee85>`
-  Release Artefact: oecore-6ec2c52b938302b894f119f701ffcf0a847eee85
-  sha: 00453354efdd9c977d559f7c0047691bb974170ce313ac9a1e6cb94108d6c648
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2/oecore-6ec2c52b938302b894f119f701ffcf0a847eee85.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2/oecore-6ec2c52b938302b894f119f701ffcf0a847eee85.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`walnascar </meta-mingw/log/?h=walnascar>`
-  Tag:  :yocto_git:`yocto-5.2 </meta-mingw/log/?h=yocto-5.2>`
-  Git Revision: :yocto_git:`edce693e1b8fabd84651aa6c0888aafbcf238577 </meta-mingw/commit/?id=edce693e1b8fabd84651aa6c0888aafbcf238577>`
-  Release Artefact: meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577
-  sha: 6cfed41b54f83da91a6cf201ec1c2cd4ac284f642b1268c8fa89d2335ea2bce1
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2/meta-mingw-edce693e1b8fabd84651aa6c0888aafbcf238577.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.12 </bitbake/log/?h=2.12>`
-  Tag:  :oe_git:`yocto-5.2 </bitbake/log/?h=yocto-5.2>`
-  Git Revision: :oe_git:`5b4e20377eea8d428edf1aeb2187c18f82ca6757 </bitbake/commit/?id=5b4e20377eea8d428edf1aeb2187c18f82ca6757>`
-  Release Artefact: bitbake-5b4e20377eea8d428edf1aeb2187c18f82ca6757
-  sha: 48cff22c1e61f47adce474b636ca865e7e0b62293fc5c8829d09e7f1ac5252af
-  Download Locations:
   https://downloads.yoctoproject.org/releases/yocto/yocto-5.2/bitbake-5b4e20377eea8d428edf1aeb2187c18f82ca6757.tar.bz2
   https://mirrors.kernel.org/yocto/yocto/yocto-5.2/bitbake-5b4e20377eea8d428edf1aeb2187c18f82ca6757.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`walnascar </yocto-docs/log/?h=walnascar>`
-  Tag: :yocto_git:`yocto-5.2 </yocto-docs/log/?h=yocto-5.2>`
-  Git Revision: :yocto_git:`b8d9cf79d299b2e553e6bc962527d835206022ec </yocto-docs/commit/?id=b8d9cf79d299b2e553e6bc962527d835206022ec>`

