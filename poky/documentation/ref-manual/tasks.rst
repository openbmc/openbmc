.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*****
Tasks
*****

Tasks are units of execution for BitBake. Recipes (``.bb`` files) use
tasks to complete configuring, compiling, and packaging software. This
chapter provides a reference of the tasks defined in the OpenEmbedded
build system.

Normal Recipe Build Tasks
=========================

The following sections describe normal tasks associated with building a
recipe. For more information on tasks and dependencies, see the
":ref:`bitbake-user-manual/bitbake-user-manual-metadata:tasks`" and
":ref:`bitbake-user-manual/bitbake-user-manual-execution:dependencies`" sections in the
BitBake User Manual.

.. _ref-tasks-build:

``do_build``
------------

The default task for all recipes. This task depends on all other normal
tasks required to build a recipe.

.. _ref-tasks-compile:

``do_compile``
--------------

Compiles the source code. This task runs with the current working
directory set to ``${``\ :term:`B`\ ``}``.

The default behavior of this task is to run the ``oe_runmake`` function
if a makefile (``Makefile``, ``makefile``, or ``GNUmakefile``) is found.
If no such file is found, the :ref:`ref-tasks-compile` task does nothing.

.. _ref-tasks-compile_ptest_base:

``do_compile_ptest_base``
-------------------------

Compiles the runtime test suite included in the software being built.

.. _ref-tasks-configure:

``do_configure``
----------------

Configures the source by enabling and disabling any build-time and
configuration options for the software being built. The task runs with
the current working directory set to ``${``\ :term:`B`\ ``}``.

The default behavior of this task is to run ``oe_runmake clean`` if a
makefile (``Makefile``, ``makefile``, or ``GNUmakefile``) is found and
:term:`CLEANBROKEN` is not set to "1". If no such
file is found or the :term:`CLEANBROKEN` variable is set to "1", the
:ref:`ref-tasks-configure` task does nothing.

.. _ref-tasks-configure_ptest_base:

``do_configure_ptest_base``
---------------------------

Configures the runtime test suite included in the software being built.

.. _ref-tasks-deploy:

``do_deploy``
-------------

Writes output files that are to be deployed to
``${``\ :term:`DEPLOY_DIR_IMAGE`\ ``}``. The
task runs with the current working directory set to
``${``\ :term:`B`\ ``}``.

Recipes implementing this task should inherit the
:ref:`ref-classes-deploy` class and should write the output
to ``${``\ :term:`DEPLOYDIR`\ ``}``, which is not to be
confused with ``${DEPLOY_DIR}``. The :ref:`ref-classes-deploy` class sets up
:ref:`ref-tasks-deploy` as a shared state (sstate) task that can be accelerated
through sstate use. The sstate mechanism takes care of copying the
output from ``${DEPLOYDIR}`` to ``${DEPLOY_DIR_IMAGE}``.

.. note::

   Do not write the output directly to ``${DEPLOY_DIR_IMAGE}``, as this causes
   the sstate mechanism to malfunction.

The :ref:`ref-tasks-deploy` task is not added as a task by default and
consequently needs to be added manually. If you want the task to run
after :ref:`ref-tasks-compile`, you can add it by doing
the following::

      addtask deploy after do_compile

Adding :ref:`ref-tasks-deploy` after other tasks works the same way.

.. note::

   You do not need to add ``before do_build`` to the ``addtask`` command
   (though it is harmless), because the :ref:`ref-classes-base` class contains the following::

           do_build[recrdeptask] += "do_deploy"


   See the ":ref:`bitbake-user-manual/bitbake-user-manual-execution:dependencies`"
   section in the BitBake User Manual for more information.

If the :ref:`ref-tasks-deploy` task re-executes, any previous output is removed
(i.e. "cleaned").

.. _ref-tasks-fetch:

``do_fetch``
------------

Fetches the source code. This task uses the :term:`SRC_URI` variable and the
argument's prefix to determine the correct
:ref:`fetcher <bitbake-user-manual/bitbake-user-manual-fetching:fetchers>`
module.

.. _ref-tasks-image:

``do_image``
------------

Starts the image generation process. The :ref:`ref-tasks-image` task runs after
the OpenEmbedded build system has run the
:ref:`ref-tasks-rootfs` task during which packages are
identified for installation into the image and the root filesystem is
created, complete with post-processing.

The :ref:`ref-tasks-image` task performs pre-processing on the image through the
:term:`IMAGE_PREPROCESS_COMMAND` and
dynamically generates supporting :ref:`do_image_* <ref-tasks-image>` tasks as needed.

For more information on image creation, see the ":ref:`overview-manual/concepts:image generation`"
section in the Yocto Project Overview and Concepts Manual.

.. _ref-tasks-image-complete:

``do_image_complete``
---------------------

Completes the image generation process. The :ref:`do_image_complete <ref-tasks-image-complete>` task
runs after the OpenEmbedded build system has run the
:ref:`ref-tasks-image` task during which image
pre-processing occurs and through dynamically generated :ref:`do_image_* <ref-tasks-image>`
tasks the image is constructed.

The :ref:`do_image_complete <ref-tasks-image-complete>` task performs post-processing on the image
through the
:term:`IMAGE_POSTPROCESS_COMMAND`.

For more information on image creation, see the
":ref:`overview-manual/concepts:image generation`"
section in the Yocto Project Overview and Concepts Manual.

.. _ref-tasks-install:

``do_install``
--------------

Copies files that are to be packaged into the holding area
``${``\ :term:`D`\ ``}``. This task runs with the current
working directory set to ``${``\ :term:`B`\ ``}``, which is the
compilation directory. The :ref:`ref-tasks-install` task, as well as other tasks
that either directly or indirectly depend on the installed files (e.g.
:ref:`ref-tasks-package`, :ref:`do_package_write_* <ref-tasks-package_write_deb>`, and
:ref:`ref-tasks-rootfs`), run under
:ref:`fakeroot <overview-manual/concepts:fakeroot and pseudo>`.

.. note::

   When installing files, be careful not to set the owner and group IDs
   of the installed files to unintended values. Some methods of copying
   files, notably when using the recursive ``cp`` command, can preserve
   the UID and/or GID of the original file, which is usually not what
   you want. The ``host-user-contaminated`` QA check checks for files
   that probably have the wrong ownership.

   Safe methods for installing files include the following:

   -  The ``install`` utility. This utility is the preferred method.

   -  The ``cp`` command with the ``--no-preserve=ownership`` option.

   -  The ``tar`` command with the ``--no-same-owner`` option. See the
      ``bin_package.bbclass`` file in the ``meta/classes-recipe``
      subdirectory of the :term:`Source Directory` for an example.

.. _ref-tasks-install_ptest_base:

``do_install_ptest_base``
-------------------------

Copies the runtime test suite files from the compilation directory to a
holding area.

.. _ref-tasks-package:

``do_package``
--------------

Analyzes the content of the holding area
``${``\ :term:`D`\ ``}`` and splits the content into subsets
based on available packages and files. This task makes use of the
:term:`PACKAGES` and :term:`FILES`
variables.

The :ref:`ref-tasks-package` task, in conjunction with the
:ref:`ref-tasks-packagedata` task, also saves some
important package metadata. For additional information, see the
:term:`PKGDESTWORK` variable and the
":ref:`overview-manual/concepts:automatically added runtime dependencies`"
section in the Yocto Project Overview and Concepts Manual.

.. _ref-tasks-package_qa:

``do_package_qa``
-----------------

Runs QA checks on packaged files. For more information on these checks,
see the :ref:`ref-classes-insane` class.

.. _ref-tasks-package_write_deb:

``do_package_write_deb``
------------------------

Creates Debian packages (i.e. ``*.deb`` files) and places them in the
``${``\ :term:`DEPLOY_DIR_DEB`\ ``}`` directory in
the package feeds area. For more information, see the
":ref:`overview-manual/concepts:package feeds`" section in
the Yocto Project Overview and Concepts Manual.

.. _ref-tasks-package_write_ipk:

``do_package_write_ipk``
------------------------

Creates IPK packages (i.e. ``*.ipk`` files) and places them in the
``${``\ :term:`DEPLOY_DIR_IPK`\ ``}`` directory in
the package feeds area. For more information, see the
":ref:`overview-manual/concepts:package feeds`" section in
the Yocto Project Overview and Concepts Manual.

.. _ref-tasks-package_write_rpm:

``do_package_write_rpm``
------------------------

Creates RPM packages (i.e. ``*.rpm`` files) and places them in the
``${``\ :term:`DEPLOY_DIR_RPM`\ ``}`` directory in
the package feeds area. For more information, see the
":ref:`overview-manual/concepts:package feeds`" section in
the Yocto Project Overview and Concepts Manual.

.. _ref-tasks-packagedata:

``do_packagedata``
------------------

Saves package metadata generated by the
:ref:`ref-tasks-package` task in
:term:`PKGDATA_DIR` to make it available globally.

.. _ref-tasks-patch:

``do_patch``
------------

Locates patch files and applies them to the source code.

After fetching and unpacking source files, the build system uses the
recipe's :term:`SRC_URI` statements
to locate and apply patch files to the source code.

.. note::

   The build system uses the :term:`FILESPATH` variable to determine the
   default set of directories when searching for patches.

Patch files, by default, are ``*.patch`` and ``*.diff`` files created
and kept in a subdirectory of the directory holding the recipe file. For
example, consider the
:yocto_git:`bluez5 </poky/tree/meta/recipes-connectivity/bluez5>`
recipe from the OE-Core layer (i.e. ``poky/meta``)::

   poky/meta/recipes-connectivity/bluez5

This recipe has two patch files located here::

   poky/meta/recipes-connectivity/bluez5/bluez5

In the ``bluez5`` recipe, the :term:`SRC_URI` statements point to the source
and patch files needed to build the package.

.. note::

   In the case for the ``bluez5_5.48.bb`` recipe, the :term:`SRC_URI` statements
   are from an include file ``bluez5.inc``.

As mentioned earlier, the build system treats files whose file types are
``.patch`` and ``.diff`` as patch files. However, you can use the
"apply=yes" parameter with the :term:`SRC_URI` statement to indicate any
file as a patch file::

   SRC_URI = " \
       git://path_to_repo/some_package \
       file://file;apply=yes \
       "

Conversely, if you have a file whose file type is ``.patch`` or ``.diff``
and you want to exclude it so that the :ref:`ref-tasks-patch` task does not apply
it during the patch phase, you can use the "apply=no" parameter with the
:term:`SRC_URI` statement::

   SRC_URI = " \
       git://path_to_repo/some_package \
       file://file1.patch \
       file://file2.patch;apply=no \
       "

In the previous example ``file1.patch`` would be applied as a patch by default
while ``file2.patch`` would not be applied.

You can find out more about the patching process in the
":ref:`overview-manual/concepts:patching`" section in
the Yocto Project Overview and Concepts Manual and the
":ref:`dev-manual/new-recipe:patching code`" section in the
Yocto Project Development Tasks Manual.

.. _ref-tasks-populate_lic:

``do_populate_lic``
-------------------

Writes license information for the recipe that is collected later when
the image is constructed.

.. _ref-tasks-populate_sdk:

``do_populate_sdk``
-------------------

Creates the file and directory structure for an installable SDK. See the
":ref:`overview-manual/concepts:sdk generation`"
section in the Yocto Project Overview and Concepts Manual for more
information.

.. _ref-tasks-populate_sdk_ext:

``do_populate_sdk_ext``
-----------------------

Creates the file and directory structure for an installable extensible
SDK (eSDK). See the ":ref:`overview-manual/concepts:sdk generation`"
section in the Yocto Project Overview and Concepts Manual for more
information.


.. _ref-tasks-populate_sysroot:

``do_populate_sysroot``
-----------------------

Stages (copies) a subset of the files installed by the
:ref:`ref-tasks-install` task into the appropriate
sysroot. For information on how to access these files from other
recipes, see the :term:`STAGING_DIR* <STAGING_DIR_HOST>` variables.
Directories that would typically not be needed by other recipes at build
time (e.g. ``/etc``) are not copied by default.

For information on what directories are copied by default, see the
:term:`SYSROOT_DIRS* <SYSROOT_DIRS>` variables. You can change
these variables inside your recipe if you need to make additional (or
fewer) directories available to other recipes at build time.

The :ref:`ref-tasks-populate_sysroot` task is a shared state (sstate) task, which
means that the task can be accelerated through sstate use. Realize also
that if the task is re-executed, any previous output is removed (i.e.
"cleaned").

.. _ref-tasks-prepare_recipe_sysroot:

``do_prepare_recipe_sysroot``
-----------------------------

Installs the files into the individual recipe specific sysroots (i.e.
``recipe-sysroot`` and ``recipe-sysroot-native`` under
``${``\ :term:`WORKDIR`\ ``}`` based upon the
dependencies specified by :term:`DEPENDS`). See the
":ref:`ref-classes-staging`" class for more information.

.. _ref-tasks-rm_work:

``do_rm_work``
--------------

Removes work files after the OpenEmbedded build system has finished with
them. You can learn more by looking at the
":ref:`ref-classes-rm-work`" section.

.. _ref-tasks-unpack:

``do_unpack``
-------------

Unpacks the source code into a working directory pointed to by
``${``\ :term:`WORKDIR`\ ``}``. The :term:`S`
variable also plays a role in where unpacked source files ultimately
reside. For more information on how source files are unpacked, see the
":ref:`overview-manual/concepts:source fetching`"
section in the Yocto Project Overview and Concepts Manual and also see
the :term:`WORKDIR` and :term:`S` variable descriptions.

Manually Called Tasks
=====================

These tasks are typically manually triggered (e.g. by using the
``bitbake -c`` command-line option):

``do_checkuri``
---------------

Validates the :term:`SRC_URI` value.

.. _ref-tasks-clean:

``do_clean``
------------

Removes all output files for a target from the
:ref:`ref-tasks-unpack` task forward (i.e. :ref:`ref-tasks-unpack`,
:ref:`ref-tasks-configure`,
:ref:`ref-tasks-compile`,
:ref:`ref-tasks-install`, and
:ref:`ref-tasks-package`).

You can run this task using BitBake as follows::

   $ bitbake -c clean recipe

Running this task does not remove the
:ref:`sstate <overview-manual/concepts:shared state cache>` cache files.
Consequently, if no changes have been made and the recipe is rebuilt
after cleaning, output files are simply restored from the sstate cache.
If you want to remove the sstate cache files for the recipe, you need to
use the :ref:`ref-tasks-cleansstate` task instead
(i.e. ``bitbake -c cleansstate`` recipe).

.. _ref-tasks-cleanall:

``do_cleanall``
---------------

Removes all output files, shared state
(:ref:`sstate <overview-manual/concepts:shared state cache>`) cache, and
downloaded source files for a target (i.e. the contents of
:term:`DL_DIR`). Essentially, the :ref:`ref-tasks-cleanall` task is
identical to the :ref:`ref-tasks-cleansstate` task
with the added removal of downloaded source files.

You can run this task using BitBake as follows::

   $ bitbake -c cleanall recipe

You should never use the :ref:`ref-tasks-cleanall` task in a normal
scenario. If you want to start fresh with the :ref:`ref-tasks-fetch` task,
use instead::

  $ bitbake -f -c fetch recipe

.. note::

   The reason to prefer ``bitbake -f -c fetch`` is that the
   :ref:`ref-tasks-cleanall` task would break in some cases, such as::

      $ bitbake -c fetch    recipe
      $ bitbake -c cleanall recipe-native
      $ bitbake -c unpack   recipe

   because after step 1 there is a stamp file for the
   :ref:`ref-tasks-fetch` task of ``recipe``, and it won't be removed at
   step 2 because step 2 uses a different work directory. So the unpack task
   at step 3 will try to extract the downloaded archive and fail as it has
   been deleted in step 2.

   Note that this also applies to BitBake from concurrent processes when a
   shared download directory (:term:`DL_DIR`) is setup.

.. _ref-tasks-cleansstate:

``do_cleansstate``
------------------

Removes all output files and shared state
(:ref:`sstate <overview-manual/concepts:shared state cache>`) cache for a
target. Essentially, the :ref:`ref-tasks-cleansstate` task is identical to the
:ref:`ref-tasks-clean` task with the added removal of
shared state (:ref:`sstate <overview-manual/concepts:shared state cache>`)
cache.

You can run this task using BitBake as follows::

   $ bitbake -c cleansstate recipe

When you run the :ref:`ref-tasks-cleansstate` task, the OpenEmbedded build system
no longer uses any sstate. Consequently, building the recipe from
scratch is guaranteed.

.. note::

   Using :ref:`ref-tasks-cleansstate` with a shared :term:`SSTATE_DIR` is
   not recommended because it could trigger an error during the build of a
   separate BitBake instance. This is because the builds check sstate "up
   front" but download the files later, so it if is deleted in the
   meantime, it will cause an error but not a total failure as it will
   rebuild it.

   The reliable and preferred way to force a new build is to use ``bitbake
   -f`` instead.

.. note::

   The :ref:`ref-tasks-cleansstate` task cannot remove sstate from a remote sstate
   mirror. If you need to build a target from scratch using remote mirrors, use
   the "-f" option as follows::

      $ bitbake -f -c do_cleansstate target


.. _ref-tasks-pydevshell:

``do_pydevshell``
-----------------

Starts a shell in which an interactive Python interpreter allows you to
interact with the BitBake build environment. From within this shell, you
can directly examine and set bits from the data store and execute
functions as if within the BitBake environment. See the ":ref:`dev-manual/python-development-shell:using a Python development shell`" section in
the Yocto Project Development Tasks Manual for more information about
using ``pydevshell``.

.. _ref-tasks-devshell:

``do_devshell``
---------------

Starts a shell whose environment is set up for development, debugging,
or both. See the ":ref:`dev-manual/development-shell:using a development shell`" section in the
Yocto Project Development Tasks Manual for more information about using
``devshell``.

.. _ref-tasks-listtasks:

``do_listtasks``
----------------

Lists all defined tasks for a target.

.. _ref-tasks-package_index:

``do_package_index``
--------------------

Creates or updates the index in the :ref:`overview-manual/concepts:package feeds` area.

.. note::

   This task is not triggered with the ``bitbake -c`` command-line option as
   are the other tasks in this section. Because this task is specifically for
   the ``package-index`` recipe, you run it using ``bitbake package-index``.

Image-Related Tasks
===================

The following tasks are applicable to image recipes.

.. _ref-tasks-bootimg:

``do_bootimg``
--------------

Creates a bootable live image. See the
:term:`IMAGE_FSTYPES` variable for additional
information on live image types.

.. _ref-tasks-bundle_initramfs:

``do_bundle_initramfs``
-----------------------

Combines an :term:`Initramfs` image and kernel together to
form a single image.

.. _ref-tasks-rootfs:

``do_rootfs``
-------------

Creates the root filesystem (file and directory structure) for an image.
See the ":ref:`overview-manual/concepts:image generation`"
section in the Yocto Project Overview and Concepts Manual for more
information on how the root filesystem is created.

.. _ref-tasks-testimage:

``do_testimage``
----------------

Boots an image and performs runtime tests within the image. For
information on automatically testing images, see the
":ref:`dev-manual/runtime-testing:performing automated runtime testing`"
section in the Yocto Project Development Tasks Manual.

.. _ref-tasks-testimage_auto:

``do_testimage_auto``
---------------------

Boots an image and performs runtime tests within the image immediately
after it has been built. This task is enabled when you set
:term:`TESTIMAGE_AUTO` equal to "1".

For information on automatically testing images, see the
":ref:`dev-manual/runtime-testing:performing automated runtime testing`"
section in the Yocto Project Development Tasks Manual.

Kernel-Related Tasks
====================

The following tasks are applicable to kernel recipes. Some of these
tasks (e.g. the :ref:`ref-tasks-menuconfig` task) are
also applicable to recipes that use Linux kernel style configuration
such as the BusyBox recipe.

.. _ref-tasks-compile_kernelmodules:

``do_compile_kernelmodules``
----------------------------

Runs the step that builds the kernel modules (if needed). Building a
kernel consists of two steps: 1) the kernel (``vmlinux``) is built, and
2) the modules are built (i.e. ``make modules``).

.. _ref-tasks-diffconfig:

``do_diffconfig``
-----------------

When invoked by the user, this task creates a file containing the
differences between the original config as produced by
:ref:`ref-tasks-kernel_configme` task and the
changes made by the user with other methods (i.e. using
(:ref:`ref-tasks-kernel_menuconfig`). Once the
file of differences is created, it can be used to create a config
fragment that only contains the differences. You can invoke this task
from the command line as follows::

   $ bitbake linux-yocto -c diffconfig

For more information, see the
":ref:`kernel-dev/common:creating configuration fragments`"
section in the Yocto Project Linux Kernel Development Manual.

.. _ref-tasks-kernel_checkout:

``do_kernel_checkout``
----------------------

Converts the newly unpacked kernel source into a form with which the
OpenEmbedded build system can work. Because the kernel source can be
fetched in several different ways, the :ref:`ref-tasks-kernel_checkout` task makes
sure that subsequent tasks are given a clean working tree copy of the
kernel with the correct branches checked out.

.. _ref-tasks-kernel_configcheck:

``do_kernel_configcheck``
-------------------------

Validates the configuration produced by the
:ref:`ref-tasks-kernel_menuconfig` task. The
:ref:`ref-tasks-kernel_configcheck` task produces warnings when a requested
configuration does not appear in the final ``.config`` file or when you
override a policy configuration in a hardware configuration fragment.
You can run this task explicitly and view the output by using the
following command::

   $ bitbake linux-yocto -c kernel_configcheck -f

For more information, see the
":ref:`kernel-dev/common:validating configuration`"
section in the Yocto Project Linux Kernel Development Manual.

.. _ref-tasks-kernel_configme:

``do_kernel_configme``
----------------------

After the kernel is patched by the :ref:`ref-tasks-patch`
task, the :ref:`ref-tasks-kernel_configme` task assembles and merges all the
kernel config fragments into a merged configuration that can then be
passed to the kernel configuration phase proper. This is also the time
during which user-specified defconfigs are applied if present, and where
configuration modes such as ``--allnoconfig`` are applied.

.. _ref-tasks-kernel_menuconfig:

``do_kernel_menuconfig``
------------------------

Invoked by the user to manipulate the ``.config`` file used to build a
linux-yocto recipe. This task starts the Linux kernel configuration
tool, which you then use to modify the kernel configuration.

.. note::

   You can also invoke this tool from the command line as follows::

           $ bitbake linux-yocto -c menuconfig


See the ":ref:`kernel-dev/common:using \`\`menuconfig\`\``"
section in the Yocto Project Linux Kernel Development Manual for more
information on this configuration tool.

.. _ref-tasks-kernel_metadata:

``do_kernel_metadata``
----------------------

Collects all the features required for a given kernel build, whether the
features come from :term:`SRC_URI` or from Git
repositories. After collection, the :ref:`ref-tasks-kernel_metadata` task
processes the features into a series of config fragments and patches,
which can then be applied by subsequent tasks such as
:ref:`ref-tasks-patch` and
:ref:`ref-tasks-kernel_configme`.

.. _ref-tasks-menuconfig:

``do_menuconfig``
-----------------

Runs ``make menuconfig`` for the kernel. For information on
``menuconfig``, see the
":ref:`kernel-dev/common:using \`\`menuconfig\`\``"
section in the Yocto Project Linux Kernel Development Manual.

.. _ref-tasks-savedefconfig:

``do_savedefconfig``
--------------------

When invoked by the user, creates a defconfig file that can be used
instead of the default defconfig. The saved defconfig contains the
differences between the default defconfig and the changes made by the
user using other methods (i.e. the
:ref:`ref-tasks-kernel_menuconfig` task. You
can invoke the task using the following command::

   $ bitbake linux-yocto -c savedefconfig

.. _ref-tasks-shared_workdir:

``do_shared_workdir``
---------------------

After the kernel has been compiled but before the kernel modules have
been compiled, this task copies files required for module builds and
which are generated from the kernel build into the shared work
directory. With these copies successfully copied, the
:ref:`ref-tasks-compile_kernelmodules` task
can successfully build the kernel modules in the next step of the build.

.. _ref-tasks-sizecheck:

``do_sizecheck``
----------------

After the kernel has been built, this task checks the size of the
stripped kernel image against
:term:`KERNEL_IMAGE_MAXSIZE`. If that
variable was set and the size of the stripped kernel exceeds that size,
the kernel build produces a warning to that effect.

.. _ref-tasks-strip:

``do_strip``
------------

If ``KERNEL_IMAGE_STRIP_EXTRA_SECTIONS`` is defined, this task strips
the sections named in that variable from ``vmlinux``. This stripping is
typically used to remove nonessential sections such as ``.comment``
sections from a size-sensitive configuration.

.. _ref-tasks-validate_branches:

``do_validate_branches``
------------------------

After the kernel is unpacked but before it is patched, this task makes
sure that the machine and metadata branches as specified by the
:term:`SRCREV` variables actually exist on the specified
branches. Otherwise, if :term:`AUTOREV` is not being used, the
:ref:`ref-tasks-validate_branches` task fails during the build.
