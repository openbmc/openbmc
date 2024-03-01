.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 1.5 (dora)
==================

This section provides migration information for moving to the Yocto
Project 1.5 Release (codename "dora") from the prior release.

.. _migration-1.5-host-dependency-changes:

Host Dependency Changes
-----------------------

The OpenEmbedded build system now has some additional requirements on
the host system:

-  Python 2.7.3+

-  Tar 1.24+

-  Git 1.7.8+

-  Patched version of Make if you are using 3.82. Most distributions
   that provide Make 3.82 use the patched version.

If the Linux distribution you are using on your build host does not
provide packages for these, you can install and use the Buildtools
tarball, which provides an SDK-like environment containing them.

For more information on this requirement, see the
":ref:`system-requirements-buildtools`" section.

.. _migration-1.5-atom-pc-bsp:

``atom-pc`` Board Support Package (BSP)
---------------------------------------

The ``atom-pc`` hardware reference BSP has been replaced by a
``genericx86`` BSP. This BSP is not necessarily guaranteed to work on
all x86 hardware, but it will run on a wider range of systems than the
``atom-pc`` did.

.. note::

   Additionally, a ``genericx86-64`` BSP has been added for 64-bit Atom
   systems.

.. _migration-1.5-bitbake:

BitBake
-------

The following changes have been made that relate to BitBake:

-  BitBake now supports a ``_remove`` operator. The addition of this
   operator means you will have to rename any items in recipe space
   (functions, variables) whose names currently contain ``_remove_`` or
   end with ``_remove`` to avoid unexpected behavior.

-  BitBake's global method pool has been removed. This method is not
   particularly useful and led to clashes between recipes containing
   functions that had the same name.

-  The "none" server backend has been removed. The "process" server
   backend has been serving well as the default for a long time now.

-  The ``bitbake-runtask`` script has been removed.

-  ``${``\ :term:`P`\ ``}`` and
   ``${``\ :term:`PF`\ ``}`` are no longer added to
   :term:`PROVIDES` by default in ``bitbake.conf``.
   These version-specific :term:`PROVIDES` items were seldom used.
   Attempting to use them could result in two versions being built
   simultaneously rather than just one version due to the way BitBake
   resolves dependencies.

.. _migration-1.5-qa-warnings:

QA Warnings
-----------

The following changes have been made to the package QA checks:

-  If you have customized :term:`ERROR_QA` or
   :term:`WARN_QA` values in your configuration, check
   that they contain all of the issues that you wish to be reported.
   Previous Yocto Project versions contained a bug that meant that any
   item not mentioned in :term:`ERROR_QA` or :term:`WARN_QA` would be treated as
   a warning. Consequently, several important items were not already in
   the default value of :term:`WARN_QA`. All of the possible QA checks are
   now documented in the ":ref:`ref-classes-insane`" section.

-  An additional QA check has been added to check if
   ``/usr/share/info/dir`` is being installed. Your recipe should delete
   this file within :ref:`ref-tasks-install` if "make
   install" is installing it.

-  If you are using the :ref:`ref-classes-buildhistory` class, the check for the
   package version going backwards is now controlled using a standard QA check.
   Thus, if you have customized your :term:`ERROR_QA` or :term:`WARN_QA` values
   and still wish to have this check performed, you should add
   "version-going-backwards" to your value for one or the other
   variables depending on how you wish it to be handled. See the
   documented QA checks in the ":ref:`ref-classes-insane`" section.

.. _migration-1.5-directory-layout-changes:

Directory Layout Changes
------------------------

The following directory changes exist:

-  Output SDK installer files are now named to include the image name
   and tuning architecture through the :term:`SDK_NAME`
   variable.

-  Images and related files are now installed into a directory that is
   specific to the machine, instead of a parent directory containing
   output files for multiple machines. The
   :term:`DEPLOY_DIR_IMAGE` variable continues
   to point to the directory containing images for the current
   :term:`MACHINE` and should be used anywhere there is a
   need to refer to this directory. The ``runqemu`` script now uses this
   variable to find images and kernel binaries and will use BitBake to
   determine the directory. Alternatively, you can set the
   :term:`DEPLOY_DIR_IMAGE` variable in the external environment.

-  When buildhistory is enabled, its output is now written under the
   :term:`Build Directory` rather than :term:`TMPDIR`. Doing so makes
   it easier to delete :term:`TMPDIR` and preserve the build history.
   Additionally, data for produced SDKs is now split by :term:`IMAGE_NAME`.

-  When :ref:`ref-classes-buildhistory` is enabled, its output
   is now written under the :term:`Build Directory` rather than :term:`TMPDIR`.
   Doing so makes it easier to delete :term:`TMPDIR` and preserve the build
   history. Additionally, data for produced SDKs is now split by :term:`IMAGE_NAME`.

-  The ``pkgdata`` directory produced as part of the packaging process
   has been collapsed into a single machine-specific directory. This
   directory is located under ``sysroots`` and uses a machine-specific
   name (i.e. ``tmp/sysroots/machine/pkgdata``).

.. _migration-1.5-shortened-git-srcrev-values:

Shortened Git ``SRCREV`` Values
-------------------------------

BitBake will now shorten revisions from Git repositories from the normal
40 characters down to 10 characters within :term:`SRCPV`
for improved usability in path and filenames. This change should be
safe within contexts where these revisions are used because the chances
of spatially close collisions is very low. Distant collisions are not a
major issue in the way the values are used.

.. _migration-1.5-image-features:

``IMAGE_FEATURES``
------------------

The following changes have been made that relate to
:term:`IMAGE_FEATURES`:

-  The value of :term:`IMAGE_FEATURES` is now validated to ensure invalid
   feature items are not added. Some users mistakenly add package names
   to this variable instead of using
   :term:`IMAGE_INSTALL` in order to have the
   package added to the image, which does not work. This change is
   intended to catch those kinds of situations. Valid :term:`IMAGE_FEATURES`
   are drawn from ``PACKAGE_GROUP`` definitions,
   :term:`COMPLEMENTARY_GLOB` and a new
   "validitems" varflag on :term:`IMAGE_FEATURES`. The "validitems" varflag
   change allows additional features to be added if they are not
   provided using the previous two mechanisms.

-  The previously deprecated "apps-console-core" :term:`IMAGE_FEATURES` item
   is no longer supported. Add "splash" to :term:`IMAGE_FEATURES` if you
   wish to have the splash screen enabled, since this is all that
   apps-console-core was doing.

.. _migration-1.5-run:

``/run``
--------

The ``/run`` directory from the Filesystem Hierarchy Standard 3.0 has
been introduced. You can find some of the implications for this change
:oe_git:`here </openembedded-core/commit/?id=0e326280a15b0f2c4ef2ef4ec441f63f55b75873>`.
The change also means that recipes that install files to ``/var/run``
must be changed. You can find a guide on how to make these changes
`here <https://www.mail-archive.com/openembedded-devel@lists.openembedded.org/msg31649.html>`__.

.. _migration-1.5-removal-of-package-manager-database-within-image-recipes:

Removal of Package Manager Database Within Image Recipes
--------------------------------------------------------

The image ``core-image-minimal`` no longer adds
``remove_packaging_data_files`` to
:term:`ROOTFS_POSTPROCESS_COMMAND`.
This addition is now handled automatically when "package-management" is
not in :term:`IMAGE_FEATURES`. If you have custom
image recipes that make this addition, you should remove the lines, as
they are not needed and might interfere with correct operation of
postinstall scripts.

.. _migration-1.5-images-now-rebuild-only-on-changes-instead-of-every-time:

Images Now Rebuild Only on Changes Instead of Every Time
--------------------------------------------------------

The :ref:`ref-tasks-rootfs` and other related image
construction tasks are no longer marked as "nostamp". Consequently, they
will only be re-executed when their inputs have changed. Previous
versions of the OpenEmbedded build system always rebuilt the image when
requested rather when necessary.

.. _migration-1.5-task-recipes:

Task Recipes
------------

The previously deprecated ``task.bbclass`` has now been dropped. For
recipes that previously inherited from this class, you should rename
them from ``task-*`` to ``packagegroup-*`` and inherit
:ref:`ref-classes-packagegroup` instead.

For more information, see the ":ref:`ref-classes-packagegroup`" section.

.. _migration-1.5-busybox:

BusyBox
-------

By default, we now split BusyBox into two binaries: one that is suid
root for those components that need it, and another for the rest of the
components. Splitting BusyBox allows for optimization that eliminates
the ``tinylogin`` recipe as recommended by upstream. You can disable
this split by setting
:term:`BUSYBOX_SPLIT_SUID` to "0".

.. _migration-1.5-automated-image-testing:

Automated Image Testing
-----------------------

A new automated image testing framework has been added through the
:ref:`ref-classes-testimage` classes. This
framework replaces the older ``imagetest-qemu`` framework.

You can learn more about performing automated image tests in the
":ref:`dev-manual/runtime-testing:performing automated runtime testing`"
section in the Yocto Project Development Tasks Manual.

.. _migration-1.5-build-history:

Build History
-------------

The changes to Build History are:

-  Installed package sizes: ``installed-package-sizes.txt`` for an image
   now records the size of the files installed by each package instead
   of the size of each compressed package archive file.

-  The dependency graphs (``depends*.dot``) now use the actual package
   names instead of replacing dashes, dots and plus signs with
   underscores.

-  The ``buildhistory-diff`` and ``buildhistory-collect-srcrevs``
   utilities have improved command-line handling. Use the ``--help``
   option for each utility for more information on the new syntax.

For more information on Build History, see the
":ref:`dev-manual/build-quality:maintaining build output quality`"
section in the Yocto Project Development Tasks Manual.

.. _migration-1.5-udev:

``udev``
--------

The changes to ``udev`` are:

-  ``udev`` no longer brings in ``udev-extraconf`` automatically through
   :term:`RRECOMMENDS`, since this was originally
   intended to be optional. If you need the extra rules, then add
   ``udev-extraconf`` to your image.

-  ``udev`` no longer brings in ``pciutils-ids`` or ``usbutils-ids``
   through :term:`RRECOMMENDS`. These are not needed by ``udev`` itself and
   removing them saves around 350KB.

.. _migration-1.5-removed-renamed-recipes:

Removed and Renamed Recipes
---------------------------

-  The ``linux-yocto`` 3.2 kernel has been removed.

-  ``libtool-nativesdk`` has been renamed to ``nativesdk-libtool``.

-  ``tinylogin`` has been removed. It has been replaced by a suid
   portion of Busybox. See the ":ref:`migration-1.5-busybox`"
   section for more information.

-  ``external-python-tarball`` has been renamed to
   ``buildtools-tarball``.

-  ``web-webkit`` has been removed. It has been functionally replaced by
   ``midori``.

-  ``imake`` has been removed. It is no longer needed by any other
   recipe.

-  ``transfig-native`` has been removed. It is no longer needed by any
   other recipe.

-  ``anjuta-remote-run`` has been removed. Anjuta IDE integration has
   not been officially supported for several releases.

.. _migration-1.5-other-changes:

Other Changes
-------------

Here is a list of short entries describing other changes:

-  ``run-postinsts``: Make this generic.

-  ``base-files``: Remove the unnecessary ``media/``\ xxx directories.

-  ``alsa-state``: Provide an empty ``asound.conf`` by default.

-  ``classes/image``: Ensure
   :term:`BAD_RECOMMENDATIONS` supports
   pre-renamed package names.

-  ``classes/rootfs_rpm``: Implement :term:`BAD_RECOMMENDATIONS` for RPM.

-  ``systemd``: Remove ``systemd_unitdir`` if ``systemd`` is not in
   :term:`DISTRO_FEATURES`.

-  ``systemd``: Remove ``init.d`` dir if ``systemd`` unit file is
   present and ``sysvinit`` is not a distro feature.

-  ``libpam``: Deny all services for the ``OTHER`` entries.

-  :ref:`ref-classes-image`: Move ``runtime_mapping_rename`` to avoid conflict
   with ``multilib``. See :yocto_bugs:`YOCTO #4993 </show_bug.cgi?id=4993>`
   in Bugzilla for more information.

-  ``linux-dtb``: Use kernel build system to generate the ``dtb`` files.

-  ``kern-tools``: Switch from guilt to new ``kgit-s2q`` tool.

