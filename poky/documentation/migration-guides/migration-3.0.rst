.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 3.0 (zeus)
==================

This section provides migration information for moving to the Yocto
Project 3.0 Release (codename "zeus") from the prior release.

.. _migration-3.0-init-system-selection:

Init System Selection
---------------------

Changing the init system manager previously required setting a number of
different variables. You can now change the manager by setting the
``INIT_MANAGER`` variable and the corresponding include files (i.e.
``conf/distro/include/init-manager-*.conf``). Include files are provided
for four values: "none", "sysvinit", "systemd", and "mdev-busybox". The
default value, "none", for ``INIT_MANAGER`` should allow your current
settings to continue working. However, it is advisable to explicitly set
``INIT_MANAGER``.

.. _migration-3.0-lsb-support-removed:

LSB Support Removed
-------------------

Linux Standard Base (LSB) as a standard is not current, and is not well
suited for embedded applications. Support can be continued in a separate
layer if needed. However, presently LSB support has been removed from
the core.

As a result of this change, the ``poky-lsb`` derivative distribution
configuration that was also used for testing alternative configurations
has been replaced with a ``poky-altcfg`` distribution that has LSB parts
removed.

.. _migration-3.0-removed-recipes:

Removed Recipes
---------------

The following recipes have been removed.

-  ``core-image-lsb-dev``: Part of removed LSB support.

-  ``core-image-lsb``: Part of removed LSB support.

-  ``core-image-lsb-sdk``: Part of removed LSB support.

-  ``cve-check-tool``: Functionally replaced by the ``cve-update-db``
   recipe and :ref:`ref-classes-cve-check` class.

-  ``eglinfo``: No longer maintained. ``eglinfo`` from ``mesa-demos`` is
   an adequate and maintained alternative.

-  ``gcc-8.3``: Version 8.3 removed. Replaced by 9.2.

-  ``gnome-themes-standard``: Only needed by gtk+ 2.x, which has been
   removed.

-  ``gtk+``: GTK+ 2 is obsolete and has been replaced by gtk+3.

-  ``irda-utils``: Has become obsolete. IrDA support has been removed
   from the Linux kernel in version 4.17 and later.

-  ``libnewt-python``: ``libnewt`` Python support merged into main
   ``libnewt`` recipe.

-  ``libsdl``: Replaced by newer ``libsdl2``.

-  ``libx11-diet``: Became obsolete.

-  ``libxx86dga``: Removed obsolete client library.

-  ``libxx86misc``: Removed. Library is redundant.

-  ``linux-yocto``: Version 5.0 removed, which is now redundant (5.2 /
   4.19 present).

-  ``lsbinitscripts``: Part of removed LSB support.

-  ``lsb``: Part of removed LSB support.

-  ``lsbtest``: Part of removed LSB support.

-  ``openssl10``: Replaced by newer ``openssl`` version 1.1.

-  ``packagegroup-core-lsb``: Part of removed LSB support.

-  ``python-nose``: Removed the Python 2.x version of the recipe.

-  ``python-numpy``: Removed the Python 2.x version of the recipe.

-  ``python-scons``: Removed the Python 2.x version of the recipe.

-  ``source-highlight``: No longer needed.

-  ``stress``: Replaced by ``stress-ng``.

-  ``vulkan``: Split into ``vulkan-loader``, ``vulkan-headers``, and
   ``vulkan-tools``.

-  ``weston-conf``: Functionality moved to ``weston-init``.

.. _migration-3.0-packaging-changes:

Packaging Changes
-----------------

The following packaging changes have occurred.

-  The :wikipedia:`Epiphany <GNOME_Web>` browser
   has been dropped from ``packagegroup-self-hosted`` as it has not been
   needed inside ``build-appliance-image`` for quite some time and was
   causing resource problems.

-  ``libcap-ng`` Python support has been moved to a separate
   ``libcap-ng-python`` recipe to streamline the build process when the
   Python bindings are not needed.

-  ``libdrm`` now packages the file ``amdgpu.ids`` into a separate
   ``libdrm-amdgpu`` package.

-  ``python3``: The ``runpy`` module is now in the ``python3-core``
   package as it is required to support the common "python3 -m" command
   usage.

-  ``distcc`` now provides separate ``distcc-client`` and
   ``distcc-server`` packages as typically one or the other are needed,
   rather than both.

-  ``python*-setuptools`` recipes now separately package the
   ``pkg_resources`` module in a ``python-pkg-resources`` /
   ``python3-pkg-resources`` package as the module is useful independent
   of the rest of the setuptools package. The main ``python-setuptools``
   / ``python3-setuptools`` package depends on this new package so you
   should only need to update dependencies unless you want to take
   advantage of the increased granularity.

.. _migration-3.0-cve-checking:

CVE Checking
------------

``cve-check-tool`` has been functionally replaced by a new
``cve-update-db`` recipe and functionality built into the :ref:`ref-classes-cve-check`
class. The result uses NVD JSON data feeds rather than the deprecated
XML feeds that ``cve-check-tool`` was using, supports CVSSv3 scoring,
and makes other improvements.

Additionally, the ``CVE_CHECK_CVE_WHITELIST`` variable has been replaced
by ``CVE_CHECK_WHITELIST`` (replaced by :term:`CVE_CHECK_IGNORE` in version 4.0).

.. _migration-3.0-bitbake-changes:

BitBake Changes
---------------

The following BitBake changes have occurred.

-  ``addtask`` statements now properly validate dependent tasks.
   Previously, an invalid task was silently ignored. With this change,
   the invalid task generates a warning.

-  Other invalid ``addtask`` and ``deltask`` usages now trigger these
   warnings: "multiple target tasks arguments with addtask / deltask",
   and "multiple before/after clauses".

-  The "multiconfig" prefix is now shortened to "mc". "multiconfig" will
   continue to work, however it may be removed in a future release.

-  The ``bitbake -g`` command no longer generates a
   ``recipe-depends.dot`` file as the contents (i.e. a reprocessed
   version of ``task-depends.dot``) were confusing.

-  The ``bb.build.FuncFailed`` exception, previously raised by
   ``bb.build.exec_func()`` when certain other exceptions have occurred,
   has been removed. The real underlying exceptions will be raised
   instead. If you have calls to ``bb.build.exec_func()`` in custom
   classes or ``tinfoil-using`` scripts, any references to
   ``bb.build.FuncFailed`` should be cleaned up.

-  Additionally, the ``bb.build.exec_func()`` no longer accepts the
   "pythonexception" parameter. The function now always raises
   exceptions. Remove this argument in any calls to
   ``bb.build.exec_func()`` in custom classes or scripts.

-  The ``BB_SETSCENE_VERIFY_FUNCTION2`` variable is no longer used. In
   the unlikely event that you have any references to it, they should be
   removed.

-  The ``RunQueueExecuteScenequeue`` and ``RunQueueExecuteTasks`` events
   have been removed since setscene tasks are now executed as part of
   the normal runqueue. Any event handling code in custom classes or
   scripts that handles these two events need to be updated.

-  The arguments passed to functions used with
   :term:`BB_HASHCHECK_FUNCTION`
   have changed. If you are using your own custom hash check function,
   see :yocto_git:`/poky/commit/?id=40a5e193c4ba45c928fccd899415ea56b5417725`
   for details.

-  Task specifications in ``BB_TASKDEPDATA`` and class implementations
   used in signature generator classes now use "<fn>:<task>" everywhere
   rather than the "." delimiter that was being used in some places.
   This change makes it consistent with all areas in the code. Custom
   signature generator classes and code that reads ``BB_TASKDEPDATA``
   need to be updated to use ':' as a separator rather than '.'.

.. _migration-3.0-sanity-checks:

Sanity Checks
-------------

The following sanity check changes occurred.

-  :term:`SRC_URI` is now checked for usage of two
   problematic items:

   -  "${PN}" prefix/suffix use --- warnings always appear if ${PN} is
      used. You must fix the issue regardless of whether multiconfig or
      anything else that would cause prefixing/suffixing to happen.

   -  Github archive tarballs --- these are not guaranteed to be stable.
      Consequently, it is likely that the tarballs will be refreshed and
      thus the :term:`SRC_URI` checksums will fail to apply. It is recommended
      that you fetch either an official release tarball or a specific
      revision from the actual Git repository instead.

   Either one of these items now trigger a warning by default. If you
   wish to disable this check, remove ``src-uri-bad`` from
   :term:`WARN_QA`.

-  The ``file-rdeps`` runtime dependency check no longer expands
   :term:`RDEPENDS` recursively as there is no mechanism
   to ensure they can be fully computed, and thus races sometimes result
   in errors either showing up or not. Thus, you might now see errors
   for missing runtime dependencies that were previously satisfied
   recursively. Here is an example: package A contains a shell script
   starting with ``#!/bin/bash`` but has no dependency on bash. However,
   package A depends on package B, which does depend on bash. You need
   to add the missing dependency or dependencies to resolve the warning.

-  Setting ``DEPENDS_${PN}`` anywhere (i.e. typically in a recipe) now
   triggers an error. The error is triggered because
   :term:`DEPENDS` is not a package-specific variable
   unlike RDEPENDS. You should set :term:`DEPENDS` instead.

-  systemd currently does not work well with the musl C library because
   only upstream officially supports linking the library with glibc.
   Thus, a warning is shown when building systemd in conjunction with
   musl.

.. _migration-3.0-miscellaneous-changes:

Miscellaneous Changes
---------------------

The following miscellaneous changes have occurred.

-  The ``gnome`` class has been removed because it now does very little.
   You should update recipes that previously inherited this class to do
   the following::

      inherit gnomebase gtk-icon-cache gconf mime

-  The ``meta/recipes-kernel/linux/linux-dtb.inc`` file has been
   removed. This file was previously deprecated in favor of setting
   :term:`KERNEL_DEVICETREE` in any kernel
   recipe and only produced a warning. Remove any ``include`` or
   ``require`` statements pointing to this file.

-  :term:`TARGET_CFLAGS`,
   :term:`TARGET_CPPFLAGS`,
   :term:`TARGET_CXXFLAGS`, and
   :term:`TARGET_LDFLAGS` are no longer exported
   to the external environment. This change did not require any changes
   to core recipes, which is a good indicator that no changes will be
   required. However, if for some reason the software being built by one
   of your recipes is expecting these variables to be set, then building
   the recipe will fail. In such cases, you must either export the
   variable or variables in the recipe or change the scripts so that
   exporting is not necessary.

-  You must change the host distro identifier used in
   :term:`NATIVELSBSTRING` to use all lowercase
   characters even if it does not contain a version number. This change
   is necessary only if you are not using
   :ref:`ref-classes-uninative` and :term:`SANITY_TESTED_DISTROS`.

-  In the ``base-files`` recipe, writing the hostname into
   ``/etc/hosts`` and ``/etc/hostname`` is now done within the main
   :ref:`ref-tasks-install` function rather than in the
   ``do_install_basefilesissue`` function. The reason for the change is
   because ``do_install_basefilesissue`` is more easily overridden
   without having to duplicate the hostname functionality. If you have
   done the latter (e.g. in a ``base-files`` bbappend), then you should
   remove it from your customized ``do_install_basefilesissue``
   function.

-  The ``wic --expand`` command now uses commas to separate "key:value"
   pairs rather than hyphens.

   .. note::

      The wic command-line help is not updated.

   You must update any scripts or commands where you use
   ``wic --expand`` with multiple "key:value" pairs.

-  UEFI image variable settings have been moved from various places to a
   central ``conf/image-uefi.conf``. This change should not influence
   any existing configuration as the ``meta/conf/image-uefi.conf`` in
   the core metadata sets defaults that can be overridden in the same
   manner as before.

-  ``conf/distro/include/world-broken.inc`` has been removed. For cases
   where certain recipes need to be disabled when using the musl C
   library, these recipes now have ``COMPATIBLE_HOST_libc-musl`` set
   with a comment that explains why.


