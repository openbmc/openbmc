DESCRIPTION = "Python module for Semantic Versioning"
HOMEPAGE = "https://github.com/k-bx/python-semver"
BUGTRACKER = "https://github.com/k-bx/python-semver/issues"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

SRC_URI[md5sum] = "e98b5fb283ea84daa5195087de83ebf1"
SRC_URI[sha256sum] = "fa0fe2722ee1c3f57eac478820c3a5ae2f624af8264cbdf9000c980ff7f75e3f"

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
