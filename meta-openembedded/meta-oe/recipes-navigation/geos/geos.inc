DESCRIPTION = "GEOS - Geometry Engine, Open Source"
HOMEPAGE = "http://trac.osgeo.org/geos/"
SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "http://download.osgeo.org/geos/geos-${PV}.tar.bz2"

inherit autotools pkgconfig binconfig

PACKAGES =+ "geoslib ${PN}-c1"

DESCRIPTION:${PN}lib = "Geometry engine for Geographic Information Systems - C++ Library"
FILES:${PN}lib += "${libdir}/libgeos-${PV}.so"

DESCRIPTION:${PN}-c1 = "Geometry engine for Geographic Information Systems - C Library"
FILES:${PN}-c1 += "${libdir}/libgeos_c.so.*"

ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} += "geoslib ${PN}-c1"
