SUMMARY = "Library for converting WMF files"
HOMEPAGE = "http://wvware.sourceforge.net/libwmf.html"
SECTION = "libs"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

PR = "r3"

DEPENDS_class-native = "freetype-native libpng-native jpeg-native"
DEPENDS = "freetype libpng jpeg expat gtk+"

BBCLASSEXTEND = "native"

inherit distro_features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "${SOURCEFORGE_MIRROR}/wvware/${BPN}/${PV}/${BPN}-${PV}.tar.gz;name=tarball \
           file://libwmf-0.2.8.4-intoverflow.patch \
           file://libwmf-0.2.8.4-useafterfree.patch \
           file://0001-configure-use-pkg-config-for-freetype.patch \
          "

SRC_URI[tarball.md5sum] = "d1177739bf1ceb07f57421f0cee191e0"
SRC_URI[tarball.sha256sum] = "5b345c69220545d003ad52bfd035d5d6f4f075e65204114a9e875e84895a7cf8"

FILES_${PN}-dbg += "${libdir}/gtk-2.0/2.10.0/loaders/.debug"
FILES_${PN}-dev += "${libdir}/gtk-2.0/2.10.0/loaders/*.la"
FILES_${PN}-staticdev += "${libdir}/gtk-2.0/2.10.0/loaders/*.a"
FILES_${PN} += "${libdir}/gtk-2.0/2.10.0/loaders/*.so"

