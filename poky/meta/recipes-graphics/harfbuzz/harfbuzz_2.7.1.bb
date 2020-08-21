SUMMARY = "Text shaping library"
DESCRIPTION = "HarfBuzz is an OpenType text shaping engine."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/HarfBuzz"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=HarfBuzz"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f787620b7d3866d9552fd1924c07572 \
                    file://src/hb-ucd.cc;beginline=1;endline=15;md5=29d4dcb6410429195df67efe3382d8bc"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "harfbuzz-(?P<pver>\d+(\.\d+)+).tar"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "e95ee43b6bd0d3d1307e2aacf0f9c0050e5baceb21988b367b833028114aa569"

inherit autotools pkgconfig lib_package gtk-doc

PACKAGECONFIG ??= "cairo fontconfig freetype glib icu"
PACKAGECONFIG[cairo] = "--with-cairo,--without-cairo,cairo"
PACKAGECONFIG[fontconfig] = "--with-fontconfig,--without-fontconfig,fontconfig"
PACKAGECONFIG[freetype] = "--with-freetype,--without-freetype,freetype"
PACKAGECONFIG[glib] = "--with-glib,--without-glib,glib-2.0"
PACKAGECONFIG[graphite] = "--with-graphite2,--without-graphite2,graphite2"
PACKAGECONFIG[icu] = "--with-icu,--without-icu,icu"

PACKAGES =+ "${PN}-icu ${PN}-icu-dev ${PN}-subset"

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
FILES_${PN}-subset = "${libdir}/libharfbuzz-subset.so.*"

BBCLASSEXTEND = "native nativesdk"
