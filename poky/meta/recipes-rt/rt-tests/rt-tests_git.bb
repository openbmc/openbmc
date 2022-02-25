SUMMARY = "Real-Time preemption testcases"
HOMEPAGE = "https://wiki.linuxfoundation.org/realtime/documentation/start"
DESCRIPTION = "The main aim of the PREEMPT_RT patch is to minimize the amount of kernel code that is non-preemptible Therefore several substitution mechanisms and new mechanisms are implemented."
SECTION = "tests"
DEPENDS = "linux-libc-headers virtual/libc numactl"
LICENSE = "GPL-2.0-only & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

require rt-tests.inc
inherit ptest

SRC_URI += " \
            file://run-ptest \
            file://rt_bmark.py \
            file://0001-Makefile-Allow-for-CC-and-AR-to-be-overridden.patch \
           "

# rt-tests needs PI mutex support in libc
COMPATIBLE_HOST:libc-musl = 'null'

# Do not install hwlatdetect
EXTRA_OEMAKE += "PYLIB=''"

do_install() {
        oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} \
                           INCLUDEDIR=${includedir}
}

do_install_ptest() {
        cp ${WORKDIR}/rt_bmark.py ${D}${PTEST_PATH}
}

RDEPENDS:${PN}-ptest += " stress-ng python3 python3-multiprocessing python3-datetime python3-misc"

FILES:${PN} += "${prefix}/src/backfire"
RDEPENDS:${PN} += "bash"
