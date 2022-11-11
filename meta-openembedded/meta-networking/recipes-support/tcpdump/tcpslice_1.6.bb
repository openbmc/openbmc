SUMMARY = "tcpslice"
DESCRIPTION = "A tool for extracting parts of a tcpdump packet trace."
HOMEPAGE = "http://www.tcpdump.org/related.html"
SECTION = "net"

LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://tcpslice.c;endline=20;md5=99519e2e5234d1662a4ce16baa62c64e"

SRC_URI = "http://www.tcpdump.org/release/${BP}.tar.gz \
           "
SRC_URI[sha256sum] = "60d23f00d4c485fef2dda9b12c2018af958df3a511238c45374733bbc1231920"

UPSTREAM_CHECK_REGEX = "tcpslice-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools-brokensep pkgconfig

DEPENDS = "libpcap"

EXTRA_AUTORECONF += "--exclude=aclocal"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 tcpslice ${D}${sbindir}
}
