SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"

DEPENDS = "gtk+3 libxml-parser-perl-native python3-docutils-native intltool-native"

inherit distro_features_check autotools pkgconfig perlnative pythonnative gettext

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://download.geany.org/${BP}.tar.bz2"
SRC_URI[md5sum] = "87220f4a0e03623ab9d86820f6be7b5d"
SRC_URI[sha256sum] = "e765efd89e759defe3fd797d8a2052afbb4b23522efbcc72e3a72b7f1093ec11"

FILES_${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"
