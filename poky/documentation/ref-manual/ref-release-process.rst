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

The Yocto Project delivers major releases (e.g. DISTRO) using a six
month cadence roughly timed each April and October of the year.
Following are examples of some major YP releases with their codenames
also shown. See the "`Major Release
Codenames <#major-release-codenames>`__" section for information on
codenames used with major releases.

  - 2.2 (Morty) 
  - 2.1 (Krogoth)
  - 2.0 (Jethro) 

While the cadence is never perfect, this timescale facilitates
regular releases that have strong QA cycles while not overwhelming users
with too many new releases. The cadence is predictable and avoids many
major holidays in various geographies.

The Yocto project delivers minor (point) releases on an unscheduled
basis and are usually driven by the accumulation of enough significant
fixes or enhancements to the associated major release. Following are
some example past point releases:

  - 2.1.1
  - 2.1.2
  - 2.2.1 

The point release
indicates a point in the major release branch where a full QA cycle and
release process validates the content of the new branch.

.. note::

   Realize that there can be patches merged onto the stable release
   branches as and when they become available.

Major Release Codenames
=======================

Each major release receives a codename that identifies the release in
the :ref:`overview-manual/overview-manual-development-environment:yocto project source repositories`.
The concept is that branches of :term:`Metadata` with the same
codename are likely to be compatible and thus work together.

.. note::

   Codenames are associated with major releases because a Yocto Project
   release number (e.g. DISTRO) could conflict with a given layer or
   company versioning scheme. Codenames are unique, interesting, and
   easily identifiable.

Releases are given a nominal release version as well but the codename is
used in repositories for this reason. You can find information on Yocto
Project releases and codenames at
https://wiki.yoctoproject.org/wiki/Releases.

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
   exception to this policy occurs when a strong reason exists such as
   the fix happens to also be the preferred upstream approach.

Stable release branches have strong maintenance for about a year after
their initial release. Should significant issues be found for any
release regardless of its age, fixes could be backported to older
releases. For issues that are not backported given an older release,
Community LTS trees and branches exist where community members share
patches for older releases. However, these types of patches do not go
through the same release process as do point releases. You can find more
information about stable branch maintenance at
https://wiki.yoctoproject.org/wiki/Stable_branch_maintenance.

Testing and Quality Assurance
=============================

Part of the Yocto Project development and release process is quality
assurance through the execution of test strategies. Test strategies
provide the Yocto Project team a way to ensure a release is validated.
Additionally, because the test strategies are visible to you as a
developer, you can validate your projects. This section overviews the
available test infrastructure used in the Yocto Project. For information
on how to run available tests on your projects, see the
":ref:`dev-manual/dev-manual-common-tasks:performing automated runtime testing`"
section in the Yocto Project Development Tasks Manual.

The QA/testing infrastructure is woven into the project to the point
where core developers take some of it for granted. The infrastructure
consists of the following pieces:

-  ``bitbake-selftest``: A standalone command that runs unit tests on
   key pieces of BitBake and its fetchers.

-  :ref:`sanity.bbclass <ref-classes-sanity>`: This automatically
   included class checks the build environment for missing tools (e.g.
   ``gcc``) or common misconfigurations such as
   :term:`MACHINE` set incorrectly.

-  :ref:`insane.bbclass <ref-classes-insane>`: This class checks the
   generated output from builds for sanity. For example, if building for
   an ARM target, did the build produce ARM binaries. If, for example,
   the build produced PPC binaries then there is a problem.

-  :ref:`testimage.bbclass <ref-classes-testimage*>`: This class
   performs runtime testing of images after they are built. The tests
   are usually used with :doc:`QEMU <../dev-manual/dev-manual-qemu>`
   to boot the images and check the combined runtime result boot
   operation and functions. However, the test can also use the IP
   address of a machine to test.

-  :ref:`ptest <dev-manual/dev-manual-common-tasks:testing packages with ptest>`:
   Runs tests against packages produced during the build for a given
   piece of software. The test allows the packages to be be run within a
   target image.

-  ``oe-selftest``: Tests combination BitBake invocations. These tests
   operate outside the OpenEmbedded build system itself. The
   ``oe-selftest`` can run all tests by default or can run selected
   tests or test suites.

   .. note::

      Running
      oe-selftest
      requires host packages beyond the "Essential" grouping. See the "
      Required Packages for the Build Host
      " section for more information.

Originally, much of this testing was done manually. However, significant
effort has been made to automate the tests so that more people can use
them and the Yocto Project development team can run them faster and more
efficiently.

The Yocto Project's main Autobuilder (https://autobuilder.yoctoproject.org/)
publicly tests each Yocto Project release's code in the
:term:`OpenEmbedded-Core (OE-Core)`, Poky, and BitBake repositories. The testing
occurs for both the current state of the "master" branch and also for
submitted patches. Testing for submitted patches usually occurs in the
"ross/mut" branch in the ``poky-contrib`` repository (i.e. the
master-under-test branch) or in the "master-next" branch in the ``poky``
repository.

.. note::

   You can find all these branches in the Yocto Project
   Source Repositories
   .

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
