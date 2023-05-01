.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. _device-manager:

Selecting a Device Manager
**************************

The Yocto Project provides multiple ways to manage the device manager
(``/dev``):

-  Persistent and Pre-Populated ``/dev``: For this case, the ``/dev``
   directory is persistent and the required device nodes are created
   during the build.

-  Use ``devtmpfs`` with a Device Manager: For this case, the ``/dev``
   directory is provided by the kernel as an in-memory file system and
   is automatically populated by the kernel at runtime. Additional
   configuration of device nodes is done in user space by a device
   manager like ``udev`` or ``busybox-mdev``.

Using Persistent and Pre-Populated ``/dev``
===========================================

To use the static method for device population, you need to set the
:term:`USE_DEVFS` variable to "0"
as follows::

   USE_DEVFS = "0"

The content of the resulting ``/dev`` directory is defined in a Device
Table file. The
:term:`IMAGE_DEVICE_TABLES`
variable defines the Device Table to use and should be set in the
machine or distro configuration file. Alternatively, you can set this
variable in your ``local.conf`` configuration file.

If you do not define the :term:`IMAGE_DEVICE_TABLES` variable, the default
``device_table-minimal.txt`` is used::

   IMAGE_DEVICE_TABLES = "device_table-mymachine.txt"

The population is handled by the ``makedevs`` utility during image
creation:

Using ``devtmpfs`` and a Device Manager
=======================================

To use the dynamic method for device population, you need to use (or be
sure to set) the :term:`USE_DEVFS`
variable to "1", which is the default::

   USE_DEVFS = "1"

With this
setting, the resulting ``/dev`` directory is populated by the kernel
using ``devtmpfs``. Make sure the corresponding kernel configuration
variable ``CONFIG_DEVTMPFS`` is set when building you build a Linux
kernel.

All devices created by ``devtmpfs`` will be owned by ``root`` and have
permissions ``0600``.

To have more control over the device nodes, you can use a device manager
like ``udev`` or ``busybox-mdev``. You choose the device manager by
defining the ``VIRTUAL-RUNTIME_dev_manager`` variable in your machine or
distro configuration file. Alternatively, you can set this variable in
your ``local.conf`` configuration file::

   VIRTUAL-RUNTIME_dev_manager = "udev"

   # Some alternative values
   # VIRTUAL-RUNTIME_dev_manager = "busybox-mdev"
   # VIRTUAL-RUNTIME_dev_manager = "systemd"

