Release 2.7 (warrior)
=====================

This section provides migration information for moving to the Yocto
Project 2.7 Release (codename "warrior") from the prior release.

.. _migration-2.7-bitbake-changes:

BitBake Changes
---------------

The following changes have been made to BitBake:

-  BitBake now checks anonymous Python functions and pure Python
   functions (e.g. ``def funcname:``) in the metadata for tab
   indentation. If found, BitBake produces a warning.

-  Bitbake now checks
   :term:`BBFILE_COLLECTIONS` for duplicate
   entries and triggers an error if any are found.

.. _migration-2.7-eclipse-support-dropped:

Eclipse Support Removed
-----------------------

Support for the Eclipse IDE has been removed. Support continues for
those releases prior to 2.7 that did include support. The 2.7 release
does not include the Eclipse Yocto plugin.

.. _migration-2.7-qemu-native-splits-system-and-user-mode-parts:

``qemu-native`` Splits the System and User-Mode Parts
-----------------------------------------------------

The system and user-mode parts of ``qemu-native`` are now split.
``qemu-native`` provides the user-mode components and
``qemu-system-native`` provides the system components. If you have
recipes that depend on QEMU's system emulation functionality at build
time, they should now depend upon ``qemu-system-native`` instead of
``qemu-native``.

.. _migration-2.7-upstream-tracking.inc-removed:

The ``upstream-tracking.inc`` File Has Been Removed
---------------------------------------------------

The previously deprecated ``upstream-tracking.inc`` file is now removed.
Any ``UPSTREAM_TRACKING*`` variables are now set in the corresponding
recipes instead.

Remove any references you have to the ``upstream-tracking.inc`` file in
your configuration.

.. _migration-2.7-distro-features-libc-removed:

The ``DISTRO_FEATURES_LIBC`` Variable Has Been Removed
------------------------------------------------------

The ``DISTRO_FEATURES_LIBC`` variable is no longer used. The ability to
configure glibc using kconfig has been removed for quite some time
making the ``libc-*`` features set no longer effective.

Remove any references you have to ``DISTRO_FEATURES_LIBC`` in your own
layers.

.. _migration-2.7-license-values:

License Value Corrections
-------------------------

The following corrections have been made to the
:term:`LICENSE` values set by recipes:

- *socat*: Corrected :term:`LICENSE` to be "GPLv2" rather than "GPLv2+".
- *libgfortran*: Set license to "GPL-3.0-with-GCC-exception".
- *elfutils*: Removed "Elfutils-Exception" and set to "GPLv2" for shared libraries

.. _migration-2.7-packaging-changes:

Packaging Changes
-----------------

This section provides information about packaging changes.

-  ``bind``: The ``nsupdate`` binary has been moved to the
   ``bind-utils`` package.

-  Debug split: The default debug split has been changed to create
   separate source packages (i.e. ``package_name-dbg`` and
   ``package_name-src``). If you are currently using ``dbg-pkgs`` in
   :term:`IMAGE_FEATURES` to bring in debug
   symbols and you still need the sources, you must now also add
   ``src-pkgs`` to :term:`IMAGE_FEATURES`. Source packages remain in the
   target portion of the SDK by default, unless you have set your own
   value for :term:`SDKIMAGE_FEATURES` that
   does not include ``src-pkgs``.

-  Mount all using ``util-linux``: ``/etc/default/mountall`` has moved
   into the -mount sub-package.

-  Splitting binaries using ``util-linux``: ``util-linux`` now splits
   each binary into its own package for fine-grained control. The main
   ``util-linux`` package pulls in the individual binary packages using
   the :term:`RRECOMMENDS` and
   :term:`RDEPENDS` variables. As a result, existing
   images should not see any changes assuming
   :term:`NO_RECOMMENDATIONS` is not set.

-  ``netbase/base-files``: ``/etc/hosts`` has moved from ``netbase`` to
   ``base-files``.

-  ``tzdata``: The main package has been converted to an empty meta
   package that pulls in all ``tzdata`` packages by default.

-  ``lrzsz``: This package has been removed from
   ``packagegroup-self-hosted`` and
   ``packagegroup-core-tools-testapps``. The X/Y/ZModem support is less
   likely to be needed on modern systems. If you are relying on these
   packagegroups to include the ``lrzsz`` package in your image, you now
   need to explicitly add the package.

.. _migration-2.7-removed-recipes:

Removed Recipes
---------------

The following recipes have been removed:

- *gcc*: Drop version 7.3 recipes. Version 8.3 now remains.
- *linux-yocto*: Drop versions 4.14 and 4.18 recipes. Versions 4.19 and 5.0 remain.
- *go*: Drop version 1.9 recipes. Versions 1.11 and 1.12 remain.
- *xvideo-tests*: Became obsolete.
- *libart-lgpl*: Became obsolete.
- *gtk-icon-utils-native*: These tools are now provided by gtk+3-native
- *gcc-cross-initial*: No longer needed. gcc-cross/gcc-crosssdk is now used instead.
- *gcc-crosssdk-initial*: No longer needed. gcc-cross/gcc-crosssdk is now used instead.
- *glibc-initial*: Removed because the benefits of having it for site_config are currently outweighed by the cost of building the recipe.

.. _migration-2.7-removed-classes:

Removed Classes
---------------

The following classes have been removed:

- *distutils-tools*: This class was never used.
- *bugzilla.bbclass*: Became obsolete.
- *distrodata*: This functionally has been replaced by a more modern tinfoil-based implementation.

.. _migration-2.7-miscellaneous-changes:

Miscellaneous Changes
---------------------

The following miscellaneous changes occurred:

-  The ``distro`` subdirectory of the Poky repository has been removed
   from the top-level ``scripts`` directory.

-  Perl now builds for the target using
   `perl-cross <https://arsv.github.io/perl-cross/>`_ for better
   maintainability and improved build performance. This change should
   not present any problems unless you have heavily customized your Perl
   recipe.

-  ``arm-tunes``: Removed the "-march" option if mcpu is already added.

-  ``update-alternatives``: Convert file renames to
   :term:`PACKAGE_PREPROCESS_FUNCS`

-  ``base/pixbufcache``: Obsolete ``sstatecompletions`` code has been
   removed.

-  :ref:`native <ref-classes-native>` class:
   :term:`RDEPENDS` handling has been enabled.

-  ``inetutils``: This recipe has rsh disabled.


