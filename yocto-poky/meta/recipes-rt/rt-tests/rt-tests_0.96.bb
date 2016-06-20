SUMMARY = "Real-Time preemption testcases"
HOMEPAGE = "https://rt.wiki.kernel.org/index.php/Cyclictest"
SECTION = "tests"
DEPENDS = "linux-libc-headers virtual/libc"
LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://src/cyclictest/cyclictest.c;beginline=7;endline=9;md5=e768b8da44555fe63f65e5c497844cb5 \
                    file://src/pi_tests/pi_stress.c;beginline=6;endline=19;md5=bd426a634a43ec612e9fbf125dfcc949"

require rt-tests.inc
inherit ptest

SRC_URI += "file://0001-Makefile-Set-CC-AR-variable-only-if-it-doesn-t-have-.patch \
            file://run-ptest \
            file://rt_bmark.py \
           "
# Do not install hwlatdetect
EXTRA_OEMAKE += "PYLIB='' CROSS_COMPILE=${TARGET_PREFIX}"

CFLAGS_prepend = "${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"

do_install() {
        oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} \
                           INCLUDEDIR=${includedir}
}

do_install_ptest() {
        cp ${WORKDIR}/rt_bmark.py ${D}${PTEST_PATH}
}

RDEPENDS_${PN}-ptest += " stress python python-subprocess python-multiprocessing python-datetime python-re python-lang"

FILES_${PN} += "${prefix}/src/backfire"
