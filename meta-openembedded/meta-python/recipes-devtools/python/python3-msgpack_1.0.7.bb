SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

inherit pypi setuptools3 ptest

SRC_URI[sha256sum] = "572efc93db7a4d27e404501975ca6d2d9775705c2d922390d878fcf768d92c87"

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
