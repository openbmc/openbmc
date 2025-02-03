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
Project releases and codenames at :yocto_wiki:`/Releases`.

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
   Project provides hosting for those repositories, it does not provides
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
:yocto_git:`poky </poky>` and :oe_git:`bitbake </bitbake>` repositories. The
testing occurs for both the current state of the "master" branch and also for
submitted patches. Testing for submitted patches usually occurs in the
in the "master-next" branch in the :yocto_git:`poky </poky>` repository.

.. note::

   You can find all these branches in the
   :ref:`overview-manual/development-environment:yocto project source repositories`.

Testing within these public branches ensures in a publicly visible way
that all of the main supposed architectures and recipes in OE-Core
successfully build and behave properly.

Various features such as ``multilib``, sub architectures (e.g. ``x32``,
``poky-tiny``, ``musl``, ``no-x11`` and and so forth),
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
