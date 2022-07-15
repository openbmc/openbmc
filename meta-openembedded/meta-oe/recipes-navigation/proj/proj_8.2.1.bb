SUMMARY = "PROJ.4 - Cartographic Projections library"
HOMEPAGE = "http://trac.osgeo.org/proj/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27445198ba1500f508fce2b183ce0ff"

SRC_URI = "http://download.osgeo.org/${BPN}/${BP}.tar.gz"

SRC_URI[sha256sum] = "76ed3d0c3a348a6693dfae535e5658bbfd47f71cb7ff7eb96d9f12f7e068b1cf"

DEPENDS = "sqlite3 sqlite3-native tiff"

inherit autotools pkgconfig lib_package

PACKAGECONFIG ?= "curl"

PACKAGECONFIG[curl] = ",--without-curl,curl"

FILES:${PN} += "${datadir}/proj"

BBCLASSEXTEND = "native"
