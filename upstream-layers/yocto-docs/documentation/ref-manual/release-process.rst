.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*****************************************************
Yocto Project Releases and the Stable Release Process
*****************************************************

The Yocto Project release process is predictable and consists of both
major and minor (point) releases. This brief chapter provides
information on how releases are named, their life cycle, and their
stability.

Major and Minor Release Cadence
===============================

The Yocto Project delivers major releases (e.g. &DISTRO;) using a six
month cadence roughly timed each April and October of the year.
Here are examples of some major YP releases with their codenames
also shown. See the ":ref:`ref-manual/release-process:major release codenames`"
section for information on codenames used with major releases.

  - 4.1 ("Langdale")
  - 4.0 ("Kirkstone")
  - 3.4 ("Honister")

While the cadence is never perfect, this timescale facilitates
regular releases that have strong QA cycles while not overwhelming users
with too many new releases. The cadence is predictable and avoids many
major holidays in various geographies.

The Yocto project delivers minor (point) releases on an unscheduled
basis and are usually driven by the accumulation of enough significant
fixes or enhancements to the associated major release.
Some example past point releases are:

  - 4.1.3
  - 4.0.8
  - 3.4.4

The point release
indicates a point in the major release branch where a full QA cycle and
release process validates the content of the new branch.

.. note::

   Realize that there can be patches merged onto the stable release
   branches as and when they become available.

.. _ref-yp-development-cycle:

Development Cycle
=================

As explained in the previous :ref:`ref-manual/release-process:Major and Minor
Release Cadence` section, a new release comes out every six months.

During this six-months period of time, the Yocto Project releases four
"Milestone" releases which represent distinct points of time. The milestone
releases are tested through the :ref:`ref-manual/release-process:Testing and
Quality Assurance` process and helps spotting issues before the actual release
is out.

The time span between milestone releases can vary, but they are in general
evenly spaced out during this six-months period of time.

These milestone releases are tagged with a capital "M" after the future release
tag name. For example, the milestone tags "&DISTRO_RELEASE_SERIES;M1",
"&DISTRO_RELEASE_SERIES;M2", and "&DISTRO_RELEASE_SERIES;M3" are released before
the actual "&DISTRO_RELEASE_SERIES;" release.

.. note::

   The fourth milestone (M4) is not actually released and announced, but
   represents a point of time for the Quality Assurance team to start the
   :ref:`ref-manual/release-process:Testing and Quality Assurance` process
   before tagging and delivering the final release.

After the third milestone release (M3), the Yocto Project enters **Feature
Freeze**. This means that the maintainers of :term:`OpenEmbedded-Core
(OE-Core)`, :term:`BitBake` and other core repositories stop accepting
significant changes on the "master" branch. Changes that may be accepted are
minor upgrades to core components and security/bug fixes.

During feature freeze, a new branch is created and maintained separately to
test new features and enhancements received from contributors, but these changes
will only make it to the master branch after the release is out.

Major Release Codenames
=======================

Each major release receives a codename that identifies the release in
the :ref:`overview-manual/development-environment:yocto project source repositories`.
The concept is that branches of :term:`Metadata` with the same
codename are likely to be compatible and thus work together.

.. note::

   Codenames are associated with major releases because a Yocto Project
   release number (e.g. &DISTRO;) could conflict with a given layer or
   company versioning scheme. Codenames are unique, interesting, and
   easily identifiable.

Releases are given a nominal release version as well but the codename is
used in repositories for this reason. You can find information on Yocto
Project releases and codenames in the :yocto_home:`Releases page
</development/releases/>`.

Our :doc:`/migration-guides/index` detail how to migrate from one release of
the Yocto Project to the next.

Stable Release Process
======================

Once released, the release enters the stable release process at which
time a person is assigned as the maintainer for that stable release.
This maintainer monitors activity for the release by investigating and
handling nominated patches and backport activity. Only fixes and
enhancements that have first been applied on the "master" branch (i.e.
the current, in-development branch) are considered for backporting to a
stable release.

.. note::

   The current Yocto Project policy regarding backporting is to consider
   bug fixes and security fixes only. Policy dictates that features are
   not backported to a stable release. This policy means generic recipe
   version upgrades are unlikely to be accepted for backporting. The
   exception to this policy occurs when there is a strong reason such as
   the fix happens to also be the preferred upstream approach.

.. _ref-stable-lts-patch-acceptance-policies:

Stable/LTS Patch Acceptance Policies
=====================================

All changes must have already been accepted into the current master release
and any other release still within its stable support window.

**Acceptable:**

-  Security and CVE fixes
-  Fixes for bugs
-  Version updates which follow an upstream stable series or LTS that aligns with
   the original release (based on compatibility). See
   :ref:`ref-manual/release-process:stable point release upgrades` for details.

.. note::

   Once :term:`UPSTREAM_STABLE_RELEASE_REGEX` has been accepted for a recipe,
   version upgrades matching the accepted pattern are considered acceptable
   by default (with the usual ability to reject if upstream fails to follow
   their own stable guidelines).

**Potentially acceptable:**

-  Fixes so the codebase works with newly released distros, e.g.
   :ref:`ref-classes-uninative` tarball updates or build fixes for a new GCC
   version on the :term:`Build Host`.

**Unacceptable:**

-  General version upgrades
-  New features
-  ABI/API breakage
-  Metadata improvements and refactoring, unless this is a dependency of a
   required fix

.. note::

   Exceptions to these rules must be approved by the TSC.

.. _ref-stable-point-release-upgrades:

Stable Point Release Upgrades
=============================

Some upstream projects maintain stable branches that only collect bug fixes
and security patches --- similar to the Yocto Project's own stable release
policy. For recipes tracking such upstreams, it is possible to perform
*stable point release upgrades* (e.g. upgrading from 1.0.2 to 1.0.3) rather
than only accepting backported patches. This is the mechanism behind the
"acceptable: version updates which follow an upstream stable series" policy above.

Criteria for Qualifying Upstreams
---------------------------------

Setting :term:`UPSTREAM_STABLE_RELEASE_REGEX` for a recipe means that certain
version upgrades can be confidently treated as stable point upgrades under
OpenEmbedded's criteria. The
:ref:`Automated Upgrade Helper (AUH) <dev-manual/upgrading-recipes:Using the Auto Upgrade Helper (AUH)>`
will then automatically send patches to upgrade recipes with this variable
set during stable releases.

.. note::

   Patches to upgrade recipes without this variable may still be submitted
   for stable branches, but the barrier for acceptance is high. The commit
   message needs to justify that the version upgrade follows our stable
   branch policy or that there is an exceptional reason for taking the
   upgrade.

A recipe may have :term:`UPSTREAM_STABLE_RELEASE_REGEX` set based on the
following criteria:

1. **Upstream stable branches exist** --- the upstream project maintains
   branches named with a stable version component, and there is evidence
   that these branches only collect bug-fix and security-fix changes
   (no new features or ABI/API breakage). For example, util-linux version
   ``2.41.3`` has three parts in its version and upstream has branches like
   ``stable/v2.41`` with two parts. This demonstrates that bumps in the
   third part (e.g. ``2.41.3`` to ``2.41.4``) are stable point releases.
   Similarly, systemd maintains ``stable/v259-stable``.

2. **Upstream maintainer confirmation** --- the upstream project's maintainer
   explicitly confirms that a bump in a certain part of the version is
   bug-fix only.

3. **Clear historical or common-sense evidence** --- the project's change
   history clearly shows that a certain class of version bump is bug-fix
   only, or it is widely understood convention. For example, openssh's
   ``p`` suffix increments (``10.2p1`` to ``10.2p2``) and kmod's patch-level
   bumps are historically bug-fix only releases.

.. note::

   Patches to set :term:`UPSTREAM_STABLE_RELEASE_REGEX` are sent against the
   master branch first and follow the normal review process. Once accepted on
   master they may be backported to stable release branches. Exceptions may be
   made when necessary, e.g. setting :term:`UPSTREAM_STABLE_RELEASE_REGEX` on
   a stable branch for a recipe which has since been removed on master.

How Stable Release Regexes Work
-------------------------------

The stable point upgrade mechanism uses a *filter regex* to constrain the
upstream version check so that only versions within the same stable series
are considered. When a recipe sets :term:`UPSTREAM_STABLE_RELEASE_REGEX`, the
version-checking infrastructure in BitBake's fetchers (git, wget, crate)
applies that regex to filter discovered upstream versions. Only versions
matching the regex are considered as upgrade candidates.

For example, if a recipe is currently at version ``1.4.2`` and the regex is
``^1\.4(\.\d+)*$``, then version ``1.4.7`` would be a valid upgrade candidate
but ``1.5.0`` would not.

For recipes whose version uses a dot-separated scheme, the
:ref:`ref-classes-upstream-stable-release-point` class can automatically
generate this regex. For other versioning schemes, set
:term:`UPSTREAM_STABLE_RELEASE_REGEX` directly in the recipe.

.. _ref-long-term-support-releases:

Long Term Support Releases
==========================

While stable releases are supported for a duration of seven months,
some specific ones are now supported for a longer period by the Yocto
Project, and are called Long Term Support (:term:`LTS`) releases.

When significant issues are found, :term:`LTS` releases allow to publish
fixes not only for the current stable release, but also to the
:term:`LTS` releases that are still supported. Older stable releases which
have reached their End of Life (EOL) won't receive such updates.

This started with version 3.1 ("Dunfell"), released in April 2020, which
the project initially committed to supporting for two years, but this duration
was later extended to four years.

A new :term:`LTS` release is made every two years and is supported for four
years. This offers more stability to project users and leaves more time to
upgrade to the following :term:`LTS` release.

The currently supported :term:`LTS` releases are:

-  Version 5.0 ("Scarthgap"), released in April 2024 and supported until April 2028.
-  Version 4.0 ("Kirkstone"), released in May 2022 and supported until May 2026.

See :yocto_wiki:`/Stable_Release_and_LTS` for details about the management
of stable and :term:`LTS` releases.

This documentation was built for the &DISTRO_NAME; release.

.. image:: svg/releases.*
   :width: 100%

.. note::

   In some circumstances, a layer can be created by the community in order to
   add a specific feature or support a new version of some package for an :term:`LTS`
   release. This is called a :term:`Mixin` layer. These are thin and specific
   purpose layers which can be stacked with an :term:`LTS` release to "mix" a specific
   feature into that build. These are created on an as-needed basis and
   maintained by the people who need them.

   Policies on testing these layers depend on how widespread their usage is and
   determined on a case-by-case basis. You can find some :term:`Mixin` layers in the
   :yocto_git:`meta-lts-mixins </meta-lts-mixins>` repository. While the Yocto
   Project provides hosting for those repositories, it does not provide
   testing on them. Other :term:`Mixin` layers may be released elsewhere by the wider
   community.

Testing and Quality Assurance
=============================

Part of the Yocto Project development and release process is quality
assurance through the execution of test strategies. Test strategies
provide the Yocto Project team a way to ensure a release is validated.
Additionally, because the test strategies are visible to you as a
developer, you can validate your projects. This section overviews the
available test infrastructure used in the Yocto Project. For information
on how to run available tests on your projects, see the
":ref:`test-manual/runtime-testing:performing automated runtime testing`"
section in the Yocto Project Test Environment Manual.

The QA/testing infrastructure is woven into the project to the point
where core developers take some of it for granted. The infrastructure
consists of the following pieces:

-  ``bitbake-selftest``: A standalone command that runs unit tests on
   key pieces of BitBake and its fetchers.

-  :ref:`ref-classes-sanity`: This automatically
   included class checks the build environment for missing tools (e.g.
   ``gcc``) or common misconfigurations such as
   :term:`MACHINE` set incorrectly.

-  :ref:`ref-classes-insane`: This class checks the
   generated output from builds for sanity. For example, if building for
   an ARM target, did the build produce ARM binaries. If, for example,
   the build produced PPC binaries then there is a problem.

-  :ref:`ref-classes-testimage`: This class
   performs runtime testing of images after they are built. The tests
   are usually used with :doc:`QEMU </dev-manual/qemu>`
   to boot the images and check the combined runtime result boot
   operation and functions. However, the test can also use the IP
   address of a machine to test.

-  :ref:`ptest <test-manual/ptest:testing packages with ptest>`:
   Runs tests against packages produced during the build for a given
   piece of software. The test allows the packages to be run within a
   target image.

-  ``oe-selftest``: Tests combinations of BitBake invocations. These tests
   operate outside the OpenEmbedded build system itself. The
   ``oe-selftest`` can run all tests by default or can run selected
   tests or test suites.

Originally, much of this testing was done manually. However, significant
effort has been made to automate the tests so that more people can use
them and the Yocto Project development team can run them faster and more
efficiently.

The Yocto Project's main :yocto_ab:`Autobuilder <>` publicly tests each Yocto
Project release's code in the :oe_git:`openembedded-core </openembedded-core>`,
:yocto_git:`meta-yocto </meta-yocto>` and :oe_git:`bitbake </bitbake>` repositories. The
testing occurs for both the current state of the "master" branch and also for
submitted patches. Testing for submitted patches usually occurs
in the "master-next" branch in the :oe_git:`openembedded-core
</openembedded-core>`, :yocto_git:`meta-yocto </meta-yocto>` and
:oe_git:`bitbake </bitbake>` repository.

.. note::

   You can find all these branches in the
   :ref:`overview-manual/development-environment:yocto project source repositories`.

Testing within these public branches ensures in a publicly visible way
that all of the main supposed architectures and recipes in OE-Core
successfully build and behave properly.

Various features such as ``multilib``, sub architectures (e.g. ``x32``,
``poky-tiny``, ``musl``, ``no-x11`` and so forth),
``bitbake-selftest``, and ``oe-selftest`` are tested as part of the QA
process of a release. Complete testing and validation for a release
takes the Autobuilder workers several hours.

.. note::

   The Autobuilder workers are non-homogeneous, which means regular
   testing across a variety of Linux distributions occurs. The
   Autobuilder is limited to only testing QEMU-based setups and not real
   hardware.

Finally, in addition to the Autobuilder's tests, the Yocto Project QA
team also performs testing on a variety of platforms, which includes
actual hardware, to ensure expected results.
