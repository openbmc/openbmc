SUMMARY = "Python wrapper around rapidjson"
HOMEPAGE = "https://github.com/python-rapidjson/python-rapidjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4daf3929156304df67003c33274a98bd"

SRC_URI[sha256sum] = "26806f0a658c34b48d2951d8d3f846ca9deb93a34e664ef436db632a188b6779"

# Inheriting ptest provides functionality for packaging and installing runtime tests for this recipe
inherit setuptools3 pypi ptest

PYPI_PACKAGE = "python-rapidjson"

SETUPTOOLS_BUILD_ARGS += " --rj-include-dir=${RECIPE_SYSROOT}${includedir}"

# run-ptest is a shell script that starts the test suite
SRC_URI += " \
    file://run-ptest \
"

DEPENDS += " \
    rapidjson \
"

# Adding required python package for the ptest (pytest and pytest->automake report translation)
RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
    python3-pytz \
"

RDEPENDS:${PN} += " \
    python3-core \
"

# Installing the test suite on the target
do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
