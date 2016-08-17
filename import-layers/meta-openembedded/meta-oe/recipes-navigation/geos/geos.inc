DESCRIPTION = "GEOS - Geometry Engine, Open Source"
HOMEPAGE = "http://trac.osgeo.org/geos/"
SECTION = "libs"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "http://download.osgeo.org/geos/geos-${PV}.tar.bz2"

inherit autotools pkgconfig binconfig

PACKAGES =+ "geoslib ${PN}-c1"

DESCRIPTION_${PN}lib = "Geometry engine for Geographic Information Systems - C++ Library"
FILES_${PN}lib += "${libdir}/libgeos-${PV}.so"

DESCRIPTION_${PN}-c1 = "Geometry engine for Geographic Information Systems - C Library"
FILES_${PN}-c1 += "${libdir}/libgeos_c.so.*"

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "geoslib ${PN}-c1"
