SUMMARY = "A FUSE based implemention of unionfs"
HOMEPAGE = "http://podgorny.cz/moin/UnionFsFuse"
SECTION = "console/network"
LICENSE = "BSD-3-Clause"
DEPENDS = "fuse"
LIC_FILES_CHKSUM = "file://src/unionfs.c;beginline=3;endline=8;md5=30fa8de70fd8abab00b483a1b7943a32"

SRC_URI = "http://podgorny.cz/unionfs-fuse/releases/${BP}.tar.xz"

SRC_URI[md5sum] = "689c636484756f6f7a728ef354cbeac2"
SRC_URI[sha256sum] = "8d5c9dcb51ecb9a9b03890e16d17e37d602b0c1f23ed6a9ddec2b0f719c9f662"

do_install() {
	oe_runmake install DESTDIR=${D} PREFIX=${exec_prefix}
}

RDEPENDS_${PN} += "bash"
