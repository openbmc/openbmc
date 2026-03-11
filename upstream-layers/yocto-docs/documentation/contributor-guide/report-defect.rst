.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Reporting a Defect Against the Yocto Project and OpenEmbedded
**************************************************************

You can use the Yocto Project instance of
`Bugzilla <https://www.bugzilla.org/about/>`__ to submit a defect (bug)
against BitBake, OpenEmbedded-Core, against any other Yocto Project component
or for tool issues. For additional information on this implementation of
Bugzilla see the ":ref:`Yocto Project Bugzilla <resources-bugtracker>`" section
in the Yocto Project Reference Manual. For more detail on any of the following
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

There are no guarantees about if or when a bug might be worked on since an
open-source project has no dedicated engineering resources. However, the
project does have a good track record of resolving common issues over the
medium and long term. We do encourage people to file bugs so issues are
at least known about. It helps other users when they find somebody having
the same issue as they do, and an issue that is unknown is much less likely
to ever be fixed!
