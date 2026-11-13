SUMMARY = "Thin-wrapper around the mock package for easier use with pytest"
HOMEPAGE = "https://github.com/pytest-dev/pytest-mock/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=b2ddb1e69238461b7e4ef2a84d874109 \
"

SRC_URI += " \
    file://run-ptest \
"
SRC_URI[sha256sum] = "e36576e0985e01027e999891e7a966c67822d40c89ef1b4c8b6e22737e3590b4"

inherit pypi python_setuptools_build_meta ptest

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN}-ptest += " \
    python3-asyncio \
    python3-misc \
    python3-mock \
    python3-pytest \
    python3-pytest-asyncio \
    python3-threading \
    python3-tox \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests ${D}${PTEST_PATH}/
}
