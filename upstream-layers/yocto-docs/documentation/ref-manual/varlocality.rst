.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

****************
Variable Context
****************

While you can use most variables in almost any context such as
``.conf``, ``.bbclass``, ``.inc``, and ``.bb`` files, some variables are
often associated with a particular locality or context. This chapter
describes some common associations.

.. _ref-varlocality-configuration:

Configuration
=============

The following subsections provide lists of variables whose context is
configuration: distribution, machine, and local.

.. _ref-varlocality-config-distro:

Distribution (Distro)
---------------------

This section lists variables whose configuration context is the
distribution, or distro.

-  :term:`DISTRO`

-  :term:`DISTRO_NAME`

-  :term:`DISTRO_VERSION`

-  :term:`MAINTAINER`

-  :term:`PACKAGE_CLASSES`

-  :term:`TARGET_OS`

-  :term:`TARGET_FPU`

-  :term:`TCMODE`

-  :term:`TCLIBC`

.. _ref-varlocality-config-machine:

Machine
-------

This section lists variables whose configuration context is the machine.

-  :term:`TARGET_ARCH`

-  :term:`SERIAL_CONSOLES`

-  :term:`PACKAGE_EXTRA_ARCHS`

-  :term:`IMAGE_FSTYPES`

-  :term:`MACHINE_FEATURES`

-  :term:`MACHINE_EXTRA_RDEPENDS`

-  :term:`MACHINE_EXTRA_RRECOMMENDS`

-  :term:`MACHINE_ESSENTIAL_EXTRA_RDEPENDS`

-  :term:`MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS`

.. _ref-varlocality-config-local:

Local
-----

This section lists variables whose configuration context is the local
configuration through the ``local.conf`` file.

-  :term:`DISTRO`

-  :term:`MACHINE`

-  :term:`DL_DIR`

-  :term:`BBFILES`

-  :term:`EXTRA_IMAGE_FEATURES`

-  :term:`PACKAGE_CLASSES`

-  :term:`BB_NUMBER_THREADS`

-  :term:`BBINCLUDELOGS`

-  :term:`ENABLE_BINARY_LOCALE_GENERATION`

.. _ref-varlocality-recipes:

Recipes
=======

The following subsections provide lists of variables whose context is
recipes: required, dependencies, path, and extra build information.

.. _ref-varlocality-recipe-required:

Required
--------

This section lists variables that are required for recipes.

-  :term:`LICENSE`

-  :term:`LIC_FILES_CHKSUM`

-  :term:`SRC_URI` --- used in recipes that fetch local or remote files.

.. _ref-varlocality-recipe-dependencies:

Dependencies
------------

This section lists variables that define recipe dependencies.

-  :term:`DEPENDS`

-  :term:`RDEPENDS`

-  :term:`RRECOMMENDS`

-  :term:`RCONFLICTS`

-  :term:`RREPLACES`

.. _ref-varlocality-recipe-paths:

Paths
-----

This section lists variables that define recipe paths.

-  :term:`WORKDIR`

-  :term:`S`

-  :term:`FILES`

.. _ref-varlocality-recipe-build:

Extra Build Information
-----------------------

This section lists variables that define extra build information for
recipes.

-  :term:`DEFAULT_PREFERENCE`

-  :term:`EXTRA_OECMAKE`

-  :term:`EXTRA_OECONF`

-  :term:`EXTRA_OEMAKE`

-  :term:`PACKAGECONFIG_CONFARGS`

-  :term:`PACKAGES`
