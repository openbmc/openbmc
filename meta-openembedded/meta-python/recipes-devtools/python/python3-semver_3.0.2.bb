DESCRIPTION = "Python module for Semantic Versioning"
HOMEPAGE = "https://github.com/python-semver/python-semver"
BUGTRACKER = "https://github.com/python-semver/python-semver"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d9da679db3bdce30a1b4328d5c474f98"

SRC_URI[sha256sum] = "6253adb39c70f6e51afed2fa7152bcd414c411286088fb4b9effb133885ab4cc"

inherit pypi python_setuptools_build_meta ptest

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
        file://run-ptest \
"

DEPENDS += " python3-setuptools-scm-native"

RDEPENDS:${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
"

do_install_ptest() {
        cp -rf ${S}/tests ${D}${PTEST_PATH}/
}
