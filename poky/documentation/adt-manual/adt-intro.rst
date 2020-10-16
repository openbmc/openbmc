.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*****************************************
The Application Development Toolkit (ADT)
*****************************************

Part of the Yocto Project development solution is an Application
Development Toolkit (ADT). The ADT provides you with a custom-built,
cross-development platform suited for developing a user-targeted product
application.

Fundamentally, the ADT consists of the following:

-  An architecture-specific cross-toolchain and matching sysroot both
   built by the :term:`OpenEmbedded Build System`.
   The toolchain and
   sysroot are based on a `Metadata <&YOCTO_DOCS_DEV_URL;#metadata>`__
   configuration and extensions, which allows you to cross-develop on
   the host machine for the target hardware.

-  The Eclipse IDE Yocto Plug-in.

-  The Quick EMUlator (QEMU), which lets you simulate target hardware.

-  Various user-space tools that greatly enhance your application
   development experience.

The Cross-Development Toolchain
===============================

The `Cross-Development
Toolchain <&YOCTO_DOCS_DEV_URL;#cross-development-toolchain>`__ consists
of a cross-compiler, cross-linker, and cross-debugger that are used to
develop user-space applications for targeted hardware. This toolchain is
created either by running the ADT Installer script, a toolchain
installer script, or through a :term:`Build Directory`
that is based on
your Metadata configuration or extension for your targeted device. The
cross-toolchain works with a matching target sysroot.

Sysroot
=======

The matching target sysroot contains needed headers and libraries for
generating binaries that run on the target architecture. The sysroot is
based on the target root filesystem image that is built by the
OpenEmbedded build system and uses the same Metadata configuration used
to build the cross-toolchain.

.. _eclipse-overview:

Eclipse Yocto Plug-in
=====================

The Eclipse IDE is a popular development environment and it fully
supports development using the Yocto Project. When you install and
configure the Eclipse Yocto Project Plug-in into the Eclipse IDE, you
maximize your Yocto Project experience. Installing and configuring the
Plug-in results in an environment that has extensions specifically
designed to let you more easily develop software. These extensions allow
for cross-compilation, deployment, and execution of your output into a
QEMU emulation session. You can also perform cross-debugging and
profiling. The environment also supports a suite of tools that allows
you to perform remote profiling, tracing, collection of power data,
collection of latency data, and collection of performance data.

For information about the application development workflow that uses the
Eclipse IDE and for a detailed example of how to install and configure
the Eclipse Yocto Project Plug-in, see the "`Working Within
Eclipse <&YOCTO_DOCS_DEV_URL;#adt-eclipse>`__" section of the Yocto
Project Development Manual.

The QEMU Emulator
=================

The QEMU emulator allows you to simulate your hardware while running
your application or image. QEMU is made available a number of ways:

-  If you use the ADT Installer script to install ADT, you can specify
   whether or not to install QEMU.

-  If you have cloned the ``poky`` Git repository to create a
   :term:`Source Directory` and you have
   sourced the environment setup script, QEMU is installed and
   automatically available.

-  If you have downloaded a Yocto Project release and unpacked it to
   create a :term:`Source Directory`
   and you have sourced the environment setup script, QEMU is installed
   and automatically available.

-  If you have installed the cross-toolchain tarball and you have
   sourced the toolchain's setup environment script, QEMU is also
   installed and automatically available.

User-Space Tools
================

User-space tools are included as part of the Yocto Project. You will
find these tools helpful during development. The tools include
LatencyTOP, PowerTOP, OProfile, Perf, SystemTap, and Lttng-ust. These
tools are common development tools for the Linux platform.

-  *LatencyTOP:* LatencyTOP focuses on latency that causes skips in
   audio, stutters in your desktop experience, or situations that
   overload your server even when you have plenty of CPU power left.

-  *PowerTOP:* Helps you determine what software is using the most
   power. You can find out more about PowerTOP at
   https://01.org/powertop/.

-  *OProfile:* A system-wide profiler for Linux systems that is capable
   of profiling all running code at low overhead. You can find out more
   about OProfile at http://oprofile.sourceforge.net/about/. For
   examples on how to setup and use this tool, see the
   "`OProfile <&YOCTO_DOCS_PROF_URL;#profile-manual-oprofile>`__"
   section in the Yocto Project Profiling and Tracing Manual.

-  *Perf:* Performance counters for Linux used to keep track of certain
   types of hardware and software events. For more information on these
   types of counters see https://perf.wiki.kernel.org/. For
   examples on how to setup and use this tool, see the
   "`perf <&YOCTO_DOCS_PROF_URL;#profile-manual-perf>`__" section in the
   Yocto Project Profiling and Tracing Manual.

-  *SystemTap:* A free software infrastructure that simplifies
   information gathering about a running Linux system. This information
   helps you diagnose performance or functional problems. SystemTap is
   not available as a user-space tool through the Eclipse IDE Yocto
   Plug-in. See http://sourceware.org/systemtap for more
   information on SystemTap. For examples on how to setup and use this
   tool, see the
   "`SystemTap <&YOCTO_DOCS_PROF_URL;#profile-manual-systemtap>`__"
   section in the Yocto Project Profiling and Tracing Manual.

-  *Lttng-ust:* A User-space Tracer designed to provide detailed
   information on user-space activity. See http://lttng.org/ust
   for more information on Lttng-ust.
