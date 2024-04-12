.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

.. _init-manager:

Selecting an Initialization Manager
***********************************

By default, the Yocto Project uses :wikipedia:`SysVinit <Init#SysV-style>` as
the initialization manager. There is also support for BusyBox init, a simpler
implementation, as well as support for :wikipedia:`systemd <Systemd>`, which
is a full replacement for init with parallel starting of services, reduced
shell overhead, increased security and resource limits for services, and other
features that are used by many distributions.

Within the system, SysVinit and BusyBox init treat system components as
services. These services are maintained as shell scripts stored in the
``/etc/init.d/`` directory.

SysVinit is more elaborate than BusyBox init and organizes services in
different run levels. This organization is maintained by putting links
to the services in the ``/etc/rcN.d/`` directories, where `N/` is one
of the following options: "S", "0", "1", "2", "3", "4", "5", or "6".

.. note::

   Each runlevel has a dependency on the previous runlevel. This
   dependency allows the services to work properly.

Both SysVinit and BusyBox init are configured through the ``/etc/inittab``
file, with a very similar syntax, though of course BusyBox init features
are more limited.

In comparison, systemd treats components as units. Using units is a
broader concept as compared to using a service. A unit includes several
different types of entities. ``Service`` is one of the types of entities.
The runlevel concept in SysVinit corresponds to the concept of a target
in systemd, where target is also a type of supported unit.

In systems with SysVinit or BusyBox init, services load sequentially (i.e. one
by one) during init and parallelization is not supported. With systemd, services
start in parallel. This method can have an impact on the startup performance
of a given service, though systemd will also provide more services by default,
therefore increasing the total system boot time. systemd also substantially
increases system size because of its multiple components and the extra
dependencies it pulls.

On the contrary, BusyBox init is the simplest and the lightest solution and
also comes with BusyBox mdev as device manager, a lighter replacement to
:wikipedia:`udev <Udev>`, which SysVinit and systemd both use.

The ":ref:`device-manager`" chapter has more details about device managers.

Using SysVinit with udev
=========================

SysVinit with the udev device manager corresponds to the
default setting in Poky. This corresponds to setting::

   INIT_MANAGER = "sysvinit"

Using BusyBox init with BusyBox mdev
====================================

BusyBox init with BusyBox mdev is the simplest and lightest solution
for small root filesystems. All you need is BusyBox, which most systems
have anyway::

   INIT_MANAGER = "mdev-busybox"

Using systemd
=============

The last option is to use systemd together with the udev device
manager. This is the most powerful and versatile solution, especially
for more complex systems::

   INIT_MANAGER = "systemd"

This will enable systemd and remove sysvinit components from the image.
See :yocto_git:`meta/conf/distro/include/init-manager-systemd.inc
</poky/tree/meta/conf/distro/include/init-manager-systemd.inc>` for exact
details on what this does.

Controling systemd from the target command line
-----------------------------------------------

Here is a quick reference for controling systemd from the command line on the
target. Instead of opening and sometimes modifying files, most interaction
happens through the ``systemctl`` and ``journalctl`` commands:

-  ``systemctl status``: show the status of all services
-  ``systemctl status <service>``: show the status of one service
-  ``systemctl [start|stop] <service>``: start or stop a service
-  ``systemctl [enable|disable] <service>``: enable or disable a service at boot time
-  ``systemctl list-units``: list all available units
-  ``journalctl -a``: show all logs for all services
-  ``journalctl -f``: show only the last log entries, and keep printing updates as they arrive
-  ``journalctl -u``: show only logs from a particular service

Using systemd-journald without a traditional syslog daemon
----------------------------------------------------------

Counter-intuitively, ``systemd-journald`` is not a syslog runtime or provider,
and the proper way to use ``systemd-journald`` as your sole logging mechanism is to
effectively disable syslog entirely by setting these variables in your distribution
configuration file::

   VIRTUAL-RUNTIME_syslog = ""
   VIRTUAL-RUNTIME_base-utils-syslog = ""

Doing so will prevent ``rsyslog`` / ``busybox-syslog`` from being pulled in by
default, leaving only ``systemd-journald``.

Summary
-------

The Yocto Project supports three different initialization managers, offering
increasing levels of complexity and functionality:

.. list-table::
   :widths: 40 20 20 20
   :header-rows: 1

   * -
     - BusyBox init
     - SysVinit
     - systemd
   * - Size
     - Small
     - Small
     - Big [#footnote-systemd-size]_
   * - Complexity
     - Small
     - Medium
     - High
   * - Support for boot profiles
     - No
     - Yes ("runlevels")
     - Yes ("targets")
   * - Services defined as
     - Shell scripts
     - Shell scripts
     - Description files
   * - Starting services in parallel
     - No
     - No
     - Yes
   * - Setting service resource limits
     - No
     - No
     - Yes
   * - Support service isolation
     - No
     - No
     - Yes
   * - Integrated logging
     - No
     - No
     - Yes

.. [#footnote-systemd-size] Using systemd increases the ``core-image-minimal``
   image size by 160\% for ``qemux86-64`` on Mickledore (4.2), compared to SysVinit.
