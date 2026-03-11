SUMMARY = "Python expectaton library"
DESCRIPTION = "Minimal but very flexible implementation of the expect pattern"
SECTION = "devel/python"
HOMEPAGE = " https://bitbucket.org/dwt/pyexpect"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://README.md;md5=a6aa1e7097aca5955f9a2e1c8b0ce158"

inherit pypi setuptools3

SRC_URI[sha256sum] = "659351e7ee8923b42de8a774fabfc806acf07377d7fd19f2ea4412ef8f619c6a"

RDEPENDS:${PN} += "python3-numbers"
