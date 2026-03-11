DESCRIPTION = "vnStat is a console-based network traffic monitor for Linux and BSD that keeps a log of network traffic for the selected interface(s)."
HOMEPAGE = "https://humdi.net/vnstat/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SECTION = "net"
DEPENDS = "gd sqlite3"

SRC_URI = "https://github.com/vergoh/vnstat/releases/download/v${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "fe2928a81243cc8a532a357f97221736"
SRC_URI[sha256sum] = "89276e0a7281943edb554b874078278ad947dc312938a2451e03eb80679f7ff7"

inherit autotools pkgconfig systemd

EXTRA_OECONF = "--disable-extra-paths"

do_install:append() {
    install -Dm644 ${S}/examples/systemd/vnstat.service "${D}${systemd_system_unitdir}/vnstat.service"
}

PARALLEL_MAKEINST = ""

SYSTEMD_SERVICE:${PN} = "vnstat.service"
