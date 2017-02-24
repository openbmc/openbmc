SUMMARY = "C library for country/city/organization to IP address or hostname mapping"
DESCRIPTION = "GeoIP is a C library that enables the user to find the country that any IP \
address or hostname originates from. It uses a file based database that is \
accurate as of March 2003. This database simply contains IP blocks as keys, and \
countries as values. This database should be more complete and accurate than \
using reverse DNS lookups."

HOMEPAGE = "http://dev.maxmind.com/geoip/"
SECTION = "libdevel"

SRC_URI = "git://github.com/maxmind/geoip-api-c.git \
           http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz;apply=no;name=GeoIP-dat \
           http://geolite.maxmind.com/download/geoip/database/GeoIPv6.dat.gz;apply=no;name=GeoIPv6-dat \
           http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz;apply=no;name=GeoLiteCity-dat \
           http://geolite.maxmind.com/download/geoip/database/GeoLiteCityv6-beta/GeoLiteCityv6.dat.gz;apply=no;name=GeoLiteCityv6-dat \
           file://run-ptest \
"
SRCREV = "4f487bf95532e0bba7783d591faff178ab0aa462"

SRC_URI[GeoIP-dat.md5sum] = "37c84ead332dda0362a5ac7b049b72d4"
SRC_URI[GeoIP-dat.sha256sum] = "79ff1099e96c2dc1c2539c9a18aaa13a9afd085cae477df60d95f1644d42bc07"

SRC_URI[GeoIPv6-dat.md5sum] = "e75b84a4044e81d6d4484e33816bc762"
SRC_URI[GeoIPv6-dat.sha256sum] = "a009b0f21968d2868e6dd19d14f3c3b8cd60ae84a4bfc2970df34d771a04811e"

SRC_URI[GeoLiteCity-dat.md5sum] = "4b6588d0bfe1af22e267ac90aa97f769"
SRC_URI[GeoLiteCity-dat.sha256sum] = "8a6467033a528f68b1a97de24d9d0ce86c8e8e83683820e16e433ddbd3f712f7"

SRC_URI[GeoLiteCityv6-dat.md5sum] = "ad0cb42518af7f752499425dca0952bb"
SRC_URI[GeoLiteCityv6-dat.sha256sum] = "eda67f4204ba9fa5204a53cdb629167cca9394c712f5378bc723a8c29c0b440f"

LICENSE = "LGPL-2.1"

LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad \
                    file://LICENSE;md5=0388276749a542b0d611601fa7c1dcc8 "

S = "${WORKDIR}/git"

inherit autotools

EXTRA_OECONF = "--disable-static               \
                --disable-dependency-tracking  "

do_install() {
    make DESTDIR=${D} install
    install -d ${D}/${datadir}/GeoIP
    install ${WORKDIR}/GeoIP.dat ${D}/${datadir}/GeoIP/
    install ${WORKDIR}/GeoIPv6.dat ${D}/${datadir}/GeoIP/
    install ${WORKDIR}/GeoLiteCity.dat ${D}/${datadir}/GeoIP/
    install ${WORKDIR}/GeoLiteCityv6.dat ${D}/${datadir}/GeoIP/
    ln -s GeoLiteCity.dat ${D}${datadir}/GeoIP/GeoIPCity.dat
}

PACKAGES =+ "${PN}-database"
FILES_${PN}-database = ""
FILES_${PN}-database += "${datadir}/GeoIP/*"

# We cannot do much looking up without databases.
#
RDEPENDS_${PN} += "${PN}-database"

inherit ptest

do_configure_ptest() {
    sed -i -e "s/noinst_PROGRAMS = /test_PROGRAMS = /g" \
        -e 's:SRCDIR=\\"$(top_srcdir)\\":SRCDIR=\\"$(testdir)\\":' \
        ${S}/test/Makefile.am

    if ! grep "^testdir = " ${S}/test/Makefile.am ; then
        sed -e '/EXTRA_PROGRAMS = /itestdir = ${PTEST_PATH}/tests' \
            -i ${S}/test/Makefile.am
    fi

    sed -i -e "s:/usr/local/share:/usr/share:g" \
        ${S}/test/benchmark.c

    sed -i -e 's:"../data/:"/usr/share/GeoIP/:g' \
        ${S}/test/test-geoip-city.c \
        ${S}/test/test-geoip-isp.c \
        ${S}/test/test-geoip-asnum.c \
        ${S}/test/test-geoip-netspeed.c \
        ${S}/test/test-geoip-org.c \
        ${S}/test/test-geoip-region.c
}


do_install_ptest() {
    oe_runmake -C test DESTDIR=${D}  install-testPROGRAMS
    install ${S}/test/*.txt ${D}${PTEST_PATH}/tests
}
