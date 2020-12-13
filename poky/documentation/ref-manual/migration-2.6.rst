Moving to the Yocto Project 2.6 Release
=======================================

This section provides migration information for moving to the Yocto
Project 2.6 Release from the prior release.

.. _migration-2.6-gcc-changes:

GCC 8.2 is Now Used by Default
------------------------------

The GNU Compiler Collection version 8.2 is now used by default for
compilation. For more information on what has changed in the GCC 8.x
release, see https://gcc.gnu.org/gcc-8/changes.html.

If you still need to compile with version 7.x, GCC 7.3 is also provided.
You can select this version by setting the and can be selected by
setting the :term:`GCCVERSION` variable to "7.%" in
your configuration.

.. _migration-2.6-removed-recipes:

Removed Recipes
---------------

The following recipes have been removed:

- *beecrypt*: No longer needed since moving to RPM 4.
- *bigreqsproto*: Replaced by ``xorgproto``.
- *calibrateproto*: Removed in favor of ``xinput``.
- *compositeproto*: Replaced by ``xorgproto``.
- *damageproto*: Replaced by ``xorgproto``.
- *dmxproto*: Replaced by ``xorgproto``.
- *dri2proto*: Replaced by ``xorgproto``.
- *dri3proto*: Replaced by ``xorgproto``.
- *eee-acpi-scripts*: Became obsolete.
- *fixesproto*: Replaced by ``xorgproto``.
- *fontsproto*: Replaced by ``xorgproto``.
- *fstests*: Became obsolete.
- *gccmakedep*: No longer used.
- *glproto*: Replaced by ``xorgproto``.
- *gnome-desktop3*: No longer needed. This recipe has moved to ``meta-oe``.
- *icon-naming-utils*: No longer used since the Sato theme was removed in 2016.
- *inputproto*: Replaced by ``xorgproto``.
- *kbproto*: Replaced by ``xorgproto``.
- *libusb-compat*: Became obsolete.
- *libuser*: Became obsolete.
- *libnfsidmap*: No longer an external requirement since ``nfs-utils`` 2.2.1. ``libnfsidmap`` is now integrated.
- *libxcalibrate*: No longer needed with ``xinput``
- *mktemp*: Became obsolete. The ``mktemp`` command is provided by both ``busybox`` and ``coreutils``.
- *ossp-uuid*: Is not being maintained and has mostly been replaced by ``uuid.h`` in ``util-linux``.
- *pax-utils*: No longer needed. Previous QA tests that did use this recipe are now done at build time.
- *pcmciautils*: Became obsolete.
- *pixz*: No longer needed. ``xz`` now supports multi-threaded compression.
- *presentproto*: Replaced by ``xorgproto``.
- *randrproto*: Replaced by ``xorgproto``.
- *recordproto*: Replaced by ``xorgproto``.
- *renderproto*: Replaced by ``xorgproto``.
- *resourceproto*: Replaced by ``xorgproto``.
- *scrnsaverproto*: Replaced by ``xorgproto``.
- *trace-cmd*: Became obsolete. ``perf`` replaced this recipe's functionally.
- *videoproto*: Replaced by ``xorgproto``.
- *wireless-tools*: Became obsolete. Superseded by ``iw``.
- *xcmiscproto*: Replaced by ``xorgproto``.
- *xextproto*: Replaced by ``xorgproto``.
- *xf86dgaproto*: Replaced by ``xorgproto``.
- *xf86driproto*: Replaced by ``xorgproto``.
- *xf86miscproto*: Replaced by ``xorgproto``.
- *xf86-video-omapfb*: Became obsolete. Use kernel modesetting driver instead.
- *xf86-video-omap*: Became obsolete. Use kernel modesetting driver instead.
- *xf86vidmodeproto*: Replaced by ``xorgproto``.
- *xineramaproto*: Replaced by ``xorgproto``.
- *xproto*: Replaced by ``xorgproto``.
- *yasm*: No longer needed since previous usages are now satisfied by ``nasm``.

.. _migration-2.6-packaging-changes:

Packaging Changes
-----------------

The following packaging changes have been made:

-  *cmake*: ``cmake.m4`` and ``toolchain`` files have been moved to
   the main package.

-  *iptables*: The ``iptables`` modules have been split into
   separate packages.

-  *alsa-lib*: ``libasound`` is now in the main ``alsa-lib`` package
   instead of ``libasound``.

-  *glibc*: ``libnss-db`` is now in its own package along with a
   ``/var/db/makedbs.sh`` script to update databases.

-  *python and python3*: The main package has been removed from
   the recipe. You must install specific packages or ``python-modules``
   / ``python3-modules`` for everything.

-  *systemtap*: Moved ``systemtap-exporter`` into its own package.

.. _migration-2.6-xorg-protocol-dependencies:

XOrg Protocol dependencies
--------------------------

The ``*proto`` upstream repositories have been combined into one
"xorgproto" repository. Thus, the corresponding recipes have also been
combined into a single ``xorgproto`` recipe. Any recipes that depend
upon the older ``*proto`` recipes need to be changed to depend on the
newer ``xorgproto`` recipe instead.

For names of recipes removed because of this repository change, see the
`Removed Recipes <#removed-recipes>`__ section.

.. _migration-2.6-distutils-distutils3-fetching-dependencies:

``distutils`` and ``distutils3`` Now Prevent Fetching Dependencies During the ``do_configure`` Task
---------------------------------------------------------------------------------------------------

Previously, it was possible for Python recipes that inherited the
:ref:`distutils <ref-classes-distutils>` and
:ref:`distutils3 <ref-classes-distutils3>` classes to fetch code
during the :ref:`ref-tasks-configure` task to satisfy
dependencies mentioned in ``setup.py`` if those dependencies were not
provided in the sysroot (i.e. recipes providing the dependencies were
missing from :term:`DEPENDS`).

.. note::

   This change affects classes beyond just the two mentioned (i.e.
   ``distutils`` and ``distutils3``). Any recipe that inherits ``distutils*``
   classes are affected. For example, the ``setuptools`` and ``setuptools3``
   recipes are affected since they inherit the ``distutils*`` classes.

Fetching these types of dependencies that are not provided in the
sysroot negatively affects the ability to reproduce builds. This type of
fetching is now explicitly disabled. Consequently, any missing
dependencies in Python recipes that use these classes now result in an
error during the ``do_configure`` task.

.. _migration-2.6-linux-yocto-configuration-audit-issues-now-correctly-reported:

``linux-yocto`` Configuration Audit Issues Now Correctly Reported
-----------------------------------------------------------------

Due to a bug, the kernel configuration audit functionality was not
writing out any resulting warnings during the build. This issue is now
corrected. You might notice these warnings now if you have a custom
kernel configuration with a ``linux-yocto`` style kernel recipe.

.. _migration-2.6-image-kernel-artifact-naming-changes:

Image/Kernel Artifact Naming Changes
------------------------------------

The following changes have been made:

-  Name variables (e.g. :term:`IMAGE_NAME`) use a new
   ``IMAGE_VERSION_SUFFIX`` variable instead of
   :term:`DATETIME`. Using ``IMAGE_VERSION_SUFFIX``
   allows easier and more direct changes.

   The ``IMAGE_VERSION_SUFFIX`` variable is set in the ``bitbake.conf``
   configuration file as follows:
   ::

      IMAGE_VERSION_SUFFIX = "-${DATETIME}"

-  Several variables have changed names for consistency:
   ::

      Old Variable Name             New Variable Name
      ========================================================
      KERNEL_IMAGE_BASE_NAME        KERNEL_IMAGE_NAME
      KERNEL_IMAGE_SYMLINK_NAME     KERNEL_IMAGE_LINK_NAME
      MODULE_TARBALL_BASE_NAME      MODULE_TARBALL_NAME
      MODULE_TARBALL_SYMLINK_NAME   MODULE_TARBALL_LINK_NAME
      INITRAMFS_BASE_NAME           INITRAMFS_NAME

-  The ``MODULE_IMAGE_BASE_NAME`` variable has been removed. The module
   tarball name is now controlled directly with the
   :term:`MODULE_TARBALL_NAME` variable.

-  The :term:`KERNEL_DTB_NAME` and
   :term:`KERNEL_DTB_LINK_NAME` variables
   have been introduced to control kernel Device Tree Binary (DTB)
   artifact names instead of mangling ``KERNEL_IMAGE_*`` variables.

-  The :term:`KERNEL_FIT_NAME` and
   :term:`KERNEL_FIT_LINK_NAME` variables
   have been introduced to specify the name of flattened image tree
   (FIT) kernel images similar to other deployed artifacts.

-  The :term:`MODULE_TARBALL_NAME` and
   :term:`MODULE_TARBALL_LINK_NAME`
   variable values no longer include the "module-" prefix or ".tgz"
   suffix. These parts are now hardcoded so that the values are
   consistent with other artifact naming variables.

-  Added the :term:`INITRAMFS_LINK_NAME`
   variable so that the symlink can be controlled similarly to other
   artifact types.

-  :term:`INITRAMFS_NAME` now uses
   "${PKGE}-${PKGV}-${PKGR}-${MACHINE}${IMAGE_VERSION_SUFFIX}" instead
   of "${PV}-${PR}-${MACHINE}-${DATETIME}", which makes it consistent
   with other variables.

.. _migration-2.6-serial-console-deprecated:

``SERIAL_CONSOLE`` Deprecated
-----------------------------

The :term:`SERIAL_CONSOLE` variable has been
functionally replaced by the
:term:`SERIAL_CONSOLES` variable for some time.
With the Yocto Project 2.6 release, ``SERIAL_CONSOLE`` has been
officially deprecated.

``SERIAL_CONSOLE`` will continue to work as before for the 2.6 release.
However, for the sake of future compatibility, it is recommended that
you replace all instances of ``SERIAL_CONSOLE`` with
``SERIAL_CONSOLES``.

.. note::

   The only difference in usage is that ``SERIAL_CONSOLES``
   expects entries to be separated using semicolons as compared to
   ``SERIAL_CONSOLE``, which expects spaces.

.. _migration-2.6-poky-sets-unknown-configure-option-to-qa-error:

Configure Script Reports Unknown Options as Errors
--------------------------------------------------

If the configure script reports an unknown option, this now triggers a
QA error instead of a warning. Any recipes that previously got away with
specifying such unknown options now need to be fixed.

.. _migration-2.6-override-changes:

Override Changes
----------------

The following changes have occurred:

-  The ``virtclass-native`` and ``virtclass-nativesdk`` Overrides Have
   Been Removed: The ``virtclass-native`` and ``virtclass-nativesdk``
   overrides have been deprecated since 2012 in favor of
   ``class-native`` and ``class-nativesdk``, respectively. Both
   ``virtclass-native`` and ``virtclass-nativesdk`` are now dropped.

   .. note::

      The ``virtclass-multilib-`` overrides for multilib are still valid.

-  The ``forcevariable`` Override Now Has a Higher Priority Than
   ``libc`` Overrides: The ``forcevariable`` override is documented to
   be the highest priority override. However, due to a long-standing
   quirk of how :term:`OVERRIDES` is set, the ``libc``
   overrides (e.g. ``libc-glibc``, ``libc-musl``, and so forth)
   erroneously had a higher priority. This issue is now corrected.

   It is likely this change will not cause any problems. However, it is
   possible with some unusual configurations that you might see a change
   in behavior if you were relying on the previous behavior. Be sure to
   check how you use ``forcevariable`` and ``libc-*`` overrides in your
   custom layers and configuration files to ensure they make sense.

-  The ``build-${BUILD_OS}`` Override Has Been Removed: The
   ``build-${BUILD_OS}``, which is typically ``build-linux``, override
   has been removed because building on a host operating system other
   than a recent version of Linux is neither supported nor recommended.
   Dropping the override avoids giving the impression that other host
   operating systems might be supported.

-  The "_remove" operator now preserves whitespace. Consequently, when
   specifying list items to remove, be aware that leading and trailing
   whitespace resulting from the removal is retained.

   See the ":ref:`bitbake:bitbake-user-manual/bitbake-user-manual-metadata:removal (override style syntax)`"
   section in the BitBake User Manual for a detailed example.

.. _migration-2.6-systemd-configuration-now-split-out-to-system-conf:

``systemd`` Configuration is Now Split Into ``systemd-conf``
------------------------------------------------------------

The configuration for the ``systemd`` recipe has been moved into a
``system-conf`` recipe. Moving this configuration to a separate recipe
avoids the ``systemd`` recipe from becoming machine-specific for cases
where machine-specific configurations need to be applied (e.g. for
``qemu*`` machines).

Currently, the new recipe packages the following files:
::

   ${sysconfdir}/machine-id
   ${sysconfdir}/systemd/coredump.conf
   ${sysconfdir}/systemd/journald.conf
   ${sysconfdir}/systemd/logind.conf
   ${sysconfdir}/systemd/system.conf
   ${sysconfdir}/systemd/user.conf

If you previously used bbappend files to append the ``systemd`` recipe to
change any of the listed files, you must do so for the ``systemd-conf``
recipe instead.

.. _migration-2.6-automatic-testing-changes:

Automatic Testing Changes
-------------------------

This section provides information about automatic testing changes:

-  ``TEST_IMAGE`` Variable Removed: Prior to this release, you set the
   ``TEST_IMAGE`` variable to "1" to enable automatic testing for
   successfully built images. The ``TEST_IMAGE`` variable no longer
   exists and has been replaced by the
   :term:`TESTIMAGE_AUTO` variable.

-  Inheriting the ``testimage`` and ``testsdk`` Classes: Best
   practices now dictate that you use the
   :term:`IMAGE_CLASSES` variable rather than the
   :term:`INHERIT` variable when you inherit the
   :ref:`testimage <ref-classes-testimage*>` and
   :ref:`testsdk <ref-classes-testsdk>` classes used for automatic
   testing.

.. _migration-2.6-openssl-changes:

OpenSSL Changes
---------------

`OpenSSL <https://www.openssl.org/>`__ has been upgraded from 1.0 to
1.1. By default, this upgrade could cause problems for recipes that have
both versions in their dependency chains. The problem is that both
versions cannot be installed together at build time.

.. note::

   It is possible to have both versions of the library at runtime.

.. _migration-2.6-bitbake-changes:

BitBake Changes
---------------

The server logfile ``bitbake-cookerdaemon.log`` is now always placed in
the :term:`Build Directory` instead of the current
directory.

.. _migration-2.6-security-changes:

Security Changes
----------------

The Poky distribution now uses security compiler flags by default.
Inclusion of these flags could cause new failures due to stricter
checking for various potential security issues in code.

.. _migration-2.6-post-installation-changes:

Post Installation Changes
-------------------------

You must explicitly mark post installs to defer to the target. If you
want to explicitly defer a postinstall to first boot on the target
rather than at rootfs creation time, use ``pkg_postinst_ontarget()`` or
call ``postinst_intercept delay_to_first_boot`` from ``pkg_postinst()``.
Any failure of a ``pkg_postinst()`` script (including exit 1) triggers
an error during the :ref:`ref-tasks-rootfs` task.

For more information on post-installation behavior, see the
":ref:`dev-manual/common-tasks:post-installation scripts`"
section in the Yocto Project Development Tasks Manual.

.. _migration-2.6-python-3-profile-guided-optimizations:

Python 3 Profile-Guided Optimization
------------------------------------

The ``python3`` recipe now enables profile-guided optimization. Using
this optimization requires a little extra build time in exchange for
improved performance on the target at runtime. Additionally, the
optimization is only enabled if the current
:term:`MACHINE` has support for user-mode emulation in
QEMU (i.e. "qemu-usermode" is in
:term:`MACHINE_FEATURES`, which it is by
default).

If you wish to disable Python profile-guided optimization regardless of
the value of ``MACHINE_FEATURES``, then ensure that
:term:`PACKAGECONFIG` for the ``python3`` recipe
does not contain "pgo". You could accomplish the latter using the
following at the configuration level:
::

   PACKAGECONFIG_remove_pn-python3 = "pgo"

Alternatively, you can set ``PACKAGECONFIG`` using an append file
for the ``python3`` recipe.

.. _migration-2.6-miscellaneous-changes:

Miscellaneous Changes
---------------------

The following miscellaneous changes occurred:

-  Default to using the Thumb-2 instruction set for armv7a and above. If
   you have any custom recipes that build software that needs to be
   built with the ARM instruction set, change the recipe to set the
   instruction set as follows:
   ::

      ARM_INSTRUCTION_SET = "arm"

-  ``run-postinsts`` no longer uses ``/etc/*-postinsts`` for
   ``dpkg/opkg`` in favor of built-in postinst support. RPM behavior
   remains unchanged.

-  The ``NOISO`` and ``NOHDD`` variables are no longer used. You now
   control building ``*.iso`` and ``*.hddimg`` image types directly by
   using the :term:`IMAGE_FSTYPES` variable.

-  The ``scripts/contrib/mkefidisk.sh`` has been removed in favor of
   Wic.

-  ``kernel-modules`` has been removed from
   :term:`RRECOMMENDS` for ``qemumips`` and
   ``qemumips64`` machines. Removal also impacts the ``x86-base.inc``
   file.

   .. note::

      ``genericx86`` and ``genericx86-64`` retain ``kernel-modules`` as part of
      the ``RRECOMMENDS`` variable setting.

-  The ``LGPLv2_WHITELIST_GPL-3.0`` variable has been removed. If you
   are setting this variable in your configuration, set or append it to
   the ``WHITELIST_GPL-3.0`` variable instead.

-  ``${ASNEEDED}`` is now included in the
   :term:`TARGET_LDFLAGS` variable directly. The
   remaining definitions from ``meta/conf/distro/include/as-needed.inc``
   have been moved to corresponding recipes.

-  Support for DSA host keys has been dropped from the OpenSSH recipes.
   If you are still using DSA keys, you must switch over to a more
   secure algorithm as recommended by OpenSSH upstream.

-  The ``dhcp`` recipe now uses the ``dhcpd6.conf`` configuration file
   in ``dhcpd6.service`` for IPv6 DHCP rather than re-using
   ``dhcpd.conf``, which is now reserved for IPv4.


