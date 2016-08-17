SUMMARY = "The Geolocation Service"
DESCRIPTION = "Geoclue is a D-Bus service that provides location information. \
The primary goal of the Geoclue project is to make creating location-aware applications \
as simple as possible, while the secondary goal is to ensure that no application \
can access location information without explicit permission from user."
LICENSE = "GPLv2.0+"
SECTION = "console/network"

LIC_FILES_CHKSUM = "file://COPYING;md5=8114b83a0435d8136b47bd70111ce5cd"

DEPENDS = "glib-2.0 dbus dbus-glib json-glib libsoup-2.4"

inherit autotools pkgconfig gtk-doc

SRC_URI = " \
  http://www.freedesktop.org/software/geoclue/releases/2.0/geoclue-${PV}.tar.xz \
  file://soup-session-fix.patch \
"

SRC_URI[md5sum] = "401ff99d530b177c62afacef0a33efd9"
SRC_URI[sha256sum] = "4a82f184e55a163d86e0ad69bbe1bba9960bb5094220fe1f01350bceda8c67a1"

PACKAGECONFIG[geoip] = "--enable-geoip-server,--disable-geoip-server,geoip"

EXTRA_OECONF += " \
  --with-dbus-service-user=root \
  --with-dbus-sys-dir=${sysconfdir}/dbus-1/system.d \
"

FILES_${PN} += " \
  ${datadir}/dbus-1/system-services/org.freedesktop.GeoClue2.service \
  ${datadir}/geoclue-2.0/geoclue-interface.xml \
"
