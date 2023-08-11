DESCRIPTION = "Python module for Semantic Versioning"
HOMEPAGE = "https://github.com/python-semver/python-semver"
BUGTRACKER = "https://github.com/python-semver/python-semver"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d9da679db3bdce30a1b4328d5c474f98"

SRC_URI[md5sum] = "b7502c12ce325ffffeab694fed52f6f5"
SRC_URI[sha256sum] = "9ec78c5447883c67b97f98c3b6212796708191d22e4ad30f4570f840171cbce1"

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
