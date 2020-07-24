DESCRIPTION = "Python module for Semantic Versioning"
HOMEPAGE = "https://github.com/k-bx/python-semver"
BUGTRACKER = "https://github.com/k-bx/python-semver/issues"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

SRC_URI[md5sum] = "3e11ae9782121e8ffe9f8a8b763a8cb5"
SRC_URI[sha256sum] = "c0a4a9d1e45557297a722ee9bac3de2ec2ea79016b6ffcaca609b0bc62cf4276"

inherit pypi setuptools3 ptest

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
"

do_install_ptest() {
        cp -f ${S}/test_semver.py ${D}${PTEST_PATH}/
}
