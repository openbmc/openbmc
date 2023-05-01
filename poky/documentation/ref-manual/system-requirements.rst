.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*******************
System Requirements
*******************

Welcome to the Yocto Project Reference Manual. This manual provides
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

Minimum Free Disk Space
=======================

To build an image such as ``core-image-sato`` for the ``qemux86-64`` machine,
you need a system with at least &MIN_DISK_SPACE; Gbytes of free disk space.
However, much more disk space will be necessary to build more complex images,
to run multiple builds and to cache build artifacts, improving build efficiency.

If you have a shortage of disk space, see the ":doc:`/dev-manual/disk-space`"
section of the Development Tasks Manual.

Minimum System RAM
==================

You will manage to build an image such as ``core-image-sato`` for the
``qemux86-64`` machine with as little as &MIN_RAM; Gbytes of RAM on an old
system with 4 CPU cores, but your builds will be much faster on a system with
as much RAM and as many CPU cores as possible.

.. _system-requirements-supported-distros:

Supported Linux Distributions
=============================

Currently, the Yocto Project is supported on the following distributions:

-  Ubuntu 18.04 (LTS)

-  Ubuntu 20.04 (LTS)

-  Ubuntu 22.04 (LTS)

-  Fedora 36

-  Fedora 37

-  AlmaLinux 8.7

-  AlmaLinux 9.1

-  Debian GNU/Linux 11.x (Bullseye)

-  OpenSUSE Leap 15.3

-  OpenSUSE Leap 15.4

.. note::

   -  While the Yocto Project Team attempts to ensure all Yocto Project
      releases are one hundred percent compatible with each officially
      supported Linux distribution, you may still encounter problems
      that happen only with a specific distribution.

   -  Yocto Project releases are tested against the stable Linux
      distributions in the above list. The Yocto Project should work
      on other distributions but validation is not performed against
      them.

   -  In particular, the Yocto Project does not support and currently
      has no plans to support rolling-releases or development
      distributions due to their constantly changing nature. We welcome
      patches and bug reports, but keep in mind that our priority is on
      the supported platforms listed above.

   -  If your Linux distribution is not in the above list, we recommend to
      get the :term:`buildtools` or :term:`buildtools-extended` tarballs
      containing the host tools required by your Yocto Project release,
      typically by running ``scripts/install-buildtools`` as explained in
      the ":ref:`system-requirements-buildtools`" section.

   -  You may use Windows Subsystem For Linux v2 to set up a build host
      using Windows 10 or later, or Windows Server 2019 or later, but validation
      is not performed against build hosts using WSL 2.

      See the
      :ref:`dev-manual/start:setting up to use windows subsystem for linux (wsl 2)`
      section in the Yocto Project Development Tasks Manual for more information.

   -  If you encounter problems, please go to :yocto_bugs:`Yocto Project
      Bugzilla <>` and submit a bug. We are
      interested in hearing about your experience. For information on
      how to submit a bug, see the Yocto Project
      :yocto_wiki:`Bugzilla wiki page </Bugzilla_Configuration_and_Bug_Tracking>`
      and the ":ref:`dev-manual/changes:submitting a defect against the yocto project`"
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

Here are the packages needed to build an image on a headless system
with a supported Ubuntu or Debian Linux distribution::

   $ sudo apt install &UBUNTU_HOST_PACKAGES_ESSENTIAL;

.. note::

   -  If your build system has the ``oss4-dev`` package installed, you
      might experience QEMU build failures due to the package installing
      its own custom ``/usr/include/linux/soundcard.h`` on the Debian
      system. If you run into this situation, try either of these solutions::

         $ sudo apt build-dep qemu
         $ sudo apt remove oss4-dev

Here are the packages needed to build Project documentation manuals::

   $ sudo apt install make python3-pip inkscape texlive-latex-extra
   &PIP3_HOST_PACKAGES_DOC;

Fedora Packages
---------------

Here are the packages needed to build an image on a headless system
with a supported Fedora Linux distribution::

   $ sudo dnf install &FEDORA_HOST_PACKAGES_ESSENTIAL;

Here are the packages needed to build Project documentation manuals::

   $ sudo dnf install make python3-pip which inkscape texlive-fncychap
   &PIP3_HOST_PACKAGES_DOC;

openSUSE Packages
-----------------

Here are the packages needed to build an image on a headless system
with a supported openSUSE distribution::

   $ sudo zypper install &OPENSUSE_HOST_PACKAGES_ESSENTIAL;

Here are the packages needed to build Project documentation manuals::

   $ sudo zypper install make python3-pip which inkscape texlive-fncychap
   &PIP3_HOST_PACKAGES_DOC;


AlmaLinux Packages
------------------

Here are the packages needed to build an image on a headless system
with a supported AlmaLinux distribution::

   $ sudo dnf install &ALMALINUX8_HOST_PACKAGES_ESSENTIAL;

.. note::

   -  Extra Packages for Enterprise Linux (i.e. ``epel-release``) is
      a collection of packages from Fedora built on RHEL/CentOS for
      easy installation of packages not included in enterprise Linux
      by default. You need to install these packages separately.

   -  The ``PowerTools/CRB`` repo provides additional packages such as
      ``rpcgen`` and ``texinfo``.

   -  The ``makecache`` command consumes additional Metadata from
      ``epel-release``.

Here are the packages needed to build Project documentation manuals::

   $ sudo dnf install make python3-pip which inkscape texlive-fncychap
   &PIP3_HOST_PACKAGES_DOC;

.. _system-requirements-buildtools:

Required Git, tar, Python, make and gcc Versions
================================================

In order to use the build system, your host development system must meet
the following version requirements for Git, tar, and Python:

-  Git &MIN_GIT_VERSION; or greater

-  tar &MIN_TAR_VERSION; or greater

-  Python &MIN_PYTHON_VERSION; or greater

-  GNU make &MIN_MAKE_VERSION; or greater

If your host development system does not meet all these requirements,
you can resolve this by installing a :term:`buildtools` tarball that
contains these tools. You can either download a pre-built tarball or
use BitBake to build one.

In addition, your host development system must meet the following
version requirement for gcc:

-  gcc &MIN_GCC_VERSION; or greater

If your host development system does not meet this requirement, you can
resolve this by installing a :term:`buildtools-extended` tarball that
contains additional tools, the equivalent of the Debian/Ubuntu ``build-essential``
package.

For systems with a broken make version (e.g. make 4.2.1 without patches) but
where the rest of the host tools are usable, you can use the :term:`buildtools-make`
tarball instead.

In the sections that follow, three different methods will be described for
installing the :term:`buildtools`, :term:`buildtools-extended` or :term:`buildtools-make`
toolset.

Installing a Pre-Built ``buildtools`` Tarball with ``install-buildtools`` script
--------------------------------------------------------------------------------

The ``install-buildtools`` script is the easiest of the three methods by
which you can get these tools. It downloads a pre-built :term:`buildtools`
installer and automatically installs the tools for you:

#. Execute the ``install-buildtools`` script. Here is an example::

      $ cd poky
      $ scripts/install-buildtools \
        --without-extended-buildtools \
        --base-url &YOCTO_DL_URL;/releases/yocto \
        --release yocto-&DISTRO; \
        --installer-version &DISTRO;

   During execution, the :term:`buildtools` tarball will be downloaded, the
   checksum of the download will be verified, the installer will be run
   for you, and some basic checks will be run to make sure the
   installation is functional.

   To avoid the need of ``sudo`` privileges, the ``install-buildtools``
   script will by default tell the installer to install in::

      /path/to/poky/buildtools

   If your host development system needs the additional tools provided
   in the :term:`buildtools-extended` tarball, you can instead execute the
   ``install-buildtools`` script with the default parameters::

      $ cd poky
      $ scripts/install-buildtools

   Alternatively if your host development system has a broken ``make``
   version such that you only need a known good version of ``make``,
   you can use the ``--make-only`` option::

      $ cd poky
      $ scripts/install-buildtools --make-only

#. Source the tools environment setup script by using a command like the
   following::

      $ source /path/to/poky/buildtools/environment-setup-x86_64-pokysdk-linux

   After you have sourced the setup script, the tools are added to
   ``PATH`` and any other environment variables required to run the
   tools are initialized. The results are working versions versions of
   Git, tar, Python and ``chrpath``. And in the case of the
   :term:`buildtools-extended` tarball, additional working versions of tools
   including ``gcc``, ``make`` and the other tools included in
   ``packagegroup-core-buildessential``.

Downloading a Pre-Built ``buildtools`` Tarball
----------------------------------------------

If you would prefer not to use the ``install-buildtools`` script, you can instead
download and run a pre-built :term:`buildtools` installer yourself with the following
steps:

#. Go to :yocto_dl:`/releases/yocto/yocto-&DISTRO;/buildtools/`, locate and
   download the ``.sh`` file corresponding to your host architecture
   and to :term:`buildtools`, :term:`buildtools-extended` or :term:`buildtools-make`.

#. Execute the installation script. Here is an example for the
   traditional installer::

      $ sh ~/Downloads/x86_64-buildtools-nativesdk-standalone-&DISTRO;.sh

   Here is an example for the extended installer::

      $ sh ~/Downloads/x86_64-buildtools-extended-nativesdk-standalone-&DISTRO;.sh

   An example for the make-only installer::

      $ sh ~/Downloads/x86_64-buildtools-make-nativesdk-standalone-&DISTRO;.sh

   During execution, a prompt appears that allows you to choose the
   installation directory. For example, you could choose the following:
   ``/home/your-username/buildtools``

#. As instructed by the installer script, you will have to source the tools
   environment setup script::

      $ source /home/your_username/buildtools/environment-setup-x86_64-pokysdk-linux

   After you have sourced the setup script, the tools are added to
   ``PATH`` and any other environment variables required to run the
   tools are initialized. The results are working versions versions of
   Git, tar, Python and ``chrpath``. And in the case of the
   :term:`buildtools-extended` tarball, additional working versions of tools
   including ``gcc``, ``make`` and the other tools included in
   ``packagegroup-core-buildessential``.

Building Your Own ``buildtools`` Tarball
----------------------------------------

Building and running your own :term:`buildtools` installer applies only when you
have a build host that can already run BitBake. In this case, you use
that machine to build the ``.sh`` file and then take steps to transfer
and run it on a machine that does not meet the minimal Git, tar, and
Python (or gcc) requirements.

Here are the steps to take to build and run your own :term:`buildtools`
installer:

#. On the machine that is able to run BitBake, be sure you have set up
   your build environment with the setup script
   (:ref:`structure-core-script`).

#. Run the BitBake command to build the tarball::

      $ bitbake buildtools-tarball

   or to build the extended tarball::

      $ bitbake buildtools-extended-tarball

   or to build the make-only tarball::

      $ bitbake buildtools-make-tarball

   .. note::

      The :term:`SDKMACHINE` variable in your ``local.conf`` file determines
      whether you build tools for a 32-bit or 64-bit system.

   Once the build completes, you can find the ``.sh`` file that installs
   the tools in the ``tmp/deploy/sdk`` subdirectory of the
   :term:`Build Directory`. The installer file has the string
   "buildtools" or "buildtools-extended" in the name.

#. Transfer the ``.sh`` file from the build host to the machine that
   does not meet the Git, tar, or Python (or gcc) requirements.

#. On this machine, run the ``.sh`` file to install the tools. Here is an
   example for the traditional installer::

      $ sh ~/Downloads/x86_64-buildtools-nativesdk-standalone-&DISTRO;.sh

   For the extended installer::

      $ sh ~/Downloads/x86_64-buildtools-extended-nativesdk-standalone-&DISTRO;.sh

   And for the make-only installer::

      $ sh ~/Downloads/x86_64-buildtools-make-nativesdk-standalone-&DISTRO;.sh

   During execution, a prompt appears that allows you to choose the
   installation directory. For example, you could choose the following:
   ``/home/your_username/buildtools``

#. Source the tools environment setup script by using a command like the
   following::

      $ source /home/your_username/buildtools/environment-setup-x86_64-poky-linux

   After you have sourced the setup script, the tools are added to
   ``PATH`` and any other environment variables required to run the
   tools are initialized. The results are working versions versions of
   Git, tar, Python and ``chrpath``. And in the case of the
   :term:`buildtools-extended` tarball, additional working versions of tools
   including ``gcc``, ``make`` and the other tools included in
   ``packagegroup-core-buildessential``.
