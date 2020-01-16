SUMMARY = "PROJ.4 - Cartographic Projections library"
HOMEPAGE = "http://trac.osgeo.org/proj/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=74d9aaec5fa0cd734341e8c4dc91b608"

SRC_URI = "http://download.osgeo.org/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "5cde556545828beaffbe50b1bb038480"
SRC_URI[sha256sum] = "b300c0f872f632ad7f8eb60725edbf14f0f8f52db740a3ab23e7b94f1cd22a50"

DEPENDS = "sqlite3 sqlite3-native"

inherit autotools pkgconfig lib_package

FILES_${PN} += "${datadir}/proj"
