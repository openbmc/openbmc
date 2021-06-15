SUMMARY = "PROJ.4 - Cartographic Projections library"
HOMEPAGE = "http://trac.osgeo.org/proj/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=74d9aaec5fa0cd734341e8c4dc91b608"

SRC_URI = "http://download.osgeo.org/${BPN}/${BP}.tar.gz"

SRC_URI[md5sum] = "5ba7536b579a6c9e0ad822dbdd455985"
SRC_URI[sha256sum] = "a7026d39c9c80d51565cfc4b33d22631c11e491004e19020b3ff5a0791e1779f"

DEPENDS = "sqlite3 sqlite3-native tiff"

inherit autotools pkgconfig lib_package

PACKAGECONFIG ?= "curl"

PACKAGECONFIG[curl] = ",--without-curl,curl"

FILES_${PN} += "${datadir}/proj"
