SUMMARY = "RAR archive reader for Python"
HOMEPAGE = "https://github.com/markokr/rarfile"
LICENSE = "ISC"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1916695551f7eec48dfd97db9467b831"

inherit setuptools3

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "7425d0afa180f0092db903abb1526a130b36858980aad90b3694f48e41420155"

inherit pypi ptest

RDEPENDS:${PN} += "\
    7zip \
    python3-core \
    python3-datetime \
    python3-crypt \
    python3-io \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-datetime \
    python3-crypt \
    python3-compression \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
}

BBCLASSEXTEND = "native nativesdk"
