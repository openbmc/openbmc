SUMMARY = "Python library for displaying tabular data in a ASCII table format"
HOMEPAGE = "http://code.google.com/p/prettytable"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3e73500ffa52de5071cff65990055282"

SRC_URI[md5sum] = "a6b80afeef286ce66733d54a0296b13b"
SRC_URI[sha256sum] = "2d5460dc9db74a32bcc8f9f67de68b2c4f4d2f01fa3bd518764c69156d9cacd9"

do_install_append() {
    perm_files=`find "${D}${PYTHON_SITEPACKAGES_DIR}/" -name "*.txt" -o -name "PKG-INFO"`
    for f in $perm_files; do
        chmod 644 "${f}"
    done
}

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/PrettyTable/"
UPSTREAM_CHECK_REGEX = "/PrettyTable/(?P<pver>(\d+[\.\-_]*)+)"

BBCLASSEXTEND = "native nativesdk"
inherit pypi ptest setuptools3

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-math \
	${PYTHON_PN}-html \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/prettytable_test.py ${D}${PTEST_PATH}/
}
