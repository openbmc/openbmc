.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Enabling GObject Introspection Support
**************************************

`GObject introspection <https://gi.readthedocs.io/en/latest/>`__
is the standard mechanism for accessing GObject-based software from
runtime environments. GObject is a feature of the GLib library that
provides an object framework for the GNOME desktop and related software.
GObject Introspection adds information to GObject that allows objects
created within it to be represented across different programming
languages. If you want to construct GStreamer pipelines using Python, or
control UPnP infrastructure using Javascript and GUPnP, GObject
introspection is the only way to do it.

This section describes the Yocto Project support for generating and
packaging GObject introspection data. GObject introspection data is a
description of the API provided by libraries built on top of the GLib
framework, and, in particular, that framework's GObject mechanism.
GObject Introspection Repository (GIR) files go to ``-dev`` packages,
``typelib`` files go to main packages as they are packaged together with
libraries that are introspected.

The data is generated when building such a library, by linking the
library with a small executable binary that asks the library to describe
itself, and then executing the binary and processing its output.

Generating this data in a cross-compilation environment is difficult
because the library is produced for the target architecture, but its
code needs to be executed on the build host. This problem is solved with
the OpenEmbedded build system by running the code through QEMU, which
allows precisely that. Unfortunately, QEMU does not always work
perfectly as mentioned in the ":ref:`dev-manual/gobject-introspection:known issues`"
section.

Enabling the Generation of Introspection Data
=============================================

Enabling the generation of introspection data (GIR files) in your
library package involves the following:

#. Inherit the :ref:`ref-classes-gobject-introspection` class.

#. Make sure introspection is not disabled anywhere in the recipe or
   from anything the recipe includes. Also, make sure that
   "gobject-introspection-data" is not in
   :term:`DISTRO_FEATURES_BACKFILL_CONSIDERED`
   and that "qemu-usermode" is not in
   :term:`MACHINE_FEATURES_BACKFILL_CONSIDERED`.
   In either of these conditions, nothing will happen.

#. Try to build the recipe. If you encounter build errors that look like
   something is unable to find ``.so`` libraries, check where these
   libraries are located in the source tree and add the following to the
   recipe::

      GIR_EXTRA_LIBS_PATH = "${B}/something/.libs"

   .. note::

      See recipes in the ``oe-core`` repository that use that
      :term:`GIR_EXTRA_LIBS_PATH` variable as an example.

#. Look for any other errors, which probably mean that introspection
   support in a package is not entirely standard, and thus breaks down
   in a cross-compilation environment. For such cases, custom-made fixes
   are needed. A good place to ask and receive help in these cases is
   the :ref:`Yocto Project mailing
   lists <resources-mailinglist>`.

.. note::

   Using a library that no longer builds against the latest Yocto
   Project release and prints introspection related errors is a good
   candidate for the previous procedure.

Disabling the Generation of Introspection Data
==============================================

You might find that you do not want to generate introspection data. Or,
perhaps QEMU does not work on your build host and target architecture
combination. If so, you can use either of the following methods to
disable GIR file generations:

-  Add the following to your distro configuration::

      DISTRO_FEATURES_BACKFILL_CONSIDERED = "gobject-introspection-data"

   Adding this statement disables generating introspection data using
   QEMU but will still enable building introspection tools and libraries
   (i.e. building them does not require the use of QEMU).

-  Add the following to your machine configuration::

      MACHINE_FEATURES_BACKFILL_CONSIDERED = "qemu-usermode"

   Adding this statement disables the use of QEMU when building packages for your
   machine. Currently, this feature is used only by introspection
   recipes and has the same effect as the previously described option.

   .. note::

      Future releases of the Yocto Project might have other features
      affected by this option.

If you disable introspection data, you can still obtain it through other
means such as copying the data from a suitable sysroot, or by generating
it on the target hardware. The OpenEmbedded build system does not
currently provide specific support for these techniques.

Testing that Introspection Works in an Image
============================================

Use the following procedure to test if generating introspection data is
working in an image:

#. Make sure that "gobject-introspection-data" is not in
   :term:`DISTRO_FEATURES_BACKFILL_CONSIDERED`
   and that "qemu-usermode" is not in
   :term:`MACHINE_FEATURES_BACKFILL_CONSIDERED`.

#. Build ``core-image-sato``.

#. Launch a Terminal and then start Python in the terminal.

#. Enter the following in the terminal::

      >>> from gi.repository import GLib
      >>> GLib.get_host_name()

#. For something a little more advanced, enter the following see:
   https://python-gtk-3-tutorial.readthedocs.io/en/latest/introduction.html

Known Issues
============

Here are know issues in GObject Introspection Support:

-  ``qemu-ppc64`` immediately crashes. Consequently, you cannot build
   introspection data on that architecture.

-  x32 is not supported by QEMU. Consequently, introspection data is
   disabled.

-  musl causes transient GLib binaries to crash on assertion failures.
   Consequently, generating introspection data is disabled.

-  Because QEMU is not able to run the binaries correctly, introspection
   is disabled for some specific packages under specific architectures
   (e.g. ``gcr``, ``libsecret``, and ``webkit``).

-  QEMU usermode might not work properly when running 64-bit binaries
   under 32-bit host machines. In particular, "qemumips64" is known to
   not work under i686.

