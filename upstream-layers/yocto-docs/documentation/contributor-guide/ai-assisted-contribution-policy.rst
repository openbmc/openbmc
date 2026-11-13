.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

AI-Assisted Contribution Policy
*******************************

Scope
=====

This policy applies to all contributions to the Yocto Project which
include non-trivial AI-generated content.

A *contribution* is anything you send to the project, including:

-  Patches, commit messages and cover letters

-  Review comments, discussions and other mailing list submissions

-  Bug reports and comments in Bugzilla

-  Security reports sent to the Security Team

-  Wiki content

*AI-generated content* is anything produced by a Large Language Model
(LLM) or other generative AI technology, including output from AI
assistants, coding agents, etc.

Trivial or incidental inclusion of AI-generated content is outside the
scope of this policy - a single line copied from AI-assistant output
into an otherwise human authored document, or autocompletion of a
function signature in code that is otherwise hand-written, does not rise
to the level of significance that we are considering here.

This policy does not apply to upstream projects whose software we
package, including patch files copied verbatim from upstream. It does
however apply to patches against upstream software which you modify,
develop or generate as a contributor to the Yocto Project.

General conditions
==================

**AI-generated contributions to the Yocto Project must comply with the**
`Linux Foundation Generative AI Policy`_. In particular,

    Code or other content generated in whole or in part using AI tools can
    be contributed to Linux Foundation projects. However, there are some
    unique considerations related to AI generated content that developers
    should factor into their contributions.

    1. Contributors should ensure that the terms and conditions of the
       generative AI tool do not place any contractual restrictions on how
       the tool's output can be used that are inconsistent with the
       project's open source software license, the project's intellectual
       property policies, or the Open Source Definition.

    2. If any pre-existing copyrighted materials (including pre-existing
       open source code) authored or owned by third parties are included in
       the AI tool's output, prior to contributing such output to the
       project, the Contributor should confirm that they have have
       permission from the third party owners-such as the form of an open
       source license or public domain declaration that complies with the
       project's licensing policies-to use and modify such pre-existing
       materials and contribute them to the project. Additionally, the
       contributor should provide notice and attribution of such third party
       rights, along with information about the applicable license terms,
       with their contribution.

**All existing Yocto Project contribution guidelines are expected to be
followed**, including the `Code of Conduct`_, the
:doc:`/contributor-guide/recipe-style-guide` and the documentation on how to
:ref:`contributor-guide/submit-changes:Implement and commit changes`.

Human responsibility
====================

The Yocto Project is a collaborative endeavour and our community is as
valuable as our code base for the long-term health of the project. At a
fundamental level, we each need to take responsibility for what we
contribute to the project. For example, this includes ensuring that our
contributions follow the project's Code of Conduct. It also includes
being respectful of the finite bandwidth that we all have for reading,
reviewing and responding to contributions.

**Patch authors take responsibility for the code they submit to the
project** - we expect them to understand and be able to explain the
proposed changes, to be able to engage with review comments and to be
able to revise and follow up contributions. Similarly, reviewers must be
able to participate in discussion about their comments.

For bug reports, including security vulnerability reports, contributors
must be able to substantiate their findings beyond simply taking
AI-generated output at its word.

**Do not act as a simple proxy between your AI-agent and the Yocto
Project community**. When engaging with the community you need to do
more than just copy questions you are asked into an AI-agent chat and
send us the output. If you are using an AI-agent to assist you then
read, understand and validate its output and send only relevant
information rather than complete unedited agent responses.

Signed-off-by tags
==================

**The Signed-off-by tag must never be added by an AI-agent.** In
commit messages and patches, this tag indicates acceptance of the
Developer Certificate of Origin - it confirms your right to submit the
contribution under the appropriate open source license. It is your
responsibility to add this tag, either manually or via Git
configuration, when sending patches.

Patch upstreaming
=================

**Patches against upstream software components must comply with any
relevant AI-assisted contribution policies of the upstream project**.
This enables patches to be submitted upstream and helps us to maintain
good relationships with upstream projects.

Transparency
============

**Contributors need to disclose where they have used AI-generated output
in their submissions to the project**. As stated above, incidental or
trivial use of AI-generated output within an otherwise human authored
submission doesn't require disclosure - but if in doubt, err on the side
of disclosing the use of AI tools.

The use of generative AI and LLMs in software development is a rapidly
evolving field. Many questions around this technology are not yet
settled, including the copyright status of AI-generated content and the
biases or inclinations of these tools. Within the Yocto Project we need
to be able to determine which contributions include non-trivial
AI-generated content and which do not. Experience shows LLM patches have
different review requirements to human ones. We also need to be able to
evaluate the impact of AI-generated submissions on the project over
time.

In patches and commits
----------------------

**Where AI-generated code is contributed to the project, commit messages
must include the** ``AI-Generated`` **tag identifying the AI tool or
framework and the specific AI model used**. We encourage contributors to
be as specific as they can be. For example::

    AI-Generated: Claude Code, Opus 5.0

Or::

    AI-Generated: Copilot, GPT-5.5

Note that this policy applies where commit messages are AI-generated as
well as where the code change itself is AI-generated.

Where a patch is a mix of human authored and AI-generated content, it
may be helpful to briefly explain which parts are tool generated in
addition to using the ``AI-Generated`` tag.

In emails, bug reports and security reports
-------------------------------------------

**Where AI-generated output is used in emails, bug reports or security
reports, disclosure is also required.** In this case there is not a
standard or convention to follow, but disclosure must be clear. You may
wish to use the ``AI-Generated`` tag, or simply include a sentence to
say that the contribution is AI-generated.

Fully automated contributions
=============================

**The project will not accept automated contributions which rely
completely on AI generation without any significant human involvement,
judgement or review.**

Exceptions to this policy may be considered by the Yocto Project
Technical Steering Committee (TSC).

Privacy and confidentiality
===========================

**Avoid sending private or confidential information (including embargoed
information about security vulnerabilities) as input to AI-assistants
operated as-a-Service by untrusted third parties.**

Many AI-assistant or agent tools are hosted by a service provider and
accessed over the internet. Always bear in mind when interacting with
such an AI-assistant that you are communicating with a third-party
organisation who may have access to the full history of your
interactions with the tool.

Enforcement
===========

Violations of this policy may result in contributions being rejected or
deleted as appropriate. Repeated violations will be dealt with through
the project's `Code of Conduct`_ policy.

.. _Linux Foundation Generative AI Policy: https://www.linuxfoundation.org/legal/generative-ai
.. _Code of Conduct: https://www.yoctoproject.org/code-of-conduct
