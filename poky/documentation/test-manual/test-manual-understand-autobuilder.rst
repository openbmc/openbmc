.. SPDX-License-Identifier: CC-BY-2.0-UK

*******************************************
Understanding the Yocto Project Autobuilder
*******************************************

Execution Flow within the Autobuilder
=====================================

The "a-full" and "a-quick" targets are the usual entry points into the
Autobuilder and it makes sense to follow the process through the system
starting there. This is best visualised from the Autobuilder Console
view (:yocto_ab:`/typhoon/#/console`).

Each item along the top of that view represents some "target build" and
these targets are all run in parallel. The 'full' build will trigger the
majority of them, the "quick" build will trigger some subset of them.
The Autobuilder effectively runs whichever configuration is defined for
each of those targets on a seperate buildbot worker. To understand the
configuration, you need to look at the entry on ``config.json`` file
within the ``yocto-autobuilder-helper`` repository. The targets are
defined in the ‘overrides' section, a quick example could be qemux86-64
which looks like::

   "qemux86-64" : {
         "MACHINE" : "qemux86-64",
         "TEMPLATE" : "arch-qemu",
         "step1" : {
               "extravars" : [
                     "IMAGE_FSTYPES_append = ' wic wic.bmap'"
                    ]
        }
   },

And to expand that, you need the "arch-qemu" entry from
the "templates" section, which looks like::

   "arch-qemu" : {
         "BUILDINFO" : true,
         "BUILDHISTORY" : true,
         "step1" : {
               "BBTARGETS" : "core-image-sato core-image-sato-dev core-image-sato-sdk core-image-minimal core-image-minimal-dev core-image-sato:do_populate_sdk",
         "SANITYTARGETS" : "core-image-minimal:do_testimage core-image-sato:do_testimage core-image-sato-sdk:do_testimage core-image-sato:do_testsdk"
         },
         "step2" : {
               "SDKMACHINE" : "x86_64",
               "BBTARGETS" : "core-image-sato:do_populate_sdk core-image-minimal:do_populate_sdk_ext core-image-sato:do_populate_sdk_ext",
               "SANITYTARGETS" : "core-image-sato:do_testsdk core-image-minimal:do_testsdkext core-image-sato:do_testsdkext"
         },
         "step3" : {
               "BUILDHISTORY" : false,
               "EXTRACMDS" : ["${SCRIPTSDIR}/checkvnc; DISPLAY=:1 oe-selftest ${HELPERSTMACHTARGS} -j 15"],
               "ADDLAYER" : ["${BUILDDIR}/../meta-selftest"]
         }
   },

Combining these two entries you can see that "qemux86-64" is a three step build where the
``bitbake BBTARGETS`` would be run, then ``bitbake SANITYTARGETS`` for each step; all for
``MACHINE="qemx86-64"`` but with differing SDKMACHINE settings. In step
1 an extra variable is added to the ``auto.conf`` file to enable wic
image generation.

While not every detail of this is covered here, you can see how the
template mechanism allows quite complex configurations to be built up
yet allows duplication and repetition to be kept to a minimum.

The different build targets are designed to allow for parallelisation,
so different machines are usually built in parallel, operations using
the same machine and metadata are built sequentially, with the aim of
trying to optimise build efficiency as much as possible.

The ``config.json`` file is processed by the scripts in the Helper
repository in the ``scripts`` directory. The following section details
how this works.

.. _test-autobuilder-target-exec-overview:

Autobuilder Target Execution Overview
=====================================

For each given target in a build, the Autobuilder executes several
steps. These are configured in ``yocto-autobuilder2/builders.py`` and
roughly consist of:

#. *Run clobberdir*.

   This cleans out any previous build. Old builds are left around to
   allow easier debugging of failed builds. For additional information,
   see :ref:`test-manual/test-manual-understand-autobuilder:clobberdir`.

#. *Obtain yocto-autobuilder-helper*

   This step clones the ``yocto-autobuilder-helper`` git repository.
   This is necessary to prevent the requirement to maintain all the
   release or project-specific code within Buildbot. The branch chosen
   matches the release being built so we can support older releases and
   still make changes in newer ones.

#. *Write layerinfo.json*

   This transfers data in the Buildbot UI when the build was configured
   to the Helper.

#. *Call scripts/shared-repo-unpack*

   This is a call into the Helper scripts to set up a checkout of all
   the pieces this build might need. It might clone the BitBake
   repository and the OpenEmbedded-Core repository. It may clone the
   Poky repository, as well as additional layers. It will use the data
   from the ``layerinfo.json`` file to help understand the
   configuration. It will also use a local cache of repositories to
   speed up the clone checkouts. For additional information, see
   :ref:`test-manual/test-manual-understand-autobuilder:Autobuilder Clone Cache`.

   This step has two possible modes of operation. If the build is part
   of a parent build, its possible that all the repositories needed may
   already be available, ready in a pre-prepared directory. An "a-quick"
   or "a-full" build would prepare this before starting the other
   sub-target builds. This is done for two reasons:

   -  the upstream may change during a build, for example, from a forced
      push and this ensures we have matching content for the whole build

   -  if 15 Workers all tried to pull the same data from the same repos,
      we can hit resource limits on upstream servers as they can think
      they are under some kind of network attack

   This pre-prepared directory is shared among the Workers over NFS. If
   the build is an individual build and there is no "shared" directory
   available, it would clone from the cache and the upstreams as
   necessary. This is considered the fallback mode.

#. *Call scripts/run-config*

   This is another call into the Helper scripts where its expected that
   the main functionality of this target will be executed.

.. _test-autobuilder-tech:

Autobuilder Technology
======================

The Autobuilder has Yocto Project-specific functionality to allow builds
to operate with increased efficiency and speed.

.. _test-clobberdir:

clobberdir
----------

When deleting files, the Autobuilder uses ``clobberdir``, which is a
special script that moves files to a special location, rather than
deleting them. Files in this location are deleted by an ``rm`` command,
which is run under ``ionice -c 3``. For example, the deletion only
happens when there is idle IO capacity on the Worker. The Autobuilder
Worker Janitor runs this deletion. See :ref:`test-manual/test-manual-understand-autobuilder:Autobuilder Worker Janitor`.

.. _test-autobuilder-clone-cache:

Autobuilder Clone Cache
-----------------------

Cloning repositories from scratch each time they are required was slow
on the Autobuilder. We therefore have a stash of commonly used
repositories pre-cloned on the Workers. Data is fetched from these
during clones first, then "topped up" with later revisions from any
upstream when necesary. The cache is maintained by the Autobuilder
Worker Janitor. See :ref:`test-manual/test-manual-understand-autobuilder:Autobuilder Worker Janitor`.

.. _test-autobuilder-worker-janitor:

Autobuilder Worker Janitor
--------------------------

This is a process running on each Worker that performs two basic
operations, including background file deletion at IO idle (see :ref:`test-manual/test-manual-understand-autobuilder:Autobuilder Target Execution Overview`: Run clobberdir) and
maintainenance of a cache of cloned repositories to improve the speed
the system can checkout repositories.

.. _test-shared-dl-dir:

Shared DL_DIR
-------------

The Workers are all connected over NFS which allows DL_DIR to be shared
between them. This reduces network accesses from the system and allows
the build to be sped up. Usage of the directory within the build system
is designed to be able to be shared over NFS.

.. _test-shared-sstate-cache:

Shared SSTATE_DIR
-----------------

The Workers are all connected over NFS which allows the ``sstate``
directory to be shared between them. This means once a Worker has built
an artifact, all the others can benefit from it. Usage of the directory
within the directory is designed for sharing over NFS.

.. _test-resulttool:

Resulttool
----------

All of the different tests run as part of the build generate output into
``testresults.json`` files. This allows us to determine which tests ran
in a given build and their status. Additional information, such as
failure logs or the time taken to run the tests, may also be included.

Resulttool is part of OpenEmbedded-Core and is used to manipulate these
json results files. It has the ability to merge files together, display
reports of the test results and compare different result files.

For details, see :yocto_wiki:`/wiki/Resulttool`.

.. _test-run-config-tgt-execution:

run-config Target Execution
===========================

The ``scripts/run-config`` execution is where most of the work within
the Autobuilder happens. It runs through a number of steps; the first
are general setup steps that are run once and include:

#. Set up any ``buildtools-tarball`` if configured.

#. Call "buildhistory-init" if buildhistory is configured.

For each step that is configured in ``config.json``, it will perform the
following:

#. Add any layers that are specified using the
   ``bitbake-layers add-layer`` command (logging as stepXa)

#. Call the ``scripts/setup-config`` script to generate the necessary
   ``auto.conf`` configuration file for the build

#. Run the ``bitbake BBTARGETS`` command (logging as stepXb)

#. Run the ``bitbake SANITYTARGETS`` command (logging as stepXc)

#. Run the ``EXTRACMDS`` command, which are run within the BitBake build
   environment (logging as stepXd)

#. Run the ``EXTRAPLAINCMDS`` command(s), which are run outside the
   BitBake build environment (logging as stepXd)

#. Remove any layers added in step
   1 using the ``bitbake-layers remove-layer`` command (logging as stepXa)

Once the execution steps above complete, ``run-config`` executes a set
of post-build steps, including:

#. Call ``scripts/publish-artifacts`` to collect any output which is to
   be saved from the build.

#. Call ``scripts/collect-results`` to collect any test results to be
   saved from the build.

#. Call ``scripts/upload-error-reports`` to send any error reports
   generated to the remote server.

#. Cleanup the build directory using
   :ref:`test-manual/test-manual-understand-autobuilder:clobberdir` if the build was successful,
   else rename it to "build-renamed" for potential future debugging.

.. _test-deploying-yp-autobuilder:

Deploying Yocto Autobuilder
===========================

The most up to date information about how to setup and deploy your own
Autbuilder can be found in README.md in the ``yocto-autobuilder2``
repository.

We hope that people can use the ``yocto-autobuilder2`` code directly but
it is inevitable that users will end up needing to heavily customise the
``yocto-autobuilder-helper`` repository, particularly the
``config.json`` file as they will want to define their own test matrix.

The Autobuilder supports wo customization options:

-  variable substitution

-  overlaying configuration files

The standard ``config.json`` minimally attempts to allow substitution of
the paths. The Helper script repository includes a
``local-example.json`` file to show how you could override these from a
separate configuration file. Pass the following into the environment of
the Autobuilder::

   $ ABHELPER_JSON="config.json local-example.json"

As another example, you could also pass the following into the
environment::

   $ ABHELPER_JSON="config.json /some/location/local.json"

One issue users often run into is validation of the ``config.json`` files. A
tip for minimizing issues from invalid json files is to use a Git
``pre-commit-hook.sh`` script to verify the JSON file before committing
it. Create a symbolic link as follows::

   $ ln -s ../../scripts/pre-commit-hook.sh .git/hooks/pre-commit
