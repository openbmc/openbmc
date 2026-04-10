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

-  Linux kernel XXX, gcc XXX, glibc XXX, LLVM XXX, and over XXX other
   recipe upgrades

-  Minimum Python version required on the host: XXX.

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

-  Kernel-related changes:

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

-  New core classes:

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

-  Architecture-specific changes:

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

-  Documentation changes:

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

-  Wic Image Creator changes:

   -  ``wic/engine``: Fix copying directories into wic image with ``ext*``
      partitions (:oecore_rev:`1ed38aff5f810d064c87aff9cbd310906833b6ba`)

-  SDK-related changes:

-  Testing-related changes:

   - :ref:`ref-classes-ptest` support was added for the following recipes:

      -  ``libarchive`` (:oecore_rev:`6e0bf90e31c969fb18efb18aaceaee886194a7b7`)
      -  ``libassuan`` (:oecore_rev:`1010abf3e32e6616ef0075d4d826c1734937152b`)
      -  ``libcheck`` (:oecore_rev:`1bb06e23c1c87829e5c4211e34229305c7d5f851`)
      -  ``libconfig`` (:oecore_rev:`f3e9d1326bf37361ff94dc4eef52de13b64651b2`)
      -  ``libksba`` (:oecore_rev:`f50a2005dda8cecf3a9db44edb131e7e332fa42d`)
      -  ``libmd`` (:oecore_rev:`4c0a41389bdab30e3b349fef8df6ca0ef4893b89`)
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

-  Utility script changes:

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

   -  ``cooker``: Use :term:`bitbake:BB_HASHSERVE_DB_DIR` as hash server
      database location. If unset, the existing behavior is preserved
      (:bitbake_rev:`b339d05ad2b69a6518522ee4c46dd5f5a6e33f65`)

   -  ``bitbake-getvar``: Show close matches when no providers are found
      (:bitbake_rev:`1f8fa7c25e71cd0f230a2f6bfd9d5153c694da81`)

-  Packaging changes:

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

-  ``devtool`` changes:

   -  ``ide-sdk``: Find ``bitbake-setup``'s ``init-build-env``
      first, and ``oe-init-build-env`` if not found
      (:oecore_rev:`6ab7e9e8e52fa123551438820c59b8c5e0c9c8a5`)

   -  ``ide-sdk``: Add `gdbserver` attach mode support
      (:oecore_rev:`119171087681bd47842865d6451868c1127f1149`)

   -  ``ide-sdk``: Support GDB pretty-printing for C++ STL types
      (:oecore_rev:`a69e2baba81b0cd88d58b164433c72e1156424b1`)

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

-  Security changes:

   -  A new document was added to the Yocto Project documentation:
      :doc:`/security-reference/index`. It is intended to document how to report
      vulnerabilities to the Yocto Project security team.

-  :ref:`ref-classes-cve-check`-related changes:

   -  ``cve-update-nvd2-native``: Use maximum CVSS score when extracting it from
      multiple sources (:oecore_rev:`4f6192f3165de0bc2499e045607c7e7ffd878a4b`)

-  New :term:`PACKAGECONFIG` options for individual recipes:

   -  ``curl``: ``schannel``
   -  ``gstreamer1.0-plugins-good``: ``qt6``
   -  ``libinput``: ``lua``, ``libwacom``, ``mtdev``
   -  ``mesa``: ``expat``, ``zlib``
   -  ``openssl``: ``legacy``
   -  ``opkg``: ``acl``, ``xattr``
   -  ``python3-cryptography``: ``legacy-openssl``

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

   -  ``initramfs-framework``: Add handover of PID 1's arguments to modules
      (:oecore_rev:`a0ab3d1c4f9ed34d1d17e6534f42d17b3387ebb3`)

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

   -  ``busybox``: Enable SELinux support if :term:`DISTRO_FEATURES` contains
      ``selinux`` (:oecore_rev:`c544f12073ea712c3d3ce08105d52640a7a322b9`)

   -  ``coreutils``: ``kill`` and ``uptime`` are no longer provided by the
      recipe (:oecore_rev:`cedeb958dfa892e409bdce8525030c20b3400332`)

Known Issues in |yocto-ver|
---------------------------

Recipe License changes in |yocto-ver|
-------------------------------------

The following changes have been made to the :term:`LICENSE` values set by recipes:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous value
     - New value
   * - ``recipe name``
     - Previous value
     - New value

Security Fixes in |yocto-ver|
-----------------------------

The following CVEs have been fixed:

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Recipe
     - CVE IDs
   * - ``recipe name``
     - :cve_nist:`xxx-xxxx`, ...

Recipe Upgrades in |yocto-ver|
------------------------------

The following recipes have been upgraded:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous version
     - New version
   * - ``recipe name``
     - Previous version
     - New version

Contributors to |yocto-ver|
---------------------------

Thanks to the following people who contributed to this release:

Repositories / Downloads for Yocto-|yocto-ver|
----------------------------------------------
