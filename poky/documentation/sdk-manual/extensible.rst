.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

************************
Using the Extensible SDK
************************

This chapter describes the extensible SDK and how to install it.
Information covers the pieces of the SDK, how to install it, and
presents a look at using the ``devtool`` functionality. The extensible
SDK makes it easy to add new applications and libraries to an image,
modify the source for an existing component, test changes on the target
hardware, and ease integration into the rest of the
:term:`OpenEmbedded Build System`.

.. note::

   For a side-by-side comparison of main features supported for an
   extensible SDK as compared to a standard SDK, see the
   :ref:`sdk-manual/intro:introduction` section.

In addition to the functionality available through ``devtool``, you can
alternatively make use of the toolchain directly, for example from
Makefile and Autotools. See the
":ref:`sdk-manual/working-projects:using the sdk toolchain directly`" chapter
for more information.

Why use the Extensible SDK and What is in It?
=============================================

The extensible SDK provides a cross-development toolchain and libraries
tailored to the contents of a specific image. You would use the
Extensible SDK if you want a toolchain experience supplemented with the
powerful set of ``devtool`` commands tailored for the Yocto Project
environment.

The installed extensible SDK consists of several files and directories.
Basically, it contains an SDK environment setup script, some
configuration files, an internal build system, and the ``devtool``
functionality.

Installing the Extensible SDK
=============================

Two ways to install the Extensible SDK
--------------------------------------

Extensible SDK can be installed in two different ways, and both have
their own pros and cons:

#. *Setting up the Extensible SDK environment directly in a Yocto build*. This
   avoids having to produce, test, distribute and maintain separate SDK
   installer archives, which can get very large. There is only one environment
   for the regular Yocto build and the SDK and less code paths where things can
   go not according to plan. It's easier to update the SDK: it simply means
   updating the Yocto layers with git fetch or layer management tooling. The
   SDK extensibility is better than in the second option: just run ``bitbake``
   again to add more things to the sysroot, or add layers if even more things
   are required.

#. *Setting up the Extensible SDK from a standalone installer*. This has the
   benefit of having a single, self-contained archive that includes all the
   needed binary artifacts. So nothing needs to be rebuilt, and there is no
   need to provide a well-functioning binary artefact cache over the network
   for developers with underpowered laptops.

.. _setting_up_ext_sdk_in_build:

Setting up the Extensible SDK environment directly in a Yocto build
-------------------------------------------------------------------

#. Set up all the needed layers and a Yocto :term:`Build Directory`, e.g. a regular Yocto
   build where ``bitbake`` can be executed.

#. Run::

      $ bitbake meta-ide-support
      $ bitbake -c populate_sysroot gtk+3
      # or any other target or native item that the application developer would need
      $ bitbake build-sysroots -c build_native_sysroot && bitbake build-sysroots -c build_target_sysroot

Setting up the Extensible SDK from a standalone installer
---------------------------------------------------------

The first thing you need to do is install the SDK on your :term:`Build
Host` by running the ``*.sh`` installation script.

You can download a tarball installer, which includes the pre-built
toolchain, the ``runqemu`` script, the internal build system,
``devtool``, and support files from the appropriate
:yocto_dl:`toolchain </releases/yocto/yocto-&DISTRO;/toolchain/>` directory within the Index of
Releases. Toolchains are available for several 32-bit and 64-bit
architectures with the ``x86_64`` directories, respectively. The
toolchains the Yocto Project provides are based off the
``core-image-sato`` and ``core-image-minimal`` images and contain
libraries appropriate for developing against that image.

The names of the tarball installer scripts are such that a string
representing the host system appears first in the filename and then is
immediately followed by a string representing the target architecture.
An extensible SDK has the string "-ext" as part of the name. Following
is the general form::

   poky-glibc-host_system-image_type-arch-toolchain-ext-release_version.sh

   Where:
       host_system is a string representing your development system:

                  i686 or x86_64.

       image_type is the image for which the SDK was built:

                  core-image-sato or core-image-minimal

       arch is a string representing the tuned target architecture:

                  aarch64, armv5e, core2-64, i586, mips32r2, mips64, ppc7400, or cortexa8hf-neon

       release_version is a string representing the release number of the Yocto Project:

                  &DISTRO;, &DISTRO;+snapshot

For example, the following SDK installer is for a 64-bit
development host system and a i586-tuned target architecture based off
the SDK for ``core-image-sato`` and using the current &DISTRO; snapshot::

   poky-glibc-x86_64-core-image-sato-i586-toolchain-ext-&DISTRO;.sh

.. note::

   As an alternative to downloading an SDK, you can build the SDK
   installer. For information on building the installer, see the
   :ref:`sdk-manual/appendix-obtain:building an sdk installer`
   section.

The SDK and toolchains are self-contained and by default are installed
into the ``poky_sdk`` folder in your home directory. You can choose to
install the extensible SDK in any location when you run the installer.
However, because files need to be written under that directory during
the normal course of operation, the location you choose for installation
must be writable for whichever users need to use the SDK.

The following command shows how to run the installer given a toolchain
tarball for a 64-bit x86 development host system and a 64-bit x86 target
architecture. The example assumes the SDK installer is located in
``~/Downloads/`` and has execution rights::

   $ ./Downloads/poky-glibc-x86_64-core-image-minimal-core2-64-toolchain-ext-2.5.sh
   Poky (Yocto Project Reference Distro) Extensible SDK installer version 2.5
   ==========================================================================
   Enter target directory for SDK (default: poky_sdk):
   You are about to install the SDK to "/home/scottrif/poky_sdk". Proceed [Y/n]? Y
   Extracting SDK..............done
   Setting it up...
   Extracting buildtools...
   Preparing build system...
   Parsing recipes: 100% |##################################################################| Time: 0:00:52
   Initialising tasks: 100% |###############################################################| Time: 0:00:00
   Checking sstate mirror object availability: 100% |#######################################| Time: 0:00:00
   Loading cache: 100% |####################################################################| Time: 0:00:00
   Initialising tasks: 100% |###############################################################| Time: 0:00:00
   done
   SDK has been successfully set up and is ready to be used.
   Each time you wish to use the SDK in a new shell session, you need to source the environment setup script e.g.
    $ . /home/scottrif/poky_sdk/environment-setup-core2-64-poky-linux

.. note::

   If you do not have write permissions for the directory into which you
   are installing the SDK, the installer notifies you and exits. For
   that case, set up the proper permissions in the directory and run the
   installer again.

.. _running_the_ext_sdk_env:

Running the Extensible SDK Environment Setup Script
===================================================

Once you have the SDK installed, you must run the SDK environment setup
script before you can actually use the SDK.

When using an SDK directly in a Yocto build, you will find the script in
``tmp/deploy/images/qemux86-64/`` in your :term:`Build Directory`.

When using a standalone SDK installer, this setup script resides in
the directory you chose when you installed the SDK, which is either the
default ``poky_sdk`` directory or the directory you chose during
installation.

Before running the script, be sure it is the one that matches the
architecture for which you are developing. Environment setup scripts
begin with the string "``environment-setup``" and include as part of
their name the tuned target architecture. As an example, the following
commands set the working directory to where the SDK was installed and
then source the environment setup script. In this example, the setup
script is for an IA-based target machine using i586 tuning::

   $ cd /home/scottrif/poky_sdk
   $ source environment-setup-core2-64-poky-linux
   SDK environment now set up; additionally you may now run devtool to perform development tasks.
   Run devtool --help for further details.

When using the environment script directly in a Yocto build, it can
be run similarly::

   $ source tmp/deploy/images/qemux86-64/environment-setup-core2-64-poky-linux

Running the setup script defines many environment variables needed in order to
use the SDK (e.g. ``PATH``, :term:`CC`, :term:`LD`, and so forth). If you want
to see all the environment variables the script exports, examine the
installation file itself.

.. _using_devtool:

Using ``devtool`` in Your SDK Workflow
======================================

The cornerstone of the extensible SDK is a command-line tool called
``devtool``. This tool provides a number of features that help you
build, test and package software within the extensible SDK, and
optionally integrate it into an image built by the OpenEmbedded build
system.

.. note::

   The use of ``devtool`` is not limited to the extensible SDK. You can use
   ``devtool`` to help you easily develop any project whose build output must be
   part of an image built using the build system.

The ``devtool`` command line is organized similarly to
:ref:`overview-manual/development-environment:git` in that it has a number of
sub-commands for each function. You can run ``devtool --help`` to see
all the commands.

.. note::

   See the ":doc:`/ref-manual/devtool-reference`"
   section in the Yocto Project Reference Manual.

``devtool`` subcommands provide entry-points into development:

-  *devtool add*: Assists in adding new software to be built.

-  *devtool modify*: Sets up an environment to enable you to modify
   the source of an existing component.

-  *devtool ide-sdk*: Generates a configuration for an IDE.

-  *devtool upgrade*: Updates an existing recipe so that you can
   build it for an updated set of source files.

As with the build system, "recipes" represent software packages within
``devtool``. When you use ``devtool add``, a recipe is automatically
created. When you use ``devtool modify``, the specified existing recipe
is used in order to determine where to get the source code and how to
patch it. In both cases, an environment is set up so that when you build
the recipe a source tree that is under your control is used in order to
allow you to make changes to the source as desired. By default, new
recipes and the source go into a "workspace" directory under the SDK.

To learn how to use ``devtool`` to add, modify, upgrade recipes and more, see
the :ref:`dev-manual/devtool:Using the \`\`devtool\`\` command-line tool`
section of the Yocto Project Development Tasks Manual.

Installing Additional Items Into the Extensible SDK
===================================================

Out of the box the extensible SDK typically only comes with a small
number of tools and libraries. A minimal SDK starts mostly empty and is
populated on-demand. Sometimes you must explicitly install extra items
into the SDK. If you need these extra items, you can first search for
the items using the ``devtool search`` command. For example, suppose you
need to link to libGL but you are not sure which recipe provides libGL.
You can use the following command to find out::

   $ devtool search libGL mesa
   A free implementation of the OpenGL API

Once you know the recipe
(i.e. ``mesa`` in this example), you can install it.

When using the extensible SDK directly in a Yocto build
-------------------------------------------------------

In this scenario, the Yocto build tooling, e.g. ``bitbake``
is directly accessible to build additional items, and it
can simply be executed directly::

   $ bitbake curl-native
   # Add newly built native items to native sysroot
   $ bitbake build-sysroots -c build_native_sysroot
   $ bitbake mesa
   # Add newly built target items to target sysroot
   $ bitbake build-sysroots -c build_target_sysroot

When using a standalone installer for the Extensible SDK
--------------------------------------------------------

::

   $ devtool sdk-install mesa

By default, the ``devtool sdk-install`` command assumes
the item is available in pre-built form from your SDK provider. If the
item is not available and it is acceptable to build the item from
source, you can add the "-s" option as follows::

   $ devtool sdk-install -s mesa

It is important to remember that building the item from source
takes significantly longer than installing the pre-built artifact. Also,
if there is no recipe for the item you want to add to the SDK, you must
instead add the item using the ``devtool add`` command.

Applying Updates to an Installed Extensible SDK
===============================================

If you are working with an installed extensible SDK that gets
occasionally updated (e.g. a third-party SDK), then you will need to
manually "pull down" the updates into the installed SDK.

To update your installed SDK, use ``devtool`` as follows::

   $ devtool sdk-update

The previous command assumes your SDK provider has set the default update URL
for you through the :term:`SDK_UPDATE_URL` variable as described in the
":ref:`sdk-manual/appendix-customizing:Providing Updates to the Extensible SDK After Installation`"
section. If the SDK provider has not set that default URL, you need to
specify it yourself in the command as follows::

   $ devtool sdk-update path_to_update_directory

.. note::

   The URL needs to point specifically to a published SDK and not to an
   SDK installer that you would download and install.

Creating a Derivative SDK With Additional Components
====================================================

You might need to produce an SDK that contains your own custom
libraries. A good example would be if you were a vendor with customers
that use your SDK to build their own platform-specific software and
those customers need an SDK that has custom libraries. In such a case,
you can produce a derivative SDK based on the currently installed SDK
fairly easily by following these steps:

#. If necessary, install an extensible SDK that you want to use as a
   base for your derivative SDK.

#. Source the environment script for the SDK.

#. Add the extra libraries or other components you want by using the
   ``devtool add`` command.

#. Run the ``devtool build-sdk`` command.

The previous steps take the recipes added to the workspace and construct
a new SDK installer that contains those recipes and the resulting binary
artifacts. The recipes go into their own separate layer in the
constructed derivative SDK, which leaves the workspace clean and ready
for users to add their own recipes.
