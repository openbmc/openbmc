.. SPDX-License-Identifier: CC-BY-2.0-UK

*******************
Real-World Examples
*******************

|

This chapter contains real-world examples.

Slow Write Speed on Live Images
===============================

In one of our previous releases (denzil), users noticed that booting off
of a live image and writing to disk was noticeably slower. This included
the boot itself, especially the first one, since first boots tend to do
a significant amount of writing due to certain post-install scripts.

The problem (and solution) was discovered by using the Yocto tracing
tools, in this case 'perf stat', 'perf script', 'perf record' and 'perf
report'.

See all the unvarnished details of how this bug was diagnosed and solved
here: Yocto Bug #3049
