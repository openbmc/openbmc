.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Moving to the Yocto Project 1.3 Release
=======================================

This section provides migration information for moving to the Yocto
Project 1.3 Release from the prior release.

.. _1.3-local-configuration:

Local Configuration
-------------------

Differences include changes for
:term:`SSTATE_MIRRORS` and ``bblayers.conf``.

.. _migration-1.3-sstate-mirrors:

SSTATE_MIRRORS
~~~~~~~~~~~~~~

The shared state cache (sstate-cache), as pointed to by
:term:`SSTATE_DIR`, by default now has two-character
subdirectories to prevent issues arising from too many files in the same
directory. Also, native sstate-cache packages, which are built to run on
the host system, will go into a subdirectory named using the distro ID
string. If you copy the newly structured sstate-cache to a mirror
location (either local or remote) and then point to it in
:term:`SSTATE_MIRRORS`, you need to append "PATH"
to the end of the mirror URL so that the path used by BitBake before the
mirror substitution is appended to the path used to access the mirror.
Here is an example: ::

   SSTATE_MIRRORS = "file://.* http://someserver.tld/share/sstate/PATH"

.. _migration-1.3-bblayers-conf:

bblayers.conf
~~~~~~~~~~~~~

The ``meta-yocto`` layer consists of two parts that correspond to the
Poky reference distribution and the reference hardware Board Support
Packages (BSPs), respectively: ``meta-yocto`` and ``meta-yocto-bsp``.
When running BitBake for the first time after upgrading, your
``conf/bblayers.conf`` file will be updated to handle this change and
you will be asked to re-run or restart for the changes to take effect.

.. _1.3-recipes:

Recipes
-------

Differences include changes for the following:

.. _migration-1.3-python-function-whitespace:

Python Function Whitespace
~~~~~~~~~~~~~~~~~~~~~~~~~~

All Python functions must now use four spaces for indentation.
Previously, an inconsistent mix of spaces and tabs existed, which made
extending these functions using ``_append`` or ``_prepend`` complicated
given that Python treats whitespace as syntactically significant. If you
are defining or extending any Python functions (e.g.
``populate_packages``, ``do_unpack``, ``do_patch`` and so forth) in
custom recipes or classes, you need to ensure you are using consistent
four-space indentation.

.. _migration-1.3-proto=-in-src-uri:

proto= in SRC_URI
~~~~~~~~~~~~~~~~~

Any use of ``proto=`` in :term:`SRC_URI` needs to be
changed to ``protocol=``. In particular, this applies to the following
URIs:

-  ``svn://``

-  ``bzr://``

-  ``hg://``

-  ``osc://``

Other URIs were already using ``protocol=``. This change improves
consistency.

.. _migration-1.3-nativesdk:

nativesdk
~~~~~~~~~

The suffix ``nativesdk`` is now implemented as a prefix, which
simplifies a lot of the packaging code for ``nativesdk`` recipes. All
custom ``nativesdk`` recipes, which are relocatable packages that are
native to :term:`SDK_ARCH`, and any references need to
be updated to use ``nativesdk-*`` instead of ``*-nativesdk``.

.. _migration-1.3-task-recipes:

Task Recipes
~~~~~~~~~~~~

"Task" recipes are now known as "Package groups" and have been renamed
from ``task-*.bb`` to ``packagegroup-*.bb``. Existing references to the
previous ``task-*`` names should work in most cases as there is an
automatic upgrade path for most packages. However, you should update
references in your own recipes and configurations as they could be
removed in future releases. You should also rename any custom ``task-*``
recipes to ``packagegroup-*``, and change them to inherit
``packagegroup`` instead of ``task``, as well as taking the opportunity
to remove anything now handled by ``packagegroup.bbclass``, such as
providing ``-dev`` and ``-dbg`` packages, setting
:term:`LIC_FILES_CHKSUM`, and so forth. See the
":ref:`packagegroup.bbclass <ref-classes-packagegroup>`" section for
further details.

.. _migration-1.3-image-features:

IMAGE_FEATURES
~~~~~~~~~~~~~~

Image recipes that previously included ``apps-console-core`` in
:term:`IMAGE_FEATURES` should now include ``splash``
instead to enable the boot-up splash screen. Retaining
``apps-console-core`` will still include the splash screen but generates a
warning. The ``apps-x11-core`` and ``apps-x11-games`` ``IMAGE_FEATURES``
features have been removed.

.. _migration-1.3-removed-recipes:

Removed Recipes
~~~~~~~~~~~~~~~

The following recipes have been removed. For most of them, it is
unlikely that you would have any references to them in your own
:term:`Metadata`. However, you should check your metadata
against this list to be sure:

-  ``libx11-trim``: Replaced by ``libx11``, which has a negligible
   size difference with modern Xorg.

-  ``xserver-xorg-lite``: Use ``xserver-xorg``, which has a negligible
   size difference when DRI and GLX modules are not installed.

-  ``xserver-kdrive``: Effectively unmaintained for many years.

-  ``mesa-xlib``: No longer serves any purpose.

-  ``galago``: Replaced by telepathy.

-  ``gail``: Functionality was integrated into GTK+ 2.13.

-  ``eggdbus``: No longer needed.

-  ``gcc-*-intermediate``: The build has been restructured to avoid
   the need for this step.

-  ``libgsmd``: Unmaintained for many years. Functionality now
   provided by ``ofono`` instead.

-  *contacts, dates, tasks, eds-tools*: Largely unmaintained PIM
   application suite. It has been moved to ``meta-gnome`` in
   ``meta-openembedded``.

In addition to the previously listed changes, the ``meta-demoapps``
directory has also been removed because the recipes in it were not being
maintained and many had become obsolete or broken. Additionally, these
recipes were not parsed in the default configuration. Many of these
recipes are already provided in an updated and maintained form within
the OpenEmbedded community layers such as ``meta-oe`` and
``meta-gnome``. For the remainder, you can now find them in the
``meta-extras`` repository, which is in the
:yocto_git:`Source Repositories <>` at
:yocto_git:`/cgit/cgit.cgi/meta-extras/`.

.. _1.3-linux-kernel-naming:

Linux Kernel Naming
-------------------

The naming scheme for kernel output binaries has been changed to now
include :term:`PE` as part of the filename:
::

   KERNEL_IMAGE_BASE_NAME ?= "${KERNEL_IMAGETYPE}-${PE}-${PV}-${PR}-${MACHINE}-${DATETIME}"

Because the ``PE`` variable is not set by default, these binary files
could result with names that include two dash characters. Here is an
example: ::

   bzImage--3.10.9+git0+cd502a8814_7144bcc4b8-r0-qemux86-64-20130830085431.bin


