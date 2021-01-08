.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*****************************************
The Yocto Project Development Environment
*****************************************

This chapter takes a look at the Yocto Project development environment.
The chapter provides Yocto Project Development environment concepts that
help you understand how work is accomplished in an open source
environment, which is very different as compared to work accomplished in
a closed, proprietary environment.

Specifically, this chapter addresses open source philosophy, source
repositories, workflows, Git, and licensing.

Open Source Philosophy
======================

Open source philosophy is characterized by software development directed
by peer production and collaboration through an active community of
developers. Contrast this to the more standard centralized development
models used by commercial software companies where a finite set of
developers produces a product for sale using a defined set of procedures
that ultimately result in an end product whose architecture and source
material are closed to the public.

Open source projects conceptually have differing concurrent agendas,
approaches, and production. These facets of the development process can
come from anyone in the public (community) who has a stake in the
software project. The open source environment contains new copyright,
licensing, domain, and consumer issues that differ from the more
traditional development environment. In an open source environment, the
end product, source material, and documentation are all available to the
public at no cost.

A benchmark example of an open source project is the Linux kernel, which
was initially conceived and created by Finnish computer science student
Linus Torvalds in 1991. Conversely, a good example of a non-open source
project is the Windows family of operating systems developed by
Microsoft Corporation.

Wikipedia has a good historical description of the Open Source
Philosophy `here <https://en.wikipedia.org/wiki/Open_source>`__. You can
also find helpful information on how to participate in the Linux
Community
`here <https://www.kernel.org/doc/html/latest/process/index.html>`__.

The Development Host
====================

A development host or :term:`Build Host` is key to
using the Yocto Project. Because the goal of the Yocto Project is to
develop images or applications that run on embedded hardware,
development of those images and applications generally takes place on a
system not intended to run the software - the development host.

You need to set up a development host in order to use it with the Yocto
Project. Most find that it is best to have a native Linux machine
function as the development host. However, it is possible to use a
system that does not run Linux as its operating system as your
development host. When you have a Mac or Windows-based system, you can
set it up as the development host by using
`CROPS <https://github.com/crops/poky-container>`__, which leverages
`Docker Containers <https://www.docker.com/>`__. Once you take the steps
to set up a CROPS machine, you effectively have access to a shell
environment that is similar to what you see when using a Linux-based
development host. For the steps needed to set up a system using CROPS,
see the
":ref:`dev-manual/start:setting up to use cross platforms (crops)`"
section in
the Yocto Project Development Tasks Manual.

If your development host is going to be a system that runs a Linux
distribution, steps still exist that you must take to prepare the system
for use with the Yocto Project. You need to be sure that the Linux
distribution on the system is one that supports the Yocto Project. You
also need to be sure that the correct set of host packages are installed
that allow development using the Yocto Project. For the steps needed to
set up a development host that runs Linux, see the
":ref:`dev-manual/start:setting up a native linux host`"
section in the Yocto Project Development Tasks Manual.

Once your development host is set up to use the Yocto Project, several
methods exist for you to do work in the Yocto Project environment:

-  *Command Lines, BitBake, and Shells:* Traditional development in the
   Yocto Project involves using the :term:`OpenEmbedded Build System`,
   which uses
   BitBake, in a command-line environment from a shell on your
   development host. You can accomplish this from a host that is a
   native Linux machine or from a host that has been set up with CROPS.
   Either way, you create, modify, and build images and applications all
   within a shell-based environment using components and tools available
   through your Linux distribution and the Yocto Project.

   For a general flow of the build procedures, see the
   ":ref:`dev-manual/common-tasks:building a simple image`"
   section in the Yocto Project Development Tasks Manual.

-  *Board Support Package (BSP) Development:* Development of BSPs
   involves using the Yocto Project to create and test layers that allow
   easy development of images and applications targeted for specific
   hardware. To development BSPs, you need to take some additional steps
   beyond what was described in setting up a development host.

   The :doc:`/bsp-guide/index` provides BSP-related development
   information. For specifics on development host preparation, see the
   ":ref:`bsp-guide/bsp:preparing your build host to work with bsp layers`"
   section in the Yocto Project Board Support Package (BSP) Developer's
   Guide.

-  *Kernel Development:* If you are going to be developing kernels using
   the Yocto Project you likely will be using ``devtool``. A workflow
   using ``devtool`` makes kernel development quicker by reducing
   iteration cycle times.

   The :doc:`/kernel-dev/index` provides kernel-related
   development information. For specifics on development host
   preparation, see the
   ":ref:`kernel-dev/common:preparing the build host to work on the kernel`"
   section in the Yocto Project Linux Kernel Development Manual.

-  *Using Toaster:* The other Yocto Project development method that
   involves an interface that effectively puts the Yocto Project into
   the background is Toaster. Toaster provides an interface to the
   OpenEmbedded build system. The interface enables you to configure and
   run your builds. Information about builds is collected and stored in
   a database. You can use Toaster to configure and start builds on
   multiple remote build servers.

   For steps that show you how to set up your development host to use
   Toaster and on how to use Toaster in general, see the
   :doc:`/toaster-manual/index`.

Yocto Project Source Repositories
=================================

The Yocto Project team maintains complete source repositories for all
Yocto Project files at :yocto_git:`/`. This web-based source
code browser is organized into categories by function such as IDE
Plugins, Matchbox, Poky, Yocto Linux Kernel, and so forth. From the
interface, you can click on any particular item in the "Name" column and
see the URL at the bottom of the page that you need to clone a Git
repository for that particular item. Having a local Git repository of
the :term:`Source Directory`, which
is usually named "poky", allows you to make changes, contribute to the
history, and ultimately enhance the Yocto Project's tools, Board Support
Packages, and so forth.

For any supported release of Yocto Project, you can also go to the
:yocto_home:`Yocto Project Website <>` and select the "DOWNLOADS"
item from the "SOFTWARE" menu and get a released tarball of the ``poky``
repository, any supported BSP tarball, or Yocto Project tools. Unpacking
these tarballs gives you a snapshot of the released files.

.. note::

   -  The recommended method for setting up the Yocto Project
      :term:`Source Directory` and the files
      for supported BSPs (e.g., ``meta-intel``) is to use `Git <#git>`__
      to create a local copy of the upstream repositories.

   -  Be sure to always work in matching branches for both the selected
      BSP repository and the Source Directory (i.e. ``poky``)
      repository. For example, if you have checked out the "master"
      branch of ``poky`` and you are going to use ``meta-intel``, be
      sure to checkout the "master" branch of ``meta-intel``.

In summary, here is where you can get the project files needed for
development:

-  :yocto_git:`Source Repositories: <>` This area contains IDE
   Plugins, Matchbox, Poky, Poky Support, Tools, Yocto Linux Kernel, and
   Yocto Metadata Layers. You can create local copies of Git
   repositories for each of these areas.

   .. image:: figures/source-repos.png
      :align: center

   For steps on how to view and access these upstream Git repositories,
   see the ":ref:`dev-manual/start:accessing source repositories`"
   Section in the Yocto Project Development Tasks Manual.

-  :yocto_dl:`Index of /releases: </releases>` This is an index
   of releases such as Poky, Pseudo, installers for cross-development
   toolchains, miscellaneous support and all released versions of Yocto
   Project in the form of images or tarballs. Downloading and extracting
   these files does not produce a local copy of the Git repository but
   rather a snapshot of a particular release or image.

   .. image:: figures/index-downloads.png
      :align: center

   For steps on how to view and access these files, see the
   ":ref:`dev-manual/start:accessing index of releases`"
   section in the Yocto Project Development Tasks Manual.

-  *"DOWNLOADS" page for the* :yocto_home:`Yocto Project Website <>` *:*

   The Yocto Project website includes a "DOWNLOADS" page accessible
   through the "SOFTWARE" menu that allows you to download any Yocto
   Project release, tool, and Board Support Package (BSP) in tarball
   form. The tarballs are similar to those found in the
   :yocto_dl:`Index of /releases: </releases>` area.

   .. image:: figures/yp-download.png
      :align: center

   For steps on how to use the "DOWNLOADS" page, see the
   ":ref:`dev-manual/start:using the downloads page`"
   section in the Yocto Project Development Tasks Manual.

Git Workflows and the Yocto Project
===================================

Developing using the Yocto Project likely requires the use of
`Git <#git>`__. Git is a free, open source distributed version control
system used as part of many collaborative design environments. This
section provides workflow concepts using the Yocto Project and Git. In
particular, the information covers basic practices that describe roles
and actions in a collaborative development environment.

.. note::

   If you are familiar with this type of development environment, you
   might not want to read this section.

The Yocto Project files are maintained using Git in "branches" whose Git
histories track every change and whose structures provide branches for
all diverging functionality. Although there is no need to use Git, many
open source projects do so.

For the Yocto Project, a key individual called the "maintainer" is
responsible for the integrity of the "master" branch of a given Git
repository. The "master" branch is the "upstream" repository from which
final or most recent builds of a project occur. The maintainer is
responsible for accepting changes from other developers and for
organizing the underlying branch structure to reflect release strategies
and so forth.

.. note::

   For information on finding out who is responsible for (maintains) a
   particular area of code in the Yocto Project, see the
   ":ref:`dev-manual/common-tasks:submitting a change to the yocto project`"
   section of the Yocto Project Development Tasks Manual.

The Yocto Project ``poky`` Git repository also has an upstream
contribution Git repository named ``poky-contrib``. You can see all the
branches in this repository using the web interface of the
:yocto_git:`Source Repositories <>` organized within the "Poky Support"
area. These branches hold changes (commits) to the project that have
been submitted or committed by the Yocto Project development team and by
community members who contribute to the project. The maintainer
determines if the changes are qualified to be moved from the "contrib"
branches into the "master" branch of the Git repository.

Developers (including contributing community members) create and
maintain cloned repositories of upstream branches. The cloned
repositories are local to their development platforms and are used to
develop changes. When a developer is satisfied with a particular feature
or change, they "push" the change to the appropriate "contrib"
repository.

Developers are responsible for keeping their local repository up-to-date
with whatever upstream branch they are working against. They are also
responsible for straightening out any conflicts that might arise within
files that are being worked on simultaneously by more than one person.
All this work is done locally on the development host before anything is
pushed to a "contrib" area and examined at the maintainer's level.

A somewhat formal method exists by which developers commit changes and
push them into the "contrib" area and subsequently request that the
maintainer include them into an upstream branch. This process is called
"submitting a patch" or "submitting a change." For information on
submitting patches and changes, see the
":ref:`dev-manual/common-tasks:submitting a change to the yocto project`"
section in the Yocto Project Development Tasks Manual.

In summary, a single point of entry exists for changes into a "master"
or development branch of the Git repository, which is controlled by the
project's maintainer. And, a set of developers exist who independently
develop, test, and submit changes to "contrib" areas for the maintainer
to examine. The maintainer then chooses which changes are going to
become a permanent part of the project.

.. image:: figures/git-workflow.png
   :align: center

While each development environment is unique, there are some best
practices or methods that help development run smoothly. The following
list describes some of these practices. For more information about Git
workflows, see the workflow topics in the `Git Community
Book <https://book.git-scm.com>`__.

-  *Make Small Changes:* It is best to keep the changes you commit small
   as compared to bundling many disparate changes into a single commit.
   This practice not only keeps things manageable but also allows the
   maintainer to more easily include or refuse changes.

-  *Make Complete Changes:* It is also good practice to leave the
   repository in a state that allows you to still successfully build
   your project. In other words, do not commit half of a feature, then
   add the other half as a separate, later commit. Each commit should
   take you from one buildable project state to another buildable state.

-  *Use Branches Liberally:* It is very easy to create, use, and delete
   local branches in your working Git repository on the development
   host. You can name these branches anything you like. It is helpful to
   give them names associated with the particular feature or change on
   which you are working. Once you are done with a feature or change and
   have merged it into your local master branch, simply discard the
   temporary branch.

-  *Merge Changes:* The ``git merge`` command allows you to take the
   changes from one branch and fold them into another branch. This
   process is especially helpful when more than a single developer might
   be working on different parts of the same feature. Merging changes
   also automatically identifies any collisions or "conflicts" that
   might happen as a result of the same lines of code being altered by
   two different developers.

-  *Manage Branches:* Because branches are easy to use, you should use a
   system where branches indicate varying levels of code readiness. For
   example, you can have a "work" branch to develop in, a "test" branch
   where the code or change is tested, a "stage" branch where changes
   are ready to be committed, and so forth. As your project develops,
   you can merge code across the branches to reflect ever-increasing
   stable states of the development.

-  *Use Push and Pull:* The push-pull workflow is based on the concept
   of developers "pushing" local commits to a remote repository, which
   is usually a contribution repository. This workflow is also based on
   developers "pulling" known states of the project down into their
   local development repositories. The workflow easily allows you to
   pull changes submitted by other developers from the upstream
   repository into your work area ensuring that you have the most recent
   software on which to develop. The Yocto Project has two scripts named
   ``create-pull-request`` and ``send-pull-request`` that ship with the
   release to facilitate this workflow. You can find these scripts in
   the ``scripts`` folder of the
   :term:`Source Directory`. For information
   on how to use these scripts, see the
   ":ref:`dev-manual/common-tasks:using scripts to push a change upstream and request a pull`"
   section in the Yocto Project Development Tasks Manual.

-  *Patch Workflow:* This workflow allows you to notify the maintainer
   through an email that you have a change (or patch) you would like
   considered for the "master" branch of the Git repository. To send
   this type of change, you format the patch and then send the email
   using the Git commands ``git format-patch`` and ``git send-email``.
   For information on how to use these scripts, see the
   ":ref:`dev-manual/common-tasks:submitting a change to the yocto project`"
   section in the Yocto Project Development Tasks Manual.

Git
===

The Yocto Project makes extensive use of Git, which is a free, open
source distributed version control system. Git supports distributed
development, non-linear development, and can handle large projects. It
is best that you have some fundamental understanding of how Git tracks
projects and how to work with Git if you are going to use the Yocto
Project for development. This section provides a quick overview of how
Git works and provides you with a summary of some essential Git
commands.

.. note::

   -  For more information on Git, see
      https://git-scm.com/documentation.

   -  If you need to download Git, it is recommended that you add Git to
      your system through your distribution's "software store" (e.g. for
      Ubuntu, use the Ubuntu Software feature). For the Git download
      page, see https://git-scm.com/download.

   -  For information beyond the introductory nature in this section,
      see the ":ref:`dev-manual/start:locating yocto project source files`"
      section in the Yocto Project Development Tasks Manual.

Repositories, Tags, and Branches
--------------------------------

As mentioned briefly in the previous section and also in the "`Git
Workflows and the Yocto
Project <#gs-git-workflows-and-the-yocto-project>`__" section, the Yocto
Project maintains source repositories at :yocto_git:`/`. If you
look at this web-interface of the repositories, each item is a separate
Git repository.

Git repositories use branching techniques that track content change (not
files) within a project (e.g. a new feature or updated documentation).
Creating a tree-like structure based on project divergence allows for
excellent historical information over the life of a project. This
methodology also allows for an environment from which you can do lots of
local experimentation on projects as you develop changes or new
features.

A Git repository represents all development efforts for a given project.
For example, the Git repository ``poky`` contains all changes and
developments for that repository over the course of its entire life.
That means that all changes that make up all releases are captured. The
repository maintains a complete history of changes.

You can create a local copy of any repository by "cloning" it with the
``git clone`` command. When you clone a Git repository, you end up with
an identical copy of the repository on your development system. Once you
have a local copy of a repository, you can take steps to develop
locally. For examples on how to clone Git repositories, see the
":ref:`dev-manual/start:locating yocto project source files`"
section in the Yocto Project Development Tasks Manual.

It is important to understand that Git tracks content change and not
files. Git uses "branches" to organize different development efforts.
For example, the ``poky`` repository has several branches that include
the current "&DISTRO_NAME_NO_CAP;" branch, the "master" branch, and many
branches for past Yocto Project releases. You can see all the branches
by going to :yocto_git:`/poky/` and clicking on the
``[...]`` link beneath the "Branch" heading.

Each of these branches represents a specific area of development. The
"master" branch represents the current or most recent development. All
other branches represent offshoots of the "master" branch.

When you create a local copy of a Git repository, the copy has the same
set of branches as the original. This means you can use Git to create a
local working area (also called a branch) that tracks a specific
development branch from the upstream source Git repository. in other
words, you can define your local Git environment to work on any
development branch in the repository. To help illustrate, consider the
following example Git commands:
::

   $ cd ~
   $ git clone git://git.yoctoproject.org/poky
   $ cd poky
   $ git checkout -b &DISTRO_NAME_NO_CAP; origin/&DISTRO_NAME_NO_CAP;

In the previous example
after moving to the home directory, the ``git clone`` command creates a
local copy of the upstream ``poky`` Git repository. By default, Git
checks out the "master" branch for your work. After changing the working
directory to the new local repository (i.e. ``poky``), the
``git checkout`` command creates and checks out a local branch named
"&DISTRO_NAME_NO_CAP;", which tracks the upstream
"origin/&DISTRO_NAME_NO_CAP;" branch. Changes you make while in this
branch would ultimately affect the upstream "&DISTRO_NAME_NO_CAP;" branch
of the ``poky`` repository.

It is important to understand that when you create and checkout a local
working branch based on a branch name, your local environment matches
the "tip" of that particular development branch at the time you created
your local branch, which could be different from the files in the
"master" branch of the upstream repository. In other words, creating and
checking out a local branch based on the "&DISTRO_NAME_NO_CAP;" branch
name is not the same as checking out the "master" branch in the
repository. Keep reading to see how you create a local snapshot of a
Yocto Project Release.

Git uses "tags" to mark specific changes in a repository branch
structure. Typically, a tag is used to mark a special point such as the
final change (or commit) before a project is released. You can see the
tags used with the ``poky`` Git repository by going to :yocto_git:`/poky/`
and clicking on the ``[...]`` link beneath the "Tag" heading.

Some key tags for the ``poky`` repository are ``jethro-14.0.3``,
``morty-16.0.1``, ``pyro-17.0.0``, and
``&DISTRO_NAME_NO_CAP;-&POKYVERSION;``. These tags represent Yocto Project
releases.

When you create a local copy of the Git repository, you also have access
to all the tags in the upstream repository. Similar to branches, you can
create and checkout a local working Git branch based on a tag name. When
you do this, you get a snapshot of the Git repository that reflects the
state of the files when the change was made associated with that tag.
The most common use is to checkout a working branch that matches a
specific Yocto Project release. Here is an example:
::

   $ cd ~
   $ git clone git://git.yoctoproject.org/poky
   $ cd poky
   $ git fetch --tags
   $ git checkout tags/rocko-18.0.0 -b my_rocko-18.0.0

In this example, the name
of the top-level directory of your local Yocto Project repository is
``poky``. After moving to the ``poky`` directory, the ``git fetch``
command makes all the upstream tags available locally in your
repository. Finally, the ``git checkout`` command creates and checks out
a branch named "my-rocko-18.0.0" that is based on the upstream branch
whose "HEAD" matches the commit in the repository associated with the
"rocko-18.0.0" tag. The files in your repository now exactly match that
particular Yocto Project release as it is tagged in the upstream Git
repository. It is important to understand that when you create and
checkout a local working branch based on a tag, your environment matches
a specific point in time and not the entire development branch (i.e.
from the "tip" of the branch backwards).

Basic Commands
--------------

Git has an extensive set of commands that lets you manage changes and
perform collaboration over the life of a project. Conveniently though,
you can manage with a small set of basic operations and workflows once
you understand the basic philosophy behind Git. You do not have to be an
expert in Git to be functional. A good place to look for instruction on
a minimal set of Git commands is
`here <https://git-scm.com/documentation>`__.

The following list of Git commands briefly describes some basic Git
operations as a way to get started. As with any set of commands, this
list (in most cases) simply shows the base command and omits the many
arguments it supports. See the Git documentation for complete
descriptions and strategies on how to use these commands:

-  *git init:* Initializes an empty Git repository. You cannot use
   Git commands unless you have a ``.git`` repository.

-  *git clone:* Creates a local clone of a Git repository that is on
   equal footing with a fellow developer's Git repository or an upstream
   repository.

-  *git add:* Locally stages updated file contents to the index that
   Git uses to track changes. You must stage all files that have changed
   before you can commit them.

-  *git commit:* Creates a local "commit" that documents the changes
   you made. Only changes that have been staged can be committed.
   Commits are used for historical purposes, for determining if a
   maintainer of a project will allow the change, and for ultimately
   pushing the change from your local Git repository into the project's
   upstream repository.

-  *git status:* Reports any modified files that possibly need to be
   staged and gives you a status of where you stand regarding local
   commits as compared to the upstream repository.

-  *git checkout branch-name:* Changes your local working branch and
   in this form assumes the local branch already exists. This command is
   analogous to "cd".

-  *git checkout –b working-branch upstream-branch:* Creates and
   checks out a working branch on your local machine. The local branch
   tracks the upstream branch. You can use your local branch to isolate
   your work. It is a good idea to use local branches when adding
   specific features or changes. Using isolated branches facilitates
   easy removal of changes if they do not work out.

-  *git branch:* Displays the existing local branches associated
   with your local repository. The branch that you have currently
   checked out is noted with an asterisk character.

-  *git branch -D branch-name:* Deletes an existing local branch.
   You need to be in a local branch other than the one you are deleting
   in order to delete branch-name.

-  *git pull --rebase:* Retrieves information from an upstream Git
   repository and places it in your local Git repository. You use this
   command to make sure you are synchronized with the repository from
   which you are basing changes (.e.g. the "master" branch). The
   "--rebase" option ensures that any local commits you have in your
   branch are preserved at the top of your local branch.

-  *git push repo-name local-branch:upstream-branch:* Sends
   all your committed local changes to the upstream Git repository that
   your local repository is tracking (e.g. a contribution repository).
   The maintainer of the project draws from these repositories to merge
   changes (commits) into the appropriate branch of project's upstream
   repository.

-  *git merge:* Combines or adds changes from one local branch of
   your repository with another branch. When you create a local Git
   repository, the default branch is named "master". A typical workflow
   is to create a temporary branch that is based off "master" that you
   would use for isolated work. You would make your changes in that
   isolated branch, stage and commit them locally, switch to the
   "master" branch, and then use the ``git merge`` command to apply the
   changes from your isolated branch into the currently checked out
   branch (e.g. "master"). After the merge is complete and if you are
   done with working in that isolated branch, you can safely delete the
   isolated branch.

-  *git cherry-pick commits:* Choose and apply specific commits from
   one branch into another branch. There are times when you might not be
   able to merge all the changes in one branch with another but need to
   pick out certain ones.

-  *gitk:* Provides a GUI view of the branches and changes in your
   local Git repository. This command is a good way to graphically see
   where things have diverged in your local repository.

   .. note::

      You need to install the
      gitk
      package on your development system to use this command.

-  *git log:* Reports a history of your commits to the repository.
   This report lists all commits regardless of whether you have pushed
   them upstream or not.

-  *git diff:* Displays line-by-line differences between a local
   working file and the same file as understood by Git. This command is
   useful to see what you have changed in any given file.

Licensing
=========

Because open source projects are open to the public, they have different
licensing structures in place. License evolution for both Open Source
and Free Software has an interesting history. If you are interested in
this history, you can find basic information here:

-  `Open source license
   history <https://en.wikipedia.org/wiki/Open-source_license>`__

-  `Free software license
   history <https://en.wikipedia.org/wiki/Free_software_license>`__

In general, the Yocto Project is broadly licensed under the
Massachusetts Institute of Technology (MIT) License. MIT licensing
permits the reuse of software within proprietary software as long as the
license is distributed with that software. MIT is also compatible with
the GNU General Public License (GPL). Patches to the Yocto Project
follow the upstream licensing scheme. You can find information on the
MIT license
`here <https://www.opensource.org/licenses/mit-license.php>`__. You can
find information on the GNU GPL
`here <https://www.opensource.org/licenses/LGPL-3.0>`__.

When you build an image using the Yocto Project, the build process uses
a known list of licenses to ensure compliance. You can find this list in
the :term:`Source Directory` at
``meta/files/common-licenses``. Once the build completes, the list of
all licenses found and used during that build are kept in the
:term:`Build Directory` at
``tmp/deploy/licenses``.

If a module requires a license that is not in the base list, the build
process generates a warning during the build. These tools make it easier
for a developer to be certain of the licenses with which their shipped
products must comply. However, even with these tools it is still up to
the developer to resolve potential licensing issues.

The base list of licenses used by the build process is a combination of
the Software Package Data Exchange (SPDX) list and the Open Source
Initiative (OSI) projects. `SPDX Group <https://spdx.org>`__ is a working
group of the Linux Foundation that maintains a specification for a
standard format for communicating the components, licenses, and
copyrights associated with a software package.
`OSI <https://opensource.org>`__ is a corporation dedicated to the Open
Source Definition and the effort for reviewing and approving licenses
that conform to the Open Source Definition (OSD).

You can find a list of the combined SPDX and OSI licenses that the Yocto
Project uses in the ``meta/files/common-licenses`` directory in your
:term:`Source Directory`.

For information that can help you maintain compliance with various open
source licensing during the lifecycle of a product created using the
Yocto Project, see the
":ref:`dev-manual/common-tasks:maintaining open source license compliance during your product's lifecycle`"
section in the Yocto Project Development Tasks Manual.
