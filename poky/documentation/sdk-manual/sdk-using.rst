.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

**********************
Using the Standard SDK
**********************

This chapter describes the standard SDK and how to install it.
Information includes unique installation and setup aspects for the
standard SDK.

.. note::

   For a side-by-side comparison of main features supported for a
   standard SDK as compared to an extensible SDK, see the "
   Introduction
   " section.

You can use a standard SDK to work on Makefile and Autotools-based
projects. See the "`Using the SDK Toolchain
Directly <#sdk-working-projects>`__" chapter for more information.

.. _sdk-standard-sdk-intro:

Why use the Standard SDK and What is in It?
===========================================

The Standard SDK provides a cross-development toolchain and libraries
tailored to the contents of a specific image. You would use the Standard
SDK if you want a more traditional toolchain experience as compared to
the extensible SDK, which provides an internal build system and the
``devtool`` functionality.

The installed Standard SDK consists of several files and directories.
Basically, it contains an SDK environment setup script, some
configuration files, and host and target root filesystems to support
usage. You can see the directory structure in the "`Installed Standard
SDK Directory
Structure <#sdk-installed-standard-sdk-directory-structure>`__" section.

.. _sdk-installing-the-sdk:

Installing the SDK
==================

The first thing you need to do is install the SDK on your :term:`Build
Host` by running the ``*.sh`` installation script.

You can download a tarball installer, which includes the pre-built
toolchain, the ``runqemu`` script, and support files from the
appropriate :yocto_dl:`toolchain </releases/yocto/yocto-3.1.2/toolchain/>` directory within
the Index of Releases. Toolchains are available for several 32-bit and
64-bit architectures with the ``x86_64`` directories, respectively. The
toolchains the Yocto Project provides are based off the
``core-image-sato`` and ``core-image-minimal`` images and contain
libraries appropriate for developing against that image.

The names of the tarball installer scripts are such that a string
representing the host system appears first in the filename and then is
immediately followed by a string representing the target architecture.
::

   poky-glibc-host_system-image_type-arch-toolchain-release_version.sh

   Where:
       host_system is a string representing your development system:

                  i686 or x86_64.

       image_type is the image for which the SDK was built:

                  core-image-minimal or core-image-sato.

       arch is a string representing the tuned target architecture:

                  aarch64, armv5e, core2-64, i586, mips32r2, mips64, ppc7400, or cortexa8hf-neon.

       release_version is a string representing the release number of the Yocto Project:

                  3.1.2, 3.1.2+snapshot

For example, the following SDK installer is for a 64-bit
development host system and a i586-tuned target architecture based off
the SDK for ``core-image-sato`` and using the current DISTRO snapshot:
::

   poky-glibc-x86_64-core-image-sato-i586-toolchain-DISTRO.sh

.. note::

   As an alternative to downloading an SDK, you can build the SDK
   installer. For information on building the installer, see the "
   Building an SDK Installer
   " section.

The SDK and toolchains are self-contained and by default are installed
into the ``poky_sdk`` folder in your home directory. You can choose to
install the extensible SDK in any location when you run the installer.
However, because files need to be written under that directory during
the normal course of operation, the location you choose for installation
must be writable for whichever users need to use the SDK.

The following command shows how to run the installer given a toolchain
tarball for a 64-bit x86 development host system and a 64-bit x86 target
architecture. The example assumes the SDK installer is located in
``~/Downloads/`` and has execution rights.

.. note::

   If you do not have write permissions for the directory into which you
   are installing the SDK, the installer notifies you and exits. For
   that case, set up the proper permissions in the directory and run the
   installer again.

::

   $ ./Downloads/poky-glibc-x86_64-core-image-sato-i586-toolchain-3.1.2.sh
   Poky (Yocto Project Reference Distro) SDK installer version 3.1.2
   ===============================================================
   Enter target directory for SDK (default: /opt/poky/3.1.2):
   You are about to install the SDK to "/opt/poky/3.1.2". Proceed [Y/n]? Y
   Extracting SDK........................................ ..............................done
   Setting it up...done
   SDK has been successfully set up and is ready to be used.
   Each time you wish to use the SDK in a new shell session, you need to source the environment setup script e.g.
    $ . /opt/poky/3.1.2/environment-setup-i586-poky-linux

Again, reference the "`Installed Standard SDK Directory
Structure <#sdk-installed-standard-sdk-directory-structure>`__" section
for more details on the resulting directory structure of the installed
SDK.

.. _sdk-running-the-sdk-environment-setup-script:

Running the SDK Environment Setup Script
========================================

Once you have the SDK installed, you must run the SDK environment setup
script before you can actually use the SDK. This setup script resides in
the directory you chose when you installed the SDK, which is either the
default ``/opt/poky/3.1.2`` directory or the directory you chose during
installation.

Before running the script, be sure it is the one that matches the
architecture for which you are developing. Environment setup scripts
begin with the string "``environment-setup``" and include as part of
their name the tuned target architecture. As an example, the following
commands set the working directory to where the SDK was installed and
then source the environment setup script. In this example, the setup
script is for an IA-based target machine using i586 tuning:
::

   $ source /opt/poky/3.1.2/environment-setup-i586-poky-linux

When you run the
setup script, the same environment variables are defined as are when you
run the setup script for an extensible SDK. See the "`Running the
Extensible SDK Environment Setup
Script <#sdk-running-the-extensible-sdk-environment-setup-script>`__"
section for more information.
