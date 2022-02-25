SUMMARY = "tcpslice"
DESCRIPTION = "A tool for extracting parts of a tcpdump packet trace."
HOMEPAGE = "http://www.tcpdump.org/related.html"
SECTION = "net"

LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://tcpslice.c;endline=20;md5=99519e2e5234d1662a4ce16baa62c64e"

SRC_URI = "http://www.tcpdump.org/release/${BP}.tar.gz \
           "
SRC_URI[md5sum] = "8907e60376e629f6e6ce2255988aaf47"
SRC_URI[sha256sum] = "f6935e3e7ca00ef50c515d062fddd410868467ec5b6d8f2eca12066f8d91dda2"

UPSTREAM_CHECK_REGEX = "tcpslice-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools-brokensep pkgconfig

DEPENDS = "libpcap"

EXTRA_AUTORECONF += "--exclude=aclocal"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 tcpslice ${D}${sbindir}
}
