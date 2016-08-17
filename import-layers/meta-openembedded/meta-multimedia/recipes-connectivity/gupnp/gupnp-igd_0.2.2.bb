SUMMARY = "Helpers for interacting with Internet Gateway Devices over UPnP"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://libgupnp-igd/gupnp-simple-igd.c;beginline=1;endline=21;md5=aa292c0d9390463a6e1055dc5fc68e80"

DEPENDS = "glib-2.0 gupnp sqlite3"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.2/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "f881323304185c02634034e3bda714ba"
SRC_URI[sha256sum] = "73b6a98a0f13b29b34c3bfc07f99f78b1319211cb95a8585752873af2b9067d3"

inherit autotools pkgconfig gtk-doc gobject-introspection

EXTRA_OECONF = "--disable-python"
