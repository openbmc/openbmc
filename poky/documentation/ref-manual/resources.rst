.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

****************************************
Contributions and Additional Information
****************************************

.. _resources-intro:

Introduction
============

The Yocto Project team is happy for people to experiment with the Yocto
Project. There is a number of places where you can find help if you run into
difficulties or find bugs. This presents information about contributing
and participating in the Yocto Project.

.. _resources-contributions:

Contributions
=============

The Yocto Project gladly accepts contributions. You can submit changes
to the project either by creating and sending pull requests, or by
submitting patches through email. For information on how to do both as
well as information on how to identify the maintainer for each area of
code, see the :doc:`../contributor-guide/index`.

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

For a general procedure and guidelines on how to use Bugzilla to submit a bug
against the Yocto Project, see the following:

-  The ":doc:`../contributor-guide/report-defect`"
   section in the Yocto Project and OpenEmbedded Contributor Guide.

-  The Yocto Project :yocto_wiki:`Bugzilla wiki page </Bugzilla_Configuration_and_Bug_Tracking>`

For information on Bugzilla in general, see https://www.bugzilla.org/about/.

.. _resources-mailinglist:

Mailing lists
=============

There are multiple mailing lists maintained by the Yocto Project as well
as related OpenEmbedded mailing lists for discussion, patch submission
and announcements. To subscribe to one of the following mailing lists,
click on the appropriate URL in the following list and follow the
instructions:

-  :yocto_lists:`/g/yocto` --- general Yocto Project
   discussion mailing list.

-  :yocto_lists:`/g/yocto-patches` --- patch contribution mailing list for Yocto
   Project-related layers which do not have their own mailing list.

-  :oe_lists:`/g/openembedded-core` --- discussion mailing
   list about OpenEmbedded-Core (the core metadata).

-  :oe_lists:`/g/openembedded-devel` --- discussion
   mailing list about OpenEmbedded.

-  :oe_lists:`/g/bitbake-devel` --- discussion mailing
   list about the :term:`BitBake` build tool.

-  :yocto_lists:`/g/poky` --- discussion mailing list
   about :term:`Poky`.

-  :yocto_lists:`/g/yocto-announce` --- mailing list to
   receive official Yocto Project release and milestone announcements.

-  :yocto_lists:`/g/docs` --- discussion mailing list about the Yocto Project
   documentation.

See also :yocto_home:`the description of all mailing lists </community/mailing-lists/>`.

.. _resources-irc:

Internet Relay Chat (IRC)
=========================

Two IRC channels on `Libera Chat <https://libera.chat/>`__
are available for the Yocto Project and OpenEmbedded discussions:

-  ``#yocto``

-  ``#oe``

.. _resources-links-and-related-documentation:

Links and Related Documentation
===============================

Here is a list of resources you might find helpful:

-  :yocto_home:`The Yocto Project Website <>`: The home site
   for the Yocto Project.

-  :yocto_wiki:`The Yocto Project Main Wiki Page <>`: The main wiki page for
   the Yocto Project. This page contains information about project
   planning, release engineering, QA & automation, a reference site map,
   and other resources related to the Yocto Project.

-  :oe_home:`OpenEmbedded <>`: The build system used by the
   Yocto Project. This project is the upstream, generic, embedded
   distribution from which the Yocto Project derives its build system
   (Poky) and to which it contributes.

-  :oe_wiki:`BitBake </BitBake>`: The tool used to process metadata.

-  :doc:`BitBake User Manual <bitbake:index>`: A comprehensive
   guide to the BitBake tool. If you want information on BitBake, see
   this manual.

-  :doc:`/brief-yoctoprojectqs/index`: This
   short document lets you experience building an image using the Yocto
   Project without having to understand any concepts or details.

-  :doc:`/overview-manual/index`: This manual provides overview
   and conceptual information about the Yocto Project.

-  :doc:`/dev-manual/index`: This manual is a "how-to" guide
   that presents procedures useful to both application and system
   developers who use the Yocto Project.

-  :doc:`/sdk-manual/index` manual: This
   guide provides information that lets you get going with the standard
   or extensible SDK. An SDK, with its cross-development toolchains,
   allows you to develop projects inside or outside of the Yocto Project
   environment.

-  :doc:`/bsp-guide/bsp`: This guide defines the structure
   for BSP components. Having a commonly understood structure encourages
   standardization.

-  :doc:`/kernel-dev/index`: This manual describes
   how to work with Linux Yocto kernels as well as provides a bit of
   conceptual information on the construction of the Yocto Linux kernel
   tree.

-  :doc:`/ref-manual/index`: This
   manual provides reference material such as variable, task, and class
   descriptions.

-  :yocto_docs:`Yocto Project Mega-Manual </singleindex.html>`: This manual
   is simply a single HTML file comprised of the bulk of the Yocto
   Project manuals. It makes it easy to search for phrases and terms used
   in the Yocto Project documentation set.

-  :doc:`/profile-manual/index`: This manual presents a set of
   common and generally useful tracing and profiling schemes along with
   their applications (as appropriate) to each tool.

-  :doc:`/toaster-manual/index`: This manual
   introduces and describes how to set up and use Toaster. Toaster is an
   Application Programming Interface (API) and web-based interface to
   the :term:`OpenEmbedded Build System`, which uses
   BitBake, that reports build information.

-  `Yocto Project BitBake extension for VSCode
   <https://marketplace.visualstudio.com/items?itemName=yocto-project.yocto-bitbake>`__:
   This extension provides a rich feature set when working with BitBake recipes
   within the Visual Studio Code IDE.

-  :yocto_wiki:`FAQ </FAQ>`: A list of commonly asked
   questions and their answers.

-  :doc:`Release Information </migration-guides/index>`:
   Migration guides, release notes, new features, updates and known issues
   for the current and past releases of the Yocto Project.

-  :yocto_bugs:`Bugzilla <>`: The bug tracking application
   the Yocto Project uses. If you find problems with the Yocto Project,
   you should report them using this application.

-  :yocto_wiki:`Bugzilla Configuration and Bug Tracking Wiki Page
   </Bugzilla_Configuration_and_Bug_Tracking>`:
   Information on how to get set up and use the Yocto Project
   implementation of Bugzilla for logging and tracking Yocto Project
   defects.

-  Internet Relay Chat (IRC): Two IRC channels on
   `Libera Chat <https://libera.chat/>`__ are
   available for Yocto Project and OpenEmbeddded discussions: ``#yocto`` and
   ``#oe``, respectively.

-  `Quick EMUlator (QEMU) <https://wiki.qemu.org/Index.html>`__: An
   open-source machine emulator and virtualizer.
