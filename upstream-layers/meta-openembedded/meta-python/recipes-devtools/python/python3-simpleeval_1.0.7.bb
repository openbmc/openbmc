SUMMARY = "A simple, safe single expression evaluator library"
HOMEPAGE = "https://pypi.org/project/simpleeval/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=aed013161b2a0bfa75e69eeb4e8a89e7"

SRC_URI[sha256sum] = "1e10e5f9fec597814444e20c0892ed15162fa214c8a88f434b5b077cf2fef85b"

inherit pypi python_hatchling ptest-python-pytest

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
	python3-math \
"

do_install_ptest:append() {
	cp -f ${S}/test_simpleeval.py ${D}${PTEST_PATH}/
}
