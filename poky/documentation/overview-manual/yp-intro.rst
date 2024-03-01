.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*****************************
Introducing the Yocto Project
*****************************

What is the Yocto Project?
==========================

The Yocto Project is an open source collaboration project that helps
developers create custom Linux-based systems that are designed for
embedded products regardless of the product's hardware architecture.
Yocto Project provides a flexible toolset and a development environment
that allows embedded device developers across the world to collaborate
through shared technologies, software stacks, configurations, and best
practices used to create these tailored Linux images.

Thousands of developers worldwide have discovered that Yocto Project
provides advantages in both systems and applications development,
archival and management benefits, and customizations used for speed,
footprint, and memory utilization. The project is a standard when it
comes to delivering embedded software stacks. The project allows
software customizations and build interchange for multiple hardware
platforms as well as software stacks that can be maintained and scaled.

.. image:: figures/key-dev-elements.png
    :width: 100%

For further introductory information on the Yocto Project, you might be
interested in this
`article <https://www.embedded.com/electronics-blogs/say-what-/4458600/Why-the-Yocto-Project-for-my-IoT-Project->`__
by Drew Moseley and in this short introductory
`video <https://www.youtube.com/watch?v=utZpKM7i5Z4>`__.

The remainder of this section overviews advantages and challenges tied
to the Yocto Project.

Features
--------

Here are features and advantages of the Yocto Project:

-  *Widely Adopted Across the Industry:* Many semiconductor, operating
   system, software, and service vendors adopt and support the Yocto
   Project in their products and services. For a look at the Yocto
   Project community and the companies involved with the Yocto Project,
   see the "COMMUNITY" and "ECOSYSTEM" tabs on the
   :yocto_home:`Yocto Project <>` home page.

-  *Architecture Agnostic:* Yocto Project supports Intel, ARM, MIPS,
   AMD, PPC and other architectures. Most ODMs, OSVs, and chip vendors
   create and supply BSPs that support their hardware. If you have
   custom silicon, you can create a BSP that supports that architecture.

   Aside from broad architecture support, the Yocto Project fully
   supports a wide range of devices emulated by the Quick EMUlator
   (QEMU).

-  *Images and Code Transfer Easily:* Yocto Project output can easily
   move between architectures without moving to new development
   environments. Additionally, if you have used the Yocto Project to
   create an image or application and you find yourself not able to
   support it, commercial Linux vendors such as Wind River, Mentor
   Graphics, Timesys, and ENEA could take it and provide ongoing
   support. These vendors have offerings that are built using the Yocto
   Project.

-  *Flexibility:* Corporations use the Yocto Project many different
   ways. One example is to create an internal Linux distribution as a
   code base the corporation can use across multiple product groups.
   Through customization and layering, a project group can leverage the
   base Linux distribution to create a distribution that works for their
   product needs.

-  *Ideal for Constrained Embedded and IoT devices:* Unlike a full Linux
   distribution, you can use the Yocto Project to create exactly what
   you need for embedded devices. You only add the feature support or
   packages that you absolutely need for the device. For devices that
   have display hardware, you can use available system components such
   as X11, Wayland, GTK+, Qt, Clutter, and SDL (among others) to create
   a rich user experience. For devices that do not have a display or
   where you want to use alternative UI frameworks, you can choose to
   not build these components.

-  *Comprehensive Toolchain Capabilities:* Toolchains for supported
   architectures satisfy most use cases. However, if your hardware
   supports features that are not part of a standard toolchain, you can
   easily customize that toolchain through specification of
   platform-specific tuning parameters. And, should you need to use a
   third-party toolchain, mechanisms built into the Yocto Project allow
   for that.

-  *Mechanism Rules Over Policy:* Focusing on mechanism rather than
   policy ensures that you are free to set policies based on the needs
   of your design instead of adopting decisions enforced by some system
   software provider.

-  *Uses a Layer Model:* The Yocto Project :ref:`layer
   infrastructure <overview-manual/yp-intro:the yocto project layer model>`
   groups related functionality into separate bundles. You can incrementally
   add these grouped functionalities to your project as needed. Using layers to
   isolate and group functionality reduces project complexity and
   redundancy, allows you to easily extend the system, make
   customizations, and keep functionality organized.

-  *Supports Partial Builds:* You can build and rebuild individual
   packages as needed. Yocto Project accomplishes this through its
   :ref:`overview-manual/concepts:shared state cache` (sstate) scheme.
   Being able to build and debug components individually eases project
   development.

-  *Releases According to a Strict Schedule:* Major releases occur on a
   :doc:`six-month cycle </ref-manual/release-process>`
   predictably in October and April. The most recent two releases
   support point releases to address common vulnerabilities and
   exposures. This predictability is crucial for projects based on the
   Yocto Project and allows development teams to plan activities.

-  *Rich Ecosystem of Individuals and Organizations:* For open source
   projects, the value of community is very important. Support forums,
   expertise, and active developers who continue to push the Yocto
   Project forward are readily available.

-  *Binary Reproducibility:* The Yocto Project allows you to be very
   specific about dependencies and achieves very high percentages of
   binary reproducibility (e.g. 99.8% for ``core-image-minimal``). When
   distributions are not specific about which packages are pulled in and
   in what order to support dependencies, other build systems can
   arbitrarily include packages.

-  *License Manifest:* The Yocto Project provides a :ref:`license
   manifest <dev-manual/licenses:maintaining open source license compliance during your product's lifecycle>`
   for review by people who need to track the use of open source
   licenses (e.g. legal teams).

Challenges
----------

Here are challenges you might encounter when developing using the Yocto Project:

-  *Steep Learning Curve:* The Yocto Project has a steep learning curve
   and has many different ways to accomplish similar tasks. It can be
   difficult to choose between such ways.

-  *Understanding What Changes You Need to Make For Your Design Requires
   Some Research:* Beyond the simple tutorial stage, understanding what
   changes need to be made for your particular design can require a
   significant amount of research and investigation. For information
   that helps you transition from trying out the Yocto Project to using
   it for your project, see the ":ref:`what-i-wish-id-known:what i wish i'd known about yocto project`" and
   ":ref:`transitioning-to-a-custom-environment:transitioning to a custom environment for systems development`"
   documents on the Yocto Project website.

-  *Project Workflow Could Be Confusing:* The :ref:`Yocto Project
   workflow <overview-manual/development-environment:the yocto project development environment>`
   could be confusing if you are used to traditional desktop and server
   software development.
   In a desktop development environment, there are mechanisms to easily pull
   and install new packages, which are typically pre-compiled binaries
   from servers accessible over the Internet. Using the Yocto Project,
   you must modify your configuration and rebuild to add additional
   packages.

-  *Working in a Cross-Build Environment Can Feel Unfamiliar:* When
   developing code to run on a target, compilation, execution, and
   testing done on the actual target can be faster than running a
   BitBake build on a development host and then deploying binaries to
   the target for test. While the Yocto Project does support development
   tools on the target, the additional step of integrating your changes
   back into the Yocto Project build environment would be required.
   Yocto Project supports an intermediate approach that involves making
   changes on the development system within the BitBake environment and
   then deploying only the updated packages to the target.

   The Yocto Project :term:`OpenEmbedded Build System`
   produces packages
   in standard formats (i.e. RPM, DEB, IPK, and TAR). You can deploy
   these packages into the running system on the target by using
   utilities on the target such as ``rpm`` or ``ipk``.

-  *Initial Build Times Can be Significant:* Long initial build times
   are unfortunately unavoidable due to the large number of packages
   initially built from scratch for a fully functioning Linux system.
   Once that initial build is completed, however, the shared-state
   (sstate) cache mechanism Yocto Project uses keeps the system from
   rebuilding packages that have not been "touched" since the last
   build. The sstate mechanism significantly reduces times for
   successive builds.

The Yocto Project Layer Model
=============================

The Yocto Project's "Layer Model" is a development model for embedded
and IoT Linux creation that distinguishes the Yocto Project from other
simple build systems. The Layer Model simultaneously supports
collaboration and customization. Layers are repositories that contain
related sets of instructions that tell the :term:`OpenEmbedded Build System`
what to do. You can
collaborate, share, and reuse layers.

Layers can contain changes to previous instructions or settings at any
time. This powerful override capability is what allows you to customize
previously supplied collaborative or community layers to suit your
product requirements.

You use different layers to logically separate information in your
build. As an example, you could have BSP, GUI, distro configuration,
middleware, or application layers. Putting your entire build into one
layer limits and complicates future customization and reuse. Isolating
information into layers, on the other hand, helps simplify future
customizations and reuse. You might find it tempting to keep everything
in one layer when working on a single project. However, the more modular
your Metadata, the easier it is to cope with future changes.

.. note::

   -  Use Board Support Package (BSP) layers from silicon vendors when
      possible.

   -  Familiarize yourself with the
      :yocto_home:`Yocto Project Compatible Layers </software-overview/layers/>`
      or the :oe_layerindex:`OpenEmbedded Layer Index <>`.
      The latter contains more layers but they are less universally
      validated.

   -  Layers support the inclusion of technologies, hardware components,
      and software components. The :ref:`Yocto Project
      Compatible <dev-manual/layers:making sure your layer is compatible with yocto project>`
      designation provides a minimum level of standardization that
      contributes to a strong ecosystem. "YP Compatible" is applied to
      appropriate products and software components such as BSPs, other
      OE-compatible layers, and related open-source projects, allowing
      the producer to use Yocto Project badges and branding assets.

To illustrate how layers are used to keep things modular, consider
machine customizations. These types of customizations typically reside
in a special layer, rather than a general layer, called a BSP Layer.
Furthermore, the machine customizations should be isolated from recipes
and Metadata that support a new GUI environment, for example. This
situation gives you a couple of layers: one for the machine
configurations, and one for the GUI environment. It is important to
understand, however, that the BSP layer can still make machine-specific
additions to recipes within the GUI environment layer without polluting
the GUI layer itself with those machine-specific changes. You can
accomplish this through a recipe that is a BitBake append
(``.bbappend``) file, which is described later in this section.

.. note::

   For general information on BSP layer structure, see the
   :doc:`/bsp-guide/index`.

The :term:`Source Directory`
contains both general layers and BSP layers right out of the box. You
can easily identify layers that ship with a Yocto Project release in the
Source Directory by their names. Layers typically have names that begin
with the string ``meta-``.

.. note::

   It is not a requirement that a layer name begin with the prefix
   ``meta-``, but it is a commonly accepted standard in the Yocto Project
   community.

For example, if you were to examine the :yocto_git:`tree view </poky/tree/>`
of the ``poky`` repository, you will see several layers: ``meta``,
``meta-skeleton``, ``meta-selftest``, ``meta-poky``, and
``meta-yocto-bsp``. Each of these repositories represents a distinct
layer.

For procedures on how to create layers, see the
":ref:`dev-manual/layers:understanding and creating layers`"
section in the Yocto Project Development Tasks Manual.

Components and Tools
====================

The Yocto Project employs a collection of components and tools used by
the project itself, by project developers, and by those using the Yocto
Project. These components and tools are open source projects and
metadata that are separate from the reference distribution
(:term:`Poky`) and the :term:`OpenEmbedded Build System`. Most of the
components and tools are downloaded separately.

This section provides brief overviews of the components and tools
associated with the Yocto Project.

Development Tools
-----------------

Here are tools that help you develop images and applications using
the Yocto Project:

-  *CROPS:* `CROPS <https://github.com/crops/poky-container/>`__ is an
   open source, cross-platform development framework that leverages
   `Docker Containers <https://www.docker.com/>`__. CROPS provides an
   easily managed, extensible environment that allows you to build
   binaries for a variety of architectures on Windows, Linux and Mac OS
   X hosts.

-  *devtool:* This command-line tool is available as part of the
   extensible SDK (eSDK) and is its cornerstone. You can use ``devtool``
   to help build, test, and package software within the eSDK. You can
   use the tool to optionally integrate what you build into an image
   built by the OpenEmbedded build system.

   The ``devtool`` command employs a number of sub-commands that allow
   you to add, modify, and upgrade recipes. As with the OpenEmbedded
   build system, "recipes" represent software packages within
   ``devtool``. When you use ``devtool add``, a recipe is automatically
   created. When you use ``devtool modify``, the specified existing
   recipe is used in order to determine where to get the source code and
   how to patch it. In both cases, an environment is set up so that when
   you build the recipe a source tree that is under your control is used
   in order to allow you to make changes to the source as desired. By
   default, both new recipes and the source go into a "workspace"
   directory under the eSDK. The ``devtool upgrade`` command updates an
   existing recipe so that you can build it for an updated set of source
   files.

   You can read about the ``devtool`` workflow in the Yocto Project
   Application Development and Extensible Software Development Kit
   (eSDK) Manual in the
   ":ref:`sdk-manual/extensible:using \`\`devtool\`\` in your sdk workflow`"
   section.

-  *Extensible Software Development Kit (eSDK):* The eSDK provides a
   cross-development toolchain and libraries tailored to the contents of
   a specific image. The eSDK makes it easy to add new applications and
   libraries to an image, modify the source for an existing component,
   test changes on the target hardware, and integrate into the rest of
   the OpenEmbedded build system. The eSDK gives you a toolchain
   experience supplemented with the powerful set of ``devtool`` commands
   tailored for the Yocto Project environment.

   For information on the eSDK, see the :doc:`/sdk-manual/index` Manual.

-  *Toaster:* Toaster is a web interface to the Yocto Project
   OpenEmbedded build system. Toaster allows you to configure, run, and
   view information about builds. For information on Toaster, see the
   :doc:`/toaster-manual/index`.

-  *VSCode IDE Extension:* The `Yocto Project BitBake
   <https://marketplace.visualstudio.com/items?itemName=yocto-project.yocto-bitbake>`__
   extension for Visual Studio Code provides a rich set of features for working
   with BitBake recipes. The extension provides syntax highlighting,
   hover tips, and completion for BitBake files as well as embedded Python and
   Bash languages. Additional views and commands allow you to efficiently
   browse, build and edit recipes. It also provides SDK integration for
   cross-compiling and debugging through ``devtool``.

   Learn more about the VSCode Extension on the `extension's frontpage
   <https://marketplace.visualstudio.com/items?itemName=yocto-project.yocto-bitbake>`__.

Production Tools
----------------

Here are tools that help with production related activities using the
Yocto Project:

-  *Auto Upgrade Helper:* This utility when used in conjunction with the
   :term:`OpenEmbedded Build System`
   (BitBake and
   OE-Core) automatically generates upgrades for recipes that are based
   on new versions of the recipes published upstream. See
   :ref:`dev-manual/upgrading-recipes:using the auto upgrade helper (auh)`
   for how to set it up.

-  *Recipe Reporting System:* The Recipe Reporting System tracks recipe
   versions available for Yocto Project. The main purpose of the system
   is to help you manage the recipes you maintain and to offer a dynamic
   overview of the project. The Recipe Reporting System is built on top
   of the :oe_layerindex:`OpenEmbedded Layer Index <>`, which
   is a website that indexes OpenEmbedded-Core layers.

-  *Patchwork:* `Patchwork <https://patchwork.yoctoproject.org/>`__
   is a fork of a project originally started by
   `OzLabs <https://ozlabs.org/>`__. The project is a web-based tracking
   system designed to streamline the process of bringing contributions
   into a project. The Yocto Project uses Patchwork as an organizational
   tool to handle patches, which number in the thousands for every
   release.

-  *AutoBuilder:* AutoBuilder is a project that automates build tests
   and quality assurance (QA). By using the public AutoBuilder, anyone
   can determine the status of the current development branch of Poky.

   .. note::

      AutoBuilder is based on buildbot.

   A goal of the Yocto Project is to lead the open source industry with
   a project that automates testing and QA procedures. In doing so, the
   project encourages a development community that publishes QA and test
   plans, publicly demonstrates QA and test plans, and encourages
   development of tools that automate and test and QA procedures for the
   benefit of the development community.

   You can learn more about the AutoBuilder used by the Yocto Project
   Autobuilder :doc:`here </test-manual/understand-autobuilder>`.

-  *Pseudo:* Pseudo is the Yocto Project implementation of
   `fakeroot <http://man.he.net/man1/fakeroot>`__, which is used to run
   commands in an environment that seemingly has root privileges.

   During a build, it can be necessary to perform operations that
   require system administrator privileges. For example, file ownership
   or permissions might need to be defined. Pseudo is a tool that you
   can either use directly or through the environment variable
   ``LD_PRELOAD``. Either method allows these operations to succeed
   even without system administrator privileges.

   Thanks to Pseudo, the Yocto Project never needs root privileges to
   build images for your target system.

   You can read more about Pseudo in the
   ":ref:`overview-manual/concepts:fakeroot and pseudo`" section.

Open-Embedded Build System Components
-------------------------------------

Here are components associated with the :term:`OpenEmbedded Build System`:

-  *BitBake:* BitBake is a core component of the Yocto Project and is
   used by the OpenEmbedded build system to build images. While BitBake
   is key to the build system, BitBake is maintained separately from the
   Yocto Project.

   BitBake is a generic task execution engine that allows shell and
   Python tasks to be run efficiently and in parallel while working
   within complex inter-task dependency constraints. In short, BitBake
   is a build engine that works through recipes written in a specific
   format in order to perform sets of tasks.

   You can learn more about BitBake in the :doc:`BitBake User
   Manual <bitbake:index>`.

-  *OpenEmbedded-Core:* OpenEmbedded-Core (OE-Core) is a common layer of
   metadata (i.e. recipes, classes, and associated files) used by
   OpenEmbedded-derived systems, which includes the Yocto Project. The
   Yocto Project and the OpenEmbedded Project both maintain the
   OpenEmbedded-Core. You can find the OE-Core metadata in the Yocto
   Project :yocto_git:`Source Repositories </poky/tree/meta>`.

   Historically, the Yocto Project integrated the OE-Core metadata
   throughout the Yocto Project source repository reference system
   (Poky). After Yocto Project Version 1.0, the Yocto Project and
   OpenEmbedded agreed to work together and share a common core set of
   metadata (OE-Core), which contained much of the functionality
   previously found in Poky. This collaboration achieved a long-standing
   OpenEmbedded objective for having a more tightly controlled and
   quality-assured core. The results also fit well with the Yocto
   Project objective of achieving a smaller number of fully featured
   tools as compared to many different ones.

   Sharing a core set of metadata results in Poky as an integration
   layer on top of OE-Core. You can see that in this
   :ref:`figure <overview-manual/yp-intro:what is the yocto project?>`.
   The Yocto Project combines various components such as BitBake, OE-Core,
   script "glue", and documentation for its build system.

Reference Distribution (Poky)
-----------------------------

Poky is the Yocto Project reference distribution. It contains the
:term:`OpenEmbedded Build System`
(BitBake and OE-Core) as well as a set of metadata to get you started
building your own distribution. See the figure in
":ref:`overview-manual/yp-intro:what is the yocto project?`"
section for an illustration that shows Poky and its relationship with
other parts of the Yocto Project.

To use the Yocto Project tools and components, you can download
(``clone``) Poky and use it to bootstrap your own distribution.

.. note::

   Poky does not contain binary files. It is a working example of how to
   build your own custom Linux distribution from source.

You can read more about Poky in the
":ref:`overview-manual/yp-intro:reference embedded distribution (poky)`"
section.

Packages for Finished Targets
-----------------------------

Here are components associated with packages for finished targets:

-  *Matchbox:* Matchbox is an Open Source, base environment for the X
   Window System running on non-desktop, embedded platforms such as
   handhelds, set-top boxes, kiosks, and anything else for which screen
   space, input mechanisms, or system resources are limited.

   Matchbox consists of a number of interchangeable and optional
   applications that you can tailor to a specific, non-desktop platform
   to enhance usability in constrained environments.

   You can find the Matchbox source in the Yocto Project
   :yocto_git:`Source Repositories <>`.

-  *Opkg:* Open PacKaGe management (opkg) is a lightweight package
   management system based on the itsy package (ipkg) management system.
   Opkg is written in C and resembles Advanced Package Tool (APT) and
   Debian Package (dpkg) in operation.

   Opkg is intended for use on embedded Linux devices and is used in
   this capacity in the :oe_home:`OpenEmbedded <>` and
   `OpenWrt <https://openwrt.org/>`__ projects, as well as the Yocto
   Project.

   .. note::

      As best it can, opkg maintains backwards compatibility with ipkg
      and conforms to a subset of Debian's policy manual regarding
      control files.

   You can find the opkg source in the Yocto Project
   :yocto_git:`Source Repositories <>`.

Archived Components
-------------------

The Build Appliance is a virtual machine image that enables you to build
and boot a custom embedded Linux image with the Yocto Project using a
non-Linux development system.

Historically, the Build Appliance was the second of three methods by
which you could use the Yocto Project on a system that was not native to
Linux.

#. *Hob:* Hob, which is now deprecated and is no longer available since
   the 2.1 release of the Yocto Project provided a rudimentary,
   GUI-based interface to the Yocto Project. Toaster has fully replaced
   Hob.

#. *Build Appliance:* Post Hob, the Build Appliance became available. It
   was never recommended that you use the Build Appliance as a
   day-to-day production development environment with the Yocto Project.
   Build Appliance was useful as a way to try out development in the
   Yocto Project environment.

#. *CROPS:* The final and best solution available now for developing
   using the Yocto Project on a system not native to Linux is with
   :ref:`CROPS <overview-manual/yp-intro:development tools>`.

Development Methods
===================

The Yocto Project development environment usually involves a
:term:`Build Host` and target
hardware. You use the Build Host to build images and develop
applications, while you use the target hardware to execute deployed
software.

This section provides an introduction to the choices or development
methods you have when setting up your Build Host. Depending on your
particular workflow preference and the type of operating system your
Build Host runs, you have several choices.

.. note::

   For additional detail about the Yocto Project development
   environment, see the ":doc:`/overview-manual/development-environment`"
   chapter.

-  *Native Linux Host:* By far the best option for a Build Host. A
   system running Linux as its native operating system allows you to
   develop software by directly using the
   :term:`BitBake` tool. You can
   accomplish all aspects of development from a regular shell in a
   supported Linux distribution.

   For information on how to set up a Build Host on a system running
   Linux as its native operating system, see the
   ":ref:`dev-manual/start:setting up a native linux host`"
   section in the Yocto Project Development Tasks Manual.

-  *CROss PlatformS (CROPS):* Typically, you use
   `CROPS <https://github.com/crops/poky-container/>`__, which leverages
   `Docker Containers <https://www.docker.com/>`__, to set up a Build
   Host that is not running Linux (e.g. Microsoft Windows or macOS).

   .. note::

      You can, however, use CROPS on a Linux-based system.

   CROPS is an open source, cross-platform development framework that
   provides an easily managed, extensible environment for building
   binaries targeted for a variety of architectures on Windows, macOS,
   or Linux hosts. Once the Build Host is set up using CROPS, you can
   prepare a shell environment to mimic that of a shell being used on a
   system natively running Linux.

   For information on how to set up a Build Host with CROPS, see the
   ":ref:`dev-manual/start:setting up to use cross platforms (crops)`"
   section in the Yocto Project Development Tasks Manual.

-  *Windows Subsystem For Linux (WSL 2):* You may use Windows Subsystem
   For Linux version 2 to set up a Build Host using Windows 10 or later,
   or Windows Server 2019 or later.

   The Windows Subsystem For Linux allows Windows to run a real Linux
   kernel inside of a lightweight virtual machine (VM).

   For information on how to set up a Build Host with WSL 2, see the
   ":ref:`dev-manual/start:setting up to use windows subsystem for linux (wsl 2)`"
   section in the Yocto Project Development Tasks Manual.

-  *Toaster:* Regardless of what your Build Host is running, you can use
   Toaster to develop software using the Yocto Project. Toaster is a web
   interface to the Yocto Project's :term:`OpenEmbedded Build System`.
   The interface allows you to configure and run your builds. Information
   about builds is collected and stored in a database. You can use Toaster
   to configure and start builds on multiple remote build servers.

   For information about and how to use Toaster, see the
   :doc:`/toaster-manual/index`.

-  *Using the VSCode Extension:* You can use the `Yocto Project BitBake
   <https://marketplace.visualstudio.com/items?itemName=yocto-project.yocto-bitbake>`__
   extension for Visual Studio Code to start your BitBake builds through a
   graphical user interface.

   Learn more about the VSCode Extension on the `extension's marketplace page
   <https://marketplace.visualstudio.com/items?itemName=yocto-project.yocto-bitbake>`__

Reference Embedded Distribution (Poky)
======================================

"Poky", which is pronounced *Pock*-ee, is the name of the Yocto
Project's reference distribution or Reference OS Kit. Poky contains the
:term:`OpenEmbedded Build System` (:term:`BitBake` and
:term:`OpenEmbedded-Core (OE-Core)`) as well as a set of
:term:`Metadata` to get you started building your own distro. In other
words, Poky is a base specification of the functionality needed for a
typical embedded system as well as the components from the Yocto Project
that allow you to build a distribution into a usable binary image.

Poky is a combined repository of BitBake, OpenEmbedded-Core (which is
found in ``meta``), ``meta-poky``, ``meta-yocto-bsp``, and documentation
provided all together and known to work well together. You can view
these items that make up the Poky repository in the
:yocto_git:`Source Repositories </poky/tree/>`.

.. note::

   If you are interested in all the contents of the
   poky
   Git repository, see the ":ref:`ref-manual/structure:top-level core components`"
   section in the Yocto Project Reference Manual.

The following figure illustrates what generally comprises Poky:

.. image:: figures/poky-reference-distribution.png
    :width: 100%

-  BitBake is a task executor and scheduler that is the heart of the
   OpenEmbedded build system.

-  ``meta-poky``, which is Poky-specific metadata.

-  ``meta-yocto-bsp``, which are Yocto Project-specific Board Support
   Packages (BSPs).

-  OpenEmbedded-Core (OE-Core) metadata, which includes shared
   configurations, global variable definitions, shared classes,
   packaging, and recipes. Classes define the encapsulation and
   inheritance of build logic. Recipes are the logical units of software
   and images to be built.

-  Documentation, which contains the Yocto Project source files used to
   make the set of user manuals.

.. note::

   While Poky is a "complete" distribution specification and is tested
   and put through QA, you cannot use it as a product "out of the box"
   in its current form.

To use the Yocto Project tools, you can use Git to clone (download) the
Poky repository then use your local copy of the reference distribution
to bootstrap your own distribution.

.. note::

   Poky does not contain binary files. It is a working example of how to
   build your own custom Linux distribution from source.

Poky has a regular, well established, six-month release cycle under its
own version. Major releases occur at the same time major releases (point
releases) occur for the Yocto Project, which are typically in the Spring
and Fall. For more information on the Yocto Project release schedule and
cadence, see the ":doc:`/ref-manual/release-process`" chapter in the
Yocto Project Reference Manual.

Much has been said about Poky being a "default configuration". A default
configuration provides a starting image footprint. You can use Poky out
of the box to create an image ranging from a shell-accessible minimal
image all the way up to a Linux Standard Base-compliant image that uses
a GNOME Mobile and Embedded (GMAE) based reference user interface called
Sato.

One of the most powerful properties of Poky is that every aspect of a
build is controlled by the metadata. You can use metadata to augment
these base image types by adding metadata :ref:`layers
<overview-manual/yp-intro:the yocto project layer model>` that extend
functionality.
These layers can provide, for example, an additional software stack for
an image type, add a board support package (BSP) for additional
hardware, or even create a new image type.

Metadata is loosely grouped into configuration files or package recipes.
A recipe is a collection of non-executable metadata used by BitBake to
set variables or define additional build-time tasks. A recipe contains
fields such as the recipe description, the recipe version, the license
of the package and the upstream source repository. A recipe might also
indicate that the build process uses autotools, make, distutils or any
other build process, in which case the basic functionality can be
defined by the classes it inherits from the OE-Core layer's class
definitions in ``./meta/classes``. Within a recipe you can also define
additional tasks as well as task prerequisites. Recipe syntax through
BitBake also supports both ``:prepend`` and ``:append`` operators as a
method of extending task functionality. These operators inject code into
the beginning or end of a task. For information on these BitBake
operators, see the
":ref:`bitbake-user-manual/bitbake-user-manual-metadata:appending and prepending (override style syntax)`"
section in the BitBake User's Manual.

The OpenEmbedded Build System Workflow
======================================

The :term:`OpenEmbedded Build System` uses a "workflow" to
accomplish image and SDK generation. The following figure overviews that
workflow:

.. image:: figures/YP-flow-diagram.png
    :width: 100%

Here is a brief summary of the "workflow":

#. Developers specify architecture, policies, patches and configuration
   details.

#. The build system fetches and downloads the source code from the
   specified location. The build system supports standard methods such
   as tarballs or source code repositories systems such as Git.

#. Once source code is downloaded, the build system extracts the sources
   into a local work area where patches are applied and common steps for
   configuring and compiling the software are run.

#. The build system then installs the software into a temporary staging
   area where the binary package format you select (DEB, RPM, or IPK) is
   used to roll up the software.

#. Different QA and sanity checks run throughout entire build process.

#. After the binaries are created, the build system generates a binary
   package feed that is used to create the final root file image.

#. The build system generates the file system image and a customized
   Extensible SDK (eSDK) for application development in parallel.

For a very detailed look at this workflow, see the
":ref:`overview-manual/concepts:openembedded build system concepts`" section.

Some Basic Terms
================

It helps to understand some basic fundamental terms when learning the
Yocto Project. Although there is a list of terms in the ":doc:`Yocto Project
Terms </ref-manual/terms>`" section of the Yocto Project
Reference Manual, this section provides the definitions of some terms
helpful for getting started:

-  *Configuration Files:* Files that hold global definitions of
   variables, user-defined variables, and hardware configuration
   information. These files tell the :term:`OpenEmbedded Build System`
   what to build and
   what to put into the image to support a particular platform.

-  *Extensible Software Development Kit (eSDK):* A custom SDK for
   application developers. This eSDK allows developers to incorporate
   their library and programming changes back into the image to make
   their code available to other application developers. For information
   on the eSDK, see the :doc:`/sdk-manual/index` manual.

-  *Layer:* A collection of related recipes. Layers allow you to
   consolidate related metadata to customize your build. Layers also
   isolate information used when building for multiple architectures.
   Layers are hierarchical in their ability to override previous
   specifications. You can include any number of available layers from
   the Yocto Project and customize the build by adding your own layers
   after them. You can search the Layer Index for layers used within
   Yocto Project.

   For more detailed information on layers, see the
   ":ref:`dev-manual/layers:understanding and creating layers`"
   section in the Yocto Project Development Tasks Manual. For a
   discussion specifically on BSP Layers, see the
   ":ref:`bsp-guide/bsp:bsp layers`" section in the Yocto
   Project Board Support Packages (BSP) Developer's Guide.

-  *Metadata:* A key element of the Yocto Project is the Metadata that
   is used to construct a Linux distribution and is contained in the
   files that the OpenEmbedded build system parses when building an
   image. In general, Metadata includes recipes, configuration files,
   and other information that refers to the build instructions
   themselves, as well as the data used to control what things get built
   and the effects of the build. Metadata also includes commands and
   data used to indicate what versions of software are used, from where
   they are obtained, and changes or additions to the software itself
   (patches or auxiliary files) that are used to fix bugs or customize
   the software for use in a particular situation. OpenEmbedded-Core is
   an important set of validated metadata.

-  *OpenEmbedded Build System:* The terms "BitBake" and "build system"
   are sometimes used for the OpenEmbedded Build System.

   BitBake is a task scheduler and execution engine that parses
   instructions (i.e. recipes) and configuration data. After a parsing
   phase, BitBake creates a dependency tree to order the compilation,
   schedules the compilation of the included code, and finally executes
   the building of the specified custom Linux image (distribution).
   BitBake is similar to the ``make`` tool.

   During a build process, the build system tracks dependencies and
   performs a native or cross-compilation of each package. As a first
   step in a cross-build setup, the framework attempts to create a
   cross-compiler toolchain (i.e. Extensible SDK) suited for the target
   platform.

-  *OpenEmbedded-Core (OE-Core):* OE-Core is metadata comprised of
   foundation recipes, classes, and associated files that are meant to
   be common among many different OpenEmbedded-derived systems,
   including the Yocto Project. OE-Core is a curated subset of an
   original repository developed by the OpenEmbedded community that has
   been pared down into a smaller, core set of continuously validated
   recipes. The result is a tightly controlled and quality-assured core
   set of recipes.

   You can see the Metadata in the ``meta`` directory of the Yocto
   Project :yocto_git:`Source Repositories <>`.

-  *Packages:* In the context of the Yocto Project, this term refers to
   a recipe's packaged output produced by BitBake (i.e. a "baked
   recipe"). A package is generally the compiled binaries produced from
   the recipe's sources. You "bake" something by running it through
   BitBake.

   It is worth noting that the term "package" can, in general, have
   subtle meanings. For example, the packages referred to in the
   ":ref:`ref-manual/system-requirements:required packages for the build host`"
   section in the Yocto Project Reference Manual are compiled binaries
   that, when installed, add functionality to your host Linux
   distribution.

   Another point worth noting is that historically within the Yocto
   Project, recipes were referred to as packages --- thus, the existence
   of several BitBake variables that are seemingly mis-named, (e.g.
   :term:`PR`,
   :term:`PV`, and
   :term:`PE`).

-  *Poky:* Poky is a reference embedded distribution and a reference
   test configuration. Poky provides the following:

   -  A base-level functional distro used to illustrate how to customize
      a distribution.

   -  A means by which to test the Yocto Project components (i.e. Poky
      is used to validate the Yocto Project).

   -  A vehicle through which you can download the Yocto Project.

   Poky is not a product level distro. Rather, it is a good starting
   point for customization.

   .. note::

      Poky is an integration layer on top of OE-Core.

-  *Recipe:* The most common form of metadata. A recipe contains a list
   of settings and tasks (i.e. instructions) for building packages that
   are then used to build the binary image. A recipe describes where you
   get source code and which patches to apply. Recipes describe
   dependencies for libraries or for other recipes as well as
   configuration and compilation options. Related recipes are
   consolidated into a layer.
