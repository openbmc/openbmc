SUMMARY = "A tool to run a command as a daemon"
HOMEPAGE = "http://software.clapper.org/daemonize/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=3cf9084faa88bc8554a9139d8d7dd35f"
PV = "1.7.7+git${SRCPV}"

inherit autotools

SRCREV = "6b10308b13c13e7b911e75e27bf7e65c30d58799"
SRC_URI = "git://github.com/bmc/daemonize.git \
           file://fix-ldflags-for-gnuhash.patch"

S = "${WORKDIR}/git"

EXTRA_AUTORECONF += "--exclude=autoheader"
