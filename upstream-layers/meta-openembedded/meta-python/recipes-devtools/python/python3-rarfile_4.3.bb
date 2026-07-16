SUMMARY = "RAR archive reader for Python"
HOMEPAGE = "https://github.com/markokr/rarfile"
LICENSE = "ISC"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1916695551f7eec48dfd97db9467b831"

inherit setuptools3

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "0d8b5a7ffb9f8e9a7b002f2398905e4420c0cb373e799b48e48f418db9c8816a"

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
