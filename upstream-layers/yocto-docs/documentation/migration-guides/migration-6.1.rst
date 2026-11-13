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

The :term:`OLDEST_KERNEL` setting is 5.15 in this release, meaning that
out the box, older kernels are not supported. See :ref:`4.3 migration notes
<migration-4.3-supported-kernel-versions>` for details.

Supported distributions
-----------------------

Compared to the previous releases, running BitBake is supported on new
GNU/Linux distributions:

-  AlmaLinux 10
-  Fedora 44
-  Rocky Linux 10

See :ref:`all supported distributions <system-requirements-supported-distros>`.

.. _ref-migration-6-1-groupmems:

useradd: replace ``GROUPMEMS_PARAM`` assignments to :term:`USERMOD_PARAM`
-------------------------------------------------------------------------

The ``groupmems`` command is removed from the ``shadow`` recipe starting from
version 4.20. The same functionality provided by ``groupmems`` can be achieved
with the ``usermod`` command.

Assignments made to the ``GROUPMEMS_PARAM`` variable can be converted to use
:term:`USERMOD_PARAM`, by replacing::

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

:term:`LICENSE` is now a :term:`SPDX License Expression`
--------------------------------------------------------

The :term:`LICENSE` variable is now a :term:`SPDX License Expression` instead
of the custom license expressions that have been used historically.

The changes required for the new expressions are as follows:

1. The ``&`` operator is replaced with ``AND``

2. The ``|`` operator is replaced with ``OR``

3. Any license value which is not a valid :term:`SPDX License Expression` is an
   error.

4. Custom (non `SPDX License Identifier`_) licenses are still allowed, as long
   as they are prefixed with ``LicenseRef-``. The behavior of looking for the
   license text in :term:`LICENSE_PATH` (with or without the ``LicenseRef-``
   prefix) or :term:`NO_GENERIC_LICENSE` is unchanged, and may still be used.

5. ``CLOSED`` as a license is deprecated and will issue a warning. This license
   is effectively "no license" (usually meaning e.g. "All rights reserved"),
   but a more precise definition is to provide some sort of actual license text
   using a custom license. This provides a more consistent definition of the
   license text, since the meaning of "no license" may vary by jurisdiction.
   Keep in mind that you can still have a common license file in
   :term:`LICENSE_PATH` and refer to it with a ``LicenseRef-`` license. Note
   that when you do this, you will also need to provide a
   :term:`LIC_FILES_CHKSUM` value to point to your license file.

6. The generic ``PD`` (Public Domain) license is no longer allowed (since it is
   not a valid `SPDX License Identifier`_). Additionally, the meaning of
   "Public Domain" varies by jurisdiction, so leaving the exact license text
   unspecified is not recommended. Instead, either find a matching SPDX license
   (The `SPDX License Check`_ website can be useful here), or use a
   ``LicenseRef-`` with :term:`NO_GENERIC_LICENSE` to specify the actual
   license text.

7. The ``WITH`` operator should now be used to describe an exception to a
   license, instead of a bespoke license identifier. For example, the old
   bespoke license ``Apache-2.0-with-LLVM-exception`` would become
   ``Apache-2.0 WITH LLVM-exception``. For a list of the valid license
   exceptions, see `SPDX License Exception`_.

8. Because of the change to use ``WITH`` instead of bespoke licenses, there is
   a change in how :term:`INCOMPATIBLE_LICENSE` works. Anything listed in this
   variable will match a single license (unchanged), but it will also match the
   left-hand (license) side of a ``WITH`` expression. To allow a license with a
   specific `SPDX License Exception`_, the SPDX license exception must be
   listed in :term:`INCOMPATIBLE_LICENSE_EXCEPTIONS`.

   Practically speaking, the place where this comes up the most is when
   attempting to exclude GPLv3 code using :term:`INCOMPATIBLE_LICENSE`.
   Previously, this would have allowed any ``GPLv3-with-exception`` license,
   since they were bespoke licenses that did not match the
   ``GPL-3.0* LGPL-3.0*`` expansion. Now, however, the licenses will match
   because they are e.g. ``GPL-3.0-or-later WITH exception``. As such, any
   exceptions to the GPLv3 that should be allowed must be listed in
   :term:`INCOMPATIBLE_LICENSE_EXCEPTIONS`.

Currently, the older syntax for license expressions is still parsed and
automatically converted to a :term:`SPDX License Expression`, but a warning is
issued when this occurs. This support will eventually be removed and the only
valid values for these variables will be :term:`SPDX License Expression`.

.. tip::

   The :oecore_path:`scripts/contrib/convert-spdx-licenses.py` script can help
   converting :term:`LICENSE` expressions in :term:`layers <Layer>`.

Class file functions moved to the ``oe`` library
------------------------------------------------

Many class file functions have been moved to :oecore_path:`oe <meta/lib/oe>`
library in :term:`OpenEmbedded-Core (OE-Core)`:

-  :ref:`ref-classes-kernel`:

   -  The ``get_kernel*()`` functions are now ``oe.kernel.get*()``
   -  The default kernel build ``make`` commands now set ``ARCH``
   -  The ``valid_arches`` variable is no longer available to change ``ARCH``

-  :ref:`ref-classes-kernel-arch` no longer exports or sets ``ARCH`` and
   ``UBOOT_ARCH``, ``oe.kernel.map_kernel_arch(d)`` and
   ``oe.kernel.map_uboot_arch(d)`` should be used instead

-  :ref:`ref-classes-kernel`, :ref:`ref-classes-uboot-config`: ``ARCH`` and
   ``UBOOT_ARCH`` are not exported into the environment in most cases

-  Various :ref:`ref-classes-utils` class Python functions are now
   ``oe.utils.XXX()`` (see :oecore_path:`meta/lib/oe/utils.py`)

-  Most :ref:`ref-classes-sanity` class functions are now ``oe.sanity.XXX()``
   (see :oecore_path:`meta/lib/oe/sanity.py`)

-  ``lsb_distro_identifier`` is now always ``oe.lsb.distro_identifier()``

Removed recipes
---------------

-  ``libdazzle``, ``libhandy``: No longer a dependency of the ``epiphany`` recipe, moved to
   `meta-gnome` (in `meta-openembedded`)
   (:oecore_rev:`32d91b67b71de89e0e7cc7525371aec123655908`)

-  ``libpcre``: Obsolete project now replaced by ``libpcre2``
   (:oecore_rev:`057cccd9576e1dd0f947fbfc390bc06b210f71cb`)

-  ``libxfont``: Recipe is obsolete and successor libxfont2 is available,
   nothing depends on it in :term:`OpenEmbedded-Core (OE-Core)` anymore
   (:oecore_rev:`4666968b23eefe8290b75e60fc3f3a7b8328c193`)

-  ``gptfdisk``: Remove recipe as there are no dependencies on this recipe in
   :term:`OpenEmbedded-Core (OE-Core)` anymore
   (:oecore_rev:`188d84bcc136e87d03736606fb8e49f35df47fae`)

-  ``libclc``: Remove recipe as it was replaced by ``mesa-libclc``
   (:oecore_rev:`5457c35142cc9b6d3fa537cc5ef56e90cce85c88`)

-  ``makedepend``: Remove recipe as ``mesa`` no longer depends on ``makedepend``
   (:oecore_rev:`7e696b1fa0862da801fbe6e41f8820e65d82cd7a`)

-  ``python-six``: Moved to :oe_git:`meta-python
   </meta-openembedded/tree/meta-python>`
   (:oecore_rev:`372ee47516cf6abb2b9a2767df80094491690f97`)

Removed variables
-----------------

-  ``SDK_TOOLCHAIN_LANGS``: Merged into :term:`SDK_FEATURES`
   (:oecore_rev:`933981a21e58dffce6dd2b400f2690444320d7d7`)

Removed :term:`PACKAGECONFIG` options
-------------------------------------

Removed classes
---------------

-  ``linux-kernel-base.bbclass``: Was removed as its functionality was moved to
   :ref:`ref-classes-kernel-arch`. Users of this class should now inherit
   :ref:`ref-classes-kernel-arch` instead.

Miscellaneous changes
---------------------

-  :ref:`ref-classes-archiver`: ``srpm`` support for :term:`ARCHIVER_MODE` was
   removed (:oecore_rev:`69ce7593e8ed2d82125ed44e3989731d9e9f6858`)

-  ``systemd-systemctl-native``: Rename to ``systemd-tools-native``
   (:oecore_rev:`ff985de4df46945a92ae4ca59e54c930b7640c9a`)

.. _SPDX License Identifier: https://spdx.org/licenses/
.. _SPDX License Exception: https://spdx.org/licenses/exceptions-index.html
.. _SPDX License Check: https://tools.spdx.org/app/check_license/
