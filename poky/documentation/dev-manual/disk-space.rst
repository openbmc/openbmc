.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Conserving Disk Space
*********************

Conserving Disk Space During Builds
===================================

To help conserve disk space during builds, you can add the following
statement to your project's ``local.conf`` configuration file found in
the :term:`Build Directory`::

   INHERIT += "rm_work"

Adding this statement deletes the work directory used for
building a recipe once the recipe is built. For more information on
"rm_work", see the :ref:`ref-classes-rm-work` class in the
Yocto Project Reference Manual.

When you inherit this class and build a ``core-image-sato`` image for a
``qemux86-64`` machine from an Ubuntu 22.04 x86-64 system, you end up with a
final disk usage of 22 Gbytes instead of &MIN_DISK_SPACE; Gbytes. However,
&MIN_DISK_SPACE_RM_WORK; Gbytes of initial free disk space are still needed to
create temporary files before they can be deleted.

Purging Duplicate Shared State Cache Files
==========================================

After multiple build iterations, the Shared State (sstate) cache can contain
duplicate cache files for a given package, while only the most recent one
is likely to be reusable. The following command purges all but the
newest sstate cache file for each package::

   sstate-cache-management.sh --remove-duplicated --cache-dir=build/sstate-cache

This command will ask you to confirm the deletions it identifies.

.. note::

   The duplicated sstate cache files of one package must have the same
   architecture, which means that sstate cache files with multiple
   architectures are not considered as duplicate.

Run ``sstate-cache-management.sh`` for more details about this script.

