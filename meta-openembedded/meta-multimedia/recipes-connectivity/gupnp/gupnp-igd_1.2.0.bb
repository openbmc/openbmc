SUMMARY = "Helpers for interacting with Internet Gateway Devices over UPnP"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://libgupnp-igd/gupnp-simple-igd.c;beginline=1;endline=21;md5=aa292c0d9390463a6e1055dc5fc68e80"

DEPENDS = "glib-2.0 gssdp gupnp sqlite3"

SRC_URI = "http://download.gnome.org/sources/${BPN}/1.2/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "4b5120098aa13edd27818ba9ee4d7fe961bf540bf50d056ff703c61545e02be1"

GTKDOC_MESON_OPTION = "gtk_doc"

inherit meson pkgconfig gtk-doc gobject-introspection
