SUMMARY = "A simple, safe single expression evaluator library"
HOMEPAGE = "https://pypi.org/project/simpleeval/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=dc9277482effe59b734b004cbcc1fee7"

SRC_URI[sha256sum] = "3e0be507486d4e21cf9d08847c7e57dd61a1603950399985f7c5a0be7fd33e36"

inherit pypi setuptools3 ptest

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-math \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_configure:prepend() {
	sed -i -e "/use_2to3=True,/d" ${S}/setup.py
}

do_install_ptest() {
	cp -f ${S}/test_simpleeval.py ${D}${PTEST_PATH}/
}
