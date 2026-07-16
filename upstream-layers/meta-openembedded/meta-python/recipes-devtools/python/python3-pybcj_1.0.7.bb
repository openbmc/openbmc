SUMMARY = "bcj filter library"
HOMEPAGE = "https://codeberg.org/miurahr/pybcj"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

# The PyPI package omits files for testing
# so use the GitHub source instead.
SRCREV = "0735ad15fa001748dc3a13f36be2fe7a4971cf79"
SRC_URI = " \
    git://github.com/miurahr/pybcj;branch=main;protocol=https \
    file://run-ptest \
"

inherit python_setuptools_build_meta ptest

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "\
    python3-email \
    python3-importlib-metadata \
    python3-core \
    python3-compression \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-datetime \
    python3-crypt \
    python3-compression \
    python3-hypothesis \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
