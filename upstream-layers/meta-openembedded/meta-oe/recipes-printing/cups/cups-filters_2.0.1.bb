DESCRIPTION = "CUPS backends, filters, and other software"
HOMEPAGE = "https://wiki.linuxfoundation.org/openprinting/cups-filters"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d5b952b53dbe7752199903d082e5f07"

DEPENDS = "libcupsfilters libppd glib-2.0 poppler"

SRC_URI = "https://github.com/OpenPrinting/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           file://fix-make-race.patch \
           file://0001-Fix-build-failure-with-GCC-15-and-std-c23.patch \
           file://CVE-2025-64524.patch \
           "
SRC_URI[sha256sum] = "39e71de3ce06762b342749f1dc7cba6817738f7bf4d322c1bb9ab10b8569ab80"

UPSTREAM_CHECK_URI = "https://github.com/OpenPrinting/cups-filters/releases"

inherit autotools gettext pkgconfig github-releases

EXTRA_OECONF += " \
    --enable-imagefilters \
    --enable-ghostscript --with-gs-path=${bindir}/gs \
    --with-fontdir=${datadir}/fonts \
    --localstatedir=${localstatedir} \
"

FILES:${PN} += "${datadir}"
FILES:${PN}-dev += "${datadir}/ppdc"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "ghostscript"
