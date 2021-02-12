SUMMARY = "Real-Time preemption testcases"
HOMEPAGE = "https://rt.wiki.kernel.org/index.php/Cyclictest"
SECTION = "tests"
DEPENDS = "linux-libc-headers virtual/libc numactl"
LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

require rt-tests.inc
inherit ptest

SRC_URI += " \
            file://run-ptest \
            file://rt_bmark.py \
            file://0001-Makefile-Allow-for-CC-and-AR-to-be-overridden.patch \
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
RDEPENDS_${PN} += "bash"
