.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: wrynose
.. |yocto-ver| replace:: 6.0
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Release notes for |yocto-ver| (|yocto-codename|)
================================================

This document lists new features and enhancements for the Yocto Project
|yocto-ver| Release (codename "|yocto-codename|"). For a list of breaking
changes and migration guides, see the :doc:`/migration-guides/migration-6.0`
section.

New Features / Enhancements in |yocto-ver|
------------------------------------------

-  Linux kernel XXX, gcc XXX, glibc XXX, LLVM XXX, and over XXX other
   recipe upgrades.

-  Minimum Python version required on the host: XXX.

-  New variables:

   - The :term:`OPENSSH_HOST_KEY_DIR_READONLY_CONFIG` variable can be used to
     specify the directory where OpenSSH host keys are stored when the device
     uses a read-only filesystem. The default value is ``/var/run/ssh``.

   - The :term:`OPENSSH_HOST_KEY_DIR` variable can be used to specify the
     directory where OpenSSH host keys are stored. The default value is
     ``/etc/ssh``.

   - :term:`SPDX_INCLUDE_KERNEL_CONFIG`: export the Linux kernel
     configuration (``CONFIG_*`` parameters) into the SPDX document.

   - :term:`SPDX_INCLUDE_PACKAGECONFIG`: export a recipe's
     :term:`PACKAGECONFIG` features (enabled/disabled) into the SPDX document.

-  Kernel-related changes:

-  New core recipes:

-  New core classes:

-  Architecture-specific changes:

-  QEMU / ``runqemu`` changes:

-  Documentation changes:

-  Go changes:

-  Rust changes:

-  Wic Image Creator changes:

-  SDK-related changes:

-  Testing-related changes:

-  Utility script changes:

-  BitBake changes:

-  Packaging changes:

-  LLVM related changes:

-  SPDX-related changes:

-  ``devtool`` changes:

-  Patchtest-related changes:

-  :ref:`ref-classes-insane` class related changes:

-  Security changes:

-  :ref:`ref-classes-cve-check` changes:

-  New :term:`PACKAGECONFIG` options for individual recipes:

-  Systemd related changes:

-  :ref:`ref-classes-sanity` class changes:

-  U-boot related changes:

   -  :ref:`ref-classes-uboot-config`: Add support for generating the U-Boot
      initial environment in binary format using
      :term:`UBOOT_INITIAL_ENV_BINARY`.

-  Miscellaneous changes:

Known Issues in |yocto-ver|
---------------------------

Recipe License changes in |yocto-ver|
-------------------------------------

The following changes have been made to the :term:`LICENSE` values set by recipes:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous value
     - New value
   * - ``recipe name``
     - Previous value
     - New value

Security Fixes in |yocto-ver|
-----------------------------

The following CVEs have been fixed:

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Recipe
     - CVE IDs
   * - ``recipe name``
     - :cve_nist:`xxx-xxxx`, ...

Recipe Upgrades in |yocto-ver|
------------------------------

The following recipes have been upgraded:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous version
     - New version
   * - ``recipe name``
     - Previous version
     - New version

Contributors to |yocto-ver|
---------------------------

Thanks to the following people who contributed to this release:

Repositories / Downloads for Yocto-|yocto-ver|
----------------------------------------------
