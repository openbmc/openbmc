SUMMARY = "Programs for accessing Microsoft Word documents"
HOMEPAGE = "http://wvware.sourceforge.net/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6cbca01f1c9178aca280e8ff64d85b2f"

DEPENDS = "libgsf glib-2.0 libpng"

SRC_URI = "https://www.abisource.com/downloads/wv/${PV}/${BP}.tar.gz \
           file://0001-configure-use-foreign-mode-to-avoid-errors-with-miss.patch \
           file://0001-wvRTF.c-Specify-print-format-in-rtf_output.patch \
           "

SRC_URI[md5sum] = "dbccf2e9f747e50c913b7e3d126b73f7"
SRC_URI[sha256sum] = "4c730d3b325c0785450dd3a043eeb53e1518598c4f41f155558385dd2635c19d"

inherit autotools pkgconfig

EXTRA_OECONF = ""
