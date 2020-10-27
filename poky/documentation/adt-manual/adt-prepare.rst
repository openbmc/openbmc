.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*************************************
Preparing for Application Development
*************************************

In order to develop applications, you need set up your host development
system. Several ways exist that allow you to install cross-development
tools, QEMU, the Eclipse Yocto Plug-in, and other tools. This chapter
describes how to prepare for application development.

.. _installing-the-adt:

Installing the ADT and Toolchains
=================================

The following list describes installation methods that set up varying
degrees of tool availability on your system. Regardless of the
installation method you choose, you must ``source`` the cross-toolchain
environment setup script, which establishes several key environment
variables, before you use a toolchain. See the "`Setting Up the
Cross-Development
Environment <#setting-up-the-cross-development-environment>`__" section
for more information.

.. note::

   Avoid mixing installation methods when installing toolchains for
   different architectures. For example, avoid using the ADT Installer
   to install some toolchains and then hand-installing cross-development
   toolchains by running the toolchain installer for different
   architectures. Mixing installation methods can result in situations
   where the ADT Installer becomes unreliable and might not install the
   toolchain.

   If you must mix installation methods, you might avoid problems by
   deleting ``/var/lib/opkg``, thus purging the ``opkg`` package
   metadata.

-  *Use the ADT installer script:* This method is the recommended way to
   install the ADT because it automates much of the process for you. For
   example, you can configure the installation to install the QEMU
   emulator and the user-space NFS, specify which root filesystem
   profiles to download, and define the target sysroot location.

-  *Use an existing toolchain:* Using this method, you select and
   download an architecture-specific toolchain installer and then run
   the script to hand-install the toolchain. If you use this method, you
   just get the cross-toolchain and QEMU - you do not get any of the
   other mentioned benefits had you run the ADT Installer script.

-  *Use the toolchain from within the Build Directory:* If you already
   have a :term:`Build Directory`,
   you can build the cross-toolchain within the directory. However, like
   the previous method mentioned, you only get the cross-toolchain and
   QEMU - you do not get any of the other benefits without taking
   separate steps.

Using the ADT Installer
-----------------------

To run the ADT Installer, you need to get the ADT Installer tarball, be
sure you have the necessary host development packages that support the
ADT Installer, and then run the ADT Installer Script.

For a list of the host packages needed to support ADT installation and
use, see the "ADT Installer Extras" lists in the "`Required Packages for
the Host Development
System <&YOCTO_DOCS_REF_URL;#required-packages-for-the-host-development-system>`__"
section of the Yocto Project Reference Manual.

Getting the ADT Installer Tarball
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ADT Installer is contained in the ADT Installer tarball. You can get
the tarball using either of these methods:

-  *Download the Tarball:* You can download the tarball from
   ` <&YOCTO_ADTINSTALLER_DL_URL;>`__ into any directory.

-  *Build the Tarball:* You can use
   :term:`BitBake` to generate the
   tarball inside an existing :term:`Build Directory`.

   If you use BitBake to generate the ADT Installer tarball, you must
   ``source`` the environment setup script
   (````` <&YOCTO_DOCS_REF_URL;#structure-core-script>`__ or
   ```oe-init-build-env-memres`` <&YOCTO_DOCS_REF_URL;#structure-memres-core-script>`__)
   located in the Source Directory before running the ``bitbake``
   command that creates the tarball.

   The following example commands establish the
   :term:`Source Directory`, check out the
   current release branch, set up the build environment while also
   creating the default Build Directory, and run the ``bitbake`` command
   that results in the tarball
   ``poky/build/tmp/deploy/sdk/adt_installer.tar.bz2``:

   .. note::

      Before using BitBake to build the ADT tarball, be sure to make
      sure your
      local.conf
      file is properly configured. See the "
      User Configuration
      " section in the Yocto Project Reference Manual for general
      configuration information.

   $ cd ~ $ git clone git://git.yoctoproject.org/poky $ cd poky $ git
   checkout -b DISTRO_NAME origin/DISTRO_NAME $ source oe-init-build-env $
   bitbake adt-installer

Configuring and Running the ADT Installer Script
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Before running the ADT Installer script, you need to unpack the tarball.
You can unpack the tarball in any directory you wish. For example, this
command copies the ADT Installer tarball from where it was built into
the home directory and then unpacks the tarball into a top-level
directory named ``adt-installer``: $ cd ~ $ cp
poky/build/tmp/deploy/sdk/adt_installer.tar.bz2 $HOME $ tar -xjf
adt_installer.tar.bz2 Unpacking it creates the directory
``adt-installer``, which contains the ADT Installer script
(``adt_installer``) and its configuration file (``adt_installer.conf``).

Before you run the script, however, you should examine the ADT Installer
configuration file and be sure you are going to get what you want. Your
configurations determine which kernel and filesystem image are
downloaded.

The following list describes the configurations you can define for the
ADT Installer. For configuration values and restrictions, see the
comments in the ``adt-installer.conf`` file:

-  ``YOCTOADT_REPO``: This area includes the IPKG-based packages and the
   root filesystem upon which the installation is based. If you want to
   set up your own IPKG repository pointed to by ``YOCTOADT_REPO``, you
   need to be sure that the directory structure follows the same layout
   as the reference directory set up at
   http://adtrepo.yoctoproject.org. Also, your repository needs
   to be accessible through HTTP.

-  ``YOCTOADT_TARGETS``: The machine target architectures for which you
   want to set up cross-development environments.

-  ``YOCTOADT_QEMU``: Indicates whether or not to install the emulator
   QEMU.

-  ``YOCTOADT_NFS_UTIL``: Indicates whether or not to install user-mode
   NFS. If you plan to use the Eclipse IDE Yocto plug-in against QEMU,
   you should install NFS.

   .. note::

      To boot QEMU images using our userspace NFS server, you need to be
      running
      portmap
      or
      rpcbind
      . If you are running
      rpcbind
      , you will also need to add the
      -i
      option when
      rpcbind
      starts up. Please make sure you understand the security
      implications of doing this. You might also have to modify your
      firewall settings to allow NFS booting to work.

-  ``YOCTOADT_ROOTFS_``\ arch: The root filesystem images you want to
   download from the ``YOCTOADT_IPKG_REPO`` repository.

-  ``YOCTOADT_TARGET_SYSROOT_IMAGE_``\ arch: The particular root
   filesystem used to extract and create the target sysroot. The value
   of this variable must have been specified with
   ``YOCTOADT_ROOTFS_``\ arch. For example, if you downloaded both
   ``minimal`` and ``sato-sdk`` images by setting
   ``YOCTOADT_ROOTFS_``\ arch to "minimal sato-sdk", then
   ``YOCTOADT_ROOTFS_``\ arch must be set to either "minimal" or
   "sato-sdk".

-  ``YOCTOADT_TARGET_SYSROOT_LOC_``\ arch: The location on the
   development host where the target sysroot is created.

After you have configured the ``adt_installer.conf`` file, run the
installer using the following command: $ cd adt-installer $
./adt_installer Once the installer begins to run, you are asked to enter
the location for cross-toolchain installation. The default location is
``/opt/poky/``\ release. After either accepting the default location or
selecting your own location, you are prompted to run the installation
script interactively or in silent mode. If you want to closely monitor
the installation, choose "I" for interactive mode rather than "S" for
silent mode. Follow the prompts from the script to complete the
installation.

Once the installation completes, the ADT, which includes the
cross-toolchain, is installed in the selected installation directory.
You will notice environment setup files for the cross-toolchain in the
installation directory, and image tarballs in the ``adt-installer``
directory according to your installer configurations, and the target
sysroot located according to the ``YOCTOADT_TARGET_SYSROOT_LOC_``\ arch
variable also in your configuration file.

.. _using-an-existing-toolchain-tarball:

Using a Cross-Toolchain Tarball
-------------------------------

If you want to simply install a cross-toolchain by hand, you can do so
by running the toolchain installer. The installer includes the pre-built
cross-toolchain, the ``runqemu`` script, and support files. If you use
this method to install the cross-toolchain, you might still need to
install the target sysroot by installing and extracting it separately.
For information on how to install the sysroot, see the "`Extracting the
Root Filesystem <#extracting-the-root-filesystem>`__" section.

Follow these steps:

1. *Get your toolchain installer using one of the following methods:*

   -  Go to ` <&YOCTO_TOOLCHAIN_DL_URL;>`__ and find the folder that
      matches your host development system (i.e. ``i686`` for 32-bit
      machines or ``x86_64`` for 64-bit machines).

      Go into that folder and download the toolchain installer whose
      name includes the appropriate target architecture. The toolchains
      provided by the Yocto Project are based off of the
      ``core-image-sato`` image and contain libraries appropriate for
      developing against that image. For example, if your host
      development system is a 64-bit x86 system and you are going to use
      your cross-toolchain for a 32-bit x86 target, go into the
      ``x86_64`` folder and download the following installer:
      poky-glibc-x86_64-core-image-sato-i586-toolchain-DISTRO.sh

   -  Build your own toolchain installer. For cases where you cannot use
      an installer from the download area, you can build your own as
      described in the "`Optionally Building a Toolchain
      Installer <#optionally-building-a-toolchain-installer>`__"
      section.

2. *Once you have the installer, run it to install the toolchain:*

   .. note::

      You must change the permissions on the toolchain installer script
      so that it is executable.

   The following command shows how to run the installer given a
   toolchain tarball for a 64-bit x86 development host system and a
   32-bit x86 target architecture. The example assumes the toolchain
   installer is located in ``~/Downloads/``. $
   ~/Downloads/poky-glibc-x86_64-core-image-sato-i586-toolchain-DISTRO.sh
   The first thing the installer prompts you for is the directory into
   which you want to install the toolchain. The default directory used
   is ``/opt/poky/DISTRO``. If you do not have write permissions for the
   directory into which you are installing the toolchain, the toolchain
   installer notifies you and exits. Be sure you have write permissions
   in the directory and run the installer again.

   When the script finishes, the cross-toolchain is installed. You will
   notice environment setup files for the cross-toolchain in the
   installation directory.

.. _using-the-toolchain-from-within-the-build-tree:

Using BitBake and the Build Directory
-------------------------------------

A final way of making the cross-toolchain available is to use BitBake to
generate the toolchain within an existing :term:`Build Directory`.
This method does
not install the toolchain into the default ``/opt`` directory. As with
the previous method, if you need to install the target sysroot, you must
do that separately as well.

Follow these steps to generate the toolchain into the Build Directory:

1. *Set up the Build Environment:* Source the OpenEmbedded build
   environment setup script (i.e.
   ````` <&YOCTO_DOCS_REF_URL;#structure-core-script>`__ or
   ```oe-init-build-env-memres`` <&YOCTO_DOCS_REF_URL;#structure-memres-core-script>`__)
   located in the :term:`Source Directory`.

2. *Check your Local Configuration File:* At this point, you should be
   sure that the :term:`MACHINE`
   variable in the ``local.conf`` file found in the ``conf`` directory
   of the Build Directory is set for the target architecture. Comments
   within the ``local.conf`` file list the values you can use for the
   ``MACHINE`` variable. If you do not change the ``MACHINE`` variable,
   the OpenEmbedded build system uses ``qemux86`` as the default target
   machine when building the cross-toolchain.

   .. note::

      You can populate the Build Directory with the cross-toolchains for
      more than a single architecture. You just need to edit the
      MACHINE
      variable in the
      local.conf
      file and re-run the
      bitbake
      command.

3. *Make Sure Your Layers are Enabled:* Examine the
   ``conf/bblayers.conf`` file and make sure that you have enabled all
   the compatible layers for your target machine. The OpenEmbedded build
   system needs to be aware of each layer you want included when
   building images and cross-toolchains. For information on how to
   enable a layer, see the "`Enabling Your
   Layer <&YOCTO_DOCS_DEV_URL;#enabling-your-layer>`__" section in the
   Yocto Project Development Manual.

4. *Generate the Cross-Toolchain:* Run ``bitbake meta-ide-support`` to
   complete the cross-toolchain generation. Once the ``bitbake`` command
   finishes, the cross-toolchain is generated and populated within the
   Build Directory. You will notice environment setup files for the
   cross-toolchain that contain the string "``environment-setup``" in
   the Build Directory's ``tmp`` folder.

   Be aware that when you use this method to install the toolchain, you
   still need to separately extract and install the sysroot filesystem.
   For information on how to do this, see the "`Extracting the Root
   Filesystem <#extracting-the-root-filesystem>`__" section.

Setting Up the Cross-Development Environment
============================================

Before you can develop using the cross-toolchain, you need to set up the
cross-development environment by sourcing the toolchain's environment
setup script. If you used the ADT Installer or hand-installed
cross-toolchain, then you can find this script in the directory you
chose for installation. For this release, the default installation
directory is ````. If you installed the toolchain in the
:term:`Build Directory`, you can find the
environment setup script for the toolchain in the Build Directory's
``tmp`` directory.

Be sure to run the environment setup script that matches the
architecture for which you are developing. Environment setup scripts
begin with the string "``environment-setup``" and include as part of
their name the architecture. For example, the toolchain environment
setup script for a 64-bit IA-based architecture installed in the default
installation directory would be the following:
YOCTO_ADTPATH_DIR/environment-setup-x86_64-poky-linux When you run the
setup script, many environment variables are defined:
:term:`SDKTARGETSYSROOT` -
The path to the sysroot used for cross-compilation
:term:`PKG_CONFIG_PATH` - The
path to the target pkg-config files
:term:`CONFIG_SITE` - A GNU
autoconf site file preconfigured for the target
:term:`CC` - The minimal command and
arguments to run the C compiler
:term:`CXX` - The minimal command and
arguments to run the C++ compiler
:term:`CPP` - The minimal command and
arguments to run the C preprocessor
:term:`AS` - The minimal command and
arguments to run the assembler :term:`LD`
- The minimal command and arguments to run the linker
:term:`GDB` - The minimal command and
arguments to run the GNU Debugger
:term:`STRIP` - The minimal command and
arguments to run 'strip', which strips symbols
:term:`RANLIB` - The minimal command
and arguments to run 'ranlib'
:term:`OBJCOPY` - The minimal command
and arguments to run 'objcopy'
:term:`OBJDUMP` - The minimal command
and arguments to run 'objdump' :term:`AR`
- The minimal command and arguments to run 'ar'
:term:`NM` - The minimal command and
arguments to run 'nm'
:term:`TARGET_PREFIX` - The
toolchain binary prefix for the target tools
:term:`CROSS_COMPILE` - The
toolchain binary prefix for the target tools
:term:`CONFIGURE_FLAGS` - The
minimal arguments for GNU configure
:term:`CFLAGS` - Suggested C flags
:term:`CXXFLAGS` - Suggested C++
flags :term:`LDFLAGS` - Suggested
linker flags when you use CC to link
:term:`CPPFLAGS` - Suggested
preprocessor flags

Securing Kernel and Filesystem Images
=====================================

You will need to have a kernel and filesystem image to boot using your
hardware or the QEMU emulator. Furthermore, if you plan on booting your
image using NFS or you want to use the root filesystem as the target
sysroot, you need to extract the root filesystem.

Getting the Images
------------------

To get the kernel and filesystem images, you either have to build them
or download pre-built versions. For an example of how to build these
images, see the "`Buiding
Images <&YOCTO_DOCS_QS_URL;#qs-buiding-images>`__" section of the Yocto
Project Quick Start. For an example of downloading pre-build versions,
see the "`Example Using Pre-Built Binaries and
QEMU <#using-pre-built>`__" section.

The Yocto Project ships basic kernel and filesystem images for several
architectures (``x86``, ``x86-64``, ``mips``, ``powerpc``, and ``arm``)
that you can use unaltered in the QEMU emulator. These kernel images
reside in the release area - ` <&YOCTO_MACHINES_DL_URL;>`__ and are
ideal for experimentation using Yocto Project. For information on the
image types you can build using the OpenEmbedded build system, see the
":ref:`ref-manual/ref-images:Images`" chapter in the Yocto
Project Reference Manual.

If you are planning on developing against your image and you are not
building or using one of the Yocto Project development images (e.g.
``core-image-*-dev``), you must be sure to include the development
packages as part of your image recipe.

If you plan on remotely deploying and debugging your application from
within the Eclipse IDE, you must have an image that contains the Yocto
Target Communication Framework (TCF) agent (``tcf-agent``). You can do
this by including the ``eclipse-debug`` image feature.

.. note::

   See the "
   Image Features
   " section in the Yocto Project Reference Manual for information on
   image features.

To include the ``eclipse-debug`` image feature, modify your
``local.conf`` file in the :term:`Build Directory`
so that the
:term:`EXTRA_IMAGE_FEATURES`
variable includes the "eclipse-debug" feature. After modifying the
configuration file, you can rebuild the image. Once the image is
rebuilt, the ``tcf-agent`` will be included in the image and is launched
automatically after the boot.

Extracting the Root Filesystem
------------------------------

If you install your toolchain by hand or build it using BitBake and you
need a root filesystem, you need to extract it separately. If you use
the ADT Installer to install the ADT, the root filesystem is
automatically extracted and installed.

Here are some cases where you need to extract the root filesystem:

-  You want to boot the image using NFS.

-  You want to use the root filesystem as the target sysroot. For
   example, the Eclipse IDE environment with the Eclipse Yocto Plug-in
   installed allows you to use QEMU to boot under NFS.

-  You want to develop your target application using the root filesystem
   as the target sysroot.

To extract the root filesystem, first ``source`` the cross-development
environment setup script to establish necessary environment variables.
If you built the toolchain in the Build Directory, you will find the
toolchain environment script in the ``tmp`` directory. If you installed
the toolchain by hand, the environment setup script is located in
``/opt/poky/DISTRO``.

After sourcing the environment script, use the ``runqemu-extract-sdk``
command and provide the filesystem image.

Following is an example. The second command sets up the environment. In
this case, the setup script is located in the ``/opt/poky/DISTRO``
directory. The third command extracts the root filesystem from a
previously built filesystem that is located in the ``~/Downloads``
directory. Furthermore, this command extracts the root filesystem into
the ``qemux86-sato`` directory: $ cd ~ $ source
/opt/poky/DISTRO/environment-setup-i586-poky-linux $ runqemu-extract-sdk
\\ ~/Downloads/core-image-sato-sdk-qemux86-2011091411831.rootfs.tar.bz2
\\ $HOME/qemux86-sato You could now point to the target sysroot at
``qemux86-sato``.

Optionally Building a Toolchain Installer
=========================================

As an alternative to locating and downloading a toolchain installer, you
can build the toolchain installer if you have a :term:`Build Directory`.

.. note::

   Although not the preferred method, it is also possible to use
   bitbake meta-toolchain
   to build the toolchain installer. If you do use this method, you must
   separately install and extract the target sysroot. For information on
   how to install the sysroot, see the "
   Extracting the Root Filesystem
   " section.

To build the toolchain installer and populate the SDK image, use the
following command: $ bitbake image -c populate_sdk The command results
in a toolchain installer that contains the sysroot that matches your
target root filesystem.

Another powerful feature is that the toolchain is completely
self-contained. The binaries are linked against their own copy of
``libc``, which results in no dependencies on the target system. To
achieve this, the pointer to the dynamic loader is configured at install
time since that path cannot be dynamically altered. This is the reason
for a wrapper around the ``populate_sdk`` archive.

Another feature is that only one set of cross-canadian toolchain
binaries are produced per architecture. This feature takes advantage of
the fact that the target hardware can be passed to ``gcc`` as a set of
compiler options. Those options are set up by the environment script and
contained in variables such as :term:`CC`
and :term:`LD`. This reduces the space
needed for the tools. Understand, however, that a sysroot is still
needed for every target since those binaries are target-specific.

Remember, before using any BitBake command, you must source the build
environment setup script (i.e.
````` <&YOCTO_DOCS_REF_URL;#structure-core-script>`__ or
```oe-init-build-env-memres`` <&YOCTO_DOCS_REF_URL;#structure-memres-core-script>`__)
located in the Source Directory and you must make sure your
``conf/local.conf`` variables are correct. In particular, you need to be
sure the :term:`MACHINE` variable
matches the architecture for which you are building and that the
:term:`SDKMACHINE` variable is
correctly set if you are building a toolchain designed to run on an
architecture that differs from your current development host machine
(i.e. the build machine).

When the ``bitbake`` command completes, the toolchain installer will be
in ``tmp/deploy/sdk`` in the Build Directory.

.. note::

   By default, this toolchain does not build static binaries. If you
   want to use the toolchain to build these types of libraries, you need
   to be sure your image has the appropriate static development
   libraries. Use the
   IMAGE_INSTALL
   variable inside your
   local.conf
   file to install the appropriate library packages. Following is an
   example using
   glibc
   static development libraries:
   ::

           IMAGE_INSTALL_append = " glibc-staticdev"
                  

Optionally Using an External Toolchain
======================================

You might want to use an external toolchain as part of your development.
If this is the case, the fundamental steps you need to accomplish are as
follows:

-  Understand where the installed toolchain resides. For cases where you
   need to build the external toolchain, you would need to take separate
   steps to build and install the toolchain.

-  Make sure you add the layer that contains the toolchain to your
   ``bblayers.conf`` file through the
   :term:`BBLAYERS` variable.

-  Set the
   :term:`EXTERNAL_TOOLCHAIN`
   variable in your ``local.conf`` file to the location in which you
   installed the toolchain.

A good example of an external toolchain used with the Yocto Project is
Mentor Graphics Sourcery G++ Toolchain. You can see information on how
to use that particular layer in the ``README`` file at
http://github.com/MentorEmbedded/meta-sourcery/. You can find
further information by reading about the
:term:`TCMODE` variable in the Yocto
Project Reference Manual's variable glossary.

.. _using-pre-built:

Example Using Pre-Built Binaries and QEMU
=========================================

If hardware, libraries and services are stable, you can get started by
using a pre-built binary of the filesystem image, kernel, and toolchain
and run it using the QEMU emulator. This scenario is useful for
developing application software.

|Using a Pre-Built Image|

For this scenario, you need to do several things:

-  Install the appropriate stand-alone toolchain tarball.

-  Download the pre-built image that will boot with QEMU. You need to be
   sure to get the QEMU image that matches your target machine's
   architecture (e.g. x86, ARM, etc.).

-  Download the filesystem image for your target machine's architecture.

-  Set up the environment to emulate the hardware and then start the
   QEMU emulator.

Installing the Toolchain
------------------------

You can download a tarball installer, which includes the pre-built
toolchain, the ``runqemu`` script, and support files from the
appropriate directory under ` <&YOCTO_TOOLCHAIN_DL_URL;>`__. Toolchains
are available for 32-bit and 64-bit x86 development systems from the
``i686`` and ``x86_64`` directories, respectively. The toolchains the
Yocto Project provides are based off the ``core-image-sato`` image and
contain libraries appropriate for developing against that image. Each
type of development system supports five or more target architectures.

The names of the tarball installer scripts are such that a string
representing the host system appears first in the filename and then is
immediately followed by a string representing the target architecture.

::

        poky-glibc-host_system-image_type-arch-toolchain-release_version.sh

        Where:
            host_system is a string representing your development system:

                       i686 or x86_64.

            image_type is a string representing the image you wish to
                   develop a Software Development Toolkit (SDK) for use against.
                   The Yocto Project builds toolchain installers using the
                   following BitBake command:

                       bitbake core-image-sato -c populate_sdk

            arch is a string representing the tuned target architecture:

                       i586, x86_64, powerpc, mips, armv7a or armv5te

            release_version is a string representing the release number of the
                   Yocto Project:

                       DISTRO, DISTRO+snapshot
               

For example, the following toolchain installer is for a 64-bit
development host system and a i586-tuned target architecture based off
the SDK for ``core-image-sato``:
poky-glibc-x86_64-core-image-sato-i586-toolchain-DISTRO.sh

Toolchains are self-contained and by default are installed into
``/opt/poky``. However, when you run the toolchain installer, you can
choose an installation directory.

The following command shows how to run the installer given a toolchain
tarball for a 64-bit x86 development host system and a 32-bit x86 target
architecture. You must change the permissions on the toolchain installer
script so that it is executable.

The example assumes the toolchain installer is located in
``~/Downloads/``.

.. note::

   If you do not have write permissions for the directory into which you
   are installing the toolchain, the toolchain installer notifies you
   and exits. Be sure you have write permissions in the directory and
   run the installer again.

$ ~/Downloads/poky-glibc-x86_64-core-image-sato-i586-toolchain-DISTRO.sh

For more information on how to install tarballs, see the "`Using a
Cross-Toolchain
Tarball <&YOCTO_DOCS_ADT_URL;#using-an-existing-toolchain-tarball>`__"
and "`Using BitBake and the Build
Directory <&YOCTO_DOCS_ADT_URL;#using-the-toolchain-from-within-the-build-tree>`__"
sections in the Yocto Project Application Developer's Guide.

Downloading the Pre-Built Linux Kernel
--------------------------------------

You can download the pre-built Linux kernel suitable for running in the
QEMU emulator from ` <&YOCTO_QEMU_DL_URL;>`__. Be sure to use the kernel
that matches the architecture you want to simulate. Download areas exist
for the five supported machine architectures: ``qemuarm``, ``qemumips``,
``qemuppc``, ``qemux86``, and ``qemux86-64``.

Most kernel files have one of the following forms: \*zImage-qemuarch.bin
vmlinux-qemuarch.bin Where: arch is a string representing the target
architecture: x86, x86-64, ppc, mips, or arm.

You can learn more about downloading a Yocto Project kernel in the
"`Yocto Project Kernel <&YOCTO_DOCS_DEV_URL;#local-kernel-files>`__"
bulleted item in the Yocto Project Development Manual.

Downloading the Filesystem
--------------------------

You can also download the filesystem image suitable for your target
architecture from ` <&YOCTO_QEMU_DL_URL;>`__. Again, be sure to use the
filesystem that matches the architecture you want to simulate.

The filesystem image has two tarball forms: ``ext3`` and ``tar``. You
must use the ``ext3`` form when booting an image using the QEMU
emulator. The ``tar`` form can be flattened out in your host development
system and used for build purposes with the Yocto Project.
core-image-profile-qemuarch.ext3 core-image-profile-qemuarch.tar.bz2
Where: profile is the filesystem image's profile: lsb, lsb-dev, lsb-sdk,
lsb-qt3, minimal, minimal-dev, sato, sato-dev, or sato-sdk. For
information on these types of image profiles, see the
":ref:`ref-manual/ref-images:Images`" chapter in the Yocto
Project Reference Manual. arch is a string representing the target
architecture: x86, x86-64, ppc, mips, or arm.

Setting Up the Environment and Starting the QEMU Emulator
---------------------------------------------------------

Before you start the QEMU emulator, you need to set up the emulation
environment. The following command form sets up the emulation
environment. $ source
YOCTO_ADTPATH_DIR/environment-setup-arch-poky-linux-if Where: arch is a
string representing the target architecture: i586, x86_64, ppc603e,
mips, or armv5te. if is a string representing an embedded application
binary interface. Not all setup scripts include this string.

Finally, this command form invokes the QEMU emulator $ runqemu qemuarch
kernel-image filesystem-image Where: qemuarch is a string representing
the target architecture: qemux86, qemux86-64, qemuppc, qemumips, or
qemuarm. kernel-image is the architecture-specific kernel image.
filesystem-image is the .ext3 filesystem image.

Continuing with the example, the following two commands setup the
emulation environment and launch QEMU. This example assumes the root
filesystem (``.ext3`` file) and the pre-built kernel image file both
reside in your home directory. The kernel and filesystem are for a
32-bit target architecture. $ cd $HOME $ source
YOCTO_ADTPATH_DIR/environment-setup-i586-poky-linux $ runqemu qemux86
bzImage-qemux86.bin \\ core-image-sato-qemux86.ext3

The environment in which QEMU launches varies depending on the
filesystem image and on the target architecture. For example, if you
source the environment for the ARM target architecture and then boot the
minimal QEMU image, the emulator comes up in a new shell in command-line
mode. However, if you boot the SDK image, QEMU comes up with a GUI.

.. note::

   Booting the PPC image results in QEMU launching in the same shell in
   command-line mode.

.. |Using a Pre-Built Image| image:: figures/using-a-pre-built-image.png
