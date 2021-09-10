SUMMARY = "Library tasked with managing, extracting and handling media art caches"

LICENSE = "LGPLv2+ & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
                    file://libmediaart/extract.c;endline=18;md5=dff2b6328ab067b5baadc135f9876c36 \
                    file://tests/mediaarttest.c;endline=18;md5=067106eaa1f7a9d918759a096667f18e"

DEPENDS = "glib-2.0 gdk-pixbuf"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gobject-introspection vala features_check

SRC_URI = "${GNOME_MIRROR}/libmediaart/1.9/libmediaart-${PV}.tar.xz"
SRC_URI[sha256sum] = "07def5a42c482ae71d3e1f77a4d0fdc337f74226059a65284d6d5a241f0e9cd6"

S = "${WORKDIR}/libmediaart-${PV}"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST = "introspection"

EXTRA_OEMESON = "-Dimage_library=gdk-pixbuf"
