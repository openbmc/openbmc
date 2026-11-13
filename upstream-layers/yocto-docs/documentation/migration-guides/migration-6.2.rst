.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: garburn
.. |yocto-ver| replace:: 6.2
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Migration notes for |yocto-ver| (|yocto-codename|)
**************************************************

This section provides migration information for moving to the Yocto
Project |yocto-ver| Release (codename "|yocto-codename|") from the prior release.
For a list of new features and enhancements, see the
:doc:`/migration-guides/release-notes-6.2` section.

Supported kernel versions
-------------------------

The :term:`OLDEST_KERNEL` setting is 5.15 in this release, meaning that
out the box, older kernels are not supported. See :ref:`4.3 migration notes
<migration-4.3-supported-kernel-versions>` for details.

Supported distributions
-----------------------

Compared to the previous releases, running BitBake is supported on new
GNU/Linux distributions:

-  XXX

See :ref:`all supported distributions <system-requirements-supported-distros>`.

Removed recipes
---------------

Removed variables
-----------------

Removed :term:`PACKAGECONFIG` options
-------------------------------------

Removed classes
---------------

Miscellaneous changes
---------------------
