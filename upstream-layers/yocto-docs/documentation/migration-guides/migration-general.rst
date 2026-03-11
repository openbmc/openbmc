.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Introduction
============

This guide provides a list of the backwards-incompatible changes you
might need to adapt to in your existing Yocto Project configuration
when upgrading to a new release.

If you are upgrading over multiple releases, you will need to follow
the sections from the version following the one you were previously
using up to the new version you are upgrading to.


General Migration Considerations
--------------------------------

Some considerations are not tied to a specific Yocto Project release.
This section presents information you should consider when migrating to
any new Yocto Project release.

-  *Dealing with Customized Recipes*:

   Issues could arise if you take
   older recipes that contain customizations and simply copy them
   forward expecting them to work after you migrate to new Yocto Project
   metadata. For example, suppose you have a recipe in your layer that
   is a customized version of a core recipe copied from the earlier
   release, rather than through the use of an append file. When you
   migrate to a newer version of Yocto Project, the metadata (e.g.
   perhaps an include file used by the recipe) could have changed in a
   way that would break the build. Say, for example, a function is
   removed from an include file and the customized recipe tries to call
   that function.

   You could "forward-port" all your customizations in your recipe so
   that everything works for the new release. However, this is not the
   optimal solution as you would have to repeat this process with each
   new release if changes occur that give rise to problems.

   The better solution (where practical) is to use append files
   (``*.bbappend``) to capture any customizations you want to make to a
   recipe. Doing so isolates your changes from the main recipe, making
   them much more manageable. However, sometimes it is not practical to
   use an append file. A good example of this is when introducing a
   newer or older version of a recipe in another layer.


-  *Updating Append Files*:

   Since append (``.bbappend``) files generally only contain
   your customizations, they often do not need to be adjusted for new
   releases. However, if the append file is specific to a
   particular version of the recipe (i.e. its name does not use the %
   wildcard) and the version of the recipe to which it is appending has
   changed, then you will at a minimum need to rename the append file to
   match the name of the recipe file. A mismatch between an append file
   and its corresponding recipe file (``.bb``) will trigger an error
   during parsing.

   Depending on the type of customization the append file applies, other
   incompatibilities might occur when you upgrade. For example, if your
   append file applies a patch and the recipe to which it is appending
   is updated to a newer version, the patch might no longer apply. If
   this is the case and assuming the patch is still needed, you must
   modify the patch file so that it does apply.

 .. tip::

   You can list all append files used in your configuration by running:

     bitbake-layers show-appends


.. _migration-general-buildhistory:

- *Checking Image / SDK Changes*:

   The :ref:`ref-classes-buildhistory` class can be used
   if you wish to check the impact of changes to images / SDKs across
   the migration (e.g. added/removed packages, added/removed files, size
   changes etc.). To do this, follow these steps:

   #. Enable :ref:`ref-classes-buildhistory` before the migration

   #. Run a pre-migration build

   #. Capture the :ref:`ref-classes-buildhistory` output (as
      specified by :term:`BUILDHISTORY_DIR`) and ensure it is preserved for
      subsequent builds. How you would do this depends on how you are running
      your builds - if you are doing this all on one workstation in the same
      :term:`Build Directory` you may not need to do anything other than not
      deleting the :ref:`ref-classes-buildhistory` output
      directory. For builds in a pipeline it may be more complicated.

   #. Set a tag in the :ref:`ref-classes-buildhistory` output (which is a git repository) before
      migration, to make the commit from the pre-migration build easy to find
      as you may end up running multiple builds during the migration.

   #. Perform the migration

   #. Run a build

   #. Check the output changes between the previously set tag and HEAD in the
      :ref:`ref-classes-buildhistory` output using ``git diff`` or ``buildhistory-diff``.

   For more information on using :ref:`ref-classes-buildhistory`, see
   :ref:`dev-manual/build-quality:maintaining build output quality`.
