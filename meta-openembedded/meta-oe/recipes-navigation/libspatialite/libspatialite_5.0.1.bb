DESCRIPTION = "Library extending the SQLite core to support fully fledged Spatial SQL capabilities"
HOMEPAGE = "https://www.gaia-gis.it/fossil/libspatialite/"
SECTION = "libs"
DEPENDS = "proj geos sqlite3 libxml2 zlib"

LICENSE = "MPL-1.1 & GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0e92e1a36cc384b60f5b31dde0bdd39e"

SRC_URI = "http://www.gaia-gis.it/gaia-sins/libspatialite-sources/libspatialite-${PV}.tar.gz \
           file://libspatialite_geos.patch \
           file://libspatialite_macros.patch \
           file://libspatialite_pkgconfig.patch \
"

SRC_URI[sha256sum] = "eecbc94311c78012d059ebc0fae86ea5ef6eecb13303e6e82b3753c1b3409e98"

inherit autotools-brokensep pkgconfig

EXTRA_OECONF = "--enable-freexl=no --disable-rttopo --disable-minizip"

# package plugins for SQLite3
PACKAGES += "${PN}-plugin"
INSANE_SKIP:${PN}-plugin = "dev-so"
FILES:${PN}-plugin += "${libdir}/mod_*"
