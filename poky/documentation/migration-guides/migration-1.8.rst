.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 1.8 (fido)
==================

This section provides migration information for moving to the Yocto
Project 1.8 Release (codename "fido") from the prior release.

.. _migration-1.8-removed-recipes:

Removed Recipes
---------------

The following recipes have been removed:

-  ``owl-video``: Functionality replaced by ``gst-player``.

-  ``gaku``: Functionality replaced by ``gst-player``.

-  ``gnome-desktop``: This recipe is now available in ``meta-gnome`` and
   is no longer needed.

-  ``gsettings-desktop-schemas``: This recipe is now available in
   ``meta-gnome`` and is no longer needed.

-  ``python-argparse``: The ``argparse`` module is already provided in
   the default Python distribution in a package named
   ``python-argparse``. Consequently, the separate ``python-argparse``
   recipe is no longer needed.

-  ``telepathy-python, libtelepathy, telepathy-glib, telepathy-idle, telepathy-mission-control``:
   All these recipes have moved to ``meta-oe`` and are consequently no
   longer needed by any recipes in OpenEmbedded-Core.

-  ``linux-yocto_3.10`` and ``linux-yocto_3.17``: Support for the
   linux-yocto 3.10 and 3.17 kernels has been dropped. Support for the
   3.14 kernel remains, while support for 3.19 kernel has been added.

-  ``poky-feed-config-opkg``: This recipe has become obsolete and is no
   longer needed. Use ``distro-feed-config`` from ``meta-oe`` instead.

-  ``libav 0.8.x``: ``libav 9.x`` is now used.

-  ``sed-native``: No longer needed. A working version of ``sed`` is
   expected to be provided by the host distribution.

.. _migration-1.8-bluez:

BlueZ 4.x / 5.x Selection
-------------------------

Proper built-in support for selecting BlueZ 5.x in preference to the
default of 4.x now exists. To use BlueZ 5.x, simply add "bluez5" to your
:term:`DISTRO_FEATURES` value. If you had
previously added append files (``*.bbappend``) to make this selection,
you can now remove them.

Additionally, a ``bluetooth`` class has been added to make selection of
the appropriate bluetooth support within a recipe a little easier. If
you wish to make use of this class in a recipe, add something such as
the following::

   inherit bluetooth
   PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '${BLUEZ}', '', d)}"
   PACKAGECONFIG[bluez4] = "--enable-bluetooth,--disable-bluetooth,bluez4"
   PACKAGECONFIG[bluez5] = "--enable-bluez5,--disable-bluez5,bluez5"

.. _migration-1.8-kernel-build-changes:

Kernel Build Changes
--------------------

The kernel build process was changed to place the source in a common shared work
area and to place build artifacts separately in the source code tree. In theory,
migration paths have been provided for most common usages in kernel recipes but
this might not work in all cases. In particular, users need to ensure that
``${S}`` (source files) and ``${B}`` (build artifacts) are used correctly in
functions such as :ref:`ref-tasks-configure` and :ref:`ref-tasks-install`. For
kernel recipes that do not inherit from :ref:`ref-classes-kernel-yocto` or
include ``linux-yocto.inc``, you might wish to refer to the ``linux.inc`` file
in the ``meta-oe`` layer for the kinds of changes you need to make. For reference,
here is the
:oe_git:`commit </meta-openembedded/commit/meta-oe/recipes-kernel/linux/linux.inc?id=fc7132ede27ac67669448d3d2845ce7d46c6a1ee>`
where the ``linux.inc`` file in ``meta-oe`` was updated.

Recipes that rely on the kernel source code and do not inherit the
:ref:`module <ref-classes-module>` classes might need to add explicit
dependencies on the :ref:`ref-tasks-shared_workdir` kernel task, for example::

   do_configure[depends] += "virtual/kernel:do_shared_workdir"

.. _migration-1.8-ssl:

SSL 3.0 is Now Disabled in OpenSSL
----------------------------------

SSL 3.0 is now disabled when building OpenSSL. Disabling SSL 3.0 avoids
any lingering instances of the POODLE vulnerability. If you feel you
must re-enable SSL 3.0, then you can add an append file (``*.bbappend``)
for the ``openssl`` recipe to remove "-no-ssl3" from
:term:`EXTRA_OECONF`.

.. _migration-1.8-default-sysroot-poisoning:

Default Sysroot Poisoning
-------------------------

``gcc's`` default sysroot and include directories are now "poisoned". In
other words, the sysroot and include directories are being redirected to
a non-existent location in order to catch when host directories are
being used due to the correct options not being passed. This poisoning
applies both to the cross-compiler used within the build and to the
cross-compiler produced in the SDK.

If this change causes something in the build to fail, it almost
certainly means the various compiler flags and commands are not being
passed correctly to the underlying piece of software. In such cases, you
need to take corrective steps.

.. _migration-1.8-rebuild-improvements:

Rebuild Improvements
--------------------

Changes have been made to the :ref:`ref-classes-base`,
:ref:`ref-classes-autotools`, and :ref:`ref-classes-cmake` classes to clean out
generated files when the :ref:`ref-tasks-configure` task needs to be
re-executed.

One of the improvements is to attempt to run "make clean" during the
:ref:`ref-tasks-configure` task if a ``Makefile`` exists. Some software packages
do not provide a working clean target within their make files. If you
have such recipes, you need to set
:term:`CLEANBROKEN` to "1" within the recipe, for example::

   CLEANBROKEN = "1"

.. _migration-1.8-qa-check-and-validation-changes:

QA Check and Validation Changes
-------------------------------

The following QA Check and Validation Changes have occurred:

-  Usage of ``PRINC`` previously triggered a warning. It now triggers an
   error. You should remove any remaining usage of ``PRINC`` in any
   recipe or append file.

-  An additional QA check has been added to detect usage of ``${D}`` in
   :term:`FILES` values where :term:`D` values
   should not be used at all. The same check ensures that ``$D`` is used
   in ``pkg_preinst/pkg_postinst/pkg_prerm/pkg_postrm`` functions
   instead of ``${D}``.

-  :term:`S` now needs to be set to a valid value within a
   recipe. If :term:`S` is not set in the recipe, the directory is not
   automatically created. If :term:`S` does not point to a directory that
   exists at the time the :ref:`ref-tasks-unpack` task
   finishes, a warning will be shown.

-  :term:`LICENSE` is now validated for correct
   formatting of multiple licenses. If the format is invalid (e.g.
   multiple licenses are specified with no operators to specify how the
   multiple licenses interact), then a warning will be shown.

.. _migration-1.8-miscellaneous-changes:

Miscellaneous Changes
---------------------

The following miscellaneous changes have occurred:

-  The ``send-error-report`` script now expects a "-s" option to be
   specified before the server address. This assumes a server address is
   being specified.

-  The ``oe-pkgdata-util`` script now expects a "-p" option to be
   specified before the ``pkgdata`` directory, which is now optional. If
   the ``pkgdata`` directory is not specified, the script will run
   BitBake to query :term:`PKGDATA_DIR` from the
   build environment.


