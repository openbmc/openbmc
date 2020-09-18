Moving to the Yocto Project 3.1 Release
=======================================

This section provides migration information for moving to the Yocto
Project 3.1 Release from the prior release.

.. _migration-3.1-minimum-system-requirements:

Minimum system requirements
---------------------------

The following versions / requirements of build host components have been
updated:

-  gcc 5.0

-  python 3.5

-  tar 1.28

-  ``rpcgen`` is now required on the host (part of the ``libc-dev-bin``
   package on Ubuntu, Debian and related distributions, and the
   ``glibc`` package on RPM-based distributions).

Additionally, the ``makeinfo`` and ``pod2man`` tools are *no longer*
required on the host.

.. _migration-3.1-mpc8315e-rdb-removed:

mpc8315e-rdb machine removed
----------------------------

The MPC8315E-RDB machine is old/obsolete and unobtainable, thus given
the maintenance burden the ``mpc8315e-rdb`` machine configuration that
supported it has been removed in this release. The removal does leave a
gap in official PowerPC reference hardware support; this may change in
future if a suitable machine with accompanying support resources is
found.

.. _migration-3.1-python-2-removed:

Python 2 removed
----------------

Due to the expiration of upstream support in January 2020, support for
Python 2 has now been removed; it is recommended that you use Python 3
instead. If absolutely needed there is a meta-python2 community layer
containing Python 2, related classes and various Python 2-based modules,
however it should not be considered as supported.

.. _migration-3.1-reproducible-builds:

Reproducible builds now enabled by default
------------------------------------------

In order to avoid unnecessary differences in output files (aiding binary
reproducibility), the Poky distribution configuration
(``DISTRO = "poky"``) now inherits the ``reproducible_build`` class by
default.

.. _migration-3.1-ptest-feature-impact:

Impact of ptest feature is now more significant
-----------------------------------------------

The Poky distribution configuration (``DISTRO = "poky"``) enables ptests
by default to enable runtime testing of various components. In this
release, a dependency needed to be added that has resulted in a
significant increase in the number of components that will be built just
when building a simple image such as core-image-minimal. If you do not
need runtime tests enabled for core components, then it is recommended
that you remove "ptest" from
:term:`DISTRO_FEATURES` to save a significant
amount of build time e.g. by adding the following in your configuration:
::

   DISTRO_FEATURES_remove = "ptest"

.. _migration-3.1-removed-recipes:

Removed recipes
---------------

The following recipes have been removed:

-  ``chkconfig``: obsolete

-  ``console-tools``: obsolete

-  ``enchant``: replaced by ``enchant2``

-  ``foomatic-filters``: obsolete

-  ``libidn``: no longer needed, moved to meta-oe

-  ``libmodulemd``: replaced by ``libmodulemd-v1``

-  ``linux-yocto``: drop 4.19, 5.2 version recipes (5.4 now provided)

-  ``nspr``: no longer needed, moved to meta-oe

-  ``nss``: no longer needed, moved to meta-oe

-  ``python``: Python 2 removed (Python 3 preferred)

-  ``python-setuptools``: Python 2 version removed (python3-setuptools
   preferred)

-  ``sysprof``: no longer needed, moved to meta-oe

-  ``texi2html``: obsolete

-  ``u-boot-fw-utils``: functionally replaced by ``libubootenv``

.. _migration-3.1-features-check:

features_check class replaces distro_features_check
---------------------------------------------------

The ``distro_features_check`` class has had its functionality expanded,
now supporting ``ANY_OF_MACHINE_FEATURES``,
``REQUIRED_MACHINE_FEATURES``, ``CONFLICT_MACHINE_FEATURES``,
``ANY_OF_COMBINED_FEATURES``, ``REQUIRED_COMBINED_FEATURES``,
``CONFLICT_COMBINED_FEATURES``. As a result the class has now been
renamed to ``features_check``; the ``distro_features_check`` class still
exists but generates a warning and redirects to the new class. In
preparation for a future removal of the old class it is recommended that
you update recipes currently inheriting ``distro_features_check`` to
inherit ``features_check`` instead.

.. _migration-3.1-removed-classes:

Removed classes
---------------

The following classes have been removed:

-  ``distutils-base``: moved to meta-python2

-  ``distutils``: moved to meta-python2

-  ``libc-common``: merged into the glibc recipe as nothing else used
   it.

-  ``python-dir``: moved to meta-python2

-  ``pythonnative``: moved to meta-python2

-  ``setuptools``: moved to meta-python2

-  ``tinderclient``: dropped as it was obsolete.

.. _migration-3.1-src-uri-checksums:

SRC_URI checksum behaviour
--------------------------

Previously, recipes by tradition included both SHA256 and MD5 checksums
for remotely fetched files in :term:`SRC_URI`, even
though only one is actually mandated. However, the MD5 checksum does not
add much given its inherent weakness; thus when a checksum fails only
the SHA256 sum will now be printed. The md5sum will still be verified if
it is specified.

.. _migration-3.1-npm:

npm fetcher changes
-------------------

The npm fetcher has been completely reworked in this release. The npm
fetcher now only fetches the package source itself and no longer the
dependencies; there is now also an npmsw fetcher which explicitly
fetches the shrinkwrap file and the dependencies. This removes the
slightly awkward ``NPM_LOCKDOWN`` and ``NPM_SHRINKWRAP`` variables which
pointed to local files; the lockdown file is no longer needed at all.
Additionally, the package name in ``npm://`` entries in
:term:`SRC_URI` is now specified using a ``package``
parameter instead of the earlier ``name`` which overlapped with the
generic ``name`` parameter. All recipes using the npm fetcher will need
to be changed as a result.

An example of the new scheme: ::

   SRC_URI = "npm://registry.npmjs.org;package=array-flatten;version=1.1.1 \\
              npmsw://${THISDIR}/npm-shrinkwrap.json"

Another example where the sources are fetched from git rather than an npm repository: ::

   SRC_URI = "git://github.com/foo/bar.git;protocol=https \
              npmsw://${THISDIR}/npm-shrinkwrap.json"

devtool and recipetool have also been updated to match with the npm
fetcher changes. Other than producing working and more complete recipes
for npm sources, there is also a minor change to the command line for
devtool: the ``--fetch-dev`` option has been renamed to ``--npm-dev`` as
it is npm-specific.

.. _migration-3.1-packaging-changes:

Packaging changes
-----------------

-  ``intltool`` has been removed from ``packagegroup-core-sdk`` as it is
   rarely needed to build modern software - gettext can do most of the
   things it used to be needed for. ``intltool`` has also been removed
   from ``packagegroup-core-self-hosted`` as it is not needed to for
   standard builds.

-  git: ``git-am``, ``git-difftool``, ``git-submodule``, and
   ``git-request-pull`` are no longer perl-based, so are now installed
   with the main ``git`` package instead of within ``git-perltools``.

-  The ``ldconfig`` binary built as part of glibc has now been moved to
   its own ``ldconfig`` package (note no ``glibc-`` prefix). This
   package is in the :term:`RRECOMMENDS` of the main
   ``glibc`` package if ``ldconfig`` is present in
   :term:`DISTRO_FEATURES`.

-  ``libevent`` now splits each shared library into its own package (as
   Debian does). Since these are shared libraries and will be pulled in
   through the normal shared library dependency handling, there should
   be no impact to existing configurations other than less unnecessary
   libraries being installed in some cases.

-  linux-firmware now has a new package for ``bcm4366c`` and includes
   available NVRAM config files into the ``bcm43340``, ``bcm43362``,
   ``bcm43430`` and ``bcm4356-pcie`` packages.

-  ``harfbuzz`` now splits the new ``libharfbuzz-subset.so`` library
   into its own package to reduce the main package size in cases where
   ``libharfbuzz-subset.so`` is not needed.

.. _migration-3.1-package-qa-warnings:

Additional warnings
-------------------

Warnings will now be shown at ``do_package_qa`` time in the following
circumstances:

-  A recipe installs ``.desktop`` files containing ``MimeType`` keys but
   does not inherit the new ``mime-xdg`` class

-  A recipe installs ``.xml`` files into ``${datadir}/mime/packages``
   but does not inherit the ``mime`` class

.. _migration-3.1-x86-live-wic:

``wic`` image type now used instead of ``live`` by default for x86
------------------------------------------------------------------

``conf/machine/include/x86-base.inc`` (inherited by most x86 machine
configurations) now specifies ``wic`` instead of ``live`` by default in
:term:`IMAGE_FSTYPES`. The ``live`` image type will
likely be removed in a future release so it is recommended that you use
``wic`` instead.

.. _migration-3.1-misc:

Miscellaneous changes
---------------------

-  The undocumented ``SRC_DISTRIBUTE_LICENSES`` variable has now been
   removed in favour of a new ``AVAILABLE_LICENSES`` variable which is
   dynamically set based upon license files found in
   ``${COMMON_LICENSE_DIR}`` and ``${LICENSE_PATH}``.

-  The tune definition for big-endian microblaze machines is now
   ``microblaze`` instead of ``microblazeeb``.

-  ``newlib`` no longer has built-in syscalls. ``libgloss`` should then
   provide the syscalls, ``crt0.o`` and other functions that are no
   longer part of ``newlib`` itself. If you are using
   ``TCLIBC = "newlib"`` this now means that you must link applications
   with both ``newlib`` and ``libgloss``, whereas before ``newlib``
   would run in many configurations by itself.
