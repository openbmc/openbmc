.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

********
Features
********

This chapter provides a reference of shipped machine and distro features
you can include as part of your image, a reference on image features you
can select, and a reference on :ref:`ref-features-backfill`.

Features provide a mechanism for working out which packages should be
included in the generated images. Distributions can select which
features they want to support through the :term:`DISTRO_FEATURES` variable,
which is set or appended to in a distribution's configuration file such
as ``poky.conf``, ``poky-tiny.conf``, ``poky-lsb.conf`` and so forth.
Machine features are set in the :term:`MACHINE_FEATURES` variable, which is
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
given feature::

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

-  *bluetooth:* Hardware has integrated BT

-  *efi:* Support for booting through EFI

-  *ext2:* Hardware HDD or Microdrive

-  *keyboard:* Hardware has a keyboard

-  *numa:* Hardware has non-uniform memory access

-  *pcbios:* Support for booting through BIOS

-  *pci:* Hardware has a PCI bus

-  *pcmcia:* Hardware has PCMCIA or CompactFlash sockets

-  *phone:* Mobile phone (voice) support

-  *qemu-usermode:* QEMU can support user-mode emulation for this machine

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
the recipes that optionally support the feature. Appropriate options
must be supplied, and enabling/disabling :term:`PACKAGECONFIG` for the
concerned packages is one way of supplying such options.

Some distro features are also machine features. These select features
make sense to be controlled both at the machine and distribution
configuration level. See the
:term:`COMBINED_FEATURES` variable for more
information.

.. note::

   :term:`DISTRO_FEATURES` is normally independent of kernel configuration,
   so if a feature specified in :term:`DISTRO_FEATURES` also relies on
   support in the kernel, you will also need to ensure that support is
   enabled in the kernel configuration.

This list only represents features as shipped with the Yocto Project
metadata, as extra layers can define their own:

-  *3g:* Include support for cellular data.

-  *acl:* Include :wikipedia:`Access Control List <Access-control_list>` support.

-  *alsa:* Include :wikipedia:`Advanced Linux Sound Architecture <Advanced_Linux_Sound_Architecture>`
   support (OSS compatibility kernel modules installed if available).

-  *api-documentation:* Enables generation of API documentation during
   recipe builds. The resulting documentation is added to SDK tarballs
   when the ``bitbake -c populate_sdk`` command is used. See the
   ":ref:`sdk-manual/appendix-customizing-standard:adding api documentation to the standard sdk`"
   section in the Yocto Project Application Development and the
   Extensible Software Development Kit (eSDK) manual.

-  *bluetooth:* Include bluetooth support (integrated BT only).

-  *cramfs:* Include CramFS support.

-  *debuginfod:* Include support for getting ELF debugging information through
   a :ref:`debuginfod <dev-manual/debugging:using the debuginfod server method>`
   server.

-  *directfb:* Include DirectFB support.

-  *ext2:* Include tools for supporting for devices with internal
   HDD/Microdrive for storing files (instead of Flash only devices).

-  *gobject-introspection-data:* Include data to support
   `GObject Introspection <https://gi.readthedocs.io/en/latest/>`__.

-  *ipsec:* Include IPSec support.

-  *ipv4:* Include IPv4 support.

-  *ipv6:* Include IPv6 support.

-  *keyboard:* Include keyboard support (e.g. keymaps will be loaded
   during boot).

-  *minidebuginfo:* Add minimal debug symbols :ref:`(minidebuginfo)<dev-manual/debugging:enabling minidebuginfo>`
   to binary files containing, allowing ``coredumpctl`` and ``gdb`` to show symbolicated stack traces.

-  *multiarch:* Enable building applications with multiple architecture
   support.

-  *ld-is-gold:* Use the :wikipedia:`gold <Gold_(linker)>`
   linker instead of the standard GCC linker (bfd).

-  *ldconfig:* Include support for ldconfig and ``ld.so.conf`` on the
   target.

-  *lto:* Enable `Link-Time Optimisation <https://gcc.gnu.org/wiki/LinkTimeOptimization>`__.

-  *nfc:* Include support for
   `Near Field Communication <https://en.wikipedia.org/wiki/Near-field_communication>`__.

-  *nfs:* Include NFS client support (for mounting NFS exports on
   device).

-  *nls:* Include National Language Support (NLS).

-  *opengl:* Include the Open Graphics Library, which is a
   cross-language, multi-platform application programming interface used
   for rendering two and three-dimensional graphics.

-  *overlayfs:* Include `OverlayFS <https://docs.kernel.org/filesystems/overlayfs.html>`__
   support.

-  *pam:* Include :wikipedia:`Pluggable Authentication Module (PAM) <Pluggable_authentication_module>`
   support.

-  *pci:* Include PCI bus support.

-  *pcmcia:* Include PCMCIA/CompactFlash support.

-  *polkit:* Include :wikipedia:`Polkit <Polkit>` support.

-  *ppp:* Include PPP dialup support.

-  *ptest:* Enables building the package tests where supported by
   individual recipes. For more information on package tests, see the
   ":ref:`dev-manual/packages:testing packages with ptest`" section
   in the Yocto Project Development Tasks Manual.

-  *pulseaudio:* Include support for
   `PulseAudio <https://www.freedesktop.org/wiki/Software/PulseAudio/>`__.

-  *selinux:* Include support for
   :wikipedia:`Security-Enhanced Linux (SELinux) <Security-Enhanced_Linux>`
   (requires `meta-selinux <https://layers.openembedded.org/layerindex/layer/meta-selinux/>`__).

-  *seccomp:* Enables building applications with
   :wikipedia:`seccomp <Seccomp>` support, to
   allow them to strictly restrict the system calls that they are allowed
   to invoke.

-  *smbfs:* Include SMB networks client support (for mounting
   Samba/Microsoft Windows shares on device).

-  *systemd:* Include support for this ``init`` manager, which is a full
   replacement of for ``init`` with parallel starting of services,
   reduced shell overhead, and other features. This ``init`` manager is
   used by many distributions.

-  *systemd-resolved:* Include support and use ``systemd-resolved`` as the
   main DNS name resolver in ``glibc`` Name Service Switch. This is a DNS
   resolver daemon from ``systemd``.

-  *usbgadget:* Include USB Gadget Device support (for USB
   networking/serial/storage).

-  *usbhost:* Include USB Host support (allows to connect external
   keyboard, mouse, storage, network etc).

-  *usrmerge:* Merges the ``/bin``, ``/sbin``, ``/lib``, and ``/lib64``
   directories into their respective counterparts in the ``/usr``
   directory to provide better package and application compatibility.

-  *vfat:* Include :wikipedia:`FAT filesystem <File_Allocation_Table>`
   support.

-  *vulkan:* Include support for the :wikipedia:`Vulkan API <Vulkan>`.

-  *wayland:* Include the Wayland display server protocol and the
   library that supports it.

-  *wifi:* Include WiFi support (integrated only).

-  *x11:* Include the X server and libraries.

-  *xattr:* Include support for
   :wikipedia:`extended file attributes <Extended_file_attributes>`.

-  *zeroconf:* Include support for
   `zero configuration networking <https://en.wikipedia.org/wiki/Zero-configuration_networking>`__.

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

The image features available for all images are:

-  *allow-empty-password:* Allows Dropbear and OpenSSH to accept
   logins from accounts having an empty password string.

-  *allow-root-login:* Allows Dropbear and OpenSSH to accept root logins.

-  *dbg-pkgs:* Installs debug symbol packages for all packages installed
   in a given image.

-  *debug-tweaks:* Makes an image suitable for development (e.g. allows
   root logins, logins without passwords ---including root ones, and enables
   post-installation logging). See the ``allow-empty-password``,
   ``allow-root-login``, ``empty-root-password``, and ``post-install-logging``
   features in this list for additional information.

-  *dev-pkgs:* Installs development packages (headers and extra library
   links) for all packages installed in a given image.

-  *doc-pkgs:* Installs documentation packages for all packages
   installed in a given image.

-  *empty-root-password:* This feature or ``debug-tweaks`` is required if
   you want to allow root login with an empty password. If these features
   are not present in :term:`IMAGE_FEATURES`, a non-empty password is
   forced in ``/etc/passwd`` and ``/etc/shadow`` if such files exist.

   .. note::
       ``empty-root-password`` doesn't set an empty root password by itself.
       You get an initial empty root password thanks to the
       :oe_git:`base-passwd </openembedded-core/tree/meta/recipes-core/base-passwd/>`
       and :oe_git:`shadow </openembedded-core/tree/meta/recipes-extended/shadow/>`
       recipes, and the presence of ``empty-root-password`` or ``debug-tweaks``
       just disables the mechanism which forces an non-empty password for the
       root user.

-  *lic-pkgs:* Installs license packages for all packages installed in a
   given image.

-  *overlayfs-etc:* Configures the ``/etc`` directory to be in ``overlayfs``.
   This allows to store device specific information elsewhere, especially
   if the root filesystem is configured to be read-only.

-  *package-management:* Installs package management tools and preserves
   the package manager database.

-  *post-install-logging:* Enables logging postinstall script runs to
   the ``/var/log/postinstall.log`` file on first boot of the image on
   the target system.

   .. note::

      To make the ``/var/log`` directory on the target persistent, use the
      :term:`VOLATILE_LOG_DIR` variable by setting it to "no".

-  *ptest-pkgs:* Installs ptest packages for all ptest-enabled recipes.

-  *read-only-rootfs:* Creates an image whose root filesystem is
   read-only. See the
   ":ref:`dev-manual/read-only-rootfs:creating a read-only root filesystem`"
   section in the Yocto Project Development Tasks Manual for more
   information.

-  *read-only-rootfs-delayed-postinsts:* when specified in conjunction
   with ``read-only-rootfs``, specifies that post-install scripts are
   still permitted (this assumes that the root filesystem will be made
   writeable for the first boot; this feature does not do anything to
   ensure that - it just disables the check for post-install scripts.)

-  *serial-autologin-root:* when specified in conjunction with
   ``empty-root-password`` will automatically login as root on the
   serial console. This of course opens up a security hole if the
   serial console is potentially accessible to an attacker, so use
   with caution.

-  *splash:* Enables showing a splash screen during boot. By default,
   this screen is provided by ``psplash``, which does allow
   customization. If you prefer to use an alternative splash screen
   package, you can do so by setting the :term:`SPLASH` variable to a
   different package name (or names) within the image recipe or at the
   distro configuration level.

-  *stateless-rootfs:*: specifies that the image should be created as
   stateless - when using ``systemd``, ``systemctl-native`` will not
   be run on the image, leaving the image for population at runtime by
   systemd.

-  *staticdev-pkgs:* Installs static development packages, which are
   static libraries (i.e. ``*.a`` files), for all packages installed in
   a given image.

Some image features are available only when you inherit the
:ref:`ref-classes-core-image` class. The current list of
these valid features is as follows:

-  *hwcodecs:* Installs hardware acceleration codecs.

-  *nfs-server:* Installs an NFS server.

-  *perf:* Installs profiling tools such as ``perf``, ``systemtap``, and
   ``LTTng``. For general information on user-space tools, see the
   :doc:`/sdk-manual/index` manual.

-  *ssh-server-dropbear:* Installs the Dropbear minimal SSH server.

   .. note::

      As of the 4.1 release, the ``ssh-server-dropbear`` feature also
      recommends the ``openssh-sftp-server`` package, which by default
      will be pulled into the image. This is because recent versions of
      the OpenSSH ``scp`` client now use the SFTP protocol, and thus
      require an SFTP server to be present to connect to. However, if
      you wish to use the Dropbear ssh server `without` the SFTP server
      installed, you can either remove ``ssh-server-dropbear`` from
      ``IMAGE_FEATURES`` and add ``dropbear`` to :term:`IMAGE_INSTALL`
      instead, or alternatively still use the feature but set
      :term:`BAD_RECOMMENDATIONS` as follows::

         BAD_RECOMMENDATIONS += "openssh-sftp-server"

-  *ssh-server-openssh:* Installs the OpenSSH SSH server, which is more
   full-featured than Dropbear. Note that if both the OpenSSH SSH server
   and the Dropbear minimal SSH server are present in
   :term:`IMAGE_FEATURES`, then OpenSSH will take precedence and Dropbear
   will not be installed.

-  *tools-debug:* Installs debugging tools such as ``strace`` and
   ``gdb``. For information on GDB, see the
   ":ref:`dev-manual/debugging:debugging with the gnu project debugger (gdb) remotely`" section
   in the Yocto Project Development Tasks Manual. For information on
   tracing and profiling, see the :doc:`/profile-manual/index`.

-  *tools-sdk:* Installs a full SDK that runs on the device.

-  *tools-testapps:* Installs device testing tools (e.g. touchscreen
   debugging).

-  *weston:* Installs Weston (reference Wayland environment).

-  *x11:* Installs the X server.

-  *x11-base:* Installs the X server with a minimal environment.

-  *x11-sato:* Installs the OpenedHand Sato environment.

.. _ref-features-backfill:

Feature Backfilling
===================

Sometimes it is necessary in the OpenEmbedded build system to
add new functionality to :term:`MACHINE_FEATURES` or
:term:`DISTRO_FEATURES`, but at the same time, allow existing
distributions or machine definitions to opt out of such new
features, to retain the same overall level of functionality.

To make this possible, the OpenEmbedded build system has a mechanism to
automatically "backfill" features into existing distro or machine
configurations. You can see the list of features for which this is done
by checking the :term:`DISTRO_FEATURES_BACKFILL` and
:term:`MACHINE_FEATURES_BACKFILL` variables in the
``meta/conf/bitbake.conf`` file.

These two variables are paired with the
:term:`DISTRO_FEATURES_BACKFILL_CONSIDERED`
and :term:`MACHINE_FEATURES_BACKFILL_CONSIDERED` variables
which allow distro or machine configuration maintainers to `consider` any
added feature, and decide when they wish to keep or exclude such feature,
thus preventing the backfilling from happening.

Here are two examples to illustrate feature backfilling:

-  *The "pulseaudio" distro feature option*: Previously, PulseAudio support was
   enabled within the Qt and GStreamer frameworks. Because of this, the feature
   is now backfilled and thus enabled for all distros through the
   :term:`DISTRO_FEATURES_BACKFILL` variable in the ``meta/conf/bitbake.conf``
   file. However, if your distro needs to disable the feature, you can do so
   without affecting other existing distro configurations that need PulseAudio
   support. You do this by adding "pulseaudio" to
   :term:`DISTRO_FEATURES_BACKFILL_CONSIDERED` in your distro's ``.conf``
   file. So, adding the feature to this variable when it also exists in the
   :term:`DISTRO_FEATURES_BACKFILL` variable prevents the build system from
   adding the feature to your configuration's :term:`DISTRO_FEATURES`,
   effectively disabling the feature for that particular distro.

-  *The "rtc" machine feature option*: Previously, real time clock (RTC)
   support was enabled for all target devices. Because of this, the
   feature is backfilled and thus enabled for all machines through the
   :term:`MACHINE_FEATURES_BACKFILL` variable in the ``meta/conf/bitbake.conf``
   file. However, if your target device does not have this capability, you can
   disable RTC support for your device without affecting other machines
   that need RTC support. You do this by adding the "rtc" feature to the
   :term:`MACHINE_FEATURES_BACKFILL_CONSIDERED` list in your machine's ``.conf``
   file. So, adding the feature to this variable when it also exists in the
   :term:`MACHINE_FEATURES_BACKFILL` variable prevents the build system from
   adding the feature to your configuration's :term:`MACHINE_FEATURES`,
   effectively disabling RTC support for that particular machine.
