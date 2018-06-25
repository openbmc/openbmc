#
# Copyright (C) 2014, 2015 Wind River Systems, Inc.
# Released under the MIT license (see COPYING.MIT for the terms)
#
SUMMARY = "GeoIP perl API library to access location database"
DESCRIPTION = "perl library for country/city/organization to IP address or hostname mapping"
HOMEPAGE = "http://www.maxmind.com/app/ip-location"
SECTION = "libdevel"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b7a36f55e8ba62aadd74e4f0886a405e"

S = "${WORKDIR}/git"
SRCREV = "47f7d49bd15cfc2e5f8c0f5c4068dc8bb0e10e96"
SRC_URI = "git://github.com/maxmind/geoip-api-perl.git;protocol=https; \
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

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/Geo/IP/.debug"

