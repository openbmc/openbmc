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
      listed in the ":ref:`ref-classes-insane`"
      section) appears within square brackets.

   -  As mentioned, this list of error and warning messages is for QA
      checks only. The list does not cover all possible build errors or
      warnings you could encounter.

   -  Because some QA checks are disabled by default, this list does not
      include all possible QA check errors and warnings.

.. _qa-errors-and-warnings:

Errors and Warnings
===================

.. _qa-check-libexec:

-  ``<packagename>: <path> is using libexec please relocate to <libexecdir> [libexec]``

   The specified package contains files in ``/usr/libexec`` when the
   distro configuration uses a different path for ``<libexecdir>`` By
   default, ``<libexecdir>`` is ``$prefix/libexec``. However, this
   default can be changed (e.g. ``${libdir}``).

    
.. _qa-check-rpaths:

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

    
.. _qa-check-useless-rpaths:

-  ``<packagename>: <file> contains probably-redundant RPATH <rpath> [useless-rpaths]``

   The specified binary produced by the recipe contains dynamic library
   load paths (rpaths) that on a standard system are searched by default
   by the linker (e.g. ``/lib`` and ``/usr/lib``). While these paths
   will not cause any breakage, they do waste space and are unnecessary.
   Depending on the build system used by the software being built, there
   might be a configure option to disable rpath usage completely within
   the build of the software.

    
.. _qa-check-file-rdeps:

-  ``<packagename> requires <files>, but no providers in its RDEPENDS [file-rdeps]``

   A file-level dependency has been identified from the specified
   package on the specified files, but there is no explicit
   corresponding entry in :term:`RDEPENDS`. If
   particular files are required at runtime then :term:`RDEPENDS` should be
   declared in the recipe to ensure the packages providing them are
   built.

    
.. _qa-check-build-deps:

-  ``<packagename1> rdepends on <packagename2>, but it isn't a build dependency? [build-deps]``

   There is a runtime dependency between the two specified packages, but
   there is nothing explicit within the recipe to enable the
   OpenEmbedded build system to ensure that dependency is satisfied.
   This condition is usually triggered by an
   :term:`RDEPENDS` value being added at the packaging
   stage rather than up front, which is usually automatic based on the
   contents of the package. In most cases, you should change the recipe
   to add an explicit :term:`RDEPENDS` for the dependency.

    
.. _qa-check-dev-so:

-  ``non -dev/-dbg/nativesdk- package contains symlink .so: <packagename> path '<path>' [dev-so]``

   Symlink ``.so`` files are for development only, and should therefore
   go into the ``-dev`` package. This situation might occur if you add
   ``*.so*`` rather than ``*.so.*`` to a non-dev package. Change
   :term:`FILES` (and possibly
   :term:`PACKAGES`) such that the specified ``.so``
   file goes into an appropriate ``-dev`` package.

    
.. _qa-check-staticdev:

-  ``non -staticdev package contains static .a library: <packagename> path '<path>' [staticdev]``

   Static ``.a`` library files should go into a ``-staticdev`` package.
   Change :term:`FILES` (and possibly
   :term:`PACKAGES`) such that the specified ``.a`` file
   goes into an appropriate ``-staticdev`` package.

    
.. _qa-check-libdir:

-  ``<packagename>: found library in wrong location [libdir]``

   The specified file may have been installed into an incorrect
   (possibly hardcoded) installation path. For example, this test will
   catch recipes that install ``/lib/bar.so`` when ``${base_libdir}`` is
   "lib32". Another example is when recipes install
   ``/usr/lib64/foo.so`` when ``${libdir}`` is "/usr/lib". False
   positives occasionally exist. For these cases add "libdir" to
   :term:`INSANE_SKIP` for the package.

    
.. _qa-check-debug-files:

-  ``non debug package contains .debug directory: <packagename> path <path> [debug-files]``

   The specified package contains a ``.debug`` directory, which should
   not appear in anything but the ``-dbg`` package. This situation might
   occur if you add a path which contains a ``.debug`` directory and do
   not explicitly add the ``.debug`` directory to the ``-dbg`` package.
   If this is the case, add the ``.debug`` directory explicitly to
   ``FILES:${PN}-dbg``. See :term:`FILES` for additional
   information on :term:`FILES`.

.. _qa-check-empty-dirs:

-  ``<packagename> installs files in <path>, but it is expected to be empty [empty-dirs]``

   The specified package is installing files into a directory that is
   normally expected to be empty (such as ``/tmp``). These files may
   be more appropriately installed to a different location, or
   perhaps alternatively not installed at all, usually by updating the
   :ref:`ref-tasks-install` task/function.

.. _qa-check-arch:

-  ``Architecture did not match (<file_arch>, expected <machine_arch>) in <file> [arch]``

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

    

-  ``Bit size did not match (<file_bits>, expected <machine_bits>) in <file> [arch]``

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

    

-  ``Endianness did not match (<file_endianness>, expected <machine_endianness>) in <file> [arch]``

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

    
.. _qa-check-textrel:

-  ``ELF binary '<file>' has relocations in .text [textrel]``

   The specified ELF binary contains relocations in its ``.text``
   sections. This situation can result in a performance impact at
   runtime.

   Typically, the way to solve this performance issue is to add "-fPIC"
   or "-fpic" to the compiler command-line options. For example, given
   software that reads :term:`CFLAGS` when you build it,
   you could add the following to your recipe::

      CFLAGS:append = " -fPIC "

   For more information on text relocations at runtime, see
   https://www.akkadia.org/drepper/textrelocs.html.

    
.. _qa-check-ldflags:

-  ``File '<file>' in package '<package>' doesn't have GNU_HASH (didn't pass LDFLAGS?) [ldflags]``

   This indicates that binaries produced when building the recipe have
   not been linked with the :term:`LDFLAGS` options
   provided by the build system. Check to be sure that the :term:`LDFLAGS`
   variable is being passed to the linker command. A common workaround
   for this situation is to pass in :term:`LDFLAGS` using
   :term:`TARGET_CC_ARCH` within the recipe as
   follows::

      TARGET_CC_ARCH += "${LDFLAGS}"

    
.. _qa-check-xorg-driver-abi:

-  ``Package <packagename> contains Xorg driver (<driver>) but no xorg-abi- dependencies [xorg-driver-abi]``

   The specified package contains an Xorg driver, but does not have a
   corresponding ABI package dependency. The xserver-xorg recipe
   provides driver ABI names. All drivers should depend on the ABI
   versions that they have been built against. Driver recipes that
   include ``xorg-driver-input.inc`` or ``xorg-driver-video.inc`` will
   automatically get these versions. Consequently, you should only need
   to explicitly add dependencies to binary driver recipes.

    
.. _qa-check-infodir:

-  ``The /usr/share/info/dir file is not meant to be shipped in a particular package. [infodir]``

   The ``/usr/share/info/dir`` should not be packaged. Add the following
   line to your :ref:`ref-tasks-install` task or to your
   ``do_install:append`` within the recipe as follows::

      rm ${D}${infodir}/dir
   

.. _qa-check-symlink-to-sysroot:

-  ``Symlink <path> in <packagename> points to TMPDIR [symlink-to-sysroot]``

   The specified symlink points into :term:`TMPDIR` on the
   host. Such symlinks will work on the host. However, they are clearly
   invalid when running on the target. You should either correct the
   symlink to use a relative path or remove the symlink.

    
.. _qa-check-la:

-  ``<file> failed sanity test (workdir) in path <path> [la]``

   The specified ``.la`` file contains :term:`TMPDIR`
   paths. Any ``.la`` file containing these paths is incorrect since
   ``libtool`` adds the correct sysroot prefix when using the files
   automatically itself.

    
.. _qa-check-pkgconfig:

-  ``<file> failed sanity test (tmpdir) in path <path> [pkgconfig]``

   The specified ``.pc`` file contains
   :term:`TMPDIR`\ ``/``\ :term:`WORKDIR`
   paths. Any ``.pc`` file containing these paths is incorrect since
   ``pkg-config`` itself adds the correct sysroot prefix when the files
   are accessed.

    
.. _qa-check-debug-deps:

-  ``<packagename> rdepends on <debug_packagename> [debug-deps]``

   There is a dependency between the specified non-dbg package (i.e. a
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

    
.. _qa-check-dev-deps:

-  ``<packagename> rdepends on <dev_packagename> [dev-deps]``

   There is a dependency between the specified non-dev package (a package
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

    
.. _qa-check-dep-cmp:

-  ``<var>:<packagename> is invalid: <comparison> (<value>)   only comparisons <, =, >, <=, and >= are allowed [dep-cmp]``

   If you are adding a versioned dependency relationship to one of the
   dependency variables (:term:`RDEPENDS`,
   :term:`RRECOMMENDS`,
   :term:`RSUGGESTS`,
   :term:`RPROVIDES`,
   :term:`RREPLACES`, or
   :term:`RCONFLICTS`), you must only use the named
   comparison operators. Change the versioned dependency values you are
   adding to match those listed in the message.

    
.. _qa-check-compile-host-path:

-  ``<recipename>: The compile log indicates that host include and/or library paths were used. Please check the log '<logfile>' for more information. [compile-host-path]``

   The log for the :ref:`ref-tasks-compile` task
   indicates that paths on the host were searched for files, which is
   not appropriate when cross-compiling. Look for "is unsafe for
   cross-compilation" or "CROSS COMPILE Badness" in the specified log
   file.

    
.. _qa-check-install-host-path:

-  ``<recipename>: The install log indicates that host include and/or library paths were used. Please check the log '<logfile>' for more information. [install-host-path]``

   The log for the :ref:`ref-tasks-install` task
   indicates that paths on the host were searched for files, which is
   not appropriate when cross-compiling. Look for "is unsafe for
   cross-compilation" or "CROSS COMPILE Badness" in the specified log
   file.

    
.. _qa-check-configure-unsafe:

-  ``This autoconf log indicates errors, it looked at host include and/or library paths while determining system capabilities. Rerun configure task after fixing this. [configure-unsafe]``

   The log for the :ref:`ref-tasks-configure` task
   indicates that paths on the host were searched for files, which is
   not appropriate when cross-compiling. Look for "is unsafe for
   cross-compilation" or "CROSS COMPILE Badness" in the specified log
   file.

    
.. _qa-check-pkgname:

-  ``<packagename> doesn't match the [a-z0-9.+-]+ regex [pkgname]``

   The convention within the OpenEmbedded build system (sometimes
   enforced by the package manager itself) is to require that package
   names are all lower case and to allow a restricted set of characters.
   If your recipe name does not match this, or you add packages to
   :term:`PACKAGES` that do not conform to the
   convention, then you will receive this error. Rename your recipe. Or,
   if you have added a non-conforming package name to :term:`PACKAGES`,
   change the package name appropriately.

    
.. _qa-check-unknown-configure-option:

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

    
.. _qa-check-pn-overrides:

-  ``Recipe <recipefile> has PN of "<recipename>" which is in OVERRIDES, this can result in unexpected behavior. [pn-overrides]``

   The specified recipe has a name (:term:`PN`) value that
   appears in :term:`OVERRIDES`. If a recipe is named
   such that its :term:`PN` value matches something already in :term:`OVERRIDES`
   (e.g. :term:`PN` happens to be the same as :term:`MACHINE`
   or :term:`DISTRO`), it can have unexpected
   consequences. For example, assignments such as
   ``FILES:${PN} = "xyz"`` effectively turn into ``FILES = "xyz"``.
   Rename your recipe (or if :term:`PN` is being set explicitly, change the
   :term:`PN` value) so that the conflict does not occur. See
   :term:`FILES` for additional information.

    
.. _qa-check-pkgvarcheck:

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
   such as ``RDEPENDS:${PN} = "value"`` rather than
   ``RDEPENDS = "value"``). If you receive this error, correct any
   assignments to these variables within your recipe.


- ``recipe uses DEPENDS:${PN}, should use DEPENDS [pkgvarcheck]``

   This check looks for instances of setting ``DEPENDS:${PN}``
   which is erroneous (:term:`DEPENDS` is a recipe-wide variable and thus
   it is not correct to specify it for a particular package, nor will such
   an assignment actually work.) Set :term:`DEPENDS` instead.


.. _qa-check-already-stripped:

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
      splits out debug symbols to the ``-dbg`` package, it will then
      strip the symbols from the binaries.

    
.. _qa-check-packages-list:

-  ``<packagename> is listed in PACKAGES multiple times, this leads to packaging errors. [packages-list]``

   Package names must appear only once in the
   :term:`PACKAGES` variable. You might receive this
   error if you are attempting to add a package to :term:`PACKAGES` that is
   already in the variable's value.

    
.. _qa-check-files-invalid:

-  ``FILES variable for package <packagename> contains '//' which is invalid. Attempting to fix this but you should correct the metadata. [files-invalid]``

   The string "//" is invalid in a Unix path. Correct all occurrences
   where this string appears in a :term:`FILES` variable so
   that there is only a single "/".

    
.. _qa-check-installed-vs-shipped:

-  ``<recipename>: Files/directories were installed but not shipped in any package [installed-vs-shipped]``

   Files have been installed within the
   :ref:`ref-tasks-install` task but have not been
   included in any package by way of the :term:`FILES`
   variable. Files that do not appear in any package cannot be present
   in an image later on in the build process. You need to do one of the
   following:

   -  Add the files to :term:`FILES` for the package you want them to appear
      in (e.g. ``FILES:${``\ :term:`PN`\ ``}`` for the main
      package).

   -  Delete the files at the end of the :ref:`ref-tasks-install` task if the
      files are not needed in any package.

    

-  ``<oldpackage>-<oldpkgversion> was registered as shlib provider for <library>, changing it to <newpackage>-<newpkgversion> because it was built later``

   This message means that both ``<oldpackage>`` and ``<newpackage>``
   provide the specified shared library. You can expect this message
   when a recipe has been renamed. However, if that is not the case, the
   message might indicate that a private version of a library is being
   erroneously picked up as the provider for a common library. If that
   is the case, you should add the library's ``.so`` filename to
   :term:`PRIVATE_LIBS` in the recipe that provides
   the private version of the library.


.. _qa-check-unlisted-pkg-lics:

-  ``LICENSE:<packagename> includes licenses (<licenses>) that are not listed in LICENSE [unlisted-pkg-lics]``

   The :term:`LICENSE` of the recipe should be a superset
   of all the licenses of all packages produced by this recipe. In other
   words, any license in ``LICENSE:*`` should also appear in
   :term:`LICENSE`.


.. _qa-check-configure-gettext:

-  ``AM_GNU_GETTEXT used but no inherit gettext [configure-gettext]``

    If a recipe is building something that uses automake and the automake
    files contain an ``AM_GNU_GETTEXT`` directive then this check will fail
    if there is no ``inherit gettext`` statement in the recipe to ensure
    that gettext is available during the build. Add ``inherit gettext`` to
    remove the warning.


.. _qa-check-mime:

- ``package contains mime types but does not inherit mime: <packagename> path '<file>' [mime]``

   The specified package contains mime type files (``.xml`` files in
   ``${datadir}/mime/packages``) and yet does not inherit the
   :ref:`ref-classes-mime` class which will ensure that these get
   properly installed. Either add ``inherit mime`` to the recipe or remove the
   files at the :ref:`ref-tasks-install` step if they are not needed.


.. _qa-check-mime-xdg:

- ``package contains desktop file with key 'MimeType' but does not inhert mime-xdg: <packagename> path '<file>' [mime-xdg]``

    The specified package contains a .desktop file with a 'MimeType' key
    present, but does not inherit the :ref:`ref-classes-mime-xdg`
    class that is required in order for that to be activated. Either add
    ``inherit mime`` to the recipe or remove the files at the
    :ref:`ref-tasks-install` step if they are not needed.


.. _qa-check-src-uri-bad:

- ``<recipename>: SRC_URI uses unstable GitHub archives [src-uri-bad]``

    GitHub provides "archive" tarballs, however these can be re-generated
    on the fly and thus the file's signature will not necessarily match that
    in the :term:`SRC_URI` checksums in future leading to build failures. It is
    recommended that you use an official release tarball or switch to
    pulling the corresponding revision in the actual git repository instead.


- ``SRC_URI uses PN not BPN [src-uri-bad]``

    If some part of :term:`SRC_URI` needs to reference the recipe name, it should do
    so using ${:term:`BPN`} rather than ${:term:`PN`} as the latter will change
    for different variants of the same recipe e.g. when :term:`BBCLASSEXTEND`
    or multilib are being used. This check will fail if a reference to ``${PN}``
    is found within the :term:`SRC_URI` value --- change it to ``${BPN}`` instead.


.. _qa-check-unhandled-features-check:

- ``<recipename>: recipe doesn't inherit features_check [unhandled-features-check]``

    This check ensures that if one of the variables that the
    :ref:`ref-classes-features_check` class supports (e.g.
    :term:`REQUIRED_DISTRO_FEATURES`) is used, then the recipe
    inherits :ref:`ref-classes-features_check` in order for
    the requirement to actually work. If you are seeing this message, either
    add ``inherit features_check`` to your recipe or remove the reference to
    the variable if it is not needed.


.. _qa-check-missing-update-alternatives:

- ``<recipename>: recipe defines ALTERNATIVE:<packagename> but doesn't inherit update-alternatives. This might fail during do_rootfs later! [missing-update-alternatives]``

    This check ensures that if a recipe sets the :term:`ALTERNATIVE` variable that the
    recipe also inherits :ref:`ref-classes-update-alternatives` such
    that the alternative will be correctly set up. If you are seeing this message, either
    add ``inherit update-alternatives`` to your recipe or remove the reference to the variable
    if it is not needed.


.. _qa-check-shebang-size:

- ``<packagename>: <file> maximum shebang size exceeded, the maximum size is 128. [shebang-size]``

    This check ensures that the shebang line (``#!`` in the first line) for a script
    is not longer than 128 characters, which can cause an error at runtime depending
    on the operating system. If you are seeing this message then the specified script
    may need to be patched to have a shorter in order to avoid runtime problems.


.. _qa-check-perllocalpod:

- ``<packagename> contains perllocal.pod (<files>), should not be installed [perllocalpod]``

    ``perllocal.pod`` is an index file of locally installed modules and so shouldn't be
    installed by any distribution packages. The :ref:`ref-classes-cpan` class
    already sets ``NO_PERLLOCAL`` to stop this file being generated by most Perl recipes,
    but if a recipe is using ``MakeMaker`` directly then they might not be doing this
    correctly. This check ensures that perllocal.pod is not in any package in order to
    avoid multiple packages shipping this file and thus their packages conflicting
    if installed together.


.. _qa-check-usrmerge:

- ``<packagename> package is not obeying usrmerge distro feature. /<path> should be relocated to /usr. [usrmerge]``

    If ``usrmerge`` is in :term:`DISTRO_FEATURES`, this check will ensure that no package
    installs files to root (``/bin``, ``/sbin``, ``/lib``, ``/lib64``) directories. If you are seeing this
    message, it indicates that the :ref:`ref-tasks-install` step (or perhaps the build process that
    :ref:`ref-tasks-install` is calling into, e.g. ``make install`` is using hardcoded paths instead
    of the variables set up for this (``bindir``, ``sbindir``, etc.), and should be
    changed so that it does.


.. _qa-check-patch-fuzz:

- ``Fuzz detected: <patch output> [patch-fuzz]``

    This check looks for evidence of "fuzz" when applying patches within the :ref:`ref-tasks-patch`
    task. Patch fuzz is a situation when the ``patch`` tool ignores some of the context
    lines in order to apply the patch. Consider this example:

    Patch to be applied::

        --- filename
        +++ filename
         context line 1
         context line 2
         context line 3
        +newly added line
         context line 4
         context line 5
         context line 6

    Original source code::

        different context line 1
        different context line 2
        context line 3
        context line 4
        different context line 5
        different context line 6

    Outcome (after applying patch with fuzz)::

        different context line 1
        different context line 2
        context line 3
        newly added line
        context line 4
        different context line 5
        different context line 6

    Chances are, the newly added line was actually added in a completely
    wrong location, or it was already in the original source and was added
    for the second time. This is especially possible if the context line 3
    and 4 are blank or have only generic things in them, such as ``#endif`` or ``}``.
    Depending on the patched code, it is entirely possible for an incorrectly
    patched file to still compile without errors.

    *How to eliminate patch fuzz warnings*

    Use the ``devtool`` command as explained by the warning. First, unpack the
    source into devtool workspace::

            devtool modify <recipe>

    This will apply all of the patches, and create new commits out of them in
    the workspace --- with the patch context updated.

    Then, replace the patches in the recipe layer::

            devtool finish --force-patch-refresh <recipe> <layer_path>

    The patch updates then need be reviewed (preferably with a side-by-side diff
    tool) to ensure they are indeed doing the right thing i.e.:

    #. they are applied in the correct location within the file;
    #. they do not introduce duplicate lines, or otherwise do things that
       are no longer necessary.

    To confirm these things, you can also review the patched source code in
    devtool's workspace, typically in ``<build_dir>/workspace/sources/<recipe>/``

    Once the review is done, you can create and publish a layer commit with
    the patch updates that modify the context. Devtool may also refresh
    other things in the patches, those can be discarded.


.. _qa-check-patch-status:

- ``Missing Upstream-Status in patch <patchfile> Please add according to <url> [patch-status]``

    The ``Upstream-Status`` value is missing in the specified patch file's header.
    This value is intended to track whether or not the patch has been sent
    upstream, whether or not it has been merged, etc.

    For more information, see the
    ":ref:`contributor-guide/recipe-style-guide:patch upstream status`"
    section in the Yocto Project and OpenEmbedded Contributor Guide.

- ``Malformed Upstream-Status in patch <patchfile> Please correct according to <url> [patch-status]``

    The ``Upstream-Status`` value in the specified patch file's header is invalid -
    it must be a specific format. See the "Missing Upstream-Status" entry above
    for more information.


.. _qa-check-buildpaths:

- ``File <filename> in package <packagename> contains reference to TMPDIR [buildpaths]``

    This check ensures that build system paths (including :term:`TMPDIR`) do not
    appear in output files, which not only leaks build system configuration into
    the target, but also hinders binary reproducibility as the output will change
    if the build system configuration changes.

    Typically these paths will enter the output through some mechanism in the
    configuration or compilation of the software being built by the recipe. To
    resolve this issue you will need to determine how the detected path is
    entering the output. Sometimes it may require adjusting scripts or code to
    use a relative path rather than an absolute one, or to pick up the path from
    runtime configuration or environment variables.

.. _qa-check-unimplemented-ptest:

- ``<tool> tests detected [unimplemented-ptest]``

    This check will detect if the source of the package contains some
    upstream-provided tests and, if so, that ptests are implemented for this
    recipe.  See the ":ref:`test-manual/ptest:testing packages with ptest`"
    section in the Yocto Project Development Tasks Manual. See also the
    ":ref:`ref-classes-ptest`" section.

.. _qa-check-virtual-slash:

- ``<variable> is set to <value> but the substring 'virtual/' holds no meaning in this context. It only works for build time dependencies, not runtime ones. It is suggested to use 'VIRTUAL-RUNTIME_' variables instead.``

    ``virtual/`` is a convention intended for use in the build context
    (i.e. :term:`PROVIDES` and :term:`DEPENDS`) rather than the runtime
    context (i.e. :term:`RPROVIDES` and :term:`RDEPENDS`). Use
    :term:`VIRTUAL-RUNTIME` variables instead for the latter.


Configuring and Disabling QA Checks
===================================

You can configure the QA checks globally so that specific check failures
either raise a warning or an error message, using the
:term:`WARN_QA` and :term:`ERROR_QA`
variables, respectively. You can also disable checks within a particular
recipe using :term:`INSANE_SKIP`. For information on
how to work with the QA checks, see the
":ref:`ref-classes-insane`" section.

.. note::

   Please keep in mind that the QA checks are meant to detect real
   or potential problems in the packaged output. So exercise caution
   when disabling these checks.
