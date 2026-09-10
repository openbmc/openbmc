SUMMARY = "GSL: Guidelines Support Library"
DESCRIPTION = "The Guidelines Support Library (GSL) contains functions \
    and types that are suggested for use by the C++ Core Guidelines \
    maintained by the Standard C++ Foundation. \
    This repo contains Microsoft's implementation of GSL."
HOMEPAGE = "https://github.com/microsoft/GSL"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=363055e71e77071107ba2bb9a54bd9a7"

SRC_URI = "git://github.com/microsoft/GSL.git;protocol=https;branch=rel/5.0;tag=v${PV} \
           file://run-ptest \
           "
SRCREV = "417ef685eafd626db765f0027b7fc5a0057e0770"

inherit cmake pkgconfig ptest

EXTRA_OECMAKE += "-DGSL_CXX_STANDARD=17"

# this is header-only library
ALLOW_EMPTY:${PN} = "1"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'ptest','', d)}"
PACKAGECONFIG[ptest] = "-DGSL_TEST=ON,-DGSL_TEST=OFF,googletest"

# clang disagrees with https://github.com/google/googletest/pull/3457
# The tests are built with -Weverything -Werror, so each clang release turns
# its new diagnostics into build failures.
CXXFLAGS:append:toolchain-clang = "\
    -Wno-error=switch-default \
    -Wno-unknown-warning-option \
    -Wno-error=lifetime-safety-intra-tu-suggestions \
    -Wno-error=lifetime-safety-intra-tu-constructor-suggestions \
"
# Disable disabled-macro-expansion warning as error as its seen on musl
CXXFLAGS:append:toolchain-clang:libc-musl = " -Wno-error=disabled-macro-expansion"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${B}/tests/*_tests ${D}${PTEST_PATH}
}

# there is already other gsl recipe, so recipe name does not match the real component name
CVE_PRODUCT = "microsoft:gsl"

BBCLASSEXTEND = "native nativesdk"

# This one is reproducible only on 32bit arm MACHINEs (didn't see it with qemux86 or qemux86-64 builds)
# http://errors.yoctoproject.org/Errors/Details/766976/
# lib32-microsoft-gsl/4.0.0/git/tests/span_tests.cpp:1275:34: error: value computed is not used [-Werror=unused-value]
CXXFLAGS += "-Wno-error=unused-value"
