.. SPDX-License-Identifier: CC-BY-2.0-UK

****************************************
Contributions and Additional Information
****************************************

.. _resources-intro:

Introduction
============

The Yocto Project team is happy for people to experiment with the Yocto
Project. A number of places exist to find help if you run into
difficulties or find bugs. This presents information about contributing
and participating in the Yocto Project.

.. _resources-contributions:

Contributions
=============

The Yocto Project gladly accepts contributions. You can submit changes
to the project either by creating and sending pull requests, or by
submitting patches through email. For information on how to do both as
well as information on how to identify the maintainer for each area of
code, see the ":ref:`how-to-submit-a-change`" section in the
Yocto Project Development Tasks Manual.

.. _resources-bugtracker:

Yocto Project Bugzilla
======================

The Yocto Project uses its own implementation of
:yocto_bugs:`Bugzilla <>` to track defects (bugs).
Implementations of Bugzilla work well for group development because they
track bugs and code changes, can be used to communicate changes and
problems with developers, can be used to submit and review patches, and
can be used to manage quality assurance.

Sometimes it is helpful to submit, investigate, or track a bug against
the Yocto Project itself (e.g. when discovering an issue with some
component of the build system that acts contrary to the documentation or
your expectations).

A general procedure and guidelines exist for when you use Bugzilla to
submit a bug. For information on how to use Bugzilla to submit a bug
against the Yocto Project, see the following:

-  The ":ref:`dev-manual/dev-manual-common-tasks:submitting a defect against the yocto project`"
   section in the Yocto Project Development Tasks Manual.

-  The Yocto Project :yocto_wiki:`Bugzilla wiki page </wiki/Bugzilla_Configuration_and_Bug_Tracking>`

For information on Bugzilla in general, see http://www.bugzilla.org/about/.

.. _resources-mailinglist:

Mailing lists
=============

A number of mailing lists maintained by the Yocto Project exist as well
as related OpenEmbedded mailing lists for discussion, patch submission
and announcements. To subscribe to one of the following mailing lists,
click on the appropriate URL in the following list and follow the
instructions:

-  https://lists.yoctoproject.org/g/yocto - General Yocto Project
   discussion mailing list.

-  https://lists.openembedded.org/g/openembedded-core - Discussion mailing
   list about OpenEmbedded-Core (the core metadata).

-  https://lists.openembedded.org/g/openembedded-devel - Discussion
   mailing list about OpenEmbedded.

-  https://lists.openembedded.org/g/bitbake-devel - Discussion mailing
   list about the :term:`BitBake` build tool.

-  https://lists.yoctoproject.org/g/poky - Discussion mailing list
   about `Poky <#poky>`__.

-  https://lists.yoctoproject.org/g/yocto-announce - Mailing list to
   receive official Yocto Project release and milestone announcements.

For more Yocto Project-related mailing lists, see the
Yocto Project Website
.
.. _resources-irc:

Internet Relay Chat (IRC)
=========================

Two IRC channels on freenode are available for the Yocto Project and
Poky discussions:

-  ``#yocto``

-  ``#poky``

.. _resources-links-and-related-documentation:

Links and Related Documentation
===============================

Here is a list of resources you might find helpful:

-  :yocto_home:`The Yocto Project Website <>`\ *:* The home site
   for the Yocto Project.

-  :yocto_wiki:`The Yocto Project Main Wiki Page </wiki/Main_Page>`\ *:* The main wiki page for
   the Yocto Project. This page contains information about project
   planning, release engineering, QA & automation, a reference site map,
   and other resources related to the Yocto Project.

-  `OpenEmbedded <http://www.openembedded.org/>`__\ *:* The build system used by the
   Yocto Project. This project is the upstream, generic, embedded
   distribution from which the Yocto Project derives its build system
   (Poky) and to which it contributes.

-  `BitBake <http://www.openembedded.org/wiki/BitBake>`__\ *:* The tool
   used to process metadata.

-  :doc:`BitBake User Manual <bitbake:index>`\ *:* A comprehensive
   guide to the BitBake tool. If you want information on BitBake, see
   this manual.

-  :doc:`../brief-yoctoprojectqs/brief-yoctoprojectqs` *:* This
   short document lets you experience building an image using the Yocto
   Project without having to understand any concepts or details.

-  :doc:`../overview-manual/overview-manual` *:* This manual provides overview
   and conceptual information about the Yocto Project.

-  :doc:`../dev-manual/dev-manual` *:* This manual is a "how-to" guide
   that presents procedures useful to both application and system
   developers who use the Yocto Project.

-  :doc:`../sdk-manual/sdk-manual` *manual :* This
   guide provides information that lets you get going with the standard
   or extensible SDK. An SDK, with its cross-development toolchains,
   allows you to develop projects inside or outside of the Yocto Project
   environment.

-  :doc:`../bsp-guide/bsp` *:* This guide defines the structure
   for BSP components. Having a commonly understood structure encourages
   standardization.

-  :doc:`../kernel-dev/kernel-dev` *:* This manual describes
   how to work with Linux Yocto kernels as well as provides a bit of
   conceptual information on the construction of the Yocto Linux kernel
   tree.

-  :doc:`../ref-manual/ref-manual` *:* This
   manual provides reference material such as variable, task, and class
   descriptions.

-  `Yocto Project Mega-Manual <https://docs.yoctoproject.org/singleindex.html>`__\ *:* This manual
   is simply a single HTML file comprised of the bulk of the Yocto
   Project manuals. The Mega-Manual primarily exists as a vehicle by
   which you can easily search for phrases and terms used in the Yocto
   Project documentation set.

-  :doc:`../profile-manual/profile-manual` *:* This manual presents a set of
   common and generally useful tracing and profiling schemes along with
   their applications (as appropriate) to each tool.

-  :doc:`../toaster-manual/toaster-manual` *:* This manual
   introduces and describes how to set up and use Toaster. Toaster is an
   Application Programming Interface (API) and web-based interface to
   the :term:`OpenEmbedded Build System`, which uses
   BitBake, that reports build information.

-  :yocto_wiki:`FAQ </wiki/FAQ>`\ *:* A list of commonly asked
   questions and their answers.

-  *Release Notes:* Features, updates and known issues for the current
   release of the Yocto Project. To access the Release Notes, go to the
   :yocto_home:`Downloads </software-overview/downloads>` page on
   the Yocto Project website and click on the "RELEASE INFORMATION" link
   for the appropriate release.

-  `Bugzilla <https://bugzilla.yoctoproject.org>`__\ *:* The bug tracking application
   the Yocto Project uses. If you find problems with the Yocto Project,
   you should report them using this application.

-  :yocto_wiki:`Bugzilla Configuration and Bug Tracking Wiki Page </wiki/Bugzilla_Configuration_and_Bug_Tracking>`\ *:*
   Information on how to get set up and use the Yocto Project
   implementation of Bugzilla for logging and tracking Yocto Project
   defects.

-  *Internet Relay Chat (IRC):* Two IRC channels on freenode are
   available for Yocto Project and Poky discussions: ``#yocto`` and
   ``#poky``, respectively.

-  `Quick EMUlator (QEMU) <http://wiki.qemu.org/Index.html>`__\ *:* An
   open-source machine emulator and virtualizer.
