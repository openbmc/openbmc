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
LIC_FILES_CHKSUM = "file://LICENSE;md5=d098223e44bdd19585315ee75cd9d2d7"

DEPENDS = "openssl"

SRC_URI = "git://github.com/esnet/iperf.git \
           file://automake-foreign.patch \
           file://0001-include-stdint.h-for-various-std-c99-int-types.patch \
           file://0002-Remove-pg-from-profile_CFLAGS.patch \
           "

SRCREV = "88d907f7fb58bfab5d086c5da60c922e1c582c92"

S = "${WORKDIR}/git"

inherit autotools

PACKAGECONFIG[lksctp] = "ac_cv_header_netinet_sctp_h=yes,ac_cv_header_netinet_sctp_h=no,lksctp-tools"

CFLAGS += "-D_GNU_SOURCE"

EXTRA_OECONF = "--with-openssl=${RECIPE_SYSROOT}"

BBCLASSEXTEND = "native"
