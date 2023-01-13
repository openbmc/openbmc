.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Making Changes to the Yocto Project
***********************************

Because the Yocto Project is an open-source, community-based project,
you can effect changes to the project. This section presents procedures
that show you how to submit a defect against the project and how to
submit a change.

Submitting a Defect Against the Yocto Project
=============================================

Use the Yocto Project implementation of
`Bugzilla <https://www.bugzilla.org/about/>`__ to submit a defect (bug)
against the Yocto Project. For additional information on this
implementation of Bugzilla see the ":ref:`Yocto Project
Bugzilla <resources-bugtracker>`" section in the
Yocto Project Reference Manual. For more detail on any of the following
steps, see the Yocto Project
:yocto_wiki:`Bugzilla wiki page </Bugzilla_Configuration_and_Bug_Tracking>`.

Use the following general steps to submit a bug:

#.  Open the Yocto Project implementation of :yocto_bugs:`Bugzilla <>`.

#.  Click "File a Bug" to enter a new bug.

#.  Choose the appropriate "Classification", "Product", and "Component"
    for which the bug was found. Bugs for the Yocto Project fall into
    one of several classifications, which in turn break down into
    several products and components. For example, for a bug against the
    ``meta-intel`` layer, you would choose "Build System, Metadata &
    Runtime", "BSPs", and "bsps-meta-intel", respectively.

#.  Choose the "Version" of the Yocto Project for which you found the
    bug (e.g. &DISTRO;).

#.  Determine and select the "Severity" of the bug. The severity
    indicates how the bug impacted your work.

#.  Choose the "Hardware" that the bug impacts.

#.  Choose the "Architecture" that the bug impacts.

#.  Choose a "Documentation change" item for the bug. Fixing a bug might
    or might not affect the Yocto Project documentation. If you are
    unsure of the impact to the documentation, select "Don't Know".

#.  Provide a brief "Summary" of the bug. Try to limit your summary to
    just a line or two and be sure to capture the essence of the bug.

#.  Provide a detailed "Description" of the bug. You should provide as
    much detail as you can about the context, behavior, output, and so
    forth that surrounds the bug. You can even attach supporting files
    for output from logs by using the "Add an attachment" button.

#.  Click the "Submit Bug" button submit the bug. A new Bugzilla number
    is assigned to the bug and the defect is logged in the bug tracking
    system.

Once you file a bug, the bug is processed by the Yocto Project Bug
Triage Team and further details concerning the bug are assigned (e.g.
priority and owner). You are the "Submitter" of the bug and any further
categorization, progress, or comments on the bug result in Bugzilla
sending you an automated email concerning the particular change or
progress to the bug.

Submitting a Change to the Yocto Project
========================================

Contributions to the Yocto Project and OpenEmbedded are very welcome.
Because the system is extremely configurable and flexible, we recognize
that developers will want to extend, configure or optimize it for their
specific uses.

The Yocto Project uses a mailing list and a patch-based workflow that is
similar to the Linux kernel but contains important differences. In
general, there is a mailing list through which you can submit patches. You
should send patches to the appropriate mailing list so that they can be
reviewed and merged by the appropriate maintainer. The specific mailing
list you need to use depends on the location of the code you are
changing. Each component (e.g. layer) should have a ``README`` file that
indicates where to send the changes and which process to follow.

You can send the patch to the mailing list using whichever approach you
feel comfortable with to generate the patch. Once sent, the patch is
usually reviewed by the community at large. If somebody has concerns
with the patch, they will usually voice their concern over the mailing
list. If a patch does not receive any negative reviews, the maintainer
of the affected layer typically takes the patch, tests it, and then
based on successful testing, merges the patch.

The "poky" repository, which is the Yocto Project's reference build
environment, is a hybrid repository that contains several individual
pieces (e.g. BitBake, Metadata, documentation, and so forth) built using
the combo-layer tool. The upstream location used for submitting changes
varies by component:

-  *Core Metadata:* Send your patch to the
   :oe_lists:`openembedded-core </g/openembedded-core>`
   mailing list. For example, a change to anything under the ``meta`` or
   ``scripts`` directories should be sent to this mailing list.

-  *BitBake:* For changes to BitBake (i.e. anything under the
   ``bitbake`` directory), send your patch to the
   :oe_lists:`bitbake-devel </g/bitbake-devel>`
   mailing list.

-  *"meta-\*" trees:* These trees contain Metadata. Use the
   :yocto_lists:`poky </g/poky>` mailing list.

-  *Documentation*: For changes to the Yocto Project documentation, use the
   :yocto_lists:`docs </g/docs>` mailing list.

For changes to other layers hosted in the Yocto Project source
repositories (i.e. ``yoctoproject.org``) and tools use the
:yocto_lists:`Yocto Project </g/yocto/>` general mailing list.

.. note::

   Sometimes a layer's documentation specifies to use a particular
   mailing list. If so, use that list.

For additional recipes that do not fit into the core Metadata, you
should determine which layer the recipe should go into and submit the
change in the manner recommended by the documentation (e.g. the
``README`` file) supplied with the layer. If in doubt, please ask on the
Yocto general mailing list or on the openembedded-devel mailing list.

You can also push a change upstream and request a maintainer to pull the
change into the component's upstream repository. You do this by pushing
to a contribution repository that is upstream. See the
":ref:`overview-manual/development-environment:git workflows and the yocto project`"
section in the Yocto Project Overview and Concepts Manual for additional
concepts on working in the Yocto Project development environment.

Maintainers commonly use ``-next`` branches to test submissions prior to
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

The following sections provide procedures for submitting a change.

Preparing Changes for Submission
--------------------------------

#. *Make Your Changes Locally:* Make your changes in your local Git
   repository. You should make small, controlled, isolated changes.
   Keeping changes small and isolated aids review, makes
   merging/rebasing easier and keeps the change history clean should
   anyone need to refer to it in future.

#. *Stage Your Changes:* Stage your changes by using the ``git add``
   command on each file you changed.

#. *Commit Your Changes:* Commit the change by using the ``git commit``
   command. Make sure your commit information follows standards by
   following these accepted conventions:

   -  Be sure to include a "Signed-off-by:" line in the same style as
      required by the Linux kernel. This can be done by using the
      ``git commit -s`` command. Adding this line signifies that you,
      the submitter, have agreed to the Developer's Certificate of
      Origin 1.1 as follows:

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

   -  For the body of the commit message, provide detailed information
      that describes what you changed, why you made the change, and the
      approach you used. It might also be helpful if you mention how you
      tested the change. Provide as much detail as you can in the body
      of the commit message.

      .. note::

         You do not need to provide a more detailed explanation of a
         change if the change is minor to the point of the single line
         summary providing all the information.

   -  If the change addresses a specific bug or issue that is associated
      with a bug-tracking ID, include a reference to that ID in your
      detailed description. For example, the Yocto Project uses a
      specific convention for bug references --- any commit that addresses
      a specific bug should use the following form for the detailed
      description. Be sure to use the actual bug-tracking ID from
      Bugzilla for bug-id::

         Fixes [YOCTO #bug-id]

         detailed description of change

Using Email to Submit a Patch
-----------------------------

Depending on the components changed, you need to submit the email to a
specific mailing list. For some guidance on which mailing list to use,
see the
:ref:`list <dev-manual/changes:submitting a change to the yocto project>`
at the beginning of this section. For a description of all the available
mailing lists, see the ":ref:`Mailing Lists <resources-mailinglist>`" section in the
Yocto Project Reference Manual.

Here is the general procedure on how to submit a patch through email
without using the scripts once the steps in
:ref:`dev-manual/changes:preparing changes for submission` have been followed:

#. *Format the Commit:* Format the commit into an email message. To
   format commits, use the ``git format-patch`` command. When you
   provide the command, you must include a revision list or a number of
   patches as part of the command. For example, either of these two
   commands takes your most recent single commit and formats it as an
   email message in the current directory::

      $ git format-patch -1

   or ::

      $ git format-patch HEAD~

   After the command is run, the current directory contains a numbered
   ``.patch`` file for the commit.

   If you provide several commits as part of the command, the
   ``git format-patch`` command produces a series of numbered files in
   the current directory – one for each commit. If you have more than
   one patch, you should also use the ``--cover`` option with the
   command, which generates a cover letter as the first "patch" in the
   series. You can then edit the cover letter to provide a description
   for the series of patches. For information on the
   ``git format-patch`` command, see ``GIT_FORMAT_PATCH(1)`` displayed
   using the ``man git-format-patch`` command.

   .. note::

      If you are or will be a frequent contributor to the Yocto Project
      or to OpenEmbedded, you might consider requesting a contrib area
      and the necessary associated rights.

#. *Send the patches via email:* Send the patches to the recipients and
   relevant mailing lists by using the ``git send-email`` command.

   .. note::

      In order to use ``git send-email``, you must have the proper Git packages
      installed on your host.
      For Ubuntu, Debian, and Fedora the package is ``git-email``.

   The ``git send-email`` command sends email by using a local or remote
   Mail Transport Agent (MTA) such as ``msmtp``, ``sendmail``, or
   through a direct ``smtp`` configuration in your Git ``~/.gitconfig``
   file. If you are submitting patches through email only, it is very
   important that you submit them without any whitespace or HTML
   formatting that either you or your mailer introduces. The maintainer
   that receives your patches needs to be able to save and apply them
   directly from your emails. A good way to verify that what you are
   sending will be applicable by the maintainer is to do a dry run and
   send them to yourself and then save and apply them as the maintainer
   would.

   The ``git send-email`` command is the preferred method for sending
   your patches using email since there is no risk of compromising
   whitespace in the body of the message, which can occur when you use
   your own mail client. The command also has several options that let
   you specify recipients and perform further editing of the email
   message. For information on how to use the ``git send-email``
   command, see ``GIT-SEND-EMAIL(1)`` displayed using the
   ``man git-send-email`` command.

The Yocto Project uses a `Patchwork instance <https://patchwork.openembedded.org/>`__
to track the status of patches submitted to the various mailing lists and to
support automated patch testing. Each submitted patch is checked for common
mistakes and deviations from the expected patch format and submitters are
notified by patchtest if such mistakes are found. This process helps to
reduce the burden of patch review on maintainers.

.. note::

   This system is imperfect and changes can sometimes get lost in the flow.
   Asking about the status of a patch or change is reasonable if the change
   has been idle for a while with no feedback.

Using Scripts to Push a Change Upstream and Request a Pull
----------------------------------------------------------

For larger patch series it is preferable to send a pull request which not
only includes the patch but also a pointer to a branch that can be pulled
from. This involves making a local branch for your changes, pushing this
branch to an accessible repository and then using the ``create-pull-request``
and ``send-pull-request`` scripts from openembedded-core to create and send a
patch series with a link to the branch for review.

Follow this procedure to push a change to an upstream "contrib" Git
repository once the steps in :ref:`dev-manual/changes:preparing changes for submission` have
been followed:

.. note::

   You can find general Git information on how to push a change upstream
   in the
   `Git Community Book <https://git-scm.com/book/en/v2/Distributed-Git-Distributed-Workflows>`__.

#. *Push Your Commits to a "Contrib" Upstream:* If you have arranged for
   permissions to push to an upstream contrib repository, push the
   change to that repository::

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

   -  *Examine the List of Mailing Lists:* For a list of the Yocto
      Project and related mailing lists, see the ":ref:`Mailing
      lists <resources-mailinglist>`" section in
      the Yocto Project Reference Manual.

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

Responding to Patch Review
--------------------------

You may get feedback on your submitted patches from other community members
or from the automated patchtest service. If issues are identified in your
patch then it is usually necessary to address these before the patch will be
accepted into the project. In this case you should amend the patch according
to the feedback and submit an updated version to the relevant mailing list,
copying in the reviewers who provided feedback to the previous version of the
patch.

The patch should be amended using ``git commit --amend`` or perhaps ``git
rebase`` for more expert git users. You should also modify the ``[PATCH]``
tag in the email subject line when sending the revised patch to mark the new
iteration as ``[PATCH v2]``, ``[PATCH v3]``, etc as appropriate. This can be
done by passing the ``-v`` argument to ``git format-patch`` with a version
number.

Lastly please ensure that you also test your revised changes. In particular
please don't just edit the patch file written out by ``git format-patch`` and
resend it.

Submitting Changes to Stable Release Branches
---------------------------------------------

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
      steps in :ref:`dev-manual/changes:preparing changes for submission` and
      :ref:`dev-manual/changes:using email to submit a patch` but modify the subject header of your patch
      email to include the name of the stable branch which you are
      targetting. This can be done using the ``--subject-prefix`` argument to
      ``git format-patch``, for example to submit a patch to the dunfell
      branch use
      ``git format-patch --subject-prefix='&DISTRO_NAME_NO_CAP_MINUS_ONE;][PATCH' ...``.

