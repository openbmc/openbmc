SUMMARY = "PROJ.4 - Cartographic Projections library"
HOMEPAGE = "http://trac.osgeo.org/proj/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=74d9aaec5fa0cd734341e8c4dc91b608"

SRC_URI = "http://download.osgeo.org/${BPN}/${BP}.tar.gz"
SRC_URI[sha256sum] = "ee0e14c1bd2f9429b1a28999240304c0342ed739ebaea3d4ff44c585b1097be8"

DEPENDS = "sqlite3 sqlite3-native tiff"

inherit autotools pkgconfig lib_package

PACKAGECONFIG ?= "curl"

PACKAGECONFIG[curl] = ",--without-curl,curl"

FILES_${PN} += "${datadir}/proj"
