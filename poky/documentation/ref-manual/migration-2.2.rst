Moving to the Yocto Project 2.2 Release
=======================================

This section provides migration information for moving to the Yocto
Project 2.2 Release from the prior release.

.. _migration-2.2-minimum-kernel-version:

Minimum Kernel Version
----------------------

The minimum kernel version for the target system and for SDK is now
3.2.0, due to the upgrade to ``glibc 2.24``. Specifically, for
AArch64-based targets the version is 3.14. For Nios II-based targets,
the minimum kernel version is 3.19.

.. note::

   For x86 and x86_64, you can reset :term:`OLDEST_KERNEL`
   to anything down to 2.6.32 if desired.

.. _migration-2.2-staging-directories-in-sysroot-simplified:

Staging Directories in Sysroot Has Been Simplified
--------------------------------------------------

The way directories are staged in sysroot has been simplified and
introduces the new :term:`SYSROOT_DIRS`,
:term:`SYSROOT_DIRS_NATIVE`, and
:term:`SYSROOT_DIRS_BLACKLIST`. See the
`v2 patch series on the OE-Core Mailing
List <http://lists.openembedded.org/pipermail/openembedded-core/2016-May/121365.html>`__
for additional information.

.. _migration-2.2-removal-of-old-images-from-tmp-deploy-now-enabled:

Removal of Old Images and Other Files in ``tmp/deploy`` Now Enabled
-------------------------------------------------------------------

Removal of old images and other files in ``tmp/deploy/`` is now enabled
by default due to a new staging method used for those files. As a result
of this change, the ``RM_OLD_IMAGE`` variable is now redundant.

.. _migration-2.2-python-changes:

Python Changes
--------------

The following changes for Python occurred:

.. _migration-2.2-bitbake-now-requires-python-3.4:

BitBake Now Requires Python 3.4+
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

BitBake requires Python 3.4 or greater.

.. _migration-2.2-utf-8-locale-required-on-build-host:

UTF-8 Locale Required on Build Host
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A UTF-8 locale is required on the build host due to Python 3. Since
C.UTF-8 is not a standard, the default is en_US.UTF-8.

.. _migration-2.2-metadata-now-must-use-python-3-syntax:

Metadata Must Now Use Python 3 Syntax
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The metadata is now required to use Python 3 syntax. For help preparing
metadata, see any of the many Python 3 porting guides available.
Alternatively, you can reference the conversion commits for Bitbake and
you can use :term:`OpenEmbedded-Core (OE-Core)` as a guide for changes. Following are
particular areas of interest:

  - subprocess command-line pipes needing locale decoding

  - the syntax for octal values changed

  - the ``iter*()`` functions changed name

  - iterators now return views, not lists

  - changed names for Python modules

.. _migration-2.2-target-python-recipes-switched-to-python-3:

Target Python Recipes Switched to Python 3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Most target Python recipes have now been switched to Python 3.
Unfortunately, systems using RPM as a package manager and providing
online package-manager support through SMART still require Python 2.

.. note::

   Python 2 and recipes that use it can still be built for the target as
   with previous versions.

.. _migration-2.2-buildtools-tarball-includes-python-3:

``buildtools-tarball`` Includes Python 3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

``buildtools-tarball`` now includes Python 3.

.. _migration-2.2-uclibc-replaced-by-musl:

uClibc Replaced by musl
-----------------------

uClibc has been removed in favor of musl. Musl has matured, is better
maintained, and is compatible with a wider range of applications as
compared to uClibc.

.. _migration-2.2-B-no-longer-default-working-directory-for-tasks:

``${B}`` No Longer Default Working Directory for Tasks
------------------------------------------------------

``${``\ :term:`B`\ ``}`` is no longer the default working
directory for tasks. Consequently, any custom tasks you define now need
to either have the
``[``\ :ref:`dirs <bitbake:bitbake-user-manual/bitbake-user-manual-metadata:variable flags>`\ ``]`` flag
set, or the task needs to change into the appropriate working directory
manually (e.g using ``cd`` for a shell task).

.. note::

   The preferred method is to use the
   [dirs]
   flag.

.. _migration-2.2-runqemu-ported-to-python:

``runqemu`` Ported to Python
----------------------------

``runqemu`` has been ported to Python and has changed behavior in some
cases. Previous usage patterns continue to be supported.

The new ``runqemu`` is a Python script. Machine knowledge is no longer
hardcoded into ``runqemu``. You can choose to use the ``qemuboot``
configuration file to define the BSP's own arguments and to make it
bootable with ``runqemu``. If you use a configuration file, use the
following form:
::

   image-name-machine.qemuboot.conf

The configuration file
enables fine-grained tuning of options passed to QEMU without the
``runqemu`` script hard-coding any knowledge about different machines.
Using a configuration file is particularly convenient when trying to use
QEMU with machines other than the ``qemu*`` machines in
:term:`OpenEmbedded-Core (OE-Core)`. The ``qemuboot.conf`` file is generated by the
``qemuboot`` class when the root filesystem is being build (i.e. build
rootfs). QEMU boot arguments can be set in BSP's configuration file and
the ``qemuboot`` class will save them to ``qemuboot.conf``.

If you want to use ``runqemu`` without a configuration file, use the
following command form:
::

   $ runqemu machine rootfs kernel [options]

Supported machines are as follows:

  - qemuarm
  - qemuarm64
  - qemux86
  - qemux86-64
  - qemuppc
  - qemumips
  - qemumips64
  - qemumipsel
  - qemumips64el

Consider the
following example, which uses the ``qemux86-64`` machine, provides a
root filesystem, provides an image, and uses the ``nographic`` option: ::

   $ runqemu qemux86-64 tmp/deploy/images/qemux86-64/core-image-minimal-qemux86-64.ext4 tmp/deploy/images/qemux86-64/bzImage nographic

Following is a list of variables that can be set in configuration files
such as ``bsp.conf`` to enable the BSP to be booted by ``runqemu``:

.. note::

   "QB" means "QEMU Boot".

::

   QB_SYSTEM_NAME: QEMU name (e.g. "qemu-system-i386")
   QB_OPT_APPEND: Options to append to QEMU (e.g. "-show-cursor")
   QB_DEFAULT_KERNEL: Default kernel to boot (e.g. "bzImage")
   QB_DEFAULT_FSTYPE: Default FSTYPE to boot (e.g. "ext4")
   QB_MEM: Memory (e.g. "-m 512")
   QB_MACHINE: QEMU machine (e.g. "-machine virt")
   QB_CPU: QEMU cpu (e.g. "-cpu qemu32")
   QB_CPU_KVM: Similar to QB_CPU except used for kvm support (e.g. "-cpu kvm64")
   QB_KERNEL_CMDLINE_APPEND: Options to append to the kernel's -append
                             option (e.g. "console=ttyS0 console=tty")
   QB_DTB: QEMU dtb name
   QB_AUDIO_DRV: QEMU audio driver (e.g. "alsa", set it when support audio)
   QB_AUDIO_OPT: QEMU audio option (e.g. "-soundhw ac97,es1370"), which is used
                 when QB_AUDIO_DRV is set.
   QB_KERNEL_ROOT: Kernel's root (e.g. /dev/vda)
   QB_TAP_OPT: Network option for 'tap' mode (e.g.
               "-netdev tap,id=net0,ifname=@TAP@,script=no,downscript=no -device virtio-net-device,netdev=net0").
                runqemu will replace "@TAP@" with the one that is used, such as tap0, tap1 ...
   QB_SLIRP_OPT: Network option for SLIRP mode (e.g. "-netdev user,id=net0 -device virtio-net-device,netdev=net0")
   QB_ROOTFS_OPT: Used as rootfs (e.g.
                  "-drive id=disk0,file=@ROOTFS@,if=none,format=raw -device virtio-blk-device,drive=disk0").
                  runqemu will replace "@ROOTFS@" with the one which is used, such as
                  core-image-minimal-qemuarm64.ext4.
   QB_SERIAL_OPT: Serial port (e.g. "-serial mon:stdio")
   QB_TCPSERIAL_OPT: tcp serial port option (e.g.
                     " -device virtio-serial-device -chardev socket,id=virtcon,port=@PORT@,host=127.0.0.1 -device      virtconsole,chardev=virtcon"
                     runqemu will replace "@PORT@" with the port number which is used.

To use ``runqemu``, set :term:`IMAGE_CLASSES` as
follows and run ``runqemu``:

.. note::

   For command-line syntax, use ``runqemu help``.

::

   IMAGE_CLASSES += "qemuboot"

.. _migration-2.2-default-linker-hash-style-changed:

Default Linker Hash Style Changed
---------------------------------

The default linker hash style for ``gcc-cross`` is now "sysv" in order
to catch recipes that are building software without using the
OpenEmbedded :term:`LDFLAGS`. This change could result in
seeing some "No GNU_HASH in the elf binary" QA issues when building such
recipes. You need to fix these recipes so that they use the expected
``LDFLAGS``. Depending on how the software is built, the build system
used by the software (e.g. a Makefile) might need to be patched.
However, sometimes making this fix is as simple as adding the following
to the recipe:
::

   TARGET_CC_ARCH += "${LDFLAGS}"

.. _migration-2.2-kernel-image-base-name-no-longer-uses-kernel-imagetype:

``KERNEL_IMAGE_BASE_NAME`` no Longer Uses ``KERNEL_IMAGETYPE``
--------------------------------------------------------------

The ``KERNEL_IMAGE_BASE_NAME`` variable no longer uses the
:term:`KERNEL_IMAGETYPE` variable to create the
image's base name. Because the OpenEmbedded build system can now build
multiple kernel image types, this part of the kernel image base name as
been removed leaving only the following:
::

   KERNEL_IMAGE_BASE_NAME ?= "${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"

If you have recipes or
classes that use ``KERNEL_IMAGE_BASE_NAME`` directly, you might need to
update the references to ensure they continue to work.

.. _migration-2.2-bitbake-changes:

BitBake Changes
---------------

The following changes took place for BitBake:

-  The "goggle" UI and standalone image-writer tool have been removed as
   they both require GTK+ 2.0 and were not being maintained.

-  The Perforce fetcher now supports :term:`SRCREV` for
   specifying the source revision to use, be it
   ``${``\ :term:`AUTOREV`\ ``}``, changelist number,
   p4date, or label, in preference to separate
   :term:`SRC_URI` parameters to specify these. This
   change is more in-line with how the other fetchers work for source
   control systems. Recipes that fetch from Perforce will need to be
   updated to use ``SRCREV`` in place of specifying the source revision
   within ``SRC_URI``.

-  Some of BitBake's internal code structures for accessing the recipe
   cache needed to be changed to support the new multi-configuration
   functionality. These changes will affect external tools that use
   BitBake's tinfoil module. For information on these changes, see the
   changes made to the scripts supplied with OpenEmbedded-Core:
   `1 <http://git.yoctoproject.org/cgit/cgit.cgi/poky/commit/?id=189371f8393971d00bca0fceffd67cc07784f6ee>`__
   and
   `2 <http://git.yoctoproject.org/cgit/cgit.cgi/poky/commit/?id=4a5aa7ea4d07c2c90a1654b174873abb018acc67>`__.

-  The task management code has been rewritten to avoid using ID
   indirection in order to improve performance. This change is unlikely
   to cause any problems for most users. However, the setscene
   verification function as pointed to by
   ``BB_SETSCENE_VERIFY_FUNCTION`` needed to change signature.
   Consequently, a new variable named ``BB_SETSCENE_VERIFY_FUNCTION2``
   has been added allowing multiple versions of BitBake to work with
   suitably written metadata, which includes OpenEmbedded-Core and Poky.
   Anyone with custom BitBake task scheduler code might also need to
   update the code to handle the new structure.

.. _migration-2.2-swabber-has-been-removed:

Swabber has Been Removed
------------------------

Swabber, a tool that was intended to detect host contamination in the
build process, has been removed, as it has been unmaintained and unused
for some time and was never particularly effective. The OpenEmbedded
build system has since incorporated a number of mechanisms including
enhanced QA checks that mean that there is less of a need for such a
tool.

.. _migration-2.2-removed-recipes:

Removed Recipes
---------------

The following recipes have been removed:

-  ``augeas``: No longer needed and has been moved to ``meta-oe``.

-  ``directfb``: Unmaintained and has been moved to ``meta-oe``.

-  ``gcc``: Removed 4.9 version. Versions 5.4 and 6.2 are still present.

-  ``gnome-doc-utils``: No longer needed.

-  ``gtk-doc-stub``: Replaced by ``gtk-doc``.

-  ``gtk-engines``: No longer needed and has been moved to
   ``meta-gnome``.

-  ``gtk-sato-engine``: Became obsolete.

-  ``libglade``: No longer needed and has been moved to ``meta-oe``.

-  ``libmad``: Unmaintained and functionally replaced by ``libmpg123``.
   ``libmad`` has been moved to ``meta-oe``.

-  ``libowl``: Became obsolete.

-  ``libxsettings-client``: No longer needed.

-  ``oh-puzzles``: Functionally replaced by ``puzzles``.

-  ``oprofileui``: Became obsolete. OProfile has been largely supplanted
   by perf.

-  ``packagegroup-core-directfb.bb``: Removed.

-  ``core-image-directfb.bb``: Removed.

-  ``pointercal``: No longer needed and has been moved to ``meta-oe``.

-  ``python-imaging``: No longer needed and moved to ``meta-python``

-  ``python-pyrex``: No longer needed and moved to ``meta-python``.

-  ``sato-icon-theme``: Became obsolete.

-  ``swabber-native``: Swabber has been removed. See the `entry on
   Swabber <#swabber-has-been-removed>`__.

-  ``tslib``: No longer needed and has been moved to ``meta-oe``.

-  ``uclibc``: Removed in favor of musl.

-  ``xtscal``: No longer needed and moved to ``meta-oe``

.. _migration-2.2-removed-classes:

Removed Classes
---------------

The following classes have been removed:

-  ``distutils-native-base``: No longer needed.

-  ``distutils3-native-base``: No longer needed.

-  ``sdl``: Only set :term:`DEPENDS` and
   :term:`SECTION`, which are better set within the
   recipe instead.

-  ``sip``: Mostly unused.

-  ``swabber``: See the `entry on
   Swabber <#swabber-has-been-removed>`__.

.. _migration-2.2-minor-packaging-changes:

Minor Packaging Changes
-----------------------

The following minor packaging changes have occurred:

-  ``grub``: Split ``grub-editenv`` into its own package.

-  ``systemd``: Split container and vm related units into a new package,
   systemd-container.

-  ``util-linux``: Moved ``prlimit`` to a separate
   ``util-linux-prlimit`` package.

.. _migration-2.2-miscellaneous-changes:

Miscellaneous Changes
---------------------

The following miscellaneous changes have occurred:

-  ``package_regex.inc``: Removed because the definitions
   ``package_regex.inc`` previously contained have been moved to their
   respective recipes.

-  Both ``devtool add`` and ``recipetool create`` now use a fixed
   :term:`SRCREV` by default when fetching from a Git
   repository. You can override this in either case to use
   ``${``\ :term:`AUTOREV`\ ``}`` instead by using the
   ``-a`` or ``DASHDASHautorev`` command-line option

-  ``distcc``: GTK+ UI is now disabled by default.

-  ``packagegroup-core-tools-testapps``: Removed Piglit.

-  ``image.bbclass``: Renamed COMPRESS(ION) to CONVERSION. This change
   means that ``COMPRESSIONTYPES``, ``COMPRESS_DEPENDS`` and
   ``COMPRESS_CMD`` are deprecated in favor of ``CONVERSIONTYPES``,
   ``CONVERSION_DEPENDS`` and ``CONVERSION_CMD``. The ``COMPRESS*``
   variable names will still work in the 2.2 release but metadata that
   does not need to be backwards-compatible should be changed to use the
   new names as the ``COMPRESS*`` ones will be removed in a future
   release.

-  ``gtk-doc``: A full version of ``gtk-doc`` is now made available.
   However, some old software might not be capable of using the current
   version of ``gtk-doc`` to build documentation. You need to change
   recipes that build such software so that they explicitly disable
   building documentation with ``gtk-doc``.


