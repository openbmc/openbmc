Moving to the Yocto Project 2.1 Release
=======================================

This section provides migration information for moving to the Yocto
Project 2.1 Release from the prior release.

.. _migration-2.1-variable-expansion-in-python-functions:

Variable Expansion in Python Functions
--------------------------------------

Variable expressions, such as ``${``\ VARNAME\ ``}`` no longer expand
automatically within Python functions. Suppressing expansion was done to
allow Python functions to construct shell scripts or other code for
situations in which you do not want such expressions expanded. For any
existing code that relies on these expansions, you need to change the
expansions to expand the value of individual variables through
``d.getVar()``. To alternatively expand more complex expressions, use
``d.expand()``.

.. _migration-2.1-overrides-must-now-be-lower-case:

Overrides Must Now be Lower-Case
--------------------------------

The convention for overrides has always been for them to be lower-case
characters. This practice is now a requirement as BitBake's datastore
now assumes lower-case characters in order to give a slight performance
boost during parsing. In practical terms, this requirement means that
anything that ends up in :term:`OVERRIDES` must now
appear in lower-case characters (e.g. values for ``MACHINE``,
``TARGET_ARCH``, ``DISTRO``, and also recipe names if
``_pn-``\ recipename overrides are to be effective).

.. _migration-2.1-expand-parameter-to-getvar-and-getvarflag-now-mandatory:

Expand Parameter to ``getVar()`` and ``getVarFlag()`` is Now Mandatory
----------------------------------------------------------------------

The expand parameter to ``getVar()`` and ``getVarFlag()`` previously
defaulted to False if not specified. Now, however, no default exists so
one must be specified. You must change any ``getVar()`` calls that do
not specify the final expand parameter to calls that do specify the
parameter. You can run the following ``sed`` command at the base of a
layer to make this change:
::

   sed -e 's:\(\.getVar([^,()]*\)):\1, False):g' -i `grep -ril getVar *`
   sed -e 's:\(\.getVarFlag([^,()]*,[^,()]*\)):\1, False):g' -i `grep -ril getVarFlag *`

.. note::

   The reason for this change is that it prepares the way for changing
   the default to True in a future Yocto Project release. This future
   change is a much more sensible default than False. However, the
   change needs to be made gradually as a sudden change of the default
   would potentially cause side-effects that would be difficult to
   detect.

.. _migration-2.1-makefile-environment-changes:

Makefile Environment Changes
----------------------------

:term:`EXTRA_OEMAKE` now defaults to "" instead of
"-e MAKEFLAGS=". Setting ``EXTRA_OEMAKE`` to "-e MAKEFLAGS=" by default
was a historical accident that has required many classes (e.g.
``autotools``, ``module``) and recipes to override this default in order
to work with sensible build systems. When upgrading to the release, you
must edit any recipe that relies upon this old default by either setting
``EXTRA_OEMAKE`` back to "-e MAKEFLAGS=" or by explicitly setting any
required variable value overrides using ``EXTRA_OEMAKE``, which is
typically only needed when a Makefile sets a default value for a
variable that is inappropriate for cross-compilation using the "="
operator rather than the "?=" operator.

.. _migration-2.1-libexecdir-reverted-to-prefix-libexec:

``libexecdir`` Reverted to ``${prefix}/libexec``
------------------------------------------------

The use of ``${libdir}/${BPN}`` as ``libexecdir`` is different as
compared to all other mainstream distributions, which either uses
``${prefix}/libexec`` or ``${libdir}``. The use is also contrary to the
GNU Coding Standards (i.e.
https://www.gnu.org/prep/standards/html_node/Directory-Variables.html)
that suggest ``${prefix}/libexec`` and also notes that any
package-specific nesting should be done by the package itself. Finally,
having ``libexecdir`` change between recipes makes it very difficult for
different recipes to invoke binaries that have been installed into
``libexecdir``. The Filesystem Hierarchy Standard (i.e.
http://refspecs.linuxfoundation.org/FHS_3.0/fhs/ch04s07.html) now
recognizes the use of ``${prefix}/libexec/``, giving distributions the
choice between ``${prefix}/lib`` or ``${prefix}/libexec`` without
breaking FHS.

.. _migration-2.1-ac-cv-sizeof-off-t-no-longer-cached-in-site-files:

``ac_cv_sizeof_off_t`` is No Longer Cached in Site Files
--------------------------------------------------------

For recipes inheriting the :ref:`autotools <ref-classes-autotools>`
class, ``ac_cv_sizeof_off_t`` is no longer cached in the site files for
``autoconf``. The reason for this change is because the
``ac_cv_sizeof_off_t`` value is not necessarily static per architecture
as was previously assumed. Rather, the value changes based on whether
large file support is enabled. For most software that uses ``autoconf``,
this change should not be a problem. However, if you have a recipe that
bypasses the standard :ref:`ref-tasks-configure` task
from the ``autotools`` class and the software the recipe is building
uses a very old version of ``autoconf``, the recipe might be incapable
of determining the correct size of ``off_t`` during ``do_configure``.

The best course of action is to patch the software as necessary to allow
the default implementation from the ``autotools`` class to work such
that ``autoreconf`` succeeds and produces a working configure script,
and to remove the overridden ``do_configure`` task such that the default
implementation does get used.

.. _migration-2.1-image-generation-split-out-from-filesystem-generation:

Image Generation is Now Split Out from Filesystem Generation
------------------------------------------------------------

Previously, for image recipes the :ref:`ref-tasks-rootfs`
task assembled the filesystem and then from that filesystem generated
images. With this Yocto Project release, image generation is split into
separate ```do_image_*`` <#ref-tasks-image>`__ tasks for clarity both in
operation and in the code.

For most cases, this change does not present any problems. However, if
you have made customizations that directly modify the ``do_rootfs`` task
or that mention ``do_rootfs``, you might need to update those changes.
In particular, if you had added any tasks after ``do_rootfs``, you
should make edits so that those tasks are after the
```do_image_complete`` <#ref-tasks-image-complete>`__ task rather than
after ``do_rootfs`` so that the your added tasks run at the correct
time.

A minor part of this restructuring is that the post-processing
definitions and functions have been moved from the
:ref:`image <ref-classes-image>` class to the
:ref:`rootfs-postcommands <ref-classes-rootfs*>` class. Functionally,
however, they remain unchanged.

.. _migration-2.1-removed-recipes:

Removed Recipes
---------------

The following recipes have been removed in the 2.1 release:

-  ``gcc`` version 4.8: Versions 4.9 and 5.3 remain.

-  ``qt4``: All support for Qt 4.x has been moved out to a separate
   ``meta-qt4`` layer because Qt 4 is no longer supported upstream.

-  ``x11vnc``: Moved to the ``meta-oe`` layer.

-  ``linux-yocto-3.14``: No longer supported.

-  ``linux-yocto-3.19``: No longer supported.

-  ``libjpeg``: Replaced by the ``libjpeg-turbo`` recipe.

-  ``pth``: Became obsolete.

-  ``liboil``: Recipe is no longer needed and has been moved to the
   ``meta-multimedia`` layer.

-  ``gtk-theme-torturer``: Recipe is no longer needed and has been moved
   to the ``meta-gnome`` layer.

-  ``gnome-mime-data``: Recipe is no longer needed and has been moved to
   the ``meta-gnome`` layer.

-  ``udev``: Replaced by the ``eudev`` recipe for compatibility when
   using ``sysvinit`` with newer kernels.

-  ``python-pygtk``: Recipe became obsolete.

-  ``adt-installer``: Recipe became obsolete. See the "`ADT
   Removed <#migration-2.1-adt-removed>`__" section for more
   information.

.. _migration-2.1-class-changes:

Class Changes
-------------

The following classes have changed:

-  ``autotools_stage``: Removed because the
   :ref:`autotools <ref-classes-autotools>` class now provides its
   functionality. Recipes that inherited from ``autotools_stage`` should
   now inherit from ``autotools`` instead.

-  ``boot-directdisk``: Merged into the ``image-vm`` class. The
   ``boot-directdisk`` class was rarely directly used. Consequently,
   this change should not cause any issues.

-  ``bootimg``: Merged into the
   :ref:`image-live <ref-classes-image-live>` class. The ``bootimg``
   class was rarely directly used. Consequently, this change should not
   cause any issues.

-  ``packageinfo``: Removed due to its limited use by the Hob UI, which
   has itself been removed.

.. _migration-2.1-build-system-ui-changes:

Build System User Interface Changes
-----------------------------------

The following changes have been made to the build system user interface:

-  *Hob GTK+-based UI*: Removed because it is unmaintained and based on
   the outdated GTK+ 2 library. The Toaster web-based UI is much more
   capable and is actively maintained. See the
   ":ref:`toaster-manual/toaster-manual-setup-and-use:using the toaster web interface`"
   section in the Toaster User Manual for more information on this
   interface.

-  *"puccho" BitBake UI*: Removed because is unmaintained and no longer
   useful.

.. _migration-2.1-adt-removed:

ADT Removed
-----------

The Application Development Toolkit (ADT) has been removed because its
functionality almost completely overlapped with the :ref:`standard
SDK <sdk-manual/sdk-using:using the standard sdk>` and the
:ref:`extensible SDK <sdk-manual/sdk-extensible:using the extensible sdk>`. For
information on these SDKs and how to build and use them, see the
:doc:`../sdk-manual/sdk-manual` manual.

.. note::

   The Yocto Project Eclipse IDE Plug-in is still supported and is not
   affected by this change.

.. _migration-2.1-poky-reference-distribution-changes:

Poky Reference Distribution Changes
-----------------------------------

The following changes have been made for the Poky distribution:

-  The ``meta-yocto`` layer has been renamed to ``meta-poky`` to better
   match its purpose, which is to provide the Poky reference
   distribution. The ``meta-yocto-bsp`` layer retains its original name
   since it provides reference machines for the Yocto Project and it is
   otherwise unrelated to Poky. References to ``meta-yocto`` in your
   ``conf/bblayers.conf`` should automatically be updated, so you should
   not need to change anything unless you are relying on this naming
   elsewhere.

-  The :ref:`uninative <ref-classes-uninative>` class is now enabled
   by default in Poky. This class attempts to isolate the build system
   from the host distribution's C library and makes re-use of native
   shared state artifacts across different host distributions practical.
   With this class enabled, a tarball containing a pre-built C library
   is downloaded at the start of the build.

   The ``uninative`` class is enabled through the
   ``meta/conf/distro/include/yocto-uninative.inc`` file, which for
   those not using the Poky distribution, can include to easily enable
   the same functionality.

   Alternatively, if you wish to build your own ``uninative`` tarball,
   you can do so by building the ``uninative-tarball`` recipe, making it
   available to your build machines (e.g. over HTTP/HTTPS) and setting a
   similar configuration as the one set by ``yocto-uninative.inc``.

-  Static library generation, for most cases, is now disabled by default
   in the Poky distribution. Disabling this generation saves some build
   time as well as the size used for build output artifacts.

   Disabling this library generation is accomplished through a
   ``meta/conf/distro/include/no-static-libs.inc``, which for those not
   using the Poky distribution can easily include to enable the same
   functionality.

   Any recipe that needs to opt-out of having the "--disable-static"
   option specified on the configure command line either because it is
   not a supported option for the configure script or because static
   libraries are needed should set the following variable:
   DISABLE_STATIC = ""

-  The separate ``poky-tiny`` distribution now uses the musl C library
   instead of a heavily pared down ``glibc``. Using musl results in a
   smaller distribution and facilitates much greater maintainability
   because musl is designed to have a small footprint.

   If you have used ``poky-tiny`` and have customized the ``glibc``
   configuration you will need to redo those customizations with musl
   when upgrading to the new release.

.. _migration-2.1-packaging-changes:

Packaging Changes
-----------------

The following changes have been made to packaging:

-  The ``runuser`` and ``mountpoint`` binaries, which were previously in
   the main ``util-linux`` package, have been split out into the
   ``util-linux-runuser`` and ``util-linux-mountpoint`` packages,
   respectively.

-  The ``python-elementtree`` package has been merged into the
   ``python-xml`` package.

.. _migration-2.1-tuning-file-changes:

Tuning File Changes
-------------------

The following changes have been made to the tuning files:

-  The "no-thumb-interwork" tuning feature has been dropped from the ARM
   tune include files. Because interworking is required for ARM EABI,
   attempting to disable it through a tuning feature no longer makes
   sense.

   .. note::

      Support for ARM OABI was deprecated in gcc 4.7.

-  The ``tune-cortexm*.inc`` and ``tune-cortexr4.inc`` files have been
   removed because they are poorly tested. Until the OpenEmbedded build
   system officially gains support for CPUs without an MMU, these tuning
   files would probably be better maintained in a separate layer if
   needed.

.. _migration-2.1-supporting-gobject-introspection:

Supporting GObject Introspection
--------------------------------

This release supports generation of GLib Introspective Repository (GIR)
files through GObject introspection, which is the standard mechanism for
accessing GObject-based software from runtime environments. You can
enable, disable, and test the generation of this data. See the
":ref:`dev-manual/dev-manual-common-tasks:enabling gobject introspection support`"
section in the Yocto Project Development Tasks Manual for more
information.

.. _migration-2.1-miscellaneous-changes:

Miscellaneous Changes
---------------------

These additional changes exist:

-  The minimum Git version has been increased to 1.8.3.1. If your host
   distribution does not provide a sufficiently recent version, you can
   install the buildtools, which will provide it. See the "`Required
   Git, tar, Python and gcc
   Versions <#required-git-tar-python-and-gcc-versions>`__" section for
   more information on the buildtools tarball.

-  The buggy and incomplete support for the RPM version 4 package
   manager has been removed. The well-tested and maintained support for
   RPM version 5 remains.

-  Previously, the following list of packages were removed if
   package-management was not in
   :term:`IMAGE_FEATURES`, regardless of any
   dependencies:
   ::

      update-rc.d
      base-passwd
      shadow
      update-alternatives

   run-postinsts With the Yocto Project 2.1 release, these packages are
   only removed if "read-only-rootfs" is in ``IMAGE_FEATURES``, since
   they might still be needed for a read-write image even in the absence
   of a package manager (e.g. if users need to be added, modified, or
   removed at runtime).

-  The
   :ref:`devtool modify <sdk-manual/sdk-extensible:use \`\`devtool modify\`\` to modify the source of an existing component>`
   command now defaults to extracting the source since that is most
   commonly expected. The "-x" or "--extract" options are now no-ops. If
   you wish to provide your own existing source tree, you will now need
   to specify either the "-n" or "--no-extract" options when running
   ``devtool modify``.

-  If the formfactor for a machine is either not supplied or does not
   specify whether a keyboard is attached, then the default is to assume
   a keyboard is attached rather than assume no keyboard. This change
   primarily affects the Sato UI.

-  The ``.debug`` directory packaging is now automatic. If your recipe
   builds software that installs binaries into directories other than
   the standard ones, you no longer need to take care of setting
   ``FILES_${PN}-dbg`` to pick up the resulting ``.debug`` directories
   as these directories are automatically found and added.

-  Inaccurate disk and CPU percentage data has been dropped from
   ``buildstats`` output. This data has been replaced with
   ``getrusage()`` data and corrected IO statistics. You will probably
   need to update any custom code that reads the ``buildstats`` data.

-  The ``meta/conf/distro/include/package_regex.inc`` is now deprecated.
   The contents of this file have been moved to individual recipes.

   .. note::

      Because this file will likely be removed in a future Yocto Project
      release, it is suggested that you remove any references to the
      file that might be in your configuration.

-  The ``v86d/uvesafb`` has been removed from the ``genericx86`` and
   ``genericx86-64`` reference machines, which are provided by the
   ``meta-yocto-bsp`` layer. Most modern x86 boards do not rely on this
   file and it only adds kernel error messages during startup. If you do
   still need to support ``uvesafb``, you can simply add ``v86d`` to
   your image.

-  Build sysroot paths are now removed from debug symbol files. Removing
   these paths means that remote GDB using an unstripped build system
   sysroot will no longer work (although this was never documented to
   work). The supported method to accomplish something similar is to set
   ``IMAGE_GEN_DEBUGFS`` to "1", which will generate a companion debug
   image containing unstripped binaries and associated debug sources
   alongside the image.


