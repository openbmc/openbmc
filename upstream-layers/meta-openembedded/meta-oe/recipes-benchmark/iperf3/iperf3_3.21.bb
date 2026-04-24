SUMMARY = "Network benchmark tool"
DESCRIPTION = "\
iperf is a tool for active measurements of the maximum achievable bandwidth \
on IP networks. It supports tuning of various parameters related to timing, \
protocols, and buffers. For each test it reports the bandwidth, loss, and \
other parameters."

HOMEPAGE = "http://software.es.net/iperf/"
SECTION = "console/network"
BUGTRACKER = "https://github.com/esnet/iperf/issues"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f2eb355b6d3b9d63b6b7a861cdc62440"

SRC_URI = "git://github.com/esnet/iperf.git;branch=master;protocol=https;tag=${PV} \
           file://0002-Remove-pg-from-profile_CFLAGS.patch \
           file://0001-configure.ac-check-for-CPP-prog.patch \
          "

SRCREV = "d39cf41526626b4e5a130f115d931cd6cbdffc19"

RDEPENDS:${PN} = "libgcc"

inherit autotools

PACKAGECONFIG ?= "openssl"

PACKAGECONFIG[lksctp] = "ac_cv_header_netinet_sctp_h=yes,ac_cv_header_netinet_sctp_h=no,lksctp-tools"
PACKAGECONFIG[openssl] = "--with-openssl=${RECIPE_SYSROOT}${prefix},--without-openssl,openssl"

CFLAGS += "-D_GNU_SOURCE"

BBCLASSEXTEND = "native nativesdk"
