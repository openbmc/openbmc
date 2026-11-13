SUMMARY = "Network benchmarking utility for measuring latency and throughput"
DESCRIPTION = "Tool for network performance measurement written in C++"
HOMEPAGE = "https://github.com/Mellanox/sockperf"
SECTION = "console/network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://copying;md5=b4563b57c98bc23c8cecbc0b6d9546e9"

SRC_URI = "git://github.com/Mellanox/sockperf;branch=sockperf_v2;protocol=https \
           file://0001-src-fix-missing-includes-for-musl-struct-timeval-ss.patch \
           "
SRCREV = "3c65ad99cd385e18f8a2a655c19826e81a4d17e8"

UPSTREAM_CHECK_COMMITS = "1"

# musl lacks glibc's backtrace()/backtrace_symbols(); configure.ac uses
# AC_SEARCH_LIBS([backtrace], [execinfo], ...) which errors out with
# "libexecinfo not found" unless the compat library is available.
DEPENDS:append:libc-musl = " libexecinfo"

inherit autotools
