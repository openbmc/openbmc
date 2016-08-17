#
# Copyright (C) 2014, 2015 Wind River Systems, Inc.
# Released under the MIT license (see COPYING.MIT for the terms)
#
SUMMARY = "GeoIP perl API library to access location database"
DESCRIPTION = "perl library for country/city/organization to IP address or hostname mapping"
HOMEPAGE = "http://www.maxmind.com/app/ip-location"
SECTION = "libdevel"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;md5=b0fa745303912bd2d64430f7ae69487d"

# Note that we do not want to use the upstream file name locally.
#
SRC_URI = "http://github.com/maxmind/geoip-api-perl/archive/v${PV}.tar.gz;downloadfilename=${BPN}-${PV}.tar.gz \
           file://run-ptest \
          "

SRC_URI[md5sum] = "0ce57140890bf81958e0cea4fe1885b2"
SRC_URI[sha256sum] = "c56437b1cc8887736cb1e435d0320c1c1ff3754830249516317b99137005fb23"

S = "${WORKDIR}/geoip-api-perl-${PV}"

DEPENDS += "geoip"

inherit cpan ptest

EXTRA_CPANFLAGS = "LIBS='-L${STAGING_LIBDIR}' INC='-I${STAGING_INCDIR}'"


# perl scripts and some special small data files
#
do_install_ptest () {
	install -d -m 0755 ${D}${PTEST_PATH}/t/data

	install ${S}/t/*.t* ${D}${PTEST_PATH}/t
	install ${S}/t/data/* ${D}${PTEST_PATH}/t/data
}

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/Geo/IP/.debug"

