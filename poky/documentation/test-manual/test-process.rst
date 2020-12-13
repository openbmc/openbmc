.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

***********************************
Project Testing and Release Process
***********************************

Day to Day Development
======================

This section details how the project tests changes, through automation
on the Autobuilder or with the assistance of QA teams, through to making
releases.

The project aims to test changes against our test matrix before those
changes are merged into the master branch. As such, changes are queued
up in batches either in the ``master-next`` branch in the main trees, or
in user trees such as ``ross/mut`` in ``poky-contrib`` (Ross Burton
helps review and test patches and this is his testing tree).

We have two broad categories of test builds, including "full" and
"quick". On the Autobuilder, these can be seen as "a-quick" and
"a-full", simply for ease of sorting in the UI. Use our Autobuilder
console view to see where me manage most test-related items, available
at: :yocto_ab:`/typhoon/#/console`.

Builds are triggered manually when the test branches are ready. The
builds are monitored by the SWAT team. For additional information, see
:yocto_wiki:`/Yocto_Build_Failure_Swat_Team`.
If successful, the changes would usually be merged to the ``master``
branch. If not successful, someone would respond to the changes on the
mailing list explaining that there was a failure in testing. The choice
of quick or full would depend on the type of changes and the speed with
which the result was required.

The Autobuilder does build the ``master`` branch once daily for several
reasons, in particular, to ensure the current ``master`` branch does
build, but also to keep ``yocto-testresults``
(:yocto_git:`/yocto-testresults/`),
buildhistory
(:yocto_git:`/poky-buildhistory/`), and
our sstate up to date. On the weekend, there is a master-next build
instead to ensure the test results are updated for the less frequently
run targets.

Performance builds (buildperf-\* targets in the console) are triggered
separately every six hours and automatically push their results to the
buildstats repository at:
:yocto_git:`/yocto-buildstats/`.

The 'quick' targets have been selected to be the ones which catch the
most failures or give the most valuable data. We run 'fast' ptests in
this case for example but not the ones which take a long time. The quick
target doesn't include \*-lsb builds for all architectures, some world
builds and doesn't trigger performance tests or ltp testing. The full
build includes all these things and is slower but more comprehensive.

Release Builds
==============

The project typically has two major releases a year with a six month
cadence in April and October. Between these there would be a number of
milestone releases (usually four) with the final one being stablization
only along with point releases of our stable branches.

The build and release process for these project releases is similar to
that in `Day to Day Development <#test-daily-devel>`__, in that the
a-full target of the Autobuilder is used but in addition the form is
configured to generate and publish artefacts and the milestone number,
version, release candidate number and other information is entered. The
box to "generate an email to QA"is also checked.

When the build completes, an email is sent out using the send-qa-email
script in the ``yocto-autobuilder-helper`` repository to the list of
people configured for that release. Release builds are placed into a
directory in https://autobuilder.yocto.io/pub/releases on the
Autobuilder which is included in the email. The process from here is
more manual and control is effectively passed to release engineering.
The next steps include:

-  QA teams respond to the email saying which tests they plan to run and
   when the results will be available.

-  QA teams run their tests and share their results in the yocto-
   testresults-contrib repository, along with a summary of their
   findings.

-  Release engineering prepare the release as per their process.

-  Test results from the QA teams are included into the release in
   separate directories and also uploaded to the yocto-testresults
   repository alongside the other test results for the given revision.

-  The QA report in the final release is regenerated using resulttool to
   include the new test results and the test summaries from the teams
   (as headers to the generated report).

-  The release is checked against the release checklist and release
   readiness criteria.

-  A final decision on whether to release is made by the YP TSC who have
   final oversight on release readiness.
