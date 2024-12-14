SUMMARY = "toml config parser and serializer for c++."
HOMEPAGE = "https://github.com/marzer/tomlplusplus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90960f22c10049c117d56ed2ee5ee167"

SRC_URI = "git://github.com/marzer/tomlplusplus.git;protocol=https;branch=master \
           file://run-ptest \
"

PV = "3.4.0"
SRCREV = "30172438cee64926dc41fdd9c11fb3ba5b2ba9de"
S = "${WORKDIR}/git"

DEPENDS = "cmake-native"

inherit meson ptest pkgconfig

EXTRA_OEMESON += "-Dbuild_tests=${@bb.utils.contains("DISTRO_FEATURES", "ptest", "true", "false", d)} \
"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    cp ${B}/tests/tomlplusplus_odr_test ${D}${PTEST_PATH}/tests
    cp ${B}/tests/tomlplusplus_tests ${D}${PTEST_PATH}/tests
}
