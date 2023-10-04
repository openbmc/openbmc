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

Purging Obsolete Shared State Cache Files
=========================================

After multiple build iterations, the Shared State (sstate) cache can contain
multiple cache files for a given package, consuming a substantial amount of
disk space. However, only the most recent ones are likely to be reused.

The following command is a quick way to purge all the cache files which
haven't been used for a least a specified number of days::

   find build/sstate-cache -type f -mtime +$DAYS -delete

The above command relies on the fact that BitBake touches the sstate cache
files as it accesses them, when it has write access to the cache.

You could use ``-atime`` instead of ``-mtime`` if the partition isn't mounted
with the ``noatime`` option for a read only cache.

For more advanced needs, OpenEmbedded-Core also offers a more elaborate
command. It has the ability to purge all but the newest cache files on each
architecture, and also to remove files that it considers unreachable by
exploring a set of build configurations. However, this command
requires a full build environment to be available and doesn't work well
covering multiple releases. It won't work either on limited environments
such as BSD based NAS::

   sstate-cache-management.sh --remove-duplicated --cache-dir=build/sstate-cache

This command will ask you to confirm the deletions it identifies.
Run ``sstate-cache-management.sh`` for more details about this script.

.. note::

   As this command is much more cautious and selective, removing only cache files,
   it will execute much slower than the simple ``find`` command described above.
   Therefore, it may not be your best option to trim huge cache directories.
