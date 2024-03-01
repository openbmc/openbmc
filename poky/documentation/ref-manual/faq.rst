.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

***
FAQ
***

.. contents::

General questions
=================

How does Poky differ from OpenEmbedded?
---------------------------------------

The term ``Poky`` refers to the specific reference build
system that the Yocto Project provides. Poky is based on
:term:`OpenEmbedded-Core (OE-Core)` and :term:`BitBake`. Thus, the
generic term used here for the build system is the "OpenEmbedded build
system." Development in the Yocto Project using Poky is closely tied to
OpenEmbedded, with changes always being merged to OE-Core or BitBake
first before being pulled back into Poky. This practice benefits both
projects immediately.

How can you claim Poky / OpenEmbedded-Core is stable?
-----------------------------------------------------

There are three areas that help with stability;

-  The Yocto Project team keeps :term:`OpenEmbedded-Core (OE-Core)` small and
   focused, containing around 830 recipes as opposed to the thousands
   available in other OpenEmbedded community layers. Keeping it small
   makes it easy to test and maintain.

-  The Yocto Project team runs manual and automated tests using a small,
   fixed set of reference hardware as well as emulated targets.

-  The Yocto Project uses an :yocto_ab:`autobuilder <>`, which provides
   continuous build and integration tests.

Are there any products built using the OpenEmbedded build system?
-----------------------------------------------------------------

See :yocto_wiki:`Products that use the Yocto Project
</Project_Users#Products_that_use_the_Yocto_Project>` in the Yocto Project
Wiki. Don't hesitate to contribute to this page if you know other such
products.

Building environment
====================

Missing dependencies on the development system?
-----------------------------------------------

If your development system does not meet the required Git, tar, and
Python versions, you can get the required tools on your host development
system in different ways (i.e. building a tarball or downloading a
tarball). See the ":ref:`ref-manual/system-requirements:required git, tar, python, make and gcc versions`"
section for steps on how to update your build tools.

How does OpenEmbedded fetch source code? Will it work through a firewall or proxy server?
-----------------------------------------------------------------------------------------

The way the build system obtains source code is highly
configurable. You can setup the build system to get source code in most
environments if HTTP transport is available.

When the build system searches for source code, it first tries the local
download directory. If that location fails, Poky tries
:term:`PREMIRRORS`, the upstream source, and then
:term:`MIRRORS` in that order.

Assuming your distribution is "poky", the OpenEmbedded build system uses
the Yocto Project source :term:`PREMIRRORS` by default for SCM-based
sources, upstreams for normal tarballs, and then falls back to a number
of other mirrors including the Yocto Project source mirror if those
fail.

As an example, you could add a specific server for the build system to
attempt before any others by adding something like the following to the
``local.conf`` configuration file::

   PREMIRRORS:prepend = "\
       git://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
       ftp://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
       http://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
       https://.*/.* &YOCTO_DL_URL;/mirror/sources/"

These changes cause the build system to intercept Git, FTP, HTTP, and
HTTPS requests and direct them to the ``http://`` sources mirror. You
can use ``file://`` URLs to point to local directories or network shares
as well.

Another option is to set::

   BB_NO_NETWORK = "1"

This statement tells BitBake to issue an error
instead of trying to access the Internet. This technique is useful if
you want to ensure code builds only from local sources.

Here is another technique::

   BB_FETCH_PREMIRRORONLY = "1"

This statement limits the build system to pulling source from the
:term:`PREMIRRORS` only.  Again, this technique is useful for reproducing
builds.

Here is yet another technique::

   BB_GENERATE_MIRROR_TARBALLS = "1"

This statement tells the build system to generate mirror tarballs. This
technique is useful if you want to create a mirror server. If not,
however, the technique can simply waste time during the build.

Finally, consider an example where you are behind an HTTP-only firewall.
You could make the following changes to the ``local.conf`` configuration
file as long as the :term:`PREMIRRORS` server is current::

   PREMIRRORS:prepend = "\
       git://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
       ftp://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
       http://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
       https://.*/.* &YOCTO_DL_URL;/mirror/sources/"
   BB_FETCH_PREMIRRORONLY = "1"

These changes would cause the build system to successfully fetch source
over HTTP and any network accesses to anything other than the
:term:`PREMIRRORS` would fail.

Most source fetching by the OpenEmbedded build system is done by
``wget`` and you therefore need to specify the proxy settings in a
``.wgetrc`` file, which can be in your home directory if you are a
single user or can be in ``/usr/local/etc/wgetrc`` as a global user
file.

Here is the applicable code for setting various proxy types in the
``.wgetrc`` file. By default, these settings are disabled with comments.
To use them, remove the comments::

   # You can set the default proxies for Wget to use for http, https, and ftp.
   # They will override the value in the environment.
   #https_proxy = http://proxy.yoyodyne.com:18023/
   #http_proxy = http://proxy.yoyodyne.com:18023/
   #ftp_proxy = http://proxy.yoyodyne.com:18023/

   # If you do not want to use proxy at all, set this to off.
   #use_proxy = on

The build system also accepts ``http_proxy``, ``ftp_proxy``, ``https_proxy``,
and ``all_proxy`` set as to standard shell environment variables to redirect
requests through proxy servers.

The Yocto Project also includes a
``meta-poky/conf/templates/default/site.conf.sample`` file that shows
how to configure CVS and Git proxy servers if needed.

.. note::

   You can find more information on the
   ":yocto_wiki:`Working Behind a Network Proxy </Working_Behind_a_Network_Proxy>`"
   Wiki page.

Using the OpenEmbedded Build system
===================================

How do I use an external toolchain?
-----------------------------------

See the ":ref:`dev-manual/external-toolchain:optionally using an external toolchain`"
section in the Development Task manual.

Why do I get chmod permission issues?
-------------------------------------

If you see the error
``chmod: XXXXX new permissions are r-xrwxrwx, not r-xr-xr-x``,
you are probably running the build on an NTFS filesystem. Instead,
run the build system on a partition with a modern Linux filesystem such as
``ext4``, ``btrfs`` or ``xfs``.

I see many 404 errors trying to download sources. Is anything wrong?
--------------------------------------------------------------------

Nothing is wrong. The OpenEmbedded build system checks any
configured source mirrors before downloading from the upstream sources.
The build system does this searching for both source archives and
pre-checked out versions of SCM-managed software. These checks help in
large installations because it can reduce load on the SCM servers
themselves. This can also allow builds to continue to work if an
upstream source disappears.

Why do I get random build failures?
-----------------------------------

If the same build is failing in totally different and random
ways, the most likely explanation is:

-  The hardware you are running the build on has some problem.

-  You are running the build under virtualization, in which case the
   virtualization probably has bugs.

The OpenEmbedded build system processes a massive amount of data that
causes lots of network, disk and CPU activity and is sensitive to even
single-bit failures in any of these areas. True random failures have
always been traced back to hardware or virtualization issues.

Why does the build fail with ``iconv.h`` problems?
--------------------------------------------------

When you try to build a native recipe, you may get an error message that
indicates that GNU ``libiconv`` is not in use but ``iconv.h`` has been
included from ``libiconv``::

   #error GNU libiconv not in use but included iconv.h is from libiconv

When this happens, you need to check whether you have a previously
installed version of the header file in ``/usr/local/include/``.
If that's the case, you should either uninstall it or temporarily rename
it and try the build again.

This issue is just a single manifestation of "system leakage" issues
caused when the OpenEmbedded build system finds and uses previously
installed files during a native build. This type of issue might not be
limited to ``iconv.h``. Make sure that leakage cannot occur from
``/usr/local/include`` and ``/opt`` locations.

Why don't other recipes find the files provided by my ``*-native`` recipe?
--------------------------------------------------------------------------

Files provided by your native recipe could be missing from the native
sysroot, your recipe could also be installing to the wrong place, or you
could be getting permission errors during the :ref:`ref-tasks-install`
task in your recipe.

This situation happens when the build system used by a package does not
recognize the environment variables supplied to it by :term:`BitBake`. The
incident that prompted this FAQ entry involved a Makefile that used an
environment variable named ``BINDIR`` instead of the more standard
variable ``bindir``. The makefile's hardcoded default value of
"/usr/bin" worked most of the time, but not for the recipe's ``-native``
variant. For another example, permission errors might be caused by a
Makefile that ignores ``DESTDIR`` or uses a different name for that
environment variable. Check the build system of the package to see if
these kinds of issues exist.

Can I get rid of build output so I can start over?
--------------------------------------------------

Yes --- you can easily do this. When you use BitBake to build an
image, all the build output goes into the directory created when you run
the build environment setup script (i.e.  :ref:`structure-core-script`).
By default, this :term:`Build Directory` is named ``build`` but can be named
anything you want.

Within the :term:`Build Directory`, is the ``tmp`` directory. To remove all the
build output yet preserve any source code or downloaded files from
previous builds, simply remove the ``tmp`` directory.

Customizing generated images
============================

What does the OpenEmbedded build system produce as output?
----------------------------------------------------------

Because you can use the same set of recipes to create output of
various formats, the output of an OpenEmbedded build depends on how you
start it. Usually, the output is a flashable image ready for the target
device.

How do I make the Yocto Project support my board?
-------------------------------------------------

Support for an additional board is added by creating a Board
Support Package (BSP) layer for it. For more information on how to
create a BSP layer, see the
":ref:`dev-manual/layers:understanding and creating layers`"
section in the Yocto Project Development Tasks Manual and the
:doc:`/bsp-guide/index`.

Usually, if the board is not completely exotic, adding support in the
Yocto Project is fairly straightforward.

How do I make the Yocto Project support my package?
---------------------------------------------------

To add a package, you need to create a BitBake recipe. For
information on how to create a BitBake recipe, see the
":ref:`dev-manual/new-recipe:writing a new recipe`"
section in the Yocto Project Development Tasks Manual.

What do I need to ship for license compliance?
----------------------------------------------

This is a difficult question and you need to consult your lawyer
for the answer for your specific case. It is worth bearing in mind that
for GPL compliance, there needs to be enough information shipped to
allow someone else to rebuild and produce the same end result you are
shipping. This means sharing the source code, any patches applied to it,
and also any configuration information about how that package was
configured and built.

You can find more information on licensing in the
":ref:`overview-manual/development-environment:licensing`"
section in the Yocto Project Overview and Concepts Manual and also in the
":ref:`dev-manual/licenses:maintaining open source license compliance during your product's lifecycle`"
section in the Yocto Project Development Tasks Manual.

Do I have to make a full reflash after recompiling one package?
---------------------------------------------------------------

The OpenEmbedded build system can build packages in various
formats such as IPK for OPKG, Debian package (``.deb``), or RPM. You can
then upgrade only the modified packages using the package tools on the device,
much like on a desktop distribution such as Ubuntu or Fedora. However,
package management on the target is entirely optional.

How to prevent my package from being marked as machine specific?
----------------------------------------------------------------

If you have machine-specific data in a package for one machine only
but the package is being marked as machine-specific in all cases,
you can set :term:`SRC_URI_OVERRIDES_PACKAGE_ARCH` = "0" in the ``.bb`` file.
However, but make sure the package is manually marked as machine-specific for the
case that needs it. The code that handles :term:`SRC_URI_OVERRIDES_PACKAGE_ARCH`
is in the ``meta/classes-global/base.bbclass`` file.

What's the difference between ``target`` and ``target-native``?
---------------------------------------------------------------

The ``*-native`` targets are designed to run on the system being
used for the build. These are usually tools that are needed to assist
the build in some way such as ``quilt-native``, which is used to apply
patches. The non-native version is the one that runs on the target
device.

Why do ``${bindir}`` and ``${libdir}`` have strange values for ``-native`` recipes?
-----------------------------------------------------------------------------------

Executables and libraries might need to be used from a directory
other than the directory into which they were initially installed.
Complicating this situation is the fact that sometimes these executables
and libraries are compiled with the expectation of being run from that
initial installation target directory. If this is the case, moving them
causes problems.

This scenario is a fundamental problem for package maintainers of
mainstream Linux distributions as well as for the OpenEmbedded build
system. As such, a well-established solution exists. Makefiles,
Autotools configuration scripts, and other build systems are expected to
respect environment variables such as ``bindir``, ``libdir``, and
``sysconfdir`` that indicate where executables, libraries, and data
reside when a program is actually run. They are also expected to respect
a ``DESTDIR`` environment variable, which is prepended to all the other
variables when the build system actually installs the files. It is
understood that the program does not actually run from within
``DESTDIR``.

When the OpenEmbedded build system uses a recipe to build a
target-architecture program (i.e. one that is intended for inclusion on
the image being built), that program eventually runs from the root file
system of that image. Thus, the build system provides a value of
"/usr/bin" for ``bindir``, a value of "/usr/lib" for ``libdir``, and so
forth.

Meanwhile, ``DESTDIR`` is a path within the :term:`Build Directory`.
However, when the recipe builds a native program (i.e. one that is
intended to run on the build machine), that program is never installed
directly to the build machine's root file system. Consequently, the build
system uses paths within the Build Directory for ``DESTDIR``, ``bindir``
and related variables. To better understand this, consider the following
two paths (artificially broken across lines for readability) where the
first is relatively normal and the second is not::

   /home/maxtothemax/poky-bootchart2/build/tmp/work/i586-poky-linux/zlib/
      1.2.8-r0/sysroot-destdir/usr/bin

   /home/maxtothemax/poky-bootchart2/build/tmp/work/x86_64-linux/
      zlib-native/1.2.8-r0/sysroot-destdir/home/maxtothemax/poky-bootchart2/
      build/tmp/sysroots/x86_64-linux/usr/bin

Even if the paths look unusual, they both are correct --- the first for
a target and the second for a native recipe. These paths are a consequence
of the ``DESTDIR`` mechanism and while they appear strange, they are correct
and in practice very effective.

How do I create images with more free space?
--------------------------------------------

By default, the OpenEmbedded build system creates images that are
1.3 times the size of the populated root filesystem. To affect the image
size, you need to set various configurations:

-  *Image Size:* The OpenEmbedded build system uses the
   :term:`IMAGE_ROOTFS_SIZE` variable to define
   the size of the image in Kbytes. The build system determines the size
   by taking into account the initial root filesystem size before any
   modifications such as requested size for the image and any requested
   additional free disk space to be added to the image.

-  *Overhead:* Use the
   :term:`IMAGE_OVERHEAD_FACTOR` variable
   to define the multiplier that the build system applies to the initial
   image size, which is 1.3 by default.

-  *Additional Free Space:* Use the
   :term:`IMAGE_ROOTFS_EXTRA_SPACE`
   variable to add additional free space to the image. The build system
   adds this space to the image after it determines its
   :term:`IMAGE_ROOTFS_SIZE`.

Why aren't spaces in path names supported?
------------------------------------------

The Yocto Project team has tried to do this before but too many
of the tools the OpenEmbedded build system depends on, such as
``autoconf``, break when they find spaces in pathnames. Until that
situation changes, the team will not support spaces in pathnames.

I'm adding a binary in a recipe. Why is it different in the image?
------------------------------------------------------------------

The first most obvious change is the system stripping debug symbols from
it. Setting :term:`INHIBIT_PACKAGE_STRIP` to stop debug symbols being
stripped and/or :term:`INHIBIT_PACKAGE_DEBUG_SPLIT` to stop debug symbols
being split into a separate file will ensure the binary is unchanged.

Issues on the running system
============================

How do I disable the cursor on my touchscreen device?
-----------------------------------------------------

You need to create a form factor file as described in the
":ref:`bsp-guide/bsp:miscellaneous bsp-specific recipe files`" section in
the Yocto Project Board Support Packages (BSP) Developer's Guide. Set
the ``HAVE_TOUCHSCREEN`` variable equal to one as follows::

   HAVE_TOUCHSCREEN=1

How to always bring up connected network interfaces?
----------------------------------------------------

The default interfaces file provided by the netbase recipe does
not automatically bring up network interfaces. Therefore, you will need
to add a BSP-specific netbase that includes an interfaces file. See the
":ref:`bsp-guide/bsp:miscellaneous bsp-specific recipe files`" section in
the Yocto Project Board Support Packages (BSP) Developer's Guide for
information on creating these types of miscellaneous recipe files.

For example, add the following files to your layer::

   meta-MACHINE/recipes-bsp/netbase/netbase/MACHINE/interfaces
   meta-MACHINE/recipes-bsp/netbase/netbase_5.0.bbappend
