SUMMARY = "Helpers for AV applications using DLNA"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://libgupnp-dlna/gupnp-dlna-profile.h;beginline=1;endline=22;md5=1b85459f65cb1e73a885ca137aab6274"

DEPENDS = "libxml2 glib-2.0"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.10/${BPN}-${PV}.tar.xz \
           file://move_vapigen.patch \
"
SRC_URI[md5sum] = "2d0dc1e4189d0243ac3838ece1e8fea0"
SRC_URI[sha256sum] = "123e368227c11d5c17fc1aa76cbdaffa345355eb51d172cd39fc74a5b468ff6a"

inherit autotools pkgconfig gobject-introspection vala

PACKAGECONFIG ?= "gstreamer"
PACKAGECONFIG[gstreamer] = "--enable-gstreamer-metadata-backend,--disable-gstreamer-metadata-backend,gstreamer1.0 gstreamer1.0-plugins-base"

FILES_${PN} += "${datadir}/gupnp-dlna-2.0/dlna-profiles"
