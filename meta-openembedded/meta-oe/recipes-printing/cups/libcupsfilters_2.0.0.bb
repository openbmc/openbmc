DESCRIPTION = "OpenPrinting libcupsfilters"
HOMEPAGE = "https://github.com/OpenPrinting"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=aab2024bd2a475438a154cd1640c9684"

DEPENDS = "cups fontconfig libexif dbus lcms qpdf poppler libpng jpeg tiff"

SRC_URI = " \
	https://github.com/OpenPrinting/${BPN}/releases/download/${PV}/${BP}.tar.xz \
	file://0001-use-noexcept-false-instead-of-throw-from-c-17-onward.patch \
"
SRC_URI[sha256sum] = "542f2bfbc58136a4743c11dc8c86cee03c9aca705612654e36ac34aa0d9aa601"

inherit autotools gettext pkgconfig github-releases

FILES:${PN} += "${datadir}"
RDEPENDS:${PN} += "ghostscript"
