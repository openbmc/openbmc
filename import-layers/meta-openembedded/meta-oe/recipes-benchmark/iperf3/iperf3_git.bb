SUMMARY = "Network benchmark tool"
DESCRIPTION = "\
iperf is a tool for active measurements of the maximum achievable bandwidth \
on IP networks. It supports tuning of various parameters related to timing, \
protocols, and buffers. For each test it reports the bandwidth, loss, and \
other parameters."
HOMEPAGE = "http://software.es.net/iperf/"
SECTION = "console/network"
BUGTRACKER = "https://github.com/esnet/iperf/issues"
AUTHOR = "ESNET <info@es.net>, Lawrence Berkeley National Laboratory <websupport@lbl.gov>"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=062ab1bc33fae1926387ac1274cb0873"

SRC_URI = "\
    git://github.com/esnet/iperf.git \
    file://automake-foreign.patch \
"

PV = "3.1+gitr${SRCPV}"
SRCREV = "e396134952a01199326665d1df7c51ae9e62e945"

S = "${WORKDIR}/git"

inherit autotools

PACKAGECONFIG[lksctp] = "ac_cv_header_netinet_sctp_h=yes,ac_cv_header_netinet_sctp_h=no,lksctp-tools"

BBCLASSEXTEND = "native"
