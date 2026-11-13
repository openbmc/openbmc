SUMMARY = "A simple, safe single expression evaluator library"
HOMEPAGE = "https://pypi.org/project/simpleeval/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=aed013161b2a0bfa75e69eeb4e8a89e7"

SRC_URI[sha256sum] = "b417f869c11f2de034eb919820a825f35d12ce99f63a43971947ce73679eb9ba"

CVE_PRODUCT = "danthedeckie:simpleeval"

inherit pypi python_hatchling ptest-python-pytest

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
	python3-math \
"

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
