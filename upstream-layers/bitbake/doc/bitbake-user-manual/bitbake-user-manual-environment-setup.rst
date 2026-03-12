.. SPDX-License-Identifier: CC-BY-2.5

=============================================
Setting Up The Environment With bitbake-setup
=============================================

|

Setting up layers and configuring builds can be done with the
``bitbake-setup`` tool. This tool acts as a top-level utility which can perform
the following tasks:

-  Parse a JSON configuration file that describes what layers and which snippets
   of configuration to use.

-  Clone the layers onto the versions specified in the configuration file.

-  Create and setup a directory ready for building what is specified in
   configuration files.

-  Behave according to global or per-project settings.

-  Synchronize with upstream configuration changes.

Quick Start
===========

#. If you haven't already, clone the BitBake repository:

   .. code-block:: shell

      $ git clone https://git.openembedded.org/bitbake

#. ``bitbake-setup`` is part of the BitBake source tree under
   ``./bitbake/bin/bitbake-setup``.

   To start, run:

   .. code-block:: shell

      $ ./bitbake/bin/bitbake-setup init

   This command will ask you to choose which configurations to use available as
   part of the default BitBake :term:`Configuration Templates <Configuration
   Template>`.

   .. note::

      These default configurations are located in ``./bitbake/default-registry/``.
      See the :ref:`ref-bbsetup-section-config-reference` section to learn more
      about ``bitbake-setup`` input configuration files.

#. Here is an example of files and directories structure created by ``bitbake-setup init``::

      ./bitbake-builds/
      ├── site.conf
      └── poky-master-poky-distro_poky-machine_qemux86-64/
          ├── build/
          ├── config/
          └── layers/

   With:

   -  ``./bitbake-builds``: the :term:`Top Directory`, where ``bitbake-setup``
      configures everything. This directory can be configured with the
      :ref:`ref-bbsetup-setting-top-dir-prefix` and
      :ref:`ref-bbsetup-setting-top-dir-name` settings.

   -  ``poky-master-poky-distro_poky-machine_qemux86-64``: a :term:`Setup`
      directory, which holds a :term:`Setup`: a result of the choices made
      during the ``bitbake-setup init`` execution.

      The name of the directory will vary depending on the choices.

   -  ``config/``: holds the :term:`Configuration Instance`, which embeds the
      :term:`Configuration Template` (first choice of the :ref:`ref-bbsetup-command-init` command)
      and the choices made during the initialization.

   -  ``build/``: the :term:`BitBake Build` directory, where BitBake stores
      its own configuration and outputs for the builds.

   -  ``layers/``: the directory where :ref:`layers
      <bitbake-user-manual/bitbake-user-manual-intro:Layers>` and other
      repositories managed by ``bitbake-setup`` are stored and updated.

   -  ``site.conf``: a BitBake configuration file that contains site specific
      configurations for your build environment. When it is created, it contains
      some variable definitions that are based on your current :term:`settings`.
      Comments in this file will help you understand what these variables
      correspond to.

      These configurations are shared between the :term:`setups <Setup>`.

#. Source the ``init-build-env`` file present in the :term:`BitBake Build`
   directory:

   .. code-block:: shell

      $ source ./poky-master-poky-distro_poky-machine_qemux86-64/build/init-build-env

   This command will prepare your current shell with the BitBake environment.

#. You can then start running ``bitbake`` in the current shell. For more information
   on how to use ``bitbake``, see the :doc:`/bitbake-user-manual/bitbake-user-manual-execution`
   section of this manual.

Terminology
===========

The ``bitbake-setup`` tool revolves around some common terms we define in this
section.

``bitbake-setup`` works with a specific hierarchy of files and directories, that
can be represented as follows::

   Top Directory
   ├── site.conf
   ├── Setup 1
   │   ├── build/
   │   ├── config/
   │   └── layers/
   ├── Setup 2
   │   ├── build/
   │   ├── config/
   │   └── layers/
   ...

The "Top Directory" and "Setup" directories are defined as follows:

.. glossary::
   :term:`Top Directory`
      The top directory is the working directory of ``bitbake-setup``, where its
      outputs end-up (unless otherwise configured by :term:`settings` such as
      :ref:`ref-bbsetup-setting-dl-dir`).

      The location of this directory can be changed with the
      :ref:`ref-bbsetup-setting-top-dir-prefix` and
      :ref:`ref-bbsetup-setting-top-dir-name` settings.

      The top directory contains one or more :term:`Setup` directories, each of
      them containing a :term:`Setup`, and a :term:`Site Configuration File`
      (named ``site.conf``).

   :term:`Setup`
      A Setup is the result of the :ref:`ref-bbsetup-command-init`
      command, which creates a :term:`Setup` directory. It is constructed from a
      :term:`Configuration Template` and choices made during the ``init`` command.

      It contains at least:

      -  A :term:`BitBake Build` (``build/`` directory).
      -  A :term:`Configuration Instance` (``config/`` directory).
      -  Sources such as :ref:`layers
         <bitbake-user-manual/bitbake-user-manual-intro:Layers>` or other
         repositories managed by ``bitbake-setup`` (``layers/`` directory).

   :term:`Site Configuration File`
      This file named ``site.conf`` is a unique file located in the :term:`Top
      Directory`, and holds top-level BitBake configuration statements shared
      between the :term:`Setups <Setup>`.

      When it is created, it contains some variable definitions that are based
      on your current :term:`settings`. Comments in this file will help you
      understand what these variables correspond to.

The following components are involved to create the content of these directories:

.. glossary::
   :term:`BitBake Build`
      A BitBake Build is a sub-tree inside a :term:`Setup` that BitBake itself
      operates on. The files in the ``conf/`` directory of a :term:`BitBake
      Build` constitute the :ref:`BitBake configuration
      <bitbake-user-manual/bitbake-user-manual-intro:Configuration Files>`.

   :term:`Configuration Template`
      A Configuration Template is a file in JSON format containing a template to
      create a :term:`Setup`. These files are used during the :ref:`ref-bbsetup-command-init`
      command as a starting point to configure the :term:`Setup`. When the
      command runs, the user may be prompted with choices to further specify the
      :term:`Setup` to create.

      It is also possible to specify the choices on the command line for a
      completely non-interactive initialization.

      :term:`Configuration Template` files are stored in :term:`registries
      <Registry>`, and can be listed with the :ref:`ref-bbsetup-command-list`
      command.

      :term:`Configuration Template` files must end with the ``.conf.json``
      suffix for ``bitbake-setup`` to locate them.

      .. note::

         The default :term:`Configuration Templates <Configuration Template>` are
         located in the BitBake repository in a local registry. the
         ``default-registry/`` directory. This can be modified with the
         :ref:`ref-bbsetup-setting-registry` setting.

      :ref:`ref-bbsetup-command-status` will tell if a :term:`Setup`
      is in sync with the :term:`Configuration Template` it was constructed from
      (typically: layer updates).

      :ref:`ref-bbsetup-command-update` will bring a :term:`Setup`
      in sync with its :term:`Configuration Template`.

   :term:`Configuration Instance`
      The :term:`Configuration Instance` is stored in the ``config/`` directory
      in a :term:`Setup`. It embeds the :term:`Configuration Template` and the
      choices made during the initialization.

      It is also a Git repository, that contains a history of the configuration
      instance and updates made to it via :ref:`ref-bbsetup-command-update`.

   :term:`Registry`
      A configuration registry is a place where one or more :term:`Configuration
      Templates <Configuration Template>` are stored.

      The directory structure of the registry can be any: ``bitbake-setup``
      recursively find files ending with ``.conf.json`` and consider it a
      :term:`Configuration Template`.

      The registry location is configured through the
      :ref:`ref-bbsetup-setting-registry` setting. This location can be the URL to
      a Git repository, a local directory, or any URI supported by the BitBake
      fetcher (see the :doc:`/bitbake-user-manual/bitbake-user-manual-fetching`
      section for more information on fetchers).

   :term:`Settings`
      Settings are operational parameters that are global to all builds under a
      :term:`Top Directory`, stored in a ``settings.conf`` file. For example,
      this could be the location of the configuration registry, or where the
      BitBake fetcher should store the downloads.

      There are also global settings, common to all top directories that are
      stored in ``~/.config/bitbake-setup/settings.conf``.

      See the :ref:`bitbake-user-manual/bitbake-user-manual-environment-setup:Settings`
      section to see the supported settings and where they can be stored.

   :term:`Source Override`
      A source override is a JSON file that can be used to modify revisions and
      origins of layers or other sources that need to be checked out into a
      :term:`Setup` (in the ``layers/`` directory). It can be useful for example
      when the master branches need to be changed to master-next for the purpose
      of testing, or to set up a CI pipeline that tests code in a pull request
      coming from a developer's repository and branch.

      Such a file is specified with a command-line option to
      :ref:`ref-bbsetup-command-init`.

      See the :ref:`ref-bbsetup-source-overrides` section for more information on
      the format of these files.

The ``bitbake-setup`` command
=============================

The ``bitbake-setup`` program has general options and sub-commands. These can be
obtained using ``bitbake-setup --help``.

The general options, common to all commands, are:

-  ``-h`` or ``--help``: Show the help message and exit.
-  ``-d`` or ``--debug``: Enable debug outputs.
-  ``-q`` or ``--quiet``: Print only errors.
-  ``--color``: Colorize output (where COLOR is auto, always, never).
-  ``--no-network``: Do not check whether configuration repositories and layer
   repositories have been updated; use only the local cache.
-  ``--global-settings``: Path to the global settings file.
-  ``--setting``: Modify a setting (for this bitbake-setup invocation only).
   For example ``--setting default top-dir-prefix /path/to/top-dir``.

.. _ref-bbsetup-command-init:

``bitbake-setup init``
----------------------

The ``bitbake-setup init`` sub-command helps initializing a :term:`Setup`.

This command can be run without any arguments to prompt the user with
configuration options to choose from. These configuration options are taken from
the input :term:`Configuration Template` files in the :term:`registry`.

.. note::

   The registry location can be set with the :ref:`ref-bbsetup-setting-registry`
   setting and the :ref:`ref-bbsetup-command-settings` command.

Otherwise, the first argument to :ref:`ref-bbsetup-command-init` can be:

-  A Configuration Template ID in the registry.
-  A path to a Configuration Template file on a local disk.
-  An HTTP URI to the Configuration Template file.

The choices made during the bare ``bitbake-setup init`` command can also be
passed directly on the command-line, for example::

   bitbake-setup init <generic config> poky distro/poky-tiny ...

``bitbake-setup`` will stop and ask to make a choice if the above command does
not contain all of the required configurations to complete the sequence of
choices.

In addition, the command can take the following arguments:

-  ``--non-interactive``: can be used to create :term:`Setups <Setup>`
   without interactions from the user. The command will fail if not all the
   required choices are provided in the command.

-  ``--source-overrides``: can be used to pass one or more
   :ref:`source override <ref-bbsetup-source-overrides>`. See the
   :ref:`ref-bbsetup-source-overrides` section.

-  ``--setup-dir-name``: can be used to configure the name of the :term:`Setup`
   directory.

-  ``--skip-selection``: can be used to skip some of the choices
   (which may result in an incomplete :term:`Setup`!)

-  ``-L`` or ``--use-local-source``: instead of getting a source as prescribed in
   a configuration, symlink it into a :term:`Setup` from a path on local disk. This
   is useful for local development where that particular source directory is managed
   separately, and bitbake-setup will include it in a build but will not otherwise
   touch or modify it. This option can be specified multiple times to specify multiple
   local sources.

   The option can be seen as a command line shortcut to providing an override file
   with a ``local`` source in it. See the :ref:`ref-bbsetup-source-overrides` section
   for more information on source overrides.

``bitbake-setup init`` Examples
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  This example prompts the user to choose a :term:`Configuration Template` from
   a custom registry:

   .. code-block:: shell

      $ ./bitbake/bin/bitbake-setup \
          --setting default registry 'git://example.com/bitbake-setup-configurations.git;protocol=https;branch=main;rev=main' \
          init

-  This example takes a :term:`Configuration Template` from a remote location
   (here, one of the default configuration in BitBake):

   .. code-block:: shell

      $ ./bitbake/bin/bitbake-setup init https://git.openembedded.org/bitbake/plain/default-registry/configurations/oe-nodistro-master.conf.json

-  This example initializes a :term:`Setup` with:

   -  A custom :ref:`ref-bbsetup-setting-top-dir-prefix` and :ref:`ref-bbsetup-setting-top-dir-name`
   -  A :term:`source override`, and additionally overriding openembedded-core with a locally managed directory.
   -  A standalone :term:`Configuration Template` file.
   -  Choices passed on the command-line, applied non-interactively.

   .. code-block:: shell

      $ ./bitbake/bin/bitbake-setup \
          --setting default top-dir-prefix /work/bitbake-setup \
          --setting default top-dir-name custom-project \
          init \
          --non-interactive \
          --source-overrides develop-branch.json \
          -L openembedded-core ~/development/openembedded-core-gadget \
          ./gadget_master.conf.json \
          gadget distro/gadget machine/gadget

.. _ref-bbsetup-command-list:

``bitbake-setup list``
----------------------

The ``bitbake-setup list`` sub-command lists the available :term:`Configuration
Templates <Configuration Template>` in the current :term:`registry`.

In addition, the command can take the following arguments:

-  ``--with-expired``: list the expired configuration (e.g. older Yocto releases
   that have reached their End-Of-Life dates).

-  ``--write-json``: write the configurations into a JSON file so they can be
   programmatically processed.

.. _ref-bbsetup-command-status:

``bitbake-setup status``
------------------------

The ``bitbake-setup status`` sub-command shows the status of a
:term:`Setup`. Any differences between the local copy of the :term:`Configuration
Template` and the upstream one are printed on the console.

If the BitBake environment is sourced and ready to build, the ``bitbake-setup
status`` command (without any arguments) will show the status of the current
:term:`Setup`.

In addition, the command can take the following arguments:

-  ``--setup-dir``: path to the :term:`Setup` to check to status for. Not
   required if the command is invoked from an initialized BitBake environment
   that contains :term:`BBPATH`.

.. _ref-bbsetup-command-update:

``bitbake-setup update``
------------------------

The ``bitbake-setup update`` sub-command updates a :term:`Setup` to sync with
the latest changes from the :term:`Configuration Template` it was constructed from.
The :ref:`ref-bbsetup-command-status` command can be used to show the current
status of the :term:`Setup` before updating it.

In addition, the command can take the following arguments:

-  ``--update-bb-conf``: whether to update the :term:`BitBake Build`
   configuration (``local.conf``, ``bblayers.conf``, etc.). This argument can
   take up to three values:

   -  ``prompt`` (default): ask the user whether to update.
   -  ``yes``: update the configuration files.
   -  ``no``: don't update the configuration files.

-  ``--setup-dir``: path to the :term:`Setup` to update. Not required if the
   command is invoked from an initialized BitBake environment that contains
   :term:`BBPATH`.

.. _ref-bbsetup-command-install-buildtools:

``bitbake-setup install-buildtools``
------------------------------------

The ``bitbake-setup install-buildtools`` sub-command installs and extracts a
buildtools tarball into the specified :term:`Setup`.

After completion, help is printed to help the user on how to use the extracted
tarball.

.. note::

   The purpose of the Buildtools tarball is to provide tools needed to run
   BitBake on build machines where such tools cannot be easily obtained from the
   host Linux distribution (for example on older distribution versions that do
   not contain a recent enough GCC compiler or Python interpreter, or machines
   where the user running BitBake cannot easily install additional software into
   the system). This command requires that the OpenEmbedded-core layer is
   present in the BitBake configuration.

   See https://docs.yoctoproject.org/ref-manual/system-requirements.html#required-git-tar-python-make-and-gcc-versions
   for more information.

In addition, the command can take the following arguments:

-  ``--force``: force the re-installation of the tarball.

-  ``--setup-dir``: path to the :term:`Setup` to check to status for. Not
   required if :term:`BBPATH` is already configured.

.. _ref-bbsetup-command-settings:

``bitbake-setup settings``
--------------------------

The ``bitbake-setup settings`` sub-command helps modifying the settings of
``bitbake-setup``. This sub-command has sub-commands itself:

-  ``bitbake-setup settings list`` lists the current settings and their value.
-  ``bitbake-setup settings set`` sets a setting.
-  ``bitbake-setup settings unset`` removes a setting.

Settings must be set with a section and a value, for example::

   bitbake-setup setting set default top-dir-name bitbake-builds

Will set the value of ``top-dir-name`` in the ``default`` section to
"bitbake-builds".

In addition, the command can take the following arguments:

-  ``--global``: write to the global settings
   (``~/.config/bitbake-setup/settings.conf``) instead of the :term:`Top
   Directory` settings.

See the :ref:`bitbake-user-manual/bitbake-user-manual-environment-setup:Settings`
section to see the supported settings.

.. note::

   The supported setting listed in the
   :ref:`bitbake-user-manual/bitbake-user-manual-environment-setup:Settings`
   section are only affected when set in the ``default`` section.

Settings
========

The settings allow configuring ``bitbake-setup``. Settings are stored in a file
named ``settings.conf``, in :wikipedia:`INI <INI_file>` format.

There are multiple locations for storing settings. Settings in different
locations can override each other, but the final value of a setting is computed
from reading the files in this order:

#. Global settings file: ``~/.config/bitbake-setup/settings.conf``.

#. Local settings file, taken from a ``settings.conf`` file in the :term:`Top
   Directory`.

#. Command-line settings, passed with the ``--setting`` argument.

A valid settings file would for example be:

.. code-block:: ini

   [default]
   top-dir-prefix = /path/to/workspace
   top-dir-name = bitbake-builds
   registry = /path/to/bitbake/default-registry
   dl-dir = /path/to/bitbake-setup-downloads
   use-full-setup-dir-name = yes

Settings and their values can be listed and modified with the ``bitbake-setup
settings`` command. See the :ref:`ref-bbsetup-command-settings` section for
more information.

Below are the available settings.

.. _ref-bbsetup-setting-top-dir-prefix:

``top-dir-prefix``
------------------

The :ref:`ref-bbsetup-setting-top-dir-prefix` setting helps configuring the
leftmost part of the path to the :term:`Top Directory`.

For example, with:

.. code-block:: ini

   [default]
   top-dir-prefix = /path/to/workspace

The :term:`top directory` would be ``/path/to/workspace/<top-dir-name>`` with
the ``<top-dir-name>`` corresponding to the :ref:`ref-bbsetup-setting-top-dir-name`
setting.

This is most useful to customize on systems where the default location of the
:term:`Top Directory` (``~/bitbake-builds``) is not suitable, and there is a
dedicated directory for builds somewhere else.

.. _ref-bbsetup-setting-top-dir-name:

``top-dir-name``
----------------

The :ref:`ref-bbsetup-setting-top-dir-name` setting helps configuring the
rightmost part of the path to the :term:`Top Directory`.

For example, with:

.. code-block:: ini

   [default]
   top-dir-name = builds

The :term:`top directory` would be ``<top-dir-prefix>/builds`` with
the ``<top-dir-prefix>`` corresponding to the :ref:`ref-bbsetup-setting-top-dir-prefix`
setting.

.. _ref-bbsetup-setting-registry:

``registry``
------------

The :ref:`ref-bbsetup-setting-registry` setting sets the URI location of the
registry. This URI can be any URI supported by the BitBake fetcher.

A local registry would be configured as follows:

.. code-block:: ini

   [default]
   registry = /path/to/registry

When using another fetcher, it must be specified in the URI scheme. For example:

.. code-block:: ini

   [default]
   registry = git://example.com/bitbake-setup-configurations;protocol=https;branch=master;rev=master

This would fetch the remote configurations from a remote Git remote repository,
on the ``master`` branch.

See the :doc:`/bitbake-user-manual/bitbake-user-manual-fetching` section for more
information on BitBake fetchers.

.. _ref-bbsetup-setting-dl-dir:

``dl-dir``
----------

The :ref:`ref-bbsetup-setting-dl-dir` setting sets the location of the download
cache that ``bitbake-setup`` will configure for the purpose of downloading
configuration repositories, layers and other sources using BitBake fetchers.
Please see :doc:`/bitbake-user-manual/bitbake-user-manual-fetching` and the
:term:`DL_DIR` variable for more information.

The location can be set such that it is shared with :term:`DL_DIR` specified by
BitBake builds, so that there is a single directory containing a copy of
everything needed to set up and run a BitBake build offline in a reproducible
manner.

.. _ref-bbsetup-setting-use-full-setup-dir-name:

``use-full-setup-dir-name``
---------------------------

The :ref:`ref-bbsetup-setting-use-full-setup-dir-name` setting, if set to ``yes``
will override the suggestions for the :term:`Setup` directory name made by
``setup-dir-name`` entries in :term:`Configuration Template` files. This
will make the directory names longer, but fully specific: they will contain
all selections made during initialization.

.. _ref-bbsetup-section-config-reference:

Configuration Template Files Reference
======================================

:term:`Configuration Templates <Configuration Template>` are the input files given
to ``bitbake-setup`` to configure :term:`Setups <Setup>`.

These files are written in the JSON file format and are stored in a
:term:`Registry`. They can also be standalone files directly passed to the
:ref:`ref-bbsetup-command-init` command:

.. code-block:: shell

   $ bitbake-setup init /path/to/config.conf.json

They contain the following sections:

-  ``version`` (**required**): version of the configuration file.

   Example:

   .. code-block:: json
      :force:

      {
          "version": "1.0"
      }

-  ``description`` (**required**): the description of the configuration.

   Example:

   .. code-block:: json
      :force:

      {
          "description": "OpenEmbedded - 'nodistro' basic configuration"
      }

-  ``sources`` (**required**): sources, such as git repositories that should
   be provided under ``layers/`` directory of a :term:`Setup`. Although the
   list of sources can be very flexible, at least BitBake should be cloned
   to generate a :term:`Setup`.

   Example:

   .. code-block:: json
      :force:

      {
         "sources": {
             "bitbake": {
                 "git-remote": {
                     "uri": "https://git.openembedded.org/bitbake",
                     "branch": "master",
                     "rev": "master"
                 },
                 "path": "bitbake"
             },
             "openembedded-core": {
                 "local": {
                     "path": "~/path/to/local/openembedded-core"
                 }
             },
             "meta-private": {
                 "git-remote": {
                     "remotes": {
                         "origin": {
                             "uri": "ssh://git@git.example.com/meta-private"
                         }
                     },
                     "branch": "master",
                     "rev": "master"
                 },
                 "path": "meta-private"
             }
         }
      }

   Sources can be specified with the following options:

   -  ``path`` (*optional*): where the source is extracted, relative to the
      ``layers/`` directory of a :term:`Setup`. If unspecified, the name of the
      source is used.

   -  ``git-remote`` (*optional*): specifies URI, branch and revision of a git
      repository to fetch from.

      ``git-remote`` entries are specified with the following options:

      -  ``remotes`` (*optional*): a dictionary of git remote names, each containing a ``uri`` property
         with a URI that follows the git URI syntax. See https://git-scm.com/docs/git-clone#_git_urls
         and https://git-scm.com/docs/git-remote for more information.

         This structure is useful if you want to provide several remotes. Please
         note that each URI will be cloned, but only the the last treated one
         will be used in the unpacked source.

         Example:

         .. code-block:: json
            :force:

            {
               "sources": {
                   "bitbake": {
                       "git-remote": {
                           "remotes": {
                               "origin": {
                                   "uri": "https://git.openembedded.org/bitbake"
                               },
                               "contrib": {
                                   "uri": "https://git.openembedded.org/bitbake-contrib"
                               }
                           },
                           "branch": "master",
                           "rev": "master"
                       },
                       "path": "bitbake"
                   }
               }
            }
      -  ``rev`` (**required**): the revision to checkout. Can be the name of the
         branch to checkout on the latest revision of the specified ``branch``.

         If the value is the branch name, ``bitbake-setup`` will check out the
         latest revision on that branch, and keep it updated when using the
         :ref:`ref-bbsetup-command-update` command.

      -  ``branch`` (**required**): the Git branch, used to check that the
         specified ``rev`` is indeed on that branch.

      -  ``uri`` (*optional*): a URI that follows the git URI syntax. You can replace
         the ``remotes`` structure if only one URI is provided. Despite this, ``uri``
         and ``remotes`` can still be used together.

         See https://git-scm.com/docs/git-clone#_git_urls for more information.

   -  ``local`` (*optional*): specifies a path on local disk that should be symlinked
      to under ``layers/``. This is useful for local development, where some layer
      or other component used in a build is managed separately, but should still be
      available for bitbake-setup driven builds.

      ``local`` entries are specified with the following options:

      -  ``path`` (**required**): the path on local disk where the externally
         managed source tree is. ``~`` and ``~user`` are expanded to that user's home
         directory. Paths in configuration files must be absolute (after possible
         ~ expansion), paths in override files can be relative to the directory where
         the override file is.

-  ``expires`` (*optional*): Expiration date of the configuration. This date
   should be in :wikipedia:`ISO 8601 <ISO_8601>` format (``YYYY-MM-DDTHH:MM:SS``).

-  ``bitbake-setup`` (**required**): contains a list of configurations.

   Example:

   .. code-block:: json

      {
         "bitbake-setup": {
            "configurations": [
            {
                "bb-layers": ["openembedded-core/meta","meta-yocto/meta-yocto-bsp","meta-yocto/meta-poky"],
                "bb-env-passthrough-additions": ["DL_DIR","SSTATE_DIR"],
                "oe-fragments-one-of": {
                    "machine": {
                        "description": "Target machines",
                        "options" : [
                            { "name": "machine/qemux86-64", "description": "Machine configuration for running an x86-64 system on QEMU" },
                            { "name": "machine/qemuarm64", "description": "Machine configuration for running an ARMv8 system on QEMU" },
                            { "name": "machine/qemuriscv64", "description": "Machine configuration for running an RISC-V system on QEMU" },
                            { "name": "machine/genericarm64", "description": "Machine configuration for Arm64 SystemReady IR/ES platforms" },
                            { "name": "machine/genericx86-64", "description": "Machine configuration for generic x86_64 (64-bit) PCs and servers" }
                        ]
                    },
                    "distro": {
                        "description": "Target Distributions",
                        "options" : [
                            { "name": "distro/poky", "description": "Distro configuration for Poky (Yocto Project Reference Distro)" },
                            { "name": "distro/poky-altcfg", "description": "Distro configuration for Poky (systemd init manager)" },
                            { "name": "distro/poky-tiny", "description": "Distro configuration for Poky (optimized for size)" }
                        ]
                    }
                },
                "configurations": [
                {
                    "name": "poky",
                    "description": "Poky - The Yocto Project testing distribution"
                },
                {
                    "name": "poky-with-sstate",
                    "description": "Poky - The Yocto Project testing distribution with internet sstate acceleration. Use with caution as it requires a completely robust local network with sufficient bandwidth.",
                    "oe-fragments": ["core/yocto/sstate-mirror-cdn"]
                }
                ]
            }
            ]
         }
      }

   Configurations can be specified with the following options:

   -  ``name`` (**required**): the name of this configuration snippet. This is
      what is prompted during the :ref:`ref-bbsetup-command-init` command
      execution.

   -  ``description`` (**required**): the description of this configuration
      snippet. This is what is prompted during the
      :ref:`ref-bbsetup-command-init` command execution.

   -  ``configurations``: Configurations can recursively contain as many nested
      configurations as needed. This will create more choices when running the
      :ref:`ref-bbsetup-command-init` command.

      The purpose of such nesting is to be able to scale the configurations, for
      example when there is a need to create multiple configurations that share
      some parameters (which are specified in their common parent), but differ
      between themselves in other parameters. ``bitbake-setup`` will assemble
      configuration choices by putting together information from a leaf
      configuration and all of its ancestors.

   -  ``bb-env-passthrough-additions`` (*optional*): List of environment
      variables to include in :term:`BB_ENV_PASSTHROUGH_ADDITIONS`.

   -  ``bb-layers`` (*optional*): List of layers to add to the ``bblayers.conf``
      file. Paths in this list are relative to the ``layers/`` directory of a
      :term:`Setup`.

      The ``bb-layers`` keyword cannot be used in conjunction with the
      ``oe-template`` option, as the ``bblayers.conf`` file comes from the
      template itself.

   -  ``bb-layers-file-relative`` (*optional*): List of layers that are not
      managed by ``bitbake-setup`` but that need to be included as part of the
      ``bblayers.conf`` file. Paths in this list are relative to the
      configuration file.

      This is useful when (one or more) configuration files and (one or
      more) layers are hosted in the same Git repository, which is cloned
      and managed independently from bitbake-setup workflows. For example::

         ├── meta-myproject/
         └── myproject.conf.json

      Then ``myproject.conf.json`` can contain the following to add
      ``meta-myproject`` to ``bblayers.conf``::

         {
            ...
            "bb-layers-file-relative": [
               "meta-myproject"
            ],
            ...
         }

      The ``bb-layers-file-relative`` keyword cannot be used in conjunction with the
      ``oe-template`` keyword, as the ``bblayers.conf`` file comes from the
      template itself.

   -  ``oe-template`` (*optional*, OpenEmbedded specific): OpenEmbedded template
      to use. This cannot be used in conjunction with the
      ``bb-layers`` or ``bb-layers-file-relative`` keywords as it
      already provides a ready ``bblayers.conf`` file to use.

      See https://docs.yoctoproject.org/dev-manual/custom-template-configuration-directory.html
      for more information of OpenEmbedded templates.

   -  ``oe-fragments-one-of`` (*optional*, OpenEmbedded specific): the OpenEmbedded
      fragments to select as part of the build.

      This can one of two formats:

      -  A list of dictionaries with ``name`` and a ``description``,
         corresponding to respectively the fragments names and fragment
         descriptions (like in the example above).

      -  A list of fragment names. For example:

         .. code-block:: json

            "options" : ["machine/qemux86-64", "machine/qemuarm64", "machine/qemuriscv64", "machine/genericarm64", "machine/genericx86-64"]

      This will trigger choices to make during the
      :ref:`ref-bbsetup-command-init` command execution.

      See https://docs.yoctoproject.org/dev/ref-manual/fragments.html for
      more information of OpenEmbedded configuration fragments.

   -  ``oe-fragments`` (*optional*, OpenEmbedded specific): fragments to select
      as part of the build.

      See https://docs.yoctoproject.org/dev/ref-manual/fragments.html for
      more information of OpenEmbedded configuration fragments.

   -  ``setup-dir-name`` (*optional*): a suggestion for the :term:`Setup`
      directory name. Bitbake-setup will use it, unless it already exists, or
      the :ref:`ref-bbsetup-setting-use-full-setup-dir-name` setting is set
      to ``yes`` (in those cases, it will fall back to the built-in full name,
      containing the full set of choices made during initialization).

      This key can have variable expansions in the same format as python
      string.Template strings (which matches the shell variable expansion rules):
      https://docs.python.org/3/library/string.html#template-strings-strings .

      Variables that can be expanded are any fragment configuration prompted
      by the user (e.g. the keys in "oe-fragments-one-of")::

            {
                ...
                "oe-fragments-one-of": {
                    "machine": {
                        "description": "Target machines",
                        "options" : ["machine/gadget", "machine/gizmo"]
                    }
                },
                "setup-dir-name": "somebuild-$machine"
                ...
            }

      would expand to ``somebuild-machine_gadget``.

Configuration Template Examples
-------------------------------

OpenEmbedded "nodistro" configuration for master branches:

.. literalinclude:: ../../default-registry/configurations/oe-nodistro-master.conf.json
   :language: json

Poky distribution configuration for master branches:

.. literalinclude:: ../../default-registry/configurations/poky-master.conf.json
   :language: json

.. _ref-bbsetup-source-overrides:

Source Overrides
================

See the definition of :term:`Source Override` in the Terminology section.

These files are written in the JSON file format and are optionally passed to the
``--source-overrides`` argument of the :ref:`ref-bbsetup-command-init` command.
The ``--source-overrides`` option can be passed multiple times, in which case the
overrides are applied in the order specified in the command-line.

Here is an example file that overrides the branch of the BitBake repository to
"master-next", and provides openembedded-core as a symlink to a path on local disk:

.. code-block:: json

   {
       "description": "Source override file",
       "sources": {
           "bitbake": {
               "git-remote": {
                   "branch": "master-next",
                   "remotes": {
                       "origin": {
                           "uri": "https://git.openembedded.org/bitbake"
                       }
                   },
                   "rev": "master-next"
               }
           },
           "openembedded-core": {
               "local": {
                   "path": "~/path/to/local/openembedded-core"
               }
           }
       },
       "version": "1.0"
   }

-  The ``version`` parameter contains the version of the used configuration, and
   should match the one of the :term:`Configuration Template` file in use.

-  The ``sources`` section contains the same options as the ``sources`` option
   of a :term:`Configuration Template` file. See the
   :ref:`ref-bbsetup-section-config-reference` section for more information.

.. _ref-bbsetup-fixed-revisions:

Fixed source revisions
======================

:term:`Configuration Template` can set source revisions in ``rev`` to a tag or a branch.
Bitbake-setup will make sure the actual revision will match the tag or branch when performing
initializations or updates, and will capture the revisions in a :ref:`ref-bbsetup-source-overrides`
file.

This file is named ``sources-fixed-revisions.json`` and is available in ``layers/``
under the :term:`Setup` directory (as well as under ``config/`` for backwards compatibility).
It can be used to keep record of what was checked out when using configurations that do not
specify exact revisions.

Also, as it is an override file, it can be combined with the original configurations
to initialize a setup in a reproducible way that guarantees an exact, never-changing
set of revisions, by using the ``--source-overrides`` option of the
:ref:`ref-bbsetup-command-init` command.

For example if the original configuration had specified only a master branch
for a source::

     "bitbake": {
         "git-remote": {
             "remotes": {
                 "origin": {
                     "uri": "https://git.openembedded.org/bitbake"
                 }
             },
             "branch": "master",
             "rev": "master"
     }


the override will contain the exact revisions::

     "bitbake": {
         "git-remote": {
             "remotes": {
                 "origin": {
                     "uri": "https://git.openembedded.org/bitbake"
                 }
             },
             "branch": "master",
             "rev": "720df1a53452983c1c832f624490e255cf389204"
     }
