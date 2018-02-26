SUMMARY = "Helpers for interacting with Internet Gateway Devices over UPnP"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://libgupnp-igd/gupnp-simple-igd.c;beginline=1;endline=21;md5=aa292c0d9390463a6e1055dc5fc68e80"

DEPENDS = "glib-2.0 gupnp sqlite3"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.2/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "124371136b5a7b1056a3681780a62772"
SRC_URI[sha256sum] = "38c4a6d7718d17eac17df95a3a8c337677eda77e58978129ad3182d769c38e44"

inherit autotools pkgconfig gtk-doc gobject-introspection

EXTRA_OECONF = "--disable-python"
