.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Recipe Style Guide
******************

Recipe Naming Conventions
=========================

In general, most recipes should follow the naming convention
``recipes-category/package/packagename_version.bb``. Recipes for related
projects may share the same package directory. ``packagename``, ``category``,
and ``package`` may contain hyphens, but hyphens are not allowed in ``version``.

If the recipe is tracking a Git revision that does not correspond to a released
version of the software, ``version`` may be ``git`` (e.g. ``packagename_git.bb``)

Version Policy
==============

Our versions follow the form ``<package epoch>:<package version>-<package revision>``
or in BitBake variable terms ${:term:`PE`}:${:term:`PV`}-${:term:`PR`}. We
generally follow the `Debian <https://www.debian.org/doc/debian-policy/ch-controlfields.html#version>`__
version policy which defines these terms.

In most cases the version :term:`PV` will be set automatically from the recipe
file name. It is recommended to use released versions of software as these are
revisions that upstream are expecting people to use.

Package versions should always compare and sort correctly so that upgrades work
as expected. With conventional versions such as ``1.4`` upgrading ``to 1.5``
this happens naturally, but some versions don't sort. For example,
``1.5 Release Candidate 2`` could be written as ``1.5rc2`` but this sorts after
``1.5``, so upgrades from feeds won't happen correctly.

Instead the tilde (``~``) operator can be used, which sorts before the empty
string so ``1.5~rc2`` comes before ``1.5``. There is a historical syntax which
may be found where :term:`PV` is set as a combination of the prior version
``+`` the pre-release version, for example ``PV=1.4+1.5rc2``. This is a valid
syntax but the tilde form is preferred.

For version comparisons, the ``opkg-compare-versions`` program from
``opkg-utils`` can be useful when attempting to determine how two version
numbers compare to each other. Our definitive version comparison algorithm is
the one within bitbake which aims to match those of the package managers and
Debian policy closely.

When a recipe references a git revision that does not correspond to a released
version of software (e.g. is not a tagged version), the :term:`PV` variable
should include the Git revision using the following to make the
version clear::

    PV = "<version>+git${SRCPV}"

In this case, ``<version>`` should be the most recently released version of the
software from the current source revision (``git describe`` can be useful for
determining this). Whilst not recommended for published layers, this format is
also useful when using :term:`AUTOREV` to set the recipe to increment source
control revisions automatically, which can be useful during local development.

Version Number Changes
======================

The :term:`PR` variable is used to indicate different revisions of a recipe
that reference the same upstream source version. It can be used to force a
new version of a package to be installed onto a device from a package feed.
These once had to be set manually but in most cases these can now be set and
incremented automatically by a PR Server connected with a package feed.

When :term:`PV` increases, any existing :term:`PR` value can and should be
removed.

If :term:`PV` changes in such a way that it does not increase with respect to
the previous value, you need to increase :term:`PE` to ensure package managers
will upgrade it correctly. If unset you should set :term:`PE` to "1" since
the default of empty is easily confused with "0" depending on the package
manager. :term:`PE` can only have an integer value.

Recipe formatting
=================

Variable Formatting
-------------------

-  Variable assignment should a space around each side of the operator, e.g.
   ``FOO = "bar"``, not ``FOO="bar"``.

-  Double quotes should be used on the right-hand side of the assignment,
   e.g. ``FOO = "bar"`` not ``FOO = 'bar'``

-  Spaces should be used for indenting variables, with 4 spaces per tab

-  Long variables should be split over multiple lines when possible by using
   the continuation character (``\``)

-  When splitting a long variable over multiple lines, all continuation lines
   should be indented (with spaces) to align with the start of the quote on the
   first line::

       FOO = "this line is \
              long \
              "

   Instead of::

       FOO = "this line is \
       long \
       "

Python Function formatting
--------------------------

-  Spaces must be used for indenting Python code, with 4 spaces per tab

Shell Function formatting
-------------------------

-  The formatting of shell functions should be consistent within layers.
   Some use tabs, some use spaces.

Recipe metadata
===============

Required Variables
------------------

The following variables should be included in all recipes:

-  :term:`SUMMARY`: a one line description of the upstream project

-  :term:`DESCRIPTION`: an extended description of the upstream project,
   possibly with multiple lines. If no reasonable description can be written,
   this may be omitted as it defaults to :term:`SUMMARY`.

-  :term:`HOMEPAGE`: the URL to the upstream projects homepage.

-  :term:`BUGTRACKER`: the URL upstream projects bug tracking website,
   if applicable.

Recipe Ordering
---------------

When a variable is defined in recipes and classes, variables should follow the
general order when possible:

-  :term:`SUMMARY`
-  :term:`DESCRIPTION`
-  :term:`HOMEPAGE`
-  :term:`BUGTRACKER`
-  :term:`SECTION`
-  :term:`LICENSE`
-  :term:`LIC_FILES_CHKSUM`
-  :term:`DEPENDS`
-  :term:`PROVIDES`
-  :term:`PV`
-  :term:`SRC_URI`
-  :term:`SRCREV`
-  :term:`S`
-  ``inherit ...``
-  :term:`PACKAGECONFIG`
-  Build class specific variables such as ``EXTRA_QMAKEVARS_POST`` and :term:`EXTRA_OECONF`
-  Tasks such as :ref:`ref-tasks-configure`
-  :term:`PACKAGE_ARCH`
-  :term:`PACKAGES`
-  :term:`FILES`
-  :term:`RDEPENDS`
-  :term:`RRECOMMENDS`
-  :term:`RSUGGESTS`
-  :term:`RPROVIDES`
-  :term:`RCONFLICTS`
-  :term:`BBCLASSEXTEND`

There are some cases where ordering is important and these cases would override
this default order. Examples include:

-  :term:`PACKAGE_ARCH` needing to be set before ``inherit packagegroup``

Tasks should be ordered based on the order they generally execute. For commonly
used tasks this would be:

-  :ref:`ref-tasks-fetch`
-  :ref:`ref-tasks-unpack`
-  :ref:`ref-tasks-patch`
-  :ref:`ref-tasks-prepare_recipe_sysroot`
-  :ref:`ref-tasks-configure`
-  :ref:`ref-tasks-compile`
-  :ref:`ref-tasks-install`
-  :ref:`ref-tasks-populate_sysroot`
-  :ref:`ref-tasks-package`

Custom tasks should be sorted similarly.

Package specific variables are typically grouped together, e.g.::

    RDEPENDS:${PN} = “foo”
    RDEPENDS:${PN}-libs = “bar”

    RRECOMMENDS:${PN} = “one”
    RRECOMMENDS:${PN}-libs = “two”

Recipe License Fields
---------------------

Recipes need to define both the :term:`LICENSE` and
:term:`LIC_FILES_CHKSUM` variables:

-  :term:`LICENSE`: This variable specifies the license for the software.
   If you do not know the license under which the software you are
   building is distributed, you should go to the source code and look
   for that information. Typical files containing this information
   include ``COPYING``, :term:`LICENSE`, and ``README`` files. You could
   also find the information near the top of a source file. For example,
   given a piece of software licensed under the GNU General Public
   License version 2, you would set :term:`LICENSE` as follows::

      LICENSE = "GPL-2.0-only"

   The licenses you specify within :term:`LICENSE` can have any name as long
   as you do not use spaces, since spaces are used as separators between
   license names. For standard licenses, use the names of the files in
   ``meta/files/common-licenses/`` or the :term:`SPDXLICENSEMAP` flag names
   defined in ``meta/conf/licenses.conf``.

-  :term:`LIC_FILES_CHKSUM`: The OpenEmbedded build system uses this
   variable to make sure the license text has not changed. If it has,
   the build produces an error and it affords you the chance to figure
   it out and correct the problem.

   You need to specify all applicable licensing files for the software.
   At the end of the configuration step, the build process will compare
   the checksums of the files to be sure the text has not changed. Any
   differences result in an error with the message containing the
   current checksum. For more explanation and examples of how to set the
   :term:`LIC_FILES_CHKSUM` variable, see the
   ":ref:`dev-manual/licenses:tracking license changes`" section.

   To determine the correct checksum string, you can list the
   appropriate files in the :term:`LIC_FILES_CHKSUM` variable with incorrect
   md5 strings, attempt to build the software, and then note the
   resulting error messages that will report the correct md5 strings.
   See the ":ref:`dev-manual/new-recipe:fetching code`" section for
   additional information.

   Here is an example that assumes the software has a ``COPYING`` file::

      LIC_FILES_CHKSUM = "file://COPYING;md5=xxx"

   When you try to build the
   software, the build system will produce an error and give you the
   correct string that you can substitute into the recipe file for a
   subsequent build.

Tips and Guidelines for Writing Recipes
---------------------------------------

-  Use :term:`BBCLASSEXTEND` instead of creating separate recipes such as ``-native``
   and ``-nativesdk`` ones, whenever possible. This avoids having to maintain multiple
   recipe files at the same time.
