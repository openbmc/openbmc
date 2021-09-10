.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*******
Classes
*******

Class files are used to abstract common functionality and share it
amongst multiple recipe (``.bb``) files. To use a class file, you simply
make sure the recipe inherits the class. In most cases, when a recipe
inherits a class it is enough to enable its features. There are cases,
however, where in the recipe you might need to set variables or override
some default behavior.

Any :term:`Metadata` usually found in a recipe can also be
placed in a class file. Class files are identified by the extension
``.bbclass`` and are usually placed in a ``classes/`` directory beneath
the ``meta*/`` directory found in the :term:`Source Directory`.
Class files can also be pointed to by
:term:`BUILDDIR` (e.g. ``build/``) in the same way as
``.conf`` files in the ``conf`` directory. Class files are searched for
in :term:`BBPATH` using the same method by which ``.conf``
files are searched.

This chapter discusses only the most useful and important classes. Other
classes do exist within the ``meta/classes`` directory in the Source
Directory. You can reference the ``.bbclass`` files directly for more
information.

.. _ref-classes-allarch:

``allarch.bbclass``
===================

The ``allarch`` class is inherited by recipes that do not produce
architecture-specific output. The class disables functionality that is
normally needed for recipes that produce executable binaries (such as
building the cross-compiler and a C library as pre-requisites, and
splitting out of debug symbols during packaging).

.. note::

   Unlike some distro recipes (e.g. Debian), OpenEmbedded recipes that
   produce packages that depend on tunings through use of the
   :term:`RDEPENDS` and
   :term:`TUNE_PKGARCH` variables, should never be
   configured for all architectures using ``allarch``. This is the case
   even if the recipes do not produce architecture-specific output.

   Configuring such recipes for all architectures causes the
   ``do_package_write_*`` tasks to
   have different signatures for the machines with different tunings.
   Additionally, unnecessary rebuilds occur every time an image for a
   different :term:`MACHINE` is built even when the recipe never changes.

By default, all recipes inherit the :ref:`base <ref-classes-base>` and
:ref:`package <ref-classes-package>` classes, which enable
functionality needed for recipes that produce executable output. If your
recipe, for example, only produces packages that contain configuration
files, media files, or scripts (e.g. Python and Perl), then it should
inherit the ``allarch`` class.

.. _ref-classes-archiver:

``archiver.bbclass``
====================

The ``archiver`` class supports releasing source code and other
materials with the binaries.

For more details on the source archiver, see the
":ref:`dev-manual/common-tasks:maintaining open source license compliance during your product's lifecycle`"
section in the Yocto Project Development Tasks Manual. You can also see
the :term:`ARCHIVER_MODE` variable for information
about the variable flags (varflags) that help control archive creation.

.. _ref-classes-autotools:

``autotools*.bbclass``
======================

The ``autotools*`` classes support Autotooled packages.

The ``autoconf``, ``automake``, and ``libtool`` packages bring
standardization. This class defines a set of tasks (e.g. ``configure``,
``compile`` and so forth) that work for all Autotooled packages. It
should usually be enough to define a few standard variables and then
simply ``inherit autotools``. These classes can also work with software
that emulates Autotools. For more information, see the
":ref:`dev-manual/common-tasks:autotooled package`" section
in the Yocto Project Development Tasks Manual.

By default, the ``autotools*`` classes use out-of-tree builds (i.e.
``autotools.bbclass`` building with ``B != S``).

If the software being built by a recipe does not support using
out-of-tree builds, you should have the recipe inherit the
``autotools-brokensep`` class. The ``autotools-brokensep`` class behaves
the same as the ``autotools`` class but builds with :term:`B`
== :term:`S`. This method is useful when out-of-tree build
support is either not present or is broken.

.. note::

   It is recommended that out-of-tree support be fixed and used if at
   all possible.

It's useful to have some idea of how the tasks defined by the
``autotools*`` classes work and what they do behind the scenes.

-  :ref:`ref-tasks-configure` - Regenerates the
   configure script (using ``autoreconf``) and then launches it with a
   standard set of arguments used during cross-compilation. You can pass
   additional parameters to ``configure`` through the :term:`EXTRA_OECONF`
   or :term:`PACKAGECONFIG_CONFARGS`
   variables.

-  :ref:`ref-tasks-compile` - Runs ``make`` with
   arguments that specify the compiler and linker. You can pass
   additional arguments through the :term:`EXTRA_OEMAKE` variable.

-  :ref:`ref-tasks-install` - Runs ``make install`` and
   passes in ``${``\ :term:`D`\ ``}`` as ``DESTDIR``.

.. _ref-classes-base:

``base.bbclass``
================

The ``base`` class is special in that every ``.bb`` file implicitly
inherits the class. This class contains definitions for standard basic
tasks such as fetching, unpacking, configuring (empty by default),
compiling (runs any ``Makefile`` present), installing (empty by default)
and packaging (empty by default). These classes are often overridden or
extended by other classes such as the
:ref:`autotools <ref-classes-autotools>` class or the
:ref:`package <ref-classes-package>` class.

The class also contains some commonly used functions such as
``oe_runmake``, which runs ``make`` with the arguments specified in
:term:`EXTRA_OEMAKE` variable as well as the
arguments passed directly to ``oe_runmake``.

.. _ref-classes-bash-completion:

``bash-completion.bbclass``
===========================

Sets up packaging and dependencies appropriate for recipes that build
software that includes bash-completion data.

.. _ref-classes-bin-package:

``bin_package.bbclass``
=======================

The ``bin_package`` class is a helper class for recipes that extract the
contents of a binary package (e.g. an RPM) and install those contents
rather than building the binary from source. The binary package is
extracted and new packages in the configured output package format are
created. Extraction and installation of proprietary binaries is a good
example use for this class.

.. note::

   For RPMs and other packages that do not contain a subdirectory, you
   should specify an appropriate fetcher parameter to point to the
   subdirectory. For example, if BitBake is using the Git fetcher (``git://``),
   the "subpath" parameter limits the checkout to a specific subpath
   of the tree. Here is an example where ``${BP}`` is used so that the files
   are extracted into the subdirectory expected by the default value of
   :term:`S`::

           SRC_URI = "git://example.com/downloads/somepackage.rpm;subpath=${BP}"


   See the ":ref:`bitbake-user-manual/bitbake-user-manual-fetching:fetchers`" section in the BitBake User Manual for
   more information on supported BitBake Fetchers.

.. _ref-classes-binconfig:

``binconfig.bbclass``
=====================

The ``binconfig`` class helps to correct paths in shell scripts.

Before ``pkg-config`` had become widespread, libraries shipped shell
scripts to give information about the libraries and include paths needed
to build software (usually named ``LIBNAME-config``). This class assists
any recipe using such scripts.

During staging, the OpenEmbedded build system installs such scripts into
the ``sysroots/`` directory. Inheriting this class results in all paths
in these scripts being changed to point into the ``sysroots/`` directory
so that all builds that use the script use the correct directories for
the cross compiling layout. See the
:term:`BINCONFIG_GLOB` variable for more
information.

.. _ref-classes-binconfig-disabled:

``binconfig-disabled.bbclass``
==============================

An alternative version of the :ref:`binconfig <ref-classes-binconfig>`
class, which disables binary configuration scripts by making them return
an error in favor of using ``pkg-config`` to query the information. The
scripts to be disabled should be specified using the
:term:`BINCONFIG` variable within the recipe inheriting
the class.

.. _ref-classes-blacklist:

``blacklist.bbclass``
=====================

The ``blacklist`` class prevents the OpenEmbedded build system from
building specific recipes (blacklists them). To use this class, inherit
the class globally and set :term:`PNBLACKLIST` for
each recipe you wish to blacklist. Specify the :term:`PN`
value as a variable flag (varflag) and provide a reason, which is
reported, if the package is requested to be built as the value. For
example, if you want to blacklist a recipe called "exoticware", you add
the following to your ``local.conf`` or distribution configuration::

   INHERIT += "blacklist"
   PNBLACKLIST[exoticware] = "Not supported by our organization."

.. _ref-classes-buildhistory:

``buildhistory.bbclass``
========================

The ``buildhistory`` class records a history of build output metadata,
which can be used to detect possible regressions as well as used for
analysis of the build output. For more information on using Build
History, see the
":ref:`dev-manual/common-tasks:maintaining build output quality`"
section in the Yocto Project Development Tasks Manual.

.. _ref-classes-buildstats:

``buildstats.bbclass``
======================

The ``buildstats`` class records performance statistics about each task
executed during the build (e.g. elapsed time, CPU usage, and I/O usage).

When you use this class, the output goes into the
:term:`BUILDSTATS_BASE` directory, which defaults
to ``${TMPDIR}/buildstats/``. You can analyze the elapsed time using
``scripts/pybootchartgui/pybootchartgui.py``, which produces a cascading
chart of the entire build process and can be useful for highlighting
bottlenecks.

Collecting build statistics is enabled by default through the
:term:`USER_CLASSES` variable from your
``local.conf`` file. Consequently, you do not have to do anything to
enable the class. However, if you want to disable the class, simply
remove "buildstats" from the :term:`USER_CLASSES` list.

.. _ref-classes-buildstats-summary:

``buildstats-summary.bbclass``
==============================

When inherited globally, prints statistics at the end of the build on
sstate re-use. In order to function, this class requires the
:ref:`buildstats <ref-classes-buildstats>` class be enabled.

.. _ref-classes-ccache:

``ccache.bbclass``
==================

The ``ccache`` class enables the C/C++ Compiler Cache for the build.
This class is used to give a minor performance boost during the build.
However, using the class can lead to unexpected side-effects. Thus, it
is recommended that you do not use this class. See
https://ccache.samba.org/ for information on the C/C++ Compiler
Cache.

.. _ref-classes-chrpath:

``chrpath.bbclass``
===================

The ``chrpath`` class is a wrapper around the "chrpath" utility, which
is used during the build process for ``nativesdk``, ``cross``, and
``cross-canadian`` recipes to change ``RPATH`` records within binaries
in order to make them relocatable.

.. _ref-classes-cmake:

``cmake.bbclass``
=================

The ``cmake`` class allows for recipes that need to build software using
the `CMake <https://cmake.org/overview/>`__ build system. You can use
the :term:`EXTRA_OECMAKE` variable to specify
additional configuration options to be passed using the ``cmake``
command line.

On the occasion that you would be installing custom CMake toolchain
files supplied by the application being built, you should install them
to the preferred CMake Module directory: ``${D}${datadir}/cmake/``
Modules during
:ref:`ref-tasks-install`.

.. _ref-classes-cml1:

``cml1.bbclass``
================

The ``cml1`` class provides basic support for the Linux kernel style
build configuration system.

.. _ref-classes-compress_doc:

``compress_doc.bbclass``
========================

Enables compression for man pages and info pages. This class is intended
to be inherited globally. The default compression mechanism is gz (gzip)
but you can select an alternative mechanism by setting the
:term:`DOC_COMPRESS` variable.

.. _ref-classes-copyleft_compliance:

``copyleft_compliance.bbclass``
===============================

The ``copyleft_compliance`` class preserves source code for the purposes
of license compliance. This class is an alternative to the ``archiver``
class and is still used by some users even though it has been deprecated
in favor of the :ref:`archiver <ref-classes-archiver>` class.

.. _ref-classes-copyleft_filter:

``copyleft_filter.bbclass``
===========================

A class used by the :ref:`archiver <ref-classes-archiver>` and
:ref:`copyleft_compliance <ref-classes-copyleft_compliance>` classes
for filtering licenses. The ``copyleft_filter`` class is an internal
class and is not intended to be used directly.

.. _ref-classes-core-image:

``core-image.bbclass``
======================

The ``core-image`` class provides common definitions for the
``core-image-*`` image recipes, such as support for additional
:term:`IMAGE_FEATURES`.

.. _ref-classes-cpan:

``cpan*.bbclass``
=================

The ``cpan*`` classes support Perl modules.

Recipes for Perl modules are simple. These recipes usually only need to
point to the source's archive and then inherit the proper class file.
Building is split into two methods depending on which method the module
authors used.

-  Modules that use old ``Makefile.PL``-based build system require
   ``cpan.bbclass`` in their recipes.

-  Modules that use ``Build.PL``-based build system require using
   ``cpan_build.bbclass`` in their recipes.

Both build methods inherit the ``cpan-base`` class for basic Perl
support.

.. _ref-classes-cross:

``cross.bbclass``
=================

The ``cross`` class provides support for the recipes that build the
cross-compilation tools.

.. _ref-classes-cross-canadian:

``cross-canadian.bbclass``
==========================

The ``cross-canadian`` class provides support for the recipes that build
the Canadian Cross-compilation tools for SDKs. See the
":ref:`overview-manual/concepts:cross-development toolchain generation`"
section in the Yocto Project Overview and Concepts Manual for more
discussion on these cross-compilation tools.

.. _ref-classes-crosssdk:

``crosssdk.bbclass``
====================

The ``crosssdk`` class provides support for the recipes that build the
cross-compilation tools used for building SDKs. See the
":ref:`overview-manual/concepts:cross-development toolchain generation`"
section in the Yocto Project Overview and Concepts Manual for more
discussion on these cross-compilation tools.

.. _ref-classes-cve-check:

``cve-check.bbclass``
=====================

The ``cve-check`` class looks for known CVEs (Common Vulnerabilities
and Exposures) while building an image. This class is meant to be
inherited globally from a configuration file::

   INHERIT += "cve-check"

You can also look for vulnerabilities in specific packages by passing
``-c cve_check`` to BitBake. You will find details in the
":ref:`dev-manual/common-tasks:checking for vulnerabilities`"
section in the Development Tasks Manual.

.. _ref-classes-debian:

``debian.bbclass``
==================

The ``debian`` class renames output packages so that they follow the
Debian naming policy (i.e. ``glibc`` becomes ``libc6`` and
``glibc-devel`` becomes ``libc6-dev``.) Renaming includes the library
name and version as part of the package name.

If a recipe creates packages for multiple libraries (shared object files
of ``.so`` type), use the :term:`LEAD_SONAME`
variable in the recipe to specify the library on which to apply the
naming scheme.

.. _ref-classes-deploy:

``deploy.bbclass``
==================

The ``deploy`` class handles deploying files to the
:term:`DEPLOY_DIR_IMAGE` directory. The main
function of this class is to allow the deploy step to be accelerated by
shared state. Recipes that inherit this class should define their own
:ref:`ref-tasks-deploy` function to copy the files to be
deployed to :term:`DEPLOYDIR`, and use ``addtask`` to
add the task at the appropriate place, which is usually after
:ref:`ref-tasks-compile` or
:ref:`ref-tasks-install`. The class then takes care of
staging the files from :term:`DEPLOYDIR` to :term:`DEPLOY_DIR_IMAGE`.

.. _ref-classes-devshell:

``devshell.bbclass``
====================

The ``devshell`` class adds the ``do_devshell`` task. Distribution
policy dictates whether to include this class. See the ":ref:`dev-manual/common-tasks:using a development shell`"
section in the Yocto Project Development Tasks Manual for more
information about using ``devshell``.

.. _ref-classes-devupstream:

``devupstream.bbclass``
=======================

The ``devupstream`` class uses
:term:`BBCLASSEXTEND` to add a variant of the
recipe that fetches from an alternative URI (e.g. Git) instead of a
tarball. Following is an example::

   BBCLASSEXTEND = "devupstream:target"
   SRC_URI:class-devupstream = "git://git.example.com/example"
   SRCREV:class-devupstream = "abcd1234"

Adding the above statements to your recipe creates a variant that has
:term:`DEFAULT_PREFERENCE` set to "-1".
Consequently, you need to select the variant of the recipe to use it.
Any development-specific adjustments can be done by using the
``class-devupstream`` override. Here is an example::

   DEPENDS:append:class-devupstream = " gperf-native"
   do_configure:prepend:class-devupstream() {
       touch ${S}/README
   }

The class
currently only supports creating a development variant of the target
recipe, not ``native`` or ``nativesdk`` variants.

The :term:`BBCLASSEXTEND` syntax (i.e. ``devupstream:target``) provides
support for ``native`` and ``nativesdk`` variants. Consequently, this
functionality can be added in a future release.

Support for other version control systems such as Subversion is limited
due to BitBake's automatic fetch dependencies (e.g.
``subversion-native``).

.. _ref-classes-distutils3:

``distutils3*.bbclass``
=======================

The ``distutils3*`` classes support recipes for Python version 3.x
extensions, which are simple. These recipes usually only need to point
to the source's archive and then inherit the proper class. Building is
split into three methods depending on which method the module authors
used.

-  Extensions that use an Autotools-based build system require Autotools
   and ``distutils``-based classes in their recipes.

-  Extensions that use ``distutils``-based build systems require the
   ``distutils`` class in their recipes.

-  Extensions that use build systems based on ``setuptools3`` require
   the :ref:`setuptools3 <ref-classes-setuptools3>` class in their
   recipes.

.. _ref-classes-externalsrc:

``externalsrc.bbclass``
=======================

The ``externalsrc`` class supports building software from source code
that is external to the OpenEmbedded build system. Building software
from an external source tree means that the build system's normal fetch,
unpack, and patch process is not used.

By default, the OpenEmbedded build system uses the :term:`S`
and :term:`B` variables to locate unpacked recipe source code
and to build it, respectively. When your recipe inherits the
``externalsrc`` class, you use the
:term:`EXTERNALSRC` and
:term:`EXTERNALSRC_BUILD` variables to
ultimately define :term:`S` and :term:`B`.

By default, this class expects the source code to support recipe builds
that use the :term:`B` variable to point to the directory in
which the OpenEmbedded build system places the generated objects built
from the recipes. By default, the :term:`B` directory is set to the
following, which is separate from the source directory (:term:`S`)::

   ${WORKDIR}/${BPN}/{PV}/

See these variables for more information:
:term:`WORKDIR`, :term:`BPN`, and
:term:`PV`,

For more information on the ``externalsrc`` class, see the comments in
``meta/classes/externalsrc.bbclass`` in the :term:`Source Directory`.
For information on how to use the
``externalsrc`` class, see the
":ref:`dev-manual/common-tasks:building software from an external source`"
section in the Yocto Project Development Tasks Manual.

.. _ref-classes-extrausers:

``extrausers.bbclass``
======================

The ``extrausers`` class allows additional user and group configuration
to be applied at the image level. Inheriting this class either globally
or from an image recipe allows additional user and group operations to
be performed using the
:term:`EXTRA_USERS_PARAMS` variable.

.. note::

   The user and group operations added using the
   extrausers
   class are not tied to a specific recipe outside of the recipe for the
   image. Thus, the operations can be performed across the image as a
   whole. Use the
   useradd
   class to add user and group configuration to a specific recipe.

Here is an example that uses this class in an image recipe::

   inherit extrausers
   EXTRA_USERS_PARAMS = "\
       useradd -p '' tester; \
       groupadd developers; \
       userdel nobody; \
       groupdel -g video; \
       groupmod -g 1020 developers; \
       usermod -s /bin/sh tester; \
       "

Here is an example that adds two users named "tester-jim" and "tester-sue" and assigns
passwords::

   inherit extrausers
   EXTRA_USERS_PARAMS = "\
       useradd -P tester01 tester-jim; \
       useradd -P tester01 tester-sue; \
       "

Finally, here is an example that sets the root password to "1876*18"::

   inherit extrausers
   EXTRA_USERS_PARAMS = "\
       usermod -P 1876*18 root; \
       "

.. _ref-classes-features_check:

``features_check.bbclass``
=================================

The ``features_check`` class allows individual recipes to check
for required and conflicting
:term:`DISTRO_FEATURES`, :term:`MACHINE_FEATURES` or :term:`COMBINED_FEATURES`.

This class provides support for the following variables:

- :term:`REQUIRED_DISTRO_FEATURES`
- :term:`CONFLICT_DISTRO_FEATURES`
- :term:`ANY_OF_DISTRO_FEATURES`
- ``REQUIRED_MACHINE_FEATURES``
- ``CONFLICT_MACHINE_FEATURES``
- ``ANY_OF_MACHINE_FEATURES``
- ``REQUIRED_COMBINED_FEATURES``
- ``CONFLICT_COMBINED_FEATURES``
- ``ANY_OF_COMBINED_FEATURES``

If any conditions specified in the recipe using the above
variables are not met, the recipe will be skipped, and if the
build system attempts to build the recipe then an error will be
triggered.

.. _ref-classes-fontcache:

``fontcache.bbclass``
=====================

The ``fontcache`` class generates the proper post-install and
post-remove (postinst and postrm) scriptlets for font packages. These
scriptlets call ``fc-cache`` (part of ``Fontconfig``) to add the fonts
to the font information cache. Since the cache files are
architecture-specific, ``fc-cache`` runs using QEMU if the postinst
scriptlets need to be run on the build host during image creation.

If the fonts being installed are in packages other than the main
package, set :term:`FONT_PACKAGES` to specify the
packages containing the fonts.

.. _ref-classes-fs-uuid:

``fs-uuid.bbclass``
===================

The ``fs-uuid`` class extracts UUID from
``${``\ :term:`ROOTFS`\ ``}``, which must have been built
by the time that this function gets called. The ``fs-uuid`` class only
works on ``ext`` file systems and depends on ``tune2fs``.

.. _ref-classes-gconf:

``gconf.bbclass``
=================

The ``gconf`` class provides common functionality for recipes that need
to install GConf schemas. The schemas will be put into a separate
package (``${``\ :term:`PN`\ ``}-gconf``) that is created
automatically when this class is inherited. This package uses the
appropriate post-install and post-remove (postinst/postrm) scriptlets to
register and unregister the schemas in the target image.

.. _ref-classes-gettext:

``gettext.bbclass``
===================

The ``gettext`` class provides support for building software that uses
the GNU ``gettext`` internationalization and localization system. All
recipes building software that use ``gettext`` should inherit this
class.

.. _ref-classes-gnomebase:

``gnomebase.bbclass``
=====================

The ``gnomebase`` class is the base class for recipes that build
software from the GNOME stack. This class sets
:term:`SRC_URI` to download the source from the GNOME
mirrors as well as extending :term:`FILES` with the typical
GNOME installation paths.

.. _ref-classes-gobject-introspection:

``gobject-introspection.bbclass``
=================================

Provides support for recipes building software that supports GObject
introspection. This functionality is only enabled if the
"gobject-introspection-data" feature is in
:term:`DISTRO_FEATURES` as well as
"qemu-usermode" being in
:term:`MACHINE_FEATURES`.

.. note::

   This functionality is backfilled by default and, if not applicable,
   should be disabled through :term:`DISTRO_FEATURES_BACKFILL_CONSIDERED` or
   :term:`MACHINE_FEATURES_BACKFILL_CONSIDERED`, respectively.

.. _ref-classes-grub-efi:

``grub-efi.bbclass``
====================

The ``grub-efi`` class provides ``grub-efi``-specific functions for
building bootable images.

This class supports several variables:

-  :term:`INITRD`: Indicates list of filesystem images to
   concatenate and use as an initial RAM disk (initrd) (optional).

-  :term:`ROOTFS`: Indicates a filesystem image to include
   as the root filesystem (optional).

-  :term:`GRUB_GFXSERIAL`: Set this to "1" to have
   graphics and serial in the boot menu.

-  :term:`LABELS`: A list of targets for the automatic
   configuration.

-  :term:`APPEND`: An override list of append strings for
   each ``LABEL``.

-  :term:`GRUB_OPTS`: Additional options to add to the
   configuration (optional). Options are delimited using semi-colon
   characters (``;``).

-  :term:`GRUB_TIMEOUT`: Timeout before executing
   the default ``LABEL`` (optional).

.. _ref-classes-gsettings:

``gsettings.bbclass``
=====================

The ``gsettings`` class provides common functionality for recipes that
need to install GSettings (glib) schemas. The schemas are assumed to be
part of the main package. Appropriate post-install and post-remove
(postinst/postrm) scriptlets are added to register and unregister the
schemas in the target image.

.. _ref-classes-gtk-doc:

``gtk-doc.bbclass``
===================

The ``gtk-doc`` class is a helper class to pull in the appropriate
``gtk-doc`` dependencies and disable ``gtk-doc``.

.. _ref-classes-gtk-icon-cache:

``gtk-icon-cache.bbclass``
==========================

The ``gtk-icon-cache`` class generates the proper post-install and
post-remove (postinst/postrm) scriptlets for packages that use GTK+ and
install icons. These scriptlets call ``gtk-update-icon-cache`` to add
the fonts to GTK+'s icon cache. Since the cache files are
architecture-specific, ``gtk-update-icon-cache`` is run using QEMU if
the postinst scriptlets need to be run on the build host during image
creation.

.. _ref-classes-gtk-immodules-cache:

``gtk-immodules-cache.bbclass``
===============================

The ``gtk-immodules-cache`` class generates the proper post-install and
post-remove (postinst/postrm) scriptlets for packages that install GTK+
input method modules for virtual keyboards. These scriptlets call
``gtk-update-icon-cache`` to add the input method modules to the cache.
Since the cache files are architecture-specific,
``gtk-update-icon-cache`` is run using QEMU if the postinst scriptlets
need to be run on the build host during image creation.

If the input method modules being installed are in packages other than
the main package, set
:term:`GTKIMMODULES_PACKAGES` to specify
the packages containing the modules.

.. _ref-classes-gzipnative:

``gzipnative.bbclass``
======================

The ``gzipnative`` class enables the use of different native versions of
``gzip`` and ``pigz`` rather than the versions of these tools from the
build host.

.. _ref-classes-icecc:

``icecc.bbclass``
=================

The ``icecc`` class supports
`Icecream <https://github.com/icecc/icecream>`__, which facilitates
taking compile jobs and distributing them among remote machines.

The class stages directories with symlinks from ``gcc`` and ``g++`` to
``icecc``, for both native and cross compilers. Depending on each
configure or compile, the OpenEmbedded build system adds the directories
at the head of the ``PATH`` list and then sets the ``ICECC_CXX`` and
``ICEC_CC`` variables, which are the paths to the ``g++`` and ``gcc``
compilers, respectively.

For the cross compiler, the class creates a ``tar.gz`` file that
contains the Yocto Project toolchain and sets ``ICECC_VERSION``, which
is the version of the cross-compiler used in the cross-development
toolchain, accordingly.

The class handles all three different compile stages (i.e native
,cross-kernel and target) and creates the necessary environment
``tar.gz`` file to be used by the remote machines. The class also
supports SDK generation.

If :term:`ICECC_PATH` is not set in your
``local.conf`` file, then the class tries to locate the ``icecc`` binary
using ``which``. If :term:`ICECC_ENV_EXEC` is set
in your ``local.conf`` file, the variable should point to the
``icecc-create-env`` script provided by the user. If you do not point to
a user-provided script, the build system uses the default script
provided by the recipe ``icecc-create-env-native.bb``.

.. note::

   This script is a modified version and not the one that comes with
   icecc.

If you do not want the Icecream distributed compile support to apply to
specific recipes or classes, you can effectively "blacklist" them by
listing the recipes and classes using the
:term:`ICECC_USER_PACKAGE_BL` and
:term:`ICECC_USER_CLASS_BL`, variables,
respectively, in your ``local.conf`` file. Doing so causes the
OpenEmbedded build system to handle these compilations locally.

Additionally, you can list recipes using the
:term:`ICECC_USER_PACKAGE_WL` variable in
your ``local.conf`` file to force ``icecc`` to be enabled for recipes
using an empty :term:`PARALLEL_MAKE` variable.

Inheriting the ``icecc`` class changes all sstate signatures.
Consequently, if a development team has a dedicated build system that
populates :term:`SSTATE_MIRRORS` and they want to
reuse sstate from :term:`SSTATE_MIRRORS`, then all developers and the build
system need to either inherit the ``icecc`` class or nobody should.

At the distribution level, you can inherit the ``icecc`` class to be
sure that all builders start with the same sstate signatures. After
inheriting the class, you can then disable the feature by setting the
:term:`ICECC_DISABLED` variable to "1" as follows::

   INHERIT_DISTRO:append = " icecc"
   ICECC_DISABLED ??= "1"

This practice
makes sure everyone is using the same signatures but also requires
individuals that do want to use Icecream to enable the feature
individually as follows in your ``local.conf`` file::

   ICECC_DISABLED = ""

.. _ref-classes-image:

``image.bbclass``
=================

The ``image`` class helps support creating images in different formats.
First, the root filesystem is created from packages using one of the
``rootfs*.bbclass`` files (depending on the package format used) and
then one or more image files are created.

-  The :term:`IMAGE_FSTYPES` variable controls the types of images to
   generate.

-  The :term:`IMAGE_INSTALL` variable controls the list of packages to
   install into the image.

For information on customizing images, see the
":ref:`dev-manual/common-tasks:customizing images`" section
in the Yocto Project Development Tasks Manual. For information on how
images are created, see the
":ref:`overview-manual/concepts:images`" section in the
Yocto Project Overview and Concepts Manual.

.. _ref-classes-image-buildinfo:

``image-buildinfo.bbclass``
===========================

The ``image-buildinfo`` class writes information to the target
filesystem on ``/etc/build``.

.. _ref-classes-image_types:

``image_types.bbclass``
=======================

The ``image_types`` class defines all of the standard image output types
that you can enable through the
:term:`IMAGE_FSTYPES` variable. You can use this
class as a reference on how to add support for custom image output
types.

By default, the :ref:`image <ref-classes-image>` class automatically
enables the ``image_types`` class. The ``image`` class uses the
``IMGCLASSES`` variable as follows::

   IMGCLASSES = "rootfs_${IMAGE_PKGTYPE} image_types ${IMAGE_CLASSES}"
   IMGCLASSES += "${@['populate_sdk_base', 'populate_sdk_ext']['linux' in d.getVar("SDK_OS")]}"
   IMGCLASSES += "${@bb.utils.contains_any('IMAGE_FSTYPES', 'live iso hddimg', 'image-live', '', d)}"
   IMGCLASSES += "${@bb.utils.contains('IMAGE_FSTYPES', 'container', 'image-container', '', d)}"
   IMGCLASSES += "image_types_wic"
   IMGCLASSES += "rootfs-postcommands"
   IMGCLASSES += "image-postinst-intercepts"
   inherit ${IMGCLASSES}

The ``image_types`` class also handles conversion and compression of images.

.. note::

   To build a VMware VMDK image, you need to add "wic.vmdk" to
   :term:`IMAGE_FSTYPES`. This would also be similar for Virtual Box Virtual Disk
   Image ("vdi") and QEMU Copy On Write Version 2 ("qcow2") images.

.. _ref-classes-image-live:

``image-live.bbclass``
======================

This class controls building "live" (i.e. HDDIMG and ISO) images. Live
images contain syslinux for legacy booting, as well as the bootloader
specified by :term:`EFI_PROVIDER` if
:term:`MACHINE_FEATURES` contains "efi".

Normally, you do not use this class directly. Instead, you add "live" to
:term:`IMAGE_FSTYPES`.

.. _ref-classes-image-prelink:

``image-prelink.bbclass``
=========================

The ``image-prelink`` class enables the use of the ``prelink`` utility
during the :ref:`ref-tasks-rootfs` task, which optimizes
the dynamic linking of shared libraries to reduce executable startup
time.

By default, the class is enabled in the ``local.conf.template`` using
the :term:`USER_CLASSES` variable as follows::

   USER_CLASSES ?= "buildstats image-prelink"

.. _ref-classes-insane:

``insane.bbclass``
==================

The ``insane`` class adds a step to the package generation process so
that output quality assurance checks are generated by the OpenEmbedded
build system. A range of checks are performed that check the build's
output for common problems that show up during runtime. Distribution
policy usually dictates whether to include this class.

You can configure the sanity checks so that specific test failures
either raise a warning or an error message. Typically, failures for new
tests generate a warning. Subsequent failures for the same test would
then generate an error message once the metadata is in a known and good
condition. See the ":doc:`/ref-manual/qa-checks`" Chapter for a list of all the warning
and error messages you might encounter using a default configuration.

Use the :term:`WARN_QA` and
:term:`ERROR_QA` variables to control the behavior of
these checks at the global level (i.e. in your custom distro
configuration). However, to skip one or more checks in recipes, you
should use :term:`INSANE_SKIP`. For example, to skip
the check for symbolic link ``.so`` files in the main package of a
recipe, add the following to the recipe. You need to realize that the
package name override, in this example ``${PN}``, must be used::

   INSANE_SKIP:${PN} += "dev-so"

Please keep in mind that the QA checks
are meant to detect real or potential problems in the packaged
output. So exercise caution when disabling these checks.

Here are the tests you can list with the :term:`WARN_QA` and
:term:`ERROR_QA` variables:

-  ``already-stripped:`` Checks that produced binaries have not
   already been stripped prior to the build system extracting debug
   symbols. It is common for upstream software projects to default to
   stripping debug symbols for output binaries. In order for debugging
   to work on the target using ``-dbg`` packages, this stripping must be
   disabled.

-  ``arch:`` Checks the Executable and Linkable Format (ELF) type, bit
   size, and endianness of any binaries to ensure they match the target
   architecture. This test fails if any binaries do not match the type
   since there would be an incompatibility. The test could indicate that
   the wrong compiler or compiler options have been used. Sometimes
   software, like bootloaders, might need to bypass this check.

-  ``buildpaths:`` Checks for paths to locations on the build host
   inside the output files. Currently, this test triggers too many false
   positives and thus is not normally enabled.

-  ``build-deps:`` Determines if a build-time dependency that is
   specified through :term:`DEPENDS`, explicit
   :term:`RDEPENDS`, or task-level dependencies exists
   to match any runtime dependency. This determination is particularly
   useful to discover where runtime dependencies are detected and added
   during packaging. If no explicit dependency has been specified within
   the metadata, at the packaging stage it is too late to ensure that
   the dependency is built, and thus you can end up with an error when
   the package is installed into the image during the
   :ref:`ref-tasks-rootfs` task because the auto-detected
   dependency was not satisfied. An example of this would be where the
   :ref:`update-rc.d <ref-classes-update-rc.d>` class automatically
   adds a dependency on the ``initscripts-functions`` package to
   packages that install an initscript that refers to
   ``/etc/init.d/functions``. The recipe should really have an explicit
   :term:`RDEPENDS` for the package in question on ``initscripts-functions``
   so that the OpenEmbedded build system is able to ensure that the
   ``initscripts`` recipe is actually built and thus the
   ``initscripts-functions`` package is made available.

-  ``compile-host-path:`` Checks the
   :ref:`ref-tasks-compile` log for indications that
   paths to locations on the build host were used. Using such paths
   might result in host contamination of the build output.

-  ``debug-deps:`` Checks that all packages except ``-dbg`` packages
   do not depend on ``-dbg`` packages, which would cause a packaging
   bug.

-  ``debug-files:`` Checks for ``.debug`` directories in anything but
   the ``-dbg`` package. The debug files should all be in the ``-dbg``
   package. Thus, anything packaged elsewhere is incorrect packaging.

-  ``dep-cmp:`` Checks for invalid version comparison statements in
   runtime dependency relationships between packages (i.e. in
   :term:`RDEPENDS`,
   :term:`RRECOMMENDS`,
   :term:`RSUGGESTS`,
   :term:`RPROVIDES`,
   :term:`RREPLACES`, and
   :term:`RCONFLICTS` variable values). Any invalid
   comparisons might trigger failures or undesirable behavior when
   passed to the package manager.

-  ``desktop:`` Runs the ``desktop-file-validate`` program against any
   ``.desktop`` files to validate their contents against the
   specification for ``.desktop`` files.

-  ``dev-deps:`` Checks that all packages except ``-dev`` or
   ``-staticdev`` packages do not depend on ``-dev`` packages, which
   would be a packaging bug.

-  ``dev-so:`` Checks that the ``.so`` symbolic links are in the
   ``-dev`` package and not in any of the other packages. In general,
   these symlinks are only useful for development purposes. Thus, the
   ``-dev`` package is the correct location for them. In very rare
   cases, such as dynamically loaded modules, these symlinks
   are needed instead in the main package.

-  ``file-rdeps:`` Checks that file-level dependencies identified by
   the OpenEmbedded build system at packaging time are satisfied. For
   example, a shell script might start with the line ``#!/bin/bash``.
   This line would translate to a file dependency on ``/bin/bash``. Of
   the three package managers that the OpenEmbedded build system
   supports, only RPM directly handles file-level dependencies,
   resolving them automatically to packages providing the files.
   However, the lack of that functionality in the other two package
   managers does not mean the dependencies do not still need resolving.
   This QA check attempts to ensure that explicitly declared
   :term:`RDEPENDS` exist to handle any file-level
   dependency detected in packaged files.

-  ``files-invalid:`` Checks for :term:`FILES` variable
   values that contain "//", which is invalid.

-  ``host-user-contaminated:`` Checks that no package produced by the
   recipe contains any files outside of ``/home`` with a user or group
   ID that matches the user running BitBake. A match usually indicates
   that the files are being installed with an incorrect UID/GID, since
   target IDs are independent from host IDs. For additional information,
   see the section describing the
   :ref:`ref-tasks-install` task.

-  ``incompatible-license:`` Report when packages are excluded from
   being created due to being marked with a license that is in
   :term:`INCOMPATIBLE_LICENSE`.

-  ``install-host-path:`` Checks the
   :ref:`ref-tasks-install` log for indications that
   paths to locations on the build host were used. Using such paths
   might result in host contamination of the build output.

-  ``installed-vs-shipped:`` Reports when files have been installed
   within ``do_install`` but have not been included in any package by
   way of the :term:`FILES` variable. Files that do not
   appear in any package cannot be present in an image later on in the
   build process. Ideally, all installed files should be packaged or not
   installed at all. These files can be deleted at the end of
   ``do_install`` if the files are not needed in any package.

-  ``invalid-chars:`` Checks that the recipe metadata variables
   :term:`DESCRIPTION`,
   :term:`SUMMARY`, :term:`LICENSE`, and
   :term:`SECTION` do not contain non-UTF-8 characters.
   Some package managers do not support such characters.

-  ``invalid-packageconfig:`` Checks that no undefined features are
   being added to :term:`PACKAGECONFIG`. For
   example, any name "foo" for which the following form does not exist::

      PACKAGECONFIG[foo] = "..."

-  ``la:`` Checks ``.la`` files for any :term:`TMPDIR` paths. Any ``.la``
   file containing these paths is incorrect since ``libtool`` adds the
   correct sysroot prefix when using the files automatically itself.

-  ``ldflags:`` Ensures that the binaries were linked with the
   :term:`LDFLAGS` options provided by the build system.
   If this test fails, check that the :term:`LDFLAGS` variable is being
   passed to the linker command.

-  ``libdir:`` Checks for libraries being installed into incorrect
   (possibly hardcoded) installation paths. For example, this test will
   catch recipes that install ``/lib/bar.so`` when ``${base_libdir}`` is
   "lib32". Another example is when recipes install
   ``/usr/lib64/foo.so`` when ``${libdir}`` is "/usr/lib".

-  ``libexec:`` Checks if a package contains files in
   ``/usr/libexec``. This check is not performed if the ``libexecdir``
   variable has been set explicitly to ``/usr/libexec``.

-  ``packages-list:`` Checks for the same package being listed
   multiple times through the :term:`PACKAGES` variable
   value. Installing the package in this manner can cause errors during
   packaging.

-  ``perm-config:`` Reports lines in ``fs-perms.txt`` that have an
   invalid format.

-  ``perm-line:`` Reports lines in ``fs-perms.txt`` that have an
   invalid format.

-  ``perm-link:`` Reports lines in ``fs-perms.txt`` that specify
   'link' where the specified target already exists.

-  ``perms:`` Currently, this check is unused but reserved.

-  ``pkgconfig:`` Checks ``.pc`` files for any
   :term:`TMPDIR`/:term:`WORKDIR` paths.
   Any ``.pc`` file containing these paths is incorrect since
   ``pkg-config`` itself adds the correct sysroot prefix when the files
   are accessed.

-  ``pkgname:`` Checks that all packages in
   :term:`PACKAGES` have names that do not contain
   invalid characters (i.e. characters other than 0-9, a-z, ., +, and
   -).

-  ``pkgv-undefined:`` Checks to see if the :term:`PKGV` variable is
   undefined during :ref:`ref-tasks-package`.

-  ``pkgvarcheck:`` Checks through the variables
   :term:`RDEPENDS`,
   :term:`RRECOMMENDS`,
   :term:`RSUGGESTS`,
   :term:`RCONFLICTS`,
   :term:`RPROVIDES`,
   :term:`RREPLACES`, :term:`FILES`,
   :term:`ALLOW_EMPTY`, ``pkg_preinst``,
   ``pkg_postinst``, ``pkg_prerm`` and ``pkg_postrm``, and reports if
   there are variable sets that are not package-specific. Using these
   variables without a package suffix is bad practice, and might
   unnecessarily complicate dependencies of other packages within the
   same recipe or have other unintended consequences.

-  ``pn-overrides:`` Checks that a recipe does not have a name
   (:term:`PN`) value that appears in
   :term:`OVERRIDES`. If a recipe is named such that
   its :term:`PN` value matches something already in :term:`OVERRIDES` (e.g.
   :term:`PN` happens to be the same as :term:`MACHINE` or
   :term:`DISTRO`), it can have unexpected consequences.
   For example, assignments such as ``FILES:${PN} = "xyz"`` effectively
   turn into ``FILES = "xyz"``.

-  ``rpaths:`` Checks for rpaths in the binaries that contain build
   system paths such as :term:`TMPDIR`. If this test fails, bad ``-rpath``
   options are being passed to the linker commands and your binaries
   have potential security issues.

-  ``split-strip:`` Reports that splitting or stripping debug symbols
   from binaries has failed.

-  ``staticdev:`` Checks for static library files (``*.a``) in
   non-``staticdev`` packages.

-  ``symlink-to-sysroot:`` Checks for symlinks in packages that point
   into :term:`TMPDIR` on the host. Such symlinks will
   work on the host, but are clearly invalid when running on the target.

-  ``textrel:`` Checks for ELF binaries that contain relocations in
   their ``.text`` sections, which can result in a performance impact at
   runtime. See the explanation for the ``ELF binary`` message in
   ":doc:`/ref-manual/qa-checks`" for more information regarding runtime performance
   issues.

-  ``unlisted-pkg-lics:`` Checks that all declared licenses applying
   for a package are also declared on the recipe level (i.e. any license
   in ``LICENSE:*`` should appear in :term:`LICENSE`).

-  ``useless-rpaths:`` Checks for dynamic library load paths (rpaths)
   in the binaries that by default on a standard system are searched by
   the linker (e.g. ``/lib`` and ``/usr/lib``). While these paths will
   not cause any breakage, they do waste space and are unnecessary.

-  ``var-undefined:`` Reports when variables fundamental to packaging
   (i.e. :term:`WORKDIR`,
   :term:`DEPLOY_DIR`, :term:`D`,
   :term:`PN`, and :term:`PKGD`) are undefined
   during :ref:`ref-tasks-package`.

-  ``version-going-backwards:`` If Build History is enabled, reports
   when a package being written out has a lower version than the
   previously written package under the same name. If you are placing
   output packages into a feed and upgrading packages on a target system
   using that feed, the version of a package going backwards can result
   in the target system not correctly upgrading to the "new" version of
   the package.

   .. note::

      This is only relevant when you are using runtime package management
      on your target system.

-  ``xorg-driver-abi:`` Checks that all packages containing Xorg
   drivers have ABI dependencies. The ``xserver-xorg`` recipe provides
   driver ABI names. All drivers should depend on the ABI versions that
   they have been built against. Driver recipes that include
   ``xorg-driver-input.inc`` or ``xorg-driver-video.inc`` will
   automatically get these versions. Consequently, you should only need
   to explicitly add dependencies to binary driver recipes.

.. _ref-classes-insserv:

``insserv.bbclass``
===================

The ``insserv`` class uses the ``insserv`` utility to update the order
of symbolic links in ``/etc/rc?.d/`` within an image based on
dependencies specified by LSB headers in the ``init.d`` scripts
themselves.

.. _ref-classes-kernel:

``kernel.bbclass``
==================

The ``kernel`` class handles building Linux kernels. The class contains
code to build all kernel trees. All needed headers are staged into the
:term:`STAGING_KERNEL_DIR` directory to allow out-of-tree module builds
using the :ref:`module <ref-classes-module>` class.

This means that each built kernel module is packaged separately and
inter-module dependencies are created by parsing the ``modinfo`` output.
If all modules are required, then installing the ``kernel-modules``
package installs all packages with modules and various other kernel
packages such as ``kernel-vmlinux``.

The ``kernel`` class contains logic that allows you to embed an initial
RAM filesystem (initramfs) image when you build the kernel image. For
information on how to build an initramfs, see the
":ref:`dev-manual/common-tasks:building an initial ram filesystem (initramfs) image`" section in
the Yocto Project Development Tasks Manual.

Various other classes are used by the ``kernel`` and ``module`` classes
internally including the :ref:`kernel-arch <ref-classes-kernel-arch>`,
:ref:`module-base <ref-classes-module-base>`, and
:ref:`linux-kernel-base <ref-classes-linux-kernel-base>` classes.

.. _ref-classes-kernel-arch:

``kernel-arch.bbclass``
=======================

The ``kernel-arch`` class sets the ``ARCH`` environment variable for
Linux kernel compilation (including modules).

.. _ref-classes-kernel-devicetree:

``kernel-devicetree.bbclass``
=============================

The ``kernel-devicetree`` class, which is inherited by the
:ref:`kernel <ref-classes-kernel>` class, supports device tree
generation.

.. _ref-classes-kernel-fitimage:

``kernel-fitimage.bbclass``
===========================

The ``kernel-fitimage`` class provides support to pack a kernel image,
device trees, a U-boot script, a Initramfs bundle and a RAM disk
into a single FIT image. In theory, a FIT image can support any number
of kernels, U-boot scripts, Initramfs bundles, RAM disks and device-trees.
However, ``kernel-fitimage`` currently only supports
limited usescases: just one kernel image, an optional U-boot script,
an optional Initramfs bundle, an optional RAM disk, and any number of
device tree.

To create a FIT image, it is required that :term:`KERNEL_CLASSES`
is set to include "kernel-fitimage" and :term:`KERNEL_IMAGETYPE`
is set to "fitImage".

The options for the device tree compiler passed to ``mkimage -D``
when creating the FIT image are specified using the
:term:`UBOOT_MKIMAGE_DTCOPTS` variable.

Only a single kernel can be added to the FIT image created by
``kernel-fitimage`` and the kernel image in FIT is mandatory. The
address where the kernel image is to be loaded by U-Boot is
specified by :term:`UBOOT_LOADADDRESS` and the entrypoint by
:term:`UBOOT_ENTRYPOINT`.

Multiple device trees can be added to the FIT image created by
``kernel-fitimage`` and the device tree is optional.
The address where the device tree is to be loaded by U-Boot is
specified by :term:`UBOOT_DTBO_LOADADDRESS` for device tree overlays
and by :term:`UBOOT_DTB_LOADADDRESS` for device tree binaries.

Only a single RAM disk can be added to the FIT image created by
``kernel-fitimage`` and the RAM disk in FIT is optional.
The address where the RAM disk image is to be loaded by U-Boot
is specified by :term:`UBOOT_RD_LOADADDRESS` and the entrypoint by
:term:`UBOOT_RD_ENTRYPOINT`. The ramdisk is added to FIT image when
:term:`INITRAMFS_IMAGE` is specified and that :term:`INITRAMFS_IMAGE_BUNDLE`
is set to 0.

Only a single Initramfs bundle can be added to the FIT image created by
``kernel-fitimage`` and the Initramfs bundle in FIT is optional.
In case of Initramfs, the kernel is configured to be bundled with the rootfs
in the same binary (example: zImage-initramfs-:term:`MACHINE`.bin).
When the kernel is copied to RAM and executed, it unpacks the Initramfs rootfs.
The Initramfs bundle can be enabled when :term:`INITRAMFS_IMAGE`
is specified and that :term:`INITRAMFS_IMAGE_BUNDLE` is set to 1.
The address where the Initramfs bundle is to be loaded by U-boot is specified
by :term:`UBOOT_LOADADDRESS` and the entrypoint by :term:`UBOOT_ENTRYPOINT`.

Only a single U-boot boot script can be added to the FIT image created by
``kernel-fitimage`` and the boot script is optional.
The boot script is specified in the ITS file as a text file containing
U-boot commands. When using a boot script the user should configure the
U-boot ``do_install`` task to copy the script to sysroot.
So the script can be included in the FIT image by the ``kernel-fitimage``
class. At run-time, U-boot CONFIG_BOOTCOMMAND define can be configured to
load the boot script from the FIT image and executes it.

The FIT image generated by ``kernel-fitimage`` class is signed when the
variables :term:`UBOOT_SIGN_ENABLE`, :term:`UBOOT_MKIMAGE_DTCOPTS`,
:term:`UBOOT_SIGN_KEYDIR` and :term:`UBOOT_SIGN_KEYNAME` are set
appropriately. The default values used for :term:`FIT_HASH_ALG` and
:term:`FIT_SIGN_ALG` in ``kernel-fitimage`` are "sha256" and
"rsa2048" respectively. The keys for signing fitImage can be generated using
the ``kernel-fitimage`` class when both :term:`FIT_GENERATE_KEYS` and
:term:`UBOOT_SIGN_ENABLE` are set to "1".


.. _ref-classes-kernel-grub:

``kernel-grub.bbclass``
=======================

The ``kernel-grub`` class updates the boot area and the boot menu with
the kernel as the priority boot mechanism while installing a RPM to
update the kernel on a deployed target.

.. _ref-classes-kernel-module-split:

``kernel-module-split.bbclass``
===============================

The ``kernel-module-split`` class provides common functionality for
splitting Linux kernel modules into separate packages.

.. _ref-classes-kernel-uboot:

``kernel-uboot.bbclass``
========================

The ``kernel-uboot`` class provides support for building from
vmlinux-style kernel sources.

.. _ref-classes-kernel-uimage:

``kernel-uimage.bbclass``
=========================

The ``kernel-uimage`` class provides support to pack uImage.

.. _ref-classes-kernel-yocto:

``kernel-yocto.bbclass``
========================

The ``kernel-yocto`` class provides common functionality for building
from linux-yocto style kernel source repositories.

.. _ref-classes-kernelsrc:

``kernelsrc.bbclass``
=====================

The ``kernelsrc`` class sets the Linux kernel source and version.

.. _ref-classes-lib_package:

``lib_package.bbclass``
=======================

The ``lib_package`` class supports recipes that build libraries and
produce executable binaries, where those binaries should not be
installed by default along with the library. Instead, the binaries are
added to a separate ``${``\ :term:`PN`\ ``}-bin`` package to
make their installation optional.

.. _ref-classes-libc*:

``libc*.bbclass``
=================

The ``libc*`` classes support recipes that build packages with ``libc``:

-  The ``libc-common`` class provides common support for building with
   ``libc``.

-  The ``libc-package`` class supports packaging up ``glibc`` and
   ``eglibc``.

.. _ref-classes-license:

``license.bbclass``
===================

The ``license`` class provides license manifest creation and license
exclusion. This class is enabled by default using the default value for
the :term:`INHERIT_DISTRO` variable.

.. _ref-classes-linux-kernel-base:

``linux-kernel-base.bbclass``
=============================

The ``linux-kernel-base`` class provides common functionality for
recipes that build out of the Linux kernel source tree. These builds
goes beyond the kernel itself. For example, the Perf recipe also
inherits this class.

.. _ref-classes-linuxloader:

``linuxloader.bbclass``
=======================

Provides the function ``linuxloader()``, which gives the value of the
dynamic loader/linker provided on the platform. This value is used by a
number of other classes.

.. _ref-classes-logging:

``logging.bbclass``
===================

The ``logging`` class provides the standard shell functions used to log
messages for various BitBake severity levels (i.e. ``bbplain``,
``bbnote``, ``bbwarn``, ``bberror``, ``bbfatal``, and ``bbdebug``).

This class is enabled by default since it is inherited by the ``base``
class.

.. _ref-classes-meta:

``meta.bbclass``
================

The ``meta`` class is inherited by recipes that do not build any output
packages themselves, but act as a "meta" target for building other
recipes.

.. _ref-classes-metadata_scm:

``metadata_scm.bbclass``
========================

The ``metadata_scm`` class provides functionality for querying the
branch and revision of a Source Code Manager (SCM) repository.

The :ref:`base <ref-classes-base>` class uses this class to print the
revisions of each layer before starting every build. The
``metadata_scm`` class is enabled by default because it is inherited by
the ``base`` class.

.. _ref-classes-migrate_localcount:

``migrate_localcount.bbclass``
==============================

The ``migrate_localcount`` class verifies a recipe's localcount data and
increments it appropriately.

.. _ref-classes-mime:

``mime.bbclass``
================

The ``mime`` class generates the proper post-install and post-remove
(postinst/postrm) scriptlets for packages that install MIME type files.
These scriptlets call ``update-mime-database`` to add the MIME types to
the shared database.

.. _ref-classes-mirrors:

``mirrors.bbclass``
===================

The ``mirrors`` class sets up some standard
:term:`MIRRORS` entries for source code mirrors. These
mirrors provide a fall-back path in case the upstream source specified
in :term:`SRC_URI` within recipes is unavailable.

This class is enabled by default since it is inherited by the
:ref:`base <ref-classes-base>` class.

.. _ref-classes-module:

``module.bbclass``
==================

The ``module`` class provides support for building out-of-tree Linux
kernel modules. The class inherits the
:ref:`module-base <ref-classes-module-base>` and
:ref:`kernel-module-split <ref-classes-kernel-module-split>` classes,
and implements the :ref:`ref-tasks-compile` and
:ref:`ref-tasks-install` tasks. The class provides
everything needed to build and package a kernel module.

For general information on out-of-tree Linux kernel modules, see the
":ref:`kernel-dev/common:incorporating out-of-tree modules`"
section in the Yocto Project Linux Kernel Development Manual.

.. _ref-classes-module-base:

``module-base.bbclass``
=======================

The ``module-base`` class provides the base functionality for building
Linux kernel modules. Typically, a recipe that builds software that
includes one or more kernel modules and has its own means of building
the module inherits this class as opposed to inheriting the
:ref:`module <ref-classes-module>` class.

.. _ref-classes-multilib*:

``multilib*.bbclass``
=====================

The ``multilib*`` classes provide support for building libraries with
different target optimizations or target architectures and installing
them side-by-side in the same image.

For more information on using the Multilib feature, see the
":ref:`dev-manual/common-tasks:combining multiple versions of library files into one image`"
section in the Yocto Project Development Tasks Manual.

.. _ref-classes-native:

``native.bbclass``
==================

The ``native`` class provides common functionality for recipes that
build tools to run on the :term:`Build Host` (i.e. tools that use the compiler
or other tools from the build host).

You can create a recipe that builds tools that run natively on the host
a couple different ways:

-  Create a myrecipe\ ``-native.bb`` recipe that inherits the ``native``
   class. If you use this method, you must order the inherit statement
   in the recipe after all other inherit statements so that the
   ``native`` class is inherited last.

   .. note::

      When creating a recipe this way, the recipe name must follow this
      naming convention::

         myrecipe-native.bb


      Not using this naming convention can lead to subtle problems
      caused by existing code that depends on that naming convention.

-  Create or modify a target recipe that contains the following::

      BBCLASSEXTEND = "native"

   Inside the
   recipe, use ``:class-native`` and ``:class-target`` overrides to
   specify any functionality specific to the respective native or target
   case.

Although applied differently, the ``native`` class is used with both
methods. The advantage of the second method is that you do not need to
have two separate recipes (assuming you need both) for native and
target. All common parts of the recipe are automatically shared.

.. _ref-classes-nativesdk:

``nativesdk.bbclass``
=====================

The ``nativesdk`` class provides common functionality for recipes that
wish to build tools to run as part of an SDK (i.e. tools that run on
:term:`SDKMACHINE`).

You can create a recipe that builds tools that run on the SDK machine a
couple different ways:

-  Create a ``nativesdk-``\ myrecipe\ ``.bb`` recipe that inherits the
   ``nativesdk`` class. If you use this method, you must order the
   inherit statement in the recipe after all other inherit statements so
   that the ``nativesdk`` class is inherited last.

-  Create a ``nativesdk`` variant of any recipe by adding the following::

       BBCLASSEXTEND = "nativesdk"

   Inside the
   recipe, use ``:class-nativesdk`` and ``:class-target`` overrides to
   specify any functionality specific to the respective SDK machine or
   target case.

.. note::

   When creating a recipe, you must follow this naming convention::

           nativesdk-myrecipe.bb


   Not doing so can lead to subtle problems because there is code that
   depends on the naming convention.

Although applied differently, the ``nativesdk`` class is used with both
methods. The advantage of the second method is that you do not need to
have two separate recipes (assuming you need both) for the SDK machine
and the target. All common parts of the recipe are automatically shared.

.. _ref-classes-nopackages:

``nopackages.bbclass``
======================

Disables packaging tasks for those recipes and classes where packaging
is not needed.

.. _ref-classes-npm:

``npm.bbclass``
===============

Provides support for building Node.js software fetched using the `node
package manager (NPM) <https://en.wikipedia.org/wiki/Npm_(software)>`__.

.. note::

   Currently, recipes inheriting this class must use the ``npm://``
   fetcher to have dependencies fetched and packaged automatically.

For information on how to create NPM packages, see the
":ref:`dev-manual/common-tasks:creating node package manager (npm) packages`"
section in the Yocto Project Development Tasks Manual.

.. _ref-classes-oelint:

``oelint.bbclass``
==================

The ``oelint`` class is an obsolete lint checking tool available in
``meta/classes`` in the :term:`Source Directory`.

There are some classes that could be generally useful in OE-Core but
are never actually used within OE-Core itself. The ``oelint`` class is
one such example. However, being aware of this class can reduce the
proliferation of different versions of similar classes across multiple
layers.

.. _ref-classes-own-mirrors:

``own-mirrors.bbclass``
=======================

The ``own-mirrors`` class makes it easier to set up your own
:term:`PREMIRRORS` from which to first fetch source
before attempting to fetch it from the upstream specified in
:term:`SRC_URI` within each recipe.

To use this class, inherit it globally and specify
:term:`SOURCE_MIRROR_URL`. Here is an example::

   INHERIT += "own-mirrors"
   SOURCE_MIRROR_URL = "http://example.com/my-source-mirror"

You can specify only a single URL
in :term:`SOURCE_MIRROR_URL`.

.. _ref-classes-package:

``package.bbclass``
===================

The ``package`` class supports generating packages from a build's
output. The core generic functionality is in ``package.bbclass``. The
code specific to particular package types resides in these
package-specific classes:
:ref:`package_deb <ref-classes-package_deb>`,
:ref:`package_rpm <ref-classes-package_rpm>`,
:ref:`package_ipk <ref-classes-package_ipk>`, and
:ref:`package_tar <ref-classes-package_tar>`.

.. note::

   The
   package_tar
   class is broken and not supported. It is recommended that you do not
   use this class.

You can control the list of resulting package formats by using the
:term:`PACKAGE_CLASSES` variable defined in your ``conf/local.conf``
configuration file, which is located in the :term:`Build Directory`.
When defining the variable, you can
specify one or more package types. Since images are generated from
packages, a packaging class is needed to enable image generation. The
first class listed in this variable is used for image generation.

If you take the optional step to set up a repository (package feed) on
the development host that can be used by DNF, you can install packages
from the feed while you are running the image on the target (i.e.
runtime installation of packages). For more information, see the
":ref:`dev-manual/common-tasks:using runtime package management`"
section in the Yocto Project Development Tasks Manual.

The package-specific class you choose can affect build-time performance
and has space ramifications. In general, building a package with IPK
takes about thirty percent less time as compared to using RPM to build
the same or similar package. This comparison takes into account a
complete build of the package with all dependencies previously built.
The reason for this discrepancy is because the RPM package manager
creates and processes more :term:`Metadata` than the IPK package
manager. Consequently, you might consider setting :term:`PACKAGE_CLASSES` to
"package_ipk" if you are building smaller systems.

Before making your package manager decision, however, you should
consider some further things about using RPM:

-  RPM starts to provide more abilities than IPK due to the fact that it
   processes more Metadata. For example, this information includes
   individual file types, file checksum generation and evaluation on
   install, sparse file support, conflict detection and resolution for
   Multilib systems, ACID style upgrade, and repackaging abilities for
   rollbacks.

-  For smaller systems, the extra space used for the Berkeley Database
   and the amount of metadata when using RPM can affect your ability to
   perform on-device upgrades.

You can find additional information on the effects of the package class
at these two Yocto Project mailing list links:

-  :yocto_lists:`/pipermail/poky/2011-May/006362.html`

-  :yocto_lists:`/pipermail/poky/2011-May/006363.html`

.. _ref-classes-package_deb:

``package_deb.bbclass``
=======================

The ``package_deb`` class provides support for creating packages that
use the Debian (i.e. ``.deb``) file format. The class ensures the
packages are written out in a ``.deb`` file format to the
``${``\ :term:`DEPLOY_DIR_DEB`\ ``}`` directory.

This class inherits the :ref:`package <ref-classes-package>` class and
is enabled through the :term:`PACKAGE_CLASSES`
variable in the ``local.conf`` file.

.. _ref-classes-package_ipk:

``package_ipk.bbclass``
=======================

The ``package_ipk`` class provides support for creating packages that
use the IPK (i.e. ``.ipk``) file format. The class ensures the packages
are written out in a ``.ipk`` file format to the
``${``\ :term:`DEPLOY_DIR_IPK`\ ``}`` directory.

This class inherits the :ref:`package <ref-classes-package>` class and
is enabled through the :term:`PACKAGE_CLASSES`
variable in the ``local.conf`` file.

.. _ref-classes-package_rpm:

``package_rpm.bbclass``
=======================

The ``package_rpm`` class provides support for creating packages that
use the RPM (i.e. ``.rpm``) file format. The class ensures the packages
are written out in a ``.rpm`` file format to the
``${``\ :term:`DEPLOY_DIR_RPM`\ ``}`` directory.

This class inherits the :ref:`package <ref-classes-package>` class and
is enabled through the :term:`PACKAGE_CLASSES`
variable in the ``local.conf`` file.

.. _ref-classes-package_tar:

``package_tar.bbclass``
=======================

The ``package_tar`` class provides support for creating tarballs. The
class ensures the packages are written out in a tarball format to the
``${``\ :term:`DEPLOY_DIR_TAR`\ ``}`` directory.

This class inherits the :ref:`package <ref-classes-package>` class and
is enabled through the :term:`PACKAGE_CLASSES`
variable in the ``local.conf`` file.

.. note::

   You cannot specify the ``package_tar`` class first using the
   :term:`PACKAGE_CLASSES` variable. You must use ``.deb``, ``.ipk``, or ``.rpm``
   file formats for your image or SDK.

.. _ref-classes-packagedata:

``packagedata.bbclass``
=======================

The ``packagedata`` class provides common functionality for reading
``pkgdata`` files found in :term:`PKGDATA_DIR`. These
files contain information about each output package produced by the
OpenEmbedded build system.

This class is enabled by default because it is inherited by the
:ref:`package <ref-classes-package>` class.

.. _ref-classes-packagegroup:

``packagegroup.bbclass``
========================

The ``packagegroup`` class sets default values appropriate for package
group recipes (e.g. :term:`PACKAGES`, :term:`PACKAGE_ARCH`, :term:`ALLOW_EMPTY`, and
so forth). It is highly recommended that all package group recipes
inherit this class.

For information on how to use this class, see the
":ref:`dev-manual/common-tasks:customizing images using custom package groups`"
section in the Yocto Project Development Tasks Manual.

Previously, this class was called the ``task`` class.

.. _ref-classes-patch:

``patch.bbclass``
=================

The ``patch`` class provides all functionality for applying patches
during the :ref:`ref-tasks-patch` task.

This class is enabled by default because it is inherited by the
:ref:`base <ref-classes-base>` class.

.. _ref-classes-perlnative:

``perlnative.bbclass``
======================

When inherited by a recipe, the ``perlnative`` class supports using the
native version of Perl built by the build system rather than using the
version provided by the build host.

.. _ref-classes-pixbufcache:

``pixbufcache.bbclass``
=======================

The ``pixbufcache`` class generates the proper post-install and
post-remove (postinst/postrm) scriptlets for packages that install
pixbuf loaders, which are used with ``gdk-pixbuf``. These scriptlets
call ``update_pixbuf_cache`` to add the pixbuf loaders to the cache.
Since the cache files are architecture-specific, ``update_pixbuf_cache``
is run using QEMU if the postinst scriptlets need to be run on the build
host during image creation.

If the pixbuf loaders being installed are in packages other than the
recipe's main package, set
:term:`PIXBUF_PACKAGES` to specify the packages
containing the loaders.

.. _ref-classes-pkgconfig:

``pkgconfig.bbclass``
=====================

The ``pkgconfig`` class provides a standard way to get header and
library information by using ``pkg-config``. This class aims to smooth
integration of ``pkg-config`` into libraries that use it.

During staging, BitBake installs ``pkg-config`` data into the
``sysroots/`` directory. By making use of sysroot functionality within
``pkg-config``, the ``pkgconfig`` class no longer has to manipulate the
files.

.. _ref-classes-populate-sdk:

``populate_sdk.bbclass``
========================

The ``populate_sdk`` class provides support for SDK-only recipes. For
information on advantages gained when building a cross-development
toolchain using the :ref:`ref-tasks-populate_sdk`
task, see the ":ref:`sdk-manual/appendix-obtain:building an sdk installer`"
section in the Yocto Project Application Development and the Extensible
Software Development Kit (eSDK) manual.

.. _ref-classes-populate-sdk-*:

``populate_sdk_*.bbclass``
==========================

The ``populate_sdk_*`` classes support SDK creation and consist of the
following classes:

-  ``populate_sdk_base``: The base class supporting SDK creation under
   all package managers (i.e. DEB, RPM, and opkg).

-  ``populate_sdk_deb``: Supports creation of the SDK given the Debian
   package manager.

-  ``populate_sdk_rpm``: Supports creation of the SDK given the RPM
   package manager.

-  ``populate_sdk_ipk``: Supports creation of the SDK given the opkg
   (IPK format) package manager.

-  ``populate_sdk_ext``: Supports extensible SDK creation under all
   package managers.

The ``populate_sdk_base`` class inherits the appropriate
``populate_sdk_*`` (i.e. ``deb``, ``rpm``, and ``ipk``) based on
:term:`IMAGE_PKGTYPE`.

The base class ensures all source and destination directories are
established and then populates the SDK. After populating the SDK, the
``populate_sdk_base`` class constructs two sysroots:
``${``\ :term:`SDK_ARCH`\ ``}-nativesdk``, which
contains the cross-compiler and associated tooling, and the target,
which contains a target root filesystem that is configured for the SDK
usage. These two images reside in :term:`SDK_OUTPUT`,
which consists of the following::

   ${SDK_OUTPUT}/${SDK_ARCH}-nativesdk-pkgs
   ${SDK_OUTPUT}/${SDKTARGETSYSROOT}/target-pkgs

Finally, the base populate SDK class creates the toolchain environment
setup script, the tarball of the SDK, and the installer.

The respective ``populate_sdk_deb``, ``populate_sdk_rpm``, and
``populate_sdk_ipk`` classes each support the specific type of SDK.
These classes are inherited by and used with the ``populate_sdk_base``
class.

For more information on the cross-development toolchain generation, see
the ":ref:`overview-manual/concepts:cross-development toolchain generation`"
section in the Yocto Project Overview and Concepts Manual. For
information on advantages gained when building a cross-development
toolchain using the :ref:`ref-tasks-populate_sdk`
task, see the
":ref:`sdk-manual/appendix-obtain:building an sdk installer`"
section in the Yocto Project Application Development and the Extensible
Software Development Kit (eSDK) manual.

.. _ref-classes-prexport:

``prexport.bbclass``
====================

The ``prexport`` class provides functionality for exporting
:term:`PR` values.

.. note::

   This class is not intended to be used directly. Rather, it is enabled
   when using "``bitbake-prserv-tool export``".

.. _ref-classes-primport:

``primport.bbclass``
====================

The ``primport`` class provides functionality for importing
:term:`PR` values.

.. note::

   This class is not intended to be used directly. Rather, it is enabled
   when using "``bitbake-prserv-tool import``".

.. _ref-classes-prserv:

``prserv.bbclass``
==================

The ``prserv`` class provides functionality for using a :ref:`PR
service <dev-manual/common-tasks:working with a pr service>` in order to
automatically manage the incrementing of the :term:`PR`
variable for each recipe.

This class is enabled by default because it is inherited by the
:ref:`package <ref-classes-package>` class. However, the OpenEmbedded
build system will not enable the functionality of this class unless
:term:`PRSERV_HOST` has been set.

.. _ref-classes-ptest:

``ptest.bbclass``
=================

The ``ptest`` class provides functionality for packaging and installing
runtime tests for recipes that build software that provides these tests.

This class is intended to be inherited by individual recipes. However,
the class' functionality is largely disabled unless "ptest" appears in
:term:`DISTRO_FEATURES`. See the
":ref:`dev-manual/common-tasks:testing packages with ptest`"
section in the Yocto Project Development Tasks Manual for more information
on ptest.

.. _ref-classes-ptest-gnome:

``ptest-gnome.bbclass``
=======================

Enables package tests (ptests) specifically for GNOME packages, which
have tests intended to be executed with ``gnome-desktop-testing``.

For information on setting up and running ptests, see the
":ref:`dev-manual/common-tasks:testing packages with ptest`"
section in the Yocto Project Development Tasks Manual.

.. _ref-classes-python3-dir:

``python3-dir.bbclass``
=======================

The ``python3-dir`` class provides the base version, location, and site
package location for Python 3.

.. _ref-classes-python3native:

``python3native.bbclass``
=========================

The ``python3native`` class supports using the native version of Python
3 built by the build system rather than support of the version provided
by the build host.

.. _ref-classes-python3targetconfig:

``python3targetconfig.bbclass``
===============================

The ``python3targetconfig`` class supports using the native version of Python
3 built by the build system rather than support of the version provided
by the build host, except that the configuration for the target machine
is accessible (such as correct installation directories). This also adds a
dependency on target ``python3``, so should only be used where appropriate
in order to avoid unnecessarily lengthening builds.

.. _ref-classes-qemu:

``qemu.bbclass``
================

The ``qemu`` class provides functionality for recipes that either need
QEMU or test for the existence of QEMU. Typically, this class is used to
run programs for a target system on the build host using QEMU's
application emulation mode.

.. _ref-classes-recipe_sanity:

``recipe_sanity.bbclass``
=========================

The ``recipe_sanity`` class checks for the presence of any host system
recipe prerequisites that might affect the build (e.g. variables that
are set or software that is present).

.. _ref-classes-relocatable:

``relocatable.bbclass``
=======================

The ``relocatable`` class enables relocation of binaries when they are
installed into the sysroot.

This class makes use of the :ref:`chrpath <ref-classes-chrpath>` class
and is used by both the :ref:`cross <ref-classes-cross>` and
:ref:`native <ref-classes-native>` classes.

.. _ref-classes-remove-libtool:

``remove-libtool.bbclass``
==========================

The ``remove-libtool`` class adds a post function to the
:ref:`ref-tasks-install` task to remove all ``.la`` files
installed by ``libtool``. Removing these files results in them being
absent from both the sysroot and target packages.

If a recipe needs the ``.la`` files to be installed, then the recipe can
override the removal by setting ``REMOVE_LIBTOOL_LA`` to "0" as follows::

   REMOVE_LIBTOOL_LA = "0"

.. note::

   The ``remove-libtool`` class is not enabled by default.

.. _ref-classes-report-error:

``report-error.bbclass``
========================

The ``report-error`` class supports enabling the :ref:`error reporting
tool <dev-manual/common-tasks:using the error reporting tool>`",
which allows you to submit build error information to a central database.

The class collects debug information for recipe, recipe version, task,
machine, distro, build system, target system, host distro, branch,
commit, and log. From the information, report files using a JSON format
are created and stored in
``${``\ :term:`LOG_DIR`\ ``}/error-report``.

.. _ref-classes-rm-work:

``rm_work.bbclass``
===================

The ``rm_work`` class supports deletion of temporary workspace, which
can ease your hard drive demands during builds.

The OpenEmbedded build system can use a substantial amount of disk space
during the build process. A portion of this space is the work files
under the ``${TMPDIR}/work`` directory for each recipe. Once the build
system generates the packages for a recipe, the work files for that
recipe are no longer needed. However, by default, the build system
preserves these files for inspection and possible debugging purposes. If
you would rather have these files deleted to save disk space as the
build progresses, you can enable ``rm_work`` by adding the following to
your ``local.conf`` file, which is found in the :term:`Build Directory`.
::

   INHERIT += "rm_work"

If you are
modifying and building source code out of the work directory for a
recipe, enabling ``rm_work`` will potentially result in your changes to
the source being lost. To exclude some recipes from having their work
directories deleted by ``rm_work``, you can add the names of the recipe
or recipes you are working on to the :term:`RM_WORK_EXCLUDE` variable, which
can also be set in your ``local.conf`` file. Here is an example::

   RM_WORK_EXCLUDE += "busybox glibc"

.. _ref-classes-rootfs*:

``rootfs*.bbclass``
===================

The ``rootfs*`` classes support creating the root filesystem for an
image and consist of the following classes:

-  The ``rootfs-postcommands`` class, which defines filesystem
   post-processing functions for image recipes.

-  The ``rootfs_deb`` class, which supports creation of root filesystems
   for images built using ``.deb`` packages.

-  The ``rootfs_rpm`` class, which supports creation of root filesystems
   for images built using ``.rpm`` packages.

-  The ``rootfs_ipk`` class, which supports creation of root filesystems
   for images built using ``.ipk`` packages.

-  The ``rootfsdebugfiles`` class, which installs additional files found
   on the build host directly into the root filesystem.

The root filesystem is created from packages using one of the
``rootfs*.bbclass`` files as determined by the
:term:`PACKAGE_CLASSES` variable.

For information on how root filesystem images are created, see the
":ref:`overview-manual/concepts:image generation`"
section in the Yocto Project Overview and Concepts Manual.

.. _ref-classes-sanity:

``sanity.bbclass``
==================

The ``sanity`` class checks to see if prerequisite software is present
on the host system so that users can be notified of potential problems
that might affect their build. The class also performs basic user
configuration checks from the ``local.conf`` configuration file to
prevent common mistakes that cause build failures. Distribution policy
usually determines whether to include this class.

.. _ref-classes-scons:

``scons.bbclass``
=================

The ``scons`` class supports recipes that need to build software that
uses the SCons build system. You can use the
:term:`EXTRA_OESCONS` variable to specify
additional configuration options you want to pass SCons command line.

.. _ref-classes-sdl:

``sdl.bbclass``
===============

The ``sdl`` class supports recipes that need to build software that uses
the Simple DirectMedia Layer (SDL) library.

.. _ref-classes-setuptools3:

``setuptools3.bbclass``
=======================

The ``setuptools3`` class supports Python version 3.x extensions that
use build systems based on ``setuptools``. If your recipe uses these
build systems, the recipe needs to inherit the ``setuptools3`` class.

.. _ref-classes-sign_rpm:

``sign_rpm.bbclass``
====================

The ``sign_rpm`` class supports generating signed RPM packages.

.. _ref-classes-sip:

``sip.bbclass``
===============

The ``sip`` class supports recipes that build or package SIP-based
Python bindings.

.. _ref-classes-siteconfig:

``siteconfig.bbclass``
======================

The ``siteconfig`` class provides functionality for handling site
configuration. The class is used by the
:ref:`autotools <ref-classes-autotools>` class to accelerate the
:ref:`ref-tasks-configure` task.

.. _ref-classes-siteinfo:

``siteinfo.bbclass``
====================

The ``siteinfo`` class provides information about the targets that might
be needed by other classes or recipes.

As an example, consider Autotools, which can require tests that must
execute on the target hardware. Since this is not possible in general
when cross compiling, site information is used to provide cached test
results so these tests can be skipped over but still make the correct
values available. The ``meta/site directory`` contains test results
sorted into different categories such as architecture, endianness, and
the ``libc`` used. Site information provides a list of files containing
data relevant to the current build in the :term:`CONFIG_SITE` variable that
Autotools automatically picks up.

The class also provides variables like :term:`SITEINFO_ENDIANNESS` and
:term:`SITEINFO_BITS` that can be used elsewhere in the metadata.

.. _ref-classes-sstate:

``sstate.bbclass``
==================

The ``sstate`` class provides support for Shared State (sstate). By
default, the class is enabled through the
:term:`INHERIT_DISTRO` variable's default value.

For more information on sstate, see the
":ref:`overview-manual/concepts:shared state cache`"
section in the Yocto Project Overview and Concepts Manual.

.. _ref-classes-staging:

``staging.bbclass``
===================

The ``staging`` class installs files into individual recipe work
directories for sysroots. The class contains the following key tasks:

-  The :ref:`ref-tasks-populate_sysroot` task,
   which is responsible for handing the files that end up in the recipe
   sysroots.

-  The
   :ref:`ref-tasks-prepare_recipe_sysroot`
   task (a "partner" task to the ``populate_sysroot`` task), which
   installs the files into the individual recipe work directories (i.e.
   :term:`WORKDIR`).

The code in the ``staging`` class is complex and basically works in two
stages:

-  *Stage One:* The first stage addresses recipes that have files they
   want to share with other recipes that have dependencies on the
   originating recipe. Normally these dependencies are installed through
   the :ref:`ref-tasks-install` task into
   ``${``\ :term:`D`\ ``}``. The ``do_populate_sysroot`` task
   copies a subset of these files into ``${SYSROOT_DESTDIR}``. This
   subset of files is controlled by the
   :term:`SYSROOT_DIRS`,
   :term:`SYSROOT_DIRS_NATIVE`, and
   :term:`SYSROOT_DIRS_BLACKLIST`
   variables.

   .. note::

      Additionally, a recipe can customize the files further by
      declaring a processing function in the :term:`SYSROOT_PREPROCESS_FUNCS`
      variable.

   A shared state (sstate) object is built from these files and the
   files are placed into a subdirectory of
   :ref:`structure-build-tmp-sysroots-components`.
   The files are scanned for hardcoded paths to the original
   installation location. If the location is found in text files, the
   hardcoded locations are replaced by tokens and a list of the files
   needing such replacements is created. These adjustments are referred
   to as "FIXMEs". The list of files that are scanned for paths is
   controlled by the :term:`SSTATE_SCAN_FILES`
   variable.

-  *Stage Two:* The second stage addresses recipes that want to use
   something from another recipe and declare a dependency on that recipe
   through the :term:`DEPENDS` variable. The recipe will
   have a
   :ref:`ref-tasks-prepare_recipe_sysroot`
   task and when this task executes, it creates the ``recipe-sysroot``
   and ``recipe-sysroot-native`` in the recipe work directory (i.e.
   :term:`WORKDIR`). The OpenEmbedded build system
   creates hard links to copies of the relevant files from
   ``sysroots-components`` into the recipe work directory.

   .. note::

      If hard links are not possible, the build system uses actual
      copies.

   The build system then addresses any "FIXMEs" to paths as defined from
   the list created in the first stage.

   Finally, any files in ``${bindir}`` within the sysroot that have the
   prefix "``postinst-``" are executed.

   .. note::

      Although such sysroot post installation scripts are not
      recommended for general use, the files do allow some issues such
      as user creation and module indexes to be addressed.

   Because recipes can have other dependencies outside of :term:`DEPENDS`
   (e.g. ``do_unpack[depends] += "tar-native:do_populate_sysroot"``),
   the sysroot creation function ``extend_recipe_sysroot`` is also added
   as a pre-function for those tasks whose dependencies are not through
   :term:`DEPENDS` but operate similarly.

   When installing dependencies into the sysroot, the code traverses the
   dependency graph and processes dependencies in exactly the same way
   as the dependencies would or would not be when installed from sstate.
   This processing means, for example, a native tool would have its
   native dependencies added but a target library would not have its
   dependencies traversed or installed. The same sstate dependency code
   is used so that builds should be identical regardless of whether
   sstate was used or not. For a closer look, see the
   ``setscene_depvalid()`` function in the
   :ref:`sstate <ref-classes-sstate>` class.

   The build system is careful to maintain manifests of the files it
   installs so that any given dependency can be installed as needed. The
   sstate hash of the installed item is also stored so that if it
   changes, the build system can reinstall it.

.. _ref-classes-syslinux:

``syslinux.bbclass``
====================

The ``syslinux`` class provides syslinux-specific functions for building
bootable images.

The class supports the following variables:

-  :term:`INITRD`: Indicates list of filesystem images to
   concatenate and use as an initial RAM disk (initrd). This variable is
   optional.

-  :term:`ROOTFS`: Indicates a filesystem image to include
   as the root filesystem. This variable is optional.

-  :term:`AUTO_SYSLINUXMENU`: Enables creating
   an automatic menu when set to "1".

-  :term:`LABELS`: Lists targets for automatic
   configuration.

-  :term:`APPEND`: Lists append string overrides for each
   label.

-  :term:`SYSLINUX_OPTS`: Lists additional options
   to add to the syslinux file. Semicolon characters separate multiple
   options.

-  :term:`SYSLINUX_SPLASH`: Lists a background
   for the VGA boot menu when you are using the boot menu.

-  :term:`SYSLINUX_DEFAULT_CONSOLE`: Set
   to "console=ttyX" to change kernel boot default console.

-  :term:`SYSLINUX_SERIAL`: Sets an alternate
   serial port. Or, turns off serial when the variable is set with an
   empty string.

-  :term:`SYSLINUX_SERIAL_TTY`: Sets an
   alternate "console=tty..." kernel boot argument.

.. _ref-classes-systemd:

``systemd.bbclass``
===================

The ``systemd`` class provides support for recipes that install systemd
unit files.

The functionality for this class is disabled unless you have "systemd"
in :term:`DISTRO_FEATURES`.

Under this class, the recipe or Makefile (i.e. whatever the recipe is
calling during the :ref:`ref-tasks-install` task)
installs unit files into
``${``\ :term:`D`\ ``}${systemd_unitdir}/system``. If the unit
files being installed go into packages other than the main package, you
need to set :term:`SYSTEMD_PACKAGES` in your
recipe to identify the packages in which the files will be installed.

You should set :term:`SYSTEMD_SERVICE` to the
name of the service file. You should also use a package name override to
indicate the package to which the value applies. If the value applies to
the recipe's main package, use ``${``\ :term:`PN`\ ``}``. Here
is an example from the connman recipe::

   SYSTEMD_SERVICE:${PN} = "connman.service"

Services are set up to start on boot automatically
unless you have set
:term:`SYSTEMD_AUTO_ENABLE` to "disable".

For more information on ``systemd``, see the
":ref:`dev-manual/common-tasks:selecting an initialization manager`"
section in the Yocto Project Development Tasks Manual.

.. _ref-classes-systemd-boot:

``systemd-boot.bbclass``
========================

The ``systemd-boot`` class provides functions specific to the
systemd-boot bootloader for building bootable images. This is an
internal class and is not intended to be used directly.

.. note::

   The ``systemd-boot`` class is a result from merging the ``gummiboot`` class
   used in previous Yocto Project releases with the ``systemd`` project.

Set the :term:`EFI_PROVIDER` variable to
"systemd-boot" to use this class. Doing so creates a standalone EFI
bootloader that is not dependent on systemd.

For information on more variables used and supported in this class, see
the :term:`SYSTEMD_BOOT_CFG`,
:term:`SYSTEMD_BOOT_ENTRIES`, and
:term:`SYSTEMD_BOOT_TIMEOUT` variables.

You can also see the `Systemd-boot
documentation <https://www.freedesktop.org/wiki/Software/systemd/systemd-boot/>`__
for more information.

.. _ref-classes-terminal:

``terminal.bbclass``
====================

The ``terminal`` class provides support for starting a terminal session.
The :term:`OE_TERMINAL` variable controls which
terminal emulator is used for the session.

Other classes use the ``terminal`` class anywhere a separate terminal
session needs to be started. For example, the
:ref:`patch <ref-classes-patch>` class assuming
:term:`PATCHRESOLVE` is set to "user", the
:ref:`cml1 <ref-classes-cml1>` class, and the
:ref:`devshell <ref-classes-devshell>` class all use the ``terminal``
class.

.. _ref-classes-testimage*:

``testimage*.bbclass``
======================

The ``testimage*`` classes support running automated tests against
images using QEMU and on actual hardware. The classes handle loading the
tests and starting the image. To use the classes, you need to perform
steps to set up the environment.

.. note::

   Best practices include using :term:`IMAGE_CLASSES` rather than
   :term:`INHERIT` to inherit the ``testimage`` class for automated image
   testing.

The tests are commands that run on the target system over ``ssh``. Each
test is written in Python and makes use of the ``unittest`` module.

The ``testimage.bbclass`` runs tests on an image when called using the
following::

   $ bitbake -c testimage image

The ``testimage-auto`` class
runs tests on an image after the image is constructed (i.e.
:term:`TESTIMAGE_AUTO` must be set to "1").

For information on how to enable, run, and create new tests, see the
":ref:`dev-manual/common-tasks:performing automated runtime testing`"
section in the Yocto Project Development Tasks Manual.

.. _ref-classes-testsdk:

``testsdk.bbclass``
===================

This class supports running automated tests against software development
kits (SDKs). The ``testsdk`` class runs tests on an SDK when called
using the following::

   $ bitbake -c testsdk image

.. note::

   Best practices include using :term:`IMAGE_CLASSES` rather than
   :term:`INHERIT` to inherit the ``testsdk`` class for automated SDK
   testing.

.. _ref-classes-texinfo:

``texinfo.bbclass``
===================

This class should be inherited by recipes whose upstream packages invoke
the ``texinfo`` utilities at build-time. Native and cross recipes are
made to use the dummy scripts provided by ``texinfo-dummy-native``, for
improved performance. Target architecture recipes use the genuine
Texinfo utilities. By default, they use the Texinfo utilities on the
host system.

.. note::

   If you want to use the Texinfo recipe shipped with the build system,
   you can remove "texinfo-native" from :term:`ASSUME_PROVIDED` and makeinfo
   from :term:`SANITY_REQUIRED_UTILITIES`.

.. _ref-classes-toaster:

``toaster.bbclass``
===================

The ``toaster`` class collects information about packages and images and
sends them as events that the BitBake user interface can receive. The
class is enabled when the Toaster user interface is running.

This class is not intended to be used directly.

.. _ref-classes-toolchain-scripts:

``toolchain-scripts.bbclass``
=============================

The ``toolchain-scripts`` class provides the scripts used for setting up
the environment for installed SDKs.

.. _ref-classes-typecheck:

``typecheck.bbclass``
=====================

The ``typecheck`` class provides support for validating the values of
variables set at the configuration level against their defined types.
The OpenEmbedded build system allows you to define the type of a
variable using the "type" varflag. Here is an example::

   IMAGE_FEATURES[type] = "list"

.. _ref-classes-uboot-config:

``uboot-config.bbclass``
========================

The ``uboot-config`` class provides support for U-Boot configuration for
a machine. Specify the machine in your recipe as follows::

   UBOOT_CONFIG ??= <default>
   UBOOT_CONFIG[foo] = "config,images"

You can also specify the machine using this method::

   UBOOT_MACHINE = "config"

See the :term:`UBOOT_CONFIG` and :term:`UBOOT_MACHINE` variables for additional
information.

.. _ref-classes-uninative:

``uninative.bbclass``
=====================

Attempts to isolate the build system from the host distribution's C
library in order to make re-use of native shared state artifacts across
different host distributions practical. With this class enabled, a
tarball containing a pre-built C library is downloaded at the start of
the build. In the Poky reference distribution this is enabled by default
through ``meta/conf/distro/include/yocto-uninative.inc``. Other
distributions that do not derive from poky can also
"``require conf/distro/include/yocto-uninative.inc``" to use this.
Alternatively if you prefer, you can build the uninative-tarball recipe
yourself, publish the resulting tarball (e.g. via HTTP) and set
``UNINATIVE_URL`` and ``UNINATIVE_CHECKSUM`` appropriately. For an
example, see the ``meta/conf/distro/include/yocto-uninative.inc``.

The ``uninative`` class is also used unconditionally by the extensible
SDK. When building the extensible SDK, ``uninative-tarball`` is built
and the resulting tarball is included within the SDK.

.. _ref-classes-update-alternatives:

``update-alternatives.bbclass``
===============================

The ``update-alternatives`` class helps the alternatives system when
multiple sources provide the same command. This situation occurs when
several programs that have the same or similar function are installed
with the same name. For example, the ``ar`` command is available from
the ``busybox``, ``binutils`` and ``elfutils`` packages. The
``update-alternatives`` class handles renaming the binaries so that
multiple packages can be installed without conflicts. The ``ar`` command
still works regardless of which packages are installed or subsequently
removed. The class renames the conflicting binary in each package and
symlinks the highest priority binary during installation or removal of
packages.

To use this class, you need to define a number of variables:

-  :term:`ALTERNATIVE`

-  :term:`ALTERNATIVE_LINK_NAME`

-  :term:`ALTERNATIVE_TARGET`

-  :term:`ALTERNATIVE_PRIORITY`

These variables list alternative commands needed by a package, provide
pathnames for links, default links for targets, and so forth. For
details on how to use this class, see the comments in the
:yocto_git:`update-alternatives.bbclass </poky/tree/meta/classes/update-alternatives.bbclass>`
file.

.. note::

   You can use the ``update-alternatives`` command directly in your recipes.
   However, this class simplifies things in most cases.

.. _ref-classes-update-rc.d:

``update-rc.d.bbclass``
=======================

The ``update-rc.d`` class uses ``update-rc.d`` to safely install an
initialization script on behalf of the package. The OpenEmbedded build
system takes care of details such as making sure the script is stopped
before a package is removed and started when the package is installed.

Three variables control this class: :term:`INITSCRIPT_PACKAGES`,
:term:`INITSCRIPT_NAME` and :term:`INITSCRIPT_PARAMS`. See the variable links
for details.

.. _ref-classes-useradd:

``useradd*.bbclass``
====================

The ``useradd*`` classes support the addition of users or groups for
usage by the package on the target. For example, if you have packages
that contain system services that should be run under their own user or
group, you can use these classes to enable creation of the user or
group. The ``meta-skeleton/recipes-skeleton/useradd/useradd-example.bb``
recipe in the :term:`Source Directory` provides a simple
example that shows how to add three users and groups to two packages.
See the ``useradd-example.bb`` recipe for more information on how to use
these classes.

The ``useradd_base`` class provides basic functionality for user or
groups settings.

The ``useradd*`` classes support the
:term:`USERADD_PACKAGES`,
:term:`USERADD_PARAM`,
:term:`GROUPADD_PARAM`, and
:term:`GROUPMEMS_PARAM` variables.

The ``useradd-staticids`` class supports the addition of users or groups
that have static user identification (``uid``) and group identification
(``gid``) values.

The default behavior of the OpenEmbedded build system for assigning
``uid`` and ``gid`` values when packages add users and groups during
package install time is to add them dynamically. This works fine for
programs that do not care what the values of the resulting users and
groups become. In these cases, the order of the installation determines
the final ``uid`` and ``gid`` values. However, if non-deterministic
``uid`` and ``gid`` values are a problem, you can override the default,
dynamic application of these values by setting static values. When you
set static values, the OpenEmbedded build system looks in
:term:`BBPATH` for ``files/passwd`` and ``files/group``
files for the values.

To use static ``uid`` and ``gid`` values, you need to set some
variables. See the :term:`USERADDEXTENSION`,
:term:`USERADD_UID_TABLES`,
:term:`USERADD_GID_TABLES`, and
:term:`USERADD_ERROR_DYNAMIC` variables.
You can also see the :ref:`useradd <ref-classes-useradd>` class for
additional information.

.. note::

   You do not use the ``useradd-staticids`` class directly. You either enable
   or disable the class by setting the :term:`USERADDEXTENSION` variable. If you
   enable or disable the class in a configured system, :term:`TMPDIR` might
   contain incorrect ``uid`` and ``gid`` values. Deleting the :term:`TMPDIR`
   directory will correct this condition.

.. _ref-classes-utility-tasks:

``utility-tasks.bbclass``
=========================

The ``utility-tasks`` class provides support for various "utility" type
tasks that are applicable to all recipes, such as
:ref:`ref-tasks-clean` and
:ref:`ref-tasks-listtasks`.

This class is enabled by default because it is inherited by the
:ref:`base <ref-classes-base>` class.

.. _ref-classes-utils:

``utils.bbclass``
=================

The ``utils`` class provides some useful Python functions that are
typically used in inline Python expressions (e.g. ``${@...}``). One
example use is for ``bb.utils.contains()``.

This class is enabled by default because it is inherited by the
:ref:`base <ref-classes-base>` class.

.. _ref-classes-vala:

``vala.bbclass``
================

The ``vala`` class supports recipes that need to build software written
using the Vala programming language.

.. _ref-classes-waf:

``waf.bbclass``
===============

The ``waf`` class supports recipes that need to build software that uses
the Waf build system. You can use the
:term:`EXTRA_OECONF` or
:term:`PACKAGECONFIG_CONFARGS` variables
to specify additional configuration options to be passed on the Waf
command line.
