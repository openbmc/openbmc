SUMMARY = "DLNA server libraries"
DESCRIPTION = "dleyna-server is a library for implementing services that \
allow clients to discover, browse and manipulate Digital Media Servers. \
An implementation of such a service for linux is also included."
HOMEPAGE = "https://01.org/dleyna/"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://libdleyna/server/server.c;endline=22;md5=437455d8aeff69ebd0996a76c67397bb"

DEPENDS = "glib-2.0 gssdp gupnp gupnp-av gupnp-dlna libsoup-2.4 libxml2 dleyna-core"
RDEPENDS_${PN} = "dleyna-connector-dbus"

SRC_URI = "git://github.com/01org/${BPN}.git"
SRCREV = "776950d5d96ac9dbf5c5c47bde8ac06f50a3cf46"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

FILES_${PN} += "${datadir}/dbus-1"
FILES_${PN}-dev += "${libdir}/${PN}/*.so"
