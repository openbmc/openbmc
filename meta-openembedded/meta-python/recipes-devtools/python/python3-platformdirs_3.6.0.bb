SUMMARY = "A small Python module for determining appropriate platform-specific dirs"
HOMEPAGE = "https://github.com/platformdirs/platformdirs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea4f5a41454746a9ed111e3d8723d17a"

SRC_URI = " \
    git://github.com/platformdirs/platformdirs.git;branch=main;protocol=https \
    file://run-ptest \
"

SRCREV ?= "85b0b67eb33a835b797c1d52dab4a06c5554b7ee"
SRC_URI[sha256sum] = "7954a68d0ba23558d753f73437c55f89027cf8f5108c19844d4b82e5af396335"

inherit python_setuptools_build_meta ptest

DEPENDS += " \
    python3-hatch-vcs-native \
    python3-setuptools-scm-native \
    python3-toml-native \
"

S = "${WORKDIR}/git"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-appdirs \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-pytest-mock \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/Tests
        cp -rf ${S}/tests ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
