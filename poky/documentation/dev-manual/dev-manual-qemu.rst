.. SPDX-License-Identifier: CC-BY-2.0-UK

*******************************
Using the Quick EMUlator (QEMU)
*******************************

The Yocto Project uses an implementation of the Quick EMUlator (QEMU)
Open Source project as part of the Yocto Project development "tool set".
This chapter provides both procedures that show you how to use the Quick
EMUlator (QEMU) and other QEMU information helpful for development
purposes.

.. _qemu-dev-overview:

Overview
========

Within the context of the Yocto Project, QEMU is an emulator and
virtualization machine that allows you to run a complete image you have
built using the Yocto Project as just another task on your build system.
QEMU is useful for running and testing images and applications on
supported Yocto Project architectures without having actual hardware.
Among other things, the Yocto Project uses QEMU to run automated Quality
Assurance (QA) tests on final images shipped with each release.

.. note::

   This implementation is not the same as QEMU in general.

This section provides a brief reference for the Yocto Project
implementation of QEMU.

For official information and documentation on QEMU in general, see the
following references:

-  `QEMU Website <http://wiki.qemu.org/Main_Page>`__\ *:* The official
   website for the QEMU Open Source project.

-  `Documentation <http://wiki.qemu.org/Manual>`__\ *:* The QEMU user
   manual.

.. _qemu-running-qemu:

Running QEMU
============

To use QEMU, you need to have QEMU installed and initialized as well as
have the proper artifacts (i.e. image files and root filesystems)
available. Follow these general steps to run QEMU:

1. *Install QEMU:* QEMU is made available with the Yocto Project a
   number of ways. One method is to install a Software Development Kit
   (SDK). See ":ref:`sdk-manual/sdk-intro:the qemu emulator`" section in the
   Yocto Project Application Development and the Extensible Software
   Development Kit (eSDK) manual for information on how to install QEMU.

2. *Setting Up the Environment:* How you set up the QEMU environment
   depends on how you installed QEMU:

   -  If you cloned the ``poky`` repository or you downloaded and
      unpacked a Yocto Project release tarball, you can source the build
      environment script (i.e. :ref:`structure-core-script`):
      ::

         $ cd ~/poky
         $ source oe-init-build-env

   -  If you installed a cross-toolchain, you can run the script that
      initializes the toolchain. For example, the following commands run
      the initialization script from the default ``poky_sdk`` directory:
      ::

         . ~/poky_sdk/environment-setup-core2-64-poky-linux

3. *Ensure the Artifacts are in Place:* You need to be sure you have a
   pre-built kernel that will boot in QEMU. You also need the target
   root filesystem for your target machine's architecture:

   -  If you have previously built an image for QEMU (e.g. ``qemux86``,
      ``qemuarm``, and so forth), then the artifacts are in place in
      your :term:`Build Directory`.

   -  If you have not built an image, you can go to the
      :yocto_dl:`machines/qemu </releases/yocto/yocto-3.1.2/machines/qemu/>` area and download a
      pre-built image that matches your architecture and can be run on
      QEMU.

   See the ":ref:`sdk-manual/sdk-appendix-obtain:extracting the root filesystem`"
   section in the Yocto Project Application Development and the
   Extensible Software Development Kit (eSDK) manual for information on
   how to extract a root filesystem.

4. *Run QEMU:* The basic ``runqemu`` command syntax is as follows:
   ::

      $ runqemu [option ] [...]

   Based on what you provide on the command
   line, ``runqemu`` does a good job of figuring out what you are trying
   to do. For example, by default, QEMU looks for the most recently
   built image according to the timestamp when it needs to look for an
   image. Minimally, through the use of options, you must provide either
   a machine name, a virtual machine image (``*wic.vmdk``), or a kernel
   image (``*.bin``).

   Here are some additional examples to help illustrate further QEMU:

   -  This example starts QEMU with MACHINE set to "qemux86-64".
      Assuming a standard
      :term:`Build Directory`, ``runqemu``
      automatically finds the ``bzImage-qemux86-64.bin`` image file and
      the ``core-image-minimal-qemux86-64-20200218002850.rootfs.ext4``
      (assuming the current build created a ``core-image-minimal``
      image).

      .. note::

         When more than one image with the same name exists, QEMU finds
         and uses the most recently built image according to the
         timestamp.

      ::

        $ runqemu qemux86-64

   -  This example produces the exact same results as the previous
      example. This command, however, specifically provides the image
      and root filesystem type.
      ::

         $ runqemu qemux86-64 core-image-minimal ext4

   -  This example specifies to boot an initial RAM disk image and to
      enable audio in QEMU. For this case, ``runqemu`` set the internal
      variable ``FSTYPE`` to "cpio.gz". Also, for audio to be enabled,
      an appropriate driver must be installed (see the previous
      description for the ``audio`` option for more information).
      ::

         $ runqemu qemux86-64 ramfs audio

   -  This example does not provide enough information for QEMU to
      launch. While the command does provide a root filesystem type, it
      must also minimally provide a MACHINE, KERNEL, or VM option.
      ::

         $ runqemu ext4

   -  This example specifies to boot a virtual machine image
      (``.wic.vmdk`` file). From the ``.wic.vmdk``, ``runqemu``
      determines the QEMU architecture (MACHINE) to be "qemux86-64" and
      the root filesystem type to be "vmdk".
      ::

         $ runqemu /home/scott-lenovo/vm/core-image-minimal-qemux86-64.wic.vmdk

Switching Between Consoles
==========================

When booting or running QEMU, you can switch between supported consoles
by using Ctrl+Alt+number. For example, Ctrl+Alt+3 switches you to the
serial console as long as that console is enabled. Being able to switch
consoles is helpful, for example, if the main QEMU console breaks for
some reason.

.. note::

   Usually, "2" gets you to the main console and "3" gets you to the
   serial console.

Removing the Splash Screen
==========================

You can remove the splash screen when QEMU is booting by using Alt+left.
Removing the splash screen allows you to see what is happening in the
background.

Disabling the Cursor Grab
=========================

The default QEMU integration captures the cursor within the main window.
It does this since standard mouse devices only provide relative input
and not absolute coordinates. You then have to break out of the grab
using the "Ctrl+Alt" key combination. However, the Yocto Project's
integration of QEMU enables the wacom USB touch pad driver by default to
allow input of absolute coordinates. This default means that the mouse
can enter and leave the main window without the grab taking effect
leading to a better user experience.

.. _qemu-running-under-a-network-file-system-nfs-server:

Running Under a Network File System (NFS) Server
================================================

One method for running QEMU is to run it on an NFS server. This is
useful when you need to access the same file system from both the build
and the emulated system at the same time. It is also worth noting that
the system does not need root privileges to run. It uses a user space
NFS server to avoid that. Follow these steps to set up for running QEMU
using an NFS server.

1. *Extract a Root Filesystem:* Once you are able to run QEMU in your
   environment, you can use the ``runqemu-extract-sdk`` script, which is
   located in the ``scripts`` directory along with the ``runqemu``
   script.

   The ``runqemu-extract-sdk`` takes a root filesystem tarball and
   extracts it into a location that you specify. Here is an example that
   takes a file system and extracts it to a directory named
   ``test-nfs``:
   ::

      runqemu-extract-sdk ./tmp/deploy/images/qemux86-64/core-image-sato-qemux86-64.tar.bz2 test-nfs

2. *Start QEMU:* Once you have extracted the file system, you can run
   ``runqemu`` normally with the additional location of the file system.
   You can then also make changes to the files within ``./test-nfs`` and
   see those changes appear in the image in real time. Here is an
   example using the ``qemux86`` image:
   ::

      runqemu qemux86-64 ./test-nfs

.. note::

   Should you need to start, stop, or restart the NFS share, you can use
   the following commands:

   -  The following command starts the NFS share: runqemu-export-rootfs
      start file-system-location

   -  The following command stops the NFS share: runqemu-export-rootfs
      stop file-system-location

   -  The following command restarts the NFS share:
      runqemu-export-rootfs restart file-system-location

.. _qemu-kvm-cpu-compatibility:

QEMU CPU Compatibility Under KVM
================================

By default, the QEMU build compiles for and targets 64-bit and x86 Intel
Core2 Duo processors and 32-bit x86 Intel Pentium II processors. QEMU
builds for and targets these CPU types because they display a broad
range of CPU feature compatibility with many commonly used CPUs.

Despite this broad range of compatibility, the CPUs could support a
feature that your host CPU does not support. Although this situation is
not a problem when QEMU uses software emulation of the feature, it can
be a problem when QEMU is running with KVM enabled. Specifically,
software compiled with a certain CPU feature crashes when run on a CPU
under KVM that does not support that feature. To work around this
problem, you can override QEMU's runtime CPU setting by changing the
``QB_CPU_KVM`` variable in ``qemuboot.conf`` in the
:term:`Build Directory` ``deploy/image``
directory. This setting specifies a ``-cpu`` option passed into QEMU in
the ``runqemu`` script. Running ``qemu -cpu help`` returns a list of
available supported CPU types.

.. _qemu-dev-performance:

QEMU Performance
================

Using QEMU to emulate your hardware can result in speed issues depending
on the target and host architecture mix. For example, using the
``qemux86`` image in the emulator on an Intel-based 32-bit (x86) host
machine is fast because the target and host architectures match. On the
other hand, using the ``qemuarm`` image on the same Intel-based host can
be slower. But, you still achieve faithful emulation of ARM-specific
issues.

To speed things up, the QEMU images support using ``distcc`` to call a
cross-compiler outside the emulated system. If you used ``runqemu`` to
start QEMU, and the ``distccd`` application is present on the host
system, any BitBake cross-compiling toolchain available from the build
system is automatically used from within QEMU simply by calling
``distcc``. You can accomplish this by defining the cross-compiler
variable (e.g. ``export CC="distcc"``). Alternatively, if you are using
a suitable SDK image or the appropriate stand-alone toolchain is
present, the toolchain is also automatically used.

.. note::

   Several mechanisms exist that let you connect to the system running
   on the QEMU emulator:

   -  QEMU provides a framebuffer interface that makes standard consoles
      available.

   -  Generally, headless embedded devices have a serial port. If so,
      you can configure the operating system of the running image to use
      that port to run a console. The connection uses standard IP
      networking.

   -  SSH servers exist in some QEMU images. The ``core-image-sato``
      QEMU image has a Dropbear secure shell (SSH) server that runs with
      the root password disabled. The ``core-image-full-cmdline`` and
      ``core-image-lsb`` QEMU images have OpenSSH instead of Dropbear.
      Including these SSH servers allow you to use standard ``ssh`` and
      ``scp`` commands. The ``core-image-minimal`` QEMU image, however,
      contains no SSH server.

   -  You can use a provided, user-space NFS server to boot the QEMU
      session using a local copy of the root filesystem on the host. In
      order to make this connection, you must extract a root filesystem
      tarball by using the ``runqemu-extract-sdk`` command. After
      running the command, you must then point the ``runqemu`` script to
      the extracted directory instead of a root filesystem image file.
      See the "`Running Under a Network File System (NFS)
      Server <#qemu-running-under-a-network-file-system-nfs-server>`__"
      section for more information.

.. _qemu-dev-command-line-syntax:

QEMU Command-Line Syntax
========================

The basic ``runqemu`` command syntax is as follows:
::

   $ runqemu [option ] [...]

Based on what you provide on the command line, ``runqemu`` does a
good job of figuring out what you are trying to do. For example, by
default, QEMU looks for the most recently built image according to the
timestamp when it needs to look for an image. Minimally, through the use
of options, you must provide either a machine name, a virtual machine
image (``*wic.vmdk``), or a kernel image (``*.bin``).

Following is the command-line help output for the ``runqemu`` command:
::

   $ runqemu --help

   Usage: you can run this script with any valid combination
   of the following environment variables (in any order):
     KERNEL - the kernel image file to use
     ROOTFS - the rootfs image file or nfsroot directory to use
     MACHINE - the machine name (optional, autodetected from KERNEL filename if unspecified)
     Simplified QEMU command-line options can be passed with:
       nographic - disable video console
       serial - enable a serial console on /dev/ttyS0
       slirp - enable user networking, no root privileges is required
       kvm - enable KVM when running x86/x86_64 (VT-capable CPU required)
       kvm-vhost - enable KVM with vhost when running x86/x86_64 (VT-capable CPU required)
       publicvnc - enable a VNC server open to all hosts
       audio - enable audio
       [*/]ovmf* - OVMF firmware file or base name for booting with UEFI
     tcpserial=<port> - specify tcp serial port number
     biosdir=<dir> - specify custom bios dir
     biosfilename=<filename> - specify bios filename
     qemuparams=<xyz> - specify custom parameters to QEMU
     bootparams=<xyz> - specify custom kernel parameters during boot
     help, -h, --help: print this text

   Examples:
     runqemu
     runqemu qemuarm
     runqemu tmp/deploy/images/qemuarm
     runqemu tmp/deploy/images/qemux86/<qemuboot.conf>
     runqemu qemux86-64 core-image-sato ext4
     runqemu qemux86-64 wic-image-minimal wic
     runqemu path/to/bzImage-qemux86.bin path/to/nfsrootdir/ serial
     runqemu qemux86 iso/hddimg/wic.vmdk/wic.qcow2/wic.vdi/ramfs/cpio.gz...
     runqemu qemux86 qemuparams="-m 256"
     runqemu qemux86 bootparams="psplash=false"
     runqemu path/to/<image>-<machine>.wic
     runqemu path/to/<image>-<machine>.wic.vmdk

.. _qemu-dev-runqemu-command-line-options:

``runqemu`` Command-Line Options
================================

Following is a description of ``runqemu`` options you can provide on the
command line:

.. note::

   If you do provide some "illegal" option combination or perhaps you do
   not provide enough in the way of options,
   runqemu
   provides appropriate error messaging to help you correct the problem.

-  QEMUARCH: The QEMU machine architecture, which must be "qemuarm",
   "qemuarm64", "qemumips", "qemumips64", "qemuppc", "qemux86", or
   "qemux86-64".

-  ``VM``: The virtual machine image, which must be a ``.wic.vmdk``
   file. Use this option when you want to boot a ``.wic.vmdk`` image.
   The image filename you provide must contain one of the following
   strings: "qemux86-64", "qemux86", "qemuarm", "qemumips64",
   "qemumips", "qemuppc", or "qemush4".

-  ROOTFS: A root filesystem that has one of the following filetype
   extensions: "ext2", "ext3", "ext4", "jffs2", "nfs", or "btrfs". If
   the filename you provide for this option uses "nfs", it must provide
   an explicit root filesystem path.

-  KERNEL: A kernel image, which is a ``.bin`` file. When you provide a
   ``.bin`` file, ``runqemu`` detects it and assumes the file is a
   kernel image.

-  MACHINE: The architecture of the QEMU machine, which must be one of
   the following: "qemux86", "qemux86-64", "qemuarm", "qemuarm64",
   "qemumips", "qemumips64", or "qemuppc". The MACHINE and QEMUARCH
   options are basically identical. If you do not provide a MACHINE
   option, ``runqemu`` tries to determine it based on other options.

-  ``ramfs``: Indicates you are booting an initial RAM disk (initramfs)
   image, which means the ``FSTYPE`` is ``cpio.gz``.

-  ``iso``: Indicates you are booting an ISO image, which means the
   ``FSTYPE`` is ``.iso``.

-  ``nographic``: Disables the video console, which sets the console to
   "ttys0". This option is useful when you have logged into a server and
   you do not want to disable forwarding from the X Window System (X11)
   to your workstation or laptop.

-  ``serial``: Enables a serial console on ``/dev/ttyS0``.

-  ``biosdir``: Establishes a custom directory for BIOS, VGA BIOS and
   keymaps.

-  ``biosfilename``: Establishes a custom BIOS name.

-  ``qemuparams=\"xyz\"``: Specifies custom QEMU parameters. Use this
   option to pass options other than the simple "kvm" and "serial"
   options.

-  ``bootparams=\"xyz\"``: Specifies custom boot parameters for the
   kernel.

-  ``audio``: Enables audio in QEMU. The MACHINE option must be either
   "qemux86" or "qemux86-64" in order for audio to be enabled.
   Additionally, the ``snd_intel8x0`` or ``snd_ens1370`` driver must be
   installed in linux guest.

-  ``slirp``: Enables "slirp" networking, which is a different way of
   networking that does not need root access but also is not as easy to
   use or comprehensive as the default.

-  ``kvm``: Enables KVM when running "qemux86" or "qemux86-64" QEMU
   architectures. For KVM to work, all the following conditions must be
   met:

   -  Your MACHINE must be either qemux86" or "qemux86-64".

   -  Your build host has to have the KVM modules installed, which are
      ``/dev/kvm``.

   -  The build host ``/dev/kvm`` directory has to be both writable and
      readable.

-  ``kvm-vhost``: Enables KVM with VHOST support when running "qemux86"
   or "qemux86-64" QEMU architectures. For KVM with VHOST to work, the
   following conditions must be met:

   -  `kvm <#kvm-cond>`__ option conditions must be met.

   -  Your build host has to have virtio net device, which are
      ``/dev/vhost-net``.

   -  The build host ``/dev/vhost-net`` directory has to be either
      readable or writable and "slirp-enabled".

-  ``publicvnc``: Enables a VNC server open to all hosts.
