.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

************
Introduction
************

Toaster is a web interface to the Yocto Project's
:term:`OpenEmbedded Build System`. The interface
enables you to configure and run your builds. Information about builds
is collected and stored in a database. You can use Toaster to configure
and start builds on multiple remote build servers.

.. _intro-features:

Toaster Features
================

Toaster allows you to configure and run builds, and it provides
extensive information about the build process.

-  *Configure and Run Builds:* You can use the Toaster web interface to
   configure and start your builds. Builds started using the Toaster web
   interface are organized into projects. When you create a project, you
   are asked to select a release, or version of the build system you
   want to use for the project builds. As shipped, Toaster supports
   Yocto Project releases 1.8 and beyond. With the Toaster web
   interface, you can:

   -  Browse layers listed in the various
      :ref:`layer sources <toaster-manual/toaster-manual-reference:layer source>`
      that are available in your project (e.g. the OpenEmbedded Layer Index at
      http://layers.openembedded.org/layerindex/).

   -  Browse images, recipes, and machines provided by those layers.

   -  Import your own layers for building.

   -  Add and remove layers from your configuration.

   -  Set configuration variables.

   -  Select a target or multiple targets to build.

   -  Start your builds.

   Toaster also allows you to configure and run your builds from the
   command line, and switch between the command line and the web
   interface at any time. Builds started from the command line appear
   within a special Toaster project called "Command line builds".

-  *Information About the Build Process:* Toaster also records extensive
   information about your builds. Toaster collects data for builds you
   start from the web interface and from the command line as long as
   Toaster is running.

   .. note::

      You must start Toaster before the build or it will not collect
      build data.

   With Toaster you can:

   -  See what was built (recipes and packages) and what packages were
      installed into your final image.

   -  Browse the directory structure of your image.

   -  See the value of all variables in your build configuration, and
      which files set each value.

   -  Examine error, warning, and trace messages to aid in debugging.

   -  See information about the BitBake tasks executed and reused during
      your build, including those that used shared state.

   -  See dependency relationships between recipes, packages, and tasks.

   -  See performance information such as build time, task time, CPU
      usage, and disk I/O.

For an overview of Toaster shipped with the Yocto Project &DISTRO;
Release, see the "`Toaster - Yocto Project
2.2 <https://youtu.be/BlXdOYLgPxA>`__" video.

.. _toaster-installation-options:

Installation Options
====================

You can set Toaster up to run as a local instance or as a shared hosted
service.

When Toaster is set up as a local instance, all the components reside on
a single build host. Fundamentally, a local instance of Toaster is
suited for a single user developing on a single build host.

.. image:: figures/simple-configuration.png
   :align: center

Toaster as a hosted service is suited for multiple users developing
across several build hosts. When Toaster is set up as a hosted service,
its components can be spread across several machines:

.. image:: figures/hosted-service.png
   :align: center
