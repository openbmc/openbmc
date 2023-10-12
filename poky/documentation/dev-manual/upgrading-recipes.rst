.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Upgrading Recipes
*****************

Over time, upstream developers publish new versions for software built
by layer recipes. It is recommended to keep recipes up-to-date with
upstream version releases.

While there are several methods to upgrade a recipe, you might
consider checking on the upgrade status of a recipe first. You can do so
using the ``devtool check-upgrade-status`` command. See the
":ref:`devtool-checking-on-the-upgrade-status-of-a-recipe`"
section in the Yocto Project Reference Manual for more information.

The remainder of this section describes three ways you can upgrade a
recipe. You can use the Automated Upgrade Helper (AUH) to set up
automatic version upgrades. Alternatively, you can use
``devtool upgrade`` to set up semi-automatic version upgrades. Finally,
you can manually upgrade a recipe by editing the recipe itself.

Using the Auto Upgrade Helper (AUH)
===================================

The AUH utility works in conjunction with the OpenEmbedded build system
in order to automatically generate upgrades for recipes based on new
versions being published upstream. Use AUH when you want to create a
service that performs the upgrades automatically and optionally sends
you an email with the results.

AUH allows you to update several recipes with a single use. You can also
optionally perform build and integration tests using images with the
results saved to your hard drive and emails of results optionally sent
to recipe maintainers. Finally, AUH creates Git commits with appropriate
commit messages in the layer's tree for the changes made to recipes.

.. note::

   In some conditions, you should not use AUH to upgrade recipes
   and should instead use either ``devtool upgrade`` or upgrade your
   recipes manually:

   -  When AUH cannot complete the upgrade sequence. This situation
      usually results because custom patches carried by the recipe
      cannot be automatically rebased to the new version. In this case,
      ``devtool upgrade`` allows you to manually resolve conflicts.

   -  When for any reason you want fuller control over the upgrade
      process. For example, when you want special arrangements for
      testing.

The following steps describe how to set up the AUH utility:

#. *Be Sure the Development Host is Set Up:* You need to be sure that
   your development host is set up to use the Yocto Project. For
   information on how to set up your host, see the
   ":ref:`dev-manual/start:Preparing the Build Host`" section.

#. *Make Sure Git is Configured:* The AUH utility requires Git to be
   configured because AUH uses Git to save upgrades. Thus, you must have
   Git user and email configured. The following command shows your
   configurations::

      $ git config --list

   If you do not have the user and
   email configured, you can use the following commands to do so::

      $ git config --global user.name some_name
      $ git config --global user.email username@domain.com

#. *Clone the AUH Repository:* To use AUH, you must clone the repository
   onto your development host. The following command uses Git to create
   a local copy of the repository on your system::

      $ git clone git://git.yoctoproject.org/auto-upgrade-helper
      Cloning into 'auto-upgrade-helper'... remote: Counting objects: 768, done.
      remote: Compressing objects: 100% (300/300), done.
      remote: Total 768 (delta 499), reused 703 (delta 434)
      Receiving objects: 100% (768/768), 191.47 KiB | 98.00 KiB/s, done.
      Resolving deltas: 100% (499/499), done.
      Checking connectivity... done.

   AUH is not part of the :term:`OpenEmbedded-Core (OE-Core)` or
   :term:`Poky` repositories.

#. *Create a Dedicated Build Directory:* Run the :ref:`structure-core-script`
   script to create a fresh :term:`Build Directory` that you use exclusively
   for running the AUH utility::

      $ cd poky
      $ source oe-init-build-env your_AUH_build_directory

   Re-using an existing :term:`Build Directory` and its configurations is not
   recommended as existing settings could cause AUH to fail or behave
   undesirably.

#. *Make Configurations in Your Local Configuration File:* Several
   settings are needed in the ``local.conf`` file in the build
   directory you just created for AUH. Make these following
   configurations:

   -  If you want to enable :ref:`Build
      History <dev-manual/build-quality:maintaining build output quality>`,
      which is optional, you need the following lines in the
      ``conf/local.conf`` file::

         INHERIT =+ "buildhistory"
         BUILDHISTORY_COMMIT = "1"

      With this configuration and a successful
      upgrade, a build history "diff" file appears in the
      ``upgrade-helper/work/recipe/buildhistory-diff.txt`` file found in
      your :term:`Build Directory`.

   -  If you want to enable testing through the :ref:`ref-classes-testimage`
      class, which is optional, you need to have the following set in
      your ``conf/local.conf`` file::

         IMAGE_CLASSES += "testimage"

      .. note::

         If your distro does not enable by default ptest, which Poky
         does, you need the following in your ``local.conf`` file::

                 DISTRO_FEATURES:append = " ptest"


#. *Optionally Start a vncserver:* If you are running in a server
   without an X11 session, you need to start a vncserver::

      $ vncserver :1
      $ export DISPLAY=:1

#. *Create and Edit an AUH Configuration File:* You need to have the
   ``upgrade-helper/upgrade-helper.conf`` configuration file in your
   :term:`Build Directory`. You can find a sample configuration file in the
   :yocto_git:`AUH source repository </auto-upgrade-helper/tree/>`.

   Read through the sample file and make configurations as needed. For
   example, if you enabled build history in your ``local.conf`` as
   described earlier, you must enable it in ``upgrade-helper.conf``.

   Also, if you are using the default ``maintainers.inc`` file supplied
   with Poky and located in ``meta-yocto`` and you do not set a
   "maintainers_whitelist" or "global_maintainer_override" in the
   ``upgrade-helper.conf`` configuration, and you specify "-e all" on
   the AUH command-line, the utility automatically sends out emails to
   all the default maintainers. Please avoid this.

This next set of examples describes how to use the AUH:

-  *Upgrading a Specific Recipe:* To upgrade a specific recipe, use the
   following form::

      $ upgrade-helper.py recipe_name

   For example, this command upgrades the ``xmodmap`` recipe::

      $ upgrade-helper.py xmodmap

-  *Upgrading a Specific Recipe to a Particular Version:* To upgrade a
   specific recipe to a particular version, use the following form::

      $ upgrade-helper.py recipe_name -t version

   For example, this command upgrades the ``xmodmap`` recipe to version 1.2.3::

      $ upgrade-helper.py xmodmap -t 1.2.3

-  *Upgrading all Recipes to the Latest Versions and Suppressing Email
   Notifications:* To upgrade all recipes to their most recent versions
   and suppress the email notifications, use the following command::

      $ upgrade-helper.py all

-  *Upgrading all Recipes to the Latest Versions and Send Email
   Notifications:* To upgrade all recipes to their most recent versions
   and send email messages to maintainers for each attempted recipe as
   well as a status email, use the following command::

      $ upgrade-helper.py -e all

Once you have run the AUH utility, you can find the results in the AUH
:term:`Build Directory`::

   ${BUILDDIR}/upgrade-helper/timestamp

The AUH utility
also creates recipe update commits from successful upgrade attempts in
the layer tree.

You can easily set up to run the AUH utility on a regular basis by using
a cron job. See the
:yocto_git:`weeklyjob.sh </auto-upgrade-helper/tree/weeklyjob.sh>`
file distributed with the utility for an example.

Using ``devtool upgrade``
=========================

As mentioned earlier, an alternative method for upgrading recipes to
newer versions is to use
:doc:`devtool upgrade </ref-manual/devtool-reference>`.
You can read about ``devtool upgrade`` in general in the
":ref:`sdk-manual/extensible:use \`\`devtool upgrade\`\` to create a version of the recipe that supports a newer version of the software`"
section in the Yocto Project Application Development and the Extensible
Software Development Kit (eSDK) Manual.

To see all the command-line options available with ``devtool upgrade``,
use the following help command::

   $ devtool upgrade -h

If you want to find out what version a recipe is currently at upstream
without any attempt to upgrade your local version of the recipe, you can
use the following command::

   $ devtool latest-version recipe_name

As mentioned in the previous section describing AUH, ``devtool upgrade``
works in a less-automated manner than AUH. Specifically,
``devtool upgrade`` only works on a single recipe that you name on the
command line, cannot perform build and integration testing using images,
and does not automatically generate commits for changes in the source
tree. Despite all these "limitations", ``devtool upgrade`` updates the
recipe file to the new upstream version and attempts to rebase custom
patches contained by the recipe as needed.

.. note::

   AUH uses much of ``devtool upgrade`` behind the scenes making AUH somewhat
   of a "wrapper" application for ``devtool upgrade``.

A typical scenario involves having used Git to clone an upstream
repository that you use during build operations. Because you have built the
recipe in the past, the layer is likely added to your
configuration already. If for some reason, the layer is not added, you
could add it easily using the
":ref:`bitbake-layers <bsp-guide/bsp:creating a new bsp layer using the \`\`bitbake-layers\`\` script>`"
script. For example, suppose you use the ``nano.bb`` recipe from the
``meta-oe`` layer in the ``meta-openembedded`` repository. For this
example, assume that the layer has been cloned into following area::

   /home/scottrif/meta-openembedded

The following command from your :term:`Build Directory` adds the layer to
your build configuration (i.e. ``${BUILDDIR}/conf/bblayers.conf``)::

   $ bitbake-layers add-layer /home/scottrif/meta-openembedded/meta-oe
   NOTE: Starting bitbake server...
   Parsing recipes: 100% |##########################################| Time: 0:00:55
   Parsing of 1431 .bb files complete (0 cached, 1431 parsed). 2040 targets, 56 skipped, 0 masked, 0 errors.
   Removing 12 recipes from the x86_64 sysroot: 100% |##############| Time: 0:00:00
   Removing 1 recipes from the x86_64_i586 sysroot: 100% |##########| Time: 0:00:00
   Removing 5 recipes from the i586 sysroot: 100% |#################| Time: 0:00:00
   Removing 5 recipes from the qemux86 sysroot: 100% |##############| Time: 0:00:00

For this example, assume that the ``nano.bb`` recipe that
is upstream has a 2.9.3 version number. However, the version in the
local repository is 2.7.4. The following command from your build
directory automatically upgrades the recipe for you::

   $ devtool upgrade nano -V 2.9.3
   NOTE: Starting bitbake server...
   NOTE: Creating workspace layer in /home/scottrif/poky/build/workspace
   Parsing recipes: 100% |##########################################| Time: 0:00:46
   Parsing of 1431 .bb files complete (0 cached, 1431 parsed). 2040 targets, 56 skipped, 0 masked, 0 errors.
   NOTE: Extracting current version source...
   NOTE: Resolving any missing task queue dependencies
          .
          .
          .
   NOTE: Executing SetScene Tasks
   NOTE: Executing RunQueue Tasks
   NOTE: Tasks Summary: Attempted 74 tasks of which 72 didn't need to be rerun and all succeeded.
   Adding changed files: 100% |#####################################| Time: 0:00:00
   NOTE: Upgraded source extracted to /home/scottrif/poky/build/workspace/sources/nano
   NOTE: New recipe is /home/scottrif/poky/build/workspace/recipes/nano/nano_2.9.3.bb

.. note::

   Using the ``-V`` option is not necessary. Omitting the version number causes
   ``devtool upgrade`` to upgrade the recipe to the most recent version.

Continuing with this example, you can use ``devtool build`` to build the
newly upgraded recipe::

   $ devtool build nano
   NOTE: Starting bitbake server...
   Loading cache: 100% |################################################################################################| Time: 0:00:01
   Loaded 2040 entries from dependency cache.
   Parsing recipes: 100% |##############################################################################################| Time: 0:00:00
   Parsing of 1432 .bb files complete (1431 cached, 1 parsed). 2041 targets, 56 skipped, 0 masked, 0 errors.
   NOTE: Resolving any missing task queue dependencies
          .
          .
          .
   NOTE: Executing SetScene Tasks
   NOTE: Executing RunQueue Tasks
   NOTE: nano: compiling from external source tree /home/scottrif/poky/build/workspace/sources/nano
   NOTE: Tasks Summary: Attempted 520 tasks of which 304 didn't need to be rerun and all succeeded.

Within the ``devtool upgrade`` workflow, you can
deploy and test your rebuilt software. For this example,
however, running ``devtool finish`` cleans up the workspace once the
source in your workspace is clean. This usually means using Git to stage
and submit commits for the changes generated by the upgrade process.

Once the tree is clean, you can clean things up in this example with the
following command from the ``${BUILDDIR}/workspace/sources/nano``
directory::

   $ devtool finish nano meta-oe
   NOTE: Starting bitbake server...
   Loading cache: 100% |################################################################################################| Time: 0:00:00
   Loaded 2040 entries from dependency cache.
   Parsing recipes: 100% |##############################################################################################| Time: 0:00:01
   Parsing of 1432 .bb files complete (1431 cached, 1 parsed). 2041 targets, 56 skipped, 0 masked, 0 errors.
   NOTE: Adding new patch 0001-nano.bb-Stuff-I-changed-when-upgrading-nano.bb.patch
   NOTE: Updating recipe nano_2.9.3.bb
   NOTE: Removing file /home/scottrif/meta-openembedded/meta-oe/recipes-support/nano/nano_2.7.4.bb
   NOTE: Moving recipe file to /home/scottrif/meta-openembedded/meta-oe/recipes-support/nano
   NOTE: Leaving source tree /home/scottrif/poky/build/workspace/sources/nano as-is; if you no longer need it then please delete it manually


Using the ``devtool finish`` command cleans up the workspace and creates a patch
file based on your commits. The tool puts all patch files back into the
source directory in a sub-directory named ``nano`` in this case.

Manually Upgrading a Recipe
===========================

If for some reason you choose not to upgrade recipes using
:ref:`dev-manual/upgrading-recipes:Using the Auto Upgrade Helper (AUH)` or
by :ref:`dev-manual/upgrading-recipes:Using \`\`devtool upgrade\`\``,
you can manually edit the recipe files to upgrade the versions.

.. note::

   Manually updating multiple recipes scales poorly and involves many
   steps. The recommendation to upgrade recipe versions is through AUH
   or ``devtool upgrade``, both of which automate some steps and provide
   guidance for others needed for the manual process.

To manually upgrade recipe versions, follow these general steps:

#. *Change the Version:* Rename the recipe such that the version (i.e.
   the :term:`PV` part of the recipe name)
   changes appropriately. If the version is not part of the recipe name,
   change the value as it is set for :term:`PV` within the recipe itself.

#. *Update* :term:`SRCREV` *if Needed*: If the source code your recipe builds
   is fetched from Git or some other version control system, update
   :term:`SRCREV` to point to the
   commit hash that matches the new version.

#. *Build the Software:* Try to build the recipe using BitBake. Typical
   build failures include the following:

   -  License statements were updated for the new version. For this
      case, you need to review any changes to the license and update the
      values of :term:`LICENSE` and
      :term:`LIC_FILES_CHKSUM`
      as needed.

      .. note::

         License changes are often inconsequential. For example, the
         license text's copyright year might have changed.

   -  Custom patches carried by the older version of the recipe might
      fail to apply to the new version. For these cases, you need to
      review the failures. Patches might not be necessary for the new
      version of the software if the upgraded version has fixed those
      issues. If a patch is necessary and failing, you need to rebase it
      into the new version.

#. *Optionally Attempt to Build for Several Architectures:* Once you
   successfully build the new software for a given architecture, you
   could test the build for other architectures by changing the
   :term:`MACHINE` variable and
   rebuilding the software. This optional step is especially important
   if the recipe is to be released publicly.

#. *Check the Upstream Change Log or Release Notes:* Checking both these
   reveals if there are new features that could break
   backwards-compatibility. If so, you need to take steps to mitigate or
   eliminate that situation.

#. *Optionally Create a Bootable Image and Test:* If you want, you can
   test the new software by booting it onto actual hardware.

#. *Create a Commit with the Change in the Layer Repository:* After all
   builds work and any testing is successful, you can create commits for
   any changes in the layer holding your upgraded recipe.

