SUMMARY = "Python library for displaying tabular data in a ASCII table format"
HOMEPAGE = "http://code.google.com/p/prettytable"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c9a6829fcd174d9535b46211917c7671"

SRC_URI[sha256sum] = "f4ed94803c23073a90620b201965e5dc0bccf1760b7a7eaf3158cab8aaffdf34"

do_install:append() {
    perm_files=`find "${D}${PYTHON_SITEPACKAGES_DIR}/" -name "*.txt" -o -name "PKG-INFO"`
    for f in $perm_files; do
        chmod 644 "${f}"
    done
}

BBCLASSEXTEND = "native nativesdk"
inherit pypi ptest python_hatchling

SRC_URI += " \
	file://run-ptest \
"

DEPENDS += "\
    ${PYTHON_PN}-hatch-vcs-native \
"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-math \
	${PYTHON_PN}-html \
	${PYTHON_PN}-wcwidth \
	${PYTHON_PN}-json \
	${PYTHON_PN}-compression \
	${PYTHON_PN}-importlib-metadata \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-pytest-lazy-fixture \
    ${PYTHON_PN}-sqlite3 \
"

do_install_ptest() {
	cp -f ${S}/tests/test_prettytable.py ${D}${PTEST_PATH}/
}
