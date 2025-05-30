.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*******************
Yocto Project Terms
*******************

Here is a list of terms and definitions users new to the Yocto Project
development environment might find helpful. While some of these terms are
universal, the list includes them just in case:

.. glossary::

   :term:`Append Files`
      Files that append build information to a recipe file.  Append files are
      known as BitBake append files and ``.bbappend`` files. The OpenEmbedded
      build system expects every append file to have a corresponding recipe
      (``.bb``) file. Furthermore, the append file and corresponding recipe file
      must use the same root filename.  The filenames can differ only in the
      file type suffix used (e.g. ``formfactor_0.0.bb`` and
      ``formfactor_0.0.bbappend``).

      Information in append files extends or overrides the information in the
      similarly-named recipe file. For an example of an append file in use, see
      the    ":ref:`dev-manual/layers:appending other layers metadata with your layer`"
      section in the Yocto Project Development Tasks Manual.

      When you name an append file, you can use the "``%``" wildcard character
      to allow for matching recipe names. For example, suppose you have an
      append file named as follows::

         busybox_1.21.%.bbappend

      That append file
      would match any ``busybox_1.21.x.bb`` version of the recipe. So,
      the append file would match any of the following recipe names:

      .. code-block:: shell

         busybox_1.21.1.bb
         busybox_1.21.2.bb
         busybox_1.21.3.bb
         busybox_1.21.10.bb
         busybox_1.21.25.bb

      .. note::

         The use of the "%" character is limited in that it only works
         directly in front of the .bbappend portion of the append file's
         name. You cannot use the wildcard character in any other location of
         the name.

   :term:`BitBake`
      The task executor and scheduler used by the OpenEmbedded build system to
      build images. For more information on BitBake, see the :doc:`BitBake User
      Manual <bitbake:index>`.

   :term:`Board Support Package (BSP)`
      A group of drivers, definitions, and other components that provide support
      for a specific hardware configuration. For more information on BSPs, see
      the :doc:`/bsp-guide/index`.

   :term:`Build Directory`
      This term refers to the area used by the OpenEmbedded build system for
      builds. The area is created when you ``source`` the setup environment
      script that is found in the Source Directory
      (i.e. :ref:`ref-manual/structure:``oe-init-build-env```). The
      :term:`TOPDIR` variable points to the :term:`Build Directory`.

      You have a lot of flexibility when creating the :term:`Build Directory`.
      Here are some examples that show how to create the directory.  The
      examples assume your :term:`Source Directory` is named ``poky``:

         -  Create the :term:`Build Directory` inside your Source Directory and let
            the name of the :term:`Build Directory` default to ``build``:

            .. code-block:: shell

               $ cd poky
               $ source oe-init-build-env

         -  Create the :term:`Build Directory` inside your home directory and
            specifically name it ``test-builds``:

            .. code-block:: shell

               $ source poky/oe-init-build-env test-builds

         -  Provide a directory path and specifically name the
            :term:`Build Directory`. Any intermediate folders in the pathname
            must exist.  This next example creates a :term:`Build Directory`
            named ``YP-&DISTRO;`` within the existing directory ``mybuilds``:

            .. code-block:: shell

               $ source poky/oe-init-build-env mybuilds/YP-&DISTRO;

      .. note::

         By default, the :term:`Build Directory` contains :term:`TMPDIR`, which is a
         temporary directory the build system uses for its work. :term:`TMPDIR` cannot
         be under NFS. Thus, by default, the :term:`Build Directory` cannot be under
         NFS. However, if you need the :term:`Build Directory` to be under NFS, you can
         set this up by setting :term:`TMPDIR` in your ``local.conf`` file to use a local
         drive. Doing so effectively separates :term:`TMPDIR` from :term:`TOPDIR`, which is the
         :term:`Build Directory`.

   :term:`Build Host`
      The system used to build images in a Yocto Project Development
      environment. The build system is sometimes referred to as the development
      host.

   :term:`buildtools`
      Build tools in binary form, providing required versions of development
      tools (such as Git, GCC, Python and make), to run the OpenEmbedded build
      system on a development host without such minimum versions.

      See the ":ref:`system-requirements-buildtools`" paragraph in the
      Reference Manual for details about downloading or building an archive
      of such tools.

   :term:`buildtools-extended`
      A set of :term:`buildtools` binaries extended with additional development
      tools, such as a required version of the GCC compiler to run the
      OpenEmbedded build system.

      See the ":ref:`system-requirements-buildtools`" paragraph in the
      Reference Manual for details about downloading or building an archive
      of such tools.

   :term:`buildtools-make`
      A variant of :term:`buildtools`, just providing the required
      version of ``make`` to run the OpenEmbedded build system.

   :term:`Classes`
      Files that provide for logic encapsulation and inheritance so that
      commonly used patterns can be defined once and then easily used in
      multiple recipes. For reference information on the Yocto Project classes,
      see the ":ref:`ref-manual/classes:Classes`" chapter. Class files end with the
      ``.bbclass`` filename extension.

   :term:`Configuration File`
      Files that hold global definitions of variables, user-defined variables,
      and hardware configuration information. These files tell the OpenEmbedded
      build system what to build and what to put into the image to support a
      particular platform.

      Configuration files end with a ``.conf`` filename extension. The
      :file:`conf/local.conf` configuration file in the :term:`Build Directory`
      contains user-defined variables that affect every build. The
      :file:`meta-poky/conf/distro/poky.conf` configuration file defines Yocto
      "distro" configuration variables used only when building with this
      policy. Machine configuration files, which are located throughout the
      :term:`Source Directory`, define variables for specific hardware and are
      only used when building for that target (e.g. the
      :file:`machine/beaglebone.conf` configuration file defines variables for
      the Texas Instruments ARM Cortex-A8 development board).

   :term:`Container Layer`
      A flexible definition that typically refers to a single Git checkout
      which contains multiple (and typically related) sub-layers which can
      be included independently in your project's ``bblayers.conf`` file.

      In some cases, such as with OpenEmbedded's :oe_git:`meta-openembedded </meta-openembedded>`
      layer, the top level ``meta-openembedded/`` directory is not itself an actual layer,
      so you would never explicitly include it in a ``bblayers.conf`` file;
      rather, you would include any number of its layer subdirectories, such as
      :oe_git:`meta-oe </meta-openembedded/tree/meta-oe>`, :oe_git:`meta-python
      </meta-openembedded/tree/meta-python>` and so on.

      On the other hand, some container layers (such as
      :yocto_git:`meta-security </meta-security>`)
      have a top-level directory that is itself an actual layer, as well as
      a variety of sub-layers, both of which could be included in your
      ``bblayers.conf`` file.

      In either case, the phrase "container layer" is simply used to describe
      a directory structure which contains multiple valid OpenEmbedded layers.

   :term:`Cross-Development Toolchain`
      In general, a cross-development toolchain is a collection of software
      development tools and utilities that run on one architecture and allow you
      to develop software for a different, or targeted, architecture. These
      toolchains contain cross-compilers, linkers, and debuggers that are
      specific to the target architecture.

      The Yocto Project supports two different cross-development toolchains:

      - A toolchain only used by and within BitBake when building an image for a
        target architecture.

      - A relocatable toolchain used outside of BitBake by developers when
        developing applications that will run on a targeted device.

      Creation of these toolchains is simple and automated. For information on
      toolchain concepts as they apply to the Yocto Project, see the
      ":ref:`overview-manual/concepts:Cross-Development
      Toolchain Generation`" section in the Yocto Project Overview and Concepts
      Manual. You can also find more information on using the relocatable
      toolchain in the :doc:`/sdk-manual/index` manual.

   :term:`Extensible Software Development Kit (eSDK)`
      A custom SDK for application developers. This eSDK allows developers to
      incorporate their library and programming changes back into the image to
      make their code available to other application developers.

      For information on the eSDK, see the :doc:`/sdk-manual/index` manual.

   :term:`Image`
      An image is an artifact of the BitBake build process given a collection of
      recipes and related Metadata. Images are the binary output that run on
      specific hardware or QEMU and are used for specific use-cases. For a list
      of the supported image types that the Yocto Project provides, see the
      ":ref:`ref-manual/images:Images`" chapter.

   :term:`Initramfs`
      An Initial RAM Filesystem (:term:`Initramfs`) is an optionally compressed
      :wikipedia:`cpio <Cpio>` archive which is extracted
      by the Linux kernel into RAM in a special :wikipedia:`tmpfs <Tmpfs>`
      instance, used as the initial root filesystem.

      This is a replacement for the legacy init RAM disk ("initrd")
      technique, booting on an emulated block device in RAM, but being less
      efficient because of the overhead of going through a filesystem and
      having to duplicate accessed file contents in the file cache in RAM,
      as for any block device.

      .. note::

         As far as bootloaders are concerned, :term:`Initramfs` and "initrd"
         images are still copied to RAM in the same way. That's why most
         most bootloaders refer to :term:`Initramfs` images as "initrd"
         or "init RAM disk".

      This kind of mechanism is typically used for two reasons:

      -  For booting the same kernel binary on multiple systems requiring
         different device drivers. The :term:`Initramfs` image is then customized
         for each type of system, to include the specific kernel modules
         necessary to access the final root filesystem. This technique
         is used on all GNU / Linux distributions for desktops and servers.

      -  For booting faster. As the root filesystem is extracted into RAM,
         accessing the first user-space applications is very fast, compared
         to having to initialize a block device, to access multiple blocks
         from it, and to go through a filesystem having its own overhead.
         For example, this allows to display a splashscreen very early,
         and to later take care of mounting the final root filesystem and
         loading less time-critical kernel drivers.

      This cpio archive can either be loaded to RAM by the bootloader,
      or be included in the kernel binary.

      For information on creating and using an :term:`Initramfs`, see the
      ":ref:`dev-manual/building:building an initial ram filesystem (Initramfs) image`"
      section in the Yocto Project Development Tasks Manual.

   :term:`Layer`
      A collection of related recipes. Layers allow you to consolidate related
      metadata to customize your build. Layers also isolate information used
      when building for multiple architectures.  Layers are hierarchical in
      their ability to override previous specifications. You can include any
      number of available layers from the Yocto Project and customize the build
      by adding your layers after them. You can search the Layer Index for
      layers used within Yocto Project.

      For introductory information on layers, see the
      ":ref:`overview-manual/yp-intro:The Yocto Project Layer
      Model`" section in the Yocto Project Overview and Concepts Manual. For
      more detailed information on layers, see the
      ":ref:`dev-manual/layers:Understanding and Creating
      Layers`" section in the Yocto Project Development Tasks Manual. For a
      discussion specifically on BSP Layers, see the ":ref:`bsp-guide/bsp:BSP
      Layers`" section in the Yocto Project Board Support Packages (BSP)
      Developer's Guide.

   :term:`LTS`
      This term means "Long Term Support", and in the context of the Yocto
      Project, it corresponds to selected stable releases for which bug and
      security fixes are provided for at least four years. See
      the :ref:`ref-long-term-support-releases` section for details.

   :term:`Metadata`
      A key element of the Yocto Project is the Metadata that
      is used to construct a Linux distribution and is contained in the
      files that the :term:`OpenEmbedded Build System`
      parses when building an image. In general, Metadata includes recipes,
      configuration files, and other information that refers to the build
      instructions themselves, as well as the data used to control what
      things get built and the effects of the build. Metadata also includes
      commands and data used to indicate what versions of software are
      used, from where they are obtained, and changes or additions to the
      software itself (patches or auxiliary files) that are used to fix
      bugs or customize the software for use in a particular situation.
      OpenEmbedded-Core is an important set of validated metadata.

      In the context of the kernel ("kernel Metadata"), the term refers to
      the kernel config fragments and features contained in the
      :yocto_git:`yocto-kernel-cache </yocto-kernel-cache>`
      Git repository.

   :term:`Mixin`
      A :term:`Mixin` layer is a layer which can be created by the community to
      add a specific feature or support a new version of some package for an
      :term:`LTS` release. See the :ref:`ref-long-term-support-releases`
      section for details.

   :term:`OpenEmbedded-Core (OE-Core)`
      OE-Core is metadata comprised of
      foundational recipes, classes, and associated files that are meant to
      be common among many different OpenEmbedded-derived systems,
      including the Yocto Project. OE-Core is a curated subset of an
      original repository developed by the OpenEmbedded community that has
      been pared down into a smaller, core set of continuously validated
      recipes. The result is a tightly controlled and an quality-assured
      core set of recipes.

      You can see the Metadata in the ``meta`` directory of the Yocto
      Project :yocto_git:`Source Repositories </poky>`.

   :term:`OpenEmbedded Build System`
      The build system specific to the Yocto
      Project. The OpenEmbedded build system is based on another project
      known as "Poky", which uses :term:`BitBake` as the task
      executor. Throughout the Yocto Project documentation set, the
      OpenEmbedded build system is sometimes referred to simply as "the
      build system". If other build systems, such as a host or target build
      system are referenced, the documentation clearly states the
      difference.

      .. note::

         For some historical information about Poky, see the :term:`Poky` term.

   :term:`Package`
      In the context of the Yocto Project, this term refers to a
      recipe's packaged output produced by BitBake (i.e. a "baked recipe").
      A package is generally the compiled binaries produced from the
      recipe's sources. You "bake" something by running it through BitBake.

      It is worth noting that the term "package" can, in general, have
      subtle meanings. For example, the packages referred to in the
      ":ref:`ref-manual/system-requirements:required packages for the build host`"
      section are compiled binaries that, when installed, add functionality to
      your Linux distribution.

      Another point worth noting is that historically within the Yocto
      Project, recipes were referred to as packages --- thus, the existence
      of several BitBake variables that are seemingly mis-named, (e.g.
      :term:`PR`, :term:`PV`, and
      :term:`PE`).

   :term:`Package Groups`
      Arbitrary groups of software Recipes. You use
      package groups to hold recipes that, when built, usually accomplish a
      single task. For example, a package group could contain the recipes
      for a company's proprietary or value-add software. Or, the package
      group could contain the recipes that enable graphics. A package group
      is really just another recipe. Because package group files are
      recipes, they end with the ``.bb`` filename extension.

   :term:`Poky`
      Poky, which is pronounced *Pock*-ee, is a reference embedded
      distribution and a reference test configuration. Poky provides the
      following:

      -  A base-level functional distro used to illustrate how to customize
         a distribution.

      -  A means by which to test the Yocto Project components (i.e. Poky
         is used to validate the Yocto Project).

      -  A vehicle through which you can download the Yocto Project.

      Poky is not a product level distro. Rather, it is a good starting
      point for customization.

      .. note::

         Poky began as an open-source project initially developed by
         OpenedHand. OpenedHand developed Poky from the existing
         OpenEmbedded build system to create a commercially supportable
         build system for embedded Linux. After Intel Corporation acquired
         OpenedHand, the poky project became the basis for the Yocto
         Project's build system.

   :term:`Recipe`
      A set of instructions for building packages. A recipe
      describes where you get source code, which patches to apply, how to
      configure the source, how to compile it and so on. Recipes also
      describe dependencies for libraries or for other recipes. Recipes
      represent the logical unit of execution, the software to build, the
      images to build, and use the ``.bb`` file extension.

   :term:`Reference Kit`
      A working example of a system, which includes a
      :term:`BSP<Board Support Package (BSP)>` as well as a
      :term:`build host<Build Host>` and other components, that can
      work on specific hardware.

   :term:`SBOM`
      This term means *Software Bill of Materials*. When you distribute
      software, it offers a description of all the components you used,
      their corresponding licenses, their dependencies, the changes that were
      applied and the known vulnerabilities that were fixed.

      This can be used by the recipients of the software to assess
      their exposure to license compliance and security vulnerability issues.

      See the :wikipedia:`Software Supply Chain <Software_supply_chain>`
      article on Wikipedia for more details.

      The OpenEmbedded Build System can generate such documentation for your
      project, in :term:`SPDX` format, based on all the metadata it used to
      build the software images. See the ":ref:`dev-manual/sbom:creating
      a software bill of materials`" section of the Development Tasks manual.

   :term:`Source Directory`
     This term refers to the directory structure
     created as a result of creating a local copy of the ``poky`` Git
     repository ``git://git.yoctoproject.org/poky`` or expanding a
     released ``poky`` tarball.

     .. note::

        Creating a local copy of the
        poky
        Git repository is the recommended method for setting up your
        Source Directory.

     Sometimes you might hear the term "poky directory" used to refer to
     this directory structure.

     .. note::

        The OpenEmbedded build system does not support file or directory
        names that contain spaces. Be sure that the Source Directory you
        use does not contain these types of names.

     The Source Directory contains BitBake, Documentation, Metadata and
     other files that all support the Yocto Project. Consequently, you
     must have the Source Directory in place on your development system in
     order to do any development using the Yocto Project.

     When you create a local copy of the Git repository, you can name the
     repository anything you like. Throughout much of the documentation,
     "poky" is used as the name of the top-level folder of the local copy
     of the poky Git repository. So, for example, cloning the ``poky`` Git
     repository results in a local Git repository whose top-level folder
     is also named "poky".

     While it is not recommended that you use tarball extraction to set up
     the Source Directory, if you do, the top-level directory name of the
     Source Directory is derived from the Yocto Project release tarball.
     For example, downloading and unpacking poky tarballs from
     :yocto_dl:`/releases/yocto/&DISTRO_REL_LATEST_TAG;/`
     results in a Source Directory whose root folder is named poky.


     It is important to understand the differences between the Source
     Directory created by unpacking a released tarball as compared to
     cloning ``git://git.yoctoproject.org/poky``. When you unpack a
     tarball, you have an exact copy of the files based on the time of
     release --- a fixed release point. Any changes you make to your local
     files in the Source Directory are on top of the release and will
     remain local only. On the other hand, when you clone the ``poky`` Git
     repository, you have an active development repository with access to
     the upstream repository's branches and tags. In this case, any local
     changes you make to the local Source Directory can be later applied
     to active development branches of the upstream ``poky`` Git
     repository.

     For more information on concepts related to Git repositories,
     branches, and tags, see the
     ":ref:`overview-manual/development-environment:repositories, tags, and branches`"
     section in the Yocto Project Overview and Concepts Manual.

   :term:`SPDX`
      This term means *Software Package Data Exchange*, and is used as an open
      standard for providing a *Software Bill of Materials* (:term:`SBOM`).
      This standard is developed through a `Linux Foundation project
      <https://spdx.dev/>`__ and is used by the OpenEmbedded Build System to
      provide an :term:`SBOM` associated to each software image.

      For details, see Wikipedia's :wikipedia:`SPDX page <Software_Package_Data_Exchange>`
      and the ":ref:`dev-manual/sbom:creating a software bill of materials`"
      section of the Development Tasks manual.

   :term:`Sysroot`
      When cross-compiling, the target file system may be differently laid
      out and contain different things compared to the host system. The concept
      of a *sysroot* is directory which looks like the target filesystem and
      can be used to cross-compile against.

      In the context of cross-compiling toolchains, a *sysroot*
      typically contains C library and kernel headers, plus the
      compiled binaries for the C library. A *multilib toolchain*
      can contain multiple variants of the C library binaries,
      each compiled for a target instruction set (such as ``armv5``,
      ``armv7`` and ``armv8``), and possibly optimized for a specific CPU core.

      In the more specific context of the OpenEmbedded build System and
      of the Yocto Project, each recipe has two sysroots:

      -  A *target sysroot* contains all the **target** libraries and headers
         needed to build the recipe.

      -  A *native sysroot* contains all the **host** files and executables
         needed to build the recipe.

      See the :term:`SYSROOT_* <SYSROOT_DESTDIR>` variables controlling
      how sysroots are created and stored.

   :term:`Task`
      A per-recipe unit of execution for BitBake (e.g.
      :ref:`ref-tasks-compile`,
      :ref:`ref-tasks-fetch`,
      :ref:`ref-tasks-patch`, and so forth).
      One of the major benefits of the build system is that, since each
      recipe will typically spawn the execution of numerous tasks,
      it is entirely possible that many tasks can execute in parallel,
      either tasks from separate recipes or independent tasks within
      the same recipe, potentially up to the parallelism of your
      build system.

   :term:`Toaster`
      A web interface to the Yocto Project's :term:`OpenEmbedded Build System`.
      The interface enables you to
      configure and run your builds. Information about builds is collected
      and stored in a database. For information on Toaster, see the
      :doc:`/toaster-manual/index`.

   :term:`Upstream`
      A reference to source code or repositories that are not
      local to the development system but located in a remote area that is
      controlled by the maintainer of the source code. For example, in
      order for a developer to work on a particular piece of code, they
      need to first get a copy of it from an "upstream" source.
