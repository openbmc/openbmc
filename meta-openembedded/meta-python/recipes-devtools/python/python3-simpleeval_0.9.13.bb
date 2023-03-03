SUMMARY = "A simple, safe single expression evaluator library"
HOMEPAGE = "https://pypi.org/project/simpleeval/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=dc9277482effe59b734b004cbcc1fee7"

SRC_URI[sha256sum] = "4a30f9cc01825fe4c719c785e3762623e350c4840d5e6855c2a8496baaa65fac"

inherit pypi python_setuptools_build_meta ptest

BBCLASSEXTEND = "native nativesdk"

SRC_URI += "file://run-ptest"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-math \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/test_simpleeval.py ${D}${PTEST_PATH}/
}
