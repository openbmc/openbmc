.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Speeding Up a Build
*******************

Build time can be an issue. By default, the build system uses simple
controls to try and maximize build efficiency. In general, the default
settings for all the following variables result in the most efficient
build times when dealing with single socket systems (i.e. a single CPU).
If you have multiple CPUs, you might try increasing the default values
to gain more speed. See the descriptions in the glossary for each
variable for more information:

-  :term:`BB_NUMBER_THREADS`:
   The maximum number of threads BitBake simultaneously executes.

-  :term:`BB_NUMBER_PARSE_THREADS`:
   The number of threads BitBake uses during parsing.

-  :term:`PARALLEL_MAKE`: Extra
   options passed to the ``make`` command during the
   :ref:`ref-tasks-compile` task in
   order to specify parallel compilation on the local build host.

-  :term:`PARALLEL_MAKEINST`:
   Extra options passed to the ``make`` command during the
   :ref:`ref-tasks-install` task in
   order to specify parallel installation on the local build host.

As mentioned, these variables all scale to the number of processor cores
available on the build system. For single socket systems, this
auto-scaling ensures that the build system fundamentally takes advantage
of potential parallel operations during the build based on the build
machine's capabilities.

Additional factors that can affect build speed are:

-  File system type: The file system type that the build is being
   performed on can also influence performance. Using ``ext4`` is
   recommended as compared to ``ext2`` and ``ext3`` due to ``ext4``
   improved features such as extents.

-  Disabling the updating of access time using ``noatime``: The
   ``noatime`` mount option prevents the build system from updating file
   and directory access times.

-  Setting a longer commit: Using the "commit=" mount option increases
   the interval in seconds between disk cache writes. Changing this
   interval from the five second default to something longer increases
   the risk of data loss but decreases the need to write to the disk,
   thus increasing the build performance.

-  Choosing the packaging backend: Of the available packaging backends,
   IPK is the fastest. Additionally, selecting a singular packaging
   backend also helps.

-  Using ``tmpfs`` for :term:`TMPDIR`
   as a temporary file system: While this can help speed up the build,
   the benefits are limited due to the compiler using ``-pipe``. The
   build system goes to some lengths to avoid ``sync()`` calls into the
   file system on the principle that if there was a significant failure,
   the :term:`Build Directory` contents could easily be rebuilt.

-  Inheriting the :ref:`ref-classes-rm-work` class:
   Inheriting this class has shown to speed up builds due to
   significantly lower amounts of data stored in the data cache as well
   as on disk. Inheriting this class also makes cleanup of
   :term:`TMPDIR` faster, at the
   expense of being easily able to dive into the source code. File
   system maintainers have recommended that the fastest way to clean up
   large numbers of files is to reformat partitions rather than delete
   files due to the linear nature of partitions. This, of course,
   assumes you structure the disk partitions and file systems in a way
   that this is practical.

Aside from the previous list, you should keep some trade offs in mind
that can help you speed up the build:

-  Remove items from
   :term:`DISTRO_FEATURES`
   that you might not need.

-  Exclude debug symbols and other debug information: If you do not need
   these symbols and other debug information, disabling the ``*-dbg``
   package generation can speed up the build. You can disable this
   generation by setting the
   :term:`INHIBIT_PACKAGE_DEBUG_SPLIT`
   variable to "1".

-  Disable static library generation for recipes derived from
   ``autoconf`` or ``libtool``: Here is an example showing how to
   disable static libraries and still provide an override to handle
   exceptions::

      STATICLIBCONF = "--disable-static"
      STATICLIBCONF:sqlite3-native = ""
      EXTRA_OECONF += "${STATICLIBCONF}"

   .. note::

      -  Some recipes need static libraries in order to work correctly
         (e.g. ``pseudo-native`` needs ``sqlite3-native``). Overrides,
         as in the previous example, account for these kinds of
         exceptions.

      -  Some packages have packaging code that assumes the presence of
         the static libraries. If so, you might need to exclude them as
         well.

