SUMMARY = "tcpslice"
DESCRIPTION = "A tool for extracting parts of a tcpdump packet trace."
HOMEPAGE = "http://www.tcpdump.org/related.html"
SECTION = "net"

LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://tcpslice.c;endline=20;md5=99519e2e5234d1662a4ce16baa62c64e"

SRC_URI = "http://www.tcpdump.org/release/${BP}.tar.gz \
           "
SRC_URI[sha256sum] = "e513f0710c4ab45ec627e5df6f7b80d12e488146e1bd500c92247e20ca608903"

UPSTREAM_CHECK_REGEX = "tcpslice-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools-brokensep pkgconfig

DEPENDS = "libpcap"

EXTRA_AUTORECONF += "--exclude=aclocal"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 tcpslice ${D}${sbindir}
}
