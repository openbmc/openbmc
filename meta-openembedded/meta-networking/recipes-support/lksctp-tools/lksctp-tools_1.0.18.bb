SUMMARY = "The Linux Kernel Stream Control Transmission Protocol (lksctp) project"
HOMEPAGE = "http://lksctp.org"
SECTION = "net"
LICENSE = "LGPLv2.1 & GPLv2"

LIC_FILES_CHKSUM = " \
    file://COPYING.lib;md5=0a1b79af951c42a9c8573533fbba9a92 \
    file://COPYING;md5=0c56db0143f4f80c369ee3af7425af6e \
"

SRCREV = "12c74404e09755561dee40cf194954f7ff5afd60"

PV .= "+git${SRCPV}"
LK_REL = "1.0.18"

SRC_URI = " \
    git://github.com/sctp/lksctp-tools.git \
    file://0001-m4-sctp.m4-make-conpatible-to-autoconf-2.70.patch \
    file://run-ptest \
    file://v4test.sh \
    file://v6test.sh \
"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

inherit autotools-brokensep pkgconfig binconfig ptest

do_install_ptest () {
    install -m 0755 ${WORKDIR}/v4test.sh ${D}${PTEST_PATH}
    install -m 0755 ${WORKDIR}/v6test.sh ${D}${PTEST_PATH}
    for testcase in `find ${B}/src/apps/.libs ${B}/src/func_tests/.libs -maxdepth 1 -type f -executable`; do
        install $testcase ${D}${PTEST_PATH}
    done
}

SOLIBVERSION="${LK_REL}"
SOLIBMAJORVERSION="1"

PACKAGES =+ "${PN}-withsctp ${PN}-utils"

FILES_${PN} = " \
    ${libdir}/libsctp.so.${SOLIBVERSION} \
    ${libdir}/libsctp.so.${SOLIBMAJORVERSION} \
"

FILES_${PN}-withsctp = " \
    ${libdir}/lksctp-tools/libwithsctp.so.${SOLIBVERSION} \
    ${libdir}/lksctp-tools/libwithsctp.so.${SOLIBMAJORVERSION} \
"

FILES_${PN}-dev += " \
    ${libdir}/libsctp.so \
    ${libdir}/lksctp-tools/libwithsctp.so \
    ${datadir}/lksctp-tools/*.c \
    ${datadir}/lksctp-tools/*.h \
"

FILES_${PN}-utils = "${bindir}/*"

RRECOMMENDS_${PN} += "kernel-module-sctp"
RRECOMMENDS_${PN}-utils += "kernel-module-sctp"
RRECOMMENDS_${PN}-ptest += "kernel-module-sctp"
