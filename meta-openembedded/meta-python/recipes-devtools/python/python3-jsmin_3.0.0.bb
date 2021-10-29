DESCRIPTION = "JavaScript minifier."
HOMEPAGE = "https://github.com/tikitu/jsmin/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3a3301ce2ad647e172f4a1016c67324d"

inherit setuptools3 pypi ptest
SRC_URI[sha256sum] = "88fc1bd6033a47c5911dbcada7d279c7a8b7ad0841909590f6a742c20c4d2e08"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/jsmin/test.py ${D}${PTEST_PATH}/
}
