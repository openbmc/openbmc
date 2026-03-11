.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

******************************************
Yocto Project Profiling and Tracing Manual
******************************************

Introduction
============

Yocto Project bundles a number of tracing and profiling tools --- this manual
describes their basic usage and shows by example how to make use of them
to analyze application and system behavior.

The tools presented are, for the most part, completely open-ended and have
quite good and/or extensive documentation of their own which can be used
to solve just about any problem you might come across in Linux. Each
section that describes a particular tool has links to that tool's
documentation and website.

The purpose of this manual is to present a set of common and generally
useful tracing and profiling idioms along with their application (as
appropriate) to each tool, in the context of a general-purpose
'drill-down' methodology that can be applied to solving a large number
of problems. For help with more advanced usages and problems,
refer to the documentation and/or websites provided for each tool.

The final section of this manual is a collection of real-world examples
which we'll be continually updating as we solve more problems using the
tools --- feel free to suggest additions to what you read here.

General Setup
=============

Most of the tools are available only in ``sdk`` images or in images built
after adding ``tools-profile`` to your ``local.conf`` file. So, in order to be able
to access all of the tools described here, you can build and boot
an ``sdk`` image, perhaps one of::

   $ bitbake core-image-sato-sdk
   $ bitbake core-image-weston-sdk
   $ bitbake core-image-rt-sdk

Alternatively,  you can add ``tools-profile`` to the :term:`EXTRA_IMAGE_FEATURES` line in
your ``local.conf`` file::

   EXTRA_IMAGE_FEATURES:append = " tools-profile"

If you use the ``tools-profile`` method, you don't need to build an sdk image ---
the tracing and profiling tools will be included in non-sdk images as well e.g.::

   $ bitbake core-image-sato

.. note::

   By default, the Yocto build system strips symbols from the binaries
   it packages, which makes it difficult to use some of the tools.

   You can prevent that by setting the
   :term:`INHIBIT_PACKAGE_STRIP`
   variable to "1" in your ``local.conf`` when you build the image::

      INHIBIT_PACKAGE_STRIP = "1"

   The above setting will noticeably increase the size of your image.

If you've already built a stripped image, you can generate debug
packages (xxx-dbg) which you can manually install as needed.

To generate debug info for packages, you can add ``dbg-pkgs`` to
:term:`EXTRA_IMAGE_FEATURES` in ``local.conf``. For example::

   EXTRA_IMAGE_FEATURES:append = " dbg-pkgs"

Additionally, in order to generate the right type of debug info, we also need to
set :term:`PACKAGE_DEBUG_SPLIT_STYLE` in the ``local.conf`` file::

   PACKAGE_DEBUG_SPLIT_STYLE = 'debug-file-directory'
