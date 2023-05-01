SUMMARY = "A handy serial sniffer"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

PV = "2.3.6+git${SRCPV}"

SRC_URI = "git://github.com/snarlistic/jpnevulator.git;protocol=https;branch=master"
SRCREV = "bc1d4f6587a4a4829b5d55e3ca7ad584da6de545"

S = "${WORKDIR}/git"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install 'DESTDIR=${D}'
}

