.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: wrynose
.. |yocto-ver| replace:: 6.0
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Release notes for |yocto-ver| (|yocto-codename|)
================================================

This document lists new features and enhancements for the Yocto Project
|yocto-ver| Release (codename "|yocto-codename|"). For a list of breaking
changes and migration guides, see the :doc:`/migration-guides/migration-6.0`
section.

The |yocto-ver| (|yocto-codename|) release is the new LTS release after 5.0
(scarthgap). If you are migrating from the 5.0 version, be sure to read the
previous migration guides:

-  :doc:`/migration-guides/migration-5.1`
-  :doc:`/migration-guides/migration-5.2`
-  :doc:`/migration-guides/migration-5.3`

See also the list of new features and enhancements of the previous releases:

-  :doc:`/migration-guides/release-notes-5.1`
-  :doc:`/migration-guides/release-notes-5.2`
-  :doc:`/migration-guides/release-notes-5.3`

New Features / Enhancements in |yocto-ver|
------------------------------------------

-  Linux kernel 6.18, gcc 15.2, glibc 2.43, LLVM 22.1.2, and over 300 other
   recipe upgrades

..
   Found in meta/classes-global/sanity.bbclass:check_sanity_everybuild

-  Minimum Python version required on the host: 3.9.

-  New variables:

   -  The :term:`OPENSSH_HOST_KEY_DIR` variable can be used to specify the
      directory where OpenSSH host keys are stored. The default value is
      ``/etc/ssh`` (:oecore_rev:`addd80ddfd892eb4513f323d369d210935416e05`)

   -  The :term:`OPENSSH_HOST_KEY_DIR_READONLY_CONFIG` variable can be used to
      specify the directory where OpenSSH host keys are stored when the device
      uses a read-only filesystem. The default value is ``/var/run/ssh``
      (:oecore_rev:`addd80ddfd892eb4513f323d369d210935416e05`)

   -  The :term:`SPDX_INCLUDE_KERNEL_CONFIG` can be set to "1" to export the Linux
      kernel configuration (``CONFIG_*`` parameters) into the SPDX document when
      using the :ref:`ref-classes-create-spdx` class
      (:oecore_rev:`228a968e7c47d811c06143279bdb0f9c5f374bef`)

   -  The :term:`SPDX_INCLUDE_PACKAGECONFIG` variable can be set to "1" to
      export a recipe's :term:`PACKAGECONFIG` features (enabled/disabled) into
      the SPDX document when using the :ref:`ref-classes-create-spdx` class
      (:oecore_rev:`228a968e7c47d811c06143279bdb0f9c5f374bef`)

   -  The :term:`SPDX_PACKAGE_URL` allows specifying a space-separated list of
      Package URLs (purls) for the software Package when using the
      :ref:`ref-classes-create-spdx` class
      (:oecore_rev:`874b2d301d3cac617b1028bc6ce91b1f916a6508`)

   -  The :term:`SPDX_GIT_PURL_MAPPINGS` variable allows mapping domain names to
      PURLs (Package URLs) in SPDX documents
      (:oecore_rev:`9f1825e74d0f70917676201286b148aea84cc660`)

   -  The :term:`SPDX_CONCLUDED_LICENSE` allows specifying the
      ``hasConcludedLicense`` object of individual SBOM packages when using the
      :ref:`ref-classes-create-spdx` class
      (:oecore_rev:`bb21c6a429a2ecec82a8afe7d79502291ccaef01`)

   -  The :term:`FIT_MKIMAGE_EXTRA_OPTS` variable allows passing extra options
      to the ``mkimage`` command when creating a FIT image with the
      :ref:`ref-classes-kernel-fit-image` class
      (:oecore_rev:`d925d67061ef5d7a8abe15c715614650094d50c6`)

   -  The :term:`FIT_CONF_MAPPINGS` variable allows mapping extra configurations
      to existing ones or rename an existing configuration in FIT images created
      with the :ref:`ref-classes-kernel-fit-image` class (:oecore_rev:`e30f809a50c2151e525424879383c02325a7ec9a`)

   -  The :term:`UBOOT_CONFIG_FRAGMENTS` and :term:`UBOOT_FRAGMENTS` allow
      supplying additional configuration fragments to the existing U-Boot
      configuration. See the definition of the variables for more information,
      and the documentation of the :ref:`ref-classes-uboot-config` class
      (:oecore_rev:`9e96d3dedee47657657686310508e0aaee7f4e02`)

   -  The :term:`IMAGE_EXTRA_PARTITION_FILES` allows specifying extra files from
      the deploy directory (:term:`DEPLOY_DIR_IMAGE`) to install in a WIC
      partition created with the ``extra_partition`` plugin
      (:oecore_rev:`e1526079d205dac6e3cff6d8e5cb37f68b631009`)

   -  The :term:`FIT_LOADABLES`, :term:`FIT_LOADABLE_FILENAME`,
      :term:`FIT_LOADABLE_TYPE`, :term:`FIT_LOADABLE_ARCH`,
      :term:`FIT_LOADABLE_OS` and :term:`FIT_LOADABLE_LOADADDRESS` variables can
      be used to specify arbitrary ``loadables`` in a FIT image
      (:oecore_rev:`2535427d8de07f6e432d08cbdc046bdfd8788911`)

-  Kernel-related changes:

   -  :ref:`ref-classes-kernel-fit-image`: Support arbitrary loadables
      (:oecore_rev:`2535427d8de07f6e432d08cbdc046bdfd8788911`)

   -  :ref:`ref-classes-kernel-yocto`: Allow enabling Rust in the kernel by
      adding ``rust`` to the :term:`KERNEL_FEATURES` (for Linux kernel recipes
      inheriting this class)
      (:oecore_rev:`6719ed4a34051cf3dcbb67984ee15613512c061a`)

   -  Disable :ref:`ref-classes-ccache` support when Linux kernel Rust support
      is enabled
      (:oecore_rev:`d80d006ae85172eb5125b7e1b44d4dee48615c92`)

-  New core recipes:

   -  ``libconfig``: Import recipe from :oe_git:`/meta-openembedded/`, needed by
      one of the Mesa recipes (:oecore_rev:`1a0196a794f8858c4715871558e97c3d69deb19e`)

   -  ``python3-sphinxcontrib-svg2pdfconverter``: Used for the generation of the
      Yocto Project documentation (:oecore_rev:`f3f001967744b593fb39c32058d29595bbf0ffb6`)

   -  ``python3-pyzstd``: Import from :oe_git:`meta-python
      </meta-openembedded/tree/meta-python>`, needed by the ``ukify`` tool of
      systemd v258 (:oecore_rev:`88a27133c184125e1503d1397cbd276a9a76f6ab`)

   -  ``python3-uv-build``: This recipe adds the ``uv`` Python build backend,
      required by ``python3-cryptography`` (:oecore_rev:`0880cd2b79ee47274d62035e354aa4b93966b4d0`)

   -  ``blueprint-compiler``: Add the recipe as it became a dependency of the
      ``epiphany`` recipe after its upgrade to 49.2 (:oecore_rev:`4212392ca7ebf890e1e192ddd0e7dbe1f8dabcf2`)

   -  ``python3-sbom-cve-check``: New recipe for building and using
      `sbom-cve-check <https://github.com/bootlin/sbom-cve-check>`__, a
      lightweight SBOM CVE analysis tool
      (:oecore_rev:`0fdacec2d7101d3fe638b430c43b1e14acd148ae`)

   -  ``python3-shacl2code``, ``python3-hatch-build-scripts``,
      ``python3-spdx-python-model``: Added as dependencies of
      ``python3-sbom-cve-check``
      (:oecore_rev:`48622216cb1a80f9cc127ebb08e13cc455d09240`,
      :oecore_rev:`41591afd51ddbdaf1779799f4258a81afcff238d`,
      :oecore_rev:`a83eaca5d9f3e2eb76a5d3c6bb42cf3b3bfe92fb`)

   -  ``libfyaml``: Added as a dependency of ``appstream``
      (:oecore_rev:`b8b7b5873fecef4040c5f35d5a0e857f8f9fc907`)

   -  ``meta-world-recipe-sbom``: Building this recipe will produce SBOM
      documents for each recipe present in the build environment, using the
      :ref:`ref-classes-create-spdx` class. This is different from the image
      SBOM which can also be generated using the :ref:`ref-classes-create-spdx`
      class after building an image. See :doc:`/dev-manual/sbom` for more
      information
      (:oecore_rev:`d999ac407c86b462134008818d5863ecb577f3c6`)

   -  ``python3-kirk``: The Kirk application is a fork of ``runltp-ng`` and
      became the official `LTP <https://linux-test-project.readthedocs.io/en/latest/>`__
      tests executor
      (:oecore_rev:`c33fd4e50a66ace990f3820f980876bfbfc07baa`)

   -  ``wic``: This recipe builds the :doc:`WIC </dev-manual/wic>` command-line
      tool. This used to be part of :term:`OpenEmbedded-Core (OE-Core)` but is
      now externally managed
      (:oecore_rev:`25ca1cb46dd6d0c57f61f2dc3b649601dc81b50c`,
      :oecore_rev:`b9e2a2f584376076c4552bef7309c81b9fe986c0`)

-  New core classes:

   -  :ref:`ref-classes-kernel-yocto-rust`: Adds the required dependencies to
      build the Rust components of the Linux kernel
      (:oecore_rev:`6c90097bebeffe7c5be35fc56e61b1994f03d6a9`)

   -  :ref:`ref-classes-module-rust`: Support for building out-of-tree Rust
      kernel modules. An example recipe using this class can be found in
      :oecore_path:`meta-skeleton/recipes-kernel/rust-out-of-tree-module`
      (:oecore_rev:`76fd22f09fab6c1de339dac310f9b31cb5e4ad69`,
      :oecore_rev:`76fd22f09fab6c1de339dac310f9b31cb5e4ad69`)

   -  :ref:`ref-classes-sbom-cve-check`: Class for post-build CVE analysis of an
      image, which uses the `sbom-cve-check <github.com/bootlin/sbom-cve-check>`__
      tool internally
      (:oecore_rev:`8ef22ad9e302f86b2da4fa81541a464e95b9ef3c`)

   -  :ref:`ref-classes-sbom-cve-check-recipe`: Class for post-build CVE
      analysis of recipes (using the recipe SBOM, meaning building the
      software provided by the recipe is not needed), which uses the
      `sbom-cve-check <github.com/bootlin/sbom-cve-check>`__ tool internally
      (:oecore_rev:`e2518b1eaabef13c9f8d44b52b8ec9d4dd4ed916`)

-  Global configuration changes:

   -  ``base-passwd``: Add a ``clock`` group as `systemd` version v258 introduces
      this group to `enable applications like linuxptp to open clocks without
      root privileges <https://github.com/systemd/systemd/commit/af96ccfc24bc4803078a46b4ef2cdeb5decdfbcd>`__
      (:oecore_rev:`aad849301be157b5605fc0133e7312ca30250d82`)

   -  ``lto.inc``: Add a `Clang` specific :term:`LTO` configuration
      (:oecore_rev:`253da2e6fc0aa01cbd1b249cfcca35d9fe7740ba`)

   -  ``bitbake.conf``:

      -  remove :term:`DEBUG_PREFIX_MAP` from :term:`TARGET_LDFLAGS`
         (:oecore_rev:`1797741aad02b8bf429fac4b81e30cdda64b5448`)

      -  The default definition of :term:`TARGET_LDFLAGS` used to
         contain the value of :term:`DEBUG_PREFIX_MAP`, to fix binary
         reproducibility issues. This was no longer needed after the originating
         `GCC bug <https://gcc.gnu.org/bugzilla/show_bug.cgi?id=101473>`__ was
         fixed (:oecore_rev:`1797741aad02b8bf429fac4b81e30cdda64b5448`)

      -  Switch :term:`BB_SIGNATURE_HANDLER` to ``OEEquivHash`` and
         :term:`BB_HASHSERVE` to ``auto`` by default
         (:oecore_rev:`5596ea156d3f2abea57e590798bbbf1bf4a860de`,
         :oecore_rev:`4a388406acf0210e8a47c4733979256b10e078ff`)

   -  The :ref:`ref-classes-uninative` class is now enabled by default. This
      allows reuse of native sstate built on one distro on another
      (:oecore_rev:`722897f96d30e978b20e140419fb044d850f5c74`)

   -  The :oe_git:`no-static-libs.inc
      <openembedded-core/tree/meta/conf/distro/include/no-static-libs.inc>`
      file, disabling most static libraries in various recipes, is now included
      by default in the default distro setup (appearing as the ``nodistro``
      :term:`DISTRO`) (:oecore_rev:`03fc931bfe9ea3fa9f33553e6020cbc067b24291`)

   -  The :oe_git:`security_flags.inc
      <openembedded-core/tree/meta/conf/distro/include/security_flags.inc>`
      file, adding various security related flags to the default compiler and
      linker, is now included by default in the default distro setup (appearing
      as ``nodistro`` :term:`DISTRO`)
      (:oecore_rev:`4c2d64c10a5b0437ab1ea04df22386f0f95124d1`)

   -  The :oe_git:`yocto-space-optimize.inc
      <openembedded-core/tree/meta/conf/distro/include/yocto-space-optimize.inc>`
      file, adding various space optimization tweaks, is now included by default
      in the default distro setup (appearing as ``nodistro`` :term:`DISTRO`)
      (:oecore_rev:`175fcf9fad699dd122680d3f6961af9bf8487046`)

-  QEMU / ``runqemu`` changes:

   -  ``qemuboot```: Make the tap interface nameserver configurable through
      :term:`QB_TAP_NAMESERVER`
      (:oecore_rev:`0e8c2582d46dc703511521386d1cc1694ae21e22`)

   -  ``qemu``: Disable the ``libkeyutils`` feature
      (:oecore_rev:`30cc9f5192436f5498ff9c55c0545eebb50fa423`)

   -  ``runqemu-extract-sdk``: Support the ``tar.zst`` format
      (:oecore_rev:`650bb45251b518847fd998891d5b6b5989fb7cd8`)

   -  ``qemurunner``: Improve ``qmp`` module detection
      (:oecore_rev:`a7386d071d42c7cd8413a957d1f72d73838ad6e7`)

   -  ``runqemu``: Support ``.tar.zst``, ``.tar,xz``, ``.tar`` rootfs archive
      types (:oecore_rev:`3a6172fbb6d3866b84627bcbf13e0a96837a85b1`)

   -  ``runqemu``: Allow VNC to be used as a fallback when there is no
      ``DISPLAY`` set
      (:oecore_rev:`df9e9f382eb27f15cee4f3f77023646dcc1273fa`)

-  Documentation changes:

   -  The documentation build now fetches the list of active and inactive
      version of the documentation from the remote `releases.json
      <dashboard.yoctoproject.org/releases.json>`__ file. This also applies to
      the :term:`BitBake` documentation.

-  Go changes:

   -  :ref:`ref-classes-go`: Disable workspaces when building
      (``GOWORK="off"``) (:oecore_rev:`c52c5e88626968b08510818f09829f2e1c9f94ae`)

   -  ``meta-go-toolchain``: Disable support for the ``riscv32`` and ``ppc32``
      architectures, as this was not supported
      (:oecore_rev:`f55407185c63c895fa3c4fdf74e6e63ea9517a20`)

-  Rust changes:

   -  Enable dynamic linking with llvm. This allows dynamically linking to
      ``libLLVM.so`` instead of linking statically
      (:oecore_rev:`74ba238ff1ba1e9b612aece1989b828f3a8f8770`)

   -  Install Rust library sources for ``make rustavailable`` support
      (:oecore_rev:`2912ca3b341a5c5f6a658cde332ccd87368bd39d`)

   -  Enable dynamic LLVM linking by default
      (:oecore_rev:`d0671c3dad87a063b3a41dd07cde89b5684e692c`)

   -  Enable fully static linking when :term:`TCLIBC` is set to ``musl``
      (:oecore_rev:`75409c60e9e63fdcbb9d4f54130052991362ec08`)

-  Wic Image Creator changes:

   -  ``wic/engine``: Fix copying directories into wic image with ``ext*``
      partitions (:oecore_rev:`1ed38aff5f810d064c87aff9cbd310906833b6ba`)

   -  Re-implement sector-size support
      (:oecore_rev:`b50d6debf7baa555fbfb3521c4f952675bba2d37`)

   -  The Wic tool is now maintained in a separate project, no longer part of
      :term:`OpenEmbedded-Core (OE-Core)`: :yocto_git:`/wic/`

   -  A new ``wicenv`` type can be added to :term:`IMAGE_FSTYPES` to place the
      ``.env`` file generate by Wic in the deployment directory
      (:term:`DEPLOY_DIR_IMAGE`)
      (:oecore_rev:`e4d49702f21fb75444d58f419432649a04e351c9`)

-  Testing-related changes:

   - :ref:`ref-classes-ptest` support was added for the following recipes:

      -  ``libarchive`` (:oecore_rev:`6e0bf90e31c969fb18efb18aaceaee886194a7b7`)
      -  ``libassuan`` (:oecore_rev:`1010abf3e32e6616ef0075d4d826c1734937152b`)
      -  ``libcheck`` (:oecore_rev:`1bb06e23c1c87829e5c4211e34229305c7d5f851`)
      -  ``libconfig`` (:oecore_rev:`f3e9d1326bf37361ff94dc4eef52de13b64651b2`)
      -  ``libksba`` (:oecore_rev:`f50a2005dda8cecf3a9db44edb131e7e332fa42d`)
      -  ``libmd`` (:oecore_rev:`4c0a41389bdab30e3b349fef8df6ca0ef4893b89`)
      -  ``libpcre2`` (:oecore_rev:`a7b79bf6ab86cb3ca82234e80f31c5f0208cd92d`)
      -  ``libsolv`` (:oecore_rev:`f5432d1c45f9eb47182049c6930cfc6d5b26bc8d`)
      -  ``libyaml`` (:oecore_rev:`ed2a3459829bb3b6c10143cceaef0147a0cb2b98`)
      -  ``utfcpp`` (:oecore_rev:`49314caa7eb8efd86577121337a0b0d7472eab1b`)

   -  ``selftests``: Use SHA256 keys for RPM tests
      (:oecore_rev:`692919be1947b95a73b9655977396c4f65f811c4`)

   -  ``oeqa``: Open JSON files to parse in a context manager
      (:oecore_rev:`e96baf588dfa90d366e94f2a72ec8941e397c596`)

   -  ``resulttool``: Add :ref:`ref-classes-ptest` support to the JUnit output
      format (:oecore_rev:`2abe2d701c98cbf3a81f211252157de523742b0b`)

   -  :ref:`ref-tasks-testimage`: Print last lines of kernel log on test fail
      (:oecore_rev:`fea3c445a9ccb803468b83ea4e8fa92fe442b8e0`)

   -  ``reproducible``: Use the `jQuery` CDN instead of ``jquery-native``
      (:oecore_rev:`d3ee5497b1ce6eb487419f6d821c3ad38491e5ec`)

   -  ``selftest``: Test installation of recipes with complex packaging
      (:oecore_rev:`6f3aab6bfa754ecaeee0acc013cb6be1f07c1ec0`)

   -  Add ``test_sdk_runqemu`` to test the execution of ``runqemu`` from an SDK
      (:oecore_rev:`7fbb281cc16b6a0071777df5a6ed988463dda263`)

   -  Add tests for Rust support in the Linux kernel
      (:oecore_rev:`01ea2b2add3c0e87a4a23c9d2dabee8a46b60702`,
      :oecore_rev:`10dff9f0ed2a87512d96c9419f3b4b35db41dd8b`)

   -  Replace ``runltp`` with ``kirk``, as it became the new official LTP test
      executor
      (:oecore_rev:`c1e5ed4133729212e6ce3e135a2e4d1d624de20b`)

   -  :doc:`WIC </dev-manual/wic>` related tests were updated after wic was
      moved to its own repository and is externally managed
      (:oecore_rev:`b9e2a2f584376076c4552bef7309c81b9fe986c0`)

-  Utility script changes:classes

   -  ``bitbake-config-build``: It is now possible to disable all fragments
      starting with a prefix by issuing ``bitbake-config-build disable-fragment
      <prefix>/`` (:oecore_rev:`573695d2ff3e0d47c6ef91418e5002df017bb7bc`)

   -  ``recipetool``: Support PEP639-variant of license key in Python
      ``pyproject.toml`` files (:oecore_rev:`9d1a7bb5d8aa94b74cd66edcb88e323c926d299b`)

   -  :ref:`ref-classes-buildhistory`:

      -  Also show renamed directories (:oecore_rev:`9bf22112ea4687e4bf855f12b592b37479aa40df`)
      -  Fix handling of :term:`RDEPENDS` style strings (:oecore_rev:`b013d62d1092a5f2ed14c11d6e7bb37d74e5e6cc`)

   -  ``create-pull-request``: Keep commit hash to be pulled in cover email
      (:oecore_rev:`c78f5ae4a5ba3675b78cc226feb7b9fbbfd8da19`)

   -  ``yocto-check-layer``: Add messages in ``test_readme`` assertions
      (:oecore_rev:`9fe883ce4c6284f1b75031adafeeafb47e56958c`)

   -  ``improve_kernel_cve_report``:

      -  Sort ``kernel_compiled_files`` (:oecore_rev:`682e5beb0ce100ddc8413296334dfdbe0426dd38`)
      -  Correct the description for fixed version (:oecore_rev:`b76da2048bf3d72708d0d26215b959d09de17da0`)
      -  Update data if CVE exists (:oecore_rev:`9ea6d9209b95f8d31975d71315fb52343e6aa729`)
      -  Validate that cve details field exists (:oecore_rev:`80ff4903ea1b839f9cd9393b314c3adfbb80b765`)

   -  ``oe-pkgdata-util``: improve the ``lookup-pkg`` error message for
      :term:`RPROVIDES` packages
      (:oecore_rev:`46ff3a8d2c18fcba87c711bb23dbdabae20eef84`)

-  BitBake changes:

   -  ``bitbake-layers``:

      -  Add a ``--show-variants`` option to the ``show-recipes`` subcommand to
         display :term:`BBCLASSEXTEND` variants
         (:bitbake_rev:`353d5e948c99a5d4f76f414054aca039a78e7ab9`)

      -  Fix the branch detection method of ``layerindex-fetch``
         (:bitbake_rev:`af9dd012e7f4d16dccd1d6118be5da94ede68f85`)

   -  ``bitbake-setup``:

      -  Implement symlinking local sources into builds with the
         ``--use-local-source`` option of the ``init`` subcommand
         (:bitbake_rev:`ed5a3a0fc82041c95a8104ba8a123f7eb1c19e57`)

      -  Convert ``print()`` calls to use a :term:`BitBake` logger
         (:bitbake_rev:`6e511d035a3d1e4129dab7b0dfbf216bd8e99b47`)

      -  Correct several scenarios in layer updates
         (:bitbake_rev:`aa15cc7bd10264ca28aabd3b5a652d818efc389e`)

      -  Source in the ``git-remote`` section can now be specified more simply
         with the ``uri`` property, instead of the ``remotes`` property
         (:bitbake_rev:`7941a5dc4dba81ab2141531b8af94371a923b32b`). For example:

         .. code-block:: json

            "bitbake": {
                "git-remote": {
                    "uri": "https://git.openembedded.org/bitbake",
                    "branch": "master",
                    "rev": "master"
                }
            }

      -  Use the internal registry if run from a Git checkout, from a remote
         :term:`BitBake` repository otherwise
         (:bitbake_rev:`675e9076a25248d49f01d7877a78f5a08a9daabc`)

      -  Fragments passed in the ``oe-fragments-one-of`` property can now
         contain descriptions (:bitbake_rev:`29f2cee655be31c401e30ad818a1c4b10458b530`)

      -  Improve the readability of choices during the
         :ref:`bitbake:ref-bbsetup-command-init` command
         (:bitbake_rev:`d9700632bd6b627d1124fdc83ddf7bfb4199228d`)

      -  Enable coloring of the diff outputs when using the
         :ref:`bitbake:ref-bbsetup-command-status` or
         :ref:`bitbake:ref-bbsetup-command-update` commands

      -  The :ref:`bitbake:ref-bbsetup-command-update` now behaves in a
         non-destructive way: local commits and modifications to layers are
         taken into account, and the tool will either stop or warn the user that
         the update is possible or not
         (:bitbake_rev:`2ee3a195bbe1b7458f44a712a271abd9686f90c7`)

      -  Share :ref:`overview-manual/concepts:Shared State` by default between
         builds, by adding a definition for :term:`SSTATE_DIR` and
         :term:`BB_HASHSERVE_DB_DIR` in the ``site.conf`` file created by
         :ref:`bitbake:ref-bbsetup-command-init`
         (:bitbake_rev:`a70c336790a9188aae67975fac6ca13579ad1d3e`)

      -  Generate config files for VSCode by default, unless
         ``--no-init-vscode`` is passed to :ref:`bitbake:ref-bbsetup-command-init`
         (:bitbake_rev:`92fd721941fd17d1febc7205739e9f9ce1bb3aee`)

   -  The ``unpack()`` function (the one containing the logic of the
      :ref:`ref-tasks-unpack` task), can now take an ``update`` argument to
      allow updating a Git repository in-place rather than deleting it and
      re-creating it. An alias function named ``unpack_update()`` was created
      for this unpack mode. See :ref:`bitbake:bb-the-unpack-update` for more
      information
      (:bitbake_rev:`e7d5e156275782948d3346f299780389ab263ab6`)

   -  ``cooker``: Use :term:`bitbake:BB_HASHSERVE_DB_DIR` as hash server
      database location. If unset, the existing behavior is preserved
      (:bitbake_rev:`b339d05ad2b69a6518522ee4c46dd5f5a6e33f65`)

   -  ``bitbake-getvar``: Show close matches when no providers are found
      (:bitbake_rev:`1f8fa7c25e71cd0f230a2f6bfd9d5153c694da81`)

   -  The ``GIT_CONFIG_GLOBAL`` environment variable will now be taken into
      account by the Git fetcher, to allow passing a different set of Git
      configuration options when fetching Git repositories
      (:bitbake_rev:`4c378445969853d6aff4694d937b9af47c7f7300`)

   -  When using the ``subpath`` parameter with the Git fetcher in an
      :term:`SRC_URI`, properly make the ``HEAD`` point to the value specified
      in :term:`SRCREV`.

-  Clang/LLVM related changes:

   -  ``compiler-rt``: 

      -  Remove the need to depend on ``libgcc``
         (:oecore_rev:`f504b6bb8366019d46e48efc5f3fa79859476431`)
      -  Always build C runtime (``crt``) files
         (:oecore_rev:`56fe42abe21eb5a5f809abb9893d8f33dac8bc12`)

   -  ``libcxx``: Remove GNU runtime from dependencies
      (:oecore_rev:`8034509d30657ce40eb0773c4cf39c0e77af84c8`)

   -  ``libcxx/compiler-rt``: Add support for ``llvm-libgcc``, a drop-in
      replacement for ``libgcc`` and ``crt`` files
      (:oecore_rev:`ed02230e3bba030b227e3af0c5438d00800d3457`)

-  SPDX-related changes:

   -  Output SBOM documents now include recipe metadata
      (:oecore_rev:`d999ac407c86b462134008818d5863ecb577f3c6`)

   -  ``spdx30_tasks``: Fix :term:`SPDX_CUSTOM_ANNOTATION_VARS` implementation
      (:oecore_rev:`52ab3b640c6bb7ece34cb4ea6026fd6375f17af4`)

   -  :ref:`ref-classes-kernel`: Add a task to export the kernel configuration
      to SPDX (:oecore_rev:`228a968e7c47d811c06143279bdb0f9c5f374bef`)

   -  Add support for exporting the :term:`PACKAGECONFIG` to SPDX
      (:oecore_rev:`7ec61ac40345a5c0ef1ce20513a4596989c91ef4`)

   -  Add suport for package URLs (PURLs) through :term:`SPDX_PACKAGE_URL`
      (:oecore_rev:`874b2d301d3cac617b1028bc6ce91b1f916a6508`)

   -  ``create-spdx-2.2``: Add CVEs in :term:`CVE_CHECK_IGNORE`
      to the list of fixed CVEs in the output SBOM
      (:oecore_rev:`f8525224cb825b1aad2be240731eabafdde7612d`)

   -  The :ref:`ref-classes-create-spdx` class used to include `VEX
      <https://cyclonedx.org/capabilities/vex/>`__ statements in the SPDX documents
      tied to each packages. This is no longer the case, as these statements are
      now found in the SPDX documents for recipes directly. This was done to
      decrease the duplication of these statements for packages that were generated
      by the same recipe.

      The output SPDX document for an image recipe will still include the VEX
      statements, as the SPDX document for the image also include the recipe SPDX
      metadata.

      The inclusion of VEX statements in SPDX documents can be controlled with the
      :term:`SPDX_INCLUDE_VEX` variable
      (:oecore_rev:`d999ac407c86b462134008818d5863ecb577f3c6`)

-  ``devtool`` changes:

   -  ``ide-sdk``:

      -  Find ``bitbake-setup``'s ``init-build-env`` first, and
         ``oe-init-build-env`` if not found
         (:oecore_rev:`6ab7e9e8e52fa123551438820c59b8c5e0c9c8a5`)

      -  Add `gdbserver` attach mode support
         (:oecore_rev:`119171087681bd47842865d6451868c1127f1149`)

      -  Support GDB pretty-printing for C++ STL types
         (:oecore_rev:`a69e2baba81b0cd88d58b164433c72e1156424b1`)

      -  Support kernel module development
         (:oecore_rev:`aaf15d656db0f83b440de3b22a817355dd8dfebb`)

   -  Add new patches in correct order when running ``devtool finish``
      (:oecore_rev:`fa7877d25826f58a74909908148b6b963dfe6908`)

   -  Prevent ``devtool modify -n`` from corrupting Linux kernel Git repos
      (:oecore_rev:`d383ea37e4987ecabe011226f1a8e658a52ede12`)

-  Patchtest-related changes:

   -  Code refactoring and improvements
      (:oecore_rev:`86d0b2254ae9dc5bf9d19469c7ef71f8129fbf93`,
      :oecore_rev:`317ef42b9b2324847574f62b1ec3627ffcf76e38`,
      :oecore_rev:`6cdb5cbbee6281af1b71407da0d0af74dc7b9631`,
      :oecore_rev:`ae787b32d501d8dce85c26c01229297f9184b7f8`,
      :oecore_rev:`a850252348096e5d6b0bb267e5108bf73de88e85`)

   -  Reject ``Upstream-Status`` after scissors
      (:oecore_rev:`2156ef9e6defa3ec9087789fcea25fb4fee7b83c`)

-  :ref:`ref-classes-insane` / :ref:`ref-classes-sanity` classes related changes:

   -  Reject :term:`TMPDIR` containing redundant slashes to avoid errors in
      executions of :ref:`ref-tasks-populate_sysroot`
      (:oecore_rev:`3e72ebe9ed4e2e5f34eb89cd460e75a9242e296f`)

   -  :ref:`ref-classes-sanity`: Warn when the
      :ref:`overview-manual/concepts:Shared State` cache directory
      (:term:`SSTATE_DIR`) is outside of the build directory (:term:`BUILDDIR`),
      but the :ref:`overview-manual/concepts:Hash Equivalence` database is
      inside it
      (:oecore_rev:`491de0db64a0decd616a9e1c035f105faa14cc3c`)

-  Security changes:

   -  A new document was added to the Yocto Project documentation:
      :doc:`/security-reference/index`. It is intended to document how to report
      vulnerabilities to the Yocto Project security team.

-  :ref:`ref-classes-sbom-cve-check`-related changes:

   -  Escape special characters in CPE 2.3 strings
      (:oecore_rev:`9dd9c0038907340ba08ff4c8ee06a8748c1ac00a`)

-  New :term:`PACKAGECONFIG` options for individual recipes:

   -  ``curl``: ``schannel``
   -  ``gstreamer1.0-plugins-bad``: ``fdkaac``
   -  ``gstreamer1.0-plugins-good``: ``qt6``
   -  ``libinput``: ``lua``, ``libwacom``, ``mtdev``
   -  ``librepo``: ``sequoia``
   -  ``mesa``: ``expat``, ``zlib``
   -  ``openssl``: ``legacy``
   -  ``opkg``: ``acl``, ``xattr``
   -  ``orc``: ``hotdoc``
   -  ``python3``: ``freethreading`` (experimental, see
      :oecore_rev:`c56990178b31b893fbf695eaf6b67de501e9d2e9`)
   -  ``python3-cryptography``: ``legacy-openssl``
   -  ``systemd``: ``osc-context``
   -  ``systemtap``: ``readline``

-  systemd related changes:

   -  Package ``ukify`` separately, with the ``systemd-ukify`` package name
      (:oecore_rev:`e9242749621040accba8252d50c036b3e4b10e09`)

-  U-Boot related changes:

   -  :ref:`ref-classes-uboot-config`: Add support for generating the U-Boot
      initial environment in binary format using
      :term:`UBOOT_INITIAL_ENV_BINARY`
      (:oecore_rev:`cf11b14a4cfc0617f45f7cdb87d1dec4aa58e765`)

   -  A new way of specifying multiple U-Boot configurations has been added
      (:oecore_rev:`cd9e7304481b24b27df61c03ad73496d18e4d47c`). See
      :ref:`ref-migration-6-0-u-boot-config-flow-changes`

-  Miscellaneous changes:

   -  ``curl``: Ensure ``CURL_CA_BUNDLE`` from host environment is respected
      (:oecore_rev:`545e43a7a45be02fda8fc3af69faa20e889f58c4`)

   -  ``weston``: Add PipeWire as runtime dependency when ``pipewire`` is part
      of :term:`PACKAGECONFIG`
      (:oecore_rev:`9f5286725ad4c3ab241ce7992f992d4e81acc81e`)

   -  :ref:`ref-classes-uki`: Use basename of device trees available via
      :term:`KERNEL_DEVICETREE`
      (:oecore_rev:`27a7fbb767c0a25b34a03cae90320908f8ade8de`)

   -  ``rpcbind``: Set the owner of ``/run/rpcbind`` to ``rpc``
      (:oecore_rev:`80e428924715fa954fc68c381cb0aea19e73a5b8`)

   -  :ref:`ref-classes-archiver`: Remove :term:`WORKDIR` from the patch
      directory (:oecore_rev:`c99d22827c9515e9fdb31d4989925aa9e9604134`)

   -  ``gtk4``: Convert to use the :ref:`ref-classes-gnomebase` class
      (:oecore_rev:`fcd5e7c4468fe28b8d1e22ba134346bf92ddbe1c`)

   -  ``udev-extraconf``: Split ``automount`` and ``autonet`` into seperate
      packages (``udev-extraconf-automount`` and ``udev-extraconf-autonet``)
      (:oecore_rev:`08662d71cd357c29c47dc42ead1d9106c584a1b8`)

   -  ``e2fsprogs``: Fix a bug for files larger than 2GB
      (:oecore_rev:`683a1e773899f3042458604b3f136861318c1028`)

   -  ``mesa``: Add support for the ``virtio``, ``gfxstream``, ``hasvk`` Vulkan drivers
      (:oecore_rev:`8e7ffdceded33091e72c9a3ceb239d847cf917a9`,
      :oecore_rev:`3b56f14b2019c42e0efce2a8d10cb7aeaf782da8`)

   -  ``mesa``: Drop :wikipedia:`VDPAU <VDPAU>` remnants in the recipe after
      upstream support was removed (:oecore_rev:`3b05f58586bc3cc156c194342fe1775e567870d1`)

   -  :ref:`ref-classes-cross`: Propagate dependencies to ``outhash``,
      improving :ref:`hash equivalence <overview-manual/concepts:Hash
      Equivalence>` (:oecore_rev:`267b651e875d9381a23ffd5757d426714c029409`)

   -  ``run-postinsts``: Propagate exit status to the ``run-postinsts.service``
      systemd service (:oecore_rev:`7f74d88bb628f186309c9228cf01293b046e43ca`)

   -  ``freetype``: Use :ref:`ref-classes-meson` instead of
      :ref:`ref-classes-autotools` (:oecore_rev:`7395e4f99e90063dbb9c07b62ddffd824fba84fc`)

   -  ``wpa-supplicant``:

      -  Build with :wikipedia:`OWE <Opportunistic_Wireless_Encryption>` support
         by default (:oecore_rev:`d16c66b4efada276536ccd3c8456f02ab9753e2e`)

      -  Build with :wikipedia:`802.11be <Wi-Fi_7>` support by default
         (:oecore_rev:`d16c66b4efada276536ccd3c8456f02ab9753e2e`)

   -  ``overlayfs``: Remove helper unit
      (:oecore_rev:`623c20ff1e989730138c3fbe6e8247eaada20707`)

   -  :ref:`ref-classes-patch`: Show full path when a patch fails to apply
      (:oecore_rev:`602e28b4813479f87d3f949e5cf23b34fc34a478`)

   -  ``kea``: Replace ``keactrl`` with `kea` daemons (``kea-dhcp*``) in
      initscripts (:oecore_rev:`7f9d9297a84f8f5dc08bc310f825ac8a4acf5452`), and
      remove ``keactrl`` from the recipe
      (:oecore_rev:`08c3877f4df8392ae347b03ac5334b170b1a4fec`)

   -  ``initramfs-framework``:

      -  Add handover of PID 1's arguments to modules
         (:oecore_rev:`a0ab3d1c4f9ed34d1d17e6534f42d17b3387ebb3`)

      -  Fix Linux kernel command line parsing when passing in double quotes
         (:oecore_rev:`f9acaf1c130220859150bb4c0a0635fca2ad8487`)

   -  ``perl``: Provide ``pod2man`` (in the recipe's :term:`PROVIDES`
      definition). This is used by many other recipes to produce :wikipedia:`man
      pages <Man_page>`. This allows existing recipes to explicitly depend on
      ``pod2man-native`` to produce man pages
      (:oecore_rev:`1d1e55d200fb0363d1cb96cc1323a407f3b93349`)

   -  ``build-sysroots``: Add sysroot tasks to default build and remove warning
      (:oecore_rev:`e73f1509552285b628477267824e48eb79790fb7`)

   -  Licenses and manifests are now deployed in the SDK when setting
      :term:`COPY_LIC_DIRS` and/or :term:`COPY_LIC_MANIFEST`, for both host and
      target sysroots (:oecore_rev:`f757ae4dadabd09cfa056bd34172c09ca3693441`)

   -  ``openssl``: Disable TLS 1.0/1.1 by default
      (:oecore_rev:`d5501e77208825b6ebffe51e8d680cdd84cfd0ab`)

   -  ``python3-cryptography``: Disable ``legacy-openssl`` feature by default
      (:oecore_rev:`1acd1998bbaf6b346c756aea7c11916e5e22fbbb`)

   -  ``openssl``: Add support for config snippet includes. This can be done by
      installing extra configuration files in ``${sysconfdir}/ssl/openssl.cnf.d/``
      (:oecore_rev:`34bafcf3d8cdaa87506df30ef554d18981454c5e`)

   -  ``busybox``:

      -  Enable SELinux support if :term:`DISTRO_FEATURES` contains
         ``selinux`` (:oecore_rev:`c544f12073ea712c3d3ce08105d52640a7a322b9`)

      -  Do not build SUID binary without an applet
         (:oecore_rev:`1406f9523c104d5357ce9594737c3bd32625b068`)

   -  ``coreutils``: ``kill`` and ``uptime`` are no longer provided by the
      recipe (:oecore_rev:`cedeb958dfa892e409bdce8525030c20b3400332`)

   -  ``tcl8``: Skip timing-sensitive :ref:`ptests <ref-classes-ptest>`
      (:oecore_rev:`1b93479f7b5ae4e2a62f929386e516adabbce46b`)

   -  ``license_image.bbclass``: Report all packages with incompatible license
      (when using :term:`INCOMPATIBLE_LICENSE`)
      (:oecore_rev:`57fe3e411faec8cc60853f2e499661f9ede4f453`)

   -  ``python3``: Package all of the compression module into
      ``python3-compression``
      (:oecore_rev:`5f346802198f14d4c315783dea6a55743e34a2e8`)

   -  ``gobject-introspection``: Disable cache for the scanner during
      the :ref:`ref-tasks-compile` task (``GI_SCANNER_DISABLE_CACHE=1``), to fix
      an intermittent build failure
      (:oecore_rev:`2b55dd12fc9593beba20d684c8b143483e212bc6`)

   -  :ref:`ref-classes-archiver`: Don't try to preserve all attributes when
      copying files (:oecore_rev:`6e8313688fa994c82e4c846993ed8da0d1f4db0e`)

   -  :ref:`ref-classes-useradd`: allow inheriting the class with only
      :term:`USERADD_DEPENDS` set, when a recipe only depends on users/groups
      created by another (:oecore_rev:`09a901b9874f76e665fb4ba9e537703a792011e3`)

   -  ``vim``: disable `GTK+3` UI by default
      (:oecore_rev:`a07763f03d4faacca4470e4f1f80f766ed068296`)

Known Issues in |yocto-ver|
---------------------------

-  A known bug is affecting :term:`build hosts <Build Host>` that have Intel
   Ultra 7 CPUs and breaks :term:`OpenEmbedded-Core (OE-Core)` tests that
   involve KVM. See bug :yocto_bug:`16074` for more information.

Recipe License changes in |yocto-ver|
-------------------------------------

..
   Going through commits on OE-Core filtered by License-Update:
   git log -U0 --patch --grep "License-Update:" yocto-5.3..origin/master

The following changes have been made to the :term:`LICENSE` values set by recipes:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe(s)
     - Previous value
     - New value
   * - ``libxcrypt-compat``, ``libxcrypt``
     - ``LGPL-2.1-only``
     - ``LGPL-2.1-only & 0BSD & BSD-3-Clause``
   * - ``libpcre2``
     - ``BSD-3-Clause``
     - ``BSD-3-Clause & BSD-2-Clause & MIT``
   * - ``libtest-fatal-perl``
     - ``Artistic-1.0 | GPL-1.0-or-later``
     - ``Artistic-1.0-Perl | GPL-1.0-or-later``
   * - ``python3-cffi``
     - ``MIT``
     - ``MIT-0``
   * - ``icu``
     - ``ICU``
     - ``ICU & MIT``
   * - ``iso-code``
     - ``LGPL-2.1-only``
     - ``LGPL-2.1-or-later``
   * - ``ruby``
     - ``Ruby | BSD-2-Clause | BSD-3-Clause | GPL-2.0-only | ISC | MIT``
     - ``Ruby | BSD-2-Clause | BSD-3-Clause | GPL-2.0-only | ISC | MIT | BSL-1.0 | Apache-2.0``

Security Fixes in |yocto-ver|
-----------------------------

..
   Generated with documentation/tools/gen-cve-release-notes

The following CVEs have been fixed:

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Recipe
     - CVE IDs
   * - ``avahi``
     - :cve_nist:`2025-59529`, :cve_nist:`2026-34933`
   * - ``binutils``
     - :cve_nist:`2025-69644`, :cve_nist:`2025-69647`, :cve_nist:`2025-69648`, :cve_nist:`2025-69649`, :cve_nist:`2025-69650`, :cve_nist:`2025-69651`, :cve_nist:`2025-69652`, :cve_nist:`2026-3441`, :cve_nist:`2026-3442`, :cve_nist:`2026-4647`
   * - ``binutils-cross-x86_64``
     - :cve_nist:`2025-69644`, :cve_nist:`2025-69647`, :cve_nist:`2025-69648`, :cve_nist:`2025-69649`, :cve_nist:`2025-69650`, :cve_nist:`2025-69651`, :cve_nist:`2025-69652`, :cve_nist:`2026-3441`, :cve_nist:`2026-3442`, :cve_nist:`2026-4647`
   * - ``binutils-testsuite``
     - :cve_nist:`2025-69644`, :cve_nist:`2025-69647`, :cve_nist:`2025-69648`, :cve_nist:`2025-69649`, :cve_nist:`2025-69650`, :cve_nist:`2025-69651`, :cve_nist:`2025-69652`, :cve_nist:`2026-3441`, :cve_nist:`2026-3442`, :cve_nist:`2026-4647`
   * - ``cargo``
     - :cve_nist:`2026-39837`, :cve_nist:`2026-39839`, :cve_nist:`2026-39840`, :cve_nist:`2026-39841`
   * - ``cups``
     - :cve_nist:`2026-34978`, :cve_nist:`2026-34979`, :cve_nist:`2026-34980`, :cve_nist:`2026-34990`, :cve_nist:`2026-39314`, :cve_nist:`2026-39316`
   * - ``ffmpeg``
     - :cve_nist:`2025-69693`, :cve_nist:`2026-40962`
   * - ``glibc``
     - :cve_nist:`2026-4046`, :cve_nist:`2026-4437`, :cve_nist:`2026-4438`
   * - ``go``
     - :cve_nist:`2026-27140`, :cve_nist:`2026-27143`, :cve_nist:`2026-27144`, :cve_nist:`2026-32280`, :cve_nist:`2026-32281`, :cve_nist:`2026-32282`, :cve_nist:`2026-32283`, :cve_nist:`2026-32288`, :cve_nist:`2026-32289`
   * - ``go-binary-native``
     - :cve_nist:`2026-27140`, :cve_nist:`2026-27143`, :cve_nist:`2026-27144`, :cve_nist:`2026-32280`, :cve_nist:`2026-32281`, :cve_nist:`2026-32282`, :cve_nist:`2026-32283`, :cve_nist:`2026-32288`, :cve_nist:`2026-32289`
   * - ``go-cross-x86-64-v3``
     - :cve_nist:`2026-27140`, :cve_nist:`2026-27143`, :cve_nist:`2026-27144`, :cve_nist:`2026-32280`, :cve_nist:`2026-32281`, :cve_nist:`2026-32282`, :cve_nist:`2026-32283`, :cve_nist:`2026-32288`, :cve_nist:`2026-32289`
   * - ``go-runtime``
     - :cve_nist:`2026-27140`, :cve_nist:`2026-27143`, :cve_nist:`2026-27144`, :cve_nist:`2026-32280`, :cve_nist:`2026-32281`, :cve_nist:`2026-32282`, :cve_nist:`2026-32283`, :cve_nist:`2026-32288`, :cve_nist:`2026-32289`
   * - ``gstreamer1.0``
     - :cve_nist:`2026-2920`, :cve_nist:`2026-2921`, :cve_nist:`2026-2922`, :cve_nist:`2026-2923`, :cve_nist:`2026-3081`, :cve_nist:`2026-3082`, :cve_nist:`2026-3083`, :cve_nist:`2026-3084`, :cve_nist:`2026-3085`, :cve_nist:`2026-3086`
   * - ``libarchive``
     - :cve_nist:`2026-5121`
   * - ``libexif``
     - :cve_nist:`2026-40385`, :cve_nist:`2026-40386`
   * - ``libinput``
     - :cve_nist:`2026-35093`, :cve_nist:`2026-35094`
   * - ``libpng``
     - :cve_nist:`2026-33416`, :cve_nist:`2026-33636`
   * - ``libsndfile1``
     - :cve_nist:`2024-50613`, :cve_nist:`2025-52194`
   * - ``libsoup``
     - :cve_nist:`2026-1467`, :cve_nist:`2026-1536`, :cve_nist:`2026-1539`, :cve_nist:`2026-1801`, :cve_nist:`2026-2443`, :cve_nist:`2026-3099`, :cve_nist:`2026-3632`, :cve_nist:`2026-3633`, :cve_nist:`2026-3634`, :cve_nist:`2026-4271`, :cve_nist:`2026-5119`
   * - ``linux-yocto``
     - :cve_nist:`2019-14899`, :cve_nist:`2021-3714`, :cve_nist:`2021-3864`, :cve_nist:`2022-0400`, :cve_nist:`2022-1247`, :cve_nist:`2022-4543`, :cve_nist:`2023-3397`, :cve_nist:`2023-3640`, :cve_nist:`2023-4010`, :cve_nist:`2023-6238`, :cve_nist:`2023-6240`, :cve_nist:`2025-40039`, :cve_nist:`2025-40040`, :cve_nist:`2025-40082`, :cve_nist:`2025-40149`, :cve_nist:`2025-40164`, :cve_nist:`2025-40251`, :cve_nist:`2025-68211`, :cve_nist:`2025-68214`, :cve_nist:`2025-68223`, :cve_nist:`2025-68333`, :cve_nist:`2025-68340`, :cve_nist:`2025-68351`, :cve_nist:`2025-68358`, :cve_nist:`2025-68365`, :cve_nist:`2025-68725`, :cve_nist:`2025-68749`, :cve_nist:`2025-68817`, :cve_nist:`2025-68823`, :cve_nist:`2025-71071`, :cve_nist:`2025-71072`, :cve_nist:`2025-71073`, :cve_nist:`2025-71074`, :cve_nist:`2025-71075`, :cve_nist:`2025-71076`, :cve_nist:`2025-71077`, :cve_nist:`2025-71078`, :cve_nist:`2025-71079`, :cve_nist:`2025-71080`, :cve_nist:`2025-71081`, :cve_nist:`2025-71082`, :cve_nist:`2025-71083`, :cve_nist:`2025-71084`, :cve_nist:`2025-71085`, :cve_nist:`2025-71086`, :cve_nist:`2025-71087`, :cve_nist:`2025-71088`, :cve_nist:`2025-71089`, :cve_nist:`2025-71091`, :cve_nist:`2025-71093`, :cve_nist:`2025-71094`, :cve_nist:`2025-71095`, :cve_nist:`2025-71096`, :cve_nist:`2025-71097`, :cve_nist:`2025-71098`, :cve_nist:`2025-71099`, :cve_nist:`2025-71100`, :cve_nist:`2025-71101`, :cve_nist:`2025-71102`, :cve_nist:`2025-71104`, :cve_nist:`2025-71105`, :cve_nist:`2025-71107`, :cve_nist:`2025-71108`, :cve_nist:`2025-71109`, :cve_nist:`2025-71111`, :cve_nist:`2025-71112`, :cve_nist:`2025-71113`, :cve_nist:`2025-71114`, :cve_nist:`2025-71115`, :cve_nist:`2025-71116`, :cve_nist:`2025-71117`, :cve_nist:`2025-71118`, :cve_nist:`2025-71119`, :cve_nist:`2025-71120`, :cve_nist:`2025-71121`, :cve_nist:`2025-71122`, :cve_nist:`2025-71124`, :cve_nist:`2025-71125`, :cve_nist:`2025-71126`, :cve_nist:`2025-71127`, :cve_nist:`2025-71128`, :cve_nist:`2025-71129`, :cve_nist:`2025-71130`, :cve_nist:`2025-71131`, :cve_nist:`2025-71132`, :cve_nist:`2025-71133`, :cve_nist:`2025-71134`, :cve_nist:`2025-71135`, :cve_nist:`2025-71136`, :cve_nist:`2025-71137`, :cve_nist:`2025-71138`, :cve_nist:`2025-71141`, :cve_nist:`2025-71142`, :cve_nist:`2025-71143`, :cve_nist:`2025-71147`, :cve_nist:`2025-71148`, :cve_nist:`2025-71149`, :cve_nist:`2025-71150`, :cve_nist:`2025-71151`, :cve_nist:`2025-71152`, :cve_nist:`2025-71153`, :cve_nist:`2025-71154`, :cve_nist:`2025-71156`, :cve_nist:`2025-71157`, :cve_nist:`2025-71158`, :cve_nist:`2025-71160`, :cve_nist:`2025-71161`, :cve_nist:`2025-71162`, :cve_nist:`2025-71163`, :cve_nist:`2025-71180`, :cve_nist:`2025-71182`, :cve_nist:`2025-71183`, :cve_nist:`2025-71184`, :cve_nist:`2025-71185`, :cve_nist:`2025-71186`, :cve_nist:`2025-71187`, :cve_nist:`2025-71188`, :cve_nist:`2025-71189`, :cve_nist:`2025-71190`, :cve_nist:`2025-71191`, :cve_nist:`2025-71200`, :cve_nist:`2025-71201`, :cve_nist:`2025-71202`, :cve_nist:`2025-71203`, :cve_nist:`2025-71204`, :cve_nist:`2025-71220`, :cve_nist:`2025-71221`, :cve_nist:`2025-71222`, :cve_nist:`2025-71223`, :cve_nist:`2025-71225`, :cve_nist:`2025-71227`, :cve_nist:`2025-71229`, :cve_nist:`2025-71230`, :cve_nist:`2025-71231`, :cve_nist:`2025-71232`, :cve_nist:`2025-71233`, :cve_nist:`2025-71234`, :cve_nist:`2025-71235`, :cve_nist:`2025-71236`, :cve_nist:`2025-71237`, :cve_nist:`2025-71238`, :cve_nist:`2026-22976`, :cve_nist:`2026-22977`, :cve_nist:`2026-22978`, :cve_nist:`2026-22979`, :cve_nist:`2026-22980`, :cve_nist:`2026-22981`, :cve_nist:`2026-22982`, :cve_nist:`2026-22984`, :cve_nist:`2026-22985`, :cve_nist:`2026-22986`, :cve_nist:`2026-22989`, :cve_nist:`2026-22990`, :cve_nist:`2026-22991`, :cve_nist:`2026-22992`, :cve_nist:`2026-22993`, :cve_nist:`2026-22994`, :cve_nist:`2026-22996`, :cve_nist:`2026-22997`, :cve_nist:`2026-22998`, :cve_nist:`2026-22999`, :cve_nist:`2026-23000`, :cve_nist:`2026-23001`, :cve_nist:`2026-23002`, :cve_nist:`2026-23003`, :cve_nist:`2026-23005`, :cve_nist:`2026-23006`, :cve_nist:`2026-23007`, :cve_nist:`2026-23008`, :cve_nist:`2026-23009`, :cve_nist:`2026-23010`, :cve_nist:`2026-23011`, :cve_nist:`2026-23013`, :cve_nist:`2026-23015`, :cve_nist:`2026-23017`, :cve_nist:`2026-23018`, :cve_nist:`2026-23019`, :cve_nist:`2026-23020`, :cve_nist:`2026-23021`, :cve_nist:`2026-23023`, :cve_nist:`2026-23025`, :cve_nist:`2026-23026`, :cve_nist:`2026-23060`, :cve_nist:`2026-23061`, :cve_nist:`2026-23062`, :cve_nist:`2026-23063`, :cve_nist:`2026-23064`, :cve_nist:`2026-23065`, :cve_nist:`2026-23066`, :cve_nist:`2026-23067`, :cve_nist:`2026-23068`, :cve_nist:`2026-23069`, :cve_nist:`2026-23070`, :cve_nist:`2026-23071`, :cve_nist:`2026-23072`, :cve_nist:`2026-23073`, :cve_nist:`2026-23074`, :cve_nist:`2026-23075`, :cve_nist:`2026-23076`, :cve_nist:`2026-23077`, :cve_nist:`2026-23078`, :cve_nist:`2026-23080`, :cve_nist:`2026-23081`, :cve_nist:`2026-23083`, :cve_nist:`2026-23084`, :cve_nist:`2026-23085`, :cve_nist:`2026-23086`, :cve_nist:`2026-23087`, :cve_nist:`2026-23088`, :cve_nist:`2026-23089`, :cve_nist:`2026-23090`, :cve_nist:`2026-23091`, :cve_nist:`2026-23092`, :cve_nist:`2026-23093`, :cve_nist:`2026-23094`, :cve_nist:`2026-23095`, :cve_nist:`2026-23096`, :cve_nist:`2026-23097`, :cve_nist:`2026-23098`, :cve_nist:`2026-23099`, :cve_nist:`2026-23100`, :cve_nist:`2026-23101`, :cve_nist:`2026-23102`, :cve_nist:`2026-23103`, :cve_nist:`2026-23104`, :cve_nist:`2026-23105`, :cve_nist:`2026-23107`, :cve_nist:`2026-23108`, :cve_nist:`2026-23109`, :cve_nist:`2026-23110`, :cve_nist:`2026-23111`, :cve_nist:`2026-23112`, :cve_nist:`2026-23113`, :cve_nist:`2026-23114`, :cve_nist:`2026-23115`, :cve_nist:`2026-23116`, :cve_nist:`2026-23118`, :cve_nist:`2026-23119`, :cve_nist:`2026-23120`, :cve_nist:`2026-23121`, :cve_nist:`2026-23122`, :cve_nist:`2026-23123`, :cve_nist:`2026-23124`, :cve_nist:`2026-23125`, :cve_nist:`2026-23126`, :cve_nist:`2026-23128`, :cve_nist:`2026-23129`, :cve_nist:`2026-23130`, :cve_nist:`2026-23131`, :cve_nist:`2026-23133`, :cve_nist:`2026-23135`, :cve_nist:`2026-23136`, :cve_nist:`2026-23137`, :cve_nist:`2026-23138`, :cve_nist:`2026-23139`, :cve_nist:`2026-23140`, :cve_nist:`2026-23141`, :cve_nist:`2026-23142`, :cve_nist:`2026-23143`, :cve_nist:`2026-23144`, :cve_nist:`2026-23146`, :cve_nist:`2026-23147`, :cve_nist:`2026-23148`, :cve_nist:`2026-23150`, :cve_nist:`2026-23151`, :cve_nist:`2026-23152`, :cve_nist:`2026-23154`, :cve_nist:`2026-23156`, :cve_nist:`2026-23157`, :cve_nist:`2026-23158`, :cve_nist:`2026-23160`, :cve_nist:`2026-23161`, :cve_nist:`2026-23163`, :cve_nist:`2026-23164`, :cve_nist:`2026-23166`, :cve_nist:`2026-23167`, :cve_nist:`2026-23168`, :cve_nist:`2026-23169`, :cve_nist:`2026-23170`, :cve_nist:`2026-23171`, :cve_nist:`2026-23172`, :cve_nist:`2026-23173`, :cve_nist:`2026-23186`, :cve_nist:`2026-23187`, :cve_nist:`2026-23188`, :cve_nist:`2026-23190`, :cve_nist:`2026-23191`, :cve_nist:`2026-23192`, :cve_nist:`2026-23193`, :cve_nist:`2026-23195`, :cve_nist:`2026-23196`, :cve_nist:`2026-23197`, :cve_nist:`2026-23198`, :cve_nist:`2026-23199`, :cve_nist:`2026-23201`, :cve_nist:`2026-23204`, :cve_nist:`2026-23205`, :cve_nist:`2026-23206`, :cve_nist:`2026-23208`, :cve_nist:`2026-23209`, :cve_nist:`2026-23210`, :cve_nist:`2026-23212`, :cve_nist:`2026-23213`, :cve_nist:`2026-23214`, :cve_nist:`2026-23215`, :cve_nist:`2026-23216`, :cve_nist:`2026-23217`, :cve_nist:`2026-23219`, :cve_nist:`2026-23220`, :cve_nist:`2026-23221`, :cve_nist:`2026-23222`, :cve_nist:`2026-23223`, :cve_nist:`2026-23224`, :cve_nist:`2026-23226`, :cve_nist:`2026-23227`, :cve_nist:`2026-23228`, :cve_nist:`2026-23229`, :cve_nist:`2026-23230`, :cve_nist:`2026-23231`, :cve_nist:`2026-23233`, :cve_nist:`2026-23234`, :cve_nist:`2026-23235`, :cve_nist:`2026-23236`, :cve_nist:`2026-23237`, :cve_nist:`2026-23238`
   * - ``mesa``
     - :cve_nist:`2026-40393`
   * - ``nfs-utils``
     - :cve_nist:`2025-12801`
   * - ``nghttp2``
     - :cve_nist:`2026-27135`
   * - ``openssh``
     - :cve_nist:`2026-35414`
   * - ``python3``
     - :cve_nist:`2026-4519`
   * - ``python3-requests``
     - :cve_nist:`2026-25645`
   * - ``qemu``
     - :cve_nist:`2024-6519`
   * - ``qemu-system-native``
     - :cve_nist:`2024-6519`
   * - ``sqlite3``
     - :cve_nist:`2025-70873`
   * - ``systemd-boot``
     - :cve_nist:`2026-29111`, :cve_nist:`2026-40226`
   * - ``vim``
     - :cve_nist:`2026-28417`, :cve_nist:`2026-28418`, :cve_nist:`2026-28419`, :cve_nist:`2026-28420`, :cve_nist:`2026-28421`, :cve_nist:`2026-28422`, :cve_nist:`2026-33412`, :cve_nist:`2026-34714`, :cve_nist:`2026-35177`
   * - ``xz``
     - :cve_nist:`2026-34743`

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
   * - ``acpica``
     - 20250807
     - 20251212
   * - ``adwaita-icon-theme``
     - 48.0
     - 49.0
   * - ``alsa-lib``
     - 1.2.14
     - 1.2.15.3
   * - ``alsa-tools``
     - 1.2.14
     - 1.2.15
   * - ``alsa-ucm-conf``
     - 1.2.14
     - 1.2.15.3
   * - ``alsa-utils``
     - 1.2.14
     - 1.2.15.2
   * - ``appstream``
     - 1.0.6
     - 1.1.2
   * - ``aspell``
     - 0.60.8.1
     - 0.60.8.2
   * - ``at-spi2-core``
     - 2.56.4
     - 2.60.0
   * - ``autoconf``
     - 2.72
     - 2.73
   * - ``barebox``
     - 2025.09.3
     - 2026.04.0
   * - ``barebox-tools``
     - 2025.09.3
     - 2026.04.0
   * - ``base-passwd``
     - 3.6.7
     - 3.6.8
   * - ``bash-completion``
     - 2.16.0
     - 2.17.0
   * - ``bind``
     - 9.20.15
     - 9.20.22
   * - ``binutils``
     - 2.45.1+git
     - 2.46
   * - ``binutils-cross``
     - 2.45.1+git
     - 2.46
   * - ``binutils-cross-canadian``
     - 2.45.1+git
     - 2.46
   * - ``binutils-crosssdk``
     - 2.45.1+git
     - 2.46
   * - ``binutils-testsuite``
     - 2.45.1+git
     - 2.46
   * - ``bluez5``
     - 5.84
     - 5.86
   * - ``boost``
     - 1.89.0
     - 1.90.0
   * - ``boost-build-native``
     - 1.89.0
     - 1.90.0
   * - ``btrfs-tools``
     - 6.16
     - 6.19.1
   * - ``cargo``
     - 1.90.0
     - 1.94.1
   * - ``cargo-c``
     - 0.10.16+cargo-0.91.0
     - 0.10.21+cargo-0.95.0
   * - ``ccache``
     - 4.12.3
     - 4.13.2
   * - ``clang``
     - 21.1.7
     - 22.1.3
   * - ``cmake``
     - 4.1.2
     - 4.3.1
   * - ``cmake-native``
     - 4.1.2
     - 4.3.1
   * - ``compiler-rt``
     - 21.1.7
     - 22.1.3
   * - ``compiler-rt-sanitizers``
     - 21.1.7
     - 22.1.3
   * - ``connman``
     - 1.45
     - 2.0
   * - ``coreutils``
     - 9.7
     - 9.10
   * - ``createrepo-c``
     - 1.2.1
     - 1.2.3
   * - ``cross-localedef-native``
     - 2.42+git
     - 2.43+git
   * - ``cryptodev-linux``
     - 1.14 (135cbff90af2…)
     - 1.14 (08644db02d43…)
   * - ``cryptodev-module``
     - 1.14 (135cbff90af2…)
     - 1.14 (08644db02d43…)
   * - ``cryptodev-tests``
     - 1.14 (135cbff90af2…)
     - 1.14 (08644db02d43…)
   * - ``cups``
     - 2.4.15
     - 2.4.16
   * - ``curl``
     - 8.17.0
     - 8.19.0
   * - ``dhcpcd``
     - 10.2.4
     - 10.3.0
   * - ``diffoscope``
     - 306
     - 314
   * - ``dmidecode``
     - 3.6
     - 3.7
   * - ``dnf``
     - 4.23.0
     - 4.24.0
   * - ``dos2unix``
     - 7.5.2
     - 7.5.4
   * - ``dpkg``
     - 1.22.21
     - 1.23.7
   * - ``dropbear``
     - 2025.88
     - 2025.89
   * - ``e2fsprogs``
     - 1.47.3
     - 1.47.4
   * - ``ed``
     - 1.22.2
     - 1.22.5
   * - ``elfutils``
     - 0.193
     - 0.194
   * - ``ell``
     - 0.80
     - 0.83
   * - ``enchant2``
     - 2.8.14
     - 2.8.15
   * - ``epiphany``
     - 48.5
     - 49.7
   * - ``erofs-utils``
     - 1.8.10
     - 1.9.1
   * - ``ethtool``
     - 6.15
     - 6.19
   * - ``expat``
     - 2.7.4
     - 2.7.5
   * - ``fastfloat``
     - 8.0.2
     - 8.2.4
   * - ``ffmpeg``
     - 8.0
     - 8.0.1
   * - ``file``
     - 5.46
     - 5.47
   * - ``fmt``
     - 11.2.0
     - 12.1.0
   * - ``font-alias``
     - 1.0.5
     - 1.0.6
   * - ``freetype``
     - 2.13.3
     - 2.14.3
   * - ``gawk``
     - 5.3.2
     - 5.4.0
   * - ``gdb``
     - 16.3
     - 17.1
   * - ``gdb-cross``
     - 16.3
     - 17.1
   * - ``gdb-cross-canadian``
     - 16.3
     - 17.1
   * - ``gdk-pixbuf``
     - 2.42.12
     - 2.44.5
   * - ``gettext``
     - 0.26
     - 1.0
   * - ``gettext-minimal-native``
     - 0.26
     - 1.0
   * - ``gi-docgen``
     - 2025.4
     - 2026.1
   * - ``git``
     - 2.51.0
     - 2.53.0
   * - ``glew``
     - 2.2.0
     - 2.3.1
   * - ``glib-2.0``
     - 2.86.4
     - 2.88.0
   * - ``glib-2.0-initial``
     - 2.86.4
     - 2.88.0
   * - ``glibc``
     - 2.42+git
     - 2.43+git
   * - ``glibc-locale``
     - 2.42+git
     - 2.43+git
   * - ``glibc-mtrace``
     - 2.42+git
     - 2.43+git
   * - ``glibc-scripts``
     - 2.42+git
     - 2.43+git
   * - ``glibc-testsuite``
     - 2.42+git
     - 2.43+git
   * - ``glslang``
     - 1.4.328.1
     - 1.4.341.0
   * - ``gn``
     - 0+git (81b24e01531e…)
     - 0+git (9d19a7870add…)
   * - ``gnu-efi``
     - 4.0.2
     - 4.0.4
   * - ``gnupg``
     - 2.5.11
     - 2.5.17
   * - ``gnutls``
     - 3.8.10
     - 3.8.12
   * - ``go``
     - 1.25.9
     - 1.26.2
   * - ``go-binary-native``
     - 1.25.9
     - 1.26.2
   * - ``go-cross-canadian``
     - 1.25.9
     - 1.26.2
   * - ``go-cross-core2-32``
     - 1.25.9
     - 1.26.2
   * - ``go-crosssdk``
     - 1.25.9
     - 1.26.2
   * - ``go-helloworld``
     - 0.1 (8b405629c4a5…)
     - 0.1 (7f05d217867b…)
   * - ``go-runtime``
     - 1.25.9
     - 1.26.2
   * - ``gobject-introspection``
     - 1.84.0
     - 1.86.0
   * - ``groff``
     - 1.23.0
     - 1.24.0
   * - ``grub``
     - 2.12
     - 2.14
   * - ``grub-efi``
     - 2.12
     - 2.14
   * - ``gsettings-desktop-schemas``
     - 48.0
     - 50.0
   * - ``gst-devtools``
     - 1.26.7
     - 1.28.2
   * - ``gst-examples``
     - 1.26.7
     - 1.28.2
   * - ``gstreamer1.0``
     - 1.26.7
     - 1.28.2
   * - ``gstreamer1.0-libav``
     - 1.26.7
     - 1.28.2
   * - ``gstreamer1.0-plugins-bad``
     - 1.26.7
     - 1.28.2
   * - ``gstreamer1.0-plugins-base``
     - 1.26.7
     - 1.28.2
   * - ``gstreamer1.0-plugins-good``
     - 1.26.7
     - 1.28.2
   * - ``gstreamer1.0-plugins-ugly``
     - 1.26.7
     - 1.28.2
   * - ``gstreamer1.0-python``
     - 1.26.7
     - 1.28.2
   * - ``gstreamer1.0-rtsp-server``
     - 1.26.7
     - 1.28.2
   * - ``gtk-doc``
     - 1.34.0
     - 1.35.1
   * - ``gtk4``
     - 4.18.6
     - 4.22.1
   * - ``harfbuzz``
     - 11.4.5
     - 12.3.2
   * - ``hwdata``
     - 0.399
     - 0.405
   * - ``hwlatdetect``
     - 2.9
     - 2.10
   * - ``icu``
     - 77-1
     - 78.3
   * - ``ifupdown``
     - 0.8.44
     - 0.8.45
   * - ``igt-gpu-tools``
     - 2.1
     - 2.3
   * - ``inetutils``
     - 2.6
     - 2.7
   * - ``iproute2``
     - 6.16.0
     - 6.19.0
   * - ``iptables``
     - 1.8.11
     - 1.8.13
   * - ``iso-codes``
     - 4.18.0
     - 4.20.1
   * - ``kbd``
     - 2.8.0
     - 2.9.0
   * - ``kea``
     - 3.0.1
     - 3.0.3
   * - ``kern-tools-native``
     - 0.3+git (f589e1df2325…)
     - 0.3+git (a4a362d9f4f0…)
   * - ``kexec-tools``
     - 2.0.31
     - 2.0.32
   * - ``kmscube``
     - 0.0.1+git (2c1f2646c5e5…)
     - 0.0.1+git (f60e50e887d3…)
   * - ``less``
     - 679
     - 692
   * - ``libadwaita``
     - 1.7.6
     - 1.8.4
   * - ``libarchive``
     - 3.8.6
     - 3.8.7
   * - ``libatomic-ops``
     - 7.8.4
     - 7.10.0
   * - ``libcap``
     - 2.76
     - 2.77
   * - ``libcap-ng``
     - 0.8.5
     - 0.9.1
   * - ``libcap-ng-python``
     - 0.8.5
     - 0.9.1
   * - ``libclc``
     - 21.1.7
     - 22.1.3
   * - ``libcomps``
     - 0.1.22
     - 0.1.24
   * - ``libcxx``
     - 21.1.7
     - 22.1.3
   * - ``libdisplay-info``
     - 0.2.0
     - 0.3.0
   * - ``libdnf``
     - 0.74.0
     - 0.75.0
   * - ``libdrm``
     - 2.4.125
     - 2.4.131
   * - ``libedit``
     - 20250104-3.1
     - 20251016-3.1
   * - ``libevdev``
     - 1.13.5
     - 1.13.6
   * - ``libexif``
     - 0.6.25
     - 0.6.26
   * - ``libfontenc``
     - 1.1.8
     - 1.1.9
   * - ``libgcrypt``
     - 1.11.2
     - 1.12.1
   * - ``libgit2``
     - 1.9.1
     - 1.9.2
   * - ``libgloss``
     - 4.5.0+git
     - 4.6.0+git
   * - ``libgpg-error``
     - 1.56
     - 1.59
   * - ``libinput``
     - 1.29.1
     - 1.30.2
   * - ``libjpeg-turbo``
     - 3.1.2
     - 3.1.3
   * - ``libksba``
     - 1.6.7
     - 1.6.8
   * - ``libnl``
     - 3.11.0
     - 3.12.0
   * - ``libnotify``
     - 0.8.6
     - 0.8.8
   * - ``libpam``
     - 1.7.1
     - 1.7.2
   * - ``libpciaccess``
     - 0.18.1
     - 0.19
   * - ``libpcre2``
     - 10.46
     - 10.47
   * - ``libproxy``
     - 0.5.10
     - 0.5.12
   * - ``librsvg``
     - 2.61.0
     - 2.61.3
   * - ``libsolv``
     - 0.7.35
     - 0.7.36
   * - ``libstd-rs``
     - 1.90.0
     - 1.94.1
   * - ``libtasn1``
     - 4.20.0
     - 4.21.0
   * - ``libtest-fatal-perl``
     - 0.017
     - 0.018
   * - ``libtirpc``
     - 1.3.6
     - 1.3.7
   * - ``libtraceevent``
     - 1.8.4
     - 1.9.0
   * - ``libubootenv``
     - 0.3.6
     - 0.3.7
   * - ``libunistring``
     - 1.3
     - 1.4.2
   * - ``liburcu``
     - 0.15.3
     - 0.15.6
   * - ``libuv``
     - 1.51.0
     - 1.52.1
   * - ``libva``
     - 2.22.0
     - 2.23.0
   * - ``libva-initial``
     - 2.22.0
     - 2.23.0
   * - ``libva-utils``
     - 2.22.0
     - 2.23.0
   * - ``libx11``
     - 1.8.12
     - 1.8.13
   * - ``libx11-compose-data``
     - 1.8.4
     - 1.8.12
   * - ``libxcomposite``
     - 0.4.6
     - 0.4.7
   * - ``libxcrypt``
     - 4.4.38
     - 4.5.2
   * - ``libxcrypt-compat``
     - 4.4.38
     - 4.5.2
   * - ``libxdamage``
     - 1.1.6
     - 1.1.7
   * - ``libxext``
     - 1.3.6
     - 1.3.7
   * - ``libxinerama``
     - 1.1.5
     - 1.1.6
   * - ``libxkbcommon``
     - 1.11.0
     - 1.13.1
   * - ``libxkbfile``
     - 1.1.3
     - 1.2.0
   * - ``libxml2``
     - 2.14.6
     - 2.15.2
   * - ``libxmu``
     - 1.2.1
     - 1.3.1
   * - ``libxpm``
     - 3.5.17
     - 3.5.18
   * - ``libxrandr``
     - 1.5.4
     - 1.5.5
   * - ``libxslt``
     - 1.1.43
     - 1.1.45
   * - ``libxvmc``
     - 1.0.14
     - 1.0.15
   * - ``libxxf86vm``
     - 1.1.6
     - 1.1.7
   * - ``lighttpd``
     - 1.4.81
     - 1.4.82
   * - ``linux-firmware``
     - 20251111
     - 20260410
   * - ``linux-libc-headers``
     - 6.17
     - 6.18
   * - ``linux-yocto``
     - 6.12.69+git, 6.16.11+git
     - 6.18.24+git
   * - ``linux-yocto-dev``
     - 6.18+git
     - 7.0+git
   * - ``linux-yocto-rt``
     - 6.12.69+git, 6.16.11+git
     - 6.18.24+git
   * - ``linux-yocto-tiny``
     - 6.12.69+git, 6.16.11+git
     - 6.18.24+git
   * - ``lld``
     - 21.1.7
     - 22.1.3
   * - ``lldb``
     - 21.1.7
     - 22.1.3
   * - ``llvm``
     - 21.1.7
     - 22.1.3
   * - ``llvm-tblgen-native``
     - 21.1.7
     - 22.1.3
   * - ``lsof``
     - 4.99.5
     - 4.99.6
   * - ``ltp``
     - 20250930
     - 20260130
   * - ``lttng-modules``
     - 2.14.3
     - 2.14.4
   * - ``lttng-tools``
     - 2.14.0
     - 2.14.1
   * - ``lua``
     - 5.4.8
     - 5.5.0
   * - ``lzlib``
     - 1.15
     - 1.16
   * - ``m4``
     - 1.4.20
     - 1.4.21
   * - ``m4-native``
     - 1.4.20
     - 1.4.21
   * - ``makedumpfile``
     - 1.7.7
     - 1.7.8
   * - ``man-pages``
     - 6.15
     - 6.17
   * - ``mdadm``
     - 4.4
     - 4.6
   * - ``mesa``
     - 25.2.8
     - 26.0.5
   * - ``mesa-gl``
     - 25.2.8
     - 26.0.5
   * - ``meson``
     - 1.9.1
     - 1.10.2
   * - ``mpg123``
     - 1.33.2
     - 1.33.4
   * - ``msmtp``
     - 1.8.31
     - 1.8.32
   * - ``mtd-utils``
     - 2.3.0
     - 2.3.1
   * - ``musl``
     - 1.2.5+git
     - 1.2.6+git
   * - ``nasm``
     - 2.16.03
     - 3.01
   * - ``ncurses``
     - 6.5
     - 6.6
   * - ``newlib``
     - 4.5.0+git
     - 4.6.0+git
   * - ``nfs-utils``
     - 2.8.4
     - 2.8.7
   * - ``nghttp2``
     - 1.66.0
     - 1.68.1
   * - ``ninja``
     - 1.13.1
     - 1.13.2
   * - ``ofono``
     - 2.18
     - 2.19
   * - ``openmp``
     - 21.1.7
     - 22.1.3
   * - ``opensbi``
     - 1.7
     - 1.8.1
   * - ``openssh``
     - 10.2p1
     - 10.3p1
   * - ``opkg``
     - 0.8.0
     - 0.9.0
   * - ``orc``
     - 0.4.41
     - 0.4.42
   * - ``ovmf``
     - edk2-stable202508
     - edk2-stable202511
   * - ``p11-kit``
     - 0.25.5
     - 0.26.2
   * - ``perl``
     - 5.40.2
     - 5.42.0
   * - ``perlcross``
     - 1.6.2
     - 1.6.4
   * - ``picolibc``
     - 1.8.6+git
     - 1.8.11+git
   * - ``picolibc-helloworld``
     - 1.8.6+git
     - 1.8.11+git
   * - ``procps``
     - 4.0.5
     - 4.0.6
   * - ``pseudo``
     - 1.9.3+git
     - 1.9.5
   * - ``puzzles``
     - 0.0+git (a7c7826bce5c…)
     - 0.0+git (ecb576fb2a0a…)
   * - ``python3``
     - 3.13.12
     - 3.14.4
   * - ``python3-attrs``
     - 25.3.0
     - 25.4.0
   * - ``python3-babel``
     - 2.17.0
     - 2.18.0
   * - ``python3-bcrypt``
     - 4.3.0
     - 5.0.0
   * - ``python3-beartype``
     - 0.21.0
     - 0.22.9
   * - ``python3-build``
     - 1.3.0
     - 1.4.0
   * - ``python3-calver``
     - 2025.04.17
     - 2025.10.20
   * - ``python3-certifi``
     - 2025.8.3
     - 2026.2.25
   * - ``python3-cffi``
     - 1.17.1
     - 2.0.0
   * - ``python3-chardet``
     - 5.2.0
     - 6.0.0.post1
   * - ``python3-click``
     - 8.2.2
     - 8.3.1
   * - ``python3-cryptography``
     - 45.0.7
     - 46.0.5
   * - ``python3-cryptography-vectors``
     - 45.0.7
     - 46.0.5
   * - ``python3-cython``
     - 3.1.3
     - 3.2.4
   * - ``python3-dbusmock``
     - 0.37.0
     - 0.38.1
   * - ``python3-docutils``
     - 0.22
     - 0.22.4
   * - ``python3-dtschema``
     - 2025.8
     - 2025.12
   * - ``python3-hatchling``
     - 1.27.0
     - 1.29.0
   * - ``python3-hypothesis``
     - 6.142.2
     - 6.151.9
   * - ``python3-imagesize``
     - 1.4.1
     - 2.0.0
   * - ``python3-iniconfig``
     - 2.1.0
     - 2.3.0
   * - ``python3-jsonschema``
     - 4.25.1
     - 4.26.0
   * - ``python3-markdown``
     - 3.9
     - 3.10.2
   * - ``python3-markupsafe``
     - 3.0.2
     - 3.0.3
   * - ``python3-maturin``
     - 1.9.4
     - 1.12.4
   * - ``python3-meson-python``
     - 0.18.0
     - 0.19.0
   * - ``python3-numpy``
     - 2.3.4
     - 2.4.3
   * - ``python3-packaging``
     - 25.0
     - 26.0
   * - ``python3-pathspec``
     - 0.12.1
     - 1.0.4
   * - ``python3-pbr``
     - 7.0.1
     - 7.0.3
   * - ``python3-pdm``
     - 2.25.9
     - 2.26.6
   * - ``python3-pdm-backend``
     - 2.4.5
     - 2.4.7
   * - ``python3-pdm-build-locked``
     - 0.3.5
     - 0.3.7
   * - ``python3-pip``
     - 25.2
     - 26.0.1
   * - ``python3-poetry-core``
     - 2.1.3
     - 2.3.1
   * - ``python3-psutil``
     - 7.0.0
     - 7.2.2
   * - ``python3-pyasn1``
     - 0.6.1
     - 0.6.2
   * - ``python3-pycairo``
     - 1.28.0
     - 1.29.0
   * - ``python3-pycparser``
     - 2.22
     - 3.0
   * - ``python3-pygobject``
     - 3.52.3
     - 3.56.1
   * - ``python3-pyopenssl``
     - 25.1.0
     - 26.0.0
   * - ``python3-pyparsing``
     - 3.2.4
     - 3.3.2
   * - ``python3-pyproject-metadata``
     - 0.9.1
     - 0.11.0
   * - ``python3-pytest``
     - 8.4.2
     - 9.0.2
   * - ``python3-pytest-subtests``
     - 0.14.2
     - 0.15.0
   * - ``python3-pytz``
     - 2025.2
     - 2026.1
   * - ``python3-pyyaml``
     - 6.0.2
     - 6.0.3
   * - ``python3-rdflib``
     - 7.1.4
     - 7.6.0
   * - ``python3-rpds-py``
     - 0.27.1
     - 0.30.0
   * - ``python3-ruamel-yaml``
     - 0.18.15
     - 0.19.1
   * - ``python3-scons``
     - 4.9.1
     - 4.10.1
   * - ``python3-setuptools``
     - 80.9.0
     - 82.0.1
   * - ``python3-setuptools-scm``
     - 8.3.1
     - 9.2.2
   * - ``python3-sphinx``
     - 8.2.1
     - 9.1.0
   * - ``python3-sphinx-rtd-theme``
     - 3.0.2
     - 3.1.0
   * - ``python3-testtools``
     - 2.7.2
     - 2.8.7
   * - ``python3-trove-classifiers``
     - 2025.9.11.17
     - 2026.1.14.14
   * - ``python3-unittest-automake-output``
     - 0.3
     - 0.4
   * - ``python3-uritools``
     - 5.0.0
     - 6.0.1
   * - ``python3-urllib3``
     - 2.5.0
     - 2.6.3
   * - ``python3-wcwidth``
     - 0.2.13
     - 0.6.0
   * - ``python3-webcolors``
     - 24.11.1
     - 25.10.0
   * - ``python3-websockets``
     - 15.0.1
     - 16.0
   * - ``python3-wheel``
     - 0.46.1
     - 0.46.3
   * - ``python3-xmltodict``
     - 0.15.1
     - 1.0.4
   * - ``python3-yamllint``
     - 1.37.1
     - 1.38.0
   * - ``qemu``
     - 10.0.6
     - 10.2.0
   * - ``qemu-native``
     - 10.0.6
     - 10.2.0
   * - ``qemu-system-native``
     - 10.0.6
     - 10.2.0
   * - ``quota``
     - 4.10
     - 4.11
   * - ``re2c``
     - 4.3
     - 4.4
   * - ``repo``
     - 2.58
     - 2.61.1
   * - ``resolvconf``
     - 1.93
     - 1.94
   * - ``rgb``
     - 1.1.0
     - 1.1.1
   * - ``rpm-sequoia``
     - 1.9.0
     - 1.10.1
   * - ``rpm-sequoia-crypto-policy``
     - git (ae1df75b1155…)
     - git (f3f5fa454345…)
   * - ``rt-tests``
     - 2.9
     - 2.10
   * - ``ruby``
     - 3.4.5
     - 4.0.2
   * - ``rust``
     - 1.90.0
     - 1.94.1
   * - ``rust-cross-canadian``
     - 1.90.0
     - 1.94.1
   * - ``sbc``
     - 2.1
     - 2.2
   * - ``scdoc``
     - 1.11.3
     - 1.11.4
   * - ``seatd``
     - 0.9.1
     - 0.9.3
   * - ``shaderc``
     - 2025.3
     - 2026.1
   * - ``shadow``
     - 4.18.0
     - 4.19.4
   * - ``socat``
     - 1.8.0.3
     - 1.8.1.1
   * - ``spirv-headers``
     - 1.4.328.1
     - 1.4.341.0
   * - ``spirv-llvm-translator``
     - 21.1.1
     - 22.1.1
   * - ``spirv-tools``
     - 1.4.328.1
     - 1.4.341.0
   * - ``sqlite3``
     - 3.48.0
     - 3.51.3
   * - ``squashfs-tools``
     - 4.7.2
     - 4.7.5
   * - ``strace``
     - 6.16
     - 6.19
   * - ``stress-ng``
     - 0.19.04
     - 0.20.01
   * - ``swig``
     - 4.3.1
     - 4.4.1
   * - ``sysstat``
     - 12.7.8
     - 12.7.9
   * - ``systemd``
     - 257.8
     - 259.5
   * - ``systemd-boot``
     - 257.8
     - 259.5
   * - ``systemd-boot-native``
     - 257.8
     - 259.5
   * - ``systemd-systemctl-native``
     - 257.8
     - 259.5
   * - ``systemtap``
     - 5.3
     - 5.4
   * - ``systemtap-native``
     - 5.3
     - 5.4
   * - ``taglib``
     - 2.1.1
     - 2.2.1
   * - ``tcl``
     - 9.0.2
     - 9.0.3
   * - ``texinfo``
     - 7.2
     - 7.3
   * - ``ttyrun``
     - 2.38.0
     - 2.41.0
   * - ``u-boot``
     - 2025.10
     - 2026.01
   * - ``u-boot-tools``
     - 2025.10
     - 2026.01
   * - ``usbutils``
     - 018
     - 019
   * - ``utfcpp``
     - 4.0.6
     - 4.0.9
   * - ``util-linux``
     - 2.41.1
     - 2.41.3
   * - ``util-linux-libuuid``
     - 2.41.1
     - 2.41.3
   * - ``valgrind``
     - 3.25.1
     - 3.26.0
   * - ``vim``
     - 9.1.1683
     - 9.2.0340
   * - ``vim-tiny``
     - 9.1.1683
     - 9.2.0340
   * - ``virglrenderer``
     - 1.1.1
     - 1.2.0
   * - ``vte``
     - 0.82.1
     - 0.82.2
   * - ``vulkan-headers``
     - 1.4.328.1
     - 1.4.341.0
   * - ``vulkan-loader``
     - 1.4.328.1
     - 1.4.341.0
   * - ``vulkan-samples``
     - git (d27205d14d01…)
     - git (fa2cf45adde0…)
   * - ``vulkan-tools``
     - 1.4.328.1
     - 1.4.341.0
   * - ``vulkan-utility-libraries``
     - 1.4.328.1
     - 1.4.341.0
   * - ``vulkan-validation-layers``
     - 1.4.328.1
     - 1.4.341.0
   * - ``vulkan-volk``
     - 1.4.328.1
     - 1.4.341.0
   * - ``wayland-protocols``
     - 1.45
     - 1.47
   * - ``wayland-utils``
     - 1.2.0
     - 1.3.0
   * - ``webkitgtk``
     - 2.50.4
     - 2.50.6
   * - ``weston``
     - 14.0.2
     - 15.0.0
   * - ``wpebackend-fdo``
     - 1.16.0
     - 1.16.1
   * - ``x264``
     - r3039+git (31e19f92f00c…)
     - r3039+git (0480cb05fa18…)
   * - ``xauth``
     - 1.1.4
     - 1.1.5
   * - ``xcb-util-cursor``
     - 0.1.5
     - 0.1.6
   * - ``xeyes``
     - 1.3.0
     - 1.3.1
   * - ``xkbcomp``
     - 1.4.7
     - 1.5.0
   * - ``xkeyboard-config``
     - 2.45
     - 2.47
   * - ``xorgproto``
     - 2024.1
     - 2025.1
   * - ``xserver-xorg``
     - 21.1.18
     - 21.1.21
   * - ``xwayland``
     - 24.1.8
     - 24.1.9
   * - ``xz``
     - 5.8.1
     - 5.8.2
   * - ``zlib``
     - 1.3.1
     - 1.3.2

Contributors to |yocto-ver|
---------------------------

..
   List obtained with the following shell snippet:

      authors=""
      for repo in openembedded-core yocto-docs bitbake meta-yocto; do
         authors="${authors}\n$(git --no-pager -C $repo log --format="-  %an" yocto-5.3..origin/master)"
      done
      echo $authors | sort | uniq

   Email addresses and duplicates removed.

Thanks to the following people who contributed to this release:

-  Adam Blank
-  Adam Duskett
-  Adarsh Jagadish Kamini
-  Aditya Kurdunkar
-  Adrian Freihofer
-  Ahmad Fatoum
-  Alejandro Hernandez Samaniego
-  Aleksandar Nikolic
-  Alexander Kanavin
-  Alexander Sverdlin
-  Alex Bradbury
-  Alex Kiernan
-  Amaury Couderc
-  Andrej Kozemcak
-  Anibal Limon
-  Ankur Tyagi
-  Antonin Godard
-  Ashish Kumar Mishra
-  Ashish Sharma
-  BELHADJ SALEM Talel
-  Benjamin Robin
-  Bruce Ashfield
-  Changqing Li
-  Chen Qi
-  Clement Faure
-  Colin Pinnell McAllister
-  Corentin Guillevic
-  Daiane Angolini
-  Daniel Dragomir
-  Daniel Turull
-  Dan McGregor
-  Deepesh Varatharajan
-  Dmitry Baryshkov
-  Dragomir, Daniel
-  El Mehdi YOUNES
-  Enrico Jörns
-  Ernst Persson
-  Etienne Cordonnier
-  Fabio Berton
-  Fabio Estevam
-  Favazza, Samuele
-  Florian Schmaus
-  Francesco Valla
-  Franz Schnyder
-  Germann, Bastian
-  Guðni Már Gilbert
-  Gyorgy Sarvari
-  Haiqing Bai
-  Harish Sadineni
-  Hemanth Kumar M D
-  Het Patel
-  Hiago De Franco
-  Himanshu Jadon
-  hongxu
-  Hongxu Jia
-  Jaeyoon Jung
-  Jan Luebbe
-  Jan Vermaete
-  Jason Schonberg
-  Javier Tia
-  Jiaying Song
-  Jinfeng Wang
-  João Marcos Costa
-  Jörg Sommer
-  Jose Quaresma
-  Joshua Watt
-  Kai Kang
-  Kamel Bouhara
-  Kavinaya S
-  Ken Kurematsu
-  Khai Dang
-  Khalifa Rouis
-  Khem Raj
-  Koen Kooi
-  Kory Maincent
-  Kristiyan Chakarov
-  Krupal Ka Patel
-  Lee Chee Yang
-  Leon Anavi
-  Le Qi
-  Liu Yiding
-  Livin Sunny
-  Liyin Zhang
-  Logan Gallois
-  Louis Rannou
-  Lucas Stach
-  Luka Krstic
-  Mahesh Angadi
-  Mark Hatle
-  Mark-Pk Tsai
-  Markus Volk
-  mark.yang
-  Martin Jansa
-  Martin Schwan
-  Mathieu Dubois-Briand
-  Matt Madison
-  Maxin B. John
-  Maxin John
-  Max Krummenacher
-  Miaoqing Pan
-  Michael Arndt
-  Michael Halstead
-  Michael Opdenacker
-  Michal Sieron
-  Mikko Rapeli
-  Ming Liu
-  Mingli Yu
-  Miroslav Cernak
-  Mohammad Rafi Shaik
-  Mohammad Rahimi
-  Moritz Haase
-  Naftaly RALAMBOARIVONY
-  Naman Jain
-  Nikhil R
-  Niko Mauno
-  Nora Schiffer
-  Osama Abdelkader
-  Patrick Vogelaar
-  Patrick Wicki
-  Paul Barker
-  Pavel Löbl
-  Peter Bergin
-  Peter de Ridder
-  Peter Kjellerstedt
-  Peter Marko
-  Peter Tatrai
-  Philip Lorenz
-  Pierre-Loup GOSSE
-  Piotr Buliński
-  Pratik Farkase
-  Quentin Schulz
-  Randolph Sapp
-  Randy MacLeod
-  Ricardo Salveti
-  Ricardo Simoes
-  Ricardo Ungerer
-  Richard Purdie
-  Robert Joslyn
-  Robert P. J. Day
-  Robert Yang
-  Rob Woolley
-  Ross Burton
-  Rouven Rastetter
-  Ryan Eatmon
-  Sam Povilus
-  Samuli Piippo
-  Sandeep Gundlupet Raju
-  Scott Murray
-  Shaik Moin
-  Shotaro Uchida
-  Stefano Babic
-  Stefano Tondo
-  Sunil Dora
-  sven.kalmbach
-  Swami
-  Telukula Jeevan Kumar Sahu
-  Theo GAIGE
-  Thomas Perrot
-  Tim Orling
-  Tom Geelen
-  Trevor Gamblin
-  Trevor Woerner
-  Ulrich Ölmann
-  Uwe Kleine-König
-  Veeresh Kadasani
-  Victor Kamensky
-  Vijay Anusuri
-  Viswanath Kraleti
-  Vivek Puar
-  Vyacheslav Yurkov
-  Wang Mingyu
-  Weisser, Pascal
-  Xiangyu Chen
-  Yanis BINARD
-  Yann Dirson
-  Yannic Moog
-  Yash Gupta
-  Yash Shinde
-  Yasir Al-Latifi
-  Yiding Liu
-  Yi Zhao
-  Yoann Congal
-  Yongxin Liu
-  Zhangfei Gao
-  Zhang Peng
-  Zk47T
-  Zoltán Böszörményi

Repositories / Downloads for Yocto-|yocto-ver|
----------------------------------------------
