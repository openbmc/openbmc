Moving to the Yocto Project 1.6 Release
=======================================

This section provides migration information for moving to the Yocto
Project 1.6 Release from the prior release.

.. _migration-1.6-archiver-class:

``archiver`` Class
------------------

The :ref:`archiver <ref-classes-archiver>` class has been rewritten
and its configuration has been simplified. For more details on the
source archiver, see the
":ref:`dev-manual/common-tasks:maintaining open source license compliance during your product's lifecycle`"
section in the Yocto Project Development Tasks Manual.

.. _migration-1.6-packaging-changes:

Packaging Changes
-----------------

The following packaging changes have been made:

-  The ``binutils`` recipe no longer produces a ``binutils-symlinks``
   package. ``update-alternatives`` is now used to handle the preferred
   ``binutils`` variant on the target instead.

-  The tc (traffic control) utilities have been split out of the main
   ``iproute2`` package and put into the ``iproute2-tc`` package.

-  The ``gtk-engines`` schemas have been moved to a dedicated
   ``gtk-engines-schemas`` package.

-  The ``armv7a`` with thumb package architecture suffix has changed.
   The suffix for these packages with the thumb optimization enabled is
   "t2" as it should be. Use of this suffix was not the case in the 1.5
   release. Architecture names will change within package feeds as a
   result.

.. _migration-1.6-bitbake:

BitBake
-------

The following changes have been made to :term:`BitBake`.

.. _migration-1.6-matching-branch-requirement-for-git-fetching:

Matching Branch Requirement for Git Fetching
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When fetching source from a Git repository using
:term:`SRC_URI`, BitBake will now validate the
:term:`SRCREV` value against the branch. You can specify
the branch using the following form:
::

      SRC_URI = "git://server.name/repository;branch=branchname"

If you do not specify a branch, BitBake looks in the default "master" branch.

Alternatively, if you need to bypass this check (e.g. if you are
fetching a revision corresponding to a tag that is not on any branch),
you can add ";nobranch=1" to the end of the URL within ``SRC_URI``.

.. _migration-1.6-bitbake-deps:

Python Definition substitutions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

BitBake had some previously deprecated Python definitions within its
``bb`` module removed. You should use their sub-module counterparts
instead:

-  ``bb.MalformedUrl``: Use ``bb.fetch.MalformedUrl``.

-  ``bb.encodeurl``: Use ``bb.fetch.encodeurl``.

-  ``bb.decodeurl``: Use ``bb.fetch.decodeurl``

-  ``bb.mkdirhier``: Use ``bb.utils.mkdirhier``.

-  ``bb.movefile``: Use ``bb.utils.movefile``.

-  ``bb.copyfile``: Use ``bb.utils.copyfile``.

-  ``bb.which``: Use ``bb.utils.which``.

-  ``bb.vercmp_string``: Use ``bb.utils.vercmp_string``.

-  ``bb.vercmp``: Use ``bb.utils.vercmp``.

.. _migration-1.6-bitbake-fetcher:

SVK Fetcher
~~~~~~~~~~~

The SVK fetcher has been removed from BitBake.

.. _migration-1.6-bitbake-console-output:

Console Output Error Redirection
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The BitBake console UI will now output errors to ``stderr`` instead of
``stdout``. Consequently, if you are piping or redirecting the output of
``bitbake`` to somewhere else, and you wish to retain the errors, you
will need to add ``2>&1`` (or something similar) to the end of your
``bitbake`` command line.

.. _migration-1.6-task-taskname-overrides:

``task-``\ taskname Overrides
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

``task-``\ taskname overrides have been adjusted so that tasks whose
names contain underscores have the underscores replaced by hyphens for
the override so that they now function properly. For example, the task
override for :ref:`ref-tasks-populate_sdk` is
``task-populate-sdk``.

.. _migration-1.6-variable-changes:

Changes to Variables
--------------------

The following variables have changed. For information on the
OpenEmbedded build system variables, see the ":doc:`/ref-manual/variables`" Chapter.

.. _migration-1.6-variable-changes-TMPDIR:

``TMPDIR``
~~~~~~~~~~

:term:`TMPDIR` can no longer be on an NFS mount. NFS does
not offer full POSIX locking and inode consistency and can cause
unexpected issues if used to store ``TMPDIR``.

The check for this occurs on startup. If ``TMPDIR`` is detected on an
NFS mount, an error occurs.

.. _migration-1.6-variable-changes-PRINC:

``PRINC``
~~~~~~~~~

The ``PRINC`` variable has been deprecated and triggers a warning if
detected during a build. For :term:`PR` increments on changes,
use the PR service instead. You can find out more about this service in
the ":ref:`dev-manual/common-tasks:working with a pr service`"
section in the Yocto Project Development Tasks Manual.

.. _migration-1.6-variable-changes-IMAGE_TYPES:

``IMAGE_TYPES``
~~~~~~~~~~~~~~~

The "sum.jffs2" option for :term:`IMAGE_TYPES` has
been replaced by the "jffs2.sum" option, which fits the processing
order.

.. _migration-1.6-variable-changes-COPY_LIC_MANIFEST:

``COPY_LIC_MANIFEST``
~~~~~~~~~~~~~~~~~~~~~

The :term:`COPY_LIC_MANIFEST` variable must now
be set to "1" rather than any value in order to enable it.

.. _migration-1.6-variable-changes-COPY_LIC_DIRS:

``COPY_LIC_DIRS``
~~~~~~~~~~~~~~~~~

The :term:`COPY_LIC_DIRS` variable must now be set
to "1" rather than any value in order to enable it.

.. _migration-1.6-variable-changes-PACKAGE_GROUP:

``PACKAGE_GROUP``
~~~~~~~~~~~~~~~~~

The ``PACKAGE_GROUP`` variable has been renamed to
:term:`FEATURE_PACKAGES` to more accurately
reflect its purpose. You can still use ``PACKAGE_GROUP`` but the
OpenEmbedded build system produces a warning message when it encounters
the variable.

.. _migration-1.6-variable-changes-variable-entry-behavior:

Preprocess and Post Process Command Variable Behavior
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following variables now expect a semicolon separated list of
functions to call and not arbitrary shell commands:

  - :term:`ROOTFS_PREPROCESS_COMMAND`
  - :term:`ROOTFS_POSTPROCESS_COMMAND`
  - :term:`SDK_POSTPROCESS_COMMAND`
  - :term:`POPULATE_SDK_POST_TARGET_COMMAND`
  - :term:`POPULATE_SDK_POST_HOST_COMMAND`
  - :term:`IMAGE_POSTPROCESS_COMMAND`
  - :term:`IMAGE_PREPROCESS_COMMAND`
  - :term:`ROOTFS_POSTUNINSTALL_COMMAND`
  - :term:`ROOTFS_POSTINSTALL_COMMAND`

For
migration purposes, you can simply wrap shell commands in a shell
function and then call the function. Here is an example: ::

   my_postprocess_function() {
      echo "hello" > ${IMAGE_ROOTFS}/hello.txt
   }
   ROOTFS_POSTPROCESS_COMMAND += "my_postprocess_function; "

.. _migration-1.6-package-test-ptest:

Package Test (ptest)
--------------------

Package Tests (ptest) are built but not installed by default. For
information on using Package Tests, see the
":ref:`dev-manual/common-tasks:testing packages with ptest`"
section in the Yocto Project Development Tasks Manual. For information on the
``ptest`` class, see the ":ref:`ptest.bbclass <ref-classes-ptest>`"
section.

.. _migration-1.6-build-changes:

Build Changes
-------------

Separate build and source directories have been enabled by default for
selected recipes where it is known to work (a whitelist) and for all
recipes that inherit the :ref:`cmake <ref-classes-cmake>` class. In
future releases the :ref:`autotools <ref-classes-autotools>` class
will enable a separate build directory by default as well. Recipes
building Autotools-based software that fails to build with a separate
build directory should be changed to inherit from the
:ref:`autotools-brokensep <ref-classes-autotools>` class instead of
the ``autotools`` or ``autotools_stage``\ classes.

.. _migration-1.6-building-qemu-native:

``qemu-native``
---------------

``qemu-native`` now builds without SDL-based graphical output support by
default. The following additional lines are needed in your
``local.conf`` to enable it:
::

   PACKAGECONFIG_pn-qemu-native = "sdl"
   ASSUME_PROVIDED += "libsdl-native"

.. note::

   The default ``local.conf`` contains these statements. Consequently, if you
   are building a headless system and using a default ``local.conf``
   file, you will need comment these two lines out.

.. _migration-1.6-core-image-basic:

``core-image-basic``
--------------------

``core-image-basic`` has been renamed to ``core-image-full-cmdline``.

In addition to ``core-image-basic`` being renamed,
``packagegroup-core-basic`` has been renamed to
``packagegroup-core-full-cmdline`` to match.

.. _migration-1.6-licensing:

Licensing
---------

The top-level ``LICENSE`` file has been changed to better describe the
license of the various components of :term:`OpenEmbedded-Core (OE-Core)`. However,
the licensing itself remains unchanged.

Normally, this change would not cause any side-effects. However, some
recipes point to this file within
:term:`LIC_FILES_CHKSUM` (as
``${COREBASE}/LICENSE``) and thus the accompanying checksum must be
changed from 3f40d7994397109285ec7b81fdeb3b58 to
4d92cd373abda3937c2bc47fbc49d690. A better alternative is to have
``LIC_FILES_CHKSUM`` point to a file describing the license that is
distributed with the source that the recipe is building, if possible,
rather than pointing to ``${COREBASE}/LICENSE``.

.. _migration-1.6-cflags-options:

``CFLAGS`` Options
------------------

The "-fpermissive" option has been removed from the default
:term:`CFLAGS` value. You need to take action on
individual recipes that fail when building with this option. You need to
either patch the recipes to fix the issues reported by the compiler, or
you need to add "-fpermissive" to ``CFLAGS`` in the recipes.

.. _migration-1.6-custom-images:

Custom Image Output Types
-------------------------

Custom image output types, as selected using
:term:`IMAGE_FSTYPES`, must declare their
dependencies on other image types (if any) using a new
:term:`IMAGE_TYPEDEP` variable.

.. _migration-1.6-do-package-write-task:

Tasks
-----

The ``do_package_write`` task has been removed. The task is no longer
needed.

.. _migration-1.6-update-alternatives-provider:

``update-alternative`` Provider
-------------------------------

The default ``update-alternatives`` provider has been changed from
``opkg`` to ``opkg-utils``. This change resolves some troublesome
circular dependencies. The runtime package has also been renamed from
``update-alternatives-cworth`` to ``update-alternatives-opkg``.

.. _migration-1.6-virtclass-overrides:

``virtclass`` Overrides
-----------------------

The ``virtclass`` overrides are now deprecated. Use the equivalent class
overrides instead (e.g. ``virtclass-native`` becomes ``class-native``.)

.. _migration-1.6-removed-renamed-recipes:

Removed and Renamed Recipes
---------------------------

The following recipes have been removed:

-  ``packagegroup-toolset-native`` - This recipe is largely unused.

-  ``linux-yocto-3.8`` - Support for the Linux yocto 3.8 kernel has been
   dropped. Support for the 3.10 and 3.14 kernels have been added with
   the ``linux-yocto-3.10`` and ``linux-yocto-3.14`` recipes.

-  ``ocf-linux`` - This recipe has been functionally replaced using
   ``cryptodev-linux``.

-  ``genext2fs`` - ``genext2fs`` is no longer used by the build system
   and is unmaintained upstream.

-  ``js`` - This provided an ancient version of Mozilla's javascript
   engine that is no longer needed.

-  ``zaurusd`` - The recipe has been moved to the ``meta-handheld``
   layer.

-  ``eglibc 2.17`` - Replaced by the ``eglibc 2.19`` recipe.

-  ``gcc 4.7.2`` - Replaced by the now stable ``gcc 4.8.2``.

-  ``external-sourcery-toolchain`` - this recipe is now maintained in
   the ``meta-sourcery`` layer.

-  ``linux-libc-headers-yocto 3.4+git`` - Now using version 3.10 of the
   ``linux-libc-headers`` by default.

-  ``meta-toolchain-gmae`` - This recipe is obsolete.

-  ``packagegroup-core-sdk-gmae`` - This recipe is obsolete.

-  ``packagegroup-core-standalone-gmae-sdk-target`` - This recipe is
   obsolete.

.. _migration-1.6-removed-classes:

Removed Classes
---------------

The following classes have become obsolete and have been removed:

-  ``module_strip``

-  ``pkg_metainfo``

-  ``pkg_distribute``

-  ``image-empty``

.. _migration-1.6-reference-bsps:

Reference Board Support Packages (BSPs)
---------------------------------------

The following reference BSPs changes occurred:

-  The BeagleBoard (``beagleboard``) ARM reference hardware has been
   replaced by the BeagleBone (``beaglebone``) hardware.

-  The RouterStation Pro (``routerstationpro``) MIPS reference hardware
   has been replaced by the EdgeRouter Lite (``edgerouter``) hardware.

The previous reference BSPs for the ``beagleboard`` and
``routerstationpro`` machines are still available in a new
``meta-yocto-bsp-old`` layer in the
:yocto_git:`Source Repositories <>` at
:yocto_git:`/meta-yocto-bsp-old/`.


