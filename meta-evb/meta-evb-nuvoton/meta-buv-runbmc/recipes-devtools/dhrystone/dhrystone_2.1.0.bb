SUMMARY = "Dhrystone benchmark"
DESCRIPTION = "Dhrystone benchmark" 
HOMEPAGE = "http://www.netlib.org/benchmark/dhry-c"

PR = "r1"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/README;md5=2178d6495724d4d5a956aef832e06a7c"

SRC_URI = "file://dhrystone.tar.bz2"

S = "${WORKDIR}/${PN}"

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX} CC="${CC}""
INSANE_SKIP:${PN} += "ldflags"

do_install () {
    install -d ${D}${bindir}
    install cc_dry2 ${D}${bindir}
}
