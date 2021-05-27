SUMMARY = "Real-Time preemption testcases"
HOMEPAGE = "https://wiki.linuxfoundation.org/realtime/documentation/start"
DESCRIPTION = "The main aim of the PREEMPT_RT patch is to minimize the amount of kernel code that is non-preemptible Therefore several substitution mechanisms and new mechanisms are implemented."
SECTION = "tests"
DEPENDS = "linux-libc-headers virtual/libc"
LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://src/cyclictest/cyclictest.c;beginline=7;endline=9;md5=e768b8da44555fe63f65e5c497844cb5 \
                    file://src/pi_tests/pi_stress.c;beginline=6;endline=19;md5=bd426a634a43ec612e9fbf125dfcc949"

require rt-tests.inc
inherit ptest

SRC_URI += " \
            file://run-ptest \
            file://rt_bmark.py \
            file://0001-gzip-with-n-for-build-reproducibilty.patch \
           "

# rt-tests needs PI mutex support in libc
COMPATIBLE_HOST_libc-musl = 'null'

# Do not install hwlatdetect
EXTRA_OEMAKE += "PYLIB=''"

do_install() {
        oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} \
                           INCLUDEDIR=${includedir}
}

do_install_ptest() {
        cp ${WORKDIR}/rt_bmark.py ${D}${PTEST_PATH}
}

RDEPENDS_${PN}-ptest += " stress-ng python3 python3-multiprocessing python3-datetime python3-misc"

FILES_${PN} += "${prefix}/src/backfire"
