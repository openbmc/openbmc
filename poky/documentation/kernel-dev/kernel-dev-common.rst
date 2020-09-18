.. SPDX-License-Identifier: CC-BY-2.0-UK

************
Common Tasks
************

This chapter presents several common tasks you perform when you work
with the Yocto Project Linux kernel. These tasks include preparing your
host development system for kernel development, preparing a layer,
modifying an existing recipe, patching the kernel, configuring the
kernel, iterative development, working with your own sources, and
incorporating out-of-tree modules.

.. note::

   The examples presented in this chapter work with the Yocto Project
   2.4 Release and forward.

Preparing the Build Host to Work on the Kernel
==============================================

Before you can do any kernel development, you need to be sure your build
host is set up to use the Yocto Project. For information on how to get
set up, see the ":doc:`../dev-manual/dev-manual-start`" section in
the Yocto Project Development Tasks Manual. Part of preparing the system
is creating a local Git repository of the
:term:`Source Directory` (``poky``) on your system. Follow the steps in the
":ref:`dev-manual/dev-manual-start:cloning the \`\`poky\`\` repository`"
section in the Yocto Project Development Tasks Manual to set up your
Source Directory.

.. note::

   Be sure you check out the appropriate development branch or you
   create your local branch by checking out a specific tag to get the
   desired version of Yocto Project. See the "
   Checking Out by Branch in Poky
   " and "
   Checking Out by Tag in Poky
   " sections in the Yocto Project Development Tasks Manual for more
   information.

Kernel development is best accomplished using
:ref:`devtool <sdk-manual/sdk-extensible:using \`\`devtool\`\` in your sdk workflow>`
and not through traditional kernel workflow methods. The remainder of
this section provides information for both scenarios.

Getting Ready to Develop Using ``devtool``
------------------------------------------

Follow these steps to prepare to update the kernel image using
``devtool``. Completing this procedure leaves you with a clean kernel
image and ready to make modifications as described in the "
:ref:`kernel-dev/kernel-dev-common:using \`\`devtool\`\` to patch the kernel`"
section:

1. *Initialize the BitBake Environment:* Before building an extensible
   SDK, you need to initialize the BitBake build environment by sourcing
   the build environment script (i.e. :ref:`structure-core-script`):
   ::

      $ cd ~/poky
      $ source oe-init-build-env

   .. note::

      The previous commands assume the
      Source Repositories
      (i.e.
      poky
      ) have been cloned using Git and the local repository is named
      "poky".

2. *Prepare Your local.conf File:* By default, the
   :term:`MACHINE` variable is set to
   "qemux86-64", which is fine if you are building for the QEMU emulator
   in 64-bit mode. However, if you are not, you need to set the
   ``MACHINE`` variable appropriately in your ``conf/local.conf`` file
   found in the
   :term:`Build Directory` (i.e.
   ``~/poky/build`` in this example).

   Also, since you are preparing to work on the kernel image, you need
   to set the
   :term:`MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS`
   variable to include kernel modules.

   In this example we wish to build for qemux86 so we must set the
   ``MACHINE`` variable to "qemux86" and also add the "kernel-modules".
   As described we do this by appending to ``conf/local.conf``:
   ::

      MACHINE = "qemux86"
      MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "kernel-modules"

3. *Create a Layer for Patches:* You need to create a layer to hold
   patches created for the kernel image. You can use the
   ``bitbake-layers create-layer`` command as follows:
   ::

      $ cd ~/poky/build
      $ bitbake-layers create-layer ../../meta-mylayer
      NOTE: Starting bitbake server...
      Add your new layer with 'bitbake-layers add-layer ../../meta-mylayer'
      $

   .. note::

      For background information on working with common and BSP layers,
      see the "
      Understanding and Creating Layers
      " section in the Yocto Project Development Tasks Manual and the "
      BSP Layers
      " section in the Yocto Project Board Support (BSP) Developer's
      Guide, respectively. For information on how to use the
      bitbake-layers create-layer
      command to quickly set up a layer, see the "
      Creating a General Layer Using the
      bitbake-layers
      Script
      " section in the Yocto Project Development Tasks Manual.

4. *Inform the BitBake Build Environment About Your Layer:* As directed
   when you created your layer, you need to add the layer to the
   :term:`BBLAYERS` variable in the
   ``bblayers.conf`` file as follows:
   ::

      $ cd ~/poky/build
      $ bitbake-layers add-layer ../../meta-mylayer
      NOTE: Starting bitbake server...
      $

5. *Build the Extensible SDK:* Use BitBake to build the extensible SDK
   specifically for use with images to be run using QEMU:
   ::

      $ cd ~/poky/build
      $ bitbake core-image-minimal -c populate_sdk_ext

   Once
   the build finishes, you can find the SDK installer file (i.e.
   ``*.sh`` file) in the following directory:
   ~/poky/build/tmp/deploy/sdk For this example, the installer file is
   named
   ``poky-glibc-x86_64-core-image-minimal-i586-toolchain-ext-DISTRO.sh``

6. *Install the Extensible SDK:* Use the following command to install
   the SDK. For this example, install the SDK in the default
   ``~/poky_sdk`` directory:
   ::

      $ cd ~/poky/build/tmp/deploy/sdk
      $ ./poky-glibc-x86_64-core-image-minimal-i586-toolchain-ext-3.1.2.sh
      Poky (Yocto Project Reference Distro) Extensible SDK installer version 3.1.2
      ============================================================================
      Enter target directory for SDK (default: ~/poky_sdk):
      You are about to install the SDK to "/home/scottrif/poky_sdk". Proceed [Y/n]? Y
      Extracting SDK......................................done
      Setting it up...
      Extracting buildtools...
      Preparing build system...
      Parsing recipes: 100% |#################################################################| Time: 0:00:52
      Initializing tasks: 100% |############## ###############################################| Time: 0:00:04
      Checking sstate mirror object availability: 100% |######################################| Time: 0:00:00
      Parsing recipes: 100% |#################################################################| Time: 0:00:33
      Initializing tasks: 100% |##############################################################| Time: 0:00:00
      done
      SDK has been successfully set up and is ready to be used.
      Each time you wish to use the SDK in a new shell session, you need to source the environment setup script e.g.
       $ . /home/scottrif/poky_sdk/environment-setup-i586-poky-linux


7. *Set Up a New Terminal to Work With the Extensible SDK:* You must set
   up a new terminal to work with the SDK. You cannot use the same
   BitBake shell used to build the installer.

   After opening a new shell, run the SDK environment setup script as
   directed by the output from installing the SDK:
   ::

      $ source ~/poky_sdk/environment-setup-i586-poky-linux
      "SDK environment now set up; additionally you may now run devtool to perform development tasks.
      Run devtool --help for further details.

   .. note::

      If you get a warning about attempting to use the extensible SDK in
      an environment set up to run BitBake, you did not use a new shell.

8. *Build the Clean Image:* The final step in preparing to work on the
   kernel is to build an initial image using ``devtool`` in the new
   terminal you just set up and initialized for SDK work:
   ::

      $ devtool build-image
      Parsing recipes: 100% |##########################################| Time: 0:00:05
      Parsing of 830 .bb files complete (0 cached, 830 parsed). 1299 targets, 47 skipped, 0 masked, 0 errors.
      WARNING: No packages to add, building image core-image-minimal unmodified
      Loading cache: 100% |############################################| Time: 0:00:00
      Loaded 1299 entries from dependency cache.
      NOTE: Resolving any missing task queue dependencies
      Initializing tasks: 100% |#######################################| Time: 0:00:07
      Checking sstate mirror object availability: 100% |###############| Time: 0:00:00
      NOTE: Executing SetScene Tasks
      NOTE: Executing RunQueue Tasks
      NOTE: Tasks Summary: Attempted 2866 tasks of which 2604 didn't need to be rerun and all succeeded.
      NOTE: Successfully built core-image-minimal. You can find output files in /home/scottrif/poky_sdk/tmp/deploy/images/qemux86

   If you were
   building for actual hardware and not for emulation, you could flash
   the image to a USB stick on ``/dev/sdd`` and boot your device. For an
   example that uses a Minnowboard, see the
   `TipsAndTricks/KernelDevelopmentWithEsdk <https://wiki.yoctoproject.org/wiki/TipsAndTricks/KernelDevelopmentWithEsdk>`__
   Wiki page.

At this point you have set up to start making modifications to the
kernel by using the extensible SDK. For a continued example, see the
":ref:`kernel-dev/kernel-dev-common:using \`\`devtool\`\` to patch the kernel`"
section.

Getting Ready for Traditional Kernel Development
------------------------------------------------

Getting ready for traditional kernel development using the Yocto Project
involves many of the same steps as described in the previous section.
However, you need to establish a local copy of the kernel source since
you will be editing these files.

Follow these steps to prepare to update the kernel image using
traditional kernel development flow with the Yocto Project. Completing
this procedure leaves you ready to make modifications to the kernel
source as described in the ":ref:`kernel-dev/kernel-dev-common:using traditional kernel development to patch the kernel`"
section:

1. *Initialize the BitBake Environment:* Before you can do anything
   using BitBake, you need to initialize the BitBake build environment
   by sourcing the build environment script (i.e.
   :ref:`structure-core-script`).
   Also, for this example, be sure that the local branch you have
   checked out for ``poky`` is the Yocto Project &DISTRO_NAME; branch. If
   you need to checkout out the &DISTRO_NAME; branch, see the
   ":ref:`dev-manual/dev-manual-start:checking out by branch in poky`"
   section in the Yocto Project Development Tasks Manual.
   ::

      $ cd ~/poky
      $ git branch
      master
      * &DISTRO_NAME;
      $ source oe-init-build-env

   .. note::

      The previous commands assume the
      Source Repositories
      (i.e.
      poky
      ) have been cloned using Git and the local repository is named
      "poky".

2. *Prepare Your local.conf File:* By default, the
   :term:`MACHINE` variable is set to
   "qemux86-64", which is fine if you are building for the QEMU emulator
   in 64-bit mode. However, if you are not, you need to set the
   ``MACHINE`` variable appropriately in your ``conf/local.conf`` file
   found in the
   :term:`Build Directory` (i.e.
   ``~/poky/build`` in this example).

   Also, since you are preparing to work on the kernel image, you need
   to set the
   :term:`MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS`
   variable to include kernel modules.

   In this example we wish to build for qemux86 so we must set the
   ``MACHINE`` variable to "qemux86" and also add the "kernel-modules".
   As described we do this by appending to ``conf/local.conf``:
   ::

      MACHINE = "qemux86"
      MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "kernel-modules"

3. *Create a Layer for Patches:* You need to create a layer to hold
   patches created for the kernel image. You can use the
   ``bitbake-layers create-layer`` command as follows:
   ::

      $ cd ~/poky/build
      $ bitbake-layers create-layer ../../meta-mylayer
      NOTE: Starting bitbake server...
      Add your new layer with 'bitbake-layers add-layer ../../meta-mylayer'

   .. note::

      For background information on working with common and BSP layers,
      see the "
      Understanding and Creating Layers
      " section in the Yocto Project Development Tasks Manual and the "
      BSP Layers
      " section in the Yocto Project Board Support (BSP) Developer's
      Guide, respectively. For information on how to use the
      bitbake-layers create-layer
      command to quickly set up a layer, see the "
      Creating a General Layer Using the
      bitbake-layers
      Script
      " section in the Yocto Project Development Tasks Manual.

4. *Inform the BitBake Build Environment About Your Layer:* As directed
   when you created your layer, you need to add the layer to the
   :term:`BBLAYERS` variable in the
   ``bblayers.conf`` file as follows:
   ::

      $ cd ~/poky/build
      $ bitbake-layers add-layer ../../meta-mylayer
      NOTE: Starting bitbake server ...
      $

5. *Create a Local Copy of the Kernel Git Repository:* You can find Git
   repositories of supported Yocto Project kernels organized under
   "Yocto Linux Kernel" in the Yocto Project Source Repositories at
   :yocto_git:`/`.

   For simplicity, it is recommended that you create your copy of the
   kernel Git repository outside of the
   :term:`Source Directory`, which is
   usually named ``poky``. Also, be sure you are in the
   ``standard/base`` branch.

   The following commands show how to create a local copy of the
   ``linux-yocto-4.12`` kernel and be in the ``standard/base`` branch.

   .. note::

      The
      linux-yocto-4.12
      kernel can be used with the Yocto Project 2.4 release and forward.
      You cannot use the
      linux-yocto-4.12
      kernel with releases prior to Yocto Project 2.4:

   ::

      $ cd ~
      $ git clone git://git.yoctoproject.org/linux-yocto-4.12 --branch standard/base
      Cloning into 'linux-yocto-4.12'...
      remote: Counting objects: 6097195, done.
      remote: Compressing objects: 100% (901026/901026), done.
      remote: Total 6097195 (delta 5152604), reused 6096847 (delta 5152256)
      Receiving objects: 100% (6097195/6097195), 1.24 GiB | 7.81 MiB/s, done.
      Resolving deltas: 100% (5152604/5152604), done. Checking connectivity... done.
      Checking out   files: 100% (59846/59846), done.

6. *Create a Local Copy of the Kernel Cache Git Repository:* For
   simplicity, it is recommended that you create your copy of the kernel
   cache Git repository outside of the
   :term:`Source Directory`, which is
   usually named ``poky``. Also, for this example, be sure you are in
   the ``yocto-4.12`` branch.

   The following commands show how to create a local copy of the
   ``yocto-kernel-cache`` and be in the ``yocto-4.12`` branch:
   ::

      $ cd ~
      $ git clone git://git.yoctoproject.org/yocto-kernel-cache --branch yocto-4.12
      Cloning into 'yocto-kernel-cache'...
      remote: Counting objects: 22639, done.
      remote: Compressing objects: 100% (9761/9761), done.
      remote: Total 22639 (delta 12400), reused 22586 (delta 12347)
      Receiving objects: 100% (22639/22639), 22.34 MiB | 6.27 MiB/s, done.
      Resolving deltas: 100% (12400/12400), done.
      Checking connectivity... done.

At this point, you are ready to start making modifications to the kernel
using traditional kernel development steps. For a continued example, see
the "`Using Traditional Kernel Development to Patch the
Kernel <#using-traditional-kernel-development-to-patch-the-kernel>`__"
section.

Creating and Preparing a Layer
==============================

If you are going to be modifying kernel recipes, it is recommended that
you create and prepare your own layer in which to do your work. Your
layer contains its own :term:`BitBake`
append files (``.bbappend``) and provides a convenient mechanism to
create your own recipe files (``.bb``) as well as store and use kernel
patch files. For background information on working with layers, see the
":ref:`dev-manual/dev-manual-common-tasks:understanding and creating layers`"
section in the Yocto Project Development Tasks Manual.

.. note::

   The Yocto Project comes with many tools that simplify tasks you need
   to perform. One such tool is the
   bitbake-layers create-layer
   command, which simplifies creating a new layer. See the "
   Creating a General Layer Using the
   bitbake-layers
   Script
   " section in the Yocto Project Development Tasks Manual for
   information on how to use this script to quick set up a new layer.

To better understand the layer you create for kernel development, the
following section describes how to create a layer without the aid of
tools. These steps assume creation of a layer named ``mylayer`` in your
home directory:

1. *Create Structure*: Create the layer's structure:
   ::

      $ cd $HOME
      $ mkdir meta-mylayer
      $ mkdir meta-mylayer/conf
      $ mkdir meta-mylayer/recipes-kernel
      $ mkdir meta-mylayer/recipes-kernel/linux
      $ mkdir meta-mylayer/recipes-kernel/linux/linux-yocto

   The ``conf`` directory holds your configuration files, while the
   ``recipes-kernel`` directory holds your append file and eventual
   patch files.

2. *Create the Layer Configuration File*: Move to the
   ``meta-mylayer/conf`` directory and create the ``layer.conf`` file as
   follows:
   ::

      # We have a conf and classes directory, add to BBPATH
      BBPATH .= ":${LAYERDIR}"

      # We have recipes-* directories, add to BBFILES
      BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
                  ${LAYERDIR}/recipes-*/*/*.bbappend"

      BBFILE_COLLECTIONS += "mylayer"
      BBFILE_PATTERN_mylayer = "^${LAYERDIR}/"
      BBFILE_PRIORITY_mylayer = "5"

   Notice ``mylayer`` as part of the last three statements.

3. *Create the Kernel Recipe Append File*: Move to the
   ``meta-mylayer/recipes-kernel/linux`` directory and create the
   kernel's append file. This example uses the ``linux-yocto-4.12``
   kernel. Thus, the name of the append file is
   ``linux-yocto_4.12.bbappend``:
   ::

      FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

      SRC_URI_append = " file://patch-file-one"
      SRC_URI_append = " file://patch-file-two"
      SRC_URI_append = " file://patch-file-three"

   The :term:`FILESEXTRAPATHS` and :term:`SRC_URI` statements
   enable the OpenEmbedded build system to find patch files. For more
   information on using append files, see the
   ":ref:`dev-manual/dev-manual-common-tasks:using .bbappend files in your layer`"
   section in the Yocto Project Development Tasks Manual.

Modifying an Existing Recipe
============================

In many cases, you can customize an existing linux-yocto recipe to meet
the needs of your project. Each release of the Yocto Project provides a
few Linux kernel recipes from which you can choose. These are located in
the :term:`Source Directory` in
``meta/recipes-kernel/linux``.

Modifying an existing recipe can consist of the following:

-  Creating the append file

-  Applying patches

-  Changing the configuration

Before modifying an existing recipe, be sure that you have created a
minimal, custom layer from which you can work. See the "`Creating and
Preparing a Layer <#creating-and-preparing-a-layer>`__" section for
information.

Creating the Append File
------------------------

You create this file in your custom layer. You also name it accordingly
based on the linux-yocto recipe you are using. For example, if you are
modifying the ``meta/recipes-kernel/linux/linux-yocto_4.12.bb`` recipe,
the append file will typically be located as follows within your custom
layer:
::

   your-layer/recipes-kernel/linux/linux-yocto_4.12.bbappend

The append file should initially extend the
:term:`FILESPATH` search path by
prepending the directory that contains your files to the
:term:`FILESEXTRAPATHS`
variable as follows:
::

   FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

The path ``${``\ :term:`THISDIR`\ ``}/${``\ :term:`PN`\ ``}``
expands to "linux-yocto" in the current directory for this example. If
you add any new files that modify the kernel recipe and you have
extended ``FILESPATH`` as described above, you must place the files in
your layer in the following area:
::

   your-layer/recipes-kernel/linux/linux-yocto/

.. note::

   If you are working on a new machine Board Support Package (BSP), be
   sure to refer to the
   Yocto Project Board Support Package (BSP) Developer's Guide
   .

As an example, consider the following append file used by the BSPs in
``meta-yocto-bsp``:
::

   meta-yocto-bsp/recipes-kernel/linux/linux-yocto_4.12.bbappend

The following listing shows the file. Be aware that the actual commit ID
strings in this example listing might be different than the actual
strings in the file from the ``meta-yocto-bsp`` layer upstream.
::

   KBRANCH_genericx86  = "standard/base"
   KBRANCH_genericx86-64  = "standard/base"

   KMACHINE_genericx86 ?= "common-pc"
   KMACHINE_genericx86-64 ?= "common-pc-64"
   KBRANCH_edgerouter = "standard/edgerouter"
   KBRANCH_beaglebone = "standard/beaglebone"

   SRCREV_machine_genericx86    ?= "d09f2ce584d60ecb7890550c22a80c48b83c2e19"
   SRCREV_machine_genericx86-64 ?= "d09f2ce584d60ecb7890550c22a80c48b83c2e19"
   SRCREV_machine_edgerouter ?= "b5c8cfda2dfe296410d51e131289fb09c69e1e7d"
   SRCREV_machine_beaglebone ?= "b5c8cfda2dfe296410d51e131289fb09c69e1e7d"


   COMPATIBLE_MACHINE_genericx86 = "genericx86"
   COMPATIBLE_MACHINE_genericx86-64 = "genericx86-64"
   COMPATIBLE_MACHINE_edgerouter = "edgerouter"
   COMPATIBLE_MACHINE_beaglebone = "beaglebone"

   LINUX_VERSION_genericx86 = "4.12.7"
   LINUX_VERSION_genericx86-64 = "4.12.7"
   LINUX_VERSION_edgerouter = "4.12.10"
   LINUX_VERSION_beaglebone = "4.12.10"

This append file
contains statements used to support several BSPs that ship with the
Yocto Project. The file defines machines using the
:term:`COMPATIBLE_MACHINE`
variable and uses the
:term:`KMACHINE` variable to ensure
the machine name used by the OpenEmbedded build system maps to the
machine name used by the Linux Yocto kernel. The file also uses the
optional :term:`KBRANCH` variable to
ensure the build process uses the appropriate kernel branch.

Although this particular example does not use it, the
:term:`KERNEL_FEATURES`
variable could be used to enable features specific to the kernel. The
append file points to specific commits in the
:term:`Source Directory` Git repository and
the ``meta`` Git repository branches to identify the exact kernel needed
to build the BSP.

One thing missing in this particular BSP, which you will typically need
when developing a BSP, is the kernel configuration file (``.config``)
for your BSP. When developing a BSP, you probably have a kernel
configuration file or a set of kernel configuration files that, when
taken together, define the kernel configuration for your BSP. You can
accomplish this definition by putting the configurations in a file or a
set of files inside a directory located at the same level as your
kernel's append file and having the same name as the kernel's main
recipe file. With all these conditions met, simply reference those files
in the :term:`SRC_URI` statement in
the append file.

For example, suppose you had some configuration options in a file called
``network_configs.cfg``. You can place that file inside a directory
named ``linux-yocto`` and then add a ``SRC_URI`` statement such as the
following to the append file. When the OpenEmbedded build system builds
the kernel, the configuration options are picked up and applied.
::

   SRC_URI += "file://network_configs.cfg"

To group related configurations into multiple files, you perform a
similar procedure. Here is an example that groups separate
configurations specifically for Ethernet and graphics into their own
files and adds the configurations by using a ``SRC_URI`` statement like
the following in your append file:
::

   SRC_URI += "file://myconfig.cfg \
               file://eth.cfg \
               file://gfx.cfg"

Another variable you can use in your kernel recipe append file is the
:term:`FILESEXTRAPATHS`
variable. When you use this statement, you are extending the locations
used by the OpenEmbedded system to look for files and patches as the
recipe is processed.

.. note::

   Other methods exist to accomplish grouping and defining configuration
   options. For example, if you are working with a local clone of the
   kernel repository, you could checkout the kernel's ``meta`` branch,
   make your changes, and then push the changes to the local bare clone
   of the kernel. The result is that you directly add configuration
   options to the ``meta`` branch for your BSP. The configuration
   options will likely end up in that location anyway if the BSP gets
   added to the Yocto Project.

   In general, however, the Yocto Project maintainers take care of
   moving the ``SRC_URI``-specified configuration options to the
   kernel's ``meta`` branch. Not only is it easier for BSP developers to
   not have to worry about putting those configurations in the branch,
   but having the maintainers do it allows them to apply 'global'
   knowledge about the kinds of common configuration options multiple
   BSPs in the tree are typically using. This allows for promotion of
   common configurations into common features.

Applying Patches
----------------

If you have a single patch or a small series of patches that you want to
apply to the Linux kernel source, you can do so just as you would with
any other recipe. You first copy the patches to the path added to
:term:`FILESEXTRAPATHS` in
your ``.bbappend`` file as described in the previous section, and then
reference them in :term:`SRC_URI`
statements.

For example, you can apply a three-patch series by adding the following
lines to your linux-yocto ``.bbappend`` file in your layer:
::

   SRC_URI += "file://0001-first-change.patch"
   SRC_URI += "file://0002-second-change.patch"
   SRC_URI += "file://0003-third-change.patch"

The next time you run BitBake to build
the Linux kernel, BitBake detects the change in the recipe and fetches
and applies the patches before building the kernel.

For a detailed example showing how to patch the kernel using
``devtool``, see the
":ref:`kernel-dev/kernel-dev-common:using \`\`devtool\`\` to patch the kernel`"
and
":ref:`kernel-dev/kernel-dev-common:using traditional kernel development to patch the kernel`"
sections.

Changing the Configuration
--------------------------

You can make wholesale or incremental changes to the final ``.config``
file used for the eventual Linux kernel configuration by including a
``defconfig`` file and by specifying configuration fragments in the
:term:`SRC_URI` to be applied to that
file.

If you have a complete, working Linux kernel ``.config`` file you want
to use for the configuration, as before, copy that file to the
appropriate ``${PN}`` directory in your layer's ``recipes-kernel/linux``
directory, and rename the copied file to "defconfig". Then, add the
following lines to the linux-yocto ``.bbappend`` file in your layer:
::

   FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
   SRC_URI += "file://defconfig"

The ``SRC_URI`` tells the build system how to search
for the file, while the
:term:`FILESEXTRAPATHS`
extends the :term:`FILESPATH`
variable (search directories) to include the ``${PN}`` directory you
created to hold the configuration changes.

.. note::

   The build system applies the configurations from the
   defconfig
   file before applying any subsequent configuration fragments. The
   final kernel configuration is a combination of the configurations in
   the
   defconfig
   file and any configuration fragments you provide. You need to realize
   that if you have any configuration fragments, the build system
   applies these on top of and after applying the existing
   defconfig
   file configurations.

Generally speaking, the preferred approach is to determine the
incremental change you want to make and add that as a configuration
fragment. For example, if you want to add support for a basic serial
console, create a file named ``8250.cfg`` in the ``${PN}`` directory
with the following content (without indentation):
::

   CONFIG_SERIAL_8250=y
   CONFIG_SERIAL_8250_CONSOLE=y
   CONFIG_SERIAL_8250_PCI=y
   CONFIG_SERIAL_8250_NR_UARTS=4
   CONFIG_SERIAL_8250_RUNTIME_UARTS=4
   CONFIG_SERIAL_CORE=y
   CONFIG_SERIAL_CORE_CONSOLE=y

Next, include this
configuration fragment and extend the ``FILESPATH`` variable in your
``.bbappend`` file:
::

   FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
   SRC_URI += "file://8250.cfg"

The next time you run BitBake to build the
Linux kernel, BitBake detects the change in the recipe and fetches and
applies the new configuration before building the kernel.

For a detailed example showing how to configure the kernel, see the
"`Configuring the Kernel <#configuring-the-kernel>`__" section.

Using an "In-Tree"  ``defconfig`` File
--------------------------------------

It might be desirable to have kernel configuration fragment support
through a ``defconfig`` file that is pulled from the kernel source tree
for the configured machine. By default, the OpenEmbedded build system
looks for ``defconfig`` files in the layer used for Metadata, which is
"out-of-tree", and then configures them using the following:
::

   SRC_URI += "file://defconfig"

If you do not want to maintain copies of
``defconfig`` files in your layer but would rather allow users to use
the default configuration from the kernel tree and still be able to add
configuration fragments to the
:term:`SRC_URI` through, for example,
append files, you can direct the OpenEmbedded build system to use a
``defconfig`` file that is "in-tree".

To specify an "in-tree" ``defconfig`` file, use the following statement
form:
::

   KBUILD_DEFCONFIG_KMACHINE ?= defconfig_file

Here is an example
that assigns the ``KBUILD_DEFCONFIG`` variable based on "raspberrypi2"
and provides the path to the "in-tree" ``defconfig`` file to be used for
a Raspberry Pi 2, which is based on the Broadcom 2708/2709 chipset:
::

   KBUILD_DEFCONFIG_raspberrypi2 ?= "bcm2709_defconfig"

Aside from modifying your kernel recipe and providing your own
``defconfig`` file, you need to be sure no files or statements set
``SRC_URI`` to use a ``defconfig`` other than your "in-tree" file (e.g.
a kernel's ``linux-``\ machine\ ``.inc`` file). In other words, if the
build system detects a statement that identifies an "out-of-tree"
``defconfig`` file, that statement will override your
``KBUILD_DEFCONFIG`` variable.

See the
:term:`KBUILD_DEFCONFIG`
variable description for more information.

Using ``devtool`` to Patch the Kernel
=====================================

The steps in this procedure show you how you can patch the kernel using
the extensible SDK and ``devtool``.

.. note::

   Before attempting this procedure, be sure you have performed the
   steps to get ready for updating the kernel as described in the "
   Getting Ready to Develop Using
   devtool
   " section.

Patching the kernel involves changing or adding configurations to an
existing kernel, changing or adding recipes to the kernel that are
needed to support specific hardware features, or even altering the
source code itself.

This example creates a simple patch by adding some QEMU emulator console
output at boot time through ``printk`` statements in the kernel's
``calibrate.c`` source code file. Applying the patch and booting the
modified image causes the added messages to appear on the emulator's
console. The example is a continuation of the setup procedure found in
the ":ref:`kernel-dev/kernel-dev-common:getting ready to develop using \`\`devtool\`\``" Section.

1. *Check Out the Kernel Source Files:* First you must use ``devtool``
   to checkout the kernel source code in its workspace. Be sure you are
   in the terminal set up to do work with the extensible SDK.

   .. note::

      See this
      step
      in the "
      Getting Ready to Develop Using
      devtool
      " section for more information.

   Use the following ``devtool`` command to check out the code:
   ::

      $ devtool modify linux-yocto

   .. note::

      During the checkout operation, a bug exists that could cause
      errors such as the following to appear:
      ::

              ERROR: Taskhash mismatch 2c793438c2d9f8c3681fd5f7bc819efa versus
                     be3a89ce7c47178880ba7bf6293d7404 for
                     /path/to/esdk/layers/poky/meta/recipes-kernel/linux/linux-yocto_4.10.bb.do_unpack


      You can safely ignore these messages. The source code is correctly
      checked out.

2. *Edit the Source Files* Follow these steps to make some simple
   changes to the source files:

   1. *Change the working directory*: In the previous step, the output
      noted where you can find the source files (e.g.
      ``~/poky_sdk/workspace/sources/linux-yocto``). Change to where the
      kernel source code is before making your edits to the
      ``calibrate.c`` file:
      ::

         $ cd ~/poky_sdk/workspace/sources/linux-yocto

   2. *Edit the source file*: Edit the ``init/calibrate.c`` file to have
      the following changes:
      ::

         void calibrate_delay(void)
         {
             unsigned long lpj;
             static bool printed;
             int this_cpu = smp_processor_id();

             printk("*************************************\n");
             printk("*                                   *\n");
             printk("*        HELLO YOCTO KERNEL         *\n");
             printk("*                                   *\n");
             printk("*************************************\n");

             if (per_cpu(cpu_loops_per_jiffy, this_cpu)) {
                   .
                   .
                   .

3. *Build the Updated Kernel Source:* To build the updated kernel
   source, use ``devtool``:
   ::

      $ devtool build linux-yocto

4. *Create the Image With the New Kernel:* Use the
   ``devtool build-image`` command to create a new image that has the
   new kernel.

   .. note::

      If the image you originally created resulted in a Wic file, you
      can use an alternate method to create the new image with the
      updated kernel. For an example, see the steps in the
      TipsAndTricks/KernelDevelopmentWithEsdk
      Wiki Page.

   ::

      $ cd ~
      $ devtool build-image core-image-minimal

5. *Test the New Image:* For this example, you can run the new image
   using QEMU to verify your changes:

   1. *Boot the image*: Boot the modified image in the QEMU emulator
      using this command:
      ::

         $ runqemu qemux86

   2. *Verify the changes*: Log into the machine using ``root`` with no
      password and then use the following shell command to scroll
      through the console's boot output.
      ::

         # dmesg | less

      You should see
      the results of your ``printk`` statements as part of the output
      when you scroll down the console window.

6. *Stage and commit your changes*: Within your eSDK terminal, change
   your working directory to where you modified the ``calibrate.c`` file
   and use these Git commands to stage and commit your changes:
   ::

      $ cd ~/poky_sdk/workspace/sources/linux-yocto
      $ git status
      $ git add init/calibrate.c
      $ git commit -m "calibrate: Add printk example"

7. *Export the Patches and Create an Append File:* To export your
   commits as patches and create a ``.bbappend`` file, use the following
   command in the terminal used to work with the extensible SDK. This
   example uses the previously established layer named ``meta-mylayer``.

   .. note::

      See Step 3 of the "
      Getting Ready to Develop Using devtool
      " section for information on setting up this layer.

   $ devtool finish linux-yocto ~/meta-mylayer

   Once the command
   finishes, the patches and the ``.bbappend`` file are located in the
   ``~/meta-mylayer/recipes-kernel/linux`` directory.

8. *Build the Image With Your Modified Kernel:* You can now build an
   image that includes your kernel patches. Execute the following
   command from your
   :term:`Build Directory` in the terminal
   set up to run BitBake:
   ::

      $ cd ~/poky/build
      $ bitbake core-image-minimal

Using Traditional Kernel Development to Patch the Kernel
========================================================

The steps in this procedure show you how you can patch the kernel using
traditional kernel development (i.e. not using ``devtool`` and the
extensible SDK as described in the
":ref:`kernel-dev/kernel-dev-common:using \`\`devtool\`\` to patch the kernel`"
section).

.. note::

   Before attempting this procedure, be sure you have performed the
   steps to get ready for updating the kernel as described in the "
   Getting Ready for Traditional Kernel Development
   " section.

Patching the kernel involves changing or adding configurations to an
existing kernel, changing or adding recipes to the kernel that are
needed to support specific hardware features, or even altering the
source code itself.

The example in this section creates a simple patch by adding some QEMU
emulator console output at boot time through ``printk`` statements in
the kernel's ``calibrate.c`` source code file. Applying the patch and
booting the modified image causes the added messages to appear on the
emulator's console. The example is a continuation of the setup procedure
found in the "`Getting Ready for Traditional Kernel
Development <#getting-ready-for-traditional-kernel-development>`__"
Section.

1. *Edit the Source Files* Prior to this step, you should have used Git
   to create a local copy of the repository for your kernel. Assuming
   you created the repository as directed in the "`Getting Ready for
   Traditional Kernel
   Development <#getting-ready-for-traditional-kernel-development>`__"
   section, use the following commands to edit the ``calibrate.c`` file:

   1. *Change the working directory*: You need to locate the source
      files in the local copy of the kernel Git repository: Change to
      where the kernel source code is before making your edits to the
      ``calibrate.c`` file:
      ::

         $ cd ~/linux-yocto-4.12/init

   2. *Edit the source file*: Edit the ``calibrate.c`` file to have the
      following changes:
      ::

         void calibrate_delay(void)
         {
             unsigned long lpj;
             static bool printed;
             int this_cpu = smp_processor_id();

             printk("*************************************\n");
             printk("*                                   *\n");
             printk("*        HELLO YOCTO KERNEL         *\n");
             printk("*                                   *\n");
             printk("*************************************\n");

             if (per_cpu(cpu_loops_per_jiffy, this_cpu)) {
                   .
                   .
                   .

2. *Stage and Commit Your Changes:* Use standard Git commands to stage
   and commit the changes you just made:
   ::

      $ git add calibrate.c
      $ git commit -m "calibrate.c - Added some printk statements"

   If you do not
   stage and commit your changes, the OpenEmbedded Build System will not
   pick up the changes.

3. *Update Your local.conf File to Point to Your Source Files:* In
   addition to your ``local.conf`` file specifying to use
   "kernel-modules" and the "qemux86" machine, it must also point to the
   updated kernel source files. Add
   :term:`SRC_URI` and
   :term:`SRCREV` statements similar
   to the following to your ``local.conf``:
   ::

      $ cd ~/poky/build/conf

   Add the following to the ``local.conf``:
   ::

      SRC_URI_pn-linux-yocto = "git:///path-to/linux-yocto-4.12;protocol=file;name=machine;branch=standard/base; \
                                git:///path-to/yocto-kernel-cache;protocol=file;type=kmeta;name=meta;branch=yocto-4.12;destsuffix=${KMETA}"
      SRCREV_meta_qemux86 = "${AUTOREV}"
      SRCREV_machine_qemux86 = "${AUTOREV}"

   .. note::

      Be sure to replace
      path-to
      with the pathname to your local Git repositories. Also, you must
      be sure to specify the correct branch and machine types. For this
      example, the branch is
      standard/base
      and the machine is "qemux86".

4. *Build the Image:* With the source modified, your changes staged and
   committed, and the ``local.conf`` file pointing to the kernel files,
   you can now use BitBake to build the image:
   ::

      $ cd ~/poky/build
      $ bitbake core-image-minimal

5. *Boot the image*: Boot the modified image in the QEMU emulator using
   this command. When prompted to login to the QEMU console, use "root"
   with no password:
   ::

      $ cd ~/poky/build
      $ runqemu qemux86

6. *Look for Your Changes:* As QEMU booted, you might have seen your
   changes rapidly scroll by. If not, use these commands to see your
   changes:
   ::

      # dmesg | less

   You should see the results of your
   ``printk`` statements as part of the output when you scroll down the
   console window.

7. *Generate the Patch File:* Once you are sure that your patch works
   correctly, you can generate a ``*.patch`` file in the kernel source
   repository:
   ::

      $ cd ~/linux-yocto-4.12/init
      $ git format-patch -1
      0001-calibrate.c-Added-some-printk-statements.patch

8. *Move the Patch File to Your Layer:* In order for subsequent builds
   to pick up patches, you need to move the patch file you created in
   the previous step to your layer ``meta-mylayer``. For this example,
   the layer created earlier is located in your home directory as
   ``meta-mylayer``. When the layer was created using the
   ``yocto-create`` script, no additional hierarchy was created to
   support patches. Before moving the patch file, you need to add
   additional structure to your layer using the following commands:
   ::

      $ cd ~/meta-mylayer
      $ mkdir recipes-kernel
      $ mkdir recipes-kernel/linux
      $ mkdir recipes-kernel/linux/linux-yocto

   Once you have created this
   hierarchy in your layer, you can move the patch file using the
   following command:
   ::

      $ mv ~/linux-yocto-4.12/init/0001-calibrate.c-Added-some-printk-statements.patch ~/meta-mylayer/recipes-kernel/linux/linux-yocto

9. *Create the Append File:* Finally, you need to create the
   ``linux-yocto_4.12.bbappend`` file and insert statements that allow
   the OpenEmbedded build system to find the patch. The append file
   needs to be in your layer's ``recipes-kernel/linux`` directory and it
   must be named ``linux-yocto_4.12.bbappend`` and have the following
   contents:
   ::

      FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
      SRC_URI_append = "file://0001-calibrate.c-Added-some-printk-statements.patch"

   The :term:`FILESEXTRAPATHS` and :term:`SRC_URI` statements
   enable the OpenEmbedded build system to find the patch file.

   For more information on append files and patches, see the "`Creating
   the Append File <#creating-the-append-file>`__" and "`Applying
   Patches <#applying-patches>`__" sections. You can also see the
   ":ref:`dev-manual/dev-manual-common-tasks:using .bbappend files in your layer`"
   section in the Yocto Project Development Tasks Manual.

   .. note::

      To build
      core-image-minimal
      again and see the effects of your patch, you can essentially
      eliminate the temporary source files saved in
      poky/build/tmp/work/...
      and residual effects of the build by entering the following
      sequence of commands:
      ::

              $ cd ~/poky/build
              $ bitbake -c cleanall yocto-linux
              $ bitbake core-image-minimal -c cleanall
              $ bitbake core-image-minimal
              $ runqemu qemux86


Configuring the Kernel
======================

Configuring the Yocto Project kernel consists of making sure the
``.config`` file has all the right information in it for the image you
are building. You can use the ``menuconfig`` tool and configuration
fragments to make sure your ``.config`` file is just how you need it.
You can also save known configurations in a ``defconfig`` file that the
build system can use for kernel configuration.

This section describes how to use ``menuconfig``, create and use
configuration fragments, and how to interactively modify your
``.config`` file to create the leanest kernel configuration file
possible.

For more information on kernel configuration, see the "`Changing the
Configuration <#changing-the-configuration>`__" section.

Using  ``menuconfig``
---------------------

The easiest way to define kernel configurations is to set them through
the ``menuconfig`` tool. This tool provides an interactive method with
which to set kernel configurations. For general information on
``menuconfig``, see http://en.wikipedia.org/wiki/Menuconfig.

To use the ``menuconfig`` tool in the Yocto Project development
environment, you must do the following:

-  Because you launch ``menuconfig`` using BitBake, you must be sure to
   set up your environment by running the
   :ref:`structure-core-script` script found in
   the :term:`Build Directory`.

-  You must be sure of the state of your build's configuration in the
   :term:`Source Directory`.

-  Your build host must have the following two packages installed:
   ::

      libncurses5-dev
      libtinfo-dev

The following commands initialize the BitBake environment, run the
:ref:`ref-tasks-kernel_configme`
task, and launch ``menuconfig``. These commands assume the Source
Directory's top-level folder is ``~/poky``:
::

   $ cd poky
   $ source oe-init-build-env
   $ bitbake linux-yocto -c kernel_configme -f
   $ bitbake linux-yocto -c menuconfig

Once ``menuconfig`` comes up, its standard
interface allows you to interactively examine and configure all the
kernel configuration parameters. After making your changes, simply exit
the tool and save your changes to create an updated version of the
``.config`` configuration file.

.. note::

   You can use the entire
   .config
   file as the
   defconfig
   file. For information on
   defconfig
   files, see the "
   Changing the Configuration
   ", "
   Using an In-Tree
   defconfig
   File
   , and "
   Creating a
   defconfig
   File
   " sections.

Consider an example that configures the "CONFIG_SMP" setting for the
``linux-yocto-4.12`` kernel.

.. note::

   The OpenEmbedded build system recognizes this kernel as
   linux-yocto
   through Metadata (e.g.
   PREFERRED_VERSION
   \_linux-yocto ?= "12.4%"
   ).

Once ``menuconfig`` launches, use the interface to navigate through the
selections to find the configuration settings in which you are
interested. For this example, you deselect "CONFIG_SMP" by clearing the
"Symmetric Multi-Processing Support" option. Using the interface, you
can find the option under "Processor Type and Features". To deselect
"CONFIG_SMP", use the arrow keys to highlight "Symmetric
Multi-Processing Support" and enter "N" to clear the asterisk. When you
are finished, exit out and save the change.

Saving the selections updates the ``.config`` configuration file. This
is the file that the OpenEmbedded build system uses to configure the
kernel during the build. You can find and examine this file in the Build
Directory in ``tmp/work/``. The actual ``.config`` is located in the
area where the specific kernel is built. For example, if you were
building a Linux Yocto kernel based on the ``linux-yocto-4.12`` kernel
and you were building a QEMU image targeted for ``x86`` architecture,
the ``.config`` file would be:
::

   poky/build/tmp/work/qemux86-poky-linux/linux-yocto/4.12.12+gitAUTOINC+eda4d18...
   ...967-r0/linux-qemux86-standard-build/.config

.. note::

   The previous example directory is artificially split and many of the
   characters in the actual filename are omitted in order to make it
   more readable. Also, depending on the kernel you are using, the exact
   pathname might differ.

Within the ``.config`` file, you can see the kernel settings. For
example, the following entry shows that symmetric multi-processor
support is not set:
::

   # CONFIG_SMP is not set

A good method to isolate changed configurations is to use a combination
of the ``menuconfig`` tool and simple shell commands. Before changing
configurations with ``menuconfig``, copy the existing ``.config`` and
rename it to something else, use ``menuconfig`` to make as many changes
as you want and save them, then compare the renamed configuration file
against the newly created file. You can use the resulting differences as
your base to create configuration fragments to permanently save in your
kernel layer.

.. note::

   Be sure to make a copy of the
   .config
   file and do not just rename it. The build system needs an existing
   .config
   file from which to work.

Creating a  ``defconfig`` File
------------------------------

A ``defconfig`` file in the context of the Yocto Project is often a
``.config`` file that is copied from a build or a ``defconfig`` taken
from the kernel tree and moved into recipe space. You can use a
``defconfig`` file to retain a known set of kernel configurations from
which the OpenEmbedded build system can draw to create the final
``.config`` file.

.. note::

   Out-of-the-box, the Yocto Project never ships a
   defconfig
   or
   .config
   file. The OpenEmbedded build system creates the final
   .config
   file used to configure the kernel.

To create a ``defconfig``, start with a complete, working Linux kernel
``.config`` file. Copy that file to the appropriate
``${``\ :term:`PN`\ ``}`` directory in
your layer's ``recipes-kernel/linux`` directory, and rename the copied
file to "defconfig" (e.g.
``~/meta-mylayer/recipes-kernel/linux/linux-yocto/defconfig``). Then,
add the following lines to the linux-yocto ``.bbappend`` file in your
layer:
::

   FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
   SRC_URI += "file://defconfig"

The :term:`SRC_URI` tells the build system how to search for the file, while the
:term:`FILESEXTRAPATHS` extends the :term:`FILESPATH`
variable (search directories) to include the ``${PN}`` directory you
created to hold the configuration changes.

.. note::

   The build system applies the configurations from the
   defconfig
   file before applying any subsequent configuration fragments. The
   final kernel configuration is a combination of the configurations in
   the
   defconfig
   file and any configuration fragments you provide. You need to realize
   that if you have any configuration fragments, the build system
   applies these on top of and after applying the existing defconfig
   file configurations.

For more information on configuring the kernel, see the "`Changing the
Configuration <#changing-the-configuration>`__" section.

.. _creating-config-fragments:

Creating Configuration Fragments
--------------------------------

Configuration fragments are simply kernel options that appear in a file
placed where the OpenEmbedded build system can find and apply them. The
build system applies configuration fragments after applying
configurations from a ``defconfig`` file. Thus, the final kernel
configuration is a combination of the configurations in the
``defconfig`` file and then any configuration fragments you provide. The
build system applies fragments on top of and after applying the existing
defconfig file configurations.

Syntactically, the configuration statement is identical to what would
appear in the ``.config`` file, which is in the :term:`Build Directory`.

.. note::

   For more information about where the
   .config
   file is located, see the example in the
   ":ref:`kernel-dev/kernel-dev-common:using \`\`menuconfig\`\``"
   section.

It is simple to create a configuration fragment. One method is to use
shell commands. For example, issuing the following from the shell
creates a configuration fragment file named ``my_smp.cfg`` that enables
multi-processor support within the kernel:
::

   $ echo "CONFIG_SMP=y" >> my_smp.cfg

.. note::

   All configuration fragment files must use the
   .cfg
   extension in order for the OpenEmbedded build system to recognize
   them as a configuration fragment.

Another method is to create a configuration fragment using the
differences between two configuration files: one previously created and
saved, and one freshly created using the ``menuconfig`` tool.

To create a configuration fragment using this method, follow these
steps:

1. *Complete a Build Through Kernel Configuration:* Complete a build at
   least through the kernel configuration task as follows:
   ::

      $ bitbake linux-yocto -c kernel_configme -f

   This step ensures that you create a
   ``.config`` file from a known state. Because situations exist where
   your build state might become unknown, it is best to run this task
   prior to starting ``menuconfig``.

2. *Launch menuconfig:* Run the ``menuconfig`` command:
   ::

      $ bitbake linux-yocto -c menuconfig

3. *Create the Configuration Fragment:* Run the ``diffconfig`` command
   to prepare a configuration fragment. The resulting file
   ``fragment.cfg`` is placed in the
   ``${``\ :term:`WORKDIR`\ ``}``
   directory:
   ::

      $ bitbake linux-yocto -c diffconfig

The ``diffconfig`` command creates a file that is a list of Linux kernel
``CONFIG_`` assignments. See the "`Changing the
Configuration <#changing-the-configuration>`__" section for additional
information on how to use the output as a configuration fragment.

.. note::

   You can also use this method to create configuration fragments for a
   BSP. See the "
   BSP Descriptions
   " section for more information.

Where do you put your configuration fragment files? You can place these
files in an area pointed to by
:term:`SRC_URI` as directed by your
``bblayers.conf`` file, which is located in your layer. The OpenEmbedded
build system picks up the configuration and adds it to the kernel's
configuration. For example, suppose you had a set of configuration
options in a file called ``myconfig.cfg``. If you put that file inside a
directory named ``linux-yocto`` that resides in the same directory as
the kernel's append file within your layer and then add the following
statements to the kernel's append file, those configuration options will
be picked up and applied when the kernel is built:
::

   FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
   SRC_URI += "file://myconfig.cfg"

As mentioned earlier, you can group related configurations into multiple
files and name them all in the ``SRC_URI`` statement as well. For
example, you could group separate configurations specifically for
Ethernet and graphics into their own files and add those by using a
``SRC_URI`` statement like the following in your append file:
::

   SRC_URI += "file://myconfig.cfg \
               file://eth.cfg \
               file://gfx.cfg"

Validating Configuration
------------------------

You can use the
:ref:`ref-tasks-kernel_configcheck`
task to provide configuration validation:
::

   $ bitbake linux-yocto -c kernel_configcheck -f

Running this task produces warnings for when a
requested configuration does not appear in the final ``.config`` file or
when you override a policy configuration in a hardware configuration
fragment.

In order to run this task, you must have an existing ``.config`` file.
See the ":ref:`kernel-dev/kernel-dev-common:using \`\`menuconfig\`\``" section for
information on how to create a configuration file.

Following is sample output from the ``do_kernel_configcheck`` task:
::

   Loading cache: 100% |########################################################| Time: 0:00:00
   Loaded 1275 entries from dependency cache.
   NOTE: Resolving any missing task queue dependencies

   Build Configuration:
       .
       .
       .

   NOTE: Executing SetScene Tasks
   NOTE: Executing RunQueue Tasks
   WARNING: linux-yocto-4.12.12+gitAUTOINC+eda4d18ce4_16de014967-r0 do_kernel_configcheck:
       [kernel config]: specified values did not make it into the kernel's final configuration:

   ---------- CONFIG_X86_TSC -----------------
   Config: CONFIG_X86_TSC
   From: /home/scottrif/poky/build/tmp/work-shared/qemux86/kernel-source/.kernel-meta/configs/standard/bsp/common-pc/common-pc-cpu.cfg
   Requested value:  CONFIG_X86_TSC=y
   Actual value:


   ---------- CONFIG_X86_BIGSMP -----------------
   Config: CONFIG_X86_BIGSMP
   From: /home/scottrif/poky/build/tmp/work-shared/qemux86/kernel-source/.kernel-meta/configs/standard/cfg/smp.cfg
         /home/scottrif/poky/build/tmp/work-shared/qemux86/kernel-source/.kernel-meta/configs/standard/defconfig
   Requested value:  # CONFIG_X86_BIGSMP is not set
   Actual value:


   ---------- CONFIG_NR_CPUS -----------------
   Config: CONFIG_NR_CPUS
   From: /home/scottrif/poky/build/tmp/work-shared/qemux86/kernel-source/.kernel-meta/configs/standard/cfg/smp.cfg
         /home/scottrif/poky/build/tmp/work-shared/qemux86/kernel-source/.kernel-meta/configs/standard/bsp/common-pc/common-pc.cfg
         /home/scottrif/poky/build/tmp/work-shared/qemux86/kernel-source/.kernel-meta/configs/standard/defconfig
   Requested value:  CONFIG_NR_CPUS=8
   Actual value:     CONFIG_NR_CPUS=1


   ---------- CONFIG_SCHED_SMT -----------------
   Config: CONFIG_SCHED_SMT
   From: /home/scottrif/poky/build/tmp/work-shared/qemux86/kernel-source/.kernel-meta/configs/standard/cfg/smp.cfg
         /home/scottrif/poky/build/tmp/work-shared/qemux86/kernel-source/.kernel-meta/configs/standard/defconfig
   Requested value:  CONFIG_SCHED_SMT=y
   Actual value:



   NOTE: Tasks Summary: Attempted 288 tasks of which 285 didn't need to be rerun and all succeeded.

   Summary: There were 3 WARNING messages shown.

.. note::

   The previous output example has artificial line breaks to make it
   more readable.

The output describes the various problems that you can encounter along
with where to find the offending configuration items. You can use the
information in the logs to adjust your configuration files and then
repeat the
:ref:`ref-tasks-kernel_configme`
and
:ref:`ref-tasks-kernel_configcheck`
tasks until they produce no warnings.

For more information on how to use the ``menuconfig`` tool, see the
:ref:`kernel-dev/kernel-dev-common:using \`\`menuconfig\`\`` section.

Fine-Tuning the Kernel Configuration File
-----------------------------------------

You can make sure the ``.config`` file is as lean or efficient as
possible by reading the output of the kernel configuration fragment
audit, noting any issues, making changes to correct the issues, and then
repeating.

As part of the kernel build process, the ``do_kernel_configcheck`` task
runs. This task validates the kernel configuration by checking the final
``.config`` file against the input files. During the check, the task
produces warning messages for the following issues:

-  Requested options that did not make the final ``.config`` file.

-  Configuration items that appear twice in the same configuration
   fragment.

-  Configuration items tagged as "required" that were overridden.

-  A board overrides a non-board specific option.

-  Listed options not valid for the kernel being processed. In other
   words, the option does not appear anywhere.

.. note::

   The
   do_kernel_configcheck
   task can also optionally report if an option is overridden during
   processing.

For each output warning, a message points to the file that contains a
list of the options and a pointer to the configuration fragment that
defines them. Collectively, the files are the key to streamlining the
configuration.

To streamline the configuration, do the following:

1. *Use a Working Configuration:* Start with a full configuration that
   you know works. Be sure the configuration builds and boots
   successfully. Use this configuration file as your baseline.

2. *Run Configure and Check Tasks:* Separately run the
   ``do_kernel_configme`` and ``do_kernel_configcheck`` tasks:
   ::

      $ bitbake linux-yocto -c kernel_configme -f
      $ bitbake linux-yocto -c kernel_configcheck -f

3. *Process the Results:* Take the resulting list of files from the
   ``do_kernel_configcheck`` task warnings and do the following:

   -  Drop values that are redefined in the fragment but do not change
      the final ``.config`` file.

   -  Analyze and potentially drop values from the ``.config`` file that
      override required configurations.

   -  Analyze and potentially remove non-board specific options.

   -  Remove repeated and invalid options.

4. *Re-Run Configure and Check Tasks:* After you have worked through the
   output of the kernel configuration audit, you can re-run the
   ``do_kernel_configme`` and ``do_kernel_configcheck`` tasks to see the
   results of your changes. If you have more issues, you can deal with
   them as described in the previous step.

Iteratively working through steps two through four eventually yields a
minimal, streamlined configuration file. Once you have the best
``.config``, you can build the Linux Yocto kernel.

Expanding Variables
===================

Sometimes it is helpful to determine what a variable expands to during a
build. You can do examine the values of variables by examining the
output of the ``bitbake -e`` command. The output is long and is more
easily managed in a text file, which allows for easy searches:
::

   $ bitbake -e virtual/kernel > some_text_file

Within the text file, you can see
exactly how each variable is expanded and used by the OpenEmbedded build
system.

Working with a "Dirty" Kernel Version String
============================================

If you build a kernel image and the version string has a "+" or a
"-dirty" at the end, uncommitted modifications exist in the kernel's
source directory. Follow these steps to clean up the version string:

1. *Discover the Uncommitted Changes:* Go to the kernel's locally cloned
   Git repository (source directory) and use the following Git command
   to list the files that have been changed, added, or removed:
   ::

      $ git status

2. *Commit the Changes:* You should commit those changes to the kernel
   source tree regardless of whether or not you will save, export, or
   use the changes:
   ::

      $ git add
      $ git commit -s -a -m "getting rid of -dirty"

3. *Rebuild the Kernel Image:* Once you commit the changes, rebuild the
   kernel.

   Depending on your particular kernel development workflow, the
   commands you use to rebuild the kernel might differ. For information
   on building the kernel image when using ``devtool``, see the
   ":ref:`kernel-dev/kernel-dev-common:using \`\`devtool\`\` to patch the kernel`"
   section. For
   information on building the kernel image when using Bitbake, see the
   "`Using Traditional Kernel Development to Patch the
   Kernel <#using-traditional-kernel-development-to-patch-the-kernel>`__"
   section.

Working With Your Own Sources
=============================

If you cannot work with one of the Linux kernel versions supported by
existing linux-yocto recipes, you can still make use of the Yocto
Project Linux kernel tooling by working with your own sources. When you
use your own sources, you will not be able to leverage the existing
kernel :term:`Metadata` and stabilization
work of the linux-yocto sources. However, you will be able to manage
your own Metadata in the same format as the linux-yocto sources.
Maintaining format compatibility facilitates converging with linux-yocto
on a future, mutually-supported kernel version.

To help you use your own sources, the Yocto Project provides a
linux-yocto custom recipe (``linux-yocto-custom.bb``) that uses
``kernel.org`` sources and the Yocto Project Linux kernel tools for
managing kernel Metadata. You can find this recipe in the ``poky`` Git
repository of the Yocto Project :yocto_git:`Source Repository <>`
at:
::

   poky/meta-skeleton/recipes-kernel/linux/linux-yocto-custom.bb

Here are some basic steps you can use to work with your own sources:

1. *Create a Copy of the Kernel Recipe:* Copy the
   ``linux-yocto-custom.bb`` recipe to your layer and give it a
   meaningful name. The name should include the version of the Yocto
   Linux kernel you are using (e.g. ``linux-yocto-myproject_4.12.bb``,
   where "4.12" is the base version of the Linux kernel with which you
   would be working).

2. *Create a Directory for Your Patches:* In the same directory inside
   your layer, create a matching directory to store your patches and
   configuration files (e.g. ``linux-yocto-myproject``).

3. *Ensure You Have Configurations:* Make sure you have either a
   ``defconfig`` file or configuration fragment files in your layer.
   When you use the ``linux-yocto-custom.bb`` recipe, you must specify a
   configuration. If you do not have a ``defconfig`` file, you can run
   the following:
   ::

      $ make defconfig

   After running the command, copy the
   resulting ``.config`` file to the ``files`` directory in your layer
   as "defconfig" and then add it to the
   :term:`SRC_URI` variable in the
   recipe.

   Running the ``make defconfig`` command results in the default
   configuration for your architecture as defined by your kernel.
   However, no guarantee exists that this configuration is valid for
   your use case, or that your board will even boot. This is
   particularly true for non-x86 architectures.

   To use non-x86 ``defconfig`` files, you need to be more specific and
   find one that matches your board (i.e. for arm, you look in
   ``arch/arm/configs`` and use the one that is the best starting point
   for your board).

4. *Edit the Recipe:* Edit the following variables in your recipe as
   appropriate for your project:

   -  :term:`SRC_URI`: The
      ``SRC_URI`` should specify a Git repository that uses one of the
      supported Git fetcher protocols (i.e. ``file``, ``git``, ``http``,
      and so forth). The ``SRC_URI`` variable should also specify either
      a ``defconfig`` file or some configuration fragment files. The
      skeleton recipe provides an example ``SRC_URI`` as a syntax
      reference.

   -  :term:`LINUX_VERSION`:
      The Linux kernel version you are using (e.g. "4.12").

   -  :term:`LINUX_VERSION_EXTENSION`:
      The Linux kernel ``CONFIG_LOCALVERSION`` that is compiled into the
      resulting kernel and visible through the ``uname`` command.

   -  :term:`SRCREV`: The commit ID
      from which you want to build.

   -  :term:`PR`: Treat this variable the
      same as you would in any other recipe. Increment the variable to
      indicate to the OpenEmbedded build system that the recipe has
      changed.

   -  :term:`PV`: The default ``PV``
      assignment is typically adequate. It combines the
      ``LINUX_VERSION`` with the Source Control Manager (SCM) revision
      as derived from the :term:`SRCPV`
      variable. The combined results are a string with the following
      form:
      3.19.11+git1+68a635bf8dfb64b02263c1ac80c948647cc76d5f_1+218bd8d2022b9852c60d32f0d770931e3cf343e2
      While lengthy, the extra verbosity in ``PV`` helps ensure you are
      using the exact sources from which you intend to build.

   -  :term:`COMPATIBLE_MACHINE`:
      A list of the machines supported by your new recipe. This variable
      in the example recipe is set by default to a regular expression
      that matches only the empty string, "(^$)". This default setting
      triggers an explicit build failure. You must change it to match a
      list of the machines that your new recipe supports. For example,
      to support the ``qemux86`` and ``qemux86-64`` machines, use the
      following form: COMPATIBLE_MACHINE = "qemux86|qemux86-64"

5. *Customize Your Recipe as Needed:* Provide further customizations to
   your recipe as needed just as you would customize an existing
   linux-yocto recipe. See the "`Modifying an Existing
   Recipe <#modifying-an-existing-recipe>`__" section for information.

Working with Out-of-Tree Modules
================================

This section describes steps to build out-of-tree modules on your target
and describes how to incorporate out-of-tree modules in the build.

Building Out-of-Tree Modules on the Target
------------------------------------------

While the traditional Yocto Project development model would be to
include kernel modules as part of the normal build process, you might
find it useful to build modules on the target. This could be the case if
your target system is capable and powerful enough to handle the
necessary compilation. Before deciding to build on your target, however,
you should consider the benefits of using a proper cross-development
environment from your build host.

If you want to be able to build out-of-tree modules on the target, there
are some steps you need to take on the target that is running your SDK
image. Briefly, the ``kernel-dev`` package is installed by default on
all ``*.sdk`` images and the ``kernel-devsrc`` package is installed on
many of the ``*.sdk`` images. However, you need to create some scripts
prior to attempting to build the out-of-tree modules on the target that
is running that image.

Prior to attempting to build the out-of-tree modules, you need to be on
the target as root and you need to change to the ``/usr/src/kernel``
directory. Next, ``make`` the scripts:
::

   # cd /usr/src/kernel
   # make scripts

Because all SDK image recipes include ``dev-pkgs``, the
``kernel-dev`` packages will be installed as part of the SDK image and
the ``kernel-devsrc`` packages will be installed as part of applicable
SDK images. The SDK uses the scripts when building out-of-tree modules.
Once you have switched to that directory and created the scripts, you
should be able to build your out-of-tree modules on the target.

Incorporating Out-of-Tree Modules
---------------------------------

While it is always preferable to work with sources integrated into the
Linux kernel sources, if you need an external kernel module, the
``hello-mod.bb`` recipe is available as a template from which you can
create your own out-of-tree Linux kernel module recipe.

This template recipe is located in the ``poky`` Git repository of the
Yocto Project :yocto_git:`Source Repository <>` at:
::

   poky/meta-skeleton/recipes-kernel/hello-mod/hello-mod_0.1.bb

To get started, copy this recipe to your layer and give it a meaningful
name (e.g. ``mymodule_1.0.bb``). In the same directory, create a new
directory named ``files`` where you can store any source files, patches,
or other files necessary for building the module that do not come with
the sources. Finally, update the recipe as needed for the module.
Typically, you will need to set the following variables:

-  :term:`DESCRIPTION`

-  :term:`LICENSE* <LICENSE>`

-  :term:`SRC_URI`

-  :term:`PV`

Depending on the build system used by the module sources, you might need
to make some adjustments. For example, a typical module ``Makefile``
looks much like the one provided with the ``hello-mod`` template:
::

   obj-m := hello.o

   SRC := $(shell pwd)

   all:
   	$(MAKE) -C $(KERNEL_SRC) M=$(SRC)

   modules_install:
   	$(MAKE) -C $(KERNEL_SRC) M=$(SRC) modules_install
   ...

The important point to note here is the :term:`KERNEL_SRC` variable. The
:ref:`module <ref-classes-module>` class sets this variable and the
:term:`KERNEL_PATH` variable to
``${STAGING_KERNEL_DIR}`` with the necessary Linux kernel build
information to build modules. If your module ``Makefile`` uses a
different variable, you might want to override the
:ref:`ref-tasks-compile` step, or
create a patch to the ``Makefile`` to work with the more typical
``KERNEL_SRC`` or ``KERNEL_PATH`` variables.

After you have prepared your recipe, you will likely want to include the
module in your images. To do this, see the documentation for the
following variables in the Yocto Project Reference Manual and set one of
them appropriately for your machine configuration file:

-  :term:`MACHINE_ESSENTIAL_EXTRA_RDEPENDS`

-  :term:`MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS`

-  :term:`MACHINE_EXTRA_RDEPENDS`

-  :term:`MACHINE_EXTRA_RRECOMMENDS`

Modules are often not required for boot and can be excluded from certain
build configurations. The following allows for the most flexibility:
::

   MACHINE_EXTRA_RRECOMMENDS += "kernel-module-mymodule"

The value is
derived by appending the module filename without the ``.ko`` extension
to the string "kernel-module-".

Because the variable is
:term:`RRECOMMENDS` and not a
:term:`RDEPENDS` variable, the build
will not fail if this module is not available to include in the image.

Inspecting Changes and Commits
==============================

A common question when working with a kernel is: "What changes have been
applied to this tree?" Rather than using "grep" across directories to
see what has changed, you can use Git to inspect or search the kernel
tree. Using Git is an efficient way to see what has changed in the tree.

What Changed in a Kernel?
-------------------------

Following are a few examples that show how to use Git commands to
examine changes. These examples are by no means the only way to see
changes.

.. note::

   In the following examples, unless you provide a commit range,
   kernel.org
   history is blended with Yocto Project kernel changes. You can form
   ranges by using branch names from the kernel tree as the upper and
   lower commit markers with the Git commands. You can see the branch
   names through the web interface to the Yocto Project source
   repositories at
   .

To see a full range of the changes, use the ``git whatchanged`` command
and specify a commit range for the branch (commit\ ``..``\ commit).

Here is an example that looks at what has changed in the ``emenlow``
branch of the ``linux-yocto-3.19`` kernel. The lower commit range is the
commit associated with the ``standard/base`` branch, while the upper
commit range is the commit associated with the ``standard/emenlow``
branch.
::

   $ git whatchanged origin/standard/base..origin/standard/emenlow

To see short, one line summaries of changes use the ``git log`` command:
::

   $ git log --oneline origin/standard/base..origin/standard/emenlow

Use this command to see code differences for the changes:
::

   $ git diff origin/standard/base..origin/standard/emenlow

Use this command to see the commit log messages and the text
differences:
::

   $ git show origin/standard/base..origin/standard/emenlow

Use this command to create individual patches for each change. Here is
an example that that creates patch files for each commit and places them
in your ``Documents`` directory:
::

   $ git format-patch -o $HOME/Documents origin/standard/base..origin/standard/emenlow

Showing a Particular Feature or Branch Change
---------------------------------------------

Tags in the Yocto Project kernel tree divide changes for significant
features or branches. The ``git show`` tag command shows changes based
on a tag. Here is an example that shows ``systemtap`` changes:
::

   $ git show systemtap

You can use the ``git branch --contains`` tag command to
show the branches that contain a particular feature. This command shows
the branches that contain the ``systemtap`` feature:
::

   $ git branch --contains systemtap

Adding Recipe-Space Kernel Features
===================================

You can add kernel features in the
`recipe-space <#recipe-space-metadata>`__ by using the
:term:`KERNEL_FEATURES`
variable and by specifying the feature's ``.scc`` file path in the
:term:`SRC_URI` statement. When you
add features using this method, the OpenEmbedded build system checks to
be sure the features are present. If the features are not present, the
build stops. Kernel features are the last elements processed for
configuring and patching the kernel. Therefore, adding features in this
manner is a way to enforce specific features are present and enabled
without needing to do a full audit of any other layer's additions to the
``SRC_URI`` statement.

You add a kernel feature by providing the feature as part of the
``KERNEL_FEATURES`` variable and by providing the path to the feature's
``.scc`` file, which is relative to the root of the kernel Metadata. The
OpenEmbedded build system searches all forms of kernel Metadata on the
``SRC_URI`` statement regardless of whether the Metadata is in the
"kernel-cache", system kernel Metadata, or a recipe-space Metadata (i.e.
part of the kernel recipe). See the "`Kernel Metadata
Location <#kernel-metadata-location>`__" section for additional
information.

When you specify the feature's ``.scc`` file on the ``SRC_URI``
statement, the OpenEmbedded build system adds the directory of that
``.scc`` file along with all its subdirectories to the kernel feature
search path. Because subdirectories are searched, you can reference a
single ``.scc`` file in the ``SRC_URI`` statement to reference multiple
kernel features.

Consider the following example that adds the "test.scc" feature to the
build.

1. *Create the Feature File:* Create a ``.scc`` file and locate it just
   as you would any other patch file, ``.cfg`` file, or fetcher item you
   specify in the ``SRC_URI`` statement.

   .. note::

      -  You must add the directory of the ``.scc`` file to the
         fetcher's search path in the same manner as you would add a
         ``.patch`` file.

      -  You can create additional ``.scc`` files beneath the directory
         that contains the file you are adding. All subdirectories are
         searched during the build as potential feature directories.

   Continuing with the example, suppose the "test.scc" feature you are
   adding has a ``test.scc`` file in the following directory:
   ::

      my_recipe
      |
      +-linux-yocto
         |
         +-test.cfg
         +-test.scc

   In this example, the
   ``linux-yocto`` directory has both the feature ``test.scc`` file and
   a similarly named configuration fragment file ``test.cfg``.

2. *Add the Feature File to SRC_URI:* Add the ``.scc`` file to the
   recipe's ``SRC_URI`` statement:
   ::

      SRC_URI_append = " file://test.scc"

   The leading space before the path is important as the path is
   appended to the existing path.

3. *Specify the Feature as a Kernel Feature:* Use the
   ``KERNEL_FEATURES`` statement to specify the feature as a kernel
   feature:
   ::

      KERNEL_FEATURES_append = " test.scc"

   The OpenEmbedded build
   system processes the kernel feature when it builds the kernel.

   .. note::

      If other features are contained below "test.scc", then their
      directories are relative to the directory containing the
      test.scc
      file.
