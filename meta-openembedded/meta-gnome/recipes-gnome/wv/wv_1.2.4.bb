SUMMARY = "Programs for accessing Microsoft Word documents"
HOMEPAGE = "http://wvware.sourceforge.net/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6cbca01f1c9178aca280e8ff64d85b2f"

DEPENDS = "libgsf glib-2.0 libpng"

SRC_URI = "${SOURCEFORGE_MIRROR}/wvware/wv-${PV}.tar.gz \
           file://pkgconfig.patch \
           file://0001-configure-use-foreign-mode-to-avoid-errors-with-miss.patch \
           file://0001-wvRTF.c-Specify-print-format-in-rtf_output.patch \
           "

SRC_URI[md5sum] = "c1861c560491f121e12917fa76970ac5"
SRC_URI[sha256sum] = "673109910e22d4cf94cc8be4dcb9a0c41b5fbdb1736d4b7bdc7778894d57c2d6"

inherit autotools pkgconfig

S = "${WORKDIR}/${PN}-${PV}"

EXTRA_OECONF = ""
