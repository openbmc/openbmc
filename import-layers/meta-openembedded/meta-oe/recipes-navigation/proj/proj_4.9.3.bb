SUMMARY = "PROJ.4 - Cartographic Projections library"
HOMEPAGE = "http://trac.osgeo.org/proj/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=74d9aaec5fa0cd734341e8c4dc91b608"

SRC_URI = "http://download.osgeo.org/proj/proj-${PV}.tar.gz"
SRC_URI[md5sum] = "d598336ca834742735137c5674b214a1"
SRC_URI[sha256sum] = "6984542fea333488de5c82eea58d699e4aff4b359200a9971537cd7e047185f7"

inherit autotools pkgconfig lib_package

FILES_${PN} += "${datadir}/proj"
