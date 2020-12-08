SUMMARY = "A handy serial sniffer"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

PV = "2.3.5+git${SRCPV}"

SRC_URI = "git://github.com/snarlistic/jpnevulator.git;protocol=http"
SRCREV = "c2d857091c0dfed05139ac07ea9b0f36ad259638"

S = "${WORKDIR}/git"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install 'DESTDIR=${D}'
}

