Moving to the Yocto Project 2.0 Release
=======================================

This section provides migration information for moving to the Yocto
Project 2.0 Release from the prior release.

.. _migration-2.0-gcc-5:

GCC 5
-----

The default compiler is now GCC 5.2. This change has required fixes for
compilation errors in a number of other recipes.

One important example is a fix for when the Linux kernel freezes at boot
time on ARM when built with GCC 5. If you are using your own kernel
recipe or source tree and building for ARM, you will likely need to
apply this
`patch <https://git.kernel.org/cgit/linux/kernel/git/torvalds/linux.git/commit?id=a077224fd35b2f7fbc93f14cf67074fc792fbac2>`__.
The standard ``linux-yocto`` kernel source tree already has a workaround
for the same issue.

For further details, see https://gcc.gnu.org/gcc-5/changes.html
and the porting guide at
https://gcc.gnu.org/gcc-5/porting_to.html.

Alternatively, you can switch back to GCC 4.9 or 4.8 by setting
``GCCVERSION`` in your configuration, as follows:
::

   GCCVERSION = "4.9%"

.. _migration-2.0-Gstreamer-0.10-removed:

Gstreamer 0.10 Removed
----------------------

Gstreamer 0.10 has been removed in favor of Gstreamer 1.x. As part of
the change, recipes for Gstreamer 0.10 and related software are now
located in ``meta-multimedia``. This change results in Qt4 having Phonon
and Gstreamer support in QtWebkit disabled by default.

.. _migration-2.0-removed-recipes:

Removed Recipes
---------------

The following recipes have been moved or removed:

-  ``bluez4``: The recipe is obsolete and has been moved due to
   ``bluez5`` becoming fully integrated. The ``bluez4`` recipe now
   resides in ``meta-oe``.

-  ``gamin``: The recipe is obsolete and has been removed.

-  ``gnome-icon-theme``: The recipe's functionally has been replaced by
   ``adwaita-icon-theme``.

-  Gstreamer 0.10 Recipes: Recipes for Gstreamer 0.10 have been removed
   in favor of the recipes for Gstreamer 1.x.

-  ``insserv``: The recipe is obsolete and has been removed.

-  ``libunique``: The recipe is no longer used and has been moved to
   ``meta-oe``.

-  ``midori``: The recipe's functionally has been replaced by
   ``epiphany``.

-  ``python-gst``: The recipe is obsolete and has been removed since it
   only contains bindings for Gstreamer 0.10.

-  ``qt-mobility``: The recipe is obsolete and has been removed since it
   requires ``Gstreamer 0.10``, which has been replaced.

-  ``subversion``: All 1.6.x versions of this recipe have been removed.

-  ``webkit-gtk``: The older 1.8.3 version of this recipe has been
   removed in favor of ``webkitgtk``.

.. _migration-2.0-bitbake-datastore-improvements:

BitBake datastore improvements
------------------------------

The method by which BitBake's datastore handles overrides has changed.
Overrides are now applied dynamically and ``bb.data.update_data()`` is
now a no-op. Thus, ``bb.data.update_data()`` is no longer required in
order to apply the correct overrides. In practice, this change is
unlikely to require any changes to Metadata. However, these minor
changes in behavior exist:

-  All potential overrides are now visible in the variable history as
   seen when you run the following:
   ::

      $ bitbake -e

-  ``d.delVar('``\ VARNAME\ ``')`` and
   ``d.setVar('``\ VARNAME\ ``', None)`` result in the variable and all
   of its overrides being cleared out. Before the change, only the
   non-overridden values were cleared.

.. _migration-2.0-shell-message-function-changes:

Shell Message Function Changes
------------------------------

The shell versions of the BitBake message functions (i.e. ``bbdebug``,
``bbnote``, ``bbwarn``, ``bbplain``, ``bberror``, and ``bbfatal``) are
now connected through to their BitBake equivalents ``bb.debug()``,
``bb.note()``, ``bb.warn()``, ``bb.plain()``, ``bb.error()``, and
``bb.fatal()``, respectively. Thus, those message functions that you
would expect to be printed by the BitBake UI are now actually printed.
In practice, this change means two things:

-  If you now see messages on the console that you did not previously
   see as a result of this change, you might need to clean up the calls
   to ``bbwarn``, ``bberror``, and so forth. Or, you might want to
   simply remove the calls.

-  The ``bbfatal`` message function now suppresses the full error log in
   the UI, which means any calls to ``bbfatal`` where you still wish to
   see the full error log should be replaced by ``die`` or
   ``bbfatal_log``.

.. _migration-2.0-extra-development-debug-package-cleanup:

Extra Development/Debug Package Cleanup
---------------------------------------

The following recipes have had extra ``dev/dbg`` packages removed:

-  ``acl``

-  ``apmd``

-  ``aspell``

-  ``attr``

-  ``augeas``

-  ``bzip2``

-  ``cogl``

-  ``curl``

-  ``elfutils``

-  ``gcc-target``

-  ``libgcc``

-  ``libtool``

-  ``libxmu``

-  ``opkg``

-  ``pciutils``

-  ``rpm``

-  ``sysfsutils``

-  ``tiff``

-  ``xz``

All of the above recipes now conform to the standard packaging scheme
where a single ``-dev``, ``-dbg``, and ``-staticdev`` package exists per
recipe.

.. _migration-2.0-recipe-maintenance-tracking-data-moved-to-oe-core:

Recipe Maintenance Tracking Data Moved to OE-Core
-------------------------------------------------

Maintenance tracking data for recipes that was previously part of
``meta-yocto`` has been moved to :term:`OpenEmbedded-Core (OE-Core)`. The change
includes ``package_regex.inc`` and ``distro_alias.inc``, which are
typically enabled when using the ``distrodata`` class. Additionally, the
contents of ``upstream_tracking.inc`` has now been split out to the
relevant recipes.

.. _migration-2.0-automatic-stale-sysroot-file-cleanup:

Automatic Stale Sysroot File Cleanup
------------------------------------

Stale files from recipes that no longer exist in the current
configuration are now automatically removed from sysroot as well as
removed from any other place managed by shared state. This automatic
cleanup means that the build system now properly handles situations such
as renaming the build system side of recipes, removal of layers from
``bblayers.conf``, and :term:`DISTRO_FEATURES`
changes.

Additionally, work directories for old versions of recipes are now
pruned. If you wish to disable pruning old work directories, you can set
the following variable in your configuration:
::

   SSTATE_PRUNE_OBSOLETEWORKDIR = "0"

.. _migration-2.0-linux-yocto-kernel-metadata-repository-now-split-from-source:

``linux-yocto`` Kernel Metadata Repository Now Split from Source
----------------------------------------------------------------

The ``linux-yocto`` tree has up to now been a combined set of kernel
changes and configuration (meta) data carried in a single tree. While
this format is effective at keeping kernel configuration and source
modifications synchronized, it is not always obvious to developers how
to manipulate the Metadata as compared to the source.

Metadata processing has now been removed from the
:ref:`kernel-yocto <ref-classes-kernel-yocto>` class and the external
Metadata repository ``yocto-kernel-cache``, which has always been used
to seed the ``linux-yocto`` "meta" branch. This separate ``linux-yocto``
cache repository is now the primary location for this data. Due to this
change, ``linux-yocto`` is no longer able to process combined trees.
Thus, if you need to have your own combined kernel repository, you must
do the split there as well and update your recipes accordingly. See the
``meta/recipes-kernel/linux/linux-yocto_4.1.bb`` recipe for an example.

.. _migration-2.0-additional-qa-checks:

Additional QA checks
--------------------

The following QA checks have been added:

-  Added a "host-user-contaminated" check for ownership issues for
   packaged files outside of ``/home``. The check looks for files that
   are incorrectly owned by the user that ran BitBake instead of owned
   by a valid user in the target system.

-  Added an "invalid-chars" check for invalid (non-UTF8) characters in
   recipe metadata variable values (i.e.
   :term:`DESCRIPTION`,
   :term:`SUMMARY`, :term:`LICENSE`, and
   :term:`SECTION`). Some package managers do not support
   these characters.

-  Added an "invalid-packageconfig" check for any options specified in
   :term:`PACKAGECONFIG` that do not match any
   ``PACKAGECONFIG`` option defined for the recipe.

.. _migration-2.0-miscellaneous:

Miscellaneous Changes
---------------------

These additional changes exist:

-  ``gtk-update-icon-cache`` has been renamed to ``gtk-icon-utils``.

-  The ``tools-profile`` :term:`IMAGE_FEATURES`
   item as well as its corresponding packagegroup and
   ``packagegroup-core-tools-profile`` no longer bring in ``oprofile``.
   Bringing in ``oprofile`` was originally added to aid compilation on
   resource-constrained targets. However, this aid has not been widely
   used and is not likely to be used going forward due to the more
   powerful target platforms and the existence of better
   cross-compilation tools.

-  The :term:`IMAGE_FSTYPES` variable's default
   value now specifies ``ext4`` instead of ``ext3``.

-  All support for the ``PRINC`` variable has been removed.

-  The ``packagegroup-core-full-cmdline`` packagegroup no longer brings
   in ``lighttpd`` due to the fact that bringing in ``lighttpd`` is not
   really in line with the packagegroup's purpose, which is to add full
   versions of command-line tools that by default are provided by
   ``busybox``.


