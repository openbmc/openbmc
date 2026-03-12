SUMMARY = "Travel through time in your tests."
HOMEPAGE = "https://github.com/adamchainz/time-machine"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fb9b93a440d3ef2fb6eeebfa59384c53"

SRCREV = "1b7ac45defb26c72076dc202aba46967c8c28fa9"
PYPI_SRC_URI = "git://github.com/adamchainz/time-machine;protocol=https;branch=main;tag=${PV};destsuffix=time_machine-${PV}"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PYPI_PACKAGE = "time_machine"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += "\
    python3-tzdata \
    python3-unittest \
    python3-zoneinfo \
    "
do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += "\
    python3-dateutil \
    python3-freezegun \
    python3-tokenize-rt \
"
