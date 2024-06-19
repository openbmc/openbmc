SUMMARY = "A small Python module for determining appropriate platform-specific dirs"
HOMEPAGE = "https://github.com/platformdirs/platformdirs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea4f5a41454746a9ed111e3d8723d17a"

SRC_URI += " \
    file://run-ptest \
"

SRC_URI[sha256sum] = "38b7b51f512eed9e84a22788b4bce1de17c0adb134d6becb09836e37d8654cd3"

inherit pypi python_hatchling ptest

DEPENDS += " \
    python3-hatch-vcs-native \
"

RDEPENDS:${PN}-ptest += " \
    python3-appdirs \
    python3-covdefaults \
    python3-pytest \
    python3-pytest-cov \
    python3-pytest-mock \
    python3-unittest-automake-output \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}
        cp -rf ${S}/tests ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
