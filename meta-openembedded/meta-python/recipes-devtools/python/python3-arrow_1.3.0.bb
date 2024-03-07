SUMMARY = "Better dates and times for Python"
HOMEPAGE = "https://github.com/arrow-py/arrow"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=14a2e29a9d542fb9052d75344d67619d"

SRC_URI[sha256sum] = "d4540617648cb5f895730f1ad8c82a65f2dad0166f57b75f3ca54759c4d67a85"

inherit pypi python_flit_core ptest

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    python3-dateutil-zoneinfo \
    python3-pytest \
    python3-pytest-mock \
    python3-pytz \
    python3-simplejson \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
    python3-compression \
    python3-dateutil \
    python3-dateutil-zoneinfo \
    python3-json \
    python3-types-python-dateutil \
"
