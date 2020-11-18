.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

********
Features
********

This chapter provides a reference of shipped machine and distro features
you can include as part of your image, a reference on image features you
can select, and a reference on feature backfilling.

Features provide a mechanism for working out which packages should be
included in the generated images. Distributions can select which
features they want to support through the ``DISTRO_FEATURES`` variable,
which is set or appended to in a distribution's configuration file such
as ``poky.conf``, ``poky-tiny.conf``, ``poky-lsb.conf`` and so forth.
Machine features are set in the ``MACHINE_FEATURES`` variable, which is
set in the machine configuration file and specifies the hardware
features for a given machine.

These two variables combine to work out which kernel modules, utilities,
and other packages to include. A given distribution can support a
selected subset of features so some machine features might not be
included if the distribution itself does not support them.

One method you can use to determine which recipes are checking to see if
a particular feature is contained or not is to ``grep`` through the
:term:`Metadata` for the feature. Here is an example that
discovers the recipes whose build is potentially changed based on a
given feature:
::

   $ cd poky
   $ git grep 'contains.*MACHINE_FEATURES.*feature'

.. _ref-features-machine:

Machine Features
================

The items below are features you can use with
:term:`MACHINE_FEATURES`. Features do not have a
one-to-one correspondence to packages, and they can go beyond simply
controlling the installation of a package or packages. Sometimes a
feature can influence how certain recipes are built. For example, a
feature might determine whether a particular configure option is
specified within the :ref:`ref-tasks-configure` task
for a particular recipe.

This feature list only represents features as shipped with the Yocto
Project metadata:

-  *acpi:* Hardware has ACPI (x86/x86_64 only)

-  *alsa:* Hardware has ALSA audio drivers

-  *apm:* Hardware uses APM (or APM emulation)

-  *bluetooth:* Hardware has integrated BT

-  *efi:* Support for booting through EFI

-  *ext2:* Hardware HDD or Microdrive

-  *keyboard:* Hardware has a keyboard

-  *pcbios:* Support for booting through BIOS

-  *pci:* Hardware has a PCI bus

-  *pcmcia:* Hardware has PCMCIA or CompactFlash sockets

-  *phone:* Mobile phone (voice) support

-  *qvga:* Machine has a QVGA (320x240) display

-  *rtc:* Machine has a Real-Time Clock

-  *screen:* Hardware has a screen

-  *serial:* Hardware has serial support (usually RS232)

-  *touchscreen:* Hardware has a touchscreen

-  *usbgadget:* Hardware is USB gadget device capable

-  *usbhost:* Hardware is USB Host capable

-  *vfat:* FAT file system support

-  *wifi:* Hardware has integrated WiFi

.. _ref-features-distro:

Distro Features
===============

The items below are features you can use with
:term:`DISTRO_FEATURES` to enable features across
your distribution. Features do not have a one-to-one correspondence to
packages, and they can go beyond simply controlling the installation of
a package or packages. In most cases, the presence or absence of a
feature translates to the appropriate option supplied to the configure
script during the :ref:`ref-tasks-configure` task for
the recipes that optionally support the feature.

Some distro features are also machine features. These select features
make sense to be controlled both at the machine and distribution
configuration level. See the
:term:`COMBINED_FEATURES` variable for more
information.

This list only represents features as shipped with the Yocto Project
metadata:

-  *alsa:* Include ALSA support (OSS compatibility kernel modules
   installed if available).

-  *api-documentation:* Enables generation of API documentation during
   recipe builds. The resulting documentation is added to SDK tarballs
   when the ``bitbake -c populate_sdk`` command is used. See the
   ":ref:`sdk-manual/sdk-appendix-customizing-standard:adding api documentation to the standard sdk`"
   section in the Yocto Project Application Development and the
   Extensible Software Development Kit (eSDK) manual.

-  *bluetooth:* Include bluetooth support (integrated BT only).

-  *cramfs:* Include CramFS support.

-  *directfb:* Include DirectFB support.

-  *ext2:* Include tools for supporting for devices with internal
   HDD/Microdrive for storing files (instead of Flash only devices).

-  *ipsec:* Include IPSec support.

-  *ipv6:* Include IPv6 support.

-  *keyboard:* Include keyboard support (e.g. keymaps will be loaded
   during boot).

-  *ldconfig:* Include support for ldconfig and ``ld.so.conf`` on the
   target.

-  *nfs:* Include NFS client support (for mounting NFS exports on
   device).

-  *opengl:* Include the Open Graphics Library, which is a
   cross-language, multi-platform application programming interface used
   for rendering two and three-dimensional graphics.

-  *pci:* Include PCI bus support.

-  *pcmcia:* Include PCMCIA/CompactFlash support.

-  *ppp:* Include PPP dialup support.

-  *ptest:* Enables building the package tests where supported by
   individual recipes. For more information on package tests, see the
   ":ref:`dev-manual/dev-manual-common-tasks:testing packages with ptest`" section
   in the Yocto Project Development Tasks Manual.

-  *smbfs:* Include SMB networks client support (for mounting
   Samba/Microsoft Windows shares on device).

-  *systemd:* Include support for this ``init`` manager, which is a full
   replacement of for ``init`` with parallel starting of services,
   reduced shell overhead, and other features. This ``init`` manager is
   used by many distributions.

-  *usbgadget:* Include USB Gadget Device support (for USB
   networking/serial/storage).

-  *usbhost:* Include USB Host support (allows to connect external
   keyboard, mouse, storage, network etc).

-  *usrmerge:* Merges the ``/bin``, ``/sbin``, ``/lib``, and ``/lib64``
   directories into their respective counterparts in the ``/usr``
   directory to provide better package and application compatibility.

-  *wayland:* Include the Wayland display server protocol and the
   library that supports it.

-  *wifi:* Include WiFi support (integrated only).

-  *x11:* Include the X server and libraries.

.. _ref-features-image:

Image Features
==============

The contents of images generated by the OpenEmbedded build system can be
controlled by the :term:`IMAGE_FEATURES` and
:term:`EXTRA_IMAGE_FEATURES` variables that
you typically configure in your image recipes. Through these variables,
you can add several different predefined packages such as development
utilities or packages with debug information needed to investigate
application problems or profile applications.

The following image features are available for all images:

-  *allow-empty-password:* Allows Dropbear and OpenSSH to accept root
   logins and logins from accounts having an empty password string.

-  *dbg-pkgs:* Installs debug symbol packages for all packages installed
   in a given image.

-  *debug-tweaks:* Makes an image suitable for development (e.g. allows
   root logins without passwords and enables post-installation logging).
   See the 'allow-empty-password', 'empty-root-password', and
   'post-install-logging' features in this list for additional
   information.

-  *dev-pkgs:* Installs development packages (headers and extra library
   links) for all packages installed in a given image.

-  *doc-pkgs:* Installs documentation packages for all packages
   installed in a given image.

-  *empty-root-password:* Sets the root password to an empty string,
   which allows logins with a blank password.

-  *package-management:* Installs package management tools and preserves
   the package manager database.

-  *post-install-logging:* Enables logging postinstall script runs to
   the ``/var/log/postinstall.log`` file on first boot of the image on
   the target system.

   .. note::

      To make the
      /var/log
      directory on the target persistent, use the
      VOLATILE_LOG_DIR
      variable by setting it to "no".

-  *ptest-pkgs:* Installs ptest packages for all ptest-enabled recipes.

-  *read-only-rootfs:* Creates an image whose root filesystem is
   read-only. See the
   ":ref:`dev-manual/dev-manual-common-tasks:creating a read-only root filesystem`"
   section in the Yocto Project Development Tasks Manual for more
   information.

-  *splash:* Enables showing a splash screen during boot. By default,
   this screen is provided by ``psplash``, which does allow
   customization. If you prefer to use an alternative splash screen
   package, you can do so by setting the ``SPLASH`` variable to a
   different package name (or names) within the image recipe or at the
   distro configuration level.

-  *staticdev-pkgs:* Installs static development packages, which are
   static libraries (i.e. ``*.a`` files), for all packages installed in
   a given image.

Some image features are available only when you inherit the
:ref:`core-image <ref-classes-core-image>` class. The current list of
these valid features is as follows:

-  *hwcodecs:* Installs hardware acceleration codecs.

-  *nfs-server:* Installs an NFS server.

-  *perf:* Installs profiling tools such as ``perf``, ``systemtap``, and
   ``LTTng``. For general information on user-space tools, see the
   :doc:`../sdk-manual/sdk-manual` manual.

-  *ssh-server-dropbear:* Installs the Dropbear minimal SSH server.

-  *ssh-server-openssh:* Installs the OpenSSH SSH server, which is more
   full-featured than Dropbear. Note that if both the OpenSSH SSH server
   and the Dropbear minimal SSH server are present in
   ``IMAGE_FEATURES``, then OpenSSH will take precedence and Dropbear
   will not be installed.

-  *tools-debug:* Installs debugging tools such as ``strace`` and
   ``gdb``. For information on GDB, see the
   ":ref:`platdev-gdb-remotedebug`" section
   in the Yocto Project Development Tasks Manual. For information on
   tracing and profiling, see the :doc:`../profile-manual/profile-manual`.

-  *tools-sdk:* Installs a full SDK that runs on the device.

-  *tools-testapps:* Installs device testing tools (e.g. touchscreen
   debugging).

-  *x11:* Installs the X server.

-  *x11-base:* Installs the X server with a minimal environment.

-  *x11-sato:* Installs the OpenedHand Sato environment.

.. _ref-features-backfill:

Feature Backfilling
===================

Sometimes it is necessary in the OpenEmbedded build system to extend
:term:`MACHINE_FEATURES` or
:term:`DISTRO_FEATURES` to control functionality
that was previously enabled and not able to be disabled. For these
cases, we need to add an additional feature item to appear in one of
these variables, but we do not want to force developers who have
existing values of the variables in their configuration to add the new
feature in order to retain the same overall level of functionality.
Thus, the OpenEmbedded build system has a mechanism to automatically
"backfill" these added features into existing distro or machine
configurations. You can see the list of features for which this is done
by finding the
:term:`DISTRO_FEATURES_BACKFILL` and
:term:`MACHINE_FEATURES_BACKFILL`
variables in the ``meta/conf/bitbake.conf`` file.

Because such features are backfilled by default into all configurations
as described in the previous paragraph, developers who wish to disable
the new features need to be able to selectively prevent the backfilling
from occurring. They can do this by adding the undesired feature or
features to the
:term:`DISTRO_FEATURES_BACKFILL_CONSIDERED`
or
:term:`MACHINE_FEATURES_BACKFILL_CONSIDERED`
variables for distro features and machine features respectively.

Here are two examples to help illustrate feature backfilling:

-  *The "pulseaudio" distro feature option*: Previously, PulseAudio
   support was enabled within the Qt and GStreamer frameworks. Because
   of this, the feature is backfilled and thus enabled for all distros
   through the ``DISTRO_FEATURES_BACKFILL`` variable in the
   ``meta/conf/bitbake.conf`` file. However, your distro needs to
   disable the feature. You can disable the feature without affecting
   other existing distro configurations that need PulseAudio support by
   adding "pulseaudio" to ``DISTRO_FEATURES_BACKFILL_CONSIDERED`` in
   your distro's ``.conf`` file. Adding the feature to this variable
   when it also exists in the ``DISTRO_FEATURES_BACKFILL`` variable
   prevents the build system from adding the feature to your
   configuration's ``DISTRO_FEATURES``, effectively disabling the
   feature for that particular distro.

-  *The "rtc" machine feature option*: Previously, real time clock (RTC)
   support was enabled for all target devices. Because of this, the
   feature is backfilled and thus enabled for all machines through the
   ``MACHINE_FEATURES_BACKFILL`` variable in the
   ``meta/conf/bitbake.conf`` file. However, your target device does not
   have this capability. You can disable RTC support for your device
   without affecting other machines that need RTC support by adding the
   feature to your machine's ``MACHINE_FEATURES_BACKFILL_CONSIDERED``
   list in the machine's ``.conf`` file. Adding the feature to this
   variable when it also exists in the ``MACHINE_FEATURES_BACKFILL``
   variable prevents the build system from adding the feature to your
   configuration's ``MACHINE_FEATURES``, effectively disabling RTC
   support for that particular machine.
