.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Setting Up the Poky Reference Distro Manually
*********************************************

While the default way to setup the :term:`Poky` reference distro is to use
``bitbake-setup``, it is also possible to manually setup the environment. This
document guides through this setup step-by-step.

.. note::

   This document will produce a setup similar to what is described in
   :doc:`/brief-yoctoprojectqs/index`, which shows how to setup :term:`Poky`
   with ``bitbake-setup``.

Obtaining The Source Repositories
=================================

You can obtain the source repositories required to build the Poky reference
distro in two ways described below: :ref:`cloning the repositories with Git
<dev-manual/poky-manual-setup:Use Git to Clone The Layers>`, or
:ref:`downloading the released archives <dev-manual/poky-manual-setup:Using the
Source Archives>`.

Use Git to Clone The Layers
---------------------------

Go to the :yocto_home:`Releases </development/releases/>` page, and choose a release
(such as ``&DISTRO_REL_LATEST_TAG;``), corresponding to either the latest stable
release or a Long Term Support release.

Once you complete the setup instructions for your machine (see the
:doc:`/ref-manual/system-requirements` section of the Yocto Project Reference
Manual), create a :term:`Source Directory`: the base directory for your project.
Throughout the documentation, we will use ``bitbake-builds`` as the name of the
:term:`Source Directory`. Here's how to create it:

.. code-block:: console

   $ mkdir bitbake-builds
   $ cd bitbake-builds

You need to get a copy of the different :term:`layers <Layer>` needed
to setup the :term:`Poky` reference distribution on your build host. Use the
following commands:

.. code-block:: shell

   $ mkdir layers/
   $ git clone -b &DISTRO_REL_LATEST_TAG; https://git.openembedded.org/bitbake ./layers/bitbake
   $ git clone -b &DISTRO_REL_LATEST_TAG; https://git.openembedded.org/openembedded-core ./layers/openembedded-core
   $ git clone -b &DISTRO_REL_LATEST_TAG; https://git.yoctoproject.org/meta-yocto ./layers/meta-yocto


Using the Source Archives
-------------------------

The Yocto Project also provides source archives of its releases, which
are available at :yocto_dl:`/releases/yocto/`. Then, choose the subdirectory
containing the release you wish to use, for example
:yocto_dl:`&DISTRO_REL_LATEST_TAG; </releases/yocto/&DISTRO_REL_LATEST_TAG;/>`.

You will find there source archives of :term:`OpenEmbedded-Core (OE-Core)`,
:term:`BitBake`, and :yocto_git:`meta-yocto </meta-yocto>`.

.. note::

   The recommended method for accessing Yocto Project components is to :ref:`use
   Git to clone the upstream repository <dev-manual/poky-manual-setup:Use Git to
   Clone The Layers>` and work from within the locally cloned repositories.

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

#.  **Initialize the Build Environment:** From your current working directory,
    setup a build environment with the following command:

    .. code-block:: shell

       $ TEMPLATECONF=$PWD/layers/meta-yocto/meta-poky/conf/templates/default source ./layers/openembedded-core/oe-init-build-env

    Among other things, the script creates the :term:`Build Directory`, which is
    ``build`` in this case and is located in the :term:`Source Directory`.
    After the script runs, your current working directory is set to the
    :term:`Build Directory`. Later, when the build completes, the :term:`Build
    Directory` contains all the files created during the build.

#.  **Examine Your Local Configuration File:** When you set up the build
    environment, a local configuration file named ``local.conf`` becomes
    available in a ``conf`` sub-directory of the :term:`Build Directory`. For
    this example, the defaults are set to build for a ``qemux86-64`` target,
    which is suitable for emulation. The package manager used is set to the RPM
    package manager.

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
          SSTATE_MIRRORS ?= "file://.* http://sstate.yoctoproject.org/all/PATH;downloadfilename=PATH"
          BB_HASHSERVE = "auto"
          BB_SIGNATURE_HANDLER = "OEEquivHash"

       The hash equivalence server needs the websockets python module version 9.1
       or later. Debian GNU/Linux 12 (Bookworm) and later, Fedora, CentOS Stream
       9 and later, and Ubuntu 22.04 (LTS) and later, all have a recent enough
       package. Other supported distributions need to get the module some other
       place than their package feed, e.g. via ``pip``. You can otherwise
       install a :term:`Buildtools` tarball by following the instructions in
       the :ref:`system-requirements-buildtools` section of the Yocto Project
       Reference Manual.

#.  **Start the Build:** Continue with the following command to build an OS
    image for the target, which is ``core-image-sato`` in this example:

    .. code-block:: shell

       $ bitbake core-image-sato

    For information on using the ``bitbake`` command, see the
    :ref:`overview-manual/concepts:bitbake` section in the Yocto Project Overview and
    Concepts Manual, or see
    :ref:`bitbake-user-manual/bitbake-user-manual-intro:the bitbake command`
    in the BitBake User Manual.

#.  **Simulate Your Image Using QEMU:** Once this particular image is
    built, you can start QEMU, which is a Quick EMUlator that ships with
    the Yocto Project:

    .. code-block:: shell

       $ runqemu qemux86-64

    If you want to learn more about running QEMU, see the
    :ref:`dev-manual/qemu:using the quick emulator (qemu)` chapter in
    the Yocto Project Development Tasks Manual.

#.  **Exit QEMU:** Exit QEMU by either clicking on the shutdown icon or by typing
    ``Ctrl-C`` in the QEMU transcript window from which you evoked QEMU.
