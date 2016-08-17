SUMMARY = "FoxtrotGPS is a map and GPS application using OpenStreetMap"
AUTHOR = "Joshua Judson Rosen <rozzin@geekspace.com>"
HOMEPAGE = "http://www.foxtrotgps.org/"
SECTION = "x11/applications"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
DEPENDS = "curl gtk+ libglade sqlite3 libexif gpsd bluez4 intltool-native"

PNBLACKLIST[foxtrotgps] ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5 conflicts with bluez4 and bluez5 is selected in DISTRO_FEATURES', '', d)}"

SRC_URI = "http://www.foxtrotgps.org/releases/${BP}.tar.gz"
SRC_URI[md5sum] = "6777d448ee9d3ba195f9d26ea90e3163"
SRC_URI[sha256sum] = "ae9706285510554cc0813ac92522e0d1672b0ddb065307bfacfcff3c328f6adb"

inherit autotools pkgconfig perlnative gconf

PR = "r2"

do_configure_prepend() {
    if [ -f ${S}/configure.in ] ; then
    mv ${S}/configure.in ${S}/configure.ac
    fi
}

RDEPENDS_${PN} += "python perl"
RRECOMMENDS_${PN} = "gpsd"
