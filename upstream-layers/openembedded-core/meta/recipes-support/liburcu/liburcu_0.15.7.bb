SUMMARY = "Userspace RCU (read-copy-update) library"
DESCRIPTION = "A userspace RCU (read-copy-update) library. This data \
synchronization library provides read-side access which scales linearly \
with the number of cores. "
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

LICENSE = "LGPL-2.1-or-later AND MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=c2a92498b6e88e276f986877995425b8 \
                    file://include/urcu/urcu.h;beginline=1;endline=18;md5=d35fe8cc2613ca70e0a624ed8bf6fef9 \
                    file://include/urcu/uatomic/x86.h;beginline=1;endline=6;md5=358d69272ba7b5f85e29e342430d440c \
                    "

SRC_URI = "http://lttng.org/files/urcu/userspace-rcu-${PV}.tar.bz2 \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "2556b83adc0f9b3ac8024e613e17d014d04c4c49110604ce55fcb14eae32edd3"

S = "${UNPACKDIR}/userspace-rcu-${PV}"
inherit autotools multilib_header ptest

CPPFLAGS:append:riscv64  = " -pthread -D_REENTRANT"

RDEPENDS:${PN}-ptest += "bash"

do_install:append() {
    oe_multilib_header urcu/config.h
}

do_compile_ptest() {
    oe_runmake -C ${B}/tests/unit check TESTS=
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests/unit

    # Compiled test programs (libtool resolves the real binary from .libs).
    for p in $(makefile-getvar ${B}/tests/unit/Makefile noinst_PROGRAMS); do
        ${B}/libtool --mode=install install -m 0755 ${B}/tests/unit/$p \
            ${D}${PTEST_PATH}/tests/unit/
    done

    # Shell-script test wrappers shipped in the source tree (names come from
    # the configured build Makefile; the files live in ${S}).
    for s in $(makefile-getvar ${B}/tests/unit/Makefile dist_noinst_SCRIPTS); do
        install -m 0755 ${S}/tests/unit/$s ${D}${PTEST_PATH}/tests/unit/
    done

    install -d ${D}${PTEST_PATH}/tests/utils
    install -m 0644 ${S}/tests/utils/tap.sh ${D}${PTEST_PATH}/tests/utils/
    install -m 0644 ${S}/tests/utils/utils.sh ${D}${PTEST_PATH}/tests/utils/
}
