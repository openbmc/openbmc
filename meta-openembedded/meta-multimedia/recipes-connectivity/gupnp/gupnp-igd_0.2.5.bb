SUMMARY = "Helpers for interacting with Internet Gateway Devices over UPnP"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://libgupnp-igd/gupnp-simple-igd.c;beginline=1;endline=21;md5=aa292c0d9390463a6e1055dc5fc68e80"

DEPENDS = "glib-2.0 gssdp gupnp sqlite3"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.2/${BPN}-${PV}.tar.xz \
           file://0001-Swtich-to-new-GUPnP-API.patch \
          "
SRC_URI[md5sum] = "d164e096d0f140bb1f5d9503727b424a"
SRC_URI[sha256sum] = "8b4a1aa38bacbcac2c1755153147ead7ee9af7d4d1f544b6577cfc35e10e3b20"

inherit autotools pkgconfig gtk-doc gobject-introspection

EXTRA_OECONF = "--disable-python"
