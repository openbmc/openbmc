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
      :ref:`dev-manual/dev-manual-start:setting up to use cross platforms (crops)`
      section
      in the Yocto Project Development Tasks Manual for more
      information.

   -  You may use Windows Subsystem For Linux v2 to set up a build host
      using Windows 10.

      .. note::

         The Yocto Project is not compatible with WSLv1, it is
         compatible but not officially supported nor validated with
         WSLv2, if you still decide to use WSL please upgrade to WSLv2.

      See the :ref:`dev-manual/dev-manual-start:setting up to use windows
      subsystem for linux (wslv2)` section in the Yocto Project Development
      Tasks Manual for more information.

If you want more conceptual or background information on the Yocto
Project, see the :doc:`../overview-manual/overview-manual`.

Compatible Linux Distribution
=============================

Make sure your :term:`Build Host` meets the
following requirements:

-  50 Gbytes of free disk space

-  Runs a supported Linux distribution (i.e. recent releases of Fedora,
   openSUSE, CentOS, Debian, or Ubuntu). For a list of Linux
   distributions that support the Yocto Project, see the
   :ref:`ref-manual/ref-system-requirements:supported linux distributions`
   section in the Yocto Project Reference Manual. For detailed
   information on preparing your build host, see the
   :ref:`dev-manual/dev-manual-start:preparing the build host`
   section in the Yocto Project Development Tasks Manual.

-

   -  Git 1.8.3.1 or greater
   -  tar 1.28 or greater
   -  Python 3.5.0 or greater.
   -  gcc 5.0 or greater.

If your build host does not meet any of these three listed version
requirements, you can take steps to prepare the system so that you
can still use the Yocto Project. See the
:ref:`ref-manual/ref-system-requirements:required git, tar, python and gcc versions`
section in the Yocto Project Reference Manual for information.

Build Host Packages
===================

You must install essential host packages on your build host. The
following command installs the host packages based on an Ubuntu
distribution:

.. code-block:: shell

  $ sudo apt-get install &UBUNTU_HOST_PACKAGES_ESSENTIAL;

.. note::

   For host package requirements on all supported Linux distributions,
   see the :ref:`ref-manual/ref-system-requirements:required packages for the build host`
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

Move to the ``poky`` directory and take a look at the tags:

.. code-block:: shell

   $ cd poky
   $ git fetch --tags
   $ git tag
   1.1_M1.final
   1.1_M1.rc1
   1.1_M1.rc2
   1.1_M2.final
   1.1_M2.rc1
   .
   .
   .
   yocto-2.5
   yocto-2.5.1
   yocto-2.5.2
   yocto-2.6
   yocto-2.6.1
   yocto-2.6.2
   yocto-2.7
   yocto_1.5_M5.rc8

For this example, check out the branch based on the
``&DISTRO_REL_TAG;`` release:

.. code-block:: shell

   $ git checkout tags/&DISTRO_REL_TAG; -b my-&DISTRO_REL_TAG;
   Switched to a new branch 'my-&DISTRO_REL_TAG;'

The previous Git checkout command creates a local branch named
``my-&DISTRO_REL_TAG;``. The files available to you in that branch exactly
match the repository's files in the ``&DISTRO_NAME_NO_CAP;`` development
branch at the time of the Yocto Project &DISTRO_REL_TAG; release.

For more options and information about accessing Yocto Project related
repositories, see the
:ref:`dev-manual/dev-manual-start:locating yocto project source files`
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
      ":yocto_wiki:`Working Behind a Network Proxy </wiki/Working_Behind_a_Network_Proxy>`"
      page of the Yocto Project Wiki.

#. **Initialize the Build Environment:** From within the ``poky``
   directory, run the :ref:`ref-manual/ref-structure:\`\`oe-init-build-env\`\``
   environment
   setup script to define Yocto Project's build environment on your
   build host.

   .. code-block:: shell

      $ cd ~/poky
      $ source &OE_INIT_FILE;
      You had no conf/local.conf file. This configuration file has therefore been
      created for you with some default values. You may wish to edit it to, for
      example, select a different MACHINE (target hardware). See conf/local.conf
      for more information as common configuration options are commented.

      You had no conf/bblayers.conf file. This configuration file has therefore
      been created for you with some default values. To add additional metadata
      layers into your configuration please add entries to conf/bblayers.conf.

      The Yocto Project has extensive documentation about OE including a reference
      manual which can be found at:
          http://yoctoproject.org/documentation

      For more information about OpenEmbedded see their website:
          http://www.openembedded.org/

      ### Shell environment set up for builds. ###

      You can now run 'bitbake <target>'

      Common targets are:
          core-image-minimal
          core-image-sato
          meta-toolchain
          meta-ide-support

      You can also run generated qemu images with a command like 'runqemu qemux86-64'

   Among other things, the script creates the :term:`Build Directory`, which is
   ``build`` in this case and is located in the :term:`Source Directory`.  After
   the script runs, your current working directory is set to the Build
   Directory. Later, when the build completes, the Build Directory contains all the
   files created during the build.

#. **Examine Your Local Configuration File:** When you set up the build
   environment, a local configuration file named ``local.conf`` becomes
   available in a ``conf`` subdirectory of the Build Directory. For this
   example, the defaults are set to build for a ``qemux86`` target,
   which is suitable for emulation. The package manager used is set to
   the RPM package manager.

   .. tip::

      You can significantly speed up your build and guard against fetcher
      failures by using mirrors. To use mirrors, add these lines to your
      local.conf file in the Build directory: ::

         SSTATE_MIRRORS = "\
         file://.* http://sstate.yoctoproject.org/dev/PATH;downloadfilename=PATH \n \
         file://.* http://sstate.yoctoproject.org/&YOCTO_DOC_VERSION_MINUS_ONE;/PATH;downloadfilename=PATH \n \
         file://.* http://sstate.yoctoproject.org/&YOCTO_DOC_VERSION;/PATH;downloadfilename=PATH \n \
         "


      The previous examples showed how to add sstate paths for Yocto Project
      &YOCTO_DOC_VERSION_MINUS_ONE;, &YOCTO_DOC_VERSION;, and a development
      area. For a complete index of sstate locations, see http://sstate.yoctoproject.org/.

#. **Start the Build:** Continue with the following command to build an OS
   image for the target, which is ``core-image-sato`` in this example:

   .. code-block:: shell

      $ bitbake core-image-sato

   For information on using the ``bitbake`` command, see the
   :ref:`usingpoky-components-bitbake` section in the Yocto Project Overview and
   Concepts Manual, or see the ":ref:`BitBake Command
   <bitbake:bitbake-user-manual-command>`" section in the BitBake User Manual.

#. **Simulate Your Image Using QEMU:** Once this particular image is
   built, you can start QEMU, which is a Quick EMUlator that ships with
   the Yocto Project:

   .. code-block:: shell

      $ runqemu qemux86-64

   If you want to learn more about running QEMU, see the
   :ref:`dev-manual/dev-manual-qemu:using the quick emulator (qemu)` chapter in
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

#. **Find a Layer:** Lots of hardware layers exist. The Yocto Project
   :yocto_git:`Source Repositories <>` has many hardware layers.
   This example adds the
   `meta-altera <https://github.com/kraj/meta-altera>`__ hardware layer.

#. **Clone the Layer:** Use Git to make a local copy of the layer on your
   machine. You can put the copy in the top level of the copy of the
   Poky repository created earlier:

   .. code-block:: shell

      $ cd ~/poky
      $ git clone https://github.com/kraj/meta-altera.git
      Cloning into 'meta-altera'...
      remote: Counting objects: 25170, done.
      remote: Compressing objects: 100% (350/350), done.
      remote: Total 25170 (delta 645), reused 719 (delta 538), pack-reused 24219
      Receiving objects: 100% (25170/25170), 41.02 MiB | 1.64 MiB/s, done.
      Resolving deltas: 100% (13385/13385), done.
      Checking connectivity... done.

   The hardware layer now exists
   with other layers inside the Poky reference repository on your build
   host as ``meta-altera`` and contains all the metadata needed to
   support hardware from Altera, which is owned by Intel.

   .. note::

      It is recommended for layers to have a branch per Yocto Project release.
      Please make sure to checkout the layer branch supporting the Yocto Project
      release you're using.

#. **Change the Configuration to Build for a Specific Machine:** The
   :term:`MACHINE` variable in the
   ``local.conf`` file specifies the machine for the build. For this
   example, set the ``MACHINE`` variable to ``cyclone5``. These
   configurations are used:
   https://github.com/kraj/meta-altera/blob/master/conf/machine/cyclone5.conf.

   .. note::

      See the "Examine Your Local Configuration File" step earlier for more
      information on configuring the build.

#. **Add Your Layer to the Layer Configuration File:** Before you can use
   a layer during a build, you must add it to your ``bblayers.conf``
   file, which is found in the
   :term:`Build Directory` ``conf``
   directory.

   Use the ``bitbake-layers add-layer`` command to add the layer to the
   configuration file:

   .. code-block:: shell

      $ cd ~/poky/build
      $ bitbake-layers add-layer ../meta-altera
      NOTE: Starting bitbake server...
      Parsing recipes: 100% |##################################################################| Time: 0:00:32
      Parsing of 918 .bb files complete (0 cached, 918 parsed). 1401 targets,
      123 skipped, 0 masked, 0 errors.

   You can find
   more information on adding layers in the
   :ref:`dev-manual/dev-manual-common-tasks:adding a layer using the \`\`bitbake-layers\`\` script`
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

   $ cd ~/poky
   $ bitbake-layers create-layer meta-mylayer
   NOTE: Starting bitbake server...
   Add your new layer with 'bitbake-layers add-layer meta-mylayer'

For more information
on layers and how to create them, see the
:ref:`dev-manual/dev-manual-common-tasks:creating a general layer using the \`\`bitbake-layers\`\` script`
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

-  **Developer Screencast:** The `Getting Started with the Yocto Project -
   New Developer Screencast Tutorial <http://vimeo.com/36450321>`__
   provides a 30-minute video created for users unfamiliar with the
   Yocto Project but familiar with Linux build hosts. While this
   screencast is somewhat dated, the introductory and fundamental
   concepts are useful for the beginner.

-  **Yocto Project Overview and Concepts Manual:** The
   :doc:`../overview-manual/overview-manual` is a great
   place to start to learn about the Yocto Project. This manual
   introduces you to the Yocto Project and its development environment.
   The manual also provides conceptual information for various aspects
   of the Yocto Project.

-  **Yocto Project Wiki:** The :yocto_wiki:`Yocto Project Wiki <>`
   provides additional information on where to go next when ramping up
   with the Yocto Project, release information, project planning, and QA
   information.

-  **Yocto Project Mailing Lists:** Related mailing lists provide a forum
   for discussion, patch submission and announcements. Several mailing
   lists exist and are grouped according to areas of concern. See the
   :ref:`ref-manual/resources:mailing lists`
   section in the Yocto Project Reference Manual for a complete list of
   Yocto Project mailing lists.

-  **Comprehensive List of Links and Other Documentation:** The
   :ref:`ref-manual/resources:links and related documentation`
   section in the Yocto Project Reference Manual provides a
   comprehensive list of all related links and other user documentation.

.. include:: /boilerplate.rst
