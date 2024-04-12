.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Understanding and Creating Layers
*********************************

The OpenEmbedded build system supports organizing
:term:`Metadata` into multiple layers.
Layers allow you to isolate different types of customizations from each
other. For introductory information on the Yocto Project Layer Model,
see the
":ref:`overview-manual/yp-intro:the yocto project layer model`"
section in the Yocto Project Overview and Concepts Manual.

Creating Your Own Layer
=======================

.. note::

   It is very easy to create your own layers to use with the OpenEmbedded
   build system, as the Yocto Project ships with tools that speed up creating
   layers. This section describes the steps you perform by hand to create
   layers so that you can better understand them. For information about the
   layer-creation tools, see the
   ":ref:`bsp-guide/bsp:creating a new bsp layer using the \`\`bitbake-layers\`\` script`"
   section in the Yocto Project Board Support Package (BSP) Developer's
   Guide and the ":ref:`dev-manual/layers:creating a general layer using the \`\`bitbake-layers\`\` script`"
   section further down in this manual.

Follow these general steps to create your layer without using tools:

#. *Check Existing Layers:* Before creating a new layer, you should be
   sure someone has not already created a layer containing the Metadata
   you need. You can see the :oe_layerindex:`OpenEmbedded Metadata Index <>`
   for a list of layers from the OpenEmbedded community that can be used in
   the Yocto Project. You could find a layer that is identical or close
   to what you need.

#. *Create a Directory:* Create the directory for your layer. When you
   create the layer, be sure to create the directory in an area not
   associated with the Yocto Project :term:`Source Directory`
   (e.g. the cloned ``poky`` repository).

   While not strictly required, prepend the name of the directory with
   the string "meta-". For example::

      meta-mylayer
      meta-GUI_xyz
      meta-mymachine

   With rare exceptions, a layer's name follows this form::

      meta-root_name

   Following this layer naming convention can save
   you trouble later when tools, components, or variables "assume" your
   layer name begins with "meta-". A notable example is in configuration
   files as shown in the following step where layer names without the
   "meta-" string are appended to several variables used in the
   configuration.

#. *Create a Layer Configuration File:* Inside your new layer folder,
   you need to create a ``conf/layer.conf`` file. It is easiest to take
   an existing layer configuration file and copy that to your layer's
   ``conf`` directory and then modify the file as needed.

   The ``meta-yocto-bsp/conf/layer.conf`` file in the Yocto Project
   :yocto_git:`Source Repositories </poky/tree/meta-yocto-bsp/conf>`
   demonstrates the required syntax. For your layer, you need to replace
   "yoctobsp" with a unique identifier for your layer (e.g. "machinexyz"
   for a layer named "meta-machinexyz")::

      # We have a conf and classes directory, add to BBPATH
      BBPATH .= ":${LAYERDIR}"

      # We have recipes-* directories, add to BBFILES
      BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
                  ${LAYERDIR}/recipes-*/*/*.bbappend"

      BBFILE_COLLECTIONS += "yoctobsp"
      BBFILE_PATTERN_yoctobsp = "^${LAYERDIR}/"
      BBFILE_PRIORITY_yoctobsp = "5"
      LAYERVERSION_yoctobsp = "4"
      LAYERSERIES_COMPAT_yoctobsp = "dunfell"

   Here is an explanation of the layer configuration file:

   -  :term:`BBPATH`: Adds the layer's
      root directory to BitBake's search path. Through the use of the
      :term:`BBPATH` variable, BitBake locates class files (``.bbclass``),
      configuration files, and files that are included with ``include``
      and ``require`` statements. For these cases, BitBake uses the
      first file that matches the name found in :term:`BBPATH`. This is
      similar to the way the ``PATH`` variable is used for binaries. It
      is recommended, therefore, that you use unique class and
      configuration filenames in your custom layer.

   -  :term:`BBFILES`: Defines the
      location for all recipes in the layer.

   -  :term:`BBFILE_COLLECTIONS`:
      Establishes the current layer through a unique identifier that is
      used throughout the OpenEmbedded build system to refer to the
      layer. In this example, the identifier "yoctobsp" is the
      representation for the container layer named "meta-yocto-bsp".

   -  :term:`BBFILE_PATTERN`:
      Expands immediately during parsing to provide the directory of the
      layer.

   -  :term:`BBFILE_PRIORITY`:
      Establishes a priority to use for recipes in the layer when the
      OpenEmbedded build finds recipes of the same name in different
      layers.

   -  :term:`LAYERVERSION`:
      Establishes a version number for the layer. You can use this
      version number to specify this exact version of the layer as a
      dependency when using the
      :term:`LAYERDEPENDS`
      variable.

   -  :term:`LAYERDEPENDS`:
      Lists all layers on which this layer depends (if any).

   -  :term:`LAYERSERIES_COMPAT`:
      Lists the :yocto_wiki:`Yocto Project </Releases>`
      releases for which the current version is compatible. This
      variable is a good way to indicate if your particular layer is
      current.


   .. note::

      A layer does not have to contain only recipes ``.bb`` or append files
      ``.bbappend``. Generally, developers create layers using
      ``bitbake-layers create-layer``.
      See ":ref:`dev-manual/layers:creating a general layer using the \`\`bitbake-layers\`\` script`",
      explaining how the ``layer.conf`` file is created from a template located in
      ``meta/lib/bblayers/templates/layer.conf``.
      In fact, none of the variables set in ``layer.conf`` are mandatory,
      except when :term:`BBFILE_COLLECTIONS` is present. In this case
      :term:`LAYERSERIES_COMPAT` and :term:`BBFILE_PATTERN` have to be
      defined too.

#. *Add Content:* Depending on the type of layer, add the content. If
   the layer adds support for a machine, add the machine configuration
   in a ``conf/machine/`` file within the layer. If the layer adds
   distro policy, add the distro configuration in a ``conf/distro/``
   file within the layer. If the layer introduces new recipes, put the
   recipes you need in ``recipes-*`` subdirectories within the layer.

   .. note::

      For an explanation of layer hierarchy that is compliant with the
      Yocto Project, see the ":ref:`bsp-guide/bsp:example filesystem layout`"
      section in the Yocto Project Board Support Package (BSP) Developer's Guide.

#. *Optionally Test for Compatibility:* If you want permission to use
   the Yocto Project Compatibility logo with your layer or application
   that uses your layer, perform the steps to apply for compatibility.
   See the
   ":ref:`dev-manual/layers:making sure your layer is compatible with yocto project`"
   section for more information.

Following Best Practices When Creating Layers
=============================================

To create layers that are easier to maintain and that will not impact
builds for other machines, you should consider the information in the
following list:

-  *Avoid "Overlaying" Entire Recipes from Other Layers in Your
   Configuration:* In other words, do not copy an entire recipe into
   your layer and then modify it. Rather, use an append file
   (``.bbappend``) to override only those parts of the original recipe
   you need to modify.

-  *Avoid Duplicating Include Files:* Use append files (``.bbappend``)
   for each recipe that uses an include file. Or, if you are introducing
   a new recipe that requires the included file, use the path relative
   to the original layer directory to refer to the file. For example,
   use ``require recipes-core/``\ `package`\ ``/``\ `file`\ ``.inc`` instead
   of ``require`` `file`\ ``.inc``. If you're finding you have to overlay
   the include file, it could indicate a deficiency in the include file
   in the layer to which it originally belongs. If this is the case, you
   should try to address that deficiency instead of overlaying the
   include file. For example, you could address this by getting the
   maintainer of the include file to add a variable or variables to make
   it easy to override the parts needing to be overridden.

-  *Structure Your Layers:* Proper use of overrides within append files
   and placement of machine-specific files within your layer can ensure
   that a build is not using the wrong Metadata and negatively impacting
   a build for a different machine. Here are some examples:

   -  *Modify Variables to Support a Different Machine:* Suppose you
      have a layer named ``meta-one`` that adds support for building
      machine "one". To do so, you use an append file named
      ``base-files.bbappend`` and create a dependency on "foo" by
      altering the :term:`DEPENDS`
      variable::

         DEPENDS = "foo"

      The dependency is created during any
      build that includes the layer ``meta-one``. However, you might not
      want this dependency for all machines. For example, suppose you
      are building for machine "two" but your ``bblayers.conf`` file has
      the ``meta-one`` layer included. During the build, the
      ``base-files`` for machine "two" will also have the dependency on
      ``foo``.

      To make sure your changes apply only when building machine "one",
      use a machine override with the :term:`DEPENDS` statement::

         DEPENDS:one = "foo"

      You should follow the same strategy when using ``:append``
      and ``:prepend`` operations::

         DEPENDS:append:one = " foo"
         DEPENDS:prepend:one = "foo "

      As an actual example, here's a
      snippet from the generic kernel include file ``linux-yocto.inc``,
      wherein the kernel compile and link options are adjusted in the
      case of a subset of the supported architectures::

         DEPENDS:append:aarch64 = " libgcc"
         KERNEL_CC:append:aarch64 = " ${TOOLCHAIN_OPTIONS}"
         KERNEL_LD:append:aarch64 = " ${TOOLCHAIN_OPTIONS}"

         DEPENDS:append:nios2 = " libgcc"
         KERNEL_CC:append:nios2 = " ${TOOLCHAIN_OPTIONS}"
         KERNEL_LD:append:nios2 = " ${TOOLCHAIN_OPTIONS}"

         DEPENDS:append:arc = " libgcc"
         KERNEL_CC:append:arc = " ${TOOLCHAIN_OPTIONS}"
         KERNEL_LD:append:arc = " ${TOOLCHAIN_OPTIONS}"

         KERNEL_FEATURES:append:qemuall=" features/debug/printk.scc"

   -  *Place Machine-Specific Files in Machine-Specific Locations:* When
      you have a base recipe, such as ``base-files.bb``, that contains a
      :term:`SRC_URI` statement to a
      file, you can use an append file to cause the build to use your
      own version of the file. For example, an append file in your layer
      at ``meta-one/recipes-core/base-files/base-files.bbappend`` could
      extend :term:`FILESPATH` using :term:`FILESEXTRAPATHS` as follows::

         FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

      The build for machine "one" will pick up your machine-specific file as
      long as you have the file in
      ``meta-one/recipes-core/base-files/base-files/``. However, if you
      are building for a different machine and the ``bblayers.conf``
      file includes the ``meta-one`` layer and the location of your
      machine-specific file is the first location where that file is
      found according to :term:`FILESPATH`, builds for all machines will
      also use that machine-specific file.

      You can make sure that a machine-specific file is used for a
      particular machine by putting the file in a subdirectory specific
      to the machine. For example, rather than placing the file in
      ``meta-one/recipes-core/base-files/base-files/`` as shown above,
      put it in ``meta-one/recipes-core/base-files/base-files/one/``.
      Not only does this make sure the file is used only when building
      for machine "one", but the build process locates the file more
      quickly.

      In summary, you need to place all files referenced from
      :term:`SRC_URI` in a machine-specific subdirectory within the layer in
      order to restrict those files to machine-specific builds.

-  *Perform Steps to Apply for Yocto Project Compatibility:* If you want
   permission to use the Yocto Project Compatibility logo with your
   layer or application that uses your layer, perform the steps to apply
   for compatibility. See the
   ":ref:`dev-manual/layers:making sure your layer is compatible with yocto project`"
   section for more information.

-  *Follow the Layer Naming Convention:* Store custom layers in a Git
   repository that use the ``meta-layer_name`` format.

-  *Group Your Layers Locally:* Clone your repository alongside other
   cloned ``meta`` directories from the :term:`Source Directory`.

Making Sure Your Layer is Compatible With Yocto Project
=======================================================

When you create a layer used with the Yocto Project, it is advantageous
to make sure that the layer interacts well with existing Yocto Project
layers (i.e. the layer is compatible with the Yocto Project). Ensuring
compatibility makes the layer easy to be consumed by others in the Yocto
Project community and could allow you permission to use the Yocto
Project Compatible Logo.

.. note::

   Only Yocto Project member organizations are permitted to use the
   Yocto Project Compatible Logo. The logo is not available for general
   use. For information on how to become a Yocto Project member
   organization, see the :yocto_home:`Yocto Project Website <>`.

The Yocto Project Compatibility Program consists of a layer application
process that requests permission to use the Yocto Project Compatibility
Logo for your layer and application. The process consists of two parts:

#. Successfully passing a script (``yocto-check-layer``) that when run
   against your layer, tests it against constraints based on experiences
   of how layers have worked in the real world and where pitfalls have
   been found. Getting a "PASS" result from the script is required for
   successful compatibility registration.

#. Completion of an application acceptance form, which you can find at
   :yocto_home:`/compatible-registration/`.

To be granted permission to use the logo, you need to satisfy the
following:

-  Be able to check the box indicating that you got a "PASS" when
   running the script against your layer.

-  Answer "Yes" to the questions on the form or have an acceptable
   explanation for any questions answered "No".

-  Be a Yocto Project Member Organization.

The remainder of this section presents information on the registration
form and on the ``yocto-check-layer`` script.

Yocto Project Compatible Program Application
--------------------------------------------

Use the form to apply for your layer's approval. Upon successful
application, you can use the Yocto Project Compatibility Logo with your
layer and the application that uses your layer.

To access the form, use this link:
:yocto_home:`/compatible-registration`.
Follow the instructions on the form to complete your application.

The application consists of the following sections:

-  *Contact Information:* Provide your contact information as the fields
   require. Along with your information, provide the released versions
   of the Yocto Project for which your layer is compatible.

-  *Acceptance Criteria:* Provide "Yes" or "No" answers for each of the
   items in the checklist. There is space at the bottom of the form for
   any explanations for items for which you answered "No".

-  *Recommendations:* Provide answers for the questions regarding Linux
   kernel use and build success.

``yocto-check-layer`` Script
----------------------------

The ``yocto-check-layer`` script provides you a way to assess how
compatible your layer is with the Yocto Project. You should run this
script prior to using the form to apply for compatibility as described
in the previous section. You need to achieve a "PASS" result in order to
have your application form successfully processed.

The script divides tests into three areas: COMMON, BSP, and DISTRO. For
example, given a distribution layer (DISTRO), the layer must pass both
the COMMON and DISTRO related tests. Furthermore, if your layer is a BSP
layer, the layer must pass the COMMON and BSP set of tests.

To execute the script, enter the following commands from your build
directory::

   $ source oe-init-build-env
   $ yocto-check-layer your_layer_directory

Be sure to provide the actual directory for your
layer as part of the command.

Entering the command causes the script to determine the type of layer
and then to execute a set of specific tests against the layer. The
following list overviews the test:

-  ``common.test_readme``: Tests if a ``README`` file exists in the
   layer and the file is not empty.

-  ``common.test_parse``: Tests to make sure that BitBake can parse the
   files without error (i.e. ``bitbake -p``).

-  ``common.test_show_environment``: Tests that the global or per-recipe
   environment is in order without errors (i.e. ``bitbake -e``).

-  ``common.test_world``: Verifies that ``bitbake world`` works.

-  ``common.test_signatures``: Tests to be sure that BSP and DISTRO
   layers do not come with recipes that change signatures.

-  ``common.test_layerseries_compat``: Verifies layer compatibility is
   set properly.

-  ``bsp.test_bsp_defines_machines``: Tests if a BSP layer has machine
   configurations.

-  ``bsp.test_bsp_no_set_machine``: Tests to ensure a BSP layer does not
   set the machine when the layer is added.

-  ``bsp.test_machine_world``: Verifies that ``bitbake world`` works
   regardless of which machine is selected.

-  ``bsp.test_machine_signatures``: Verifies that building for a
   particular machine affects only the signature of tasks specific to
   that machine.

-  ``distro.test_distro_defines_distros``: Tests if a DISTRO layer has
   distro configurations.

-  ``distro.test_distro_no_set_distros``: Tests to ensure a DISTRO layer
   does not set the distribution when the layer is added.

Enabling Your Layer
===================

Before the OpenEmbedded build system can use your new layer, you need to
enable it. To enable your layer, simply add your layer's path to the
:term:`BBLAYERS` variable in your ``conf/bblayers.conf`` file, which is
found in the :term:`Build Directory`. The following example shows how to
enable your new ``meta-mylayer`` layer (note how your new layer exists
outside of the official ``poky`` repository which you would have checked
out earlier)::

   # POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
   # changes incompatibly
   POKY_BBLAYERS_CONF_VERSION = "2"
   BBPATH = "${TOPDIR}"
   BBFILES ?= ""
   BBLAYERS ?= " \
       /home/user/poky/meta \
       /home/user/poky/meta-poky \
       /home/user/poky/meta-yocto-bsp \
       /home/user/mystuff/meta-mylayer \
       "

BitBake parses each ``conf/layer.conf`` file from the top down as
specified in the :term:`BBLAYERS` variable within the ``conf/bblayers.conf``
file. During the processing of each ``conf/layer.conf`` file, BitBake
adds the recipes, classes and configurations contained within the
particular layer to the source directory.

Appending Other Layers Metadata With Your Layer
===============================================

A recipe that appends Metadata to another recipe is called a BitBake
append file. A BitBake append file uses the ``.bbappend`` file type
suffix, while the corresponding recipe to which Metadata is being
appended uses the ``.bb`` file type suffix.

You can use a ``.bbappend`` file in your layer to make additions or
changes to the content of another layer's recipe without having to copy
the other layer's recipe into your layer. Your ``.bbappend`` file
resides in your layer, while the main ``.bb`` recipe file to which you
are appending Metadata resides in a different layer.

Being able to append information to an existing recipe not only avoids
duplication, but also automatically applies recipe changes from a
different layer into your layer. If you were copying recipes, you would
have to manually merge changes as they occur.

When you create an append file, you must use the same root name as the
corresponding recipe file. For example, the append file
``someapp_3.1.bbappend`` must apply to ``someapp_3.1.bb``. This
means the original recipe and append filenames are version
number-specific. If the corresponding recipe is renamed to update to a
newer version, you must also rename and possibly update the
corresponding ``.bbappend`` as well. During the build process, BitBake
displays an error on starting if it detects a ``.bbappend`` file that
does not have a corresponding recipe with a matching name. See the
:term:`BB_DANGLINGAPPENDS_WARNONLY`
variable for information on how to handle this error.

Overlaying a File Using Your Layer
----------------------------------

As an example, consider the main formfactor recipe and a corresponding
formfactor append file both from the :term:`Source Directory`.
Here is the main
formfactor recipe, which is named ``formfactor_0.0.bb`` and located in
the "meta" layer at ``meta/recipes-bsp/formfactor``::

   SUMMARY = "Device formfactor information"
   DESCRIPTION = "A formfactor configuration file provides information about the \
   target hardware for which the image is being built and information that the \
   build system cannot obtain from other sources such as the kernel."
   SECTION = "base"
   LICENSE = "MIT"
   LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
   PR = "r45"

   SRC_URI = "file://config file://machconfig"
   S = "${WORKDIR}"

   PACKAGE_ARCH = "${MACHINE_ARCH}"
   INHIBIT_DEFAULT_DEPS = "1"

   do_install() {
           # Install file only if it has contents
           install -d ${D}${sysconfdir}/formfactor/
           install -m 0644 ${S}/config ${D}${sysconfdir}/formfactor/
           if [ -s "${S}/machconfig" ]; then
                   install -m 0644 ${S}/machconfig ${D}${sysconfdir}/formfactor/
           fi
   }

In the main recipe, note the :term:`SRC_URI`
variable, which tells the OpenEmbedded build system where to find files
during the build.

Here is the append file, which is named ``formfactor_0.0.bbappend``
and is from the Raspberry Pi BSP Layer named ``meta-raspberrypi``. The
file is in the layer at ``recipes-bsp/formfactor``::

   FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

By default, the build system uses the
:term:`FILESPATH` variable to
locate files. This append file extends the locations by setting the
:term:`FILESEXTRAPATHS`
variable. Setting this variable in the ``.bbappend`` file is the most
reliable and recommended method for adding directories to the search
path used by the build system to find files.

The statement in this example extends the directories to include
``${``\ :term:`THISDIR`\ ``}/${``\ :term:`PN`\ ``}``,
which resolves to a directory named ``formfactor`` in the same directory
in which the append file resides (i.e.
``meta-raspberrypi/recipes-bsp/formfactor``. This implies that you must
have the supporting directory structure set up that will contain any
files or patches you will be including from the layer.

Using the immediate expansion assignment operator ``:=`` is important
because of the reference to :term:`THISDIR`. The trailing colon character is
important as it ensures that items in the list remain colon-separated.

.. note::

   BitBake automatically defines the :term:`THISDIR` variable. You should
   never set this variable yourself. Using ":prepend" as part of the
   :term:`FILESEXTRAPATHS` ensures your path will be searched prior to other
   paths in the final list.

   Also, not all append files add extra files. Many append files simply
   allow to add build options (e.g. ``systemd``). For these cases, your
   append file would not even use the :term:`FILESEXTRAPATHS` statement.

The end result of this ``.bbappend`` file is that on a Raspberry Pi, where
``rpi`` will exist in the list of :term:`OVERRIDES`, the file
``meta-raspberrypi/recipes-bsp/formfactor/formfactor/rpi/machconfig`` will be
used during :ref:`ref-tasks-fetch` and the test for a non-zero file size in
:ref:`ref-tasks-install` will return true, and the file will be installed.

Installing Additional Files Using Your Layer
--------------------------------------------

As another example, consider the main ``xserver-xf86-config`` recipe and a
corresponding ``xserver-xf86-config`` append file both from the :term:`Source
Directory`.  Here is the main ``xserver-xf86-config`` recipe, which is named
``xserver-xf86-config_0.1.bb`` and located in the "meta" layer at
``meta/recipes-graphics/xorg-xserver``::

   SUMMARY = "X.Org X server configuration file"
   HOMEPAGE = "http://www.x.org"
   SECTION = "x11/base"
   LICENSE = "MIT"
   LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
   PR = "r33"

   SRC_URI = "file://xorg.conf"

   S = "${WORKDIR}"

   CONFFILES:${PN} = "${sysconfdir}/X11/xorg.conf"

   PACKAGE_ARCH = "${MACHINE_ARCH}"
   ALLOW_EMPTY:${PN} = "1"

   do_install () {
        if test -s ${WORKDIR}/xorg.conf; then
                install -d ${D}/${sysconfdir}/X11
                install -m 0644 ${WORKDIR}/xorg.conf ${D}/${sysconfdir}/X11/
        fi
   }

Here is the append file, which is named ``xserver-xf86-config_%.bbappend``
and is from the Raspberry Pi BSP Layer named ``meta-raspberrypi``. The
file is in the layer at ``recipes-graphics/xorg-xserver``::

   FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

   SRC_URI:append:rpi = " \
       file://xorg.conf.d/98-pitft.conf \
       file://xorg.conf.d/99-calibration.conf \
   "
   do_install:append:rpi () {
       PITFT="${@bb.utils.contains("MACHINE_FEATURES", "pitft", "1", "0", d)}"
       if [ "${PITFT}" = "1" ]; then
           install -d ${D}/${sysconfdir}/X11/xorg.conf.d/
           install -m 0644 ${WORKDIR}/xorg.conf.d/98-pitft.conf ${D}/${sysconfdir}/X11/xorg.conf.d/
           install -m 0644 ${WORKDIR}/xorg.conf.d/99-calibration.conf ${D}/${sysconfdir}/X11/xorg.conf.d/
       fi
   }

   FILES:${PN}:append:rpi = " ${sysconfdir}/X11/xorg.conf.d/*"

Building off of the previous example, we once again are setting the
:term:`FILESEXTRAPATHS` variable.  In this case we are also using
:term:`SRC_URI` to list additional source files to use when ``rpi`` is found in
the list of :term:`OVERRIDES`.  The :ref:`ref-tasks-install` task will then perform a
check for an additional :term:`MACHINE_FEATURES` that if set will cause these
additional files to be installed.  These additional files are listed in
:term:`FILES` so that they will be packaged.

Prioritizing Your Layer
=======================

Each layer is assigned a priority value. Priority values control which
layer takes precedence if there are recipe files with the same name in
multiple layers. For these cases, the recipe file from the layer with a
higher priority number takes precedence. Priority values also affect the
order in which multiple ``.bbappend`` files for the same recipe are
applied. You can either specify the priority manually, or allow the
build system to calculate it based on the layer's dependencies.

To specify the layer's priority manually, use the
:term:`BBFILE_PRIORITY`
variable and append the layer's root name::

   BBFILE_PRIORITY_mylayer = "1"

.. note::

   It is possible for a recipe with a lower version number
   :term:`PV` in a layer that has a higher
   priority to take precedence.

   Also, the layer priority does not currently affect the precedence
   order of ``.conf`` or ``.bbclass`` files. Future versions of BitBake
   might address this.

Managing Layers
===============

You can use the BitBake layer management tool ``bitbake-layers`` to
provide a view into the structure of recipes across a multi-layer
project. Being able to generate output that reports on configured layers
with their paths and priorities and on ``.bbappend`` files and their
applicable recipes can help to reveal potential problems.

For help on the BitBake layer management tool, use the following
command::

   $ bitbake-layers --help

The following list describes the available commands:

-  ``help:`` Displays general help or help on a specified command.

-  ``show-layers:`` Shows the current configured layers.

-  ``show-overlayed:`` Lists overlayed recipes. A recipe is overlayed
   when a recipe with the same name exists in another layer that has a
   higher layer priority.

-  ``show-recipes:`` Lists available recipes and the layers that
   provide them.

-  ``show-appends:`` Lists ``.bbappend`` files and the recipe files to
   which they apply.

-  ``show-cross-depends:`` Lists dependency relationships between
   recipes that cross layer boundaries.

-  ``add-layer:`` Adds a layer to ``bblayers.conf``.

-  ``remove-layer:`` Removes a layer from ``bblayers.conf``

-  ``flatten:`` Flattens the layer configuration into a separate
   output directory. Flattening your layer configuration builds a
   "flattened" directory that contains the contents of all layers, with
   any overlayed recipes removed and any ``.bbappend`` files appended to
   the corresponding recipes. You might have to perform some manual
   cleanup of the flattened layer as follows:

   -  Non-recipe files (such as patches) are overwritten. The flatten
      command shows a warning for these files.

   -  Anything beyond the normal layer setup has been added to the
      ``layer.conf`` file. Only the lowest priority layer's
      ``layer.conf`` is used.

   -  Overridden and appended items from ``.bbappend`` files need to be
      cleaned up. The contents of each ``.bbappend`` end up in the
      flattened recipe. However, if there are appended or changed
      variable values, you need to tidy these up yourself. Consider the
      following example. Here, the ``bitbake-layers`` command adds the
      line ``#### bbappended ...`` so that you know where the following
      lines originate::

         ...
         DESCRIPTION = "A useful utility"
         ...
         EXTRA_OECONF = "--enable-something"
         ...

         #### bbappended from meta-anotherlayer ####

         DESCRIPTION = "Customized utility"
         EXTRA_OECONF += "--enable-somethingelse"


      Ideally, you would tidy up these utilities as follows::

         ...
         DESCRIPTION = "Customized utility"
         ...
         EXTRA_OECONF = "--enable-something --enable-somethingelse"
         ...

-  ``layerindex-fetch``: Fetches a layer from a layer index, along
   with its dependent layers, and adds the layers to the
   ``conf/bblayers.conf`` file.

-  ``layerindex-show-depends``: Finds layer dependencies from the
   layer index.

-  ``save-build-conf``: Saves the currently active build configuration
   (``conf/local.conf``, ``conf/bblayers.conf``) as a template into a layer.
   This template can later be used for setting up builds via :term:`TEMPLATECONF`.
   For information about saving and using configuration templates, see
   ":ref:`dev-manual/custom-template-configuration-directory:creating a custom template configuration directory`".

-  ``create-layer``: Creates a basic layer.

-  ``create-layers-setup``: Writes out a configuration file and/or a script that
   can replicate the directory structure and revisions of the layers in a current build.
   For more information, see ":ref:`dev-manual/layers:saving and restoring the layers setup`".

Creating a General Layer Using the ``bitbake-layers`` Script
============================================================

The ``bitbake-layers`` script with the ``create-layer`` subcommand
simplifies creating a new general layer.

.. note::

   -  For information on BSP layers, see the ":ref:`bsp-guide/bsp:bsp layers`"
      section in the Yocto
      Project Board Specific (BSP) Developer's Guide.

   -  In order to use a layer with the OpenEmbedded build system, you
      need to add the layer to your ``bblayers.conf`` configuration
      file. See the ":ref:`dev-manual/layers:adding a layer using the \`\`bitbake-layers\`\` script`"
      section for more information.

The default mode of the script's operation with this subcommand is to
create a layer with the following:

-  A layer priority of 6.

-  A ``conf`` subdirectory that contains a ``layer.conf`` file.

-  A ``recipes-example`` subdirectory that contains a further
   subdirectory named ``example``, which contains an ``example.bb``
   recipe file.

-  A ``COPYING.MIT``, which is the license statement for the layer. The
   script assumes you want to use the MIT license, which is typical for
   most layers, for the contents of the layer itself.

-  A ``README`` file, which is a file describing the contents of your
   new layer.

In its simplest form, you can use the following command form to create a
layer. The command creates a layer whose name corresponds to
"your_layer_name" in the current directory::

   $ bitbake-layers create-layer your_layer_name

As an example, the following command creates a layer named ``meta-scottrif``
in your home directory::

   $ cd /usr/home
   $ bitbake-layers create-layer meta-scottrif
   NOTE: Starting bitbake server...
   Add your new layer with 'bitbake-layers add-layer meta-scottrif'

If you want to set the priority of the layer to other than the default
value of "6", you can either use the ``--priority`` option or you
can edit the
:term:`BBFILE_PRIORITY` value
in the ``conf/layer.conf`` after the script creates it. Furthermore, if
you want to give the example recipe file some name other than the
default, you can use the ``--example-recipe-name`` option.

The easiest way to see how the ``bitbake-layers create-layer`` command
works is to experiment with the script. You can also read the usage
information by entering the following::

   $ bitbake-layers create-layer --help
   NOTE: Starting bitbake server...
   usage: bitbake-layers create-layer [-h] [--priority PRIORITY]
                                      [--example-recipe-name EXAMPLERECIPE]
                                      layerdir

   Create a basic layer

   positional arguments:
     layerdir              Layer directory to create

   optional arguments:
     -h, --help            show this help message and exit
     --priority PRIORITY, -p PRIORITY
                           Layer directory to create
     --example-recipe-name EXAMPLERECIPE, -e EXAMPLERECIPE
                           Filename of the example recipe

Adding a Layer Using the ``bitbake-layers`` Script
==================================================

Once you create your general layer, you must add it to your
``bblayers.conf`` file. Adding the layer to this configuration file
makes the OpenEmbedded build system aware of your layer so that it can
search it for metadata.

Add your layer by using the ``bitbake-layers add-layer`` command::

   $ bitbake-layers add-layer your_layer_name

Here is an example that adds a
layer named ``meta-scottrif`` to the configuration file. Following the
command that adds the layer is another ``bitbake-layers`` command that
shows the layers that are in your ``bblayers.conf`` file::

   $ bitbake-layers add-layer meta-scottrif
   NOTE: Starting bitbake server...
   Parsing recipes: 100% |##########################################################| Time: 0:00:49
   Parsing of 1441 .bb files complete (0 cached, 1441 parsed). 2055 targets, 56 skipped, 0 masked, 0 errors.
   $ bitbake-layers show-layers
   NOTE: Starting bitbake server...
   layer                 path                                      priority
   ==========================================================================
   meta                  /home/scottrif/poky/meta                  5
   meta-poky             /home/scottrif/poky/meta-poky             5
   meta-yocto-bsp        /home/scottrif/poky/meta-yocto-bsp        5
   workspace             /home/scottrif/poky/build/workspace       99
   meta-scottrif         /home/scottrif/poky/build/meta-scottrif   6


Adding the layer to this file
enables the build system to locate the layer during the build.

.. note::

   During a build, the OpenEmbedded build system looks in the layers
   from the top of the list down to the bottom in that order.

Saving and restoring the layers setup
=====================================

Once you have a working build with the correct set of layers, it is beneficial
to capture the layer setup --- what they are, which repositories they come from
and which SCM revisions they're at --- into a configuration file, so that this
setup can be easily replicated later, perhaps on a different machine. Here's
how to do this::

   $ bitbake-layers create-layers-setup /srv/work/alex/meta-alex/
   NOTE: Starting bitbake server...
   NOTE: Created /srv/work/alex/meta-alex/setup-layers.json
   NOTE: Created /srv/work/alex/meta-alex/setup-layers

The tool needs a single argument which tells where to place the output, consisting
of a json formatted layer configuration, and a ``setup-layers`` script that can use that configuration
to restore the layers in a different location, or on a different host machine. The argument
can point to a custom layer (which is then deemed a "bootstrap" layer that needs to be
checked out first), or into a completely independent location.

The replication of the layers is performed by running the ``setup-layers`` script provided
above:

#. Clone the bootstrap layer or some other repository to obtain
   the json config and the setup script that can use it.

#. Run the script directly with no options::

      alex@Zen2:/srv/work/alex/my-build$ meta-alex/setup-layers
      Note: not checking out source meta-alex, use --force-bootstraplayer-checkout to override.

      Setting up source meta-intel, revision 15.0-hardknott-3.3-310-g0a96edae, branch master
      Running 'git init -q /srv/work/alex/my-build/meta-intel'
      Running 'git remote remove origin > /dev/null 2>&1; git remote add origin git://git.yoctoproject.org/meta-intel' in /srv/work/alex/my-build/meta-intel
      Running 'git fetch -q origin || true' in /srv/work/alex/my-build/meta-intel
      Running 'git checkout -q 0a96edae609a3f48befac36af82cf1eed6786b4a' in /srv/work/alex/my-build/meta-intel

      Setting up source poky, revision 4.1_M1-372-g55483d28f2, branch akanavin/setup-layers
      Running 'git init -q /srv/work/alex/my-build/poky'
      Running 'git remote remove origin > /dev/null 2>&1; git remote add origin git://git.yoctoproject.org/poky' in /srv/work/alex/my-build/poky
      Running 'git fetch -q origin || true' in /srv/work/alex/my-build/poky
      Running 'git remote remove poky-contrib > /dev/null 2>&1; git remote add poky-contrib ssh://git@push.yoctoproject.org/poky-contrib' in /srv/work/alex/my-build/poky
      Running 'git fetch -q poky-contrib || true' in /srv/work/alex/my-build/poky
      Running 'git checkout -q 11db0390b02acac1324e0f827beb0e2e3d0d1d63' in /srv/work/alex/my-build/poky

.. note::
   This will work to update an existing checkout as well.

.. note::
   The script is self-sufficient and requires only python3
   and git on the build machine.

.. note::
   Both the ``create-layers-setup`` and the ``setup-layers`` provided several additional options
   that customize their behavior - you are welcome to study them via ``--help`` command line parameter.

