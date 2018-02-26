SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"

DEPENDS = "gtk+ libxml-parser-perl-native python3-docutils-native intltool-native"

inherit autotools pkgconfig perlnative pythonnative gettext

SRC_URI = "http://download.geany.org/${BP}.tar.bz2"
SRC_URI[md5sum] = "386000be6b26972c6a699939c37cda34"
SRC_URI[sha256sum] = "30fdb906bb76c4251a8bcf83ee267db28c26ef6ab867668a782cec1164a3aba5"

FILES_${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"
