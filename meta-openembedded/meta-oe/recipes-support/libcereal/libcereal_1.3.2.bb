SUMMARY = "A C++11 library for serialization"
HOMEPAGE = "https://uscilab.github.io/cereal/"

SECTION = "libs"

LICENSE = "BSD-3-Clause & MIT & BSL-1.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=4921372a1fb38469e667c38b17a1c4b3 \
    file://include/cereal/external/rapidxml/license.txt;md5=d63ab70ba21ca0544b03284958324301 \
    file://include/cereal/external/LICENSE;md5=b07578c9df99c0b8b45eb041efd4a645 \
    file://include/cereal/external/rapidjson/LICENSE;md5=e7abb663111d4ac17cf00323698aff08 \
    file://include/cereal/external/rapidjson/msinttypes/LICENSE;md5=dffce65b98c773976de2e338bd130f46 \
"

DEPENDS = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'boost', '', d)} "

PROVIDES += "${PN}-dev"

PV .= "+git"
SRCREV = "ebef1e929807629befafbb2918ea1a08c7194554"
SRC_URI = "git://github.com/USCiLab/cereal.git;branch=master;protocol=https \
           file://0001-sandbox-Do-not-use-int8_t-in-std-uniform_int_distrib.patch \
           file://0001-cmake-Use-idirafter-instead-of-isystem.patch \
           file://0001-doctest-Do-not-use-unnamed-class.patch \
           file://0001-Fix-instances-of-Wmissing-template-arg-list-after-te.patch \
           file://run-ptest \
"

S = "${WORKDIR}/git"

inherit cmake pkgconfig ptest

CXXFLAGS:append:toolchain-clang = " -Wno-error=c++11-narrowing-const-reference"

LIBATOMIC:mips = "${@bb.utils.contains('PTEST_ENABLED', '1', '-DCEREAL_THREAD_LIBS="-latomic"', '', d)}"
LIBATOMIC:riscv32 = "${@bb.utils.contains('PTEST_ENABLED', '1', '-DCEREAL_THREAD_LIBS="-latomic"', '', d)}"
LIBATOMIC:powerpc = "${@bb.utils.contains('PTEST_ENABLED', '1', '-DCEREAL_THREAD_LIBS="-latomic"', '', d)}"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)}"
PACKAGECONFIG[with-tests] = "-DWITH_WERROR=OFF -DBUILD_TESTS=ON ${LIBATOMIC},,"
EXTRA_OECMAKE = "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', '', '-DJUST_INSTALL_CEREAL=ON', d)} \
                 ${@['','-DSKIP_PORTABILITY_TEST=ON'][d.getVar('SITEINFO_BITS') != '32']}"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp ${B}/unittests/test_*  ${D}${PTEST_PATH}/tests
}

ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"

#it needs to work with CXXFLAGS += " -mlong-double-64" but ppc only supports 128bit long double
COMPATIBLE_HOST:powerpc:libc-musl = "null"
COMPATIBLE_HOST:powerpc64le:libc-musl = "null"
