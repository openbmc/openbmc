.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Selecting an Initialization Manager
***********************************

By default, the Yocto Project uses SysVinit as the initialization
manager. However, there is also support for systemd, which is a full
replacement for init with parallel starting of services, reduced shell
overhead and other features that are used by many distributions.

Within the system, SysVinit treats system components as services. These
services are maintained as shell scripts stored in the ``/etc/init.d/``
directory. Services organize into different run levels. This
organization is maintained by putting links to the services in the
``/etc/rcN.d/`` directories, where `N/` is one of the following options:
"S", "0", "1", "2", "3", "4", "5", or "6".

.. note::

   Each runlevel has a dependency on the previous runlevel. This
   dependency allows the services to work properly.

In comparison, systemd treats components as units. Using units is a
broader concept as compared to using a service. A unit includes several
different types of entities. Service is one of the types of entities.
The runlevel concept in SysVinit corresponds to the concept of a target
in systemd, where target is also a type of supported unit.

In a SysVinit-based system, services load sequentially (i.e. one by one)
during init and parallelization is not supported. With systemd, services
start in parallel. Needless to say, the method can have an impact on
system startup performance.

If you want to use SysVinit, you do not have to do anything. But, if you
want to use systemd, you must take some steps as described in the
following sections.

Using systemd Exclusively
=========================

Set these variables in your distribution configuration file as follows::

   DISTRO_FEATURES:append = " systemd"
   VIRTUAL-RUNTIME_init_manager = "systemd"

You can also prevent the SysVinit distribution feature from
being automatically enabled as follows::

   DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"

Doing so removes any
redundant SysVinit scripts.

To remove initscripts from your image altogether, set this variable
also::

   VIRTUAL-RUNTIME_initscripts = ""

For information on the backfill variable, see
:term:`DISTRO_FEATURES_BACKFILL_CONSIDERED`.

Using systemd for the Main Image and Using SysVinit for the Rescue Image
========================================================================

Set these variables in your distribution configuration file as follows::

   DISTRO_FEATURES:append = " systemd"
   VIRTUAL-RUNTIME_init_manager = "systemd"

Doing so causes your main image to use the
``packagegroup-core-boot.bb`` recipe and systemd. The rescue/minimal
image cannot use this package group. However, it can install SysVinit
and the appropriate packages will have support for both systemd and
SysVinit.

Using systemd-journald without a traditional syslog daemon
==========================================================

Counter-intuitively, ``systemd-journald`` is not a syslog runtime or provider,
and the proper way to use systemd-journald as your sole logging mechanism is to
effectively disable syslog entirely by setting these variables in your distribution
configuration file::

   VIRTUAL-RUNTIME_syslog = ""
   VIRTUAL-RUNTIME_base-utils-syslog = ""

Doing so will prevent ``rsyslog`` / ``busybox-syslog`` from being pulled in by
default, leaving only ``journald``.

