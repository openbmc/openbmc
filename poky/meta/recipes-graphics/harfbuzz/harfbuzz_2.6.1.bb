SUMMARY = "Text shaping library"
DESCRIPTION = "HarfBuzz is an OpenType text shaping engine."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/HarfBuzz"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=HarfBuzz"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e11f5c3149cdec4bb309babb020b32b9 \
                    file://src/hb-ucd.cc;beginline=1;endline=15;md5=29d4dcb6410429195df67efe3382d8bc"

SRC_URI = "http://www.freedesktop.org/software/harfbuzz/release/${BP}.tar.xz"
SRC_URI[md5sum] = "89b758a2eb14d56a94406cf914b62388"
SRC_URI[sha256sum] = "c651fb3faaa338aeb280726837c2384064cdc17ef40539228d88a1260960844f"

inherit autotools pkgconfig lib_package gtk-doc

PACKAGECONFIG ??= "cairo fontconfig freetype glib icu"
PACKAGECONFIG[cairo] = "--with-cairo,--without-cairo,cairo"
PACKAGECONFIG[fontconfig] = "--with-fontconfig,--without-fontconfig,fontconfig"
PACKAGECONFIG[freetype] = "--with-freetype,--without-freetype,freetype"
PACKAGECONFIG[glib] = "--with-glib,--without-glib,glib-2.0"
PACKAGECONFIG[graphite] = "--with-graphite2,--without-graphite2,graphite2"
PACKAGECONFIG[icu] = "--with-icu,--without-icu,icu"

PACKAGES =+ "${PN}-icu ${PN}-icu-dev"

LEAD_SONAME = "libharfbuzz.so"

do_install_append() {
    # If no tools are installed due to PACKAGECONFIG then this directory is
    #still installed, so remove it to stop packaging wanings.
    rmdir --ignore-fail-on-non-empty ${D}${bindir}
}

FILES_${PN}-icu = "${libdir}/libharfbuzz-icu.so.*"
FILES_${PN}-icu-dev = "${libdir}/libharfbuzz-icu.la \
                       ${libdir}/libharfbuzz-icu.so \
                       ${libdir}/pkgconfig/harfbuzz-icu.pc \
"

BBCLASSEXTEND = "native nativesdk"
