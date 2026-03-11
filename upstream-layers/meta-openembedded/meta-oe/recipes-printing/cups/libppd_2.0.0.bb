DESCRIPTION = "OpenPrinting libppd"
HOMEPAGE = "https://github.com/OpenPrinting"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=c1fca671047153ce6825c4ab06f2ab49"

DEPENDS = "libcupsfilters"

SRC_URI = "https://github.com/OpenPrinting/${BPN}/releases/download/${PV}/${BP}.tar.xz"
SRC_URI[sha256sum] = "882d3c659a336e91559de8f3c76fc26197fe6e5539d9b484a596e29a5a4e0bc8"

inherit autotools gettext pkgconfig github-releases

do_install:append() {
	rm -r ${D}${bindir}
}

FILES:${PN} += "${datadir}"

