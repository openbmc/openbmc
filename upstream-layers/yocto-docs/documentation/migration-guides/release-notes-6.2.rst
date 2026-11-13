.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. |yocto-codename| replace:: garburn
.. |yocto-ver| replace:: 6.2
.. Note: anchors id below cannot contain substitutions so replace them with the
   value of |yocto-ver| above.

Release notes for |yocto-ver| (|yocto-codename|)
================================================

This document lists new features and enhancements for the Yocto Project
|yocto-ver| Release (codename "|yocto-codename|"). For a list of breaking
changes and migration guides, see the :doc:`/migration-guides/migration-6.2`
section.

New Features / Enhancements in |yocto-ver|
------------------------------------------

-  Linux kernel XXX, gcc XXX, glibc XXX, LLVM XXX, and over XXX other
   recipe upgrades

..
   Found in meta/lib/oe/sanity.py:check_sanity_everybuild

-  Minimum Python version required on the host: XXX.

-  Kernel-related changes:

-  New core recipes:

-  New core classes:

-  New variables:

-  :term:`OpenEmbedded-Core (OE-Core)` library changes:

-  Global configuration changes:

-  BitBake changes:

-  SPDX-related changes:

-  :ref:`ref-classes-sbom-cve-check`-related changes:

-  Packaging changes:

-  QEMU / ``runqemu`` changes:

-  Documentation changes:

-  Go changes:

-  Testing-related changes:

-  Utility script changes:

-  Clang/LLVM-related changes:

-  Patchtest-related changes:

-  :ref:`ref-classes-insane` / :ref:`ref-classes-sanity` classes-related changes:

-  Security changes:

-  :ref:`ref-classes-sbom-cve-check`-related changes:

-  New :term:`PACKAGECONFIG` options for individual recipes:

-  systemd-related changes:

-  U-Boot-related changes:

-  Busybox-related changes:

-  ``initramfs-framework``-related changes:

-  Miscellaneous changes:

Known Issues in |yocto-ver|
---------------------------

N/A

Recipe License changes in |yocto-ver|
-------------------------------------

..
   Going through commits on OE-Core filtered by License-Update:
   git log -U0 --patch --grep "License-Update:" yocto-6.1..origin/master

The following changes have been made to the :term:`LICENSE` values set by recipes:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe(s)
     - Previous value
     - New value
   * - recipe
     - previous
     - new

Security Fixes in |yocto-ver|
-----------------------------

..
   Generated with documentation/tools/gen-cve-release-notes
   With data from https://git.yoctoproject.org/yocto-metrics/plain/cve-check

The following CVEs have been fixed:

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Recipe
     - CVE IDs
   * - recipe
     - cve

Recipe Upgrades in |yocto-ver|
------------------------------

..
   Generated with https://layers.openembedded.org/layerindex/branch_comparison
   With "rST" output selected

The following recipes have been upgraded:

.. list-table::
   :widths: 20 40 40
   :header-rows: 1

   * - Recipe
     - Previous version(s)
     - New version(s)
   * - recipe
     - previous
     - new

Contributors to |yocto-ver|
---------------------------

..
   List obtained with the following shell snippet:

      authors=""
      for repo in openembedded-core yocto-docs bitbake meta-yocto; do
         authors="${authors}\n$(git --no-pager -C $repo log --format="-  %an" yocto-6.1..origin/master)"
      done
      echo $authors | sort | uniq

   Email addresses and duplicates removed.

Thanks to the following people who contributed to this release:

Repositories / Downloads for Yocto-|yocto-ver|
----------------------------------------------
