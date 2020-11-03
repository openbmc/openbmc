.. SPDX-License-Identifier: CC-BY-2.0-UK

************************************************************
Optionally Customizing the Development Packages Installation
************************************************************

Because the Yocto Project is suited for embedded Linux development, it
is likely that you will need to customize your development packages
installation. For example, if you are developing a minimal image, then
you might not need certain packages (e.g. graphics support packages).
Thus, you would like to be able to remove those packages from your
target sysroot.

Package Management Systems
==========================

The OpenEmbedded build system supports the generation of sysroot files
using three different Package Management Systems (PMS):

-  *OPKG:* A less well known PMS whose use originated in the
   OpenEmbedded and OpenWrt embedded Linux projects. This PMS works with
   files packaged in an ``.ipk`` format. See
   http://en.wikipedia.org/wiki/Opkg for more information about
   OPKG.

-  *RPM:* A more widely known PMS intended for GNU/Linux distributions.
   This PMS works with files packaged in an ``.rpm`` format. The build
   system currently installs through this PMS by default. See
   http://en.wikipedia.org/wiki/RPM_Package_Manager for more
   information about RPM.

-  *Debian:* The PMS for Debian-based systems is built on many PMS
   tools. The lower-level PMS tool ``dpkg`` forms the base of the Debian
   PMS. For information on dpkg see
   http://en.wikipedia.org/wiki/Dpkg.

Configuring the PMS
===================

Whichever PMS you are using, you need to be sure that the
:term:`PACKAGE_CLASSES`
variable in the ``conf/local.conf`` file is set to reflect that system.
The first value you choose for the variable specifies the package file
format for the root filesystem at sysroot. Additional values specify
additional formats for convenience or testing. See the
``conf/local.conf`` configuration file for details.

.. note::

   For build performance information related to the PMS, see the "
   package.bbclass
   " section in the Yocto Project Reference Manual.

As an example, consider a scenario where you are using OPKG and you want
to add the ``libglade`` package to the target sysroot.

First, you should generate the IPK file for the ``libglade`` package and
add it into a working ``opkg`` repository. Use these commands: $ bitbake
libglade $ bitbake package-index

Next, source the cross-toolchain environment setup script found in the
:term:`Source Directory`. Follow
that by setting up the installation destination to point to your sysroot
as sysroot_dir. Finally, have an OPKG configuration file conf_file that
corresponds to the ``opkg`` repository you have just created. The
following command forms should now work: $ opkg-cl –f conf_file -o
sysroot_dir update $ opkg-cl –f cconf_file -o sysroot_dir \\
--force-overwrite install libglade $ opkg-cl –f cconf_file -o
sysroot_dir \\ --force-overwrite install libglade-dbg $ opkg-cl –f
conf_file> -osysroot_dir> \\ --force-overwrite install libglade-dev
