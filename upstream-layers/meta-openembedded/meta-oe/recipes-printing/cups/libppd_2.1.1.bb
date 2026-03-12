DESCRIPTION = "OpenPrinting libppd"
HOMEPAGE = "https://github.com/OpenPrinting"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=c1fca671047153ce6825c4ab06f2ab49"

DEPENDS = "libcupsfilters"

SRC_URI = "https://github.com/OpenPrinting/${BPN}/releases/download/${PV}/${BP}.tar.xz"
SRC_URI[sha256sum] = "3fa341cc03964046d2bf6b161d80c1b4b2e20609f38d860bcaa11cb70c1285e4"

inherit autotools gettext pkgconfig github-releases

do_install:append() {
	rm -r ${D}${bindir}
}

FILES:${PN} += "${datadir}"

