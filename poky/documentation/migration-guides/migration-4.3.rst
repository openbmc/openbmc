.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 4.3 (nanbield)
========================

Migration notes for 4.3 (nanbield)
------------------------------------

This section provides migration information for moving to the Yocto
Project 4.3 Release (codename "nanbield") from the prior release.

.. _migration-4.3-supported-kernel-versions:

Supported kernel versions
~~~~~~~~~~~~~~~~~~~~~~~~~

The :term:`OLDEST_KERNEL` setting has been changed to "5.15" in this release, meaning that
out the box, older kernels are not supported. There were two reasons for this.
Firstly it allows glibc optimisations that improve the performance of the system
by removing compatibility code and using modern kernel APIs exclusively. The second
issue was this allows 64 bit time support even on 32 bit platforms and resolves Y2038
issues.

It is still possible to override this value and build for older kernels, this is just
no longer the default supported configuration. This setting does not affect which
kernel versions SDKs will run against and does not affect which versions of the kernel
can be used to run builds.

.. _migration-4.3-supported-distributions:

Supported distributions
~~~~~~~~~~~~~~~~~~~~~~~

This release supports running BitBake on new GNU/Linux distributions:

On the other hand, some earlier distributions are no longer supported:

See :ref:`all supported distributions <system-requirements-supported-distros>`.

.. _migration-4.3-go-changes:

Go language changes
~~~~~~~~~~~~~~~~~~~

-  Support for the Glide package manager has been removed, as ``go mod``
   has become the standard.

.. _migration-4.3-recipe-changes:

Recipe changes
~~~~~~~~~~~~~~

-  Runtime testing of ptest now fails if no test results are returned by
   any given ptest.

.. _migration-4.3-class-changes:

Class changes
~~~~~~~~~~~~~

-  The ``perl-version`` class no longer provides the ``PERLVERSION`` and ``PERLARCH`` variables
   as there were no users in any core layer. The functions for this functionality
   are still available.

.. _migration-4.3-removed-variables:

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:

-  ``PERLARCH``
-  ``PERLVERSION``

.. _migration-4.3-removed-recipes:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

-  ``glide``, as explained in :ref:`migration-4.3-go-changes`.

.. _migration-4.3-removed-classes:

Removed classes
~~~~~~~~~~~~~~~

The following classes have been removed in this release:


.. _migration-4.3-misc-changes:

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

-  The ``-crosssdk`` suffix and any :term:`MLPREFIX` were removed from
   ``virtual/XXX`` provider/dependencies where a ``PREFIX`` was used as well,
   as we don't need both and it made automated dependency rewriting
   unnecessarily complex. In general this only affects internal toolchain
   dependencies so isn't end user visible.

