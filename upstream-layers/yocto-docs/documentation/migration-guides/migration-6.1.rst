.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: blacksail
.. |yocto-ver| replace:: 6.1
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Migration notes for |yocto-ver| (|yocto-codename|)
**************************************************

This section provides migration information for moving to the Yocto
Project |yocto-ver| Release (codename "|yocto-codename|") from the prior release.
For a list of new features and enhancements, see the
:doc:`/migration-guides/release-notes-6.1` section.

Supported kernel versions
-------------------------

The :term:`OLDEST_KERNEL` setting is XXX in this release, meaning that
out the box, older kernels are not supported. See :ref:`4.3 migration notes
<migration-4.3-supported-kernel-versions>` for details.

Supported distributions
-----------------------

Compared to the previous releases, running BitBake is supported on new
GNU/Linux distributions:

-  XXX

On the other hand, some earlier distributions are no longer supported:

-  XXX

See :ref:`all supported distributions <system-requirements-supported-distros>`.

.. _ref-migration-6-1-groupmems:

useradd: replace :term:`GROUPMEMS_PARAM` assignments to :term:`USERMOD_PARAMS`
------------------------------------------------------------------------------

The ``groupmems`` command is removed from the ``shadow`` recipe starting from
version 4.20. The same functionality provided by ``groupmems`` can be achieved
with the ``usermod`` command.

Assignments made to the :term:`GROUPMEMS_PARAM` variable can be converted to use
:term:`USERMOD_PARAMS`, by replacing::

   GROUPMEMS_PARAM:${PN} = "--add user --group group1; \
                            --add user --group group2"

With::

   USERMOD_PARAM:${PN} = "--append --groups group1 user; \
                          --append --groups group2 user"

Or written more simply as::

   USERMOD_PARAM:${PN} = "--append --groups group1,group2 user"

See (:oecore_rev:`b8da733ab12c64503a353d5ceb2eb63fed95d851`) for more details.

Removal of ``oe.utils.all_distro_features()`` and ``oe.utils.any_distro_features()``
------------------------------------------------------------------------------------

The ``oe.utils.all_distro_features()`` and ``oe.utils.any_distro_features()``
functions have been removed from :term:`OpenEmbedded-Core (OE-Core)`.

Those can be replaced by ``bb.utils.contains()`` and ``bb.utils.contains_any()``
calls::

   oe.utils.all_distro_features("x y", ...) -> bb.utils.contains("DISTRO_FEATURES", "x y", ...)

And::

   oe.utils.any_distro_features("x y", ...) -> bb.utils.contains_any("DISTRO_FEATURES", "x y", ...)

Removed recipes
---------------

-  ``libdazzle``, ``libhandy``: no longer a dependency of the ``epiphany`` recipe, moved to
   `meta-gnome` (in `meta-openembedded`)
   (:oecore_rev:`32d91b67b71de89e0e7cc7525371aec123655908`)

-  ``libpcre``: obsolete project now replaced by ``libpcre2``
   (:oecore_rev:`057cccd9576e1dd0f947fbfc390bc06b210f71cb`)

Removed :term:`PACKAGECONFIG` options
-------------------------------------

Removed classes
---------------

Miscellaneous changes
---------------------
