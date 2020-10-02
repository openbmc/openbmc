.. SPDX-License-Identifier: CC-BY-2.0-UK

***********************************
Setting Up to Use the Yocto Project
***********************************

This chapter provides guidance on how to prepare to use the Yocto
Project. You can learn about creating a team environment to develop
using the Yocto Project, how to set up a :ref:`build
host <dev-manual/dev-manual-start:preparing the build host>`, how to locate
Yocto Project source repositories, and how to create local Git
repositories.

.. _usingpoky-changes-collaborate:

Creating a Team Development Environment
=======================================

It might not be immediately clear how you can use the Yocto Project in a
team development environment, or how to scale it for a large team of
developers. You can adapt the Yocto Project to many different use cases
and scenarios; however, this flexibility could cause difficulties if you
are trying to create a working setup that scales effectively.

To help you understand how to set up this type of environment, this
section presents a procedure that gives you information that can help
you get the results you want. The procedure is high-level and presents
some of the project's most successful experiences, practices, solutions,
and available technologies that have proved to work well in the past;
however, keep in mind, the procedure here is simply a starting point.
You can build off these steps and customize the procedure to fit any
particular working environment and set of practices.

1.  *Determine Who is Going to be Developing:* You first need to
    understand who is going to be doing anything related to the Yocto
    Project and determine their roles. Making this determination is
    essential to completing subsequent steps, which are to get your
    equipment together and set up your development environment's
    hardware topology.

    The following roles exist:

    -  *Application Developer:* This type of developer does application
       level work on top of an existing software stack.

    -  *Core System Developer:* This type of developer works on the
       contents of the operating system image itself.

    -  *Build Engineer:* This type of developer manages Autobuilders and
       releases. Depending on the specifics of the environment, not all
       situations might need a Build Engineer.

    -  *Test Engineer:* This type of developer creates and manages
       automated tests that are used to ensure all application and core
       system development meets desired quality standards.

2.  *Gather the Hardware:* Based on the size and make-up of the team,
    get the hardware together. Ideally, any development, build, or test
    engineer uses a system that runs a supported Linux distribution.
    These systems, in general, should be high performance (e.g. dual,
    six-core Xeons with 24 Gbytes of RAM and plenty of disk space). You
    can help ensure efficiency by having any machines used for testing
    or that run Autobuilders be as high performance as possible.

    .. note::

       Given sufficient processing power, you might also consider
       building Yocto Project development containers to be run under
       Docker, which is described later.

3.  *Understand the Hardware Topology of the Environment:* Once you
    understand the hardware involved and the make-up of the team, you
    can understand the hardware topology of the development environment.
    You can get a visual idea of the machines and their roles across the
    development environment.

4.  *Use Git as Your Source Control Manager (SCM):* Keeping your
    :term:`Metadata` (i.e. recipes,
    configuration files, classes, and so forth) and any software you are
    developing under the control of an SCM system that is compatible
    with the OpenEmbedded build system is advisable. Of all of the SCMs
    supported by BitBake, the Yocto Project team strongly recommends using
    :ref:`overview-manual/overview-manual-development-environment:git`.
    Git is a distributed system
    that is easy to back up, allows you to work remotely, and then
    connects back to the infrastructure.

    .. note::

       For information about BitBake, see the
       BitBake User Manual
       .

    It is relatively easy to set up Git services and create
    infrastructure like
    :yocto_git:`http://git.yoctoproject.org <>`, which is based on
    server software called ``gitolite`` with ``cgit`` being used to
    generate the web interface that lets you view the repositories. The
    ``gitolite`` software identifies users using SSH keys and allows
    branch-based access controls to repositories that you can control as
    little or as much as necessary.

    .. note::

       The setup of these services is beyond the scope of this manual.
       However, sites such as the following exist that describe how to
       perform setup:

       -  `Git documentation <http://git-scm.com/book/ch4-8.html>`__:
          Describes how to install ``gitolite`` on the server.

       -  `Gitolite <http://gitolite.com>`__: Information for
          ``gitolite``.

       -  `Interfaces, frontends, and
          tools <https://git.wiki.kernel.org/index.php/Interfaces,_frontends,_and_tools>`__:
          Documentation on how to create interfaces and frontends for
          Git.

5.  *Set up the Application Development Machines:* As mentioned earlier,
    application developers are creating applications on top of existing
    software stacks. Following are some best practices for setting up
    machines used for application development:

    -  Use a pre-built toolchain that contains the software stack
       itself. Then, develop the application code on top of the stack.
       This method works well for small numbers of relatively isolated
       applications.

    -  Keep your cross-development toolchains updated. You can do this
       through provisioning either as new toolchain downloads or as
       updates through a package update mechanism using ``opkg`` to
       provide updates to an existing toolchain. The exact mechanics of
       how and when to do this depend on local policy.

    -  Use multiple toolchains installed locally into different
       locations to allow development across versions.

6.  *Set up the Core Development Machines:* As mentioned earlier, core
    developers work on the contents of the operating system itself.
    Following are some best practices for setting up machines used for
    developing images:

    -  Have the :term:`OpenEmbedded Build System` available on
       the developer workstations so developers can run their own builds
       and directly rebuild the software stack.

    -  Keep the core system unchanged as much as possible and do your
       work in layers on top of the core system. Doing so gives you a
       greater level of portability when upgrading to new versions of
       the core system or Board Support Packages (BSPs).

    -  Share layers amongst the developers of a particular project and
       contain the policy configuration that defines the project.

7.  *Set up an Autobuilder:* Autobuilders are often the core of the
    development environment. It is here that changes from individual
    developers are brought together and centrally tested. Based on this
    automated build and test environment, subsequent decisions about
    releases can be made. Autobuilders also allow for "continuous
    integration" style testing of software components and regression
    identification and tracking.

    See "`Yocto Project
    Autobuilder <http://autobuilder.yoctoproject.org>`__" for more
    information and links to buildbot. The Yocto Project team has found
    this implementation works well in this role. A public example of
    this is the Yocto Project Autobuilders, which the Yocto Project team
    uses to test the overall health of the project.

    The features of this system are:

    -  Highlights when commits break the build.

    -  Populates an :ref:`sstate
       cache <overview-manual/overview-manual-concepts:shared state cache>` from which
       developers can pull rather than requiring local builds.

    -  Allows commit hook triggers, which trigger builds when commits
       are made.

    -  Allows triggering of automated image booting and testing under
       the QuickEMUlator (QEMU).

    -  Supports incremental build testing and from-scratch builds.

    -  Shares output that allows developer testing and historical
       regression investigation.

    -  Creates output that can be used for releases.

    -  Allows scheduling of builds so that resources can be used
       efficiently.

8.  *Set up Test Machines:* Use a small number of shared, high
    performance systems for testing purposes. Developers can use these
    systems for wider, more extensive testing while they continue to
    develop locally using their primary development system.

9.  *Document Policies and Change Flow:* The Yocto Project uses a
    hierarchical structure and a pull model. Scripts exist to create and
    send pull requests (i.e. ``create-pull-request`` and
    ``send-pull-request``). This model is in line with other open source
    projects where maintainers are responsible for specific areas of the
    project and a single maintainer handles the final "top-of-tree"
    merges.

    .. note::

       You can also use a more collective push model. The
       gitolite
       software supports both the push and pull models quite easily.

    As with any development environment, it is important to document the
    policy used as well as any main project guidelines so they are
    understood by everyone. It is also a good idea to have
    well-structured commit messages, which are usually a part of a
    project's guidelines. Good commit messages are essential when
    looking back in time and trying to understand why changes were made.

    If you discover that changes are needed to the core layer of the
    project, it is worth sharing those with the community as soon as
    possible. Chances are if you have discovered the need for changes,
    someone else in the community needs them also.

10. *Development Environment Summary:* Aside from the previous steps,
    some best practices exist within the Yocto Project development
    environment. Consider the following:

    -  Use :ref:`overview-manual/overview-manual-development-environment:git` as the source control
       system.

    -  Maintain your Metadata in layers that make sense for your
       situation. See the ":ref:`overview-manual/overview-manual-yp-intro:the yocto project layer model`"
       section in the Yocto Project Overview and Concepts Manual and the
       ":ref:`dev-manual/dev-manual-common-tasks:understanding and creating layers`"
       section for more information on layers.

    -  Separate the project's Metadata and code by using separate Git
       repositories. See the ":ref:`overview-manual/overview-manual-development-environment:yocto project source repositories`"
       section in the Yocto Project Overview and Concepts Manual for
       information on these repositories. See the "`Locating Yocto
       Project Source Files <#locating-yocto-project-source-files>`__"
       section for information on how to set up local Git repositories
       for related upstream Yocto Project Git repositories.

    -  Set up the directory for the shared state cache
       (:term:`SSTATE_DIR`) where
       it makes sense. For example, set up the sstate cache on a system
       used by developers in the same organization and share the same
       source directories on their machines.

    -  Set up an Autobuilder and have it populate the sstate cache and
       source directories.

    -  The Yocto Project community encourages you to send patches to the
       project to fix bugs or add features. If you do submit patches,
       follow the project commit guidelines for writing good commit
       messages. See the
       ":ref:`dev-manual/dev-manual-common-tasks:submitting a change to the yocto project`"
       section.

    -  Send changes to the core sooner than later as others are likely
       to run into the same issues. For some guidance on mailing lists
       to use, see the list in the
       ":ref:`dev-manual/dev-manual-common-tasks:submitting a change to the yocto project`"
       section. For a description
       of the available mailing lists, see the ":ref:`resources-mailinglist`" section in
       the Yocto Project Reference Manual.

.. _dev-preparing-the-build-host:

Preparing the Build Host
========================

This section provides procedures to set up a system to be used as your
:term:`Build Host` for
development using the Yocto Project. Your build host can be a native
Linux machine (recommended), it can be a machine (Linux, Mac, or
Windows) that uses `CROPS <https://github.com/crops/poky-container>`__,
which leverages `Docker Containers <https://www.docker.com/>`__ or it
can be a Windows machine capable of running Windows Subsystem For Linux
v2 (WSL).

.. note::

   The Yocto Project is not compatible with
   Windows Subsystem for Linux v1
   . It is compatible but not officially supported nor validated with
   WSLv2. If you still decide to use WSL please upgrade to
   WSLv2
   .

Once your build host is set up to use the Yocto Project, further steps
are necessary depending on what you want to accomplish. See the
following references for information on how to prepare for Board Support
Package (BSP) development and kernel development:

-  *BSP Development:* See the ":ref:`bsp-guide/bsp:preparing your build host to work with bsp layers`"
   section in the Yocto Project Board Support Package (BSP) Developer's
   Guide.

-  *Kernel Development:* See the ":ref:`kernel-dev/kernel-dev-common:preparing the build host to work on the kernel`"
   section in the Yocto Project Linux Kernel Development Manual.

Setting Up a Native Linux Host
------------------------------

Follow these steps to prepare a native Linux machine as your Yocto
Project Build Host:

1. *Use a Supported Linux Distribution:* You should have a reasonably
   current Linux-based host system. You will have the best results with
   a recent release of Fedora, openSUSE, Debian, Ubuntu, RHEL or CentOS
   as these releases are frequently tested against the Yocto Project and
   officially supported. For a list of the distributions under
   validation and their status, see the ":ref:`Supported Linux
   Distributions <detailed-supported-distros>`"
   section in the Yocto Project Reference Manual and the wiki page at
   :yocto_wiki:`Distribution Support </wiki/Distribution_Support>`.

2. *Have Enough Free Memory:* Your system should have at least 50 Gbytes
   of free disk space for building images.

3. *Meet Minimal Version Requirements:* The OpenEmbedded build system
   should be able to run on any modern distribution that has the
   following versions for Git, tar, Python and gcc.

   -  Git 1.8.3.1 or greater

   -  tar 1.28 or greater

   -  Python 3.5.0 or greater.

   -  gcc 5.0 or greater.

   If your build host does not meet any of these three listed version
   requirements, you can take steps to prepare the system so that you
   can still use the Yocto Project. See the
   ":ref:`ref-manual/ref-system-requirements:required git, tar, python and gcc versions`"
   section in the Yocto Project Reference Manual for information.

4. *Install Development Host Packages:* Required development host
   packages vary depending on your build host and what you want to do
   with the Yocto Project. Collectively, the number of required packages
   is large if you want to be able to cover all cases.

   For lists of required packages for all scenarios, see the
   ":ref:`ref-manual/ref-system-requirements:required packages for the build host`"
   section in the Yocto Project Reference Manual.

Once you have completed the previous steps, you are ready to continue
using a given development path on your native Linux machine. If you are
going to use BitBake, see the
":ref:`dev-manual/dev-manual-start:cloning the \`\`poky\`\` repository`"
section. If you are going
to use the Extensible SDK, see the ":doc:`../sdk-manual/sdk-extensible`" Chapter in the Yocto
Project Application Development and the Extensible Software Development
Kit (eSDK) manual. If you want to work on the kernel, see the :doc:`../kernel-dev/kernel-dev`. If you are going to use
Toaster, see the ":doc:`../toaster-manual/toaster-manual-setup-and-use`"
section in the Toaster User Manual.

.. _setting-up-to-use-crops:

Setting Up to Use CROss PlatformS (CROPS)
-----------------------------------------

With `CROPS <https://github.com/crops/poky-container>`__, which
leverages `Docker Containers <https://www.docker.com/>`__, you can
create a Yocto Project development environment that is operating system
agnostic. You can set up a container in which you can develop using the
Yocto Project on a Windows, Mac, or Linux machine.

Follow these general steps to prepare a Windows, Mac, or Linux machine
as your Yocto Project build host:

1. *Determine What Your Build Host Needs:*
   `Docker <https://www.docker.com/what-docker>`__ is a software
   container platform that you need to install on the build host.
   Depending on your build host, you might have to install different
   software to support Docker containers. Go to the Docker installation
   page and read about the platform requirements in "`Supported
   Platforms <https://docs.docker.com/engine/install/#supported-platforms>`__"
   your build host needs to run containers.

2. *Choose What To Install:* Depending on whether or not your build host
   meets system requirements, you need to install "Docker CE Stable" or
   the "Docker Toolbox". Most situations call for Docker CE. However, if
   you have a build host that does not meet requirements (e.g.
   Pre-Windows 10 or Windows 10 "Home" version), you must install Docker
   Toolbox instead.

3. *Go to the Install Site for Your Platform:* Click the link for the
   Docker edition associated with your build host's native software. For
   example, if your build host is running Microsoft Windows Version 10
   and you want the Docker CE Stable edition, click that link under
   "Supported Platforms".

4. *Install the Software:* Once you have understood all the
   pre-requisites, you can download and install the appropriate
   software. Follow the instructions for your specific machine and the
   type of the software you need to install:

   -  Install `Docker CE for
      Windows <https://docs.docker.com/docker-for-windows/install/#install-docker-desktop-on-windows>`__
      for Windows build hosts that meet requirements.

   -  Install `Docker CE for
      MacOs <https://docs.docker.com/docker-for-mac/install/#install-and-run-docker-desktop-on-mac>`__
      for Mac build hosts that meet requirements.

   -  Install `Docker Toolbox for
      Windows <https://docs.docker.com/toolbox/toolbox_install_windows/>`__
      for Windows build hosts that do not meet Docker requirements.

   -  Install `Docker Toolbox for
      MacOS <https://docs.docker.com/toolbox/toolbox_install_mac/>`__
      for Mac build hosts that do not meet Docker requirements.

   -  Install `Docker CE for
      CentOS <https://docs.docker.com/install/linux/docker-ce/centos/>`__
      for Linux build hosts running the CentOS distribution.

   -  Install `Docker CE for
      Debian <https://docs.docker.com/install/linux/docker-ce/debian/>`__
      for Linux build hosts running the Debian distribution.

   -  Install `Docker CE for
      Fedora <https://docs.docker.com/install/linux/docker-ce/fedora/>`__
      for Linux build hosts running the Fedora distribution.

   -  Install `Docker CE for
      Ubuntu <https://docs.docker.com/install/linux/docker-ce/ubuntu/>`__
      for Linux build hosts running the Ubuntu distribution.

5. *Optionally Orient Yourself With Docker:* If you are unfamiliar with
   Docker and the container concept, you can learn more here -
   https://docs.docker.com/get-started/.

6. *Launch Docker or Docker Toolbox:* You should be able to launch
   Docker or the Docker Toolbox and have a terminal shell on your
   development host.

7. *Set Up the Containers to Use the Yocto Project:* Go to
   https://github.com/crops/docker-win-mac-docs/wiki and follow
   the directions for your particular build host (i.e. Linux, Mac, or
   Windows).

   Once you complete the setup instructions for your machine, you have
   the Poky, Extensible SDK, and Toaster containers available. You can
   click those links from the page and learn more about using each of
   those containers.

Once you have a container set up, everything is in place to develop just
as if you were running on a native Linux machine. If you are going to
use the Poky container, see the "`Cloning the ``poky``
Repository <#cloning-the-poky-repository>`__" section. If you are going
to use the Extensible SDK container, see the
":doc:`../sdk-manual/sdk-extensible`" Chapter in the Yocto
Project Application Development and the Extensible Software Development
Kit (eSDK) manual. If you are going to use the Toaster container, see
the ":doc:`../toaster-manual/toaster-manual-setup-and-use`"
section in the Toaster User Manual.

.. _setting-up-to-use-wsl:

Setting Up to Use Windows Subsystem For Linux (WSLv2)
-----------------------------------------------------

With `Windows Subsystem for Linux
(WSLv2) <https://docs.microsoft.com/en-us/windows/wsl/wsl2-about>`__,
you can create a Yocto Project development environment that allows you
to build on Windows. You can set up a Linux distribution inside Windows
in which you can develop using the Yocto Project.

Follow these general steps to prepare a Windows machine using WSLv2 as
your Yocto Project build host:

1. *Make sure your Windows 10 machine is capable of running WSLv2:*
   WSLv2 is only available for Windows 10 builds > 18917. To check which
   build version you are running, you may open a command prompt on
   Windows and execute the command "ver".
   ::

      C:\Users\myuser> ver

      Microsoft Windows [Version 10.0.19041.153]

   If your build is capable of running
   WSLv2 you may continue, for more information on this subject or
   instructions on how to upgrade to WSLv2 visit `Windows 10
   WSLv2 <https://docs.microsoft.com/en-us/windows/wsl/wsl2-install>`__

2. *Install the Linux distribution of your choice inside Windows 10:*
   Once you know your version of Windows 10 supports WSLv2, you can
   install the distribution of your choice from the Microsoft Store.
   Open the Microsoft Store and search for Linux. While there are
   several Linux distributions available, the assumption is that your
   pick will be one of the distributions supported by the Yocto Project
   as stated on the instructions for using a native Linux host. After
   making your selection, simply click "Get" to download and install the
   distribution.

3. *Check your Linux distribution is using WSLv2:* Open a Windows
   PowerShell and run:
   ::

      C:\WINDOWS\system32> wsl -l -v
      NAME    STATE   VERSION
      *Ubuntu Running 2

   Note the version column which says the WSL version
   being used by your distribution, on compatible systems, this can be
   changed back at any point in time.

4. *Optionally Orient Yourself on WSL:* If you are unfamiliar with WSL,
   you can learn more here -
   https://docs.microsoft.com/en-us/windows/wsl/wsl2-about.

5. *Launch your WSL Distibution:* From the Windows start menu simply
   launch your WSL distribution just like any other application.

6. *Optimize your WSLv2 storage often:* Due to the way storage is
   handled on WSLv2, the storage space used by the undelying Linux
   distribution is not reflected immedately, and since bitbake heavily
   uses storage, after several builds, you may be unaware you are
   running out of space. WSLv2 uses a VHDX file for storage, this issue
   can be easily avoided by manually optimizing this file often, this
   can be done in the following way:

   1. *Find the location of your VHDX file:* First you need to find the
      distro app package directory, to achieve this open a Windows
      Powershell as Administrator and run:
      ::

         C:\WINDOWS\system32> Get-AppxPackage -Name "*Ubuntu*" | Select PackageFamilyName
         PackageFamilyName
         -----------------
         CanonicalGroupLimited.UbuntuonWindows_79abcdefgh


      You should now
      replace the PackageFamilyName and your user on the following path
      to find your VHDX file:
      ::

          ls C:\Users\myuser\AppData\Local\Packages\CanonicalGroupLimited.UbuntuonWindows_79abcdefgh\LocalState\
          Mode                 LastWriteTime         Length Name
          -a----         3/14/2020   9:52 PM    57418973184 ext4.vhdx

      Your VHDX file path is:
      ``C:\Users\myuser\AppData\Local\Packages\CanonicalGroupLimited.UbuntuonWindows_79abcdefgh\LocalState\ext4.vhdx``

   2. *Optimize your VHDX file:* Open a Windows Powershell as
      Administrator to optimize your VHDX file, shutting down WSL first:
      ::

         C:\WINDOWS\system32> wsl --shutdown
         C:\WINDOWS\system32> optimize-vhd -Path C:\Users\myuser\AppData\Local\Packages\CanonicalGroupLimited.UbuntuonWindows_79abcdefgh\LocalState\ext4.vhdx -Mode full

      A progress bar should be shown while optimizing the
      VHDX file, and storage should now be reflected correctly on the
      Windows Explorer.

.. note::

   The current implementation of WSLv2 does not have out-of-the-box
   access to external devices such as those connected through a USB
   port, but it automatically mounts your
   C:
   drive on
   /mnt/c/
   (and others), which you can use to share deploy artifacts to be later
   flashed on hardware through Windows, but your build directory should
   not reside inside this mountpoint.

Once you have WSLv2 set up, everything is in place to develop just as if
you were running on a native Linux machine. If you are going to use the
Extensible SDK container, see the ":doc:`../sdk-manual/sdk-extensible`" Chapter in the Yocto
Project Application Development and the Extensible Software Development
Kit (eSDK) manual. If you are going to use the Toaster container, see
the ":doc:`../toaster-manual/toaster-manual-setup-and-use`"
section in the Toaster User Manual.

Locating Yocto Project Source Files
===================================

This section shows you how to locate, fetch and configure the source
files you'll need to work with the Yocto Project.

.. note::

   -  For concepts and introductory information about Git as it is used
      in the Yocto Project, see the ":ref:`overview-manual/overview-manual-development-environment:git`"
      section in the Yocto Project Overview and Concepts Manual.

   -  For concepts on Yocto Project source repositories, see the
      ":ref:`overview-manual/overview-manual-development-environment:yocto project source repositories`"
      section in the Yocto Project Overview and Concepts Manual."

Accessing Source Repositories
-----------------------------

Working from a copy of the upstream :ref:`dev-manual/dev-manual-start:accessing source repositories` is the
preferred method for obtaining and using a Yocto Project release. You
can view the Yocto Project Source Repositories at
:yocto_git:`/`. In particular, you can find the ``poky``
repository at :yocto_git:`/cgit.cgi/poky`.

Use the following procedure to locate the latest upstream copy of the
``poky`` Git repository:

1. *Access Repositories:* Open a browser and go to
   :yocto_git:`/` to access the GUI-based interface into the
   Yocto Project source repositories.

2. *Select the Repository:* Click on the repository in which you are
   interested (e.g. ``poky``).

3. *Find the URL Used to Clone the Repository:* At the bottom of the
   page, note the URL used to clone that repository
   (e.g. :yocto_git:`/cgit.cgi/poky`).

   .. note::

      For information on cloning a repository, see the "
      Cloning the
      poky
      Repository
      " section.

Accessing Index of Releases
---------------------------

Yocto Project maintains an Index of Releases area that contains related
files that contribute to the Yocto Project. Rather than Git
repositories, these files are tarballs that represent snapshots in time
of a given component.

.. note::

   The recommended method for accessing Yocto Project components is to
   use Git to clone the upstream repository and work from within that
   locally cloned repository. The procedure in this section exists
   should you desire a tarball snapshot of any given component.

Follow these steps to locate and download a particular tarball:

1. *Access the Index of Releases:* Open a browser and go to
   :yocto_dl:`Index of Releases </releases>`. The
   list represents released components (e.g. ``bitbake``, ``sato``, and
   so on).

   .. note::

      The
      yocto
      directory contains the full array of released Poky tarballs. The
      poky
      directory in the Index of Releases was historically used for very
      early releases and exists now only for retroactive completeness.

2. *Select a Component:* Click on any released component in which you
   are interested (e.g. ``yocto``).

3. *Find the Tarball:* Drill down to find the associated tarball. For
   example, click on ``yocto-&DISTRO;`` to view files associated with the
   Yocto Project &DISTRO; release (e.g.
   ``&YOCTO_POKY;.tar.bz2``, which is the
   released Poky tarball).

4. *Download the Tarball:* Click the tarball to download and save a
   snapshot of the given component.

Using the Downloads Page
------------------------

The :yocto_home:`Yocto Project Website <>` uses a "DOWNLOADS" page
from which you can locate and download tarballs of any Yocto Project
release. Rather than Git repositories, these files represent snapshot
tarballs similar to the tarballs located in the Index of Releases
described in the "`Accessing Index of
Releases <#accessing-index-of-releases>`__" section.

.. note::

   The recommended method for accessing Yocto Project components is to
   use Git to clone a repository and work from within that local
   repository. The procedure in this section exists should you desire a
   tarball snapshot of any given component.

1. *Go to the Yocto Project Website:* Open The
   :yocto_home:`Yocto Project Website <>` in your browser.

2. *Get to the Downloads Area:* Select the "DOWNLOADS" item from the
   pull-down "SOFTWARE" tab menu near the top of the page.

3. *Select a Yocto Project Release:* Use the menu next to "RELEASE" to
   display and choose a recent or past supported Yocto Project release
   (e.g. &DISTRO_NAME_NO_CAP;, &DISTRO_NAME_NO_CAP_MINUS_ONE;, and so forth).

   .. note::

      For a "map" of Yocto Project releases to version numbers, see the
      Releases
      wiki page.

   You can use the "RELEASE ARCHIVE" link to reveal a menu of all Yocto
   Project releases.

4. *Download Tools or Board Support Packages (BSPs):* From the
   "DOWNLOADS" page, you can download tools or BSPs as well. Just scroll
   down the page and look for what you need.

Accessing Nightly Builds
------------------------

Yocto Project maintains an area for nightly builds that contains tarball
releases at https://autobuilder.yocto.io//pub/nightly/. These builds include Yocto
Project releases ("poky"), toolchains, and builds for supported
machines.

Should you ever want to access a nightly build of a particular Yocto
Project component, use the following procedure:

1. *Locate the Index of Nightly Builds:* Open a browser and go to
   https://autobuilder.yocto.io//pub/nightly/ to access the Nightly Builds.

2. *Select a Date:* Click on the date in which you are interested. If
   you want the latest builds, use "CURRENT".

3. *Select a Build:* Choose the area in which you are interested. For
   example, if you are looking for the most recent toolchains, select
   the "toolchain" link.

4. *Find the Tarball:* Drill down to find the associated tarball.

5. *Download the Tarball:* Click the tarball to download and save a
   snapshot of the given component.

Cloning and Checking Out Branches
=================================

To use the Yocto Project for development, you need a release locally
installed on your development system. This locally installed set of
files is referred to as the :term:`Source Directory`
in the Yocto Project documentation.

The preferred method of creating your Source Directory is by using
:ref:`overview-manual/overview-manual-development-environment:git` to clone a local copy of the upstream
``poky`` repository. Working from a cloned copy of the upstream
repository allows you to contribute back into the Yocto Project or to
simply work with the latest software on a development branch. Because
Git maintains and creates an upstream repository with a complete history
of changes and you are working with a local clone of that repository,
you have access to all the Yocto Project development branches and tag
names used in the upstream repository.

Cloning the ``poky`` Repository
-------------------------------

Follow these steps to create a local version of the upstream
:term:`Poky` Git repository.

1. *Set Your Directory:* Change your working directory to where you want
   to create your local copy of ``poky``.

2. *Clone the Repository:* The following example command clones the
   ``poky`` repository and uses the default name "poky" for your local
   repository:
   ::

      $ git clone git://git.yoctoproject.org/poky
      Cloning into 'poky'...
      remote: Counting objects: 432160, done.
      remote: Compressing objects: 100% (102056/102056), done.
      remote: Total 432160 (delta 323116), reused 432037 (delta 323000)
      Receiving objects: 100% (432160/432160), 153.81 MiB | 8.54 MiB/s, done.
      Resolving deltas: 100% (323116/323116), done.
      Checking connectivity... done.

   Unless you
   specify a specific development branch or tag name, Git clones the
   "master" branch, which results in a snapshot of the latest
   development changes for "master". For information on how to check out
   a specific development branch or on how to check out a local branch
   based on a tag name, see the "`Checking Out By Branch in
   Poky <#checking-out-by-branch-in-poky>`__" and `Checking Out By Tag
   in Poky <#checkout-out-by-tag-in-poky>`__" sections, respectively.

   Once the local repository is created, you can change to that
   directory and check its status. Here, the single "master" branch
   exists on your system and by default, it is checked out:
   ::

      $ cd ~/poky
      $ git status
      On branch master
      Your branch is up-to-date with 'origin/master'.
      nothing to commit, working directory clean
      $ git branch
      * master

   Your local repository of poky is identical to the
   upstream poky repository at the time from which it was cloned. As you
   work with the local branch, you can periodically use the
   ``git pull --rebase`` command to be sure you are up-to-date
   with the upstream branch.

Checking Out by Branch in Poky
------------------------------

When you clone the upstream poky repository, you have access to all its
development branches. Each development branch in a repository is unique
as it forks off the "master" branch. To see and use the files of a
particular development branch locally, you need to know the branch name
and then specifically check out that development branch.

.. note::

   Checking out an active development branch by branch name gives you a
   snapshot of that particular branch at the time you check it out.
   Further development on top of the branch that occurs after check it
   out can occur.

1. *Switch to the Poky Directory:* If you have a local poky Git
   repository, switch to that directory. If you do not have the local
   copy of poky, see the "`Cloning the ``poky``
   Repository <#cloning-the-poky-repository>`__" section.

2. *Determine Existing Branch Names:*
   ::

      $ git branch -a
      * master
      remotes/origin/1.1_M1
      remotes/origin/1.1_M2
      remotes/origin/1.1_M3
      remotes/origin/1.1_M4
      remotes/origin/1.2_M1
      remotes/origin/1.2_M2
      remotes/origin/1.2_M3
      . . .
      remotes/origin/thud
      remotes/origin/thud-next
      remotes/origin/warrior
      remotes/origin/warrior-next
      remotes/origin/zeus
      remotes/origin/zeus-next
      ... and so on ...

3. *Check out the Branch:* Check out the development branch in which you
   want to work. For example, to access the files for the Yocto Project
   &DISTRO; Release (&DISTRO_NAME;), use the following command:
   ::

      $ git checkout -b &DISTRO_NAME; origin/&DISTRO_NAME;
      Branch &DISTRO_NAME; set up to track remote branch &DISTRO_NAME; from origin.
      Switched to a new branch '&DISTRO_NAME;'

   The previous command checks out the "&DISTRO_NAME;" development
   branch and reports that the branch is tracking the upstream
   "origin/&DISTRO_NAME;" branch.

   The following command displays the branches that are now part of your
   local poky repository. The asterisk character indicates the branch
   that is currently checked out for work:
   ::

      $ git branch
        master *
        &DISTRO_NAME;

.. _checkout-out-by-tag-in-poky:

Checking Out by Tag in Poky
---------------------------

Similar to branches, the upstream repository uses tags to mark specific
commits associated with significant points in a development branch (i.e.
a release point or stage of a release). You might want to set up a local
branch based on one of those points in the repository. The process is
similar to checking out by branch name except you use tag names.

.. note::

   Checking out a branch based on a tag gives you a stable set of files
   not affected by development on the branch above the tag.

1. *Switch to the Poky Directory:* If you have a local poky Git
   repository, switch to that directory. If you do not have the local
   copy of poky, see the "`Cloning the ``poky``
   Repository <#cloning-the-poky-repository>`__" section.

2. *Fetch the Tag Names:* To checkout the branch based on a tag name,
   you need to fetch the upstream tags into your local repository:
   ::

      $ git fetch --tags
      $

3. *List the Tag Names:* You can list the tag names now:
   ::

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
      yocto-2.5.3
      yocto-2.6
      yocto-2.6.1
      yocto-2.6.2
      yocto-2.7
      yocto_1.5_M5.rc8


4. *Check out the Branch:*
   ::

      $ git checkout tags/yocto-&DISTRO; -b my_yocto_&DISTRO;
      Switched to a new branch 'my_yocto_&DISTRO;'
      $ git branch
        master
      * my_yocto_&DISTRO;

   The previous command creates and
   checks out a local branch named "my_yocto_&DISTRO;", which is based on
   the commit in the upstream poky repository that has the same tag. In
   this example, the files you have available locally as a result of the
   ``checkout`` command are a snapshot of the "&DISTRO_NAME_NO_CAP;"
   development branch at the point where Yocto Project &DISTRO; was
   released.
