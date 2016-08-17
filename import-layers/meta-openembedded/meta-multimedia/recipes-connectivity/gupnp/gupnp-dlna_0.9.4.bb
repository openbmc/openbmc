SUMMARY = "Helpers for AV applications using DLNA"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://libgupnp-dlna/gupnp-dlna-profile.h;beginline=1;endline=22;md5=1b85459f65cb1e73a885ca137aab6274"

DEPENDS = "libxml2 glib-2.0"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.9/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "f93665e535a512e4d515a86311435cb6"
SRC_URI[sha256sum] = "69969713f36c0e815fbbbcfdfb3ad9bd447cfd10d0fd86227d82dfd8edb6c807"

inherit autotools pkgconfig gobject-introspection vala

PACKAGECONFIG ?= "gstreamer"
PACKAGECONFIG[gstreamer] = "--enable-gstreamer-metadata-backend,--disable-gstreamer-metadata-backend,gstreamer1.0 gstreamer1.0-plugins-base"

FILES_${PN} += "${datadir}/gupnp-dlna-2.0/dlna-profiles"
