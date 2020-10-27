Moving to the Yocto Project 2.5 Release
=======================================

This section provides migration information for moving to the Yocto
Project 2.5 Release from the prior release.

.. _migration-2.5-packaging-changes:

Packaging Changes
-----------------

This section provides information about packaging changes that have
occurred:

-  ``bind-libs``: The libraries packaged by the bind recipe are in a
   separate ``bind-libs`` package.

-  ``libfm-gtk``: The ``libfm`` GTK+ bindings are split into a
   separate ``libfm-gtk`` package.

-  ``flex-libfl``: The flex recipe splits out libfl into a separate
   ``flex-libfl`` package to avoid too many dependencies being pulled in
   where only the library is needed.

-  ``grub-efi``: The ``grub-efi`` configuration is split into a
   separate ``grub-bootconf`` recipe. However, the dependency
   relationship from ``grub-efi`` is through a virtual/grub-bootconf
   provider making it possible to have your own recipe provide the
   dependency. Alternatively, you can use a BitBake append file to bring
   the configuration back into the ``grub-efi`` recipe.

-  *armv7a Legacy Package Feed Support:* Legacy support is removed for
   transitioning from ``armv7a`` to ``armv7a-vfp-neon`` in package
   feeds, which was previously enabled by setting
   ``PKGARCHCOMPAT_ARMV7A``. This transition occurred in 2011 and active
   package feeds should by now be updated to the new naming.

.. _migration-2.5-removed-recipes:

Removed Recipes
---------------

The following recipes have been removed:

-  ``gcc``: The version 6.4 recipes are replaced by 7.x.

-  ``gst-player``: Renamed to ``gst-examples`` as per upstream.

-  ``hostap-utils``: This software package is obsolete.

-  ``latencytop``: This recipe is no longer maintained upstream. The
   last release was in 2009.

-  ``libpfm4``: The only file that requires this recipe is
   ``oprofile``, which has been removed.

-  ``linux-yocto``: The version 4.4, 4.9, and 4.10 recipes have been
   removed. Versions 4.12, 4.14, and 4.15 remain.

-  ``man``: This recipe has been replaced by modern ``man-db``

-  ``mkelfimage``: This tool has been removed in the upstream coreboot
   project, and is no longer needed with the removal of the ELF image
   type.

-  ``nativesdk-postinst-intercept``: This recipe is not maintained.

-  ``neon``: This software package is no longer maintained upstream
   and is no longer needed by anything in OpenEmbedded-Core.

-  ``oprofile``: The functionality of this recipe is replaced by
   ``perf`` and keeping compatibility on an ongoing basis with ``musl``
   is difficult.

-  ``pax``: This software package is obsolete.

-  ``stat``: This software package is not maintained upstream.
   ``coreutils`` provides a modern stat binary.

-  ``zisofs-tools-native``: This recipe is no longer needed because
   the compressed ISO image feature has been removed.

.. _migration-2.5-scripts-and-tools-changes:

Scripts and Tools Changes
-------------------------

The following are changes to scripts and tools:

-  ``yocto-bsp``, ``yocto-kernel``, and ``yocto-layer``: The
   ``yocto-bsp``, ``yocto-kernel``, and ``yocto-layer`` scripts
   previously shipped with poky but not in OpenEmbedded-Core have been
   removed. These scripts are not maintained and are outdated. In many
   cases, they are also limited in scope. The
   ``bitbake-layers create-layer`` command is a direct replacement for
   ``yocto-layer``. See the documentation to create a BSP or kernel
   recipe in the ":ref:`bsp-guide/bsp:bsp kernel recipe example`" section.

-  ``devtool finish``: ``devtool finish`` now exits with an error if
   there are uncommitted changes or a rebase/am in progress in the
   recipe's source repository. If this error occurs, there might be
   uncommitted changes that will not be included in updates to the
   patches applied by the recipe. A -f/--force option is provided for
   situations that the uncommitted changes are inconsequential and you
   want to proceed regardless.

-  ``scripts/oe-setup-rpmrepo`` script: The functionality of
   ``scripts/oe-setup-rpmrepo`` is replaced by
   ``bitbake package-index``.

-  ``scripts/test-dependencies.sh`` script: The script is largely made
   obsolete by the recipe-specific sysroots functionality introduced in
   the previous release.

.. _migration-2.5-bitbake-changes:

BitBake Changes
---------------

The following are BitBake changes:

-  The ``--runall`` option has changed. There are two different
   behaviors people might want:

   -  *Behavior A:* For a given target (or set of targets) look through
      the task graph and run task X only if it is present and will be
      built.

   -  *Behavior B:* For a given target (or set of targets) look through
      the task graph and run task X if any recipe in the taskgraph has
      such a target, even if it is not in the original task graph.

   The ``--runall`` option now performs "Behavior B". Previously
   ``--runall`` behaved like "Behavior A". A ``--runonly`` option has
   been added to retain the ability to perform "Behavior A".

-  Several explicit "run this task for all recipes in the dependency
   tree" tasks have been removed (e.g. ``fetchall``, ``checkuriall``,
   and the ``*all`` tasks provided by the ``distrodata`` and
   ``archiver`` classes). There is a BitBake option to complete this for
   any arbitrary task. For example:
   ::

      bitbake <target> -c fetchall

   should now be replaced with:
   ::

      bitbake <target> --runall=fetch

.. _migration-2.5-python-and-python3-changes:

Python and Python 3 Changes
---------------------------

The following are auto-packaging changes to Python and Python 3:

The script-managed ``python-*-manifest.inc`` files that were previously
used to generate Python and Python 3 packages have been replaced with a
JSON-based file that is easier to read and maintain. A new task is
available for maintainers of the Python recipes to update the JSON file
when upgrading to new Python versions. You can now edit the file
directly instead of having to edit a script and run it to update the
file.

One particular change to note is that the Python recipes no longer have
build-time provides for their packages. This assumes ``python-foo`` is
one of the packages provided by the Python recipe. You can no longer run
``bitbake python-foo`` or have a
:term:`DEPENDS` on ``python-foo``,
but doing either of the following causes the package to work as
expected: ::

   IMAGE_INSTALL_append = " python-foo"

or ::

   RDEPENDS_${PN} = "python-foo"

The earlier build-time provides behavior was a quirk of the
way the Python manifest file was created. For more information on this
change please see :yocto_git:`this commit
</cgit/cgit.cgi/poky/commit/?id=8d94b9db221d1def42f091b991903faa2d1651ce>`.

.. _migration-2.5-miscellaneous-changes:

Miscellaneous Changes
---------------------

The following are additional changes:

-  The ``kernel`` class supports building packages for multiple kernels.
   If your kernel recipe or ``.bbappend`` file mentions packaging at
   all, you should replace references to the kernel in package names
   with ``${KERNEL_PACKAGE_NAME}``. For example, if you disable
   automatic installation of the kernel image using
   ``RDEPENDS_kernel-base = ""`` you can avoid warnings using
   ``RDEPENDS_${KERNEL_PACKAGE_NAME}-base = ""`` instead.

-  The ``buildhistory`` class commits changes to the repository by
   default so you no longer need to set ``BUILDHISTORY_COMMIT = "1"``.
   If you want to disable commits you need to set
   ``BUILDHISTORY_COMMIT = "0"`` in your configuration.

-  The ``beaglebone`` reference machine has been renamed to
   ``beaglebone-yocto``. The ``beaglebone-yocto`` BSP is a reference
   implementation using only mainline components available in
   OpenEmbedded-Core and ``meta-yocto-bsp``, whereas Texas Instruments
   maintains a full-featured BSP in the ``meta-ti`` layer. This rename
   avoids the previous name clash that existed between the two BSPs.

-  The ``update-alternatives`` class no longer works with SysV ``init``
   scripts because this usage has been problematic. Also, the
   ``sysklogd`` recipe no longer uses ``update-alternatives`` because it
   is incompatible with other implementations.

-  By default, the :ref:`cmake <ref-classes-cmake>` class uses
   ``ninja`` instead of ``make`` for building. This improves build
   performance. If a recipe is broken with ``ninja``, then the recipe
   can set ``OECMAKE_GENERATOR = "Unix Makefiles"`` to change back to
   ``make``.

-  The previously deprecated ``base_*`` functions have been removed in
   favor of their replacements in ``meta/lib/oe`` and
   ``bitbake/lib/bb``. These are typically used from recipes and
   classes. Any references to the old functions must be updated. The
   following table shows the removed functions and their replacements:

   +------------------------------+----------------------------------------------------------+
   | *Removed*                    | *Replacement*                                            |
   +==============================+==========================================================+
   | base_path_join()             | oe.path.join()                                           |
   +------------------------------+----------------------------------------------------------+
   | base_path_relative()         | oe.path.relative()                                       |
   +------------------------------+----------------------------------------------------------+
   | base_path_out()              | oe.path.format_display()                                 |
   +------------------------------+----------------------------------------------------------+
   | base_read_file()             | oe.utils.read_file()                                     |
   +------------------------------+----------------------------------------------------------+
   | base_ifelse()                | oe.utils.ifelse()                                        |
   +------------------------------+----------------------------------------------------------+
   | base_conditional()           | oe.utils.conditional()                                   |
   +------------------------------+----------------------------------------------------------+
   | base_less_or_equal()         | oe.utils.less_or_equal()                                 |
   +------------------------------+----------------------------------------------------------+
   | base_version_less_or_equal() | oe.utils.version_less_or_equal()                         |
   +------------------------------+----------------------------------------------------------+
   | base_contains()              | bb.utils.contains()                                      |
   +------------------------------+----------------------------------------------------------+
   | base_both_contain()          | oe.utils.both_contain()                                  |
   +------------------------------+----------------------------------------------------------+
   | base_prune_suffix()          | oe.utils.prune_suffix()                                  |
   +------------------------------+----------------------------------------------------------+
   | oe_filter()                  | oe.utils.str_filter()                                    |
   +------------------------------+----------------------------------------------------------+
   | oe_filter_out()              | oe.utils.str_filter_out() (or use the \_remove operator) |
   +------------------------------+----------------------------------------------------------+

-  Using ``exit 1`` to explicitly defer a postinstall script until first
   boot is now deprecated since it is not an obvious mechanism and can
   mask actual errors. If you want to explicitly defer a postinstall to
   first boot on the target rather than at ``rootfs`` creation time, use
   ``pkg_postinst_ontarget()`` or call
   ``postinst_intercept delay_to_first_boot`` from ``pkg_postinst()``.
   Any failure of a ``pkg_postinst()`` script (including ``exit 1``)
   will trigger a warning during ``do_rootfs``.

   For more information, see the
   ":ref:`dev-manual/dev-manual-common-tasks:post-installation scripts`"
   section in the Yocto Project Development Tasks Manual.

-  The ``elf`` image type has been removed. This image type was removed
   because the ``mkelfimage`` tool that was required to create it is no
   longer provided by coreboot upstream and required updating every time
   ``binutils`` updated.

-  Support for .iso image compression (previously enabled through
   ``COMPRESSISO = "1"``) has been removed. The userspace tools
   (``zisofs-tools``) are unmaintained and ``squashfs`` provides better
   performance and compression. In order to build a live image with
   squashfs+lz4 compression enabled you should now set
   ``LIVE_ROOTFS_TYPE = "squashfs-lz4"`` and ensure that ``live`` is in
   ``IMAGE_FSTYPES``.

-  Recipes with an unconditional dependency on ``libpam`` are only
   buildable with ``pam`` in ``DISTRO_FEATURES``. If the dependency is
   truly optional then it is recommended that the dependency be
   conditional upon ``pam`` being in ``DISTRO_FEATURES``.

-  For EFI-based machines, the bootloader (``grub-efi`` by default) is
   installed into the image at /boot. Wic can be used to split the
   bootloader into separate boot and rootfs partitions if necessary.

-  Patches whose context does not match exactly (i.e. where patch
   reports "fuzz" when applying) will generate a warning. For an example
   of this see `this
   commit <http://git.yoctoproject.org/cgit/cgit.cgi/poky/commit/?id=cc97bc08125b63821ce3f616771830f77c456f57>`__.

-  Layers are expected to set ``LAYERSERIES_COMPAT_layername`` to match
   the version(s) of OpenEmbedded-Core they are compatible with. This is
   specified as codenames using spaces to separate multiple values (e.g.
   "rocko sumo"). If a layer does not set
   ``LAYERSERIES_COMPAT_layername``, a warning will is shown. If a layer
   sets a value that does not include the current version ("sumo" for
   the 2.5 release), then an error will be produced.

-  The ``TZ`` environment variable is set to "UTC" within the build
   environment in order to fix reproducibility problems in some recipes.


