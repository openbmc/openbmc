SUMMARY = "Text shaping library"
DESCRIPTION = "HarfBuzz is an OpenType text shaping engine."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/HarfBuzz"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=HarfBuzz"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e11f5c3149cdec4bb309babb020b32b9 \
                    file://src/hb-ucd.cc;md5=343f1797944de49ab2b5f5cf3126d3f2"

DEPENDS = "glib-2.0 cairo fontconfig freetype"

SRC_URI = "http://www.freedesktop.org/software/harfbuzz/release/${BP}.tar.xz"
SRC_URI[md5sum] = "29a310e74a90b4e4f6d62f3e74a571e5"
SRC_URI[sha256sum] = "fed00dc797b7ba3ca943225f0a854baaed4c1640fff8a31d455cd3b5caec855c"

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

LEAD_SONAME = "libharfbuzz.so"

FILES_${PN}-icu = "${libdir}/libharfbuzz-icu.so.*"
FILES_${PN}-icu-dev = "${libdir}/libharfbuzz-icu.la \
                       ${libdir}/libharfbuzz-icu.so \
                       ${libdir}/pkgconfig/harfbuzz-icu.pc \
"

BBCLASSEXTEND = "native nativesdk"
