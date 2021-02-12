.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*******************
System Requirements
*******************

Welcome to the Yocto Project Reference Manual! This manual provides
reference information for the current release of the Yocto Project, and
is most effectively used after you have an understanding of the basics
of the Yocto Project. The manual is neither meant to be read as a
starting point to the Yocto Project, nor read from start to finish.
Rather, use this manual to find variable definitions, class
descriptions, and so forth as needed during the course of using the
Yocto Project.

For introductory information on the Yocto Project, see the
:yocto_home:`Yocto Project Website <>` and the
":ref:`overview-manual/development-environment:the yocto project development environment`"
chapter in the Yocto Project Overview and Concepts Manual.

If you want to use the Yocto Project to quickly build an image without
having to understand concepts, work through the
:doc:`/brief-yoctoprojectqs/index` document. You can find "how-to"
information in the :doc:`/dev-manual/index`. You can find Yocto Project overview
and conceptual information in the :doc:`/overview-manual/index`.

.. note::

   For more information about the Yocto Project Documentation set, see
   the :ref:`ref-manual/resources:links and related documentation` section.

.. _detailed-supported-distros:

Supported Linux Distributions
=============================

Currently, the Yocto Project is supported on the following
distributions:

-  Ubuntu 16.04 (LTS)

-  Ubuntu 18.04 (LTS)

-  Ubuntu 20.04

-  Fedora 30

-  Fedora 31

-  Fedora 32

-  CentOS 7.x

-  CentOS 8.x

-  Debian GNU/Linux 8.x (Jessie)

-  Debian GNU/Linux 9.x (Stretch)

-  Debian GNU/Linux 10.x (Buster)

-  OpenSUSE Leap 15.1


.. note::

   -  While the Yocto Project Team attempts to ensure all Yocto Project
      releases are one hundred percent compatible with each officially
      supported Linux distribution, instances might exist where you
      encounter a problem while using the Yocto Project on a specific
      distribution.

   -  Yocto Project releases are tested against the stable Linux
      distributions in the above list. The Yocto Project should work
      on other distributions but validation is not performed against
      them.

   -  In particular, the Yocto Project does not support and currently
      has no plans to support rolling-releases or development
      distributions due to their constantly changing nature. We welcome
      patches and bug reports, but keep in mind that our priority is on
      the supported platforms listed below.

   -  You may use Windows Subsystem For Linux v2 to set up a build host
      using Windows 10, but validation is not performed against build
      hosts using WSLv2.

   -  The Yocto Project is not compatible with WSLv1, it is
      compatible but not officially supported nor validated with
      WSLv2, if you still decide to use WSL please upgrade to WSLv2.

   -  If you encounter problems, please go to :yocto_bugs:`Yocto Project
      Bugzilla <>` and submit a bug. We are
      interested in hearing about your experience. For information on
      how to submit a bug, see the Yocto Project
      :yocto_wiki:`Bugzilla wiki page </Bugzilla_Configuration_and_Bug_Tracking>`
      and the ":ref:`dev-manual/common-tasks:submitting a defect against the yocto project`"
      section in the Yocto Project Development Tasks Manual.


Required Packages for the Build Host
====================================

The list of packages you need on the host development system can be
large when covering all build scenarios using the Yocto Project. This
section describes required packages according to Linux distribution and
function.

.. _ubuntu-packages:

Ubuntu and Debian
-----------------

The following list shows the required packages by function given a
supported Ubuntu or Debian Linux distribution:

.. note::

   -  If your build system has the ``oss4-dev`` package installed, you
      might experience QEMU build failures due to the package installing
      its own custom ``/usr/include/linux/soundcard.h`` on the Debian
      system. If you run into this situation, either of the following
      solutions exist:
      ::

         $ sudo apt-get build-dep qemu
         $ sudo apt-get remove oss4-dev

   -  For Debian-8, ``python3-git`` and ``pylint3`` are no longer
      available via ``apt-get``.
      ::

         $ sudo pip3 install GitPython pylint==1.9.5

-  *Essentials:* Packages needed to build an image on a headless system:
   ::

      $ sudo apt-get install &UBUNTU_HOST_PACKAGES_ESSENTIAL;

-  *Documentation:* Packages needed if you are going to build out the
   Yocto Project documentation manuals:
   ::

      $ sudo apt-get install make python3-pip
      &PIP3_HOST_PACKAGES_DOC;

   .. note::

      It is currently not possible to build out documentation from Debian 8
      (Jessie) because of outdated ``pip3`` and ``python3``. ``python3-sphinx``
      is too outdated.

Fedora Packages
---------------

The following list shows the required packages by function given a
supported Fedora Linux distribution:

-  *Essentials:* Packages needed to build an image for a headless
   system:
   ::

      $ sudo dnf install &FEDORA_HOST_PACKAGES_ESSENTIAL;

-  *Documentation:* Packages needed if you are going to build out the
   Yocto Project documentation manuals:
   ::

      $ sudo dnf install make python3-pip which
      &PIP3_HOST_PACKAGES_DOC;

openSUSE Packages
-----------------

The following list shows the required packages by function given a
supported openSUSE Linux distribution:

-  *Essentials:* Packages needed to build an image for a headless
   system:
   ::

      $ sudo zypper install &OPENSUSE_HOST_PACKAGES_ESSENTIAL;

-  *Documentation:* Packages needed if you are going to build out the
   Yocto Project documentation manuals:
   ::

      $ sudo zypper install make python3-pip which
      &PIP3_HOST_PACKAGES_DOC;


CentOS-7 Packages
-----------------

The following list shows the required packages by function given a
supported CentOS-7 Linux distribution:

-  *Essentials:* Packages needed to build an image for a headless
   system:
   ::

      $ sudo yum install &CENTOS7_HOST_PACKAGES_ESSENTIAL;

   .. note::

      -  Extra Packages for Enterprise Linux (i.e. ``epel-release``) is
         a collection of packages from Fedora built on RHEL/CentOS for
         easy installation of packages not included in enterprise Linux
         by default. You need to install these packages separately.

      -  The ``makecache`` command consumes additional Metadata from
         ``epel-release``.

-  *Documentation:* Packages needed if you are going to build out the
   Yocto Project documentation manuals:
   ::

      $ sudo yum install make python3-pip which
      &PIP3_HOST_PACKAGES_DOC;

CentOS-8 Packages
-----------------

The following list shows the required packages by function given a
supported CentOS-8 Linux distribution:

-  *Essentials:* Packages needed to build an image for a headless
   system:
   ::

      $ sudo dnf install &CENTOS8_HOST_PACKAGES_ESSENTIAL;

   .. note::

      -  Extra Packages for Enterprise Linux (i.e. ``epel-release``) is
         a collection of packages from Fedora built on RHEL/CentOS for
         easy installation of packages not included in enterprise Linux
         by default. You need to install these packages separately.

      -  The ``PowerTools`` repo provides additional packages such as
         ``rpcgen`` and ``texinfo``.

      -  The ``makecache`` command consumes additional Metadata from
         ``epel-release``.

-  *Documentation:* Packages needed if you are going to build out the
   Yocto Project documentation manuals:
   ::

      $ sudo dnf install make python3-pip which
      &PIP3_HOST_PACKAGES_DOC;

Required Git, tar, Python and gcc Versions
==========================================

In order to use the build system, your host development system must meet
the following version requirements for Git, tar, and Python:

-  Git 1.8.3.1 or greater

-  tar 1.28 or greater

-  Python 3.5.0 or greater

If your host development system does not meet all these requirements,
you can resolve this by installing a ``buildtools`` tarball that
contains these tools. You can get the tarball one of two ways: download
a pre-built tarball or use BitBake to build the tarball.

In addition, your host development system must meet the following
version requirement for gcc:

-  gcc 5.0 or greater

If your host development system does not meet this requirement, you can
resolve this by installing a ``buildtools-extended`` tarball that
contains additional tools, the equivalent of ``buildtools-essential``.

Installing a Pre-Built ``buildtools`` Tarball with ``install-buildtools`` script
--------------------------------------------------------------------------------

The ``install-buildtools`` script is the easiest of the three methods by
which you can get these tools. It downloads a pre-built buildtools
installer and automatically installs the tools for you:

1. Execute the ``install-buildtools`` script. Here is an example:
   ::

      $ cd poky
      $ scripts/install-buildtools --without-extended-buildtools \
        --base-url &YOCTO_DL_URL;/releases/yocto \
        --release yocto-&DISTRO; \
        --installer-version &DISTRO;

   During execution, the buildtools tarball will be downloaded, the
   checksum of the download will be verified, the installer will be run
   for you, and some basic checks will be run to to make sure the
   installation is functional.

   To avoid the need of ``sudo`` privileges, the ``install-buildtools``
   script will by default tell the installer to install in:
   ::

      /path/to/poky/buildtools

   If your host development system needs the additional tools provided
   in the ``buildtools-extended`` tarball, you can instead execute the
   ``install-buildtools`` script with the default parameters:
   ::

      $ cd poky
      $ scripts/install-buildtools

2. Source the tools environment setup script by using a command like the
   following:
   ::

      $ source /path/to/poky/buildtools/environment-setup-x86_64-pokysdk-linux

   Of course, you need to supply your installation directory and be sure to
   use the right file (i.e. i586 or x86_64).

   After you have sourced the setup script, the tools are added to
   ``PATH`` and any other environment variables required to run the
   tools are initialized. The results are working versions versions of
   Git, tar, Python and ``chrpath``. And in the case of the
   ``buildtools-extended`` tarball, additional working versions of tools
   including ``gcc``, ``make`` and the other tools included in
   ``packagegroup-core-buildessential``.

Downloading a Pre-Built ``buildtools`` Tarball
----------------------------------------------

Downloading and running a pre-built buildtools installer is the easiest
of the two methods by which you can get these tools:

1. Locate and download the ``*.sh`` at &YOCTO_RELEASE_DL_URL;/buildtools/

2. Execute the installation script. Here is an example for the
   traditional installer:
   ::

      $ sh ~/Downloads/x86_64-buildtools-nativesdk-standalone-&DISTRO;.sh

   Here is an example for the extended installer:
   ::

      $ sh ~/Downloads/x86_64-buildtools-extended-nativesdk-standalone-&DISTRO;.sh

   During execution, a prompt appears that allows you to choose the
   installation directory. For example, you could choose the following:
   ``/home/your-username/buildtools``

3. Source the tools environment setup script by using a command like the
   following:
   ::

      $ source /home/your_username/buildtools/environment-setup-i586-poky-linux

   Of
   course, you need to supply your installation directory and be sure to
   use the right file (i.e. i585 or x86-64).

   After you have sourced the setup script, the tools are added to
   ``PATH`` and any other environment variables required to run the
   tools are initialized. The results are working versions versions of
   Git, tar, Python and ``chrpath``. And in the case of the
   ``buildtools-extended`` tarball, additional working versions of tools
   including ``gcc``, ``make`` and the other tools included in
   ``packagegroup-core-buildessential``.

Building Your Own ``buildtools`` Tarball
----------------------------------------

Building and running your own buildtools installer applies only when you
have a build host that can already run BitBake. In this case, you use
that machine to build the ``.sh`` file and then take steps to transfer
and run it on a machine that does not meet the minimal Git, tar, and
Python (or gcc) requirements.

Here are the steps to take to build and run your own buildtools
installer:

1. On the machine that is able to run BitBake, be sure you have set up
   your build environment with the setup script
   (:ref:`structure-core-script`).

2. Run the BitBake command to build the tarball:
   ::

      $ bitbake buildtools-tarball

   or run the BitBake command to build the extended tarball:
   ::

      $ bitbake buildtools-extended-tarball

   .. note::

      The :term:`SDKMACHINE` variable in your ``local.conf`` file determines
      whether you build tools for a 32-bit or 64-bit system.

   Once the build completes, you can find the ``.sh`` file that installs
   the tools in the ``tmp/deploy/sdk`` subdirectory of the
   :term:`Build Directory`. The installer file has the string
   "buildtools" (or "buildtools-extended") in the name.

3. Transfer the ``.sh`` file from the build host to the machine that
   does not meet the Git, tar, or Python (or gcc) requirements.

4. On the machine that does not meet the requirements, run the ``.sh``
   file to install the tools. Here is an example for the traditional
   installer:
   ::

      $ sh ~/Downloads/x86_64-buildtools-nativesdk-standalone-&DISTRO;.sh

   Here is an example for the extended installer:
   ::

      $ sh ~/Downloads/x86_64-buildtools-extended-nativesdk-standalone-&DISTRO;.sh

   During execution, a prompt appears that allows you to choose the
   installation directory. For example, you could choose the following:
   ``/home/your_username/buildtools``

5. Source the tools environment setup script by using a command like the
   following:
   ::

      $ source /home/your_username/buildtools/environment-setup-x86_64-poky-linux

   Of course, you need to supply your installation directory and be sure to
   use the right file (i.e. i586 or x86_64).

   After you have sourced the setup script, the tools are added to
   ``PATH`` and any other environment variables required to run the
   tools are initialized. The results are working versions versions of
   Git, tar, Python and ``chrpath``. And in the case of the
   ``buildtools-extended`` tarball, additional working versions of tools
   including ``gcc``, ``make`` and the other tools included in
   ``packagegroup-core-buildessential``.
