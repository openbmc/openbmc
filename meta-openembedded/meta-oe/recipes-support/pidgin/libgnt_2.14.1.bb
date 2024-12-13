SUMMARY = "GNT: The GLib Ncurses Toolkit"

SECTION = "libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9a1abacd0166f595a9fbe6afb1f0d5e"
DEPENDS = "glib-2.0 ncurses libxml2 glib-2.0-native"

inherit meson pkgconfig

# SRCREV = "0a44b1d01c41"
# SRC_URI = "hg://keep.imfreedom.org/${BPN};module=${BPN}

SRC_URI = "${SOURCEFORGE_MIRROR}/project/pidgin/${BPN}/${PV}/${BP}.tar.xz \
    file://0001-meson-import-changes-from-3.0.-version.patch \
"
SRC_URI[sha256sum] = "5ec3e68e18f956e9998d79088b299fa3bca689bcc95c86001bc5da17c1eb4bd8"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/pidgin/files/libgnt/"
UPSTREAM_CHECK_REGEX = "${BPN}/(?P<pver>\d+(\.\d+)+)"

EXTRA_OEMESON = "-Dintrospection=false -Ddoc=false"

FILES:${PN} += "${libdir}/gnt/s.so ${libdir}/gnt/irssi.so"
