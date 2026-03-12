DESCRIPTION = "Netsniff-ng is a fast zero-copy analyzer, pcap capturing and replaying tool"
HOMEPAGE = "http://netsniff-ng.org"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=9dd40dfb621eed702c0775577fbb7011"
DEPENDS = "libpcap"

SRCREV = "fb6183fe03b6eaa6c47b18de03b1745fa78633e4"
SRC_URI = "git://github.com/netsniff-ng/netsniff-ng.git;protocol=https;branch=main;tag=v${PV}"


inherit pkgconfig

EXTRA_OEMAKE += " TERM='' "

PACKAGECONFIG ??= ""
PACKAGECONFIG[zlib] = ",--disable-zlib,zlib,"
PACKAGECONFIG[libnl] = ",--disable-libnl,libnl,"
PACKAGECONFIG[geoip] = ",--disable-geoip,geoip,"

do_configure() {
    ./configure --prefix=${prefix}
}

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} netsniff-ng_install
}

BBCLASSEXTEND = "native"
