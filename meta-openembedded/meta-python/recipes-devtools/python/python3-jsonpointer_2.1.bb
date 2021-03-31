SUMMARY = "Resolve JSON Pointers in Python"
HOMEPAGE = "https://github.com/stefankoegl/python-json-pointer"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi ptest setuptools3

SRC_URI[sha256sum] = "5a34b698db1eb79ceac454159d3f7c12a451a91f6334a4f638454327b7a89962"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-json \
"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/tests.py ${D}${PTEST_PATH}/
}
