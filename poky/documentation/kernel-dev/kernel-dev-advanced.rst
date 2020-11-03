.. SPDX-License-Identifier: CC-BY-2.0-UK

*******************************************************
Working with Advanced Metadata (``yocto-kernel-cache``)
*******************************************************

.. _kernel-dev-advanced-overview:

Overview
========

In addition to supporting configuration fragments and patches, the Yocto
Project kernel tools also support rich
:term:`Metadata` that you can use to define
complex policies and Board Support Package (BSP) support. The purpose of
the Metadata and the tools that manage it is to help you manage the
complexity of the configuration and sources used to support multiple
BSPs and Linux kernel types.

Kernel Metadata exists in many places. One area in the
:ref:`overview-manual/overview-manual-development-environment:yocto project source repositories`
is the ``yocto-kernel-cache`` Git repository. You can find this repository
grouped under the "Yocto Linux Kernel" heading in the
:yocto_git:`Yocto Project Source Repositories <>`.

Kernel development tools ("kern-tools") exist also in the Yocto Project
Source Repositories under the "Yocto Linux Kernel" heading in the
``yocto-kernel-tools`` Git repository. The recipe that builds these
tools is ``meta/recipes-kernel/kern-tools/kern-tools-native_git.bb`` in
the :term:`Source Directory` (e.g.
``poky``).

Using Kernel Metadata in a Recipe
=================================

As mentioned in the introduction, the Yocto Project contains kernel
Metadata, which is located in the ``yocto-kernel-cache`` Git repository.
This Metadata defines Board Support Packages (BSPs) that correspond to
definitions in linux-yocto recipes for corresponding BSPs. A BSP
consists of an aggregation of kernel policy and enabled
hardware-specific features. The BSP can be influenced from within the
linux-yocto recipe.

.. note::

   A Linux kernel recipe that contains kernel Metadata (e.g. inherits
   from the
   linux-yocto.inc
   file) is said to be a "linux-yocto style" recipe.

Every linux-yocto style recipe must define the
:term:`KMACHINE` variable. This
variable is typically set to the same value as the ``MACHINE`` variable,
which is used by :term:`BitBake`.
However, in some cases, the variable might instead refer to the
underlying platform of the ``MACHINE``.

Multiple BSPs can reuse the same ``KMACHINE`` name if they are built
using the same BSP description. Multiple Corei7-based BSPs could share
the same "intel-corei7-64" value for ``KMACHINE``. It is important to
realize that ``KMACHINE`` is just for kernel mapping, while ``MACHINE``
is the machine type within a BSP Layer. Even with this distinction,
however, these two variables can hold the same value. See the `BSP
Descriptions <#bsp-descriptions>`__ section for more information.

Every linux-yocto style recipe must also indicate the Linux kernel
source repository branch used to build the Linux kernel. The
:term:`KBRANCH` variable must be set
to indicate the branch.

.. note::

   You can use the
   KBRANCH
   value to define an alternate branch typically with a machine override
   as shown here from the
   meta-yocto-bsp
   layer:
   ::

           KBRANCH_edgerouter = "standard/edgerouter"


The linux-yocto style recipes can optionally define the following
variables:

  - :term:`KERNEL_FEATURES`

  - :term:`LINUX_KERNEL_TYPE`

:term:`LINUX_KERNEL_TYPE`
defines the kernel type to be used in assembling the configuration. If
you do not specify a ``LINUX_KERNEL_TYPE``, it defaults to "standard".
Together with ``KMACHINE``, ``LINUX_KERNEL_TYPE`` defines the search
arguments used by the kernel tools to find the appropriate description
within the kernel Metadata with which to build out the sources and
configuration. The linux-yocto recipes define "standard", "tiny", and
"preempt-rt" kernel types. See the "`Kernel Types <#kernel-types>`__"
section for more information on kernel types.

During the build, the kern-tools search for the BSP description file
that most closely matches the ``KMACHINE`` and ``LINUX_KERNEL_TYPE``
variables passed in from the recipe. The tools use the first BSP
description it finds that match both variables. If the tools cannot find
a match, they issue a warning.

The tools first search for the ``KMACHINE`` and then for the
``LINUX_KERNEL_TYPE``. If the tools cannot find a partial match, they
will use the sources from the ``KBRANCH`` and any configuration
specified in the :term:`SRC_URI`.

You can use the
:term:`KERNEL_FEATURES`
variable to include features (configuration fragments, patches, or both)
that are not already included by the ``KMACHINE`` and
``LINUX_KERNEL_TYPE`` variable combination. For example, to include a
feature specified as "features/netfilter/netfilter.scc", specify:
::

   KERNEL_FEATURES += "features/netfilter/netfilter.scc"

To include a
feature called "cfg/sound.scc" just for the ``qemux86`` machine,
specify:
::

   KERNEL_FEATURES_append_qemux86 = " cfg/sound.scc"

The value of
the entries in ``KERNEL_FEATURES`` are dependent on their location
within the kernel Metadata itself. The examples here are taken from the
``yocto-kernel-cache`` repository. Each branch of this repository
contains "features" and "cfg" subdirectories at the top-level. For more
information, see the "`Kernel Metadata
Syntax <#kernel-metadata-syntax>`__" section.

Kernel Metadata Syntax
======================

The kernel Metadata consists of three primary types of files: ``scc``
[1]_ description files, configuration fragments, and patches. The
``scc`` files define variables and include or otherwise reference any of
the three file types. The description files are used to aggregate all
types of kernel Metadata into what ultimately describes the sources and
the configuration required to build a Linux kernel tailored to a
specific machine.

The ``scc`` description files are used to define two fundamental types
of kernel Metadata:

-  Features

-  Board Support Packages (BSPs)

Features aggregate sources in the form of patches and configuration
fragments into a modular reusable unit. You can use features to
implement conceptually separate kernel Metadata descriptions such as
pure configuration fragments, simple patches, complex features, and
kernel types. `Kernel types <#kernel-types>`__ define general kernel
features and policy to be reused in the BSPs.

BSPs define hardware-specific features and aggregate them with kernel
types to form the final description of what will be assembled and built.

While the kernel Metadata syntax does not enforce any logical separation
of configuration fragments, patches, features or kernel types, best
practices dictate a logical separation of these types of Metadata. The
following Metadata file hierarchy is recommended:
::

   base/
      bsp/
      cfg/
      features/
      ktypes/
      patches/

The ``bsp`` directory contains the `BSP
descriptions <#bsp-descriptions>`__. The remaining directories all
contain "features". Separating ``bsp`` from the rest of the structure
aids conceptualizing intended usage.

Use these guidelines to help place your ``scc`` description files within
the structure:

-  If your file contains only configuration fragments, place the file in
   the ``cfg`` directory.

-  If your file contains only source-code fixes, place the file in the
   ``patches`` directory.

-  If your file encapsulates a major feature, often combining sources
   and configurations, place the file in ``features`` directory.

-  If your file aggregates non-hardware configuration and patches in
   order to define a base kernel policy or major kernel type to be
   reused across multiple BSPs, place the file in ``ktypes`` directory.

These distinctions can easily become blurred - especially as out-of-tree
features slowly merge upstream over time. Also, remember that how the
description files are placed is a purely logical organization and has no
impact on the functionality of the kernel Metadata. There is no impact
because all of ``cfg``, ``features``, ``patches``, and ``ktypes``,
contain "features" as far as the kernel tools are concerned.

Paths used in kernel Metadata files are relative to base, which is
either
:term:`FILESEXTRAPATHS` if
you are creating Metadata in `recipe-space <#recipe-space-metadata>`__,
or the top level of
:yocto_git:`yocto-kernel-cache </cgit/cgit.cgi/yocto-kernel-cache/tree/>`
if you are creating `Metadata outside of the
recipe-space <#metadata-outside-the-recipe-space>`__.

.. [1]
   ``scc`` stands for Series Configuration Control, but the naming has
   less significance in the current implementation of the tooling than
   it had in the past. Consider ``scc`` files to be description files.

Configuration
-------------

The simplest unit of kernel Metadata is the configuration-only feature.
This feature consists of one or more Linux kernel configuration
parameters in a configuration fragment file (``.cfg``) and a ``.scc``
file that describes the fragment.

As an example, consider the Symmetric Multi-Processing (SMP) fragment
used with the ``linux-yocto-4.12`` kernel as defined outside of the
recipe space (i.e. ``yocto-kernel-cache``). This Metadata consists of
two files: ``smp.scc`` and ``smp.cfg``. You can find these files in the
``cfg`` directory of the ``yocto-4.12`` branch in the
``yocto-kernel-cache`` Git repository:
::

   cfg/smp.scc:
      define KFEATURE_DESCRIPTION "Enable SMP for 32 bit builds"
      define KFEATURE_COMPATIBILITY all

      kconf hardware smp.cfg

   cfg/smp.cfg:
      CONFIG_SMP=y
      CONFIG_SCHED_SMT=y
      # Increase default NR_CPUS from 8 to 64 so that platform with
      # more than 8 processors can be all activated at boot time
      CONFIG_NR_CPUS=64
      # The following is needed when setting NR_CPUS to something
      # greater than 8 on x86 architectures, it should be automatically
      # disregarded by Kconfig when using a different arch
      CONFIG_X86_BIGSMP=y

You can find general information on configuration
fragment files in the "`Creating Configuration
Fragments <#creating-config-fragments>`__" section.

Within the ``smp.scc`` file, the
:term:`KFEATURE_DESCRIPTION`
statement provides a short description of the fragment. Higher level
kernel tools use this description.

Also within the ``smp.scc`` file, the ``kconf`` command includes the
actual configuration fragment in an ``.scc`` file, and the "hardware"
keyword identifies the fragment as being hardware enabling, as opposed
to general policy, which would use the "non-hardware" keyword. The
distinction is made for the benefit of the configuration validation
tools, which warn you if a hardware fragment overrides a policy set by a
non-hardware fragment.

.. note::

   The description file can include multiple
   kconf
   statements, one per fragment.

As described in the "`Validating
Configuration <#validating-configuration>`__" section, you can use the
following BitBake command to audit your configuration:
::

   $ bitbake linux-yocto -c kernel_configcheck -f

Patches
-------

Patch descriptions are very similar to configuration fragment
descriptions, which are described in the previous section. However,
instead of a ``.cfg`` file, these descriptions work with source patches
(i.e. ``.patch`` files).

A typical patch includes a description file and the patch itself. As an
example, consider the build patches used with the ``linux-yocto-4.12``
kernel as defined outside of the recipe space (i.e.
``yocto-kernel-cache``). This Metadata consists of several files:
``build.scc`` and a set of ``*.patch`` files. You can find these files
in the ``patches/build`` directory of the ``yocto-4.12`` branch in the
``yocto-kernel-cache`` Git repository.

The following listings show the ``build.scc`` file and part of the
``modpost-mask-trivial-warnings.patch`` file:
::

   patches/build/build.scc:
      patch arm-serialize-build-targets.patch
      patch powerpc-serialize-image-targets.patch
      patch kbuild-exclude-meta-directory-from-distclean-processi.patch

      # applied by kgit
      # patch kbuild-add-meta-files-to-the-ignore-li.patch

      patch modpost-mask-trivial-warnings.patch
      patch menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch

   patches/build/modpost-mask-trivial-warnings.patch:
      From bd48931bc142bdd104668f3a062a1f22600aae61 Mon Sep 17 00:00:00 2001
      From: Paul Gortmaker <paul.gortmaker@windriver.com>
      Date: Sun, 25 Jan 2009 17:58:09 -0500
      Subject: [PATCH] modpost: mask trivial warnings

      Newer HOSTCC will complain about various stdio fcns because
                        .
                        .
                        .
 	        char *dump_write = NULL, *files_source = NULL;
 	        int opt;
      --
      2.10.1

      generated by cgit v0.10.2 at 2017-09-28 15:23:23 (GMT)

The description file can
include multiple patch statements where each statement handles a single
patch. In the example ``build.scc`` file, five patch statements exist
for the five patches in the directory.

You can create a typical ``.patch`` file using ``diff -Nurp`` or
``git format-patch`` commands. For information on how to create patches,
see the "`Using ``devtool`` to Patch the
Kernel <#using-devtool-to-patch-the-kernel>`__" and "`Using Traditional
Kernel Development to Patch the
Kernel <#using-traditional-kernel-development-to-patch-the-kernel>`__"
sections.

Features
--------

Features are complex kernel Metadata types that consist of configuration
fragments, patches, and possibly other feature description files. As an
example, consider the following generic listing:
::

   features/myfeature.scc
      define KFEATURE_DESCRIPTION "Enable myfeature"

      patch 0001-myfeature-core.patch
      patch 0002-myfeature-interface.patch

      include cfg/myfeature_dependency.scc
      kconf non-hardware myfeature.cfg

This example shows how the ``patch`` and ``kconf`` commands are used as well
as how an additional feature description file is included with the
``include`` command.

Typically, features are less granular than configuration fragments and
are more likely than configuration fragments and patches to be the types
of things you want to specify in the ``KERNEL_FEATURES`` variable of the
Linux kernel recipe. See the "`Using Kernel Metadata in a
Recipe <#using-kernel-metadata-in-a-recipe>`__" section earlier in the
manual.

Kernel Types
------------

A kernel type defines a high-level kernel policy by aggregating
non-hardware configuration fragments with patches you want to use when
building a Linux kernel of a specific type (e.g. a real-time kernel).
Syntactically, kernel types are no different than features as described
in the "`Features <#features>`__" section. The
:term:`LINUX_KERNEL_TYPE`
variable in the kernel recipe selects the kernel type. For example, in
the ``linux-yocto_4.12.bb`` kernel recipe found in
``poky/meta/recipes-kernel/linux``, a
:ref:`require <bitbake:require-inclusion>` directive
includes the ``poky/meta/recipes-kernel/linux/linux-yocto.inc`` file,
which has the following statement that defines the default kernel type:
::

   LINUX_KERNEL_TYPE ??= "standard"

Another example would be the real-time kernel (i.e.
``linux-yocto-rt_4.12.bb``). This kernel recipe directly sets the kernel
type as follows:
::

   LINUX_KERNEL_TYPE = "preempt-rt"

.. note::

   You can find kernel recipes in the
   meta/recipes-kernel/linux
   directory of the
   Source Directory
   (e.g.
   poky/meta/recipes-kernel/linux/linux-yocto_4.12.bb
   ). See the "
   Using Kernel Metadata in a Recipe
   " section for more information.

Three kernel types ("standard", "tiny", and "preempt-rt") are supported
for Linux Yocto kernels:

-  "standard": Includes the generic Linux kernel policy of the Yocto
   Project linux-yocto kernel recipes. This policy includes, among other
   things, which file systems, networking options, core kernel features,
   and debugging and tracing options are supported.

-  "preempt-rt": Applies the ``PREEMPT_RT`` patches and the
   configuration options required to build a real-time Linux kernel.
   This kernel type inherits from the "standard" kernel type.

-  "tiny": Defines a bare minimum configuration meant to serve as a base
   for very small Linux kernels. The "tiny" kernel type is independent
   from the "standard" configuration. Although the "tiny" kernel type
   does not currently include any source changes, it might in the
   future.

For any given kernel type, the Metadata is defined by the ``.scc`` (e.g.
``standard.scc``). Here is a partial listing for the ``standard.scc``
file, which is found in the ``ktypes/standard`` directory of the
``yocto-kernel-cache`` Git repository:
::

   # Include this kernel type fragment to get the standard features and
   # configuration values.

   # Note: if only the features are desired, but not the configuration
   #       then this should be included as:
   #             include ktypes/standard/standard.scc nocfg
   #       if no chained configuration is desired, include it as:
   #             include ktypes/standard/standard.scc nocfg inherit



   include ktypes/base/base.scc
   branch standard

   kconf non-hardware standard.cfg

   include features/kgdb/kgdb.scc
              .
              .
              .

   include cfg/net/ip6_nf.scc
   include cfg/net/bridge.scc

   include cfg/systemd.scc

   include features/rfkill/rfkill.scc

As with any ``.scc`` file, a kernel type definition can aggregate other
``.scc`` files with ``include`` commands. These definitions can also
directly pull in configuration fragments and patches with the ``kconf``
and ``patch`` commands, respectively.

.. note::

   It is not strictly necessary to create a kernel type
   .scc
   file. The Board Support Package (BSP) file can implicitly define the
   kernel type using a
   define
   KTYPE
   myktype
   line. See the "
   BSP Descriptions
   " section for more information.

BSP Descriptions
----------------

BSP descriptions (i.e. ``*.scc`` files) combine kernel types with
hardware-specific features. The hardware-specific Metadata is typically
defined independently in the BSP layer, and then aggregated with each
supported kernel type.

.. note::

   For BSPs supported by the Yocto Project, the BSP description files
   are located in the
   bsp
   directory of the
   yocto-kernel-cache
   repository organized under the "Yocto Linux Kernel" heading in the
   Yocto Project Source Repositories
   .

This section overviews the BSP description structure, the aggregation
concepts, and presents a detailed example using a BSP supported by the
Yocto Project (i.e. BeagleBone Board). For complete information on BSP
layer file hierarchy, see the :doc:`../bsp-guide/bsp-guide`.

.. _bsp-description-file-overview:

Description Overview
~~~~~~~~~~~~~~~~~~~~

For simplicity, consider the following root BSP layer description files
for the BeagleBone board. These files employ both a structure and naming
convention for consistency. The naming convention for the file is as
follows:
::

   bsp_root_name-kernel_type.scc

Here are some example root layer
BSP filenames for the BeagleBone Board BSP, which is supported by the
Yocto Project:
::

   beaglebone-standard.scc
   beaglebone-preempt-rt.scc

Each file uses the root name (i.e "beaglebone") BSP name followed by the
kernel type.

Examine the ``beaglebone-standard.scc`` file:
::

   define KMACHINE beaglebone
   define KTYPE standard
   define KARCH arm

   include ktypes/standard/standard.scc
   branch beaglebone

   include beaglebone.scc

   # default policy for standard kernels
   include features/latencytop/latencytop.scc
   include features/profiling/profiling.scc

Every top-level BSP description file
should define the :term:`KMACHINE`,
:term:`KTYPE`, and
:term:`KARCH` variables. These
variables allow the OpenEmbedded build system to identify the
description as meeting the criteria set by the recipe being built. This
example supports the "beaglebone" machine for the "standard" kernel and
the "arm" architecture.

Be aware that a hard link between the ``KTYPE`` variable and a kernel
type description file does not exist. Thus, if you do not have the
kernel type defined in your kernel Metadata as it is here, you only need
to ensure that the
:term:`LINUX_KERNEL_TYPE`
variable in the kernel recipe and the ``KTYPE`` variable in the BSP
description file match.

To separate your kernel policy from your hardware configuration, you
include a kernel type (``ktype``), such as "standard". In the previous
example, this is done using the following:
::

   include ktypes/standard/standard.scc

This file aggregates all the configuration
fragments, patches, and features that make up your standard kernel
policy. See the "`Kernel Types <#kernel-types>`__" section for more
information.

To aggregate common configurations and features specific to the kernel
for mybsp, use the following:
::

   include mybsp.scc

You can see that in the BeagleBone example with the following:
::

   include beaglebone.scc

For information on how to break a complete ``.config`` file into the various
configuration fragments, see the "`Creating Configuration
Fragments <#creating-config-fragments>`__" section.

Finally, if you have any configurations specific to the hardware that
are not in a ``*.scc`` file, you can include them as follows:
::

   kconf hardware mybsp-extra.cfg

The BeagleBone example does not include these
types of configurations. However, the Malta 32-bit board does
("mti-malta32"). Here is the ``mti-malta32-le-standard.scc`` file:
::

   define KMACHINE mti-malta32-le
   define KMACHINE qemumipsel
   define KTYPE standard
   define KARCH mips

   include ktypes/standard/standard.scc
   branch mti-malta32

   include mti-malta32.scc
   kconf hardware mti-malta32-le.cfg

.. _bsp-description-file-example-minnow:

Example
~~~~~~~

Many real-world examples are more complex. Like any other ``.scc`` file,
BSP descriptions can aggregate features. Consider the Minnow BSP
definition given the ``linux-yocto-4.4`` branch of the
``yocto-kernel-cache`` (i.e.
``yocto-kernel-cache/bsp/minnow/minnow.scc``):

.. note::

   Although the Minnow Board BSP is unused, the Metadata remains and is
   being used here just as an example.

::

   include cfg/x86.scc
   include features/eg20t/eg20t.scc
   include cfg/dmaengine.scc
   include features/power/intel.scc
   include cfg/efi.scc
   include features/usb/ehci-hcd.scc
   include features/usb/ohci-hcd.scc
   include features/usb/usb-gadgets.scc
   include features/usb/touchscreen-composite.scc
   include cfg/timer/hpet.scc
   include features/leds/leds.scc
   include features/spi/spidev.scc
   include features/i2c/i2cdev.scc
   include features/mei/mei-txe.scc

   # Earlyprintk and port debug requires 8250
   kconf hardware cfg/8250.cfg

   kconf hardware minnow.cfg
   kconf hardware minnow-dev.cfg

The ``minnow.scc`` description file includes a hardware configuration
fragment (``minnow.cfg``) specific to the Minnow BSP as well as several
more general configuration fragments and features enabling hardware
found on the machine. This ``minnow.scc`` description file is then
included in each of the three "minnow" description files for the
supported kernel types (i.e. "standard", "preempt-rt", and "tiny").
Consider the "minnow" description for the "standard" kernel type (i.e.
``minnow-standard.scc``:
::

   define KMACHINE minnow
   define KTYPE standard
   define KARCH i386

   include ktypes/standard

   include minnow.scc

   # Extra minnow configs above the minimal defined in minnow.scc
   include cfg/efi-ext.scc
   include features/media/media-all.scc
   include features/sound/snd_hda_intel.scc

   # The following should really be in standard.scc
   # USB live-image support
   include cfg/usb-mass-storage.scc
   include cfg/boot-live.scc

   # Basic profiling
   include features/latencytop/latencytop.scc
   include features/profiling/profiling.scc

   # Requested drivers that don't have an existing scc
   kconf hardware minnow-drivers-extra.cfg

The ``include`` command midway through the file includes the ``minnow.scc`` description
that defines all enabled hardware for the BSP that is common to all
kernel types. Using this command significantly reduces duplication.

Now consider the "minnow" description for the "tiny" kernel type (i.e.
``minnow-tiny.scc``):
::

   define KMACHINE minnow
   define KTYPE tiny
   define KARCH i386

   include ktypes/tiny

   include minnow.scc

As you might expect,
the "tiny" description includes quite a bit less. In fact, it includes
only the minimal policy defined by the "tiny" kernel type and the
hardware-specific configuration required for booting the machine along
with the most basic functionality of the system as defined in the base
"minnow" description file.

Notice again the three critical variables:
:term:`KMACHINE`,
:term:`KTYPE`, and
:term:`KARCH`. Of these variables, only
``KTYPE`` has changed to specify the "tiny" kernel type.

Kernel Metadata Location
========================

Kernel Metadata always exists outside of the kernel tree either defined
in a kernel recipe (recipe-space) or outside of the recipe. Where you
choose to define the Metadata depends on what you want to do and how you
intend to work. Regardless of where you define the kernel Metadata, the
syntax used applies equally.

If you are unfamiliar with the Linux kernel and only wish to apply a
configuration and possibly a couple of patches provided to you by
others, the recipe-space method is recommended. This method is also a
good approach if you are working with Linux kernel sources you do not
control or if you just do not want to maintain a Linux kernel Git
repository on your own. For partial information on how you can define
kernel Metadata in the recipe-space, see the "`Modifying an Existing
Recipe <#modifying-an-existing-recipe>`__" section.

Conversely, if you are actively developing a kernel and are already
maintaining a Linux kernel Git repository of your own, you might find it
more convenient to work with kernel Metadata kept outside the
recipe-space. Working with Metadata in this area can make iterative
development of the Linux kernel more efficient outside of the BitBake
environment.

Recipe-Space Metadata
---------------------

When stored in recipe-space, the kernel Metadata files reside in a
directory hierarchy below
:term:`FILESEXTRAPATHS`. For
a linux-yocto recipe or for a Linux kernel recipe derived by copying and
modifying
``oe-core/meta-skeleton/recipes-kernel/linux/linux-yocto-custom.bb`` to
a recipe in your layer, ``FILESEXTRAPATHS`` is typically set to
``${``\ :term:`THISDIR`\ ``}/${``\ :term:`PN`\ ``}``.
See the "`Modifying an Existing
Recipe <#modifying-an-existing-recipe>`__" section for more information.

Here is an example that shows a trivial tree of kernel Metadata stored
in recipe-space within a BSP layer:
::

   meta-my_bsp_layer/
   `-- recipes-kernel
       `-- linux
           `-- linux-yocto
               |-- bsp-standard.scc
               |-- bsp.cfg
               `-- standard.cfg

When the Metadata is stored in recipe-space, you must take steps to
ensure BitBake has the necessary information to decide what files to
fetch and when they need to be fetched again. It is only necessary to
specify the ``.scc`` files on the
:term:`SRC_URI`. BitBake parses them
and fetches any files referenced in the ``.scc`` files by the
``include``, ``patch``, or ``kconf`` commands. Because of this, it is
necessary to bump the recipe :term:`PR`
value when changing the content of files not explicitly listed in the
``SRC_URI``.

If the BSP description is in recipe space, you cannot simply list the
``*.scc`` in the ``SRC_URI`` statement. You need to use the following
form from your kernel append file:
::

   SRC_URI_append_myplatform = " \
       file://myplatform;type=kmeta;destsuffix=myplatform \
       "

Metadata Outside the Recipe-Space
---------------------------------

When stored outside of the recipe-space, the kernel Metadata files
reside in a separate repository. The OpenEmbedded build system adds the
Metadata to the build as a "type=kmeta" repository through the
:term:`SRC_URI` variable. As an
example, consider the following ``SRC_URI`` statement from the
``linux-yocto_4.12.bb`` kernel recipe:
::

   SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.12.git;name=machine;branch=${KBRANCH}; \
              git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.12;destsuffix=${KMETA}"


``${KMETA}``, in this context, is simply used to name the directory into
which the Git fetcher places the Metadata. This behavior is no different
than any multi-repository ``SRC_URI`` statement used in a recipe (e.g.
see the previous section).

You can keep kernel Metadata in a "kernel-cache", which is a directory
containing configuration fragments. As with any Metadata kept outside
the recipe-space, you simply need to use the ``SRC_URI`` statement with
the "type=kmeta" attribute. Doing so makes the kernel Metadata available
during the configuration phase.

If you modify the Metadata, you must not forget to update the ``SRCREV``
statements in the kernel's recipe. In particular, you need to update the
``SRCREV_meta`` variable to match the commit in the ``KMETA`` branch you
wish to use. Changing the data in these branches and not updating the
``SRCREV`` statements to match will cause the build to fetch an older
commit.

Organizing Your Source
======================

Many recipes based on the ``linux-yocto-custom.bb`` recipe use Linux
kernel sources that have only a single branch - "master". This type of
repository structure is fine for linear development supporting a single
machine and architecture. However, if you work with multiple boards and
architectures, a kernel source repository with multiple branches is more
efficient. For example, suppose you need a series of patches for one
board to boot. Sometimes, these patches are works-in-progress or
fundamentally wrong, yet they are still necessary for specific boards.
In these situations, you most likely do not want to include these
patches in every kernel you build (i.e. have the patches as part of the
lone "master" branch). It is situations like these that give rise to
multiple branches used within a Linux kernel sources Git repository.

Repository organization strategies exist that maximize source reuse,
remove redundancy, and logically order your changes. This section
presents strategies for the following cases:

-  Encapsulating patches in a feature description and only including the
   patches in the BSP descriptions of the applicable boards.

-  Creating a machine branch in your kernel source repository and
   applying the patches on that branch only.

-  Creating a feature branch in your kernel source repository and
   merging that branch into your BSP when needed.

The approach you take is entirely up to you and depends on what works
best for your development model.

Encapsulating Patches
---------------------

if you are reusing patches from an external tree and are not working on
the patches, you might find the encapsulated feature to be appropriate.
Given this scenario, you do not need to create any branches in the
source repository. Rather, you just take the static patches you need and
encapsulate them within a feature description. Once you have the feature
description, you simply include that into the BSP description as
described in the "`BSP Descriptions <#bsp-descriptions>`__" section.

You can find information on how to create patches and BSP descriptions
in the "`Patches <#patches>`__" and "`BSP
Descriptions <#bsp-descriptions>`__" sections.

Machine Branches
----------------

When you have multiple machines and architectures to support, or you are
actively working on board support, it is more efficient to create
branches in the repository based on individual machines. Having machine
branches allows common source to remain in the "master" branch with any
features specific to a machine stored in the appropriate machine branch.
This organization method frees you from continually reintegrating your
patches into a feature.

Once you have a new branch, you can set up your kernel Metadata to use
the branch a couple different ways. In the recipe, you can specify the
new branch as the ``KBRANCH`` to use for the board as follows:
::

   KBRANCH = "mynewbranch"

Another method is to use the ``branch`` command in the BSP
description:

   mybsp.scc:
      define KMACHINE mybsp
      define KTYPE standard
      define KARCH i386
      include standard.scc

      branch mynewbranch

      include mybsp-hw.scc

If you find yourself with numerous branches, you might consider using a
hierarchical branching system similar to what the Yocto Linux Kernel Git
repositories use:
::

   common/kernel_type/machine

If you had two kernel types, "standard" and "small" for instance, three
machines, and common as ``mydir``, the branches in your Git repository
might look like this:
:

   mydir/base
   mydir/standard/base
   mydir/standard/machine_a
   mydir/standard/machine_b
   mydir/standard/machine_c
   mydir/small/base
   mydir/small/machine_a

This organization can help clarify the branch relationships. In this
case, ``mydir/standard/machine_a`` includes everything in ``mydir/base``
and ``mydir/standard/base``. The "standard" and "small" branches add
sources specific to those kernel types that for whatever reason are not
appropriate for the other branches.

.. note::

   The "base" branches are an artifact of the way Git manages its data
   internally on the filesystem: Git will not allow you to use
   mydir/standard
   and
   mydir/standard/machine_a
   because it would have to create a file and a directory named
   "standard".

Feature Branches
----------------

When you are actively developing new features, it can be more efficient
to work with that feature as a branch, rather than as a set of patches
that have to be regularly updated. The Yocto Project Linux kernel tools
provide for this with the ``git merge`` command.

To merge a feature branch into a BSP, insert the ``git merge`` command
after any ``branch`` commands:
::

   mybsp.scc:
      define KMACHINE mybsp
      define KTYPE standard
      define KARCH i386
      include standard.scc

      branch mynewbranch
      git merge myfeature

      include mybsp-hw.scc

.. _scc-reference:

SCC Description File Reference
==============================

This section provides a brief reference for the commands you can use
within an SCC description file (``.scc``):

-  ``branch [ref]``: Creates a new branch relative to the current branch
   (typically ``${KTYPE}``) using the currently checked-out branch, or
   "ref" if specified.

-  ``define``: Defines variables, such as
   :term:`KMACHINE`,
   :term:`KTYPE`,
   :term:`KARCH`, and
   :term:`KFEATURE_DESCRIPTION`.

-  ``include SCC_FILE``: Includes an SCC file in the current file. The
   file is parsed as if you had inserted it inline.

-  ``kconf [hardware|non-hardware] CFG_FILE``: Queues a configuration
   fragment for merging into the final Linux ``.config`` file.

-  ``git merge GIT_BRANCH``: Merges the feature branch into the current
   branch.

-  ``patch PATCH_FILE``: Applies the patch to the current Git branch.


