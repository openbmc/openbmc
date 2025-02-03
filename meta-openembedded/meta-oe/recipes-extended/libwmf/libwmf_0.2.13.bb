SUMMARY = "Library for converting WMF files"
#HOMEPAGE = "http://wvware.sourceforge.net/libwmf.html"
HOMEPAGE = "https://github.com/caolanm/libwmf"
SECTION = "libs"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"


DEPENDS:class-native = "freetype-native libpng-native jpeg-native"
DEPENDS = "freetype libpng jpeg expat gtk+"

BBCLASSEXTEND = "native"

inherit features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://github.com/caolanm/libwmf.git;protocol=https;branch=master"
SRCREV = "9e4737f2293c0d127bda92e5b01896df10571424"

S = "${WORKDIR}/git"

do_install:append() {
    sed -i -e 's@${RECIPE_SYSROOT}@@g' ${D}${bindir}/libwmf-config ${D}${libdir}/pkgconfig/libwmf.pc
}

FILES:${PN}-dbg += "${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/.debug"
FILES:${PN}-dev += "${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/*.la"
FILES:${PN}-staticdev += "${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/*.a"
FILES:${PN} += "${libdir}/gdk-pixbuf-2.0/2.10.0/loaders/*.so"

