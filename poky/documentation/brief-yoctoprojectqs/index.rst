.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

=========================
Yocto Project Quick Build
=========================

Welcome!
========

This short document steps you through the process for a typical
image build using the Yocto Project. The document also introduces how to
configure a build for specific hardware. You will use Yocto Project to
build a reference embedded OS called Poky.

.. note::

   -  The examples in this paper assume you are using a native Linux
      system running a recent Ubuntu Linux distribution. If the machine
      you want to use Yocto Project on to build an image
      (:term:`Build Host`) is not
      a native Linux system, you can still perform these steps by using
      CROss PlatformS (CROPS) and setting up a Poky container. See the
      :ref:`dev-manual/start:setting up to use cross platforms (crops)`
      section
      in the Yocto Project Development Tasks Manual for more
      information.

   -  You may use version 2 of Windows Subsystem For Linux (WSL 2) to set
      up a build host using Windows 10 or later, Windows Server 2019 or later.
      See the :ref:`dev-manual/start:setting up to use windows subsystem for
      linux (wsl 2)` section in the Yocto Project Development Tasks Manual
      for more information.

If you want more conceptual or background information on the Yocto
Project, see the :doc:`/overview-manual/index`.

Compatible Linux Distribution
=============================

Make sure your :term:`Build Host` meets the
following requirements:

-  At least &MIN_DISK_SPACE; Gbytes of free disk space, though
   much more will help to run multiple builds and increase
   performance by reusing build artifacts.

-  At least &MIN_RAM; Gbytes of RAM, though a modern modern build host with as
   much RAM and as many CPU cores as possible is strongly recommended to
   maximize build performance.

-  Runs a supported Linux distribution (i.e. recent releases of Fedora,
   openSUSE, CentOS, Debian, or Ubuntu). For a list of Linux
   distributions that support the Yocto Project, see the
   :ref:`ref-manual/system-requirements:supported linux distributions`
   section in the Yocto Project Reference Manual. For detailed
   information on preparing your build host, see the
   :ref:`dev-manual/start:preparing the build host`
   section in the Yocto Project Development Tasks Manual.

-

   -  Git &MIN_GIT_VERSION; or greater
   -  tar &MIN_TAR_VERSION; or greater
   -  Python &MIN_PYTHON_VERSION; or greater.
   -  gcc &MIN_GCC_VERSION; or greater.
   -  GNU make &MIN_MAKE_VERSION; or greater

If your build host does not meet any of these three listed version
requirements, you can take steps to prepare the system so that you
can still use the Yocto Project. See the
:ref:`ref-manual/system-requirements:required git, tar, python, make and gcc versions`
section in the Yocto Project Reference Manual for information.

Build Host Packages
===================

You must install essential host packages on your build host. The
following command installs the host packages based on an Ubuntu
distribution::

   $ sudo apt install &UBUNTU_HOST_PACKAGES_ESSENTIAL;

.. note::

   For host package requirements on all supported Linux distributions,
   see the :ref:`ref-manual/system-requirements:required packages for the build host`
   section in the Yocto Project Reference Manual.

Use Git to Clone Poky
=====================

Once you complete the setup instructions for your machine, you need to
get a copy of the Poky repository on your build host. Use the following
commands to clone the Poky repository.

.. code-block:: shell

   $ git clone git://git.yoctoproject.org/poky
   Cloning into 'poky'...
   remote: Counting
   objects: 432160, done. remote: Compressing objects: 100%
   (102056/102056), done. remote: Total 432160 (delta 323116), reused
   432037 (delta 323000) Receiving objects: 100% (432160/432160), 153.81 MiB | 8.54 MiB/s, done.
   Resolving deltas: 100% (323116/323116), done.
   Checking connectivity... done.

Go to :yocto_wiki:`Releases wiki page </Releases>`, and choose a release
codename (such as ``&DISTRO_NAME_NO_CAP;``), corresponding to either the
latest stable release or a Long Term Support release.

Then move to the ``poky`` directory and take a look at existing branches:

.. code-block:: shell

   $ cd poky
   $ git branch -a
   .
   .
   .
   remotes/origin/HEAD -> origin/master
   remotes/origin/dunfell
   remotes/origin/dunfell-next
   .
   .
   .
   remotes/origin/gatesgarth
   remotes/origin/gatesgarth-next
   .
   .
   .
   remotes/origin/master
   remotes/origin/master-next
   .
   .
   .


For this example, check out the ``&DISTRO_NAME_NO_CAP;`` branch based on the
``&DISTRO_NAME;`` release:

.. code-block:: shell

   $ git checkout -t origin/&DISTRO_NAME_NO_CAP; -b my-&DISTRO_NAME_NO_CAP;
   Branch 'my-&DISTRO_NAME_NO_CAP;' set up to track remote branch '&DISTRO_NAME_NO_CAP;' from 'origin'.
   Switched to a new branch 'my-&DISTRO_NAME_NO_CAP;'

The previous Git checkout command creates a local branch named
``my-&DISTRO_NAME_NO_CAP;``. The files available to you in that branch
exactly match the repository's files in the ``&DISTRO_NAME_NO_CAP;``
release branch.

Note that you can regularly type the following command in the same directory
to keep your local files in sync with the release branch:

.. code-block:: shell

   $ git pull

For more options and information about accessing Yocto Project related
repositories, see the
:ref:`dev-manual/start:locating yocto project source files`
section in the Yocto Project Development Tasks Manual.

Building Your Image
===================

Use the following steps to build your image. The build process creates
an entire Linux distribution, including the toolchain, from source.

.. note::

   -  If you are working behind a firewall and your build host is not
      set up for proxies, you could encounter problems with the build
      process when fetching source code (e.g. fetcher failures or Git
      failures).

   -  If you do not know your proxy settings, consult your local network
      infrastructure resources and get that information. A good starting
      point could also be to check your web browser settings. Finally,
      you can find more information on the
      ":yocto_wiki:`Working Behind a Network Proxy </Working_Behind_a_Network_Proxy>`"
      page of the Yocto Project Wiki.

#. **Initialize the Build Environment:** From within the ``poky``
   directory, run the :ref:`ref-manual/structure:\`\`oe-init-build-env\`\``
   environment
   setup script to define Yocto Project's build environment on your
   build host.

   .. code-block:: shell

      $ cd poky
      $ source oe-init-build-env
      You had no conf/local.conf file. This configuration file has therefore been
      created for you with some default values. You may wish to edit it to, for
      example, select a different MACHINE (target hardware). See conf/local.conf
      for more information as common configuration options are commented.

      You had no conf/bblayers.conf file. This configuration file has therefore
      been created for you with some default values. To add additional metadata
      layers into your configuration please add entries to conf/bblayers.conf.

      The Yocto Project has extensive documentation about OE including a reference
      manual which can be found at:
          https://docs.yoctoproject.org

      For more information about OpenEmbedded see their website:
          https://www.openembedded.org/

      ### Shell environment set up for builds. ###

      You can now run 'bitbake <target>'

      Common targets are:
          core-image-minimal
          core-image-full-cmdline
          core-image-sato
          core-image-weston
          meta-toolchain
          meta-ide-support

      You can also run generated QEMU images with a command like 'runqemu qemux86-64'

      Other commonly useful commands are:
       - 'devtool' and 'recipetool' handle common recipe tasks
       - 'bitbake-layers' handles common layer tasks
       - 'oe-pkgdata-util' handles common target package tasks

   Among other things, the script creates the :term:`Build Directory`, which is
   ``build`` in this case and is located in the :term:`Source Directory`.  After
   the script runs, your current working directory is set to the
   :term:`Build Directory`. Later, when the build completes, the
   :term:`Build Directory` contains all the files created during the build.

#. **Examine Your Local Configuration File:** When you set up the build
   environment, a local configuration file named ``local.conf`` becomes
   available in a ``conf`` subdirectory of the :term:`Build Directory`. For this
   example, the defaults are set to build for a ``qemux86`` target,
   which is suitable for emulation. The package manager used is set to
   the RPM package manager.

   .. tip::

      You can significantly speed up your build and guard against fetcher
      failures by using :ref:`overview-manual/concepts:shared state cache`
      mirrors and enabling :ref:`overview-manual/concepts:hash equivalence`.
      This way, you can use pre-built artifacts rather than building them.
      This is relevant only when your network and the server that you use
      can download these artifacts faster than you would be able to build them.

      To use such mirrors, uncomment the below lines in your ``conf/local.conf``
      file in the :term:`Build Directory`::

         BB_HASHSERVE_UPSTREAM = "wss://hashserv.yoctoproject.org/ws"
         SSTATE_MIRRORS ?= "file://.* http://cdn.jsdelivr.net/yocto/sstate/all/PATH;downloadfilename=PATH"
         BB_HASHSERVE = "auto"
         BB_SIGNATURE_HANDLER = "OEEquivHash"

      The hash equivalence server needs the websockets python module version 9.1
      or later. Debian GNU/Linux 12 (Bookworm) and later, Fedora, CentOS Stream
      9 and later, and Ubuntu 22.04 (LTS) and later, all have a recent enough
      package. Other supported distributions need to get the module some other
      place than their package feed, e.g. via ``pip``.

#. **Start the Build:** Continue with the following command to build an OS
   image for the target, which is ``core-image-sato`` in this example:

   .. code-block:: shell

      $ bitbake core-image-sato

   For information on using the ``bitbake`` command, see the
   :ref:`overview-manual/concepts:bitbake` section in the Yocto Project Overview and
   Concepts Manual, or see
   :ref:`bitbake-user-manual/bitbake-user-manual-intro:the bitbake command`
   in the BitBake User Manual.

#. **Simulate Your Image Using QEMU:** Once this particular image is
   built, you can start QEMU, which is a Quick EMUlator that ships with
   the Yocto Project:

   .. code-block:: shell

      $ runqemu qemux86-64

   If you want to learn more about running QEMU, see the
   :ref:`dev-manual/qemu:using the quick emulator (qemu)` chapter in
   the Yocto Project Development Tasks Manual.

#. **Exit QEMU:** Exit QEMU by either clicking on the shutdown icon or by typing
   ``Ctrl-C`` in the QEMU transcript window from which you evoked QEMU.

Customizing Your Build for Specific Hardware
============================================

So far, all you have done is quickly built an image suitable for
emulation only. This section shows you how to customize your build for
specific hardware by adding a hardware layer into the Yocto Project
development environment.

In general, layers are repositories that contain related sets of
instructions and configurations that tell the Yocto Project what to do.
Isolating related metadata into functionally specific layers facilitates
modular development and makes it easier to reuse the layer metadata.

.. note::

   By convention, layer names start with the string "meta-".

Follow these steps to add a hardware layer:

#. **Find a Layer:** Many hardware layers are available. The Yocto Project
   :yocto_git:`Source Repositories <>` has many hardware layers.
   This example adds the
   `meta-altera <https://github.com/kraj/meta-altera>`__ hardware layer.

#. **Clone the Layer:** Use Git to make a local copy of the layer on your
   machine. You can put the copy in the top level of the copy of the
   Poky repository created earlier:

   .. code-block:: shell

      $ cd poky
      $ git clone https://github.com/kraj/meta-altera.git
      Cloning into 'meta-altera'...
      remote: Counting objects: 25170, done.
      remote: Compressing objects: 100% (350/350), done.
      remote: Total 25170 (delta 645), reused 719 (delta 538), pack-reused 24219
      Receiving objects: 100% (25170/25170), 41.02 MiB | 1.64 MiB/s, done.
      Resolving deltas: 100% (13385/13385), done.
      Checking connectivity... done.

   The hardware layer is now available
   next to other layers inside the Poky reference repository on your build
   host as ``meta-altera`` and contains all the metadata needed to
   support hardware from Altera, which is owned by Intel.

   .. note::

      It is recommended for layers to have a branch per Yocto Project release.
      Please make sure to checkout the layer branch supporting the Yocto Project
      release you're using.

#. **Change the Configuration to Build for a Specific Machine:** The
   :term:`MACHINE` variable in the
   ``local.conf`` file specifies the machine for the build. For this
   example, set the :term:`MACHINE` variable to ``cyclone5``. These
   configurations are used:
   https://github.com/kraj/meta-altera/blob/master/conf/machine/cyclone5.conf.

   .. note::

      See the "Examine Your Local Configuration File" step earlier for more
      information on configuring the build.

#. **Add Your Layer to the Layer Configuration File:** Before you can use
   a layer during a build, you must add it to your ``bblayers.conf``
   file, which is found in the :term:`Build Directory` ``conf`` directory.

   Use the ``bitbake-layers add-layer`` command to add the layer to the
   configuration file:

   .. code-block:: shell

      $ cd poky/build
      $ bitbake-layers add-layer ../meta-altera
      NOTE: Starting bitbake server...
      Parsing recipes: 100% |##################################################################| Time: 0:00:32
      Parsing of 918 .bb files complete (0 cached, 918 parsed). 1401 targets,
      123 skipped, 0 masked, 0 errors.

   You can find
   more information on adding layers in the
   :ref:`dev-manual/layers:adding a layer using the \`\`bitbake-layers\`\` script`
   section.

Completing these steps has added the ``meta-altera`` layer to your Yocto
Project development environment and configured it to build for the
``cyclone5`` machine.

.. note::

   The previous steps are for demonstration purposes only. If you were
   to attempt to build an image for the ``cyclone5`` machine, you should
   read the Altera ``README``.

Creating Your Own General Layer
===============================

Maybe you have an application or specific set of behaviors you need to
isolate. You can create your own general layer using the
``bitbake-layers create-layer`` command. The tool automates layer
creation by setting up a subdirectory with a ``layer.conf``
configuration file, a ``recipes-example`` subdirectory that contains an
``example.bb`` recipe, a licensing file, and a ``README``.

The following commands run the tool to create a layer named
``meta-mylayer`` in the ``poky`` directory:

.. code-block:: shell

   $ cd poky
   $ bitbake-layers create-layer meta-mylayer
   NOTE: Starting bitbake server...
   Add your new layer with 'bitbake-layers add-layer meta-mylayer'

For more information
on layers and how to create them, see the
:ref:`dev-manual/layers:creating a general layer using the \`\`bitbake-layers\`\` script`
section in the Yocto Project Development Tasks Manual.

Where To Go Next
================

Now that you have experienced using the Yocto Project, you might be
asking yourself "What now?". The Yocto Project has many sources of
information including the website, wiki pages, and user manuals:

-  **Website:** The :yocto_home:`Yocto Project Website <>` provides
   background information, the latest builds, breaking news, full
   development documentation, and access to a rich Yocto Project
   Development Community into which you can tap.

-  **Video Seminar:** The `Introduction to the Yocto Project and BitBake, Part 1
   <https://youtu.be/yuE7my3KOpo>`__ and
   `Introduction to the Yocto Project and BitBake, Part 2
   <https://youtu.be/iZ05TTyzGHk>`__ videos offer a video seminar
   introducing you to the most important aspects of developing a
   custom embedded Linux distribution with the Yocto Project.

-  **Yocto Project Overview and Concepts Manual:** The
   :doc:`/overview-manual/index` is a great
   place to start to learn about the Yocto Project. This manual
   introduces you to the Yocto Project and its development environment.
   The manual also provides conceptual information for various aspects
   of the Yocto Project.

-  **Yocto Project Wiki:** The :yocto_wiki:`Yocto Project Wiki <>`
   provides additional information on where to go next when ramping up
   with the Yocto Project, release information, project planning, and QA
   information.

-  **Yocto Project Mailing Lists:** Related mailing lists provide a forum
   for discussion, patch submission and announcements. There are several
   mailing lists grouped by topic. See the
   :ref:`ref-manual/resources:mailing lists`
   section in the Yocto Project Reference Manual for a complete list of
   Yocto Project mailing lists.

-  **Comprehensive List of Links and Other Documentation:** The
   :ref:`ref-manual/resources:links and related documentation`
   section in the Yocto Project Reference Manual provides a
   comprehensive list of all related links and other user documentation.

.. include:: /boilerplate.rst
