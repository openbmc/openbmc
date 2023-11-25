SUMMARY = "DLNA server libraries"
DESCRIPTION = "dleyna-server is a library for implementing services that \
allow clients to discover, browse and manipulate Digital Media Servers. \
An implementation of such a service for linux is also included."
HOMEPAGE = "https://01.org/dleyna/"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://libdleyna/server/server.c;endline=22;md5=437455d8aeff69ebd0996a76c67397bb"

DEPENDS = "glib-2.0 gssdp gupnp gupnp-av gupnp-dlna libsoup-2.4 libxml2 dleyna-core"
RDEPENDS:${PN} = "dleyna-connector-dbus"

PV .= "+git${SRCPV}"
SRC_URI = "git://github.com/01org/${BPN}.git;branch=master;protocol=https"
SRCREV = "eb895ae82715e9889a948ffa810c0f828b4f4c76"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

FILES:${PN} += "${datadir}/dbus-1"
FILES:${PN}-dev += "${libdir}/${BPN}/*.so"

SKIP_RECIPE[dleyna-server] ?= "Upstream is dead moreover needs porting to work with latest gupnp >= 1.2"
