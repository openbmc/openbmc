SUMMARY = "Resolve JSON Pointers in Python"
HOMEPAGE = "https://github.com/stefankoegl/python-json-pointer"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi ptest setuptools3

SRC_URI[sha256sum] = "f09f8deecaaa5aea65b5eb4f67ca4e54e1a61f7a11c75085e360fe6feb6a48bf"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-json \
"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/tests.py ${D}${PTEST_PATH}/
}
