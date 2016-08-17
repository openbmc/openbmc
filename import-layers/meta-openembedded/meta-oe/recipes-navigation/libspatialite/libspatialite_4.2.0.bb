DESCRIPTION = "Library extending the SQLite core to support fully fledged Spatial SQL capabilities"
HOMEPAGE = "https://www.gaia-gis.it/fossil/libspatialite/"
SECTION = "libs"
DEPENDS = "proj geos sqlite3 libxml2 zlib"

LICENSE = "MPLv1.1 & GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0e92e1a36cc384b60f5b31dde0bdd39e"

SRC_URI = "http://www.gaia-gis.it/gaia-sins/libspatialite-sources/libspatialite-${PV}.tar.gz"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-freexl=no"

# package plugins for SQLite3
PACKAGES += "${PN}-plugin"
INSANE_SKIP_${PN}-plugin = "dev-so"
FILES_${PN}-plugin += "${libdir}/mod_*"

SRC_URI[md5sum] = "83305ed694a77152120d1f74c5151779"
SRC_URI[sha256sum] = "9f138a6854740c7827fdee53845eb1485fce3e805a7aa9fc9151f8046ebd312d"

SRC_URI += "file://geos-config.patch"
