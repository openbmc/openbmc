.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Recipe Style Guide
******************

Recipe Naming Conventions
=========================

In general, most recipes should follow the naming convention
``recipes-category/recipename/recipename_version.bb``. Recipes for related
projects may share the same recipe directory. ``recipename`` and ``category``
may contain hyphens, but hyphens are not allowed in ``version``.

If the recipe is tracking a Git revision that does not correspond to a released
version of the software, ``version`` may be ``git`` (e.g. ``recipename_git.bb``)
and the recipe would set :term:`PV`.

Version Policy
==============

Our versions follow the form ``<epoch>:<version>-<revision>``
or in BitBake variable terms ${:term:`PE`}:${:term:`PV`}-${:term:`PR`}. We
generally follow the `Debian <https://www.debian.org/doc/debian-policy/ch-controlfields.html#version>`__
version policy which defines these terms.

In most cases the version :term:`PV` will be set automatically from the recipe
file name. It is recommended to use released versions of software as these are
revisions that upstream are expecting people to use.

Recipe versions should always compare and sort correctly so that upgrades work
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
new version of a recipe to be installed onto a device from a package feed.
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

License Updates
~~~~~~~~~~~~~~~

When you change the :term:`LICENSE` or :term:`LIC_FILES_CHKSUM` in the recipe
you need to briefly explain the reason for the change via a ``License-Update:``
tag.  Often it's quite trivial, such as::

    License-Update: copyright years refreshed

Less often, the actual licensing terms themselves will have changed.  If so, do
try to link to upstream making/justifying that decision.

Tips and Guidelines for Writing Recipes
---------------------------------------

-  Use :term:`BBCLASSEXTEND` instead of creating separate recipes such as ``-native``
   and ``-nativesdk`` ones, whenever possible. This avoids having to maintain multiple
   recipe files at the same time.

-  Recipes should have tasks which are idempotent, i.e. that executing a given task
   multiple times shouldn't change the end result. The build environment is built upon
   this assumption and breaking it can cause obscure build failures.

-  For idempotence when modifying files in tasks, it is usually best to:

   - copy a file ``X`` to ``X.orig`` (only if it doesn't exist already)
   - then, copy ``X.orig`` back to ``X``,
   - and, finally, modify ``X``.

   This ensures if rerun the task always has the same end result and the
   original file can be preserved to reuse. It also guards against an
   interrupted build corrupting the file.

Patch Upstream Status
=====================

In order to keep track of patches applied by recipes and ultimately reduce the
number of patches that need maintaining, the OpenEmbedded build system
requires information about the upstream status of each patch.

In its description, each patch should provide detailed information about the
bug that it addresses, such as the URL in a bug tracking system and links
to relevant mailing list archives.

Then, you should also add an ``Upstream-Status:`` tag containing one of the
following status strings:

``Pending``
   No determination has been made yet, or patch has not yet been submitted to
   upstream.

   Keep in mind that every patch submitted upstream reduces the maintainance
   burden in OpenEmbedded and Yocto Project in the long run, so this patch
   status should only be used in exceptional cases if there are genuine
   obstacles to submitting a patch upstream; the reason for that should be
   included in the patch.

``Submitted [where]``
   Submitted to upstream, waiting for approval. Optionally include where
   it was submitted, such as the author, mailing list, etc.

``Backport [version]``
   Accepted upstream and included in the next release, or backported from newer
   upstream version, because we are at a fixed version.
   Include upstream version info (e.g. commit ID or next expected version).

``Denied``
   Not accepted by upstream, include reason in patch.

``Inactive-Upstream [lastcommit: when (and/or) lastrelease: when]``
   The upstream is no longer available. This typically means a defunct project
   where no activity has happened for a long time --- measured in years. To make
   that judgement, it is recommended to look at not only when the last release
   happened, but also when the last commit happened, and whether newly made bug
   reports and merge requests since that time receive no reaction. It is also
   recommended to add to the patch description any relevant links where the
   inactivity can be clearly seen.

``Inappropriate [reason]``
   The patch is not appropriate for upstream, include a brief reason on the
   same line enclosed with ``[]``. In the past, there were several different
   reasons not to submit patches upstream, but we have to consider that every
   non-upstreamed patch means a maintainance burden for recipe maintainers.
   Currently, the only reasons to mark patches as inappropriate for upstream
   submission are:

   -  ``oe specific``: the issue is specific to how OpenEmbedded performs builds
      or sets things up at runtime, and can be resolved only with a patch that
      is not however relevant or appropriate for general upstream submission.
   -  ``upstream ticket <link>``: the issue is not specific to Open-Embedded
      and should be fixed upstream, but the patch in its current form is not
      suitable for merging upstream, and the author lacks sufficient expertise
      to develop a proper patch. Instead the issue is handled via a bug report
      (include link).

Of course, if another person later takes care of submitting this patch upstream,
the status should be changed to ``Submitted [where]``, and an additional
``Signed-off-by:`` line should be added to the patch by the person claiming
responsibility for upstreaming.

Examples
--------

Here's an example of a patch that has been submitted upstream::

   rpm: Adjusted the foo setting in bar

   [RPM Ticket #65] -- http://rpm5.org/cvs/tktview?tn=65,5

   The foo setting in bar was decreased from X to X-50% in order to
   ensure we don't exhaust all system memory with foobar threads.

   Upstream-Status: Submitted [rpm5-devel@rpm5.org]

   Signed-off-by: Joe Developer <joe.developer@example.com>

A future update can change the value to ``Backport`` or ``Denied`` as
appropriate.

Another example of a patch that is specific to OpenEmbedded::

   Do not treat warnings as errors

   There are additional warnings found with musl which are
   treated as errors and fails the build, we have more combinations
   than upstream supports to handle.

   Upstream-Status: Inappropriate [oe specific]

Here's a patch that has been backported from an upstream commit::

   include missing sys/file.h for LOCK_EX

   Upstream-Status: Backport [https://github.com/systemd/systemd/commit/ac8db36cbc26694ee94beecc8dca208ec4b5fd45]

CVE patches
===========

In order to have a better control of vulnerabilities, patches that fix CVEs must
contain a ``CVE:`` tag. This tag list all CVEs fixed by the patch. If more than
one CVE is fixed, separate them using spaces.

CVE Examples
------------

This should be the header of patch that fixes :cve:`2015-8370` in GRUB2::

   grub2: Fix CVE-2015-8370

   [No upstream tracking] -- https://bugzilla.redhat.com/show_bug.cgi?id=1286966

   Back to 28; Grub2 Authentication

   Two functions suffer from integer underflow fault; the grub_username_get() and grub_password_get()located in
   grub-core/normal/auth.c and lib/crypto.c respectively. This can be exploited to obtain a Grub rescue shell.

   Upstream-Status: Backport [http://git.savannah.gnu.org/cgit/grub.git/commit/?id=451d80e52d851432e109771bb8febafca7a5f1f2]
   CVE: CVE-2015-8370
   Signed-off-by: Joe Developer <joe.developer@example.com>
