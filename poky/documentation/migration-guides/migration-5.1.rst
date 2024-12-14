.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 5.1 (styhead)
=====================

Migration notes for 5.1 (styhead)
---------------------------------

This section provides migration information for moving to the Yocto
Project 5.1 Release (codename "styhead") from the prior release.

.. _migration-5.1-workdir-changes:

:term:`WORKDIR` changes
~~~~~~~~~~~~~~~~~~~~~~~

S = ${WORKDIR} no longer supported
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If a recipe has :term:`S` set to be :term:`WORKDIR`, this is no longer
supported, and an error will be issued. The recipe should be changed to::

    S = "${WORKDIR}/sources"
    UNPACKDIR = "${S}"

Any :term:`WORKDIR` references where files from :term:`SRC_URI` are referenced
should be changed to :term:`S`. These are commonly in :ref:`ref-tasks-compile`,
:ref:`ref-tasks-compile`, :ref:`ref-tasks-install` and :term:`LIC_FILES_CHKSUM`.

:term:`WORKDIR` references in recipes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

:term:`WORKDIR` references in other recipes need auditing. If they reference
files from :term:`SRC_URI`, they likely need changing to :term:`UNPACKDIR`.
These are commonly in :ref:`ref-tasks-compile` and :ref:`ref-tasks-install`
for things like service or configuration files. One unusual case is
``${WORKDIR}/${BP}`` which should probably be set to ``${S}``.

References to ``../`` in :term:`LIC_FILES_CHKSUM` or elsewhere may need changing
to :term:`UNPACKDIR`. References to :term:`WORKDIR` in ``sed`` commands are
usually left as they are.

General notes
^^^^^^^^^^^^^

Files from :ref:`ref-tasks-unpack` now unpack to ``WORKDIR/sources-unpack/``
rather than ``WORKDIR/``.

If :term:`S` is set to a subdirectory under :term:`WORKDIR` and that
subdirectory exists in ``sources-unpack`` after :ref:`ref-tasks-unpack` runs,
it is moved to :term:`WORKDIR`. This means that ``S = "${WORKDIR}/${BP}"``,
``S = "${WORKDIR}/git"`` and also deeper paths continue to work as expected
without changes. We cannot use symlinks to do this as it breaks autotools
based recipes. Keeping all sources under ``sources-unpack`` wasn't considered
as it meant more invasive recipes changes. The key objective was separating the
:ref:`ref-tasks-unpack` task output from :term:`WORKDIR`.

Previously, :term:`S` was always created but after the recent changes it is no
longer the case. This means the check in ``do_unpack_qa`` triggers where
:term:`S` is not created by a recipe while it didn't happen before. This can
require to add an :term:`S` definition to a recipe that only uses
``file://`` :term:`SRC_URI` entries. To be consistent, the following pattern is
recommended::

    S = "${WORKDIR}/sources"
    UNPACKDIR = "${S}"

Building C files from :term:`UNPACKDIR` without setting :term:`S` to point at
it does not work as the debug prefix mapping doesn't handle that.

``devtool``  and ``recipetool`` have been updated to handle this and their
support for ``S = WORKDIR`` and ``oe-local-files`` has been removed.

.. _migration-5.1-supported-kernel-versions:

Supported kernel versions
~~~~~~~~~~~~~~~~~~~~~~~~~

The :term:`OLDEST_KERNEL` setting is still "5.15" in this release, meaning that
out the box, older kernels are not supported. See :ref:`4.3 migration notes
<migration-4.3-supported-kernel-versions>` for details.

.. _migration-5.1-supported-distributions:

Supported distributions
~~~~~~~~~~~~~~~~~~~~~~~

Compared to the previous releases, running BitBake is supported on new
GNU/Linux distributions:

-  Ubuntu 24.10
-  Fedora 40
-  OpenSUSE Leap 15.5
-  OpenSUSE Leap 15.6

On the other hand, some earlier distributions are no longer supported:

-  Ubuntu 23.04

See :ref:`all supported distributions <system-requirements-supported-distros>`.

.. _migration-5.1-go-changes:

Go language changes
~~~~~~~~~~~~~~~~~~~

-  After dropping the custom :ref:`ref-tasks-unpack` from the
   :ref:`ref-classes-go` class, go recipes should now add
   ``destsuffix=${GO_SRCURI_DESTSUFFIX}`` to their :term:`SRC_URI` to extract
   them in the appropriate path. An example would be::

      SRC_URI = "git://go.googlesource.com/example;branch=master;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX}"

-  Go modules are no longer compiled with ``--linkmode=external``.

.. _migration-5.1-systemd-changes:

systemd changes
~~~~~~~~~~~~~~~

-  New :term:`PACKAGECONFIG` value ``bpf-framework`` used to pre-compile eBPFs
   that are required for the systemd.resource-control features
   ``RestrictFileSystems`` and ``RestrictNetworkInterfaces``.

.. _migration-5.1-recipe-changes:

Recipe changes
~~~~~~~~~~~~~~

-  ``gobject-introspection``: the ``giscanner`` utility is now shipped as a
   separate package in ``gobject-introspection-tools``.

-  ``perf`` no longer uses ``libnewt`` for compiling its TUI.

-  ``openssl``: do not build the test suite unless ptests are enabled.

.. _migration-5.1-removed-variables:

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:

-  ``TCLIBCAPPEND`` is now removed as sharing :term:`TMPDIR` for multiple libc
   providers has been supported for years.

-  ``VOLATILE_LOG_DIR``: :term:`FILESYSTEM_PERMS_TABLES` is now used instead.
   By default, :term:`FILESYSTEM_PERMS_TABLES` now contains the value
   ``files/fs-perms-volatile-log.txt``, which means that volatile log is
   enabled. Users can disable the volatile log by removing the value
   ``files/fs-perms-volatile-log.txt`` from :term:`FILESYSTEM_PERMS_TABLES`.

-  ``VOLATILE_TMP_DIR``: :term:`FILESYSTEM_PERMS_TABLES` is now used instead.
   By default, :term:`FILESYSTEM_PERMS_TABLES` now contains the value
   ``files/fs-perms-volatile-tmp.txt``, which means that volatile tmp is
   enabled. Users can disable the volatile tmp by removing the value
   ``files/fs-perms-volatile-tmp.txt`` from :term:`FILESYSTEM_PERMS_TABLES`.

.. _migration-5.1-removed-recipes:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

-  ``liba52``: superseded by ``ffmpeg``
-  ``libomxil``: recipe removed as its only consumer, the gstreamer omx plugin,
    was removed and has not been developed for several years
-  ``libnewt``: moved to meta-oe
-  ``mpeg2dec``: inactive for 10 years and superseded by ``ffmpeg``
-  ``pytest-runner``: moved to meta-python
-  ``python3-importlib-metadata``: moved to meta-python
-  ``python3-pathlib2``: moved to meta-python
-  ``python3-py``: moved to meta-python
-  ``python3-rfc3986-validator``: moved to meta-python
-  ``python3-toml``: moved to meta-python
-  ``python3-tomli``: moved to meta-python
-  ``usbinit``: recipe was poorly named as it is a gadget Ethernet driver.
   Gadget Ethernet is of questionable use now and usbinit isn't referenced/used
   anywhere within OE-Core.


.. _migration-5.1-removed-classes:

Removed classes
~~~~~~~~~~~~~~~

The following classes have been removed in this release:

-  ``siteconfig``:  removed as it was only used by ``ncurses`` and ``zlib`` and
   adding minimal added-value for a considerable amount of added runtime.


.. _migration-5.1-misc-changes:

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

-  `oe-selftest` now only rewrites environment variable paths that absolutely
   point to builddir (i.e ``X=$BUILDDIR/conf/`` is still rewritten to point to
   the `oe-selftest` ``conf/`` directory but not ``Y=$BUILDDIR/../bitbake/`` which
   still point to the ``bitbake/`` directory)

   For example (taken from :yocto_ab:`autobuilder <>` environment):
   :term:`BB_LOGCONFIG` is set as:
   ``BB_LOGCONFIG="${BUILDDIR}/../bitbake/contrib/autobuilderlog.json"``.
   Note the relative path starting from the build directory to outside of it.
   This path is not changed by `oe-selftest` anymore.

   Environment variables containing relative paths from tested build directory
   to outside of the original build directory may need to be updated as they
   won't be changed by `oe-selftest`.

-  Several sanity checks from the :ref:`ref-classes-insane` class, such as
   ``buildpaths``, have been promoted to errors instead of warnings.

-  The ``license-incompatible`` :term:`ERROR_QA` sanity check was renamed to
   ``license-exception``.
