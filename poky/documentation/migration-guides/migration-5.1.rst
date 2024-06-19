.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 5.1 (styhead)
=====================

Migration notes for 5.1 (styhead)
------------------------------------

This section provides migration information for moving to the Yocto
Project 5.1 Release (codename "styhead") from the prior release.

.. _migration-5.1-supported-kernel-versions:

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

On the other hand, some earlier distributions are no longer supported:

See :ref:`all supported distributions <system-requirements-supported-distros>`.

.. _migration-5.1-go-changes:

Go language changes
~~~~~~~~~~~~~~~~~~~

.. _migration-5.1-systemd-changes:

systemd changes
~~~~~~~~~~~~~~~

.. _migration-5.1-recipe-changes:

Recipe changes
~~~~~~~~~~~~~~

.. _migration-5.1-deprecated-variables:

Deprecated variables
~~~~~~~~~~~~~~~~~~~~

.. _migration-5.1-removed-variables:

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:

.. _migration-5.1-removed-recipes:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

.. _migration-5.1-removed-classes:

Removed classes
~~~~~~~~~~~~~~~

No classes have been removed in this release.

.. _migration-5.1-qemu-changes:

QEMU changes
~~~~~~~~~~~~

.. _migration-5.1-misc-changes:

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

