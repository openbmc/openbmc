.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: blacksail
.. |yocto-ver| replace:: 6.1
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Release notes for |yocto-ver| (|yocto-codename|)
================================================

This document lists new features and enhancements for the Yocto Project
|yocto-ver| Release (codename "|yocto-codename|"). For a list of breaking
changes and migration guides, see the :doc:`/migration-guides/migration-6.1`
section.

New Features / Enhancements in |yocto-ver|
------------------------------------------

-  Linux kernel 7.2, gcc 16.2, glibc 2.44, LLVM 23.1.1, and over 300 other
   recipe upgrades

..
   Found in meta/lib/oe/sanity.py:check_sanity_everybuild

-  Minimum Python version required on the host: 3.9.

-  Kernel-related changes:

   -  :ref:`ref-classes-kernel-module-split`: Return list of values in
      ``extract_modinfo``
      (:oecore_rev:`43da9b93bdf40bc4f2fb81687e80b94571232d5b`)

   -  :ref:`ref-classes-kernel`: Disable module tarball deployment
      (:term:`MODULE_TARBALL_DEPLOY`) by default
      (:oecore_rev:`f51d48c9eb0bcb630525566091c66ec1af4d770e`)

   -  ``linux-yocto``: Remove CVE exclusion list as
      :ref:`ref-classes-sbom-cve-check` does this itself
      (:oecore_rev:`596ad6c397c5ca264c6c257283ea78095562e67c`)

   -  :ref:`ref-classes-kernel-fit-image`:

      -  Validate key files expected by
         ``mkimage`` for the selected algorithm
         (:oecore_rev:`7570ab598d10a08fa046d3e8f5897424193240e1`)

      -  Do not include ``kernel`` property in DTBO configuration sub-nodes
         (:oecore_rev:`06ed34005957a6afb88270603df5e545941546b0`)

      -  Fix operation with :term:`KERNEL_DTBVENDORED` set to "1"
         (:oecore_rev:`4297b94c3728cd2e320d75b68c508e12ab127719`)

      -  Add :term:`KERNEL_DTBVENDORED` support for :term:`FIT_CONF_DEFAULT_DTB`
         (:oecore_rev:`3bceb2dabeee13c0a80ddd74ea7ae991606d6772`)

      -  Make kernel entry point and load address optional
         (:oecore_rev:`54c4b4826a3a67fbfef23c93356bfd7e5cb097c1`)

      -  Avoid shadowing os stdlib import
         (:oecore_rev:`fe1b5a0e7a39619b648cc420ef6ea4033f8cc50d`)

      -  Introduce :term:`FIT_OS` variable to override 'os' field
         (:oecore_rev:`dc00e830c3f8e6876630a8c81e7d11c18e531a01`)

      -  Reference the boot script as "script"
         (:oecore_rev:`6bb0775e6ca56eb50119e9ad618dc5b865252f72`)

      -  Skip sign key check for PKCS #11 URI
         (:oecore_rev:`b606b7153001b76de3467e1fd8a66ae6396de5c1`)

      -  Don't add configuration hash node when signing is enabled
         (:oecore_rev:`7346ffe190482ee4932728ba8d8741be8bed1d5b`)

   -  :ref:`ref-classes-kernel-arch`: Add `Clang` toolchain support
      (:oecore_rev:`928d81916f6e3e30e486d63d92a02fd6dcb8a59b`)

   -  :ref:`ref-classes-kernel-arch`: Fall back to GNU ``ld`` unless
      ``ld-is-lld`` is in :term:`DISTRO_FEATURES`
      (:oecore_rev:`f92bfb5309d87aaf5f9683cd3e80e2a1884db6dc`)

   -  :ref:`ref-classes-kernel-yocto`: Set ``CLANG_FLAGS`` for kernel config
      checks when using Clang
      (:oecore_rev:`e88ee794a3f75d7754fe0a3b3c14877c60e7401f`)

   -  ``linux-yocto-dev``: Enable BSP config audit reporting (through
      :term:`KCONF_BSP_AUDIT_LEVEL`)
      (:oecore_rev:`c98e8391b3227d0bfefa9a2e3dee1143f1c54589`)

   -  ``kernel-devsrc``: Install sources required for BPF host tools
      (:oecore_rev:`cf5b6271a9f88f70b2eb9a4a77613ba70c408859`)

   -  :ref:`ref-classes-kernel-yocto-rust`: Add `Clang` toolchain check for
      ``riscv64``
      (:oecore_rev:`2b228490b7ef063cc54165fc2aa582a9abf9aa85`)

   -  ``lib/oe/kernel_module.py``: Re-sign kernel modules after package stripping process
      (:oecore_rev:`a608d107cca13587cd45be45ddfbc4bfdc228f2c`)

   -  :ref:`ref-classes-kernel-module-split`: Don't re-sign compressed kernel modules
      (:oecore_rev:`3483d029346c56050b40596cf4ad51de45fc63f8`)

-  New core recipes:

   -  ``python3-vcs-versioning``: Added as a new dependency of the
      ``python3-setuptools-scm`` recipe
      (:oecore_rev:`507bb5234cc13377ba27b8708f798a0815c3485f`)

   -  ``cgo-helloworld``: A Go example recipe with `cgo
      <https://go.dev/wiki/cgo>`__ enabled
      (:oecore_rev:`204e4f7398ca0e8847a026c5b4f46685760af90a`)

   -  ``jansson``: Add a recipe from ``meta-oe``, as it is a new hard dependency
      of the ``igt-gpu-tools`` recipe
      (:oecore_rev:`2b6e7d092b4a21930186682ebf085848b2e49de5`)

   -  ``packagegroup-core-buildessential-rust``: A
      :ref:`ref-classes-packagegroup` to provide the on-target dependencies
      required for building `Rust`-based kernel modules, including the `Rust`
      toolchain, ``bindgen``, and ``libclang``
      (:oecore_rev:`500b45f8382a0f772c8ade37a46307fb671796b2`)

   -  ``nativesdk-packagegroup-sdk-host-rust``: Similar to
      ``packagegroup-core-buildessential-rust`` but for building `Rust`-based
      kernel modules in the SDK
      (:oecore_rev:`a05c097edb552c5a7bc0fd812be353f079f8d201`)

   -  ``nativesdk-packagegroup-sdk-host-clang``: :ref:`ref-classes-nativesdk`
      :ref:`ref-classes-packagegroup` providing the `Clang`, ``lld``, and
      ``llvm-bin`` dependencies needed to build external/out-of-tree modules in
      the SDK using the LLVM/Clang toolchain
      (:oecore_rev:`2b1db6ec316d6f92d1631b82e91b4c334962213e`)

   -  ``gstreamer1.0-plugins-rs``: GStreamer Rust plugins
      (:oecore_rev:`3a2b032f0fccc9dd636db82a63f67d644ad8bf51`)

   -  ``rust-source``: Recipe that fetches, unpacks, and patches the ``rustc``
      source tree into a "work-shared" location
      (:oecore_rev:`395fe161362c5b6d39a9b01e938d99567c6273c3`)

   -  ``libzip``: Added because it is a dependency of various tools
      (:oecore_rev:`f17e8750c39030fbd4bc41e22d4f5e80c4ccaf87`)

   -  ``mesa-libclc``: New recipe for forking ``libclc`` for ``Rusticl`` purposes in Mesa
      (:oecore_rev:`956aaf355dc875cdf4579884d187e9eb3a236f85`)

   -  ``libtracefs``: Added as ``powertop`` version 2.16 requires it
      (:oecore_rev:`3c3f22739573f91392330e202c0fa7e96c8dc7ab`)

   -  ``libfaketime``: Used for reproducibility testing
      (:oecore_rev:`f3a4faaf4c3cf78f74baa7e5a147749dfa6bbe47`)

   -  ``python3-tomlkit``: New dependency of ``hatchling``
      (:oecore_rev:`46d3b26a63703f4b5d055c0d9da3c7550161ec32`)

-  New core classes:

   -  :ref:`ref-classes-python_uv_build`: Used to build Python recipes with
      ``uv_build``, a slimmed down version of ``uv``
      (:oecore_rev:`c535a9f5dbdc5d8ddb4a2438604456b8084a73aa`)

   -  :ref:`ref-classes-upstream-stable-release-point`: Helper class for setting
      the :term:`UPSTREAM_STABLE_RELEASE_REGEX` variable with
      :term:`STABLE_VERSION_PARTS`
      (:oecore_rev:`a1e069d04cb13e990b362804bd56a4935338ef96`)

   -  :ref:`ref-classes-python_pbr`: Used to build Python recipes with the
      `Python Build Reasonableness <https://pypi.org/project/pbr/>`__ backend
      (:oecore_rev:`b9f3b369b8538a1cf5d4980c9010888d697eb2f1`)

-  New variables:

   -  :term:`UBOOT_FIT_CONF_DESC` allows configuring the description property
      of the configuration node of a U-Boot FIT image
      (:oecore_rev:`df16e4a3900f53245cc8d9a92e277aead56a7369`)

   -  :term:`KERNEL_TOOLCHAIN`: Allows specifying the toolchain to use to build
      the Linux kernel
      (:oecore_rev:`0e3756efe0923e621d88f61121cfa56b23454211`)

   -  :term:`FIT_OS`: Controls the ``os`` field of the FIT image built with the
      :ref:`ref-classes-kernel-fit-image` class
      (:oecore_rev:`dc00e830c3f8e6876630a8c81e7d11c18e531a01`)

   -  :term:`CARGO_PROFILE`: Allows selecting the :ref:`ref-classes-cargo`
      profile to use (``--profile``)
      (:oecore_rev:`d4de7d0256aca47cdb1df48640c37221deaa09d4`)

-  :term:`OpenEmbedded-Core (OE-Core)` library changes:

   -  ``oe/lsb``: Only read ``/etc/os-release``
      (:oecore_rev:`1331c5f2c49e448bc32ec364b13fecfdcf5e05f4`)

   -  ``oe/utils``: Drop ``all_distro_features()``
      (:oecore_rev:`28d32a940ff46ca80db75e8ed24a3e26eec95599`)

-  Global configuration changes:

   -  ``bitbake.conf``:

      -  Define a new ``firmwaredir`` variable (which by default points to
         ``/lib/firmware``). This variable was adopted in several recipes to
         replace ``${nonarch_base_libdir}/firmware``
         (:oecore_rev:`2b75c7ba5e3aa6fc57d7b4afe59e2277b4d87de1`)

      -  Drop ``opencl`` from :term:`DISTRO_FEATURES_FILTER_NATIVE` and
         :term:`DISTRO_FEATURES_FILTER_NATIVESDK`
         (:oecore_rev:`f810998ac90f7a3e20e26555f39d754b16ce8b69`)

   -  :ref:`ref-classes-useradd`: Add support for the :term:`USERMOD_PARAM`
      variable, acting as a replacement of the ``GROUPMEMS_PARAM`` variable
      (:oecore_rev:`b8da733ab12c64503a353d5ceb2eb63fed95d851`,
      :oecore_rev:`cec67e24ac94554e092f8ab18b42e09b4feba77e`)

      Show a deprecation warning if ``GROUPMEMS_PARAM`` is used
      (:oecore_rev:`06f48de92f4b8d7116cd1ce8ba5bc0bd7f8eda9e`)

   -  :ref:`ref-classes-useradd`: Switch from ``--root`` to ``--prefix`` option
      (:oecore_rev:`a7b846ba7d6d63a5e59939d75d9c5fe3e4cbb0e9`)

-  BitBake changes:

   -  The ``git-make-shallow`` utility script was dropped along with its test
      cases. It was replaced by Git native shallow fetches
      :bitbake_rev:`0223ec6f6319b58b740dbbb463dcd190bc85339a`

   -  Replace ``codegen`` with ``ast.unparse()``
      (:bitbake_rev:`76dd6d69c59c1686be2dfb3fc5b72c2a9df34871`)

   -  After the removal of the previous obsolete ``bb.fetch`` module, the
      ``bb.fetch2`` module was renamed to ``bb.fetch``. A warning will be
      printed if ``bb.fetch2`` is used in layers
      (:bitbake_rev:`34ea7cb48090f1ee962d40aca3ebbaf1679b6b23`)

   -  ``fetch/git``:

      -  Fix leaking of temporary directory
         (:bitbake_rev:`c7ebe03c7ebe266795d20c5b722129a0fad86668`)

      -  Fix trailing slash in clone command causing double slash in alternates
         (:bitbake_rev:`797b0a348d7426d03459e577feacd3488fdeee47`)

      -  Accept SHA256 revisions
         (:bitbake_rev:`707ba7e3f218e9d9fff2649ca4be11e2cf3b45ac`)

   -  Unpack RPMs with ``--no-absolute-filenames``
      (:bitbake_rev:`1b1a71586aa93678c1d9ca40ef2c6fa518f89356`)

   -  ``tinfoil``:

      -  Only allow one process progress bar at once
         (:bitbake_rev:`d6bc0e5ec549a4f984cb3d470dd3c04d0ea46fde`)

      -  Add a prepared task runner, to execute a single recipe task and nothing
         else
         (:bitbake_rev:`280a3b379bc11567ac1397e25f164297d65c70eb`)

   -  ``knotty``: Show elapsed time on the task progress bar
      (:bitbake_rev:`b8dfa3567db01d129fc5bda4285fcb36883b0f12`)

   -  ``bitbake-setup``:

      -  Add a "notes" item to ``bitbake-setup`` configuration
         files which create a "conf-notes.txt" file in the :term:`Build Directory`
         (:bitbake_rev:`020a5ba24c7df53eacf834deb87216053ccd38db`)

      -  Add a ``--version`` option
         (:bitbake_rev:`0e39acaaebb726a1e5524fdd28749f747b6ccf63`)

      -  Add PyPI packaging for ``bitbake-setup``, allowing to install the tool
         with ``pip``
         (:bitbake_rev:`6b51b93a77adb363e9da19244064486f968a547e`)

      -  Use the :term:`BitBake` built-in fetcher for buildtools installer download
         (:bitbake_rev:`e8d531cec67644ee4340c6b99365454349ebc246`)

      -  ``default-registry``: Use the
         :ref:`ref-fragments-core-yocto-monitor-disk-space` fragment by default
         (:bitbake_rev:`975b322cfa6349e751e21717c3e2e8526494108c`)

      -  Print a note about fetcher log if something fails there
         (:bitbake_rev:`3860fabb2d0dcdb280ad268c08d6f6f27a417141`)

   -  ``lib``: Drop ``pyinotify``, as it wasn't used by :term:`BitBake` anymore
      (:bitbake_rev:`d20e7dcbd8b6b47273222b5281b956aab58b3351`)

   -  ``lib``: Rename ``fetch2`` as ``fetch`` and invert compatibility shim to
      make ``bb.fetch2`` equal to ``bb.fetch``
      (:bitbake_rev:`34ea7cb48090f1ee962d40aca3ebbaf1679b6b23`)

   -  ``fetch``: Switch to shared locking for read accesses
      (:bitbake_rev:`e8f8ab657e521f6409f0ad783b420e6ca559f317`)

   -  ``fetch/crate``: Support user-defined protocols
      (:bitbake_rev:`22a993aed05baecb06bcf9b6875eb43bf543a1a0`)

   -  ``fetch``: Export ``AWS_SHARED_CREDENTIALS_FILE`` for S3 fetches,
      allowing to use a credential file different than ``~/.aws/credentials``
      (:bitbake_rev:`0d6aa2b58aa2f253c91c3874f42e2051f8b56d56`)

   -  ``fetch/{npm,npmsw}``: Re-enable fetchers now that checksums come from
      :term:`SRC_URI`, and restore the fetcher's test coverage
      (:bitbake_rev:`9e45e00a2009f28829417c9517f5ab623f66dd5d`,
      :bitbake_rev:`f7608f3195af9abb405af9be95286e2267221331`)

   -  ``fetch/wget``:

      -  Handle long filenames correctly
         (:bitbake_rev:`da1fcdf15c647a2a718ae921e261a76e451b442a`)

      -  Reuse cached HTTPS connections
         (:bitbake_rev:`2c236d8bdc51742cbc5a97e30fb126d82d4a908a`)

      -  Cache SSL context instead of recreating every time
         (:bitbake_rev:`625b25ad1c115d0a732330c71a79d323e09b8848`)

   -  ``fetch/gitsm``: Store the original URL data to fix relative ``gitsm://`` paths
      (:bitbake_rev:`58872250f72f5760b4126552bdec5497f5a1a1f4`)

   -  Add ``pyproject.toml`` and ``vendor.txt`` for vendoring
      (:bitbake_rev:`70b54fdace8597043ff70e23737273a17d303a70`)

   -  ``data``: Return a list from exported_vars()
      (:bitbake_rev:`c870f5bd96ad02efc02c58133c27bdae3a300e3e`)

   -  ``asyncrpc``: Add Task Group
      (:bitbake_rev:`4fc36e6b93bb85040526954371ed53a6f8b71ea7`)

   -  ``hashserv``:

      -  Client: Add asynchronous streaming API
         (:bitbake_rev:`544d9f1badcc51843f1448be7c73974b5aea0c3f`)

      -  Server: Add queued streaming API
         (:bitbake_rev:`9e0f0649de0f956c942f5fe42df13ba094e8c92b`)

-  SPDX-related changes:

   -  Add SHA 512 support
      (:oecore_rev:`383a5a63963a063b3cec0f38dcba1c394911c3a5`)

   -  Add custom annotations to recipe packages
      (:oecore_rev:`e5a4a7d7c1916d88456838fbb31ee87d6a1e48ab`)

   -  ``common-licenses``: Add SPDX License Exceptions
      (:oecore_rev:`9c8d126ac9bb6f88a77ed34278f4c3502646260a`)

   -  ``lib/oe``: Add SPDX license library
      (:oecore_rev:`51c793022081ce3c404cb0f8b3fa18551c88f187`)

   -  ``lib/oe/license``: Rework package licenses matching
      (:oecore_rev:`e3c5cb744195f519e9e3b52a68326dcff7749620`)

   -  ``lib/oe/spdx30_tasks``: Fix image deploy dir
      (:oecore_rev:`fe3a72172d7ad67cd1f54e6a1e8337567ccdc042`)

-  :ref:`ref-classes-sbom-cve-check`-related changes:

   -  ``sbom-cve-check`` recipe: Enable offline mode
      (:oecore_rev:`db2e0fdf82af5b50053840e522bf72550d1cbe66`)

-  Packaging changes:

   -  ``package_manager/ipk``: Skip checksums for unsigned local feeds
      (:oecore_rev:`3f50b047b17392a11a4a2587d44c1db8fb652506`)

   -  :ref:`ref-classes-package_rpm`: Drop external dependency generator to support RPM 6
      (:oecore_rev:`093ae9987bd2de38a3a9627fcbc09745f0a8b67f`)

   -  ``libarchive``: Add RPM format reader to support RPM 6
      (:oecore_rev:`8401bac0ca8ab969e9a2861b116896e1f9c8b9f6`)

   -  No longer modify paths in ``save_debugsources_info()``
      (:oecore_rev:`b567c2f0d91fbf36886d0c3a6f6ea90c07771044`)

-  QEMU / ``runqemu`` changes:

   -  ``runqemu-export-rootfs``: Set :term:`PSEUDO_INCLUDE_PATHS` for ``unfsd``
      (:oecore_rev:`76b8b8320436b086059b20813de6523161a5a666`)

   -  ``runqemu-extract-sdk``: Refactor in Python
      (:oecore_rev:`78c176df185b49f7d7720145e85089c3556c9833`)

   -  ``qemu``: Set the compilation progress bar flag
      (:oecore_rev:`21994d6de3f753728ec2bc1ed11d2e819442a3f7`)

   -  ``qemu``: Add ``ppc64le`` support to :term:`COMPATIBLE_HOST`
      (:oecore_rev:`77a329d18a068dcc7d3b24c1e90826dfde45d2d1`)

   -  ``python3-qemu-qmp``: Added as a new dependency of QEMU
      (:oecore_rev:`5babf4e57c20eb54674c323b3527f9b922f17e1e`)

-  Documentation changes:

   -  New document on :doc:`shared state signing </security-manual/sstate-signing>`

   -  New document on :doc:`setting up a shared state mirror </dev-manual/sstate-mirrors-setup>`

   -  New document on how security is handled in builds:
      :doc:`/security-manual/build-process-security`

-  Go changes:

   -  :ref:`ref-classes-go-vendor`: Remove vendor symlink
      (:oecore_rev:`d3cbc285a257f132a17ec042ddb11eef136c6d2b`)

-  Testing-related changes:

   - :ref:`ref-classes-ptest` support was added for the following recipes:

      -  ``go`` (:oecore_rev:`9ca41405e6bca276468a3b6f67eaa328b8016485`)
      -  ``libatomic-ops`` (:oecore_rev:`d899b7f50aad0c09d2db16dc83627fc1898bfab2`)
      -  ``libcap`` (:oecore_rev:`bfc0a651f5bdf57080f811fd0c63b80f179f7563`)
      -  ``libxslt`` (:oecore_rev:`fa4328b70af3e6bad42970322a5874c192761c6d`)
      -  ``python3-certifi`` (:oecore_rev:`0aa251fa23646440182f5507215a91381ddeef5c`)
      -  ``python3-pyopenssl`` (:oecore_rev:`3fdfff9b7e873904674eb64d345334e017bea10d`)
      -  ``python3-vcs-versioning`` (:oecore_rev:`90c8bc12b2af0df3ea73d3e036e114bc683bdb6c`)
      -  ``wget`` (:oecore_rev:`bb24e394d151293be52e110d898e398a95c32ea0`)
      -  ``zstd`` (:oecore_rev:`f3e3ccba7f8090cc6e8752faed6b973409ae0832`)

   -  ``meta-selftest``: Add a ``usegroup-deponly`` recipe to test
      :term:`USERADD_DEPENDS` only
      (:oecore_rev:`36eac58184a22ba972f8613bd9f564e0f6bd1680`)

   -  ``python3``: Use a ``SKIPPED_TESTS`` variable instead of test skip patches
      (:oecore_rev:`a8b2baa6020f96468a98200619ec37c460694c4c`)

   -  ``oeqa/oelib``: Add ``runcmd`` tests
      (:oecore_rev:`b364c44de83bcf73c621972ad5e09e46ccb9d412`)

   -  ``oeqa/selftest/pkgdata``: Add a test for variable consistency in
      :ref:`ref-classes-multilib*`
      (:oecore_rev:`b937278645b4e170c821910c559bfb7be7be0f44`)

   -  ``oeqa/selftest/kernelmodulesplit``: Test cross-recipe kernel module dependencies
      (:oecore_rev:`6ca1f213e2718b95c52a1e643b80d17bb9317da5`)

   -  ``oe-selftest``: :ref:`ref-classes-kernel-fit-image`: Add tests for
      :term:`KERNEL_DTBVENDORED`
      (:oecore_rev:`de2e11e63624f86583875163e66f63ea7457c121`)

   -  :ref:`ref-classes-testimage`: Handle bootlog variants on failed QEMU tests
      (:oecore_rev:`7a6596925cb0eb8dd48a0362a51875cdd60152bc`)

   -  ``oeqa/selftest``: Add :ref:`ref-classes-go-vendor` selftest suite
      (:oecore_rev:`79d251a1893865ad126aabf3599173729a6321a7`)

   -  ``oeqa/selftest/clang``: Add selftests for Clang/LLVM/LLD test suites
      (:oecore_rev:`8e7a293bcf050ff79f5a42d9048ab58960dc335c`)

   -  ``oe-selftest``: ``devtool``: Add test for ``add``/``finish`` workflow
      (:oecore_rev:`36472a7b495eabe5a038644e943931cf6913cf15`)

-  ``devtool``-related changes:

   -  Disable GPG signing when setting up source tree repos
      (:oecore_rev:`b5c84b07b87eafb4f68f7662b6cf26d8b73e3247`)

   -  ``upgrade`` command: Extract changelog between versions
      (:oecore_rev:`ddd54d6d9760e6c049ef82f250c81a10592d409d`)

   -  Fix ``finish``/``update-recipe`` for recipes using :term:`AUTOREV`
      (:oecore_rev:`ae7be93cfd63088f6f160e75a99d9eaca1e1bd7f`)

   -  Fix ``update-recipe``/``finish`` ``--initial-rev`` override
      (:oecore_rev:`62abb60b899e63ea6db5c745f4b1b6af0d4b40f1`)

   -  Guess ``srcrev`` update mode for ``gitsm://`` recipes too
      (:oecore_rev:`9307c37634c56f7df19ca5a93689e2fe6213f10e`)

   -  ``upgrade``: Ignore changelogs from 3rd party
      (:oecore_rev:`1c9d4bb853e457ce533edecd2ddddf1f6ff8a8f1`)

   -  ``deploy-target``: Fix running ``strip`` under ``pseudo``
      (:oecore_rev:`a48a731a2beb7833177400dc9422b1621d064e35`)

   -  ``ide-sdk``:

      -  Add `LLDB` support for `Clang` toolchain
         (:oecore_rev:`e81d8971f756963850be7dee7b05e5579bfe7bbf`)

      -  Default to all modified recipes
         (:oecore_rev:`4c1c0ff4512fd7882f8e0916bc19441e91e5a6c3`)

      -  Auto-write image debug settings to ``bbappend``
         (:oecore_rev:`929a8c198df54f022f1ba65d8931f9d285a0e763`)

      -  Support ``runqemu slirp``
         (:oecore_rev:`5cc15eccbcde40ecdb3ff04ea7c08e0e3dda2d9d`)

      -  Support NFS rootfs
         (:oecore_rev:`4ab284aadca01f4fcb262e99f3d3e48b86121689`)

      -  Add clangd support for VSCode IntelliSense
         (:oecore_rev:`10a0a09003cbfed7fb180fc21d56f021e9dd33f9`)

      -  Support clangd for non-clang toolchain recipes
         (:oecore_rev:`da9ac674451616dcb7c6f8d59e15a459c9fb37d6`)

      -  Format C/C++ with clangd when .clang-format is present
         (:oecore_rev:`5c13e480ad926ebd67f5f0c44e676a3ac2c482d7`)

      -  Support LLDB ATTACH mode
         (:oecore_rev:`6a4227b03a2c515cd97b33f4bf2b9ae65bc5d82c`)

      -  Allow --ide to select multiple plugins
         (:oecore_rev:`8aa5c039973d614f7ebe557e5cd58537bf012ebc`)

   -  Fix file copy on ``finish --force``
      (:oecore_rev:`b8fda5dd1571bba32f894339e83f43d1872db986`)

   -  ``deploy-target``: Add ``--package``/``--file-glob`` filters
      (:oecore_rev:`cf6d07f8d8417b9c241b8e2ba1dbc1fd1e3998ff`)

   -  ``deploy``: Make ``pseudo`` calls independent of bitbake.conf
      (:oecore_rev:`462019b92fb023768a17df5162975e65e60fda47`)

   -  ``deploy``: Allow deploying directly into a local rootfs directory
      (:oecore_rev:`763accbf1ddc558175413b80fdfdc643b8d4ec47`)

-  Utility script changes:

   -  ``scripts/cve-json-to-text.py``: Simplify ``getopt`` argument parsing
      (:oecore_rev:`a92dfe569844189344fbb5ea0521b2bf7dbf0623`)

   -  ``upgrade`` command: Add a ``--stable`` option
      (:oecore_rev:`1e86aa039108621b2af734ef358a1e9d3c4d88d8`)

   -  ``oe-pkgdata-util``: Fix empty ``runtime-rprovides`` directory handling
      (:oecore_rev:`678c1c2077316b6b81ba9be000528b50dca19ca6`,
      :oecore_rev:`dbca656205a7d9a9a9b0aa25b4ad6562af9c5180`)

   -  ``recipeutils``: Add optional ``stable_upgrade`` parameter to
      ``get_recipe_upgrade_status``, to add the possibility of doing stable
      version upgrades of recipes
      (:oecore_rev:`1ed8fdda035dcc21f3df71c0c996973224f4f683`)

   -  ``scripts/pull-spdx-licenses.py``: Add exceptions to the script that
      updates the SPDX license files
      (:oecore_rev:`4190cdd6ceff9b88005f0845866a78eb407b0aa5`)

   -  ``install-buildtools``:

      -  Auto-discover environment setup script via glob
         (:oecore_rev:`1105378e967a812f3bdc3fcc25bb7fd5350cca6c`)

      -  Add a ``--local-file`` option
         (:oecore_rev:`e160ff4510de712ba43e37c8f2bfb1a57580bfaf`)

   -  ``scripts/contrib``: Add ``reuse-get-license.py``, a script that extracts
      the :term:`LICENSE` and :term:`LIC_FILES_CHKSUM` for projects that use
      `reuse <https://reuse.software/>`__
      (:oecore_rev:`8ec5b74051308d0882e2018bbec3be06958dae03`)

   -  ``scripts/contrib``: Add helper to report and fix missing :term:`SRC_URI`
      ``;tag=`` parameters
      (:oecore_rev:`d9a75fa667a89f9dfd81b8fdcea74c5b9048689b`)

   -  ``resulttool``: Add support for ptests results for both `Musl` and `Glibc`
      (:oecore_rev:`c0508fbea8d26242e0c761d412d727943f9d6355`)

   -  ``scripts/oe-depends-dot``: Give the ``dotfile`` argument a sensible default
      (:oecore_rev:`88620a3d917ec23ea3a4760a622bf319a3fe3d84`)

   -  ``sstate-cache-management.py``: Also check the :term:`SSTATE_DIR`
      environment variable for ``--cache-dir``
      (:oecore_rev:`ad44f5edfb440a97ad8add379489b649ae29ab1b`)

-  Clang/LLVM-related changes:

   -  ``clang``: Enable cmake flags for llvm, clang, lld tests
      (:oecore_rev:`c926c4f9f7ecf22698b7426609f8806feee175ca`)

-  Patchtest-related changes:

-  :ref:`ref-classes-insane` / :ref:`ref-classes-sanity` classes-related changes:

   -  Add a check that :term:`SOURCE_MIRROR_URL` is defined when
      :ref:`ref-classes-own-mirrors` is used
      (:oecore_rev:`6d989b9d15266ca0b3650c93ac961cffc3e82c14`)

   -  Add a check for build host ``HOME`` directory in packaged files
      (:oecore_rev:`81bbe42edbed590389a1b0ed4b1a58a6738dc760`)

   -  Allow exceptions in ``buildpaths`` ``HOME`` checks
      (:oecore_rev:`ee29a9132a2566ba03f28c5042554a5faf9d233e`)

   -  :ref:`ref-classes-sanity`: Warn on `Cargo` config files outside of the
      build tree
      (:oecore_rev:`77516d3c4fe4291eb947c2f7c636a362c041c911`)

   -  Increase shebang size limit
      (:oecore_rev:`c205e77fc394b50ddf4f4e0bec29bb1c8eec44d6`)

   -  Improve ``HOME`` checks exceptions
      (:oecore_rev:`f6b9494eac4d4da3ba72303ca5de82d5f60e48f0`)

-  Security changes:

-  :ref:`ref-classes-sbom-cve-check`-related changes:

-  New :term:`PACKAGECONFIG` options for individual recipes:

   -  ``curl``: ``libpsl``, ``libssh``
   -  ``dhcpcd``: ``seccomp``
   -  ``ethtool``: ``pretty-dump``
   -  ``ffmpeg``: ``libdav1d``
   -  ``iproute2``: ``dcb``, ``dpll``
   -  ``libical``: ``introspection``, ``vala``
   -  ``libportal``: ``gtk4``
   -  ``librsvg``: ``avif``, ``gdkpixbuf``
   -  ``p11-kit``: ``systemd``, ``trust``
   -  ``pciutils``: ``dns``, ``shared``
   -  ``perf``: ``bpf-skel``, ``llvm``
   -  ``python3-requests``: ``chardet``, ``charset-normalizer``
   -  ``seatd``: ``manpages``
   -  ``strace``: ``libdw``
   -  ``vim``: ``desktop``
   -  ``webkitgtk``: ``bubblewrap``, ``librice``, ``webrtc``
   -  ``wpa-supplicant``: ``suiteb``, ``mbo``, ``wnm``

-  systemd-related changes:

   -  ``systemd-tools-native``: Add ``systemd-hwdb``
      (:oecore_rev:`2a7c2ddd739db59bc14d781aa919bdd4724b365e`)

   -  ``systemd``: Enable ``coredump`` in :term:`PACKAGECONFIG` by default
      (:oecore_rev:`1086a56d222fac8c602f086cc3d6467b34f4d61e`)

-  U-Boot-related changes:

   -  Add support for `Clang` toolchain
      (:oecore_rev:`321421117de088e6b23da9c746d16df0c6081323`)

   -  :ref:`ref-classes-uboot-sign`: List the TEE loadable behind U-Boot
      (:oecore_rev:`a9a20a0c95eba16e0dce0abe5e9a3998e4520386`)

-  Busybox-related changes:

   -  ``fdisk``: Enable GPT support
      (:oecore_rev:`d0914b892b9236ad18ba29d4807d5adbbae992e6`)

   -  Enable ``xxd``
      (:oecore_rev:`2852025109cb23577897461f4d2bdfd213602461`)

   -  Enable ``findfs`` for initramfs ``LABEL`` support
      (:oecore_rev:`c2b681e39421ae5b4fea917b3c3ed69bd1020b6c`)

-  ``initramfs-framework``-related changes:

   -  ``overlayroot``: Use ``switch_root`` instead of ``chroot``
      (:oecore_rev:`848c368291b59ce6e928cdc973f94b34a6cdaa12`)

   -  Add opt-in root-only ``udev`` trigger
      (:oecore_rev:`d3a196f5742425fb24cfc21bb380f3e96c49253c`)

   -  Support ``LABEL`` with root-only ``udev`` trigger
      (:oecore_rev:`fda394c2b91b3c66545f75dc13f7e8649e3cdb6d`)

   -  Avoid external commands in rootfs resolution
      (:oecore_rev:`d6e8e722763f87ceef9c0dbd1ec7f38088e7f0df`)

   -  Allow opting out of ``efivarfs`` mount
      (:oecore_rev:`9147744020f9b948f656a116ac03a9656e5a254b`)

-  Miscellaneous changes:

   -  ``pulseaudio``: Split pactl into a dedicated client subpackage
      (:oecore_rev:`31dd308b3fe68a6142738100e8ad18a09bccbce6`)

   -  ``u-boot-tools``: Add dependency on ``libyaml`` for ``dtschema`` validation
      (:oecore_rev:`02e09e036e5d037b29f2a53d63c2231535da1a5e`)

   -  ``p11-kit``: Rewrite packaging to provide additional ``p11-kit-modules``
      and ``p11-kit-remote`` packages
      (:oecore_rev:`7511a8624e79ab5c9fc242bcf3c6b74c828c8584`)

   -  ``harfbuzz``: Improve packaging
      (:oecore_rev:`fb39870f27b19af790244a20ae9887923df8e464`)

   -  :ref:`ref-classes-sstate`: Detect broken shared state paths containing
      :term:`TMPDIR`
      (:oecore_rev:`907af8fb448e2f9ecf8e0439f2d8c7c397fb873f`)

   -  ``gcr``: Package the ssh-agent into a separate package
      (:oecore_rev:`c9579094da0a6a178cab9f370ab7cb0e414d6ca9`)

   -  ``glibc``: Disable automatic ``libatomic`` linking
      (:oecore_rev:`677f0acc96072e65442158519589cea4294a88f9`)

   -  :ref:`ref-classes-uboot-sign`: Sign SPL FIT into a copy of the SPL DTB
      (:oecore_rev:`79584fe7e7efec4fc9153217d74b1d48774df911`)

   -  :ref:`ref-classes-archiver`: Properly remove artifacts when configuration changes
      (:oecore_rev:`4b0ac92f28caa8b7ae7d645afbeff1ebc34b36dc`)

   -  ``wget``: Disable NTLM support
      (:oecore_rev:`4eb7e98020eb5b87990f5fd5929adf7e333dc038`)

   -  ``vex``: Drop obsolete conflict check with ``cve-check`` class
      (:oecore_rev:`ddb00a3eaeb5fa7c84636aad9fb841f61cd99fa7`)

   -  :ref:`ref-classes-toaster`: Support layers that are not Git repositories
      (:oecore_rev:`3fb96cc91dc4e625502751b64ac982e5ba6f0cb8`)

   -  ``classes/meson``: Use ninja explicitly when compiling
      (:oecore_rev:`9e1513b29849c56fe372e0d73ce229cebed32d9e`)

   -  ``webkitgtk``: Allow using `Clang` to compile for arm target
      (:oecore_rev:`77023812cf13a96d67aef7ad9a743f66861bd759`)

   -  :ref:`ref-classes-report-error`: Add ``site.conf``/``toolcfg.conf`` into error report
      (:oecore_rev:`40ca5fc9098be25574130a3810560219b8ee9d07`)

   -  :ref:`ref-classes-cml1`: Use :term:`KCONFIG_CONFIG_ROOTDIR` in savedefconfig
      (:oecore_rev:`46964503b6340d4244d1c19e90808f399a3c862c`)

   -  :ref:`ref-classes-uki`: Make `initramfs` optional
      (:oecore_rev:`909d3ba1fa925c105681c091af00965fc4aac191`)

   -  ``cpan_build``: Disable ``.packlist`` and HTML doc
      (:oecore_rev:`3c2bb7bce1da68e31a60de044ee1dd9abb2eadc1`)

   -  :ref:`ref-classes-useradd`: Drop ``groupmems`` from :term:`sysroot` setup,
      as ``groupmems`` was `removed <https://github.com/shadow-maint/shadow/pull/1601>`__
      from `shadow` version 4.20.0
      (:oecore_rev:`de0bf5771644edbe317ee9ad89cca4bc8836cf12`)

   -  :ref:`ref-classes-image_types`: Make ``oe_mkext234fs`` reproducible
      (:oecore_rev:`8d1d548e36fa8426f20f43a514f0dd60b699f86b`)

   -  :ref:`ref-classes-pypi`: Only set ``downloadprefix`` in :term:`SRC_URI` if needed
      (:oecore_rev:`be391d8a8b7d4898063ceae1fbd707bfd2218be3`)

   -  ``sqlite3``: Squash ``sqlite3.inc`` into the recipe, since this is the only place
      where it is used.
      (:oecore_rev:`35db54823f0eebc8258f29f18bbd59b04dbca88d`)

Known Issues in |yocto-ver|
---------------------------

N/A

Recipe License changes in |yocto-ver|
-------------------------------------

..
   Going through commits on OE-Core filtered by License-Update:
   git log -U0 --patch --grep "License-Update:" yocto-6.0..origin/master

The following changes have been made to the :term:`LICENSE` values set by recipes:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe(s)
     - Previous value
     - New value
   * - ``erofs-utils``
     - ``GPL-2.0-or-later``
     - ``MIT AND (GPL-2.0-or-later OR MIT)``
   * - ``git``
     - ``GPL-2.0-only AND GPL-2.0-or-later AND BSD-3-Clause AND MIT AND BSL-1.0 AND LGPL-2.1-or-later``
     - ``GPL-2.0-only AND GPL-2.0-or-later AND BSD-3-Clause AND MIT AND LGPL-2.1-or-later``
   * - ``fmt``
     - ``MIT``
     - ``MIT-with-fmt-exception``
   * - ``vulkan-headers``
     - ``Apache-2.0 AND MIT``
     - ``Apache-2.0 AND MIT AND (Apache-2.0 OR MIT)``
   * - ``vulkan-validation-layers``
     - ``Apache-2.0 AND BSL-1.0 AND MIT``
     - ``Apache-2.0 AND BSL-1.0 AND MIT AND BSD-2-Clause AND (Apache-2.0 WITH LLVM-exception)``
   * - ``lttng-tools``
     - ``GPL-2.0-only AND LGPL-2.1-only``
     - ``BSD-2-Clause AND BSD-3-Clause AND BSL-1.0 AND CC0-1.0 AND CC-BY-SA-4.0 AND FSFAP AND GPL-2.0-only AND GPL-2.0-or-later AND GPL-2.0-or-later WITH Autoconf-exception-2.0 AND GPL-2.0-or-later WITH Autoconf-exception-macro AND LGPL-2.1-only AND LGPL-2.1-or-later AND MIT``

Security Fixes in |yocto-ver|
-----------------------------

..
   Generated with documentation/tools/gen-cve-release-notes
   With data from https://git.yoctoproject.org/yocto-metrics/plain/cve-check

The following CVEs have been fixed:

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Recipe
     - CVE IDs
   * - ``acl``
     - :cve_nist:`2026-54369`, :cve_nist:`2026-54370`
   * - ``alsa-lib``
     - :cve_nist:`2026-90781`, :cve_nist:`2026-96674`, :cve_nist:`2026-96675`
   * - ``attr``
     - :cve_nist:`2026-54371`
   * - ``avahi``
     - :cve_nist:`2025-59529`
   * - ``bind``
     - :cve_nist:`2026-19033`, :cve_nist:`2026-19662`, :cve_nist:`2026-19666`, :cve_nist:`2026-19667`, :cve_nist:`2026-19668`, :cve_nist:`2026-19941`, :cve_nist:`2026-75029`, :cve_nist:`2026-76163`, :cve_nist:`2026-77119`, :cve_nist:`2026-77692`, :cve_nist:`2026-78301`, :cve_nist:`2026-80274`, :cve_nist:`2026-81563`, :cve_nist:`2026-81736`
   * - ``binutils``
     - :cve_nist:`2026-6844`, :cve_nist:`2026-6845`
   * - ``binutils-cross-x86_64``
     - :cve_nist:`2026-6844`, :cve_nist:`2026-6845`
   * - ``binutils-testsuite``
     - :cve_nist:`2026-6844`, :cve_nist:`2026-6845`
   * - ``bison``
     - :cve_nist:`2026-56390`
   * - ``bluez5``
     - :cve_nist:`2026-19774`
   * - ``busybox``
     - :cve_nist:`2026-38752`, :cve_nist:`2026-38753`, :cve_nist:`2026-38755`
   * - ``cairo``
     - :cve_nist:`2025-50422`
   * - ``coreutils``
     - :cve_nist:`2026-56392`
   * - ``cryptodev-linux``
     - :cve_nist:`2026-28529`
   * - ``cups``
     - :cve_nist:`2024-47850`
   * - ``curl``
     - :cve_nist:`2026-8926`, :cve_nist:`2026-11564`, :cve_nist:`2026-11856`, :cve_nist:`2026-18924`, :cve_nist:`2026-19931`, :cve_nist:`2026-80230`, :cve_nist:`2026-80231`, :cve_nist:`2026-80255`, :cve_nist:`2026-82208`
   * - ``epiphany``
     - :cve_nist:`2026-18487`
   * - ``expat``
     - :cve_nist:`2026-66046`, :cve_nist:`2026-76641`, :cve_nist:`2026-76956`, :cve_nist:`2026-76957`, :cve_nist:`2026-93990`, :cve_nist:`2026-102633`
   * - ``ffmpeg``
     - :cve_nist:`2026-58049`, :cve_nist:`2026-66036`, :cve_nist:`2026-66040`, :cve_nist:`2026-66041`, :cve_nist:`2026-70628`, :cve_nist:`2026-70629`, :cve_nist:`2026-70630`, :cve_nist:`2026-70631`, :cve_nist:`2026-70632`, :cve_nist:`2026-75141`, :cve_nist:`2026-75142`, :cve_nist:`2026-75143`, :cve_nist:`2026-75144`, :cve_nist:`2026-75145`, :cve_nist:`2026-75146`, :cve_nist:`2026-75147`, :cve_nist:`2026-96611`
   * - ``gdk-pixbuf``
     - :cve_nist:`2026-5201`
   * - ``ghostscript``
     - :cve_nist:`2026-19547`, :cve_nist:`2026-39919`
   * - ``glibc``
     - :cve_nist:`2011-0536`, :cve_nist:`2025-0577`, :cve_nist:`2026-8674`, :cve_nist:`2026-18374`, :cve_nist:`2026-19499`, :cve_nist:`2026-19542`, :cve_nist:`2026-77117`, :cve_nist:`2026-80489`, :cve_nist:`2026-86805`, :cve_nist:`2026-89092`, :cve_nist:`2026-95818`, :cve_nist:`2026-97399`
   * - ``gnutls``
     - :cve_nist:`2023-0361`
   * - ``graphene``
     - :cve_nist:`2026-81281`
   * - ``gstreamer1.0``
     - :cve_nist:`2026-73433`, :cve_nist:`2026-73434`
   * - ``libarchive``
     - :cve_nist:`2026-4424`
   * - ``libgcrypt``
     - :cve_nist:`2024-2236`
   * - ``libgit2``
     - :cve_nist:`2026-5917`, :cve_nist:`2026-53583`, :cve_nist:`2026-53584`, :cve_nist:`2026-53585`, :cve_nist:`2026-53586`, :cve_nist:`2026-53587`
   * - ``libpcre2``
     - :cve_nist:`2026-86145`, :cve_nist:`2026-89156`, :cve_nist:`2026-89157`, :cve_nist:`2026-89158`, :cve_nist:`2026-89160`, :cve_nist:`2026-89161`, :cve_nist:`2026-89162`, :cve_nist:`2026-103111`
   * - ``libsndfile1``
     - :cve_nist:`2024-50613`, :cve_nist:`2025-52194`
   * - ``libsolv``
     - :cve_nist:`2026-48863`, :cve_nist:`2026-48864`
   * - ``libsoup``
     - :cve_nist:`2026-3099`, :cve_nist:`2026-3632`, :cve_nist:`2026-3633`, :cve_nist:`2026-3634`, :cve_nist:`2026-12548`, :cve_nist:`2026-12549`, :cve_nist:`2026-66337`, :cve_nist:`2026-66338`, :cve_nist:`2026-66339`
   * - ``libtheora``
     - :cve_nist:`2026-5673`
   * - ``libx11``
     - :cve_nist:`2026-88806`, :cve_nist:`2026-94283`, :cve_nist:`2026-94284`, :cve_nist:`2026-94285`
   * - ``libxfont``
     - :cve_nist:`2026-44950`
   * - ``libxi``
     - :cve_nist:`2026-93541`, :cve_nist:`2026-93542`, :cve_nist:`2026-93543`, :cve_nist:`2026-93544`, :cve_nist:`2026-93545`, :cve_nist:`2026-94281`, :cve_nist:`2026-94282`
   * - ``libxrender``
     - :cve_nist:`2026-88807`
   * - ``libxtst``
     - :cve_nist:`2026-94286`
   * - ``linux-yocto``
     - :cve_nist:`2019-14899`, :cve_nist:`2020-10774`, :cve_nist:`2021-3714`, :cve_nist:`2021-3864`, :cve_nist:`2022-0400`, :cve_nist:`2022-1247`, :cve_nist:`2022-4543`, :cve_nist:`2023-3397`, :cve_nist:`2023-3640`, :cve_nist:`2023-6238`, :cve_nist:`2023-6240`, :cve_nist:`2025-71074`, :cve_nist:`2025-71306`, :cve_nist:`2025-71308`, :cve_nist:`2025-71313`, :cve_nist:`2026-23328`, :cve_nist:`2026-23374`, :cve_nist:`2026-23377`, :cve_nist:`2026-31501`, :cve_nist:`2026-31771`, :cve_nist:`2026-31777`, :cve_nist:`2026-43022`, :cve_nist:`2026-43042`, :cve_nist:`2026-43045`, :cve_nist:`2026-43053`, :cve_nist:`2026-43095`, :cve_nist:`2026-43115`, :cve_nist:`2026-43131`, :cve_nist:`2026-43174`, :cve_nist:`2026-43191`, :cve_nist:`2026-43204`, :cve_nist:`2026-43228`, :cve_nist:`2026-43299`, :cve_nist:`2026-43308`, :cve_nist:`2026-43310`, :cve_nist:`2026-43311`, :cve_nist:`2026-43326`, :cve_nist:`2026-43414`, :cve_nist:`2026-43443`, :cve_nist:`2026-45961`, :cve_nist:`2026-45991`, :cve_nist:`2026-46008`, :cve_nist:`2026-46017`, :cve_nist:`2026-46032`, :cve_nist:`2026-46153`, :cve_nist:`2026-46210`, :cve_nist:`2026-46245`, :cve_nist:`2026-46298`, :cve_nist:`2026-46302`, :cve_nist:`2026-46311`, :cve_nist:`2026-46330`, :cve_nist:`2026-52949`, :cve_nist:`2026-52956`, :cve_nist:`2026-52960`, :cve_nist:`2026-52965`, :cve_nist:`2026-52988`, :cve_nist:`2026-53007`, :cve_nist:`2026-53008`, :cve_nist:`2026-53009`, :cve_nist:`2026-53017`, :cve_nist:`2026-53024`, :cve_nist:`2026-53025`, :cve_nist:`2026-53091`, :cve_nist:`2026-53106`, :cve_nist:`2026-53108`, :cve_nist:`2026-53124`, :cve_nist:`2026-53222`, :cve_nist:`2026-53257`, :cve_nist:`2026-53285`, :cve_nist:`2026-53292`, :cve_nist:`2026-53308`, :cve_nist:`2026-53401`, :cve_nist:`2026-63811`, :cve_nist:`2026-63879`, :cve_nist:`2026-63941`, :cve_nist:`2026-63977`, :cve_nist:`2026-63983`, :cve_nist:`2026-64013`, :cve_nist:`2026-64019`, :cve_nist:`2026-64020`, :cve_nist:`2026-64040`, :cve_nist:`2026-64057`, :cve_nist:`2026-64067`, :cve_nist:`2026-64079`, :cve_nist:`2026-64117`, :cve_nist:`2026-64123`, :cve_nist:`2026-64146`, :cve_nist:`2026-64154`, :cve_nist:`2026-64159`, :cve_nist:`2026-64160`, :cve_nist:`2026-64283`, :cve_nist:`2026-64325`, :cve_nist:`2026-64341`, :cve_nist:`2026-64388`, :cve_nist:`2026-64400`, :cve_nist:`2026-68086`, :cve_nist:`2026-68103`, :cve_nist:`2026-68105`, :cve_nist:`2026-68242`, :cve_nist:`2026-68286`, :cve_nist:`2026-68287`, :cve_nist:`2026-68288`, :cve_nist:`2026-68289`, :cve_nist:`2026-68291`, :cve_nist:`2026-68295`, :cve_nist:`2026-68303`, :cve_nist:`2026-68305`, :cve_nist:`2026-68312`, :cve_nist:`2026-68323`, :cve_nist:`2026-68337`, :cve_nist:`2026-68375`, :cve_nist:`2026-68399`, :cve_nist:`2026-68404`, :cve_nist:`2026-68436`, :cve_nist:`2026-68441`, :cve_nist:`2026-68447`, :cve_nist:`2026-68470`, :cve_nist:`2026-72031`, :cve_nist:`2026-72064`, :cve_nist:`2026-72091`, :cve_nist:`2026-72315`, :cve_nist:`2026-72329`, :cve_nist:`2026-72331`, :cve_nist:`2026-72334`, :cve_nist:`2026-72337`, :cve_nist:`2026-72345`, :cve_nist:`2026-72355`, :cve_nist:`2026-72370`, :cve_nist:`2026-72377`, :cve_nist:`2026-72380`, :cve_nist:`2026-72388`, :cve_nist:`2026-72397`, :cve_nist:`2026-72402`, :cve_nist:`2026-72404`, :cve_nist:`2026-72413`, :cve_nist:`2026-72423`, :cve_nist:`2026-72438`, :cve_nist:`2026-72440`, :cve_nist:`2026-72454`, :cve_nist:`2026-72463`, :cve_nist:`2026-72477`, :cve_nist:`2026-72485`, :cve_nist:`2026-72494`, :cve_nist:`2026-72496`, :cve_nist:`2026-72497`, :cve_nist:`2026-72499`, :cve_nist:`2026-74258`, :cve_nist:`2026-74260`, :cve_nist:`2026-74269`, :cve_nist:`2026-74272`, :cve_nist:`2026-74273`, :cve_nist:`2026-74277`, :cve_nist:`2026-74289`, :cve_nist:`2026-74291`, :cve_nist:`2026-74294`, :cve_nist:`2026-74307`, :cve_nist:`2026-74317`, :cve_nist:`2026-74336`, :cve_nist:`2026-74338`, :cve_nist:`2026-74342`, :cve_nist:`2026-74347`, :cve_nist:`2026-74350`, :cve_nist:`2026-74354`, :cve_nist:`2026-74367`, :cve_nist:`2026-74373`, :cve_nist:`2026-74374`, :cve_nist:`2026-74375`, :cve_nist:`2026-74407`, :cve_nist:`2026-74419`, :cve_nist:`2026-74449`, :cve_nist:`2026-74466`, :cve_nist:`2026-74496`, :cve_nist:`2026-74521`, :cve_nist:`2026-74527`, :cve_nist:`2026-74542`, :cve_nist:`2026-74544`, :cve_nist:`2026-74558`, :cve_nist:`2026-74561`, :cve_nist:`2026-74562`, :cve_nist:`2026-74568`, :cve_nist:`2026-74713`, :cve_nist:`2026-74715`, :cve_nist:`2026-74716`, :cve_nist:`2026-74721`, :cve_nist:`2026-74723`, :cve_nist:`2026-74729`, :cve_nist:`2026-74735`, :cve_nist:`2026-74745`, :cve_nist:`2026-74747`, :cve_nist:`2026-74750`, :cve_nist:`2026-74752`, :cve_nist:`2026-74754`, :cve_nist:`2026-80521`, :cve_nist:`2026-80524`, :cve_nist:`2026-80579`, :cve_nist:`2026-80580`, :cve_nist:`2026-80631`, :cve_nist:`2026-80634`, :cve_nist:`2026-80655`, :cve_nist:`2026-80657`, :cve_nist:`2026-80668`, :cve_nist:`2026-80693`, :cve_nist:`2026-80705`, :cve_nist:`2026-80729`, :cve_nist:`2026-80738`, :cve_nist:`2026-80747`, :cve_nist:`2026-80785`, :cve_nist:`2026-80786`, :cve_nist:`2026-80884`, :cve_nist:`2026-80899`, :cve_nist:`2026-89513`, :cve_nist:`2026-89527`, :cve_nist:`2026-89534`, :cve_nist:`2026-89537`, :cve_nist:`2026-89568`, :cve_nist:`2026-89584`, :cve_nist:`2026-89601`, :cve_nist:`2026-89610`, :cve_nist:`2026-89611`, :cve_nist:`2026-89642`, :cve_nist:`2026-89654`, :cve_nist:`2026-89705`, :cve_nist:`2026-89709`, :cve_nist:`2026-89718`, :cve_nist:`2026-89755`, :cve_nist:`2026-89764`, :cve_nist:`2026-89772`, :cve_nist:`2026-89792`, :cve_nist:`2026-89796`, :cve_nist:`2026-89805`, :cve_nist:`2026-89812`, :cve_nist:`2026-89813`, :cve_nist:`2026-89815`, :cve_nist:`2026-89827`, :cve_nist:`2026-89838`, :cve_nist:`2026-89840`, :cve_nist:`2026-89859`, :cve_nist:`2026-89862`, :cve_nist:`2026-89866`, :cve_nist:`2026-89868`, :cve_nist:`2026-89907`, :cve_nist:`2026-90040`, :cve_nist:`2026-90061`, :cve_nist:`2026-90079`, :cve_nist:`2026-90082`, :cve_nist:`2026-90093`, :cve_nist:`2026-90095`, :cve_nist:`2026-90098`, :cve_nist:`2026-90099`, :cve_nist:`2026-90104`, :cve_nist:`2026-90105`, :cve_nist:`2026-90106`, :cve_nist:`2026-90111`, :cve_nist:`2026-90121`, :cve_nist:`2026-90142`, :cve_nist:`2026-90144`, :cve_nist:`2026-90154`, :cve_nist:`2026-90155`, :cve_nist:`2026-90156`, :cve_nist:`2026-90162`, :cve_nist:`2026-90167`, :cve_nist:`2026-90174`, :cve_nist:`2026-90177`, :cve_nist:`2026-90182`, :cve_nist:`2026-90183`, :cve_nist:`2026-90206`, :cve_nist:`2026-90208`, :cve_nist:`2026-90211`, :cve_nist:`2026-90217`, :cve_nist:`2026-90234`, :cve_nist:`2026-90237`, :cve_nist:`2026-90242`, :cve_nist:`2026-90263`, :cve_nist:`2026-90267`, :cve_nist:`2026-90273`, :cve_nist:`2026-90301`, :cve_nist:`2026-90311`, :cve_nist:`2026-90315`, :cve_nist:`2026-90317`, :cve_nist:`2026-90320`, :cve_nist:`2026-90321`, :cve_nist:`2026-90323`, :cve_nist:`2026-90330`, :cve_nist:`2026-90335`, :cve_nist:`2026-90336`, :cve_nist:`2026-90337`, :cve_nist:`2026-90345`, :cve_nist:`2026-90346`, :cve_nist:`2026-90347`, :cve_nist:`2026-90355`, :cve_nist:`2026-90359`, :cve_nist:`2026-90365`, :cve_nist:`2026-90380`, :cve_nist:`2026-90381`, :cve_nist:`2026-90408`, :cve_nist:`2026-90409`, :cve_nist:`2026-90412`, :cve_nist:`2026-90417`, :cve_nist:`2026-90421`, :cve_nist:`2026-90427`, :cve_nist:`2026-92480`, :cve_nist:`2026-92482`, :cve_nist:`2026-93044`, :cve_nist:`2026-93047`, :cve_nist:`2026-93096`, :cve_nist:`2026-93099`, :cve_nist:`2026-93104`, :cve_nist:`2026-93105`, :cve_nist:`2026-93116`, :cve_nist:`2026-93122`, :cve_nist:`2026-93125`, :cve_nist:`2026-93129`, :cve_nist:`2026-93135`, :cve_nist:`2026-93144`, :cve_nist:`2026-93148`, :cve_nist:`2026-93164`, :cve_nist:`2026-93168`, :cve_nist:`2026-93175`, :cve_nist:`2026-93176`, :cve_nist:`2026-93179`, :cve_nist:`2026-93181`, :cve_nist:`2026-93201`, :cve_nist:`2026-93216`, :cve_nist:`2026-93221`, :cve_nist:`2026-93248`, :cve_nist:`2026-93251`, :cve_nist:`2026-93260`, :cve_nist:`2026-93270`, :cve_nist:`2026-93282`, :cve_nist:`2026-93284`, :cve_nist:`2026-93285`, :cve_nist:`2026-93286`, :cve_nist:`2026-93287`, :cve_nist:`2026-93288`, :cve_nist:`2026-93781`, :cve_nist:`2026-93782`, :cve_nist:`2026-93783`, :cve_nist:`2026-93784`, :cve_nist:`2026-93785`, :cve_nist:`2026-93786`, :cve_nist:`2026-93787`, :cve_nist:`2026-93788`, :cve_nist:`2026-93789`, :cve_nist:`2026-93790`, :cve_nist:`2026-93791`, :cve_nist:`2026-93792`, :cve_nist:`2026-93793`, :cve_nist:`2026-93794`, :cve_nist:`2026-93795`, :cve_nist:`2026-93796`, :cve_nist:`2026-93797`, :cve_nist:`2026-93798`, :cve_nist:`2026-93799`, :cve_nist:`2026-93800`, :cve_nist:`2026-93801`, :cve_nist:`2026-93802`, :cve_nist:`2026-93803`, :cve_nist:`2026-93804`, :cve_nist:`2026-93805`, :cve_nist:`2026-93806`, :cve_nist:`2026-93807`, :cve_nist:`2026-93808`, :cve_nist:`2026-93810`, :cve_nist:`2026-93811`, :cve_nist:`2026-93812`, :cve_nist:`2026-93813`, :cve_nist:`2026-93814`, :cve_nist:`2026-93815`, :cve_nist:`2026-93816`, :cve_nist:`2026-93817`, :cve_nist:`2026-93818`, :cve_nist:`2026-93819`, :cve_nist:`2026-93820`, :cve_nist:`2026-93821`, :cve_nist:`2026-93822`, :cve_nist:`2026-93823`, :cve_nist:`2026-93824`, :cve_nist:`2026-93825`, :cve_nist:`2026-93826`, :cve_nist:`2026-93827`, :cve_nist:`2026-93828`, :cve_nist:`2026-93829`, :cve_nist:`2026-93830`, :cve_nist:`2026-97407`, :cve_nist:`2026-97408`, :cve_nist:`2026-97409`, :cve_nist:`2026-97410`, :cve_nist:`2026-97411`, :cve_nist:`2026-97412`, :cve_nist:`2026-97413`, :cve_nist:`2026-97414`, :cve_nist:`2026-97415`, :cve_nist:`2026-97416`, :cve_nist:`2026-97417`, :cve_nist:`2026-97418`, :cve_nist:`2026-97419`, :cve_nist:`2026-97420`, :cve_nist:`2026-97421`, :cve_nist:`2026-97422`, :cve_nist:`2026-97423`, :cve_nist:`2026-97424`, :cve_nist:`2026-97425`, :cve_nist:`2026-97426`, :cve_nist:`2026-97427`, :cve_nist:`2026-97428`, :cve_nist:`2026-97429`, :cve_nist:`2026-97430`, :cve_nist:`2026-97431`, :cve_nist:`2026-97432`, :cve_nist:`2026-97433`, :cve_nist:`2026-97434`, :cve_nist:`2026-97435`, :cve_nist:`2026-97436`, :cve_nist:`2026-97437`, :cve_nist:`2026-97438`, :cve_nist:`2026-97439`, :cve_nist:`2026-97440`, :cve_nist:`2026-97441`, :cve_nist:`2026-97442`, :cve_nist:`2026-97443`, :cve_nist:`2026-97444`, :cve_nist:`2026-97445`, :cve_nist:`2026-97446`, :cve_nist:`2026-97447`, :cve_nist:`2026-97448`, :cve_nist:`2026-97449`, :cve_nist:`2026-97450`, :cve_nist:`2026-97451`, :cve_nist:`2026-97452`, :cve_nist:`2026-97453`, :cve_nist:`2026-97454`, :cve_nist:`2026-97455`, :cve_nist:`2026-97456`, :cve_nist:`2026-97472`, :cve_nist:`2026-97473`, :cve_nist:`2026-97474`, :cve_nist:`2026-97475`, :cve_nist:`2026-97476`, :cve_nist:`2026-97477`, :cve_nist:`2026-97478`, :cve_nist:`2026-97479`, :cve_nist:`2026-97480`, :cve_nist:`2026-97481`, :cve_nist:`2026-97482`, :cve_nist:`2026-97483`, :cve_nist:`2026-97484`, :cve_nist:`2026-97485`, :cve_nist:`2026-97486`, :cve_nist:`2026-97487`, :cve_nist:`2026-97488`, :cve_nist:`2026-97489`, :cve_nist:`2026-97490`, :cve_nist:`2026-97491`, :cve_nist:`2026-97492`, :cve_nist:`2026-97493`, :cve_nist:`2026-97494`, :cve_nist:`2026-97495`, :cve_nist:`2026-97496`, :cve_nist:`2026-97497`, :cve_nist:`2026-97498`, :cve_nist:`2026-97499`, :cve_nist:`2026-97500`, :cve_nist:`2026-97501`, :cve_nist:`2026-97502`, :cve_nist:`2026-97503`, :cve_nist:`2026-97504`, :cve_nist:`2026-97505`, :cve_nist:`2026-97506`, :cve_nist:`2026-97507`, :cve_nist:`2026-97508`, :cve_nist:`2026-97509`, :cve_nist:`2026-97510`, :cve_nist:`2026-97511`, :cve_nist:`2026-97512`, :cve_nist:`2026-97513`, :cve_nist:`2026-97514`, :cve_nist:`2026-97515`, :cve_nist:`2026-97516`, :cve_nist:`2026-97517`, :cve_nist:`2026-97518`, :cve_nist:`2026-97519`, :cve_nist:`2026-97520`, :cve_nist:`2026-97521`, :cve_nist:`2026-97522`, :cve_nist:`2026-97523`, :cve_nist:`2026-97524`, :cve_nist:`2026-97525`, :cve_nist:`2026-97526`, :cve_nist:`2026-97527`, :cve_nist:`2026-97528`, :cve_nist:`2026-97529`, :cve_nist:`2026-97530`, :cve_nist:`2026-97531`, :cve_nist:`2026-97532`, :cve_nist:`2026-97533`, :cve_nist:`2026-97534`, :cve_nist:`2026-97535`, :cve_nist:`2026-97536`, :cve_nist:`2026-97537`, :cve_nist:`2026-97538`, :cve_nist:`2026-97539`, :cve_nist:`2026-97540`, :cve_nist:`2026-97541`, :cve_nist:`2026-97542`, :cve_nist:`2026-97543`, :cve_nist:`2026-97544`, :cve_nist:`2026-97545`, :cve_nist:`2026-97546`, :cve_nist:`2026-97547`, :cve_nist:`2026-97548`, :cve_nist:`2026-97549`, :cve_nist:`2026-97550`, :cve_nist:`2026-97551`, :cve_nist:`2026-97552`, :cve_nist:`2026-97555`, :cve_nist:`2026-97556`, :cve_nist:`2026-97557`, :cve_nist:`2026-97558`, :cve_nist:`2026-97559`, :cve_nist:`2026-97560`, :cve_nist:`2026-97561`, :cve_nist:`2026-97562`, :cve_nist:`2026-97563`, :cve_nist:`2026-97564`, :cve_nist:`2026-97565`, :cve_nist:`2026-97566`, :cve_nist:`2026-97567`, :cve_nist:`2026-97568`, :cve_nist:`2026-97571`, :cve_nist:`2026-97572`, :cve_nist:`2026-97573`, :cve_nist:`2026-97574`, :cve_nist:`2026-97575`, :cve_nist:`2026-97576`, :cve_nist:`2026-97577`, :cve_nist:`2026-97578`, :cve_nist:`2026-97579`, :cve_nist:`2026-97581`, :cve_nist:`2026-97582`, :cve_nist:`2026-97583`, :cve_nist:`2026-97584`, :cve_nist:`2026-97585`, :cve_nist:`2026-97586`, :cve_nist:`2026-97587`, :cve_nist:`2026-97588`, :cve_nist:`2026-97589`, :cve_nist:`2026-97590`, :cve_nist:`2026-97591`, :cve_nist:`2026-97592`, :cve_nist:`2026-97593`, :cve_nist:`2026-97594`, :cve_nist:`2026-97595`, :cve_nist:`2026-97596`, :cve_nist:`2026-97597`, :cve_nist:`2026-97598`, :cve_nist:`2026-97599`, :cve_nist:`2026-97600`, :cve_nist:`2026-97601`, :cve_nist:`2026-97602`, :cve_nist:`2026-97603`, :cve_nist:`2026-97604`, :cve_nist:`2026-97605`, :cve_nist:`2026-97606`, :cve_nist:`2026-97607`, :cve_nist:`2026-97608`, :cve_nist:`2026-97609`, :cve_nist:`2026-97610`, :cve_nist:`2026-97611`, :cve_nist:`2026-97612`, :cve_nist:`2026-97613`, :cve_nist:`2026-97614`, :cve_nist:`2026-97615`, :cve_nist:`2026-97616`, :cve_nist:`2026-97617`, :cve_nist:`2026-97618`, :cve_nist:`2026-97619`, :cve_nist:`2026-97620`, :cve_nist:`2026-97621`, :cve_nist:`2026-97899`, :cve_nist:`2026-97900`, :cve_nist:`2026-97901`, :cve_nist:`2026-97902`, :cve_nist:`2026-97903`, :cve_nist:`2026-97904`, :cve_nist:`2026-97905`, :cve_nist:`2026-97906`, :cve_nist:`2026-97907`, :cve_nist:`2026-97908`, :cve_nist:`2026-97909`, :cve_nist:`2026-97910`, :cve_nist:`2026-97915`, :cve_nist:`2026-97916`, :cve_nist:`2026-97917`, :cve_nist:`2026-97918`, :cve_nist:`2026-97919`, :cve_nist:`2026-97920`, :cve_nist:`2026-97921`, :cve_nist:`2026-97922`, :cve_nist:`2026-97923`, :cve_nist:`2026-97924`, :cve_nist:`2026-97925`, :cve_nist:`2026-97926`, :cve_nist:`2026-97927`, :cve_nist:`2026-97928`, :cve_nist:`2026-97929`, :cve_nist:`2026-97930`, :cve_nist:`2026-97931`, :cve_nist:`2026-97934`, :cve_nist:`2026-97935`, :cve_nist:`2026-97936`, :cve_nist:`2026-97938`, :cve_nist:`2026-97939`, :cve_nist:`2026-97940`, :cve_nist:`2026-97942`, :cve_nist:`2026-97943`, :cve_nist:`2026-97944`, :cve_nist:`2026-97945`, :cve_nist:`2026-97946`, :cve_nist:`2026-97947`, :cve_nist:`2026-97948`, :cve_nist:`2026-97949`, :cve_nist:`2026-97950`, :cve_nist:`2026-97951`, :cve_nist:`2026-97952`, :cve_nist:`2026-97953`, :cve_nist:`2026-97954`, :cve_nist:`2026-97955`, :cve_nist:`2026-97956`, :cve_nist:`2026-97957`, :cve_nist:`2026-97958`, :cve_nist:`2026-97959`, :cve_nist:`2026-97960`, :cve_nist:`2026-97961`, :cve_nist:`2026-97962`, :cve_nist:`2026-97963`, :cve_nist:`2026-97964`, :cve_nist:`2026-97965`, :cve_nist:`2026-97966`, :cve_nist:`2026-97967`, :cve_nist:`2026-97968`, :cve_nist:`2026-97969`, :cve_nist:`2026-97970`, :cve_nist:`2026-97972`, :cve_nist:`2026-97973`, :cve_nist:`2026-97974`, :cve_nist:`2026-97975`, :cve_nist:`2026-97976`, :cve_nist:`2026-97977`, :cve_nist:`2026-97978`, :cve_nist:`2026-97979`, :cve_nist:`2026-97981`, :cve_nist:`2026-97982`, :cve_nist:`2026-97984`, :cve_nist:`2026-97985`, :cve_nist:`2026-97986`, :cve_nist:`2026-97987`, :cve_nist:`2026-97988`, :cve_nist:`2026-97989`, :cve_nist:`2026-97990`, :cve_nist:`2026-97991`, :cve_nist:`2026-97992`, :cve_nist:`2026-97993`, :cve_nist:`2026-97994`, :cve_nist:`2026-97995`, :cve_nist:`2026-97996`, :cve_nist:`2026-97997`, :cve_nist:`2026-97998`, :cve_nist:`2026-98000`, :cve_nist:`2026-98001`, :cve_nist:`2026-98003`, :cve_nist:`2026-98004`, :cve_nist:`2026-98006`, :cve_nist:`2026-98007`, :cve_nist:`2026-98008`, :cve_nist:`2026-98009`, :cve_nist:`2026-98010`, :cve_nist:`2026-98011`, :cve_nist:`2026-98012`, :cve_nist:`2026-98013`, :cve_nist:`2026-98014`, :cve_nist:`2026-98015`, :cve_nist:`2026-98016`, :cve_nist:`2026-98017`, :cve_nist:`2026-98018`, :cve_nist:`2026-98019`, :cve_nist:`2026-98020`, :cve_nist:`2026-98021`, :cve_nist:`2026-98022`, :cve_nist:`2026-98023`, :cve_nist:`2026-98024`, :cve_nist:`2026-98025`, :cve_nist:`2026-98026`, :cve_nist:`2026-98027`, :cve_nist:`2026-98028`, :cve_nist:`2026-98029`, :cve_nist:`2026-98030`, :cve_nist:`2026-98031`, :cve_nist:`2026-98032`, :cve_nist:`2026-98033`, :cve_nist:`2026-98037`, :cve_nist:`2026-98038`, :cve_nist:`2026-98039`, :cve_nist:`2026-98041`, :cve_nist:`2026-98043`, :cve_nist:`2026-98044`, :cve_nist:`2026-98045`, :cve_nist:`2026-98046`, :cve_nist:`2026-98047`, :cve_nist:`2026-98048`, :cve_nist:`2026-98049`, :cve_nist:`2026-98050`, :cve_nist:`2026-98051`, :cve_nist:`2026-98052`, :cve_nist:`2026-98053`, :cve_nist:`2026-98054`, :cve_nist:`2026-98055`, :cve_nist:`2026-98056`, :cve_nist:`2026-98058`, :cve_nist:`2026-98059`, :cve_nist:`2026-98060`, :cve_nist:`2026-98061`, :cve_nist:`2026-98062`, :cve_nist:`2026-98063`, :cve_nist:`2026-98064`, :cve_nist:`2026-98066`, :cve_nist:`2026-98067`, :cve_nist:`2026-98068`, :cve_nist:`2026-98069`, :cve_nist:`2026-98070`, :cve_nist:`2026-98071`, :cve_nist:`2026-98072`, :cve_nist:`2026-98073`, :cve_nist:`2026-98074`, :cve_nist:`2026-98075`, :cve_nist:`2026-98076`, :cve_nist:`2026-98077`, :cve_nist:`2026-98078`, :cve_nist:`2026-98079`, :cve_nist:`2026-98080`, :cve_nist:`2026-98081`, :cve_nist:`2026-98082`, :cve_nist:`2026-98083`, :cve_nist:`2026-98085`, :cve_nist:`2026-98086`, :cve_nist:`2026-98087`, :cve_nist:`2026-98088`, :cve_nist:`2026-98089`, :cve_nist:`2026-98090`, :cve_nist:`2026-98091`, :cve_nist:`2026-98092`, :cve_nist:`2026-98093`, :cve_nist:`2026-98094`, :cve_nist:`2026-98095`, :cve_nist:`2026-98096`, :cve_nist:`2026-98097`, :cve_nist:`2026-98098`, :cve_nist:`2026-98099`, :cve_nist:`2026-98101`, :cve_nist:`2026-98102`, :cve_nist:`2026-98103`, :cve_nist:`2026-98104`, :cve_nist:`2026-98105`, :cve_nist:`2026-98106`, :cve_nist:`2026-98107`, :cve_nist:`2026-98108`, :cve_nist:`2026-98109`, :cve_nist:`2026-98110`, :cve_nist:`2026-98111`, :cve_nist:`2026-98113`, :cve_nist:`2026-98114`, :cve_nist:`2026-98116`, :cve_nist:`2026-98117`, :cve_nist:`2026-98118`, :cve_nist:`2026-98119`, :cve_nist:`2026-98120`, :cve_nist:`2026-98121`, :cve_nist:`2026-98122`, :cve_nist:`2026-98123`, :cve_nist:`2026-98124`, :cve_nist:`2026-98126`, :cve_nist:`2026-98127`, :cve_nist:`2026-98128`, :cve_nist:`2026-98129`, :cve_nist:`2026-98130`, :cve_nist:`2026-98131`, :cve_nist:`2026-98134`, :cve_nist:`2026-98142`, :cve_nist:`2026-98146`, :cve_nist:`2026-98147`, :cve_nist:`2026-98148`, :cve_nist:`2026-98151`, :cve_nist:`2026-98152`, :cve_nist:`2026-98153`, :cve_nist:`2026-98154`, :cve_nist:`2026-98155`, :cve_nist:`2026-98156`, :cve_nist:`2026-98157`, :cve_nist:`2026-98158`, :cve_nist:`2026-98159`, :cve_nist:`2026-100072`
   * - ``nfs-utils``
     - :cve_nist:`2025-12801`
   * - ``openssh``
     - :cve_nist:`2026-55654`, :cve_nist:`2026-55655`, :cve_nist:`2026-73281`, :cve_nist:`2026-73282`, :cve_nist:`2026-73283`
   * - ``openssl``
     - :cve_nist:`2015-3216`, :cve_nist:`2026-35189`, :cve_nist:`2026-35191`, :cve_nist:`2026-42772`, :cve_nist:`2026-54872`, :cve_nist:`2026-54873`, :cve_nist:`2026-54875`, :cve_nist:`2026-72897`, :cve_nist:`2026-75804`, :cve_nist:`2026-75805`, :cve_nist:`2026-75806`, :cve_nist:`2026-77696`, :cve_nist:`2026-84782`, :cve_nist:`2026-84784`
   * - ``ovmf``
     - :cve_nist:`2017-5731`, :cve_nist:`2019-14584`
   * - ``perl``
     - :cve_nist:`2026-15534`, :cve_nist:`2026-82560`
   * - ``popt``
     - :cve_nist:`2026-18739`, :cve_nist:`2026-18743`, :cve_nist:`2026-18839`
   * - ``ppp``
     - :cve_nist:`2020-15704`
   * - ``pulseaudio``
     - :cve_nist:`2020-15710`, :cve_nist:`2020-16123`
   * - ``python3``
     - :cve_nist:`2025-15367`, :cve_nist:`2026-12345`, :cve_nist:`2026-15310`, :cve_nist:`2026-15806`, :cve_nist:`2026-17084`, :cve_nist:`2026-19445`, :cve_nist:`2026-19553`, :cve_nist:`2026-19672`, :cve_nist:`2026-87910`
   * - ``python3-cryptography``
     - :cve_nist:`2026-69247`
   * - ``python3-git``
     - :cve_nist:`2026-67322`, :cve_nist:`2026-67323`, :cve_nist:`2026-67324`, :cve_nist:`2026-67325`, :cve_nist:`2026-67326`, :cve_nist:`2026-69097`, :cve_nist:`2026-73619`, :cve_nist:`2026-73620`, :cve_nist:`2026-73621`, :cve_nist:`2026-73622`, :cve_nist:`2026-73623`, :cve_nist:`2026-73624`, :cve_nist:`2026-73625`, :cve_nist:`2026-76217`, :cve_nist:`2026-76218`, :cve_nist:`2026-76219`, :cve_nist:`2026-76220`, :cve_nist:`2026-76221`, :cve_nist:`2026-76222`, :cve_nist:`2026-78675`, :cve_nist:`2026-78676`, :cve_nist:`2026-78677`, :cve_nist:`2026-78678`, :cve_nist:`2026-78679`, :cve_nist:`2026-87817`, :cve_nist:`2026-87818`, :cve_nist:`2026-87819`, :cve_nist:`2026-100689`
   * - ``python3-lxml``
     - :cve_nist:`2026-49825`
   * - ``python3-mako``
     - :cve_nist:`2026-102991`
   * - ``python3-urllib3``
     - :cve_nist:`2026-44432`, :cve_nist:`2026-97687`, :cve_nist:`2026-97688`, :cve_nist:`2026-97689`
   * - ``qemu``
     - :cve_nist:`2026-3195`, :cve_nist:`2026-3196`, :cve_nist:`2026-3842`
   * - ``qemu-system-native``
     - :cve_nist:`2026-3195`, :cve_nist:`2026-3196`, :cve_nist:`2026-3842`
   * - ``rpm-sequoia``
     - :cve_nist:`2026-2625`
   * - ``rsync``
     - :cve_nist:`2026-29518`, :cve_nist:`2026-41035`, :cve_nist:`2026-43617`, :cve_nist:`2026-43618`, :cve_nist:`2026-43619`, :cve_nist:`2026-43620`, :cve_nist:`2026-45232`, :cve_nist:`2026-53783`, :cve_nist:`2026-53784`, :cve_nist:`2026-53785`, :cve_nist:`2026-53786`, :cve_nist:`2026-53788`, :cve_nist:`2026-53789`, :cve_nist:`2026-53790`, :cve_nist:`2026-53791`, :cve_nist:`2026-53792`, :cve_nist:`2026-53793`, :cve_nist:`2026-53794`, :cve_nist:`2026-53795`, :cve_nist:`2026-53796`, :cve_nist:`2026-53797`, :cve_nist:`2026-53798`, :cve_nist:`2026-53799`, :cve_nist:`2026-53800`, :cve_nist:`2026-53801`, :cve_nist:`2026-53802`, :cve_nist:`2026-53803`, :cve_nist:`2026-70452`, :cve_nist:`2026-70453`, :cve_nist:`2026-70454`, :cve_nist:`2026-70456`, :cve_nist:`2026-70457`, :cve_nist:`2026-70458`, :cve_nist:`2026-70459`, :cve_nist:`2026-70460`, :cve_nist:`2026-70461`, :cve_nist:`2026-70462`, :cve_nist:`2026-70463`, :cve_nist:`2026-70464`
   * - ``sudo``
     - :cve_nist:`2026-82474`
   * - ``tar``
     - :cve_nist:`2026-18477`, :cve_nist:`2026-18508`
   * - ``u-boot-tools``
     - :cve_nist:`2026-71971`, :cve_nist:`2026-71972`, :cve_nist:`2026-71973`, :cve_nist:`2026-71974`, :cve_nist:`2026-74220`, :cve_nist:`2026-74221`, :cve_nist:`2026-74222`, :cve_nist:`2026-74225`
   * - ``util-linux``
     - :cve_nist:`2026-76642`
   * - ``util-linux-libuuid``
     - :cve_nist:`2026-76642`
   * - ``vim``
     - :cve_nist:`2026-51400`, :cve_nist:`2026-51401`, :cve_nist:`2026-73070`
   * - ``webkitgtk``
     - :cve_nist:`2026-78376`, :cve_nist:`2026-83596`
   * - ``xserver-xorg``
     - :cve_nist:`2026-55999`, :cve_nist:`2026-56000`
   * - ``zlib``
     - :cve_nist:`2026-85091`

Recipe Upgrades in |yocto-ver|
------------------------------

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
   * - ``acl``
     - 2.3.2
     - 2.4.0
   * - ``acpica``
     - 20251212
     - 20260408
   * - ``adwaita-icon-theme``
     - 49.0
     - 50.0
   * - ``alsa-lib``
     - 1.2.15.3
     - 1.2.16.1
   * - ``alsa-ucm-conf``
     - 1.2.15.3
     - 1.2.16.1
   * - ``alsa-utils``
     - 1.2.15.2
     - 1.2.16
   * - ``appstream``
     - 1.1.2
     - 1.2.0
   * - ``apr-util``
     - 1.6.3
     - 1.6.5
   * - ``at-spi2-core``
     - 2.60.0
     - 2.62.0.1
   * - ``attr``
     - 2.5.2
     - 2.6.0
   * - ``avahi``
     - 0.8
     - 0.9~rc5
   * - ``barebox``
     - 2026.04.0
     - 2026.09.0
   * - ``barebox-tools``
     - 2026.04.0
     - 2026.08.0
   * - ``bash-completion``
     - 2.17.0
     - 2.18.0
   * - ``bind``
     - 9.20.26
     - 9.20.29
   * - ``binutils``
     - 2.46.1
     - 2.47
   * - ``binutils-cross``
     - 2.46.1
     - 2.47
   * - ``binutils-cross-canadian``
     - 2.46.1
     - 2.47
   * - ``binutils-crosssdk``
     - 2.46.1
     - 2.47
   * - ``binutils-testsuite``
     - 2.46.1
     - 2.47
   * - ``bluez5``
     - 5.86
     - 5.87
   * - ``boost``
     - 1.90.0
     - 1.92.0
   * - ``boost-build-native``
     - 1.90.0
     - 1.92.0
   * - ``btrfs-tools``
     - 6.19.1
     - 7.1
   * - ``busybox``
     - 1.37.0
     - 1.38.0
   * - ``busybox-inittab``
     - 1.37.0
     - 1.38.0
   * - ``ca-certificates``
     - 20260601
     - 20260816
   * - ``cairo``
     - 1.18.4
     - 1.18.6
   * - ``cargo``
     - 1.94.1
     - 1.98.1
   * - ``cargo-c``
     - 0.10.21+cargo-0.95.0
     - 0.10.25+cargo-0.99.0
   * - ``ccache``
     - 4.13.3
     - 4.14
   * - ``clang``
     - 22.1.8
     - 23.1.0
   * - ``cmake``
     - 4.3.1
     - 4.4.3
   * - ``cmake-native``
     - 4.3.1
     - 4.4.3
   * - ``compiler-rt``
     - 22.1.8
     - 23.1.0
   * - ``compiler-rt-sanitizers``
     - 22.1.8
     - 23.1.0
   * - ``coreutils``
     - 9.10
     - 9.11
   * - ``createrepo-c``
     - 1.2.3
     - 1.2.4
   * - ``cross-localedef-native``
     - 2.43+git
     - 2.44+git
   * - ``cryptodev-linux``
     - 1.14
     - 1.14+git
   * - ``cryptodev-module``
     - 1.14
     - 1.14+git
   * - ``cryptodev-tests``
     - 1.14
     - 1.14+git
   * - ``cups``
     - 2.4.16
     - 2.4.19
   * - ``curl``
     - 8.19.0
     - 8.22.0
   * - ``debianutils``
     - 5.23.2
     - 5.24
   * - ``debugedit``
     - 5.2
     - 5.3
   * - ``dhcpcd``
     - 10.3.1
     - 10.5.2
   * - ``diffoscope``
     - 314
     - 329
   * - ``diffstat``
     - 1.68
     - 1.69
   * - ``dos2unix``
     - 7.5.4
     - 7.5.7
   * - ``dropbear``
     - 2025.89
     - 2026.94
   * - ``dtc``
     - 1.7.2
     - 1.8.1
   * - ``ed``
     - 1.22.5
     - 1.22.6
   * - ``elfutils``
     - 0.194
     - 0.196
   * - ``enchant2``
     - 2.8.15
     - 2.8.21
   * - ``epiphany``
     - 49.7
     - 50.6
   * - ``erofs-utils``
     - 1.9.1
     - 1.9.4
   * - ``ethtool``
     - 6.19
     - 7.1
   * - ``expat``
     - 2.7.5
     - 2.8.4
   * - ``fastfloat``
     - 8.2.4
     - 8.2.10
   * - ``ffmpeg``
     - 8.0.3
     - 9.0.2
   * - ``file``
     - 5.47
     - 5.48
   * - ``findutils``
     - 4.10.0
     - 4.11.0
   * - ``fmt``
     - 12.1.0
     - 12.2.0
   * - ``font-util``
     - 1.4.1
     - 1.4.2
   * - ``fontconfig``
     - 2.17.1
     - 2.18.3
   * - ``gawk``
     - 5.4.0
     - 5.4.1
   * - ``gcc``
     - 15.3.0
     - 16.2.0
   * - ``gcc-cross``
     - 15.3.0
     - 16.2.0
   * - ``gcc-cross-canadian``
     - 15.3.0
     - 16.2.0
   * - ``gcc-crosssdk``
     - 15.3.0
     - 16.2.0
   * - ``gcc-runtime``
     - 15.3.0
     - 16.2.0
   * - ``gcc-sanitizers``
     - 15.3.0
     - 16.2.0
   * - ``gcc-source``
     - 15.3.0
     - 16.2.0
   * - ``gdk-pixbuf``
     - 2.44.5
     - 2.44.8
   * - ``ghostscript``
     - 10.06.0
     - 10.07.1
   * - ``git``
     - 2.53.0
     - 2.55.0
   * - ``glib-2.0``
     - 2.88.2
     - 2.88.3
   * - ``glib-2.0-initial``
     - 2.88.2
     - 2.88.3
   * - ``glibc``
     - 2.43+git
     - 2.44+git
   * - ``glibc-locale``
     - 2.43+git
     - 2.44+git
   * - ``glibc-mtrace``
     - 2.43+git
     - 2.44+git
   * - ``glibc-scripts``
     - 2.43+git
     - 2.44+git
   * - ``glibc-testsuite``
     - 2.43+git
     - 2.44+git
   * - ``glslang``
     - 1.4.341.0
     - 1.4.357.0
   * - ``gn``
     - 0+git (9d19a7870add…)
     - 0+git (17b0057970fa…)
   * - ``gnu-config``
     - 20250709+git
     - 20260629+git
   * - ``gnupg``
     - 2.5.17
     - 2.5.22
   * - ``gnutls``
     - 3.8.12
     - 3.8.13
   * - ``go``
     - 1.26.5
     - 1.27.1
   * - ``go-binary-native``
     - 1.26.5
     - 1.27.1
   * - ``go-cross-canadian``
     - 1.26.5
     - 1.27.1
   * - ``go-cross-core2-32``
     - 1.26.5
     - 1.27.1
   * - ``go-crosssdk``
     - 1.26.5
     - 1.27.1
   * - ``go-runtime``
     - 1.26.5
     - 1.27.1
   * - ``gpgme``
     - 2.0.1
     - 2.1.2
   * - ``gst-devtools``
     - 1.28.5
     - 1.28.6
   * - ``gst-examples``
     - 1.28.5
     - 1.28.6
   * - ``gstreamer1.0``
     - 1.28.5
     - 1.28.6
   * - ``gstreamer1.0-libav``
     - 1.28.5
     - 1.28.6
   * - ``gstreamer1.0-plugins-bad``
     - 1.28.5
     - 1.28.6
   * - ``gstreamer1.0-plugins-base``
     - 1.28.5
     - 1.28.6
   * - ``gstreamer1.0-plugins-good``
     - 1.28.5
     - 1.28.6
   * - ``gstreamer1.0-plugins-ugly``
     - 1.28.5
     - 1.28.6
   * - ``gstreamer1.0-python``
     - 1.28.5
     - 1.28.6
   * - ``gstreamer1.0-rtsp-server``
     - 1.28.5
     - 1.28.6
   * - ``gtk-doc``
     - 1.35.1
     - 1.36.1
   * - ``gtk4``
     - 4.22.2
     - 4.22.4
   * - ``gzip``
     - 1.14
     - 1.15
   * - ``harfbuzz``
     - 12.3.2
     - 14.4.0
   * - ``hwdata``
     - 0.406
     - 0.410
   * - ``igt-gpu-tools``
     - 2.3
     - 2.5
   * - ``inetutils``
     - 2.7
     - 2.8
   * - ``iproute2``
     - 6.19.0
     - 7.2.0
   * - ``json-c``
     - 0.18
     - 0.19
   * - ``kbd``
     - 2.9.0
     - 2.10.0
   * - ``kea``
     - 3.0.3
     - 3.2.0
   * - ``less``
     - 692
     - 704
   * - ``libadwaita``
     - 1.8.4
     - 1.9.3
   * - ``libarchive``
     - 3.8.7
     - 3.8.9
   * - ``libcap``
     - 2.77
     - 2.78
   * - ``libcap-ng``
     - 0.9.1
     - 0.9.5
   * - ``libcap-ng-python``
     - 0.9.1
     - 0.9.5
   * - ``libcxx``
     - 22.1.8
     - 23.1.0
   * - ``libdisplay-info``
     - 0.3.0
     - 0.4.0
   * - ``libdrm``
     - 2.4.131
     - 2.4.134
   * - ``libedit``
     - 20251016-3.1
     - 20260512-3.1
   * - ``libevdev``
     - 1.13.6
     - 1.13.7
   * - ``libevent``
     - 2.1.12
     - 2.1.13
   * - ``libffi``
     - 3.5.2
     - 3.8.0
   * - ``libfyaml``
     - 0.9.4
     - 0.9.6
   * - ``libgcc``
     - 15.3.0
     - 16.2.0
   * - ``libgcc-initial``
     - 15.3.0
     - 16.2.0
   * - ``libgcrypt``
     - 1.12.1
     - 1.12.3
   * - ``libgfortran``
     - 15.3.0
     - 16.2.0
   * - ``libgit2``
     - 1.9.2
     - 1.9.7
   * - ``libgpg-error``
     - 1.59
     - 1.61
   * - ``libical``
     - 3.0.20
     - 4.0.5
   * - ``libinput``
     - 1.30.2
     - 1.31.3
   * - ``libjpeg-turbo``
     - 3.1.3
     - 3.2.0
   * - ``libksba``
     - 1.6.8
     - 1.8.1
   * - ``libmd``
     - 1.1.0
     - 1.2.0
   * - ``libmicrohttpd``
     - 1.0.2
     - 1.0.10
   * - ``libmodulemd``
     - 2.15.2
     - 2.15.3
   * - ``libmpc``
     - 1.3.1
     - 1.4.1
   * - ``libpcre2``
     - 10.47
     - 10.48
   * - ``libpng``
     - 1.6.56
     - 1.6.58
   * - ``libportal``
     - 0.9.1
     - 0.10.0
   * - ``libpsl``
     - 0.21.5
     - 0.23.3
   * - ``librepo``
     - 1.20.0
     - 1.21.0
   * - ``librsvg``
     - 2.61.3
     - 2.63.2
   * - ``libseccomp``
     - 2.6.0
     - 2.6.1
   * - ``libsecret``
     - 0.21.7
     - 0.21.8.2
   * - ``libslirp``
     - 4.9.1
     - 4.9.5
   * - ``libsolv``
     - 0.7.36
     - 0.7.39
   * - ``libstd-rs``
     - 1.94.1
     - 1.98.1
   * - ``libtest-fatal-perl``
     - 0.018
     - 0.019
   * - ``libtool``
     - 2.5.4
     - 2.6.2
   * - ``libtool-cross``
     - 2.5.4
     - 2.6.2
   * - ``libtool-native``
     - 2.5.4
     - 2.6.2
   * - ``libusb1``
     - 1.0.29
     - 1.0.30
   * - ``libva``
     - 2.23.0
     - 2.24.1
   * - ``libva-initial``
     - 2.23.0
     - 2.24.1
   * - ``libva-utils``
     - 2.23.0
     - 2.24.0
   * - ``libxfont2``
     - 2.0.7
     - 2.0.9
   * - ``libxi``
     - 1.8.2
     - 1.8.3
   * - ``libxkbcommon``
     - 1.13.1
     - 1.13.2
   * - ``libxmlb``
     - 0.3.25
     - 0.3.29
   * - ``lighttpd``
     - 1.4.82
     - 1.4.85
   * - ``linux-firmware``
     - 20260410
     - 20260916
   * - ``linux-libc-headers``
     - 6.18
     - 7.2
   * - ``linux-yocto``
     - 6.18.39+git
     - 6.18.48+git, 7.2.2+git
   * - ``linux-yocto-dev``
     - 7.0+git
     - 7.2+git
   * - ``linux-yocto-rt``
     - 6.18.39+git
     - 6.18.48+git, 7.2.2+git
   * - ``linux-yocto-tiny``
     - 6.18.39+git
     - 6.18.48+git, 7.2.2+git
   * - ``lld``
     - 22.1.8
     - 23.1.0
   * - ``lldb``
     - 22.1.8
     - 23.1.0
   * - ``llvm``
     - 22.1.8
     - 23.1.0
   * - ``llvm-tblgen-native``
     - 22.1.8
     - 23.1.0
   * - ``log4cplus``
     - 2.1.2
     - 2.2.0.1
   * - ``lsof``
     - 4.99.6
     - 4.99.7
   * - ``ltp``
     - 20260130
     - 20260529
   * - ``lttng-modules``
     - 2.14.4
     - 2.16.0
   * - ``lttng-tools``
     - 2.14.1
     - 2.16.0
   * - ``lttng-ust``
     - 2.14.0
     - 2.16.0
   * - ``lua``
     - 5.5.0
     - 5.5.1
   * - ``lzip``
     - 1.25
     - 1.26
   * - ``makedumpfile``
     - 1.7.8
     - 1.7.9
   * - ``man-pages``
     - 6.17
     - 6.19
   * - ``mesa``
     - 26.0.5
     - 26.2.3
   * - ``mesa-demos``
     - 9.0.0
     - 9.0.0+git
   * - ``mesa-gl``
     - 26.0.5
     - 26.2.2
   * - ``mesa-tools-native``
     - 26.0.5
     - 26.2.2
   * - ``meson``
     - 1.10.2
     - 1.12.0
   * - ``minicom``
     - 2.10
     - 2.11.1
   * - ``mkfontscale``
     - 1.2.3
     - 1.2.4
   * - ``mpg123``
     - 1.33.4
     - 1.33.7
   * - ``msmtp``
     - 1.8.32
     - 1.8.34
   * - ``nasm``
     - 3.01
     - 3.02
   * - ``nativesdk-libtool``
     - 2.5.4
     - 2.6.2
   * - ``neard``
     - 0.19
     - 0.20
   * - ``netbase``
     - 6.5
     - 6.6
   * - ``nettle``
     - 3.10.2
     - 4.0
   * - ``nfs-utils``
     - 2.8.7
     - 2.9.2
   * - ``nghttp2``
     - 1.68.1
     - 1.70.0
   * - ``openmp``
     - 22.1.8
     - 23.1.0
   * - ``opensbi``
     - 1.8.1
     - 1.9
   * - ``openssh``
     - 10.3p1
     - 10.5p1
   * - ``openssl``
     - 3.5.7
     - 4.0.3
   * - ``opkg``
     - 0.9.0
     - 0.10.0
   * - ``orc``
     - 0.4.42
     - 0.4.43
   * - ``ovmf``
     - edk2-stable202511
     - edk2-stable202605
   * - ``p11-kit``
     - 0.26.4
     - 0.26.5
   * - ``pango``
     - 1.57.0
     - 1.58.2
   * - ``parted``
     - 3.6
     - 3.8
   * - ``patchelf``
     - 0.18.0+git
     - 0.19.1
   * - ``pciutils``
     - 3.14.0
     - 3.15.0
   * - ``perl``
     - 5.42.0
     - 5.44.0
   * - ``piglit``
     - 1.0+gitr (a0a27e528f64…)
     - 1.0+gitr (56f237be32ed…)
   * - ``pinentry``
     - 1.3.2
     - 1.3.3
   * - ``pkgconf``
     - 2.5.1
     - 3.0.6
   * - ``powertop``
     - 2.15
     - 2.16
   * - ``ppp``
     - 2.5.2
     - 2.5.4
   * - ``procps``
     - 4.0.6
     - 4.0.7
   * - ``puzzles``
     - 0.0+git (ecb576fb2a0a…)
     - 0.0+git (3c3632259d29…)
   * - ``python3``
     - 3.14.6
     - 3.14.7
   * - ``python3-attrs``
     - 25.4.0
     - 26.1.0
   * - ``python3-build``
     - 1.4.0
     - 1.6.0
   * - ``python3-certifi``
     - 2026.2.25
     - 2026.7.22
   * - ``python3-cffi``
     - 2.0.0
     - 2.1.1
   * - ``python3-click``
     - 8.3.1
     - 8.5.0
   * - ``python3-cryptography``
     - 46.0.7
     - 50.0.1
   * - ``python3-cryptography-vectors``
     - 46.0.7
     - 50.0.1
   * - ``python3-cython``
     - 3.2.4
     - 3.2.9
   * - ``python3-docutils``
     - 0.22.4
     - 0.23
   * - ``python3-dtc``
     - 1.7.2
     - 1.8.1
   * - ``python3-dtschema``
     - 2025.12
     - 2026.6
   * - ``python3-editables``
     - 0.5
     - 0.6
   * - ``python3-git``
     - 3.1.43
     - 3.1.61
   * - ``python3-hatchling``
     - 1.29.0
     - 1.31.0
   * - ``python3-hypothesis``
     - 6.151.9
     - 6.167.0
   * - ``python3-idna``
     - 3.11
     - 3.20
   * - ``python3-imagesize``
     - 2.0.0
     - 2.0.1
   * - ``python3-installer``
     - 0.7.0
     - 1.0.1
   * - ``python3-jsonpointer``
     - 3.0.0
     - 3.1.1
   * - ``python3-lxml``
     - 6.0.2
     - 6.1.2
   * - ``python3-mako``
     - 1.3.10
     - 1.4.1
   * - ``python3-markdown``
     - 3.10.2
     - 3.10.3
   * - ``python3-maturin``
     - 1.12.4
     - 1.15.0
   * - ``python3-meson-python``
     - 0.19.0
     - 0.20.0
   * - ``python3-more-itertools``
     - 10.6.0
     - 10.8.0
   * - ``python3-numpy``
     - 2.4.3
     - 2.5.2
   * - ``python3-packaging``
     - 26.0
     - 26.3
   * - ``python3-pathspec``
     - 1.0.4
     - 1.1.1
   * - ``python3-pbr``
     - 7.0.3
     - 7.1.0
   * - ``python3-pdm``
     - 2.26.6
     - 2.29.0
   * - ``python3-pdm-backend``
     - 2.4.7
     - 2.4.9
   * - ``python3-pip``
     - 26.0.1
     - 26.2.1
   * - ``python3-poetry-core``
     - 2.3.1
     - 2.4.1
   * - ``python3-pycairo``
     - 1.29.0
     - 1.29.1
   * - ``python3-pyelftools``
     - 0.32
     - 0.33
   * - ``python3-pygments``
     - 2.19.2
     - 2.21.0
   * - ``python3-pygobject``
     - 3.56.1
     - 3.58.0
   * - ``python3-pyopenssl``
     - 26.0.0
     - 26.4.0
   * - ``python3-pyproject-hooks``
     - 1.2.0
     - 1.3.3
   * - ``python3-pyproject-metadata``
     - 0.11.0
     - 0.12.1
   * - ``python3-pytest``
     - 9.0.2
     - 9.1.1
   * - ``python3-pytz``
     - 2026.1
     - 2026.3.post1
   * - ``python3-requests``
     - 2.32.5
     - 2.34.2
   * - ``python3-rpds-py``
     - 0.30.0
     - 2026.6.3
   * - ``python3-sbom-cve-check``
     - 1.3.1
     - 1.3.3
   * - ``python3-scons``
     - 4.10.1
     - 4.11.1
   * - ``python3-setuptools``
     - 82.0.1
     - 84.0.0
   * - ``python3-setuptools-rust``
     - 1.12.0
     - 1.13.0
   * - ``python3-setuptools-scm``
     - 9.2.2
     - 10.2.3
   * - ``python3-shacl2code``
     - 1.0.1
     - 1.1.0
   * - ``python3-snowballstemmer``
     - 3.0.1
     - 3.1.1
   * - ``python3-spdx-python-model``
     - 0.0.5
     - 0.0.6
   * - ``python3-sphinx-argparse``
     - 0.5.2
     - 0.6.1
   * - ``python3-testtools``
     - 2.8.7
     - 2.9.1
   * - ``python3-trove-classifiers``
     - 2026.1.14.14
     - 2026.6.1.19
   * - ``python3-typing-extensions``
     - 4.15.0
     - 4.16.0
   * - ``python3-uritools``
     - 6.0.1
     - 6.1.3
   * - ``python3-urllib3``
     - 2.6.3
     - 2.8.0
   * - ``python3-uv-build``
     - 0.10.10
     - 0.12.9
   * - ``python3-wcwidth``
     - 0.6.0
     - 0.8.3
   * - ``python3-websockets``
     - 16.0
     - 17.1
   * - ``python3-wheel``
     - 0.46.3
     - 0.48.0
   * - ``python3-zipp``
     - 3.23.0
     - 4.1.0
   * - ``qemu``
     - 10.2.0
     - 11.1.1
   * - ``qemu-native``
     - 10.2.0
     - 11.1.1
   * - ``qemu-system-native``
     - 10.2.0
     - 11.1.1
   * - ``re2c``
     - 4.4
     - 4.6
   * - ``repo``
     - 2.61.1
     - 2.66.1
   * - ``resolvconf``
     - 1.94
     - 1.95
   * - ``rpm``
     - 4.20.1
     - 6.0.2
   * - ``rpm-sequoia``
     - 1.10.1
     - 1.10.2
   * - ``rpm-sequoia-crypto-policy``
     - git (f3f5fa454345…)
     - git (359ab169da6a…)
   * - ``rsync``
     - 3.4.1
     - 3.5.1
   * - ``ruby``
     - 4.0.5
     - 4.0.7
   * - ``rust``
     - 1.94.1
     - 1.98.1
   * - ``rust-cross-canadian``
     - 1.94.1
     - 1.98.1
   * - ``sbom-cve-check-update-cvelist-native``
     - 2026-05-07
     - 2026-08-25
   * - ``sbom-cve-check-update-nvd-native``
     - 2026.05.07-000006
     - 2026.08.25-000009
   * - ``scdoc``
     - 1.11.4
     - 1.11.5
   * - ``screen``
     - 5.0.1
     - 5.0.2
   * - ``shaderc``
     - 2026.1
     - 2026.3
   * - ``shadow``
     - 4.19.4
     - 4.20.2
   * - ``shared-mime-info``
     - 2.4
     - 2.5.1
   * - ``spirv-headers``
     - 1.4.341.0
     - 1.4.357.0
   * - ``spirv-llvm-translator``
     - 22.1.1
     - 23.1.1
   * - ``spirv-tools``
     - 1.4.341.0
     - 1.4.357.0
   * - ``sqlite3``
     - 3.51.3
     - 3.53.4
   * - ``strace``
     - 6.19
     - 7.2
   * - ``stress-ng``
     - 0.20.01
     - 0.22.00
   * - ``swig``
     - 4.4.1
     - 4.5.0
   * - ``sysstat``
     - 12.7.9
     - 12.8.0
   * - ``systemd``
     - 259.5
     - 261.2
   * - ``systemd-boot``
     - 259.5
     - 261.2
   * - ``systemd-boot-native``
     - 259.5
     - 261.2
   * - ``systemtap``
     - 5.4
     - 5.5
   * - ``systemtap-native``
     - 5.4
     - 5.5
   * - ``taglib``
     - 2.2.1
     - 2.3.1
   * - ``tcf-agent``
     - 1.9.0
     - 1.11.0
   * - ``tcl``
     - 9.0.3
     - 9.0.4
   * - ``tcl8``
     - 8.6.17
     - 8.6.18
   * - ``tiff``
     - 4.7.1
     - 4.7.2
   * - ``time``
     - 1.9
     - 1.10
   * - ``ttyrun``
     - 2.41.0
     - 2.44.0
   * - ``tzdata/tzcode-native``
     - 2026c
     - 2026d
   * - ``u-boot``
     - 2026.01
     - 2026.07
   * - ``u-boot-tools``
     - 2026.01
     - 2026.07
   * - ``utfcpp``
     - 4.0.9
     - 4.2.0
   * - ``util-linux``
     - 2.41.5
     - 2.42.4
   * - ``util-linux-libuuid``
     - 2.41.5
     - 2.42.3
   * - ``vala``
     - 0.56.18
     - 0.56.19
   * - ``valgrind``
     - 3.26.0
     - 3.27.1
   * - ``vim``
     - 9.2.0340
     - 9.2.0993
   * - ``vim-tiny``
     - 9.2.0340
     - 9.2.0993
   * - ``virglrenderer``
     - 1.2.0
     - 1.3.0
   * - ``vte``
     - 0.82.2
     - 0.84.1
   * - ``vulkan-headers``
     - 1.4.341.0
     - 1.4.357.0
   * - ``vulkan-loader``
     - 1.4.341.0
     - 1.4.357.0
   * - ``vulkan-samples``
     - git (fa2cf45adde0…)
     - git (383471195757…)
   * - ``vulkan-tools``
     - 1.4.341.0
     - 1.4.357.0
   * - ``vulkan-utility-libraries``
     - 1.4.341.0
     - 1.4.357.0
   * - ``vulkan-validation-layers``
     - 1.4.341.0
     - 1.4.357.0
   * - ``vulkan-volk``
     - 1.4.341.0
     - 1.4.357.0
   * - ``waffle``
     - 1.8.1
     - 1.8.3
   * - ``wayland``
     - 1.24.0
     - 1.26.0
   * - ``wayland-protocols``
     - 1.47
     - 1.49
   * - ``webkitgtk``
     - 2.50.6
     - 2.52.6
   * - ``weston``
     - 15.0.0
     - 16.0.0
   * - ``which``
     - 2.23
     - 2.25
   * - ``wireless-regdb``
     - 2026.05.30
     - 2026.09.03
   * - ``wpa-supplicant``
     - 2.11
     - 2.12
   * - ``xev``
     - 1.2.6
     - 1.2.7
   * - ``xkeyboard-config``
     - 2.47
     - 2.48
   * - ``xmodmap``
     - 1.0.11
     - 1.0.12
   * - ``xrandr``
     - 1.5.3
     - 1.5.4
   * - ``xset``
     - 1.2.5
     - 1.2.6
   * - ``xvinfo``
     - 1.1.5
     - 1.1.6
   * - ``xwininfo``
     - 1.1.6
     - 1.1.7
   * - ``xz``
     - 5.8.2
     - 5.8.3

Contributors to |yocto-ver|
---------------------------

..
   List obtained with the following shell snippet:

      authors=""
      for repo in openembedded-core yocto-docs bitbake meta-yocto; do
         authors="${authors}\n$(git --no-pager -C $repo log --format="-  %an" yocto-6.0..origin/master)"
      done
      echo $authors | sort | uniq

   Email addresses and duplicates removed.

Thanks to the following people who contributed to this release:

-  Adam Blank
-  Adam Duskett
-  Adarsh Jagadish Kamini
-  Aditya GS
-  Adrian Freihofer
-  Aksh Garg
-  Alejandro Hernandez Samaniego
-  Alejandro Mery
-  Alessandro Zini
-  Alexander Kanavin
-  Alexander Stein
-  Alex Kiernan
-  Alper Ak
-  Amaury Couderc
-  Anders Heimer
-  Andreas Mützel
-  Andrei Lalaev
-  Andrej Valek
-  Andrew Geissler
-  Anis Bougrine
-  Ankur Tyagi
-  Anthony Squires
-  Antoine Gouby
-  Antonin Godard
-  Anton Skorup
-  Aravind Bandari
-  AshishKumar Mishra
-  Aswin Murugan
-  Aurelien DESBRIERES
-  Baban
-  Babanpreet Singh
-  Benjamin Robin
-  Bhavikkumar Girishbhai Ronvelia
-  Bin Cao
-  Bruce Ashfield
-  Changqing Li
-  Chen Qi
-  Chris Laplante
-  Colin Pinnell McAllister
-  Corentin Guillevic
-  Daiane Angolini
-  Daniel Dragomir
-  Daniel McGregor
-  Daniel Turull
-  Daniel Wagenknecht
-  David Nyström
-  David Reyna
-  Dawid Bijak
-  Deepesh Varatharajan
-  Denys Dmytriyenko
-  Devansh Patel
-  Dmitry Baryshkov
-  Dmitry Sakhonchik
-  Efe Can Icoz
-  Eilís 'pidge' Ní Fhlannagáin
-  El Mehdi YOUNES
-  Enoch Ng
-  Enzo Frese
-  Eric Meyers
-  Ernest Van Hoecke
-  Esa Jaaskela
-  Etienne Cordonnier
-  Fabian Pfitzner
-  Fabian Pflug
-  Fabien Lehoussel
-  Fabio Estevam
-  Florin Diaconescu
-  Francesco Valla
-  Francisco Pedraza
-  Fredrik Svensson (svsvenss)
-  Frieder Schrempf
-  Gabriel Smith
-  Gavvala, Kris
-  George Refseth
-  Ghanshyam Banait
-  Guðni Már Gilbert
-  Gustavo Henrique Nihei
-  Haiqing Bai
-  Haixiao Yan
-  Hangtian Zhu
-  Harald Brinkmann
-  Harish Sadineni
-  Hemanth Kumar M D
-  Hetvi Thakar
-  Hiago De Franco
-  Himani Barde
-  Himani Ramesh Barde
-  Himanshu Jadon
-  hongxu
-  Igor Opaniuk
-  Ivan Nestlerode
-  jaekyu.lee
-  Jaeyoon Jung
-  Jaipaul Cheernam
-  Jamin Lin
-  Jan Vermaete
-  Jate Sujjavanich
-  Jesse Van Gavere
-  Jinfeng Wang
-  Jinwang Li
-  Joao Marcos Costa
-  João Marcos Costa
-  Joerg Vehlow
-  Johan Anderholm
-  John Ripple
-  Jonas Juffinger
-  Jörg Sommer
-  Jose Quaresma
-  Joshua Watt
-  Juhandré Knoetze
-  Junjie Cao
-  Kanksha Paturi
-  Karthik
-  KAZUYOSHI AKIYAMA (秋山 和慶)
-  Khem Raj
-  Kris Gavvala
-  Kyungjik Min
-  Lee Chee Yang
-  Leon Anavi
-  Leonardo Costa
-  Leonid Iziumtsev
-  Levi Shafter
-  Lian Wang
-  Li Wang
-  Li Zhou
-  Luca Fancellu
-  Marcio Henriques
-  Marcus Flyckt
-  Marek Vasut
-  Mark Hatle
-  Mark Jonas
-  Markus Swarowsky
-  Markus Volk
-  mark.yang
-  Marta Rybczynska
-  Martin Jansa
-  Mathieu Dubois-Briand
-  Matt Madison
-  Mengshi Wu
-  Metz, Josiah
-  Michael Halstead
-  Michael Opdenacker
-  Michael Tretter
-  Michal Sieron
-  Mikko Rapeli
-  Mingli Yu
-  Minwoo Choi
-  Moritz Haase
-  Nate Kent
-  Nathaniel White
-  Nick Owens
-  Nico
-  Nicolas Dechesne
-  Niko Mauno
-  Nora Schiffer
-  Oleksiy Obitotskyy
-  Omkar Patil
-  Otavio Salvador
-  Pascal Eberhard
-  Paul Barker
-  Paul Eggleton
-  Peter Kjellerstedt
-  Peter Marko
-  Peter Tatrai
-  Philip Lorenz
-  Prabhudasu Vatala
-  Pratik Farkase
-  Quan Sun
-  Quentin Schulz
-  Rasmus Villemoes
-  Ricardo Salveti
-  Ricardo Simoes
-  Richard Purdie
-  Robert P. J. Day
-  Robert Yang
-  Rob Woolley
-  Roland Kovacs
-  Ross Burton
-  Rouven Czerwinski
-  Rouven Rastetter
-  Ryan Eatmon
-  Sai Sneha
-  Sam Kent
-  Sandeep J
-  Sebastian Muxel
-  Sergio Prado
-  sh0127.shin
-  Shinu Chandran
-  Siddharth Doshi
-  Siva Balasubramanian
-  Sudhir Dumbhare
-  Sumanth Gavini
-  Sundeep KOKKONDA
-  Sunil Dora
-  Tafil Avdyli
-  Tan Siewert
-  Taruntej Kanakamalla
-  Tejas Kanfade
-  Theo Gaige
-  Thomas Perrot
-  Thorsten Schmelzer
-  Thune Tran
-  Tim Orling
-  Tobias Pistora
-  Trevor Gamblin
-  Trevor Woerner
-  T, Sai Sireesha
-  Tushar Darote
-  Ulrich Ölmann
-  Vijay Anusuri
-  Viswanath Kraleti
-  Vivek Puar
-  Vyacheslav Yurkov
-  Walter Werner Schneider
-  Wang Mingyu
-  Wei Deng
-  Wei Gao
-  Wei Zhang
-  Wenwen Fu
-  Wes Malone
-  WXbet
-  Xiaozhan Li
-  Xiuzhuo Shang
-  Yann Dirson
-  Yash Shinde
-  Yoann Congal
-  Yogesh Tyagi
-  Zheng Ruoqin
-  Zhixiong Chi
-  Zk47T
-  Zoltán Böszörményi

Repositories / Downloads for Yocto-|yocto-ver|
----------------------------------------------

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`blacksail </yocto-docs/log/?h=blacksail>`
-  Tag:  :yocto_git:`yocto-6.1 </yocto-docs/log/?h=yocto-6.1>`
-  Git Revision: :yocto_git:`f26faba8568a3c8fc81d81d3650cc419847a4e17 </yocto-docs/commit/?id=f26faba8568a3c8fc81d81d3650cc419847a4e17>`
-  Release Artefact: yocto-docs-f26faba8568a3c8fc81d81d3650cc419847a4e17
-  sha: db04fa7ccbe99ab633f83329490a4af296328c749835559bcc840a3f8aaf9b51
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.1/yocto-docs-f26faba8568a3c8fc81d81d3650cc419847a4e17.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.1/yocto-docs-f26faba8568a3c8fc81d81d3650cc419847a4e17.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`blacksail </openembedded-core/log/?h=blacksail>`
-  Tag:  :oe_git:`yocto-6.1 </openembedded-core/log/?h=yocto-6.1>`
-  Git Revision: :oe_git:`419c1b0f1ed0e3ce22b1022061f763d007883121 </openembedded-core/commit/?id=419c1b0f1ed0e3ce22b1022061f763d007883121>`
-  Release Artefact: oecore-419c1b0f1ed0e3ce22b1022061f763d007883121
-  sha: 2619d7b6104324debe382938dfddc6e3b23d8b258e09a3d22c440d0dcafb17be
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.1/oecore-419c1b0f1ed0e3ce22b1022061f763d007883121.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.1/oecore-419c1b0f1ed0e3ce22b1022061f763d007883121.tar.bz2

meta-yocto

-  Repository Location: :yocto_git:`/meta-yocto`
-  Branch: :yocto_git:`blacksail </meta-yocto/log/?h=blacksail>`
-  Tag:  :yocto_git:`yocto-6.1 </meta-yocto/log/?h=yocto-6.1>`
-  Git Revision: :yocto_git:`43946d08974e61361f6313ae348c5d87c12be934 </meta-yocto/commit/?id=43946d08974e61361f6313ae348c5d87c12be934>`
-  Release Artefact: meta-yocto-43946d08974e61361f6313ae348c5d87c12be934
-  sha: cc2c54afc85fce7cd429eb356ada6b3b921fe6065169a297c085adba16bead21
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.1/meta-yocto-43946d08974e61361f6313ae348c5d87c12be934.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.1/meta-yocto-43946d08974e61361f6313ae348c5d87c12be934.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.20 </bitbake/log/?h=2.20>`
-  Tag:  :oe_git:`yocto-6.1 </bitbake/log/?h=yocto-6.1>`
-  Git Revision: :oe_git:`634f8dede6b9d71b5a6691499fd05a8678b46eae </bitbake/commit/?id=634f8dede6b9d71b5a6691499fd05a8678b46eae>`
-  Release Artefact: bitbake-634f8dede6b9d71b5a6691499fd05a8678b46eae
-  sha: bb9f71d0d6de0d8f456bfe35f6e3f9db518e39d15af3cd49b51a90f2c5488c58
-  Download Locations:

   https://downloads.yoctoproject.org/releases/yocto/yocto-6.1/bitbake-634f8dede6b9d71b5a6691499fd05a8678b46eae.tar.bz2

   https://mirrors.edge.kernel.org/yocto/yocto/yocto-6.1/bitbake-634f8dede6b9d71b5a6691499fd05a8678b46eae.tar.bz2


