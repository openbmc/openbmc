.. SPDX-License-Identifier: CC-BY-2.5

=========
Execution
=========

|

The primary purpose for running BitBake is to produce some kind of
output such as a single installable package, a kernel, a software
development kit, or even a full, board-specific bootable Linux image,
complete with bootloader, kernel, and root filesystem. Of course, you
can execute the ``bitbake`` command with options that cause it to
execute single tasks, compile single recipe files, capture or clear
data, or simply return information about the execution environment.

This chapter describes BitBake's execution process from start to finish
when you use it to create an image. The execution process is launched
using the following command form: ::

  $ bitbake target

For information on
the BitBake command and its options, see ":ref:`The BitBake Command
<bitbake-user-manual-command>`" section.

.. note::

   Prior to executing BitBake, you should take advantage of available
   parallel thread execution on your build host by setting the
   :term:`BB_NUMBER_THREADS` variable in
   your project's ``local.conf`` configuration file.

   A common method to determine this value for your build host is to run
   the following: ::

     $ grep processor /proc/cpuinfo

   This command returns
   the number of processors, which takes into account hyper-threading.
   Thus, a quad-core build host with hyper-threading most likely shows
   eight processors, which is the value you would then assign to
   ``BB_NUMBER_THREADS``.

   A possibly simpler solution is that some Linux distributions (e.g.
   Debian and Ubuntu) provide the ``ncpus`` command.

Parsing the Base Configuration Metadata
=======================================

The first thing BitBake does is parse base configuration metadata. Base
configuration metadata consists of your project's ``bblayers.conf`` file
to determine what layers BitBake needs to recognize, all necessary
``layer.conf`` files (one from each layer), and ``bitbake.conf``. The
data itself is of various types:

-  **Recipes:** Details about particular pieces of software.

-  **Class Data:** An abstraction of common build information (e.g. how to
   build a Linux kernel).

-  **Configuration Data:** Machine-specific settings, policy decisions,
   and so forth. Configuration data acts as the glue to bind everything
   together.

The ``layer.conf`` files are used to construct key variables such as
:term:`BBPATH` and :term:`BBFILES`.
``BBPATH`` is used to search for configuration and class files under the
``conf`` and ``classes`` directories, respectively. ``BBFILES`` is used
to locate both recipe and recipe append files (``.bb`` and
``.bbappend``). If there is no ``bblayers.conf`` file, it is assumed the
user has set the ``BBPATH`` and ``BBFILES`` directly in the environment.

Next, the ``bitbake.conf`` file is located using the ``BBPATH`` variable
that was just constructed. The ``bitbake.conf`` file may also include
other configuration files using the ``include`` or ``require``
directives.

Prior to parsing configuration files, BitBake looks at certain
variables, including:

-  :term:`BB_ENV_WHITELIST`
-  :term:`BB_ENV_EXTRAWHITE`
-  :term:`BB_PRESERVE_ENV`
-  :term:`BB_ORIGENV`
-  :term:`BITBAKE_UI`

The first four variables in this list relate to how BitBake treats shell
environment variables during task execution. By default, BitBake cleans
the environment variables and provides tight control over the shell
execution environment. However, through the use of these first four
variables, you can apply your control regarding the environment
variables allowed to be used by BitBake in the shell during execution of
tasks. See the
":ref:`bitbake-user-manual/bitbake-user-manual-metadata:Passing Information Into the Build Task Environment`"
section and the information about these variables in the variable
glossary for more information on how they work and on how to use them.

The base configuration metadata is global and therefore affects all
recipes and tasks that are executed.

BitBake first searches the current working directory for an optional
``conf/bblayers.conf`` configuration file. This file is expected to
contain a :term:`BBLAYERS` variable that is a
space-delimited list of 'layer' directories. Recall that if BitBake
cannot find a ``bblayers.conf`` file, then it is assumed the user has
set the ``BBPATH`` and ``BBFILES`` variables directly in the
environment.

For each directory (layer) in this list, a ``conf/layer.conf`` file is
located and parsed with the :term:`LAYERDIR` variable
being set to the directory where the layer was found. The idea is these
files automatically set up :term:`BBPATH` and other
variables correctly for a given build directory.

BitBake then expects to find the ``conf/bitbake.conf`` file somewhere in
the user-specified ``BBPATH``. That configuration file generally has
include directives to pull in any other metadata such as files specific
to the architecture, the machine, the local environment, and so forth.

Only variable definitions and include directives are allowed in BitBake
``.conf`` files. Some variables directly influence BitBake's behavior.
These variables might have been set from the environment depending on
the environment variables previously mentioned or set in the
configuration files. The ":ref:`bitbake-user-manual/bitbake-user-manual-ref-variables:Variables Glossary`"
chapter presents a full list of
variables.

After parsing configuration files, BitBake uses its rudimentary
inheritance mechanism, which is through class files, to inherit some
standard classes. BitBake parses a class when the inherit directive
responsible for getting that class is encountered.

The ``base.bbclass`` file is always included. Other classes that are
specified in the configuration using the
:term:`INHERIT` variable are also included. BitBake
searches for class files in a ``classes`` subdirectory under the paths
in ``BBPATH`` in the same way as configuration files.

A good way to get an idea of the configuration files and the class files
used in your execution environment is to run the following BitBake
command: ::

  $ bitbake -e > mybb.log

Examining the top of the ``mybb.log``
shows you the many configuration files and class files used in your
execution environment.

.. note::

   You need to be aware of how BitBake parses curly braces. If a recipe
   uses a closing curly brace within the function and the character has
   no leading spaces, BitBake produces a parsing error. If you use a
   pair of curly braces in a shell function, the closing curly brace
   must not be located at the start of the line without leading spaces.

   Here is an example that causes BitBake to produce a parsing error: ::

      fakeroot create_shar() {
         cat << "EOF" > ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh
      usage()
      {
         echo "test"
         ######  The following "}" at the start of the line causes a parsing error ######
      }
      EOF
      }

      Writing the recipe this way avoids the error:
      fakeroot create_shar() {
         cat << "EOF" > ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh
      usage()
      {
         echo "test"
         ###### The following "}" with a leading space at the start of the line avoids the error ######
       }
      EOF
      }

Locating and Parsing Recipes
============================

During the configuration phase, BitBake will have set
:term:`BBFILES`. BitBake now uses it to construct a
list of recipes to parse, along with any append files (``.bbappend``) to
apply. ``BBFILES`` is a space-separated list of available files and
supports wildcards. An example would be: ::

  BBFILES = "/path/to/bbfiles/*.bb /path/to/appends/*.bbappend"

BitBake parses each
recipe and append file located with ``BBFILES`` and stores the values of
various variables into the datastore.

.. note::

   Append files are applied in the order they are encountered in BBFILES.

For each file, a fresh copy of the base configuration is made, then the
recipe is parsed line by line. Any inherit statements cause BitBake to
find and then parse class files (``.bbclass``) using
:term:`BBPATH` as the search path. Finally, BitBake
parses in order any append files found in ``BBFILES``.

One common convention is to use the recipe filename to define pieces of
metadata. For example, in ``bitbake.conf`` the recipe name and version
are used to set the variables :term:`PN` and
:term:`PV`: ::

   PN = "${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[0] or 'defaultpkgname'}"
   PV = "${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1] or '1.0'}"

In this example, a recipe called "something_1.2.3.bb" would set
``PN`` to "something" and ``PV`` to "1.2.3".

By the time parsing is complete for a recipe, BitBake has a list of
tasks that the recipe defines and a set of data consisting of keys and
values as well as dependency information about the tasks.

BitBake does not need all of this information. It only needs a small
subset of the information to make decisions about the recipe.
Consequently, BitBake caches the values in which it is interested and
does not store the rest of the information. Experience has shown it is
faster to re-parse the metadata than to try and write it out to the disk
and then reload it.

Where possible, subsequent BitBake commands reuse this cache of recipe
information. The validity of this cache is determined by first computing
a checksum of the base configuration data (see
:term:`BB_HASHCONFIG_WHITELIST`) and
then checking if the checksum matches. If that checksum matches what is
in the cache and the recipe and class files have not changed, BitBake is
able to use the cache. BitBake then reloads the cached information about
the recipe instead of reparsing it from scratch.

Recipe file collections exist to allow the user to have multiple
repositories of ``.bb`` files that contain the same exact package. For
example, one could easily use them to make one's own local copy of an
upstream repository, but with custom modifications that one does not
want upstream. Here is an example: ::

  BBFILES = "/stuff/openembedded/*/*.bb /stuff/openembedded.modified/*/*.bb"
  BBFILE_COLLECTIONS = "upstream local"
  BBFILE_PATTERN_upstream = "^/stuff/openembedded/"
  BBFILE_PATTERN_local = "^/stuff/openembedded.modified/"
  BBFILE_PRIORITY_upstream = "5" BBFILE_PRIORITY_local = "10"

.. note::

   The layers mechanism is now the preferred method of collecting code.
   While the collections code remains, its main use is to set layer
   priorities and to deal with overlap (conflicts) between layers.

.. _bb-bitbake-providers:

Providers
=========

Assuming BitBake has been instructed to execute a target and that all
the recipe files have been parsed, BitBake starts to figure out how to
build the target. BitBake looks through the ``PROVIDES`` list for each
of the recipes. A ``PROVIDES`` list is the list of names by which the
recipe can be known. Each recipe's ``PROVIDES`` list is created
implicitly through the recipe's :term:`PN` variable and
explicitly through the recipe's :term:`PROVIDES`
variable, which is optional.

When a recipe uses ``PROVIDES``, that recipe's functionality can be
found under an alternative name or names other than the implicit ``PN``
name. As an example, suppose a recipe named ``keyboard_1.0.bb``
contained the following: ::

  PROVIDES += "fullkeyboard"

The ``PROVIDES``
list for this recipe becomes "keyboard", which is implicit, and
"fullkeyboard", which is explicit. Consequently, the functionality found
in ``keyboard_1.0.bb`` can be found under two different names.

.. _bb-bitbake-preferences:

Preferences
===========

The ``PROVIDES`` list is only part of the solution for figuring out a
target's recipes. Because targets might have multiple providers, BitBake
needs to prioritize providers by determining provider preferences.

A common example in which a target has multiple providers is
"virtual/kernel", which is on the ``PROVIDES`` list for each kernel
recipe. Each machine often selects the best kernel provider by using a
line similar to the following in the machine configuration file: ::

  PREFERRED_PROVIDER_virtual/kernel = "linux-yocto"

The default :term:`PREFERRED_PROVIDER` is the provider
with the same name as the target. BitBake iterates through each target
it needs to build and resolves them and their dependencies using this
process.

Understanding how providers are chosen is made complicated by the fact
that multiple versions might exist for a given provider. BitBake
defaults to the highest version of a provider. Version comparisons are
made using the same method as Debian. You can use the
:term:`PREFERRED_VERSION` variable to
specify a particular version. You can influence the order by using the
:term:`DEFAULT_PREFERENCE` variable.

By default, files have a preference of "0". Setting
``DEFAULT_PREFERENCE`` to "-1" makes the recipe unlikely to be used
unless it is explicitly referenced. Setting ``DEFAULT_PREFERENCE`` to
"1" makes it likely the recipe is used. ``PREFERRED_VERSION`` overrides
any ``DEFAULT_PREFERENCE`` setting. ``DEFAULT_PREFERENCE`` is often used
to mark newer and more experimental recipe versions until they have
undergone sufficient testing to be considered stable.

When there are multiple "versions" of a given recipe, BitBake defaults
to selecting the most recent version, unless otherwise specified. If the
recipe in question has a
:term:`DEFAULT_PREFERENCE` set lower than
the other recipes (default is 0), then it will not be selected. This
allows the person or persons maintaining the repository of recipe files
to specify their preference for the default selected version.
Additionally, the user can specify their preferred version.

If the first recipe is named ``a_1.1.bb``, then the
:term:`PN` variable will be set to "a", and the
:term:`PV` variable will be set to 1.1.

Thus, if a recipe named ``a_1.2.bb`` exists, BitBake will choose 1.2 by
default. However, if you define the following variable in a ``.conf``
file that BitBake parses, you can change that preference: ::

  PREFERRED_VERSION_a = "1.1"

.. note::

   It is common for a recipe to provide two versions -- a stable,
   numbered (and preferred) version, and a version that is automatically
   checked out from a source code repository that is considered more
   "bleeding edge" but can be selected only explicitly.

   For example, in the OpenEmbedded codebase, there is a standard,
   versioned recipe file for BusyBox, ``busybox_1.22.1.bb``, but there
   is also a Git-based version, ``busybox_git.bb``, which explicitly
   contains the line ::

     DEFAULT_PREFERENCE = "-1"

   to ensure that the
   numbered, stable version is always preferred unless the developer
   selects otherwise.

.. _bb-bitbake-dependencies:

Dependencies
============

Each target BitBake builds consists of multiple tasks such as ``fetch``,
``unpack``, ``patch``, ``configure``, and ``compile``. For best
performance on multi-core systems, BitBake considers each task as an
independent entity with its own set of dependencies.

Dependencies are defined through several variables. You can find
information about variables BitBake uses in the
:doc:`bitbake-user-manual-ref-variables` near the end of this manual. At a
basic level, it is sufficient to know that BitBake uses the
:term:`DEPENDS` and
:term:`RDEPENDS` variables when calculating
dependencies.

For more information on how BitBake handles dependencies, see the
:ref:`bitbake-user-manual/bitbake-user-manual-metadata:Dependencies`
section.

.. _ref-bitbake-tasklist:

The Task List
=============

Based on the generated list of providers and the dependency information,
BitBake can now calculate exactly what tasks it needs to run and in what
order it needs to run them. The
:ref:`bitbake-user-manual/bitbake-user-manual-execution:executing tasks`
section has more information on how BitBake chooses which task to
execute next.

The build now starts with BitBake forking off threads up to the limit
set in the :term:`BB_NUMBER_THREADS`
variable. BitBake continues to fork threads as long as there are tasks
ready to run, those tasks have all their dependencies met, and the
thread threshold has not been exceeded.

It is worth noting that you can greatly speed up the build time by
properly setting the ``BB_NUMBER_THREADS`` variable.

As each task completes, a timestamp is written to the directory
specified by the :term:`STAMP` variable. On subsequent
runs, BitBake looks in the build directory within ``tmp/stamps`` and
does not rerun tasks that are already completed unless a timestamp is
found to be invalid. Currently, invalid timestamps are only considered
on a per recipe file basis. So, for example, if the configure stamp has
a timestamp greater than the compile timestamp for a given target, then
the compile task would rerun. Running the compile task again, however,
has no effect on other providers that depend on that target.

The exact format of the stamps is partly configurable. In modern
versions of BitBake, a hash is appended to the stamp so that if the
configuration changes, the stamp becomes invalid and the task is
automatically rerun. This hash, or signature used, is governed by the
signature policy that is configured (see the
:ref:`bitbake-user-manual/bitbake-user-manual-execution:checksums (signatures)`
section for information). It is also
possible to append extra metadata to the stamp using the
``[stamp-extra-info]`` task flag. For example, OpenEmbedded uses this
flag to make some tasks machine-specific.

.. note::

   Some tasks are marked as "nostamp" tasks. No timestamp file is
   created when these tasks are run. Consequently, "nostamp" tasks are
   always rerun.

For more information on tasks, see the
:ref:`bitbake-user-manual/bitbake-user-manual-metadata:tasks` section.

Executing Tasks
===============

Tasks can be either a shell task or a Python task. For shell tasks,
BitBake writes a shell script to
``${``\ :term:`T`\ ``}/run.do_taskname.pid`` and then
executes the script. The generated shell script contains all the
exported variables, and the shell functions with all variables expanded.
Output from the shell script goes to the file
``${T}/log.do_taskname.pid``. Looking at the expanded shell functions in
the run file and the output in the log files is a useful debugging
technique.

For Python tasks, BitBake executes the task internally and logs
information to the controlling terminal. Future versions of BitBake will
write the functions to files similar to the way shell tasks are handled.
Logging will be handled in a way similar to shell tasks as well.

The order in which BitBake runs the tasks is controlled by its task
scheduler. It is possible to configure the scheduler and define custom
implementations for specific use cases. For more information, see these
variables that control the behavior:

-  :term:`BB_SCHEDULER`

-  :term:`BB_SCHEDULERS`

It is possible to have functions run before and after a task's main
function. This is done using the ``[prefuncs]`` and ``[postfuncs]``
flags of the task that lists the functions to run.

.. _checksums:

Checksums (Signatures)
======================

A checksum is a unique signature of a task's inputs. The signature of a
task can be used to determine if a task needs to be run. Because it is a
change in a task's inputs that triggers running the task, BitBake needs
to detect all the inputs to a given task. For shell tasks, this turns
out to be fairly easy because BitBake generates a "run" shell script for
each task and it is possible to create a checksum that gives you a good
idea of when the task's data changes.

To complicate the problem, some things should not be included in the
checksum. First, there is the actual specific build path of a given task
- the working directory. It does not matter if the working directory
changes because it should not affect the output for target packages. The
simplistic approach for excluding the working directory is to set it to
some fixed value and create the checksum for the "run" script. BitBake
goes one step better and uses the
:term:`BB_HASHBASE_WHITELIST` variable
to define a list of variables that should never be included when
generating the signatures.

Another problem results from the "run" scripts containing functions that
might or might not get called. The incremental build solution contains
code that figures out dependencies between shell functions. This code is
used to prune the "run" scripts down to the minimum set, thereby
alleviating this problem and making the "run" scripts much more readable
as a bonus.

So far we have solutions for shell scripts. What about Python tasks? The
same approach applies even though these tasks are more difficult. The
process needs to figure out what variables a Python function accesses
and what functions it calls. Again, the incremental build solution
contains code that first figures out the variable and function
dependencies, and then creates a checksum for the data used as the input
to the task.

Like the working directory case, situations exist where dependencies
should be ignored. For these cases, you can instruct the build process
to ignore a dependency by using a line like the following: ::

  PACKAGE_ARCHS[vardepsexclude] = "MACHINE"

This example ensures that the
``PACKAGE_ARCHS`` variable does not depend on the value of ``MACHINE``,
even if it does reference it.

Equally, there are cases where we need to add dependencies BitBake is
not able to find. You can accomplish this by using a line like the
following: ::

  PACKAGE_ARCHS[vardeps] = "MACHINE"

This example explicitly
adds the ``MACHINE`` variable as a dependency for ``PACKAGE_ARCHS``.

Consider a case with in-line Python, for example, where BitBake is not
able to figure out dependencies. When running in debug mode (i.e. using
``-DDD``), BitBake produces output when it discovers something for which
it cannot figure out dependencies.

Thus far, this section has limited discussion to the direct inputs into
a task. Information based on direct inputs is referred to as the
"basehash" in the code. However, there is still the question of a task's
indirect inputs - the things that were already built and present in the
build directory. The checksum (or signature) for a particular task needs
to add the hashes of all the tasks on which the particular task depends.
Choosing which dependencies to add is a policy decision. However, the
effect is to generate a master checksum that combines the basehash and
the hashes of the task's dependencies.

At the code level, there are a variety of ways both the basehash and the
dependent task hashes can be influenced. Within the BitBake
configuration file, we can give BitBake some extra information to help
it construct the basehash. The following statement effectively results
in a list of global variable dependency excludes - variables never
included in any checksum. This example uses variables from OpenEmbedded
to help illustrate the concept: ::

   BB_HASHBASE_WHITELIST ?= "TMPDIR FILE PATH PWD BB_TASKHASH BBPATH DL_DIR \
       SSTATE_DIR THISDIR FILESEXTRAPATHS FILE_DIRNAME HOME LOGNAME SHELL \
       USER FILESPATH STAGING_DIR_HOST STAGING_DIR_TARGET COREBASE PRSERV_HOST \
       PRSERV_DUMPDIR PRSERV_DUMPFILE PRSERV_LOCKDOWN PARALLEL_MAKE \
       CCACHE_DIR EXTERNAL_TOOLCHAIN CCACHE CCACHE_DISABLE LICENSE_PATH SDKPKGSUFFIX"

The previous example excludes the work directory, which is part of
``TMPDIR``.

The rules for deciding which hashes of dependent tasks to include
through dependency chains are more complex and are generally
accomplished with a Python function. The code in
``meta/lib/oe/sstatesig.py`` shows two examples of this and also
illustrates how you can insert your own policy into the system if so
desired. This file defines the two basic signature generators
OpenEmbedded-Core uses: "OEBasic" and "OEBasicHash". By default, there
is a dummy "noop" signature handler enabled in BitBake. This means that
behavior is unchanged from previous versions. ``OE-Core`` uses the
"OEBasicHash" signature handler by default through this setting in the
``bitbake.conf`` file: ::

  BB_SIGNATURE_HANDLER ?= "OEBasicHash"

The "OEBasicHash" ``BB_SIGNATURE_HANDLER`` is the same as the "OEBasic"
version but adds the task hash to the stamp files. This results in any
metadata change that changes the task hash, automatically causing the
task to be run again. This removes the need to bump
:term:`PR` values, and changes to metadata automatically
ripple across the build.

It is also worth noting that the end result of these signature
generators is to make some dependency and hash information available to
the build. This information includes:

-  ``BB_BASEHASH_task-``\ *taskname*: The base hashes for each task in the
   recipe.

-  ``BB_BASEHASH_``\ *filename:taskname*: The base hashes for each
   dependent task.

-  ``BBHASHDEPS_``\ *filename:taskname*: The task dependencies for
   each task.

-  ``BB_TASKHASH``: The hash of the currently running task.

It is worth noting that BitBake's "-S" option lets you debug BitBake's
processing of signatures. The options passed to -S allow different
debugging modes to be used, either using BitBake's own debug functions
or possibly those defined in the metadata/signature handler itself. The
simplest parameter to pass is "none", which causes a set of signature
information to be written out into ``STAMPS_DIR`` corresponding to the
targets specified. The other currently available parameter is
"printdiff", which causes BitBake to try to establish the closest
signature match it can (e.g. in the sstate cache) and then run
``bitbake-diffsigs`` over the matches to determine the stamps and delta
where these two stamp trees diverge.

.. note::

   It is likely that future versions of BitBake will provide other
   signature handlers triggered through additional "-S" parameters.

You can find more information on checksum metadata in the
:ref:`bitbake-user-manual/bitbake-user-manual-metadata:task checksums and setscene`
section.

Setscene
========

The setscene process enables BitBake to handle "pre-built" artifacts.
The ability to handle and reuse these artifacts allows BitBake the
luxury of not having to build something from scratch every time.
Instead, BitBake can use, when possible, existing build artifacts.

BitBake needs to have reliable data indicating whether or not an
artifact is compatible. Signatures, described in the previous section,
provide an ideal way of representing whether an artifact is compatible.
If a signature is the same, an object can be reused.

If an object can be reused, the problem then becomes how to replace a
given task or set of tasks with the pre-built artifact. BitBake solves
the problem with the "setscene" process.

When BitBake is asked to build a given target, before building anything,
it first asks whether cached information is available for any of the
targets it's building, or any of the intermediate targets. If cached
information is available, BitBake uses this information instead of
running the main tasks.

BitBake first calls the function defined by the
:term:`BB_HASHCHECK_FUNCTION` variable
with a list of tasks and corresponding hashes it wants to build. This
function is designed to be fast and returns a list of the tasks for
which it believes in can obtain artifacts.

Next, for each of the tasks that were returned as possibilities, BitBake
executes a setscene version of the task that the possible artifact
covers. Setscene versions of a task have the string "_setscene" appended
to the task name. So, for example, the task with the name ``xxx`` has a
setscene task named ``xxx_setscene``. The setscene version of the task
executes and provides the necessary artifacts returning either success
or failure.

As previously mentioned, an artifact can cover more than one task. For
example, it is pointless to obtain a compiler if you already have the
compiled binary. To handle this, BitBake calls the
:term:`BB_SETSCENE_DEPVALID` function for
each successful setscene task to know whether or not it needs to obtain
the dependencies of that task.

Finally, after all the setscene tasks have executed, BitBake calls the
function listed in
:term:`BB_SETSCENE_VERIFY_FUNCTION2`
with the list of tasks BitBake thinks has been "covered". The metadata
can then ensure that this list is correct and can inform BitBake that it
wants specific tasks to be run regardless of the setscene result.

You can find more information on setscene metadata in the
:ref:`bitbake-user-manual/bitbake-user-manual-metadata:task checksums and setscene`
section.

Logging
=======

In addition to the standard command line option to control how verbose
builds are when execute, bitbake also supports user defined
configuration of the `Python
logging <https://docs.python.org/3/library/logging.html>`__ facilities
through the :term:`BB_LOGCONFIG` variable. This
variable defines a json or yaml `logging
configuration <https://docs.python.org/3/library/logging.config.html>`__
that will be intelligently merged into the default configuration. The
logging configuration is merged using the following rules:

-  The user defined configuration will completely replace the default
   configuration if top level key ``bitbake_merge`` is set to the value
   ``False``. In this case, all other rules are ignored.

-  The user configuration must have a top level ``version`` which must
   match the value of the default configuration.

-  Any keys defined in the ``handlers``, ``formatters``, or ``filters``,
   will be merged into the same section in the default configuration,
   with the user specified keys taking replacing a default one if there
   is a conflict. In practice, this means that if both the default
   configuration and user configuration specify a handler named
   ``myhandler``, the user defined one will replace the default. To
   prevent the user from inadvertently replacing a default handler,
   formatter, or filter, all of the default ones are named with a prefix
   of "``BitBake.``"

-  If a logger is defined by the user with the key ``bitbake_merge`` set
   to ``False``, that logger will be completely replaced by user
   configuration. In this case, no other rules will apply to that
   logger.

-  All user defined ``filter`` and ``handlers`` properties for a given
   logger will be merged with corresponding properties from the default
   logger. For example, if the user configuration adds a filter called
   ``myFilter`` to the ``BitBake.SigGen``, and the default configuration
   adds a filter called ``BitBake.defaultFilter``, both filters will be
   applied to the logger

As an example, consider the following user logging configuration file
which logs all Hash Equivalence related messages of VERBOSE or higher to
a file called ``hashequiv.log`` ::

   {
       "version": 1,
       "handlers": {
           "autobuilderlog": {
               "class": "logging.FileHandler",
               "formatter": "logfileFormatter",
               "level": "DEBUG",
               "filename": "hashequiv.log",
               "mode": "w"
           }
       },
       "formatters": {
               "logfileFormatter": {
                   "format": "%(name)s: %(levelname)s: %(message)s"
               }
       },
       "loggers": {
           "BitBake.SigGen.HashEquiv": {
               "level": "VERBOSE",
               "handlers": ["autobuilderlog"]
           },
           "BitBake.RunQueue.HashEquiv": {
               "level": "VERBOSE",
               "handlers": ["autobuilderlog"]
           }
       }
   }
