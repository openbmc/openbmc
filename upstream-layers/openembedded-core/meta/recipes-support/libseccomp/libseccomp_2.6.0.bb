SUMMARY = "interface to seccomp filtering mechanism"
DESCRIPTION = "The libseccomp library provides an easy to use, platform independent, interface to the Linux Kernel's syscall filtering mechanism: seccomp."
HOMEPAGE = "https://github.com/seccomp/libseccomp"
SECTION = "security"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c13b3376cea0ce68d2d2da0a1b3a72c"

DEPENDS += "gperf-native"

SRCREV = "c7c0caed1d04292500ed4b9bb386566053eb9775"

SRC_URI = "git://github.com/seccomp/libseccomp.git;branch=release-2.6;protocol=https \
           file://0001-api-fix-seccomp_export_bpf_mem-out-of-bounds-read.patch \
           file://run-ptest \
           "

inherit autotools pkgconfig ptest features_check

inherit_defer ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3native', '', d)}

REQUIRED_DISTRO_FEATURES = "seccomp"

PACKAGECONFIG ??= ""
PACKAGECONFIG[python] = "--enable-python, --disable-python, python3-cython-native"

DISABLE_STATIC = ""

do_compile_ptest() {
    oe_runmake -C tests check-build
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    for file in $(find ${S}/tests/* -executable -type f); do
        install $file ${D}/${PTEST_PATH}/tests
    done
    for file in $(find ${S}/tests/*.tests -type f); do
        install $file ${D}/${PTEST_PATH}/tests
    done
    for file in $(find ${B}/tests/* -executable -type f); do
        ${B}/libtool --mode=install install $file ${D}/${PTEST_PATH}/tests
    done
        for file in $(find ${S}/tests/*.pfc -type f); do
        install -m 644 $file ${D}/${PTEST_PATH}/tests
    done
    install -m 644 ${S}/tests/valgrind_test.supp ${D}/${PTEST_PATH}/tests

    install -d ${D}${PTEST_PATH}/tools
    for file in $(find ${S}/tools/* -executable -type f); do
        install $file ${D}/${PTEST_PATH}/tools
    done
    for file in $(find ${B}/tools/* -executable -type f); do
        ${B}/libtool --mode=install install $file ${D}/${PTEST_PATH}/tools
    done

    # fix python shebang
    sed -i -e 's@cmd /usr/bin/env python @cmd /usr/bin/env python3 @' ${D}/${PTEST_PATH}/tests/regression
    sed -i -e 's@^#!/usr/bin/env python$@#!/usr/bin/env python3@' ${D}/${PTEST_PATH}/tests/*.py
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/"

RDEPENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3', '', d)}"
RDEPENDS:${PN}-ptest = "coreutils bash diffutils"
