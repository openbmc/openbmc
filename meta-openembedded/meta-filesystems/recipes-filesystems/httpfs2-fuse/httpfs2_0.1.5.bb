SUMMARY = "This is a filesystem client based on the HTTP using FUSE"
HOMEPAGE = "http://httpfs.sourceforge.net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=83f224c5182f148ec92e0b9f84b3c6c7"

inherit pkgconfig

DEPENDS += "fuse"
RDEPENDS_${PN} += "fuse"

SRC_URI += "https://astuteinternet.dl.sourceforge.net/project/httpfs/httpfs2/httpfs2-${PV}.tar.gz"
SRC_URI[sha256sum] = "01cb4bb38deb344f540da6f1464dc7edbdeb51213ad810b8c9c282c1e17e0fc1"

S = "${WORKDIR}/httpfs2-${PV}"

do_compile() {
    cd ${S}
    oe_runmake httpfs2
}

do_install() {
    cd ${S}
    install -d ${D}${bindir}
    install -m 0755 httpfs2 ${D}${bindir}
}
