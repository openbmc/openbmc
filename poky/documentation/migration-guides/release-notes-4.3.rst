.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for 4.3 (nandbield)
----------------------------------

New Features / Enhancements in 4.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Linux kernel 6.x, glibc 2.xx and ~xxx other recipe upgrades

-  New variables:

   -  :term:`FIT_ADDRESS_CELLS` and :term:`UBOOT_FIT_ADDRESS_CELLS`.
      See details below.

   -  :term:`KERNEL_DTBDEST`: directory where to install DTB files.

   -  :term:`KERNEL_DTBVENDORED`: whether to keep vendor subdirectories.

-  Architecture-specific enhancements:

-  Kernel-related enhancements:

-  New core recipes:

-  New classes:

   - A ``ptest-cargo`` class was added to allow Cargo based recipes to easily add ptests

-  QEMU/runqemu enhancements:

   -  QEMU has been upgraded to version 8.0

-  Rust improvements:

   -  Rust has been upgraded to version 1.69

-  Image-related enhancements:

-  wic Image Creator enhancements:

-  FIT image related improvements:

   -  New :term:`FIT_ADDRESS_CELLS` and :term:`UBOOT_FIT_ADDRESS_CELLS` variables allowing
      to specify 64 bit addresses, typically for loading U-Boot.

-  SDK-related improvements:

-  Testing:

-  Utility script changes:

-  BitBake improvements:

   -  The BitBake Cooker log now contains notes when the caches are
      invalidated which is useful for memory resident bitbake debugging.

-  Packaging changes:

-  Prominent documentation updates:

-  Miscellaneous changes:

   -  Git based recipes in OE-Core which used the git protocol have been
      changed to use https where possibile. https is now believed to be
      faster and more reliable.

   -  The ``os-release`` recipe added a ``CPE_NAME`` to the fields provided, with the
      default being populated from :term:`DISTRO`.

Known Issues in 4.3
~~~~~~~~~~~~~~~~~~~

Recipe License changes in 4.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following corrections have been made to the :term:`LICENSE` values set by recipes:

Security Fixes in 4.3
~~~~~~~~~~~~~~~~~~~~~

Recipe Upgrades in 4.3
~~~~~~~~~~~~~~~~~~~~~~

Contributors to 4.3
~~~~~~~~~~~~~~~~~~~
