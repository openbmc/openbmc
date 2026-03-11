.. SPDX-License-Identifier: CC-BY-2.5

========
Overview
========

|

Welcome to the BitBake User Manual. This manual provides information on
the BitBake tool. The information attempts to be as independent as
possible regarding systems that use BitBake, such as OpenEmbedded and
the Yocto Project. In some cases, scenarios or examples within the
context of a build system are used in the manual to help with
understanding. For these cases, the manual clearly states the context.

.. _intro:

Introduction
============

Fundamentally, BitBake is a generic task execution engine that allows
shell and Python tasks to be run efficiently and in parallel while
working within complex inter-task dependency constraints. One of
BitBake's main users, OpenEmbedded, takes this core and builds embedded
Linux software stacks using a task-oriented approach.

Conceptually, BitBake is similar to GNU Make in some regards but has
significant differences:

-  BitBake executes tasks according to the provided metadata that builds up
   the tasks. Metadata is stored in recipe (``.bb``) and related recipe
   "append" (``.bbappend``) files, configuration (``.conf``) and
   underlying include (``.inc``) files, and in class (``.bbclass``)
   files. The metadata provides BitBake with instructions on what tasks
   to run and the dependencies between those tasks.

-  BitBake includes a fetcher library for obtaining source code from
   various places such as local files, source control systems, or
   websites.

-  The instructions for each unit to be built (e.g. a piece of software)
   are known as "recipe" files and contain all the information about the
   unit (dependencies, source file locations, checksums, description and
   so on).

-  BitBake includes a client/server abstraction and can be used from a
   command line or used as a service over XML-RPC and has several
   different user interfaces.

History and Goals
=================

BitBake was originally a part of the OpenEmbedded project. It was
inspired by the Portage package management system used by the Gentoo
Linux distribution. On December 7, 2004, OpenEmbedded project team
member Chris Larson split the project into two distinct pieces:

-  BitBake, a generic task executor

-  OpenEmbedded, a metadata set utilized by BitBake

Today, BitBake is the primary basis of the
`OpenEmbedded <https://www.openembedded.org/>`__ project, which is being
used to build and maintain Linux distributions such as the `Poky
Reference Distribution <https://www.yoctoproject.org/software-item/poky/>`__,
developed under the umbrella of the `Yocto Project <https://www.yoctoproject.org>`__.

Prior to BitBake, no other build tool adequately met the needs of an
aspiring embedded Linux distribution. All of the build systems used by
traditional desktop Linux distributions lacked important functionality,
and none of the ad hoc Buildroot-based systems, prevalent in the
embedded space, were scalable or maintainable.

Some important original goals for BitBake were:

-  Handle cross-compilation.

-  Handle inter-package dependencies (build time on target architecture,
   build time on native architecture, and runtime).

-  Support running any number of tasks within a given package,
   including, but not limited to, fetching upstream sources, unpacking
   them, patching them, configuring them, and so forth.

-  Be Linux distribution agnostic for both build and target systems.

-  Be architecture agnostic.

-  Support multiple build and target operating systems (e.g. Cygwin, the
   BSDs, and so forth).

-  Be self-contained, rather than tightly integrated into the build
   machine's root filesystem.

-  Handle conditional metadata on the target architecture, operating
   system, distribution, and machine.

-  Be easy to use the tools to supply local metadata and packages
   against which to operate.

-  Be easy to use BitBake to collaborate between multiple projects for
   their builds.

-  Provide an inheritance mechanism to share common metadata between
   many packages.

Over time it became apparent that some further requirements were
necessary:

-  Handle variants of a base recipe (e.g. native, sdk, and multilib).

-  Split metadata into layers and allow layers to enhance or override
   other layers.

-  Allow representation of a given set of input variables to a task as a
   checksum. Based on that checksum, allow acceleration of builds with
   prebuilt components.

BitBake satisfies all the original requirements and many more with
extensions being made to the basic functionality to reflect the
additional requirements. Flexibility and power have always been the
priorities. BitBake is highly extensible and supports embedded Python
code and execution of any arbitrary tasks.

.. _Concepts:

Concepts
========

BitBake is a program written in the Python language. At the highest
level, BitBake interprets metadata, decides what tasks are required to
run, and executes those tasks. Similar to GNU Make, BitBake controls how
software is built. GNU Make achieves its control through "makefiles",
while BitBake uses "recipes".

BitBake extends the capabilities of a simple tool like GNU Make by
allowing for the definition of much more complex tasks, such as
assembling entire embedded Linux distributions.

The remainder of this section introduces several concepts that should be
understood in order to better leverage the power of BitBake.

Recipes
-------

BitBake Recipes, which are denoted by the file extension ``.bb``, are
the most basic metadata files. These recipe files provide BitBake with
the following:

-  Descriptive information about the package (author, homepage, license,
   and so on)

-  The version of the recipe

-  Existing dependencies (both build and runtime dependencies)

-  Where the source code resides and how to fetch it

-  Whether the source code requires any patches, where to find them, and
   how to apply them

-  How to configure and compile the source code

-  How to assemble the generated artifacts into one or more installable
   packages

-  Where on the target machine to install the package or packages
   created

Within the context of BitBake, or any project utilizing BitBake as its
build system, files with the ``.bb`` extension are referred to as
recipes.

.. note::

   The term "package" is also commonly used to describe recipes.
   However, since the same word is used to describe packaged output from
   a project, it is best to maintain a single descriptive term -
   "recipes". Put another way, a single "recipe" file is quite capable
   of generating a number of related but separately installable
   "packages". In fact, that ability is fairly common.

Configuration Files
-------------------

Configuration files, which are denoted by the ``.conf`` extension,
define various configuration variables that govern the project's build
process. These files fall into several areas that define machine
configuration, distribution configuration, possible compiler tuning,
general common configuration, and user configuration. The main
configuration file is the sample ``bitbake.conf`` file, which is located
within the BitBake source tree ``conf`` directory.

Classes
-------

Class files, which are denoted by the ``.bbclass`` extension, contain
information that is useful to share between metadata files. The BitBake
source tree currently comes with one class metadata file called
``base.bbclass``. You can find this file in the ``classes`` directory.
The ``base.bbclass`` class files is special since it is always included
automatically for all recipes and classes. This class contains
definitions for standard basic tasks such as fetching, unpacking,
configuring (empty by default), compiling (runs any Makefile present),
installing (empty by default) and packaging (empty by default). These
tasks are often overridden or extended by other classes added during the
project development process.

Layers
------

Layers allow you to isolate different types of customizations from each
other. While you might find it tempting to keep everything in one layer
when working on a single project, the more modular your metadata, the
easier it is to cope with future changes.

To illustrate how you can use layers to keep things modular, consider
customizations you might make to support a specific target machine.
These types of customizations typically reside in a special layer,
rather than a general layer, called a Board Support Package (BSP) layer.
Furthermore, the machine customizations should be isolated from recipes
and metadata that support a new GUI environment, for example. This
situation gives you a couple of layers: one for the machine
configurations and one for the GUI environment. It is important to
understand, however, that the BSP layer can still make machine-specific
additions to recipes within the GUI environment layer without polluting
the GUI layer itself with those machine-specific changes. You can
accomplish this through a recipe that is a BitBake append
(``.bbappend``) file.

.. _append-bbappend-files:

Append Files
------------

Append files, which are files that have the ``.bbappend`` file
extension, extend or override information in an existing recipe file.

BitBake expects every append file to have a corresponding recipe file.
Furthermore, the append file and corresponding recipe file must use the
same root filename. The filenames can differ only in the file type
suffix used (e.g. ``formfactor_0.0.bb`` and
``formfactor_0.0.bbappend``).

Information in append files extends or overrides the information in the
underlying, similarly-named recipe files.

When you name an append file, you can use the "``%``" wildcard character
to allow for matching recipe names. For example, suppose you have an
append file named as follows::

  busybox_1.21.%.bbappend

That append file
would match any ``busybox_1.21.``\ x\ ``.bb`` version of the recipe. So,
the append file would match the following recipe names::

  busybox_1.21.1.bb
  busybox_1.21.2.bb
  busybox_1.21.3.bb

.. note::

   The use of the " % " character is limited in that it only works directly in
   front of the .bbappend portion of the append file's name. You cannot use the
   wildcard character in any other location of the name.

If the ``busybox`` recipe was updated to ``busybox_1.3.0.bb``, the
append name would not match. However, if you named the append file
``busybox_1.%.bbappend``, then you would have a match.

In the most general case, you could name the append file something as
simple as ``busybox_%.bbappend`` to be entirely version independent.

Obtaining BitBake
=================

You can obtain BitBake several different ways:

-  **Cloning BitBake:** Using Git to clone the BitBake source code
   repository is the recommended method for obtaining BitBake. Cloning
   the repository makes it easy to get bug fixes and have access to
   stable branches and the master branch. Once you have cloned BitBake,
   you should use the latest stable branch for development since the
   master branch is for BitBake development and might contain less
   stable changes.

   You usually need a version of BitBake that matches the metadata you
   are using. The metadata is generally backwards compatible but not
   forward compatible.

   Here is an example that clones the BitBake repository::

     $ git clone git://git.openembedded.org/bitbake

   This command clones the BitBake
   Git repository into a directory called ``bitbake``. Alternatively,
   you can designate a directory after the ``git clone`` command if you
   want to call the new directory something other than ``bitbake``. Here
   is an example that names the directory ``bbdev``::

     $ git clone git://git.openembedded.org/bitbake bbdev

-  **Installation using your Distribution Package Management System:**
   This method is not recommended because the BitBake version that is
   provided by your distribution, in most cases, is several releases
   behind a snapshot of the BitBake repository.

-  **Taking a snapshot of BitBake:** Downloading a snapshot of BitBake
   from the source code repository gives you access to a known branch or
   release of BitBake.

      .. note::

         Cloning the Git repository, as described earlier, is the preferred
         method for getting BitBake. Cloning the repository makes it easier
         to update as patches are added to the stable branches.

   The following example downloads a snapshot of BitBake version 1.17.0::

     $ wget https://git.openembedded.org/bitbake/snapshot/bitbake-1.17.0.tar.gz
     $ tar zxpvf bitbake-1.17.0.tar.gz

   After extraction of the tarball using
   the tar utility, you have a directory entitled ``bitbake-1.17.0``.

-  **Using the BitBake that Comes With Your Build Checkout:** A final
   possibility for getting a copy of BitBake is that it already comes
   with your checkout of a larger BitBake-based build system, such as
   Poky. Rather than manually checking out individual layers and gluing
   them together yourself, you can check out an entire build system. The
   checkout will already include a version of BitBake that has been
   thoroughly tested for compatibility with the other components. For
   information on how to check out a particular BitBake-based build
   system, consult that build system's supporting documentation.

.. _bitbake-user-manual-command:

The BitBake Command
===================

The ``bitbake`` command is the primary interface to the BitBake tool.
This section presents the BitBake command syntax and provides several
execution examples.

Usage and syntax
----------------

Following is the usage and syntax for BitBake::

   $ bitbake -h
   usage: bitbake [-s] [-e] [-g] [-u UI] [--version] [-h] [-f] [-c CMD]
                  [-C INVALIDATE_STAMP] [--runall RUNALL] [--runonly RUNONLY]
                  [--no-setscene] [--skip-setscene] [--setscene-only] [-n] [-p]
                  [-k] [-P] [-S SIGNATURE_HANDLER] [--revisions-changed]
                  [-b BUILDFILE] [-D] [-l DEBUG_DOMAINS] [-v] [-q]
                  [-w WRITEEVENTLOG] [-B BIND] [-T SERVER_TIMEOUT]
                  [--remote-server REMOTE_SERVER] [-m] [--token XMLRPCTOKEN]
                  [--observe-only] [--status-only] [--server-only] [-r PREFILE]
                  [-R POSTFILE] [-I EXTRA_ASSUME_PROVIDED]
                  [recipename/target ...]

   It is assumed there is a conf/bblayers.conf available in cwd or in BBPATH
   which will provide the layer, BBFILES and other configuration information.

   General options:
     recipename/target     Execute the specified task (default is 'build') for
                           these target recipes (.bb files).
     -s, --show-versions   Show current and preferred versions of all recipes.
     -e, --environment     Show the global or per-recipe environment complete
                           with information about where variables were
                           set/changed.
     -g, --graphviz        Save dependency tree information for the specified
                           targets in the dot syntax.
     -u UI, --ui UI        The user interface to use (knotty, ncurses, taskexp,
                           taskexp_ncurses or teamcity - default knotty).
     --version             Show programs version and exit.
     -h, --help            Show this help message and exit.

   Task control options:
     -f, --force           Force the specified targets/task to run (invalidating
                           any existing stamp file).
     -c CMD, --cmd CMD     Specify the task to execute. The exact options
                           available depend on the metadata. Some examples might
                           be 'compile' or 'populate_sysroot' or 'listtasks' may
                           give a list of the tasks available.
     -C INVALIDATE_STAMP, --clear-stamp INVALIDATE_STAMP
                           Invalidate the stamp for the specified task such as
                           'compile' and then run the default task for the
                           specified target(s).
     --runall RUNALL       Run the specified task for any recipe in the taskgraph
                           of the specified target (even if it wouldn't otherwise
                           have run).
     --runonly RUNONLY     Run only the specified task within the taskgraph of
                           the specified targets (and any task dependencies those
                           tasks may have).
     --no-setscene         Do not run any setscene tasks. sstate will be ignored
                           and everything needed, built.
     --skip-setscene       Skip setscene tasks if they would be executed. Tasks
                           previously restored from sstate will be kept, unlike
                           --no-setscene.
     --setscene-only       Only run setscene tasks, don't run any real tasks.

   Execution control options:
     -n, --dry-run         Don't execute, just go through the motions.
     -p, --parse-only      Quit after parsing the BB recipes.
     -k, --continue        Continue as much as possible after an error. While the
                           target that failed and anything depending on it cannot
                           be built, as much as possible will be built before
                           stopping.
     -P, --profile         Profile the command and save reports.
     -S SIGNATURE_HANDLER, --dump-signatures SIGNATURE_HANDLER
                           Dump out the signature construction information, with
                           no task execution. The SIGNATURE_HANDLER parameter is
                           passed to the handler. Two common values are none and
                           printdiff but the handler may define more/less. none
                           means only dump the signature, printdiff means
                           recursively compare the dumped signature with the most
                           recent one in a local build or sstate cache (can be
                           used to find out why tasks re-run when that is not
                           expected)
     --revisions-changed   Set the exit code depending on whether upstream
                           floating revisions have changed or not.
     -b BUILDFILE, --buildfile BUILDFILE
                           Execute tasks from a specific .bb recipe directly.
                           WARNING: Does not handle any dependencies from other
                           recipes.

   Logging/output control options:
     -D, --debug           Increase the debug level. You can specify this more
                           than once. -D sets the debug level to 1, where only
                           bb.debug(1, ...) messages are printed to stdout; -DD
                           sets the debug level to 2, where both bb.debug(1, ...)
                           and bb.debug(2, ...) messages are printed; etc.
                           Without -D, no debug messages are printed. Note that
                           -D only affects output to stdout. All debug messages
                           are written to ${T}/log.do_taskname, regardless of the
                           debug level.
     -l DEBUG_DOMAINS, --log-domains DEBUG_DOMAINS
                           Show debug logging for the specified logging domains.
     -v, --verbose         Enable tracing of shell tasks (with 'set -x'). Also
                           print bb.note(...) messages to stdout (in addition to
                           writing them to ${T}/log.do_<task>).
     -q, --quiet           Output less log message data to the terminal. You can
                           specify this more than once.
     -w WRITEEVENTLOG, --write-log WRITEEVENTLOG
                           Writes the event log of the build to a bitbake event
                           json file. Use '' (empty string) to assign the name
                           automatically.

   Server options:
     -B BIND, --bind BIND  The name/address for the bitbake xmlrpc server to bind
                           to.
     -T SERVER_TIMEOUT, --idle-timeout SERVER_TIMEOUT
                           Set timeout to unload bitbake server due to
                           inactivity, set to -1 means no unload, default:
                           Environment variable BB_SERVER_TIMEOUT.
     --remote-server REMOTE_SERVER
                           Connect to the specified server.
     -m, --kill-server     Terminate any running bitbake server.
     --token XMLRPCTOKEN   Specify the connection token to be used when
                           connecting to a remote server.
     --observe-only        Connect to a server as an observing-only client.
     --status-only         Check the status of the remote bitbake server.
     --server-only         Run bitbake without a UI, only starting a server
                           (cooker) process.

   Configuration options:
     -r PREFILE, --read PREFILE
                           Read the specified file before bitbake.conf.
     -R POSTFILE, --postread POSTFILE
                           Read the specified file after bitbake.conf.
     -I EXTRA_ASSUME_PROVIDED, --ignore-deps EXTRA_ASSUME_PROVIDED
                           Assume these dependencies don't exist and are already
                           provided (equivalent to ASSUME_PROVIDED). Useful to
                           make dependency graphs more appealing.

..
    Bitbake help output generated with "stty columns 80; bin/bitbake -h"

.. _bitbake-examples:

Examples
--------

This section presents some examples showing how to use BitBake.

.. _example-executing-a-task-against-a-single-recipe:

Executing a Task Against a Single Recipe
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Executing tasks for a single recipe file is relatively simple. You
specify the file in question, and BitBake parses it and executes the
specified task. If you do not specify a task, BitBake executes the
default task, which is "build". BitBake obeys inter-task dependencies
when doing so.

The following command runs the build task, which is the default task, on
the ``foo_1.0.bb`` recipe file::

  $ bitbake -b foo_1.0.bb

The following command runs the clean task on the ``foo.bb`` recipe file::

  $ bitbake -b foo.bb -c clean

.. note::

   The "-b" option explicitly does not handle recipe dependencies. Other
   than for debugging purposes, it is instead recommended that you use
   the syntax presented in the next section.

Executing Tasks Against a Set of Recipe Files
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

There are a number of additional complexities introduced when one wants
to manage multiple ``.bb`` files. Clearly there needs to be a way to
tell BitBake what files are available and, of those, which you want to
execute. There also needs to be a way for each recipe to express its
dependencies, both for build-time and runtime. There must be a way for
you to express recipe preferences when multiple recipes provide the same
functionality, or when there are multiple versions of a recipe.

The ``bitbake`` command, when not using "--buildfile" or "-b" only
accepts a "PROVIDES". You cannot provide anything else. By default, a
recipe file generally "PROVIDES" its "packagename" as shown in the
following example::

  $ bitbake foo

This next example "PROVIDES" the
package name and also uses the "-c" option to tell BitBake to just
execute the ``do_clean`` task::

  $ bitbake -c clean foo

Executing a List of Task and Recipe Combinations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The BitBake command line supports specifying different tasks for
individual targets when you specify multiple targets. For example,
suppose you had two targets (or recipes) ``myfirstrecipe`` and
``mysecondrecipe`` and you needed BitBake to run ``taskA`` for the first
recipe and ``taskB`` for the second recipe::

  $ bitbake myfirstrecipe:do_taskA mysecondrecipe:do_taskB

Generating Dependency Graphs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

BitBake is able to generate dependency graphs using the ``dot`` syntax.
You can convert these graphs into images using the ``dot`` tool from
`Graphviz <http://www.graphviz.org>`__.

When you generate a dependency graph, BitBake writes two files to the
current working directory:

-  ``task-depends.dot``: Shows dependencies between tasks. These
   dependencies match BitBake's internal task execution list.

-  ``pn-buildlist``: Shows a simple list of targets that are to be
   built.

To stop depending on common depends, use the ``-I`` depend option and
BitBake omits them from the graph. Leaving this information out can
produce more readable graphs. This way, you can remove from the graph
:term:`DEPENDS` from inherited classes such as ``base.bbclass``.

Here are two examples that create dependency graphs. The second example
omits depends common in OpenEmbedded from the graph::

  $ bitbake -g foo

  $ bitbake -g -I virtual/kernel -I eglibc foo

Executing a Multiple Configuration Build
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

BitBake is able to build multiple images or packages using a single
command where the different targets require different configurations
(multiple configuration builds). Each target, in this scenario, is
referred to as a "multiconfig".

To accomplish a multiple configuration build, you must define each
target's configuration separately using a parallel configuration file in
the build directory. The location for these multiconfig configuration
files is specific. They must reside in the current build directory in a
sub-directory of ``conf`` named ``multiconfig``. Following is an example
for two separate targets:

.. image:: figures/bb_multiconfig_files.png
   :align: center

The reason for this required file hierarchy is because the :term:`BBPATH`
variable is not constructed until the layers are parsed. Consequently,
using the configuration file as a pre-configuration file is not possible
unless it is located in the current working directory.

Minimally, each configuration file must define the machine and the
temporary directory BitBake uses for the build. Suggested practice
dictates that you do not overlap the temporary directories used during
the builds.

Aside from separate configuration files for each target, you must also
enable BitBake to perform multiple configuration builds. Enabling is
accomplished by setting the
:term:`BBMULTICONFIG` variable in the
``local.conf`` configuration file. As an example, suppose you had
configuration files for ``target1`` and ``target2`` defined in the build
directory. The following statement in the ``local.conf`` file both
enables BitBake to perform multiple configuration builds and specifies
the two extra multiconfigs::

  BBMULTICONFIG = "target1 target2"

Once the target configuration files are in place and BitBake has been
enabled to perform multiple configuration builds, use the following
command form to start the builds::

  $ bitbake [mc:multiconfigname:]target [[[mc:multiconfigname:]target] ... ]

Here is an example for two extra multiconfigs: ``target1`` and ``target2``::

  $ bitbake mc::target mc:target1:target mc:target2:target

.. _bb-enabling-multiple-configuration-build-dependencies:

Enabling Multiple Configuration Build Dependencies
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Sometimes dependencies can exist between targets (multiconfigs) in a
multiple configuration build. For example, suppose that in order to
build an image for a particular architecture, the root filesystem of
another build for a different architecture needs to exist. In other
words, the image for the first multiconfig depends on the root
filesystem of the second multiconfig. This dependency is essentially
that the task in the recipe that builds one multiconfig is dependent on
the completion of the task in the recipe that builds another
multiconfig.

To enable dependencies in a multiple configuration build, you must
declare the dependencies in the recipe using the following statement
form::

  task_or_package[mcdepends] = "mc:from_multiconfig:to_multiconfig:recipe_name:task_on_which_to_depend"

To better show how to use this statement, consider an example with two
multiconfigs: ``target1`` and ``target2``::

  image_task[mcdepends] = "mc:target1:target2:image2:rootfs_task"

In this example, the
``from_multiconfig`` is "target1" and the ``to_multiconfig`` is "target2". The
task on which the image whose recipe contains image_task depends on the
completion of the rootfs_task used to build out image2, which is
associated with the "target2" multiconfig.

Once you set up this dependency, you can build the "target1" multiconfig
using a BitBake command as follows::

  $ bitbake mc:target1:image1

This command executes all the tasks needed to create ``image1`` for the "target1"
multiconfig. Because of the dependency, BitBake also executes through
the ``rootfs_task`` for the "target2" multiconfig build.

Having a recipe depend on the root filesystem of another build might not
seem that useful. Consider this change to the statement in the image1
recipe::

  image_task[mcdepends] = "mc:target1:target2:image2:image_task"

In this case, BitBake must create ``image2`` for the "target2" build since
the "target1" build depends on it.

Because "target1" and "target2" are enabled for multiple configuration
builds and have separate configuration files, BitBake places the
artifacts for each build in the respective temporary build directories.
