.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Working With Libraries
**********************

Libraries are an integral part of your system. This section describes
some common practices you might find helpful when working with libraries
to build your system:

-  :ref:`How to include static library files
   <dev-manual/libraries:including static library files>`

-  :ref:`How to use the Multilib feature to combine multiple versions of
   library files into a single image
   <dev-manual/libraries:combining multiple versions of library files into one image>`

-  :ref:`How to install multiple versions of the same library in parallel on
   the same system
   <dev-manual/libraries:installing multiple versions of the same library>`

Including Static Library Files
==============================

If you are building a library and the library offers static linking, you
can control which static library files (``*.a`` files) get included in
the built library.

The :term:`PACKAGES` and
:term:`FILES:* <FILES>` variables in the
``meta/conf/bitbake.conf`` configuration file define how files installed
by the :ref:`ref-tasks-install` task are packaged. By default, the :term:`PACKAGES`
variable includes ``${PN}-staticdev``, which represents all static
library files.

.. note::

   Some previously released versions of the Yocto Project defined the
   static library files through ``${PN}-dev``.

Here is the part of the BitBake configuration file, where you can see
how the static library files are defined::

   PACKAGE_BEFORE_PN ?= ""
   PACKAGES = "${PN}-src ${PN}-dbg ${PN}-staticdev ${PN}-dev ${PN}-doc ${PN}-locale ${PACKAGE_BEFORE_PN} ${PN}"
   PACKAGES_DYNAMIC = "^${PN}-locale-.*"
   FILES = ""

   FILES:${PN} = "${bindir}/* ${sbindir}/* ${libexecdir}/* ${libdir}/lib*${SOLIBS} \
               ${sysconfdir} ${sharedstatedir} ${localstatedir} \
               ${base_bindir}/* ${base_sbindir}/* \
               ${base_libdir}/*${SOLIBS} \
               ${base_prefix}/lib/udev ${prefix}/lib/udev \
               ${base_libdir}/udev ${libdir}/udev \
               ${datadir}/${BPN} ${libdir}/${BPN}/* \
               ${datadir}/pixmaps ${datadir}/applications \
               ${datadir}/idl ${datadir}/omf ${datadir}/sounds \
               ${libdir}/bonobo/servers"

   FILES:${PN}-bin = "${bindir}/* ${sbindir}/*"

   FILES:${PN}-doc = "${docdir} ${mandir} ${infodir} ${datadir}/gtk-doc \
               ${datadir}/gnome/help"
   SECTION:${PN}-doc = "doc"

   FILES_SOLIBSDEV ?= "${base_libdir}/lib*${SOLIBSDEV} ${libdir}/lib*${SOLIBSDEV}"
   FILES:${PN}-dev = "${includedir} ${FILES_SOLIBSDEV} ${libdir}/*.la \
                   ${libdir}/*.o ${libdir}/pkgconfig ${datadir}/pkgconfig \
                   ${datadir}/aclocal ${base_libdir}/*.o \
                   ${libdir}/${BPN}/*.la ${base_libdir}/*.la \
                   ${libdir}/cmake ${datadir}/cmake"
   SECTION:${PN}-dev = "devel"
   ALLOW_EMPTY:${PN}-dev = "1"
   RDEPENDS:${PN}-dev = "${PN} (= ${EXTENDPKGV})"

   FILES:${PN}-staticdev = "${libdir}/*.a ${base_libdir}/*.a ${libdir}/${BPN}/*.a"
   SECTION:${PN}-staticdev = "devel"
   RDEPENDS:${PN}-staticdev = "${PN}-dev (= ${EXTENDPKGV})"

Combining Multiple Versions of Library Files into One Image
===========================================================

The build system offers the ability to build libraries with different
target optimizations or architecture formats and combine these together
into one system image. You can link different binaries in the image
against the different libraries as needed for specific use cases. This
feature is called "Multilib".

An example would be where you have most of a system compiled in 32-bit
mode using 32-bit libraries, but you have something large, like a
database engine, that needs to be a 64-bit application and uses 64-bit
libraries. Multilib allows you to get the best of both 32-bit and 64-bit
libraries.

While the Multilib feature is most commonly used for 32 and 64-bit
differences, the approach the build system uses facilitates different
target optimizations. You could compile some binaries to use one set of
libraries and other binaries to use a different set of libraries. The
libraries could differ in architecture, compiler options, or other
optimizations.

There are several examples in the ``meta-skeleton`` layer found in the
:term:`Source Directory`:

-  :oe_git:`conf/multilib-example.conf </openembedded-core/tree/meta-skeleton/conf/multilib-example.conf>`
   configuration file.

-  :oe_git:`conf/multilib-example2.conf </openembedded-core/tree/meta-skeleton/conf/multilib-example2.conf>`
   configuration file.

-  :oe_git:`recipes-multilib/images/core-image-multilib-example.bb </openembedded-core/tree/meta-skeleton/recipes-multilib/images/core-image-multilib-example.bb>`
   recipe

Preparing to Use Multilib
-------------------------

User-specific requirements drive the Multilib feature. Consequently,
there is no one "out-of-the-box" configuration that would
meet your needs.

In order to enable Multilib, you first need to ensure your recipe is
extended to support multiple libraries. Many standard recipes are
already extended and support multiple libraries. You can check in the
``meta/conf/multilib.conf`` configuration file in the
:term:`Source Directory` to see how this is
done using the
:term:`BBCLASSEXTEND` variable.
Eventually, all recipes will be covered and this list will not be
needed.

For the most part, the :ref:`Multilib <ref-classes-multilib*>`
class extension works automatically to
extend the package name from ``${PN}`` to ``${MLPREFIX}${PN}``, where
:term:`MLPREFIX` is the particular multilib (e.g. "lib32-" or "lib64-").
Standard variables such as
:term:`DEPENDS`,
:term:`RDEPENDS`,
:term:`RPROVIDES`,
:term:`RRECOMMENDS`,
:term:`PACKAGES`, and
:term:`PACKAGES_DYNAMIC` are
automatically extended by the system. If you are extending any manual
code in the recipe, you can use the ``${MLPREFIX}`` variable to ensure
those names are extended correctly.

Using Multilib
--------------

After you have set up the recipes, you need to define the actual
combination of multiple libraries you want to build. You accomplish this
through your ``local.conf`` configuration file in the
:term:`Build Directory`. An example configuration would be as follows::

   MACHINE = "qemux86-64"
   require conf/multilib.conf
   MULTILIBS = "multilib:lib32"
   DEFAULTTUNE:virtclass-multilib-lib32 = "x86"
   IMAGE_INSTALL:append = " lib32-glib-2.0"

This example enables an additional library named
``lib32`` alongside the normal target packages. When combining these
"lib32" alternatives, the example uses "x86" for tuning. For information
on this particular tuning, see
``meta/conf/machine/include/ia32/arch-ia32.inc``.

The example then includes ``lib32-glib-2.0`` in all the images, which
illustrates one method of including a multiple library dependency. You
can use a normal image build to include this dependency, for example::

   $ bitbake core-image-sato

You can also build Multilib packages
specifically with a command like this::

   $ bitbake lib32-glib-2.0

Additional Implementation Details
---------------------------------

There are generic implementation details as well as details that are specific to
package management systems. Here are implementation details
that exist regardless of the package management system:

-  The typical convention used for the class extension code as used by
   Multilib assumes that all package names specified in
   :term:`PACKAGES` that contain
   ``${PN}`` have ``${PN}`` at the start of the name. When that
   convention is not followed and ``${PN}`` appears at the middle or the
   end of a name, problems occur.

-  The :term:`TARGET_VENDOR`
   value under Multilib will be extended to "-vendormlmultilib" (e.g.
   "-pokymllib32" for a "lib32" Multilib with Poky). The reason for this
   slightly unwieldy contraction is that any "-" characters in the
   vendor string presently break Autoconf's ``config.sub``, and other
   separators are problematic for different reasons.

Here are the implementation details for the RPM Package Management System:

-  A unique architecture is defined for the Multilib packages, along
   with creating a unique deploy folder under ``tmp/deploy/rpm`` in the
   :term:`Build Directory`. For example, consider ``lib32`` in a
   ``qemux86-64`` image. The possible architectures in the system are "all",
   "qemux86_64", "lib32:qemux86_64", and "lib32:x86".

-  The ``${MLPREFIX}`` variable is stripped from ``${PN}`` during RPM
   packaging. The naming for a normal RPM package and a Multilib RPM
   package in a ``qemux86-64`` system resolves to something similar to
   ``bash-4.1-r2.x86_64.rpm`` and ``bash-4.1.r2.lib32_x86.rpm``,
   respectively.

-  When installing a Multilib image, the RPM backend first installs the
   base image and then installs the Multilib libraries.

-  The build system relies on RPM to resolve the identical files in the
   two (or more) Multilib packages.

Here are the implementation details for the IPK Package Management System:

-  The ``${MLPREFIX}`` is not stripped from ``${PN}`` during IPK
   packaging. The naming for a normal RPM package and a Multilib IPK
   package in a ``qemux86-64`` system resolves to something like
   ``bash_4.1-r2.x86_64.ipk`` and ``lib32-bash_4.1-rw:x86.ipk``,
   respectively.

-  The IPK deploy folder is not modified with ``${MLPREFIX}`` because
   packages with and without the Multilib feature can exist in the same
   folder due to the ``${PN}`` differences.

-  IPK defines a sanity check for Multilib installation using certain
   rules for file comparison, overridden, etc.

Installing Multiple Versions of the Same Library
================================================

There are be situations where you need to install and use multiple versions
of the same library on the same system at the same time. This
almost always happens when a library API changes and you have
multiple pieces of software that depend on the separate versions of the
library. To accommodate these situations, you can install multiple
versions of the same library in parallel on the same system.

The process is straightforward as long as the libraries use proper
versioning. With properly versioned libraries, all you need to do to
individually specify the libraries is create separate, appropriately
named recipes where the :term:`PN` part of
the name includes a portion that differentiates each library version
(e.g. the major part of the version number). Thus, instead of having a
single recipe that loads one version of a library (e.g. ``clutter``),
you provide multiple recipes that result in different versions of the
libraries you want. As an example, the following two recipes would allow
the two separate versions of the ``clutter`` library to co-exist on the
same system:

.. code-block:: none

   clutter-1.6_1.6.20.bb
   clutter-1.8_1.8.4.bb

Additionally, if
you have other recipes that depend on a given library, you need to use
the :term:`DEPENDS` variable to
create the dependency. Continuing with the same example, if you want to
have a recipe depend on the 1.8 version of the ``clutter`` library, use
the following in your recipe::

   DEPENDS = "clutter-1.8"

