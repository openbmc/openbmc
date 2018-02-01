SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"

DEPENDS = "gtk+ libxml-parser-perl-native python3-docutils-native intltool-native"

inherit autotools pkgconfig perlnative pythonnative gettext

SRC_URI = "http://download.geany.org/${BP}.tar.bz2"
SRC_URI[md5sum] = "75081b600560c5c8366eda0e1b8cc531"
SRC_URI[sha256sum] = "0ac360f1f3d6c28790a81d570252a7d40421f6e1d8e5a8d653756bd041d88491"

FILES_${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"
