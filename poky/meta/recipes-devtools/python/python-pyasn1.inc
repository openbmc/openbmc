SUMMARY = "Python library implementing ASN.1 types."
HOMEPAGE = "http://pyasn1.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=190f79253908c986e6cacf380c3a5f6d"

SRC_URI[sha256sum] = "6d391a96e59b23130a5cfa74d6fd7f388dbbe26cc8f1edf39fdddf08d9d6676c"

RDEPENDS:${PN}:class-target += " \
    python3-codecs \
    python3-logging \
    python3-math \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"

inherit ptest

SRC_URI += " \
       file://run-ptest \
           "

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
