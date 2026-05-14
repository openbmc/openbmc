SUMMARY = "Resolve JSON Pointers in Python"
HOMEPAGE = "https://github.com/stefankoegl/python-json-pointer"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi ptest setuptools3

SRC_URI[sha256sum] = "0b801c7db33a904024f6004d526dcc53bbb8a4a0f4e32bfd10beadf60adf1900"

SRC_URI += " \
	file://run-ptest \
"

do_install_ptest() {
	cp -f ${S}/tests.py ${D}${PTEST_PATH}/
}

RDEPENDS:${PN} += " \
    python3-json \
"

RDEPENDS:${PN}-ptest += " \
	python3-doctest \
	python3-unittest \
	python3-unittest-automake-output \
"

BBCLASSEXTEND = "native nativesdk"
