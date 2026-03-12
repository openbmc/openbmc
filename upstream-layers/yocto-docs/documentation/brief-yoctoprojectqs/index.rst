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
      system running a
      :ref:`supported version of Ubuntu Linux distribution<ref-manual/system-requirements:supported linux distributions>`.
      If the machine
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

-  At least &MIN_RAM; Gbytes of RAM, though a modern build host with as
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

-  Ensure that the following utilities have these minimum version numbers:

   -  Git &MIN_GIT_VERSION; or greater
   -  tar &MIN_TAR_VERSION; or greater
   -  Python &MIN_PYTHON_VERSION; or greater.
   -  gcc &MIN_GCC_VERSION; or greater.
   -  GNU make &MIN_MAKE_VERSION; or greater

If your build host does not satisfy all of the above version
requirements, you can take steps to prepare the system so that you
can still use the Yocto Project. See the
:ref:`ref-manual/system-requirements:required git, tar, python, make and gcc versions`
section in the Yocto Project Reference Manual for information.

Build Host Packages
===================

.. include:: ../ref-manual/ubuntu-debian-host-packages-nodocs.rst

.. note::

   For host package requirements on all supported Linux distributions,
   see the :ref:`ref-manual/system-requirements:required packages for the build host`
   section in the Yocto Project Reference Manual.

Use Git to Clone bitbake-setup
==============================

Once you complete the setup instructions for your machine, you need to
get a copy of the ``bitbake-setup`` tool to setup the :term:`Poky` reference
distribution on your build host. Use the following commands to clone
the bitbake repository.

.. code-block:: console

   $ git clone https://git.openembedded.org/bitbake

Setup a build environment with the following command:

.. code-block:: console

   $ ./bitbake/bin/bitbake-setup init

By default, this will setup a top directory in the current directory.

If you prefer to setup your builds in a different top directory, for example
``$HOME/bitbake-builds``, you can set it with the :ref:`bitbake:ref-bbsetup-command-settings` command:

.. code-block:: console

   $ ./bitbake/bin/bitbake-setup settings set --global default top-dir-prefix $HOME

.. note::

   Use :ref:`bitbake-setup settings list <bitbake:ref-bbsetup-command-settings>`
   to get an overview of the settings.

:ref:`bitbake:ref-bbsetup-command-init` is an interactive program by default and
will ask you to make some decisions. Depending on your answers, the output may
differ from the examples below.

#. Choose a configuration (for example, ``poky-master``):

   .. code-block:: text

      Available configurations:
      1. poky-master  Poky - The Yocto Project testing distribution configurations and hardware test platforms
      2. oe-nodistro-&DISTRO_NAME_NO_CAP;       OpenEmbedded - 'nodistro' basic configuration, release &DISTRO_RELEASE_SERIES; '&DISTRO_NAME_NO_CAP;'
      3. poky-&DISTRO_NAME_NO_CAP;      Poky - The Yocto Project testing distribution configurations and hardware test platforms, release &DISTRO_RELEASE_SERIES; '&DISTRO_NAME_NO_CAP;'
      4. oe-nodistro-master   OpenEmbedded - 'nodistro' basic configuration
      ...

      Please select one of the above configurations by its number:
      1

   Depending on the choice above, new options can be prompted to further specify
   which configuration to use. For example:

   .. code-block:: text

      Available bitbake configurations:
      1. poky Poky - The Yocto Project testing distribution
      2. poky-with-sstate     Poky - The Yocto Project testing distribution with internet sstate acceleration. Use with caution as it requires a completely robust local network with sufficient bandwidth.

      Please select one of the above bitbake configurations by its number:
      1

#. Choose a target :term:`MACHINE` (for example, ``qemux86-64``):

   .. code-block:: text

      Target machines:
      1. machine/qemux86-64
      2. machine/qemuarm64
      3. machine/qemuriscv64
      4. machine/genericarm64
      5. machine/genericx86-64

      Please select one of the above options by its number:
      1

#. Choose a :term:`DISTRO` (for example, ``poky``):

   .. code-block:: text

      Distribution configuration variants:
      1. distro/poky
      2. distro/poky-altcfg
      3. distro/poky-tiny

      Please select one of the above options by its number:
      1

#. Choose a :term:`bitbake:setup` directory name:

   .. code-block:: text

      Enter setup directory name: [poky-master-poky-distro_poky-machine_qemux86-64]

   Press Enter to leave it to the default value shown in the brackets, or type a
   custom directory name.

.. note::

   If you prefer to run non-interactively, you can run a command like the
   following:

   .. code-block:: console

      $ bitbake-setup init --non-interactive poky-master poky-with-sstate distro/poky machine/qemux86-64

The ``init`` command creates a new :term:`bitbake:Setup` in the
:term:`bitbake:top directory`. The default name is derived from the selected
configuration above.

For the selected options in the above example, this would be:

.. code-block:: text

   poky-master-poky-distro_poky-machine_qemux86-64

This will be our example configuration in the following sections.

This directory contains:

-  The :term:`bitbake:BitBake Build` directory, named ``build``. Later, when the
   build completes, this directory contains all the files created during the
   build.

   This directory also contains a ``README``, describing the current
   configuration and showing some instructions.

-  The :term:`layers <Layer>` needed to build the Poky reference distribution,
   in the ``layers`` directory.

-  A ``config`` directory, representing the current configuration used for this
   :term:`bitbake:setup`.

.. note::

   It is also possible to setup the :term:`Poky` reference distro manually. For
   that refer to the :doc:`/dev-manual/poky-manual-setup` section of the Yocto
   Project Development Tasks Manual.

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

#.  **Initialize the Build Environment:** Source the ``init-build-env``
    environment setup script within the :term:`bitbake:BitBake build` directory
    to setup the :term:`BitBake` build environment on your host:

    .. code-block:: console

       $ source poky-master-poky-distro_poky-machine_qemux86-64/build/init-build-env
       Poky reference distro build

#.  **Examine Your Current Configuration:** When you set up the build
    environment, an configuration file named :ref:`toolcfg.conf
    <structure-build-conf-toolcfg.conf>` becomes available in a ``conf/``
    sub-directory of the :term:`bitbake:BitBake build` directory. This file is
    automatically modified by the ``bitbake-config-build`` command-line tool.
    With this tool, list the currently enabled :term:`configuration fragments
    <Configuration Fragment>`:

    .. code-block:: console

       $ bitbake-config-build list-fragments

    For this configuration, the default is to use two :term:`Built-in Fragments
    <Built-in Fragment>`:

    -  ``distro/poky`` sets the :term:`DISTRO` to :term:`Poky`
       (:ref:`ref-fragments-builtin-core-distro` fragment).
    -  ``machine/qemux86-64`` sets the :term:`MACHINE` to ``qemux86-64``
       (:ref:`ref-fragments-builtin-core-machine` fragment).

    These fragment values correspond to the choices made when running
    :ref:`bitbake:ref-bbsetup-command-init`.

    .. note::

       These set up the environment similar to what was previously in the local
       configuration file :ref:`local.conf <structure-build-conf-local.conf>`,
       which is now largely empty. To setup the build how it was done
       previously, see the :doc:`/dev-manual/poky-manual-setup` section of the
       Yocto Project Development Tasks Manual.

    The current configuration does not allow the ``root`` user to login. As this
    can be useful for development, you can enable the
    :ref:`ref-fragments-root-login-with-empty-password` fragment:

    .. code-block:: console

       $ bitbake-config-build enable-fragment root-login-with-empty-password

    .. note::

       You can significantly speed up your build and guard against fetcher
       failures by using :ref:`overview-manual/concepts:shared state cache`
       mirrors and enabling :ref:`overview-manual/concepts:hash equivalence`.
       This way, you can use pre-built artifacts rather than building them.
       This is relevant only when your network and the server that you use
       can download these artifacts faster than you would be able to build them.

       To use such mirrors, enable the
       :ref:`ref-fragments-core-yocto-sstate-mirror-cdn` fragment::

          $ bitbake-config-build enable-fragment core/yocto/sstate-mirror-cdn

       The hash equivalence server needs the websockets python module version 9.1
       or later. Debian GNU/Linux 12 (Bookworm) and later, Fedora, CentOS Stream
       9 and later, and Ubuntu 22.04 (LTS) and later, all have a recent enough
       package. Other supported distributions need to get the module some other
       place than their package feed, e.g. via ``pip``. You can otherwise
       install a :term:`Buildtools` tarball by following the instructions in
       the :ref:`system-requirements-buildtools` section of the Yocto Project
       Reference Manual.

#. **Start the Build:** Continue with the following command to build an OS
   image for the target, which is ``core-image-sato`` in this example:

   .. code-block:: console

      $ bitbake core-image-sato

   For information on using the ``bitbake`` command, see the
   :ref:`overview-manual/concepts:bitbake` section in the Yocto Project Overview and
   Concepts Manual, or see
   :ref:`bitbake-user-manual/bitbake-user-manual-intro:the bitbake command`
   in the BitBake User Manual.

#. **Simulate Your Image Using QEMU:** Once this particular image is
   built, you can start QEMU, which is a Quick EMUlator that ships with
   the Yocto Project:

   .. code-block:: console

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

Follow these steps to add a :ref:`BSP layer <overview-manual/concepts:bsp
layer>`:

#.  **Find a Layer:** Many BSP layers are available. The
    :oe_layerindex:`layer index <>` can be used to find such layers. This example
    adds the :yocto_git:`meta-raspberrypi </meta-raspberrypi>` BSP
    layer.

    First, clone the layer next the other layers::

      git clone -b &DISTRO_NAME_NO_CAP; https://git.yoctoproject.org/meta-raspberrypi ../layers/meta-raspberrypi

#.  **Add Your Layer to the Layer Configuration File:** Before you can use
    it, you must add the layer and its dependencies to your ``bblayers.conf``
    file, which is found in the :term:`Build Directory` (``conf/``) directory.

    For this, the ``bitbake-layers add-layer`` can be used:

    .. code-block:: console

       $ bitbake-layers add-layer ../layers/meta-raspberrypi

    You can find more information on adding layers in the
    :ref:`dev-manual/layers:adding a layer using the \`\`bitbake-layers\`\`
    script` section.

#.  **Change the Configuration to Build for a Specific Machine:** The
    :term:`MACHINE` variable is defined by the :ref:`ref-fragments-builtin-core-machine`
    fragment. For this example, the meta-raspberrypi layer provides the
    :yocto_git:`raspberrypi5 </meta-yocto/tree/meta-yocto-bsp/conf/machine/beaglebone-yocto.conf>`
    machine, so let's make it the :term:`MACHINE` used for the build with
    ``bitbake-config-build``:

    .. code-block:: console

       $ bitbake-config-build enable-fragment machine/raspberrypi5

    .. note::

       See the "Examine Your Current Configuration" step earlier for more
       information on configuring the build.

    The ``raspberrypi5`` build depends on non-free firmware
    (https://github.com/RPi-Distro/firmware-nonfree) that includes the
    `Synaptics` license. See the :yocto_git:`ipcompliance.md
    </meta-raspberrypi/tree/docs/ipcompliance.md>` document for more information.
    Add the ``synaptics-killswitch`` value to the :term:`LICENSE_FLAGS_ACCEPTED`
    variable, in the ``conf/local.conf`` file of your build directory::

       LICENSE_FLAGS_ACCEPTED = "synaptics-killswitch"

#. **Start The Build:** The configuration is now set to build for the Raspberry
   Pi 5. Start the build again:

   .. code-block:: console

      $ bitbake core-image-sato

Completing these steps has added the ``meta-raspberrypi`` layer to your Yocto
Project development environment and configured it to build for the
``raspberrypi5`` machine.

.. note::

   The previous steps are for demonstration purposes only. If you were
   to attempt to build an image for the ``raspberrypi5`` machine, you
   should read the ``README.md`` file in ``meta-raspberrypi``.

Creating Your Own General Layer
===============================

Maybe you have an application or specific set of behaviors you need to
isolate. You can create your own general layer using the
``bitbake-layers create-layer`` command. The tool automates layer
creation by setting up a subdirectory with a ``layer.conf``
configuration file, a ``recipes-example`` subdirectory that contains an
``example.bb`` recipe, a licensing file, and a ``README``.

The following commands run the tool to create a layer named
``meta-mylayer``:

.. code-block:: console

   $ bitbake-layers create-layer ../layers/meta-mylayer
   NOTE: Starting bitbake server...
   Add your new layer with 'bitbake-layers add-layer ../layers/meta-mylayer'

.. note::

   By convention, layers are placed side-by-side.

For more information
on layers and how to create them, see the
:ref:`dev-manual/layers:Creating Your Own Layer`
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
