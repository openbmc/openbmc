SUMMARY = "A simple, safe single expression evaluator library"
HOMEPAGE = "https://pypi.org/project/simpleeval/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=83843c8f0f3beb18af2f282faecbdebe"

SRC_URI[sha256sum] = "67bbf246040ac3b57c29cf048657b9cf31d4e7b9d6659684daa08ca8f1e45829"

inherit pypi python_setuptools_build_meta python_hatchling ptest-python-pytest

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
	python3-math \
"

do_install_ptest:append() {
	cp -f ${S}/test_simpleeval.py ${D}${PTEST_PATH}/
}
