.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

******************
Variables Glossary
******************

This chapter lists common variables used in the OpenEmbedded build
system and gives an overview of their function and contents.

:term:`A <ABIEXTENSION>` :term:`B` :term:`C <CACHE>`
:term:`D` :term:`E <EFI_PROVIDER>` :term:`F <FEATURE_PACKAGES>`
:term:`G <GCCPIE>` :term:`H <HOMEPAGE>` :term:`I <ICECC_DISABLED>`
:term:`K <KARCH>` :term:`L <LABELS>` :term:`M <MACHINE>`
:term:`N <NATIVELSBSTRING>` :term:`O <OBJCOPY>` :term:`P`
:term:`R <RANLIB>` :term:`S` :term:`T`
:term:`U <UBOOT_CONFIG>` :term:`V <VOLATILE_LOG_DIR>`
:term:`W <WARN_QA>` :term:`X <XSERVER>`

.. glossary::
   :sorted:

   :term:`ABIEXTENSION`
      Extension to the Application Binary Interface (ABI) field of the GNU
      canonical architecture name (e.g. "eabi").

      ABI extensions are set in the machine include files. For example, the
      ``meta/conf/machine/include/arm/arch-arm.inc`` file sets the
      following extension::

         ABIEXTENSION = "eabi"

   :term:`ALLOW_EMPTY`
      Specifies whether to produce an output package even if it is empty.
      By default, BitBake does not produce empty packages. This default
      behavior can cause issues when there is an
      :term:`RDEPENDS` or some other hard runtime
      requirement on the existence of the package.

      Like all package-controlling variables, you must always use them in
      conjunction with a package name override, as in::

         ALLOW_EMPTY:${PN} = "1"
         ALLOW_EMPTY:${PN}-dev = "1"
         ALLOW_EMPTY:${PN}-staticdev = "1"

   :term:`ALTERNATIVE`
      Lists commands in a package that need an alternative binary naming
      scheme. Sometimes the same command is provided in multiple packages.
      When this occurs, the OpenEmbedded build system needs to use the
      alternatives system to create a different binary naming scheme so the
      commands can co-exist.

      To use the variable, list out the package's commands that are also
      provided by another package. For example, if the ``busybox`` package
      has four such commands, you identify them as follows::

         ALTERNATIVE:busybox = "sh sed test bracket"

      For more information on the alternatives system, see the
      ":ref:`ref-classes-update-alternatives`"
      section.

   :term:`ALTERNATIVE_LINK_NAME`
      Used by the alternatives system to map duplicated commands to actual
      locations. For example, if the ``bracket`` command provided by the
      ``busybox`` package is duplicated through another package, you must
      use the :term:`ALTERNATIVE_LINK_NAME` variable to specify the actual
      location::

         ALTERNATIVE_LINK_NAME[bracket] = "/usr/bin/["

      In this example, the binary for the ``bracket`` command (i.e. ``[``)
      from the ``busybox`` package resides in ``/usr/bin/``.

      .. note::

         If :term:`ALTERNATIVE_LINK_NAME` is not defined, it defaults to ``${bindir}/name``.

      For more information on the alternatives system, see the
      ":ref:`ref-classes-update-alternatives`"
      section.

   :term:`ALTERNATIVE_PRIORITY`
      Used by the alternatives system to create default priorities for
      duplicated commands. You can use the variable to create a single
      default regardless of the command name or package, a default for
      specific duplicated commands regardless of the package, or a default
      for specific commands tied to particular packages. Here are the
      available syntax forms::

         ALTERNATIVE_PRIORITY = "priority"
         ALTERNATIVE_PRIORITY[name] = "priority"
         ALTERNATIVE_PRIORITY_pkg[name] = "priority"

      For more information on the alternatives system, see the
      ":ref:`ref-classes-update-alternatives`"
      section.

   :term:`ALTERNATIVE_TARGET`
      Used by the alternatives system to create default link locations for
      duplicated commands. You can use the variable to create a single
      default location for all duplicated commands regardless of the
      command name or package, a default for specific duplicated commands
      regardless of the package, or a default for specific commands tied to
      particular packages. Here are the available syntax forms::

         ALTERNATIVE_TARGET = "target"
         ALTERNATIVE_TARGET[name] = "target"
         ALTERNATIVE_TARGET_pkg[name] = "target"

      .. note::

         If :term:`ALTERNATIVE_TARGET` is not defined, it inherits the value
         from the :term:`ALTERNATIVE_LINK_NAME` variable.

         If :term:`ALTERNATIVE_LINK_NAME` and :term:`ALTERNATIVE_TARGET` are the
         same, the target for :term:`ALTERNATIVE_TARGET` has "``.{BPN}``"
         appended to it.

         Finally, if the file referenced has not been renamed, the
         alternatives system will rename it to avoid the need to rename
         alternative files in the :ref:`ref-tasks-install`
         task while retaining support for the command if necessary.

      For more information on the alternatives system, see the
      ":ref:`ref-classes-update-alternatives`" section.

   :term:`ANY_OF_DISTRO_FEATURES`
      When inheriting the :ref:`ref-classes-features_check`
      class, this variable identifies a list of distribution features where
      at least one must be enabled in the current configuration in order
      for the OpenEmbedded build system to build the recipe. In other words,
      if none of the features listed in :term:`ANY_OF_DISTRO_FEATURES`
      appear in :term:`DISTRO_FEATURES` within the current configuration, then
      the recipe will be skipped, and if the build system attempts to build
      the recipe then an error will be triggered.

   :term:`APPEND`
      An override list of append strings for each target specified with
      :term:`LABELS`.

      See the :ref:`ref-classes-grub-efi` class for more
      information on how this variable is used.

   :term:`AR`
      The minimal command and arguments used to run ``ar``.

   :term:`ARCHIVER_MODE`
      When used with the :ref:`ref-classes-archiver` class,
      determines the type of information used to create a released archive.
      You can use this variable to create archives of patched source,
      original source, configured source, and so forth by employing the
      following variable flags (varflags)::

         ARCHIVER_MODE[src] = "original"                   # Uses original (unpacked) source files.
         ARCHIVER_MODE[src] = "patched"                    # Uses patched source files. This is the default.
         ARCHIVER_MODE[src] = "configured"                 # Uses configured source files.
         ARCHIVER_MODE[diff] = "1"                         # Uses patches between do_unpack and do_patch.
         ARCHIVER_MODE[diff-exclude] ?= "file file ..."    # Lists files and directories to exclude from diff.
         ARCHIVER_MODE[dumpdata] = "1"                     # Uses environment data.
         ARCHIVER_MODE[recipe] = "1"                       # Uses recipe and include files.
         ARCHIVER_MODE[srpm] = "1"                         # Uses RPM package files.

      For information on how the variable works, see the
      ``meta/classes/archiver.bbclass`` file in the :term:`Source Directory`.

   :term:`AS`
      Minimal command and arguments needed to run the assembler.

   :term:`ASSUME_PROVIDED`
      Lists recipe names (:term:`PN` values) BitBake does not
      attempt to build. Instead, BitBake assumes these recipes have already
      been built.

      In OpenEmbedded-Core, :term:`ASSUME_PROVIDED` mostly specifies native
      tools that should not be built. An example is ``git-native``, which
      when specified, allows for the Git binary from the host to be used
      rather than building ``git-native``.

   :term:`ASSUME_SHLIBS`
      Provides additional ``shlibs`` provider mapping information, which
      adds to or overwrites the information provided automatically by the
      system. Separate multiple entries using spaces.

      As an example, use the following form to add an ``shlib`` provider of
      shlibname in packagename with the optional version::

         shlibname:packagename[_version]

      Here is an example that adds a shared library named ``libEGL.so.1``
      as being provided by the ``libegl-implementation`` package::

         ASSUME_SHLIBS = "libEGL.so.1:libegl-implementation"

   :term:`AUTO_LIBNAME_PKGS`
      When the :ref:`ref-classes-debian` class is inherited,
      which is the default behavior, :term:`AUTO_LIBNAME_PKGS` specifies which
      packages should be checked for libraries and renamed according to
      Debian library package naming.

      The default value is "${PACKAGES}", which causes the
      :ref:`ref-classes-debian` class to act on all packages that are
      explicitly generated by the recipe.

   :term:`AUTOREV`
      When :term:`SRCREV` is set to the value of this variable, it specifies to
      use the latest source revision in the repository. Here is an example::

         SRCREV = "${AUTOREV}"

      If you use the previous statement to retrieve the latest version of
      software, you need to be sure :term:`PV` contains
      ``${``\ :term:`SRCPV`\ ``}``. For example, suppose you have a kernel
      recipe that inherits the :ref:`ref-classes-kernel` class and you
      use the previous statement. In this example, ``${SRCPV}`` does not
      automatically get into :term:`PV`. Consequently, you need to change
      :term:`PV` in your recipe so that it does contain ``${SRCPV}``.

      For more information see the
      ":ref:`dev-manual/packages:automatically incrementing a package version number`"
      section in the Yocto Project Development Tasks Manual.

   :term:`AUTO_SYSLINUXMENU`
      Enables creating an automatic menu for the syslinux bootloader. You
      must set this variable in your recipe. The
      :ref:`ref-classes-syslinux` class checks this variable.

   :term:`AVAILTUNES`
      The list of defined CPU and Application Binary Interface (ABI)
      tunings (i.e. "tunes") available for use by the OpenEmbedded build
      system.

      The list simply presents the tunes that are available. Not all tunes
      may be compatible with a particular machine configuration, or with
      each other in a
      :ref:`Multilib <dev-manual/libraries:combining multiple versions of library files into one image>`
      configuration.

      To add a tune to the list, be sure to append it with spaces using the
      "+=" BitBake operator. Do not simply replace the list by using the
      "=" operator. See the
      ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:basic syntax`" section in the BitBake
      User Manual for more information.

   :term:`AZ_SAS`
      Azure Storage Shared Access Signature, when using the
      :ref:`Azure Storage fetcher (az://) <bitbake-user-manual/bitbake-user-manual-fetching:fetchers>`
      This variable can be defined to be used by the fetcher to authenticate
      and gain access to non-public artifacts::

         AZ_SAS = ""se=2021-01-01&sp=r&sv=2018-11-09&sr=c&skoid=<skoid>&sig=<signature>""

      For more information see Microsoft's Azure Storage documentation at
      https://docs.microsoft.com/en-us/azure/storage/common/storage-sas-overview

   :term:`B`
      The directory within the :term:`Build Directory` in which the
      OpenEmbedded build system places generated objects during a recipe's
      build process. By default, this directory is the same as the
      :term:`S` directory, which is defined as::

         S = "${WORKDIR}/${BP}"

      You can separate the (:term:`S`) directory and the directory pointed to
      by the :term:`B` variable. Most Autotools-based recipes support
      separating these directories. The build system defaults to using
      separate directories for ``gcc`` and some kernel recipes.

   :term:`BAD_RECOMMENDATIONS`
      Lists "recommended-only" packages to not install. Recommended-only
      packages are packages installed only through the
      :term:`RRECOMMENDS` variable. You can prevent any
      of these "recommended" packages from being installed by listing them
      with the :term:`BAD_RECOMMENDATIONS` variable::

         BAD_RECOMMENDATIONS = "package_name package_name package_name ..."

      You can set this variable globally in your ``local.conf`` file or you
      can attach it to a specific image recipe by using the recipe name
      override::

         BAD_RECOMMENDATIONS:pn-target_image = "package_name"

      It is important to realize that if you choose to not install packages
      using this variable and some other packages are dependent on them
      (i.e. listed in a recipe's :term:`RDEPENDS`
      variable), the OpenEmbedded build system ignores your request and
      will install the packages to avoid dependency errors.

      This variable is supported only when using the IPK and RPM
      packaging backends. DEB is not supported.

      See the :term:`NO_RECOMMENDATIONS` and the
      :term:`PACKAGE_EXCLUDE` variables for related
      information.

   :term:`BASE_LIB`
      The library directory name for the CPU or Application Binary
      Interface (ABI) tune. The :term:`BASE_LIB` applies only in the Multilib
      context. See the ":ref:`dev-manual/libraries:combining multiple versions of library files into one image`"
      section in the Yocto Project Development Tasks Manual for information
      on Multilib.

      The :term:`BASE_LIB` variable is defined in the machine include files in
      the :term:`Source Directory`. If Multilib is not
      being used, the value defaults to "lib".

   :term:`BASE_WORKDIR`
      Points to the base of the work directory for all recipes. The default
      value is "${TMPDIR}/work".

   :term:`BB_ALLOWED_NETWORKS`
      Specifies a space-delimited list of hosts that the fetcher is allowed
      to use to obtain the required source code. Here are
      considerations surrounding this variable:

      -  This host list is only used if :term:`BB_NO_NETWORK` is either not set
         or set to "0".

      -  There is limited support for wildcard matching against the beginning of
         host names. For example, the following setting matches
         ``git.gnu.org``, ``ftp.gnu.org``, and ``foo.git.gnu.org``::

            BB_ALLOWED_NETWORKS = "*.gnu.org"

         .. note::

            The use of the "``*``" character only works at the beginning of
            a host name and it must be isolated from the remainder of the
            host name. You cannot use the wildcard character in any other
            location of the name or combined with the front part of the
            name.

            For example, ``*.foo.bar`` is supported, while ``*aa.foo.bar``
            is not.

      -  Mirrors not in the host list are skipped and logged in debug.

      -  Attempts to access networks not in the host list cause a failure.

      Using :term:`BB_ALLOWED_NETWORKS` in conjunction with
      :term:`PREMIRRORS` is very useful. Adding the host
      you want to use to :term:`PREMIRRORS` results in the source code being
      fetched from an allowed location and avoids raising an error when a
      host that is not allowed is in a :term:`SRC_URI`
      statement. This is because the fetcher does not attempt to use the
      host listed in :term:`SRC_URI` after a successful fetch from the
      :term:`PREMIRRORS` occurs.

   :term:`BB_BASEHASH_IGNORE_VARS`
      See :term:`bitbake:BB_BASEHASH_IGNORE_VARS` in the BitBake manual.

   :term:`BB_CACHEDIR`
      See :term:`bitbake:BB_CACHEDIR` in the BitBake manual.

   :term:`BB_CHECK_SSL_CERTS`
      See :term:`bitbake:BB_CHECK_SSL_CERTS` in the BitBake manual.

   :term:`BB_CONSOLELOG`
      See :term:`bitbake:BB_CONSOLELOG` in the BitBake manual.

   :term:`BB_CURRENTTASK`
      See :term:`bitbake:BB_CURRENTTASK` in the BitBake manual.

   :term:`BB_DANGLINGAPPENDS_WARNONLY`
      Defines how BitBake handles situations where an append file
      (``.bbappend``) has no corresponding recipe file (``.bb``). This
      condition often occurs when layers get out of sync (e.g. ``oe-core``
      bumps a recipe version and the old recipe no longer exists and the
      other layer has not been updated to the new version of the recipe
      yet).

      The default fatal behavior is safest because it is the sane reaction
      given something is out of sync. It is important to realize when your
      changes are no longer being applied.

      You can change the default behavior by setting this variable to "1",
      "yes", or "true" in your ``local.conf`` file, which is located in the
      :term:`Build Directory`: Here is an example::

         BB_DANGLINGAPPENDS_WARNONLY = "1"

   :term:`BB_DEFAULT_TASK`
      See :term:`bitbake:BB_DEFAULT_TASK` in the BitBake manual.

   :term:`BB_DEFAULT_UMASK`
      See :term:`bitbake:BB_DEFAULT_UMASK` in the BitBake manual.

   :term:`BB_DISKMON_DIRS`
      Monitors disk space and available inodes during the build and allows
      you to control the build based on these parameters.

      Disk space monitoring is disabled by default. To enable monitoring,
      add the :term:`BB_DISKMON_DIRS` variable to your ``conf/local.conf`` file
      found in the :term:`Build Directory`. Use the
      following form:

      .. code-block:: none

         BB_DISKMON_DIRS = "action,dir,threshold [...]"

         where:

            action is:
               ABORT:     Immediately stop the build when
                          a threshold is broken.
               STOPTASKS: Stop the build after the currently
                          executing tasks have finished when
                          a threshold is broken.
               WARN:      Issue a warning but continue the
                          build when a threshold is broken.
                          Subsequent warnings are issued as
                          defined by the BB_DISKMON_WARNINTERVAL
                          variable, which must be defined in
                          the conf/local.conf file.

            dir is:
               Any directory you choose. You can specify one or
               more directories to monitor by separating the
               groupings with a space.  If two directories are
               on the same device, only the first directory
               is monitored.

            threshold is:
               Either the minimum available disk space,
               the minimum number of free inodes, or
               both.  You must specify at least one.  To
               omit one or the other, simply omit the value.
               Specify the threshold using G, M, K for Gbytes,
               Mbytes, and Kbytes, respectively. If you do
               not specify G, M, or K, Kbytes is assumed by
               default.  Do not use GB, MB, or KB.

      Here are some examples::

         BB_DISKMON_DIRS = "ABORT,${TMPDIR},1G,100K WARN,${SSTATE_DIR},1G,100K"
         BB_DISKMON_DIRS = "STOPTASKS,${TMPDIR},1G"
         BB_DISKMON_DIRS = "ABORT,${TMPDIR},,100K"

      The first example works only if you also provide the
      :term:`BB_DISKMON_WARNINTERVAL`
      variable in the ``conf/local.conf``. This example causes the build
      system to immediately stop when either the disk space in
      ``${TMPDIR}`` drops below 1 Gbyte or the available free inodes drops
      below 100 Kbytes. Because two directories are provided with the
      variable, the build system also issue a warning when the disk space
      in the ``${SSTATE_DIR}`` directory drops below 1 Gbyte or the number
      of free inodes drops below 100 Kbytes. Subsequent warnings are issued
      during intervals as defined by the :term:`BB_DISKMON_WARNINTERVAL`
      variable.

      The second example stops the build after all currently executing
      tasks complete when the minimum disk space in the ``${TMPDIR}``
      directory drops below 1 Gbyte. No disk monitoring occurs for the free
      inodes in this case.

      The final example immediately stops the build when the number of
      free inodes in the ``${TMPDIR}`` directory drops below 100 Kbytes. No
      disk space monitoring for the directory itself occurs in this case.

   :term:`BB_DISKMON_WARNINTERVAL`
      Defines the disk space and free inode warning intervals. To set these
      intervals, define the variable in your ``conf/local.conf`` file in
      the :term:`Build Directory`.

      If you are going to use the :term:`BB_DISKMON_WARNINTERVAL` variable, you
      must also use the :term:`BB_DISKMON_DIRS`
      variable and define its action as "WARN". During the build,
      subsequent warnings are issued each time disk space or number of free
      inodes further reduces by the respective interval.

      If you do not provide a :term:`BB_DISKMON_WARNINTERVAL` variable and you
      do use :term:`BB_DISKMON_DIRS` with the "WARN" action, the disk
      monitoring interval defaults to the following::

         BB_DISKMON_WARNINTERVAL = "50M,5K"

      When specifying the variable in your configuration file, use the
      following form:

      .. code-block:: none

         BB_DISKMON_WARNINTERVAL = "disk_space_interval,disk_inode_interval"

         where:

            disk_space_interval is:
               An interval of memory expressed in either
               G, M, or K for Gbytes, Mbytes, or Kbytes,
               respectively. You cannot use GB, MB, or KB.

            disk_inode_interval is:
               An interval of free inodes expressed in either
               G, M, or K for Gbytes, Mbytes, or Kbytes,
               respectively. You cannot use GB, MB, or KB.

      Here is an example::

         BB_DISKMON_DIRS = "WARN,${SSTATE_DIR},1G,100K"
         BB_DISKMON_WARNINTERVAL = "50M,5K"

      These variables cause the
      OpenEmbedded build system to issue subsequent warnings each time the
      available disk space further reduces by 50 Mbytes or the number of
      free inodes further reduces by 5 Kbytes in the ``${SSTATE_DIR}``
      directory. Subsequent warnings based on the interval occur each time
      a respective interval is reached beyond the initial warning (i.e. 1
      Gbytes and 100 Kbytes).

   :term:`BB_ENV_PASSTHROUGH`
      See :term:`bitbake:BB_ENV_PASSTHROUGH` in the BitBake manual.

   :term:`BB_ENV_PASSTHROUGH_ADDITIONS`
      See :term:`bitbake:BB_ENV_PASSTHROUGH_ADDITIONS` in the BitBake manual.

   :term:`BB_FETCH_PREMIRRORONLY`
      See :term:`bitbake:BB_FETCH_PREMIRRORONLY` in the BitBake manual.

   :term:`BB_FILENAME`
      See :term:`bitbake:BB_FILENAME` in the BitBake manual.

   :term:`BB_GENERATE_MIRROR_TARBALLS`
      Causes tarballs of the source control repositories (e.g. Git
      repositories), including metadata, to be placed in the
      :term:`DL_DIR` directory.

      For performance reasons, creating and placing tarballs of these
      repositories is not the default action by the OpenEmbedded build
      system::

         BB_GENERATE_MIRROR_TARBALLS = "1"

      Set this variable in your
      ``local.conf`` file in the :term:`Build Directory`.

      Once you have the tarballs containing your source files, you can
      clean up your :term:`DL_DIR` directory by deleting any Git or other
      source control work directories.

   :term:`BB_GENERATE_SHALLOW_TARBALLS`
      See :term:`bitbake:BB_GENERATE_SHALLOW_TARBALLS` in the BitBake manual.

   :term:`BB_GIT_SHALLOW`
      See :term:`bitbake:BB_GIT_SHALLOW` in the BitBake manual.

   :term:`BB_GIT_SHALLOW_DEPTH`
      See :term:`bitbake:BB_GIT_SHALLOW_DEPTH` in the BitBake manual.

   :term:`BB_HASHCHECK_FUNCTION`
      See :term:`bitbake:BB_HASHCHECK_FUNCTION` in the BitBake manual.

   :term:`BB_HASHCONFIG_IGNORE_VARS`
      See :term:`bitbake:BB_HASHCONFIG_IGNORE_VARS` in the BitBake manual.

   :term:`BB_HASHSERVE`
      See :term:`bitbake:BB_HASHSERVE` in the BitBake manual.

   :term:`BB_HASHSERVE_UPSTREAM`
      See :term:`bitbake:BB_HASHSERVE_UPSTREAM` in the BitBake manual.

   :term:`BB_INVALIDCONF`
      See :term:`bitbake:BB_INVALIDCONF` in the BitBake manual.

   :term:`BB_LOADFACTOR_MAX`
      The system load threshold above which BitBake will stop runnig extra
      tasks.

   :term:`BB_LOGCONFIG`
      See :term:`bitbake:BB_LOGCONFIG` in the BitBake manual.

   :term:`BB_LOGFMT`
      See :term:`bitbake:BB_LOGFMT` in the BitBake manual.

   :term:`BB_MULTI_PROVIDER_ALLOWED`
      See :term:`bitbake:BB_MULTI_PROVIDER_ALLOWED` in the BitBake manual.

   :term:`BB_NICE_LEVEL`
      See :term:`bitbake:BB_NICE_LEVEL` in the BitBake manual.

   :term:`BB_NO_NETWORK`
      See :term:`bitbake:BB_NO_NETWORK` in the BitBake manual.

   :term:`BB_NUMBER_PARSE_THREADS`
      See :term:`bitbake:BB_NUMBER_PARSE_THREADS` in the BitBake manual.

   :term:`BB_NUMBER_THREADS`
      The maximum number of tasks BitBake should run in parallel at any one
      time. The OpenEmbedded build system automatically configures this
      variable to be equal to the number of cores on the build system. For
      example, a system with a dual core processor that also uses
      hyper-threading causes the :term:`BB_NUMBER_THREADS` variable to default
      to "4".

      For single socket systems (i.e. one CPU), you should not have to
      override this variable to gain optimal parallelism during builds.
      However, if you have very large systems that employ multiple physical
      CPUs, you might want to make sure the :term:`BB_NUMBER_THREADS` variable
      is not set higher than "20".

      For more information on speeding up builds, see the
      ":ref:`dev-manual/speeding-up-build:speeding up a build`"
      section in the Yocto Project Development Tasks Manual.

      On the other hand, if your goal is to limit the amount of system
      resources consumed by BitBake tasks, setting :term:`BB_NUMBER_THREADS`
      to a number lower than the number of CPU threads in your machine
      won't be sufficient. That's because each package will still be built
      and installed through a number of parallel jobs specified by the
      :term:`PARALLEL_MAKE` variable, which is by default the number of CPU
      threads in your system, and is not impacted by the
      :term:`BB_NUMBER_THREADS` value.

      So, if you set :term:`BB_NUMBER_THREADS` to "1" but don't set
      :term:`PARALLEL_MAKE`, most of your system resources will be consumed
      anyway.

      Therefore, if you intend to reduce the load of your build system by
      setting :term:`BB_NUMBER_THREADS` to a relatively low value compared
      to the number of CPU threads on your system, you should also set
      :term:`PARALLEL_MAKE` to a similarly low value.

      An alternative to using :term:`BB_NUMBER_THREADS` to keep the usage
      of build system resources under control is to use the smarter
      :term:`BB_PRESSURE_MAX_CPU`, :term:`BB_PRESSURE_MAX_IO` or
      :term:`BB_PRESSURE_MAX_MEMORY` controls. They will prevent BitBake
      from starting new tasks as long as thresholds are exceeded. Anyway,
      as with :term:`BB_NUMBER_THREADS`, such controls won't prevent the
      tasks already being run from using all CPU threads on the system
      if :term:`PARALLEL_MAKE` is not set to a low value.

   :term:`BB_ORIGENV`
      See :term:`bitbake:BB_ORIGENV` in the BitBake manual.

   :term:`BB_PRESERVE_ENV`
      See :term:`bitbake:BB_PRESERVE_ENV` in the BitBake manual.

   :term:`BB_PRESSURE_MAX_CPU`
      See :term:`bitbake:BB_PRESSURE_MAX_CPU` in the BitBake manual.

   :term:`BB_PRESSURE_MAX_IO`
      See :term:`bitbake:BB_PRESSURE_MAX_IO` in the BitBake manual.

   :term:`BB_PRESSURE_MAX_MEMORY`
      See :term:`bitbake:BB_PRESSURE_MAX_MEMORY` in the BitBake manual.

   :term:`BB_RUNFMT`
      See :term:`bitbake:BB_RUNFMT` in the BitBake manual.

   :term:`BB_RUNTASK`
      See :term:`bitbake:BB_RUNTASK` in the BitBake manual.

   :term:`BB_SCHEDULER`
      See :term:`bitbake:BB_SCHEDULER` in the BitBake manual.

   :term:`BB_SCHEDULERS`
      See :term:`bitbake:BB_SCHEDULERS` in the BitBake manual.

   :term:`BB_SERVER_TIMEOUT`
      Specifies the time (in seconds) after which to unload the BitBake
      server due to inactivity. Set :term:`BB_SERVER_TIMEOUT` to determine how
      long the BitBake server stays resident between invocations.

      For example, the following statement in your ``local.conf`` file
      instructs the server to be unloaded after 20 seconds of inactivity::

         BB_SERVER_TIMEOUT = "20"

      If you want the server to never be unloaded,
      set :term:`BB_SERVER_TIMEOUT` to "-1".

   :term:`BB_SETSCENE_DEPVALID`
      See :term:`bitbake:BB_SETSCENE_DEPVALID` in the BitBake manual.

   :term:`BB_SIGNATURE_EXCLUDE_FLAGS`
      See :term:`bitbake:BB_SIGNATURE_EXCLUDE_FLAGS` in the BitBake manual.

   :term:`BB_SIGNATURE_HANDLER`
      See :term:`bitbake:BB_SIGNATURE_HANDLER` in the BitBake manual.

   :term:`BB_SRCREV_POLICY`
      See :term:`bitbake:BB_SRCREV_POLICY` in the BitBake manual.

   :term:`BB_STRICT_CHECKSUM`
      See :term:`bitbake:BB_STRICT_CHECKSUM` in the BitBake manual.

   :term:`BB_TASK_IONICE_LEVEL`
      See :term:`bitbake:BB_TASK_IONICE_LEVEL` in the BitBake manual.

   :term:`BB_TASK_NICE_LEVEL`
      See :term:`bitbake:BB_TASK_NICE_LEVEL` in the BitBake manual.

   :term:`BB_TASKHASH`
      See :term:`bitbake:BB_TASKHASH` in the BitBake manual.

   :term:`BB_VERBOSE_LOGS`
      See :term:`bitbake:BB_VERBOSE_LOGS` in the BitBake manual.

   :term:`BB_WORKERCONTEXT`
      See :term:`bitbake:BB_WORKERCONTEXT` in the BitBake manual.

   :term:`BBCLASSEXTEND`
      Allows you to extend a recipe so that it builds variants of the
      software. There are common variants for recipes as "natives" like
      ``quilt-native``, which is a copy of Quilt built to run on the build
      system; "crosses" such as ``gcc-cross``, which is a compiler built to
      run on the build machine but produces binaries that run on the target
      :term:`MACHINE`; ":ref:`ref-classes-nativesdk`", which
      targets the SDK machine instead of :term:`MACHINE`; and "mulitlibs" in
      the form "``multilib:``\ multilib_name".

      To build a different variant of the recipe with a minimal amount of
      code, it usually is as simple as adding the following to your recipe::

         BBCLASSEXTEND =+ "native nativesdk"
         BBCLASSEXTEND =+ "multilib:multilib_name"

      .. note::

         Internally, the :term:`BBCLASSEXTEND` mechanism generates recipe
         variants by rewriting variable values and applying overrides such
         as ``:class-native``. For example, to generate a native version of
         a recipe, a :term:`DEPENDS` on "foo" is rewritten
         to a :term:`DEPENDS` on "foo-native".

         Even when using :term:`BBCLASSEXTEND`, the recipe is only parsed once.
         Parsing once adds some limitations. For example, it is not
         possible to include a different file depending on the variant,
         since ``include`` statements are processed when the recipe is
         parsed.

   :term:`BBDEBUG`
      See :term:`bitbake:BBDEBUG` in the BitBake manual.

   :term:`BBFILE_COLLECTIONS`
      Lists the names of configured layers. These names are used to find
      the other ``BBFILE_*`` variables. Typically, each layer will append
      its name to this variable in its ``conf/layer.conf`` file.

   :term:`BBFILE_PATTERN`
      Variable that expands to match files from
      :term:`BBFILES` in a particular layer. This variable
      is used in the ``conf/layer.conf`` file and must be suffixed with the
      name of the specific layer (e.g. ``BBFILE_PATTERN_emenlow``).

   :term:`BBFILE_PRIORITY`
      Assigns the priority for recipe files in each layer.

      This variable is useful in situations where the same recipe appears
      in more than one layer. Setting this variable allows you to
      prioritize a layer against other layers that contain the same recipe
      --- effectively letting you control the precedence for the multiple
      layers. The precedence established through this variable stands
      regardless of a recipe's version (:term:`PV` variable). For
      example, a layer that has a recipe with a higher :term:`PV` value but for
      which the :term:`BBFILE_PRIORITY` is set to have a lower precedence still
      has a lower precedence.

      A larger value for the :term:`BBFILE_PRIORITY` variable results in a
      higher precedence. For example, the value 6 has a higher precedence
      than the value 5. If not specified, the :term:`BBFILE_PRIORITY` variable
      is set based on layer dependencies (see the :term:`LAYERDEPENDS` variable
      for more information. The default priority, if unspecified for a
      layer with no dependencies, is the lowest defined priority + 1 (or 1
      if no priorities are defined).

      .. tip::

         You can use the command ``bitbake-layers show-layers``
         to list all configured layers along with their priorities.

   :term:`BBFILES`
      A space-separated list of recipe files BitBake uses to build
      software.

      When specifying recipe files, you can pattern match using Python's
      `glob <https://docs.python.org/3/library/glob.html>`__ syntax.
      For details on the syntax, see the documentation by following the
      previous link.

   :term:`BBFILES_DYNAMIC`
      Activates content when identified layers are present. You identify
      the layers by the collections that the layers define.

      Use the :term:`BBFILES_DYNAMIC` variable to avoid ``.bbappend`` files
      whose corresponding ``.bb`` file is in a layer that attempts to
      modify other layers through ``.bbappend`` but does not want to
      introduce a hard dependency on those other layers.

      Use the following form for :term:`BBFILES_DYNAMIC`:
      ``collection_name:filename_pattern``.

      The following example identifies two collection names and two
      filename patterns::

         BBFILES_DYNAMIC += " \
            clang-layer:${LAYERDIR}/bbappends/meta-clang/*/*/*.bbappend \
            core:${LAYERDIR}/bbappends/openembedded-core/meta/*/*/*.bbappend \
            "

      This next example shows an error message that occurs because invalid
      entries are found, which cause parsing to fail:

      .. code-block:: none

         ERROR: BBFILES_DYNAMIC entries must be of the form <collection name>:<filename pattern>, not:
             /work/my-layer/bbappends/meta-security-isafw/*/*/*.bbappend
             /work/my-layer/bbappends/openembedded-core/meta/*/*/*.bbappend

   :term:`BBINCLUDED`
      See :term:`bitbake:BBINCLUDED` in the BitBake manual.

   :term:`BBINCLUDELOGS`
      Variable that controls how BitBake displays logs on build failure.

   :term:`BBINCLUDELOGS_LINES`
      If :term:`BBINCLUDELOGS` is set, specifies the
      maximum number of lines from the task log file to print when
      reporting a failed task. If you do not set :term:`BBINCLUDELOGS_LINES`,
      the entire log is printed.

   :term:`BBLAYERS`
      Lists the layers to enable during the build. This variable is defined
      in the ``bblayers.conf`` configuration file in the :term:`Build Directory`.
      Here is an example::

         BBLAYERS = " \
             /home/scottrif/poky/meta \
             /home/scottrif/poky/meta-poky \
             /home/scottrif/poky/meta-yocto-bsp \
             /home/scottrif/poky/meta-mykernel \
             "

      This example enables four layers, one of which is a custom,
      user-defined layer named ``meta-mykernel``.

   :term:`BBLAYERS_FETCH_DIR`
      See :term:`bitbake:BBLAYERS_FETCH_DIR` in the BitBake manual.

   :term:`BBMASK`
      Prevents BitBake from processing recipes and recipe append files.

      You can use the :term:`BBMASK` variable to "hide" these ``.bb`` and
      ``.bbappend`` files. BitBake ignores any recipe or recipe append
      files that match any of the expressions. It is as if BitBake does not
      see them at all. Consequently, matching files are not parsed or
      otherwise used by BitBake.

      The values you provide are passed to Python's regular expression
      compiler. Consequently, the syntax follows Python's Regular
      Expression (re) syntax. The expressions are compared against the full
      paths to the files. For complete syntax information, see Python's
      documentation at https://docs.python.org/3/library/re.html#regular-expression-syntax.

      The following example uses a complete regular expression to tell
      BitBake to ignore all recipe and recipe append files in the
      ``meta-ti/recipes-misc/`` directory::

         BBMASK = "meta-ti/recipes-misc/"

      If you want to mask out multiple directories or recipes, you can
      specify multiple regular expression fragments. This next example
      masks out multiple directories and individual recipes::

         BBMASK += "/meta-ti/recipes-misc/ meta-ti/recipes-ti/packagegroup/"
         BBMASK += "/meta-oe/recipes-support/"
         BBMASK += "/meta-foo/.*/openldap"
         BBMASK += "opencv.*\.bbappend"
         BBMASK += "lzma"

      .. note::

         When specifying a directory name, use the trailing slash character
         to ensure you match just that directory name.

   :term:`BBMULTICONFIG`
      Specifies each additional separate configuration when you are
      building targets with multiple configurations. Use this variable in
      your ``conf/local.conf`` configuration file. Specify a
      multiconfigname for each configuration file you are using. For
      example, the following line specifies three configuration files::

         BBMULTICONFIG = "configA configB configC"

      Each configuration file you use must reside in a ``multiconfig``
      subdirectory of a configuration directory within a layer, or
      within the :term:`Build Directory` (e.g.
      ``build_directory/conf/multiconfig/configA.conf`` or
      ``mylayer/conf/multiconfig/configB.conf``).

      For information on how to use :term:`BBMULTICONFIG` in an environment
      that supports building targets with multiple configurations, see the
      ":ref:`dev-manual/building:building images for multiple targets using multiple configurations`"
      section in the Yocto Project Development Tasks Manual.

   :term:`BBPATH`
      See :term:`bitbake:BBPATH` in the BitBake manual.

   :term:`BBSERVER`
      If defined in the BitBake environment, :term:`BBSERVER` points to the
      BitBake remote server.

      Use the following format to export the variable to the BitBake
      environment::

         export BBSERVER=localhost:$port

      By default, :term:`BBSERVER` also appears in :term:`BB_BASEHASH_IGNORE_VARS`.
      Consequently, :term:`BBSERVER` is excluded from checksum and dependency
      data.

   :term:`BBTARGETS`
      See :term:`bitbake:BBTARGETS` in the BitBake manual.

   :term:`BINCONFIG`
      When inheriting the :ref:`ref-classes-binconfig-disabled` class, this
      variable specifies binary configuration scripts to disable in favor of
      using ``pkg-config`` to query the information. The
      :ref:`ref-classes-binconfig-disabled` class will modify the specified
      scripts to return an error so that calls to them can be easily found
      and replaced.

      To add multiple scripts, separate them by spaces. Here is an example
      from the ``libpng`` recipe::

         BINCONFIG = "${bindir}/libpng-config ${bindir}/libpng16-config"

   :term:`BINCONFIG_GLOB`
      When inheriting the :ref:`ref-classes-binconfig` class,
      this variable specifies a wildcard for configuration scripts that
      need editing. The scripts are edited to correct any paths that have
      been set up during compilation so that they are correct for use when
      installed into the sysroot and called by the build processes of other
      recipes.

      .. note::

         The :term:`BINCONFIG_GLOB` variable uses
         `shell globbing <https://tldp.org/LDP/abs/html/globbingref.html>`__,
         which is recognition and expansion of wildcards during pattern
         matching. Shell globbing is very similar to
         `fnmatch <https://docs.python.org/3/library/fnmatch.html#module-fnmatch>`__
         and `glob <https://docs.python.org/3/library/glob.html>`__.

      For more information on how this variable works, see
      ``meta/classes-recipe/binconfig.bbclass`` in the :term:`Source Directory`.
      You can also find general
      information on the class in the
      ":ref:`ref-classes-binconfig`" section.

   :term:`BITBAKE_UI`
      See :term:`bitbake:BITBAKE_UI` in the BitBake manual.

   :term:`BP`
      The base recipe name and version but without any special recipe name
      suffix (i.e. ``-native``, ``lib64-``, and so forth). :term:`BP` is
      comprised of the following::

         ${BPN}-${PV}

   :term:`BPN`
      This variable is a version of the :term:`PN` variable with
      common prefixes and suffixes removed, such as ``nativesdk-``,
      ``-cross``, ``-native``, and multilib's ``lib64-`` and ``lib32-``.
      The exact lists of prefixes and suffixes removed are specified by the
      :term:`MLPREFIX` and
      :term:`SPECIAL_PKGSUFFIX` variables,
      respectively.

   :term:`BUGTRACKER`
      Specifies a URL for an upstream bug tracking website for a recipe.
      The OpenEmbedded build system does not use this variable. Rather, the
      variable is a useful pointer in case a bug in the software being
      built needs to be manually reported.

   :term:`BUILD_ARCH`
      Specifies the architecture of the build host (e.g. ``i686``). The
      OpenEmbedded build system sets the value of :term:`BUILD_ARCH` from the
      machine name reported by the ``uname`` command.

   :term:`BUILD_AS_ARCH`
      Specifies the architecture-specific assembler flags for the build
      host. By default, the value of :term:`BUILD_AS_ARCH` is empty.

   :term:`BUILD_CC_ARCH`
      Specifies the architecture-specific C compiler flags for the build
      host. By default, the value of :term:`BUILD_CC_ARCH` is empty.

   :term:`BUILD_CCLD`
      Specifies the linker command to be used for the build host when the C
      compiler is being used as the linker. By default, :term:`BUILD_CCLD`
      points to GCC and passes as arguments the value of
      :term:`BUILD_CC_ARCH`, assuming
      :term:`BUILD_CC_ARCH` is set.

   :term:`BUILD_CFLAGS`
      Specifies the flags to pass to the C compiler when building for the
      build host. When building in the ``-native`` context,
      :term:`CFLAGS` is set to the value of this variable by
      default.

   :term:`BUILD_CPPFLAGS`
      Specifies the flags to pass to the C preprocessor (i.e. to both the C
      and the C++ compilers) when building for the build host. When
      building in the ``-native`` context, :term:`CPPFLAGS`
      is set to the value of this variable by default.

   :term:`BUILD_CXXFLAGS`
      Specifies the flags to pass to the C++ compiler when building for the
      build host. When building in the ``-native`` context,
      :term:`CXXFLAGS` is set to the value of this variable
      by default.

   :term:`BUILD_FC`
      Specifies the Fortran compiler command for the build host. By
      default, :term:`BUILD_FC` points to Gfortran and passes as arguments the
      value of :term:`BUILD_CC_ARCH`, assuming
      :term:`BUILD_CC_ARCH` is set.

   :term:`BUILD_LD`
      Specifies the linker command for the build host. By default,
      :term:`BUILD_LD` points to the GNU linker (ld) and passes as arguments
      the value of :term:`BUILD_LD_ARCH`, assuming
      :term:`BUILD_LD_ARCH` is set.

   :term:`BUILD_LD_ARCH`
      Specifies architecture-specific linker flags for the build host. By
      default, the value of :term:`BUILD_LD_ARCH` is empty.

   :term:`BUILD_LDFLAGS`
      Specifies the flags to pass to the linker when building for the build
      host. When building in the ``-native`` context,
      :term:`LDFLAGS` is set to the value of this variable
      by default.

   :term:`BUILD_OPTIMIZATION`
      Specifies the optimization flags passed to the C compiler when
      building for the build host or the SDK. The flags are passed through
      the :term:`BUILD_CFLAGS` and
      :term:`BUILDSDK_CFLAGS` default values.

      The default value of the :term:`BUILD_OPTIMIZATION` variable is "-O2
      -pipe".

   :term:`BUILD_OS`
      Specifies the operating system in use on the build host (e.g.
      "linux"). The OpenEmbedded build system sets the value of
      :term:`BUILD_OS` from the OS reported by the ``uname`` command --- the
      first word, converted to lower-case characters.

   :term:`BUILD_PREFIX`
      The toolchain binary prefix used for native recipes. The OpenEmbedded
      build system uses the :term:`BUILD_PREFIX` value to set the
      :term:`TARGET_PREFIX` when building for :ref:`ref-classes-native` recipes.

   :term:`BUILD_STRIP`
      Specifies the command to be used to strip debugging symbols from
      binaries produced for the build host. By default, :term:`BUILD_STRIP`
      points to
      ``${``\ :term:`BUILD_PREFIX`\ ``}strip``.

   :term:`BUILD_SYS`
      Specifies the system, including the architecture and the operating
      system, to use when building for the build host (i.e. when building
      :ref:`ref-classes-native` recipes).

      The OpenEmbedded build system automatically sets this variable based
      on :term:`BUILD_ARCH`,
      :term:`BUILD_VENDOR`, and
      :term:`BUILD_OS`. You do not need to set the
      :term:`BUILD_SYS` variable yourself.

   :term:`BUILD_VENDOR`
      Specifies the vendor name to use when building for the build host.
      The default value is an empty string ("").

   :term:`BUILDDIR`
      Points to the location of the :term:`Build Directory`. You can define
      this directory indirectly through the :ref:`structure-core-script` script
      by passing in a :term:`Build Directory` path when you run the script. If
      you run the script and do not provide a :term:`Build Directory` path, the
      :term:`BUILDDIR` defaults to ``build`` in the current directory.

   :term:`BUILDHISTORY_COMMIT`
      When inheriting the :ref:`ref-classes-buildhistory` class, this variable
      specifies whether or not to commit the build history output in a local
      Git repository. If set to "1", this local repository will be maintained
      automatically by the :ref:`ref-classes-buildhistory` class and a commit
      will be created on every build for changes to each top-level subdirectory
      of the build history output (images, packages, and sdk). If you want to
      track changes to build history over time, you should set this value to
      "1".

      By default, the :ref:`ref-classes-buildhistory` class
      enables committing the buildhistory output in a local Git repository::

         BUILDHISTORY_COMMIT ?= "1"

   :term:`BUILDHISTORY_COMMIT_AUTHOR`
      When inheriting the :ref:`ref-classes-buildhistory`
      class, this variable specifies the author to use for each Git commit.
      In order for the :term:`BUILDHISTORY_COMMIT_AUTHOR` variable to work, the
      :term:`BUILDHISTORY_COMMIT` variable must
      be set to "1".

      Git requires that the value you provide for the
      :term:`BUILDHISTORY_COMMIT_AUTHOR` variable takes the form of "name
      email@host". Providing an email address or host that is not valid
      does not produce an error.

      By default, the :ref:`ref-classes-buildhistory` class sets the variable
      as follows::

         BUILDHISTORY_COMMIT_AUTHOR ?= "buildhistory <buildhistory@${DISTRO}>"

   :term:`BUILDHISTORY_DIR`
      When inheriting the :ref:`ref-classes-buildhistory`
      class, this variable specifies the directory in which build history
      information is kept. For more information on how the variable works,
      see the :ref:`ref-classes-buildhistory` class.

      By default, the :ref:`ref-classes-buildhistory` class sets the directory
      as follows::

         BUILDHISTORY_DIR ?= "${TOPDIR}/buildhistory"

   :term:`BUILDHISTORY_FEATURES`
      When inheriting the :ref:`ref-classes-buildhistory`
      class, this variable specifies the build history features to be
      enabled. For more information on how build history works, see the
      ":ref:`dev-manual/build-quality:maintaining build output quality`"
      section in the Yocto Project Development Tasks Manual.

      You can specify these features in the form of a space-separated list:

      -  *image:* Analysis of the contents of images, which includes the
         list of installed packages among other things.

      -  *package:* Analysis of the contents of individual packages.

      -  *sdk:* Analysis of the contents of the software development kit
         (SDK).

      -  *task:* Save output file signatures for
         :ref:`shared state <overview-manual/concepts:shared state cache>`
         (sstate) tasks.
         This saves one file per task and lists the SHA-256 checksums for
         each file staged (i.e. the output of the task).

      By default, the :ref:`ref-classes-buildhistory` class enables the
      following features::

         BUILDHISTORY_FEATURES ?= "image package sdk"

   :term:`BUILDHISTORY_IMAGE_FILES`
      When inheriting the :ref:`ref-classes-buildhistory`
      class, this variable specifies a list of paths to files copied from
      the image contents into the build history directory under an
      "image-files" directory in the directory for the image, so that you
      can track the contents of each file. The default is to copy
      ``/etc/passwd`` and ``/etc/group``, which allows you to monitor for
      changes in user and group entries. You can modify the list to include
      any file. Specifying an invalid path does not produce an error.
      Consequently, you can include files that might not always be present.

      By default, the :ref:`ref-classes-buildhistory` class provides paths to
      the following files::

         BUILDHISTORY_IMAGE_FILES ?= "/etc/passwd /etc/group"

   :term:`BUILDHISTORY_PATH_PREFIX_STRIP`
      When inheriting the :ref:`ref-classes-buildhistory`
      class, this variable specifies a common path prefix that should be
      stripped off the beginning of paths in the task signature list when the
      ``task`` feature is active in :term:`BUILDHISTORY_FEATURES`. This can be
      useful when build history is populated from multiple sources that may not
      all use the same top level directory.

      By default, the :ref:`ref-classes-buildhistory` class sets the variable
      as follows::

         BUILDHISTORY_PATH_PREFIX_STRIP ?= ""

      In this case, no prefixes will be stripped.

   :term:`BUILDHISTORY_PUSH_REPO`
      When inheriting the :ref:`ref-classes-buildhistory` class, this variable
      optionally specifies a remote repository to which build history pushes
      Git changes. In order for :term:`BUILDHISTORY_PUSH_REPO` to work,
      :term:`BUILDHISTORY_COMMIT` must be set to "1".

      The repository should correspond to a remote address that specifies a
      repository as understood by Git, or alternatively to a remote name
      that you have set up manually using ``git remote`` within the local
      repository.

      By default, the :ref:`ref-classes-buildhistory` class sets the variable
      as follows::

         BUILDHISTORY_PUSH_REPO ?= ""

   :term:`BUILDNAME`
      See :term:`bitbake:BUILDNAME` in the BitBake manual.

   :term:`BUILDSDK_CFLAGS`
      Specifies the flags to pass to the C compiler when building for the
      SDK. When building in the ``nativesdk-`` context,
      :term:`CFLAGS` is set to the value of this variable by
      default.

   :term:`BUILDSDK_CPPFLAGS`
      Specifies the flags to pass to the C pre-processor (i.e. to both the
      C and the C++ compilers) when building for the SDK. When building in
      the ``nativesdk-`` context, :term:`CPPFLAGS` is set
      to the value of this variable by default.

   :term:`BUILDSDK_CXXFLAGS`
      Specifies the flags to pass to the C++ compiler when building for the
      SDK. When building in the ``nativesdk-`` context,
      :term:`CXXFLAGS` is set to the value of this variable
      by default.

   :term:`BUILDSDK_LDFLAGS`
      Specifies the flags to pass to the linker when building for the SDK.
      When building in the ``nativesdk-`` context,
      :term:`LDFLAGS` is set to the value of this variable
      by default.

   :term:`BUILDSTATS_BASE`
      Points to the location of the directory that holds build statistics
      when you use and enable the :ref:`ref-classes-buildstats` class. The
      :term:`BUILDSTATS_BASE` directory defaults to
      ``${``\ :term:`TMPDIR`\ ``}/buildstats/``.

   :term:`BUSYBOX_SPLIT_SUID`
      For the BusyBox recipe, specifies whether to split the output
      executable file into two parts: one for features that require
      ``setuid root``, and one for the remaining features (i.e. those that
      do not require ``setuid root``).

      The :term:`BUSYBOX_SPLIT_SUID` variable defaults to "1", which results in
      splitting the output executable file. Set the variable to "0" to get
      a single output executable file.

   :term:`BZRDIR`
      See :term:`bitbake:BZRDIR` in the BitBake manual.

   :term:`CACHE`
      Specifies the directory BitBake uses to store a cache of the
      :term:`Metadata` so it does not need to be parsed every time
      BitBake is started.

   :term:`CC`
      The minimal command and arguments used to run the C compiler.

   :term:`CFLAGS`
      Specifies the flags to pass to the C compiler. This variable is
      exported to an environment variable and thus made visible to the
      software being built during the compilation step.

      Default initialization for :term:`CFLAGS` varies depending on what is
      being built:

      -  :term:`TARGET_CFLAGS` when building for the
         target

      -  :term:`BUILD_CFLAGS` when building for the
         build host (i.e. ``-native``)

      -  :term:`BUILDSDK_CFLAGS` when building for
         an SDK (i.e. ``nativesdk-``)

   :term:`CLASSOVERRIDE`
      An internal variable specifying the special class override that
      should currently apply (e.g. "class-target", "class-native", and so
      forth). The classes that use this variable (e.g.
      :ref:`ref-classes-native`, :ref:`ref-classes-nativesdk`, and so forth)
      set the variable to appropriate values.

      .. note::

         :term:`CLASSOVERRIDE` gets its default "class-target" value from the
         ``bitbake.conf`` file.

      As an example, the following override allows you to install extra
      files, but only when building for the target::

         do_install:append:class-target() {
             install my-extra-file ${D}${sysconfdir}
         }

      Here is an example where ``FOO`` is set to
      "native" when building for the build host, and to "other" when not
      building for the build host::

         FOO:class-native = "native"
         FOO = "other"

      The underlying mechanism behind :term:`CLASSOVERRIDE` is simply
      that it is included in the default value of
      :term:`OVERRIDES`.

   :term:`CLEANBROKEN`
      If set to "1" within a recipe, :term:`CLEANBROKEN` specifies that the
      ``make clean`` command does not work for the software being built.
      Consequently, the OpenEmbedded build system will not try to run
      ``make clean`` during the :ref:`ref-tasks-configure`
      task, which is the default behavior.

   :term:`COMBINED_FEATURES`
      Provides a list of hardware features that are enabled in both
      :term:`MACHINE_FEATURES` and
      :term:`DISTRO_FEATURES`. This select list of
      features contains features that make sense to be controlled both at
      the machine and distribution configuration level. For example, the
      "bluetooth" feature requires hardware support but should also be
      optional at the distribution level, in case the hardware supports
      Bluetooth but you do not ever intend to use it.

   :term:`COMMERCIAL_AUDIO_PLUGINS`
      This variable is specific to the :yocto_git:`GStreamer recipes
      </poky/tree/meta/recipes-multimedia/gstreamer/gstreamer1.0-meta-base.bb>`.
      It allows to build the GStreamer `"ugly"
      <https://github.com/GStreamer/gst-plugins-ugly>`__ and
      `"bad" <https://github.com/GStreamer/gst-plugins-bad>`__ audio plugins.

      See the :ref:`dev-manual/licenses:other variables related to commercial licenses`
      section for usage details.

   :term:`COMMERCIAL_VIDEO_PLUGINS`
      This variable is specific to the :yocto_git:`GStreamer recipes
      </poky/tree/meta/recipes-multimedia/gstreamer/gstreamer1.0-meta-base.bb>`.
      It allows to build the GStreamer `"ugly"
      <https://github.com/GStreamer/gst-plugins-ugly>`__ and
      `"bad" <https://github.com/GStreamer/gst-plugins-bad>`__ video plugins.

      See the :ref:`dev-manual/licenses:other variables related to commercial licenses`
      section for usage details.

   :term:`COMMON_LICENSE_DIR`
      Points to ``meta/files/common-licenses`` in the
      :term:`Source Directory`, which is where generic license
      files reside.

   :term:`COMPATIBLE_HOST`
      A regular expression that resolves to one or more hosts (when the
      recipe is native) or one or more targets (when the recipe is
      non-native) with which a recipe is compatible. The regular expression
      is matched against :term:`HOST_SYS`. You can use the
      variable to stop recipes from being built for classes of systems with
      which the recipes are not compatible. Stopping these builds is
      particularly useful with kernels. The variable also helps to increase
      parsing speed since the build system skips parsing recipes not
      compatible with the current system.

   :term:`COMPATIBLE_MACHINE`
      A regular expression that resolves to one or more target machines
      with which a recipe is compatible. The regular expression is matched
      against :term:`MACHINEOVERRIDES`. You can use
      the variable to stop recipes from being built for machines with which
      the recipes are not compatible. Stopping these builds is particularly
      useful with kernels. The variable also helps to increase parsing
      speed since the build system skips parsing recipes not compatible
      with the current machine.

      If one wants to have a recipe only available for some architectures
      (here ``aarch64`` and ``mips64``), the following can be used::

         COMPATIBLE_MACHINE = "^$"
         COMPATIBLE_MACHINE:arch64 = "^(aarch64)$"
         COMPATIBLE_MACHINE:mips64 = "^(mips64)$"

      The first line means "match all machines whose :term:`MACHINEOVERRIDES`
      contains the empty string", which will always be none.

      The second is for matching all machines whose :term:`MACHINEOVERRIDES`
      contains one override which is exactly ``aarch64``.

      The third is for matching all machines whose :term:`MACHINEOVERRIDES`
      contains one override which is exactly ``mips64``.

      The same could be achieved with::

         COMPATIBLE_MACHINE = "^(aarch64|mips64)$"

      .. note::

         When :term:`COMPATIBLE_MACHINE` is set in a recipe inherits from
         native, the recipe is always skipped. All native recipes must be
         entirely target independent and should not rely on :term:`MACHINE`.

   :term:`COMPLEMENTARY_GLOB`
      Defines wildcards to match when installing a list of complementary
      packages for all the packages explicitly (or implicitly) installed in
      an image.

      The :term:`COMPLEMENTARY_GLOB` variable uses Unix filename pattern matching
      (`fnmatch <https://docs.python.org/3/library/fnmatch.html#module-fnmatch>`__),
      which is similar to the Unix style pathname pattern expansion
      (`glob <https://docs.python.org/3/library/glob.html>`__).

      The resulting list of complementary packages is associated with an
      item that can be added to
      :term:`IMAGE_FEATURES`. An example usage of
      this is the "dev-pkgs" item that when added to :term:`IMAGE_FEATURES`
      will install -dev packages (containing headers and other development
      files) for every package in the image.

      To add a new feature item pointing to a wildcard, use a variable flag
      to specify the feature item name and use the value to specify the
      wildcard. Here is an example::

         COMPLEMENTARY_GLOB[dev-pkgs] = '*-dev'

      .. note::

         When installing complementary packages, recommends relationships
         (set via :term:`RRECOMMENDS`) are always ignored.

   :term:`COMPONENTS_DIR`
      Stores sysroot components for each recipe. The OpenEmbedded build
      system uses :term:`COMPONENTS_DIR` when constructing recipe-specific
      sysroots for other recipes.

      The default is
      "``${``\ :term:`STAGING_DIR`\ ``}-components``."
      (i.e.
      "``${``\ :term:`TMPDIR`\ ``}/sysroots-components``").

   :term:`CONF_VERSION`
      Tracks the version of the local configuration file (i.e.
      ``local.conf``). The value for :term:`CONF_VERSION` increments each time
      ``build/conf/`` compatibility changes.

   :term:`CONFFILES`
      Identifies editable or configurable files that are part of a package.
      If the Package Management System (PMS) is being used to update
      packages on the target system, it is possible that configuration
      files you have changed after the original installation and that you
      now want to remain unchanged are overwritten. In other words,
      editable files might exist in the package that you do not want reset
      as part of the package update process. You can use the :term:`CONFFILES`
      variable to list the files in the package that you wish to prevent
      the PMS from overwriting during this update process.

      To use the :term:`CONFFILES` variable, provide a package name override
      that identifies the resulting package. Then, provide a
      space-separated list of files. Here is an example::

         CONFFILES:${PN} += "${sysconfdir}/file1 \
             ${sysconfdir}/file2 ${sysconfdir}/file3"

      There is a relationship between the :term:`CONFFILES` and :term:`FILES`
      variables. The files listed within :term:`CONFFILES` must be a subset of
      the files listed within :term:`FILES`. Because the configuration files
      you provide with :term:`CONFFILES` are simply being identified so that
      the PMS will not overwrite them, it makes sense that the files must
      already be included as part of the package through the :term:`FILES`
      variable.

      .. note::

         When specifying paths as part of the :term:`CONFFILES` variable, it is
         good practice to use appropriate path variables.
         For example, ``${sysconfdir}`` rather than ``/etc`` or ``${bindir}``
         rather than ``/usr/bin``. You can find a list of these variables at
         the top of the ``meta/conf/bitbake.conf`` file in the
         :term:`Source Directory`.

   :term:`CONFIG_INITRAMFS_SOURCE`
      Identifies the initial RAM filesystem (:term:`Initramfs`) source files. The
      OpenEmbedded build system receives and uses this kernel Kconfig
      variable as an environment variable. By default, the variable is set
      to null ("").

      The :term:`CONFIG_INITRAMFS_SOURCE` can be either a single cpio archive
      with a ``.cpio`` suffix or a space-separated list of directories and
      files for building the :term:`Initramfs` image. A cpio archive should contain
      a filesystem archive to be used as an :term:`Initramfs` image. Directories
      should contain a filesystem layout to be included in the :term:`Initramfs`
      image. Files should contain entries according to the format described
      by the ``usr/gen_init_cpio`` program in the kernel tree.

      If you specify multiple directories and files, the :term:`Initramfs` image
      will be the aggregate of all of them.

      For information on creating an :term:`Initramfs`, see the
      ":ref:`dev-manual/building:building an initial ram filesystem (Initramfs) image`" section
      in the Yocto Project Development Tasks Manual.

   :term:`CONFIG_SITE`
      A list of files that contains ``autoconf`` test results relevant to
      the current build. This variable is used by the Autotools utilities
      when running ``configure``.

   :term:`CONFIGURE_FLAGS`
      The minimal arguments for GNU configure.

   :term:`CONFLICT_DISTRO_FEATURES`
      When inheriting the :ref:`ref-classes-features_check`
      class, this variable identifies distribution features that would be
      in conflict should the recipe be built. In other words, if the
      :term:`CONFLICT_DISTRO_FEATURES` variable lists a feature that also
      appears in :term:`DISTRO_FEATURES` within the current configuration, then
      the recipe will be skipped, and if the build system attempts to build
      the recipe then an error will be triggered.

   :term:`CONVERSION_CMD`
      This variable is used for storing image conversion commands.
      Image conversion can convert an image into different objects like:

      -   Compressed version of the image

      -   Checksums for the image

      An example of :term:`CONVERSION_CMD` from :ref:`ref-classes-image_types`
      class is::

         CONVERSION_CMD:lzo = "lzop -9 ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"

   :term:`COPY_LIC_DIRS`
      If set to "1" along with the
      :term:`COPY_LIC_MANIFEST` variable, the
      OpenEmbedded build system copies into the image the license files,
      which are located in ``/usr/share/common-licenses``, for each
      package. The license files are placed in directories within the image
      itself during build time.

      .. note::

         The :term:`COPY_LIC_DIRS` does not offer a path for adding licenses for
         newly installed packages to an image, which might be most suitable for
         read-only filesystems that cannot be upgraded. See the
         :term:`LICENSE_CREATE_PACKAGE` variable for additional information.
         You can also reference the ":ref:`dev-manual/licenses:providing license text`"
         section in the Yocto Project Development Tasks Manual for
         information on providing license text.

   :term:`COPY_LIC_MANIFEST`
      If set to "1", the OpenEmbedded build system copies the license
      manifest for the image to
      ``/usr/share/common-licenses/license.manifest`` within the image
      itself during build time.

      .. note::

         The :term:`COPY_LIC_MANIFEST` does not offer a path for adding licenses for
         newly installed packages to an image, which might be most suitable for
         read-only filesystems that cannot be upgraded. See the
         :term:`LICENSE_CREATE_PACKAGE` variable for additional information.
         You can also reference the ":ref:`dev-manual/licenses:providing license text`"
         section in the Yocto Project Development Tasks Manual for
         information on providing license text.

   :term:`COPYLEFT_LICENSE_EXCLUDE`
      A space-separated list of licenses to exclude from the source archived by
      the :ref:`ref-classes-archiver` class. In other words, if a license in a
      recipe's :term:`LICENSE` value is in the value of
      :term:`COPYLEFT_LICENSE_EXCLUDE`, then its source is not archived by the
      class.

      .. note::

         The :term:`COPYLEFT_LICENSE_EXCLUDE` variable takes precedence over the
         :term:`COPYLEFT_LICENSE_INCLUDE` variable.

      The default value, which is "CLOSED Proprietary", for
      :term:`COPYLEFT_LICENSE_EXCLUDE` is set by the
      :ref:`ref-classes-copyleft_filter` class, which
      is inherited by the :ref:`ref-classes-archiver` class.

   :term:`COPYLEFT_LICENSE_INCLUDE`
      A space-separated list of licenses to include in the source archived
      by the :ref:`ref-classes-archiver` class. In other
      words, if a license in a recipe's :term:`LICENSE`
      value is in the value of :term:`COPYLEFT_LICENSE_INCLUDE`, then its
      source is archived by the class.

      The default value is set by the :ref:`ref-classes-copyleft_filter` class,
      which is inherited by the :ref:`ref-classes-archiver` class. The default
      value includes "GPL*", "LGPL*", and "AGPL*".

   :term:`COPYLEFT_PN_EXCLUDE`
      A list of recipes to exclude in the source archived by the
      :ref:`ref-classes-archiver` class. The :term:`COPYLEFT_PN_EXCLUDE`
      variable overrides the license inclusion and exclusion caused through the
      :term:`COPYLEFT_LICENSE_INCLUDE` and :term:`COPYLEFT_LICENSE_EXCLUDE`
      variables, respectively.

      The default value, which is "" indicating to not explicitly exclude
      any recipes by name, for :term:`COPYLEFT_PN_EXCLUDE` is set by the
      :ref:`ref-classes-copyleft_filter` class, which is inherited by the
      :ref:`ref-classes-archiver` class.

   :term:`COPYLEFT_PN_INCLUDE`
      A list of recipes to include in the source archived by the
      :ref:`ref-classes-archiver` class. The :term:`COPYLEFT_PN_INCLUDE`
      variable overrides the license inclusion and exclusion caused through the
      :term:`COPYLEFT_LICENSE_INCLUDE` and :term:`COPYLEFT_LICENSE_EXCLUDE`
      variables, respectively.

      The default value, which is "" indicating to not explicitly include
      any recipes by name, for :term:`COPYLEFT_PN_INCLUDE` is set by the
      :ref:`ref-classes-copyleft_filter` class, which is inherited by the
      :ref:`ref-classes-archiver` class.

   :term:`COPYLEFT_RECIPE_TYPES`
      A space-separated list of recipe types to include in the source
      archived by the :ref:`archiver <ref-classes-archiver>` class.
      Recipe types are ``target``, :ref:`ref-classes-native`,
      :ref:`ref-classes-nativesdk`, :ref:`ref-classes-cross`,
      :ref:`ref-classes-crosssdk`, and :ref:`ref-classes-cross-canadian`.

      The default value, which is "target*", for :term:`COPYLEFT_RECIPE_TYPES`
      is set by the :ref:`ref-classes-copyleft_filter` class, which is
      inherited by the :ref:`ref-classes-archiver` class.

   :term:`CORE_IMAGE_EXTRA_INSTALL`
      Specifies the list of packages to be added to the image. You should
      only set this variable in the ``local.conf`` configuration file found
      in the :term:`Build Directory`.

      This variable replaces ``POKY_EXTRA_INSTALL``, which is no longer
      supported.

   :term:`COREBASE`
      Specifies the parent directory of the OpenEmbedded-Core Metadata
      layer (i.e. ``meta``).

      It is an important distinction that :term:`COREBASE` points to the parent
      of this layer and not the layer itself. Consider an example where you
      have cloned the Poky Git repository and retained the ``poky`` name
      for your local copy of the repository. In this case, :term:`COREBASE`
      points to the ``poky`` folder because it is the parent directory of
      the ``poky/meta`` layer.

   :term:`COREBASE_FILES`
      Lists files from the :term:`COREBASE` directory that
      should be copied other than the layers listed in the
      ``bblayers.conf`` file. The :term:`COREBASE_FILES` variable allows
      to copy metadata from the OpenEmbedded build system
      into the extensible SDK.

      Explicitly listing files in :term:`COREBASE` is needed because it
      typically contains build directories and other files that should not
      normally be copied into the extensible SDK. Consequently, the value
      of :term:`COREBASE_FILES` is used in order to only copy the files that
      are actually needed.

   :term:`CPP`
      The minimal command and arguments used to run the C preprocessor.

   :term:`CPPFLAGS`
      Specifies the flags to pass to the C pre-processor (i.e. to both the
      C and the C++ compilers). This variable is exported to an environment
      variable and thus made visible to the software being built during the
      compilation step.

      Default initialization for :term:`CPPFLAGS` varies depending on what is
      being built:

      -  :term:`TARGET_CPPFLAGS` when building for
         the target

      -  :term:`BUILD_CPPFLAGS` when building for the
         build host (i.e. ``-native``)

      -  :term:`BUILDSDK_CPPFLAGS` when building
         for an SDK (i.e. ``nativesdk-``)

   :term:`CROSS_COMPILE`
      The toolchain binary prefix for the target tools. The
      :term:`CROSS_COMPILE` variable is the same as the
      :term:`TARGET_PREFIX` variable.

      .. note::

         The OpenEmbedded build system sets the :term:`CROSS_COMPILE`
         variable only in certain contexts (e.g. when building for kernel
         and kernel module recipes).

   :term:`CVE_CHECK_IGNORE`
      This variable is deprecated and should be replaced by :term:`CVE_STATUS`.

   :term:`CVE_CHECK_SHOW_WARNINGS`
      Specifies whether or not the :ref:`ref-classes-cve-check`
      class should generate warning messages on the console when unpatched
      CVEs are found. The default is "1", but you may wish to set it to "0" if
      you are already examining/processing the logs after the build has
      completed and thus do not need the warning messages.

   :term:`CVE_CHECK_SKIP_RECIPE`
      The list of package names (:term:`PN`) for which
      CVEs (Common Vulnerabilities and Exposures) are ignored.

   :term:`CVE_DB_INCR_UPDATE_AGE_THRES`
      Specifies the maximum age of the CVE database in seconds for an
      incremental update (instead of a full-download). Use "0" to force a
      full-download.

   :term:`CVE_DB_UPDATE_INTERVAL`
      Specifies the CVE database update interval in seconds, as used by
      ``cve-update-db-native``. The default value is "86400" i.e. once a day
      (24*60*60). If the value is set to "0" then the update will be forced
      every time. Alternatively, a negative value e.g. "-1" will disable
      updates entirely.

   :term:`CVE_PRODUCT`
      In a recipe, defines the name used to match the recipe name
      against the name in the upstream `NIST CVE database <https://nvd.nist.gov/>`__.

      The default is ${:term:`BPN`} (except for recipes that inherit the
      :ref:`ref-classes-pypi` class where it is set based upon
      :term:`PYPI_PACKAGE`). If it does not match the name in the NIST CVE
      database or matches with multiple entries in the database, the default
      value needs to be changed.

      Here is an example from the :oe_layerindex:`Berkeley DB recipe </layerindex/recipe/544>`::

         CVE_PRODUCT = "oracle_berkeley_db berkeley_db"

      Sometimes the product name is not specific enough, for example
      "tar" has been matching CVEs for the GNU ``tar`` package and also
      the ``node-tar`` node.js extension. To avoid this problem, use the
      vendor name as a prefix. The syntax for this is::

         CVE_PRODUCT = "vendor:package"

   :term:`CVE_STATUS`
      The CVE ID which is patched or should be ignored. Here is
      an example from the :oe_layerindex:`Python3 recipe</layerindex/recipe/23823>`::

         CVE_STATUS[CVE-2020-15523] = "not-applicable-platform: Issue only applies on Windows"

      It has the format "reason: description" and the description is optional.
      The Reason is mapped to the final CVE state by mapping via
      :term:`CVE_CHECK_STATUSMAP`. See :ref:`dev-manual/vulnerabilities:fixing vulnerabilities in recipes`
      for details.

   :term:`CVE_STATUS_GROUPS`
      If there are many CVEs with the same status and reason, they can by simplified by using this
      variable instead of many similar lines with :term:`CVE_STATUS`::

         CVE_STATUS_GROUPS = "CVE_STATUS_WIN CVE_STATUS_PATCHED"

         CVE_STATUS_WIN = "CVE-1234-0001 CVE-1234-0002"
         CVE_STATUS_WIN[status] = "not-applicable-platform: Issue only applies on Windows"
         CVE_STATUS_PATCHED = "CVE-1234-0003 CVE-1234-0004"
         CVE_STATUS_PATCHED[status] = "fixed-version: Fixed externally"

   :term:`CVE_CHECK_STATUSMAP`
      Mapping variable for all possible reasons of :term:`CVE_STATUS`:
      ``Patched``, ``Unpatched`` and ``Ignored``.
      See :ref:`ref-classes-cve-check` or ``meta/conf/cve-check-map.conf`` for more details::

         CVE_CHECK_STATUSMAP[cpe-incorrect] = "Ignored"

   :term:`CVE_VERSION`
      In a recipe, defines the version used to match the recipe version
      against the version in the `NIST CVE database <https://nvd.nist.gov/>`__
      when usign :ref:`ref-classes-cve-check`.

      The default is ${:term:`PV`} but if recipes use custom version numbers
      which do not map to upstream software component release versions and the versions
      used in the CVE database, then this variable can be used to set the
      version number for :ref:`ref-classes-cve-check`. Example::

          CVE_VERSION = "2.39"

   :term:`CVSDIR`
      The directory in which files checked out under the CVS system are
      stored.

   :term:`CXX`
      The minimal command and arguments used to run the C++ compiler.

   :term:`CXXFLAGS`
      Specifies the flags to pass to the C++ compiler. This variable is
      exported to an environment variable and thus made visible to the
      software being built during the compilation step.

      Default initialization for :term:`CXXFLAGS` varies depending on what is
      being built:

      -  :term:`TARGET_CXXFLAGS` when building for
         the target

      -  :term:`BUILD_CXXFLAGS` when building for the
         build host (i.e. ``-native``)

      -  :term:`BUILDSDK_CXXFLAGS` when building
         for an SDK (i.e. ``nativesdk-``)

   :term:`D`
      The destination directory. The location in the :term:`Build Directory`
      where components are installed by the
      :ref:`ref-tasks-install` task. This location defaults
      to::

         ${WORKDIR}/image

      .. note::

         Tasks that read from or write to this directory should run under
         :ref:`fakeroot <overview-manual/concepts:fakeroot and pseudo>`.

   :term:`DATE`
      The date the build was started. Dates appear using the year, month,
      and day (YMD) format (e.g. "20150209" for February 9th, 2015).

   :term:`DATETIME`
      The date and time on which the current build started. The format is
      suitable for timestamps.

   :term:`DEBIAN_NOAUTONAME`
      When the :ref:`ref-classes-debian` class is inherited,
      which is the default behavior, :term:`DEBIAN_NOAUTONAME` specifies a
      particular package should not be renamed according to Debian library
      package naming. You must use the package name as an override when you
      set this variable. Here is an example from the ``fontconfig`` recipe::

         DEBIAN_NOAUTONAME:fontconfig-utils = "1"

   :term:`DEBIANNAME`
      When the :ref:`ref-classes-debian` class is inherited,
      which is the default behavior, :term:`DEBIANNAME` allows you to override
      the library name for an individual package. Overriding the library
      name in these cases is rare. You must use the package name as an
      override when you set this variable. Here is an example from the
      ``dbus`` recipe::

         DEBIANNAME:${PN} = "dbus-1"

   :term:`DEBUG_BUILD`
      Specifies to build packages with debugging information. This
      influences the value of the :term:`SELECTED_OPTIMIZATION` variable.

   :term:`DEBUG_OPTIMIZATION`
      The options to pass in :term:`TARGET_CFLAGS` and :term:`CFLAGS` when
      compiling a system for debugging. This variable defaults to "-O
      -fno-omit-frame-pointer ${DEBUG_FLAGS} -pipe".

   :term:`DEBUG_PREFIX_MAP`
      Allows to set C compiler options, such as ``-fdebug-prefix-map``,
      ``-fmacro-prefix-map``, and ``-ffile-prefix-map``, which allow to
      replace build-time paths by install-time ones in the debugging sections
      of binaries.  This makes compiler output files location independent,
      at the cost of having to pass an extra command to tell the debugger
      where source files are.

      This is used by the Yocto Project to guarantee
      :doc:`/test-manual/reproducible-builds` even when the source code of
      a package uses the ``__FILE__`` or ``assert()`` macros. See the
      `reproducible-builds.org <https://reproducible-builds.org/docs/build-path/>`__
      website for details.

      This variable is set in the ``meta/conf/bitbake.conf`` file. It is
      not intended to be user-configurable.

   :term:`DEFAULT_PREFERENCE`
      Specifies a weak bias for recipe selection priority.

      The most common usage of this is variable is to set it to "-1" within
      a recipe for a development version of a piece of software. Using the
      variable in this way causes the stable version of the recipe to build
      by default in the absence of :term:`PREFERRED_VERSION` being used to
      build the development version.

      .. note::

         The bias provided by :term:`DEFAULT_PREFERENCE` is weak and is overridden
         by :term:`BBFILE_PRIORITY` if that variable is different between two
         layers that contain different versions of the same recipe.

   :term:`DEFAULTTUNE`
      The default CPU and Application Binary Interface (ABI) tunings (i.e.
      the "tune") used by the OpenEmbedded build system. The
      :term:`DEFAULTTUNE` helps define
      :term:`TUNE_FEATURES`.

      The default tune is either implicitly or explicitly set by the
      machine (:term:`MACHINE`). However, you can override
      the setting using available tunes as defined with
      :term:`AVAILTUNES`.

   :term:`DEPENDS`
      Lists a recipe's build-time dependencies. These are dependencies on
      other recipes whose contents (e.g. headers and shared libraries) are
      needed by the recipe at build time.

      As an example, consider a recipe ``foo`` that contains the following
      assignment::

          DEPENDS = "bar"

      The practical effect of the previous assignment is that all files
      installed by bar will be available in the appropriate staging sysroot,
      given by the :term:`STAGING_DIR* <STAGING_DIR>` variables, by the time
      the :ref:`ref-tasks-configure` task for ``foo`` runs. This mechanism is
      implemented by having :ref:`ref-tasks-configure` depend on the
      :ref:`ref-tasks-populate_sysroot` task of each recipe listed in
      :term:`DEPENDS`, through a
      ``[``\ :ref:`deptask <bitbake-user-manual/bitbake-user-manual-metadata:variable flags>`\ ``]``
      declaration in the :ref:`ref-classes-base` class.

      .. note::

         It seldom is necessary to reference, for example, :term:`STAGING_DIR_HOST`
         explicitly. The standard classes and build-related variables are
         configured to automatically use the appropriate staging sysroots.

      As another example, :term:`DEPENDS` can also be used to add utilities
      that run on the build machine during the build. For example, a recipe
      that makes use of a code generator built by the recipe ``codegen``
      might have the following::

         DEPENDS = "codegen-native"

      For more
      information, see the :ref:`ref-classes-native` class and
      the :term:`EXTRANATIVEPATH` variable.

      .. note::

         -  :term:`DEPENDS` is a list of recipe names. Or, to be more precise,
            it is a list of :term:`PROVIDES` names, which
            usually match recipe names. Putting a package name such as
            "foo-dev" in :term:`DEPENDS` does not make sense. Use "foo"
            instead, as this will put files from all the packages that make
            up ``foo``, which includes those from ``foo-dev``, into the
            sysroot.

         -  One recipe having another recipe in :term:`DEPENDS` does not by
            itself add any runtime dependencies between the packages
            produced by the two recipes. However, as explained in the
            ":ref:`overview-manual/concepts:automatically added runtime dependencies`"
            section in the Yocto Project Overview and Concepts Manual,
            runtime dependencies will often be added automatically, meaning
            :term:`DEPENDS` alone is sufficient for most recipes.

         -  Counterintuitively, :term:`DEPENDS` is often necessary even for
            recipes that install precompiled components. For example, if
            ``libfoo`` is a precompiled library that links against
            ``libbar``, then linking against ``libfoo`` requires both
            ``libfoo`` and ``libbar`` to be available in the sysroot.
            Without a :term:`DEPENDS` from the recipe that installs ``libfoo``
            to the recipe that installs ``libbar``, other recipes might
            fail to link against ``libfoo``.

      For information on runtime dependencies, see the :term:`RDEPENDS`
      variable. You can also see the
      ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:tasks`" and
      ":ref:`bitbake-user-manual/bitbake-user-manual-execution:dependencies`"
      sections in the BitBake User Manual for additional information on tasks
      and dependencies.

   :term:`DEPLOY_DIR`
      Points to the general area that the OpenEmbedded build system uses to
      place images, packages, SDKs, and other output files that are ready
      to be used outside of the build system. By default, this directory
      resides within the :term:`Build Directory` as ``${TMPDIR}/deploy``.

      For more information on the structure of the Build Directory, see
      ":ref:`ref-manual/structure:the build directory --- \`\`build/\`\``" section.
      For more detail on the contents of the ``deploy`` directory, see the
      ":ref:`overview-manual/concepts:images`",
      ":ref:`overview-manual/concepts:package feeds`", and
      ":ref:`overview-manual/concepts:application development sdk`" sections all in the
      Yocto Project Overview and Concepts Manual.

   :term:`DEPLOY_DIR_DEB`
      Points to the area that the OpenEmbedded build system uses to place
      Debian packages that are ready to be used outside of the build
      system. This variable applies only when :term:`PACKAGE_CLASSES` contains
      ":ref:`ref-classes-package_deb`".

      The BitBake configuration file initially defines the
      :term:`DEPLOY_DIR_DEB` variable as a sub-folder of
      :term:`DEPLOY_DIR`::

         DEPLOY_DIR_DEB = "${DEPLOY_DIR}/deb"

      The :ref:`ref-classes-package_deb` class uses the
      :term:`DEPLOY_DIR_DEB` variable to make sure the
      :ref:`ref-tasks-package_write_deb` task
      writes Debian packages into the appropriate folder. For more
      information on how packaging works, see the
      ":ref:`overview-manual/concepts:package feeds`" section
      in the Yocto Project Overview and Concepts Manual.

   :term:`DEPLOY_DIR_IMAGE`
      Points to the area that the OpenEmbedded build system uses to place
      images and other associated output files that are ready to be
      deployed onto the target machine. The directory is machine-specific
      as it contains the ``${MACHINE}`` name. By default, this directory
      resides within the :term:`Build Directory` as
      ``${DEPLOY_DIR}/images/${MACHINE}/``.

      It must not be used directly in recipes when deploying files. Instead,
      it's only useful when a recipe needs to "read" a file already deployed
      by a dependency. So, it should be filled with the contents of
      :term:`DEPLOYDIR` by the :ref:`ref-classes-deploy` class or with the
      contents of :term:`IMGDEPLOYDIR` by the :ref:`ref-classes-image` class.

      For more information on the structure of the :term:`Build Directory`, see
      ":ref:`ref-manual/structure:the build directory --- \`\`build/\`\``" section.
      For more detail on the contents of the ``deploy`` directory, see the
      ":ref:`overview-manual/concepts:images`" and
      ":ref:`overview-manual/concepts:application development sdk`" sections both in
      the Yocto Project Overview and Concepts Manual.

   :term:`DEPLOY_DIR_IPK`
      Points to the area that the OpenEmbedded build system uses to place
      IPK packages that are ready to be used outside of the build system.
      This variable applies only when :term:`PACKAGE_CLASSES` contains
      ":ref:`ref-classes-package_ipk`".

      The BitBake configuration file initially defines this variable as a
      sub-folder of :term:`DEPLOY_DIR`::

         DEPLOY_DIR_IPK = "${DEPLOY_DIR}/ipk"

      The :ref:`ref-classes-package_ipk` class uses the :term:`DEPLOY_DIR_IPK`
      variable to make sure the :ref:`ref-tasks-package_write_ipk` task
      writes IPK packages into the appropriate folder. For more information
      on how packaging works, see the
      ":ref:`overview-manual/concepts:package feeds`" section
      in the Yocto Project Overview and Concepts Manual.

   :term:`DEPLOY_DIR_RPM`
      Points to the area that the OpenEmbedded build system uses to place
      RPM packages that are ready to be used outside of the build system.
      This variable applies only when :term:`PACKAGE_CLASSES` contains
      ":ref:`ref-classes-package_rpm`".

      The BitBake configuration file initially defines this variable as a
      sub-folder of :term:`DEPLOY_DIR`::

         DEPLOY_DIR_RPM = "${DEPLOY_DIR}/rpm"

      The :ref:`ref-classes-package_rpm` class uses the
      :term:`DEPLOY_DIR_RPM` variable to make sure the
      :ref:`ref-tasks-package_write_rpm` task
      writes RPM packages into the appropriate folder. For more information
      on how packaging works, see the
      ":ref:`overview-manual/concepts:package feeds`" section
      in the Yocto Project Overview and Concepts Manual.

   :term:`DEPLOYDIR`
      When inheriting the :ref:`ref-classes-deploy` class, the
      :term:`DEPLOYDIR` points to a temporary work area for deployed files that
      is set in the :ref:`ref-classes-deploy` class as follows::

         DEPLOYDIR = "${WORKDIR}/deploy-${PN}"

      Recipes inheriting the :ref:`ref-classes-deploy` class should copy files to be
      deployed into :term:`DEPLOYDIR`, and the class will take care of copying
      them into :term:`DEPLOY_DIR_IMAGE`
      afterwards.

   :term:`DESCRIPTION`
      The package description used by package managers. If not set,
      :term:`DESCRIPTION` takes the value of the :term:`SUMMARY`
      variable.

   :term:`DEV_PKG_DEPENDENCY`
      Provides an easy way for recipes to disable or adjust the runtime recommendation
      (:term:`RRECOMMENDS`) of the ``${PN}-dev`` package on the main
      (``${PN}``) package.

   :term:`DISABLE_STATIC`
      Used in order to disable static linking by default (in order to save
      space, since static libraries are often unused in embedded systems.)
      The default value is " --disable-static", however it can be set to ""
      in order to enable static linking if desired. Certain recipes do this
      individually, and also there is a
      ``meta/conf/distro/include/no-static-libs.inc`` include file that
      disables static linking for a number of recipes. Some software
      packages or build tools (such as CMake) have explicit support for
      enabling / disabling static linking, and in those cases
      :term:`DISABLE_STATIC` is not used.

   :term:`DISTRO`
      The short name of the distribution. For information on the long name
      of the distribution, see the :term:`DISTRO_NAME`
      variable.

      The :term:`DISTRO` variable corresponds to a distribution configuration
      file whose root name is the same as the variable's argument and whose
      filename extension is ``.conf``. For example, the distribution
      configuration file for the Poky distribution is named ``poky.conf``
      and resides in the ``meta-poky/conf/distro`` directory of the
      :term:`Source Directory`.

      Within that ``poky.conf`` file, the :term:`DISTRO` variable is set as
      follows::

         DISTRO = "poky"

      Distribution configuration files are located in a ``conf/distro``
      directory within the :term:`Metadata` that contains the
      distribution configuration. The value for :term:`DISTRO` must not contain
      spaces, and is typically all lower-case.

      .. note::

         If the :term:`DISTRO` variable is blank, a set of default configurations
         are used, which are specified within
         ``meta/conf/distro/defaultsetup.conf`` also in the Source Directory.

   :term:`DISTRO_CODENAME`
      Specifies a codename for the distribution being built.

   :term:`DISTRO_EXTRA_RDEPENDS`
      Specifies a list of distro-specific packages to add to all images.
      This variable takes effect through ``packagegroup-base`` so the
      variable only really applies to the more full-featured images that
      include ``packagegroup-base``. You can use this variable to keep
      distro policy out of generic images. As with all other distro
      variables, you set this variable in the distro ``.conf`` file.

   :term:`DISTRO_EXTRA_RRECOMMENDS`
      Specifies a list of distro-specific packages to add to all images if
      the packages exist. The packages might not exist or be empty (e.g.
      kernel modules). The list of packages are automatically installed but
      you can remove them.

   :term:`DISTRO_FEATURES`
      The software support you want in your distribution for various
      features. You define your distribution features in the distribution
      configuration file.

      In most cases, the presence or absence of a feature in
      :term:`DISTRO_FEATURES` is translated to the appropriate option supplied
      to the configure script during the
      :ref:`ref-tasks-configure` task for recipes that
      optionally support the feature. For example, specifying "x11" in
      :term:`DISTRO_FEATURES`, causes every piece of software built for the
      target that can optionally support X11 to have its X11 support
      enabled.

      .. note::

         Just enabling :term:`DISTRO_FEATURES` alone doesn't
         enable feature support for packages. Mechanisms such as making
         :term:`PACKAGECONFIG` track :term:`DISTRO_FEATURES` are used
         to enable/disable package features.

      Two more examples are Bluetooth and NFS support. For a more complete
      list of features that ships with the Yocto Project and that you can
      provide with this variable, see the ":ref:`ref-features-distro`" section.

   :term:`DISTRO_FEATURES_BACKFILL`
      A space-separated list of features to be added to :term:`DISTRO_FEATURES`
      if not also present in :term:`DISTRO_FEATURES_BACKFILL_CONSIDERED`.

      This variable is set in the ``meta/conf/bitbake.conf`` file. It is
      not intended to be user-configurable. It is best to just reference
      the variable to see which distro features are being
      :ref:`backfilled <ref-features-backfill>` for all distro configurations.

   :term:`DISTRO_FEATURES_BACKFILL_CONSIDERED`
      A space-separated list of features from :term:`DISTRO_FEATURES_BACKFILL`
      that should not be :ref:`backfilled <ref-features-backfill>` (i.e. added
      to :term:`DISTRO_FEATURES`) during the build.

      This corresponds to an opt-out mechanism. When new default distro
      features are introduced, distribution maintainers can review (`consider`)
      them and decide to exclude them from the
      :ref:`backfilled <ref-features-backfill>` features. Therefore, the
      combination of :term:`DISTRO_FEATURES_BACKFILL` and
      :term:`DISTRO_FEATURES_BACKFILL_CONSIDERED` makes it possible to
      add new default features without breaking existing distributions.


   :term:`DISTRO_FEATURES_DEFAULT`
      A convenience variable that gives you the default list of distro
      features with the exception of any features specific to the C library
      (``libc``).

      When creating a custom distribution, you might find it useful to be
      able to reuse the default
      :term:`DISTRO_FEATURES` options without the
      need to write out the full set. Here is an example that uses
      :term:`DISTRO_FEATURES_DEFAULT` from a custom distro configuration file::

         DISTRO_FEATURES ?= "${DISTRO_FEATURES_DEFAULT} myfeature"

   :term:`DISTRO_FEATURES_FILTER_NATIVE`
      Specifies a list of features that if present in the target
      :term:`DISTRO_FEATURES` value should be
      included in :term:`DISTRO_FEATURES` when building native recipes. This
      variable is used in addition to the features filtered using the
      :term:`DISTRO_FEATURES_NATIVE`
      variable.

   :term:`DISTRO_FEATURES_FILTER_NATIVESDK`
      Specifies a list of features that if present in the target
      :term:`DISTRO_FEATURES` value should be included in
      :term:`DISTRO_FEATURES` when building :ref:`ref-classes-nativesdk`
      recipes. This variable is used in addition to the features filtered using
      the :term:`DISTRO_FEATURES_NATIVESDK` variable.

   :term:`DISTRO_FEATURES_NATIVE`
      Specifies a list of features that should be included in
      :term:`DISTRO_FEATURES` when building native
      recipes. This variable is used in addition to the features filtered
      using the
      :term:`DISTRO_FEATURES_FILTER_NATIVE`
      variable.

   :term:`DISTRO_FEATURES_NATIVESDK`
      Specifies a list of features that should be included in
      :term:`DISTRO_FEATURES` when building
      :ref:`ref-classes-nativesdk` recipes. This variable is used
      in addition to the features filtered using the
      :term:`DISTRO_FEATURES_FILTER_NATIVESDK` variable.

   :term:`DISTRO_NAME`
      The long name of the distribution. For information on the short name
      of the distribution, see the :term:`DISTRO` variable.

      The :term:`DISTRO_NAME` variable corresponds to a distribution
      configuration file whose root name is the same as the variable's
      argument and whose filename extension is ``.conf``. For example, the
      distribution configuration file for the Poky distribution is named
      ``poky.conf`` and resides in the ``meta-poky/conf/distro`` directory
      of the :term:`Source Directory`.

      Within that ``poky.conf`` file, the :term:`DISTRO_NAME` variable is set
      as follows::

         DISTRO_NAME = "Poky (Yocto Project Reference Distro)"

      Distribution configuration files are located in a ``conf/distro``
      directory within the :term:`Metadata` that contains the
      distribution configuration.

      .. note::

         If the :term:`DISTRO_NAME` variable is blank, a set of default
         configurations are used, which are specified within
         ``meta/conf/distro/defaultsetup.conf`` also in the Source Directory.

   :term:`DISTRO_VERSION`
      The version of the distribution.

   :term:`DISTROOVERRIDES`
      A colon-separated list of overrides specific to the current
      distribution. By default, this list includes the value of
      :term:`DISTRO`.

      You can extend :term:`DISTROOVERRIDES` to add extra overrides that should
      apply to the distribution.

      The underlying mechanism behind :term:`DISTROOVERRIDES` is simply that it
      is included in the default value of
      :term:`OVERRIDES`.

      Here is an example from :yocto_git:`meta-poky/conf/distro/poky-tiny.conf
      </poky/tree/meta-poky/conf/distro/poky-tiny.conf>`::

         DISTROOVERRIDES = "poky:poky-tiny"

   :term:`DL_DIR`
      The central download directory used by the build process to store
      downloads. By default, :term:`DL_DIR` gets files suitable for mirroring
      for everything except Git repositories. If you want tarballs of Git
      repositories, use the
      :term:`BB_GENERATE_MIRROR_TARBALLS`
      variable.

      You can set this directory by defining the :term:`DL_DIR` variable in the
      ``conf/local.conf`` file. This directory is self-maintaining and you
      should not have to touch it. By default, the directory is
      ``downloads`` in the :term:`Build Directory`::

         #DL_DIR ?= "${TOPDIR}/downloads"

      To specify a different download directory,
      simply remove the comment from the line and provide your directory.

      During a first build, the system downloads many different source code
      tarballs from various upstream projects. Downloading can take a
      while, particularly if your network connection is slow. Tarballs are
      all stored in the directory defined by :term:`DL_DIR` and the build
      system looks there first to find source tarballs.

      .. note::

         When wiping and rebuilding, you can preserve this directory to
         speed up this part of subsequent builds.

      You can safely share this directory between multiple builds on the
      same development machine. For additional information on how the build
      process gets source files when working behind a firewall or proxy
      server, see this specific question in the ":doc:`faq`"
      chapter. You can also refer to the
      ":yocto_wiki:`Working Behind a Network Proxy </Working_Behind_a_Network_Proxy>`"
      Wiki page.

   :term:`DOC_COMPRESS`
      When inheriting the :ref:`ref-classes-compress_doc`
      class, this variable sets the compression policy used when the
      OpenEmbedded build system compresses manual and info pages. By
      default, the compression method used is gz (gzip). Other policies
      available are xz and bz2.

      For information on policies and on how to use this variable, see the
      comments in the ``meta/classes-recipe/compress_doc.bbclass`` file.

   :term:`DT_FILES`
      Space-separated list of device tree source files to compile using
      a recipe that inherits the :ref:`ref-classes-devicetree` class. These
      are relative to the :term:`DT_FILES_PATH`.

      For convenience, both ``.dts`` and ``.dtb`` extensions can be used.

      Use an empty string (default) to build all device tree sources within
      the :term:`DT_FILES_PATH` directory.

   :term:`DT_FILES_PATH`
      When compiling out-of-tree device tree sources using a recipe that
      inherits the :ref:`ref-classes-devicetree` class, this variable specifies
      the path to the directory containing dts files to build.

      Defaults to the :term:`S` directory.

   :term:`DT_PADDING_SIZE`
      When inheriting the :ref:`ref-classes-devicetree` class, this variable
      specifies the size of padding appended to the device tree blob, used as
      extra space typically for additional properties during boot.

   :term:`EFI_PROVIDER`
      When building bootable images (i.e. where ``hddimg``, ``iso``, or
      ``wic.vmdk`` is in :term:`IMAGE_FSTYPES`), the
      :term:`EFI_PROVIDER` variable specifies the EFI bootloader to use. The
      default is "grub-efi", but "systemd-boot" can be used instead.

      See the :ref:`ref-classes-systemd-boot` and :ref:`ref-classes-image-live`
      classes for more information.

   :term:`EFI_UKI_DIR`
      The primary place for the UKI image inside the EFI System Partition.

   :term:`EFI_UKI_PATH`
      The path for the UKI image inside the root filesystem.

   :term:`ENABLE_BINARY_LOCALE_GENERATION`
      Variable that controls which locales for ``glibc`` are generated
      during the build (useful if the target device has 64Mbytes of RAM or
      less).

   :term:`ERR_REPORT_DIR`
      When used with the :ref:`ref-classes-report-error` class, specifies the
      path used for storing the debug files created by the :ref:`error reporting
      tool <dev-manual/error-reporting-tool:using the error reporting tool>`,
      which allows you to submit build errors you encounter to a central
      database. By default, the value of this variable is
      ``${``\ :term:`LOG_DIR`\ ``}/error-report``.

      You can set :term:`ERR_REPORT_DIR` to the path you want the error
      reporting tool to store the debug files as follows in your
      ``local.conf`` file::

         ERR_REPORT_DIR = "path"

   :term:`ERROR_QA`
      Specifies the quality assurance checks whose failures are reported as
      errors by the OpenEmbedded build system. You set this variable in
      your distribution configuration file. For a list of the checks you
      can control with this variable, see the
      ":ref:`ref-classes-insane`" section.

   :term:`ESDK_CLASS_INHERIT_DISABLE`
      A list of classes to remove from the :term:`INHERIT`
      value globally within the extensible SDK configuration. The
      :ref:`populate-sdk-ext <ref-classes-populate-sdk-*>` class sets the
      default value::

         ESDK_CLASS_INHERIT_DISABLE ?= "buildhistory icecc"

      Some classes are not generally applicable within the extensible SDK
      context. You can use this variable to disable those classes.

      For additional information on how to customize the extensible SDK's
      configuration, see the
      ":ref:`sdk-manual/appendix-customizing:configuring the extensible sdk`"
      section in the Yocto Project Application Development and the
      Extensible Software Development Kit (eSDK) manual.

   :term:`ESDK_LOCALCONF_ALLOW`
      A list of variables allowed through from the OpenEmbedded build
      system configuration into the extensible SDK configuration. By
      default, the list of variables is empty and is set in the
      :ref:`populate-sdk-ext <ref-classes-populate-sdk-*>` class.

      This list overrides the variables specified using the
      :term:`ESDK_LOCALCONF_REMOVE` variable as well as
      other variables automatically added due to the "/" character
      being found at the start of the
      value, which is usually indicative of being a path and thus might not
      be valid on the system where the SDK is installed.

      For additional information on how to customize the extensible SDK's
      configuration, see the
      ":ref:`sdk-manual/appendix-customizing:configuring the extensible sdk`"
      section in the Yocto Project Application Development and the
      Extensible Software Development Kit (eSDK) manual.

   :term:`ESDK_LOCALCONF_REMOVE`
      A list of variables not allowed through from the OpenEmbedded build
      system configuration into the extensible SDK configuration. Usually,
      these are variables that are specific to the machine on which the
      build system is running and thus would be potentially problematic
      within the extensible SDK.

      By default, :term:`ESDK_LOCALCONF_REMOVE` is set in the
      :ref:`populate-sdk-ext <ref-classes-populate-sdk-*>` class and
      excludes the following variables:

      - :term:`CONF_VERSION`
      - :term:`BB_NUMBER_THREADS`
      - :term:`BB_NUMBER_PARSE_THREADS`
      - :term:`PARALLEL_MAKE`
      - :term:`PRSERV_HOST`
      - :term:`SSTATE_MIRRORS` :term:`DL_DIR`
      - :term:`SSTATE_DIR` :term:`TMPDIR`
      - :term:`BB_SERVER_TIMEOUT`

      For additional information on how to customize the extensible SDK's
      configuration, see the
      ":ref:`sdk-manual/appendix-customizing:configuring the extensible sdk`"
      section in the Yocto Project Application Development and the
      Extensible Software Development Kit (eSDK) manual.

   :term:`EXCLUDE_FROM_SHLIBS`
      Triggers the OpenEmbedded build system's shared libraries resolver to
      exclude an entire package when scanning for shared libraries.

      .. note::

         The shared libraries resolver's functionality results in part from
         the internal function ``package_do_shlibs``, which is part of the
         :ref:`ref-tasks-package` task. You should be aware that the shared
         libraries resolver might implicitly define some dependencies between
         packages.

      The :term:`EXCLUDE_FROM_SHLIBS` variable is similar to the
      :term:`PRIVATE_LIBS` variable, which excludes a
      package's particular libraries only and not the whole package.

      Use the :term:`EXCLUDE_FROM_SHLIBS` variable by setting it to "1" for a
      particular package::

         EXCLUDE_FROM_SHLIBS = "1"

   :term:`EXCLUDE_FROM_WORLD`
      Directs BitBake to exclude a recipe from world builds (i.e.
      ``bitbake world``). During world builds, BitBake locates, parses and
      builds all recipes found in every layer exposed in the
      ``bblayers.conf`` configuration file.

      To exclude a recipe from a world build using this variable, set the
      variable to "1" in the recipe.

      .. note::

         Recipes added to :term:`EXCLUDE_FROM_WORLD` may still be built during a
         world build in order to satisfy dependencies of other recipes. Adding
         a recipe to :term:`EXCLUDE_FROM_WORLD` only ensures that the recipe is not
         explicitly added to the list of build targets in a world build.

   :term:`EXTENDPE`
      Used with file and pathnames to create a prefix for a recipe's
      version based on the recipe's :term:`PE` value. If :term:`PE`
      is set and greater than zero for a recipe, :term:`EXTENDPE` becomes that
      value (e.g if :term:`PE` is equal to "1" then :term:`EXTENDPE` becomes "1").
      If a recipe's :term:`PE` is not set (the default) or is equal to zero,
      :term:`EXTENDPE` becomes "".

      See the :term:`STAMP` variable for an example.

   :term:`EXTENDPKGV`
      The full package version specification as it appears on the final
      packages produced by a recipe. The variable's value is normally used
      to fix a runtime dependency to the exact same version of another
      package in the same recipe::

         RDEPENDS:${PN}-additional-module = "${PN} (= ${EXTENDPKGV})"

      The dependency relationships are intended to force the package
      manager to upgrade these types of packages in lock-step.

   :term:`EXTERNAL_KERNEL_TOOLS`
      When set, the :term:`EXTERNAL_KERNEL_TOOLS` variable indicates that these
      tools are not in the source tree.

      When kernel tools are available in the tree, they are preferred over
      any externally installed tools. Setting the :term:`EXTERNAL_KERNEL_TOOLS`
      variable tells the OpenEmbedded build system to prefer the installed
      external tools. See the :ref:`ref-classes-kernel-yocto` class in
      ``meta/classes-recipe`` to see how the variable is used.

   :term:`KERNEL_LOCALVERSION`
      This variable allows to append a string to the version
      of the kernel image. This corresponds to the ``CONFIG_LOCALVERSION``
      kernel configuration parameter.

      Using this variable is only useful when you are using a kernel recipe
      inheriting the :ref:`ref-classes-kernel` class, and which doesn't
      already set a local version. Therefore, setting this variable has no
      impact on ``linux-yocto`` kernels.

   :term:`EXTERNAL_TOOLCHAIN`
      When you intend to use an
      :ref:`external toolchain <dev-manual/external-toolchain:optionally using an external toolchain>`,
      this variable allows to specify the directory where this toolchain was
      installed.

   :term:`EXTERNALSRC`
      When inheriting the :ref:`ref-classes-externalsrc`
      class, this variable points to the source tree, which is outside of
      the OpenEmbedded build system. When set, this variable sets the
      :term:`S` variable, which is what the OpenEmbedded build
      system uses to locate unpacked recipe source code.

      See the ":ref:`ref-classes-externalsrc`" section for details. You
      can also find information on how to use this variable in the
      ":ref:`dev-manual/building:building software from an external source`"
      section in the Yocto Project Development Tasks Manual.

   :term:`EXTERNALSRC_BUILD`
      When inheriting the :ref:`ref-classes-externalsrc`
      class, this variable points to the directory in which the recipe's
      source code is built, which is outside of the OpenEmbedded build
      system. When set, this variable sets the :term:`B` variable,
      which is what the OpenEmbedded build system uses to locate the
      :term:`Build Directory`.

      See the ":ref:`ref-classes-externalsrc`" section for details. You
      can also find information on how to use this variable in the
      ":ref:`dev-manual/building:building software from an external source`"
      section in the Yocto Project Development Tasks Manual.

   :term:`EXTRA_AUTORECONF`
      For recipes inheriting the :ref:`ref-classes-autotools`
      class, you can use :term:`EXTRA_AUTORECONF` to specify extra options to
      pass to the ``autoreconf`` command that is executed during the
      :ref:`ref-tasks-configure` task.

      The default value is "--exclude=autopoint".

   :term:`EXTRA_IMAGE_FEATURES`
      A list of additional features to include in an image. When listing
      more than one feature, separate them with a space.

      Typically, you configure this variable in your ``local.conf`` file,
      which is found in the :term:`Build Directory`. Although you can use this
      variable from within a recipe, best practices dictate that you do not.

      .. note::

         To enable primary features from within the image recipe, use the
         :term:`IMAGE_FEATURES` variable.

      Here are some examples of features you can add:

        - "dbg-pkgs" --- adds -dbg packages for all installed packages including
          symbol information for debugging and profiling.

        - "debug-tweaks" --- makes an image suitable for debugging. For example, allows root logins without passwords and
          enables post-installation logging. See the 'allow-empty-password' and
          'post-install-logging' features in the ":ref:`ref-features-image`"
          section for more information.
        - "dev-pkgs" --- adds -dev packages for all installed packages. This is
          useful if you want to develop against the libraries in the image.
        - "read-only-rootfs" --- creates an image whose root filesystem is
          read-only. See the
          ":ref:`dev-manual/read-only-rootfs:creating a read-only root filesystem`"
          section in the Yocto Project Development Tasks Manual for more
          information
        - "tools-debug" --- adds debugging tools such as gdb and strace.
        - "tools-sdk" --- adds development tools such as gcc, make,
          pkgconfig and so forth.
        - "tools-testapps" --- adds useful testing tools
          such as ts_print, aplay, arecord and so forth.

      For a complete list of image features that ships with the Yocto
      Project, see the ":ref:`ref-features-image`" section.

      For an example that shows how to customize your image by using this
      variable, see the ":ref:`dev-manual/customizing-images:customizing images using custom \`\`image_features\`\` and \`\`extra_image_features\`\``"
      section in the Yocto Project Development Tasks Manual.

   :term:`EXTRA_IMAGECMD`
      Specifies additional options for the image creation command that has
      been specified in :term:`IMAGE_CMD`. When setting
      this variable, use an override for the associated image type. Here is
      an example::

         EXTRA_IMAGECMD:ext3 ?= "-i 4096"

   :term:`EXTRA_IMAGEDEPENDS`
      A list of recipes to build that do not provide packages for
      installing into the root filesystem.

      Sometimes a recipe is required to build the final image but is not
      needed in the root filesystem. You can use the :term:`EXTRA_IMAGEDEPENDS`
      variable to list these recipes and thus specify the dependencies. A
      typical example is a required bootloader in a machine configuration.

      .. note::

         To add packages to the root filesystem, see the various
         :term:`RDEPENDS` and :term:`RRECOMMENDS` variables.

   :term:`EXTRA_OECMAKE`
      Additional `CMake <https://cmake.org/overview/>`__ options. See the
      :ref:`ref-classes-cmake` class for additional information.

   :term:`EXTRA_OECONF`
      Additional ``configure`` script options. See
      :term:`PACKAGECONFIG_CONFARGS` for
      additional information on passing configure script options.

   :term:`EXTRA_OEMAKE`
      Additional GNU ``make`` options.

      Because the :term:`EXTRA_OEMAKE` defaults to "", you need to set the
      variable to specify any required GNU options.

      :term:`PARALLEL_MAKE` and
      :term:`PARALLEL_MAKEINST` also make use of
      :term:`EXTRA_OEMAKE` to pass the required flags.

   :term:`EXTRA_OESCONS`
      When inheriting the :ref:`ref-classes-scons` class, this
      variable specifies additional configuration options you want to pass
      to the ``scons`` command line.

   :term:`EXTRA_OEMESON`
      Additional `Meson <https://mesonbuild.com/>`__ options. See the
      :ref:`ref-classes-meson` class for additional information.

      In addition to standard Meson options, such options correspond to
      `Meson build options <https://mesonbuild.com/Build-options.html>`__
      defined in the ``meson_options.txt`` file in the sources to build.
      Here is an example::

         EXTRA_OEMESON = "-Dpython=disabled -Dvalgrind=disabled"

      Note that any custom value for the Meson ``--buildtype`` option
      should be set through the :term:`MESON_BUILDTYPE` variable.

   :term:`EXTRA_USERS_PARAMS`
      When inheriting the :ref:`ref-classes-extrausers`
      class, this variable provides image level user and group operations.
      This is a more global method of providing user and group
      configuration as compared to using the
      :ref:`ref-classes-useradd` class, which ties user and
      group configurations to a specific recipe.

      The set list of commands you can configure using the
      :term:`EXTRA_USERS_PARAMS` is shown in the
      :ref:`ref-classes-extrausers` class. These commands map to the normal
      Unix commands of the same names::

         # EXTRA_USERS_PARAMS = "\
         # useradd -p '' tester; \
         # groupadd developers; \
         # userdel nobody; \
         # groupdel -g video; \
         # groupmod -g 1020 developers; \
         # usermod -s /bin/sh tester; \
         # "

      Hardcoded passwords are supported via the ``-p`` parameters for
      ``useradd`` or ``usermod``, but only hashed.

      Here is an example that adds two users named "tester-jim" and "tester-sue" and assigns
      passwords. First on host, create the (escaped) password hash::

         printf "%q" $(mkpasswd -m sha256crypt tester01)

      The resulting hash is set to a variable and used in ``useradd`` command parameters::

         inherit extrausers
         PASSWD = "\$X\$ABC123\$A-Long-Hash"
         EXTRA_USERS_PARAMS = "\
             useradd -p '${PASSWD}' tester-jim; \
             useradd -p '${PASSWD}' tester-sue; \
             "

      Finally, here is an example that sets the root password::

         inherit extrausers
         EXTRA_USERS_PARAMS = "\
             usermod -p '${PASSWD}' root; \
             "

      .. note::

         From a security perspective, hardcoding a default password is not
         generally a good idea or even legal in some jurisdictions. It is
         recommended that you do not do this if you are building a production
         image.

      Additionally there is a special ``passwd-expire`` command that will
      cause the password for a user to be expired and thus force changing it
      on first login, for example::

         EXTRA_USERS_PARAMS += " useradd myuser; passwd-expire myuser;"

      .. note::

         At present, ``passwd-expire`` may only work for remote logins when
         using OpenSSH and not dropbear as an SSH server.

   :term:`EXTRANATIVEPATH`
      A list of subdirectories of
      ``${``\ :term:`STAGING_BINDIR_NATIVE`\ ``}``
      added to the beginning of the environment variable ``PATH``. As an
      example, the following prepends
      "${STAGING_BINDIR_NATIVE}/foo:${STAGING_BINDIR_NATIVE}/bar:" to
      ``PATH``::

         EXTRANATIVEPATH = "foo bar"

   :term:`FAKEROOT`
      See :term:`bitbake:FAKEROOT` in the BitBake manual.

   :term:`FAKEROOTBASEENV`
      See :term:`bitbake:FAKEROOTBASEENV` in the BitBake manual.

   :term:`FAKEROOTCMD`
      See :term:`bitbake:FAKEROOTCMD` in the BitBake manual.

   :term:`FAKEROOTDIRS`
      See :term:`bitbake:FAKEROOTDIRS` in the BitBake manual.

   :term:`FAKEROOTENV`
      See :term:`bitbake:FAKEROOTENV` in the BitBake manual.

   :term:`FAKEROOTNOENV`
      See :term:`bitbake:FAKEROOTNOENV` in the BitBake manual.

   :term:`FEATURE_PACKAGES`
      Defines one or more packages to include in an image when a specific
      item is included in :term:`IMAGE_FEATURES`.
      When setting the value, :term:`FEATURE_PACKAGES` should have the name of
      the feature item as an override. Here is an example::

         FEATURE_PACKAGES_widget = "package1 package2"

      In this example, if "widget" were added to :term:`IMAGE_FEATURES`,
      package1 and package2 would be included in the image.

      .. note::

         Packages installed by features defined through :term:`FEATURE_PACKAGES`
         are often package groups. While similarly named, you should not
         confuse the :term:`FEATURE_PACKAGES` variable with package groups, which
         are discussed elsewhere in the documentation.

   :term:`FEED_DEPLOYDIR_BASE_URI`
      Points to the base URL of the server and location within the
      document-root that provides the metadata and packages required by
      OPKG to support runtime package management of IPK packages. You set
      this variable in your ``local.conf`` file.

      Consider the following example::

         FEED_DEPLOYDIR_BASE_URI = "http://192.168.7.1/BOARD-dir"

      This example assumes you are serving
      your packages over HTTP and your databases are located in a directory
      named ``BOARD-dir``, which is underneath your HTTP server's
      document-root. In this case, the OpenEmbedded build system generates
      a set of configuration files for you in your target that work with
      the feed.

   :term:`FETCHCMD`
      See :term:`bitbake:FETCHCMD` in the BitBake manual.

   :term:`FILE`
      See :term:`bitbake:FILE` in the BitBake manual.

   :term:`FILES`
      The list of files and directories that are placed in a package. The
      :term:`PACKAGES` variable lists the packages
      generated by a recipe.

      To use the :term:`FILES` variable, provide a package name override that
      identifies the resulting package. Then, provide a space-separated
      list of files or paths that identify the files you want included as
      part of the resulting package. Here is an example::

         FILES:${PN} += "${bindir}/mydir1 ${bindir}/mydir2/myfile"

      .. note::

         -  When specifying files or paths, you can pattern match using
            Python's
            `glob <https://docs.python.org/3/library/glob.html>`__
            syntax. For details on the syntax, see the documentation by
            following the previous link.

         -  When specifying paths as part of the :term:`FILES` variable, it is
            good practice to use appropriate path variables. For example,
            use ``${sysconfdir}`` rather than ``/etc``, or ``${bindir}``
            rather than ``/usr/bin``. You can find a list of these
            variables at the top of the ``meta/conf/bitbake.conf`` file in
            the :term:`Source Directory`. You will also
            find the default values of the various ``FILES:*`` variables in
            this file.

      If some of the files you provide with the :term:`FILES` variable are
      editable and you know they should not be overwritten during the
      package update process by the Package Management System (PMS), you
      can identify these files so that the PMS will not overwrite them. See
      the :term:`CONFFILES` variable for information on
      how to identify these files to the PMS.

   :term:`FILES_SOLIBSDEV`
      Defines the file specification to match
      :term:`SOLIBSDEV`. In other words,
      :term:`FILES_SOLIBSDEV` defines the full path name of the development
      symbolic link (symlink) for shared libraries on the target platform.

      The following statement from the ``bitbake.conf`` shows how it is
      set::

         FILES_SOLIBSDEV ?= "${base_libdir}/lib*${SOLIBSDEV} ${libdir}/lib*${SOLIBSDEV}"

   :term:`FILESEXTRAPATHS`
      A colon-separated list to extend the search path the OpenEmbedded build
      system uses when looking for files and patches as it processes recipes
      and append files. The default directories BitBake uses when it processes
      recipes are initially defined by the :term:`FILESPATH` variable. You can
      extend :term:`FILESPATH` variable by using :term:`FILESEXTRAPATHS`.

      Best practices dictate that you accomplish this by using
      :term:`FILESEXTRAPATHS` from within a ``.bbappend`` file and that you
      prepend paths as follows::

         FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

      In the above example, the build system first
      looks for files in a directory that has the same name as the
      corresponding append file.

      .. note::

         When extending :term:`FILESEXTRAPATHS`, be sure to use the immediate
         expansion (``:=``) operator. Immediate expansion makes sure that
         BitBake evaluates :term:`THISDIR` at the time the
         directive is encountered rather than at some later time when
         expansion might result in a directory that does not contain the
         files you need.

         Also, include the trailing separating colon character if you are
         prepending. The trailing colon character is necessary because you
         are directing BitBake to extend the path by prepending directories
         to the search path.

      Here is another common use::

         FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

      In this example, the build system extends the
      :term:`FILESPATH` variable to include a directory named ``files`` that is
      in the same directory as the corresponding append file.

      This next example specifically adds three paths::

         FILESEXTRAPATHS:prepend := "path_1:path_2:path_3:"

      A final example shows how you can extend the search path and include
      a :term:`MACHINE`-specific override, which is useful
      in a BSP layer::

          FILESEXTRAPATHS:prepend:intel-x86-common := "${THISDIR}/${PN}:"

      The previous statement appears in the
      ``linux-yocto-dev.bbappend`` file, which is found in the
      :ref:`overview-manual/development-environment:yocto project source repositories` in
      ``meta-intel/common/recipes-kernel/linux``. Here, the machine
      override is a special :term:`PACKAGE_ARCH`
      definition for multiple ``meta-intel`` machines.

      .. note::

         For a layer that supports a single BSP, the override could just be
         the value of :term:`MACHINE`.

      By prepending paths in ``.bbappend`` files, you allow multiple append
      files that reside in different layers but are used for the same
      recipe to correctly extend the path.

   :term:`FILESOVERRIDES`
      A colon-separated list to specify a subset of :term:`OVERRIDES` used by
      the OpenEmbedded build system for creating :term:`FILESPATH`. The
      :term:`FILESOVERRIDES` variable uses overrides to automatically extend
      the :term:`FILESPATH` variable. For an example of how that works, see the
      :term:`FILESPATH` variable description. Additionally, you find more
      information on how overrides are handled in the
      ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:conditional syntax (overrides)`"
      section of the BitBake User Manual.

      By default, the :term:`FILESOVERRIDES` variable is defined as::

         FILESOVERRIDES = "${TRANSLATED_TARGET_ARCH}:${MACHINEOVERRIDES}:${DISTROOVERRIDES}"

      .. note::

         Do not hand-edit the :term:`FILESOVERRIDES` variable. The values match up
         with expected overrides and are used in an expected manner by the
         build system.

   :term:`FILESPATH`
      A colon-separated list specifying the default set of directories the
      OpenEmbedded build system uses when searching for patches and files.

      During the build process, BitBake searches each directory in
      :term:`FILESPATH` in the specified order when looking for files and
      patches specified by each ``file://`` URI in a recipe's
      :term:`SRC_URI` statements.

      The default value for the :term:`FILESPATH` variable is defined in the
      :ref:`ref-classes-base` class found in ``meta/classes-global`` in the
      :term:`Source Directory`::

         FILESPATH = "${@base_set_filespath(["${FILE_DIRNAME}/${BP}", \
             "${FILE_DIRNAME}/${BPN}", "${FILE_DIRNAME}/files"], d)}"

      The
      :term:`FILESPATH` variable is automatically extended using the overrides
      from the :term:`FILESOVERRIDES` variable.

      .. note::

         -  Do not hand-edit the :term:`FILESPATH` variable. If you want the
            build system to look in directories other than the defaults,
            extend the :term:`FILESPATH` variable by using the
            :term:`FILESEXTRAPATHS` variable.

         -  Be aware that the default :term:`FILESPATH` directories do not map
            to directories in custom layers where append files
            (``.bbappend``) are used. If you want the build system to find
            patches or files that reside with your append files, you need
            to extend the :term:`FILESPATH` variable by using the
            :term:`FILESEXTRAPATHS` variable.

      You can take advantage of this searching behavior in useful ways. For
      example, consider a case where there is the following directory structure
      for general and machine-specific configurations::

         files/defconfig
         files/MACHINEA/defconfig
         files/MACHINEB/defconfig

      Also in the example, the :term:`SRC_URI` statement contains
      "file://defconfig". Given this scenario, you can set
      :term:`MACHINE` to "MACHINEA" and cause the build
      system to use files from ``files/MACHINEA``. Set :term:`MACHINE` to
      "MACHINEB" and the build system uses files from ``files/MACHINEB``.
      Finally, for any machine other than "MACHINEA" and "MACHINEB", the
      build system uses files from ``files/defconfig``.

      You can find out more about the patching process in the
      ":ref:`overview-manual/concepts:patching`" section
      in the Yocto Project Overview and Concepts Manual and the
      ":ref:`dev-manual/new-recipe:patching code`" section in
      the Yocto Project Development Tasks Manual. See the
      :ref:`ref-tasks-patch` task as well.

   :term:`FILESYSTEM_PERMS_TABLES`
      Allows you to define your own file permissions settings table as part
      of your configuration for the packaging process. For example, suppose
      you need a consistent set of custom permissions for a set of groups
      and users across an entire work project. It is best to do this in the
      packages themselves but this is not always possible.

      By default, the OpenEmbedded build system uses the ``fs-perms.txt``,
      which is located in the ``meta/files`` folder in the :term:`Source Directory`.
      If you create your own file
      permissions setting table, you should place it in your layer or the
      distro's layer.

      You define the :term:`FILESYSTEM_PERMS_TABLES` variable in the
      ``conf/local.conf`` file, which is found in the :term:`Build Directory`,
      to point to your custom ``fs-perms.txt``. You can specify more than a
      single file permissions setting table. The paths you specify to these
      files must be defined within the :term:`BBPATH` variable.

      For guidance on how to create your own file permissions settings
      table file, examine the existing ``fs-perms.txt``.

   :term:`FIT_ADDRESS_CELLS`
      Specifies the value of the ``#address-cells`` value for the
      description of the FIT image.

      The default value is set to "1" by the :ref:`ref-classes-kernel-fitimage`
      class, which corresponds to 32 bit addresses.

      For platforms that need to set 64 bit addresses, for example in
      :term:`UBOOT_LOADADDRESS` and :term:`UBOOT_ENTRYPOINT`, you need to
      set this value to "2", as two 32 bit values (cells) will be needed
      to represent such addresses.

      Here is an example setting "0x400000000" as a load address::

         FIT_ADDRESS_CELLS = "2"
         UBOOT_LOADADDRESS= "0x04 0x00000000"

      See `more details about #address-cells <https://elinux.org/Device_Tree_Usage#How_Addressing_Works>`__.

   :term:`FIT_CONF_DEFAULT_DTB`
      Specifies the default device tree binary (dtb) file for a FIT image
      when multiple ones are provided.

      This variable is used in the :ref:`ref-classes-kernel-fitimage` class.

   :term:`FIT_DESC`
      Specifies the description string encoded into a FIT image. The
      default value is set by the :ref:`ref-classes-kernel-fitimage` class as
      follows::

         FIT_DESC ?= "U-Boot fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}"

   :term:`FIT_GENERATE_KEYS`
      Decides whether to generate the keys for signing the FIT image if
      they don't already exist. The keys are created in
      :term:`UBOOT_SIGN_KEYDIR`. The default value is set to "0"
      by the :ref:`ref-classes-kernel-fitimage` class.

   :term:`FIT_HASH_ALG`
      Specifies the hash algorithm used in creating the FIT Image.
      This variable is set by default to "sha256" by the
      :ref:`ref-classes-kernel-fitimage` class.

   :term:`FIT_KERNEL_COMP_ALG`
      The compression algorithm to use for the kernel image inside the FIT Image.
      At present, the only supported values are "gzip" (default), "lzo" or "none".
      If you set this variable to anything other than "none" you may also need
      to set :term:`FIT_KERNEL_COMP_ALG_EXTENSION`.

      This variable is used in the :ref:`ref-classes-kernel-uboot` class.

   :term:`FIT_KERNEL_COMP_ALG_EXTENSION`
      File extension corresponding to :term:`FIT_KERNEL_COMP_ALG`. The default
      value is set ".gz" by the :ref:`ref-classes-kernel-uboot` class. If you
      set :term:`FIT_KERNEL_COMP_ALG` to "lzo", you may want to set this
      variable to ".lzo".

   :term:`FIT_KEY_GENRSA_ARGS`
      Arguments to ``openssl genrsa`` for generating a RSA private key for
      signing the FIT image. The default value is set to "-F4" by the
      :ref:`ref-classes-kernel-fitimage` class.

   :term:`FIT_KEY_REQ_ARGS`
      Arguments to ``openssl req`` for generating a certificate for signing
      the FIT image. The default value is "-batch -new" by the
      :ref:`ref-classes-kernel-fitimage` class, "batch" for
      non interactive mode and "new" for generating new keys.

   :term:`FIT_KEY_SIGN_PKCS`
      Format for the public key certificate used for signing the FIT image.
      The default value is set to "x509" by the
      :ref:`ref-classes-kernel-fitimage` class.

   :term:`FIT_SIGN_ALG`
      Specifies the signature algorithm used in creating the FIT Image.
      This variable is set by default to "rsa2048" by the
      :ref:`ref-classes-kernel-fitimage` class.

   :term:`FIT_PAD_ALG`
      Specifies the padding algorithm used in creating the FIT Image.
      The default value is set to "pkcs-1.5" by the
      :ref:`ref-classes-kernel-fitimage` class.

   :term:`FIT_SIGN_INDIVIDUAL`
      If set to "1", then the :ref:`ref-classes-kernel-fitimage`
      class will sign the kernel, dtb and ramdisk images individually in addition
      to signing the FIT image itself. This could be useful if you are
      intending to verify signatures in another context than booting via
      U-Boot.

      This variable is set to "0" by default.

   :term:`FIT_SIGN_NUMBITS`
      Size of the private key used in the FIT image, in number of bits.
      The default value for this variable is set to "2048"
      by the :ref:`ref-classes-kernel-fitimage` class.

   :term:`FONT_EXTRA_RDEPENDS`
      When inheriting the :ref:`ref-classes-fontcache` class,
      this variable specifies the runtime dependencies for font packages.
      By default, the :term:`FONT_EXTRA_RDEPENDS` is set to "fontconfig-utils".

   :term:`FONT_PACKAGES`
      When inheriting the :ref:`ref-classes-fontcache` class, this variable
      identifies packages containing font files that need to be cached by
      Fontconfig. By default, the :ref:`ref-classes-fontcache` class assumes
      that fonts are in the recipe's main package (i.e.
      ``${``\ :term:`PN`\ ``}``). Use this variable if fonts you
      need are in a package other than that main package.

   :term:`FORCE_RO_REMOVE`
      Forces the removal of the packages listed in ``ROOTFS_RO_UNNEEDED``
      during the generation of the root filesystem.

      Set the variable to "1" to force the removal of these packages.

   :term:`FULL_OPTIMIZATION`
      The options to pass in :term:`TARGET_CFLAGS` and :term:`CFLAGS` when
      compiling an optimized system. This variable defaults to "-O2 -pipe
      ${DEBUG_FLAGS}".

   :term:`GCCPIE`
      Enables Position Independent Executables (PIE) within the GNU C
      Compiler (GCC). Enabling PIE in the GCC makes Return Oriented
      Programming (ROP) attacks much more difficult to execute.

      By default the ``security_flags.inc`` file enables PIE by setting the
      variable as follows::

         GCCPIE ?= "--enable-default-pie"

   :term:`GCCVERSION`
      Specifies the default version of the GNU C Compiler (GCC) used for
      compilation. By default, :term:`GCCVERSION` is set to "8.x" in the
      ``meta/conf/distro/include/tcmode-default.inc`` include file::

         GCCVERSION ?= "8.%"

      You can override this value by setting it in a
      configuration file such as the ``local.conf``.

   :term:`GDB`
      The minimal command and arguments to run the GNU Debugger.

   :term:`GIR_EXTRA_LIBS_PATH`
      Allows to specify an extra search path for ``.so`` files
      in GLib related recipes using GObject introspection,
      and which do not compile without this setting.
      See the ":ref:`dev-manual/gobject-introspection:enabling gobject introspection support`"
      section for details.

   :term:`GITDIR`
      The directory in which a local copy of a Git repository is stored
      when it is cloned.

   :term:`GITHUB_BASE_URI`
      When inheriting the :ref:`ref-classes-github-releases`
      class, specifies the base URL for fetching releases for the github
      project you wish to fetch sources from. The default value is as follows::

         GITHUB_BASE_URI ?= "https://github.com/${BPN}/${BPN}/releases/"

   :term:`GLIBC_GENERATE_LOCALES`
      Specifies the list of GLIBC locales to generate should you not wish
      to generate all LIBC locals, which can be time consuming.

      .. note::

         If you specifically remove the locale ``en_US.UTF-8``, you must set
         :term:`IMAGE_LINGUAS` appropriately.

      You can set :term:`GLIBC_GENERATE_LOCALES` in your ``local.conf`` file.
      By default, all locales are generated::

         GLIBC_GENERATE_LOCALES = "en_GB.UTF-8 en_US.UTF-8"

   :term:`GO_IMPORT`
      When inheriting the :ref:`ref-classes-go` class, this mandatory variable
      sets the import path for the Go package that will be created for the code
      to build. If you have a ``go.mod`` file in the source directory, this
      typically matches the path in the ``module`` line in this file.

      Other Go programs importing this package will use this path.

      Here is an example setting from the
      :yocto_git:`go-helloworld_0.1.bb </poky/tree/meta/recipes-extended/go-examples/go-helloworld_0.1.bb>`
      recipe::

          GO_IMPORT = "golang.org/x/example"

   :term:`GO_INSTALL`
      When inheriting the :ref:`ref-classes-go` class, this optional variable
      specifies which packages in the sources should be compiled and
      installed in the Go build space by the
      `go install <https://go.dev/ref/mod#go-install>`__ command.

      Here is an example setting from the
      :oe_git:`crucible </meta-openembedded/tree/meta-oe/recipes-support/crucible/>`
      recipe::

         GO_INSTALL = "\
             ${GO_IMPORT}/cmd/crucible \
             ${GO_IMPORT}/cmd/habtool \
         "

      By default, :term:`GO_INSTALL` is defined as::

         GO_INSTALL ?= "${GO_IMPORT}/..."

      The ``...`` wildcard means that it will catch all
      packages found in the sources.

      See the :term:`GO_INSTALL_FILTEROUT` variable for
      filtering out unwanted packages from the ones
      found from the :term:`GO_INSTALL` value.

   :term:`GO_INSTALL_FILTEROUT`
      When using the Go "vendor" mechanism to bring in dependencies for a Go
      package, the default :term:`GO_INSTALL` setting, which uses the ``...``
      wildcard, will include the vendored packages in the build, which produces
      incorrect results.

      There are also some Go packages that are structured poorly, so that the
      ``...`` wildcard results in building example or test code that should not
      be included in the build, or could fail to build.

      This optional variable allows for filtering out a subset of the sources.
      It defaults to excluding everything under the ``vendor`` subdirectory
      under package's main directory. This is the normal location for vendored
      packages, but it can be overridden by a recipe to filter out other
      subdirectories if needed.

   :term:`GO_WORKDIR`
      When using Go Modules, the current working directory must be the directory
      containing the ``go.mod`` file, or one of its subdirectories. When the
      ``go`` tool is used, it will automatically look for the ``go.mod`` file
      in the Go working directory or in any parent directory, but not in
      subdirectories.

      When using the :ref:`ref-classes-go-mod` class to use Go modules,
      the optional :term:`GO_WORKDIR` variable, defaulting to the value
      of :term:`GO_IMPORT`, allows to specify a different Go working directory.

   :term:`GROUPADD_PARAM`
      When inheriting the :ref:`ref-classes-useradd` class,
      this variable specifies for a package what parameters should be
      passed to the ``groupadd`` command if you wish to add a group to the
      system when the package is installed.

      Here is an example from the ``dbus`` recipe::

         GROUPADD_PARAM:${PN} = "-r netdev"

      More than one group can be added by separating each set of different
      groups' parameters with a semicolon.

      Here is an example adding multiple groups from the ``useradd-example.bb``
      file in the ``meta-skeleton`` layer::

         GROUPADD_PARAM:${PN} = "-g 880 group1; -g 890 group2"

      For information on the standard Linux shell command
      ``groupadd``, see https://linux.die.net/man/8/groupadd.

   :term:`GROUPMEMS_PARAM`
      When inheriting the :ref:`ref-classes-useradd` class,
      this variable specifies for a package what parameters should be
      passed to the ``groupmems`` command if you wish to modify the members
      of a group when the package is installed.

      For information on the standard Linux shell command ``groupmems``,
      see https://linux.die.net/man/8/groupmems.

   :term:`GRUB_GFXSERIAL`
      Configures the GNU GRand Unified Bootloader (GRUB) to have graphics
      and serial in the boot menu. Set this variable to "1" in your
      ``local.conf`` or distribution configuration file to enable graphics
      and serial in the menu.

      See the :ref:`ref-classes-grub-efi` class for more
      information on how this variable is used.

   :term:`GRUB_OPTS`
      Additional options to add to the GNU GRand Unified Bootloader (GRUB)
      configuration. Use a semi-colon character (``;``) to separate
      multiple options.

      The :term:`GRUB_OPTS` variable is optional. See the
      :ref:`ref-classes-grub-efi` class for more information
      on how this variable is used.

   :term:`GRUB_TIMEOUT`
      Specifies the timeout before executing the default ``LABEL`` in the
      GNU GRand Unified Bootloader (GRUB).

      The :term:`GRUB_TIMEOUT` variable is optional. See the
      :ref:`ref-classes-grub-efi` class for more information
      on how this variable is used.

   :term:`GTKIMMODULES_PACKAGES`
      When inheriting the :ref:`ref-classes-gtk-immodules-cache` class,
      this variable specifies the packages that contain the GTK+ input
      method modules being installed when the modules are in packages other
      than the main package.

   :term:`HGDIR`
      See :term:`bitbake:HGDIR` in the BitBake manual.

   :term:`HOMEPAGE`
      Website where more information about the software the recipe is
      building can be found.

   :term:`HOST_ARCH`
      The name of the target architecture, which is normally the same as
      :term:`TARGET_ARCH`. The OpenEmbedded build system
      supports many architectures. Here is an example list of architectures
      supported. This list is by no means complete as the architecture is
      configurable:

      - arm
      - i586
      - x86_64
      - powerpc
      - powerpc64
      - mips
      - mipsel

   :term:`HOST_CC_ARCH`
      Specifies architecture-specific compiler flags that are passed to the
      C compiler.

      Default initialization for :term:`HOST_CC_ARCH` varies depending on what
      is being built:

      -  :term:`TARGET_CC_ARCH` when building for the
         target

      -  :term:`BUILD_CC_ARCH` when building for the build host (i.e.
         ``-native``)

      -  ``BUILDSDK_CC_ARCH`` when building for an SDK (i.e.
         ``nativesdk-``)

   :term:`HOST_OS`
      Specifies the name of the target operating system, which is normally
      the same as the :term:`TARGET_OS`. The variable can
      be set to "linux" for ``glibc``-based systems and to "linux-musl" for
      ``musl``. For ARM/EABI targets, there are also "linux-gnueabi" and
      "linux-musleabi" values possible.

   :term:`HOST_PREFIX`
      Specifies the prefix for the cross-compile toolchain. :term:`HOST_PREFIX`
      is normally the same as :term:`TARGET_PREFIX`.

   :term:`HOST_SYS`
      Specifies the system, including the architecture and the operating
      system, for which the build is occurring in the context of the
      current recipe.

      The OpenEmbedded build system automatically sets this variable based
      on :term:`HOST_ARCH`,
      :term:`HOST_VENDOR`, and
      :term:`HOST_OS` variables.

      .. note::

         You do not need to set the variable yourself.

      Consider these two examples:

      -  Given a native recipe on a 32-bit x86 machine running Linux, the
         value is "i686-linux".

      -  Given a recipe being built for a little-endian MIPS target running
         Linux, the value might be "mipsel-linux".

   :term:`HOST_VENDOR`
      Specifies the name of the vendor. :term:`HOST_VENDOR` is normally the
      same as :term:`TARGET_VENDOR`.

   :term:`HOSTTOOLS`
      A space-separated list (filter) of tools on the build host that
      should be allowed to be called from within build tasks. Using this
      filter helps reduce the possibility of host contamination. If a tool
      specified in the value of :term:`HOSTTOOLS` is not found on the build
      host, the OpenEmbedded build system produces an error and the build
      is not started.

      For additional information, see
      :term:`HOSTTOOLS_NONFATAL`.

   :term:`HOSTTOOLS_NONFATAL`
      A space-separated list (filter) of tools on the build host that
      should be allowed to be called from within build tasks. Using this
      filter helps reduce the possibility of host contamination. Unlike
      :term:`HOSTTOOLS`, the OpenEmbedded build system
      does not produce an error if a tool specified in the value of
      :term:`HOSTTOOLS_NONFATAL` is not found on the build host. Thus, you can
      use :term:`HOSTTOOLS_NONFATAL` to filter optional host tools.

   :term:`ICECC_CLASS_DISABLE`
      Identifies user classes that you do not want the Icecream distributed
      compile support to consider. This variable is used by the
      :ref:`ref-classes-icecc` class. You set this variable in
      your ``local.conf`` file.

      When you list classes using this variable, the recipes inheriting
      those classes will not benefit from distributed compilation across
      remote hosts. Instead they will be built locally.

   :term:`ICECC_DISABLED`
      Disables or enables the ``icecc`` (Icecream) function. For more
      information on this function and best practices for using this
      variable, see the ":ref:`ref-classes-icecc`"
      section.

      Setting this variable to "1" in your ``local.conf`` disables the
      function::

         ICECC_DISABLED ??= "1"

      To enable the function, set the variable as follows::

         ICECC_DISABLED = ""

   :term:`ICECC_ENV_EXEC`
      Points to the ``icecc-create-env`` script that you provide. This
      variable is used by the :ref:`ref-classes-icecc` class. You
      set this variable in your ``local.conf`` file.

      If you do not point to a script that you provide, the OpenEmbedded
      build system uses the default script provided by the
      :oe_git:`icecc-create-env_0.1.bb
      </openembedded-core/tree/meta/recipes-devtools/icecc-create-env/icecc-create-env_0.1.bb>`
      recipe, which is a modified version and not the one that comes with
      ``icecream``.

   :term:`ICECC_PARALLEL_MAKE`
      Extra options passed to the ``make`` command during the
      :ref:`ref-tasks-compile` task that specify parallel
      compilation. This variable usually takes the form of "-j x", where x
      represents the maximum number of parallel threads ``make`` can run.

      .. note::

         The options passed affect builds on all enabled machines on the
         network, which are machines running the ``iceccd`` daemon.

      If your enabled machines support multiple cores, coming up with the
      maximum number of parallel threads that gives you the best
      performance could take some experimentation since machine speed,
      network lag, available memory, and existing machine loads can all
      affect build time. Consequently, unlike the
      :term:`PARALLEL_MAKE` variable, there is no
      rule-of-thumb for setting :term:`ICECC_PARALLEL_MAKE` to achieve optimal
      performance.

      If you do not set :term:`ICECC_PARALLEL_MAKE`, the build system does not
      use it (i.e. the system does not detect and assign the number of
      cores as is done with :term:`PARALLEL_MAKE`).

   :term:`ICECC_PATH`
      The location of the ``icecc`` binary. You can set this variable in
      your ``local.conf`` file. If your ``local.conf`` file does not define
      this variable, the :ref:`ref-classes-icecc` class attempts
      to define it by locating ``icecc`` using ``which``.

   :term:`ICECC_RECIPE_DISABLE`
      Identifies user recipes that you do not want the Icecream distributed
      compile support to consider. This variable is used by the
      :ref:`ref-classes-icecc` class. You set this variable in
      your ``local.conf`` file.

      When you list recipes using this variable, you are excluding them
      from distributed compilation across remote hosts. Instead they will
      be built locally.

   :term:`ICECC_RECIPE_ENABLE`
      Identifies user recipes that use an empty
      :term:`PARALLEL_MAKE` variable that you want to
      force remote distributed compilation on using the Icecream
      distributed compile support. This variable is used by the
      :ref:`ref-classes-icecc` class. You set this variable in
      your ``local.conf`` file.

   :term:`IMAGE_BASENAME`
      The base name of image output files. This variable defaults to the
      recipe name (``${``\ :term:`PN`\ ``}``).

   :term:`IMAGE_BOOT_FILES`
      A space-separated list of files installed into the boot partition
      when preparing an image using the Wic tool with the
      ``bootimg-partition`` source plugin. By default,
      the files are
      installed under the same name as the source files. To change the
      installed name, separate it from the original name with a semi-colon
      (;). Source files need to be located in
      :term:`DEPLOY_DIR_IMAGE`. Here are two
      examples::

         IMAGE_BOOT_FILES = "u-boot.img uImage;kernel"
         IMAGE_BOOT_FILES = "u-boot.${UBOOT_SUFFIX} ${KERNEL_IMAGETYPE}"

      Alternatively, source files can be picked up using a glob pattern. In
      this case, the destination file must have the same name as the base
      name of the source file path. To install files into a directory
      within the target location, pass its name after a semi-colon (;).
      Here are two examples::

         IMAGE_BOOT_FILES = "bcm2835-bootfiles/*"
         IMAGE_BOOT_FILES = "bcm2835-bootfiles/*;boot/"

      The first example
      installs all files from ``${DEPLOY_DIR_IMAGE}/bcm2835-bootfiles``
      into the root of the target partition. The second example installs
      the same files into a ``boot`` directory within the target partition.

      You can find information on how to use the Wic tool in the
      ":ref:`dev-manual/wic:creating partitioned images using wic`"
      section of the Yocto Project Development Tasks Manual. Reference
      material for Wic is located in the
      ":doc:`/ref-manual/kickstart`" chapter.

   :term:`IMAGE_BUILDINFO_FILE`
      When using the :ref:`ref-classes-image-buildinfo` class,
      specifies the file in the image to write the build information into. The
      default value is "``${sysconfdir}/buildinfo``".

   :term:`IMAGE_BUILDINFO_VARS`
      When using the :ref:`ref-classes-image-buildinfo` class,
      specifies the list of variables to include in the `Build Configuration`
      section of the output file (as a space-separated list). Defaults to
      ":term:`DISTRO` :term:`DISTRO_VERSION`".

   :term:`IMAGE_CLASSES`
      A list of classes that all images should inherit. This is typically used
      to enable functionality across all image recipes.

      Classes specified in :term:`IMAGE_CLASSES` must be located in the
      ``classes-recipe/`` or ``classes/`` subdirectories.

   :term:`IMAGE_CMD`
      Specifies the command to create the image file for a specific image
      type, which corresponds to the value set in
      :term:`IMAGE_FSTYPES`, (e.g. ``ext3``,
      ``btrfs``, and so forth). When setting this variable, you should use
      an override for the associated type. Here is an example::

         IMAGE_CMD:jffs2 = "mkfs.jffs2 --root=${IMAGE_ROOTFS} --faketime \
             --output=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.jffs2 \
             ${EXTRA_IMAGECMD}"

      You typically do not need to set this variable unless you are adding
      support for a new image type. For more examples on how to set this
      variable, see the :ref:`ref-classes-image_types`
      class file, which is ``meta/classes-recipe/image_types.bbclass``.

   :term:`IMAGE_DEVICE_TABLES`
      Specifies one or more files that contain custom device tables that
      are passed to the ``makedevs`` command as part of creating an image.
      These files list basic device nodes that should be created under
      ``/dev`` within the image. If :term:`IMAGE_DEVICE_TABLES` is not set,
      ``files/device_table-minimal.txt`` is used, which is located by
      :term:`BBPATH`. For details on how you should write
      device table files, see ``meta/files/device_table-minimal.txt`` as an
      example.

   :term:`IMAGE_EFI_BOOT_FILES`
      A space-separated list of files installed into the boot partition
      when preparing an image using the Wic tool with the
      ``bootimg-efi`` source plugin. By default,
      the files are
      installed under the same name as the source files. To change the
      installed name, separate it from the original name with a semi-colon
      (;). Source files need to be located in
      :term:`DEPLOY_DIR_IMAGE`. Here are two
      examples::

         IMAGE_EFI_BOOT_FILES = "${KERNEL_IMAGETYPE};bz2"
         IMAGE_EFI_BOOT_FILES = "${KERNEL_IMAGETYPE} microcode.cpio"

      Alternatively, source files can be picked up using a glob pattern. In
      this case, the destination file must have the same name as the base
      name of the source file path. To install files into a directory
      within the target location, pass its name after a semi-colon (;).
      Here are two examples::

         IMAGE_EFI_BOOT_FILES = "boot/loader/*"
         IMAGE_EFI_BOOT_FILES = "boot/loader/*;boot/"

      The first example
      installs all files from ``${DEPLOY_DIR_IMAGE}/boot/loader/``
      into the root of the target partition. The second example installs
      the same files into a ``boot`` directory within the target partition.

      You can find information on how to use the Wic tool in the
      ":ref:`dev-manual/wic:creating partitioned images using wic`"
      section of the Yocto Project Development Tasks Manual. Reference
      material for Wic is located in the
      ":doc:`/ref-manual/kickstart`" chapter.

   :term:`IMAGE_FEATURES`
      The primary list of features to include in an image. Typically, you
      configure this variable in an image recipe. Although you can use this
      variable from your ``local.conf`` file, which is found in the
      :term:`Build Directory`, best practices dictate that you do
      not.

      .. note::

         To enable extra features from outside the image recipe, use the
         :term:`EXTRA_IMAGE_FEATURES` variable.

      For a list of image features that ships with the Yocto Project, see
      the ":ref:`ref-features-image`" section.

      For an example that shows how to customize your image by using this
      variable, see the ":ref:`dev-manual/customizing-images:customizing images using custom \`\`image_features\`\` and \`\`extra_image_features\`\``"
      section in the Yocto Project Development Tasks Manual.

   :term:`IMAGE_FSTYPES`
      Specifies the formats the OpenEmbedded build system uses during the
      build when creating the root filesystem. For example, setting
      :term:`IMAGE_FSTYPES` as follows causes the build system to create root
      filesystems using two formats: ``.ext3`` and ``.tar.bz2``::

         IMAGE_FSTYPES = "ext3 tar.bz2"

      For the complete list of supported image formats from which you can
      choose, see :term:`IMAGE_TYPES`.

      .. note::

         -  If an image recipe uses the "inherit image" line and you are
            setting :term:`IMAGE_FSTYPES` inside the recipe, you must set
            :term:`IMAGE_FSTYPES` prior to using the "inherit image" line.

         -  Due to the way the OpenEmbedded build system processes this
            variable, you cannot update its contents by using ``:append``
            or ``:prepend``. You must use the ``+=`` operator to add one or
            more options to the :term:`IMAGE_FSTYPES` variable.

   :term:`IMAGE_INSTALL`
      Used by recipes to specify the packages to install into an image
      through the :ref:`ref-classes-image` class. Use the
      :term:`IMAGE_INSTALL` variable with care to avoid ordering issues.

      Image recipes set :term:`IMAGE_INSTALL` to specify the packages to
      install into an image through :ref:`ref-classes-image`. Additionally,
      there are "helper" classes such as the :ref:`ref-classes-core-image`
      class which can take lists used with :term:`IMAGE_FEATURES` and turn
      them into auto-generated entries in :term:`IMAGE_INSTALL` in addition
      to its default contents.

      When you use this variable, it is best to use it as follows::

         IMAGE_INSTALL:append = " package-name"

      Be sure to include the space
      between the quotation character and the start of the package name or
      names.

      .. note::

         -  When working with a
            :ref:`core-image-minimal-initramfs <ref-manual/images:images>`
            image, do not use the :term:`IMAGE_INSTALL` variable to specify
            packages for installation. Instead, use the
            :term:`PACKAGE_INSTALL` variable, which
            allows the initial RAM filesystem (:term:`Initramfs`) recipe to use a
            fixed set of packages and not be affected by :term:`IMAGE_INSTALL`.
            For information on creating an :term:`Initramfs`, see the
            ":ref:`dev-manual/building:building an initial ram filesystem (Initramfs) image`"
            section in the Yocto Project Development Tasks Manual.

         -  Using :term:`IMAGE_INSTALL` with the
            :ref:`+= <bitbake-user-manual/bitbake-user-manual-metadata:appending (+=) and prepending (=+) with spaces>`
            BitBake operator within the ``/conf/local.conf`` file or from
            within an image recipe is not recommended. Use of this operator in
            these ways can cause ordering issues. Since
            :ref:`ref-classes-core-image` sets :term:`IMAGE_INSTALL` to a
            default value using the
            :ref:`?= <bitbake-user-manual/bitbake-user-manual-metadata:setting a default value (?=)>`
            operator, using a ``+=`` operation against :term:`IMAGE_INSTALL`
            results in unexpected behavior when used within
            ``conf/local.conf``. Furthermore, the same operation from within an
            image recipe may or may not succeed depending on the specific
            situation. In both these cases, the behavior is contrary to how
            most users expect the ``+=`` operator to work.

   :term:`IMAGE_LINGUAS`
      Specifies the list of locales to install into the image during the
      root filesystem construction process. The OpenEmbedded build system
      automatically splits locale files, which are used for localization,
      into separate packages. Setting the :term:`IMAGE_LINGUAS` variable
      ensures that any locale packages that correspond to packages already
      selected for installation into the image are also installed. Here is
      an example::

         IMAGE_LINGUAS = "pt-br de-de"

      In this example, the build system ensures any Brazilian Portuguese
      and German locale files that correspond to packages in the image are
      installed (i.e. ``*-locale-pt-br`` and ``*-locale-de-de`` as well as
      ``*-locale-pt`` and ``*-locale-de``, since some software packages
      only provide locale files by language and not by country-specific
      language).

      See the :term:`GLIBC_GENERATE_LOCALES`
      variable for information on generating GLIBC locales.


   :term:`IMAGE_LINK_NAME`
      The name of the output image symlink (which does not include
      the version part as :term:`IMAGE_NAME` does). The default value
      is derived using the :term:`IMAGE_BASENAME` and
      :term:`IMAGE_MACHINE_SUFFIX` variables::

         IMAGE_LINK_NAME ?= "${IMAGE_BASENAME}${IMAGE_MACHINE_SUFFIX}"

      .. note::

         It is possible to set this to "" to disable symlink creation,
         however, you also need to set :term:`IMAGE_NAME` to still have
         a reasonable value e.g.::

            IMAGE_LINK_NAME = ""
            IMAGE_NAME = "${IMAGE_BASENAME}${IMAGE_MACHINE_SUFFIX}${IMAGE_VERSION_SUFFIX}"

   :term:`IMAGE_MACHINE_SUFFIX`
      Specifies the by default machine-specific suffix for image file names
      (before the extension). The default value is set as follows::

         IMAGE_MACHINE_SUFFIX ??= "-${MACHINE}"

      The default :term:`DEPLOY_DIR_IMAGE` already has a :term:`MACHINE`
      subdirectory, so you may find it unnecessary to also include this suffix
      in the name of every image file. If you prefer to remove the suffix you
      can set this variable to an empty string::

         IMAGE_MACHINE_SUFFIX = ""

      (Not to be confused with :term:`IMAGE_NAME_SUFFIX`.)

   :term:`IMAGE_MANIFEST`
      The manifest file for the image. This file lists all the installed
      packages that make up the image. The file contains package
      information on a line-per-package basis as follows::

          packagename packagearch version

      The :ref:`rootfs-postcommands <ref-classes-rootfs*>` class defines the manifest
      file as follows::

         IMAGE_MANIFEST ="${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.manifest"

      The location is
      derived using the :term:`IMGDEPLOYDIR`
      and :term:`IMAGE_NAME` variables. You can find
      information on how the image is created in the ":ref:`overview-manual/concepts:image generation`"
      section in the Yocto Project Overview and Concepts Manual.

   :term:`IMAGE_NAME`
      The name of the output image files minus the extension. By default
      this variable is set using the :term:`IMAGE_LINK_NAME`, and
      :term:`IMAGE_VERSION_SUFFIX` variables::

         IMAGE_NAME ?= "${IMAGE_LINK_NAME}${IMAGE_VERSION_SUFFIX}"

   :term:`IMAGE_NAME_SUFFIX`
      Suffix used for the image output filename --- defaults to ``".rootfs"``
      to distinguish the image file from other files created during image
      building; however if this suffix is redundant or not desired you can
      clear the value of this variable (set the value to ""). For example,
      this is typically cleared in :term:`Initramfs` image recipes.

   :term:`IMAGE_OVERHEAD_FACTOR`
      Defines a multiplier that the build system applies to the initial
      image size for cases when the multiplier times the returned disk
      usage value for the image is greater than the sum of
      :term:`IMAGE_ROOTFS_SIZE` and :term:`IMAGE_ROOTFS_EXTRA_SPACE`. The result of
      the multiplier applied to the initial image size creates free disk
      space in the image as overhead. By default, the build process uses a
      multiplier of 1.3 for this variable. This default value results in
      30% free disk space added to the image when this method is used to
      determine the final generated image size. You should be aware that
      post install scripts and the package management system uses disk
      space inside this overhead area. Consequently, the multiplier does
      not produce an image with all the theoretical free disk space. See
      :term:`IMAGE_ROOTFS_SIZE` for information on how the build system
      determines the overall image size.

      The default 30% free disk space typically gives the image enough room
      to boot and allows for basic post installs while still leaving a
      small amount of free disk space. If 30% free space is inadequate, you
      can increase the default value. For example, the following setting
      gives you 50% free space added to the image::

         IMAGE_OVERHEAD_FACTOR = "1.5"

      Alternatively, you can ensure a specific amount of free disk space is
      added to the image by using the :term:`IMAGE_ROOTFS_EXTRA_SPACE`
      variable.

   :term:`IMAGE_PKGTYPE`
      Defines the package type (i.e. DEB, RPM, IPK, or TAR) used by the
      OpenEmbedded build system. The variable is defined appropriately by
      the :ref:`ref-classes-package_deb`, :ref:`ref-classes-package_rpm`,
      or :ref:`ref-classes-package_ipk` class.

      The :ref:`ref-classes-populate-sdk-*` and :ref:`ref-classes-image`
      classes use the :term:`IMAGE_PKGTYPE` for packaging up images and SDKs.

      You should not set the :term:`IMAGE_PKGTYPE` manually. Rather, the
      variable is set indirectly through the appropriate
      :ref:`package_* <ref-classes-package>` class using the
      :term:`PACKAGE_CLASSES` variable. The
      OpenEmbedded build system uses the first package type (e.g. DEB, RPM,
      or IPK) that appears with the variable

      .. note::

         Files using the ``.tar`` format are never used as a substitute
         packaging format for DEB, RPM, and IPK formatted files for your image
         or SDK.

   :term:`IMAGE_POSTPROCESS_COMMAND`
      Specifies a list of functions to call once the OpenEmbedded build
      system creates the final image output files. You can specify
      functions separated by spaces::

         IMAGE_POSTPROCESS_COMMAND += "function"

      If you need to pass the root filesystem path to a command within the
      function, you can use ``${IMAGE_ROOTFS}``, which points to the
      directory that becomes the root filesystem image. See the
      :term:`IMAGE_ROOTFS` variable for more
      information.

   :term:`IMAGE_PREPROCESS_COMMAND`
      Specifies a list of functions to call before the OpenEmbedded build
      system creates the final image output files. You can specify
      functions separated by spaces::

         IMAGE_PREPROCESS_COMMAND += "function"

      If you need to pass the root filesystem path to a command within the
      function, you can use ``${IMAGE_ROOTFS}``, which points to the
      directory that becomes the root filesystem image. See the
      :term:`IMAGE_ROOTFS` variable for more
      information.

   :term:`IMAGE_ROOTFS`
      The location of the root filesystem while it is under construction
      (i.e. during the :ref:`ref-tasks-rootfs` task). This
      variable is not configurable. Do not change it.

   :term:`IMAGE_ROOTFS_ALIGNMENT`
      Specifies the alignment for the output image file in Kbytes. If the
      size of the image is not a multiple of this value, then the size is
      rounded up to the nearest multiple of the value. The default value is
      "1". See :term:`IMAGE_ROOTFS_SIZE` for
      additional information.

   :term:`IMAGE_ROOTFS_EXTRA_SPACE`
      Defines additional free disk space created in the image in Kbytes. By
      default, this variable is set to "0". This free disk space is added
      to the image after the build system determines the image size as
      described in :term:`IMAGE_ROOTFS_SIZE`.

      This variable is particularly useful when you want to ensure that a
      specific amount of free disk space is available on a device after an
      image is installed and running. For example, to be sure 5 Gbytes of
      free disk space is available, set the variable as follows::

         IMAGE_ROOTFS_EXTRA_SPACE = "5242880"

      For example, the Yocto Project Build Appliance specifically requests
      40 Gbytes of extra space with the line::

         IMAGE_ROOTFS_EXTRA_SPACE = "41943040"

   :term:`IMAGE_ROOTFS_SIZE`
      Defines the size in Kbytes for the generated image. The OpenEmbedded
      build system determines the final size for the generated image using
      an algorithm that takes into account the initial disk space used for
      the generated image, a requested size for the image, and requested
      additional free disk space to be added to the image. Programatically,
      the build system determines the final size of the generated image as
      follows::

         if (image-du * overhead) < rootfs-size:
             internal-rootfs-size = rootfs-size + xspace
         else:
             internal-rootfs-size = (image-du * overhead) + xspace
         where:
             image-du = Returned value of the du command on the image.
             overhead = IMAGE_OVERHEAD_FACTOR
             rootfs-size = IMAGE_ROOTFS_SIZE
             internal-rootfs-size = Initial root filesystem size before any modifications.
             xspace = IMAGE_ROOTFS_EXTRA_SPACE

      See the :term:`IMAGE_OVERHEAD_FACTOR`
      and :term:`IMAGE_ROOTFS_EXTRA_SPACE`
      variables for related information.

   :term:`IMAGE_TYPEDEP`
      Specifies a dependency from one image type on another. Here is an
      example from the :ref:`ref-classes-image-live` class::

         IMAGE_TYPEDEP:live = "ext3"

      In the previous example, the variable ensures that when "live" is
      listed with the :term:`IMAGE_FSTYPES` variable,
      the OpenEmbedded build system produces an ``ext3`` image first since
      one of the components of the live image is an ``ext3`` formatted
      partition containing the root filesystem.

   :term:`IMAGE_TYPES`
      Specifies the complete list of supported image types by default:

      - btrfs
      - container
      - cpio
      - cpio.gz
      - cpio.lz4
      - cpio.lzma
      - cpio.xz
      - cramfs
      - erofs
      - erofs-lz4
      - erofs-lz4hc
      - ext2
      - ext2.bz2
      - ext2.gz
      - ext2.lzma
      - ext3
      - ext3.gz
      - ext4
      - ext4.gz
      - f2fs
      - hddimg
      - iso
      - jffs2
      - jffs2.sum
      - multiubi
      - squashfs
      - squashfs-lz4
      - squashfs-lzo
      - squashfs-xz
      - tar
      - tar.bz2
      - tar.gz
      - tar.lz4
      - tar.xz
      - tar.zst
      - ubi
      - ubifs
      - wic
      - wic.bz2
      - wic.gz
      - wic.lzma

      For more information about these types of images, see
      ``meta/classes-recipe/image_types*.bbclass`` in the :term:`Source Directory`.

   :term:`IMAGE_VERSION_SUFFIX`
      Version suffix that is part of the default :term:`IMAGE_NAME` and
      :term:`KERNEL_ARTIFACT_NAME` values.
      Defaults to ``"-${DATETIME}"``, however you could set this to a
      version string that comes from your external build environment if
      desired, and this suffix would then be used consistently across
      the build artifacts.

   :term:`IMGDEPLOYDIR`
      When inheriting the :ref:`ref-classes-image` class directly or
      through the :ref:`ref-classes-core-image` class, the
      :term:`IMGDEPLOYDIR` points to a temporary work area for deployed files
      that is set in the ``image`` class as follows::

         IMGDEPLOYDIR = "${WORKDIR}/deploy-${PN}-image-complete"

      Recipes inheriting the :ref:`ref-classes-image` class should copy
      files to be deployed into :term:`IMGDEPLOYDIR`, and the class will take
      care of copying them into :term:`DEPLOY_DIR_IMAGE` afterwards.

   :term:`INCOMPATIBLE_LICENSE`
      Specifies a space-separated list of license names (as they would
      appear in :term:`LICENSE`) that should be excluded
      from the build (if set globally), or from an image (if set locally
      in an image recipe).

      When the variable is set globally, recipes that provide no alternatives to listed
      incompatible licenses are not built. Packages that are individually
      licensed with the specified incompatible licenses will be deleted.
      Most of the time this does not allow a feasible build (because it becomes impossible
      to satisfy build time dependencies), so the recommended way to
      implement license restrictions is to set the variable in specific
      image recipes where the restrictions must apply. That way there
      are no build time restrictions, but the license check is still
      performed when the image's filesystem is assembled from packages.

      There is some support for wildcards in this variable's value,
      however it is restricted to specific licenses. Currently only
      these wildcards are allowed and expand as follows:

      - ``AGPL-3.0*"``: ``AGPL-3.0-only``, ``AGPL-3.0-or-later``
      - ``GPL-3.0*``: ``GPL-3.0-only``, ``GPL-3.0-or-later``
      - ``LGPL-3.0*``: ``LGPL-3.0-only``, ``LGPL-3.0-or-later``

      .. note::

         This functionality is only regularly tested using the following
         setting::

                 INCOMPATIBLE_LICENSE = "GPL-3.0* LGPL-3.0* AGPL-3.0*"


         Although you can use other settings, you might be required to
         remove dependencies on (or provide alternatives to) components that
         are required to produce a functional system image.

   :term:`INCOMPATIBLE_LICENSE_EXCEPTIONS`
      Specifies a space-separated list of package and license pairs that
      are allowed to be used even if the license is specified in
      :term:`INCOMPATIBLE_LICENSE`. The package and license pairs are
      separated using a colon. Example::

         INCOMPATIBLE_LICENSE_EXCEPTIONS = "gdbserver:GPL-3.0-only gdbserver:LGPL-3.0-only"

   :term:`INHERIT`
      Causes the named class or classes to be inherited globally. Anonymous
      functions in the class or classes are not executed for the base
      configuration and in each individual recipe. The OpenEmbedded build
      system ignores changes to :term:`INHERIT` in individual recipes.
      Classes inherited using :term:`INHERIT` must be located in the
      ``classes-global/`` or ``classes/`` subdirectories.

      For more information on :term:`INHERIT`, see the
      :ref:`bitbake-user-manual/bitbake-user-manual-metadata:\`\`inherit\`\` configuration directive`"
      section in the BitBake User Manual.

   :term:`INHERIT_DISTRO`
      Lists classes that will be inherited at the distribution level. It is
      unlikely that you want to edit this variable.

      Classes specified in :term:`INHERIT_DISTRO` must be located in the
      ``classes-global/`` or ``classes/`` subdirectories.

      The default value of the variable is set as follows in the
      ``meta/conf/distro/defaultsetup.conf`` file::

         INHERIT_DISTRO ?= "debian devshell sstate license remove-libtool create-spdx"

   :term:`INHIBIT_DEFAULT_DEPS`
      Prevents the default dependencies, namely the C compiler and standard
      C library (libc), from being added to :term:`DEPENDS`.
      This variable is usually used within recipes that do not require any
      compilation using the C compiler.

      Set the variable to "1" to prevent the default dependencies from
      being added.

   :term:`INHIBIT_PACKAGE_DEBUG_SPLIT`
      Prevents the OpenEmbedded build system from splitting out debug
      information during packaging. By default, the build system splits out
      debugging information during the
      :ref:`ref-tasks-package` task. For more information on
      how debug information is split out, see the
      :term:`PACKAGE_DEBUG_SPLIT_STYLE`
      variable.

      To prevent the build system from splitting out debug information
      during packaging, set the :term:`INHIBIT_PACKAGE_DEBUG_SPLIT` variable as
      follows::

         INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

   :term:`INHIBIT_PACKAGE_STRIP`
      If set to "1", causes the build to not strip binaries in resulting
      packages and prevents the ``-dbg`` package from containing the source
      files.

      By default, the OpenEmbedded build system strips binaries and puts
      the debugging symbols into ``${``\ :term:`PN`\ ``}-dbg``.
      Consequently, you should not set :term:`INHIBIT_PACKAGE_STRIP` when you
      plan to debug in general.

   :term:`INHIBIT_SYSROOT_STRIP`
      If set to "1", causes the build to not strip binaries in the
      resulting sysroot.

      By default, the OpenEmbedded build system strips binaries in the
      resulting sysroot. When you specifically set the
      :term:`INHIBIT_SYSROOT_STRIP` variable to "1" in your recipe, you inhibit
      this stripping.

      If you want to use this variable, include the :ref:`ref-classes-staging`
      class. This class uses a ``sys_strip()`` function to test for the variable
      and acts accordingly.

      .. note::

         Use of the :term:`INHIBIT_SYSROOT_STRIP` variable occurs in rare and
         special circumstances. For example, suppose you are building
         bare-metal firmware by using an external GCC toolchain. Furthermore,
         even if the toolchain's binaries are strippable, there are other files
         needed for the build that are not strippable.

   :term:`INIT_MANAGER`
      Specifies the system init manager to use. Available options are:

      -  ``sysvinit``
      -  ``systemd``
      -  ``mdev-busybox``

      With ``sysvinit``, the init manager is set to
      :wikipedia:`SysVinit <Init#SysV-style>`, the traditional UNIX init
      system. This is the default choice in the Poky distribution, together with
      the Udev device manager (see the ":ref:`device-manager`" section).

      With ``systemd``, the init manager becomes :wikipedia:`systemd <Systemd>`,
      which comes with the :wikipedia:`udev <Udev>` device manager.

      With ``mdev-busybox``, the init manager becomes the much simpler BusyBox
      init, together with the BusyBox mdev device manager. This is the simplest
      and lightest solution, and probably the best choice for low-end systems
      with a rather slow CPU and a limited amount of RAM.

      More concretely, this is used to include
      ``conf/distro/include/init-manager-${INIT_MANAGER}.inc`` into the global
      configuration. You can have a look at the
      :yocto_git:`meta/conf/distro/include/init-manager-*.inc </poky/tree/meta/conf/distro/include>`
      files for more information, and also the ":ref:`init-manager`"
      section in the Yocto Project Development Tasks Manual.

   :term:`INITRAMFS_DEPLOY_DIR_IMAGE`
      Indicates the deploy directory used by :ref:`ref-tasks-bundle_initramfs`
      where the :term:`INITRAMFS_IMAGE` will be fetched from. This variable is
      set by default to ``${DEPLOY_DIR_IMAGE}`` in the
      :ref:`ref-classes-kernel` class and it's only meant to be changed when
      building an :term:`Initramfs` image from a separate multiconfig via
      :term:`INITRAMFS_MULTICONFIG`.

   :term:`INITRAMFS_FSTYPES`
      Defines the format for the output image of an initial RAM filesystem
      (:term:`Initramfs`), which is used during boot. Supported formats are the
      same as those supported by the
      :term:`IMAGE_FSTYPES` variable.

      The default value of this variable, which is set in the
      ``meta/conf/bitbake.conf`` configuration file in the
      :term:`Source Directory`, is "cpio.gz". The Linux kernel's
      :term:`Initramfs` mechanism, as opposed to the initial RAM filesystem
      :wikipedia:`initrd <Initrd>` mechanism, expects
      an optionally compressed cpio archive.

   :term:`INITRAMFS_IMAGE`
      Specifies the :term:`PROVIDES` name of an image
      recipe that is used to build an initial RAM filesystem (:term:`Initramfs`)
      image. In other words, the :term:`INITRAMFS_IMAGE` variable causes an
      additional recipe to be built as a dependency to whatever root
      filesystem recipe you might be using (e.g. ``core-image-sato``). The
      :term:`Initramfs` image recipe you provide should set
      :term:`IMAGE_FSTYPES` to
      :term:`INITRAMFS_FSTYPES`.

      An :term:`Initramfs` image provides a temporary root filesystem used for
      early system initialization (e.g. loading of modules needed to locate
      and mount the "real" root filesystem).

      .. note::

         See the ``meta/recipes-core/images/core-image-minimal-initramfs.bb``
         recipe in the :term:`Source Directory`
         for an example :term:`Initramfs` recipe. To select this sample recipe as
         the one built to provide the :term:`Initramfs` image, set :term:`INITRAMFS_IMAGE`
         to "core-image-minimal-initramfs".

      You can also find more information by referencing the
      ``meta-poky/conf/templates/default/local.conf.sample.extended``
      configuration file in the Source Directory, the :ref:`ref-classes-image`
      class, and the :ref:`ref-classes-kernel` class to see how to use the
      :term:`INITRAMFS_IMAGE` variable.

      If :term:`INITRAMFS_IMAGE` is empty, which is the default, then no
      :term:`Initramfs` image is built.

      For more information, you can also see the
      :term:`INITRAMFS_IMAGE_BUNDLE`
      variable, which allows the generated image to be bundled inside the
      kernel image. Additionally, for information on creating an :term:`Initramfs`
      image, see the ":ref:`dev-manual/building:building an initial ram filesystem (Initramfs) image`" section
      in the Yocto Project Development Tasks Manual.

   :term:`INITRAMFS_IMAGE_BUNDLE`
      Controls whether or not the image recipe specified by
      :term:`INITRAMFS_IMAGE` is run through an
      extra pass
      (:ref:`ref-tasks-bundle_initramfs`) during
      kernel compilation in order to build a single binary that contains
      both the kernel image and the initial RAM filesystem (:term:`Initramfs`)
      image. This makes use of the
      :term:`CONFIG_INITRAMFS_SOURCE` kernel
      feature.

      .. note::

         Bundling the :term:`Initramfs` with the kernel conflates the code in the
         :term:`Initramfs` with the GPLv2 licensed Linux kernel binary. Thus only GPLv2
         compatible software may be part of a bundled :term:`Initramfs`.

      .. note::

         Using an extra compilation pass to bundle the :term:`Initramfs` avoids a
         circular dependency between the kernel recipe and the :term:`Initramfs`
         recipe should the :term:`Initramfs` include kernel modules. Should that be
         the case, the :term:`Initramfs` recipe depends on the kernel for the
         kernel modules, and the kernel depends on the :term:`Initramfs` recipe
         since the :term:`Initramfs` is bundled inside the kernel image.

      The combined binary is deposited into the ``tmp/deploy`` directory,
      which is part of the :term:`Build Directory`.

      Setting the variable to "1" in a configuration file causes the
      OpenEmbedded build system to generate a kernel image with the
      :term:`Initramfs` specified in :term:`INITRAMFS_IMAGE` bundled within::

         INITRAMFS_IMAGE_BUNDLE = "1"

      By default, the :ref:`ref-classes-kernel` class sets this variable to a
      null string as follows::

         INITRAMFS_IMAGE_BUNDLE ?= ""

      .. note::

         You must set the :term:`INITRAMFS_IMAGE_BUNDLE` variable in a
         configuration file. You cannot set the variable in a recipe file.

      See the
      :yocto_git:`local.conf.sample.extended </poky/tree/meta-poky/conf/templates/default/local.conf.sample.extended>`
      file for additional information. Also, for information on creating an
      :term:`Initramfs`, see the ":ref:`dev-manual/building:building an initial ram filesystem (Initramfs) image`" section
      in the Yocto Project Development Tasks Manual.

   :term:`INITRAMFS_IMAGE_NAME`

      This value needs to stay in sync with :term:`IMAGE_LINK_NAME`, but with
      :term:`INITRAMFS_IMAGE` instead of :term:`IMAGE_BASENAME`. The default value
      is set as follows:

         INITRAMFS_IMAGE_NAME ?= "${@['${INITRAMFS_IMAGE}${IMAGE_MACHINE_SUFFIX}', ''][d.getVar('INITRAMFS_IMAGE') == '']}"

      That is, if :term:`INITRAMFS_IMAGE` is set, the value of
      :term:`INITRAMFS_IMAGE_NAME` will be set based upon
      :term:`INITRAMFS_IMAGE` and :term:`IMAGE_MACHINE_SUFFIX`.


   :term:`INITRAMFS_LINK_NAME`
      The link name of the initial RAM filesystem image. This variable is
      set in the ``meta/classes-recipe/kernel-artifact-names.bbclass`` file as
      follows::

         INITRAMFS_LINK_NAME ?= "initramfs-${KERNEL_ARTIFACT_LINK_NAME}"

      The value of the
      ``KERNEL_ARTIFACT_LINK_NAME`` variable, which is set in the same
      file, has the following value::

         KERNEL_ARTIFACT_LINK_NAME ?= "${MACHINE}"

      See the :term:`MACHINE` variable for additional
      information.

   :term:`INITRAMFS_MULTICONFIG`
      Defines the multiconfig to create a multiconfig dependency to be used by
      the :ref:`ref-classes-kernel` class.

      This allows the kernel to bundle an :term:`INITRAMFS_IMAGE` coming from
      a separate multiconfig, this is meant to be used in addition to :term:`INITRAMFS_DEPLOY_DIR_IMAGE`.

      For more information on how to bundle an :term:`Initramfs` image from a separate
      multiconfig see the ":ref:`dev-manual/building:Bundling an Initramfs Image From a Separate Multiconfig`"
      section in the Yocto Project Development Tasks Manual.

   :term:`INITRAMFS_NAME`
      The base name of the initial RAM filesystem image. This variable is
      set in the ``meta/classes-recipe/kernel-artifact-names.bbclass`` file as
      follows::

         INITRAMFS_NAME ?= "initramfs-${KERNEL_ARTIFACT_NAME}"

      See :term:`KERNEL_ARTIFACT_NAME` for additional information.

   :term:`INITRD`
      Indicates list of filesystem images to concatenate and use as an
      initial RAM disk (``initrd``).

      The :term:`INITRD` variable is an optional variable used with the
      :ref:`ref-classes-image-live` class.

   :term:`INITRD_IMAGE`
      When building a "live" bootable image (i.e. when
      :term:`IMAGE_FSTYPES` contains "live"),
      :term:`INITRD_IMAGE` specifies the image recipe that should be built to
      provide the initial RAM disk image. The default value is
      "core-image-minimal-initramfs".

      See the :ref:`ref-classes-image-live` class for more information.

   :term:`INITSCRIPT_NAME`
      The filename of the initialization script as installed to
      ``${sysconfdir}/init.d``.

      This variable is used in recipes when using :ref:`ref-classes-update-rc.d`.
      The variable is mandatory.

   :term:`INITSCRIPT_PACKAGES`
      A list of the packages that contain initscripts. If multiple packages
      are specified, you need to append the package name to the other
      ``INITSCRIPT_*`` as an override.

      This variable is used in recipes when using :ref:`ref-classes-update-rc.d`.
      The variable is optional and defaults to the :term:`PN`
      variable.

   :term:`INITSCRIPT_PARAMS`
      Specifies the options to pass to ``update-rc.d``. Here is an example::

         INITSCRIPT_PARAMS = "start 99 5 2 . stop 20 0 1 6 ."

      In this example, the script has a runlevel of 99, starts the script
      in initlevels 2 and 5, and stops the script in levels 0, 1 and 6.

      The variable's default value is "defaults", which is set in the
      :ref:`ref-classes-update-rc.d` class.

      The value in :term:`INITSCRIPT_PARAMS` is passed through to the
      ``update-rc.d`` command. For more information on valid parameters,
      please see the ``update-rc.d`` manual page at
      https://manpages.debian.org/buster/init-system-helpers/update-rc.d.8.en.html

   :term:`INSANE_SKIP`
      Specifies the QA checks to skip for a specific package within a
      recipe. For example, to skip the check for symbolic link ``.so``
      files in the main package of a recipe, add the following to the
      recipe. The package name override must be used, which in this example
      is ``${PN}``::

         INSANE_SKIP:${PN} += "dev-so"

      See the ":ref:`ref-classes-insane`" section for a
      list of the valid QA checks you can specify using this variable.

   :term:`INSTALL_TIMEZONE_FILE`
      By default, the ``tzdata`` recipe packages an ``/etc/timezone`` file.
      Set the :term:`INSTALL_TIMEZONE_FILE` variable to "0" at the
      configuration level to disable this behavior.

   :term:`IPK_FEED_URIS`
      When the IPK backend is in use and package management is enabled on
      the target, you can use this variable to set up ``opkg`` in the
      target image to point to package feeds on a nominated server. Once
      the feed is established, you can perform installations or upgrades
      using the package manager at runtime.

   :term:`KARCH`
      Defines the kernel architecture used when assembling the
      configuration. Architectures supported for this release are:

      - powerpc
      - i386
      - x86_64
      - arm
      - qemu
      - mips

      You define the :term:`KARCH` variable in the :ref:`kernel-dev/advanced:bsp descriptions`.

   :term:`KBRANCH`
      A regular expression used by the build process to explicitly identify
      the kernel branch that is validated, patched, and configured during a
      build. You must set this variable to ensure the exact kernel branch
      you want is being used by the build process.

      Values for this variable are set in the kernel's recipe file and the
      kernel's append file. For example, if you are using the
      ``linux-yocto_4.12`` kernel, the kernel recipe file is the
      ``meta/recipes-kernel/linux/linux-yocto_4.12.bb`` file. :term:`KBRANCH`
      is set as follows in that kernel recipe file::

         KBRANCH ?= "standard/base"

      This variable is also used from the kernel's append file to identify
      the kernel branch specific to a particular machine or target
      hardware. Continuing with the previous kernel example, the kernel's
      append file is located in the
      BSP layer for a given machine. For example, the append file for the
      Beaglebone and generic versions of both 32 and 64-bit IA
      machines (``meta-yocto-bsp``) is named
      ``meta-yocto-bsp/recipes-kernel/linux/linux-yocto_6.1.bbappend``.
      Here are the related statements from that append file::

         KBRANCH:genericx86  = "v6.1/standard/base"
         KBRANCH:genericx86-64  = "v6.1/standard/base"
         KBRANCH:beaglebone-yocto = "v6.1/standard/beaglebone"

      The :term:`KBRANCH` statements
      identify the kernel branch to use when building for each supported
      BSP.

   :term:`KBUILD_DEFCONFIG`
      When used with the :ref:`ref-classes-kernel-yocto`
      class, specifies an "in-tree" kernel configuration file for use
      during a kernel build.

      Typically, when using a ``defconfig`` to configure a kernel during a
      build, you place the file in your layer in the same manner as you
      would place patch files and configuration fragment files (i.e.
      "out-of-tree"). However, if you want to use a ``defconfig`` file that
      is part of the kernel tree (i.e. "in-tree"), you can use the
      :term:`KBUILD_DEFCONFIG` variable and append the
      :term:`KMACHINE` variable to point to the
      ``defconfig`` file.

      To use the variable, set it in the append file for your kernel recipe
      using the following form::

         KBUILD_DEFCONFIG:<machine> ?= "defconfig_file"

      Here is an example from a "raspberrypi2" :term:`MACHINE` build that uses
      a ``defconfig`` file named "bcm2709_defconfig"::

         KBUILD_DEFCONFIG:raspberrypi2 = "bcm2709_defconfig"

      As an alternative, you can use the following within your append file::

         KBUILD_DEFCONFIG:pn-linux-yocto ?= "defconfig_file"

      For more
      information on how to use the :term:`KBUILD_DEFCONFIG` variable, see the
      ":ref:`kernel-dev/common:using an "in-tree" \`\`defconfig\`\` file`"
      section in the Yocto Project Linux Kernel Development Manual.

   :term:`KCONFIG_MODE`
      When used with the :ref:`ref-classes-kernel-yocto`
      class, specifies the kernel configuration values to use for options
      not specified in the provided ``defconfig`` file. Valid options are::

         KCONFIG_MODE = "alldefconfig"
         KCONFIG_MODE = "allnoconfig"

      In ``alldefconfig`` mode the options not explicitly specified will be
      assigned their Kconfig default value. In ``allnoconfig`` mode the
      options not explicitly specified will be disabled in the kernel
      config.

      In case :term:`KCONFIG_MODE` is not set the behaviour will depend on where
      the ``defconfig`` file is coming from. An "in-tree" ``defconfig`` file
      will be handled in ``alldefconfig`` mode, a ``defconfig`` file placed
      in ``${WORKDIR}`` through a meta-layer will be handled in
      ``allnoconfig`` mode.

      An "in-tree" ``defconfig`` file can be selected via the
      :term:`KBUILD_DEFCONFIG` variable. :term:`KCONFIG_MODE` does not need to
      be explicitly set.

      A ``defconfig`` file compatible with ``allnoconfig`` mode can be
      generated by copying the ``.config`` file from a working Linux kernel
      build, renaming it to ``defconfig`` and placing it into the Linux
      kernel ``${WORKDIR}`` through your meta-layer. :term:`KCONFIG_MODE` does
      not need to be explicitly set.

      A ``defconfig`` file compatible with ``alldefconfig`` mode can be
      generated using the
      :ref:`ref-tasks-savedefconfig`
      task and placed into the Linux kernel ``${WORKDIR}`` through your
      meta-layer. Explicitely set :term:`KCONFIG_MODE`::

         KCONFIG_MODE = "alldefconfig"

   :term:`KERNEL_ALT_IMAGETYPE`
      Specifies an alternate kernel image type for creation in addition to
      the kernel image type specified using the :term:`KERNEL_IMAGETYPE` and
      :term:`KERNEL_IMAGETYPES` variables.

   :term:`KERNEL_ARTIFACT_NAME`
      Specifies the name of all of the build artifacts. You can change the
      name of the artifacts by changing the :term:`KERNEL_ARTIFACT_NAME`
      variable.

      The value of :term:`KERNEL_ARTIFACT_NAME`, which is set in the
      ``meta/classes-recipe/kernel-artifact-names.bbclass`` file, has the
      following default value::

         KERNEL_ARTIFACT_NAME ?= "${PKGE}-${PKGV}-${PKGR}${IMAGE_MACHINE_SUFFIX}${IMAGE_VERSION_SUFFIX}"

      See the :term:`PKGE`, :term:`PKGV`, :term:`PKGR`, :term:`IMAGE_MACHINE_SUFFIX`
      and :term:`IMAGE_VERSION_SUFFIX` variables for additional information.

   :term:`KERNEL_CLASSES`
      A list of classes defining kernel image types that the
      :ref:`ref-classes-kernel` class should inherit. You typically
      append this variable to enable extended image types. An example is
      ":ref:`ref-classes-kernel-fitimage`", which enables
      FIT image support and resides in ``meta/classes-recipe/kernel-fitimage.bbclass``.
      You can register custom kernel image types with the
      :ref:`ref-classes-kernel` class using this variable.

   :term:`KERNEL_DANGLING_FEATURES_WARN_ONLY`
      When kernel configuration fragments are missing for some
      :term:`KERNEL_FEATURES` specified by layers or BSPs,
      building and configuring the kernel stops with an error.

      You can turn these errors into warnings by setting the
      following in ``conf/local.conf``::

         KERNEL_DANGLING_FEATURES_WARN_ONLY = "1"

      You will still be warned that runtime issues may occur,
      but at least the kernel configuration and build process will
      be allowed to continue.

   :term:`KERNEL_DEBUG_TIMESTAMPS`
      If set to "1", enables timestamping functionality during building
      the kernel. The default is "0" to disable this for reproducibility
      reasons.

   :term:`KERNEL_DEPLOY_DEPEND`
      Provides a means of controlling the dependency of an image recipe
      on the kernel. The default value is "virtual/kernel:do_deploy",
      however for a small initramfs image or other images that do not
      need the kernel, this can be set to "" in the image recipe.

   :term:`KERNEL_DEVICETREE`
      Specifies the name of the generated Linux kernel device tree (i.e.
      the ``.dtb``) file.

      .. note::

         There is legacy support for specifying the full path to the device
         tree. However, providing just the ``.dtb`` file is preferred.

      In order to use this variable, the :ref:`ref-classes-kernel-devicetree`
      class must be inherited.

   :term:`KERNEL_DEVICETREE_BUNDLE`
      When set to "1", this variable allows to bundle the Linux kernel
      and the Device Tree Binary together in a single file.

      This feature is currently only supported on the "arm" (32 bit)
      architecture.

      This variable is set to "0" by default by the
      :ref:`ref-classes-kernel-devicetree` class.

   :term:`KERNEL_DTB_LINK_NAME`
      The link name of the kernel device tree binary (DTB). This variable
      is set in the ``meta/classes-recipe/kernel-artifact-names.bbclass`` file as
      follows::

         KERNEL_DTB_LINK_NAME ?= "${KERNEL_ARTIFACT_LINK_NAME}"

      The
      value of the ``KERNEL_ARTIFACT_LINK_NAME`` variable, which is set in
      the same file, has the following value::

         KERNEL_ARTIFACT_LINK_NAME ?= "${MACHINE}"

      See the :term:`MACHINE` variable for additional
      information.

   :term:`KERNEL_DTB_NAME`
      The base name of the kernel device tree binary (DTB). This variable
      is set in the ``meta/classes-recipe/kernel-artifact-names.bbclass`` file as
      follows::

         KERNEL_DTB_NAME ?= "${KERNEL_ARTIFACT_NAME}"

      See :term:`KERNEL_ARTIFACT_NAME` for additional information.

   :term:`KERNEL_DTBDEST`
      This variable, used by the :ref:`ref-classes-kernel-devicetree`
      class, allows to change the installation directory of the DTB
      (Device Tree Binary) files.

      It is set by default to "${KERNEL_IMAGEDEST}" by the
      :ref:`ref-classes-kernel` class.

   :term:`KERNEL_DTBVENDORED`
      This variable, used by the :ref:`ref-classes-kernel-devicetree`,
      allows to ignore vendor subdirectories when installing DTB
      (Device Tree Binary) files, when it is set to "false".

      To keep vendor subdirectories, set this variable to "true".

      It is set by default to "false" by the :ref:`ref-classes-kernel` class.

   :term:`KERNEL_DTC_FLAGS`
      Specifies the ``dtc`` flags that are passed to the Linux kernel build
      system when generating the device trees (via ``DTC_FLAGS`` environment
      variable).

      In order to use this variable, the :ref:`ref-classes-kernel-devicetree`
      class must be inherited.

   :term:`KERNEL_EXTRA_ARGS`
      Specifies additional ``make`` command-line arguments the OpenEmbedded
      build system passes on when compiling the kernel.

   :term:`KERNEL_FEATURES`
      Includes additional kernel metadata. In the OpenEmbedded build
      system, the default Board Support Packages (BSPs)
      :term:`Metadata` is provided through the
      :term:`KMACHINE` and :term:`KBRANCH`
      variables. You can use the :term:`KERNEL_FEATURES` variable from within
      the kernel recipe or kernel append file to further add metadata for
      all BSPs or specific BSPs.

      The metadata you add through this variable includes config fragments
      and features descriptions, which usually includes patches as well as
      config fragments. You typically override the :term:`KERNEL_FEATURES`
      variable for a specific machine. In this way, you can provide
      validated, but optional, sets of kernel configurations and features.

      For example, the following example from the ``linux-yocto-rt_4.12``
      kernel recipe adds "netfilter" and "taskstats" features to all BSPs
      as well as "virtio" configurations to all QEMU machines. The last two
      statements add specific configurations to targeted machine types::

         KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc features/taskstats/taskstats.scc"
         KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
         KERNEL_FEATURES:append:qemuall = " cfg/virtio.scc"
         KERNEL_FEATURES:append:qemux86 = "  cfg/sound.scc cfg/paravirt_kvm.scc"
         KERNEL_FEATURES:append:qemux86-64 = " cfg/sound.scc"

   :term:`KERNEL_FIT_LINK_NAME`
      The link name of the kernel flattened image tree (FIT) image. This
      variable is set in the ``meta/classes-recipe/kernel-artifact-names.bbclass``
      file as follows::

         KERNEL_FIT_LINK_NAME ?= "${KERNEL_ARTIFACT_LINK_NAME}"

      The value of the
      ``KERNEL_ARTIFACT_LINK_NAME`` variable, which is set in the same
      file, has the following value::

         KERNEL_ARTIFACT_LINK_NAME ?= "${MACHINE}"

      See the :term:`MACHINE` variable for additional
      information.

   :term:`KERNEL_FIT_NAME`
      The base name of the kernel flattened image tree (FIT) image. This
      variable is set in the ``meta/classes-recipe/kernel-artifact-names.bbclass``
      file as follows::

         KERNEL_FIT_NAME ?= "${KERNEL_ARTIFACT_NAME}"

      See :term:`KERNEL_ARTIFACT_NAME` for additional information.

   :term:`KERNEL_IMAGE_LINK_NAME`
      The link name for the kernel image. This variable is set in the
      ``meta/classes-recipe/kernel-artifact-names.bbclass`` file as follows::

         KERNEL_IMAGE_LINK_NAME ?= "${KERNEL_ARTIFACT_LINK_NAME}"

      The value of
      the ``KERNEL_ARTIFACT_LINK_NAME`` variable, which is set in the same
      file, has the following value::

         KERNEL_ARTIFACT_LINK_NAME ?= "${MACHINE}"

      See the :term:`MACHINE` variable for additional
      information.

   :term:`KERNEL_IMAGE_MAXSIZE`
      Specifies the maximum size of the kernel image file in kilobytes. If
      :term:`KERNEL_IMAGE_MAXSIZE` is set, the size of the kernel image file is
      checked against the set value during the
      :ref:`ref-tasks-sizecheck` task. The task fails if
      the kernel image file is larger than the setting.

      :term:`KERNEL_IMAGE_MAXSIZE` is useful for target devices that have a
      limited amount of space in which the kernel image must be stored.

      By default, this variable is not set, which means the size of the
      kernel image is not checked.

   :term:`KERNEL_IMAGE_NAME`
      The base name of the kernel image. This variable is set in the
      ``meta/classes-recipe/kernel-artifact-names.bbclass`` file as follows::

         KERNEL_IMAGE_NAME ?= "${KERNEL_ARTIFACT_NAME}"

      See :term:`KERNEL_ARTIFACT_NAME` for additional information.

   :term:`KERNEL_IMAGETYPE`
      The type of kernel to build for a device, usually set by the machine
      configuration files and defaults to "zImage". This variable is used
      when building the kernel and is passed to ``make`` as the target to
      build.

      To build additional kernel image types, use :term:`KERNEL_IMAGETYPES`.

   :term:`KERNEL_IMAGETYPES`
      Lists additional types of kernel images to build for a device in addition
      to image type specified in :term:`KERNEL_IMAGETYPE`. Usually set by the
      machine configuration files.

   :term:`KERNEL_MODULE_AUTOLOAD`
      Lists kernel modules that need to be auto-loaded during boot.

      .. note::

         This variable replaces the deprecated :term:`module_autoload`
         variable.

      You can use the :term:`KERNEL_MODULE_AUTOLOAD` variable anywhere that it
      can be recognized by the kernel recipe or by an out-of-tree kernel
      module recipe (e.g. a machine configuration file, a distribution
      configuration file, an append file for the recipe, or the recipe
      itself).

      Specify it as follows::

         KERNEL_MODULE_AUTOLOAD += "module_name1 module_name2 module_name3"

      Including :term:`KERNEL_MODULE_AUTOLOAD` causes the OpenEmbedded build
      system to populate the ``/etc/modules-load.d/modname.conf`` file with
      the list of modules to be auto-loaded on boot. The modules appear
      one-per-line in the file. Here is an example of the most common use
      case::

         KERNEL_MODULE_AUTOLOAD += "module_name"

      For information on how to populate the ``modname.conf`` file with
      ``modprobe.d`` syntax lines, see the :term:`KERNEL_MODULE_PROBECONF` variable.

   :term:`KERNEL_MODULE_PROBECONF`
      Provides a list of modules for which the OpenEmbedded build system
      expects to find ``module_conf_``\ modname values that specify
      configuration for each of the modules. For information on how to
      provide those module configurations, see the
      :term:`module_conf_* <module_conf>` variable.

   :term:`KERNEL_PACKAGE_NAME`
      Specifies the base name of the kernel packages, such as "kernel"
      in the kernel packages such as "kernel-modules", "kernel-image" and
      "kernel-dbg".

      The default value for this variable is set to "kernel" by the
      :ref:`ref-classes-kernel` class.

   :term:`KERNEL_PATH`
      The location of the kernel sources. This variable is set to the value
      of the :term:`STAGING_KERNEL_DIR` within the :ref:`ref-classes-module`
      class. For information on how this variable is used, see the
      ":ref:`kernel-dev/common:incorporating out-of-tree modules`"
      section in the Yocto Project Linux Kernel Development Manual.

      To help maximize compatibility with out-of-tree drivers used to build
      modules, the OpenEmbedded build system also recognizes and uses the
      :term:`KERNEL_SRC` variable, which is identical to
      the :term:`KERNEL_PATH` variable. Both variables are common variables
      used by external Makefiles to point to the kernel source directory.

   :term:`KERNEL_SRC`
      The location of the kernel sources. This variable is set to the value
      of the :term:`STAGING_KERNEL_DIR` within the :ref:`ref-classes-module`
      class. For information on how this variable is used, see the
      ":ref:`kernel-dev/common:incorporating out-of-tree modules`"
      section in the Yocto Project Linux Kernel Development Manual.

      To help maximize compatibility with out-of-tree drivers used to build
      modules, the OpenEmbedded build system also recognizes and uses the
      :term:`KERNEL_PATH` variable, which is identical
      to the :term:`KERNEL_SRC` variable. Both variables are common variables
      used by external Makefiles to point to the kernel source directory.

   :term:`KERNEL_STRIP`
      Allows to specific which ``strip`` command to use to strip the kernel
      binary, typically either GNU binutils ``strip`` or ``llvm-strip``.

   :term:`KERNEL_VERSION`
      Specifies the version of the kernel as extracted from ``version.h``
      or ``utsrelease.h`` within the kernel sources. Effects of setting
      this variable do not take effect until the kernel has been
      configured. Consequently, attempting to refer to this variable in
      contexts prior to configuration will not work.

   :term:`KERNELDEPMODDEPEND`
      Specifies whether the data referenced through
      :term:`PKGDATA_DIR` is needed or not.
      :term:`KERNELDEPMODDEPEND` does not control whether or not that data
      exists, but simply whether or not it is used. If you do not need to
      use the data, set the :term:`KERNELDEPMODDEPEND` variable in your
      :term:`Initramfs` recipe. Setting the variable there when the data is not
      needed avoids a potential dependency loop.

   :term:`KFEATURE_DESCRIPTION`
      Provides a short description of a configuration fragment. You use
      this variable in the ``.scc`` file that describes a configuration
      fragment file. Here is the variable used in a file named ``smp.scc``
      to describe SMP being enabled::

          define KFEATURE_DESCRIPTION "Enable SMP"

   :term:`KMACHINE`
      The machine as known by the kernel. Sometimes the machine name used
      by the kernel does not match the machine name used by the
      OpenEmbedded build system. For example, the machine name that the
      OpenEmbedded build system understands as ``core2-32-intel-common``
      goes by a different name in the Linux Yocto kernel. The kernel
      understands that machine as ``intel-core2-32``. For cases like these,
      the :term:`KMACHINE` variable maps the kernel machine name to the
      OpenEmbedded build system machine name.

      These mappings between different names occur in the Yocto Linux
      Kernel's ``meta`` branch. As an example take a look in the
      ``common/recipes-kernel/linux/linux-yocto_3.19.bbappend`` file::

         LINUX_VERSION:core2-32-intel-common = "3.19.0"
         COMPATIBLE_MACHINE:core2-32-intel-common = "${MACHINE}"
         SRCREV_meta:core2-32-intel-common = "8897ef68b30e7426bc1d39895e71fb155d694974"
         SRCREV_machine:core2-32-intel-common = "43b9eced9ba8a57add36af07736344dcc383f711"
         KMACHINE:core2-32-intel-common = "intel-core2-32"
         KBRANCH:core2-32-intel-common = "standard/base"
         KERNEL_FEATURES:append:core2-32-intel-common = " ${KERNEL_FEATURES_INTEL_COMMON}"

      The :term:`KMACHINE` statement says
      that the kernel understands the machine name as "intel-core2-32".
      However, the OpenEmbedded build system understands the machine as
      "core2-32-intel-common".

   :term:`KTYPE`
      Defines the kernel type to be used in assembling the configuration.
      The linux-yocto recipes define "standard", "tiny", and "preempt-rt"
      kernel types. See the ":ref:`kernel-dev/advanced:kernel types`"
      section in the
      Yocto Project Linux Kernel Development Manual for more information on
      kernel types.

      You define the :term:`KTYPE` variable in the
      :ref:`kernel-dev/advanced:bsp descriptions`. The
      value you use must match the value used for the
      :term:`LINUX_KERNEL_TYPE` value used by the
      kernel recipe.

   :term:`LABELS`
      Provides a list of targets for automatic configuration.

      See the :ref:`ref-classes-grub-efi` class for more
      information on how this variable is used.

   :term:`LAYERDEPENDS`
      Lists the layers, separated by spaces, on which this recipe depends.
      Optionally, you can specify a specific layer version for a dependency
      by adding it to the end of the layer name. Here is an example::

         LAYERDEPENDS_mylayer = "anotherlayer (=3)"

      In this previous example,
      version 3 of "anotherlayer" is compared against
      :term:`LAYERVERSION`\ ``_anotherlayer``.

      An error is produced if any dependency is missing or the version
      numbers (if specified) do not match exactly. This variable is used in
      the ``conf/layer.conf`` file and must be suffixed with the name of
      the specific layer (e.g. ``LAYERDEPENDS_mylayer``).

   :term:`LAYERDIR`
      When used inside the ``layer.conf`` configuration file, this variable
      provides the path of the current layer. This variable is not
      available outside of ``layer.conf`` and references are expanded
      immediately when parsing of the file completes.

   :term:`LAYERDIR_RE`
      See :term:`bitbake:LAYERDIR_RE` in the BitBake manual.

   :term:`LAYERRECOMMENDS`
      Lists the layers, separated by spaces, recommended for use with this
      layer.

      Optionally, you can specify a specific layer version for a
      recommendation by adding the version to the end of the layer name.
      Here is an example::

         LAYERRECOMMENDS_mylayer = "anotherlayer (=3)"

      In this previous example, version 3 of "anotherlayer" is compared
      against ``LAYERVERSION_anotherlayer``.

      This variable is used in the ``conf/layer.conf`` file and must be
      suffixed with the name of the specific layer (e.g.
      ``LAYERRECOMMENDS_mylayer``).

   :term:`LAYERSERIES_COMPAT`
      See :term:`bitbake:LAYERSERIES_COMPAT` in the BitBake manual.

   :term:`LAYERVERSION`
      Optionally specifies the version of a layer as a single number. You
      can use this within :term:`LAYERDEPENDS` for
      another layer in order to depend on a specific version of the layer.
      This variable is used in the ``conf/layer.conf`` file and must be
      suffixed with the name of the specific layer (e.g.
      ``LAYERVERSION_mylayer``).

   :term:`LD`
      The minimal command and arguments used to run the linker.

   :term:`LDFLAGS`
      Specifies the flags to pass to the linker. This variable is exported
      to an environment variable and thus made visible to the software
      being built during the compilation step.

      Default initialization for :term:`LDFLAGS` varies depending on what is
      being built:

      -  :term:`TARGET_LDFLAGS` when building for the
         target

      -  :term:`BUILD_LDFLAGS` when building for the
         build host (i.e. ``-native``)

      -  :term:`BUILDSDK_LDFLAGS` when building for
         an SDK (i.e. ``nativesdk-``)

   :term:`LEAD_SONAME`
      Specifies the lead (or primary) compiled library file (i.e. ``.so``)
      that the :ref:`ref-classes-debian` class applies its
      naming policy to given a recipe that packages multiple libraries.

      This variable works in conjunction with the :ref:`ref-classes-debian`
      class.

   :term:`LIC_FILES_CHKSUM`
      Checksums of the license text in the recipe source code.

      This variable tracks changes in license text of the source code
      files. If the license text is changed, it will trigger a build
      failure, which gives the developer an opportunity to review any
      license change.

      This variable must be defined for all recipes (unless
      :term:`LICENSE` is set to "CLOSED").

      For more information, see the ":ref:`dev-manual/licenses:tracking license changes`"
      section in the Yocto Project Development Tasks Manual.

   :term:`LICENSE`
      The list of source licenses for the recipe. Follow these rules:

      -  Do not use spaces within individual license names.

      -  Separate license names using \| (pipe) when there is a choice
         between licenses.

      -  Separate license names using & (ampersand) when there are
         multiple licenses for different parts of the source.

      -  You can use spaces between license names.

      -  For standard licenses, use the names of the files in
         ``meta/files/common-licenses/`` or the
         :term:`SPDXLICENSEMAP` flag names defined in
         ``meta/conf/licenses.conf``.

      Here are some examples::

         LICENSE = "LGPL-2.1-only | GPL-3.0-only"
         LICENSE = "MPL-1.0 & LGPL-2.1-only"
         LICENSE = "GPL-2.0-or-later"

      The first example is from the
      recipes for Qt, which the user may choose to distribute under either
      the LGPL version 2.1 or GPL version 3. The second example is from
      Cairo where two licenses cover different parts of the source code.
      The final example is from ``sysstat``, which presents a single
      license.

      You can also specify licenses on a per-package basis to handle
      situations where components of the output have different licenses.
      For example, a piece of software whose code is licensed under GPLv2
      but has accompanying documentation licensed under the GNU Free
      Documentation License 1.2 could be specified as follows::

         LICENSE = "GFDL-1.2 & GPL-2.0-only"
         LICENSE:${PN} = "GPL-2.0.only"
         LICENSE:${PN}-doc = "GFDL-1.2"

   :term:`LICENSE_CREATE_PACKAGE`
      Setting :term:`LICENSE_CREATE_PACKAGE` to "1" causes the OpenEmbedded
      build system to create an extra package (i.e.
      ``${``\ :term:`PN`\ ``}-lic``) for each recipe and to add
      those packages to the
      :term:`RRECOMMENDS`\ ``:${PN}``.

      The ``${PN}-lic`` package installs a directory in
      ``/usr/share/licenses`` named ``${PN}``, which is the recipe's base
      name, and installs files in that directory that contain license and
      copyright information (i.e. copies of the appropriate license files
      from ``meta/common-licenses`` that match the licenses specified in
      the :term:`LICENSE` variable of the recipe metadata
      and copies of files marked in
      :term:`LIC_FILES_CHKSUM` as containing
      license text).

      For related information on providing license text, see the
      :term:`COPY_LIC_DIRS` variable, the
      :term:`COPY_LIC_MANIFEST` variable, and the
      ":ref:`dev-manual/licenses:providing license text`"
      section in the Yocto Project Development Tasks Manual.

   :term:`LICENSE_FLAGS`
      Specifies additional flags for a recipe you must allow through
      :term:`LICENSE_FLAGS_ACCEPTED` in
      order for the recipe to be built. When providing multiple flags,
      separate them with spaces.

      This value is independent of :term:`LICENSE` and is
      typically used to mark recipes that might require additional licenses
      in order to be used in a commercial product. For more information,
      see the
      ":ref:`dev-manual/licenses:enabling commercially licensed recipes`"
      section in the Yocto Project Development Tasks Manual.

   :term:`LICENSE_FLAGS_ACCEPTED`
      Lists license flags that when specified in
      :term:`LICENSE_FLAGS` within a recipe should not
      prevent that recipe from being built.  For more information, see the
      ":ref:`dev-manual/licenses:enabling commercially licensed recipes`"
      section in the Yocto Project Development Tasks Manual.

   :term:`LICENSE_FLAGS_DETAILS`
      Adds details about a flag in :term:`LICENSE_FLAGS`. This way,
      if such a flag is not accepted through :term:`LICENSE_FLAGS_ACCEPTED`,
      the error message will be more informative, containing the specified
      extra details.

      For example, a recipe with an EULA may set::

         LICENSE_FLAGS = "FooBar-EULA"
         LICENSE_FLAGS_DETAILS[FooBar-EULA] = "For further details, see https://example.com/eula."

      If ``Foobar-EULA`` isn't in :term:`LICENSE_FLAGS_ACCEPTED`, the
      error message is more useful::

        Has a restricted license 'FooBar-EULA' which is not listed in your LICENSE_FLAGS_ACCEPTED.
        For further details, see https://example.com/eula.

   :term:`LICENSE_PATH`
      Path to additional licenses used during the build. By default, the
      OpenEmbedded build system uses :term:`COMMON_LICENSE_DIR` to define the
      directory that holds common license text used during the build. The
      :term:`LICENSE_PATH` variable allows you to extend that location to other
      areas that have additional licenses::

         LICENSE_PATH += "path-to-additional-common-licenses"

   :term:`LINUX_KERNEL_TYPE`
      Defines the kernel type to be used in assembling the configuration.
      The linux-yocto recipes define "standard", "tiny", and "preempt-rt"
      kernel types. See the ":ref:`kernel-dev/advanced:kernel types`"
      section in the
      Yocto Project Linux Kernel Development Manual for more information on
      kernel types.

      If you do not specify a :term:`LINUX_KERNEL_TYPE`, it defaults to
      "standard". Together with :term:`KMACHINE`, the
      :term:`LINUX_KERNEL_TYPE` variable defines the search arguments used by
      the kernel tools to find the appropriate description within the
      kernel :term:`Metadata` with which to build out the sources
      and configuration.

   :term:`LINUX_VERSION`
      The Linux version from ``kernel.org`` on which the Linux kernel image
      being built using the OpenEmbedded build system is based. You define
      this variable in the kernel recipe. For example, the
      ``linux-yocto-3.4.bb`` kernel recipe found in
      ``meta/recipes-kernel/linux`` defines the variables as follows::

         LINUX_VERSION ?= "3.4.24"

      The :term:`LINUX_VERSION` variable is used to define :term:`PV`
      for the recipe::

         PV = "${LINUX_VERSION}+git${SRCPV}"

   :term:`LINUX_VERSION_EXTENSION`
      A string extension compiled into the version string of the Linux
      kernel built with the OpenEmbedded build system. You define this
      variable in the kernel recipe. For example, the linux-yocto kernel
      recipes all define the variable as follows::

         LINUX_VERSION_EXTENSION ?= "-yocto-${LINUX_KERNEL_TYPE}"

      Defining this variable essentially sets the Linux kernel
      configuration item ``CONFIG_LOCALVERSION``, which is visible through
      the ``uname`` command. Here is an example that shows the extension
      assuming it was set as previously shown::

         $ uname -r
         3.7.0-rc8-custom

   :term:`LOG_DIR`
      Specifies the directory to which the OpenEmbedded build system writes
      overall log files. The default directory is ``${TMPDIR}/log``.

      For the directory containing logs specific to each task, see the
      :term:`T` variable.

   :term:`MACHINE`
      Specifies the target device for which the image is built. You define
      :term:`MACHINE` in the ``local.conf`` file found in the
      :term:`Build Directory`. By default, :term:`MACHINE` is set to
      "qemux86", which is an x86-based architecture machine to be emulated
      using QEMU::

         MACHINE ?= "qemux86"

      The variable corresponds to a machine configuration file of the same
      name, through which machine-specific configurations are set. Thus,
      when :term:`MACHINE` is set to "qemux86", the corresponding
      ``qemux86.conf`` machine configuration file can be found in
      the :term:`Source Directory` in
      ``meta/conf/machine``.

      The list of machines supported by the Yocto Project as shipped
      include the following::

         MACHINE ?= "qemuarm"
         MACHINE ?= "qemuarm64"
         MACHINE ?= "qemumips"
         MACHINE ?= "qemumips64"
         MACHINE ?= "qemuppc"
         MACHINE ?= "qemux86"
         MACHINE ?= "qemux86-64"
         MACHINE ?= "genericx86"
         MACHINE ?= "genericx86-64"
         MACHINE ?= "beaglebone"

      The last five are Yocto Project reference hardware
      boards, which are provided in the ``meta-yocto-bsp`` layer.

      .. note::

         Adding additional Board Support Package (BSP) layers to your
         configuration adds new possible settings for :term:`MACHINE`.

   :term:`MACHINE_ARCH`
      Specifies the name of the machine-specific architecture. This
      variable is set automatically from :term:`MACHINE` or
      :term:`TUNE_PKGARCH`. You should not hand-edit
      the :term:`MACHINE_ARCH` variable.

   :term:`MACHINE_ESSENTIAL_EXTRA_RDEPENDS`
      A list of required machine-specific packages to install as part of
      the image being built. The build process depends on these packages
      being present. Furthermore, because this is a "machine-essential"
      variable, the list of packages are essential for the machine to boot.
      The impact of this variable affects images based on
      ``packagegroup-core-boot``, including the ``core-image-minimal``
      image.

      This variable is similar to the
      :term:`MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS` variable with the exception
      that the image being built has a build dependency on the variable's
      list of packages. In other words, the image will not build if a file
      in this list is not found.

      As an example, suppose the machine for which you are building
      requires ``example-init`` to be run during boot to initialize the
      hardware. In this case, you would use the following in the machine's
      ``.conf`` configuration file::

         MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "example-init"

   :term:`MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS`
      A list of recommended machine-specific packages to install as part of
      the image being built. The build process does not depend on these
      packages being present. However, because this is a
      "machine-essential" variable, the list of packages are essential for
      the machine to boot. The impact of this variable affects images based
      on ``packagegroup-core-boot``, including the ``core-image-minimal``
      image.

      This variable is similar to the :term:`MACHINE_ESSENTIAL_EXTRA_RDEPENDS`
      variable with the exception that the image being built does not have
      a build dependency on the variable's list of packages. In other
      words, the image will still build if a package in this list is not
      found. Typically, this variable is used to handle essential kernel
      modules, whose functionality may be selected to be built into the
      kernel rather than as a module, in which case a package will not be
      produced.

      Consider an example where you have a custom kernel where a specific
      touchscreen driver is required for the machine to be usable. However,
      the driver can be built as a module or into the kernel depending on
      the kernel configuration. If the driver is built as a module, you
      want it to be installed. But, when the driver is built into the
      kernel, you still want the build to succeed. This variable sets up a
      "recommends" relationship so that in the latter case, the build will
      not fail due to the missing package. To accomplish this, assuming the
      package for the module was called ``kernel-module-ab123``, you would
      use the following in the machine's ``.conf`` configuration file::

         MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "kernel-module-ab123"

      .. note::

         In this example, the ``kernel-module-ab123`` recipe needs to
         explicitly set its :term:`PACKAGES` variable to ensure that BitBake
         does not use the kernel recipe's :term:`PACKAGES_DYNAMIC` variable to
         satisfy the dependency.

      Some examples of these machine essentials are flash, screen,
      keyboard, mouse, or touchscreen drivers (depending on the machine).

   :term:`MACHINE_EXTRA_RDEPENDS`
      A list of machine-specific packages to install as part of the image
      being built that are not essential for the machine to boot. However,
      the build process for more fully-featured images depends on the
      packages being present.

      This variable affects all images based on ``packagegroup-base``,
      which does not include the ``core-image-minimal`` or
      ``core-image-full-cmdline`` images.

      The variable is similar to the :term:`MACHINE_EXTRA_RRECOMMENDS` variable
      with the exception that the image being built has a build dependency
      on the variable's list of packages. In other words, the image will
      not build if a file in this list is not found.

      An example is a machine that has WiFi capability but is not essential
      for the machine to boot the image. However, if you are building a
      more fully-featured image, you want to enable the WiFi. The package
      containing the firmware for the WiFi hardware is always expected to
      exist, so it is acceptable for the build process to depend upon
      finding the package. In this case, assuming the package for the
      firmware was called ``wifidriver-firmware``, you would use the
      following in the ``.conf`` file for the machine::

         MACHINE_EXTRA_RDEPENDS += "wifidriver-firmware"

   :term:`MACHINE_EXTRA_RRECOMMENDS`
      A list of machine-specific packages to install as part of the image
      being built that are not essential for booting the machine. The image
      being built has no build dependency on this list of packages.

      This variable affects only images based on ``packagegroup-base``,
      which does not include the ``core-image-minimal`` or
      ``core-image-full-cmdline`` images.

      This variable is similar to the :term:`MACHINE_EXTRA_RDEPENDS` variable
      with the exception that the image being built does not have a build
      dependency on the variable's list of packages. In other words, the
      image will build if a file in this list is not found.

      An example is a machine that has WiFi capability but is not essential
      For the machine to boot the image. However, if you are building a
      more fully-featured image, you want to enable WiFi. In this case, the
      package containing the WiFi kernel module will not be produced if the
      WiFi driver is built into the kernel, in which case you still want
      the build to succeed instead of failing as a result of the package
      not being found. To accomplish this, assuming the package for the
      module was called ``kernel-module-examplewifi``, you would use the
      following in the ``.conf`` file for the machine::

         MACHINE_EXTRA_RRECOMMENDS += "kernel-module-examplewifi"

   :term:`MACHINE_FEATURES`
      Specifies the list of hardware features the
      :term:`MACHINE` is capable of supporting. For related
      information on enabling features, see the
      :term:`DISTRO_FEATURES`,
      :term:`COMBINED_FEATURES`, and
      :term:`IMAGE_FEATURES` variables.

      For a list of hardware features supported by the Yocto Project as
      shipped, see the ":ref:`ref-features-machine`" section.

   :term:`MACHINE_FEATURES_BACKFILL`
      A list of space-separated features to be added to
      :term:`MACHINE_FEATURES` if not also present in
      :term:`MACHINE_FEATURES_BACKFILL_CONSIDERED`.

      This variable is set in the ``meta/conf/bitbake.conf`` file. It is not
      intended to be user-configurable. It is best to just reference the
      variable to see which machine features are being
      :ref:`backfilled <ref-features-backfill>` for all machine configurations.

   :term:`MACHINE_FEATURES_BACKFILL_CONSIDERED`
      A list of space-separated features from :term:`MACHINE_FEATURES_BACKFILL`
      that should not be :ref:`backfilled <ref-features-backfill>` (i.e. added
      to :term:`MACHINE_FEATURES`) during the build.

      This corresponds to an opt-out mechanism. When new default machine
      features are introduced, machine definition maintainers can review
      (`consider`) them and decide to exclude them from the
      :ref:`backfilled <ref-features-backfill>` features. Therefore, the
      combination of :term:`MACHINE_FEATURES_BACKFILL` and
      :term:`MACHINE_FEATURES_BACKFILL_CONSIDERED` makes it possible to
      add new default features without breaking existing machine definitions.

   :term:`MACHINEOVERRIDES`
      A colon-separated list of overrides that apply to the current
      machine. By default, this list includes the value of
      :term:`MACHINE`.

      You can extend :term:`MACHINEOVERRIDES` to add extra overrides that
      should apply to a machine. For example, all machines emulated in QEMU
      (e.g. ``qemuarm``, ``qemux86``, and so forth) include a file named
      ``meta/conf/machine/include/qemu.inc`` that prepends the following
      override to :term:`MACHINEOVERRIDES`::

         MACHINEOVERRIDES =. "qemuall:"

      This
      override allows variables to be overridden for all machines emulated
      in QEMU, like in the following example from the ``connman-conf``
      recipe::

         SRC_URI:append:qemuall = " file://wired.config \
             file://wired-setup \
             "

      The underlying mechanism behind
      :term:`MACHINEOVERRIDES` is simply that it is included in the default
      value of :term:`OVERRIDES`.

   :term:`MAINTAINER`
      The email address of the distribution maintainer.

   :term:`MESON_BUILDTYPE`
      Value of the Meson ``--buildtype`` argument used by the
      :ref:`ref-classes-meson` class. It defaults to ``debug`` if
      :term:`DEBUG_BUILD` is set to "1", and ``plain`` otherwise.

      See `Meson build options <https://mesonbuild.com/Builtin-options.html>`__
      for the values you could set in a recipe. Values such as ``plain``,
      ``debug``, ``debugoptimized``, ``release`` and ``minsize`` allow
      you to specify the inclusion of debugging symbols and the compiler
      optimizations (none, performance or size).

   :term:`MESON_TARGET`
      A variable for the :ref:`ref-classes-meson` class, allowing to choose
      a Meson target to build in :ref:`ref-tasks-compile`.  Otherwise, the
      default targets are built.

   :term:`METADATA_BRANCH`
      The branch currently checked out for the OpenEmbedded-Core layer (path
      determined by :term:`COREBASE`).

   :term:`METADATA_REVISION`
      The revision currently checked out for the OpenEmbedded-Core layer (path
      determined by :term:`COREBASE`).

   :term:`MIME_XDG_PACKAGES`
      The current implementation of the :ref:`ref-classes-mime-xdg`
      class cannot detect ``.desktop`` files installed through absolute
      symbolic links. Use this setting to make the class create post-install
      and post-remove scripts for these packages anyway, to invoke the
      ``update-destop-database`` command.

   :term:`MIRRORS`
      Specifies additional paths from which the OpenEmbedded build system
      gets source code. When the build system searches for source code, it
      first tries the local download directory. If that location fails, the
      build system tries locations defined by
      :term:`PREMIRRORS`, the upstream source, and then
      locations specified by :term:`MIRRORS` in that order.

      The default value for :term:`MIRRORS` is defined in the
      ``meta/classes-global/mirrors.bbclass`` file in the core metadata layer.

   :term:`MLPREFIX`
      Specifies a prefix has been added to :term:`PN` to create a
      special version of a recipe or package (i.e. a Multilib version). The
      variable is used in places where the prefix needs to be added to or
      removed from a name (e.g. the :term:`BPN` variable).
      :term:`MLPREFIX` gets set when a prefix has been added to :term:`PN`.

      .. note::

         The "ML" in :term:`MLPREFIX` stands for "MultiLib". This representation
         is historical and comes from a time when ":ref:`ref-classes-nativesdk`"
         was a suffix rather than a prefix on the recipe name. When
         ":ref:`ref-classes-nativesdk`" was turned into a prefix, it made sense
         to set :term:`MLPREFIX` for it as well.

      To help understand when :term:`MLPREFIX` might be needed, consider when
      :term:`BBCLASSEXTEND` is used to provide a :ref:`ref-classes-nativesdk`
      version of a recipe in addition to the target version. If that recipe
      declares build-time dependencies on tasks in other recipes by using
      :term:`DEPENDS`, then a dependency on "foo" will automatically get
      rewritten to a dependency on "nativesdk-foo". However, dependencies like
      the following will not get rewritten automatically::

         do_foo[depends] += "recipe:do_foo"

      If you want such a dependency to also get transformed, you can do the
      following::

         do_foo[depends] += "${MLPREFIX}recipe:do_foo"

   :term:`module_autoload`
      This variable has been replaced by the :term:`KERNEL_MODULE_AUTOLOAD`
      variable. You should replace all occurrences of :term:`module_autoload`
      with additions to :term:`KERNEL_MODULE_AUTOLOAD`, for example::

         module_autoload_rfcomm = "rfcomm"

      should now be replaced with::

         KERNEL_MODULE_AUTOLOAD += "rfcomm"

      See the :term:`KERNEL_MODULE_AUTOLOAD` variable for more information.

   :term:`module_conf`
      Specifies `modprobe.d <https://linux.die.net/man/5/modprobe.d>`__
      syntax lines for inclusion in the ``/etc/modprobe.d/modname.conf``
      file.

      You can use this variable anywhere that it can be recognized by the
      kernel recipe or out-of-tree kernel module recipe (e.g. a machine
      configuration file, a distribution configuration file, an append file
      for the recipe, or the recipe itself). If you use this variable, you
      must also be sure to list the module name in the
      :term:`KERNEL_MODULE_PROBECONF`
      variable.

      Here is the general syntax::

         module_conf_module_name = "modprobe.d-syntax"

      You must use the kernel module name override.

      Run ``man modprobe.d`` in the shell to find out more information on
      the exact syntax you want to provide with :term:`module_conf`.

      Including :term:`module_conf` causes the OpenEmbedded build system to
      populate the ``/etc/modprobe.d/modname.conf`` file with
      ``modprobe.d`` syntax lines. Here is an example that adds the options
      ``arg1`` and ``arg2`` to a module named ``mymodule``::

         module_conf_mymodule = "options mymodule arg1=val1 arg2=val2"

      For information on how to specify kernel modules to auto-load on
      boot, see the :term:`KERNEL_MODULE_AUTOLOAD` variable.

   :term:`MODULE_TARBALL_DEPLOY`
      Controls creation of the ``modules-*.tgz`` file. Set this variable to
      "0" to disable creation of this file, which contains all of the
      kernel modules resulting from a kernel build.

   :term:`MODULE_TARBALL_LINK_NAME`
      The link name of the kernel module tarball. This variable is set in
      the ``meta/classes-recipe/kernel-artifact-names.bbclass`` file as follows::

         MODULE_TARBALL_LINK_NAME ?= "${KERNEL_ARTIFACT_LINK_NAME}"

      The value
      of the ``KERNEL_ARTIFACT_LINK_NAME`` variable, which is set in the
      same file, has the following value::

         KERNEL_ARTIFACT_LINK_NAME ?= "${MACHINE}"

      See the :term:`MACHINE` variable for additional information.

   :term:`MODULE_TARBALL_NAME`
      The base name of the kernel module tarball. This variable is set in
      the ``meta/classes-recipe/kernel-artifact-names.bbclass`` file as follows::

         MODULE_TARBALL_NAME ?= "${KERNEL_ARTIFACT_NAME}"

      See :term:`KERNEL_ARTIFACT_NAME` for additional information.

   :term:`MOUNT_BASE`
      On non-systemd systems (where ``udev-extraconf`` is being used),
      specifies the base directory for auto-mounting filesystems. The
      default value is "/run/media".

   :term:`MULTIMACH_TARGET_SYS`
      Uniquely identifies the type of the target system for which packages
      are being built. This variable allows output for different types of
      target systems to be put into different subdirectories of the same
      output directory.

      The default value of this variable is::

         ${PACKAGE_ARCH}${TARGET_VENDOR}-${TARGET_OS}

      Some classes (e.g.  :ref:`ref-classes-cross-canadian`) modify the
      :term:`MULTIMACH_TARGET_SYS` value.

      See the :term:`STAMP` variable for an example. See the
      :term:`STAGING_DIR_TARGET` variable for more information.

   :term:`NATIVELSBSTRING`
      A string identifying the host distribution. Strings consist of the
      host distributor ID followed by the release, as reported by the
      ``lsb_release`` tool or as read from ``/etc/lsb-release``. For
      example, when running a build on Ubuntu 12.10, the value is
      "Ubuntu-12.10". If this information is unable to be determined, the
      value resolves to "Unknown".

      This variable is used by default to isolate native shared state
      packages for different distributions (e.g. to avoid problems with
      ``glibc`` version incompatibilities). Additionally, the variable is
      checked against
      :term:`SANITY_TESTED_DISTROS` if that
      variable is set.

   :term:`NM`
      The minimal command and arguments to run ``nm``.

   :term:`NO_GENERIC_LICENSE`
      Avoids QA errors when you use a non-common, non-CLOSED license in a
      recipe. There are packages, such as the linux-firmware package, with many
      licenses that are not in any way common. Also, new licenses are added
      occasionally to avoid introducing a lot of common license files,
      which are only applicable to a specific package.
      :term:`NO_GENERIC_LICENSE` is used to allow copying a license that does
      not exist in common licenses.

      The following example shows how to add :term:`NO_GENERIC_LICENSE` to a
      recipe::

         NO_GENERIC_LICENSE[license_name] = "license_file_in_fetched_source"

      Here is an example that
      uses the ``LICENSE.Abilis.txt`` file as the license from the fetched
      source::

         NO_GENERIC_LICENSE[Firmware-Abilis] = "LICENSE.Abilis.txt"

   :term:`NO_RECOMMENDATIONS`
      Prevents installation of all "recommended-only" packages.
      Recommended-only packages are packages installed only through the
      :term:`RRECOMMENDS` variable). Setting the
      :term:`NO_RECOMMENDATIONS` variable to "1" turns this feature on::

         NO_RECOMMENDATIONS = "1"

      You can set this variable globally in your ``local.conf`` file or you
      can attach it to a specific image recipe by using the recipe name
      override::

         NO_RECOMMENDATIONS:pn-target_image = "1"

      It is important to realize that if you choose to not install packages
      using this variable and some other packages are dependent on them
      (i.e. listed in a recipe's :term:`RDEPENDS`
      variable), the OpenEmbedded build system ignores your request and
      will install the packages to avoid dependency errors.

      .. note::

         Some recommended packages might be required for certain system
         functionality, such as kernel modules. It is up to you to add
         packages with the :term:`IMAGE_INSTALL` variable.

      This variable is only supported when using the IPK and RPM
      packaging backends. DEB is not supported.

      See the :term:`BAD_RECOMMENDATIONS` and
      the :term:`PACKAGE_EXCLUDE` variables for
      related information.

   :term:`NOAUTOPACKAGEDEBUG`
      Disables auto package from splitting ``.debug`` files. If a recipe
      requires ``FILES:${PN}-dbg`` to be set manually, the
      :term:`NOAUTOPACKAGEDEBUG` can be defined allowing you to define the
      content of the debug package. For example::

         NOAUTOPACKAGEDEBUG = "1"
         FILES:${PN}-dev = "${includedir}/${QT_DIR_NAME}/Qt/*"
         FILES:${PN}-dbg = "/usr/src/debug/"
         FILES:${QT_BASE_NAME}-demos-doc = "${docdir}/${QT_DIR_NAME}/qch/qt.qch"

   :term:`NON_MULTILIB_RECIPES`
      A list of recipes that should not be built for multilib. OE-Core's
      ``multilib.conf`` file defines a reasonable starting point for this
      list with::

         NON_MULTILIB_RECIPES = "grub grub-efi make-mod-scripts ovmf u-boot"

   :term:`OBJCOPY`
      The minimal command and arguments to run ``objcopy``.

   :term:`OBJDUMP`
      The minimal command and arguments to run ``objdump``.

   :term:`OE_BINCONFIG_EXTRA_MANGLE`
      When inheriting the :ref:`ref-classes-binconfig` class,
      this variable specifies additional arguments passed to the "sed"
      command. The sed command alters any paths in configuration scripts
      that have been set up during compilation. Inheriting this class
      results in all paths in these scripts being changed to point into the
      ``sysroots/`` directory so that all builds that use the script will
      use the correct directories for the cross compiling layout.

      See the ``meta/classes-recipe/binconfig.bbclass`` in the
      :term:`Source Directory` for details on how this class
      applies these additional sed command arguments.

   :term:`OECMAKE_GENERATOR`
      A variable for the :ref:`ref-classes-cmake` class, allowing to choose
      which back-end will be generated by CMake to build an application.

      By default, this variable is set to ``Ninja``, which is faster than GNU
      make, but if building is broken with Ninja, a recipe can use this
      variable to use GNU make instead::

         OECMAKE_GENERATOR = "Unix Makefiles"

   :term:`OE_IMPORTS`
      An internal variable used to tell the OpenEmbedded build system what
      Python modules to import for every Python function run by the system.

      .. note::

         Do not set this variable. It is for internal use only.

   :term:`OE_INIT_ENV_SCRIPT`
      The name of the build environment setup script for the purposes of
      setting up the environment within the extensible SDK. The default
      value is "oe-init-build-env".

      If you use a custom script to set up your build environment, set the
      :term:`OE_INIT_ENV_SCRIPT` variable to its name.

   :term:`OE_TERMINAL`
      Controls how the OpenEmbedded build system spawns interactive
      terminals on the host development system (e.g. using the BitBake
      command with the ``-c devshell`` command-line option). For more
      information, see the ":ref:`dev-manual/development-shell:using a development shell`" section in
      the Yocto Project Development Tasks Manual.

      You can use the following values for the :term:`OE_TERMINAL` variable:

      - auto
      - gnome
      - xfce
      - rxvt
      - screen
      - konsole
      - none

   :term:`OEROOT`
      The directory from which the top-level build environment setup script
      is sourced. The Yocto Project provides a top-level build environment
      setup script: :ref:`structure-core-script`. When you run this
      script, the :term:`OEROOT` variable resolves to the directory that
      contains the script.

      For additional information on how this variable is used, see the
      initialization script.

   :term:`OEQA_REPRODUCIBLE_TEST_PACKAGE`
      Set the package manager(s) for build reproducibility testing.
      See :yocto_git:`reproducible.py </poky/tree/meta/lib/oeqa/selftest/cases/reproducible.py>`
      and :doc:`/test-manual/reproducible-builds`.

   :term:`OEQA_REPRODUCIBLE_TEST_TARGET`
      Set build target for build reproducibility testing. By default
      all available recipes are compiled with "bitbake world", see also :term:`EXCLUDE_FROM_WORLD`
      and :doc:`/test-manual/reproducible-builds`.

   :term:`OEQA_REPRODUCIBLE_TEST_SSTATE_TARGETS`
      Set build targets which can be rebuilt using :ref:`shared state <overview-manual/concepts:shared state cache>`
      when running build reproducibility tests. See :doc:`/test-manual/reproducible-builds`.

   :term:`OLDEST_KERNEL`
      Declares the oldest version of the Linux kernel that the produced
      binaries must support. This variable is passed into the build of the
      Embedded GNU C Library (``glibc``).

      The default for this variable comes from the
      ``meta/conf/bitbake.conf`` configuration file. You can override this
      default by setting the variable in a custom distribution
      configuration file.

   :term:`OPKG_MAKE_INDEX_EXTRA_PARAMS`
      Specifies extra parameters for the ``opkg-make-index`` command.

   :term:`OVERLAYFS_ETC_DEVICE`
      When the :ref:`ref-classes-overlayfs-etc` class is
      inherited, specifies the device to be mounted for the read/write
      layer of ``/etc``. There is no default, so you must set this if you
      wish to enable :ref:`ref-classes-overlayfs-etc`, for
      example, assuming ``/dev/mmcblk0p2`` was the desired device::

         OVERLAYFS_ETC_DEVICE = "/dev/mmcblk0p2"

   :term:`OVERLAYFS_ETC_EXPOSE_LOWER`
      When the :ref:`ref-classes-overlayfs-etc` class is
      inherited, if set to "1" then a read-only access to the original
      ``/etc`` content will be provided as a ``lower/`` subdirectory of
      :term:`OVERLAYFS_ETC_MOUNT_POINT`. The default value is "0".

   :term:`OVERLAYFS_ETC_FSTYPE`
      When the :ref:`ref-classes-overlayfs-etc` class is
      inherited, specifies the file system type for the read/write
      layer of ``/etc``. There is no default, so you must set this if you
      wish to enable :ref:`ref-classes-overlayfs-etc`,
      for example, assuming the file system is ext4::

         OVERLAYFS_ETC_FSTYPE = "ext4"

   :term:`OVERLAYFS_ETC_MOUNT_OPTIONS`
      When the :ref:`ref-classes-overlayfs-etc` class is
      inherited, specifies the mount options for the read-write layer.
      The default value is "defaults".

   :term:`OVERLAYFS_ETC_MOUNT_POINT`
      When the :ref:`ref-classes-overlayfs-etc` class is
      inherited, specifies the parent mount path for the filesystem layers.
      There is no default, so you must set this if you wish to enable
      :ref:`ref-classes-overlayfs-etc`, for example if the desired path is
      "/data"::

         OVERLAYFS_ETC_MOUNT_POINT = "/data"

   :term:`OVERLAYFS_ETC_USE_ORIG_INIT_NAME`
      When the :ref:`ref-classes-overlayfs-etc` class is inherited, controls
      how the generated init will be named. For more information, see the
      :ref:`ref-classes-overlayfs-etc` class documentation. The default value
      is "1".

   :term:`OVERLAYFS_MOUNT_POINT`
      When inheriting the :ref:`ref-classes-overlayfs` class,
      specifies mount point(s) to be used. For example::

         OVERLAYFS_MOUNT_POINT[data] = "/data"

      The assumes you have a ``data.mount`` systemd unit defined elsewhere in
      your BSP (e.g. in ``systemd-machine-units`` recipe) and it is installed
      into the image. For more information see :ref:`ref-classes-overlayfs`.

      .. note::

         Although the :ref:`ref-classes-overlayfs` class is
         inherited by individual recipes, :term:`OVERLAYFS_MOUNT_POINT`
         should be set in your machine configuration.

   :term:`OVERLAYFS_QA_SKIP`
      When inheriting the :ref:`ref-classes-overlayfs` class,
      provides the ability to disable QA checks for particular overlayfs
      mounts. For example::

         OVERLAYFS_QA_SKIP[data] = "mount-configured"

      .. note::

         Although the :ref:`ref-classes-overlayfs` class is
         inherited by individual recipes, :term:`OVERLAYFS_QA_SKIP`
         should be set in your machine configuration.

   :term:`OVERLAYFS_WRITABLE_PATHS`
      When inheriting the :ref:`ref-classes-overlayfs` class,
      specifies writable paths used at runtime for the recipe. For
      example::

         OVERLAYFS_WRITABLE_PATHS[data] = "/usr/share/my-custom-application"

   :term:`OVERRIDES`
      A colon-separated list of overrides that currently apply. Overrides
      are a BitBake mechanism that allows variables to be selectively
      overridden at the end of parsing. The set of overrides in
      :term:`OVERRIDES` represents the "state" during building, which includes
      the current recipe being built, the machine for which it is being
      built, and so forth.

      As an example, if the string "an-override" appears as an element in
      the colon-separated list in :term:`OVERRIDES`, then the following
      assignment will override ``FOO`` with the value "overridden" at the
      end of parsing::

         FOO:an-override = "overridden"

      See the
      ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:conditional syntax (overrides)`"
      section in the BitBake User Manual for more information on the
      overrides mechanism.

      The default value of :term:`OVERRIDES` includes the values of the
      :term:`CLASSOVERRIDE`,
      :term:`MACHINEOVERRIDES`, and
      :term:`DISTROOVERRIDES` variables. Another
      important override included by default is ``pn-${PN}``. This override
      allows variables to be set for a single recipe within configuration
      (``.conf``) files. Here is an example::

         FOO:pn-myrecipe = "myrecipe-specific value"

      .. note::

         An easy way to see what overrides apply is to search for :term:`OVERRIDES`
         in the output of the ``bitbake -e`` command. See the
         ":ref:`dev-manual/debugging:viewing variable values`" section in the Yocto
         Project Development Tasks Manual for more information.

   :term:`P`
      The recipe name and version. :term:`P` is comprised of the following::

         ${PN}-${PV}

   :term:`P4DIR`
      See :term:`bitbake:P4DIR` in the BitBake manual.

   :term:`PACKAGE_ADD_METADATA`
      This variable defines additional metadata to add to packages.

      You may find you need to inject additional metadata into packages.
      This variable allows you to do that by setting the injected data as
      the value. Multiple fields can be added by splitting the content with
      the literal separator "\n".

      The suffixes '_IPK', '_DEB', or '_RPM' can be applied to the variable
      to do package type specific settings. It can also be made package
      specific by using the package name as a suffix.

      You can find out more about applying this variable in the
      ":ref:`dev-manual/packages:adding custom metadata to packages`"
      section in the Yocto Project Development Tasks Manual.

   :term:`PACKAGE_ARCH`
      The architecture of the resulting package or packages.

      By default, the value of this variable is set to
      :term:`TUNE_PKGARCH` when building for the
      target, :term:`BUILD_ARCH` when building for the
      build host, and "${SDK_ARCH}-${SDKPKGSUFFIX}" when building for the
      SDK.

      .. note::

         See :term:`SDK_ARCH` for more information.

      However, if your recipe's output packages are built specific to the
      target machine rather than generally for the architecture of the
      machine, you should set :term:`PACKAGE_ARCH` to the value of
      :term:`MACHINE_ARCH` in the recipe as follows::

         PACKAGE_ARCH = "${MACHINE_ARCH}"

   :term:`PACKAGE_ARCHS`
      Specifies a list of architectures compatible with the target machine.
      This variable is set automatically and should not normally be
      hand-edited. Entries are separated using spaces and listed in order
      of priority. The default value for :term:`PACKAGE_ARCHS` is "all any
      noarch ${PACKAGE_EXTRA_ARCHS} ${MACHINE_ARCH}".

   :term:`PACKAGE_BEFORE_PN`
      Enables easily adding packages to :term:`PACKAGES` before ``${PN}`` so
      that those added packages can pick up files that would normally be
      included in the default package.

   :term:`PACKAGE_CLASSES`
      This variable, which is set in the ``local.conf`` configuration file
      found in the ``conf`` folder of the
      :term:`Build Directory`, specifies the package manager the
      OpenEmbedded build system uses when packaging data.

      You can provide one or more of the following arguments for the
      variable::

         PACKAGE_CLASSES ?= "package_rpm package_deb package_ipk"

      The build system uses only the first argument in the list as the
      package manager when creating your image or SDK. However, packages
      will be created using any additional packaging classes you specify.
      For example, if you use the following in your ``local.conf`` file::

         PACKAGE_CLASSES ?= "package_ipk"

      The OpenEmbedded build system uses
      the IPK package manager to create your image or SDK.

      For information on packaging and build performance effects as a
      result of the package manager in use, see the
      ":ref:`ref-classes-package`" section.

   :term:`PACKAGE_DEBUG_SPLIT_STYLE`
      Determines how to split up and package debug and source information
      when creating debugging packages to be used with the GNU Project
      Debugger (GDB). In general, based on the value of this variable,
      you can combine the source and debug info in a single package,
      you can break out the source into a separate package that can be
      installed independently, or you can choose to not have the source
      packaged at all.

      The possible values of :term:`PACKAGE_DEBUG_SPLIT_STYLE` variable:

      -  "``.debug``": All debugging and source info is placed in a single
         ``*-dbg`` package; debug symbol files are placed next to the
         binary in a ``.debug`` directory so that, if a binary is installed
         into ``/bin``, the corresponding debug symbol file is installed
         in ``/bin/.debug``. Source files are installed in the same ``*-dbg``
         package under ``/usr/src/debug``.

      -  "``debug-file-directory``": As above, all debugging and source info
         is placed in a single ``*-dbg`` package; debug symbol files are
         placed entirely under the directory ``/usr/lib/debug`` and separated
         by the path from where the binary is installed, so that if a binary
         is installed in ``/bin``, the corresponding debug symbols are installed
         in ``/usr/lib/debug/bin``, and so on. As above, source is installed
         in the same package under ``/usr/src/debug``.

      -  "``debug-with-srcpkg``": Debugging info is placed in the standard
         ``*-dbg`` package as with the ``.debug`` value, while source is
         placed in a separate ``*-src`` package, which can be installed
         independently.  This is the default setting for this variable,
         as defined in Poky's ``bitbake.conf`` file.

      -  "``debug-without-src``": The same behavior as with the ``.debug``
         setting, but no source is packaged at all.

      .. note::

         Much of the above package splitting can be overridden via
         use of the :term:`INHIBIT_PACKAGE_DEBUG_SPLIT` variable.

      You can find out more about debugging using GDB by reading the
      ":ref:`dev-manual/debugging:debugging with the gnu project debugger (gdb) remotely`" section
      in the Yocto Project Development Tasks Manual.

   :term:`PACKAGE_EXCLUDE`
      Lists packages that should not be installed into an image. For
      example::

         PACKAGE_EXCLUDE = "package_name package_name package_name ..."

      You can set this variable globally in your ``local.conf`` file or you
      can attach it to a specific image recipe by using the recipe name
      override::

         PACKAGE_EXCLUDE:pn-target_image = "package_name"

      If you choose to not install a package using this variable and some
      other package is dependent on it (i.e. listed in a recipe's
      :term:`RDEPENDS` variable), the OpenEmbedded build
      system generates a fatal installation error. Because the build system
      halts the process with a fatal error, you can use the variable with
      an iterative development process to remove specific components from a
      system.

      This variable is supported only when using the IPK and RPM
      packaging backends. DEB is not supported.

      See the :term:`NO_RECOMMENDATIONS` and the
      :term:`BAD_RECOMMENDATIONS` variables for
      related information.

   :term:`PACKAGE_EXCLUDE_COMPLEMENTARY`
      Prevents specific packages from being installed when you are
      installing complementary packages.

      You might find that you want to prevent installing certain packages
      when you are installing complementary packages. For example, if you
      are using :term:`IMAGE_FEATURES` to install
      ``dev-pkgs``, you might not want to install all packages from a
      particular multilib. If you find yourself in this situation, you can
      use the :term:`PACKAGE_EXCLUDE_COMPLEMENTARY` variable to specify regular
      expressions to match the packages you want to exclude.

   :term:`PACKAGE_EXTRA_ARCHS`
      Specifies the list of architectures compatible with the device CPU.
      This variable is useful when you build for several different devices
      that use miscellaneous processors such as XScale and ARM926-EJS.

   :term:`PACKAGE_FEED_ARCHS`
      Optionally specifies the package architectures used as part of the
      package feed URIs during the build. When used, the
      :term:`PACKAGE_FEED_ARCHS` variable is appended to the final package feed
      URI, which is constructed using the
      :term:`PACKAGE_FEED_URIS` and
      :term:`PACKAGE_FEED_BASE_PATHS`
      variables.

      .. note::

         You can use the :term:`PACKAGE_FEED_ARCHS`
         variable to allow specific package architectures. If you do
         not need to allow specific architectures, which is a common
         case, you can omit this variable. Omitting the variable results in
         all available architectures for the current machine being included
         into remote package feeds.

      Consider the following example where the :term:`PACKAGE_FEED_URIS`,
      :term:`PACKAGE_FEED_BASE_PATHS`, and :term:`PACKAGE_FEED_ARCHS` variables are
      defined in your ``local.conf`` file::

         PACKAGE_FEED_URIS = "https://example.com/packagerepos/release \
                              https://example.com/packagerepos/updates"
         PACKAGE_FEED_BASE_PATHS = "rpm rpm-dev"
         PACKAGE_FEED_ARCHS = "all core2-64"

      Given these settings, the resulting package feeds are as follows:

      .. code-block:: none

         https://example.com/packagerepos/release/rpm/all
         https://example.com/packagerepos/release/rpm/core2-64
         https://example.com/packagerepos/release/rpm-dev/all
         https://example.com/packagerepos/release/rpm-dev/core2-64
         https://example.com/packagerepos/updates/rpm/all
         https://example.com/packagerepos/updates/rpm/core2-64
         https://example.com/packagerepos/updates/rpm-dev/all
         https://example.com/packagerepos/updates/rpm-dev/core2-64

   :term:`PACKAGE_FEED_BASE_PATHS`
      Specifies the base path used when constructing package feed URIs. The
      :term:`PACKAGE_FEED_BASE_PATHS` variable makes up the middle portion of a
      package feed URI used by the OpenEmbedded build system. The base path
      lies between the :term:`PACKAGE_FEED_URIS`
      and :term:`PACKAGE_FEED_ARCHS` variables.

      Consider the following example where the :term:`PACKAGE_FEED_URIS`,
      :term:`PACKAGE_FEED_BASE_PATHS`, and :term:`PACKAGE_FEED_ARCHS` variables are
      defined in your ``local.conf`` file::

         PACKAGE_FEED_URIS = "https://example.com/packagerepos/release \
                              https://example.com/packagerepos/updates"
         PACKAGE_FEED_BASE_PATHS = "rpm rpm-dev"
         PACKAGE_FEED_ARCHS = "all core2-64"

      Given these settings, the resulting package feeds are as follows:

      .. code-block:: none

         https://example.com/packagerepos/release/rpm/all
         https://example.com/packagerepos/release/rpm/core2-64
         https://example.com/packagerepos/release/rpm-dev/all
         https://example.com/packagerepos/release/rpm-dev/core2-64
         https://example.com/packagerepos/updates/rpm/all
         https://example.com/packagerepos/updates/rpm/core2-64
         https://example.com/packagerepos/updates/rpm-dev/all
         https://example.com/packagerepos/updates/rpm-dev/core2-64

   :term:`PACKAGE_FEED_URIS`
      Specifies the front portion of the package feed URI used by the
      OpenEmbedded build system. Each final package feed URI is comprised
      of :term:`PACKAGE_FEED_URIS`,
      :term:`PACKAGE_FEED_BASE_PATHS`, and
      :term:`PACKAGE_FEED_ARCHS` variables.

      Consider the following example where the :term:`PACKAGE_FEED_URIS`,
      :term:`PACKAGE_FEED_BASE_PATHS`, and :term:`PACKAGE_FEED_ARCHS` variables are
      defined in your ``local.conf`` file::

         PACKAGE_FEED_URIS = "https://example.com/packagerepos/release \
                              https://example.com/packagerepos/updates"
         PACKAGE_FEED_BASE_PATHS = "rpm rpm-dev"
         PACKAGE_FEED_ARCHS = "all core2-64"

      Given these settings, the resulting package feeds are as follows:

      .. code-block:: none

         https://example.com/packagerepos/release/rpm/all
         https://example.com/packagerepos/release/rpm/core2-64
         https://example.com/packagerepos/release/rpm-dev/all
         https://example.com/packagerepos/release/rpm-dev/core2-64
         https://example.com/packagerepos/updates/rpm/all
         https://example.com/packagerepos/updates/rpm/core2-64
         https://example.com/packagerepos/updates/rpm-dev/all
         https://example.com/packagerepos/updates/rpm-dev/core2-64

   :term:`PACKAGE_INSTALL`
      The final list of packages passed to the package manager for
      installation into the image.

      Because the package manager controls actual installation of all
      packages, the list of packages passed using :term:`PACKAGE_INSTALL` is
      not the final list of packages that are actually installed. This
      variable is internal to the image construction code. Consequently, in
      general, you should use the
      :term:`IMAGE_INSTALL` variable to specify
      packages for installation. The exception to this is when working with
      the :ref:`core-image-minimal-initramfs <ref-manual/images:images>`
      image. When working with an initial RAM filesystem (:term:`Initramfs`) image,
      use the :term:`PACKAGE_INSTALL` variable. For information on creating an
      :term:`Initramfs`, see the ":ref:`dev-manual/building:building an initial ram filesystem (Initramfs) image`" section
      in the Yocto Project Development Tasks Manual.

   :term:`PACKAGE_INSTALL_ATTEMPTONLY`
      Specifies a list of packages the OpenEmbedded build system attempts
      to install when creating an image. If a listed package fails to
      install, the build system does not generate an error. This variable
      is generally not user-defined.

   :term:`PACKAGE_PREPROCESS_FUNCS`
      Specifies a list of functions run to pre-process the
      :term:`PKGD` directory prior to splitting the files out
      to individual packages.

   :term:`PACKAGE_WRITE_DEPS`
      Specifies a list of dependencies for post-installation and
      pre-installation scripts on native/cross tools. If your
      post-installation or pre-installation script can execute at root filesystem
      creation time rather than on the target but depends on a native tool
      in order to execute, you need to list the tools in
      :term:`PACKAGE_WRITE_DEPS`.

      For information on running post-installation scripts, see the
      ":ref:`dev-manual/new-recipe:post-installation scripts`"
      section in the Yocto Project Development Tasks Manual.

   :term:`PACKAGECONFIG`
      This variable provides a means of enabling or disabling features of a
      recipe on a per-recipe basis. :term:`PACKAGECONFIG` blocks are defined in
      recipes when you specify features and then arguments that define
      feature behaviors. Here is the basic block structure (broken over
      multiple lines for readability)::

         PACKAGECONFIG ??= "f1 f2 f3 ..."
         PACKAGECONFIG[f1] = "\
             --with-f1, \
             --without-f1, \
             build-deps-for-f1, \
             runtime-deps-for-f1, \
             runtime-recommends-for-f1, \
             packageconfig-conflicts-for-f1"
         PACKAGECONFIG[f2] = "\
              ... and so on and so on ...

      The :term:`PACKAGECONFIG` variable itself specifies a space-separated
      list of the features to enable. Following the features, you can
      determine the behavior of each feature by providing up to six
      order-dependent arguments, which are separated by commas. You can
      omit any argument you like but must retain the separating commas. The
      order is important and specifies the following:

      #. Extra arguments that should be added to :term:`PACKAGECONFIG_CONFARGS`
         if the feature is enabled.

      #. Extra arguments that should be added to :term:`PACKAGECONFIG_CONFARGS`
         if the feature is disabled.

      #. Additional build dependencies (:term:`DEPENDS`)
         that should be added if the feature is enabled.

      #. Additional runtime dependencies (:term:`RDEPENDS`)
         that should be added if the feature is enabled.

      #. Additional runtime recommendations
         (:term:`RRECOMMENDS`) that should be added if
         the feature is enabled.

      #. Any conflicting (that is, mutually exclusive) :term:`PACKAGECONFIG`
         settings for this feature.

      Consider the following :term:`PACKAGECONFIG` block taken from the
      ``librsvg`` recipe. In this example the feature is ``gtk``, which has
      three arguments that determine the feature's behavior::

         PACKAGECONFIG[gtk] = "--with-gtk3,--without-gtk3,gtk+3"

      The
      ``--with-gtk3`` and ``gtk+3`` arguments apply only if the feature is
      enabled. In this case, ``--with-gtk3`` is added to the configure
      script argument list and ``gtk+3`` is added to :term:`DEPENDS`. On the
      other hand, if the feature is disabled say through a ``.bbappend``
      file in another layer, then the second argument ``--without-gtk3`` is
      added to the configure script instead.

      The basic :term:`PACKAGECONFIG` structure previously described holds true
      regardless of whether you are creating a block or changing a block.
      When creating a block, use the structure inside your recipe.

      If you want to change an existing :term:`PACKAGECONFIG` block, you can do
      so one of two ways:

      -  *Append file:* Create an append file named
         ``recipename.bbappend`` in your layer and override the value of
         :term:`PACKAGECONFIG`. You can either completely override the
         variable::

            PACKAGECONFIG = "f4 f5"

         Or, you can just append the variable::

            PACKAGECONFIG:append = " f4"

      -  *Configuration file:* This method is identical to changing the
         block through an append file except you edit your ``local.conf``
         or ``mydistro.conf`` file. As with append files previously
         described, you can either completely override the variable::

            PACKAGECONFIG:pn-recipename = "f4 f5"

         Or, you can just amend the variable::

            PACKAGECONFIG:append:pn-recipename = " f4"

      Consider the following example of a :ref:`ref-classes-cmake` recipe with a systemd service
      in which :term:`PACKAGECONFIG` is used to transform the systemd service
      into a feature that can be easily enabled or disabled via :term:`PACKAGECONFIG`::

         example.c
         example.service
         CMakeLists.txt

      The ``CMakeLists.txt`` file contains::

         if(WITH_SYSTEMD)
            install(FILES ${PROJECT_SOURCE_DIR}/example.service DESTINATION /etc/systemd/systemd)
         endif(WITH_SYSTEMD)

      In order to enable the installation of ``example.service`` we need to
      ensure that ``-DWITH_SYSTEMD=ON`` is passed to the ``cmake`` command
      execution.  Recipes that have ``CMakeLists.txt`` generally inherit the
      :ref:`ref-classes-cmake` class, that runs ``cmake`` with
      :term:`EXTRA_OECMAKE`, which :term:`PACKAGECONFIG_CONFARGS` will be
      appended to.  Now, knowing that :term:`PACKAGECONFIG_CONFARGS` is
      automatically filled with either the first or second element of
      :term:`PACKAGECONFIG` flag value, the recipe would be like::

         inherit cmake
         PACKAGECONFIG = "systemd"
         PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=ON,-DWITH_SYSTEMD=OFF"

      A side note to this recipe is to check if ``systemd`` is in fact the used :term:`INIT_MANAGER`
      or not::

         PACKAGECONFIG = "${@'systemd' if d.getVar('INIT_MANAGER') == 'systemd' else ''}"

   :term:`PACKAGECONFIG_CONFARGS`
      A space-separated list of configuration options generated from the
      :term:`PACKAGECONFIG` setting.

      Classes such as :ref:`ref-classes-autotools` and :ref:`ref-classes-cmake`
      use :term:`PACKAGECONFIG_CONFARGS` to pass :term:`PACKAGECONFIG` options
      to ``configure`` and ``cmake``, respectively. If you are using
      :term:`PACKAGECONFIG` but not a class that handles the
      :ref:`ref-tasks-configure` task, then you need to use
      :term:`PACKAGECONFIG_CONFARGS` appropriately.

   :term:`PACKAGEGROUP_DISABLE_COMPLEMENTARY`
      For recipes inheriting the :ref:`ref-classes-packagegroup` class, setting
      :term:`PACKAGEGROUP_DISABLE_COMPLEMENTARY` to "1" specifies that the
      normal complementary packages (i.e. ``-dev``, ``-dbg``, and so forth)
      should not be automatically created by the ``packagegroup`` recipe,
      which is the default behavior.

   :term:`PACKAGES`
      The list of packages the recipe creates. The default value is the
      following::

         ${PN}-src ${PN}-dbg ${PN}-staticdev ${PN}-dev ${PN}-doc ${PN}-locale ${PACKAGE_BEFORE_PN} ${PN}

      During packaging, the :ref:`ref-tasks-package` task
      goes through :term:`PACKAGES` and uses the :term:`FILES`
      variable corresponding to each package to assign files to the
      package. If a file matches the :term:`FILES` variable for more than one
      package in :term:`PACKAGES`, it will be assigned to the earliest
      (leftmost) package.

      Packages in the variable's list that are empty (i.e. where none of
      the patterns in ``FILES:``\ pkg match any files installed by the
      :ref:`ref-tasks-install` task) are not generated,
      unless generation is forced through the
      :term:`ALLOW_EMPTY` variable.

   :term:`PACKAGES_DYNAMIC`
      A promise that your recipe satisfies runtime dependencies for
      optional modules that are found in other recipes.
      :term:`PACKAGES_DYNAMIC` does not actually satisfy the dependencies, it
      only states that they should be satisfied. For example, if a hard,
      runtime dependency (:term:`RDEPENDS`) of another
      package is satisfied at build time through the :term:`PACKAGES_DYNAMIC`
      variable, but a package with the module name is never actually
      produced, then the other package will be broken. Thus, if you attempt
      to include that package in an image, you will get a dependency
      failure from the packaging system during the
      :ref:`ref-tasks-rootfs` task.

      Typically, if there is a chance that such a situation can occur and
      the package that is not created is valid without the dependency being
      satisfied, then you should use :term:`RRECOMMENDS`
      (a soft runtime dependency) instead of :term:`RDEPENDS`.

      For an example of how to use the :term:`PACKAGES_DYNAMIC` variable when
      you are splitting packages, see the
      ":ref:`dev-manual/packages:handling optional module packaging`"
      section in the Yocto Project Development Tasks Manual.

   :term:`PACKAGESPLITFUNCS`
      Specifies a list of functions run to perform additional splitting of
      files into individual packages. Recipes can either prepend to this
      variable or prepend to the ``populate_packages`` function in order to
      perform additional package splitting. In either case, the function
      should set :term:`PACKAGES`,
      :term:`FILES`, :term:`RDEPENDS` and
      other packaging variables appropriately in order to perform the
      desired splitting.

   :term:`PARALLEL_MAKE`

      Extra options passed to the build tool command (``make``,
      ``ninja`` or more specific build engines, like the Go language one)
      during the :ref:`ref-tasks-compile` task, to specify parallel compilation
      on the local build host. This variable is usually in the form "-j x",
      where x represents the maximum number of parallel threads such engines
      can run.

      .. note::

         For software compiled by ``make``, in order for :term:`PARALLEL_MAKE`
         to be effective, ``make`` must be called with
         ``${``\ :term:`EXTRA_OEMAKE`\ ``}``. An easy
         way to ensure this is to use the ``oe_runmake`` function.

      By default, the OpenEmbedded build system automatically sets this
      variable to be equal to the number of cores the build system uses.

      .. note::

         If the software being built experiences dependency issues during
         the :ref:`ref-tasks-compile` task that result in race conditions, you can clear
         the :term:`PARALLEL_MAKE` variable within the recipe as a workaround. For
         information on addressing race conditions, see the
         ":ref:`dev-manual/debugging:debugging parallel make races`"
         section in the Yocto Project Development Tasks Manual.

      For single socket systems (i.e. one CPU), you should not have to
      override this variable to gain optimal parallelism during builds.
      However, if you have very large systems that employ multiple physical
      CPUs, you might want to make sure the :term:`PARALLEL_MAKE` variable is
      not set higher than "-j 20".

      For more information on speeding up builds, see the
      ":ref:`dev-manual/speeding-up-build:speeding up a build`"
      section in the Yocto Project Development Tasks Manual.

   :term:`PARALLEL_MAKEINST`
      Extra options passed to the build tool install command
      (``make install``, ``ninja install`` or more specific ones)
      during the :ref:`ref-tasks-install` task in order to specify
      parallel installation. This variable defaults to the value of
      :term:`PARALLEL_MAKE`.

      .. note::

         For software compiled by ``make``, in order for :term:`PARALLEL_MAKEINST`
         to be effective, ``make`` must be called with
         ``${``\ :term:`EXTRA_OEMAKE`\ ``}``. An easy
         way to ensure this is to use the ``oe_runmake`` function.

         If the software being built experiences dependency issues during
         the :ref:`ref-tasks-install` task that result in race conditions, you can
         clear the :term:`PARALLEL_MAKEINST` variable within the recipe as a
         workaround. For information on addressing race conditions, see the
         ":ref:`dev-manual/debugging:debugging parallel make races`"
         section in the Yocto Project Development Tasks Manual.

   :term:`PATCHRESOLVE`
      Determines the action to take when a patch fails. You can set this
      variable to one of two values: "noop" and "user".

      The default value of "noop" causes the build to simply fail when the
      OpenEmbedded build system cannot successfully apply a patch. Setting
      the value to "user" causes the build system to launch a shell and
      places you in the right location so that you can manually resolve the
      conflicts.

      Set this variable in your ``local.conf`` file.

   :term:`PATCHTOOL`
      Specifies the utility used to apply patches for a recipe during the
      :ref:`ref-tasks-patch` task. You can specify one of
      three utilities: "patch", "quilt", or "git". The default utility used
      is "quilt" except for the quilt-native recipe itself. Because the
      quilt tool is not available at the time quilt-native is being
      patched, it uses "patch".

      If you wish to use an alternative patching tool, set the variable in
      the recipe using one of the following::

         PATCHTOOL = "patch"
         PATCHTOOL = "quilt"
         PATCHTOOL = "git"

   :term:`PE`
      The epoch of the recipe. By default, this variable is unset. The
      variable is used to make upgrades possible when the versioning scheme
      changes in some backwards incompatible way.

      :term:`PE` is the default value of the :term:`PKGE` variable.

   :term:`PEP517_WHEEL_PATH`
      When used by recipes that inherit the :ref:`ref-classes-python_pep517`
      class, denotes the path to ``dist/`` (short for distribution) where the
      binary archive ``wheel`` is built.

   :term:`PERSISTENT_DIR`
      See :term:`bitbake:PERSISTENT_DIR` in the BitBake manual.

   :term:`PF`
      Specifies the recipe or package name and includes all version and
      revision numbers (i.e. ``glibc-2.13-r20+svnr15508/`` and
      ``bash-4.2-r1/``). This variable is comprised of the following:
      ${:term:`PN`}-${:term:`EXTENDPE`}${:term:`PV`}-${:term:`PR`}

   :term:`PIXBUF_PACKAGES`
      When inheriting the :ref:`ref-classes-pixbufcache`
      class, this variable identifies packages that contain the pixbuf
      loaders used with ``gdk-pixbuf``. By default, the
      :ref:`ref-classes-pixbufcache` class assumes that
      the loaders are in the recipe's main package (i.e.
      ``${``\ :term:`PN`\ ``}``). Use this variable if the
      loaders you need are in a package other than that main package.

   :term:`PKG`
      The name of the resulting package created by the OpenEmbedded build
      system.

      .. note::

         When using the :term:`PKG` variable, you must use a package name override.

      For example, when the :ref:`ref-classes-debian` class renames the output
      package, it does so by setting ``PKG:packagename``.

   :term:`PKG_CONFIG_PATH`
      The path to ``pkg-config`` files for the current build context.
      ``pkg-config`` reads this variable from the environment.

   :term:`PKGD`
      Points to the destination directory for files to be packaged before
      they are split into individual packages. This directory defaults to
      the following::

         ${WORKDIR}/package

      Do not change this default.

   :term:`PKGDATA_DIR`
      Points to a shared, global-state directory that holds data generated
      during the packaging process. During the packaging process, the
      :ref:`ref-tasks-packagedata` task packages data
      for each recipe and installs it into this temporary, shared area.
      This directory defaults to the following, which you should not
      change::

         ${STAGING_DIR_HOST}/pkgdata

      For examples of how this data is used, see the
      ":ref:`overview-manual/concepts:automatically added runtime dependencies`"
      section in the Yocto Project Overview and Concepts Manual and the
      ":ref:`dev-manual/debugging:viewing package information with \`\`oe-pkgdata-util\`\``"
      section in the Yocto Project Development Tasks Manual. For more
      information on the shared, global-state directory, see
      :term:`STAGING_DIR_HOST`.

   :term:`PKGDEST`
      Points to the parent directory for files to be packaged after they
      have been split into individual packages. This directory defaults to
      the following::

         ${WORKDIR}/packages-split

      Under this directory, the build system creates directories for each
      package specified in :term:`PACKAGES`. Do not change
      this default.

   :term:`PKGDESTWORK`
      Points to a temporary work area where the
      :ref:`ref-tasks-package` task saves package metadata.
      The :term:`PKGDESTWORK` location defaults to the following::

         ${WORKDIR}/pkgdata

      Do not change this default.

      The :ref:`ref-tasks-packagedata` task copies the
      package metadata from :term:`PKGDESTWORK` to
      :term:`PKGDATA_DIR` to make it available globally.

   :term:`PKGE`
      The epoch of the package(s) built by the recipe. By default, :term:`PKGE`
      is set to :term:`PE`.

   :term:`PKGR`
      The revision of the package(s) built by the recipe. By default,
      :term:`PKGR` is set to :term:`PR`.

   :term:`PKGV`
      The version of the package(s) built by the recipe. By default,
      :term:`PKGV` is set to :term:`PV`.

   :term:`PN`
      This variable can have two separate functions depending on the
      context: a recipe name or a resulting package name.

      :term:`PN` refers to a recipe name in the context of a file used by the
      OpenEmbedded build system as input to create a package. The name is
      normally extracted from the recipe file name. For example, if the
      recipe is named ``expat_2.0.1.bb``, then the default value of :term:`PN`
      will be "expat".

      The variable refers to a package name in the context of a file
      created or produced by the OpenEmbedded build system.

      If applicable, the :term:`PN` variable also contains any special suffix
      or prefix. For example, using ``bash`` to build packages for the
      native machine, :term:`PN` is ``bash-native``. Using ``bash`` to build
      packages for the target and for Multilib, :term:`PN` would be ``bash``
      and ``lib64-bash``, respectively.

   :term:`POPULATE_SDK_POST_HOST_COMMAND`
      Specifies a list of functions to call once the OpenEmbedded build
      system has created the host part of the SDK. You can specify
      functions separated by spaces::

          POPULATE_SDK_POST_HOST_COMMAND += "function"

      If you need to pass the SDK path to a command within a function, you
      can use ``${SDK_DIR}``, which points to the parent directory used by
      the OpenEmbedded build system when creating SDK output. See the
      :term:`SDK_DIR` variable for more information.

   :term:`POPULATE_SDK_POST_TARGET_COMMAND`
      Specifies a list of functions to call once the OpenEmbedded build
      system has created the target part of the SDK. You can specify
      functions separated by spaces::

         POPULATE_SDK_POST_TARGET_COMMAND += "function"

      If you need to pass the SDK path to a command within a function, you
      can use ``${SDK_DIR}``, which points to the parent directory used by
      the OpenEmbedded build system when creating SDK output. See the
      :term:`SDK_DIR` variable for more information.

   :term:`PR`
      The revision of the recipe. The default value for this variable is
      "r0". Subsequent revisions of the recipe conventionally have the
      values "r1", "r2", and so forth. When :term:`PV` increases,
      :term:`PR` is conventionally reset to "r0".

      .. note::

         The OpenEmbedded build system does not need the aid of :term:`PR`
         to know when to rebuild a recipe. The build system uses the task
         :ref:`input checksums <overview-manual/concepts:checksums (signatures)>` along with the
         :ref:`stamp <structure-build-tmp-stamps>` and
         :ref:`overview-manual/concepts:shared state cache`
         mechanisms.

      The :term:`PR` variable primarily becomes significant when a package
      manager dynamically installs packages on an already built image. In
      this case, :term:`PR`, which is the default value of
      :term:`PKGR`, helps the package manager distinguish which
      package is the most recent one in cases where many packages have the
      same :term:`PV` (i.e. :term:`PKGV`). A component having many packages with
      the same :term:`PV` usually means that the packages all install the same
      upstream version, but with later (:term:`PR`) version packages including
      packaging fixes.

      .. note::

         :term:`PR` does not need to be increased for changes that do not change the
         package contents or metadata.

      Because manually managing :term:`PR` can be cumbersome and error-prone,
      an automated solution exists. See the
      ":ref:`dev-manual/packages:working with a pr service`" section
      in the Yocto Project Development Tasks Manual for more information.

   :term:`PREFERRED_PROVIDER`
      If multiple recipes provide the same item, this variable determines
      which recipe is preferred and thus provides the item (i.e. the
      preferred provider). You should always suffix this variable with the
      name of the provided item. And, you should define the variable using
      the preferred recipe's name (:term:`PN`). Here is a common
      example::

         PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto"

      In the previous example, multiple recipes are providing "virtual/kernel".
      The :term:`PREFERRED_PROVIDER` variable is set with the name (:term:`PN`) of
      the recipe you prefer to provide "virtual/kernel".

      Here are more examples::

         PREFERRED_PROVIDER_virtual/xserver = "xserver-xf86"
         PREFERRED_PROVIDER_virtual/libgl ?= "mesa"

      For more
      information, see the ":ref:`dev-manual/new-recipe:using virtual providers`"
      section in the Yocto Project Development Tasks Manual.

      .. note::

         If you use a ``virtual/\*`` item with :term:`PREFERRED_PROVIDER`, then any
         recipe that :term:`PROVIDES` that item but is not selected (defined)
         by :term:`PREFERRED_PROVIDER` is prevented from building, which is usually
         desirable since this mechanism is designed to select between mutually
         exclusive alternative providers.

   :term:`PREFERRED_PROVIDERS`
      See :term:`bitbake:PREFERRED_PROVIDERS` in the BitBake manual.

   :term:`PREFERRED_VERSION`
      If there are multiple versions of a recipe available, this variable
      determines which version should be given preference. You must always
      suffix the variable with the :term:`PN` you want to select (`python` in
      the first example below), and you should specify the :term:`PV`
      accordingly (`3.4.0` in the example).

      The :term:`PREFERRED_VERSION` variable supports limited wildcard use
      through the "``%``" character. You can use the character to match any
      number of characters, which can be useful when specifying versions
      that contain long revision numbers that potentially change. Here are
      two examples::

         PREFERRED_VERSION_python = "3.4.0"
         PREFERRED_VERSION_linux-yocto = "5.0%"

      .. note::

         The use of the "%" character is limited in that it only works at the end of the
         string. You cannot use the wildcard character in any other
         location of the string.

      The specified version is matched against :term:`PV`, which
      does not necessarily match the version part of the recipe's filename.
      For example, consider two recipes ``foo_1.2.bb`` and ``foo_git.bb``
      where ``foo_git.bb`` contains the following assignment::

         PV = "1.1+git${SRCPV}"

      In this case, the correct way to select
      ``foo_git.bb`` is by using an assignment such as the following::

         PREFERRED_VERSION_foo = "1.1+git%"

      Compare that previous example
      against the following incorrect example, which does not work::

         PREFERRED_VERSION_foo = "git"

      Sometimes the :term:`PREFERRED_VERSION` variable can be set by
      configuration files in a way that is hard to change. You can use
      :term:`OVERRIDES` to set a machine-specific
      override. Here is an example::

         PREFERRED_VERSION_linux-yocto:qemux86 = "5.0%"

      Although not recommended, worst case, you can also use the
      "forcevariable" override, which is the strongest override possible.
      Here is an example::

         PREFERRED_VERSION_linux-yocto:forcevariable = "5.0%"

      .. note::

         The ``:forcevariable`` override is not handled specially. This override
         only works because the default value of :term:`OVERRIDES` includes "forcevariable".

      If a recipe with the specified version is not available, a warning
      message will be shown. See :term:`REQUIRED_VERSION` if you want this
      to be an error instead.

   :term:`PREMIRRORS`
      Specifies additional paths from which the OpenEmbedded build system
      gets source code. When the build system searches for source code, it
      first tries the local download directory. If that location fails, the
      build system tries locations defined by :term:`PREMIRRORS`, the upstream
      source, and then locations specified by
      :term:`MIRRORS` in that order.

      The default value for :term:`PREMIRRORS` is defined in the
      ``meta/classes-global/mirrors.bbclass`` file in the core metadata layer.

      Typically, you could add a specific server for the build system to
      attempt before any others by adding something like the following to
      the ``local.conf`` configuration file in the
      :term:`Build Directory`::

         PREMIRRORS:prepend = "\
             git://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
             ftp://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
             http://.*/.* &YOCTO_DL_URL;/mirror/sources/ \
             https://.*/.* &YOCTO_DL_URL;/mirror/sources/"

      These changes cause the
      build system to intercept Git, FTP, HTTP, and HTTPS requests and
      direct them to the ``http://`` sources mirror. You can use
      ``file://`` URLs to point to local directories or network shares as
      well.

   :term:`PRIORITY`
      Indicates the importance of a package.

      :term:`PRIORITY` is considered to be part of the distribution policy
      because the importance of any given recipe depends on the purpose for
      which the distribution is being produced. Thus, :term:`PRIORITY` is not
      normally set within recipes.

      You can set :term:`PRIORITY` to "required", "standard", "extra", and
      "optional", which is the default.

   :term:`PRIVATE_LIBS`
      Specifies libraries installed within a recipe that should be ignored
      by the OpenEmbedded build system's shared library resolver. This
      variable is typically used when software being built by a recipe has
      its own private versions of a library normally provided by another
      recipe. In this case, you would not want the package containing the
      private libraries to be set as a dependency on other unrelated
      packages that should instead depend on the package providing the
      standard version of the library.

      Libraries specified in this variable should be specified by their
      file name. For example, from the Firefox recipe in meta-browser::

         PRIVATE_LIBS = "libmozjs.so \
                         libxpcom.so \
                         libnspr4.so \
                         libxul.so \
                         libmozalloc.so \
                         libplc4.so \
                         libplds4.so"

      For more information, see the
      ":ref:`overview-manual/concepts:automatically added runtime dependencies`"
      section in the Yocto Project Overview and Concepts Manual.

   :term:`PROVIDES`
      A list of aliases by which a particular recipe can be known. By
      default, a recipe's own :term:`PN` is implicitly already in its
      :term:`PROVIDES` list and therefore does not need to mention that it
      provides itself. If a recipe uses :term:`PROVIDES`, the additional
      aliases are synonyms for the recipe and can be useful for satisfying
      dependencies of other recipes during the build as specified by
      :term:`DEPENDS`.

      Consider the following example :term:`PROVIDES` statement from the recipe
      file ``eudev_3.2.9.bb``::

         PROVIDES += "udev"

      The :term:`PROVIDES` statement
      results in the "eudev" recipe also being available as simply "udev".

      .. note::

         A recipe's own recipe name (:term:`PN`) is always implicitly prepended
         to :term:`PROVIDES`, so while using "+=" in the above example may not be
         strictly necessary it is recommended to avoid confusion.

      In addition to providing recipes under alternate names, the
      :term:`PROVIDES` mechanism is also used to implement virtual targets. A
      virtual target is a name that corresponds to some particular
      functionality (e.g. a Linux kernel). Recipes that provide the
      functionality in question list the virtual target in :term:`PROVIDES`.
      Recipes that depend on the functionality in question can include the
      virtual target in :term:`DEPENDS` to leave the choice of provider open.

      Conventionally, virtual targets have names on the form
      "virtual/function" (e.g. "virtual/kernel"). The slash is simply part
      of the name and has no syntactical significance.

      The :term:`PREFERRED_PROVIDER` variable is
      used to select which particular recipe provides a virtual target.

      .. note::

         A corresponding mechanism for virtual runtime dependencies (packages)
         exists. However, the mechanism does not depend on any special
         functionality beyond ordinary variable assignments. For example,
         :term:`VIRTUAL-RUNTIME_dev_manager <VIRTUAL-RUNTIME>` refers to the
         package of the component that manages the ``/dev`` directory.

         Setting the "preferred provider" for runtime dependencies is as
         simple as using the following assignment in a configuration file::

                 VIRTUAL-RUNTIME_dev_manager = "udev"


   :term:`PRSERV_HOST`
      The network based :term:`PR` service host and port.

      The ``conf/templates/default/local.conf.sample.extended`` configuration
      file in the :term:`Source Directory` shows how the :term:`PRSERV_HOST`
      variable is set::

         PRSERV_HOST = "localhost:0"

      You must
      set the variable if you want to automatically start a local :ref:`PR
      service <dev-manual/packages:working with a pr service>`. You can
      set :term:`PRSERV_HOST` to other values to use a remote PR service.


   :term:`PSEUDO_IGNORE_PATHS`
      A comma-separated (without spaces) list of path prefixes that should be ignored
      by pseudo when monitoring and recording file operations, in order to avoid
      problems with files being written to outside of the pseudo context and
      reduce pseudo's overhead. A path is ignored if it matches any prefix in the list
      and can include partial directory (or file) names.


   :term:`PTEST_ENABLED`
      Specifies whether or not :ref:`Package
      Test <dev-manual/packages:testing packages with ptest>` (ptest)
      functionality is enabled when building a recipe. You should not set
      this variable directly. Enabling and disabling building Package Tests
      at build time should be done by adding "ptest" to (or removing it
      from) :term:`DISTRO_FEATURES`.

   :term:`PV`
      The version of the recipe. The version is normally extracted from the
      recipe filename. For example, if the recipe is named
      ``expat_2.0.1.bb``, then the default value of :term:`PV` will be "2.0.1".
      :term:`PV` is generally not overridden within a recipe unless it is
      building an unstable (i.e. development) version from a source code
      repository (e.g. Git or Subversion).

      :term:`PV` is the default value of the :term:`PKGV` variable.

   :term:`PYPI_PACKAGE`
      When inheriting the :ref:`ref-classes-pypi` class, specifies the
      `PyPI <https://pypi.org/>`__ package name to be built. The default value
      is set based upon :term:`BPN` (stripping any "python-" or "python3-"
      prefix off if present), however for some packages it will need to be set
      explicitly if that will not match the package name (e.g. where the
      package name has a prefix, underscores, uppercase letters etc.)

   :term:`PYTHON_ABI`
      When used by recipes that inherit the :ref:`ref-classes-setuptools3`
      class, denotes the Application Binary Interface (ABI) currently in use
      for Python. By default, the ABI is "m". You do not have to set this
      variable as the OpenEmbedded build system sets it for you.

      The OpenEmbedded build system uses the ABI to construct directory
      names used when installing the Python headers and libraries in
      sysroot (e.g. ``.../python3.3m/...``).

   :term:`QA_EMPTY_DIRS`
      Specifies a list of directories that are expected to be empty when
      packaging; if ``empty-dirs`` appears in :term:`ERROR_QA` or
      :term:`WARN_QA` these will be checked and an error or warning
      (respectively) will be produced.

      The default :term:`QA_EMPTY_DIRS` value is set in
      :ref:`insane.bbclass <ref-classes-insane>`.

   :term:`QA_EMPTY_DIRS_RECOMMENDATION`
      Specifies a recommendation for why a directory must be empty,
      which will be included in the error message if a specific directory
      is found to contain files. Must be overridden with the directory
      path to match on.

      If no recommendation is specified for a directory, then the default
      "but it is expected to be empty" will be used.

      An example message shows if files were present in '/dev'::

         QA_EMPTY_DIRS_RECOMMENDATION:/dev = "but all devices must be created at runtime"

   :term:`RANLIB`
      The minimal command and arguments to run ``ranlib``.

   :term:`RCONFLICTS`
      The list of packages that conflict with packages. Note that packages
      will not be installed if conflicting packages are not first removed.

      Like all package-controlling variables, you must always use them in
      conjunction with a package name override. Here is an example::

         RCONFLICTS:${PN} = "another_conflicting_package_name"

      BitBake, which the OpenEmbedded build system uses, supports
      specifying versioned dependencies. Although the syntax varies
      depending on the packaging format, BitBake hides these differences
      from you. Here is the general syntax to specify versions with the
      :term:`RCONFLICTS` variable::

         RCONFLICTS:${PN} = "package (operator version)"

      For ``operator``, you can specify the following:

      - =
      - <
      - >
      - <=
      - >=

      For example, the following sets up a dependency on version 1.2 or
      greater of the package ``foo``::

         RCONFLICTS:${PN} = "foo (>= 1.2)"

   :term:`RDEPENDS`
      Lists runtime dependencies of a package. These dependencies are other
      packages that must be installed in order for the package to function
      correctly. As an example, the following assignment declares that the
      package ``foo`` needs the packages ``bar`` and ``baz`` to be
      installed::

         RDEPENDS:foo = "bar baz"

      The most common types of package
      runtime dependencies are automatically detected and added. Therefore,
      most recipes do not need to set :term:`RDEPENDS`. For more information,
      see the
      ":ref:`overview-manual/concepts:automatically added runtime dependencies`"
      section in the Yocto Project Overview and Concepts Manual.

      The practical effect of the above :term:`RDEPENDS` assignment is that
      ``bar`` and ``baz`` will be declared as dependencies inside the
      package ``foo`` when it is written out by one of the
      :ref:`do_package_write_* <ref-tasks-package_write_deb>` tasks.
      Exactly how this is done depends on which package format is used,
      which is determined by
      :term:`PACKAGE_CLASSES`. When the
      corresponding package manager installs the package, it will know to
      also install the packages on which it depends.

      To ensure that the packages ``bar`` and ``baz`` get built, the
      previous :term:`RDEPENDS` assignment also causes a task dependency to be
      added. This dependency is from the recipe's
      :ref:`ref-tasks-build` (not to be confused with
      :ref:`ref-tasks-compile`) task to the
      :ref:`do_package_write_* <ref-tasks-package_write_deb>` task of the recipes that build ``bar`` and
      ``baz``.

      The names of the packages you list within :term:`RDEPENDS` must be the
      names of other packages --- they cannot be recipe names. Although
      package names and recipe names usually match, the important point
      here is that you are providing package names within the :term:`RDEPENDS`
      variable. For an example of the default list of packages created from
      a recipe, see the :term:`PACKAGES` variable.

      Because the :term:`RDEPENDS` variable applies to packages being built,
      you should always use the variable in a form with an attached package
      name (remember that a single recipe can build multiple packages). For
      example, suppose you are building a development package that depends
      on the ``perl`` package. In this case, you would use the following
      :term:`RDEPENDS` statement::

         RDEPENDS:${PN}-dev += "perl"

      In the example,
      the development package depends on the ``perl`` package. Thus, the
      :term:`RDEPENDS` variable has the ``${PN}-dev`` package name as part of
      the variable.

      .. note::

         ``RDEPENDS:${PN}-dev`` includes ``${``\ :term:`PN`\ ``}``
         by default. This default is set in the BitBake configuration file
         (``meta/conf/bitbake.conf``). Be careful not to accidentally remove
         ``${PN}`` when modifying ``RDEPENDS:${PN}-dev``. Use the "+=" operator
         rather than the "=" operator.

      The package names you use with :term:`RDEPENDS` must appear as they would
      in the :term:`PACKAGES` variable. The :term:`PKG` variable
      allows a different name to be used for the final package (e.g. the
      :ref:`ref-classes-debian` class uses this to rename
      packages), but this final package name cannot be used with
      :term:`RDEPENDS`, which makes sense as :term:`RDEPENDS` is meant to be
      independent of the package format used.

      BitBake, which the OpenEmbedded build system uses, supports
      specifying versioned dependencies. Although the syntax varies
      depending on the packaging format, BitBake hides these differences
      from you. Here is the general syntax to specify versions with the
      :term:`RDEPENDS` variable::

         RDEPENDS:${PN} = "package (operator version)"

      For ``operator``, you can specify the following:

      - =
      - <
      - >
      - <=
      - >=

      For version, provide the version number.

      .. note::

         You can use :term:`EXTENDPKGV` to provide a full package version
         specification.

      For example, the following sets up a dependency on version 1.2 or
      greater of the package ``foo``::

         RDEPENDS:${PN} = "foo (>= 1.2)"

      For information on build-time dependencies, see the :term:`DEPENDS`
      variable. You can also see the
      ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:tasks`" and
      ":ref:`bitbake-user-manual/bitbake-user-manual-execution:dependencies`" sections in the
      BitBake User Manual for additional information on tasks and dependencies.

   :term:`RECIPE_MAINTAINER`
      This variable defines the name and e-mail address of the maintainer of a
      recipe. Such information can be used by human users submitted changes,
      and by automated tools to send notifications, for example about
      vulnerabilities or source updates.

      The variable can be defined in a global distribution :oe_git:`maintainers.inc
      </openembedded-core/tree/meta/conf/distro/include/maintainers.inc>` file::

          meta/conf/distro/include/maintainers.inc:RECIPE_MAINTAINER:pn-sysvinit = "Ross Burton <ross.burton@arm.com>"

      It can also be directly defined in a recipe,
      for example in the ``libgpiod`` one::

          RECIPE_MAINTAINER = "Bartosz Golaszewski <brgl@bgdev.pl>"

   :term:`RECIPE_NO_UPDATE_REASON`
      If a recipe should not be replaced by a more recent upstream version,
      putting the reason why in this variable in a recipe allows
      ``devtool check-upgrade-status`` command to display it, as explained
      in the ":ref:`ref-manual/devtool-reference:checking on the upgrade status of a recipe`"
      section.

   :term:`RECIPE_SYSROOT`
      This variable points to the directory that holds all files populated from
      recipes specified in :term:`DEPENDS`. As the name indicates,
      think of this variable as a custom root (``/``) for the recipe that will be
      used by the compiler in order to find headers and other files needed to complete
      its job.

      This variable is related to :term:`STAGING_DIR_HOST` or :term:`STAGING_DIR_TARGET`
      according to the type of the recipe and the build target.

      To better understand this variable, consider the following examples:

      -  For ``#include <header.h>``, ``header.h`` should be in ``"${RECIPE_SYSROOT}/usr/include"``

      -  For ``-lexample``, ``libexample.so`` should be in ``"${RECIPE_SYSROOT}/lib"``
         or other library sysroot directories.

      The default value is ``"${WORKDIR}/recipe-sysroot"``.
      Do not modify it.

   :term:`RECIPE_SYSROOT_NATIVE`
      This is similar to :term:`RECIPE_SYSROOT` but the populated files are from
      ``-native`` recipes. This allows a recipe built for the target machine to
      use ``native`` tools.

      This variable is related to :term:`STAGING_DIR_NATIVE`.

      The default value is ``"${WORKDIR}/recipe-sysroot-native"``.
      Do not modify it.

   :term:`REPODIR`
      See :term:`bitbake:REPODIR` in the BitBake manual.

   :term:`REQUIRED_DISTRO_FEATURES`
      When inheriting the :ref:`ref-classes-features_check`
      class, this variable identifies distribution features that must exist
      in the current configuration in order for the OpenEmbedded build
      system to build the recipe. In other words, if the
      :term:`REQUIRED_DISTRO_FEATURES` variable lists a feature that does not
      appear in :term:`DISTRO_FEATURES` within the current configuration, then
      the recipe will be skipped, and if the build system attempts to build
      the recipe then an error will be triggered.

   :term:`REQUIRED_VERSION`
      If there are multiple versions of a recipe available, this variable
      determines which version should be given preference.
      :term:`REQUIRED_VERSION` works in exactly the same manner as
      :term:`PREFERRED_VERSION`, except that if the specified version is not
      available then an error message is shown and the build fails
      immediately.

      If both :term:`REQUIRED_VERSION` and :term:`PREFERRED_VERSION` are set
      for the same recipe, the :term:`REQUIRED_VERSION` value applies.

   :term:`RM_WORK_EXCLUDE`
      With :ref:`ref-classes-rm-work` enabled, this variable
      specifies a list of recipes whose work directories should not be removed.
      See the ":ref:`ref-classes-rm-work`" section for more details.

   :term:`ROOT_HOME`
      Defines the root home directory. By default, this directory is set as
      follows in the BitBake configuration file::

         ROOT_HOME ??= "/home/root"

      .. note::

         This default value is likely used because some embedded solutions
         prefer to have a read-only root filesystem and prefer to keep
         writeable data in one place.

      You can override the default by setting the variable in any layer or
      in the ``local.conf`` file. Because the default is set using a "weak"
      assignment (i.e. "??="), you can use either of the following forms to
      define your override::

         ROOT_HOME = "/root"
         ROOT_HOME ?= "/root"

      These
      override examples use ``/root``, which is probably the most commonly
      used override.

   :term:`ROOTFS`
      Indicates a filesystem image to include as the root filesystem.

      The :term:`ROOTFS` variable is an optional variable used with the
      :ref:`ref-classes-image-live` class.

   :term:`ROOTFS_POSTINSTALL_COMMAND`
      Specifies a list of functions to call after the OpenEmbedded build
      system has installed packages. You can specify functions separated by
      spaces::

         ROOTFS_POSTINSTALL_COMMAND += "function"

      If you need to pass the root filesystem path to a command within a
      function, you can use ``${IMAGE_ROOTFS}``, which points to the
      directory that becomes the root filesystem image. See the
      :term:`IMAGE_ROOTFS` variable for more
      information.

   :term:`ROOTFS_POSTPROCESS_COMMAND`
      Specifies a list of functions to call once the OpenEmbedded build
      system has created the root filesystem. You can specify functions
      separated by spaces::

         ROOTFS_POSTPROCESS_COMMAND += "function"

      If you need to pass the root filesystem path to a command within a
      function, you can use ``${IMAGE_ROOTFS}``, which points to the
      directory that becomes the root filesystem image. See the
      :term:`IMAGE_ROOTFS` variable for more
      information.

   :term:`ROOTFS_POSTUNINSTALL_COMMAND`
      Specifies a list of functions to call after the OpenEmbedded build
      system has removed unnecessary packages. When runtime package
      management is disabled in the image, several packages are removed
      including ``base-passwd``, ``shadow``, and ``update-alternatives``.
      You can specify functions separated by spaces::

         ROOTFS_POSTUNINSTALL_COMMAND += "function"

      If you need to pass the root filesystem path to a command within a
      function, you can use ``${IMAGE_ROOTFS}``, which points to the
      directory that becomes the root filesystem image. See the
      :term:`IMAGE_ROOTFS` variable for more
      information.

   :term:`ROOTFS_PREPROCESS_COMMAND`
      Specifies a list of functions to call before the OpenEmbedded build
      system has created the root filesystem. You can specify functions
      separated by spaces::

         ROOTFS_PREPROCESS_COMMAND += "function"

      If you need to pass the root filesystem path to a command within a
      function, you can use ``${IMAGE_ROOTFS}``, which points to the
      directory that becomes the root filesystem image. See the
      :term:`IMAGE_ROOTFS` variable for more
      information.

   :term:`RPMBUILD_EXTRA_PARAMS`
      Specifies extra user-defined parameters for the ``rpmbuild`` command.

   :term:`RPROVIDES`
      A list of package name aliases that a package also provides. These
      aliases are useful for satisfying runtime dependencies of other
      packages both during the build and on the target (as specified by
      :term:`RDEPENDS`).

      .. note::

         A package's own name is implicitly already in its :term:`RPROVIDES` list.

      As with all package-controlling variables, you must always use the
      variable in conjunction with a package name override. Here is an
      example::

         RPROVIDES:${PN} = "widget-abi-2"

   :term:`RRECOMMENDS`
      A list of packages that extends the usability of a package being
      built. The package being built does not depend on this list of
      packages in order to successfully build, but rather uses them for
      extended usability. To specify runtime dependencies for packages, see
      the :term:`RDEPENDS` variable.

      The package manager will automatically install the :term:`RRECOMMENDS`
      list of packages when installing the built package. However, you can
      prevent listed packages from being installed by using the
      :term:`BAD_RECOMMENDATIONS`,
      :term:`NO_RECOMMENDATIONS`, and
      :term:`PACKAGE_EXCLUDE` variables.

      Packages specified in :term:`RRECOMMENDS` need not actually be produced.
      However, there must be a recipe providing each package, either
      through the :term:`PACKAGES` or
      :term:`PACKAGES_DYNAMIC` variables or the
      :term:`RPROVIDES` variable, or an error will occur
      during the build. If such a recipe does exist and the package is not
      produced, the build continues without error.

      Because the :term:`RRECOMMENDS` variable applies to packages being built,
      you should always attach an override to the variable to specify the
      particular package whose usability is being extended. For example,
      suppose you are building a development package that is extended to
      support wireless functionality. In this case, you would use the
      following::

         RRECOMMENDS:${PN}-dev += "wireless_package_name"

      In the
      example, the package name (``${PN}-dev``) must appear as it would in
      the :term:`PACKAGES` namespace before any renaming of the output package
      by classes such as :ref:`ref-classes-debian`.

      BitBake, which the OpenEmbedded build system uses, supports
      specifying versioned recommends. Although the syntax varies depending
      on the packaging format, BitBake hides these differences from you.
      Here is the general syntax to specify versions with the
      :term:`RRECOMMENDS` variable::

         RRECOMMENDS:${PN} = "package (operator version)"

      For ``operator``, you can specify the following:

      - =
      - <
      - >
      - <=
      - >=

      For example, the following sets up a recommend on version 1.2 or
      greater of the package ``foo``::

         RRECOMMENDS:${PN} = "foo (>= 1.2)"

   :term:`RREPLACES`
      A list of packages replaced by a package. The package manager uses
      this variable to determine which package should be installed to
      replace other package(s) during an upgrade. In order to also have the
      other package(s) removed at the same time, you must add the name of
      the other package to the :term:`RCONFLICTS` variable.

      As with all package-controlling variables, you must use this variable
      in conjunction with a package name override. Here is an example::

         RREPLACES:${PN} = "other_package_being_replaced"

      BitBake, which the OpenEmbedded build system uses, supports
      specifying versioned replacements. Although the syntax varies
      depending on the packaging format, BitBake hides these differences
      from you. Here is the general syntax to specify versions with the
      :term:`RREPLACES` variable::

         RREPLACES:${PN} = "package (operator version)"

      For ``operator``, you can specify the following:

      - =
      - <
      - >
      - <=
      - >=

      For example, the following sets up a replacement using version 1.2
      or greater of the package ``foo``::

          RREPLACES:${PN} = "foo (>= 1.2)"

   :term:`RSUGGESTS`
      A list of additional packages that you can suggest for installation
      by the package manager at the time a package is installed. Not all
      package managers support this functionality.

      As with all package-controlling variables, you must always use this
      variable in conjunction with a package name override. Here is an
      example::

         RSUGGESTS:${PN} = "useful_package another_package"

   :term:`RUST_CHANNEL`
      Specifies which version of Rust to build - "stable", "beta" or "nightly".
      The default value is "stable". Set this at your own risk, as values other
      than "stable" are not guaranteed to work at a given time.

   :term:`S`
      The location in the :term:`Build Directory` where
      unpacked recipe source code resides. By default, this directory is
      ``${``\ :term:`WORKDIR`\ ``}/${``\ :term:`BPN`\ ``}-${``\ :term:`PV`\ ``}``,
      where ``${BPN}`` is the base recipe name and ``${PV}`` is the recipe
      version. If the source tarball extracts the code to a directory named
      anything other than ``${BPN}-${PV}``, or if the source code is
      fetched from an SCM such as Git or Subversion, then you must set
      :term:`S` in the recipe so that the OpenEmbedded build system knows where
      to find the unpacked source.

      As an example, assume a :term:`Source Directory`
      top-level folder named ``poky`` and a default :term:`Build Directory` at
      ``poky/build``. In this case, the work directory the build system
      uses to keep the unpacked recipe for ``db`` is the following::

         poky/build/tmp/work/qemux86-poky-linux/db/5.1.19-r3/db-5.1.19

      The unpacked source code resides in the ``db-5.1.19`` folder.

      This next example assumes a Git repository. By default, Git
      repositories are cloned to ``${WORKDIR}/git`` during
      :ref:`ref-tasks-fetch`. Since this path is different
      from the default value of :term:`S`, you must set it specifically so the
      source can be located::

         SRC_URI = "git://path/to/repo.git;branch=main"
         S = "${WORKDIR}/git"

   :term:`SANITY_REQUIRED_UTILITIES`
      Specifies a list of command-line utilities that should be checked for
      during the initial sanity checking process when running BitBake. If
      any of the utilities are not installed on the build host, then
      BitBake immediately exits with an error.

   :term:`SANITY_TESTED_DISTROS`
      A list of the host distribution identifiers that the build system has
      been tested against. Identifiers consist of the host distributor ID
      followed by the release, as reported by the ``lsb_release`` tool or
      as read from ``/etc/lsb-release``. Separate the list items with
      explicit newline characters (``\n``). If :term:`SANITY_TESTED_DISTROS` is
      not empty and the current value of
      :term:`NATIVELSBSTRING` does not appear in the
      list, then the build system reports a warning that indicates the
      current host distribution has not been tested as a build host.

   :term:`SDK_ARCH`
      The target architecture for the SDK. Typically, you do not directly
      set this variable. Instead, use :term:`SDKMACHINE`.

   :term:`SDK_ARCHIVE_TYPE`
      Specifies the type of archive to create for the SDK. Valid values:

      - ``tar.xz`` (default)
      - ``zip``

      Only one archive type can be specified.

   :term:`SDK_BUILDINFO_FILE`
      When using the :ref:`ref-classes-image-buildinfo` class,
      specifies the file in the SDK to write the build information into. The
      default value is "``/buildinfo``".

   :term:`SDK_CUSTOM_TEMPLATECONF`
      When building the extensible SDK, if :term:`SDK_CUSTOM_TEMPLATECONF` is set to
      "1" and a ``conf/templateconf.cfg`` file exists in the :term:`Build Directory`
      (:term:`TOPDIR`) then this will be copied into the SDK.

   :term:`SDK_DEPLOY`
      The directory set up and used by the
      :ref:`populate_sdk_base <ref-classes-populate-sdk>` class to which the
      SDK is deployed. The :ref:`populate_sdk_base <ref-classes-populate-sdk>`
      class defines :term:`SDK_DEPLOY` as follows::

         SDK_DEPLOY = "${TMPDIR}/deploy/sdk"

   :term:`SDK_DIR`
      The parent directory used by the OpenEmbedded build system when
      creating SDK output. The
      :ref:`populate_sdk_base <ref-classes-populate-sdk-*>` class defines
      the variable as follows::

         SDK_DIR = "${WORKDIR}/sdk"

      .. note::

         The :term:`SDK_DIR` directory is a temporary directory as it is part of
         :term:`WORKDIR`. The final output directory is :term:`SDK_DEPLOY`.

   :term:`SDK_EXT_TYPE`
      Controls whether or not shared state artifacts are copied into the
      extensible SDK. The default value of "full" copies all of the
      required shared state artifacts into the extensible SDK. The value
      "minimal" leaves these artifacts out of the SDK.

      .. note::

         If you set the variable to "minimal", you need to ensure
         :term:`SSTATE_MIRRORS` is set in the SDK's configuration to enable the
         artifacts to be fetched as needed.

   :term:`SDK_HOST_MANIFEST`
      The manifest file for the host part of the SDK. This file lists all
      the installed packages that make up the host part of the SDK. The
      file contains package information on a line-per-package basis as
      follows::

         packagename packagearch version

      The :ref:`populate_sdk_base <ref-classes-populate-sdk-*>` class
      defines the manifest file as follows::

         SDK_HOST_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.host.manifest"

      The location is derived using the :term:`SDK_DEPLOY` and
      :term:`TOOLCHAIN_OUTPUTNAME` variables.

   :term:`SDK_INCLUDE_PKGDATA`
      When set to "1", specifies to include the packagedata for all recipes
      in the "world" target in the extensible SDK. Including this data
      allows the ``devtool search`` command to find these recipes in search
      results, as well as allows the ``devtool add`` command to map
      dependencies more effectively.

      .. note::

         Enabling the :term:`SDK_INCLUDE_PKGDATA`
         variable significantly increases build time because all of world
         needs to be built. Enabling the variable also slightly increases
         the size of the extensible SDK.

   :term:`SDK_INCLUDE_TOOLCHAIN`
      When set to "1", specifies to include the toolchain in the extensible
      SDK. Including the toolchain is useful particularly when
      :term:`SDK_EXT_TYPE` is set to "minimal" to keep
      the SDK reasonably small but you still want to provide a usable
      toolchain. For example, suppose you want to use the toolchain from an
      IDE or from other tools and you do not want to perform additional
      steps to install the toolchain.

      The :term:`SDK_INCLUDE_TOOLCHAIN` variable defaults to "0" if
      :term:`SDK_EXT_TYPE` is set to "minimal", and defaults to "1" if
      :term:`SDK_EXT_TYPE` is set to "full".

   :term:`SDK_NAME`
      The base name for SDK output files. The default value (as set in
      ``meta-poky/conf/distro/poky.conf``) is derived from the
      :term:`DISTRO`,
      :term:`TCLIBC`,
      :term:`SDKMACHINE`,
      :term:`IMAGE_BASENAME`,
      :term:`TUNE_PKGARCH`, and
      :term:`MACHINE` variables::

         SDK_NAME = "${DISTRO}-${TCLIBC}-${SDKMACHINE}-${IMAGE_BASENAME}-${TUNE_PKGARCH}-${MACHINE}"

   :term:`SDK_OS`
      Specifies the operating system for which the SDK will be built. The
      default value is the value of :term:`BUILD_OS`.

   :term:`SDK_OUTPUT`
      The location used by the OpenEmbedded build system when creating SDK
      output. The :ref:`populate_sdk_base <ref-classes-populate-sdk-*>`
      class defines the variable as follows::

         SDK_DIR = "${WORKDIR}/sdk"
         SDK_OUTPUT = "${SDK_DIR}/image"
         SDK_DEPLOY = "${DEPLOY_DIR}/sdk"

      .. note::

         The :term:`SDK_OUTPUT` directory is a temporary directory as it is part of
         :term:`WORKDIR` by way of :term:`SDK_DIR`. The final output directory is
         :term:`SDK_DEPLOY`.

   :term:`SDK_PACKAGE_ARCHS`
      Specifies a list of architectures compatible with the SDK machine.
      This variable is set automatically and should not normally be
      hand-edited. Entries are separated using spaces and listed in order
      of priority. The default value for :term:`SDK_PACKAGE_ARCHS` is "all any
      noarch ${SDK_ARCH}-${SDKPKGSUFFIX}".

   :term:`SDK_POSTPROCESS_COMMAND`
      Specifies a list of functions to call once the OpenEmbedded build
      system creates the SDK. You can specify functions separated by
      spaces:

         SDK_POSTPROCESS_COMMAND += "function"

      If you need to pass an SDK path to a command within a function, you
      can use ``${SDK_DIR}``, which points to the parent directory used by
      the OpenEmbedded build system when creating SDK output. See the
      :term:`SDK_DIR` variable for more information.

   :term:`SDK_PREFIX`
      The toolchain binary prefix used for
      :ref:`ref-classes-nativesdk` recipes. The
      OpenEmbedded build system uses the :term:`SDK_PREFIX` value to set the
      :term:`TARGET_PREFIX` when building
      ``nativesdk`` recipes. The default value is "${SDK_SYS}-".

   :term:`SDK_RECRDEP_TASKS`
      A list of shared state tasks added to the extensible SDK. By default,
      the following tasks are added:

      - :ref:`ref-tasks-populate_lic`
      - :ref:`ref-tasks-package_qa`
      - :ref:`ref-tasks-populate_sysroot`
      - :ref:`ref-tasks-deploy`

      Despite the default value of "" for the
      :term:`SDK_RECRDEP_TASKS` variable, the above four tasks are always added
      to the SDK. To specify tasks beyond these four, you need to use the
      :term:`SDK_RECRDEP_TASKS` variable (e.g. you are defining additional
      tasks that are needed in order to build
      :term:`SDK_TARGETS`).

   :term:`SDK_SYS`
      Specifies the system, including the architecture and the operating
      system, for which the SDK will be built.

      The OpenEmbedded build system automatically sets this variable based
      on :term:`SDK_ARCH`,
      :term:`SDK_VENDOR`, and
      :term:`SDK_OS`. You do not need to set the :term:`SDK_SYS`
      variable yourself.

   :term:`SDK_TARGET_MANIFEST`
      The manifest file for the target part of the SDK. This file lists all
      the installed packages that make up the target part of the SDK. The
      file contains package information on a line-per-package basis as
      follows::

         packagename packagearch version

      The :ref:`populate_sdk_base <ref-classes-populate-sdk-*>` class
      defines the manifest file as follows::

         SDK_TARGET_MANIFEST = "${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.target.manifest"

      The location is derived using the :term:`SDK_DEPLOY` and
      :term:`TOOLCHAIN_OUTPUTNAME` variables.

   :term:`SDK_TARGETS`
      A list of targets to install from shared state as part of the
      standard or extensible SDK installation. The default value is "${PN}"
      (i.e. the image from which the SDK is built).

      The :term:`SDK_TARGETS` variable is an internal variable and typically
      would not be changed.

   :term:`SDK_TITLE`
      The title to be printed when running the SDK installer. By default,
      this title is based on the :term:`DISTRO_NAME` or
      :term:`DISTRO` variable and is set in the
      :ref:`populate_sdk_base <ref-classes-populate-sdk-*>` class as
      follows::

         SDK_TITLE ??= "${@d.getVar('DISTRO_NAME') or d.getVar('DISTRO')} SDK"

      For the default distribution "poky",
      :term:`SDK_TITLE` is set to "Poky (Yocto Project Reference Distro)".

      For information on how to change this default title, see the
      ":ref:`sdk-manual/appendix-customizing:changing the extensible sdk installer title`"
      section in the Yocto Project Application Development and the
      Extensible Software Development Kit (eSDK) manual.

   :term:`SDK_TOOLCHAIN_LANGS`
      Specifies programming languages to support in the SDK, as a
      space-separated list. Currently supported items are ``rust`` and ``go``.

   :term:`SDK_UPDATE_URL`
      An optional URL for an update server for the extensible SDK. If set,
      the value is used as the default update server when running
      ``devtool sdk-update`` within the extensible SDK.

   :term:`SDK_VENDOR`
      Specifies the name of the SDK vendor.

   :term:`SDK_VERSION`
      Specifies the version of the SDK. The Poky distribution configuration file
      (``/meta-poky/conf/distro/poky.conf``) sets the default
      :term:`SDK_VERSION` as follows::

         SDK_VERSION = "${@d.getVar('DISTRO_VERSION').replace('snapshot-${METADATA_REVISION}', 'snapshot')}"

      For additional information, see the
      :term:`DISTRO_VERSION` and
      :term:`METADATA_REVISION` variables.

   :term:`SDK_ZIP_OPTIONS`
      Specifies extra options to pass to the ``zip`` command when zipping the SDK
      (i.e. when :term:`SDK_ARCHIVE_TYPE` is set to "zip"). The default value is
      "-y".

   :term:`SDKEXTPATH`
      The default installation directory for the Extensible SDK. By
      default, this directory is based on the :term:`DISTRO`
      variable and is set in the
      :ref:`populate_sdk_base <ref-classes-populate-sdk-*>` class as
      follows::

         SDKEXTPATH ??= "~/${@d.getVar('DISTRO')}_sdk"

      For the
      default distribution "poky", the :term:`SDKEXTPATH` is set to "poky_sdk".

      For information on how to change this default directory, see the
      ":ref:`sdk-manual/appendix-customizing:changing the default sdk installation directory`"
      section in the Yocto Project Application Development and the
      Extensible Software Development Kit (eSDK) manual.

   :term:`SDKIMAGE_FEATURES`
      Equivalent to :term:`IMAGE_FEATURES`. However, this variable applies to
      the SDK generated from an image using the following command::

         $ bitbake -c populate_sdk imagename

   :term:`SDKMACHINE`
      The machine for which the SDK is built. In other words, the SDK is built
      such that it runs on the target you specify with the :term:`SDKMACHINE`
      value. The value points to a corresponding ``.conf`` file under
      ``conf/machine-sdk/`` in the enabled layers, for example ``aarch64``,
      ``i586``, ``i686``, ``ppc64``, ``ppc64le``, and ``x86_64`` are
      :oe_git:`available in OpenEmbedded-Core </openembedded-core/tree/meta/conf/machine-sdk>`.

      The variable defaults to :term:`BUILD_ARCH` so that SDKs are built for the
      architecture of the build machine.

      .. note::

         You cannot set the :term:`SDKMACHINE`
         variable in your distribution configuration file. If you do, the
         configuration will not take effect.

   :term:`SDKPATH`
      Defines the path used to collect the SDK components and build the
      installer.

   :term:`SDKPATHINSTALL`
      Defines the path offered to the user for installation of the SDK that
      is generated by the OpenEmbedded build system. The path appears as
      the default location for installing the SDK when you run the SDK's
      installation script. You can override the offered path when you run
      the script.

   :term:`SDKTARGETSYSROOT`
      The full path to the sysroot used for cross-compilation within an SDK
      as it will be when installed into the default
      :term:`SDKPATHINSTALL`.

   :term:`SECTION`
      The section in which packages should be categorized. Package
      management utilities can make use of this variable.

   :term:`SELECTED_OPTIMIZATION`
      Specifies the optimization flags passed to the C compiler when
      building for the target. The flags are passed through the default
      value of the :term:`TARGET_CFLAGS` variable.

      The :term:`SELECTED_OPTIMIZATION` variable takes the value of
      :term:`FULL_OPTIMIZATION` unless :term:`DEBUG_BUILD` = "1", in which
      case the value of :term:`DEBUG_OPTIMIZATION` is used.

   :term:`SERIAL_CONSOLES`
      Defines a serial console (TTY) to enable using
      :wikipedia:`getty <Getty_(Unix)>`. Provide a value that specifies the
      baud rate followed by the TTY device name separated by a semicolon.
      Use spaces to separate multiple devices::

         SERIAL_CONSOLES = "115200;ttyS0 115200;ttyS1"

   :term:`SETUPTOOLS_BUILD_ARGS`
      When used by recipes that inherit the :ref:`ref-classes-setuptools3`
      class, this variable can be used to specify additional arguments to be
      passed to ``setup.py build`` in the ``setuptools3_do_compile()`` task.

   :term:`SETUPTOOLS_INSTALL_ARGS`
      When used by recipes that inherit the :ref:`ref-classes-setuptools3`
      class, this variable can be used to specify additional arguments to be
      passed to ``setup.py install`` in the ``setuptools3_do_install()`` task.

   :term:`SETUPTOOLS_SETUP_PATH`
      When used by recipes that inherit the :ref:`ref-classes-setuptools3`
      class, this variable should be used to specify the directory in which
      the ``setup.py`` file is located if it is not at the root of the source
      tree (as specified by :term:`S`). For example, in a recipe where the
      sources are fetched from a Git repository and ``setup.py`` is in a
      ``python/pythonmodule`` subdirectory, you would have this::

         S = "${WORKDIR}/git"
         SETUPTOOLS_SETUP_PATH = "${S}/python/pythonmodule"

   :term:`SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS`
      A list of recipe dependencies that should not be used to determine
      signatures of tasks from one recipe when they depend on tasks from
      another recipe. For example::

         SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS += "intone->mplayer2"

      In the previous example, ``intone`` depends on ``mplayer2``.

      You can use the special token ``"*"`` on the left-hand side of the
      dependency to match all recipes except the one on the right-hand
      side. Here is an example::

         SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS += "*->quilt-native"

      In the previous example, all recipes except ``quilt-native`` ignore
      task signatures from the ``quilt-native`` recipe when determining
      their task signatures.

      Use of this variable is one mechanism to remove dependencies that
      affect task signatures and thus force rebuilds when a recipe changes.

      .. note::

         If you add an inappropriate dependency for a recipe relationship,
         the software might break during runtime if the interface of the
         second recipe was changed after the first recipe had been built.

   :term:`SIGGEN_EXCLUDERECIPES_ABISAFE`
      A list of recipes that are completely stable and will never change.
      The ABI for the recipes in the list are presented by output from the
      tasks run to build the recipe. Use of this variable is one way to
      remove dependencies from one recipe on another that affect task
      signatures and thus force rebuilds when the recipe changes.

      .. note::

         If you add an inappropriate variable to this list, the software
         might break at runtime if the interface of the recipe was changed
         after the other had been built.

   :term:`SITEINFO_BITS`
      Specifies the number of bits for the target system CPU. The value
      should be either "32" or "64".

   :term:`SITEINFO_ENDIANNESS`
      Specifies the endian byte order of the target system. The value
      should be either "le" for little-endian or "be" for big-endian.

   :term:`SKIP_FILEDEPS`
      Enables removal of all files from the "Provides" section of an RPM
      package. Removal of these files is required for packages containing
      prebuilt binaries and libraries such as ``libstdc++`` and ``glibc``.

      To enable file removal, set the variable to "1" in your
      ``conf/local.conf`` configuration file in your:
      :term:`Build Directory`::

         SKIP_FILEDEPS = "1"

   :term:`SKIP_RECIPE`
      Used to prevent the OpenEmbedded build system from building a given
      recipe. Specify the :term:`PN` value as a variable flag (``varflag``)
      and provide a reason, which will be reported when attempting to
      build the recipe.

      To prevent a recipe from being built, use the :term:`SKIP_RECIPE`
      variable in your ``local.conf`` file or distribution configuration.
      Here is an example which prevents ``myrecipe`` from being built::

         SKIP_RECIPE[myrecipe] = "Not supported by our organization."

   :term:`SOC_FAMILY`
      A colon-separated list grouping together machines based upon the same
      family of SoC (System On Chip). You typically set this variable in a
      common ``.inc`` file that you include in the configuration files of all
      the machines.

      .. note::

         You must include ``conf/machine/include/soc-family.inc`` for this
         variable to appear in :term:`MACHINEOVERRIDES`.

   :term:`SOLIBS`
      Defines the suffix for shared libraries used on the target platform.
      By default, this suffix is ".so.*" for all Linux-based systems and is
      defined in the ``meta/conf/bitbake.conf`` configuration file.

      You will see this variable referenced in the default values of
      ``FILES:${PN}``.

   :term:`SOLIBSDEV`
      Defines the suffix for the development symbolic link (symlink) for
      shared libraries on the target platform. By default, this suffix is
      ".so" for Linux-based systems and is defined in the
      ``meta/conf/bitbake.conf`` configuration file.

      You will see this variable referenced in the default values of
      ``FILES:${PN}-dev``.

   :term:`SOURCE_DATE_EPOCH`
      This defines a date expressed in number of seconds since
      the UNIX EPOCH (01 Jan 1970 00:00:00 UTC), which is used by
      multiple build systems to force a timestamp in built binaries.
      Many upstream projects already support this variable.

      You will find more details in the `official specifications
      <https://reproducible-builds.org/specs/source-date-epoch/>`__.

      A value for each recipe is computed from the sources by
      :oe_git:`meta/lib/oe/reproducible.py </openembedded-core/tree/meta/lib/oe/reproducible.py>`.

      If a recipe wishes to override the default behavior, it should set its
      own :term:`SOURCE_DATE_EPOCH` value::

          SOURCE_DATE_EPOCH = "1613559011"

   :term:`SOURCE_MIRROR_FETCH`
      When you are fetching files to create a mirror of sources (i.e.
      creating a source mirror), setting :term:`SOURCE_MIRROR_FETCH` to "1" in
      your ``local.conf`` configuration file ensures the source for all
      recipes are fetched regardless of whether or not a recipe is
      compatible with the configuration. A recipe is considered
      incompatible with the currently configured machine when either or
      both the :term:`COMPATIBLE_MACHINE`
      variable and :term:`COMPATIBLE_HOST` variables
      specify compatibility with a machine other than that of the current
      machine or host.

      .. note::

         Do not set the :term:`SOURCE_MIRROR_FETCH`
         variable unless you are creating a source mirror. In other words,
         do not set the variable during a normal build.

   :term:`SOURCE_MIRROR_URL`
      Defines your own :term:`PREMIRRORS` from which to
      first fetch source before attempting to fetch from the upstream
      specified in :term:`SRC_URI`.

      To use this variable, you must globally inherit the
      :ref:`ref-classes-own-mirrors` class and then provide
      the URL to your mirrors. Here is the general syntax::

         INHERIT += "own-mirrors"
         SOURCE_MIRROR_URL = "http://example.com/my_source_mirror"

      .. note::

         You can specify only a single URL in :term:`SOURCE_MIRROR_URL`.

   :term:`SPDX_ARCHIVE_PACKAGED`
      This option allows to add to :term:`SPDX` output compressed archives
      of the files in the generated target packages.

      Such archives are available in
      ``tmp/deploy/spdx/MACHINE/packages/packagename.tar.zst``
      under the :term:`Build Directory`.

      Enable this option as follows::

         SPDX_ARCHIVE_PACKAGED = "1"

      According to our tests on release 4.1 "langdale", building
      ``core-image-minimal`` for the ``qemux86-64`` machine, enabling this
      option multiplied the size of the ``tmp/deploy/spdx`` directory by a
      factor of 13 (+1.6 GiB for this image), compared to just using the
      :ref:`ref-classes-create-spdx` class with no option.

      Note that this option doesn't increase the size of :term:`SPDX`
      files in ``tmp/deploy/images/MACHINE``.

   :term:`SPDX_ARCHIVE_SOURCES`
      This option allows to add to :term:`SPDX` output compressed archives
      of the sources for packages installed on the target. It currently
      only works when :term:`SPDX_INCLUDE_SOURCES` is set.

      This is one way of fulfilling "source code access" license
      requirements.

      Such source archives are available in
      ``tmp/deploy/spdx/MACHINE/recipes/recipe-packagename.tar.zst``
      under the :term:`Build Directory`.

      Enable this option as follows::

         SPDX_INCLUDE_SOURCES = "1"
         SPDX_ARCHIVE_SOURCES = "1"

      According to our tests on release 4.1 "langdale", building
      ``core-image-minimal`` for the ``qemux86-64`` machine, enabling
      these options multiplied the size of the ``tmp/deploy/spdx``
      directory by a factor of 11 (+1.4 GiB for this image),
      compared to just using the :ref:`ref-classes-create-spdx`
      class with no option.

      Note that using this option only marginally increases the size
      of the :term:`SPDX` output in ``tmp/deploy/images/MACHINE/``
      (+ 0.07\% with the tested image), compared to just enabling
      :term:`SPDX_INCLUDE_SOURCES`.

   :term:`SPDX_CUSTOM_ANNOTATION_VARS`
      This option allows to associate `SPDX annotations
      <https://spdx.github.io/spdx-spec/v2.3/annotations/>`__ to a recipe,
      using the values of variables in the recipe::

         ANNOTATION1 = "First annotation for recipe"
         ANNOTATION2 = "Second annotation for recipe"
         SPDX_CUSTOM_ANNOTATION_VARS = "ANNOTATION1 ANNOTATION2"

      This will add a new block to the recipe ``.sdpx.json`` output::

         "annotations": [
           {
             "annotationDate": "2023-04-18T08:32:12Z",
             "annotationType": "OTHER",
             "annotator": "Tool: oe-spdx-creator - 1.0",
             "comment": "ANNOTATION1=First annotation for recipe"
           },
           {
             "annotationDate": "2023-04-18T08:32:12Z",
             "annotationType": "OTHER",
             "annotator": "Tool: oe-spdx-creator - 1.0",
             "comment": "ANNOTATION2=Second annotation for recipe"
           }
         ],

   :term:`SPDX_INCLUDE_SOURCES`
      This option allows to add a description of the source files used to build
      the host tools and the target packages, to the ``spdx.json`` files in
      ``tmp/deploy/spdx/MACHINE/recipes/`` under the :term:`Build Directory`.
      As a consequence, the ``spdx.json`` files under the ``by-namespace`` and
      ``packages`` subdirectories in ``tmp/deploy/spdx/MACHINE`` are also
      modified to include references to such source file descriptions.

      Enable this option as follows::

         SPDX_INCLUDE_SOURCES = "1"

      According to our tests on release 4.1 "langdale", building
      ``core-image-minimal`` for the ``qemux86-64`` machine, enabling
      this option multiplied the total size of the ``tmp/deploy/spdx``
      directory by a factor of 3  (+291 MiB for this image),
      and the size of the ``IMAGE-MACHINE.spdx.tar.zst`` in
      ``tmp/deploy/images/MACHINE`` by a factor of 130 (+15 MiB for this
      image), compared to just using the :ref:`ref-classes-create-spdx` class
      with no option.

   :term:`SPDX_NAMESPACE_PREFIX`
      This option could be used in order to change the prefix of ``spdxDocument``
      and the prefix of ``documentNamespace``. It is set by default to
      ``http://spdx.org/spdxdoc``.

   :term:`SPDX_PRETTY`
      This option makes the SPDX output more human-readable, using
      identation and newlines, instead of the default output in a
      single line::

         SPDX_PRETTY = "1"

      The generated SPDX files are approximately 20% bigger, but
      this option is recommended if you want to inspect the SPDX
      output files with a text editor.

   :term:`SPDXLICENSEMAP`
      Maps commonly used license names to their SPDX counterparts found in
      ``meta/files/common-licenses/``. For the default :term:`SPDXLICENSEMAP`
      mappings, see the ``meta/conf/licenses.conf`` file.

      For additional information, see the :term:`LICENSE`
      variable.

   :term:`SPECIAL_PKGSUFFIX`
      A list of prefixes for :term:`PN` used by the OpenEmbedded
      build system to create variants of recipes or packages. The list
      specifies the prefixes to strip off during certain circumstances such
      as the generation of the :term:`BPN` variable.

   :term:`SPL_BINARY`
      The file type for the Secondary Program Loader (SPL). Some devices
      use an SPL from which to boot (e.g. the BeagleBone development
      board). For such cases, you can declare the file type of the SPL
      binary in the ``u-boot.inc`` include file, which is used in the
      U-Boot recipe.

      The SPL file type is set to "null" by default in the ``u-boot.inc``
      file as follows::

         # Some versions of u-boot build an SPL (Second Program Loader) image that
         # should be packaged along with the u-boot binary as well as placed in the
         # deploy directory. For those versions they can set the following variables
         # to allow packaging the SPL.
         SPL_BINARY ?= ""
         SPL_BINARYNAME ?= "${@os.path.basename(d.getVar("SPL_BINARY"))}"
         SPL_IMAGE ?= "${SPL_BINARYNAME}-${MACHINE}-${PV}-${PR}"
         SPL_SYMLINK ?= "${SPL_BINARYNAME}-${MACHINE}"

      The :term:`SPL_BINARY` variable helps form
      various ``SPL_*`` variables used by the OpenEmbedded build system.

      See the BeagleBone machine configuration example in the
      ":ref:`dev-manual/layers:adding a layer using the \`\`bitbake-layers\`\` script`"
      section in the Yocto Project Board Support Package Developer's Guide
      for additional information.

   :term:`SPL_MKIMAGE_DTCOPTS`
      Options for the device tree compiler passed to ``mkimage -D`` feature
      while creating a FIT image with the :ref:`ref-classes-uboot-sign`
      class. If :term:`SPL_MKIMAGE_DTCOPTS` is not set then the
      :ref:`ref-classes-uboot-sign` class will not pass the ``-D`` option
      to ``mkimage``.

      The default value is set to "" by the :ref:`ref-classes-uboot-config`
      class.

   :term:`SPL_SIGN_ENABLE`
      Enable signing of the U-Boot FIT image. The default value is "0".
      This variable is used by the :ref:`ref-classes-uboot-sign` class.

   :term:`SPL_SIGN_KEYDIR`
      Location of the directory containing the RSA key and certificate used for
      signing the U-Boot FIT image, used by the :ref:`ref-classes-uboot-sign`
      class.

   :term:`SPL_SIGN_KEYNAME`
      The name of keys used by the :ref:`ref-classes-kernel-fitimage` class
      for signing U-Boot FIT image stored in the :term:`SPL_SIGN_KEYDIR`
      directory. If we have for example a ``dev.key`` key and a ``dev.crt``
      certificate stored in the :term:`SPL_SIGN_KEYDIR` directory, you will
      have to set :term:`SPL_SIGN_KEYNAME` to ``dev``.

   :term:`SPLASH`
      This variable, used by the :ref:`ref-classes-image` class, allows
      to choose splashscreen applications. Set it to the names of packages
      for such applications to use. This variable is set by default to
      ``psplash``.

   :term:`SPLASH_IMAGES`
      This variable, used by the ``psplash`` recipe, allows to customize
      the default splashscreen image.

      Specified images in PNG format are converted to ``.h`` files by the recipe,
      and are included in the ``psplash`` binary, so you won't find them in
      the root filesystem.

      To make such a change, it is recommended to customize the
      ``psplash`` recipe in a custom layer. Here is an example structure for
      an ``ACME`` board::

          meta-acme/recipes-core/psplash
          ├── files
          │   └── logo-acme.png
          └── psplash_%.bbappend

      And here are the contents of the ``psplash_%.bbappend`` file in
      this example::

          SPLASH_IMAGES = "file://logo-acme.png;outsuffix=default"
          FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

      You could even add specific configuration options for ``psplash``,
      for example::

          EXTRA_OECONF += "--disable-startup-msg --enable-img-fullscreen"

      For information on append files, see the
      ":ref:`dev-manual/layers:appending other layers metadata with your layer`"
      section.

   :term:`SRCREV_FORMAT`
      See :term:`bitbake:SRCREV_FORMAT` in the BitBake manual.

   :term:`SRC_URI`

      See the BitBake manual for the initial description for this variable:
      :term:`bitbake:SRC_URI`.

      The following features are added by OpenEmbedded and the Yocto Project.

      There are standard and recipe-specific options. Here are standard ones:

      -  ``apply`` --- whether to apply the patch or not. The default
         action is to apply the patch.

      -  ``striplevel`` --- which striplevel to use when applying the
         patch. The default level is 1.

      -  ``patchdir`` --- specifies the directory in which the patch should
         be applied. The default is ``${``\ :term:`S`\ ``}``.

      Here are options specific to recipes building code from a revision
      control system:

      -  ``mindate`` --- apply the patch only if
         :term:`SRCDATE` is equal to or greater than
         ``mindate``.

      -  ``maxdate`` --- apply the patch only if :term:`SRCDATE` is not later
         than ``maxdate``.

      -  ``minrev`` --- apply the patch only if :term:`SRCREV` is equal to or
         greater than ``minrev``.

      -  ``maxrev`` --- apply the patch only if :term:`SRCREV` is not later
         than ``maxrev``.

      -  ``rev`` --- apply the patch only if :term:`SRCREV` is equal to
         ``rev``.

      -  ``notrev`` --- apply the patch only if :term:`SRCREV` is not equal to
         ``rev``.

      .. note::

         If you want the build system to pick up files specified through
         a :term:`SRC_URI` statement from your append file, you need to be
         sure to extend the :term:`FILESPATH` variable by also using the
         :term:`FILESEXTRAPATHS` variable from within your append file.

   :term:`SRC_URI_OVERRIDES_PACKAGE_ARCH`
      By default, the OpenEmbedded build system automatically detects
      whether :term:`SRC_URI` contains files that are machine-specific. If so,
      the build system automatically changes :term:`PACKAGE_ARCH`. Setting this
      variable to "0" disables this behavior.

   :term:`SRCDATE`
      The date of the source code used to build the package. This variable
      applies only if the source was fetched from a Source Code Manager
      (SCM).

   :term:`SRCPV`
      Returns the version string of the current package. This string is
      used to help define the value of :term:`PV`.

      The :term:`SRCPV` variable is defined in the ``meta/conf/bitbake.conf``
      configuration file in the :term:`Source Directory` as
      follows::

         SRCPV = "${@bb.fetch2.get_srcrev(d)}"

      Recipes that need to define :term:`PV` do so with the help of the
      :term:`SRCPV`. For example, the ``ofono`` recipe (``ofono_git.bb``)
      located in ``meta/recipes-connectivity`` in the Source Directory
      defines :term:`PV` as follows::

         PV = "0.12-git${SRCPV}"

   :term:`SRCREV`
      The revision of the source code used to build the package. This
      variable applies to Subversion, Git, Mercurial, and Bazaar only. Note
      that if you want to build a fixed revision and you want to avoid
      performing a query on the remote repository every time BitBake parses
      your recipe, you should specify a :term:`SRCREV` that is a full revision
      identifier (e.g. the full SHA hash in git) and not just a tag.

      .. note::

         For information on limitations when inheriting the latest revision
         of software using :term:`SRCREV`, see the :term:`AUTOREV` variable
         description and the
         ":ref:`dev-manual/packages:automatically incrementing a package version number`"
         section, which is in the Yocto Project Development Tasks Manual.

   :term:`SRCTREECOVEREDTASKS`
      A list of tasks that are typically not relevant (and therefore skipped)
      when building using the :ref:`ref-classes-externalsrc`
      class. The default value as set in that class file is the set of tasks
      that are rarely needed when using external source::

         SRCTREECOVEREDTASKS ?= "do_patch do_unpack do_fetch"

      The notable exception is when processing external kernel source as
      defined in the :ref:`ref-classes-kernel-yocto` class file (formatted for
      aesthetics)::

         SRCTREECOVEREDTASKS += "\
           do_validate_branches \
           do_kernel_configcheck \
           do_kernel_checkout \
           do_fetch \
           do_unpack \
           do_patch \
         "

      See the associated :term:`EXTERNALSRC` and :term:`EXTERNALSRC_BUILD`
      variables for more information.

   :term:`SSTATE_DIR`
      The directory for the shared state cache.

   :term:`SSTATE_EXCLUDEDEPS_SYSROOT`
      This variable allows to specify indirect dependencies to exclude
      from sysroots, for example to avoid the situations when a dependency on
      any ``-native`` recipe will pull in all dependencies of that recipe
      in the recipe sysroot. This behaviour might not always be wanted,
      for example when that ``-native`` recipe depends on build tools
      that are not relevant for the current recipe.

      This way, irrelevant dependencies are ignored, which could have
      prevented the reuse of prebuilt artifacts stored in the Shared
      State Cache.

      :term:`SSTATE_EXCLUDEDEPS_SYSROOT` is evaluated as two regular
      expressions of recipe and dependency to ignore. An example
      is the rule in :oe_git:`meta/conf/layer.conf </openembedded-core/tree/meta/conf/layer.conf>`::

         # Nothing needs to depend on libc-initial
         # base-passwd/shadow-sysroot don't need their dependencies
         SSTATE_EXCLUDEDEPS_SYSROOT += "\
             .*->.*-initial.* \
             .*(base-passwd|shadow-sysroot)->.* \
         "

      The ``->`` substring represents the dependency between
      the two regular expressions.

   :term:`SSTATE_MIRROR_ALLOW_NETWORK`
      If set to "1", allows fetches from mirrors that are specified in
      :term:`SSTATE_MIRRORS` to work even when
      fetching from the network is disabled by setting :term:`BB_NO_NETWORK` to
      "1". Using the :term:`SSTATE_MIRROR_ALLOW_NETWORK` variable is useful if
      you have set :term:`SSTATE_MIRRORS` to point to an internal server for
      your shared state cache, but you want to disable any other fetching
      from the network.

   :term:`SSTATE_MIRRORS`
      Configures the OpenEmbedded build system to search other mirror
      locations for prebuilt cache data objects before building out the
      data. This variable works like fetcher :term:`MIRRORS`
      and :term:`PREMIRRORS` and points to the cache
      locations to check for the shared state (sstate) objects.

      You can specify a filesystem directory or a remote URL such as HTTP
      or FTP. The locations you specify need to contain the shared state
      cache (sstate-cache) results from previous builds. The sstate-cache
      you point to can also be from builds on other machines.

      When pointing to sstate build artifacts on another machine that uses
      a different GCC version for native builds, you must configure
      :term:`SSTATE_MIRRORS` with a regular expression that maps local search
      paths to server paths. The paths need to take into account
      :term:`NATIVELSBSTRING` set by the :ref:`ref-classes-uninative` class.
      For example, the following maps the local search path ``universal-4.9``
      to the server-provided path server_url_sstate_path::

         SSTATE_MIRRORS ?= "file://universal-4.9/(.*) https://server_url_sstate_path/universal-4.8/\1"

      If a mirror uses the same structure as
      :term:`SSTATE_DIR`, you need to add "PATH" at the
      end as shown in the examples below. The build system substitutes the
      correct path within the directory structure::

         SSTATE_MIRRORS ?= "\
             file://.* https://someserver.tld/share/sstate/PATH;downloadfilename=PATH \
             file://.* file:///some-local-dir/sstate/PATH"

      The Yocto Project actually shares the cache data objects built by its
      autobuilder::

         SSTATE_MIRRORS ?= "file://.* http://cdn.jsdelivr.net/yocto/sstate/all/PATH;downloadfilename=PATH"

      As such binary artifacts are built for the generic QEMU machines
      supported by the various Poky releases, they are less likely to be
      reusable in real projects building binaries optimized for a specific
      CPU family.

   :term:`SSTATE_SCAN_FILES`
      Controls the list of files the OpenEmbedded build system scans for
      hardcoded installation paths. The variable uses a space-separated
      list of filenames (not paths) with standard wildcard characters
      allowed.

      During a build, the OpenEmbedded build system creates a shared state
      (sstate) object during the first stage of preparing the sysroots.
      That object is scanned for hardcoded paths for original installation
      locations. The list of files that are scanned for paths is controlled
      by the :term:`SSTATE_SCAN_FILES` variable. Typically, recipes add files
      they want to be scanned to the value of :term:`SSTATE_SCAN_FILES` rather
      than the variable being comprehensively set. The
      :ref:`ref-classes-sstate` class specifies the default list of files.

      For details on the process, see the :ref:`ref-classes-staging` class.

   :term:`STAGING_BASE_LIBDIR_NATIVE`
      Specifies the path to the ``/lib`` subdirectory of the sysroot
      directory for the build host.

   :term:`STAGING_BASELIBDIR`
      Specifies the path to the ``/lib`` subdirectory of the sysroot
      directory for the target for which the current recipe is being built
      (:term:`STAGING_DIR_HOST`).

   :term:`STAGING_BINDIR`
      Specifies the path to the ``/usr/bin`` subdirectory of the sysroot
      directory for the target for which the current recipe is being built
      (:term:`STAGING_DIR_HOST`).

   :term:`STAGING_BINDIR_CROSS`
      Specifies the path to the directory containing binary configuration
      scripts. These scripts provide configuration information for other
      software that wants to make use of libraries or include files
      provided by the software associated with the script.

      .. note::

         This style of build configuration has been largely replaced by
         ``pkg-config``. Consequently, if ``pkg-config`` is supported by the
         library to which you are linking, it is recommended you use
         ``pkg-config`` instead of a provided configuration script.

   :term:`STAGING_BINDIR_NATIVE`
      Specifies the path to the ``/usr/bin`` subdirectory of the sysroot
      directory for the build host.

   :term:`STAGING_DATADIR`
      Specifies the path to the ``/usr/share`` subdirectory of the sysroot
      directory for the target for which the current recipe is being built
      (:term:`STAGING_DIR_HOST`).

   :term:`STAGING_DATADIR_NATIVE`
      Specifies the path to the ``/usr/share`` subdirectory of the sysroot
      directory for the build host.

   :term:`STAGING_DIR`
      Helps construct the ``recipe-sysroots`` directory, which is used
      during packaging.

      For information on how staging for recipe-specific sysroots occurs,
      see the :ref:`ref-tasks-populate_sysroot`
      task, the ":ref:`sdk-manual/extensible:sharing files between recipes`"
      section in the Yocto Project Development Tasks Manual, the
      ":ref:`overview-manual/concepts:configuration, compilation, and staging`"
      section in the Yocto Project Overview and Concepts Manual, and the
      :term:`SYSROOT_DIRS` variable.

      .. note::

         Recipes should never write files directly under the :term:`STAGING_DIR`
         directory because the OpenEmbedded build system manages the
         directory automatically. Instead, files should be installed to
         ``${``\ :term:`D`\ ``}`` within your recipe's :ref:`ref-tasks-install`
         task and then the OpenEmbedded build system will stage a subset of
         those files into the sysroot.

   :term:`STAGING_DIR_HOST`
      Specifies the path to the sysroot directory for the system on which
      the component is built to run (the system that hosts the component).
      For most recipes, this sysroot is the one in which that recipe's
      :ref:`ref-tasks-populate_sysroot` task copies
      files. Exceptions include ``-native`` recipes, where the
      :ref:`ref-tasks-populate_sysroot` task instead uses
      :term:`STAGING_DIR_NATIVE`. Depending on
      the type of recipe and the build target, :term:`STAGING_DIR_HOST` can
      have the following values:

      -  For recipes building for the target machine, the value is
         "${:term:`STAGING_DIR`}/${:term:`MACHINE`}".

      -  For native recipes building for the build host, the value is empty
         given the assumption that when building for the build host, the
         build host's own directories should be used.

         .. note::

            ``-native`` recipes are not installed into host paths like such
            as ``/usr``. Rather, these recipes are installed into
            :term:`STAGING_DIR_NATIVE`. When compiling ``-native`` recipes,
            standard build environment variables such as
            :term:`CPPFLAGS` and
            :term:`CFLAGS` are set up so that both host paths
            and :term:`STAGING_DIR_NATIVE` are searched for libraries and
            headers using, for example, GCC's ``-isystem`` option.

            Thus, the emphasis is that the ``STAGING_DIR*`` variables
            should be viewed as input variables by tasks such as
            :ref:`ref-tasks-configure`,
            :ref:`ref-tasks-compile`, and
            :ref:`ref-tasks-install`. Having the real system
            root correspond to :term:`STAGING_DIR_HOST` makes conceptual sense
            for ``-native`` recipes, as they make use of host headers and
            libraries.

      Check :term:`RECIPE_SYSROOT` and :term:`RECIPE_SYSROOT_NATIVE`.

   :term:`STAGING_DIR_NATIVE`
      Specifies the path to the sysroot directory used when building
      components that run on the build host itself.

      The default value is ``"${RECIPE_SYSROOT_NATIVE}"``,
      check :term:`RECIPE_SYSROOT_NATIVE`.

   :term:`STAGING_DIR_TARGET`
      Specifies the path to the sysroot used for the system for which the
      component generates code. For components that do not generate code,
      which is the majority, :term:`STAGING_DIR_TARGET` is set to match
      :term:`STAGING_DIR_HOST`.

      Some recipes build binaries that can run on the target system but those
      binaries in turn generate code for another different system (e.g.
      :ref:`ref-classes-cross-canadian` recipes). Using terminology from GNU,
      the primary system is referred to as the "HOST" and the secondary, or
      different, system is referred to as the "TARGET". Thus, the binaries
      run on the "HOST" system and generate binaries for the "TARGET"
      system. The :term:`STAGING_DIR_HOST` variable points to the sysroot used
      for the "HOST" system, while :term:`STAGING_DIR_TARGET` points to the
      sysroot used for the "TARGET" system.

   :term:`STAGING_ETCDIR_NATIVE`
      Specifies the path to the ``/etc`` subdirectory of the sysroot
      directory for the build host.

   :term:`STAGING_EXECPREFIXDIR`
      Specifies the path to the ``/usr`` subdirectory of the sysroot
      directory for the target for which the current recipe is being built
      (:term:`STAGING_DIR_HOST`).

   :term:`STAGING_INCDIR`
      Specifies the path to the ``/usr/include`` subdirectory of the
      sysroot directory for the target for which the current recipe being
      built (:term:`STAGING_DIR_HOST`).

   :term:`STAGING_INCDIR_NATIVE`
      Specifies the path to the ``/usr/include`` subdirectory of the
      sysroot directory for the build host.

   :term:`STAGING_KERNEL_BUILDDIR`
      Points to the directory containing the kernel build artifacts.
      Recipes building software that needs to access kernel build artifacts
      (e.g. ``systemtap-uprobes``) can look in the directory specified with
      the :term:`STAGING_KERNEL_BUILDDIR` variable to find these artifacts
      after the kernel has been built.

   :term:`STAGING_KERNEL_DIR`
      The directory with kernel headers that are required to build
      out-of-tree modules.

   :term:`STAGING_LIBDIR`
      Specifies the path to the ``/usr/lib`` subdirectory of the sysroot
      directory for the target for which the current recipe is being built
      (:term:`STAGING_DIR_HOST`).

   :term:`STAGING_LIBDIR_NATIVE`
      Specifies the path to the ``/usr/lib`` subdirectory of the sysroot
      directory for the build host.

   :term:`STAMP`
      Specifies the base path used to create recipe stamp files. The path
      to an actual stamp file is constructed by evaluating this string and
      then appending additional information. Currently, the default
      assignment for :term:`STAMP` as set in the ``meta/conf/bitbake.conf``
      file is::

         STAMP = "${STAMPS_DIR}/${MULTIMACH_TARGET_SYS}/${PN}/${EXTENDPE}${PV}-${PR}"

      For information on how BitBake uses stamp files to determine if a
      task should be rerun, see the
      ":ref:`overview-manual/concepts:stamp files and the rerunning of tasks`"
      section in the Yocto Project Overview and Concepts Manual.

      See :term:`STAMPS_DIR`,
      :term:`MULTIMACH_TARGET_SYS`,
      :term:`PN`, :term:`EXTENDPE`,
      :term:`PV`, and :term:`PR` for related variable
      information.

   :term:`STAMPCLEAN`
      See :term:`bitbake:STAMPCLEAN` in the BitBake manual.

   :term:`STAMPS_DIR`
      Specifies the base directory in which the OpenEmbedded build system
      places stamps. The default directory is ``${TMPDIR}/stamps``.

   :term:`STRIP`
      The minimal command and arguments to run ``strip``, which is used to
      strip symbols.

   :term:`SUMMARY`
      The short (72 characters or less) summary of the binary package for
      packaging systems such as ``opkg``, ``rpm``, or ``dpkg``. By default,
      :term:`SUMMARY` is used to define the
      :term:`DESCRIPTION` variable if :term:`DESCRIPTION` is
      not set in the recipe.

   :term:`SVNDIR`
      The directory in which files checked out of a Subversion system are
      stored.

   :term:`SYSLINUX_DEFAULT_CONSOLE`
      Specifies the kernel boot default console. If you want to use a
      console other than the default, set this variable in your recipe as
      follows where "X" is the console number you want to use::

         SYSLINUX_DEFAULT_CONSOLE = "console=ttyX"

      The :ref:`ref-classes-syslinux` class initially sets
      this variable to null but then checks for a value later.

   :term:`SYSLINUX_OPTS`
      Lists additional options to add to the syslinux file. You need to set
      this variable in your recipe. If you want to list multiple options,
      separate the options with a semicolon character (``;``).

      The :ref:`ref-classes-syslinux` class uses this variable
      to create a set of options.

   :term:`SYSLINUX_SERIAL`
      Specifies the alternate serial port or turns it off. To turn off
      serial, set this variable to an empty string in your recipe. The
      variable's default value is set in the
      :ref:`ref-classes-syslinux` class as follows::

         SYSLINUX_SERIAL ?= "0 115200"

      The class checks for and uses the variable as needed.

   :term:`SYSLINUX_SERIAL_TTY`
      Specifies the alternate console=tty... kernel boot argument. The
      variable's default value is set in the :ref:`ref-classes-syslinux`
      class as follows::

         SYSLINUX_SERIAL_TTY ?= "console=ttyS0,115200"

      The class checks for and uses the variable as needed.

   :term:`SYSLINUX_SPLASH`
      An ``.LSS`` file used as the background for the VGA boot menu when
      you use the boot menu. You need to set this variable in your recipe.

      The :ref:`ref-classes-syslinux` class checks for this
      variable and if found, the OpenEmbedded build system installs the
      splash screen.

   :term:`SYSROOT_DESTDIR`
      Points to the temporary directory under the work directory (default
      "``${``\ :term:`WORKDIR`\ ``}/sysroot-destdir``")
      where the files populated into the sysroot are assembled during the
      :ref:`ref-tasks-populate_sysroot` task.

   :term:`SYSROOT_DIRS`
      Directories that are staged into the sysroot by the
      :ref:`ref-tasks-populate_sysroot` task. By
      default, the following directories are staged::

         SYSROOT_DIRS = " \
             ${includedir} \
             ${libdir} \
             ${base_libdir} \
             ${nonarch_base_libdir} \
             ${datadir} \
             /sysroot-only \
             "

   :term:`SYSROOT_DIRS_IGNORE`
      Directories that are not staged into the sysroot by the
      :ref:`ref-tasks-populate_sysroot` task. You
      can use this variable to exclude certain subdirectories of
      directories listed in :term:`SYSROOT_DIRS` from
      staging. By default, the following directories are not staged::

         SYSROOT_DIRS_IGNORE = " \
             ${mandir} \
             ${docdir} \
             ${infodir} \
             ${datadir}/X11/locale \
             ${datadir}/applications \
             ${datadir}/bash-completion \
             ${datadir}/fonts \
             ${datadir}/gtk-doc/html \
             ${datadir}/installed-tests \
             ${datadir}/locale \
             ${datadir}/pixmaps \
             ${datadir}/terminfo \
             ${libdir}/${BPN}/ptest \
             "

      Consider the following example in which you need to manipulate this variable.
      Assume you have a recipe ``A`` that provides a shared library ``.so.*`` that is
      installed into a custom folder other than "``${libdir}``"
      or "``${base_libdir}``", let's say "``/opt/lib``".

      .. note::

         This is not a recommended way to deal with shared libraries, but this
         is just to show the usefulness of setting :term:`SYSROOT_DIRS`.

      When a recipe ``B`` :term:`DEPENDS` on ``A``, it means what is in
      :term:`SYSROOT_DIRS` will be copied from :term:`D` of the recipe ``B``
      into ``B``'s :term:`SYSROOT_DESTDIR` that is "``${WORKDIR}/sysroot-destdir``".

      Now, since ``/opt/lib`` is not in :term:`SYSROOT_DIRS`, it will never be copied to
      ``A``'s :term:`RECIPE_SYSROOT`, which is "``${WORKDIR}/recipe-sysroot``". So,
      the linking process will fail.

      To fix this, you need to add ``/opt/lib`` to :term:`SYSROOT_DIRS`::

         SYSROOT_DIRS:append = " /opt/lib"

      .. note::
         Even after setting ``/opt/lib`` to :term:`SYSROOT_DIRS`, the linking process will still fail
         because the linker does not know that location, since :term:`TARGET_LDFLAGS`
         doesn't contain it (if your recipe is for the target). Therefore, so you should add::

            TARGET_LDFLAGS:append = " -L${RECIPE_SYSROOT}/opt/lib"

   :term:`SYSROOT_DIRS_NATIVE`
      Extra directories staged into the sysroot by the
      :ref:`ref-tasks-populate_sysroot` task for
      ``-native`` recipes, in addition to those specified in
      :term:`SYSROOT_DIRS`. By default, the following
      extra directories are staged::

         SYSROOT_DIRS_NATIVE = " \
             ${bindir} \
             ${sbindir} \
             ${base_bindir} \
             ${base_sbindir} \
             ${libexecdir} \
             ${sysconfdir} \
             ${localstatedir} \
             "

      .. note::

         Programs built by ``-native`` recipes run directly from the sysroot
         (:term:`STAGING_DIR_NATIVE`), which is why additional directories
         containing program executables and supporting files need to be staged.

   :term:`SYSROOT_PREPROCESS_FUNCS`
      A list of functions to execute after files are staged into the
      sysroot. These functions are usually used to apply additional
      processing on the staged files, or to stage additional files.

   :term:`SYSTEMD_AUTO_ENABLE`
      When inheriting the :ref:`ref-classes-systemd` class,
      this variable specifies whether the specified service in
      :term:`SYSTEMD_SERVICE` should start
      automatically or not. By default, the service is enabled to
      automatically start at boot time. The default setting is in the
      :ref:`ref-classes-systemd` class as follows::

         SYSTEMD_AUTO_ENABLE ??= "enable"

      You can disable the service by setting the variable to "disable".

   :term:`SYSTEMD_BOOT_CFG`
      When :term:`EFI_PROVIDER` is set to
      "systemd-boot", the :term:`SYSTEMD_BOOT_CFG` variable specifies the
      configuration file that should be used. By default, the
      :ref:`ref-classes-systemd-boot` class sets the
      :term:`SYSTEMD_BOOT_CFG` as follows::

         SYSTEMD_BOOT_CFG ?= "${S}/loader.conf"

      For information on Systemd-boot, see the `Systemd-boot
      documentation <https://www.freedesktop.org/wiki/Software/systemd/systemd-boot/>`__.

   :term:`SYSTEMD_BOOT_ENTRIES`
      When :term:`EFI_PROVIDER` is set to
      "systemd-boot", the :term:`SYSTEMD_BOOT_ENTRIES` variable specifies a
      list of entry files (``*.conf``) to install that contain one boot
      entry per file. By default, the :ref:`ref-classes-systemd-boot` class
      sets the :term:`SYSTEMD_BOOT_ENTRIES` as follows::

          SYSTEMD_BOOT_ENTRIES ?= ""

      For information on Systemd-boot, see the `Systemd-boot
      documentation <https://www.freedesktop.org/wiki/Software/systemd/systemd-boot/>`__.

   :term:`SYSTEMD_BOOT_TIMEOUT`
      When :term:`EFI_PROVIDER` is set to
      "systemd-boot", the :term:`SYSTEMD_BOOT_TIMEOUT` variable specifies the
      boot menu timeout in seconds. By default, the
      :ref:`ref-classes-systemd-boot` class sets the
      :term:`SYSTEMD_BOOT_TIMEOUT` as follows::

         SYSTEMD_BOOT_TIMEOUT ?= "10"

      For information on Systemd-boot, see the `Systemd-boot
      documentation <https://www.freedesktop.org/wiki/Software/systemd/systemd-boot/>`__.

   :term:`SYSTEMD_DEFAULT_TARGET`

      This variable allows to set the default unit that systemd starts at bootup.
      Usually, this is either ``multi-user.target`` or ``graphical.target``.
      This works by creating a ``default.target`` symbolic link to the chosen systemd
      target file.

      See `systemd's documentation
      <https://www.freedesktop.org/software/systemd/man/systemd.special.html>`__
      for details.

      For example, this variable is used in the :oe_git:`core-image-minimal-xfce.bb
      </meta-openembedded/tree/meta-xfce/recipes-core/images/core-image-minimal-xfce.bb>`
      recipe::

          SYSTEMD_DEFAULT_TARGET = "graphical.target"

   :term:`SYSTEMD_PACKAGES`
      When inheriting the :ref:`ref-classes-systemd` class,
      this variable locates the systemd unit files when they are not found
      in the main recipe's package. By default, the :term:`SYSTEMD_PACKAGES`
      variable is set such that the systemd unit files are assumed to
      reside in the recipes main package::

         SYSTEMD_PACKAGES ?= "${PN}"

      If these unit files are not in this recipe's main package, you need
      to use :term:`SYSTEMD_PACKAGES` to list the package or packages in which
      the build system can find the systemd unit files.

   :term:`SYSTEMD_SERVICE`
      When inheriting the :ref:`ref-classes-systemd` class,
      this variable specifies the systemd service name for a package.

      Multiple services can be specified, each one separated by a space.

      When you specify this file in your recipe, use a package name
      override to indicate the package to which the value applies. Here is
      an example from the connman recipe::

         SYSTEMD_SERVICE:${PN} = "connman.service"

      The package overrides that can be specified are directly related to the value of
      :term:`SYSTEMD_PACKAGES`. Overrides not included in :term:`SYSTEMD_PACKAGES`
      will be silently ignored.

   :term:`SYSVINIT_ENABLED_GETTYS`
      When using :ref:`SysVinit <dev-manual/new-recipe:enabling system services>`,
      specifies a space-separated list of the virtual terminals that should
      run a :wikipedia:`getty <Getty_(Unix)>` (allowing login), assuming
      :term:`USE_VT` is not set to "0".

      The default value for :term:`SYSVINIT_ENABLED_GETTYS` is "1" (i.e. only
      run a getty on the first virtual terminal).

   :term:`T`
      This variable points to a directory were BitBake places temporary
      files, which consist mostly of task logs and scripts, when building a
      particular recipe. The variable is typically set as follows::

         T = "${WORKDIR}/temp"

      The :term:`WORKDIR` is the directory into which
      BitBake unpacks and builds the recipe. The default ``bitbake.conf``
      file sets this variable.

      The :term:`T` variable is not to be confused with the
      :term:`TMPDIR` variable, which points to the root of
      the directory tree where BitBake places the output of an entire
      build.

   :term:`TARGET_ARCH`
      The target machine's architecture. The OpenEmbedded build system
      supports many architectures. Here is an example list of architectures
      supported. This list is by no means complete as the architecture is
      configurable:

      - arm
      - i586
      - x86_64
      - powerpc
      - powerpc64
      - mips
      - mipsel

      For additional information on machine architectures, see the
      :term:`TUNE_ARCH` variable.

   :term:`TARGET_AS_ARCH`
      Specifies architecture-specific assembler flags for the target
      system. :term:`TARGET_AS_ARCH` is initialized from
      :term:`TUNE_ASARGS` by default in the BitBake
      configuration file (``meta/conf/bitbake.conf``)::

         TARGET_AS_ARCH = "${TUNE_ASARGS}"

   :term:`TARGET_CC_ARCH`
      Specifies architecture-specific C compiler flags for the target
      system. :term:`TARGET_CC_ARCH` is initialized from
      :term:`TUNE_CCARGS` by default.

      .. note::

         It is a common workaround to append :term:`LDFLAGS` to
         :term:`TARGET_CC_ARCH` in recipes that build software for the target that
         would not otherwise respect the exported :term:`LDFLAGS` variable.

   :term:`TARGET_CC_KERNEL_ARCH`
      This is a specific kernel compiler flag for a CPU or Application
      Binary Interface (ABI) tune. The flag is used rarely and only for
      cases where a userspace :term:`TUNE_CCARGS` is not
      compatible with the kernel compilation. The :term:`TARGET_CC_KERNEL_ARCH`
      variable allows the kernel (and associated modules) to use a
      different configuration. See the
      ``meta/conf/machine/include/arm/feature-arm-thumb.inc`` file in the
      :term:`Source Directory` for an example.

   :term:`TARGET_CFLAGS`
      Specifies the flags to pass to the C compiler when building for the
      target. When building in the target context,
      :term:`CFLAGS` is set to the value of this variable by
      default.

      Additionally, the SDK's environment setup script sets the :term:`CFLAGS`
      variable in the environment to the :term:`TARGET_CFLAGS` value so that
      executables built using the SDK also have the flags applied.

   :term:`TARGET_CPPFLAGS`
      Specifies the flags to pass to the C pre-processor (i.e. to both the
      C and the C++ compilers) when building for the target. When building
      in the target context, :term:`CPPFLAGS` is set to the
      value of this variable by default.

      Additionally, the SDK's environment setup script sets the
      :term:`CPPFLAGS` variable in the environment to the :term:`TARGET_CPPFLAGS`
      value so that executables built using the SDK also have the flags
      applied.

   :term:`TARGET_CXXFLAGS`
      Specifies the flags to pass to the C++ compiler when building for the
      target. When building in the target context,
      :term:`CXXFLAGS` is set to the value of this variable
      by default.

      Additionally, the SDK's environment setup script sets the
      :term:`CXXFLAGS` variable in the environment to the :term:`TARGET_CXXFLAGS`
      value so that executables built using the SDK also have the flags
      applied.

   :term:`TARGET_DBGSRC_DIR`
      Specifies the target path to debug source files. The default is
      ``/usr/src/debug/${PN}/${PV}``.

   :term:`TARGET_FPU`
      Specifies the method for handling FPU code. For FPU-less targets,
      which include most ARM CPUs, the variable must be set to "soft". If
      not, the kernel emulation gets used, which results in a performance
      penalty.

   :term:`TARGET_LD_ARCH`
      Specifies architecture-specific linker flags for the target system.
      :term:`TARGET_LD_ARCH` is initialized from
      :term:`TUNE_LDARGS` by default in the BitBake
      configuration file (``meta/conf/bitbake.conf``)::

         TARGET_LD_ARCH = "${TUNE_LDARGS}"

   :term:`TARGET_LDFLAGS`
      Specifies the flags to pass to the linker when building for the
      target. When building in the target context,
      :term:`LDFLAGS` is set to the value of this variable
      by default.

      Additionally, the SDK's environment setup script sets the
      :term:`LDFLAGS` variable in the environment to the
      :term:`TARGET_LDFLAGS` value so that executables built using the SDK also
      have the flags applied.

   :term:`TARGET_OS`
      Specifies the target's operating system. The variable can be set to
      "linux" for glibc-based systems (GNU C Library) and to "linux-musl"
      for musl libc. For ARM/EABI targets, the possible values are
      "linux-gnueabi" and "linux-musleabi".

   :term:`TARGET_PREFIX`
      Specifies the prefix used for the toolchain binary target tools.

      Depending on the type of recipe and the build target,
      :term:`TARGET_PREFIX` is set as follows:

      -  For recipes building for the target machine, the value is
         "${:term:`TARGET_SYS`}-".

      -  For native recipes, the build system sets the variable to the
         value of :term:`BUILD_PREFIX`.

      -  For native SDK recipes (:ref:`ref-classes-nativesdk`),
         the build system sets the variable to the value of :term:`SDK_PREFIX`.

   :term:`TARGET_SYS`
      Specifies the system, including the architecture and the operating
      system, for which the build is occurring in the context of the
      current recipe.

      The OpenEmbedded build system automatically sets this variable based
      on :term:`TARGET_ARCH`,
      :term:`TARGET_VENDOR`, and
      :term:`TARGET_OS` variables.

      .. note::

         You do not need to set the :term:`TARGET_SYS` variable yourself.

      Consider these two examples:

      -  Given a native recipe on a 32-bit, x86 machine running Linux, the
         value is "i686-linux".

      -  Given a recipe being built for a little-endian, MIPS target
         running Linux, the value might be "mipsel-linux".

   :term:`TARGET_VENDOR`
      Specifies the name of the target vendor.

   :term:`TCLIBC`
      Specifies the GNU standard C library (``libc``) variant to use during
      the build process.

      You can select "glibc", "musl", "newlib", or "baremetal".

   :term:`TCLIBCAPPEND`
      Specifies a suffix to be appended onto the :term:`TMPDIR` value. The
      suffix identifies the ``libc`` variant for building. When you are
      building for multiple variants with the same :term:`Build Directory`,
      this mechanism ensures that output for different ``libc`` variants is
      kept separate to avoid potential conflicts.

      In the ``defaultsetup.conf`` file, the default value of
      :term:`TCLIBCAPPEND` is "-${TCLIBC}". However, distros such as poky,
      which normally only support one ``libc`` variant, set
      :term:`TCLIBCAPPEND` to "" in their distro configuration file resulting
      in no suffix being applied.

   :term:`TCMODE`
      Specifies the toolchain selector. :term:`TCMODE` controls the
      characteristics of the generated packages and images by telling the
      OpenEmbedded build system which toolchain profile to use. By default,
      the OpenEmbedded build system builds its own internal toolchain. The
      variable's default value is "default", which uses that internal
      toolchain.

      .. note::

         If :term:`TCMODE` is set to a value other than "default", then it is your
         responsibility to ensure that the toolchain is compatible with the
         default toolchain. Using older or newer versions of these
         components might cause build problems. See
         :doc:`Release Information </migration-guides/index>` for your
         version of the Yocto Project, to find the specific components with
         which the toolchain must be compatible.

      The :term:`TCMODE` variable is similar to :term:`TCLIBC`,
      which controls the variant of the GNU standard C library (``libc``)
      used during the build process: ``glibc`` or ``musl``.

      With additional layers, it is possible to use a pre-compiled external
      toolchain. One example is the Sourcery G++ Toolchain. The support for
      this toolchain resides in the separate Mentor Graphics
      ``meta-sourcery`` layer at
      https://github.com/MentorEmbedded/meta-sourcery/.

      The layer's ``README`` file contains information on how to use the
      Sourcery G++ Toolchain as an external toolchain. You will have to
      add the layer to your ``bblayers.conf`` file and then set the
      :term:`EXTERNAL_TOOLCHAIN` variable in your ``local.conf`` file to
      the location of the toolchain.

      The fundamentals used for this example apply to any external
      toolchain. You can use ``meta-sourcery`` as a template for adding
      support for other external toolchains.

      In addition to toolchain configuration, you will also need a
      corresponding toolchain recipe file. This recipe file needs to package
      up any pre-built objects in the toolchain such as ``libgcc``,
      ``libstdcc++``, any locales, and ``libc``.

   :term:`TC_CXX_RUNTIME`
      Specifies the C/C++ STL and runtime variant to use during
      the build process. Default value is 'gnu'

      You can select "gnu", "llvm", or "android".

   :term:`TEMPLATECONF`
      Specifies the directory used by the build system to find templates
      from which to build the ``bblayers.conf`` and ``local.conf`` files.
      Use this variable if you wish to customize such files, and the default
      BitBake targets shown when sourcing the ``oe-init-build-env`` script.

      For details, see the
      :ref:`dev-manual/custom-template-configuration-directory:creating a custom template configuration directory`
      section in the Yocto Project Development Tasks manual.

      .. note::

         You must set this variable in the external environment in order
         for it to work.

   :term:`TEST_EXPORT_DIR`
      The location the OpenEmbedded build system uses to export tests when
      the :term:`TEST_EXPORT_ONLY` variable is set
      to "1".

      The :term:`TEST_EXPORT_DIR` variable defaults to
      ``"${TMPDIR}/testimage/${PN}"``.

   :term:`TEST_EXPORT_ONLY`
      Specifies to export the tests only. Set this variable to "1" if you
      do not want to run the tests but you want them to be exported in a
      manner that you to run them outside of the build system.

   :term:`TEST_LOG_DIR`
      Holds the SSH log and the boot log for QEMU machines. The
      :term:`TEST_LOG_DIR` variable defaults to ``"${WORKDIR}/testimage"``.

      .. note::

         Actual test results reside in the task log (``log.do_testimage``),
         which is in the ``${WORKDIR}/temp/`` directory.

   :term:`TEST_POWERCONTROL_CMD`
      For automated hardware testing, specifies the command to use to
      control the power of the target machine under test. Typically, this
      command would point to a script that performs the appropriate action
      (e.g. interacting with a web-enabled power strip). The specified
      command should expect to receive as the last argument "off", "on" or
      "cycle" specifying to power off, on, or cycle (power off and then
      power on) the device, respectively.

   :term:`TEST_POWERCONTROL_EXTRA_ARGS`
      For automated hardware testing, specifies additional arguments to
      pass through to the command specified in
      :term:`TEST_POWERCONTROL_CMD`. Setting
      :term:`TEST_POWERCONTROL_EXTRA_ARGS` is optional. You can use it if you
      wish, for example, to separate the machine-specific and
      non-machine-specific parts of the arguments.

   :term:`TEST_QEMUBOOT_TIMEOUT`
      The time in seconds allowed for an image to boot before automated
      runtime tests begin to run against an image. The default timeout
      period to allow the boot process to reach the login prompt is 500
      seconds. You can specify a different value in the ``local.conf``
      file.

      For more information on testing images, see the
      ":ref:`dev-manual/runtime-testing:performing automated runtime testing`"
      section in the Yocto Project Development Tasks Manual.

   :term:`TEST_SERIALCONTROL_CMD`
      For automated hardware testing, specifies the command to use to
      connect to the serial console of the target machine under test. This
      command simply needs to connect to the serial console and forward
      that connection to standard input and output as any normal terminal
      program does.

      For example, to use the Picocom terminal program on serial device
      ``/dev/ttyUSB0`` at 115200bps, you would set the variable as follows::

         TEST_SERIALCONTROL_CMD = "picocom /dev/ttyUSB0 -b 115200"

   :term:`TEST_SERIALCONTROL_EXTRA_ARGS`
      For automated hardware testing, specifies additional arguments to
      pass through to the command specified in
      :term:`TEST_SERIALCONTROL_CMD`. Setting
      :term:`TEST_SERIALCONTROL_EXTRA_ARGS` is optional. You can use it if you
      wish, for example, to separate the machine-specific and
      non-machine-specific parts of the command.

   :term:`TEST_SERVER_IP`
      The IP address of the build machine (host machine). This IP address
      is usually automatically detected. However, if detection fails, this
      variable needs to be set to the IP address of the build machine (i.e.
      where the build is taking place).

      .. note::

         The :term:`TEST_SERVER_IP` variable is only used for a small number of
         tests such as the "dnf" test suite, which needs to download packages
         from ``WORKDIR/oe-rootfs-repo``.

   :term:`TEST_SUITES`
      An ordered list of tests (modules) to run against an image when
      performing automated runtime testing.

      The OpenEmbedded build system provides a core set of tests that can
      be used against images.

      .. note::

         Currently, there is only support for running these tests under
         QEMU.

      Tests include ``ping``, ``ssh``, ``df`` among others. You can add
      your own tests to the list of tests by appending :term:`TEST_SUITES` as
      follows::

         TEST_SUITES:append = " mytest"

      Alternatively, you can
      provide the "auto" option to have all applicable tests run against
      the image::

         TEST_SUITES:append = " auto"

      Using this option causes the
      build system to automatically run tests that are applicable to the
      image. Tests that are not applicable are skipped.

      The order in which tests are run is important. Tests that depend on
      another test must appear later in the list than the test on which
      they depend. For example, if you append the list of tests with two
      tests (``test_A`` and ``test_B``) where ``test_B`` is dependent on
      ``test_A``, then you must order the tests as follows::

         TEST_SUITES = "test_A test_B"

      For more information on testing images, see the
      ":ref:`dev-manual/runtime-testing:performing automated runtime testing`"
      section in the Yocto Project Development Tasks Manual.

   :term:`TEST_TARGET`
      Specifies the target controller to use when running tests against a
      test image. The default controller to use is "qemu"::

         TEST_TARGET = "qemu"

      A target controller is a class that defines how an image gets
      deployed on a target and how a target is started. A layer can extend
      the controllers by adding a module in the layer's
      ``/lib/oeqa/controllers`` directory and by inheriting the
      ``BaseTarget`` class, which is an abstract class that cannot be used
      as a value of :term:`TEST_TARGET`.

      You can provide the following arguments with :term:`TEST_TARGET`:

      -  *"qemu":* Boots a QEMU image and runs the tests. See the
         ":ref:`dev-manual/runtime-testing:enabling runtime tests on qemu`" section
         in the Yocto Project Development Tasks Manual for more
         information.

      -  *"simpleremote":* Runs the tests on target hardware that is
         already up and running. The hardware can be on the network or it
         can be a device running an image on QEMU. You must also set
         :term:`TEST_TARGET_IP` when you use
         "simpleremote".

         .. note::

            This argument is defined in
            ``meta/lib/oeqa/controllers/simpleremote.py``.

      For information on running tests on hardware, see the
      ":ref:`dev-manual/runtime-testing:enabling runtime tests on hardware`"
      section in the Yocto Project Development Tasks Manual.

   :term:`TEST_TARGET_IP`
      The IP address of your hardware under test. The :term:`TEST_TARGET_IP`
      variable has no effect when :term:`TEST_TARGET` is
      set to "qemu".

      When you specify the IP address, you can also include a port. Here is
      an example::

         TEST_TARGET_IP = "192.168.1.4:2201"

      Specifying a port is
      useful when SSH is started on a non-standard port or in cases when
      your hardware under test is behind a firewall or network that is not
      directly accessible from your host and you need to do port address
      translation.

   :term:`TESTIMAGE_AUTO`
      Automatically runs the series of automated tests for images when an
      image is successfully built. Setting :term:`TESTIMAGE_AUTO` to "1" causes
      any image that successfully builds to automatically boot under QEMU.
      Using the variable also adds in dependencies so that any SDK for
      which testing is requested is automatically built first.

      These tests are written in Python making use of the ``unittest``
      module, and the majority of them run commands on the target system
      over ``ssh``. You can set this variable to "1" in your ``local.conf``
      file in the :term:`Build Directory` to have the
      OpenEmbedded build system automatically run these tests after an
      image successfully builds:

         TESTIMAGE_AUTO = "1"

      For more information
      on enabling, running, and writing these tests, see the
      ":ref:`dev-manual/runtime-testing:performing automated runtime testing`"
      section in the Yocto Project Development Tasks Manual and the
      ":ref:`ref-classes-testimage`" section.

   :term:`THISDIR`
      The directory in which the file BitBake is currently parsing is
      located. Do not manually set this variable.

   :term:`TIME`
      The time the build was started. Times appear using the hour, minute,
      and second (HMS) format (e.g. "140159" for one minute and fifty-nine
      seconds past 1400 hours).

   :term:`TMPDIR`
      This variable is the base directory the OpenEmbedded build system
      uses for all build output and intermediate files (other than the
      shared state cache). By default, the :term:`TMPDIR` variable points to
      ``tmp`` within the :term:`Build Directory`.

      If you want to establish this directory in a location other than the
      default, you can uncomment and edit the following statement in the
      ``conf/local.conf`` file in the :term:`Source Directory`::

         #TMPDIR = "${TOPDIR}/tmp"

      An example use for this scenario is to set :term:`TMPDIR` to a local disk,
      which does not use NFS, while having the :term:`Build Directory` use NFS.

      The filesystem used by :term:`TMPDIR` must have standard filesystem
      semantics (i.e. mixed-case files are unique, POSIX file locking, and
      persistent inodes). Due to various issues with NFS and bugs in some
      implementations, NFS does not meet this minimum requirement.
      Consequently, :term:`TMPDIR` cannot be on NFS.

   :term:`TOOLCHAIN_HOST_TASK`
      This variable lists packages the OpenEmbedded build system uses when
      building an SDK, which contains a cross-development environment. The
      packages specified by this variable are part of the toolchain set
      that runs on the :term:`SDKMACHINE`, and each
      package should usually have the prefix ``nativesdk-``. For example,
      consider the following command when building an SDK::

         $ bitbake -c populate_sdk imagename

      In this case, a default list of packages is
      set in this variable, but you can add additional packages to the
      list. See the
      ":ref:`sdk-manual/appendix-customizing-standard:adding individual packages to the standard sdk`" section
      in the Yocto Project Application Development and the Extensible
      Software Development Kit (eSDK) manual for more information.

      For background information on cross-development toolchains in the
      Yocto Project development environment, see the
      ":ref:`sdk-manual/intro:the cross-development toolchain`"
      section in the Yocto Project Overview and Concepts Manual. For
      information on setting up a cross-development environment, see the
      :doc:`/sdk-manual/index` manual.

      Note that this variable applies to building an SDK, not an eSDK,
      in which case the :term:`TOOLCHAIN_HOST_TASK_ESDK` setting should be
      used instead.

   :term:`TOOLCHAIN_HOST_TASK_ESDK`
      This variable allows to extend what is installed in the host
      portion of an eSDK. This is similar to :term:`TOOLCHAIN_HOST_TASK`
      applying to SDKs.

   :term:`TOOLCHAIN_OPTIONS`
      This variable holds extra options passed to the compiler and the linker
      for non ``-native`` recipes as they have to point to their custom
      ``sysroot`` folder pointed to by :term:`RECIPE_SYSROOT`::

         TOOLCHAIN_OPTIONS = " --sysroot=${RECIPE_SYSROOT}"

      Native recipes don't need this variable to be set, as they are
      built for the host machine with the native compiler.

   :term:`TOOLCHAIN_OUTPUTNAME`
      This variable defines the name used for the toolchain output. The
      :ref:`populate_sdk_base <ref-classes-populate-sdk-*>` class sets
      the :term:`TOOLCHAIN_OUTPUTNAME` variable as follows::

         TOOLCHAIN_OUTPUTNAME ?= "${SDK_NAME}-toolchain-${SDK_VERSION}"

      See
      the :term:`SDK_NAME` and
      :term:`SDK_VERSION` variables for additional
      information.

   :term:`TOOLCHAIN_TARGET_TASK`
      This variable lists packages the OpenEmbedded build system uses when
      it creates the target part of an SDK (i.e. the part built for the
      target hardware), which includes libraries and headers. Use this
      variable to add individual packages to the part of the SDK that runs
      on the target. See the
      ":ref:`sdk-manual/appendix-customizing-standard:adding individual packages to the standard sdk`" section
      in the Yocto Project Application Development and the Extensible
      Software Development Kit (eSDK) manual for more information.

      For background information on cross-development toolchains in the
      Yocto Project development environment, see the
      ":ref:`sdk-manual/intro:the cross-development toolchain`"
      section in the Yocto Project Overview and Concepts Manual. For
      information on setting up a cross-development environment, see the
      :doc:`/sdk-manual/index` manual.

   :term:`TOPDIR`
      See :term:`bitbake:TOPDIR` in the BitBake manual.

   :term:`TRANSLATED_TARGET_ARCH`
      A sanitized version of :term:`TARGET_ARCH`. This
      variable is used where the architecture is needed in a value where
      underscores are not allowed, for example within package filenames. In
      this case, dash characters replace any underscore characters used in
      :term:`TARGET_ARCH`.

      Do not edit this variable.

   :term:`TUNE_ARCH`
      The GNU canonical architecture for a specific architecture (i.e.
      ``arm``, ``armeb``, ``mips``, ``mips64``, and so forth). BitBake uses
      this value to setup configuration.

      :term:`TUNE_ARCH` definitions are specific to a given architecture. The
      definitions can be a single static definition, or can be dynamically
      adjusted. You can see details for a given CPU family by looking at
      the architecture's ``README`` file. For example, the
      ``meta/conf/machine/include/mips/README`` file in the
      :term:`Source Directory` provides information for
      :term:`TUNE_ARCH` specific to the ``mips`` architecture.

      :term:`TUNE_ARCH` is tied closely to
      :term:`TARGET_ARCH`, which defines the target
      machine's architecture. The BitBake configuration file
      (``meta/conf/bitbake.conf``) sets :term:`TARGET_ARCH` as follows::

         TARGET_ARCH = "${TUNE_ARCH}"

      The following list, which is by no means complete since architectures
      are configurable, shows supported machine architectures:

      - arm
      - i586
      - x86_64
      - powerpc
      - powerpc64
      - mips
      - mipsel

   :term:`TUNE_ASARGS`
      Specifies architecture-specific assembler flags for the target
      system. The set of flags is based on the selected tune features.
      :term:`TUNE_ASARGS` is set using the tune include files, which are
      typically under ``meta/conf/machine/include/`` and are influenced
      through :term:`TUNE_FEATURES`. For example, the
      ``meta/conf/machine/include/x86/arch-x86.inc`` file defines the flags
      for the x86 architecture as follows::

         TUNE_ASARGS += "${@bb.utils.contains("TUNE_FEATURES", "mx32", "-x32", "", d)}"

      .. note::

         Board Support Packages (BSPs) select the tune. The selected tune,
         in turn, affects the tune variables themselves (i.e. the tune can
         supply its own set of flags).

   :term:`TUNE_CCARGS`
      Specifies architecture-specific C compiler flags for the target
      system. The set of flags is based on the selected tune features.
      :term:`TUNE_CCARGS` is set using the tune include files, which are
      typically under ``meta/conf/machine/include/`` and are influenced
      through :term:`TUNE_FEATURES`.

      .. note::

         Board Support Packages (BSPs) select the tune. The selected tune,
         in turn, affects the tune variables themselves (i.e. the tune can
         supply its own set of flags).

   :term:`TUNE_FEATURES`
      Features used to "tune" a compiler for optimal use given a specific
      processor. The features are defined within the tune files and allow
      arguments (i.e. ``TUNE_*ARGS``) to be dynamically generated based on
      the features.

      The OpenEmbedded build system verifies the features to be sure they
      are not conflicting and that they are supported.

      The BitBake configuration file (``meta/conf/bitbake.conf``) defines
      :term:`TUNE_FEATURES` as follows::

         TUNE_FEATURES ??= "${TUNE_FEATURES:tune-${DEFAULTTUNE}}"

      See the :term:`DEFAULTTUNE` variable for more information.

   :term:`TUNE_LDARGS`
      Specifies architecture-specific linker flags for the target system.
      The set of flags is based on the selected tune features.
      :term:`TUNE_LDARGS` is set using the tune include files, which are
      typically under ``meta/conf/machine/include/`` and are influenced
      through :term:`TUNE_FEATURES`. For example, the
      ``meta/conf/machine/include/x86/arch-x86.inc`` file defines the flags
      for the x86 architecture as follows::

         TUNE_LDARGS += "${@bb.utils.contains("TUNE_FEATURES", "mx32", "-m elf32_x86_64", "", d)}"

      .. note::

         Board Support Packages (BSPs) select the tune. The selected tune,
         in turn, affects the tune variables themselves (i.e. the tune can
         supply its own set of flags).

   :term:`TUNE_PKGARCH`
      The package architecture understood by the packaging system to define
      the architecture, ABI, and tuning of output packages. The specific
      tune is defined using the "_tune" override as follows::

         TUNE_PKGARCH:tune-tune = "tune"

      These tune-specific package architectures are defined in the machine
      include files. Here is an example of the "core2-32" tuning as used in
      the ``meta/conf/machine/include/x86/tune-core2.inc`` file::

         TUNE_PKGARCH:tune-core2-32 = "core2-32"

   :term:`TUNECONFLICTS[feature]`
      Specifies CPU or Application Binary Interface (ABI) tuning features
      that conflict with feature.

      Known tuning conflicts are specified in the machine include files in
      the :term:`Source Directory`. Here is an example from
      the ``meta/conf/machine/include/mips/arch-mips.inc`` include file
      that lists the "o32" and "n64" features as conflicting with the "n32"
      feature::

         TUNECONFLICTS[n32] = "o32 n64"

   :term:`TUNEVALID[feature]`
      Specifies a valid CPU or Application Binary Interface (ABI) tuning
      feature. The specified feature is stored as a flag. Valid features
      are specified in the machine include files (e.g.
      ``meta/conf/machine/include/arm/arch-arm.inc``). Here is an example
      from that file::

         TUNEVALID[bigendian] = "Enable big-endian mode."

      See the machine include files in the :term:`Source Directory`
      for these features.

   :term:`UBOOT_BINARY`
      Specifies the name of the binary build by U-Boot.

   :term:`UBOOT_CONFIG`
      Configures one or more U-Boot configurations to build. Each
      configuration can define the :term:`UBOOT_MACHINE` and optionally the
      :term:`IMAGE_FSTYPES` and the :term:`UBOOT_BINARY`.

      Here is an example from the ``meta-freescale`` layer. ::

         UBOOT_CONFIG ??= "sdcard-ifc-secure-boot sdcard-ifc sdcard-qspi lpuart qspi secure-boot nor"
         UBOOT_CONFIG[nor] = "ls1021atwr_nor_defconfig"
         UBOOT_CONFIG[sdcard-ifc] = "ls1021atwr_sdcard_ifc_defconfig,,u-boot-with-spl-pbl.bin"
         UBOOT_CONFIG[sdcard-qspi] = "ls1021atwr_sdcard_qspi_defconfig,,u-boot-with-spl-pbl.bin"
         UBOOT_CONFIG[lpuart] = "ls1021atwr_nor_lpuart_defconfig"
         UBOOT_CONFIG[qspi] = "ls1021atwr_qspi_defconfig"
         UBOOT_CONFIG[secure-boot] = "ls1021atwr_nor_SECURE_BOOT_defconfig"
         UBOOT_CONFIG[sdcard-ifc-secure-boot] = "ls1021atwr_sdcard_ifc_SECURE_BOOT_defconfig,,u-boot-with-spl-pbl.bin"

      In this example, all possible seven configurations are selected. Each
      configuration specifies "..._defconfig" as :term:`UBOOT_MACHINE`, and
      the "sd..." configurations define an individual name for
      :term:`UBOOT_BINARY`. No configuration defines a second parameter for
      :term:`IMAGE_FSTYPES` to use for the U-Boot image.

      For more information on how the :term:`UBOOT_CONFIG` is handled, see the
      :ref:`ref-classes-uboot-config` class.

   :term:`UBOOT_DTB_LOADADDRESS`
      Specifies the load address for the dtb image used by U-Boot. During FIT
      image creation, the :term:`UBOOT_DTB_LOADADDRESS` variable is used in
      :ref:`ref-classes-kernel-fitimage` class to specify the load address to be
      used in creating the dtb sections of Image Tree Source for the FIT image.

   :term:`UBOOT_DTBO_LOADADDRESS`
      Specifies the load address for the dtbo image used by U-Boot.  During FIT
      image creation, the :term:`UBOOT_DTBO_LOADADDRESS` variable is used in
      :ref:`ref-classes-kernel-fitimage` class to specify the load address to be
      used in creating the dtbo sections of Image Tree Source for the FIT image.

   :term:`UBOOT_ENTRYPOINT`
      Specifies the entry point for the U-Boot image. During U-Boot image
      creation, the :term:`UBOOT_ENTRYPOINT` variable is passed as a
      command-line parameter to the ``uboot-mkimage`` utility.

      To pass a 64 bit address for FIT image creation, you will need to set:
      -  The :term:`FIT_ADDRESS_CELLS` variable for FIT image creation.
      -  The :term:`UBOOT_FIT_ADDRESS_CELLS` variable for U-Boot FIT image creation.

      This variable is used by the :ref:`ref-classes-kernel-fitimage`,
      :ref:`ref-classes-kernel-uimage`, :ref:`ref-classes-kernel`,
      :ref:`ref-classes-uboot-config` and :ref:`ref-classes-uboot-sign`
      classes.

   :term:`UBOOT_FIT_ADDRESS_CELLS`
      Specifies the value of the ``#address-cells`` value for the
      description of the U-Boot FIT image.

      The default value is set to "1" by the :ref:`ref-classes-uboot-sign`
      class, which corresponds to 32 bit addresses.

      For platforms that need to set 64 bit addresses in
      :term:`UBOOT_LOADADDRESS` and :term:`UBOOT_ENTRYPOINT`, you need to
      set this value to "2", as two 32 bit values (cells) will be needed
      to represent such addresses.

      Here is an example setting "0x400000000" as a load address::

         UBOOT_FIT_ADDRESS_CELLS = "2"
         UBOOT_LOADADDRESS= "0x04 0x00000000"

      See `more details about #address-cells <https://elinux.org/Device_Tree_Usage#How_Addressing_Works>`__.

   :term:`UBOOT_FIT_DESC`
      Specifies the description string encoded into a U-Boot fitImage. The default
      value is set by the :ref:`ref-classes-uboot-sign` class as follows::

         UBOOT_FIT_DESC ?= "U-Boot fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}"

   :term:`UBOOT_FIT_GENERATE_KEYS`
      Decides whether to generate the keys for signing the U-Boot fitImage if
      they don't already exist. The keys are created in :term:`SPL_SIGN_KEYDIR`.
      The default value is "0".

      Enable this as follows::

         UBOOT_FIT_GENERATE_KEYS = "1"

      This variable is used in the :ref:`ref-classes-uboot-sign` class.

   :term:`UBOOT_FIT_HASH_ALG`
      Specifies the hash algorithm used in creating the U-Boot FIT Image.
      It is set by default to ``sha256`` by the :ref:`ref-classes-uboot-sign`
      class.

   :term:`UBOOT_FIT_KEY_GENRSA_ARGS`
      Arguments to ``openssl genrsa`` for generating a RSA private key for
      signing the U-Boot FIT image. The default value of this variable
      is set to "-F4" by the :ref:`ref-classes-uboot-sign` class.

   :term:`UBOOT_FIT_KEY_REQ_ARGS`
      Arguments to ``openssl req`` for generating a certificate for signing
      the U-Boot FIT image. The default value is "-batch -new" by the
      :ref:`ref-classes-uboot-sign` class, "batch" for
      non interactive mode and "new" for generating new keys.

   :term:`UBOOT_FIT_KEY_SIGN_PKCS`
      Format for the public key certificate used for signing the U-Boot FIT
      image. The default value is set to "x509" by the
      :ref:`ref-classes-uboot-sign` class.

   :term:`UBOOT_FIT_SIGN_ALG`
      Specifies the signature algorithm used in creating the U-Boot FIT Image.
      This variable is set by default to "rsa2048" by the
      :ref:`ref-classes-uboot-sign` class.

   :term:`UBOOT_FIT_SIGN_NUMBITS`
      Size of the private key used in signing the U-Boot FIT image, in number
      of bits. The default value for this variable is set to "2048"
      by the :ref:`ref-classes-uboot-sign` class.

   :term:`UBOOT_FITIMAGE_ENABLE`
      This variable allows to generate a FIT image for U-Boot, which is one
      of the ways to implement a verified boot process.

      Its default value is "0", so set it to "1" to enable this functionality::

         UBOOT_FITIMAGE_ENABLE = "1"

      See the :ref:`ref-classes-uboot-sign` class for details.

   :term:`UBOOT_LOADADDRESS`
      Specifies the load address for the U-Boot image. During U-Boot image
      creation, the :term:`UBOOT_LOADADDRESS` variable is passed as a
      command-line parameter to the ``uboot-mkimage`` utility.

      To pass a 64 bit address, you will also need to set:

      -  The :term:`FIT_ADDRESS_CELLS` variable for FIT image creation.
      -  The :term:`UBOOT_FIT_ADDRESS_CELLS` variable for U-Boot FIT image creation.

      This variable is used by the :ref:`ref-classes-kernel-fitimage`,
      :ref:`ref-classes-kernel-uimage`, :ref:`ref-classes-kernel`,
      :ref:`ref-classes-uboot-config` and :ref:`ref-classes-uboot-sign`
      classes.

   :term:`UBOOT_LOCALVERSION`
      Appends a string to the name of the local version of the U-Boot
      image. For example, assuming the version of the U-Boot image built
      was "2013.10", the full version string reported by U-Boot would be
      "2013.10-yocto" given the following statement::

         UBOOT_LOCALVERSION = "-yocto"

   :term:`UBOOT_MACHINE`
      Specifies the value passed on the ``make`` command line when building
      a U-Boot image. The value indicates the target platform
      configuration. You typically set this variable from the machine
      configuration file (i.e. ``conf/machine/machine_name.conf``).

      Please see the "Selection of Processor Architecture and Board Type"
      section in the U-Boot README for valid values for this variable.

   :term:`UBOOT_MAKE_TARGET`
      Specifies the target called in the ``Makefile``. The default target
      is "all".

   :term:`UBOOT_MKIMAGE`
      Specifies the name of the mkimage command as used by the
      :ref:`ref-classes-kernel-fitimage` class to assemble
      the FIT image. This can be used to substitute an alternative command, wrapper
      script or function if desired. The default is "uboot-mkimage".

   :term:`UBOOT_MKIMAGE_DTCOPTS`
      Options for the device tree compiler passed to ``mkimage -D`` feature
      while creating a FIT image with the :ref:`ref-classes-kernel-fitimage`
      class. If :term:`UBOOT_MKIMAGE_DTCOPTS` is not set then the
      :ref:`ref-classes-kernel-fitimage` class will not pass the ``-D`` option
      to ``mkimage``.

      This variable is also used by the :ref:`ref-classes-uboot-sign` class.

   :term:`UBOOT_MKIMAGE_KERNEL_TYPE`
      Specifies the type argument for the kernel as passed to ``uboot-mkimage``.
      The default value is "kernel".

   :term:`UBOOT_MKIMAGE_SIGN`
      Specifies the name of the mkimage command as used by the
      :ref:`ref-classes-kernel-fitimage` class to sign
      the FIT image after it has been assembled (if enabled). This can be used
      to substitute an alternative command, wrapper script or function if
      desired. The default is "${:term:`UBOOT_MKIMAGE`}".

   :term:`UBOOT_MKIMAGE_SIGN_ARGS`
      Optionally specifies additional arguments for the
      :ref:`ref-classes-kernel-fitimage` class to pass to the
      mkimage command when signing the FIT image.

   :term:`UBOOT_RD_ENTRYPOINT`
      Specifies the entrypoint for the RAM disk image. During FIT image
      creation, the :term:`UBOOT_RD_ENTRYPOINT` variable is used in
      :ref:`ref-classes-kernel-fitimage` class to specify the entrypoint to be
      used in creating the Image Tree Source for the FIT image.

   :term:`UBOOT_RD_LOADADDRESS`
      Specifies the load address for the RAM disk image. During FIT image
      creation, the :term:`UBOOT_RD_LOADADDRESS` variable is used in
      :ref:`ref-classes-kernel-fitimage` class to specify the load address to
      be used in creating the Image Tree Source for the FIT image.

   :term:`UBOOT_SIGN_ENABLE`
      Enable signing of FIT image. The default value is "0".

      This variable is used by the :ref:`ref-classes-kernel-fitimage`,
      :ref:`ref-classes-uboot-config` and :ref:`ref-classes-uboot-sign`
      classes.

   :term:`UBOOT_SIGN_KEYDIR`
      Location of the directory containing the RSA key and certificate used for
      signing FIT image, used by the :ref:`ref-classes-kernel-fitimage` and
      :ref:`ref-classes-uboot-sign` classes.

   :term:`UBOOT_SIGN_KEYNAME`
      The name of keys used by the :ref:`ref-classes-kernel-fitimage` class
      for signing U-Boot FIT image stored in the :term:`UBOOT_SIGN_KEYDIR`
      directory. If we have for example a ``dev.key`` key and a ``dev.crt``
      certificate stored in the :term:`UBOOT_SIGN_KEYDIR` directory, you will
      have to set :term:`UBOOT_SIGN_KEYNAME` to ``dev``.

   :term:`UBOOT_SUFFIX`
      Points to the generated U-Boot extension. For example, ``u-boot.sb``
      has a ``.sb`` extension.

      The default U-Boot extension is ``.bin``

   :term:`UBOOT_TARGET`
      Specifies the target used for building U-Boot. The target is passed
      directly as part of the "make" command (e.g. SPL and AIS). If you do
      not specifically set this variable, the OpenEmbedded build process
      passes and uses "all" for the target during the U-Boot building
      process.

   :term:`UNKNOWN_CONFIGURE_OPT_IGNORE`
      Specifies a list of options that, if reported by the configure script
      as being invalid, should not generate a warning during the
      :ref:`ref-tasks-configure` task. Normally, invalid
      configure options are simply not passed to the configure script (e.g.
      should be removed from :term:`EXTRA_OECONF` or
      :term:`PACKAGECONFIG_CONFARGS`).
      However, there are common options that are passed to all
      configure scripts at a class level, but might not be valid for some
      configure scripts. Therefore warnings about these options are useless.
      For these cases, the options are added to :term:`UNKNOWN_CONFIGURE_OPT_IGNORE`.

      The configure arguments check that uses
      :term:`UNKNOWN_CONFIGURE_OPT_IGNORE` is part of the
      :ref:`ref-classes-insane` class and is only enabled if the
      recipe inherits the :ref:`ref-classes-autotools` class.

   :term:`UPDATERCPN`
      For recipes inheriting the
      :ref:`ref-classes-update-rc.d` class, :term:`UPDATERCPN`
      specifies the package that contains the initscript that is enabled.

      The default value is "${PN}". Given that almost all recipes that
      install initscripts package them in the main package for the recipe,
      you rarely need to set this variable in individual recipes.

   :term:`UPSTREAM_CHECK_COMMITS`
      You can perform a per-recipe check for what the latest upstream
      source code version is by calling ``devtool latest-version recipe``. If
      the recipe source code is provided from Git repositories, but
      releases are not identified by Git tags, set :term:`UPSTREAM_CHECK_COMMITS`
      to ``1`` in the recipe, and the OpenEmbedded build system
      will compare the latest commit with the one currently specified
      by the recipe (:term:`SRCREV`)::

         UPSTREAM_CHECK_COMMITS = "1"

   :term:`UPSTREAM_CHECK_GITTAGREGEX`
      You can perform a per-recipe check for what the latest upstream
      source code version is by calling ``devtool latest-version recipe``. If
      the recipe source code is provided from Git repositories, the
      OpenEmbedded build system determines the latest upstream version by
      picking the latest tag from the list of all repository tags.

      You can use the :term:`UPSTREAM_CHECK_GITTAGREGEX` variable to provide a
      regular expression to filter only the relevant tags should the
      default filter not work correctly::

         UPSTREAM_CHECK_GITTAGREGEX = "git_tag_regex"

   :term:`UPSTREAM_CHECK_REGEX`
      Use the :term:`UPSTREAM_CHECK_REGEX` variable to specify a different
      regular expression instead of the default one when the package
      checking system is parsing the page found using
      :term:`UPSTREAM_CHECK_URI`::

         UPSTREAM_CHECK_REGEX = "package_regex"

   :term:`UPSTREAM_CHECK_URI`
      You can perform a per-recipe check for what the latest upstream
      source code version is by calling ``devtool latest-version recipe``. If
      the source code is provided from tarballs, the latest version is
      determined by fetching the directory listing where the tarball is and
      attempting to find a later tarball. When this approach does not work,
      you can use :term:`UPSTREAM_CHECK_URI` to provide a different URI that
      contains the link to the latest tarball::

         UPSTREAM_CHECK_URI = "recipe_url"

   :term:`UPSTREAM_VERSION_UNKNOWN`
      You can perform a per-recipe check for what the latest upstream
      source code version is by calling ``devtool latest-version recipe``.
      If no combination of the :term:`UPSTREAM_CHECK_URI`, :term:`UPSTREAM_CHECK_REGEX`,
      :term:`UPSTREAM_CHECK_GITTAGREGEX` and :term:`UPSTREAM_CHECK_COMMITS` variables in
      the recipe allows to determine what the latest upstream version is,
      you can set :term:`UPSTREAM_VERSION_UNKNOWN` to ``1`` in the recipe
      to acknowledge that the check cannot be performed::

         UPSTREAM_VERSION_UNKNOWN = "1"

   :term:`USE_DEVFS`
      Determines if ``devtmpfs`` is used for ``/dev`` population. The
      default value used for :term:`USE_DEVFS` is "1" when no value is
      specifically set. Typically, you would set :term:`USE_DEVFS` to "0" for a
      statically populated ``/dev`` directory.

      See the ":ref:`dev-manual/device-manager:selecting a device manager`" section in
      the Yocto Project Development Tasks Manual for information on how to
      use this variable.

   :term:`USE_VT`
      When using
      :ref:`SysVinit <dev-manual/new-recipe:enabling system services>`,
      determines whether or not to run a :wikipedia:`getty <Getty_(Unix)>`
      on any virtual terminals in order to enable logging in through those
      terminals.

      The default value used for :term:`USE_VT` is "1" when no default value is
      specifically set. Typically, you would set :term:`USE_VT` to "0" in the
      machine configuration file for machines that do not have a graphical
      display attached and therefore do not need virtual terminal
      functionality.

   :term:`USER_CLASSES`
      A list of classes to globally inherit. These classes are used by the
      OpenEmbedded build system to enable extra features.

      Classes inherited using :term:`USER_CLASSES` must be located in the
      ``classes-global/`` or ``classes/`` subdirectories.

      The default list is set in your ``local.conf`` file::

         USER_CLASSES ?= "buildstats"

      For more information, see
      ``meta-poky/conf/templates/default/local.conf.sample`` in the
      :term:`Source Directory`.

   :term:`USERADD_DEPENDS`
      Specifies a list of recipes that create users / groups (via
      :term:`USERADD_PARAM` / :term:`GROUPADD_PARAM`) which a recipe
      depends upon. This ensures that those users / groups are available
      when building a recipe.

   :term:`USERADD_ERROR_DYNAMIC`
      If set to ``error``, forces the OpenEmbedded build system to produce
      an error if the user identification (``uid``) and group
      identification (``gid``) values are not defined in any of the files
      listed in :term:`USERADD_UID_TABLES` and
      :term:`USERADD_GID_TABLES`. If set to
      ``warn``, a warning will be issued instead.

      The default behavior for the build system is to dynamically apply
      ``uid`` and ``gid`` values. Consequently, the
      :term:`USERADD_ERROR_DYNAMIC` variable is by default not set. If you plan
      on using statically assigned ``gid`` and ``uid`` values, you should
      set the :term:`USERADD_ERROR_DYNAMIC` variable in your ``local.conf``
      file as follows::

         USERADD_ERROR_DYNAMIC = "error"

      Overriding the
      default behavior implies you are going to also take steps to set
      static ``uid`` and ``gid`` values through use of the
      :term:`USERADDEXTENSION`,
      :term:`USERADD_UID_TABLES`, and
      :term:`USERADD_GID_TABLES` variables.

      .. note::

         There is a difference in behavior between setting
         :term:`USERADD_ERROR_DYNAMIC` to ``error`` and setting it to ``warn``.
         When it is set to ``warn``, the build system will report a warning for
         every undefined ``uid`` and ``gid`` in any recipe. But when it is set
         to ``error``, it will only report errors for recipes that are actually
         built.
         This saves you from having to add static IDs for recipes that you
         know will never be built.

   :term:`USERADD_GID_TABLES`
      Specifies a password file to use for obtaining static group
      identification (``gid``) values when the OpenEmbedded build system
      adds a group to the system during package installation.

      When applying static group identification (``gid``) values, the
      OpenEmbedded build system looks in :term:`BBPATH` for a
      ``files/group`` file and then applies those ``uid`` values. Set the
      variable as follows in your ``local.conf`` file::


         USERADD_GID_TABLES = "files/group"

      .. note::

         Setting the :term:`USERADDEXTENSION` variable to "useradd-staticids"
         causes the build system to use static ``gid`` values.

   :term:`USERADD_PACKAGES`
      When inheriting the :ref:`ref-classes-useradd` class,
      this variable specifies the individual packages within the recipe
      that require users and/or groups to be added.

      You must set this variable if the recipe inherits the class. For
      example, the following enables adding a user for the main package in
      a recipe::

         USERADD_PACKAGES = "${PN}"

      .. note::

         It follows that if you are going to use the :term:`USERADD_PACKAGES`
         variable, you need to set one or more of the :term:`USERADD_PARAM`,
         :term:`GROUPADD_PARAM`, or :term:`GROUPMEMS_PARAM` variables.

   :term:`USERADD_PARAM`
      When inheriting the :ref:`ref-classes-useradd` class,
      this variable specifies for a package what parameters should pass to
      the ``useradd`` command if you add a user to the system when the
      package is installed.

      Here is an example from the ``dbus`` recipe::

         USERADD_PARAM:${PN} = "--system --home ${localstatedir}/lib/dbus \
                                --no-create-home --shell /bin/false \
                                --user-group messagebus"

      For information on the
      standard Linux shell command ``useradd``, see
      https://linux.die.net/man/8/useradd.

   :term:`USERADD_UID_TABLES`
      Specifies a password file to use for obtaining static user
      identification (``uid``) values when the OpenEmbedded build system
      adds a user to the system during package installation.

      When applying static user identification (``uid``) values, the
      OpenEmbedded build system looks in :term:`BBPATH` for a
      ``files/passwd`` file and then applies those ``uid`` values. Set the
      variable as follows in your ``local.conf`` file::

         USERADD_UID_TABLES = "files/passwd"

      .. note::

         Setting the :term:`USERADDEXTENSION` variable to "useradd-staticids"
         causes the build system to use static ``uid`` values.

   :term:`USERADDEXTENSION`
      When set to "useradd-staticids", causes the OpenEmbedded build system
      to base all user and group additions on a static ``passwd`` and
      ``group`` files found in :term:`BBPATH`.

      To use static user identification (``uid``) and group identification
      (``gid``) values, set the variable as follows in your ``local.conf``
      file: USERADDEXTENSION = "useradd-staticids"

      .. note::

         Setting this variable to use static ``uid`` and ``gid``
         values causes the OpenEmbedded build system to employ the
         :ref:`ref-classes-useradd` class.

      If you use static ``uid`` and ``gid`` information, you must also
      specify the ``files/passwd`` and ``files/group`` files by setting the
      :term:`USERADD_UID_TABLES` and
      :term:`USERADD_GID_TABLES` variables.
      Additionally, you should also set the
      :term:`USERADD_ERROR_DYNAMIC` variable.

   :term:`VIRTUAL-RUNTIME`
      :term:`VIRTUAL-RUNTIME` is a commonly used prefix for defining virtual
      packages for runtime usage, typically for use in :term:`RDEPENDS`
      or in image definitions.

      An example is ``VIRTUAL-RUNTIME_base-utils`` that makes it possible
      to either use BusyBox based utilities::

         VIRTUAL-RUNTIME_base-utils = "busybox"

      or their full featured implementations from GNU Coreutils
      and other projects::

         VIRTUAL-RUNTIME_base-utils = "packagegroup-core-base-utils"

      Here are two examples using this virtual runtime package. The
      first one is in :yocto_git:`initramfs-framework_1.0.bb
      </poky/tree/meta/recipes-core/initrdscripts/initramfs-framework_1.0.bb?h=scarthgap>`::

         RDEPENDS:${PN} += "${VIRTUAL-RUNTIME_base-utils}"

      The second example is in the :yocto_git:`core-image-initramfs-boot
      </poky/tree/meta/recipes-core/images/core-image-initramfs-boot.bb?h=scarthgap>`
      image definition::

         PACKAGE_INSTALL = "${INITRAMFS_SCRIPTS} ${VIRTUAL-RUNTIME_base-utils} base-passwd"

   :term:`VOLATILE_LOG_DIR`
      Specifies the persistence of the target's ``/var/log`` directory,
      which is used to house postinstall target log files.

      By default, :term:`VOLATILE_LOG_DIR` is set to "yes", which means the
      file is not persistent. You can override this setting by setting the
      variable to "no" to make the log directory persistent.

   :term:`VOLATILE_TMP_DIR`
      Specifies the persistence of the target's ``/tmp`` directory.

      By default, :term:`VOLATILE_TMP_DIR` is set to "yes", in which case
      ``/tmp`` links to a directory which resides in RAM in a ``tmpfs``
      filesystem.

      If instead, you want the ``/tmp`` directory to be persistent, set the
      variable to "no" to make it a regular directory in the root filesystem.

      This supports both sysvinit and systemd based systems.

   :term:`WARN_QA`
      Specifies the quality assurance checks whose failures are reported as
      warnings by the OpenEmbedded build system. You set this variable in
      your distribution configuration file. For a list of the checks you
      can control with this variable, see the
      ":ref:`ref-classes-insane`" section.

   :term:`WATCHDOG_TIMEOUT`
      Specifies the timeout in seconds used by the ``watchdog`` recipe and
      also by ``systemd`` during reboot. The default is 60 seconds.

   :term:`WIRELESS_DAEMON`
      For ``connman`` and ``packagegroup-base``, specifies the wireless
      daemon to use. The default is "wpa-supplicant" (note that the value
      uses a dash and not an underscore).

   :term:`WKS_FILE`
      Specifies the location of the Wic kickstart file that is used by the
      OpenEmbedded build system to create a partitioned image
      (``image.wic``). For information on how to create a partitioned
      image, see the
      ":ref:`dev-manual/wic:creating partitioned images using wic`"
      section in the Yocto Project Development Tasks Manual. For details on
      the kickstart file format, see the ":doc:`/ref-manual/kickstart`" Chapter.

   :term:`WKS_FILE_DEPENDS`
      When placed in the recipe that builds your image, this variable lists
      build-time dependencies. The :term:`WKS_FILE_DEPENDS` variable is only
      applicable when Wic images are active (i.e. when
      :term:`IMAGE_FSTYPES` contains entries related
      to Wic). If your recipe does not create Wic images, the variable has
      no effect.

      The :term:`WKS_FILE_DEPENDS` variable is similar to the
      :term:`DEPENDS` variable. When you use the variable in
      your recipe that builds the Wic image, dependencies you list in the
      :term:`WKS_FILE_DEPENDS` variable are added to the :term:`DEPENDS` variable.

      With the :term:`WKS_FILE_DEPENDS` variable, you have the possibility to
      specify a list of additional dependencies (e.g. native tools,
      bootloaders, and so forth), that are required to build Wic images.
      Here is an example::

         WKS_FILE_DEPENDS = "some-native-tool"

      In the
      previous example, some-native-tool would be replaced with an actual
      native tool on which the build would depend.

   :term:`WKS_FILES`
      Specifies a list of candidate Wic kickstart files to be used by the
      OpenEmbedded build system to create a partitioned image. Only the
      first one that is found, from left to right, will be used.

      This is only useful when there are multiple ``.wks`` files that can be
      used to produce an image. A typical case is when multiple layers are
      used for different hardware platforms, each supplying a different
      ``.wks`` file. In this case, you specify all possible ones through
      :term:`WKS_FILES`.

      If only one ``.wks`` file is used, set :term:`WKS_FILE` instead.

   :term:`WORKDIR`
      The pathname of the work directory in which the OpenEmbedded build
      system builds a recipe. This directory is located within the
      :term:`TMPDIR` directory structure and is specific to
      the recipe being built and the system for which it is being built.

      The :term:`WORKDIR` directory is defined as follows::

         ${TMPDIR}/work/${MULTIMACH_TARGET_SYS}/${PN}/${EXTENDPE}${PV}-${PR}

      The actual directory depends on several things:

      -  :term:`TMPDIR`: The top-level build output directory
      -  :term:`MULTIMACH_TARGET_SYS`: The target system identifier
      -  :term:`PN`: The recipe name
      -  :term:`EXTENDPE`: The epoch --- if :term:`PE` is not specified, which
         is usually the case for most recipes, then :term:`EXTENDPE` is blank.
      -  :term:`PV`: The recipe version
      -  :term:`PR`: The recipe revision

      As an example, assume a Source Directory top-level folder name
      ``poky``, a default :term:`Build Directory` at ``poky/build``, and a
      ``qemux86-poky-linux`` machine target system. Furthermore, suppose
      your recipe is named ``foo_1.3.0-r0.bb``. In this case, the work
      directory the build system uses to build the package would be as
      follows::

         poky/build/tmp/work/qemux86-poky-linux/foo/1.3.0-r0

   :term:`XSERVER`
      Specifies the packages that should be installed to provide an X
      server and drivers for the current machine, assuming your image
      directly includes ``packagegroup-core-x11-xserver`` or, perhaps
      indirectly, includes "x11-base" in
      :term:`IMAGE_FEATURES`.

      The default value of :term:`XSERVER`, if not specified in the machine
      configuration, is "xserver-xorg xf86-video-fbdev xf86-input-evdev".

   :term:`XZ_THREADS`
      Specifies the number of parallel threads that should be used when
      using xz compression.

      By default this scales with core count, but is never set less than 2
      to ensure that multi-threaded mode is always used so that the output
      file contents are deterministic. Builds will work with a value of 1
      but the output will differ compared to the output from the compression
      generated when more than one thread is used.

      On systems where many tasks run in parallel, setting a limit to this
      can be helpful in controlling system resource usage.

   :term:`XZ_MEMLIMIT`
      Specifies the maximum memory the xz compression should use as a percentage
      of system memory. If unconstrained the xz compressor can use large amounts of
      memory and become problematic with parallelism elsewhere in the build.
      "50%" has been found to be a good value.

   :term:`ZSTD_THREADS`
      Specifies the number of parallel threads that should be used when
      using ZStandard compression.

      By default this scales with core count, but is never set less than 2
      to ensure that multi-threaded mode is always used so that the output
      file contents are deterministic. Builds will work with a value of 1
      but the output will differ compared to the output from the compression
      generated when more than one thread is used.

      On systems where many tasks run in parallel, setting a limit to this
      can be helpful in controlling system resource usage.
