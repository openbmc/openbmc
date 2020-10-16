.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*****************************
QA Error and Warning Messages
*****************************

.. _qa-introduction:

Introduction
============

When building a recipe, the OpenEmbedded build system performs various
QA checks on the output to ensure that common issues are detected and
reported. Sometimes when you create a new recipe to build new software,
it will build with no problems. When this is not the case, or when you
have QA issues building any software, it could take a little time to
resolve them.

While it is tempting to ignore a QA message or even to disable QA
checks, it is best to try and resolve any reported QA issues. This
chapter provides a list of the QA messages and brief explanations of the
issues you could encounter so that you can properly resolve problems.

The next section provides a list of all QA error and warning messages
based on a default configuration. Each entry provides the message or
error form along with an explanation.

.. note::

   -  At the end of each message, the name of the associated QA test (as
      listed in the ":ref:`insane.bbclass <ref-classes-insane>`"
      section) appears within square brackets.

   -  As mentioned, this list of error and warning messages is for QA
      checks only. The list does not cover all possible build errors or
      warnings you could encounter.

   -  Because some QA checks are disabled by default, this list does not
      include all possible QA check errors and warnings.

.. _qa-errors-and-warnings:

Errors and Warnings
===================

-  ``<packagename>: <path> is using libexec please relocate to <libexecdir> [libexec]``

   The specified package contains files in ``/usr/libexec`` when the
   distro configuration uses a different path for ``<libexecdir>`` By
   default, ``<libexecdir>`` is ``$prefix/libexec``. However, this
   default can be changed (e.g. ``${libdir}``).

    

-  ``package <packagename> contains bad RPATH <rpath> in file <file> [rpaths]``

   The specified binary produced by the recipe contains dynamic library
   load paths (rpaths) that contain build system paths such as
   :term:`TMPDIR`, which are incorrect for the target and
   could potentially be a security issue. Check for bad ``-rpath``
   options being passed to the linker in your
   :ref:`ref-tasks-compile` log. Depending on the build
   system used by the software being built, there might be a configure
   option to disable rpath usage completely within the build of the
   software.

    

-  ``<packagename>: <file> contains probably-redundant RPATH <rpath> [useless-rpaths]``

   The specified binary produced by the recipe contains dynamic library
   load paths (rpaths) that on a standard system are searched by default
   by the linker (e.g. ``/lib`` and ``/usr/lib``). While these paths
   will not cause any breakage, they do waste space and are unnecessary.
   Depending on the build system used by the software being built, there
   might be a configure option to disable rpath usage completely within
   the build of the software.

    

-  ``<packagename> requires <files>, but no providers in its RDEPENDS [file-rdeps]``

   A file-level dependency has been identified from the specified
   package on the specified files, but there is no explicit
   corresponding entry in :term:`RDEPENDS`. If
   particular files are required at runtime then ``RDEPENDS`` should be
   declared in the recipe to ensure the packages providing them are
   built.

    

-  ``<packagename1> rdepends on <packagename2>, but it isn't a build dependency? [build-deps]``

   A runtime dependency exists between the two specified packages, but
   there is nothing explicit within the recipe to enable the
   OpenEmbedded build system to ensure that dependency is satisfied.
   This condition is usually triggered by an
   :term:`RDEPENDS` value being added at the packaging
   stage rather than up front, which is usually automatic based on the
   contents of the package. In most cases, you should change the recipe
   to add an explicit ``RDEPENDS`` for the dependency.

    

-  ``non -dev/-dbg/nativesdk- package contains symlink .so: <packagename> path '<path>' [dev-so]``

   Symlink ``.so`` files are for development only, and should therefore
   go into the ``-dev`` package. This situation might occur if you add
   ``*.so*`` rather than ``*.so.*`` to a non-dev package. Change
   :term:`FILES` (and possibly
   :term:`PACKAGES`) such that the specified ``.so``
   file goes into an appropriate ``-dev`` package.

    

-  ``non -staticdev package contains static .a library: <packagename> path '<path>' [staticdev]``

   Static ``.a`` library files should go into a ``-staticdev`` package.
   Change :term:`FILES` (and possibly
   :term:`PACKAGES`) such that the specified ``.a`` file
   goes into an appropriate ``-staticdev`` package.

    

-  ``<packagename>: found library in wrong location [libdir]``

   The specified file may have been installed into an incorrect
   (possibly hardcoded) installation path. For example, this test will
   catch recipes that install ``/lib/bar.so`` when ``${base_libdir}`` is
   "lib32". Another example is when recipes install
   ``/usr/lib64/foo.so`` when ``${libdir}`` is "/usr/lib". False
   positives occasionally exist. For these cases add "libdir" to
   :term:`INSANE_SKIP` for the package.

    

-  ``non debug package contains .debug directory: <packagename> path <path> [debug-files]``

   The specified package contains a ``.debug`` directory, which should
   not appear in anything but the ``-dbg`` package. This situation might
   occur if you add a path which contains a ``.debug`` directory and do
   not explicitly add the ``.debug`` directory to the ``-dbg`` package.
   If this is the case, add the ``.debug`` directory explicitly to
   ``FILES_${PN}-dbg``. See :term:`FILES` for additional
   information on ``FILES``.

    

-  ``Architecture did not match (<machine_arch> to <file_arch>) on <file> [arch]``

   By default, the OpenEmbedded build system checks the Executable and
   Linkable Format (ELF) type, bit size, and endianness of any binaries
   to ensure they match the target architecture. This test fails if any
   binaries do not match the type since there would be an
   incompatibility. The test could indicate that the wrong compiler or
   compiler options have been used. Sometimes software, like
   bootloaders, might need to bypass this check. If the file you receive
   the error for is firmware that is not intended to be executed within
   the target operating system or is intended to run on a separate
   processor within the device, you can add "arch" to
   :term:`INSANE_SKIP` for the package. Another
   option is to check the :ref:`ref-tasks-compile` log
   and verify that the compiler options being used are correct.

    

-  ``Bit size did not match (<machine_bits> to <file_bits>) <recipe> on <file> [arch]``

   By default, the OpenEmbedded build system checks the Executable and
   Linkable Format (ELF) type, bit size, and endianness of any binaries
   to ensure they match the target architecture. This test fails if any
   binaries do not match the type since there would be an
   incompatibility. The test could indicate that the wrong compiler or
   compiler options have been used. Sometimes software, like
   bootloaders, might need to bypass this check. If the file you receive
   the error for is firmware that is not intended to be executed within
   the target operating system or is intended to run on a separate
   processor within the device, you can add "arch" to
   :term:`INSANE_SKIP` for the package. Another
   option is to check the :ref:`ref-tasks-compile` log
   and verify that the compiler options being used are correct.

    

-  ``Endianness did not match (<machine_endianness> to <file_endianness>) on <file> [arch]``

   By default, the OpenEmbedded build system checks the Executable and
   Linkable Format (ELF) type, bit size, and endianness of any binaries
   to ensure they match the target architecture. This test fails if any
   binaries do not match the type since there would be an
   incompatibility. The test could indicate that the wrong compiler or
   compiler options have been used. Sometimes software, like
   bootloaders, might need to bypass this check. If the file you receive
   the error for is firmware that is not intended to be executed within
   the target operating system or is intended to run on a separate
   processor within the device, you can add "arch" to
   :term:`INSANE_SKIP` for the package. Another
   option is to check the :ref:`ref-tasks-compile` log
   and verify that the compiler options being used are correct.

    

-  ``ELF binary '<file>' has relocations in .text [textrel]``

   The specified ELF binary contains relocations in its ``.text``
   sections. This situation can result in a performance impact at
   runtime.

   Typically, the way to solve this performance issue is to add "-fPIC"
   or "-fpic" to the compiler command-line options. For example, given
   software that reads :term:`CFLAGS` when you build it,
   you could add the following to your recipe:
   ::

      CFLAGS_append = " -fPIC "

   For more information on text relocations at runtime, see
   http://www.akkadia.org/drepper/textrelocs.html.

    

-  ``No GNU_HASH in the elf binary: '<file>' [ldflags]``

   This indicates that binaries produced when building the recipe have
   not been linked with the :term:`LDFLAGS` options
   provided by the build system. Check to be sure that the ``LDFLAGS``
   variable is being passed to the linker command. A common workaround
   for this situation is to pass in ``LDFLAGS`` using
   :term:`TARGET_CC_ARCH` within the recipe as
   follows:
   ::

      TARGET_CC_ARCH += "${LDFLAGS}"

    

-  ``Package <packagename> contains Xorg driver (<driver>) but no xorg-abi- dependencies [xorg-driver-abi]``

   The specified package contains an Xorg driver, but does not have a
   corresponding ABI package dependency. The xserver-xorg recipe
   provides driver ABI names. All drivers should depend on the ABI
   versions that they have been built against. Driver recipes that
   include ``xorg-driver-input.inc`` or ``xorg-driver-video.inc`` will
   automatically get these versions. Consequently, you should only need
   to explicitly add dependencies to binary driver recipes.

    

-  ``The /usr/share/info/dir file is not meant to be shipped in a particular package. [infodir]``

   The ``/usr/share/info/dir`` should not be packaged. Add the following
   line to your :ref:`ref-tasks-install` task or to your
   ``do_install_append`` within the recipe as follows:
   ::

      rm ${D}${infodir}/dir
   

-  ``Symlink <path> in <packagename> points to TMPDIR [symlink-to-sysroot]``

   The specified symlink points into :term:`TMPDIR` on the
   host. Such symlinks will work on the host. However, they are clearly
   invalid when running on the target. You should either correct the
   symlink to use a relative path or remove the symlink.

    

-  ``<file> failed sanity test (workdir) in path <path> [la]``

   The specified ``.la`` file contains :term:`TMPDIR`
   paths. Any ``.la`` file containing these paths is incorrect since
   ``libtool`` adds the correct sysroot prefix when using the files
   automatically itself.

    

-  ``<file> failed sanity test (tmpdir) in path <path> [pkgconfig]``

   The specified ``.pc`` file contains
   :term:`TMPDIR`\ ``/``\ :term:`WORKDIR`
   paths. Any ``.pc`` file containing these paths is incorrect since
   ``pkg-config`` itself adds the correct sysroot prefix when the files
   are accessed.

    

-  ``<packagename> rdepends on <debug_packagename> [debug-deps]``

   A dependency exists between the specified non-dbg package (i.e. a
   package whose name does not end in ``-dbg``) and a package that is a
   ``dbg`` package. The ``dbg`` packages contain debug symbols and are
   brought in using several different methods:

   -  Using the ``dbg-pkgs``
      :term:`IMAGE_FEATURES` value.

   -  Using :term:`IMAGE_INSTALL`.

   -  As a dependency of another ``dbg`` package that was brought in
      using one of the above methods.

   The dependency might have been automatically added because the
   ``dbg`` package erroneously contains files that it should not contain
   (e.g. a non-symlink ``.so`` file) or it might have been added
   manually (e.g. by adding to :term:`RDEPENDS`).

    

-  ``<packagename> rdepends on <dev_packagename> [dev-deps]``

   A dependency exists between the specified non-dev package (a package
   whose name does not end in ``-dev``) and a package that is a ``dev``
   package. The ``dev`` packages contain development headers and are
   usually brought in using several different methods:

   -  Using the ``dev-pkgs``
      :term:`IMAGE_FEATURES` value.

   -  Using :term:`IMAGE_INSTALL`.

   -  As a dependency of another ``dev`` package that was brought in
      using one of the above methods.

   The dependency might have been automatically added (because the
   ``dev`` package erroneously contains files that it should not have
   (e.g. a non-symlink ``.so`` file) or it might have been added
   manually (e.g. by adding to :term:`RDEPENDS`).

    

-  ``<var>_<packagename> is invalid: <comparison> (<value>)   only comparisons <, =, >, <=, and >= are allowed [dep-cmp]``

   If you are adding a versioned dependency relationship to one of the
   dependency variables (:term:`RDEPENDS`,
   :term:`RRECOMMENDS`,
   :term:`RSUGGESTS`,
   :term:`RPROVIDES`,
   :term:`RREPLACES`, or
   :term:`RCONFLICTS`), you must only use the named
   comparison operators. Change the versioned dependency values you are
   adding to match those listed in the message.

    

-  ``<recipename>: The compile log indicates that host include and/or library paths were used. Please check the log '<logfile>' for more information. [compile-host-path]``

   The log for the :ref:`ref-tasks-compile` task
   indicates that paths on the host were searched for files, which is
   not appropriate when cross-compiling. Look for "is unsafe for
   cross-compilation" or "CROSS COMPILE Badness" in the specified log
   file.

    

-  ``<recipename>: The install log indicates that host include and/or library paths were used. Please check the log '<logfile>' for more information. [install-host-path]``

   The log for the :ref:`ref-tasks-install` task
   indicates that paths on the host were searched for files, which is
   not appropriate when cross-compiling. Look for "is unsafe for
   cross-compilation" or "CROSS COMPILE Badness" in the specified log
   file.

    

-  ``This autoconf log indicates errors, it looked at host include and/or library paths while determining system capabilities. Rerun configure task after fixing this. The path was '<path>'``

   The log for the :ref:`ref-tasks-configure` task
   indicates that paths on the host were searched for files, which is
   not appropriate when cross-compiling. Look for "is unsafe for
   cross-compilation" or "CROSS COMPILE Badness" in the specified log
   file.

    

-  ``<packagename> doesn't match the [a-z0-9.+-]+ regex [pkgname]``

   The convention within the OpenEmbedded build system (sometimes
   enforced by the package manager itself) is to require that package
   names are all lower case and to allow a restricted set of characters.
   If your recipe name does not match this, or you add packages to
   :term:`PACKAGES` that do not conform to the
   convention, then you will receive this error. Rename your recipe. Or,
   if you have added a non-conforming package name to ``PACKAGES``,
   change the package name appropriately.

    

-  ``<recipe>: configure was passed unrecognized options: <options> [unknown-configure-option]``

   The configure script is reporting that the specified options are
   unrecognized. This situation could be because the options were
   previously valid but have been removed from the configure script. Or,
   there was a mistake when the options were added and there is another
   option that should be used instead. If you are unsure, consult the
   upstream build documentation, the ``./configure --help`` output, and
   the upstream change log or release notes. Once you have worked out
   what the appropriate change is, you can update
   :term:`EXTRA_OECONF`,
   :term:`PACKAGECONFIG_CONFARGS`, or the
   individual :term:`PACKAGECONFIG` option values
   accordingly.

    

-  ``Recipe <recipefile> has PN of "<recipename>" which is in OVERRIDES, this can result in unexpected behavior. [pn-overrides]``

   The specified recipe has a name (:term:`PN`) value that
   appears in :term:`OVERRIDES`. If a recipe is named
   such that its ``PN`` value matches something already in ``OVERRIDES``
   (e.g. ``PN`` happens to be the same as :term:`MACHINE`
   or :term:`DISTRO`), it can have unexpected
   consequences. For example, assignments such as
   ``FILES_${PN} = "xyz"`` effectively turn into ``FILES = "xyz"``.
   Rename your recipe (or if ``PN`` is being set explicitly, change the
   ``PN`` value) so that the conflict does not occur. See
   :term:`FILES` for additional information.

    

-  ``<recipefile>: Variable <variable> is set as not being package specific, please fix this. [pkgvarcheck]``

   Certain variables (:term:`RDEPENDS`,
   :term:`RRECOMMENDS`,
   :term:`RSUGGESTS`,
   :term:`RCONFLICTS`,
   :term:`RPROVIDES`,
   :term:`RREPLACES`, :term:`FILES`,
   ``pkg_preinst``, ``pkg_postinst``, ``pkg_prerm``, ``pkg_postrm``, and
   :term:`ALLOW_EMPTY`) should always be set specific
   to a package (i.e. they should be set with a package name override
   such as ``RDEPENDS_${PN} = "value"`` rather than
   ``RDEPENDS = "value"``). If you receive this error, correct any
   assignments to these variables within your recipe.

    

-  ``File '<file>' from <recipename> was already stripped, this will prevent future debugging! [already-stripped]``

   Produced binaries have already been stripped prior to the build
   system extracting debug symbols. It is common for upstream software
   projects to default to stripping debug symbols for output binaries.
   In order for debugging to work on the target using ``-dbg`` packages,
   this stripping must be disabled.

   Depending on the build system used by the software being built,
   disabling this stripping could be as easy as specifying an additional
   configure option. If not, disabling stripping might involve patching
   the build scripts. In the latter case, look for references to "strip"
   or "STRIP", or the "-s" or "-S" command-line options being specified
   on the linker command line (possibly through the compiler command
   line if preceded with "-Wl,").

   .. note::

      Disabling stripping here does not mean that the final packaged
      binaries will be unstripped. Once the OpenEmbedded build system
      splits out debug symbols to the
      -dbg
      package, it will then strip the symbols from the binaries.

    

-  ``<packagename> is listed in PACKAGES multiple times, this leads to packaging errors. [packages-list]``

   Package names must appear only once in the
   :term:`PACKAGES` variable. You might receive this
   error if you are attempting to add a package to ``PACKAGES`` that is
   already in the variable's value.

    

-  ``FILES variable for package <packagename> contains '//' which is invalid. Attempting to fix this but you should correct the metadata. [files-invalid]``

   The string "//" is invalid in a Unix path. Correct all occurrences
   where this string appears in a :term:`FILES` variable so
   that there is only a single "/".

    

-  ``<recipename>: Files/directories were installed but not shipped in any package [installed-vs-shipped]``

   Files have been installed within the
   :ref:`ref-tasks-install` task but have not been
   included in any package by way of the :term:`FILES`
   variable. Files that do not appear in any package cannot be present
   in an image later on in the build process. You need to do one of the
   following:

   -  Add the files to ``FILES`` for the package you want them to appear
      in (e.g. ``FILES_${``\ :term:`PN`\ ``}`` for the main
      package).

   -  Delete the files at the end of the ``do_install`` task if the
      files are not needed in any package.

    

-  ``<oldpackage>-<oldpkgversion> was registered as shlib provider for <library>, changing it to <newpackage>-<newpkgversion> because it was built later``

   This message means that both ``<oldpackage>`` and ``<newpackage>``
   provide the specified shared library. You can expect this message
   when a recipe has been renamed. However, if that is not the case, the
   message might indicate that a private version of a library is being
   erroneously picked up as the provider for a common library. If that
   is the case, you should add the library's ``.so`` file name to
   :term:`PRIVATE_LIBS` in the recipe that provides
   the private version of the library.

-  ``LICENSE_<packagename> includes licenses (<licenses>) that are not listed in LICENSE [unlisted-pkg-lics]``

   The :term:`LICENSE` of the recipe should be a superset
   of all the licenses of all packages produced by this recipe. In other
   words, any license in ``LICENSE_*`` should also appear in
   :term:`LICENSE`.

    

Configuring and Disabling QA Checks
===================================

You can configure the QA checks globally so that specific check failures
either raise a warning or an error message, using the
:term:`WARN_QA` and :term:`ERROR_QA`
variables, respectively. You can also disable checks within a particular
recipe using :term:`INSANE_SKIP`. For information on
how to work with the QA checks, see the
":ref:`insane.bbclass <ref-classes-insane>`" section.

.. note::

   Please keep in mind that the QA checks exist in order to detect real
   or potential problems in the packaged output. So exercise caution
   when disabling these checks.
