.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Security team
*************

The Yocto Project/OpenEmbedded security team coordinates the work on security
subjects in the project. All general discussion takes place publicly. The
Security Team only uses confidential communication tools to deal with private
vulnerability reports before they are released.

Security team appointment
=========================

The Yocto Project Security Team consists of at least three members. When new
members are needed, the Yocto Project Technical Steering Committee (YP TSC)
asks for nominations by public channels including a nomination deadline.
Self-nominations are possible. When the limit time is
reached, the YP TSC posts the list of candidates for the comments of project
participants and developers. Comments may be sent publicly or privately to the
YP and OE TSCs. The candidates are approved by both YP TSC and OpenEmbedded
Technical Steering Committee (OE TSC) and the final list of the team members
is announced publicly. The aim is to have people representing technical
leadership, security knowledge and infrastructure present with enough people
to provide backup/coverage but keep the notification list small enough to
minimize information risk and maintain trust.

YP Security Team members may resign at any time.

Security Team Operations
========================

The work of the Security Team might require high confidentiality. Team members
are individuals selected by merit and do not represent the companies they work
for. They do not share information about confidential issues outside of the team
and do not hint about ongoing embargoes.

Team members can bring in domain experts as needed. Those people should be
added to individual issues only and adhere to the same standards as the YP
Security Team.

The YP security team organizes its meetings and communication as needed.

When the YP Security team receives a report about a potential security
vulnerability, they quickly analyze and notify the reporter of the result.
They might also request more information.

If the issue is confirmed and affects the code maintained by the YP, they
confidentially notify maintainers of that code and work with them to prepare
a fix.

If the issue is confirmed and affects an upstream project, the YP security team
notifies the project. Usually, the upstream project analyzes the problem again.
If they deem it a real security problem in their software, they develop and
release a fix following their security policy. They may want to include the
original reporter in the loop. There is also sometimes some coordination for
handling patches, backporting patches etc, or just understanding the problem
or what caused it.

The security policy of the upstream project might include a notification to
Linux distributions or other important downstream projects in advance to
discuss coordinated disclosure. These mailing lists are normally non-public.

When the upstream project releases a version with the fix, they are responsible
for contacting an appropriate CVE Numbering Authority (CNA), such as `Mitre
<https://cveform.mitre.org/>`__, to get a CVE number assigned and the CVE
record published.

When the fix is publicly available, the YP security team member or the
package maintainer sends patches against the YP code base, following usual
procedures, including public code review.

If an upstream project does not respond quickly
-----------------------------------------------

If an upstream project does not fix the problem in a reasonable time,
the Yocto's Security Team will contact other interested parties (usually
other distributions) in the community and together try to solve the
vulnerability as quickly as possible.

The Yocto Project Security team adheres to the 90 days disclosure policy
by default. An increase of the embargo time is possible when necessary.

Handling multi-project embargoes
--------------------------------

In rare cases, a severe security issue affects multiple projects. This might be
numerous projects having a similar issue because of design, coding pattern, or
reuse of the same code (an example of this situation is :cve_nist:`2023-44487`
where multiple web servers share a design weakness). It might also be a
high-profile issue in a commonly used library (like OpenSSL). In such cases,
the project, learning first about the issue, might decide to notify other
affected projects confidentially so that they come up with a synchronized fix.
It might also be the affected project informing major distributions to roll out
the update simultaneously.

Such notifications happen over confidential, non-public means. Typically, the
project initiating this "embargo" directly notifies a selected number of people
from each project, including a subset of the security team. When Yocto Project
is a part of such a notified group, developers prepare fixes on separate
infrastructure and test it. They might also include additional developers and
domain experts who can help with the fix and eventual regressions. When the
embargo is lifted, they send a patch to the relevant public list, and the usual
review process starts.

Security Team Members
=====================

For secure communications, please send your messages encrypted using the GPG
keys. Remember, message headers are not encrypted so do not include sensitive
information in the subject line.

-  Ross Burton: <ross [at] burtonini [dot] com> `Public key <https://keys.openpgp.org/search?q=ross%40burtonini.com>`__

-  Michael Halstead: <mhalstead [at] linuxfoundation [dot] org>
   `Public key <https://pgp.mit.edu/pks/lookup?op=vindex&search=0x3373170601861969>`__
   or `Public key <https://keyserver.ubuntu.com/pks/lookup?op=get&search=0xd1f2407285e571ed12a407a73373170601861969>`__

-  Richard Purdie: <richard.purdie [at] linuxfoundation [dot] org> `Public key <https://keys.openpgp.org/search?q=richard.purdie%40linuxfoundation.org>`__

-  Marta Rybczynska: <marta DOT rybczynska [at] ygreky [dot] com> `Public key <https://keys.openpgp.org/search?q=marta.rybczynska@ygreky.com>`__

-  Paul Barker <paul [at] pbarker [dot] dev> `Public key <https://keys.openpgp.org/search?q=paul@pbarker.dev>`__
