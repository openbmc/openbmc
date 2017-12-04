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
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3434c5a9a53c78c7739f0bc9e5adda"

SRC_URI = "\
    git://github.com/esnet/iperf.git \
    file://automake-foreign.patch \
"

PV = "3.1.3+gitr${SRCPV}"
SRCREV = "099244ec686b620393e9845478a554b1c7ca5c8b"

S = "${WORKDIR}/git"

inherit autotools

PACKAGECONFIG[lksctp] = "ac_cv_header_netinet_sctp_h=yes,ac_cv_header_netinet_sctp_h=no,lksctp-tools"

BBCLASSEXTEND = "native"
