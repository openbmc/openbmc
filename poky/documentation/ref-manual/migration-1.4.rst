Moving to the Yocto Project 1.4 Release
=======================================

This section provides migration information for moving to the Yocto
Project 1.4 Release from the prior release.

.. _migration-1.4-bitbake:

BitBake
-------

Differences include the following:

-  *Comment Continuation:* If a comment ends with a line continuation
   (\\) character, then the next line must also be a comment. Any
   instance where this is not the case, now triggers a warning. You must
   either remove the continuation character, or be sure the next line is
   a comment.

-  *Package Name Overrides:* The runtime package specific variables
   :term:`RDEPENDS`,
   :term:`RRECOMMENDS`,
   :term:`RSUGGESTS`,
   :term:`RPROVIDES`,
   :term:`RCONFLICTS`,
   :term:`RREPLACES`, :term:`FILES`,
   :term:`ALLOW_EMPTY`, and the pre, post, install,
   and uninstall script functions ``pkg_preinst``, ``pkg_postinst``,
   ``pkg_prerm``, and ``pkg_postrm`` should always have a package name
   override. For example, use ``RDEPENDS_${PN}`` for the main package
   instead of ``RDEPENDS``. BitBake uses more strict checks when it
   parses recipes.

.. _migration-1.4-build-behavior:

Build Behavior
--------------

Differences include the following:

-  *Shared State Code:* The shared state code has been optimized to
   avoid running unnecessary tasks. For example, the following no longer
   populates the target sysroot since that is not necessary:
   ::

      $ bitbake -c rootfs some-image

   Instead, the system just needs to extract the
   output package contents, re-create the packages, and construct the
   root filesystem. This change is unlikely to cause any problems unless
   you have missing declared dependencies.

-  *Scanning Directory Names:* When scanning for files in
   :term:`SRC_URI`, the build system now uses
   :term:`FILESOVERRIDES` instead of
   :term:`OVERRIDES` for the directory names. In
   general, the values previously in ``OVERRIDES`` are now in
   ``FILESOVERRIDES`` as well. However, if you relied upon an additional
   value you previously added to ``OVERRIDES``, you might now need to
   add it to ``FILESOVERRIDES`` unless you are already adding it through
   the :term:`MACHINEOVERRIDES` or
   :term:`DISTROOVERRIDES` variables, as
   appropriate. For more related changes, see the
   ":ref:`ref-manual/migration-1.4:variables`" section.

.. _migration-1.4-proxies-and-fetching-source:

Proxies and Fetching Source
---------------------------

A new ``oe-git-proxy`` script has been added to replace previous methods
of handling proxies and fetching source from Git. See the
``meta-yocto/conf/site.conf.sample`` file for information on how to use
this script.

.. _migration-1.4-custom-interfaces-file-netbase-change:

Custom Interfaces File (netbase change)
---------------------------------------

If you have created your own custom ``etc/network/interfaces`` file by
creating an append file for the ``netbase`` recipe, you now need to
create an append file for the ``init-ifupdown`` recipe instead, which
you can find in the :term:`Source Directory` at
``meta/recipes-core/init-ifupdown``. For information on how to use
append files, see the
":ref:`dev-manual/common-tasks:using .bbappend files in your layer`"
section in the Yocto Project Development Tasks Manual.

.. _migration-1.4-remote-debugging:

Remote Debugging
----------------

Support for remote debugging with the Eclipse IDE is now separated into
an image feature (``eclipse-debug``) that corresponds to the
``packagegroup-core-eclipse-debug`` package group. Previously, the
debugging feature was included through the ``tools-debug`` image
feature, which corresponds to the ``packagegroup-core-tools-debug``
package group.

.. _migration-1.4-variables:

Variables
---------

The following variables have changed:

-  ``SANITY_TESTED_DISTROS``: This variable now uses a distribution
   ID, which is composed of the host distributor ID followed by the
   release. Previously,
   :term:`SANITY_TESTED_DISTROS` was
   composed of the description field. For example, "Ubuntu 12.10"
   becomes "Ubuntu-12.10". You do not need to worry about this change if
   you are not specifically setting this variable, or if you are
   specifically setting it to "".

-  ``SRC_URI``: The ``${``\ :term:`PN`\ ``}``,
   ``${``\ :term:`PF`\ ``}``,
   ``${``\ :term:`P`\ ``}``, and ``FILE_DIRNAME`` directories
   have been dropped from the default value of the
   :term:`FILESPATH` variable, which is used as the
   search path for finding files referred to in
   :term:`SRC_URI`. If you have a recipe that relied upon
   these directories, which would be unusual, then you will need to add
   the appropriate paths within the recipe or, alternatively, rearrange
   the files. The most common locations are still covered by ``${``\ :term:`BP`\ ``}``,
   ``${``\ :term:`BPN`\ ``}``, and "files", which all remain in the default value of
   :term:`FILESPATH`.

.. _migration-target-package-management-with-rpm:

Target Package Management with RPM
----------------------------------

If runtime package management is enabled and the RPM backend is
selected, Smart is now installed for package download, dependency
resolution, and upgrades instead of Zypper. For more information on how
to use Smart, run the following command on the target:
::

   smart --help

.. _migration-1.4-recipes-moved:

Recipes Moved
-------------

The following recipes were moved from their previous locations because
they are no longer used by anything in the OpenEmbedded-Core:

-  ``clutter-box2d``: Now resides in the ``meta-oe`` layer.

-  ``evolution-data-server``: Now resides in the ``meta-gnome`` layer.

-  ``gthumb``: Now resides in the ``meta-gnome`` layer.

-  ``gtkhtml2``: Now resides in the ``meta-oe`` layer.

-  ``gupnp``: Now resides in the ``meta-multimedia`` layer.

-  ``gypsy``: Now resides in the ``meta-oe`` layer.

-  ``libcanberra``: Now resides in the ``meta-gnome`` layer.

-  ``libgdata``: Now resides in the ``meta-gnome`` layer.

-  ``libmusicbrainz``: Now resides in the ``meta-multimedia`` layer.

-  ``metacity``: Now resides in the ``meta-gnome`` layer.

-  ``polkit``: Now resides in the ``meta-oe`` layer.

-  ``zeroconf``: Now resides in the ``meta-networking`` layer.

.. _migration-1.4-removals-and-renames:

Removals and Renames
--------------------

The following list shows what has been removed or renamed:

-  ``evieext``: Removed because it has been removed from ``xserver``
   since 2008.

-  *Gtk+ DirectFB:* Removed support because upstream Gtk+ no longer
   supports it as of version 2.18.

-  ``libxfontcache / xfontcacheproto``: Removed because they were
   removed from the Xorg server in 2008.

-  ``libxp / libxprintapputil / libxprintutil / printproto``: Removed
   because the XPrint server was removed from Xorg in 2008.

-  ``libxtrap / xtrapproto``: Removed because their functionality was
   broken upstream.

-  *linux-yocto 3.0 kernel:* Removed with linux-yocto 3.8 kernel being
   added. The linux-yocto 3.2 and linux-yocto 3.4 kernels remain as part
   of the release.

-  ``lsbsetup``: Removed with functionality now provided by
   ``lsbtest``.

-  ``matchbox-stroke``: Removed because it was never more than a
   proof-of-concept.

-  ``matchbox-wm-2 / matchbox-theme-sato-2``: Removed because they are
   not maintained. However, ``matchbox-wm`` and ``matchbox-theme-sato``
   are still provided.

-  ``mesa-dri``: Renamed to ``mesa``.

-  ``mesa-xlib``: Removed because it was no longer useful.

-  ``mutter``: Removed because nothing ever uses it and the recipe is
   very old.

-  ``orinoco-conf``: Removed because it has become obsolete.

-  ``update-modules``: Removed because it is no longer used. The
   kernel module ``postinstall`` and ``postrm`` scripts can now do the
   same task without the use of this script.

-  ``web``: Removed because it is not maintained. Superseded by
   ``web-webkit``.

-  ``xf86bigfontproto``: Removed because upstream it has been disabled
   by default since 2007. Nothing uses ``xf86bigfontproto``.

-  ``xf86rushproto``: Removed because its dependency in ``xserver``
   was spurious and it was removed in 2005.

-  ``zypper / libzypp / sat-solver``: Removed and been functionally
   replaced with Smart (``python-smartpm``) when RPM packaging is used
   and package management is enabled on the target.

