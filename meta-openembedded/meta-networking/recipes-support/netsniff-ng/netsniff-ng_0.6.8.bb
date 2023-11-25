DESCRIPTION = "Netsniff-ng is a fast zero-copy analyzer, pcap capturing and replaying tool"
HOMEPAGE = "http://netsniff-ng.org"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=9dd40dfb621eed702c0775577fbb7011"
DEPENDS = "libpcap"

SRCREV = "be3e706f00295024ebc199e70177343fdaebbc9e"
SRC_URI = " \
	git://github.com/netsniff-ng/netsniff-ng.git;protocol=https;branch=master \
	file://0001-Cmds-automatically-create-folder.patch \
	"

S = "${WORKDIR}/git"

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
