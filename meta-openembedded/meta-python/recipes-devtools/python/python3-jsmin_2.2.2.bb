DESCRIPTION = "JavaScript minifier."
HOMEPAGE = "https://github.com/tikitu/jsmin/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3a3301ce2ad647e172f4a1016c67324d"

inherit setuptools3 pypi ptest
SRC_URI[md5sum] = "00e7a3179a4591aab2ee707b3214e2fd"
SRC_URI[sha256sum] = "b6df99b2cd1c75d9d342e4335b535789b8da9107ec748212706ef7bbe5c2553b"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/jsmin/test.py ${D}${PTEST_PATH}/
}
