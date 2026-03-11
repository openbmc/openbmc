.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Adding a New Machine
********************

Adding a new machine to the Yocto Project is a straightforward process.
This section describes how to add machines that are similar to those
that the Yocto Project already supports.

.. note::

   Although well within the capabilities of the Yocto Project, adding a
   totally new architecture might require changes to ``gcc``/``glibc``
   and to the site information, which is beyond the scope of this
   manual.

For a complete example that shows how to add a new machine, see the
":ref:`bsp-guide/bsp:creating a new bsp layer using the \`\`bitbake-layers\`\` script`"
section in the Yocto Project Board Support Package (BSP) Developer's
Guide.

Adding the Machine Configuration File
=====================================

To add a new machine, you need to add a new machine configuration file
to the layer's ``conf/machine`` directory. This configuration file
provides details about the device you are adding.

The OpenEmbedded build system uses the root name of the machine
configuration file to reference the new machine. For example, given a
machine configuration file named ``crownbay.conf``, the build system
recognizes the machine as "crownbay".

The most important variables you must set in your machine configuration
file or include from a lower-level configuration file are as follows:

-  :term:`TARGET_ARCH` (e.g. "arm")

-  ``PREFERRED_PROVIDER_virtual/kernel``

-  :term:`MACHINE_FEATURES` (e.g. "screen wifi")

You might also need these variables:

-  :term:`SERIAL_CONSOLES` (e.g. "115200;ttyS0 115200;ttyS1")

-  :term:`KERNEL_IMAGETYPE` (e.g. "zImage")

-  :term:`IMAGE_FSTYPES` (e.g. "tar.gz jffs2")

You can find full details on these variables in the reference section.
You can leverage existing machine ``.conf`` files from
``meta-yocto-bsp/conf/machine/``.

Adding a Kernel for the Machine
===============================

The OpenEmbedded build system needs to be able to build a kernel for the
machine. You need to either create a new kernel recipe for this machine,
or extend an existing kernel recipe. You can find several kernel recipe
examples in the Source Directory at ``meta/recipes-kernel/linux`` that
you can use as references.

If you are creating a new kernel recipe, normal recipe-writing rules
apply for setting up a :term:`SRC_URI`. Thus, you need to specify any
necessary patches and set :term:`S` to point at the source code. You need to
create a :ref:`ref-tasks-configure` task that configures the unpacked kernel with
a ``defconfig`` file. You can do this by using a ``make defconfig``
command or, more commonly, by copying in a suitable ``defconfig`` file
and then running ``make oldconfig``. By making use of ``inherit kernel``
and potentially some of the ``linux-*.inc`` files, most other
functionality is centralized and the defaults of the class normally work
well.

If you are extending an existing kernel recipe, it is usually a matter
of adding a suitable ``defconfig`` file. The file needs to be added into
a location similar to ``defconfig`` files used for other machines in a
given kernel recipe. A possible way to do this is by listing the file in
the :term:`SRC_URI` and adding the machine to the expression in
:term:`COMPATIBLE_MACHINE`::

   COMPATIBLE_MACHINE = '(qemux86|qemumips)'

For more information on ``defconfig`` files, see the
":ref:`kernel-dev/common:changing the configuration`"
section in the Yocto Project Linux Kernel Development Manual.

Adding a Formfactor Configuration File
======================================

A formfactor configuration file provides information about the target
hardware for which the image is being built and information that the
build system cannot obtain from other sources such as the kernel. Some
examples of information contained in a formfactor configuration file
include framebuffer orientation, whether or not the system has a
keyboard, the positioning of the keyboard in relation to the screen, and
the screen resolution.

The build system uses reasonable defaults in most cases. However, if
customization is necessary, you need to create a ``machconfig`` file in
the ``meta/recipes-bsp/formfactor/files`` directory. This directory
contains directories for specific machines such as ``qemuarm`` and
``qemux86``. For information about the settings available and the
defaults, see the ``meta/recipes-bsp/formfactor/files/config`` file
found in the same area.

Here is an example for "qemuarm" machine::

   HAVE_TOUCHSCREEN=1
   HAVE_KEYBOARD=1
   DISPLAY_CAN_ROTATE=0
   DISPLAY_ORIENTATION=0
   #DISPLAY_WIDTH_PIXELS=640
   #DISPLAY_HEIGHT_PIXELS=480
   #DISPLAY_BPP=16
   DISPLAY_DPI=150
   DISPLAY_SUBPIXEL_ORDER=vrgb

