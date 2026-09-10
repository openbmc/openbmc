SUMMARY = "GNT: The GLib Ncurses Toolkit"

SECTION = "libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9a1abacd0166f595a9fbe6afb1f0d5e"
DEPENDS = "glib-2.0 ncurses libxml2 glib-2.0-native"

inherit meson pkgconfig

# SRCREV = "0a44b1d01c41"
# SRC_URI = "hg://keep.imfreedom.org/${BPN};module=${BPN}

SRC_URI = "${SOURCEFORGE_MIRROR}/project/pidgin/${BPN}/${PV}/${BP}.tar.xz \
    file://0001-meson-use-pkg-config-to-find-ncursesw-panelw.patch \
"
SRC_URI[sha256sum] = "57f5457f72999d0bb1a139a37f2746ec1b5a02c094f2710a339d8bcea4236123"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/pidgin/files/libgnt/"
UPSTREAM_CHECK_REGEX = "${BPN}/(?P<pver>\d+(\.\d+)+)"

EXTRA_OEMESON = "-Dpython2=false -Ddoc=false"

FILES:${PN} += "${libdir}/gnt/s.so ${libdir}/gnt/irssi.so"
