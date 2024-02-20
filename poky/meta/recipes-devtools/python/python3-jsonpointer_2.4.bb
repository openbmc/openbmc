SUMMARY = "Resolve JSON Pointers in Python"
HOMEPAGE = "https://github.com/stefankoegl/python-json-pointer"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi ptest setuptools3

SRC_URI[sha256sum] = "585cee82b70211fa9e6043b7bb89db6e1aa49524340dde8ad6b63206ea689d88"

RDEPENDS:${PN} += " \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-doctest \
	python3-unittest \
	python3-unittest-automake-output \
"

do_install_ptest() {
	cp -f ${S}/tests.py ${D}${PTEST_PATH}/
}
