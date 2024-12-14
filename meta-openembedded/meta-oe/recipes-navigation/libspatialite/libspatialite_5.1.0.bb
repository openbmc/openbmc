DESCRIPTION = "Library extending the SQLite core to support fully fledged Spatial SQL capabilities"
HOMEPAGE = "https://www.gaia-gis.it/fossil/libspatialite/"
SECTION = "libs"
DEPENDS = "proj geos sqlite3 libxml2 zlib"

LICENSE = "MPL-1.1 & GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0e92e1a36cc384b60f5b31dde0bdd39e"

SRC_URI = "http://www.gaia-gis.it/gaia-sins/libspatialite-sources/libspatialite-${PV}.tar.gz \
           file://libspatialite_geos.patch \
           file://libspatialite_pkgconfig.patch \
           file://0001-wfs-Cover-xmlNanoHTTPCleanup-with-LIBXML_HTTP_ENABLE.patch \
"
SRC_URI[sha256sum] = "43be2dd349daffe016dd1400c5d11285828c22fea35ca5109f21f3ed50605080"

inherit autotools-brokensep pkgconfig

EXTRA_OECONF = "--enable-freexl=no --disable-rttopo --disable-minizip"

# package plugins for SQLite3
PACKAGES += "${PN}-plugin"
INSANE_SKIP:${PN}-plugin = "dev-so"
FILES:${PN}-plugin += "${libdir}/mod_*"
