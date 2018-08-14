SUMMARY = "Text shaping library"
DESCRIPTION = "HarfBuzz is an OpenType text shaping engine."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/HarfBuzz"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=HarfBuzz"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e021dd6dda6ff1e6b1044002fc662b9b \
                    file://src/hb-ucdn/COPYING;md5=994ba0f1295f15b4bda4999a5bbeddef \
"

DEPENDS = "glib-2.0 cairo fontconfig freetype"

SRC_URI = "http://www.freedesktop.org/software/harfbuzz/release/${BP}.tar.bz2"

SRC_URI[md5sum] = "1466ab51fc5aaa6af4065936939cec62"
SRC_URI[sha256sum] = "84574e1b1f65ca694cb8fb6905309665c0368af18a312357f8ff886ee2f29563"

inherit autotools pkgconfig lib_package gtk-doc

PACKAGECONFIG ??= "icu"
PACKAGECONFIG[icu] = "--with-icu,--without-icu,icu"

EXTRA_OECONF = " \
    --with-cairo \
    --with-fontconfig \
    --with-freetype \
    --with-glib \
    --without-graphite2 \
"

PACKAGES =+ "${PN}-icu ${PN}-icu-dev"

FILES_${PN}-icu = "${libdir}/libharfbuzz-icu.so.*"
FILES_${PN}-icu-dev = "${libdir}/libharfbuzz-icu.la \
                       ${libdir}/libharfbuzz-icu.so \
                       ${libdir}/pkgconfig/harfbuzz-icu.pc \
"

BBCLASSEXTEND = "native"
