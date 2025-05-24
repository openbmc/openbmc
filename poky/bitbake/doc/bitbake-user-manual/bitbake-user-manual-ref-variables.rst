.. SPDX-License-Identifier: CC-BY-2.5

==================
Variables Glossary
==================

|

This chapter lists common variables used by BitBake and gives an
overview of their function and contents.

.. note::

   Following are some points regarding the variables listed in this
   glossary:

   -  The variables listed in this glossary are specific to BitBake.
      Consequently, the descriptions are limited to that context.

   -  Also, variables exist in other systems that use BitBake (e.g. The
      Yocto Project and OpenEmbedded) that have names identical to those
      found in this glossary. For such cases, the variables in those
      systems extend the functionality of the variable as it is
      described here in this glossary.

.. glossary::
   :sorted:

   :term:`ASSUME_PROVIDED`
      Lists recipe names (:term:`PN` values) BitBake does not
      attempt to build. Instead, BitBake assumes these recipes have already
      been built.

      In OpenEmbedded-Core, :term:`ASSUME_PROVIDED` mostly specifies native
      tools that should not be built. An example is ``git-native``, which
      when specified allows for the Git binary from the host to be used
      rather than building ``git-native``.

   :term:`AZ_SAS`
      Azure Storage Shared Access Signature, when using the
      :ref:`Azure Storage fetcher <bitbake-user-manual/bitbake-user-manual-fetching:fetchers>`
      This variable can be defined to be used by the fetcher to authenticate
      and gain access to non-public artifacts::

         AZ_SAS = ""se=2021-01-01&sp=r&sv=2018-11-09&sr=c&skoid=<skoid>&sig=<signature>""

      For more information see Microsoft's Azure Storage documentation at
      https://docs.microsoft.com/en-us/azure/storage/common/storage-sas-overview


   :term:`B`
      The directory in which BitBake executes functions during a recipe's
      build process.

   :term:`BB_ALLOWED_NETWORKS`
      Specifies a space-delimited list of hosts that the fetcher is allowed
      to use to obtain the required source code. Following are
      considerations surrounding this variable:

      -  This host list is only used if
         :term:`BB_NO_NETWORK` is either not set or
         set to "0".

      -  Limited support for the "``*``" wildcard character for matching
         against the beginning of host names exists. For example, the
         following setting matches ``git.gnu.org``, ``ftp.gnu.org``, and
         ``foo.git.gnu.org``. ::

            BB_ALLOWED_NETWORKS = "\*.gnu.org"

         .. important::

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
      :term:`PREMIRRORS` is very useful. Adding the
      host you want to use to :term:`PREMIRRORS` results in the source code
      being fetched from an allowed location and avoids raising an error
      when a host that is not allowed is in a
      :term:`SRC_URI` statement. This is because the
      fetcher does not attempt to use the host listed in :term:`SRC_URI` after
      a successful fetch from the :term:`PREMIRRORS` occurs.

   :term:`BB_BASEHASH_IGNORE_VARS`
      Lists variables that are excluded from checksum and dependency data.
      Variables that are excluded can therefore change without affecting
      the checksum mechanism. A common example would be the variable for
      the path of the build. BitBake's output should not (and usually does
      not) depend on the directory in which it was built.

   :term:`BB_CACHEDIR`
      Specifies the code parser cache directory (distinct from :term:`CACHE`
      and :term:`PERSISTENT_DIR` although they can be set to the same value
      if desired). The default value is "${TOPDIR}/cache".

   :term:`BB_CHECK_SSL_CERTS`
      Specifies if SSL certificates should be checked when fetching. The default
      value is ``1`` and certificates are not checked if the value is set to ``0``.

   :term:`BB_HASH_CODEPARSER_VALS`
      Specifies values for variables to use when populating the codeparser cache.
      This can be used selectively to set dummy values for variables to avoid
      the codeparser cache growing on every parse. Variables that would typically
      be included are those where the value is not significant for where the
      codeparser cache is used (i.e. when calculating variable dependencies for
      code fragments.) The value is space-separated without quoting values, for
      example::

         BB_HASH_CODEPARSER_VALS = "T=/ WORKDIR=/ DATE=1234 TIME=1234"

   :term:`BB_CONSOLELOG`
      Specifies the path to a log file into which BitBake's user interface
      writes output during the build.

   :term:`BB_CURRENTTASK`
      Contains the name of the currently running task. The name does not
      include the ``do_`` prefix.

   :term:`BB_CURRENT_MC`
      Contains the name of the current multiconfig a task is being run under.
      The name is taken from the multiconfig configuration file (a file
      ``mc1.conf`` would make this variable equal to ``mc1``).

   :term:`BB_DEFAULT_TASK`
      The default task to use when none is specified (e.g. with the ``-c``
      command line option). The task name specified should not include the
      ``do_`` prefix.

   :term:`BB_DEFAULT_UMASK`
      The default umask to apply to tasks if specified and no task specific
      umask flag is set.

   :term:`BB_DISKMON_DIRS`
      Monitors disk space and available inodes during the build and allows
      you to control the build based on these parameters.

      Disk space monitoring is disabled by default. When setting this
      variable, use the following form::

         BB_DISKMON_DIRS = "<action>,<dir>,<threshold> [...]"

         where:

            <action> is:
               HALT:      Immediately halt the build when
                          a threshold is broken.
               STOPTASKS: Stop the build after the currently
                          executing tasks have finished when
                          a threshold is broken.
               WARN:      Issue a warning but continue the
                          build when a threshold is broken.
                          Subsequent warnings are issued as
                          defined by the
                          BB_DISKMON_WARNINTERVAL variable,
                          which must be defined.

            <dir> is:
               Any directory you choose. You can specify one or
               more directories to monitor by separating the
               groupings with a space.  If two directories are
               on the same device, only the first directory
               is monitored.

            <threshold> is:
               Either the minimum available disk space,
               the minimum number of free inodes, or
               both.  You must specify at least one.  To
               omit one or the other, simply omit the value.
               Specify the threshold using G, M, K for Gbytes,
               Mbytes, and Kbytes, respectively. If you do
               not specify G, M, or K, Kbytes is assumed by
               default.  Do not use GB, MB, or KB.

      Here are some examples::

         BB_DISKMON_DIRS = "HALT,${TMPDIR},1G,100K WARN,${SSTATE_DIR},1G,100K"
         BB_DISKMON_DIRS = "STOPTASKS,${TMPDIR},1G"
         BB_DISKMON_DIRS = "HALT,${TMPDIR},,100K"

      The first example works only if you also set the
      :term:`BB_DISKMON_WARNINTERVAL`
      variable. This example causes the build system to immediately halt
      when either the disk space in ``${TMPDIR}`` drops below 1 Gbyte or
      the available free inodes drops below 100 Kbytes. Because two
      directories are provided with the variable, the build system also
      issues a warning when the disk space in the ``${SSTATE_DIR}``
      directory drops below 1 Gbyte or the number of free inodes drops
      below 100 Kbytes. Subsequent warnings are issued during intervals as
      defined by the :term:`BB_DISKMON_WARNINTERVAL` variable.

      The second example stops the build after all currently executing
      tasks complete when the minimum disk space in the ``${TMPDIR}``
      directory drops below 1 Gbyte. No disk monitoring occurs for the free
      inodes in this case.

      The final example immediately halts the build when the number of
      free inodes in the ``${TMPDIR}`` directory drops below 100 Kbytes. No
      disk space monitoring for the directory itself occurs in this case.

   :term:`BB_DISKMON_WARNINTERVAL`
      Defines the disk space and free inode warning intervals.

      If you are going to use the :term:`BB_DISKMON_WARNINTERVAL` variable, you
      must also use the :term:`BB_DISKMON_DIRS`
      variable and define its action as "WARN". During the build,
      subsequent warnings are issued each time disk space or number of free
      inodes further reduces by the respective interval.

      If you do not provide a :term:`BB_DISKMON_WARNINTERVAL` variable and you
      do use :term:`BB_DISKMON_DIRS` with the "WARN" action, the disk
      monitoring interval defaults to the following:
      BB_DISKMON_WARNINTERVAL = "50M,5K"

      When specifying the variable in your configuration file, use the
      following form::

         BB_DISKMON_WARNINTERVAL = "<disk_space_interval>,<disk_inode_interval>"

         where:

            <disk_space_interval> is:
               An interval of memory expressed in either
               G, M, or K for Gbytes, Mbytes, or Kbytes,
               respectively. You cannot use GB, MB, or KB.

            <disk_inode_interval> is:
               An interval of free inodes expressed in either
               G, M, or K for Gbytes, Mbytes, or Kbytes,
               respectively. You cannot use GB, MB, or KB.

      Here is an example::

         BB_DISKMON_DIRS = "WARN,${SSTATE_DIR},1G,100K"
         BB_DISKMON_WARNINTERVAL = "50M,5K"

      These variables cause BitBake to
      issue subsequent warnings each time the available disk space further
      reduces by 50 Mbytes or the number of free inodes further reduces by
      5 Kbytes in the ``${SSTATE_DIR}`` directory. Subsequent warnings
      based on the interval occur each time a respective interval is
      reached beyond the initial warning (i.e. 1 Gbytes and 100 Kbytes).

   :term:`BB_ENV_PASSTHROUGH`
      Specifies the internal list of variables to allow through from
      the external environment into BitBake's datastore. If the value of
      this variable is not specified (which is the default), the following
      list is used: :term:`BBPATH`, :term:`BB_PRESERVE_ENV`,
      :term:`BB_ENV_PASSTHROUGH`, and :term:`BB_ENV_PASSTHROUGH_ADDITIONS`.

      .. note::

         You must set this variable in the external environment in order
         for it to work.

   :term:`BB_ENV_PASSTHROUGH_ADDITIONS`
      Specifies an additional set of variables to allow through from the
      external environment into BitBake's datastore. This list of variables
      are on top of the internal list set in
      :term:`BB_ENV_PASSTHROUGH`.

      .. note::

         You must set this variable in the external environment in order
         for it to work.

   :term:`BB_FETCH_PREMIRRORONLY`
      When set to "1", causes BitBake's fetcher module to only search
      :term:`PREMIRRORS` for files. BitBake will not
      search the main :term:`SRC_URI` or
      :term:`MIRRORS`.

   :term:`BB_FILENAME`
      Contains the filename of the recipe that owns the currently running
      task. For example, if the ``do_fetch`` task that resides in the
      ``my-recipe.bb`` is executing, the :term:`BB_FILENAME` variable contains
      "/foo/path/my-recipe.bb".

   :term:`BB_GENERATE_MIRROR_TARBALLS`
      Causes tarballs of the Git repositories, including the Git metadata,
      to be placed in the :term:`DL_DIR` directory. Anyone
      wishing to create a source mirror would want to enable this variable.

      For performance reasons, creating and placing tarballs of the Git
      repositories is not the default action by BitBake. ::

         BB_GENERATE_MIRROR_TARBALLS = "1"

   :term:`BB_GENERATE_SHALLOW_TARBALLS`
      Setting this variable to "1" when :term:`BB_GIT_SHALLOW` is also set to
      "1" causes bitbake to generate shallow mirror tarballs when fetching git
      repositories. The number of commits included in the shallow mirror
      tarballs is controlled by :term:`BB_GIT_SHALLOW_DEPTH`.

      If both :term:`BB_GIT_SHALLOW` and :term:`BB_GENERATE_MIRROR_TARBALLS` are
      enabled, bitbake will generate shallow mirror tarballs by default for git
      repositories. This separate variable exists so that shallow tarball
      generation can be enabled without needing to also enable normal mirror
      generation if it is not desired.

      For example usage, see :term:`BB_GIT_SHALLOW`.

   :term:`BB_GIT_SHALLOW`
      Setting this variable to "1" enables the support for fetching, using and
      generating mirror tarballs of `shallow git repositories <https://riptutorial.com/git/example/4584/shallow-clone>`_.
      The external `git-make-shallow <https://git.openembedded.org/bitbake/tree/bin/git-make-shallow>`_
      script is used for shallow mirror tarball creation.

      When :term:`BB_GIT_SHALLOW` is enabled, bitbake will attempt to fetch a shallow
      mirror tarball. If the shallow mirror tarball cannot be fetched, it will
      try to fetch the full mirror tarball and use that.

      This setting causes an initial shallow clone instead of an initial full bare clone.
      The amount of data transferred during the initial clone will be significantly reduced.

      However, every time the source revision (referenced in :term:`SRCREV`)
      changes, regardless of whether the cache within the download directory
      (defined by :term:`DL_DIR`) has been cleaned up or not,
      the data transfer may be significantly higher because entirely
      new shallow clones are required for each source revision change.

      Over time, numerous shallow clones may cumulatively transfer
      the same amount of data as an initial full bare clone.
      This is especially the case with very large repositories.

      Existing initial full bare clones, created without this setting,
      will still be utilized.

      If the Git error "Server does not allow request for unadvertised object"
      occurs, an initial full bare clone is fetched automatically.
      This may happen if the Git server does not allow the request
      or if the Git client has issues with this functionality.

      See also :term:`BB_GIT_SHALLOW_DEPTH` and
      :term:`BB_GENERATE_SHALLOW_TARBALLS`.

      Example usage::

         BB_GIT_SHALLOW ?= "1"

         # Keep only the top commit
         BB_GIT_SHALLOW_DEPTH ?= "1"

         # This defaults to enabled if both BB_GIT_SHALLOW and
         # BB_GENERATE_MIRROR_TARBALLS are enabled
         BB_GENERATE_SHALLOW_TARBALLS ?= "1"

   :term:`BB_GIT_SHALLOW_DEPTH`
      When used with :term:`BB_GENERATE_SHALLOW_TARBALLS`, this variable sets
      the number of commits to include in generated shallow mirror tarballs.
      With a depth of 1, only the commit referenced in :term:`SRCREV` is
      included in the shallow mirror tarball. Increasing the depth includes
      additional parent commits, working back through the commit history.

      If this variable is unset, bitbake will default to a depth of 1 when
      generating shallow mirror tarballs.

      For example usage, see :term:`BB_GIT_SHALLOW`.

   :term:`BB_GLOBAL_PYMODULES`
      Specifies the list of Python modules to place in the global namespace.
      It is intended that only the core layer should set this and it is meant
      to be a very small list, typically just ``os`` and ``sys``.
      :term:`BB_GLOBAL_PYMODULES` is expected to be set before the first
      ``addpylib`` directive.
      See also ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:extending python library code`".

   :term:`BB_HASHCHECK_FUNCTION`
      Specifies the name of the function to call during the "setscene" part
      of the task's execution in order to validate the list of task hashes.
      The function returns the list of setscene tasks that should be
      executed.

      At this point in the execution of the code, the objective is to
      quickly verify if a given setscene function is likely to work or not.
      It's easier to check the list of setscene functions in one pass than
      to call many individual tasks. The returned list need not be
      completely accurate. A given setscene task can still later fail.
      However, the more accurate the data returned, the more efficient the
      build will be.

   :term:`BB_HASHCONFIG_IGNORE_VARS`
      Lists variables that are excluded from base configuration checksum,
      which is used to determine if the cache can be reused.

      One of the ways BitBake determines whether to re-parse the main
      metadata is through checksums of the variables in the datastore of
      the base configuration data. There are variables that you typically
      want to exclude when checking whether or not to re-parse and thus
      rebuild the cache. As an example, you would usually exclude ``TIME``
      and ``DATE`` because these variables are always changing. If you did
      not exclude them, BitBake would never reuse the cache.

   :term:`BB_HASHSERVE`
      Specifies the Hash Equivalence server to use.

      If set to ``auto``, BitBake automatically starts its own server
      over a UNIX domain socket. An option is to connect this server
      to an upstream one, by setting :term:`BB_HASHSERVE_UPSTREAM`.

      If set to ``unix://path``, BitBake will connect to an existing
      hash server available over a UNIX domain socket.

      If set to ``host:port``, BitBake will connect to a remote server on the
      specified host. This allows multiple clients to share the same
      hash equivalence data.

      The remote server can be started manually through
      the ``bin/bitbake-hashserv`` script provided by BitBake,
      which supports UNIX domain sockets too. This script also allows
      to start the server in read-only mode, to avoid accepting
      equivalences that correspond to Share State caches that are
      only available on specific clients.

   :term:`BB_HASHSERVE_UPSTREAM`
      Specifies an upstream Hash Equivalence server.

      This optional setting is only useful when a local Hash Equivalence
      server is started (setting :term:`BB_HASHSERVE` to ``auto``),
      and you wish the local server to query an upstream server for
      Hash Equivalence data.

      Example usage::

         BB_HASHSERVE_UPSTREAM = "hashserv.yoctoproject.org:8686"

   :term:`BB_INVALIDCONF`
      Used in combination with the ``ConfigParsed`` event to trigger
      re-parsing the base metadata (i.e. all the recipes). The
      ``ConfigParsed`` event can set the variable to trigger the re-parse.
      You must be careful to avoid recursive loops with this functionality.

   :term:`BB_LOADFACTOR_MAX`
      Setting this to a value will cause BitBake to check the system load
      average before executing new tasks. If the load average is above the
      the number of CPUs multipled by this factor, no new task will be started
      unless there is no task executing. A value of "1.5" has been found to
      work reasonably. This is helpful for systems which don't have pressure
      regulation enabled, which is more granular. Pressure values take
      precedence over loadfactor.

   :term:`BB_LOGCONFIG`
      Specifies the name of a config file that contains the user logging
      configuration. See
      :ref:`bitbake-user-manual/bitbake-user-manual-execution:logging`
      for additional information

   :term:`BB_LOGFMT`
      Specifies the name of the log files saved into
      ``${``\ :term:`T`\ ``}``. By default, the :term:`BB_LOGFMT`
      variable is undefined and the log filenames get created using the
      following form::

         log.{task}.{pid}

      If you want to force log files to take a specific name, you can set this
      variable in a configuration file.

   :term:`BB_MULTI_PROVIDER_ALLOWED`
      Allows you to suppress BitBake warnings caused when building two
      separate recipes that provide the same output.

      BitBake normally issues a warning when building two different recipes
      where each provides the same output. This scenario is usually
      something the user does not want. However, cases do exist where it
      makes sense, particularly in the ``virtual/*`` namespace. You can use
      this variable to suppress BitBake's warnings.

      To use the variable, list provider names (e.g. recipe names,
      ``virtual/kernel``, and so forth).

   :term:`BB_NICE_LEVEL`
      Allows BitBake to run at a specific priority (i.e. nice level).
      System permissions usually mean that BitBake can reduce its priority
      but not raise it again. See :term:`BB_TASK_NICE_LEVEL` for
      additional information.

   :term:`BB_NO_NETWORK`
      Disables network access in the BitBake fetcher modules. With this
      access disabled, any command that attempts to access the network
      becomes an error.

      Disabling network access is useful for testing source mirrors,
      running builds when not connected to the Internet, and when operating
      in certain kinds of firewall environments.

   :term:`BB_NUMBER_PARSE_THREADS`
      Sets the number of threads BitBake uses when parsing. By default, the
      number of threads is equal to the number of cores on the system.

   :term:`BB_NUMBER_THREADS`
      The maximum number of tasks BitBake should run in parallel at any one
      time. If your host development system supports multiple cores, a good
      rule of thumb is to set this variable to twice the number of cores.

   :term:`BB_ORIGENV`
      Contains a copy of the original external environment in which BitBake
      was run. The copy is taken before any variable values configured to
      pass through from the external environment are filtered into BitBake's
      datastore.

      .. note::

         The contents of this variable is a datastore object that can be
         queried using the normal datastore operations.

   :term:`BB_PRESERVE_ENV`
      Disables environment filtering and instead allows all variables through
      from the external environment into BitBake's datastore.

      .. note::

         You must set this variable in the external environment in order
         for it to work.

   :term:`BB_PRESSURE_MAX_CPU`
      Specifies a maximum CPU pressure threshold, above which BitBake's
      scheduler will not start new tasks (providing there is at least
      one active task). If no value is set, CPU pressure is not
      monitored when starting tasks.

      The pressure data is calculated based upon what Linux kernels since
      version 4.20 expose under ``/proc/pressure``. The threshold represents
      the difference in "total" pressure from the previous second. The
      minimum value is 1.0 (extremely slow builds) and the maximum is
      1000000 (a pressure value unlikely to ever be reached).

      This threshold can be set in ``conf/local.conf`` as::

         BB_PRESSURE_MAX_CPU = "500"

   :term:`BB_PRESSURE_MAX_IO`
      Specifies a maximum I/O pressure threshold, above which BitBake's
      scheduler will not start new tasks (providing there is at least
      one active task). If no value is set, I/O pressure is not
      monitored when starting tasks.

      The pressure data is calculated based upon what Linux kernels since
      version 4.20 expose under ``/proc/pressure``. The threshold represents
      the difference in "total" pressure from the previous second. The
      minimum value is 1.0 (extremely slow builds) and the maximum is
      1000000 (a pressure value unlikely to ever be reached).

      At this point in time, experiments show that IO pressure tends to
      be short-lived and regulating just the CPU with
      :term:`BB_PRESSURE_MAX_CPU` can help to reduce it.

   :term:`BB_PRESSURE_MAX_MEMORY`

      Specifies a maximum memory pressure threshold, above which BitBake's
      scheduler will not start new tasks (providing there is at least
      one active task). If no value is set, memory pressure is not
      monitored when starting tasks.

      The pressure data is calculated based upon what Linux kernels since
      version 4.20 expose under ``/proc/pressure``. The threshold represents
      the difference in "total" pressure from the previous second. The
      minimum value is 1.0 (extremely slow builds) and the maximum is
      1000000 (a pressure value unlikely to ever be reached).

      Memory pressure is experienced when time is spent swapping,
      refaulting pages from the page cache or performing direct reclaim.
      This is why memory pressure is rarely seen, but setting this variable
      might be useful as a last resort to prevent OOM errors if they are
      occurring during builds.

   :term:`BB_RUNFMT`
      Specifies the name of the executable script files (i.e. run files)
      saved into ``${``\ :term:`T`\ ``}``. By default, the
      :term:`BB_RUNFMT` variable is undefined and the run filenames get
      created using the following form::

         run.{func}.{pid}

      If you want to force run files to take a specific name, you can set this
      variable in a configuration file.

   :term:`BB_RUNTASK`
      Contains the name of the currently executing task. The value includes
      the "do\_" prefix. For example, if the currently executing task is
      ``do_config``, the value is "do_config".

   :term:`BB_SCHEDULER`
      Selects the name of the scheduler to use for the scheduling of
      BitBake tasks. Three options exist:

      -  *basic* --- the basic framework from which everything derives. Using
         this option causes tasks to be ordered numerically as they are
         parsed.

      -  *speed* --- executes tasks first that have more tasks depending on
         them. The "speed" option is the default.

      -  *completion* --- causes the scheduler to try to complete a given
         recipe once its build has started.

   :term:`BB_SCHEDULERS`
      Defines custom schedulers to import. Custom schedulers need to be
      derived from the ``RunQueueScheduler`` class.

      For information how to select a scheduler, see the
      :term:`BB_SCHEDULER` variable.

   :term:`BB_SETSCENE_DEPVALID`
      Specifies a function BitBake calls that determines whether BitBake
      requires a setscene dependency to be met.

      When running a setscene task, BitBake needs to know which
      dependencies of that setscene task also need to be run. Whether
      dependencies also need to be run is highly dependent on the metadata.
      The function specified by this variable returns a "True" or "False"
      depending on whether the dependency needs to be met.

   :term:`BB_SIGNATURE_EXCLUDE_FLAGS`
      Lists variable flags (varflags) that can be safely excluded from
      checksum and dependency data for keys in the datastore. When
      generating checksum or dependency data for keys in the datastore, the
      flags set against that key are normally included in the checksum.

      For more information on varflags, see the
      ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:variable flags`"
      section.

   :term:`BB_SIGNATURE_HANDLER`
      Defines the name of the signature handler BitBake uses. The signature
      handler defines the way stamp files are created and handled, if and
      how the signature is incorporated into the stamps, and how the
      signature itself is generated.

      A new signature handler can be added by injecting a class derived
      from the ``SignatureGenerator`` class into the global namespace.

   :term:`BB_SRCREV_POLICY`
      Defines the behavior of the fetcher when it interacts with source
      control systems and dynamic source revisions. The
      :term:`BB_SRCREV_POLICY` variable is useful when working without a
      network.

      The variable can be set using one of two policies:

      -  *cache* --- retains the value the system obtained previously rather
         than querying the source control system each time.

      -  *clear* --- queries the source controls system every time. With this
         policy, there is no cache. The "clear" policy is the default.

   :term:`BB_STRICT_CHECKSUM`
      Sets a more strict checksum mechanism for non-local URLs. Setting
      this variable to a value causes BitBake to report an error if it
      encounters a non-local URL that does not have at least one checksum
      specified.

   :term:`BB_TASK_IONICE_LEVEL`
      Allows adjustment of a task's Input/Output priority. During
      Autobuilder testing, random failures can occur for tasks due to I/O
      starvation. These failures occur during various QEMU runtime
      timeouts. You can use the :term:`BB_TASK_IONICE_LEVEL` variable to adjust
      the I/O priority of these tasks.

      .. note::

         This variable works similarly to the :term:`BB_TASK_NICE_LEVEL`
         variable except with a task's I/O priorities.

      Set the variable as follows::

         BB_TASK_IONICE_LEVEL = "class.prio"

      For *class*, the default value is "2", which is a best effort. You can use
      "1" for realtime and "3" for idle. If you want to use realtime, you
      must have superuser privileges.

      For *prio*, you can use any value from "0", which is the highest
      priority, to "7", which is the lowest. The default value is "4". You
      do not need any special privileges to use this range of priority
      values.

      .. note::

         In order for your I/O priority settings to take effect, you need the
         Completely Fair Queuing (CFQ) Scheduler selected for the backing block
         device. To select the scheduler, use the following command form where
         device is the device (e.g. sda, sdb, and so forth)::

            $ sudo sh -c "echo cfq > /sys/block/device/queu/scheduler"

   :term:`BB_TASK_NICE_LEVEL`
      Allows specific tasks to change their priority (i.e. nice level).

      You can use this variable in combination with task overrides to raise
      or lower priorities of specific tasks. For example, on the `Yocto
      Project <https://www.yoctoproject.org>`__ autobuilder, QEMU emulation
      in images is given a higher priority as compared to build tasks to
      ensure that images do not suffer timeouts on loaded systems.

   :term:`BB_TASKHASH`
      Within an executing task, this variable holds the hash of the task as
      returned by the currently enabled signature generator.

   :term:`BB_USE_HOME_NPMRC`
      Controls whether or not BitBake uses the user's .npmrc file within their
      home directory within the npm fetcher. This can be used for authentication
      of private NPM registries, among other uses. This is turned off by default
      and requires the user to explicitly set it to "1" to enable.

   :term:`BB_VERBOSE_LOGS`
      Controls how verbose BitBake is during builds. If set, shell scripts
      echo commands and shell script output appears on standard out
      (stdout).

   :term:`BB_WORKERCONTEXT`
      Specifies if the current context is executing a task. BitBake sets
      this variable to "1" when a task is being executed. The value is not
      set when the task is in server context during parsing or event
      handling.

   :term:`BBCLASSEXTEND`
      Allows you to extend a recipe so that it builds variants of the
      software. Some examples of these variants for recipes from the
      OpenEmbedded-Core metadata are "natives" such as ``quilt-native``,
      which is a copy of Quilt built to run on the build system; "crosses"
      such as ``gcc-cross``, which is a compiler built to run on the build
      machine but produces binaries that run on the target ``MACHINE``;
      "nativesdk", which targets the SDK machine instead of ``MACHINE``;
      and "mulitlibs" in the form "``multilib:``\ multilib_name".

      To build a different variant of the recipe with a minimal amount of
      code, it usually is as simple as adding the variable to your recipe.
      Here are two examples. The "native" variants are from the
      OpenEmbedded-Core metadata::

         BBCLASSEXTEND =+ "native nativesdk"
         BBCLASSEXTEND =+ "multilib:multilib_name"

      .. note::

         Internally, the :term:`BBCLASSEXTEND` mechanism generates recipe
         variants by rewriting variable values and applying overrides such
         as ``_class-native``. For example, to generate a native version of
         a recipe, a :term:`DEPENDS` on "foo" is
         rewritten to a :term:`DEPENDS` on "foo-native".

         Even when using :term:`BBCLASSEXTEND`, the recipe is only parsed once.
         Parsing once adds some limitations. For example, it is not
         possible to include a different file depending on the variant,
         since ``include`` statements are processed when the recipe is
         parsed.

   :term:`BBDEBUG`
      Sets the BitBake debug output level to a specific value as
      incremented by the ``-D`` command line option.

      .. note::

         You must set this variable in the external environment in order
         for it to work.

   :term:`BBFILE_COLLECTIONS`
      Lists the names of configured layers. These names are used to find
      the other ``BBFILE_*`` variables. Typically, each layer appends its
      name to this variable in its ``conf/layer.conf`` file.

   :term:`BBFILE_PATTERN`
      Variable that expands to match files from
      :term:`BBFILES` in a particular layer. This
      variable is used in the ``conf/layer.conf`` file and must be suffixed
      with the name of the specific layer (e.g.
      ``BBFILE_PATTERN_emenlow``).

   :term:`BBFILE_PRIORITY`
      Assigns the priority for recipe files in each layer.

      This variable is used in the ``conf/layer.conf`` file and must be
      suffixed with a `_` followed by the name of the specific layer (e.g.
      ``BBFILE_PRIORITY_emenlow``). Colon as separator is not supported.

      This variable is useful in situations where the same recipe appears
      in more than one layer. Setting this variable allows you to
      prioritize a layer against other layers that contain the same recipe
      --- effectively letting you control the precedence for the multiple
      layers. The precedence established through this variable stands
      regardless of a recipe's version (:term:`PV` variable).
      For example, a layer that has a recipe with a higher :term:`PV` value but
      for which the :term:`BBFILE_PRIORITY` is set to have a lower precedence
      still has a lower precedence.

      A larger value for the :term:`BBFILE_PRIORITY` variable results in a
      higher precedence. For example, the value 6 has a higher precedence
      than the value 5. If not specified, the :term:`BBFILE_PRIORITY` variable
      is set based on layer dependencies (see the :term:`LAYERDEPENDS` variable
      for more information). The default priority, if unspecified for a
      layer with no dependencies, is the lowest defined priority + 1 (or 1
      if no priorities are defined).

      .. tip::

         You can use the command bitbake-layers show-layers to list all
         configured layers along with their priorities.

   :term:`BBFILES`
      A space-separated list of recipe files BitBake uses to build
      software.

      When specifying recipe files, you can pattern match using Python's
      `glob <https://docs.python.org/3/library/glob.html>`_ syntax.
      For details on the syntax, see the documentation by following the
      previous link.

   :term:`BBFILES_DYNAMIC`
      Activates content depending on presence of identified layers.  You
      identify the layers by the collections that the layers define.

      Use the :term:`BBFILES_DYNAMIC` variable to avoid ``.bbappend`` files whose
      corresponding ``.bb`` file is in a layer that attempts to modify other
      layers through ``.bbappend`` but does not want to introduce a hard
      dependency on those other layers.

      Additionally you can prefix the rule with "!" to add ``.bbappend`` and
      ``.bb`` files in case a layer is not present.  Use this avoid hard
      dependency on those other layers.

      Use the following form for :term:`BBFILES_DYNAMIC`::

         collection_name:filename_pattern

      The following example identifies two collection names and two filename
      patterns::

         BBFILES_DYNAMIC += "\
             clang-layer:${LAYERDIR}/bbappends/meta-clang/*/*/*.bbappend \
             core:${LAYERDIR}/bbappends/openembedded-core/meta/*/*/*.bbappend \
         "

      When the collection name is prefixed with "!" it will add the file pattern in case
      the layer is absent::

         BBFILES_DYNAMIC += "\
             !clang-layer:${LAYERDIR}/backfill/meta-clang/*/*/*.bb \
         "

      This next example shows an error message that occurs because invalid
      entries are found, which cause parsing to fail::

         ERROR: BBFILES_DYNAMIC entries must be of the form {!}<collection name>:<filename pattern>, not:
         /work/my-layer/bbappends/meta-security-isafw/*/*/*.bbappend
         /work/my-layer/bbappends/openembedded-core/meta/*/*/*.bbappend

   :term:`BBINCLUDED`
      Contains a space-separated list of all of all files that BitBake's
      parser included during parsing of the current file.

   :term:`BBINCLUDELOGS`
      If set to a value, enables printing the task log when reporting a
      failed task.

   :term:`BBINCLUDELOGS_LINES`
      If :term:`BBINCLUDELOGS` is set, specifies
      the maximum number of lines from the task log file to print when
      reporting a failed task. If you do not set :term:`BBINCLUDELOGS_LINES`,
      the entire log is printed.

   :term:`BBLAYERS`
      Lists the layers to enable during the build. This variable is defined
      in the ``bblayers.conf`` configuration file in the build directory.
      Here is an example::

         BBLAYERS = " \
             /home/scottrif/poky/meta \
             /home/scottrif/poky/meta-yocto \
             /home/scottrif/poky/meta-yocto-bsp \
             /home/scottrif/poky/meta-mykernel \
         "

      This example enables four layers, one of which is a custom, user-defined
      layer named ``meta-mykernel``.

   :term:`BBLAYERS_FETCH_DIR`
      Sets the base location where layers are stored. This setting is used
      in conjunction with ``bitbake-layers layerindex-fetch`` and tells
      ``bitbake-layers`` where to place the fetched layers.

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
      documentation at http://docs.python.org/3/library/re.html.

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
      Enables BitBake to perform multiple configuration builds and lists
      each separate configuration (multiconfig). You can use this variable
      to cause BitBake to build multiple targets where each target has a
      separate configuration. Define :term:`BBMULTICONFIG` in your
      ``conf/local.conf`` configuration file.

      As an example, the following line specifies three multiconfigs, each
      having a separate configuration file::

         BBMULTIFONFIG = "configA configB configC"

      Each configuration file you use must reside in the
      build directory within a directory named ``conf/multiconfig`` (e.g.
      build_directory\ ``/conf/multiconfig/configA.conf``).

      For information on how to use :term:`BBMULTICONFIG` in an environment
      that supports building targets with multiple configurations, see the
      ":ref:`bitbake-user-manual/bitbake-user-manual-intro:executing a multiple configuration build`"
      section.

   :term:`BBPATH`
      A colon-separated list used by BitBake to locate class (``.bbclass``)
      and configuration (``.conf``) files. This variable is analogous to the
      ``PATH`` variable.

      If you run BitBake from a directory outside of the build directory,
      you must be sure to set :term:`BBPATH` to point to the build directory.
      Set the variable as you would any environment variable and then run
      BitBake::

         $ BBPATH="build_directory"
         $ export BBPATH
         $ bitbake target

   :term:`BBSERVER`
      Points to the server that runs memory-resident BitBake. The variable
      is only used when you employ memory-resident BitBake.

   :term:`BBTARGETS`
      Allows you to use a configuration file to add to the list of
      command-line target recipes you want to build.

   :term:`BITBAKE_UI`
      Used to specify the UI module to use when running BitBake. Using this
      variable is equivalent to using the ``-u`` command-line option.

      .. note::

         You must set this variable in the external environment in order
         for it to work.

   :term:`BUILDNAME`
      A name assigned to the build. The name defaults to a datetime stamp
      of when the build was started but can be defined by the metadata.

   :term:`BZRDIR`
      The directory in which files checked out of a Bazaar system are
      stored.

   :term:`CACHE`
      Specifies the directory BitBake uses to store a cache of the metadata
      so it does not need to be parsed every time BitBake is started.

   :term:`CVSDIR`
      The directory in which files checked out under the CVS system are
      stored.

   :term:`DEFAULT_PREFERENCE`
      Specifies a weak bias for recipe selection priority.

      The most common usage of this is variable is to set it to "-1" within
      a recipe for a development version of a piece of software. Using the
      variable in this way causes the stable version of the recipe to build
      by default in the absence of :term:`PREFERRED_VERSION` being used to
      build the development version.

      .. note::

         The bias provided by DEFAULT_PREFERENCE is weak and is overridden by
         :term:`BBFILE_PRIORITY` if that variable is different between two
         layers that contain different versions of the same recipe.

   :term:`DEPENDS`
      Lists a recipe's build-time dependencies (i.e. other recipe files).

      Consider this simple example for two recipes named "a" and "b" that
      produce similarly named packages. In this example, the :term:`DEPENDS`
      statement appears in the "a" recipe::

         DEPENDS = "b"

      Here, the dependency is such that the ``do_configure`` task for recipe "a"
      depends on the ``do_populate_sysroot`` task of recipe "b". This means
      anything that recipe "b" puts into sysroot is available when recipe "a" is
      configuring itself.

      For information on runtime dependencies, see the :term:`RDEPENDS`
      variable.

   :term:`DESCRIPTION`
      A long description for the recipe.

   :term:`DL_DIR`
      The central download directory used by the build process to store
      downloads. By default, :term:`DL_DIR` gets files suitable for mirroring for
      everything except Git repositories. If you want tarballs of Git
      repositories, use the :term:`BB_GENERATE_MIRROR_TARBALLS` variable.

   :term:`EXCLUDE_FROM_WORLD`
      Directs BitBake to exclude a recipe from world builds (i.e.
      ``bitbake world``). During world builds, BitBake locates, parses and
      builds all recipes found in every layer exposed in the
      ``bblayers.conf`` configuration file.

      To exclude a recipe from a world build using this variable, set the
      variable to "1" in the recipe. Set it to "0" to add it back to world build.

      .. note::

         Recipes added to :term:`EXCLUDE_FROM_WORLD` may still be built during a world
         build in order to satisfy dependencies of other recipes. Adding a
         recipe to :term:`EXCLUDE_FROM_WORLD` only ensures that the recipe is not
         explicitly added to the list of build targets in a world build.

   :term:`FAKEROOT`
      Contains the command to use when running a shell script in a fakeroot
      environment. The :term:`FAKEROOT` variable is obsolete and has been
      replaced by the other ``FAKEROOT*`` variables. See these entries in
      the glossary for more information.

   :term:`FAKEROOTBASEENV`
      Lists environment variables to set when executing the command defined
      by :term:`FAKEROOTCMD` that starts the
      bitbake-worker process in the fakeroot environment.

   :term:`FAKEROOTCMD`
      Contains the command that starts the bitbake-worker process in the
      fakeroot environment.

   :term:`FAKEROOTDIRS`
      Lists directories to create before running a task in the fakeroot
      environment.

   :term:`FAKEROOTENV`
      Lists environment variables to set when running a task in the
      fakeroot environment. For additional information on environment
      variables and the fakeroot environment, see the
      :term:`FAKEROOTBASEENV` variable.

   :term:`FAKEROOTNOENV`
      Lists environment variables to set when running a task that is not in
      the fakeroot environment. For additional information on environment
      variables and the fakeroot environment, see the
      :term:`FAKEROOTENV` variable.

   :term:`FETCHCMD`
      Defines the command the BitBake fetcher module executes when running
      fetch operations. You need to use an override suffix when you use the
      variable (e.g. ``FETCHCMD_git`` or ``FETCHCMD_svn``).

   :term:`FILE`
      Points at the current file. BitBake sets this variable during the
      parsing process to identify the file being parsed. BitBake also sets
      this variable when a recipe is being executed to identify the recipe
      file.

   :term:`FILESPATH`
      Specifies directories BitBake uses when searching for patches and
      files. The "local" fetcher module uses these directories when
      handling ``file://`` URLs. The variable behaves like a shell ``PATH``
      environment variable. The value is a colon-separated list of
      directories that are searched left-to-right in order.

   :term:`FILE_LAYERNAME`
      During parsing and task execution, this is set to the name of the
      layer containing the recipe file. Code can use this to identify which
      layer a recipe is from.

   :term:`GITDIR`
      The directory in which a local copy of a Git repository is stored
      when it is cloned.

   :term:`HGDIR`
      The directory in which files checked out of a Mercurial system are
      stored.

   :term:`HOMEPAGE`
      Website where more information about the software the recipe is
      building can be found.

   :term:`INHERIT`
      Causes the named class or classes to be inherited globally. Anonymous
      functions in the class or classes are not executed for the base
      configuration and in each individual recipe. The OpenEmbedded build
      system ignores changes to :term:`INHERIT` in individual recipes.

      For more information on :term:`INHERIT`, see the
      ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:\`\`inherit\`\` configuration directive`"
      section.

   :term:`LAYERDEPENDS`
      Lists the layers, separated by spaces, upon which this recipe
      depends. Optionally, you can specify a specific layer version for a
      dependency by adding it to the end of the layer name with a colon,
      (e.g. "anotherlayer:3" to be compared against
      :term:`LAYERVERSION`\ ``_anotherlayer`` in
      this case). BitBake produces an error if any dependency is missing or
      the version numbers do not match exactly (if specified).

      You use this variable in the ``conf/layer.conf`` file. You must also
      use the specific layer name as a suffix to the variable (e.g.
      ``LAYERDEPENDS_mylayer``).

   :term:`LAYERDIR`
      When used inside the ``layer.conf`` configuration file, this variable
      provides the path of the current layer. This variable is not
      available outside of ``layer.conf`` and references are expanded
      immediately when parsing of the file completes.

   :term:`LAYERDIR_RE`
      When used inside the ``layer.conf`` configuration file, this variable
      provides the path of the current layer, escaped for use in a regular
      expression (:term:`BBFILE_PATTERN`). This
      variable is not available outside of ``layer.conf`` and references
      are expanded immediately when parsing of the file completes.

   :term:`LAYERSERIES_COMPAT`
      Lists the versions of the OpenEmbedded-Core (OE-Core) for which
      a layer is compatible. Using the :term:`LAYERSERIES_COMPAT` variable
      allows the layer maintainer to indicate which combinations of the
      layer and OE-Core can be expected to work. The variable gives the
      system a way to detect when a layer has not been tested with new
      releases of OE-Core (e.g. the layer is not maintained).

      To specify the OE-Core versions for which a layer is compatible, use
      this variable in your layer's ``conf/layer.conf`` configuration file.
      For the list, use the Yocto Project release name (e.g. "kirkstone",
      "mickledore"). To specify multiple OE-Core versions for the layer, use
      a space-separated list::

         LAYERSERIES_COMPAT_layer_root_name = "kirkstone mickledore"

      .. note::

         Setting :term:`LAYERSERIES_COMPAT` is required by the Yocto Project
         Compatible version 2 standard.
         The OpenEmbedded build system produces a warning if the variable
         is not set for any given layer.

   :term:`LAYERVERSION`
      Optionally specifies the version of a layer as a single number. You
      can use this variable within
      :term:`LAYERDEPENDS` for another layer in
      order to depend on a specific version of the layer.

      You use this variable in the ``conf/layer.conf`` file. You must also
      use the specific layer name as a suffix to the variable (e.g.
      ``LAYERDEPENDS_mylayer``).

   :term:`LICENSE`
      The list of source licenses for the recipe.

   :term:`MIRRORS`
      Specifies additional paths from which BitBake gets source code. When
      the build system searches for source code, it first tries the local
      download directory. If that location fails, the build system tries
      locations defined by :term:`PREMIRRORS`, the
      upstream source, and then locations specified by :term:`MIRRORS` in that
      order.

   :term:`OVERRIDES`
      A colon-separated list that BitBake uses to control what variables are
      overridden after BitBake parses recipes and configuration files.

      Following is a simple example that uses an overrides list based on
      machine architectures: OVERRIDES = "arm:x86:mips:powerpc" You can
      find information on how to use :term:`OVERRIDES` in the
      ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:conditional syntax
      (overrides)`" section.

   :term:`P4DIR`
      The directory in which a local copy of a Perforce depot is stored
      when it is fetched.

   :term:`PACKAGES`
      The list of packages the recipe creates.

   :term:`PACKAGES_DYNAMIC`
      A promise that your recipe satisfies runtime dependencies for
      optional modules that are found in other recipes.
      :term:`PACKAGES_DYNAMIC` does not actually satisfy the dependencies, it
      only states that they should be satisfied. For example, if a hard,
      runtime dependency (:term:`RDEPENDS`) of another
      package is satisfied during the build through the
      :term:`PACKAGES_DYNAMIC` variable, but a package with the module name is
      never actually produced, then the other package will be broken.

   :term:`PE`
      The epoch of the recipe. By default, this variable is unset. The
      variable is used to make upgrades possible when the versioning scheme
      changes in some backwards incompatible way.

   :term:`PERSISTENT_DIR`
      Specifies the directory BitBake uses to store data that should be
      preserved between builds. In particular, the data stored is the data
      that uses BitBake's persistent data API and the data used by the PR
      Server and PR Service.

   :term:`PF`
      Specifies the recipe or package name and includes all version and
      revision numbers (i.e. ``eglibc-2.13-r20+svnr15508/`` and
      ``bash-4.2-r1/``).

   :term:`PN`
      The recipe name.

   :term:`PR`
      The revision of the recipe.

   :term:`PREFERRED_PROVIDER`
      Determines which recipe should be given preference when multiple
      recipes provide the same item. You should always suffix the variable
      with the name of the provided item, and you should set it to the
      :term:`PN` of the recipe to which you want to give
      precedence. Some examples::

         PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto"
         PREFERRED_PROVIDER_virtual/xserver = "xserver-xf86"
         PREFERRED_PROVIDER_virtual/libgl ?= "mesa"

   :term:`PREFERRED_PROVIDERS`
      Determines which recipe should be given preference for cases where
      multiple recipes provide the same item. Functionally,
      :term:`PREFERRED_PROVIDERS` is identical to
      :term:`PREFERRED_PROVIDER`. However, the :term:`PREFERRED_PROVIDERS` variable
      lets you define preferences for multiple situations using the following
      form::

         PREFERRED_PROVIDERS = "xxx:yyy aaa:bbb ..."

      This form is a convenient replacement for the following::

         PREFERRED_PROVIDER_xxx = "yyy"
         PREFERRED_PROVIDER_aaa = "bbb"

   :term:`PREFERRED_VERSION`
      If there are multiple versions of a recipe available, this variable
      determines which version should be given preference. You must always
      suffix the variable with the :term:`PN` you want to
      select, and you should set :term:`PV` accordingly for
      precedence.

      The :term:`PREFERRED_VERSION` variable supports limited wildcard use
      through the "``%``" character. You can use the character to match any
      number of characters, which can be useful when specifying versions
      that contain long revision numbers that potentially change. Here are
      two examples::

         PREFERRED_VERSION_python = "2.7.3"
         PREFERRED_VERSION_linux-yocto = "4.12%"

      .. important::

         The use of the " % " character is limited in that it only works at the
         end of the string. You cannot use the wildcard character in any other
         location of the string.

      If a recipe with the specified version is not available, a warning
      message will be shown. See :term:`REQUIRED_VERSION` if you want this
      to be an error instead.

   :term:`PREMIRRORS`
      Specifies additional paths from which BitBake gets source code. When
      the build system searches for source code, it first tries the local
      download directory. If that location fails, the build system tries
      locations defined by :term:`PREMIRRORS`, the upstream source, and then
      locations specified by :term:`MIRRORS` in that order.

      Typically, you would add a specific server for the build system to
      attempt before any others by adding something like the following to
      your configuration::

         PREMIRRORS:prepend = "\
         git://.*/.* http://downloads.yoctoproject.org/mirror/sources/ \
         ftp://.*/.* http://downloads.yoctoproject.org/mirror/sources/ \
         http://.*/.* http://downloads.yoctoproject.org/mirror/sources/ \
         https://.*/.* http://downloads.yoctoproject.org/mirror/sources/"

      These changes cause the build system to intercept Git, FTP, HTTP, and
      HTTPS requests and direct them to the ``http://`` sources mirror. You can
      use ``file://`` URLs to point to local directories or network shares as
      well.

   :term:`PROVIDES`
      A list of aliases by which a particular recipe can be known. By
      default, a recipe's own :term:`PN` is implicitly already in its
      :term:`PROVIDES` list. If a recipe uses :term:`PROVIDES`, the additional
      aliases are synonyms for the recipe and can be useful satisfying
      dependencies of other recipes during the build as specified by
      :term:`DEPENDS`.

      Consider the following example :term:`PROVIDES` statement from a recipe
      file ``libav_0.8.11.bb``::

         PROVIDES += "libpostproc"

      The :term:`PROVIDES` statement results in the "libav" recipe also being known
      as "libpostproc".

      In addition to providing recipes under alternate names, the
      :term:`PROVIDES` mechanism is also used to implement virtual targets. A
      virtual target is a name that corresponds to some particular
      functionality (e.g. a Linux kernel). Recipes that provide the
      functionality in question list the virtual target in :term:`PROVIDES`.
      Recipes that depend on the functionality in question can include the
      virtual target in :term:`DEPENDS` to leave the
      choice of provider open.

      Conventionally, virtual targets have names on the form
      "virtual/function" (e.g. "virtual/kernel"). The slash is simply part
      of the name and has no syntactical significance.

   :term:`PRSERV_HOST`
      The network based :term:`PR` service host and port.

      Following is an example of how the :term:`PRSERV_HOST` variable is set::

         PRSERV_HOST = "localhost:0"

      You must set the variable if you want to automatically start a local PR
      service. You can set :term:`PRSERV_HOST` to other values to use a remote PR
      service.

   :term:`PV`
      The version of the recipe.

   :term:`RDEPENDS`
      Lists a package's runtime dependencies (i.e. other packages) that
      must be installed in order for the built package to run correctly. If
      a package in this list cannot be found during the build, you will get
      a build error.

      Because the :term:`RDEPENDS` variable applies to packages being built,
      you should always use the variable in a form with an attached package
      name. For example, suppose you are building a development package
      that depends on the ``perl`` package. In this case, you would use the
      following :term:`RDEPENDS` statement::

         RDEPENDS:${PN}-dev += "perl"

      In the example, the development package depends on the ``perl`` package.
      Thus, the :term:`RDEPENDS` variable has the ``${PN}-dev`` package name as part
      of the variable.

      BitBake supports specifying versioned dependencies. Although the
      syntax varies depending on the packaging format, BitBake hides these
      differences from you. Here is the general syntax to specify versions
      with the :term:`RDEPENDS` variable::

         RDEPENDS:${PN} = "package (operator version)"

      For ``operator``, you can specify the following::

         =
         <
         >
         <=
         >=

      For example, the following sets up a dependency on version 1.2 or
      greater of the package ``foo``::

         RDEPENDS:${PN} = "foo (>= 1.2)"

      For information on build-time dependencies, see the :term:`DEPENDS`
      variable.

   :term:`REPODIR`
      The directory in which a local copy of a ``google-repo`` directory is
      stored when it is synced.

   :term:`REQUIRED_VERSION`
      If there are multiple versions of a recipe available, this variable
      determines which version should be given preference. :term:`REQUIRED_VERSION`
      works in exactly the same manner as :term:`PREFERRED_VERSION`, except
      that if the specified version is not available then an error message
      is shown and the build fails immediately.

      If both :term:`REQUIRED_VERSION` and :term:`PREFERRED_VERSION` are set for
      the same recipe, the :term:`REQUIRED_VERSION` value applies.

   :term:`RPROVIDES`
      A list of package name aliases that a package also provides. These
      aliases are useful for satisfying runtime dependencies of other
      packages both during the build and on the target (as specified by
      :term:`RDEPENDS`).

      As with all package-controlling variables, you must always use the
      variable in conjunction with a package name override. Here is an
      example::

         RPROVIDES:${PN} = "widget-abi-2"

   :term:`RRECOMMENDS`
      A list of packages that extends the usability of a package being
      built. The package being built does not depend on this list of
      packages in order to successfully build, but needs them for the
      extended usability. To specify runtime dependencies for packages, see
      the :term:`RDEPENDS` variable.

      BitBake supports specifying versioned recommends. Although the syntax
      varies depending on the packaging format, BitBake hides these
      differences from you. Here is the general syntax to specify versions
      with the :term:`RRECOMMENDS` variable::

         RRECOMMENDS:${PN} = "package (operator version)"

      For ``operator``, you can specify the following::

         =
         <
         >
         <=
         >=

      For example, the following sets up a recommend on version
      1.2 or greater of the package ``foo``::

         RRECOMMENDS:${PN} = "foo (>= 1.2)"

   :term:`SECTION`
      The section in which packages should be categorized.

   :term:`SRC_URI`
      The list of source files --- local or remote. This variable tells
      BitBake which bits to pull for the build and how to pull them. For
      example, if the recipe or append file needs to fetch a single tarball
      from the Internet, the recipe or append file uses a :term:`SRC_URI`
      entry that specifies that tarball. On the other hand, if the recipe or
      append file needs to fetch a tarball, apply two patches, and include
      a custom file, the recipe or append file needs an :term:`SRC_URI`
      variable that specifies all those sources.

      The following list explains the available URI protocols. URI
      protocols are highly dependent on particular BitBake Fetcher
      submodules. Depending on the fetcher BitBake uses, various URL
      parameters are employed. For specifics on the supported Fetchers, see
      the :ref:`bitbake-user-manual/bitbake-user-manual-fetching:fetchers`
      section.

      -  ``az://``: Fetches files from an Azure Storage account using HTTPS.

      -  ``bzr://``: Fetches files from a Bazaar revision control
         repository.

      -  ``ccrc://``: Fetches files from a ClearCase repository.

      -  ``cvs://``: Fetches files from a CVS revision control
         repository.

      -  ``file://``: Fetches files, which are usually files shipped
         with the Metadata, from the local machine.
         The path is relative to the :term:`FILESPATH`
         variable. Thus, the build system searches, in order, from the
         following directories, which are assumed to be a subdirectories of
         the directory in which the recipe file (``.bb``) or append file
         (``.bbappend``) resides:

         -  ``${BPN}``: the base recipe name without any special suffix
            or version numbers.

         -  ``${BP}`` - ``${BPN}-${PV}``: the base recipe name and
            version but without any special package name suffix.

         -  ``files``: files within a directory, which is named ``files``
            and is also alongside the recipe or append file.

      -  ``ftp://``: Fetches files from the Internet using FTP.

      -  ``git://``: Fetches files from a Git revision control
         repository.

      -  ``gitsm://``: Fetches submodules from a Git revision control
         repository.

      -  ``hg://``: Fetches files from a Mercurial (``hg``) revision
         control repository.

      -  ``http://``: Fetches files from the Internet using HTTP.

      -  ``https://``: Fetches files from the Internet using HTTPS.

      -  ``npm://``: Fetches JavaScript modules from a registry.

      -  ``osc://``: Fetches files from an OSC (OpenSUSE Build service)
         revision control repository.

      -  ``p4://``: Fetches files from a Perforce (``p4``) revision
         control repository.

      -  ``repo://``: Fetches files from a repo (Git) repository.

      -  ``ssh://``: Fetches files from a secure shell.

      -  ``svn://``: Fetches files from a Subversion (``svn``) revision
         control repository.

      Here are some additional options worth mentioning:

      -  ``downloadfilename``: Specifies the filename used when storing
         the downloaded file.

      -  ``name``: Specifies a name to be used for association with
         :term:`SRC_URI` checksums or :term:`SRCREV` when you have more than one
         file or git repository specified in :term:`SRC_URI`. For example::

            SRC_URI = "git://example.com/foo.git;branch=main;name=first \
                       git://example.com/bar.git;branch=main;name=second \
                       http://example.com/file.tar.gz;name=third"

            SRCREV_first = "f1d2d2f924e986ac86fdf7b36c94bcdf32beec15"
            SRCREV_second = "e242ed3bffccdf271b7fbaf34ed72d089537b42f"
            SRC_URI[third.sha256sum] = "13550350a8681c84c861aac2e5b440161c2b33a3e4f302ac680ca5b686de48de"

      -  ``subdir``: Places the file (or extracts its contents) into the
         specified subdirectory. This option is useful for unusual tarballs
         or other archives that do not have their files already in a
         subdirectory within the archive.

      -  ``subpath``: Limits the checkout to a specific subpath of the
         tree when using the Git fetcher is used.

      -  ``unpack``: Controls whether or not to unpack the file if it is
         an archive. The default action is to unpack the file.

   :term:`SRCDATE`
      The date of the source code used to build the package. This variable
      applies only if the source was fetched from a Source Code Manager
      (SCM).

   :term:`SRCREV`
      The revision of the source code used to build the package. This
      variable applies only when using Subversion, Git, Mercurial and
      Bazaar. If you want to build a fixed revision and you want to avoid
      performing a query on the remote repository every time BitBake parses
      your recipe, you should specify a :term:`SRCREV` that is a full revision
      identifier and not just a tag.

   :term:`SRCREV_FORMAT`
      Helps construct valid :term:`SRCREV` values when
      multiple source controlled URLs are used in
      :term:`SRC_URI`.

      The system needs help constructing these values under these
      circumstances. Each component in the :term:`SRC_URI` is assigned a name
      and these are referenced in the :term:`SRCREV_FORMAT` variable. Consider
      an example with URLs named "machine" and "meta". In this case,
      :term:`SRCREV_FORMAT` could look like "machine_meta" and those names
      would have the SCM versions substituted into each position. Only one
      ``AUTOINC`` placeholder is added and if needed. And, this placeholder
      is placed at the start of the returned string.

   :term:`STAMP`
      Specifies the base path used to create recipe stamp files. The path
      to an actual stamp file is constructed by evaluating this string and
      then appending additional information.

   :term:`STAMPCLEAN`
      Specifies the base path used to create recipe stamp files. Unlike the
      :term:`STAMP` variable, :term:`STAMPCLEAN` can contain
      wildcards to match the range of files a clean operation should
      remove. BitBake uses a clean operation to remove any other stamps it
      should be removing when creating a new stamp.

   :term:`SUMMARY`
      A short summary for the recipe, which is 72 characters or less.

   :term:`SVNDIR`
      The directory in which files checked out of a Subversion system are
      stored.

   :term:`T`
      Points to a directory were BitBake places temporary files, which
      consist mostly of task logs and scripts, when building a particular
      recipe.

   :term:`TOPDIR`
      Points to the build directory. BitBake automatically sets this
      variable.
