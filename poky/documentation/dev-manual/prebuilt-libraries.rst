.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Working with Pre-Built Libraries
********************************

Introduction
============

Some library vendors do not release source code for their software but do
release pre-built binaries. When shared libraries are built, they should
be versioned (see `this article
<https://tldp.org/HOWTO/Program-Library-HOWTO/shared-libraries.html>`__
for some background), but sometimes this is not done.

To summarize, a versioned library must meet two conditions:

#.    The filename must have the version appended, for example: ``libfoo.so.1.2.3``.
#.    The library must have the ELF tag ``SONAME`` set to the major version
      of the library, for example: ``libfoo.so.1``. You can check this by
      running ``readelf -d filename | grep SONAME``.

This section shows how to deal with both versioned and unversioned
pre-built libraries.

Versioned Libraries
===================

In this example we work with pre-built libraries for the FT4222H USB I/O chip.
Libraries are built for several target architecture variants and packaged in
an archive as follows::

   ├── build-arm-hisiv300
   │   └── libft4222.so.1.4.4.44
   ├── build-arm-v5-sf
   │   └── libft4222.so.1.4.4.44
   ├── build-arm-v6-hf
   │   └── libft4222.so.1.4.4.44
   ├── build-arm-v7-hf
   │   └── libft4222.so.1.4.4.44
   ├── build-arm-v8
   │   └── libft4222.so.1.4.4.44
   ├── build-i386
   │   └── libft4222.so.1.4.4.44
   ├── build-i486
   │   └── libft4222.so.1.4.4.44
   ├── build-mips-eglibc-hf
   │   └── libft4222.so.1.4.4.44
   ├── build-pentium
   │   └── libft4222.so.1.4.4.44
   ├── build-x86_64
   │   └── libft4222.so.1.4.4.44
   ├── examples
   │   ├── get-version.c
   │   ├── i2cm.c
   │   ├── spim.c
   │   └── spis.c
   ├── ftd2xx.h
   ├── install4222.sh
   ├── libft4222.h
   ├── ReadMe.txt
   └── WinTypes.h

To write a recipe to use such a library in your system:

-  The vendor will probably have a proprietary licence, so set
   :term:`LICENSE_FLAGS` in your recipe.
-  The vendor provides a tarball containing libraries so set :term:`SRC_URI`
   appropriately.
-  Set :term:`COMPATIBLE_HOST` so that the recipe cannot be used with an
   unsupported architecture. In the following example, we only support the 32
   and 64 bit variants of the ``x86`` architecture.
-  As the vendor provides versioned libraries, we can use ``oe_soinstall``
   from :ref:`ref-classes-utils` to install the shared library and create
   symbolic links. If the vendor does not do this, we need to follow the
   non-versioned library guidelines in the next section.
-  As the vendor likely used :term:`LDFLAGS` different from those in your Yocto
   Project build, disable the corresponding checks by adding ``ldflags``
   to :term:`INSANE_SKIP`.
-  The vendor will typically ship release builds without debugging symbols.
   Avoid errors by preventing the packaging task from stripping out the symbols
   and adding them to a separate debug package. This is done by setting the
   ``INHIBIT_`` flags shown below.

The complete recipe would look like this::

   SUMMARY = "FTDI FT4222H Library"
   SECTION = "libs"
   LICENSE_FLAGS = "ftdi"
   LICENSE = "CLOSED"

   COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

   # Sources available in a .tgz file in .zip archive
   # at https://ftdichip.com/wp-content/uploads/2021/01/libft4222-linux-1.4.4.44.zip
   # Found on https://ftdichip.com/software-examples/ft4222h-software-examples/
   # Since dealing with this particular type of archive is out of topic here,
   # we use a local link.
   SRC_URI = "file://libft4222-linux-${PV}.tgz"

   S = "${WORKDIR}"

   ARCH_DIR:x86-64 = "build-x86_64"
   ARCH_DIR:i586 = "build-i386"
   ARCH_DIR:i686 = "build-i386"

   INSANE_SKIP:${PN} = "ldflags"
   INHIBIT_PACKAGE_STRIP = "1"
   INHIBIT_SYSROOT_STRIP = "1"
   INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

   do_install () {
           install -m 0755 -d ${D}${libdir}
           oe_soinstall ${S}/${ARCH_DIR}/libft4222.so.${PV} ${D}${libdir}
           install -d ${D}${includedir}
           install -m 0755 ${S}/*.h ${D}${includedir}
   }

If the precompiled binaries are not statically linked and have dependencies on
other libraries, then by adding those libraries to :term:`DEPENDS`, the linking
can be examined and the appropriate :term:`RDEPENDS` automatically added.

Non-Versioned Libraries
=======================

Some Background
---------------

Libraries in Linux systems are generally versioned so that it is possible
to have multiple versions of the same library installed, which eases upgrades
and support for older software. For example, suppose that in a versioned
library, an actual library is called ``libfoo.so.1.2``, a symbolic link named
``libfoo.so.1`` points to ``libfoo.so.1.2``, and a symbolic link named
``libfoo.so`` points to ``libfoo.so.1.2``. Given these conditions, when you
link a binary against a library, you typically provide the unversioned file
name (i.e. ``-lfoo`` to the linker). However, the linker follows the symbolic
link and actually links against the versioned filename. The unversioned symbolic
link is only used at development time. Consequently, the library is packaged
along with the headers in the development package ``${PN}-dev`` along with the
actual library and versioned symbolic links in ``${PN}``. Because versioned
libraries are far more common than unversioned libraries, the default packaging
rules assume versioned libraries.

Yocto Library Packaging Overview
--------------------------------

It follows that packaging an unversioned library requires a bit of work in the
recipe. By default, ``libfoo.so`` gets packaged into ``${PN}-dev``, which
triggers a QA warning that a non-symlink library is in a ``-dev`` package,
and binaries in the same recipe link to the library in ``${PN}-dev``,
which triggers more QA warnings. To solve this problem, you need to package the
unversioned library into ``${PN}`` where it belongs. The abridged
default :term:`FILES` variables in ``bitbake.conf`` are::

   SOLIBS = ".so.*"
   SOLIBSDEV = ".so"
   FILES:${PN} = "... ${libdir}/lib*${SOLIBS} ..."
   FILES_SOLIBSDEV ?= "... ${libdir}/lib*${SOLIBSDEV} ..."
   FILES:${PN}-dev = "... ${FILES_SOLIBSDEV} ..."

:term:`SOLIBS` defines a pattern that matches real shared object libraries.
:term:`SOLIBSDEV` matches the development form (unversioned symlink). These two
variables are then used in ``FILES:${PN}`` and ``FILES:${PN}-dev``, which puts
the real libraries into ``${PN}`` and the unversioned symbolic link into ``${PN}-dev``.
To package unversioned libraries, you need to modify the variables in the recipe
as follows::

   SOLIBS = ".so"
   FILES_SOLIBSDEV = ""

The modifications cause the ``.so`` file to be the real library
and unset :term:`FILES_SOLIBSDEV` so that no libraries get packaged into
``${PN}-dev``. The changes are required because unless :term:`PACKAGES` is changed,
``${PN}-dev`` collects files before `${PN}`. ``${PN}-dev`` must not collect any of
the files you want in ``${PN}``.

Finally, loadable modules, essentially unversioned libraries that are linked
at runtime using ``dlopen()`` instead of at build time, should generally be
installed in a private directory. However, if they are installed in ``${libdir}``,
then the modules can be treated as unversioned libraries.

Example
-------

The example below installs an unversioned x86-64 pre-built library named
``libfoo.so``. The :term:`COMPATIBLE_HOST` variable limits recipes to the
x86-64 architecture while the :term:`INSANE_SKIP`, :term:`INHIBIT_PACKAGE_STRIP`
and :term:`INHIBIT_SYSROOT_STRIP` variables are all set as in the above
versioned library example. The "magic" is setting the :term:`SOLIBS` and
:term:`FILES_SOLIBSDEV` variables as explained above::

   SUMMARY = "libfoo sample recipe"
   SECTION = "libs"
   LICENSE = "CLOSED"

   SRC_URI = "file://libfoo.so"

   COMPATIBLE_HOST = "x86_64.*-linux"

   INSANE_SKIP:${PN} = "ldflags"
   INHIBIT_PACKAGE_STRIP = "1"
   INHIBIT_SYSROOT_STRIP = "1"
   SOLIBS = ".so"
   FILES_SOLIBSDEV = ""

   do_install () {
           install -d ${D}${libdir}
           install -m 0755 ${WORKDIR}/libfoo.so ${D}${libdir}
   }

