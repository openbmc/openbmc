SUMMARY = "GSL: Guidelines Support Library"
DESCRIPTION = "The Guidelines Support Library (GSL) contains functions \
    and types that are suggested for use by the C++ Core Guidelines \
    maintained by the Standard C++ Foundation. \
    This repo contains Microsoft's implementation of GSL."
HOMEPAGE = "https://github.com/microsoft/GSL"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=363055e71e77071107ba2bb9a54bd9a7"

SRC_URI = "git://github.com/microsoft/GSL.git;protocol=https;branch=main \
    file://run-ptest \
    file://0001-Fix-initialization-in-test-1140.patch \
    file://0002-Fix-gcc-build-problem.patch \
    file://0003-Adapt-check_cxx_compiler_flag-to-cmake-3.0.2.patch \
"
SRCREV = "a3534567187d2edc428efd3f13466ff75fe5805c"

S = "${WORKDIR}/git"

inherit cmake pkgconfig ptest

# this is header-only library
ALLOW_EMPTY:${PN} = "1"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'ptest','', d)}"
PACKAGECONFIG[ptest] = "-DGSL_TEST=ON,-DGSL_TEST=OFF,googletest"

# clang disagrees with https://github.com/google/googletest/pull/3457
CXXFLAGS:append:toolchain-clang = " -Wno-error=switch-default"
# Disable disabled-macro-expansion warning as error as its seen on musl
CXXFLAGS:append:toolchain-clang:libc-musl = " -Wno-error=disabled-macro-expansion"

do_install_ptest() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/tests/gsl_tests ${D}${bindir}
    install -m 0755 ${B}/tests/gsl_noexcept_tests ${D}${bindir}
}

FILES:${PN}-ptest = "${bindir}/gsl*_tests"

# there is already other gsl recipe, so recipe name does not match the real component name
CVE_PRODUCT = "microsoft:gsl"

BBCLASSEXTEND = "native nativesdk"

# This one is reproducible only on 32bit arm MACHINEs (didn't see it with qemux86 or qemux86-64 builds)
# http://errors.yoctoproject.org/Errors/Details/766976/
# lib32-microsoft-gsl/4.0.0/git/tests/span_tests.cpp:1275:34: error: value computed is not used [-Werror=unused-value]
CXXFLAGS += "-Wno-error=unused-value"
