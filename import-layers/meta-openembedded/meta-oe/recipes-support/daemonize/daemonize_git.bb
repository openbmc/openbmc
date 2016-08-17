SUMMARY = "A tool to run a command as a daemon"
HOMEPAGE = "http://software.clapper.org/daemonize/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=3cf9084faa88bc8554a9139d8d7dd35f"
PV = "1.7.3+git${SRCPV}"

inherit autotools

SRCREV = "a4ac64a243af91dc434b7a3915f43482d528a2b1"
SRC_URI = "git://github.com/bmc/daemonize.git"

S = "${WORKDIR}/git"

EXTRA_AUTORECONF += "--exclude=autoheader"
