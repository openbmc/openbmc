SUMMARY = "interface to seccomp filtering mechanism"
DESCRIPTION = "The libseccomp library provides an easy to use, platform independent, interface to the Linux Kernel's syscall filtering mechanism: seccomp."
HOMEPAGE = "https://github.com/seccomp/libseccomp"
SECTION = "security"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c13b3376cea0ce68d2d2da0a1b3a72c"

DEPENDS += "gperf-native"

SRCREV = "57357d2741a3b3d3e8425889a6b79a130e0fa2f3"

SRC_URI = "git://github.com/seccomp/libseccomp.git;branch=release-2.5;protocol=https \
           file://run-ptest \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig ptest features_check

REQUIRED_DISTRO_FEATURES = "seccomp"

PACKAGECONFIG ??= ""
PACKAGECONFIG[python] = "--enable-python, --disable-python, python3"

DISABLE_STATIC = ""

do_compile_ptest() {
    oe_runmake -C tests check-build
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -d ${D}${PTEST_PATH}/tools
    for file in $(find tests/* -executable -type f); do
        install -m 744 ${S}/${file} ${D}/${PTEST_PATH}/tests
    done
    for file in $(find tests/*.tests -type f); do
        install -m 744 ${S}/${file} ${D}/${PTEST_PATH}/tests
    done
        for file in $(find tests/*.pfc -type f); do
        install -m 644 ${S}/${file} ${D}/${PTEST_PATH}/tests
    done
    install -m 644 ${S}/tests/valgrind_test.supp ${D}/${PTEST_PATH}/tests
    for file in $(find tools/* -executable -type f); do
        install -m 744 ${S}/${file} ${D}/${PTEST_PATH}/tools
    done
    # Overwrite libtool wrappers with real executables
    for file in $(find tools/.libs/* -executable -type f); do
        install -m 744 ${S}/${file} ${D}/${PTEST_PATH}/tools
    done
}

FILES:${PN} = "${bindir} ${libdir}/${BPN}.so*"
FILES:${PN}-dbg += "${libdir}/${PN}/tests/.debug/* ${libdir}/${PN}/tools/.debug"

RDEPENDS:${PN}-ptest = "coreutils bash"

PV = "2.5.3"
