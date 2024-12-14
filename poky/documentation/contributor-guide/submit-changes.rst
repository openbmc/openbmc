.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Contributing Changes to a Component
************************************

Contributions to the Yocto Project and OpenEmbedded are very welcome.
Because the system is extremely configurable and flexible, we recognize
that developers will want to extend, configure or optimize it for their
specific uses.

.. _ref-why-mailing-lists:

Contributing through mailing lists --- Why not using web-based workflows?
=========================================================================

Both Yocto Project and OpenEmbedded have many key components that are
maintained by patches being submitted on mailing lists. We appreciate this
approach does look a little old fashioned when other workflows are available
through web technology such as GitHub, GitLab and others. Since we are often
asked this question, we’ve decided to document the reasons for using mailing
lists.

One significant factor is that we value peer review. When a change is proposed
to many of the core pieces of the project, it helps to have many eyes of review
go over them. Whilst there is ultimately one maintainer who needs to make the
final call on accepting or rejecting a patch, the review is made by many eyes
and the exact people reviewing it are likely unknown to the maintainer. It is
often the surprise reviewer that catches the most interesting issues!

This is in contrast to the "GitHub" style workflow where either just a
maintainer makes that review, or review is specifically requested from
nominated people. We believe there is significant value added to the codebase
by this peer review and that moving away from mailing lists would be to the
detriment of our code.

We also need to acknowledge that many of our developers are used to this
mailing list workflow and have worked with it for years, with tools and
processes built around it. Changing away from this would result in a loss
of key people from the project, which would again be to its detriment.

The projects are acutely aware that potential new contributors find the
mailing list approach off-putting and would prefer a web-based GUI.
Since we don’t believe that can work for us, the project is aiming to ensure
`patchwork <https://patchwork.yoctoproject.org/>`__ is available to help track
patch status and also looking at how tooling can provide more feedback to users
about patch status. We are looking at improving tools such as ``patchtest`` to
test user contributions before they hit the mailing lists and also at better
documenting how to use such workflows since we recognise that whilst this was
common knowledge a decade ago, it might not be as familiar now.

Preparing Changes for Submission
================================

Set up Git
----------

The first thing to do is to install Git packages. Here is an example
on Debian and Ubuntu::

   sudo apt install git-core git-email

Then, you need to set a name and e-mail address that Git will
use to identify your commits::

   git config --global user.name "Ada Lovelace"
   git config --global user.email "ada.lovelace@gmail.com"

Clone the Git repository for the component to modify
----------------------------------------------------

After identifying the component to modify as described in the
":doc:`../contributor-guide/identify-component`" section, clone the
corresponding Git repository. Here is an example for OpenEmbedded-Core::

  git clone https://git.openembedded.org/openembedded-core
  cd openembedded-core

Create a new branch
-------------------

Then, create a new branch in your local Git repository
for your changes, starting from the reference branch in the upstream
repository (often called ``master``)::

   $ git checkout <ref-branch>
   $ git checkout -b my-changes

If you have completely unrelated sets of changes to submit, you should even
create one branch for each set.

Implement and commit changes
----------------------------

In each branch, you should group your changes into small, controlled and
isolated ones. Keeping changes small and isolated aids review, makes
merging/rebasing easier and keeps the change history clean should anyone need
to refer to it in future.

To this purpose, you should create *one Git commit per change*,
corresponding to each of the patches you will eventually submit.
See `further guidance <https://www.kernel.org/doc/html/latest/process/submitting-patches.html#separate-your-changes>`__
in the Linux kernel documentation if needed.

For example, when you intend to add multiple new recipes, each recipe
should be added in a separate commit. For upgrades to existing recipes,
the previous version should usually be deleted as part of the same commit
to add the upgraded version.

#. *Stage Your Changes:* Stage your changes by using the ``git add``
   command on each file you modified. If you want to stage all the
   files you modified, you can even use the ``git add -A`` command.

#. *Commit Your Changes:* This is when you can create separate commits. For
   each commit to create, use the ``git commit -s`` command with the files
   or directories you want to include in the commit::

      $ git commit -s file1 file2 dir1 dir2 ...

   To include **a**\ ll staged files::

      $ git commit -sa

   -  The ``-s`` option of ``git commit`` adds a "Signed-off-by:" line
      to your commit message. There is the same requirement for contributing
      to the Linux kernel. Adding such a line signifies that you, the
      submitter, have agreed to the `Developer's Certificate of Origin 1.1
      <https://www.kernel.org/doc/html/latest/process/submitting-patches.html#sign-your-work-the-developer-s-certificate-of-origin>`__
      as follows:

      .. code-block:: none

         Developer's Certificate of Origin 1.1

         By making a contribution to this project, I certify that:

         (a) The contribution was created in whole or in part by me and I
             have the right to submit it under the open source license
             indicated in the file; or

         (b) The contribution is based upon previous work that, to the best
             of my knowledge, is covered under an appropriate open source
             license and I have the right under that license to submit that
             work with modifications, whether created in whole or in part
             by me, under the same open source license (unless I am
             permitted to submit under a different license), as indicated
             in the file; or

         (c) The contribution was provided directly to me by some other
             person who certified (a), (b) or (c) and I have not modified
             it.

         (d) I understand and agree that this project and the contribution
             are public and that a record of the contribution (including all
             personal information I submit with it, including my sign-off) is
             maintained indefinitely and may be redistributed consistent with
             this project or the open source license(s) involved.

   -  Provide a single-line summary of the change and, if more
      explanation is needed, provide more detail in the body of the
      commit. This summary is typically viewable in the "shortlist" of
      changes. Thus, providing something short and descriptive that
      gives the reader a summary of the change is useful when viewing a
      list of many commits. You should prefix this short description
      with the recipe name (if changing a recipe), or else with the
      short form path to the file being changed.

      .. note::

         To find a suitable prefix for the commit summary, a good idea
         is to look for prefixes used in previous commits touching the
         same files or directories::

            git log --oneline <paths>

   -  For the body of the commit message, provide detailed information
      that describes what you changed, why you made the change, and the
      approach you used. It might also be helpful if you mention how you
      tested the change. Provide as much detail as you can in the body
      of the commit message.

      .. note::

         If the single line summary is enough to describe a simple
         change, the body of the commit message can be left empty.

   -  If the change addresses a specific bug or issue that is associated
      with a bug-tracking ID, include a reference to that ID in your
      detailed description. For example, the Yocto Project uses a
      specific convention for bug references --- any commit that addresses
      a specific bug should use the following form for the detailed
      description. Be sure to use the actual bug-tracking ID from
      Bugzilla for bug-id::

         Fixes [YOCTO #bug-id]

         detailed description of change

#. *Crediting contributors:* By using the ``git commit --amend`` command,
   you can add some tags to the commit description to credit other contributors
   to the change:

   -  ``Reported-by``: name and email of a person reporting a bug
      that your commit is trying to fix. This is a good practice
      to encourage people to go on reporting bugs and let them
      know that their reports are taken into account.

   -  ``Suggested-by``: name and email of a person to credit for the
      idea of making the change.

   -  ``Tested-by``, ``Reviewed-by``: name and email for people having
      tested your changes or reviewed their code. These fields are
      usually added by the maintainer accepting a patch, or by
      yourself if you submitted your patches to early reviewers,
      or are submitting an unmodified patch again as part of a
      new iteration of your patch series.

   -  ``CC:`` Name and email of people you want to send a copy
      of your changes to. This field will be used by ``git send-email``.

   See `more guidance about using such tags
   <https://www.kernel.org/doc/html/latest/process/submitting-patches.html#using-reported-by-tested-by-reviewed-by-suggested-by-and-fixes>`__
   in the Linux kernel documentation.

Test your changes
-----------------

For each contributions you make, you should test your changes as well.
For this the Yocto Project offers several types of tests. Those tests cover
different areas and it depends on your changes which are feasible. For example run:

   -  For changes that affect the build environment:

      -  ``bitbake-selftest``: for changes within BitBake

      -  ``oe-selftest``: to test combinations of BitBake runs

      -  ``oe-build-perf-test``: to test the performance of common build scenarios

   -  For changes in a recipe:

      - ``ptest``: run package specific tests, if they exist

      - ``testimage``: build an image, boot it and run testcases on it

      - If applicable, ensure also the ``native`` and ``nativesdk`` variants builds

   -  For changes relating to the SDK:

      - ``testsdk``: to build, install and run tests against a SDK

      - ``testsdk_ext``: to build, install and run tests against an extended SDK

Note that this list just gives suggestions and is not exhaustive. More details can
be found here: :ref:`test-manual/intro:Yocto Project Tests --- Types of Testing Overview`.

Creating Patches
================

Here is the general procedure on how to create patches to be sent through email:

#. *Describe the Changes in your Branch:* If you have more than one commit
   in your branch, it's recommended to provide a cover letter describing
   the series of patches you are about to send.

   For this purpose, a good solution is to store the cover letter contents
   in the branch itself::

      git branch --edit-description

   This will open a text editor to fill in the description for your
   changes. This description can be updated when necessary and will
   be used by Git to create the cover letter together with the patches.

   It is recommended to start this description with a title line which
   will serve a the subject line for the cover letter.

#. *Generate Patches for your Branch:* The ``git format-patch`` command will
   generate patch files for each of the commits in your branch. You need
   to pass the reference branch your branch starts from.

   If you branch didn't need a description in the previous step::

      $ git format-patch <ref-branch>

   If you filled a description for your branch, you will want to generate
   a cover letter too::

      $ git format-patch --cover-letter --cover-from-description=auto <ref-branch>

   After the command is run, the current directory contains numbered
   ``.patch`` files for the commits in your branch. If you have a cover
   letter, it will be in the ``0000-cover-letter.patch``.

   .. note::

      The ``--cover-from-description=auto`` option makes ``git format-patch``
      use the first paragraph of the branch description as the cover
      letter title. Another possibility, which is easier to remember, is to pass
      only the ``--cover-letter`` option, but you will have to edit the
      subject line manually every time you generate the patches.

      See the `git format-patch manual page <https://git-scm.com/docs/git-format-patch>`__
      for details.

#. *Review each of the Patch Files:* This final review of the patches
   before sending them often allows to view your changes from a different
   perspective and discover defects such as typos, spacing issues or lines
   or even files that you didn't intend to modify. This review should
   include the cover letter patch too.

   If necessary, rework your commits as described in
   ":ref:`contributor-guide/submit-changes:taking patch review into account`".

Validating Patches with Patchtest
=================================

``patchtest`` is available in ``openembedded-core`` as a tool for making
sure that your patches are well-formatted and contain important info for
maintenance purposes, such as ``Signed-off-by`` and ``Upstream-Status``
tags. Note that no functional testing of the changes will be performed by ``patchtest``.
Currently, it only supports testing patches for ``openembedded-core`` branches.
To setup, perform the following::

    pip install -r meta/lib/patchtest/requirements.txt
    source oe-init-build-env
    bitbake-layers add-layer ../meta-selftest

Once these steps are complete and you have generated your patch files,
you can run ``patchtest`` like so::

    patchtest --patch <patch_name>

Alternatively, if you want ``patchtest`` to iterate over and test
multiple patches stored in a directory, you can use::

    patchtest --directory <directory_name>

By default, ``patchtest`` uses its own modules' file paths to determine what
repository and test suite to check patches against. If you wish to test
patches against a repository other than ``openembedded-core`` and/or use
a different set of tests, you can use the ``--repodir`` and ``--testdir``
flags::

    patchtest --patch <patch_name> --repodir <path/to/repo> --testdir <path/to/testdir>

Finally, note that ``patchtest`` is designed to test patches in a standalone
way, so if your patches are meant to apply on top of changes made by
previous patches in a series, it is possible that ``patchtest`` will report
false failures regarding the "merge on head" test.

Using ``patchtest`` in this manner provides a final check for the overall
quality of your changes before they are submitted for review by the
maintainers.

Sending the Patches via Email
=============================

Using Git to Send Patches
-------------------------

To submit patches through email, it is very important that you send them
without any whitespace or HTML formatting that either you or your mailer
introduces. The maintainer that receives your patches needs to be able
to save and apply them directly from your emails, using the ``git am``
command.

Using the ``git send-email`` command is the only error-proof way of sending
your patches using email since there is no risk of compromising whitespace
in the body of the message, which can occur when you use your own mail
client. It will also properly include your patches as *inline attachments*,
which is not easy to do with standard e-mail clients without breaking lines.
If you used your regular e-mail client and shared your patches as regular
attachments, reviewers wouldn't be able to quote specific sections of your
changes and make comments about them.

Setting up Git to Send Email
----------------------------

The ``git send-email`` command can send email by using a local or remote
Mail Transport Agent (MTA) such as ``msmtp``, ``sendmail``, or
through a direct SMTP configuration in your Git ``~/.gitconfig`` file.

Here are the settings for letting ``git send-email`` send e-mail through your
regular STMP server, using a Google Mail account as an example::

   git config --global sendemail.smtpserver smtp.gmail.com
   git config --global sendemail.smtpserverport 587
   git config --global sendemail.smtpencryption tls
   git config --global sendemail.smtpuser ada.lovelace@gmail.com
   git config --global sendemail.smtppass = XXXXXXXX

These settings will appear in the ``.gitconfig`` file in your home directory.

If you neither can use a local MTA nor SMTP,  make sure you use an email client
that does not touch the message (turning spaces in tabs, wrapping lines, etc.).
A good mail client to do so is Pine (or Alpine) or Mutt. For more
information about suitable clients, see `Email clients info for Linux
<https://www.kernel.org/doc/html/latest/process/email-clients.html>`__
in the Linux kernel sources.

If you use such clients, just include the patch in the body of your email.

Finding a Suitable Mailing List
-------------------------------

You should send patches to the appropriate mailing list so that they can be
reviewed by the right contributors and merged by the appropriate maintainer.
The specific mailing list you need to use depends on the location of the code
you are changing.

If people have concerns with any of the patches, they will usually voice
their concern over the mailing list. If patches do not receive any negative
reviews, the maintainer of the affected layer typically takes them, tests them,
and then based on successful testing, merges them.

In general, each component (e.g. layer) should have a ``README`` file
that indicates where to send the changes and which process to follow.

The "poky" repository, which is the Yocto Project's reference build
environment, is a hybrid repository that contains several individual
pieces (e.g. BitBake, Metadata, documentation, and so forth) built using
the combo-layer tool. The upstream location used for submitting changes
varies by component:

-  *Core Metadata:* Send your patches to the
   :oe_lists:`openembedded-core </g/openembedded-core>`
   mailing list. For example, a change to anything under the ``meta`` or
   ``scripts`` directories should be sent to this mailing list.

-  *BitBake:* For changes to BitBake (i.e. anything under the
   ``bitbake`` directory), send your patches to the
   :oe_lists:`bitbake-devel </g/bitbake-devel>`
   mailing list.

-  *meta-poky* and *meta-yocto-bsp* trees: These trees contain Metadata. Use the
   :yocto_lists:`poky </g/poky>` mailing list.

-  *Documentation*: For changes to the Yocto Project documentation, use the
   :yocto_lists:`docs </g/docs>` mailing list.

For changes to other layers and tools hosted in the Yocto Project source
repositories (i.e. :yocto_git:`git.yoctoproject.org <>`), use the
:yocto_lists:`yocto-patches </g/yocto-patches/>` general mailing list.

For changes to other layers hosted in the OpenEmbedded source
repositories (i.e. :oe_git:`git.openembedded.org <>`), use
the :oe_lists:`openembedded-devel </g/openembedded-devel>`
mailing list, unless specified otherwise in the layer's ``README`` file.

If you intend to submit a new recipe that neither fits into the core Metadata,
nor into :oe_git:`meta-openembedded </meta-openembedded/>`, you should
look for a suitable layer in https://layers.openembedded.org. If similar
recipes can be expected, you may consider :ref:`dev-manual/layers:creating your own layer`.

If in doubt, please ask on the :yocto_lists:`yocto </g/yocto/>` general mailing list
or on the :oe_lists:`openembedded-devel </g/openembedded-devel>` mailing list.

Subscribing to the Mailing List
-------------------------------

After identifying the right mailing list to use, you will have to subscribe to
it if you haven't done it yet.

If you attempt to send patches to a list you haven't subscribed to, your email
will be returned as undelivered.

However, if you don't want to be receive all the messages sent to a mailing list,
you can set your subscription to "no email". You will still be a subscriber able
to send messages, but you won't receive any e-mail. If people reply to your message,
their e-mail clients will default to including your email address in the
conversation anyway.

Anyway, you'll also be able to access the new messages on mailing list archives,
either through a web browser, or for the lists archived on https://lore.kernel.org,
through an individual newsgroup feed or a git repository.

Sending Patches via Email
-------------------------

At this stage, you are ready to send your patches via email. Here's the
typical usage of ``git send-email``::

   git send-email --to <mailing-list-address> *.patch

Then, review each subject line and list of recipients carefully, and then
allow the command to send each message.

You will see that ``git send-email`` will automatically copy the people listed
in any commit tags such as ``Signed-off-by`` or ``Reported-by``.

In case you are sending patches for :oe_git:`meta-openembedded </meta-openembedded/>`
or any layer other than :oe_git:`openembedded-core </openembedded-core/>`,
please add the appropriate prefix so that it is clear which layer the patch is intended
to be applied to::

   git format-patch --subject-prefix="meta-oe][PATCH" ...

.. note::

   It is actually possible to send patches without generating them
   first. However, make sure you have reviewed your changes carefully
   because ``git send-email`` will just show you the title lines of
   each patch.

   Here's a command you can use if you just have one patch in your
   branch::

      git send-email --to <mailing-list-address> -1

   If you have multiple patches and a cover letter, you can send
   patches for all the commits between the reference branch
   and the tip of your branch::

      git send-email --cover-letter --cover-from-description=auto --to <mailing-list-address> -M <ref-branch>

See the `git send-email manual page <https://git-scm.com/docs/git-send-email>`__
for details.

Troubleshooting Email Issues
----------------------------

Fixing your From identity
~~~~~~~~~~~~~~~~~~~~~~~~~

We have a frequent issue with contributors whose patches are received through
a ``From`` field which doesn't match the ``Signed-off-by`` information. Here is
a typical example for people sending from a domain name with :wikipedia:`DMARC`::

   From: "Linus Torvalds via lists.openembedded.org <linus.torvalds=kernel.org@lists.openembedded.org>"

This ``From`` field is used by ``git am`` to recreate commits with the right
author name. The following will ensure that your e-mails have an additional
``From`` field at the beginning of the Email body, and therefore that
maintainers accepting your patches don't have to fix commit author information
manually::

   git config --global sendemail.from "linus.torvalds@kernel.org"

The ``sendemail.from`` should match your ``user.email`` setting,
which appears in the ``Signed-off-by`` line of your commits.

Streamlining git send-email usage
---------------------------------

If you want to save time and not be forced to remember the right options to use
with ``git send-email``, you can use Git configuration settings.

-  To set the right mailing list address for a given repository::

      git config --local sendemail.to openembedded-devel@lists.openembedded.org

-  If the mailing list requires a subject prefix for the layer
   (this only works when the repository only contains one layer)::

      git config --local format.subjectprefix "meta-something][PATCH"

Using Scripts to Push a Change Upstream and Request a Pull
==========================================================

For larger patch series it is preferable to send a pull request which not
only includes the patch but also a pointer to a branch that can be pulled
from. This involves making a local branch for your changes, pushing this
branch to an accessible repository and then using the ``create-pull-request``
and ``send-pull-request`` scripts from openembedded-core to create and send a
patch series with a link to the branch for review.

Follow this procedure to push a change to an upstream "contrib" Git
repository once the steps in
":ref:`contributor-guide/submit-changes:preparing changes for submission`"
have been followed:

.. note::

   You can find general Git information on how to push a change upstream
   in the
   `Git Community Book <https://git-scm.com/book/en/v2/Distributed-Git-Distributed-Workflows>`__.

#. *Request Push Access to an "Upstream" Contrib Repository:* Send an email to
   ``helpdesk@yoctoproject.org``:

    -  Attach your SSH public key which usually named ``id_rsa.pub.``.
       If you don't have one generate it by running ``ssh-keygen -t rsa -b 4096 -C "your_email@example.com"``.

    -  List the repositories you're planning to contribute to.

    -  Include your preferred branch prefix for ``-contrib`` repositories.

#. *Push Your Commits to the "Contrib" Upstream:* Push your
   changes to that repository::

      $ git push upstream_remote_repo local_branch_name

   For example, suppose you have permissions to push
   into the upstream ``meta-intel-contrib`` repository and you are
   working in a local branch named `your_name`\ ``/README``. The following
   command pushes your local commits to the ``meta-intel-contrib``
   upstream repository and puts the commit in a branch named
   `your_name`\ ``/README``::

      $ git push meta-intel-contrib your_name/README

#. *Determine Who to Notify:* Determine the maintainer or the mailing
   list that you need to notify for the change.

   Before submitting any change, you need to be sure who the maintainer
   is or what mailing list that you need to notify. Use either these
   methods to find out:

   -  *Maintenance File:* Examine the ``maintainers.inc`` file, which is
      located in the :term:`Source Directory` at
      ``meta/conf/distro/include``, to see who is responsible for code.

   -  *Search by File:* Using :ref:`overview-manual/development-environment:git`, you can
      enter the following command to bring up a short list of all
      commits against a specific file::

         git shortlog -- filename

      Just provide the name of the file for which you are interested. The
      information returned is not ordered by history but does include a
      list of everyone who has committed grouped by name. From the list,
      you can see who is responsible for the bulk of the changes against
      the file.

   -  *Find the Mailing List to Use:* See the
      ":ref:`contributor-guide/submit-changes:finding a suitable mailing list`"
      section above.

#. *Make a Pull Request:* Notify the maintainer or the mailing list that
   you have pushed a change by making a pull request.

   The Yocto Project provides two scripts that conveniently let you
   generate and send pull requests to the Yocto Project. These scripts
   are ``create-pull-request`` and ``send-pull-request``. You can find
   these scripts in the ``scripts`` directory within the
   :term:`Source Directory` (e.g.
   ``poky/scripts``).

   Using these scripts correctly formats the requests without
   introducing any whitespace or HTML formatting. The maintainer that
   receives your patches either directly or through the mailing list
   needs to be able to save and apply them directly from your emails.
   Using these scripts is the preferred method for sending patches.

   First, create the pull request. For example, the following command
   runs the script, specifies the upstream repository in the contrib
   directory into which you pushed the change, and provides a subject
   line in the created patch files::

      $ poky/scripts/create-pull-request -u meta-intel-contrib -s "Updated Manual Section Reference in README"

   Running this script forms ``*.patch`` files in a folder named
   ``pull-``\ `PID` in the current directory. One of the patch files is a
   cover letter.

   Before running the ``send-pull-request`` script, you must edit the
   cover letter patch to insert information about your change. After
   editing the cover letter, send the pull request. For example, the
   following command runs the script and specifies the patch directory
   and email address. In this example, the email address is a mailing
   list::

      $ poky/scripts/send-pull-request -p ~/meta-intel/pull-10565 -t meta-intel@lists.yoctoproject.org

   You need to follow the prompts as the script is interactive.

   .. note::

      For help on using these scripts, simply provide the ``-h``
      argument as follows::

              $ poky/scripts/create-pull-request -h
              $ poky/scripts/send-pull-request -h

Submitting Changes to Stable Release Branches
=============================================

The process for proposing changes to a Yocto Project stable branch differs
from the steps described above. Changes to a stable branch must address
identified bugs or CVEs and should be made carefully in order to avoid the
risk of introducing new bugs or breaking backwards compatibility. Typically
bug fixes must already be accepted into the master branch before they can be
backported to a stable branch unless the bug in question does not affect the
master branch or the fix on the master branch is unsuitable for backporting.

The list of stable branches along with the status and maintainer for each
branch can be obtained from the
:yocto_wiki:`Releases wiki page </Releases>`.

.. note::

   Changes will not typically be accepted for branches which are marked as
   End-Of-Life (EOL).

With this in mind, the steps to submit a change for a stable branch are as
follows:

#. *Identify the bug or CVE to be fixed:* This information should be
   collected so that it can be included in your submission.

   See :ref:`dev-manual/vulnerabilities:checking for vulnerabilities`
   for details about CVE tracking.

#. *Check if the fix is already present in the master branch:* This will
   result in the most straightforward path into the stable branch for the
   fix.

   #. *If the fix is present in the master branch --- submit a backport request
      by email:* You should send an email to the relevant stable branch
      maintainer and the mailing list with details of the bug or CVE to be
      fixed, the commit hash on the master branch that fixes the issue and
      the stable branches which you would like this fix to be backported to.

   #. *If the fix is not present in the master branch --- submit the fix to the
      master branch first:* This will ensure that the fix passes through the
      project's usual patch review and test processes before being accepted.
      It will also ensure that bugs are not left unresolved in the master
      branch itself. Once the fix is accepted in the master branch a backport
      request can be submitted as above.

   #. *If the fix is unsuitable for the master branch --- submit a patch
      directly for the stable branch:* This method should be considered as a
      last resort. It is typically necessary when the master branch is using
      a newer version of the software which includes an upstream fix for the
      issue or when the issue has been fixed on the master branch in a way
      that introduces backwards incompatible changes. In this case follow the
      steps in ":ref:`contributor-guide/submit-changes:preparing changes for submission`"
      and in the following sections but modify the subject header of your patch
      email to include the name of the stable branch which you are
      targetting. This can be done using the ``--subject-prefix`` argument to
      ``git format-patch``, for example to submit a patch to the
      "&DISTRO_NAME_NO_CAP_MINUS_ONE;" branch use::

         git format-patch --subject-prefix='&DISTRO_NAME_NO_CAP_MINUS_ONE;][PATCH' ...

Taking Patch Review into Account
================================

You may get feedback on your submitted patches from other community members
or from the automated patchtest service. If issues are identified in your
patches then it is usually necessary to address these before the patches are
accepted into the project. In this case you should your commits according
to the feedback and submit an updated version to the relevant mailing list.

In any case, never fix reported issues by fixing them in new commits
on the tip of your branch. Always come up with a new series of commits
without the reported issues.

.. note::

   It is a good idea to send a copy to the reviewers who provided feedback
   to the previous version of the patch. You can make sure this happens
   by adding a ``CC`` tag to the commit description::

      CC: William Shakespeare <bill@yoctoproject.org>

A single patch can be amended using ``git commit --amend``, and multiple
patches can be easily reworked and reordered through an interactive Git rebase::

   git rebase -i <ref-branch>

See `this tutorial <https://hackernoon.com/beginners-guide-to-interactive-rebasing-346a3f9c3a6d>`__
for practical guidance about using Git interactive rebasing.

You should also modify the ``[PATCH]`` tag in the email subject line when
sending the revised patch to mark the new iteration as ``[PATCH v2]``,
``[PATCH v3]``, etc as appropriate. This can be done by passing the ``-v``
argument to ``git format-patch`` with a version number::

   git format-patch -v2 <ref-branch>

Lastly please ensure that you also test your revised changes. In particular
please don't just edit the patch file written out by ``git format-patch`` and
resend it.

Tracking the Status of Patches
==============================

The Yocto Project uses a `Patchwork instance <https://patchwork.yoctoproject.org/>`__
to track the status of patches submitted to the various mailing lists and to
support automated patch testing. Each submitted patch is checked for common
mistakes and deviations from the expected patch format and submitters are
notified by ``patchtest`` if such mistakes are found. This process helps to
reduce the burden of patch review on maintainers.

.. note::

   This system is imperfect and changes can sometimes get lost in the flow.
   Asking about the status of a patch or change is reasonable if the change
   has been idle for a while with no feedback.

If your patches have not had any feedback in a few days, they may have already
been merged. You can run ``git pull``  branch to check this. Note that many if
not most layer maintainers do not send out acknowledgement emails when they
accept patches. Alternatively, if there is no response or merge after a few days
the patch may have been missed or the appropriate reviewers may not currently be
around. It is then perfectly fine to reply to it yourself with a reminder asking
for feedback.

.. note::

      Patch reviews for feature and recipe upgrade patches are likely be delayed
      during a feature freeze because these types of patches aren't merged during
      at that time --- you may have to wait until after the freeze is lifted.

Maintainers also commonly use ``-next`` branches to test submissions prior to
merging patches. Thus, you can get an idea of the status of a patch based on
whether the patch has been merged into one of these branches. The commonly
used testing branches for OpenEmbedded-Core are as follows:

-  *openembedded-core "master-next" branch:* This branch is part of the
   :oe_git:`openembedded-core </openembedded-core/>` repository and contains
   proposed changes to the core metadata.

-  *poky "master-next" branch:* This branch is part of the
   :yocto_git:`poky </poky/>` repository and combines proposed
   changes to BitBake, the core metadata and the poky distro.

Similarly, stable branches maintained by the project may have corresponding
``-next`` branches which collect proposed changes. For example,
``&DISTRO_NAME_NO_CAP;-next`` and ``&DISTRO_NAME_NO_CAP_MINUS_ONE;-next``
branches in both the "openembdedded-core" and "poky" repositories.

Other layers may have similar testing branches but there is no formal
requirement or standard for these so please check the documentation for the
layers you are contributing to.

