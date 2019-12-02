SUMMARY = "A handy serial sniffer"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

PV = "2.3.4+git${SRCPV}"

SRC_URI = "git://github.com/snarlistic/jpnevulator.git;protocol=http"
SRCREV = "97438ecbd52fbc01540221bc590f8388a43c74e0"

S = "${WORKDIR}/git"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install 'DESTDIR=${D}'
}

