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
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9088fe7ffdccd042f7645f1012d7f70"

DEPENDS = "openssl"

SRC_URI = "git://github.com/esnet/iperf.git \
           file://0002-Remove-pg-from-profile_CFLAGS.patch \
           "

SRCREV = "dfcea9f6a09ead01089a3c9d20c7032f2c0af2c1"

S = "${WORKDIR}/git"

inherit autotools

PACKAGECONFIG[lksctp] = "ac_cv_header_netinet_sctp_h=yes,ac_cv_header_netinet_sctp_h=no,lksctp-tools"

CFLAGS += "-D_GNU_SOURCE"

EXTRA_OECONF = "--with-openssl=${RECIPE_SYSROOT}${prefix}"
