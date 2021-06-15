SUMMARY = "DLNA renderer libraries"
DESCRIPTION = "dleyna-renderer is a library for implementing services \
that allow clients to discover and manipulate Digital Media Renderers. \
An implementation of such a service for linux is also included."
HOMEPAGE = "https://01.org/dleyna/"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://libdleyna/renderer/server.c;endline=21;md5=f51acd4757fb6a779a87122c43cf1346"

DEPENDS = "glib-2.0 gssdp gupnp gupnp-av gupnp-dlna libsoup-2.4 dleyna-core"
RDEPENDS_${PN} = "dleyna-connector-dbus"

SRC_URI = "git://github.com/01org/${BPN}.git \
           file://0001-add-gupnp-1.2-API-support.patch \
          "
SRCREV = "50fd1ec9d51328e7dea98874129dc8d6fe3ea1dd"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

CFLAGS += " -I${S}"

FILES_${PN} += "${datadir}/dbus-1"
FILES_${PN}-dev += "${libdir}/${PN}/*.so"
