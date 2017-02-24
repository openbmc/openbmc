SUMMARY = "htop process monitor"
HOMEPAGE = "http://htop.sf.net"
SECTION = "console/utils"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=c312653532e8e669f30e5ec8bdc23be3"

DEPENDS = "ncurses"
RDEPENDS_${PN} = "ncurses-terminfo"

SRC_URI = "http://hisham.hm/htop/releases/${PV}/htop-${PV}.tar.gz"

SRC_URI[md5sum] = "e768b9b55c033d9c1dffda72db3a6ac7"
SRC_URI[sha256sum] = "055c57927f75847fdc222b5258b079a9542811a9dcf5421c615c7e17f55d1829"

LDFLAGS_append_libc-uclibc = " -lubacktrace"

do_configure_prepend () {
    rm -rf ${S}/config.h
}

inherit autotools
