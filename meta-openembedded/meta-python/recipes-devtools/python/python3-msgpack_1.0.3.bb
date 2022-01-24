SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

PYPI_PACKAGE = "msgpack"
inherit pypi setuptools3 ptest

SRC_URI[sha256sum] = "51fdc7fb93615286428ee7758cecc2f374d5ff363bdd884c7ea622a7a327a81e"

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-io \
"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/test
	cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
}
