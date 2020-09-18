Moving to the Yocto Project 1.7 Release
=======================================

This section provides migration information for moving to the Yocto
Project 1.7 Release from the prior release.

.. _migration-1.7-changes-to-setting-qemu-packageconfig-options:

Changes to Setting QEMU ``PACKAGECONFIG`` Options in ``local.conf``
-------------------------------------------------------------------

The QEMU recipe now uses a number of
:term:`PACKAGECONFIG` options to enable various
optional features. The method used to set defaults for these options
means that existing ``local.conf`` files will need to be be modified to
append to ``PACKAGECONFIG`` for ``qemu-native`` and ``nativesdk-qemu``
instead of setting it. In other words, to enable graphical output for
QEMU, you should now have these lines in ``local.conf``:
::

   PACKAGECONFIG_append_pn-qemu-native = " sdl"
   PACKAGECONFIG_append_pn-nativesdk-qemu = " sdl"

.. _migration-1.7-minimum-git-version:

Minimum Git version
-------------------

The minimum :ref:`overview-manual/overview-manual-development-environment:git`
version required on the
build host is now 1.7.8 because the ``--list`` option is now required by
BitBake's Git fetcher. As always, if your host distribution does not
provide a version of Git that meets this requirement, you can use the
``buildtools-tarball`` that does. See the "`Required Git, tar, Python
and gcc Versions <#required-git-tar-python-and-gcc-versions>`__" section
for more information.

.. _migration-1.7-autotools-class-changes:

Autotools Class Changes
-----------------------

The following :ref:`autotools <ref-classes-autotools>` class changes
occurred:

-  *A separate build directory is now used by default:* The
   ``autotools`` class has been changed to use a directory for building
   (:term:`B`), which is separate from the source directory
   (:term:`S`). This is commonly referred to as ``B != S``, or
   an out-of-tree build.

   If the software being built is already capable of building in a
   directory separate from the source, you do not need to do anything.
   However, if the software is not capable of being built in this
   manner, you will need to either patch the software so that it can
   build separately, or you will need to change the recipe to inherit
   the :ref:`autotools-brokensep <ref-classes-autotools>` class
   instead of the ``autotools`` or ``autotools_stage`` classes.

-  The ``--foreign`` option is no longer passed to ``automake`` when
   running ``autoconf``: This option tells ``automake`` that a
   particular software package does not follow the GNU standards and
   therefore should not be expected to distribute certain files such as
   ``ChangeLog``, ``AUTHORS``, and so forth. Because the majority of
   upstream software packages already tell ``automake`` to enable
   foreign mode themselves, the option is mostly superfluous. However,
   some recipes will need patches for this change. You can easily make
   the change by patching ``configure.ac`` so that it passes "foreign"
   to ``AM_INIT_AUTOMAKE()``. See `this
   commit <http://cgit.openembedded.org/openembedded-core/commit/?id=01943188f85ce6411717fb5bf702d609f55813f2>`__
   for an example showing how to make the patch.

.. _migration-1.7-binary-configuration-scripts-disabled:

Binary Configuration Scripts Disabled
-------------------------------------

Some of the core recipes that package binary configuration scripts now
disable the scripts due to the scripts previously requiring error-prone
path substitution. Software that links against these libraries using
these scripts should use the much more robust ``pkg-config`` instead.
The list of recipes changed in this version (and their configuration
scripts) is as follows:
::

   directfb (directfb-config)
   freetype (freetype-config)
   gpgme (gpgme-config)
   libassuan (libassuan-config)
   libcroco (croco-6.0-config)
   libgcrypt (libgcrypt-config)
   libgpg-error (gpg-error-config)
   libksba (ksba-config)
   libpcap (pcap-config)
   libpcre (pcre-config)
   libpng (libpng-config, libpng16-config)
   libsdl (sdl-config)
   libusb-compat (libusb-config)
   libxml2 (xml2-config)
   libxslt (xslt-config)
   ncurses (ncurses-config)
   neon (neon-config)
   npth (npth-config)
   pth (pth-config)
   taglib (taglib-config)

Additionally, support for ``pkg-config`` has been added to some recipes in the
previous list in the rare cases where the upstream software package does
not already provide it.

.. _migration-1.7-glibc-replaces-eglibc:

``eglibc 2.19`` Replaced with ``glibc 2.20``
--------------------------------------------

Because ``eglibc`` and ``glibc`` were already fairly close, this
replacement should not require any significant changes to other software
that links to ``eglibc``. However, there were a number of minor changes
in ``glibc 2.20`` upstream that could require patching some software
(e.g. the removal of the ``_BSD_SOURCE`` feature test macro).

``glibc 2.20`` requires version 2.6.32 or greater of the Linux kernel.
Thus, older kernels will no longer be usable in conjunction with it.

For full details on the changes in ``glibc 2.20``, see the upstream
release notes
`here <https://sourceware.org/ml/libc-alpha/2014-09/msg00088.html>`__.

.. _migration-1.7-kernel-module-autoloading:

Kernel Module Autoloading
-------------------------

The :term:`module_autoload_* <module_autoload>` variable is now
deprecated and a new
:term:`KERNEL_MODULE_AUTOLOAD` variable
should be used instead. Also, :term:`module_conf_* <module_conf>`
must now be used in conjunction with a new
:term:`KERNEL_MODULE_PROBECONF` variable.
The new variables no longer require you to specify the module name as
part of the variable name. This change not only simplifies usage but
also allows the values of these variables to be appropriately
incorporated into task signatures and thus trigger the appropriate tasks
to re-execute when changed. You should replace any references to
``module_autoload_*`` with ``KERNEL_MODULE_AUTOLOAD``, and add any
modules for which ``module_conf_*`` is specified to
``KERNEL_MODULE_PROBECONF``.

.. _migration-1.7-qa-check-changes:

QA Check Changes
----------------

The following changes have occurred to the QA check process:

-  Additional QA checks ``file-rdeps`` and ``build-deps`` have been
   added in order to verify that file dependencies are satisfied (e.g.
   package contains a script requiring ``/bin/bash``) and build-time
   dependencies are declared, respectively. For more information, please
   see the "`QA Error and Warning Messages <#ref-qa-checks>`__" chapter.

-  Package QA checks are now performed during a new
   :ref:`ref-tasks-package_qa` task rather than being
   part of the :ref:`ref-tasks-package` task. This allows
   more parallel execution. This change is unlikely to be an issue
   except for highly customized recipes that disable packaging tasks
   themselves by marking them as ``noexec``. For those packages, you
   will need to disable the ``do_package_qa`` task as well.

-  Files being overwritten during the
   :ref:`ref-tasks-populate_sysroot` task now
   trigger an error instead of a warning. Recipes should not be
   overwriting files written to the sysroot by other recipes. If you
   have these types of recipes, you need to alter them so that they do
   not overwrite these files.

   You might now receive this error after changes in configuration or
   metadata resulting in orphaned files being left in the sysroot. If
   you do receive this error, the way to resolve the issue is to delete
   your :term:`TMPDIR` or to move it out of the way and
   then re-start the build. Anything that has been fully built up to
   that point and does not need rebuilding will be restored from the
   shared state cache and the rest of the build will be able to proceed
   as normal.

.. _migration-1.7-removed-recipes:

Removed Recipes
---------------

The following recipes have been removed:

-  ``x-load``: This recipe has been superseded by U-boot SPL for all
   Cortex-based TI SoCs. For legacy boards, the ``meta-ti`` layer, which
   contains a maintained recipe, should be used instead.

-  ``ubootchart``: This recipe is obsolete. A ``bootchart2`` recipe has
   been added to functionally replace it.

-  ``linux-yocto 3.4``: Support for the linux-yocto 3.4 kernel has been
   dropped. Support for the 3.10 and 3.14 kernels remains, while support
   for version 3.17 has been added.

-  ``eglibc`` has been removed in favor of ``glibc``. See the
   "```eglibc 2.19`` Replaced with
   ``glibc 2.20`` <#migration-1.7-glibc-replaces-eglibc>`__" section for
   more information.

.. _migration-1.7-miscellaneous-changes:

Miscellaneous Changes
---------------------

The following miscellaneous change occurred:

-  The build history feature now writes ``build-id.txt`` instead of
   ``build-id``. Additionally, ``build-id.txt`` now contains the full
   build header as printed by BitBake upon starting the build. You
   should manually remove old "build-id" files from your existing build
   history repositories to avoid confusion. For information on the build
   history feature, see the
   ":ref:`dev-manual/dev-manual-common-tasks:maintaining build output quality`"
   section in the Yocto Project Development Tasks Manual.


