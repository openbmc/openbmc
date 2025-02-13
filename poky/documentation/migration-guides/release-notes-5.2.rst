.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: walnascar
.. |yocto-ver| replace:: 5.2

Release notes for |yocto-ver| (|yocto-codename|)
------------------------------------------------

New Features / Enhancements in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Linux kernel 6.XXX, gcc 14.XXX, glibc 2.XXX, LLVM 18.1.XXX, and over XXX other
   recipe upgrades.

-  New variables:

   -  ``linux-firmware``: Add the :term:`FIRMWARE_COMPRESSION` variable which
      allows compression the firmwares provided by the ``linux-firmware`` recipe.
      Possible values are ``xz`` and ``zst``.

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

   -  The :ref:`ref-classes-kernel-yocto` classes now supports in-tree
      configuration fragments. These can be added with the
      :term:`KERNEL_FEATURES` variable.

   -  The ``kern-tools`` recipe is now able to recognize files ending with
      ``.config`` for :ref:`ref-classes-kernel-yocto`-based Kernel recipes.

   -  Support the LZMA compression algorithm in the
      :ref:`ref-classes-kernel-uboot` class. This can be done by setting the
      variable :term:`FIT_KERNEL_COMP_ALG` to ``lzma``.

-  New core recipes:

   -  ``python3-pefile``: required for the :ref:`ref-classes-uki` class.

   -  Add initial support for the `Barebox <https://www.barebox.org>`__
      bootloader, along with associated OEQA test cases.

   -  Import ``makedumpfile`` from meta-openembedded, as the ``kexec-tools``
      recipe :term:`RDEPENDS` on it.

   -  The ``tcl-8`` recipe was added back to support the build of ``expect``.

   -  Add the ``libdisplay-info`` recipe, an EDID and DisplayID library,
      required for Weston 14.0.1 and newer.

   -  The ``hwdata`` recipe was imported from :oe_git:`meta-openembedded
      </meta-openembedded>`, a recipe for hardware identification and
      configuration data, needed by ``libdisplay-info``.

-  New core classes:

   -  New :ref:`ref-classes-uki` class for building Unified Kernel Images (UKI).
      Associated OEQA tests were also added for this class.

   -  New :ref:`ref-classes-cython` class for python recipes that require Cython
      for their compilation. Existing recipes depending on Cython now inherit
      this class. This class also strips potential build paths in the compilation
      output for reproducibility.

-  Architecture-specific changes:

   -  ``tune-cortexa32``: set tune feature to ``armv8a``.

-  QEMU / ``runqemu`` changes:

-  Documentation changes:

   -  Use ``rsvg`` as a replacement of ``inkscape`` to convert svg files in the
      documentation.

   -  The ``cve`` role was replaced by ``cve_nist`` to avoid a conflict with
      more recent version of Sphinx.

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

-  Wic Image Creator changes:

   -  Allow the ``--exclude-path`` option to exclude symlinks.

   -  Add the variable :term:`WIC_SECTOR_SIZE` to control the sector size of Wic
      images.

-  SDK-related changes:

   -  Add support for ZST-compression through :term:`SDK_ARCHIVE_TYPE`, by
      setting its value to ``tar.zst``.

   -  The ``debug-tweaks`` features were removed from ``-sdk`` images
      (``core-image-*-sdk.bb``).

   -  Enable ``ipv6``, ``acl``, and ``xattr`` in :term:`DISTRO_FEATURES_NATIVESDK`.

-  Testing-related changes:

   -  ``oeqa/postactions``: Fix archive retrieval from target.

   -  ``oeqa/selftest/gcc``: Fix kex exchange identification error.

   -  ``oeqa/utils/qemurunner``: support ignoring vt100 escape sequences.

   -  ``oeqa``: support passing custom boot patterns to runqemu.

   -  ``oeqa/selftest/cases``: add basic U-boot and Barebox tests.

   -  ``oeqa/selftest/rust``: skip on all MIPS platforms.

   -  Lots of changes and improvements to the :term:`Toaster` OEQA tests.

   -  ``oeqa/selftest``: add a test for bitbake "-e" and "-getvar" difference.

   -  ``oeqa/ssh``: improve performance and log sizes when handling large files.

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

   -  ``scripts/yocto-check-layer``:

      -  Check for the presence of a ``SECURITY.md`` file in layers and make it
         mandatory.

      -  The :ref:`ref-classes-yocto-check-layer` class now uses
         :term:`CHECKLAYER_REQUIRED_TESTS` to get the list of QA checks to verify
         when running the ``yocto-check-layer`` script.

-  BitBake changes:

   -  ``fetch2``: do not preserve ownership when unpacking.

   -  ``fetch2``: switch from Sqlite ``persist_data`` to a standard cache file
      for checksums, and drop ``persist_data``.

   -  ``fetch2``: add support for GitHub codespaces by adding the
      ``GITHUB_TOKEN`` to the list of variables exported during ``git``
      invocations.

   -  ``fetch2``: set User-Agent to 'bitbake/version' instead of a "fake
      mozilla" user agent.

   -  ``compress``: use ``lz4`` instead of ``lz4c``, as ``lz4c`` as been
      considered deprecrated since 2018.

   -  ``server/process``: decrease idle/main loop frequency, as it is idle and
      main loops have socket select calls to know when to execute.

   -  ``bitbake-worker``: improve bytearray truncation performance when large
       amounts of data are being transferred from the cooker to the worker.

   -  ``bitbake-worker/cooker``: increase the default pipe size from 64KB to
      512KB for better efficiency when transferring large amounts of data.

   -  ``fetch/wget``: increase timeout to 100s from 30s to match CDN worst
      response time.

   -  ``bitbake-getvar``: catch ``NoProvider`` exception to improve error
      readability when a recipe is not found with ``--recipe``.

   -  ``bb/build``: add a function ``bb.build.listtasks()`` to list the tasks in
      a datastore.

   -  Remove custom exception backtrace formatting, and replace occurences of
      ``bb.exception.format_exception()`` by ``traceback.format_exception()``.

   -  ``runqueue``: various performance optimizations including:

      -  Fix performance of multiconfigs with large overlap.
      -  Optimise ``setscene`` loop processing by starting where it
         was left off in the previous execution.

   -  ``knotty`` now hints the user if :term:`MACHINE` was not set in
      the ``local.conf`` file.

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

-  SPDX-related changes:

   -  SPDX 3.0: Find local sources when searching for debug sources.

   -  SPDX 3.0: Map ``gitsm`` URIs to ``git``.

   -  SPDX 3.0: Link license and build by alias instead of SPDX ID.

   -  Fix SPDX tasks not running when code changes (use of ``file-checksums``).

-  ``devtool`` changes:

   -  Remove the "S = WORKDIR" workaround as now :term:`S` cannot be equal to
      :term:`WORKDIR`.

   -  The already broken ``--debug-build-config`` option of
      ``devtool ide-sdk`` has been replaced by a new ``--debug-build`` option
      of ``devtool modify``. The new ``devtool ide-sdk`` workflow is:
      ``devtool modify my-recipe --debug-build`` followed by
      ``devtool ide-sdk my-recipe my-image``.

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

-  New :term:`PACKAGECONFIG` options for individual recipes:

      -  ``perf``: ``zstd``
      -  ``ppp``: ``pam``, ``openssl``
      -  ``libpciaccess``: ``zlib``
      -  ``gdk-pixbuf``: ``gif``, ``others``
      -  ``libpam``: ``selinux``
      -  ``libsecret``: ``pam``
      -  ``rpm``: ``sequoia``

-  Miscellaneous changes:

   -  ``bluez``: fix mesh build when building with musl.

   -  ``systemd-bootchart``: now supports the 32-bit *riscv* architecture.

   -  ``systemd-boot``: now supports the *riscv* architecture.

   -  ``python3-pip``: the ``pip`` executable is now left and not deleted, and
      can be used instead of ``pip3`` and ``pip2``.

   -  ``tar`` image types are now more reproducible as the :term:`IMAGE_CMD` for
      ``tar`` now strips ``atime`` and ``ctime`` from the archive content.

   -  :term:`SOLIBSDEV` and :term:`SOLIBS` are now defined for the *mingw32*
      architecture (``.dll``).

   -  :ref:`rootfs-postcommands <ref-classes-rootfs*>`: make opkg status reproducible.

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
      ``spectre`, and ``tests`` by default.

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

   -  ``systemd``: set better sane time at startup by creating the
      ``clock-epoch`` file in ``${libdir}`` if the ``set-time-epoch``
      :term:`PACKAGECONFIG` config is set.

   -  ``cve-update-nvd2-native``: updating the database will now result in an
      error if :term:`BB_NO_NETWORK` is enabled and
      :term:`CVE_DB_UPDATE_INTERVAL` is not set to ``-1``.

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


Known Issues in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Recipe License changes in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following corrections have been made to the :term:`LICENSE` values set by recipes:

Security Fixes in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Recipe Upgrades in |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Contributors to |yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Repositories / Downloads for Yocto-|yocto-ver|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
