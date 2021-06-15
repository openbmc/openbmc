SUMMARY = "A MutableSet that remembers its order, so that every entry has an index."
HOMEPAGE = "http://github.com/LuminosoInsight/ordered-set"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://MIT-LICENSE;md5=2b36be0d99854aa2ae292a800a7c1d4e"

SRC_URI[md5sum] = "5d88f3870c32d4868b28c8fe833f7e74"
SRC_URI[sha256sum] = "ba93b2df055bca202116ec44b9bead3df33ea63a7d5827ff8e16738b97f33a95"

inherit pypi setuptools3 ptest

DEPENDS += "python3-pytest-runner-native"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/test.py ${D}${PTEST_PATH}/
}
