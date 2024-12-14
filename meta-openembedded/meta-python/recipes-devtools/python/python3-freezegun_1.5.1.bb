SUMMARY = "FreezeGun is a library that allows your Python tests to travel through time by mocking the datetime module."
HOMEPAGE = "https://github.com/spulec/freezegun"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=acf1d209bb6eddae4cbe6ffd6a0144fe"

SRC_URI[sha256sum] = "b29dedfcda6d5e8e083ce71b2b542753ad48cfec44037b3fc79702e2980a89e9"

inherit pypi python_setuptools_build_meta ptest

SRC_URI += " \
        file://run-ptest \
        file://1777174bb97c0b514033a09b820078b0d117f4a8.patch \
"

RDEPENDS:${PN}-ptest += " \
        python3-pytest \
        python3-sqlite3 \
        python3-unittest-automake-output \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} = "\
        python3-asyncio \
        python3-dateutil \
        python3-unittest \
"
