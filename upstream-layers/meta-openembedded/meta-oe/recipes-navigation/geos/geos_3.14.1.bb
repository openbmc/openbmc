DESCRIPTION = "GEOS - Geometry Engine, Open Source"
HOMEPAGE = "http://trac.osgeo.org/geos/"
SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "http://download.osgeo.org/${BPN}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "3c20919cda9a505db07b5216baa980bacdaa0702da715b43f176fb07eff7e716"

inherit cmake pkgconfig binconfig

PACKAGES =+ "geoslib ${PN}-c1"

DESCRIPTION:${PN}lib = "Geometry engine for Geographic Information Systems - C++ Library"
FILES:${PN}lib += "${libdir}/libgeos.so.*"

DESCRIPTION:${PN}-c1 = "Geometry engine for Geographic Information Systems - C Library"
FILES:${PN}-c1 += "${libdir}/libgeos_c.so.*"

ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} += "geoslib ${PN}-c1"
