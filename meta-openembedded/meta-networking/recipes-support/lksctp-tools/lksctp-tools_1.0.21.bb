SUMMARY = "The Linux Kernel Stream Control Transmission Protocol (lksctp) project"
HOMEPAGE = "http://lksctp.org"
SECTION = "net"
LICENSE = "LGPL-2.1-only & GPL-2.0-only"

LIC_FILES_CHKSUM = " \
    file://COPYING.lib;md5=0a1b79af951c42a9c8573533fbba9a92 \
    file://COPYING;md5=0c56db0143f4f80c369ee3af7425af6e \
"

SRCREV = "37d5f1225573b91d706a5e547d081f79963a9deb"

SRC_URI = " \
    git://github.com/sctp/lksctp-tools.git;branch=master;protocol=https \
    file://run-ptest \
    file://v4test.sh \
    file://v6test.sh \
"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

inherit autotools-brokensep pkgconfig binconfig ptest

do_install_ptest () {
    install -m 0755 ${UNPACKDIR}/v4test.sh ${D}${PTEST_PATH}
    install -m 0755 ${UNPACKDIR}/v6test.sh ${D}${PTEST_PATH}
    for testcase in `find ${B}/src/apps/.libs ${B}/src/func_tests/.libs -maxdepth 1 -type f -executable`; do
        install $testcase ${D}${PTEST_PATH}
    done
}

PACKAGES =+ "${PN}-withsctp ${PN}-utils"

FILES:${PN} = " \
    ${libdir}/libsctp.so.* \
"

FILES:${PN}-withsctp = " \
    ${libdir}/lksctp-tools/libwithsctp.so.* \
"

FILES:${PN}-dev += " \
    ${libdir}/libsctp.so \
    ${libdir}/lksctp-tools/libwithsctp.so \
    ${datadir}/lksctp-tools/*.c \
    ${datadir}/lksctp-tools/*.h \
"

FILES:${PN}-utils = "${bindir}/*"

RRECOMMENDS:${PN} += "kernel-module-sctp"
RRECOMMENDS:${PN}-utils += "kernel-module-sctp"
RRECOMMENDS:${PN}-ptest += "kernel-module-sctp"
