.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

******************
Kernel Maintenance
******************

Tree Construction
=================

This section describes construction of the Yocto Project kernel source
repositories as accomplished by the Yocto Project team to create Yocto
Linux kernel repositories. These kernel repositories are found under the
heading "Yocto Linux Kernel" at :yocto_git:`/` and
are shipped as part of a Yocto Project release. The team creates these
repositories by compiling and executing the set of feature descriptions
for every BSP and feature in the product. Those feature descriptions
list all necessary patches, configurations, branches, tags, and feature
divisions found in a Yocto Linux kernel. Thus, the Yocto Project Linux
kernel repository (or tree) and accompanying Metadata in the
``yocto-kernel-cache`` are built.

The existence of these repositories allow you to access and clone a
particular Yocto Project Linux kernel repository and use it to build
images based on their configurations and features.

You can find the files used to describe all the valid features and BSPs
in the Yocto Project Linux kernel in any clone of the Yocto Project
Linux kernel source repository and ``yocto-kernel-cache`` Git trees. For
example, the following commands clone the Yocto Project baseline Linux
kernel that branches off ``linux.org`` version 4.12 and the
``yocto-kernel-cache``, which contains stores of kernel Metadata:
::

   $ git clone git://git.yoctoproject.org/linux-yocto-4.12
   $ git clone git://git.yoctoproject.org/linux-kernel-cache

For more information on
how to set up a local Git repository of the Yocto Project Linux kernel
files, see the
":ref:`kernel-dev/common:preparing the build host to work on the kernel`"
section.

Once you have cloned the kernel Git repository and the cache of Metadata
on your local machine, you can discover the branches that are available
in the repository using the following Git command:
::

   $ git branch -a

Checking out a branch allows you to work with a particular Yocto Linux
kernel. For example, the following commands check out the
"standard/beagleboard" branch of the Yocto Linux kernel repository and
the "yocto-4.12" branch of the ``yocto-kernel-cache`` repository:
::

   $ cd ~/linux-yocto-4.12
   $ git checkout -b my-kernel-4.12 remotes/origin/standard/beagleboard
   $ cd ~/linux-kernel-cache
   $ git checkout -b my-4.12-metadata remotes/origin/yocto-4.12

.. note::

   Branches in the ``yocto-kernel-cache`` repository correspond to Yocto Linux
   kernel versions (e.g. "yocto-4.12", "yocto-4.10", "yocto-4.9", and so forth).

Once you have checked out and switched to appropriate branches, you can
see a snapshot of all the kernel source files used to used to build that
particular Yocto Linux kernel for a particular board.

To see the features and configurations for a particular Yocto Linux
kernel, you need to examine the ``yocto-kernel-cache`` Git repository.
As mentioned, branches in the ``yocto-kernel-cache`` repository
correspond to Yocto Linux kernel versions (e.g. ``yocto-4.12``).
Branches contain descriptions in the form of ``.scc`` and ``.cfg``
files.

You should realize, however, that browsing your local
``yocto-kernel-cache`` repository for feature descriptions and patches
is not an effective way to determine what is in a particular kernel
branch. Instead, you should use Git directly to discover the changes in
a branch. Using Git is an efficient and flexible way to inspect changes
to the kernel.

.. note::

   Ground up reconstruction of the complete kernel tree is an action
   only taken by the Yocto Project team during an active development
   cycle. When you create a clone of the kernel Git repository, you are
   simply making it efficiently available for building and development.

The following steps describe what happens when the Yocto Project Team
constructs the Yocto Project kernel source Git repository (or tree)
found at :yocto_git:`/` given the introduction of a new
top-level kernel feature or BSP. The following actions effectively
provide the Metadata and create the tree that includes the new feature,
patch, or BSP:

1. *Pass Feature to the OpenEmbedded Build System:* A top-level kernel
   feature is passed to the kernel build subsystem. Normally, this
   feature is a BSP for a particular kernel type.

2. *Locate Feature:* The file that describes the top-level feature is
   located by searching these system directories:

   -  The in-tree kernel-cache directories, which are located in the
      :yocto_git:`yocto-kernel-cache </yocto-kernel-cache/tree/bsp>`
      repository organized under the "Yocto Linux Kernel" heading in the
      :yocto_git:`Yocto Project Source Repositories <>`.

   -  Areas pointed to by ``SRC_URI`` statements found in kernel recipes.

   For a typical build, the target of the search is a feature
   description in an ``.scc`` file whose name follows this format (e.g.
   ``beaglebone-standard.scc`` and ``beaglebone-preempt-rt.scc``):
   ::

      bsp_root_name-kernel_type.scc

3. *Expand Feature:* Once located, the feature description is either
   expanded into a simple script of actions, or into an existing
   equivalent script that is already part of the shipped kernel.

4. *Append Extra Features:* Extra features are appended to the top-level
   feature description. These features can come from the
   :term:`KERNEL_FEATURES`
   variable in recipes.

5. *Locate, Expand, and Append Each Feature:* Each extra feature is
   located, expanded and appended to the script as described in step
   three.

6. *Execute the Script:* The script is executed to produce files
   ``.scc`` and ``.cfg`` files in appropriate directories of the
   ``yocto-kernel-cache`` repository. These files are descriptions of
   all the branches, tags, patches and configurations that need to be
   applied to the base Git repository to completely create the source
   (build) branch for the new BSP or feature.

7. *Clone Base Repository:* The base repository is cloned, and the
   actions listed in the ``yocto-kernel-cache`` directories are applied
   to the tree.

8. *Perform Cleanup:* The Git repositories are left with the desired
   branches checked out and any required branching, patching and tagging
   has been performed.

The kernel tree and cache are ready for developer consumption to be
locally cloned, configured, and built into a Yocto Project kernel
specific to some target hardware.

.. note::

   -  The generated ``yocto-kernel-cache`` repository adds to the kernel
      as shipped with the Yocto Project release. Any add-ons and
      configuration data are applied to the end of an existing branch.
      The full repository generation that is found in the official Yocto
      Project kernel repositories at :yocto_git:`/` is the
      combination of all supported boards and configurations.

   -  The technique the Yocto Project team uses is flexible and allows
      for seamless blending of an immutable history with additional
      patches specific to a deployment. Any additions to the kernel
      become an integrated part of the branches.

   -  The full kernel tree that you see on :yocto_git:`/` is
      generated through repeating the above steps for all valid BSPs.
      The end result is a branched, clean history tree that makes up the
      kernel for a given release. You can see the script (``kgit-scc``)
      responsible for this in the
      :yocto_git:`yocto-kernel-tools </yocto-kernel-tools/tree/tools>`
      repository.

   -  The steps used to construct the full kernel tree are the same
      steps that BitBake uses when it builds a kernel image.

Build Strategy
==============

Once you have cloned a Yocto Linux kernel repository and the cache
repository (``yocto-kernel-cache``) onto your development system, you
can consider the compilation phase of kernel development, which is
building a kernel image. Some prerequisites exist that are validated by
the build process before compilation starts:

-  The :term:`SRC_URI` points to the
   kernel Git repository.

-  A BSP build branch with Metadata exists in the ``yocto-kernel-cache``
   repository. The branch is based on the Yocto Linux kernel version and
   has configurations and features grouped under the
   ``yocto-kernel-cache/bsp`` directory. For example, features and
   configurations for the BeagleBone Board assuming a
   ``linux-yocto_4.12`` kernel reside in the following area of the
   ``yocto-kernel-cache`` repository: yocto-kernel-cache/bsp/beaglebone

   .. note::

      In the previous example, the "yocto-4.12" branch is checked out in
      the ``yocto-kernel-cache`` repository.

The OpenEmbedded build system makes sure these conditions exist before
attempting compilation. Other means, however, do exist, such as
bootstrapping a BSP.

Before building a kernel, the build process verifies the tree and
configures the kernel by processing all of the configuration "fragments"
specified by feature descriptions in the ``.scc`` files. As the features
are compiled, associated kernel configuration fragments are noted and
recorded in the series of directories in their compilation order. The
fragments are migrated, pre-processed and passed to the Linux Kernel
Configuration subsystem (``lkc``) as raw input in the form of a
``.config`` file. The ``lkc`` uses its own internal dependency
constraints to do the final processing of that information and generates
the final ``.config`` file that is used during compilation.

Using the board's architecture and other relevant values from the
board's template, kernel compilation is started and a kernel image is
produced.

The other thing that you notice once you configure a kernel is that the
build process generates a build tree that is separate from your kernel's
local Git source repository tree. This build tree has a name that uses
the following form, where ``${MACHINE}`` is the metadata name of the
machine (BSP) and "kernel_type" is one of the Yocto Project supported
kernel types (e.g. "standard"):
::

   linux-${MACHINE}-kernel_type-build

The existing support in the ``kernel.org`` tree achieves this default
functionality.

This behavior means that all the generated files for a particular
machine or BSP are now in the build tree directory. The files include
the final ``.config`` file, all the ``.o`` files, the ``.a`` files, and
so forth. Since each machine or BSP has its own separate
:term:`Build Directory` in its own separate
branch of the Git repository, you can easily switch between different
builds.
