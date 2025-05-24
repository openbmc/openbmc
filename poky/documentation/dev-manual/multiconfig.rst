.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Building Images for Multiple Targets With Multiconfig
*****************************************************

You can use a single ``bitbake`` command to build multiple images or
packages for different targets where each image or package requires a
different configuration (multiple configuration builds). The builds, in
this scenario, are sometimes referred to as "multiconfigs", and this
section uses that term throughout.

This section describes how to set up for multiple configuration builds
and how to account for cross-build dependencies between the
multiconfigs.

Setting Up and Running a Multiple Configuration Build
=====================================================

To accomplish a multiple configuration build, you must define each
target's configuration separately using a parallel :term:`configuration file` in
the :term:`Build Directory` or configuration directory within a layer, and you
must follow a required file hierarchy. Additionally, you must enable the
multiple configuration builds in your ``local.conf`` file.

Follow these steps to set up and execute multiple configuration builds:

-  *Create Separate Configuration Files*: You need to create a single
   :term:`Configuration File` for each build target (each multiconfig).
   The configuration definitions are implementation dependent but often
   each configuration file will define the :term:`MACHINE` and the
   temporary directory (:term:`TMPDIR`) BitBake uses for the build.

   .. note::

      Whether the same temporary directory (:term:`TMPDIR`) can be shared will
      depend on what is similar and what is different between the
      configurations. Multiple :term:`MACHINE` targets can share the same
      :term:`TMPDIR` as long as the rest of the configuration is the same,
      multiple :term:`DISTRO` settings would need separate :term:`TMPDIR`
      directories.

      For example, consider a scenario with two different multiconfigs for the same
      :term:`MACHINE`: "qemux86" built for two distributions such as "poky" and
      "poky-lsb". In this case, you would need to use two different :term:`TMPDIR`.

      In the general case, using separate :term:`TMPDIR` for the different
      multiconfigs is strongly recommended.

   The location for these multiconfig configuration files is specific.
   They must reside in the current :term:`Build Directory` in a sub-directory of
   ``conf`` named ``multiconfig`` or within a :term:`Layer`'s ``conf`` directory
   under a directory named ``multiconfig``. Here is an example that defines
   two configuration files for the "x86" and "arm" multiconfigs:

   .. image:: figures/multiconfig_files.png
      :align: center
      :width: 50%

   The usual :term:`BBPATH` search path is used to locate multiconfig files in
   a similar way to other configuration files.

   Here is an example showing the minimal statements needed in a
   :term:`configuration file` named ``qemux86.conf`` for a ``qemux86`` target
   whose temporary build directory is ``tmp-qemux86``::

      MACHINE = "qemux86"
      TMPDIR .= "-${BB_CURRENT_MC}"

   BitBake will expand the :term:`BB_CURRENT_MC` variable to the value of the
   current multiconfig in use. We append this value to :term:`TMPDIR` so that
   any change on the definition of :term:`TMPDIR` will automatically affect the
   value of :term:`TMPDIR` for each multiconfig.

-  *Add the BitBake Multi-configuration Variable to the Local
   Configuration File*: Use the
   :term:`BBMULTICONFIG`
   variable in your ``conf/local.conf`` configuration file to specify
   each multiconfig. Continuing with the example from the previous
   figure, the :term:`BBMULTICONFIG` variable needs to enable two
   multiconfigs: "x86" and "arm" by specifying each configuration file::

      BBMULTICONFIG = "x86 arm"

   .. note::

      A "default" configuration already exists by definition. This
      configuration is named: "" (i.e. empty string) and is defined by
      the variables coming from your ``local.conf``
      file. Consequently, the previous example actually adds two
      additional configurations to your build: "arm" and "x86" along
      with "".

-  *Launch BitBake*: Use the following BitBake command form to launch
   the multiple configuration build::

      $ bitbake [mc:multiconfigname:]target [[[mc:multiconfigname:]target] ... ]

   For the example in this section, the following command applies::

      $ bitbake mc:x86:core-image-minimal mc:arm:core-image-sato mc::core-image-base

   The previous BitBake command builds several components:

   -  A ``core-image-minimal`` image that is configured through the ``x86.conf``
      configuration file

   -  A ``core-image-sato`` image that is configured through the ``arm.conf``
      configuration file

   -  A ``core-image-base`` that is configured through your ``local.conf``
      configuration file

.. note::

   Support for multiple configuration builds in the Yocto Project &DISTRO;
   (&DISTRO_NAME;) Release does not include Shared State (sstate)
   optimizations. Consequently, if a build uses the same object twice
   in, for example, two different :term:`TMPDIR`
   directories, the build either loads from an existing sstate cache for
   that build at the start or builds the object fresh.

Enabling Multiple Configuration Build Dependencies
==================================================

Sometimes dependencies can exist between targets (multiconfigs) in a
multiple configuration build. For example, suppose that in order to
build a ``core-image-sato`` image for an "x86" multiconfig, the root
filesystem of an "arm" multiconfig must exist. This dependency is
essentially that the
:ref:`ref-tasks-image` task in the
``core-image-sato`` recipe depends on the completion of the
:ref:`ref-tasks-rootfs` task of the
``core-image-minimal`` recipe.

To enable dependencies in a multiple configuration build, you must
declare the dependencies in the recipe using the following statement
form::

   task_or_package[mcdepends] = "mc:from_multiconfig:to_multiconfig:recipe_name:task_on_which_to_depend"

To better show how to use this statement, consider the example scenario
from the first paragraph of this section. The following statement needs
to be added to the recipe that builds the ``core-image-sato`` image::

   do_image[mcdepends] = "mc:x86:arm:core-image-minimal:do_rootfs"

In this example, the `from_multiconfig` is "x86". The `to_multiconfig` is "arm". The
task on which the :ref:`ref-tasks-image` task in the recipe depends is the
:ref:`ref-tasks-rootfs` task from the ``core-image-minimal`` recipe associated
with the "arm" multiconfig.

Once you set up this dependency, you can build the "x86" multiconfig
using a BitBake command as follows::

   $ bitbake mc:x86:core-image-sato

This command executes all the tasks needed to create the
``core-image-sato`` image for the "x86" multiconfig. Because of the
dependency, BitBake also executes through the :ref:`ref-tasks-rootfs` task for the
"arm" multiconfig build.

Having a recipe depend on the root filesystem of another build might not
seem that useful. Consider this change to the statement in the
``core-image-sato`` recipe::

   do_image[mcdepends] = "mc:x86:arm:core-image-minimal:do_image"

In this case, BitBake must
create the ``core-image-minimal`` image for the "arm" build since the
"x86" build depends on it.

Because "x86" and "arm" are enabled for multiple configuration builds
and have separate configuration files, BitBake places the artifacts for
each build in the respective temporary build directories (i.e.
:term:`TMPDIR`).

Suggested best practices
========================

-  :term:`TMPDIR` (other than the default set in bitbake.conf) is only set in
   ``local.conf`` by the user. This means that we should **not** manipulate
   :term:`TMPDIR` in any way within the Machine or Distro :term:`configuration
   file`.

-  A multiconfig should specify a :term:`TMPDIR`, and should specify it by
   appending the multiconfig name with :term:`BB_CURRENT_MC`.

-  Recipes that are used to transfer the output from a multiconfig build to
   another should use ``do_task[mcdepends]`` to trigger the build of the
   component, and then transfer the item to the current configuration in
   :ref:`ref-tasks-install` and :ref:`ref-tasks-deploy`, assuming the value of
   the deployed item based on :term:`TMPDIR`.

   The :ref:`ref-tasks-install` and :ref:`ref-tasks-deploy` tasks should look
   like this::

      do_install() {
          install -m 0644 ${TMPDIR}-<multiconfig>/tmp/deploy/images/<machine>/somefile ${D}/some/path
      }

      do_deploy() {
          install -m 0644 ${TMPDIR}-<multiconfig>/tmp/deploy/images/<machine>/somefile ${DEPLOYDIR}/somefile
      }

   In the example above:

   -  ``<multiconfig>`` is the multiconfig name as set by the multiconfig
      :term:`configuration file` (see the :ref:`dev-manual/multiconfig:Setting
      Up and Running a Multiple Configuration Build` section above).

   -  ``<machine>`` must be the :term:`MACHINE` for which ``somefile`` was built
      and deployed. This value may differ from the current :term:`MACHINE` if
      the multiconfig :term:`configuration file` overrides it.

-  Firmware recipes can set the :term:`INHIBIT_DEFAULT_DEPS` variable to ``1``
   if they don't rely on default dependencies such as the standard C library.

Common use case: building baremetal firmware alongside a Linux build
====================================================================

A common use case for multiconfig is to use the default configuration as the
regular Linux build, while one or more multiconfigs can be used to build special
components, such as baremetal firmware. It would also apply to a scenario where
a microcontroller, for example, is companion to a main processor where Linux is
running. This section details how one can achieve these kinds of scenarios with
a multiconfig build.

Adding a multiconfig configuration file and recipe for a baremetal firmware
---------------------------------------------------------------------------

As described in :ref:`dev-manual/multiconfig:Setting Up and Running a Multiple
Configuration Build`, each multiconfig will require a separate
:term:`Configuration File`. In addition, we will define a separate
:term:`TMPDIR` for our baremetal firmware build configuration.

For example, we will define a new ``conf/multiconfig/baremetal-firmware.conf``
as follows::

   TMPDIR .= "-${BB_CURRENT_MC}"
   TCLIBC = "newlib"

The ``baremetal-firmware.conf`` file configures a separate :term:`TMPDIR` for
holding binaries compiled with the `newlib <https://sourceware.org/newlib/>`__
toolchain (see :term:`TCLIBC`).

.. note::

   Here, the default :term:`MACHINE` is not overridden by the multiconfig
   configuration file. As a consequence, the architecture of the built baremetal
   binaries will be the same. In other cases, where the firmware runs on a
   completely different architecture, the :term:`MACHINE` must be overridden.

We then create a recipe ``my-firmware.bb`` that defines how the baremetal
firmware is built. The recipe should contain enough information for the
:term:`OpenEmbedded build system` to properly compile the firmware with our
toolchain. The building tasks may vary depending on the nature of the firmware.
However, the recipe should define a :ref:`ref-classes-deploy` task that deploys
the output into the :term:`DEPLOYDIR` directory. We will consider in the
following that the file is named ``my-firmware.elf``.

Building the firmware
---------------------

The firmware can be built with BitBake with the following command::

   $ bitbake mc:baremetal-firmware:my-firmware

However, we would prefer for ``my-firmware`` to be automatically built when
triggering a normal Linux build.

Using a ``mcdepend``, a recipe belonging to the Linux build can trigger the
build of ``my-firmware``. For example, let's consider that our Linux build needs
to assemble a "special" firmware that uses the output of our ``my-firmware``
recipe - let's call it ``my-parent-firmware.bb``. Then, we should specify this
dependency in ``my-parent-firmware.bb`` with::

   do_compile[mcdepends] = "mc::baremetal-firmware:my-firmware:do_deploy"

The above will ensure that when the :ref:`ref-tasks-compile` task of
``my-parent-firmware`` is triggered, the :ref:`ref-tasks-deploy` task of
``my-firmware`` will already have run successfully.

Using the output of ``my-firmware``
-----------------------------------

After ``my-firmware`` recipe has deployed ``my-firmware.elf``, we need to use
the output in some way. We can make a series of assumptions, based on the
default Yocto Project variables in order to get the binary for packaging.

First, we can set the following in ``my-parent-firmware.bb``::

   FIRMWARE_FILE ??= "${TMPDIR}-baremetal-firmware/deploy/images/<machine>/my-firmware.elf"
   FIRMWARE_FILE[vardepsexclude] += "TMPDIR"

The first assignment stores the value of the path to the firmware built and
deployed by the ``my-firmware.bb`` recipe. The second assignment excludes the
:term:`TMPDIR` variable from being part of ``FIRMWARE_FILE``'s dependencies ---
meaning that changing the value of :term:`TMPDIR` (for example, changing the
host on which the firmware is built) will not invalidate the :ref:`shared state
cache <overview-manual/concepts:shared state cache>`.

Additionally, ``<machine>`` should be replaced by the :term:`MACHINE` for which
we are building in the baremetal-firmware context.

We can then add a :ref:`ref-tasks-install` task to ``my-parent-firmware``::

   do_install() {
       install -Dm 0644 ${FIRMWARE_FILE} ${D}/lib/firmware/my-firmware.elf
   }

Doing the above will allow the firmware binary to be transferred and packaged
into the Linux context and rootfs.
