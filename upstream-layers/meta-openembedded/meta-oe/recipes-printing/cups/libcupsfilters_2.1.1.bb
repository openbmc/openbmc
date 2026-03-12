DESCRIPTION = "OpenPrinting libcupsfilters"
HOMEPAGE = "https://github.com/OpenPrinting"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=aab2024bd2a475438a154cd1640c9684"

DEPENDS = "cups fontconfig libexif dbus lcms qpdf poppler libpng jpeg tiff"

SRC_URI = "https://github.com/OpenPrinting/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           file://0001-use-noexcept-false-instead-of-throw-from-c-17-onward.patch \
           file://CVE-2025-64503.patch \
           "
SRC_URI[sha256sum] = "6c303e36cfde05a6c88fb940c62b6a18e7cdbfb91f077733ebc98f104925ce36"

inherit autotools gettext pkgconfig github-releases

FILES:${PN} += "${datadir}"
RDEPENDS:${PN} += "ghostscript"
