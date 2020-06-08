SUMMARY = "A MutableSet that remembers its order, so that every entry has an index."
HOMEPAGE = "http://github.com/LuminosoInsight/ordered-set"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://MIT-LICENSE;md5=2b36be0d99854aa2ae292a800a7c1d4e"

SRC_URI[md5sum] = "6e12312c8dc4c90fe840e86e8a352644"
SRC_URI[sha256sum] = "a7bfa858748c73b096e43db14eb23e2bc714a503f990c89fac8fab9b0ee79724"

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
