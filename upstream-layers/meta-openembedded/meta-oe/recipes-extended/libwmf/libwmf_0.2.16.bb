SUMMARY = "Library for converting WMF files"
#HOMEPAGE = "http://wvware.sourceforge.net/libwmf.html"
HOMEPAGE = "https://github.com/caolanm/libwmf"
SECTION = "libs"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=41890f71f740302b785c27661123bff5"


DEPENDS:class-native = "freetype-native libpng-native jpeg-native"
DEPENDS = "freetype libpng jpeg expat gtk+"

BBCLASSEXTEND = "native"

inherit features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://github.com/caolanm/libwmf.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "a916f30f6232cf3e28df285fcba5753ee5b1aa0f"


do_install:append() {
    sed -i -e 's@${RECIPE_SYSROOT}@@g' ${D}${bindir}/libwmf-config ${D}${libdir}/pkgconfig/libwmf.pc
}

FILES:${PN}-dbg += "${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/.debug"
FILES:${PN}-dev += "${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/*.la"
FILES:${PN}-staticdev += "${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/*.a"
FILES:${PN} += "${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/*.so"

