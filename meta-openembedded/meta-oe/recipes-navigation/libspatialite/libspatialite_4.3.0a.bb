DESCRIPTION = "Library extending the SQLite core to support fully fledged Spatial SQL capabilities"
HOMEPAGE = "https://www.gaia-gis.it/fossil/libspatialite/"
SECTION = "libs"
DEPENDS = "proj geos sqlite3 libxml2 zlib"

LICENSE = "MPLv1.1 & GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0e92e1a36cc384b60f5b31dde0bdd39e"

SRC_URI = "http://www.gaia-gis.it/gaia-sins/libspatialite-sources/libspatialite-${PV}.tar.gz \
           file://geos-config.patch"
SRC_URI[md5sum] = "6b380b332c00da6f76f432b10a1a338c"
SRC_URI[sha256sum] = "88900030a4762904a7880273f292e5e8ca6b15b7c6c3fb88ffa9e67ee8a5a499"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-freexl=no"
CFLAGS += "-DACCEPT_USE_OF_DEPRECATED_PROJ_API_H"

# package plugins for SQLite3
PACKAGES += "${PN}-plugin"
INSANE_SKIP_${PN}-plugin = "dev-so"
FILES_${PN}-plugin += "${libdir}/mod_*"
