SUMMARY = "Text shaping library"
DESCRIPTION = "HarfBuzz is an OpenType text shaping engine."
HOMEPAGE = "https://harfbuzz.github.io"
BUGTRACKER = "https://github.com/harfbuzz/harfbuzz"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b98429b8e8e3c2a67cfef01e99e4893d \
                    file://src/hb-ucd.cc;beginline=1;endline=15;md5=29d4dcb6410429195df67efe3382d8bc \
                    "

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "94017020f96d025bb66ae91574e4cf334bcad23e8175a8a40565b3721bc2eaff"

inherit meson pkgconfig lib_package gtk-doc gobject-introspection github-releases

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_ENABLE_FLAG = 'enabled'
GTKDOC_MESON_DISABLE_FLAG = 'disabled'

# As per upstream CONFIG.md, it is recommended to always build with -Os.
FULL_OPTIMIZATION = "-Os ${DEBUG_LEVELFLAG}"

EXTRA_OEMESON = "-Dtests=disabled"

PACKAGECONFIG ??= "cairo gobject subset glib icu freetype"

# Optional harfbuzz libraries
PACKAGECONFIG[cairo] = "-Dcairo=enabled,-Dcairo=disabled,cairo"
PACKAGECONFIG[gobject] = "-Dgobject=enabled,-Dgobject=disabled,glib-2.0-native glib-2.0"
PACKAGECONFIG[gpu] = "-Dgpu=enabled,-Dgpu=disabled"
PACKAGECONFIG[raster] = "-Draster=enabled,-Draster=disabled"
PACKAGECONFIG[subset] = "-Dsubset=enabled,-Dsubset=disabled"
PACKAGECONFIG[vector] = "-Dvector=enabled,-Dvector=disabled"

# Unicode providers
PACKAGECONFIG[glib] = "-Dglib=enabled,-Dglib=disabled,glib-2.0"
PACKAGECONFIG[icu] = "-Dicu=enabled,-Dicu=disabled,icu"

# Optional dependencies
PACKAGECONFIG[chafa] = "-Dchafa=enabled,-Dchafa=disabled,chafa"
PACKAGECONFIG[freetype] = "-Dfreetype=enabled,-Dfreetype=disabled,freetype"
PACKAGECONFIG[graphite] = "-Dgraphite2=enabled,-Dgraphite2=disabled,graphite2"
# Optional dependency in the raster library
PACKAGECONFIG[png] = "-Dpng=enabled,-Dpng=disabled,libpng"
# Optional dependency in the raster and vector libraries
PACKAGECONFIG[zlib] = "-Dzlib=enabled,-Dzlib=disabled,zlib"

do_install:append() {
    # If no tools are installed due to PACKAGECONFIG then this directory might
    # still be installed, so remove it to stop packaging warnings.
    [ ! -d ${D}${bindir} ] || rmdir --ignore-fail-on-non-empty ${D}${bindir}
}

python populate_packages:prepend () {
    do_split_packages(d, '${libdir}', r'^libharfbuzz-(.+)\.so\..*$', '${PN}-%s', 'HarfBuzz %s library', allow_links=True, prepend=True)
}

PACKAGES_DYNAMIC = "^${PN}-.*"

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2024-56732] = "fixed-version: affected versions are >= 8.5.0, <= 10.0.1"
