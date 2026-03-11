SUMMARY = "Helpers for interacting with Internet Gateway Devices over UPnP"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://libgupnp-igd/gupnp-simple-igd.c;beginline=1;endline=21;md5=aa292c0d9390463a6e1055dc5fc68e80"

DEPENDS = "glib-2.0 gssdp gupnp"

inherit gnomebase pkgconfig gtk-doc gobject-introspection

SRC_URI[archive.sha256sum] = "4099978339ab22126d4968f2a332b6d094fc44c78797860781f1fc2f11771b74"

GTKDOC_MESON_OPTION = "gtk_doc"
