.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: walnascar
.. |yocto-ver| replace:: 5.2
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Release |yocto-ver| (|yocto-codename|)
======================================

Migration notes for |yocto-ver| (|yocto-codename|)
--------------------------------------------------

This section provides migration information for moving to the Yocto
Project |yocto-ver| Release (codename "|yocto-codename|") from the prior release.

``debug-tweaks`` removed from :term:`IMAGE_FEATURES`
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``debug-tweaks`` image feature is now removed because it was too vague about
what it achieved: an image on which the ``root`` user can login without a
password.

To achieve the same result, the features previously added by ``debug-tweaks``
should be manually added to the :term:`IMAGE_FEATURES` variable. These are:

-  ``allow-empty-password``
-  ``allow-root-login``
-  ``empty-root-password``
-  ``post-install-logging``

Such a statement would be::

   IMAGE_FEATURES += "allow-empty-password allow-root-login empty-root-password post-install-logging"

See the list of available image features in the :ref:`ref-features-image`
section of the Yocto Project Reference Manual.

Supported kernel versions
~~~~~~~~~~~~~~~~~~~~~~~~~

The :term:`OLDEST_KERNEL` setting is XXX in this release, meaning that
out the box, older kernels are not supported. See :ref:`4.3 migration notes
<migration-4.3-supported-kernel-versions>` for details.

Supported distributions
~~~~~~~~~~~~~~~~~~~~~~~

Compared to the previous releases, running BitBake is supported on new
GNU/Linux distributions:

On the other hand, some earlier distributions are no longer supported:

See :ref:`all supported distributions <system-requirements-supported-distros>`.

Go language changes
~~~~~~~~~~~~~~~~~~~

Rust language changes
~~~~~~~~~~~~~~~~~~~~~

-  Recipes inheriting the :ref:`ref-classes-cargo` do not install libraries by
   default anymore. This behavior can be controlled by the
   :term:`CARGO_INSTALL_LIBRARIES` variable.

systemd changes
~~~~~~~~~~~~~~~

-  Support for ``split-usr`` and ``unmerged-usr`` are now removed after Systemd
   was updated to version 255. This option allowed installing Systemd in a
   system where directories such as ``/lib``, ``/sbin`` or ``/bin`` are *not*
   merged into ``/usr``.

   As a consequence, the ``systemd`` recipe no longer contains the ``usrmerge``
   :term:`PACKAGECONFIG` option as it is now implied by default.

-  ``systemd.bbclass``: If a ``systemd`` service file had referred to other service
   files by starting them via
   `Also <https://www.freedesktop.org/software/systemd/man/latest/systemd.unit.html#Also=>`__,
   the other service files were automatically added to the :term:`FILES` variable of
   the same package. Example: 

   a.service contains::

      [Install]
      Also=b.service

   If ``a.service`` is packaged in package ``A``, ``b.service`` was
   automatically packaged into package ``A`` as well. This happened even if
   ``b.service`` was explicitly added to package ``B`` using :term:`FILES` and
   :term:`SYSTEMD_SERVICE` variables.
   This prevented such services from being packaged into different packages.
   Therefore, this automatic behavior has been removed for service files (but
   not for socket files).
   Now all service files must be explicitly added to :term:`FILES`.

Recipe changes
~~~~~~~~~~~~~~

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:

-  ``PACKAGE_SNAP_LIB_SYMLINKS``: related to an unmaintained and obsolete
   "micro" :term:`distro <DISTRO>`.

-  ``SETUPTOOLS_INSTALL_ARGS``: obsolete and unused variable.

-  ``BB_DANGLINGAPPENDS_WARNONLY``: support for only warning the user when a
   ``.bbappend`` file doesn't apply to the original recipe has been dropped. See
   the :ref:`dev-manual/layers:Appending Other Layers Metadata With Your Layer`
   section of the Yocto Project Development Tasks Manual for alternatives to
   this variable.

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

-  ``liburi-perl``: moved to :oe_git:`meta-perl </meta-openembedded/tree/meta-perl>`.
-  ``python3-isodate``: moved to :oe_git:`meta-python </meta-openembedded/tree/meta-python>`.
-  ``python3-iniparse``: removed as there are no consumers of this recipe in
   :oe_git:`openembedded-core </openembedded-core>` or :oe_git:`meta-openembedded </meta-openembedded>`.

Removed classes
~~~~~~~~~~~~~~~

The following classes have been removed in this release:

-  ``migrate_localcount.bbclass``: obsolete class for which code was already
   removed in 2012.

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

-  :term:`ZSTD_COMPRESSION_LEVEL` is now a plain integer number instead of a dash-prefixed
   command-line option (e.g. it should be set to ``3`` rather than ``-3``).
