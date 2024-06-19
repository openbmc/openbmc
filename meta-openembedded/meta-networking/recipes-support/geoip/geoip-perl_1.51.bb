#
# Copyright (C) 2014, 2015 Wind River Systems, Inc.
# Released under the MIT license (see COPYING.MIT for the terms)
#
SUMMARY = "GeoIP perl API library to access location database"
DESCRIPTION = "perl library for country/city/organization to IP address or hostname mapping"
HOMEPAGE = "http://www.maxmind.com/app/ip-location"
SECTION = "libdevel"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4f3ea6e9b28af88dc0321190a1f8250"

S = "${WORKDIR}/git"
SRCREV = "4cdfdc38eca237c19c22a8b90490446ce6d970fa"
SRC_URI = "git://github.com/maxmind/geoip-api-perl.git;branch=main;protocol=https \
    file://run-ptest \
"

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
RDEPENDS:${PN}-ptest += "perl-modules"

FILES:${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/Geo/IP/.debug"

