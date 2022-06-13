OpenEmbedded-Core and Yocto Project Maintainer Information
==========================================================

OpenEmbedded and Yocto Project work jointly together to maintain the metadata,
layers, tools and sub-projects that make up their ecosystems.

The projects operate through collaborative development. This currently takes
place on mailing lists for many components as the "pull request on github"
workflow works well for single or small numbers of maintainers but we have
a large number, all with different specialisms and benefit from the mailing
list review process. Changes therefore undergo peer review through mailing
lists in many cases.

This file aims to acknowledge people with specific skills/knowledge/interest
both to recognise their contributions but also empower them to help lead and
curate those components. Where we have people with specialist knowledge in
particular areas, during review patches/feedback from these people in these
areas would generally carry weight.

This file is maintained in OE-Core but may refer to components that are separate
to it if that makes sense in the context of maintainership. The README of specific
layers and components should ultimately be definitive about the patch process and
maintainership for the component.

Recipe Maintainers
------------------

See meta/conf/distro/include/maintainers.inc

Component/Subsystem Maintainers
-------------------------------

* Kernel (inc. linux-yocto, perf): Bruce Ashfield
* Reproducible Builds: Joshua Watt
* Toaster: David Reyna
* Hash-Equivalence: Joshua Watt
* Recipe upgrade infrastructure: Alex Kanavin
* Toolchain: Khem Raj
* ptest-runner: Aníbal Limón
* opkg: Alex Stewart
* devtool: Saul Wold
* eSDK: Saul Wold
* overlayfs: Vyacheslav Yurkov

Maintainers needed
------------------

* Pseudo
* Layer Index
* recipetool
* QA framework/automated testing
* error reporting system/web UI
* wic
* Patchwork
* Patchtest
* Matchbox
* Sato
* Autobuilder

Layer Maintainers needed
------------------------

* meta-gplv2 (ideally new strategy but active maintainer welcome)

Shadow maintainers/development needed
--------------------------------------

* toaster
* bitbake


