DESCRIPTION = "Add ANSI colors and decorations to your strings"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aef5566ac4fede9815eccf124c281317"

SRC_URI[sha256sum] = "99f94f5e3348a0bcd43c82e5fc4414013ccc19d70bd939ad71e0133ce9c372e0"

PYPI_PACKAGE_EXT = "zip"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
	cp -f ${S}/test/test.py ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
