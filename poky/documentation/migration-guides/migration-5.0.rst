.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 5.0 LTS (scarthgap)
===========================

Migration notes for 5.0 (scarthgap)
------------------------------------

This section provides migration information for moving to the Yocto
Project 5.0 Release (codename "scarthgap") from the prior release.

To migrate from an earlier LTS release, you **also** need to check all
the previous migration notes from your release to this new one:

-  :doc:`/migration-guides/migration-4.3`
-  :doc:`/migration-guides/migration-4.2`
-  :doc:`/migration-guides/migration-4.1`
-  :doc:`/migration-guides/migration-4.0`
-  :doc:`/migration-guides/migration-3.4`
-  :doc:`/migration-guides/migration-3.3`
-  :doc:`/migration-guides/migration-3.2`

.. _migration-5.0-supported-kernel-versions:

Supported kernel versions
~~~~~~~~~~~~~~~~~~~~~~~~~

The :term:`OLDEST_KERNEL` setting is still "5.15" in this release, meaning that
out the box, older kernels are not supported. See :ref:`4.3 migration notes
<migration-4.3-supported-kernel-versions>` for details.

.. _migration-5.0-supported-distributions:

Supported distributions
~~~~~~~~~~~~~~~~~~~~~~~

Compared to the previous releases, running BitBake is supported on new
GNU/Linux distributions:

-  Rocky 9

On the other hand, some earlier distributions are no longer supported:

-  Fedora 37
-  Ubuntu 22.10
-  OpenSUSE Leap 15.3

See :ref:`all supported distributions <system-requirements-supported-distros>`.

.. _migration-5.0-go-changes:

Go language changes
~~~~~~~~~~~~~~~~~~~

The ``linkmode`` flag was dropped from ``GO_LDFLAGS`` for ``nativesdk`` and
``cross-canadian``. Also, dynamic linking was disabled for the whole set of
(previously) supported architectures in the ``goarch`` class.

.. _migration-5.0-systemd-changes:

systemd changes
~~~~~~~~~~~~~~~

Systemd's nss-resolve plugin is now supported and can be added via the
``nss-resolve`` :term:`PACKAGECONFIG` option , which is from now on required
(along with ``resolved``) by the ``systemd-resolved`` feature. Related to that
(i.e., Systemd's network name resolution), an option to use ``stub-resolv.conf``
was added as well.

.. _migration-5.0-recipe-changes:

Recipe changes
~~~~~~~~~~~~~~

-  Runtime testing of ptest now fails if no test results are returned by
   any given ptest.

.. _migration-5.0-deprecated-variables:

Deprecated variables
~~~~~~~~~~~~~~~~~~~~

No variables have been deprecated in this release.

.. _migration-5.0-removed-variables:

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:

-  ``DEPLOY_DIR_TAR``.
-  ``PYTHON_PN``: Python 2 has been removed, leaving Python 3 as the sole
   major version. Therefore, an abstraction to differentiate both versions is
   no longer needed.
-  ``oldincludedir``.
-  ``USE_L10N``: previously deprecated, and now removed.
-  ``CVE_SOCKET_TIMEOUT``.

.. _migration-5.0-removed-recipes:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

-  ``libcroco``: deprecated and archived by the Gnome Project.
-  ``linux-yocto``: version 6.1 (version 6.6 provided instead).
-  ``zvariant``: fails to build with newer Rust.
-  ``systemtap-uprobes``: obsolete.

.. _migration-5.0-removed-classes:

Removed classes
~~~~~~~~~~~~~~~

No classes have been removed in this release.

.. _migration-5.0-qemu-changes:

QEMU changes
~~~~~~~~~~~~

In ``tune-core2``, the cpu models ``n270`` and ``core2duo`` are no longer
passed to QEMU, since its documentation recommends not using them with ``-cpu``
option. Therefore, from now on, ``Nehalem`` model is used instead.

.. _migration-5.0-misc-changes:

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

-  ``bitbake-whatchanged`` script was removed.
-  ``ccache`` no longer supports FORTRAN.
