SUMMARY = "A simple, safe single expression evaluator library"
HOMEPAGE = "https://pypi.org/project/simpleeval/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=dc9277482effe59b734b004cbcc1fee7"

SRC_URI[md5sum] = "f175fc12d408487ca26fa3905e0a6691"
SRC_URI[sha256sum] = "692055488c2864637f6c2edb5fa48175978a2a07318009e7cf03c9790ca17bea"

inherit pypi setuptools3 ptest

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/test_simpleeval.py ${D}${PTEST_PATH}/
}
