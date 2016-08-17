SUMMARY = "PROJ.4 - Cartographic Projections library"
HOMEPAGE = "http://trac.osgeo.org/proj/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=74d9aaec5fa0cd734341e8c4dc91b608"

SRC_URI = "http://download.osgeo.org/proj/proj-${PV}.tar.gz"
SRC_URI[md5sum] = "d815838c92a29179298c126effbb1537"
SRC_URI[sha256sum] = "2db2dbf0fece8d9880679154e0d6d1ce7c694dd8e08b4d091028093d87a9d1b5"

inherit autotools pkgconfig lib_package

FILES_${PN} += "${datadir}/proj"
