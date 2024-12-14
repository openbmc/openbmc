DESCRIPTION = "CUPS backends, filters, and other software"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/openprinting/cups-filters"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d5b952b53dbe7752199903d082e5f07"

DEPENDS = "libcupsfilters libppd glib-2.0 poppler"

SRC_URI = " \
	https://github.com/OpenPrinting/${BPN}/releases/download/${PV}/${BP}.tar.xz \
	file://fix-make-race.patch \
"
SRC_URI[sha256sum] = "b5152e3dd148ed73835827ac2f219df7cf5808dbf9dbaec2aa0127b44de800d8"

UPSTREAM_CHECK_URI = "https://github.com/OpenPrinting/cups-filters/releases"

inherit autotools gettext pkgconfig github-releases

EXTRA_OECONF += " \
	--enable-imagefilters \
	--enable-ghostscript --with-gs-path=${bindir}/gs \
	--with-fontdir=${datadir}/fonts \
	--localstatedir=${localstatedir} \
"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)}"
PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"

FILES:${PN} += "${datadir}"
FILES:${PN}-dev += "${datadir}/ppdc"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "ghostscript"
