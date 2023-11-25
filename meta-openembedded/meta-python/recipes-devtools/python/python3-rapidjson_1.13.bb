SUMMARY = "Python wrapper around rapidjson"
HOMEPAGE = "https://github.com/python-rapidjson/python-rapidjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4daf3929156304df67003c33274a98bd"

SRC_URI = "git://github.com/python-rapidjson/python-rapidjson.git;protocol=https;branch=master"
SRCREV = "a87053d9b97750afddb504da05bd1cd9f4b94654"

S = "${WORKDIR}/git"

# Inheriting ptest provides functionality for packaging and installing runtime tests for this recipe
inherit setuptools3 ptest

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
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-unittest-automake-output \
    ${PYTHON_PN}-pytz \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-core \
"

# Installing the test suite on the target
do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
