SUMMARY = "interface to seccomp filtering mechanism"
DESCRIPTION = "The libseccomp library provides and easy to use, platform independent,interface to the Linux Kernel's syscall filtering mechanism: seccomp."
HOMEPAGE = "https://github.com/seccomp/libseccomp"
SECTION = "security"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=0;endline=1;md5=8eac08d22113880357ceb8e7c37f989f"

DEPENDS += "gperf-native"

PV .= "+git${SRCPV}"
SRCREV = "5822e50c2920ce597d038077dea4a0eedf193f86"

SRC_URI = "git://github.com/seccomp/libseccomp.git;branch=main \
           file://0001-configure.ac-Bump-version-to-2.5.99.patch \
           file://0001-arch-Add-riscv32-architecture-support.patch \
           file://0002-Regenerate-syscall-cvs-file-from-5.13-rc5-kernel.patch \
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
