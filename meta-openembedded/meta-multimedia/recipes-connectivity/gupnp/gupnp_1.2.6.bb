require gupnp.inc

SRC_URI = " \
    ${GNOME_MIRROR}/${BPN}/1.2/${BPN}-${PV}.tar.xz \
    file://0001-Fix-build-with-hardened-security-flags.patch \
"
SRC_URI[sha256sum] = "00b20f1e478a72deac92c34723693a2ac55789ed1e4bb4eed99eb4d62092aafd"

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://libgupnp/gupnp.h;beginline=1;endline=20;md5=d78a69d9b6e63ee2dc72e7b674d97520"
