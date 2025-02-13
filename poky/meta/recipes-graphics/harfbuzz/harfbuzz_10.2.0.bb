SUMMARY = "Text shaping library"
DESCRIPTION = "HarfBuzz is an OpenType text shaping engine."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/HarfBuzz"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=HarfBuzz"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b98429b8e8e3c2a67cfef01e99e4893d \
                    file://src/hb-ucd.cc;beginline=1;endline=15;md5=29d4dcb6410429195df67efe3382d8bc \
                    "

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "620e3468faec2ea8685d32c46a58469b850ef63040b3565cde05959825b48227"

DEPENDS += "glib-2.0-native"

inherit meson pkgconfig lib_package gtk-doc gobject-introspection github-releases

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_ENABLE_FLAG = 'enabled'
GTKDOC_MESON_DISABLE_FLAG = 'disabled'

PACKAGECONFIG ??= "cairo freetype glib icu"
PACKAGECONFIG[cairo] = "-Dcairo=enabled,-Dcairo=disabled,cairo"
PACKAGECONFIG[chafa] = "-Dchafa=enabled,-Dchafa=disabled,chafa"
PACKAGECONFIG[freetype] = "-Dfreetype=enabled,-Dfreetype=disabled,freetype"
PACKAGECONFIG[glib] = "-Dglib=enabled,-Dglib=disabled,glib-2.0"
PACKAGECONFIG[graphite] = "-Dgraphite2=enabled,-Dgraphite2=disabled,graphite2"
PACKAGECONFIG[icu] = "-Dicu=enabled,-Dicu=disabled,icu"

PACKAGES =+ "${PN}-icu ${PN}-icu-dev ${PN}-subset"

LEAD_SONAME = "libharfbuzz.so"

# Remove when https://github.com/harfbuzz/harfbuzz/issues/4671 is resolved
EXTRA_OEMESON += "-Dcpp_std=c++17"

do_install:append() {
    # If no tools are installed due to PACKAGECONFIG then this directory might
    # still be installed, so remove it to stop packaging warnings.
    [ ! -d ${D}${bindir} ] || rmdir --ignore-fail-on-non-empty ${D}${bindir}
}

FILES:${PN}-icu = "${libdir}/libharfbuzz-icu.so.*"
FILES:${PN}-icu-dev = "${libdir}/libharfbuzz-icu.la \
                       ${libdir}/libharfbuzz-icu.so \
                       ${libdir}/pkgconfig/harfbuzz-icu.pc \
"
FILES:${PN}-subset = "${libdir}/libharfbuzz-subset.so.*"

BBCLASSEXTEND = "native nativesdk"
