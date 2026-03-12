SUMMARY = "A tool to run a command as a daemon"
HOMEPAGE = "http://software.clapper.org/daemonize/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=3cf9084faa88bc8554a9139d8d7dd35f"
PV = "1.7.8"

inherit autotools

SRCREV = "58237626e6999e68b9583ed1b4b08136f118f68c"
SRC_URI = " \
    git://github.com/bmc/daemonize.git;branch=master;protocol=https \
"


EXTRA_AUTORECONF += "--exclude=autoheader"
