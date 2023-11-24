.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 4.3 (nanbield)
========================

Migration notes for 4.3 (nanbield)
------------------------------------

This section provides migration information for moving to the Yocto
Project 4.3 Release (codename "nanbield") from the prior release.

.. _migration-4.3-supported-kernel-versions:

Supported kernel versions
~~~~~~~~~~~~~~~~~~~~~~~~~

The :term:`OLDEST_KERNEL` setting has been changed to "5.15" in this release, meaning that
out the box, older kernels are not supported. There were two reasons for this.
Firstly it allows glibc optimisations that improve the performance of the system
by removing compatibility code and using modern kernel APIs exclusively. The second
issue was this allows 64 bit time support even on 32 bit platforms and resolves Y2038
issues.

It is still possible to override this value and build for older kernels, this is just
no longer the default supported configuration. This setting does not affect which
kernel versions SDKs will run against and does not affect which versions of the kernel
can be used to run builds.

.. _migration-4.3-layername-override:

Layername override implications
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Code can now know which layer a recipe is coming from through the newly added
:term:`FILE_LAYERNAME` variable and the ``layer-<layername> override``. This is being used
for enabling QA checks on a per layer basis. For existing code this has the
side effect that the QA checks will apply to recipes being bbappended
from other layers - for example, patches added through such bbappends will now
need to have the "Upstream-Status" specified in the patch header.

.. _migration-4.3-compiling-changes:

Compiling changes
~~~~~~~~~~~~~~~~~

-  Code on 32 bit platforms is now compiled with largefile support and 64
   bit ``time_t``, to avoid the Y2038 time overflow issue. This breaks the ABI
   and could break existing programs in untested layers.

.. _migration-4.3-supported-distributions:

Supported distributions
~~~~~~~~~~~~~~~~~~~~~~~

This release supports running BitBake on new GNU/Linux distributions:

-  Ubuntu 22.10
-  Fedora 38
-  Debian 12
-  CentOS Stream 8
-  AlmaLinux 8.8
-  AlmaLinux 9.2

On the other hand, some earlier distributions are no longer supported:

-  Fedora 36
-  AlmaLinux 8.7
-  AlmaLinux 9.1

See :ref:`all supported distributions <system-requirements-supported-distros>`.

.. _migration-4.3-removed-machines:

edgerouter machine removed
~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``edgerouter`` reference BSP for the MIPS architecture in ``meta-yocto-bsp``
has been removed as the hardware has been unavailable for some time. There is no
suitable reference MIPS hardware to replace it with, but the MIPS architecture
will continue to get coverage via QEMU build/boot testing.

.. _migration-4.3-go-changes:

Go language changes
~~~~~~~~~~~~~~~~~~~

-  Support for the Glide package manager has been removed, as ``go mod``
   has become the standard.

.. _migration-4.3-systemd-changes:

systemd changes
~~~~~~~~~~~~~~~

Upstream systemd is now more strict on filesystem layout and the ``usrmerge``
feature is therefore required alongside systemd. The Poky test configurations
have been updated accordingly for systemd.

.. _migration-4.3-recipe-changes:

Recipe changes
~~~~~~~~~~~~~~

-  Runtime testing of ptest now fails if no test results are returned by
   any given ptest.

.. _migration-4.3-deprecated-variables:

Deprecated variables
~~~~~~~~~~~~~~~~~~~~

The following variables have been deprecated:

-  :term:`CVE_CHECK_IGNORE`: use :term:`CVE_STATUS` instead.

.. _migration-4.3-removed-variables:

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:

-  ``AUTHOR``
-  ``PERLARCH``
-  ``PERLVERSION``
-  ``QEMU_USE_SLIRP`` - add ``slirp`` to ``TEST_RUNQEMUPARAMS`` instead.
-  ``SERIAL_CONSOLES_CHECK`` - no longer necessary because all
   consoles listed in :term:`SERIAL_CONSOLES` are checked for their existence
   before a ``getty`` is started.

.. _migration-4.3-removed-recipes:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

-  ``apmd``: obsolete (``apm`` in :term:`MACHINE_FEATURES` also removed).
-  ``cve-update-db-native``: functionally replaced by ``cve-update-nvd2-native``
-  ``gcr3``: no longer needed by core recipes, moved to meta-gnome (gcr, i.e. version 4.x, is still provided).
-  ``glide``: as explained in :ref:`migration-4.3-go-changes`.
-  ``libdmx``: obsolete
-  ``linux-yocto`` version 5.15 (versions 6.1 and 6.5 provided instead).
-  ``python3-async``: obsolete - no longer needed by ``python3-gitdb`` or any other core recipe
-  ``rust-hello-world``: there are sufficient other Rust recipes and test cases such that this is no longer needed.


.. _migration-4.3-removed-classes:

Removed classes
~~~~~~~~~~~~~~~

The following classes have been removed in this release:

-  ``glide``: as explained in :ref:`migration-4.3-go-changes`.


Output file naming changes
~~~~~~~~~~~~~~~~~~~~~~~~~~

In 4.3 there are some minor differences in image and SDK output file names.
If you rely on the existing naming (e.g. in external scripts) you may need to
either modify configuration or adapt to the new naming. Further details:

-  :term:`IMAGE_NAME` and :term:`IMAGE_LINK_NAME` now include the
   :term:`IMAGE_NAME_SUFFIX` value directly. In practical terms, this means
   that ``.rootfs`` will now appear in image output file names. If you do not
   wish to have the ``.rootfs`` suffix used, you can just set
   :term:`IMAGE_NAME_SUFFIX` to "" and this will now be consistently respected
   in both the image file and image file symlink names. As part of this change,
   support for the ``imgsuffix`` task varflag has been dropped (mostly
   an internal implementation detail, but if you were implementing a custom
   image construction with a task in a similar manner to ``do_bootimg``
   you may have been using this).

-  :term:`SDK_NAME` now includes the values of :term:`IMAGE_BASENAME` and
   :term:`MACHINE` so that they are unique when building SDKs for different
   images and machines.



.. _migration-4.3-pr-pe:

Versioning changes
~~~~~~~~~~~~~~~~~~

-  :term:`PR` values have been removed from all core recipes - distro maintainers
   who make use of :term:`PR` values would need to curate these already so the
   sparsely set base values would not be that useful anymore. If you have been
   relying on these (i.e. you are maintaining a binary package feed where package
   versions should only ever increase), double-check the output (perhaps with the
   help of the :ref:`ref-classes-buildhistory` class) to ensure that package
   versions are consistent.

-  The :term:`PR` value can no longer be set from the recipe file name - this
   was rarely used, but in any case is no longer supported.

-  :term:`PE` and :term:`PR` are no longer included in the work directory path
   (:term:`WORKDIR`). This may break some tool assumptions about directory paths,
   but those should really be querying paths from the build system (or not poking
   into :term:`WORKDIR` externally).

-  Source revision information has been moved from :term:`PV` to :term:`PKGV`.
   The user visible effect of this change is that :term:`PV` will no longer have
   revision information in it and this will now be appended to the :term:`PV`
   value through :term:`PKGV` when the packages are written out (as long as "+"
   is present in the :term:`PKGV` value). Since :term:`PV` is used in
   :term:`STAMP` and :term:`WORKDIR`, you may notice small directory naming and
   stamp naming changes.

-  The :term:`SRCPV` variable is no longer needed in :term:`PV`, but since
   the default :term:`SRCPV` value is now "", using it is effectively now just a
   null operation - you can remove it (leaving behind the "+") , but it is not
   yet required to do so.


.. _migration-4.3-qemu-changes:

QEMU changes
~~~~~~~~~~~~

-  The ``runqemu`` script no longer systematically adds two serial ports
   (``--serial null`` and ``-serial mon:stdio``) to the QEMU emulated machine
   if the user already adds such ports through the ``QB_OPT_APPEND`` setting.

   If the user adds one port, only ``--serial null`` is added, and
   ``-serial mon:stdio`` is no longer passed. If the user adds more than one
   port, ``--serial null`` is no longer added either. This can break some
   existing QEMU based configurations expecting such serial ports to be added
   when ``runqemu`` is executed.

   This change was made to avoid exceeding two serial ports, which interferes
   with automated testing.

-  ``runqemu`` now uses the ``ip tuntap`` command instead of ``tunctl``, and
   thus ``tunctl`` is no longer built by the ``qemu-helper-native`` recipe; if
   for some reason you were calling ``tunctl`` directly from your own scripts
   you should switch to calling ``ip tuntap`` instead.

.. _migration-4.3-misc-changes:

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

-  The ``-crosssdk`` suffix and any :term:`MLPREFIX` were removed from
   ``virtual/XXX`` provider/dependencies where a ``PREFIX`` was used as well,
   as we don't need both and it made automated dependency rewriting
   unnecessarily complex. In general this only affects internal toolchain
   dependencies so isn't end user visible, but if for some reason you have
   custom classes or recipes that rely upon the old providers then you will
   need to update those.

