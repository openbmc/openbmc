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


