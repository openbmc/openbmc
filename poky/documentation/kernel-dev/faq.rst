.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

**********************
Kernel Development FAQ
**********************

Common Questions and Solutions
==============================

Here are some solutions for common questions.

How do I use my own Linux kernel ``.config`` file?
--------------------------------------------------

Refer to the
":ref:`kernel-dev/common:changing the configuration`"
section for information.

How do I create configuration fragments?
----------------------------------------

A: Refer to the
":ref:`kernel-dev/common:creating configuration fragments`"
section for information.

How do I use my own Linux kernel sources?
-----------------------------------------

Refer to the
":ref:`kernel-dev/common:working with your own sources`"
section for information.

How do I install/not-install the kernel image on the root filesystem?
---------------------------------------------------------------------

The kernel image (e.g. ``vmlinuz``) is provided by the
``kernel-image`` package. Image recipes depend on ``kernel-base``. To
specify whether or not the kernel image is installed in the generated
root filesystem, override ``RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base`` to include or not
include "kernel-image". See the
":ref:`dev-manual/common-tasks:appending other layers metadata with your layer`"
section in the
Yocto Project Development Tasks Manual for information on how to use an
append file to override metadata.

How do I install a specific kernel module?
------------------------------------------

Linux kernel modules are packaged individually. To ensure a
specific kernel module is included in an image, include it in the
appropriate machine :term:`RRECOMMENDS` variable.
These other variables are useful for installing specific modules:
- :term:`MACHINE_ESSENTIAL_EXTRA_RDEPENDS`
- :term:`MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS`
- :term:`MACHINE_EXTRA_RDEPENDS`
- :term:`MACHINE_EXTRA_RRECOMMENDS`

For example, set the following in the ``qemux86.conf`` file to include
the ``ab123`` kernel modules with images built for the ``qemux86``
machine::

   MACHINE_EXTRA_RRECOMMENDS += "kernel-module-ab123"

For more information, see the
":ref:`kernel-dev/common:incorporating out-of-tree modules`" section.

How do I change the Linux kernel command line?
----------------------------------------------

The Linux kernel command line is
typically specified in the machine config using the :term:`APPEND` variable.
For example, you can add some helpful debug information doing the
following::

   APPEND += "printk.time=y initcall_debug debug"

